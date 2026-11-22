package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.support.IntegrationTestBase;
import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class TeamWorkflowIntegrationTest extends IntegrationTestBase {

    @Test
    void transferRefreshesPermissionsRosterAndSingleAuditThenRemovalPreservesHistory() throws Exception {
        User oldLeader = createUser("old-leader@test.local");
        User newLeader = createUser("new-leader@test.local");
        User member = createUser("team-member@test.local");
        Engagement engagement = createEngagement(oldLeader);
        addMember(engagement, newLeader, EngagementRole.MEMBER);
        addMember(engagement, member, EngagementRole.MEMBER);
        String uri = "/api/engagements/" + engagement.getId();
        long memberRows = memberRepository.count();
        long auditsBefore = auditLogRepository.count();

        mockMvc.perform(post(uri + "/transfer-leadership").with(asUser(oldLeader)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newLeaderId\":" + newLeader.getId() + "}"))
            .andExpect(status().isNoContent());
        mockMvc.perform(get(uri).with(asUser(oldLeader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("MEMBER"))
            .andExpect(jsonPath("$.leader.id").value(newLeader.getId()))
            .andExpect(jsonPath("$.members.length()").value(3));
        mockMvc.perform(get(uri).with(asUser(newLeader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("LEADER"));

        Engagement saved = engagementRepository.findById(engagement.getId()).orElseThrow();
        assertEquals(newLeader.getId(), saved.getLeader().getId());
        assertEquals(EngagementRole.MEMBER, memberRepository.findByEngagementAndUser(saved, oldLeader).orElseThrow().getRole());
        assertEquals(EngagementRole.LEADER, memberRepository.findByEngagementAndUser(saved, newLeader).orElseThrow().getRole());
        assertEquals(1, memberRepository.findByEngagement(saved).stream()
            .filter(m -> m.getRemovedAt() == null && m.getRole() == EngagementRole.LEADER).count());
        var transferAudit = auditLogRepository.findByEngagementId(String.valueOf(saved.getId()), 200);
        assertEquals(1, transferAudit.size());
        assertEquals("TRANSFER_LEADERSHIP", transferAudit.get(0).getAction());
        assertEquals(oldLeader.getId(), transferAudit.get(0).getActorId());
        assertEquals(oldLeader.getId().longValue(), ((Number) transferAudit.get(0).getMetadata().get("previousLeaderId")).longValue());
        assertEquals(newLeader.getId().longValue(), ((Number) transferAudit.get(0).getMetadata().get("newLeaderId")).longValue());

        mockMvc.perform(put(uri).with(asUser(oldLeader)).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"stale leader change\"}"))
            .andExpect(status().isForbidden());
        mockMvc.perform(get(uri + "/audit").with(asUser(oldLeader)))
            .andExpect(status().isForbidden());
        mockMvc.perform(delete(uri + "/members/" + member.getId()).with(asUser(oldLeader)).with(csrf()))
            .andExpect(status().isForbidden());
        mockMvc.perform(post(uri + "/transfer-leadership").with(asUser(oldLeader)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{\"newLeaderId\":" + member.getId() + "}"))
            .andExpect(status().isForbidden());
        assertEquals(auditsBefore + 1, auditLogRepository.count());
        assertEquals("test description", engagementRepository.findById(saved.getId()).orElseThrow().getDescription());

        mockMvc.perform(put(uri).with(asUser(newLeader)).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"new leader change\"}"))
            .andExpect(status().isOk());
        mockMvc.perform(delete(uri + "/members/" + member.getId()).with(asUser(newLeader)).with(csrf()))
            .andExpect(status().isNoContent());
        mockMvc.perform(get(uri).with(asUser(newLeader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.members.length()").value(2));
        mockMvc.perform(get(uri).with(asUser(member))).andExpect(status().isNotFound());
        assertEquals(memberRows, memberRepository.count());
        assertNotNull(memberRepository.findByEngagementAndUser(saved, member).orElseThrow().getRemovedAt());
        mockMvc.perform(get(uri + "/audit").with(asUser(newLeader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
        assertEquals(1, auditLogRepository.findByEngagementId(String.valueOf(saved.getId()), 200).stream()
            .filter(a -> a.getAction().equals("REMOVE_MEMBER")).count());
    }

    @Test
    void nonmemberTransferAndStaleRemovalLeaveLeaderStateAndAuditIntact() throws Exception {
        User leader = createUser("stable-leader@test.local");
        User member = createUser("removed-member@test.local");
        User outsider = createUser("foreign-member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);
        var log = createLog(engagement, member, io.muzoo.ssc.plogit.domain.ReviewState.DRAFT);
        String uri = "/api/engagements/" + engagement.getId();
        long auditsBefore = auditLogRepository.count();
        long membersBefore = memberRepository.count();

        mockMvc.perform(post(uri + "/transfer-leadership").with(asUser(leader)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newLeaderId\":" + outsider.getId() + "}"))
            .andExpect(status().isNotFound());
        mockMvc.perform(delete(uri + "/members/" + leader.getId()).with(asUser(leader)).with(csrf()))
            .andExpect(status().isBadRequest());
        mockMvc.perform(delete(uri + "/members/" + member.getId()).with(asUser(member)).with(csrf()))
            .andExpect(status().isForbidden());
        mockMvc.perform(delete(uri + "/members/" + member.getId()).with(asUser(leader)))
            .andExpect(status().isForbidden());
        assertEquals(auditsBefore, auditLogRepository.count());

        mockMvc.perform(delete(uri + "/members/" + member.getId()).with(asUser(leader)).with(csrf()))
            .andExpect(status().isNoContent());
        var removedAt = memberRepository.findByEngagementAndUser(engagement, member).orElseThrow().getRemovedAt();
        assertNotNull(removedAt);
        mockMvc.perform(delete(uri + "/members/" + member.getId()).with(asUser(leader)).with(csrf()))
            .andExpect(status().isNotFound());
        mockMvc.perform(post(uri + "/transfer-leadership").with(asUser(leader)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newLeaderId\":" + member.getId() + "}"))
            .andExpect(status().isNotFound());
        Engagement saved = engagementRepository.findById(engagement.getId()).orElseThrow();
        assertEquals(leader.getId(), saved.getLeader().getId());
        assertEquals(EngagementRole.LEADER, memberRepository.findByEngagementAndUser(saved, leader).orElseThrow().getRole());
        assertEquals(EngagementRole.MEMBER, memberRepository.findByEngagementAndUser(saved, member).orElseThrow().getRole());
        assertEquals(removedAt, memberRepository.findByEngagementAndUser(saved, member).orElseThrow().getRemovedAt());
        assertEquals(1, memberRepository.findByEngagement(saved).stream()
            .filter(m -> m.getRemovedAt() == null && m.getRole() == EngagementRole.LEADER).count());
        assertEquals(membersBefore, memberRepository.count());
        assertEquals(auditsBefore + 1, auditLogRepository.count());
        assertEquals(member.getId(), logRepository.findById(log.getId()).orElseThrow().getAuthor().getId());
        assertEquals(io.muzoo.ssc.plogit.domain.ReviewState.DRAFT,
            logRepository.findById(log.getId()).orElseThrow().getReviewState());
    }
}

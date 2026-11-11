package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementMember;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.support.IntegrationTestBase;
import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import io.muzoo.ssc.plogit.service.MembershipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class EngagementAuthzIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MembershipService membershipService;

    @Test
    void activeMemberCanReadAndLeaderCanUpdateSameEngagement() throws Exception {
        User leader = createUser("scoped-leader@test.local");
        User member = createUser("scoped-member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);

        mockMvc.perform(get("/api/engagements/" + engagement.getId()).with(asUser(member)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("MEMBER"))
            .andExpect(jsonPath("$.id").value(engagement.getId()));

        mockMvc.perform(put("/api/engagements/" + engagement.getId())
                .with(asUser(leader)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Updated under leader approval\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("LEADER"))
            .andExpect(jsonPath("$.description").value("Updated under leader approval"));

        assertEquals("Updated under leader approval",
            engagementRepository.findById(engagement.getId()).orElseThrow().getDescription());
    }

    @Test
    void crossEngagementWritesAndRemovedMembershipAreDeniedWithoutMutation() throws Exception {
        User firstLeader = createUser("first-leader@test.local");
        User secondLeader = createUser("second-leader@test.local");
        User member = createUser("boundary-member@test.local");
        Engagement first = createEngagement(firstLeader);
        Engagement second = createEngagement(secondLeader);
        EngagementMember membership = addMember(first, member, EngagementRole.MEMBER);
        long auditsBefore = auditLogRepository.count();
        String edit = "{\"description\":\"Unauthorized change\"}";

        mockMvc.perform(get("/api/engagements/" + second.getId()).with(asUser(firstLeader)))
            .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/engagements/" + second.getId())
                .with(asUser(firstLeader)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(edit))
            .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/engagements/" + first.getId())
                .with(asUser(member)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(edit))
            .andExpect(status().isForbidden());

        membership.setRemovedAt(Instant.now());
        memberRepository.save(membership);
        mockMvc.perform(get("/api/engagements/" + first.getId()).with(asUser(member)))
            .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/engagements/" + first.getId())
                .with(asUser(member)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(edit))
            .andExpect(status().isNotFound());

        assertEquals("test description", engagementRepository.findById(first.getId()).orElseThrow().getDescription());
        assertEquals("test description", engagementRepository.findById(second.getId()).orElseThrow().getDescription());
        assertEquals(auditsBefore, auditLogRepository.count());
    }

    @Test
    void memberCanViewEngagement() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);

        mockMvc.perform(get("/api/engagements/" + engagement.getId()).with(asUser(member)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(engagement.getId()))
            .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    void nonMemberGets404Not403() throws Exception {
        User leader = createUser("leader@test.local");
        User outsider = createUser("outsider@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(get("/api/engagements/" + engagement.getId()).with(asUser(outsider)))
            .andExpect(status().isNotFound());
    }

    @Test
    void removedMemberCannotViewEngagement() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        EngagementMember membership = addMember(engagement, member, EngagementRole.MEMBER);
        membership.setRemovedAt(Instant.now());
        memberRepository.save(membership);

        mockMvc.perform(get("/api/engagements/" + engagement.getId()).with(asUser(member)))
            .andExpect(status().isNotFound());
    }

    @Test
    void removedMemberCannotAccessLogsOrTransitions() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        EngagementMember membership = addMember(engagement, member, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);
        membership.setRemovedAt(Instant.now());
        memberRepository.save(membership);

        mockMvc.perform(get("/api/engagements/" + engagement.getId() + "/logs").with(asUser(member)))
            .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/logs/" + log.getId() + "/transition")
                .with(asUser(member))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"SUBMIT"}
                    """))
            .andExpect(status().isNotFound());
    }

    @Test
    void removedMemberCanRejoinWithoutDuplicateMembership() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        EngagementMember membership = addMember(engagement, member, EngagementRole.MEMBER);
        membership.setRemovedAt(Instant.now());
        memberRepository.save(membership);

        String code = mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/join-code")
                .with(asUser(leader))
                .with(csrf()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString()
            .replaceAll(".*\"code\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(post("/api/join")
                .with(asUser(member))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"" + code + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(engagement.getId()))
            .andExpect(jsonPath("$.role").value("MEMBER"));

        EngagementMember rejoined = memberRepository.findByEngagementAndUser(engagement, member).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(membership.getId(), rejoined.getId());
        org.junit.jupiter.api.Assertions.assertNull(rejoined.getRemovedAt());
        org.junit.jupiter.api.Assertions.assertEquals("code", rejoined.getJoinedVia());
        org.junit.jupiter.api.Assertions.assertEquals(2, memberRepository.findByEngagement(engagement).size());
    }

    @Test
    void activeMemberCanStillAccessLogs() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);

        mockMvc.perform(get("/api/engagements/" + engagement.getId() + "/logs").with(asUser(member)))
            .andExpect(status().isOk());
    }

    @Test
    void unauthenticatedGetsForbidden() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(get("/api/engagements/" + engagement.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    void leaderCanUpdateEngagement() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/transfer-leadership")
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"newLeaderId":999}
                    """))
            .andExpect(status().isNotFound());
    }

    @Test
    void leadershipTransferUpdatesLeaderAndMemberRoles() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);

        mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/transfer-leadership")
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newLeaderId\":" + member.getId() + "}"))
            .andExpect(status().isNoContent());

        Engagement updated = engagementRepository.findById(engagement.getId()).orElseThrow();
        assertEquals(member.getId(), updated.getLeader().getId());
        assertEquals(EngagementRole.MEMBER, memberRepository.findByEngagementAndUser(updated, leader).orElseThrow().getRole());
        assertEquals(EngagementRole.LEADER, memberRepository.findByEngagementAndUser(updated, member).orElseThrow().getRole());
    }

    @Test
    void failedLeadershipTransferRollsBackMemberRoles() {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);

        Engagement staleEngagement = engagementRepository.findById(engagement.getId()).orElseThrow();
        Engagement updated = engagementRepository.findById(engagement.getId()).orElseThrow();
        updated.setDescription("Updated after the transfer started");
        engagementRepository.saveAndFlush(updated);

        assertThrows(OptimisticLockingFailureException.class, () ->
            membershipService.transferLeadership(staleEngagement, leader, member.getId()));

        Engagement persisted = engagementRepository.findById(engagement.getId()).orElseThrow();
        assertEquals(leader.getId(), persisted.getLeader().getId());
        assertEquals(EngagementRole.LEADER, memberRepository.findByEngagementAndUser(persisted, leader).orElseThrow().getRole());
        assertEquals(EngagementRole.MEMBER, memberRepository.findByEngagementAndUser(persisted, member).orElseThrow().getRole());
    }

    @Test
    void leadershipTransferRejectsMissingOrNonPositiveTargetId() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);

        for (String payload : new String[]{"{}", "{\"newLeaderId\":null}", "{\"newLeaderId\":0}", "{\"newLeaderId\":-1}"}) {
            mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/transfer-leadership")
                    .with(asUser(leader))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("newLeaderId"));
        }
        assertEquals(leader.getId(),
            engagementRepository.findById(engagement.getId()).orElseThrow().getLeader().getId());
    }

    @Test
    void leadershipTransferRejectsMalformedTargetId() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/transfer-leadership")
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newLeaderId\":\"invalid\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void memberCannotTransferLeadership() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);

        mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/transfer-leadership")
                .with(asUser(member))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"newLeaderId":999}
                    """))
            .andExpect(status().isForbidden());
    }
}

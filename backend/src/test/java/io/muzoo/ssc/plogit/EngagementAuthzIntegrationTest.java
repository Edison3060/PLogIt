package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementMember;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.support.IntegrationTestBase;
import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.time.Instant;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class EngagementAuthzIntegrationTest extends IntegrationTestBase {

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
            .andExpect(status().isNotFound());
    }
}

package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementMember;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class JoinCodeWorkflowIntegrationTest extends IntegrationTestBase {

    @Test
    void generatedCodeCreatesExactlyOneMemberWithCorrectRole() throws Exception {
        User leader = createUser("join-flow-leader@test.local");
        User joiner = createUser("join-flow-member@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/join-code")
                .with(asUser(leader)).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").isString())
            .andExpect(jsonPath("$.engagementId").value(engagement.getId()));

        String code = engagementRepository.findById(engagement.getId()).orElseThrow().getCurrentJoinCode();
        assertNotNull(code);

        mockMvc.perform(post("/api/join")
                .with(asUser(joiner)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"" + code + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(engagement.getId()))
            .andExpect(jsonPath("$.role").value("MEMBER"));

        EngagementMember joined = memberRepository.findByEngagementAndUser(engagement, joiner).orElseThrow();
        assertEquals(EngagementRole.MEMBER, joined.getRole());
        assertEquals("code", joined.getJoinedVia());
        assertEquals(2, memberRepository.findByEngagement(engagement).size());

        mockMvc.perform(get("/api/engagements").with(asUser(joiner)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].role").value("MEMBER"));
        mockMvc.perform(get("/api/engagements/" + engagement.getId()).with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.members.length()").value(2));
    }

    @Test
    void revokedUnknownAndReplayedCodesCannotAddMembershipOrAudit() throws Exception {
        User leader = createUser("join-denial-leader@test.local");
        User existingMember = createUser("join-denial-member@test.local");
        User joiner = createUser("join-denial-joiner@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, existingMember, EngagementRole.MEMBER);

        mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/join-code")
                .with(asUser(existingMember)).with(csrf()))
            .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/join-code")
                .with(asUser(joiner)).with(csrf()))
            .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/join-code")
                .with(asUser(leader)).with(csrf()))
            .andExpect(status().isOk());
        String oldCode = engagementRepository.findById(engagement.getId()).orElseThrow().getCurrentJoinCode();

        mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/join-code")
                .with(asUser(leader)).with(csrf()))
            .andExpect(status().isOk());
        String newCode = engagementRepository.findById(engagement.getId()).orElseThrow().getCurrentJoinCode();
        assertNotNull(joinCodeRepository.findByCode(oldCode).orElseThrow().getRevokedAt());

        long auditsBefore = auditLogRepository.count();

        mockMvc.perform(post("/api/join")
                .with(asUser(joiner)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"" + oldCode + "\"}"))
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/join")
                .with(asUser(joiner)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code":"NOT-A-REAL-CODE"}
                    """))
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/join")
                .with(asUser(joiner))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"" + newCode + "\"}"))
            .andExpect(status().isForbidden());

        assertEquals(2, memberRepository.findByEngagement(engagement).size());
        assertEquals(auditsBefore, auditLogRepository.count());

        mockMvc.perform(post("/api/join")
                .with(asUser(joiner)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"" + newCode + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("MEMBER"));

        long auditAfterJoin = auditLogRepository.count();
        mockMvc.perform(post("/api/join")
                .with(asUser(joiner)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"" + newCode + "\"}"))
            .andExpect(status().isConflict());

        assertEquals(3, memberRepository.findByEngagement(engagement).size());
        assertEquals(EngagementRole.MEMBER,
            memberRepository.findByEngagementAndUser(engagement, joiner).orElseThrow().getRole());
        assertEquals(auditAfterJoin, auditLogRepository.count());
        assertEquals(auditsBefore + 1, auditAfterJoin);
    }
}

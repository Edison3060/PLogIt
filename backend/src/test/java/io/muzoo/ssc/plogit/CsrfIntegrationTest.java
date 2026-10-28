package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.Engagement;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class CsrfIntegrationTest extends IntegrationTestBase {

    @Test
    void missingCsrfTokenRejectsLogEditsAndTransitions() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(put(logUri(log.getId()))
                .with(asUser(leader))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"changed without CSRF"}
                    """))
            .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/logs/" + log.getId() + "/transition")
                .with(asUser(leader))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"SUBMIT"}
                    """))
            .andExpect(status().isForbidden());

        LogEntry unchanged = logRepository.findById(log.getId()).orElseThrow();
        assertEquals("Test log", unchanged.getTitle());
        assertEquals(ReviewState.DRAFT, unchanged.getReviewState());
        assertEquals(0, versionRepository.findByLogOrderByVersionNumberAsc(unchanged).size());
    }

    @Test
    void invalidCsrfTokenRejectsAuthenticatedRequests() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(put(logUri(log.getId()))
                .with(asUser(leader))
                .with(csrf().useInvalidToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"changed with invalid CSRF"}
                    """))
            .andExpect(status().isForbidden());

        assertEquals("Test log", logRepository.findById(log.getId()).orElseThrow().getTitle());
    }

    @Test
    void missingCsrfTokenRejectsEngagementMutations() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);
        String uri = "/api/engagements/" + engagement.getId();

        mockMvc.perform(put(uri)
                .with(asUser(leader))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"description":"changed without CSRF"}
                    """))
            .andExpect(status().isForbidden());

        mockMvc.perform(post(uri + "/export")
                .with(asUser(leader))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"format":"CSV"}
                    """))
            .andExpect(status().isForbidden());

        mockMvc.perform(delete(uri + "/members/" + member.getId())
                .with(asUser(leader)))
            .andExpect(status().isForbidden());

        mockMvc.perform(post(uri + "/transfer-leadership")
                .with(asUser(leader))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newLeaderId\":" + member.getId() + "}"))
            .andExpect(status().isForbidden());

        mockMvc.perform(post(uri + "/join-code")
                .with(asUser(leader)))
            .andExpect(status().isForbidden());

        Engagement unchanged = engagementRepository.findById(engagement.getId()).orElseThrow();
        assertEquals("test description", unchanged.getDescription());
        assertEquals(leader.getId(), unchanged.getLeader().getId());
        assertNull(unchanged.getCurrentJoinCode());
        assertEquals(EngagementRole.MEMBER,
            memberRepository.findByEngagementAndUser(unchanged, member).orElseThrow().getRole());
        assertEquals(0, auditLogRepository.findByEngagementId(String.valueOf(engagement.getId()), 200).size());
    }
}

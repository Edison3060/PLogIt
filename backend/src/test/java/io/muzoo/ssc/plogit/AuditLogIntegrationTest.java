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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class AuditLogIntegrationTest extends IntegrationTestBase {

    @Test
    void submitCreatesAuditEntry() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.DRAFT);

        mockMvc.perform(post("/api/logs/" + log.getId() + "/transition")
                .with(asUser(author))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"SUBMIT"}
                    """))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/engagements/" + engagement.getId() + "/audit").with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].action").value("SUBMIT"))
            .andExpect(jsonPath("$[0].targetType").value("LOG"))
            .andExpect(jsonPath("$[0].metadata.fromState").value("DRAFT"))
            .andExpect(jsonPath("$[0].metadata.toState").value("SUBMITTED"));
    }

    @Test
    void approveCreatesAuditEntry() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.SUBMITTED);

        mockMvc.perform(post("/api/logs/" + log.getId() + "/transition")
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"APPROVE"}
                    """))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/engagements/" + engagement.getId() + "/audit").with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].action").value("APPROVE"))
            .andExpect(jsonPath("$[0].metadata.fromState").value("SUBMITTED"))
            .andExpect(jsonPath("$[0].metadata.toState").value("APPROVED"));
    }

    @Test
    void rejectCreatesAuditEntryWithComment() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.SUBMITTED);

        mockMvc.perform(post("/api/logs/" + log.getId() + "/transition")
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"REJECT","comment":"fix the title"}
                    """))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/engagements/" + engagement.getId() + "/audit").with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].action").value("REJECT"))
            .andExpect(jsonPath("$[0].metadata.fromState").value("SUBMITTED"))
            .andExpect(jsonPath("$[0].metadata.toState").value("DRAFT"))
            .andExpect(jsonPath("$[0].metadata.comment").value("fix the title"));
    }

    @Test
    void multipleTransitionsProduceOrderedAuditTrail() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.DRAFT);

        mockMvc.perform(post("/api/logs/" + log.getId() + "/transition")
                .with(asUser(author)).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"SUBMIT"}
                    """))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/logs/" + log.getId() + "/transition")
                .with(asUser(leader)).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"REJECT","comment":"no"}
                    """))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/logs/" + log.getId() + "/transition")
                .with(asUser(author)).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"SUBMIT"}
                    """))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/engagements/" + engagement.getId() + "/audit").with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void memberCannotViewAuditLog() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);

        mockMvc.perform(get("/api/engagements/" + engagement.getId() + "/audit").with(asUser(member)))
            .andExpect(status().isNotFound());
    }

    @Test
    void auditEntriesScopedToEngagement() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement eng1 = createEngagement(leader);
        Engagement eng2 = createEngagement(leader);
        LogEntry log1 = createLog(eng1, leader, ReviewState.DRAFT);
        LogEntry log2 = createLog(eng2, leader, ReviewState.DRAFT);

        mockMvc.perform(post("/api/logs/" + log1.getId() + "/transition")
                .with(asUser(leader)).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"SUBMIT"}
                    """))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/engagements/" + eng1.getId() + "/audit").with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/api/engagements/" + eng2.getId() + "/audit").with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}

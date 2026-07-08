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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class LogVersioningIntegrationTest extends IntegrationTestBase {

    private static final String CREATE_BODY = """
        {
          "activityType": "RECONNAISSANCE",
          "title": "Test log",
          "description": "desc",
          "result": "result",
          "target": "10.0.0.1",
          "outcome": "IN_PROGRESS"
        }
        """;

    private String createLogViaApi(Engagement engagement, User author) throws Exception {
        String response = mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/logs")
                .with(asUser(author))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(CREATE_BODY))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        return response.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");
    }

    @Test
    void createProducesVersion1() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        String logId = createLogViaApi(engagement, leader);

        mockMvc.perform(get("/api/logs/" + logId + "/history").with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].versionNumber").value(1))
            .andExpect(jsonPath("$[0].snapshot.title").value("Test log"));
    }

    @Test
    void editProducesVersion2() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        String logId = createLogViaApi(engagement, leader);

        mockMvc.perform(put("/api/logs/" + logId)
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"edited title"}
                    """))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/logs/" + logId + "/history").with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].versionNumber").value(1))
            .andExpect(jsonPath("$[0].snapshot.title").value("Test log"))
            .andExpect(jsonPath("$[1].versionNumber").value(2))
            .andExpect(jsonPath("$[1].snapshot.title").value("edited title"));
    }

    @Test
    void multipleEditsProduceIncrementingVersions() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        String logId = createLogViaApi(engagement, leader);

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(put("/api/logs/" + logId)
                    .with(asUser(leader))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"title\":\"edit " + i + "\"}"))
                .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/logs/" + logId + "/history").with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(4))
            .andExpect(jsonPath("$[3].versionNumber").value(4))
            .andExpect(jsonPath("$[3].snapshot.title").value("edit 2"));
    }

    @Test
    void memberCanViewHistory() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);
        String logId = createLogViaApi(engagement, leader);

        mockMvc.perform(get("/api/logs/" + logId + "/history").with(asUser(member)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void nonMemberCannotViewHistory() throws Exception {
        User leader = createUser("leader@test.local");
        User outsider = createUser("outsider@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(get("/api/logs/" + log.getId() + "/history").with(asUser(outsider)))
            .andExpect(status().isNotFound());
    }
}

package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.Engagement;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class BadRequestIntegrationTest extends IntegrationTestBase {

    @Test
    void malformedJsonReturnsBadRequestWithoutChangingLog() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(put(logUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Malformed request body"));

        assertEquals("Test log", logRepository.findById(log.getId()).orElseThrow().getTitle());
    }

    @Test
    void invalidTransitionEnumReturnsBadRequestWithoutChangingState() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(post("/api/logs/" + log.getId() + "/transition")
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"INVALID_ACTION"}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Malformed request body"));

        assertEquals(ReviewState.DRAFT, logRepository.findById(log.getId()).orElseThrow().getReviewState());
    }

    @Test
    void invalidExportEnumReturnsBadRequest() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/export")
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"format":"INVALID_FORMAT"}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void invalidQueryEnumReturnsBadRequest() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(get("/api/engagements/" + engagement.getId() + "/logs")
                .with(asUser(leader))
                .param("activityType", "INVALID_TYPE"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Invalid value for parameter: activityType"));
    }

    @Test
    void malformedUuidReturnsBadRequest() throws Exception {
        User leader = createUser("leader@test.local");

        mockMvc.perform(get("/api/logs/not-a-uuid")
                .with(asUser(leader)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Invalid value for parameter: logId"));
    }

    @Test
    void malformedNumericPathReturnsBadRequest() throws Exception {
        User leader = createUser("leader@test.local");

        mockMvc.perform(get("/api/engagements/not-a-number")
                .with(asUser(leader)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Invalid value for parameter: id"));
    }

    @Test
    void invalidJsonFieldTypeReturnsBadRequest() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(post("/api/logs/" + log.getId() + "/transition")
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":{"value":"SUBMIT"}}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Malformed request body"));

        assertEquals(ReviewState.DRAFT, logRepository.findById(log.getId()).orElseThrow().getReviewState());
    }

    @Test
    void missingRequiredEnumStillReturnsValidationErrors() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(post("/api/logs/" + log.getId() + "/transition")
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":null}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("action"));
    }
}

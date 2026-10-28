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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class LogAuthzIntegrationTest extends IntegrationTestBase {

    @Test
    void memberCanViewLog() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(get(logUri(log.getId())).with(asUser(leader)))
            .andExpect(status().isOk());
    }

    @Test
    void nonMemberCannotViewLog() throws Exception {
        User leader = createUser("leader@test.local");
        User outsider = createUser("outsider@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(get(logUri(log.getId())).with(asUser(outsider)))
            .andExpect(status().isNotFound());
    }

    @Test
    void authorCanEditOwnDraft() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.DRAFT);

        mockMvc.perform(put(logUri(log.getId()))
                .with(asUser(author))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"updated title"}
                    """))
            .andExpect(status().isOk());
    }

    @Test
    void authorCannotEditSubmittedLog() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.SUBMITTED);

        mockMvc.perform(put(logUri(log.getId()))
                .with(asUser(author))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"updated title"}
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void leaderCannotEditApprovedLog() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.APPROVED);

        mockMvc.perform(put(logUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"updated title"}
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void leaderCannotEditExportedLog() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.EXPORTED);

        mockMvc.perform(put(logUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"updated title"}
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void nonAuthorCannotEditOthersDraft() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        User other = createUser("other@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        addMember(engagement, other, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.DRAFT);

        mockMvc.perform(put(logUri(log.getId()))
                .with(asUser(other))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"hijacked"}
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void leaderCanEditAnyDraft() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.DRAFT);

        mockMvc.perform(put(logUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"leader edit"}
                    """))
            .andExpect(status().isOk());
    }

    @Test
    void unauthenticatedCannotEditLog() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(put(logUri(log.getId()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"x"}
                    """))
            .andExpect(status().isForbidden());
    }
    @Test
    void authorCannotEditApprovedOrExportedLog() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);

        for (ReviewState state : new ReviewState[]{ReviewState.APPROVED, ReviewState.EXPORTED}) {
            LogEntry log = createLog(engagement, author, state);

            mockMvc.perform(put(logUri(log.getId()))
                    .with(asUser(author))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"title":"unauthorized change"}
                        """))
                .andExpect(status().isConflict());

            LogEntry unchanged = logRepository.findById(log.getId()).orElseThrow();
            assertEquals("Test log", unchanged.getTitle());
            assertEquals(state, unchanged.getReviewState());
        }
    }

    @Test
    void leaderCannotEditSubmittedLog() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.SUBMITTED);

        mockMvc.perform(put(logUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"unauthorized change"}
                    """))
            .andExpect(status().isConflict());

        LogEntry unchanged = logRepository.findById(log.getId()).orElseThrow();
        assertEquals("Test log", unchanged.getTitle());
        assertEquals(ReviewState.SUBMITTED, unchanged.getReviewState());
    }

}

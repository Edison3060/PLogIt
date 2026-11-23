package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.AuditLog;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class ReviewTransitionIntegrationTest extends IntegrationTestBase {

    private String transitionUri(UUID id) {
        return "/api/logs/" + id + "/transition";
    }

    @Test
    void authorCanSubmitOwnDraft() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.DRAFT);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(author))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"SUBMIT"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reviewState").value("SUBMITTED"))
            .andExpect(jsonPath("$.rejectionComment").doesNotExist());
    }

    @Test
    void authorSubmissionAndLeaderApprovalCommitStateAndOneAuditEach() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry draft = createLog(engagement, author, ReviewState.DRAFT);
        UUID id = draft.getId();

        mockMvc.perform(post(transitionUri(id)).with(asUser(author)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"SUBMIT"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reviewState").value("SUBMITTED"));
        assertEquals(ReviewState.SUBMITTED, logRepository.findById(id).orElseThrow().getReviewState());

        mockMvc.perform(post(transitionUri(id)).with(asUser(leader)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"APPROVE"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reviewState").value("APPROVED"));

        mockMvc.perform(get(logUri(id)).with(asUser(author)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.authorId").value(author.getId().intValue()))
            .andExpect(jsonPath("$.title").value("Test log"))
            .andExpect(jsonPath("$.reviewState").value("APPROVED"));
        assertEquals(ReviewState.APPROVED, logRepository.findById(id).orElseThrow().getReviewState());

        List<AuditLog> audits = auditLogRepository.findByEngagementId(engagement.getId().toString(), 10);
        assertEquals(2, audits.size());
        AuditLog submission = audits.stream().filter(audit -> "SUBMIT".equals(audit.getAction()))
            .findFirst().orElseThrow();
        AuditLog approval = audits.stream().filter(audit -> "APPROVE".equals(audit.getAction()))
            .findFirst().orElseThrow();
        assertEquals(author.getId(), submission.getActorId());
        assertEquals(id.toString(), submission.getTargetId());
        assertEquals("DRAFT", submission.getMetadata().get("fromState"));
        assertEquals("SUBMITTED", submission.getMetadata().get("toState"));
        assertEquals(leader.getId(), approval.getActorId());
        assertEquals(id.toString(), approval.getTargetId());
        assertEquals("SUBMITTED", approval.getMetadata().get("fromState"));
        assertEquals("APPROVED", approval.getMetadata().get("toState"));
    }

    @Test
    void leaderCanSubmitAnyDraft() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.DRAFT);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"SUBMIT"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reviewState").value("SUBMITTED"));
    }

    @Test
    void nonAuthorMemberCannotSubmitOthersDraft() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        User other = createUser("other@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        addMember(engagement, other, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.DRAFT);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(other))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"SUBMIT"}
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void leaderCanApproveSubmitted() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.SUBMITTED);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"APPROVE"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reviewState").value("APPROVED"));
    }

    @Test
    void memberCannotApproveSubmitted() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.SUBMITTED);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(author))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"APPROVE"}
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    void leaderCanRejectSubmittedWithComment() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.SUBMITTED);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"REJECT","comment":"needs more detail"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reviewState").value("DRAFT"))
            .andExpect(jsonPath("$.rejectionComment").value("needs more detail"));
    }

    @Test
    void leaderCannotRejectWithoutComment() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.SUBMITTED);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"REJECT"}
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void cannotApproveFromDraft() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.DRAFT);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"APPROVE"}
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void cannotRejectFromDraft() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"REJECT","comment":"x"}
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void cannotSubmitFromSubmitted() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.SUBMITTED);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"SUBMIT"}
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void cannotTransitionFromApproved() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.APPROVED);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"REJECT","comment":"x"}
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void cannotTransitionFromExported() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.EXPORTED);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"APPROVE"}
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void nonMemberCannotTransition() throws Exception {
        User leader = createUser("leader@test.local");
        User outsider = createUser("outsider@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(outsider))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"SUBMIT"}
                    """))
            .andExpect(status().isNotFound());
    }

    @Test
    void unauthenticatedCannotTransition() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"SUBMIT"}
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    void leaderCanExportApproved() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.APPROVED);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"EXPORT"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reviewState").value("EXPORTED"));
    }

    @Test
    void memberCannotExportApproved() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.APPROVED);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(author))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"EXPORT"}
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    void cannotExportFromSubmitted() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.SUBMITTED);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"EXPORT"}
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void leaderCanReExportExported() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.EXPORTED);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"EXPORT"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reviewState").value("EXPORTED"));
    }

    @Test
    void resubmitClearsRejectionComment() throws Exception {
        User leader = createUser("leader@test.local");
        User author = createUser("author@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, author, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, author, ReviewState.SUBMITTED);

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"REJECT","comment":"fix it"}
                    """))
            .andExpect(status().isOk());

        LogEntry rejected = logRepository.findById(log.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(ReviewState.DRAFT, rejected.getReviewState());

        mockMvc.perform(post(transitionUri(log.getId()))
                .with(asUser(author))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"action":"SUBMIT"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reviewState").value("SUBMITTED"))
            .andExpect(jsonPath("$.rejectionComment").doesNotExist());
    }
}

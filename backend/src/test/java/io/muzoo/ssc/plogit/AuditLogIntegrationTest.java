package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.AuditLog;
import io.muzoo.ssc.plogit.domain.Engagement;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class AuditLogIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MembershipService membershipService;

    private List<AuditLog> auditFor(Engagement engagement) {
        return auditLogRepository.findByEngagementId(String.valueOf(engagement.getId()), 200);
    }

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
    @Test
    void reExportCreatesLogAndEngagementAuditEntries() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.EXPORTED);

        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/export")
                    .with(asUser(leader))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"format":"CSV","includeExported":true}
                        """))
                .andExpect(status().isOk());
        }

        List<AuditLog> entries = auditFor(engagement);
        assertEquals(4, entries.size());
        assertEquals(2, entries.stream().filter(e -> e.getAction().equals("EXPORT_ENGAGEMENT")).count());
        List<AuditLog> logExports = entries.stream().filter(e -> e.getAction().equals("EXPORT")).toList();
        assertEquals(2, logExports.size());
        for (AuditLog entry : logExports) {
            assertEquals(log.getId().toString(), entry.getTargetId());
            assertEquals("EXPORTED", entry.getMetadata().get("fromState"));
            assertEquals("EXPORTED", entry.getMetadata().get("toState"));
        }
        for (AuditLog entry : entries.stream().filter(e -> e.getAction().equals("EXPORT_ENGAGEMENT")).toList()) {
            assertEquals("CSV", entry.getMetadata().get("format"));
            assertEquals(1, entry.getMetadata().get("logCount"));
            assertEquals(true, entry.getMetadata().get("includeExported"));
        }
    }

    @Test
    void emptyExportStillCreatesAuditEntry() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/export")
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"format":"CSV"}
                    """))
            .andExpect(status().isOk());

        List<AuditLog> entries = auditFor(engagement);
        assertEquals(1, entries.size());
        assertEquals("EXPORT_ENGAGEMENT", entries.get(0).getAction());
        assertEquals(0, entries.get(0).getMetadata().get("logCount"));
    }

    @Test
    void memberRemovalCreatesAuditEntry() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);

        mockMvc.perform(delete("/api/engagements/" + engagement.getId() + "/members/" + member.getId())
                .with(asUser(leader))
                .with(csrf()))
            .andExpect(status().isNoContent());

        List<AuditLog> entries = auditFor(engagement);
        assertEquals(1, entries.size());
        assertEquals("REMOVE_MEMBER", entries.get(0).getAction());
        assertEquals("MEMBER", entries.get(0).getTargetType());
        assertEquals(member.getId().toString(), entries.get(0).getTargetId());
        assertEquals(leader.getId(), entries.get(0).getActorId());
    }

    @Test
    void leadershipTransferCreatesAuditEntry() throws Exception {
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

        List<AuditLog> entries = auditFor(engagement);
        assertEquals(1, entries.size());
        assertEquals("TRANSFER_LEADERSHIP", entries.get(0).getAction());
        assertEquals(leader.getId(), entries.get(0).getMetadata().get("previousLeaderId"));
        assertEquals(member.getId(), entries.get(0).getMetadata().get("newLeaderId"));

        mockMvc.perform(get("/api/engagements/" + engagement.getId() + "/audit")
                .with(asUser(member)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].action").value("TRANSFER_LEADERSHIP"));
    }

    @Test
    void generatingAndRegeneratingJoinCodeCreatesAuditEntries() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);

        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/join-code")
                    .with(asUser(leader))
                    .with(csrf()))
                .andExpect(status().isOk());
        }

        List<AuditLog> entries = auditFor(engagement);
        assertEquals(2, entries.size());
        assertTrue(entries.stream().anyMatch(e -> e.getAction().equals("GENERATE_JOIN_CODE")
            && Boolean.FALSE.equals(e.getMetadata().get("regenerated"))));
        assertTrue(entries.stream().anyMatch(e -> e.getAction().equals("REGENERATE_JOIN_CODE")
            && Boolean.TRUE.equals(e.getMetadata().get("regenerated"))));
        assertFalse(entries.stream().anyMatch(e -> e.getMetadata().containsKey("code")));
    }

    @Test
    void joiningEngagementCreatesAuditEntry() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);

        String code = mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/join-code")
                .with(asUser(leader)).with(csrf()))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString()
            .replaceAll(".*\"code\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(post("/api/join")
                .with(asUser(member))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"" + code + "\"}"))
            .andExpect(status().isOk());

        AuditLog entry = auditFor(engagement).stream()
            .filter(e -> e.getAction().equals("JOIN_ENGAGEMENT"))
            .findFirst().orElseThrow();
        assertEquals(member.getId(), entry.getActorId());
        assertEquals(member.getId().toString(), entry.getTargetId());
        assertEquals(false, entry.getMetadata().get("rejoined"));
    }

    @Test
    void scopeChangesAreAuditedWithoutStoringSensitiveValues() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(put("/api/engagements/" + engagement.getId())
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"allowedHours":"09:00-17:00","inScopeTargets":["10.0.0.1"]}
                    """))
            .andExpect(status().isOk());

        List<AuditLog> entries = auditFor(engagement);
        assertEquals(1, entries.size());
        assertEquals("UPDATE_ENGAGEMENT", entries.get(0).getAction());
        List<?> fields = (List<?>) entries.get(0).getMetadata().get("changedFields");
        assertTrue(fields.contains("allowedHours"));
        assertTrue(fields.contains("inScopeTargets"));
        assertFalse(entries.get(0).getMetadata().toString().contains("10.0.0.1"));
    }

    @Test
    void failedLeadershipTransferDoesNotCreateAuditEntry() {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);

        Engagement stale = engagementRepository.findById(engagement.getId()).orElseThrow();
        Engagement updated = engagementRepository.findById(engagement.getId()).orElseThrow();
        updated.setDescription("Newer description");
        engagementRepository.saveAndFlush(updated);

        assertThrows(OptimisticLockingFailureException.class, () ->
            membershipService.transferLeadership(stale, leader, member.getId()));

        assertTrue(auditFor(engagement).isEmpty());
    }

}

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class ExportIntegrationTest extends IntegrationTestBase {

    @Autowired
    private JsonMapper jsonMapper;

    private String exportUri(Long id) {
        return "/api/engagements/" + id + "/export";
    }

    @Test
    void leaderCanExportJsonAndMarksLogsExported() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.APPROVED);

        mockMvc.perform(post(exportUri(engagement.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"format":"JSON"}
                    """))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(header().exists("Content-Disposition"));

        LogEntry reloaded = logRepository.findById(log.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(ReviewState.EXPORTED, reloaded.getReviewState());
    }

    @Test
    void jsonExportSelectsExactLogsAndCommitsAudit() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry draft = createLog(engagement, leader, ReviewState.DRAFT);
        LogEntry submitted = createLog(engagement, leader, ReviewState.SUBMITTED);
        LogEntry approved = createLog(engagement, leader, ReviewState.APPROVED);
        LogEntry exported = createLog(engagement, leader, ReviewState.EXPORTED);
        Engagement otherEngagement = createEngagement(leader);
        LogEntry unrelated = createLog(otherEngagement, leader, ReviewState.APPROVED);

        JsonNode first = jsonResponse(exportJson(engagement, leader, "{\"format\":\"JSON\"}"));
        assertEquals(4, first.path("totalLogCount").asInt());
        assertEquals(List.of(approved.getId().toString()), exportedIds(first));
        assertEquals("APPROVED", first.path("logs").get(0).path("reviewState").asText());
        assertEquals(ReviewState.EXPORTED, persistedState(approved));
        assertExportAudit(engagement, leader, List.of(approved), false, 0);
        AuditLog firstTransition = auditLogRepository.findAll().stream()
            .filter(event -> event.getAction().equals("EXPORT")).findFirst().orElseThrow();
        assertEquals("APPROVED", firstTransition.getMetadata().get("fromState"));

        long lastAuditId = auditLogRepository.findAll().stream().mapToLong(AuditLog::getId).max().orElseThrow();
        JsonNode repeated = jsonResponse(exportJson(engagement, leader,
            "{\"format\":\"JSON\",\"includeExported\":true}"));
        assertEquals(4, repeated.path("totalLogCount").asInt());
        assertEquals(List.of(approved.getId().toString(), exported.getId().toString()), exportedIds(repeated));
        assertEquals("EXPORTED", repeated.path("logs").get(0).path("reviewState").asText());
        assertEquals("EXPORTED", repeated.path("logs").get(1).path("reviewState").asText());
        assertEquals(ReviewState.DRAFT, persistedState(draft));
        assertEquals(ReviewState.SUBMITTED, persistedState(submitted));
        assertEquals(ReviewState.EXPORTED, persistedState(approved));
        assertEquals(ReviewState.EXPORTED, persistedState(exported));
        assertEquals(ReviewState.APPROVED, persistedState(unrelated));
        assertExportAudit(engagement, leader, List.of(approved, exported), true, lastAuditId);
        assertTrue(auditLogRepository.findAll().stream().filter(event -> event.getId() > lastAuditId && event.getAction().equals("EXPORT"))
            .allMatch(event -> event.getMetadata().get("fromState").equals("EXPORTED")));
    }

    private ResultActions exportJson(Engagement engagement, User user, String request) throws Exception {
        return mockMvc.perform(post(exportUri(engagement.getId()))
            .with(asUser(user)).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(request));
    }

    private JsonNode jsonResponse(ResultActions response) throws Exception {
        return jsonMapper.readTree(response.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(header().exists("Content-Disposition"))
            .andReturn().getResponse().getContentAsByteArray());
    }

    private List<String> exportedIds(JsonNode report) {
        List<String> ids = new ArrayList<>();
        for (JsonNode log : report.path("logs")) {
            ids.add(log.path("id").asText());
        }
        return ids;
    }

    private ReviewState persistedState(LogEntry log) {
        return logRepository.findById(log.getId()).orElseThrow().getReviewState();
    }

    private void assertExportAudit(Engagement engagement, User leader, List<LogEntry> exported, boolean includeExported, long afterId) {
        List<AuditLog> audit = auditLogRepository.findAll().stream().filter(event -> event.getId() > afterId).toList();
        assertEquals(exported.size() + 1, audit.size());
        assertTrue(audit.stream().allMatch(event -> event.getActorId().equals(leader.getId())));
        AuditLog aggregate = audit.stream().filter(event -> event.getAction().equals("EXPORT_ENGAGEMENT"))
            .findFirst().orElseThrow();
        assertEquals("ENGAGEMENT", aggregate.getTargetType());
        assertEquals(engagement.getId().toString(), aggregate.getTargetId());
        assertEquals("JSON", aggregate.getMetadata().get("format"));
        assertEquals(exported.size(), ((Number) aggregate.getMetadata().get("logCount")).intValue());
        assertEquals(includeExported, aggregate.getMetadata().get("includeExported"));

        for (LogEntry log : exported) {
            AuditLog transition = audit.stream()
                .filter(event -> event.getAction().equals("EXPORT") && event.getTargetId().equals(log.getId().toString()))
                .findFirst().orElseThrow();
            assertEquals("LOG", transition.getTargetType());
            assertEquals(engagement.getId().longValue(), ((Number) transition.getMetadata().get("engagementId")).longValue());
            assertEquals("EXPORTED", transition.getMetadata().get("toState"));
        }
    }

    @Test
    void leaderCanExportCsv() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        createLog(engagement, leader, ReviewState.APPROVED);

        mockMvc.perform(post(exportUri(engagement.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"format":"CSV"}
                    """))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString(".csv")));
    }

    @Test
    void leaderCanExportPdf() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        createLog(engagement, leader, ReviewState.APPROVED);

        byte[] pdf = mockMvc.perform(post(exportUri(engagement.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"format":"PDF"}
                    """))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/pdf"))
            .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString(".pdf")))
            .andReturn().getResponse().getContentAsByteArray();

        org.junit.jupiter.api.Assertions.assertTrue(pdf.length > 1000, "PDF should be non-empty");
        org.junit.jupiter.api.Assertions.assertEquals(0x25, pdf[0]);
        org.junit.jupiter.api.Assertions.assertEquals(0x50, pdf[1]);
        org.junit.jupiter.api.Assertions.assertEquals(0x44, pdf[2]);
        org.junit.jupiter.api.Assertions.assertEquals(0x46, pdf[3]);
    }

    @Test
    void pdfRendersMarkdownContent() {
        io.muzoo.ssc.plogit.domain.Engagement engagement = createEngagement(createUser("ld@test.local"));
        io.muzoo.ssc.plogit.domain.LogEntry log = createLog(engagement, engagement.getLeader(), ReviewState.APPROVED);

        io.muzoo.ssc.plogit.service.MarkdownService md = new io.muzoo.ssc.plogit.service.MarkdownService();
        io.muzoo.ssc.plogit.service.export.EngagementReportData.LogReportEntry entry =
            new io.muzoo.ssc.plogit.service.export.EngagementReportData.LogReportEntry(
                log.getId().toString(), log.getCreatedAt(), log.getAuthor().getDisplayName(),
                log.getActivityType().name(), log.getTitle(),
                log.getDescription(), md.render(log.getDescription()),
                log.getResult(), md.render(log.getResult()),
                log.getTarget(), log.getToolUsed(), log.getOutcome().name(),
                log.getReviewState().name(), log.getTags(),
                log.getCodeBlock(), log.getCodeLanguage(),
                java.util.List.of()
            );

        io.muzoo.ssc.plogit.service.export.EngagementReportData data =
            new io.muzoo.ssc.plogit.service.export.EngagementReportData(
                engagement.getId(), engagement.getName(), engagement.getDescription(),
                engagement.getStartDate(), engagement.getDueDate(),
                engagement.getLeader().getDisplayName(), java.util.List.of(),
                java.util.List.of(), null, null, null, null, null,
                java.time.Instant.now(), 1L, java.util.List.of(entry)
            );

        io.muzoo.ssc.plogit.service.export.ReportOutput out =
            new io.muzoo.ssc.plogit.service.export.PdfReportStrategy().generate(data);
        org.junit.jupiter.api.Assertions.assertTrue(out.content().length > 1000);
        org.junit.jupiter.api.Assertions.assertEquals("application/pdf", out.contentType());
    }

    @Test
    void exportIncludesApprovedAndExportedWhenFlagSet() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        createLog(engagement, leader, ReviewState.APPROVED);
        createLog(engagement, leader, ReviewState.EXPORTED);

        String content = mockMvc.perform(post(exportUri(engagement.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"format":"CSV","includeExported":true}
                    """))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        String[] lines = content.split("\r\n");
        org.junit.jupiter.api.Assertions.assertEquals(3, lines.length);
    }

    @Test
    void exportDefaultExcludesExportedLogs() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        createLog(engagement, leader, ReviewState.APPROVED);
        createLog(engagement, leader, ReviewState.EXPORTED);

        String content = mockMvc.perform(post(exportUri(engagement.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"format":"CSV"}
                    """))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        String[] lines = content.split("\r\n");
        org.junit.jupiter.api.Assertions.assertEquals(2, lines.length);
    }

    @Test
    void emptyExportProducesValidFile() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(post(exportUri(engagement.getId()))
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"format":"CSV"}
                    """))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"));
    }

    @Test
    void memberCannotExport() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);

        mockMvc.perform(post(exportUri(engagement.getId()))
                .with(asUser(member))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"format":"JSON"}
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    void nonMemberCannotExport() throws Exception {
        User leader = createUser("leader@test.local");
        User outsider = createUser("outsider@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(post(exportUri(engagement.getId()))
                .with(asUser(outsider))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"format":"JSON"}
                    """))
            .andExpect(status().isNotFound());
    }

    @Test
    void csvEscapesCommaInTitle() {
        String escaped = io.muzoo.ssc.plogit.service.export.CsvReportStrategy.escape("hello, world");
        org.junit.jupiter.api.Assertions.assertEquals("\"hello, world\"", escaped);
    }

    @Test
    void csvEscapesQuoteAndNewline() {
        org.junit.jupiter.api.Assertions.assertEquals("\"a\"\"b\"", io.muzoo.ssc.plogit.service.export.CsvReportStrategy.escape("a\"b"));
        org.junit.jupiter.api.Assertions.assertEquals("\"line1\nline2\"", io.muzoo.ssc.plogit.service.export.CsvReportStrategy.escape("line1\nline2"));
        org.junit.jupiter.api.Assertions.assertEquals("plain", io.muzoo.ssc.plogit.service.export.CsvReportStrategy.escape("plain"));
    }

    @Test
    void unauthenticatedCannotExport() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(post(exportUri(engagement.getId()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"format":"JSON"}
                    """))
            .andExpect(status().isForbidden());
    }
}

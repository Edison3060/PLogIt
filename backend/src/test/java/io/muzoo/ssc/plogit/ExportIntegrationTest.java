package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.AuditLog;
import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.service.export.EngagementReportData;
import io.muzoo.ssc.plogit.service.export.JsonReportStrategy;
import io.muzoo.ssc.plogit.service.export.ReportOutput;
import io.muzoo.ssc.plogit.support.IntegrationTestBase;
import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class ExportIntegrationTest extends IntegrationTestBase {

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private JsonReportStrategy jsonReportStrategy;

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

        JsonNode first = jsonResponse(requestExport(engagement, leader, "{\"format\":\"JSON\"}"));
        assertEquals(4, first.path("totalLogCount").asInt());
        assertEquals(List.of(approved.getId().toString()), exportedIds(first));
        assertEquals("APPROVED", first.path("logs").get(0).path("reviewState").asText());
        assertEquals(ReviewState.EXPORTED, persistedState(approved));
        assertExportAudit(engagement, leader, List.of(approved), false, 0, "JSON");
        AuditLog firstTransition = auditLogRepository.findAll().stream()
            .filter(event -> event.getAction().equals("EXPORT")).findFirst().orElseThrow();
        assertEquals("APPROVED", firstTransition.getMetadata().get("fromState"));

        long lastAuditId = auditLogRepository.findAll().stream().mapToLong(AuditLog::getId).max().orElseThrow();
        JsonNode repeated = jsonResponse(requestExport(engagement, leader,
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
        assertExportAudit(engagement, leader, List.of(approved, exported), true, lastAuditId, "JSON");
        assertTrue(auditLogRepository.findAll().stream().filter(event -> event.getId() > lastAuditId && event.getAction().equals("EXPORT"))
            .allMatch(event -> event.getMetadata().get("fromState").equals("EXPORTED")));
    }

    @Test
    void jsonExportIncludesOnlySelectedAttachmentNames() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry approved = createLog(engagement, leader, ReviewState.DRAFT);
        LogEntry draft = createLog(engagement, leader, ReviewState.DRAFT);
        LogEntry submitted = createLog(engagement, leader, ReviewState.SUBMITTED);
        LogEntry previouslyExported = createLog(engagement, leader, ReviewState.EXPORTED);
        String imageBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+a6ioAAAAASUVORK5CYII=";
        byte[] image = Base64.getDecoder().decode(imageBase64);
        for (LogEntry log : List.of(approved, draft)) {
            String filename = log.equals(approved) ? "selected.png" : "excluded-draft.png";
            mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                    .file(new MockMultipartFile("file", filename, MediaType.IMAGE_PNG_VALUE, image))
                    .with(asUser(leader))
                    .with(csrf()))
                .andExpect(status().isOk());
        }
        approved.setReviewState(ReviewState.APPROVED);
        logRepository.saveAndFlush(approved);

        JsonNode report = jsonResponse(requestExport(engagement, leader, "{\"format\":\"JSON\"}"));
        assertEquals(4, report.path("totalLogCount").asInt());
        assertEquals(List.of(approved.getId().toString()), exportedIds(report));
        JsonNode exported = report.path("logs").get(0);
        assertEquals(jsonMapper.readTree("[\"selected.png\"]"), exported.path("attachmentFilenames"));
        assertFalse(report.has("schemaVersion"));
        assertFalse(report.has("evidenceManifest"));
        for (String field : List.of("attachments", "attachmentBytes", "attachmentHashes", "contentHash", "evidenceManifest")) {
            assertFalse(exported.has(field), field);
        }
        assertFalse(report.toString().contains(imageBase64));
        assertFalse(report.toString().contains("excluded-draft.png"));
        assertEquals(ReviewState.DRAFT, persistedState(draft));
        assertEquals(ReviewState.SUBMITTED, persistedState(submitted));
        assertEquals(ReviewState.EXPORTED, persistedState(previouslyExported));
        assertEquals(ReviewState.EXPORTED, persistedState(approved));
    }

    @Test
    void memberDenialDoesNotDiscloseOrMutateEligibleLogs() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        User outsider = createUser("outsider@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);
        LogEntry approved = createLog(engagement, leader, ReviewState.APPROVED);
        String request = "{\"format\":\"JSON\"}";

        String memberBody = requestExport(engagement, member, request)
            .andExpect(status().isForbidden()).andExpect(header().doesNotExist("Content-Disposition"))
            .andReturn().getResponse().getContentAsString();
        String outsiderBody = requestExport(engagement, outsider, request)
            .andExpect(status().isNotFound()).andExpect(header().doesNotExist("Content-Disposition"))
            .andReturn().getResponse().getContentAsString();

        for (String body : List.of(memberBody, outsiderBody)) {
            assertFalse(body.contains(engagement.getName()));
            assertFalse(body.contains(approved.getId().toString()));
            assertFalse(body.contains(leader.getEmail()));
        }
        assertEquals(ReviewState.APPROVED, persistedState(approved));
        assertEquals(0, auditLogRepository.count());
    }

    @Test
    void emptyJsonExportRecordsOnlyTheAggregateAudit() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry submitted = createLog(engagement, leader, ReviewState.SUBMITTED);

        JsonNode report = jsonResponse(requestExport(engagement, leader, "{\"format\":\"JSON\"}"));
        assertEquals(1, report.path("totalLogCount").asInt());
        assertTrue(report.path("logs").isArray());
        assertTrue(report.path("logs").isEmpty());
        assertEquals(ReviewState.SUBMITTED, persistedState(submitted));
        assertExportAudit(engagement, leader, List.of(), false, 0, "JSON");
    }

    @Test
    void jsonSerializerMatchesLegacyFixture() throws Exception {
        ReportOutput output = jsonReportStrategy.generate(legacyReportData());
        assertEquals("application/json", output.contentType());
        assertEquals("Synthetic_Engagement-evidence.json", output.filename());
        try (InputStream fixture = getClass().getResourceAsStream("/exports/legacy-evidence.json")) {
            assertEquals(jsonMapper.readTree(fixture), jsonMapper.readTree(output.content()));
        }
    }

    static EngagementReportData legacyReportData() {
        EngagementReportData.LogReportEntry log = new EngagementReportData.LogReportEntry(
            "00000000-0000-4000-8000-000000000001", Instant.parse("2026-11-01T12:00:00Z"),
            "Synthetic Lead", "RECONNAISSANCE", "Synthetic activity",
            "**Raw** Markdown", "<strong>Raw</strong> Markdown", "Observed *text*", "<em>text</em>",
            "example.invalid", null, "IN_PROGRESS", "APPROVED", null,
            null, null, List.of(new EngagementReportData.ImageAttachment("sample.png", "image/png", new byte[]{1, 2, 3})));
        return new EngagementReportData(
            42L, "Synthetic Engagement", "Synthetic fixture", LocalDate.parse("2026-11-01"), null,
            "Synthetic Lead", List.of(new EngagementReportData.MemberInfo("lead@example.invalid", "Synthetic Lead", "LEADER")),
            null, null, null, null, null, null, Instant.parse("2026-11-01T13:00:00Z"), 2, List.of(log)
        );
    }

    private ResultActions requestExport(Engagement engagement, User user, String request) throws Exception {
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

    private void assertExportAudit(Engagement engagement, User leader, List<LogEntry> exported, boolean includeExported, long afterId, String format) {
        List<AuditLog> audit = auditLogRepository.findAll().stream().filter(event -> event.getId() > afterId).toList();
        assertEquals(exported.size() + 1, audit.size());
        assertTrue(audit.stream().allMatch(event -> event.getActorId().equals(leader.getId())));
        AuditLog aggregate = audit.stream().filter(event -> event.getAction().equals("EXPORT_ENGAGEMENT"))
            .findFirst().orElseThrow();
        assertEquals("ENGAGEMENT", aggregate.getTargetType());
        assertEquals(engagement.getId().toString(), aggregate.getTargetId());
        assertEquals(format, aggregate.getMetadata().get("format"));
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
    void leaderCanExportPdfWithSelectedEvidenceAndMinimalAudit() throws Exception {
        User leader = createUser("leader-account@test.local");
        leader.setDisplayName("Test Lead");
        userRepository.saveAndFlush(leader);
        Engagement engagement = createEngagement(leader);
        LogEntry approved = createLog(engagement, leader, ReviewState.APPROVED);
        approved.setTitle("Approved evidence");
        approved.setDescription("**Evidence summary**");
        approved.setCodeBlock("<sample>literal code</sample>");
        approved.setCodeLanguage("<em>plain</em>");
        logRepository.saveAndFlush(approved);
        LogEntry draft = createLog(engagement, leader, ReviewState.DRAFT);
        draft.setTitle("Private draft marker");
        logRepository.saveAndFlush(draft);
        Engagement otherEngagement = createEngagement(leader);
        LogEntry unrelated = createLog(otherEngagement, leader, ReviewState.APPROVED);
        unrelated.setTitle("Different engagement marker");
        logRepository.saveAndFlush(unrelated);

        byte[] pdf = requestExport(engagement, leader, "{\"format\":\"PDF\"}")
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/pdf"))
            .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString(".pdf")))
            .andReturn().getResponse().getContentAsByteArray();

        try (PDDocument document = Loader.loadPDF(pdf)) {
            String report = new PDFTextStripper().getText(document);
            assertTrue(report.contains("Approved evidence"));
            assertTrue(report.contains("Evidence summary"));
            assertTrue(report.contains("Test Lead"));
            assertTrue(report.contains("<sample>literal code</sample>"));
            assertTrue(report.contains("<em>plain</em>"));
            assertFalse(report.contains("Private draft marker"));
            assertFalse(report.contains("Different engagement marker"));
            assertFalse(report.contains(leader.getEmail()));
        }
        assertEquals(ReviewState.EXPORTED, persistedState(approved));
        assertEquals(ReviewState.DRAFT, persistedState(draft));
        assertEquals(ReviewState.APPROVED, persistedState(unrelated));
        assertExportAudit(engagement, leader, List.of(approved), false, 0, "PDF");
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

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class ExportIntegrationTest extends IntegrationTestBase {

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
            .andExpect(status().isNotFound());
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

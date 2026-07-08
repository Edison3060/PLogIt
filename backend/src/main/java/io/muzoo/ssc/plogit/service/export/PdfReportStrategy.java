package io.muzoo.ssc.plogit.service.export;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import io.muzoo.ssc.plogit.domain.ReportFormat;
import io.muzoo.ssc.plogit.web.exception.ConflictException;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Component
public class PdfReportStrategy implements ReportStrategy {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm z")
        .withZone(ZoneId.systemDefault());

    @Override
    public ReportFormat format() {
        return ReportFormat.PDF;
    }

    @Override
    public ReportOutput generate(EngagementReportData data) {
        String html = renderHtml(data);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();

            String filename = safeName(data.engagementName()) + "-evidence.pdf";
            return new ReportOutput(out.toByteArray(), "application/pdf", filename);
        } catch (Exception e) {
            throw new ConflictException("Could not generate PDF");
        }
    }

    private String renderHtml(EngagementReportData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset=\"UTF-8\"/><style>");
        sb.append(CSS);
        sb.append("</style></head><body>");

        renderCover(sb, data);
        renderScope(sb, data);
        renderObjectives(sb, data);
        renderLogs(sb, data);
        renderAppendix(sb, data);

        sb.append("</body></html>");
        return sb.toString();
    }

    private void renderCover(StringBuilder sb, EngagementReportData data) {
        sb.append("<div class=\"cover\">");
        sb.append("<h1>").append(escapeHtml(data.engagementName())).append("</h1>");
        sb.append("<p class=\"subtitle\">Pentest Engagement Evidence Pack</p>");
        sb.append("<table class=\"cover-meta\">");
        row(sb, "Engagement", escapeHtml(data.engagementName()));
        row(sb, "Start Date", data.startDate() == null ? "-" : data.startDate().toString());
        row(sb, "Due Date", data.dueDate() == null ? "-" : data.dueDate().toString());
        row(sb, "Leader", escapeHtml(data.leaderName()));
        row(sb, "Exported At", data.exportedAt() == null ? "-" : DATE_FMT.format(data.exportedAt()));
        row(sb, "Logs Included", String.valueOf(data.logs().size()));
        row(sb, "Total Logs In Engagement", String.valueOf(data.totalLogCount()));
        sb.append("</table>");
        sb.append("<h3>Team</h3><ul>");
        for (EngagementReportData.MemberInfo m : data.members()) {
            sb.append("<li>").append(escapeHtml(m.displayName()))
                .append(" (").append(escapeHtml(m.role())).append(")</li>");
        }
        sb.append("</ul>");
        sb.append("</div>");
    }

    private void renderScope(StringBuilder sb, EngagementReportData data) {
        sb.append("<h2>Scope &amp; Rules of Engagement</h2>");
        sb.append("<h3>In-Scope Targets</h3>");
        if (data.inScopeTargets() == null || data.inScopeTargets().isEmpty()) {
            sb.append("<p class=\"muted\">No in-scope targets declared.</p>");
        } else {
            sb.append("<ul>");
            for (String t : data.inScopeTargets()) {
                sb.append("<li><code>").append(escapeHtml(t)).append("</code></li>");
            }
            sb.append("</ul>");
        }
        section(sb, "Allowed Testing Hours", data.allowedHours());
        section(sb, "Allowed Techniques", data.allowedTechniques());
        section(sb, "Forbidden Techniques", data.forbiddenTechniques());
        section(sb, "Emergency Contacts", data.emergencyContacts());
        section(sb, "Explicitly Out of Scope", data.outOfScope());
    }

    private void renderObjectives(StringBuilder sb, EngagementReportData data) {
        sb.append("<h2>Engagement Description</h2>");
        sb.append("<p>").append(nl2br(escapeHtml(data.description()))).append("</p>");
    }

    private void renderLogs(StringBuilder sb, EngagementReportData data) {
        sb.append("<h2>Activity Logs</h2>");
        if (data.logs().isEmpty()) {
            sb.append("<p class=\"muted\">No logs included in this export.</p>");
            return;
        }
        for (EngagementReportData.LogReportEntry log : data.logs()) {
            renderSingleLog(sb, log);
        }
    }

    private void renderSingleLog(StringBuilder sb, EngagementReportData.LogReportEntry log) {
        sb.append("<div class=\"log\">");
        sb.append("<h3>").append(escapeHtml(log.title())).append("</h3>");
        sb.append("<table class=\"log-meta\">");
        row(sb, "Timestamp", log.createdAt() == null ? "-" : DATE_FMT.format(log.createdAt()));
        row(sb, "Author", escapeHtml(log.authorName()));
        row(sb, "Activity Type", prettify(log.activityType()));
        row(sb, "Target", log.target() == null ? "-" : escapeHtml(log.target()));
        row(sb, "Tool", log.toolUsed() == null ? "-" : escapeHtml(log.toolUsed()));
        row(sb, "Outcome", prettify(log.outcome()));
        row(sb, "Review State", prettify(log.reviewState()));
        sb.append("</table>");

        if (log.tags() != null && !log.tags().isEmpty()) {
            sb.append("<p class=\"tags\"><strong>Tags:</strong> ");
            sb.append(escapeHtml(String.join(", ", log.tags())));
            sb.append("</p>");
        }

        if (log.descriptionHtml() != null && !log.descriptionHtml().isBlank()) {
            sb.append("<h4>Description</h4>");
            sb.append("<div class=\"markdown\">").append(log.descriptionHtml()).append("</div>");
        }
        if (log.resultHtml() != null && !log.resultHtml().isBlank()) {
            sb.append("<h4>Result</h4>");
            sb.append("<div class=\"markdown\">").append(log.resultHtml()).append("</div>");
        }

        if (log.codeBlock() != null && !log.codeBlock().isBlank()) {
            String lang = log.codeLanguage() == null ? "" : log.codeLanguage();
            sb.append("<h4>Code Block (").append(escapeHtml(lang)).append(")</h4>");
            sb.append("<pre>").append(escapeHtml(log.codeBlock())).append("</pre>");
        }

        if (!log.images().isEmpty()) {
            sb.append("<h4>Evidence Images</h4>");
            for (EngagementReportData.ImageAttachment img : log.images()) {
                String dataUri = "data:" + img.mimeType() + ";base64,"
                    + Base64.getEncoder().encodeToString(img.content());
                sb.append("<div class=\"figure\">");
                sb.append("<img src=\"").append(dataUri).append("\" />");
                sb.append("<p class=\"caption\">").append(escapeHtml(img.filename())).append("</p>");
                sb.append("</div>");
            }
        }

        sb.append("</div>");
    }

    private void renderAppendix(StringBuilder sb, EngagementReportData data) {
        sb.append("<h2>Appendix</h2>");
        sb.append("<p>Logs included in this export: <strong>").append(data.logs().size()).append("</strong></p>");
        sb.append("<p>Total logs in engagement: <strong>").append(data.totalLogCount()).append("</strong></p>");
        if (data.outOfScope() != null && !data.outOfScope().isBlank()) {
            sb.append("<h3>Out-of-Scope Notes</h3>");
            sb.append("<p>").append(nl2br(escapeHtml(data.outOfScope()))).append("</p>");
        }
    }

    private void section(StringBuilder sb, String heading, String body) {
        sb.append("<h3>").append(heading).append("</h3>");
        if (body == null || body.isBlank()) {
            sb.append("<p class=\"muted\">Not specified.</p>");
        } else {
            sb.append("<p>").append(nl2br(escapeHtml(body))).append("</p>");
        }
    }

    private void row(StringBuilder sb, String key, String value) {
        sb.append("<tr><td class=\"k\">").append(key).append("</td><td>")
            .append(value).append("</td></tr>");
    }

    private static final String CSS = """
        body { font-family: 'Helvetica', 'Arial', sans-serif; font-size: 11pt; color: #1a1a1a; margin: 36pt; line-height: 1.5; }
        h1 { font-size: 22pt; color: #1e40af; margin-bottom: 0; }
        h2 { font-size: 15pt; color: #1e40af; border-bottom: 2px solid #1e40af; padding-bottom: 3pt; margin-top: 24pt; }
        h3 { font-size: 12pt; color: #1f2937; margin-bottom: 4pt; }
        h4 { font-size: 11pt; color: #374151; margin: 10pt 0 4pt 0; }
        .subtitle { color: #6b7280; font-style: italic; margin-top: 0; }
        .cover { page-break-after: always; }
        table { border-collapse: collapse; width: 100%; }
        .cover-meta td, .log-meta td { border: 1px solid #d1d5db; padding: 4pt 8pt; vertical-align: top; }
        .cover-meta td.k, .log-meta td.k { background: #f3f4f6; font-weight: bold; width: 32%; }
        .muted { color: #9ca3af; font-style: italic; }
        .log { border: 1px solid #e5e7eb; border-radius: 4pt; padding: 10pt; margin: 10pt 0; page-break-inside: avoid; }
        pre { background: #f9fafb; border: 1px solid #e5e7eb; border-radius: 3pt; padding: 8pt; font-family: 'Courier New', monospace; font-size: 9pt; white-space: pre-wrap; word-wrap: break-word; }
        code { font-family: 'Courier New', monospace; background: #f3f4f6; padding: 0 2pt; }
        .markdown p { margin: 4pt 0; }
        .figure { margin: 8pt 0; text-align: center; page-break-inside: avoid; }
        .figure img { max-width: 100%; border: 1px solid #e5e7eb; }
        .caption { font-size: 9pt; color: #6b7280; margin-top: 2pt; }
        .tags { font-size: 9pt; color: #6b7280; }
        ul { margin-top: 2pt; }
        """;

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }

    private String nl2br(String s) {
        if (s == null) return "";
        return s.replace("\n", "<br/>");
    }

    private String prettify(String enumValue) {
        if (enumValue == null) return "-";
        String spaced = enumValue.replace("_", " ");
        return spaced.substring(0, 1).toUpperCase() + spaced.substring(1).toLowerCase();
    }

    private String safeName(String name) {
        return name == null ? "engagement" : name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}

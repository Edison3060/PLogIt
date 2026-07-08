package io.muzoo.ssc.plogit.service.export;

import io.muzoo.ssc.plogit.domain.ReportFormat;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CsvReportStrategy implements ReportStrategy {

    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter
        .ISO_INSTANT;

    private static final List<String> HEADER = List.of(
        "id", "createdAt", "author", "activityType", "title",
        "target", "tool", "outcome", "reviewState",
        "description", "result", "tags"
    );

    @Override
    public ReportFormat format() {
        return ReportFormat.CSV;
    }

    @Override
    public ReportOutput generate(EngagementReportData data) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", HEADER)).append("\r\n");
        for (EngagementReportData.LogReportEntry log : data.logs()) {
            String[] fields = new String[] {
                log.id(),
                log.createdAt() == null ? "" : TIMESTAMP_FMT.format(log.createdAt()),
                log.authorName(),
                log.activityType(),
                log.title(),
                log.target(),
                log.toolUsed(),
                log.outcome(),
                log.reviewState(),
                log.description(),
                log.result(),
                log.tags() == null ? "" : String.join(";", log.tags())
            };
            for (int i = 0; i < fields.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(escape(fields[i]));
            }
            sb.append("\r\n");
        }

        byte[] content = sb.toString().getBytes(StandardCharsets.UTF_8);
        String filename = safeName(data.engagementName()) + "-logs.csv";
        return new ReportOutput(content, "text/csv", filename);
    }

    public static String escape(String field) {
        if (field == null) {
            return "";
        }
        boolean needsQuoting = field.indexOf(',') >= 0
            || field.indexOf('"') >= 0
            || field.indexOf('\n') >= 0
            || field.indexOf('\r') >= 0;
        if (!needsQuoting) {
            return field;
        }
        return "\"" + field.replace("\"", "\"\"") + "\"";
    }

    private String safeName(String name) {
        return name == null ? "engagement" : name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}

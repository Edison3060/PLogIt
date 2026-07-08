package io.muzoo.ssc.plogit.service.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.muzoo.ssc.plogit.domain.ReportFormat;
import io.muzoo.ssc.plogit.web.exception.ConflictException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Component
public class JsonReportStrategy implements ReportStrategy {

    private final ObjectMapper objectMapper;

    public JsonReportStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ReportFormat format() {
        return ReportFormat.JSON;
    }

    @Override
    public ReportOutput generate(EngagementReportData data) {
        List<JsonLog> jsonLogs = data.logs().stream()
            .map(this::toJsonLog)
            .toList();
        JsonReport report = new JsonReport(
            data.engagementId(),
            data.engagementName(),
            data.description(),
            data.startDate(),
            data.dueDate(),
            data.leaderName(),
            data.members(),
            data.inScopeTargets(),
            data.allowedHours(),
            data.allowedTechniques(),
            data.forbiddenTechniques(),
            data.emergencyContacts(),
            data.outOfScope(),
            data.exportedAt(),
            data.totalLogCount(),
            jsonLogs
        );

        try {
            byte[] content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(report);
            String filename = safeName(data.engagementName()) + "-evidence.json";
            return new ReportOutput(content, "application/json", filename);
        } catch (JsonProcessingException e) {
            throw new ConflictException("Could not serialize report");
        }
    }

    private JsonLog toJsonLog(EngagementReportData.LogReportEntry log) {
        return new JsonLog(
            log.id(),
            log.createdAt(),
            log.authorName(),
            log.activityType(),
            log.title(),
            log.description(),
            log.result(),
            log.target(),
            log.toolUsed(),
            log.outcome(),
            log.reviewState(),
            log.tags(),
            log.codeBlock(),
            log.codeLanguage(),
            log.images().stream().map(EngagementReportData.ImageAttachment::filename).toList()
        );
    }

    private String safeName(String name) {
        return name == null ? "engagement" : name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private record JsonReport(
        Long engagementId,
        String engagementName,
        String description,
        java.time.LocalDate startDate,
        java.time.LocalDate dueDate,
        String leaderName,
        List<EngagementReportData.MemberInfo> members,
        List<String> inScopeTargets,
        String allowedHours,
        String allowedTechniques,
        String forbiddenTechniques,
        String emergencyContacts,
        String outOfScope,
        Instant exportedAt,
        long totalLogCount,
        List<JsonLog> logs
    ) {
    }

    private record JsonLog(
        String id,
        Instant createdAt,
        String authorName,
        String activityType,
        String title,
        String description,
        String result,
        String target,
        String toolUsed,
        String outcome,
        String reviewState,
        List<String> tags,
        String codeBlock,
        String codeLanguage,
        List<String> attachmentFilenames
    ) {
    }
}

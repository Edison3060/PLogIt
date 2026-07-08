package io.muzoo.ssc.plogit.service.export;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record EngagementReportData(
    Long engagementId,
    String engagementName,
    String description,
    LocalDate startDate,
    LocalDate dueDate,
    String leaderName,
    List<MemberInfo> members,
    List<String> inScopeTargets,
    String allowedHours,
    String allowedTechniques,
    String forbiddenTechniques,
    String emergencyContacts,
    String outOfScope,
    Instant exportedAt,
    long totalLogCount,
    List<LogReportEntry> logs
) {

    public record MemberInfo(String email, String displayName, String role) {
    }

    public record LogReportEntry(
        String id,
        Instant createdAt,
        String authorName,
        String activityType,
        String title,
        String description,
        String descriptionHtml,
        String result,
        String resultHtml,
        String target,
        String toolUsed,
        String outcome,
        String reviewState,
        List<String> tags,
        String codeBlock,
        String codeLanguage,
        List<ImageAttachment> images
    ) {
    }

    public record ImageAttachment(String filename, String mimeType, byte[] content) {
    }
}

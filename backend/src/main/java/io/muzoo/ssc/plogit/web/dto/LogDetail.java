package io.muzoo.ssc.plogit.web.dto;

import io.muzoo.ssc.plogit.domain.ActivityType;
import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.Outcome;
import io.muzoo.ssc.plogit.domain.ReviewState;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LogDetail(
    UUID id,
    Long engagementId,
    Long leaderId,
    Long authorId,
    String authorDisplayName,
    ActivityType activityType,
    String title,
    String description,
    String result,
    String target,
    String toolUsed,
    Outcome outcome,
    List<String> tags,
    String codeBlock,
    String codeLanguage,
    ReviewState reviewState,
    String rejectionComment,
    Instant createdAt,
    Instant lastEditedAt,
    Long lastEditedById
) {
    public static LogDetail from(LogEntry log) {
        return new LogDetail(
            log.getId(),
            log.getEngagement().getId(),
            log.getEngagement().getLeader().getId(),
            log.getAuthor().getId(),
            log.getAuthor().getDisplayName(),
            log.getActivityType(),
            log.getTitle(),
            log.getDescription(),
            log.getResult(),
            log.getTarget(),
            log.getToolUsed(),
            log.getOutcome(),
            log.getTags(),
            log.getCodeBlock(),
            log.getCodeLanguage(),
            log.getReviewState(),
            log.getRejectionComment(),
            log.getCreatedAt(),
            log.getLastEditedAt(),
            log.getLastEditedById()
        );
    }
}

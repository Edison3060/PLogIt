package io.muzoo.ssc.plogit.web.dto;

import io.muzoo.ssc.plogit.domain.ActivityType;
import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.Outcome;
import io.muzoo.ssc.plogit.domain.ReviewState;

import java.time.Instant;
import java.util.UUID;

public record LogSummary(
    UUID id,
    Instant createdAt,
    String authorDisplayName,
    String title,
    ActivityType activityType,
    String target,
    Outcome outcome,
    ReviewState reviewState
) {
    public static LogSummary from(LogEntry log) {
        return new LogSummary(
            log.getId(),
            log.getCreatedAt(),
            log.getAuthor().getDisplayName(),
            log.getTitle(),
            log.getActivityType(),
            log.getTarget(),
            log.getOutcome(),
            log.getReviewState()
        );
    }
}

package io.muzoo.ssc.plogit.web.dto;

import io.muzoo.ssc.plogit.domain.ActivityType;
import io.muzoo.ssc.plogit.domain.Outcome;
import io.muzoo.ssc.plogit.domain.ReviewState;

public record LogFilter(
    Long authorId,
    ActivityType activityType,
    Outcome outcome,
    ReviewState reviewState,
    String search
) {
}

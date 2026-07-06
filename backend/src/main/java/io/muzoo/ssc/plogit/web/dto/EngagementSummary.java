package io.muzoo.ssc.plogit.web.dto;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementStatus;

import java.time.LocalDate;

public record EngagementSummary(
    Long id,
    String name,
    EngagementStatus status,
    LocalDate dueDate,
    String role
) {
    public static EngagementSummary from(Engagement engagement, String role) {
        return new EngagementSummary(
            engagement.getId(),
            engagement.getName(),
            engagement.getStatus(),
            engagement.getDueDate(),
            role
        );
    }
}

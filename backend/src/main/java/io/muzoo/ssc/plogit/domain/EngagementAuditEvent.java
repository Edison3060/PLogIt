package io.muzoo.ssc.plogit.domain;

import java.util.Map;

public record EngagementAuditEvent(
    Long actorId,
    Long engagementId,
    String action,
    String targetType,
    String targetId,
    Map<String, Object> metadata
) {
}

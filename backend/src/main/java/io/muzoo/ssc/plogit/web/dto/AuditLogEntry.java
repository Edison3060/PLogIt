package io.muzoo.ssc.plogit.web.dto;

import io.muzoo.ssc.plogit.domain.AuditLog;

import java.time.Instant;
import java.util.Map;

public record AuditLogEntry(
    Long id,
    Long actorId,
    String action,
    String targetType,
    String targetId,
    Map<String, Object> metadata,
    Instant timestamp
) {
    public static AuditLogEntry from(AuditLog log) {
        return new AuditLogEntry(
            log.getId(),
            log.getActorId(),
            log.getAction(),
            log.getTargetType(),
            log.getTargetId(),
            log.getMetadata(),
            log.getTimestamp()
        );
    }
}

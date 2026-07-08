package io.muzoo.ssc.plogit.web.dto;

import io.muzoo.ssc.plogit.domain.LogVersion;

import java.time.Instant;
import java.util.Map;

public record LogVersionSummary(
    Long id,
    Integer versionNumber,
    Map<String, Object> snapshot,
    Long editedById,
    Instant editedAt
) {
    public static LogVersionSummary from(LogVersion version) {
        return new LogVersionSummary(
            version.getId(),
            version.getVersionNumber(),
            version.getSnapshot(),
            version.getEditedById(),
            version.getEditedAt()
        );
    }
}

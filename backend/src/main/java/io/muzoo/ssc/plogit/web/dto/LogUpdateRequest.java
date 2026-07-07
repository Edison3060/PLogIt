package io.muzoo.ssc.plogit.web.dto;

import io.muzoo.ssc.plogit.domain.ActivityType;
import io.muzoo.ssc.plogit.domain.Outcome;
import jakarta.validation.constraints.Size;

import java.util.List;

public record LogUpdateRequest(
    ActivityType activityType,
    @Size(max = 255) String title,
    String description,
    String result,
    @Size(max = 255) String target,
    @Size(max = 255) String toolUsed,
    Outcome outcome,
    List<String> tags,
    String codeBlock,
    @Size(max = 40) String codeLanguage
) {
}

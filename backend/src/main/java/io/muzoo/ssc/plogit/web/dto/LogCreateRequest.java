package io.muzoo.ssc.plogit.web.dto;

import io.muzoo.ssc.plogit.domain.ActivityType;
import io.muzoo.ssc.plogit.domain.Outcome;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record LogCreateRequest(
    @NotNull ActivityType activityType,
    @NotBlank @Size(max = 255) String title,
    @NotBlank String description,
    @NotBlank String result,
    @Size(max = 255) String target,
    @Size(max = 255) String toolUsed,
    @NotNull Outcome outcome,
    List<String> tags,
    String codeBlock,
    @Size(max = 40) String codeLanguage
) {
}

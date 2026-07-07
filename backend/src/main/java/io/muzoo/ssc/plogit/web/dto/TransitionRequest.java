package io.muzoo.ssc.plogit.web.dto;

import io.muzoo.ssc.plogit.domain.ReviewAction;
import jakarta.validation.constraints.NotNull;

public record TransitionRequest(
    @NotNull ReviewAction action,
    String comment
) {
}

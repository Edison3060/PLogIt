package io.muzoo.ssc.plogit.web.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinRequest(
    @NotBlank String code
) {
}

package io.muzoo.ssc.plogit.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateEngagementRequest(
    @NotBlank @Size(max = 255) String name,
    @NotBlank @Size(max = 4000) String description,
    LocalDate startDate,
    LocalDate dueDate
) {
}

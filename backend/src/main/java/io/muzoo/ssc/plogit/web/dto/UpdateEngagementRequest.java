package io.muzoo.ssc.plogit.web.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record UpdateEngagementRequest(
    @Size(max = 255) String name,
    @Size(max = 4000) String description,
    LocalDate startDate,
    LocalDate dueDate,
    @Size(max = 1000) String allowedHours,
    @Size(max = 4000) String allowedTechniques,
    @Size(max = 4000) String forbiddenTechniques,
    @Size(max = 4000) String emergencyContacts,
    @Size(max = 4000) String outOfScope,
    List<String> inScopeTargets
) {
}

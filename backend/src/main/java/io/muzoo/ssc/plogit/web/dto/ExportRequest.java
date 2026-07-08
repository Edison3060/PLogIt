package io.muzoo.ssc.plogit.web.dto;

import io.muzoo.ssc.plogit.domain.ReportFormat;
import jakarta.validation.constraints.NotNull;

public record ExportRequest(
    @NotNull ReportFormat format,
    boolean includeExported
) {
}

package io.muzoo.ssc.plogit.service.export;

import io.muzoo.ssc.plogit.domain.ReportFormat;

public interface ReportStrategy {

    ReportFormat format();

    ReportOutput generate(EngagementReportData data);
}

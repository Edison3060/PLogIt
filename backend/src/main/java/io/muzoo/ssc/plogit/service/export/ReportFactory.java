package io.muzoo.ssc.plogit.service.export;

import io.muzoo.ssc.plogit.domain.ReportFormat;
import io.muzoo.ssc.plogit.web.exception.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ReportFactory {

    private final Map<ReportFormat, ReportStrategy> strategies = new EnumMap<>(ReportFormat.class);

    public ReportFactory(List<ReportStrategy> strategyBeans) {
        for (ReportStrategy strategy : strategyBeans) {
            strategies.put(strategy.format(), strategy);
        }
    }

    public ReportStrategy forFormat(ReportFormat format) {
        ReportStrategy strategy = strategies.get(format);
        if (strategy == null) {
            throw new NotFoundException("Unsupported report format: " + format);
        }
        return strategy;
    }
}

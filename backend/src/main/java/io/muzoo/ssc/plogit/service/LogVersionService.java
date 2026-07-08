package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.LogSnapshot;
import io.muzoo.ssc.plogit.domain.LogVersion;
import io.muzoo.ssc.plogit.repository.LogVersionRepository;
import io.muzoo.ssc.plogit.web.dto.LogVersionSummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LogVersionService {

    private final LogVersionRepository versionRepository;

    public LogVersionService(LogVersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    public void snapshot(LogEntry log, Long editedById) {
        int nextVersion = versionRepository.findByLogOrderByVersionNumberAsc(log).size() + 1;
        LogVersion version = LogVersion.builder()
            .log(log)
            .versionNumber(nextVersion)
            .snapshot(LogSnapshot.from(log).toMap())
            .editedById(editedById)
            .build();
        versionRepository.save(version);
    }

    @Transactional(readOnly = true)
    public List<LogVersionSummary> historyFor(LogEntry log) {
        return versionRepository.findByLogOrderByVersionNumberAsc(log).stream()
            .map(LogVersionSummary::from)
            .toList();
    }
}

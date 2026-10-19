package io.muzoo.ssc.plogit.repository;

import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.LogVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LogVersionRepository extends JpaRepository<LogVersion, Long> {

    List<LogVersion> findByLogOrderByVersionNumberAsc(LogEntry log);

    Optional<LogVersion> findTopByLogOrderByVersionNumberDesc(LogEntry log);
}

package io.muzoo.ssc.plogit.repository;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.ScopeWarning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScopeWarningRepository extends JpaRepository<ScopeWarning, Long> {

    List<ScopeWarning> findByEngagement(Engagement engagement);
}

package io.muzoo.ssc.plogit.repository;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.ReviewState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface LogEntryRepository extends JpaRepository<LogEntry, UUID>, JpaSpecificationExecutor<LogEntry> {

    Page<LogEntry> findByEngagementOrderByCreatedAtDesc(Engagement engagement, Pageable pageable);

    long countByEngagement(Engagement engagement);

    List<LogEntry> findByEngagementAndReviewStateInOrderByCreatedAtAsc(Engagement engagement, Collection<ReviewState> states);
}

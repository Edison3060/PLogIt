package io.muzoo.ssc.plogit.repository;

import io.muzoo.ssc.plogit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query(value = """
        SELECT * FROM audit_log
        WHERE (metadata ->> 'engagementId') = :engagementId
        ORDER BY timestamp DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<AuditLog> findByEngagementId(@Param("engagementId") String engagementId, @Param("limit") int limit);
}

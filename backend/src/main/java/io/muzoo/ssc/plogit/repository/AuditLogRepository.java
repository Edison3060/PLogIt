package io.muzoo.ssc.plogit.repository;

import io.muzoo.ssc.plogit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}

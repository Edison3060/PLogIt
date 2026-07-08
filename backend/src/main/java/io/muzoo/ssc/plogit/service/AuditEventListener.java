package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.AuditLog;
import io.muzoo.ssc.plogit.domain.LogTransitionedEvent;
import io.muzoo.ssc.plogit.repository.AuditLogRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AuditEventListener {

    private final AuditLogRepository auditLogRepository;

    public AuditEventListener(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Transactional
    public void onLogTransitioned(LogTransitionedEvent event) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("fromState", event.getFromState());
        metadata.put("toState", event.getToState());
        metadata.put("engagementId", event.getEngagementId());
        if (event.getComment() != null) {
            metadata.put("comment", event.getComment());
        }

        AuditLog entry = AuditLog.builder()
            .actorId(event.getActorId())
            .action(event.getAction())
            .targetType("LOG")
            .targetId(String.valueOf(event.getLogId()))
            .metadata(metadata)
            .build();
        auditLogRepository.save(entry);
    }
}

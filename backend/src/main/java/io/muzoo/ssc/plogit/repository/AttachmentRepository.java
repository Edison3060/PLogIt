package io.muzoo.ssc.plogit.repository;

import io.muzoo.ssc.plogit.domain.Attachment;
import io.muzoo.ssc.plogit.domain.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByLog(LogEntry log);

    List<Attachment> findByLogOrderByUploadedAtDesc(LogEntry log);
}

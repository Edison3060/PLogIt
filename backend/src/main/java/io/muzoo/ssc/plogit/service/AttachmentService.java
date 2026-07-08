package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.Attachment;
import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.AttachmentRepository;
import io.muzoo.ssc.plogit.repository.LogEntryRepository;
import io.muzoo.ssc.plogit.web.dto.AttachmentResponse;
import io.muzoo.ssc.plogit.web.exception.ConflictException;
import io.muzoo.ssc.plogit.web.exception.NotFoundException;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AttachmentService {

    private static final List<String> ALLOWED_TYPES = List.of(
        "image/png", "image/jpeg", "image/gif", "image/webp"
    );

    private final AttachmentRepository attachmentRepository;
    private final LogEntryRepository logRepository;
    private final MembershipService membershipService;
    private final Path storageDir;
    private final Tika tika = new Tika();

    public AttachmentService(
        AttachmentRepository attachmentRepository,
        LogEntryRepository logRepository,
        MembershipService membershipService,
        @Value("${plogit.storage.dir:./attachments}") String storageDir
    ) {
        this.attachmentRepository = attachmentRepository;
        this.logRepository = logRepository;
        this.membershipService = membershipService;
        this.storageDir = Paths.get(storageDir);
    }

    public AttachmentResponse upload(UUID logId, MultipartFile file, User uploader) {
        LogEntry log = logRepository.findById(logId)
            .orElseThrow(() -> new NotFoundException("Log not found"));
        membershipService.assertMember(log.getEngagement(), uploader);

        String detectedType;
        try {
            detectedType = tika.detect(file.getBytes());
        } catch (IOException e) {
            throw new ConflictException("Could not read file");
        }

        if (!ALLOWED_TYPES.contains(detectedType)) {
            throw new ConflictException("Only image files are allowed (PNG, JPEG, GIF, WebP). Detected: " + detectedType);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            originalFilename = "upload";
        }
        String safeFilename = originalFilename.replaceAll("[^a-zA-Z0-9.\\-]", "_");
        String storedFilename = UUID.randomUUID() + "-" + safeFilename;

        Long engagementId = log.getEngagement().getId();
        Path logDir = storageDir.resolve(String.valueOf(engagementId)).resolve(String.valueOf(logId));
        Path destination = logDir.resolve(storedFilename);

        try {
            Files.createDirectories(logDir);
            Files.copy(file.getInputStream(), destination);
        } catch (IOException e) {
            throw new ConflictException("Could not store file");
        }

        String relativePath = engagementId + "/" + logId + "/" + storedFilename;
        Attachment attachment = Attachment.builder()
            .log(log)
            .filename(originalFilename)
            .storagePath(relativePath)
            .mimeType(detectedType)
            .size(file.getSize())
            .uploadedById(uploader.getId())
            .build();
        attachment = attachmentRepository.save(attachment);

        return AttachmentResponse.from(attachment);
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponse> listForLog(UUID logId, User viewer) {
        LogEntry log = logRepository.findById(logId)
            .orElseThrow(() -> new NotFoundException("Log not found"));
        membershipService.assertMember(log.getEngagement(), viewer);
        return attachmentRepository.findByLogOrderByUploadedAtDesc(log).stream()
            .map(AttachmentResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public AttachmentFile loadFile(UUID logId, Long attachmentId, User viewer) {
        LogEntry log = logRepository.findById(logId)
            .orElseThrow(() -> new NotFoundException("Log not found"));
        membershipService.assertMember(log.getEngagement(), viewer);

        Attachment attachment = attachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new NotFoundException("Attachment not found"));
        if (!attachment.getLog().getId().equals(log.getId())) {
            throw new NotFoundException("Attachment not found");
        }

        return readFile(attachment);
    }

    @Transactional(readOnly = true)
    public List<AttachmentFile> loadImagesForLog(LogEntry log) {
        return attachmentRepository.findByLogOrderByUploadedAtDesc(log).stream()
            .filter(a -> a.getMimeType() != null && a.getMimeType().startsWith("image/"))
            .map(this::readFile)
            .toList();
    }

    private AttachmentFile readFile(Attachment attachment) {
        Path path = storageDir.resolve(attachment.getStoragePath());
        if (!Files.exists(path)) {
            throw new NotFoundException("File not found on disk");
        }
        try {
            return new AttachmentFile(
                attachment.getFilename(),
                attachment.getMimeType(),
                Files.readAllBytes(path)
            );
        } catch (IOException e) {
            throw new ConflictException("Could not read file");
        }
    }

    public record AttachmentFile(String filename, String mimeType, byte[] content) {
    }
}

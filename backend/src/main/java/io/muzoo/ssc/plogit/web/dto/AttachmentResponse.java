package io.muzoo.ssc.plogit.web.dto;

import io.muzoo.ssc.plogit.domain.Attachment;

import java.time.Instant;
import java.util.UUID;

public record AttachmentResponse(
    Long id,
    UUID logId,
    String filename,
    String mimeType,
    Long size,
    Long uploadedById,
    Instant uploadedAt
) {
    public static AttachmentResponse from(Attachment attachment) {
        return new AttachmentResponse(
            attachment.getId(),
            attachment.getLog().getId(),
            attachment.getFilename(),
            attachment.getMimeType(),
            attachment.getSize(),
            attachment.getUploadedById(),
            attachment.getUploadedAt()
        );
    }
}

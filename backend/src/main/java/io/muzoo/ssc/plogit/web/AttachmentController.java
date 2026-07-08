package io.muzoo.ssc.plogit.web;

import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.security.CurrentUser;
import io.muzoo.ssc.plogit.service.AttachmentService;
import io.muzoo.ssc.plogit.web.dto.AttachmentResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/api/logs/{logId}/attachments")
    public ResponseEntity<AttachmentResponse> upload(
        @PathVariable UUID logId,
        @RequestParam("file") MultipartFile file,
        @CurrentUser User currentUser
    ) {
        return ResponseEntity.ok(attachmentService.upload(logId, file, currentUser));
    }

    @GetMapping("/api/logs/{logId}/attachments")
    public ResponseEntity<List<AttachmentResponse>> list(
        @PathVariable UUID logId,
        @CurrentUser User currentUser
    ) {
        return ResponseEntity.ok(attachmentService.listForLog(logId, currentUser));
    }

    @GetMapping("/api/logs/{logId}/attachments/{attachmentId}")
    public ResponseEntity<ByteArrayResource> download(
        @PathVariable UUID logId,
        @PathVariable Long attachmentId,
        @CurrentUser User currentUser
    ) {
        AttachmentService.AttachmentFile file = attachmentService.loadFile(logId, attachmentId, currentUser);

        ContentDisposition disposition = ContentDisposition.inline()
            .filename(file.filename())
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(disposition);
        headers.setContentType(MediaType.parseMediaType(file.mimeType()));

        ByteArrayResource resource = new ByteArrayResource(file.content());
        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(file.content().length)
            .body(resource);
    }
}

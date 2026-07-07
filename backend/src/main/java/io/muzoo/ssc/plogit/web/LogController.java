package io.muzoo.ssc.plogit.web;

import io.muzoo.ssc.plogit.domain.ActivityType;
import io.muzoo.ssc.plogit.domain.Outcome;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.security.CurrentUser;
import io.muzoo.ssc.plogit.service.LogService;
import io.muzoo.ssc.plogit.web.dto.LogCreateRequest;
import io.muzoo.ssc.plogit.web.dto.LogDetail;
import io.muzoo.ssc.plogit.web.dto.LogFilter;
import io.muzoo.ssc.plogit.web.dto.LogSummary;
import io.muzoo.ssc.plogit.web.dto.LogUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping("/api/engagements/{engagementId}/logs")
    public ResponseEntity<LogDetail> create(
        @PathVariable Long engagementId,
        @Valid @RequestBody LogCreateRequest request,
        @CurrentUser User currentUser
    ) {
        return ResponseEntity.ok(logService.create(engagementId, request, currentUser));
    }

    @GetMapping("/api/engagements/{engagementId}/logs")
    public ResponseEntity<Map<String, Object>> list(
        @PathVariable Long engagementId,
        @RequestParam(required = false) Long authorId,
        @RequestParam(required = false) ActivityType activityType,
        @RequestParam(required = false) Outcome outcome,
        @RequestParam(required = false) ReviewState reviewState,
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @CurrentUser User currentUser
    ) {
        LogFilter filter = new LogFilter(authorId, activityType, outcome, reviewState, search);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<LogSummary> result = logService.list(engagementId, currentUser, filter, pageable);

        return ResponseEntity.ok(Map.of(
            "items", result.getContent(),
            "page", result.getNumber(),
            "size", result.getSize(),
            "totalElements", result.getTotalElements(),
            "totalPages", result.getTotalPages()
        ));
    }

    @GetMapping("/api/logs/{logId}")
    public ResponseEntity<LogDetail> get(
        @PathVariable UUID logId,
        @CurrentUser User currentUser
    ) {
        return ResponseEntity.ok(logService.findDetailByIdAndAssertMember(logId, currentUser));
    }

    @PutMapping("/api/logs/{logId}")
    public ResponseEntity<LogDetail> update(
        @PathVariable UUID logId,
        @Valid @RequestBody LogUpdateRequest request,
        @CurrentUser User currentUser
    ) {
        return ResponseEntity.ok(logService.update(logId, request, currentUser));
    }
}

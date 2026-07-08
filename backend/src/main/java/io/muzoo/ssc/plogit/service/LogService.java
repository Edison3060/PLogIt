package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.LogEntryRepository;
import io.muzoo.ssc.plogit.repository.LogSpecifications;
import io.muzoo.ssc.plogit.web.dto.LogCreateRequest;
import io.muzoo.ssc.plogit.web.dto.LogDetail;
import io.muzoo.ssc.plogit.web.dto.LogFilter;
import io.muzoo.ssc.plogit.web.dto.LogSummary;
import io.muzoo.ssc.plogit.web.dto.LogUpdateRequest;
import io.muzoo.ssc.plogit.web.dto.LogVersionSummary;
import io.muzoo.ssc.plogit.web.exception.ConflictException;
import io.muzoo.ssc.plogit.web.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LogService {

    private final LogEntryRepository logRepository;
    private final EngagementService engagementService;
    private final MembershipService membershipService;
    private final LogVersionService versionService;

    public LogService(
        LogEntryRepository logRepository,
        EngagementService engagementService,
        MembershipService membershipService,
        LogVersionService versionService
    ) {
        this.logRepository = logRepository;
        this.engagementService = engagementService;
        this.membershipService = membershipService;
        this.versionService = versionService;
    }

    public LogDetail create(Long engagementId, LogCreateRequest request, User author) {
        Engagement engagement = engagementService.findByIdAndAssertMember(engagementId, author);

        LogEntry log = LogEntry.builder()
            .engagement(engagement)
            .author(author)
            .activityType(request.activityType())
            .title(request.title())
            .description(request.description())
            .result(request.result())
            .target(request.target())
            .toolUsed(request.toolUsed())
            .outcome(request.outcome())
            .tags(request.tags())
            .codeBlock(request.codeBlock())
            .codeLanguage(request.codeLanguage())
            .reviewState(ReviewState.DRAFT)
            .lastEditedById(author.getId())
            .build();

        LogEntry saved = logRepository.save(log);
        versionService.snapshot(saved, author.getId());
        return LogDetail.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<LogSummary> list(Long engagementId, User viewer, LogFilter filter, Pageable pageable) {
        Engagement engagement = engagementService.findByIdAndAssertMember(engagementId, viewer);

        Specification<LogEntry> spec = LogSpecifications.forEngagement(engagement);
        if (filter != null) {
            if (filter.authorId() != null) {
                spec = spec.and(LogSpecifications.hasAuthor(filter.authorId()));
            }
            if (filter.activityType() != null) {
                spec = spec.and(LogSpecifications.hasActivityType(filter.activityType()));
            }
            if (filter.outcome() != null) {
                spec = spec.and(LogSpecifications.hasOutcome(filter.outcome()));
            }
            if (filter.reviewState() != null) {
                spec = spec.and(LogSpecifications.hasReviewState(filter.reviewState()));
            }
            if (filter.search() != null && !filter.search().isBlank()) {
                spec = spec.and(LogSpecifications.textContains(filter.search()));
            }
        }

        return logRepository.findAll(spec, pageable).map(LogSummary::from);
    }

    @Transactional(readOnly = true)
    public LogDetail findDetailByIdAndAssertMember(UUID logId, User viewer) {
        LogEntry log = logRepository.findById(logId)
            .orElseThrow(() -> new NotFoundException("Log not found"));
        membershipService.assertMember(log.getEngagement(), viewer);
        return LogDetail.from(log);
    }

    @Transactional(readOnly = true)
    public List<LogVersionSummary> historyFor(UUID logId, User viewer) {
        LogEntry log = logRepository.findById(logId)
            .orElseThrow(() -> new NotFoundException("Log not found"));
        membershipService.assertMember(log.getEngagement(), viewer);
        return versionService.historyFor(log);
    }

    public LogDetail update(UUID logId, LogUpdateRequest request, User editor) {
        LogEntry log = logRepository.findById(logId)
            .orElseThrow(() -> new NotFoundException("Log not found"));

        membershipService.assertMember(log.getEngagement(), editor);

        boolean isLeader = membershipService.isLeader(log.getEngagement(), editor);
        boolean isAuthor = log.getAuthor().getId().equals(editor.getId());

        if (!isLeader && !isAuthor) {
            throw new ConflictException("You can only edit your own logs");
        }

        if (!isLeader && log.getReviewState() != ReviewState.DRAFT) {
            throw new ConflictException("Only draft logs can be edited");
        }

        if (request.activityType() != null) {
            log.setActivityType(request.activityType());
        }
        if (request.title() != null) {
            log.setTitle(request.title());
        }
        if (request.description() != null) {
            log.setDescription(request.description());
        }
        if (request.result() != null) {
            log.setResult(request.result());
        }
        if (request.target() != null) {
            log.setTarget(request.target());
        }
        if (request.toolUsed() != null) {
            log.setToolUsed(request.toolUsed());
        }
        if (request.outcome() != null) {
            log.setOutcome(request.outcome());
        }
        if (request.tags() != null) {
            log.setTags(request.tags());
        }
        if (request.codeBlock() != null) {
            log.setCodeBlock(request.codeBlock());
        }
        if (request.codeLanguage() != null) {
            log.setCodeLanguage(request.codeLanguage());
        }
        log.setLastEditedById(editor.getId());

        LogEntry saved = logRepository.save(log);
        versionService.snapshot(saved, editor.getId());
        return LogDetail.from(saved);
    }
}

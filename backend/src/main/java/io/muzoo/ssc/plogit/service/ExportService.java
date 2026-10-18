package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementAuditEvent;
import io.muzoo.ssc.plogit.domain.EngagementMember;
import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.ReviewAction;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.EngagementRepository;
import io.muzoo.ssc.plogit.repository.LogEntryRepository;
import io.muzoo.ssc.plogit.service.export.EngagementReportData;
import io.muzoo.ssc.plogit.service.export.ReportFactory;
import io.muzoo.ssc.plogit.service.export.ReportOutput;
import io.muzoo.ssc.plogit.service.export.ReportStrategy;
import io.muzoo.ssc.plogit.web.dto.TransitionRequest;
import io.muzoo.ssc.plogit.web.exception.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ExportService {

    private final EngagementRepository engagementRepository;
    private final EngagementService engagementService;
    private final MembershipService membershipService;
    private final LogEntryRepository logRepository;
    private final AttachmentService attachmentService;
    private final MarkdownService markdownService;
    private final LogReviewService logReviewService;
    private final ReportFactory reportFactory;
    private final ApplicationEventPublisher eventPublisher;

    public ExportService(
        EngagementRepository engagementRepository,
        EngagementService engagementService,
        MembershipService membershipService,
        LogEntryRepository logRepository,
        AttachmentService attachmentService,
        MarkdownService markdownService,
        LogReviewService logReviewService,
        ReportFactory reportFactory,
        ApplicationEventPublisher eventPublisher
    ) {
        this.engagementRepository = engagementRepository;
        this.engagementService = engagementService;
        this.membershipService = membershipService;
        this.logRepository = logRepository;
        this.attachmentService = attachmentService;
        this.markdownService = markdownService;
        this.logReviewService = logReviewService;
        this.reportFactory = reportFactory;
        this.eventPublisher = eventPublisher;
    }

    public ReportOutput export(Long engagementId, io.muzoo.ssc.plogit.web.dto.ExportRequest request, User leader) {
        Engagement engagement = engagementRepository.findById(engagementId)
            .orElseThrow(() -> new NotFoundException("Engagement not found"));
        membershipService.assertLeader(engagement, leader);

        List<LogEntry> logsToInclude = findIncludedLogs(engagement, Boolean.TRUE.equals(request.includeExported()));
        long totalLogCount = logRepository.countByEngagement(engagement);

        EngagementReportData data = buildReportData(engagement, logsToInclude, totalLogCount);

        ReportStrategy strategy = reportFactory.forFormat(request.format());
        ReportOutput output = strategy.generate(data);

        markExported(logsToInclude, leader);
        eventPublisher.publishEvent(new EngagementAuditEvent(
            leader.getId(),
            engagementId,
            "EXPORT_ENGAGEMENT",
            "ENGAGEMENT",
            String.valueOf(engagementId),
            Map.of(
                "format", request.format().name(),
                "logCount", logsToInclude.size(),
                "includeExported", Boolean.TRUE.equals(request.includeExported())
            )
        ));

        return output;
    }

    private List<LogEntry> findIncludedLogs(Engagement engagement, boolean includeExported) {
        List<ReviewState> states = new ArrayList<>();
        states.add(ReviewState.APPROVED);
        if (includeExported) {
            states.add(ReviewState.EXPORTED);
        }
        return logRepository.findByEngagementAndReviewStateInOrderByCreatedAtAsc(engagement, states);
    }

    private EngagementReportData buildReportData(Engagement engagement, List<LogEntry> logs, long totalLogCount) {
        List<EngagementReportData.LogReportEntry> logEntries = new ArrayList<>();
        for (LogEntry log : logs) {
            List<AttachmentService.AttachmentFile> images = attachmentService.loadImagesForLog(log);
            List<EngagementReportData.ImageAttachment> imageAttachments = images.stream()
                .map(img -> new EngagementReportData.ImageAttachment(img.filename(), img.mimeType(), img.content()))
                .toList();
            logEntries.add(new EngagementReportData.LogReportEntry(
                log.getId().toString(),
                log.getCreatedAt(),
                log.getAuthor().getDisplayName(),
                log.getActivityType().name(),
                log.getTitle(),
                log.getDescription(),
                markdownService.render(log.getDescription()),
                log.getResult(),
                markdownService.render(log.getResult()),
                log.getTarget(),
                log.getToolUsed(),
                log.getOutcome().name(),
                log.getReviewState().name(),
                log.getTags(),
                log.getCodeBlock(),
                log.getCodeLanguage(),
                imageAttachments
            ));
        }

        List<EngagementReportData.MemberInfo> members = engagementService.getMembers(engagement).stream()
            .filter(m -> m.getRemovedAt() == null)
            .map(m -> new EngagementReportData.MemberInfo(
                m.getUser().getEmail(),
                m.getUser().getDisplayName(),
                m.getRole().name()
            ))
            .toList();

        return new EngagementReportData(
            engagement.getId(),
            engagement.getName(),
            engagement.getDescription(),
            engagement.getStartDate(),
            engagement.getDueDate(),
            engagement.getLeader().getDisplayName(),
            members,
            engagement.getInScopeTargets(),
            engagement.getAllowedHours(),
            engagement.getAllowedTechniques(),
            engagement.getForbiddenTechniques(),
            engagement.getEmergencyContacts(),
            engagement.getOutOfScope(),
            Instant.now(),
            totalLogCount,
            logEntries
        );
    }

    private void markExported(List<LogEntry> logs, User leader) {
        TransitionRequest exportRequest = new TransitionRequest(ReviewAction.EXPORT, null);
        for (LogEntry log : logs) {
            logReviewService.transition(log.getId(), exportRequest, leader);
        }
    }
}

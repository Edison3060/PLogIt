package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.LogTransitionedEvent;
import io.muzoo.ssc.plogit.domain.ReviewAction;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.domain.review.ReviewStateBehavior;
import io.muzoo.ssc.plogit.domain.review.ReviewStates;
import io.muzoo.ssc.plogit.domain.review.TransitionContext;
import io.muzoo.ssc.plogit.repository.LogEntryRepository;
import io.muzoo.ssc.plogit.web.dto.LogDetail;
import io.muzoo.ssc.plogit.web.dto.TransitionRequest;
import io.muzoo.ssc.plogit.web.exception.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class LogReviewService {

    private final LogEntryRepository logRepository;
    private final MembershipService membershipService;
    private final ApplicationEventPublisher eventPublisher;
    private final MarkdownService markdownService;

    public LogReviewService(
        LogEntryRepository logRepository,
        MembershipService membershipService,
        ApplicationEventPublisher eventPublisher,
        MarkdownService markdownService
    ) {
        this.logRepository = logRepository;
        this.membershipService = membershipService;
        this.eventPublisher = eventPublisher;
        this.markdownService = markdownService;
    }

    public LogDetail transition(UUID logId, TransitionRequest request, User actor) {
        LogEntry log = logRepository.findById(logId)
            .orElseThrow(() -> new NotFoundException("Log not found"));

        membershipService.assertMember(log.getEngagement(), actor);

        boolean isLeader = membershipService.isLeader(log.getEngagement(), actor);
        boolean isAuthor = log.getAuthor().getId().equals(actor.getId());

        TransitionContext ctx = new TransitionContext(isAuthor, isLeader, request.comment());
        ReviewStateBehavior behavior = ReviewStates.of(log.getReviewState());
        ReviewState fromState = log.getReviewState();
        ReviewState next = behavior.transition(request.action(), ctx);

        applySideEffects(log, request.action(), next, actor, request.comment());
        log.setReviewState(next);
        LogEntry saved = logRepository.save(log);

        eventPublisher.publishEvent(new LogTransitionedEvent(
            actor.getId(),
            saved.getId(),
            saved.getEngagement().getId(),
            request.action().name(),
            fromState.name(),
            next.name(),
            request.comment()
        ));

        return LogDetail.from(saved, markdownService);
    }

    private void applySideEffects(LogEntry log, ReviewAction action, ReviewState next, User actor, String comment) {
        if (next == ReviewState.DRAFT && action == ReviewAction.REJECT) {
            log.setRejectionComment(comment);
            log.setRejectedAt(Instant.now());
            log.setRejectedById(actor.getId());
            return;
        }
        if (next == ReviewState.SUBMITTED || next == ReviewState.APPROVED) {
            log.setRejectionComment(null);
            log.setRejectedAt(null);
            log.setRejectedById(null);
        }
    }
}

package io.muzoo.ssc.plogit.domain.review;

import io.muzoo.ssc.plogit.domain.ReviewAction;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.web.exception.ConflictException;
import io.muzoo.ssc.plogit.web.exception.ForbiddenException;

public final class SubmittedState implements ReviewStateBehavior {

    @Override
    public ReviewState transition(ReviewAction action, TransitionContext ctx) {
        if (action == ReviewAction.SUBMIT) {
            throw new ConflictException("Log is already submitted");
        }
        if (!ctx.isLeader()) {
            throw new ForbiddenException("Only the leader can approve or reject a submitted log");
        }
        if (action == ReviewAction.APPROVE) {
            return ReviewState.APPROVED;
        }
        if (action == ReviewAction.REJECT) {
            if (ctx.comment() == null || ctx.comment().isBlank()) {
                throw new ConflictException("A rejection comment is required");
            }
            return ReviewState.DRAFT;
        }
        throw new ConflictException("Unknown action: " + action);
    }
}

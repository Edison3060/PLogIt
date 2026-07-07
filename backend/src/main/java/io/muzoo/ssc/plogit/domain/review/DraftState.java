package io.muzoo.ssc.plogit.domain.review;

import io.muzoo.ssc.plogit.domain.ReviewAction;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.web.exception.ConflictException;

public final class DraftState implements ReviewStateBehavior {

    @Override
    public ReviewState transition(ReviewAction action, TransitionContext ctx) {
        if (action != ReviewAction.SUBMIT) {
            throw new ConflictException("Only SUBMIT is allowed from DRAFT");
        }
        if (!ctx.isAuthor() && !ctx.isLeader()) {
            throw new ConflictException("Only the author or leader can submit a draft");
        }
        return ReviewState.SUBMITTED;
    }
}

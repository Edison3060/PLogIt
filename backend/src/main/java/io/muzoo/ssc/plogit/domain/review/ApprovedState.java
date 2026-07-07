package io.muzoo.ssc.plogit.domain.review;

import io.muzoo.ssc.plogit.domain.ReviewAction;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.web.exception.ConflictException;

public final class ApprovedState implements ReviewStateBehavior {

    @Override
    public ReviewState transition(ReviewAction action, TransitionContext ctx) {
        throw new ConflictException("Approved logs cannot be transitioned further (export arrives in a later slice)");
    }
}

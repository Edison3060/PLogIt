package io.muzoo.ssc.plogit.domain.review;

import io.muzoo.ssc.plogit.domain.ReviewAction;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.web.exception.ConflictException;
import io.muzoo.ssc.plogit.web.exception.ForbiddenException;

public final class ApprovedState implements ReviewStateBehavior {

    @Override
    public ReviewState transition(ReviewAction action, TransitionContext ctx) {
        if (action == ReviewAction.EXPORT) {
            if (!ctx.isLeader()) {
                throw new ForbiddenException("Only the leader can export an approved log");
            }
            return ReviewState.EXPORTED;
        }
        throw new ConflictException("Approved logs can only be exported");
    }
}

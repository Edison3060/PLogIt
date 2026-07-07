package io.muzoo.ssc.plogit.domain.review;

import io.muzoo.ssc.plogit.domain.ReviewAction;
import io.muzoo.ssc.plogit.domain.ReviewState;

public interface ReviewStateBehavior {

    ReviewState transition(ReviewAction action, TransitionContext ctx);
}

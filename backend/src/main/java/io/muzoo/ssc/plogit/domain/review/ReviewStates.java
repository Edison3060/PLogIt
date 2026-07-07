package io.muzoo.ssc.plogit.domain.review;

import io.muzoo.ssc.plogit.domain.ReviewState;

import java.util.EnumMap;
import java.util.Map;

public final class ReviewStates {

    private static final Map<ReviewState, ReviewStateBehavior> BEHAVIORS = new EnumMap<>(ReviewState.class);

    static {
        BEHAVIORS.put(ReviewState.DRAFT, new DraftState());
        BEHAVIORS.put(ReviewState.SUBMITTED, new SubmittedState());
        BEHAVIORS.put(ReviewState.APPROVED, new ApprovedState());
        BEHAVIORS.put(ReviewState.EXPORTED, new ExportedState());
    }

    private ReviewStates() {
    }

    public static ReviewStateBehavior of(ReviewState state) {
        ReviewStateBehavior behavior = BEHAVIORS.get(state);
        if (behavior == null) {
            throw new IllegalStateException("No behavior registered for state: " + state);
        }
        return behavior;
    }
}

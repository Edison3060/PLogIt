package io.muzoo.ssc.plogit.domain.review;

public record TransitionContext(boolean isAuthor, boolean isLeader, String comment) {
}

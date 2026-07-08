package io.muzoo.ssc.plogit.domain;

import java.util.UUID;

public class LogTransitionedEvent {

    private final Long actorId;
    private final UUID logId;
    private final Long engagementId;
    private final String action;
    private final String fromState;
    private final String toState;
    private final String comment;

    public LogTransitionedEvent(
        Long actorId,
        UUID logId,
        Long engagementId,
        String action,
        String fromState,
        String toState,
        String comment
    ) {
        this.actorId = actorId;
        this.logId = logId;
        this.engagementId = engagementId;
        this.action = action;
        this.fromState = fromState;
        this.toState = toState;
        this.comment = comment;
    }

    public Long getActorId() {
        return actorId;
    }

    public UUID getLogId() {
        return logId;
    }

    public Long getEngagementId() {
        return engagementId;
    }

    public String getAction() {
        return action;
    }

    public String getFromState() {
        return fromState;
    }

    public String getToState() {
        return toState;
    }

    public String getComment() {
        return comment;
    }
}

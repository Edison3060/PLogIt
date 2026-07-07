package io.muzoo.ssc.plogit.repository;

import io.muzoo.ssc.plogit.domain.ActivityType;
import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.Outcome;
import io.muzoo.ssc.plogit.domain.ReviewState;
import org.springframework.data.jpa.domain.Specification;

public final class LogSpecifications {

    private LogSpecifications() {
    }

    public static Specification<LogEntry> forEngagement(Engagement engagement) {
        return (root, query, cb) -> cb.equal(root.get("engagement"), engagement);
    }

    public static Specification<LogEntry> hasAuthor(Long authorId) {
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<LogEntry> hasActivityType(ActivityType type) {
        return (root, query, cb) -> cb.equal(root.get("activityType"), type);
    }

    public static Specification<LogEntry> hasOutcome(Outcome outcome) {
        return (root, query, cb) -> cb.equal(root.get("outcome"), outcome);
    }

    public static Specification<LogEntry> hasReviewState(ReviewState state) {
        return (root, query, cb) -> cb.equal(root.get("reviewState"), state);
    }

    public static Specification<LogEntry> textContains(String text) {
        String pattern = "%" + text.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
            cb.like(cb.lower(root.get("title")), pattern),
            cb.like(cb.lower(root.get("description")), pattern)
        );
    }
}

package io.muzoo.ssc.plogit.web.dto;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record EngagementDetail(
    Long id,
    String name,
    String description,
    LocalDate startDate,
    LocalDate dueDate,
    EngagementStatus status,
    String role,
    LeaderInfo leader,
    List<MemberInfo> members,
    List<String> inScopeTargets,
    String allowedHours,
    String allowedTechniques,
    String forbiddenTechniques,
    String emergencyContacts,
    String outOfScope,
    Instant createdAt
) {
    public record LeaderInfo(Long id, String email, String displayName) {
    }

    public record MemberInfo(Long id, String email, String displayName, String role) {
    }

    public static EngagementDetail from(Engagement engagement, String currentUserRole, List<MemberInfo> members) {
        return new EngagementDetail(
            engagement.getId(),
            engagement.getName(),
            engagement.getDescription(),
            engagement.getStartDate(),
            engagement.getDueDate(),
            engagement.getStatus(),
            currentUserRole,
            new LeaderInfo(
                engagement.getLeader().getId(),
                engagement.getLeader().getEmail(),
                engagement.getLeader().getDisplayName()
            ),
            members,
            engagement.getInScopeTargets(),
            engagement.getAllowedHours(),
            engagement.getAllowedTechniques(),
            engagement.getForbiddenTechniques(),
            engagement.getEmergencyContacts(),
            engagement.getOutOfScope(),
            engagement.getCreatedAt()
        );
    }
}

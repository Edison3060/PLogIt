package io.muzoo.ssc.plogit.web;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementMember;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.security.CurrentUser;
import io.muzoo.ssc.plogit.service.EngagementService;
import io.muzoo.ssc.plogit.service.MembershipService;
import io.muzoo.ssc.plogit.web.dto.CreateEngagementRequest;
import io.muzoo.ssc.plogit.web.dto.EngagementDetail;
import io.muzoo.ssc.plogit.web.dto.EngagementSummary;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/engagements")
public class EngagementController {

    private final EngagementService engagementService;
    private final MembershipService membershipService;

    public EngagementController(EngagementService engagementService, MembershipService membershipService) {
        this.engagementService = engagementService;
        this.membershipService = membershipService;
    }

    @GetMapping
    public ResponseEntity<List<EngagementSummary>> listMine(@CurrentUser User currentUser) {
        List<EngagementMember> memberships = engagementService.getMembershipsForUser(currentUser);

        List<EngagementSummary> summaries = memberships.stream()
            .filter(m -> m.getRemovedAt() == null)
            .map(m -> EngagementSummary.from(m.getEngagement(), m.getRole().name()))
            .toList();

        return ResponseEntity.ok(summaries);
    }

    @PostMapping
    public ResponseEntity<EngagementSummary> create(
        @Valid @RequestBody CreateEngagementRequest request,
        @CurrentUser User currentUser
    ) {
        Engagement engagement = engagementService.create(request, currentUser);
        return ResponseEntity.ok(
            EngagementSummary.from(engagement, "LEADER")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EngagementDetail> get(
        @PathVariable Long id,
        @CurrentUser User currentUser
    ) {
        Engagement engagement = engagementService.findByIdAndAssertMember(id, currentUser);
        String role = membershipService.getRole(engagement, currentUser).name();

        List<EngagementDetail.MemberInfo> members = engagementService.getMembers(engagement).stream()
            .filter(m -> m.getRemovedAt() == null)
            .map(m -> new EngagementDetail.MemberInfo(
                m.getUser().getId(),
                m.getUser().getEmail(),
                m.getUser().getDisplayName(),
                m.getRole().name()
            ))
            .toList();

        return ResponseEntity.ok(EngagementDetail.from(engagement, role, members));
    }
}

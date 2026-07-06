package io.muzoo.ssc.plogit.web;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.EngagementRepository;
import io.muzoo.ssc.plogit.security.CurrentUser;
import io.muzoo.ssc.plogit.service.EngagementService;
import io.muzoo.ssc.plogit.service.JoinCodeService;
import io.muzoo.ssc.plogit.service.MembershipService;
import io.muzoo.ssc.plogit.web.dto.EngagementSummary;
import io.muzoo.ssc.plogit.web.dto.JoinCodeResponse;
import io.muzoo.ssc.plogit.web.dto.JoinRequest;
import io.muzoo.ssc.plogit.web.exception.ConflictException;
import io.muzoo.ssc.plogit.web.exception.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class JoinController {

    private final JoinCodeService joinCodeService;
    private final MembershipService membershipService;
    private final EngagementService engagementService;
    private final EngagementRepository engagementRepository;

    public JoinController(
        JoinCodeService joinCodeService,
        MembershipService membershipService,
        EngagementService engagementService,
        EngagementRepository engagementRepository
    ) {
        this.joinCodeService = joinCodeService;
        this.membershipService = membershipService;
        this.engagementService = engagementService;
        this.engagementRepository = engagementRepository;
    }

    @PostMapping("/join")
    public ResponseEntity<EngagementSummary> join(
        @Valid @RequestBody JoinRequest request,
        @CurrentUser User currentUser
    ) {
        Engagement engagement = joinCodeService.resolveCode(request.code());

        if (membershipService.isMember(engagement, currentUser)) {
            throw new ConflictException("You are already a member of this engagement");
        }

        membershipService.addMember(engagement, currentUser, EngagementRole.MEMBER, "code");

        engagementRepository.save(engagement);

        return ResponseEntity.ok(
            EngagementSummary.from(engagement, "MEMBER")
        );
    }

    @PostMapping("/engagements/{id}/join-code")
    public ResponseEntity<JoinCodeResponse> generateJoinCode(
        @PathVariable Long id,
        @CurrentUser User currentUser
    ) {
        Engagement engagement = engagementService.findByIdAndAssertMember(id, currentUser);
        membershipService.assertLeader(engagement, currentUser);

        String code = joinCodeService.generateFor(engagement, currentUser);
        engagementRepository.save(engagement);

        return ResponseEntity.ok(new JoinCodeResponse(code, engagement.getId()));
    }
}

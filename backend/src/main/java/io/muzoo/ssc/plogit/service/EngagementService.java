package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementMember;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.EngagementStatus;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.EngagementMemberRepository;
import io.muzoo.ssc.plogit.repository.EngagementRepository;
import io.muzoo.ssc.plogit.web.dto.CreateEngagementRequest;
import io.muzoo.ssc.plogit.web.dto.UpdateEngagementRequest;
import io.muzoo.ssc.plogit.web.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EngagementService {

    private final EngagementRepository engagementRepository;
    private final EngagementMemberRepository memberRepository;
    private final MembershipService membershipService;

    public EngagementService(
        EngagementRepository engagementRepository,
        EngagementMemberRepository memberRepository,
        MembershipService membershipService
    ) {
        this.engagementRepository = engagementRepository;
        this.memberRepository = memberRepository;
        this.membershipService = membershipService;
    }

    public Engagement create(CreateEngagementRequest request, User creator) {
        Engagement engagement = Engagement.builder()
            .name(request.name())
            .description(request.description())
            .startDate(request.startDate())
            .dueDate(request.dueDate())
            .status(EngagementStatus.ACTIVE)
            .leader(creator)
            .build();
        engagement = engagementRepository.save(engagement);

        membershipService.addMember(engagement, creator, EngagementRole.LEADER, "created");

        return engagement;
    }

    @Transactional(readOnly = true)
    public List<EngagementMember> getMembershipsForUser(User user) {
        return memberRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Engagement findByIdAndAssertMember(Long engagementId, User user) {
        Engagement engagement = engagementRepository.findById(engagementId)
            .orElseThrow(() -> new NotFoundException("Engagement not found"));
        membershipService.assertMember(engagement, user);
        return engagement;
    }

    @Transactional(readOnly = true)
    public List<EngagementMember> getMembers(Engagement engagement) {
        return memberRepository.findByEngagement(engagement);
    }

    public Engagement updateEngagement(Engagement engagement, UpdateEngagementRequest request) {
        if (request.name() != null) {
            engagement.setName(request.name());
        }
        if (request.description() != null) {
            engagement.setDescription(request.description());
        }
        if (request.startDate() != null) {
            engagement.setStartDate(request.startDate());
        }
        if (request.dueDate() != null) {
            engagement.setDueDate(request.dueDate());
        }
        if (request.allowedHours() != null) {
            engagement.setAllowedHours(request.allowedHours());
        }
        if (request.allowedTechniques() != null) {
            engagement.setAllowedTechniques(request.allowedTechniques());
        }
        if (request.forbiddenTechniques() != null) {
            engagement.setForbiddenTechniques(request.forbiddenTechniques());
        }
        if (request.emergencyContacts() != null) {
            engagement.setEmergencyContacts(request.emergencyContacts());
        }
        if (request.outOfScope() != null) {
            engagement.setOutOfScope(request.outOfScope());
        }
        if (request.inScopeTargets() != null) {
            engagement.setInScopeTargets(request.inScopeTargets());
        }
        return engagementRepository.save(engagement);
    }

    public Engagement findByIdAndAssertLeader(Long engagementId, User user) {
        Engagement engagement = engagementRepository.findById(engagementId)
            .orElseThrow(() -> new NotFoundException("Engagement not found"));
        membershipService.assertLeader(engagement, user);
        return engagement;
    }
}

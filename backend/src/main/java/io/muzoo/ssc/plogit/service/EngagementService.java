package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementMember;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.EngagementStatus;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.EngagementMemberRepository;
import io.muzoo.ssc.plogit.repository.EngagementRepository;
import io.muzoo.ssc.plogit.web.dto.CreateEngagementRequest;
import io.muzoo.ssc.plogit.web.dto.EngagementDetail;
import io.muzoo.ssc.plogit.web.dto.EngagementSummary;
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
    private final JoinCodeService joinCodeService;

    public EngagementService(
        EngagementRepository engagementRepository,
        EngagementMemberRepository memberRepository,
        MembershipService membershipService,
        JoinCodeService joinCodeService
    ) {
        this.engagementRepository = engagementRepository;
        this.memberRepository = memberRepository;
        this.membershipService = membershipService;
        this.joinCodeService = joinCodeService;
    }

    @Transactional(readOnly = true)
    public List<EngagementDetail.MemberInfo> buildMemberInfos(Engagement engagement) {
        return getMembers(engagement).stream()
            .filter(m -> m.getRemovedAt() == null)
            .map(m -> new EngagementDetail.MemberInfo(
                m.getUser().getId(),
                m.getUser().getEmail(),
                m.getUser().getDisplayName(),
                m.getRole().name()
            ))
            .toList();
    }

    @Transactional(readOnly = true)
    public EngagementDetail getDetailForMember(Long engagementId, User viewer) {
        Engagement engagement = engagementRepository.findById(engagementId)
            .orElseThrow(() -> new NotFoundException("Engagement not found"));
        membershipService.assertMember(engagement, viewer);
        String role = membershipService.getRole(engagement, viewer).name();
        List<EngagementDetail.MemberInfo> members = buildMemberInfos(engagement);
        return EngagementDetail.from(engagement, role, members);
    }

    @Transactional
    public EngagementDetail updateAndBuildDetail(Long engagementId, User leader, UpdateEngagementRequest request) {
        Engagement engagement = engagementRepository.findById(engagementId)
            .orElseThrow(() -> new NotFoundException("Engagement not found"));
        membershipService.assertLeader(engagement, leader);
        updateEngagement(engagement, request);
        String role = membershipService.getRole(engagement, leader).name();
        List<EngagementDetail.MemberInfo> members = buildMemberInfos(engagement);
        return EngagementDetail.from(engagement, role, members);
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
    public List<EngagementSummary> listSummariesForUser(User user) {
        return memberRepository.findByUser(user).stream()
            .filter(m -> m.getRemovedAt() == null)
            .map(m -> EngagementSummary.from(m.getEngagement(), m.getRole().name()))
            .toList();
    }

    @Transactional
    public EngagementSummary joinByCode(String code, User user) {
        Engagement engagement = joinCodeService.resolveCode(code);
        if (membershipService.isMember(engagement, user)) {
            throw new io.muzoo.ssc.plogit.web.exception.ConflictException("You are already a member of this engagement");
        }
        membershipService.addMember(engagement, user, EngagementRole.MEMBER, "code");
        engagementRepository.save(engagement);
        return EngagementSummary.from(engagement, "MEMBER");
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

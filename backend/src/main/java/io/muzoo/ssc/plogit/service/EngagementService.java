package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementAuditEvent;
import io.muzoo.ssc.plogit.domain.EngagementMember;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.EngagementStatus;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.AuditLogRepository;
import io.muzoo.ssc.plogit.repository.EngagementMemberRepository;
import io.muzoo.ssc.plogit.repository.EngagementRepository;
import io.muzoo.ssc.plogit.web.dto.AuditLogEntry;
import io.muzoo.ssc.plogit.web.dto.CreateEngagementRequest;
import io.muzoo.ssc.plogit.web.dto.EngagementDetail;
import io.muzoo.ssc.plogit.web.dto.EngagementSummary;
import io.muzoo.ssc.plogit.web.dto.UpdateEngagementRequest;
import io.muzoo.ssc.plogit.web.exception.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class EngagementService {

    private final EngagementRepository engagementRepository;
    private final EngagementMemberRepository memberRepository;
    private final MembershipService membershipService;
    private final JoinCodeService joinCodeService;
    private final AuditLogRepository auditLogRepository;
    private final ApplicationEventPublisher eventPublisher;

    public EngagementService(
        EngagementRepository engagementRepository,
        EngagementMemberRepository memberRepository,
        MembershipService membershipService,
        JoinCodeService joinCodeService,
        AuditLogRepository auditLogRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.engagementRepository = engagementRepository;
        this.memberRepository = memberRepository;
        this.membershipService = membershipService;
        this.joinCodeService = joinCodeService;
        this.auditLogRepository = auditLogRepository;
        this.eventPublisher = eventPublisher;
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
        List<String> changedFields = changedFields(engagement, request);
        updateEngagement(engagement, request);
        if (!changedFields.isEmpty()) {
            eventPublisher.publishEvent(new EngagementAuditEvent(
                leader.getId(), engagementId, "UPDATE_ENGAGEMENT", "ENGAGEMENT",
                String.valueOf(engagementId), Map.of("changedFields", changedFields)
            ));
        }
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
        Optional<EngagementMember> existingMembership = memberRepository.findByEngagementAndUser(engagement, user);
        if (existingMembership.isPresent() && existingMembership.get().getRemovedAt() == null) {
            throw new io.muzoo.ssc.plogit.web.exception.ConflictException("You are already a member of this engagement");
        }
        if (existingMembership.isPresent()) {
            EngagementMember member = existingMembership.get();
            member.setRemovedAt(null);
            member.setJoinedVia("code");
            memberRepository.save(member);
        } else {
            membershipService.addMember(engagement, user, EngagementRole.MEMBER, "code");
        }
        engagementRepository.save(engagement);
        eventPublisher.publishEvent(new EngagementAuditEvent(
            user.getId(), engagement.getId(), "JOIN_ENGAGEMENT", "MEMBER",
            String.valueOf(user.getId()), Map.of("rejoined", existingMembership.isPresent())
        ));
        return EngagementSummary.from(engagement, "MEMBER");
    }

    @Transactional(readOnly = true)
    public List<AuditLogEntry> getAuditLogForLeader(Long engagementId, User viewer) {
        Engagement engagement = engagementRepository.findById(engagementId)
            .orElseThrow(() -> new NotFoundException("Engagement not found"));
        membershipService.assertLeader(engagement, viewer);
        return auditLogRepository.findByEngagementId(String.valueOf(engagementId), 200).stream()
            .map(AuditLogEntry::from)
            .toList();
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

    private List<String> changedFields(Engagement engagement, UpdateEngagementRequest request) {
        List<String> fields = new ArrayList<>();
        if (request.name() != null && !Objects.equals(request.name(), engagement.getName())) {
            fields.add("name");
        }
        if (request.description() != null && !Objects.equals(request.description(), engagement.getDescription())) {
            fields.add("description");
        }
        if (request.startDate() != null && !Objects.equals(request.startDate(), engagement.getStartDate())) {
            fields.add("startDate");
        }
        if (request.dueDate() != null && !Objects.equals(request.dueDate(), engagement.getDueDate())) {
            fields.add("dueDate");
        }
        if (request.allowedHours() != null && !Objects.equals(request.allowedHours(), engagement.getAllowedHours())) {
            fields.add("allowedHours");
        }
        if (request.allowedTechniques() != null && !Objects.equals(request.allowedTechniques(), engagement.getAllowedTechniques())) {
            fields.add("allowedTechniques");
        }
        if (request.forbiddenTechniques() != null && !Objects.equals(request.forbiddenTechniques(), engagement.getForbiddenTechniques())) {
            fields.add("forbiddenTechniques");
        }
        if (request.emergencyContacts() != null && !Objects.equals(request.emergencyContacts(), engagement.getEmergencyContacts())) {
            fields.add("emergencyContacts");
        }
        if (request.outOfScope() != null && !Objects.equals(request.outOfScope(), engagement.getOutOfScope())) {
            fields.add("outOfScope");
        }
        if (request.inScopeTargets() != null && !Objects.equals(request.inScopeTargets(), engagement.getInScopeTargets())) {
            fields.add("inScopeTargets");
        }
        return fields;
    }

    public Engagement findByIdAndAssertLeader(Long engagementId, User user) {
        Engagement engagement = engagementRepository.findById(engagementId)
            .orElseThrow(() -> new NotFoundException("Engagement not found"));
        membershipService.assertLeader(engagement, user);
        return engagement;
    }
}

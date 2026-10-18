package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementAuditEvent;
import io.muzoo.ssc.plogit.domain.EngagementMember;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.EngagementMemberRepository;
import io.muzoo.ssc.plogit.repository.EngagementRepository;
import io.muzoo.ssc.plogit.repository.UserRepository;
import io.muzoo.ssc.plogit.web.exception.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
@Transactional
public class MembershipService {

    private final EngagementMemberRepository memberRepository;
    private final EngagementRepository engagementRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MembershipService(
        EngagementMemberRepository memberRepository,
        EngagementRepository engagementRepository,
        UserRepository userRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.memberRepository = memberRepository;
        this.engagementRepository = engagementRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public boolean isMember(Engagement engagement, User user) {
        return memberRepository.existsByEngagementAndUserAndRemovedAtIsNull(engagement, user);
    }

    @Transactional(readOnly = true)
    public boolean isLeader(Engagement engagement, User user) {
        return memberRepository.findByEngagementAndUserAndRemovedAtIsNull(engagement, user)
            .map(m -> m.getRole() == EngagementRole.LEADER)
            .orElse(false);
    }

    @Transactional(readOnly = true)
    public EngagementRole getRole(Engagement engagement, User user) {
        return memberRepository.findByEngagementAndUserAndRemovedAtIsNull(engagement, user)
            .map(EngagementMember::getRole)
            .orElseThrow(() -> new NotFoundException("You are not a member of this engagement"));
    }

    @Transactional(readOnly = true)
    public void assertMember(Engagement engagement, User user) {
        if (!isMember(engagement, user)) {
            throw new NotFoundException("Engagement not found");
        }
    }

    @Transactional(readOnly = true)
    public void assertLeader(Engagement engagement, User user) {
        if (!isLeader(engagement, user)) {
            throw new NotFoundException("Engagement not found");
        }
    }

    public EngagementMember addMember(Engagement engagement, User user, EngagementRole role, String joinedVia) {
        EngagementMember member = EngagementMember.builder()
            .engagement(engagement)
            .user(user)
            .role(role)
            .joinedVia(joinedVia)
            .build();
        return memberRepository.save(member);
    }

    public void removeMember(Engagement engagement, Long userIdToRemove, User removedBy) {
        assertLeader(engagement, removedBy);
        User userToRemove = userRepository.findById(userIdToRemove)
            .orElseThrow(() -> new NotFoundException("User not found"));
        EngagementMember member = memberRepository.findByEngagementAndUserAndRemovedAtIsNull(engagement, userToRemove)
            .orElseThrow(() -> new NotFoundException("Member not found"));
        if (member.getRole() == EngagementRole.LEADER) {
            throw new IllegalArgumentException("Cannot remove the leader. Transfer leadership first.");
        }
        member.setRemovedAt(Instant.now());
        memberRepository.save(member);
        eventPublisher.publishEvent(new EngagementAuditEvent(
            removedBy.getId(), engagement.getId(), "REMOVE_MEMBER", "MEMBER",
            String.valueOf(userIdToRemove), Map.of("memberId", userIdToRemove)
        ));
    }

    public void transferLeadership(Engagement engagement, User currentLeader, Long newLeaderUserId) {
        assertLeader(engagement, currentLeader);
        EngagementMember currentLeaderMember = memberRepository.findByEngagementAndUserAndRemovedAtIsNull(engagement, currentLeader)
            .orElseThrow(() -> new NotFoundException("Engagement not found"));

        User newLeaderUser = userRepository.findById(newLeaderUserId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        EngagementMember newLeaderMember = memberRepository.findByEngagementAndUserAndRemovedAtIsNull(engagement, newLeaderUser)
            .orElseThrow(() -> new NotFoundException("Member not found"));

        currentLeaderMember.setRole(EngagementRole.MEMBER);
        newLeaderMember.setRole(EngagementRole.LEADER);

        memberRepository.save(currentLeaderMember);
        memberRepository.save(newLeaderMember);

        engagement.setLeader(newLeaderUser);
        engagementRepository.save(engagement);
        eventPublisher.publishEvent(new EngagementAuditEvent(
            currentLeader.getId(), engagement.getId(), "TRANSFER_LEADERSHIP", "ENGAGEMENT",
            String.valueOf(engagement.getId()),
            Map.of("previousLeaderId", currentLeader.getId(), "newLeaderId", newLeaderUserId)
        ));
    }
}

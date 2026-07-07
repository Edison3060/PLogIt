package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementMember;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.EngagementMemberRepository;
import io.muzoo.ssc.plogit.repository.UserRepository;
import io.muzoo.ssc.plogit.web.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class MembershipService {

    private final EngagementMemberRepository memberRepository;
    private final UserRepository userRepository;

    public MembershipService(EngagementMemberRepository memberRepository, UserRepository userRepository) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public boolean isMember(Engagement engagement, User user) {
        return memberRepository.existsByEngagementAndUser(engagement, user);
    }

    @Transactional(readOnly = true)
    public boolean isLeader(Engagement engagement, User user) {
        return memberRepository.findByEngagementAndUser(engagement, user)
            .map(m -> m.getRole() == EngagementRole.LEADER)
            .orElse(false);
    }

    @Transactional(readOnly = true)
    public EngagementRole getRole(Engagement engagement, User user) {
        return memberRepository.findByEngagementAndUser(engagement, user)
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

    public void removeMember(Engagement engagement, Long userIdToRemove) {
        User userToRemove = userRepository.findById(userIdToRemove)
            .orElseThrow(() -> new NotFoundException("User not found"));
        EngagementMember member = memberRepository.findByEngagementAndUser(engagement, userToRemove)
            .orElseThrow(() -> new NotFoundException("Member not found"));
        if (member.getRole() == EngagementRole.LEADER) {
            throw new IllegalArgumentException("Cannot remove the leader. Transfer leadership first.");
        }
        member.setRemovedAt(Instant.now());
        memberRepository.save(member);
    }

    public void transferLeadership(Engagement engagement, User currentLeader, Long newLeaderUserId) {
        EngagementMember currentLeaderMember = memberRepository.findByEngagementAndUser(engagement, currentLeader)
            .orElseThrow(() -> new NotFoundException("Engagement not found"));

        User newLeaderUser = userRepository.findById(newLeaderUserId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        EngagementMember newLeaderMember = memberRepository.findByEngagementAndUser(engagement, newLeaderUser)
            .orElseThrow(() -> new NotFoundException("Member not found"));

        if (newLeaderMember.getRemovedAt() != null) {
            throw new IllegalArgumentException("Cannot transfer to a removed member");
        }

        currentLeaderMember.setRole(EngagementRole.MEMBER);
        newLeaderMember.setRole(EngagementRole.LEADER);

        memberRepository.save(currentLeaderMember);
        memberRepository.save(newLeaderMember);

        engagement.setLeader(newLeaderUser);
    }
}

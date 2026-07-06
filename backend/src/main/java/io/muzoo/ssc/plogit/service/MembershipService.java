package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementMember;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.EngagementMemberRepository;
import io.muzoo.ssc.plogit.web.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MembershipService {

    private final EngagementMemberRepository memberRepository;

    public MembershipService(EngagementMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
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
}

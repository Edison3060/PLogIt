package io.muzoo.ssc.plogit.repository;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementMember;
import io.muzoo.ssc.plogit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EngagementMemberRepository extends JpaRepository<EngagementMember, Long> {

    List<EngagementMember> findByEngagement(Engagement engagement);

    List<EngagementMember> findByUser(User user);

    Optional<EngagementMember> findByEngagementAndUser(Engagement engagement, User user);

    boolean existsByEngagementAndUser(Engagement engagement, User user);

    long countByEngagement(Engagement engagement);
}

package io.muzoo.ssc.plogit.support;

import io.muzoo.ssc.plogit.domain.ActivityType;
import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementMember;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.EngagementStatus;
import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.Outcome;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.EngagementMemberRepository;
import io.muzoo.ssc.plogit.repository.EngagementRepository;
import io.muzoo.ssc.plogit.repository.LogEntryRepository;
import io.muzoo.ssc.plogit.repository.UserRepository;
import io.muzoo.ssc.plogit.security.AuthenticatedUser;
import io.muzoo.ssc.plogit.security.PlogitUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected EngagementRepository engagementRepository;

    @Autowired
    protected EngagementMemberRepository memberRepository;

    @Autowired
    protected LogEntryRepository logRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected PlogitUserDetailsService userDetailsService;

    @BeforeEach
    void wipeData() {
        logRepository.deleteAll();
        memberRepository.deleteAll();
        engagementRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected User createUser(String email) {
        User user = User.builder()
            .email(email)
            .passwordHash(passwordEncoder.encode("password"))
            .displayName(email)
            .build();
        return userRepository.save(user);
    }

    protected Engagement createEngagement(User leader) {
        Engagement engagement = Engagement.builder()
            .name("Test Engagement")
            .description("test description")
            .status(EngagementStatus.ACTIVE)
            .leader(leader)
            .build();
        engagement = engagementRepository.save(engagement);
        addMember(engagement, leader, EngagementRole.LEADER);
        return engagement;
    }

    protected EngagementMember addMember(Engagement engagement, User user, EngagementRole role) {
        EngagementMember member = EngagementMember.builder()
            .engagement(engagement)
            .user(user)
            .role(role)
            .joinedVia("test")
            .build();
        return memberRepository.save(member);
    }

    protected LogEntry createLog(Engagement engagement, User author, ReviewState state) {
        LogEntry log = LogEntry.builder()
            .engagement(engagement)
            .author(author)
            .activityType(ActivityType.RECONNAISSANCE)
            .title("Test log")
            .description("desc")
            .result("result")
            .target("10.0.0.1")
            .outcome(Outcome.IN_PROGRESS)
            .reviewState(state)
            .lastEditedById(author.getId())
            .build();
        return logRepository.save(log);
    }

    protected AuthenticatedUser principalFor(User user) {
        return (AuthenticatedUser) userDetailsService.loadUserByUsername(user.getEmail());
    }

    protected org.springframework.test.web.servlet.request.RequestPostProcessor asUser(User user) {
        return org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(principalFor(user));
    }

    protected String logUri(UUID id) {
        return "/api/logs/" + id;
    }
}

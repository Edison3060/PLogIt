package io.muzoo.ssc.plogit.config;

import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class SeedDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${plogit.seed.user-email:leader@plogit.local}")
    private String seedEmail;

    @Value("${plogit.seed.user-password:change-me-leader}")
    private String seedPassword;

    @Value("${plogit.seed.user-name:Demo Leader}")
    private String seedName;

    public SeedDataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(seedEmail)) {
            return;
        }
        User user = User.builder()
            .email(seedEmail)
            .passwordHash(passwordEncoder.encode(seedPassword))
            .displayName(seedName)
            .build();
        userRepository.save(user);
    }
}

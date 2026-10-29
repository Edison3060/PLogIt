package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.UserRepository;
import io.muzoo.ssc.plogit.web.exception.ConflictException;
import io.muzoo.ssc.plogit.web.exception.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String password, String displayName) {
        // Keep password hashing work similar for new and already-registered emails.
        String passwordHash = passwordEncoder.encode(password);
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already registered");
        }
        User user = User.builder()
            .email(email)
            .passwordHash(passwordHash)
            .displayName(displayName)
            .build();
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("User not found"));
    }
}

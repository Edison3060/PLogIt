package io.muzoo.ssc.plogit.security;

import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PlogitUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public PlogitUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return new AuthenticatedUser(user);
    }
}

package io.muzoo.ssc.plogit.web;

import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.security.AuthenticatedUser;
import io.muzoo.ssc.plogit.security.CurrentUser;
import io.muzoo.ssc.plogit.service.UserService;
import io.muzoo.ssc.plogit.web.dto.ApiResponse;
import io.muzoo.ssc.plogit.web.dto.LoginRequest;
import io.muzoo.ssc.plogit.web.dto.RegisterRequest;
import io.muzoo.ssc.plogit.web.dto.UserResponse;
import io.muzoo.ssc.plogit.web.exception.ConflictException;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            userService.register(request.email(), request.password(), request.displayName());
        } catch (ConflictException ex) {
            // Do not disclose whether an email address is registered.
        } catch (DataIntegrityViolationException ex) {
            if (!userService.emailExists(request.email())) {
                throw ex;
            }
        }
        return ResponseEntity.accepted().body(ApiResponse.of("Registration request received. Please sign in"));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
        @Valid @RequestBody LoginRequest request,
        HttpServletRequest httpRequest
    ) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            request.email(),
            request.password()
        );
        Authentication authentication = authenticationManager.authenticate(token);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            securityContext
        );

        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.ok(UserResponse.from(principal.getUser()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.of("Logged out"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@CurrentUser User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(UserResponse.from(currentUser));
    }
}

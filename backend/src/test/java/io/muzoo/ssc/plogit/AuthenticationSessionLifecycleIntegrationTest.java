package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.support.IntegrationTestBase;
import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class AuthenticationSessionLifecycleIntegrationTest extends IntegrationTestBase {

    @Test
    void registrationLoginSessionRotationAndLogoutAreEnforced() throws Exception {
        String email = "session05-auth@test.local";
        String password = "Session05Password123!";
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password
                    + "\",\"displayName\":\"Session 05\"}"))
            .andExpect(status().isAccepted());

        User user = userRepository.findByEmail(email).orElseThrow();
        assertNotEquals(password, user.getPasswordHash());
        assertTrue(passwordEncoder.matches(password, user.getPasswordHash()));

        MockHttpSession preLoginSession = new MockHttpSession();
        String preLoginId = preLoginSession.getId();
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                .session(preLoginSession)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(email))
            .andReturn();

        MockHttpSession activeSession = (MockHttpSession) login.getRequest().getSession(false);
        assertNotNull(activeSession);
        assertNotEquals(preLoginId, activeSession.getId());
        String authenticatedId = activeSession.getId();
        mockMvc.perform(get("/api/auth/me").session(activeSession))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(email));

        mockMvc.perform(post("/api/auth/logout").session(activeSession).with(csrf()))
            .andExpect(status().isOk());
        assertTrue(activeSession.isInvalid());
        mockMvc.perform(get("/api/auth/me").cookie(new Cookie("JSESSIONID", authenticatedId)))
            .andExpect(status().isForbidden());
    }

    @Test
    void invalidCredentialsNeverCreateAuthenticatedContext() throws Exception {
        createUser("session05-invalid@test.local");
        MockHttpSession anonymousSession = new MockHttpSession();
        mockMvc.perform(post("/api/auth/login")
                .session(anonymousSession)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"session05-invalid@test.local\",\"password\":\"incorrect\"}"))
            .andExpect(status().isUnauthorized());
        org.junit.jupiter.api.Assertions.assertNull(anonymousSession.getAttribute(
            org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
    }

    @Test
    void staleSessionCookieCannotAccessAuthenticatedRoute() throws Exception {
        createUser("session05-stale@test.local");
        MockHttpSession staleSession = new MockHttpSession();
        String expiredSessionId = staleSession.getId();
        staleSession.invalidate();
        assertTrue(staleSession.isInvalid());
        mockMvc.perform(get("/api/auth/me").cookie(new Cookie("JSESSIONID", expiredSessionId)))
            .andExpect(status().isForbidden());
    }
}

package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.support.IntegrationTestBase;
import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class AuthCsrfIntegrationTest extends IntegrationTestBase {

    @Test
    void missingAndInvalidCsrfRejectRegistrationWithoutCreatingUser() throws Exception {
        String request = "{\"email\":\"missingcsrf@test.local\",\"password\":\"password123\",\"displayName\":\"CSRF Probe\"}";
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/auth/register")
                .with(csrf().useInvalidToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isForbidden());
        assertFalse(userRepository.existsByEmail("missingcsrf@test.local"));
    }

    @Test
    void missingCsrfRejectsLoginWithoutNewAuthentication() throws Exception {
        createUser("logincsrf@test.local");
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/api/auth/login")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"logincsrf@test.local\",\"password\":\"password\"}"))
            .andExpect(status().isForbidden());
        assertNull(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
    }

    @Test
    void missingAndMismatchedCsrfCannotLogOutAnAuthenticatedUser() throws Exception {
        createUser("logoutcsrf@test.local");
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/api/auth/login")
                .session(session)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"logoutcsrf@test.local\",\"password\":\"password\"}"))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/auth/logout").session(session))
            .andExpect(status().isForbidden());
        assertFalse(session.isInvalid());
        mockMvc.perform(post("/api/auth/logout").session(session).with(csrf().useInvalidToken()))
            .andExpect(status().isForbidden());
        assertFalse(session.isInvalid());
        mockMvc.perform(get("/api/auth/me").session(session))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/auth/logout").session(session).with(csrf()))
            .andExpect(status().isOk());
        assertTrue(session.isInvalid());
    }
}

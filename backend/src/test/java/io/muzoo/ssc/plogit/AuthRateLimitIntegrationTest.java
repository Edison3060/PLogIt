package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.support.IntegrationTestBase;
import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "plogit.auth.rate-limit.login-attempts=3",
    "plogit.auth.rate-limit.registration-attempts=2"
})
@Import(PostgresTestConfig.class)
class AuthRateLimitIntegrationTest extends IntegrationTestBase {

    private RequestPostProcessor from(String address) {
        return request -> {
            request.setRemoteAddr(address);
            return request;
        };
    }

    @Test
    void loginLimitRejectsAttemptsEvenWithSpoofedForwardedAddress() throws Exception {
        createUser("login@test.local");
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/auth/login")
                    .with(from("192.0.2.11"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"email":"login@test.local","password":"wrong-password"}
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
        }

        String retryAfter = mockMvc.perform(post("/api/auth/login")
                .with(from("192.0.2.11"))
                .header("X-Forwarded-For", "203.0.113.77")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"login@test.local","password":"password"}
                    """))
            .andExpect(status().isTooManyRequests())
            .andExpect(header().exists("Retry-After"))
            .andExpect(jsonPath("$.status").value(429))
            .andExpect(jsonPath("$.message").value("Too many authentication requests. Try again later"))
            .andReturn().getResponse().getHeader("Retry-After");
        assertTrue(Long.parseLong(retryAfter) > 0);
    }

    @Test
    void registrationIsLimitedBeforeParsingMalformedBodies() throws Exception {
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/api/auth/register")
                    .with(from("192.0.2.12"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":"))
                .andExpect(status().isBadRequest());
        }

        mockMvc.perform(post("/api/auth/register")
                .with(from("192.0.2.12"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"blocked@test.local","password":"password123","displayName":"Blocked"}
                    """))
            .andExpect(status().isTooManyRequests());

        assertFalse(userRepository.existsByEmail("blocked@test.local"));
    }

    @Test
    void differentClientsAndEndpointsHaveIndependentLimits() throws Exception {
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/api/auth/register")
                    .with(from("192.0.2.13"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":"))
                .andExpect(status().isBadRequest());
        }

        mockMvc.perform(post("/api/auth/register")
                .with(from("192.0.2.13"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":"))
            .andExpect(status().isTooManyRequests());

        mockMvc.perform(post("/api/auth/register")
                .with(from("192.0.2.14"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":"))
            .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/auth/login")
                .with(from("192.0.2.13"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"unknown@test.local","password":"incorrect"}
                    """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void registrationForNewAndExistingEmailsReturnsSameGenericResponse() throws Exception {
        String content = """
            {"email":"duplicate@test.local","password":"password123","displayName":"Example"}
            """;

        String first = mockMvc.perform(post("/api/auth/register")
                .with(from("192.0.2.15"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.message").value("Registration request received. Please sign in"))
            .andReturn().getResponse().getContentAsString();

        String second = mockMvc.perform(post("/api/auth/register")
                .with(from("192.0.2.15"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.message").value("Registration request received. Please sign in"))
            .andReturn().getResponse().getContentAsString();

        assertEquals(first, second);
        assertFalse(first.contains("duplicate@test.local"));
        assertEquals(1, userRepository.count());
    }

    @Test
    void unknownEmailAndWrongPasswordReturnSameLoginError() throws Exception {
        createUser("known@test.local");

        String unknown = mockMvc.perform(post("/api/auth/login")
                .with(from("192.0.2.16"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"unknown@test.local","password":"wrong"}
                    """))
            .andExpect(status().isUnauthorized())
            .andReturn().getResponse().getContentAsString();

        String incorrect = mockMvc.perform(post("/api/auth/login")
                .with(from("192.0.2.16"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"known@test.local","password":"wrong"}
                    """))
            .andExpect(status().isUnauthorized())
            .andReturn().getResponse().getContentAsString();

        // The dynamic response timestamp differs; compare only public error fields.
        assertTrue(unknown.contains("Invalid email or password"));
        assertTrue(incorrect.contains("Invalid email or password"));
        assertFalse(unknown.contains("unknown@test.local"));
        assertFalse(incorrect.contains("known@test.local"));
    }
}

package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.support.IntegrationTestBase;
import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class AuthIntegrationTest extends IntegrationTestBase {

    @Test
    void registerCreatesUser() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"new@test.local","password":"password123","displayName":"New User"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("new@test.local"))
            .andExpect(jsonPath("$.displayName").value("New User"));
    }

    @Test
    void loginWithCorrectCredentialsSucceeds() throws Exception {
        createUser("login@test.local");

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"login@test.local","password":"password"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("login@test.local"));
    }

    @Test
    void loginWithWrongPasswordFails() throws Exception {
        createUser("login@test.local");

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"login@test.local","password":"wrong-password"}
                    """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void meWithoutAuthReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
            .andExpect(status().isForbidden());
    }

    @Test
    void meWithAuthReturnsCurrentUser() throws Exception {
        User user = createUser("me@test.local");

        mockMvc.perform(get("/api/auth/me").with(user(principalFor(user))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("me@test.local"));
    }
}

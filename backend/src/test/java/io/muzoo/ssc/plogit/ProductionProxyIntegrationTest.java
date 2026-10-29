package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.support.IntegrationTestBase;
import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "plogit.cookies.secure=true",
    "server.forward-headers-strategy=framework"
})
@Import(PostgresTestConfig.class)
class ProductionProxyIntegrationTest extends IntegrationTestBase {

    @Test
    void forwardedHttpsRequestGetsSecureReadableCsrfCookie() throws Exception {
        Cookie token = mockMvc.perform(get("/api/health")
                .header("X-Forwarded-Proto", "https")
                .header("X-Forwarded-Host", "plogit.example.com"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getCookie("XSRF-TOKEN");

        assertNotNull(token);
        assertTrue(token.getSecure());
        assertFalse(token.isHttpOnly());
        assertEquals("Lax", token.getAttribute("SameSite"));
    }
}

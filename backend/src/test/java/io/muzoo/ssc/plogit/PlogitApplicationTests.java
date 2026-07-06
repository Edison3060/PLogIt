package io.muzoo.ssc.plogit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires PostgreSQL - integration tests added in Slice 3 with Testcontainers")
class PlogitApplicationTests {

    @Test
    void contextLoads() {
    }
}

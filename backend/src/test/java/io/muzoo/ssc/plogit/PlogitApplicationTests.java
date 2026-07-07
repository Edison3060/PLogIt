package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(PostgresTestConfig.class)
class PlogitApplicationTests {

    @Test
    void contextLoads() {
    }
}

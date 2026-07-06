package io.muzoo.ssc.plogit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:postgresql://localhost:5432/plogit_test",
    "spring.datasource.username=plogit",
    "spring.datasource.password=plogit",
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=none"
})
class PlogitApplicationTests {

    @Test
    void contextLoads() {
    }
}

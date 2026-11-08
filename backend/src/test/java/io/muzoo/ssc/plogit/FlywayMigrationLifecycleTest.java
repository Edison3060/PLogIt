package io.muzoo.ssc.plogit;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlywayMigrationLifecycleTest {

    private static final String SAMPLE_ID = "00000000-0000-0000-0000-000000000031";

    @Test
    void cleanV1ThenIncrementalV2V3PreservesSyntheticLogAndSnapshot() throws Exception {
        try (PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine")) {
            postgres.start();
            Path directory = Files.createTempDirectory("plogit-flyway-v1-");
            try {
                Path v1 = directory.resolve("V1__init_schema.sql");
                try (InputStream source = getClass().getClassLoader()
                        .getResourceAsStream("db/migration/V1__init_schema.sql")) {
                    if (source == null) {
                        throw new IllegalStateException("Canonical V1 migration unavailable");
                    }
                    Files.copy(source, v1);
                }

                migrate(postgres, "filesystem:" + directory.toAbsolutePath());
                assertEquals(1, queryCount(postgres, "SELECT count(*) FROM flyway_schema_history WHERE success"));

                execute(postgres, """
                    INSERT INTO users (id, email, password_hash, display_name)
                    VALUES (31, 'synthetic-migration@invalid.test', 'synthetic-not-a-password', 'Synthetic');
                    INSERT INTO engagements (id, name, description, status, leader_id)
                    VALUES (31, 'Synthetic', 'Migration preservation fixture', 'ACTIVE', 31);
                    INSERT INTO logs (id, engagement_id, author_id, activity_type, title,
                                      description, result, outcome, review_state)
                    VALUES ('00000000-0000-0000-0000-000000000031'::uuid, 31, 31,
                            'RECONNAISSANCE', 'Preserve this log', 'synthetic', 'synthetic',
                            'IN_PROGRESS', 'DRAFT');
                    INSERT INTO log_versions (log_id, version_number, snapshot, edited_by_id)
                    VALUES ('00000000-0000-0000-0000-000000000031'::uuid, 1,
                            '{"title":"Preserve this log"}'::jsonb, 31);
                    """);

                migrate(postgres, "classpath:db/migration");

                assertEquals(3, queryCount(postgres,
                    "SELECT count(*) FROM flyway_schema_history WHERE success AND version IN ('1','2','3')"));
                assertEquals(1, queryCount(postgres,
                    "SELECT count(*) FROM logs WHERE id = '" + SAMPLE_ID
                        + "'::uuid AND title = 'Preserve this log' AND version = 0"));
                assertEquals(1, queryCount(postgres,
                    "SELECT count(*) FROM engagements WHERE id = 31 AND version = 0"));
                assertEquals(1, queryCount(postgres,
                    "SELECT count(*) FROM log_versions WHERE log_id = '" + SAMPLE_ID
                        + "'::uuid AND version_number = 1"));
                assertEquals(1, queryCount(postgres,
                    "SELECT count(*) FROM pg_constraint WHERE conname = 'uk_log_versions_log_version'"));
            } finally {
                Files.deleteIfExists(directory.resolve("V1__init_schema.sql"));
                Files.deleteIfExists(directory);
            }
        }
    }

    private static void migrate(PostgreSQLContainer postgres, String location) {
        Flyway.configure()
            .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
            .locations(location)
            .load()
            .migrate();
    }

    private static void execute(PostgreSQLContainer postgres, String sql) throws Exception {
        try (Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             Statement statement = connection.createStatement()) {
            for (String command : sql.split(";\\s*\\n")) {
                if (!command.isBlank()) {
                    statement.execute(command);
                }
            }
        }
    }

    private static long queryCount(PostgreSQLContainer postgres, String query) throws Exception {
        try (Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(query)) {
            result.next();
            return result.getLong(1);
        }
    }
}

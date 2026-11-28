# Session 19 | Dependency refresh

Phase 10 stacks on unmerged Phase 09 PR #30 at `591e3e8a88f82e38c66203ff4fbe69ef238411cf`. The controller permits isolated verification while Phase 09 and production acceptance remain open. The canonical source and copied suite plans retain their separate roles.

## JR-181

**DONE.** Map the Java and Spring dependency baseline

At Phase 09 head `591e3e8a88f82e38c66203ff4fbe69ef238411cf`, canonical `backend/pom.xml` owns Java 21 and Spring Boot 4.1.1. It pins PostgreSQL JDBC 42.7.12, Tomcat 11.0.25, Jackson 3 BOM 3.1.7, and independently imports the Jackson 2.21.7 BOM. `backend/Dockerfile` separately pins Java 21 build/runtime images. `.github/workflows/ci.yml` owns the actual Java 21 Maven regression and uses Docker-backed PostgreSQL 16 via `PostgresTestConfig` and `FlywayMigrationLifecycleTest`. `ExportIntegrationTest` covers the legacy export, while authentication, CSRF, review, attachment and audit integration classes cover the affected boundaries. The dated PLogIt Product Spec and Suite Master Spec corroborate the baseline and describe historical status. Phase 09 executable CI 46013369940 passed 170 backend tests. Expected authoritative manifest and coverage map found. Builder Lead reviewed.

## JR-182

**DONE.** Set isolated Boot patch acceptance

The baseline is Java 21 and Boot 4.1.1, with the prior Phase 09 PostgreSQL-backed result. Choose Boot 4.1.3 within the same minor line, keep the Java level and four explicit library pins, and resolve the effective dependency tree before considering a source PR. Spring announced 4.1.3 on 2026-11-20 (https://spring.io/blog/2026/11/20/spring-boot-4-1-3-available-now/, accessed 2026-11-28). A BOM patch can change transitive libraries even when direct code is identical. Verify the same V1→V3 Flyway history with existing synthetic rows, rejection and rollback on an invalid migration, anonymous and out-of-scope denial without writes, and the existing unversioned JSON DTO and attachment-filename contract. Java 25 and Boot 4.2 need independent assessment.

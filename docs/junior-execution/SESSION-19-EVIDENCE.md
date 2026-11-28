# Session 19 | Dependency refresh

Phase 10 stacks on unmerged Phase 09 PR #30 at `591e3e8a88f82e38c66203ff4fbe69ef238411cf`. The controller permits isolated verification while Phase 09 and production acceptance remain open. The canonical source and copied suite plans retain their separate roles.

## JR-181

**DONE.** Map the Java and Spring dependency baseline

At Phase 09 head `591e3e8a88f82e38c66203ff4fbe69ef238411cf`, canonical `backend/pom.xml` owns Java 21 and Spring Boot 4.1.1. It pins PostgreSQL JDBC 42.7.12, Tomcat 11.0.25, Jackson 3 BOM 3.1.7, and independently imports the Jackson 2.21.7 BOM. `backend/Dockerfile` separately pins Java 21 build/runtime images. `.github/workflows/ci.yml` owns the actual Java 21 Maven regression and uses Docker-backed PostgreSQL 16 via `PostgresTestConfig` and `FlywayMigrationLifecycleTest`. `ExportIntegrationTest` covers the legacy export, while authentication, CSRF, review, attachment and audit integration classes cover the affected boundaries. The dated PLogIt Product Spec and Suite Master Spec corroborate the baseline and describe historical status. Phase 09 executable CI 46013369940 passed 170 backend tests. Expected authoritative manifest and coverage map found. Builder Lead reviewed.

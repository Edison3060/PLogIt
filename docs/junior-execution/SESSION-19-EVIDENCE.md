# Session 19 | Dependency refresh

Phase 10 stacks on unmerged Phase 09 PR #30 at `591e3e8a88f82e38c66203ff4fbe69ef238411cf`. The controller permits isolated verification while Phase 09 and production acceptance remain open. The canonical source and copied suite plans retain their separate roles.

## JR-181

**DONE.** Map the Java and Spring dependency baseline

At Phase 09 head `591e3e8a88f82e38c66203ff4fbe69ef238411cf`, canonical `backend/pom.xml` owns Java 21 and Spring Boot 4.1.1. It pins PostgreSQL JDBC 42.7.12, Tomcat 11.0.25, Jackson 3 BOM 3.1.7, and independently imports the Jackson 2.21.7 BOM. `backend/Dockerfile` separately pins Java 21 build/runtime images. `.github/workflows/ci.yml` owns the actual Java 21 Maven regression and uses Docker-backed PostgreSQL 16 via `PostgresTestConfig` and `FlywayMigrationLifecycleTest`. `ExportIntegrationTest` covers the legacy export, while authentication, CSRF, review, attachment and audit integration classes cover the affected boundaries. The dated PLogIt Product Spec and Suite Master Spec corroborate the baseline and describe historical status. Phase 09 executable CI 46013369940 passed 170 backend tests. Expected authoritative manifest and coverage map found. Builder Lead reviewed.

## JR-182

**DONE.** Set isolated Boot patch acceptance

The baseline is Java 21 and Boot 4.1.1, with the prior Phase 09 PostgreSQL-backed result. Choose Boot 4.1.3 within the same minor line, keep the Java level and four explicit library pins, and resolve the effective dependency tree before considering a source PR. Spring announced 4.1.3 on 2026-11-20 (https://spring.io/blog/2026/11/20/spring-boot-4-1-3-available-now/, accessed 2026-11-28). A BOM patch can change transitive libraries even when direct code is identical. Verify the same V1→V3 Flyway history with existing synthetic rows, rejection and rollback on an invalid migration, anonymous and out-of-scope denial without writes, and the existing unversioned JSON DTO and attachment-filename contract. Java 25 and Boot 4.2 need independent assessment.

## JR-183

**DONE.** Run the full Maven suite on Boot 4.1.3

The isolated one-line patch on `work/plogit-session-19-boot-candidate` at `2e079773d3d61b7bf019dc0d2bcbef9302358f3d` went through [draft PR #31](https://github.com/Edison3060/PLogIt/pull/31) and [CI 46096595474](https://github.com/Edison3060/PLogIt/actions/runs/46096595474). Expected complete Java/PostgreSQL regression and a packaged candidate. Observed `mvn -B clean verify` with Java 21.0.13.1, Maven 3.9.15, Boot 4.1.3, and PostgreSQL 16.16: 170 tests, zero failures, errors or skips, then `BUILD SUCCESS`. Relevant suites included Flyway (4), export (18), log authorization (12) and audit (18). The Testcontainers fixture uses isolated PostgreSQL from `PostgresTestConfig` and independent databases for migration lifecycle cases. A local Maven retry hit proxy connectivity. The recorded complete suite ran on GitHub.

## JR-184

**DONE.** Verify migration conflict rollback on the candidate

On candidate [Maven job 140829239686](https://github.com/Edison3060/PLogIt/actions/runs/46096595474/job/140829239686), `FlywayMigrationLifecycleTest#invalidMigrationRollsBackPartialDdlAndPreservesV1Data` applies V1, seeds one user, then attempts a synthetic V2 with partial DDL and an invalid write. The log reports that the V2 failed and Flyway rolled it back. Source assertions require zero partial table, no successful V2 history row and preserved user and V1 history. All four migration tests passed. The same run passed 12 log-authorization, 18 audit and 18 export tests, including the checked-in unversioned JSON fixture. The only candidate change is the Boot parent property. Unapproved version and dependency conflicts remain gated on the draft branch.

## JR-185

**DONE.** Close Boot verification without changing Phase 10 runtime

The supported patch reproduced the complete 170-test Maven baseline and rolled back the invalid migration on PostgreSQL 16.16. The Boot proposal remains in draft PR #31, reversible by retaining 4.1.1 or reverting its one-line commit if subsequently accepted. Phase 10 retains the 4.1.1 POM. Promotion still requires review of effective dependency changes, candidate CI and the existing Phase 09 and release gates. The patch announcement is evidence of availability. Applicability of upstream advisories and library pins must be evaluated separately.

## JR-186

**DONE.** Map the Node and frontend dependency baseline

Phase 09 head `591e3e8a88f82e38c66203ff4fbe69ef238411cf` contains a digest-pinned `node:20-alpine` frontend build stage and pinned nginx runtime in `frontend/Dockerfile`. `.github/workflows/ci.yml` uses Node 22.23.4 for npm and Vitest. The canonical lock resolves React and React DOM 18.3.1, Vite 6.4.3, Vitest 4.1.12 and the React plugin 4.7.0. `frontend/package.json` owns build, lint and test entry points. Existing test suites cover protected routes, API handling, theme, engagement and log detail. `scripts/verify-log-export-journeys.cjs` owns full browser journeys. Older mirrored specs describe the architecture and baseline, whereas the current files own what is built.

## JR-187

**DONE.** Select an isolated Node 24 LTS image candidate

Node's official EOL page lists Node 20's final update in March 2026, and its official security release identifies Node 24.22.3 LTS on 2026-11-11 (https://nodejs.org/en/about/eol and https://nodejs.org/en/blog/release/v24.22.3/, accessed 2026-11-28). Verify the existing lock with Node 24.22.3 and the published multi-platform digest, keep React and Vite changes isolated, and verify production asset serving and unauthenticated routing. Vite's support page requires using a currently supported minor for security fixes. Preserve current Vite in the Node-only candidate and track supported-line evaluation separately rather than expanding this runtime patch (https://vite.dev/releases.html, accessed 2026-11-28).

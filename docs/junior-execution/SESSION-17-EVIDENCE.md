# Session 17 evidence

Phase 09 stacks on Phase 08 at `01dc9925a1f55924e242a888a592224cad956737` while [PR #29](https://github.com/Edison3060/PLogIt/pull/29) remains draft. The scope is JR-161–JR-170. All data is synthetic.

## JR-161

At Phase 08 head `01dc9925a1f55924e242a888a592224cad956737`, Flyway V1 owns foreign keys from memberships to users and engagements, logs to users and engagements, and snapshots to logs and editors. V1 uniquely indexes each membership pair. V2 adds `version` for Hibernate `@Version` on logs and engagements. V3 uniquely constrains the snapshot pair `(log_id, version_number)`. `Engagement`, `EngagementMember` and `LogEntry` map domain enum names to strings. Hibernate validates the Flyway schema and the reviewed migrations define these fields as `VARCHAR`. Existing coverage is in `FlywayMigrationLifecycleTest`, `LogVersioningIntegrationTest`, `OptimisticLockingIntegrationTest`, and `TeamWorkflowIntegrationTest`. The product specification supplies the desired behavior. The canonical migrations and live JPA mappings own current behavior. Expected inspection found the FK and uniqueness declarations and an enum enforcement gap at the storage layer. No runtime test or code change at this inventory checkpoint. Reviewer is the Builder Lead.

## JR-162

Expected on empty and seeded V1-to-V3 databases are valid engagement/user membership and log links, one membership per engagement/user pair, one snapshot per log/version pair, and lossless preservation of persisted state and snapshots. The application expects known domain names for engagement status, member role, log activity type, outcome and review state. Current PostgreSQL DDL relies on string widths and nullability for those enum columns. ORM/API validation provides the normal write boundary. Direct writes are outside that boundary and a new storage-level rule would affect historical rows and future enum rollout order. Treat the stronger invariant as pending in JR-165. Reviewer is the Builder Lead. The decision is recorded in DEC-2026-11-27-01.

## JR-163

Added `cleanDatabaseAppliesCanonicalMigrationsAndEnforcesDeclaredSchema` and extended `cleanV1ThenIncrementalV2V3PreservesSyntheticLogAndSnapshot` in `FlywayMigrationLifecycleTest`. Expect migration versions 1–3, six validated foreign keys, two unique constraints and the unchanged seeded leadership, log, and JSON snapshot. The existing Maven verification job executes these Testcontainers tests in PostgreSQL 16. Local `git diff --check` passed. Maven and Docker are unavailable locally. Runtime results pending GitHub CI. Reviewer is the Builder Lead.

## JR-164

Added `duplicateAndForeignKeyViolationsRollBackPriorWrites`: PostgreSQL is expected to return `23505` for a repeated membership pair and `23503` for a non-existent user. Each transaction first updates a synthetic user. Both failure cases must reach `25P02`, then roll back and preserve the prior user and exact membership. SQL fixture runs on a disposable server. HTTP active membership denial is covered separately by `TeamWorkflowIntegrationTest` and `JoinCodeWorkflowIntegrationTest`. Local `git diff --check` passed. Test execution is pending GitHub CI. Reviewer is the Builder Lead.

## JR-165

JR-163 and JR-164 exercise Flyway and active database FKs and uniqueness. Existing `LogVersioningIntegrationTest` and `OptimisticLockingIntegrationTest` cover snapshot and optimistic version behavior. Storage-level enforcement of the domain enum values remains open in DEC-2026-11-27-01. `EnumType.STRING` constrains ORM writes and the application API validates inputs. V1–V3 do not contain an enum or enum CHECK constraint. No change to migrations. Close the reviewed FK/uniqueness evidence after exact-head CI and keep the broader enum invariant open. Reviewer is the Builder Lead.

## JR-166

At baseline `01dc9925a1f55924e242a888a592224cad956737`, `EngagementController` resolves engagement through `EngagementService` and invokes `MembershipService.transferLeadership`. The service updates both roles and the engagement leader. `LogReviewService.transition` owns review mutations. `ExportService.export` authorizes the leader, generates the report, performs review transitions within its transaction, and emits the aggregate event. `AuditEventListener` stores business audit events with `BEFORE_COMMIT` listeners. Existing `TeamWorkflowIntegrationTest`, `ReviewTransitionIntegrationTest`, `ExportIntegrationTest` and `AuditLogIntegrationTest` map both commits and known failure cases. The product spec describes desired lifecycle and audit behavior. These canonical controllers, services and listener own actual behavior. Expected owners and boundaries were found by source inspection. No code change or new runtime test in this inventory checkpoint. Reviewer is the Builder Lead.

## JR-167

Leadership POST requires the active leader and `newLeaderId` of an active member. The engagement pointer, both roster roles and a single `TRANSFER_LEADERSHIP` audit must commit together. Review POST requires an authorized actor and `action=APPROVE` for a submitted log. State and scoped `APPROVE` audit must commit together. Export POST requires the leader and `format=JSON` with optional `includeExported`. Approved selections become exported and their per-log audit plus the aggregate audit must commit together. On audit persistence failure expect a generic server response, no report attachment, and original roles, states, versions and audit rows. The code uses before-commit audit persistence. Coverage of a database-side audit failure was missing across all three entry points. See DEC-2026-11-27-02. Reviewer is the Builder Lead.

## JR-168

Use `mvn -B clean verify` from `backend/`. `TeamWorkflowIntegrationTest.transferRefreshesPermissionsRosterAndSingleAuditThenRemovalPreservesHistory` uses `/api/engagements/{id}/transfer-leadership` and reloads pointer, roster, access and audit. `ReviewTransitionIntegrationTest` uses `/api/logs/{id}/transition` and verifies stored submission/approval plus scoped audit actor and action. `ExportIntegrationTest.jsonExportSelectsExactLogsAndCommitsAudit` uses `/api/engagements/{id}/export` and re-reads selected and excluded states, affected targets, actor and per-log and aggregate events. Prior Phase 08 CI is historical. Runtime verification on the Session 17 head is pending. Reviewer is the Builder Lead.

## JR-169

Added three `AuditLogIntegrationTest` requests through MockMvc and the real database. A test-only CHECK rejects the relevant audit INSERT at commit and is removed in `finally`. The export test permits per-log EXPORT writes and rejects the aggregate. Expected results are generic HTTP 500, no download response, unchanged persisted role assignments and leader pointer, review states and versions, and no surviving audit. Authorization denials are separately covered by the existing HTTP integration suite. Local `git diff --check` passed. Exact-head CI pending. Reviewer is the Builder Lead.

The follow-up also checks that the server's internal exception chain carries `23514` from the named audit constraint. This prevents an unrelated HTTP 500 from satisfying the fixture.

## JR-170

Executable SHA `8c852b564928bbb1ed7613fb0310b71273de7792`. [GitHub Actions run 45926331023](https://github.com/Edison3060/PLogIt/actions/runs/45926331023) ran `mvn -B clean verify` with Java 21.0.13, Maven 3.9.15 and PostgreSQL 16.16. All 170 backend tests passed with zero failures, errors or skips. This includes 4 Flyway migration tests, 18 audit tests, 21 review tests, 2 team tests and 18 export tests. Frontend build and all 25 Vitest tests passed. Isolated Compose HTTP and volume-restart smoke, Semgrep, Trivy filesystem and both images, and proxy validation passed. Production CD was skipped. Local `git diff --check` and an append-only step log check passed. Builder Lead reviewed.

- JR-163 Clean and seeded V1-to-V3 migrations succeeded. PostgreSQL reported six validated foreign keys and two unique constraints. The seeded leadership, log, JSON snapshot and version defaults were preserved.
- JR-164 The duplicate membership and invalid user FK produced SQLSTATE 23505 and 23503, respectively. Both transactions reported 25P02 after the rejected statement. Fresh queries confirmed the first write rolled back and the original membership remained.
- JR-168 Leadership, submission and approval, and legacy JSON export committed. The tests re-read the leader and roster, review states, selected and excluded export logs, and the matching actor, action and target of the audit rows.
- JR-169 Three PostgreSQL audit constraints returned SQLSTATE 23514 at commit. The API returned a generic 500 and export returned no download header. Fresh reads confirmed the original leader and roles, log states and versions, and zero remaining audit rows.
- JR-165 Foreign-key, unique constraint and rollback evidence passed. PostgreSQL enum enforcement remains open under DEC-2026-11-27-01, so JR-165 remains IN REVIEW.
- JR-170 The original transactional success and denial evidence both passed. VERIFIED NO CHANGE to application code, migrations, authentication, audit data and unversioned JSON.

[Draft PR #30](https://github.com/Edison3060/PLogIt/pull/30) stays stacked on unmerged [Phase 08 PR #29](https://github.com/Edison3060/PLogIt/pull/29). Phase 09 acceptance requires Session 18 recovery evidence. INT-001 and the existing release gates remain open.

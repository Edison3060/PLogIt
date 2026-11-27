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

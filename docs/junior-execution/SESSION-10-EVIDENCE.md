# PLogIt Session 10 — Phase 05B

Base: `f331623749db0165cb31146b8118373859e7fb34`, stacked on Phase 04 through [draft PR #26](https://github.com/Edison3060/PLogIt/pull/26). Synthetic fixtures only.

## JR-091

At base `f331623749db0165cb31146b8118373859e7fb34`, `LogService.create/update` persist the log and invoke `LogVersionService.snapshot` within the write transaction. The latter locks the log row, allocates the next ordinal, and saves an actor-stamped `LogSnapshot`. V3 enforces unique `(log_id, version_number)`. `LogEntry.@Version` and `saveAndFlush` provide optimistic locking during concurrent write transactions. `LogController` exposes member-scoped ascending history. Existing `LogVersioningIntegrationTest` covers two versions, unique constraints, concurrent snapshots and concurrent edits. `OptimisticLockingIntegrationTest` covers exception mapping. The mirrored product spec describes historical UUID and approved-edit policy differences recorded in DEC-2026-11-23-01. Expected inventory matched canonical source. Next JR-092 specifies the API and browser-staleness boundary.

## JR-092

Draft writes use authenticated `PUT /api/logs/{id}` with editable fields only, as defined by `LogUpdateRequest`. `GET /api/logs/{id}/history` returns ascending `versionNumber`, snapshot, editor and time. Two concurrent transactions loading the same JPA version should produce one durable update and one conflict, mapped to HTTP 409. A delayed browser save that reaches a new transaction after the winning save has committed supplies no expected version and can still succeed. A strict browser precondition would require an approved caller-compatibility decision. Preserve existing authorization and wire shape. Next JR-093 verifies the successful persistence fixture.

## JR-093

In [push CI 45098552729](https://github.com/Edison3060/PLogIt/actions/runs/45098552729) at `f331623749db0165cb31146b8118373859e7fb34`, `mvn -B clean verify` ran all ten `LogVersioningIntegrationTest` cases on PostgreSQL 16.16. `authorCanCreateAndEditDraftWithStableIdentityAndOrderedSnapshots` creates an authenticated synthetic draft with Markdown and tags, updates it, GETs history and reads persisted snapshots. Expected and observed sequence `[1, 2]`, stable actor/log identity, and distinct original and edited content. V3 and the separate duplicate-insert test protect unique ordinals. Next JR-094 verifies races and the stale-client limitation.

## JR-094

In [push CI 45098552729](https://github.com/Edison3060/PLogIt/actions/runs/45098552729) at `f331623749db0165cb31146b8118373859e7fb34`, `concurrentEditsOnlySnapshotCommittedChanges` starts two PostgreSQL transactions that preload the same draft and release together. Exactly one optimistic lock failure, ordered versions `[1, 2]`, and the second snapshot matching the durable title passed. Parallel snapshots produce `[1, 2, 3]`. A duplicate insert fails and leaves one snapshot. Nonmembers cannot read history. `OptimisticLockingIntegrationTest` (2/2) verifies HTTP 409 mapping separately from the service-level race. The update DTO and client send no revision precondition, so a stale screen saving after the winner commits is outside that guarantee. JR-094 stays in review for strict client-side staleness acceptance; see the decision ledger. Next JR-095 records closure status.

## JR-095

JR-093's ordered success and JR-094's negative checks passed in [push CI 45098552729](https://github.com/Edison3060/PLogIt/actions/runs/45098552729) at `f331623749db0165cb31146b8118373859e7fb34`, with 150 total backend tests, zero failures/errors/skips. Builder Lead reviewed both fixtures and the safe HTTP error mapping. Leave full stale-client closure in review under DEC-2026-11-23-03. No production or compatibility behavior changed, so there is no application rollback. Next JR-096 inventories audit and board ownership.

## JR-096

`LogReviewService` updates state, then publishes `LogTransitionedEvent`. `AuditEventListener` persists at BEFORE_COMMIT in the same transaction; `AuditLogRepository` and engagement leader authorization govern reads. `LogBoard` sends scoped filters with React Query, and `LogDetail` renders current server state and ordered history. `useTransitionLog` invalidates board and detail queries on success and has no optimistic approval write. Existing `AuditLogIntegrationTest` and `ReviewTransitionIntegrationTest` cover stored transitions, membership, denials and replay. The product spec proposes additional filter dimensions and broader audit coverage. Canonical source map verified against `f331623749db0165cb31146b8118373859e7fb34`. Next JR-097 defines the exact board and rollback verification.

## JR-097

`GET /api/engagements/{id}/logs?reviewState=SUBMITTED&search=...` should select only committed matching logs. `GET /api/logs/{id}/history` should render version ordinals, editor and snapshots. Authorized `POST /api/logs/{id}/transition` accepts `action` and optional `comment`. Author SUBMIT and leader APPROVE should each produce exactly one audit row with actor and old/new states. Denied/rolled-back approval must not change durable state or create audit. A stale approval error must not render an approved state optimistically. Next JR-098 exercises the actual board and PostgreSQL.

## JR-098

`ReviewTransitionIntegrationTest` and `AuditLogIntegrationTest` pass at `f331623749db0165cb31146b8118373859e7fb34` in [push CI 45098552729](https://github.com/Edison3060/PLogIt/actions/runs/45098552729). The baseline tests check committed author submission, leader approval and audit. The Chromium fixture exercises history, composed review queue/search filters, author submission and leader approval against the isolated local PostgreSQL-backed application. Browser results pending at this checkpoint. Clarification of JR-097: UI history shows version, time, title and result. Editor ID is verified in the history API and database. Next JR-099 adds a forced rollback assertion and verifies stale review behavior.

## JR-099

`AuditLogIntegrationTest.rolledBackApprovalLeavesSubmittedStateAndNoAudit` executes leader approval from SUBMITTED inside a real managed transaction, observes transient APPROVED, flushes the update, and marks the outer transaction for rollback. Expected durable SUBMITTED and an empty audit list. New test execution pending CI. Baseline `deniedReviewAndDuplicateApprovalLeaveNoExtraWritesOrAudit` passed; Chromium stale-click scenario is pending. Next JR-100 reconciles browser and CI before phase disposition.

### Reconciliation

Initial assessment and final reconciliation confirm the positive and negative audit cases. Reviewer: Builder Lead.

### Reconciliation

- **JR-098** passed in headless Chromium 153.0.8010.0, with separate leader and author sessions, Vite 6.4.3, Java 21.0.13.1 and isolated PostgreSQL 16.16. Register two synthetic users, create an engagement, join the author, create two drafts, PUT an edit to one draft and open Edit history. The browser showed v1/v2 and the original title. Click Submit as author. In the leader's board, click Review Queue, search for a miss and observe an empty result, then search the edited title and observe the one matching row. Open it and click Approve. The browser showed Approved following a successful server response. PostgreSQL directly confirmed the original and edited titles in versions 1 and 2, the author as editor in both, durable APPROVED, and exactly one SUBMIT and one APPROVE audit row with correct actors and prior/next state. Screenshots: [history](evidence/session-10/session10-history.png), [filtered board](evidence/session-10/session10-review-queue.png), [approval](evidence/session-10/session10-approved.png).
- **JR-099** passed. Member approval returned 403. After a separate committed rejection, a stale leader page attempted Approve and received 409, displayed the conflict, and showed no Approved badge. SQL found the second log in DRAFT and exactly SUBMIT and REJECT audit rows. [Conflict screenshot](evidence/session-10/session10-stale.png). In CI, `rolledBackApprovalLeavesSubmittedStateAndNoAudit` forced a rollback after flushing a transient approval, and asserted SUBMITTED and no durable audit row.
- **JR-094/095** remain IN REVIEW. Two authenticated clients read the same draft before editing. The author saved first. The second client's delayed PUT returned 200 and installed its title as version 3. History retained the original and first committed title. This is an HTTP reproduction of the version-precondition gap in DEC-2026-11-23-03. The existing simultaneous database transaction guard continues to pass.
- **Regression and phase gate.** [Push CI 45190233070](https://github.com/Edison3060/PLogIt/actions/runs/45190233070), executable/test SHA `1064cf0ff89743886d520dbb7b09fc62bcd599a3`: `mvn -B clean verify` passed 151 backend tests, zero failures/errors/skips, including 15 audit tests. Frontend lint, build and 19 Vitest tests, live isolated Compose, Semgrep, Trivy filesystem and both images, and proxy passed. Production CD skipped. Locally `npm ci --ignore-scripts --no-audit --no-fund`, `npm test`, `npm run lint` and `npm run build` exited 0 on Node 24.22.3/npm 11.19.1. Local Maven compilation succeeded. Local repackaging hit dependency transfer failures, so the isolated browser test used Maven-compiled application classes and resolved classpath. System Chromium could not create its local browser socket and the first headless attempt timed out with shared browser contexts. The pass used separate headless Chromium processes and returned exit 0 for browser plus SQL assertions.
- Checkpoints: JR-091 `ae625c2d97e6`, JR-092 `d45d5700a68b`, JR-093 `a51dbe18659d`, JR-094 `a53c6c9097b6`, JR-095 `f533ca42911b`, JR-096 `da089837b5fe`, JR-097 `f3075abdeb63`, JR-098 `84f00762179f`, JR-099 `1064cf0ff897`. JR-100 is the reconciliation commit. Application runtime, schema and production deployment configuration were untouched. The only executable repository change is the forced rollback regression test. Rollback, if requested, is removal of that test.
- Phase 05 gate remains pending for draft preconditions and previous phase/release acceptance. See handoff and decisions.

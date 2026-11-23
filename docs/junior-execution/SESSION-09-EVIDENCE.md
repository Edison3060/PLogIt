# Session 09 | Log authoring and review

Scope is JR-081–JR-090 on `work/plogit-phase-05`, stacked on Phase 04 at `456d8487fc970b1dff829576f6dd9e5588dca67b`. The Phase 04 acceptance gate remains pending. Independent log lifecycle tests use synthetic data. Reviewer is Builder Lead. Owner sign-off remains pending.

## JR-081

At Phase 04 source baseline `456d8487fc970b1dff829576f6dd9e5588dca67b`, `web/LogController.java` dispatches POST for creation, PUT for edits and GET for detail and history. `service/LogService.java` resolves the engagement and authenticated actor. `domain/LogEntry.java` generates and persists the UUID. Creation sets DRAFT and saves snapshot 1 via `LogVersionService`. Edits require active membership, author or leader, and DRAFT, then persist a snapshot using the authenticated editor. `LogCreateRequest` and `LogUpdateRequest` expose only editable content fields.

The roadmap calls for live ownership and existing coverage. `LogAuthzIntegrationTest`, `LogVersioningIntegrationTest`, `BadRequestIntegrationTest` and `CsrfIntegrationTest` cover isolated parts of the flow. Prior Phase 04 CI is baseline context. This inventory was verified by reading canonical source and tests. The July product text and copied RES implementation are historical reference material. No test execution or application change was needed for this inventory.

## JR-082

Authenticated POST `/api/engagements/{engagementId}/logs` with CSRF takes required `activityType`, `title`, Markdown `description` and `result`, and `outcome`, plus optional `target`, `toolUsed`, `tags`, `codeBlock` and `codeLanguage`. It returns HTTP 200 and a server-generated stable UUID, author, engagement, and DRAFT state. PUT `/api/logs/{logId}` uses the same editable fields as optional updates. A null update field leaves its previous value. Content changes and snapshots commit together. GET detail and history return current content and ordered snapshots with editor IDs.

Expected denials include HTTP 400 for invalid activity, 409 for an active non-author nonleader's draft edit or an edit outside DRAFT, 404 for a nonmember, and 403 for missing CSRF. Identity, editor and review state are server-owned. The historical product spec §§4–5 and 8–9 describe client-generated UUIDs, target scope checks and post-approval leader edits. The November suite rebaseline confirms server-side UUID persistence, and the canonical edit guard and JR-082 require DRAFT. Target scope enforcement and any post-approval editing policy require an owner decision before changing behavior. The app's target field accepts optional text up to 255 characters. This checkpoint specifies the contract and makes no runtime claim.

## JR-083

Added `LogVersioningIntegrationTest#authorCanCreateAndEditDraftWithStableIdentityAndOrderedSnapshots` using an active member and a synthetic `fixture.example` note. POST -> PUT -> GET detail -> GET history must retain the same UUID, author and engagement, keep DRAFT, preserve omitted fields, persist new Markdown and tags, and return exactly snapshots 1 and 2 with the original and updated values and authenticated editor. No screenshot is required for HTTP/backend checks. `git diff --check` exited 0. Local Maven and Docker are unavailable. PostgreSQL 16/Testcontainers runtime evidence is pending GitHub Actions.

## JR-084

Added `LogVersioningIntegrationTest#invalidActivityAndDeniedEditsLeaveContentAndHistoryIntact`. An unknown activity must yield 400 and write no log, snapshot or audit. An active non-author member's edit must yield 409 with no state, title, row-version, snapshot or audit mutation. After real author submission, an author edit must yield 409 and leave the log SUBMITTED, original content and editor intact, with exactly one original snapshot and one SUBMIT event. `git diff --check` exited 0. Runtime results pending GitHub Actions.

## JR-085

The JR-083 and JR-084 tests exercise the create/edit/reread/history path and the invalid activity, other member draft edit and post-submit denial paths. Reviewed both test diffs and the DTO, controller, membership, persistence and version boundaries. No production code correction is proposed. If the tests pass, the authoring result will be verified with no application behavior change. The test-only changes can be reverted independently. Phase 04 acceptance and potential changes to log-target scope checks or approved-log editing are separate open decisions. Reviewer: Builder Lead. Runtime and regression sign-off remain pending.

## JR-086

At `456d8487fc970b1dff829576f6dd9e5588dca67b`, `LogController.transition` delegates to transactional `LogReviewService.transition`. The service requires active engagement membership and derives author/leader from persisted data. `ReviewStates` dispatches to `DraftState`, `SubmittedState`, `ApprovedState` or `ExportedState`. DRAFT accepts SUBMIT from author or leader. SUBMITTED accepts leader APPROVE or REJECT, with nonblank comment on rejection. Rejection returns to DRAFT and records the reviewer, comment and time. Resubmission clears those fields. `AuditEventListener` writes transition events before commit in the same transaction. `LogVersionService` tracks content revisions, while transitions use audit events.

`ReviewTransitionIntegrationTest` maps routes, role and state eligibility, rejection and retry. `AuditLogIntegrationTest` maps audit evidence. `BadRequestIntegrationTest` and `CsrfIntegrationTest` cover request guards. This is a source-only inventory. Historical diagrams and copied code are references. No production or test source change at this checkpoint.

## JR-087

Authenticated POST `/api/logs/{id}/transition` with CSRF takes an `action` enum and optional `comment`. A DRAFT created by the member can be submitted by that author or the leader. A leader can approve SUBMITTED exactly once, or return it to DRAFT with a nonblank comment. Fresh reads show committed state, and transition audits retain actor, log, engagement and from/to state. Approval retries and other ineligible transitions return 409, nonleader approval or rejection from SUBMITTED returns 403, and outsiders receive 404. Missing or blank rejection comments return 409. The rejection is a transition to DRAFT, with metadata, in accordance with the product's rejection prose. The state enum contains no REJECTED value.

Product text describes comments only for rejection. The current DTO accepts optional text for any action, and the audit listener records a supplied comment for any successful transition. That existing behavior is outside the narrow transition acceptance criteria. A policy to restrict or migrate those comments requires a separate decision and does not alter the required rejection behavior. The existing export transition and unversioned export behavior remain release boundaries.

## JR-088

Added `ReviewTransitionIntegrationTest#authorSubmissionAndLeaderApprovalCommitStateAndOneAuditEach` with a synthetic member-author, separate leader and DRAFT fixture. POST SUBMIT as author and POST APPROVE as leader should return 200. Fresh DB reads must observe each committed state. A new member GET must return APPROVED with the original author and content. Exactly one audit for each action must identify the correct actor, UUID and from/to state. `git diff --check` exited 0. Runtime evidence pending CI.

## JR-089

Added `ReviewTransitionIntegrationTest#deniedReviewAndDuplicateApprovalLeaveNoExtraWritesOrAudit`. Expected outcomes are nonleader APPROVE 403 and missing or whitespace-only REJECT comment 409. After denials a fresh DB read must retain SUBMITTED, the same row version, no rejection metadata, no snapshots and no audit. One eligible leader approval succeeds. A sequential approval retry from APPROVED must yield 409 with no row version change and exactly one committed APPROVE audit. This test covers a sequential retry. `git diff --check` exited 0. Runtime evidence pending CI.

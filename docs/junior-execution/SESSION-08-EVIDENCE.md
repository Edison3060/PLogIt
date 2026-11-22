# Session 08 — scope/RoE and leadership/team verification

Branch `work/plogit-phase-04`, source baseline e3e2f5ae8132df4988c615490f3676ec7fc739d9. Controller claim c3a72d9bb3df60354eb3a1f3a7c9228fcaaa6a00. Exactly JR-071–080, no subagents. Test-only approach; preserve application, auth, audit and JSON semantics.

## JR-071
Status at checkpoint: DONE. Inventory Scope and rules of engagement.
- Inventory: ScopePanel.tsx renders saved in-scope targets and all five RoE fields; editing lives in pages/EngagementDetail.tsx (startEdit/handleSave), not ScopePanel. DTOs UpdateEngagementRequest/EngagementDetail and controller PUT/GET /api/engagements/{id} own wire data. EngagementService.updateAndBuildDetail asserts leader, updates non-null fields transactionally, emits changedFields-only UPDATE_ENGAGEMENT audit, and returns server-owned role. Dates are LocalDate; null update dates currently mean unchanged. Update DTO caps hours at 1000 and RoE strings at 4000 but has no target element/list bounds or syntax validation. Do not silently invent a target grammar or date-clearing policy.
- Existing tests: EngagementAuthzIntegrationTest activeMemberCanReadAndLeaderCanUpdateSameEngagement and crossEngagementWritesAndRemovedMembershipAreDeniedWithoutMutation cover description/permissions, AuditLogIntegrationTest engagement update metadata, EngagementPanels.test.tsx renders saved rules. Missing full scope/date persistence and invalid-target denial evidence. Canonical source baseline e3e2f5ae8132, planning copy reference-only. Expected owner/source map found, implemented with validation gap; VERIFIED NO APPLICATION CHANGE. Executed inspection only, no runtime test pass claimed. Next specify acceptance and isolate regressions via Java21/PostgreSQL16 CI.


## JR-072
Status at checkpoint: DONE. Specify Scope and rules of engagement acceptance.
- Specified exact leader PUT + member GET scope/RoE/date request and response, bounded text failures, missing CSRF 403, nonleader 403, outsider 404, no mutation and changedFields-only audit in DEC-2026-11-22-01. Existing product scope supports leader-managed IP/domain/CIDR targets. Actual target array has no validation; target grammar/limits are unresolved, so original invalid-target denial cannot be declared passing. Null dates leave existing dates intact. Recommended reversible test-only verification, no source-policy changes.
- Expected vs actual: leader ownership implemented; target validation criterion incomplete. Reviewer Builder Lead. Runtime NOT EXECUTED in this specification step. Plan new ScopeRulesIntegrationTest for positive persistence, role/CSRF/oversize negatives and explicit gap characterization under Java21/PostgreSQL16 CI. No owner decision required for tests; policy gap held IN REVIEW at closure.


## JR-073
Status at checkpoint: IN REVIEW. Reproduce Scope and rules of engagement success.
- Added ScopeRulesIntegrationTest.leaderScopeRulesAndDatesPersistAcrossFreshMemberReadWithPrivateAudit: isolated synthetic leader PUT of three targets (IP/CIDR/domain), dates and all five RoE fields; independent member GET simulates refresh; DB dates/targets and single leader audit asserted with private emergency-contact value absent from audit metadata. Expected HTTP 200, LEADER then MEMBER, identical persisted values. This is backend refresh evidence, not a browser screenshot or full UI edit proof.
- Actual result at checkpoint: test authored/reviewed, runtime NOT EXECUTED locally (Maven/Docker unavailable); Java21/PostgreSQL16 `cd backend && mvn -B clean verify` planned in GitHub CI. Preserve IN REVIEW until observed. No production source/JSON changes. Changed test plus register/log/handoff/Session08 evidence. Reviewer Builder Lead.


## JR-074
Status at checkpoint: IN REVIEW. Reject Scope and rules of engagement invalid operation.
- Added nonleadersMalformedBodiesAndOversizedRulesCannotMutateScopeOrAudit: active member PUT 403, outsider 404, anonymous/missing CSRF 403; malformed target object and date 400; every bounded RoE field at limit+1 400 with named fieldErrors. Asserts persisted dates/targets/all RoE, membership count and audit count unchanged. Separate characterizesUnresolvedTargetSyntaxValidationGap deliberately expects current 200 for malformed/blank target strings and verifies persistence; this is evidence of a GAP, never invalid-target denial success. No production policy invented.
- Test source reviewed; runtime NOT EXECUTED locally; pending Java21 PostgreSQL16 Actions `mvn -B clean verify`. Original JR074 remains IN REVIEW even if characterization passes; DEC-2026-11-22-01 target policy/size validation acceptance unresolved. No race claim or full UI screenshot. Changed ScopeRulesIntegrationTest and per-JR tracking. Reviewer Builder Lead.


## JR-075
Status at checkpoint: IN REVIEW. Close Scope and rules of engagement verification.
- Compared positive scope/date refresh+DB audit fixture against role/CSRF/malformed-body/bounded-text failures and unchanged persistence. Existing ScopePanel rendering regression remains relevant. JR073 runtime pending, JR074 unresolved invalid target string acceptance demonstrated in source characterization; full original success+negative closure cannot be DONE. Preserve IN REVIEW under DEC-2026-11-22-01, test-only approach, no production rollback needed beyond reverting added tests/docs (append correction rather than erase evidence).
- Actual checks: `git diff --check` exit0, source review. Maven/Docker unavailable locally; Java21/PostgreSQL16 CI result not yet complete, not represented as passing. Expected vs actual gap remains target grammar/bounds. Next independent leadership/team JRs eligible despite prior phase acceptance debt. Reviewer Builder Lead; tracking-only checkpoint.


## JR-076
Status at checkpoint: DONE. Inventory Leadership transfer and team UX.
- Canonical owners at 52b68dae: EngagementController POST /{id}/transfer-leadership and DELETE /{id}/members/{userId}, MembershipService @Transactional reasserts active leader; transfer updates old/new roles plus engagement.leader and emits TRANSFER_LEADERSHIP, removal soft-deletes active member and emits REMOVE_MEMBER, forbids leader removal. AuditEventListener BEFORE_COMMIT persists audit in same transaction. User IDs, not membership-row IDs, are the wire identifiers.
- TeamPanel renders server roster and receives isLeader; actual page EngagementDetail derives it from engagement.role. Transfer/remove success invalidates engagement and audit queries, then refetch supplies canonical role. Existing EngagementAuthzIntegrationTest checks transfer role consistency, nonmember/invalid ID/member denial and stale optimistic rollback; AuditLogIntegrationTest checks transfer/removal events and no failed-action audit. EngagementPanels.test.tsx covers button wiring but not role refresh. Missing combined HTTP old/new authorization, stale removal/team state + audit isolation and page-level refreshed controls. Inventory complete, no code/runtime changes in this JR. Expected source/test map found. Reviewer Builder Lead.


## JR-077
Status at checkpoint: DONE. Specify Leadership transfer and team UX acceptance.
- Specified valid member transfer204; canonical GET roles, exactly one DB leader, changed actor/IDs audit; old leader PUT/transfer/remove/audit denied, new leader succeeds; soft removal204 preserves history while excluding roster. Negative nonmember/removed target404, nonleader403, missing CSRF403, malformed400, repeated stale removal404, current-leader removal400, no state/audit side effects. DEC-2026-11-22-02 records recommendation and distinction between stale sequential request and simultaneous race.
- Existing optimistic rollback tests required; add backend combined HTTP/DB tests plus frontend actual QueryClient role-refresh regression. No permission/API or product-policy change. Acceptance specification DONE, runtime NOT EXECUTED in this JR. Reviewer Builder Lead, no subagents. Next reproduce positive.


## JR-078
Status at checkpoint: IN REVIEW. Reproduce Leadership transfer and team UX success.
- Added TeamWorkflowIntegrationTest.transferRefreshesPermissionsRosterAndSingleAuditThenRemovalPreservesHistory: real PostgreSQL16 HTTP transfer204; fresh old/new detail MEMBER/LEADER, DB FK + one active leader, precise one transfer audit actor/IDs; old leader PUT/remove/transfer/audit403 with no mutation; new leader PUT200/remove204/audit200; removed member404, roster excludes member but historical membership row preserved. Added EngagementTeamWorkflow.test.tsx with real React Query client: successful transfer invalidates/refetches server role and removes old leader team controls. Synthetic API mock for frontend only, backend endpoints not mocked.
- Expected above; actual source authored and reviewed, runtime NOT EXECUTED at checkpoint. Java21 PostgreSQL16 `mvn -B clean verify`, frontend `npm ci && npm run lint && npm run build && npm test` planned via Actions. No browser screenshot/full browser journey claimed; frontend is jsdom DOM regression. No production application behavior changed; IN REVIEW until actual runtime results. Reviewer Builder Lead.


## JR-079
Status at checkpoint: IN REVIEW. Reject Leadership transfer and team UX invalid operation.
- Added TeamWorkflowIntegrationTest.nonmemberTransferAndStaleRemovalLeaveLeaderStateAndAuditIntact: real existing outsider target transfer404, self-leader removal400, member removal403, missing-CSRF403 with no audit; legitimate removal204 followed by stale repeat404 and removed-target transfer404; asserts unchanged leader FK/active role count, member role/removal timestamp, history row count, preserved authored DRAFT log, exactly one legitimate removal audit. Existing stale optimistic-lock rollback and failed-action audit tests retained. These are sequential stale requests, not a simultaneous concurrency test.
- Expected safe denials and no unintended side effects; actual test authored/reviewed, runtime NOT EXECUTED at checkpoint; PostgreSQL16 CI pending. Frontend prior role-refresh test remains pending. Production services/API/auth/export unchanged. IN REVIEW until actual runtime checks, reviewer Builder Lead.


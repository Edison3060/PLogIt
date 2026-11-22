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


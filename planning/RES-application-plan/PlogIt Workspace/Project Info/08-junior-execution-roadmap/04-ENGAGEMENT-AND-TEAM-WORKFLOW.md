# Phase 04 — Engagement and team workflow

**Prerequisite:** Pass Phase 03 with recorded evidence. **Exit gate:** Keep already-working engagement features intact with regression checks.

**Existing-code policy:** PLogIt is built. Each JR is a verification or separately approved correction; do not recreate a feature or edit the copied RES source. **All JR execution statuses begin NOT STARTED**, even where implementation exists.

### JR-061 — Inventory Engagement create/list/detail

- **Where:** `web/EngagementController.java and service/EngagementService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `web/EngagementController.java and service/EngagementService.java`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-062 — Specify Engagement create/list/detail acceptance

- **Where:** `web/EngagementController.java and service/EngagementService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: An authenticated owner creates one engagement and sees only authorized resources. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-063 — Reproduce Engagement create/list/detail success

- **Where:** `web/EngagementController.java and service/EngagementService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Create, list and fetch engagement; compare serialized fields to schema. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Create, list and fetch engagement; compare serialized fields to schema. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-064 — Reject Engagement create/list/detail invalid operation

- **Where:** `web/EngagementController.java and service/EngagementService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Duplicate/invalid create and unauthorized list/detail cause no foreign disclosure. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Duplicate/invalid create and unauthorized list/detail cause no foreign disclosure. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-065 — Close Engagement create/list/detail verification

- **Where:** `web/EngagementController.java and service/EngagementService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-066 — Inventory Join codes and membership

- **Where:** `service/JoinCodeService.java and service/MembershipService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `service/JoinCodeService.java and service/MembershipService.java`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-067 — Specify Join codes and membership acceptance

- **Where:** `service/JoinCodeService.java and service/MembershipService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Code redemption creates one correct-role membership and revocation blocks future joins. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-068 — Reproduce Join codes and membership success

- **Where:** `service/JoinCodeService.java and service/MembershipService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Generate code, join as member and verify team roster reflects exactly one record. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Generate code, join as member and verify team roster reflects exactly one record. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-069 — Reject Join codes and membership invalid operation

- **Where:** `service/JoinCodeService.java and service/MembershipService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Expired/revoked code and replayed join cannot create duplicate memberships. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Expired/revoked code and replayed join cannot create duplicate memberships. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-070 — Close Join codes and membership verification

- **Where:** `service/JoinCodeService.java and service/MembershipService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-071 — Inventory Scope and rules of engagement

- **Where:** `frontend/src/components/engagement/ScopePanel.tsx and engagement DTOs` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `frontend/src/components/engagement/ScopePanel.tsx and engagement DTOs`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-072 — Specify Scope and rules of engagement acceptance

- **Where:** `frontend/src/components/engagement/ScopePanel.tsx and engagement DTOs` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Only leader may edit in/out of scope, dates, permitted techniques and emergency contacts. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-073 — Reproduce Scope and rules of engagement success

- **Where:** `frontend/src/components/engagement/ScopePanel.tsx and engagement DTOs` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Edit valid scope as leader and verify persistence after refresh. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Edit valid scope as leader and verify persistence after refresh. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-074 — Reject Scope and rules of engagement invalid operation

- **Where:** `frontend/src/components/engagement/ScopePanel.tsx and engagement DTOs` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Attempt invalid target, oversized value and nonleader edit; expect safe denial. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Attempt invalid target, oversized value and nonleader edit; expect safe denial. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-075 — Close Scope and rules of engagement verification

- **Where:** `frontend/src/components/engagement/ScopePanel.tsx and engagement DTOs` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-076 — Inventory Leadership transfer and team UX

- **Where:** `service/EngagementService.java and frontend/src/components/engagement/TeamPanel.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `service/EngagementService.java and frontend/src/components/engagement/TeamPanel.tsx`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-077 — Specify Leadership transfer and team UX acceptance

- **Where:** `service/EngagementService.java and frontend/src/components/engagement/TeamPanel.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Transfer/remove effects are atomic and server-owned; UI follows canonical role. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-078 — Reproduce Leadership transfer and team UX success

- **Where:** `service/EngagementService.java and frontend/src/components/engagement/TeamPanel.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Transfer to valid member and confirm old/new permissions, team state and audit entry. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Transfer to valid member and confirm old/new permissions, team state and audit entry. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-079 — Reject Leadership transfer and team UX invalid operation

- **Where:** `service/EngagementService.java and frontend/src/components/engagement/TeamPanel.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Reject transfer to nonmember and stale removal without corrupting leader state. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Reject transfer to nonmember and stale removal without corrupting leader state. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

### JR-080 — Close Leadership transfer and team UX verification

- **Where:** `service/EngagementService.java and frontend/src/components/engagement/TeamPanel.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not change existing UI or data semantics without a documented product decision.

## Phase sign-off

Keep already-working engagement features intact with regression checks. Re-run affected prior-phase tests and record the SHA, isolated environment, redacted logs and rollback. A past historical "done" does not satisfy this new gate.

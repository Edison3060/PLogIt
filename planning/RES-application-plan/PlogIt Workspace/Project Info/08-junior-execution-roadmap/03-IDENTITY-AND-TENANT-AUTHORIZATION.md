# Phase 03 — Identity and tenant authorization

**Prerequisite:** Pass Phase 02 with recorded evidence. **Exit gate:** Protect every operation through server checks and demonstrated denial tests.

**Existing-code policy:** PLogIt is built. Each JR is a verification or separately approved correction; do not recreate a feature or edit the copied RES source. **All JR execution statuses begin NOT STARTED**, even where implementation exists.

### JR-041 — Inventory Authentication session

- **Where:** `web/AuthController.java and frontend/src/hooks/useAuth.ts` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `web/AuthController.java and frontend/src/hooks/useAuth.ts`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-042 — Specify Authentication session acceptance

- **Where:** `web/AuthController.java and frontend/src/hooks/useAuth.ts` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Registration, hashed password, login, logout and cookie scope follow server rules. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-043 — Reproduce Authentication session success

- **Where:** `web/AuthController.java and frontend/src/hooks/useAuth.ts` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Create user, login, access authenticated route and log out; inspect session invalidation. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Create user, login, access authenticated route and log out; inspect session invalidation. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-044 — Reject Authentication session invalid operation

- **Where:** `web/AuthController.java and frontend/src/hooks/useAuth.ts` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Try expired session and reused logout cookie; verify denied. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Try expired session and reused logout cookie; verify denied. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-045 — Close Authentication session verification

- **Where:** `web/AuthController.java and frontend/src/hooks/useAuth.ts` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-046 — Inventory CSRF enforcement

- **Where:** `backend security configuration, web endpoints and frontend/src/lib/api.ts` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `backend security configuration, web endpoints and frontend/src/lib/api.ts`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-047 — Specify CSRF enforcement acceptance

- **Where:** `backend security configuration, web endpoints and frontend/src/lib/api.ts` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Mutating endpoints require correct CSRF token in the real HTTP path. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-048 — Reproduce CSRF enforcement success

- **Where:** `backend security configuration, web endpoints and frontend/src/lib/api.ts` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Perform authenticated POST with genuine token and verify accepted. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Perform authenticated POST with genuine token and verify accepted. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-049 — Reject CSRF enforcement invalid operation

- **Where:** `backend security configuration, web endpoints and frontend/src/lib/api.ts` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Repeat without/mismatched token and assert no data mutation. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Repeat without/mismatched token and assert no data mutation. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-050 — Close CSRF enforcement verification

- **Where:** `backend security configuration, web endpoints and frontend/src/lib/api.ts` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-051 — Inventory Engagement membership boundary

- **Where:** `service/MembershipService.java and service/EngagementService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `service/MembershipService.java and service/EngagementService.java`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-052 — Specify Engagement membership boundary acceptance

- **Where:** `service/MembershipService.java and service/EngagementService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Leader and member roles are scoped to one engagement. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-053 — Reproduce Engagement membership boundary success

- **Where:** `service/MembershipService.java and service/EngagementService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Member can see permitted own engagement and leader can edit allowed fields. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Member can see permitted own engagement and leader can edit allowed fields. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-054 — Reject Engagement membership boundary invalid operation

- **Where:** `service/MembershipService.java and service/EngagementService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Cross-engagement access, unauthorized leader action and removed membership rejected. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Cross-engagement access, unauthorized leader action and removed membership rejected. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-055 — Close Engagement membership boundary verification

- **Where:** `service/MembershipService.java and service/EngagementService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-056 — Inventory Auth abuse controls

- **Where:** `security/AuthRateLimitInterceptor.java and backend auth tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `security/AuthRateLimitInterceptor.java and backend auth tests`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-057 — Specify Auth abuse controls acceptance

- **Where:** `security/AuthRateLimitInterceptor.java and backend auth tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Per-user/IP login controls and safe errors do not reveal credential state. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-058 — Reproduce Auth abuse controls success

- **Where:** `security/AuthRateLimitInterceptor.java and backend auth tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Normal login throughput succeeds and audit/error responses are bounded. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Normal login throughput succeeds and audit/error responses are bounded. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-059 — Reject Auth abuse controls invalid operation

- **Where:** `security/AuthRateLimitInterceptor.java and backend auth tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Burst invalid logins; confirm throttle without locking out unrelated legitimate users. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Burst invalid logins; confirm throttle without locking out unrelated legitimate users. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

### JR-060 — Close Auth abuse controls verification

- **Where:** `security/AuthRateLimitInterceptor.java and backend auth tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** UI visibility is not authorization; never disable CSRF or rate limits to pass tests.

## Phase sign-off

Protect every operation through server checks and demonstrated denial tests. Re-run affected prior-phase tests and record the SHA, isolated environment, redacted logs and rollback. A past historical "done" does not satisfy this new gate.

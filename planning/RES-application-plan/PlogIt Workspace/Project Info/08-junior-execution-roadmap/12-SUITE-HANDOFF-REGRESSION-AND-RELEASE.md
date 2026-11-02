# Phase 12 — Suite handoff regression and release

**Prerequisite:** Pass Phase 11 with recorded evidence. **Exit gate:** Prove a real producer-to-consumer fixture, not just aligned prose.

**Existing-code policy:** PLogIt is built. Each JR is a verification or separately approved correction; do not recreate a feature or edit the copied RES source. **All JR execution statuses begin NOT STARTED**, even where implementation exists.

### JR-221 — Inventory Real legacy evidence fixture

- **Where:** `PLogIt ExportService integration fixture and JudgeIt import contract tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `PLogIt ExportService integration fixture and JudgeIt import contract tests`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-222 — Specify Real legacy evidence fixture acceptance

- **Where:** `PLogIt ExportService integration fixture and JudgeIt import contract tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: JudgeIt consumes exact current unversioned JSON rather than idealized evidence-pack fields. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-223 — Reproduce Real legacy evidence fixture success

- **Where:** `PLogIt ExportService integration fixture and JudgeIt import contract tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Generate authentic fixture then import as authorized auditor into isolated JudgeIt test DB. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Generate authentic fixture then import as authorized auditor into isolated JudgeIt test DB. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-224 — Reject Real legacy evidence fixture invalid operation

- **Where:** `PLogIt ExportService integration fixture and JudgeIt import contract tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Malformed and duplicate fixture cause safe reject or dedupe without phantom finding. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Malformed and duplicate fixture cause safe reject or dedupe without phantom finding. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-225 — Close Real legacy evidence fixture verification

- **Where:** `PLogIt ExportService integration fixture and JudgeIt import contract tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-226 — Inventory Identity and cross-tenant provenance

- **Where:** `PLogIt UUID output and JudgeIt source evidence records` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `PLogIt UUID output and JudgeIt source evidence records`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-227 — Specify Identity and cross-tenant provenance acceptance

- **Where:** `PLogIt UUID output and JudgeIt source evidence records` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Source log UUID never changes and destination has distinct finding ID and tenant/source namespace. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-228 — Reproduce Identity and cross-tenant provenance success

- **Where:** `PLogIt UUID output and JudgeIt source evidence records` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Trace same UUID through PLogIt export to JudgeIt imported source. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Trace same UUID through PLogIt export to JudgeIt imported source. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-229 — Reject Identity and cross-tenant provenance invalid operation

- **Where:** `PLogIt UUID output and JudgeIt source evidence records` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Cross-tenant replay or UUID collision cannot disclose or merge data. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Cross-tenant replay or UUID collision cannot disclose or merge data. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-230 — Close Identity and cross-tenant provenance verification

- **Where:** `PLogIt UUID output and JudgeIt source evidence records` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-231 — Inventory Full app regression and security gate

- **Where:** `backend Maven tests, frontend npm scripts, HTTP auth tests and CI scanners` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `backend Maven tests, frontend npm scripts, HTTP auth tests and CI scanners`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-232 — Specify Full app regression and security gate acceptance

- **Where:** `backend Maven tests, frontend npm scripts, HTTP auth tests and CI scanners` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: All required release gates run on one exact source revision. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-233 — Reproduce Full app regression and security gate success

- **Where:** `backend Maven tests, frontend npm scripts, HTTP auth tests and CI scanners` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Run Maven, npm lint/build/Vitest, CSRF/authz and Semgrep/Trivy with logs. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Run Maven, npm lint/build/Vitest, CSRF/authz and Semgrep/Trivy with logs. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-234 — Reject Full app regression and security gate invalid operation

- **Where:** `backend Maven tests, frontend npm scripts, HTTP auth tests and CI scanners` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Any failed mandatory check blocks release rather than receiving cosmetic waiver. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Any failed mandatory check blocks release rather than receiving cosmetic waiver. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-235 — Close Full app regression and security gate verification

- **Where:** `backend Maven tests, frontend npm scripts, HTTP auth tests and CI scanners` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-236 — Inventory Final evidence and next-session handoff

- **Where:** `PLogIt Workspace/Project Info/HANDOFF.md and PROGRESS-TRACKER.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `PLogIt Workspace/Project Info/HANDOFF.md and PROGRESS-TRACKER.md`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-237 — Specify Final evidence and next-session handoff acceptance

- **Where:** `PLogIt Workspace/Project Info/HANDOFF.md and PROGRESS-TRACKER.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Reviewer receives accepted task IDs, outstanding proposed PRs, deploy status and rollback references. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-238 — Reproduce Final evidence and next-session handoff success

- **Where:** `PLogIt Workspace/Project Info/HANDOFF.md and PROGRESS-TRACKER.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: New junior resumes next incomplete JR solely from written evidence. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: New junior resumes next incomplete JR solely from written evidence. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-239 — Reject Final evidence and next-session handoff invalid operation

- **Where:** `PLogIt Workspace/Project Info/HANDOFF.md and PROGRESS-TRACKER.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Missing production proof, unapproved contract change or incomplete JudgeIt tests stay BLOCKED. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Missing production proof, unapproved contract change or incomplete JudgeIt tests stay BLOCKED. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

### JR-240 — Close Final evidence and next-session handoff verification

- **Where:** `PLogIt Workspace/Project Info/HANDOFF.md and PROGRESS-TRACKER.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** An unbuilt JudgeIt importer is a dependency blocker, not a passing integration test.

## Phase sign-off

Prove a real producer-to-consumer fixture, not just aligned prose. Re-run affected prior-phase tests and record the SHA, isolated environment, redacted logs and rollback. A past historical "done" does not satisfy this new gate.

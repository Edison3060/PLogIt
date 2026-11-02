# Phase 09 — Database integrity and recoverability

**Prerequisite:** Pass Phase 08 with recorded evidence. **Exit gate:** Keep state and evidence durable with tested restore behavior.

**Existing-code policy:** PLogIt is built. Each JR is a verification or separately approved correction; do not recreate a feature or edit the copied RES source. **All JR execution statuses begin NOT STARTED**, even where implementation exists.

### JR-161 — Inventory Schema integrity constraints

- **Where:** `backend/src/main/resources/db/migration and domain entities` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `backend/src/main/resources/db/migration and domain entities`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-162 — Specify Schema integrity constraints acceptance

- **Where:** `backend/src/main/resources/db/migration and domain entities` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: FKs, unique indexes and allowed enums keep memberships/logs/versions consistent. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-163 — Reproduce Schema integrity constraints success

- **Where:** `backend/src/main/resources/db/migration and domain entities` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Apply all migrations to empty and seeded DB, inspect constraints. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Apply all migrations to empty and seeded DB, inspect constraints. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-164 — Reject Schema integrity constraints invalid operation

- **Where:** `backend/src/main/resources/db/migration and domain entities` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Force duplicate key and invalid foreign key; partial mutation rolled back. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Force duplicate key and invalid foreign key; partial mutation rolled back. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-165 — Close Schema integrity constraints verification

- **Where:** `backend/src/main/resources/db/migration and domain entities` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-166 — Inventory Transactional business actions

- **Where:** `service/EngagementService.java, LogReviewService.java and ExportService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `service/EngagementService.java, LogReviewService.java and ExportService.java`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-167 — Specify Transactional business actions acceptance

- **Where:** `service/EngagementService.java, LogReviewService.java and ExportService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Leadership, review and export state changes commit atomically with audit. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-168 — Reproduce Transactional business actions success

- **Where:** `service/EngagementService.java, LogReviewService.java and ExportService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Commit valid operation and re-read business record and audit rows. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Commit valid operation and re-read business record and audit rows. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-169 — Reject Transactional business actions invalid operation

- **Where:** `service/EngagementService.java, LogReviewService.java and ExportService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Inject exception between writes, verify no partial state or orphan audit. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Inject exception between writes, verify no partial state or orphan audit. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-170 — Close Transactional business actions verification

- **Where:** `service/EngagementService.java, LogReviewService.java and ExportService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-171 — Inventory Database and attachment backup

- **Where:** `docker-compose and operational backup plan` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `docker-compose and operational backup plan`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-172 — Specify Database and attachment backup acceptance

- **Where:** `docker-compose and operational backup plan` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Restore process covers PostgreSQL and separate image volume with consistent checkpoint. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-173 — Reproduce Database and attachment backup success

- **Where:** `docker-compose and operational backup plan` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Restore synthetic engagement, logs, versions and image, compare counts/checksums. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Restore synthetic engagement, logs, versions and image, compare counts/checksums. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-174 — Reject Database and attachment backup invalid operation

- **Where:** `docker-compose and operational backup plan` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Missing media volume or incompatible migration detected before production promotion. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Missing media volume or incompatible migration detected before production promotion. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-175 — Close Database and attachment backup verification

- **Where:** `docker-compose and operational backup plan` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-176 — Inventory Health, logging and incident recovery

- **Where:** `web/HealthController.java and deployment runbook` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `web/HealthController.java and deployment runbook`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-177 — Specify Health, logging and incident recovery acceptance

- **Where:** `web/HealthController.java and deployment runbook` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Health endpoints, structured error logs and restart sequence expose genuine faults without secrets. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-178 — Reproduce Health, logging and incident recovery success

- **Where:** `web/HealthController.java and deployment runbook` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Start/restart app, check dependency status and redacted correlation IDs. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Start/restart app, check dependency status and redacted correlation IDs. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-179 — Reject Health, logging and incident recovery invalid operation

- **Where:** `web/HealthController.java and deployment runbook` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Unavailable DB/storage produces bounded failure; no silent fallback or secret logging. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Unavailable DB/storage produces bounded failure; no silent fallback or secret logging. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

### JR-180 — Close Health, logging and incident recovery verification

- **Where:** `web/HealthController.java and deployment runbook` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A successful UI response is not proof of atomic or restorable persistence.

## Phase sign-off

Keep state and evidence durable with tested restore behavior. Re-run affected prior-phase tests and record the SHA, isolated environment, redacted logs and rollback. A past historical "done" does not satisfy this new gate.

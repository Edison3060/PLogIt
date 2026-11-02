# Phase 05 — Logs, review states and audit

**Prerequisite:** Pass Phase 04 with recorded evidence. **Exit gate:** Preserve the complete observed capture and approval lifecycle.

**Existing-code policy:** PLogIt is built. Each JR is a verification or separately approved correction; do not recreate a feature or edit the copied RES source. **All JR execution statuses begin NOT STARTED**, even where implementation exists.

### JR-081 — Inventory Log authoring and edit rules

- **Where:** `service/LogService.java and web/LogController.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `service/LogService.java and web/LogController.java`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-082 — Specify Log authoring and edit rules acceptance

- **Where:** `service/LogService.java and web/LogController.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Log has stable UUID, author, engagement, target, Markdown, tags and draft-only edits. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-083 — Reproduce Log authoring and edit rules success

- **Where:** `service/LogService.java and web/LogController.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Create draft and update own draft, then re-query fields and version. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Create draft and update own draft, then re-query fields and version. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-084 — Reject Log authoring and edit rules invalid operation

- **Where:** `service/LogService.java and web/LogController.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Invalid activity, cross-user draft edit or post-submit edit rejected. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Invalid activity, cross-user draft edit or post-submit edit rejected. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-085 — Close Log authoring and edit rules verification

- **Where:** `service/LogService.java and web/LogController.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-086 — Inventory Review state machine

- **Where:** `service/LogReviewService.java and domain review classes` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `service/LogReviewService.java and domain review classes`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-087 — Specify Review state machine acceptance

- **Where:** `service/LogReviewService.java and domain review classes` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Draft→Submitted→Approved/Rejected follows per-role transition rules. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-088 — Reproduce Review state machine success

- **Where:** `service/LogReviewService.java and domain review classes` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Author submits and leader approves valid log once. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Author submits and leader approves valid log once. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-089 — Reject Review state machine invalid operation

- **Where:** `service/LogReviewService.java and domain review classes` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Nonleader approval, missing reject comment or invalid transition leaves state intact. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Nonleader approval, missing reject comment or invalid transition leaves state intact. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-090 — Close Review state machine verification

- **Where:** `service/LogReviewService.java and domain review classes` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-091 — Inventory Versioning and concurrency

- **Where:** `service/LogVersionService.java and db/migration/V3__unique_log_version_numbers.sql` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `service/LogVersionService.java and db/migration/V3__unique_log_version_numbers.sql`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-092 — Specify Versioning and concurrency acceptance

- **Where:** `service/LogVersionService.java and db/migration/V3__unique_log_version_numbers.sql` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Edit history is immutable and optimistic locking rejects stale saves. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-093 — Reproduce Versioning and concurrency success

- **Where:** `service/LogVersionService.java and db/migration/V3__unique_log_version_numbers.sql` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Create two versions and confirm ordered snapshots and unique numbers. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Create two versions and confirm ordered snapshots and unique numbers. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-094 — Reject Versioning and concurrency invalid operation

- **Where:** `service/LogVersionService.java and db/migration/V3__unique_log_version_numbers.sql` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Simultaneous stale writers cannot overwrite latest or duplicate version index. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Simultaneous stale writers cannot overwrite latest or duplicate version index. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-095 — Close Versioning and concurrency verification

- **Where:** `service/LogVersionService.java and db/migration/V3__unique_log_version_numbers.sql` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-096 — Inventory Audit and log board

- **Where:** `service/AuditEventListener.java, frontend/src/pages/LogBoard.tsx and LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `service/AuditEventListener.java, frontend/src/pages/LogBoard.tsx and LogDetail.tsx`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-097 — Specify Audit and log board acceptance

- **Where:** `service/AuditEventListener.java, frontend/src/pages/LogBoard.tsx and LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Transactional audit events and review UI derive from committed server state. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-098 — Reproduce Audit and log board success

- **Where:** `service/AuditEventListener.java, frontend/src/pages/LogBoard.tsx and LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Observe history, filters, submission and leader review in browser and DB. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Observe history, filters, submission and leader review in browser and DB. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-099 — Reject Audit and log board invalid operation

- **Where:** `service/AuditEventListener.java, frontend/src/pages/LogBoard.tsx and LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Force rollback, denied review and stale UI event; no orphan audit or phantom approval. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Force rollback, denied review and stale UI event; no orphan audit or phantom approval. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

### JR-100 — Close Audit and log board verification

- **Where:** `service/AuditEventListener.java, frontend/src/pages/LogBoard.tsx and LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No client may set review state directly or fabricate another author's version.

## Phase sign-off

Preserve the complete observed capture and approval lifecycle. Re-run affected prior-phase tests and record the SHA, isolated environment, redacted logs and rollback. A past historical "done" does not satisfy this new gate.

# Phase 01 — Source baseline and controlled scope

**Prerequisite:** Read suite master spec, 2026 research, current source and standing rules. **Exit gate:** Freeze code and approved requirements before suggesting changes.

**Existing-code policy:** PLogIt is built. Each JR is a verification or separately approved correction; do not recreate a feature or edit the copied RES source. **All JR execution statuses begin NOT STARTED**, even where implementation exists.

### JR-001 — Inventory Canonical source identity

- **Where:** `Edison3060/PLogIt main and PLogIt Workspace/PLogIt` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `Edison3060/PLogIt main and PLogIt Workspace/PLogIt`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-002 — Specify Canonical source identity acceptance

- **Where:** `Edison3060/PLogIt main and PLogIt Workspace/PLogIt` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Standalone main at last-reviewed 28aa156 is authoritative; compare copied snapshot and record divergence. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-003 — Reproduce Canonical source identity success

- **Where:** `Edison3060/PLogIt main and PLogIt Workspace/PLogIt` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Resolve canonical SHA, copied tree content and review baseline. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Resolve canonical SHA, copied tree content and review baseline. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-004 — Reject Canonical source identity invalid operation

- **Where:** `Edison3060/PLogIt main and PLogIt Workspace/PLogIt` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Reject assuming the RES copy is an independent development fork. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Reject assuming the RES copy is an independent development fork. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-005 — Close Canonical source identity verification

- **Where:** `Edison3060/PLogIt main and PLogIt Workspace/PLogIt` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-006 — Inventory Product role boundary

- **Where:** `Suite - Master Spec.md and PLogIt - Product Spec.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `Suite - Master Spec.md and PLogIt - Product Spec.md`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-007 — Specify Product role boundary acceptance

- **Where:** `Suite - Master Spec.md and PLogIt - Product Spec.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: PLogIt records activity and approved evidence, not scoring or remediation. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-008 — Reproduce Product role boundary success

- **Where:** `Suite - Master Spec.md and PLogIt - Product Spec.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Trace log to leader export and identify owner of each subsequent suite stage. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Trace log to leader export and identify owner of each subsequent suite stage. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-009 — Reject Product role boundary invalid operation

- **Where:** `Suite - Master Spec.md and PLogIt - Product Spec.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Reject a proposed PLogIt finding/CVSS/ticket feature as out of scope. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Reject a proposed PLogIt finding/CVSS/ticket feature as out of scope. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-010 — Close Product role boundary verification

- **Where:** `Suite - Master Spec.md and PLogIt - Product Spec.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-011 — Inventory Implemented API and UI inventory

- **Where:** `backend/src/main/java/io/muzoo/ssc/plogit/web and frontend/src/App.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `backend/src/main/java/io/muzoo/ssc/plogit/web and frontend/src/App.tsx`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-012 — Specify Implemented API and UI inventory acceptance

- **Where:** `backend/src/main/java/io/muzoo/ssc/plogit/web and frontend/src/App.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Enumerate actual controllers, routes, entities, tests and deferred features. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-013 — Reproduce Implemented API and UI inventory success

- **Where:** `backend/src/main/java/io/muzoo/ssc/plogit/web and frontend/src/App.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Map every shipped page to its backend endpoint and existing tests. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Map every shipped page to its backend endpoint and existing tests. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-014 — Reject Implemented API and UI inventory invalid operation

- **Where:** `backend/src/main/java/io/muzoo/ssc/plogit/web and frontend/src/App.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Flag historical July claims unsupported by source as unverified. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Flag historical July claims unsupported by source as unverified. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-015 — Close Implemented API and UI inventory verification

- **Where:** `backend/src/main/java/io/muzoo/ssc/plogit/web and frontend/src/App.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-016 — Inventory Open decisions and rebaseline

- **Where:** `Suite - Research Refresh 2026-11-02.md and Suite - To Be Scoped.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `Suite - Research Refresh 2026-11-02.md and Suite - To Be Scoped.md`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-017 — Specify Open decisions and rebaseline acceptance

- **Where:** `Suite - Research Refresh 2026-11-02.md and Suite - To Be Scoped.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Record unresolved log inclusion, media packaging, tenant identity, privacy and tech-stack choices. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-018 — Reproduce Open decisions and rebaseline success

- **Where:** `Suite - Research Refresh 2026-11-02.md and Suite - To Be Scoped.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Create dated fact/assumption/approval register with exact source links. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Create dated fact/assumption/approval register with exact source links. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-019 — Reject Open decisions and rebaseline invalid operation

- **Where:** `Suite - Research Refresh 2026-11-02.md and Suite - To Be Scoped.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Mark unsupported security, production or schema assumptions BLOCKED. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Mark unsupported security, production or schema assumptions BLOCKED. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

### JR-020 — Close Open decisions and rebaseline verification

- **Where:** `Suite - Research Refresh 2026-11-02.md and Suite - To Be Scoped.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** No edits to the RES-copied PLogIt implementation; code work requires separate approved PRs.

## Phase sign-off

Freeze code and approved requirements before suggesting changes. Re-run affected prior-phase tests and record the SHA, isolated environment, redacted logs and rollback. A past historical "done" does not satisfy this new gate.

# Phase 08 — Frontend journeys and accessibility

**Prerequisite:** Pass Phase 07 with recorded evidence. **Exit gate:** Verify actual web usability rather than only green unit tests.

**Existing-code policy:** PLogIt is built. Each JR is a verification or separately approved correction; do not recreate a feature or edit the copied RES source. **All JR execution statuses begin NOT STARTED**, even where implementation exists.

### JR-141 — Inventory Navigation and sessions

- **Where:** `frontend/src/App.tsx and frontend/src/components/AppShell.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `frontend/src/App.tsx and frontend/src/components/AppShell.tsx`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-142 — Specify Navigation and sessions acceptance

- **Where:** `frontend/src/App.tsx and frontend/src/components/AppShell.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Protected routes, sign-in redirects and logout states are consistent. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-143 — Reproduce Navigation and sessions success

- **Where:** `frontend/src/App.tsx and frontend/src/components/AppShell.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Navigate authenticated dashboard, engagement and logs on desktop and mobile widths. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Navigate authenticated dashboard, engagement and logs on desktop and mobile widths. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-144 — Reject Navigation and sessions invalid operation

- **Where:** `frontend/src/App.tsx and frontend/src/components/AppShell.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Reload with expired cookie and verify no stale protected page or leaked data. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Reload with expired cookie and verify no stale protected page or leaked data. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-145 — Close Navigation and sessions verification

- **Where:** `frontend/src/App.tsx and frontend/src/components/AppShell.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-146 — Inventory Engagement pages

- **Where:** `frontend/src/pages/Dashboard.tsx, EngagementsPage.tsx and EngagementDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `frontend/src/pages/Dashboard.tsx, EngagementsPage.tsx and EngagementDetail.tsx`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-147 — Specify Engagement pages acceptance

- **Where:** `frontend/src/pages/Dashboard.tsx, EngagementsPage.tsx and EngagementDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Create/join/list and detail tabs handle loading, empty, success and error states. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-148 — Reproduce Engagement pages success

- **Where:** `frontend/src/pages/Dashboard.tsx, EngagementsPage.tsx and EngagementDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Complete create→join→scope/team user journey and refresh. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Complete create→join→scope/team user journey and refresh. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-149 — Reject Engagement pages invalid operation

- **Where:** `frontend/src/pages/Dashboard.tsx, EngagementsPage.tsx and EngagementDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Denied endpoint or empty roster gives accessible error rather than false success. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Denied endpoint or empty roster gives accessible error rather than false success. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-150 — Close Engagement pages verification

- **Where:** `frontend/src/pages/Dashboard.tsx, EngagementsPage.tsx and EngagementDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-151 — Inventory Log forms and review UI

- **Where:** `frontend/src/pages/LogForm.tsx, LogBoard.tsx and LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `frontend/src/pages/LogForm.tsx, LogBoard.tsx and LogDetail.tsx`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-152 — Specify Log forms and review UI acceptance

- **Where:** `frontend/src/pages/LogForm.tsx, LogBoard.tsx and LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Draft, submit, approve/reject, filters and Markdown display match backend roles. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-153 — Reproduce Log forms and review UI success

- **Where:** `frontend/src/pages/LogForm.tsx, LogBoard.tsx and LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Use keyboard to create, submit and review log with correct state and evidence. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Use keyboard to create, submit and review log with correct state and evidence. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-154 — Reject Log forms and review UI invalid operation

- **Where:** `frontend/src/pages/LogForm.tsx, LogBoard.tsx and LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Concurrent update and role-restricted actions show correct error without dropped draft. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Concurrent update and role-restricted actions show correct error without dropped draft. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-155 — Close Log forms and review UI verification

- **Where:** `frontend/src/pages/LogForm.tsx, LogBoard.tsx and LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-156 — Inventory Themes, devices and exports

- **Where:** `frontend/tailwind.config.js, styles.css and components/engagement/ExportDialog.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `frontend/tailwind.config.js, styles.css and components/engagement/ExportDialog.tsx`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-157 — Specify Themes, devices and exports acceptance

- **Where:** `frontend/tailwind.config.js, styles.css and components/engagement/ExportDialog.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Light/dark theme, focus, contrast, small-screen layout and export clarity meet chosen accessibility checks. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-158 — Reproduce Themes, devices and exports success

- **Where:** `frontend/tailwind.config.js, styles.css and components/engagement/ExportDialog.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Test keyboard, narrow viewport, both themes and PDF/JSON/CSV dialog. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Test keyboard, narrow viewport, both themes and PDF/JSON/CSV dialog. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-159 — Reject Themes, devices and exports invalid operation

- **Where:** `frontend/tailwind.config.js, styles.css and components/engagement/ExportDialog.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Check overflow, invisible focus, invalid download and misleading inclusion text. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Check overflow, invisible focus, invalid download and misleading inclusion text. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

### JR-160 — Close Themes, devices and exports verification

- **Where:** `frontend/tailwind.config.js, styles.css and components/engagement/ExportDialog.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never substitute screenshots for state, authorization and keyboard tests.

## Phase sign-off

Verify actual web usability rather than only green unit tests. Re-run affected prior-phase tests and record the SHA, isolated environment, redacted logs and rollback. A past historical "done" does not satisfy this new gate.

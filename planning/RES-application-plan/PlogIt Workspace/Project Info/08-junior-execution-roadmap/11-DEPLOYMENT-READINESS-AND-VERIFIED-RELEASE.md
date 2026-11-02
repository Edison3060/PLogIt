# Phase 11 — Deployment readiness and verified release

**Prerequisite:** Pass Phase 10 with recorded evidence. **Exit gate:** Address blocked CD safely, with real host identity and observable behavior.

**Existing-code policy:** PLogIt is built. Each JR is a verification or separately approved correction; do not recreate a feature or edit the copied RES source. **All JR execution statuses begin NOT STARTED**, even where implementation exists.

### JR-201 — Inventory SSH host trust preflight

- **Where:** `.github/workflows/ci.yml and authenticated server console` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `.github/workflows/ci.yml and authenticated server console`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-202 — Specify SSH host trust preflight acceptance

- **Where:** `.github/workflows/ci.yml and authenticated server console` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: PROD_SSH_HOST_FINGERPRINT must match independently authenticated production host identity. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-203 — Reproduce SSH host trust preflight success

- **Where:** `.github/workflows/ci.yml and authenticated server console` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Verify host fingerprint from trusted console, properly scoped secret and fail-closed check. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Verify host fingerprint from trusted console, properly scoped secret and fail-closed check. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-204 — Reject SSH host trust preflight invalid operation

- **Where:** `.github/workflows/ci.yml and authenticated server console` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Missing or wrong fingerprint blocks deploy without guessing a value. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Missing or wrong fingerprint blocks deploy without guessing a value. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-205 — Close SSH host trust preflight verification

- **Where:** `.github/workflows/ci.yml and authenticated server console` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-206 — Inventory Server topology and reverse proxy

- **Where:** `authorized production host, Nginx/Caddy configuration and compose override` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `authorized production host, Nginx/Caddy configuration and compose override`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-207 — Specify Server topology and reverse proxy acceptance

- **Where:** `authorized production host, Nginx/Caddy configuration and compose override` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Historical Nginx claims and repo Caddy template are not proof of current topology. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-208 — Reproduce Server topology and reverse proxy success

- **Where:** `authorized production host, Nginx/Caddy configuration and compose override` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Document true server path, proxy, loopback binds, TLS and migration state. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Document true server path, proxy, loopback binds, TLS and migration state. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-209 — Reject Server topology and reverse proxy invalid operation

- **Where:** `authorized production host, Nginx/Caddy configuration and compose override` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Unverified server shell, stale checkout or public DB port remains BLOCKED. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Unverified server shell, stale checkout or public DB port remains BLOCKED. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-210 — Close Server topology and reverse proxy verification

- **Where:** `authorized production host, Nginx/Caddy configuration and compose override` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-211 — Inventory Live health and source parity

- **Where:** `GitHub Actions deployment log, server image digest and HTTPS health probe` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `GitHub Actions deployment log, server image digest and HTTPS health probe`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-212 — Specify Live health and source parity acceptance

- **Where:** `GitHub Actions deployment log, server image digest and HTTPS health probe` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: CI success, deploy, reachable and live-verified are four distinct statuses. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-213 — Reproduce Live health and source parity success

- **Where:** `GitHub Actions deployment log, server image digest and HTTPS health probe` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Compare target SHA, running revision, healthy endpoint, login smoke and TLS certificate. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Compare target SHA, running revision, healthy endpoint, login smoke and TLS certificate. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-214 — Reject Live health and source parity invalid operation

- **Where:** `GitHub Actions deployment log, server image digest and HTTPS health probe` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Wrong revision, failed health or stale HTTPS prohibits DONE. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Wrong revision, failed health or stale HTTPS prohibits DONE. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-215 — Close Live health and source parity verification

- **Where:** `GitHub Actions deployment log, server image digest and HTTPS health probe` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-216 — Inventory Rollback and recovery approval

- **Where:** `deployment runbook, DB/media backup and release tracker` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `deployment runbook, DB/media backup and release tracker`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-217 — Specify Rollback and recovery approval acceptance

- **Where:** `deployment runbook, DB/media backup and release tracker` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Promotion requires tested backup/restore and route to previous release. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-218 — Reproduce Rollback and recovery approval success

- **Where:** `deployment runbook, DB/media backup and release tracker` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Execute documented rollback in isolated environment and record owner approval. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Execute documented rollback in isolated environment and record owner approval. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-219 — Reject Rollback and recovery approval invalid operation

- **Where:** `deployment runbook, DB/media backup and release tracker` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: No verified restore or destructive migration blocks deployment. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: No verified restore or destructive migration blocks deployment. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

### JR-220 — Close Rollback and recovery approval verification

- **Where:** `deployment runbook, DB/media backup and release tracker` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never disable SSH host-key validation or claim production is current from CI success.

## Phase sign-off

Address blocked CD safely, with real host identity and observable behavior. Re-run affected prior-phase tests and record the SHA, isolated environment, redacted logs and rollback. A past historical "done" does not satisfy this new gate.

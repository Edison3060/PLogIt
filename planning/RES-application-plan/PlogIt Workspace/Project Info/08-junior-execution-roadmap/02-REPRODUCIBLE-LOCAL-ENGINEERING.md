# Phase 02 — Reproducible local engineering

**Prerequisite:** Pass Phase 01 with recorded evidence. **Exit gate:** Establish a repeatable local test baseline before updates.

**Existing-code policy:** PLogIt is built. Each JR is a verification or separately approved correction; do not recreate a feature or edit the copied RES source. **All JR execution statuses begin NOT STARTED**, even where implementation exists.

### JR-021 — Inventory Maven and Spring baseline

- **Where:** `backend/pom.xml and backend/src/test` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `backend/pom.xml and backend/src/test`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-022 — Specify Maven and Spring baseline acceptance

- **Where:** `backend/pom.xml and backend/src/test` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Actual Java 21 and Spring Boot 4.1.1 are measured, not July version claims. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-023 — Reproduce Maven and Spring baseline success

- **Where:** `backend/pom.xml and backend/src/test` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Run clean Maven test gate and record failures plus test totals. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Run clean Maven test gate and record failures plus test totals. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-024 — Reject Maven and Spring baseline invalid operation

- **Where:** `backend/pom.xml and backend/src/test` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Simulate incompatible JDK or failed Testcontainers setup and record safe blocker. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Simulate incompatible JDK or failed Testcontainers setup and record safe blocker. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-025 — Close Maven and Spring baseline verification

- **Where:** `backend/pom.xml and backend/src/test` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-026 — Inventory React Vite toolchain

- **Where:** `frontend/package.json and frontend/package-lock.json` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `frontend/package.json and frontend/package-lock.json`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-027 — Specify React Vite toolchain acceptance

- **Where:** `frontend/package.json and frontend/package-lock.json` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: React 18/Vite 6/TypeScript versions and scripts are resolved from source. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-028 — Reproduce React Vite toolchain success

- **Where:** `frontend/package.json and frontend/package-lock.json` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Run npm ci, lint, build and Vitest on a pinned runtime. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Run npm ci, lint, build and Vitest on a pinned runtime. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-029 — Reject React Vite toolchain invalid operation

- **Where:** `frontend/package.json and frontend/package-lock.json` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Deliberately corrupt fixture/lock mismatch in isolated branch and verify build fails. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Deliberately corrupt fixture/lock mismatch in isolated branch and verify build fails. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-030 — Close React Vite toolchain verification

- **Where:** `frontend/package.json and frontend/package-lock.json` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-031 — Inventory PostgreSQL Flyway schema

- **Where:** `backend/src/main/resources/db/migration` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `backend/src/main/resources/db/migration`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-032 — Specify PostgreSQL Flyway schema acceptance

- **Where:** `backend/src/main/resources/db/migration` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Versioned migrations and entities agree with an isolated PostgreSQL 16 fixture. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-033 — Reproduce PostgreSQL Flyway schema success

- **Where:** `backend/src/main/resources/db/migration` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Migrate clean DB, then test incremental migration without losing sample log. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Migrate clean DB, then test incremental migration without losing sample log. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-034 — Reject PostgreSQL Flyway schema invalid operation

- **Where:** `backend/src/main/resources/db/migration` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Inject migration error and verify startup fails rather than writes partial schema. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Inject migration error and verify startup fails rather than writes partial schema. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-035 — Close PostgreSQL Flyway schema verification

- **Where:** `backend/src/main/resources/db/migration` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-036 — Inventory Compose and healthchecks

- **Where:** `docker-compose.yml, docker-compose.prod.yml, backend/Dockerfile and frontend/Dockerfile` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `docker-compose.yml, docker-compose.prod.yml, backend/Dockerfile and frontend/Dockerfile`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-037 — Specify Compose and healthchecks acceptance

- **Where:** `docker-compose.yml, docker-compose.prod.yml, backend/Dockerfile and frontend/Dockerfile` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Development/prod overrides, volume persistence, health routes and port exposure are documented. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-038 — Reproduce Compose and healthchecks success

- **Where:** `docker-compose.yml, docker-compose.prod.yml, backend/Dockerfile and frontend/Dockerfile` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Start dev stack and observe health status plus API/browser reachability. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Start dev stack and observe health status plus API/browser reachability. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-039 — Reject Compose and healthchecks invalid operation

- **Where:** `docker-compose.yml, docker-compose.prod.yml, backend/Dockerfile and frontend/Dockerfile` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Test missing env var and unavailable Postgres with bounded failure. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Test missing env var and unavailable Postgres with bounded failure. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

### JR-040 — Close Compose and healthchecks verification

- **Where:** `docker-compose.yml, docker-compose.prod.yml, backend/Dockerfile and frontend/Dockerfile` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never mutate lockfiles, databases or images merely to make a command appear successful.

## Phase sign-off

Establish a repeatable local test baseline before updates. Re-run affected prior-phase tests and record the SHA, isolated environment, redacted logs and rollback. A past historical "done" does not satisfy this new gate.

# Phase 10 — Dependency refresh and supply chain

**Prerequisite:** Pass Phase 09 with recorded evidence. **Exit gate:** Upgrade cautiously in isolated PRs with full regression.

**Existing-code policy:** PLogIt is built. Each JR is a verification or separately approved correction; do not recreate a feature or edit the copied RES source. **All JR execution statuses begin NOT STARTED**, even where implementation exists.

### JR-181 — Inventory Java and Spring version review

- **Where:** `backend/pom.xml and backend integration tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `backend/pom.xml and backend integration tests`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-182 — Specify Java and Spring version review acceptance

- **Where:** `backend/pom.xml and backend integration tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Current Java 21/Boot 4.1.1 baseline is reproducible; proposed upgrade lists incompatibilities. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-183 — Reproduce Java and Spring version review success

- **Where:** `backend/pom.xml and backend integration tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Run complete Maven suite against proposed supported version on candidate branch. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Run complete Maven suite against proposed supported version on candidate branch. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-184 — Reject Java and Spring version review invalid operation

- **Where:** `backend/pom.xml and backend integration tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Fail migration/Jackson conflict without shipping untested changes. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Fail migration/Jackson conflict without shipping untested changes. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-185 — Close Java and Spring version review verification

- **Where:** `backend/pom.xml and backend integration tests` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-186 — Inventory Node React Vite support

- **Where:** `frontend/Dockerfile, package.json and package-lock.json` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `frontend/Dockerfile, package.json and package-lock.json`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-187 — Specify Node React Vite support acceptance

- **Where:** `frontend/Dockerfile, package.json and package-lock.json` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Node 20 build-stage is EOL; React/Vite updates are optional separate candidates. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-188 — Reproduce Node React Vite support success

- **Where:** `frontend/Dockerfile, package.json and package-lock.json` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Run npm ci, build, lint, Vitest and browser smoke on supported pinned Node LTS. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Run npm ci, build, lint, Vitest and browser smoke on supported pinned Node LTS. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-189 — Reject Node React Vite support invalid operation

- **Where:** `frontend/Dockerfile, package.json and package-lock.json` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Peer dependency or snapshot regression blocks update. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Peer dependency or snapshot regression blocks update. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-190 — Close Node React Vite support verification

- **Where:** `frontend/Dockerfile, package.json and package-lock.json` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-191 — Inventory Image and workflow hardening

- **Where:** `Dockerfiles, Compose and .github/workflows/ci.yml` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `Dockerfiles, Compose and .github/workflows/ci.yml`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-192 — Specify Image and workflow hardening acceptance

- **Where:** `Dockerfiles, Compose and .github/workflows/ci.yml` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Base images/actions have immutable references and minimal CI permission. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-193 — Reproduce Image and workflow hardening success

- **Where:** `Dockerfiles, Compose and .github/workflows/ci.yml` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Inspect digest pins, Semgrep/Trivy jobs and rendered Compose security settings. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Inspect digest pins, Semgrep/Trivy jobs and rendered Compose security settings. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-194 — Reject Image and workflow hardening invalid operation

- **Where:** `Dockerfiles, Compose and .github/workflows/ci.yml` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Deliberately trigger scanner failure in test branch; gate cannot be bypassed. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Deliberately trigger scanner failure in test branch; gate cannot be bypassed. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-195 — Close Image and workflow hardening verification

- **Where:** `Dockerfiles, Compose and .github/workflows/ci.yml` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-196 — Inventory Artifact traceability

- **Where:** `CI build/deploy jobs and proposed provenance manifest` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `CI build/deploy jobs and proposed provenance manifest`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-197 — Specify Artifact traceability acceptance

- **Where:** `CI build/deploy jobs and proposed provenance manifest` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Release source SHA, scanned image digest, DB migration and rollback reference are linked. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-198 — Reproduce Artifact traceability success

- **Where:** `CI build/deploy jobs and proposed provenance manifest` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Trace test artifact to deployable image and recorded release metadata. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Trace test artifact to deployable image and recorded release metadata. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-199 — Reject Artifact traceability invalid operation

- **Where:** `CI build/deploy jobs and proposed provenance manifest` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Mismatched rebuilt production image is flagged as non-identical to tested artifact. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Mismatched rebuilt production image is flagged as non-identical to tested artifact. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

### JR-200 — Close Artifact traceability verification

- **Where:** `CI build/deploy jobs and proposed provenance manifest` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** A newer dependency release is not by itself evidence of a vulnerability.

## Phase sign-off

Upgrade cautiously in isolated PRs with full regression. Re-run affected prior-phase tests and record the SHA, isolated environment, redacted logs and rollback. A past historical "done" does not satisfy this new gate.

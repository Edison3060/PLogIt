# Session 18 — Database integrity and recoverability

Base: `927085c4f3ed7def4f55eaae351b2add2abe66d8`. Reviewer: Builder Lead. Phase 09 [draft PR #30](https://github.com/Edison3060/PLogIt/pull/30) stacks on Phase 08 [PR #29](https://github.com/Edison3060/PLogIt/pull/29).

## JR-171

Reviewed base `927085c4f3ed7def4f55eaae351b2add2abe66d8`. Compose mounts `pgdata` at PostgreSQL and `attachments` at the backend. Flyway owns schema and AttachmentService stores relative paths in PostgreSQL. Production Compose retains both volumes. Existing `scripts/ci-compose-smoke.sh` restarts PostgreSQL and re-reads one row. README documents liveness and release operations. No canonical paired backup, restore or pre-promotion verification was identified. Deployment operator owns backup and restore. Inventory only.

## JR-172

Require a writer-quiesced checkpoint containing a PostgreSQL custom-format dump and separate attachment archive, with private checksums and restore into a fresh isolated target. Compare engagement, log, version, attachment and audit records and image bytes. Missing media, a failed restore or an incompatible application schema must stop any promotion. The source currently provisions separate volumes. The PLogIt owner and deployment operator own production backup access, encryption, retention, RPO/RTO and cutover; those decisions remain open. This session authorizes a disposable drill.

## JR-173

Added `scripts/ci-recovery-drill.sh` to the existing CI Compose job. It creates a synthetic engagement, a log with two versions and a PNG through authenticated HTTP. It stops the backend writer, captures a PostgreSQL custom-format dump and a separate attachment archive, restores them to a fresh database and directory, and compares records and original image bytes. `bash -n scripts/ci-recovery-drill.sh` and the out-of-CI guard passed locally. Runtime requires the CI Docker environment. Local Maven could not resolve its parent POM through sandbox DNS.

## JR-174

The isolated drill also supplies an empty restored media directory to the same byte verifier. It requires explicit missing-media denial and compares the restored database manifest again to detect side effects. The code never promotes the candidate or connects to production. Runtime pending on GitHub Actions.

## JR-175

Paired restore acceptance depends on fresh CI for JR-173/174 and operational sign-off under DEC-2026-11-27-03. Keep production promotion, access, retention and RPO/RTO unapproved. The test uses only synthetic data and a separate destination; no application source or export changes.

## JR-176

Reviewed `HealthController.java`, `GlobalExceptionHandler.java`, the backend Dockerfile, Compose, README and the existing Compose smoke at the Phase 09 base. The public endpoint returns process liveness only. Compose exposes PostgreSQL container health and gates backend startup. HTTP 500 uses a generic response, with no application correlation ID or matching structured incident record. The README describes deployment and probes; a recovery and triage procedure is incomplete. The deployment operator owns recovery and backend maintainers own application diagnostics.

## JR-177

Preserve the public /api/health liveness payload. Verify PostgreSQL and storage using authenticated bounded operations, restart in dependency order, and check the logs and external responses for seeded credential and payload values. An operator readiness design should expose only dependency state to an approved audience, with generated opaque correlation IDs and allowlisted diagnostic fields. The deployment operator and PLogIt owner own production exposure, log access/retention, and response policy. This branch tests the existing endpoint and includes a deployment-independent runbook.

## JR-178

Extended the Compose drill to restart the backend, verify DB-container health and public liveness separately, deny the old session, log in, and compare the authenticated download with the original PNG. Added the isolated recovery runbook. The backend image now creates the attachment mountpoint owned by the existing runtime UID for new named volumes. It does not alter existing volumes. Rollback is reverting the Dockerfile change. No endpoint or JSON changes. Shell syntax and diff whitespace passed. Real restart and image tests are pending fresh CI. The baseline lacks a redacted correlation ID.

## JR-179

The CI drill temporarily removes the synthetic image from its expected path and checks a bounded generic 404. It stops the disposable DB and makes an authenticated, CSRF-protected edit request with a 45s budget. It expects a generic 500, verifies current liveness behavior, restarts PostgreSQL, and compares the post-recovery database manifest and image to baseline. It checks response and backend logs for seeded credentials, cookies and synthetic evidence text. Media capture uses a networkless, read-only helper attached to the stopped backend volumes. Shell syntax and the CI-only guard passed. Runtime evidence is pending.

## JR-180

Application diagnostics remain incomplete under DEC-2026-11-27-04: public liveness does not verify dependencies and generic failures lack structured correlation. Interim operations use independent dependency and authenticated probes. Await fresh Compose and regression CI. Phase acceptance also retains JR-165 and upstream phase, privacy, production and JudgeIt gates.

## Executed verification and reconciliation

[GitHub Actions 46013369940](https://github.com/Edison3060/PLogIt/actions/runs/46013369940) on `7734c0712ad774069c28c1476f7cb48c6087303f`. Builder Lead reviewed.

### JR-173 | DONE

CI ran the actual authenticated create, edit, upload and submit APIs. Dump and media archive checksums passed. Restore recovered 1 engagement, 1 membership, 1 log, 2 versions, 1 attachment and the matching submit audit record and Flyway history. Image SHA-256 `2da746510008efb247a5f13c32a809748eb1bec2b882c7fac7eba990f55ee1cb` matched. A separate local PostgreSQL 16.16/pg_dump/pg_restore drill with direct SQL fixtures also compared the same counts and image hash.

### JR-174 | DONE

The same verifier rejected a fresh target with absent media and reported the missing attachment explicitly. Both database manifests matched after rejection. No target was promoted.

### JR-175 | DONE

The tested synthetic backup and denial meet local acceptance. The operator runbook records backup access, retention, encryption, objectives, operational migration validation and authorized promotion. DEC-2026-11-27-03 remains open for production.

### JR-178 | IN REVIEW

After backend restart, PostgreSQL health and process liveness passed. The old session received 403. A fresh authorized session downloaded identical bytes. Redacted correlation remains missing. The fresh attachment mount initialization was exercised by the successful real upload. [Docker volume mount behavior](https://docs.docker.com/engine/storage/volumes/#mounting-a-volume-over-existing-data), accessed 2026-11-27, informed the fix. Existing volume ownership requires operator verification.

### JR-179 | DONE

Missing storage returned 404. The authenticated edit during DB outage returned generic 500 within the 45-second budget, reporting 0 elapsed whole seconds. The public liveness endpoint still showed UP. The backend startup with unavailable DB also failed within its 110-second budget. Recovered database records and image bytes matched the originals. Selected credential, cookie and content markers were absent from backend logs and error responses.

### JR-180 | IN REVIEW

Checks ran against `7734c0712ad774069c28c1476f7cb48c6087303f`. GitHub Actions passed Java 21/Maven 170 tests with PostgreSQL 16.16, frontend build and 25 tests, Compose recovery, Semgrep, Trivy filesystem and both images, and production proxy validation. Production CD was skipped. The equivalent stacked PR validation run 46013380950 passed. The phase retains open dependency readiness and structured correlation work, DB enum enforcement (JR-165), prior stacked acceptance, the INT-001 contract test and production approvals.

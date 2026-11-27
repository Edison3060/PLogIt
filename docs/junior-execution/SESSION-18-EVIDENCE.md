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

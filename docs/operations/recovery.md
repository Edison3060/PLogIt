# PLogIt recovery and incident verification

[The CI recovery drill](../../scripts/ci-recovery-drill.sh) creates synthetic data in a disposable Compose project. Its output is evidence for that environment. A production restore requires authorization and an approved recovery plan.

## Database and media checkpoint

- Record the tested application commit and PostgreSQL major version. Quiesce the backend and any other writers until both backups finish.
- Pair the PostgreSQL custom-format dump with the attachment volume archive. Record both checksums under the same checkpoint ID. Keep the checkpoint private because it includes credentials in hashed form, audit records and raw evidence.
- Restore into a fresh isolated database and attachment volume using the matching PostgreSQL version. Validate migration history and startup before testing authenticated reads. Check engagement, membership, log and version records, audit rows, attachment metadata and attachment bytes against the checkpoint.
- Reject an incomplete media archive, failed import, checksum mismatch or incompatible migration. Keep the current deployment and volumes available for rollback. Approve cutover only after operational verification.
- The backend image initializes a fresh attachment volume for its runtime account. Check ownership and access on a restored or pre-existing volume independently before enabling writes.

## Incident recovery

- Check the PostgreSQL container and the backend process separately. The public `/api/health` reports process liveness. Use an authorized, read-only operation to verify the database and a known attachment to verify the media mount.
- After restoring dependencies, start PostgreSQL first, verify its health, then start the backend and frontend. Verify that an authorized account can read the prior records and attachment bytes, and that stale sessions cannot be reused after a backend restart.
- Keep logs private. Record UTC, tested commit, dependency state, route template and HTTP status. Check error responses and log excerpts for credentials, cookies and evidence content before sharing. The current application does not return a correlation ID for generic failures.
- The deployment operator must approve production backup access, encryption, retention, recovery objectives, log access and cutover. The drill performs no production restore.

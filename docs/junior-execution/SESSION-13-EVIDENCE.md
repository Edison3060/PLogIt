# Session 13 attachment verification

Phase 07 stacks on the unmerged Phase 06 head `b0ad327368e4c2276d81e85cb9310eddc2ec4de7`. Baseline release and JudgeIt gates remain open. All evidence uses synthetic data. Builder Lead reviewed each checkpoint.


## JR-121

**DONE.** Expected a current source-and-test map. At `b0ad327368e4c2276d81e85cb9310eddc2ec4de7`, `AttachmentController.upload` binds authenticated multipart `file`; `AttachmentService.upload` checks engagement membership, leader or log author, and DRAFT state before Tika byte detection and a PNG/JPEG/GIF/WebP allowlist. `application.yml` sets servlet caps. Existing `AttachmentIntegrationTest` covers PNG, plain text, disguised content, membership, state and CSRF. Its PNG helper contains only a header, and current MockMvc requests do not exercise the servlet multipart size limits. Historical design documentation is reference material. No application or test code changed.


## JR-122

**DONE.** Expected an authenticated, CSRF-protected multipart `file` for a draft owned by the caller or within their leader scope. The servlet caps are 10 MiB per file and 25 MiB per request. Tika ignores the client MIME and filename for type detection and permits PNG, JPEG, GIF and WebP. It identifies type without proving that the whole image decodes. Both the product spec section 9 and the roadmap require byte-based validation. Preserve original evidence bytes and legacy metadata while full-image validation, pixel limits and format handling await explicit acceptance. The PLogIt owner owns that decision. OWASP File Upload Cheat Sheet, checked 2026-11-25, supports layered type detection, filename handling, size limits and isolated storage. https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html


## JR-123

**IN REVIEW.** Expected a successful upload and byte-for-byte authenticated retrieval of synthetic PNG, JPEG, GIF and WebP. `AttachmentIntegrationTest.acceptedImageRoundTripsWithDetectedType` uses Java ImageIO for PNG/JPEG/GIF and one lossless single-pixel WebP fixture generated with Pillow 13.0.0. POST `/api/logs/{logId}/attachments` uses a neutral filename and Content-Type. GET `/api/logs/{logId}/attachments/{id}` asserts the detected MIME and exact bytes. PostgreSQL-backed CI execution is pending.


## JR-124

**IN REVIEW.** Expected 409 for non-image bytes and 413 for multipart file size above the configured limit, with no metadata or file. The existing PostgreSQL test covers disguised script rejection. `scripts/ci-auth-smoke.sh` checks a declared PNG containing inert text and a 10 MiB plus one byte file over authenticated, CSRF-protected HTTP. It also asserts an empty attachment list and no per-log storage directory. `GlobalExceptionHandler` now returns a fixed 413 response for multipart size exceptions before the generic handler. `bash -n scripts/ci-auth-smoke.sh` passed. Full-image structural validation remains a separate policy gap because type recognition does not guarantee complete decoding. CI pending.


## JR-125

**IN REVIEW.** The positive suite checks full synthetic image round trips. The negative suite checks non-image content, the existing disguised-script rejection and the 10 MiB servlet limit. CI must execute both. Signature recognition is narrower than full decoding and original evidence bytes remain intact. Keep strict malformed-image rejection and pixel limits in DEC-2026-11-25-01 for owner approval. The only production change for JR-121–125 is the explicit size-error response. Rollback is a normal revert of that handler change on the phase branch.


## JR-126

**DONE.** Expected a map of both metadata and file owners. At baseline `b0ad327368e4c2276d81e85cb9310eddc2ec4de7`, `AttachmentController` exposes authenticated upload/list/download routes. `AttachmentService` verifies active engagement membership and binds the attachment row to the supplied log; uploads generate a UUID-prefixed leaf below the engagement/log directory. The saved filename is display metadata. `AttachmentService.readFile` resolves persisted storage paths without checking their scope at baseline, including for PDF images. Existing `AttachmentIntegrationTest` covers removed and foreign members and `ExportIntegrationTest` preserves filename-only legacy JSON. The roadmap is reference acceptance, while canonical backend code owns live behavior. No application code changed in this JR.


## JR-127

**DONE.** Expected route identifiers are a log UUID and numeric attachment ID. Active engagement membership and matching log are required for reads. Filesystem reads should also require the metadata path to identify a regular file directly under that record's engagement/log directory. The original filename currently survives as display metadata while storage uses a sanitized UUID-prefixed leaf. The original exercise requires path-like names to be rejected. The PLogIt owner owns acceptance of a narrower filename policy. DEC-2026-11-25-02 preserves existing display/JSON names and authorizes the focused runtime storage containment correction under existing isolation rules.


## JR-128

**IN REVIEW.** Expected the author to upload, list and retrieve the exact attachment under the correct log UUID and numeric ID. `AttachmentIntegrationTest.authorCanReadAttachmentFromOwnLogDirectory` creates a leader and separate active member who authors the draft. It checks that the database path's parent matches the engagement/log directory, the file exists, listing returns the same ID and protected GET returns the original bytes. Exact-head PostgreSQL-backed CI pending.


## JR-129

**IN REVIEW.** `AttachmentService.readFile` now validates a normalized relative path directly under the record's engagement/log, requires a regular file at its resolved location without symlinks beneath the configured root, and opens the final file without following links. It is shared by authorized downloads and PDF image reads. The expected safe response for an invalid location is 404. Regression cases exercise an ID scoped to another engagement visible to the same user, a deleted metadata row with retained synthetic bytes, and an alias whose target is a synthetic fixture within the same log directory. Stored display-name policy remains open in DEC-2026-11-25-02. The 413 smoke script's original executable bit was restored. CI pending.


## JR-130

**IN REVIEW.** The scoped author read, foreign attachment denial, deleted-row denial and same-log link denial are covered in `AttachmentIntegrationTest`. The shared reader preserves valid generated paths and evidence bytes. The private storage volume must remain writable only by the application and trusted infrastructure. The acceptance requirement to reject path-like display names remains open under DEC-2026-11-25-02 because changing original filename metadata can affect existing JSON consumers. The PLogIt owner retains this policy decision. Rollback is a normal revert of the focused path-validation commit. Run the full PostgreSQL, frontend, Compose and security gates before controller reconciliation.


## Reconciliation

[Push CI 45531027378](https://github.com/Edison3060/PLogIt/actions/runs/45531027378) at `569390e4a5f3820ea4b882e33fe3a554bfe38985` used Java 21.0.13, Maven 3.9.15 and PostgreSQL 16.16. `mvn -B clean verify` passed 163 backend tests, including 22 attachment tests, with zero failures, errors or skips. Node 22.23.4 passed frontend lint, build and all 19 tests. Compose passed live authenticated 409/413 checks and verified no metadata or disk directory for either denied upload. Semgrep, Trivy filesystem and image gates, and production proxy checks passed. Production CD was skipped. Local checks passed for Bash syntax, WebP fixture decode and `git diff --check`. Local Maven and PostgreSQL were unavailable.

- JR-123 **DONE**. All four complete fixtures returned their detected MIME type and byte-identical content.

- JR-124 **IN REVIEW**. Disguised script and non-image bytes were denied. Real HTTP returned 413 for a file over the cap; invalid bytes returned 409. Both live denials left the list empty and no log storage directory. Complete malformed-image rejection remains open.

- JR-125 **IN REVIEW**. Round trips and denial regressions passed. Strict malformed-image acceptance remains open under DEC-2026-11-25-01.

- JR-128 **DONE**. A non-leader author uploaded and listed the attachment and retrieved the original bytes under the scoped ID. The row mapped to a regular file in the expected directory.

- JR-129 **IN REVIEW**. Mismatched engagement scope, deleted metadata and an in-directory symbolic link returned 404. Strict path-like display-name rejection remains open.

- JR-130 **IN REVIEW**. The shared path guard and regressions passed. DEC-2026-11-25-02 still governs filename policy and Phase 07 acceptance remains open.

Ten numbered checkpoints on `work/plogit-phase-07`. JR-121 `cad6411e4e97`, JR-122 `8669972b660a`, JR-123 `a75b1eea7b68`, JR-124 `f7dce45e9761`, JR-125 `3f2831f6a6d4`, JR-126 `4b2b0f0265ca`, JR-127 `fc3e0eef166c`, JR-128 `13098352f08b`, JR-129 `5bfcdf76b898`, JR-130 `569390e4a5f3`.

[Draft PR #28](https://github.com/Edison3060/PLogIt/pull/28) stacks on unmerged Phase 06 PR #27. Phase 07 approval and production authorization remain open.

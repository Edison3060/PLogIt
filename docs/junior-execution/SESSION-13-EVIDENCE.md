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

# Phase 07 — Media, Markdown and evidence privacy

**Prerequisite:** Pass Phase 06 with recorded evidence. **Exit gate:** Review unsafe input handling and evidence lifecycle without leaking pentest material.

**Existing-code policy:** PLogIt is built. Each JR is a verification or separately approved correction; do not recreate a feature or edit the copied RES source. **All JR execution statuses begin NOT STARTED**, even where implementation exists.

### JR-121 — Inventory Image upload validation

- **Where:** `service/AttachmentService.java and AttachmentIntegrationTest.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `service/AttachmentService.java and AttachmentIntegrationTest.java`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-122 — Specify Image upload validation acceptance

- **Where:** `service/AttachmentService.java and AttachmentIntegrationTest.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Magic-byte MIME, size and type checks protect uploaded evidence. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-123 — Reproduce Image upload validation success

- **Where:** `service/AttachmentService.java and AttachmentIntegrationTest.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Upload valid PNG/JPEG/WebP/GIF fixture and retrieve authorized attachment. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Upload valid PNG/JPEG/WebP/GIF fixture and retrieve authorized attachment. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-124 — Reject Image upload validation invalid operation

- **Where:** `service/AttachmentService.java and AttachmentIntegrationTest.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Disguised script, oversized body and invalid media leave no stored artifact. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Disguised script, oversized body and invalid media leave no stored artifact. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-125 — Close Image upload validation verification

- **Where:** `service/AttachmentService.java and AttachmentIntegrationTest.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-126 — Inventory Attachment isolation and paths

- **Where:** `web/AttachmentController.java and service/AttachmentService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `web/AttachmentController.java and service/AttachmentService.java`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-127 — Specify Attachment isolation and paths acceptance

- **Where:** `web/AttachmentController.java and service/AttachmentService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Metadata and filesystem access cannot cross engagement or storage root. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-128 — Reproduce Attachment isolation and paths success

- **Where:** `web/AttachmentController.java and service/AttachmentService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Authorized owner accesses attachment with correct scoped ID. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Authorized owner accesses attachment with correct scoped ID. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-129 — Reject Attachment isolation and paths invalid operation

- **Where:** `web/AttachmentController.java and service/AttachmentService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Traversal filename, foreign engagement and deleted attachment rejected. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Traversal filename, foreign engagement and deleted attachment rejected. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-130 — Close Attachment isolation and paths verification

- **Where:** `web/AttachmentController.java and service/AttachmentService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-131 — Inventory Markdown and code-block sanitization

- **Where:** `service/MarkdownService.java and frontend/src/pages/LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `service/MarkdownService.java and frontend/src/pages/LogDetail.tsx`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-132 — Specify Markdown and code-block sanitization acceptance

- **Where:** `service/MarkdownService.java and frontend/src/pages/LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Stored raw Markdown is rendered through safe sanitized HTML. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-133 — Reproduce Markdown and code-block sanitization success

- **Where:** `service/MarkdownService.java and frontend/src/pages/LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Render legitimate code block and links with expected visible formatting. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Render legitimate code block and links with expected visible formatting. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-134 — Reject Markdown and code-block sanitization invalid operation

- **Where:** `service/MarkdownService.java and frontend/src/pages/LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: XSS scripts, dangerous links and malicious language attributes inert. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: XSS scripts, dangerous links and malicious language attributes inert. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-135 — Close Markdown and code-block sanitization verification

- **Where:** `service/MarkdownService.java and frontend/src/pages/LogDetail.tsx` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-136 — Inventory Retention, exports and sensitive data

- **Where:** `service/export/PdfReportStrategy.java and privacy decision register` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `service/export/PdfReportStrategy.java and privacy decision register`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-137 — Specify Retention, exports and sensitive data acceptance

- **Where:** `service/export/PdfReportStrategy.java and privacy decision register` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: PDF output, PII fields, evidence retention, media size caps and backup access are explicitly governed. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-138 — Reproduce Retention, exports and sensitive data success

- **Where:** `service/export/PdfReportStrategy.java and privacy decision register` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Export safe synthetic case and measure bounded memory and access logging. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Export safe synthetic case and measure bounded memory and access logging. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-139 — Reject Retention, exports and sensitive data invalid operation

- **Where:** `service/export/PdfReportStrategy.java and privacy decision register` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Large PDF, sensitive log disclosure and unauthorized evidence download fail safely. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Large PDF, sensitive log disclosure and unauthorized evidence download fail safely. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

### JR-140 — Close Retention, exports and sensitive data verification

- **Where:** `service/export/PdfReportStrategy.java and privacy decision register` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Never trust filenames, image extensions, HTML or attachment paths from the client.

## Phase sign-off

Review unsafe input handling and evidence lifecycle without leaking pentest material. Re-run affected prior-phase tests and record the SHA, isolated environment, redacted logs and rollback. A past historical "done" does not satisfy this new gate.

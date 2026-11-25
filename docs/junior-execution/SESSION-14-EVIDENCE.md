# Session 14 evidence — Phase 07

PLogIt branch `work/plogit-phase-07` at intake `ea6e640ed93db112e761bc3445a47b2392456532`, stacked above unmerged [Phase 06 PR #27](https://github.com/Edison3060/PLogIt/pull/27). Reviewer: Builder Lead. All fixtures use synthetic data.

## JR-131

2026-11-25T14:03:09Z · **DONE** · Prior branch head `ea6e640ed93db112e761bc3445a47b2392456532`

Traced raw Markdown storage and the sanitized rendering paths.

LogService persists raw description and result. LogDetail.from renders both through Flexmark and the OWASP policy in MarkdownService. LogDetail.tsx displays those rendered fields and keeps standalone code and its language in React text nodes. ExportService renders Markdown for PDF via the same service. MarkdownSanitizationTest covers formatting, fenced code, links, disallowed tags and attributes, and empty inputs. These owners implement the rendering invariant in the product spec. No application code changed.

## JR-132

2026-11-25T14:03:22Z · **DONE** · Prior branch head `f8775f1bb34ad47480b8ba5497cac7fc16717c1a`

Recorded the Markdown and code rendering acceptance criteria.

Authorized log detail requests must provide separately rendered descriptionHtml and resultHtml. The source Markdown, code body and code language remain evidence fields. Fenced code and ordinary HTTPS links must be visible, and executable tags, disallowed URL schemes and event attributes must be absent from the rendered output. Rendered code language must be plain text in the web UI. The scoped PDF uses sanitized rendering and its own escaping for code metadata. Existing export schemas and authorization are the compatibility boundary.

## JR-133

2026-11-25T14:04:26Z · **IN REVIEW** · Prior branch head `350bdb5605804accc805213875f98e65f3a4be3f`

Verified the frontend renders fenced code and an HTTPS link.

Frontend focused command npm test -- --run src/pages/LogDetail.test.tsx exited 0 with 3 tests passed on Node 24.22.3 and Vitest 4.1.12. The component fixture uses already-sanitized HTML. The new PostgreSQL-backed LogAuthzIntegrationTest.detailRendersStoredFencedCodeAndSafeLinks asserts HTTP 200, unchanged raw fields, pre/code markup and the canonical HTTPS anchor at GET /api/logs/{uuid}. Backend execution is pending CI. The local Maven attempt stopped during dependency resolution before running tests.

## JR-134

2026-11-25T14:04:55Z · **IN REVIEW** · Prior branch head `e7c992adec63c2431090f0c5a69dc6cd93276e65`

Verified literal code-language rendering in the log detail.

The focused Vitest run passed four tests, including markup-shaped but inert strings in codeLanguage and codeBlock. The test requires their visible literal text and absence of corresponding elements. The existing frontend regression also verifies empty sanitized output never falls back to raw markup. Backend denial coverage uses the existing MarkdownSanitizationTest and LogAuthzIntegrationTest from the phase branch. Await direct CI evidence for both suites.

## JR-135

2026-11-25T14:05:00Z · **IN REVIEW** · Prior branch head `f5a1a031df8b56d6a67b52a10dddfe79ef2b5c36`

Reviewed the Markdown regressions and retained the existing renderer.

The positive component result and literal rendering denial check pass. The server-side acceptance depends on PostgreSQL-backed GET, the existing sanitizer suite and scoped log authorization on the current branch. No renderer, sanitizer or raw evidence formats were changed. Reverting the two test-only checkpoints removes the added assertions without changing runtime behavior.

## JR-136

2026-11-25T14:05:05Z · **DONE** · Prior branch head `e4de006093477359b5332c82849d177f467b8b15`

Mapped PDF generation, audit and evidence retention ownership.

ExportService checks leader access and selects eligible log states, loads report data, calls PdfReportStrategy and transitions after generation. The PDF strategy emits an auditor evidence pack with display names, emergency contacts, rendered content and selected images, using an in-memory byte array. Attachment traffic has per-file and request caps. The product spec describes read-only archival. DEC-2026-11-06-09 leaves transfer classification, retention and access open. Existing ExportIntegrationTest tests PDF signatures and JSON state and audit; AttachmentIntegrationTest checks scoped bytes. An aggregate PDF budget, binding redaction policy and verified backup access policy were not found in the owners inspected.

## JR-137

2026-11-25T14:05:21Z · **DONE** · Prior branch head `74c5dbc33da771c513f48951c8c0da80172e7325`

Recorded PDF, PII, retention and backup acceptance boundaries.

Request POST /api/engagements/{id}/export with JSON format PDF and optional includeExported. Expect an active leader to receive only eligible same-engagement evidence and a minimal export audit. A member and outsider must fail without a file, state change, audit side effect or disclosure. Current documented multipart caps are 10 MB per file and 25 MB per request. Those limits do not bound accumulated evidence size or renderer heap. A complete phase gate requires an explicit budget and failure response, field-level disclosure policy (including emergency contacts and free text), archive retention and authorized backup evidence.

## JR-138

2026-11-25T14:06:15Z · **IN REVIEW** · Prior branch head `12ae06f14525f2daf55a850135fc9065b766f110`

Added PDF content, selection and audit assertions for synthetic evidence.

The test exercises the real PDF endpoint on PostgreSQL with an approved record, a draft and a separate engagement. PDFBox parses the bytes to verify rendered evidence, literal code and language, display name, exclusion of private draft and cross-engagement content, and exclusion of the account email. The approved record must transition and persist only the expected export and transition audit. The published OpenHTMLtoPDF 1.1.40 POM declares PDFBox 3.0.3. The Apache PDFBox 3 migration guide (https://pdfbox.apache.org/3.0/migration.html, consulted 2026-11-25) documents the Loader API used in this test. CI is pending. A small fixture cannot establish a general memory ceiling, and the renderer has no aggregate budget. That criterion remains open under DEC-2026-11-25-04.

## JR-139

2026-11-25T14:06:34Z · **IN REVIEW** · Prior branch head `9ed51ae1cd49da6f3a4a2b9bb0918481aee2d36b`

Added PDF denial coverage for members and outsiders.

Expected HTTP 403 for an active nonleader and 404 for an outsider, with no PDF response, sensitive marker or account email, approved-state transition, or audit write. The new Spring MVC/PostgreSQL test exercises those conditions and existing AttachmentIntegrationTest covers scoped download denials. Oversized aggregate reports have no identified application-level rejection threshold. Free-text and emergency-contact redaction remain policy questions. Large-report rejection and the complete disclosure gate remain open; no large-resource run was performed.

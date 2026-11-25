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

## JR-140

2026-11-25T14:06:50Z · **IN REVIEW** · Prior branch head `3e5f1d62d9f5615266caf817358a1cc3e4f43f3b`

Reviewed the Phase 07 exit gate and retained draft status.

Session 14 modifies test coverage and tracking only. Frontend lint, build and all 21 tests passed locally with Node 24.22.3. git diff --check passed. Await exact-head GitHub CI for backend, isolated Compose, Semgrep, Trivy and proxy. Owner gate DEC-2026-11-25-04 remains open for aggregate PDF size and memory, sensitive-field policy and verified backup access and retention. Earlier JR-124/125 and JR-129/130 and the Phase 06 JudgeIt prerequisites remain open. Keep draft PR #28 stacked on #27. Rollback of the added test coverage requires reverting test-only changes; evidence history is append-only.

## JR-133

2026-11-25T14:09:20Z · **IN REVIEW** · Prior branch head `643a9198cf1c1f27781edf5dc6d0ec31b539c753`

Preserved fenced code layout through the existing sanitizer.

Push CI 45630535101 at 643a9198cf1c1f27781edf5dc6d0ec31b539c753 ran 165 tests with one failure. HTML contained the code text and a code element, with the pre element missing. The authoring and sanitizer accept the original text and escape its punctuation. The render policy now explicitly allows the pre element without extra attributes. The positive assertion uses an ordinary fenced statement and requires its full pre/code wrapper. The existing sanitizer denials and raw persistence assertions remain in place. Fix rollback is the inverse of this source patch, followed by re-verification. Fresh full CI is required.

## Reconciliation

[Full push CI](https://github.com/Edison3060/PLogIt/actions/runs/45630981676) at `4845d5d44f3af937c929626fc48d5a490df82193`: Java 21.0.13 and Maven 3.9.15 ran `mvn -B clean verify` against isolated PostgreSQL 16.16. All 165 tests passed with zero failures, errors or skips, including 18 export, 22 attachment, 12 log authorization and 8 sanitizer tests. Node 22.23.4 passed lint, build and all 21 Vitest tests. Compose, Semgrep, pinned Trivy dependency/secret and image checks, and production proxy validation passed. Production CD was skipped.

[Initial push CI](https://github.com/Edison3060/PLogIt/actions/runs/45630535101) ran 165 backend tests with one failure in the new fenced-code test. The description contained a code tag but lacked its preformatted wrapper. The correction allows the pre element with no added attributes, preserving the existing sanitizer and evidence fields. The direct Java check also produced the expected pre/code markup. Local Maven initially failed dependency resolution; compiling under an extracted JDK 21.0.13.1 succeeded for 97 production and 24 test sources, then local Surefire dependency resolution failed. Test counts above are from GitHub CI.

For JR-138, a separate, disposable Java 21.0.13.1 renderer process ran a synthetic one-log, no-image fixture with `java -Xms16m -Xmx64m -XX:+UseSerialGC --class-path "$PLOGIT_COMPILED_CLASSES_AND_DEPENDENCIES" PdfMemoryProbe.java`. With a configured maximum of 64,880,640 bytes, it produced a readable 5,594-byte PDF across three pages and exited 0. Pool peaks after measuring the render were 44,761,088 bytes tenured, 17,891,328 Eden and 2,228,224 survivor. Pool peaks are independent, and the probe excludes the HTTP and database layers. It is not an aggregate PDF size or memory guarantee. The PostgreSQL-backed export test separately verifies the response, state transition and scoped audit.

### Synthetic renderer probe

```java
import io.muzoo.ssc.plogit.service.MarkdownService;
import io.muzoo.ssc.plogit.service.export.EngagementReportData;
import io.muzoo.ssc.plogit.service.export.EngagementReportData.LogReportEntry;
import io.muzoo.ssc.plogit.service.export.PdfReportStrategy;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryType;
import java.time.Instant;
import java.util.List;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;

class PdfMemoryProbe {
    public static void main(String[] args) throws Exception {
        var pools = ManagementFactory.getMemoryPoolMXBeans().stream()
            .filter(pool -> pool.getType() == MemoryType.HEAP).toList();
        pools.forEach(pool -> pool.resetPeakUsage());
        var timestamp = Instant.parse("2026-11-25T00:00:00Z");
        var markdown = "**Evidence summary**";
        var rendered = new MarkdownService().render(markdown);
        var entry = new LogReportEntry("fixture", timestamp, "Test Lead", "RECONNAISSANCE",
            "Approved evidence", markdown, rendered, null, null, null, null,
            "IN_PROGRESS", "APPROVED", List.of(), "<sample>literal code</sample>", "<em>plain</em>", List.of());
        var data = new EngagementReportData(1L, "Test Engagement", "", null, null, "Test Lead",
            List.of(), List.of(), null, null, null, null, null, timestamp, 1, List.of(entry));
        byte[] pdf = new PdfReportStrategy().generate(data).content();
        for (var pool : pools) {
            System.out.printf("%s pool peak bytes: %d%n", pool.getName(), pool.getPeakUsage().getUsed());
        }
        System.out.printf("Configured maximum heap bytes: %d%n", Runtime.getRuntime().maxMemory());
        try (var document = Loader.loadPDF(pdf)) {
            String text = new PDFTextStripper().getText(document);
            for (String expected : List.of("Evidence summary", "<sample>literal code</sample>", "<em>plain</em>")) {
                if (!text.contains(expected)) {
                    throw new AssertionError("Missing text: " + expected);
                }
            }
            System.out.printf("Generated %d bytes across %d pages%n", pdf.length, document.getNumberOfPages());
        }
    }
}
```

## JR-140

2026-11-25T14:13:07Z · **IN REVIEW** · Prior branch head `4845d5d44f3af937c929626fc48d5a490df82193`

Reconciled the Session 14 tests and recorded readiness for independent work.

JR-131–137 are DONE. JR-138–140 remain IN REVIEW. See [executed checks and the renderer memory sample](#reconciliation). The full Phase 07 acceptance gate remains open for DEC-2026-11-25-04 (aggregate PDF size and server memory, sensitive fields, backup access and retention), JR-124/125 (complete image validation), and JR-129/130 (strict path-like display-name policy). INT-001 remains provisional and the exact legacy JudgeIt nullable-list incompatibility remains open. Phase 07 [draft PR #28](https://github.com/Edison3060/PLogIt/pull/28) stays stacked on unmerged #27. Production authorization remains pending. **READY WITH PROVISIONAL INTEGRATION** for independent Session 15 only.

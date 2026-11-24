# Session 12 evidence completeness, privacy and legacy compatibility

Phase 06 remains stacked on Phase 05 at `9efcae1ecccb9dbe8895e7d88a20253dbbace3db`. Phase 05 stale-draft review and prior release holds stay open. Isolated export verification uses synthetic data. Builder Lead is the reviewer.

## JR-111

**Expected.** Map the live serializer, selection/membership, attachment metadata and existing tests. **Observed.** At `12b7fd7b33a297227597219910603e558803fa8d`, `EngagementController.export` delegates to transactional `ExportService.export`. `MembershipService.assertLeader` guards selection. `AttachmentService.loadImagesForLog` supplies bytes and names to `EngagementReportData`. `JsonReportStrategy.toJsonLog` projects filenames only. The report carries the engagement's total count and fields for active member email, display name, role, scope and RoE. `ExportIntegrationTest.jsonSerializerMatchesLegacyFixture`, `jsonExportSelectsExactLogsAndCommitsAudit` and `memberDenialDoesNotDiscloseOrMutateEligibleLogs` cover serialization, states and the boundary. The suite handoff research separates current names-only output from a proposed manifest. The PLogIt product spec keeps engagement membership isolated. Source and prior test evidence reviewed. No production change at this checkpoint.

## JR-112

DEC-2026-11-24-04 records the expected leader-only JSON POST with active member and engagement metadata, eligible logs, and filenames as references. DRAFT and SUBMITTED bodies must be absent. Prior EXPORTED bodies require explicit opt-in. The current aggregate count includes omitted logs. Preserve audit and all current known fields. The PLogIt owner must approve any changed disclosure, redaction, media or selection policy. The receiver must retain missing-evidence indicators. Source/spec comparison only.

## JR-113

The new `ExportIntegrationTest.jsonExportIncludesOnlySelectedAttachmentNames` uploads synthetic PNGs to two drafts with authenticated multipart requests, seeds one as APPROVED and then invokes POST /api/engagements/{id}/export. It expects one selected UUID out of four and only the eligible filename. It checks for absence of bytes, hashes, manifest and the draft filename, and verifies all persisted review states. Runtime execution pending. Existing serializer and production paths remain at the baseline.

## JR-114

With Java 21.0.13 and Jackson 3.1.7, compiled the byte-matched JudgeIt legacy parser (`994df722a4d0da16d2676fbc606ff6d1eaa892a1`) and invoked `stage(byte[])`. Base fixture SHA `bd07afa8df0b18a2b40ecd32863835c4e573732e58a70711a328908a14254e9c` returned INVALID_TARGETS. For a staging-only diagnostic, set optional scope targets and tags to arrays. Both `sample.png` and a plain altered label `renamed.png` preserved the source UUID, 1 selected and 1 omitted, and explicitly yielded `missingAttachmentBytes=true`. The file SHA changed. A synthetic per-log attachmentHashes claim returned UNSUPPORTED_ATTACHMENT_CLAIM. This stage has no publication or authorization effects. The endpoint and persisted receipt still require direct evidence before closure.

## JR-115

The serializer fixture and mixed states in Session 11 passed CI 45310715443. Session 12 added an endpoint multipart regression for selected and excluded attachment names, with current runtime evidence pending. The real reader staging test retained the missing-media signal and rejected unsupported claims. Its synthetic diagnostic uses array-valued optional lists. Close the locally testable criteria once export, audit and consumer persistence evidence are reconciled. The nullable-list issue remains independently tracked under JR-109/110 and INT-001.

## JR-116

The active PLogIt branch at `12b7fd7b33a297227597219910603e558803fa8d` has `backend/src/test/resources/exports/legacy-evidence.json` and the suite research proposal. No approved `evidence-pack-v2.schema.json` exists on the Phase 06 branch. JudgeIt Phase 06 owns `contracts/plogit-legacy-v0.schema.json` (blob `0ffd04007ee3b3e5413f2a87e1c249af609f52a6`), `fixtures/plogit-empty.json` and `fixtures/plogit-multilog.json`, the Java parser, upload controller and import journal. The old reader rejects a producer schemaVersion and requires arrays for scope targets and tags. The distinct opt-in PLogIt bridge at `ce4ab70c449b8e98b3b66b262769a508be8231c9` remains provisional in PR #21. Current reads show the adapter is a private file wrapper. It leaves producer payload values intact and adds a canonical payload hash, namespace-scoped import key and unverified filename marker. No app code changed during inventory.

## JR-117

DEC-2026-11-24-05 records the owner decision for a possible versioned format. The existing export and existing consumer compatibility need dedicated evidence. Maintain the same one-way offline file boundary and independent databases. Approval must cover envelope, export ID, hash semantics, media packaging, source namespace, receipt dedupe and revision policy.

## JR-118

The PLogIt fixture produced by Spring JSON during Session 11 still fails with INVALID_TARGETS at the real JudgeIt parser. The same bytes are accepted unchanged as an inner payload by PLogIt's provisional wrapper. The parser accepts a diagnostic fixture with array-valued lists and preserves the UUID, omitted count and missing-media warning. This conditional parse result leaves the exact legacy handoff criterion unmet. New schema and fixtures remain unapproved under DEC-2026-11-24-05. An authenticated disposable PostgreSQL test is being prepared with the original bytes and the diagnostic inputs.

## JR-119

The unmodified JudgeIt parser rejected schemaVersion=999 as UNSUPPORTED_PRODUCER_VERSION and duplicate source UUIDs as DUPLICATE_LOG_ID. A repeated parse of identical bytes returned the same file SHA and UUID. The separate provisional wrapper's 12 Python tests passed, including deterministic scoped import keys and simulated idempotent replay. Parsing and mock replay provide no transaction proof. The real controller and journal replay test is pending in isolated PostgreSQL. The exact producer artifact still fails earlier on nullable optional collections.

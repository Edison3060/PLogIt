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

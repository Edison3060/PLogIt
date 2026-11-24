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

## JR-120

The exact old serializer fixture stays incompatible with the JudgeIt reader. Phase 06 cannot be accepted while legacy compatibility, the distinct optional future version approval and prior stacked phase acceptance remain open. Keep the current producer bytes and consumer provenance intact. Confirm current full CI, record local positive and negative evidence, and preserve DEC-2026-11-06-11. Any rollback of Session 12's code diff is test-only and requires no application data or migration rollback.

## Reconciliation

- PLogIt CI 45411481575 at `0234a6a492e2542e9f29b2eb9ca5cff649eb4291` used Java 21.0.13, Maven 3.9.15 and PostgreSQL 16.16. Backend `mvn -B clean verify` passed 156 tests with zero failures, errors or skips, including 17 export, 15 audit and 15 attachment tests. Frontend Node 22.23.4 passed lint, build and 19 tests. Compose health and failure probes, Semgrep, Trivy filesystem and both image gates, and production proxy checks passed. Production CD was skipped. [GitHub Actions](https://github.com/Edison3060/PLogIt/actions/runs/45411481575). The local PLogIt Maven attempt hit proxy connection refusal and a loopback mirror retry remained in dependency resolution. CI supplied the PLogIt runtime result.
- Direct local JudgeIt integration used OpenJDK 21.0.13, Jackson 3.1.7, Spring JDBC 7.0.10, PostgreSQL JDBC 42.7.12 and disposable PostgreSQL 17.12. It compiled the original parser, access policy, session registry and mutation gate, tenant guard, import controller (blob `d3caa903909299d9cd50e72484bbcf5585909a1b`), audit service (`0db3824a07a6b5f7969496225cc2dcf4243ec9ef`) and repository (`7f0d8d4bf7db2585a2cd40b5bcbe10f9a0befc9f`) with `javac -cp <Jackson, Spring and PostgreSQL jars> -d <classes> <sources and harness>`. A local Java harness used the same construction as JudgeIt's `ImportUploadTest` and applied the unmodified V1–V4 disposable database scripts over JDBC. The harness invoked `ImportController.upload` directly with an auditor session, matching CSRF and a server-resolved scope. It did not exercise an HTTP listener.
- The byte-identical producer fixture failed with INVALID_TARGETS and zero import batches, source records, links, digests or attempts. To reproduce diagnostics, copy the fixture, set inScopeTargets and logs[0].tags to empty arrays, and keep its original UUID and media reference. That copy produced one source and reported one omitted log with missingAttachmentBytes=true. Exact replay reused the same batch and immutable source UUID, with two distinct authenticated audit events. Changing attachmentFilenames[0] to renamed.png on that copy after import yielded IMPORT_CONTENT_CONFLICT. The receipt and stored sample.png provenance were preserved. A claimed attachmentHashes field, schemaVersion=999, a duplicate UUID, a mismatched bound producer ID and a foreign organization were all denied before any persisted import. Process exit was 0.
- The real reader still rejects the exact producer artifact. The optional versioned envelope, media/namespace policy and approval remain pending under DEC-2026-11-24-05. A test-only revert can remove the new PLogIt coverage without database rollback. `git diff --check` passed. JR-111–117 and JR-119 DONE. JR-118 and JR-120 IN REVIEW for exact legacy compatibility and version approval. Earlier JR-109/110 remain IN REVIEW.
- JR-111 `d71585e0b435`, JR-112 `597aa2be2617`, JR-113 `af2b6b6a763a`, JR-114 `7e6e1b8ef392`, JR-115 `ca7e39ed1ecc`, JR-116 `57263dc331e8`, JR-117 `083328a2025a`, JR-118 `e596d2f56586`, JR-119 `f8106376cd5b`, JR-120 `0234a6a492e2`. The reconciliation commit changes tracking only.
- Keep INT-001 provisional. Preserve existing disclosure, selection and unversioned exports. Phase 05 stale-draft DEC-2026-11-23-03, JR-040 scanner acceptance, JR-044/045 elapsed idle time, Phase 04 policy and direct API CSRF compatibility remain open. PR #27 stays draft above #26, with #22–#26 still unmerged. DEC-2026-11-06-11 production authorization remains pending.
- **READY WITH PROVISIONAL INTEGRATION** for safe independent Session 13, JR-121–JR-130. Start `work/plogit-phase-07` from the latest Phase 06 tracking head while PR #27 remains unmerged. Stop at JR-120.

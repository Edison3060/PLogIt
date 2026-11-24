# Session 11 export verification

## JR-101

Phase 06 is stacked on unmerged Phase 05 at `9efcae1ecccb9dbe8895e7d88a20253dbbace3db`. Phase 05 JR-094/095 and release acceptance remain open. The delayed draft save finding does not prevent isolated tests seeded with approved records. Baseline run [45190233070](https://github.com/Edison3060/PLogIt/actions/runs/45190233070) passed the non-production jobs at `1064cf0ff897`.

The controller handling POST /api/engagements/{id}/export calls transactional ExportService.export, which invokes MembershipService.assertLeader, queries APPROVED plus optional EXPORTED in creation order, builds EngagementReportData, selects a ReportFactory strategy, calls LogReviewService.transition(EXPORT) for each selected log and publishes EXPORT_ENGAGEMENT. Audit listeners own persistence. ExportIntegrationTest covers formats, selection counts, member/outsider denial, empty files and APPROVED to EXPORTED. The existing tests do not prove the exact mixed-state JSON IDs and per-log audit effects together. The live implementation matches the suite handoff research for selection. Historical PDF sort controls and future evidence packaging do not define current behavior. Source and tests only for this checkpoint. Reviewer is the Builder Lead.

## JR-102

DEC-2026-11-24-01 records acceptance. Send JSON {"format":"JSON","includeExported":false} or omit the optional flag. A valid active leader with authentication and CSRF receives a downloadable JSON report with APPROVED entries, with EXPORTED entries added when true. Report states are from before mutation. A new export persists APPROVED → EXPORTED and audit. Re-export leaves state EXPORTED and adds audit. An empty eligible set still produces a report and an aggregate audit event with logCount=0. An active member gets 403 and an outsider gets 404. Neither gets report data or export side effects. Changing permissions, selection, empty behavior or audit semantics requires product approval. The prescribed cases will be run on an isolated fixture.

## JR-103

The PostgreSQL-backed ExportIntegrationTest.jsonExportSelectsExactLogsAndCommitsAudit seeds DRAFT, SUBMITTED, APPROVED and EXPORTED plus another engagement's APPROVED log. The two authenticated MockMvc POSTs expect only the original APPROVED UUID by default, then two EXPORTED UUIDs in creation order with includeExported=true. Fresh reads assert all five persisted states and exact per-log plus aggregate audit effects. This test is ready for the Phase 06 CI run. Local Maven 3.9.11 failed at model resolution with DNS for repo.maven.apache.org before tests started. No runtime pass is claimed at entry.

## JR-104

ExportIntegrationTest.memberDenialDoesNotDiscloseOrMutateEligibleLogs checks HTTP 403 and 404, no download header or leaked log UUID/engagement/leader, unchanged APPROVED state and zero audit. ExportIntegrationTest.emptyJsonExportRecordsOnlyTheAggregateAudit checks an engagement with only SUBMITTED data returns HTTP 200 and a zero-entry report, no review-state write, and exactly one aggregate audit. PostgreSQL-backed execution is pending after local dependency resolution failed.

## JR-105

Source review confirms authorization before log lookup and output generation before mutations. The focused diff adds only MockMvc/PostgreSQL regressions. The outward API, state machine and transaction boundaries retain the baseline implementation. Reconcile JR-103/104 and this closure with the backend Maven job at the final executable SHA. If fixture expectations fail, inspect current behavior before altering an existing API. Reviewer is the Builder Lead. Phase 05 and owner release approvals remain open.

## JR-106

JsonReportStrategy and EngagementReportData at baseline `9efcae1ecccb9dbe8895e7d88a20253dbbace3db` own the unversioned record. The Boot JsonMapper determines physical date and null representation. ExportService converts persisted UUIDs with toString(), forwards raw source Markdown, and supplies internal rendered HTML and image bytes only to the report data model. The JSON strategy selects names only. ExportIntegrationTest previously checked the MIME and download header without a full payload assertion. Suite handoff research correctly maps the fields, but the product spec's historical PDF emphasis and the proposed versioned JSON envelope have no effect on this serializer. JudgeIt's provisional consumer schema requires array-valued inScopeTargets and tags and the producer model can pass null for either. The fixture will establish actual wire behavior. Reviewer is the Builder Lead.

## JR-107

DEC-2026-11-24-02 records legacy acceptance. The wire object uses a numeric local engagement ID, text metadata, calendar dates, ISO UTC instants, member details, total log count and a logs array. Each selected record carries a string UUID, raw description and result Markdown, review state and attachmentFilenames. Optional values must follow the serializer, including possible null for scoped targets and tags. The payload has no schemaVersion, media bytes or hashing claims. Keep the unversioned producer intact and compare any legacy reader with producer-derived evidence. New schema, idempotency, tenant or media policy needs separate approval.

## JR-108

ExportIntegrationTest.jsonSerializerMatchesLegacyFixture sends fixed synthetic EngagementReportData to the production JsonReportStrategy with the Spring-configured JsonMapper. The test compares every parsed JSON field with backend/src/test/resources/exports/legacy-evidence.json, including numeric engagement ID/count, calendar date, UTC instants, member metadata, Markdown, explicit nulls and a filename-only attachment. The test feeds internal HTML and synthetic image bytes to the producer model to distinguish them from wire fields. Full runtime match is pending. No JSON endpoint, production serializer or schema change.

## JR-109

On OpenJDK 21.0.13 with the PLogIt Maven-resolved Jackson 3.1.7 jars, compiled the unmodified [JudgeIt Java reader](https://github.com/Edison3060/JudgeIt/blob/work/judgeit-phase-06/backend/src/main/java/io/muzoo/ssc/judgeit/ingest/LegacyPLogItParser.java) (blob `994df722a4d0da16d2676fbc606ff6d1eaa892a1`) and a local harness with javac -cp <Jackson jars> -d <classes>, then ran java -cp <classes and jars> ReaderCheck <serializer-output>. The harness invoked LegacyPLogItParser.stage(byte[]) without HTTP or database publication.

| Input | Expected and observed |
|---|---|
| Exact Spring serializer bytes with inScopeTargets=null and tags=null | INVALID_TARGETS |
| Only inScopeTargets changed to [] in a synthetic variant | INVALID_TAGS |
| Both lists changed to [] in a diagnostic variant | One selected, one omitted, original UUID, sample.png and missingAttachmentBytes=true |
| Diagnostic variant with id=invalid-uuid | INVALID_LOG_ID |
| Diagnostic variant with schemaVersion=999 | UNSUPPORTED_PRODUCER_VERSION |
| Top-level array | INVALID_ENVELOPE |
| Diagnostic variant with logs={} | INVALID_LOG_COUNT |

The exact generated file is now stored at backend/src/test/resources/exports/legacy-evidence.json (SHA-256 bd07afa8df0b18a2b40ecd32863835c4e573732e58a70711a328908a14254e9c). The separate provisional bridge from PR #21 passed all 12 unit tests via python3 -m unittest discover -s tools/integrations/tests -p 'test_*.py' -v, and wrapped the actual serializer bytes as plogit-judgeit-provisional/0.1 with unchanged payload and source UUID. This is not the JudgeIt production contract. Keep INT-001 provisional. A JudgeIt-owned change to accept optional null arrays while preserving original evidence bytes is recommended for review. Authentication, authorized tenant publication, database/audit behavior and replay were not tested across apps in this session.

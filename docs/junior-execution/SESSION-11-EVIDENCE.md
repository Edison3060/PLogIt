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

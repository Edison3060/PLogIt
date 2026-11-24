# Session 11 export verification

## JR-101

Phase 06 is stacked on unmerged Phase 05 at `9efcae1ecccb9dbe8895e7d88a20253dbbace3db`. Phase 05 JR-094/095 and release acceptance remain open. The delayed draft save finding does not prevent isolated tests seeded with approved records. Baseline run [45190233070](https://github.com/Edison3060/PLogIt/actions/runs/45190233070) passed the non-production jobs at `1064cf0ff897`.

The controller handling POST /api/engagements/{id}/export calls transactional ExportService.export, which invokes MembershipService.assertLeader, queries APPROVED plus optional EXPORTED in creation order, builds EngagementReportData, selects a ReportFactory strategy, calls LogReviewService.transition(EXPORT) for each selected log and publishes EXPORT_ENGAGEMENT. Audit listeners own persistence. ExportIntegrationTest covers formats, selection counts, member/outsider denial, empty files and APPROVED to EXPORTED. The existing tests do not prove the exact mixed-state JSON IDs and per-log audit effects together. The live implementation matches the suite handoff research for selection. Historical PDF sort controls and future evidence packaging do not define current behavior. Source and tests only for this checkpoint. Reviewer is the Builder Lead.

## JR-102

DEC-2026-11-24-01 records acceptance. Send JSON {"format":"JSON","includeExported":false} or omit the optional flag. A valid active leader with authentication and CSRF receives a downloadable JSON report with APPROVED entries, with EXPORTED entries added when true. Report states are from before mutation. A new export persists APPROVED → EXPORTED and audit. Re-export leaves state EXPORTED and adds audit. An empty eligible set still produces a report and an aggregate audit event with logCount=0. An active member gets 403 and an outsider gets 404. Neither gets report data or export side effects. Changing permissions, selection, empty behavior or audit semantics requires product approval. The prescribed cases will be run on an isolated fixture.

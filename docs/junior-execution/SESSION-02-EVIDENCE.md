# Session 02: JR-011–JR-020 evidence, Phase 01B

- **Provenance:** canonical repo `Edison3060/PLogIt`; reviewed phase source at `da9ebfa80d7ed9a0632af24284c95f90e49474b1` (2026-11-06), no application code modifications in this session unless explicitly documented.
- **Scope:** original `01-SOURCE-BASELINE-AND-CONTROLLED-SCOPE.md`, JR-011–020 ONLY; session index maps this to 01B.
- **Methods:** GitHub immutable file/blob inspection and 33 synchronous read-only JavaScript assertions on source, plus actual pre-session Phase 01 branch GitHub Actions run [42212527183](https://github.com/Edison3060/PLogIt/actions/runs/42212527183) at `da9ebfa80d7ed9a0632af24284c95f90e49474b1`. The run completed **SUCCESS**: backend Maven, frontend lint/build/tests, Semgrep, Trivy, proxy PASS; production CD SKIPPED. Historical CI indicates regressions at that SHA, not a new isolated UI/browser test. A fresh branch-head CI gate will be checked after JR-020.
- **Runtime limits:** local container has Java 21, Node 22 and Git, but no Maven executable and cannot resolve github.com; no local Spring/PostgreSQL/browser test claimed. Static inspection is not an HTTP contract test.
- **Integrity:** authenticated session, CSRF, leader/engagement authorization, audit and legacy export behavior unchanged. Do not interpret source inventory as production deployment or real JudgeIt interoperability.


## JR-011 — source-and-tests inventory

**Observed source at phase SHA `da9ebfa80d7ed9a0632af24284c95f90e49474b1`:** Six Spring MVC controllers: `AuthController` (4 operations), `EngagementController` (8), `JoinController` (2), `LogController` (6), `AttachmentController` (3), `HealthController` (1): **24** annotated HTTP method mappings. Source is in `backend/src/main/java/io/muzoo/ssc/plogit/web/`. Ten page routes appear in `frontend/src/App.tsx`; the `/*` parent is an authenticated wrapper, not an eleventh page. Eleven existing UI modules used by these routes and reusable panels are under `frontend/src/pages/` and `frontend/src/components/engagement/`.

**Backend evidence owners:** `AuthIntegrationTest`, `EngagementAuthzIntegrationTest`, `LogAuthzIntegrationTest`, `ReviewTransitionIntegrationTest`, `ExportIntegrationTest`, `AttachmentIntegrationTest`, `AuditLogIntegrationTest`, `CsrfIntegrationTest`, `BadRequestIntegrationTest`, `LogVersioningIntegrationTest`, `AuthRateLimitIntegrationTest`, `OptimisticLockingIntegrationTest`, `ProductionProxyIntegrationTest`; frontend `ProtectedRoute.test.tsx`, `api.test.ts`, `EngagementPanels.test.tsx`, `LogDetail.test.tsx`, `theme.test.ts`. This names available test owners but does not assert full journey coverage.

**Historical vs current:** July prose can describe intended features; only current controllers and routes count as implemented. No JudgeIt-import, CVSS/finding/ticket or dedicated PLogIt reporting/scoring endpoint was found in six controllers. Deferred external apps/next-phase work are not shipped UI. **Reviewer:** Builder Lead read-only mapping; no source edits. Next JR-012.


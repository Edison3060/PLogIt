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


## JR-012 — acceptance and dated decision

**Invariant (DEC-2026-11-06-05):** Count only currently declared MVC mappings, React routes, API client methods, entity owners and existing tests. A page is accepted only when its corresponding `frontend/src/lib/{auth,engagements,logs}.ts` function maps to a current controller. Classify missing end-to-end UI coverage as **unverified**, never as a PASS. Accept legacy current behavior, not aspirational July claims. New endpoints/roles/fields require a focused scoped change with independent tests and, where product-facing, owner approval. **Expected request/data:** authenticated cookie and CSRF-backed `/api` APIs; engagement numeric IDs, log UUIDs; leader-only mutating export (JSON/CSV/PDF); state and audit remain authoritative. No changes approved by this verification.

**Decision owner:** Edison3060 for product/schema deviations; Builder Lead for an evidence-based no-change inventory. **Source:** `App.tsx`, `web/*Controller.java`, `frontend/src/lib`, Phase 01 roadmap, product/suite specs. **Expected vs observed:** six controllers / 24 mapping annotations, 10 leaf routes, unchanged no-scoring boundary. JR-013 exercises a structurally verifiable route-to-controller matrix.


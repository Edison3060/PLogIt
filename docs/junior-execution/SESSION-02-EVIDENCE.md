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


## JR-013 — reproducible success case

**Fixture:** An isolated read-only snapshot of `App.tsx`, six controller files, `frontend/src/lib/{api,auth,engagements,logs}.ts`, `Settings.tsx` and `JsonReportStrategy.java` obtained by GitHub `fetch_file` at `work/plogit-phase-01`, SHA `da9ebfa80d7ed9a0632af24284c95f90e49474b1`. No live customer records. A JavaScript structural assertion harness exercised **33/33 PASS, 0 FAIL**: ten expected React leaf-route strings; each of six controllers has expected 4/8/2/6/3/1 HTTP annotations; 14 client URL/method snippets; local profile read-only and exporter filename-only; controller route denylist for finding/ticket/CVSS endpoints. This checks source wiring only, not every route's runtime behavior.

**Page → client → controller → test map (current, not promised future features):**

| Shipped page | Actual client API / server controller | Existing test/evidence owner |
|---|---|---|
| `/login` | `auth.loginUser` → POST `/api/auth/login`, `AuthController` | `AuthIntegrationTest`, `api.test.ts` |
| `/register` | `auth.registerUser` → POST `/api/auth/register` | `AuthIntegrationTest`, `api.test.ts` |
| `/` Dashboard | `fetchEngagements`, `fetchCurrentUser` → GET `/api/engagements`, `/api/auth/me` | `EngagementAuthzIntegrationTest`, `ProtectedRoute.test.tsx` (partial) |
| `/engagements` | GET/POST `/api/engagements`, POST `/api/join` | `EngagementAuthzIntegrationTest` and join/audit tests |
| `/settings` | GET `/api/auth/me`; theme local-only, read-only profile | `AuthIntegrationTest`, `theme.test.ts` |
| `/engagements/:id` | GET/PUT `/api/engagements/{id}`, GET audit, POST export/join-code/transfer, DELETE member | `EngagementAuthzIntegrationTest`, `ExportIntegrationTest`, `AuditLogIntegrationTest`, `EngagementPanels.test.tsx` |
| `/engagements/:id/logs` | GET `/api/engagements/{id}/logs` | `LogAuthzIntegrationTest`, `ReviewTransitionIntegrationTest` (partial) |
| `/engagements/:id/logs/new` | POST `/api/engagements/{id}/logs` | `LogAuthzIntegrationTest` (partial) |
| `/engagements/:id/logs/:logId` | GET `/api/logs/{logId}`, GET history/attachments/download, POST review transition | `LogAuthzIntegrationTest`, `AttachmentIntegrationTest`, `ReviewTransitionIntegrationTest`, `LogDetail.test.tsx` |
| `/engagements/:id/logs/:logId/edit` | PUT `/api/logs/{logId}`; draft authorization | `LogAuthzIntegrationTest`, `LogVersioningIntegrationTest` |

**Independent non-page route:** GET `/api/health` `HealthController` used for readiness; not exposed as a page.

**Executed CI evidence:** [42212527183](https://github.com/Edison3060/PLogIt/actions/runs/42212527183), exact reviewed branch SHA, completed SUCCESS. No interactive/browser journey was executed during this session; frontend test coverage is scoped, so do not claim every page has an E2E test. **Expected:** ten mapped routes, 24 controller operations; **observed:** yes. Next JR-014.


## JR-014 — negative classification

**Invalid claims tested against frozen sources:** (a) "PLogIt ships findings, CVSS scoring or remediation tickets" → REJECTED: none of six controllers declares those endpoints, product spec assigns them to other apps; (b) "Export JSON contains all image evidence" → REJECTED: `JsonReportStrategy.java` emits `attachmentFilenames`, not bytes/hashes; (c) "green CI proves live production deploy" → REJECTED: the workflow `deploy` job is gated on `refs/heads/main`, and reviewed run's CD job was SKIPPED; (d) "JudgeIt imports the provisional envelope" → REJECTED: prototype is file-only with a synthetic mock on separate PR #21.

**Negative harness evidence:** controller denylist assertion PASS; filename-only assertion PASS; workflow gate and skipped deploy read from real `.github/workflows/ci.yml` and run [42212527183](https://github.com/Edison3060/PLogIt/actions/runs/42212527183). This is rejection of an unsupported **documentation/requirement claim**, not an HTTP denial endpoint, so no fake 403/rollback scenario was asserted. **Side effects:** read-only source inspection and tests, zero API writes or auth bypass, no export/deployment executed. Next JR-015.


## JR-015 — closure and regression

Compared JR-011 source owners, JR-012 acceptance/DEC-2026-11-06-05, JR-013 page-to-client-to-controller test map and 33/33 structural source assertions, and JR-014 invalid historical-claim rejections. **DONE / VERIFIED NO APPLICATION CHANGE**, because the inventory and negative classification are documented, not evidence of complete interactive browser E2E coverage. Reference CI [run 42212527183](https://github.com/Edison3060/PLogIt/actions/runs/42212527183) at `da9ebfa8`: Maven and frontend, Semgrep, Trivy and proxy PASS; CD SKIPPED. The branch already includes approved APT CVE correction. No local Maven/browser execution during this checkpoint. Regression must be re-checked on final branch head. No app code/schema change or data migration, rollback would revert docs only. **Reviewer:** Builder Lead; next JR-016.


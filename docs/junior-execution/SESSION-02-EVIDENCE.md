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


## JR-016 — unresolved decisions and rebaseline inventory

**Source owner:** `planning/RES-application-plan/Suite - Research Refresh 2026-11-02.md`, `Suite - To Be Scoped.md`, `Suite - Handoff Contracts and Evidence.md`, `PlogIt Workspace/Project Info/PLogIt - Product Spec.md`, `docs/integrations/PROVISIONAL-SUITE-CONTRACTS.md`, `DECISIONS.md`, current `ExportService.java`, `JsonReportStrategy.java`, `MembershipService.java`, `backend/pom.xml`, `frontend/package.json`, `.github/workflows/ci.yml`. Mirrored 2026 research supersedes July deployment/version claims; no edits to those frozen source documents.

**Inventory (classification is the outcome; decisions are not silently approved):**

| Topic | Verified current fact | Working assumption / open decision | Approval / verification |
|---|---|---|---|
| Log inclusion | Leader POST export includes APPROVED by default, optionally prior EXPORTED; mutates state/audit | If JudgeIt needs DRAFT/SUBMITTED, change requires separate product authorization and regression tests | OPEN, do not change exporter |
| Media packaging | Export JSON has `attachmentFilenames`, no image bytes/hashes | Self-contained bundle, redaction and integrity scheme to define | OPEN, no claim of evidence completeness |
| Tenant/source identity | Engagement-local numeric ID and log UUID; membership/leader enforcement; no approved cross-instance identity | Operator-assigned `sourceNamespace` in provisional adapter, not org derived from email | PROVISIONAL; live consumer isolation unverified |
| Sensitive evidence | Auth/CSRF/review controls exist; exact cross-app retention, permission/consent and classification policy not accepted | Local file-only, 0600 prototype output, no live transport, no secrets | OPEN owner privacy policy before real transfer |
| Contract schema | Producer JSON unversioned; separate opt-in `plogit-judgeit-provisional/0.1` wrapper on PR #21 | Versioning/idempotency/replay rules of future JudgeIt receiver | PROVISIONAL; 12 synthetic mock tests PASS in CI 42212049183; real import NOT verified |
| Tech stack | Current Spring Boot 4.1.1 / Java 21, React 18 / Vite 6, PostgreSQL 16 | Modernization prioritization and new-app stacks are separate scopes | OPEN; no unapproved upgrade |
| Production | Main CD path runs on green `main` commits, has fingerprint preflight; Phase 01 push CI run 42212527183 green but CD skipped | Safe merge/release gate before `main` merge | **BLOCKED for release, not for read-only inventory**; owner approval needed |

**Integration policy file created:** `INTEGRATION-ASSUMPTIONS.md` tracks field names and status explicitly, cross-referencing existing `docs/integrations/PROVISIONAL-SUITE-CONTRACTS.md`. Owner-authorized DEC-2026-11-06-04 permits reversible local mock work, not live interoperability or production. No tests executed against other apps. **Reviewer:** Builder Lead; next JR-017.


## JR-017 — acceptance: facts vs assumptions vs approval

**Acceptance standard:** Every unresolved item must include date, exact current source, observed fact, target/proposal, owner, approval state and a no-accidental-change action. Current PLogIt behavior is the default; source facts are not permission to change contracts. DEC-2026-11-06-06 through 10 record distinct **PENDING** choices for log inclusion, evidence packaging, tenant namespace, sensitive-data privacy and modernization. DEC-2026-11-06-04 already authorizes a replaceable file-only mock wrapper as **PROVISIONAL**, not a final schema. Any new producer/export behavior needs explicit product sign-off and security/regression testing. Merge into `main` is separately release-gated. No JR in this inventory is BLOCKED solely because JudgeIt is unbuilt; owners can decide final interoperability later. **Reviewer:** Builder Lead.

**Expected:** documented, dated truth table; no unsupported security, production, tenant or schema claim marked DONE. **Observed:** source and decision ledger distinguish owner-reserved choices from tested legacy behavior. No code changes. Next JR-018.


## JR-018 — synthetic register success case

**Isolated fixture:** Seven non-customer decision items `LOG`, `MEDIA`, `TENANT`, `PRIVACY`, `SCHEMA`, `STACK`, `RELEASE`, each with `source`, `actual`, `proposal`, and `approval` fields, checked by synchronous JavaScript structural assertions inside the authorized Builder Lead tool environment. **Executed 7/7 PASS, 0 FAIL**: seven unique topics; non-empty source/actual/proposal; approvals classified rather than inferred; release NOT_AUTHORIZED; producer log inclusion unchanged; versioned wrapper PROVISIONAL; media incompleteness explicit.

**Source-linked fact/assumption/approval register, dated 2026-11-06:**

| ID | Actual, source of truth | Proposed or deferred | Approval |
|---|---|---|---|
| LOG | `backend/src/main/java/io/muzoo/ssc/plogit/service/ExportService.java`, `JsonReportStrategy.java`: APPROVED default, optional EXPORTED, mutation/audit | JudgeIt inclusion policy beyond current states | PENDING owner, DEC-06 |
| MEDIA | `JsonReportStrategy.java`: filenames, no bytes/hashes | integrity/redaction/self-contained bundle | PENDING owner, DEC-07 |
| TENANT | `MembershipService.java`: per-engagement membership and local IDs; `docs/integrations/PROVISIONAL-SUITE-CONTRACTS.md` | operator sourceNamespace for mock, real tenant identity agreement | PROVISIONAL mock; PENDING real mapping DEC-08 |
| PRIVACY | `planning/RES-application-plan/Suite - To Be Scoped.md` | transfer consent, roles, retention/erasure policies | PENDING owner, DEC-09 |
| SCHEMA | `docs/integrations/PROVISIONAL-SUITE-CONTRACTS.md`: actual unversioned producer | `plogit-judgeit-provisional/0.1` opt-in adapter, not producer contract | PROVISIONAL authorized DEC-04; real receiver UNVERIFIED |
| STACK | `backend/pom.xml`, `frontend/package.json` | controlled upgrade in later JRs, not Phase 01 | PENDING scheduling DEC-10 |
| RELEASE | `.github/workflows/ci.yml`, run [42212527183](https://github.com/Edison3060/PLogIt/actions/runs/42212527183) | safe `main` merge/deploy gating | NOT AUTHORIZED: no production release |

**Validation meaning:** This tests consistency of the decision fixture, not live API or remote-import operation. Real downstream validation remains pending and is not marked DONE. No web research needed beyond source and existing 2026 dated citations in plan. **Reviewer:** Builder Lead; next JR-019.


## JR-019 — negative assumption-denial test

**Isolated synthetic denial fixture, executed now:** six claims with attempted `DONE` statuses, classified from available source/approval/test evidence. **6/6 PASS**, including five rejected unsupported assertions and one allowed verified current-state control.

| Attempted claim | Expected / observed | Evidence |
|---|---|---|
| Production already deployed | BLOCKED / BLOCKED | `deploy` only on main, branch run 42212527183 skipped CD |
| Real JudgeIt accepts provisional versioned JSON | BLOCKED / BLOCKED | PR #21 mock bridge 12/12 test success only; no real receiving app |
| Attachment content integrity proven | BLOCKED / BLOCKED | `JsonReportStrategy` filename-only |
| Cross-tenant identity approved | BLOCKED / BLOCKED | only local engagement membership; sourceNamespace operator-assigned mock |
| Retention, consent, transfer privacy approved | BLOCKED / BLOCKED | `Suite - To Be Scoped.md` unresolved, DEC-09 pending |
| Existing approved-only export semantics implemented | DONE / DONE | current `ExportService`/controller and real prior `ExportIntegrationTest` run |

**Meaning of BLOCKED:** the *claim of final security/schema/deployment approval* is blocked, **not** these source-baseline JRs or reversible mock implementation. Rejection is a pure classification without HTTP endpoint, transaction or user data mutation; no 403 or database rollback invented. Current app permission checks are covered by existing backend regression suite in green Actions run. **Decision DEC-2026-11-06-11** records main merge/release pending owner authorization. Alternatives of bypassing CI, inventing deployment or calling mocked JudgeIt interoperable rejected. No code change. Next JR-020.


## JR-020 — close rebaseline verification and bounded session

**Reviewed:** JR-016 fact/deferred inventory; JR-017 dated DEC-06..10 recommended pending decisions; JR-018 seven-source register and **7/7** JS assertions; JR-019 negative fixture **6/6** denial/positive-control tests. Earlier JR-011..015 inventory verified: 33/33 structural source assertions, ten route/client/controller/test mappings, six Spring MVC controllers and 24 annotated operations. No app code/API/schema/auth/export/deployment change in Session 02; no production deploy, no use of real customer evidence. **DONE for scoped Phase 01B evidence tasks**, not sign-off of unbuilt integrations or production release. No migration/rollback necessary for docs-only commits; revert JR checkpoints if evidence is later contradicted.

**Actual runtime CI evidence:** last known completed source-equivalent phase branch run [42212527183](https://github.com/Edison3060/PLogIt/actions/runs/42212527183) had backend Maven, frontend tests/build/lint, Semgrep, Trivy image and filesystem, proxy PASS; deployment SKIPPED. That run predates these documentation checkpoints. **Mandatory current-head CI check is PENDING** until run completes; never call it passed merely because code is unchanged. Phase 01 acceptance comprises JR-001–020, but phase merge cannot occur without current green checks AND safe production-CD decision DEC-11. Any final PR is to remain open/draft; do not merge under current authorization. **Reviewer:** Builder Lead.

**Session boundary:** STOP after JR-020. Next action is inspect final HEAD CI and Phase 01 PR, resolve release gate without deployment, then request owner sign-off for Session 03 (JR-021–030). No JR-021 action started.


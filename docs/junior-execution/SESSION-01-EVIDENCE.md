# Session 01 — source identity and product boundary evidence

**Reviewed 2026-11-06 (UTC).** Repository: `Edison3060/PLogIt`. Execution owner/reviewer: Builder Lead; owner sign-off pending. Source code unchanged throughout this session unless a later entry explicitly says otherwise. This file separates API/static assertions from integration tests actually executed.

## JR-001 — Canonical source inventory

| Role | Location / SHA | Observed purpose |
|---|---|---|
| Canonical editable app | `Edison3060/PLogIt` `main`, `852cfbaa6682de8e99895a63c09515ea659e4b94` | Live code at `backend/` and `frontend/`, root workflows and README |
| Historical last-reviewed source | `Edison3060/PLogIt` `28aa1561cb59637e954a08caf2a2ed068178a9fb` | Reference source baseline, not the current head |
| Read-only archived snapshot | `Edison3060/RES-application-plan` `596f8a1ad0997f2cdacfcc859107f3b642e2e545` under `PlogIt Workspace/PLogIt/` | Mirror of the standalone implementation, no authorized independent code work |
| Plan mirror | `planning/RES-application-plan/` | Archived specs and phases, not executable source |

**Executed provenance fixture:** Compare `GET /repos/Edison3060/PLogIt/git/trees/852cfbaa6682de8e99895a63c09515ea659e4b94?recursive=1` to `GET /repos/Edison3060/RES-application-plan/git/trees/596f8a1ad0997f2cdacfcc859107f3b642e2e545?recursive=1`, normalize RES `PlogIt Workspace/PLogIt/` prefix and ignore canonical `planning/`, `docs/junior-execution/`, and `AGENTS.md`. Assert identical Git blob IDs for each canonical application file. **Observed: 185/185 matched, 0 mismatches**; archive-only `docs/PLOGIT-RESEARCH-UPDATE-PLAN-2026-11.md`. `compare_commits(28aa156..852cfbaa)` reported six ahead, zero behind, and only planning/execution documentation differences.

**Owner/source-and-test map:** `LogController` / `LogService` own log capture; `EngagementController` / `ExportService` and `JsonReportStrategy` own leader export; `LogEntry` and `ReviewState` own persisted log fields/states; JUnit `ExportIntegrationTest`, `ReviewTransitionIntegrationTest`, `LogAuthzIntegrationTest` contain synthetic coverage. React `frontend/src/App.tsx` registers pages. Current-source review only; listing a test does not mean it ran.

**Expected / observed:** The deployed revision cannot be inferred from main; repository authoritative source is canonical, and archived copy is identical for application files. No production state was inspected.

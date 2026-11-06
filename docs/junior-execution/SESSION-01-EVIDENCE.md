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

## JR-002 — Specify Canonical source identity acceptance

**Expected:** Specify Canonical source identity acceptance. **Actual:** DEC-2026-11-06-01 confirms canonical main as sole editable source; copied RES snapshot is a read-only planning reference. Expected main at reviewed SHA 28aa156; actual current main 852cfbaa remains identical for all 185 app blobs, six docs-only commits newer. No approval to fork the RES source or change applications.

**Executed check / reproducible fixture:** read-only compare_commits and two Git Trees: 185/185 identical; no runtime test applicable to editorial source-of-truth decision. Evidence sources: GitHub tree APIs for `Edison3060/PLogIt@852cfbaa` and read-only `Edison3060/RES-application-plan@596f8a1`, commit comparison `28aa156...852cfbaa`, `AGENTS.md`, `planning/README.md`, and Phase 01 roadmap. A source policy refusal is not an HTTP error. Reviewer: Builder Lead; owner sign-off pending.


## JR-003 — Reproduce Canonical source identity success

**Expected:** Reproduce Canonical source identity success. **Actual:** Reproduced canonical provenance against immutable GitHub REST tree objects at canonical 852cfbaa and RES 596f8a, with 28aa156 as baseline. On a read-only isolated source fixture, 185 canonical app blobs have identical archived SHA. Only archive extra is research markdown. The six later main commits are planning/docs only.

**Executed check / reproducible fixture:** Executed GitHub trees read and JS SHA equality assertions: 185 equal, 0 unequal; API returned success; no local app data required for this source-only task. Evidence sources: GitHub tree APIs for `Edison3060/PLogIt@852cfbaa` and read-only `Edison3060/RES-application-plan@596f8a1`, commit comparison `28aa156...852cfbaa`, `AGENTS.md`, `planning/README.md`, and Phase 01 roadmap. A source policy refusal is not an HTTP error. Reviewer: Builder Lead; owner sign-off pending.


## JR-004 — Reject Canonical source identity invalid operation

**Expected:** Reject Canonical source identity invalid operation. **Actual:** Invalid proposal: treat RES nested PLogIt Workspace/PLogIt copy as an independent editable development fork. Rejected by source-ownership policy from AGENTS.md, planning/README.md, SESSION-PROTOCOL.md and the matching app blob identities. Check performed using read-only API; no RES write, transaction, authorization bypass, or app state affected. There is no HTTP endpoint for this repository-policy rejection.

**Executed check / reproducible fixture:** Read-only Git tree and commit compare plus policy check: RES nested snapshot is not a canonical branch; no write attempted; 0 application mutations. A runtime HTTP denial test is not applicable to repository provenance. Evidence sources: GitHub tree APIs for `Edison3060/PLogIt@852cfbaa` and read-only `Edison3060/RES-application-plan@596f8a1`, commit comparison `28aa156...852cfbaa`, `AGENTS.md`, `planning/README.md`, and Phase 01 roadmap. A source policy refusal is not an HTTP error. Reviewer: Builder Lead; owner sign-off pending.


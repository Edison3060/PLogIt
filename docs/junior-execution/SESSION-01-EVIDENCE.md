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


## JR-005 — Close Canonical source identity verification

**Expected:** Close Canonical source identity verification. **Actual:** Reviewed JR-001 inventory, JR-002 dated source decision, JR-003 185/185 success and JR-004 invalid fork rejection. No defect found in application code, no migration or rollback required. No RES edits. Scope closed VERIFIED NO CHANGE after source-provenance regression (same immutable SHAs).

**Executed check / reproducible fixture:** Regressed git tree identity: 185 canonical application SHA matches RES; compare 28aa156..852cfbaa only documentation. No Maven or npm runtime tests claimed for source provenance. Evidence sources: GitHub tree APIs for `Edison3060/PLogIt@852cfbaa` and read-only `Edison3060/RES-application-plan@596f8a1`, commit comparison `28aa156...852cfbaa`, `AGENTS.md`, `planning/README.md`, and Phase 01 roadmap. A source policy refusal is not an HTTP error. Reviewer: Builder Lead; owner sign-off pending.


## JR-006 — Inventory Product role boundary

**Expected / boundary:** Inventory Product role boundary. **Observed source:** Read mirrored Suite - Master Spec.md and PLogIt - Product Spec.md, current README, Java LogController, LogService, LogEntry, ExportService, JsonReportStrategy and React routes. Actual source owner is PLogIt main 852cfbaa, not archived RES source. JUnit ExportIntegrationTest (12 @Test), ReviewTransitionIntegrationTest (19), LogAuthzIntegrationTest (11) map the relevant PLogIt behavior; downstream JudgeIt/PatchIt/GaugeIt are planning scope, not implemented integrations.

**Executed checks:** GitHub fetch_file read assertions, JUnit method inventory, 9/9 source-contract checks executed; listing tests is not execution. For source-contract test rerun, use GitHub `fetch_file` on `backend/src/main/java/io/muzoo/ssc/plogit/{web/EngagementController.java,web/LogController.java,service/ExportService.java,service/export/JsonReportStrategy.java,domain/LogEntry.java}` at `852cfbaa` and assert `@PostMapping("/{id}/export")`, `membershipService.assertLeader(engagement, leader)`, APPROVED/optional EXPORTED selection, attachmentFilenames and absence of severity/CVSS/ticket routes. The 2026-11-06 read-only nine-assertion set reported 9 passed, 0 failed.

**Synthetic test fixture (source inspected; execution pending CI where noted):** `backend/src/test/java/io/muzoo/ssc/plogit/ExportIntegrationTest.java` uses isolated `PostgresTestConfig`, `createUser("leader@test.local")`, `createEngagement`, `createLog(..., APPROVED)` and MockMvc `POST /api/engagements/{id}/export` with CSRF and `{"format":"JSON"}`; checks 200, JSON content, disposition and state EXPORTED. Negative fixtures create member/nonmember/anonymous and expect 403/404/403. Reviewer Builder Lead; owner sign-off at session boundary.


## JR-007 — Specify Product role boundary acceptance

**Expected / boundary:** Specify Product role boundary acceptance. **Observed source:** Accepted invariant from approved product spec: PLogIt produces engagement-scoped activity logs, review history and leader-created evidence exports, not findings, CVSS/CVE scoring, remediation tickets, SLAs or client findings reports. Current request POST /api/engagements/{id}/export with {format:'JSON', includeExported?:boolean} and role guard match. Wire JSON unversioned and attachmentFilenames only; do not change contract. Dated engineering confirmation DEC-2026-11-06-02; product expansion requires owner approval.

**Executed checks:** Source-contract check includes export route, assertLeader, default APPROVED state, schemaVersion absence, attachmentFilenames and absence of scoring fields, 9/9 PASS. For source-contract test rerun, use GitHub `fetch_file` on `backend/src/main/java/io/muzoo/ssc/plogit/{web/EngagementController.java,web/LogController.java,service/ExportService.java,service/export/JsonReportStrategy.java,domain/LogEntry.java}` at `852cfbaa` and assert `@PostMapping("/{id}/export")`, `membershipService.assertLeader(engagement, leader)`, APPROVED/optional EXPORTED selection, attachmentFilenames and absence of severity/CVSS/ticket routes. The 2026-11-06 read-only nine-assertion set reported 9 passed, 0 failed.

**Synthetic test fixture (source inspected; execution pending CI where noted):** `backend/src/test/java/io/muzoo/ssc/plogit/ExportIntegrationTest.java` uses isolated `PostgresTestConfig`, `createUser("leader@test.local")`, `createEngagement`, `createLog(..., APPROVED)` and MockMvc `POST /api/engagements/{id}/export` with CSRF and `{"format":"JSON"}`; checks 200, JSON content, disposition and state EXPORTED. Negative fixtures create member/nonmember/anonymous and expect 403/404/403. Reviewer Builder Lead; owner sign-off at session boundary.


## JR-008 — Reproduce Product role boundary success

**Expected / boundary:** Reproduce Product role boundary success. **Observed source:** Traced existing synthetic JUnit fixture: create leader, engagement and APPROVED log, POST /api/engagements/{id}/export with CSRF and JSON format; expected HTTP 200 application/json, content-disposition and persisted EXPORTED state. Actual source flow EngagementController.export→ExportService.assertLeader→findIncludedLogs→JsonReportStrategy.generate→markExported, with audit event. Suite roles: PLogIt pentester capture→JudgeIt auditor score/finding→PatchIt blue team remediate→GaugeIt leadership metrics. Downstream steps documented only, not runnable.

**Executed checks:** Read-only source-contract assertions 9/9 PASS; ExportIntegrationTest.leaderCanExportJsonAndMarksLogsExported JUnit fixture inspected, NOT locally executed; branch CI push build/test pending. For source-contract test rerun, use GitHub `fetch_file` on `backend/src/main/java/io/muzoo/ssc/plogit/{web/EngagementController.java,web/LogController.java,service/ExportService.java,service/export/JsonReportStrategy.java,domain/LogEntry.java}` at `852cfbaa` and assert `@PostMapping("/{id}/export")`, `membershipService.assertLeader(engagement, leader)`, APPROVED/optional EXPORTED selection, attachmentFilenames and absence of severity/CVSS/ticket routes. The 2026-11-06 read-only nine-assertion set reported 9 passed, 0 failed.

**Synthetic test fixture (source inspected; execution pending CI where noted):** `backend/src/test/java/io/muzoo/ssc/plogit/ExportIntegrationTest.java` uses isolated `PostgresTestConfig`, `createUser("leader@test.local")`, `createEngagement`, `createLog(..., APPROVED)` and MockMvc `POST /api/engagements/{id}/export` with CSRF and `{"format":"JSON"}`; checks 200, JSON content, disposition and state EXPORTED. Negative fixtures create member/nonmember/anonymous and expect 403/404/403. Reviewer Builder Lead; owner sign-off at session boundary.


## JR-009 — Reject Product role boundary invalid operation

**Expected / boundary:** Reject Product role boundary invalid operation. **Observed source:** Explicit scope-rejection review: proposed PLogIt finding/CVSS/ticket endpoints or data model are disallowed under locked Product Spec A1 and Suite Master Spec. Static inspection found no severity/CVSS/CVE/ticket fields in LogEntry, no finding/CVSS/ticket routes in LogController and EngagementController. No runtime request endpoint exists to reject; no writes performed. For actual unauthorized export, JUnit fixtures memberCannotExport (403), nonMemberCannotExport (404), unauthenticatedCannotExport (403) were inspected, not executed locally.

**Executed checks:** Source-contract assertions 'log is not finding' and 'out-of-scope routes absent' passed; 9/9 source suite, no HTTP authorization test executed in this connector environment. For source-contract test rerun, use GitHub `fetch_file` on `backend/src/main/java/io/muzoo/ssc/plogit/{web/EngagementController.java,web/LogController.java,service/ExportService.java,service/export/JsonReportStrategy.java,domain/LogEntry.java}` at `852cfbaa` and assert `@PostMapping("/{id}/export")`, `membershipService.assertLeader(engagement, leader)`, APPROVED/optional EXPORTED selection, attachmentFilenames and absence of severity/CVSS/ticket routes. The 2026-11-06 read-only nine-assertion set reported 9 passed, 0 failed.

**Synthetic test fixture (source inspected; execution pending CI where noted):** `backend/src/test/java/io/muzoo/ssc/plogit/ExportIntegrationTest.java` uses isolated `PostgresTestConfig`, `createUser("leader@test.local")`, `createEngagement`, `createLog(..., APPROVED)` and MockMvc `POST /api/engagements/{id}/export` with CSRF and `{"format":"JSON"}`; checks 200, JSON content, disposition and state EXPORTED. Negative fixtures create member/nonmember/anonymous and expect 403/404/403. Reviewer Builder Lead; owner sign-off at session boundary.


## JR-010 — Close Product role boundary verification

**Expected / boundary:** Close Product role boundary verification. **Observed source:** Reviewed JR-006 inventory, JR-007 dated boundary acceptance, JR-008 source trace and pending synthetic runtime CI, JR-009 static rejection and existing unauthorized-export tests. Application source stays unchanged. Closure is provisional until branch CI confirms Maven/JUnit and frontend tests and any failures are resolved; no claim of end-to-end JudgeIt integration or self-contained evidence pack.

**Executed checks:** Executed 9/9 static source-contract assertions; JUnit ExportIntegrationTest, ReviewTransitionIntegrationTest, LogAuthzIntegrationTest only inspected; GitHub CI started by branch pushes, awaiting final HEAD run results. For source-contract test rerun, use GitHub `fetch_file` on `backend/src/main/java/io/muzoo/ssc/plogit/{web/EngagementController.java,web/LogController.java,service/ExportService.java,service/export/JsonReportStrategy.java,domain/LogEntry.java}` at `852cfbaa` and assert `@PostMapping("/{id}/export")`, `membershipService.assertLeader(engagement, leader)`, APPROVED/optional EXPORTED selection, attachmentFilenames and absence of severity/CVSS/ticket routes. The 2026-11-06 read-only nine-assertion set reported 9 passed, 0 failed.

**Synthetic test fixture (source inspected; execution pending CI where noted):** `backend/src/test/java/io/muzoo/ssc/plogit/ExportIntegrationTest.java` uses isolated `PostgresTestConfig`, `createUser("leader@test.local")`, `createEngagement`, `createLog(..., APPROVED)` and MockMvc `POST /api/engagements/{id}/export` with CSRF and `{"format":"JSON"}`; checks 200, JSON content, disposition and state EXPORTED. Negative fixtures create member/nonmember/anonymous and expect 403/404/403. Reviewer Builder Lead; owner sign-off at session boundary.


## Session 01 final CI acceptance and security exception — 2026-11-06T11:07:09.833Z

**Actually executed remote test commands:** GitHub Actions [run 42206996982](https://github.com/Edison3060/PLogIt/actions/runs/42206996982) at commit `ec8f789a95b7d48f17b478b1cb518f91a7bad126`, triggered by push to `work/plogit-phase-01`. Back end `mvn -B verify`: 125 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS. Within those tests: ExportIntegrationTest 12/12, ReviewTransitionIntegrationTest 19/19, LogAuthzIntegrationTest 11/11. Frontend `npm ci`, `npm run lint --if-present`, `npm run build`, `npm test`: 5 files, 18/18 tests passed. Semgrep SAST SUCCESS; production proxy validation SUCCESS. Static source-contract 9/9 checks and provenance 185/185 matches recorded above.

**Do not overclaim:** Overall workflow result **FAILURE**, not green. Backend Trivy image scan `--exit-code 1 --ignore-unfixed` identified Ubuntu 26.04 `apt` and `libapt-pkg7.0` version `3.2.0` as HIGH CVE-2026-109518, fixed in `3.2.1ubuntu0.1`; exit code 1. Frontend image security scans skipped due to this failure. No production deployment ran. The original pinned runtime base is `backend/Dockerfile` `eclipse-temurin:21-jre@sha256:0b99b3376fbb50dbf9c0af8535fdd5b8eb0d3fdfc8b1e4967f383b6919f6901a`. This scan failure is a phase PR gate blocker; no image change in this source-baseline session.

**Official research used to validate changing security information (accessed 2026-11-06 UTC):** Ubuntu security notice `https://ubuntu.com/security/notices/USN-8996-1` (published 2026-11-04), `https://ubuntu.com/security/CVE-2026-109518` (updated 2026-11-04), and `https://ubuntu.com/blog/apt-vulnerability-fixes-available`. Separate from executed tests.

**Per-step checkpoint commits:** JR-001 `6fe55120fc39e2cb6e82017580df22b29899445a`; JR-002 `28afba83cc6e2fbf0aad2f53faa95e57d8bcf3eb`; JR-003 `69eaa5c2545ed354b2fea0aade4fa6b7d074a5cc`; JR-004 `55b6c3cadceca71752a5d7a3ca783f9667ab6f93`; JR-005 `4f39193c5e3b2f82cf4d1b07f5f6388ad49cc8da`; JR-006 `ada67aa9b88db821a1fd7e1adc06a4ed626132fa`; JR-007 `9985470a01e2eaeb172191c011c9cc59493d6992`; JR-008 `0f9a8b70b6455c21daacd402073ddfdf03ee73d8`; JR-009 `306b383a94111b76ad4bed9540a04f01c59564fb`; JR-010 `ec8f789a95b7d48f17b478b1cb518f91a7bad126`. An additional JR-010 closeout checkpoint saves this updated evidence and handoff. App source delta vs original main: zero.

## Post-Session 01 security hotfix evidence — 2026-11-06T11:18:58.582Z (not a JR step)

- **Separate PR:** https://github.com/Edison3060/PLogIt/pull/20; branch `fix/ubuntu-apt-cve-2026-109518`, commit `5c5657aa0997a53a6926ebd03375f06d776f9bc1` (one file: `backend/Dockerfile`, +7/-0).
- **Exact build check:** `apt-get -o Acquire::Retries=0 update`, `apt-get -o Acquire::Retries=0 install -y --no-install-recommends --only-upgrade apt libapt-pkg7.0`, `dpkg --compare-versions` both packages against `3.2.1ubuntu0.1`. Image build observed both upgraded from 3.2.0 to 3.2.1ubuntu0.1, cached lists removed. Base image digest unchanged. No scanner bypass.
- **Actual GitHub CI:** https://github.com/Edison3060/PLogIt/actions/runs/42208728367 completed **SUCCESS**: backend Maven 125/125, frontend 18/18, Semgrep and proxy SUCCESS, Trivy filesystem and both blocking image scans SUCCESS, container builds/healthchecks SUCCESS. This evidence applies to **hotfix commit only**, not the earlier Phase 01 branch head.
- **Merge/deploy restriction:** PR #20 is open, not merged. Main branch CI automatically invokes production CD when all checks pass, and the owner has not separately authorized production deployment. Phase 01 session boundary remains JR-010; do not automatically begin JR-011 or mark JR-191 completed. Source: `.github/workflows/ci.yml`.

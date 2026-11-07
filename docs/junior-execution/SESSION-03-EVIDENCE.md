# Session 03: reproducible local engineering evidence (JR-021–JR-030)

- Controller: Session 03, claimed on `control/session-state` at 2026-11-07T04:24:33.825Z; single Lead, no subagents.
- Stacked branch: `work/plogit-phase-02` based on unmerged Phase 01 head `6426609e77b436e2804b9c65c9c3701134c54967`; Phase 01 draft PR #22 is not accepted/merged. `main` unchanged.
- Scope: verify Maven/Spring and React/Vite reproducibility, record positive and negative gates; no change to application runtime, security, schemas, legacy JSON or production CD.
- Prior valid CI baseline: [Phase 01 source-identical run 42228277341](https://github.com/Edison3060/PLogIt/actions/runs/42228277341) at `6426609e77b436e2804b9c65c9c3701134c54967`, overall SUCCESS, backend `mvn -B verify`, frontend `npm ci`, lint, build, test, Semgrep, Trivy, proxy SUCCESS, production CD SKIPPED. This is **inherited proof, not a new Session 03 local run**.
- Session runtime: isolated sandbox JDK 21.0.12.1, Node 22.16.0, npm 10.9.2; no Maven executable, no Docker daemon, outbound github.com DNS unavailable. Do not call repository Maven/React tests locally completed. Build confirmation is via new GitHub Actions results, when finished.

## JR-021
- Requirement: inventory `backend/pom.xml` and `backend/src/test` at actual canonical source. **DONE, VERIFIED NO APPLICATION CHANGE.**
- Source: `backend/pom.xml` blob `15e207eb6a6aac19d926f2d965ba3daa676b9e57` parent `org.springframework.boot:spring-boot-starter-parent:4.1.1`, `java.version=21`, Jackson/JPA/Security/Validation, Flyway PostgreSQL, JUnit Spring test starters and Testcontainers dependencies; version field is source-measured, not stale July claims.
- Test inventory: 15 top-level tests (`AttachmentIntegrationTest`, `AuditLogIntegrationTest`, `AuthIntegrationTest`, `AuthRateLimitIntegrationTest`, `BadRequestIntegrationTest`, `CsrfIntegrationTest`, `EngagementAuthzIntegrationTest`, `ExportIntegrationTest`, `LogAuthzIntegrationTest`, `LogVersioningIntegrationTest`, `MarkdownSanitizationTest`, `OptimisticLockingIntegrationTest`, `PlogitApplicationTests`, `ProductionProxyIntegrationTest`, `ReviewTransitionIntegrationTest`) with `support/IntegrationTestBase.java` (Spring `MockMvc`, isolated synthetic fixtures, DB cleanup) and `support/PostgresTestConfig.java` (Testcontainers `postgres:16-alpine`). Test files are canonical, not frozen mirrored RES source.
- Expected: source/test ownership and SHAs, current implementation distinguished from documents. Observed: matching canonical implementation and tests; no build executed for this inventory JR. Reviewer: Builder Lead; next JR-022 acceptance specification.

## JR-022
- **Original acceptance:** Specify Maven and Spring baseline acceptance; phase 02 reproducible engineering, source scope `backend/pom.xml` / `backend/src/test`.
- **DONE.** Actual `backend/pom.xml` SHA `15e207eb6a6aac19d926f2d965ba3daa676b9e57` fixes Java 21, Spring Boot parent 4.1.1, PostgreSQL Testcontainers fixture `postgres:16-alpine`; no July Spring 3.x assumption or forced framework upgrade. CI `actions/setup-java` Temurin 21, real backend tests, and Docker prerequisite measured from code.
- **Acceptance:** in isolated runner with JDK 21, Maven 3.x and Docker available, execute `cd backend && java -version && mvn -version && mvn -B clean verify`; require exit 0, reported Surefire/JUnit total and no errors; test fixture must not touch production DB. If absent runtime, incompatible Java or Docker unavailable, fail explicitly without changes to app/DB. Discrepancy is local tool availability, not app code. Owner: Builder Lead; CI implementation is a reversible test-only engineering change, no protected owner decision.
- **Expected vs observed:** source version properties and 15 test classes match baseline; live clean command not yet run under Session 03, inherited `mvn -B verify` green at Phase 01 SHA. Reviewer: Builder Lead.

## JR-023
- **Original acceptance:** Reproduce Maven and Spring baseline success; phase 02 reproducible engineering, source scope `backend/pom.xml` / `backend/src/test`.
- **IN REVIEW pending new Phase 02 CI.** Changed only `.github/workflows/ci.yml`: before `mvn -B clean verify` emit `java -version`, `mvn -version`; required success includes real clean compile/test lifecycle and test totals in the Actions job. Previous Phase 01 [run 42228277341](https://github.com/Edison3060/PLogIt/actions/runs/42228277341) ran `mvn -B verify` successfully with backend test suite but **did not run the new clean gate**.
- **Actual local command:** `mvn -version` → exit 127 (`mvn: command not found`); Docker absent and source checkout blocked by DNS. This is an environment limitation, not a failed source build. Expected Actions on Phase 02: clean Maven gate exit 0, no production data. Reviewer: Builder Lead; reclassify only after actual branch run.

## JR-024
- **Original acceptance:** Reject Maven and Spring baseline invalid operation; phase 02 reproducible engineering, source scope `backend/pom.xml` / `backend/src/test`.
- **IN REVIEW until CI execution.** Reversible negative control in backend CI step uses Temurin 21 compiler on disposable `mktemp` synthetic `ToolchainProbe.java` with incompatible `javac --release 22`; asserts nonzero, exact unsupported-target diagnostic and no class file. The original Phase 02 task allows simulated incompatible JDK **or** Testcontainers setup failure; Docker failure was not simulated.
- **Executed locally (2026-11-07 UTC):** `javac --release 22 /tmp/PLogItJr024X.java` on OpenJDK 21.0.12.1 → exit 2, `error: release version 22 not supported` (PASS negative), no compiled bytecode; Maven/JUnit/DB negative runtime not available locally. CI step must run on branch before closure. Neither auth nor transactions modified; source review shows no application mutation. Reviewer: Builder Lead.

## JR-025
- **Original acceptance:** Close Maven and Spring baseline verification; phase 02 reproducible engineering, source scope `backend/pom.xml` / `backend/src/test`.
- **IN REVIEW, not VERIFIED NO CHANGE yet.** Compare JR-023 clean success required, JR-024 invalid JDK rejection; inherited Phase 01 success is `mvn -B verify` not clean; local JDK21 compiler negative is genuine but limited. App source and dependencies untouched; test-only CI changes reversible by reverting the workflow commits.
- **Closure gate:** latest Phase 02 head Actions backend job must pass Java/Maven versions + `mvn -B clean verify` and the new negative step; record actual run URL, test totals, commit SHA and security jobs before DONE. Owner sign-off is only session boundary; reviewer: Builder Lead. No production, privacy, authorization or JSON changes.

## JR-026
- **Original acceptance:** Inventory React Vite toolchain; scope `frontend/package.json` and `frontend/package-lock.json` from the original Phase 02 file.
- **DONE / VERIFIED NO APPLICATION CHANGE.** Canonical `frontend/package.json` blob `b9c2ae314cd4a236aa698ff33553b9f92191b754` scripts `dev=vite`, `build=tsc -b && vite build`, `lint=eslint .`, `test=vitest run`. `frontend/package-lock.json` blob `c2d524de941a98e4988fedd7449926fc83c45d8b`, lockfileVersion 3 with 376 package entries; `node_modules/react=18.3.1`, react-dom18.3.1, Vite6.4.3, Vitest4.1.12, TypeScript5.9.3 and @vitejs/plugin-react4.7.0.
- **Test inventory:** `frontend/src/components/ProtectedRoute.test.tsx`, `components/engagement/EngagementPanels.test.tsx`, `lib/api.test.ts`, `lib/theme.test.ts`, `pages/LogDetail.test.tsx`, `test/setup.ts`. Previous source-identical GitHub CI 42228277341 frontend npm ci/lint/build/test success; later new runtime evidence must be scoped to its own SHA.
- **Expected/observed:** exactly implementational source, lock and tests identified; inherited code unchanged from Phase 01, not mirrored historical copy. No npm install executed during inventory. Reviewer Builder Lead.

## JR-027
- **Original acceptance:** Specify React Vite toolchain acceptance; scope `frontend/package.json` and `frontend/package-lock.json` from the original Phase 02 file.
- **DONE.** Expected PLogIt toolchain from actual source: React18.3.1, Vite6.4.3, Vitest4.1.12, TypeScript5.9.3; scripts `npm ci`, `npm run lint`, `npm run build`, `npm test`; canonical `package-lock.json` is lockfileVersion3. `vitest@4.1.12` lock declares Node `^20.0.0 || ^22.0.0 || >=24.0.0`, `vite@6.4.3` declares `^18.0.0 || ^20.0.0 || >=22.0.0`.
- **Decision:** GitHub CI currently uses `node-version: 20`, a floating EOL major. Replace *CI-only* Node selection with exact supported `22.23.4` (Node.js official archive confirms LTS version published through 2026-10-20) and log actual node/npm versions; do not change package lock or runtime app. Version pin is reversible, satisfies repeatable acceptance. Link official Node archive https://nodejs.org/en/download/archive/v22.23.4 (access 2026-11-07). New CI must verify this choice, not assume compatibility.
- **Negative:** isolated invalid lockfile `npm ci` must exit nonzero and not modify checkout; positive path must run all four commands and tests on exact Node. No privacy or JSON changes. Reviewer Builder Lead.

## JR-028
- **Original acceptance:** Reproduce React Vite toolchain success; scope `frontend/package.json` and `frontend/package-lock.json` from the original Phase 02 file.
- **IN REVIEW pending Phase 02 new CI.** Updated CI Node runner `node-version: 22.23.4` and emits `node --version`, `npm --version`; then preserves original real steps `npm ci`, `npm run lint --if-present`, `npm run build`, `npm test`. npm test invokes `vitest run` and avoids watch mode. Inherited Phase 01 [42228277341](https://github.com/Edison3060/PLogIt/actions/runs/42228277341) passed on floating Node20, **not** proof of pinned Node22 run.
- **Local limitation:** Node22.16.0 npm10.9.2 installed but outbound github.com/registry network unavailable and frontend checkout absent, so no actual `npm ci` or Vitest on real source locally. Expect GitHub Actions pass with test totals, test runner env and commit SHA. No unversioned exports or product code changed. Reviewer Builder Lead.

## JR-029
- **Original acceptance:** Reject React Vite toolchain invalid operation; scope `frontend/package.json` and `frontend/package-lock.json` from the original Phase 02 file.
- **IN REVIEW pending fresh CI negative step.** Added CI guard that copies the real `frontend/package.json` and lockfile into a disposable directory, corrupts only that copied lock JSON, runs `npm ci --offline --ignore-scripts --no-audit --no-fund`, requires a nonzero EUSAGE/EJSONPARSE exit and absence of node_modules; `trap` removes scratch. Real project lock and app source are untouched.
- **Executed local synthetic fixture:** disposable `/tmp/plogit-jr029-corrupt.XXXXXX` with valid minimal manifest + malformed lock, `npm ci --offline --ignore-scripts --no-audit --no-fund` exit 1, `npm error code EUSAGE`, no node_modules (negative PASS). First attempted missing-package offline mismatch returned `ENOTCACHED`, which is **not** lock-mismatch proof; intentionally replaced with deterministic malformed-lock fixture and kept this failure in the evidence. This does not test direct dependency semver mismatch; the original step accepts deliberately corrupt fixture/lock. CI confirmation still pending. Reviewer Builder Lead.

## JR-030
- **IN REVIEW** for full runtime acceptance, original JR-030 "Close React Vite toolchain verification". Compared positive JR-028 and invalid fixture JR-029; source `frontend/package.json`, lock and actual 5 Vitest test files unchanged. Phase 01 CI 42228277341 passed on Node20 but was not pinned Node22. The new `npm ci`, lint, build, test and malformed-lock fixture need real final branch CI run at JR-030 SHA.
- **Evidence and decision:** exact Node22.23.4 pin, official archived release as of 2026-10-20; local malformed-lock synthetic npm ci exit 1 EUSAGE and no node_modules. Initial local offline missing-package fixture failed ENOTCACHED (not lock mismatch proof), preserved in JR-029. No app runtime, auth, audit, export, DB, privacy or deployment changes. Rollback by reverting only test workflow edits; security scans remain on.
- **Gate:** review real latest Phase 02 GitHub Actions backend Java21 `mvn -B clean verify`, JDK21 wrong-release denial, frontend pinned Node22 steps, corrupted-lock denial, Semgrep, Trivy, proxy, skipped deploy. Record exact SHA, test totals and failures. Reconcile JR-023–025 and JR-028–030 from IN REVIEW only when tests executed and passed; otherwise do not claim DONE. Reviewer Builder Lead.
- **Boundary:** all JR-021–030 have individual checkpoints and are assessed; no JR-031 task performed. Phase 02 (20-step) acceptance after Session 04, not now. Draft stacked PR base Phase 01 rather than main; Phase 01 release hold remains.

## Final verification reconciliation — 2026-11-07T04:34:47.828Z

**All ten selected JRs JR-021–JR-030 are DONE for their original Phase 02A source/toolchain acceptance.** Statuses in earlier dated evidence sections reflect their then-pending CI; this dated conclusion supersedes those IN REVIEW checkpoints without removing history.

CI [42338792629](https://github.com/Edison3060/PLogIt/actions/runs/42338792629) at executable commit `c3b5b562aaddba47b9d8f426418d6e2f343e5b1f`: **SUCCESS** 2026-11-07T04:33:57Z; Java Temurin 21.0.13, Apache Maven 3.9.15, `mvn -B clean verify` **125/125 tests, 0 failures/errors/skips**, invalid JDK21 `javac --release 22` guard success; Node **v22.23.4**, `npm ci`, lint, `tsc -b && vite build`, Vitest **18/18 tests across 5 files**, malformed-copy lockfile `npm ci` rejection guard success. Semgrep, Trivy filesystem and both runtime images, production proxy **SUCCESS**; `CD - Deploy to Production` **SKIPPED**. No real production test or downstream JudgeIt integration claimed.

### Exact individual JR commits (all pushed)
- JR-021: [`195919067d64`](https://github.com/Edison3060/PLogIt/commit/195919067d64d90c35d0c15090c5d5a3bf6576a1)
- JR-022: [`91395728d35a`](https://github.com/Edison3060/PLogIt/commit/91395728d35a0dc5ac9eb3a6f6c19445f381ddef)
- JR-023: [`86fad5cacdca`](https://github.com/Edison3060/PLogIt/commit/86fad5cacdca913f842ff25d3ec123f5d6a25d1f)
- JR-024: [`08c1b185459e`](https://github.com/Edison3060/PLogIt/commit/08c1b185459e270738c087a1a302df4990b82184)
- JR-025: [`5b3b5472c027`](https://github.com/Edison3060/PLogIt/commit/5b3b5472c027387fa57d6f263edfb0ab23b5955a)
- JR-026: [`2200f582c776`](https://github.com/Edison3060/PLogIt/commit/2200f582c7766bd44183845d1f32904162583b98)
- JR-027: [`3f053e14a55f`](https://github.com/Edison3060/PLogIt/commit/3f053e14a55fd60f3cf88d7df22a35f8736ad448)
- JR-028: [`d0fafb8d198f`](https://github.com/Edison3060/PLogIt/commit/d0fafb8d198f7eb5cb4e84292ac11a46feb37f92)
- JR-029: [`e213edec3b67`](https://github.com/Edison3060/PLogIt/commit/e213edec3b676d1780e9a5f2cac024eab4425062)
- JR-030: [`c3b5b562aadd`](https://github.com/Edison3060/PLogIt/commit/c3b5b562aaddba47b9d8f426418d6e2f343e5b1f)

### Security, source, and handoff limits
- Compare against Phase 01 head `6426609e77b436e2804b9c65c9c3701134c54967`: 10 numbered commits ahead, only `.github/workflows/ci.yml` and `docs/junior-execution/` changed. Clean Maven, pinned frontend Node and disposable failures are CI-only. No production database, login/authorization, engagement log, audit/history, export wire-format or live consumer changes.
- Draft [stacked Phase 02 PR #23](https://github.com/Edison3060/PLogIt/pull/23) is based on unmerged Phase 01 draft [PR #22](https://github.com/Edison3060/PLogIt/pull/22), NOT on `main`. Phase 02B and full 20-JR phase sign-off still pending; no merge or deploy.
- Unverified external evidence: real JudgeIt importer, evidence bytes/hash and tenant identity/privacy decisions remain PROVISIONAL/PENDING under prior `INTEGRATION-ASSUMPTIONS.md` and DEC-2026-11-06-06..09. Production release authorization DEC-2026-11-06-11 remains PENDING.
- **Confidence boundary:** this documented closeout is written in a new docs-only commit after the passed CI at `c3b5b562aaddba47b9d8f426418d6e2f343e5b1f`. The new commit's CI must be checked separately; it has no executable behavior changes. The successful run cannot be falsely relabeled as a test of that later docs commit. Reviewer: Builder Lead.

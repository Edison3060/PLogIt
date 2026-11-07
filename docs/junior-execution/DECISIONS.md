# PLogIt: owner decisions and engineering decision log

The RES `Suite - To Be Scoped.md` and Planning Questions contain **open** decisions. Do not treat a recommendation as owner approval. Use this log to record new questions and approved answers, linked to the exact JR IDs.

## Owner-question template

### DEC-YYYY-NN | PENDING | JR-XXX | Short title
- **Question needing owner approval:**
- **RECOMMENDED ANSWER:** Specific preferred option.
- **Why recommended:** Technical/product evidence; identify verified facts versus inference.
- **Alternatives / trade-offs:**
- **Blocked JR IDs:** Exact dependencies. Continue unaffected JRs.
- **Owner response / timestamp:** Pending until explicit answer.
- **Commit/issue reference:**

## Engineering decisions (within approved scope)

Write an append-only dated entry: what was selected, why, rejected alternatives, tests/impact and commit. Routine engineering decisions may be made by the Lead; product/tenant/privacy/contract/deployment decisions require the owner.

_No execution-session decisions have been recorded._

### DEC-2026-11-06-01 | ENGINEERING CONFIRMED | JR-002 | Canonical PLogIt source identity
- **Date / observed:** 2026-11-06 UTC. Main `Edison3060/PLogIt@852cfbaa6682de8e99895a63c09515ea659e4b94`; original last-reviewed `28aa1561cb59637e954a08caf2a2ed068178a9fb`; archived RES `596f8a1ad0997f2cdacfcc859107f3b642e2e545`. 185/185 application blobs match archive; six subsequent changes only planning/docs.
- **Expected / target / responsible owner:** Canonical standalone main remains the only editable implementation (Edison3060 owner; Builder Lead reviewer); RES nested PLogIt source is reference-only. No runtime request/data wire-format changes. Existing authentication, review state, audit, JSON semantics retained.
- **Approval status:** Already required by repo AGENTS.md and planning/README.md; this entry confirms, not a new owner-level product approval. Any future divergence or migration requires explicit focused approval and tests.
- **Alternatives rejected:** Development directly in nested RES snapshot creates competing code ownership and loses provenance. Code reconstruction from copied snapshot is unnecessary and prohibited. Commit tracked in JR-002 branch history.

### DEC-2026-11-06-02 | ENGINEERING CONFIRMED | JR-007 | Preserve capture-vs-judgment boundary
- **Date / observed:** 2026-11-06 UTC, canonical source `Edison3060/PLogIt@852cfbaa6682de8e99895a63c09515ea659e4b94`. `LogEntry` has activity/review fields, no severity/CVSS/CVE/ticket properties. `EngagementController` delegates POST `/api/engagements/{id}/export` to `ExportService` with leader check.
- **Actual vs expected / target:** PLogIt creates logs and leader-controlled export; JudgeIt creates auditor findings and CVSS; PatchIt owns remediation tickets; GaugeIt owns metrics. Producer JSON remains unversioned, approved-only by default, optionally EXPORTED and attachment filenames only. Existing shape is accepted baseline, not a promised self-contained downstream contract.
- **Responsible owner / approvals:** Product boundary is already locked in PLogIt - Product Spec.md and Suite - Master Spec.md. Edison3060 owns any scope change; Builder Lead only confirms current behavior. **No approval** to add PLogIt scoring, ticket workflows or alter producer JSON.
- **Alternatives rejected:** Putting findings/CVSS/tickets into PLogIt duplicates downstream responsibilities and silently changes its data model. Introducing a versioned envelope during source baseline would break legacy consumers without an approved compatibility strategy. Pending future product changes remain separate owner decisions; none are requested for no-change verification.

### DEC-2026-11-06-03 | PENDING OWNER APPROVAL | Phase 01 gate / JR-191–JR-195, JR-231–JR-235 | Remediate inherited backend image CVE
- **Observed 2026-11-06:** GitHub Actions run https://github.com/Edison3060/PLogIt/actions/runs/42206996982 (`ec8f789a95b7d48f17b478b1cb518f91a7bad126`) passed Maven 125 tests, frontend 18, Semgrep and proxy checks; overall FAILED on Trivy backend runtime-image scan exit 1. Ubuntu 26.04 `apt` and `libapt-pkg7.0` version `3.2.0` flagged HIGH CVE-2026-109518; Ubuntu-fixed `3.2.1ubuntu0.1`.
- **Question requiring owner approval:** Permit a focused **separate security hotfix** or explicitly schedule remediation as the first authorized Phase 01B correction before any phase PR/merge? No source/package/image change was made under Session 01A's source-freeze scope.
- **RECOMMENDED ANSWER:** Authorize a narrowly scoped Docker runtime base refresh (retain an independently verified immutable digest), rebuild both images, re-run full tests plus Trivy image scans, review exact CVE results and avoid production deployment until separate approval. Prefer upstream patched image over disabling security checks.
- **Why:** The scan's pinned Ubuntu 26.04 base contains a fixable high-severity APT vulnerability. Ubuntu security notice https://ubuntu.com/security/notices/USN-8996-1 (2026-11-04, accessed 2026-11-06) confirms corrected version `3.2.1ubuntu0.1`; Ubuntu CVE page https://ubuntu.com/security/CVE-2026-109518 explains threat model. The image change has compatibility/supply-chain implications; use digest pin and CI before acceptance. The scan is genuine failed-test evidence; sources support fix but do not prove a patched image passes.
- **Alternatives / trade-offs:** Defer until planned JR-191–JR-195 image/workflow hardening (retains red CI and security risk); suppress/ignore vulnerability or loosen Trivy threshold (rejected as hiding a known fixable HIGH finding); apply a targeted APT package update during image build (possible after validation, but increases Dockerfile maintenance and mutable package behavior).
- **Affected JR IDs:** No Session 01 source/product scope JR is blocked from source-level acceptance; Phase 01 PR gate in Session 02 is blocked until CI passes; future JR-191–JR-195 and JR-231–JR-235 must reconcile the remediation and evidence. Product role boundary JR-010 is DONE for its own acceptance, not a CI sign-off or merge clearance.
- **Owner response / timestamp:** Pending. No credentials or production operations authorized.
- **Commit / issue reference:** `work/plogit-phase-01`, JR-010 closeout checkpoint; no hotfix PR yet.

### 2026-11-06T11:18:58.582Z | DEC-2026-11-06-03 | OWNER APPROVED HOTFIX IMPLEMENTATION; PRODUCTION RELEASE NOT AUTHORIZED
- **Approval:** Owner follow-up “Do what you recommend...” authorizes the recommended focused CVE correction; it does NOT authorize deployment to production or crossing the explicit Session 01 boundary. The original PENDING entry above is preserved as history.
- **Implementation:** `fix/ubuntu-apt-cve-2026-109518`, commit `5c5657aa0997a53a6926ebd03375f06d776f9bc1`, [PR #20](https://github.com/Edison3060/PLogIt/pull/20). Updated only `backend/Dockerfile`. Retained digest-pinned Temurin JRE 21 and installed Ubuntu 26.04 APT 3.2.1ubuntu0.1 with `Acquire::Retries=0` and fail-closed `dpkg --compare-versions` checks. No unverified digest or scanner bypass.
- **Actual verification:** [CI run 42208728367](https://github.com/Edison3060/PLogIt/actions/runs/42208728367) **SUCCESS**: backend 125 tests, frontend 18 tests, Semgrep, proxy, Trivy filesystem and BOTH image blocking scans, image healthchecks. Trivy CVE no longer blocks rebuilt backend.
- **Remaining owner-reserved release decision:** **Keep PR #20 open/unmerged**. CI `.github/workflows/ci.yml` attempts production deployment on green pushes to `main`; a PR merge may trigger deployment. Approve a safe release/merge path separately before merge. No Session 02 work started; JR-191–JR-195 remain not yet executed under their own numbered session.
- **Sources / date:** Ubuntu https://ubuntu.com/security/notices/USN-8996-1 and https://ubuntu.com/blog/apt-vulnerability-fixes-available (2026-11-04; checked 2026-11-06); actual CI result above. These are research and real test evidence, respectively.

### 2026-11-06 | DEC-2026-11-06-04 | OWNER AUTHORIZED PROVISIONAL EXTERNAL-APP ASSUMPTIONS
- **Owner directive (2026-11-06):** “For anything that is block due to other Applications, just do it assuming what you think and how you think that application works and connect to your application. I will merge the features together later. I don't want anything to be blocked right now.”
- **Interpretation / scope:** Approves autonomous **reversible working assumptions** at PLogIt's external JudgeIt/PatchIt/GaugeIt boundaries, mocked consumers, producer contract fixtures and opt-in local JSON adapter(s). Do not BLOCK an otherwise executable JR solely because the downstream app is unbuilt or the final cross-app contract is unsettled. Execute and test the current app against provisional, documented consumer fixtures. Reconcile mappings on later integration/merge.
- **Non-negotiable safety / boundaries:** This is NOT authorization to invent functioning remote services, access another repository, deploy, send sensitive evidence or credentials to hypothetical endpoints, disable auth/privacy controls, or silently change PLogIt's approved logs/export logic. PLogIt remains activity/evidence capture; JudgeIt owns findings/CVSS; PatchIt remediation; GaugeIt metrics. Legacy leader-only export, APPROVED/optional EXPORTED and filename-only JSON remain unchanged unless a future separately scoped producer contract change is approved. External tenant association, evidence bytes, final schemas, identity mapping and status callbacks are not yet verified; keep them explicitly PROVISIONAL, no falsely green real E2E integration claims.
- **Recommended engineering pattern:** Existing one-way file JSON handoff first, versioned *adapter outside the legacy exporter*, explicit source namespace for provenance, stable unchanged source UUIDs, fail-closed shape validation, synthetic positive/negative fixtures, idempotency notes and no network calls. Treat other downstream handoff proposals as illustrative until their apps actually exist; a future team can replace the adapter and adjust contracts without rewriting PLogIt.
- **Approval limitations:** This is a standing working-assumption decision, not the `SESSION_NUMBER: 02` authorization for JR-011–020 or a production deployment/merge release authorization. Keep Session 01 status CLOSED; do not alter JR rows or advance sessions implicitly. This update only removes *other-app readiness* as a reason to mark upcoming dependent JRs BLOCKED, provided other JR prerequisites and security checks are genuinely satisfied.
- **Tracking and reference:** `docs/integrations/PROVISIONAL-SUITE-CONTRACTS.md`; a separately scoped opt-in bridge proof is developed on `feat/plogit-provisional-handoff`. External apps are NOT modified.

### 2026-11-06 | DEC-2026-11-06-04 | PROVISIONAL ADAPTER VERIFIED; DEVELOPER BRANCHES NOT RELEASED
- **Actual implementation / branches:** `feat/plogit-provisional-handoff` in [PR #21](https://github.com/Edison3060/PLogIt/pull/21) targets `work/plogit-phase-01` (not main). File-only `tools/integrations/plogit_judgeit.py` wraps the unchanged PLogIt JSON under a provisional envelope and preserves UUID/source namespace; no HTTP, no guessed credentials, no live downstream app, no evidence bytes. Minimal current PLogIt code untouched.
- **Verification:** [GitHub Actions run 42212049183](https://github.com/Edison3060/PLogIt/actions/runs/42212049183) at `ce4ab70c449b8e98b3b66b262769a508be8231c9` **COMPLETED SUCCESS**: all six validation jobs passed; bridge 12/12 tests (synthetic source fixture and simulated receiver, including duplicates, replay, tamper rejection and namespace isolation); backend Maven 125/125, frontend 18/18, Semgrep, proxy, Trivy filesystem + both blocking images, builds and healthchecks all pass. CD skipped. This is *not* real JudgeIt import integration; real downstream contract remains provisional and needs separate future E2E tests.
- **Security correction in development, release gate unchanged:** The previously tested exact Dockerfile fix (from [PR #20](https://github.com/Edison3060/PLogIt/pull/20), pinned Temurin 21 and Ubuntu APT 3.2.1ubuntu0.1) was also carried onto `work/plogit-phase-01` at `ff49cf0d359f4a226e08e6a6b9d16cab77fc5029` and onto feature branch, with a no-force reconciliation merge. PR #21 now changes only four integration-related files. Main remains unchanged and PR #20 remains OPEN awaiting separately authorized safe merge/release path.
- **Approval scope:** Owner will merge features together later. PR #21 remains OPEN, not merged. No Session 02 JR steps begun or marked DONE, no production deployment authorized. Future JR work involving other apps may use these provisional assumptions without claiming nonexistent live interoperability.

### DEC-2026-11-06-05 | ENGINEERING CONFIRMED | JR-012 | Source-grounded API/UI inventory
- **Date:** 2026-11-06 UTC. Branch reviewed at `da9ebfa80d7ed9a0632af24284c95f90e49474b1`. **Decision:** Inventory shipped surfaces only: 10 React leaf routes, six controllers with 24 HTTP mapping annotations, current DTOs and mapped regression test owners. July plan features do not become implementation evidence. No product/schema/auth change authorized by this decision.
- **Actual vs expected:** `App.tsx` and six `web/*Controller.java` files agree with `frontend/src/lib/{auth,engagements,logs}.ts` and Phase 01 roadmap. No new endpoints, CVSS/tickets, self-contained attachment claim or live cross-app integration. Some UI routes have no explicit browser E2E test, therefore journey proof remains unverified.
- **Reviewer / alternatives:** Builder Lead, source and CI review; rejected rebuilding routes merely to make old status prose true. Owner required for any new product behavior. Evidence: `SESSION-02-EVIDENCE.md` JR-011–015 and GitHub Actions run 42212527183.

### DEC-2026-11-06-06 | PENDING OWNER PRODUCT DECISION | JR-017 | Export log inclusion for downstream
- **Question:** Should future evidence handoff include states other than APPROVED/default and optional EXPORTED?
- **RECOMMENDED ANSWER:** Keep legacy PLogIt exporter unchanged. Let a separately versioned, tested future consumer define whether broader capture is required; never silently include unapproved entries. Current source `ExportService.java` and `JsonReportStrategy.java`.
- **Alternatives:** Include drafts (privacy/review leakage), silently change selection (break consumers): rejected pending explicit policy.
- **Blocked JRs:** None in this source-inventory session; future producer schema changes and live handoff require owner approval.
- **Owner response:** PENDING as of 2026-11-06. No sign-off inferred.

### DEC-2026-11-06-07 | PENDING OWNER SECURITY/PRIVACY DECISION | JR-017 | Evidence bytes, portability and integrity
- **Question:** What authorized archive/media bytes, redaction, hashes and recipient retention should accompany attachment filenames?
- **RECOMMENDED ANSWER:** Keep filename-only current JSON; design an explicit versioned, permissioned evidence bundle and tests later. Label mock `evidenceCompleteness=attachment-filenames-only-unverified`.
- **Alternatives:** Embed base64 now (privacy/size and compatibility), treat filenames as complete (incorrect): rejected.
- **Blocked JRs:** None for provisional mock; real media handoff/integrity certification pending.
- **Owner response:** PENDING as of 2026-11-06.

### DEC-2026-11-06-08 | PENDING OWNER IDENTITY DECISION | JR-017 | Cross-instance tenancy
- **Question:** How should engagement/tenant identity be proven at future JudgeIt import?
- **RECOMMENDED ANSWER:** Maintain flat engagement membership/leader authorization. In mock only, use operator-assigned stable `sourceNamespace` plus log UUID; do not infer tenant from email or expose a shared endpoint.
- **Alternatives:** Numeric engagement ID alone (collision), inferred email domains (unsafe): rejected.
- **Blocked JRs:** None for opt-in file-only adapter; real authenticated multi-tenant receiver remains unverified.
- **Owner response:** PENDING real cross-app mapping as of 2026-11-06; DEC-04 authorizes provisional mock only.

### DEC-2026-11-06-09 | PENDING OWNER PRIVACY DECISION | JR-017 | Transfer consent, retention and access
- **Question:** Which pentest evidence classifications, export/import permissions, consent, retention and deletion rules should apply across apps?
- **RECOMMENDED ANSWER:** Do not transmit sensitive evidence to an imagined receiver; retain local authorized export only and add explicit consent/retention policy before real cross-app transfer.
- **Alternatives:** Auto-upload, broad sharing, undocumented indefinite retention: rejected.
- **Blocked JRs:** None in Phase 01; real integration/data transfer pending.
- **Owner response:** PENDING as of 2026-11-06.

### DEC-2026-11-06-10 | ENGINEERING REBASELINE, FUTURE OWNER PRIORITY OPEN | JR-017 | Supported stack and migration scope
- **Observed:** Current `backend/pom.xml` Spring Boot 4.1.1 / Java 21, `frontend/package.json` React 18 / Vite 6; PostgreSQL 16. July 3.5.x and an assumed newer UI are not source facts.
- **RECOMMENDED ANSWER:** Freeze the verified working Phase 01 code; address tooling modernization only in its already numbered later phases, with current dependency advisories researched at that point.
- **Alternatives:** Rewrite or upgrade in baseline verification without focused tests: rejected.
- **Blocked JRs:** None. Future modernization priority remains owner-reserved if it changes scope.
- **Owner response:** No new stack change approved on 2026-11-06.

### DEC-2026-11-06-11 | PENDING OWNER RELEASE SAFETY DECISION | JR-019 / JR-020 | Phase 01 merge may trigger production CD
- **Observed (2026-11-06):** `.github/workflows/ci.yml` has push-to-main production `deploy` job after validation, guarded by SSH host-key fingerprint preflight. Phase 01 branch CI run 42212527183 was green and CD SKIPPED on non-main; no proof of prod deployment. Branch includes focused APT CVE fix, separate security PR #20 remains OPEN.
- **Question needing owner approval:** How should the accepted Phase 01 branch merge into main without triggering an unauthorized production deployment?
- **RECOMMENDED ANSWER:** Keep the Phase 01 PR OPEN/DRAFT for review. Establish an independently verified fail-closed manual approval environment/deployment gate (or explicitly authorize a controlled deployment window) before merging to main. Re-run all mandatory CI/security checks at the merge candidate SHA. This is the preferred safe merge path; no security bypass or CI suppression.
- **Alternatives / trade-offs:** Merge now (may deploy without permission: rejected); disable security checks or force merge (prohibited); keep PR open while safe independent future work is planned (allowed, but next phase main base may lag). A deployment workflow change is separately scoped and must be proven safe.
- **Blocked JR IDs:** None of JR-011..020 for documentation/verification. Phase 01 **merge/release gate** is BLOCKED, and beginning Session 03 on an unreconciled main would risk missing prior phase work.
- **Owner response:** PENDING, user explicitly forbids production deployment without separate authorization. No merge performed. Relevant PR to be created after Session 02; do not treat creation as merge approval.

### 2026-11-06T13:43:48.226Z | ENGINEERING RECONCILIATION | Session 02 (no new JR)
- **Decision:** Follow latest owner-approved `main@0fce5181a2265af5b37a8621d0156eb2eace21f8` policy for safe stacked work after an unmerged phase: READY WITH PROVISIONAL INTEGRATION is compatible with pending mock E2E and safe blocked main merge. Preserve DEC-11 deployment approval requirement, leave Phase 01 PR draft, and base next session phase branch on unmerged safe Phase 01 HEAD with explicit stacked ancestry.
- **Alternative rejected:** Block all safe subsequent work solely because real JudgeIt is unfinished or because main merge could deploy. This would contradict latest SESSION-PROTOCOL. Also rejected merging main unsafely or claiming phase PR accepted.
- **Evidence:** GitHub two-parent merge of 4 main docs, no code changes; CI at merge SHA to check. Owner sign-off still required to *start* Session 03; current session stops.

### 2026-11-07T04:28:46.764Z | DEC-2026-11-07-01 | JR-022,23,24 | Test-only Maven baseline hardening
- **Decision:** preserve application POM (Java21/Boot4.1.1); require `mvn -B clean verify`, Java/Maven runtime reporting and a separate isolated unsupported-release rejection in GitHub Actions. No runtime/export code or production settings changes.
- **Basis:** original Phase 02 JR-021..025 and source at 6426609e77b436e2804b9c65c9c3701134c54967; existing Phase 01 CI ran `mvn -B verify` (not clean). Local sandbox has JDK21 but no Maven/Docker; actual CI needed.
- **Alternatives rejected:** upgrading Spring or using static file assertions as a claim of tests passed; bypassing Testcontainers or altering database connections to manufacture success. Reviewer: Builder Lead.

### 2026-11-07T04:29:59.100Z | DEC-2026-11-07-02 | JR-027,028,029 | Exact LTS Node pin and isolated negative lock guard
- **Current facts:** canonical lock resolves React18.3.1, Vite6.4.3, Vitest4.1.12, TypeScript5.9.3; Vitest lock engines include Node22; source CI used floating Node20. Node.js official archive https://nodejs.org/en/download/archive/v22.23.4 (checked 2026-11-07 UTC) lists v22.23.4, updated 2026-10-20; Node20 end-of-maintenance confirmed at https://nodejs.org/en/download/archive/v20.19.5.
- **Decision:** pin GitHub Actions runtime to supported Node22.23.4 without package dependency refresh; add version reporting plus isolated malformed lock failure test. Rationale: repeatable, supported, safe, no application or JSON changes. Pass is contingent on fresh CI evidence.
- **Rejected:** retain unsupported Node20 indefinitely; change package-lock to make a failing install appear green; upgrade React/Vite and auth/export unrelated to this verification. Reviewer Builder Lead.

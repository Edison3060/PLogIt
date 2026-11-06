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

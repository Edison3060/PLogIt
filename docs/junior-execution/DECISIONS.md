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

# The Suite - To Be Scoped

> **2026-11-02 RESEARCH UPDATE.** The first export is now inspected directly: the current PLogIt JSON has no schemaVersion, includes APPROVED logs (optionally prior EXPORTED), stores attachment filenames without image bytes/hashes, and requires a leader-only POST with state changes. Current implementation is Spring Boot 4.1.1 / Java 21, React 18 / Vite 6, PostgreSQL 16. Formal JSON Schema 2020-12, provenance, evidence packaging, tenant-aware idempotency, CVSS v4.0 vectors, roles, and data privacy are still open. See [evidence contract research](./Suite%20-%20Handoff%20Contracts%20and%20Evidence.md). All unbuilt-app design fields below remain **proposals requiring product sign-off**.

> **Status:** Open design work. This file holds the cross-app and per-app items that are deliberately not yet decided. Nothing here is locked. Each item moves out of this file and into the relevant app's Product Spec when it is scoped and signed off.
> **What this is:** The holding pen for the design decisions that the three not-yet-built apps (JudgeIt, PatchIt, GaugeIt) need before they can be built. It complements `Suite - Master Spec.md`, which holds the locked foundational decisions.
> **Last updated:** 2026-07-26.

---

## 1. Why this file exists

`Suite - Master Spec.md` locks the vision: four apps, shared-nothing architecture, one-way JSON handoff, UUID golden thread. It does not lock the implementation contracts. The JSON schemas, the data models, the API contracts, and the per-app tech stack details are all design work that happens when each app is properly scoped, one at a time, with the user signing off.

Rather than scatter "to be scoped" placeholders inside each Product Spec, this file is the single place where open design work lives. Each app's Product Spec references it. When an item is scoped, it moves out of here and into the relevant spec as a locked decision.

---

## 2. The three JSON handoff schemas (cross-app contracts)

The handoff protocol is the heart of the Suite. Each link is a single JSON file with a defined schema that one app owns and the next app consumes. None of the three schemas are designed yet.

### 2.1 Handoff 1: PLogIt to JudgeIt

- **File:** `evidence-pack.json`
- **Producer:** PLogIt (the only built app).
- **Consumer:** JudgeIt.
- **Status:** partly exists. PLogIt's export code already produces JSON, so the producer side is real. The schema needs to be extracted from PLogIt's actual export output and written down as the formal contract JudgeIt consumes.
- **To scope:** read PLogIt's `ExportService` and `JsonReportStrategy` to extract the real shape of the exported JSON. Document it as the schema. Confirm whether JudgeIt needs anything that PLogIt does not currently emit, and if so, decide whether PLogIt adds it or JudgeIt derives it.

### 2.2 Handoff 2: JudgeIt to PatchIt

- **File:** `remediation-tasks.json`
- **Producer:** JudgeIt (not built).
- **Consumer:** PatchIt (not built).
- **Status:** not designed. The master spec section 12 lists the contents at a high level (formal findings with severity, scores, CVE references, affected target assets, SLAs and deadlines). The actual field-by-field schema is open.
- **To scope:** the data model for a finding inside JudgeIt, the SLA rules, and the asset shape PatchIt expects. This schema is designed when JudgeIt is scoped, because JudgeIt is the producer and owns the contract.

### 2.3 Handoff 3: PatchIt to GaugeIt

- **File:** `posture-metrics.json`
- **Producer:** PatchIt (not built).
- **Consumer:** GaugeIt (not built).
- **Status:** not designed. The master spec section 12 lists the contents at a high level (counts of tickets closed, mean-time-to-remediate, assets still vulnerable, SLA breach counts, severity breakdowns over a period). The actual schema is open.
- **To scope:** the aggregation shape GaugeIt needs to render heatmaps and trend charts. This schema is designed when PatchIt is scoped, because PatchIt is the producer.

---

## 3. Per-app data models

Each app owns its own database and its own data model. None of the three are designed yet.

- **JudgeIt:** the finding is the primary entity. Open: finding fields, severity and CVSS representation, the finding-to-log relationship, deduplication across hosts, the SLA model, cross-engagement persistence.
- **PatchIt:** the remediation ticket is the primary entity, tied to a continuously owned asset. Open: asset fields, ticket lifecycle, the SLA clock, the re-test and verification loop, the cycle aggregation that produces the posture metrics.
- **GaugeIt:** the posture snapshot is the primary input. Open: whether GaugeIt persists its own state or is purely a read layer over imported metrics, the compliance framework mapping model, the trend model.

Each data model is scoped when its app is scoped.

---

## 4. Per-app API contracts

Each app is a standalone Spring Boot REST API. The endpoint list for each is open.

- **JudgeIt:** ingestion endpoint for `evidence-pack.json`, finding CRUD, the review and promotion path, the SLA assignment endpoint, the report export endpoint, the `remediation-tasks.json` export endpoint.
- **PatchIt:** ingestion endpoint for `remediation-tasks.json`, ticket CRUD, the Kanban transition endpoint, the verification loop endpoints, the cycle export endpoint for `posture-metrics.json`.
- **GaugeIt:** ingestion endpoint for `posture-metrics.json`, the heatmap and trend read endpoints, the compliance mapping endpoints, the board-ready report endpoint.

Each API contract is scoped when its app is scoped.

---

## 5. The UUID golden thread

The master spec section 13 locks the rule: an app never reassigns an ID it received. It stores it as-is and references it. What is open is the field-level convention.

- Does every entity carry a `sourceLogId` that traces back to the original PLogIt log UUID?
- Does a JudgeIt finding carry both its own UUID and the source log UUID?
- Does a PatchIt ticket carry its own UUID, the finding ID, and the source log UUID?
- Is the UUID the primary key everywhere, or a separate reference field alongside a local primary key?

This is decided when JudgeIt is scoped, because JudgeIt is the first app that has to store a foreign ID and the convention set there propagates downstream.

---

## 6. Per-app tech stack details

The master spec section 17 leaves the per-app stacks open. They could match PLogIt for consistency, or diverge to fit each app's shape.

- **Baseline assumption (not locked):** all three match PLogIt. Spring Boot 3.5.16, Java 21, Spring Data JPA, Hibernate with `ddl-auto: validate`, Flyway, Spring Security with session cookies, PostgreSQL 16, React 18 with Vite 6 and TypeScript and Tailwind, GitHub Actions with Trivy and Semgrep as blocking gates. This keeps the suite consistent and lets one developer move across all four codebases without context switching.
- **Open:** whether any app has a reason to diverge. PatchIt's real-time SLA countdowns and GaugeIt's heavy visualization are the two places a divergence might be justified (for example, a charting library on the GaugeIt frontend). These get revisited at each app's scoping.

The scaffolding for all three assumes the PLogIt stack, so a divergence is a deliberate later change, not a default.

---

## 7. Open questions carried from the master spec

These are the items the master spec section 17 already flagged as undecided. They live here until they are resolved.

1. **Optional API-key sync for suite customers.** The file handoff is the baseline. A future enhancement could let a suite customer paste an API key so one app POSTs the JSON directly to the next app's ingestion endpoint instead of requiring a manual file move. Additive, not a replacement for the file flow.
2. **Webhooks for backward status.** A strictly one-way flow means PatchIt cannot tell JudgeIt when a ticket is fixed. A future webhook could fire a status update backward (PatchIt to JudgeIt: ticket fixed, awaiting re-test). Opt-in, does not change the forward file flow.
3. **Real users versus portfolio builds.** Whether the three apps are built for a real audience (a firm, a team, clients) or as portfolio and learning projects changes how much weight goes on interop polish versus each app standing alone. This is a question for the user.
4. **Naming and trademarks.** JudgeIt, PatchIt, and GaugeIt are working names. Trademark and domain checks have not been done. If one conflicts, that one app is renamed, and PLogIt stays as-is.

---

## 8. How items leave this file

When an item is scoped and signed off, it moves out of this file and into the relevant app's Product Spec as a locked decision. The Process Log records the decision with its reasoning and the alternatives weighed. This file then holds only what is still open.

The goal is for this file to shrink over time. When all three apps are built, it should be empty or near-empty, because every contract will have been designed and every question will have been answered.

---

*End of To Be Scoped. This is the holding pen, not a spec. Each item here is a promise to scope something properly before building it.*

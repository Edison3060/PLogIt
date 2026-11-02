# RES — Junior Execution Roadmaps: Four-Application Builder Entry Point

**Created:** 2026-11-02. **Scope:** execution documentation only. This is the RES equivalent of The Bloxy and Blox & Banner 240-step Junior Execution Roadmaps, adapted to four independent Spring/React/PostgreSQL-style web applications rather than Roblox Studio.

## Start in the right place

| App | Current evidence | Junior guide | Task coverage |
|---|---|---|---|
| **PLogIt** | Implemented standalone source; last-reviewed CI validations passed, latest production deploy unverified/blocked | [PLogIt junior manual](./PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/README.md) | JR-001–JR-240, **verification + approved modernization**, not a rewrite |
| **JudgeIt** | Planning, Caddy/Compose scaffolding; application not built | [JudgeIt junior manual](./JudgeIt%20Workspace/Project%20Info/08-junior-execution-roadmap/README.md) | JR-001–JR-240, greenfield auditor service |
| **PatchIt** | Planning, Caddy/Compose scaffolding; application not built | [PatchIt junior manual](./PatchIt%20Workspace/Project%20Info/08-junior-execution-roadmap/README.md) | JR-001–JR-240, asset/ticket remediation service |
| **GaugeIt** | Planning, Caddy/Compose scaffolding; application not built | [GaugeIt junior manual](./GaugeIt%20Workspace/Project%20Info/08-junior-execution-roadmap/README.md) | JR-001–JR-240, posture and executive-report service |

**Inventory:** 4 manuals × (12 phases × 20 individual tasks) = **960 JR tasks**, in 48 phase files. Each workspace includes a README, an exact proposed/actual implementation file map, reference test recipes, senior/debug guardrails and a progress/evidence tracker: **17 files per app, 68 files total**. The PLogIt file map references existing canonical code; other maps explicitly label future paths as proposed.

## Correct dependency route

```text
PLogIt (implemented canonical code, verify legacy export first)
   └── offline unversioned JSON today; future schema change is an approved contract decision
        ▼
JudgeIt (unbuilt): verified intake → source evidence → reviewed findings/CVSS/SLA
   └── producer-owned remediation-tasks.json, version and fixture locked before consumer build
        ▼
PatchIt (unbuilt): source finding→durable asset→ticket/retest/SLA → metrics
   └── producer-owned posture-metrics.json, window, denominator and source cutoff agreed
        ▼
GaugeIt (unbuilt): immutable snapshots → explainable charts → reviewed board reports
```

The apps are separately deployable with **separate databases**. The baseline is one-way, adjacent-app, manual file export/import. Do **not** implement a shared database, implicit backward updates or live API sync while completing V1. An app may be tested with clearly labeled producer fixtures before its upstream app is released, but live interoperability cannot be marked DONE without actual producer and consumer tests.

## Required read order and authority

1. [2026 research baseline](./Suite%20-%20Research%20Refresh%202026-11-02.md), [suite master spec](./Suite%20-%20Master%20Spec.md), [handoff contract analysis](./Suite%20-%20Handoff%20Contracts%20and%20Evidence.md), [modernization roadmap](./Suite%20-%20Modernization%20Roadmap.md) and [open decisions](./Suite%20-%20To%20Be%20Scoped.md).
2. Each workspace's `Rules to Follow/Master Prompt.md`, `Project Info/HANDOFF.md`, `Product Spec`, `Build Plan` and current source inventory.
3. Its new `08-junior-execution-roadmap/README.md`, current numbered phase, implementation file map, guardrails, recipes and tracker.
4. For PLogIt, consult the **standalone** [Edison3060/PLogIt](https://github.com/Edison3060/PLogIt) main branch before any implementation; the copied source in RES is a frozen research reference.

**Authority order:** verified platform/security constraints and actual source/runtime evidence → owner-approved current suite/app decisions → accepted producer/consumer contract → Junior task implementation sequence → historical exploratory plans. If the task conflicts with a higher source, record the discrepancy and stop that dependent action. Do not silently invent data models, hosting or scoring behavior.

## Suite-wide decision gates before implementation

| Gate | Must be settled / verified | Consumer blocked if absent |
|---|---|---|
| 0. Baseline | Product audience, tenancy, roles, privacy/retention, hosting; current source and scaffolding inventoried | All four apps' privileged features |
| 1. PLogIt export | Exact legacy exporter fixture, APPROVED/EXPORTED selection, leader-only POST side effects, attachment filenames-only caveat | JudgeIt import semantics |
| 2. Finding/task handoff | Evidence-to-finding cardinality, per-asset identity, CVSS version/vector/priority/SLA dueAt and remediation-tasks.json schema | PatchIt ticket materialization |
| 3. Posture data handoff | Snapshot schemaVersion/ID, UTC interval, source cutoff, denominators, MTTR policy, exclusions, overlapping imports | GaugeIt aggregation and trends |
| 4. Security/recovery | Auth/tenant matrix, CSRF, file limits, audit, migrations, backups, restoring and accessible UI | Production release |
| 5. Release truth | Tests, static scans, signed revision, deployment image/proxy/TLS, startup health, rollback | Marking any app live verified |

Do not treat the documented 2026-11-02 PLogIt validation jobs as a successful latest production deploy: the SSH trusted-host fingerprint preflight failed. Resolve identity through an authenticated host console without weakening verification.

## Execution, status and evidence

The task system is deliberately repeatable. **Every JR** states where to work, the exact requested behavior, observable pass evidence and a concrete mistake. One phase has 20 tasks and a gate; each app has 12. Tasks are ordered, but a junior can record a genuine dependency BLOCKED and work on a safe independent action without claiming the gate passed.

Use the app's tracker states: **NOT STARTED → IN PROGRESS → IN REVIEW → DONE**, with **BLOCKED** on unresolved decisions or failing tests. For PLogIt, already implemented functionality still needs current regression evidence and can be recorded as **verified, no change required**; do not rebuild it. For JudgeIt/PatchIt/GaugeIt, generated source or a drafted schema is not a tested app.

Each completed task requires Git SHA, changed file or inspected owner, product decision reference, exact test command and synthetic fixture, positive and negative cases, observed API/DB/UI results, reviewer, migration/rollback and next task. For V1 web services include backend Maven/PostgreSQL tests, frontend lint/build/tests, actual HTTP authorization/CSRF tests, Semgrep/Trivy, accessible browser smoke, and validated JSON Schema 2020-12 producer-consumer fixtures. Match each gate to the task; a UI screenshot alone cannot prove secure persistence.

**Safe change discipline:** This document and the four roadmaps add planning files only. Existing code, infrastructure and historical process logs were not modified. Future source changes, especially PLogIt export format or production deployment, require their own approved implementation PR.

## Current release claim

**Documented:** four 240-step manuals and support files. **Not completed here:** app implementation, contract approvals, production deployments, CI execution or end-to-end live integration. Actual task progress starts at NOT STARTED in all new trackers.

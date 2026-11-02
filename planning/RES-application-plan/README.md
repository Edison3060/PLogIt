# RES — four-app cybersecurity suite

**Updated 2026-11-02.** This is the **planning and reference repository**, not a functional integrated application. The standalone [PLogIt repository](https://github.com/Edison3060/PLogIt) is the authoritative active codebase; an unmodified source snapshot of commit 28aa156 is preserved under the PLogIt workspace here. **No PLogIt code changes were made in this research refresh.**

## What the suite is
| Stage | App | Audience | Job | Implementation state |
|---|---|---|---|---|
| 1 | [PLogIt](./PlogIt%20Workspace/PLogIt/) | Pentester | Capture evidence, activity and review history | Code exists and CI validated; latest production deploy blocked |
| 2 | [JudgeIt](./JudgeIt%20Workspace/) | Auditor | Turn evidence into defensible findings, scores, reports and SLAs | Planning + infrastructure scaffolding only |
| 3 | [PatchIt](./PatchIt%20Workspace/) | Blue team | Track asset-based remediation, retests and SLA clocks | Planning + infrastructure scaffolding only |
| 4 | [GaugeIt](./GaugeIt%20Workspace/) | Leadership | Aggregate trends, risk posture, and reviewed control mappings | Planning + infrastructure scaffolding only |

**Architecture:** standalone app and database per stage; offline, one-way JSON handoffs (PLogIt → JudgeIt → PatchIt → GaugeIt). The principle is capture, judge, fix, understand. The three handoff schemas are not complete; the first current export contains attachment filenames but not evidence bytes. See the contract report below.

## Start here
1. [2026-11-02 research findings](./Suite%20-%20Research%20Refresh%202026-11-02.md) — verified facts, external primary sources and discrepancies.
2. [Modernization roadmap](./Suite%20-%20Modernization%20Roadmap.md) — sequenced gates, proposals and criteria.
3. [Handoff/evidence contract analysis](./Suite%20-%20Handoff%20Contracts%20and%20Evidence.md) — actual PLogIt producer fields vs proposed interoperable formats.
4. [Original suite master specification](./Suite%20-%20Master%20Spec.md) — product boundaries and historical decisions; dated 2026 notes take precedence for status.
5. [Open scoping decisions](./Suite%20-%20To%20Be%20Scoped.md) — unanswered design questions.
6. [PLogIt research-only update plan](./PlogIt%20Workspace/PLogIt/docs/PLOGIT-RESEARCH-UPDATE-PLAN-2026-11.md) — potential future code PRs, not implemented.

## Repository organization
Each workspace has a product spec, slice build plan, process log, knowledge base, planning questions, and a dated handoff. The three unbuilt projects include Docker/proxy scaffold files but **no application backend/frontend**. Original process logs and editorial rules are retained rather than erased.

## Research boundaries and deployment
The reviewed PLogIt source (Spring Boot 4.1.1 / Java 21, React 18 / Vite 6, PostgreSQL 16) passed five CI validation jobs in GitHub run [41421194108](https://github.com/Edison3060/PLogIt/actions/runs/41421194108), while deployment failed because the required verified SSH host fingerprint is not available to the workflow. This does not prove the current public instance is healthy or that the newest commit is live. Do not bypass SSH host-key verification.

**As-of date:** 2026-11-02. Research claims link to official publisher sources in the research document. Specs/roadmaps for future apps are proposals until scoped, approved, built and tested. No software was built or deployed in this planning-only update.


## Junior execution roadmaps (JR-001–JR-240 per application)

The [RES Junior Execution Roadmaps](./Suite%20-%20Junior%20Execution%20Roadmaps.md) contain **960 individually numbered tasks** (240 per app, 12 phases each), each with an exact work location, action, pass/evidence check and mistake to avoid. They are implementation **instructions**, not a claim that the four applications have been built.

- [PLogIt junior verification and modernization manual](./PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/README.md) — preserve the working canonical app; validate, characterize legacy JSON and propose approved updates.
- [JudgeIt junior build manual](./JudgeIt%20Workspace/Project%20Info/08-junior-execution-roadmap/README.md) — build auditor intake, immutable source evidence, findings, CVSS/SLA, reports and remediation export.
- [PatchIt junior build manual](./PatchIt%20Workspace/Project%20Info/08-junior-execution-roadmap/README.md) — build asset ownership, ticket workflows, independent retesting, SLA clocks and posture export.
- [GaugeIt junior build manual](./GaugeIt%20Workspace/Project%20Info/08-junior-execution-roadmap/README.md) — build versioned posture snapshot intake, explainable analytics, reviewed mappings and board reporting.

**Execution status:** no Junior Roadmap task has been executed by this documentation change. The three new applications remain unbuilt; PLogIt implementation has not been altered. Read the signed product specifications and [unresolved scoping decisions](./Suite%20-%20To%20Be%20Scoped.md) before beginning a task.

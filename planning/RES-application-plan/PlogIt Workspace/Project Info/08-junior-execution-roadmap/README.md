# PLogIt — 240-Step Junior Verification and Modernization Roadmap

**START HERE.** This is a numbered execution manual for the **existing** PLogIt application, not a ground-up rewrite. It adapts The Bloxy and Blox & Banner's task format: exact location, precise action, pass evidence, mistake to avoid and phase gate. Four feature tracks per phase use five specific tasks each, for JR-001 through JR-240.

**Canonical source:** [Edison3060/PLogIt](https://github.com/Edison3060/PLogIt), reviewed at `28aa1561cb59637e954a08caf2a2ed068178a9fb`. The copy under `PlogIt Workspace/PLogIt` is a historical reference snapshot, not the place to develop a second fork. Before touching canonical code, refresh main, obtain approval for actual changes and open a focused PR. This documentation update does **not** authorize production code or infrastructure changes.

**Required reading order:** [suite research](../../../Suite%20-%20Research%20Refresh%202026-11-02.md) → [suite master spec](../../../Suite%20-%20Master%20Spec.md) → [JSON handoff analysis](../../../Suite%20-%20Handoff%20Contracts%20and%20Evidence.md) → [PLogIt Product Spec](../PLogIt%20-%20Product%20Spec.md) and [historical Build Plan](../PLogIt%20-%20Build%20Plan.md) → [current proposed backlog](../../PLogIt/docs/PLOGIT-RESEARCH-UPDATE-PLAN-2026-11.md) → task order below. Actual tested code supersedes stale historical version claims; approved future requirements supersede proposals.

## The 12 phases

| Phase | Tasks | Guide | Exit condition |
|---|---|---|---|
| 01 | JR-001–JR-020 | [Source baseline and controlled scope](01-SOURCE-BASELINE-AND-CONTROLLED-SCOPE.md) | Freeze code and approved requirements before suggesting changes |
| 02 | JR-021–JR-040 | [Reproducible local engineering](02-REPRODUCIBLE-LOCAL-ENGINEERING.md) | Establish a repeatable local test baseline before updates |
| 03 | JR-041–JR-060 | [Identity and tenant authorization](03-IDENTITY-AND-TENANT-AUTHORIZATION.md) | Protect every operation through server checks and demonstrated denial tests |
| 04 | JR-061–JR-080 | [Engagement and team workflow](04-ENGAGEMENT-AND-TEAM-WORKFLOW.md) | Keep already-working engagement features intact with regression checks |
| 05 | JR-081–JR-100 | [Logs, review states and audit](05-LOGS-REVIEW-STATES-AND-AUDIT.md) | Preserve the complete observed capture and approval lifecycle |
| 06 | JR-101–JR-120 | [PLogIt → JudgeIt export contract](06-PLOGIT-JUDGEIT-EXPORT-CONTRACT.md) | Characterize actual current exporter before proposing a version upgrade |
| 07 | JR-121–JR-140 | [Media, Markdown and evidence privacy](07-MEDIA-MARKDOWN-AND-EVIDENCE-PRIVACY.md) | Review unsafe input handling and evidence lifecycle without leaking pentest material |
| 08 | JR-141–JR-160 | [Frontend journeys and accessibility](08-FRONTEND-JOURNEYS-AND-ACCESSIBILITY.md) | Verify actual web usability rather than only green unit tests |
| 09 | JR-161–JR-180 | [Database integrity and recoverability](09-DATABASE-INTEGRITY-AND-RECOVERABILITY.md) | Keep state and evidence durable with tested restore behavior |
| 10 | JR-181–JR-200 | [Dependency refresh and supply chain](10-DEPENDENCY-REFRESH-AND-SUPPLY-CHAIN.md) | Upgrade cautiously in isolated PRs with full regression |
| 11 | JR-201–JR-220 | [Deployment readiness and verified release](11-DEPLOYMENT-READINESS-AND-VERIFIED-RELEASE.md) | Address blocked CD safely, with real host identity and observable behavior |
| 12 | JR-221–JR-240 | [Suite handoff regression and release](12-SUITE-HANDOFF-REGRESSION-AND-RELEASE.md) | Prove a real producer-to-consumer fixture, not just aligned prose |

## How a junior should execute a task

1. Read the task, actual canonical source and tests. Identify existing implementation versus proposed behavior. **No task is automatically DONE** just because code exists.
2. Reproduce the expected behavior in an isolated environment; collect an exact source SHA, command, API result and a negative/race test.
3. If a gap remains, create a dated decision and request approval before any code/contract change; make a narrow PR in canonical PLogIt, not in RES. If verified, record **no change required**.
4. Record evidence and reviewer in [PROGRESS-TRACKER.md](PROGRESS-TRACKER.md). If access, product decision, or real consumer is missing, mark BLOCKED with a safe next action.
5. Re-run related prior-phase tests at every gate. Complete release only with real deployment evidence, not a CI-green badge.

Supporting documents: [ownership map](IMPLEMENTATION-FILE-MAP.md), [reference validation recipes](REFERENCE-RECIPES.md), [debug/engineering guardrails](SENIOR-GUARDRAILS-AND-DEBUG-DECISIONS.md) and [tracker](PROGRESS-TRACKER.md).

**Known baseline limits:** Current PLogIt JSON export is unversioned, exports APPROVED logs and optionally previously EXPORTED logs, includes `attachmentFilenames` without media bytes or hashes, and is a state-changing leader-only POST. Latest reviewed CI validation passed but production deploy was blocked on SSH host fingerprint. Do not claim the four-app pipeline exists until compatible importer and real fixtures pass.

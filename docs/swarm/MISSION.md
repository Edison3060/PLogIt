# Swarm: continuous 240-JR application build mission

## Mission and baseline

The project folder currently open in the coding harness is the working application source. **Read files directly from that folder.** Do not assume the project exists only as a GitHub web repository or require the user to provide an earlier chat or session number.

This is a **new, independent Swarm experiment starting at zero roadmap progress:** work through original **JR-001, JR-002, ... JR-240**, covering **all twelve original roadmap phases of 20 tasks each**. There is no JR-000. All 240 rows in the Swarm baseline STEP-REGISTER begin `NOT STARTED`. Disregard the legacy solo-development status, any saved prior Session 08 pointer, and the separate GitHub branch `control/session-state`.

**One continuous mission:** Do not pause or end execution after JR-010, JR-020, any phase boundary, or any former ten-JR session boundary. Continue through JR-240 without needing a fresh user prompt between phases. Conduct phase tests/reviews/checkpoints along the way, but a phase boundary is not a stop condition. If interrupted by technical or resource limits, persist the exact next JR and evidence so rerunning the same prompt resumes correctly; never pretend unfinished work was completed.

The Swarm feature is responsible for its own internal collaboration and execution strategy. This mission intentionally **does not prescribe worker counts, agent roles, team topology, commands or interaction patterns**. The goal is an implemented and verified application and an honest per-task execution record.

## Authoritative project sources

1. The original `planning/RES-application-plan/` suite and application documentation, including the application's **12 source phase files** under its `Project Info/08-junior-execution-roadmap/`. Read the source text of every JR, including its dependencies, acceptance criteria and failure/security scenarios. The existing 24-session index is only a reference to JR numbers, not an execution schedule.
2. The app's mirrored **Master Prompt**, `Rules to Follow`, Product Spec, Build Plan and agreed interface documents. Respect verified facts and security requirements. The actual working code and passing tests are the implementation baseline; plans are not proof that features exist.
3. `docs/swarm/PROGRESS.md` is the independent Swarm mission cursor; `docs/junior-execution/STEP-REGISTER.md` is the 240-row task status register; `docs/junior-execution/STEP-LOG.md`, `SESSION-HANDOFF.md`, `DECISIONS.md`, and `INTEGRATION-ASSUMPTIONS.md` are the permanent detailed evidence. `docs/swarm/SWARM-LOG.md` records continuous-run progress and recovery.

The original ten-JR solo-session prompts, mandatory stop instructions and legacy control branch **do not govern this Swarm mission**. The source roadmap acceptance criteria, safety rules and product boundaries **still do**.

## Build requirements across all 240 JRs

- Work in numeric JR order for dependencies. Independent tasks may be developed concurrently where technically sound, but no JR may be omitted, marked DONE by mere existence of a file, or counted verified from an unexecuted example.
- Follow application-specific responsibilities in `docs/swarm/START-PROMPT.md`. Each app is independently deployed, has its **own database**, and exchanges data via **one-way offline JSON**: PLogIt → JudgeIt → PatchIt → GaugeIt. Do not modify other app folders/repos during this mission.
- Choose and implement sound routine engineering decisions autonomously. Preserve auth, tenancy, audit provenance, privacy, integrity and other original safeguards. For owner-controlled matters that cannot be safely finalized (e.g. production deployment, destructive live data, retention/privacy policy or incompatible frozen contracts), use safe clearly labeled provisional implementation where feasible, document the recommended final choice and pending approval, then continue independent work. Never use an assumption to silently weaken security.
- If another application is not ready, implement a replaceable, versioned provisional JSON schema/adapter, input validation and synthetic fixtures as applicable. Record precise assumptions and future reconciliation in `INTEGRATION-ASSUMPTIONS.md`. A synthetic passing fixture proves local behavior only, not real producer/consumer compatibility. Real cross-app acceptance remains `IN REVIEW` until actually checked.
- Use web search **selectively** when a changeable fact or consequential technique needs checking: official framework/API docs, currently supported versions, security advisories and standards. Prefer recent authoritative sources and compatible secure practices rather than automatic upgrades to the newest. Record source URLs/dates and relevant decisions; web reading is not executed test evidence. Do not share secrets or private customer data through public search.
- Implement actual backend, frontend, DB schema/migrations, tests, CI checks and documentation required by the original JRs, not only synthetic domain demos. Test success and failure paths, authorization/tenant isolation, malformed input, security boundaries, consistency, regression and recoverability where applicable. Record unrun/failed tests truthfully.

## Mandatory permanent checkpoint for EACH JR

For every original JR-001–JR-240:
1. Read its exact source task and acceptance criteria, inspect real code, and set its register row to `IN PROGRESS` when started.
2. Implement or genuinely verify the requirement. Run actual available tests, inspect their output and correct problems.
3. Update the **exact JR row** in `docs/junior-execution/STEP-REGISTER.md` to `DONE`, `IN REVIEW` or `BLOCKED` based on real evidence. For existing satisfied PLogIt requirements use `VERIFIED NO CHANGE` as an evidence note, not a shortcut to DONE.
4. Append a dated **JR-specific** entry to `STEP-LOG.md`: what changed, why, files, actual test commands/results, expected vs observed, failures/unavailable tests, security/contract checks, decisions and evidence references. Update the latest handoff, assumptions/decisions, and the chronological `SWARM-LOG.md`.
5. Update `docs/swarm/PROGRESS.md` with the latest safely assessed JR, next unassessed JR, outstanding review obligations, and actual branch/commit evidence. Commit a JR-numbered checkpoint and **push to `Swarm` when GitHub access is configured**. Do not claim GitHub pushes occurred if the downloaded local folder has no Git remote/credentials. Keep working locally and record the limitation if remote writing is unavailable.
6. Continue immediately to the next eligible JR instead of asking for a new session. Phase-wide tests and review are required but never imply a stop or an automatic merge.

**Completion standard:** all 240 JR rows have been individually addressed, and the best achievable implementation and real tests have been completed. Distinguish genuinely DONE from `IN REVIEW` pending external approvals, authenticity/producer checks, or unexecuted live tests. Never claim all 240 fully accepted merely because all 240 have received checkpoints.

## GitHub branch and environment safety

**All GitHub code and progress writes for this experiment must target the exact, case-sensitive branch `Swarm` in this application's repository.** Do not commit/push to, merge into, rebase over, or open a PR targeting `main`. Do not modify the original `work/*` phase branches or `control/session-state`. **No production deployment is authorized.** Be especially careful with PLogIt's main-branch deployment workflow.

The local project folder might be a ZIP without Git metadata. If so, build and test from the local files, but do not claim pushed commits unless the folder is safely connected to the correct GitHub repository and `Swarm` branch. Do not create a conflicting or unrelated main branch to compensate. Preserve the downloaded source and all progress artifacts.

## End only when the 240-JR mission is finished

Continue through JR-240 without a planned intermediate stop. At the end report the total DONE / IN REVIEW / BLOCKED counts, verified execution and CI results, architecture and JSON contracts, outstanding reconciliation and owner decisions, GitHub Swarm branch/commits if actually pushed, and production/readiness caveats. Do not merge into main or deploy. If external runtime limits interrupt execution, the saved `PROGRESS.md` and per-JR records must permit the same Swarm to resume from the last safe checkpoint on the next invocation of the **same** prompt.

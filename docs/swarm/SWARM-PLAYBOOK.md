# RES Antigravity Swarm playbook

This playbook is mirrored in each application's `Swarm` GitHub branch. It governs **the experimental Swarm branch only**, not `main`, existing `work/*` phase branches, or the original `control/session-state` branch.

## Operating boundaries

- The repository named in `docs/swarm/START-PROMPT.md` is the **only** repository that this swarm may modify or push to.
- **Never push, commit, merge, or open a merge-target PR to `main`**, any `work/*` branch, or `control/session-state`. All accepted coordinator-integrated changes and progress logs go to the exact case-sensitive GitHub branch `Swarm`.
- Do not modify sibling app repositories. Reading public/accessible sibling docs for interface evidence is okay. Keep the four apps' databases independent and their JSON handoffs one-way and offline.
- Production deployment, destructive data changes, insecure bypasses, secrets leakage, and changes to required security/protection settings are not authorized. Inspect CI workflow side effects before any push; PLogIt must not trigger its main-only production CD.
- This is a comparative development experiment. Nothing completed here silently changes the original session controller or signifies acceptance of the original phase PRs.

## Startup and the independent Swarm controller

1. Start Antigravity with **only this application's GitHub repository**, checked out on `Swarm` (case-sensitive). If local copies diverge from remote `Swarm`, fetch and reconcile safely; never reset away unpublished work.
2. In the application or CLI, invoke native `/swarm` and supply the repository-specific START-PROMPT. The main conversation is the coordinator; do not treat the phrase "use swarm" as a replacement for the actual command.
3. Read `docs/swarm/SWARM-CONTROL.md`, this playbook, the original `docs/junior-execution/SESSION-INDEX.md`, the mapped original phase roadmap file, the app's mirrored Master Prompt/Rules to Follow and specs, actual source/tests, and the Swarm branch's `STEP-REGISTER.md`, `STEP-LOG.md`, `SESSION-HANDOFF.md`, `DECISIONS.md` and `INTEGRATION-ASSUMPTIONS.md`.
4. Inspect branch HEAD/CI. If `STATE=READY`, set `STATE=IN_PROGRESS` with a meaningful run identifier in SWARM-CONTROL, then commit/push to `Swarm`. If already IN_PROGRESS, investigate whether another coordinator is active before resuming; never start a duplicate same-repo swarm. If COMPLETE, report and stop.
5. The controller supplies `NEXT_SESSION`. Map it to exactly ten JR IDs in the original SESSION-INDEX. **Do not ask the user to type or edit the session number.** For this initial experiment all four are at Session 08, JR-071–JR-080. Never extend into the next session in the same swarm.
6. Read the latest app-specific permanent records from `Swarm`, not stale main. Preserve already implemented or IN REVIEW work; no fake DONE.

## Swarm team design (prefer a small trial)

- Have **one coordinator**, **two named implementation workers** assigned the session's two five-JR functional groups (or smaller disjoint work packets if dependencies require it), and **one independent verifier**. Allow the platform's monitor to watch for stalls. Quota rises with the number of workers, so start small.
- The coordinator posts each worker's brief and acceptance definition to the shared swarm board. Ask workers to post progress, test commands, diffs, discoveries and blocks **to the board**. Communicate directly with workers through the board as needed.
- Workers get **isolated Git working copies/worktrees** if supported. Allocate exclusive file/package ownership. If files, schema, migration order or shared interfaces overlap, coordinate them first; sequence dependent changes rather than allowing simultaneous edits to the same file. Do not conflate parallel execution with independent acceptance.
- Each worker may develop/test in its isolated worktree and deliver reviewed commits/diffs to the coordinator. **Only the coordinator integrates and pushes to `Swarm`.** Workers must not push to `main`, the legacy phase branches or the shared controller branch; workers must not concurrently edit the central STEP-REGISTER, STEP-LOG, SESSION-HANDOFF or SWARM-CONTROL.
- The verifier independently reads the integrated diffs, checks security and negative cases, runs whatever real tests are available, and posts exact pass/fail command evidence. The verifier requests fixes from the responsible worker; the coordinator adjudicates. If source remains unverified against real upstream data, retain IN REVIEW.
- The coordinator focuses on decomposition, integration, verification review, GitHub history, and the final handoff. Do not lose a worker's unlanded changes before closing/removing it.

## Autonomous choices and research

Choose and implement defensible routine reversible technical decisions without repeatedly asking the owner. Only require approval for inherently risky, irreversible production actions, security/privacy weakening, or exposure of protected data; state the recommended safe alternative and continue independent work.

When another app is unfinished, use best-supported **provisional**, versioned one-way offline JSON interfaces with replaceable adapters, validators and synthetic fixtures, and record assumptions in INTEGRATION-ASSUMPTIONS. No synthetic fixture is proof of authentic producer/consumer compatibility.

Both coordinator and workers may use targeted web search when a changing external fact or significant implementation choice needs verification (official APIs, support versions, security advisories, standards); not before every small edit. Prefer recent primary sources, consider compatibility and security over mere newness, record material source URL/date and why the choice was made. No secrets or customer data in public search. Web reading is not an executed test.

## Mandatory JR-level integration, tests and history

For each of the selected ten JRs, in dependency order:
1. Read the original requirement, prerequisites, acceptance criteria and existing code. Assign task to a worker only if that worker has exclusive ownership and a clear review interface.
2. Implement/verify code and tests in isolated worktrees, integrate reviewed work onto `Swarm`, and have the verifier run actual positive/negative/auth/contract/regression checks as applicable.
3. Update the exact `docs/junior-execution/STEP-REGISTER.md` row, append a dated per-JR entry to `STEP-LOG.md`, update `SESSION-HANDOFF.md` and relevant integration assumptions/decisions. Mark DONE only when the original applicable evidence exists; use IN REVIEW where genuine approvals, real producer evidence, or security gates are missing.
4. In `docs/swarm/SWARM-LOG.md`, record the coordinator/worker/verifier responsibilities, branch/worktree references, landed commit, test results, conflicts and next JR. Keep this as an append-only experiment log.
5. Commit a **JR-numbered checkpoint on `Swarm` and push it to GitHub**. Workers' internal commits are not a substitute. Check current pushed SHA before stating success.
6. Do not modify shared tracking files concurrently. The coordinator owns them and performs each checkpoint after worker/verifier reports.

**Do not merge `Swarm` to `main`, create a PR to main, publish releases or deploy.** It remains a standalone comparative development branch. CI may run on branch pushes, but inspect actual CI outcomes and do not interpret skipped jobs as tested behavior.

## Auto-progress and final result

When all ten assigned JRs are assessed and their evidence/commits are pushed, the coordinator assigns READY or READY WITH PROVISIONAL INTEGRATION if the next ten-JR increment is safe. Record a factual NOT READY only for a specific unsolved local safety/prerequisite issue or unpersisted work. In `docs/swarm/SWARM-CONTROL.md`, advance NEXT_SESSION by exactly one (after Session 24 set COMPLETE); clear the run claim, update LAST_ASSESSED_SESSION/range/last SHA and the next range, then commit/push to `Swarm`. **Never change the legacy controller on `control/session-state`.**

Finish with a concise per-JR status, workers and verifier summaries, real tests, GitHub `Swarm` commits, remaining provisional debt, verdict, and the next number saved in SWARM-CONTROL. Provide the Master Prompt sign-off checklist. **STOP; the user starts the next Swarm session separately.**

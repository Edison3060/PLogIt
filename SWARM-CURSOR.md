# PLogIt — Antigravity Swarm session cursor (Swarm branch only)

```text
INTEGRATION_BRANCH: Swarm
CURRENT_SESSION: 08
ASSIGNED_JR_RANGE: JR-071–JR-080
SESSION_STATUS: NEEDS_DEPENDENCY_AUDIT
TOTAL_SESSIONS: 24
TASKS_PER_SESSION: 10
LAST_CURSOR_RECONCILIATION: 2026-11-13
LAST_ACCEPTED_SWARM_SESSION: NONE
LAST_SOURCE_ACCEPTANCE: Existing JR-001–070 records predate /swarm; inspect canonical task register
NEXT_ACTION: Audit Swarm branch state, sessions 01–07 and existing unmerged PRs before session 08 edits
```

**This file lives on the existing `Swarm` branch and is an advisory automated session selector for the Antigravity experiment. It does not modify the original 240-JR roadmap or erase completed code.** The canonical task/evidence authority is `docs/junior-execution/STEP-REGISTER.md` plus actual Git source/tests. If a cursor assumption conflicts with either, reconcile with a dated decision before making edits. Do not infer all earlier IN REVIEW tasks are safe or fully implemented.

## Why session 08 is the suggested starting point

All ten JR tasks in session 08 (JR-071–JR-080) were `NOT STARTED` when this Swarm branch was inspected on 2026-11-13. Sessions 01–07 each have recorded work, including `DONE` and `IN REVIEW` rows. This **does not certify** that sessions 01–07 have passed every integration/release gate; review dependencies and current tests before beginning JR-071.

## Cold start for every new /swarm

1. Checkout the exact `Swarm` branch of this repository. Confirm with `git status` and `git branch --show-current`; never work from `main`.
2. Read [SWARM-PROMPT.md](SWARM-PROMPT.md), [SWARM-OPERATIONS.md](SWARM-OPERATIONS.md), this cursor, original `docs/junior-execution/SESSION-INDEX.md` and `STEP-REGISTER.md`, `STEP-LOG.md`, `SESSION-HANDOFF.md` and real source/CI.
3. Use `CURRENT_SESSION` to derive JR range `(session-1)*10+1..session*10`. Verify assigned range matches original session index. If Phase/PR history or critical earlier IN REVIEW work conflicts, document and repair the scheduling state cautiously.
4. Run **one** session's ten JR tasks through coordinator, independent implementation workers, adversarial verifier and one-owner integration. Create isolated worktrees if multiple agents edit at once, never on `main`.
5. Record real per-JR evidence in the original register/log/handoff, not a second 240-row register.

## Update cursor as the final commit on Swarm

- All ten original JR tasks need accurate reviewed outcomes. Feasible implementation, test and security dependencies must be accounted for, with remaining **external-only** verification explicitly logged.
- First commit working code, verified tests, updated canonical JR register, append-only log, decisions/integration assumptions and a self-contained handoff **to Swarm only**.
- Re-fetch the current branch HEAD/cursor blob SHA, verify ten statuses and review unresolved IN REVIEW blockers. **Do not advance on an unreviewed source bug or critical unresolved dependency.**
- If ready, update `CURRENT_SESSION` to exactly `09`, derive JR-081–JR-090, change `SESSION_STATUS` to `NEEDS_DEPENDENCY_AUDIT`, and record the reviewed source SHA, tests and verifier outcome. If not ready, stay on `08`, mark `ACTIVE` or `BLOCKED` and write exact remaining work.
- In all later runs, advance **at most one** session (01–24). Do not invent Session 25. At source completion change status to `SOURCE_SESSIONS_COMPLETE` without declaring cross-app, security, deployment or user acceptance.
- Push/verify this cursor file **last**, compare the old blob SHA before replacing it, and stop. If another run updated it, never force-push or overwrite; re-audit latest state and record the conflict.

**This cursor is an experiment-specific scheduling overlay.** Earlier one-agent session prompts with manually provided `SESSION_NUMBER` remain historical; for `/swarm` on the `Swarm` branch, this cursor selects the active ten-JR batch. No commits, merges or PRs to `main` are authorized.

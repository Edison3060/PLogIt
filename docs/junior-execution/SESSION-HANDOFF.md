# PLogIt: self-contained latest session handoff

**Current status:** Not started. No JR execution is evidenced by these planning files. This is a handoff template that must be rewritten and pushed after **each JR checkpoint**, not only at the end of a session.

| Field | Current value |
|---|---|
| Current session | Not started |
| Original 20-task phase | Not started |
| Allowed JR range | None until SESSION_NUMBER is specified |
| Last JR attempted / status | None |
| Current branch and last pushed SHA | main / baseline; inspect GitHub for actual SHA |
| Last accepted / merged phase and PR | None |
| Next exact eligible JR | JR-001 |
| Completed tasks in current session | None |
| In progress / blocked tasks | None |
| Owner decisions pending | Consult `DECISIONS.md` and suite `Suite - To Be Scoped.md` |
| Tests run, result, fixture | None run by the planning synchronization |
| Files / migrations / schemas changed | None by the execution process |
| Current work phase | Intake pending user-selected session |
| Next-session authorization | Owner must explicitly start a numbered session |

## What a new session must do first

Read `AGENTS.md`, `docs/junior-execution/SESSION-PROTOCOL.md`, `SESSION-INDEX.md`, `STEP-REGISTER.md`, `STEP-LOG.md`, `DECISIONS.md`, this file, and the selected original 20-task phase. Verify the authoritative branch, commits, CI status, and actual tests before resuming.

## Last session summary

_No session has started._

## Exact next safe action

Receive `SESSION_NUMBER` in the session prompt, verify prerequisites and start at the first unfinished task in that range.

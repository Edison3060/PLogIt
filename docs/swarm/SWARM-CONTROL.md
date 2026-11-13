# PLogIt: independent Swarm session controller

**This controller lives on branch `Swarm` and governs that branch only.** It does not modify the separate `control/session-state` controller or signal a main-branch phase acceptance.

| Key | Value |
|---|---|
| NEXT_SESSION | 08 |
| STATE | READY |
| LAST_ASSESSED_SESSION | 07 (inherited from work phase branch) |
| CURRENT_JR_RANGE | JR-071–JR-080 |
| CURRENT_ORIGINAL_PHASE | 04B |
| SWARM_BRANCH | Swarm |
| SWARM_RUN_OWNER | NONE |
| BASE_BRANCH | work/plogit-phase-04 |
| BASE_COMMIT_SHA | e3e2f5ae8132df4988c615490f3676ec7fc739d9 |
| LAST_VERIFIED_SWARM_COMMIT | NONE |
| UPDATED_UTC | 2026-11-13 |
| MAIN_MERGE_ALLOWED | NO |
| LEGACY_CONTROLLER_UPDATE_ALLOWED | NO |

## Current assignment

Session 08 is ten tasks grouped into two topical packets:

- **Worker A, JR-071–JR-075:** Scope and rules of engagement.
- **Worker B, JR-076–JR-080:** leadership transfer and team UX.

The original session index and phase roadmap are authoritative for exact task titles and requirements. Workers may start in parallel **only** when source files, migrations, schemas and prerequisites are disjoint; coordinator resolves shared interface dependencies before implementation.

## Update procedure

1. On a fresh native Antigravity `/swarm`, coordinator reads this file on `Swarm`, inspects the branch's JR register, handoff and last commits, and verifies no competing active swarm.
2. For READY, set STATE=IN_PROGRESS, add a unique SWARM_RUN_OWNER and UPDATED_UTC, commit and push `Swarm`. Do **not** advance NEXT_SESSION on claim.
3. For IN_PROGRESS, resume the same numbered session only when previous activity is confirmed inactive. Avoid concurrent claims or force-pushes.
4. After every JR, coordinator commits and pushes the reviewed work and the JR's register/log/handoff update to `Swarm`. Workers and verifier do not edit the controller.
5. Only after all ten assigned JRs are safely assessed, logged and pushed, advance NEXT_SESSION to the next number, update CURRENT_JR_RANGE and CURRENT_ORIGINAL_PHASE from SESSION-INDEX, set STATE=READY and SWARM_RUN_OWNER=NONE, record LAST_ASSESSED_SESSION and latest pushed SHA, then commit and push this file on `Swarm`. IN REVIEW may persist for provisional external acceptance, clearly reported. If NOT READY due to local blocker, retain the number, status IN_PROGRESS and the exact issue.
6. On final Session 24, replace NEXT_SESSION with COMPLETE, STATE=COMPLETE, and stop. Never reset to 01 automatically.
7. **Never update `main`, `control/session-state` or an existing `work/*` branch for this experiment.** All source and controller changes stay on `Swarm`.

## Initialization

2026-11-13: Created `Swarm` from the already-developed `work/plogit-phase-04` commit above. JR-071–080 were confirmed NOT STARTED on the copied step register. No new JR was implemented by initialization. Original session tracker remains independent.

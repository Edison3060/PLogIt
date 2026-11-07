# PLogIt: automatic session controller

**AUTHORITATIVE LIVE STATE IS THIS FILE ON BRANCH `control/session-state`, NOT A COPY ON `main` OR A DEVELOPMENT BRANCH.**

Only this per-app controller selects what a fresh Builder Lead session does next. The reusable prompt has no session number. The owner starts a new coding session by pasting the same prompt; the Lead must fetch THIS branch and read this file before choosing any JR work. The original 24-session index still maps ten exact JR IDs per session.

## Machine-readable session pointer (manually maintain as plain Markdown)

| Key | Value |
|---|---|
| NEXT_SESSION | 03 |
| STATE | IN_PROGRESS |
| LAST_ASSESSED_SESSION | 02 |
| LAST_ASSESSED_JR_RANGE | JR-011–JR-020 |
| CURRENT_TARGET_JR_RANGE | JR-021–JR-030 |
| CURRENT_TARGET_PHASE_BRANCH | work/plogit-phase-02 |
| LAST_PHASE_BRANCH | work/plogit-phase-01 |
| CURRENT_RUN_OWNER | gpt6-session03-2026-11-07T04:24:33.825Z |
| LAST_UPDATE_UTC | 2026-11-07T04:24:33.825Z |
| PENDING_PHASE_ACCEPTANCE | Phase 01 not necessarily accepted or merged; inspect actual branch and PR |
| AFTER_FINAL_SESSION | COMPLETE, no automatic wraparound |

## Verified baseline when this controller was created

GitHub step registers and handoffs on `work/plogit-phase-01` show that JR-001 through JR-020 have all been **assessed**, not necessarily accepted. The next eligible independent development increment is Session 03, JR-021–JR-030. Phase 01 reference and modernization tasks are assessed as DONE, but the draft phase PR and deployment safety gate remain separate; never merge while production CD authorization is pending.

**Do not retroactively label IN REVIEW as DONE, approve open policies, merge a draft PR, or claim actual producer integration.** Keep the application-specific integration assumptions and decision debt in the step records.

## Claim / resume protocol

1. Read `AGENTS.md` and `docs/junior-execution/SESSION-PROTOCOL.md` from latest `main`, plus the phase branch's real source/logs and this file on `control/session-state`. Check whether another execution is actively using the same application.
2. If `STATE=READY`, select `NEXT_SESSION` and the matching ten tasks from SESSION-INDEX, then **claim** it by updating this file on `control/session-state` to `STATE=IN_PROGRESS`, setting `CURRENT_RUN_OWNER` (a meaningful run identifier/timestamp), and `LAST_UPDATE_UTC`. Commit and push the claim. Never advance the number at claim time.
3. If `STATE=IN_PROGRESS`, assume the same `NEXT_SESSION` needs resumption. Check the latest GitHub commits and handoff. Do **not** start a duplicate concurrent Lead or increment the pointer; resume only after verifying no other run is active.
4. If a controller update conflicts with a simultaneous write, re-fetch and resolve the conflict. Never force-push the control branch or claim overlapping sessions.
5. For every JR, update the step register, append a dated per-JR log, update the handoff/integration ledger, commit/push to the **phase development branch**. This controller is a pointer, not a substitute for per-task evidence.
6. At the end of the assigned ten tasks, reconcile every JR status against the actual phase branch and executed tests. **Advance NEXT_SESSION by exactly one only if all ten steps have been assessed and safely recorded**, and the verdict is READY or READY WITH PROVISIONAL INTEGRATION. IN REVIEW may remain for external gates. Set STATE=READY, LAST_ASSESSED_SESSION, LAST_ASSESSED_JR_RANGE, CURRENT_TARGET_JR_RANGE/PHASE_BRANCH, CURRENT_RUN_OWNER=NONE, LAST_UPDATE_UTC. Commit/push the controller independently of the application phase PR. If local unsafe prerequisites remain or tracking could not be saved, leave NEXT_SESSION unchanged, STATE=IN_PROGRESS, and report NOT READY.
7. At session 24, if the ten tasks are safely assessed, set `NEXT_SESSION=COMPLETE`, `STATE=COMPLETE`. Do not silently treat provisional production/release gates as approved.
8. Updating this control branch must **never merge application source or trigger production deployment**. No unattended direct commit to `main` for pointer changes. If the coding environment cannot write this control branch, report the exact blocker and keep the pointer unchanged.

## Branch ancestry and release gates

Odd sessions start/resume one of the original twelve `work/plogit-phase-XX` branches; even sessions continue the same branch. When a preceding phase PR is unmerged, **base the next phase branch on the preceding unmerged phase branch** and document the stacked ancestry. Do not start from stale main, discard source changes, or merge a PR whose required acceptance, security or CI gates are missing.

**Owner initiation of a new chat constitutes authorization for the current numbered session shown here; the Lead still stops after that single ten-JR session.** Routine recommendations and safe provisional cross-app interfaces are preauthorized. Unsafe/irreversible production operations remain separately controlled.

## Update history

- 2026-11-07T04:24:33.825Z: Session 03 JR-021–JR-030 claimed by gpt6-session03-2026-11-07T04:24:33.825Z; phase branch to stack on work/plogit-phase-01 HEAD 6426609e77b436e2804b9c65c9c3701134c54967; no main merge or production deploy.


- 2026-11-06: initialized for Session 03 after inspecting Session 02 per-JR registers and handoffs. No application JR status was changed by controller creation.

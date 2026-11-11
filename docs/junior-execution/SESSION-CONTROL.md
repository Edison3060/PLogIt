# PLogIt: automatic session controller

**AUTHORITATIVE LIVE STATE IS THIS FILE ON BRANCH `control/session-state`, NOT A COPY ON `main` OR A DEVELOPMENT BRANCH.**

Only this per-app controller selects what a fresh Builder Lead session does next. The reusable prompt has no session number. The owner starts a new coding session by pasting the same prompt; the Lead must fetch THIS branch and read this file before choosing any JR work. The original 24-session index still maps ten exact JR IDs per session.

## Machine-readable session pointer (manually maintain as plain Markdown)

| Key | Value |
|---|---|
| NEXT_SESSION | 07 |
| STATE | IN_PROGRESS |
| LAST_ASSESSED_SESSION | 06 |
| LAST_ASSESSED_JR_RANGE | JR-051–JR-060 |
| CURRENT_TARGET_JR_RANGE | JR-061–JR-070 |
| CURRENT_TARGET_PHASE_BRANCH | work/plogit-phase-04 |
| LAST_PHASE_BRANCH | work/plogit-phase-03 |
| CURRENT_RUN_OWNER | gpt6-session07-2026-11-11T19:22:26.929Z |
| LAST_UPDATE_UTC | 2026-11-11T19:22:26.929Z |
| PENDING_PHASE_ACCEPTANCE | Phase 01 draft PR #22, Phase 02 draft PR #23 and stacked Phase 03 draft PR #24 unmerged; Phase 03B JR-051–060 DONE with 137 Maven/18 Vitest and full security/Compose green CI 43023154965 at 9a197a39, final docs handoff ff2e2ba; prior Phase 02 JR-040 scanner phase-head gate IN REVIEW despite newer Phase03 green scan; prior JR-044/045 idle-session wall-clock expiry IN REVIEW; direct-API CSRF bootstrap release compatibility review; owner main production CD DEC-2026-11-06-11 pending; JudgeIt INT-001 real consumer PROVISIONAL; no main phase merge/deployment authorized |
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

- 2026-11-07T04:35:10.967Z: Session 03 JR-021–JR-030 all independently assessed/pushed to stacked `work/plogit-phase-02`, JR evidence closed against green [CI 42338792629](https://github.com/Edison3060/PLogIt/actions/runs/42338792629) on tested code SHA `c3b5b562aaddba47b9d8f426418d6e2f343e5b1f`, final docs-only branch SHA `677fba920180aaad170848d0217ff7dc858275c3`. Readiness **READY WITH PROVISIONAL INTEGRATION**. Assign Session 04 JR-031–JR-040 on same Phase 02 branch; PR #23 draft stacked on PR #22, neither merged, no production deployment. Control claim cleared.


- 2026-11-07T04:24:33.825Z: Session 03 JR-021–JR-030 claimed by gpt6-session03-2026-11-07T04:24:33.825Z; phase branch to stack on work/plogit-phase-01 HEAD 6426609e77b436e2804b9c65c9c3701134c54967; no main merge or production deploy.


- 2026-11-06: initialized for Session 03 after inspecting Session 02 per-JR registers and handoffs. No application JR status was changed by controller creation.

- 2026-11-08T09:49:36.610Z: Session 04 JR-031–JR-040 claimed by gpt6-session04-2026-11-08T09:49:36.610Z; resume existing stacked work/plogit-phase-02 branch; no production deployment or main merge.

- 2026-11-08T10:12:50.923Z: Session 04 JR-031–JR-040 independently assessed and ten JR commits pushed on `work/plogit-phase-02`; final evidence commit `bf2c711c85d20a227fa119dceacf1dc3c4ce4496`. JR-031–039 DONE, JR-040 IN REVIEW for current-head scanner release-CDN HTTP 504 (Trivy not executed in latest runs). At source SHA `8fa9274e240d835041b13ea94f099c69e5715a88`, [push CI 42469824649](https://github.com/Edison3060/PLogIt/actions/runs/42469824649) SUCCESS with Maven 127, Flyway 2, Vitest 18, live Compose both positive/negative, Trivy filesystem+images, Semgrep/proxy; CD SKIPPED. Latest retry-only scanner SHA `63bf726a7c59cdbc572d7be83719b15f07a18077` [push 42470111362](https://github.com/Edison3060/PLogIt/actions/runs/42470111362) and [PR 42470115672](https://github.com/Edison3060/PLogIt/actions/runs/42470115672) application tests PASS, pinned scanner download HTTP 504 FAIL. Readiness **READY WITH PROVISIONAL INTEGRATION** for independent development, **NOT** Phase 02 PR acceptance/release. Assign Session 05 JR-041–JR-050 on future `work/plogit-phase-03` stacked on unmerged Phase 02; controller claim cleared. #22/#23 remain DRAFT, no main merge/production deployment, owner DEC-11 and real JudgeIt integration unverified. STOP after Session 04.

- 2026-11-09T18:23:43.099Z: Session 05 JR-041–JR-050 claimed by gpt6-session05-2026-11-09T18:23:43.099Z; Phase 03 to stack on Phase 02 HEAD bf2c711c85d20a227fa119dceacf1dc3c4ce4496; no main merge or deployment.

- 2026-11-09T18:43:18.489Z: Session 05 JR-041–JR-050 all independently assessed/pushed on `work/plogit-phase-03` stacked on `work/plogit-phase-02@bf2c711c85d20a227fa119dceacf1dc3c4ce4496`; ten JR numbered commits and final tracking-only handoff `c173e31e7fb9a6c6bbd3bebf2f9cf436139f8701`. DONE JR-041/042/043/046/047/048/049/050, IN REVIEW JR-044/045 for elapsed session inactivity timeout (real logout/replay denial tested). Executable SHA `9ba41623dab7319f5b9951d9fff68a75f91b07e8` [green full CI 42675762958](https://github.com/Edison3060/PLogIt/actions/runs/42675762958): Maven 133, Vitest 18, live Compose auth/CSRF positive/negative no mutation, Semgrep, Trivy filesystem+images, proxy PASS; CD SKIPPED. Earlier [CI 42675291335](https://github.com/Edison3060/PLogIt/actions/runs/42675291335) had 5 old-CSRF-fixture Maven failures corrected in JR-050; logged without erasure. [Draft stacked Phase03 PR #24](https://github.com/Edison3060/PLogIt/pull/24), #22/#23 still DRAFT, no main merge or production deployment. Real JudgeIt integration remains INT-001 PROVISIONAL; release DEC-11 withheld. Readiness **READY WITH PROVISIONAL INTEGRATION** only for safe independent work. Assign new Session 06 JR-051–JR-060 on the **same** Phase 03 branch, clear run claim and STOP after Session 05.

- 2026-11-11T12:32:30.670Z: Session 06 JR-051–JR-060 claimed by gpt6-session06-2026-11-11T12:32:30.670Z; resume Phase 03 branch HEAD c173e31e7fb9a6c6bbd3bebf2f9cf436139f8701; no main merge or deployment.

- 2026-11-11T12:52:10.790Z: Session 06 JR-051–JR-060 all independently assessed, each JR-numbered commit pushed to `work/plogit-phase-03` (JR-051 `3fb29745d9f8` through JR-060 executable `9a197a39eb6c0945485f9f0b4c1192aefab9bb3c`), final tracking-only handoff `ff2e2ba0f3c1171363f12bdb9f6a362f83eacdca`. All ten **DONE** after [full green push CI 43023154965](https://github.com/Edison3060/PLogIt/actions/runs/43023154965): Java21/PostgreSQL16 Maven **137**, frontend **18**, isolated real HTTP/Compose auth/CSRF, Semgrep, Trivy filesystem and both images, proxy ALL PASS; production CD SKIPPED. Preserve existing IP login quota 20/min and add independent account quota default 5/min, DEC-2026-11-11-03; no unversioned export, privacy or role weakening. Draft stacked #22/#23/#24 not merged; prior JR-044/045 idle-time and Phase02 JR-040 scanner phase-gate IN REVIEW, JudgeIt INT-001 PROVISIONAL, owner DEC-11 main deployment withheld. Readiness **READY WITH PROVISIONAL INTEGRATION** solely for independent development. Next **Session 07 JR-061–JR-070**, future `work/plogit-phase-04` stacked atop unmerged Phase03, no JR-061 started here; controller claim cleared, STOP after Session 06.

- 2026-11-11T19:22:26.929Z: Session 07 JR-061–JR-070 claimed by gpt6-session07-2026-11-11T19:22:26.929Z; Phase 04 will stack on unmerged Phase 03 HEAD ff2e2ba0f3c1171363f12bdb9f6a362f83eacdca. No main merge or production deployment.

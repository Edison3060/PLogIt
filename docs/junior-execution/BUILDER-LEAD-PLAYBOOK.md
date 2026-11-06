# RES Builder Lead: persistent GitHub execution playbook

**This is the common operating procedure for every fresh session.** It is mirrored in each independent application repository. The short repository-specific Builder Lead prompt identifies the only repository the Lead may modify. No particular model, model vendor or coding platform is required.

## 1. Configuration and repository ownership

The prompt defaults to `USE_SUBAGENTS: NO` and `SUBAGENT_MODEL: N/A`. With NO, the Lead writes, debugs, tests, reviews, integrates and tracks work itself. If the user later changes YES, use genuine available coding subagents and the chosen subagent model only when the environment supports it; otherwise work directly and disclose the fallback. Do not invent delegation.

**All coding and GitHub writes are restricted to the application repository named in the prompt.** You may read relevant sibling repositories or public documentation for context, but do not edit those repositories. You are authorized to commit, push, create or review pull requests, and merge **accepted** development phases subject to required checks. Never bypass branch protection, disable CI/security controls, expose secrets, destroy production data, or deploy without explicit separate approval.

## 2. First reads, automatic session selection and concurrency

This is a fresh chat without previous context. **Before implementation, read this playbook and `AGENTS.md`, `SESSION-PROTOCOL.md` and `SESSION-CONTROL.md` from the named repository's GitHub branch `control/session-state`.** The controller on that branch is the single authoritative next-session pointer. Older copies of instructions on `main` or development branches may be stale.

Read `docs/junior-execution/SESSION-INDEX.md` on the control branch, the applicable original 20-step roadmap phase, suite and app specifications, mirrored Master Prompt and Rules to Follow. Read the **actual working phase branch's** `STEP-REGISTER.md`, append-only `STEP-LOG.md`, `SESSION-HANDOFF.md`, `DECISIONS.md` and `INTEGRATION-ASSUMPTIONS.md`, plus real source, tests, commits, CI and PR status.

Use controller `NEXT_SESSION` and SESSION-INDEX to find the **exact ten original JR IDs**. Do not ask the user for a session number, guess one from previous chat, or skip earlier unassessed prerequisites. The user opening this session is authorization to work on that one current ten-JR increment. No automatic execution of the next increment in the same chat.

When controller STATE is READY, atomically claim the current session on `control/session-state` with a unique run identifier, mark it IN_PROGRESS and push. **Do not increment at claim time.** When STATE is IN_PROGRESS, inspect the claim and recent commits: do not start a competing session; resume only if the previous runner is clearly no longer active. If uncertain, report collision rather than overwrite. On conflicting control writes, refetch and retry safely; never force-push. If COMPLETE, report completion and stop.

## 3. Work autonomously but within real safety and acceptance boundaries

Begin with a concise plan, current verified status, risks, dependencies, the tests you intend to run and the ten-step boundary. This is the authorized Intake/Strategy checkpoint under the Master Prompt, so **select and implement your recommended routine reversible engineering approach without repeatedly asking the user for approval**. Document important approaches, trade-offs and decisions. Continue through every eligible assigned JR.

For unfinished sibling applications, research the actual available producer/consumer code and approved suite specifications where possible. **Do not block solely because another app or real JSON handoff is unfinished.** Build safe, replaceable, versioned **provisional one-way offline JSON** schemas, validators, adapters and synthetic fixtures. Keep each application's database independent. Record every meaningful assumption, evidence source, assumed version/fields, local tests, affected counterpart and future reconciliation task in `INTEGRATION-ASSUMPTIONS.md`. Separate local synthetic success from genuine cross-app interoperability. Required real-producer evidence remains IN REVIEW until actually verified; do not falsely mark DONE.

Only seek an additional owner decision for genuinely unsafe or irreversible actions that cannot be isolated, such as production deployment, destructive live-data operations, privacy/security weakening or exposure of secrets. Present the **recommended safe option first**, why, alternatives and affected JRs. Continue unrelated safe work.

## 4. Selective research, security and evidence

Both the Lead and any future subagents should use available web search **when useful**, not for every routine change: changing library/framework support, API/standards guidance, current vulnerability advisories or significant architectural choices. Prefer official documentation, release notes and primary advisories, check publication/version dates, evaluate compatibility/maintenance and avoid choosing newest merely for newness. Record consequential URLs, access dates and design decisions in the relevant JR log. If web access is unavailable, state the limitation. Never submit private data, repository secrets or customer evidence to public search. Web research is not a test result.

Review the current source and specifications rather than trusting old summaries. Execute actual available positive, negative, security, authorization, contract, database/frontend/backend and regression tests appropriate to the step. Preserve exact commands, versions, fixtures, expected vs observed results, failures, exit codes and missing test capabilities. Never fabricate test passes, production readiness or audit/compliance certification.

## 5. Mandatory evidence checkpoint after EVERY JR

For each selected JR, including no-change verification, IN REVIEW or BLOCKED:
1. Read exact original task, dependencies and acceptance criteria. Update the register row on entry.
2. Implement or verify the work. Run available real tests and correct defects.
3. Update its exact `STEP-REGISTER.md` row with truthful status and evidence. DONE requires the applicable original acceptance evidence. Use IN REVIEW for implemented but externally unverified work.
4. Append a dated, **JR-specific** entry to `STEP-LOG.md`: work done, reason, changed paths, expected/actual, real test evidence, research sources when used, assumptions/decisions and next action. Never erase old failures.
5. Update `SESSION-HANDOFF.md` so a new chat can resume with zero previous context, and update `INTEGRATION-ASSUMPTIONS.md` and `DECISIONS.md` as needed.
6. Create a reviewed **JR-numbered Git commit and push it to the correct application development phase branch**. Record commit/PR references. If interrupted, preserve accurate IN_PROGRESS evidence and resume that JR.

Each pair of ten-JR sessions makes one of the original 12 phases of 20 JRs, using the existing `work/<app>-phase-XX` branch conventions. Odd sessions begin/resume the phase branch and even sessions continue it. If a previous phase branch is not merged, preserve its ancestry using a documented stacked branch/PR. **Do not merge a phase whose mandatory review, security, CI or genuine integration acceptance gates fail.** Pending phase acceptance does not, by itself, prevent safe independent later sessions. Do not push/merge to `main` merely to progress sessions. **PLogIt in particular has a main-branch production-deployment workflow.**

## 6. Advance the controller, report, then STOP

Once the current ten JRs have all been **assessed, logged and pushed**, independently assess whether subsequent safe development is possible:
- **READY**: the current ten steps meet applicable verification needs and the next session may begin.
- **READY WITH PROVISIONAL INTEGRATION**: safe development can continue although authentic cross-app/owner approvals or phase merge gates remain pending, explicitly IN REVIEW.
- **NOT READY**: a concrete local safety or prerequisite blocker prevents the next increment, or the session/controller could not be safely persisted.

For READY or READY WITH PROVISIONAL INTEGRATION, **advance NEXT_SESSION by exactly one** in `SESSION-CONTROL.md` on `control/session-state` only, update last-session, target JR range/phase branch, state READY, clear the run claim, and commit/push the control change. Check the controller's exact field rules and latest head first. Never advance from 24 back to 01: after the final assessed session, set COMPLETE. For NOT READY, keep the pointer on the unfinished session and record the exact issue. **Do not update main to change the pointer.**

Report the ten JR statuses, actual tests, branch/pushed SHAs, PR/merge status, provisional debt, pending decisions, readiness and controller's saved next assignment. Present the Master Prompt sign-off checklist. **STOP. Never start the next ten tasks inside the same session.**

## Instruction hierarchy for this workflow

Read the original Master Prompt and application Rules to Follow for standards on verification, concise strategic advice, evidence, audit logs and self-contained handoffs. The user has expressly preauthorized normal ten-step development, recommendation execution, provisional sibling interfaces, and automatic controller advancement. This playbook and current `SESSION-CONTROL.md` supersede **older procedural instructions** that instead require user-entered session numbers, routine repeated sign-off or blocking solely because sibling apps are incomplete. They do **not** waive a genuine safety, legal/privacy, CI or production release requirement.

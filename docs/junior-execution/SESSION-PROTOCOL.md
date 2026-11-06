# PLogIt: bounded development session protocol

This is the **hot operating constitution**. A new chat must read this file, the step register, the log, decisions, session handoff, the selected phase file, and the mirrored [Master Prompt](../../planning/RES-application-plan/PlogIt%20Workspace/Rules%20to%20Follow/Master%20Prompt.md) before implementation.

## Scope and restart contract

- The owner authorizes **one 10-step increment per session** by supplying SESSION_NUMBER (01–24) in the reusable Builder Lead prompt. Do not continue into the next session automatically.
- Each increment is half of an existing 20-step roadmap phase. The original phase text is the source of exact requirements; the session index only partitions it.
- Start with Intake: classify scope, inspect repository and actual code, summarize a short plan and risk/verification checkpoints. Continue within the selected authorized increment without asking again for ordinary coding or Git actions. Strategy decisions follow actual current specifications.
- At the **end** of the selected increment, show the Master Prompt's sign-off checklist and **stop** for the owner's acceptance/next-session authorization. Also halt and ask before protected owner decisions. Keep honest "Intake / Strategy / Build / Refine / Handoff" phase awareness in your handoff.
- If interrupted, the same session number resumes at the first incomplete eligible JR. Never trust previous chat memory. Respect unfinished prerequisite/blocked tasks.
- PLogIt is already implemented. Verify first. No unnecessary rebuild, migration or silent API/export behavior change. Historical RES copied source is reference-only.

## Delegation placeholders

The user selects the Session Lead model outside this prompt. Do not prescribe a Lead model or platform. The optional subagent model comes only from the user's SUBAGENT_MODEL value.

- Follow `USE_SUBAGENTS` and `SUBAGENT_MODEL` exactly as specified in the session's prompt. When YES, use real available subagents; configure the requested model only if supported. If subagents or model selection are unavailable, proceed directly as Builder Lead and note the fallback. When NO, do all work directly. Never invent subagent runs.
- The Lead remains responsible for review and executed tests in every mode. In parallel assign exclusive files and agree interfaces.

## GitHub procedure and boundaries

- This workflow **authorizes writing to this repository**, including commits, pushes, PRs and merges. Operate only in `Edison3060/PLogIt`. Do not touch the other apps without their own authorization. Never alter `planning/RES-application-plan/` reference copies merely to mark progress.
- For original phase XX, use `work/plogit-phase-XX` (01–12). Odd-numbered session creates or resumes branch from current main; even-numbered session must resume the same branch with Session A commits. Don't create a new Phase B branch or skip unfinished Phase A.
- Commit a reviewed checkpoint with JR ID after **every step**, including a docs-only entry for BLOCKED or VERIFIED NO CHANGE. Push branch progress after each JR so another session sees it. Before committing, check secrets and generated files; never force-push or bypass branch protection.
- After even-numbered sessions, run the original 20-task phase gate; open/review/merge PR into main **only if all required acceptance, security and CI checks genuinely pass**. If gate incomplete, keep branch/PR open, record the blocker and stop. GitHub main is not a testing shortcut.
- Production or irreversible destructive operations require separate owner authorization. No blind resets, erased history, credentials or live-customer data.

## Master Prompt principles

- Use the mirrored Master Prompt and standing rules: scope before building, choose a justified approach, check primary source files, separate verified from inferred facts, avoid overclaiming, keep concise technical communication and use session-boundary sign-off.
- Preserve the Master Prompt's operating artifacts: **constitution** (this file and root AGENTS.md), **spec** (mirrored suite and application docs), **log** (STEP-LOG and DECISIONS), and self-contained HANDOFF.
- The user has scoped this development as a sequence of 10-step increments; approval to start the named session is the bounded intake/strategy authorization for routine implementation. Each session end still requires user sign-off before the next numbered increment. Do not pause for approval after every individual JR.
- Never silently invent tenancy/authorization/privacy policy, JSON contract changes, external integration assumptions, deployment claims or certifications. Present the **recommended option first**, why it is preferred, alternatives/trade-offs, affected JRs, and a direct owner decision request. Block dependent steps only and keep working on safe independent steps.
- Comply with application Rules to Follow, including production security safeguards and source-code conventions; do not add code comments where prohibited. Never claim tests or deployment succeeded without executed evidence.

## Mandatory per-step persistence

For *every* JR, including no-code verification, failure or blockage:
1. Before acting, mark the corresponding `STEP-REGISTER.md` row IN PROGRESS unless a prerequisite blocks work.
2. Execute/review/test what can actually be done. Preserve exact commands, expected versus observed, test limitations and source identifiers.
3. Update the register with status, evidence pointer and UTC date.
4. Append a **new, permanent** entry to `STEP-LOG.md` with JR, session, prior status, result, rationale, paths, tests/outcomes, approvals, blocker, next step, commit/PR. If the entry is made before the final commit, add a follow-up reference after SHA becomes available; never rewrite old log entries to conceal history.
5. Update `SESSION-HANDOFF.md`: session/branch, last JR attempted, verified DONE set, incomplete/blocked steps, last pushed SHA, pending decisions, exact next safe action.
6. Commit and push tracking files alongside the JR task, even if no code changed. Check the GitHub commit SHA and leave it discoverable from the branch history.
7. If unexpectedly interrupted mid-JR, preserve an explicit IN PROGRESS handoff on the branch; resume that JR rather than inventing completion.

## Completion and end-of-session sign-off

A task is DONE only with applicable acceptance evidence (including actual test results). A passing mock fixture is not proof of producer integration. At the end of each 10-step session include: completed/in-review/blocked JRs, GitHub branch, pushed commits/PR, tests run, exact failures and pending owner decisions, one-paragraph handoff, next session number **but do not run it**, plus checklist:
- [ ] I understand the changes and why the approach was selected.
- [ ] Inputs validated; error paths and real server-side controls checked.
- [ ] No secrets, insecure shortcuts or unverified claims.
- [ ] Actual tests and source references recorded.
- [ ] All 10 JR rows assessed; log and handoff saved to GitHub.
- [ ] Approved to begin the next session (user sign-off pending).

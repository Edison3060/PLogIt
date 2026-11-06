# PLogIt Builder Lead: always-read instructions

**Model-agnostic:** The user chooses the Session Lead model outside the repository and optionally supplies SUBAGENT_MODEL for subagents. These instructions do not require a specific provider, model family, or coding platform.

You are in the **canonical implemented PLogIt** repository. Follow the current app Rules to Follow and the mirrored Master Prompt at `planning/RES-application-plan/PlogIt Workspace/Rules to Follow/Master Prompt.md`. For sessionized development, the specific approved 10-step increment protocol is `docs/junior-execution/SESSION-PROTOCOL.md`.

**First reads:** `docs/junior-execution/BUILDER-LEAD-PROMPT.md`, `SESSION-PROTOCOL.md`, `SESSION-INDEX.md`, `STEP-REGISTER.md`, `STEP-LOG.md`, `SESSION-HANDOFF.md`, `DECISIONS.md`, the mapped original JR phase, then actual source/tests. The user supplies SESSION_NUMBER, USE_SUBAGENTS and SUBAGENT_MODEL each new session.

**Always:** work only in this repository; verified source before assumptions; prefer delegation if enabled, direct work if unavailable; log and commit/push each JR; preserve history; use phase branches and gate phase PR merges with tests; never deploy without separate permission; stop at selected 10-task session boundary; recommend an option before requesting any owner-reserved decision; never mark missing tests passed or copy old implementation over current canonical PLogIt code.

## Selective web research for both roles

Use web search when current facts, authoritative security guidance or approach comparisons materially affect a task, not for every action. The Lead researches when needed and instructs delegated subagents to fact-check material uncertainties too. If a subagent lacks search access, the Lead shares sourced findings when possible. Cite sources/dates and reasoning in the per-JR STEP-LOG, prefer compatible stable supported choices over newest-for-newest's-sake, and never leak private data to search.

## Owner-approved execution defaults for upcoming sessions

The Lead **chooses and executes its recommended routine approach** instead of requesting follow-up prompts. The owner has pre-authorized reasonable reversible design choices for each selected ten-JR session. Do not stop simply because a sister application is unfinished. Implement and test a provisional, versioned, replaceable JSON adapter or fixture using the best available sibling source/spec evidence; record every unverified assumption in `docs/junior-execution/INTEGRATION-ASSUMPTIONS.md` and link it from STEP-LOG. Do not mistake mocked integration for real producer/consumer validation or claim a required gate passed when it did not. Preserve mandatory auth, privacy, data-integrity and CI safeguards, and seek approval only for genuine unsafe/irreversible protected operations.

At session end, report **READY / READY WITH PROVISIONAL INTEGRATION / NOT READY**, say explicitly whether the owner may start the next numbered session, and then stop. The current owner-specific priority rules appear at the top of `docs/junior-execution/SESSION-PROTOCOL.md` and take precedence over older routine-approval or sibling-dependency blockage instructions.

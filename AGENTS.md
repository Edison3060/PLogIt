# PLogIt Builder Lead: always-read instructions

**Model-agnostic:** The user chooses the Session Lead model outside the repository and optionally supplies SUBAGENT_MODEL for subagents. These instructions do not require a specific provider, model family, or coding platform.

You are in the **canonical implemented PLogIt** repository. Follow the current app Rules to Follow and the mirrored Master Prompt at `planning/RES-application-plan/PlogIt Workspace/Rules to Follow/Master Prompt.md`. For sessionized development, the specific approved 10-step increment protocol is `docs/junior-execution/SESSION-PROTOCOL.md`.

**First reads:** `docs/junior-execution/BUILDER-LEAD-PROMPT.md`, `SESSION-PROTOCOL.md`, `SESSION-INDEX.md`, `STEP-REGISTER.md`, `STEP-LOG.md`, `SESSION-HANDOFF.md`, `DECISIONS.md`, the mapped original JR phase, then actual source/tests. The user supplies SESSION_NUMBER, USE_SUBAGENTS and SUBAGENT_MODEL each new session.

**Always:** work only in this repository; verified source before assumptions; prefer delegation if enabled, direct work if unavailable; log and commit/push each JR; preserve history; use phase branches and gate phase PR merges with tests; never deploy without separate permission; stop at selected 10-task session boundary; recommend an option before requesting any owner-reserved decision; never mark missing tests passed or copy old implementation over current canonical PLogIt code.

## Selective web research for both roles

Use web search when current facts, authoritative security guidance or approach comparisons materially affect a task, not for every action. The Lead researches when needed and instructs delegated subagents to fact-check material uncertainties too. If a subagent lacks search access, the Lead shares sourced findings when possible. Cite sources/dates and reasoning in the per-JR STEP-LOG, prefer compatible stable supported choices over newest-for-newest's-sake, and never leak private data to search.

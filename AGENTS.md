# PLogIt Builder Lead: always-read instructions

**Model-agnostic:** The user chooses the Session Lead model outside the repository and optionally supplies SUBAGENT_MODEL for subagents. These instructions do not require a specific provider, model family, or coding platform.

You are in the **canonical implemented PLogIt** repository. Follow the current app Rules to Follow and the mirrored Master Prompt at `planning/RES-application-plan/PlogIt Workspace/Rules to Follow/Master Prompt.md`. For sessionized development, the specific approved 10-step increment protocol is `docs/junior-execution/SESSION-PROTOCOL.md`.

**First reads:** `docs/junior-execution/BUILDER-LEAD-PROMPT.md`, `SESSION-PROTOCOL.md`, `SESSION-INDEX.md`, `STEP-REGISTER.md`, `STEP-LOG.md`, `SESSION-HANDOFF.md`, `DECISIONS.md`, the mapped original JR phase, then actual source/tests. The user supplies SESSION_NUMBER, USE_SUBAGENTS and SUBAGENT_MODEL each new session.

**Always:** work only in this repository; verified source before assumptions; prefer delegation if enabled, direct work if unavailable; log and commit/push each JR; preserve history; use phase branches and gate phase PR merges with tests; never deploy without separate permission; stop at selected 10-task session boundary; recommend an option before requesting any owner-reserved decision; never mark missing tests passed or copy old implementation over current canonical PLogIt code.

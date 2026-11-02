# PLogIt — canonical source and offline Junior verification workflow

This is the **actual implemented PLogIt application**, not the old frozen PLogIt code snapshot inside RES. Do not copy the archived RES PLogIt implementation over this repository. Current source belongs at the root `backend/`, `frontend/`, `docs/`, Docker and GitHub Actions paths.

**Plan source:** `Edison3060/RES-application-plan` at SHA `596f8a1ad0997f2cdacfcc859107f3b642e2e545`. Relevant original RES specs/roadmaps are mirrored under `planning/RES-application-plan/`. Planning documents are reference snapshots; code/test evidence in this repository and approved product decisions take precedence over historical July claims.

**Read order:** shared suite 2026 research and master spec; handoff contract evidence; open scoped questions; `PlogIt Workspace/Rules to Follow/`; `PlogIt Workspace/Project Info/`; **all** 17 files in `PlogIt Workspace/Project Info/08-junior-execution-roadmap/`; existing root `README.md`, `docs/PLOGIT-RESEARCH-UPDATE-PLAN-2026-11.md`, and real code/tests.

**PLogIt is already built:** JR-001–JR-240 are current-baseline verification, security/compatibility checks and *separately approved* modernization, not a forced rewrite. If a task is satisfied by code, demonstrate it with actual tests and record VERIFIED NO CHANGE. Existing producer JSON is unversioned, selects APPROVED and optionally EXPORTED logs, and only includes attachment filenames. Do not silently change exporter semantics or deployment SSH fingerprint preflight.

**Roles and offline policy:** One fixed GLM 5.3 Lead plans, delegates, reviews, tests, tracks and makes **local-only Git commits**. All implementation/refactoring/test-writing goes to fixed DeepSeek V4.1 1017 subagents. Never push, fetch, pull, deploy or access production in offline sessions. Keep current implementation code untouched until a legitimate task requires an approved correction.

**Source link caveat:** The canonical RES suite index contains other app links omitted from this per-app planning snapshot; use RES repo when reviewing their docs. This PLogIt roadmap is complete within this mirror.

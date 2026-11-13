# PLogIt — Antigravity Swarm operating contract

**Scope:** One `/swarm` run = one application (`Edison3060/PLogIt`) = one ten-JR session = one integration branch (`Swarm`). Use [SWARM-PROMPT.md](SWARM-PROMPT.md) as the permanent launch brief and [SWARM-CURSOR.md](SWARM-CURSOR.md) as the restart selector. This is an **experiment in an existing named branch**, not a blank repo. **No merges or commits to `main`. No production deploys or data writes.**

## Why the team looks this way

Official [Antigravity /swarm documentation](https://antigravity.google/docs/swarm/) describes a coordinator, named workers with a shared message board, an independent verifier and an auto monitor. Workers **share the workspace by default**, so explicitly request separate working copies/worktrees when concurrent edits could collide. Begin with **two implementation workers + one verifier** (and the built-in monitor as applicable), rather than letting quota-intensive preview swarms fan out without scope. Model selection is entirely the owner's choice in Antigravity; workers normally inherit the current conversation model unless changed through supported controls. Never require a particular vendor or family.

The coordinator **does not act as an extra unchecked coder**. It audits, delegates, integrates, checks independent verification, writes shared tracking records and reports results. It can assign a narrower investigative worker if two feature lanes overlap.

## Source-of-truth hierarchy

1. Current `Swarm` Git branch and canonical application code/tests.
2. `docs/junior-execution/STEP-REGISTER.md` and exact original 240-JR phase files: which tasks and what Done requires.
3. `docs/junior-execution/SESSION-INDEX.md`, `SESSION-PROTOCOL.md`, `SESSION-HANDOFF.md`, `STEP-LOG.md`, `DECISIONS.md` and integration assumptions: historic decisions/evidence/phase dependencies.
4. Product Spec, Rules to Follow and mirrored Master Prompt under `planning/RES-application-plan/`: approved product boundaries, five-stage workflow and factual testing.
5. `SWARM-CURSOR.md`: a **derived scheduling snapshot**; if it conflicts with real source or task ledger, stop and reconcile before starting.
6. Current official library/framework/security docs for facts that may have changed; search when useful, not before every simple edit.

Do **not** import sibling-app code, change their databases, or make online cross-app API calls without approved source evidence. The RES baseline is independently owned databases and offline one-way JSON handoffs. When sibling contracts are unfinished, keep strict versioned provisional adapters and a tracked integration debt, rather than inventing upstream guarantees.

## Exact initial two work lanes

- **Worker A:** Scope and rules of engagement (JR-071–075)
- **Worker B:** Leadership transfer and team UX (JR-076–080)
- **Verifier:** independently inspect worker diffs and actual tests; challenge Leader/member role transitions, per-engagement scope enforcement, immutable submitted logs/evidence, audit trails; pentesters capture facts but do not assign CVSS/CVE severity.

These labels describe the first unattempted session, **not permanent worker assignments**. In future sessions, derive two independent slices of the current ten JR tasks from the original phase file, then adjust workers and file ownership to the actual code. Do not enforce two concurrent writers when source dependency makes parallel edits unsafe.

## Worktree / branch isolation and no-main invariant

- Checkout/clone `Swarm` explicitly; run `git branch --show-current`, `git status`, and check remote branch before launching. Don't work from `main`.
- Coordinator assigns a precise list of **exclusive writable paths** and exact APIs before workers edit. Shared config/DB migration/build files belong to one owner at a time; test owners cannot silently alter another worker's API.
- Ask Antigravity for isolated worktrees for concurrent coding; it may otherwise share one workspace. Give each worker a temporary `swarm-work/<session>/<lane>` branch based on the **current `Swarm` HEAD** if actual Git tools are available. Do not use `Swarm/<lane>`: Git cannot create that ref hierarchy while branch `Swarm` exists.
- Worker writes/commits only on its isolated branch/worktree, posts commit SHA, files changed, command/test evidence and unresolved questions to the shared Board. The verifier independently reviews/reproduces tests and reports findings on the Board, tagging the owner of the failed diff.
- Coordinator accepts reviewed diffs **onto `Swarm` only**, by a safely applied cherry-pick or a PR whose base is exactly `Swarm`. Check conflict/migrations/CI; do not blindly merge unreviewed worker changes. When feasible, run integrated test matrix again after combining both lanes.
- **Never merge, push, force-push or open a PR targeting `main`.** Do not auto-merge any existing phase PR whose base is `main`. Some historical PR stacks and application branches remain unmerged. Do not assume their contents are identical to `Swarm`; reconcile by actual Git diff and tracker before importing anything. Do not reset or rebase `Swarm` just to match `main`.
- Do not commit secrets, access production databases, deploy, modify sibling repos or publish endpoints. User approval is required for production changes, spending, irreversible deletion or major frozen product-scope changes.

## Per-task event loop

1. **Intake and verify:** inspect cursor and exact JR-IDs, original tasks, register/log, genuine current code and tests. Before planning the new ten IDs, inspect earlier IN REVIEW blockers and unmerged PR overlap to confirm source dependencies are safe.
2. **Plan:** brief each worker publicly on the Board with exact source paths, task numbers, acceptance criteria, allowed edits, no-touch paths and negative tests. Request a verifier independent of those workers.
3. **Implementation:** workers research current facts when helpful; implement code/tests in isolated working copies. No invented pass claims, no hardcoded unreviewed cross-app contract and no silent changes to product scope.
4. **Adversarial review:** verifier checks permissions, input limits, tenant isolation, CSRF/auth, migration safety, idempotency and valid/hostile tests; ask worker to correct failures. No imaginary runtime/CI results.
5. **One-owner integration:** coordinator merges/cherry-picks reviewed code to `Swarm`, runs actual available integrated checks and records source SHA and remaining unverified facts.
6. **Durable step logs:** **coordinator alone** updates each touched row in `docs/junior-execution/STEP-REGISTER.md`, appends dated JR and Board/verifier evidence to `STEP-LOG.md`, updates decisions/integration assumptions and writes `SESSION-HANDOFF.md` in the main integration worktree. Do not let multiple agents append to the same Markdown file concurrently.
7. **Session exit:** after all ten JR tasks have real disposition and source work safely accounted for, commit logs/tests/handoff, re-read branch HEAD and register, and update `SWARM-CURSOR.md` **last**. Only then consider advancing by one session. If core locally feasible work or dangerous prerequisite remains, cursor stays unchanged. Never claim a task DONE if required real tests have not run.
8. **Stop:** report precise session outcome and wait for a **new owner-initiated /swarm run**. No perpetual background work claim.

### Test and release evidence

Distinguish **unit/static/contract tests**, **actual Java/React/PostgreSQL/HTTP integration**, **real cross-application JSON compatibility**, and **live deployment**. Tooling or fixture-only PASS is valuable but not a substitute for actual later layers. External waiting items may remain `IN REVIEW` with precise next-test plan; security and data-loss bugs may not be dismissed as 'provisional'. The verifier signs off on **observed** source and commands only.

### Progress and quota safeguards

Antigravity's /swarm is preview and uses more quota than single-agent sessions. Limit the first test to two workers + verifier, one ten-JR range, and a concrete stop rule. If quota pauses a worker, preserve partial commits/Board evidence and handoff rather than restarting duplicate edits. Keep worker idling automatic, monitor only if supported. If /swarm isn't available in the current Antigravity plan/surface, do not claim to have run it; the documents still describe a ready experiment.

**Branch protection confirmation:** These files are created/updated only on `Swarm`, not on `main`.

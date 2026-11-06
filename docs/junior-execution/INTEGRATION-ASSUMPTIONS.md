# PLogIt: provisional cross-application integration ledger

This is the persistent reconciliation backlog for **PLogIt → JudgeIt evidence export**. The owner authorizes implementation using reasoned provisional assumptions while other apps are unfinished. **No entries are confirmed until the real producer and consumer are tested together.** Provisional contracts are not approval to rewrite producer-owned semantics or break deployed behavior.

After every JR involving another app, append/update the relevant entry and reference its ID from `STEP-LOG.md` and `SESSION-HANDOFF.md`. Carry the unverified integration obligation into the final session readiness verdict. A synthetic passing fixture is real evidence for *local fixture behavior only*, never for external interoperability.

## Entry template

### INT-001 | PROVISIONAL | JR-XXX | Interface or behavior

- **Scope:** PLogIt → JudgeIt evidence export
- **Producer / consumer:** PLogIt existing canonical exporter → JudgeIt
- **Current ground truth:** Repo, file paths, commit SHAs and JSON examples actually inspected; state what is verified and what is inferred
- **Recommended assumption chosen:** Explain the interim behavior and why it is reasonable
- **Assumed contract:** Version, schema path, fields, identifiers, units, null/error behavior, UTC rules and ownership
- **Replaceable integration boundary:** Adapter/validator module and tests; never direct shared database writes
- **Fixture paths:** Synthetic producer/consumer input and expected output with reproducible commands
- **Actual local test outcome:** Command, environment, exit code, expected versus observed
- **Real producer/consumer verification:** PENDING until tested with real output; link evidence when eventually confirmed
- **Reconciliation task:** Specific future adaptation and owners, applicable JR IDs or GitHub issue
- **Status:** PROVISIONAL / CONFIRMED / SUPERSEDED, never implicitly upgraded
- **References:** STEP-LOG JR, decision ID, commit SHA and PR

## Existing provisional entries

### INT-001 | PROVISIONAL | JR-016 / JR-018 / JR-020 | PLogIt → JudgeIt file-only evidence handoff

- **Scope:** PLogIt canonical producer to unbuilt JudgeIt consumer, offline one-way file transfer only.
- **Producer / consumer:** Existing `ExportService.java` and `JsonReportStrategy.java`, authenticated leader-only `POST /api/engagements/{id}/export`, unversioned JSON. Consumer has NOT been verified.
- **Current ground truth:** `Edison3060/PLogIt@da9ebfa80d7ed9a0632af24284c95f90e49474b1`, filename-only attachments, state APPROVED (optional prior EXPORTED) and export audit/state mutation; source UUIDs from PLogIt.
- **Recommended assumption chosen:** Opt-in *separate* provisional consumer-side validation/adapter, not a producer schema migration, preserving source IDs and evidence limitations. Owner pre-authorized DEC-2026-11-06-04.
- **Assumed contract:** `contractVersion=plogit-judgeit-provisional/0.1`, `sourceApp=PLogIt`, operator-assigned `sourceNamespace`, `sourceEngagementId`, `sourceLogIds[]`, `payloadSha256`, `importKey`, `evidenceCompleteness=attachment-filenames-only-unverified`, original `payload`; UTC timestamps retained from source. Unknown versions, tampering, replay and duplicate/tenant mismatches require fail-closed receiver behavior in later real contract.
- **Replaceable boundary:** `tools/integrations/plogit_judgeit.py` and synthetic mock tests on separate feature branch `feat/plogit-provisional-handoff`, [PR #21](https://github.com/Edison3060/PLogIt/pull/21). No direct shared database, invented network receiver or credentials.
- **Fixture paths / command:** `tools/integrations/tests/test_plogit_judgeit.py` on PR #21; `python3 -m unittest discover -s tools/integrations/tests -p 'test_*.py' -v`. Files/fixtures are on separate PR, NOT Phase 01 branch; follow only after merger.
- **Actual local test outcome:** No local fixture execution in Session 02. GitHub Actions [42212049183](https://github.com/Edison3060/PLogIt/actions/runs/42212049183) completed SUCCESS with 12/12 synthetic bridge/mock tests; backend 125 and frontend 18 as separately logged.
- **Real producer/consumer verification:** **PENDING**. Current PLogIt exporter is source-inspected and separately covered by PLogIt backend tests. No JudgeIt receiver/schema, tenant authorization, real media hash or live cross-app E2E tested.
- **Reconciliation:** Future JR-101–120 and JR-221–240; compare actual JudgeIt import model, approve tenant identity, media/retention, schema and idempotency, then execute both positive and negative actual producer/consumer fixtures before CONFIRMED.
- **Status:** **PROVISIONAL**, never implicitly upgraded. No other-app test marked DONE.
- **References:** DEC-2026-11-06-04 and DEC-06..09, `STEP-LOG.md` JR-016/018/019/020, `SESSION-02-EVIDENCE.md`, feature PR #21.

## Session 02 detailed assumption notes

**Date:** 2026-11-06 UTC. **Status:** PROVISIONAL for downstream suite interfaces; never claim live integration. The binding implementation policy is [provisional suite contracts](../integrations/PROVISIONAL-SUITE-CONTRACTS.md) and DEC-2026-11-06-04. This file adds the session-specific audit required by the Builder Lead; it does not replace product specs.

| Boundary | Current proven contract | Provisional interface and fixtures | Real interoperability status | Replacement gate |
|---|---|---|---|---|
| PLogIt → JudgeIt | Leader-only POST `/api/engagements/{id}/export`; unversioned JSON, APPROVED and optional EXPORTED, filename-only attachment references, EXPORTED audit/state side effects | Separate opt-in JSON adapter `plogit-judgeit-provisional/0.1` on feature PR #21, with operator-provided namespace, immutable source log UUIDs, payload SHA-256 and source import key; 12 synthetic mock-consumer tests passed in run 42212049183 | **UNVERIFIED**: JudgeIt app not available for receiver/E2E testing. No real network transfer, user sync, evidence bytes or signed hash chain | Confirm real JudgeIt schema, auth/tenant consent, import size, replay and revisions, cross-app audit; test positive+negative real fixture E2E |
| JudgeIt → PatchIt | No PLogIt-produced ticket/finding contract | Hypothetical `remediation-tasks.json` fields maintained only as notes in PROVISIONAL-SUITE-CONTRACTS.md | **UNVERIFIED**, no app/consumer testing | Receiver implementation and explicit contract approval |
| PatchIt → GaugeIt | No PLogIt metrics or posture format | Hypothetical `posture-metrics.json` versioned file, downstream-only | **UNVERIFIED**, no app/consumer testing | Receiver implementation and explicit contract approval |

## Working rules

- No alteration of PLogIt producer JSON, role checks, CSRF, state machine, log versions, audit, export inclusion or filename-only semantics without a later scoped approval and tests.
- No invented cross-installation tenancy, shared secrets, API token transport, network endpoint, or claims that an attachment filename proves bytes or hash.
- Keep mock fixture success distinct from real JudgeIt import verification; failed/not-run integration remains pending.
- The prototype feature branch and PR #21 are open, not merged; avoid folding that work into the Phase 01 source-baseline checkpoint without its own acceptance.
- Recheck decisions in JR-017–020 and later actual integration JRs. Production merges/deployments are independently gated.

## Session 02 completion check (2026-11-06)
- JR-018 synthetic register 7/7 PASS and JR-019 unsupported-claim rejection 6/6 PASS. These tests validate **classification**, not remote interoperability or evidence integrity.
- PLogIt → JudgeIt real receiver, tenant authorization and evidence transfer **PENDING**. The optional versioned wrapper remains separate in PR #21; its 12 mock tests do not close the live-integration gate.
- DEC-06..09 real log-inclusion, media, identity, privacy questions remain owner-reserved; DEC-11 production-safe merge approval remains PENDING. No real integration marked DONE.


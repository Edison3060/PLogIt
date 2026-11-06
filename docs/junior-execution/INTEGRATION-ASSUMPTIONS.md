# Session 02 integration assumptions (PLogIt only)

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

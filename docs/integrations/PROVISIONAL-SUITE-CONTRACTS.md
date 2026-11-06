# Provisional Suite Interoperability — PLogIt producer boundary

**Status:** Owner-authorized **working assumptions**, not accepted downstream API/schema contracts. 2026-11-06 (DEC-2026-11-06-04). This document governs only PLogIt. No other app has been changed. Replace or version mappings when real consumers become available.

## Independence rule

Never block PLogIt work *solely* because JudgeIt, PatchIt or GaugeIt is not implemented or its final API is unsettled. Build the part PLogIt controls, with a local deterministic fixture/mock at the external boundary. Test success and negative cases locally, record every unverified assumption, and revisit at later integration. Other blockers (auth, sensitive evidence handling, failing security checks, explicit session boundaries, or mandatory prerequisites) remain real.

## Actual PLogIt producer, unchanged

- Current route: authenticated leader-only `POST /api/engagements/{id}/export` with `{"format":"JSON","includeExported":false}`. CSRF applies. Export mutates APPROVED logs to EXPORTED and audits the action; `includeExported:true` also includes previously EXPORTED logs.
- Actual output: unversioned JSON root containing `engagementId` (database-local Long), `engagementName`, `description`, `startDate`, `dueDate`, `leaderName`, `members`, scope/ROE fields, `exportedAt`, `totalLogCount`, `logs`. Each log carries its exact source UUID `id`, UTC `createdAt`, author, activity, Markdown, review state, tags, code and `attachmentFilenames`. Image bytes/hashes are absent. This is **not** a cryptographically complete evidence bundle. No schemaVersion or source-instance identifier exists.
- This producer behavior is a tested source fact, not a proposed redesign. Never add findings/severity/CVSS/tickets to PLogIt, fabricate attachment integrity, or widen review-state inclusion in order to please an imaginary consumer.

## Working Handoff 1: PLogIt → JudgeIt

**Ready now:** Generate existing PLogIt JSON and feed it to an **opt-in file-only validation/adapter tool** (`tools/integrations/plogit_judgeit.py` on its separate feature branch). The bridge copies the **unchanged** legacy JSON under `payload` and adds a *provisional* wrapper with the source app, operator-supplied namespace, stable source UUID references, content digest and explicit filename-only evidence limitations. The wrapper is a prototype **import candidate**, not a claim that JudgeIt exposes an endpoint accepting it.

**Assumptions for eventual JudgeIt:** It accepts an authenticated, tenant-scoped file import; preserves PLogIt log UUIDs as immutable external references; may map one log to multiple findings; creates its own finding IDs/CVSS/severity in its own store. It validates payload size, duplicate/unknown IDs, privilege and tenant isolation, and re-import idempotency. No credential sync, live POST URL or tenant identity is invented. The source namespace is required from the operator since PLogIt currently has no cross-installation identity field. Never infer organization from a member email.

**Provisional envelope (not PLogIt JSON output):**
```json
{
  "contractVersion": "plogit-judgeit-provisional/0.1",
  "sourceApp": "PLogIt",
  "sourceNamespace": "operator-assigned-instance-key",
  "sourceEngagementId": 42,
  "sourceLogIds": ["00000000-0000-4000-8000-000000000001"],
  "payloadSha256": "<lowercase-hex-sha256-of-canonical-payload>",
  "importKey": "<lowercase-hex-sha256-of-namespace-and-payload-hash>",
  "evidenceCompleteness": "attachment-filenames-only-unverified",
  "payload": {"engagementId":42, "logs":[]}
}
```
The abbreviated `payload` example is schematic only. Real output retains the complete original producer JSON object. `importKey` deduplicates byte-equivalent *canonical JSON* in the same source namespace; a fresh export timestamp or changed records creates a different key, so the consumer must manage revisions explicitly.

## Future Handoff 2: JudgeIt → PatchIt (NOT implemented here)

**Assumed producer-only fields:** a tentative versioned `remediation-tasks.json` carrying `sourceFindingId`, `sourceLogIds`, separate target/asset references, severity label vs CVSS vector/score, remediation guidance, due/verification status and evidence references. JudgeIt decides findings, identity and scoring; PatchIt creates ticket UUIDs and owns assignment/SLA states. No PLogIt ticket or scoring endpoint is required.

## Future Handoff 3: PatchIt → GaugeIt (NOT implemented here)

**Assumed producer-only fields:** a tentative `posture-metrics.json` of dated, traceable asset denominators, ticket counts, severity/verification breakdown, reopened/resolved stats, SLA and risk-acceptance policy notes, cutoff times and data freshness. GaugeIt calculates/presents posture with explicit denominators and provenance. PLogIt does not become a metrics database.

## Merge/reconciliation checklist

1. Treat any provisional wrapper, paths, field names and consumers as disposable; verify the real app's schema from source before connecting.
2. Align authentication, tenant isolation, data transfer consent, import size limits, audit and retention/redaction with actual receiving app; do not use open/public endpoints for pentest evidence.
3. Preserve source log UUID and cross-app provenance; agree on duplicate/revision semantics and id namespace to avoid collisions.
4. Validate nullability/timezone/enum fields against real producer fixtures and receiver tests, including unknown contract versions.
5. Decide if attachment bytes/hashes are needed; filenames alone are insufficient and must remain labeled incomplete. Do not send real evidence to mock/test services.
6. Run real end-to-end integration tests once both applications exist. A passing standalone bridge fixture is **not** proof of JudgeIt/PatchIt/GaugeIt interoperability.
7. Follow normal per-JR/session gates and independently authorize any release. No production deployment from this work.

Original research: `planning/RES-application-plan/Suite - Handoff Contracts and Evidence.md` and `Suite - To Be Scoped.md` are frozen references, not newly approved downstream product specs.

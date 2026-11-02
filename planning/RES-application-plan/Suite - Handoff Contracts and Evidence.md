# RES — evidence-chain and JSON handoff contract research

**Status:** Draft for approval, 2026-11-02. NOT implemented and not a committed JSON Schema.

## Actual PLogIt JSON producer (source snapshot 28aa156)
Read the actual JsonReportStrategy.java, ExportService.java, and EngagementReportData.java under PlogIt Workspace/PLogIt/backend.

Top-level serialized fields:
- engagementId (local Long), engagementName, description, startDate, dueDate, leaderName
- members (email, displayName, role), inScopeTargets, allowedHours, allowedTechniques, forbiddenTechniques, emergencyContacts, outOfScope
- exportedAt, totalLogCount, logs

Each log serializes:
- id (UUID string), createdAt, authorName, activityType, title, description and result (raw Markdown)
- target, toolUsed, outcome, reviewState, tags, codeBlock, codeLanguage, attachmentFilenames

Current inclusion is APPROVED logs by default, optionally previously EXPORTED logs. Other states are NOT exported. Export is a leader-only POST with write side effects (marking approved logs EXPORTED and generating audit events). The JSON contains attachment names, NOT bytes or content hashes. The downloaded filename is suffixed -evidence.json; the name evidence-pack.json in historical plans is conceptual.

## Proposal: contract governance, NOT a description of current output
- Adopt JSON Schema Draft 2020-12, a producer-owned schema file for each link, fixture files produced by real producer tests, and contract tests at consumer ingestion.
- Add a versioned envelope (e.g., schemaVersion, exportId, sourceApp, generatedAt, producerVersion, payload, optional evidenceManifest) only after explicit approval and a compatibility strategy for legacy unversioned exports.
- Preserve original sourceLogId UUID exactly. Use distinct IDs for findings, tickets and posture snapshots, retaining source references and tenant/issuer namespaces to avoid cross-organization collisions.
- Validate nullability, time format (UTC for events; dates as dates), enum values, size/record limits, unknown version behavior and optional fields; tolerate additive fields where policy permits.
- Secure import: authenticated tenant-scoped access, no untrusted relative paths, bounded JSON, safe Markdown rendering, malicious attachment scanning, import audit, encryption at rest and backup, retention and redaction.
- Make imports idempotent by source namespace + exportId/recordId or content hash; define conflict behavior for revisions.
- For evidence integrity, design a manifest with attachment identity, byte length, MIME, SHA-256 hash and transport packaging. Names alone are insufficient. Never embed user-provided file paths directly into storage or download URLs.
- Define who can export or import, who may view sensitive pentest evidence, audit history, and whether absent/rejected raw logs must be included. Avoid silently changing PLogIt's approval/export workflow.
- Keep the offline one-file experience as a design goal; if attachments require a bundle, explicitly reconcile with the "single JSON file" founding constraint instead of pretending it already holds.

## Handoff 1: PLogIt → JudgeIt
Contract owned by PLogIt, consumed by JudgeIt. Define and test exact current legacy JSON first. Decide whether a new version exports all logs or only approved logs; whether image data is base64 embedded, separate signed bundle or excluded with explicit caveat; whether tenant/client identifiers must be added. One log may support multiple findings and a finding may use multiple logs; define that cardinality instead of guessing.

## Handoff 2: JudgeIt → PatchIt (not designed)
Tentative remediation-tasks.json fields: schemaVersion; exportId; sourceFindingId; sourceLogIds; asset references; finding title and impact; evidence references/hashes; CVE/CWE; CVSS standard version/vector/base score and nomenclature; priority rationale; remediation guidance; SLA policy version and due timestamp; source event timestamps. Define ticket cardinality per finding/asset, repeat imports, SLA pause rules and risk-acceptance authority. Don't conflate severity with priority or risk.

## Handoff 3: PatchIt → GaugeIt (not designed)
Tentative posture-metrics.json fields: schemaVersion; snapshotId; tenant/organization; period start/end UTC; source data cutoff; asset population denominator; open/reopened/resolved counts; SLA breaches; verification status; MTTR methodology; exceptions/risk acceptance exclusions; severity breakdown; data freshness; provenance manifest. Aggregations must not double-count overlapping imports and should show methodology.

## Definition of ready for a handoff
- Reviewed producer and consumer field-by-field contract with example real producer payload.
- Valid and invalid fixtures, null/enum/unknown-version tests, repeat-import idempotency tests and tenant isolation tests.
- Preserved source IDs and evidence provenance; document missing records / attachments explicitly.
- Privacy review, provenance integrity and retention policy.
- Approval of any producer-side change before a code PR; no code changes in this research pass.

Primary references: https://json-schema.org/draft/2020-12/ ; https://www.first.org/cvss/v4.0/specification-document ; https://asvs.owasp.org/ .

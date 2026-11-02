# PLogIt senior engineering guardrails

**Boundaries:** PLogIt captures; JudgeIt judges; PatchIt remediates; GaugeIt aggregates. The standalone PLogIt repo is authoritative. Historical RES code copy is not a writable fork. Only approved focused PRs may change application code, security and deployment.

**Truth hierarchy:** Real tested source and infrastructure evidence → approved current suite decisions → current research/contract report → historical slice documents. A planning statement is not a deployed feature; green validation jobs are not live HTTPS proof.

**Critical invariant:** Current JSON exports APPROVED and optional prior EXPORTED logs, not raw all-states; `attachmentFilenames` gives no evidence bytes/hashes; leader export mutates review states and audit. Changing this requires privacy and backward-compatibility approval.

| Symptom | Inspect first | Required response |
|---|---|---|
| JudgeIt reports missing attachment content | JsonReportStrategy, contract fixtures | Expose legacy metadata-only caveat, do not fake media |
| Unexpected missing DRAFT logs | ExportService approval filter | Confirm intentional selection; product decision before change |
| Duplicate version numbers | LogVersionService + Flyway V3 | Concurrent write and uniqueness test |
| Cross-engagement access | MembershipService + controllers | Backend denial and no data mutation |
| HTML/script injection | MarkdownService and LogDetail | Test rendered output with malicious payload |
| Export marks logs wrongly | ExportService and AuditEventListener | Transaction/rollback and state-machine test |
| CI passed but deploy failed | Workflow host fingerprint preflight | Verify SSH host independently; never skip check |
| Prod revision stale | Server checkout and image digest | Verify target SHA, TLS, health and rollback |
| DB restore omits images | DB backup and attachment volume | Full isolated restore with media hash proof |

**Escalation format:** `[BLOCKED JR-___] symptom; canonical SHA; actual API/SQL/CI proof; expected spec; likely owning service; impact; smallest safe fix; user decision required; rollback; next independent task.` Do not record secrets or real pentest evidence in reports.

A developer can mark DONE only after current valid and negative execution evidence, reviewer sign-off and regression tests. Infrastructure-only tasks remain BLOCKED without authorized access, not inferred complete.

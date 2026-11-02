# RES — modernization and implementation roadmap

**As of 2026-11-02:** proposals and gates only. No changes to app code.

## Gate 0 — trustworthy baseline
- [x] Inventory and read RES plans and workspaces.
- [x] Read current PLogIt implementation at commit 28aa156 and relevant GitHub Actions run history.
- [x] Copy 185 PLogIt source files exactly into PlogIt Workspace/PLogIt and compare all source/destination Git blob hashes.
- [x] Publish a dated research refresh, handoff contract analysis, and independent PLogIt update recommendations.
- [ ] Resolve verified server SSH host-key fingerprint through trusted console, create correctly scoped PROD_SSH_HOST_FINGERPRINT secret, rerun deployment and verify public HTTPS and application health. Never bypass verification.
- [ ] Verify actual deployment topology, server path, backups and restore. Historical plans are not current infrastructure proof.
- [ ] Confirm product objective, real user roles, customer/tenant model, target regulatory frameworks, privacy/retention and hosting.

## Gate 1 — test the first integration boundary
- [ ] Obtain representative actual PLogIt JSON exports, including empty logs and attachment filenames.
- [ ] Generate JSON Schema and fixtures from the *current* exporter; test legacy behavior and state-change semantics.
- [ ] Decide whether/how to include attachments and non-approved logs, with a separate PLogIt code change only after approval.
- [ ] Finalize JudgeIt auth and tenant model, log-to-finding relationships, import deduplication, audit and evidence provenance.
- [ ] Approve JudgeIt finding lifecycle and CVSS v4.0 scoring representation.

## Gate 2 — build JudgeIt in vertical slices
1. Backend, database/Flyway and app health, then frontend shell, with a current, tested Java/Node/runtime baseline.
2. Auth and authorization, including cross-tenant isolation and CSRF/wire tests.
3. Strict versioned evidence pack import and immutable source evidence review.
4. Auditor finding promotion, multiple evidence links, severity + CVSS vectors + CVE/CWE references.
5. SLA rule engine, priority and manual overrides with audit.
6. Branded report generator and producer-owned remediation-tasks.json.
7. End-to-end PLogIt → JudgeIt fixture and actual import; deployment hardening and rollback.

## Gate 3 — build PatchIt
- Durable asset identity, asset ownership model and authorization.
- Versioned, idempotent remediation-task import; ticket per asset/finding policy.
- Kanban transitions, due-date clocks and real retry/escalation semantics.
- Independent retest evidence, verified resolution, reopened items, formal risk acceptance and expiry.
- Risk-over-time and posture-metrics.json schema with documented denominators.

## Gate 4 — build GaugeIt
- Validated posture snapshot import and duplicate/overlap detection.
- Trends with time windows and staleness banners, asset risk views with clear denominators.
- Reviewed mappings to applicable frameworks; no unsupported "compliant/certified" labels.
- Board-ready report with transparent data provenance, caveats and export freshness.

## Gates for every slice
- Written acceptance criteria and threat model, explicit sign-off and documented choices.
- Backend Maven verification with PostgreSQL/Testcontainers; frontend lint/build/tests; actual HTTP auth/CSRF tests for state-changing endpoints.
- Semgrep and Trivy; source/lockfile review; pinned actions/digests; minimal permissions.
- CI success and a passing PR are not proof of deployment. Track: documented, implemented, tested, merged, deployed and live-verified separately.
- Migration/rollback, audit/privacy, retention, backup restore testing, accessible interface and operational monitoring.
- Keep historic process logs, label outdated decisions and add dated records only; no rewriting history.

## Separate PLogIt roadmap
See PlogIt Workspace/PLogIt/docs/PLOGIT-RESEARCH-UPDATE-PLAN-2026-11.md. It describes potential changes without implementing them. The standalone Edison3060/PLogIt repository is not changed by any of this work.

Other references: Suite - Research Refresh 2026-11-02.md and Suite - Handoff Contracts and Evidence.md.

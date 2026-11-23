# PLogIt Session 10 — Phase 05B

Base: `f331623749db0165cb31146b8118373859e7fb34`, stacked on Phase 04 through [draft PR #26](https://github.com/Edison3060/PLogIt/pull/26). Synthetic fixtures only.

## JR-091

At base `f331623749db0165cb31146b8118373859e7fb34`, `LogService.create/update` persist the log and invoke `LogVersionService.snapshot` within the write transaction. The latter locks the log row, allocates the next ordinal, and saves an actor-stamped `LogSnapshot`. V3 enforces unique `(log_id, version_number)`. `LogEntry.@Version` and `saveAndFlush` provide optimistic locking during concurrent write transactions. `LogController` exposes member-scoped ascending history. Existing `LogVersioningIntegrationTest` covers two versions, unique constraints, concurrent snapshots and concurrent edits. `OptimisticLockingIntegrationTest` covers exception mapping. The mirrored product spec describes historical UUID and approved-edit policy differences recorded in DEC-2026-11-23-01. Expected inventory matched canonical source. Next JR-092 specifies the API and browser-staleness boundary.

## JR-092

Draft writes use authenticated `PUT /api/logs/{id}` with editable fields only, as defined by `LogUpdateRequest`. `GET /api/logs/{id}/history` returns ascending `versionNumber`, snapshot, editor and time. Two concurrent transactions loading the same JPA version should produce one durable update and one conflict, mapped to HTTP 409. A delayed browser save that reaches a new transaction after the winning save has committed supplies no expected version and can still succeed. A strict browser precondition would require an approved caller-compatibility decision. Preserve existing authorization and wire shape. Next JR-093 verifies the successful persistence fixture.

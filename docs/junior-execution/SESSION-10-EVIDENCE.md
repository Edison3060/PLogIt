# PLogIt Session 10 — Phase 05B

Base: `f331623749db0165cb31146b8118373859e7fb34`, stacked on Phase 04 through [draft PR #26](https://github.com/Edison3060/PLogIt/pull/26). Synthetic fixtures only.

## JR-091

At base `f331623749db0165cb31146b8118373859e7fb34`, `LogService.create/update` persist the log and invoke `LogVersionService.snapshot` within the write transaction. The latter locks the log row, allocates the next ordinal, and saves an actor-stamped `LogSnapshot`. V3 enforces unique `(log_id, version_number)`. `LogEntry.@Version` and `saveAndFlush` provide optimistic locking during concurrent write transactions. `LogController` exposes member-scoped ascending history. Existing `LogVersioningIntegrationTest` covers two versions, unique constraints, concurrent snapshots and concurrent edits. `OptimisticLockingIntegrationTest` covers exception mapping. The mirrored product spec describes historical UUID and approved-edit policy differences recorded in DEC-2026-11-23-01. Expected inventory matched canonical source. Next JR-092 specifies the API and browser-staleness boundary.

## JR-092

Draft writes use authenticated `PUT /api/logs/{id}` with editable fields only, as defined by `LogUpdateRequest`. `GET /api/logs/{id}/history` returns ascending `versionNumber`, snapshot, editor and time. Two concurrent transactions loading the same JPA version should produce one durable update and one conflict, mapped to HTTP 409. A delayed browser save that reaches a new transaction after the winning save has committed supplies no expected version and can still succeed. A strict browser precondition would require an approved caller-compatibility decision. Preserve existing authorization and wire shape. Next JR-093 verifies the successful persistence fixture.

## JR-093

In [push CI 45098552729](https://github.com/Edison3060/PLogIt/actions/runs/45098552729) at `f331623749db0165cb31146b8118373859e7fb34`, `mvn -B clean verify` ran all ten `LogVersioningIntegrationTest` cases on PostgreSQL 16.16. `authorCanCreateAndEditDraftWithStableIdentityAndOrderedSnapshots` creates an authenticated synthetic draft with Markdown and tags, updates it, GETs history and reads persisted snapshots. Expected and observed sequence `[1, 2]`, stable actor/log identity, and distinct original and edited content. V3 and the separate duplicate-insert test protect unique ordinals. Next JR-094 verifies races and the stale-client limitation.

## JR-094

In [push CI 45098552729](https://github.com/Edison3060/PLogIt/actions/runs/45098552729) at `f331623749db0165cb31146b8118373859e7fb34`, `concurrentEditsOnlySnapshotCommittedChanges` starts two PostgreSQL transactions that preload the same draft and release together. Exactly one optimistic lock failure, ordered versions `[1, 2]`, and the second snapshot matching the durable title passed. Parallel snapshots produce `[1, 2, 3]`. A duplicate insert fails and leaves one snapshot. Nonmembers cannot read history. `OptimisticLockingIntegrationTest` (2/2) verifies HTTP 409 mapping separately from the service-level race. The update DTO and client send no revision precondition, so a stale screen saving after the winner commits is outside that guarantee. JR-094 stays in review for strict client-side staleness acceptance; see the decision ledger. Next JR-095 records closure status.

## JR-095

JR-093's ordered success and JR-094's negative checks passed in [push CI 45098552729](https://github.com/Edison3060/PLogIt/actions/runs/45098552729) at `f331623749db0165cb31146b8118373859e7fb34`, with 150 total backend tests, zero failures/errors/skips. Builder Lead reviewed both fixtures and the safe HTTP error mapping. Leave full stale-client closure in review under DEC-2026-11-23-03. No production or compatibility behavior changed, so there is no application rollback. Next JR-096 inventories audit and board ownership.

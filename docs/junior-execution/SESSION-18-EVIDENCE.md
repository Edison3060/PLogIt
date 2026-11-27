# Session 18 — Database integrity and recoverability

Base: `927085c4f3ed7def4f55eaae351b2add2abe66d8`. Reviewer: Builder Lead. Phase 09 [draft PR #30](https://github.com/Edison3060/PLogIt/pull/30) stacks on Phase 08 [PR #29](https://github.com/Edison3060/PLogIt/pull/29).

## JR-171

Reviewed base `927085c4f3ed7def4f55eaae351b2add2abe66d8`. Compose mounts `pgdata` at PostgreSQL and `attachments` at the backend. Flyway owns schema and AttachmentService stores relative paths in PostgreSQL. Production Compose retains both volumes. Existing `scripts/ci-compose-smoke.sh` restarts PostgreSQL and re-reads one row. README documents liveness and release operations. No canonical paired backup, restore or pre-promotion verification was identified. Deployment operator owns backup and restore. Inventory only.

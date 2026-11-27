# Session 17 evidence

Phase 09 stacks on Phase 08 at `01dc9925a1f55924e242a888a592224cad956737` while [PR #29](https://github.com/Edison3060/PLogIt/pull/29) remains draft. The scope is JR-161–JR-170. All data is synthetic.

## JR-161

At Phase 08 head `01dc9925a1f55924e242a888a592224cad956737`, Flyway V1 owns foreign keys from memberships to users and engagements, logs to users and engagements, and snapshots to logs and editors. V1 uniquely indexes each membership pair. V2 adds `version` for Hibernate `@Version` on logs and engagements. V3 uniquely constrains the snapshot pair `(log_id, version_number)`. `Engagement`, `EngagementMember` and `LogEntry` map domain enum names to strings. Hibernate validates the Flyway schema and the reviewed migrations define these fields as `VARCHAR`. Existing coverage is in `FlywayMigrationLifecycleTest`, `LogVersioningIntegrationTest`, `OptimisticLockingIntegrationTest`, and `TeamWorkflowIntegrationTest`. The product specification supplies the desired behavior. The canonical migrations and live JPA mappings own current behavior. Expected inspection found the FK and uniqueness declarations and an enum enforcement gap at the storage layer. No runtime test or code change at this inventory checkpoint. Reviewer is the Builder Lead.

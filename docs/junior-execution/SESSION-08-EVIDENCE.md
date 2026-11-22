# Session 08 — scope/RoE and leadership/team verification

Branch `work/plogit-phase-04`, source baseline e3e2f5ae8132df4988c615490f3676ec7fc739d9. Controller claim c3a72d9bb3df60354eb3a1f3a7c9228fcaaa6a00. Exactly JR-071–080, no subagents. Test-only approach; preserve application, auth, audit and JSON semantics.

## JR-071
Status at checkpoint: DONE. Inventory Scope and rules of engagement.
- Inventory: ScopePanel.tsx renders saved in-scope targets and all five RoE fields; editing lives in pages/EngagementDetail.tsx (startEdit/handleSave), not ScopePanel. DTOs UpdateEngagementRequest/EngagementDetail and controller PUT/GET /api/engagements/{id} own wire data. EngagementService.updateAndBuildDetail asserts leader, updates non-null fields transactionally, emits changedFields-only UPDATE_ENGAGEMENT audit, and returns server-owned role. Dates are LocalDate; null update dates currently mean unchanged. Update DTO caps hours at 1000 and RoE strings at 4000 but has no target element/list bounds or syntax validation. Do not silently invent a target grammar or date-clearing policy.
- Existing tests: EngagementAuthzIntegrationTest activeMemberCanReadAndLeaderCanUpdateSameEngagement and crossEngagementWritesAndRemovedMembershipAreDeniedWithoutMutation cover description/permissions, AuditLogIntegrationTest engagement update metadata, EngagementPanels.test.tsx renders saved rules. Missing full scope/date persistence and invalid-target denial evidence. Canonical source baseline e3e2f5ae8132, planning copy reference-only. Expected owner/source map found, implemented with validation gap; VERIFIED NO APPLICATION CHANGE. Executed inspection only, no runtime test pass claimed. Next specify acceptance and isolate regressions via Java21/PostgreSQL16 CI.


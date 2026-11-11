# PLogIt Session 07 evidence, JR-061–JR-070

Controller claim gpt6-session07-2026-11-11T19:22:26.929Z on control/session-state. Phase04 branch based on Phase03 ff2e2ba0f3c1171363f12bdb9f6a362f83eacdca, unmerged prior PRs #22/#23/#24. Original Phase04(A) exact scope is engagement create/list/detail and join codes/membership. Verify existing code before changing; no production deployment. Test commands for CI: backend `mvn -B clean verify`; frontend `npm ci`, lint, build, `npm test`; Compose live HTTP, Semgrep, Trivy, proxy. Local Maven/Docker/Postgres unavailable, CI outcomes must be inspected separately.

### JR-061
- Status DONE as of 2026-11-11T19:27:44.987Z; reviewer Builder Lead.
- Source inventory at Phase03 ff2e2ba0: EngagementController POST/GET; EngagementService.create saves ACTIVE engagement then creator LEADER membership in one transaction; list filters removed membership; detail requires active member, outsider gets 404. DTO CreateEngagementRequest validates nonblank name (max255), description (max4000); EngagementSummary and EngagementDetail map role server-side. Existing EngagementAuthzIntegrationTest covers member/outsider detail but not full create-list-detail serialized response. Canonical source inspected, not archived planning copy. No application code change or runtime execution. Reviewer Builder Lead.
- Actual validation: source/doc inspection only, no runtime tests; no source modification. JR commit in branch history.

### JR-062
- Status DONE as of 2026-11-11T19:27:53.576Z; reviewer Builder Lead.
- Expected POST /api/engagements with name, description and optional dates yields 200 ACTIVE, LEADER role and exactly one creator membership; GET collection only active-member summaries, GET /{id} authorized detail/roster. Invalid body 400, missing CSRF 403, unauthenticated 403, outsider detail 404 and list excludes foreign record, all with no mutations. Product spec explicitly allows independently created engagements; no unique name or idempotency key. Therefore do not silently reject duplicate names although original JR-064 ambiguous duplicate-create asks for denial; flag that acceptance gap IN REVIEW pending separate product decision. Recommend focused test-only regression, DEC-2026-11-11-04. Reviewer Builder Lead.
- Actual validation: source/doc inspection only, no runtime tests; no source modification. JR commit in branch history.

### JR-063
- **Status:** IN REVIEW pending CI, 2026-11-11T19:29:21.475Z. **Test fixture:** `backend/src/test/java/io/muzoo/ssc/plogit/EngagementLifecycleIntegrationTest.java` / `creatorCanCreateListAndFetchSerializedEngagement`.
- **Expected/actual:** Auth owner POST valid engagement, GET only own list and detail, serialized name/status/dates/LEADER/roster and persisted one engagement/one membership. Synthetic fixture in EngagementLifecycleIntegrationTest.java. Test source authored, runtime PENDING.
- **Command:** `cd backend && mvn -B clean verify` (NOT EXECUTED at commit). No local Maven/Docker; CI later. **Reviewer:** Builder Lead.

### JR-064
- **Status:** IN REVIEW pending CI, 2026-11-11T19:29:30.949Z. **Test fixture:** `backend/src/test/java/io/muzoo/ssc/plogit/EngagementLifecycleIntegrationTest.java` / `invalidCreateAndUnauthorizedReadLeaveRecordsUnchanged`.
- **Expected/actual:** Synthetic outsider GET list empty and GET detail 404; malformed/missing required create 400, missing CSRF and anonymous creation 403, no engagement/member/audit mutation. Duplicate valid engagement names are allowed by current product spec; roadmap duplicate-create rejection remains unverified, DEC-2026-11-11-04. Test source authored, runtime PENDING.
- **Command:** `cd backend && mvn -B clean verify` (NOT EXECUTED at commit). No local Maven/Docker; CI later. **Reviewer:** Builder Lead.

### JR-065
- **Status:** IN REVIEW (2026-11-11T19:30:34.415Z). **Expected/actual:** Compared JR-063 positive serialization/persisted-role fixture with JR-064 negative outsider, validation, CSRF and no-mutation fixture, both in EngagementLifecycleIntegrationTest.java. Actual Maven/PostgreSQL CI result not yet inspected. Source owner and product semantics unchanged; original duplicate valid create rejection ambiguous because names are intentionally nonunique (DEC-2026-11-11-04). Therefore not DONE; close only when executed tests pass and duplicate acceptance resolved. Reviewer Builder Lead.
- **Tests:** Inspection only for this JR; actual CI pending at checkpoint; no claim of a runtime pass. Reviewer Builder Lead.

### JR-066
- **Status:** DONE (2026-11-11T19:30:42.805Z). **Expected/actual:** Inspected JoinController POST /api/engagements/{id}/join-code and POST /api/join, JoinCodeService generateFor/resolveCode, EngagementService.joinByCode, MembershipService and EngagementMember unique (engagement_id,user_id). Leader-only generation guarded server-side; regenerated prior code revoked; code redeems MEMBER role, existing active membership conflicts 409, soft-removed membership reactivated without duplicate row; DB unique constraint persists. JoinCode has generatedAt/revokedAt, no expiresAt; product spec explicitly defers expiry UI and ships non-expiring v1 codes. Existing EngagementAuthzIntegrationTest.removedMemberCanRejoinWithoutDuplicateMembership, AuditLogIntegrationTest and endpoint security tests. No source change/runtime executed. Reviewer Builder Lead.
- **Tests:** Inspection only for this JR; actual CI pending at checkpoint; no claim of a runtime pass. Reviewer Builder Lead.

### JR-067
- **Status:** DONE, 2026-11-11T19:31:29.369Z; reviewer Builder Lead.
- **Expected/actual:** Original JR-067 acceptance: leader with CSRF POST /api/engagements/{id}/join-code generates 200 {code,engagementId}, active nonleader 403, outsider 404. Authenticated POST /api/join {code} creates exactly one MEMBER role and roster/list record; regeneration revokes prior; bad/revoked 400 no mutation; existing active member replay 409 no duplicate; removed member rejoin preserves row. Existing engagement_user DB unique constraint protects duplicate persistence, but true concurrent race HTTP response still needs proof. PLogIt product spec Section 3 says v1 join codes do not expire, optional expiration postponed; no expiry field in JoinCode. Recommend tests of supported behavior without introducing unapproved expiration; original JR-069 expired-code criterion remains IN REVIEW pending product scope approval. Decision DEC-2026-11-11-05.
- **Runtime:** no tests executed here. Next test fixtures JR-068/069, original expired-case deferred per product spec.

### JR-068
- **Status:** IN REVIEW (2026-11-11T19:32:38.898Z). Fixture: `JoinCodeWorkflowIntegrationTest.generatedCodeCreatesExactlyOneMemberWithCorrectRole`.
- **Expected/actual:** Auth leader with CSRF generates code, authentic second account redeems as role MEMBER, verifies single new membership with joinedVia=code, own engagement list and leader roster; isolated Testcontainers-backed MockMvc test authored, expected API shape and persisted count. Runtime NOT EXECUTED until GitHub CI; no application source change.
- **Runtime:** `cd backend && mvn -B clean verify` not yet run. **Reviewer:** Builder Lead.

### JR-069
- **Status:** IN REVIEW 2026-11-11T19:33:43.644Z. Test `JoinCodeWorkflowIntegrationTest.revokedUnknownAndReplayedCodesCannotAddMembershipOrAudit`.
- **Expected/actual:** Auth leader regenerated code; old code revoked and rejected 400, unknown 400, missing CSRF 403, active member code generation 403, outsider 404, current code join 200 MEMBER and same-account replay 409 with unchanged membership count/audit. Test code written in JoinCodeWorkflowIntegrationTest.revokedUnknownAndReplayedCodesCannotAddMembershipOrAudit, PostgreSQL16 CI not yet executed. Product v1 explicitly non-expiring so expired-code denial untestable by design; real concurrent race response also not yet proved. DEC-2026-11-11-05. No service/schema/export change.
- **Runtime:** Not yet executed; pending `cd backend && mvn -B clean verify` (CI). Concurrent race and expiry separate acceptance debt; no false pass. Reviewer Builder Lead.

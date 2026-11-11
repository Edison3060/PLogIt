# Session 06 — JR-051–JR-060 evidence

- Controller run: gpt6-session06-2026-11-11T12:32:30.670Z, claimed on `control/session-state` at `d2cbba5344906b1e61fd4785428caa55bb9e3162`.
- Target: original Phase03(B), `work/plogit-phase-03` stacked on unmerged Phase02; starting source and tests `c173e31e7fb9a6c6bbd3bebf2f9cf436139f8701`.
- Scope: engagement authorization and auth-abuse controls only; preserve existing unversioned export, audit/history, privacy, session+CSRF and production main/CD gates.
- Evidence labels: **source inspected** is not **test executed**; new test code awaiting GitHub Actions is **IN REVIEW**, not DONE.

## JR-051
- Canonical owner: backend `service/{MembershipService,EngagementService}.java`, `web/EngagementController.java` and `EngagementAuthzIntegrationTest.java` at `c173e31e7fb9a6c6bbd3bebf2f9cf436139f8701`; current application code, not planning copy.
- Active removed members excluded by `existsByEngagementAndUserAndRemovedAtIsNull` / `findByEngagementAndUserAndRemovedAtIsNull`. `assertMember` obscures engagement existence with 404; `assertLeader` distinguishes active nonleader via 403.
- Test map: member GET 200; outsider 404; removed member GET/log/review denied; leader transfer 204 and nonleader transfer 403; no explicit multi-engagement write/no-mutation combined assertions or leader PUT positive. Verified source only, no executable tests in JR-051. Reviewer Builder Lead. DONE inventory.

## JR-052
- DEC-2026-11-11-01 acceptance: authenticated GET details by active member; leader-only PUT `{"description":"Updated under leader approval"}` with CSRF; outsider/removed 404, active member leader-action 403; persisted description and audit unchanged on denial. Original roles scoped to distinct engagement records. No source modification, no tests executed; DONE specification.

## JR-053
- New test: `EngagementAuthzIntegrationTest.activeMemberCanReadAndLeaderCanUpdateSameEngagement`; synthetic leader/member records and isolated test DB; GET /api/engagements/{id} as member expects 200/MEMBER, PUT as leader with CSRF `{"description":"Updated under leader approval"}` expects 200/LEADER and matching database field. Auth/session/CSRF unchanged. Test written, execution PENDING; IN REVIEW.

## JR-054
- `crossEngagementWritesAndRemovedMembershipAreDeniedWithoutMutation`: first engagement leader is outsider to second engagement; GET/PUT there 404; own active MEMBER PUT 403; removed MEMBER own GET/PUT 404; descriptions unchanged, audit row count identical. All PUTs include CSRF to ensure the authorization layer is under test. Corrected JR-053 new JSON string literal escaping here. Test authored; CI pending. IN REVIEW.

## JR-055
- Closeout pending real execution: JR-053/054 test sources cover positive/negative membership boundary, persisted field and no-audit-mutation claims. No local Maven, Docker or PostgreSQL run yet; mark IN REVIEW until current-head GitHub CI executes and matches assertions. No unverified bug fix or release claim.

## JR-056
- Auth owner: `AuthRateLimitInterceptor.java`, `AuthRateLimitConfig.java`, `AuthController.java`, `AuthRateLimitIntegrationTest.java` on canonical Phase03. 20 login attempts/60s per remote IP (configurable), 5 registration attempts/600s per IP; in-memory 10,000 key ceiling; rejects X-Forwarded-For spoof. Generic 401/429 and duplicate-registration 202 already covered in existing tests.
- Gap: no per-account key; same-IP unrelated client can be throttled by small bursts; targeted distributed attacks evade the single IP bucket. Inventory complete without code modifications. DONE source map, tests not executed.

## JR-057
- DEC-2026-11-11-02 acceptance: normal login throughput of two users sharing remote IP with valid CSRF; user key normalized+SHA-256 (no raw email retained in counters) limited to 3/60s in test (20/60s default), independent coarse remote-IP key at 30/60s in test (200/60s default), registration 2/600s test (5/600s default). Invalid burst → generic 429/Retry-After; unknown/wrong password generic 401, with no username echo or mutation. Normal other user on same IP must remain authenticated; spoofed XFF not accepted as IP proof. Process-local counters need distributed deployment review. Source spec only, tests pending.

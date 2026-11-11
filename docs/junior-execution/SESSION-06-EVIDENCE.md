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

## JR-058
- Implementation `AuthRateLimitInterceptor.checkLoginAccount(email)` normalizes email to lowercase, SHA-256 hash for map key, enforces configured account quota across IPs. Existing servlet interceptor enforces coarse **socket-IP 10× configured login quota**, registration untouched; 429 generic. AuthController calls account check after valid request body before authentication/session mutation. Synthetic normal shared-IP users login with correct password and CSRF, expected 200 both, user count unchanged. No local run; CI PENDING, IN REVIEW.

## JR-059
- `distributedBurstAgainstOneAccountDoesNotBlockUnrelatedSharedIpUser`: three POST /api/auth/login from 192.0.2.30/31/32 with wrong password for same seeded email → expected generic 401; 4th from .33 with spoofed forwarded IP and valid password → expected generic 429/Retry-After, no address in response; different seeded user authenticates from .30 → expected 200, DB user count remains 2. All requests include genuine MockMvc CSRF support; no browser/UI substitute for server auth. Test written; CI PENDING, IN REVIEW.

## JR-060
- Security review caught and corrected the JR-058 proposed IP quota expansion: **final implementation retains existing IP limit (default 20/60s, test 20), adds independent normalized SHA-256 account bucket (default 5/60s, test 3), leaves registration IP limit untouched (default 5/600s, test 2)**. DEC-2026-11-11-03 documents supersession of earlier 10× proposal; source commit JR-060 contains correction.
- Positive/negative tests JR-058/059 cover shared IP unrelated account, distributed targeted failures and generic 429/Retry-After/no user mutation; original auth/registration and CSRF tests must still pass. At this checkpoint no current-head CI executed; **IN REVIEW**, no merge or release.

## Session 06 final CI reconciliation — 2026-11-11T12:51:34.102Z
- **Verified real execution:** [CI 43023154965](https://github.com/Edison3060/PLogIt/actions/runs/43023154965) `work/plogit-phase-03@9a197a39eb6c0945485f9f0b4c1192aefab9bb3c` COMPLETED SUCCESS: Java21 Maven PostgreSQL16 Testcontainers **137 tests, 0 failures/errors/skips**; frontend Node22 lint/build and **18/18 Vitest**; real HTTP Compose auth/CSRF/old-cookie replay and DB/persistence/health negative checks; Semgrep, Trivy filesystem+backend/frontend images, proxy ALL SUCCESS; production CD SKIPPED. New exact JR-053/054/058/059 test methods exercised and persisted DB/auth response assertions passed.
- Previous run `943588c663d2` 43022858453 backend 137 PASS but Compose/Trivy CANCELLED; that is *not* the current full CI result. Earlier new positive fixture's escaped string corrected in JR-054, preserved in STEP-LOG. No local tests claimed.
- **Final JR statuses:** JR-051,052,053,054,055,056,057,058,059,060 **DONE**. JR-060 review superseded initial 10× IP proposal with existing unchanged IP20+new account5, synthetic test account3; DEC-2026-11-11-03. Original source/JSON/privacy/role/audit unchanged aside from scoped auth abuse limiter and tests.
- **Release/external debt:** JR-044/045 elapsed idle-time expiry IN REVIEW; historical Phase02 JR040 scanner phase acceptance separate; draft PR #22/#23/#24 unmerged; direct-API auth CSRF client compatibility owner review; DEC-2026-11-06-11 main production CD owner approval pending; INT-001 real JudgeIt receiver/integration provisional, no production deploy. Safe next Session07 JR061–070 only after control pointer saved; READY WITH PROVISIONAL INTEGRATION.

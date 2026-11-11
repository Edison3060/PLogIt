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

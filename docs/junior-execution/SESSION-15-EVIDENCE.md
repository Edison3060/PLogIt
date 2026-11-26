# Session 15 evidence

Phase 08 stacks on Phase 07 `7727c1a82d42794e9955ae3baae0cc7475361526` while PR #28 remains draft. Phase 07 acceptance is pending and the controller authorized independent frontend assessment. Builder Lead reviewed directly.

## JR-141

Prior status NOT STARTED. Expected a canonical route and test map. At `7727c1a82d42794e9955ae3baae0cc7475361526`, `App.tsx` mounts the sign-in and registration routes outside `ProtectedRoute`. `ProtectedRoute.tsx`, `hooks/useAuth.ts` and `lib/auth.ts` own the session probe. `AppShell.tsx` owns dashboard, engagement and log navigation and logout. `AuthController` and server security own authorization. `ProtectedRoute.test.tsx` covers pending/valid/invalid mocked sessions and `AuthenticationSessionLifecycleIntegrationTest` owns server lifecycle coverage. Product Spec is reference for intended membership and UX, not live routing. `npm run lint && npm run build && npm run test -- --run` passed, 21 frontend tests. Browser and elapsed expiration are reserved for JR-143/144. No application code changed.

## JR-142

Prior status NOT STARTED. `GET /api/auth/me` returns current `{id,email,displayName}` for a valid servlet session and denies a missing/expired session. Loading must conceal protected content, denied probes must replace the route with `/login`, and `POST /api/auth/logout` uses the existing CSRF header and invalidates the session. Verify dashboard, engagement and logs at two widths and deny replay after elapsed idle time. `AppShell` shifts the mobile sidebar off-screen and the toggle has no accessible name. The existing UI redirects on a failed session probe. Existing navigation does not preserve a protected deep link after login. No security or redirect contract change needed. Reviewed by Builder Lead.

## JR-143

Prior status NOT STARTED. Expected signed-in dashboard → engagement → Activity Logs at desktop and mobile widths, with usable keyboard navigation. Chromium 158.0.8127.0 with real Spring Boot and PostgreSQL 16.16, fixture `S15-1795659260305`, passed at 1280×800 and 390×844. Enter activated the dashboard card and Activity Logs. The mobile toggle exposed its expanded state, Enter and Space opened it, the closed sidebar left the tab order, focus moved inside on opening, and Escape closed it and restored toggle focus. Both `POST /api/auth/logout` returned 200, replayed session IDs returned 403 and the sign-in page loaded. `AppShell.tsx` adds named controls, responsive visibility and focus management. The scoped frontend change can be reverted independently. `npm run lint && npm run build && npm test` passed (24 tests). W3C WAI disclosure pattern and navigation example accessed 2026-11-26 informed the keyboard behavior. https://www.w3.org/WAI/ARIA/apg/patterns/disclosure/ and https://www.w3.org/WAI/ARIA/apg/patterns/disclosure/examples/disclosure-navigation/.

## JR-144

Prior status NOT STARTED. Juxtaposed the existing revoked-cookie replay (403) and real elapsed-session reload. Chromium 158.0.8127.0 against the isolated app, fixture `S15-1795659260293`, retained the original browser cookie after 65 seconds, but `/api/auth/me` returned 200 on initial reload where 403 was expected. This run did not establish server-side expiry. Investigate the effective Tomcat session window and rerun. No session, authorization, or cookie code changed.

## JR-145

Prior status NOT STARTED. Desktop and mobile navigation, real logout, replay denial, and keyboard disclosure passed at JR-143. `AppShell.tsx` is independently reversible using commit `5bfc42d9045285d01ae91cf419d0ed7011a0d05f`. `npm run lint && npm run build && npm test` passed (24 tests) with the local Session 15 changes. Elapsed expiry is pending the JR-144 rerun. Builder Lead reviewed. Track the inherited expiry decision separately from Phase 08 acceptance.

## JR-146

Prior status NOT STARTED. At canonical source baseline `7727c1a82d42794e9955ae3baae0cc7475361526`, `Dashboard.tsx` and `EngagementsPage.tsx` use `useEngagements`, `useCreateEngagement` and `useJoinEngagement`. `EngagementDetail.tsx` owns role-aware page state and passes response data to Overview, Scope, Team and Audit. `EngagementController`, `JoinController`, `EngagementService`, `JoinCodeService` and `MembershipService` own the real writes and permissions. Existing `EngagementPanels.test.tsx`, `EngagementTeamWorkflow.test.tsx`, `EngagementAuthzIntegrationTest` and `EngagementLifecycleIntegrationTest` cover component delegation, canonical role refresh and server lifecycle. Product Spec §6 describes planned journeys; the verified UI places create/join on Manage Engagements. Baseline browser proved the workflow is implemented. No application code changed in this checkpoint.

## JR-147

Prior status NOT STARTED. Expected empty list only after successful `GET /api/engagements` returning `[]`. `POST /api/engagements` accepts `{name,description}` and identifies creator role LEADER. Leader issues a code via `POST /api/engagements/:id/join-code`, and `POST /api/join` accepts `{code}` and establishes MEMBER. `GET /api/engagements/:id` provides scope and active member roster to members. The leader-only Audit tab uses its own endpoint, and an outsider receives 404. At baseline a failed list/audit read fell through to empty display, and visual denials lacked an alert role. Builder Lead authorized scoped frontend presentation changes. Server authorization, evidence and data contracts are retained.

## JR-148

Prior status NOT STARTED. Chromium 158 against real Spring Boot/PostgreSQL at 390×844. Baseline fixture `S15-1795659331643` and corrected fixture `S15-1795659331601` passed. Browser created the engagement (200, LEADER), updated `scope.example.test` (200), issued the join code (200), redeemed as a second user (200, MEMBER), and reloaded. Scope still showed the target and Team showed both users. Edit and Audit controls were absent for the member. All writes used the normal CSRF-protected HTTP handlers. No application code changed in this checkpoint. The repeatable harness is included in the JR-149 checkpoint.

## JR-149

Prior status NOT STARTED. On Chromium 158 against Spring Boot/PostgreSQL 16.16, baseline `S15-1795659331643` returned outsider detail 404 and invalid join 400, but feedback lacked an alert role. Corrected `S15-1795659331601` returned the same HTTP results and exposed both errors by role. No title or roster appeared for the outsider, join success stayed absent, outsider list stayed `[]`, and the leader's audit, two-member roster and scope target were unchanged. Added explicit list and Audit failures, persistent join/create alert, status feedback and selected view state. Four focused RTL/Vitest regressions cover denied list, invalid join and failed audit. `npm run lint && npm run build && npm test` passed with 25 tests, `git diff --check` and Node syntax check passed. `scripts/verify-frontend-journeys.cjs` reproduces the isolated real HTTP/browser journey. W3C WAI ARIA19 informed the persistent live region. A screen reader was not exercised. Draft PR #29 is the review and rollback boundary.

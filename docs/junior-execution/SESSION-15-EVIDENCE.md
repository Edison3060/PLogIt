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

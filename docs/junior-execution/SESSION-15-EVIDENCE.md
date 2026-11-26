# Session 15 evidence

Phase 08 stacks on Phase 07 `7727c1a82d42794e9955ae3baae0cc7475361526` while PR #28 remains draft. Phase 07 acceptance is pending and the controller authorized independent frontend assessment. Builder Lead reviewed directly.

## JR-141

Prior status NOT STARTED. Expected a canonical route and test map. At `7727c1a82d42794e9955ae3baae0cc7475361526`, `App.tsx` mounts the sign-in and registration routes outside `ProtectedRoute`. `ProtectedRoute.tsx`, `hooks/useAuth.ts` and `lib/auth.ts` own the session probe. `AppShell.tsx` owns dashboard, engagement and log navigation and logout. `AuthController` and server security own authorization. `ProtectedRoute.test.tsx` covers pending/valid/invalid mocked sessions and `AuthenticationSessionLifecycleIntegrationTest` owns server lifecycle coverage. Product Spec is reference for intended membership and UX, not live routing. `npm run lint && npm run build && npm run test -- --run` passed, 21 frontend tests. Browser and elapsed expiration are reserved for JR-143/144. No application code changed.

## JR-142

Prior status NOT STARTED. `GET /api/auth/me` returns current `{id,email,displayName}` for a valid servlet session and denies a missing/expired session. Loading must conceal protected content, denied probes must replace the route with `/login`, and `POST /api/auth/logout` uses the existing CSRF header and invalidates the session. Verify dashboard, engagement and logs at two widths and deny replay after elapsed idle time. `AppShell` shifts the mobile sidebar off-screen and the toggle has no accessible name. The existing UI redirects on a failed session probe. Existing navigation does not preserve a protected deep link after login. No security or redirect contract change needed. Reviewed by Builder Lead.

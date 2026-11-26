# Session 15 evidence

Phase 08 stacks on Phase 07 `7727c1a82d42794e9955ae3baae0cc7475361526` while PR #28 remains draft. Phase 07 acceptance is pending and the controller authorized independent frontend assessment. Builder Lead reviewed directly.

## JR-141

Prior status NOT STARTED. Expected a canonical route and test map. At `7727c1a82d42794e9955ae3baae0cc7475361526`, `App.tsx` mounts the sign-in and registration routes outside `ProtectedRoute`. `ProtectedRoute.tsx`, `hooks/useAuth.ts` and `lib/auth.ts` own the session probe. `AppShell.tsx` owns dashboard, engagement and log navigation and logout. `AuthController` and server security own authorization. `ProtectedRoute.test.tsx` covers pending/valid/invalid mocked sessions and `AuthenticationSessionLifecycleIntegrationTest` owns server lifecycle coverage. Product Spec is reference for intended membership and UX, not live routing. `npm run lint && npm run build && npm run test -- --run` passed, 21 frontend tests. Browser and elapsed expiration are reserved for JR-143/144. No application code changed.

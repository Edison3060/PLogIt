# PLogIt - Build Plan (Slice Roadmap)

> **2026-11-02 REBASELINE:** This document preserves July context. PLogIt source is imported unchanged at main commit 28aa156; actual Spring Boot 4.1.1 (not 3.5.16), Java 21, React 18/Vite 6 and PostgreSQL 16. The latest checked main CI passed its five validation jobs but CD failed the PROD_SSH_HOST_FINGERPRINT check. Deployed state of this commit is NOT verified. Proposed code changes belong ONLY in the new Markdown plan; source code is frozen in this research pass. See [suite research](../../Suite%20-%20Research%20Refresh%202026-11-02.md), [implementation roadmap](../../Suite%20-%20Modernization%20Roadmap.md), and [PLogIt proposed update plan](../PLogIt/docs/PLOGIT-RESEARCH-UPDATE-PLAN-2026-11.md).

> **Purpose:** The slice-by-slice roadmap. All slices are complete. Kept as a historical record of how the project was built.
> **Status legend:** done / in progress / pending / deferred
> **Project type:** Personal project (originally a university course deliverable with a 3-day deadline, the course framing is retired, and the deadline language is historical).
> **Related:** [`PLogIt - Product Spec.md`](./PLogIt%20-%20Product%20Spec.md) (the bible), [`PLogIt - Process Log.md`](./PLogIt%20-%20Process%20Log.md) (per-slice decisions).

---

## At-a-glance

| # | Slice | Status | Depends on | Day |
|---|-------|--------|------------|-----|
| 0 | Repo scaffolding | done | - | 1 |
| 1 | Spring Boot skeleton + entities + Flyway | done | 0 | 1 |
| 2 | React + Vite skeleton + theme tokens | done | 0 | 1 |
| 3 | Auth (Spring Security) | done | 1, 2 | 1 |
| 4 | Engagements + membership + join codes | done | 3 | 2 |
| 5 | Engagement board | done | 4 | 2 |
| 6 | Logs CRUD + log board + log-add form | done | 5 | 2 |
| 6.5 | Test infra + retrospective tests | done | 6 | 2 |
| 7 | Review state machine | done | 6 | 2 |
| 8 | Versioning + audit log (Observer) | done | 7 | 3 |
| 9 | Markdown + code blocks + attachments | done | 7 | 3 |
| 10 | PDF + JSON/CSV export (Factory + Strategy) | done | 9 | 3 |
| UI | Design overhaul (sidebar+topbar, HW4 palette, page reworks) | done | 10 | 3 |
| UI3 | Sidebar tabs + EngagementsPage + Settings | done | UI | 3 |
| Docs | Professional README rewrite | done | UI3 | 3 |
| UIFix | Badge wrap fix (whitespace-nowrap) | done | UI | 3 |
| 11 | Deploy + server hardening | done | 10 | 3 |

**Likely deferred (Option B honesty):** secrets detection, scope warning modal, the full filter set (only 2 or 3 ship), group-by-target, demo data, objectives list, polish, leader transfer, and final archive sign-off.

---

## Slice 0: Repo scaffolding (done)

**Scope:** Create the monorepo skeleton, with no application code.

**Files:**
- `.gitignore`, `.gitattributes`, `.editorconfig`
- `.env.example` (documented placeholders, no real secrets)
- `docker-compose.yml` (dev, profile trick)
- `docker-compose.prod.yml` (prod, adds Caddy)
- `Caddyfile` (reverse proxy and auto-TLS template)
- `.github/workflows/ci.yml` (Semgrep and Trivy blocking, file-existence guards)
- `README.md` (product-facing)
- Empty `backend/`, `frontend/`, `docs/` dirs

**Done:** 3 CI fix rounds (hashFiles bug, trivy-action compromise, mutable tag pinning). All green. The lesson is that the scanner taught us the lesson and then enforced it on us.

---

## Slice 1: Spring Boot skeleton + entities + Flyway (done)

**Scope:** Spring Boot boots, connects to Postgres, Flyway runs migrations, and `/api/health` works. All domain entities are defined.

**What gets built:**
- `pom.xml` (Spring Boot 3.5.16, JPA, Security, Flyway, Validation, Lombok)
- `application.yml` and dev/prod profiles
- 9 JPA entities (User, Engagement, EngagementMember, JoinCode, LogEntry, LogVersion, Attachment, ScopeWarning, AuditLog)
- 5 enums (EngagementStatus, EngagementRole, ActivityType, Outcome, ReviewState)
- 9 Spring Data JPA repositories
- `V1__init_schema.sql` Flyway baseline (9 tables and indexes)
- `HealthController` (`/api/health`)
- Temporary `SecurityConfig` (only `/api/health` public)
- Multi-stage `Dockerfile`
- Context-load test

**Pattern named:** Repository (DIP).

**Verification:** 28 Java files, 0 comments (rule followed), YAML valid.

---

## Slice 2: React + Vite skeleton + theme tokens (done)

**Scope:** The frontend boots, the theme system is in place (light and dark plus blue), and it is wired to nothing yet.

**What gets built:**
- `package.json` (React, Vite, TypeScript, Tailwind, Shadcn/ui, TanStack Query, react-hook-form, Zod, react-markdown, react-router)
- `vite.config.ts`
- `tsconfig.json`
- `tailwind.config.js` and `postcss.config.js`
- `index.html`
- Theme tokens through CSS variables (light and dark plus blue accent)
- `App.tsx` with routing skeleton
- `AppShell` component (sidebar and topbar layout)
- API client setup (fetch wrapper, base URL)
- Login screen (wired to nothing, just UI)
- `Dockerfile` (nginx static serve)
- ESLint config

**Patterns:** None yet (UI scaffolding).

---

## Slice 3: Auth (Spring Security) (done)

**Scope:** Register, login, logout, session cookies, and `/api/auth/me`. The frontend login form is wired to the API.

**Backend:**
- Real `SecurityConfig` (session cookies, httpOnly and Secure and SameSite, CSRF for SPA, bcrypt)
- `AuthController` (`/api/auth/register`, `/api/auth/login`, `/api/auth/logout`, `/api/auth/me`)
- `UserService`, `AuthService`
- `CurrentUser` argument resolver
- Seed user through `CommandLineRunner` (dev profile)
- Global exception handler (`@RestControllerAdvice`)

**Frontend:**
- Login form (react-hook-form and Zod)
- Register form
- Auth context (current user, login and logout actions)
- Protected route wrapper
- Redirect to dashboard on login

**Pattern:** DTO and Mapper (introduced here, used everywhere after).

**Tests:** `@WebMvcTest` for AuthController (login success, login failure, register validation).

---

## Slice 4: Engagements + membership + join codes (done)

**Scope:** Google Classroom model. Anyone creates an engagement (becomes leader), and members join through a code.

**Backend:**
- `EngagementController` (`POST /api/engagements`, `GET /api/engagements`, `GET /api/engagements/{id}`)
- `JoinController` (`POST /api/join` with code, `POST /api/engagements/{id}/join-code` to generate or regen)
- `EngagementService`, `MembershipService`, `JoinCodeService`
- Membership enforcement (`@PreAuthorize` or service-layer check on every engagement-scoped endpoint)
- DTOs: `EngagementSummary`, `EngagementDetail`, `JoinRequest`

**Frontend:**
- Main Dashboard (engagements list, join code input, create button)
- Engagement detail route (shell only, the board fills in Slice 5)

**Patterns:** DTO and Mapper (continued), service-layer authorization.

**Tests:** Authorization test (user A cannot see engagement B), join code flow test.

---

## Slice 5: Engagement board (done)

**Scope:** Leader-editable engagement info. Members are read-only. Member management.

**Status:** Pushed, CI green.

**What was built:**
- `PUT /api/engagements/{id}` (leader-only, updates all fields including scope and RoE)
- `DELETE /api/engagements/{id}/members/{userId}` (leader-only, soft delete through removedAt)
- `POST /api/engagements/{id}/transfer-leadership` (leader-only, swaps roles)
- Tabbed UI: Overview, Scope and RoE (in-scope targets, allowed and forbidden techniques, emergency contacts, out-of-scope), Team
- Edit mode toggle (leader only): single form for all fields
- Member management: remove buttons, transfer leadership flow
- CSRF tokens wired in the frontend apiFetch (resolves the gap from Slice 3)

---

## Slice 6: Logs CRUD + log board + log-add form (done)

**Scope:** The core feature. Create logs, list them, filter, and view detail.

**Status:** Built, mvn compile and ESLint and build all pass locally. Awaiting push and CI.

**What was built:**

*Backend (8 new, 1 modified):*
- `LogController` (`POST /api/engagements/{id}/logs`, `GET /api/engagements/{id}/logs`, `GET /api/logs/{id}`, `PUT /api/logs/{id}`)
- `LogService` (create, filtered and paginated list, findByIdAndAssertMember, update with author-or-leader and draft-only authz)
- `LogSpecifications` (composable `Specification` predicates: engagement, author, activity_type, outcome, review_state, ILIKE text search)
- `LogEntryRepository` modified: added `JpaSpecificationExecutor<LogEntry>`
- DTOs: `LogCreateRequest`, `LogUpdateRequest`, `LogFilter`, `LogSummary`, `LogDetail`

*Frontend (5 new, 2 modified):*
- `lib/logs.ts` (types, API functions, enum formatters, badge class helpers)
- `hooks/useLogs.ts` (TanStack Query hooks: list, detail, create, update)
- `pages/LogBoard.tsx` (table, filters bar, pagination)
- `pages/LogDetail.tsx` (full log view)
- `pages/LogForm.tsx` (shared create and edit)
- `App.tsx` modified (4 new routes)
- `pages/EngagementDetail.tsx` modified (placeholder replaced with Logs card)

**Filters shipped:** author (backend), activity_type, outcome, review_state, text search. Date-range and target-text are deferred.

**Authz:** membership gate on every endpoint (404 on unauthorized). Author-or-leader and draft-only on edit (409 on non-draft edit).

**Patterns:** DTO and Mapper (full exercise), Repository and Specification (composable queries).

**Tests:** Deferred (Testcontainers setup pending from Slice 3). See Slice 6.5.

---

## Slice 6.5: Test infra + retrospective tests (done)

**Scope:** Wire Testcontainers and Spring Boot integration testing. Write tiered retrospective tests for Slices 3, 4, and 6. Fix a real bug the tests caught.

**Status:** Built, `mvn test` green locally (18 tests, 0 failures). Awaiting push and CI.

**What was built:**

*Test infrastructure (2 new):*
- `support/PostgresTestConfig.java`: `@TestConfiguration` with `@ServiceConnection` Postgres container (postgres:16-alpine, matches dev compose)
- `support/IntegrationTestBase.java`: base class with MockMvc, autowired repos, data factory helpers (`createUser`, `createEngagement`, `addMember`, `createLog`), `asUser()` auth post-processor, and `@BeforeEach` data wipe

*Test classes (3 new, 1 re-enabled), 18 tests total:*
- `AuthIntegrationTest.java` (5 tests): register, login success, login failure, /me authed, /me unauthed (403)
- `EngagementAuthzIntegrationTest.java` (5 tests): member view, non-member 404-not-403, unauthenticated 403, transfer-leadership role gating
- `LogAuthzIntegrationTest.java` (7 tests): member and non-member view, author draft edit, submitted 409, non-author 409, leader edits any draft, unauthenticated 403
- `PlogitApplicationTests.java` (re-enabled, `@Disabled` removed)

*Bug fix (5 modified source files):*
- `service/EngagementService.java`: added `getDetailForMember()`, `updateAndBuildDetail()`, `buildMemberInfos()` (transactional DTO mapping)
- `service/LogService.java`: create, update, and findDetailByIdAndAssertMember now return DTOs. The list returns `Page<LogSummary>` mapped within the transaction.
- `web/EngagementController.java`: thinned to use service DTO methods
- `web/LogController.java`: thinned to use service DTO methods
- `pom.xml`: added `spring-boot-testcontainers`, `testcontainers/postgresql`, `testcontainers/junit-jupiter` (versions managed by the Spring Boot BOM)

**The bug:** Controllers mapped JPA entities to DTOs after `@Transactional` returned, so the lazy proxies (`getUser()`, `getLeader()`) blew up with `LazyInitializationException`. That would have 500'd every engagement and log detail view in production. The fix: all DTO mapping now happens inside service-layer transactions.

**Patterns:** Integration testing through Testcontainers, data factory pattern, `@ServiceConnection`.

---

## Slice 7: Review state machine (done)

**Scope:** Draft to Submitted to Approved to Exported. Rejection requires a comment.

**Status:** Built, `mvn test` 33/33 (15 new) and ESLint and build all pass locally. Awaiting push and CI.

**What was built:**

*Backend (12 new, 3 modified):*
- `domain/ReviewAction.java` (enum: SUBMIT, APPROVE, REJECT)
- `domain/review/ReviewStateBehavior.java` (interface: transition(action, ctx))
- `domain/review/TransitionContext.java` (record: isAuthor, isLeader, comment)
- `domain/review/DraftState.java`, `SubmittedState.java`, `ApprovedState.java`, `ExportedState.java` (4 concrete state classes, each owns its transition rules, role checks, and comment-required logic)
- `domain/review/ReviewStates.java` (flyweight registry: enum to behavior object, reconciles JPA enum persistence with GoF object-as-state)
- `web/dto/TransitionRequest.java` (record: action, comment?)
- `service/LogReviewService.java` (thin dispatcher: load log, assert membership 404, build context, delegate to behavior, apply side effects, persist)
- `web/exception/ForbiddenException.java` (403 for business-role denial, distinct from Spring Security AccessDeniedException)
- `web/LogController.java` modified (+POST /api/logs/{id}/transition)
- `web/dto/LogDetail.java` modified (+leaderId field so the frontend can show review actions without an extra fetch)
- `web/exception/GlobalExceptionHandler.java` modified (+ForbiddenException handler)

*Frontend (4 modified):*
- `lib/logs.ts` (+ReviewAction type, +transitionLog, +leaderId on LogDetail)
- `hooks/useLogs.ts` (+useTransitionLog, invalidates list and detail queries)
- `pages/LogDetail.tsx` (submit button on drafts, approve and reject for leader on submitted, reject comment modal, error banner)
- `pages/LogBoard.tsx` (leader-only Review Queue toggle, reuses cached useEngagements for role detection)

**Legal transitions enforced:**
- SUBMIT: DRAFT to SUBMITTED (author or leader)
- APPROVE: SUBMITTED to APPROVED (leader only)
- REJECT: SUBMITTED to DRAFT (leader only, comment required)
- APPROVED and EXPORTED: terminal in Slice 7 (EXPORT arrives in Slice 10)

**Authz:** non-member is 404 (cannot tell the log exists), member-wrong-role is 403, legal-but-wrong-state or missing comment is 409.

**Decision logged:** State classes throw web exceptions directly (ConflictException or ForbiddenException), consistent with the existing LogService and MembershipService pattern. We chose pragmatic consistency over clean-architecture domain exceptions for a single-delivery-channel project. Documented as deliberate in the Process Log.

**Patterns:** State (GoF, central pattern), flyweight registry as the JPA to object bridge.

**Tests:** 15 new in `ReviewTransitionIntegrationTest`: every legal transition (submit, leader submit, approve, reject-with-comment, resubmit-clears-comment), every illegal transition (approve-from-draft, reject-from-draft, submit-from-submitted, transition-from-approved, transition-from-exported), wrong role (member tries approve is 403), missing comment (reject without comment is 409), non-member (404), unauthenticated (403).

---

## Slice 8: Versioning + audit log (Observer) (done)

**Scope:** Every edit snapshots a version. Every state change writes an audit log entry.

**Status:** Built, `mvn test` 44/44 (11 new) and ESLint and build and test_api.py 100/100 all pass locally. Awaiting push and CI.

**What was built:**

*Backend (7 new, 6 modified, 1 modified test):*
- `domain/LogSnapshot.java` (new record: editable content fields, toMap() for JSONB, from(LogEntry) factory)
- `domain/LogTransitionedEvent.java` (new Spring application event: actorId, logId, engagementId, action, fromState, toState, comment)
- `service/LogVersionService.java` (new: snapshot() on create and update, historyFor() query)
- `service/AuditEventListener.java` (new: @TransactionalEventListener BEFORE_COMMIT, the Observer, catches LogTransitionedEvent and writes AuditLog)
- `web/dto/LogVersionSummary.java`, `web/dto/AuditLogEntry.java` (new DTOs)
- `service/LogService.java` (modified: +snapshot on create and update, +historyFor)
- `service/LogReviewService.java` (modified: +publishEvent(LogTransitionedEvent) after transition)
- `service/EngagementService.java` (modified: +getAuditLogForLeader, leader-only)
- `web/LogController.java` (modified: +GET /api/logs/{id}/history)
- `web/EngagementController.java` (modified: +GET /api/engagements/{id}/audit)
- `repository/AuditLogRepository.java` (modified: +findByEngagementId native JSONB query)
- `support/IntegrationTestBase.java` (modified: +version and audit table wipe in @BeforeEach)

*Frontend (6 modified):*
- `lib/logs.ts` (+LogVersionSummary type, +fetchLogHistory)
- `lib/engagements.ts` (+AuditLogEntry type, +fetchAuditLog)
- `hooks/useLogs.ts` (+useLogHistory)
- `hooks/useEngagements.ts` (+useAuditLog)
- `pages/LogDetail.tsx` (+collapsible edit history panel)
- `pages/EngagementDetail.tsx` (+leader-only Audit tab)

**Two-mechanism split (presentation material):**
- Versioning is snapshot-on-write, a direct call within the transaction. It is NOT the Observer. Its job is data integrity.
- Audit is the event-driven Observer. The service publishes LogTransitionedEvent, and AuditEventListener catches it. It is a decoupled concern. `@TransactionalEventListener BEFORE_COMMIT` joins the transition transaction, so the audit rolls back if the transition does.

**Full snapshot strategy:** all editable fields are stored each time (not diffs), per spec section 4.1.

**Authz:** history is visible to any member (content evolution), audit is leader-only (system meta).

**Patterns:** Observer (Spring @TransactionalEventListener). Versioning is a supporting mechanism, not the named pattern.

**Tests:** 11 new across LogVersioningIntegrationTest (5: create is version1, edit is version2, multiple edits incrementing, member can view, non-member 404) and AuditLogIntegrationTest (6: submit, approve, and reject each create an entry, reject stores comment, multiple transitions ordered trail, member 404, audit scoped to engagement). test_api.py +10 checks.

---

## Slice 9: Markdown + code blocks + attachments (done)

**Scope:** Markdown rendering, code block display, and image uploads.

**Status:** Built, `mvn test` 60/60 (16 new) and ESLint and build and test_api.py 108/108 all pass locally. Awaiting push and CI.

**What was built:**

*Backend (5 new, 5 modified):*
- `pom.xml` (+3 deps: flexmark-all 0.64.8, owasp-java-html-sanitizer 20240325.1, tika-core 3.1.0)
- `service/MarkdownService.java` (new: flexmark parse markdown to HTML, OWASP Sanitizers strip script, event-handlers, and javascript-protocols)
- `service/AttachmentService.java` (new: Tika magic-byte detection, local FS storage at {engagementId}/{logId}/{uuid}-{filename}, image-only allowlist)
- `web/AttachmentController.java` (new: POST upload multipart, GET list, GET download)
- `web/dto/AttachmentResponse.java` (new DTO)
- `service/LogService.java` (modified: +MarkdownService injection)
- `service/LogReviewService.java` (modified: +MarkdownService injection)
- `web/dto/LogDetail.java` (modified: +descriptionHtml, +resultHtml fields, second from() factory that takes MarkdownService)
- `repository/AttachmentRepository.java` (modified: +findByLogOrderByUploadedAtDesc)

*Frontend (2 modified):*
- `lib/logs.ts` (+Attachment type, +fetchAttachments, +uploadAttachment with manual FormData and CSRF, +attachmentUrl, +formatFileSize, +descriptionHtml and resultHtml on LogDetail)
- `pages/LogDetail.tsx` (+sanitized HTML rendering for description and result, +attachment gallery with hover-to-view, +image upload button for editors)

**Sanitization strategy (defense in depth, spec E3):**
- Store raw markdown (preserve user intent for editing)
- On read: flexmark parses to HTML, the OWASP sanitizer strips dangerous tags and attributes, and the DTO returns pre-sanitized descriptionHtml and resultHtml
- The frontend renders server-sanitized HTML (not client-trusted)

**Attachment security:**
- Tika detects the actual file type from magic bytes, not extension
- Allowlist: PNG, JPEG, GIF, WebP only (non-image is rejected with 409)
- Storage path is sanitized (filename stripped of special chars, UUID prefix)
- Local filesystem, organized by engagement and log, docker-compose volume wired
- Max 10MB per file (configured in application.yml)

**Patterns:** None new (feature slice). MarkdownService is a DTO and Mapper continuation.

**Tests:** 16 new across MarkdownSanitizationTest (8: basic render, code block, strips script and javascript-protocol and event-handlers, allows safe links, null and blank) and AttachmentIntegrationTest (8: upload image, non-image rejected, list, download, non-member 404 on upload and download, member can upload to any log, unauthenticated 403). test_api.py +8 checks (multipart upload, list, download, type rejection).

---

## Slice 10: PDF + JSON/CSV export (Factory + Strategy) (done)

**Scope:** The leader exports an engagement. PDF (auditor evidence pack) plus JSON and CSV (machine-readable).

**Status:** Built, `mvn test` 76/76 (16 new) and ESLint and build and test_api.py 129/129 (21 new) all pass locally. Pushed, CI green.

**What was built:**

*Backend (10 new, 7 modified):*
- `domain/ReportFormat.java` (PDF/JSON/CSV enum)
- `service/export/ReportStrategy.java` (Strategy interface: format() and generate(EngagementReportData))
- `service/export/ReportFactory.java` (Factory: registry of ReportStrategy beans keyed by ReportFormat, @Component, same flavor as ReviewStates)
- `service/export/EngagementReportData.java` (immutable snapshot: engagement fields, leader, members, scope and RoE, logs each with raw markdown, rendered HTML, and image bytes)
- `service/export/ReportOutput.java` (record: content bytes, contentType, filename)
- `service/export/JsonReportStrategy.java` (Jackson pretty-print, raw markdown for ControlMap and tool ingestion)
- `service/export/CsvReportStrategy.java` (hand-rolled RFC-4180 escaping, one row per log)
- `service/export/PdfReportStrategy.java` (OpenHTMLtoPDF 1.1.40, HTML template from spec section 7, reuses MarkdownService for sanitized HTML, base64-inline images)
- `service/ExportService.java` (leader-only, queries APPROVED logs by default, assembles snapshot, generates report, then transitions each APPROVED to EXPORTED through LogReviewService so the State pattern and audit Observer fire)
- `web/dto/ExportRequest.java` (format and includeExported flag)
- `domain/review/ApprovedState.java` modified (+EXPORT action, leader-only to EXPORTED)
- `domain/review/ExportedState.java` modified (+EXPORT re-export path, returns EXPORTED unchanged, audit still fires)
- `domain/ReviewAction.java` modified (+EXPORT, 4th action)
- `web/EngagementController.java` modified (+POST /api/engagements/{id}/export, leader-only, binary response with Content-Disposition)
- `service/AttachmentService.java` modified (+loadImagesForLog batch loader, refactored readFile helper)
- `repository/LogEntryRepository.java` modified (+findByEngagementAndReviewStateInOrderByCreatedAtAsc)
- `pom.xml` modified (+openhtmltopdf-core and openhtmltopdf-pdfbox 1.1.40, excluded legacy com.openhtmltopdf from flexmark-all)
- `ExportIntegrationTest.java` (12 tests)

*Frontend (2 modified):*
- `lib/engagements.ts` (+exportEngagement: dedicated fetch for binary, blob and filename from Content-Disposition, CSRF wired, +ExportFormat type, +ExportResult interface)
- `pages/EngagementDetail.tsx` (+leader-only Export button, +export modal: format selector with icons and per-format help, includeExported checkbox, info banner on state-change consequence, generating state, blob download through createObjectURL, success toast)

**State machine extension (your decision):** Export is a 4th ReviewAction.EXPORT. ApprovedState: EXPORT leader-only to EXPORTED (other actions still 409). ExportedState: EXPORT leader-only re-export, no state change but the audit event fires (other actions still 409 immutable). Spec section 8 "re-exporting creates a new audit event and does not change the log" is honored.

**Default export set:** APPROVED logs only (spec section 7 verbatim). The includeExported flag re-includes EXPORTED logs for regenerate-the-pack.

**Endpoint:** POST (not GET) because export mutates state (a REST anti-pattern to side-effect on GET) and POST flows through CSRF cleanly.

**PDF template (spec section 7):** cover page (name, dates, leader, team, exportedAt, included versus total counts), scope and RoE (in-scope targets, allowed hours, allowed and forbidden techniques, emergency contacts, out-of-scope), description, chronological logs (metadata table and rendered descriptionHtml and resultHtml and code block as `<pre>` monospace and attachment images as base64-inline figures), appendix (counts, out-of-scope notes).

**Sanitization:** PDF reuses MarkdownService.render() (flexmark and OWASP sanitizer), so the sanitization story is identical to the app. Server-sanitized HTML in the PDF, not raw markdown.

**Patterns:** Factory (report types, OCP), Strategy (format selection, OCP). State extended (EXPORT action). Three patterns in one slice.

**Three bugs caught by tests (not by me presenting):**
1. PDFBox version conflict: flexmark-all dragged in legacy com.openhtmltopdf:1.0.10 (PDFBox 2.x) colliding with io.github:1.1.40 (PDFBox 3.x), which caused NoSuchFieldError. Fix: excluded the legacy openhtmltopdf from flexmark-all.
2. Non-well-formed XHTML: `<meta charset="UTF-8">` broke the strict XML parser of OpenHTMLtoPDF. Fix: self-closed as `<meta .../>`.
3. Test-only: the CSV data-row assertion failed because the PDF export (run first) had already transitioned the log to EXPORTED. Test-logic bug, not code. Fix: assert the empty case explicitly, and re-export with includeExported for data rows.

**Tests:** 16 new (4 EXPORT transitions in ReviewTransitionIntegrationTest plus 12 in ExportIntegrationTest). test_api.py +21 (PDF magic and content-type and Content-Disposition and size, JSON structure and entries, CSV header and data rows, state change, re-export with and without the flag, member and unauth authz, audit EXPORT entries).

---

## UI Overhaul: Design overhaul, sidebar+topbar, HW4 palette, page reworks (done)

**Scope:** Full frontend visual overhaul. Sidebar and topbar shell, HW4 Bootstrap-matched palette (dark sidebar, light body, Bootstrap blue), and all 7 pages reworked. Triggered by user feedback after Slice 10: the prior UI was a bare topbar with minimal styling.

**Status:** Built, ESLint and build clean. Not yet pushed (pending user copy-paste).

**What was built:**

*Palette and tokens (styles.css, tailwind.config.js):*
- Light theme re-pointed to HW4 Bootstrap values: app bg #f8f9fa, card #ffffff, sidebar and header #212529, text #212529, muted #6c757d, primary #0d6efd, borders rgba(0,0,0,.175)
- Dark theme: same semantic mapping shifted dark
- Added semantic tokens: success #198754 (+soft), danger #dc3545 (+soft), warning #ffc107 (+soft)
- tailwind.config.js extended to expose success, danger, and warning as Tailwind colors
- logs.ts badge helpers rewritten to use semantic tokens (no more hardcoded bg-green-100 dark:bg-green-950/30)

*Shell rewrite (AppShell.tsx):*
- Fixed sidebar (w-64, bg-bg-header dark): brand, context-aware nav, theme toggle, user avatar, sign out
- Sticky topbar: page title (route-derived), date
- Responsive: sidebar slides in and out on mobile with overlay

*Full page reworks (7 pages):*
- Dashboard: engagement card grid (sm:2 and lg:3), card-contained create and join forms, designed empty and loading states
- LogBoard: polished table in rounded card, refined filter bar, styled pagination
- LogDetail: metadata icon-tile cards, token-colored action bar, attachment gallery, refined history
- LogForm: sectioned cards, consistent focus:ring-2 inputs, polished tag chips
- EngagementDetail: folder icon-tile header, refined tabs, icon-tile date cards, colored audit trail, polished export modal
- Login and Register: brand tile (rounded-2xl blue square), focus rings, token-based errors, theme toggle button
- Sidebar expanded to three global tabs (Dashboard, Engagements, Settings) plus a "Current Engagement" section that appears contextually. Two new pages: EngagementsPage (dedicated engagement CRUD: create, join, card grid) and Settings (profile info and theme preference). Information-architecture split: Dashboard is overview, Engagements is CRUD, Settings is prefs.

**Critical bug caught (the invisible-app root cause):**
- styles.css was missing `@tailwind base; @tailwind components; @tailwind utilities;` since Slice 2. Without them, Tailwind generated ZERO utility classes. The app rendered as effectively-unstyled HTML for 10 slices. The compiled CSS was 1.7 kB with no .flex or .bg-primary. Fix: added the three directives. CSS jumped to 18.2 kB with all utilities.
- Second bug: AppShell used `bg-header` (generates nothing, Tailwind reads it as the default-palette 'header' key) instead of `bg-bg-header` (the token-namespace class). The sidebar was transparent, and the white text was invisible on the light body. Fix: bg-header to bg-bg-header.

**Verification:** ESLint clean, build clean (94 modules). Compiled CSS verified to contain .flex, .bg-primary, .bg-bg-header, and .items-center.

---

## UI3: Sidebar tabs + EngagementsPage + Settings (done)

**Scope:** Collaborative edit to the repo. Sidebar expanded to three global tabs (Dashboard, Engagements, Settings) plus a contextual "Current Engagement" section. Two new pages: EngagementsPage (dedicated engagement CRUD) and Settings (profile info and theme preference).

**Status:** Built, cleaned, pushed, CI green.

**What was built:**
- `pages/EngagementsPage.tsx` (new): full engagement management lifted from the Dashboard. Create form, join bar, card grid. Route `/engagements`.
- `pages/Settings.tsx` (new): profile info card (read-only display name and email) plus an appearance card with theme toggle. Route `/settings`.
- `App.tsx`: wired the two new routes.
- `AppShell.tsx`: globalNavItems expanded from one Dashboard link to three (Dashboard, Engagements, Settings), and engagementNavItems split into a "Current Engagement" section.
- `Dashboard.tsx`: shortened (the engagement list moved to EngagementsPage).

**Cleanup done:** Three issues in the contributed code were caught by our gates. (1) ESLint: unused `setDark` in AppShell. Fix: `const [dark]`. (2) ESLint: `any[]` type. Fix: typed properly. (3) Standing rule violation: two JSX comments in Settings.tsx. Fix: removed. After cleanup: ESLint clean, build clean (96 modules).

**Why:** Information-architecture split. Dashboard is the landing overview, Engagements is the dedicated CRUD surface, and Settings is account and theme prefs. It makes the sidebar honest (each tab is a real destination).

---

## Docs: Professional README rewrite (done)

**Scope:** Replace the thin feature-bullet README with a professional narrative document that matches the AuditYote reference style.

**Status:** Built, screenshots wired. Pending push.

**What was built:**
- Narrative structure: pipeline framing, roles, state machine (with transition table), versioning and audit (Observer), markdown and attachments, export (Factory and Strategy), architecture (Mermaid diagrams), design patterns, tech stack, security, CI, testing, project layout (table).
- Mermaid diagrams for the remediation pipeline and the architecture stack (GitHub renders natively).
- Table format for the project layout (no ASCII art).
- Seven `[Image: ...]` placeholders marking where screenshots go. The `.github/assets/` folder was created.
- Corrected stale facts: removed the false Shadcn/ui claim, updated versions to actual (Spring Boot 3.5.16, React 18, Vite 6), and accurate counts (9 entities, 24 endpoints, 76 tests).

**Screenshots wired:**
- The user dropped 10 PNGs, 7 matched placeholders, and 3 were extras (deleted).
- Renamed 4 to canonical names: `engagement-scope.png`, `audit-log.png`, `log-markdown.png`, `export-modal.png`.
- Replaced all 7 placeholders with centered `<p align="center">` and `<img width="900">` blocks, each with descriptive alt text.
- Verified: 0 placeholders remain, 7 refs equal 7 files.

**Pending:** Push README.md and `.github/assets/` together.

---

## UIFix: Badge wrap fix (done)

**Scope:** Fix the "In Progress" outcome badge wrapping to two lines in the log board, which broke the rounded pill shape.

**Status:** Built, ESLint and build clean. Pending push.

**What was built:**
- Root cause: the badge `<span>` had no `whitespace-nowrap`, so two-word outcomes ("In Progress") wrapped inside the narrow table cell. "Dead End" and "Confirmed" fit on one line, so only IN_PROGRESS showed the bug.
- Fix: added `whitespace-nowrap` to the outcome and review-state badge spans in `LogBoard.tsx` and `LogDetail.tsx` (4 spans total), plus `whitespace-nowrap` on both badge `<td>` cells in LogBoard.
- Gates: ESLint clean, build clean (96 modules).

**Note:** The submit-rule was investigated in the same session (whether the leader should be able to submit members' drafts). A restriction to author-only was implemented and tested (76/76 green), then rolled back at the user's decision: the asymmetry (leader can edit but not submit) was not worth the complexity. Behavior unchanged: author or leader can submit.

---

## Slice 11: Deploy + server hardening (done)

**Scope:** Live on a VPS with SSL and a domain. Full hardening from Previous.md.

**Server-side (you, with my runbook):**
- VPS provisioning (Ubuntu 24 LTS)
- sudo user, SSH root login disabled, SSH key auth
- UFW firewall (22, 80, 443 only)
- SYN cookies, Fail2Ban
- NTP and Bangkok timezone
- OpenJDK install
- DNS (A record and CNAME)
- Docker and docker-compose

**Deploy:**
- `docker compose -f docker-compose.yml -f docker-compose.prod.yml --profile app up -d --build`
- Caddy auto-TLS through Let's Encrypt
- Flyway migrations run on startup
- Health check (`curl https://yourdomain/api/health`)
- Auto-start on reboot (Docker restart policy)

**Runbook deliverable:** Step-by-step with the why behind each hardening step (presentation material).

---

## Deferred items (documented honestly)

These did not make the original 3-day deadline and are documented as "deferred to v2". Some have since been partially addressed (leader transfer shipped in Slice 5).

- **Secrets detection (E4):** heuristic regex warn on save.
- **Scope warning modal (C1):** real-time warn on an out-of-scope target.
- **Full filter set:** shipped 5 filters (author, activity_type, outcome, review_state, text search). Date-range and target-text are deferred.
- **Group-by-target toggle (D8):** chronological only for v1.
- **Demo data seeding:** sample engagement for new users.
- **Objectives list (D4):** the data model supports objectives (Objective embeddable, stored as jsonb on Engagement), but there is no leader-editable UI for them.
- **Image re-encoding:** validates type through Tika magic bytes but does not re-encode to strip payloads.
- **PDF base64 image embedding:** memory-heavy for huge engagements. Fine for v1 scale.
- **Frontend component tests:** ESLint and build and test_api.py is the gate.

---

## Production architecture note (corrected July 2026)

Production uses Nginx installed on the droplet host (outside Docker) as the reverse proxy for SSL termination, and it proxies to the Docker containers on their loopback-bound ports. The `Caddyfile` in the repo is local and dev TLS testing only, not part of production. The deploy domain (`plogit.weilong.dev`) and the Nginx site config live on the server, outside the repo, because the project is open-source and domain-agnostic.

Two real doc items remain: the README's architecture section still says Caddy is the prod proxy (stale, needs correction), and the CI deploy path on the server (`~/plogit/Repo`) may reflect an old folder name. See HANDOFF section 8 and Knowledge Base section 7 for full detail.

---

*All slices complete. This file is a historical record of the build.*

# PLogIt - Knowledge Base

> **2026-11-02 REBASELINE:** This document preserves July context. PLogIt source is imported unchanged at main commit 28aa156; actual Spring Boot 4.1.1 (not 3.5.16), Java 21, React 18/Vite 6 and PostgreSQL 16. The latest checked main CI passed its five validation jobs but CD failed the PROD_SSH_HOST_FINGERPRINT check. Deployed state of this commit is NOT verified. Proposed code changes belong ONLY in the new Markdown plan; source code is frozen in this research pass. See [suite research](../../Suite%20-%20Research%20Refresh%202026-11-02.md), [implementation roadmap](../../Suite%20-%20Modernization%20Roadmap.md), and [PLogIt proposed update plan](../PLogIt/docs/PLOGIT-RESEARCH-UPDATE-PLAN-2026-11.md).

> **Purpose:** Consolidated reference of everything learned, plus all locked decisions from the planning conversations. Built so the reasoning survives even if the original source files are gone.
> **Status:** Living document. Append, do not rewrite.
> **Project type:** Personal project (originally a university course deliverable, the course framing is retired).
> **Related:** [`PLogIt - Product Spec.md`](./PLogIt%20-%20Product%20Spec.md) (the bible), [`Master Prompt.md`](../Rules%20to%20Follow/Master%20Prompt.md) (constitution), [`PLogIt - Process Log.md`](./PLogIt%20-%20Process%20Log.md) (build record).

---

## 0. Standing rules (never forget, never bend)

These are durable constraints that apply across all sessions and slices.

- **No comments in source code.** Files with extensions `.java`, `.tsx`, `.ts`, `.jsx`, and `.css` must contain zero comments. No `//`, no `/* */`, no `<!-- -->`, no JSDoc, and no JavaDoc. The code must self-document through naming and structure.
- **Config files keep comments.** `docker-compose.yml`, `docker-compose.prod.yml`, `Caddyfile`, `.github/workflows/*.yml`, `.env.example`, `.gitignore`, `.editorconfig`, and `.gitattributes` are operational documentation and keep explanatory comments.
- **Honesty about status.** Never claim incomplete work as done. Defer explicitly.
- **Package root:** `io.muzoo.ssc.plogit`.
- **Always run `mvn test` locally before sign-off.** Upgraded from `mvn compile` in Slice 6.5 after a LazyInitializationException that compiled fine but would have 500'd in production.
- **Always run ESLint and build locally before sign-off.** Process change from Slice 2 after multiple config bugs reached CI.
- **Run `test_api.py` for any slice touching auth or state-changing endpoints.** Process change from the CSRF fix: MockMvc's `.with(csrf())` bypasses the real HTTP cookie flow, so only the HTTP-level test script catches wire-level bugs. Three gates now: mvn test, ESLint and build, test_api.py.
- **Check `mvn dependency:tree` for transitive conflicts** when you add a library that shares a namespace with an existing transitive dep. Process change from Slice 10: flexmark-all dragged in legacy `com.openhtmltopdf:1.0.10` (PDFBox 2.x) which collided at runtime with the explicitly-added `io.github.openhtmltopdf:1.1.40` (PDFBox 3.x), and threw NoSuchFieldError. The nearest-wins resolution of Maven did not surface it, and only running the code did.
- **Verify compiled CSS contains expected Tailwind utilities** after any styles.css or tailwind.config change. Process change from the UI overhaul: styles.css was missing the three `@tailwind base/components/utilities` directives since Slice 2, so Tailwind generated zero utilities and the app rendered as unstyled HTML for 10 slices. Tailwind does NOT error on missing directives, it silently emits nothing. After CSS or config changes, grep the built output (for example `grep -c '\.flex' dist/assets/*.css`) to confirm utilities are present.
- **Tailwind token-namespace class naming:** a color defined under `colors.bg.header` in tailwind.config.js produces the class `bg-bg-header` (prefix `bg-` plus group `bg` plus shade `header`), NOT `bg-header`. Using `bg-header` silently generates nothing.
- **Verify in browser BEFORE cleaning artifacts.** Cleaning `node_modules` before browser verification breaks the dev server (`vite: command not found`). The correct sequence is: run the stack, verify the fix visually in the browser, and THEN clean all artifacts as the final step before handoff. Clean is the LAST action, not a mid-process one.
- **Clean all build artifacts before handoff.** Gitignored files (backend/target, frontend/node_modules, frontend/dist, frontend/tsconfig.tsbuildinfo, backend/attachments, .DS_Store) travel along in a folder copy. Always clean these before handoff.
- **Verify external references** (action versions, library coordinates, command flags) against primary sources before claiming they work.

---

## 1. Pipeline relationship (PLogIt and the auditor stage)

PLogIt sits in the pentester stage of a remediation pipeline. The pipeline is Pentester (us) to Auditor to Blue Team. Our job is thorough and honest capture of activity and evidence. The auditor triages, assigns severity, and writes the final report. The blue team fixes.

This framing is load-bearing for the scope decision (A1). We do not rate severity, and we do not produce client-facing reports, because that is the auditor's job. If PLogIt ever expands downstream, the log to finding promotion path is the natural extension point, but it is out of scope for v1.

The reference companion was ControlMap (an auditor-stage tool). The pipeline framing still applies conceptually, but the shared-workspace reference is gone.

---

## 2. Architecture patterns (what was adopted and why)

### 2.1 Repo structure (monorepo)
```
PLogIt/
  .env.example
  docker-compose.yml         <- local dev
  docker-compose.prod.yml    <- prod override
  Caddyfile                  <- local/dev TLS testing template (NOT used in prod, see section 2.11)
  .github/workflows/ci.yml
  .github/assets/            <- README screenshots
  docs/
  backend/                   <- Spring Boot
  frontend/                  <- React + Vite
  README.md
```

### 2.2 docker-compose profile trick
- The `db` service runs immediately (Postgres, no profile gate).
- `backend` and `frontend` sit under `profiles: ["app"]`.
- Plain `docker compose up` only starts Postgres.
- `docker compose --profile app up --build` starts the full stack.
- Prevents "build nonexistent code" errors early in the project lifecycle.

### 2.3 CI green-from-day-one pattern
- GitHub Actions with two guarded jobs (backend, frontend): existence checks on `backend/pom.xml` and `frontend/package.json`.
- Jobs no-op until code exists, so CI is useful from commit 1.

### 2.4 Repository (DIP) and DTO + Mapper (SRP)
- Spring Data JPA repositories abstract persistence (DIP).
- DTOs and mappers keep the API surface clean and decoupled from JPA entities (SRP).
- Introduced in Slice 1 and Slice 3, used everywhere after.

### 2.5 Specification pattern (composable queries)
- `LogSpecifications` builds composable `Specification<LogEntry>` predicates: engagement, author, activity_type, outcome, review_state, ILIKE text search.
- Lets the log board compose filters into one query instead of N separate screens.

### 2.6 State pattern (GoF) for review
- Four state classes (DraftState, SubmittedState, ApprovedState, ExportedState), each owns its transition rules and role checks.
- `ReviewStates` is a flyweight registry that reconciles JPA enum persistence with GoF object-as-state.
- Introduced in Slice 7, extended in Slice 10 (EXPORT action).

### 2.7 Observer pattern (Spring @TransactionalEventListener)
- `LogReviewService` publishes `LogTransitionedEvent`.
- `AuditEventListener` catches it (BEFORE_COMMIT) and writes AuditLog.
- Decoupled: the review service does not know about the audit log.
- Versioning is NOT the Observer. Versioning is snapshot-on-write (direct call within the transaction). The Observer is the audit log only.

### 2.8 Factory + Strategy (export)
- `ReportFactory` is a registry of `ReportStrategy` beans keyed by ReportFormat (PDF, JSON, CSV).
- Adding a format is "add a bean", not editing a switch.
- Open-Closed Principle on both ends.

### 2.9 Markdown sanitization (defense in depth, spec E3)
- Store raw markdown (preserve user intent for editing).
- On read: flexmark parses to HTML, OWASP sanitizer strips dangerous tags and attributes, DTO returns pre-sanitized descriptionHtml and resultHtml.
- Frontend renders server-sanitized HTML, not client-trusted.
- Code blocks rendered as inert text. Syntax highlighting is cosmetic only, never executed.

### 2.10 Attachment security (Tika magic bytes)
- Tika detects the actual file type from magic bytes, not extension.
- Allowlist: PNG, JPEG, GIF, WebP only (non-image is rejected with 409).
- Storage path sanitized (filename stripped of special chars, UUID prefix).
- Max 10MB per file.

### 2.11 Production deploy topology (corrected July 2026)
- The app containers (db, backend, frontend) run through Docker Compose on a DigitalOcean droplet.
- `docker-compose.prod.yml` binds backend and frontend to `127.0.0.1` (loopback only). This is deliberate.
- Nginx is installed directly on the droplet host, outside Docker. It is the production reverse proxy: it terminates SSL (Let's Encrypt) and proxies to the loopback-bound containers. It is NOT the same as `frontend/nginx.conf`, which is only the in-container SPA static server.
- The `Caddyfile` in the repo is a local and dev TLS testing template (mock TLS for the test_api.py flow). It is not part of production.
- The deploy domain and the Nginx site config live on the server, outside the repo, because the project is open-source and domain-agnostic.

---

## 3. The six design patterns shipped

| Pattern | Where | Slice |
|---------|-------|-------|
| Repository (DIP) | Spring Data JPA repositories | 1 |
| DTO + Mapper (SRP) | web/dto, service mappers | 3 |
| Specification | LogSpecifications | 6 |
| State (GoF) | domain/review/* | 7, extended in 10 |
| Observer | AuditEventListener (@TransactionalEventListener) | 8 |
| Factory + Strategy | service/export/* | 10 |

---

## 4. Multi-tenancy model: "Google Classroom"

There is no Organization or Account entity. Engagements are independent, top-level entities.

- Anyone with an account can create an Engagement, which makes them its leader.
- The leader invites members through a join code or by email.
- A pentester can be a member of many engagements across different leaders and firms.
- Data isolation is by membership. A user can only see engagements they are in.
- The dashboard is all your engagements (any you lead plus any you are a member of).
- No firm-level admin in v1.

Join code mechanics: regeneratable, optionally expiring, code-join always grants member role (never leader), and leadership transfer is a separate explicit action.

---

## 5. The state machine (A4)

```
Draft -> Submitted -> Approved -> Exported
```

- Draft: the member can edit any content field freely.
- Submitted: the member cannot edit. The leader reviews.
- Approved: locked. Only the leader can edit (and every edit is versioned).
- Exported: included in a PDF. Immutable. Re-exporting creates a new audit event and does not change the log.

Rejection: the leader moves Submitted to Draft and must write a rejection_comment. The member sees it, fixes it, and resubmits. This is the only place "comments" exist.

---

## 6. Security and integrity properties

- Passwords are bcrypt-hashed.
- Session cookies are httpOnly and Secure and SameSite, with CSRF protection.
- SQL is through JPA and parameterized queries (no string-built SQL).
- Membership is enforced on every engagement-scoped endpoint (404 on unauthorized, not 403, so membership is not leaked).
- Markdown is sanitized on input (stored raw) and on output (OWASP sanitizer).
- Code blocks are inert text.
- Attachments are validated by Tika magic bytes, with an image-only allowlist.
- Audit log is append-only, separate from activity logs.
- Version history stores full snapshots on every edit.

---

## 7. Real open items (honest, not hidden)

1. **The README's architecture section still says Caddy is the prod proxy.** That is stale. Production uses host-installed Nginx. The README should be corrected to describe the actual two-tier setup.
2. **The CI deploy script runs in `~/plogit/Repo` on the server.** That path reflects the old folder name. If the server-side clone has been renamed or moved, the deploy step would break. Verify the server path matches before you rely on CD.
3. **Dead frontend dependencies.** `react-hook-form`, `react-markdown`, `rehype-sanitize`, and `zod` are declared in `package.json` but no imports of them were found in `src/`. They were scaffolded in Slice 2 for anticipated use that never landed (LogForm uses plain controlled `useState`, and LogDetail uses `dangerouslySetInnerHTML` with server-sanitized HTML). Safe to remove if you want a lean dependency tree.
4. **Deferred v1 features (documented honestly, not hidden):** secrets detection (E4), scope warning modal (C1), full filter set (date-range and target-text deferred), group-by-target, demo data seeding, objectives list UI, image re-encoding to strip payloads, PDF base64 memory optimization for huge engagements, frontend component tests, and the author filter UI (the backend supports it, the frontend does not expose it).

---

## 8. Test surface

- 76 backend integration test methods (Testcontainers, Postgres 16-alpine, `@ServiceConnection`).
- 129 HTTP-level API checks (`test_api.py`).
- ESLint and build for the frontend.
- All green at last run.

The three-gate rule: `mvn test`, ESLint and build, `test_api.py` (for auth or state-changing endpoints).

---

## 9. Endpoint count

Exactly 24 REST endpoints across 6 controllers.

---

## 10. Key lessons (the ones worth remembering)

1. **`test_api.py` caught a CSRF bug that unit tests, integration tests, and CI all missed.** MockMvc's `.with(csrf())` synthesizes a token and bypasses the real HTTP cookie flow. The fix was `CsrfTokenRequestAttributeHandler` plus a force-resolve filter after `CsrfFilter`.
2. **The versioning-versus-audit split clarifies what Observer is FOR.** Versioning is snapshot-on-write (direct call). Audit is event-driven observer (decoupled, `@TransactionalEventListener` BEFORE_COMMIT).
3. **The security library itself was vulnerable.** OWASP HTML Sanitizer had a CVE. Trivy caught it. Fixed by version bump.
4. **Dependency hell through transitive namespace collision.** openhtmltopdf under two groupIds with incompatible PDFBox baselines. Fix: exclude by groupId and artifactId.
5. **The invisible app.** styles.css shipped 10 slices without the three `@tailwind` directives, so no utilities generated. Tailwind fails silently. Process change: grep compiled output, do not trust exit code 0.
6. **Verify-before-clean ordering.** Cleaning `node_modules` before browser verification breaks the dev server. Verify first, clean last.
7. **LazyInitializationException.** Controllers mapped JPA entities to DTOs after `@Transactional` returned, so lazy proxies blew up. Caught by integration tests, not by compilation.

---

## 11. How to add a new slice

1. Define the scope. One vertical slice, shippable on its own.
2. Pick the pattern(s) if any. Document the choice in the Process Log.
3. Write the backend first, then the frontend, then the tests.
4. Run all three gates before sign-off: `mvn test`, ESLint and build, `test_api.py` (if it touches auth or state-changing endpoints).
5. Verify in the browser before you clean artifacts.
6. No comments in source. Config files keep comments.
7. Append a Process Log entry on approval.

---

*End of knowledge base. Append new learnings at the bottom.*

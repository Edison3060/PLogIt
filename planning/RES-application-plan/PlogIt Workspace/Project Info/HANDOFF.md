# PLogIt - Handoff Document

> **2026-11-02 REBASELINE:** This document preserves July context. PLogIt source is imported unchanged at main commit 28aa156; actual Spring Boot 4.1.1 (not 3.5.16), Java 21, React 18/Vite 6 and PostgreSQL 16. The latest checked main CI passed its five validation jobs but CD failed the PROD_SSH_HOST_FINGERPRINT check. Deployed state of this commit is NOT verified. Proposed code changes belong ONLY in the new Markdown plan; source code is frozen in this research pass. See [suite research](../../Suite%20-%20Research%20Refresh%202026-11-02.md), [implementation roadmap](../../Suite%20-%20Modernization%20Roadmap.md), and [PLogIt proposed update plan](../PLogIt/docs/PLOGIT-RESEARCH-UPDATE-PLAN-2026-11.md).

> **Purpose:** Self-contained context for a fresh session. Read this with `Master Prompt.md` (constitution) and `PLogIt - Product Spec.md` (the bible) to continue without memory loss.
> **Project type:** Personal project (no longer a university course deliverable).
> **Last updated:** 2026-07-11 (post-audit reframe).

---

## 1. What PLogIt is

**PLogIt** is a SaaS web app for pentesters to log engagement activity. It sits in a remediation pipeline: Pentester (PLogIt) to Auditor (ControlMap) to Blue Team. Logs-only scope: no severity and no CVSS, because that is the auditor's job. The output is an evidence pack PDF plus JSON and CSV for tool ingestion.

The project is **built, deployed, and live**. It runs on a DigitalOcean droplet with Docker Compose running the app containers, fronted by Nginx (installed on the host, outside Docker) for SSL termination and reverse proxying. A GitHub Actions pipeline builds, scans, tests, and (on pushes to `main`) SSHes into the server and redeploys automatically.

## 2. Project layout (important: read this)

```
PLogIt Workspace/                          <- all paths below are relative to here
  PLogIt/                                  <- the actual project (was "Repo/")
    backend/              <- Spring Boot (package io.muzoo.ssc.plogit, 76 tests)
    frontend/             <- React + Vite (TypeScript + Tailwind, 9 pages)
    .github/workflows/    <- CI/CD pipeline
    .github/assets/       <- 7 README screenshots
    Caddyfile             <- local/dev TLS testing template (NOT used in prod, prod uses host Nginx)
    docker-compose.yml    <- dev
    docker-compose.prod.yml <- prod override
    README.md
  Rules to Follow/
    Master Prompt.md      <- constitution (read first)
  Project Info/
    HANDOFF.md            <- this file
    PLogIt - Product Spec.md       <- the bible
    PLogIt - Build Plan.md         <- slice roadmap (all done)
    PLogIt - Process Log.md        <- per-slice decisions and roadblocks
    PLogIt - Knowledge Base.md     <- consolidated learnings + standing rules
    PLogIt - Planning Questions.md <- decision provenance (Q&A rounds 1-2)
  Miscellaneous/
    dev-start.sh          <- starts Postgres + backend + frontend
    test_api.py           <- HTTP-level API tests (129 checks)
```

`PLogIt/` is the project. Everything else in the workspace is planning and reference.

---

## 3. What works right now

The app is feature-complete for v1. A user can:

1. Register and login (with a theme toggle on the login screen).
2. Dashboard: view engagement cards in a responsive grid, create or join engagements.
3. Engagements page: dedicated CRUD surface (create, join, card grid).
4. Settings page: profile info and theme preference.
5. Create an engagement (you become the leader).
6. Generate a join code and share it.
7. Another user joins through a code as a member.
8. View an engagement with tabs: Overview, Scope and RoE, Team, Audit (leader-only).
9. The leader can edit all fields, remove members, and transfer leadership.
10. Leader: export an engagement as PDF, JSON, or CSV (marks included logs as EXPORTED, audit-logged).
11. Click into the Logs board from an engagement.
12. Create activity logs (markdown content, code blocks, tags).
13. Filter logs (text search, activity type, outcome, review state).
14. View log detail with rendered markdown, attachments, and edit history.
15. Submit logs for review (draft to submitted). The author or the leader can submit.
16. Leader: review queue, approve and reject (reject requires a comment).
17. Upload image attachments (PNG, JPEG, GIF, WebP, Tika-validated).
18. View the audit trail (leader-only, transitions logged).
19. Toggle light and dark theme (sidebar, settings, or login screen).
20. Log out.

---

## 4. Tech stack (verified against the code July 2026)

| Layer | Choice |
|-------|--------|
| Backend | Spring Boot 3.5.16, Java 21, Maven |
| ORM | Hibernate through Spring Data JPA |
| Database | PostgreSQL 16 |
| Security | Spring Security, session cookies (httpOnly and Secure and SameSite), bcrypt, CSRF |
| Frontend | React 18, Vite 6, TypeScript, Tailwind 3.4 |
| Markdown | flexmark-java and OWASP HTML Sanitizer (backend) |
| Attachments | Apache Tika magic-byte validation |
| PDF | OpenHTMLtoPDF 1.1.40 |
| CI/CD | GitHub Actions: Semgrep and Trivy (blocking), mvn verify, npm lint and build, SSH deploy on main |
| Deploy | DigitalOcean droplet, Docker Compose (app containers), host Nginx (reverse proxy and SSL termination). The Caddyfile is local and dev TLS testing only. |

**Design patterns shipped (all six):** Repository (DIP, Slice 1), DTO and Mapper (SRP, Slice 3), Specification (Slice 6), State (Slice 7, extended Slice 10), Observer (Slice 8), Factory and Strategy (Slice 10).

**Test surface:** 76 backend integration test methods (Testcontainers), 129 HTTP-level API checks (test_api.py), ESLint and build for the frontend. All green at the last run.

**Endpoint count:** exactly 24 REST endpoints across 6 controllers.

---

## 5. Key lessons (the ones worth remembering)

1. **`test_api.py` caught a CSRF bug that unit tests, integration tests, and CI all missed.** MockMvc's `.with(csrf())` synthesizes a token and bypasses the real HTTP cookie flow. The fix was `CsrfTokenRequestAttributeHandler` plus a force-resolve filter after `CsrfFilter`.
2. **The versioning-versus-audit split clarifies what Observer is FOR.** Versioning is snapshot-on-write (direct call). Audit is event-driven observer (decoupled, `@TransactionalEventListener` BEFORE_COMMIT).
3. **The security library itself was vulnerable.** OWASP HTML Sanitizer had a CVE. Trivy caught it. Fixed by a version bump.
4. **Dependency hell through transitive namespace collision.** openhtmltopdf under two groupIds with incompatible PDFBox baselines. Fix: exclude by groupId and artifactId.
5. **The invisible app.** styles.css shipped 10 slices without the three `@tailwind` directives, so no utilities were generated. Tailwind fails silently. Process change: grep the compiled output, do not trust exit code 0.
6. **Verify-before-clean ordering.** Cleaning `node_modules` before browser verification breaks the dev server. Verify first, clean last.
7. **LazyInitializationException.** Controllers mapped JPA entities to DTOs after `@Transactional` returned, so the lazy proxies blew up. Caught by integration tests, not by compilation.

---

## 6. Standing rules (never bend)

These are durable constraints. Never violate them.

- **No comments in source code.** Files `.java`, `.tsx`, `.ts`, `.jsx`, and `.css` must contain zero comments. Config files keep comments.
- **Honesty about status.** Never claim incomplete work as done. Defer explicitly.
- **Package root:** `io.muzoo.ssc.plogit`.
- **Three test gates before sign-off:** `mvn test`, ESLint and build, `test_api.py` (for auth or state-changing endpoints).
- **Check `mvn dependency:tree`** for transitive conflicts when you add a library that shares a namespace with an existing dep.
- **Verify compiled CSS contains expected Tailwind utilities** after any styles.css or tailwind.config change. Grep the built output, do not trust exit code 0.
- **Tailwind token-namespace class naming:** `colors.bg.header` produces the class `bg-bg-header`, not `bg-header`. The wrong one silently generates nothing.
- **Verify in browser before cleaning artifacts.** Clean is the last action before handoff, not before verification.
- **Clean all build artifacts** (backend/target, frontend/node_modules, frontend/dist, frontend/tsconfig.tsbuildinfo, .DS_Store, backend/attachments, .env) before handoff.
- **Verify external references** (action versions, library coordinates, command flags) against primary sources before claiming they work.

---

## 7. What to deliberately drop (old context, no longer relevant)

- The project was originally a university course deliverable. That framing is gone. It is now a personal project.
- The original folder was `Planning/Repo/` at a different path. The project now lives at `PLogIt/` (the `PLogIt Source/` name used in older drafts was retired).
- ControlMap (the companion auditor project by another student) is no longer in the workspace. The pipeline framing still applies conceptually, but the shared-workspace reference is gone.
- The "Previous HW Steps" and presentation artifacts have been removed.
- The copy-paste workflow (user copies from `Planning/Repo/` to their real repo) is obsolete. `PLogIt/` is the real repo now. Edit it directly.
- The "pending push" items from the old handoff (README, screenshots, badge fix) are all present in the tree. Nothing is pending.

---

## 8. Production architecture (confirmed with the user July 2026)

This was initially flagged as "gaps" by a repo-only audit. The user clarified the actual deploy topology, so most of it is working-as-intended, not broken.

**How prod actually runs:**
- The app containers (db, backend, frontend) run through Docker Compose on a DigitalOcean droplet.
- `docker-compose.prod.yml` binds the backend and frontend ports to `127.0.0.1` (loopback only). This is deliberate.
- **Nginx is installed directly on the droplet host, outside Docker.** It is the production reverse proxy: it terminates SSL (Let's Encrypt) and proxies to the loopback-bound containers. It is NOT the same as `frontend/nginx.conf`, which is only the in-container SPA static server.
- The `Caddyfile` in the repo is a local and dev TLS testing template (mock TLS for the test_api.py flow). It is not part of production.

**Why the domain and proxy config are not in the repo:**
- The project is open-source and domain-agnostic. Anyone can host it under their own domain. `APP_DOMAIN` and `ACME_EMAIL` are empty in `.env` and `.env.example` by design.
- The real domain (`plogit.weilong.dev`) and the Nginx site config live on the server, outside the repo, because they are instance-specific.

**Real items still worth attention:**
1. **The README's architecture section still says Caddy is the prod proxy.** That is stale. Production uses host-installed Nginx. The README should be corrected to describe the actual two-tier setup (host Nginx for proxy and TLS, Docker Compose for the app, loopback binding between them). Not yet fixed.
2. **The CI deploy script runs in `~/plogit/Repo` on the server** (per the ci.yml `deploy` job). That path reflects the old folder name. If the server-side clone has been renamed or moved, the deploy step would break. Verify the server path matches before you rely on CD.
3. **Dead frontend dependencies.** `react-hook-form`, `react-markdown`, `rehype-sanitize`, and `zod` are declared in `package.json` but no imports of them were found in `src/`. They were scaffolded in Slice 2 for anticipated use that never landed (LogForm uses plain controlled `useState`, and LogDetail uses `dangerouslySetInnerHTML` with server-sanitized HTML). Safe to remove if you want a lean dependency tree.
4. **Deferred v1 features (documented honestly, not hidden):** secrets detection (E4), scope warning modal (C1), full filter set (date-range and target-text deferred), group-by-target, demo data seeding, objectives list UI, image re-encoding to strip payloads, PDF base64 memory optimization for huge engagements, frontend component tests, author filter UI (the backend supports it, the frontend does not expose it).

---

## 9. First message to send in a new session

Paste this:

> I am continuing work on PLogIt, a pentest logging web app (now a personal project). Read `HANDOFF.md`, `Master Prompt.md`, and `PLogIt - Product Spec.md` in the workspace's Project Info folder. The project lives in `PLogIt/`. All 11 build slices plus the UI overhaul are complete, tests are green, and it is deployed live with CI/CD. Production uses host-installed Nginx for reverse proxy and SSL, and Docker Compose for the app containers (loopback-bound). The Caddyfile is local and dev only. The handoff Section 8 lists the real open items: the README still claims Caddy is the prod proxy (stale, needs fixing), the CI deploy path on the server may reflect an old folder name, and four frontend deps are unused. Confirm you understand the standing rules (no comments in source, three test gates before sign-off, verify compiled CSS after Tailwind changes, verify in browser before cleaning, package root io.muzoo.ssc.plogit), then tell me what you want to work on.

---

*End of handoff. Context is clean for a new session.*

# PLogIt

A web application for pentesters to log engagement activity in a structured, reviewable, auditable way. It captures *what was done* and *what was found*, with evidence, and hands a clean evidence pack to a downstream auditor who decides what matters.

PLogIt sits at the front of a remediation pipeline:

```mermaid
flowchart LR
    A["Pentester (PLogIt)\ncapture activity\n+ evidence pack"] --> B["Auditor\ntriage, assign\nCVSS / CVE / severity"] --> C["Blue Team\nfix"]
```

The pentester stage is PLogIt's job. Severity rating, CVSS, CVE mapping, and client-facing reporting belong to the downstream auditor, not to us. The principle is capture everything, judge nothing: a log is raw activity, and judgment is someone else's job.

Most teams track pentest activity in a notebook or a shared document. PLogIt replaces that with a multi-user system that keeps an audit trail, enforces who is allowed to approve what, versions every edit, and exports an evidence pack a downstream tool can ingest directly instead of retyping.

<p align="center">
  <img src=".github/assets/login.png" width="900" alt="Login page with the PLogIt brand tile and sign-in form, plus a theme toggle" />
</p>

## What it does

PLogIt has two roles, and what you can do depends on yours. Both are per-engagement, so a user's role in one engagement says nothing about their role in another.

Every signed-in user lands on a dashboard of their engagements, each shown as a card with its role badge, status, and due date. From there they open an engagement or join a new one with a short code. The model is Google Classroom: anyone creates an engagement and becomes its leader, members join via a code, and a pentester can be in many engagements across different leaders.

<p align="center">
  <img src=".github/assets/dashboard.png" width="900" alt="Dashboard showing a welcome header and a responsive grid of engagement cards with role and status badges" />
</p>

A member creates activity logs and edits their own while they are still drafts. Each log carries an activity type, a target, the tool used, an outcome, free-form tags, a markdown description and result, an optional code block, and image evidence. Once a log is submitted, the member can no longer edit it. A leader reviews submitted logs from a dedicated queue, approves or rejects them, and a rejection has to carry a comment.

A leader does everything a member does, plus manages the engagement: edits the scope and rules of engagement, generates or regenerates the join code, adds or removes members, transfers leadership, reviews and approves logs, and exports the evidence pack. The rule that matters most is separation of duties: only the leader can approve a submitted log, and that check lives on the server, not in the interface.

<p align="center">
  <img src=".github/assets/engagement-scope.png" width="900" alt="Engagement detail page showing the scope and rules of engagement tab with in-scope targets, allowed and forbidden techniques, and emergency contacts" />
</p>

## The review workflow

A log moves through four states, and the legal moves are defined by a state machine rather than scattered checks. Each state is a class that declares which transitions it allows and who may do them, and a flyweight registry maps the persisted enum to the behavior object. An illegal transition returns 409 and a wrong-role attempt returns 403.

<p align="center">
  <img src=".github/assets/log-submitted.png" width="900" alt="Log detail page showing a submitted log with the leader-only approve and reject action bar" />
</p>

| From | Action | To | Who | Guard |
|---|---|---|---|---|
| Draft | Submit | Submitted | Author or leader | |
| Submitted | Approve | Approved | Leader only | |
| Submitted | Reject | Draft | Leader only | comment required |
| Approved | Export | Exported | Leader only | |
| Exported | Re-export | Exported (unchanged) | Leader only | creates a new audit event |

Only the leader can approve or export, and that check is enforced in the backend. Removing a button from the UI is never what stops an unauthorized action. Re-exporting an already-exported log does not change it, but it does create a new audit event, so the record shows that the pack was regenerated.

## Version history and audit trail

Every log edit snapshots a full copy of the editable fields, not a diff, so the history shows exactly what each version looked like rather than asking the reader to reconstruct it. Any member can see the edit history of a log in the engagement; only the leader sees the system audit trail.

The audit trail is driven by a Spring application event, not by the review service writing to it directly. When a transition happens, the service publishes a `LogTransitionedEvent`, and a `@TransactionalEventListener` running in the same transaction writes the `AuditLog` row. That means the audit write joins the transition's transaction: if the transition rolls back, the audit entry does too, so there are no orphan records describing things that never happened. This is the Observer pattern doing real work, keeping the auditing concern out of the review code.

<p align="center">
  <img src=".github/assets/audit-log.png" width="900" alt="Audit log tab on the engagement detail page, showing transition entries with state changes, actors, and timestamps" />
</p>

## Markdown and attachments

Descriptions and results are written in markdown and rendered to sanitized HTML on read. The backend parses with flexmark-java and strips anything dangerous with the OWASP Java HTML Sanitizer, so the HTML the browser receives has already been cleaned. The stored data stays as the user's raw markdown, which preserves their intent for editing and means every read produces a freshly sanitized result.

Image attachments are validated by their actual bytes, not their filename extension. Apache Tika reads the magic bytes at the start of the file, and only PNG, JPEG, GIF, and WebP are accepted. A file named `exploit.png` that is actually a script is rejected. Files are stored outside the web root, organized by engagement and log.

<p align="center">
  <img src=".github/assets/log-markdown.png" width="900" alt="Log detail page showing rendered markdown description, an outcome badge, and the metadata tiles" />
</p>

## Export

A leader exports an engagement in three formats, and each comes from a strategy selected by a factory keyed on format. Adding a format is a new class and no caller changes.

The PDF is an auditor-facing evidence pack, not a client deliverable. It carries a cover page, the scope and rules of engagement, the chronological logs with their rendered markdown, code blocks, and inlined evidence images, and an appendix with the export counts. The JSON and CSV exports are machine-readable, carrying the raw markdown so a downstream tool can ingest directly. The OpenHTMLtoPDF library renders the HTML template, and it reuses the same sanitized markdown the app produces, so there is one sanitization path feeding both the UI and the PDF.

Export is a state-changing action: every approved log included in the pack is marked as exported and becomes immutable, and each one fires an audit event. That is why the endpoint is a POST rather than a GET.

<p align="center">
  <img src=".github/assets/export-modal.png" width="900" alt="Export modal on the engagement detail page, showing the format selector and the include-exported toggle" />
</p>

## Architecture

PLogIt is two separate applications. The frontend is a React single-page app built by Vite. The backend is a standalone Spring Boot REST API. They talk over HTTP and JSON and are never merged into one process.

The Vite dev proxy serves the API under the same origin as the SPA during development. In production, an optional `tls` Compose profile starts Caddy for automatic HTTPS: requests to `/api/*` reach Spring Boot unchanged and everything else goes to the nginx-hosted SPA. The browser uses one origin, so CORS is unnecessary and session cookies remain first-party. The backend and frontend also retain their loopback ports for deployments with an existing host-level proxy. Spring's production `server.forward-headers-strategy: framework` honors Caddy's `X-Forwarded-*` headers. See **Production HTTPS** below.

```mermaid
flowchart TD
    Browser["Browser\nReact SPA (Vite build)"] -->|"same-origin HTTPS"| Proxy
    Proxy["Caddy (optional TLS profile) or host proxy\nautomatic HTTPS\nSPA + /api routes"] --> Frontend
    Proxy --> Backend
    Frontend["Frontend container"] --> Backend["Backend (Spring Boot)\nSpring Security (session + CSRF)\nJPA / Hibernate, Flyway"]
    Backend --> DB[("PostgreSQL")]
```

Inside the backend, a request flows through a controller that speaks DTOs, into a service that holds the domain logic, down to a Spring Data JPA repository and PostgreSQL. A mapper layer keeps the JSON wire format separate from the JPA entities.

## Design patterns

A handful of patterns carry real weight in the code rather than sitting in comments. The review workflow is a State machine, where each state class owns its own transitions and role checks, and a flyweight registry reconciles the persisted enum with the behavior objects. Export uses a Factory keyed on format together with a Strategy per format. Audit logging is an Observer: the service publishes an event and a listener records it, which keeps auditing out of the workflow code. Data access goes through Spring Data JPA repositories, and a DTO and mapper layer keeps the API contract independent of the persistence model. Composable log filters are built with the Specification pattern, chaining predicates with `.and()`. These line up with SOLID in practice: thin controllers, translation isolated in mappers, and behavior that extends by adding a class rather than editing an existing one.

## Tech stack

| Area | Technology |
|---|---|
| Backend | Spring Boot 3.5.16, Java 21, Maven |
| Persistence | Spring Data JPA / Hibernate, PostgreSQL, Flyway |
| Security | Spring Security, session cookies (httpOnly, Secure, SameSite), CSRF token, BCrypt |
| Markdown and uploads | flexmark-java, OWASP Java HTML Sanitizer, Apache Tika (magic-byte detection) |
| Reporting | OpenHTMLtoPDF 1.1.40 (PDF), Jackson (JSON), hand-rolled RFC 4180 CSV |
| Frontend | React 18, Vite 6, TypeScript, Tailwind CSS 3.4 |
| Containers | Docker multi-stage builds, non-root images |
| Proxy and TLS | Optional Caddy production profile (HTTPS and security headers), or an existing host proxy. |
| CI/CD | GitHub Actions with Semgrep (SAST) and Trivy (CVE, secret, misconfiguration scanning), both blocking |

A few of these were deliberate. The frontend is a Vite SPA rather than Next.js, on purpose, so no frontend framework can quietly become a second backend; the backend is Spring Boot and only Spring Boot. Authentication uses stateful session cookies with CSRF protection rather than JWTs, which suits an app served from a single origin and avoids the storage and expiry concerns tokens introduce. The UI is written from scratch against a token-based design system rather than pulled from a component kit, so the look is purpose-built for a security tool rather than a generic framework skin.

## Security

Security is the point of the project, so it runs through the whole thing. Authentication uses Spring Security with session cookies and BCrypt password hashing. Authorization is enforced on the server: every engagement-scoped endpoint checks membership, non-members get a 404 rather than a 403 so engagement existence cannot be enumerated, and leader-only actions use a dedicated assertion. The security filter chain requires authentication for everything except health, login, and registration.

CSRF protection uses a cookie-based token that the SPA reads and echoes back in a header on unsafe requests. Markdown is sanitized on read with a strict allowlist, so script tags, javascript URLs, and event handlers are stripped before the HTML reaches the browser. Image uploads are validated by magic bytes, not extension. The CI scanners are treated as gates: when Semgrep or Trivy flags something, it gets fixed rather than suppressed. During development Trivy flagged a CVE in the OWASP HTML Sanitizer itself, the very library added for protection, and it was patched by a version bump.

### Authentication request limits

The backend limits each client IP to **20 login attempts per minute** and **5 registration attempts per 10 minutes** by default. Requests over the limit receive HTTP `429 Too Many Requests` with a `Retry-After` header. The limits also apply to malformed requests; clients cannot avoid them by submitting invalid JSON. Login and registration have separate budgets.

Valid registration attempts return the same generic HTTP `202 Accepted` response whether the address is new or already registered. Registration does not create a signed-in session; the user proceeds to the login page. Authentication failures keep the same generic error for unknown emails and incorrect passwords.

The in-memory limiter bounds its tracked client-IP state and is intended for the project's single backend instance. For multiple backend replicas, enforce a shared limit at the reverse proxy or replace the local store with a distributed one. In production, use Caddy or a **trusted** host proxy that removes untrusted forwarded IP headers; rate limiting uses the servlet's resolved remote address rather than client-supplied `X-Forwarded-For` values. The budgets can be adjusted with `plogit.auth.rate-limit.login-attempts` and `plogit.auth.rate-limit.registration-attempts` in Spring configuration.

## Running it locally

Docker and Docker Compose are the only things you need installed. Java and Node both run inside the build.

```bash
# 1. Configure the environment
cp .env.example .env
# edit .env: set POSTGRES_PASSWORD and APP_SESSION_SECRET

# 2. Start Postgres, the backend, and the frontend
docker compose --profile app up --build
```

The frontend is served at http://localhost:5173, the API at http://localhost:8080, and Postgres listens on 5432. A seed user is created in the dev profile for immediate login. `.env.example` documents every configuration key.

To run the backend and frontend directly during development, start Postgres with `docker compose up -d db`, then run the backend with `mvn spring-boot:run` and the frontend with `npm install && npm run dev` in the `frontend/` directory.

## Production HTTPS

Production deployments can use the included Caddy reverse proxy or keep an existing host-level reverse proxy. **Caddy is opt-in**, so a deployment update will not unexpectedly take over ports 80 and 443.

Before enabling Caddy, point your domain's DNS A/AAAA records to the server, open inbound TCP 80/443 (and optionally UDP 443 for HTTP/3), and ensure no other service owns those ports. In the production server's existing `.env` (never commit this file), set:

```dotenv
APP_ENABLE_TLS=true
APP_DOMAIN=plogit.example.com
ACME_EMAIL=admin@example.com
```

The GitHub Actions CD step reads `APP_ENABLE_TLS=true` and starts the `tls` profile on subsequent deployments. You can also start or validate it manually:

```bash
docker compose -f docker-compose.yml -f docker-compose.prod.yml --profile app --profile tls config --quiet
docker compose -f docker-compose.yml -f docker-compose.prod.yml --profile app --profile tls up -d --build
APP_DOMAIN=plogit.example.com sh scripts/verify-https.sh
```

The verification script checks HTTPS, unchanged `/api` routing, SPA deep links, HTTP-to-HTTPS redirects, HSTS, CSP, other security headers, and the `Secure`, `SameSite=Lax`, JavaScript-readable CSRF cookie. It must be run against the actual public domain once DNS and TLS are configured; CI validates the Caddyfile and Compose configuration, but cannot prove a public certificate was issued. After logging in, check that the `JSESSIONID` cookie is `Secure`, `HttpOnly` and `SameSite=Lax`.

The Content Security Policy allows the existing Font Awesome and Google Fonts stylesheet/font origins while restricting scripts and API requests to the same origin. Caddy passes the `X-Forwarded-Proto` and `X-Forwarded-Host` headers to Spring, whose production profile honors them. Certificates live in persistent Caddy volumes. Backend (`127.0.0.1:8090`) and frontend (`127.0.0.1:8081`) remain loopback-only, and the production database port is not published.

If a host proxy already handles TLS, keep `APP_ENABLE_TLS=false`. The host proxy must forward `/api/*` to port 8090 **without stripping `/api`**, route all other SPA paths to port 8081, forward the original scheme/host, enforce HTTPS with equivalent security headers, and preserve first-party cookies. Run the same verification script against that domain.

### SSH host-key verification for production deployment

The CD job requires a GitHub Actions repository secret named `PROD_SSH_HOST_FINGERPRINT`. Read the public host-key fingerprint **from your trusted production server console** (or another independently verified administrative channel), not from an unverified `ssh-keyscan` connection:

```bash
ssh-keygen -lf /etc/ssh/ssh_host_ed25519_key.pub -E sha256
```

Copy only the `SHA256:...` value into **GitHub repository → Settings → Secrets and variables → Actions → New repository secret**, with the name `PROD_SSH_HOST_FINGERPRINT`. Verify that it belongs to the actual host key presented by SSH. If the server uses a different host-key algorithm, inspect the matching `/etc/ssh/ssh_host_*.pub` key instead. A legitimate host-key rotation requires updating this secret from a newly verified fingerprint.

Deployment **fails closed** when the fingerprint secret is missing or malformed; appleboy/ssh-action checks that the remote SSH host presents the pinned key. The deploy continues checking out `DEPLOY_SHA` (the exact CI-tested commit), not the moving `main` branch. GitHub Actions pins Trivy to v0.76.1 and checks the release checksum manifest against a digest committed in the workflow before executing the downloaded scanner. The vulnerability database remains updateable, so future findings can still block CI.

## Continuous integration

Every push runs a GitHub Actions pipeline. The backend job builds and runs the full test suite with Testcontainers against a real PostgreSQL on Java 21. The frontend job installs dependencies, lints with ESLint, type-checks and builds with Vite. Two security scanners run and can fail the build: Semgrep with the default, OWASP Top Ten, and secrets rulesets, and Trivy across dependencies, secrets, and misconfiguration at high and critical severity. All GitHub Actions are pinned to full commit SHAs, not mutable tags, after Semgrep flagged mutable-tag supply-chain risk in the pipeline itself.

## Testing

The backend has 76 integration test methods running against a real PostgreSQL in CI via Testcontainers. They cover authentication, engagement authorization and isolation, log CRUD and its edit constraints, the full review state machine including role gating and the export transition, markdown sanitization, attachment upload and download authorization, version history, and the audit trail. A separate HTTP-level test script exercises the real wire contract including the CSRF cookie flow, and it caught a Spring Security 6 deferred-token bug that the MockMvc suite could not, because MockMvc synthesizes CSRF tokens and bypasses the real cookie flow.

## Project layout

| Path | What lives there |
|---|---|
| `backend/` | Spring Boot 3.5.16, Java 21, Maven |
| `backend/src/main/java/io/muzoo/ssc/plogit/` | `config/`, `domain/`, `domain/review/`, `repository/`, `security/`, `service/`, `service/export/`, `web/`, `web/dto/`, `web/exception/` |
| `backend/src/main/resources/` | `application.yml`, Flyway `db/migration/` |
| `backend/src/test/java/` | Integration tests (Testcontainers, real PostgreSQL) |
| `frontend/` | React 18 + Vite 6 + TypeScript |
| `frontend/src/` | `components/`, `hooks/`, `lib/`, `pages/` |
| `.github/workflows/ci.yml` | CI pipeline (Semgrep + Trivy, blocking) |
| `docker-compose.yml` / `docker-compose.prod.yml` | Dev and prod Compose stacks |
| `Caddyfile` | Optional production HTTPS proxy, API routing and response security headers |
| `.env.example` | Documented configuration keys |

The codebase has 9 JPA entities, 9 repositories, 24 REST endpoints, and about 9 React pages across the engagement and log workflows.

## About this project

PLogIt is an open-source project. The package name (`io.muzoo.ssc.plogit`) is historical and kept for continuity.

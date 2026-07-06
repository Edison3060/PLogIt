# PLogIt

**PLogIt** (*PenTest Logging It*) is a web application for pentesters to log their engagement activity in a structured, reviewable, auditable way. It captures *what was done* and *what was found*, with evidence, and produces an evidence pack that a downstream auditor uses to decide severity, map to controls, and assign remediation.

PLogIt sits in a remediation pipeline:

```
Pentester (PLogIt)  ->  Auditor  ->  Blue Team
capture activity          triage, assign        fix
+ evidence pack           CVSS/CVE/severity
                          + final report
```

The pentester stage is PLogIt's job. Severity rating, CVSS, CVE mapping, and client-facing reporting belong to the downstream auditor, not us.

## Features

- **Engagements** with leader/member roles (Google Classroom style join codes).
- **Activity logs** with full provenance: author, timestamps, target, tool, outcome, tags, markdown description/result, code blocks, image evidence.
- **Review workflow**: Draft -> Submitted -> Approved -> Exported, with mandatory rejection comments and full edit history.
- **Audit trail**: append-only, separate from activity logs.
- **Export**: PDF (auditor evidence pack) and JSON/CSV (machine-readable for downstream tool ingestion).
- **Theme**: light/dark with blue accent.
- **Scope-aware**: warns on out-of-scope targets.

## Tech stack

| Layer | Choice |
|-------|--------|
| Backend | Spring Boot 3.x (Java 21 LTS), Maven |
| Persistence | Spring Data JPA / Hibernate, Flyway migrations |
| Security | Spring Security (session cookies, bcrypt) |
| Database | PostgreSQL |
| Frontend | React + Vite + TypeScript |
| UI | Tailwind CSS + Shadcn/ui |
| DevOps | Docker, GitHub Actions (Trivy + Semgrep, blocking) |
| Deploy | VPS, Caddy reverse proxy, automatic TLS (Let's Encrypt) |

## Local development

Prerequisites: JDK 21, Node 20+, Docker Desktop.

```bash
# 1. Clone and enter
git clone <repo-url> && cd <repo>

# 2. Set up environment
cp .env.example .env
# edit .env: set strong POSTGRES_PASSWORD and APP_SESSION_SECRET

# 3. Start Postgres
docker compose up -d db

# 4. Backend (from repo root)
cd backend && mvn spring-boot:run

# 5. Frontend (new terminal, from repo root)
cd frontend && npm install && npm run dev
```

- Backend: http://localhost:8080
- Frontend: http://localhost:5173

## Production deploy

See `docker-compose.prod.yml` and `Caddyfile`. Requires `APP_DOMAIN` and `ACME_EMAIL` in `.env`.

```bash
docker compose -f docker-compose.yml -f docker-compose.prod.yml \
    --profile app up -d --build
```

## License

All rights reserved.

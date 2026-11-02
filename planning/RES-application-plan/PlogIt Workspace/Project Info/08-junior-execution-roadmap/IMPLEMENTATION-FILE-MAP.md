# PLogIt canonical implementation and file ownership

All paths below are relative to **Edison3060/PLogIt**, not the read-only source snapshot under RES. Future contract fixtures mentioned in steps are proposed paths, not established source modules.

| Path | Ownership | Roadmap use |
|---|---|---|
| `backend/pom.xml` | Java/Boot dependency and test tooling | Phase 02, 10 |
| `backend/src/main/resources/db/migration/` | Flyway, PostgreSQL constraints | Phase 02, 09 |
| `backend/src/main/java/io/muzoo/ssc/plogit/web/AuthController.java` | Auth/registration/session HTTP entrypoint | 03 |
| `.../service/EngagementService.java`, `.../web/EngagementController.java` | Engagement domain and permission checks | 03–04 |
| `.../service/JoinCodeService.java`, `.../service/MembershipService.java` | Join/roles/member mutations | 04 |
| `.../service/LogService.java`, `.../service/LogReviewService.java` | Log editing and review transition owner | 05 |
| `.../service/LogVersionService.java`, `.../service/AuditEventListener.java` | Edit history, transactional audit | 05, 09 |
| `.../service/ExportService.java` | **Log inclusion and state-changing export** | 05–06 |
| `.../service/export/JsonReportStrategy.java` | **Real unversioned JSON producer** | 06 |
| `.../service/export/PdfReportStrategy.java` | Evidence PDF output | 07 |
| `.../service/AttachmentService.java`, `.../web/AttachmentController.java` | Attachment type, storage, scope | 07 |
| `.../service/MarkdownService.java` | Untrusted Markdown sanitization | 07 |
| `frontend/src/App.tsx`, `frontend/src/components/AppShell.tsx` | Browser routes/navigation | 08 |
| `frontend/src/pages/`, `frontend/src/components/engagement/` | Existing user journeys | 04–08 |
| `frontend/src/lib/api.ts`, `frontend/src/hooks/useAuth.ts` | Client API, CSRF, session | 03, 08 |
| `backend/src/test/java/io/muzoo/ssc/plogit/` | Backend integration, negative and export tests | all |
| `.github/workflows/ci.yml`, `docker-compose*.yml`, `*/Dockerfile` | Build, scans, deployment preflight and health | 02, 10–11 |

**Expand `...`** as `backend/src/main/java/io/muzoo/ssc/plogit`. Only PLogIt owns changes to its evidence export. JudgeIt owns its importer and finding IDs; PatchIt owns ticket IDs; GaugeIt owns snapshots. Downstream consumers retain incoming source UUIDs without reassignment.

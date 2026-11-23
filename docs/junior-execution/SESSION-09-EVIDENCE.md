# Session 09 | Log authoring and review

Scope is JR-081–JR-090 on `work/plogit-phase-05`, stacked on Phase 04 at `456d8487fc970b1dff829576f6dd9e5588dca67b`. The Phase 04 acceptance gate remains pending. Independent log lifecycle tests use synthetic data. Reviewer is Builder Lead. Owner sign-off remains pending.

## JR-081

At Phase 04 source baseline `456d8487fc970b1dff829576f6dd9e5588dca67b`, `web/LogController.java` dispatches POST for creation, PUT for edits and GET for detail and history. `service/LogService.java` resolves the engagement and authenticated actor. `domain/LogEntry.java` generates and persists the UUID. Creation sets DRAFT and saves snapshot 1 via `LogVersionService`. Edits require active membership, author or leader, and DRAFT, then persist a snapshot using the authenticated editor. `LogCreateRequest` and `LogUpdateRequest` expose only editable content fields.

The roadmap calls for live ownership and existing coverage. `LogAuthzIntegrationTest`, `LogVersioningIntegrationTest`, `BadRequestIntegrationTest` and `CsrfIntegrationTest` cover isolated parts of the flow. Prior Phase 04 CI is baseline context. This inventory was verified by reading canonical source and tests. The July product text and copied RES implementation are historical reference material. No test execution or application change was needed for this inventory.

## JR-082

Authenticated POST `/api/engagements/{engagementId}/logs` with CSRF takes required `activityType`, `title`, Markdown `description` and `result`, and `outcome`, plus optional `target`, `toolUsed`, `tags`, `codeBlock` and `codeLanguage`. It returns HTTP 200 and a server-generated stable UUID, author, engagement, and DRAFT state. PUT `/api/logs/{logId}` uses the same editable fields as optional updates. A null update field leaves its previous value. Content changes and snapshots commit together. GET detail and history return current content and ordered snapshots with editor IDs.

Expected denials include HTTP 400 for invalid activity, 409 for an active non-author nonleader's draft edit or an edit outside DRAFT, 404 for a nonmember, and 403 for missing CSRF. Identity, editor and review state are server-owned. The historical product spec §§4–5 and 8–9 describe client-generated UUIDs, target scope checks and post-approval leader edits. The November suite rebaseline confirms server-side UUID persistence, and the canonical edit guard and JR-082 require DRAFT. Target scope enforcement and any post-approval editing policy require an owner decision before changing behavior. The app's target field accepts optional text up to 255 characters. This checkpoint specifies the contract and makes no runtime claim.

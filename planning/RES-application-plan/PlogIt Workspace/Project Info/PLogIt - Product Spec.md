# PLogIt: Product Specification (v2)

> **2026-11-02 REBASELINE:** This document preserves July context. PLogIt source is imported unchanged at main commit 28aa156; actual Spring Boot 4.1.1 (not 3.5.16), Java 21, React 18/Vite 6 and PostgreSQL 16. The latest checked main CI passed its five validation jobs but CD failed the PROD_SSH_HOST_FINGERPRINT check. Deployed state of this commit is NOT verified. Proposed code changes belong ONLY in the new Markdown plan; source code is frozen in this research pass. See [suite research](../../Suite%20-%20Research%20Refresh%202026-11-02.md), [implementation roadmap](../../Suite%20-%20Modernization%20Roadmap.md), and [PLogIt proposed update plan](../PLogIt/docs/PLOGIT-RESEARCH-UPDATE-PLAN-2026-11.md).

> **Status:** Locked product decisions. The build is complete and deployed. This document is the reference for what was built and why.
> **What this is:** The consolidated, reconciled product spec.
> **Last updated:** 2026-07-11 (the project transitioned to a personal project, but the product decisions did not change).

---

## 1. Product summary

**PLogIt** is a SaaS web application for pentesters to log their engagement activity in a structured, reviewable, and auditable way. It serves the **pentester stage** of a remediation pipeline:

```
Pentester (us)  ->  Auditor  ->  Blue Team
capture activity     triage, assign        fix
+ evidence           CVSS/CVE/severity
                     + final report
```

The job of PLogIt is **thorough, honest capture**, not severity rating and not client-facing reporting. The leader curates and exports an evidence pack (PDF) that the downstream auditor uses to decide what matters.

### Design principles
1. **Capture everything, judge nothing.** Logs are raw activity. Severity and CVE are someone else's job.
2. **Pentester-speed first.** Logging must be faster than a notebook, or people will not use it.
3. **Integrity over convenience.** Auto-generated fields are immutable, every edit is versioned, and every action is audited.
4. **Scope is sacred.** The app actively warns when activity drifts out of scope. This is legal protection for the pentester.

---

## 2. Locked decisions

| # | Decision | Choice |
|---|----------|--------|
| A1 | Scope | **Logs only.** No findings object. The PDF is an evidence pack for the auditor. A client report is out of scope (someone else's job). |
| A2 | Terminology | **Engagement** (replaces "goal/contract" everywhere). |
| A3 | Scale / deploy | **Any size, SaaS, your own VPS.** Flat multi-tenant (no Org entity, Google Classroom model, see section 2.1). |
| A4 | Review workflow | **Full state machine:** `Draft -> Submitted -> Approved -> Exported`. |
| B | Log fields | All kept (see section 4). Severity is deferred and is not a log property. |
| C1 | Scope enforcement | **Real-time warn** on an out-of-scope target at log-entry, plus a dashboard summary. The leader manages the IP and domain allow-list. |
| C2 | RoE | All four: testing hours, allowed and forbidden techniques, emergency contacts, and explicit out-of-scope. |
| C3 | Data retention | **Archive read-only forever.** No auto-purge, no deletion. |
| D3 | Engagement board edits | **Leader-only.** Members contribute through logs, and the leader promotes the relevant info to the board. |
| D4 | Objectives | **Yes, leader-editable.** Track engagement progress. |
| D5 | Filters | All except severity (which does not exist on logs). |
| D6 | Comments | **Only on the rejection path** (the leader sends it back with a reason). Baked into the state machine, not a standalone feature. |
| D7 | Notifications | **None** for v1. |
| D8 | Grouping | Chronological (default) plus a group-by-target toggle. |
| D9 | Leader duties | Review and approve logs, assign members, and final sign-off (archive). |
| D10 | PDF | **Auditor-facing evidence pack.** See section 7. |
| E1 to E4 | Security | Audit log (required), version history (required), strict sanitization (required), secrets-detection warn (if feasible). |
| F2 | Auth | Email and password only (v1). |
| Roles | Hierarchy | **Leader and Member only.** No admin, viewer, or client roles. |

### 2.1 Multi-tenancy model: "Google Classroom" (resolved round 2)

**There is no Organization or Account entity.** Engagements are independent, top-level entities, like Google Classroom classes.

- **Anyone** with an account can create an Engagement, which makes them its **Leader**.
- The leader **invites members** through a short **join code** (for example `PL0G-7X2K`) or by email.
- A pentester can be a member of **many engagements** across different leaders and firms (contractor-friendly).
- **Data isolation is by membership:** a user can only see engagements they are in. Same security outcome as an Org layer, scoped one level down.
- **The dashboard is all your engagements** (any you lead plus any you are a member of).
- **No firm-level admin in v1.** If a real company hires a new pentester, they manually add them to each engagement through a code. An optional Org layer can be added *above* engagements later if real firms demand it. The schema stays compatible.

**Join code mechanics:**
- Generated by the leader from the engagement's Leader Board.
- **Regeneratable:** the old code stops working on regen.
- **Optionally expiring** (for example, valid 7 days). Defer the expiry UI to v1.1 and ship non-expiring codes first.
- Entering a code on the dashboard joins that engagement as a **member**.
- **Code-join always grants the member role, never leader.** Transferring leadership is a separate explicit action (this avoids code-brute-force takeover risk).
- The leader can **revoke membership** (remove a member from the engagement).

**Leader transfer:**
- A leader who leaves an engagement **must transfer leadership** to an existing member first. They cannot orphan an engagement.
- This is a distinct action on the Leader Board (select a member, then "Make Leader"). The outgoing leader becomes a member (or leaves entirely).
- Only the present leader can perform it. Recorded in the audit log.

### Deferred or out of scope (v1)

- Findings object, severity, CVSS, and CVE: the auditor's job, not ours.
- Client-facing report generation.
- Tool auto-ingest (XML parsing): deferred, see section 11.
- Notifications and @mentions.
- Offline or air-gapped support.
- Mobile (desktop-only, but build responsive anyway).
- SSO, MFA, and passkeys.
- External references field on logs.
- Reusable activity templates: revisit after MVP.

---

## 3. Tech stack

| Layer | Choice |
|-------|--------|
| Backend | **Spring Boot** |
| ORM | **Hibernate** (Spring Data JPA) |
| Database | **PostgreSQL** |
| Security | **Spring Security** |
| Frontend | **React** and **Vite** (SPA, the backend is Spring Boot and separate, so the SSR and backend value of Next.js is unused, which makes Vite the leaner fit) |
| CI/CD | **GitHub Actions** |
| Security scanning | **Trivy** (container and image CVEs) and **Semgrep** (static code analysis) |
| Deploy | VPS with **SSL and a domain** (reverse proxy: Nginx or Caddy and Let's Encrypt) |

---

## 4. Data model

### 4.1 Entities

**User**
- `id`, `email` (unique), `password_hash`, `display_name`, `created_at`
- No global role, the role is per-engagement.

**Engagement**
- `id`, `name`, `description`, `start_date`, `due_date`
- `status`: `active` or `archived` (archived means read-only forever, post sign-off)
- `leader_id` points to User (the current leader, changes through transfer)
- `current_join_code` (string, regeneratable, null means code invite is disabled)
- RoE fields: `allowed_hours`, `allowed_techniques`, `forbidden_techniques`, `emergency_contacts`, `out_of_scope` (text or list)
- `in_scope_targets` (list of IPs, domains, or CIDRs, leader-managed)
- `objectives` (list of `{text, status: not-started or in-progress or achieved or failed}`)
- `created_at`, `archived_at`

**EngagementMember** (join table, the membership roster)
- `engagement_id`, `user_id`, `role` (`leader` or `member`), `joined_at`, `joined_via` (code, email_invite, or created), `removed_at` (nullable)
- Constraint: exactly one row with `role=leader` at any time (enforced at transfer).
- A user can be a member of many engagements, and an engagement has many members.

**JoinCode** (audit and history of codes, optional, can be a single field on Engagement instead)
- Tracked for audit if we want history of who generated what code when. Otherwise, just store `current_join_code` on Engagement.

**Log**
- `id` (UUID, client-generated for future offline compatibility)
- `engagement_id`, `author_id`
- **Content fields (member-editable while draft):**
  - `activity_type` (enum, see section 4.2)
  - `title`, `description` (markdown), `result` (markdown)
  - `target` (string, IP, domain, or asset, scope-checked on save)
  - `tool` (string)
  - `outcome`: `in-progress`, `dead-end`, or `confirmed`
  - `tags` (array of strings)
- **Immutable and auto-generated:**
  - `created_at`, `author_id` (never changes)
  - `last_edited_at`, `last_edited_by`
- **Workflow:**
  - `review_state`: `draft`, `submitted`, `approved`, or `exported`
  - `rejection_comment` (nullable, set when the leader sends it back to draft)
  - `rejected_at`, `rejected_by`

**LogVersion** (edit history, E2)
- `id`, `log_id`, `version_number`, `snapshot` (JSON of all editable fields), `edited_by`, `edited_at`

**Attachment**
- `id`, `log_id`, `filename`, `storage_path`, `mime_type`, `uploaded_by`, `uploaded_at`
- Images only (validate mime and magic bytes, not extension).

**CodeBlock** (or embed in Log as a `code` text field and a `code_language`)
- String-only input, rendered as inert formatted text (syntax highlighting is display-only, never executed).

**ScopeWarning** (C1, acknowledged out-of-scope events)
- `id`, `engagement_id`, `log_id`, `target`, `acknowledged_by`, `acknowledged_at`
- Append-only audit record.

**AuditLog** (E1, system meta, separate from activity logs)
- `id`, `actor_id`, `action` (enum: login, join_engagement, edit_log, approve_log, export_pdf, archive_engagement, and so on), `target_type`, `target_id`, `metadata` (JSON), `timestamp`
- Append-only, tamper-evident if feasible (hash chaining).

### 4.2 Activity types (starter dropdown)

```
OSINT / Reconnaissance
Port Scanning
Service Enumeration
Web Application Testing
Vulnerability Scanning
Exploitation
Post-Exploitation / Privilege Escalation
Lateral Movement
Social Engineering
Physical Access
Wireless Testing
Manual Testing
Other
```

Configurable per engagement later, with a hardcoded starter list for v1.

### 4.3 The two status axes (important distinction)

A log has **two independent status fields**, and you should not conflate them:

| Field | Values | Meaning |
|-------|--------|---------|
| `outcome` | `in-progress` / `dead-end` / `confirmed` | **What the activity found.** Did this lead anywhere? A content property. |
| `review_state` | `draft` / `submitted` / `approved` / `exported` | **Where it is in review.** Who can edit it right now? A workflow property. |

Example: a log can be `outcome=confirmed` (a real finding) but still `review_state=draft` (the member has not submitted it yet).

---

## 5. Roles and permissions

Only two roles, **per engagement** (a user's role in one engagement says nothing about their role in another):

| Action | Member | Leader |
|--------|--------|--------|
| View engagement board | yes | yes |
| Edit engagement board (scope, RoE, objectives) | no | yes |
| Join engagement via code | yes (joins as member) | n/a |
| Generate / regenerate join code | no | yes |
| Add / remove members | no | yes |
| Transfer leadership to a member | no | yes (only current leader) |
| Create log | yes (own) | yes (own) |
| Edit own log | yes if `draft` | yes always |
| Submit own log for review | yes | yes |
| Approve / reject log | no | yes (any) |
| Edit approved log | no | yes |
| Export PDF | no | yes |
| Final sign-off (archive) | no | yes |

**The leader is also a member**, so they create logs too. A solo pentester is a self-assigned leader (all permissions trivially hold). **Code-join always grants the member role**, and leadership only transfers through the explicit transfer action.

---

## 6. Boards (screens)

### Board 1: Main Dashboard
What a user sees on login.
- **My engagements:** list with role badge (leader or member), engagement status, and days-to-deadline.
- **"Join an Engagement" entry:** input field for a join code (`PL0G-XXXX`) that joins that engagement as a member.
- **"Create Engagement" button** that creates a new engagement, where the creator becomes its leader.
- **Upcoming deadlines:** sorted by due date across all engagements.
- **Team activity feed:** the latest N logs across my engagements (who, what, when).
- **Widget: Team activity:** logs this week broken down per member (shows who is active and who is quiet).
- **Widget: Days to deadline:** per-engagement urgency at a glance.

### Board 2: Engagement Board
Click into an engagement.
- **Header:** name, dates, leader, member list, and status badge.
- **Leader-editable:** description, in-scope targets, RoE fields, objectives, and manage members.
- **Member-viewable:** everything. Editable: nothing.
- **Scope warning summary:** list of acknowledged out-of-scope events (transparency for the leader).
- **Join code:** the leader sees the current code and a regenerate button (Board 4 is where this lives, and Board 2 shows it read-only for members or hides it).
- **Tabs and sections:** Overview, Scope and RoE, Objectives, Team, and Logs (jumps to Board 3).

### Board 3: Log Board
The main work surface. Everyone lives here.
- **List:** chronological, newest first. Toggle: group by target.
- **Filters (composable):** member, type, target, tag, date range, outcome, and review_state. Full-text search.
- **Row shows:** timestamp, author, title, type, target, outcome badge, and review_state badge.
- **Click a row to see the log detail** (full content, attachments, code, edit history, and rejection comment if any).
- **"New Log" button goes to Board 5.**
- **Leader-only:** review queue (filter preset: `review_state = submitted`) and approve and reject actions.

### Board 4: Leader Board
Leader only. The review and export console.
- **Review queue:** submitted logs awaiting a decision. Approve locks it to `approved`. Reject sends it back to `draft` with a mandatory rejection comment.
- **Member management:** invite and remove members. Generate or regenerate the **join code** (`PL0G-XXXX` format). Copy the code to share it.
- **Transfer leadership:** select a member, then "Make Leader." Confirms the change, and the outgoing leader becomes a member (or leaves). Audit-logged.
- **Export PDF** opens the export dialog (choose filters and sort, then generate the evidence pack).
- **Final sign-off** marks the engagement `archived` (read-only forever). Confirmable and irreversible.

### Board 5: Log Add Board
The form for creating a log.
- **Fields:** activity_type (dropdown), title, description (markdown), result (markdown), target, tool, outcome, tags, image upload, and code block (string and language).
- **Real-time scope check:** if `target` is not in the engagement's `in_scope_targets`, show a warning modal on save. The user must acknowledge it to proceed (creates a ScopeWarning record).
- **Secrets detection:** heuristic scan of description, result, and code. If matches look like AWS keys, tokens, or private keys, show a warning (not a block).
- **Actions:** "Save as Draft" or "Submit for Review".

---

## 7. PDF export (Board 4)

**Purpose:** auditor-facing evidence pack. Honest, thorough, and well-organized. Not a client deliverable.

**Structure:**
1. **Cover page:** engagement name, dates, leader, team members, export date, and export filter summary.
2. **Scope and RoE summary:** in-scope targets, rules of engagement, and emergency contacts. (The auditor needs to know the boundaries.)
3. **Objectives:** list with status (achieved, failed, or in-progress).
4. **Logs:** the chosen set (the leader picks the filter and sort before export), chronological, each with:
   - Timestamp, author, type, target, tool, and outcome badge
   - Description and result (rendered markdown)
   - Code blocks (formatted, syntax-highlighted for readability)
   - Images (inline, captioned)
   - Review state at time of export
5. **Appendix:** out-of-scope acknowledgements (transparency), and full log count versus exported count.

**Not included (out of scope):** severity ratings, CVSS, CVE references, remediation advice, and client branding. That is the auditor's report, not ours.

**Leader controls:** before export, choose which logs to include (default: all `approved`), the sort order, and confirm. Exported logs get `review_state = exported` (they cannot be un-exported without an audit trail entry).

---

## 8. Workflow: the state machine (A4)

```
                  member creates
                        |
                        v
                    +--------+
        +--reject-->| Draft  |<--member edits freely
        |           +----+---+
        |                | member submits
        |                v
        |           +----------+
        |           |Submitted |
        |           +----+-----+
        |    leader      | leader approves
        |    rejects     v
        |  (mandatory   +---------+
        +--comment)-----| Approved |
                        +----+-----+
                             | leader exports PDF
                             v
                        +----------+
                        | Exported  | (immutable, audit-locked)
                        +----------+
```

- **Draft:** the member can edit any content field freely.
- **Submitted:** the member cannot edit. The leader reviews.
- **Approved:** locked. Only the leader can edit (and every edit is versioned).
- **Exported:** included in a PDF. Immutable. Re-exporting creates a new audit event and does not change the log.

**Rejection (D6 comments):** the leader moves `Submitted -> Draft` and must write a `rejection_comment`. The member sees it on the log, fixes it, and resubmits. This is the *only* place "comments" exist. There is no general-purpose commenting.

---

## 9. Security and integrity

- **E1: Audit log.** Append-only, separate from the activity logs. Every state change, edit, export, login, and member change is recorded. Tamper-evident (hash chain) if feasible in v1.
- **E2: Version history.** Every log edit (while draft) and every leader edit (post-approval) stores a full snapshot in `LogVersion`. The "History" tab on each log shows who changed what and when.
- **E3: Strict input sanitization.**
  - Markdown parsed with a strict allowlist (no `<script>`, no `javascript:` URLs, no event handlers). Use a hardened library (for example flexmark-java and the OWASP Java HTML Sanitizer on the backend, and re-sanitize on render).
  - Code blocks rendered as **inert text** (syntax highlighting is cosmetic only, never `eval`'d, never rendered as HTML).
  - Defense in depth: sanitize on input *and* on output.
- **E4: Secrets detection.** Heuristic regex scan (TruffleHog-style patterns, AWS keys, GCP tokens, private key headers, high-entropy strings) on description, result, and code at save time. **Warn, not block.** Pentest logs legitimately contain captured credentials, and the warn catches accidental self-leaks (the tester's own tokens).
- **Auth:** Spring Security, email and password (bcrypt-hashed). Session-based or JWT, decide in architecture. No MFA or SSO for v1.
- **File uploads:** images only. Validate by magic bytes, not extension. Store outside the web root. Re-encode on upload if feasible (strips payloads).
- **CI/CD scanning:** Trivy and Semgrep in GitHub Actions, fail the build on high or critical CVEs in deps or code patterns.

---

## 10. Theme

- **Light and Dark toggle** (user preference, persisted).
- **Blue accent** sub-color in both themes.
- Build with CSS variables and design tokens from day one, so the theme switch is a class swap, not a rewrite.

---

## 11. Deferred items: documented for later

- **Tool auto-ingest (nmap XML and so on):** deferred. Note on the "101% safe" concern: *nothing* is 101% safe, but XML and JSON parsing can be made very safe (XXE-hardened parser with external entities disabled, schema validation, strict field mapping, and no execution). Revisit post-MVP and start with nmap (the most common and best-documented schema). The log schema is already shaped to accept parsed data without reshaping.
- **Findings, severity, and CVSS:** out of scope, the auditor's job. If PLogIt ever expands downstream, the log to finding promotion path is the natural extension point.
- **Reusable templates:** revisit after MVP once we see which activity types get logged most.
- **Notifications, MFA, SSO, offline, and mobile:** all v2 and beyond.

---

## 12. Open questions for next phase

Things to resolve when we move to architecture and implementation:

1. **Auth session model:** server-side sessions (Spring Security default) or JWT? Sessions are simpler and fine for a single-server VPS deploy.
2. **File storage:** local filesystem on the VPS (simplest) or object storage (S3-compatible)? Local is fine for v1 single-server.
3. **PDF generation:** server-side library (for example OpenPDF, Flying Saucer, or Puppeteer through a headless browser)? Need markdown to HTML to PDF with image embedding.
4. **Markdown rendering:** frontend (react-markdown and rehype-sanitize) or backend? Frontend render plus backend storage-sanitize is the standard split.
5. **Multi-tenancy:** **RESOLVED (round 2).** Google Classroom model: no Org entity, flat memberships, join codes. See section 2.1.
6. **Internationalization:** English only for v1?
7. **Demo data:** seed a sample engagement for new users to click around?

---

## 13. Next steps

Recommended order to move forward:

1. **Architecture doc:** system diagram, API surface, entity relationships (ERD), auth flow, and deployment topology.
2. **Database schema:** concrete DDL and Hibernate entities from section 4.
3. **UI wireframes:** the 5 boards, low-fidelity, before any styling.
4. **Project scaffolding:** Spring Boot, React, Vite, and GitHub Actions skeleton.
5. **Implementation slices:** vertical slices (auth, engagement, log, review, export), each one shippable.

---

*End of spec v2. Ready for review: flag anything that still feels off and I will reconcile.*

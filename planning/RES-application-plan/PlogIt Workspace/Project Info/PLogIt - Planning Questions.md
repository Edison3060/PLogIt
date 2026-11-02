# PLogIt: Planning Questions

> **2026-11-02 REBASELINE:** This document preserves July context. PLogIt source is imported unchanged at main commit 28aa156; actual Spring Boot 4.1.1 (not 3.5.16), Java 21, React 18/Vite 6 and PostgreSQL 16. The latest checked main CI passed its five validation jobs but CD failed the PROD_SSH_HOST_FINGERPRINT check. Deployed state of this commit is NOT verified. Proposed code changes belong ONLY in the new Markdown plan; source code is frozen in this research pass. See [suite research](../../Suite%20-%20Research%20Refresh%202026-11-02.md), [implementation roadmap](../../Suite%20-%20Modernization%20Roadmap.md), and [PLogIt proposed update plan](../PLogIt/docs/PLOGIT-RESEARCH-UPDATE-PLAN-2026-11.md).

> **Status:** Closed. All questions are answered and locked into the Product Spec. Kept as the decision provenance record.
> **What this was:** The Q&A rounds (1 and 2) that produced the locked product decisions. Each question has a suggestion box (the recommendation and reasoning) and the user's answer. The answers fed directly into `PLogIt - Product Spec.md`.

---

## Decided

- **Name:** PLogIt.

---

## Group A: The big structural forks (answer first)

### A1. Is this an internal lab notebook or a client deliverable generator, or both?

A day-to-day activity log ("ran nmap, found port 22") is very different from a polished client report ("Critical: CVE-XXXX on host Y"). The "log board" sounds like the former, and the "PDF export" sounds like the latter. Real tools (Dradis, Faraday, PlexTrac) treat these as *separate objects*.

- [X] (a) **Logs only:** the PDF export is just a formatted dump of logs, for internal or audit use.
- [ ] (b) **Logs + Findings:** logs are raw activity, and "findings" are the curated, severity-rated vulns that go into the client PDF.
- [ ] (c) **One unified thing:** figure out the difference later.

> Suggestion: (b) Logs + Findings.
> Logs are *what you did*, and findings are *what you discovered that matters*. If you only have logs, your PDF export becomes a noisy 200-page dump that no client wants to read. Keeping them separate lets the leader cherry-pick logs, promote them to findings, and generate a clean client report. This is also how every serious tool in the space works, and you will regret not having the distinction once a real engagement hits 100 or more log entries. Cost: one extra data object and a "promote to finding" button. Worth it.

**Your answer / notes:** For now it will be log only and later on will think about client report.

---

### A2. "Goal/Contract": what do we call it?

I strongly suggest **Engagement** (industry standard, professional, scales). Alternatives: Project, Assessment, Contract, Operation. "Goal" reads like a to-do app.

- [X] Engagement *(my recommendation)*
- [ ] Project
- [ ] Assessment
- [ ] Contract
- [ ] Operation
- [ ] Other: ___________

> Suggestion: Engagement.
> It is the term used in pentest SOWs, contracts, and reporting frameworks (PCI DSS, PTES). "Contract" is too legal-flavored (and confusing: a contract is the paper, not the work). "Project" is too generic. "Assessment" is fine but slightly clinical. "Engagement" carries the right mix of "scoped body of work" and "professional service", and it scales from solo to enterprise. It also makes your UI copy read like a real product: "New Engagement," "Engagement Dashboard," "Close Engagement."

**Your answer / notes:** Go with how the professionals call it.

---

### A3. Scale and deployment model

This drives auth, MFA, encryption, multi-tenancy, and legal exposure.

**Scale:**
- [ ] Solo only
- [ ] Small teams (2 to 10)
- [ ] Medium teams (10 to 30)
- [ ] Large firms (30 or more)
- [X] Any Size

**Deployment:**
- [ ] **Self-hosted:** each pentest firm runs their own instance (safer for sensitive data).
- [X] **SaaS / cloud:** you host everyone's data (easier onboarding, bigger liability).
- [ ] **Both:** self-host first, offer cloud later.

> Suggestion: Small teams (2 to 10) and self-hosted first.
> Pentest data (target IPs, creds, vulns, OSINT) is radioactive. Storing it for strangers makes *you* a target and a legally responsible party. Self-hosting flips that: each firm owns their own risk, and you ship software, not liability. It also simplifies your MVP enormously (no multi-tenant billing, no SOC2, no GDPR-as-controller). Build for small teams because that is 90% of pentest crews. Cloud or SaaS can be a v2 once you have product-market fit. Design the architecture to *allow* cloud later (a multi-tenant-ready schema) without building it now.

**Your answer / notes:** Any size is possible, for the Deployment we will use SaaS since this my project and that is the requirement.

---

### A4. The "approval" workflow: what did you actually mean?

Re-reading the spec: *"can only edit description and result AND should only be edited by the leader from the next board."* I think you want a state machine for each log. Pick the model that fits.

**Option 1: Simple lock:**
- Members edit freely until the leader "locks" the log. After lock, only the leader can edit.

**Option 2: Full state machine:**
- `Draft` to `Submitted` to `Approved (locked)` to optionally `Exported`.
- Members edit freely while Draft. Once Approved, only the leader can edit.

- [ ] Option 1 (simple lock)
- [X] Option 2 (full state machine)
- [ ] Something else: ___________

> Suggestion: Option 2 (full state machine).
> This is literally what your spec describes once you untangle it: there is clearly a "member hands off to leader" step, and your typo-fix-only rule only makes sense if there is a locked state to protect. The state machine also gives you free superpowers later: filter the log board by status, build a "Submitted: awaiting review" queue for leaders, show progress on the dashboard, and lock logs permanently once exported into a client report (audit trail). The `Submitted` state is the key one: it lets a member say "I am done with this" without bugging the leader, and the leader can batch-review. Cost: a single `status` enum column. Trivial.

**Your answer / notes:** Full state sounds professional.

---

## Group B: The log entry itself (data model)

A proposed superset of fields for a single log entry. Mark what to keep, drop, or add.

**Auto-generated (cannot be edited):**
- [Yes] Timestamp (created)
- [Yes] Author
- [Yes] Last-edited-by
- [Yes] Edit history / version trail
- [Yes] Unique ID

**Required manual input:**
- [Yes] Activity type (OSINT, nmap, Burp, manual testing, and so on): dropdown
- [Yes] Short title
- [Yes] Description
- [Yes] Result / outcome

**Strongly suggested (optional but recommended):**
- [Yes] **Target / host:** IP, domain, or asset *(critical for filtering and scoping)*
- [Yes] **Tool used** (separate from "type")
- [Yes] **Status:** in-progress / dead-end / confirmed / needs-review
- [IDK what this is] **Tags:** free-form (for example `initial-access`, `lateral-movement`)
- [Yes, but just the level no need CVSS and CVE since that is a different part] **Severity:** info / low / medium / high / critical (for findings)

**Optional extras:**
- [Yes] File upload (image evidence)
- [Yes] Code / command block (string-only, formatted on display)
- [Uh tell me why would i need this] External references (CVE links, and so on)

> Suggestion: Keep almost everything, hold Severity for findings only.
> Auto-gen fields: take all five. They are free, and they are your audit trail (non-negotiable for a tool that may feed a legal or client report).
> Required: keep type, title, description, result. Add **target and host** to required: without it you cannot filter or scope-check, and scoping is a pentest's legal spine.
> Strongly suggested: take target, tool, status, tags. Status and tags turn a flat list into something you can actually mine.
> Hold **severity out of the log**: reserve it for promoted Findings (ties back to A1 equals b). A log like "ran nmap" has no severity, while a finding like "RCE on host X" does. Mixing them muddies both.
> Optional: image and code equal yes (you already wanted these). External refs equal yes, cheap to add.
> Skip: nothing here is bloat.

**Your answer / notes:** All filled.

---

### Follow-ups

**B1. Target and scope field: yes or no?**

- [X] Yes
- [ ] No

> Suggestion: Yes, make it required.
> On a 200-log engagement, "show me everything we did against 10.0.0.5" is the single most common query. Without a target field you are doing full-text search and praying. It is also what powers scope-warnings (see C1): the app cannot tell you "you logged an out-of-scope IP" if it does not know which IP you logged.

**Your answer / notes:**

---

**B2. Do logs need a status field** so that dead-ends are distinguishable from wins?

- [X] Yes
- [ ] No

> Suggestion: Yes.
> Pentesting is 80% dead ends. Without a status field, the log board becomes a graveyard where the wins and the "tried this, nothing" entries look identical, and the leader has to read every single one during review. A simple `in-progress / dead-end / confirmed / needs-review` makes the board skimmable and powers dashboard widgets ("3 confirmed findings this week"). Cheap to build, pays off every day.

**Your answer / notes:**

---

**B3. Severity:** only for "findings," or attachable to any log?

- [X] Only for findings
- [ ] Any log

> Suggestion: Only for findings.
> See A1 and B-main. Severity on every log creates noise ("ran nmap" is info? low? undefined?). Severity is a property of *impact*, and impact only crystallizes when raw activity is promoted into a finding. This keeps the data model clean and forces the leader to make a deliberate call during promotion.

**Your answer / notes:** Yes

---

**B4. Tool auto-ingest** (import nmap XML, and so on): strictly manual per your plan, or a future "nice to have"?

- [Manual Copy as String and formatted to code block by server for easy copy later.] Strictly manual
- [ ] Nice-to-have for later
- [ ] Must-have

> Suggestion: Nice-to-have for later (v2).
> Auto-ingest is a *huge* time-saver and a real differentiator (it is why people pay for Faraday), but it is also a scope swamp: every tool has a different format, parsing is brittle, and you will spend months chasing edge cases. For MVP, ship strict manual entry to validate the workflow. Once you see people using PLogIt daily, add nmap XML first (it is the most common and well-documented), then expand. Build the log schema now so a future importer can populate it without reshaping.

**Your answer / notes:** Is that what you mean or what is this?

---

## Group C: Scoping, rules of engagement, safety rails

Pentests have hard legal boundaries. Should the app help enforce them?

### C1. In-scope targets

Should each engagement define an allowed IP and domain list, and warn (not block) when a log references something out of scope?

- [YES] Yes, warn
- [ ] Yes, hard block
- [ ] No scope enforcement

> Suggestion: Yes, warn (not block).
> Out-of-scope testing is how pentesters get sued or fired. A warning at log-entry time ("10.0.0.99 is not in the declared scope: continue?") is genuine safety value with zero friction. **Do not hard-block**: pentests are messy (a typo in scope, dynamic cloud IPs, scope verbally expanded mid-engagement), and a block will just make users bypass the system or rage-quit. Warn loudly, log it, let the human decide. The audit trail of "user acknowledged out-of-scope warning" is itself valuable evidence.

**Your answer / notes:** a Warning block at the engagement dashboard but IP is manually added by Leader.

---

### C2. Rules of Engagement (RoE)

Store on the engagement board?

- [X] Allowed testing hours
- [X] Allowed / forbidden techniques (no DoS, no social engineering, and so on)
- [X] Emergency contacts
- [X] Out-of-scope list (explicit)

> Suggestion: Take all four.
> These are checkboxes on real pentest SOWs. Storing them on the engagement board makes PLogIt the single source of truth: when a tester is unsure at 2am whether they can run that aggressive exploit, the answer is right there, not in a PDF buried in someone's email. "Emergency contacts" especially: if a test triggers a client's SOC, someone needs to call a named human fast. Cost: four text or list fields on the engagement object. Cheap, professional, sometimes legally saves you.

**Your answer / notes:** All, of course. As many detail as needed.

---

### C3. Sensitive-data handling

When an engagement ends:

- [ ] Auto-purge data after N days (N = ___)
- [ ] Manual purge (leader clicks a button)
- [X] Archive (read-only) but never delete
- [ ] Never delete

> Suggestion: Archive by default, plus optional manual purge, plus an optional auto-purge policy set per engagement.
> Hard answer: do not auto-purge by default. Pentesters often need to revisit old engagements ("did we see this host last quarter?", "the client is asking about a regression"). Archive-as-read-only preserves audit value while it prevents accidental edits. **Offer** auto-purge as a per-engagement toggle (some clients *require* data destruction in the contract, which is common in regulated industries). And give the leader a manual "purge now" button for when that day comes. This trio covers every real scenario without forcing a single opinionated default on everyone.

**Your answer / notes:** That's right, it become read only forever.

---

## Group D: Each board, refined

### Main dashboard

**D1. What does a pentester see the moment they log in?** (Check all that apply.)

- [ ] List of their engagements
- [ ] Upcoming deadlines
- [ ] Recent activity feed (team's latest logs)
- [ ] Assigned tasks / open items needing their attention
- [ ] Stats widgets

> Suggestion: Engagements list, deadlines, and recent activity feed.
> These three answer the user's three landing-page questions in order: *"What am I working on? What's urgent? What did the team just do?"* Skip "assigned tasks" for MVP (you do not have a task system yet, logs are not tasks, and faking one adds confusion). Skip stats widgets on the landing page too: they belong on the engagement board where they have context. Keep the main dashboard calm and scannable. A pentester's first action here is almost always "click into an engagement."

**Your answer / notes:** Go with your idea.

---

**D2. Stats and widgets** you want (check all that apply):

- [ ] Open findings by severity
- [ ] Days to deadline per engagement
- [ ] My logs this week
- [ ] Team activity heatmap
- [ ] Total logs / findings count

> Suggestion: Days to deadline, My logs this week, and Open findings by severity.
> These are the three metrics that actually drive pentester behavior. "Days to deadline" creates urgency (do I need to scope-cut?). "My logs this week" is a personal accountability nudge (and a fill-in-the-blank prompt for underperforming team members). "Open findings by severity" is the leader's at-a-glance risk picture. Skip the heatmap (pretty, low-signal) and the totals count (vanity number) for v1.

**Your answer / notes:** Your Idea.

---

### Engagement (task/goal) board

**D3.** You said the team leader and members can edit this board. Can a member edit OSINT and cert info, or only the leader?

- [ ] Leader-only for canonical scope info
- [ ] Members can edit everything here
- [ ] Members can edit some fields (which ones?): ___________

> Suggestion: Leader-only for canonical scope, members contribute through logs.
> This is what your spec actually wants once untangled. The engagement board's job is to be the *authoritative* scope and context document: if any member can rewrite it, you get drift, contradiction, and "who changed the in-scope list?" arguments. Members are not silenced: they capture their OSINT discoveries as **logs**, and the leader promotes the relevant bits onto the engagement board. This also matches industry norms (the engagement lead owns scope) and gives the leader a real role beyond "PDF exporter."

**Your answer / notes:** Only the leader can, but members can find more info then log into the log list for leader to then think if needed to add.

---

**D4.** Should there be an editable **objectives** list (for example "obtain domain admin," "exfil one record") so that the team can track progress against goals?

- [ ] Yes
- [ ] No

> Suggestion: Yes.
> This is the difference between "we did a bunch of stuff" and "we achieved what the client paid for." Real engagements always have objectives: they are usually spelled out in the SOW. Letting the leader define them and the team mark them done, in-progress, or incomplete gives the dashboard a real progress signal and makes the eventual client report write itself ("Objective 2: Achieved: see Finding F-007"). Cost: a tiny linked list on the engagement object.

**Your answer / notes:** Yes but only leader can edit.

---

### Log board

**D5. Search / filter / sort: must-haves?** (Check all that apply.)

- [ ] Filter by member
- [ ] Filter by type
- [ ] Filter by target
- [ ] Filter by tag
- [ ] Filter by date range
- [ ] Filter by status
- [ ] Filter by severity
- [ ] Full-text search

> Suggestion: All except severity (for v1).
> Every filter here except severity earns its keep on a real engagement (severity is a finding property, see B3). If you only ship two, ship **target** and **date range**: that is "what did we do to this host this week," the most common question. Full-text search is the safety net for everything else. Build filters as a single composable query (not separate screens) so a user can say "Bob's nmap logs against 10.0.0.0/24 from last Tuesday." That composability is what turns a list into a tool.

**Your answer / notes:** Yeah your idea.

---

**D6. Comments on a log entry:** needed? *(For example, the leader asks "can you repro this?")*

- [ ] Yes
- [ ] No

> Suggestion: Yes (lightweight).
> Without comments, the only way to discuss a log is to edit it, which contradicts your "auto-generated, don't touch" rule for timestamps and authors. A simple threaded comment per log ("can you repro?", "yes, see log #45") keeps the conversation attached to the evidence instead of leaking into Slack or DMs where it dies. Keep it lightweight: plain text, no @mentions required, no rich formatting. v2 can add mentions.

**Your answer / notes:** Maybe they can just discuss using Social Media, but yes remember how leader is to approve, so he can add comment for anything that he rejected (Give back to fix).

---

**D7. @mentions / notifications?**

- [ ] Yes, in-app
- [ ] Yes, in-app + email
- [X] No

> Suggestion: In-app only for v1, and email as a per-user opt-in for v2.
> In-app notifications (a bell icon, unread count) are nearly free and do not drag in email infrastructure, deliverability headaches, or privacy concerns (pentest tooling sending email about vulns? No thanks). Email is genuinely useful for "engagement ends in 2 days" reminders, but make it opt-in and keep the subject lines bland: do not leak engagement details in email previews.

**Your answer / notes:** No Notification needed for now.

---

**D8. Grouping / display order** (check all that apply):

- [X] Chronologically flat (newest first)
- [ ] Grouped by day
- [ ] Grouped by target
- [ ] Grouped by member
- [ ] Grouped by type

> Suggestion: Chronological (newest first) as default, plus a "group by target" toggle.
> Chronological is the universal default: it matches how pentesters think ("what just happened?"). Make it the unchangeable default. Then offer **one** alternative grouping: by target, because that is the second-most-common mental model ("show me everything about this host"). Resist offering four grouping modes. Each one is UI complexity and test surface, and most users will never touch them. Filters (D5) already cover the "by member / by type" use cases more flexibly than grouping does.

**Your answer / notes:** Yes your idea.

---

### Leader board

**D9. Besides editing and PDF export, what else does the leader do here?** (Check all that apply.)

- [] Review / approve logs
- [ ] Assign members to engagements
- [ ] Reorder / narrate the report
- [ ] Final sign-off
- [ ] Manage roles

> Suggestion: Review and approve logs, assign members, and final sign-off.
> These three define the leader role. Approving logs is the heart of your A4 state machine. Assigning members is admin hygiene that has to live somewhere, and the leader board is the natural home. Final sign-off is a meaningful state ("this engagement's report is locked, exported, and delivered") and gives you a clean engagement lifecycle. Skip "reorder and narrate the report" for v1: that is a feature for when findings exist (A1 equals b) and deserves its own report-builder view later. Skip "manage roles" too: role assignment belongs in a global admin panel, not per engagement.

**Your answer / notes:** Your Idea.

---

**D10. PDF: what is it?**

- [ ] **Client deliverable:** polished, branded, methodology and findings and appendix.
- [ ] **Internal audit:** plain dump of logs.
- [ ] Both (separate exports).
- Template features (check all that apply):
  - [ ] Cover page
  - [ ] Scope / engagement summary
  - [ ] Findings table
  - [ ] Methodology section
  - [ ] Appendix (raw logs, evidence)
  - [ ] Branding (logo, colors)

> Suggestion: Client deliverable for v1, with cover and scope and findings table and appendix and branding.
> The client deliverable is the high-value output: it is literally the artifact the client pays for. Internal audit is just "export all logs as CSV or PDF" and can ship as a dumb button later. For the template, those five sections are the bones of every pentest report, and methodology can wait (it is often boilerplate the leader pastes in). Branding (logo and colors) matters because firms resell these reports under their own name: your blue-subcolor theme is already a starting point. *One big caveat:* this assumes A1 equals b (you have findings). If you chose logs-only, the only honest PDF is a formatted log dump.

**Your answer / notes:** Not sure, but the pdf is rather for Pentesters or Auditers to look at.

---

### Log-add board

**D11. Beyond image upload and code block, what else?** (Check all that apply.)

- [ ] Markdown rendering in description
- [Yes please] Paste-and-detect-type (paste JSON to auto-format)
- [Okay but explain more on what this does.] Templates ("New Nmap log" prefills type equals Recon)
- [Maybe not now.] Draft saving (autosave incomplete logs)
- [ ] Quick-duplicate last log (for repeated activities)

> Suggestion: Markdown, templates, and draft autosave.
> Markdown is non-negotiable for a pentest tool: descriptions need bullet lists, inline `code`, links, and emphasis, and forcing plain text is a step backwards from a notebook. Templates ("New Nmap log") are a huge time-saver and trivial to build (a named preset of field defaults). Draft autosave prevents the classic "I just typed for 20 minutes and clicked the wrong button" tragedy: cheap insurance, big goodwill. **Skip** paste-detect-type (clever but niche, save for v2). **Skip** quick-duplicate (it tempts users to copy-paste stale results instead of logging fresh, which contradicts your "log again as a new activity" rule).

**Your answer / notes:** Above.

---

## Group E: Security, audit, sensitive content

**E1. Audit log** (admin-level: who joined, who edited what, who exported): separate from activity logs?

- [MUST.] Yes, required
- [ ] Nice to have
- [ ] No

> Suggestion: Yes, required.
> For a tool that may feed legal or client reports and stores sensitive target data, an admin audit log is not optional. It is how you answer "who exported the data on the day it leaked?" and "who changed the scope to include that IP?" Keep it *separate* from the activity log board: this is meta-data about the *system*, not about the engagement, and mixing them confuses both. Append-only, tamper-evident if you can swing it (hash chaining). This is also a feature you can sell to security-conscious firms.

**Your answer / notes:** yep

---

**E2. Edit history / versioning** on log entries (so that "fixing a typo" still leaves a trail)?

- [YES] Yes, required
- [ ] Nice to have
- [ ] No

> Suggestion: Yes, required.
> Your spec explicitly allows typo fixes: without version history, a "typo fix" is indistinguishable from "I rewrote the result to hide a mistake." Version history protects the member (proof they only fixed a typo) and the leader (proof the log was not tampered with post-approval). Implementation is cheap: store diffs or full snapshots on every edit, and show a "history" tab on each log. This pairs with E1 to give you a complete integrity story, which is important if a report ever gets questioned.

**Your answer / notes:** YES

---

**E3. Input sanitization:** descriptions and markdown also need XSS protection (not just code blocks). OK to plan for strict sanitization?

- [YES...] Yes, strict sanitization
- [ ] Basic only

> Suggestion: Yes, strict sanitization.
> This is not really a question: for a security tool, shipping with an XSS hole is brand-ending. Markdown must be parsed with a strict allowlist (no `<script>`, no `javascript:` URLs, no event handlers), and code blocks must be rendered as inert text (your "string-only" instinct is correct: extend it to display). Use battle-tested libraries (DOMPurify on the frontend, a server-side sanitizer too: defense in depth). Pentesters *will* paste weird payloads to test your app, so make sure they do not pop themselves.

**Your answer / notes:** everything needs to be clean and safe.

---

**E4. Secrets detection:** warn if someone pastes what looks like a credential or API key into a log? *(Pentesters will do this.)*

- [Sure if there is a way.] Yes, warn
- [ ] Yes, warn + block
- [ ] No

> Suggestion: Yes, warn (not block).
> Pentest logs frequently contain captured credentials, hashes, and API keys: that is the job. But there is a difference between "client's production AWS key, captured during the test" (legit, log it) and "the tester's own PLogIt session token, accidentally pasted" (a leak). A heuristic warn ("this looks like an AWS access key: was this intentional?") catches the second case without blocking the first. Use pattern libraries (TruffleHog-style regexes are open source). Block-mode is too aggressive for a tool whose whole purpose is recording sensitive findings.

**Your answer / notes:** If possible.

---

## Group F: Tech and operations

**F1. Tech stack preference?**

- Frontend: ___________ (React / Vue / Svelte / other)
- Backend:  ___________ (Node / Python / Go / other)
- Database: ___________ (Postgres / MySQL / SQLite / other)
- Constraints / preferences:

> Suggestion: React + Node (TypeScript) + PostgreSQL.
> React (or Next.js) and Node and TS gives you one language across the stack: huge for a small team or solo dev, and the type safety of TypeScript is gold when your data model has subtle distinctions (log versus finding, draft versus approved). Postgres is the right default: JSON columns for flexible log payloads, strong relational integrity for users, engagements, and membership, full-text search built-in, and it scales from solo self-hosted to firm-wide without rearchitecting. Avoid SQLite for anything but the earliest prototype: its concurrency story bites once two pentesters edit at once. If you hate JS, the second-best stack is Python (FastAPI) and Postgres.

**Your answer / notes:** This will be explained later, I have a list of things we must use. Ask me in the chat.

---

**F2. Auth method?** (Check all that apply.)

- [X] Email + password
- [ ] MFA (TOTP / SMS / hardware key)
- [ ] SSO (SAML / OIDC): firms love this
- [ ] Passkeys / WebAuthn

> Suggestion: Email and password and TOTP MFA for v1, and SSO as a paid or v2 feature.
> Email and password is the universal floor: ship it. Layer TOTP MFA on top (Google Authenticator-style) because no security professional will take a password-only pentest tool seriously, and TOTP is free to implement (no SMS gateway, no hardware). **Skip SSO for v1**: it is a firm-sized feature (SAML is a pain, even OIDC needs an admin UI for tenant config) and only matters once you have firm customers asking for it. That is also when it becomes worth charging for. Passkeys are great but the ecosystem is still bumpy in 2026: revisit in v2.

**Your answer / notes:** Just email and pass for now.

---

**F3. Hosting target?**

- [X] Your own server / VPS
- [ ] Cloud (AWS / GCP / Azure)
- [ ] On-prem only

> Suggestion: Design for self-hosted (Docker), and your own VPS for the cloud beta if you offer one.
> If A3 is self-hosted (likely), ship PLogIt as a Docker Compose bundle: one command brings up the app, DB, and reverse proxy. That is what firms want: `docker compose up`, point their domain at it, done. Their hosting choice (VPS, on-prem, even air-gapped) is then *their* problem, not yours. If you do offer a hosted beta, run it on a single VPS first (Hetzner or DigitalOcean): the complexity of cloud (AWS or GCP) is not justified until you have paying SaaS customers and real load. Keep the architecture cloud-agnostic so you are not locked in.

**Your answer / notes:** This is a small project so my own.

---

**F4. Offline / isolated-network support:** pentesters often work air-gapped. Concern?

- [ ] Must support offline
- [ ] Nice to have
- [ ] Not needed

> Suggestion: Nice-to-have for v2, but design for it now.
> True offline-first (local sync, conflict resolution) is a *massive* engineering cost: it is basically rebuilding the data layer twice. Do not do it in v1. **But** design the data model and API so a future offline client is possible: stateless server, clear sync endpoints, client-generated IDs (UUIDs) so logs created offline do not collide. The realistic v1 compromise: PLogIt runs on a laptop or local server inside the isolated network (since it is self-hosted), which covers most "air-gapped" engagements without true offline sync. Revisit if customers actually demand it.

**Your answer / notes:** Not the concern right now.

---

**F5. Mobile:** phones and tablets on-site, or desktop-only?

- [ ] Responsive (works on mobile browser)
- [ ] Native mobile app
- [ ] Desktop only

> Suggestion: Responsive web for v1, and never build a native app unless forced.
> A responsive webapp covers tablets (used on-site for quick log entry) and phones (used to glance at scope while walking a facility) for free: no separate codebase, no app store review, no version-spread nightmare. Build with mobile in mind from day one (touch targets, no hover-dependent UI, responsive grids). **Native apps are a trap** for a tool like this: pentesters will not install an app on a sensitive engagement device, and you would be maintaining iOS, Android, and web forever. If you ever *need* native (for example offline), do a PWA first.

**Your answer / notes:** Let;s say desktop only for now.

---

## Group G: Nice-to-haves to consider now or later

Mark anything you want to commit to now, defer, or drop.

- [ ] Time-tracking for billing
- [How.] Reusable activity templates
- [ ] Finding deduplication
- [ ] CVSS scoring helper
- [ ] Markdown everywhere
- [YES] Dark/light + blue theme (already in plan)
- [ ] Demo / sample engagement for new users
- [We only have leader and member.] Role hierarchy beyond member and leader (admin, viewer and auditor, client-readonly)

> Suggestion (per item):
> - **Time-tracking** -> Defer. Useful for billing, but it is a different mental model (timers, not logs). Build after core logging is loved.
> - **Reusable activity templates** -> Now. Cheap (named presets), saves time on repetitive logs, ties into D11. Ship it.
> - **Finding deduplication** -> Drop for now. Hard NLP problem, low payoff until you have huge engagements. Revisit at v3.
> - **CVSS scoring helper** -> Defer but design for it. A CVSS calculator widget is a perfect fit for the finding-promotion flow, but it only matters once findings exist (A1 equals b). Add when you build findings.
> - **Markdown everywhere** -> Now. See D11. Non-negotiable.
> - **Theme** -> Already in. Build theme tokens (CSS variables) from day one so dark, light, and blue subcolor are just config, not rewrites.
> - **Demo engagement** -> Now (cheap). Lets new users (and you) click around without onboarding. Seed it with realistic fake logs.
> - **Role hierarchy** -> Defer. Member and leader covers 95% of cases. Add "admin" (manages the instance) and "viewer" (read-only auditor or client) once you have a real reason. Design the schema so roles are an enum, not a boolean, so this is additive later.

**Your answer / notes:** Only ones i filled and answer me.

---

## Open mic

Anything else on your mind: features, vibes, references (tools you like or hate), user stories, edge cases, fears, wild ideas. Write freely below.

> Suggestion: Do not skip this section even if you think you have nothing to say. The most useful things to jot down here are:
> - **Tools you have used** (and what you loved or hated about them): gives me a reference frame.
> - **A specific "worst day" story:** the engagement where everything went sideways. Pain points hide there.
> - **The one feature that, if it existed, would make you switch from whatever you use now.**
> - **Who exactly is user #1?** Just you? A team you're on? A friend? This shapes every default.

> Later.

---

*End of questions. When you are ready, tell me and I will read it.*


## 2026-11-02 new suite-integration questions (not approved)
- Should exports include unapproved records or remain only approved/exported? Current code does the latter.
- How should JudgeIt receive evidence bytes when JSON only provides attachment filenames?
- Does a versioned export envelope and idempotent import require source changes, or a compatibility adapter?
- Who can export, import and retain customer data, and how do namespaced source IDs preserve chain of custody?
- Is the last verified production environment host Nginx, optional Caddy, or another reverse proxy? Validate server facts.

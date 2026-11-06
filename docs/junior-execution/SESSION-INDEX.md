# PLogIt: 24-session execution index

## AUTO SESSION CONTROLLER: highest-priority repository workflow

The owner no longer supplies or edits SESSION_NUMBER in prompts. Read `docs/junior-execution/SESSION-CONTROL.md` from `control/session-state` FIRST and follow its NEXT_SESSION/STATE. This control-branch instruction supersedes earlier text asking the user to specify SESSION_NUMBER or manually approve the next number. User initiation of a fresh coding chat authorizes exactly that controller-selected ten-JR increment. With STATE=IN_PROGRESS, check for another active worker and resume only if safe. After the ten JRs are assessed, pushed, and safely continuable, advance the pointer by one with a **control-branch-only** commit; otherwise retain its number. Keep application JR commits on the phase work branch and never touch main merely to advance session state. The defaults are USE_SUBAGENTS=NO, SUBAGENT_MODEL=N/A; the Lead codes directly. Keep full testing/logging/assumption/phase PR safeguards. PLogIt main pushes can trigger production CD, so **no automatic main commits or merges** for session administration.

**This is a session partition of the existing 240 JR tasks, not a new roadmap.** The original 12 phase files and their instructions remain authoritative. Each session has 10 exact JR identifiers; odd/even sessions share one original 20-task phase.

**Choose one session per new coding session.** Change `SESSION_NUMBER` in `BUILDER-LEAD-PROMPT.md` to 01–24. Read `SESSION-PROTOCOL.md`, `STEP-REGISTER.md`, `STEP-LOG.md`, `SESSION-HANDOFF.md`, and `DECISIONS.md` before any work. Complete only the selected JR range, or record why specific tasks are blocked. Never skip unfinished earlier dependencies.

**GitHub cadence:** every JR gets a reviewed commit and a progress-log update pushed to a phase branch. Session A starts the phase branch; Session B resumes that exact branch and, only when the 20-task phase gate is truly accepted, opens/reviews/merges its PR. End each 10-step session with a sign-off checklist and do not begin the next numbered session automatically.

| Session | Original phase | JR range | Topic | GitHub boundary |
|---:|---:|---|---|---|
| 01 | 01A | JR-001–JR-010 | [Source baseline and controlled scope](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/01-SOURCE-BASELINE-AND-CONTROLLED-SCOPE.md) | Start/push phase branch |
| 02 | 01B | JR-011–JR-020 | [Source baseline and controlled scope](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/01-SOURCE-BASELINE-AND-CONTROLLED-SCOPE.md) | Resume branch; phase tests and PR gate |
| 03 | 02A | JR-021–JR-030 | [Reproducible local engineering](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/02-REPRODUCIBLE-LOCAL-ENGINEERING.md) | Start/push phase branch |
| 04 | 02B | JR-031–JR-040 | [Reproducible local engineering](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/02-REPRODUCIBLE-LOCAL-ENGINEERING.md) | Resume branch; phase tests and PR gate |
| 05 | 03A | JR-041–JR-050 | [Identity and tenant authorization](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/03-IDENTITY-AND-TENANT-AUTHORIZATION.md) | Start/push phase branch |
| 06 | 03B | JR-051–JR-060 | [Identity and tenant authorization](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/03-IDENTITY-AND-TENANT-AUTHORIZATION.md) | Resume branch; phase tests and PR gate |
| 07 | 04A | JR-061–JR-070 | [Engagement and team workflow](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/04-ENGAGEMENT-AND-TEAM-WORKFLOW.md) | Start/push phase branch |
| 08 | 04B | JR-071–JR-080 | [Engagement and team workflow](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/04-ENGAGEMENT-AND-TEAM-WORKFLOW.md) | Resume branch; phase tests and PR gate |
| 09 | 05A | JR-081–JR-090 | [Logs, review states and audit](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/05-LOGS-REVIEW-STATES-AND-AUDIT.md) | Start/push phase branch |
| 10 | 05B | JR-091–JR-100 | [Logs, review states and audit](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/05-LOGS-REVIEW-STATES-AND-AUDIT.md) | Resume branch; phase tests and PR gate |
| 11 | 06A | JR-101–JR-110 | [PLogIt → JudgeIt export contract](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/06-PLOGIT-JUDGEIT-EXPORT-CONTRACT.md) | Start/push phase branch |
| 12 | 06B | JR-111–JR-120 | [PLogIt → JudgeIt export contract](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/06-PLOGIT-JUDGEIT-EXPORT-CONTRACT.md) | Resume branch; phase tests and PR gate |
| 13 | 07A | JR-121–JR-130 | [Media, Markdown and evidence privacy](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/07-MEDIA-MARKDOWN-AND-EVIDENCE-PRIVACY.md) | Start/push phase branch |
| 14 | 07B | JR-131–JR-140 | [Media, Markdown and evidence privacy](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/07-MEDIA-MARKDOWN-AND-EVIDENCE-PRIVACY.md) | Resume branch; phase tests and PR gate |
| 15 | 08A | JR-141–JR-150 | [Frontend journeys and accessibility](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/08-FRONTEND-JOURNEYS-AND-ACCESSIBILITY.md) | Start/push phase branch |
| 16 | 08B | JR-151–JR-160 | [Frontend journeys and accessibility](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/08-FRONTEND-JOURNEYS-AND-ACCESSIBILITY.md) | Resume branch; phase tests and PR gate |
| 17 | 09A | JR-161–JR-170 | [Database integrity and recoverability](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/09-DATABASE-INTEGRITY-AND-RECOVERABILITY.md) | Start/push phase branch |
| 18 | 09B | JR-171–JR-180 | [Database integrity and recoverability](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/09-DATABASE-INTEGRITY-AND-RECOVERABILITY.md) | Resume branch; phase tests and PR gate |
| 19 | 10A | JR-181–JR-190 | [Dependency refresh and supply chain](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/10-DEPENDENCY-REFRESH-AND-SUPPLY-CHAIN.md) | Start/push phase branch |
| 20 | 10B | JR-191–JR-200 | [Dependency refresh and supply chain](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/10-DEPENDENCY-REFRESH-AND-SUPPLY-CHAIN.md) | Resume branch; phase tests and PR gate |
| 21 | 11A | JR-201–JR-210 | [Deployment readiness and verified release](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/11-DEPLOYMENT-READINESS-AND-VERIFIED-RELEASE.md) | Start/push phase branch |
| 22 | 11B | JR-211–JR-220 | [Deployment readiness and verified release](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/11-DEPLOYMENT-READINESS-AND-VERIFIED-RELEASE.md) | Resume branch; phase tests and PR gate |
| 23 | 12A | JR-221–JR-230 | [Suite handoff regression and release](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/12-SUITE-HANDOFF-REGRESSION-AND-RELEASE.md) | Start/push phase branch |
| 24 | 12B | JR-231–JR-240 | [Suite handoff regression and release](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/12-SUITE-HANDOFF-REGRESSION-AND-RELEASE.md) | Resume branch; phase tests and PR gate |

## Exact tasks in each session

### Session 01: JR-001–JR-010
Original phase 01 (A): [Source baseline and controlled scope](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/01-SOURCE-BASELINE-AND-CONTROLLED-SCOPE.md)

- **JR-001**: Inventory Canonical source identity
- **JR-002**: Specify Canonical source identity acceptance
- **JR-003**: Reproduce Canonical source identity success
- **JR-004**: Reject Canonical source identity invalid operation
- **JR-005**: Close Canonical source identity verification
- **JR-006**: Inventory Product role boundary
- **JR-007**: Specify Product role boundary acceptance
- **JR-008**: Reproduce Product role boundary success
- **JR-009**: Reject Product role boundary invalid operation
- **JR-010**: Close Product role boundary verification

### Session 02: JR-011–JR-020
Original phase 01 (B): [Source baseline and controlled scope](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/01-SOURCE-BASELINE-AND-CONTROLLED-SCOPE.md)

- **JR-011**: Inventory Implemented API and UI inventory
- **JR-012**: Specify Implemented API and UI inventory acceptance
- **JR-013**: Reproduce Implemented API and UI inventory success
- **JR-014**: Reject Implemented API and UI inventory invalid operation
- **JR-015**: Close Implemented API and UI inventory verification
- **JR-016**: Inventory Open decisions and rebaseline
- **JR-017**: Specify Open decisions and rebaseline acceptance
- **JR-018**: Reproduce Open decisions and rebaseline success
- **JR-019**: Reject Open decisions and rebaseline invalid operation
- **JR-020**: Close Open decisions and rebaseline verification

### Session 03: JR-021–JR-030
Original phase 02 (A): [Reproducible local engineering](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/02-REPRODUCIBLE-LOCAL-ENGINEERING.md)

- **JR-021**: Inventory Maven and Spring baseline
- **JR-022**: Specify Maven and Spring baseline acceptance
- **JR-023**: Reproduce Maven and Spring baseline success
- **JR-024**: Reject Maven and Spring baseline invalid operation
- **JR-025**: Close Maven and Spring baseline verification
- **JR-026**: Inventory React Vite toolchain
- **JR-027**: Specify React Vite toolchain acceptance
- **JR-028**: Reproduce React Vite toolchain success
- **JR-029**: Reject React Vite toolchain invalid operation
- **JR-030**: Close React Vite toolchain verification

### Session 04: JR-031–JR-040
Original phase 02 (B): [Reproducible local engineering](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/02-REPRODUCIBLE-LOCAL-ENGINEERING.md)

- **JR-031**: Inventory PostgreSQL Flyway schema
- **JR-032**: Specify PostgreSQL Flyway schema acceptance
- **JR-033**: Reproduce PostgreSQL Flyway schema success
- **JR-034**: Reject PostgreSQL Flyway schema invalid operation
- **JR-035**: Close PostgreSQL Flyway schema verification
- **JR-036**: Inventory Compose and healthchecks
- **JR-037**: Specify Compose and healthchecks acceptance
- **JR-038**: Reproduce Compose and healthchecks success
- **JR-039**: Reject Compose and healthchecks invalid operation
- **JR-040**: Close Compose and healthchecks verification

### Session 05: JR-041–JR-050
Original phase 03 (A): [Identity and tenant authorization](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/03-IDENTITY-AND-TENANT-AUTHORIZATION.md)

- **JR-041**: Inventory Authentication session
- **JR-042**: Specify Authentication session acceptance
- **JR-043**: Reproduce Authentication session success
- **JR-044**: Reject Authentication session invalid operation
- **JR-045**: Close Authentication session verification
- **JR-046**: Inventory CSRF enforcement
- **JR-047**: Specify CSRF enforcement acceptance
- **JR-048**: Reproduce CSRF enforcement success
- **JR-049**: Reject CSRF enforcement invalid operation
- **JR-050**: Close CSRF enforcement verification

### Session 06: JR-051–JR-060
Original phase 03 (B): [Identity and tenant authorization](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/03-IDENTITY-AND-TENANT-AUTHORIZATION.md)

- **JR-051**: Inventory Engagement membership boundary
- **JR-052**: Specify Engagement membership boundary acceptance
- **JR-053**: Reproduce Engagement membership boundary success
- **JR-054**: Reject Engagement membership boundary invalid operation
- **JR-055**: Close Engagement membership boundary verification
- **JR-056**: Inventory Auth abuse controls
- **JR-057**: Specify Auth abuse controls acceptance
- **JR-058**: Reproduce Auth abuse controls success
- **JR-059**: Reject Auth abuse controls invalid operation
- **JR-060**: Close Auth abuse controls verification

### Session 07: JR-061–JR-070
Original phase 04 (A): [Engagement and team workflow](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/04-ENGAGEMENT-AND-TEAM-WORKFLOW.md)

- **JR-061**: Inventory Engagement create/list/detail
- **JR-062**: Specify Engagement create/list/detail acceptance
- **JR-063**: Reproduce Engagement create/list/detail success
- **JR-064**: Reject Engagement create/list/detail invalid operation
- **JR-065**: Close Engagement create/list/detail verification
- **JR-066**: Inventory Join codes and membership
- **JR-067**: Specify Join codes and membership acceptance
- **JR-068**: Reproduce Join codes and membership success
- **JR-069**: Reject Join codes and membership invalid operation
- **JR-070**: Close Join codes and membership verification

### Session 08: JR-071–JR-080
Original phase 04 (B): [Engagement and team workflow](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/04-ENGAGEMENT-AND-TEAM-WORKFLOW.md)

- **JR-071**: Inventory Scope and rules of engagement
- **JR-072**: Specify Scope and rules of engagement acceptance
- **JR-073**: Reproduce Scope and rules of engagement success
- **JR-074**: Reject Scope and rules of engagement invalid operation
- **JR-075**: Close Scope and rules of engagement verification
- **JR-076**: Inventory Leadership transfer and team UX
- **JR-077**: Specify Leadership transfer and team UX acceptance
- **JR-078**: Reproduce Leadership transfer and team UX success
- **JR-079**: Reject Leadership transfer and team UX invalid operation
- **JR-080**: Close Leadership transfer and team UX verification

### Session 09: JR-081–JR-090
Original phase 05 (A): [Logs, review states and audit](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/05-LOGS-REVIEW-STATES-AND-AUDIT.md)

- **JR-081**: Inventory Log authoring and edit rules
- **JR-082**: Specify Log authoring and edit rules acceptance
- **JR-083**: Reproduce Log authoring and edit rules success
- **JR-084**: Reject Log authoring and edit rules invalid operation
- **JR-085**: Close Log authoring and edit rules verification
- **JR-086**: Inventory Review state machine
- **JR-087**: Specify Review state machine acceptance
- **JR-088**: Reproduce Review state machine success
- **JR-089**: Reject Review state machine invalid operation
- **JR-090**: Close Review state machine verification

### Session 10: JR-091–JR-100
Original phase 05 (B): [Logs, review states and audit](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/05-LOGS-REVIEW-STATES-AND-AUDIT.md)

- **JR-091**: Inventory Versioning and concurrency
- **JR-092**: Specify Versioning and concurrency acceptance
- **JR-093**: Reproduce Versioning and concurrency success
- **JR-094**: Reject Versioning and concurrency invalid operation
- **JR-095**: Close Versioning and concurrency verification
- **JR-096**: Inventory Audit and log board
- **JR-097**: Specify Audit and log board acceptance
- **JR-098**: Reproduce Audit and log board success
- **JR-099**: Reject Audit and log board invalid operation
- **JR-100**: Close Audit and log board verification

### Session 11: JR-101–JR-110
Original phase 06 (A): [PLogIt → JudgeIt export contract](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/06-PLOGIT-JUDGEIT-EXPORT-CONTRACT.md)

- **JR-101**: Inventory Exporter selection and side effects
- **JR-102**: Specify Exporter selection and side effects acceptance
- **JR-103**: Reproduce Exporter selection and side effects success
- **JR-104**: Reject Exporter selection and side effects invalid operation
- **JR-105**: Close Exporter selection and side effects verification
- **JR-106**: Inventory Legacy JSON wire format
- **JR-107**: Specify Legacy JSON wire format acceptance
- **JR-108**: Reproduce Legacy JSON wire format success
- **JR-109**: Reject Legacy JSON wire format invalid operation
- **JR-110**: Close Legacy JSON wire format verification

### Session 12: JR-111–JR-120
Original phase 06 (B): [PLogIt → JudgeIt export contract](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/06-PLOGIT-JUDGEIT-EXPORT-CONTRACT.md)

- **JR-111**: Inventory Evidence completeness and privacy
- **JR-112**: Specify Evidence completeness and privacy acceptance
- **JR-113**: Reproduce Evidence completeness and privacy success
- **JR-114**: Reject Evidence completeness and privacy invalid operation
- **JR-115**: Close Evidence completeness and privacy verification
- **JR-116**: Inventory Versioned contract decision and compatibility
- **JR-117**: Specify Versioned contract decision and compatibility acceptance
- **JR-118**: Reproduce Versioned contract decision and compatibility success
- **JR-119**: Reject Versioned contract decision and compatibility invalid operation
- **JR-120**: Close Versioned contract decision and compatibility verification

### Session 13: JR-121–JR-130
Original phase 07 (A): [Media, Markdown and evidence privacy](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/07-MEDIA-MARKDOWN-AND-EVIDENCE-PRIVACY.md)

- **JR-121**: Inventory Image upload validation
- **JR-122**: Specify Image upload validation acceptance
- **JR-123**: Reproduce Image upload validation success
- **JR-124**: Reject Image upload validation invalid operation
- **JR-125**: Close Image upload validation verification
- **JR-126**: Inventory Attachment isolation and paths
- **JR-127**: Specify Attachment isolation and paths acceptance
- **JR-128**: Reproduce Attachment isolation and paths success
- **JR-129**: Reject Attachment isolation and paths invalid operation
- **JR-130**: Close Attachment isolation and paths verification

### Session 14: JR-131–JR-140
Original phase 07 (B): [Media, Markdown and evidence privacy](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/07-MEDIA-MARKDOWN-AND-EVIDENCE-PRIVACY.md)

- **JR-131**: Inventory Markdown and code-block sanitization
- **JR-132**: Specify Markdown and code-block sanitization acceptance
- **JR-133**: Reproduce Markdown and code-block sanitization success
- **JR-134**: Reject Markdown and code-block sanitization invalid operation
- **JR-135**: Close Markdown and code-block sanitization verification
- **JR-136**: Inventory Retention, exports and sensitive data
- **JR-137**: Specify Retention, exports and sensitive data acceptance
- **JR-138**: Reproduce Retention, exports and sensitive data success
- **JR-139**: Reject Retention, exports and sensitive data invalid operation
- **JR-140**: Close Retention, exports and sensitive data verification

### Session 15: JR-141–JR-150
Original phase 08 (A): [Frontend journeys and accessibility](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/08-FRONTEND-JOURNEYS-AND-ACCESSIBILITY.md)

- **JR-141**: Inventory Navigation and sessions
- **JR-142**: Specify Navigation and sessions acceptance
- **JR-143**: Reproduce Navigation and sessions success
- **JR-144**: Reject Navigation and sessions invalid operation
- **JR-145**: Close Navigation and sessions verification
- **JR-146**: Inventory Engagement pages
- **JR-147**: Specify Engagement pages acceptance
- **JR-148**: Reproduce Engagement pages success
- **JR-149**: Reject Engagement pages invalid operation
- **JR-150**: Close Engagement pages verification

### Session 16: JR-151–JR-160
Original phase 08 (B): [Frontend journeys and accessibility](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/08-FRONTEND-JOURNEYS-AND-ACCESSIBILITY.md)

- **JR-151**: Inventory Log forms and review UI
- **JR-152**: Specify Log forms and review UI acceptance
- **JR-153**: Reproduce Log forms and review UI success
- **JR-154**: Reject Log forms and review UI invalid operation
- **JR-155**: Close Log forms and review UI verification
- **JR-156**: Inventory Themes, devices and exports
- **JR-157**: Specify Themes, devices and exports acceptance
- **JR-158**: Reproduce Themes, devices and exports success
- **JR-159**: Reject Themes, devices and exports invalid operation
- **JR-160**: Close Themes, devices and exports verification

### Session 17: JR-161–JR-170
Original phase 09 (A): [Database integrity and recoverability](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/09-DATABASE-INTEGRITY-AND-RECOVERABILITY.md)

- **JR-161**: Inventory Schema integrity constraints
- **JR-162**: Specify Schema integrity constraints acceptance
- **JR-163**: Reproduce Schema integrity constraints success
- **JR-164**: Reject Schema integrity constraints invalid operation
- **JR-165**: Close Schema integrity constraints verification
- **JR-166**: Inventory Transactional business actions
- **JR-167**: Specify Transactional business actions acceptance
- **JR-168**: Reproduce Transactional business actions success
- **JR-169**: Reject Transactional business actions invalid operation
- **JR-170**: Close Transactional business actions verification

### Session 18: JR-171–JR-180
Original phase 09 (B): [Database integrity and recoverability](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/09-DATABASE-INTEGRITY-AND-RECOVERABILITY.md)

- **JR-171**: Inventory Database and attachment backup
- **JR-172**: Specify Database and attachment backup acceptance
- **JR-173**: Reproduce Database and attachment backup success
- **JR-174**: Reject Database and attachment backup invalid operation
- **JR-175**: Close Database and attachment backup verification
- **JR-176**: Inventory Health, logging and incident recovery
- **JR-177**: Specify Health, logging and incident recovery acceptance
- **JR-178**: Reproduce Health, logging and incident recovery success
- **JR-179**: Reject Health, logging and incident recovery invalid operation
- **JR-180**: Close Health, logging and incident recovery verification

### Session 19: JR-181–JR-190
Original phase 10 (A): [Dependency refresh and supply chain](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/10-DEPENDENCY-REFRESH-AND-SUPPLY-CHAIN.md)

- **JR-181**: Inventory Java and Spring version review
- **JR-182**: Specify Java and Spring version review acceptance
- **JR-183**: Reproduce Java and Spring version review success
- **JR-184**: Reject Java and Spring version review invalid operation
- **JR-185**: Close Java and Spring version review verification
- **JR-186**: Inventory Node React Vite support
- **JR-187**: Specify Node React Vite support acceptance
- **JR-188**: Reproduce Node React Vite support success
- **JR-189**: Reject Node React Vite support invalid operation
- **JR-190**: Close Node React Vite support verification

### Session 20: JR-191–JR-200
Original phase 10 (B): [Dependency refresh and supply chain](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/10-DEPENDENCY-REFRESH-AND-SUPPLY-CHAIN.md)

- **JR-191**: Inventory Image and workflow hardening
- **JR-192**: Specify Image and workflow hardening acceptance
- **JR-193**: Reproduce Image and workflow hardening success
- **JR-194**: Reject Image and workflow hardening invalid operation
- **JR-195**: Close Image and workflow hardening verification
- **JR-196**: Inventory Artifact traceability
- **JR-197**: Specify Artifact traceability acceptance
- **JR-198**: Reproduce Artifact traceability success
- **JR-199**: Reject Artifact traceability invalid operation
- **JR-200**: Close Artifact traceability verification

### Session 21: JR-201–JR-210
Original phase 11 (A): [Deployment readiness and verified release](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/11-DEPLOYMENT-READINESS-AND-VERIFIED-RELEASE.md)

- **JR-201**: Inventory SSH host trust preflight
- **JR-202**: Specify SSH host trust preflight acceptance
- **JR-203**: Reproduce SSH host trust preflight success
- **JR-204**: Reject SSH host trust preflight invalid operation
- **JR-205**: Close SSH host trust preflight verification
- **JR-206**: Inventory Server topology and reverse proxy
- **JR-207**: Specify Server topology and reverse proxy acceptance
- **JR-208**: Reproduce Server topology and reverse proxy success
- **JR-209**: Reject Server topology and reverse proxy invalid operation
- **JR-210**: Close Server topology and reverse proxy verification

### Session 22: JR-211–JR-220
Original phase 11 (B): [Deployment readiness and verified release](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/11-DEPLOYMENT-READINESS-AND-VERIFIED-RELEASE.md)

- **JR-211**: Inventory Live health and source parity
- **JR-212**: Specify Live health and source parity acceptance
- **JR-213**: Reproduce Live health and source parity success
- **JR-214**: Reject Live health and source parity invalid operation
- **JR-215**: Close Live health and source parity verification
- **JR-216**: Inventory Rollback and recovery approval
- **JR-217**: Specify Rollback and recovery approval acceptance
- **JR-218**: Reproduce Rollback and recovery approval success
- **JR-219**: Reject Rollback and recovery approval invalid operation
- **JR-220**: Close Rollback and recovery approval verification

### Session 23: JR-221–JR-230
Original phase 12 (A): [Suite handoff regression and release](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/12-SUITE-HANDOFF-REGRESSION-AND-RELEASE.md)

- **JR-221**: Inventory Real legacy evidence fixture
- **JR-222**: Specify Real legacy evidence fixture acceptance
- **JR-223**: Reproduce Real legacy evidence fixture success
- **JR-224**: Reject Real legacy evidence fixture invalid operation
- **JR-225**: Close Real legacy evidence fixture verification
- **JR-226**: Inventory Identity and cross-tenant provenance
- **JR-227**: Specify Identity and cross-tenant provenance acceptance
- **JR-228**: Reproduce Identity and cross-tenant provenance success
- **JR-229**: Reject Identity and cross-tenant provenance invalid operation
- **JR-230**: Close Identity and cross-tenant provenance verification

### Session 24: JR-231–JR-240
Original phase 12 (B): [Suite handoff regression and release](../../planning/RES-application-plan/PlogIt%20Workspace/Project%20Info/08-junior-execution-roadmap/12-SUITE-HANDOFF-REGRESSION-AND-RELEASE.md)

- **JR-231**: Inventory Full app regression and security gate
- **JR-232**: Specify Full app regression and security gate acceptance
- **JR-233**: Reproduce Full app regression and security gate success
- **JR-234**: Reject Full app regression and security gate invalid operation
- **JR-235**: Close Full app regression and security gate verification
- **JR-236**: Inventory Final evidence and next-session handoff
- **JR-237**: Specify Final evidence and next-session handoff acceptance
- **JR-238**: Reproduce Final evidence and next-session handoff success
- **JR-239**: Reject Final evidence and next-session handoff invalid operation
- **JR-240**: Close Final evidence and next-session handoff verification

## Restart and incomplete-session rule

If a session is interrupted, rerun **the same SESSION_NUMBER** and resume only its incomplete JRs. The next session number is not permission to abandon BLOCKED or IN PROGRESS prerequisites. A DONE task is not redone without reason; re-verify if code changed. If a later step is independent, record the dependency judgment and continue within the assigned range.

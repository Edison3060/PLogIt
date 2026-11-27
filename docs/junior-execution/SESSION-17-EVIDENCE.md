docs/junior-execution/SESSION-17-EVIDENCE.md

## JR-170

Executable SHA `8c852b564928bbb1ed7613fb0310b71273de7792`. [GitHub Actions run 45926331023](https://github.com/Edison3060/PLogIt/actions/runs/45926331023) ran `mvn -B clean verify` with Java 21.0.13, Maven 3.9.15 and PostgreSQL 16.16. All 170 backend tests passed with zero failures, errors or skips. This includes 4 Flyway migration tests, 18 audit tests, 21 review tests, 2 team tests and 18 export tests. Frontend build and all 25 Vitest tests passed. Isolated Compose HTTP and volume-restart smoke, Semgrep, Trivy filesystem and both images, and proxy validation passed. Production CD was skipped. Local `git diff --check` and an append-only step log check passed. Builder Lead reviewed.

- JR-163 Clean and seeded V1-to-V3 migrations succeeded. PostgreSQL reported six validated foreign keys and two unique constraints. The seeded leadership, log, JSON snapshot and version defaults were preserved.
- JR-164 The duplicate membership and invalid user FK produced SQLSTATE 23505 and 23503, respectively. Both transactions reported 25P02 after the rejected statement. Fresh queries confirmed the first write rolled back and the original membership remained.
- JR-168 Leadership, submission and approval, and legacy JSON export committed. The tests re-read the leader and roster, review states, selected and excluded export logs, and the matching actor, action and target of the audit rows.
- JR-169 Three PostgreSQL audit constraints returned SQLSTATE 23514 at commit. The API returned a generic 500 and export returned no download header. Fresh reads confirmed the original leader and roles, log states and versions, and zero remaining audit rows.
- JR-165 Foreign-key, unique constraint and rollback evidence passed. PostgreSQL enum enforcement remains open under DEC-2026-11-27-01, so JR-165 remains IN REVIEW.
- JR-170 The original transactional success and denial evidence both passed. VERIFIED NO CHANGE to application code, migrations, authentication, audit data and unversioned JSON.

[Draft PR #30](https://github.com/Edison3060/PLogIt/pull/30) stays stacked on unmerged [Phase 08 PR #29](https://github.com/Edison3060/PLogIt/pull/29). Phase 09 acceptance requires Session 18 recovery evidence. INT-001 and the existing release gates remain open.

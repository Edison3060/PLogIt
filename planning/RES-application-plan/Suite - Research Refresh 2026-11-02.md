# RES Suite research refresh — 2026-11-02

**Status:** Research and planning only. Historical July 2026 specs are not current implementation evidence.

## Verified facts
- PLogIt source copied intact from Edison3060/PLogIt at commit 28aa1561cb59637e954a08caf2a2ed068178a9fb. All 185 source files, including seven PNG images, were confirmed by identical Git object SHA at the destination.
- PLogIt backend/pom.xml now uses Spring Boot 4.1.1 with Java 21, NOT the old 3.5.16 documented in the July plans and some READMEs.
- PLogIt frontend/package.json retains React 18.3.1, Vite ^6.4.3, TypeScript ^5.6.2, Tailwind ^3.4.13. PostgreSQL is 16-alpine with pinned image digest.
- Merged PLogIt PRs #1–#19 include image digests, container healthchecks, test coverage and engagement page extraction.
- GitHub Actions run 41421194108 on main passed backend, frontend, Semgrep, Trivy and proxy validation but FAILED the production deployment preflight for unavailable PROD_SSH_HOST_FINGERPRINT. Historical documentation calling the app deployed does NOT establish that the latest commit reached production.
- JudgeIt, PatchIt and GaugeIt in RES consist of planning documents and infrastructure scaffolding only. Application functionality is NOT built.

## Critical mismatch in the first handoff
Actual code: backend/src/main/java/io/muzoo/ssc/plogit/service/export/JsonReportStrategy.java and ExportService.java.
- Current JSON only exports APPROVED logs by default and optionally previous EXPORTED logs. DRAFT, SUBMITTED and REJECTED logs are excluded. Thus the suite plan's phrase "all raw logs, nothing discarded" is not true of today's producer.
- JSON includes log UUID strings and engagement metadata, but images are represented by attachmentFilenames only, without image bytes, hashes or independent retrieval grants. It is NOT a self-contained evidence pack.
- JSON has no schemaVersion, exportId, attachment manifest or validation contract. The downloaded filename ends in -evidence.json rather than necessarily evidence-pack.json.
- Export is a leader-only POST that updates approved logs to EXPORTED and writes audit events. The downstream importer must not assume read-only export semantics.
- No PLogIt code was changed. A future separately approved code PR may be needed for the suite to achieve its stated evidence-chain goal.

## 2026 external research verified against original publishers
| Area | Verified authoritative source | Planning implication |
|---|---|---|
| Spring Boot | https://spring.io/blog/2026/08/20/spring-boot-4-1-1-available-now/ | Use the actual Boot 4.1.1 code baseline; do not assume 3.5.16 |
| React | https://react.dev/versions | React 19 exists; upgrading existing code requires compatibility tests |
| Node runtime | https://github.com/nodejs/Release | Node 20 is **end-of-life since 2026-04-30** (https://nodejs.org/en/about/eol); it is used in PLogIt's frontend *build stage*, not as the shipped nginx runtime. Prioritize controlled replacement by Node 24/26 LTS. |
| PostgreSQL | https://www.postgresql.org/support/versioning/ | PostgreSQL 16 is supported until November 2028; upgrade to 18 is optional and requires migration rehearsals |
| Vulnerability scoring | https://www.first.org/cvss/v4.0/specification-document | JudgeIt must preserve CVSS version, vector, metric nomenclature and score, not just severity |
| Exploit priority | https://www.first.org/epss/ | EPSS is exploitation likelihood, NOT impact or a full risk measure |
| Web security | https://asvs.owasp.org/ and https://owasp.org/projects/top-ten | ASVS 5.0 and OWASP Top 10 2025 support concrete security acceptance criteria |
| API risks | https://devguide.owasp.org/en/07-training-education/07-api-top-ten/ | Test tenant isolation, object-level auth, imports, resource exhaustion |
| JSON contracts | https://json-schema.org/draft/2020-12/ | Version, validate and test each exported/imported file |
| GitHub Actions | https://docs.github.com/en/actions/reference/security/secure-use | Keep immutable action references; evaluate dependency lockfiles if available |
| Software security | https://csrc.nist.gov/pubs/sp/800/218/final | Incorporate threat modeling, provenance, secure builds and release gates |

## Decisions and boundaries
The founding principle remains: PLogIt captures; JudgeIt judges; PatchIt fixes; GaugeIt explains posture. Separate data stores; one-way offline JSON handoffs. The repo's future product vision is a *proposal*, not proof of implemented interoperation. Tech stack and schema details for unbuilt apps must be approved per slice.

**Next:** read Suite - Modernization Roadmap.md, Suite - Handoff Contracts and Evidence.md, and the PLogIt research plan inside its copied workspace. Keep historical logs intact and distinguish facts from proposed changes.

**Limits:** No server console or secret values available, no end-to-end suite app interoperability exists yet, and no production verification was performed. No code in the original or copied PLogIt project was changed in this research pass.

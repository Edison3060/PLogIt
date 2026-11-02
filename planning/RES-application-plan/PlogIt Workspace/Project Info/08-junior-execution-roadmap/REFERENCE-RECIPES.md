# PLogIt reference test recipes

These are **test outlines**, not implemented tests. Run with synthetic data against an isolated DB and capture actual exit codes.

## Build verification

```sh
cd backend && mvn test
cd ../frontend && npm ci && npm run lint && npm run build && npm run test -- --run
```

Consult `package.json` for the actual test script before executing. No passing result is implied.

## Legacy JSON characterization

```text
1. Seed one engagement with logs in DRAFT, SUBMITTED, APPROVED, EXPORTED and REJECTED states.
2. As leader with valid session/CSRF, POST export using current ExportService API.
3. Assert only eligible APPROVED / optionally previous EXPORTED records appear.
4. Verify exported response has engagement metadata, log UUID and attachmentFilenames.
5. Assert there is no schemaVersion, attachment byte payload or integrity hash.
6. Check export state and transactional audit side effects independently.
7. Repeat as ordinary member and with invalid CSRF: no export and no mutation.
8. Feed exact real response to JudgeIt legacy importer; reject malformed and duplicate inputs.
```

## Cross-engagement negative test

```text
Create engagement A and B with different memberships.
As A member, request B log, B attachment, B export and B scope edit.
Assert documented 403/404 policy; never return B data and never mutate B.
Repeat denied mutation without valid CSRF and inspect audit events.
```

Do not embed real pentest output or credentials in versioned fixtures. A proposed envelope/manifest is not the current PLogIt JSON contract.

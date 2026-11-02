# Phase 06 — PLogIt → JudgeIt export contract

**Prerequisite:** Pass Phase 05 with recorded evidence. **Exit gate:** Characterize actual current exporter before proposing a version upgrade.

**Existing-code policy:** PLogIt is built. Each JR is a verification or separately approved correction; do not recreate a feature or edit the copied RES source. **All JR execution statuses begin NOT STARTED**, even where implementation exists.

### JR-101 — Inventory Exporter selection and side effects

- **Where:** `service/ExportService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `service/ExportService.java`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-102 — Specify Exporter selection and side effects acceptance

- **Where:** `service/ExportService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Leader-only POST exports APPROVED and optionally previously EXPORTED; emits review-state/audit mutations. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-103 — Reproduce Exporter selection and side effects success

- **Where:** `service/ExportService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Seed every review state and prove exact included set and after-export state. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Seed every review state and prove exact included set and after-export state. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-104 — Reject Exporter selection and side effects invalid operation

- **Where:** `service/ExportService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Member export and empty eligible set follow approved rejection/empty semantics without leaks. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Member export and empty eligible set follow approved rejection/empty semantics without leaks. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-105 — Close Exporter selection and side effects verification

- **Where:** `service/ExportService.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-106 — Inventory Legacy JSON wire format

- **Where:** `service/export/JsonReportStrategy.java and service/export/EngagementReportData.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `service/export/JsonReportStrategy.java and service/export/EngagementReportData.java`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-107 — Specify Legacy JSON wire format acceptance

- **Where:** `service/export/JsonReportStrategy.java and service/export/EngagementReportData.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Actual fields include engagement metadata, UUID logs, Markdown and attachmentFilenames; no schemaVersion. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-108 — Reproduce Legacy JSON wire format success

- **Where:** `service/export/JsonReportStrategy.java and service/export/EngagementReportData.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Generate canonical unversioned JSON fixture and characterize field types/nullability. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Generate canonical unversioned JSON fixture and characterize field types/nullability. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-109 — Reject Legacy JSON wire format invalid operation

- **Where:** `service/export/JsonReportStrategy.java and service/export/EngagementReportData.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Reject malformed UUID, unknown contract assumptions and unsupported payload shapes in consumer. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Reject malformed UUID, unknown contract assumptions and unsupported payload shapes in consumer. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-110 — Close Legacy JSON wire format verification

- **Where:** `service/export/JsonReportStrategy.java and service/export/EngagementReportData.java` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-111 — Inventory Evidence completeness and privacy

- **Where:** `service/export/JsonReportStrategy.java and Suite - Handoff Contracts and Evidence.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `service/export/JsonReportStrategy.java and Suite - Handoff Contracts and Evidence.md`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-112 — Specify Evidence completeness and privacy acceptance

- **Where:** `service/export/JsonReportStrategy.java and Suite - Handoff Contracts and Evidence.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: Filename-only media and excluded states remain explicit known limitations. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-113 — Reproduce Evidence completeness and privacy success

- **Where:** `service/export/JsonReportStrategy.java and Suite - Handoff Contracts and Evidence.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Prove export contains attachmentFilenames, not hashes or bytes, and record excluded states. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Prove export contains attachmentFilenames, not hashes or bytes, and record excluded states. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-114 — Reject Evidence completeness and privacy invalid operation

- **Where:** `service/export/JsonReportStrategy.java and Suite - Handoff Contracts and Evidence.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Tampered filename cannot be mistaken for cryptographically verified media. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Tampered filename cannot be mistaken for cryptographically verified media. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-115 — Close Evidence completeness and privacy verification

- **Where:** `service/export/JsonReportStrategy.java and Suite - Handoff Contracts and Evidence.md` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-116 — Inventory Versioned contract decision and compatibility

- **Where:** `proposed contracts/evidence-pack-v2.schema.json and JudgeIt import fixtures` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Inspect `proposed contracts/evidence-pack-v2.schema.json and JudgeIt import fixtures`, trace the live owner, find existing test cases and record whether this behavior is implemented, incomplete or only proposed. Do not change code in this step.
- **Pass / evidence:** A source-and-tests map names the real owner and Git SHA; it distinguishes legacy docs from current code.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-117 — Specify Versioned contract decision and compatibility acceptance

- **Where:** `proposed contracts/evidence-pack-v2.schema.json and JudgeIt import fixtures` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare actual code and documented requirements with this invariant: New envelope, exportId, hashing, packaging, tenant namespace and import dedupe require approval. Write expected request/data shape and required approval for any discrepancy.
- **Pass / evidence:** A dated decision row identifies actual behavior, target behavior, owner and whether a change is approved.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-118 — Reproduce Versioned contract decision and compatibility success

- **Where:** `proposed contracts/evidence-pack-v2.schema.json and JudgeIt import fixtures` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** On isolated synthetic data, exercise this exact success case: Approve schema/fixtures then demonstrate same producer output accepted by JudgeIt legacy reader. Capture the command/API route, observed values and reproducible fixture.
- **Pass / evidence:** Observed output matches the documented invariant, with screenshot only when testing UI. Expected success: Approve schema/fixtures then demonstrate same producer output accepted by JudgeIt legacy reader. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-119 — Reject Versioned contract decision and compatibility invalid operation

- **Where:** `proposed contracts/evidence-pack-v2.schema.json and JudgeIt import fixtures` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Run this negative/race case: Repeat import and unknown schema version must not silently duplicate or reinterpret evidence. Verify authorization, transaction, safe error and absence of unintended side effects.
- **Pass / evidence:** Failure is explicit, no unauthorized mutation occurs, and the test can be repeated. Expected denial: Repeat import and unknown schema version must not silently duplicate or reinterpret evidence. Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

### JR-120 — Close Versioned contract decision and compatibility verification

- **Where:** `proposed contracts/evidence-pack-v2.schema.json and JudgeIt import fixtures` (canonical `Edison3060/PLogIt` unless a named suite document).
- **Do exactly:** Compare success and negative evidence; when both pass, record VERIFIED NO CHANGE or an explicitly approved focused source PR with tests and rollback. Re-run both cases after any correction.
- **Pass / evidence:** Tracker cites exact tests, commit SHA, regression result, open blockers, and reviewer sign-off.  Record expected versus actual and responsible reviewer.
- **Mistake to avoid:** Do not claim nonapproved logs or media bytes are present in current output.

## Phase sign-off

Characterize actual current exporter before proposing a version upgrade. Re-run affected prior-phase tests and record the SHA, isolated environment, redacted logs and rollback. A past historical "done" does not satisfy this new gate.

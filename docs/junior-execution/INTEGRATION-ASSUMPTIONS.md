# PLogIt: provisional cross-application integration ledger

This is the persistent reconciliation backlog for **PLogIt → JudgeIt evidence export**. The owner authorizes implementation using reasoned provisional assumptions while other apps are unfinished. **No entries are confirmed until the real producer and consumer are tested together.** Provisional contracts are not approval to rewrite producer-owned semantics or break deployed behavior.

After every JR involving another app, append/update the relevant entry and reference its ID from `STEP-LOG.md` and `SESSION-HANDOFF.md`. Carry the unverified integration obligation into the final session readiness verdict. A synthetic passing fixture is real evidence for *local fixture behavior only*, never for external interoperability.

## Entry template

### INT-001 | PROVISIONAL | JR-XXX | Interface or behavior

- **Scope:** PLogIt → JudgeIt evidence export
- **Producer / consumer:** PLogIt existing canonical exporter → JudgeIt
- **Current ground truth:** Repo, file paths, commit SHAs and JSON examples actually inspected; state what is verified and what is inferred
- **Recommended assumption chosen:** Explain the interim behavior and why it is reasonable
- **Assumed contract:** Version, schema path, fields, identifiers, units, null/error behavior, UTC rules and ownership
- **Replaceable integration boundary:** Adapter/validator module and tests; never direct shared database writes
- **Fixture paths:** Synthetic producer/consumer input and expected output with reproducible commands
- **Actual local test outcome:** Command, environment, exit code, expected versus observed
- **Real producer/consumer verification:** PENDING until tested with real output; link evidence when eventually confirmed
- **Reconciliation task:** Specific future adaptation and owners, applicable JR IDs or GitHub issue
- **Status:** PROVISIONAL / CONFIRMED / SUPERSEDED, never implicitly upgraded
- **References:** STEP-LOG JR, decision ID, commit SHA and PR

## Existing provisional entries

_None recorded yet. Creation of this ledger does not mean any JR was attempted._

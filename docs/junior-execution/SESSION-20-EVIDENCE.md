# PLogIt Session 20 supply-chain evidence

All runtime verification uses synthetic inputs. Phase 10 remains stacked on Phase 09. Builder Lead is the technical reviewer. Release acceptance remains with the PLogIt maintainer and release operator.

## JR-191

**DONE**. Inventory image pins and scanner ownership

At `a30b501f61285f79cb5db57f7064c67fa497d6bf`, the canonical backend and frontend Dockerfiles each pin both FROM images by digest, with a non-root final user and a healthcheck. Development Compose pins PostgreSQL. The production override removes database host publishing, binds the application ports to loopback and pins opt-in Caddy. `.github/workflows/ci.yml` owns checkout, toolchain setup, Semgrep, Trivy, proxy validation, isolated Compose and the main-only deployment gate. Its Semgrep and Caddy images, Trivy release checksum, and third-party actions are pinned. `.github/workflows/ci.yml` and `scripts/ci-compose-smoke.sh` own the executable checks. Canonical CI and Compose supersede the historical deployment claims in the mirrored specs. Expected and observed ownership matched under Builder Lead source review. [Baseline CI](https://github.com/Edison3060/PLogIt/actions/runs/46097253142).

## JR-192

**DONE**. Set image and workflow acceptance criteria

DEC-2026-11-28-04 sets the expected inputs and owners. Third-party action refs must be 40 hexadecimal characters. Pulled images use a `sha256:` digest. Workflow token permissions are `contents: read` with no escalation. Render production Compose using synthetic values and confirm unexposed PostgreSQL, loopback app ports and opt-in Caddy. Semgrep and each blocking Trivy invocation must fail on their configured negative cases. The release operator owns any future deployment-policy change. [GitHub secure-use reference](https://docs.github.com/en/actions/reference/security/secure-use) and [Docker image input policy](https://docs.docker.com/build/policies/validate-images/) reviewed 2026-11-28.

## JR-193

**DONE**. Verify pinned inputs and rendered Compose controls

Local Python with PyYAML 6.0.3 parsed the canonical Dockerfiles, Compose and Actions YAML at `a30b501f61285f79cb5db57f7064c67fa497d6bf`. It observed four valid Dockerfile digest pins, two pinned Compose images, ten full action pins, the pinned Semgrep container, read-only contents permission, three blocking Trivy commands and main-only deployment. The static assertions printed PASS. Exact-head [CI 46097253142](https://github.com/Edison3060/PLogIt/actions/runs/46097253142) separately ran `docker compose -f docker-compose.yml -f docker-compose.prod.yml --profile app --profile tls config --quiet` and the JSON policy assertions under synthetic environment values. Proxy job `140831421564`, Semgrep `140831421362`, and Trivy `140831421432` all passed. The CI jobs also pulled pinned PostgreSQL, validated Caddy and checked final image healthchecks. Production CD was skipped. Local Docker is unavailable. The live Compose and scanner evidence comes from the GitHub runner.

## JR-194

**DONE**. Verify Semgrep fails on a synthetic finding

Disposable branch `verify/plogit-jr194-scanner-denial` at `3d3295a1a4c883608fe01ca8c03430322cfbc57b` executed `semgrep scan --metrics=off --config "$probe/rule.yml" --error "$probe/probe.py"`. The fixture was a harmless print statement matched by one local ERROR rule. The job log showed `Findings: 1 (1 blocking)` and exit 1. The Semgrep job failed and the following step was skipped. The workflow requires successful Semgrep and Trivy and restricts CD to main. The fixture was generated in a runner temp directory and removed on exit. [Draft PR #34](https://github.com/Edison3060/PLogIt/pull/34). [Scanner job](https://github.com/Edison3060/PLogIt/actions/runs/46152779031/job/141002808014).

## JR-195

**DONE**. Close image and workflow hardening verification

The canonical inputs, scanner settings and Compose restrictions passed baseline [CI 46097253142](https://github.com/Edison3060/PLogIt/actions/runs/46097253142) at `a30b501f61285f79cb5db57f7064c67fa497d6bf`. The separate synthetic negative [job 141002808014](https://github.com/Edison3060/PLogIt/actions/runs/46152779031/job/141002808014) returned an actual blocking scanner finding. Verification required no change to the Phase 10 runtime or security gate. Base image digest pinning constrains inputs, while package upgrades during image build can still produce different output. The maintainer owns promotion and the draft branch is excluded from the Phase 10 deliverable.

## JR-196

**DONE**. Trace current CI and deployment artifact ownership

At Phase 10 baseline `a30b501f61285f79cb5db57f7064c67fa497d6bf`, `.github/workflows/ci.yml` uses `docker build -t plogit-backend:ci ./backend` and `docker build -t plogit-frontend:ci ./frontend`, validates the resulting images and scans the local tags. The main-only SSH job checks out `DEPLOY_SHA` and executes `docker compose ... up -d --build`. It has source SHA linkage and no promotion of the CI-scanned image IDs. The separate Compose smoke also builds images. The three migrations live in `backend/src/main/resources/db/migration/`. `docs/operations/recovery.md` preserves rollback records and paired data requirements. `git diff --name-only 591e3e8... HEAD` confirmed the baseline Phase 10 change set was tracking-only. Expected traceability is incomplete. Builder Lead reviewed source and exact CI scan steps. The deployment operator owns production promotion and rollback.

## JR-197

**DONE**. Define the traceability evidence and release gate

DEC-2026-11-28-05 defines the offline evidence tuple and production approvals. The exercised tuple covers exact checked-out SHA and Actions execution, local image IDs, archive SHA-256, packaged migration hashes and an ancestral test rollback SHA. Expected release evidence also binds tested and deployed registry manifests by digest, including the platform, migration verification and a known-good rollback artifact. The current source rebuilds on the deployment host. The release gate remains pending authorized configuration and genuine artifact evidence. [Docker digest guidance](https://docs.docker.com/learn/content/prove-where-your-image-came-from/) and [GitHub attestations guidance](https://docs.github.com/en/actions/how-tos/secure-your-work/use-artifact-attestations/use-artifact-attestations) reviewed 2026-11-28.

## JR-198

**DONE**. Trace scanned images through offline archive and reload

[Candidate workflow 46152942373](https://github.com/Edison3060/PLogIt/actions/runs/46152942373) succeeded on PR merge-test SHA `c6d1a29d2cab4ae7edb99cca43201922127be3dd` from candidate head `94ddd604c4c9d83fa932285bec7f772e31a11b59`. After both blocking Trivy image scans, the runner executed `python3 scripts/ci-artifact-identity.py`. It saved the scanned images, removed the local tags, checked the archive SHA-256, loaded both images and matched both local image IDs. It extracted the backend JAR and verified all three packaged Flyway SQL hashes against checked-out files. It verified the synthetic rollback commit is an ancestor. [Exact job](https://github.com/Edison3060/PLogIt/actions/runs/46152942373/job/141003305486).

```json
{
  "actions_attempt": "1",
  "actions_run": "46152942373",
  "flyway_migrations_sha256": {
    "V1__init_schema.sql": "4438ab537bddb8ec082a93f6c5ff8156dd7e2952fc0cb5868a0492596f5dcd2d",
    "V2__add_optimistic_locking.sql": "efcbb1e36fdf975a89e39609e5840b853de59a5f50f0746059249d128fb9e1ad",
    "V3__unique_log_version_numbers.sql": "43723bf0960349fb0eb5601ed4986adac3ff54f8a844ed2042bd83b73d281398"
  },
  "image_archive_sha256": "56e1d09bd713519ebaa34decf1c2c12eeb7b58eb0d0d9e50fa84584c7f7589c1",
  "local_image_ids": {
    "backend": "sha256:f41fbc07b15468151e74380d415a22c4b1712d584fd497aba397abf7607219d3",
    "frontend": "sha256:75d3d2826082a6ceebdd081a85d0471d280c0c712eafbca2b497c53e94a43d78"
  },
  "source_sha": "c6d1a29d2cab4ae7edb99cca43201922127be3dd",
  "test_rollback_source_sha": "591e3e8a88f82e38c66203ff4fbe69ef238411cf"
}
```

The test used temporary runner storage. The Phase 10 release gate still requires an approved registry digest, deployment parity and authenticated rollback evidence.

## JR-199

**DONE**. Reject the mismatched rebuilt image

On the real GitHub Docker runner, the candidate rebuilt a derived local backend image with a synthetic changed configuration label. A comparison of Docker image IDs rejected it with `Expected mismatch rejected: Loaded images differ from the images scanned in CI`. The script subsequently re-read both canonical local tags, matched them to the original scanned IDs and finished successfully. An archive with the original IDs still passed in the same run. The job used runner-local storage and the production deployment job was skipped. [Exact job](https://github.com/Edison3060/PLogIt/actions/runs/46152942373/job/141003305486).

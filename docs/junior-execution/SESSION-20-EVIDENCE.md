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

# PLogIt Session 20 supply-chain evidence

All runtime verification uses synthetic inputs. Phase 10 remains stacked on Phase 09. Builder Lead is the technical reviewer. Release acceptance remains with the PLogIt maintainer and release operator.

## JR-191

**DONE**. Inventory image pins and scanner ownership

At `a30b501f61285f79cb5db57f7064c67fa497d6bf`, the canonical backend and frontend Dockerfiles each pin both FROM images by digest, with a non-root final user and a healthcheck. Development Compose pins PostgreSQL. The production override removes database host publishing, binds the application ports to loopback and pins opt-in Caddy. `.github/workflows/ci.yml` owns checkout, toolchain setup, Semgrep, Trivy, proxy validation, isolated Compose and the main-only deployment gate. Its Semgrep and Caddy images, Trivy release checksum, and third-party actions are pinned. `.github/workflows/ci.yml` and `scripts/ci-compose-smoke.sh` own the executable checks. Canonical CI and Compose supersede the historical deployment claims in the mirrored specs. Expected and observed ownership matched under Builder Lead source review. [Baseline CI](https://github.com/Edison3060/PLogIt/actions/runs/46097253142).

## JR-192

**DONE**. Set image and workflow acceptance criteria

DEC-2026-11-28-04 sets the expected inputs and owners. Third-party action refs must be 40 hexadecimal characters. Pulled images use a `sha256:` digest. Workflow token permissions are `contents: read` with no escalation. Render production Compose using synthetic values and confirm unexposed PostgreSQL, loopback app ports and opt-in Caddy. Semgrep and each blocking Trivy invocation must fail on their configured negative cases. The release operator owns any future deployment-policy change. [GitHub secure-use reference](https://docs.github.com/en/actions/reference/security/secure-use) and [Docker image input policy](https://docs.docker.com/build/policies/validate-images/) reviewed 2026-11-28.

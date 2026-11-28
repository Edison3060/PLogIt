import hashlib
import json
import os
import re
import subprocess
import tempfile
from pathlib import Path
from zipfile import ZipFile


TAGS = {"backend": "plogit-backend:ci", "frontend": "plogit-frontend:ci"}
MIGRATION_DIR = Path("backend/src/main/resources/db/migration")


def output(*command, **kwargs):
    return subprocess.check_output(command, text=True, **kwargs).strip()


def identity(image):
    value = output("docker", "image", "inspect", "--format", "{{.Id}}", image)
    if not re.fullmatch(r"sha256:[0-9a-f]{64}", value):
        raise SystemExit("Invalid Docker image ID")
    return value


def file_digest(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def verify_images(expected, observed):
    if expected != observed:
        raise ValueError("Loaded images differ from the images scanned in CI")


def verify_migrations(container_image, directory, migrations):
    container = output("docker", "create", container_image)
    jar = directory / "app.jar"
    try:
        subprocess.run(["docker", "cp", f"{container}:/app/app.jar", str(jar)], check=True)
    finally:
        subprocess.run(["docker", "rm", container], check=True, stdout=subprocess.DEVNULL)
    prefix = "BOOT-INF/classes/db/migration/"
    with ZipFile(jar) as bundle:
        actual = {
            name.removeprefix(prefix): hashlib.sha256(bundle.read(name)).hexdigest()
            for name in bundle.namelist()
            if name.startswith(prefix) and name.endswith(".sql")
        }
    if migrations != actual:
        raise SystemExit("Packaged Flyway migrations differ from the checked-out SQL")


def main():
    source = output("git", "rev-parse", "HEAD")
    if source != os.environ["GITHUB_SHA"]:
        raise SystemExit("Checked-out source differs from the workflow SHA")
    rollback = output("git", "rev-parse", "--verify", "591e3e8a88f82e38c66203ff4fbe69ef238411cf^{commit}")
    subprocess.run(["git", "merge-base", "--is-ancestor", rollback, source], check=True)
    migrations = {path.name: file_digest(path) for path in sorted(MIGRATION_DIR.glob("*.sql"))}
    if not migrations:
        raise SystemExit("No Flyway migrations are present")

    scanned = {name: identity(tag) for name, tag in TAGS.items()}
    with tempfile.TemporaryDirectory(prefix="plogit-trace-") as tmp:
        directory = Path(tmp)
        archive = directory / "images.tar"
        subprocess.run(["docker", "save", "-o", str(archive), *TAGS.values()], check=True)
        archive_hash = file_digest(archive)
        subprocess.run(["docker", "image", "rm", *TAGS.values()], check=True, stdout=subprocess.DEVNULL)
        if archive_hash != file_digest(archive):
            raise SystemExit("Image archive failed its SHA-256 check")
        subprocess.run(["docker", "load", "-i", str(archive)], check=True)
        loaded = {name: identity(tag) for name, tag in TAGS.items()}
        verify_images(scanned, loaded)
        verify_migrations(loaded["backend"], directory, migrations)

        rebuilt = output("docker", "build", "--quiet", "--pull=false", "-", input=(
            "FROM plogit-backend:ci\n"
            "LABEL org.plogit.verification.fixture=changed-config\n"
        ))
        modified = {**loaded, "backend": identity(rebuilt)}
        try:
            verify_images(scanned, modified)
        except ValueError as error:
            print(f"Expected mismatch rejected: {error}")
        else:
            raise SystemExit("The modified image was accepted")
        verify_images(scanned, {name: identity(tag) for name, tag in TAGS.items()})
        print(json.dumps({
            "source_sha": source,
            "actions_run": os.environ["GITHUB_RUN_ID"],
            "actions_attempt": os.environ["GITHUB_RUN_ATTEMPT"],
            "local_image_ids": scanned,
            "image_archive_sha256": archive_hash,
            "flyway_migrations_sha256": migrations,
            "test_rollback_source_sha": rollback,
        }, sort_keys=True))


if __name__ == "__main__":
    main()

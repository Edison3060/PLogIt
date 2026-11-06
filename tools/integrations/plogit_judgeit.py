#!/usr/bin/env python3
"""Opt-in, file-only compatibility wrapper for legacy PLogIt JSON exports.

This is a provisional JudgeIt import candidate, not a JudgeIt API client.
The original PLogIt JSON remains unchanged under "payload".
"""

from __future__ import annotations

import argparse
import hashlib
import json
import os
import re
import sys
import tempfile
import uuid
from pathlib import Path

CONTRACT_VERSION = "plogit-judgeit-provisional/0.1"
EVIDENCE_COMPLETENESS = "attachment-filenames-only-unverified"
MAX_INPUT_BYTES = 16 * 1024 * 1024
MAX_LOGS = 10000
NAMESPACE_PATTERN = re.compile(r"[A-Za-z0-9][A-Za-z0-9._:-]{0,127}\Z")


class ContractError(ValueError):
    """An input file cannot be safely mapped to the provisional contract."""


def _unique_pairs(pairs: list[tuple[str, object]]) -> dict:
    result = {}
    for key, value in pairs:
        if key in result:
            raise ContractError("Duplicate JSON property")
        result[key] = value
    return result


def _reject_constant(value: str) -> None:
    raise ContractError("Non-finite JSON number is unsupported")


def _is_int(value: object) -> bool:
    return type(value) is int


def _required_string(obj: dict, key: str) -> str:
    value = obj.get(key)
    if not isinstance(value, str) or not value.strip():
        raise ContractError("Missing or invalid " + key)
    return value


def _validate_payload(payload: object) -> list[str]:
    if not isinstance(payload, dict):
        raise ContractError("Export must be a JSON object")
    if "schemaVersion" in payload:
        raise ContractError("This bridge supports only legacy unversioned PLogIt exports")
    engagement_id = payload.get("engagementId")
    if not _is_int(engagement_id) or engagement_id <= 0:
        raise ContractError("Invalid engagementId")
    _required_string(payload, "engagementName")
    _required_string(payload, "exportedAt")
    total = payload.get("totalLogCount")
    logs = payload.get("logs")
    if not _is_int(total) or total < 0:
        raise ContractError("Invalid totalLogCount")
    if not isinstance(logs, list) or len(logs) > MAX_LOGS:
        raise ContractError("Invalid or oversized logs array")
    if len(logs) > total:
        raise ContractError("Exported logs exceed totalLogCount")

    ids = []
    seen = set()
    for log in logs:
        if not isinstance(log, dict):
            raise ContractError("Invalid log record")
        raw_id = _required_string(log, "id")
        try:
            parsed = uuid.UUID(raw_id)
        except (ValueError, AttributeError) as exc:
            raise ContractError("Invalid source log UUID") from exc
        if str(parsed) != raw_id.lower():
            raise ContractError("Noncanonical source log UUID")
        if raw_id.lower() in seen:
            raise ContractError("Duplicate source log UUID")
        seen.add(raw_id.lower())
        if log.get("reviewState") not in ("APPROVED", "EXPORTED"):
            raise ContractError("Export includes ineligible review state")
        _required_string(log, "title")
        attachments = log.get("attachmentFilenames")
        if not isinstance(attachments, list) or any(
            not isinstance(name, str) or not name or len(name) > 1024
            for name in attachments
        ):
            raise ContractError("Invalid attachmentFilenames")
        ids.append(raw_id)
    return ids


def convert(payload: object, namespace: str) -> dict:
    """Validate and wrap the real legacy object; do not alter its fields."""
    if not isinstance(namespace, str) or not NAMESPACE_PATTERN.fullmatch(namespace):
        raise ContractError("Invalid source namespace")
    ids = _validate_payload(payload)
    canonical = json.dumps(
        payload, ensure_ascii=False, sort_keys=True,
        separators=(",", ":"), allow_nan=False,
    ).encode("utf-8")
    digest = hashlib.sha256(canonical).hexdigest()
    import_key = hashlib.sha256(
        namespace.encode("utf-8") + b"\x00" + digest.encode("ascii")
    ).hexdigest()
    return {
        "contractVersion": CONTRACT_VERSION,
        "sourceApp": "PLogIt",
        "sourceNamespace": namespace,
        "sourceEngagementId": payload["engagementId"],
        "sourceLogIds": ids,
        "payloadSha256": digest,
        "importKey": import_key,
        "evidenceCompleteness": EVIDENCE_COMPLETENESS,
        "payload": payload,
    }


def wrap_file(source: Path, destination: Path, namespace: str) -> None:
    if source.resolve() == destination.resolve():
        raise ContractError("Output must not overwrite the source export")
    with source.open("rb") as handle:
        raw = handle.read(MAX_INPUT_BYTES + 1)
    if len(raw) > MAX_INPUT_BYTES:
        raise ContractError("Input exceeds 16 MiB limit")
    try:
        payload = json.loads(
            raw, object_pairs_hook=_unique_pairs, parse_constant=_reject_constant,
        )
    except (UnicodeDecodeError, json.JSONDecodeError) as exc:
        raise ContractError("Input is not valid JSON") from exc
    envelope = convert(payload, namespace)
    content = (json.dumps(
        envelope, ensure_ascii=False, indent=2, allow_nan=False,
    ) + "\n").encode("utf-8")

    # Write private data atomically with owner-only permissions; no network use.
    temporary = None
    try:
        with tempfile.NamedTemporaryFile(
            mode="wb", dir=destination.parent, prefix=".plogit-import-",
            delete=False,
        ) as handle:
            temporary = Path(handle.name)
            os.fchmod(handle.fileno(), 0o600)
            handle.write(content)
        os.replace(temporary, destination)
    finally:
        if temporary is not None:
            temporary.unlink(missing_ok=True)


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Wrap a legacy PLogIt JSON file for provisional JudgeIt import"
    )
    parser.add_argument("--input", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument(
        "--namespace", required=True,
        help="Non-secret, operator-assigned source instance identifier",
    )
    args = parser.parse_args()
    try:
        wrap_file(args.input, args.output, args.namespace)
    except (ContractError, OSError) as exc:
        print("Bridge rejected input: " + str(exc), file=sys.stderr)
        return 2
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

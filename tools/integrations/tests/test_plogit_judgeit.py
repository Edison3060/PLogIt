"""Synthetic, file-only contract tests; no downstream service is simulated as live."""

import copy
import importlib.util
import json
import os
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

TOOLS = Path(__file__).resolve().parents[1]
SCRIPT = TOOLS / "plogit_judgeit.py"
FIXTURE = TOOLS / "fixtures" / "plogit-export-synthetic.json"
spec = importlib.util.spec_from_file_location("plogit_judgeit", SCRIPT)
bridge = importlib.util.module_from_spec(spec)
spec.loader.exec_module(bridge)


class ProvisionalBridgeTests(unittest.TestCase):
    def setUp(self):
        self.payload = json.loads(FIXTURE.read_text(encoding="utf-8"))
        self.namespace = "lab-plogit-instance"

    def test_wrap_preserves_exact_legacy_payload_and_source_ids(self):
        original = copy.deepcopy(self.payload)
        result = bridge.convert(self.payload, self.namespace)
        self.assertEqual(result["payload"], original)
        self.assertEqual(self.payload, original)
        self.assertEqual(result["sourceApp"], "PLogIt")
        self.assertEqual(result["contractVersion"], bridge.CONTRACT_VERSION)
        self.assertEqual(result["sourceEngagementId"], 42)
        self.assertEqual(result["sourceLogIds"], [log["id"] for log in self.payload["logs"]])
        self.assertEqual(result["evidenceCompleteness"], "attachment-filenames-only-unverified")
        self.assertEqual(result["payload"]["logs"][0]["attachmentFilenames"], ["screenshot.png"])
        self.assertNotIn("schemaVersion", result["payload"])
        self.assertNotIn("severity", result["payload"]["logs"][0])

    def test_hash_is_deterministic_and_namespace_scoped(self):
        first = bridge.convert(self.payload, self.namespace)
        reordered = dict(reversed(list(self.payload.items())))
        second = bridge.convert(reordered, self.namespace)
        self.assertEqual(first["importKey"], second["importKey"])
        self.assertEqual(first["payloadSha256"], second["payloadSha256"])
        other = bridge.convert(self.payload, "different-instance")
        self.assertNotEqual(first["importKey"], other["importKey"])
        self.assertEqual(first["payloadSha256"], other["payloadSha256"])

    def test_reject_nonapproved_activity(self):
        self.payload["logs"][0]["reviewState"] = "DRAFT"
        with self.assertRaises(bridge.ContractError):
            bridge.convert(self.payload, self.namespace)

    def test_reject_duplicate_and_invalid_source_ids(self):
        self.payload["logs"][1]["id"] = self.payload["logs"][0]["id"]
        with self.assertRaises(bridge.ContractError):
            bridge.convert(self.payload, self.namespace)
        self.payload["logs"][1]["id"] = "not-a-uuid"
        with self.assertRaises(bridge.ContractError):
            bridge.convert(self.payload, self.namespace)

    def test_reject_unknown_legacy_shape_and_invalid_counts(self):
        self.payload["schemaVersion"] = "10"
        with self.assertRaises(bridge.ContractError):
            bridge.convert(self.payload, self.namespace)
        del self.payload["schemaVersion"]
        self.payload["totalLogCount"] = 1
        with self.assertRaises(bridge.ContractError):
            bridge.convert(self.payload, self.namespace)

    def test_reject_invalid_attachment_list_and_namespace(self):
        self.payload["logs"][0]["attachmentFilenames"] = "../secret"
        with self.assertRaises(bridge.ContractError):
            bridge.convert(self.payload, self.namespace)
        self.payload["logs"][0]["attachmentFilenames"] = ["screenshot.png"]
        for namespace in ("", "../prod", "a b", "x" * 129):
            with self.assertRaises(bridge.ContractError):
                bridge.convert(self.payload, namespace)

    def test_reject_duplicate_json_keys(self):
        with tempfile.TemporaryDirectory() as directory:
            inp, out = Path(directory) / "bad.json", Path(directory) / "out.json"
            inp.write_text('{"engagementId":42,"engagementId":99}', encoding="utf-8")
            with self.assertRaises(bridge.ContractError):
                bridge.wrap_file(inp, out, self.namespace)
            self.assertFalse(out.exists())

    def test_reject_oversized_input_and_does_not_overwrite_source(self):
        with tempfile.TemporaryDirectory() as directory:
            inp, out = Path(directory) / "big.json", Path(directory) / "out.json"
            with inp.open("wb") as handle:
                handle.seek(bridge.MAX_INPUT_BYTES)
                handle.write(b"X")
            with self.assertRaises(bridge.ContractError):
                bridge.wrap_file(inp, out, self.namespace)
            self.assertFalse(out.exists())
            with self.assertRaises(bridge.ContractError):
                bridge.wrap_file(FIXTURE, FIXTURE, self.namespace)

    def test_cli_writes_private_file_and_round_trips_synthetic_fixture(self):
        with tempfile.TemporaryDirectory() as directory:
            out = Path(directory) / "judgeit-provisional.json"
            process = subprocess.run(
                [sys.executable, str(SCRIPT), "--input", str(FIXTURE),
                 "--output", str(out), "--namespace", self.namespace],
                text=True, capture_output=True, check=False,
            )
            self.assertEqual(process.returncode, 0, process.stderr)
            wrapped = json.loads(out.read_text(encoding="utf-8"))
            self.assertEqual(wrapped["payload"], self.payload)
            self.assertEqual(wrapped["sourceNamespace"], self.namespace)
            if os.name != "nt":
                self.assertEqual(out.stat().st_mode & 0o777, 0o600)


if __name__ == "__main__":
    unittest.main()

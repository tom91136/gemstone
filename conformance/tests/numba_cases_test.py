import json
import os
import subprocess
import sys
import tempfile
import textwrap
import unittest

SCRIPT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "interpreters", "python", "numba_cases.py")
CONFIG = os.path.abspath(os.path.join(os.path.dirname(SCRIPT), "pytest.ini"))
SKIP_STATUS = 4

FAKE_PYTEST = """
import json
import os
import sys

with open(os.environ["FAKE_PYTEST_ARGV"], "w") as handle:
    json.dump(sys.argv[1:], handle)
sys.stdout.write(os.environ["FAKE_PYTEST_OUTPUT"] + "\\n")
sys.exit(int(os.environ["FAKE_PYTEST_STATUS"]))
"""

FAKE_PYTEST_PACKAGE = """
import json
import os
import sys
import types


def main(args, plugins=()):
    with open(os.environ["FAKE_PYTEST_ARGV"], "w") as handle:
        json.dump(list(args), handle)
    if "no:terminal" in args and ("-q" in args or "--no-header" in args):
        sys.stderr.write("ERROR: usage: unrecognized arguments: -q --no-header\\n")
        return 4
    for event in json.loads(os.environ.get("FAKE_PYTEST_EVENTS", "[]")):
        for plugin in plugins:
            if event["event"] == "start":
                plugin.pytest_runtest_logstart(event["nodeid"], None)
            else:
                report = types.SimpleNamespace(
                    when=event["when"],
                    passed=event["outcome"] == "passed",
                    failed=event["outcome"] == "failed",
                    skipped=event["outcome"] == "skipped",
                    longreprtext=event.get("text", ""),
                    sections=[tuple(section) for section in event.get("sections", [])],
                )
                if event.get("xfail"):
                    report.wasxfail = ""
                plugin.pytest_runtest_logreport(report)
    return int(os.environ.get("FAKE_PYTEST_STATUS", "0"))
"""

FAKE_DRIVER = """
import os
import enum

class CUresult(enum.IntEnum):
    CUDA_SUCCESS = 0

class CUdriverProcAddressQueryResult(enum.IntEnum):
    CU_GET_PROC_ADDRESS_SUCCESS = 0
    CU_GET_PROC_ADDRESS_SYMBOL_NOT_FOUND = 1

ABSENT = set(filter(None, os.environ.get("FAKE_DRIVER_ABSENT", "").split(",")))

def cuDriverGetVersion():
    return CUresult.CUDA_SUCCESS, 13030

def cuInit(flags):
    return (CUresult.CUDA_SUCCESS,)

def cuGetProcAddress(name, version, flags):
    found = CUdriverProcAddressQueryResult.CU_GET_PROC_ADDRESS_SYMBOL_NOT_FOUND if name.decode() in ABSENT else CUdriverProcAddressQueryResult.CU_GET_PROC_ADDRESS_SUCCESS
    return CUresult.CUDA_SUCCESS, 0, found

def cuCalledByNumba():
    pass

def cuCalledByNobody():
    pass
"""

FAKE_DRVAPI = """
API_PROTOTYPES = {"cuCalledByNumba": (), "cuDriverGetVersion": ()}
"""


class NumbaCases(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)
        root = self.directory.name
        self.write("pytest/__init__.py", FAKE_PYTEST_PACKAGE)
        self.write("pytest/__main__.py", FAKE_PYTEST)
        self.write("numba/__init__.py", "")
        self.write("numba/cuda/__init__.py", "")
        self.write("numba/cuda/tests/__init__.py", "")
        self.write("numba/cuda/cudadrv/__init__.py", "")
        self.write("numba/cuda/cudadrv/drvapi.py", FAKE_DRVAPI)
        self.write("cuda/__init__.py", "")
        self.write("cuda/bindings/__init__.py", "")
        self.write("cuda/bindings/driver.py", FAKE_DRIVER)
        self.environment = dict(
            os.environ,
            PYTHONPATH=root,
            PYTHONNOUSERSITE="1",
            PYTHONDONTWRITEBYTECODE="1",
            FAKE_PYTEST_ARGV=os.path.join(root, "argv.json"),
        )

    def write(self, relative, content):
        path = os.path.join(self.directory.name, relative)
        os.makedirs(os.path.dirname(path), exist_ok=True)
        with open(path, "w") as handle:
            handle.write(textwrap.dedent(content))

    def run_script(self, arguments, pytest_output="", pytest_status=0, absent=""):
        environment = dict(
            self.environment, FAKE_PYTEST_OUTPUT=pytest_output, FAKE_PYTEST_STATUS=str(pytest_status), FAKE_DRIVER_ABSENT=absent
        )
        completed = subprocess.run(
            [sys.executable, SCRIPT, *arguments],
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            env=environment,
        )
        return completed.returncode, completed.stdout

    def run_case(self, case, pytest_output="", pytest_status=0, absent=""):
        return self.run_script(["--case", case], pytest_output, pytest_status, absent)

    def pytest_arguments(self):
        with open(os.path.join(self.directory.name, "argv.json")) as handle:
            return json.load(handle)

    def assertUsesShippedConfiguration(self, arguments):
        self.assertIn("-c", arguments)
        self.assertEqual(arguments[arguments.index("-c") + 1], CONFIG)
        self.assertIn("--rootdir", arguments)
        self.assertEqual(arguments[arguments.index("--rootdir") + 1], os.path.join(self.directory.name, "numba", "cuda", "tests"))
        self.assertIn("numba_cases_pytest", arguments)
        self.assertEqual(arguments[arguments.index("numba_cases_pytest") - 1], "-p")

    def classification(self, output):
        markers = [line for line in output.splitlines() if line.startswith("conformance-classification: ")]
        self.assertLessEqual(len(markers), 1, output)
        return markers[0].split(": ", 1)[1] if markers else None

    def test_passing_test_exits_zero(self):
        status, output = self.run_case("cudapy/test_x.py::T::test", "1 passed in 0.01s")
        self.assertEqual(status, 0, output)
        self.assertIsNone(self.classification(output))

    def test_cases_run_pytest_with_the_shipped_configuration(self):
        status, output = self.run_case("cudapy/test_x.py::T::test", "1 passed in 0.01s")
        self.assertEqual(status, 0, output)
        arguments = self.pytest_arguments()
        self.assertUsesShippedConfiguration(arguments)
        self.assertEqual(arguments[-1], "cudapy/test_x.py::T::test")

    def test_listing_collects_with_the_shipped_configuration(self):
        status, output = self.run_script(["--list"], "cudapy/test_x.py::T::test")
        self.assertEqual(status, 0, output)
        self.assertIn("cudapy/test_x.py::T::test\tcudapy/test_x.py\n", output)
        self.assertIn("inventory\n", output)
        self.assertIn("smoke/vector_add\n", output)
        arguments = self.pytest_arguments()
        self.assertIn("--collect-only", arguments)
        self.assertUsesShippedConfiguration(arguments)

    def test_skipped_test_reports_the_skip_status(self):
        status, output = self.run_case("cudapy/test_x.py::T::test", "SKIPPED [1] x.py:3: needs a device\n1 skipped in 0.01s")
        self.assertEqual(status, SKIP_STATUS, output)
        self.assertIsNone(self.classification(output))

    def test_pytest_usage_error_is_a_failure_not_a_skip(self):
        status, output = self.run_case("cudapy/test_x.py::T::test", "ERROR: file or directory not found", pytest_status=4)
        self.assertEqual(status, 1, output)

    def test_no_tests_collected_is_a_failure(self):
        status, output = self.run_case("cudapy/test_x.py::T::test", "no tests ran in 0.01s", pytest_status=5)
        self.assertEqual(status, 1, output)

    def test_gemstone_error_code_is_an_emulator_defect(self):
        status, output = self.run_case(
            "cudapy/test_x.py::T::test", "numba.cuda.cudadrv.driver.CudaAPIError: [801] Call to cuFoo results in CUDA_ERROR_NOT_SUPPORTED\n1 failed", 1
        )
        self.assertEqual(status, 1, output)
        self.assertEqual(self.classification(output), "emulator defect")

    def test_missing_symbol_is_a_missing_entry_point(self):
        status, output = self.run_case("cudapy/test_x.py::T::test", "CUDA_ERROR_NOT_FOUND\n1 failed", 1)
        self.assertEqual(self.classification(output), "missing entry point")

    def test_missing_kernel_image_is_missing_code(self):
        status, output = self.run_case("cudapy/test_x.py::T::test", "CUDA_ERROR_NO_BINARY_FOR_GPU: no kernel image is available\n1 failed", 1)
        self.assertEqual(self.classification(output), "missing code for the target")

    def test_known_package_mismatches_are_not_emulator_defects(self):
        mismatches = [
            "AssertionError: 'red.add.f64' not found in '.version 9.3 atom.global.add.f64'\n1 failed",
            "numba.core.errors.TypingError: Signature mismatch: 2 argument types given, but function takes 1 arguments\n1 failed",
        ]
        for text in mismatches:
            with self.subTest(text=text):
                status, output = self.run_case("cudapy/test_x.py::T::test", text, 1)
                self.assertEqual(status, 1, output)
                self.assertEqual(self.classification(output), "package mismatch")

    def test_unexplained_failure_is_unclassified(self):
        status, output = self.run_case("cudapy/test_x.py::T::test", "AssertionError: 3 != 4\n1 failed", 1)
        self.assertEqual(status, 1, output)
        self.assertEqual(self.classification(output), "unclassified")

    def run_batch(self, cases, events):
        path = os.path.join(self.directory.name, "cases.txt")
        with open(path, "w") as handle:
            handle.write("".join(case + "\n" for case in cases))
        environment = dict(self.environment, FAKE_PYTEST_EVENTS=json.dumps(events))
        completed = subprocess.run(
            [sys.executable, SCRIPT, "--batch", path],
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            env=environment,
        )
        return completed.returncode, completed.stdout

    def batch_records(self, output):
        records = []
        for line in output.splitlines():
            if line.startswith("conformance-case-begin: "):
                records.append([line.split(": ", 1)[1], [], None])
            elif line.startswith("conformance-case-end: "):
                records[-1][2] = int(line.split(": ", 1)[1])
            elif records and records[-1][2] is None:
                records[-1][1].append(line)
        return records

    @staticmethod
    def phases(nodeid, call="passed", setup="passed", text="", sections=(), xfail=False):
        events = [{"event": "start", "nodeid": nodeid}]
        events.append({"event": "report", "when": "setup", "outcome": setup})
        if setup == "passed":
            events.append(
                {"event": "report", "when": "call", "outcome": call, "text": text, "sections": list(sections), "xfail": xfail}
            )
        events.append({"event": "report", "when": "teardown", "outcome": "passed"})
        return events

    def test_batch_runs_every_case_in_one_pytest_with_the_shipped_configuration(self):
        cases = ["cudapy/test_x.py::T::a", "cudapy/test_x.py::T::b[1]"]
        events = self.phases(cases[0]) + self.phases(cases[1])
        status, output = self.run_batch(cases, events)
        self.assertEqual(status, 0, output)
        arguments = self.pytest_arguments()
        self.assertUsesShippedConfiguration(arguments)
        self.assertEqual(arguments[-2:], cases)
        self.assertIn("no:terminal", arguments)
        self.assertEqual([(name, code) for name, _, code in self.batch_records(output)], [(cases[0], 0), (cases[1], 0)])

    def test_batch_failure_prints_its_text_and_classification_before_its_end_marker(self):
        cases = ["cudapy/test_x.py::T::a", "cudapy/test_x.py::T::b", "cudapy/test_x.py::T::c"]
        events = (
            self.phases(cases[0])
            + self.phases(cases[1], call="failed", text="numba.cuda.cudadrv.driver.CudaAPIError: CUDA_ERROR_NOT_SUPPORTED")
            + self.phases(cases[2], call="failed", text="AssertionError: 3 != 4", sections=[("Captured stdout call", "noise")])
        )
        status, output = self.run_batch(cases, events)
        self.assertEqual(status, 0, output)
        first, second, third = self.batch_records(output)
        self.assertEqual(first, [cases[0], [], 0])
        self.assertEqual(second[2], 1)
        self.assertIn("CUDA_ERROR_NOT_SUPPORTED", "\n".join(second[1]))
        self.assertEqual(self.classification("\n".join(second[1])), "emulator defect")
        self.assertEqual(third[2], 1)
        self.assertIn("noise", "\n".join(third[1]))
        self.assertNotIn("CUDA_ERROR", "\n".join(third[1]))
        self.assertEqual(self.classification("\n".join(third[1])), "unclassified")

    def test_batch_skips_report_the_skip_status_and_xfails_pass(self):
        cases = ["cudapy/test_x.py::T::a", "cudapy/test_x.py::T::b", "cudapy/test_x.py::T::c"]
        events = (
            self.phases(cases[0], setup="skipped")
            + self.phases(cases[1], call="skipped", xfail=True)
            + self.phases(cases[2], call="skipped")
        )
        status, output = self.run_batch(cases, events)
        self.assertEqual(status, 0, output)
        self.assertEqual([code for _, _, code in self.batch_records(output)], [SKIP_STATUS, 0, SKIP_STATUS])

    def test_batch_failure_in_teardown_fails_the_case(self):
        case = "cudapy/test_x.py::T::a"
        events = [
            {"event": "start", "nodeid": case},
            {"event": "report", "when": "setup", "outcome": "passed"},
            {"event": "report", "when": "call", "outcome": "passed"},
            {"event": "report", "when": "teardown", "outcome": "failed", "text": "RuntimeError: teardown"},
        ]
        status, output = self.run_batch([case], events)
        self.assertEqual(status, 0, output)
        self.assertEqual(self.batch_records(output)[0][2], 1)

    def test_inventory_passes_when_every_numba_entry_point_resolves(self):
        status, output = self.run_case("inventory", absent="cuCalledByNobody")
        self.assertEqual(status, 0, output)
        self.assertIn("cuCalledByNobody", output)

    def test_inventory_fails_when_an_entry_point_numba_calls_is_missing(self):
        status, output = self.run_case("inventory", absent="cuCalledByNumba")
        self.assertNotEqual(status, 0, output)
        self.assertEqual(self.classification(output), "missing entry point")
        self.assertIn("cuCalledByNumba", output)


if __name__ == "__main__":
    unittest.main()

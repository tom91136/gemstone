"""Numba conformance cases for gemstone.

The program speaks the conformance `cases` protocol: --list prints one case per line, --case runs one of them.
Cases are the package's own pytest node identifiers (paths relative to numba/cuda/tests) plus a driver entry-point
inventory and a kernel smoke case. Every failure prints a conformance-classification marker derived from its output.
A pytest case lists its test module as its batch group, and --batch runs the cases named in a file in one pytest process,
bracketing each with conformance-case-begin and conformance-case-end markers.
"""

import argparse
import re
import os
import subprocess
import sys
import traceback


SKIP_STATUS = 4
HERE = os.path.dirname(os.path.abspath(__file__))
PYTEST_CONFIG = os.path.join(HERE, "pytest.ini")

PACKAGE_MISMATCHES = (
    re.compile(r"AssertionError: '(?:atom|red)(?:\.shared)?\.add\.f64' not found"),
    re.compile(r"Signature mismatch: \d+ argument types given, but function takes \d+ arguments"),
)


def classify(text):
    if "no kernel image is available" in text or "invalid device function" in text:
        return "missing code for the target"
    if "CUDA_ERROR_NOT_FOUND" in text or "SYMBOL_NOT_FOUND" in text:
        return "missing entry point"
    if re.search(r"CUDA_ERROR_[A-Z_]+", text):
        return "emulator defect"
    if any(pattern.search(text) for pattern in PACKAGE_MISMATCHES):
        return "package mismatch"
    return "unclassified"


def report(kind):
    print(f"conformance-classification: {kind}")


def tests_root():
    import numba.cuda.tests

    return os.path.dirname(os.path.abspath(numba.cuda.tests.__file__))


def pytest_command(root, *arguments):
    plugins = ["-p", "numba_cases_pytest", "-p", "no:cacheprovider"]
    return [sys.executable, "-m", "pytest", "-c", PYTEST_CONFIG, "--rootdir", root, *plugins, *arguments]


def pytest_environment():
    paths = filter(None, [HERE, os.environ.get("PYTHONPATH")])
    return dict(os.environ, PYTHONPATH=os.pathsep.join(paths))


def list_pytest_cases(root):
    completed = subprocess.run(
        pytest_command(root, "--collect-only", "-q", "--no-header", "."),
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        env=pytest_environment(),
        cwd=root,
    )
    if completed.returncode != 0:
        sys.stderr.write(completed.stdout)
        raise SystemExit(f"pytest collection failed with status {completed.returncode}")
    cases = []
    for line in completed.stdout.splitlines():
        line = line.strip()
        if "::" not in line:
            continue
        path, name = line.split("::", 1)
        if os.path.isabs(path):
            path = os.path.relpath(path, root)
        cases.append(f"{path}::{name}")
    return cases


def inventory():
    import cuda.bindings.driver as driver
    from numba.cuda.cudadrv.drvapi import API_PROTOTYPES

    status, version = driver.cuDriverGetVersion()
    if status != driver.CUresult.CUDA_SUCCESS:
        report(classify(str(status)))
        raise SystemExit(f"cuDriverGetVersion failed: {status}")
    (status,) = driver.cuInit(0)
    if status != driver.CUresult.CUDA_SUCCESS:
        report(classify(str(status)))
        raise SystemExit(f"cuInit failed: {status}")

    def missing_from(names):
        missing = []
        for name in names:
            status, _, result = driver.cuGetProcAddress(name.encode(), version, 0)
            if result != driver.CUdriverProcAddressQueryResult.CU_GET_PROC_ADDRESS_SUCCESS:
                missing.append(name)
        return missing

    called = sorted(API_PROTOTYPES)
    exposed = sorted(
        name
        for name in dir(driver)
        if name.startswith("cu")
        and not name.startswith("CU")
        and callable(getattr(driver, name))
        and not isinstance(getattr(driver, name), type)
    )
    # cuda-bindings also exposes the versioned C names; the unversioned binding is the one it calls.
    names = set(exposed)
    exposed = [name for name in exposed if not (re.match(r"^.*_v[0-9]+$", name) and re.sub(r"_v[0-9]+$", "", name) in names)]
    unused = missing_from(name for name in exposed if name not in API_PROTOTYPES)
    missing = missing_from(called)
    print(f"libcuda: driver API {version}")
    print(f"entry points called by numba: {len(called)}")
    print(f"missing entry points called by numba: {len(missing)}")
    for name in missing:
        print(f"  {name}")
    print(f"entry points exposed by cuda-bindings and not called by numba: {len(exposed) - len(called)}")
    print(f"missing entry points not called by numba: {len(unused)}")
    for name in unused:
        print(f"  {name}")
    if missing:
        report("missing entry point")
        raise SystemExit(1)


def smoke_vector_add():
    import numpy as np
    from numba import cuda

    @cuda.jit
    def add(x, y, out):
        i = cuda.grid(1)
        if i < out.size:
            out[i] = x[i] + y[i]

    size = 4096
    x = np.arange(size, dtype=np.float32)
    y = x * 2
    expected = x + y
    out = np.zeros_like(expected)
    device = cuda.get_current_device()
    print(f"device: {device.name!r}")
    x_device = cuda.to_device(x)
    y_device = cuda.to_device(y)
    out_device = cuda.device_array_like(out)
    add[(size + 127) // 128, 128](x_device, y_device, out_device)
    out_device.copy_to_host(out)
    np.testing.assert_allclose(out, expected)
    print(f"vector add: {size} elements, sum {out.sum():.1f}")


def pytest_outcomes(output):
    lines = [line for line in output.splitlines() if line.strip()]
    summary = lines[-1] if lines else ""
    return {outcome: int(count) for count, outcome in re.findall(r"(\d+) (passed|failed|skipped|xfailed|xpassed|errors?)", summary)}


class CaseMarkers:
    def __init__(self, stream):
        self.stream = stream
        self.text = []

    def emit(self, text):
        self.stream.write(text + "\n")
        self.stream.flush()

    def pytest_runtest_logstart(self, nodeid, location):
        self.text = []
        self.failed = self.skipped = self.passed = False
        self.emit(f"conformance-case-begin: {nodeid}")

    def pytest_runtest_logreport(self, report):
        if report.failed:
            self.failed = True
            sections = "".join(f"\n----- {name} -----\n{content}" for name, content in report.sections)
            self.text.append(report.longreprtext + sections)
            self.emit(self.text[-1])
        elif report.skipped and not hasattr(report, "wasxfail"):
            self.skipped = True
        elif report.passed and report.when == "call":
            self.passed = True
        if report.when == "teardown":
            if self.failed:
                self.emit(f"conformance-classification: {classify(chr(10).join(self.text))}")
            status = 1 if self.failed else SKIP_STATUS if self.skipped and not self.passed else 0
            self.emit(f"conformance-case-end: {status}")


def run_batch(root, cases_path):
    import pytest

    with open(cases_path) as handle:
        identities = [line.rstrip("\n") for line in handle if line.strip()]
    stream = os.fdopen(os.dup(sys.stdout.fileno()), "w")
    os.chdir(root)
    plugins = ["-p", "numba_cases_pytest", "-p", "no:cacheprovider", "-p", "no:terminal"]
    pytest.main(["-c", PYTEST_CONFIG, "--rootdir", root, *plugins, *identities], plugins=[CaseMarkers(stream)])


def run_pytest(root, identity):
    completed = subprocess.run(
        pytest_command(root, "-q", "--no-header", identity),
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        env=pytest_environment(),
        cwd=root,
    )
    sys.stdout.write(completed.stdout)
    if completed.returncode == 0:
        outcomes = pytest_outcomes(completed.stdout)
        if outcomes.get("skipped", 0) > 0 and outcomes.get("passed", 0) == 0:
            raise SystemExit(SKIP_STATUS)
        raise SystemExit(0)
    report(classify(completed.stdout))
    raise SystemExit(1)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--list", action="store_true")
    parser.add_argument("--case")
    parser.add_argument("--batch")
    arguments = parser.parse_args()
    root = tests_root()
    if arguments.list:
        try:
            print("inventory")
            print("smoke/vector_add")
            for case in list_pytest_cases(root):
                print(f"{case}\t{case.split('::', 1)[0]}")
        except BrokenPipeError:
            devnull = os.open(os.devnull, os.O_WRONLY)
            os.dup2(devnull, sys.stdout.fileno())
            sys.exit(0)
        return
    if arguments.batch is not None:
        run_batch(root, arguments.batch)
        return
    if arguments.case is None:
        parser.error("--case is required unless --list or --batch is given")
    try:
        if arguments.case == "inventory":
            inventory()
        elif arguments.case == "smoke/vector_add":
            smoke_vector_add()
        else:
            run_pytest(root, arguments.case)
    except SystemExit:
        raise
    except Exception:
        text = traceback.format_exc()
        sys.stderr.write(text)
        report(classify(text))
        raise SystemExit(1)


if __name__ == "__main__":
    main()

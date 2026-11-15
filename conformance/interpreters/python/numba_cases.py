"""Numba conformance cases for gemstone.

The program speaks the conformance `cases` protocol: --list prints one case per line, --case runs one of them.
Cases are the package's own pytest node identifiers (paths relative to numba/cuda/tests) plus a driver entry-point
inventory and a kernel smoke case. Failures that carry a known cause print a conformance-classification marker.
"""

import argparse
import re
import os
import subprocess
import sys
import traceback


def classify(text):
    if "no kernel image is available" in text or "invalid device function" in text:
        return "missing code for the target"
    if "CUDA_ERROR_NOT_FOUND" in text or "SYMBOL_NOT_FOUND" in text:
        return "missing entry point"
    return "emulator defect"


def report(kind):
    print(f"conformance-classification: {kind}")


def tests_root():
    import numba.cuda.tests

    return os.path.dirname(os.path.abspath(numba.cuda.tests.__file__))


def list_pytest_cases(root):
    completed = subprocess.run(
        [sys.executable, "-m", "pytest", "--collect-only", "-q", "--no-header", "-p", "no:cacheprovider", "."],
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        env=os.environ,
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

    status, version = driver.cuDriverGetVersion()
    if status != driver.CUresult.CUDA_SUCCESS:
        report("emulator defect")
        raise SystemExit(f"cuDriverGetVersion failed: {status}")
    (status,) = driver.cuInit(0)
    if status != driver.CUresult.CUDA_SUCCESS:
        report("missing entry point")
        raise SystemExit(f"cuInit failed: {status}")
    functions = sorted(
        name
        for name in dir(driver)
        if name.startswith("cu")
        and not name.startswith("CU")
        and callable(getattr(driver, name))
        and not isinstance(getattr(driver, name), type)
    )
    # cuda-bindings also exposes the versioned C names; the unversioned binding is the one it calls.
    names = set(functions)
    functions = [name for name in functions if not (re.match(r"^.*_v[0-9]+$", name) and re.sub(r"_v[0-9]+$", "", name) in names)]
    missing = []
    for name in functions:
        status, _, result = driver.cuGetProcAddress(name.encode(), version, 0)
        if result != driver.CUdriverProcAddressQueryResult.CU_GET_PROC_ADDRESS_SUCCESS:
            missing.append(name)
    print(f"libcuda: driver API {version}")
    print(f"entry points referenced by cuda-bindings: {len(functions)}")
    print(f"missing entry points: {len(missing)}")
    for name in missing:
        print(f"  {name}")


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


def run_pytest(root, identity):
    completed = subprocess.run(
        [sys.executable, "-m", "pytest", "-q", "--no-header", "-p", "no:cacheprovider", identity],
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        env=os.environ,
        cwd=root,
    )
    sys.stdout.write(completed.stdout)
    if completed.returncode != 0:
        report(classify(completed.stdout))
    raise SystemExit(completed.returncode)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--list", action="store_true")
    parser.add_argument("--case")
    arguments = parser.parse_args()
    root = tests_root()
    if arguments.list:
        try:
            print("inventory")
            print("smoke/vector_add")
            for case in list_pytest_cases(root):
                print(case)
        except BrokenPipeError:
            devnull = os.open(os.devnull, os.O_WRONLY)
            os.dup2(devnull, sys.stdout.fileno())
            sys.exit(0)
        return
    if arguments.case is None:
        parser.error("--case is required unless --list is given")
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
        traceback.print_exc()
        report("emulator defect")
        raise SystemExit(1)


if __name__ == "__main__":
    main()

#!/usr/bin/env python3
"""Serialize a fixture build and record its envelope measurements; sbt remains the build driver."""
import fcntl
import json
import os
import signal
import shutil
import tempfile
from pathlib import Path
import subprocess
import sys
import time
import uuid

def publish(staging, destination):
    staging, destination = Path(staging), Path(destination)
    if (staging.parent != destination.parent or not destination.name or
            not staging.name.startswith('.' + destination.name + '.build.')):
        raise RuntimeError('artifact staging directory is not owned by this publication')
    if staging.is_symlink() or not staging.is_dir():
        raise RuntimeError('artifact staging directory is invalid')
    if not all((staging/name).is_file() for name in ('cases.jsonl', 'manifest.json')):
        raise RuntimeError('CTS artifact is incomplete')
    lock = destination.with_name('.' + destination.name + '.publish.lock')
    with lock.open('a+') as stream:
        fcntl.lockf(stream, fcntl.LOCK_EX)
        if destination.is_symlink():
            raise RuntimeError('artifact destination is a symlink')
        if all((destination/name).is_file() for name in ('cases.jsonl', 'manifest.json')):
            shutil.rmtree(staging)
            return
        previous = None
        if destination.exists():
            allowed = {'bin', 'lib', 'licenses', 'provenance', 'share', 'cases.jsonl', 'manifest.json'}
            if not destination.is_dir() or any(
                    child.name not in allowed and not (child.name.startswith('upstream-list-') and child.suffix == '.xml')
                    for child in destination.iterdir()):
                raise RuntimeError('unrecognized contents in incomplete artifact: ' + str(destination))
            previous = Path(tempfile.mkdtemp(prefix=destination.name + '.incomplete.', dir=destination.parent))
            previous.rmdir()
            destination.rename(previous)
            print('preserved incomplete artifact: ' + str(previous), file=sys.stderr)
        try:
            staging.rename(destination)
        except BaseException:
            if previous is not None and not destination.exists():
                previous.rename(destination)
            raise

if len(sys.argv) == 4 and sys.argv[1] == '--publish':
    publish(sys.argv[2], sys.argv[3])
    raise SystemExit(0)

lock_path, *command = sys.argv[1:]
Path(lock_path).parent.mkdir(parents=True, exist_ok=True)
with open(lock_path, "a+") as lock:
    fcntl.lockf(lock, fcntl.LOCK_EX)
    environment = dict(os.environ, GEMSTONE_FIXTURE_LOCK_HELD=lock_path)
    peak = None
    try:
        group = environment.get("GEMSTONE_RESOURCE_CGROUP")
        if group:
            peak = open(Path(group) / "memory.peak", "r+")
            peak.write("1")
            peak.flush()
    except OSError:
        if peak:
            peak.close()
        peak = None
    started = time.time()
    exit_code = -1
    try:
        child = subprocess.Popen(command, env=environment, start_new_session=True)
        def terminate(signum, _frame):
            os.killpg(child.pid, signal.SIGTERM)
            try:
                child.wait(timeout=4)
            except subprocess.TimeoutExpired:
                os.killpg(child.pid, signal.SIGKILL)
            raise SystemExit(128 + signum)
        signal.signal(signal.SIGTERM, terminate)
        signal.signal(signal.SIGINT, terminate)
        exit_code = child.wait()
    finally:
        peak_bytes = None
        if peak:
            peak.seek(0)
            peak_bytes = int(peak.read().strip())
            peak.close()
        report = environment.get("GEMSTONE_CONFORMANCE_REPORT_DIR")
        if report:
            directory = Path(report) / "measurements"
            directory.mkdir(parents=True, exist_ok=True)
            (directory / f"fixture-{uuid.uuid4()}.json").write_text(json.dumps({
                "kind": "fixture", "command": command, "started_epoch": started,
                "finished_epoch": time.time(), "elapsed_seconds": time.time() - started,
                "exit": exit_code, "scope_peak_bytes": peak_bytes,
                "build_workers": environment.get("CMAKE_BUILD_PARALLEL_LEVEL"),
            }))
    raise SystemExit(exit_code)

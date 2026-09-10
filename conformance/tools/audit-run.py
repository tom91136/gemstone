#!/usr/bin/env python3
"""Check scheduler/isolation evidence after a CTest run; never polls running workloads."""
import collections
import json
import importlib.util
from pathlib import Path
import subprocess
import sys

root = Path(sys.argv[1]).resolve()
matrix = root / 'ctest'
inventory = json.loads(subprocess.check_output(['ctest', '--test-dir', str(matrix), '--show-only=json-v1'], text=True))
locks = {}
for test in inventory['tests']:
    props = {item['name']: item['value'] for item in test.get('properties', [])}
    lock = props.get('RESOURCE_LOCK', [])
    if isinstance(lock, str): lock = [lock]
    if test['name'].startswith('gemstone/') and lock:
        raise RuntimeError('an emulated case holds an oracle GPU lock: ' + test['name'])
    locks[test['name']] = lock
cases = [json.loads(path.read_text()) for path in (matrix/'cases').glob('*.json')]

def maximum(records):
    events = sorted([(x['started_epoch'], 1) for x in records] + [(x['finished_epoch'], -1) for x in records])
    active = peak = 0
    for _, change in events:
        active += change; peak = max(peak, active)
    return peak

groups = collections.defaultdict(list)
for case in cases:
    for resource in locks.get(case['name'], []): groups[resource].append(case)
resource_peaks = {key: maximum(values) for key, values in groups.items()}
if any(value > 1 for value in resource_peaks.values()):
    raise RuntimeError('oracle GPU lock overlap: ' + str(resource_peaks))
emulated = [case for case in cases if case['name'].startswith('gemstone/')]
pids = sorted({case['server_pid'] for case in emulated if case.get('server_pid') is not None})
if len(pids) > 1: raise RuntimeError('cases used more than one server process')
zero_work = {case['name']: case for case in emulated
             if case['exit'] == 0 and not any((case.get('authenticated_requests') or {}).values())}
if zero_work:
    module_spec = importlib.util.spec_from_file_location('case_run', Path(__file__).with_name('case-run.py'))
    runner = importlib.util.module_from_spec(module_spec)
    module_spec.loader.exec_module(runner)
    for line in (matrix/'cases.jsonl').read_text().splitlines():
        spec = json.loads(line)
        name = 'gemstone/' + spec['name']
        if name not in zero_work:
            continue
        case = zero_work[name]
        expected = runner.emulator_attestation(spec, 'gemstone')
        runner.require(expected == case.get('attestation') and not expected['required'],
                       'unjustified zero-work exemption: ' + name)
        spec['attestation'] = expected
        output = Path(case['stdout']).read_text(errors='replace')
        if spec.get('cts'):
            spec['_cts_status'] = runner.validate_cts(spec, 'gemstone', output)
        runner.validate_attestation(spec, output, 0, 0)
        del zero_work[name]
    if zero_work:
        raise RuntimeError('zero-work case has no canonical plan: ' + next(iter(zero_work)))
skipped_oracles = [case for case in cases if case['name'].startswith('oracle/') and case['exit'] == 77 and case['process_exit'] is None]
result = dict(cases=len(cases), maximum_case_processes=maximum(cases), maximum_emulated_cases=maximum(emulated),
    oracle_resource_maximum=resource_peaks, server_pids=pids, no_process_oracle_skips=len(skipped_oracles))
(root/'scheduler-audit.json').write_text(json.dumps(result, indent=2))
print(json.dumps(result, indent=2))

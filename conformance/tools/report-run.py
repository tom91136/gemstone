#!/usr/bin/env python3
"""Summarize individual CTest verdicts, native integration checks, and measured fixture builds."""
import collections
import csv
import json
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

root = Path(sys.argv[1]).resolve()
rows = collections.defaultdict(lambda: dict(passed=0, failed=0, skipped=0, seconds=0.0, build_seconds=0.0,
    peak_bytes=0, peak_client_bytes=0, oracle_passed=0, oracle_failed=0, oracle_skipped=0,
    gemstone_passed=0, gemstone_failed=0, gemstone_skipped=0,
    comparison_passed=0, comparison_failed=0, comparison_skipped=0))
failures = []
reported_cases = set()
measured_cases = collections.defaultdict(list)

def component(name, classname=''):
    if name.startswith(('oracle/', 'gemstone/')):
        name = name.split('/', 1)[1]
    if name.startswith('unified/'):
        backend = name.split('/')[1]
        return 'Unified / ' + ('SYCL' if backend.startswith('sycl') else backend.upper())
    if name.startswith('cts/'):
        provider = name.split('/')[1]
        return 'CTS / ' + ('SYCL' if provider.startswith('sycl-') else provider.upper())
    if name.startswith('miniapps/'):
        return 'Miniapp / ' + name.split('/')[1]
    if name.startswith('native/'):
        return 'Native / ' + name.split('/')[1].upper()
    if 'CUDABoundary' in classname:
        return 'Native / CUDA'
    for backend in ('Host', 'CUDA', 'HIP', 'HSA', 'OpenCL', 'SYCL'):
        if f'Unified{backend}ConformanceSuite' in classname:
            return 'Unified / ' + backend.upper()
    return 'Integration / ' + classname.rsplit('.', 1)[-1]

xml_paths = list((root/'ctest').glob('results.xml'))
for path in (root/'junit').rglob('*.xml'):
    if any(part in ('gemstone-conformance-integration', 'gemstone-native-integration') for part in path.parts):
        xml_paths.append(path)
for path in sorted(xml_paths):
    try:
        tree = ET.parse(path)
    except ET.ParseError as error:
        failures.append(dict(name=str(path), message=str(error)))
        continue
    for test in tree.iter('testcase'):
        name, classname = test.get('name', ''), test.get('classname', '')
        if name == 'conformance/ctest' or name in reported_cases:
            continue
        reported_cases.add(name)
        row = rows[component(name, classname)]
        row['seconds'] += float(test.get('time', 0))
        errors = list(test.findall('failure')) + list(test.findall('error'))
        verdict = 'failed' if errors else 'skipped' if test.find('skipped') is not None else 'passed'
        row[verdict] += 1
        if name.startswith(('oracle/', 'gemstone/')):
            row[name.split('/')[0] + '_' + verdict] += 1
        if errors:
            failures.append(dict(name=name, classname=classname,
                message='\n'.join(error.get('message', '') + '\n' + (error.text or '') for error in errors)
                        + '\n' + (test.findtext('system-out') or '')))

comparison_file = root/'ctest/comparisons.xml'
if comparison_file.exists():
    for case in ET.parse(comparison_file).getroot().iter('testcase'):
        name = case.get('name', '')
        key = component(name.removeprefix('comparison/'))
        failure = case.find('failure')
        status = 'failed' if failure is not None else 'skipped' if case.find('skipped') is not None else 'passed'
        rows[key]['comparison_' + status] += 1
        if failure is not None:
            failures.append(dict(name=name, message=failure.get('message', 'paired comparison failed')))

fixtures = []
processes = {}
measurements = list((root/'measurements').glob('*.json')) + list((root/'ctest/cases').glob('*.json'))
for path in measurements:
    try:
        data = json.loads(path.read_text())
    except (ValueError, OSError):
        continue
    if data['kind'] == 'process':
        processes[tuple(data['command'])] = data
    elif data['kind'] == 'case':
        key = component(data['name'])
        measured_cases[key].append(data)
        rows[key]['peak_bytes'] = max(rows[key]['peak_bytes'], data.get('scope_peak_bytes') or 0)
        rows[key]['peak_client_bytes'] = max(rows[key]['peak_client_bytes'], data.get('process_peak_bytes') or 0)
    elif data['kind'] == 'fixture':
        fixtures.append(data)
        command = data['command']
        if '/unified/' in command[1]:
            key = 'Unified / ' + command[2].upper()
        elif '/cts/' in command[1]:
            key = 'CTS / ' + Path(command[1]).parent.name.upper()
        else:
            specs = command[5:]
            key = 'Miniapp / ' + (specs[0].split(':')[0] if specs else 'unknown')
        rows[key]['build_seconds'] += data['elapsed_seconds']
        rows[key]['peak_bytes'] = max(rows[key]['peak_bytes'], data.get('scope_peak_bytes') or 0)
for fixture in fixtures:
    process = processes.get(tuple(fixture['command']), {})
    fixture['stdout'], fixture['stderr'] = process.get('stdout'), process.get('stderr')
events = sorted([(x['started_epoch'], 1) for x in fixtures] + [(x['finished_epoch'], -1) for x in fixtures])
active = maximum = 0
for _, delta in events:
    active += delta
    maximum = max(maximum, active)

run = json.loads((root/'run.json').read_text()) if (root/'run.json').exists() else {}
for key in ['Unified / '+x for x in ('HOST', 'CUDA', 'HIP', 'HSA', 'OPENCL', 'SYCL')] + \
           ['Native / '+x for x in ('CUDA', 'HIP', 'HSA', 'OPENCL', 'LEVELZERO')] + \
           ['Miniapp / '+x for x in ('babelstream', 'minibude', 'cloverleaf', 'tealeaf')]:
    rows[key]
if not run.get('openacc_image'):
    rows['Miniapp / OpenACC (SDK not configured)']
result = []
for key, values in sorted(rows.items()):
    status = 'FAIL' if values['failed'] or values['comparison_failed'] else 'PASS' if values['passed'] else 'SKIP' if values['skipped'] else 'NOT RUN'
    unreported = [case for case in measured_cases[key] if case['name'] not in reported_cases]
    if unreported:
        status = 'INCOMPLETE'
        values['seconds'] += sum(case['elapsed_seconds'] for case in unreported)
    timed = [case for case in measured_cases[key] if 'started_epoch' in case and 'finished_epoch' in case]
    wall = max(case['finished_epoch'] for case in timed) - min(case['started_epoch'] for case in timed) if timed else None
    result.append(dict(component=key, status=status, measured_without_verdict=len(unreported), wall_seconds=wall, **values))
(root/'outcomes.json').write_text(json.dumps(dict(run=run, components=result, fixtures=fixtures,
    maximum_concurrent_fixture_builds=maximum, failures=failures), indent=2))
with (root/'outcomes.csv').open('w', newline='') as stream:
    writer = csv.DictWriter(stream, fieldnames=list(result[0]))
    writer.writeheader(); writer.writerows(result)
workers = ', '.join(sorted({str(x['build_workers']) for x in fixtures if x.get('build_workers')})) or 'not recorded'
lines = ['# ' + ('Interrupted ' if 'interrupted' in run.get('status', '') else '') + 'integration outcome', '',
    f'Run: `{root}`', '', f'Maximum concurrent fixture builds: **{maximum}**. Compiler workers per build: **{workers}**.', '',
    '| Component | Outcome | Pass / Fail / Skip | Oracle P/F/S | Gemstone P/F/S | Case time (sum) | Case wall span | Fixture builds | Peak client RSS |',
    '| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |']
for row in result:
    roles = [' / '.join(str(row[role+'_'+v]) for v in ('passed', 'failed', 'skipped')) for role in ('oracle', 'gemstone')]
    memory = f'{row["peak_client_bytes"] / 1024**3:.2f} GiB' if row['peak_client_bytes'] else '—'
    wall = f'{row["wall_seconds"]:.2f} s' if row['wall_seconds'] is not None else '—'
    lines.append(f'| {row["component"]} | {row["status"]} | {row["passed"]} / {row["failed"]} / {row["skipped"]} | '
                 f'{roles[0]} | {roles[1]} | {row["seconds"]:.2f} s | {wall} | {row["build_seconds"]:.2f} s | {memory} |')
if run.get('scope_peak_bytes'):
    lines += ['', f'Peak memory for the entire shared scope: **{run["scope_peak_bytes"] / 1024**3:.2f} GiB**.']
if run.get('phases'):
    lines += ['', '| Phase | Exit | Wall time |', '| --- | ---: | ---: |']
    lines += [f'| {x["name"]} | {x["exit"]} | {x["seconds"]:.2f} s |' for x in run['phases']]
lines += ['', 'Case times are summed durations; concurrent work overlaps. Wall spans run from the first case start to the last case finish for each component.',
    'Client RSS excludes the shared server. Per-fixture scope peaks remain in outcomes.json; shared server memory cannot be attributed to an individual concurrent case.',
    'An unavailable oracle is SKIP, not a native pass. INCOMPLETE means measured work has no final JUnit verdict.', '', '## Failures', '']
for failure in failures:
    lines += ['### '+failure['name'], '', '```text', failure['message'][:16000], '```', '']
(root/'outcomes.md').write_text('\n'.join(lines))
print(root/'outcomes.md')

compared = [row for row in result if any(row['comparison_' + status] for status in ('passed', 'failed', 'skipped'))]
if compared:
    with (root/'outcomes.md').open('a') as stream:
        stream.write('\nPaired validation runs after independent oracle/emulator execution.\n\n')
        stream.write('| Component | Comparison pass / fail / skip |\n| --- | ---: |\n')
        for row in compared:
            stream.write('| ' + row['component'] + ' | ' + ' / '.join(str(row['comparison_' + status]) for status in ('passed', 'failed', 'skipped')) + ' |\n')

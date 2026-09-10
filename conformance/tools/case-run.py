#!/usr/bin/env python3
"""Execute exactly one CTest case. CTest schedules jobs; one server owns all emulation workers."""
import json
import importlib.util
import math
import os
from pathlib import Path
import re
import signal
import socket
import struct
import subprocess
import sys
import time
import xml.etree.ElementTree as ET

CONTROL_MAGIC = 0x474D4330

EXECUTION_METADATA = json.loads((Path(__file__).resolve().parents[1] / 'unified' / 'execution-metadata.json').read_text())
PROVIDER_APIS = {'hip-hsa': 'hip', 'cuda': 'cuda', 'hip': 'hip', 'sycl-opencl': 'sycl', 'sycl-levelzero': 'sycl'}
NATIVE_SINGLE_PASS = 'SUMMARY pass=1 skip=0 fail=0 xfail=0 xpass=0 xskip=0 selected=1'

def cts_metadata(spec):
    metadata = spec.get('cts')
    require(isinstance(metadata, dict), 'CTS metadata is absent')
    parts = spec.get('name', '').split('/', 2)
    valid = len(parts) == 3 and parts[0] == 'cts' and (
        (metadata.get('suite') == 'opencl' and parts[1] == 'opencl' and spec.get('api') == 'opencl'
         and spec.get('validator') == 'opencl-cts') or
        (metadata.get('suite') == 'sycl' and parts[1] in ('sycl-levelzero', 'sycl-opencl') and spec.get('api') == 'sycl'
         and spec.get('validator') == 'catch2-junit'))
    require(valid and metadata.get('execution') in ('host', 'device') and
            re.fullmatch('[0-9a-f]{40}', metadata.get('revision', '')) and metadata.get('test_name'),
            'invalid CTS metadata')
    return metadata

def emulator_attestation(spec, role):
    """Classify only canonical, explicitly selected inventory cases; default to required."""
    if role != 'gemstone' or spec.get('api') == 'host':
        return dict(required=False, reason='not-applicable')
    required = dict(required=True, reason='device-request-required')
    cts = cts_metadata(spec) if spec.get('cts') else None
    if cts:
        if cts.get('execution') == 'host':
            return dict(required=False, reason='upstream-host-case')
        return required
    parts = spec.get('name', '').split('/', 2)
    if len(parts) != 3 or parts[0] != 'unified' or PROVIDER_APIS.get(parts[1]) != spec.get('api'):
        return required
    provider, case = parts[1:]
    provider = 'hip' if provider == 'hip-hsa' else provider
    selected = [arg.removeprefix('--case=') for arg in spec[role].get('command', []) if arg.startswith('--case=')]
    if selected != [case]:
        return required
    if case in EXECUTION_METADATA['host_only_cases'].get(provider, []):
        return dict(required=False, reason='host-only-semantics')
    for family, operations in EXECUTION_METADATA['empty_input_operations'].get(provider, {}).items():
        if case.startswith(family + '/'):
            fields = case[len(family) + 1:].split('/')
            if (len(fields) == 3 and fields[0] in operations and
                fields[1] in EXECUTION_METADATA['scalar_types'] and fields[2] == 'n0'):
                return dict(required=False, reason='empty-input')
    return required

def validate_attestation(spec, stdout, cuda, execution):
    count = cuda if spec['api'] == 'cuda' else execution
    if count > 0:
        return
    require(not spec['attestation']['required'],
            'case completed without an authenticated request from its own client session')
    if spec.get('cts'):
        require(spec.get('_cts_status') == 0, 'host CTS case lacks a validated upstream PASS')
        return
    summaries = [line.strip() for line in stdout.splitlines() if line.strip().startswith('SUMMARY ')]
    require(summaries == [NATIVE_SINGLE_PASS],
            'zero-device-work case did not report exactly one native PASS')

def receive(stream, count):
    result = bytearray()
    while len(result) < count:
        part = stream.recv(count - len(result))
        if not part:
            raise RuntimeError('shared server closed the control connection')
        result.extend(part)
    return bytes(result)

def control(server, operation, credential=None):
    with socket.create_connection(('127.0.0.1', server['port']), timeout=15) as stream:
        stream.sendall(struct.pack('>I', CONTROL_MAGIC) + server['token'].encode('ascii') + struct.pack('>I', operation)
                       + (credential.encode('ascii') if credential else b''))
        if operation == 1:
            return receive(stream, struct.unpack('>H', receive(stream, 2))[0]).decode('ascii')
        if operation == 2:
            return struct.unpack('>QQ', receive(stream, 16))
        if operation == 3:
            return struct.unpack('>I', receive(stream, 4))[0]
        raise ValueError(operation)

def require(condition, message):
    if not condition:
        raise RuntimeError(message)

def near(actual, expected, tolerance, name):
    require(math.isfinite(actual) and math.isfinite(expected), name + ' is not finite')
    require(abs(actual - expected) <= tolerance * max(1.0, abs(expected)),
            f'{name}: expected {expected}, got {actual}')

NUMBER = r'[+-]?(?:\d+(?:\.\d*)?|\.\d+)(?:[eE][+-]?\d+)?'

def validate(spec, role, stdout):
    workload = spec.get('validator')
    if not workload:
        return
    directory = Path(spec[role]['report_directory'])
    report = ''
    if workload in ('cloverleaf', 'tealeaf'):
        report = (directory / ('clover.out' if workload == 'cloverleaf' else 'tea.out')).read_text()
    output = stdout + '\n' + report
    if workload == 'babelstream':
        require('FAILED validation' not in output, 'BabelStream reported failed validation')
    elif workload == 'minibude':
        require('valid: true' in output, 'MiniBUDE did not report valid: true')
    elif workload == 'cloverleaf':
        require('- Outcome: PASSED' in output, 'CloverLeaf did not report PASSED')
        pressure = '(?:' + NUMBER + r'|[+-]?(?:nan|inf(?:inity)?))'
        fields = [NUMBER] * 7
        fields[3] = pressure
        pattern = r'^\s*step:\s*2\s+' + r'\s+'.join('(' + item + ')' for item in fields) + r'\s*$'
        match = re.search(pattern, report, re.M)
        require(match is not None, 'CloverLeaf final step summary is absent')
        expected = [100.0, 28.75, 0.2875, 0.0, 45.06096, 0.09022083, 45.15118]
        for index, value in enumerate(match.groups()):
            if index != 3:
                near(float(value), expected[index], 2e-5 if index == 5 else 1e-5, f'CloverLeaf field {index + 1}')
    elif workload == 'tealeaf':
        require('- Problem: 64x64@1' in output and '- Outcome: PASSED' in output, 'TeaLeaf reference case did not pass')
        require('expected value will be invalid' not in output, 'TeaLeaf reference is invalid')
        expected = re.search(r'^\s*Expected\s+(' + NUMBER + r')\s*$', report, re.M)
        actual = re.search(r'^\s*Actual\s+(' + NUMBER + r')\s*$', report, re.M)
        require(expected is not None and actual is not None, 'TeaLeaf expected/actual values are absent')
        near(float(actual[1]), float(expected[1]), 1e-7, 'TeaLeaf reference')

def compare_catalog(catalog):
    """Run paired-output checks after independent CTest executions finish."""
    catalog = Path(catalog)
    results = ET.parse(catalog.parent / 'results.xml').getroot()
    verdicts = {}
    for case in results.iter('testcase'):
        verdicts[case.get('name')] = ('fail' if case.find('failure') is not None or case.find('error') is not None
                                    else 'skip' if case.find('skipped') is not None else 'pass')
    suite = ET.Element('testsuite', name='paired-output comparisons')
    failures = skipped = 0
    for line in catalog.read_text().splitlines():
        spec = json.loads(line)
        if spec.get('validator') != 'tealeaf':
            continue
        case = ET.SubElement(suite, 'testcase', name='comparison/' + spec['name'])
        try:
            oracle_name = 'oracle/' + spec.get('oracle_name', spec['name'])
            gemstone_name = 'gemstone/' + spec['name']
            require(oracle_name in verdicts and gemstone_name in verdicts, 'paired execution verdict is absent')
            if verdicts[oracle_name] != 'pass' or verdicts[gemstone_name] != 'pass':
                ET.SubElement(case, 'skipped', message='comparison requires two passing executions')
                skipped += 1
                continue
            def actual(role):
                report = Path(spec[role]['report_directory']) / 'tea.out'
                value = re.search(r'^\s*Actual\s+(' + NUMBER + r')\s*$', report.read_text(), re.M)
                require(value is not None, role + ' TeaLeaf value is absent')
                return float(value[1])
            near(actual('gemstone'), actual('oracle'), 1e-7, 'TeaLeaf oracle comparison')
        except Exception as error:
            ET.SubElement(case, 'failure', message=str(error))
            failures += 1
    suite.set('tests', str(len(suite)))
    suite.set('failures', str(failures))
    suite.set('skipped', str(skipped))
    ET.ElementTree(suite).write(catalog.parent / 'comparisons.xml', encoding='utf-8', xml_declaration=True)
    # Separate role reports preserve the single CTest scheduler/resource pool.
    for role in ('oracle', 'gemstone'):
        selected = [case for case in results.iter('testcase') if case.get('name', '').startswith(role + '/')]
        tree = ET.Element('testsuite', name=role, tests=str(len(selected)))
        tree.extend(selected)
        ET.ElementTree(tree).write(catalog.parent / (role + '-results.xml'), encoding='utf-8', xml_declaration=True)
    return 1 if failures else 0

def validate_cts(spec, role, stdout):
    metadata = cts_metadata(spec)
    expected = metadata['test_name']
    require(bool(expected), 'CTS test name is empty')
    if spec['validator'] == 'opencl-cts':
        path = Path(spec[role]['report_directory']) / 'opencl-cts.json'
        results = json.loads(path.read_text())['results']
        require(set(results) == {expected}, 'OpenCL CTS did not report exactly the selected case')
        status = results[expected]
        require(status in ('pass', 'fail', 'skip'), 'unknown OpenCL CTS verdict')
        require(status != 'fail', 'OpenCL CTS reported failure')
        return 77 if status == 'skip' else 0
    require(spec['validator'] == 'catch2-junit', 'unknown CTS validator')
    tree = ET.fromstring(stdout)
    filters = [p.get('value') for p in tree.iter('property') if p.get('name') == 'filters']
    require(filters == [json.dumps(expected, ensure_ascii=False)], 'CTS selection does not match requested case')
    cases = list(tree.iter('testcase'))
    require(bool(cases), 'CTS reported no test cases')
    for case in cases:
        name = case.get('name', '')
        require(name == expected or name.startswith(expected + '/') or name.startswith(expected + ' / '),
                'CTS reported a different upstream case: ' + name)
        require(case.find('failure') is None and case.find('error') is None, 'CTS reported failed assertions')
    suites = list(tree.iter('testsuite'))
    require(bool(suites), 'CTS JUnit test suite is absent')
    require(all(int(suite.get('failures', '0')) == 0 and int(suite.get('errors', '0')) == 0 for suite in suites),
            'CTS reported a failing suite')
    skipped = [case.find('skipped') is not None for case in cases]
    if all(skipped):
        return 77
    require(any(int(suite.get('tests', '0')) > 0 for suite in suites), 'CTS reported no assertions')
    return 0

def execute(spec, role, report, identity):
    plan = spec[role]
    if plan.get('skip'):
        print('[SKIP] ' + plan['skip'], flush=True)
        return 77, None
    if spec.get('build_failure'):
        raise RuntimeError(spec['build_failure'])
    spec['attestation'] = emulator_attestation(spec, role)
    environment = dict(os.environ)
    for name in list(environment):
        if name.startswith('GEMSTONE_') or name in plan.get('unset', []):
            environment.pop(name, None)
    report_directory = Path(plan.get('report_directory', plan['directory']))
    report_directory.mkdir(parents=True, exist_ok=True)
    environment.update({key: value.replace('{report_directory}', str(report_directory))
                        for key, value in plan.get('environment', {}).items()})
    if spec.get('cts') and spec['validator'] == 'opencl-cts':
        result_file = report_directory / 'opencl-cts.json'
        result_file.unlink(missing_ok=True)
        environment['CL_CONFORMANCE_RESULTS_FILENAME'] = str(result_file)
    credential = None
    child = None
    provenance = None
    stdout_path = report / (identity + '.stdout.log')
    stderr_path = report / (identity + '.stderr.log')
    time_path = report / (identity + '.time')
    try:
        if role == 'gemstone' and spec.get('api') != 'host':
            if spec.get('server_file'):
                spec['server'] = json.loads(Path(spec['server_file']).read_text())
            credential = control(spec['server'], 1)
            environment['GEMSTONE_RPC_TOKEN'] = credential
            environment['GEMSTONE_RPC_PORT'] = str(spec['server']['port'])
        if role == 'gemstone' and spec.get('runtime_route'):
            module_spec = importlib.util.spec_from_file_location('runtime_provenance', Path(__file__).with_name('runtime_provenance.py'))
            provenance = importlib.util.module_from_spec(module_spec)
            module_spec.loader.exec_module(provenance)
            environment.update(provenance.prepare(spec, role, report, identity))
        def stop(signum, _frame):
            spec['_interrupted_signal'] = signum
            if child is not None and child.poll() is None:
                try:
                    os.killpg(child.pid, signal.SIGTERM)
                except ProcessLookupError:
                    pass
            raise SystemExit(128 + signum)
        signal.signal(signal.SIGTERM, stop)
        signal.signal(signal.SIGINT, stop)
        directory = Path(plan['directory'])
        directory.mkdir(parents=True, exist_ok=True)
        with stdout_path.open('w') as stdout, stderr_path.open('w') as stderr:
            command = ['/usr/bin/time', '-f', '%M %U %S', '-o', str(time_path), '--'] + plan['command']
            child = subprocess.Popen(command, cwd=directory, env=environment, stdout=stdout, stderr=stderr, start_new_session=True)
            try:
                code = child.wait(timeout=spec['timeout'])
            except subprocess.TimeoutExpired:
                print('FAIL: workload timeout', file=sys.stderr)
                code = 124
        # Preserve the actual child result even when attestation/validation raises afterward.
        spec['_process_exit'] = code
        output = stdout_path.read_text(errors='replace')
        try:
            if credential is not None:
                cuda, execution = control(spec['server'], 2, credential)
                spec['authenticated_requests'] = dict(cuda=cuda, execution=execution)
            if provenance is not None:
                spec['runtime_provenance'] = provenance.collect(spec, role, report, identity)
                provenance.archive(spec['runtime_provenance'])
        except Exception as error:
            if code in (0, 77):
                raise
            spec.setdefault('_diagnostics', []).append('failure evidence collection: ' + str(error))
        if spec.get('cts'):
            status = validate_cts(spec, role, output)
            spec['_cts_status'] = status
            if status == 77:
                allowed = (0, 4) if spec['validator'] == 'catch2-junit' else (0,)
                require(code in allowed, 'CTS skip report accompanied by unexpected process failure')
                return 77, code
        if code == 77 and spec.get('allow_skip', False) and not spec.get('cts'):
            print(output[-4096:])
            return 77, None
        if code != 0:
            print(output[-16384:])
            print(stderr_path.read_text(errors='replace')[-16384:], file=sys.stderr)
            return 1, code
        if credential is not None:
            validate_attestation(spec, output, cuda, execution)
        if provenance is not None:
            provenance.validate(spec['runtime_provenance'])
        validate(spec, role, output)
        return 0, code
    finally:
        primary_error = sys.exc_info()[0] is not None
        cleanup_errors = []
        if child is not None and child.poll() is None:
            try:
                try:
                    os.killpg(child.pid, signal.SIGTERM)
                except ProcessLookupError:
                    pass
                try:
                    child.wait(timeout=2)
                except subprocess.TimeoutExpired:
                    try:
                        os.killpg(child.pid, signal.SIGKILL)
                    except ProcessLookupError:
                        pass
                    child.wait()
            except Exception as error:
                cleanup_errors.append('child cleanup: ' + str(error))
        if credential is not None:
            try:
                control(spec['server'], 3, credential)
            except Exception as error:
                cleanup_errors.append('client cleanup: ' + str(error))
        if cleanup_errors:
            spec.setdefault('_diagnostics', []).extend(cleanup_errors)
            if not primary_error and spec.get('_process_exit') in (None, 0, 77):
                raise RuntimeError('; '.join(cleanup_errors))

def main():
    if len(sys.argv) == 3 and sys.argv[1] == '--compare':
        return compare_catalog(sys.argv[2])
    catalog, offset, length, role = sys.argv[1:]
    with open(catalog, 'rb') as stream:
        stream.seek(int(offset))
        spec = json.loads(stream.read(int(length)))
    spec.pop('_process_exit', None)
    report = Path(catalog).parent / 'cases'
    report.mkdir(exist_ok=True)
    identity = role + '-' + offset
    started = time.monotonic()
    started_epoch = time.time()
    code = 1
    native_exit = None
    error_message = None
    try:
        code, native_exit = execute(spec, role, report, identity)
    except Exception as error:
        error_message = str(error)
        print('FAIL: ' + error_message, file=sys.stderr)
    finally:
        if '_interrupted_signal' in spec:
            code = 128 + spec['_interrupted_signal']
            error_message = 'case interrupted by signal ' + str(spec['_interrupted_signal'])
        peak = None
        timing = report / (identity + '.time')
        if timing.exists():
            lines = timing.read_text().splitlines()
            values = lines[-1].split() if lines else []
            if len(values) == 3 and values[0].isdigit():
                peak = int(values[0]) * 1024
        (report / (identity + '.json')).write_text(json.dumps(dict(
            kind='case', name=role + '/' + (spec.get('oracle_name', spec['name']) if role == 'oracle' else spec['name']), exit=code, process_exit=spec.get('_process_exit', native_exit),
            attestation=spec.get('attestation'), runtime_provenance=spec.get('runtime_provenance'), error=error_message, diagnostics=spec.get('_diagnostics', []),
            server_pid=spec.get('server', {}).get('pid'), authenticated_requests=spec.get('authenticated_requests'),
            elapsed_seconds=time.monotonic() - started, started_epoch=started_epoch, finished_epoch=time.time(), process_peak_bytes=peak,
            stdout=str(report / (identity + '.stdout.log')), stderr=str(report / (identity + '.stderr.log')))))
    return code

if __name__ == '__main__':
    raise SystemExit(main())

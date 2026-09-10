#!/usr/bin/env python3
import importlib.util
import json
from unittest import mock
from pathlib import Path
import tempfile
import unittest

spec = importlib.util.spec_from_file_location('case_run', Path(__file__).with_name('case-run.py'))
runner = importlib.util.module_from_spec(spec)
spec.loader.exec_module(runner)

class CaseValidation(unittest.TestCase):
    def test_babelstream_and_bude_fail_closed(self):
        with tempfile.TemporaryDirectory() as directory:
            case = dict(validator='babelstream', gemstone=dict(report_directory=directory))
            with self.assertRaises(RuntimeError): runner.validate(case, 'gemstone', 'FAILED validation')
            case['validator'] = 'minibude'
            with self.assertRaises(RuntimeError): runner.validate(case, 'gemstone', 'completed')
            runner.validate(case, 'gemstone', 'valid: true')
    def test_tea_execution_is_independent_but_final_comparison_is_required(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory); (root/'oracle').mkdir()
            case = dict(name='miniapps/tea/test', oracle_name='shared/tea', validator='tealeaf',
                        gemstone=dict(report_directory=directory), oracle=dict(report_directory=str(root/'oracle')))
            (root/'tea.out').write_text('Expected 3.0\nActual 3.0\n')
            # The oracle may still be running while emulation validates its own result.
            (root/'oracle/tea.out').write_text('partial output')
            runner.validate(case, 'gemstone', '- Problem: 64x64@1\n- Outcome: PASSED')
            catalog=root/'cases.jsonl'; catalog.write_text(json.dumps(case)+'\n')
            (root/'results.xml').write_text('<testsuite><testcase name="oracle/shared/tea"/>'
                                           '<testcase name="gemstone/miniapps/tea/test"/></testsuite>')
            self.assertEqual(runner.compare_catalog(catalog),1)
            (root/'oracle/tea.out').write_text('Actual 3.0\n')
            self.assertEqual(runner.compare_catalog(catalog),0)
            (root/'oracle/tea.out').write_text('Actual 4.0\n')
            self.assertEqual(runner.compare_catalog(catalog),1)
            (root/'results.xml').write_text('<testsuite><testcase name="oracle/shared/tea"><skipped/></testcase>'
                                           '<testcase name="gemstone/miniapps/tea/test"/></testsuite>')
            self.assertEqual(runner.compare_catalog(catalog),0)
            self.assertEqual(runner.ET.parse(root/'comparisons.xml').getroot().get('skipped'),'1')
            (root/'results.xml').write_text('<testsuite><testcase name="oracle/shared/tea"/></testsuite>')
            self.assertEqual(runner.compare_catalog(catalog),1)

    def test_timeout_with_empty_time_file_still_records_verdict(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            catalog = root / 'cases.jsonl'
            payload = json.dumps(dict(name='timeout-case')).encode()
            catalog.write_bytes(payload)
            def timed_out(case, role, report, identity):
                (report / (identity + '.time')).write_text('')
                return 1, 124
            with mock.patch.object(runner, 'execute', side_effect=timed_out), mock.patch.object(
                runner.sys, 'argv', ['case-run.py', str(catalog), '0', str(len(payload)), 'gemstone']):
                self.assertEqual(runner.main(), 1)
            result = json.loads((root / 'cases/gemstone-0.json').read_text())
            self.assertEqual(result['exit'], 1)
            self.assertEqual(result['process_exit'], 124)
            self.assertIsNone(result['process_peak_bytes'])

    def test_unavailable_oracle_does_not_spawn_a_process_or_connect(self):
        with tempfile.TemporaryDirectory() as directory:
            code, _ = runner.execute(dict(oracle=dict(skip='generic CI profile', command=['must-not-execute'])),
                                     'oracle', Path(directory), 'test')
            self.assertEqual(code, 77)
            self.assertEqual(list(Path(directory).iterdir()), [])

class AttestationPolicy(unittest.TestCase):
    def case(self, name, api='hip', output=runner.NATIVE_SINGLE_PASS, exit_code=0):
        import sys
        selected = name.split('/', 2)[2] if name.startswith('unified/') else name
        return dict(name=name, api=api, timeout=10, server=dict(port=1, token='test'),
                    allow_skip=True, gemstone=dict(directory='.',
                    command=[sys.executable, '-c', f'print({output!r});raise SystemExit({exit_code})', '--case='+selected]))

    def test_reviewed_operations_exist_in_the_source_catalog(self):
        sources={'algorithms':'algorithms.hpp','edge-cases/algorithms':'edge_cases.hpp','async':'async_cases.hpp'}
        include=Path(__file__).resolve().parents[1]/'unified/include/unified_suite'
        for provider,families in runner.EXECUTION_METADATA['empty_input_operations'].items():
            self.assertIn(provider,('cuda','hip'))
            for family,operations in families.items():
                header=(include/sources[family]).read_text().split('enum class Operation',1)[0]
                definitions=set(runner.re.findall(r'X\(\w+,\s*"([a-z0-9-]+)"\)',header))
                self.assertTrue(operations and set(operations)<=definitions,(provider,family))
                self.assertEqual(len(operations),len(set(operations)))

    def test_explicit_empty_and_host_metadata(self):
        for api in ('cuda','hip'):
            case=self.case(f'unified/{api}/algorithms/copy/i32/n0',api)
            self.assertEqual(runner.emulator_attestation(case,'gemstone'),dict(required=False,reason='empty-input'))
        for name in runner.EXECUTION_METADATA['host_only_cases']['hip']:
            case=self.case('unified/hip/'+name)
            self.assertEqual(runner.emulator_attestation(case,'gemstone'),dict(required=False,reason='host-only-semantics'))

    def test_unknown_nonzero_canary_and_runtime_cases_remain_required(self):
        names=['unified/hip/algorithms/copy/i32/n1', 'unified/hip/algorithms/new-operation/i32/n0',
               'unified/hip/edge-cases/runtime/dispatch/i32/n0',
               'unified/hip/edge-cases/algorithms/inplace-scan-canary/i32/n0',
               'unified/hip/algorithms/roc-thrust-semantic/tuple-sort/i32/n11',
               'unified/hip/async/hip/copy/i32/n0', 'miniapps/babelstream/hip']
        for name in names:
            case=self.case(name);case['attestation']=dict(required=False,reason='forged')
            self.assertTrue(runner.emulator_attestation(case,'gemstone')['required'],name)
        case=self.case('unified/hip/algorithms/copy/i32/n0')
        case['gemstone']['command'][-1]='--case=algorithms/copy/i32/n1'
        self.assertTrue(runner.emulator_attestation(case,'gemstone')['required'])
        case=self.case('unified/hip/algorithms/copy/i32/n0',api='cuda')
        self.assertTrue(runner.emulator_attestation(case,'gemstone')['required'])
        case=self.case('unified/hip/algorithms/copy/i32/n0')
        case['gemstone']['command'].append(case['gemstone']['command'][-1])
        self.assertTrue(runner.emulator_attestation(case,'gemstone')['required'])

    def test_native_pass_without_auth_is_allowed_only_for_reviewed_no_device_work(self):
        for name in ['unified/hip/algorithms/copy/i32/n0',
                     'unified/hip/algorithms/roc-thrust-semantic/tuple-transform/i32/n11']:
            case=self.case(name)
            with tempfile.TemporaryDirectory() as directory, mock.patch.object(runner,'control',side_effect=['client',(0,0),1]) as control:
                self.assertEqual(runner.execute(case,'gemstone',Path(directory),'case'),(0,0))
                self.assertEqual(case['_process_exit'],0)
                self.assertEqual(case['authenticated_requests'],dict(cuda=0,execution=0))
                self.assertEqual(control.call_args_list[-1].args[1],3)
        case=self.case('unified/hip/algorithms/copy/i32/n31')
        with tempfile.TemporaryDirectory() as directory, mock.patch.object(runner,'control',side_effect=['client',(0,0),1]):
            with self.assertRaisesRegex(RuntimeError,'without an authenticated request'):
                runner.execute(case,'gemstone',Path(directory),'case')
            self.assertEqual(case['_process_exit'],0)

    def test_empty_exception_does_not_hide_missing_pass_or_native_failure(self):
        for output in ['', runner.NATIVE_SINGLE_PASS+'\nSUMMARY pass=0 skip=0 fail=1',
                       'SUMMARY pass=0 skip=1 fail=0 xfail=0 xpass=0 xskip=0 selected=1']:
            case=self.case('unified/hip/algorithms/copy/i32/n0',output=output)
            with tempfile.TemporaryDirectory() as directory, mock.patch.object(runner,'control',side_effect=['client',(0,0),1]):
                with self.assertRaisesRegex(RuntimeError,'exactly one native PASS'):
                    runner.execute(case,'gemstone',Path(directory),'case')
        case=self.case('unified/hip/algorithms/copy/i32/n0',output='FAIL numerical mismatch',exit_code=7)
        with tempfile.TemporaryDirectory() as directory, mock.patch.object(runner,'control',side_effect=['client',(0,0),1]):
            self.assertEqual(runner.execute(case,'gemstone',Path(directory),'case'),(1,7))
            self.assertEqual(case['_process_exit'],7)

    def test_attested_device_work_passes_and_wrong_api_counter_does_not(self):
        for api,counts in [('cuda',(3,0)),('hip',(0,4))]:
            case=self.case(f'unified/{api}/algorithms/copy/i32/n31',api)
            with tempfile.TemporaryDirectory() as directory, mock.patch.object(runner,'control',side_effect=['client',counts,1]):
                self.assertEqual(runner.execute(case,'gemstone',Path(directory),'case'),(0,0))
        case=self.case('unified/cuda/algorithms/copy/i32/n31','cuda')
        with tempfile.TemporaryDirectory() as directory, mock.patch.object(runner,'control',side_effect=['client',(0,9),1]):
            with self.assertRaises(RuntimeError):runner.execute(case,'gemstone',Path(directory),'case')

    def test_record_retains_native_zero_when_attestation_fails(self):
        with tempfile.TemporaryDirectory() as directory:
            root=Path(directory);catalog=root/'cases.jsonl'
            payload=json.dumps(self.case('unified/hip/algorithms/copy/i32/n31')).encode();catalog.write_bytes(payload)
            with mock.patch.object(runner,'control',side_effect=['client',(0,0),1]), mock.patch.object(
                runner.sys,'argv',['case-run.py',str(catalog),'0',str(len(payload)),'gemstone']):
                self.assertEqual(runner.main(),1)
            result=json.loads((root/'cases/gemstone-0.json').read_text())
            self.assertEqual(result['process_exit'],0)
            self.assertTrue(result['attestation']['required'])
            self.assertIn('without an authenticated request',result['error'])
            self.assertEqual(result['authenticated_requests'],dict(cuda=0,execution=0))

    def test_failed_child_survives_failed_statistics_and_cleanup(self):
        case=self.case('unified/hip/algorithms/copy/i32/n31', output='FAIL original workload', exit_code=7)
        with tempfile.TemporaryDirectory() as directory, mock.patch.object(runner,'control',
                side_effect=['client',RuntimeError('statistics unavailable'),RuntimeError('close unavailable')]):
            self.assertEqual(runner.execute(case,'gemstone',Path(directory),'case'),(1,7))
            self.assertEqual(case['_process_exit'],7)
            self.assertEqual(case['_diagnostics'],['failure evidence collection: statistics unavailable',
                                                   'client cleanup: close unavailable'])

    def test_cleanup_does_not_replace_primary_validation_error(self):
        case=self.case('unified/hip/algorithms/copy/i32/n31')
        with tempfile.TemporaryDirectory() as directory, mock.patch.object(runner,'control',
                side_effect=['client',(0,0),RuntimeError('close unavailable')]):
            with self.assertRaisesRegex(RuntimeError,'without an authenticated request'):
                runner.execute(case,'gemstone',Path(directory),'case')
            self.assertEqual(case['_process_exit'],0)
            self.assertEqual(case['_diagnostics'],['client cleanup: close unavailable'])

    def test_successful_work_with_failed_cleanup_is_infrastructure_failure(self):
        case=self.case('unified/hip/algorithms/copy/i32/n31')
        with tempfile.TemporaryDirectory() as directory, mock.patch.object(runner,'control',
                side_effect=['client',(0,4),RuntimeError('close unavailable')]):
            with self.assertRaisesRegex(RuntimeError,'client cleanup: close unavailable'):
                runner.execute(case,'gemstone',Path(directory),'case')
            self.assertEqual(case['_process_exit'],0)

    def test_failed_workload_record_keeps_child_exit_and_secondary_diagnostics(self):
        with tempfile.TemporaryDirectory() as directory:
            root=Path(directory);catalog=root/'cases.jsonl'
            payload=json.dumps(self.case('unified/hip/algorithms/copy/i32/n31',
                                        output='FAIL original workload',exit_code=23)).encode()
            catalog.write_bytes(payload)
            with mock.patch.object(runner,'control',side_effect=['client',RuntimeError('stats'),RuntimeError('close')]), \
                 mock.patch.object(runner.sys,'argv',['case-run.py',str(catalog),'0',str(len(payload)),'gemstone']):
                self.assertEqual(runner.main(),1)
            record=json.loads((root/'cases/gemstone-0.json').read_text())
            self.assertEqual(record['process_exit'],23)
            self.assertIsNone(record['error'])
            self.assertEqual(len(record['diagnostics']),2)
            self.assertIn('FAIL original workload',Path(record['stdout']).read_text())


class CTSValidation(unittest.TestCase):
    def case(self, suite, directory='.'):
        return dict(name='cts/' + ('opencl' if suite == 'opencl' else 'sycl-levelzero') + '/category/selected',
                    api='opencl' if suite == 'opencl' else 'sycl',
                    validator='opencl-cts' if suite == 'opencl' else 'catch2-junit',
                    cts=dict(suite=suite, execution='device', revision='a'*40, test_name='selected'),
                    gemstone=dict(report_directory=directory))

    def test_opencl_requires_exact_upstream_result(self):
        with tempfile.TemporaryDirectory() as directory:
            case = self.case('opencl', directory)
            path = Path(directory) / 'opencl-cts.json'
            for value, status in [('pass', 0), ('skip', 77)]:
                path.write_text(json.dumps(dict(results={'selected': value})))
                self.assertEqual(runner.validate_cts(case, 'gemstone', ''), status)
            for results in ({}, {'selected': 'fail'}, {'other': 'pass'}, {'selected': 'pass', 'other': 'pass'}):
                path.write_text(json.dumps(dict(results=results)))
                with self.assertRaises(RuntimeError): runner.validate_cts(case, 'gemstone', '')

    def test_catch_sections_and_explicit_skips(self):
        case = self.case('sycl')
        xml = '<testsuites><testsuite tests="2" failures="0" errors="0"><properties><property name="filters" value="&quot;selected&quot;"/></properties><testcase name="selected/first"/><testcase name="selected/second"/></testsuite></testsuites>'
        self.assertEqual(runner.validate_cts(case, 'gemstone', xml), 0)
        for bad in (xml.replace('selected/first', 'other'), xml.replace('failures="0"', 'failures="1"'),
                    xml.replace('tests="2"', 'tests="0"'), xml.replace('&quot;selected&quot;', '*')):
            with self.assertRaises(RuntimeError): runner.validate_cts(case, 'gemstone', bad)
        skip = xml.replace('<testcase name="selected/first"/>', '<testcase name="selected/first"><skipped/></testcase>').replace('<testcase name="selected/second"/>', '<testcase name="selected/second"><skipped/></testcase>')
        self.assertEqual(runner.validate_cts(case, 'gemstone', skip), 77)

    def test_cts_host_exemption_needs_validated_pass(self):
        case = self.case('sycl')
        self.assertTrue(runner.emulator_attestation(case, 'gemstone')['required'])
        case['cts']['execution'] = 'host'
        case['attestation'] = runner.emulator_attestation(case, 'gemstone')
        self.assertFalse(case['attestation']['required'])
        with self.assertRaises(RuntimeError): runner.validate_attestation(case, '', 0, 0)
        case['_cts_status'] = 0
        runner.validate_attestation(case, '', 0, 0)
        case['api'] = 'cuda'
        with self.assertRaises(RuntimeError): runner.emulator_attestation(case, 'gemstone')

if __name__ == '__main__': unittest.main()

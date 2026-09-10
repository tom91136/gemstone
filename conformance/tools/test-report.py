#!/usr/bin/env python3
import json
from pathlib import Path
import subprocess
import tempfile
import unittest

class ReportingContract(unittest.TestCase):
    def test_only_integration_verdicts_and_real_fixture_intervals_are_counted(self):
        with tempfile.TemporaryDirectory(prefix='integration-report-') as directory:
            root = Path(directory)
            for project, tests in [
                ('gemstone-core', '<testcase name="unit" classname="Unit" time="99"/>'),
                ('gemstone-conformance-integration',
                 '<testcase name="miniapps/babelstream/cuda/cuda" time="8"/>'
                 '<testcase name="miniapps/babelstream/hip/gfx1036/hip" time="4"><failure message="bad"/></testcase>')]:
                output = root / 'junit' / project / 'test-reports'
                output.mkdir(parents=True)
                (output / 'tests.xml').write_text('<testsuite>' + tests + '</testsuite>')
            (root / 'measurements').mkdir()
            for index in range(2):
                (root / 'measurements' / f'{index}.json').write_text(json.dumps(dict(kind='fixture',
                    command=['bash', '/repo/conformance/miniapps/tools/container-build.sh', 'cuda', '/src', '/out', 'babelstream:cuda'],
                    started_epoch=index * 10, finished_epoch=index * 10 + 5, elapsed_seconds=5, exit=0)))
            (root / 'measurements' / 'partial.json').write_text(json.dumps(dict(
                kind='case', name='unified/hip/runtime/probe', elapsed_seconds=3,
                returned_normally=True, scope_peak_bytes=1024)))
            (root / 'ctest').mkdir()
            (root / 'ctest' / 'results.xml').write_text('<testsuite>'
                '<testcase name="oracle/unified/cuda/algorithms/copy/i32/n1" time="0.01"><skipped/></testcase>'
                '<testcase name="gemstone/unified/cuda/algorithms/copy/i32/n1" time="0.2"/>'
                '</testsuite>')
            subprocess.run(['python3', str(Path(__file__).with_name('report-run.py')), str(root)], check=True,
                           stdout=subprocess.DEVNULL)
            report = json.loads((root / 'outcomes.json').read_text())
            rows = {row['component']: row for row in report['components']}
            row = rows['Miniapp / babelstream']
            self.assertEqual((row['status'], row['passed'], row['failed']), ('FAIL', 1, 1))
            self.assertEqual(row['seconds'], 12)
            self.assertEqual(row['build_seconds'], 10)
            self.assertEqual(report['maximum_concurrent_fixture_builds'], 1)
            self.assertFalse(any('Unit' in name for name in rows))
            self.assertEqual(rows['Unified / HIP']['status'], 'INCOMPLETE')
            self.assertEqual(rows['Unified / HIP']['measured_without_verdict'], 1)
            self.assertEqual(rows['Unified / HIP']['passed'], 0)
            self.assertEqual(rows['Unified / HIP']['seconds'], 3)
            self.assertEqual(rows['Unified / CUDA']['oracle_skipped'], 1)
            self.assertEqual(rows['Unified / CUDA']['gemstone_passed'], 1)

if __name__ == '__main__':
    unittest.main()

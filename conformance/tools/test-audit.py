#!/usr/bin/env python3
"""Exercise the actual audit CLI against isolated case reports and a CTest inventory."""
import json
import os
from pathlib import Path
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]

class AuditContract(unittest.TestCase):
    def run_audit(self, *, name='unified/cuda/algorithms/copy/i32/n0', required=False,
                  output='SUMMARY pass=1 skip=0 fail=0 xfail=0 xpass=0 xskip=0 selected=1', lock=None):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory); matrix=root/'ctest'; (matrix/'cases').mkdir(parents=True)
            stdout=root/'stdout'; stdout.write_text(output+'\n')
            case=dict(name='gemstone/'+name, exit=0, process_exit=0, server_pid=123,
                      started_epoch=1, finished_epoch=2, authenticated_requests=dict(cuda=0,execution=0),
                      attestation=dict(required=required,reason='device-request-required' if required else 'empty-input'),
                      stdout=str(stdout))
            (matrix/'cases/case.json').write_text(json.dumps(case))
            spec=dict(name=name,api='cuda',gemstone=dict(command=['runner','--case='+name.split('/',2)[2]]))
            (matrix/'cases.jsonl').write_text(json.dumps(spec)+'\n')
            inventory={'tests':[{'name':case['name'],'properties':[] if lock is None else [{'name':'RESOURCE_LOCK','value':[lock]}]}]}
            executable=root/'ctest-tool';executable.mkdir(); tool=executable/'ctest'
            tool.write_text('#!/usr/bin/env python3\nprint('+repr(json.dumps(inventory))+')\n');tool.chmod(0o755)
            return subprocess.run(['python3',str(ROOT/'conformance/tools/audit-run.py'),str(root)],
                                  env=dict(os.environ,PATH=str(executable)+os.pathsep+os.environ['PATH']),
                                  capture_output=True,text=True)

    def test_canonical_empty_work_passes(self):
        result=self.run_audit();self.assertEqual(result.returncode,0,result.stderr)
    def test_exemption_cannot_be_forged_for_nonempty_work(self):
        self.assertNotEqual(self.run_audit(name='unified/cuda/algorithms/copy/i32/n31').returncode,0)
    def test_empty_work_requires_exact_single_pass(self):
        self.assertNotEqual(self.run_audit(output='SUMMARY pass=0 skip=0 fail=0 selected=0').returncode,0)
    def test_required_work_and_gpu_locks_still_fail(self):
        self.assertNotEqual(self.run_audit(name='unified/cuda/algorithms/copy/i32/n31',required=True).returncode,0)
        self.assertNotEqual(self.run_audit(lock='gpu-0').returncode,0)

if __name__=='__main__':unittest.main()

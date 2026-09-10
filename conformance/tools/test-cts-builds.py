#!/usr/bin/env python3
"""Retry real CTS entry points with a failing container/source fetch, without SDK work."""
import json
import os
from pathlib import Path
import shutil
import select
import subprocess
import tempfile
import unittest

ROOT=Path(__file__).resolve().parents[2]
REVISION='1'*40

class CTSBuildContract(unittest.TestCase):
    def fixture(self, directory, suite, partial_source=False):
        root=Path(directory);tools=root/'conformance/tools';tools.mkdir(parents=True)
        for name in ['container-common.sh','fixture-run.py']:
            shutil.copy2(ROOT/'conformance/tools'/name,tools/name)
        recipe=root/'conformance/cts'/suite;recipe.mkdir(parents=True)
        shutil.copy2(ROOT/'conformance/cts'/suite/'container-build.sh',recipe/'container-build.sh')
        (recipe/'prepare.py').write_text('')
        (recipe/'pin.json').write_text(json.dumps(dict(revision=REVISION,submodulepins={})))
        source=root/'source';(source/'.git').mkdir(parents=True)
        if not partial_source:(source/'HEAD').write_text(REVISION)
        binaries=root/'tools';binaries.mkdir()
        engine=binaries/'docker'
        engine.write_text('''#!/usr/bin/env python3
import json,os,sys
from pathlib import Path
args=sys.argv[1:]
with open(os.environ['CONTAINER_CALLS'],'a') as log:log.write(json.dumps(args)+'\\n')
if args[0]=='build' and '--iidfile' in args:
 Path(args[args.index('--iidfile')+1]).write_text('sha256:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa')
if args[0]=='image' and '--format' in args:print('sha256:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa')
if args[0]=='run':
 if os.environ.get('BUILD_READY_FIFO'):
  with open(os.environ['BUILD_READY_FIFO'],'w') as ready:ready.write('1')
  with open(os.environ['BUILD_CONTINUE_FIFO']) as gate:gate.read(1)
 mount=next(x for x in args if x.startswith('type=bind,src=') and x.endswith(',dst=/out,rw'))
 out=Path(mount[len('type=bind,src='):-len(',dst=/out,rw')]);(out/'bin').mkdir(parents=True)
 marker=Path(os.environ['CONTAINER_FAILURE'])
 if marker.exists():
  marker.unlink();(out/'bin/partial').write_text('interrupted');sys.exit(9)
 (out/'manifest.json').write_text(json.dumps({'revision':'1'*40}))
 (out/'cases.jsonl').write_text('{}\\n')
''');engine.chmod(0o755)
        git=binaries/'git'
        git.write_text('''#!/usr/bin/env python3
import os,sys
from pathlib import Path
args=sys.argv[1:];root=Path('.')
if args[:1]==['-C']:root=Path(args[1]);args=args[2:]
if args[0]=='init':(Path(args[1])/'.git').mkdir(parents=True,exist_ok=True)
elif args[0]=='rev-parse':
 if not (root/'HEAD').exists():sys.exit(128)
 print((root/'HEAD').read_text())
elif args[0]=='fetch':
 marker=Path(os.environ['FETCH_FAILURE'])
 if marker.exists():marker.unlink();sys.exit(7)
elif args[0]=='checkout':(root/'HEAD').write_text(args[-1])
''');git.chmod(0o755)
        env={k:v for k,v in os.environ.items() if not k.startswith('GEMSTONE_')}
        env.update(PATH=str(binaries)+os.pathsep+env['PATH'],GEMSTONE_CONTAINER_ENGINE='docker',
                   GEMSTONE_SYCL_CTS_SOURCE=str(source),CMAKE_BUILD_PARALLEL_LEVEL='2',
                   CONTAINER_FAILURE=str(root/'container-failure'),FETCH_FAILURE=str(root/'fetch-failure'),
                   CONTAINER_CALLS=str(root/'calls.jsonl'))
        return root,recipe,env

    def invoke(self,root,recipe,env):
        return subprocess.run(['bash',str(recipe/'container-build.sh'),str(root/'artifact with spaces')],
                              env=env,capture_output=True,text=True,timeout=20)

    def test_failed_container_build_retries_and_publishes_complete_artifact(self):
        for suite in ['opencl','sycl']:
            with self.subTest(suite=suite),tempfile.TemporaryDirectory() as directory:
                root,recipe,env=self.fixture(directory,suite)
                Path(env['CONTAINER_FAILURE']).touch()
                self.assertNotEqual(self.invoke(root,recipe,env).returncode,0)
                result=self.invoke(root,recipe,env)
                self.assertEqual(result.returncode,0,result.stderr)
                self.assertTrue((root/'artifact with spaces/manifest.json').is_file())
                self.assertTrue((root/'artifact with spaces/cases.jsonl').is_file())
                self.assertFalse((root/'artifact with spaces/bin/partial').exists())

    def test_legacy_partial_artifact_is_preserved_and_replaced(self):
        with tempfile.TemporaryDirectory() as directory:
            root,recipe,env=self.fixture(directory,'opencl')
            partial=root/'artifact with spaces/bin';partial.mkdir(parents=True)
            (partial/'partial').write_text('original evidence')
            result=self.invoke(root,recipe,env)
            self.assertEqual(result.returncode,0,result.stderr)
            backups=list(root.glob('artifact with spaces.incomplete.*'))
            self.assertEqual(len(backups),1)
            self.assertEqual((backups[0]/'bin/partial').read_text(),'original evidence')
            self.assertFalse((root/'artifact with spaces/bin/partial').exists())
            self.assertEqual(list(root.glob('.artifact with spaces.build.*')),[])

    def test_unknown_destination_contents_are_not_moved(self):
        with tempfile.TemporaryDirectory() as directory:
            root,recipe,env=self.fixture(directory,'opencl')
            destination=root/'artifact with spaces';destination.mkdir()
            (destination/'user-document').write_text('keep')
            self.assertNotEqual(self.invoke(root,recipe,env).returncode,0)
            self.assertEqual((destination/'user-document').read_text(),'keep')
            self.assertEqual(list(root.glob('artifact with spaces.incomplete.*')),[])

    def test_derived_builder_uses_an_image_keyed_base_tag(self):
        with tempfile.TemporaryDirectory() as directory:
            root,recipe,env=self.fixture(directory,'opencl')
            result=self.invoke(root,recipe,env)
            self.assertEqual(result.returncode,0,result.stderr)
            calls=[json.loads(line) for line in Path(env['CONTAINER_CALLS']).read_text().splitlines()]
            tags=[call for call in calls if call[0]=='tag']
            self.assertEqual(len(tags),1)
            self.assertEqual(tags[0][1],'sha256:'+('a'*64))
            self.assertEqual(tags[0][2],'localhost/gemstone-cts-base:'+('a'*64))
            builds=[call for call in calls if call[0]=='build']
            self.assertIn('BASE_IMAGE='+tags[0][2],builds[-1])

    def test_shared_cache_keeps_first_completed_publication(self):
        with tempfile.TemporaryDirectory() as directory:
            shared=Path(directory);one,recipe_one,env_one=self.fixture(shared/'one','opencl')
            two,recipe_two,env_two=self.fixture(shared/'two','opencl')
            destination=shared/'artifact'
            ready=shared/'ready';gate=shared/'continue';os.mkfifo(ready);os.mkfifo(gate)
            ready_fd=os.open(ready,os.O_RDWR|os.O_NONBLOCK);gate_fd=os.open(gate,os.O_RDWR|os.O_NONBLOCK)
            env_two.update(BUILD_READY_FIFO=str(ready),BUILD_CONTINUE_FIFO=str(gate))
            child=subprocess.Popen(['bash',str(recipe_two/'container-build.sh'),str(destination)],
                                   env=env_two,stdout=subprocess.PIPE,stderr=subprocess.PIPE,text=True)
            try:
                self.assertTrue(select.select([ready_fd],[],[],10)[0],'second builder did not reach its container')
                self.assertEqual(os.read(ready_fd,1),b'1')
                first=subprocess.run(['bash',str(recipe_one/'container-build.sh'),str(destination)],
                                     env=env_one,capture_output=True,text=True,timeout=10)
                self.assertEqual(first.returncode,0,first.stderr)
                inode=destination.stat().st_ino
                os.write(gate_fd,b'1');stdout,stderr=child.communicate(timeout=10)
                self.assertEqual(child.returncode,0,stderr)
                self.assertEqual(destination.stat().st_ino,inode,'completed artifact was replaced')
                self.assertEqual(list(shared.glob('artifact.incomplete.*')),[])
            finally:
                os.write(gate_fd,b'1')
                if child.poll() is None:
                    child.terminate();child.communicate(timeout=10)
                os.close(ready_fd);os.close(gate_fd)

    def test_publish_rejects_destination_as_staging(self):
        with tempfile.TemporaryDirectory() as directory:
            destination=Path(directory)/'artifact';destination.mkdir()
            for name in ['cases.jsonl','manifest.json']:(destination/name).write_text('{}')
            result=subprocess.run(['python3',str(ROOT/'conformance/tools/fixture-run.py'),'--publish',
                                   str(destination),str(destination)],capture_output=True,text=True)
            self.assertNotEqual(result.returncode,0)
            self.assertTrue((destination/'manifest.json').is_file())

    def test_unborn_checkout_recovers_after_failed_fetch(self):
        with tempfile.TemporaryDirectory() as directory:
            root,recipe,env=self.fixture(directory,'sycl',partial_source=True)
            Path(env['FETCH_FAILURE']).touch()
            self.assertNotEqual(self.invoke(root,recipe,env).returncode,0)
            result=self.invoke(root,recipe,env)
            self.assertEqual(result.returncode,0,result.stderr)
            self.assertEqual((root/'source/HEAD').read_text(),REVISION)

if __name__=='__main__':unittest.main()

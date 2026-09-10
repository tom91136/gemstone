#!/usr/bin/env python3
"""Discover exact upstream Catch2 names; do not use Gemstone's registered surface as inventory."""
import json,subprocess,sys,xml.etree.ElementTree as ET
from pathlib import Path
root=Path(sys.argv[1]); records=[]
host={'range deduction guides','sycl::range constructors. copy by value semantics on host'}
for category in ['item','range']:
 binary=root/'bin'/('test_'+category)
 result=subprocess.run([str(binary),'--list-tests','--reporter','xml'],text=True,capture_output=True,check=True)
 (root/f'upstream-list-{category}.xml').write_text(result.stdout)
 document=ET.fromstring(result.stdout)
 for test in document.findall('.//TestCase'):
  name=test.findtext('Name') or test.get('name')
  if not name:raise RuntimeError('upstream listing contains unnamed test')
  selector=''.join('\\'+c if c in '\\,[]' else c for c in name)
  records.append({'name':category+'/'+name,'test_name':name,'command':['bin/test_'+category,selector,'--reporter','junit'],
                  'timeout_seconds':120,'execution':'host' if name in host else 'device','validator':'catch2-junit',
                  'minimum_version':'SYCL 2020','capabilities':[],'environment':{}})
if not records:raise RuntimeError('upstream Catch2 inventory is empty')
if len({r['name'] for r in records})!=len(records):raise RuntimeError('duplicate upstream test name')
(root/'cases.jsonl').write_text(''.join(json.dumps(r)+'\n' for r in records))

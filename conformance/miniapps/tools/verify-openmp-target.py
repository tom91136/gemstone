#!/usr/bin/env python3
"""Reject an omp-target artifact that only initializes libomptarget or runs host loops."""
import json
from pathlib import Path
import re
import shlex
import subprocess
import sys

DEFINITIONS = {
    'babelstream': {'OMP_TARGET_GPU'},
    'minibude': {'OMP_TARGET'},
    'cloverleaf': {'OMP_TARGET', 'DIFFUSE_OVERLOAD'},
    'tealeaf': {'OMP_TARGET', 'DIFFUSE_OVERLOAD'},
}

def audit(app, commands, sections, symbols, architecture):
    required = DEFINITIONS[app]
    active = []
    for entry in commands:
        args = entry.get('arguments') or shlex.split(entry['command'])
        definitions = {arg[2:].split('=', 1)[0] for arg in args if arg.startswith('-D')}
        if required <= definitions:
            active.append(args)
    if not active:
        raise ValueError(f'{app}: missing OpenMP target source definitions {sorted(required)}')
    if not any('-fopenmp-targets=amdgcn-amd-amdhsa' in args and
               ('-march=' + architecture) in args for args in active):
        raise ValueError(f'{app}: no AMD OpenMP compilation for {architecture}')
    sizes = {}
    for line in sections.splitlines():
        match = re.match(r'\s*\[\s*\d+\]\s+(\S+)\s+\S+\s+[0-9a-fA-F]+\s+[0-9a-fA-F]+\s+([0-9a-fA-F]+)\b', line)
        if match:
            sizes[match[1]] = int(match[2], 16)
    entries = sizes.get('llvm_offload_entries', 0)
    images = sizes.get('.llvm.offloading', 0)
    if not entries or not images:
        raise ValueError(f'{app}: no nonempty OpenMP target entries/device image (initialization is not offload)')
    target_symbols = sorted(set(re.findall(r'\b(__tgt_target_(?:kernel|teams)\w*|__tgt_target)(?:@|\s|$)', symbols)))
    if not target_symbols:
        raise ValueError(f'{app}: no OpenMP target kernel launch reference (registration/data mapping alone is insufficient)')
    return dict(app=app, architecture=architecture, source_definitions=sorted(required),
                offload_entries_bytes=entries, device_image_bytes=images, target_launch_symbols=target_symbols)

if __name__ == '__main__':
    app, binary, database, architecture = sys.argv[1:]
    try:
        result = audit(app, json.loads(Path(database).read_text()),
                       subprocess.check_output(['readelf', '-SW', binary], text=True),
                       subprocess.check_output(['nm', '-D', binary], text=True), architecture)
    except ValueError as error:
        raise SystemExit(str(error))
    print(json.dumps(result, sort_keys=True))

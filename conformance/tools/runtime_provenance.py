#!/usr/bin/env python3
"""Linux loader evidence for distinguishing HIP frontends sharing an HSA execution boundary."""
import hashlib
import fcntl
import gzip
import json
import os
import shutil
from pathlib import Path
import re
import sys
import uuid

_locks = {}

BINDING = re.compile(r"binding file (.*?) \[\d+\] to (.*?) \[\d+\]: .*?symbol [`']([^`']+)'", re.M)
INITIALIZER = re.compile(r'calling init:\s+(.+)$', re.M)
HIP_SYMBOL = re.compile(r'(?:hip[A-Z]\w*|__hip(?:Register|Unregister)\w*)$')

def read_bindings(paths, directory):
    owners = {'hip': {}, 'hsa': {}}
    loaded = set()
    directory = Path(directory)
    def canonical(value):
        path = Path(value.strip())
        return str((path if path.is_absolute() else directory / path).resolve())
    for path in paths:
        text = Path(path).read_text(errors='replace')
        loaded.update(canonical(value) for value in INITIALIZER.findall(text))
        for source, owner, symbol in BINDING.findall(text):
            kind = 'hip' if HIP_SYMBOL.fullmatch(symbol) else 'hsa' if symbol.startswith('hsa_') else None
            if kind:
                owner = canonical(owner)
                owners[kind].setdefault(owner, set()).add(symbol)
                loaded.add(owner)
    return {kind: {path: sorted(symbols) for path, symbols in values.items()} for kind, values in owners.items()}, sorted(loaded)

def prepare(spec, role, report, identity):
    route = spec.get('runtime_route')
    if role != 'gemstone' or route is None:
        return {}
    if sys.platform != 'linux':
        raise RuntimeError('HIP runtime provenance collection requires the Linux dynamic loader; no Windows runtime qualification is implied')
    if route.get('id') not in ('hip', 'hip-hsa'):
        raise RuntimeError('unknown HIP runtime provenance route')
    if not re.fullmatch(r'[A-Za-z0-9_.-]+', identity):
        raise RuntimeError('invalid runtime provenance report identity')
    expected = {'hip': route.get('hip_library')}
    if route['id'] == 'hip-hsa':
        expected['hsa'] = route.get('hsa_library')
    for kind, path in expected.items():
        if not path or not Path(path).is_absolute() or not Path(path).is_file():
            raise RuntimeError(f'expected {kind} runtime must be an existing absolute path: {path}')
    report = Path(report)
    report.mkdir(parents=True, exist_ok=True)
    command = spec[role].get('command', [])
    command_identity = command[:6] if command[:1] == ['bash'] and command[4:5] == ['--runner'] else (
        command[:4] if command[:1] == ['bash'] else command[:1])
    key_input = json.dumps({'route': route, 'command': command_identity}, sort_keys=True).encode()
    key = hashlib.sha256(key_input).hexdigest()[:24]
    cache_root = report.parent / 'runtime-provenance'
    cache_root.mkdir(parents=True, exist_ok=True)
    cache = cache_root / (key + '.json')
    lock = (cache_root / (key + '.lock')).open('a+')
    fcntl.flock(lock, fcntl.LOCK_EX)
    if cache.is_file():
        spec['_runtime_provenance_cached'] = json.loads(cache.read_text())
        fcntl.flock(lock, fcntl.LOCK_UN)
        lock.close()
        return {}
    _locks[key] = lock
    spec['_runtime_provenance_cache'] = str(cache)
    spec['_runtime_provenance_lock'] = key
    prefix = report / (identity + '.loader-' + uuid.uuid4().hex)
    spec['_runtime_provenance_prefix'] = str(prefix)
    return {'LD_DEBUG': 'libs,bindings', 'LD_DEBUG_OUTPUT': str(prefix)}

def collect(spec, role, report, identity):
    route = spec.get('runtime_route')
    if role != 'gemstone' or route is None:
        return None
    if '_runtime_provenance_cached' in spec:
        return spec['_runtime_provenance_cached']
    prefix = spec.get('_runtime_provenance_prefix')
    if not prefix:
        raise RuntimeError('runtime provenance collection was not prepared')
    prefix = Path(prefix)
    paths = sorted(prefix.parent.glob(prefix.name + '.*'))
    bound, loaded = read_bindings(paths, spec[role]['directory'])
    expected = {'hip': str(Path(route['hip_library']).resolve())}
    if route['id'] == 'hip-hsa':
        expected['hsa'] = str(Path(route['hsa_library']).resolve())
    usage = spec.get('authenticated_requests') or {}
    no_device_work = (spec.get('attestation', {}).get('required') is False and
                      usage.get('cuda') == 0 and usage.get('execution') == 0)
    errors = []
    if not paths:
        errors.append('dynamic-loader evidence is absent')
    for kind, path in expected.items():
        others = set(bound[kind]) - {path}
        if others:
            errors.append(f'{kind} symbols bound to unexpected runtimes: {sorted(others)}')
        if path not in bound[kind] and not (no_device_work and path in loaded):
            errors.append(f'{kind} runtime binding was not observed: {path}')
    hashes = {kind: {'path': path, 'sha256': hashlib.sha256(Path(path).read_bytes()).hexdigest()}
              for kind, path in expected.items()}
    return dict(id=route['id'], verified=not errors, libraries=hashes, bindings=bound, loaded=loaded,
                evidence_files=[str(path) for path in paths], no_device_work=no_device_work, errors=errors,
                _cache_path=spec.get('_runtime_provenance_cache'), _lock_key=spec.get('_runtime_provenance_lock'))

def validate(evidence):
    if evidence is not None and not evidence['verified']:
        raise RuntimeError('HIP runtime provenance failed: ' + '; '.join(evidence['errors']))

def archive(evidence):
    """Retain full loader evidence without multiplying full-matrix disk use."""
    cache_path = evidence.pop('_cache_path', None)
    lock_key = evidence.pop('_lock_key', None)
    if cache_path is None and all(str(value).endswith('.gz') for value in evidence['evidence_files']):
        return
    paths = []
    try:
        for value in evidence['evidence_files']:
            source = Path(value)
            destination = source.with_name(source.name + '.gz')
            with source.open('rb') as incoming, gzip.open(destination, 'wb', compresslevel=1) as outgoing:
                shutil.copyfileobj(incoming, outgoing)
            source.unlink()
            paths.append(str(destination))
        evidence['evidence_files'] = paths
        if cache_path is not None and evidence['verified'] and not evidence['no_device_work']:
            destination = Path(cache_path)
            temporary = destination.with_name(destination.name + '.' + uuid.uuid4().hex + '.tmp')
            temporary.write_text(json.dumps(evidence, sort_keys=True))
            os.replace(temporary, destination)
    finally:
        lock = _locks.pop(lock_key, None)
        if lock is not None:
            fcntl.flock(lock, fcntl.LOCK_UN)
            lock.close()

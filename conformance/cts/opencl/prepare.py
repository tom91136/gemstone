#!/usr/bin/env python3
"""Fetch verified source archives or extract them into a fresh container work directory."""
import hashlib
import json
from pathlib import Path
import shutil
import sys
import tarfile
import urllib.request
import zipfile

mode, archive_directory, *rest = sys.argv[1:]
archives = Path(archive_directory)
pins = json.loads((Path(__file__).parent / 'pins.json').read_text())
archives.mkdir(parents=True, exist_ok=True)
for pin in pins['archives']:
    archive = archives / pin['name']
    if mode == 'fetch' and not archive.exists():
        cached = Path.home() / '.cache/gemstone/isa-references' / pin['name']
        if cached.exists():
            shutil.copyfile(cached, archive)
        else:
            temporary = archive.with_suffix(archive.suffix + '.tmp')
            with urllib.request.urlopen(pin['url']) as response, temporary.open('wb') as output:
                shutil.copyfileobj(response, output)
            temporary.replace(archive)
    if hashlib.sha256(archive.read_bytes()).hexdigest() != pin['sha256']:
        raise SystemExit('source archive hash mismatch: ' + str(archive))
    if mode == 'extract':
        destination = Path(rest[0])
        destination.mkdir(parents=True, exist_ok=True)
        if archive.suffix == '.zip':
            with zipfile.ZipFile(archive) as source:
                source.extractall(destination)
        else:
            with tarfile.open(archive) as source:
                source.extractall(destination, filter='data')
        (destination / pin['directory']).rename(destination / pin['destination'])
if mode not in ('fetch', 'extract'):
    raise SystemExit('expected fetch or extract')

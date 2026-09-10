#!/usr/bin/env python3
"""Enumerate upstream executable registries; never infer test names from filenames."""
import hashlib
import json
from pathlib import Path
import re
import shutil
import subprocess
import sys

source, output, image = Path(sys.argv[1]), Path(sys.argv[2]), sys.argv[3]
builder_image = sys.argv[4]
recipe = Path(__file__).parent
pins = json.loads((recipe / 'pins.json').read_text())
# Reviewed query-only entry points. Remaining entries retain device attestation.
host_tests = {
    'basic': {'host_numeric_constants'},
    'computeinfo': {'computeinfo', 'extended_versioning', 'device_uuid', 'pci_bus_info', 'conformance_version'},
    'api': {'negative_get_platform_ids', 'negative_get_platform_info', 'get_platform_info',
            'get_device_info', 'get_device_info_31', 'get_device_info_comparability',
            'get_context_info', 'get_context_info_mult_devices', 'get_command_queue_info',
            'get_command_queue_info_compatibility', 'get_sampler_info', 'get_sampler_info_compatibility'},
}
rows = []
for category in pins['categories']:
    declarations = {}
    for path in sorted((source / 'test_conformance' / category).glob('*.cpp')):
        text = path.read_text()
        for match in re.finditer(r'REGISTER_TEST\s*\(\s*(\w+)\s*\)', text):
            declarations[match[1]] = ('1.2', str(path.relative_to(source)))
        for match in re.finditer(r'REGISTER_TEST_VERSION\s*\(\s*(\w+)\s*,\s*Version\(\s*(\d+)\s*,\s*(\d+)\s*\)\s*\)', text):
            declarations[match[1]] = (match[2] + '.' + match[3], str(path.relative_to(source)))
    executable = output / 'bin' / ('test_' + category)
    listing = subprocess.check_output([str(executable), '--list'], text=True)
    (output / 'provenance' / (category + '-list.txt')).write_text(listing)
    names = re.findall(r'^\t([A-Za-z_][A-Za-z_0-9]*)\s*$', listing, re.MULTILINE)
    if not names or len(names) != len(set(names)):
        raise SystemExit('invalid upstream inventory: ' + category)
    for name in names:
        if name not in declarations:
            raise SystemExit('missing pinned version metadata for ' + category + '/' + name)
        version, location = declarations[name]
        host = name in host_tests.get(category, set())
        rows.append(dict(name=category + '/' + name, test_name=name,
                         command=['bin/test_' + category, name, 'gpu'] + (['--spirv-binaries-path', '{artifact}/bin/spirv_bin'] if category == 'compiler' else []),
                         timeout_seconds=120, execution='host' if host else 'device',
                         validator='opencl-cts', default=category + '/' + name in pins['default_cases'], minimum_version=version, capabilities=[],
                         capability_policy='upstream-runtime-checks', source=location,
                         attestation_reason='Reviewed host-only header/property CTS entry point' if host else 'OpenCL provider operations required',
                         environment={'CL_CONFORMANCE_RESULTS_FILENAME': '{report_directory}/opencl-cts.json'}))
missing = set(pins['default_cases']) - {row['name'] for row in rows}
if missing:
    raise SystemExit('default CTS cases absent from upstream executable: ' + str(sorted(missing)))
(output / 'cases.jsonl').write_text(''.join(json.dumps(row, sort_keys=True) + '\n' for row in rows))
manifest = {key: pins[key] for key in ['suite', 'repository', 'revision', 'version', 'profile', 'categories', 'archives', 'default_cases']}
manifest.update(image=image, builder_image=builder_image, case_count=len(rows), default_case_count=sum(row['default'] for row in rows),
                recipe_sha256={path.name: hashlib.sha256(path.read_bytes()).hexdigest() for path in sorted(recipe.iterdir()) if path.is_file()}, capability_policy='Upstream runtime feature checks are authoritative; empty capabilities means no extra prefilter.',
                compiler=subprocess.check_output(['c++', '--version'], text=True).splitlines()[0],
                packages=subprocess.check_output(['dpkg-query', '-W', 'spirv-tools', 'ocl-icd-opencl-dev', 'libc6-dev'], text=True).splitlines(),
                patches=[{'file': path.name, 'sha256': hashlib.sha256(path.read_bytes()).hexdigest()} for path in sorted((recipe / 'patches').glob('*.patch'))],
                runtime_source_compiler='Gemstone online OpenCL compilation requires matching icpx, llvm-spirv and llvm-link; SDK is not bundled with CTS.')
(output / 'manifest.json').write_text(json.dumps(manifest, indent=2) + '\n')
shutil.copyfile(recipe / 'pins.json', output / 'provenance/pins.json')

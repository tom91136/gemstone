#!/usr/bin/env bash
set -euo pipefail
[[ $# == 3 ]] || { echo 'usage: build.sh <source> <build> <out>' >&2; exit 2; }
source_dir="$1"; build_dir="$2"; output_dir="$3"
script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"
source /opt/intel/oneapi/setvars.sh >/dev/null 2>&1 || true
mkdir -p "$build_dir" "$output_dir/bin" "$output_dir/share/licenses"
/opt/intel/oneapi/compiler/latest/bin/icpx --version > "$output_dir/compiler-version.txt"
python3 - "$source_dir" "$build_dir/exclude.txt" <<'PY'
from pathlib import Path
import sys
source=Path(sys.argv[1]); selected={'item','range'}
Path(sys.argv[2]).write_text('\n'.join(sorted(p.name for p in (source/'tests').iterdir() if p.is_dir() and p.name not in selected and p.name!='common'))+'\n')
PY
cmake -S "$source_dir" -B "$build_dir" -G Ninja \
  -DCMAKE_BUILD_TYPE=Release -DCMAKE_CXX_COMPILER=/opt/intel/oneapi/compiler/latest/bin/icpx \
  -DSYCL_IMPLEMENTATION=DPCPP -DDPCPP_INSTALL_DIR=/opt/intel/oneapi/compiler/latest \
  -DDPCPP_TARGET_TRIPLES=spir64 -DSYCL_CTS_ENABLE_FULL_CONFORMANCE=OFF \
  -DSYCL_CTS_ENABLE_OPENCL_INTEROP_TESTS=OFF \
  -DSYCL_CTS_EXCLUDE_TEST_CATEGORIES="$build_dir/exclude.txt"
cmake --build "$build_dir" --target test_item test_range --parallel "${CMAKE_BUILD_PARALLEL_LEVEL:-4}"
cp "$build_dir/bin/test_item" "$build_dir/bin/test_range" "$output_dir/bin/"
cp "$source_dir/LICENSE" "$output_dir/share/licenses/SYCL-CTS.txt"
cp "$source_dir/vendor/Catch2/LICENSE.txt" "$output_dir/share/licenses/Catch2.txt"
cp "$source_dir/vendor/OpenCL-Headers/LICENSE" "$output_dir/share/licenses/OpenCL-Headers.txt"
cp /opt/intel/oneapi/licensing/latest/licensing/*/license.htm "$output_dir/share/licenses/oneAPI-license.htm"
source /gemstone-tools/runtime-bundle.sh
bundle_begin "$output_dir" 1
for plugin in /opt/intel/oneapi/compiler/*/lib/libur_adapter*.so* \
  /opt/intel/oneapi/compiler/*/lib/libOpenCL.so* /opt/intel/oneapi/compiler/*/lib/libintelocl.so* \
  /opt/intel/oneapi/compiler/*/lib/libsycl-preview.so* /opt/intel/oneapi/compiler/*/lib/libsycl-jit.so* \
  /usr/lib/x86_64-linux-gnu/libze_loader.so*; do
  [[ -e "$plugin" ]] || continue
  readelf -h "$(readlink -f "$plugin")" >/dev/null 2>&1 || continue
  bundle_library "$plugin"
done
bundle_resolve
bundle_relocate "$output_dir"
LD_LIBRARY_PATH="$output_dir/lib${LD_LIBRARY_PATH:+:$LD_LIBRARY_PATH}" python3 "$script_dir/inventory.py" "$output_dir"

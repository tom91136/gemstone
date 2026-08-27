#!/usr/bin/env bash
set -euo pipefail

root_dir="$(cd "$(dirname "$0")/../.." && pwd)"
native_library="$root_dir/target/out/native0.5/scala-3.8.3/gemstone-native/libgemstone-native.so"
output_dir="$root_dir/target/conformance/smoke"
library_dir="$output_dir/lib"
recording="$output_dir/api-recording.jsonl"
cuda_header="$(find "$root_dir/target/out" -path '*/gemstone-codegen/isa-references/*/nvidia/cuda/usr/local/cuda-13.3/targets/*/include/cuda.h' -print -quit)"

if [[ ! -f "$native_library" ]]; then
  printf 'missing %s; run sbt nativeNative/nativeLink first\n' "$native_library" >&2
  exit 1
fi
if [[ -z "$cuda_header" ]]; then
  printf 'pinned CUDA 13.3 headers are absent; run sbt codegen/prepareISAReferences first\n' >&2
  exit 1
fi
cuda_include="$(dirname "$cuda_header")"

mkdir -p "$library_dir"
install -m 755 "$native_library" "$library_dir/libgemstone-native.so"

cc \
  -std=c17 \
  -fPIC \
  -Wall \
  -Wextra \
  -Werror \
  -shared \
  "$root_dir/native/dispatch/gemstone_dispatch.c" \
  -I"$root_dir/generated/c" \
  -I"$root_dir/native/dispatch" \
  -L"$library_dir" \
  -Wl,-soname,libgemstone_dispatch.so \
  -Wl,-rpath,'$ORIGIN' \
  -Wl,--no-as-needed \
  -l:libgemstone-native.so \
  -pthread \
  -o "$library_dir/libgemstone_dispatch.so"

cc \
  -std=c17 \
  -fPIC \
  -Wall \
  -Wextra \
  -Werror \
  -shared \
  "$root_dir/generated/c/gemstone_cuda.c" \
  "$root_dir/generated/c/gemstone_cuda_rpc_client.c" \
  "$root_dir/generated/c/gemstone_cuda_driver_stubs.c" \
  "$root_dir/native/dispatch/gemstone_rpc_transport.c" \
  -I"$root_dir/generated/c" \
  -I"$root_dir/native/dispatch" \
  -I"$cuda_include" \
  -L"$library_dir" \
  -Wl,-rpath,'$ORIGIN' \
  -Wl,-soname,libcuda.so.1 \
  -Wl,--version-script="$root_dir/generated/c/gemstone_cuda.map" \
  -lgemstone_dispatch \
  -o "$library_dir/libcuda.so.1"
ln -sfn libcuda.so.1 "$library_dir/libcuda.so"

for source in smoke concurrent-foreign-entry surface-smoke; do
  g++ \
    -std=c++20 \
    -Wall \
    -Wextra \
    -Werror \
    "$root_dir/conformance/smoke/$source.cpp" \
    -I"$root_dir/generated/c" \
    -L"$library_dir" \
    -Wl,-rpath,"$library_dir" \
    -lcuda \
    -pthread \
    -o "$output_dir/$source"
done

: > "$recording"
GEMSTONE_RECORD="$recording" "$output_dir/smoke"
GEMSTONE_RECORD="$recording" "$output_dir/concurrent-foreign-entry"
"$output_dir/surface-smoke"

request_count="$(grep -c '"phase":"request"' "$recording")"
response_count="$(grep -c '"phase":"response"' "$recording")"
boundary_count="$(grep -c '"phase":"boundary"' "$recording")"
if [[ "$request_count" != "16002" || "$response_count" != "16002" || "$boundary_count" != "1" ]]; then
  printf 'unexpected recording counts: requests=%s responses=%s boundary=%s\n' \
    "$request_count" "$response_count" "$boundary_count" >&2
  exit 1
fi

{
  printf 'GEMSTONE generated facade report\n\n'
  file "$library_dir/libcuda.so.1" "$library_dir/libgemstone_dispatch.so" "$library_dir/libgemstone-native.so"
  printf '\nFacade exports:\n'
  nm -D --defined-only "$library_dir/libcuda.so.1"
  printf '\nCore exports:\n'
  nm -D --defined-only "$library_dir/libgemstone-native.so"
  printf '\nFacade dynamic metadata:\n'
  readelf -d "$library_dir/libcuda.so.1" | grep -E 'SONAME|NEEDED|RUNPATH'
  printf '\nFacade symbol versions:\n'
  readelf --version-info "$library_dir/libcuda.so.1"
  printf '\nRecorded requests: %s\nRecorded responses: %s\nRecorded boundary failures: %s\n' \
    "$request_count" "$response_count" "$boundary_count"
} > "$output_dir/export-report.txt"

printf 'native smoke and foreign-thread tests passed; report: %s\n' "$output_dir/export-report.txt"
printf 'API recording: %s\n' "$recording"

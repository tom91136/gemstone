#!/usr/bin/env bash
set -euo pipefail

root_dir="$(cd "$(dirname "$0")/../.." && pwd)"
core_library="$root_dir/target/out/native0.5/scala-3.8.3/gemsi-core/libgemsi-core.so"
output_dir="$root_dir/target/conformance/smoke"
library_dir="$output_dir/lib"
recording="$output_dir/api-recording.jsonl"

if [[ ! -f "$core_library" ]]; then
  printf 'missing %s; run sbt nativeNative/nativeLink first\n' "$core_library" >&2
  exit 1
fi

mkdir -p "$library_dir"
install -m 755 "$core_library" "$library_dir/libgemsi-core.so"

cc \
  -std=c17 \
  -fPIC \
  -Wall \
  -Wextra \
  -Werror \
  -shared \
  "$root_dir/native/dispatch/gemsi_dispatch.c" \
  -I"$root_dir/generated/c" \
  -I"$root_dir/native/dispatch" \
  -L"$library_dir" \
  -Wl,-soname,libgemsi_dispatch.so \
  -Wl,-rpath,'$ORIGIN' \
  -Wl,--no-as-needed \
  -l:libgemsi-core.so \
  -pthread \
  -o "$library_dir/libgemsi_dispatch.so"

cc \
  -std=c17 \
  -fPIC \
  -Wall \
  -Wextra \
  -Werror \
  -shared \
  "$root_dir/generated/c/gemsi_cuda.c" \
  "$root_dir/generated/c/gemsi_cuda_rpc_client.c" \
  "$root_dir/generated/c/gemsi_cuda_driver_stubs.c" \
  "$root_dir/native/dispatch/gemsi_rpc_transport.c" \
  -I"$root_dir/generated/c" \
  -I"$root_dir/native/dispatch" \
  -L"$library_dir" \
  -Wl,-rpath,'$ORIGIN' \
  -Wl,-soname,libcuda.so.1 \
  -Wl,--version-script="$root_dir/generated/c/gemsi_cuda.map" \
  -lgemsi_dispatch \
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
GEMSI_RECORD="$recording" "$output_dir/smoke"
GEMSI_RECORD="$recording" "$output_dir/concurrent-foreign-entry"
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
  file "$library_dir/libcuda.so.1" "$library_dir/libgemsi_dispatch.so" "$library_dir/libgemsi-core.so"
  printf '\nFacade exports:\n'
  nm -D --defined-only "$library_dir/libcuda.so.1"
  printf '\nCore exports:\n'
  nm -D --defined-only "$library_dir/libgemsi-core.so"
  printf '\nFacade dynamic metadata:\n'
  readelf -d "$library_dir/libcuda.so.1" | grep -E 'SONAME|NEEDED|RUNPATH'
  printf '\nFacade symbol versions:\n'
  readelf --version-info "$library_dir/libcuda.so.1"
  printf '\nRecorded requests: %s\nRecorded responses: %s\nRecorded boundary failures: %s\n' \
    "$request_count" "$response_count" "$boundary_count"
} > "$output_dir/export-report.txt"

printf 'native smoke and foreign-thread tests passed; report: %s\n' "$output_dir/export-report.txt"
printf 'API recording: %s\n' "$recording"

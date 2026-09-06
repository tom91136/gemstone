#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 2 ]]; then
  echo "usage: run-emulated-matrix.sh <artifact-directory> <cuda|hip|sycl>" >&2
  exit 2
fi

artifact="$1"
backend="$2"
case "$backend" in
  cuda|hip|sycl) ;;
  *) echo "unsupported backend: $backend" >&2; exit 2 ;;
esac

runner="/home/tom/gemstone/conformance/unified/tools/run-relocated.sh"
if [[ ! -x "$runner" ]]; then
  echo "relocation runner is missing: $runner" >&2
  exit 1
fi

processor_count="$(getconf _NPROCESSORS_ONLN 2>/dev/null || nproc)"
default_fanout=$((processor_count > 2 ? processor_count - 2 : 1))
((default_fanout > 64)) && default_fanout=64
if [[ "$backend" == hip && "$default_fanout" -gt 1 ]]; then
  # ROCm/HSA code-object installation is substantially more memory-heavy than
  # CUDA/SYCL startup. Keep the default session fan-out conservative. An
  # explicit override remains available up to the host cap for experiments.
  default_fanout=1
fi
fanout_override="${GEMSTONE_CONFORMANCE_FANOUT:-}"
fanout="${fanout_override:-$default_fanout}"
if ! [[ "$fanout" =~ ^[1-9][0-9]*$ ]]; then
  echo "GEMSTONE_CONFORMANCE_FANOUT must be a positive integer: $fanout" >&2
  exit 2
fi
if ((fanout > 64 || fanout > processor_count)); then
  echo "GEMSTONE_CONFORMANCE_FANOUT=$fanout exceeds host fan-out cap $processor_count" >&2
  exit 2
fi

case_log_directory="${GEMSTONE_CONFORMANCE_LOG_DIR:-$(mktemp -d "${TMPDIR:-/tmp}/gemstone-unified-matrix.XXXXXX")}"
mkdir -p -- "$case_log_directory"

algorithm_sizes="0,1,7,15,31,32,33,63,64,65,127,128,129,257,1023,4096"
if [[ "$backend" == sycl ]]; then
  algorithm_sizes="0,1,7,15,31,32,33,63,64,65,127,128,129,257,1023,1024"
fi
blas_sizes="1,2,7,31,32,64,257,1024"
matrix_shapes="1x1x1,2x2x3,3x5x7,7x5x9,17x13x19"
edge_sizes="0,1,2,7,15,31,32,33,63,64,257,1023,1024"
edge_blas_sizes="$edge_sizes"
if [[ "$backend" == sycl ]]; then
  edge_blas_sizes="0,1,2,7,15,31,32,33,63,64,257"
fi
stress_sizes="257,4096,16384"

active_pids=()
active_names=()
failed=0

reap_one() {
  local pid="${active_pids[0]}"
  local name="${active_names[0]}"
  if wait "$pid"; then
    local summary
    summary="$(awk '/^SUMMARY / { line=$0 } END { print line }' "$case_log_directory/$name.log")"
    echo "$name: ${summary:-completed without summary}"
  else
    failed=1
    echo "FAILED $name:" >&2
    sed -n '1,240p' "$case_log_directory/$name.log" >&2
  fi
  active_pids=("${active_pids[@]:1}")
  active_names=("${active_names[@]:1}")
}

run_slice() {
  local name="$1"
  shift
  "$runner" "$artifact" "$backend" "$@" \
    --quiet-pass --allow-empty-shard --allow-all-skipped \
    >"$case_log_directory/$name.log" 2>&1
}

start_slice() {
  local name="$1"
  shift
  run_slice "$name" "$@" &
  active_pids+=("$!")
  active_names+=("$name")
  if ((${#active_pids[@]} >= fanout)); then
    reap_one
  fi
}

for scalar_type in i8 i32 i64 f32 f64 f16; do
  for shard in 0 1 2 3 4 5 6 7; do
    start_slice "algorithms-$scalar_type-$shard" \
      --suite=algorithms \
      "--algorithm-sizes=$algorithm_sizes" \
      "--algorithm-types=$scalar_type" \
      "--filter=^algorithms/.*${scalar_type}/" \
      "--shard-index=$shard" --shard-count=8
  done
done

for scalar_type in i8 f32 f64 f16; do
  for shard in 0 1 2 3 4 5 6 7; do
    start_slice "blas-$scalar_type-$shard" \
      --suite=blas \
      "--blas-sizes=$blas_sizes" \
      "--matrix-shapes=$matrix_shapes" \
      "--blas-types=$scalar_type" \
      "--filter=^blas/.*${scalar_type}/" \
      "--shard-index=$shard" --shard-count=8
  done
done

for scalar_type in i8 i32 i64 f32 f64 f16; do
  for shard in 0 1 2 3 4 5 6 7; do
    start_slice "edge-algorithms-$scalar_type-$shard" \
      --suite=edge-cases \
      "--edge-case-sizes=$edge_sizes" \
      "--edge-algorithm-types=$scalar_type" \
      "--filter=^edge-cases/algorithms/.*${scalar_type}/" \
      "--shard-index=$shard" --shard-count=8
  done
done

for scalar_type in i8 f32 f64 f16; do
  for shard in 0 1 2 3 4 5 6 7; do
    start_slice "edge-blas-$scalar_type-$shard" \
      --suite=edge-cases \
      "--edge-case-sizes=$edge_blas_sizes" \
      "--edge-blas-types=$scalar_type" \
      "--filter=^edge-cases/blas/.*${scalar_type}/" \
      "--shard-index=$shard" --shard-count=8
  done
done

for scalar_type in i8 i32 i64 f32 f64 f16; do
  for shard in 0 1 2 3 4 5 6 7; do
    start_slice "async-$scalar_type-$shard" \
      --suite=async \
      "--edge-case-sizes=$edge_sizes" \
      "--edge-algorithm-types=$scalar_type" \
      "--filter=^async/.*${scalar_type}/" \
      "--shard-index=$shard" --shard-count=8
  done
done

for scalar_type in i8 i32 i64 f32 f64; do
  for shard in 0 1 2 3 4 5 6 7; do
    start_slice "stress-$scalar_type-$shard" \
      --suite=stress \
      "--stress-sizes=$stress_sizes" \
      --stress-iterations=8 \
      "--stress-types=$scalar_type" \
      "--filter=^stress/.*${scalar_type}/" \
      "--shard-index=$shard" --shard-count=8
  done
done

while ((${#active_pids[@]} != 0)); do
  reap_one
done

echo "matrix logs: $case_log_directory"
exit "$failed"

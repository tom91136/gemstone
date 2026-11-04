#!/usr/bin/env bash
# clang cannot emit a PTX-only CUDA image, so compiles split into PTX, fatbinary, and a host compile embedding it
set -euo pipefail

args=("$@")
index=-1
for i in "${!args[@]}"; do
  case "${args[$i]##*/}" in
    clang | clang++ | clang-[0-9]* | clang++-[0-9]*) index=$i; break ;;
  esac
done
if ((index < 0)); then
  echo "clang-cuda-ptx: no clang in: $*" >&2
  exit 2
fi
launcher=("${args[@]:0:index}")
compiler=${args[index]}
rest=("${args[@]:index+1}")

compile=0 passthrough=0 output="" cuda_path=${CUDAToolkit_ROOT:-} architectures=() device=()
for ((i = 0; i < ${#rest[@]}; i++)); do
  arg=${rest[i]}
  case "$arg" in
    -c) compile=1 ;;
    -o) i=$((i + 1)); output=${rest[i]} ;;
    -MD | -MMD) ;;
    -MT | -MF | -MQ) i=$((i + 1)) ;;
    -E | -S | -M | -MM | --cuda-device-only | --cuda-host-only) passthrough=1 ;;
    -fgpu-rdc) echo "clang-cuda-ptx: relocatable device code is unsupported" >&2; exit 2 ;;
    --cuda-gpu-arch=*)
      [[ " ${architectures[*]-} " == *" ${arg#*=} "* ]] || architectures+=("${arg#*=}")
      device+=("$arg") ;;
    --cuda-path=*) cuda_path=${arg#*=}; device+=("$arg") ;;
    *) device+=("$arg") ;;
  esac
done

if ((!compile || passthrough || ${#architectures[@]} == 0)); then
  exec "${args[@]}"
fi
if ((${#architectures[@]} != 1)); then
  echo "clang-cuda-ptx: expected one CUDA architecture, got: ${architectures[*]}" >&2
  exit 2
fi

architecture=${architectures[0]}
ptx="$output.ptx"
"${launcher[@]}" "$compiler" "${device[@]}" -Wno-unused-command-line-argument --cuda-device-only -S -o "$ptx"

# the embedded path is part of the host compile's cache key, so it names the content
digest=$(sha256sum "$ptx" | cut -c1-16)
fatbin="$output.$digest.fatbin"
rm -f "$output".*.fatbin
"$cuda_path/bin/fatbinary" -64 --create="$fatbin" "--image3=kind=ptx,sm=${architecture#sm_},file=$ptx"

exec "${launcher[@]}" "$compiler" "${rest[@]}" -Wno-unused-command-line-argument --cuda-host-only -Xclang -fcuda-include-gpubinary -Xclang "$fatbin"

#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 2 ]]; then
  echo "usage: run-relocated.sh <artifact-directory> <host|cuda|hip|sycl|opencl|hsa> [runner arguments ...]" >&2
  exit 2
fi

artifact_directory="$(cd -- "$1" && pwd -P)"
backend="$2"
case "$backend" in
  host|cuda|hip|sycl|opencl|hsa) ;;
  *) echo "unsupported backend: $backend" >&2; exit 2 ;;
esac
shift 2

runner="unified_suite_${backend}"
if [[ "${1:-}" == --runner ]]; then
  [[ $# -ge 2 ]] || { echo "--runner requires an executable name" >&2; exit 2; }
  runner="$2"
  [[ "$runner" =~ ^unified_suite_[A-Za-z0-9_]+$ ]] || { echo "invalid runner name: $runner" >&2; exit 2; }
  shift 2
fi

# Do not let an inherited SDK/toolchain variable turn a missing optional
# component into an accidental host ROCm/CUDA/oneAPI dependency. Device
# selection/provider variables (CUDA_VISIBLE_DEVICES, ROCR_VISIBLE_DEVICES,
# SYCL_DEVICE_FILTER, OCL_ICD_VENDORS, ...) are intentionally preserved.
unset CUDA_HOME CUDA_PATH CUDA_ROOT CUDA_TOOLKIT_ROOT_DIR \
  HIP_PATH ROCM_PATH ROCM_HOME ONEAPI_ROOT ONEAPI_HOME DPCPP_HOME \
  INTEL_ONEAPI_ROOT HSA_TOOLS_LIB ROCP_TOOL_LIB CPATH LIBRARY_PATH \
  CMAKE_PREFIX_PATH

manifest="$artifact_directory/manifest.tsv"
target_arch=""
target_archs=""
if [[ -f "$manifest" ]]; then
  manifest_backend="$(awk -F '\t' '$1 == "backend" { print $2; exit }' "$manifest")"
  if [[ -n "$manifest_backend" && "$manifest_backend" != "$backend" ]]; then
    echo "artifact backend is $manifest_backend, not $backend: $artifact_directory" >&2
    exit 1
  fi
  target_arch="$(awk -F '\t' '$1 == "target-arch" { print $2; exit }' "$manifest")"
  target_archs="$(awk -F '\t' '$1 == "target-archs" { print $2; exit }' "$manifest")"
fi

# A manifest naming several device targets leaves the per-run choice to the
# caller; only a single-target artifact implies a default.
manifest_targets=()
if [[ -n "$target_archs" ]]; then
  IFS=',;' read -r -a manifest_targets <<<"$target_archs"
  if ((${#manifest_targets[@]} == 1)); then
    target_arch="${manifest_targets[0]}"
  elif ((${#manifest_targets[@]} > 1)); then
    target_arch=""
  fi
fi

executable="$artifact_directory/bin/$runner"
if [[ ! -x "$executable" ]]; then
  echo "relocated runner is missing or not executable: $executable" >&2
  exit 1
fi

library_path="$artifact_directory/lib"
if [[ -d "$library_path" ]]; then
  # The artifact's userspace closure must win without inheriting a host CUDA,
  # ROCm or oneAPI installation.  The dynamic loader still supplies the
  # explicitly host-owned libc/loader and kernel-driver ABI libraries through
  # its normal system paths.
  export LD_LIBRARY_PATH="$library_path"
fi
if [[ "$backend" == sycl && -z "${UR_ADAPTERS_FORCE_LOAD:-}" ]]; then
  # The oneAPI UR loader does not search an arbitrary relocated directory for
  # adapters.  Force the bundled Level Zero adapter unless the caller selected
  # a different adapter explicitly (for example, the OpenCL adapter).
  adapter="$library_path/libur_adapter_level_zero.so.0.12.0"
  if [[ -f "$adapter" ]]; then
    export UR_ADAPTERS_FORCE_LOAD="$adapter"
  fi
fi
case "$backend" in
  cuda)
    # CUDA's static runtime performs host-side architecture checks from the
    # driver function attributes. Preserve the build target in a relocated
    # artifact instead of relying on host GPU discovery.
    if [[ -n "${GEMSTONE_RPC_TOKEN:-}" && -z "${GEMSTONE_CUDA_TARGET:-${GEMSTONE_CUDA_TARGET_ARCH:-}}" ]]; then
      if [[ -n "$target_arch" ]]; then
        export GEMSTONE_CUDA_TARGET="$target_arch"
      elif [[ -n "$target_archs" ]]; then
        echo "artifact contains multiple device targets: $target_archs" >&2
        echo "set GEMSTONE_CUDA_TARGET to select one for this run" >&2
      fi
    fi
    ;;
  hip|hsa)
    if [[ -n "${GEMSTONE_RPC_TOKEN:-}" && -z "${GEMSTONE_AMDGPU_TARGET:-}" ]]; then
      if [[ -n "$target_arch" ]]; then
        export GEMSTONE_AMDGPU_TARGET="$target_arch"
      elif [[ -n "$target_archs" ]]; then
        echo "artifact contains multiple device targets: $target_archs" >&2
        echo "set GEMSTONE_AMDGPU_TARGET to select one for this run" >&2
      fi
    fi
    # rocBLAS discovers Tensile kernels through a runtime path that is not
    # represented in DT_NEEDED.  Keep that lookup inside the relocated
    # artifact as well; otherwise an incomplete/host library directory can
    # make rocBLAS abort before the provider receives its first dispatch.
    tensile_library="$library_path/rocblas/library"
    if [[ -d "$tensile_library" && -z "${ROCBLAS_TENSILE_LIBPATH:-}" ]]; then
      export ROCBLAS_TENSILE_LIBPATH="$tensile_library"
    fi
    if [[ -f "$artifact_directory/share/libdrm/amdgpu.ids" ]]; then
      export AMDGPU_ASIC_ID_TABLE_PATHS="$artifact_directory/share/libdrm${AMDGPU_ASIC_ID_TABLE_PATHS:+:$AMDGPU_ASIC_ID_TABLE_PATHS}"
    fi
    ;;
esac
exec "$executable" "$@"

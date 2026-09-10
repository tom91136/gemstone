#!/usr/bin/env bash
set -euo pipefail

script_directory="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"
source "$script_directory/../../tools/container-common.sh"
conformance_fixture_lock "$@"

usage() {
  cat >&2 <<'EOF'
usage: container-build.sh <host|cuda|hip|sycl|opencl|hsa> [output-directory]

Build the unified suite in a pinned vendor container and install a relocatable
artifact on the host.  The output directory must be empty or absent.

Images can be overridden with GEMSTONE_<BACKEND>_IMAGE.  The default
engine is podman (set GEMSTONE_CONTAINER_ENGINE to use docker).
Set GEMSTONE_UNIFIED_CONTAINER_RUN_CTEST=1 to run CTest in the build container.
Set GEMSTONE_UNIFIED_BUNDLE_RUNTIME=0 to omit vendor user-space libraries.
Set GEMSTONE_CONTAINER_DEVICES to a comma-separated list of Podman/
Docker --device values when running CTest against a physical GPU.
UNIFIED_SUITE_* cache variables are forwarded to the container configure.
UNIFIED_SUITE_CUDA_ARCHITECTURES and UNIFIED_SUITE_HIP_ARCHITECTURES can pin
the device code targets as a comma- or semicolon-separated list (defaults are
CUDA `75` and AMDGPU `gfx900`, so a build does not depend on GPU discovery on
the host).
The build/run container has no network by default; set
GEMSTONE_CONTAINER_NETWORK to an engine-supported network mode only
if a custom SDK requires it.
EOF
  exit 2
}

[[ $# -ge 1 && $# -le 2 ]] || usage
backend="$1"
case "$backend" in host|cuda|hip|sycl|opencl|hsa) ;; *) usage ;; esac

source_directory="$(cd -- "$script_directory/.." && pwd -P)"
repository_directory="$(cd -- "$source_directory/../.." && pwd -P)"
output_directory="${2:-$repository_directory/target/unified-artifacts/$backend}"
output_directory="$(mkdir -p -- "$output_directory" && cd -- "$output_directory" && pwd -P)"

if [[ -n "$(find "$output_directory" -mindepth 1 -maxdepth 1 -print -quit)" ]]; then
  echo "output directory is not empty: $output_directory" >&2
  echo "remove it explicitly or choose another output directory" >&2
  exit 1
fi

for retired in UNIFIED_SUITE_CTEST_SHARDS UNIFIED_SUITE_SYCL_ALGORITHM_SIZES UNIFIED_SUITE_SYCL_EDGE_BLAS_SIZES; do
  if [[ -v "$retired" ]]; then
    echo "$retired is retired; use the common matrix settings" >&2
    exit 2
  fi
done

conformance_container_init
conformance_build_image

cmake_args=(
  -DCMAKE_BUILD_TYPE=Release
  -DCMAKE_INSTALL_PREFIX=/out
  -DCMAKE_INSTALL_LIBDIR=lib
  -DUNIFIED_SUITE_BUILD_HOST=OFF
  -DUNIFIED_SUITE_BUILD_CUDA=OFF
  -DUNIFIED_SUITE_BUILD_HIP=OFF
  -DUNIFIED_SUITE_BUILD_SYCL=OFF
  -DUNIFIED_SUITE_BUILD_OPENCL=OFF
  -DUNIFIED_SUITE_BUILD_HSA=OFF
  -DUNIFIED_SUITE_INSTALL_RUNNERS=ON
)
target_arch_manifest=""
target_archs_manifest=""
case "$backend" in
  host)
    cmake_args+=(
      -DUNIFIED_SUITE_BUILD_HOST=ON
      -DUNIFIED_SUITE_BUILD_CUDA=OFF
      -DUNIFIED_SUITE_BUILD_HIP=OFF
      -DUNIFIED_SUITE_BUILD_SYCL=OFF
    )
    ;;
  cuda)
    cuda_architectures="${UNIFIED_SUITE_CUDA_ARCHITECTURES:-${GEMSTONE_CUDA_ARCH:-75}}"
    cuda_architectures="${cuda_architectures//sm_/}"
    cuda_architectures="${cuda_architectures//,/;}"
    cuda_target="${cuda_architectures%%[;,]*}"
    cuda_target="${cuda_target%%-*}"
    target_arch_manifest="sm_${cuda_target#sm_}"
    target_archs_manifest=""
    IFS=',;' read -r -a cuda_architecture_list <<<"$cuda_architectures"
    for cuda_architecture in "${cuda_architecture_list[@]}"; do
      cuda_architecture="${cuda_architecture%%-*}"
      target_archs_manifest="${target_archs_manifest:+${target_archs_manifest},}sm_${cuda_architecture#sm_}"
    done
    cmake_args+=(
      -DUNIFIED_SUITE_BUILD_CUDA=ON
      -DCMAKE_CUDA_RUNTIME_LIBRARY=Static
      "-DCMAKE_CUDA_ARCHITECTURES=${cuda_architectures}"
    )
    ;;
  hip)
    hip_architectures="${UNIFIED_SUITE_HIP_ARCHITECTURES:-${GEMSTONE_AMDGPU_TARGETS:-gfx900}}"
    hip_architectures="${hip_architectures//,/;}"
    target_arch_manifest="${hip_architectures%%[;,]*}"
    target_archs_manifest="${hip_architectures//;/,}"
    cmake_args+=(
      -DUNIFIED_SUITE_BUILD_HIP=ON
      -DCMAKE_HIP_COMPILER=/opt/rocm/llvm/bin/clang++
      "-DCMAKE_HIP_ARCHITECTURES=${hip_architectures}"
    )
    ;;
  opencl)
    cmake_args+=(-DUNIFIED_SUITE_BUILD_OPENCL=ON)
    ;;
  hsa)
    hsa_targets="${GEMSTONE_AMDGPU_TARGETS:-gfx900}"
    target_arch_manifest="${UNIFIED_SUITE_HSA_ARCHITECTURE:-${GEMSTONE_AMDGPU_ORACLE_TARGET:-${hsa_targets%%[;,]*}}}"
    target_archs_manifest="$target_arch_manifest"
    cmake_args+=(-DUNIFIED_SUITE_BUILD_HSA=ON "-DUNIFIED_SUITE_HSA_ARCHITECTURE=$target_arch_manifest")
    ;;
  sycl)
    cmake_args+=(
      -DUNIFIED_SUITE_BUILD_SYCL=ON
      -DCMAKE_CXX_COMPILER=/opt/intel/oneapi/compiler/latest/bin/icpx
    )
    ;;
esac

if [[ "$backend" == hip ]]; then
  # The runner's HIP targets are also needed when selecting the matching
  # rocBLAS/Tensile data below.  Pass them as data rather than relying on the
  # host environment being visible inside the build container.
  container_environment_args+=(--env "GEMSTONE_UNIFIED_HIP_ARCHITECTURES=$hip_architectures")
fi

# Keep the wrapper's common defaults in one place while allowing the complete
# CTest matrix to be changed without reintroducing a host-side CMake configure.
# Values are passed as data (one cache variable per environment variable), not
# through eval or a shell command string.

for cache_variable in \
  UNIFIED_SUITE_CTEST_TIMEOUT \
  UNIFIED_SUITE_CTEST_PROFILE \
  UNIFIED_SUITE_ALGORITHM_SIZES \
  UNIFIED_SUITE_BLAS_SIZES \
  UNIFIED_SUITE_EDGE_CASE_SIZES \
  UNIFIED_SUITE_EDGE_BLAS_SIZES \
  UNIFIED_SUITE_STRESS_SIZES \
  UNIFIED_SUITE_STRESS_ITERATIONS \
  UNIFIED_SUITE_MATRIX_SHAPES \
  UNIFIED_SUITE_ALGORITHM_TYPES \
  UNIFIED_SUITE_BLAS_TYPES \
  UNIFIED_SUITE_EDGE_ALGORITHM_TYPES \
  UNIFIED_SUITE_EDGE_BLAS_TYPES \
  UNIFIED_SUITE_STRESS_TYPES \
  UNIFIED_SUITE_SYCL_ENABLE_FP64_EMULATION; do
  if [[ -n "${!cache_variable:-}" ]]; then
    cmake_args+=("-D${cache_variable}=${!cache_variable}")
  fi
done

bundle_runtime="${GEMSTONE_UNIFIED_BUNDLE_RUNTIME:-1}"
run_ctest="${GEMSTONE_UNIFIED_CONTAINER_RUN_CTEST:-0}"
echo "building $backend in $image" >&2
"$engine" run --rm -i --name "$container_name" \
  "${container_userns_args[@]}" \
  "${container_cgroup_args[@]}" \
  --user "$builder_uid:$builder_gid" \
  --security-opt label=disable \
  --network "$container_network" \
  "${container_environment_args[@]}" \
  "${container_device_args[@]}" \
  --mount "type=bind,src=$script_directory/../../tools,dst=/gemstone-tools,ro" \
  --mount "type=bind,src=$source_directory,dst=/src,ro" \
  --mount "type=bind,src=$output_directory,dst=/out,rw" \
  "$builder_image" \
  /bin/bash -s -- "$backend" "$bundle_runtime" "$run_ctest" "${cmake_args[@]}" <<'CONTAINER_SCRIPT'
set -euo pipefail

backend="$1"
bundle_runtime="$2"
run_ctest="$3"
shift 3

rm -rf /work/build
if [[ "$backend" == sycl && -f /opt/intel/oneapi/setvars.sh ]]; then
  # Some official oneAPI images export these variables already; sourcing the
  # setup script also makes an approved minimal image override behave the same
  # way without relying on a host installation.  setvars returns a non-zero
  # status when an image has already initialized its environment, which is
  # harmless because the image's exported values are still valid.
  source /opt/intel/oneapi/setvars.sh >/dev/null 2>&1 || true
fi
cmake -S /src -B /work/build -G Ninja "$@"
cmake --build /work/build --parallel "$CMAKE_BUILD_PARALLEL_LEVEL"
if [[ "$run_ctest" == 1 ]]; then
  ctest --test-dir /work/build --output-on-failure --parallel 1
fi
cmake --install /work/build --prefix /out
ctest --test-dir /work/build --show-only=json-v1 > /out/ctest-inventory.json

if [[ "$bundle_runtime" == 1 && "$backend" != host ]]; then
  mkdir -p /out/lib

  if [[ "$backend" == hip && -d /opt/rocm/lib/rocblas/library ]]; then
    # rocBLAS loads Tensile data at runtime rather than listing it in DT_NEEDED.
    # A relocatable runner therefore needs the complete library directory next
    # to the copied rocBLAS shared object.  In particular, the lazy metadata
    # names hundreds of individual .dat/.co/.hsaco files which cannot be found
    # through DT_NEEDED and must not be left in /opt/rocm.
    rocblas_library=/opt/rocm/lib/rocblas/library
    bundled_rocblas_library=/out/lib/rocblas/library
    mkdir -p "$bundled_rocblas_library"
    cp -a -- "$rocblas_library"/. "$bundled_rocblas_library/"
    rocblas_target_for() {
      case "$1" in
        gfx103[0-9]) printf '%s\n' gfx1030 ;;
        gfx110[0-9]) printf '%s\n' gfx1100 ;;
        gfx115[01]) printf '%s\n' gfx1150 ;;
        gfx120[01]) printf '%s\n' gfx1200 ;;
        gfx940|gfx941) printf '%s\n' gfx942 ;;
        *) printf '%s\n' "$1" ;;
      esac
    }
    IFS=',;' read -r -a hip_target_list <<<"$GEMSTONE_UNIFIED_HIP_ARCHITECTURES"
    for hip_target in "${hip_target_list[@]}"; do
      rocblas_target="$(rocblas_target_for "$hip_target")"
      if [[ "$rocblas_target" != "$hip_target" ]]; then
        for suffix in dat hsaco; do
          source="$bundled_rocblas_library/TensileLibrary_lazy_${rocblas_target}.dat"
          target="$bundled_rocblas_library/TensileLibrary_lazy_${hip_target}.dat"
          if [[ "$suffix" == hsaco ]]; then
            source="$bundled_rocblas_library/Kernels.so-000-${rocblas_target}.hsaco"
            target="$bundled_rocblas_library/Kernels.so-000-${hip_target}.hsaco"
          fi
          [[ -e "$source" && ! -e "$target" ]] && ln -s -- "$(basename -- "$source")" "$target"
        done

        # Tensile's lazy metadata names the per-operation files with the
        # canonical architecture (for example, gfx1036), while ROCm ships the
        # same ISA-compatible kernels under the first architecture in the
        # family (gfx1030).  Relocation must provide those names as well as the
        # top-level lazy database; otherwise rocBLAS silently reports an invalid
        # device function after failing to open every selected kernel.
        while IFS= read -r -d '' source; do
          base="$(basename -- "$source")"
          target_base="${base//${rocblas_target}/${hip_target}}"
          target="$bundled_rocblas_library/$target_base"
          [[ "$target_base" != "$base" && ! -e "$target" ]] && ln -s -- "$base" "$target"
        done < <(find "$bundled_rocblas_library" -maxdepth 1 -type f -name "*${rocblas_target}*" -print0)
      fi
    done
  fi
  if [[ "$backend" == hip && -f /usr/share/libdrm/amdgpu.ids ]]; then
    # libdrm uses this ASIC table at runtime.  Newer libdrm can be pointed at
    # the relocated copy with AMDGPU_ASIC_ID_TABLE_PATHS (see run-relocated.sh).
    mkdir -p /out/share/libdrm
    cp -a -- /usr/share/libdrm/amdgpu.ids /out/share/libdrm/
  fi

  source /gemstone-tools/runtime-bundle.sh
  bundle_begin /out 1
  if [[ "$backend" == hip || "$backend" == hsa ]]; then
    # ROCr discovers these components with dlopen rather than DT_NEEDED.  Seed
    # the closure explicitly so a relocated run never falls back to /opt/rocm.
    for plugin in \
      /opt/rocm/lib/libhsa-amd-aqlprofile64.so* \
      /opt/rocm/lib/librocprofiler*.so* \
      /opt/rocm/lib/libroctx*.so*; do
      [[ -e "$plugin" ]] || continue
      [[ "$(basename -- "$plugin")" =~ \.so(\.[0-9]+)*$ ]] || continue
      bundle_library "$plugin"
    done
  elif [[ "$backend" == sycl ]]; then
    # UR/SYCL backend plugins are also dlopen'ed rather than listed in the
    # runner's DT_NEEDED entries.
    for plugin in \
      /opt/intel/oneapi/compiler/*/lib/libur_adapter*.so* \
      /opt/intel/oneapi/compiler/*/lib/libOpenCL.so* \
      /opt/intel/oneapi/compiler/*/lib/libintelocl.so* \
      /opt/intel/oneapi/compiler/*/lib/libsycl-jit.so* \
      /opt/intel/oneapi/compiler/*/lib/libsycl-preview.so* \
      /opt/intel/oneapi/compiler/*/lib/libomptarget*.so* \
      /usr/lib/x86_64-linux-gnu/libze_loader.so* \
      /usr/lib64/libze_loader.so*; do
      [[ -e "$plugin" ]] || continue
      [[ "$(basename -- "$plugin")" =~ \.so(\.[0-9]+)*$ ]] || continue
      bundle_library "$plugin"
    done
  fi
  bundle_resolve

fi

source /gemstone-tools/runtime-bundle.sh
bundle_relocate /out
CONTAINER_SCRIPT

commit="unknown"
if git -C "$repository_directory" rev-parse --verify HEAD >/dev/null 2>&1; then
  commit="$(git -C "$repository_directory" rev-parse HEAD)"
fi
if [[ ! -x "$output_directory/bin/unified_suite_${backend}" ]]; then
  echo "container build did not produce its installed runner" >&2
  exit 1
fi
{
  printf 'format\t1\n'
  printf 'backend\t%s\n' "$backend"
  printf 'build-image\t%s\n' "$image"
  printf 'source-commit\t%s\n' "$commit"
  printf 'runtime-libraries\t%s\n' "$bundle_runtime"
  printf 'host-requirements\tlibc-cxx-abi-and-vendor-kernel-driver\n'
  if [[ -n "$target_arch_manifest" ]]; then
    printf 'target-arch\t%s\n' "$target_arch_manifest"
  fi
  if [[ -n "$target_archs_manifest" ]]; then
    printf 'target-archs\t%s\n' "$target_archs_manifest"
  fi
  while IFS= read -r runner; do
    printf 'runner\t%s\n' "${runner#"$output_directory/"}"
  done < <(find "$output_directory/bin" -maxdepth 1 -type f -perm -u+x -name "unified_suite_${backend}*" | sort)
} >"$output_directory/manifest.tsv"

echo "relocated artifact: $output_directory" >&2
find "$output_directory" -maxdepth 2 -type f -printf '%P\n' | sort >&2

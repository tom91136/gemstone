#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat >&2 <<'EOF'
usage: container-build.sh <host|cuda|hip|sycl> [output-directory]

Build the unified suite in a pinned vendor container and install a relocatable
artifact on the host.  The output directory must be empty or absent.

Images can be overridden with GEMSTONE_UNIFIED_<BACKEND>_IMAGE.  The default
engine is podman (set GEMSTONE_UNIFIED_CONTAINER_ENGINE to use docker).
Set GEMSTONE_UNIFIED_CONTAINER_RUN_CTEST=1 to run CTest in the build container.
Set GEMSTONE_UNIFIED_BUNDLE_RUNTIME=0 to omit vendor user-space libraries.
Set GEMSTONE_UNIFIED_CONTAINER_DEVICES to a comma-separated list of Podman/
Docker --device values when running CTest against a physical GPU.
UNIFIED_SUITE_* cache variables are forwarded to the container configure.
UNIFIED_SUITE_CUDA_ARCHITECTURES and UNIFIED_SUITE_HIP_ARCHITECTURES can pin
the device code targets as a comma- or semicolon-separated list (defaults are
CUDA `75` and AMDGPU `gfx900`, so a build does not depend on GPU discovery on
the host).
The build/run container has no network by default; set
GEMSTONE_UNIFIED_CONTAINER_NETWORK to an engine-supported network mode only
if a custom SDK requires it.
EOF
  exit 2
}

[[ $# -ge 1 && $# -le 2 ]] || usage
backend="$1"
case "$backend" in
  host)
    # Ubuntu 24.04 base, digest-pinned.
    default_image="docker.io/library/ubuntu@sha256:1e0a86e57d247923571b75e0aaf48a1449cf8c543d51fb3e07a4a7d7bfa79316"
    ;;
  cuda)
    # NVIDIA CUDA 13.3 devel, Ubuntu 24.04, digest-pinned.
    default_image="docker.io/nvidia/cuda@sha256:69e9e39eb8fe2cda271654a0f5eac2f1bb946b2fb9c460eb19c7c3c155f4e64e"
    ;;
  hip)
    # ROCm 7.2.4 development image, Ubuntu 24.04, digest-pinned.
    default_image="docker.io/rocm/dev-ubuntu-24.04@sha256:92f309c51b52cef8762867848f1529dee821624f23cd5df38455e819538f762f"
    ;;
  sycl)
    # Intel oneAPI toolkit 2026.1 image, Ubuntu 24.04, digest-pinned.
    default_image="docker.io/intel/oneapi-toolkit@sha256:e9db518398753434ee5aab9740a25f1d3134396a30be1569cfad8f8b0d90740c"
    ;;
  *)
    echo "unsupported backend: $backend" >&2
    usage
    ;;
esac

script_directory="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"
source_directory="$(cd -- "$script_directory/.." && pwd -P)"
repository_directory="$(cd -- "$source_directory/../.." && pwd -P)"
output_directory="${2:-${GEMSTONE_UNIFIED_OUTPUT:-$repository_directory/target/unified-artifacts/$backend}}"
output_directory="$(mkdir -p -- "$output_directory" && cd -- "$output_directory" && pwd -P)"

if [[ -n "$(find "$output_directory" -mindepth 1 -maxdepth 1 -print -quit)" ]]; then
  echo "output directory is not empty: $output_directory" >&2
  echo "remove it explicitly or choose another output directory" >&2
  exit 1
fi

engine="${GEMSTONE_UNIFIED_CONTAINER_ENGINE:-podman}"
command -v "$engine" >/dev/null || {
  echo "container engine not found: $engine" >&2
  exit 1
}

image_variable="GEMSTONE_UNIFIED_${backend^^}_IMAGE"
image="${!image_variable:-$default_image}"
image_is_present=1
if [[ "$engine" == podman ]]; then
  "$engine" image exists "$image" >/dev/null 2>&1 || image_is_present=0
else
  "$engine" image inspect "$image" >/dev/null 2>&1 || image_is_present=0
fi
if [[ "$image_is_present" == 0 ]]; then
  echo "pulling $image" >&2
  "$engine" pull "$image"
fi

builder_image="localhost/gemstone-unified-builder:${backend}"
builder_uid="$(id -u)"
builder_gid="$(id -g)"
build_pull_args=()
if [[ "$engine" == podman ]]; then
  build_pull_args+=(--pull=never)
else
  build_pull_args+=(--pull=false)
fi
"$engine" build \
  "${build_pull_args[@]}" \
  --build-arg "BASE_IMAGE=$image" \
  --build-arg "BUILDER_UID=$builder_uid" \
  --build-arg "BUILDER_GID=$builder_gid" \
  --tag "$builder_image" \
  "$script_directory/../containers"

cmake_args=(
  -DCMAKE_BUILD_TYPE=Release
  -DCMAKE_INSTALL_PREFIX=/out
  -DCMAKE_INSTALL_LIBDIR=lib
  -DUNIFIED_SUITE_BUILD_HOST=OFF
  -DUNIFIED_SUITE_BUILD_CUDA=OFF
  -DUNIFIED_SUITE_BUILD_HIP=OFF
  -DUNIFIED_SUITE_BUILD_SYCL=OFF
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
    cuda_architectures="${UNIFIED_SUITE_CUDA_ARCHITECTURES:-75}"
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
    hip_architectures="${UNIFIED_SUITE_HIP_ARCHITECTURES:-gfx900}"
    target_arch_manifest="${hip_architectures%%[;,]*}"
    target_archs_manifest="${hip_architectures//;/,}"
    cmake_args+=(
      -DUNIFIED_SUITE_BUILD_HIP=ON
      -DCMAKE_HIP_COMPILER=/opt/rocm/llvm/bin/clang++
      "-DCMAKE_HIP_ARCHITECTURES=${hip_architectures}"
    )
    ;;
  sycl)
    cmake_args+=(
      -DUNIFIED_SUITE_BUILD_SYCL=ON
      -DCMAKE_CXX_COMPILER=/opt/intel/oneapi/compiler/latest/bin/icpx
    )
    ;;
esac

container_environment_args=()
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
  UNIFIED_SUITE_CTEST_SHARDS \
  UNIFIED_SUITE_CTEST_TIMEOUT \
  UNIFIED_SUITE_CTEST_PROFILE \
  UNIFIED_SUITE_ALGORITHM_SIZES \
  UNIFIED_SUITE_SYCL_ALGORITHM_SIZES \
  UNIFIED_SUITE_BLAS_SIZES \
  UNIFIED_SUITE_EDGE_CASE_SIZES \
  UNIFIED_SUITE_EDGE_BLAS_SIZES \
  UNIFIED_SUITE_SYCL_EDGE_BLAS_SIZES \
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
container_userns_args=()
if [[ "$engine" == podman ]]; then
  container_userns_args+=(--userns=keep-id)
fi
container_device_args=()
if [[ -n "${GEMSTONE_UNIFIED_CONTAINER_DEVICES:-}" ]]; then
  IFS=',' read -r -a configured_devices <<<"$GEMSTONE_UNIFIED_CONTAINER_DEVICES"
  for device in "${configured_devices[@]}"; do
    [[ -n "$device" ]] && container_device_args+=(--device "$device")
  done
fi
container_network="${GEMSTONE_UNIFIED_CONTAINER_NETWORK:-none}"

echo "building $backend in $image" >&2
"$engine" run --rm -i \
  "${container_userns_args[@]}" \
  --user "$builder_uid:$builder_gid" \
  --security-opt label=disable \
  --network "$container_network" \
  "${container_environment_args[@]}" \
  "${container_device_args[@]}" \
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
cmake --build /work/build --parallel
if [[ "$run_ctest" == 1 ]]; then
  ctest --test-dir /work/build --output-on-failure --parallel
fi
cmake --install /work/build --prefix /out

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

  is_host_abi_library() {
    local name="$(basename -- "$1")"
    case "$name" in
      libc.so*|ld-linux*.so*|libm.so*|libpthread.so*|libdl.so*|librt.so*|libresolv.so*|libutil.so*|libcrypt.so*|libnss_*.so*)
        return 0
        ;;
      # CUDA and Level Zero driver entry points are supplied by the host.  They
      # are ABI boundaries, rather than SDK user-space libraries.
      libcuda.so*|libnvidia-*.so*|libze_intel_gpu.so*)
        return 0
        ;;
      *)
        return 1
        ;;
    esac
  }

  # Resolve the complete ELF closure, not just libraries whose names look
  # vendor-specific.  Ordinary userspace dependencies (zstd, libelf, libdrm,
  # libnuma, ...) are part of the artifact; only the host ABI/driver allow-list
  # above remains external.
  is_bundle_library() {
    local path="$1"
    [[ "$path" == /opt/* || "$path" == /usr/local/cuda/* || "$path" == /usr/local/cuda-*/* || "$path" == /usr/lib/* || "$path" == /usr/lib64/* || "$path" == /lib/* || "$path" == /lib64/* ]] || return 1
    is_host_abi_library "$path" && return 1
    return 0
  }

  pending=()
  declare -A seen=()
  bundle_library() {
    local dependency="$1"
    local real="$(readlink -f -- "$dependency")"
    [[ -f "$real" ]] || return 0
    local real_name="$(basename -- "$real")"
    if [[ -z "${seen[$real]+present}" ]]; then
      seen["$real"]=1
      cp -a -- "$real" "/out/lib/$real_name"
      pending+=("$real")
    fi
    if command -v readelf >/dev/null; then
      local soname
      soname="$(readelf -d "$real" 2>/dev/null | sed -n 's/.*(SONAME).*\[\([^]]*\)\].*/\1/p' | head -1)"
      if [[ -n "$soname" && "$soname" != "$real_name" ]]; then
        ln -sfn -- "$real_name" "/out/lib/$soname"
      fi
    fi
    local dependency_name="$(basename -- "$dependency")"
    if [[ "$dependency_name" != "$real_name" ]]; then
      ln -sfn -- "$real_name" "/out/lib/$dependency_name"
    fi
  }

  for executable in /out/bin/*; do
    [[ -x "$executable" && ! -d "$executable" ]] && pending+=("$executable")
  done
  if [[ "$backend" == hip ]]; then
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
  for pass in $(seq 1 32); do
    current=("${pending[@]}")
    pending=()
    for file in "${current[@]}"; do
      [[ -f "$file" ]] || continue
      dependency_output="$(ldd "$file" 2>&1)"
      while IFS= read -r unresolved; do
        [[ -n "$unresolved" ]] || continue
        unresolved_name="${unresolved%% *}"
        # A build container normally has no physical GPU, so its ldd output
        # may legitimately omit the host driver ABI.  Every other unresolved
        # dependency is a packaging error and must stop the build.
        is_host_abi_library "$unresolved_name" && continue
        echo "unresolved runtime dependency in $file: $unresolved" >&2
        exit 1
      done < <(grep 'not found' <<<"$dependency_output" || true)
      while IFS= read -r dependency; do
        [[ -n "$dependency" ]] || continue
        is_bundle_library "$dependency" || continue
        bundle_library "$dependency"
      done < <(sed -nE 's/.*=> (\/[^ ]+).*/\1/p' <<<"$dependency_output")
    done
    ((${#pending[@]} == 0)) && break
  done

  # Every copied shared object is flat in /out/lib.  Rewrite its search path
  # to that directory so no build-image /opt or /work path survives relocation.
  if command -v patchelf >/dev/null; then
    for shared_object in /out/lib/*; do
      [[ -f "$shared_object" ]] || continue
      file -b "$shared_object" | grep -q '^ELF ' || continue
      patchelf --set-rpath '$ORIGIN' "$shared_object"
    done
  fi
fi

for executable in /out/bin/*; do
  [[ -x "$executable" && ! -d "$executable" ]] || continue
  # Vendor compiler drivers commonly append their implicit library directory
  # to the install RPATH.  That directory is inside the build image and would
  # make the artifact accidentally container-dependent, even when its library
  # was copied above.  Keep only the relative bundle path.
  if command -v patchelf >/dev/null; then
    patchelf --set-rpath '$ORIGIN/../lib' "$executable"
  fi
  if command -v readelf >/dev/null &&
      readelf -d "$executable" | grep -Eq '/(src|work|opt/rocm|opt/intel|usr/local/cuda[^/]*)/'; then
    echo "non-relocatable path embedded in $executable" >&2
    exit 1
  fi
done

if command -v readelf >/dev/null; then
  for shared_object in /out/lib/*; do
    [[ -f "$shared_object" ]] || continue
    file -b "$shared_object" | grep -q '^ELF ' || continue
    if readelf -d "$shared_object" | grep -Eq '/(src|work|opt/rocm|opt/intel|usr/local/cuda[^/]*)/'; then
      echo "non-relocatable path embedded in $shared_object" >&2
      exit 1
    fi
  done
fi
CONTAINER_SCRIPT

commit="unknown"
if git -C "$repository_directory" rev-parse --verify HEAD >/dev/null 2>&1; then
  commit="$(git -C "$repository_directory" rev-parse HEAD)"
fi
if [[ ! -x "$output_directory/bin/unified_suite_${backend}" ]]; then
  echo "container build did not produce the installed runner" >&2
  exit 1
fi
{
  printf 'format\t1\n'
  printf 'backend\t%s\n' "$backend"
  printf 'build-image\t%s\n' "$image"
  printf 'source-commit\t%s\n' "$commit"
  printf 'runtime-libraries\t%s\n' "$bundle_runtime"
  printf 'host-requirements\tlibc-loader-and-vendor-kernel-driver\n'
  if [[ -n "$target_arch_manifest" ]]; then
    printf 'target-arch\t%s\n' "$target_arch_manifest"
  fi
  if [[ -n "$target_archs_manifest" ]]; then
    printf 'target-archs\t%s\n' "$target_archs_manifest"
  fi
  printf 'runner\tbin/unified_suite_%s\n' "$backend"
} >"$output_directory/manifest.tsv"

echo "relocated artifact: $output_directory" >&2
find "$output_directory" -maxdepth 2 -type f -printf '%P\n' | sort >&2

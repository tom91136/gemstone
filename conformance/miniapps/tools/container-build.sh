#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat >&2 <<'EOF'
usage: container-build.sh <cuda|hip|opencl|sycl|openacc> <source-root> <output-directory> <app:model>...

Build the requested upstream mini-app models in a pinned vendor container and
install a relocatable artifact.  The source root is mounted read-only, while
the compiler, SDK, configure tree, and build tree remain inside the container.
EOF
  exit 2
}

[[ $# -ge 4 ]] || usage
backend="$1"
source_directory="$(cd -- "$2" && pwd -P)"
output_directory="$(mkdir -p -- "$3" && cd -- "$3" && pwd -P)"
shift 3

case "$backend" in
  cuda)
    default_image="docker.io/nvidia/cuda@sha256:69e9e39eb8fe2cda271654a0f5eac2f1bb946b2fb9c460eb19c7c3c155f4e64e"
    ;;
  hip)
    default_image="docker.io/rocm/dev-ubuntu-24.04@sha256:92f309c51b52cef8762867848f1529dee821624f23cd5df38455e819538f762f"
    ;;
  opencl)
    default_image="docker.io/library/ubuntu@sha256:1e0a86e57d247923571b75e0aaf48a1449cf8c543d51fb3e07a4a7d7bfa79316"
    ;;
  sycl)
    default_image="docker.io/intel/oneapi-toolkit@sha256:e9db518398753434ee5aab9740a25f1d3134396a30be1569cfad8f8b0d90740c"
    ;;
  openacc)
    # NVIDIA's HPC SDK image is intentionally supplied by the caller. There
    # is no portable OpenACC base image, and silently selecting a different
    # compiler would change the port being tested.
    default_image=""
    ;;
  *)
    echo "unsupported backend: $backend" >&2
    usage
    ;;
esac

for specification in "$@"; do
  case "$specification" in
    babelstream:cuda|babelstream:hip|babelstream:ocl|babelstream:sycl|\
    babelstream:omp-target|babelstream:kokkos|babelstream:acc|babelstream:std-indices|\
    minibude:cuda|minibude:hip|minibude:ocl|minibude:sycl|\
    minibude:omp-target|minibude:kokkos|minibude:acc|minibude:std-indices|\
    cloverleaf:cuda|cloverleaf:hip|cloverleaf:sycl-acc|cloverleaf:sycl-usm|\
    cloverleaf:omp-target|cloverleaf:kokkos|cloverleaf:acc|cloverleaf:std-indices|\
    tealeaf:cuda|tealeaf:hip|tealeaf:sycl-acc|tealeaf:sycl-usm|\
    tealeaf:omp-target|tealeaf:kokkos|tealeaf:std-indices)
      ;;
    *)
      echo "unsupported mini-app model: $specification" >&2
      exit 2
      ;;
  esac
done

script_directory="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"
engine="${GEMSTONE_MINIAPP_CONTAINER_ENGINE:-${GEMSTONE_UNIFIED_CONTAINER_ENGINE:-podman}}"
command -v "$engine" >/dev/null || {
  echo "container engine not found: $engine" >&2
  exit 1
}

image_variable="GEMSTONE_MINIAPP_${backend^^}_IMAGE"
image="${!image_variable:-$default_image}"
if [[ -z "$image" ]]; then
  echo "OpenACC requires GEMSTONE_MINIAPP_OPENACC_IMAGE with an approved NVHPC base image" >&2
  exit 2
fi
if [[ "$backend" == opencl && -z "${!image_variable:-}" ]]; then
  image="${GEMSTONE_UNIFIED_HOST_IMAGE:-$image}"
fi

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

builder_image="localhost/gemstone-miniapps-builder:${backend}"
builder_uid="$(id -u)"
builder_gid="$(id -g)"
build_pull_args=()
if [[ "$engine" == podman ]]; then
  build_pull_args+=(--pull=never)
else
  build_pull_args+=(--pull=false)
fi
install_opencl=0
[[ "$backend" == opencl ]] && install_opencl=1
"$engine" build \
  "${build_pull_args[@]}" \
  --build-arg "BASE_IMAGE=$image" \
  --build-arg "BUILDER_UID=$builder_uid" \
  --build-arg "BUILDER_GID=$builder_gid" \
  --build-arg "INSTALL_OPENCL=$install_opencl" \
  --tag "$builder_image" \
  "$script_directory/../containers"

cuda_architecture="${GEMSTONE_MINIAPP_CUDA_ARCH:-sm_75}"
hip_architectures="${GEMSTONE_MINIAPP_HIP_ARCHITECTURES:-gfx900}"
container_network="${GEMSTONE_MINIAPP_CONTAINER_NETWORK:-none}"
container_userns_args=()
if [[ "$engine" == podman ]]; then
  container_userns_args+=(--userns=keep-id)
fi
container_device_args=()
if [[ -n "${GEMSTONE_MINIAPP_CONTAINER_DEVICES:-}" ]]; then
  IFS=',' read -r -a configured_devices <<<"$GEMSTONE_MINIAPP_CONTAINER_DEVICES"
  for device in "${configured_devices[@]}"; do
    [[ -n "$device" ]] && container_device_args+=(--device "$device")
  done
fi
container_environment_args=()
if [[ -n "${GEMSTONE_MINIAPP_BUILD_JOBS:-}" ]]; then
  container_environment_args+=(--env GEMSTONE_MINIAPP_BUILD_JOBS)
fi

echo "building mini-apps ($backend) in $image" >&2
"$engine" run --rm -i \
  "${container_userns_args[@]}" \
  --user "$builder_uid:$builder_gid" \
  --security-opt label=disable \
  --network "$container_network" \
  "${container_device_args[@]}" \
  "${container_environment_args[@]}" \
  --mount "type=bind,src=$source_directory,dst=/src,ro" \
  --mount "type=bind,src=$output_directory,dst=/out,rw" \
  "$builder_image" \
  /bin/bash -s -- "$backend" "$cuda_architecture" "$hip_architectures" "$@" <<'CONTAINER_SCRIPT'
set -euo pipefail

backend="$1"
cuda_architecture="$2"
hip_architectures="$3"
shift 3

[[ "$cuda_architecture" == sm_* ]] || cuda_architecture="sm_$cuda_architecture"
hip_extra_flags="-O2"
IFS=',;' read -r -a configured_hip_architectures <<<"$hip_architectures"
hip_target_architecture=""
kokkos_arch_options=()
clang_rt_builtins=""
if [[ "$backend" == hip ]]; then
  clang_rt_builtins="$(find -L /opt/rocm/llvm/lib/clang -type f -name 'libclang_rt.builtins-x86_64.a' -print -quit)"
fi
for hip_architecture in "${configured_hip_architectures[@]}"; do
  [[ -n "$hip_architecture" ]] || continue
  [[ -n "$hip_target_architecture" ]] || hip_target_architecture="$hip_architecture"
  hip_extra_flags+=" --offload-arch=$hip_architecture"
  case "$hip_architecture" in
    gfx90a) kokkos_arch_options+=( -DKokkos_ARCH_VEGA90A=ON ) ;;
    gfx908) kokkos_arch_options+=( -DKokkos_ARCH_VEGA908=ON ) ;;
    gfx906) kokkos_arch_options+=( -DKokkos_ARCH_VEGA906=ON ) ;;
    gfx1030|gfx1031|gfx1032|gfx1033|gfx1034|gfx1035|gfx1036) kokkos_arch_options+=( -DKokkos_ARCH_NAVI1030=ON ) ;;
    gfx1100|gfx1101|gfx1102|gfx1103) kokkos_arch_options+=( -DKokkos_ARCH_NAVI1100=ON ) ;;
    *)
      echo "Kokkos 4.0.01 has no architecture mapping for $hip_architecture" >&2
      exit 2
      ;;
  esac
done
hip_target_architecture="${hip_target_architecture:-gfx900}"
if ((${#kokkos_arch_options[@]} != 0)); then
  mapfile -t kokkos_arch_options < <(printf '%s\n' "${kokkos_arch_options[@]}" | sort -u)
fi

if [[ "$backend" == sycl && -f /opt/intel/oneapi/setvars.sh ]]; then
  source /opt/intel/oneapi/setvars.sh >/dev/null 2>&1 || true
fi

case "$backend" in
  cuda) host_cxx=/usr/bin/c++ ;;
  hip)
    host_cxx=/opt/rocm/bin/hipcc
    # Kokkos' ROCm TPL finder consumes ROCM_PATH from the environment when it
    # locates HIP, HSA, and clang_rt.builtins. Keep that path inside the
    # container so the build remains relocatable at install time.
    export ROCM_PATH=/opt/rocm
    ;;
  opencl) host_cxx=/usr/bin/c++ ;;
  sycl) host_cxx=/opt/intel/oneapi/compiler/latest/bin/icpx ;;
  openacc)
    host_cxx="$(command -v nvc++ || find /opt/nvidia/hpc_sdk/Linux_x86_64 -path '*/compilers/bin/nvc++' -type f -print -quit)"
    [[ -n "$host_cxx" ]] || {
      echo "OpenACC image has no nvc++ compiler" >&2
      exit 1
    }
    ;;
esac

if [[ -f /out/manifest.tsv ]]; then
  existing_backend="$(awk -F '\t' '$1 == "backend" { print $2; exit }' /out/manifest.tsv)"
  [[ "$existing_backend" == "$backend" ]] || {
    echo "artifact backend mismatch: $existing_backend (wanted $backend)" >&2
    exit 1
  }
else
  if [[ -n "$(find /out -mindepth 1 -maxdepth 1 -print -quit)" ]]; then
    echo "artifact directory has files but no manifest: /out" >&2
    exit 1
  fi
fi

mkdir -p /out/bin /out/lib /out/share/miniapps

models=("$@")
default_build_jobs="$(nproc)"
((default_build_jobs > 8)) && default_build_jobs=8
build_jobs="${GEMSTONE_MINIAPP_BUILD_JOBS:-$default_build_jobs}"
[[ "$build_jobs" =~ ^[1-9][0-9]*$ ]] || {
  echo "GEMSTONE_MINIAPP_BUILD_JOBS must be a positive integer: $build_jobs" >&2
  exit 2
}

build_one() {
  local specification="$1"
  local app model build_model suffix source kokkos_source build_source build install_root installed output model_host_cxx
  local hip_app_extra_flags
  local -a cmake_args
  app="${specification%%:*}"
  model="${specification#*:}"
  case "$backend:$model" in
    cuda:cuda|openacc:acc|hip:hip|hip:omp-target|hip:kokkos|hip:std-indices|opencl:ocl|sycl:sycl|sycl:sycl-acc|sycl:sycl-usm)
      ;;
    *)
      echo "$specification is not a $backend model" >&2
      exit 2
      ;;
  esac
  build_model="$model"
  case "$app:$model" in
    babelstream:omp-target|minibude:omp-target|cloverleaf:omp-target|tealeaf:omp-target)
      build_model=omp
      ;;
    babelstream:std-indices)
      build_model=std
      ;;
  esac
  case "$app:$model" in
    babelstream:cuda|babelstream:hip|babelstream:ocl|babelstream:sycl|\
    babelstream:omp-target|babelstream:kokkos|babelstream:acc|babelstream:std-indices)
      suffix=stream
      ;;
    minibude:cuda|minibude:hip|minibude:ocl|minibude:sycl|\
    minibude:omp-target|minibude:kokkos|minibude:acc|minibude:std-indices)
      suffix=bude
      ;;
    cloverleaf:cuda|cloverleaf:hip|cloverleaf:sycl-acc|cloverleaf:sycl-usm|\
    cloverleaf:omp-target|cloverleaf:kokkos|cloverleaf:acc|cloverleaf:std-indices)
      suffix=cloverleaf
      ;;
    tealeaf:cuda|tealeaf:hip|tealeaf:sycl-acc|tealeaf:sycl-usm|\
    tealeaf:omp-target|tealeaf:kokkos|tealeaf:std-indices)
      suffix=tealeaf
      ;;
    *)
      echo "unsupported mini-app model: $specification" >&2
      exit 2
      ;;
  esac
  source="/src/$app"
  if [[ "$build_model" != omp || "$model" != omp-target ]]; then
    [[ -d "$source/src/$build_model" ]] || {
      echo "$app does not provide src/$build_model for public model $model" >&2
      exit 1
    }
  else
    [[ -d "$source/src/omp" ]] || {
      echo "$app does not provide src/omp for OpenMP target model" >&2
      exit 1
    }
  fi
  if [[ "$model" == kokkos ]]; then
    [[ -d /src/kokkos ]] || {
      echo "Kokkos model requires /src/kokkos in the read-only source root" >&2
      exit 1
    }
    kokkos_source="/work/source/kokkos/$app-$model"
    rm -rf "$kokkos_source"
    mkdir -p "$kokkos_source"
    cp -a -- /src/kokkos/. "$kokkos_source/"
    # Kokkos 4.0.01 predates the ROCm 7 HIP property rename. This is a
    # reporting-only compatibility edit in the private container workspace;
    # the pinned dependency mounted at /src remains untouched.
    sed -i 's/hipProp\.gcnArch/hipProp.gcnArchName/g' "$kokkos_source/core/src/HIP/Kokkos_HIP_Instance.cpp"
  else
    kokkos_source=/src/kokkos
  fi
  if [[ "$model" == acc ]]; then
    [[ "$backend" == openacc ]] || {
      echo "OpenACC model must use the openacc backend" >&2
      exit 1
    }
  fi
  if [[ "$model" == omp-target || "$model" == kokkos || "$model" == std-indices ]]; then
    [[ "$backend" == hip ]] || {
      echo "AMD offload model $model must use the hip backend" >&2
      exit 1
    }
  fi
  if [[ "$model" == acc ]]; then
    [[ "$app" != tealeaf ]] || {
      echo "TeaLeaf has no OpenACC model at the pinned revision" >&2
      exit 1
    }
  fi
  if [[ "$model" == omp-target || "$model" == std-indices ]]; then
    [[ -n "${hip_architectures//[;,[:space:]]/}" ]] || {
      echo "an AMD target is required for $specification" >&2
      exit 1
    }
  fi
  build_source="$source"
  if [[ "$backend" == cuda && "$app" == babelstream && "$model" == cuda ]]; then
    # BabelStream's CUDA port unconditionally queries NVML for a memory clock.
    # NVML is a physical-driver management API, so it is unavailable when the
    # same relocatable binary is run through Gemstone. Build a private staged
    # copy with that reporting-only query replaced by cudaDeviceProp data; the
    # pinned upstream checkout itself remains read-only and unmodified.
    build_source="/work/source/$app/$model"
    rm -rf "$build_source"
    mkdir -p "$build_source"
    cp -a -- "$source/." "$build_source/"
    sed -i.bak \
      -e '/#include <nvml.h>/d' \
      -e '/^\[\[noreturn\]\] inline void nvml_error/,/^}$/d' \
      -e '/^#define NVML(EXPR)/d' \
      -e '/NVML(nvmlInit())/d' \
      -e 's/unsigned int memclock;/unsigned int memclock = 0;/' \
      -e '/char mybus\[16\];/d' \
      -e '/sprintf(&mybus\[0\]/d' \
      -e '/nvmlDevice_t nvmldev;/d' \
      -e '/NVML(nvmlDeviceGet/d' \
      "$build_source/src/cuda/CUDAStream.cu"
  elif [[ "$backend" == hip && "$app" == cloverleaf ]]; then
    # The pinned HIP port prints the architecture through the old gcnArch
    # field. ROCm 7 removed that field; gcnArchName is the portable HIP
    # property and is sufficient for this reporting-only line. Keep the
    # pinned checkout read-only and stage the compatibility-only edit.
    build_source="/work/source/$app/$model"
    rm -rf "$build_source"
    mkdir -p "$build_source"
    cp -a -- "$source/." "$build_source/"
    sed -i.bak \
      -e 's/<< "gfx" << props\.gcnArch << ")"/<< props.gcnArchName << ")"/' \
      "$build_source/src/hip/initialise.cpp"
  elif [[ "$backend" == hip && "$app" == babelstream && "$model" == std-indices ]]; then
    # ROCm's HIP StdPar Thrust implementation does not accept BabelStream's
    # C++20 ranges::iota iterator in transform_reduce. Reuse the existing
    # NVHPC counting-iterator workaround for HIP in a private compatibility
    # copy; the pinned checkout mounted at /src remains read-only.
    build_source="/work/source/$app/$model"
    rm -rf "$build_source"
    mkdir -p "$build_source"
    cp -a -- "$source/." "$build_source/"
    sed -i.bak \
      -e 's/#if defined(__NVCOMPILER) && defined(_NVHPC_STDPAR_GPU)/#if (defined(__NVCOMPILER) \&\& defined(_NVHPC_STDPAR_GPU)) || defined(__HIPCC__)/' \
      "$build_source/src/std/STDStream.cpp"
  elif [[ "$backend" == hip && "$app" == minibude && "$model" == omp-target ]]; then
    # Clang 22 rejects the redundant dependent-name `template` qualifier on
    # this concrete std::vector. Keep the upstream checkout read-only and
    # stage this compiler-compatibility edit privately.
    build_source="/work/source/$app/$model"
    rm -rf "$build_source"
    mkdir -p "$build_source"
    cp -a -- "$source/." "$build_source/"
    sed -i.bak 's/devices\.template emplace_back/devices.emplace_back/g' "$build_source/src/omp/fasten.hpp"
  fi
  build="/work/build/$app/$model"
  install_root="/work/install/$app/$model"
  installed="$install_root/bin/$build_model-$suffix"
  output="/out/bin/$app-$model-$suffix"
  if [[ -x "$output" ]]; then
    continue
  fi

  rm -rf "$build" "$install_root"
  cmake_args=(
    -S "$build_source"
    -B "$build"
    -G Ninja
    -DCMAKE_BUILD_TYPE=Release
    -DCMAKE_INSTALL_PREFIX="$install_root"
    -DMODEL="$build_model"
    -DUSAGE=OFF
    -DENABLE_MPI=OFF
    -DENABLE_PROFILING=OFF
    "-DCXX_EXTRA_FLAGS=-O2 -ffile-prefix-map=/src=. -fdebug-prefix-map=/src=."
  )
  if [[ "$app" == babelstream ]]; then
    cmake_args+=( -DBUILD_NATIVE=OFF )
  fi
  if [[ "$app" == minibude ]]; then
    cmake_args+=( -DPPWI=1 )
  fi
  if [[ "$app" == babelstream && "$model" == std-indices ]]; then
    cmake_args+=( -DSTDIMPL=INDICES )
  fi
  model_host_cxx="$host_cxx"
  case "$backend" in
    cuda)
      cmake_args+=(
        -DCMAKE_CXX_COMPILER="$model_host_cxx"
        -DCMAKE_CUDA_COMPILER=/usr/local/cuda/bin/nvcc
        -DCMAKE_CUDA_RUNTIME_LIBRARY=Static
        -DCUDA_ARCH="$cuda_architecture"
      )
      ;;
    hip)
      if [[ "$model" == omp-target ]]; then
        model_host_cxx=/usr/bin/amdclang++
        cmake_args+=(
          -DCMAKE_CXX_COMPILER="$model_host_cxx"
          "-DCXX_EXTRA_FLAGS=-O2 -ffile-prefix-map=/src=. -fdebug-prefix-map=/src=."
          "-DOFFLOAD=AMD:${hip_target_architecture}"
        )
      else
        hip_app_extra_flags="$hip_extra_flags"
        if [[ "$model" == std-indices ]]; then
          hip_app_extra_flags+=" --hipstdpar"
          # On non-gfx9 devices ROCm's StdPar implementation needs managed
          # allocation interposition for the host-visible algorithm buffers.
          # This mirrors BabelStream's AMDGPU_TARGET_OFFLOAD setup.
          [[ "$hip_target_architecture" == gfx9* ]] || hip_app_extra_flags+=" --hipstdpar-interpose-alloc"
        fi
        cmake_args+=(
          -DCMAKE_CXX_COMPILER="$model_host_cxx"
          "-DCXX_EXTRA_FLAGS=$hip_app_extra_flags"
          -DCXX_EXTRA_LIBRARIES=amdhip64
        )
      fi
      if [[ "$model" == kokkos ]]; then
        cmake_args+=(
          "-DKOKKOS_IN_TREE=$kokkos_source"
          -DKokkos_ENABLE_HIP=ON
          -DKokkos_ENABLE_HIP_RELOCATABLE_DEVICE_CODE=OFF
          "-DCMAKE_CXX_FLAGS=$hip_extra_flags"
          "${kokkos_arch_options[@]}"
        )
        [[ -n "$clang_rt_builtins" ]] || {
          echo "Kokkos build requires ROCm clang_rt.builtins" >&2
          exit 1
        }
        cmake_args+=(
          "-DCLANG_RT_LIBRARY=$clang_rt_builtins"
          "-DCMAKE_EXE_LINKER_FLAGS=-L/opt/rocm/lib"
        )
      fi
      ;;
    openacc)
      cmake_args+=(
        -DCMAKE_CXX_COMPILER="$model_host_cxx"
        -DTARGET_DEVICE=gpu
        "-DCUDA_ARCH=${cuda_architecture/sm_/cc}"
      )
      ;;
    opencl)
      cmake_args+=(
        -DOpenCL_LIBRARY=/usr/lib/x86_64-linux-gnu/libOpenCL.so.1
        -DFETCHCONTENT_SOURCE_DIR_OPENCL_HEADER=/usr
      )
      ;;
    sycl)
      cmake_args+=(
        -DCMAKE_CXX_COMPILER="$model_host_cxx"
        -DSYCL_COMPILER=ONEAPI-ICPX
        -DSYCL_COMPILER_DIR=/opt/intel/oneapi/compiler/latest
      )
      ;;
  esac
  cmake "${cmake_args[@]}"
  cmake --build "$build" --parallel "$build_jobs"
  cmake --install "$build" --prefix "$install_root"
  [[ -x "$installed" ]] || {
    echo "install did not produce $installed" >&2
    exit 1
  }
  cp -L -- "$installed" "$output"
  chmod 755 "$output"

}

for specification in "${models[@]}"; do
  build_one "$specification"
done

if [[ -d /src/minibude/data ]]; then
  mkdir -p /out/share/miniapps/minibude
  cp -a -- /src/minibude/data /out/share/miniapps/minibude/
fi

is_host_abi_library() {
  local name="$(basename -- "$1")"
  case "$name" in
    libc.so*|ld-linux*.so*|libm.so*|libpthread.so*|libdl.so*|librt.so*|libresolv.so*|libutil.so*|libcrypt.so*|libnss_*.so*|libstdc++.so*|libgcc_s.so*|libOpenCL.so*|libze_loader.so*)
      return 0
      ;;
    # Kernel-facing driver entry points, standardized compute loaders, and
    # host ABI libraries remain supplied by the host. Vendor user-space
    # runtimes are bundled below.
    libcuda.so*|libnvidia-*.so*|libze_intel_gpu.so*)
      return 0
      ;;
    *)
      return 1
      ;;
  esac
}

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
  local soname
  soname="$(readelf -d "$real" 2>/dev/null | sed -n 's/.*(SONAME).*\[\([^]]*\)\].*/\1/p' | head -1)"
  if [[ -n "$soname" && "$soname" != "$real_name" ]]; then
    ln -sfn -- "$real_name" "/out/lib/$soname"
  fi
  local dependency_name="$(basename -- "$dependency")"
  if [[ "$dependency_name" != "$real_name" ]]; then
    ln -sfn -- "$real_name" "/out/lib/$dependency_name"
  fi
}

for executable in /out/bin/*; do
  [[ -x "$executable" && ! -d "$executable" ]] && pending+=("$executable")
done

# HIP and SYCL load part of their user-space stack with dlopen. Seed those
# libraries so the installed artifact does not depend on /opt/rocm or /opt/intel.
  if [[ "$backend" == hip ]]; then
  for plugin in \
    /opt/rocm/lib/libamdhip64.so* \
    /opt/rocm/lib/libhsa-runtime64.so* \
    /opt/rocm/lib/libhsa-amd-aqlprofile64.so* \
    /opt/rocm/lib/llvm/lib/libomp.so* \
    /opt/rocm/lib/llvm/lib/libomptarget.so* \
    /opt/rocm/lib/llvm/lib/libomptarget.rtl.amdgpu.so*; do
    [[ -e "$plugin" ]] || continue
    [[ "$(basename -- "$plugin")" =~ \.so(\.[0-9]+)*$ ]] || continue
    bundle_library "$plugin"
  done
elif [[ "$backend" == sycl ]]; then
  for plugin in \
    /opt/intel/oneapi/compiler/*/lib/libsycl*.so* \
    /opt/intel/oneapi/compiler/*/lib/libur_loader.so* \
    /opt/intel/oneapi/compiler/*/lib/libur_adapter*.so*; do
    [[ -e "$plugin" ]] || continue
    [[ "$(basename -- "$plugin")" =~ \.so(\.[0-9]+)*$ ]] || continue
    bundle_library "$plugin"
  done
elif [[ "$backend" == openacc ]]; then
  for plugin in \
    /opt/nvidia/hpc_sdk/Linux_x86_64/*/compilers/lib/libacc*.so* \
    /opt/nvidia/hpc_sdk/Linux_x86_64/*/compilers/lib/libnv*.so* \
    /opt/nvidia/hpc_sdk/Linux_x86_64/*/compilers/lib/libpg*.so*; do
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
      IFS=$' \t' read -r unresolved_name _ <<<"$unresolved"
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
(( ${#pending[@]} == 0 )) || {
  echo "runtime dependency closure did not converge" >&2
  exit 1
}

for shared_object in /out/lib/*; do
  [[ -f "$shared_object" ]] || continue
  file -b "$shared_object" | grep -q '^ELF ' || continue
  patchelf --set-rpath '$ORIGIN' "$shared_object"
done

for executable in /out/bin/*; do
  [[ -x "$executable" && ! -d "$executable" ]] || continue
  patchelf --set-rpath '$ORIGIN/../lib' "$executable"
  if readelf -d "$executable" | grep -Eq '/(src|work|opt/rocm|opt/intel|usr/local/cuda[^/]*)/'; then
    echo "non-relocatable path embedded in $executable" >&2
    exit 1
  fi
done
for shared_object in /out/lib/*; do
  [[ -f "$shared_object" ]] || continue
  file -b "$shared_object" | grep -q '^ELF ' || continue
  if readelf -d "$shared_object" | grep -Eq '/(src|work|opt/rocm|opt/intel|usr/local/cuda[^/]*)/'; then
    echo "non-relocatable path embedded in $shared_object" >&2
    exit 1
  fi
done

{
  printf 'format\t1\n'
  printf 'backend\t%s\n' "$backend"
  printf 'cuda-arch\t%s\n' "$cuda_architecture"
  printf 'hip-architectures\t%s\n' "$hip_architectures"
  printf 'models\t%s\n' "$(printf '%s\n' "${models[@]}" | sort | paste -sd, -)"
  printf 'host-requirements\tlibc-loader-and-vendor-kernel-driver\n'
} > /out/manifest.tsv
CONTAINER_SCRIPT

include_guard(GLOBAL)
include("${CMAKE_CURRENT_LIST_DIR}/../cmake/ContainerBuild.cmake")

set(conformance_cuda_11_8_architectures sm_35 sm_50 sm_60 sm_70)
set(conformance_cuda_13_3_architectures sm_75 sm_80 sm_86 sm_87 sm_89 sm_90 sm_100 sm_103 sm_120)
set(conformance_cuda_11_8_image
  "docker.io/nvidia/cuda@sha256:94fd755736cb58979173d491504f0b573247b1745250249415b07fefc738e41f")
set(conformance_cuda_13_3_image
  "docker.io/nvidia/cuda@sha256:a535b42e387b5ea69f3be73e14e79eb979913b622bc61b21c8000830b4965ea4")

function(conformance_cuda_toolchain architecture image_output package_output support_output)
  if(architecture IN_LIST conformance_cuda_11_8_architectures)
    set(image "${conformance_cuda_11_8_image}")
    set(package "11-8")
    set(support "support-11.8")
  elseif(architecture IN_LIST conformance_cuda_13_3_architectures)
    set(image "${conformance_cuda_13_3_image}")
    set(package "13-3")
    set(support "support-13.3")
  else()
    message(FATAL_ERROR "No CUDA toolchain supports architecture ${architecture}")
  endif()
  set(${image_output} "${image}" PARENT_SCOPE)
  set(${package_output} "${package}" PARENT_SCOPE)
  set(${support_output} "${support}" PARENT_SCOPE)
endfunction()

# Compiler variants retain the runtime API backend and have independent artifacts.
set(conformance_clang_version 22)
set(conformance_gcc_version 14)
set(conformance_gcc_runtime_version 14.2.0)
set(conformance_gcc_runtime_sha256 a7b39bc69cbf9e25826c5a60ab26477001f7c08d85cec04bc0e29cabed6f3cc9)
set(conformance_gcc_cuda_architectures sm_35 sm_70 sm_75 sm_80)
set(conformance_gcc_hsa_architectures gfx900 gfx906 gfx908 gfx90a gfx90c gfx1030 gfx1036 gfx1100 gfx1103)

function(conformance_compiler_supported backend architecture compiler output)
  set(supported FALSE)
  if(compiler STREQUAL "vendor")
    set(supported TRUE)
  elseif(compiler STREQUAL "clang" AND backend STREQUAL "cuda")
    set(supported TRUE)
  elseif(compiler STREQUAL "gcc" AND architecture IN_LIST conformance_gcc_${backend}_architectures)
    set(supported TRUE)
  endif()
  set(${output} "${supported}" PARENT_SCOPE)
endfunction()

function(conformance_toolchain backend architecture image_output dependency_output support_output)
  set(compiler vendor)
  if(ARGC GREATER 5)
    set(compiler "${ARGV5}")
  endif()
  conformance_compiler_supported("${backend}" "${architecture}" "${compiler}" supported)
  if(NOT supported)
    message(FATAL_ERROR "Unsupported compiler route: ${backend}/${architecture}/${compiler}")
  endif()
  set(stage "toolchain-${backend}")
  if(NOT compiler STREQUAL "vendor")
    set(stage "toolchain-${compiler}-${backend}")
  endif()
  set(build_arguments)
  set(support "support")
  if(backend STREQUAL "cuda")
    if(compiler STREQUAL "gcc")
      # GNU generates PTX itself; this SDK supplies only the driver headers/probe.
      conformance_cuda_toolchain(sm_70 cuda_image cuda_package support)
    else()
      conformance_cuda_toolchain("${architecture}" cuda_image cuda_package support)
    endif()
    set(key "cuda-${cuda_package}")
    list(APPEND build_arguments
      --build-arg "CUDA_IMAGE=${cuda_image}"
      --build-arg "CUDA_PACKAGE_VERSION=${cuda_package}")
  else()
    set(key "${backend}")
    if(backend STREQUAL "hip" OR backend STREQUAL "hsa")
      list(APPEND build_arguments --build-arg "ROCM_VERSION=${ROCM_VERSION}")
    endif()
  endif()

  set(inputs "${CONFORMANCE_SOURCE_DIRECTORY}/.dockerignore")
  if(compiler STREQUAL "clang")
    if(cuda_package STREQUAL "11-8")
      set(cccl_version 2.8.2)
      set(cccl_sha256 141801ddddf5b911f64f97be66a7c75f98374bebca60f275bedec75b8eeab345)
    else()
      set(cccl_version 3.4.3)
      set(cccl_sha256 2b3eee7b044f528408b45ab85535ce6c7430b063d5bc939599a4e038da7cf6fb)
    endif()
    list(APPEND build_arguments --build-arg "LLVM_VERSION=${conformance_clang_version}"
      --build-arg "CCCL_VERSION=${cccl_version}" --build-arg "CCCL_SHA256=${cccl_sha256}")
  elseif(compiler STREQUAL "gcc")
    set(support "support-gcc")
    list(APPEND build_arguments --build-arg "GCC_VERSION=${conformance_gcc_version}"
      --build-arg "GCC_RUNTIME_VERSION=${conformance_gcc_runtime_version}"
      --build-arg "GCC_RUNTIME_SHA256=${conformance_gcc_runtime_sha256}")
    list(APPEND inputs "${CONFORMANCE_SOURCE_DIRECTORY}/patches/libgomp-default-device.patch")
  endif()
  if(NOT compiler STREQUAL "vendor")
    string(APPEND key "-${compiler}")
  endif()
  container_image(tag stamp
    EXECUTABLE "${CONTAINER_EXECUTABLE}"
    REPOSITORY "localhost/conformance-${key}"
    FILE "${CONFORMANCE_SOURCE_DIRECTORY}/Containerfile"
    CONTEXT "${CONFORMANCE_SOURCE_DIRECTORY}"
    STAGE "${stage}"
    BUILD_ARGUMENTS ${build_arguments}
    INPUTS ${inputs}
    JOB_POOL container_build)
  set(${image_output} "${tag}" PARENT_SCOPE)
  set(${dependency_output} "${stamp}" PARENT_SCOPE)
  set(${support_output} "${support}" PARENT_SCOPE)
endfunction()

# SPIR-V translation uses <output>.bc as an intermediate; declare it as a byproduct.
function(conformance_opencl_commands backend architecture source output compile_output translate_output)
  if(backend STREQUAL "hsa")
    if(NOT architecture)
      message(FATAL_ERROR "An AMDGPU architecture is required for OpenCL C compilation")
    endif()
    set(compile /opt/rocm/llvm/bin/clang -x cl -target amdgcn-amd-amdhsa
      "-mcpu=${architecture}" -cl-std=CL2.0 -mcode-object-version=4
      -ffile-prefix-map=/source=. -Xclang -finclude-default-header
      ${ARGN} "${source}" -o "${output}")
    set(translate)
  elseif(backend STREQUAL "opencl")
    set(compiler /opt/intel/oneapi/compiler/latest/bin/compiler)
    set(compile "${compiler}/clang" -cc1 -triple spir64 -cl-std=CL2.0
      -finclude-default-header -emit-llvm-bc ${ARGN} "${source}" -o "${output}.bc")
    set(translate "${compiler}/llvm-spirv" "${output}.bc" -o "${output}")
  else()
    message(FATAL_ERROR "Unsupported OpenCL C toolchain: ${backend}")
  endif()
  set(${compile_output} "${compile}" PARENT_SCOPE)
  set(${translate_output} "${translate}" PARENT_SCOPE)
endfunction()

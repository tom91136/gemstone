include_guard(GLOBAL)
include("${CMAKE_CURRENT_LIST_DIR}/../cmake/ContainerBuild.cmake")

set(conformance_cuda_11_8_architectures sm_35 sm_50 sm_60 sm_70)
set(conformance_cuda_13_3_architectures sm_75 sm_80 sm_86 sm_87 sm_89 sm_90 sm_100 sm_103 sm_120
  sm_90a sm_100a sm_100f sm_103a sm_103f sm_120a sm_120f)
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
set(conformance_clang_release 22.1.8)
set(conformance_clang_release_sha256 df0e1ecf16caf3489a272a5eea4eec9b0d82878f6477fa309504f918a0006384)
set(conformance_clang_tools_release 23.1.5)
set(conformance_clang_tools_release_sha256 f72edb0b36580ece7f52e23858f00c18fad4e87395ea7ad05d7e55c39f8fc78a)
set(conformance_clang_tools_source_sha256 fb50b1eac93ddeb3aa8dab4cfe324dff74f4a9c2bdee6160c9e36188f8d06a6e)
set(conformance_clang_source_sha256 922f1817a0df7b1489272d18134ee0087a8b068828f87ac63b9861b1a9965888)
string(REGEX MATCH "^[0-9]+" conformance_clang_version "${conformance_clang_release}")
set(conformance_clang_build_arguments
  --build-arg "LLVM_VERSION=${conformance_clang_version}"
  --build-arg "LLVM_RELEASE=${conformance_clang_release}"
  --build-arg "LLVM_RELEASE_SHA256=${conformance_clang_release_sha256}"
  --build-arg "LLVM_SOURCE_SHA256=${conformance_clang_source_sha256}")
set(conformance_native_toolchain_build_arguments
  --build-arg "LLVM_TOOLS_RELEASE=${conformance_clang_tools_release}"
  --build-arg "LLVM_TOOLS_RELEASE_SHA256=${conformance_clang_tools_release_sha256}"
  --build-arg "LLVM_TOOLS_SOURCE_SHA256=${conformance_clang_tools_source_sha256}")
set(conformance_bitwuzla_release 0.9.1)
set(conformance_bitwuzla_release_sha256 057f1546ae2068df57beb178f3eeab1678f0e5f0c378787a05b7bb294617c9c6)
set(conformance_native_toolchain_image
  REPOSITORY localhost/conformance-native-toolchain
  FILE "${CONFORMANCE_SOURCE_DIRECTORY}/Containerfile"
  STAGE native-toolchain
  BUILD_ARGUMENTS ${conformance_native_toolchain_build_arguments}
  INPUTS "${CONFORMANCE_SOURCE_DIRECTORY}/.dockerignore")
set(conformance_gcc_version 14)
set(conformance_gcc_runtime_version 14.2.0)
set(conformance_gcc_runtime_sha256 a7b39bc69cbf9e25826c5a60ab26477001f7c08d85cec04bc0e29cabed6f3cc9)
set(conformance_gcc_cuda_architectures sm_35 sm_70 sm_75 sm_80)
set(conformance_gcc_hsa_architectures gfx900 gfx906 gfx908 gfx90a gfx90c gfx1030 gfx1036 gfx1100 gfx1103)
# NVHPC takes no architecture- or family-specific targets; the CUDA 13 that 26.5 bundles starts at sm_75
set(conformance_nvhpc_25_7_architectures sm_50 sm_60 sm_70)
set(conformance_nvhpc_26_5_architectures sm_75 sm_80 sm_86 sm_87 sm_89 sm_90 sm_100 sm_103 sm_120)
set(conformance_nvhpc_25_7_image
  "nvcr.io/nvidia/nvhpc@sha256:777412a2977d4feeb054d1076d6cfb74dd4afbb44c2b0225c70554bc298b9dbc")
set(conformance_nvhpc_26_5_image
  "nvcr.io/nvidia/nvhpc@sha256:25621a24ed39fe0dce3e9458e56e3f11194c122ac0206a44532246a58972a854")
set(conformance_openacc_architectures ${conformance_nvhpc_25_7_architectures} ${conformance_nvhpc_26_5_architectures})

function(conformance_openacc_toolchain architecture image_output version_output)
  foreach(version IN ITEMS 25.7 26.5)
    string(REPLACE "." "_" name "${version}")
    if(architecture IN_LIST conformance_nvhpc_${name}_architectures)
      set(${image_output} "${conformance_nvhpc_${name}_image}" PARENT_SCOPE)
      set(${version_output} "${version}" PARENT_SCOPE)
      return()
    endif()
  endforeach()
  message(FATAL_ERROR "No NVHPC toolchain supports architecture ${architecture}")
endfunction()

function(conformance_compiler_supported backend architecture compiler output)
  set(supported FALSE)
  if(compiler STREQUAL "vendor")
    if(NOT backend STREQUAL "openacc" OR architecture IN_LIST conformance_openacc_architectures)
      set(supported TRUE)
    endif()
  elseif(compiler STREQUAL "clang" AND backend STREQUAL "cuda" AND NOT architecture MATCHES "f$")
    # Clang accepts architecture-specific (a) targets but not family-specific (f) ones.
    set(supported TRUE)
  elseif(compiler STREQUAL "gcc" AND architecture IN_LIST conformance_gcc_${backend}_architectures)
    set(supported TRUE)
  endif()
  set(${output} "${supported}" PARENT_SCOPE)
endfunction()

# Routes of one key share the runtime closure under <backend>/<key> of the artifact tree.
function(conformance_support_key backend architecture compiler output)
  set(support "support")
  if(compiler STREQUAL "gcc")
    set(support "support-gcc")
  elseif(backend STREQUAL "cuda")
    conformance_cuda_toolchain("${architecture}" unused_image unused_package support)
  elseif(backend STREQUAL "openacc")
    conformance_openacc_toolchain("${architecture}" unused_image version)
    set(support "support-${version}")
  endif()
  set(${output} "${support}" PARENT_SCOPE)
endfunction()

# The OpenMP offload runtime ships only with the Clang image, so its closure is a key of its own that Clang routes load
# beside their primary one.
function(conformance_support_supplement backend architecture compiler output)
  set(supplement "")
  if(compiler STREQUAL "clang")
    set(supplement "support-offload")
  endif()
  set(${output} "${supplement}" PARENT_SCOPE)
endfunction()

# The llvm-offload stage builds from the stages above it, so their text belongs to the key.
function(conformance_offload_key output)
  file(READ "${CONFORMANCE_SOURCE_DIRECTORY}/Containerfile" containerfile)
  string(FIND "${containerfile}" "FROM ubuntu-build-base AS llvm-offload\n" start)
  if(start EQUAL -1)
    message(FATAL_ERROR "The llvm-offload stage is missing from the Containerfile")
  endif()
  string(SUBSTRING "${containerfile}" ${start} -1 rest)
  string(FIND "${rest}" "\nFROM " next)
  math(EXPR end "${start} + ${next}")
  string(SUBSTRING "${containerfile}" 0 ${end} prefix)
  string(SHA256 digest "${conformance_clang_release};${conformance_clang_source_sha256};${prefix}")
  string(SUBSTRING "${digest}" 0 16 digest)
  set(${output} "${conformance_clang_release}-${digest}" PARENT_SCOPE)
endfunction()

function(conformance_offload_archive output)
  conformance_offload_key(key)
  set(archive "${CONFORMANCE_OFFLOAD_DIRECTORY}/${key}/llvm-offload.tar.zst")
  if(NOT EXISTS "${archive}")
    container_image(tag stamp
      EXECUTABLE "${CONTAINER_EXECUTABLE}"
      REPOSITORY "localhost/conformance-llvm-offload"
      FILE "${CONFORMANCE_SOURCE_DIRECTORY}/Containerfile"
      CONTEXT "${CONFORMANCE_SOURCE_DIRECTORY}"
      STAGE llvm-offload
      BUILD_ARGUMENTS ${conformance_clang_build_arguments}
      INPUTS "${CONFORMANCE_SOURCE_DIRECTORY}/.dockerignore"
      JOB_POOL container_build)
    get_property(archives GLOBAL PROPERTY CONFORMANCE_OFFLOAD_ARCHIVES)
    if(NOT archive IN_LIST archives)
      add_custom_command(
        OUTPUT "${archive}"
        COMMAND "${CMAKE_COMMAND}" -DMODE=export "-DCONTAINER_EXECUTABLE=${CONTAINER_EXECUTABLE}" "-DIMAGE=${tag}"
          "-DOUTPUT=${archive}" -P "${CONFORMANCE_SOURCE_DIRECTORY}/artifacts/Offload.cmake"
        DEPENDS "${stamp}"
        COMMENT "Exporting ${archive}"
        VERBATIM)
      set_property(GLOBAL APPEND PROPERTY CONFORMANCE_OFFLOAD_ARCHIVES "${archive}")
    endif()
  endif()
  set(${output} "${archive}" PARENT_SCOPE)
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
  set(lint FALSE)
  if(ARGC GREATER 6 AND ARGV6 STREQUAL "LINT")
    set(lint TRUE)
  endif()
  set(stage "toolchain-${backend}")
  if(NOT compiler STREQUAL "vendor")
    set(stage "toolchain-${compiler}-${backend}")
  endif()
  set(build_arguments)
  if(lint)
    string(REGEX REPLACE "^toolchain-" "lint-" stage "${stage}")
    if(NOT compiler STREQUAL "clang")
      list(APPEND build_arguments ${conformance_clang_build_arguments})
    endif()
  endif()
  conformance_support_key("${backend}" "${architecture}" "${compiler}" support)
  if(backend STREQUAL "cuda")
    if(compiler STREQUAL "gcc")
      # GNU generates PTX itself; this SDK supplies only the driver headers/probe.
      conformance_cuda_toolchain(sm_70 cuda_image cuda_package unused_support)
    else()
      conformance_cuda_toolchain("${architecture}" cuda_image cuda_package unused_support)
    endif()
    set(key "cuda-${cuda_package}")
    list(APPEND build_arguments
      --build-arg "CUDA_IMAGE=${cuda_image}"
      --build-arg "CUDA_PACKAGE_VERSION=${cuda_package}")
  elseif(backend STREQUAL "openacc")
    conformance_openacc_toolchain("${architecture}" nvhpc_image nvhpc_version)
    set(key "openacc-${nvhpc_version}")
    list(APPEND build_arguments --build-arg "NVHPC_IMAGE=${nvhpc_image}" --build-arg "NVHPC_VERSION=${nvhpc_version}")
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
    list(APPEND build_arguments ${conformance_clang_build_arguments}
      --build-arg "CCCL_VERSION=${cccl_version}" --build-arg "CCCL_SHA256=${cccl_sha256}")
  elseif(compiler STREQUAL "gcc")
    list(APPEND build_arguments --build-arg "GCC_VERSION=${conformance_gcc_version}"
      --build-arg "GCC_RUNTIME_VERSION=${conformance_gcc_runtime_version}"
      --build-arg "GCC_RUNTIME_SHA256=${conformance_gcc_runtime_sha256}")
    list(APPEND inputs "${CONFORMANCE_SOURCE_DIRECTORY}/patches/libgomp-default-device.patch")
  endif()
  if(NOT compiler STREQUAL "vendor")
    string(APPEND key "-${compiler}")
  endif()
  if(lint)
    string(APPEND key "-lint")
  endif()
  set(prebuilt)
  if(compiler STREQUAL "clang" AND backend STREQUAL "cuda" AND NOT lint)
    conformance_offload_archive(archive)
    get_filename_component(archive_directory "${archive}" DIRECTORY)
    set(prebuilt BUILD_CONTEXTS "llvm-offload-archive=${archive_directory}" DEPENDS "${archive}")
  endif()
  container_image(tag stamp
    EXECUTABLE "${CONTAINER_EXECUTABLE}"
    REPOSITORY "localhost/conformance-${key}"
    FILE "${CONFORMANCE_SOURCE_DIRECTORY}/Containerfile"
    CONTEXT "${CONFORMANCE_SOURCE_DIRECTORY}"
    STAGE "${stage}"
    BUILD_ARGUMENTS ${build_arguments}
    INPUTS ${inputs}
    ${prebuilt}
    JOB_POOL container_build)
  set(${image_output} "${tag}" PARENT_SCOPE)
  set(${dependency_output} "${stamp}" PARENT_SCOPE)
  set(${support_output} "${support}" PARENT_SCOPE)
endfunction()

# SPIR-V translation uses <output>.bc as an intermediate; declare it as a byproduct. TRANSLATE names the variable
# receiving the translation command, which only SPIR-V output has.
function(conformance_opencl_commands backend architecture source output compile_output)
  cmake_parse_arguments(PARSE_ARGV 5 opencl "" TRANSLATE "")
  if(backend STREQUAL "hsa")
    if(NOT architecture)
      message(FATAL_ERROR "An AMDGPU architecture is required for OpenCL C compilation")
    endif()
    set(compile /opt/rocm/llvm/bin/clang -x cl -target amdgcn-amd-amdhsa
      "-mcpu=${architecture}" -cl-std=CL2.0 -mcode-object-version=4
      -ffile-prefix-map=/source=. -Xclang -finclude-default-header
      ${opencl_UNPARSED_ARGUMENTS} "${source}" -o "${output}")
    set(translate)
  elseif(backend STREQUAL "opencl")
    set(compiler /opt/intel/oneapi/compiler/latest/bin/compiler)
    set(compile "${compiler}/clang" -cc1 -triple spir64 -cl-std=CL2.0
      -finclude-default-header -emit-llvm-bc ${opencl_UNPARSED_ARGUMENTS} "${source}" -o "${output}.bc")
    set(translate "${compiler}/llvm-spirv" "${output}.bc" -o "${output}")
  else()
    message(FATAL_ERROR "Unsupported OpenCL C toolchain: ${backend}")
  endif()
  set(${compile_output} "${compile}" PARENT_SCOPE)
  if(opencl_TRANSLATE)
    set(${opencl_TRANSLATE} "${translate}" PARENT_SCOPE)
  elseif(translate)
    message(FATAL_ERROR "OpenCL C compilation for ${backend} needs a TRANSLATE output")
  endif()
endfunction()

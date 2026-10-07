cmake_minimum_required(VERSION 3.25)

foreach(variable IN ITEMS BACKEND CONFORMANCE_OUTPUT CONFORMANCE_WORK_DIRECTORY)
  if(NOT DEFINED ${variable} OR "${${variable}}" STREQUAL "")
    message(FATAL_ERROR "${variable} is required")
  endif()
endforeach()

if(NOT DEFINED CONFORMANCE_COMPILER)
  set(CONFORMANCE_COMPILER vendor)
endif()
set(artifact_base "${BACKEND}")
if(NOT CONFORMANCE_COMPILER STREQUAL "vendor")
  string(APPEND artifact_base "/${CONFORMANCE_COMPILER}")
endif()

foreach(variable IN ITEMS
    BUILD_UNIFIED BUILD_CTS BUILD_MINIAPPS HIP_ENABLE_ROCBLAS)
  if(NOT DEFINED ${variable})
    if(variable STREQUAL "BUILD_UNIFIED" AND BACKEND MATCHES "^vulkan$")
      set(${variable} OFF)
    else()
      set(${variable} ON)
    endif()
  endif()
endforeach()
if(NOT DEFINED MINIAPP_MODELS)
  set(MINIAPP_MODELS "")
endif()
if(NOT DEFINED CONFORMANCE_SUPPORT_KEY)
  set(CONFORMANCE_SUPPORT_KEY support)
endif()
if(NOT CONFORMANCE_SUPPORT_KEY MATCHES "^[A-Za-z0-9][A-Za-z0-9._-]*$")
  message(FATAL_ERROR "Invalid conformance support key: ${CONFORMANCE_SUPPORT_KEY}")
endif()

# Include toolchain selection in the persistent work signature.
set(backend_cmake_arguments "-DCONFORMANCE_COMPILER=${CONFORMANCE_COMPILER}")
if(NOT "$ENV{CC}" STREQUAL "")
  list(APPEND backend_cmake_arguments "-DCMAKE_C_COMPILER=$ENV{CC}")
endif()
if(NOT "$ENV{CXX}" STREQUAL "")
  list(APPEND backend_cmake_arguments "-DCMAKE_CXX_COMPILER=$ENV{CXX}")
endif()
if(BACKEND STREQUAL "hip")
  list(APPEND backend_cmake_arguments -DCMAKE_HIP_PLATFORM=amd)
elseif(BACKEND STREQUAL "cuda")
  list(APPEND backend_cmake_arguments
    "-DCONFORMANCE_CUDA_CUBIN_ONLY=${CONFORMANCE_CUDA_CUBIN_ONLY}")
  if(CONFORMANCE_COMPILER STREQUAL "clang")
    list(APPEND backend_cmake_arguments "-DCMAKE_CUDA_COMPILER=$ENV{CUDACXX}"
      "-DCMAKE_CUDA_FLAGS=--cuda-path=$ENV{CUDAToolkit_ROOT} --cuda-gpu-arch=${CUDA_ARCHITECTURES}")
  elseif(CONFORMANCE_COMPILER STREQUAL "vendor")
    list(APPEND backend_cmake_arguments "-DCMAKE_CUDA_HOST_COMPILER=$ENV{CUDAHOSTCXX}")
  endif()
elseif(BACKEND STREQUAL "sycl")
  if(NOT DEFINED SYCL_CTS_ENABLE_FULL_CONFORMANCE)
    set(SYCL_CTS_ENABLE_FULL_CONFORMANCE OFF)
  endif()
  list(APPEND backend_cmake_arguments
    "-DSYCL_CTS_ENABLE_FULL_CONFORMANCE=${SYCL_CTS_ENABLE_FULL_CONFORMANCE}")
endif()

if(BACKEND MATCHES "^(cuda|openacc)$")
  if(NOT DEFINED CUDA_ARCHITECTURES OR
      NOT CUDA_ARCHITECTURES MATCHES "^sm_[0-9]+$")
    message(FATAL_ERROR "Invalid CUDA architecture: ${CUDA_ARCHITECTURES}")
  endif()
  set(architecture "${CUDA_ARCHITECTURES}")
  string(REGEX REPLACE "^sm_" "" cuda_architecture "${architecture}")
  set(amd_architecture gfx908)
elseif(BACKEND MATCHES "^(hip|hsa)$")
  if(NOT DEFINED AMDGPU_ARCHITECTURES OR
      NOT AMDGPU_ARCHITECTURES MATCHES "^gfx[0-9a-f]+$")
    message(FATAL_ERROR "Invalid AMDGPU architecture: ${AMDGPU_ARCHITECTURES}")
  endif()
  set(architecture "${AMDGPU_ARCHITECTURES}")
  set(cuda_architecture 75)
  set(amd_architecture "${architecture}")
else()
  set(architecture "")
  set(cuda_architecture 75)
  set(amd_architecture gfx908)
endif()

if(NOT DEFINED CONFORMANCE_DEPENDENCY_CACHE)
  set(CONFORMANCE_DEPENDENCY_CACHE "")
endif()
if(NOT DEFINED CONFORMANCE_OFFLINE)
  set(CONFORMANCE_OFFLINE OFF)
endif()
if(NOT DEFINED CONFORMANCE_KEEP_WORK)
  set(CONFORMANCE_KEEP_WORK ON)
endif()

include("${CMAKE_CURRENT_LIST_DIR}/../cmake/ArtifactWork.cmake")

set(work "${CONFORMANCE_WORK_DIRECTORY}")
file(SHA256 "${CMAKE_CURRENT_LIST_DIR}/../dependencies.cmake" dependencies_digest)
set(configuration_signature
  "${dependencies_digest}|${BACKEND}|${CONFORMANCE_COMPILER}|${architecture}|${BUILD_UNIFIED}|${BUILD_CTS}|${BUILD_MINIAPPS}|${HIP_ENABLE_ROCBLAS}|${MINIAPP_MODELS}|${cuda_architecture}|${amd_architecture}|${ROCM_VERSION}|${CONFORMANCE_SUPPORT_KEY}|${backend_cmake_arguments}")
prepare_work_directory("${work}" "${configuration_signature}")
set(build "${work}/build")
set(ENV{HOME} "${work}/home")
file(MAKE_DIRECTORY "$ENV{HOME}")

find_program(artifact_ninja NAMES ninja REQUIRED)
set(rocm_argument)
if(BACKEND MATCHES "^(hip|hsa)$")
  set(rocm_argument "-DROCM_VERSION=${ROCM_VERSION}")
endif()

execute_process(
  COMMAND "${CMAKE_COMMAND}"
    -S /source/conformance
    -B "${build}"
    -G Ninja
    --log-level=WARNING
    "-DCMAKE_MAKE_PROGRAM=${artifact_ninja}"
    -DCMAKE_BUILD_TYPE=Release
    -DCONFORMANCE_BACKEND=${BACKEND}
    ${rocm_argument}
    -DCONFORMANCE_BUILD_UNIFIED=${BUILD_UNIFIED}
    -DCONFORMANCE_BUILD_CTS=${BUILD_CTS}
    -DCONFORMANCE_BUILD_MINIAPPS=${BUILD_MINIAPPS}
    -DCONFORMANCE_HIP_ENABLE_ROCBLAS=${HIP_ENABLE_ROCBLAS}
    "-DCONFORMANCE_MINIAPP_MODELS=${MINIAPP_MODELS}"
    -DCMAKE_CUDA_ARCHITECTURES=${cuda_architecture}
    -DCMAKE_HIP_ARCHITECTURES=${amd_architecture}
    "-DCONFORMANCE_DEPENDENCY_CACHE=${CONFORMANCE_DEPENDENCY_CACHE}"
    -DCONFORMANCE_OFFLINE=${CONFORMANCE_OFFLINE}
    -DBUILD_TESTING=OFF
    ${backend_cmake_arguments}
  COMMAND_ERROR_IS_FATAL ANY)
execute_process(
  COMMAND "${CMAKE_COMMAND}" --build "${build}"
    --target conformance-artifact
  COMMAND_ERROR_IS_FATAL ANY)

function(write_metadata destination support)
  file(WRITE "${destination}/artifact.toml"
    "backend = \"${BACKEND}\"\n"
    "compiler = \"${CONFORMANCE_COMPILER}\"\n"
    "architecture = \"${architecture}\"\n"
    "support = \"${support}\"\n")
endfunction()

find_program(patchelf patchelf REQUIRED)
function(set_program_rpath root rpath)
  set(programs "${root}/conformance-device")
  if(EXISTS "${root}/test-programs.txt")
    file(STRINGS "${root}/test-programs.txt" entries)
    foreach(entry IN LISTS entries)
      string(REGEX REPLACE "\t.*$" "" program "${entry}")
      list(APPEND programs "${root}/${program}")
    endforeach()
  endif()
  foreach(program IN LISTS programs)
    execute_process(COMMAND "${patchelf}" --set-rpath "${rpath}" "${program}"
      COMMAND_ERROR_IS_FATAL ANY)
  endforeach()
  if(BACKEND STREQUAL "levelzero")
    file(GLOB helpers LIST_DIRECTORIES FALSE "${root}/device/*")
    foreach(helper IN LISTS helpers)
      execute_process(COMMAND "${patchelf}" --set-rpath "$ORIGIN/../lib" "${helper}"
        COMMAND_ERROR_IS_FATAL ANY)
    endforeach()
  endif()
endfunction()

if(architecture)
  if(NOT CONFORMANCE_OUTPUT MATCHES "^(both|overlay)$")
    message(FATAL_ERROR
      "Invalid architecture artifact output: ${CONFORMANCE_OUTPUT}")
  endif()
  set(overlay "/output/${artifact_base}/${architecture}")
  set(support "/output/${BACKEND}/${CONFORMANCE_SUPPORT_KEY}")
  file(REMOVE_RECURSE "${overlay}")
  execute_process(
    COMMAND "${CMAKE_COMMAND}" --install "${build}"
      --component architecture --prefix "${overlay}" --strip
    COMMAND_ERROR_IS_FATAL ANY)
  write_metadata("${overlay}" "${CONFORMANCE_SUPPORT_KEY}")
  file(RELATIVE_PATH support_relative "${overlay}" "${support}")
  set_program_rpath("${overlay}" "$ORIGIN/${support_relative}/lib")
  if(CONFORMANCE_OUTPUT STREQUAL "both")
    file(REMOVE_RECURSE "${support}")
    execute_process(
      COMMAND "${CMAKE_COMMAND}" -E env "CONFORMANCE_RUNTIME_INPUT=${overlay}"
        "${CMAKE_COMMAND}" --install "${build}"
        --component support --prefix "${support}" --strip
      COMMAND_ERROR_IS_FATAL ANY)
    file(WRITE "${support}/support.stamp" "Runtime closure for ${BACKEND}\n")
  endif()
  execute_process(
    COMMAND "${CMAKE_COMMAND}" -DARTIFACT=${overlay} -DSUPPORT=${support}
      -P /source/conformance/artifacts/Verify.cmake
    COMMAND_ERROR_IS_FATAL ANY)
else()
  if(NOT CONFORMANCE_OUTPUT STREQUAL "artifact")
    message(FATAL_ERROR "Invalid generic artifact output: ${CONFORMANCE_OUTPUT}")
  endif()
  set(artifact "/output/${artifact_base}")
  file(REMOVE_RECURSE "${artifact}")
  execute_process(
    COMMAND "${CMAKE_COMMAND}" --install "${build}"
      --component architecture --prefix "${artifact}" --strip
    COMMAND_ERROR_IS_FATAL ANY)
  execute_process(
    COMMAND "${CMAKE_COMMAND}" -E env "CONFORMANCE_RUNTIME_INPUT=${artifact}"
      "${CMAKE_COMMAND}" --install "${build}"
      --component support --prefix "${artifact}" --strip
    COMMAND_ERROR_IS_FATAL ANY)
  write_metadata("${artifact}" "")
  set_program_rpath("${artifact}" "$ORIGIN/lib")
  execute_process(
    COMMAND "${CMAKE_COMMAND}" -DARTIFACT=${artifact}
      -P /source/conformance/artifacts/Verify.cmake
    COMMAND_ERROR_IS_FATAL ANY)
endif()

if(NOT CONFORMANCE_KEEP_WORK)
  file(REMOVE_RECURSE "${build}" "$ENV{HOME}")
endif()

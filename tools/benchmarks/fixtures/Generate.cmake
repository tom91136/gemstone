cmake_minimum_required(VERSION 3.25)

if(NOT FIXTURE MATCHES "^(cuda|hip|opencl)$")
  message(FATAL_ERROR "FIXTURE must be cuda, hip, or opencl")
endif()
if(NOT FIXTURE STREQUAL "opencl" AND ARCHITECTURE STREQUAL "")
  message(FATAL_ERROR "ARCHITECTURE must be set for ${FIXTURE}")
endif()

set(source /source/tools/benchmarks/src/main/resources)
set(output /output)

if(FIXTURE STREQUAL "cuda")
  set(temporary "${output}/.cloverleaf-flux.ptx.tmp")
  execute_process(
    COMMAND nvcc -ptx -arch=${ARCHITECTURE} -O2 "${source}/cloverleaf-flux.cu" -o "${temporary}"
    COMMAND_ERROR_IS_FATAL ANY)
  file(RENAME "${temporary}" "${output}/cloverleaf-flux.ptx")
elseif(FIXTURE STREQUAL "hip")
  set(bundle "${output}/.cloverleaf-flux.bundle.tmp")
  set(temporary "${output}/.cloverleaf-flux.hsaco.tmp")
  execute_process(
    COMMAND hipcc --genco --offload-arch=${ARCHITECTURE} -O2 "${source}/cloverleaf-flux.cu" -o "${bundle}"
    COMMAND_ERROR_IS_FATAL ANY)
  execute_process(
    COMMAND clang-offload-bundler -unbundle -type=o "-input=${bundle}"
      "-output=${temporary}" -targets=hipv4-amdgcn-amd-amdhsa--${ARCHITECTURE}
    COMMAND_ERROR_IS_FATAL ANY)
  file(RENAME "${temporary}" "${output}/cloverleaf-flux.hsaco")
  file(REMOVE "${bundle}")
elseif(FIXTURE STREQUAL "opencl")
  set(bitcode "${output}/.opencl-cts-async-copy.bc.tmp")
  set(temporary "${output}/.opencl-cts-async-copy.spv.tmp")
  execute_process(
    COMMAND icpx -x cl -cl-std=CL1.2 -D__OPENCL_VERSION__=120
      -target spir64 -emit-llvm -fsigned-zeros -c
      "${source}/opencl-cts-async-copy.cl" -o "${bitcode}"
    COMMAND_ERROR_IS_FATAL ANY)
  execute_process(
    COMMAND /opt/intel/oneapi/compiler/latest/bin/compiler/llvm-spirv
      "${bitcode}" -o "${temporary}"
    COMMAND_ERROR_IS_FATAL ANY)
  file(RENAME "${temporary}" "${output}/opencl-cts-async-copy.spv")
  file(REMOVE "${bitcode}")
endif()

set(miniapp_babelstream_models cuda hip omp-target kokkos ocl sycl acc)
set(miniapp_babelstream_license LICENSE)
set(miniapp_babelstream_license_name BabelStream.txt)
set(miniapp_babelstream_main src/main.cpp)
set(miniapp_babelstream_executable [=[add_executable(${EXE_NAME} ${IMPL_SOURCES} src/main.cpp)]=])
set(miniapp_babelstream_catch_executable
  [=[add_executable(${EXE_NAME} ${IMPL_SOURCES} src/main.cpp conformance_test.cpp)]=])
set(miniapp_babelstream_omp_target_upstream_model omp)
set(miniapp_babelstream_openmp_definitions OMP_TARGET_GPU)

function(miniapp_babelstream_patch model source)
  if(CONFORMANCE_BACKEND STREQUAL "opencl" AND model STREQUAL "ocl")
    patch_exact("${source}/src/ocl/OCLStream.cpp"
      [=[    dot_wgsize     = device.getInfo<CL_DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE>() * 2;]=]
      [=[    dot_wgsize = (sizeof(T) == sizeof(double)
                      ? device.getInfo<CL_DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE>()
                      : device.getInfo<CL_DEVICE_NATIVE_VECTOR_WIDTH_FLOAT>())
                     * 2;]=])
    patch_exact("${source}/src/ocl/OCLStream.cpp"
      [=[dot_kernel = new cl::KernelFunctor<cl::Buffer, cl::Buffer, cl::Buffer, cl::LocalSpaceArg, cl_long>(program, "stream_dot");]=]
      [=[dot_kernel = new cl::KernelFunctor<cl::Buffer, cl::Buffer, cl::Buffer, cl::LocalSpaceArg, cl_long>(program, "stream_dot");

  // CL_DEVICE_MAX_WORK_GROUP_SIZE is only a device-wide ceiling. The compiled
  // reduction kernel may have a smaller limit, as it does under Rusticl.
  const auto kernel_wgsize = cl::Kernel(program, "stream_dot")
                                 .getWorkGroupInfo<CL_KERNEL_WORK_GROUP_SIZE>(device);
  dot_wgsize = std::min(dot_wgsize, kernel_wgsize);]=])
  elseif(CONFORMANCE_BACKEND STREQUAL "cuda" AND model STREQUAL "cuda")
    patch_exact("${source}/src/cuda/model.cmake"
      "    register_link_library(\"nvidia-ml\")" "")
    set(path "${source}/src/cuda/CUDAStream.cu")
    file(READ "${path}" content)
    string(REPLACE "#include \"CUDAStream.h\"\n"
      "#include \"CUDAStream.h\"\n#include <cstdint>\n" content "${content}")
    string(REGEX REPLACE "#include <nvml.h>\n" "" content "${content}")
    string(REPLACE [=[[[noreturn]] inline void nvml_error(char const* file, int line, char const* expr, nvmlReturn_t e) {
  std::fprintf(stderr, "NVML Error at %s:%d: %s (%d)\n  %s\n", file, line, nvmlErrorString(e), e, expr);
  exit(e);
}
]=] "" content "${content}")
    string(REGEX REPLACE "#define NVML\\([^\n]*\n" "" content "${content}")
    string(REGEX REPLACE "[^\n]*NVML\\(nvml[^\n]*\n" "" content "${content}")
    string(REGEX REPLACE "unsigned[ \\t]+int[ \\t]+memclock;" "uint32_t memclock = 0;" content "${content}")
    string(REGEX REPLACE "[^\n]*(mybus|nvmldev)[^\n]*\n" "" content "${content}")
    file(WRITE "${path}" "${content}")
    if(CONFORMANCE_COMPILER STREQUAL "clang")
      patch_exact("${source}/src/cuda/model.cmake"
        [=["-forward-unknown-to-host-compiler" "-arch=${CUDA_ARCH}"]=]
        [=["--cuda-gpu-arch=${CUDA_ARCH}"]=])
      patch_exact("${source}/src/cuda/model.cmake" [=["--extended-lambda"]=] "")
    endif()
  endif()
endfunction()

function(miniapp_babelstream_configure model)
  set(BUILD_NATIVE OFF CACHE BOOL "" FORCE)
endfunction()

function(miniapp_babelstream_test_arguments model output)
  set(arguments -s 4096 -n 2)
  if(model MATCHES "^(ocl|sycl)$")
    list(APPEND arguments --float)
  endif()
  set(${output} "${arguments}" PARENT_SCOPE)
endfunction()

conformance_register_miniapp(babelstream)

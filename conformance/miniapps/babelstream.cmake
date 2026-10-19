function(babelstream_adapt model source)
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
  elseif(model STREQUAL "cuda-tuned")
    patch_exact("${source}/src/cuda-tuned/model.cmake"
      "    register_link_library(\"nvidia-ml\")" "")
    set(path "${source}/src/cuda-tuned/CUDAStream.cu")
    file(READ "${path}" content)
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
      patch_exact("${source}/src/cuda-tuned/model.cmake"
        [=["-forward-unknown-to-host-compiler" "-arch=${CUDA_ARCH}"
      "--extended-lambda" ]=]
        [=["--cuda-gpu-arch=${CUDA_ARCH}" ]=])
    endif()
  endif()
  if(model MATCHES "^(ocl|sycl2020-(acc|usm))$")
    set(arguments ${arguments} --float PARENT_SCOPE)
  endif()
  set(BUILD_NATIVE OFF CACHE BOOL "" FORCE)
endfunction()

conformance_miniapp(babelstream
  MODELS cuda cuda-tuned hip omp-target=omp kokkos ocl sycl2020-acc sycl2020-usm acc
  MAIN src/main.cpp
  LICENCE_FILE LICENSE BabelStream.txt
  OPENMP_DEFINITIONS OMP_TARGET_GPU
  ARGUMENTS -s 4096 -n 2
  ADAPT babelstream_adapt)

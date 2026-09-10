#define CL_TARGET_OPENCL_VERSION 120
#include "../provider.hpp"
#include "unified_suite/runtime_checks.hpp"
#include <CL/cl.h>

namespace {
using namespace unified_suite;
using namespace runtime_checks;
void check(cl_int status, const char *operation) {
  require(status == CL_SUCCESS, std::string(operation) + " returned " + std::to_string(status));
}
void execute(bool binary_roundtrip) {
  Cleanup cleanup;
  cl_uint count = 0;
  check(clGetPlatformIDs(0, nullptr, &count), "platform count");
  require(count != 0, "no OpenCL platform");
  std::vector<cl_platform_id> platforms(count);
  check(clGetPlatformIDs(count, platforms.data(), nullptr), "platforms");
  cl_device_id device = nullptr;
  for (auto platform : platforms)
    if (clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS)
      break;
  require(device != nullptr, "no OpenCL GPU device");
  cl_int status = 0;
  auto context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &status);
  check(status, "context");
  cleanup.add([=] { clReleaseContext(context); });
  auto queue = clCreateCommandQueue(context, device, 0, &status);
  check(status, "queue");
  cleanup.add([=] { clReleaseCommandQueue(queue); });
  const char *source = R"cl(
    __kernel void stencil(__global const int *input, __global int *output, __local int *tile) {
      size_t i = get_global_id(0), l = get_local_id(0);
      tile[l] = input[i];
      barrier(CLK_LOCAL_MEM_FENCE);
      output[i] = tile[(l + 1) % get_local_size(0)] + (int)get_group_id(0);
    })cl";
  auto program = clCreateProgramWithSource(context, 1, &source, nullptr, &status);
  check(status, "source program");
  cleanup.add([=] { clReleaseProgram(program); });
  auto build = [&](cl_program value) {
    const auto result = clBuildProgram(value, 1, &device, "-cl-std=CL1.2", nullptr, nullptr);
    if (result != CL_SUCCESS) {
      std::size_t size = 0;
      clGetProgramBuildInfo(value, device, CL_PROGRAM_BUILD_LOG, 0, nullptr, &size);
      std::string log(size, '\0');
      clGetProgramBuildInfo(value, device, CL_PROGRAM_BUILD_LOG, size, log.data(), nullptr);
      throw std::runtime_error("OpenCL build failed: " + log);
    }
  };
  build(program);
  if (binary_roundtrip) {
    std::size_t size = 0;
    check(clGetProgramInfo(program, CL_PROGRAM_BINARY_SIZES, sizeof(size), &size, nullptr), "binary size");
    require(size > 0, "empty OpenCL binary");
    std::vector<unsigned char> bytes(size);
    auto *data = bytes.data();
    check(clGetProgramInfo(program, CL_PROGRAM_BINARIES, sizeof(data), &data, nullptr), "binary export");
    const unsigned char *image = data;
    cl_int binary_status = 0;
    program = clCreateProgramWithBinary(context, 1, &device, &size, &image, &binary_status, &status);
    check(status, "binary import");
    cleanup.add([=] { clReleaseProgram(program); });
    check(binary_status, "binary status");
    build(program);
  }
  auto kernel = clCreateKernel(program, "stencil", &status);
  check(status, "kernel");
  cleanup.add([=] { clReleaseKernel(kernel); });
  constexpr std::size_t size = 128, local = 32;
  auto values = input(size);
  std::vector<int> output(size, 0);
  auto source_buffer = clCreateBuffer(context, CL_MEM_READ_ONLY, size * sizeof(int), nullptr, &status);
  check(status, "input buffer");
  cleanup.add([=] { clReleaseMemObject(source_buffer); });
  auto destination = clCreateBuffer(context, CL_MEM_WRITE_ONLY, size * sizeof(int), nullptr, &status);
  check(status, "output buffer");
  cleanup.add([=] { clReleaseMemObject(destination); });
  cl_event uploaded{}, executed{}, downloaded{};
  check(clEnqueueWriteBuffer(queue, source_buffer, CL_FALSE, 0, size * sizeof(int), values.data(), 0, nullptr,
                             &uploaded),
        "upload");
  cleanup.add([=] { clReleaseEvent(uploaded); });
  check(clSetKernelArg(kernel, 0, sizeof(source_buffer), &source_buffer), "input argument");
  check(clSetKernelArg(kernel, 1, sizeof(destination), &destination), "output argument");
  check(clSetKernelArg(kernel, 2, local * sizeof(int), nullptr), "local argument");
  check(clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &size, &local, 1, &uploaded, &executed),
        "dispatch");
  cleanup.add([=] { clReleaseEvent(executed); });
  check(clEnqueueReadBuffer(queue, destination, CL_FALSE, 0, size * sizeof(int), output.data(), 1, &executed,
                            &downloaded),
        "download");
  cleanup.add([=] { clReleaseEvent(downloaded); });
  check(clWaitForEvents(1, &downloaded), "completion");
  for (std::size_t i = 0; i < size; ++i)
    require(output[i] == values[(i / local) * local + (i + 1) % local] + static_cast<int>(i / local),
            "local-memory/barrier result mismatch at " + std::to_string(i));
}
} // namespace
namespace unified_suite::program {
// Fixed API contract probes: their named geometry is independent of the portable size matrix.
std::vector<Case> edge_case_cases(const Configuration &) {
  return {
      {"edge-cases/runtime/opencl/source-local-barrier/i32/n128",
       [] { return capture([] { execute(false); }); }},
      {"edge-cases/runtime/opencl/binary-roundtrip/i32/n128", [] { return capture([] { execute(true); }); }}};
}
} // namespace unified_suite::program

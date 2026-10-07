#include "test_support.hpp"
#include <fmt/format.h>

#define CL_TARGET_OPENCL_VERSION 120
#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include <CL/cl.h>
#include <catch2/catch_test_macros.hpp>

#include "conformance/device.hpp"

namespace {
void check(cl_int status, const char *operation) {
  {
    INFO(fmt::format("{} returned {}", operation, status));
    REQUIRE(status == CL_SUCCESS);
  }
}
void execute(bool binaryRoundtrip) {
  unified::test_runtime::Cleanup cleanup;
  const auto device = reinterpret_cast<cl_device_id>(static_cast<uintptr_t>(conformance::deviceSelection().nativeHandle));
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
      size_t size = 0;
      clGetProgramBuildInfo(value, device, CL_PROGRAM_BUILD_LOG, 0, nullptr, &size);
      std::string log(size, '\0');
      clGetProgramBuildInfo(value, device, CL_PROGRAM_BUILD_LOG, size, log.data(), nullptr);
      throw std::runtime_error("OpenCL build failed: " + log);
    }
  };
  build(program);
  if (binaryRoundtrip) {
    size_t size = 0;
    check(clGetProgramInfo(program, CL_PROGRAM_BINARY_SIZES, sizeof(size), &size, nullptr), "binary size");
    {
      INFO("empty OpenCL binary");
      REQUIRE(size > 0);
    }
    std::vector<uint8_t> bytes(size);
    auto *data = bytes.data();
    check(clGetProgramInfo(program, CL_PROGRAM_BINARIES, sizeof(data), &data, nullptr), "binary export");
    const uint8_t *image = data;
    cl_int binaryStatus = 0;
    program = clCreateProgramWithBinary(context, 1, &device, &size, &image, &binaryStatus, &status);
    check(status, "binary import");
    cleanup.add([=] { clReleaseProgram(program); });
    check(binaryStatus, "binary status");
    build(program);
  }
  auto kernel = clCreateKernel(program, "stencil", &status);
  check(status, "kernel");
  cleanup.add([=] { clReleaseKernel(kernel); });
  constexpr size_t size = 128, local = 32;
  auto values = unified::test_runtime::input(size);
  std::vector<int32_t> output(size, 0);
  auto sourceBuffer = clCreateBuffer(context, CL_MEM_READ_ONLY, size * sizeof(int32_t), nullptr, &status);
  check(status, "input buffer");
  cleanup.add([=] { clReleaseMemObject(sourceBuffer); });
  auto destination = clCreateBuffer(context, CL_MEM_WRITE_ONLY, size * sizeof(int32_t), nullptr, &status);
  check(status, "output buffer");
  cleanup.add([=] { clReleaseMemObject(destination); });
  cl_event uploaded{}, executed{}, downloaded{};
  check(clEnqueueWriteBuffer(queue, sourceBuffer, CL_FALSE, 0, size * sizeof(int32_t), values.data(), 0, nullptr, &uploaded), "upload");
  cleanup.add([=] { clReleaseEvent(uploaded); });
  check(clSetKernelArg(kernel, 0, sizeof(sourceBuffer), &sourceBuffer), "input argument");
  check(clSetKernelArg(kernel, 1, sizeof(destination), &destination), "output argument");
  check(clSetKernelArg(kernel, 2, local * sizeof(int32_t), nullptr), "local argument");
  check(clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &size, &local, 1, &uploaded, &executed), "dispatch");
  cleanup.add([=] { clReleaseEvent(executed); });
  check(clEnqueueReadBuffer(queue, destination, CL_FALSE, 0, size * sizeof(int32_t), output.data(), 1, &executed, &downloaded), "download");
  cleanup.add([=] { clReleaseEvent(downloaded); });
  check(clWaitForEvents(1, &downloaded), "completion");
  for (size_t i = 0; i < size; ++i) {
    INFO(fmt::format("local-memory/barrier result mismatch at {}", i));
    REQUIRE(output[i] == values[(i / local) * local + (i + 1) % local] + static_cast<int32_t>(i / local));
  }
}
} // namespace

TEST_CASE("edge-cases/runtime/opencl/source-local-barrier/i32/n128", "[unified][runtime][compact]") { execute(false); }

TEST_CASE("edge-cases/runtime/opencl/binary-roundtrip/i32/n128", "[unified][runtime][compact]") { execute(true); }

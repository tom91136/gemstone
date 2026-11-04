#include <array>
#include <cstring>
#include <string>
#include <utility>

#include <CL/cl.h>
#include <catch2/catch_test_macros.hpp>

namespace {
constexpr cl_int Iterations = 8;
constexpr size_t Items = 16;

const char *source = R"CLC(
__kernel void count(volatile __global atomic_int *counter, volatile __global atomic_int *gate, int iterations) {
  for (int i = 0; i < iterations; i++)
    atomic_fetch_add_explicit(counter, 1, memory_order_relaxed, memory_scope_work_group);
  barrier(CLK_GLOBAL_MEM_FENCE);
  if (get_local_id(0) == 0) {
    atomic_fetch_add_explicit(gate, 1, memory_order_relaxed, memory_scope_device);
    while (atomic_load_explicit(gate, memory_order_relaxed, memory_scope_device) < 2) {}
  }
  barrier(CLK_GLOBAL_MEM_FENCE);
  for (int i = 0; i < iterations; i++)
    atomic_fetch_add_explicit(counter, 1, memory_order_relaxed, memory_scope_work_group);
}
)CLC";

struct Device {
  cl_device_id device = nullptr;
  cl_context context = nullptr;
  cl_program program = nullptr;
  Device() {
    cl_platform_id platform = nullptr;
    REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));
    REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_ALL, 1, &device, nullptr) == CL_SUCCESS));
    cl_int error = CL_SUCCESS;
    context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
    REQUIRE((context && error == CL_SUCCESS));
    program = clCreateProgramWithSource(context, 1, &source, nullptr, &error);
    REQUIRE((program && error == CL_SUCCESS));
    const cl_int build = clBuildProgram(program, 1, &device, "-cl-std=CL3.0", nullptr, nullptr);
    size_t size = 0;
    clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, 0, nullptr, &size);
    std::string log(size, '\0');
    clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, log.size(), log.data(), nullptr);
    INFO(log);
    REQUIRE((build == CL_SUCCESS));
  }
  Device(const Device &) = delete;
  Device &operator=(const Device &) = delete;
  Device(Device &&) = delete;
  Device &operator=(Device &&) = delete;
  ~Device() {
    clReleaseProgram(program);
    clReleaseContext(context);
  }
  [[nodiscard]] cl_command_queue queue() const {
    cl_int error = CL_SUCCESS;
    auto *const queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
    REQUIRE((queue && error == CL_SUCCESS));
    return queue;
  }
  [[nodiscard]] cl_kernel kernel() const {
    cl_int error = CL_SUCCESS;
    auto *const kernel = clCreateKernel(program, "count", &error);
    REQUIRE((kernel && error == CL_SUCCESS));
    return kernel;
  }
};

template <class Bind, class Read> void countConcurrently(const Device &gpu, Bind bind, Read read) {
  std::array<cl_command_queue, 2> queues{gpu.queue(), gpu.queue()};
  std::array<cl_kernel, 2> kernels{gpu.kernel(), gpu.kernel()};
  for (auto *const kernel : kernels) {
    bind(kernel);
    REQUIRE((clSetKernelArg(kernel, 2, sizeof(Iterations), &Iterations) == CL_SUCCESS));
  }
  for (size_t index = 0; index < queues.size(); ++index)
    REQUIRE((clEnqueueNDRangeKernel(queues[index], kernels[index], 1, nullptr, &Items, &Items, 0, nullptr, nullptr) == CL_SUCCESS));
  for (auto *const queue : queues)
    REQUIRE((clFinish(queue) == CL_SUCCESS));
  const auto [counter, gate] = read(queues[0]);
  CHECK(gate == 2);
  CHECK(counter == 2 * 2 * static_cast<int>(Items) * Iterations);
  for (auto *const kernel : kernels)
    clReleaseKernel(kernel);
  for (auto *const queue : queues)
    clReleaseCommandQueue(queue);
}
} // namespace

TEST_CASE("OpenCL workgroup-scope atomics on fine-grained SVM are shared by concurrent dispatches", "[opencl][integration]") {
  const Device gpu;
  auto *const words = static_cast<int *>(
      clSVMAlloc(gpu.context, CL_MEM_READ_WRITE | CL_MEM_SVM_FINE_GRAIN_BUFFER | CL_MEM_SVM_ATOMICS, 2 * sizeof(int), 64));
  REQUIRE(words);
  words[0] = 0;
  words[1] = 0;
  countConcurrently(
      gpu,
      [&](cl_kernel kernel) {
        REQUIRE((clSetKernelArgSVMPointer(kernel, 0, words) == CL_SUCCESS));
        REQUIRE((clSetKernelArgSVMPointer(kernel, 1, words + 1) == CL_SUCCESS));
      },
      [&](cl_command_queue) { return std::pair<int, int>{words[0], words[1]}; });
  clSVMFree(gpu.context, words);
}

TEST_CASE("OpenCL workgroup-scope atomics on a host pointer buffer are shared by concurrent dispatches", "[opencl][integration]") {
  const Device gpu;
  alignas(64) static int words[16] = {};
  words[0] = 0;
  words[8] = 0;
  cl_int error = CL_SUCCESS;
  auto *const counter = clCreateBuffer(gpu.context, CL_MEM_READ_WRITE | CL_MEM_USE_HOST_PTR, sizeof(int), words, &error);
  REQUIRE((counter && error == CL_SUCCESS));
  auto *const gate = clCreateBuffer(gpu.context, CL_MEM_READ_WRITE | CL_MEM_USE_HOST_PTR, sizeof(int), words + 8, &error);
  REQUIRE((gate && error == CL_SUCCESS));
  countConcurrently(
      gpu,
      [&](cl_kernel kernel) {
        REQUIRE((clSetKernelArg(kernel, 0, sizeof(counter), &counter) == CL_SUCCESS));
        REQUIRE((clSetKernelArg(kernel, 1, sizeof(gate), &gate) == CL_SUCCESS));
      },
      [&](cl_command_queue queue) {
        const auto value = [&](cl_mem buffer) {
          cl_int status = CL_SUCCESS;
          auto *const mapped =
              static_cast<int *>(clEnqueueMapBuffer(queue, buffer, CL_TRUE, CL_MAP_READ, 0, sizeof(int), 0, nullptr, nullptr, &status));
          REQUIRE((mapped && status == CL_SUCCESS));
          const int result = *mapped;
          REQUIRE((clEnqueueUnmapMemObject(queue, buffer, mapped, 0, nullptr, nullptr) == CL_SUCCESS));
          REQUIRE((clFinish(queue) == CL_SUCCESS));
          return result;
        };
        return std::pair<int, int>{value(counter), value(gate)};
      });
  clReleaseMemObject(counter);
  clReleaseMemObject(gate);
}

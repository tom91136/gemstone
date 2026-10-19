#include <array>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <memory>

#include <CL/cl.h>
#include <CL/cl_ext.h>
#include <catch2/catch_test_macros.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include "session.hpp"

using gemstone_opencl_test::gpu;

namespace {
struct Freed {
  void operator()(void *pointer) const { std::free(pointer); }
};

struct Session {
  gemstone_test::RpcFixture server;
  gemstone_test::RpcEnvironment environment{server};
  cl_device_id device = gpu();
  cl_context context = nullptr;
  cl_command_queue queue = nullptr;
  cl_program program = nullptr;
  cl_kernel kernel = nullptr;
  Session() {
    cl_int error = CL_SUCCESS;
    context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
    REQUIRE((context && error == CL_SUCCESS));
    queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
    REQUIRE((queue && error == CL_SUCCESS));
    server.programCatalogue({{.name = "groups", .linkageName = "groups"}}, {});
    program = clCreateProgramWithIL(context, gemstone_test::RpcFixture::Spirv, sizeof(gemstone_test::RpcFixture::Spirv), &error);
    REQUIRE((program && error == CL_SUCCESS));
    REQUIRE((clBuildProgram(program, 1, &device, "", nullptr, nullptr) == CL_SUCCESS));
    kernel = clCreateKernel(program, "groups", &error);
    REQUIRE((kernel && error == CL_SUCCESS));
  }
  ~Session() {
    clFinish(queue);
    clReleaseKernel(kernel);
    clReleaseProgram(program);
    clReleaseCommandQueue(queue);
    clReleaseContext(context);
  }
  void launch(uint32_t seed) {
    REQUIRE((clSetKernelArg(kernel, 1, sizeof(seed), &seed) == CL_SUCCESS));
    server.expect({.local = 4, .groups = 1, .global = 4, .offset = 0, .seed = seed});
    const size_t global = 4;
    const size_t local = 4;
    REQUIRE((clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &global, &local, 0, nullptr, nullptr) == CL_SUCCESS));
    REQUIRE((clFinish(queue) == CL_SUCCESS));
    server.check();
  }
};
} // namespace

TEST_CASE("OpenCL advertises fine-grained system SVM and Intel shared system memory", "[opencl][svm]") {
  auto *const device = gpu();
  cl_device_svm_capabilities svm = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_SVM_CAPABILITIES, sizeof(svm), &svm, nullptr) == CL_SUCCESS));
  CHECK((svm & CL_DEVICE_SVM_FINE_GRAIN_SYSTEM) != 0);
  CHECK((svm & CL_DEVICE_SVM_ATOMICS) != 0);
  cl_device_unified_shared_memory_capabilities_intel system = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_SHARED_SYSTEM_MEM_CAPABILITIES_INTEL, sizeof(system), &system, nullptr) == CL_SUCCESS));
  CHECK(system
        == (CL_UNIFIED_SHARED_MEMORY_ACCESS_INTEL | CL_UNIFIED_SHARED_MEMORY_ATOMIC_ACCESS_INTEL
            | CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ACCESS_INTEL | CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ATOMIC_ACCESS_INTEL));
}

TEST_CASE("OpenCL kernels write through ordinary heap and stack pointers", "[opencl][svm]") {
  Session session;
  std::unique_ptr<uint32_t, Freed> const heap(static_cast<uint32_t *>(std::aligned_alloc(64, 64)));
  std::memset(heap.get(), 0, 64);
  REQUIRE((clSetKernelArgSVMPointer(session.kernel, 0, heap.get() + 2) == CL_SUCCESS));
  session.launch(11);
  CHECK(session.server.hostMemory());
  for (uint32_t index = 0; index < 4; ++index)
    CHECK(heap.get()[2 + index] == 11 + index);
  CHECK(heap.get()[1] == 0);

  std::array<uint32_t, 4> stack{};
  cl_platform_id platform = nullptr;
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_PLATFORM, sizeof(platform), &platform, nullptr) == CL_SUCCESS));
  auto setPointer = reinterpret_cast<decltype(&clSetKernelArgMemPointerINTEL)>(
      clGetExtensionFunctionAddressForPlatform(platform, "clSetKernelArgMemPointerINTEL"));
  REQUIRE(setPointer);
  REQUIRE((setPointer(session.kernel, 0, stack.data()) == CL_SUCCESS));
  session.launch(29);
  for (uint32_t index = 0; index < 4; ++index)
    CHECK(stack[index] == 29 + index);
}

TEST_CASE("OpenCL launches over device buffers leave host memory access disabled", "[opencl][svm]") {
  Session session;
  cl_int error = CL_SUCCESS;
  auto *buffer = clCreateBuffer(session.context, CL_MEM_READ_WRITE, 16, nullptr, &error);
  REQUIRE((buffer && error == CL_SUCCESS));
  REQUIRE((clSetKernelArg(session.kernel, 0, sizeof(buffer), &buffer) == CL_SUCCESS));
  session.launch(5);
  CHECK_FALSE(session.server.hostMemory());
  clReleaseMemObject(buffer);
}

TEST_CASE("OpenCL SVM commands and execution info accept ordinary host pointers", "[opencl][svm]") {
  Session const session;
  std::unique_ptr<uint32_t, Freed> const source(static_cast<uint32_t *>(std::malloc(16)));
  std::unique_ptr<uint32_t, Freed> const target(static_cast<uint32_t *>(std::malloc(16)));
  void *pointers[] = {source.get(), target.get()};
  CHECK(clSetKernelExecInfo(session.kernel, CL_KERNEL_EXEC_INFO_SVM_PTRS, sizeof(pointers), pointers) == CL_SUCCESS);
  const cl_bool system = CL_TRUE;
  CHECK(clSetKernelExecInfo(session.kernel, CL_KERNEL_EXEC_INFO_SVM_FINE_GRAIN_SYSTEM, sizeof(system), &system) == CL_SUCCESS);

  const uint32_t pattern = 0xa1b2c3d4;
  REQUIRE((clEnqueueSVMMemFill(session.queue, source.get(), &pattern, sizeof(pattern), 16, 0, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((clEnqueueSVMMemcpy(session.queue, CL_TRUE, target.get(), source.get(), 16, 0, nullptr, nullptr) == CL_SUCCESS));
  for (uint32_t index = 0; index < 4; ++index)
    CHECK(target.get()[index] == pattern);
  CHECK(clEnqueueSVMMap(session.queue, CL_TRUE, CL_MAP_READ | CL_MAP_WRITE, source.get(), 16, 0, nullptr, nullptr) == CL_SUCCESS);
  CHECK(clEnqueueSVMUnmap(session.queue, source.get(), 0, nullptr, nullptr) == CL_SUCCESS);
  const void *migrated[] = {source.get()};
  CHECK(clEnqueueSVMMigrateMem(session.queue, 1, migrated, nullptr, 0, 0, nullptr, nullptr) == CL_SUCCESS);
  CHECK(clFinish(session.queue) == CL_SUCCESS);
}

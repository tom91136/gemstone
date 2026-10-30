#include <algorithm>
#include <cstddef>
#include <string>
#include <string_view>
#include <variant>
#include <vector>

#include <CL/cl.h>
#include <aspartame/vector.hpp>
#include <catch2/catch_test_macros.hpp>

#include "gemstone/test_support.hpp"

#include "../../rpc/test/rpc_fixture.hpp"
#include "rpc/gemstone_execution_rpc.hpp"
#include "session.hpp"
#include <environment_keys.hpp>

using gemstone_opencl_test::gpu;
using gemstone_opencl_test::hasFeature;
using gemstone_opencl_test::info;
using gemstone_opencl_test::requireDeviceEnqueue;

namespace {
template <class T> T queueInfo(cl_command_queue queue, cl_command_queue_info name) {
  T value{};
  REQUIRE((clGetCommandQueueInfo(queue, name, sizeof(value), &value, nullptr) == CL_SUCCESS));
  return value;
}

constexpr cl_command_queue_properties DeviceQueue = CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE | CL_QUEUE_ON_DEVICE;

struct Session {
  gemstone_test::RpcFixture server;
  gemstone_test::RpcEnvironment environment{server};
  cl_device_id device = gpu();
  cl_context context = nullptr;
  cl_command_queue queue = nullptr;
  std::vector<cl_command_queue> created;
  Session() {
    cl_int error = CL_SUCCESS;
    context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
    REQUIRE((context && error == CL_SUCCESS));
    queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
    REQUIRE((queue && error == CL_SUCCESS));
  }
  ~Session() {
    for (auto *value : created)
      clReleaseCommandQueue(value);
    clFinish(queue);
    clReleaseCommandQueue(queue);
    clReleaseContext(context);
  }
  cl_int create(std::vector<cl_queue_properties> properties, cl_command_queue *result = nullptr) {
    properties.push_back(0);
    cl_int error = CL_SUCCESS;
    auto *value = clCreateCommandQueueWithProperties(context, device, properties.data(), &error);
    if (value) created.push_back(value);
    if (result) *result = value;
    return error;
  }
  cl_command_queue deviceQueue(cl_command_queue_properties extra = 0, cl_uint size = 0) {
    std::vector<cl_queue_properties> properties{CL_QUEUE_PROPERTIES, DeviceQueue | extra};
    if (size) properties.insert(properties.end(), {CL_QUEUE_SIZE, size});
    cl_command_queue value = nullptr;
    REQUIRE((create(properties, &value) == CL_SUCCESS));
    return value;
  }
  cl_program source(const char *text, const char *options) const {
    cl_int error = CL_SUCCESS;
    auto *program = clCreateProgramWithSource(context, 1, &text, nullptr, &error);
    REQUIRE((program && error == CL_SUCCESS));
    const auto status = clBuildProgram(program, 1, &device, options, nullptr, nullptr);
    size_t size = 0;
    clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, 0, nullptr, &size);
    std::string log(size, '\0');
    clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, size, log.data(), nullptr);
    INFO(log);
    REQUIRE(status == CL_SUCCESS);
    return program;
  }
};

constexpr const char *SpawnSource = R"CLC(
  #ifndef __opencl_c_device_enqueue
  #error device-side enqueue is not enabled
  #endif
  kernel void spawn(queue_t queue, global int *out) {
    clk_event_t done;
    enqueue_kernel(queue, CLK_ENQUEUE_FLAGS_NO_WAIT, ndrange_1D(4), 0, NULL, &done, ^{ out[get_global_id(0)] = 1; });
    enqueue_marker(get_default_queue(), 1, &done, NULL);
    release_event(done);
  })CLC";
} // namespace

TEST_CASE("OpenCL advertises device-side enqueue with the required limits", "[opencl][device-enqueue]") {
  auto *const device = gpu();
  requireDeviceEnqueue(device);
  const auto capabilities = info<cl_device_device_enqueue_capabilities>(device, CL_DEVICE_DEVICE_ENQUEUE_CAPABILITIES);
  CHECK((capabilities & CL_DEVICE_QUEUE_SUPPORTED) != 0);
  CHECK((capabilities & CL_DEVICE_QUEUE_REPLACEABLE_DEFAULT) != 0);
  const auto properties = info<cl_command_queue_properties>(device, CL_DEVICE_QUEUE_ON_DEVICE_PROPERTIES);
  CHECK((properties & CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE) != 0);
  CHECK((properties & CL_QUEUE_PROFILING_ENABLE) != 0);
  const auto preferred = info<cl_uint>(device, CL_DEVICE_QUEUE_ON_DEVICE_PREFERRED_SIZE);
  CHECK(preferred >= 16 * 1024);
  CHECK(info<cl_uint>(device, CL_DEVICE_QUEUE_ON_DEVICE_MAX_SIZE) >= std::max<cl_uint>(preferred, 256 * 1024));
  CHECK(info<cl_uint>(device, CL_DEVICE_MAX_ON_DEVICE_QUEUES) >= 1);
  CHECK(info<cl_uint>(device, CL_DEVICE_MAX_ON_DEVICE_EVENTS) >= 1024);
  CHECK(hasFeature(device, "__opencl_c_device_enqueue"));
}

TEST_CASE("OpenCL reports device-side enqueue as unsupported without a SPIR-V translator", "[opencl][device-enqueue]") {
  const gemstone_test::Environment translator(gemstone::environment::OpenclLlvmSpirvExe, "none");
  Session session;
  auto *const device = session.device;
  CHECK(info<cl_device_device_enqueue_capabilities>(device, CL_DEVICE_DEVICE_ENQUEUE_CAPABILITIES) == 0);
  CHECK(info<cl_command_queue_properties>(device, CL_DEVICE_QUEUE_ON_DEVICE_PROPERTIES) == 0);
  CHECK(info<cl_uint>(device, CL_DEVICE_QUEUE_ON_DEVICE_PREFERRED_SIZE) == 0);
  CHECK(info<cl_uint>(device, CL_DEVICE_QUEUE_ON_DEVICE_MAX_SIZE) == 0);
  CHECK(info<cl_uint>(device, CL_DEVICE_MAX_ON_DEVICE_QUEUES) == 0);
  CHECK(info<cl_uint>(device, CL_DEVICE_MAX_ON_DEVICE_EVENTS) == 0);
  CHECK(!hasFeature(device, "__opencl_c_device_enqueue"));
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_VERSION, 0, nullptr, &size) == CL_SUCCESS));
  std::string version(size, '\0');
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_VERSION, size, version.data(), nullptr) == CL_SUCCESS));
  CHECK(version.rfind("OpenCL C 1.2 ", 0) == 0);
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_ALL_VERSIONS, 0, nullptr, &size) == CL_SUCCESS));
  std::vector<cl_name_version> versions(size / sizeof(cl_name_version));
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_ALL_VERSIONS, size, versions.data(), nullptr) == CL_SUCCESS));
  CHECK(!(versions ^ aspartame::exists([](const cl_name_version &entry) { return entry.version == CL_MAKE_VERSION(2, 0, 0); })));
  CHECK(session.create({CL_QUEUE_PROPERTIES, DeviceQueue}) == CL_INVALID_QUEUE_PROPERTIES);

  cl_int error = CL_SUCCESS;
  const char *text = SpawnSource;
  auto *program = clCreateProgramWithSource(session.context, 1, &text, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  CHECK(clBuildProgram(program, 1, &device, "-cl-std=CL3.0", nullptr, nullptr) == CL_BUILD_PROGRAM_FAILURE);
  REQUIRE((clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, 0, nullptr, &size) == CL_SUCCESS));
  std::string log(size, '\0');
  REQUIRE((clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, size, log.data(), nullptr) == CL_SUCCESS));
  CHECK(log.contains("device-side enqueue is not enabled"));
  clReleaseProgram(program);
}

TEST_CASE("OpenCL on-device queues report their size and track the default queue", "[opencl][device-enqueue]") {
  Session session;
  requireDeviceEnqueue(session.device);
  const auto preferred = info<cl_uint>(session.device, CL_DEVICE_QUEUE_ON_DEVICE_PREFERRED_SIZE);
  auto *fallback = session.deviceQueue(CL_QUEUE_ON_DEVICE_DEFAULT);
  CHECK(queueInfo<cl_uint>(fallback, CL_QUEUE_SIZE) == preferred);
  CHECK(queueInfo<cl_command_queue_properties>(fallback, CL_QUEUE_PROPERTIES) == (DeviceQueue | CL_QUEUE_ON_DEVICE_DEFAULT));
  CHECK(queueInfo<cl_command_queue>(fallback, CL_QUEUE_DEVICE_DEFAULT) == fallback);
  CHECK(queueInfo<cl_command_queue>(session.queue, CL_QUEUE_DEVICE_DEFAULT) == fallback);
  CHECK(session.deviceQueue(CL_QUEUE_ON_DEVICE_DEFAULT) == fallback);

  auto *sized = session.deviceQueue(CL_QUEUE_PROFILING_ENABLE, 4096);
  CHECK(queueInfo<cl_uint>(sized, CL_QUEUE_SIZE) == 4096);
  REQUIRE((clSetDefaultDeviceCommandQueue(session.context, session.device, sized) == CL_SUCCESS));
  CHECK(queueInfo<cl_command_queue>(session.queue, CL_QUEUE_DEVICE_DEFAULT) == sized);
  CHECK(clSetDefaultDeviceCommandQueue(session.context, session.device, session.queue) == CL_INVALID_COMMAND_QUEUE);

  cl_uint size = 0;
  CHECK(clGetCommandQueueInfo(session.queue, CL_QUEUE_SIZE, sizeof(size), &size, nullptr) == CL_INVALID_COMMAND_QUEUE);
  CHECK(clFinish(sized) == CL_INVALID_COMMAND_QUEUE);
  CHECK(clFlush(sized) == CL_INVALID_COMMAND_QUEUE);
  CHECK(clEnqueueMarkerWithWaitList(sized, 0, nullptr, nullptr) == CL_INVALID_COMMAND_QUEUE);
}

TEST_CASE("OpenCL rejects invalid on-device queue properties", "[opencl][device-enqueue]") {
  Session session;
  requireDeviceEnqueue(session.device);
  const auto maximum = info<cl_uint>(session.device, CL_DEVICE_QUEUE_ON_DEVICE_MAX_SIZE);
  CHECK(session.create({CL_QUEUE_PROPERTIES, CL_QUEUE_ON_DEVICE}) == CL_INVALID_VALUE);
  CHECK(session.create({CL_QUEUE_PROPERTIES, CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE | CL_QUEUE_ON_DEVICE_DEFAULT}) == CL_INVALID_VALUE);
  CHECK(session.create({CL_QUEUE_PROPERTIES, 0, CL_QUEUE_SIZE, 4096}) == CL_INVALID_VALUE);
  CHECK(session.create({CL_QUEUE_PROPERTIES, DeviceQueue, CL_QUEUE_SIZE, maximum + 1}) == CL_INVALID_VALUE);
}

TEST_CASE("OpenCL queue_t kernel arguments take device queues", "[opencl][device-enqueue]") {
  Session session;
  requireDeviceEnqueue(session.device);
  session.server.opaqueLaunches();
  session.server.programCatalogue({{.name = "spawn", .linkageName = "spawn"}}, {});
  session.server.kernelMetadata(
      16, 2, {{.width = 8, .offset = 0, .kind = 1, .containsPointers = 0}, {.width = 8, .offset = 8, .kind = 2, .containsPointers = 0}});
  auto *fallback = session.deviceQueue(CL_QUEUE_ON_DEVICE_DEFAULT);
  auto *program = session.source(SpawnSource, "-cl-std=CL3.0");
  cl_int error = CL_SUCCESS;
  auto *kernel = clCreateKernel(program, "spawn", &error);
  REQUIRE((kernel && error == CL_SUCCESS));
  cl_command_queue missing = nullptr;
  CHECK(clSetKernelArg(kernel, 0, sizeof(session.queue), &session.queue) == CL_INVALID_DEVICE_QUEUE);
  CHECK(clSetKernelArg(kernel, 0, sizeof(missing), &missing) == CL_INVALID_DEVICE_QUEUE);
  REQUIRE((clSetKernelArg(kernel, 0, sizeof(fallback), &fallback) == CL_SUCCESS));
  auto *out = clCreateBuffer(session.context, CL_MEM_READ_WRITE, 16, nullptr, &error);
  REQUIRE((out && error == CL_SUCCESS));
  REQUIRE((clSetKernelArg(kernel, 1, sizeof(out), &out) == CL_SUCCESS));
  const size_t global = 1;
  REQUIRE((clEnqueueNDRangeKernel(session.queue, kernel, 1, nullptr, &global, nullptr, 0, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((clFinish(session.queue) == CL_SUCCESS));
  const auto launch = session.server.lastLaunch();
  REQUIRE(launch.arguments.size() == 2);
  const auto *queue = std::get_if<gemstone::execution::ArgumentScalar>(launch.arguments.data());
  REQUIRE(queue);
  CHECK(queue->bytes.value.size() == 8);
  clReleaseMemObject(out);
  clReleaseKernel(kernel);
  clReleaseProgram(program);
}

TEST_CASE("OpenCL creates the reported number of device queues alongside the default queue", "[opencl][device-enqueue]") {
  Session session;
  requireDeviceEnqueue(session.device);
  session.deviceQueue(CL_QUEUE_ON_DEVICE_DEFAULT);
  for (cl_uint index = 0; index < info<cl_uint>(session.device, CL_DEVICE_MAX_ON_DEVICE_QUEUES); ++index)
    session.deviceQueue();
}

TEST_CASE("OpenCL profiling reports when commands and their child kernels complete", "[opencl][device-enqueue]") {
  Session const session;
  const cl_queue_properties properties[] = {CL_QUEUE_PROPERTIES, CL_QUEUE_PROFILING_ENABLE, 0};
  cl_int error = CL_SUCCESS;
  auto *queue = clCreateCommandQueueWithProperties(session.context, session.device, properties, &error);
  REQUIRE((queue && error == CL_SUCCESS));
  cl_event marker = nullptr;
  REQUIRE((clEnqueueMarkerWithWaitList(queue, 0, nullptr, &marker) == CL_SUCCESS));
  REQUIRE((clWaitForEvents(1, &marker) == CL_SUCCESS));
  cl_ulong ended = 0;
  cl_ulong completed = 0;
  REQUIRE((clGetEventProfilingInfo(marker, CL_PROFILING_COMMAND_END, sizeof(ended), &ended, nullptr) == CL_SUCCESS));
  REQUIRE((clGetEventProfilingInfo(marker, CL_PROFILING_COMMAND_COMPLETE, sizeof(completed), &completed, nullptr) == CL_SUCCESS));
  CHECK(completed >= ended);
  clReleaseEvent(marker);
  clReleaseCommandQueue(queue);
}

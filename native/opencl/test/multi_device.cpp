#include <algorithm>
#include <array>
#include <cstdint>
#include <cstring>
#include <string>
#include <vector>

#include <CL/cl.h>
#include <CL/cl_ext.h>
#include <catch2/catch_test_macros.hpp>

#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

namespace {

gemstone_test::RpcFixture fixture;
const std::string Endpoint = fixture.endpoint();
const gemstone_test::Environment RpcEndpoint(gemstone::environment::RpcEndpoint, Endpoint.c_str());
const gemstone_test::Environment RpcClient(gemstone::environment::RpcClientId, "42");
const gemstone_test::Environment DeviceCount(gemstone::environment::DeviceCount, "2");

struct Node {
  cl_platform_id platform;
  std::array<cl_device_id, 2> devices;
};

Node enumerate() {
  Node node{};
  REQUIRE((clGetPlatformIDs(1, &node.platform, nullptr) == CL_SUCCESS));
  cl_uint count = 0;
  REQUIRE((clGetDeviceIDs(node.platform, CL_DEVICE_TYPE_GPU, 0, nullptr, &count) == CL_SUCCESS));
  REQUIRE(count == 2);
  REQUIRE((clGetDeviceIDs(node.platform, CL_DEVICE_TYPE_GPU, 2, node.devices.data(), nullptr) == CL_SUCCESS));
  return node;
}

template <class T> T info(cl_device_id device, cl_device_info name) {
  T value{};
  REQUIRE((clGetDeviceInfo(device, name, sizeof(value), &value, nullptr) == CL_SUCCESS));
  return value;
}

std::array<std::vector<unsigned char>, 2> binariesOf(cl_program program) {
  std::array<size_t, 2> sizes{};
  REQUIRE((clGetProgramInfo(program, CL_PROGRAM_BINARY_SIZES, sizeof(sizes), sizes.data(), nullptr) == CL_SUCCESS));
  std::array<std::vector<unsigned char>, 2> binaries{std::vector<unsigned char>(sizes[0]), std::vector<unsigned char>(sizes[1])};
  std::array<unsigned char *, 2> destinations{binaries[0].data(), binaries[1].data()};
  REQUIRE((clGetProgramInfo(program, CL_PROGRAM_BINARIES, sizeof(destinations), destinations.data(), nullptr) == CL_SUCCESS));
  return binaries;
}

std::array<std::vector<unsigned char>, 2> programBinaries(cl_context context, const std::array<cl_device_id, 2> &devices,
                                                          const char *source) {
  cl_int error = CL_SUCCESS;
  auto *program = clCreateProgramWithSource(context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE((clBuildProgram(program, 2, devices.data(), "", nullptr, nullptr) == CL_SUCCESS));
  const auto binaries = binariesOf(program);
  REQUIRE((clReleaseProgram(program) == CL_SUCCESS));
  return binaries;
}

std::vector<uint8_t> launchedImage(cl_context context, cl_device_id device, cl_kernel kernel) {
  cl_int error = CL_SUCCESS;
  auto *queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
  REQUIRE((queue && error == CL_SUCCESS));
  auto *buffer = clCreateBuffer(context, CL_MEM_READ_WRITE, sizeof(int32_t), nullptr, &error);
  REQUIRE((buffer && error == CL_SUCCESS));
  REQUIRE((clSetKernelArg(kernel, 0, sizeof(buffer), &buffer) == CL_SUCCESS));
  const int32_t seed = 0;
  REQUIRE((clSetKernelArg(kernel, 1, sizeof(seed), &seed) == CL_SUCCESS));
  const size_t global = 1;
  REQUIRE((clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &global, nullptr, 0, nullptr, nullptr) == CL_SUCCESS));
  int32_t value = 0;
  REQUIRE((clEnqueueReadBuffer(queue, buffer, CL_TRUE, 0, sizeof(value), &value, 0, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((clReleaseMemObject(buffer) == CL_SUCCESS));
  REQUIRE((clReleaseCommandQueue(queue) == CL_SUCCESS));
  return fixture.programImageOf(fixture.lastLaunch().program);
}

bool contains(const std::vector<unsigned char> &binary, const std::vector<uint8_t> &image) {
  return std::search(binary.begin(), binary.end(), image.begin(), image.end()) != binary.end();
}

} // namespace

TEST_CASE("OpenCL enumerates the configured number of GPU devices", "[opencl][multi-device]") {
  cl_platform_id platform = nullptr;
  REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));

  cl_device_id single = nullptr;
  cl_uint count = 0;
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &single, &count) == CL_SUCCESS));
  CHECK(count == 2);
  CHECK(single != nullptr);

  std::array<cl_device_id, 3> devices{};
  count = 0;
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 3, devices.data(), &count) == CL_SUCCESS));
  CHECK(count == 2);
  CHECK(devices[0] == single);
  CHECK(devices[1] != nullptr);
  CHECK(devices[0] != devices[1]);
}

TEST_CASE("OpenCL devices report distinct uuid and pci identities", "[opencl][multi-device]") {
  const auto node = enumerate();

  std::array<cl_uchar, CL_UUID_SIZE_KHR> first{};
  std::array<cl_uchar, CL_UUID_SIZE_KHR> second{};
  REQUIRE((clGetDeviceInfo(node.devices[0], CL_DEVICE_UUID_KHR, first.size(), first.data(), nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceInfo(node.devices[1], CL_DEVICE_UUID_KHR, second.size(), second.data(), nullptr) == CL_SUCCESS));
  CHECK(std::memcmp(first.data(), second.data(), first.size()) != 0);

  const auto firstPci = info<cl_device_pci_bus_info_khr>(node.devices[0], CL_DEVICE_PCI_BUS_INFO_KHR);
  const auto secondPci = info<cl_device_pci_bus_info_khr>(node.devices[1], CL_DEVICE_PCI_BUS_INFO_KHR);
  CHECK(firstPci.pci_bus != secondPci.pci_bus);
}

TEST_CASE("OpenCL contexts span several devices", "[opencl][multi-device]") {
  const auto node = enumerate();
  cl_int error = CL_SUCCESS;
  auto *context = clCreateContext(nullptr, 2, node.devices.data(), nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));

  cl_uint numDevices = 0;
  REQUIRE((clGetContextInfo(context, CL_CONTEXT_NUM_DEVICES, sizeof(numDevices), &numDevices, nullptr) == CL_SUCCESS));
  CHECK(numDevices == 2);

  std::array<cl_device_id, 2> contextDevices{};
  REQUIRE((clGetContextInfo(context, CL_CONTEXT_DEVICES, sizeof(contextDevices), contextDevices.data(), nullptr) == CL_SUCCESS));
  CHECK(contextDevices == node.devices);

  for (auto *device : node.devices) {
    auto *queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
    REQUIRE((queue && error == CL_SUCCESS));
    cl_device_id queued = nullptr;
    REQUIRE((clGetCommandQueueInfo(queue, CL_QUEUE_DEVICE, sizeof(queued), &queued, nullptr) == CL_SUCCESS));
    CHECK(queued == device);
    REQUIRE((clReleaseCommandQueue(queue) == CL_SUCCESS));
  }

  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

TEST_CASE("OpenCL reports the owning device of a device USM allocation and copies across devices", "[opencl][multi-device]") {
  const auto node = enumerate();
  cl_int error = CL_SUCCESS;
  auto *context = clCreateContext(nullptr, 2, node.devices.data(), nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));
  auto *queueSource = clCreateCommandQueueWithProperties(context, node.devices[1], nullptr, &error);
  REQUIRE((queueSource && error == CL_SUCCESS));
  auto *queueDestination = clCreateCommandQueueWithProperties(context, node.devices[0], nullptr, &error);
  REQUIRE((queueDestination && error == CL_SUCCESS));

  auto *source = clDeviceMemAllocINTEL(context, node.devices[1], nullptr, 64, 64, &error);
  REQUIRE((source && error == CL_SUCCESS));
  auto *destination = clDeviceMemAllocINTEL(context, node.devices[0], nullptr, 64, 64, &error);
  REQUIRE((destination && error == CL_SUCCESS));

  cl_device_id owner = nullptr;
  REQUIRE((clGetMemAllocInfoINTEL(context, source, CL_MEM_ALLOC_DEVICE_INTEL, sizeof(owner), &owner, nullptr) == CL_SUCCESS));
  CHECK(owner == node.devices[1]);
  REQUIRE((clGetMemAllocInfoINTEL(context, destination, CL_MEM_ALLOC_DEVICE_INTEL, sizeof(owner), &owner, nullptr) == CL_SUCCESS));
  CHECK(owner == node.devices[0]);

  std::array<uint8_t, 64> expected{};
  for (size_t i = 0; i < expected.size(); ++i)
    expected[i] = static_cast<uint8_t>(i + 1);
  REQUIRE((clEnqueueMemcpyINTEL(queueSource, CL_TRUE, source, expected.data(), expected.size(), 0, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((clEnqueueMemcpyINTEL(queueDestination, CL_TRUE, destination, source, expected.size(), 0, nullptr, nullptr) == CL_SUCCESS));

  std::array<uint8_t, 64> actual{};
  REQUIRE((clEnqueueMemcpyINTEL(queueDestination, CL_TRUE, actual.data(), destination, actual.size(), 0, nullptr, nullptr) == CL_SUCCESS));
  CHECK(actual == expected);

  REQUIRE((clMemFreeINTEL(context, source) == CL_SUCCESS));
  REQUIRE((clMemFreeINTEL(context, destination) == CL_SUCCESS));
  REQUIRE((clReleaseCommandQueue(queueSource) == CL_SUCCESS));
  REQUIRE((clReleaseCommandQueue(queueDestination) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

TEST_CASE("OpenCL program binaries carry one entry per device and reload for every device", "[opencl][multi-device]") {
  const auto node = enumerate();
  cl_int error = CL_SUCCESS;
  auto *context = clCreateContext(nullptr, 2, node.devices.data(), nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));

  fixture.programCatalogue({{.name = "step", .linkageName = "step"}}, {});
  const auto binaries = programBinaries(context, node.devices, "kernel void step(global int *out) { out[0] = 7; }");
  CHECK(!binaries[0].empty());
  CHECK(!binaries[1].empty());

  const std::array<size_t, 2> lengths{binaries[0].size(), binaries[1].size()};
  const unsigned char *data[] = {binaries[0].data(), binaries[1].data()};
  std::array<cl_int, 2> statuses{};
  auto *program = clCreateProgramWithBinary(context, 2, node.devices.data(), lengths.data(), data, statuses.data(), &error);
  REQUIRE((program && error == CL_SUCCESS));
  CHECK(statuses == std::array<cl_int, 2>{CL_SUCCESS, CL_SUCCESS});
  REQUIRE((clBuildProgram(program, 2, node.devices.data(), "", nullptr, nullptr) == CL_SUCCESS));
  auto *kernel = clCreateKernel(program, "step", &error);
  CHECK(error == CL_SUCCESS);
  CHECK(kernel != nullptr);
  REQUIRE((clReleaseKernel(kernel) == CL_SUCCESS));
  REQUIRE((clReleaseProgram(program) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

TEST_CASE("OpenCL keeps each device's program from a multi-device program binary", "[opencl][multi-device]") {
  const auto node = enumerate();
  cl_int error = CL_SUCCESS;
  auto *context = clCreateContext(nullptr, 2, node.devices.data(), nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));

  fixture.programCatalogue({{.name = "step", .linkageName = "step"}}, {});
  const auto seven = programBinaries(context, node.devices, "kernel void step(global int *out) { out[0] = 7; }");
  const auto nine = programBinaries(context, node.devices, "kernel void step(global int *out) { out[0] = 9; }");
  REQUIRE(seven[0] != nine[1]);

  const std::array<size_t, 2> lengths{seven[0].size(), nine[1].size()};
  const unsigned char *data[] = {seven[0].data(), nine[1].data()};
  std::array<cl_int, 2> statuses{};
  auto *program = clCreateProgramWithBinary(context, 2, node.devices.data(), lengths.data(), data, statuses.data(), &error);
  REQUIRE((program && error == CL_SUCCESS));
  CHECK(statuses == std::array<cl_int, 2>{CL_SUCCESS, CL_SUCCESS});

  const auto returned = binariesOf(program);
  CHECK(returned[0] == seven[0]);
  CHECK(returned[1] == nine[1]);

  REQUIRE((clBuildProgram(program, 2, node.devices.data(), "", nullptr, nullptr) == CL_SUCCESS));
  auto *kernel = clCreateKernel(program, "step", &error);
  REQUIRE((kernel && error == CL_SUCCESS));
  fixture.opaqueLaunches();
  const auto first = launchedImage(context, node.devices[0], kernel);
  const auto second = launchedImage(context, node.devices[1], kernel);
  CHECK(first != second);
  CHECK(contains(seven[0], first));
  CHECK(contains(nine[1], second));

  REQUIRE((clReleaseKernel(kernel) == CL_SUCCESS));
  REQUIRE((clReleaseProgram(program) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

TEST_CASE("OpenCL program binds only the devices it was created for", "[opencl][multi-device]") {
  const auto node = enumerate();
  cl_int error = CL_SUCCESS;
  auto *context = clCreateContext(nullptr, 2, node.devices.data(), nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));

  fixture.programCatalogue({{.name = "step", .linkageName = "step"}}, {});
  const auto binaries = programBinaries(context, node.devices, "kernel void step(global int *out) { out[0] = 7; }");
  const size_t length = binaries[0].size();
  const unsigned char *data[] = {binaries[0].data()};
  auto *program = clCreateProgramWithBinary(context, 1, node.devices.data(), &length, data, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));

  cl_uint count = 0;
  REQUIRE((clGetProgramInfo(program, CL_PROGRAM_NUM_DEVICES, sizeof(count), &count, nullptr) == CL_SUCCESS));
  CHECK(count == 1);
  std::array<cl_device_id, 2> devices{};
  REQUIRE((clGetProgramInfo(program, CL_PROGRAM_DEVICES, sizeof(devices), devices.data(), nullptr) == CL_SUCCESS));
  CHECK(devices[0] == node.devices[0]);
  CHECK(devices[1] == nullptr);

  CHECK(clBuildProgram(program, 1, &node.devices[1], "", nullptr, nullptr) == CL_INVALID_DEVICE);
  cl_build_status status = CL_BUILD_NONE;
  CHECK(clGetProgramBuildInfo(program, node.devices[1], CL_PROGRAM_BUILD_STATUS, sizeof(status), &status, nullptr) == CL_INVALID_DEVICE);

  REQUIRE((clReleaseProgram(program) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

TEST_CASE("OpenCL returns CL_INVALID_DEVICE for devices outside the program context", "[opencl][multi-device]") {
  const auto node = enumerate();
  cl_int error = CL_SUCCESS;
  auto *wide = clCreateContext(nullptr, 2, node.devices.data(), nullptr, nullptr, &error);
  REQUIRE((wide && error == CL_SUCCESS));
  auto *narrow = clCreateContext(nullptr, 1, node.devices.data(), nullptr, nullptr, &error);
  REQUIRE((narrow && error == CL_SUCCESS));

  const char *source = "kernel void step(global int *out) { out[0] = 7; }";
  auto *program = clCreateProgramWithSource(narrow, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  CHECK(clBuildProgram(program, 1, &node.devices[1], "", nullptr, nullptr) == CL_INVALID_DEVICE);
  CHECK(clCompileProgram(program, 1, &node.devices[1], "", 0, nullptr, nullptr, nullptr, nullptr) == CL_INVALID_DEVICE);
  cl_build_status status = CL_BUILD_NONE;
  CHECK(clGetProgramBuildInfo(program, node.devices[1], CL_PROGRAM_BUILD_STATUS, sizeof(status), &status, nullptr) == CL_INVALID_DEVICE);
  REQUIRE((clReleaseProgram(program) == CL_SUCCESS));

  const auto binaries = programBinaries(wide, node.devices, source);
  const size_t length = binaries[1].size();
  const unsigned char *data[] = {binaries[1].data()};
  auto *binary = clCreateProgramWithBinary(narrow, 1, &node.devices[1], &length, data, nullptr, &error);
  CHECK(binary == nullptr);
  CHECK(error == CL_INVALID_DEVICE);

  int local = 0;
  auto *bogus = reinterpret_cast<cl_device_id>(&local);
  error = CL_SUCCESS;
  binary = clCreateProgramWithBinary(narrow, 1, &bogus, &length, data, nullptr, &error);
  CHECK(binary == nullptr);
  CHECK(error == CL_INVALID_DEVICE);
  CHECK(clCreateContext(nullptr, 1, &bogus, nullptr, nullptr, &error) == nullptr);
  CHECK(error == CL_INVALID_DEVICE);

  REQUIRE((clReleaseContext(narrow) == CL_SUCCESS));
  REQUIRE((clReleaseContext(wide) == CL_SUCCESS));
}

TEST_CASE("OpenCL rejects a device listed twice when creating a program from binaries", "[opencl][multi-device]") {
  const auto node = enumerate();
  cl_int error = CL_SUCCESS;
  auto *context = clCreateContext(nullptr, 2, node.devices.data(), nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));

  fixture.programCatalogue({{.name = "step", .linkageName = "step"}}, {});
  const auto binaries = programBinaries(context, node.devices, "kernel void step(global int *out) { out[0] = 7; }");
  const std::array<size_t, 2> lengths{binaries[0].size(), binaries[0].size()};
  const unsigned char *data[] = {binaries[0].data(), binaries[0].data()};
  const std::array<cl_device_id, 2> duplicated{node.devices[0], node.devices[0]};
  auto *program = clCreateProgramWithBinary(context, 2, duplicated.data(), lengths.data(), data, nullptr, &error);
  CHECK(program == nullptr);
  CHECK(error == CL_INVALID_DEVICE);

  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

TEST_CASE("OpenCL tracks build state and binaries per device", "[opencl][multi-device]") {
  const auto node = enumerate();
  cl_int error = CL_SUCCESS;
  auto *context = clCreateContext(nullptr, 2, node.devices.data(), nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));

  fixture.programCatalogue({{.name = "step", .linkageName = "step"}}, {});
  const char *source = "kernel void step(global int *out) { out[0] = 7; }";
  auto *program = clCreateProgramWithSource(context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE((clBuildProgram(program, 1, node.devices.data(), "", nullptr, nullptr) == CL_SUCCESS));

  cl_build_status status = CL_BUILD_NONE;
  REQUIRE((clGetProgramBuildInfo(program, node.devices[0], CL_PROGRAM_BUILD_STATUS, sizeof(status), &status, nullptr) == CL_SUCCESS));
  CHECK(status == CL_BUILD_SUCCESS);
  REQUIRE((clGetProgramBuildInfo(program, node.devices[1], CL_PROGRAM_BUILD_STATUS, sizeof(status), &status, nullptr) == CL_SUCCESS));
  CHECK(status == CL_BUILD_NONE);

  std::array<size_t, 2> sizes{};
  REQUIRE((clGetProgramInfo(program, CL_PROGRAM_BINARY_SIZES, sizeof(sizes), sizes.data(), nullptr) == CL_SUCCESS));
  CHECK(sizes[0] > 0);
  CHECK(sizes[1] == 0);

  auto *kernel = clCreateKernel(program, "step", &error);
  REQUIRE((kernel && error == CL_SUCCESS));
  cl_device_id none = nullptr;
  size_t groupSize = 0;
  CHECK(clGetKernelWorkGroupInfo(kernel, none, CL_KERNEL_WORK_GROUP_SIZE, sizeof(groupSize), &groupSize, nullptr) == CL_SUCCESS);
  CHECK(clGetKernelWorkGroupInfo(kernel, node.devices[0], CL_KERNEL_WORK_GROUP_SIZE, sizeof(groupSize), &groupSize, nullptr) == CL_SUCCESS);
  CHECK(clGetKernelWorkGroupInfo(kernel, node.devices[1], CL_KERNEL_WORK_GROUP_SIZE, sizeof(groupSize), &groupSize, nullptr)
        == CL_INVALID_DEVICE);

  auto *queue = clCreateCommandQueueWithProperties(context, node.devices[1], nullptr, &error);
  REQUIRE((queue && error == CL_SUCCESS));
  auto *buffer = clCreateBuffer(context, CL_MEM_READ_WRITE, sizeof(int32_t), nullptr, &error);
  REQUIRE((buffer && error == CL_SUCCESS));
  REQUIRE((clSetKernelArg(kernel, 0, sizeof(buffer), &buffer) == CL_SUCCESS));
  const size_t global = 1;
  CHECK(clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &global, nullptr, 0, nullptr, nullptr) == CL_INVALID_PROGRAM_EXECUTABLE);
  REQUIRE((clReleaseMemObject(buffer) == CL_SUCCESS));
  REQUIRE((clReleaseCommandQueue(queue) == CL_SUCCESS));

  REQUIRE((clBuildProgram(program, 1, &node.devices[1], "", nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((clGetProgramBuildInfo(program, node.devices[1], CL_PROGRAM_BUILD_STATUS, sizeof(status), &status, nullptr) == CL_SUCCESS));
  CHECK(status == CL_BUILD_SUCCESS);
  CHECK(clGetKernelWorkGroupInfo(kernel, none, CL_KERNEL_WORK_GROUP_SIZE, sizeof(groupSize), &groupSize, nullptr) == CL_INVALID_DEVICE);

  REQUIRE((clReleaseKernel(kernel) == CL_SUCCESS));
  REQUIRE((clReleaseProgram(program) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

TEST_CASE("OpenCL rejects a kernel whose definition differs between built devices", "[opencl][multi-device]") {
  const auto node = enumerate();
  cl_int error = CL_SUCCESS;
  auto *context = clCreateContext(nullptr, 2, node.devices.data(), nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));

  const char *source = "kernel void step(global int *out) { out[0] = 7; }";
  auto *program = clCreateProgramWithSource(context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  fixture.programCatalogue({{.name = "step", .linkageName = "step"}}, {});
  REQUIRE((clBuildProgram(program, 1, node.devices.data(), "", nullptr, nullptr) == CL_SUCCESS));
  auto *kernel = clCreateKernel(program, "step", &error);
  CHECK(error == CL_SUCCESS);
  REQUIRE((clReleaseKernel(kernel) == CL_SUCCESS));

  fixture.programCatalogue({{.name = "other", .linkageName = "other"}}, {});
  REQUIRE((clBuildProgram(program, 1, &node.devices[1], "", nullptr, nullptr) == CL_SUCCESS));
  char names[64] = {};
  REQUIRE((clGetProgramInfo(program, CL_PROGRAM_KERNEL_NAMES, sizeof(names), names, nullptr) == CL_SUCCESS));
  CHECK(std::string(names) == "step;other");
  CHECK(clCreateKernel(program, "step", &error) == nullptr);
  CHECK(error == CL_INVALID_KERNEL_DEFINITION);
  CHECK(clCreateKernel(program, "other", &error) == nullptr);
  CHECK(error == CL_INVALID_KERNEL_DEFINITION);
  CHECK(clCreateKernel(program, "absent", &error) == nullptr);
  CHECK(error == CL_INVALID_KERNEL_NAME);

  REQUIRE((clReleaseProgram(program) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

TEST_CASE("OpenCL links a multi-device program created from binaries", "[opencl][multi-device]") {
  const auto node = enumerate();
  cl_int error = CL_SUCCESS;
  auto *context = clCreateContext(nullptr, 2, node.devices.data(), nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));

  fixture.programCatalogue({{.name = "step", .linkageName = "step"}}, {});
  const auto seven = programBinaries(context, node.devices, "kernel void step(global int *out) { out[0] = 7; }");
  const auto nine = programBinaries(context, node.devices, "kernel void step(global int *out) { out[0] = 9; }");
  const std::array<size_t, 2> lengths{seven[0].size(), nine[1].size()};
  const unsigned char *data[] = {seven[0].data(), nine[1].data()};
  auto *input = clCreateProgramWithBinary(context, 2, node.devices.data(), lengths.data(), data, nullptr, &error);
  REQUIRE((input && error == CL_SUCCESS));
  REQUIRE((clBuildProgram(input, 0, nullptr, "", nullptr, nullptr) == CL_SUCCESS));

  auto *linked = clLinkProgram(context, 0, nullptr, "", 1, &input, nullptr, nullptr, &error);
  REQUIRE((linked && error == CL_SUCCESS));
  cl_uint count = 0;
  REQUIRE((clGetProgramInfo(linked, CL_PROGRAM_NUM_DEVICES, sizeof(count), &count, nullptr) == CL_SUCCESS));
  CHECK(count == 2);
  for (auto *device : node.devices) {
    cl_build_status status = CL_BUILD_NONE;
    REQUIRE((clGetProgramBuildInfo(linked, device, CL_PROGRAM_BUILD_STATUS, sizeof(status), &status, nullptr) == CL_SUCCESS));
    CHECK(status == CL_BUILD_SUCCESS);
  }

  auto *kernel = clCreateKernel(linked, "step", &error);
  REQUIRE((kernel && error == CL_SUCCESS));
  fixture.opaqueLaunches();
  const auto first = launchedImage(context, node.devices[0], kernel);
  const auto second = launchedImage(context, node.devices[1], kernel);
  CHECK(first != second);
  CHECK(contains(seven[0], first));
  CHECK(contains(nine[1], second));

  REQUIRE((clReleaseKernel(kernel) == CL_SUCCESS));
  REQUIRE((clReleaseProgram(linked) == CL_SUCCESS));
  REQUIRE((clReleaseProgram(input) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

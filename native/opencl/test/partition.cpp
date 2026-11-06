#include <algorithm>
#include <cstddef>
#include <set>
#include <vector>

#include <CL/cl.h>
#include <catch2/catch_test_macros.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include "session.hpp"

using gemstone_opencl_test::gpu;
using gemstone_opencl_test::info;

namespace {

using Properties = std::vector<cl_device_partition_property>;

struct Partition {
  gemstone_test::RpcFixture server;
  gemstone_test::RpcEnvironment environment{server};
  cl_device_id root = gpu();
};

Properties partitionProperties(cl_device_id device, cl_device_info name) {
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(device, name, 0, nullptr, &size) == CL_SUCCESS));
  Properties values(size / sizeof(cl_device_partition_property));
  REQUIRE((clGetDeviceInfo(device, name, size, values.data(), nullptr) == CL_SUCCESS));
  return values;
}

std::vector<cl_device_id> partition(cl_device_id device, const Properties &properties) {
  cl_uint count = 0;
  REQUIRE((clCreateSubDevices(device, properties.data(), 0, nullptr, &count) == CL_SUCCESS));
  std::vector<cl_device_id> devices(count);
  cl_uint written = 0;
  REQUIRE((clCreateSubDevices(device, properties.data(), count, devices.data(), &written) == CL_SUCCESS));
  REQUIRE(written == count);
  return devices;
}

void release(const std::vector<cl_device_id> &devices) {
  for (auto *device : devices)
    REQUIRE((clReleaseDevice(device) == CL_SUCCESS));
}

} // namespace

TEST_CASE("OpenCL partitions by compute units only", "[opencl][partition]") {
  const Partition fixture;
  const auto units = info<cl_uint>(fixture.root, CL_DEVICE_MAX_COMPUTE_UNITS);
  REQUIRE(units > 1);

  const auto supported = partitionProperties(fixture.root, CL_DEVICE_PARTITION_PROPERTIES);
  CHECK(std::count(supported.begin(), supported.end(), CL_DEVICE_PARTITION_EQUALLY) == 1);
  CHECK(std::count(supported.begin(), supported.end(), CL_DEVICE_PARTITION_BY_COUNTS) == 1);
  CHECK(std::count(supported.begin(), supported.end(), CL_DEVICE_PARTITION_BY_AFFINITY_DOMAIN) == 0);
  CHECK(info<cl_device_affinity_domain>(fixture.root, CL_DEVICE_PARTITION_AFFINITY_DOMAIN) == 0);
  CHECK(info<cl_uint>(fixture.root, CL_DEVICE_PARTITION_MAX_SUB_DEVICES) == units);
  CHECK(info<cl_device_id>(fixture.root, CL_DEVICE_PARENT_DEVICE) == nullptr);
  CHECK(info<cl_uint>(fixture.root, CL_DEVICE_REFERENCE_COUNT) == 1);
}

TEST_CASE("OpenCL partitions a device equally", "[opencl][partition]") {
  const Partition fixture;
  const auto units = info<cl_uint>(fixture.root, CL_DEVICE_MAX_COMPUTE_UNITS);
  const Properties equally{CL_DEVICE_PARTITION_EQUALLY, 4, 0};

  const auto subDevices = partition(fixture.root, equally);
  REQUIRE(subDevices.size() == units / 4);
  for (auto *sub : subDevices) {
    CHECK(info<cl_uint>(sub, CL_DEVICE_MAX_COMPUTE_UNITS) == 4);
    CHECK(info<cl_device_id>(sub, CL_DEVICE_PARENT_DEVICE) == fixture.root);
    CHECK(partitionProperties(sub, CL_DEVICE_PARTITION_TYPE) == equally);
    CHECK(info<cl_device_type>(sub, CL_DEVICE_TYPE) == info<cl_device_type>(fixture.root, CL_DEVICE_TYPE));
    CHECK(info<cl_uint>(sub, CL_DEVICE_REFERENCE_COUNT) == 1);
  }
  CHECK(std::set<cl_device_id>(subDevices.begin(), subDevices.end()).size() == subDevices.size());

  auto *first = subDevices.front();
  REQUIRE((clRetainDevice(first) == CL_SUCCESS));
  CHECK(info<cl_uint>(first, CL_DEVICE_REFERENCE_COUNT) == 2);
  REQUIRE((clReleaseDevice(first) == CL_SUCCESS));
  CHECK(info<cl_uint>(first, CL_DEVICE_REFERENCE_COUNT) == 1);

  REQUIRE((clRetainDevice(fixture.root) == CL_SUCCESS));
  REQUIRE((clReleaseDevice(fixture.root) == CL_SUCCESS));
  CHECK(info<cl_uint>(fixture.root, CL_DEVICE_REFERENCE_COUNT) == 1);

  release(subDevices);
}

TEST_CASE("OpenCL partitions a sub-device again", "[opencl][partition]") {
  const Partition fixture;
  const auto units = static_cast<cl_device_partition_property>(info<cl_uint>(fixture.root, CL_DEVICE_MAX_COMPUTE_UNITS));
  const auto halves = partition(fixture.root, {CL_DEVICE_PARTITION_EQUALLY, units / 2, 0});
  REQUIRE(halves.size() == 2);
  const auto quarters = partition(halves.front(), {CL_DEVICE_PARTITION_EQUALLY, units / 4, 0});
  REQUIRE(quarters.size() == 2);
  for (auto *sub : quarters) {
    CHECK(info<cl_uint>(sub, CL_DEVICE_MAX_COMPUTE_UNITS) == units / 4);
    CHECK(info<cl_device_id>(sub, CL_DEVICE_PARENT_DEVICE) == halves.front());
  }
  release(halves);
  CHECK(info<cl_device_id>(quarters.front(), CL_DEVICE_PARENT_DEVICE) == halves.front());
  CHECK(info<cl_uint>(halves.front(), CL_DEVICE_REFERENCE_COUNT) == quarters.size());
  release(quarters);
}

TEST_CASE("OpenCL partitions a device by counts", "[opencl][partition]") {
  const Partition fixture;
  const auto units = static_cast<cl_device_partition_property>(info<cl_uint>(fixture.root, CL_DEVICE_MAX_COMPUTE_UNITS));
  const Properties counts{CL_DEVICE_PARTITION_BY_COUNTS, 1, units - 1, CL_DEVICE_PARTITION_BY_COUNTS_LIST_END, 0};

  const auto subDevices = partition(fixture.root, counts);
  REQUIRE(subDevices.size() == 2);
  CHECK(info<cl_uint>(subDevices[0], CL_DEVICE_MAX_COMPUTE_UNITS) == 1);
  CHECK(info<cl_uint>(subDevices[1], CL_DEVICE_MAX_COMPUTE_UNITS) == units - 1);
  CHECK(partitionProperties(subDevices[0], CL_DEVICE_PARTITION_TYPE) == counts);
  CHECK(info<cl_uint>(subDevices[0], CL_DEVICE_PARTITION_MAX_SUB_DEVICES) == 0);
  CHECK(partitionProperties(subDevices[0], CL_DEVICE_PARTITION_PROPERTIES) == Properties{0});
  release(subDevices);
}

TEST_CASE("OpenCL rejects partitions it cannot make", "[opencl][partition]") {
  const Partition fixture;
  const auto units = static_cast<cl_device_partition_property>(info<cl_uint>(fixture.root, CL_DEVICE_MAX_COMPUTE_UNITS));
  std::vector<cl_device_id> out(static_cast<size_t>(units) + 1);
  const auto create = [&](const Properties &properties, cl_uint count) {
    return clCreateSubDevices(fixture.root, properties.empty() ? nullptr : properties.data(), count, out.data(), nullptr);
  };

  CHECK(clCreateSubDevices(nullptr, Properties{CL_DEVICE_PARTITION_EQUALLY, 1, 0}.data(), 1, out.data(), nullptr) == CL_INVALID_DEVICE);
  CHECK(create({}, 1) == CL_INVALID_VALUE);
  CHECK(create({CL_DEVICE_PARTITION_EQUALLY, 1, 0}, 0) == CL_INVALID_VALUE);
  CHECK(create({-1, 1, 0}, 1) == CL_INVALID_VALUE);
  CHECK(create({CL_DEVICE_PARTITION_BY_AFFINITY_DOMAIN, CL_DEVICE_AFFINITY_DOMAIN_NEXT_PARTITIONABLE, 0}, 1) == CL_INVALID_VALUE);
  CHECK(create({CL_DEVICE_PARTITION_EQUALLY, units, 0}, 1) == CL_DEVICE_PARTITION_FAILED);
  CHECK(create({CL_DEVICE_PARTITION_EQUALLY, units + 1, 0}, 1) == CL_INVALID_DEVICE_PARTITION_COUNT);
  CHECK(create({CL_DEVICE_PARTITION_EQUALLY, 0, 0}, 1) == CL_INVALID_DEVICE_PARTITION_COUNT);
  CHECK(create({CL_DEVICE_PARTITION_BY_COUNTS, units + 1, CL_DEVICE_PARTITION_BY_COUNTS_LIST_END, 0}, 1)
        == CL_INVALID_DEVICE_PARTITION_COUNT);
  CHECK(create({CL_DEVICE_PARTITION_BY_COUNTS, -1, CL_DEVICE_PARTITION_BY_COUNTS_LIST_END, 0}, 1) == CL_INVALID_DEVICE_PARTITION_COUNT);
  CHECK(create({CL_DEVICE_PARTITION_BY_COUNTS, units - 1, 2, CL_DEVICE_PARTITION_BY_COUNTS_LIST_END, 0}, 2)
        == CL_INVALID_DEVICE_PARTITION_COUNT);
  CHECK(create({CL_DEVICE_PARTITION_BY_COUNTS, CL_DEVICE_PARTITION_BY_COUNTS_LIST_END, 0}, 1) == CL_INVALID_VALUE);
}

TEST_CASE("OpenCL runs contexts and queues over sub-devices", "[opencl][partition]") {
  const Partition fixture;
  const auto units = static_cast<cl_device_partition_property>(info<cl_uint>(fixture.root, CL_DEVICE_MAX_COMPUTE_UNITS));
  const auto subDevices = partition(fixture.root, {CL_DEVICE_PARTITION_EQUALLY, units / 2, 0});
  REQUIRE(subDevices.size() == 2);
  std::vector<cl_device_id> devices(subDevices);
  devices.push_back(fixture.root);

  cl_int error = CL_SUCCESS;
  auto *context = clCreateContext(nullptr, static_cast<cl_uint>(devices.size()), devices.data(), nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));
  CHECK(info<cl_uint>(subDevices[0], CL_DEVICE_REFERENCE_COUNT) == 2);

  std::vector<cl_command_queue> queues;
  for (auto *device : devices) {
    auto *queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
    REQUIRE((queue && error == CL_SUCCESS));
    cl_device_id queued = nullptr;
    REQUIRE((clGetCommandQueueInfo(queue, CL_QUEUE_DEVICE, sizeof(queued), &queued, nullptr) == CL_SUCCESS));
    CHECK(queued == device);
    REQUIRE((clFinish(queue) == CL_SUCCESS));
    queues.push_back(queue);
  }

  size_t size = 0;
  REQUIRE((clGetContextInfo(context, CL_CONTEXT_DEVICES, 0, nullptr, &size) == CL_SUCCESS));
  CHECK(size == devices.size() * sizeof(cl_device_id));

  release(subDevices);
  CHECK(info<cl_uint>(subDevices[0], CL_DEVICE_REFERENCE_COUNT) == 1);
  for (auto *queue : queues)
    REQUIRE((clReleaseCommandQueue(queue) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

#include <array>
#include <cstdint>
#include <cstring>
#include <string>

#include <catch2/catch_test_macros.hpp>
#include <ze_api.h>

#include <gemstone/child_test.hpp>
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
  ze_driver_handle_t driver;
  std::array<ze_device_handle_t, 2> devices;
};

ze_driver_handle_t firstDriver() {
  REQUIRE(zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS);
  uint32_t count = 1;
  ze_driver_handle_t driver = nullptr;
  REQUIRE(zeDriverGet(&count, &driver) == ZE_RESULT_SUCCESS);
  return driver;
}

Node enumerate() {
  auto *const driver = firstDriver();
  uint32_t count = 2;
  std::array<ze_device_handle_t, 2> devices{};
  REQUIRE(zeDeviceGet(driver, &count, devices.data()) == ZE_RESULT_SUCCESS);
  REQUIRE(count == 2);
  return {.driver = driver, .devices = devices};
}

ze_context_handle_t createContext(ze_driver_handle_t driver) {
  ze_context_desc_t contextDesc{};
  contextDesc.stype = ZE_STRUCTURE_TYPE_CONTEXT_DESC;
  ze_context_handle_t context = nullptr;
  REQUIRE(zeContextCreate(driver, &contextDesc, &context) == ZE_RESULT_SUCCESS);
  return context;
}

} // namespace

TEST_CASE("Level Zero enumerates the configured number of root devices", "[level-zero][multi-device]") {
  auto *const driver = firstDriver();

  uint32_t count = 0;
  REQUIRE(zeDeviceGet(driver, &count, nullptr) == ZE_RESULT_SUCCESS);
  CHECK(count == 2);

  count = 1;
  ze_device_handle_t single = nullptr;
  REQUIRE(zeDeviceGet(driver, &count, &single) == ZE_RESULT_SUCCESS);
  CHECK(count == 1);

  count = 3;
  std::array<ze_device_handle_t, 3> devices{};
  REQUIRE(zeDeviceGet(driver, &count, devices.data()) == ZE_RESULT_SUCCESS);
  CHECK(count == 2);
  CHECK(devices[0] == single);
  CHECK(devices[1] != nullptr);
  CHECK(devices[0] != devices[1]);
}

TEST_CASE("Level Zero devices report distinct uuid and pci identities", "[level-zero][multi-device]") {
  const auto node = enumerate();

  ze_device_properties_t first{};
  first.stype = ZE_STRUCTURE_TYPE_DEVICE_PROPERTIES;
  ze_device_properties_t second{};
  second.stype = ZE_STRUCTURE_TYPE_DEVICE_PROPERTIES;
  REQUIRE(zeDeviceGetProperties(node.devices[0], &first) == ZE_RESULT_SUCCESS);
  REQUIRE(zeDeviceGetProperties(node.devices[1], &second) == ZE_RESULT_SUCCESS);
  CHECK(std::memcmp(first.uuid.id, second.uuid.id, ZE_MAX_DEVICE_UUID_SIZE) != 0);

  ze_pci_ext_properties_t firstPci{};
  firstPci.stype = ZE_STRUCTURE_TYPE_PCI_EXT_PROPERTIES;
  ze_pci_ext_properties_t secondPci{};
  secondPci.stype = ZE_STRUCTURE_TYPE_PCI_EXT_PROPERTIES;
  REQUIRE(zeDevicePciGetPropertiesExt(node.devices[0], &firstPci) == ZE_RESULT_SUCCESS);
  REQUIRE(zeDevicePciGetPropertiesExt(node.devices[1], &secondPci) == ZE_RESULT_SUCCESS);
  CHECK(firstPci.address.bus != secondPci.address.bus);
}

TEST_CASE("Level Zero reports the owning device of a device allocation", "[level-zero][multi-device]") {
  const auto node = enumerate();
  auto *const context = createContext(node.driver);

  ze_device_mem_alloc_desc_t allocationDesc{};
  allocationDesc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  void *memory = nullptr;
  REQUIRE(zeMemAllocDevice(context, &allocationDesc, 64, 64, node.devices[1], &memory) == ZE_RESULT_SUCCESS);

  ze_memory_allocation_properties_t properties{};
  properties.stype = ZE_STRUCTURE_TYPE_MEMORY_ALLOCATION_PROPERTIES;
  ze_device_handle_t owner = nullptr;
  REQUIRE(zeMemGetAllocProperties(context, memory, &properties, &owner) == ZE_RESULT_SUCCESS);
  CHECK(properties.type == ZE_MEMORY_TYPE_DEVICE);
  CHECK(owner == node.devices[1]);

  REQUIRE(zeMemFree(context, memory) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero exposes peer access between root devices", "[level-zero][multi-device]") {
  const auto node = enumerate();

  ze_bool_t value = 0;
  REQUIRE(zeDeviceCanAccessPeer(node.devices[0], node.devices[1], &value) == ZE_RESULT_SUCCESS);
  CHECK(value != 0);
  REQUIRE(zeDeviceCanAccessPeer(node.devices[1], node.devices[0], &value) == ZE_RESULT_SUCCESS);
  CHECK(value != 0);
  CHECK(zeDeviceCanAccessPeer(nullptr, node.devices[1], &value) == ZE_RESULT_ERROR_INVALID_NULL_HANDLE);
  CHECK(zeDeviceCanAccessPeer(node.devices[0], node.devices[1], nullptr) == ZE_RESULT_ERROR_INVALID_NULL_POINTER);

  ze_device_p2p_properties_t p2p{};
  p2p.stype = ZE_STRUCTURE_TYPE_DEVICE_P2P_PROPERTIES;
  REQUIRE(zeDeviceGetP2PProperties(node.devices[0], node.devices[1], &p2p) == ZE_RESULT_SUCCESS);
  CHECK((p2p.flags & ZE_DEVICE_P2P_PROPERTY_FLAG_ACCESS) != 0);
  CHECK((p2p.flags & ZE_DEVICE_P2P_PROPERTY_FLAG_ATOMICS) != 0);
}

TEST_CASE("Level Zero copies between device allocations owned by different devices", "[level-zero][multi-device]") {
  const auto node = enumerate();
  auto *const context = createContext(node.driver);

  ze_device_mem_alloc_desc_t allocationDesc{};
  allocationDesc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  void *source = nullptr;
  void *destination = nullptr;
  REQUIRE(zeMemAllocDevice(context, &allocationDesc, 64, 64, node.devices[1], &source) == ZE_RESULT_SUCCESS);
  REQUIRE(zeMemAllocDevice(context, &allocationDesc, 64, 64, node.devices[0], &destination) == ZE_RESULT_SUCCESS);
  auto *sourceBytes = static_cast<uint8_t *>(source);
  for (uint32_t i = 0; i < 64; ++i)
    sourceBytes[i] = static_cast<uint8_t>(i + 1);
  std::memset(destination, 0, 64);

  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
  ze_command_queue_handle_t queue = nullptr;
  REQUIRE(zeCommandQueueCreate(context, node.devices[0], &queueDesc, &queue) == ZE_RESULT_SUCCESS);
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t list = nullptr;
  REQUIRE(zeCommandListCreate(context, node.devices[0], &listDesc, &list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListAppendMemoryCopy(list, destination, source, 64, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListClose(list) == ZE_RESULT_SUCCESS);

  ze_fence_desc_t fenceDesc{};
  fenceDesc.stype = ZE_STRUCTURE_TYPE_FENCE_DESC;
  ze_fence_handle_t fence = nullptr;
  REQUIRE(zeFenceCreate(queue, &fenceDesc, &fence) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueExecuteCommandLists(queue, 1, &list, fence) == ZE_RESULT_SUCCESS);
  REQUIRE(zeFenceHostSynchronize(fence, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
  CHECK(std::memcmp(source, destination, 64) == 0);

  REQUIRE(zeFenceDestroy(fence) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS);
  REQUIRE(zeMemFree(context, source) == ZE_RESULT_SUCCESS);
  REQUIRE(zeMemFree(context, destination) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero limits root devices to ZE_AFFINITY_MASK", "[level-zero][multi-device]") {
  const auto node = enumerate();
  std::array<ze_device_handle_t, 2> devices{};

  SECTION("selects a single device by ordinal") {
    const gemstone_test::Environment affinity("ZE_AFFINITY_MASK", "1");
    uint32_t count = 2;
    REQUIRE(zeDeviceGet(node.driver, &count, devices.data()) == ZE_RESULT_SUCCESS);
    CHECK(count == 1);
    CHECK(devices[0] == node.devices[1]);
  }

  SECTION("keeps the order given by the mask") {
    const gemstone_test::Environment affinity("ZE_AFFINITY_MASK", "1,0");
    uint32_t count = 2;
    REQUIRE(zeDeviceGet(node.driver, &count, devices.data()) == ZE_RESULT_SUCCESS);
    CHECK(count == 2);
    CHECK(devices[0] == node.devices[1]);
    CHECK(devices[1] == node.devices[0]);
  }

  SECTION("treats an empty mask as unset") {
    const gemstone_test::Environment affinity("ZE_AFFINITY_MASK", "");
    uint32_t count = 2;
    REQUIRE(zeDeviceGet(node.driver, &count, devices.data()) == ZE_RESULT_SUCCESS);
    CHECK(count == 2);
  }

  SECTION("rejects ordinals beyond the configured count") {
    const gemstone_test::Environment affinity("ZE_AFFINITY_MASK", "2");
    uint32_t count = 0;
    CHECK(zeDeviceGet(node.driver, &count, nullptr) == ZE_RESULT_ERROR_INVALID_ARGUMENT);
  }

  SECTION("rejects a malformed mask") {
    const gemstone_test::Environment affinity("ZE_AFFINITY_MASK", "0,,1");
    uint32_t count = 0;
    CHECK(zeDeviceGet(node.driver, &count, nullptr) == ZE_RESULT_ERROR_INVALID_ARGUMENT);
  }
}

namespace {

ze_module_handle_t loadModule(ze_context_handle_t context, ze_device_handle_t device) {
  ze_module_desc_t moduleDesc{};
  moduleDesc.stype = ZE_STRUCTURE_TYPE_MODULE_DESC;
  moduleDesc.format = ZE_MODULE_FORMAT_IL_SPIRV;
  moduleDesc.inputSize = sizeof(gemstone_test::RpcFixture::Spirv);
  moduleDesc.pInputModule = reinterpret_cast<const uint8_t *>(gemstone_test::RpcFixture::Spirv);
  ze_module_handle_t module = nullptr;
  REQUIRE(zeModuleCreate(context, device, &moduleDesc, &module, nullptr) == ZE_RESULT_SUCCESS);
  return module;
}

ze_result_t launchOn(ze_context_handle_t context, ze_device_handle_t device, ze_kernel_handle_t kernel) {
  const ze_group_count_t groups{.groupCountX = 1, .groupCountY = 1, .groupCountZ = 1};
  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS;
  ze_command_queue_handle_t queue = nullptr;
  REQUIRE(zeCommandQueueCreate(context, device, &queueDesc, &queue) == ZE_RESULT_SUCCESS);
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t list = nullptr;
  REQUIRE(zeCommandListCreate(context, device, &listDesc, &list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListAppendLaunchKernel(list, kernel, &groups, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListClose(list) == ZE_RESULT_SUCCESS);
  const auto result = zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr);
  if (result == ZE_RESULT_SUCCESS) REQUIRE(zeCommandQueueSynchronize(queue, UINT64_MAX) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS);
  return result;
}

} // namespace

TEST_CASE("Level Zero device lost is per device child", "[.]") {
  const auto node = enumerate();
  auto *const context = createContext(node.driver);
  fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = 0});

  ze_kernel_desc_t kernelDesc{};
  kernelDesc.stype = ZE_STRUCTURE_TYPE_KERNEL_DESC;
  kernelDesc.pKernelName = "kernel";
  ze_device_mem_alloc_desc_t allocationDesc{};
  allocationDesc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  const uint32_t seed = 0;
  std::array<ze_kernel_handle_t, 2> kernels{};
  for (size_t index = 0; index < kernels.size(); ++index) {
    auto *const module = loadModule(context, node.devices[index]);
    REQUIRE(zeKernelCreate(module, &kernelDesc, &kernels[index]) == ZE_RESULT_SUCCESS);
    void *output = nullptr;
    REQUIRE(zeMemAllocDevice(context, &allocationDesc, 64, 64, node.devices[index], &output) == ZE_RESULT_SUCCESS);
    REQUIRE(zeKernelSetArgumentValue(kernels[index], 0, sizeof(output), &output) == ZE_RESULT_SUCCESS);
    REQUIRE(zeKernelSetArgumentValue(kernels[index], 1, sizeof(seed), &seed) == ZE_RESULT_SUCCESS);
  }

  fixture.failNextCompletion();
  CHECK(launchOn(context, node.devices[0], kernels[0]) == ZE_RESULT_ERROR_DEVICE_LOST);
  CHECK(launchOn(context, node.devices[0], kernels[0]) == ZE_RESULT_ERROR_DEVICE_LOST);
  CHECK(launchOn(context, node.devices[1], kernels[1]) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero device lost is per device", "[level-zero][multi-device]") {
  const auto outcome = gemstone_test::runChildTest("Level Zero device lost is per device child");
  CAPTURE(outcome.output);
  CHECK(outcome.signal == 0);
  CHECK(outcome.exitCode == 0);
}

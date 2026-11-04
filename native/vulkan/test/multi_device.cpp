#include <array>
#include <cstdint>
#include <cstring>
#include <set>
#include <string>

#include <catch2/catch_test_macros.hpp>

#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>
#include <vulkan/vulkan.h>

namespace {

gemstone_test::RpcFixture fixture;
const std::string Endpoint = fixture.endpoint();
const gemstone_test::Environment RpcEndpoint(gemstone::environment::RpcEndpoint, Endpoint.c_str());
const gemstone_test::Environment RpcClient(gemstone::environment::RpcClientId, "42");
const gemstone_test::Environment DeviceCount(gemstone::environment::DeviceCount, "2");

struct Instance {
  VkInstance instance = VK_NULL_HANDLE;
  Instance() {
    const char *extensions[] = {VK_KHR_GET_PHYSICAL_DEVICE_PROPERTIES_2_EXTENSION_NAME};
    VkInstanceCreateInfo info{};
    info.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
    info.enabledExtensionCount = 1;
    info.ppEnabledExtensionNames = extensions;
    REQUIRE(vkCreateInstance(&info, nullptr, &instance) == VK_SUCCESS);
  }
  ~Instance() { vkDestroyInstance(instance, nullptr); }
  Instance(const Instance &) = delete;
  Instance &operator=(const Instance &) = delete;
};

std::array<VkPhysicalDevice, 2> enumerate(VkInstance instance) {
  uint32_t count = 0;
  REQUIRE(vkEnumeratePhysicalDevices(instance, &count, nullptr) == VK_SUCCESS);
  REQUIRE(count == 2);
  std::array<VkPhysicalDevice, 2> devices{};
  REQUIRE(vkEnumeratePhysicalDevices(instance, &count, devices.data()) == VK_SUCCESS);
  REQUIRE(count == 2);
  return devices;
}

struct Identity {
  uint32_t deviceID = 0;
  VkPhysicalDeviceIDProperties id{};
};

Identity identity(VkPhysicalDevice physical) {
  Identity result;
  result.id.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ID_PROPERTIES;
  VkPhysicalDeviceProperties2 properties{};
  properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2;
  properties.pNext = &result.id;
  vkGetPhysicalDeviceProperties2(physical, &properties);
  result.deviceID = properties.properties.deviceID;
  return result;
}

} // namespace

TEST_CASE("Vulkan enumerates the configured number of physical devices", "[vulkan][multi-device]") {
  const Instance context;

  uint32_t count = 0;
  REQUIRE(vkEnumeratePhysicalDevices(context.instance, &count, nullptr) == VK_SUCCESS);
  CHECK(count == 2);

  std::array<VkPhysicalDevice, 3> devices{};
  count = 3;
  REQUIRE(vkEnumeratePhysicalDevices(context.instance, &count, devices.data()) == VK_SUCCESS);
  CHECK(count == 2);
  CHECK(devices[0] != VK_NULL_HANDLE);
  CHECK(devices[1] != VK_NULL_HANDLE);
  CHECK(devices[0] != devices[1]);
  CHECK(devices[2] == VK_NULL_HANDLE);

  count = 1;
  std::array<VkPhysicalDevice, 1> first{};
  CHECK(vkEnumeratePhysicalDevices(context.instance, &count, first.data()) == VK_INCOMPLETE);
  CHECK(first[0] == devices[0]);
}

TEST_CASE("Vulkan physical devices expose distinct identities", "[vulkan][multi-device]") {
  const Instance context;
  const auto physical = enumerate(context.instance);

  const auto first = identity(physical[0]);
  const auto second = identity(physical[1]);
  CHECK(first.deviceID == 1);
  CHECK(second.deviceID == 2);
  CHECK(std::memcmp(first.id.deviceUUID, second.id.deviceUUID, VK_UUID_SIZE) != 0);
  CHECK(first.id.deviceUUID[VK_UUID_SIZE - 1] != second.id.deviceUUID[VK_UUID_SIZE - 1]);
  CHECK(std::memcmp(first.id.driverUUID, second.id.driverUUID, VK_UUID_SIZE) == 0);
}

TEST_CASE("Vulkan device groups cover each physical device", "[vulkan][multi-device]") {
  const Instance context;
  const auto physical = enumerate(context.instance);

  uint32_t count = 0;
  REQUIRE(vkEnumeratePhysicalDeviceGroups(context.instance, &count, nullptr) == VK_SUCCESS);
  CHECK(count == 2);

  std::array<VkPhysicalDeviceGroupProperties, 2> groups{};
  for (auto &group : groups)
    group.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GROUP_PROPERTIES;
  REQUIRE(vkEnumeratePhysicalDeviceGroups(context.instance, &count, groups.data()) == VK_SUCCESS);
  REQUIRE(count == 2);
  CHECK(groups[0].physicalDeviceCount == 1);
  CHECK(groups[1].physicalDeviceCount == 1);
  CHECK(groups[0].physicalDevices[0] == physical[0]);
  CHECK(groups[1].physicalDevices[0] == physical[1]);
}

TEST_CASE("Vulkan creates a logical device on each physical device", "[vulkan][multi-device]") {
  const Instance context;
  const auto physical = enumerate(context.instance);

  const float priority = 1.0F;
  VkDeviceQueueCreateInfo queue{};
  queue.sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
  queue.queueCount = 1;
  queue.pQueuePriorities = &priority;
  VkDeviceCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
  info.queueCreateInfoCount = 1;
  info.pQueueCreateInfos = &queue;

  std::set<VkDevice> created;
  for (auto *const candidate : physical) {
    VkDevice device = VK_NULL_HANDLE;
    REQUIRE(vkCreateDevice(candidate, &info, nullptr, &device) == VK_SUCCESS);
    REQUIRE(device != VK_NULL_HANDLE);
    created.insert(device);
  }
  CHECK(created.size() == 2);
  for (auto *const device : created)
    vkDestroyDevice(device, nullptr);
}

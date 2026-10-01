#include <string_view>

#include "gemstone_vulkan_adapter.hpp"

#include "state.hpp"

using namespace gemstone::vulkan::adapter;

namespace gemstone::vulkan::adapter {
PFN_vkVoidFunction supportedFunction(const char *name) {
  const auto version = configuredApiVersion().value_or(VK_API_VERSION_1_0);
#include "gemstone_vulkan_lookup.inc"

  return nullptr;
}

} // namespace gemstone::vulkan::adapter

extern "C" VKAPI_ATTR PFN_vkVoidFunction VKAPI_CALL vkGetDeviceProcAddr(VkDevice device, const char *name) {
  if (!device || !name) return nullptr;
  const std::string_view function(name);
  if (function == "vkResetQueryPoolEXT") {
    std::lock_guard lock(stateMutex);
    const auto found = devices.find(device);
    return found != devices.end() && found->second->hostQueryResetExtension ? supportedFunction(name) : nullptr;
  }
  // Instance/global commands and aliases of disabled extensions must not be
  // returned through a device lookup.
  if (function.ends_with("KHR") || function.ends_with("EXT") || function.starts_with("vkGetPhysicalDevice")
      || function.starts_with("vkEnumeratePhysicalDevice") || function.starts_with("vkEnumerateInstance")
      || function.starts_with("vkEnumerateDevice") || function == "vkCreateDevice" || function == "vkCreateInstance"
      || function == "vkDestroyInstance" || function == "vkGetInstanceProcAddr")
    return nullptr;
  return supportedFunction(name);
}

extern "C" VKAPI_ATTR PFN_vkVoidFunction VKAPI_CALL vkGetInstanceProcAddr(VkInstance instance, const char *name) {
  if (!name) return nullptr;
  if (!instance) {
    if (std::strcmp(name, "vkGetInstanceProcAddr") == 0) return reinterpret_cast<PFN_vkVoidFunction>(vkGetInstanceProcAddr);
    if (std::strcmp(name, "vkCreateInstance") == 0) return reinterpret_cast<PFN_vkVoidFunction>(vkCreateInstance);
    if (std::strcmp(name, "vkEnumerateInstanceVersion") == 0) return reinterpret_cast<PFN_vkVoidFunction>(vkEnumerateInstanceVersion);
    if (std::strcmp(name, "vkEnumerateInstanceExtensionProperties") == 0)
      return reinterpret_cast<PFN_vkVoidFunction>(vkEnumerateInstanceExtensionProperties);
    if (std::strcmp(name, "vkEnumerateInstanceLayerProperties") == 0)
      return reinterpret_cast<PFN_vkVoidFunction>(vkEnumerateInstanceLayerProperties);
    return nullptr;
  }
  const std::string_view function(name);
  // Device extension commands are visible through a valid instance when any
  // physical device supports the extension; device lookup still checks enablement.
  if (function == "vkResetQueryPoolEXT") return supportedFunction(name);
  if (function.ends_with("KHR") || function.ends_with("EXT")) {
    std::lock_guard lock(stateMutex);
    const auto found = instanceProperties2.find(instance);
    if (!function.starts_with("vkGetPhysicalDevice") || !function.ends_with("2KHR") || found == instanceProperties2.end() || !found->second)
      return nullptr;
  }
  return supportedFunction(name);
}

#include "gemstone_vulkan_boundaries.inc"

#include "entrypoints.inc"

#include <string_view>

#include "gemstone_vulkan_adapter.hpp"

#include "state.hpp"

using namespace gemstone::vulkan;

namespace gemstone::vulkan {
PFN_vkVoidFunction supportedFunction(const char *name) {
  const auto version = configuredApiVersion().value_or(VK_API_VERSION_1_0);
#include "gemstone_vulkan_lookup.inc"

  return nullptr;
}

} // namespace gemstone::vulkan

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

namespace {
template <typename Info, typename Create>
VkResult createPipelines(VkDevice device, VkPipelineCache cache, uint32_t count, const Info *infos, const VkAllocationCallbacks *allocator,
                         VkPipeline *output, Create create) {
  if (count && (!output || !infos)) return VK_ERROR_INITIALIZATION_FAILED;
  if (output) std::fill_n(output, count, VK_NULL_HANDLE);
  VkResult result = VK_SUCCESS;
  for (uint32_t i = 0; i < count; ++i) {
    try {
      host::Creation creation(allocator, handleArena(device), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
      try {
        const auto status = create(device, cache, 1, infos + i, allocator, output + i);
        if (status == VK_SUCCESS) creation.commit();
        else {
          implVkDestroyPipeline(device, output[i], allocator);
          output[i] = VK_NULL_HANDLE;
          result = status;
        }
      } catch (...) {
        implVkDestroyPipeline(device, output[i], allocator);
        output[i] = VK_NULL_HANDLE;
        throw;
      }
    } catch (const std::bad_alloc &) {
      result = VK_ERROR_OUT_OF_HOST_MEMORY;
    }
  }
  return result;
}
} // namespace

// Ownership boundaries with specialised allocation or rollback behaviour.

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkResetDescriptorPool(VkDevice device, VkDescriptorPool pool, VkDescriptorPoolResetFlags arg2) {
  host::Scope scope(handleArena(pool));
  try {
    return implVkResetDescriptorPool(device, pool, arg2);
  } catch (const std::bad_alloc &) {
    return VK_ERROR_OUT_OF_HOST_MEMORY;
  }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkAllocateDescriptorSets(VkDevice device, const VkDescriptorSetAllocateInfo *info,
                                                                   VkDescriptorSet *sets) {
  if (info && sets) std::fill_n(sets, info->descriptorSetCount, VK_NULL_HANDLE);
  host::Scope scope(info ? handleArena(info->descriptorPool) : nullptr);
  try {
    return implVkAllocateDescriptorSets(device, info, sets);
  } catch (const std::bad_alloc &) {
    if (info && sets) {
      implVkFreeDescriptorSets(device, info->descriptorPool, info->descriptorSetCount, sets);
      std::fill_n(sets, info->descriptorSetCount, VK_NULL_HANDLE);
    }
    return VK_ERROR_OUT_OF_HOST_MEMORY;
  }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateComputePipelines(VkDevice device, VkPipelineCache arg1, uint32_t count,
                                                                   const VkComputePipelineCreateInfo *infos,
                                                                   const VkAllocationCallbacks *allocator, VkPipeline *output) {
  return createPipelines(device, arg1, count, infos, allocator, output, implVkCreateComputePipelines);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkResetCommandPool(VkDevice device, VkCommandPool pool, VkCommandPoolResetFlags arg2) {
  host::Scope scope(handleArena(pool));
  try {
    const auto result = implVkResetCommandPool(device, pool, arg2);
    if (result == VK_SUCCESS && (arg2 & VK_COMMAND_POOL_RESET_RELEASE_RESOURCES_BIT))
      if (auto *arena = handleArena(pool)) arena->trim();
    return result;
  } catch (const std::bad_alloc &) {
    return VK_ERROR_OUT_OF_HOST_MEMORY;
  }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkAllocateCommandBuffers(VkDevice device, const VkCommandBufferAllocateInfo *info,
                                                                   VkCommandBuffer *output) {
  if (info && output) std::fill_n(output, info->commandBufferCount, VK_NULL_HANDLE);
  host::Scope scope(info ? handleArena(info->commandPool) : nullptr);
  try {
    return implVkAllocateCommandBuffers(device, info, output);
  } catch (const std::bad_alloc &) {
    if (info && output) {
      implVkFreeCommandBuffers(device, info->commandPool, info->commandBufferCount, output);
      std::fill_n(output, info->commandBufferCount, VK_NULL_HANDLE);
    }
    return VK_ERROR_OUT_OF_HOST_MEMORY;
  }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkResetCommandBuffer(VkCommandBuffer command, VkCommandBufferResetFlags arg1) {
  host::Scope scope(handleArena(command));
  try {
    const auto result = implVkResetCommandBuffer(command, arg1);
    if (result == VK_SUCCESS && (arg1 & VK_COMMAND_BUFFER_RESET_RELEASE_RESOURCES_BIT))
      if (auto *arena = handleArena(command)) arena->trim();
    return result;
  } catch (const std::bad_alloc &) {
    return VK_ERROR_OUT_OF_HOST_MEMORY;
  }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkTrimCommandPool(VkDevice device, VkCommandPool pool, VkCommandPoolTrimFlags arg2) {
  host::Scope scope(handleArena(pool));
  try {
    implVkTrimCommandPool(device, pool, arg2);
    if (auto *arena = handleArena(pool)) arena->trim();
  } catch (const std::bad_alloc &) {
    std::lock_guard lock(stateMutex);
    const auto found = devices.find(device);
    if (found != devices.end()) found->second->lost = true;
    stateChanged.notify_all();
  }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateGraphicsPipelines(VkDevice device, VkPipelineCache arg1, uint32_t count,
                                                                    const VkGraphicsPipelineCreateInfo *infos,
                                                                    const VkAllocationCallbacks *allocator, VkPipeline *output) {
  return createPipelines(device, arg1, count, infos, allocator, output, implVkCreateGraphicsPipelines);
}

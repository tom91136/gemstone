#include <algorithm>
#include <array>
#include <cstddef>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <memory>
#include <mutex>
#include <stdexcept>

#include <aspartame/array.hpp>

#include "gemstone_execution_rpc.hpp"
#include "gemstone_vulkan_adapter.hpp"
#include "gemstone_vulkan_graphics.hpp"

#include "resources.hpp"
#include "state.hpp"

using namespace aspartame;

namespace gemstone::vulkan {

template <typename T> static VkResult enumerateOne(uint32_t *count, T *values, const T &value) {
  if (!count) return VK_ERROR_INITIALIZATION_FAILED;
  if (!values) {
    *count = 1;
    return VK_SUCCESS;
  }
  if (*count == 0) return VK_INCOMPLETE;
  values[0] = value;
  *count = 1;
  return VK_SUCCESS;
}

constexpr std::array<VkExtensionProperties, 3> SupportedDeviceExtensions{{
    {.extensionName = VK_EXT_HOST_QUERY_RESET_EXTENSION_NAME, .specVersion = VK_EXT_HOST_QUERY_RESET_SPEC_VERSION},
    {.extensionName = VK_KHR_16BIT_STORAGE_EXTENSION_NAME, .specVersion = VK_KHR_16BIT_STORAGE_SPEC_VERSION},
    {.extensionName = VK_KHR_STORAGE_BUFFER_STORAGE_CLASS_EXTENSION_NAME, .specVersion = VK_KHR_STORAGE_BUFFER_STORAGE_CLASS_SPEC_VERSION},
}};

VkResult implVkEnumerateInstanceVersion(uint32_t *version) {
  if (!version) return VK_ERROR_INITIALIZATION_FAILED;
  const auto supported = configuredApiVersion();
  if (!supported) return VK_ERROR_INITIALIZATION_FAILED;
  *version = *supported;
  return VK_SUCCESS;
}

VkResult implVkEnumerateInstanceExtensionProperties(const char *, uint32_t *count, VkExtensionProperties *properties) {
  VkExtensionProperties property{};
  std::snprintf(property.extensionName, sizeof(property.extensionName), "%s", VK_KHR_GET_PHYSICAL_DEVICE_PROPERTIES_2_EXTENSION_NAME);
  property.specVersion = VK_KHR_GET_PHYSICAL_DEVICE_PROPERTIES_2_SPEC_VERSION;
  return enumerateOne(count, properties, property);
}

VkResult implVkEnumerateInstanceLayerProperties(uint32_t *count, VkLayerProperties *) {
  if (!count) return VK_ERROR_INITIALIZATION_FAILED;
  *count = 0;
  return VK_SUCCESS;
}

VkResult implVkCreateInstance(const VkInstanceCreateInfo *info, const VkAllocationCallbacks *, VkInstance *instance) {
  if (!info || info->sType != VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO || !instance) return VK_ERROR_INITIALIZATION_FAILED;
  const auto supported = configuredApiVersion();
  if (!supported) return VK_ERROR_INITIALIZATION_FAILED;
  // Vulkan 1.1 relaxed instance-version negotiation: newer application versions
  // do not make instance creation fail. The Vulkan 1.0 target remains strict.
  if (*supported < VK_API_VERSION_1_1 && info->pApplicationInfo && (info->pApplicationInfo->apiVersion & ~0xfffU) > *supported)
    return VK_ERROR_INCOMPATIBLE_DRIVER;
  if (info->enabledLayerCount) return VK_ERROR_LAYER_NOT_PRESENT;
  for (uint32_t i = 0; i < info->enabledExtensionCount; ++i)
    if (std::strcmp(info->ppEnabledExtensionNames[i], VK_KHR_GET_PHYSICAL_DEVICE_PROPERTIES_2_EXTENSION_NAME) != 0)
      return VK_ERROR_EXTENSION_NOT_PRESENT;
  *instance = makeHandle<VkInstance>();
  std::scoped_lock const lock(stateMutex);
  instanceProperties2[*instance] = info->enabledExtensionCount != 0;
  return VK_SUCCESS;
}

void implVkDestroyInstance(VkInstance instance, const VkAllocationCallbacks *) {
  {
    std::scoped_lock const lock(stateMutex);
    instanceProperties2.erase(instance);
  }
  deleteHandle(instance);
}

VkResult implVkEnumeratePhysicalDevices(VkInstance instance, uint32_t *count, VkPhysicalDevice *devices) {
  if (!instance) return VK_ERROR_INITIALIZATION_FAILED;
  return enumerateOne(count, devices, physicalDeviceHandle(instance));
}

void implVkGetPhysicalDeviceProperties(VkPhysicalDevice, VkPhysicalDeviceProperties *properties) { fillProperties(properties); }

void implVkGetPhysicalDeviceProperties2(VkPhysicalDevice, VkPhysicalDeviceProperties2 *properties) {
  if (!properties) return;
  fillProperties(&properties->properties);
  fillPropertyChain(properties->pNext);
}

void implVkGetPhysicalDeviceFeatures(VkPhysicalDevice, VkPhysicalDeviceFeatures *features) { fillFeatures(features); }

void implVkGetPhysicalDeviceFeatures2(VkPhysicalDevice, VkPhysicalDeviceFeatures2 *features) {
  if (!features) return;
  fillFeatures(&features->features);
  fillFeatureChain(features->pNext);
}

void implVkGetPhysicalDeviceMemoryProperties(VkPhysicalDevice, VkPhysicalDeviceMemoryProperties *properties) {
  fillMemoryProperties(properties);
}

void implVkGetPhysicalDeviceMemoryProperties2(VkPhysicalDevice, VkPhysicalDeviceMemoryProperties2 *properties) {
  if (properties) fillMemoryProperties(&properties->memoryProperties);
}

void implVkGetPhysicalDeviceQueueFamilyProperties(VkPhysicalDevice, uint32_t *count, VkQueueFamilyProperties *properties) {
  if (!count) return;
  if (!properties) {
    *count = 1;
    return;
  }
  if (*count > 0) {
    properties[0] = {};
    properties[0].queueFlags = VK_QUEUE_GRAPHICS_BIT | VK_QUEUE_COMPUTE_BIT | VK_QUEUE_TRANSFER_BIT;
    properties[0].queueCount = 2;
    properties[0].timestampValidBits = 64;
    properties[0].minImageTransferGranularity = {.width = 1, .height = 1, .depth = 1};
    *count = 1;
  }
}

VkResult implVkEnumerateDeviceExtensionProperties(VkPhysicalDevice, const char *, uint32_t *count, VkExtensionProperties *properties) {
  if (!count) return VK_ERROR_INITIALIZATION_FAILED;
  const auto available = static_cast<uint32_t>(SupportedDeviceExtensions.size());
  if (!properties) {
    *count = available;
    return VK_SUCCESS;
  }
  const auto copied = std::min(*count, available);
  std::copy_n(SupportedDeviceExtensions.begin(), copied, properties);
  *count = copied;
  return copied == available ? VK_SUCCESS : VK_INCOMPLETE;
}

VkResult implVkEnumerateDeviceLayerProperties(VkPhysicalDevice, uint32_t *count, VkLayerProperties *) {
  if (!count) return VK_ERROR_INITIALIZATION_FAILED;
  *count = 0;
  return VK_SUCCESS;
}

VkResult implVkCreateDevice(VkPhysicalDevice physicalDevice, const VkDeviceCreateInfo *info, const VkAllocationCallbacks *,
                            VkDevice *device) {
  if (!physicalDevice || !handleArena(physicalDevice) || !info || !device || info->queueCreateInfoCount == 0)
    return VK_ERROR_INITIALIZATION_FAILED;
  bool hostQueryResetExtension = false;
  for (uint32_t i = 0; i < info->enabledExtensionCount; ++i) {
    const auto *name = info->ppEnabledExtensionNames[i];
    if (SupportedDeviceExtensions ^ none_match([=](const auto &extension) { return std::strcmp(name, extension.extensionName) == 0; }))
      return VK_ERROR_EXTENSION_NOT_PRESENT;
    hostQueryResetExtension |= std::strcmp(name, VK_EXT_HOST_QUERY_RESET_EXTENSION_NAME) == 0;
  }
  if (!featureChainSupported(info->pNext)) return VK_ERROR_FEATURE_NOT_PRESENT;
  VkPhysicalDeviceFeatures supported{};
  fillFeatures(&supported);
  const auto *chained = findChained<VkPhysicalDeviceFeatures2>(info->pNext, VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2);
  const auto *requested = chained ? &chained->features : info->pEnabledFeatures;
  if (requested) {
    std::array<VkBool32, sizeof(VkPhysicalDeviceFeatures) / sizeof(VkBool32)> wanted{};
    std::array<VkBool32, sizeof(VkPhysicalDeviceFeatures) / sizeof(VkBool32)> available{};
    std::memcpy(wanted.data(), requested, sizeof(*requested));
    std::memcpy(available.data(), &supported, sizeof(supported));
    for (size_t i = 0; i < wanted.size(); ++i)
      if (wanted[i] && !available[i]) return VK_ERROR_FEATURE_NOT_PRESENT;
  }
  if (info->queueCreateInfoCount != 1 || info->pQueueCreateInfos[0].queueFamilyIndex != 0 || info->pQueueCreateInfos[0].queueCount == 0
      || info->pQueueCreateInfos[0].queueCount > 2 || info->pQueueCreateInfos[0].flags)
    return VK_ERROR_INITIALIZATION_FAILED;
  const auto result = createQueues(info->pQueueCreateInfos[0].queueCount, device);
  if (result == VK_SUCCESS) {
    std::scoped_lock const lock(stateMutex);
    devices.at(*device)->hostQueryResetExtension = hostQueryResetExtension;
  }
  return result;
}

void implVkDestroyDevice(VkDevice device, const VkAllocationCallbacks *) {
  std::shared_ptr<DeviceState> state;
  {
    std::scoped_lock const lock(stateMutex);
    const auto found = devices.find(device);
    if (found == devices.end()) return;
    state = found->second;
    devices.erase(found);
    for (const auto &queue : state->queues)
      queues.erase(queue->handle);
  }
  stopQueues(state);
  for (const auto &queue : state->queues)
    deleteHandle(queue->handle);
  deleteHandle(device);
}
void implVkGetDeviceQueue(VkDevice device, uint32_t family, uint32_t index, VkQueue *queue) {
  if (!queue) return;
  std::scoped_lock const lock(stateMutex);
  const auto found = devices.find(device);
  *queue =
      family == 0 && found != devices.end() && index < found->second->queues.size() ? found->second->queues[index]->handle : VK_NULL_HANDLE;
}
void implVkGetDeviceQueue2(VkDevice device, const VkDeviceQueueInfo2 *info, VkQueue *queue) {
  if (info && !info->flags) vkGetDeviceQueue(device, info->queueFamilyIndex, info->queueIndex, queue);
  else if (queue) *queue = VK_NULL_HANDLE;
}

// Sparse residency is not advertised. The core capability queries still exist
// and return an empty list without touching the caller's output storage.
void implVkGetPhysicalDeviceSparseImageFormatProperties(VkPhysicalDevice, VkFormat, VkImageType, VkSampleCountFlagBits, VkImageUsageFlags,
                                                        VkImageTiling, uint32_t *count, VkSparseImageFormatProperties *) {
  if (count) *count = 0;
}
void implVkGetPhysicalDeviceSparseImageFormatProperties2(VkPhysicalDevice, const VkPhysicalDeviceSparseImageFormatInfo2 *, uint32_t *count,
                                                         VkSparseImageFormatProperties2 *) {
  if (count) *count = 0;
}
// Every supported image is non-sparse, so it has no sparse memory requirements.
void implVkGetImageSparseMemoryRequirements(VkDevice, VkImage, uint32_t *count, VkSparseImageMemoryRequirements *) {
  if (count) *count = 0;
}
void implVkGetImageSparseMemoryRequirements2(VkDevice, const VkImageSparseMemoryRequirementsInfo2 *, uint32_t *count,
                                             VkSparseImageMemoryRequirements2 *) {
  if (count) *count = 0;
}
void implVkGetPhysicalDeviceQueueFamilyProperties2(VkPhysicalDevice device, uint32_t *count, VkQueueFamilyProperties2 *properties) {
  if (!properties) {
    vkGetPhysicalDeviceQueueFamilyProperties(device, count, nullptr);
    return;
  }
  if (count && *count) vkGetPhysicalDeviceQueueFamilyProperties(device, count, &properties[0].queueFamilyProperties);
}
VkResult implVkCreateEvent(VkDevice, const VkEventCreateInfo *, const VkAllocationCallbacks *, VkEvent *event) {
  if (!event) return VK_ERROR_INITIALIZATION_FAILED;
  *event = makeHandle<VkEvent>();
  std::scoped_lock const lock(stateMutex);
  events[*event] = false;
  return VK_SUCCESS;
}
void implVkDestroyEvent(VkDevice, VkEvent event, const VkAllocationCallbacks *) {
  {
    std::scoped_lock const lock(stateMutex);
    events.erase(event);
    stateChanged.notify_all();
  }
  deleteHandle(event);
}
VkResult implVkGetEventStatus(VkDevice, VkEvent event) {
  std::scoped_lock const lock(stateMutex);
  const auto found = events.find(event);
  if (found == events.end()) return VK_ERROR_DEVICE_LOST;
  return found->second ? VK_EVENT_SET : VK_EVENT_RESET;
}
VkResult implVkSetEvent(VkDevice, VkEvent event) {
  std::scoped_lock const lock(stateMutex);
  const auto found = events.find(event);
  if (found == events.end()) return VK_ERROR_DEVICE_LOST;
  found->second = true;
  stateChanged.notify_all();
  return VK_SUCCESS;
}
VkResult implVkResetEvent(VkDevice, VkEvent event) {
  std::scoped_lock const lock(stateMutex);
  const auto found = events.find(event);
  if (found == events.end()) return VK_ERROR_DEVICE_LOST;
  found->second = false;
  return VK_SUCCESS;
}

VkResult implVkCreateSampler(VkDevice, const VkSamplerCreateInfo *info, const VkAllocationCallbacks *, VkSampler *sampler) {
  if (!info || !sampler) return VK_ERROR_INITIALIZATION_FAILED;
  if (info->anisotropyEnable || info->flags) return VK_ERROR_FEATURE_NOT_PRESENT;
  const auto *reduction = findChained<VkSamplerReductionModeCreateInfo>(info->pNext, VK_STRUCTURE_TYPE_SAMPLER_REDUCTION_MODE_CREATE_INFO);
  if (reduction && reduction->reductionMode != VK_SAMPLER_REDUCTION_MODE_WEIGHTED_AVERAGE) return VK_ERROR_FEATURE_NOT_PRESENT;
  gx::GraphicsSampler value;
  try {
    value = {.minFilter = graphicsFilter(info->minFilter),
             .magFilter = graphicsFilter(info->magFilter),
             .addressU = graphicsAddressMode(info->addressModeU),
             .addressV = graphicsAddressMode(info->addressModeV),
             .addressW = graphicsAddressMode(info->addressModeW),
             .unnormalized = static_cast<bool>(info->unnormalizedCoordinates),
             .compareEnable = static_cast<bool>(info->compareEnable),
             .compareOp = graphicsCompare(info->compareOp),
             .borderColour = graphicsBorderColour(info->borderColor),
             .mipLodBias = info->mipLodBias,
             .minLod = info->minLod,
             .maxLod = info->maxLod,
             .mipmapMode = graphicsMipmapMode(info->mipmapMode)};
  } catch (const std::invalid_argument &) {
    return VK_ERROR_FEATURE_NOT_PRESENT;
  }
  std::scoped_lock const lock(stateMutex);
  *sampler = makeHandle<VkSampler>();
  samplers.emplace(*sampler, value);
  return VK_SUCCESS;
}
void implVkDestroySampler(VkDevice, VkSampler sampler, const VkAllocationCallbacks *) {
  std::scoped_lock const lock(stateMutex);
  samplers.erase(sampler);
  deleteHandle(sampler);
}

VkResult implVkEnumeratePhysicalDeviceGroups(VkInstance instance, uint32_t *count, VkPhysicalDeviceGroupProperties *groups) {
  if (!instance || !count) return VK_ERROR_INITIALIZATION_FAILED;
  if (!groups) {
    *count = 1;
    return VK_SUCCESS;
  }
  if (!*count) return VK_INCOMPLETE;
  groups[0].physicalDeviceCount = 1;
  groups[0].physicalDevices[0] = physicalDeviceHandle(instance);
  groups[0].subsetAllocation = VK_FALSE;
  *count = 1;
  return VK_SUCCESS;
}
} // namespace gemstone::vulkan

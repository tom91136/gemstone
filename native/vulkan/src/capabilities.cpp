#include "runtime.hpp"

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDeviceMask(VkCommandBuffer, uint32_t) {
  // The sole valid mask for our single-device group is 1; no state changes.
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroySamplerYcbcrConversion(VkDevice, VkSamplerYcbcrConversion, const VkAllocationCallbacks *) {
  // Creation is unsupported, so VK_NULL_HANDLE is the only valid input.
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDescriptorSetLayoutSupport(VkDevice, const VkDescriptorSetLayoutCreateInfo *info,
                                                                      VkDescriptorSetLayoutSupport *support) {
  if (!support) return;
  support->supported = VK_FALSE;
  if (!info || info->flags || (info->bindingCount && !info->pBindings)) return;
  uint64_t count = 0;
  for (uint32_t i = 0; i < info->bindingCount; ++i) {
    const auto &binding = info->pBindings[i];
    if (binding.descriptorCount && binding.descriptorType > VK_DESCRIPTOR_TYPE_INPUT_ATTACHMENT) return;
    count += binding.descriptorCount;
  }
  // Matches maxPerSetDescriptors; the host-backed representation has no tighter limit.
  support->supported = count <= gemstone::vulkan::MaxDescriptorsPerSet ? VK_TRUE : VK_FALSE;
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceGroupPeerMemoryFeatures(VkDevice, uint32_t, uint32_t, uint32_t,
                                                                         VkPeerMemoryFeatureFlags *features) {
  // A single-device group has no distinct local and remote device pair.
  if (features) *features = 0;
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceExternalBufferProperties(VkPhysicalDevice,
                                                                                  const VkPhysicalDeviceExternalBufferInfo *,
                                                                                  VkExternalBufferProperties *properties) {
  if (!properties) return;
  properties->externalMemoryProperties = {};
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceExternalFenceProperties(VkPhysicalDevice,
                                                                                 const VkPhysicalDeviceExternalFenceInfo *,
                                                                                 VkExternalFenceProperties *properties) {
  if (!properties) return;
  properties->exportFromImportedHandleTypes = 0;
  properties->compatibleHandleTypes = 0;
  properties->externalFenceFeatures = 0;
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceExternalSemaphoreProperties(VkPhysicalDevice,
                                                                                     const VkPhysicalDeviceExternalSemaphoreInfo *,
                                                                                     VkExternalSemaphoreProperties *properties) {
  if (!properties) return;
  properties->exportFromImportedHandleTypes = 0;
  properties->compatibleHandleTypes = 0;
  properties->externalSemaphoreFeatures = 0;
}

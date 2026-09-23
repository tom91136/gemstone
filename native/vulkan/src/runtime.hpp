#pragma once

#include <array>
#include <map>
#include <optional>
#include <string_view>
#include <unordered_map>
#include <utility>
#include <vector>

#include <vulkan/vulkan.h>

struct __attribute__((visibility("default"))) GemstoneVulkanDescriptorState {
  std::map<VkDescriptorSet, std::map<std::pair<uint32_t, uint32_t>, VkDescriptorBufferInfo>> buffers;

  void update(uint32_t writeCount, const VkWriteDescriptorSet *writes, uint32_t copyCount, const VkCopyDescriptorSet *copies);
  const VkDescriptorBufferInfo *get(VkDescriptorSet set, uint32_t binding, uint32_t element) const;
  bool hasArrayElements(VkDescriptorSet set, uint32_t binding) const;
};

struct GemstoneVulkanDescriptorRange {
  VkDeviceMemory memory;
  VkDeviceSize offset;
  size_t size;
};

struct GemstoneVulkanDescriptorAllocation {
  VkDeviceMemory memory;
  VkDeviceSize offset;
  size_t size;
};

struct GemstoneVulkanDescriptorArgument {
  size_t allocation;
  VkDeviceSize offset;
};

struct GemstoneVulkanDescriptorAllocationPlan {
  std::vector<GemstoneVulkanDescriptorAllocation> allocations;
  std::vector<GemstoneVulkanDescriptorArgument> arguments;
};

__attribute__((visibility("default"))) std::array<uint32_t, 3>
gemstoneVulkanLocalSize(const std::vector<uint8_t> &image, std::string_view entry,
                        const std::unordered_map<uint32_t, uint32_t> &specializations);

__attribute__((visibility("default"))) std::optional<size_t> gemstoneVulkanDescriptorLength(VkDeviceSize bufferSize, VkDeviceSize offset,
                                                                                            VkDeviceSize range);

__attribute__((visibility("default"))) GemstoneVulkanDescriptorAllocationPlan
gemstoneVulkanDescriptorAllocationPlan(const std::vector<GemstoneVulkanDescriptorRange> &ranges);

__attribute__((visibility("default"))) std::optional<std::vector<uint32_t>>
gemstoneVulkanDescriptorBindings(const std::vector<uint8_t> &image, std::string_view entry);

__attribute__((visibility("default"))) PFN_vkVoidFunction gemstoneVulkanInstanceFunction(const char *name, PFN_vkVoidFunction real);
__attribute__((visibility("default"))) PFN_vkVoidFunction gemstoneVulkanDeviceFunction(const char *name, PFN_vkVoidFunction real);
PFN_vkQueueWaitIdle gemstoneVulkanSystemQueueWaitIdle();

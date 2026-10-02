#pragma once

#include <array>
#include <compare>
#include <map>
#include <optional>
#include <span>
#include <string_view>
#include <unordered_map>
#include <utility>
#include <variant>
#include <vector>

#include "../../generated/rpc/gemstone_execution_rpc.hpp"
#include <vulkan/vulkan.h>

namespace gemstone::vulkan {
inline constexpr uint32_t MaxBoundDescriptorSets = 8;
inline constexpr uint32_t MaxDescriptorsPerSet = 4096;
} // namespace gemstone::vulkan

struct __attribute__((visibility("default"))) GemstoneVulkanDescriptorState {
  struct Set {
    using Value = std::variant<VkDescriptorBufferInfo, VkDescriptorImageInfo, VkBufferView>;
    std::map<std::pair<uint32_t, uint32_t>, Value> slots;
    std::map<uint32_t, VkDescriptorSetLayoutBinding> layout;
    std::map<std::pair<uint32_t, uint32_t>, gemstone::execution::GraphicsSampler> immutableSamplers;
    explicit Set(const std::vector<VkDescriptorSetLayoutBinding> &bindings);
  };
  std::map<VkDescriptorSet, Set> sets;

  void update(uint32_t writeCount, const VkWriteDescriptorSet *writes, uint32_t copyCount, const VkCopyDescriptorSet *copies);
  void updateTemplate(VkDescriptorSet set, std::span<const VkDescriptorUpdateTemplateEntry> entries, const void *data);
  template <typename T = VkDescriptorBufferInfo> const T *get(VkDescriptorSet set, uint32_t binding, uint32_t element) const {
    const auto group = sets.find(set);
    if (group == sets.end()) return nullptr;
    const auto &slots = group->second.slots;
    const auto value = slots.find({binding, element});
    return value == slots.end() ? nullptr : std::get_if<T>(&value->second);
  }

private:
  Set::Value *slot(VkDescriptorSet set, uint32_t binding, uint32_t element, uint32_t offset);
};

struct GemstoneVulkanDescriptorSlot {
  uint32_t set, binding, element;
  gemstone::execution::ShaderResourceKind resourceType = gemstone::execution::ShaderResourceKind::Buffer;
  auto operator<=>(const GemstoneVulkanDescriptorSlot &) const = default;
};

struct GemstoneVulkanDescriptorRange {
  VkDeviceMemory memory;
  VkDeviceSize offset;
  size_t size;
};

struct GemstoneVulkanDescriptorArgument {
  size_t allocation;
  VkDeviceSize offset;
};

struct GemstoneVulkanDescriptorAllocationLayout {
  std::vector<GemstoneVulkanDescriptorRange> allocations;
  std::vector<GemstoneVulkanDescriptorArgument> arguments;
};

__attribute__((visibility("default"))) std::optional<size_t> gemstoneVulkanDescriptorLength(VkDeviceSize bufferSize, VkDeviceSize offset,
                                                                                            VkDeviceSize range);

__attribute__((visibility("default"))) GemstoneVulkanDescriptorAllocationLayout
gemstoneVulkanDescriptorAllocationLayout(const std::vector<GemstoneVulkanDescriptorRange> &ranges);

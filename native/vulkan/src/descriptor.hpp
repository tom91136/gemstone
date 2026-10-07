#pragma once

#include <compare>
#include <map>
#include <optional>
#include <span>
#include <utility>
#include <variant>
#include <vector>

#include "../../generated/rpc/gemstone_execution_rpc.hpp"
#include <vulkan/vulkan.h>

namespace gemstone::vulkan {
namespace gx = gemstone::execution;

inline constexpr uint32_t MaxBoundDescriptorSets = 8;
inline constexpr uint32_t MaxDescriptorsPerSet = 4096;

struct DescriptorState {
  struct Set {
    using Value = std::variant<VkDescriptorBufferInfo, VkDescriptorImageInfo, VkBufferView>;
    std::map<std::pair<uint32_t, uint32_t>, Value> slots;
    std::map<uint32_t, VkDescriptorSetLayoutBinding> layout;
    std::map<std::pair<uint32_t, uint32_t>, gx::GraphicsSampler> immutableSamplers;
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

struct DescriptorSlot {
  uint32_t set, binding, element;
  gx::ShaderResourceKind resourceType = gx::ShaderResourceKind::Buffer;
  auto operator<=>(const DescriptorSlot &) const = default;
};

struct DescriptorRange {
  VkDeviceMemory memory;
  VkDeviceSize offset;
  size_t size;
};

struct DescriptorAllocationLayout {
  struct Argument {
    size_t allocation;
    VkDeviceSize offset;
  };
  std::vector<DescriptorRange> allocations;
  std::vector<Argument> arguments;
};

std::optional<size_t> rangeLength(VkDeviceSize size, VkDeviceSize offset, VkDeviceSize range);
DescriptorAllocationLayout descriptorAllocationLayout(const std::vector<DescriptorRange> &ranges);
} // namespace gemstone::vulkan

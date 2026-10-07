#include "descriptor.hpp"

#include <algorithm>
#include <cstring>
#include <functional>
#include <limits>
#include <numeric>
#include <type_traits>

namespace gemstone::vulkan {

DescriptorState::Set::Set(const std::vector<VkDescriptorSetLayoutBinding> &bindings) {
  for (const auto &binding : bindings) {
    layout.emplace(binding.binding, binding);
    for (uint32_t element = 0; element < binding.descriptorCount; ++element) {
      const auto key = std::pair{binding.binding, element};
      const auto type = binding.descriptorType;
      if (type == VK_DESCRIPTOR_TYPE_UNIFORM_TEXEL_BUFFER || type == VK_DESCRIPTOR_TYPE_STORAGE_TEXEL_BUFFER)
        slots.emplace(key, VkBufferView{});
      else if (type == VK_DESCRIPTOR_TYPE_INPUT_ATTACHMENT || type == VK_DESCRIPTOR_TYPE_STORAGE_IMAGE
               || type == VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE || type == VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
               || type == VK_DESCRIPTOR_TYPE_SAMPLER)
        slots.emplace(key, VkDescriptorImageInfo{});
      else slots.emplace(key, VkDescriptorBufferInfo{});
    }
  }
}

DescriptorState::Set::Value *DescriptorState::slot(VkDescriptorSet set, uint32_t binding, uint32_t element, uint32_t offset) {
  uint64_t index = static_cast<uint64_t>(element) + offset;
  const auto record = sets.find(set);
  if (record == sets.end()) return nullptr;
  const auto &layout = record->second.layout;
  auto at = layout.lower_bound(binding);
  while (at != layout.end() && index >= at->second.descriptorCount) {
    index -= at->second.descriptorCount;
    ++at;
  }
  if (at == layout.end()) return nullptr;
  return &record->second.slots.at({at->first, static_cast<uint32_t>(index)});
}

void DescriptorState::updateTemplate(VkDescriptorSet set, std::span<const VkDescriptorUpdateTemplateEntry> entries, const void *data) {
  for (const auto &entry : entries)
    for (uint32_t i = 0; i < entry.descriptorCount; ++i) {
      auto *destination = slot(set, entry.dstBinding, entry.dstArrayElement, i);
      if (!destination) break;
      const auto *source = static_cast<const uint8_t *>(data) + entry.offset + i * entry.stride;
      // Template data may be unaligned. Slots already carry the layout's type.
      std::visit([&](auto &value) { std::memcpy(&value, source, sizeof(value)); }, *destination);
    }
}

void DescriptorState::update(uint32_t writeCount, const VkWriteDescriptorSet *writes, uint32_t copyCount,
                             const VkCopyDescriptorSet *copies) {
  // Runtime descriptor slots are reserved by vkAllocateDescriptorSets. Updates
  // therefore need no host allocation (this void API cannot report host OOM).
  for (uint32_t i = 0; i < writeCount; ++i) {
    const auto &write = writes[i];
    for (uint32_t j = 0; j < write.descriptorCount; ++j) {
      const auto destination = slot(write.dstSet, write.dstBinding, write.dstArrayElement, j);
      if (!destination) break;
      std::visit(
          [&](auto &value) {
            using T = std::decay_t<decltype(value)>;
            if constexpr (std::is_same_v<T, VkBufferView>) {
              if (write.pTexelBufferView) value = write.pTexelBufferView[j];
            } else if constexpr (std::is_same_v<T, VkDescriptorImageInfo>) {
              if (write.pImageInfo) value = write.pImageInfo[j];
            } else if (write.pBufferInfo) value = write.pBufferInfo[j];
          },
          *destination);
    }
  }
  for (uint32_t i = 0; i < copyCount; ++i) {
    const auto &copy = copies[i];
    // Preserve memmove semantics for the descriptor mirror without scratch storage.
    const bool reverse =
        copy.srcSet == copy.dstSet && std::pair{copy.dstBinding, copy.dstArrayElement} > std::pair{copy.srcBinding, copy.srcArrayElement};
    for (uint32_t index = 0; index < copy.descriptorCount; ++index) {
      const uint32_t j = reverse ? copy.descriptorCount - 1 - index : index;
      const auto source = slot(copy.srcSet, copy.srcBinding, copy.srcArrayElement, j);
      const auto destination = slot(copy.dstSet, copy.dstBinding, copy.dstArrayElement, j);
      if (!source || !destination) break;
      *destination = *source;
    }
  }
}

std::optional<size_t> rangeLength(VkDeviceSize size, VkDeviceSize offset, VkDeviceSize range) {
  if (offset > size) return std::nullopt;
  const VkDeviceSize length = range == VK_WHOLE_SIZE ? size - offset : range;
  if (length > size - offset || length > std::numeric_limits<size_t>::max()) return std::nullopt;
  return static_cast<size_t>(length);
}

DescriptorAllocationLayout descriptorAllocationLayout(const std::vector<DescriptorRange> &ranges) {
  DescriptorAllocationLayout layout;
  layout.arguments.resize(ranges.size());
  std::vector<size_t> order(ranges.size());
  std::iota(order.begin(), order.end(), 0);
  std::sort(order.begin(), order.end(), [&](size_t a, size_t b) {
    if (ranges[a].memory != ranges[b].memory) return std::less<VkDeviceMemory>{}(ranges[a].memory, ranges[b].memory);
    return ranges[a].offset < ranges[b].offset;
  });
  // Callers supply validated, nonempty ranges. Touching ranges remain separate;
  // overlapping views share one allocation but keep their individual bounds.
  for (const auto index : order) {
    const auto &range = ranges[index];
    if (layout.allocations.empty() || layout.allocations.back().memory != range.memory
        || range.offset >= layout.allocations.back().offset + layout.allocations.back().size)
      layout.allocations.push_back(range);
    else {
      auto &allocation = layout.allocations.back();
      allocation.size = static_cast<size_t>(std::max(allocation.offset + allocation.size, range.offset + range.size) - allocation.offset);
    }
    layout.arguments[index] = {layout.allocations.size() - 1, range.offset - layout.allocations.back().offset};
  }
  return layout;
}
} // namespace gemstone::vulkan

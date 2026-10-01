#pragma once

#include <algorithm>
#include <array>
#include <climits>
#include <cstdint>
#include <map>
#include <memory>
#include <optional>
#include <string>
#include <vector>

#include "runtime.hpp"

namespace gemstone::vulkan {

struct StageResources {
  struct Allocation {
    uint8_t *data;
    size_t size;
    std::unique_ptr<uint8_t[]> owned;
    VkDeviceMemory memory = VK_NULL_HANDLE;
    VkDeviceSize offset = 0;
  };
  struct Argument {
    size_t allocation, offset, size;
  };
  std::vector<execution::DescriptorBinding> descriptorBindings;
  std::vector<Allocation> allocations;
  std::vector<std::optional<Argument>> arguments;
};
struct BoundStageResources {
  std::vector<execution::DescriptorBinding> descriptorBindings;
  std::vector<std::optional<execution::GraphicsRange>> arguments;
};
// Registration and ownership stay with the caller. Image bindings retain the same
// bounded storage ranges as ordinary arguments for both compute and graphics.
template <typename Register> std::optional<BoundStageResources> bindStageResources(const StageResources &stage, Register registerMemory) {
  std::vector<execution::GraphicsRange> allocations;
  for (const auto &allocation : stage.allocations) {
    if (!allocation.data || !allocation.size || allocation.size > INT32_MAX) return std::nullopt;
    const auto range = registerMemory(allocation);
    if (range.size.value != static_cast<int64_t>(allocation.size)) return std::nullopt;
    allocations.push_back(range);
  }
  BoundStageResources result;
  for (const auto &argument : stage.arguments) {
    if (!argument) {
      const auto index = result.arguments.size();
      if (!std::any_of(stage.descriptorBindings.begin(), stage.descriptorBindings.end(), [&](const auto &binding) {
            return binding.argument.value == static_cast<int32_t>(index)
                   && std::holds_alternative<execution::GraphicsDescriptorSampler>(binding.descriptor);
          }))
        return std::nullopt;
      result.arguments.emplace_back(std::nullopt);
      continue;
    }
    if (argument->allocation >= allocations.size()) return std::nullopt;
    auto view = allocations[argument->allocation];
    if (!argument->size || argument->offset > static_cast<uint64_t>(view.size.value)
        || argument->size > static_cast<uint64_t>(view.size.value) - argument->offset)
      return std::nullopt;
    view.offset.value += argument->offset;
    view.size.value = argument->size;
    result.arguments.push_back(view);
  }
  for (auto binding : stage.descriptorBindings) {
    if (binding.argument.value < 0 || static_cast<size_t>(binding.argument.value) >= result.arguments.size()) return std::nullopt;
    const auto &range = result.arguments[binding.argument.value];
    const auto valid = std::visit(
        [&](auto &descriptor) {
          if constexpr (requires { descriptor.source; }) {
            if (!range) return false;
            descriptor.source.memory = *range;
          }
          return true;
        },
        binding.descriptor);
    if (!valid) return std::nullopt;
    result.descriptorBindings.push_back(std::move(binding));
  }
  return result;
}
std::optional<execution::PreparedShader> prepareShader(const std::vector<uint8_t> &image, const char *entry,
                                                       execution::ShaderStageKind stage, const VkSpecializationInfo *specialization);

} // namespace gemstone::vulkan

// Frozen pre-migration reference, used only by differential tests.
#pragma once

#include <algorithm>
#include <array>

#include <vulkan/vulkan.h>

namespace gemstone::vulkan {
inline std::array<float, 4> blendColor(std::array<float, 4> source, std::array<float, 4> destination, std::array<float, 4> constants,
                                       const VkPipelineColorBlendAttachmentState &state, bool normalized, float minimum = 0.0f) {
  if (normalized)
    for (unsigned lane = 0; lane < 4; ++lane) {
      source[lane] = std::clamp(source[lane], minimum, 1.0f);
      destination[lane] = std::clamp(destination[lane], minimum, 1.0f);
      constants[lane] = std::clamp(constants[lane], minimum, 1.0f);
    }
  const auto factor = [&](VkBlendFactor mode, unsigned lane) {
    switch (mode) {
      case VK_BLEND_FACTOR_ZERO: return 0.0f;
      case VK_BLEND_FACTOR_ONE: return 1.0f;
      case VK_BLEND_FACTOR_SRC_COLOR: return source[lane];
      case VK_BLEND_FACTOR_ONE_MINUS_SRC_COLOR: return 1.0f - source[lane];
      case VK_BLEND_FACTOR_DST_COLOR: return destination[lane];
      case VK_BLEND_FACTOR_ONE_MINUS_DST_COLOR: return 1.0f - destination[lane];
      case VK_BLEND_FACTOR_SRC_ALPHA: return source[3];
      case VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA: return 1.0f - source[3];
      case VK_BLEND_FACTOR_DST_ALPHA: return destination[3];
      case VK_BLEND_FACTOR_ONE_MINUS_DST_ALPHA: return 1.0f - destination[3];
      case VK_BLEND_FACTOR_CONSTANT_COLOR: return constants[lane];
      case VK_BLEND_FACTOR_ONE_MINUS_CONSTANT_COLOR: return 1.0f - constants[lane];
      case VK_BLEND_FACTOR_CONSTANT_ALPHA: return constants[3];
      case VK_BLEND_FACTOR_ONE_MINUS_CONSTANT_ALPHA: return 1.0f - constants[3];
      case VK_BLEND_FACTOR_SRC_ALPHA_SATURATE: return lane == 3 ? 1.0f : std::min(source[3], 1.0f - destination[3]);
      default: return 0.0f; // Pipeline validation rejects dual-source/extension factors.
    }
  };
  std::array<float, 4> result{};
  for (unsigned lane = 0; lane < 4; ++lane) {
    const auto operation = lane == 3 ? state.alphaBlendOp : state.colorBlendOp;
    const auto clampedFactor = [&](VkBlendFactor mode) {
      const float value = factor(mode, lane);
      return normalized ? std::clamp(value, minimum, 1.0f) : value;
    };
    const float src = source[lane] * clampedFactor(lane == 3 ? state.srcAlphaBlendFactor : state.srcColorBlendFactor);
    const float dst = destination[lane] * clampedFactor(lane == 3 ? state.dstAlphaBlendFactor : state.dstColorBlendFactor);
    switch (operation) {
      case VK_BLEND_OP_ADD: result[lane] = src + dst; break;
      case VK_BLEND_OP_SUBTRACT: result[lane] = src - dst; break;
      case VK_BLEND_OP_REVERSE_SUBTRACT: result[lane] = dst - src; break;
      case VK_BLEND_OP_MIN: result[lane] = std::min(source[lane], destination[lane]); break;
      case VK_BLEND_OP_MAX: result[lane] = std::max(source[lane], destination[lane]); break;
      default: break; // Pipeline validation rejects advanced operations.
    }
  }
  return result;
}
} // namespace gemstone::vulkan

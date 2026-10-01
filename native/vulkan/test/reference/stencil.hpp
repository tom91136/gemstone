// Frozen pre-migration reference, used only by differential tests.
#pragma once

#include <algorithm>
#include <cstdint>

#include <vulkan/vulkan.h>

namespace gemstone::vulkan {
// Stencil arithmetic is eight bits; the write mask preserves untouched bits.
inline uint8_t updateStencil(uint8_t stored, const VkStencilOpState &state, VkStencilOp operation) {
  uint32_t value = stored;
  switch (operation) {
    case VK_STENCIL_OP_KEEP: break;
    case VK_STENCIL_OP_ZERO: value = 0; break;
    case VK_STENCIL_OP_REPLACE: value = state.reference; break;
    case VK_STENCIL_OP_INCREMENT_AND_CLAMP: value = std::min(value + 1, 255u); break;
    case VK_STENCIL_OP_DECREMENT_AND_CLAMP: value = value ? value - 1 : 0; break;
    case VK_STENCIL_OP_INVERT: value = ~value; break;
    case VK_STENCIL_OP_INCREMENT_AND_WRAP: ++value; break;
    case VK_STENCIL_OP_DECREMENT_AND_WRAP: --value; break;
    default: break;
  }
  return static_cast<uint8_t>((stored & ~state.writeMask) | (value & state.writeMask));
}
} // namespace gemstone::vulkan

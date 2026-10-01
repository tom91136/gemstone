#pragma once

#include "state.hpp"

namespace gemstone::vulkan::adapter {
void recordIndirectDraw(VkCommandBuffer command, VkBuffer indirect, VkDeviceSize offset, uint32_t drawCount, uint32_t stride, bool indexed);
void recordBufferImageCopy(VkCommandBuffer command, VkBuffer bufferHandle, VkImage imageHandle, uint32_t count,
                           const VkBufferImageCopy *regions, bool toImage);
} // namespace gemstone::vulkan::adapter

#pragma once

#include <array>
#include <cstdint>
#include <optional>
#include <vector>

#include <vulkan/vulkan.h>

namespace gemstone::vulkan::adapter {
struct DeviceState;
struct QueryPool;
struct RenderScope {
  VkRenderPass pass = VK_NULL_HANDLE;
  VkFramebuffer framebuffer = VK_NULL_HANDLE;
  VkRect2D area{};
  uint32_t subpass = 0;
  std::vector<VkClearValue> clearValues;
  std::vector<std::array<uint32_t, 3>> loaded;
  // Attachments are bound to this execution of a render pass. Imageless
  // framebuffers may be reused with different views, including on other queues.
  std::vector<VkImageView> attachments;
};
struct ActiveOcclusion {
  QueryPool *pool;
  uint32_t query, count;
  uint64_t firstSample;
};
struct Execution {
  DeviceState *device;
  RenderScope renderScope;
  uint64_t occlusionSamples = 0;
  std::optional<ActiveOcclusion> activeOcclusion;
};

} // namespace gemstone::vulkan::adapter

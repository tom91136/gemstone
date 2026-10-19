#pragma once

#include <variant>

#include "graphics_state.hpp"

namespace gemstone::vulkan::recorded {
struct Dispatch {
  VkPipeline pipeline = VK_NULL_HANDLE;
  std::array<VkDescriptorSet, MaxBoundDescriptorSets> descriptors{};
  uint32_t x = 1, y = 1, z = 1;
  std::array<uint32_t, 3> base{};
  std::array<uint8_t, gx::GraphicsPushConstantBytes> pushConstants{};
  std::map<DescriptorSlot, uint32_t> dynamicOffsets;
};

struct Copy {
  VkBuffer source = VK_NULL_HANDLE;
  VkBuffer destination = VK_NULL_HANDLE;
  std::vector<VkBufferCopy> regions;
};

struct Resolve {
  VkImage source;
  VkImage destination;
  std::vector<gx::GraphicsImageCopy> copies;
};
struct Blit {
  VkImage source;
  VkImage destination;
  std::vector<gx::GraphicsBlit> copies;
  VkFilter filter;
};
struct ResetQuery {
  VkQueryPool pool;
  uint32_t first;
  uint32_t count;
};
struct BeginQuery {
  VkQueryPool pool;
  uint32_t query;
};
struct EndQuery {
  VkQueryPool pool;
  uint32_t query;
};
struct WriteTimestamp {
  VkQueryPool pool;
  uint32_t query;
};
struct CopyQueryResults {
  VkQueryPool pool;
  uint32_t first;
  uint32_t count;
  VkBuffer destination;
  VkDeviceSize offset;
  VkDeviceSize stride;
  VkQueryResultFlags flags;
};
struct BeginRenderPass {
  VkRenderPass pass;
  VkFramebuffer framebuffer;
  VkRect2D area;
  std::vector<VkClearValue> clear;
  std::vector<VkImageView> attachments;
};
struct NextSubpass {};
struct EndRenderPass {};
struct Draw {
  GraphicsCommandState state;
  uint32_t vertices{};
  uint32_t instances{};
  uint32_t firstVertex{};
  uint32_t firstInstance{};
  bool indexed{};
  int32_t vertexOffset{};
};
struct DrawIndirect {
  GraphicsCommandState state;
  VkBuffer indirect{};
  VkDeviceSize offset{};
  uint32_t drawCount{};
  uint32_t stride{};
  bool indexed{};
};
struct CopyBufferImage {
  VkBuffer bufferHandle;
  VkImage imageHandle;
  std::vector<gx::GraphicsBufferImageCopy> copies;
  bool toImage;
};
struct ClearAttachments {
  std::vector<VkClearAttachment> clears;
  std::vector<VkClearRect> rects;
};
struct ClearColour {
  VkImage handle;
  VkClearColorValue value;
  std::vector<VkImageSubresourceRange> regions;
};
struct ClearDepthStencil {
  VkImage handle;
  VkClearDepthStencilValue clear;
  std::vector<VkImageSubresourceRange> regions;
};
struct CopyImage {
  VkImage source;
  VkImage destination;
  std::vector<gx::GraphicsImageCopy> copies;
};
struct Invalid {};
struct SetEvent {
  VkEvent event;
};
struct WaitEvents {
  std::vector<VkEvent> wait;
};
struct UpdateBuffer {
  VkBuffer buffer;
  VkDeviceSize offset;
  std::vector<uint8_t> snapshot;
};
struct DispatchIndirect {
  Dispatch dispatch;
  VkBuffer buffer{};
  VkDeviceSize offset{};
};
struct ResetEvent {
  VkEvent event;
};
struct FillBuffer {
  VkBuffer buffer;
  VkDeviceSize offset;
  VkDeviceSize size;
  uint32_t value;
};
using Operation =
    std::variant<Dispatch, Copy, FillBuffer, ResetEvent, DispatchIndirect, UpdateBuffer, WaitEvents, SetEvent, Invalid, CopyImage,
                 ClearDepthStencil, ClearColour, ClearAttachments, CopyBufferImage, DrawIndirect, Draw, EndRenderPass, NextSubpass,
                 BeginRenderPass, CopyQueryResults, WriteTimestamp, EndQuery, BeginQuery, ResetQuery, Blit, Resolve>;
} // namespace gemstone::vulkan::recorded

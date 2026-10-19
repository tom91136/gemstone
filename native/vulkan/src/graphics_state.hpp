#pragma once

#include <array>
#include <vector>

#include "descriptor.hpp"
#include "shader.hpp"
#include <vulkan/vulkan.h>

namespace gemstone::vulkan {
inline constexpr uint32_t MaxColorAttachments = 8;
struct GraphicsCommandState {
  VkPipeline pipeline = VK_NULL_HANDLE;
  std::array<VkDescriptorSet, MaxBoundDescriptorSets> descriptors{};
  std::map<DescriptorSlot, uint32_t> dynamicOffsets;
  VkBuffer indexBuffer = VK_NULL_HANDLE;
  VkDeviceSize indexOffset = 0;
  VkIndexType indexType = VK_INDEX_TYPE_UINT16;
  std::array<uint8_t, gx::GraphicsPushConstantBytes> vertexPush{}, fragmentPush{};
  std::array<VkBuffer, 16> vertexBuffers{};
  std::array<VkDeviceSize, 16> vertexOffsets{};
  VkViewport viewport{};
  VkRect2D scissor{};
  bool viewportSet = false, scissorSet = false;
  std::array<float, 3> depthBias{};
  std::array<float, 4> blendConstants{};
  std::array<float, 2> depthBounds{};
  std::array<uint32_t, 2> stencilCompareMask{}, stencilWriteMask{}, stencilReference{};
};
struct GraphicsPipeline {
  gx::PreparedShader vertex, fragment;
  std::vector<VkVertexInputBindingDescription> bindings;
  std::vector<gx::GraphicsVertexAttribute> attributes;
  VkViewport viewport{};
  VkRect2D scissor{};
  gx::GraphicsRasterState raster{};
  gx::GraphicsDepthStencil depthStencil{};
  std::vector<gx::GraphicsBlend> blends;
  gx::GraphicsColour blendConstants{};
  bool dynamicDepthBias = false, dynamicBlendConstants = false;
  bool dynamicViewport = false, dynamicScissor = false, dynamicDepthBounds = false;
  bool dynamicStencilCompareMask = false, dynamicStencilWriteMask = false, dynamicStencilReference = false;
  std::vector<uint32_t> colourAttachments;
  uint32_t depthAttachment = VK_ATTACHMENT_UNUSED;
};
struct Image {
  VkExtent3D extent{};
  VkFormat format{};
  VkDeviceMemory memory = VK_NULL_HANDLE;
  VkDeviceSize offset = 0;
  uint32_t layers = 1;
  uint32_t samples = 1;
  uint32_t mipLevels = 1;
  VkImageType type = VK_IMAGE_TYPE_2D;
  VkImageCreateFlags flags = 0;
  std::vector<VkFormat> viewFormats;
};
struct Framebuffer {
  std::vector<VkImageView> attachments;
  uint32_t width{}, height{}, layers = 1;
  bool imageless = false;
};
struct AttachmentReference {
  uint32_t attachment;
  VkImageLayout layout;
  VkImageAspectFlags aspects = 0;
  std::optional<VkImageLayout> stencilLayout;
  explicit AttachmentReference(uint32_t attachment = VK_ATTACHMENT_UNUSED, VkImageLayout layout = VK_IMAGE_LAYOUT_UNDEFINED)
      : attachment(attachment), layout(layout) {}
};
struct Subpass {
  uint32_t viewMask = 0;
  std::vector<AttachmentReference> inputs;
  std::vector<AttachmentReference> colours;
  std::vector<AttachmentReference> resolves;
  AttachmentReference depth{VK_ATTACHMENT_UNUSED, VK_IMAGE_LAYOUT_UNDEFINED};
  AttachmentReference depthResolve;
  VkResolveModeFlagBits depthResolveMode = VK_RESOLVE_MODE_NONE, stencilResolveMode = VK_RESOLVE_MODE_NONE;
};
struct RenderPass {
  struct Attachment {
    VkFormat format;
    VkSampleCountFlagBits samples;
    VkAttachmentLoadOp loadOp, stencilLoadOp;
  };
  std::vector<Attachment> attachments;
  std::vector<Subpass> subpasses;
};
} // namespace gemstone::vulkan

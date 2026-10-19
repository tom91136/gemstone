#include <algorithm>
#include <array>
#include <bit>
#include <cstddef>
#include <cstdint>
#include <cstdio>
#include <mutex>
#include <utility>
#include <vector>

#include <aspartame/vector.hpp>

#include "gemstone_execution_rpc.hpp"
#include "gemstone_image_formats.hpp"
#include "gemstone_vulkan_adapter.hpp"
#include "gemstone_vulkan_graphics.hpp"
#include <gemstone/trace.hpp>

#include "graphics_transfer.hpp"

using namespace aspartame;

namespace gemstone::vulkan {
namespace {
bool imageFormat(VkFormat format) {
  return colourFormat(format) || compressedImageFormat(format) || depthStencilFormat(format) || format == VK_FORMAT_R32_UINT;
}
void setStencilFaces(std::array<uint32_t, 2> &state, VkStencilFaceFlags faces, uint32_t value) {
  if (faces & VK_STENCIL_FACE_FRONT_BIT) state[0] = value;
  if (faces & VK_STENCIL_FACE_BACK_BIT) state[1] = value;
}
void recordIndirectDraw(VkCommandBuffer command, VkBuffer indirect, VkDeviceSize offset, uint32_t drawCount, uint32_t stride,
                        bool indexed) {
  std::scoped_lock const lock(stateMutex);
  const auto &state = commands.at(command).graphics;
  commands.at(command).operations.emplace_back(recorded::DrawIndirect{
      .state = state, .indirect = indirect, .offset = offset, .drawCount = drawCount, .stride = stride, .indexed = indexed});
}

void recordBufferImageCopy(VkCommandBuffer command, VkBuffer bufferHandle, VkImage imageHandle, uint32_t count,
                           const VkBufferImageCopy *regions, bool toImage) {
  std::vector<gx::GraphicsBufferImageCopy> copies;
  for (uint32_t i = 0; i < count; ++i) {
    const auto &copy = regions[i];
    copies.push_back({.bufferOffset = {static_cast<int64_t>(copy.bufferOffset)},
                      .rowLength = {static_cast<int32_t>(copy.bufferRowLength)},
                      .imageHeight = {static_cast<int32_t>(copy.bufferImageHeight)},
                      .image = graphicsLayers(copy.imageSubresource),
                      .offset = graphicsOffset(copy.imageOffset),
                      .extent = graphicsExtent(copy.imageExtent)});
  }
  std::scoped_lock const lock(stateMutex);
  commands.at(command).operations.emplace_back(
      recorded::CopyBufferImage{.bufferHandle = bufferHandle, .imageHandle = imageHandle, .copies = std::move(copies), .toImage = toImage});
}
} // namespace

void implVkGetPhysicalDeviceFormatProperties(VkPhysicalDevice, VkFormat format, VkFormatProperties *properties) {
  if (!properties) return;
  *properties = {};
  if (colourFormat(format)) {
    properties->linearTilingFeatures = properties->optimalTilingFeatures =
        VK_FORMAT_FEATURE_SAMPLED_IMAGE_BIT | (colourAttachmentFormat(format) ? VK_FORMAT_FEATURE_COLOR_ATTACHMENT_BIT : 0)
        | VK_FORMAT_FEATURE_TRANSFER_SRC_BIT | VK_FORMAT_FEATURE_TRANSFER_DST_BIT | VK_FORMAT_FEATURE_BLIT_SRC_BIT
        | VK_FORMAT_FEATURE_BLIT_DST_BIT;
    if (!integerColourFormat(format)) {
      properties->linearTilingFeatures |= VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT;
      properties->optimalTilingFeatures |= VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT;
    }
  }
  if (compressedImageFormat(format)) {
    properties->optimalTilingFeatures = VK_FORMAT_FEATURE_SAMPLED_IMAGE_BIT | VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT
                                        | VK_FORMAT_FEATURE_BLIT_SRC_BIT | VK_FORMAT_FEATURE_TRANSFER_SRC_BIT
                                        | VK_FORMAT_FEATURE_TRANSFER_DST_BIT;
  }
  if (blendColourFormat(format)) {
    properties->linearTilingFeatures |= VK_FORMAT_FEATURE_COLOR_ATTACHMENT_BLEND_BIT;
    properties->optimalTilingFeatures |= VK_FORMAT_FEATURE_COLOR_ATTACHMENT_BLEND_BIT;
  }
  if (depthStencilFormat(format))
    properties->linearTilingFeatures = properties->optimalTilingFeatures =
        VK_FORMAT_FEATURE_SAMPLED_IMAGE_BIT | (depthFormat(format) ? VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT : 0)
        | VK_FORMAT_FEATURE_DEPTH_STENCIL_ATTACHMENT_BIT | VK_FORMAT_FEATURE_TRANSFER_SRC_BIT | VK_FORMAT_FEATURE_TRANSFER_DST_BIT
        | VK_FORMAT_FEATURE_BLIT_SRC_BIT | VK_FORMAT_FEATURE_BLIT_DST_BIT;
  if (storageColourFormat(format)) {
    properties->linearTilingFeatures |= VK_FORMAT_FEATURE_STORAGE_IMAGE_BIT;
    properties->optimalTilingFeatures |= VK_FORMAT_FEATURE_STORAGE_IMAGE_BIT;
    properties->bufferFeatures |= VK_FORMAT_FEATURE_UNIFORM_TEXEL_BUFFER_BIT | VK_FORMAT_FEATURE_STORAGE_TEXEL_BUFFER_BIT;
    if (format == VK_FORMAT_R32_UINT || format == VK_FORMAT_R32_SINT) {
      properties->linearTilingFeatures |= VK_FORMAT_FEATURE_STORAGE_IMAGE_ATOMIC_BIT;
      properties->optimalTilingFeatures |= VK_FORMAT_FEATURE_STORAGE_IMAGE_ATOMIC_BIT;
      properties->bufferFeatures |= VK_FORMAT_FEATURE_STORAGE_TEXEL_BUFFER_ATOMIC_BIT;
    }
  }
  if (format == VK_FORMAT_B8G8R8A8_UNORM) properties->bufferFeatures |= VK_FORMAT_FEATURE_UNIFORM_TEXEL_BUFFER_BIT;
  if (vertexFormat(format)) properties->bufferFeatures |= VK_FORMAT_FEATURE_VERTEX_BUFFER_BIT;
}

void implVkGetPhysicalDeviceFormatProperties2(VkPhysicalDevice device, VkFormat format, VkFormatProperties2 *properties) {
  if (properties) vkGetPhysicalDeviceFormatProperties(device, format, &properties->formatProperties);
}

VkResult implVkGetPhysicalDeviceImageFormatProperties(VkPhysicalDevice, VkFormat format, VkImageType type, VkImageTiling tiling,
                                                      VkImageUsageFlags usage, VkImageCreateFlags flags,
                                                      VkImageFormatProperties *properties) {
  // Unsupported combinations must also return a fully zeroed core payload.
  if (properties) *properties = {};
  // Uncompressed colour formats share compatibility classes by texel size. Each
  // supported class has a renderable/storage-capable view format. Extended usage
  // applies the requested usage to such views, including non-renderable RGB9E5.
  const bool extendedColour = (flags & VK_IMAGE_CREATE_EXTENDED_USAGE_BIT) && colourFormat(format);
  const VkImageUsageFlags supported = VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT
                                      | ((colourAttachmentFormat(format) || extendedColour) ? VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT : 0)
                                      | (depthStencilFormat(format) ? VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT : 0)
                                      | ((storageColourFormat(format) || extendedColour) ? VK_IMAGE_USAGE_STORAGE_BIT : 0)
                                      | ((colourAttachmentFormat(format) || depthStencilFormat(format) || extendedColour)
                                             ? VK_IMAGE_USAGE_INPUT_ATTACHMENT_BIT | VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT
                                             : 0);
  if (!imageFormat(format) || (compressedImageFormat(format) && tiling != VK_IMAGE_TILING_OPTIMAL)
      || (type != VK_IMAGE_TYPE_2D && type != VK_IMAGE_TYPE_1D && type != VK_IMAGE_TYPE_3D)
      || (flags
          & ~(VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT | VK_IMAGE_CREATE_MUTABLE_FORMAT_BIT | VK_IMAGE_CREATE_EXTENDED_USAGE_BIT
              | VK_IMAGE_CREATE_2D_ARRAY_COMPATIBLE_BIT | VK_IMAGE_CREATE_ALIAS_BIT))
      || (type != VK_IMAGE_TYPE_3D && (flags & VK_IMAGE_CREATE_2D_ARRAY_COMPATIBLE_BIT))
      || (type != VK_IMAGE_TYPE_2D && (flags & VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT)) || (usage & ~supported)
      || (tiling != VK_IMAGE_TILING_OPTIMAL && tiling != VK_IMAGE_TILING_LINEAR))
    return VK_ERROR_FORMAT_NOT_SUPPORTED;
  if (properties) {
    properties->maxExtent = type == VK_IMAGE_TYPE_3D
                                ? VkExtent3D{.width = 2048, .height = 2048, .depth = 2048}
                                : VkExtent3D{.width = 16384, .height = type == VK_IMAGE_TYPE_1D ? 1U : 16384U, .depth = 1};
    properties->maxMipLevels = type == VK_IMAGE_TYPE_3D ? 12 : 15;
    properties->maxArrayLayers = type == VK_IMAGE_TYPE_3D ? 1 : 2048;
    properties->sampleCounts = VK_SAMPLE_COUNT_1_BIT;
    if (type == VK_IMAGE_TYPE_2D && (colourAttachmentFormat(format) || depthStencilFormat(format)) && tiling == VK_IMAGE_TILING_OPTIMAL
        && !(usage & VK_IMAGE_USAGE_STORAGE_BIT) && !(flags & VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT))
      properties->sampleCounts |= VK_SAMPLE_COUNT_4_BIT;
    properties->maxResourceSize = MaxImageResourceSize;
  }
  return VK_SUCCESS;
}

VkResult implVkGetPhysicalDeviceImageFormatProperties2(VkPhysicalDevice device, const VkPhysicalDeviceImageFormatInfo2 *info,
                                                       VkImageFormatProperties2 *properties) {
  if (!info || !properties) return VK_ERROR_INITIALIZATION_FAILED;
  return vkGetPhysicalDeviceImageFormatProperties(device, info->format, info->type, info->tiling, info->usage, info->flags,
                                                  &properties->imageFormatProperties);
}

VkResult implVkCreateImage(VkDevice, const VkImageCreateInfo *info, const VkAllocationCallbacks *, VkImage *image) {
  if (!info || !image) return VK_ERROR_INITIALIZATION_FAILED;
  VkImageFormatProperties limits{};
  if (vkGetPhysicalDeviceImageFormatProperties(VK_NULL_HANDLE, info->format, info->imageType, info->tiling, info->usage, info->flags,
                                               &limits)
          != VK_SUCCESS
      || ((info->flags & VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT)
          && (info->extent.width != info->extent.height || info->arrayLayers < 6 || info->samples != VK_SAMPLE_COUNT_1_BIT))
      || (info->imageType == VK_IMAGE_TYPE_1D && (info->extent.height != 1 || info->samples != VK_SAMPLE_COUNT_1_BIT))
      || !info->extent.width || !info->extent.height || !info->extent.depth || info->extent.width > limits.maxExtent.width
      || info->extent.height > limits.maxExtent.height || info->extent.depth > limits.maxExtent.depth
      || (!info->mipLevels
          || std::cmp_greater(info->mipLevels, std::bit_width(std::max({info->extent.width, info->extent.height, info->extent.depth}))))
      || (info->samples != VK_SAMPLE_COUNT_1_BIT && info->mipLevels != 1)
      || (!info->arrayLayers || info->arrayLayers > limits.maxArrayLayers) || !std::has_single_bit(static_cast<uint32_t>(info->samples))
      || !(info->samples & limits.sampleCounts))
    return VK_ERROR_FORMAT_NOT_SUPPORTED;
  Image candidate{.extent = info->extent,
                  .format = info->format,
                  .memory = VK_NULL_HANDLE,
                  .offset = 0,
                  .layers = info->arrayLayers,
                  .samples = static_cast<uint32_t>(info->samples),
                  .mipLevels = info->mipLevels,
                  .type = info->imageType,
                  .flags = info->flags,
                  .viewFormats = {}};
  if (const auto *list = findChained<VkImageFormatListCreateInfo>(info->pNext, VK_STRUCTURE_TYPE_IMAGE_FORMAT_LIST_CREATE_INFO))
    candidate.viewFormats.assign(list->pViewFormats, list->pViewFormats + list->viewFormatCount);
  if (imageSize(candidate) > MaxImageResourceSize) return VK_ERROR_OUT_OF_DEVICE_MEMORY;
  *image = makeHandle<VkImage>();
  std::scoped_lock const lock(stateMutex);
  images[*image] = std::move(candidate);
  return VK_SUCCESS;
}

void implVkDestroyImage(VkDevice, VkImage image, const VkAllocationCallbacks *) {
  {
    std::scoped_lock const lock(stateMutex);
    images.erase(image);
  }
  deleteHandle(image);
}

void implVkGetImageMemoryRequirements(VkDevice, VkImage image, VkMemoryRequirements *requirements) {
  if (!requirements) return;
  std::scoped_lock const lock(stateMutex);
  *requirements = {};
  const auto found = images.find(image);
  if (found == images.end()) return;
  requirements->size = imageSize(found->second);
  requirements->alignment = 64;
  requirements->memoryTypeBits = 1;
}

void implVkGetImageMemoryRequirements2(VkDevice device, const VkImageMemoryRequirementsInfo2 *info, VkMemoryRequirements2 *requirements) {
  if (info && requirements) {
    vkGetImageMemoryRequirements(device, info->image, &requirements->memoryRequirements);
    if (auto *dedicated = findChained<VkMemoryDedicatedRequirements>(requirements->pNext, VK_STRUCTURE_TYPE_MEMORY_DEDICATED_REQUIREMENTS))
      dedicated->prefersDedicatedAllocation = dedicated->requiresDedicatedAllocation = VK_FALSE;
  }
}

void implVkGetImageSubresourceLayout(VkDevice, VkImage image, const VkImageSubresource *subresource, VkSubresourceLayout *layout) {
  if (!layout) return;
  *layout = {};
  std::scoped_lock const lock(stateMutex);
  const auto found = images.find(image);
  if (found == images.end() || !subresource
      || (!validAspects(found->second.format, subresource->aspectMask) || !std::has_single_bit(subresource->aspectMask))
      || subresource->mipLevel >= found->second.mipLevels || subresource->arrayLayer >= found->second.layers)
    return;
  const auto level = subresource->mipLevel;
  const auto mip = imageLayoutOf(found->second).levels.at(level);
  layout->rowPitch = mip.rowPitch.value;
  layout->depthPitch = mip.slicePitch.value;
  layout->size = mip.layerPitch.value;
  layout->arrayPitch = mip.layerPitch.value;
  layout->offset = mip.offset.value + (layout->arrayPitch * subresource->arrayLayer);
}

VkResult implVkBindImageMemory(VkDevice, VkImage image, VkDeviceMemory memory, VkDeviceSize offset) {
  std::scoped_lock const lock(stateMutex);
  const auto found = images.find(image);
  if (found == images.end()) return VK_ERROR_INITIALIZATION_FAILED;
  auto candidate = found->second;
  candidate.memory = memory;
  candidate.offset = offset;
  if (!imageData(candidate)) return VK_ERROR_OUT_OF_DEVICE_MEMORY;
  found->second = candidate;
  return VK_SUCCESS;
}

VkResult implVkBindImageMemory2(VkDevice device, uint32_t count, const VkBindImageMemoryInfo *infos) {
  for (uint32_t i = 0; i < count; ++i) {
    const auto status = vkBindImageMemory(device, infos[i].image, infos[i].memory, infos[i].memoryOffset);
    if (status != VK_SUCCESS) return status;
  }
  return VK_SUCCESS;
}

VkResult implVkCreateImageView(VkDevice, const VkImageViewCreateInfo *info, const VkAllocationCallbacks *, VkImageView *view) {
  if (!info || !view
      || (info->viewType != VK_IMAGE_VIEW_TYPE_2D && info->viewType != VK_IMAGE_VIEW_TYPE_1D
          && info->viewType != VK_IMAGE_VIEW_TYPE_2D_ARRAY && info->viewType != VK_IMAGE_VIEW_TYPE_1D_ARRAY
          && info->viewType != VK_IMAGE_VIEW_TYPE_3D && info->viewType != VK_IMAGE_VIEW_TYPE_CUBE
          && info->viewType != VK_IMAGE_VIEW_TYPE_CUBE_ARRAY)
      || !imageFormat(info->format) || !validAspects(info->format, info->subresourceRange.aspectMask)
      || static_cast<uint32_t>(info->components.r) > VK_COMPONENT_SWIZZLE_A
      || static_cast<uint32_t>(info->components.g) > VK_COMPONENT_SWIZZLE_A
      || static_cast<uint32_t>(info->components.b) > VK_COMPONENT_SWIZZLE_A
      || static_cast<uint32_t>(info->components.a) > VK_COMPONENT_SWIZZLE_A)
    return VK_ERROR_FORMAT_NOT_SUPPORTED;
  std::scoped_lock const lock(stateMutex);
  if (!images.contains(info->image)) return VK_ERROR_INITIALIZATION_FAILED;
  const auto &source = images.at(info->image);
  if ((source.type == VK_IMAGE_TYPE_1D) != (info->viewType == VK_IMAGE_VIEW_TYPE_1D || info->viewType == VK_IMAGE_VIEW_TYPE_1D_ARRAY))
    return VK_ERROR_FORMAT_NOT_SUPPORTED;
  const bool depthSlices = source.type == VK_IMAGE_TYPE_3D && (source.flags & VK_IMAGE_CREATE_2D_ARRAY_COMPATIBLE_BIT)
                           && (info->viewType == VK_IMAGE_VIEW_TYPE_2D || info->viewType == VK_IMAGE_VIEW_TYPE_2D_ARRAY);
  if (!depthSlices && (source.type == VK_IMAGE_TYPE_3D) != (info->viewType == VK_IMAGE_VIEW_TYPE_3D)) return VK_ERROR_FORMAT_NOT_SUPPORTED;
  const auto baseLevel = info->subresourceRange.baseMipLevel;
  if (baseLevel >= source.mipLevels) return VK_ERROR_FORMAT_NOT_SUPPORTED;
  const auto levels =
      info->subresourceRange.levelCount == VK_REMAINING_MIP_LEVELS ? source.mipLevels - baseLevel : info->subresourceRange.levelCount;
  // Uncompressed classes use texel size; compressed classes are format-family specific.
  const auto sourceCompressed = compressedImageFormat(source.format);
  const auto viewCompressed = compressedImageFormat(info->format);
  const bool compatible = source.format == info->format
                          || ((source.flags & VK_IMAGE_CREATE_MUTABLE_FORMAT_BIT)
                              && ((sourceCompressed && viewCompressed && sourceCompressed->compatibility == viewCompressed->compatibility)
                                  || (!sourceCompressed && !viewCompressed && colourFormat(source.format) && colourFormat(info->format)
                                      && texelSize(source.format) == texelSize(info->format))));
  const auto baseLayer = info->subresourceRange.baseArrayLayer;
  const auto availableLayers = depthSlices ? mipExtent(source, baseLevel).depth : source.layers;
  if (baseLayer >= availableLayers) return VK_ERROR_FORMAT_NOT_SUPPORTED;
  const auto layers =
      info->subresourceRange.layerCount == VK_REMAINING_ARRAY_LAYERS ? availableLayers - baseLayer : info->subresourceRange.layerCount;
  const bool cube = info->viewType == VK_IMAGE_VIEW_TYPE_CUBE || info->viewType == VK_IMAGE_VIEW_TYPE_CUBE_ARRAY;
  const bool arrayed = info->viewType == VK_IMAGE_VIEW_TYPE_1D_ARRAY || info->viewType == VK_IMAGE_VIEW_TYPE_2D_ARRAY
                       || info->viewType == VK_IMAGE_VIEW_TYPE_CUBE_ARRAY;
  if (cube
      && (!(source.flags & VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT) || source.type != VK_IMAGE_TYPE_2D || source.samples != 1
          || source.extent.width != source.extent.height || layers % 6 || (!arrayed && layers != 6)))
    return VK_ERROR_FORMAT_NOT_SUPPORTED;
  const bool listed = source.viewFormats.empty() || source.viewFormats ^ contains(info->format);
  if (!levels || levels > source.mipLevels - baseLevel || (depthSlices && levels != 1) || !compatible || !listed || !layers
      || layers > availableLayers - baseLayer || (!cube && !arrayed && layers != 1))
    return VK_ERROR_FORMAT_NOT_SUPPORTED;
  *view = makeHandle<VkImageView>();
  imageViews[*view] = {.image = info->image,
                       .baseLayer = info->subresourceRange.baseArrayLayer,
                       .level = baseLevel,
                       .levels = levels,
                       .format = info->format,
                       .layers = layers,
                       .aspects = info->subresourceRange.aspectMask,
                       .components = {info->components.r, info->components.g, info->components.b, info->components.a},
                       .depthSlices = depthSlices};
  return VK_SUCCESS;
}

void implVkDestroyImageView(VkDevice, VkImageView view, const VkAllocationCallbacks *) {
  {
    std::scoped_lock const lock(stateMutex);
    imageViews.erase(view);
  }
  deleteHandle(view);
}

namespace {
template <typename Reference> AttachmentReference attachmentReference(const Reference &ref) {
  AttachmentReference result{ref.attachment, ref.layout};
  if constexpr (requires { ref.aspectMask; }) {
    result.aspects = ref.aspectMask;
    if (const auto *stencil =
            findChained<VkAttachmentReferenceStencilLayout>(ref.pNext, VK_STRUCTURE_TYPE_ATTACHMENT_REFERENCE_STENCIL_LAYOUT))
      result.stencilLayout = stencil->stencilLayout;
  }
  return result;
}
template <typename Info> VkResult createRenderPass(const Info *info, VkRenderPass *pass) {
  if (!info || !pass || !info->subpassCount || !info->pSubpasses) return VK_ERROR_INITIALIZATION_FAILED;
  const auto *multiview = findChained<VkRenderPassMultiviewCreateInfo>(info->pNext, VK_STRUCTURE_TYPE_RENDER_PASS_MULTIVIEW_CREATE_INFO);
  if (multiview && multiview->subpassCount && (multiview->subpassCount != info->subpassCount || !multiview->pViewMasks))
    return VK_ERROR_INITIALIZATION_FAILED;
  RenderPass state;
  for (uint32_t i = 0; i < info->attachmentCount; ++i) {
    const auto &a = info->pAttachments[i];
    state.attachments.push_back({a.format, a.samples, a.loadOp, a.stencilLoadOp});
  }
  for (const auto &attachment : state.attachments)
    if ((!colourAttachmentFormat(attachment.format) && !depthStencilFormat(attachment.format))
        || (attachment.samples != VK_SAMPLE_COUNT_1_BIT && attachment.samples != VK_SAMPLE_COUNT_4_BIT))
      return VK_ERROR_FORMAT_NOT_SUPPORTED;
  for (uint32_t index = 0; index < info->subpassCount; ++index) {
    const auto &subpass = info->pSubpasses[index];
    if (subpass.colorAttachmentCount > MaxColorAttachments || subpass.inputAttachmentCount > 8
        || subpass.pipelineBindPoint != VK_PIPELINE_BIND_POINT_GRAPHICS)
      return VK_ERROR_FEATURE_NOT_PRESENT;
    Subpass value;
    if constexpr (requires { subpass.viewMask; }) value.viewMask = subpass.viewMask;
    else value.viewMask = multiview && multiview->subpassCount ? multiview->pViewMasks[index] : 0;
    if (value.viewMask & ~63U) return VK_ERROR_FEATURE_NOT_PRESENT;
    for (uint32_t i = 0; i < subpass.colorAttachmentCount; ++i) {
      value.colours.push_back(attachmentReference(subpass.pColorAttachments[i]));
      if (subpass.pResolveAttachments) value.resolves.push_back(attachmentReference(subpass.pResolveAttachments[i]));
    }
    for (uint32_t i = 0; i < subpass.inputAttachmentCount; ++i)
      value.inputs.push_back(attachmentReference(subpass.pInputAttachments[i]));
    if (subpass.pDepthStencilAttachment) {
      value.depth = attachmentReference(*subpass.pDepthStencilAttachment);
      // aspectMask only selects input attachment aspects.
      value.depth.aspects = 0;
    }
    if constexpr (requires { subpass.pNext; })
      if (const auto *resolve = findChained<VkSubpassDescriptionDepthStencilResolve>(
              subpass.pNext, VK_STRUCTURE_TYPE_SUBPASS_DESCRIPTION_DEPTH_STENCIL_RESOLVE)) {
        if ((resolve->depthResolveMode & ~VK_RESOLVE_MODE_SAMPLE_ZERO_BIT)
            || (resolve->stencilResolveMode & ~VK_RESOLVE_MODE_SAMPLE_ZERO_BIT))
          return VK_ERROR_FEATURE_NOT_PRESENT;
        if (resolve->pDepthStencilResolveAttachment && (resolve->depthResolveMode || resolve->stencilResolveMode)) {
          value.depthResolve = attachmentReference(*resolve->pDepthStencilResolveAttachment);
          value.depthResolveMode = resolve->depthResolveMode;
          value.stencilResolveMode = resolve->stencilResolveMode;
          value.depthResolve.aspects =
              (resolve->depthResolveMode ? VK_IMAGE_ASPECT_DEPTH_BIT : 0) | (resolve->stencilResolveMode ? VK_IMAGE_ASPECT_STENCIL_BIT : 0);
        }
      }
    for (const auto &input : value.inputs)
      if (input.attachment != VK_ATTACHMENT_UNUSED && input.attachment >= info->attachmentCount) return VK_ERROR_FORMAT_NOT_SUPPORTED;
    for (size_t slot = 0; slot < value.colours.size(); ++slot) {
      const auto src = value.colours[slot].attachment;
      if (src != VK_ATTACHMENT_UNUSED && (src >= info->attachmentCount || !colourFormat(state.attachments[src].format)))
        return VK_ERROR_FORMAT_NOT_SUPPORTED;
      const auto dst = value.resolves.empty() ? VK_ATTACHMENT_UNUSED : value.resolves[slot].attachment;
      if (dst != VK_ATTACHMENT_UNUSED
          && (src >= info->attachmentCount || dst >= info->attachmentCount || state.attachments[src].format != state.attachments[dst].format
              || state.attachments[src].samples != VK_SAMPLE_COUNT_4_BIT || state.attachments[dst].samples != VK_SAMPLE_COUNT_1_BIT))
        return VK_ERROR_FORMAT_NOT_SUPPORTED;
    }
    if (value.depth.attachment != VK_ATTACHMENT_UNUSED
        && (value.depth.attachment >= info->attachmentCount || !depthStencilFormat(state.attachments[value.depth.attachment].format)))
      return VK_ERROR_FORMAT_NOT_SUPPORTED;
    // Attachment contents remain resident across subpasses, including preserve attachments.
    state.subpasses.push_back(std::move(value));
  }
  // Legacy render passes may select input aspects via maintenance2.
  if (const auto *aspects = findChained<VkRenderPassInputAttachmentAspectCreateInfo>(
          info->pNext, VK_STRUCTURE_TYPE_RENDER_PASS_INPUT_ATTACHMENT_ASPECT_CREATE_INFO))
    for (uint32_t i = 0; i < aspects->aspectReferenceCount; ++i) {
      const auto &ref = aspects->pAspectReferences[i];
      if (ref.subpass >= state.subpasses.size() || ref.inputAttachmentIndex >= state.subpasses[ref.subpass].inputs.size())
        return VK_ERROR_INITIALIZATION_FAILED;
      state.subpasses[ref.subpass].inputs[ref.inputAttachmentIndex].aspects = ref.aspectMask;
    }
  // Serialised subpasses satisfy view-local and view-global dependencies.
  *pass = makeHandle<VkRenderPass>();
  std::scoped_lock const lock(stateMutex);
  renderPasses[*pass] = std::move(state);
  return VK_SUCCESS;
}

} // namespace

VkResult implVkCreateRenderPass(VkDevice, const VkRenderPassCreateInfo *info, const VkAllocationCallbacks *, VkRenderPass *pass) {
  return createRenderPass(info, pass);
}

void implVkDestroyRenderPass(VkDevice, VkRenderPass pass, const VkAllocationCallbacks *) {
  {
    std::scoped_lock const lock(stateMutex);
    renderPasses.erase(pass);
  }
  deleteHandle(pass);
}

void implVkGetRenderAreaGranularity(VkDevice, VkRenderPass, VkExtent2D *granularity) {
  if (granularity) *granularity = {.width = 1, .height = 1};
}

VkResult implVkCreateFramebuffer(VkDevice, const VkFramebufferCreateInfo *info, const VkAllocationCallbacks *, VkFramebuffer *framebuffer) {
  if (!info || !framebuffer || !info->layers || info->layers > 2048 || !info->width || !info->height || info->width > 16384
      || info->height > 16384 || (info->flags & ~VK_FRAMEBUFFER_CREATE_IMAGELESS_BIT))
    return VK_ERROR_FEATURE_NOT_PRESENT;
  std::scoped_lock const lock(stateMutex);
  const bool imageless = info->flags & VK_FRAMEBUFFER_CREATE_IMAGELESS_BIT;
  if (imageless) {
    const auto *attachments =
        findChained<VkFramebufferAttachmentsCreateInfo>(info->pNext, VK_STRUCTURE_TYPE_FRAMEBUFFER_ATTACHMENTS_CREATE_INFO);
    if (!attachments
        || (attachments->attachmentImageInfoCount != 0
            && (attachments->attachmentImageInfoCount != info->attachmentCount || !attachments->pAttachmentImageInfos)))
      return VK_ERROR_INITIALIZATION_FAILED;
    for (uint32_t i = 0; i < attachments->attachmentImageInfoCount; ++i) {
      const auto &attachment = attachments->pAttachmentImageInfos[i];
      if (attachment.width < info->width || attachment.height < info->height || attachment.layerCount < info->layers)
        return VK_ERROR_INITIALIZATION_FAILED;
    }
  } else {
    if (info->attachmentCount && !info->pAttachments) return VK_ERROR_INITIALIZATION_FAILED;
    for (uint32_t i = 0; i < info->attachmentCount; ++i) {
      const auto view = imageViews.find(info->pAttachments[i]);
      if (view == imageViews.end() || view->second.layers < info->layers) return VK_ERROR_INITIALIZATION_FAILED;
      const auto image = imageSlice(view->second);
      if (image.extent.width < info->width || image.extent.height < info->height) return VK_ERROR_INITIALIZATION_FAILED;
    }
  }
  *framebuffer = makeHandle<VkFramebuffer>();
  Framebuffer state{.attachments = {}, .width = info->width, .height = info->height, .layers = info->layers, .imageless = imageless};
  state.attachments.resize(info->attachmentCount);
  if (!imageless && info->attachmentCount) std::copy_n(info->pAttachments, info->attachmentCount, state.attachments.begin());
  framebuffers[*framebuffer] = std::move(state);
  return VK_SUCCESS;
}

void implVkDestroyFramebuffer(VkDevice, VkFramebuffer framebuffer, const VkAllocationCallbacks *) {
  {
    std::scoped_lock const lock(stateMutex);
    framebuffers.erase(framebuffer);
  }
  deleteHandle(framebuffer);
}

namespace {
gx::GraphicsStencil graphicsStencil(const VkStencilOpState &value) {
  return {.fail = graphicsStencilOp(value.failOp),
          .pass = graphicsStencilOp(value.passOp),
          .depthFail = graphicsStencilOp(value.depthFailOp),
          .compare = graphicsCompare(value.compareOp),
          .compareMask = static_cast<int32_t>(value.compareMask),
          .writeMask = static_cast<int32_t>(value.writeMask),
          .reference = static_cast<int32_t>(value.reference)};
}
} // namespace

VkResult implVkCreateGraphicsPipelines(VkDevice, VkPipelineCache, uint32_t count, const VkGraphicsPipelineCreateInfo *infos,
                                       const VkAllocationCallbacks *, VkPipeline *output) {
  std::scoped_lock const lock(stateMutex);
  for (uint32_t i = 0; i < count; ++i)
    output[i] = VK_NULL_HANDLE;
  for (uint32_t i = 0; i < count; ++i) {
    const auto &info = infos[i];
    const auto unsupported = [](int line) {
      if (gemstone::traceEnabled("vulkan"))
        std::fprintf(stderr, "Gemstone Vulkan: unsupported graphics pipeline at graphics.cpp:%d\n", line);
      return VK_ERROR_FEATURE_NOT_PRESENT;
    };
    if ((info.stageCount != 1 && info.stageCount != 2) || !info.pInputAssemblyState
        || (info.pInputAssemblyState->topology != VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST
            && info.pInputAssemblyState->topology != VK_PRIMITIVE_TOPOLOGY_POINT_LIST
            && info.pInputAssemblyState->topology != VK_PRIMITIVE_TOPOLOGY_LINE_LIST
            && info.pInputAssemblyState->topology != VK_PRIMITIVE_TOPOLOGY_LINE_STRIP
            && info.pInputAssemblyState->topology != VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP
            && info.pInputAssemblyState->topology != VK_PRIMITIVE_TOPOLOGY_TRIANGLE_FAN)
        || (info.pInputAssemblyState->primitiveRestartEnable && info.pInputAssemblyState->topology != VK_PRIMITIVE_TOPOLOGY_LINE_STRIP
            && info.pInputAssemblyState->topology != VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP
            && info.pInputAssemblyState->topology != VK_PRIMITIVE_TOPOLOGY_TRIANGLE_FAN)
        || !info.pRasterizationState
        || (!info.pRasterizationState->rasterizerDiscardEnable
            && (info.pRasterizationState->polygonMode != VK_POLYGON_MODE_FILL || info.pRasterizationState->depthClampEnable
                || !info.pMultisampleState
                || (info.pMultisampleState->rasterizationSamples != VK_SAMPLE_COUNT_1_BIT
                    && info.pMultisampleState->rasterizationSamples != VK_SAMPLE_COUNT_4_BIT)
                || info.pMultisampleState->alphaToOneEnable || !info.pViewportState || info.pViewportState->viewportCount != 1
                || info.pViewportState->scissorCount != 1)))
      return unsupported(__LINE__);
    if (!renderPasses.contains(info.renderPass) || info.subpass >= renderPasses.at(info.renderPass).subpasses.size())
      return unsupported(__LINE__);
    GraphicsPipeline pipeline{};
    pipeline.raster.samples = {1};
    pipeline.raster.sampleMask = -1;
    pipeline.depthStencil.compare = gx::GraphicsCompare::Always;
    pipeline.raster.topology = graphicsTopology(info.pInputAssemblyState->topology);
    pipeline.raster.restart = info.pInputAssemblyState->primitiveRestartEnable;
    pipeline.raster.discard = info.pRasterizationState->rasterizerDiscardEnable;
    const auto &renderPass = renderPasses.at(info.renderPass);
    const auto &subpass = renderPass.subpasses.at(info.subpass);
    if (!pipeline.raster.discard) {
      pipeline.depthAttachment = subpass.depth.attachment;
      pipeline.raster.samples = {static_cast<int32_t>(info.pMultisampleState->rasterizationSamples)};
      pipeline.raster.alphaToCoverage = info.pMultisampleState->alphaToCoverageEnable;
      pipeline.raster.sampleShading = info.pMultisampleState->sampleShadingEnable && info.pMultisampleState->minSampleShading > 0;
      pipeline.raster.sampleMask = info.pMultisampleState->pSampleMask ? static_cast<int32_t>(info.pMultisampleState->pSampleMask[0]) : -1;
      const bool hasColour = subpass.colours ^ exists([](const auto &ref) { return ref.attachment != VK_ATTACHMENT_UNUSED; });
      if (hasColour
          && (!info.pColorBlendState || info.pColorBlendState->logicOpEnable
              || info.pColorBlendState->attachmentCount != subpass.colours.size()))
        return unsupported(__LINE__);
      if (hasColour) {
        const auto *c = info.pColorBlendState->blendConstants;
        pipeline.blendConstants = {.r = c[0], .g = c[1], .b = c[2], .a = c[3]};
      }
      for (size_t slot = 0; slot < subpass.colours.size(); ++slot) {
        const auto attachment = subpass.colours[slot].attachment;
        pipeline.colourAttachments.push_back(attachment);
        const auto blend = hasColour ? info.pColorBlendState->pAttachments[slot] : VkPipelineColorBlendAttachmentState{};
        gx::GraphicsBlend converted{};
        converted.writeMask = static_cast<int32_t>(blend.colorWriteMask);
        if (attachment == VK_ATTACHMENT_UNUSED) {
          pipeline.blends.push_back(converted);
          continue;
        }
        if (renderPass.attachments.at(attachment).samples != pipeline.raster.samples.value) return unsupported(__LINE__);
        if (blend.blendEnable) {
          if (!blendColourFormat(renderPass.attachments.at(attachment).format) || blend.colorBlendOp > VK_BLEND_OP_MAX
              || blend.alphaBlendOp > VK_BLEND_OP_MAX)
            return unsupported(__LINE__);
          for (const auto factor :
               {blend.srcColorBlendFactor, blend.dstColorBlendFactor, blend.srcAlphaBlendFactor, blend.dstAlphaBlendFactor})
            if (factor > VK_BLEND_FACTOR_SRC_ALPHA_SATURATE) return unsupported(__LINE__);
          converted = {.enabled = true,
                       .sourceColour = graphicsBlendFactor(blend.srcColorBlendFactor),
                       .destinationColour = graphicsBlendFactor(blend.dstColorBlendFactor),
                       .colourOp = graphicsBlendOp(blend.colorBlendOp),
                       .sourceAlpha = graphicsBlendFactor(blend.srcAlphaBlendFactor),
                       .destinationAlpha = graphicsBlendFactor(blend.dstAlphaBlendFactor),
                       .alphaOp = graphicsBlendOp(blend.alphaBlendOp),
                       .writeMask = static_cast<int32_t>(blend.colorWriteMask)};
        }
        pipeline.blends.push_back(converted);
      }
      if (pipeline.depthAttachment != VK_ATTACHMENT_UNUSED
          && renderPass.attachments.at(pipeline.depthAttachment).samples != pipeline.raster.samples.value)
        return unsupported(__LINE__);
      if (info.pDepthStencilState) {
        const auto &depth = *info.pDepthStencilState;
        pipeline.depthStencil = {.depthTest = static_cast<bool>(depth.depthTestEnable),
                                 .depthWrite = static_cast<bool>(depth.depthWriteEnable),
                                 .compare = depth.depthTestEnable ? graphicsCompare(depth.depthCompareOp) : gx::GraphicsCompare::Always,
                                 .boundsTest = static_cast<bool>(depth.depthBoundsTestEnable),
                                 .minBounds = depth.minDepthBounds,
                                 .maxBounds = depth.maxDepthBounds,
                                 .stencilTest = static_cast<bool>(depth.stencilTestEnable),
                                 .front = depth.stencilTestEnable ? graphicsStencil(depth.front) : gx::GraphicsStencil{},
                                 .back = depth.stencilTestEnable ? graphicsStencil(depth.back) : gx::GraphicsStencil{}};
      }
    }
    for (uint32_t j = 0; j < info.stageCount; ++j) {
      const auto &stage = info.pStages[j];
      if (pipeline.raster.discard && stage.stage == VK_SHADER_STAGE_FRAGMENT_BIT) continue;
      const auto module = modules.find(stage.module);
      if (module == modules.end()) return VK_ERROR_INITIALIZATION_FAILED;
      if (stage.stage != VK_SHADER_STAGE_VERTEX_BIT && stage.stage != VK_SHADER_STAGE_FRAGMENT_BIT) return unsupported(__LINE__);
      const auto kind = stage.stage == VK_SHADER_STAGE_VERTEX_BIT ? gx::ShaderStageKind::Vertex : gx::ShaderStageKind::Fragment;
      auto shader = prepareShader(module->second, stage.pName, kind, stage.pSpecializationInfo);
      if (!shader) return unsupported(__LINE__);
      if (kind == gx::ShaderStageKind::Vertex) pipeline.vertex = std::move(*shader);
      else pipeline.fragment = std::move(*shader);
    }
    if (pipeline.vertex.image.empty()) return VK_ERROR_INITIALIZATION_FAILED;
    if (info.pVertexInputState) {
      const auto &v = *info.pVertexInputState;
      for (uint32_t i = 0; i < v.vertexAttributeDescriptionCount; ++i) {
        const auto &attribute = v.pVertexAttributeDescriptions[i];
        const auto format = vertexFormat(attribute.format);
        if (!format) return unsupported(__LINE__);
        gx::GraphicsVertexFormat converted{.encoding = format->encoding, .widths = {}, .bytes = {format->bytes}, .bgra = format->bgra};
        for (unsigned lane = 0; lane < format->components; ++lane)
          converted.widths.push_back({format->widths[lane]});
        pipeline.attributes.push_back({.location = {static_cast<int32_t>(attribute.location)},
                                       .binding = {static_cast<int32_t>(attribute.binding)},
                                       .offset = {static_cast<int32_t>(attribute.offset)},
                                       .format = std::move(converted)});
      }
      for (uint32_t i = 0; i < v.vertexBindingDescriptionCount; ++i) {
        const auto &binding = v.pVertexBindingDescriptions[i];
        const bool used = pipeline.attributes ^ exists([&](const auto &attribute) {
                            return static_cast<uint32_t>(attribute.binding.value) == binding.binding
                                   && pipeline.vertex.interfaces ^ exists([&](const auto &record) {
                                        return record.direction == gx::ShaderDirection::Input
                                               && record.fields ^ exists([&](const auto &field) {
                                                    return field.location && field.location->value == attribute.location.value;
                                                  });
                                      });
                          });
        if (used) pipeline.bindings.push_back(binding);
      }
    }
    if (info.pDynamicState)
      for (uint32_t d = 0; d < info.pDynamicState->dynamicStateCount; ++d) {
        const auto state = info.pDynamicState->pDynamicStates[d];
        if (state == VK_DYNAMIC_STATE_DEPTH_BIAS) pipeline.dynamicDepthBias = true;
        else if (state == VK_DYNAMIC_STATE_VIEWPORT) pipeline.dynamicViewport = true;
        else if (state == VK_DYNAMIC_STATE_SCISSOR) pipeline.dynamicScissor = true;
        else if (state == VK_DYNAMIC_STATE_DEPTH_BOUNDS) pipeline.dynamicDepthBounds = true;
        else if (state == VK_DYNAMIC_STATE_STENCIL_COMPARE_MASK) pipeline.dynamicStencilCompareMask = true;
        else if (state == VK_DYNAMIC_STATE_STENCIL_WRITE_MASK) pipeline.dynamicStencilWriteMask = true;
        else if (state == VK_DYNAMIC_STATE_STENCIL_REFERENCE) pipeline.dynamicStencilReference = true;
        else if (state == VK_DYNAMIC_STATE_BLEND_CONSTANTS) pipeline.dynamicBlendConstants = true;
        // Only width-one lines are advertised, so line width needs no dynamic storage.
        else if (state != VK_DYNAMIC_STATE_LINE_WIDTH) return unsupported(__LINE__);
      }
    if (!pipeline.raster.discard) {
      if (info.pViewportState->pViewports) pipeline.viewport = info.pViewportState->pViewports[0];
      if (info.pViewportState->pScissors) pipeline.scissor = info.pViewportState->pScissors[0];
    }
    const auto &raster = *info.pRasterizationState;
    pipeline.raster.biasEnabled = raster.depthBiasEnable;
    pipeline.raster.biasConstant = raster.depthBiasConstantFactor;
    pipeline.raster.biasClamp = raster.depthBiasClamp;
    pipeline.raster.biasSlope = raster.depthBiasSlopeFactor;
    pipeline.raster.cullFront = raster.cullMode & VK_CULL_MODE_FRONT_BIT;
    pipeline.raster.cullBack = raster.cullMode & VK_CULL_MODE_BACK_BIT;
    pipeline.raster.frontCounterClockwise = raster.frontFace == VK_FRONT_FACE_COUNTER_CLOCKWISE;
    output[i] = makeHandle<VkPipeline>();
    graphicsPipelines[output[i]] = std::move(pipeline);
  }
  return VK_SUCCESS;
}

void implVkCmdBindVertexBuffers(VkCommandBuffer command, uint32_t first, uint32_t count, const VkBuffer *values,
                                const VkDeviceSize *offsets) {
  std::scoped_lock const lock(stateMutex);
  auto &state = commands[command].graphics;
  for (uint32_t i = 0; i < count && first + i < state.vertexBuffers.size(); ++i) {
    state.vertexBuffers[first + i] = values[i];
    state.vertexOffsets[first + i] = offsets[i];
  }
}

void implVkCmdSetViewport(VkCommandBuffer command, uint32_t first, uint32_t count, const VkViewport *viewports) {
  if (first || !count) return;
  std::scoped_lock const lock(stateMutex);
  commands[command].graphics.viewport = viewports[0];
  commands[command].graphics.viewportSet = true;
}

void implVkCmdSetScissor(VkCommandBuffer command, uint32_t first, uint32_t count, const VkRect2D *scissors) {
  if (first || !count) return;
  std::scoped_lock const lock(stateMutex);
  commands[command].graphics.scissor = scissors[0];
  commands[command].graphics.scissorSet = true;
}

void implVkCmdBeginRenderPass(VkCommandBuffer command, const VkRenderPassBeginInfo *info, VkSubpassContents) {
  if (!info) return;
  std::scoped_lock const lock(stateMutex);
  auto &state = commands[command];
  std::vector<VkClearValue> clear;
  if (info->clearValueCount) clear.assign(info->pClearValues, info->pClearValues + info->clearValueCount);
  auto *const framebuffer = info->framebuffer;
  auto *const pass = info->renderPass;
  const auto area = info->renderArea;
  auto attachments = framebuffers.at(framebuffer).attachments;
  if (framebuffers.at(framebuffer).imageless) {
    // Copy client arrays while recording; their lifetime ends after this call.
    const auto *bound = findChained<VkRenderPassAttachmentBeginInfo>(info->pNext, VK_STRUCTURE_TYPE_RENDER_PASS_ATTACHMENT_BEGIN_INFO);
    if (bound && bound->attachmentCount == attachments.size() && bound->pAttachments)
      std::copy_n(bound->pAttachments, bound->attachmentCount, attachments.begin());
  }
  state.operations.emplace_back(recorded::BeginRenderPass{
      .pass = pass, .framebuffer = framebuffer, .area = area, .clear = std::move(clear), .attachments = std::move(attachments)});
}

void implVkCmdNextSubpass(VkCommandBuffer command, VkSubpassContents) {
  std::scoped_lock const lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::NextSubpass{});
}

void implVkCmdEndRenderPass(VkCommandBuffer command) {
  std::scoped_lock const lock(stateMutex);
  auto &state = commands[command];
  state.operations.emplace_back(recorded::EndRenderPass{});
}

void implVkCmdDraw(VkCommandBuffer command, uint32_t vertices, uint32_t instances, uint32_t firstVertex, uint32_t firstInstance) {
  std::scoped_lock const lock(stateMutex);
  const auto &state = commands[command].graphics;
  commands[command].operations.emplace_back(recorded::Draw{.state = state,
                                                           .vertices = vertices,
                                                           .instances = instances,
                                                           .firstVertex = firstVertex,
                                                           .firstInstance = firstInstance,
                                                           .indexed = false,
                                                           .vertexOffset = 0});
}

void implVkCmdBindIndexBuffer(VkCommandBuffer command, VkBuffer buffer, VkDeviceSize offset, VkIndexType type) {
  std::scoped_lock const lock(stateMutex);
  auto &state = commands.at(command).graphics;
  state.indexBuffer = buffer;
  state.indexOffset = offset;
  state.indexType = type;
}

void implVkCmdDrawIndexed(VkCommandBuffer command, uint32_t indices, uint32_t instances, uint32_t firstIndex, int32_t vertexOffset,
                          uint32_t firstInstance) {
  std::scoped_lock const lock(stateMutex);
  const auto &state = commands.at(command).graphics;
  commands.at(command).operations.emplace_back(recorded::Draw{.state = state,
                                                              .vertices = indices,
                                                              .instances = instances,
                                                              .firstVertex = firstIndex,
                                                              .firstInstance = firstInstance,
                                                              .indexed = true,
                                                              .vertexOffset = vertexOffset});
}

void implVkCmdDrawIndirect(VkCommandBuffer command, VkBuffer buffer, VkDeviceSize offset, uint32_t count, uint32_t stride) {
  recordIndirectDraw(command, buffer, offset, count, stride, false);
}

void implVkCmdDrawIndexedIndirect(VkCommandBuffer command, VkBuffer buffer, VkDeviceSize offset, uint32_t count, uint32_t stride) {
  recordIndirectDraw(command, buffer, offset, count, stride, true);
}

void implVkCmdCopyImageToBuffer(VkCommandBuffer command, VkImage source, VkImageLayout, VkBuffer destination, uint32_t count,
                                const VkBufferImageCopy *regions) {
  recordBufferImageCopy(command, destination, source, count, regions, false);
}

void implVkCmdCopyBufferToImage(VkCommandBuffer command, VkBuffer source, VkImage destination, VkImageLayout, uint32_t count,
                                const VkBufferImageCopy *regions) {
  recordBufferImageCopy(command, source, destination, count, regions, true);
}

void implVkCmdClearAttachments(VkCommandBuffer command, uint32_t attachmentCount, const VkClearAttachment *attachments, uint32_t rectCount,
                               const VkClearRect *rectangles) {
  std::vector<VkClearAttachment> clears(attachments, attachments + attachmentCount);
  std::vector<VkClearRect> rects(rectangles, rectangles + rectCount);
  std::scoped_lock const lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::ClearAttachments{.clears = std::move(clears), .rects = std::move(rects)});
}

void implVkCmdClearColorImage(VkCommandBuffer command, VkImage handle, VkImageLayout, const VkClearColorValue *colour, uint32_t count,
                              const VkImageSubresourceRange *ranges) {
  const auto value = *colour;
  std::vector<VkImageSubresourceRange> regions(ranges, ranges + count);
  std::scoped_lock const lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::ClearColour{.handle = handle, .value = value, .regions = std::move(regions)});
}

void implVkCmdClearDepthStencilImage(VkCommandBuffer command, VkImage handle, VkImageLayout, const VkClearDepthStencilValue *value,
                                     uint32_t count, const VkImageSubresourceRange *ranges) {
  const VkClearDepthStencilValue clear = *value;
  std::vector<VkImageSubresourceRange> regions(ranges, ranges + count);
  std::scoped_lock const lock(stateMutex);
  commands.at(command).operations.emplace_back(
      recorded::ClearDepthStencil{.handle = handle, .clear = clear, .regions = std::move(regions)});
}

void implVkCmdCopyImage(VkCommandBuffer command, VkImage source, VkImageLayout, VkImage destination, VkImageLayout, uint32_t count,
                        const VkImageCopy *regions) {
  std::vector<gx::GraphicsImageCopy> copies;
  copies.reserve(count);
  for (uint32_t i = 0; i < count; ++i)
    copies.push_back(graphicsCopy(regions[i]));
  std::scoped_lock const lock(stateMutex);
  commands.at(command).operations.emplace_back(
      recorded::CopyImage{.source = source, .destination = destination, .copies = std::move(copies)});
}

// Only width-one lines are advertised; valid line-width commands carry no state.
void implVkCmdSetLineWidth(VkCommandBuffer, float) {}

void implVkCmdSetDepthBias(VkCommandBuffer command, float constant, float clamp, float slope) {
  std::scoped_lock const lock(stateMutex);
  commands.at(command).graphics.depthBias = {constant, clamp, slope};
}

void implVkCmdSetBlendConstants(VkCommandBuffer command, const float values[4]) {
  std::scoped_lock const lock(stateMutex);
  std::copy_n(values, 4, commands.at(command).graphics.blendConstants.begin());
}

void implVkCmdSetDepthBounds(VkCommandBuffer command, float minimum, float maximum) {
  std::scoped_lock const lock(stateMutex);
  commands.at(command).graphics.depthBounds = {minimum, maximum};
}

void implVkCmdSetStencilCompareMask(VkCommandBuffer command, VkStencilFaceFlags faces, uint32_t value) {
  std::scoped_lock const lock(stateMutex);
  setStencilFaces(commands.at(command).graphics.stencilCompareMask, faces, value);
}

void implVkCmdSetStencilWriteMask(VkCommandBuffer command, VkStencilFaceFlags faces, uint32_t value) {
  std::scoped_lock const lock(stateMutex);
  setStencilFaces(commands.at(command).graphics.stencilWriteMask, faces, value);
}

void implVkCmdSetStencilReference(VkCommandBuffer command, VkStencilFaceFlags faces, uint32_t value) {
  std::scoped_lock const lock(stateMutex);
  setStencilFaces(commands.at(command).graphics.stencilReference, faces, value);
}

VkResult implVkCreateRenderPass2(VkDevice, const VkRenderPassCreateInfo2 *info, const VkAllocationCallbacks *, VkRenderPass *pass) {
  return createRenderPass(info, pass);
}

void implVkCmdBeginRenderPass2(VkCommandBuffer command, const VkRenderPassBeginInfo *info, const VkSubpassBeginInfo *begin) {
  vkCmdBeginRenderPass(command, info, begin ? begin->contents : VK_SUBPASS_CONTENTS_INLINE);
}

void implVkCmdNextSubpass2(VkCommandBuffer command, const VkSubpassBeginInfo *begin, const VkSubpassEndInfo *) {
  vkCmdNextSubpass(command, begin ? begin->contents : VK_SUBPASS_CONTENTS_INLINE);
}

void implVkCmdEndRenderPass2(VkCommandBuffer command, const VkSubpassEndInfo *) { vkCmdEndRenderPass(command); }

void implVkCmdResolveImage(VkCommandBuffer command, VkImage source, VkImageLayout, VkImage destination, VkImageLayout, uint32_t count,
                           const VkImageResolve *regions) {
  std::vector<gx::GraphicsImageCopy> copies;
  copies.reserve(count);
  for (uint32_t i = 0; i < count; ++i)
    copies.push_back(graphicsCopy(regions[i]));
  std::scoped_lock const lock(stateMutex);
  commands.at(command).operations.emplace_back(
      recorded::Resolve{.source = source, .destination = destination, .copies = std::move(copies)});
}

void implVkCmdBlitImage(VkCommandBuffer command, VkImage source, VkImageLayout, VkImage destination, VkImageLayout, uint32_t count,
                        const VkImageBlit *regions, VkFilter filter) {
  std::vector<gx::GraphicsBlit> copies;
  for (uint32_t i = 0; i < count; ++i) {
    const auto &copy = regions[i];
    copies.push_back({.source = graphicsLayers(copy.srcSubresource),
                      .sourceBegin = graphicsOffset(copy.srcOffsets[0]),
                      .sourceEnd = graphicsOffset(copy.srcOffsets[1]),
                      .destination = graphicsLayers(copy.dstSubresource),
                      .destinationBegin = graphicsOffset(copy.dstOffsets[0]),
                      .destinationEnd = graphicsOffset(copy.dstOffsets[1])});
  }
  std::scoped_lock const lock(stateMutex);
  commands.at(command).operations.emplace_back(
      recorded::Blit{.source = source, .destination = destination, .copies = std::move(copies), .filter = filter});
}
} // namespace gemstone::vulkan

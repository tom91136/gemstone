#include <algorithm>
#include <bit>
#include <cmath>
#include <limits>

#include "gemstone_image_formats.hpp"
#include "gemstone_vulkan_adapter.hpp"
#include "gemstone_vulkan_graphics.hpp"
#include <gemstone/trace.hpp>

#include "graphics_commands.hpp"
#include "graphics_transfer.hpp"

namespace gemstone::vulkan::adapter {
void impl_vkGetPhysicalDeviceFormatProperties(VkPhysicalDevice, VkFormat format, VkFormatProperties *properties) {
  if (!properties) return;
  *properties = {};
  if (colorFormat(format)) {
    properties->linearTilingFeatures = properties->optimalTilingFeatures =
        VK_FORMAT_FEATURE_SAMPLED_IMAGE_BIT | (colorAttachmentFormat(format) ? VK_FORMAT_FEATURE_COLOR_ATTACHMENT_BIT : 0)
        | VK_FORMAT_FEATURE_TRANSFER_SRC_BIT | VK_FORMAT_FEATURE_TRANSFER_DST_BIT | VK_FORMAT_FEATURE_BLIT_SRC_BIT
        | VK_FORMAT_FEATURE_BLIT_DST_BIT;
    if (!integerColorFormat(format)) {
      properties->linearTilingFeatures |= VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT;
      properties->optimalTilingFeatures |= VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT;
    }
  }
  if (compressedImageFormat(format)) {
    properties->optimalTilingFeatures = VK_FORMAT_FEATURE_SAMPLED_IMAGE_BIT | VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT
                                        | VK_FORMAT_FEATURE_BLIT_SRC_BIT | VK_FORMAT_FEATURE_TRANSFER_SRC_BIT
                                        | VK_FORMAT_FEATURE_TRANSFER_DST_BIT;
  }
  if (blendColorFormat(format)) {
    properties->linearTilingFeatures |= VK_FORMAT_FEATURE_COLOR_ATTACHMENT_BLEND_BIT;
    properties->optimalTilingFeatures |= VK_FORMAT_FEATURE_COLOR_ATTACHMENT_BLEND_BIT;
  }
  if (depthStencilFormat(format))
    properties->linearTilingFeatures = properties->optimalTilingFeatures =
        VK_FORMAT_FEATURE_SAMPLED_IMAGE_BIT | (depthFormat(format) ? VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT : 0)
        | VK_FORMAT_FEATURE_DEPTH_STENCIL_ATTACHMENT_BIT | VK_FORMAT_FEATURE_TRANSFER_SRC_BIT | VK_FORMAT_FEATURE_TRANSFER_DST_BIT
        | VK_FORMAT_FEATURE_BLIT_SRC_BIT | VK_FORMAT_FEATURE_BLIT_DST_BIT;
  if (storageColorFormat(format)) {
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

void impl_vkGetPhysicalDeviceFormatProperties2(VkPhysicalDevice device, VkFormat format, VkFormatProperties2 *properties) {
  if (properties) vkGetPhysicalDeviceFormatProperties(device, format, &properties->formatProperties);
}

VkResult impl_vkGetPhysicalDeviceImageFormatProperties(VkPhysicalDevice, VkFormat format, VkImageType type, VkImageTiling tiling,
                                                       VkImageUsageFlags usage, VkImageCreateFlags flags,
                                                       VkImageFormatProperties *properties) {
  // Unsupported combinations must also return a fully zeroed core payload.
  if (properties) *properties = {};
  // Uncompressed color formats share compatibility classes by texel size. Each
  // supported class has a renderable/storage-capable view format. Extended usage
  // applies the requested usage to such views, including non-renderable RGB9E5.
  const bool extendedColor = (flags & VK_IMAGE_CREATE_EXTENDED_USAGE_BIT) && colorFormat(format);
  const VkImageUsageFlags supported = VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT
                                      | ((colorAttachmentFormat(format) || extendedColor) ? VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT : 0)
                                      | (depthStencilFormat(format) ? VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT : 0)
                                      | ((storageColorFormat(format) || extendedColor) ? VK_IMAGE_USAGE_STORAGE_BIT : 0)
                                      | ((colorAttachmentFormat(format) || depthStencilFormat(format) || extendedColor)
                                             ? VK_IMAGE_USAGE_INPUT_ATTACHMENT_BIT | VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT
                                             : 0);
  if ((!colorFormat(format) && !compressedImageFormat(format) && !depthStencilFormat(format) && format != VK_FORMAT_R32_UINT)
      || (compressedImageFormat(format) && tiling != VK_IMAGE_TILING_OPTIMAL)
      || (type != VK_IMAGE_TYPE_2D && type != VK_IMAGE_TYPE_1D && type != VK_IMAGE_TYPE_3D)
      || (flags
          & ~(VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT | VK_IMAGE_CREATE_MUTABLE_FORMAT_BIT | VK_IMAGE_CREATE_EXTENDED_USAGE_BIT
              | VK_IMAGE_CREATE_2D_ARRAY_COMPATIBLE_BIT | VK_IMAGE_CREATE_ALIAS_BIT))
      || (type != VK_IMAGE_TYPE_3D && (flags & VK_IMAGE_CREATE_2D_ARRAY_COMPATIBLE_BIT))
      || (type != VK_IMAGE_TYPE_2D && (flags & VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT)) || (usage & ~supported)
      || (tiling != VK_IMAGE_TILING_OPTIMAL && tiling != VK_IMAGE_TILING_LINEAR))
    return VK_ERROR_FORMAT_NOT_SUPPORTED;
  if (properties) {
    properties->maxExtent =
        type == VK_IMAGE_TYPE_3D ? VkExtent3D{2048, 2048, 2048} : VkExtent3D{16384, type == VK_IMAGE_TYPE_1D ? 1u : 16384u, 1};
    properties->maxMipLevels = type == VK_IMAGE_TYPE_3D ? 12 : 15;
    properties->maxArrayLayers = type == VK_IMAGE_TYPE_3D ? 1 : 2048;
    properties->sampleCounts = VK_SAMPLE_COUNT_1_BIT;
    if (type == VK_IMAGE_TYPE_2D && (colorAttachmentFormat(format) || depthStencilFormat(format)) && tiling == VK_IMAGE_TILING_OPTIMAL
        && !(usage & VK_IMAGE_USAGE_STORAGE_BIT) && !(flags & VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT))
      properties->sampleCounts |= VK_SAMPLE_COUNT_4_BIT;
    properties->maxResourceSize = maxImageResourceSize;
  }
  return VK_SUCCESS;
}

VkResult impl_vkGetPhysicalDeviceImageFormatProperties2(VkPhysicalDevice device, const VkPhysicalDeviceImageFormatInfo2 *info,
                                                        VkImageFormatProperties2 *properties) {
  if (!info || !properties) return VK_ERROR_INITIALIZATION_FAILED;
  return vkGetPhysicalDeviceImageFormatProperties(device, info->format, info->type, info->tiling, info->usage, info->flags,
                                                  &properties->imageFormatProperties);
}

VkResult impl_vkCreateImage(VkDevice device, const VkImageCreateInfo *info, const VkAllocationCallbacks *, VkImage *image) {
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
          || info->mipLevels
                 > static_cast<uint32_t>(std::bit_width(std::max({info->extent.width, info->extent.height, info->extent.depth}))))
      || (info->samples != VK_SAMPLE_COUNT_1_BIT && info->mipLevels != 1)
      || (!info->arrayLayers || info->arrayLayers > limits.maxArrayLayers) || !std::has_single_bit(static_cast<uint32_t>(info->samples))
      || !(info->samples & limits.sampleCounts))
    return VK_ERROR_FORMAT_NOT_SUPPORTED;
  (void)device;
  gemstone::vulkan::Image candidate{
      info->extent,    info->format,    VK_NULL_HANDLE, 0, info->arrayLayers, static_cast<uint32_t>(info->samples),
      info->mipLevels, info->imageType, info->flags,    {}};
  for (auto next = static_cast<const VkBaseInStructure *>(info->pNext); next; next = next->pNext)
    if (next->sType == VK_STRUCTURE_TYPE_IMAGE_FORMAT_LIST_CREATE_INFO) {
      const auto *list = reinterpret_cast<const VkImageFormatListCreateInfo *>(next);
      if (list->viewFormatCount) candidate.viewFormats.assign(list->pViewFormats, list->pViewFormats + list->viewFormatCount);
    }
  if (imageSize(candidate) > maxImageResourceSize) return VK_ERROR_OUT_OF_DEVICE_MEMORY;
  *image = makeHandle<VkImage>();
  std::lock_guard lock(stateMutex);
  images[*image] = std::move(candidate);
  return VK_SUCCESS;
}

void impl_vkDestroyImage(VkDevice, VkImage image, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    images.erase(image);
  }
  deleteHandle(image);
}

void impl_vkGetImageMemoryRequirements(VkDevice, VkImage image, VkMemoryRequirements *requirements) {
  if (!requirements) return;
  std::lock_guard lock(stateMutex);
  *requirements = {};
  const auto found = images.find(image);
  if (found == images.end()) return;
  requirements->size = imageSize(found->second);
  requirements->alignment = 64;
  requirements->memoryTypeBits = 1;
}

void impl_vkGetImageMemoryRequirements2(VkDevice device, const VkImageMemoryRequirementsInfo2 *info, VkMemoryRequirements2 *requirements) {
  if (info && requirements) {
    vkGetImageMemoryRequirements(device, info->image, &requirements->memoryRequirements);
    for (auto next = static_cast<VkBaseOutStructure *>(requirements->pNext); next; next = next->pNext)
      if (next->sType == VK_STRUCTURE_TYPE_MEMORY_DEDICATED_REQUIREMENTS) {
        auto *dedicated = reinterpret_cast<VkMemoryDedicatedRequirements *>(next);
        dedicated->prefersDedicatedAllocation = VK_FALSE;
        dedicated->requiresDedicatedAllocation = VK_FALSE;
      }
  }
}

void impl_vkGetImageSubresourceLayout(VkDevice, VkImage image, const VkImageSubresource *subresource, VkSubresourceLayout *layout) {
  if (!layout) return;
  *layout = {};
  std::lock_guard lock(stateMutex);
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
  layout->offset = mip.offset.value + layout->arrayPitch * subresource->arrayLayer;
}

VkResult impl_vkBindImageMemory(VkDevice, VkImage image, VkDeviceMemory memory, VkDeviceSize offset) {
  std::lock_guard lock(stateMutex);
  const auto found = images.find(image);
  if (found == images.end()) return VK_ERROR_INITIALIZATION_FAILED;
  auto candidate = found->second;
  candidate.memory = memory;
  candidate.offset = offset;
  if (!imageData(candidate)) return VK_ERROR_OUT_OF_DEVICE_MEMORY;
  found->second = candidate;
  return VK_SUCCESS;
}

VkResult impl_vkBindImageMemory2(VkDevice device, uint32_t count, const VkBindImageMemoryInfo *infos) {
  for (uint32_t i = 0; i < count; ++i) {
    const auto status = vkBindImageMemory(device, infos[i].image, infos[i].memory, infos[i].memoryOffset);
    if (status != VK_SUCCESS) return status;
  }
  return VK_SUCCESS;
}

VkResult impl_vkCreateImageView(VkDevice, const VkImageViewCreateInfo *info, const VkAllocationCallbacks *, VkImageView *view) {
  if (!info || !view
      || (info->viewType != VK_IMAGE_VIEW_TYPE_2D && info->viewType != VK_IMAGE_VIEW_TYPE_1D
          && info->viewType != VK_IMAGE_VIEW_TYPE_2D_ARRAY && info->viewType != VK_IMAGE_VIEW_TYPE_1D_ARRAY
          && info->viewType != VK_IMAGE_VIEW_TYPE_3D && info->viewType != VK_IMAGE_VIEW_TYPE_CUBE
          && info->viewType != VK_IMAGE_VIEW_TYPE_CUBE_ARRAY)
      || (!colorFormat(info->format) && !compressedImageFormat(info->format) && !depthStencilFormat(info->format)
          && info->format != VK_FORMAT_R32_UINT)
      || !validAspects(info->format, info->subresourceRange.aspectMask)
      || static_cast<uint32_t>(info->components.r) > VK_COMPONENT_SWIZZLE_A
      || static_cast<uint32_t>(info->components.g) > VK_COMPONENT_SWIZZLE_A
      || static_cast<uint32_t>(info->components.b) > VK_COMPONENT_SWIZZLE_A
      || static_cast<uint32_t>(info->components.a) > VK_COMPONENT_SWIZZLE_A)
    return VK_ERROR_FORMAT_NOT_SUPPORTED;
  std::lock_guard lock(stateMutex);
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
  const auto sourceCompressed = compressedImageFormat(source.format), viewCompressed = compressedImageFormat(info->format);
  const bool compatible = source.format == info->format
                          || ((source.flags & VK_IMAGE_CREATE_MUTABLE_FORMAT_BIT)
                              && ((sourceCompressed && viewCompressed && sourceCompressed->compatibility == viewCompressed->compatibility)
                                  || (!sourceCompressed && !viewCompressed && colorFormat(source.format) && colorFormat(info->format)
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
  const bool listed = source.viewFormats.empty()
                      || std::find(source.viewFormats.begin(), source.viewFormats.end(), info->format) != source.viewFormats.end();
  if (!levels || levels > source.mipLevels - baseLevel || (depthSlices && levels != 1) || !compatible || !listed || !layers
      || layers > availableLayers - baseLayer || (!cube && !arrayed && layers != 1))
    return VK_ERROR_FORMAT_NOT_SUPPORTED;
  *view = makeHandle<VkImageView>();
  imageViews[*view] = {info->image,
                       info->subresourceRange.baseArrayLayer,
                       baseLevel,
                       levels,
                       info->format,
                       layers,
                       info->subresourceRange.aspectMask,
                       {info->components.r, info->components.g, info->components.b, info->components.a},
                       depthSlices};
  return VK_SUCCESS;
}

void impl_vkDestroyImageView(VkDevice, VkImageView view, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    imageViews.erase(view);
  }
  deleteHandle(view);
}

namespace {
template <typename Reference> AttachmentReference attachmentReference(const Reference &ref) {
  AttachmentReference result{ref.attachment, ref.layout};
  if constexpr (requires { ref.aspectMask; }) {
    result.aspects = ref.aspectMask;
    for (auto *next = static_cast<const VkBaseInStructure *>(ref.pNext); next; next = next->pNext)
      if (next->sType == VK_STRUCTURE_TYPE_ATTACHMENT_REFERENCE_STENCIL_LAYOUT)
        result.stencilLayout = reinterpret_cast<const VkAttachmentReferenceStencilLayout *>(next)->stencilLayout;
  }
  return result;
}
template <typename Info> VkResult createRenderPass(const Info *info, VkRenderPass *pass) {
  if (!info || !pass || !info->subpassCount || !info->pSubpasses) return VK_ERROR_INITIALIZATION_FAILED;
  const VkRenderPassMultiviewCreateInfo *multiview = nullptr;
  for (auto *next = static_cast<const VkBaseInStructure *>(info->pNext); next; next = next->pNext)
    if (next->sType == VK_STRUCTURE_TYPE_RENDER_PASS_MULTIVIEW_CREATE_INFO)
      multiview = reinterpret_cast<const VkRenderPassMultiviewCreateInfo *>(next);
  if (multiview && multiview->subpassCount && (multiview->subpassCount != info->subpassCount || !multiview->pViewMasks))
    return VK_ERROR_INITIALIZATION_FAILED;
  RenderPass state;
  for (uint32_t i = 0; i < info->attachmentCount; ++i) {
    const auto &a = info->pAttachments[i];
    state.attachments.push_back({a.format, a.samples, a.loadOp, a.stencilLoadOp});
  }
  for (const auto &attachment : state.attachments)
    if ((!colorAttachmentFormat(attachment.format) && !depthStencilFormat(attachment.format))
        || (attachment.samples != VK_SAMPLE_COUNT_1_BIT && attachment.samples != VK_SAMPLE_COUNT_4_BIT))
      return VK_ERROR_FORMAT_NOT_SUPPORTED;
  for (uint32_t index = 0; index < info->subpassCount; ++index) {
    const auto &subpass = info->pSubpasses[index];
    if (subpass.colorAttachmentCount > maxColorAttachments || subpass.inputAttachmentCount > 8
        || subpass.pipelineBindPoint != VK_PIPELINE_BIND_POINT_GRAPHICS)
      return VK_ERROR_FEATURE_NOT_PRESENT;
    Subpass value;
    if constexpr (requires { subpass.viewMask; }) value.viewMask = subpass.viewMask;
    else value.viewMask = multiview && multiview->subpassCount ? multiview->pViewMasks[index] : 0;
    if (value.viewMask & ~63u) return VK_ERROR_FEATURE_NOT_PRESENT;
    for (uint32_t i = 0; i < subpass.colorAttachmentCount; ++i) {
      value.colors.push_back(attachmentReference(subpass.pColorAttachments[i]));
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
      for (auto *next = static_cast<const VkBaseInStructure *>(subpass.pNext); next; next = next->pNext)
        if (next->sType == VK_STRUCTURE_TYPE_SUBPASS_DESCRIPTION_DEPTH_STENCIL_RESOLVE) {
          const auto &resolve = *reinterpret_cast<const VkSubpassDescriptionDepthStencilResolve *>(next);
          if ((resolve.depthResolveMode & ~VK_RESOLVE_MODE_SAMPLE_ZERO_BIT)
              || (resolve.stencilResolveMode & ~VK_RESOLVE_MODE_SAMPLE_ZERO_BIT))
            return VK_ERROR_FEATURE_NOT_PRESENT;
          if (resolve.pDepthStencilResolveAttachment && (resolve.depthResolveMode || resolve.stencilResolveMode)) {
            value.depthResolve = attachmentReference(*resolve.pDepthStencilResolveAttachment);
            value.depthResolveMode = resolve.depthResolveMode;
            value.stencilResolveMode = resolve.stencilResolveMode;
            value.depthResolve.aspects =
                (resolve.depthResolveMode ? VK_IMAGE_ASPECT_DEPTH_BIT : 0) | (resolve.stencilResolveMode ? VK_IMAGE_ASPECT_STENCIL_BIT : 0);
          }
        }
    for (const auto &input : value.inputs)
      if (input.attachment != VK_ATTACHMENT_UNUSED && input.attachment >= info->attachmentCount) return VK_ERROR_FORMAT_NOT_SUPPORTED;
    for (size_t slot = 0; slot < value.colors.size(); ++slot) {
      const auto src = value.colors[slot].attachment;
      if (src != VK_ATTACHMENT_UNUSED && (src >= info->attachmentCount || !colorFormat(state.attachments[src].format)))
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
  for (auto *next = static_cast<const VkBaseInStructure *>(info->pNext); next; next = next->pNext)
    if (next->sType == VK_STRUCTURE_TYPE_RENDER_PASS_INPUT_ATTACHMENT_ASPECT_CREATE_INFO) {
      const auto &aspects = *reinterpret_cast<const VkRenderPassInputAttachmentAspectCreateInfo *>(next);
      for (uint32_t i = 0; i < aspects.aspectReferenceCount; ++i) {
        const auto &ref = aspects.pAspectReferences[i];
        if (ref.subpass >= state.subpasses.size() || ref.inputAttachmentIndex >= state.subpasses[ref.subpass].inputs.size())
          return VK_ERROR_INITIALIZATION_FAILED;
        state.subpasses[ref.subpass].inputs[ref.inputAttachmentIndex].aspects = ref.aspectMask;
      }
    }
  // Serialized subpasses satisfy view-local and view-global dependencies.
  *pass = makeHandle<VkRenderPass>();
  std::lock_guard lock(stateMutex);
  renderPasses[*pass] = std::move(state);
  return VK_SUCCESS;
}

} // namespace

VkResult impl_vkCreateRenderPass(VkDevice, const VkRenderPassCreateInfo *info, const VkAllocationCallbacks *, VkRenderPass *pass) {
  return createRenderPass(info, pass);
}

void impl_vkDestroyRenderPass(VkDevice, VkRenderPass pass, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    renderPasses.erase(pass);
  }
  deleteHandle(pass);
}

void impl_vkGetRenderAreaGranularity(VkDevice, VkRenderPass, VkExtent2D *granularity) {
  if (granularity) *granularity = {1, 1};
}

VkResult impl_vkCreateFramebuffer(VkDevice, const VkFramebufferCreateInfo *info, const VkAllocationCallbacks *,
                                  VkFramebuffer *framebuffer) {
  if (!info || !framebuffer || !info->layers || info->layers > 2048 || !info->width || !info->height || info->width > 16384
      || info->height > 16384 || (info->flags & ~VK_FRAMEBUFFER_CREATE_IMAGELESS_BIT))
    return VK_ERROR_FEATURE_NOT_PRESENT;
  std::lock_guard lock(stateMutex);
  const bool imageless = info->flags & VK_FRAMEBUFFER_CREATE_IMAGELESS_BIT;
  if (imageless) {
    const VkFramebufferAttachmentsCreateInfo *attachments = nullptr;
    for (auto *base = static_cast<const VkBaseInStructure *>(info->pNext); base; base = base->pNext)
      if (base->sType == VK_STRUCTURE_TYPE_FRAMEBUFFER_ATTACHMENTS_CREATE_INFO)
        attachments = reinterpret_cast<const VkFramebufferAttachmentsCreateInfo *>(base);
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
  Framebuffer state{{}, info->width, info->height, info->layers, imageless};
  state.attachments.resize(info->attachmentCount);
  if (!imageless && info->attachmentCount) std::copy_n(info->pAttachments, info->attachmentCount, state.attachments.begin());
  framebuffers[*framebuffer] = std::move(state);
  return VK_SUCCESS;
}

void impl_vkDestroyFramebuffer(VkDevice, VkFramebuffer framebuffer, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    framebuffers.erase(framebuffer);
  }
  deleteHandle(framebuffer);
}

namespace {
gx::GraphicsStencil graphicsStencil(const VkStencilOpState &value) {
  return {graphicsStencilOp(value.failOp),      graphicsStencilOp(value.passOp),         graphicsStencilOp(value.depthFailOp),
          graphicsCompare(value.compareOp),     static_cast<int32_t>(value.compareMask), static_cast<int32_t>(value.writeMask),
          static_cast<int32_t>(value.reference)};
}
} // namespace

VkResult impl_vkCreateGraphicsPipelines(VkDevice, VkPipelineCache, uint32_t count, const VkGraphicsPipelineCreateInfo *infos,
                                        const VkAllocationCallbacks *, VkPipeline *output) {
  std::lock_guard lock(stateMutex);
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
      const bool hasColor =
          std::any_of(subpass.colors.begin(), subpass.colors.end(), [](const auto &ref) { return ref.attachment != VK_ATTACHMENT_UNUSED; });
      if (hasColor
          && (!info.pColorBlendState || info.pColorBlendState->logicOpEnable
              || info.pColorBlendState->attachmentCount != subpass.colors.size()))
        return unsupported(__LINE__);
      if (hasColor) {
        const auto *c = info.pColorBlendState->blendConstants;
        pipeline.blendConstants = {c[0], c[1], c[2], c[3]};
      }
      for (size_t slot = 0; slot < subpass.colors.size(); ++slot) {
        const auto attachment = subpass.colors[slot].attachment;
        pipeline.colorAttachments.push_back(attachment);
        const auto blend = hasColor ? info.pColorBlendState->pAttachments[slot] : VkPipelineColorBlendAttachmentState{};
        gx::GraphicsBlend converted{};
        converted.writeMask = static_cast<int32_t>(blend.colorWriteMask);
        if (attachment == VK_ATTACHMENT_UNUSED) {
          pipeline.blends.push_back(converted);
          continue;
        }
        if (renderPass.attachments.at(attachment).samples != pipeline.raster.samples.value) return unsupported(__LINE__);
        if (blend.blendEnable) {
          if (!blendColorFormat(renderPass.attachments.at(attachment).format) || blend.colorBlendOp > VK_BLEND_OP_MAX
              || blend.alphaBlendOp > VK_BLEND_OP_MAX)
            return unsupported(__LINE__);
          for (const auto factor :
               {blend.srcColorBlendFactor, blend.dstColorBlendFactor, blend.srcAlphaBlendFactor, blend.dstAlphaBlendFactor})
            if (factor > VK_BLEND_FACTOR_SRC_ALPHA_SATURATE) return unsupported(__LINE__);
          converted = {true,
                       graphicsBlendFactor(blend.srcColorBlendFactor),
                       graphicsBlendFactor(blend.dstColorBlendFactor),
                       graphicsBlendOp(blend.colorBlendOp),
                       graphicsBlendFactor(blend.srcAlphaBlendFactor),
                       graphicsBlendFactor(blend.dstAlphaBlendFactor),
                       graphicsBlendOp(blend.alphaBlendOp),
                       static_cast<int32_t>(blend.colorWriteMask)};
        }
        pipeline.blends.push_back(converted);
      }
      if (pipeline.depthAttachment != VK_ATTACHMENT_UNUSED
          && renderPass.attachments.at(pipeline.depthAttachment).samples != pipeline.raster.samples.value)
        return unsupported(__LINE__);
      if (info.pDepthStencilState) {
        const auto &depth = *info.pDepthStencilState;
        pipeline.depthStencil = {bool(depth.depthTestEnable),
                                 bool(depth.depthWriteEnable),
                                 depth.depthTestEnable ? graphicsCompare(depth.depthCompareOp) : gx::GraphicsCompare::Always,
                                 bool(depth.depthBoundsTestEnable),
                                 depth.minDepthBounds,
                                 depth.maxDepthBounds,
                                 bool(depth.stencilTestEnable),
                                 depth.stencilTestEnable ? graphicsStencil(depth.front) : gx::GraphicsStencil{},
                                 depth.stencilTestEnable ? graphicsStencil(depth.back) : gx::GraphicsStencil{}};
      }
    }
    for (uint32_t j = 0; j < info.stageCount; ++j) {
      const auto &stage = info.pStages[j];
      if (pipeline.raster.discard && stage.stage == VK_SHADER_STAGE_FRAGMENT_BIT) continue;
      const auto module = modules.find(stage.module);
      if (module == modules.end()) return VK_ERROR_INITIALIZATION_FAILED;
      if (stage.stage != VK_SHADER_STAGE_VERTEX_BIT && stage.stage != VK_SHADER_STAGE_FRAGMENT_BIT) return unsupported(__LINE__);
      const auto kind = stage.stage == VK_SHADER_STAGE_VERTEX_BIT ? ShaderStageKind::Vertex : ShaderStageKind::Fragment;
      auto shader = prepareShader(module->second, stage.pName, kind, stage.pSpecializationInfo);
      if (!shader) return unsupported(__LINE__);
      if (kind == ShaderStageKind::Vertex) pipeline.vertex = std::move(*shader);
      else pipeline.fragment = std::move(*shader);
    }
    if (pipeline.vertex.image.empty()) return VK_ERROR_INITIALIZATION_FAILED;
    if (info.pVertexInputState) {
      const auto &v = *info.pVertexInputState;
      for (uint32_t i = 0; i < v.vertexAttributeDescriptionCount; ++i) {
        const auto &attribute = v.pVertexAttributeDescriptions[i];
        const auto format = vertexFormat(attribute.format);
        if (!format) return unsupported(__LINE__);
        gx::GraphicsVertexFormat converted{format->encoding, {}, format->bytes, format->bgra};
        for (unsigned lane = 0; lane < format->components; ++lane)
          converted.widths.push_back({format->widths[lane]});
        pipeline.attributes.push_back({static_cast<int32_t>(attribute.location), static_cast<int32_t>(attribute.binding),
                                       static_cast<int32_t>(attribute.offset), std::move(converted)});
      }
      for (uint32_t i = 0; i < v.vertexBindingDescriptionCount; ++i) {
        const auto &binding = v.pVertexBindingDescriptions[i];
        const bool used = std::any_of(pipeline.attributes.begin(), pipeline.attributes.end(), [&](const auto &attribute) {
          return static_cast<uint32_t>(attribute.binding.value) == binding.binding
                 && std::any_of(pipeline.vertex.interfaces.begin(), pipeline.vertex.interfaces.end(), [&](const auto &record) {
                      return record.direction == gx::ShaderDirection::Input
                             && std::any_of(record.fields.begin(), record.fields.end(), [&](const auto &field) {
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

void impl_vkCmdBindVertexBuffers(VkCommandBuffer command, uint32_t first, uint32_t count, const VkBuffer *values,
                                 const VkDeviceSize *offsets) {
  std::lock_guard lock(stateMutex);
  auto &state = commands[command].graphics;
  for (uint32_t i = 0; i < count && first + i < state.vertexBuffers.size(); ++i) {
    state.vertexBuffers[first + i] = values[i];
    state.vertexOffsets[first + i] = offsets[i];
  }
}

void impl_vkCmdSetViewport(VkCommandBuffer command, uint32_t first, uint32_t count, const VkViewport *viewports) {
  if (first || !count) return;
  std::lock_guard lock(stateMutex);
  commands[command].graphics.viewport = viewports[0];
  commands[command].graphics.viewportSet = true;
}

void impl_vkCmdSetScissor(VkCommandBuffer command, uint32_t first, uint32_t count, const VkRect2D *scissors) {
  if (first || !count) return;
  std::lock_guard lock(stateMutex);
  commands[command].graphics.scissor = scissors[0];
  commands[command].graphics.scissorSet = true;
}

void impl_vkCmdBeginRenderPass(VkCommandBuffer command, const VkRenderPassBeginInfo *info, VkSubpassContents) {
  if (!info) return;
  std::lock_guard lock(stateMutex);
  auto &state = commands[command];
  std::vector<VkClearValue> clear;
  if (info->clearValueCount) clear.assign(info->pClearValues, info->pClearValues + info->clearValueCount);
  const auto framebuffer = info->framebuffer;
  const auto pass = info->renderPass;
  const auto area = info->renderArea;
  auto attachments = framebuffers.at(framebuffer).attachments;
  if (framebuffers.at(framebuffer).imageless) {
    // Copy client arrays while recording; their lifetime ends after this call.
    for (auto *base = static_cast<const VkBaseInStructure *>(info->pNext); base; base = base->pNext)
      if (base->sType == VK_STRUCTURE_TYPE_RENDER_PASS_ATTACHMENT_BEGIN_INFO) {
        const auto &bound = *reinterpret_cast<const VkRenderPassAttachmentBeginInfo *>(base);
        if (bound.attachmentCount == attachments.size() && bound.pAttachments)
          std::copy_n(bound.pAttachments, bound.attachmentCount, attachments.begin());
      }
  }
  state.operations.emplace_back(recorded::BeginRenderPass{pass, framebuffer, area, std::move(clear), std::move(attachments)});
}

void impl_vkCmdNextSubpass(VkCommandBuffer command, VkSubpassContents) {
  std::lock_guard lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::NextSubpass{});
}

void impl_vkCmdEndRenderPass(VkCommandBuffer command) {
  std::lock_guard lock(stateMutex);
  auto &state = commands[command];
  state.operations.emplace_back(recorded::EndRenderPass{});
}

void impl_vkCmdDraw(VkCommandBuffer command, uint32_t vertices, uint32_t instances, uint32_t firstVertex, uint32_t firstInstance) {
  std::lock_guard lock(stateMutex);
  const auto &state = commands[command].graphics;
  commands[command].operations.emplace_back(recorded::Draw{state, vertices, instances, firstVertex, firstInstance, false, 0});
}

void impl_vkCmdBindIndexBuffer(VkCommandBuffer command, VkBuffer buffer, VkDeviceSize offset, VkIndexType type) {
  std::lock_guard lock(stateMutex);
  auto &state = commands.at(command).graphics;
  state.indexBuffer = buffer;
  state.indexOffset = offset;
  state.indexType = type;
}

void impl_vkCmdDrawIndexed(VkCommandBuffer command, uint32_t indices, uint32_t instances, uint32_t firstIndex, int32_t vertexOffset,
                           uint32_t firstInstance) {
  std::lock_guard lock(stateMutex);
  const auto &state = commands.at(command).graphics;
  commands.at(command).operations.emplace_back(recorded::Draw{state, indices, instances, firstIndex, firstInstance, true, vertexOffset});
}

void impl_vkCmdDrawIndirect(VkCommandBuffer command, VkBuffer buffer, VkDeviceSize offset, uint32_t count, uint32_t stride) {
  recordIndirectDraw(command, buffer, offset, count, stride, false);
}

void impl_vkCmdDrawIndexedIndirect(VkCommandBuffer command, VkBuffer buffer, VkDeviceSize offset, uint32_t count, uint32_t stride) {
  recordIndirectDraw(command, buffer, offset, count, stride, true);
}

void impl_vkCmdCopyImageToBuffer(VkCommandBuffer command, VkImage source, VkImageLayout, VkBuffer destination, uint32_t count,
                                 const VkBufferImageCopy *regions) {
  recordBufferImageCopy(command, destination, source, count, regions, false);
}

void impl_vkCmdCopyBufferToImage(VkCommandBuffer command, VkBuffer source, VkImage destination, VkImageLayout, uint32_t count,
                                 const VkBufferImageCopy *regions) {
  recordBufferImageCopy(command, source, destination, count, regions, true);
}

void impl_vkCmdClearAttachments(VkCommandBuffer command, uint32_t attachmentCount, const VkClearAttachment *attachments, uint32_t rectCount,
                                const VkClearRect *rectangles) {
  std::vector<VkClearAttachment> clears(attachments, attachments + attachmentCount);
  std::vector<VkClearRect> rects(rectangles, rectangles + rectCount);
  std::lock_guard lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::ClearAttachments{std::move(clears), std::move(rects)});
}

void impl_vkCmdClearColorImage(VkCommandBuffer command, VkImage handle, VkImageLayout, const VkClearColorValue *color, uint32_t count,
                               const VkImageSubresourceRange *ranges) {
  const auto value = *color;
  std::vector<VkImageSubresourceRange> regions(ranges, ranges + count);
  std::lock_guard lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::ClearColor{handle, value, std::move(regions)});
}

void impl_vkCmdClearDepthStencilImage(VkCommandBuffer command, VkImage handle, VkImageLayout, const VkClearDepthStencilValue *value,
                                      uint32_t count, const VkImageSubresourceRange *ranges) {
  const VkClearDepthStencilValue clear = *value;
  std::vector<VkImageSubresourceRange> regions(ranges, ranges + count);
  std::lock_guard lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::ClearDepthStencil{handle, clear, std::move(regions)});
}

void impl_vkCmdCopyImage(VkCommandBuffer command, VkImage source, VkImageLayout, VkImage destination, VkImageLayout, uint32_t count,
                         const VkImageCopy *regions) {
  std::vector<gx::GraphicsImageCopy> copies;
  for (uint32_t i = 0; i < count; ++i)
    copies.push_back(graphicsCopy(regions[i]));
  std::lock_guard lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::CopyImage{source, destination, std::move(copies)});
}

// Only width-one lines are advertised; valid line-width commands carry no state.
void impl_vkCmdSetLineWidth(VkCommandBuffer, float) {}

void impl_vkCmdSetDepthBias(VkCommandBuffer command, float constant, float clamp, float slope) {
  std::lock_guard lock(stateMutex);
  commands.at(command).graphics.depthBias = {constant, clamp, slope};
}

void impl_vkCmdSetBlendConstants(VkCommandBuffer command, const float values[4]) {
  std::lock_guard lock(stateMutex);
  std::copy_n(values, 4, commands.at(command).graphics.blendConstants.begin());
}

void impl_vkCmdSetDepthBounds(VkCommandBuffer command, float minimum, float maximum) {
  std::lock_guard lock(stateMutex);
  commands.at(command).graphics.depthBounds = {minimum, maximum};
}

void impl_vkCmdSetStencilCompareMask(VkCommandBuffer command, VkStencilFaceFlags faces, uint32_t value) {
  std::lock_guard lock(stateMutex);
  auto &state = commands.at(command).graphics.stencilCompareMask;
  if (faces & VK_STENCIL_FACE_FRONT_BIT) state[0] = value;
  if (faces & VK_STENCIL_FACE_BACK_BIT) state[1] = value;
}

void impl_vkCmdSetStencilWriteMask(VkCommandBuffer command, VkStencilFaceFlags faces, uint32_t value) {
  std::lock_guard lock(stateMutex);
  auto &state = commands.at(command).graphics.stencilWriteMask;
  if (faces & VK_STENCIL_FACE_FRONT_BIT) state[0] = value;
  if (faces & VK_STENCIL_FACE_BACK_BIT) state[1] = value;
}

void impl_vkCmdSetStencilReference(VkCommandBuffer command, VkStencilFaceFlags faces, uint32_t value) {
  std::lock_guard lock(stateMutex);
  auto &state = commands.at(command).graphics.stencilReference;
  if (faces & VK_STENCIL_FACE_FRONT_BIT) state[0] = value;
  if (faces & VK_STENCIL_FACE_BACK_BIT) state[1] = value;
}

VkResult impl_vkCreateRenderPass2(VkDevice, const VkRenderPassCreateInfo2 *info, const VkAllocationCallbacks *, VkRenderPass *pass) {
  return createRenderPass(info, pass);
}

void impl_vkCmdBeginRenderPass2(VkCommandBuffer command, const VkRenderPassBeginInfo *info, const VkSubpassBeginInfo *begin) {
  vkCmdBeginRenderPass(command, info, begin ? begin->contents : VK_SUBPASS_CONTENTS_INLINE);
}

void impl_vkCmdNextSubpass2(VkCommandBuffer command, const VkSubpassBeginInfo *begin, const VkSubpassEndInfo *) {
  vkCmdNextSubpass(command, begin ? begin->contents : VK_SUBPASS_CONTENTS_INLINE);
}

void impl_vkCmdEndRenderPass2(VkCommandBuffer command, const VkSubpassEndInfo *) { vkCmdEndRenderPass(command); }

void impl_vkCmdResolveImage(VkCommandBuffer command, VkImage source, VkImageLayout, VkImage destination, VkImageLayout, uint32_t count,
                            const VkImageResolve *regions) {
  std::vector<gx::GraphicsImageCopy> copies;
  for (uint32_t i = 0; i < count; ++i)
    copies.push_back(graphicsCopy(regions[i]));
  std::lock_guard lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::Resolve{source, destination, std::move(copies)});
}

void impl_vkCmdBlitImage(VkCommandBuffer command, VkImage source, VkImageLayout, VkImage destination, VkImageLayout, uint32_t count,
                         const VkImageBlit *regions, VkFilter filter) {
  std::vector<gx::GraphicsBlit> copies;
  for (uint32_t i = 0; i < count; ++i) {
    const auto &copy = regions[i];
    copies.push_back({graphicsLayers(copy.srcSubresource), graphicsOffset(copy.srcOffsets[0]), graphicsOffset(copy.srcOffsets[1]),
                      graphicsLayers(copy.dstSubresource), graphicsOffset(copy.dstOffsets[0]), graphicsOffset(copy.dstOffsets[1])});
  }
  std::lock_guard lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::Blit{source, destination, std::move(copies), filter});
}
} // namespace gemstone::vulkan::adapter

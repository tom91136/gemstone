#include "resources.hpp"

#include <algorithm>
#include <cmath>
#include <cstdio>
#include <limits>

#include "gemstone_image_formats.hpp"
#include "gemstone_vulkan_graphics.hpp"
#include <gemstone/trace.hpp>

#include "submission.hpp"

namespace gemstone::vulkan::adapter {
namespace gx = gemstone::execution;
bool depthStencilFormat(VkFormat format) { return depthFormat(format) || stencilFormat(format); }
VkImageAspectFlags imageAspect(VkFormat format) {
  return depthStencilFormat(format)
             ? (depthFormat(format) ? VK_IMAGE_ASPECT_DEPTH_BIT : 0) | (stencilFormat(format) ? VK_IMAGE_ASPECT_STENCIL_BIT : 0)
             : VK_IMAGE_ASPECT_COLOR_BIT;
}
bool validAspects(VkFormat format, VkImageAspectFlags aspects) { return aspects && !(aspects & ~imageAspect(format)); }

VkExtent3D mipExtent(const gemstone::vulkan::Image &image, uint32_t level) {
  return {std::max(1u, image.extent.width >> level), std::max(1u, image.extent.height >> level), std::max(1u, image.extent.depth >> level)};
}
ImageLayout imageLayoutOf(const gemstone::vulkan::Image &image) {
  ImageLayout result;
  for (uint32_t level = 0; level < image.mipLevels; ++level) {
    const auto extent = mipExtent(image, level);
    auto storage = extent;
    if (gemstone::vulkan::compressedImageFormat(image.format)) {
      storage.width = gemstone::vulkan::CompressedImageFormat::blocks(storage.width);
      storage.height = gemstone::vulkan::CompressedImageFormat::blocks(storage.height);
    }
    const uint64_t row = static_cast<uint64_t>(storage.width) * texelSize(image.format) * image.samples;
    const uint64_t slice = row * storage.height;
    const uint64_t layer = slice * storage.depth;
    result.levels.push_back({{static_cast<int32_t>(extent.width), static_cast<int32_t>(extent.height), static_cast<int32_t>(extent.depth)},
                             static_cast<int64_t>(result.size),
                             static_cast<int64_t>(row),
                             static_cast<int64_t>(slice),
                             static_cast<int64_t>(layer)});
    result.size += layer * image.layers;
  }
  return result;
}
uint64_t imageSize(const gemstone::vulkan::Image &image) { return imageLayoutOf(image).size; }
gemstone::vulkan::Image imageSlice(ImageSlice slice) {
  auto image = images.at(slice.image);
  const auto level = imageLayoutOf(image).levels.at(slice.level);
  image.offset += level.offset.value + (slice.depthSlices ? level.slicePitch.value : level.layerPitch.value) * slice.baseLayer;
  image.extent = mipExtent(image, slice.level);
  image.mipLevels = 1;
  if (slice.depthSlices) {
    image.extent.depth = 1;
    image.type = VK_IMAGE_TYPE_2D;
  }
  image.layers = slice.layers;
  if (slice.format != VK_FORMAT_UNDEFINED) image.format = slice.format;
  return image;
}
static uint8_t *imageData(const gemstone::vulkan::Image &image, uint64_t size) {
  const auto memory = memories.find(image.memory);
  if (memory == memories.end() || image.offset > memory->second->bytes.size() || size > memory->second->bytes.size() - image.offset)
    return nullptr;
  return memory->second->bytes.data() + image.offset;
}

uint8_t *imageData(const gemstone::vulkan::Image &image) { return imageData(image, imageSize(image)); }

struct ImageDescriptor {
  VkDescriptorType type;
  VkImageView view;
  std::optional<gx::GraphicsSampler> sampler;
};
std::optional<ImageDescriptor> imageDescriptor(VkDescriptorSet set, GemstoneVulkanDescriptorSlot slot) {
  const auto layout = descriptorSets.sets.find(set);
  if (layout == descriptorSets.sets.end()) return std::nullopt;
  const auto binding = layout->second.layout.find(slot.binding);
  if (binding == layout->second.layout.end()) return std::nullopt;
  const auto type = binding->second.descriptorType;
  if (type != VK_DESCRIPTOR_TYPE_INPUT_ATTACHMENT && type != VK_DESCRIPTOR_TYPE_STORAGE_IMAGE && type != VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE
      && type != VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER && type != VK_DESCRIPTOR_TYPE_SAMPLER)
    return std::nullopt;
  VkDescriptorImageInfo info{};
  if (const auto *value = descriptorSets.get<VkDescriptorImageInfo>(set, slot.binding, slot.element)) info = *value;
  ImageDescriptor result{type, info.imageView, {}};
  if (type == VK_DESCRIPTOR_TYPE_SAMPLER || type == VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER) {
    if (const auto immutable = layout->second.immutableSamplers.find({slot.binding, slot.element});
        immutable != layout->second.immutableSamplers.end())
      result.sampler = immutable->second;
    else if (const auto sampler = samplers.find(info.sampler); sampler != samplers.end()) result.sampler = sampler->second;
    else return std::nullopt;
  }
  return result;
}
// Called under stateMutex. Merge overlapping descriptor ranges so shader aliases share storage,
// while preserving each argument's individual offset and bounds.
std::optional<gemstone::vulkan::StageResources> descriptorResources(const std::vector<gemstone::execution::ShaderDescriptor> &bindings,
                                                                    const std::array<VkDescriptorSet, MaxBoundDescriptorSets> &sets,
                                                                    const std::map<GemstoneVulkanDescriptorSlot, uint32_t> &dynamicOffsets,
                                                                    uint32_t inputView) {
  std::vector<GemstoneVulkanDescriptorRange> ranges;
  std::vector<size_t> bufferArguments;
  std::map<size_t, VkFormat> texelFormats;
  std::map<VkImage, size_t> imageAllocations;
  gemstone::vulkan::StageResources result;
  result.arguments.resize(bindings.size());
  for (size_t index = 0; index < bindings.size(); ++index) {
    const auto &binding = bindings[index];
    const GemstoneVulkanDescriptorSlot slot{static_cast<uint32_t>(binding.set.value), static_cast<uint32_t>(binding.binding.value),
                                            static_cast<uint32_t>(binding.element.value), binding.resource};
    const auto set = sets.at(slot.set);
    const auto *descriptor = descriptorSets.get(set, slot.binding, slot.element);
    VkDescriptorBufferInfo texelRange{};
    if (const auto *texel = descriptorSets.get<VkBufferView>(set, slot.binding, slot.element)) {
      const auto found = bufferViews.find(*texel);
      if (found == bufferViews.end()) return std::nullopt;
      const auto &view = found->second;
      texelRange = {view.buffer, view.offset, view.range};
      descriptor = &texelRange;
      texelFormats[index] = view.format;
    }
    if (!descriptor) {
      const auto descriptor = imageDescriptor(set, slot);
      if (!descriptor) {
        // Older SPIR-V entry points list all declared descriptor globals in our
        // argument ABI, including statically unused, unwritten bindings. Such
        // bindings need no resource; retain their argument positions with bounded
        // placeholder storage. Valid shaders never access an unbound descriptor.
        auto bytes = std::make_unique<uint8_t[]>(sizeof(uint32_t));
        result.arguments[index] = StageResources::Argument{result.allocations.size(), 0, sizeof(uint32_t)};
        result.allocations.push_back({bytes.get(), sizeof(uint32_t), std::move(bytes)});
        continue;
      }
      if (descriptor->type == VK_DESCRIPTOR_TYPE_SAMPLER) {
        result.descriptorBindings.push_back({static_cast<int32_t>(index), gx::GraphicsDescriptorSampler{*descriptor->sampler}});
        continue;
      }
      const auto view = imageViews.find(descriptor->view);
      if (view == imageViews.end()) return std::nullopt;
      auto slice = view->second;
      if (descriptor->type == VK_DESCRIPTOR_TYPE_INPUT_ATTACHMENT) {
        if (inputView >= slice.layers) return std::nullopt;
        slice.baseLayer += inputView;
        slice.layers = 1;
      }
      const auto &image = images.at(slice.image);
      const auto viewFormat = slice.format == VK_FORMAT_UNDEFINED ? image.format : slice.format;
      const auto format = graphicsFormat(viewFormat, slice.aspects);
      if (!format
          || (image.samples != 1 && descriptor->type != VK_DESCRIPTOR_TYPE_INPUT_ATTACHMENT
              && descriptor->type != VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE && descriptor->type != VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER))
        return std::nullopt;
      // Sampler-only variables do not consume image storage or trigger decoding.
      if (descriptor->type == VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER && slot.resourceType == ShaderResourceKind::Sampler) {
        result.descriptorBindings.push_back({static_cast<int32_t>(index), gx::GraphicsDescriptorSampler{*descriptor->sampler}});
        continue;
      }
      const auto layout = imageLayoutOf(image);
      auto *data = imageData(image, layout.size);
      if (!data) return std::nullopt;
      auto allocation = imageAllocations.find(slice.image);
      if (allocation == imageAllocations.end()) {
        allocation = imageAllocations.emplace(slice.image, result.allocations.size()).first;
        result.allocations.push_back({data, layout.size, {}, image.memory, image.offset});
      }
      if (gemstone::vulkan::compressedImageFormat(viewFormat) && descriptor->type != VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE
          && descriptor->type != VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
        return std::nullopt;
      std::vector<gx::GraphicsImageLevel> levels;
      for (uint32_t level = 0; level < slice.levels; ++level) {
        auto selected = layout.levels.at(slice.level + level);
        selected.offset.value += (slice.depthSlices ? selected.slicePitch.value : selected.layerPitch.value) * slice.baseLayer;
        if (slice.depthSlices) {
          selected.extent.depth.value = 1;
          selected.layerPitch = selected.slicePitch;
        }
        levels.push_back(selected);
      }
      const gx::GraphicsImage source{{},
                                     {std::move(levels)},
                                     static_cast<int32_t>(slice.layers),
                                     static_cast<int32_t>(image.samples),
                                     *format,
                                     image.type == VK_IMAGE_TYPE_3D && !slice.depthSlices};
      const gx::GraphicsComponents components{graphicsSwizzle(slice.components[0]), graphicsSwizzle(slice.components[1]),
                                              graphicsSwizzle(slice.components[2]), graphicsSwizzle(slice.components[3])};
      gx::DescriptorBinding binding{static_cast<int32_t>(index), gx::GraphicsDescriptorImage{source, components}};
      if (descriptor->type == VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER && slot.resourceType != ShaderResourceKind::Image)
        binding.descriptor = gx::GraphicsDescriptorCombined{source, components, *descriptor->sampler};
      result.descriptorBindings.push_back(std::move(binding));
      // The source range retains its storage until execution replaces the argument with a descriptor.
      result.arguments[index] = StageResources::Argument{allocation->second, 0, layout.size};
      continue;
    }
    const auto buffer = buffers.find(descriptor->buffer);
    if (buffer == buffers.end()) return std::nullopt;
    auto offset = descriptor->offset;
    if (const auto dynamic = dynamicOffsets.find(slot); dynamic != dynamicOffsets.end()) {
      if (offset > UINT64_MAX - dynamic->second) return std::nullopt;
      offset += dynamic->second;
    }
    const auto length = gemstoneVulkanDescriptorLength(buffer->second.size, offset, descriptor->range);
    if (!length || buffer->second.offset > UINT64_MAX - offset) return std::nullopt;
    const auto memory = memories.find(buffer->second.memory);
    const auto begin = buffer->second.offset + offset;
    if (memory == memories.end() || begin > memory->second->bytes.size() || *length > memory->second->bytes.size() - begin)
      return std::nullopt;
    ranges.push_back({buffer->second.memory, begin, *length});
    bufferArguments.push_back(index);
  }
  const auto plan = gemstoneVulkanDescriptorAllocationPlan(ranges);
  const auto imageCount = result.allocations.size();
  for (const auto &allocation : plan.allocations)
    result.allocations.push_back(
        {memories.at(allocation.memory)->bytes.data() + allocation.offset, allocation.size, {}, allocation.memory, allocation.offset});
  for (size_t i = 0; i < plan.arguments.size(); ++i) {
    const auto index = bufferArguments[i];
    result.arguments[index] = StageResources::Argument{imageCount + plan.arguments[i].allocation, plan.arguments[i].offset, ranges[i].size};
    if (const auto format = texelFormats.find(index); format != texelFormats.end()) {
      const auto size = static_cast<int64_t>(ranges[i].size);
      const auto layout = graphicsFormat(format->second);
      if (!layout) return std::nullopt;
      const auto texels = static_cast<int32_t>(size / texelSize(format->second));
      result.descriptorBindings.push_back(
          {static_cast<int32_t>(index),
           gx::GraphicsDescriptorImage{gx::GraphicsImage{{}, {{{{texels, 1, 1}, 0, size, size, size}}}, 1, 1, *layout, false}, {}}});
    }
  }
  return result;
}

bool run(const Dispatch &dispatch) {
  // Vulkan permits zero workgroups in any axis; this performs no work.
  if (!dispatch.x || !dispatch.y || !dispatch.z) return true;
  gemstone::execution::PreparedShader pipeline;
  gemstone::vulkan::StageResources resources;
  {
    std::lock_guard lock(stateMutex);
    const auto pipe = pipelines.find(dispatch.pipeline);
    if (pipe == pipelines.end()) {
      std::fprintf(stderr, "Gemstone Vulkan: missing compute pipeline\n");
      return false;
    }
    pipeline = pipe->second;
    auto packed = descriptorResources(pipeline.descriptors, dispatch.descriptors, dispatch.dynamicOffsets);
    if (!packed) {
      std::fprintf(stderr, "Gemstone Vulkan: invalid dispatch resources\n");
      return false;
    }
    resources = std::move(*packed);
  }
  const auto size = pipeline.workgroup.value_or(gemstone::execution::ShaderWorkgroup{1, 1, 1});
  const std::array<uint32_t, 3> local{static_cast<uint32_t>(size.x.value), static_cast<uint32_t>(size.y.value),
                                      static_cast<uint32_t>(size.z.value)};
  for (size_t axis = 0; axis < 3; ++axis)
    if (static_cast<uint64_t>(dispatch.base[axis]) * local[axis] > INT32_MAX) return false;
  const bool trace = gemstone::traceEnabled("vulkan");
  if (trace)
    std::fprintf(stderr, "Gemstone Vulkan dispatch: %s bindings=%zu groups=%u,%u,%u local=%u,%u,%u\n", pipeline.entry.value.c_str(),
                 pipeline.descriptors.size(), dispatch.x, dispatch.y, dispatch.z, local[0], local[1], local[2]);
  gemstone::vulkan::SubmissionResources submission(rpcExecutionMutex);
  auto *client = submission.get();
  if (!client) return false;
  const auto program = submission.load(pipeline.image);
  if (!program) return false;
  auto bound = bindStageResources(
      resources, [&](const StageResources::Allocation &allocation) { return submission.memory(allocation.data, allocation.size, true); });
  if (!bound || !submission.get()) return false;
  std::vector<Argument> arguments;
  for (const auto &view : bound->arguments) {
    if (view) arguments.emplace_back(ArgumentMemory{view->allocation, view->offset, LongPositive{view->size.value}});
    else arguments.emplace_back(ArgumentScalar{}); // Filled by descriptor preparation before shader execution.
  }
  const auto failed = [&] {
    std::fprintf(stderr, "Gemstone Vulkan: dispatch failed: %s\n", client->error().c_str());
    return false;
  };
  auto pushConstants = dispatch.pushConstants;
  if (pipeline.pushConstants) {
    const auto range = submission.memory(pushConstants.data(), pushConstants.size(), true);
    if (!submission.get()) return failed();
    arguments.emplace_back(ArgumentMemory{range.allocation, range.offset.value, LongPositive{range.size.value}});
  }

  const Dimensions groupSize{static_cast<int32_t>(local[0]), static_cast<int32_t>(local[1]), static_cast<int32_t>(local[2])};
  const Dimensions groupCount{static_cast<int32_t>(dispatch.x), static_cast<int32_t>(dispatch.y), static_cast<int32_t>(dispatch.z)};
  const GlobalDimensions globalSize{static_cast<int64_t>(local[0]) * dispatch.x, static_cast<int64_t>(local[1]) * dispatch.y,
                                    static_cast<int64_t>(local[2]) * dispatch.z};
  const std::array<uint64_t, 3> offset{static_cast<uint64_t>(dispatch.base[0]) * local[0],
                                       static_cast<uint64_t>(dispatch.base[1]) * local[1],
                                       static_cast<uint64_t>(dispatch.base[2]) * local[2]};
  const Coordinates base{static_cast<int32_t>(offset[0]), static_cast<int32_t>(offset[1]), static_cast<int32_t>(offset[2])};
  auto launched = client->call(Launch{*program,
                                      pipeline.entry.value,
                                      groupSize,
                                      groupCount,
                                      globalSize,
                                      3,
                                      base,
                                      0,
                                      0,
                                      std::move(arguments),
                                      {},
                                      false,
                                      std::move(bound->descriptorBindings)});
  if (!launched) {
    std::fprintf(stderr, "Gemstone Vulkan: launch request failed: %s\n", launched.error().detail.c_str());
    return failed();
  }
  if (!submission.complete(launched.value())) return failed();
  return true;
}

bool copyBuffers(const Copy &copy) {
  std::lock_guard lock(stateMutex);
  const auto src = buffers.find(copy.source);
  const auto dst = buffers.find(copy.destination);
  if (src == buffers.end() || dst == buffers.end()) return false;
  const auto srcMem = memories.find(src->second.memory);
  const auto dstMem = memories.find(dst->second.memory);
  if (srcMem == memories.end() || dstMem == memories.end()) return false;
  for (const auto &region : copy.regions) {
    const auto srcOffset = src->second.offset + region.srcOffset;
    const auto dstOffset = dst->second.offset + region.dstOffset;
    if (region.srcOffset > src->second.size || region.size > src->second.size - region.srcOffset || region.dstOffset > dst->second.size
        || region.size > dst->second.size - region.dstOffset || srcOffset > srcMem->second->bytes.size()
        || region.size > srcMem->second->bytes.size() - srcOffset || dstOffset > dstMem->second->bytes.size()
        || region.size > dstMem->second->bytes.size() - dstOffset)
      return false;
    std::memmove(dstMem->second->bytes.data() + dstOffset, srcMem->second->bytes.data() + srcOffset, static_cast<size_t>(region.size));
  }
  return true;
}

} // namespace gemstone::vulkan::adapter

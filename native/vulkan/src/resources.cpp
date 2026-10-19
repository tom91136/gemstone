#include "resources.hpp"

#include <algorithm>
#include <array>
#include <cstdint>
#include <cstdio>
#include <map>
#include <memory>
#include <mutex>
#include <optional>
#include <utility>
#include <vector>

#include "gemstone_execution_rpc.hpp"
#include "gemstone_image_formats.hpp"
#include "gemstone_vulkan_graphics.hpp"
#include <gemstone/trace.hpp>

#include "submission.hpp"

namespace gemstone::vulkan {
bool depthStencilFormat(VkFormat format) { return depthFormat(format) || stencilFormat(format); }
VkImageAspectFlags imageAspect(VkFormat format) {
  return depthStencilFormat(format)
             ? (depthFormat(format) ? VK_IMAGE_ASPECT_DEPTH_BIT : 0) | (stencilFormat(format) ? VK_IMAGE_ASPECT_STENCIL_BIT : 0)
             : VK_IMAGE_ASPECT_COLOR_BIT;
}
bool validAspects(VkFormat format, VkImageAspectFlags aspects) { return aspects && !(aspects & ~imageAspect(format)); }

VkExtent3D mipExtent(const Image &image, uint32_t level) {
  return {.width = std::max(1U, image.extent.width >> level),
          .height = std::max(1U, image.extent.height >> level),
          .depth = std::max(1U, image.extent.depth >> level)};
}
ImageLayout imageLayoutOf(const Image &image) {
  ImageLayout result;
  for (uint32_t level = 0; level < image.mipLevels; ++level) {
    const auto extent = mipExtent(image, level);
    auto storage = extent;
    if (compressedImageFormat(image.format)) {
      storage.width = CompressedImageFormat::blocks(storage.width);
      storage.height = CompressedImageFormat::blocks(storage.height);
    }
    const uint64_t row = static_cast<uint64_t>(storage.width) * texelSize(image.format) * image.samples;
    const uint64_t slice = row * storage.height;
    const uint64_t layer = slice * storage.depth;
    result.levels.push_back({.extent = {.width = {static_cast<int32_t>(extent.width)},
                                        .height = {static_cast<int32_t>(extent.height)},
                                        .depth = {static_cast<int32_t>(extent.depth)}},
                             .offset = {static_cast<int64_t>(result.size)},
                             .rowPitch = {static_cast<int64_t>(row)},
                             .slicePitch = {static_cast<int64_t>(slice)},
                             .layerPitch = {static_cast<int64_t>(layer)}});
    result.size += layer * image.layers;
  }
  return result;
}
uint64_t imageSize(const Image &image) { return imageLayoutOf(image).size; }
Image imageSlice(ImageSlice slice) {
  auto image = images.at(slice.image);
  const auto level = imageLayoutOf(image).levels.at(slice.level);
  image.offset += level.offset.value + ((slice.depthSlices ? level.slicePitch.value : level.layerPitch.value) * slice.baseLayer);
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
static uint8_t *imageData(const Image &image, uint64_t size) {
  const auto memory = memories.find(image.memory);
  if (memory == memories.end() || image.offset > memory->second->bytes.size() || size > memory->second->bytes.size() - image.offset)
    return nullptr;
  return memory->second->bytes.data() + image.offset;
}

uint8_t *imageData(const Image &image) { return imageData(image, imageSize(image)); }

std::optional<BufferRange> bufferRange(VkBuffer handle, VkDeviceSize offset, VkDeviceSize size) {
  const auto buffer = buffers.find(handle);
  if (buffer == buffers.end()) return std::nullopt;
  const auto memory = memories.find(buffer->second.memory);
  const auto length = rangeLength(buffer->second.size, offset, size);
  if (memory == memories.end() || !length) return std::nullopt;
  const auto begin = buffer->second.offset + offset;
  const auto bytes = memory->second->bytes;
  if (begin > bytes.size() || *length > bytes.size() - begin) return std::nullopt;
  return BufferRange{.memory = buffer->second.memory, .offset = begin, .bytes = bytes.subspan(begin, *length)};
}

namespace {
struct ImageDescriptor {
  VkDescriptorType type;
  VkImageView view;
  std::optional<gx::GraphicsSampler> sampler;
};
std::optional<ImageDescriptor> imageDescriptor(VkDescriptorSet set, DescriptorSlot slot) {
  const auto layout = descriptorSets.sets.find(set);
  if (layout == descriptorSets.sets.end()) return std::nullopt;
  const auto binding = layout->second.layout.find(slot.binding);
  if (binding == layout->second.layout.end()) return std::nullopt;
  const auto type = binding->second.descriptorType;
  if (!imageDescriptorType(type)) return std::nullopt;
  VkDescriptorImageInfo info{};
  if (const auto *value = descriptorSets.get<VkDescriptorImageInfo>(set, slot.binding, slot.element)) info = *value;
  ImageDescriptor result{.type = type, .view = info.imageView, .sampler = {}};
  if (type == VK_DESCRIPTOR_TYPE_SAMPLER || type == VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER) {
    if (const auto immutable = layout->second.immutableSamplers.find({slot.binding, slot.element});
        immutable != layout->second.immutableSamplers.end())
      result.sampler = immutable->second;
    else if (const auto sampler = samplers.find(info.sampler); sampler != samplers.end()) result.sampler = sampler->second;
    else return std::nullopt;
  }
  return result;
}
} // namespace
// Called under stateMutex. Merge overlapping descriptor ranges so shader aliases share storage,
// while preserving each argument's individual offset and bounds.
std::optional<StageResources> descriptorResources(const std::vector<gx::ShaderDescriptor> &bindings,
                                                  const std::array<VkDescriptorSet, MaxBoundDescriptorSets> &sets,
                                                  const std::map<DescriptorSlot, uint32_t> &dynamicOffsets, uint32_t inputView) {
  std::vector<DescriptorRange> ranges;
  std::vector<size_t> bufferArguments;
  std::map<size_t, VkFormat> texelFormats;
  std::map<VkImage, size_t> imageAllocations;
  StageResources result;
  result.arguments.resize(bindings.size());
  for (size_t index = 0; index < bindings.size(); ++index) {
    const auto &binding = bindings[index];
    const DescriptorSlot slot{.set = static_cast<uint32_t>(binding.set.value),
                              .binding = static_cast<uint32_t>(binding.binding.value),
                              .element = static_cast<uint32_t>(binding.element.value),
                              .resourceType = binding.resource};
    auto *const set = sets.at(slot.set);
    const auto *descriptor = descriptorSets.get(set, slot.binding, slot.element);
    VkDescriptorBufferInfo texelRange{};
    if (const auto *texel = descriptorSets.get<VkBufferView>(set, slot.binding, slot.element)) {
      const auto found = bufferViews.find(*texel);
      if (found == bufferViews.end()) return std::nullopt;
      const auto &view = found->second;
      texelRange = {.buffer = view.buffer, .offset = view.offset, .range = view.range};
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
        result.arguments[index] = StageResources::Argument{.allocation = result.allocations.size(), .offset = 0, .size = sizeof(uint32_t)};
        result.allocations.push_back({.data = bytes.get(), .size = sizeof(uint32_t), .owned = std::move(bytes)});
        continue;
      }
      if (descriptor->type == VK_DESCRIPTOR_TYPE_SAMPLER) {
        result.descriptorBindings.push_back(
            {.argument = {static_cast<int32_t>(index)}, .descriptor = gx::GraphicsDescriptorSampler{*descriptor->sampler}});
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
      if (descriptor->type == VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER && slot.resourceType == gx::ShaderResourceKind::Sampler) {
        result.descriptorBindings.push_back(
            {.argument = {static_cast<int32_t>(index)}, .descriptor = gx::GraphicsDescriptorSampler{*descriptor->sampler}});
        continue;
      }
      const auto layout = imageLayoutOf(image);
      auto *data = imageData(image, layout.size);
      if (!data) return std::nullopt;
      auto allocation = imageAllocations.find(slice.image);
      if (allocation == imageAllocations.end()) {
        allocation = imageAllocations.emplace(slice.image, result.allocations.size()).first;
        result.allocations.push_back({.data = data, .size = layout.size, .owned = {}, .memory = image.memory, .offset = image.offset});
      }
      if (compressedImageFormat(viewFormat) && descriptor->type != VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE
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
      const gx::GraphicsImage source{.memory = {},
                                     .levels = {std::move(levels)},
                                     .layers = {static_cast<int32_t>(slice.layers)},
                                     .samples = {static_cast<int32_t>(image.samples)},
                                     .format = *format,
                                     .volume = image.type == VK_IMAGE_TYPE_3D && !slice.depthSlices};
      const gx::GraphicsComponents components{.r = graphicsSwizzle(slice.components[0]),
                                              .g = graphicsSwizzle(slice.components[1]),
                                              .b = graphicsSwizzle(slice.components[2]),
                                              .a = graphicsSwizzle(slice.components[3])};
      gx::DescriptorBinding binding{.argument = {static_cast<int32_t>(index)},
                                    .descriptor = gx::GraphicsDescriptorImage{.source = source, .components = components}};
      if (descriptor->type == VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER && slot.resourceType != gx::ShaderResourceKind::Image)
        binding.descriptor = gx::GraphicsDescriptorCombined{.source = source, .components = components, .sampler = *descriptor->sampler};
      result.descriptorBindings.push_back(std::move(binding));
      // The source range retains its storage until execution replaces the argument with a descriptor.
      result.arguments[index] = StageResources::Argument{.allocation = allocation->second, .offset = 0, .size = layout.size};
      continue;
    }
    auto offset = descriptor->offset;
    if (const auto dynamic = dynamicOffsets.find(slot); dynamic != dynamicOffsets.end()) {
      if (offset > UINT64_MAX - dynamic->second) return std::nullopt;
      offset += dynamic->second;
    }
    const auto range = bufferRange(descriptor->buffer, offset, descriptor->range);
    if (!range) return std::nullopt;
    ranges.push_back({.memory = range->memory, .offset = range->offset, .size = range->bytes.size()});
    bufferArguments.push_back(index);
  }
  const auto allocationLayout = descriptorAllocationLayout(ranges);
  const auto imageCount = result.allocations.size();
  for (const auto &allocation : allocationLayout.allocations)
    result.allocations.push_back({.data = memories.at(allocation.memory)->bytes.data() + allocation.offset,
                                  .size = allocation.size,
                                  .owned = {},
                                  .memory = allocation.memory,
                                  .offset = allocation.offset});
  for (size_t i = 0; i < allocationLayout.arguments.size(); ++i) {
    const auto index = bufferArguments[i];
    result.arguments[index] = StageResources::Argument{.allocation = imageCount + allocationLayout.arguments[i].allocation,
                                                       .offset = allocationLayout.arguments[i].offset,
                                                       .size = ranges[i].size};
    if (const auto format = texelFormats.find(index); format != texelFormats.end()) {
      const auto size = static_cast<int64_t>(ranges[i].size);
      const auto layout = graphicsFormat(format->second);
      if (!layout) return std::nullopt;
      const auto texels = static_cast<int32_t>(size / texelSize(format->second));
      result.descriptorBindings.push_back(
          {.argument = {static_cast<int32_t>(index)},
           .descriptor = gx::GraphicsDescriptorImage{
               .source = gx::GraphicsImage{.memory = {},
                                           .levels = {{{.extent = {.width = {texels}, .height = {1}, .depth = {1}},
                                                        .offset = {0},
                                                        .rowPitch = {size},
                                                        .slicePitch = {size},
                                                        .layerPitch = {size}}}},
                                           .layers = {1},
                                           .samples = {1},
                                           .format = *layout,
                                           .volume = false},
               .components = {}}});
    }
  }
  return result;
}

bool execute(Execution &, const recorded::Dispatch &dispatch) {
  // Vulkan permits zero workgroups in any axis; this performs no work.
  if (!dispatch.x || !dispatch.y || !dispatch.z) return true;
  gx::PreparedShader pipeline;
  StageResources resources;
  {
    std::scoped_lock const lock(stateMutex);
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
  const auto size = pipeline.workgroup.value_or(gx::Dimensions{.x = {1}, .y = {1}, .z = {1}});
  const std::array<uint32_t, 3> local{static_cast<uint32_t>(size.x.value), static_cast<uint32_t>(size.y.value),
                                      static_cast<uint32_t>(size.z.value)};
  for (size_t axis = 0; axis < 3; ++axis)
    if (static_cast<uint64_t>(dispatch.base[axis]) * local[axis] > INT32_MAX) return false;
  const bool trace = gemstone::traceEnabled("vulkan");
  if (trace)
    std::fprintf(stderr, "Gemstone Vulkan dispatch: %s bindings=%zu groups=%u,%u,%u local=%u,%u,%u\n", pipeline.entry.value.c_str(),
                 pipeline.descriptors.size(), dispatch.x, dispatch.y, dispatch.z, local[0], local[1], local[2]);
  SubmissionResources submission(rpcExecutionMutex);
  auto *client = submission.get();
  if (!client) return false;
  const auto program = submission.load(pipeline.image);
  if (!program) return false;
  auto bound = bindStageResources(
      resources, [&](const StageResources::Allocation &allocation) { return submission.memory(allocation.data, allocation.size, true); });
  if (!bound || !submission.get()) return false;
  std::vector<gx::Argument> arguments;
  for (const auto &view : bound->arguments) {
    if (view)
      arguments.emplace_back(
          gx::ArgumentMemory{.region = view->allocation, .offset = view->offset, .range = gx::LongPositive{view->size.value}});
    else arguments.emplace_back(gx::ArgumentDescriptor{});
  }
  const auto failed = [&] {
    std::fprintf(stderr, "Gemstone Vulkan: dispatch failed: %s\n", client->error().c_str());
    return false;
  };
  auto pushConstants = dispatch.pushConstants;
  if (pipeline.pushConstants) {
    const auto range = submission.memory(pushConstants.data(), pushConstants.size(), true);
    if (!submission.get()) return failed();
    arguments.emplace_back(
        gx::ArgumentMemory{.region = range.allocation, .offset = {range.offset.value}, .range = gx::LongPositive{range.size.value}});
  }

  const gx::Dimensions groupSize{
      .x = {static_cast<int32_t>(local[0])}, .y = {static_cast<int32_t>(local[1])}, .z = {static_cast<int32_t>(local[2])}};
  const gx::Dimensions groupCount{
      .x = {static_cast<int32_t>(dispatch.x)}, .y = {static_cast<int32_t>(dispatch.y)}, .z = {static_cast<int32_t>(dispatch.z)}};
  const gx::GlobalDimensions globalSize{.x = {static_cast<int64_t>(local[0]) * dispatch.x},
                                        .y = {static_cast<int64_t>(local[1]) * dispatch.y},
                                        .z = {static_cast<int64_t>(local[2]) * dispatch.z}};
  const std::array<uint64_t, 3> offset{static_cast<uint64_t>(dispatch.base[0]) * local[0],
                                       static_cast<uint64_t>(dispatch.base[1]) * local[1],
                                       static_cast<uint64_t>(dispatch.base[2]) * local[2]};
  const gx::Coordinates base{
      .x = {static_cast<int32_t>(offset[0])}, .y = {static_cast<int32_t>(offset[1])}, .z = {static_cast<int32_t>(offset[2])}};
  auto launched = client->call(gx::Launch{.program = *program,
                                          .kernelName = {pipeline.entry.value},
                                          .groupSize = groupSize,
                                          .groupCount = groupCount,
                                          .globalSize = globalSize,
                                          .workDim = {3},
                                          .globalOffset = base,
                                          .privateSegmentBytes = {0},
                                          .groupSegmentBytes = {0},
                                          .arguments = std::move(arguments),
                                          .pointerRelocations = {},
                                          .discoverArgumentRelocations = false,
                                          .descriptorBindings = std::move(bound->descriptorBindings)});
  if (!launched) {
    std::fprintf(stderr, "Gemstone Vulkan: launch request failed: %s\n", launched.error().detail.c_str());
    return failed();
  }
  if (!submission.complete(launched.value())) return failed();
  return true;
}
} // namespace gemstone::vulkan

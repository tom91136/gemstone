#include "graphics_commands.hpp"

#include <algorithm>
#include <bit>
#include <cmath>
#include <limits>

#include "gemstone_image_formats.hpp"

#include "graphics_transfer.hpp"

namespace gemstone::vulkan::adapter {

using namespace gemstone::vulkan;
// Render-pass scope belongs to execution, so secondary buffers can inherit it
// even when recorded without a framebuffer hint.
// Read shared render-pass state while holding stateMutex.
uint32_t currentViewMask(const Execution &execution) {
  return execution.renderScope.pass ? renderPasses.at(execution.renderScope.pass).subpasses.at(execution.renderScope.subpass).viewMask : 0;
}
// Attachment views already incorporate their base mip and base array layer.
Image attachmentLayer(Image image, uint32_t layer) {
  image.offset += layer * imageLayoutOf(image).levels.front().layerPitch.value;
  image.layers = 1;
  return image;
}
template <typename F> bool forEachView(uint32_t mask, F action) {
  if (!mask) return action(0);
  for (uint32_t view = 0; view < 32; ++view)
    if ((mask & (1u << view)) && !action(view)) return false;
  return true;
}
template <typename F> bool forEachDrawView(const Execution &execution, F action) {
  uint32_t mask;
  {
    std::lock_guard lock(stateMutex);
    mask = currentViewMask(execution);
  }
  // Draws acquire stateMutex themselves and release it before waiting for RPC.
  return forEachView(mask, std::move(action));
}
// Queue workers execute commands in order. Query scopes take differences of this
// per-worker counter, including draws executed through secondary command buffers.

VkImageAspectFlags attachmentAspects(const AttachmentReference &ref, VkFormat format) {
  const auto aspects = imageAspect(format);
  if (ref.aspects) return aspects & ref.aspects;
  if (ref.layout == VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL || ref.layout == VK_IMAGE_LAYOUT_DEPTH_READ_ONLY_OPTIMAL)
    return aspects & (VK_IMAGE_ASPECT_DEPTH_BIT | (ref.stencilLayout ? VK_IMAGE_ASPECT_STENCIL_BIT : 0));
  if (ref.layout == VK_IMAGE_LAYOUT_STENCIL_ATTACHMENT_OPTIMAL || ref.layout == VK_IMAGE_LAYOUT_STENCIL_READ_ONLY_OPTIMAL)
    return aspects & VK_IMAGE_ASPECT_STENCIL_BIT;
  return aspects;
}
// One recorded clear may cover many views. Retain their alias identity and
// execute the ordered clears together, with one transfer and completion.
class ClearBatch {
  GraphicsTransfer transfer;
  std::vector<gx::GraphicsCommand> commands;

public:
  void color(Image &image, const VkRect2D &area, const VkClearColorValue &value) {
    std::vector<int32_t> bits(4);
    std::memcpy(bits.data(), value.uint32, 16);
    commands.emplace_back(gx::GraphicsCommandClearColor{transfer.image(image), graphicsRect(area), std::move(bits)});
  }
  void depthStencil(Image &image, const VkRect2D &area, VkImageAspectFlags aspects, VkClearDepthStencilValue value) {
    commands.emplace_back(
        gx::GraphicsCommandClearDepthStencil{transfer.image(image), graphicsRect(area), value.depth, static_cast<int32_t>(value.stencil),
                                             bool(aspects & VK_IMAGE_ASPECT_DEPTH_BIT), bool(aspects & VK_IMAGE_ASPECT_STENCIL_BIT)});
  }
  bool submit() { return commands.empty() || transfer.submit(std::move(commands)); }
};
bool resolveImageRect(const Image &source, const Image &destination, const VkRect2D &area) {
  GraphicsTransfer transfer;
  gx::GraphicsLayers layers{0, 0, static_cast<int32_t>(source.layers), true, false, false};
  gx::GraphicsOffset offset{area.offset.x, area.offset.y, 0};
  gx::GraphicsImageCopy copy{
      layers, offset, layers, offset, {static_cast<int32_t>(area.extent.width), static_cast<int32_t>(area.extent.height), 1}};
  return transfer.submit(gx::GraphicsCommandResolve{transfer.image(source), transfer.image(destination), {copy}});
}
bool drawView(Execution &execution, uint32_t viewIndex, const GraphicsCommandState &state, uint32_t vertexCount, uint32_t instanceCount,
              uint32_t firstVertex, uint32_t firstInstance, bool indexed = false, int32_t vertexOffset = 0,
              VkBuffer indirect = VK_NULL_HANDLE, VkDeviceSize indirectOffset = 0, uint32_t indirectCount = 0,
              uint32_t indirectStride = 0) {
  if (!indirect && (!vertexCount || !instanceCount)) return true;
  std::unique_lock lock(stateMutex);
  const auto &pipeline = graphicsPipelines.at(state.pipeline);
  gx::GraphicsDraw draw{};
  draw.raster = pipeline.raster;
  draw.depthStencil = pipeline.depthStencil;
  const auto &fb = framebuffers.at(execution.renderScope.framebuffer);
  const auto &subpass = renderPasses.at(execution.renderScope.pass).subpasses.at(execution.renderScope.subpass);
  if (pipeline.depthAttachment != VK_ATTACHMENT_UNUSED) {
    const auto format = renderPasses.at(execution.renderScope.pass).attachments.at(pipeline.depthAttachment).format;
    const auto aspects = attachmentAspects(subpass.depth, format);
    if (!(aspects & VK_IMAGE_ASPECT_DEPTH_BIT))
      draw.depthStencil.depthTest = draw.depthStencil.depthWrite = draw.depthStencil.boundsTest = false;
    if (!(aspects & VK_IMAGE_ASPECT_STENCIL_BIT)) draw.depthStencil.stencilTest = false;
  }
  if (!pipeline.raster.discard && ((pipeline.dynamicViewport && !state.viewportSet) || (pipeline.dynamicScissor && !state.scissorSet)))
    return false;
  const auto vertexResources = descriptorResources(pipeline.vertex.descriptors, state.descriptors, state.dynamicOffsets, viewIndex);
  const auto fragmentResources = descriptorResources(pipeline.fragment.descriptors, state.descriptors, state.dynamicOffsets, viewIndex);
  if (!vertexResources || !fragmentResources) return false;
  GraphicsTransfer transfer;
  draw.vertex = transfer.shader(pipeline.vertex, *vertexResources, state.vertexPush);
  if (!pipeline.fragment.image.empty()) draw.fragment = transfer.shader(pipeline.fragment, *fragmentResources, state.fragmentPush);
  for (const auto &binding : pipeline.bindings) {
    if (binding.binding < state.vertexBuffers.size() && !state.vertexBuffers[binding.binding]) continue;
    if (binding.binding >= state.vertexBuffers.size()) return false;
    draw.vertexBuffers.push_back({static_cast<int32_t>(binding.binding),
                                  transfer.buffer(state.vertexBuffers[binding.binding], state.vertexOffsets[binding.binding]),
                                  static_cast<int32_t>(binding.stride), binding.inputRate == VK_VERTEX_INPUT_RATE_INSTANCE});
  }
  draw.attributes = pipeline.attributes;
  if (indexed)
    draw.index =
        gx::GraphicsIndexBuffer{transfer.buffer(state.indexBuffer, state.indexOffset), state.indexType == VK_INDEX_TYPE_UINT16 ? 2 : 4};
  if (indirect)
    draw.indirect = gx::GraphicsIndirect{transfer.buffer(indirect, indirectOffset), static_cast<int32_t>(indirectCount),
                                         static_cast<int32_t>(indirectStride)};
  draw.call = {static_cast<int32_t>(vertexCount), static_cast<int32_t>(instanceCount), static_cast<int32_t>(firstVertex),
               static_cast<int32_t>(firstInstance), vertexOffset};
  if (pipeline.dynamicDepthBias) {
    draw.raster.biasConstant = state.depthBias[0];
    draw.raster.biasClamp = state.depthBias[1];
    draw.raster.biasSlope = state.depthBias[2];
  }
  const auto viewport = pipeline.dynamicViewport ? state.viewport : pipeline.viewport;
  draw.viewport = {viewport.x, viewport.y, viewport.width, viewport.height, viewport.minDepth, viewport.maxDepth};
  draw.scissor = graphicsRect(pipeline.dynamicScissor ? state.scissor : pipeline.scissor);
  draw.renderArea = graphicsRect(execution.renderScope.area);
  draw.width = {static_cast<int32_t>(fb.width)};
  draw.height = {static_cast<int32_t>(fb.height)};
  draw.viewIndex = {static_cast<int32_t>(viewIndex)};
  if (!pipeline.raster.discard)
    for (const auto attachment : pipeline.colorAttachments) {
      if (attachment == VK_ATTACHMENT_UNUSED) draw.colors.emplace_back(std::nullopt);
      else
        draw.colors.emplace_back(
            transfer.image(attachmentLayer(imageSlice(imageViews.at(execution.renderScope.attachments.at(attachment))), viewIndex)));
    }
  if (!pipeline.raster.discard && pipeline.depthAttachment != VK_ATTACHMENT_UNUSED)
    draw.depth = transfer.image(
        attachmentLayer(imageSlice(imageViews.at(execution.renderScope.attachments.at(pipeline.depthAttachment))), viewIndex));
  draw.blends = pipeline.blends;
  draw.blendConstants = pipeline.dynamicBlendConstants ? gx::GraphicsColor{state.blendConstants[0], state.blendConstants[1],
                                                                           state.blendConstants[2], state.blendConstants[3]}
                                                       : pipeline.blendConstants;
  const auto stencil = [&](gx::GraphicsStencil &value, size_t face) {
    if (pipeline.dynamicStencilCompareMask) value.compareMask = static_cast<int32_t>(state.stencilCompareMask[face]);
    if (pipeline.dynamicStencilWriteMask) value.writeMask = static_cast<int32_t>(state.stencilWriteMask[face]);
    if (pipeline.dynamicStencilReference) value.reference = static_cast<int32_t>(state.stencilReference[face]);
  };
  stencil(draw.depthStencil.front, 0);
  stencil(draw.depthStencil.back, 1);
  if (pipeline.dynamicDepthBounds) {
    draw.depthStencil.minBounds = state.depthBounds[0];
    draw.depthStencil.maxBounds = state.depthBounds[1];
  }
  lock.unlock();
  return transfer.submit(gx::GraphicsCommandDraw{std::move(draw)}, &execution.occlusionSamples);
}

bool beginSubpass(Execution &execution) {
  ClearBatch batch;
  const auto &rp = renderPasses.at(execution.renderScope.pass);
  if (execution.renderScope.subpass >= rp.subpasses.size()) return false;
  const auto &fb = framebuffers.at(execution.renderScope.framebuffer);
  const auto &subpass = rp.subpasses[execution.renderScope.subpass];
  const auto use = [&](const AttachmentReference &ref) {
    if (ref.attachment == VK_ATTACHMENT_UNUSED) return true;
    const auto index = ref.attachment;
    const auto &view = imageViews.at(execution.renderScope.attachments.at(index));
    auto image = imageSlice(view);
    const auto aspects = attachmentAspects(ref, image.format);
    const auto &attachment = rp.attachments[index];
    for (unsigned aspect = 0; aspect < 3; ++aspect) {
      const auto bit = 1u << aspect;
      if (!(aspects & bit)) continue;
      auto &loaded = execution.renderScope.loaded.at(index)[aspect];
      const auto pending = (subpass.viewMask ? subpass.viewMask : 1u) & ~loaded;
      if (!pending) continue;
      loaded |= pending;
      const auto load = aspect == 2 ? attachment.stencilLoadOp : attachment.loadOp;
      if (load != VK_ATTACHMENT_LOAD_OP_CLEAR) continue;
      if (index >= execution.renderScope.clearValues.size()) return false;
      const auto clear = [&](Image slice) {
        if (aspect == 0) batch.color(slice, execution.renderScope.area, execution.renderScope.clearValues[index].color);
        else batch.depthStencil(slice, execution.renderScope.area, bit, execution.renderScope.clearValues[index].depthStencil);
        return true;
      };
      if (subpass.viewMask) {
        if (!forEachView(pending, [&](uint32_t layer) { return layer < image.layers && clear(attachmentLayer(image, layer)); }))
          return false;
      } else {
        image.layers = fb.layers;
        if (!clear(image)) return false;
      }
    }
    return true;
  };
  for (const auto &ref : subpass.inputs)
    if (!use(ref)) return false;
  for (const auto &ref : subpass.colors)
    if (!use(ref)) return false;
  for (const auto &ref : subpass.resolves)
    if (!use(ref)) return false;
  return use(subpass.depth) && use(subpass.depthResolve) && batch.submit();
}
bool finishSubpass(Execution &execution) {
  const auto &subpass = renderPasses.at(execution.renderScope.pass).subpasses.at(execution.renderScope.subpass);
  const auto &fb = framebuffers.at(execution.renderScope.framebuffer);
  for (size_t slot = 0; slot < subpass.resolves.size(); ++slot) {
    if (subpass.resolves[slot].attachment == VK_ATTACHMENT_UNUSED) continue;
    auto source = imageSlice(imageViews.at(execution.renderScope.attachments.at(subpass.colors.at(slot).attachment)));
    auto destination = imageSlice(imageViews.at(execution.renderScope.attachments.at(subpass.resolves[slot].attachment)));
    if (subpass.viewMask) {
      if (!forEachView(subpass.viewMask, [&](uint32_t view) {
            return view < source.layers && view < destination.layers
                   && resolveImageRect(attachmentLayer(source, view), attachmentLayer(destination, view), execution.renderScope.area);
          }))
        return false;
    } else {
      source.layers = destination.layers = fb.layers;
      if (!resolveImageRect(source, destination, execution.renderScope.area)) return false;
    }
  }
  if (subpass.depthResolve.attachment != VK_ATTACHMENT_UNUSED && (subpass.depthResolveMode || subpass.stencilResolveMode)) {
    const auto source = imageSlice(imageViews.at(execution.renderScope.attachments.at(subpass.depth.attachment)));
    const auto destination = imageSlice(imageViews.at(execution.renderScope.attachments.at(subpass.depthResolve.attachment)));
    const auto resolve = [&](uint32_t layer) {
      auto src = attachmentLayer(source, layer), dst = attachmentLayer(destination, layer);
      auto *input = imageData(src), *output = imageData(dst);
      if (!input || !output || src.samples != 4 || dst.samples != 1 || src.format != dst.format) return false;
      const auto &area = execution.renderScope.area;
      if (area.offset.x < 0 || area.offset.y < 0
          || static_cast<uint64_t>(area.offset.x) + area.extent.width > std::min(src.extent.width, dst.extent.width)
          || static_cast<uint64_t>(area.offset.y) + area.extent.height > std::min(src.extent.height, dst.extent.height))
        return false;
      GraphicsTransfer transfer;
      return transfer.submit(
          gx::GraphicsCommandResolveAttachment{transfer.image(src), transfer.image(dst), graphicsRect(area),
                                               subpass.depthResolveMode ? gx::GraphicsResolve::SampleZero : gx::GraphicsResolve::None,
                                               subpass.stencilResolveMode ? gx::GraphicsResolve::SampleZero : gx::GraphicsResolve::None});
    };
    if (subpass.viewMask) {
      if (!forEachView(subpass.viewMask, resolve)) return false;
    } else
      for (uint32_t layer = 0; layer < fb.layers; ++layer)
        if (!resolve(layer)) return false;
  }
  return true;
}

void recordIndirectDraw(VkCommandBuffer command, VkBuffer indirect, VkDeviceSize offset, uint32_t drawCount, uint32_t stride,
                        bool indexed) {
  std::lock_guard lock(stateMutex);
  const auto &state = commands.at(command).graphics;
  commands.at(command).operations.emplace_back(recorded::DrawIndirect{state, indirect, offset, drawCount, stride, indexed});
}

void recordBufferImageCopy(VkCommandBuffer command, VkBuffer bufferHandle, VkImage imageHandle, uint32_t count,
                           const VkBufferImageCopy *regions, bool toImage) {
  std::vector<gx::GraphicsBufferImageCopy> copies;
  for (uint32_t i = 0; i < count; ++i) {
    const auto &copy = regions[i];
    copies.push_back({static_cast<int64_t>(copy.bufferOffset), static_cast<int32_t>(copy.bufferRowLength),
                      static_cast<int32_t>(copy.bufferImageHeight), graphicsLayers(copy.imageSubresource), graphicsOffset(copy.imageOffset),
                      graphicsExtent(copy.imageExtent)});
  }
  std::lock_guard lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::CopyBufferImage{bufferHandle, imageHandle, std::move(copies), toImage});
}

// Handle resolution happens at execution time; recorded values own their snapshots.

bool execute(Execution &, const recorded::FillBuffer &command) {
  const auto &[buffer, offset, size, value] = command;
  std::lock_guard guard(stateMutex);
  const auto bound = buffers.find(buffer);
  if (bound == buffers.end() || offset > bound->second.size) return false;
  const auto count = size == VK_WHOLE_SIZE ? bound->second.size - offset : size;
  if (count > bound->second.size - offset) return false;
  auto &bytes = memories.at(bound->second.memory)->bytes;
  for (VkDeviceSize i = 0; i + 4 <= count; i += 4)
    std::memcpy(bytes.data() + bound->second.offset + offset + i, &value, 4);
  return true;
}
bool execute(Execution &, const recorded::ResetEvent &command) {
  const auto &[event] = command;
  return vkResetEvent(VK_NULL_HANDLE, event) == VK_SUCCESS;
}
bool execute(Execution &, const recorded::DispatchIndirect &command) {
  const auto &[dispatch, buffer, offset] = command;
  auto invocation = dispatch;
  {
    std::lock_guard guard(stateMutex);
    const auto found = buffers.find(buffer);
    if (found == buffers.end() || offset > found->second.size || sizeof(VkDispatchIndirectCommand) > found->second.size - offset)
      return false;
    VkDispatchIndirectCommand dimensions;
    std::memcpy(&dimensions, memories.at(found->second.memory)->bytes.data() + found->second.offset + offset, sizeof(dimensions));
    invocation.x = dimensions.x;
    invocation.y = dimensions.y;
    invocation.z = dimensions.z;
  }
  return run(invocation);
}
bool execute(Execution &, const recorded::UpdateBuffer &command) {
  const auto &[buffer, offset, snapshot] = command;
  std::lock_guard guard(stateMutex);
  const auto found = buffers.find(buffer);
  if (found == buffers.end() || offset > found->second.size || snapshot.size() > found->second.size - offset) return false;
  auto &bytes = memories.at(found->second.memory)->bytes;
  std::memcpy(bytes.data() + found->second.offset + offset, snapshot.data(), snapshot.size());
  return true;
}
bool execute(Execution &execution, const recorded::WaitEvents &command) {
  const auto &[wait] = command;
  std::unique_lock guard(stateMutex);
  stateChanged.wait(guard, [&] {
    if (execution.device && (execution.device->stopping || execution.device->lost)) return true;
    return std::ranges::all_of(wait, [](auto event) {
      const auto found = events.find(event);
      return found == events.end() || found->second;
    });
  });
  return (!execution.device || (!execution.device->stopping && !execution.device->lost))
         && std::ranges::all_of(wait, [](auto event) { return events.contains(event); });
}
bool execute(Execution &, const recorded::SetEvent &command) {
  const auto &[event] = command;
  return vkSetEvent(VK_NULL_HANDLE, event) == VK_SUCCESS;
}
bool execute(Execution &, const recorded::Invalid &) { return false; }
bool execute(Execution &, const recorded::CopyImage &command) {
  const auto &[source, destination, copies] = command;
  std::lock_guard guard(stateMutex);
  GraphicsTransfer transfer;
  return transfer.submit(gx::GraphicsCommandCopyImage{transfer.image(images.at(source)), transfer.image(images.at(destination)), copies});
}
namespace {
template <typename Clear> bool clearImage(VkImage handle, const std::vector<VkImageSubresourceRange> &regions, Clear clear) {
  std::lock_guard guard(stateMutex);
  ClearBatch batch;
  const auto &image = images.at(handle);
  for (const auto &range : regions) {
    if (!validAspects(image.format, range.aspectMask) || range.baseMipLevel >= image.mipLevels || !range.levelCount
        || range.baseArrayLayer >= image.layers)
      return false;
    const auto levels = range.levelCount == VK_REMAINING_MIP_LEVELS ? image.mipLevels - range.baseMipLevel : range.levelCount;
    if (levels > image.mipLevels - range.baseMipLevel) return false;
    const auto layers = range.layerCount == VK_REMAINING_ARRAY_LAYERS ? image.layers - range.baseArrayLayer : range.layerCount;
    if (!layers || layers > image.layers - range.baseArrayLayer) return false;
    for (uint32_t level = 0; level < levels; ++level)
      for (uint32_t layer = 0; layer < layers; ++layer) {
        auto slice = imageSlice({handle, range.baseArrayLayer + layer, range.baseMipLevel + level});
        if (!clear(batch, slice, VkRect2D{{0, 0}, {slice.extent.width, slice.extent.height}}, range.aspectMask)) return false;
      }
  }
  return batch.submit();
}
} // namespace
bool execute(Execution &, const recorded::ClearDepthStencil &command) {
  return clearImage(command.handle, command.regions, [&](ClearBatch &batch, Image &image, VkRect2D area, VkImageAspectFlags aspects) {
    if (!depthStencilFormat(image.format)) return false;
    batch.depthStencil(image, area, aspects, command.clear);
    return true;
  });
}
bool execute(Execution &, const recorded::ClearColor &command) {
  return clearImage(command.handle, command.regions, [&](ClearBatch &batch, Image &image, VkRect2D area, VkImageAspectFlags aspects) {
    if (aspects != VK_IMAGE_ASPECT_COLOR_BIT) return false;
    batch.color(image, area, command.value);
    return true;
  });
}
bool execute(Execution &execution, const recorded::ClearAttachments &command) {
  const auto &[clears, rects] = command;
  std::lock_guard guard(stateMutex);
  ClearBatch batch;
  const auto pass = renderPasses.find(execution.renderScope.pass);
  const auto fb = framebuffers.find(execution.renderScope.framebuffer);
  if (pass == renderPasses.end() || fb == framebuffers.end()) return false;
  const auto &subpass = pass->second.subpasses.at(execution.renderScope.subpass);
  for (const auto &clear : clears) {
    const bool depth = (clear.aspectMask & (VK_IMAGE_ASPECT_DEPTH_BIT | VK_IMAGE_ASPECT_STENCIL_BIT)) != 0;
    if (!depth && (clear.aspectMask != VK_IMAGE_ASPECT_COLOR_BIT || clear.colorAttachment >= subpass.colors.size())) return false;
    const auto attachment = depth ? subpass.depth.attachment : subpass.colors[clear.colorAttachment].attachment;
    if (attachment == VK_ATTACHMENT_UNUSED) continue;
    auto image = imageSlice(imageViews.at(execution.renderScope.attachments.at(attachment)));
    for (const auto &rect : rects) {
      if (!rect.layerCount || rect.baseArrayLayer >= fb->second.layers || rect.layerCount > fb->second.layers - rect.baseArrayLayer)
        return false;
      const auto clearSlice = [&](Image slice) {
        if (depth) batch.depthStencil(slice, rect.rect, clear.aspectMask, clear.clearValue.depthStencil);
        else batch.color(slice, rect.rect, clear.clearValue.color);
        return true;
      };
      if (subpass.viewMask) {
        if (!forEachView(subpass.viewMask, [&](uint32_t view) { return view < image.layers && clearSlice(attachmentLayer(image, view)); }))
          return false;
      } else {
        auto slice = image;
        slice.offset += rect.baseArrayLayer * imageLayoutOf(image).levels.front().layerPitch.value;
        slice.layers = rect.layerCount;
        if (!clearSlice(slice)) return false;
      }
    }
  }
  return batch.submit();
}
bool execute(Execution &, const recorded::CopyBufferImage &command) {
  const auto &[bufferHandle, imageHandle, copies, toImage] = command;
  std::lock_guard guard(stateMutex);
  GraphicsTransfer transfer;
  return transfer.submit(
      gx::GraphicsCommandCopyBufferImage{transfer.buffer(bufferHandle), transfer.image(images.at(imageHandle)), copies, toImage});
}
bool execute(Execution &execution, const recorded::DrawIndirect &command) {
  const auto &[state, indirect, offset, drawCount, stride, indexed] = command;
  if (!drawCount) return true;
  return forEachDrawView(execution, [&](uint32_t view) {
    return drawView(execution, view, state, 0, 0, 0, 0, indexed, 0, indirect, offset, drawCount, stride);
  });
}
bool execute(Execution &execution, const recorded::Draw &command) {
  const auto &[state, vertices, instances, firstVertex, firstInstance, indexed, vertexOffset] = command;
  return forEachDrawView(execution, [&](uint32_t view) {
    return drawView(execution, view, state, vertices, instances, firstVertex, firstInstance, indexed, vertexOffset);
  });
}
bool execute(Execution &execution, const recorded::EndRenderPass &) {
  std::lock_guard guard(stateMutex);
  if (!finishSubpass(execution)) return false;
  execution.renderScope = {};
  return true;
}
bool execute(Execution &execution, const recorded::NextSubpass &) {
  std::lock_guard guard(stateMutex);
  if (!finishSubpass(execution)) return false;
  ++execution.renderScope.subpass;
  return beginSubpass(execution);
}
bool execute(Execution &execution, const recorded::BeginRenderPass &command) {
  const auto &[pass, framebuffer, area, clear, attachments] = command;
  std::lock_guard guard(stateMutex);
  for (const auto view : attachments)
    if (!imageViews.contains(view)) return false;
  execution.renderScope = {
      pass, framebuffer, area, 0, clear, std::vector<std::array<uint32_t, 3>>(renderPasses.at(pass).attachments.size()), attachments};
  return beginSubpass(execution);
}
bool execute(Execution &, const recorded::CopyQueryResults &command) {
  const auto &[pool, first, count, destination, offset, stride, flags] = command;
  if (!count) return true;
  uint8_t *data = nullptr;
  size_t size = 0;
  {
    std::lock_guard guard(stateMutex);
    const auto buffer = buffers.find(destination);
    if (buffer == buffers.end() || offset > buffer->second.size) return false;
    const auto memory = memories.find(buffer->second.memory);
    if (memory == memories.end() || buffer->second.offset > memory->second->bytes.size()
        || buffer->second.size > memory->second->bytes.size() - buffer->second.offset)
      return false;
    data = memory->second->bytes.data() + buffer->second.offset + offset;
    size = buffer->second.size - offset;
  }
  const auto status = vkGetQueryPoolResults(VK_NULL_HANDLE, pool, first, count, size, data, stride, flags);
  // Without WAIT, unavailable queries may leave result bytes unchanged; availability is still written.
  return status == VK_SUCCESS || status == VK_NOT_READY;
}
bool execute(Execution &execution, const recorded::WriteTimestamp &command) {
  const auto &[pool, query] = command;
  std::lock_guard guard(stateMutex);
  const auto found = queryPools.find(pool);
  if (found == queryPools.end()) return false;
  const auto &state = found->second;
  if (state->type != VK_QUERY_TYPE_TIMESTAMP || query >= state->slots.size()) return false;
  const auto ticks = std::chrono::duration_cast<std::chrono::nanoseconds>(std::chrono::steady_clock::now().time_since_epoch()).count();
  const auto count = static_cast<uint32_t>(std::max(1, std::popcount(currentViewMask(execution))));
  if (count > state->slots.size() - query) return false;
  for (uint32_t i = 0; i < count; ++i)
    state->slots[query + i] = {static_cast<uint64_t>(ticks), true};
  stateChanged.notify_all();
  return true;
}
bool execute(Execution &execution, const recorded::EndQuery &command) {
  const auto &[pool, query] = command;
  std::lock_guard guard(stateMutex);
  const auto found = queryPools.find(pool);
  if (found == queryPools.end()) return false;
  const auto &state = found->second;
  if (!execution.activeOcclusion || execution.activeOcclusion->pool != state.get() || execution.activeOcclusion->query != query)
    return false;
  // The spec permits the combined count in the first slot and zero in the rest.
  for (uint32_t i = 0; i < execution.activeOcclusion->count; ++i)
    state->slots[query + i] = {i == 0 ? execution.occlusionSamples - execution.activeOcclusion->firstSample : 0, true};
  execution.activeOcclusion.reset();
  stateChanged.notify_all();
  return true;
}
bool execute(Execution &execution, const recorded::BeginQuery &command) {
  const auto &[pool, query] = command;
  std::lock_guard guard(stateMutex);
  const auto found = queryPools.find(pool);
  if (found == queryPools.end()) return false;
  const auto &state = found->second;
  if (state->type != VK_QUERY_TYPE_OCCLUSION || query >= state->slots.size() || execution.activeOcclusion) return false;
  const auto count = static_cast<uint32_t>(std::max(1, std::popcount(currentViewMask(execution))));
  if (count > state->slots.size() - query) return false;
  for (uint32_t i = 0; i < count; ++i)
    state->slots[query + i] = {};
  execution.activeOcclusion = ActiveOcclusion{state.get(), query, static_cast<uint32_t>(count), execution.occlusionSamples};
  return true;
}
bool execute(Execution &, const recorded::ResetQuery &command) { return resetQueryPool(command.pool, command.first, command.count); }
bool execute(Execution &, const recorded::Blit &command) {
  const auto &[source, destination, copies, filter] = command;
  std::lock_guard guard(stateMutex);
  GraphicsTransfer transfer;
  return transfer.submit(gx::GraphicsCommandBlit{transfer.image(images.at(source)), transfer.image(images.at(destination)), copies,
                                                 filter == VK_FILTER_LINEAR});
}
bool execute(Execution &, const recorded::Resolve &command) {
  const auto &[source, destination, copies] = command;
  std::lock_guard guard(stateMutex);
  GraphicsTransfer transfer;
  return transfer.submit(gx::GraphicsCommandResolve{transfer.image(images.at(source)), transfer.image(images.at(destination)), copies});
}

bool execute(Execution &, const recorded::Dispatch &command) { return run(command); }
bool execute(Execution &, const recorded::Copy &command) { return copyBuffers(command); }
bool execute(Execution &execution, const Operation &operation) {
  return std::visit([&](const auto &command) { return execute(execution, command); }, operation);
}

} // namespace gemstone::vulkan::adapter

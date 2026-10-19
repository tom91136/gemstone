#include <algorithm>
#include <array>
#include <bit>
#include <chrono>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <mutex>
#include <optional>
#include <span>
#include <utility>
#include <variant>
#include <vector>

#include <aspartame/vector.hpp>

#include "gemstone_execution_rpc.hpp"
#include "gemstone_image_formats.hpp"

#include "graphics_transfer.hpp"

using namespace aspartame;

namespace gemstone::vulkan {
namespace {
// Render-pass scope belongs to execution, so secondary buffers can inherit it
// even when recorded without a framebuffer hint. Callers hold stateMutex.
uint32_t currentViewMask(const Execution &execution) {
  return execution.renderScope.pass ? renderPasses.at(execution.renderScope.pass).subpasses.at(execution.renderScope.subpass).viewMask : 0;
}
uint32_t viewCount(const Execution &execution) { return static_cast<uint32_t>(std::max(1, std::popcount(currentViewMask(execution)))); }
Image attachmentImage(const Execution &execution, uint32_t index) {
  return imageSlice(imageViews.at(execution.renderScope.attachments.at(index)));
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
    if ((mask & (1U << view)) && !action(view)) return false;
  return true;
}
template <typename F> bool forEachDrawView(const Execution &execution, F action) {
  uint32_t mask = 0;
  {
    std::scoped_lock const lock(stateMutex);
    mask = currentViewMask(execution);
  }
  // Draws acquire stateMutex themselves and release it before waiting for RPC.
  return forEachView(mask, std::move(action));
}
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
  void colour(Image &image, const VkRect2D &area, const VkClearColorValue &value) {
    std::vector<int32_t> bits(4);
    std::memcpy(bits.data(), value.uint32, 16);
    commands.emplace_back(
        gx::GraphicsCommandClearColour{.image = transfer.image(image), .area = graphicsRect(area), .bits = std::move(bits)});
  }
  void depthStencil(Image &image, const VkRect2D &area, VkImageAspectFlags aspects, VkClearDepthStencilValue value) {
    commands.emplace_back(gx::GraphicsCommandClearDepthStencil{.image = transfer.image(image),
                                                               .area = graphicsRect(area),
                                                               .depth = value.depth,
                                                               .stencil = static_cast<int32_t>(value.stencil),
                                                               .clearDepth = static_cast<bool>(aspects & VK_IMAGE_ASPECT_DEPTH_BIT),
                                                               .clearStencil = static_cast<bool>(aspects & VK_IMAGE_ASPECT_STENCIL_BIT)});
  }
  bool submit() { return commands.empty() || transfer.submit(std::move(commands)); }
};
bool resolveImageRect(const Image &source, const Image &destination, const VkRect2D &area) {
  GraphicsTransfer transfer;
  gx::GraphicsLayers const layers{
      .level = {0}, .baseLayer = {0}, .layers = {static_cast<int32_t>(source.layers)}, .colour = true, .depth = false, .stencil = false};
  gx::GraphicsOffset const offset{.x = area.offset.x, .y = area.offset.y, .z = 0};
  gx::GraphicsImageCopy copy{
      .source = layers,
      .sourceOffset = offset,
      .destination = layers,
      .destinationOffset = offset,
      .extent = {.width = {static_cast<int32_t>(area.extent.width)}, .height = {static_cast<int32_t>(area.extent.height)}, .depth = {1}}};
  return transfer.submit(
      gx::GraphicsCommandResolve{.source = transfer.image(source), .destination = transfer.image(destination), .regions = {copy}});
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
    draw.vertexBuffers.push_back({.binding = {static_cast<int32_t>(binding.binding)},
                                  .memory = transfer.buffer(state.vertexBuffers[binding.binding], state.vertexOffsets[binding.binding]),
                                  .stride = {static_cast<int32_t>(binding.stride)},
                                  .instanceRate = binding.inputRate == VK_VERTEX_INPUT_RATE_INSTANCE});
  }
  draw.attributes = pipeline.attributes;
  if (indexed)
    draw.index = gx::GraphicsIndexBuffer{.memory = transfer.buffer(state.indexBuffer, state.indexOffset),
                                         .width = {state.indexType == VK_INDEX_TYPE_UINT16 ? 2 : 4}};
  if (indirect)
    draw.indirect = gx::GraphicsIndirect{.memory = transfer.buffer(indirect, indirectOffset),
                                         .count = {static_cast<int32_t>(indirectCount)},
                                         .stride = {static_cast<int32_t>(indirectStride)}};
  draw.call = {.vertices = {static_cast<int32_t>(vertexCount)},
               .instances = {static_cast<int32_t>(instanceCount)},
               .firstVertex = static_cast<int32_t>(firstVertex),
               .firstInstance = static_cast<int32_t>(firstInstance),
               .vertexOffset = vertexOffset};
  if (pipeline.dynamicDepthBias) {
    draw.raster.biasConstant = state.depthBias[0];
    draw.raster.biasClamp = state.depthBias[1];
    draw.raster.biasSlope = state.depthBias[2];
  }
  const auto viewport = pipeline.dynamicViewport ? state.viewport : pipeline.viewport;
  draw.viewport = {.x = viewport.x,
                   .y = viewport.y,
                   .width = viewport.width,
                   .height = viewport.height,
                   .minDepth = viewport.minDepth,
                   .maxDepth = viewport.maxDepth};
  draw.scissor = graphicsRect(pipeline.dynamicScissor ? state.scissor : pipeline.scissor);
  draw.renderArea = graphicsRect(execution.renderScope.area);
  draw.width = {static_cast<int32_t>(fb.width)};
  draw.height = {static_cast<int32_t>(fb.height)};
  draw.viewIndex = {static_cast<int32_t>(viewIndex)};
  if (!pipeline.raster.discard)
    for (const auto attachment : pipeline.colourAttachments) {
      if (attachment == VK_ATTACHMENT_UNUSED) draw.colours.emplace_back(std::nullopt);
      else draw.colours.emplace_back(transfer.image(attachmentLayer(attachmentImage(execution, attachment), viewIndex)));
    }
  if (!pipeline.raster.discard && pipeline.depthAttachment != VK_ATTACHMENT_UNUSED)
    draw.depth = transfer.image(attachmentLayer(attachmentImage(execution, pipeline.depthAttachment), viewIndex));
  draw.blends = pipeline.blends;
  draw.blendConstants = pipeline.dynamicBlendConstants ? gx::GraphicsColour{.r = state.blendConstants[0],
                                                                            .g = state.blendConstants[1],
                                                                            .b = state.blendConstants[2],
                                                                            .a = state.blendConstants[3]}
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
    auto image = attachmentImage(execution, index);
    const auto aspects = attachmentAspects(ref, image.format);
    const auto &attachment = rp.attachments[index];
    for (unsigned aspect = 0; aspect < 3; ++aspect) {
      const auto bit = 1U << aspect;
      if (!(aspects & bit)) continue;
      auto &loaded = execution.renderScope.loaded.at(index)[aspect];
      const auto pending = (subpass.viewMask ? subpass.viewMask : 1U) & ~loaded;
      if (!pending) continue;
      loaded |= pending;
      const auto load = aspect == 2 ? attachment.stencilLoadOp : attachment.loadOp;
      if (load != VK_ATTACHMENT_LOAD_OP_CLEAR) continue;
      if (index >= execution.renderScope.clearValues.size()) return false;
      const auto clear = [&](Image slice) {
        if (aspect == 0) batch.colour(slice, execution.renderScope.area, execution.renderScope.clearValues[index].color);
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
  for (const auto &ref : subpass.colours)
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
    auto source = attachmentImage(execution, subpass.colours.at(slot).attachment);
    auto destination = attachmentImage(execution, subpass.resolves[slot].attachment);
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
    const auto source = attachmentImage(execution, subpass.depth.attachment);
    const auto destination = attachmentImage(execution, subpass.depthResolve.attachment);
    const auto resolve = [&](uint32_t layer) {
      auto src = attachmentLayer(source, layer);
      auto dst = attachmentLayer(destination, layer);
      auto *input = imageData(src);
      auto *output = imageData(dst);
      if (!input || !output || src.samples != 4 || dst.samples != 1 || src.format != dst.format) return false;
      const auto &area = execution.renderScope.area;
      if (area.offset.x < 0 || area.offset.y < 0
          || static_cast<uint64_t>(area.offset.x) + area.extent.width > std::min(src.extent.width, dst.extent.width)
          || static_cast<uint64_t>(area.offset.y) + area.extent.height > std::min(src.extent.height, dst.extent.height))
        return false;
      GraphicsTransfer transfer;
      return transfer.submit(gx::GraphicsCommandResolveAttachment{
          .source = transfer.image(src),
          .destination = transfer.image(dst),
          .area = graphicsRect(area),
          .depth = subpass.depthResolveMode ? gx::GraphicsResolve::SampleZero : gx::GraphicsResolve::None,
          .stencil = subpass.stencilResolveMode ? gx::GraphicsResolve::SampleZero : gx::GraphicsResolve::None});
    };
    if (subpass.viewMask) {
      if (!forEachView(subpass.viewMask, resolve)) return false;
    } else {
      for (uint32_t layer = 0; layer < fb.layers; ++layer)
        if (!resolve(layer)) return false;
    }
  }
  return true;
}

template <typename Clear> bool clearImage(VkImage handle, const std::vector<VkImageSubresourceRange> &regions, Clear clear) {
  std::scoped_lock const guard(stateMutex);
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
        auto slice = imageSlice({.image = handle, .baseLayer = range.baseArrayLayer + layer, .level = range.baseMipLevel + level});
        if (!clear(batch, slice,
                   VkRect2D{.offset = {.x = 0, .y = 0}, .extent = {.width = slice.extent.width, .height = slice.extent.height}},
                   range.aspectMask))
          return false;
      }
  }
  return batch.submit();
}
} // namespace

// Handle resolution happens at execution time; recorded values own their snapshots.

static bool execute(Execution &, const recorded::FillBuffer &command) {
  std::scoped_lock const guard(stateMutex);
  const auto range = bufferRange(command.buffer, command.offset, command.size);
  if (!range) return false;
  for (size_t i = 0; i + 4 <= range->bytes.size(); i += 4)
    std::memcpy(range->bytes.data() + i, &command.value, 4);
  return true;
}
static bool execute(Execution &, const recorded::ResetEvent &command) { return vkResetEvent(VK_NULL_HANDLE, command.event) == VK_SUCCESS; }
static bool execute(Execution &execution, const recorded::DispatchIndirect &command) {
  auto invocation = command.dispatch;
  {
    std::scoped_lock const guard(stateMutex);
    const auto range = bufferRange(command.buffer, command.offset, sizeof(VkDispatchIndirectCommand));
    if (!range) return false;
    VkDispatchIndirectCommand dimensions;
    std::memcpy(&dimensions, range->bytes.data(), sizeof(dimensions));
    invocation.x = dimensions.x;
    invocation.y = dimensions.y;
    invocation.z = dimensions.z;
  }
  return execute(execution, invocation);
}
static bool execute(Execution &, const recorded::UpdateBuffer &command) {
  std::scoped_lock const guard(stateMutex);
  const auto range = bufferRange(command.buffer, command.offset, command.snapshot.size());
  if (!range) return false;
  std::copy(command.snapshot.begin(), command.snapshot.end(), range->bytes.begin());
  return true;
}
static bool execute(Execution &execution, const recorded::WaitEvents &command) {
  const auto &[wait] = command;
  std::unique_lock guard(stateMutex);
  const auto failed = [&] { return execution.device && execution.device->failed(); };
  commandQueue().blocking([&] {
    stateChanged.wait(guard, [&] {
      return failed() || wait ^ forall([](auto event) {
                           const auto found = events.find(event);
                           return found == events.end() || found->second;
                         });
    });
  });
  return !failed() && wait ^ forall([](auto event) { return events.contains(event); });
}
static bool execute(Execution &, const recorded::SetEvent &command) { return vkSetEvent(VK_NULL_HANDLE, command.event) == VK_SUCCESS; }
static bool execute(Execution &, const recorded::Invalid &) { return false; }
static bool execute(Execution &, const recorded::CopyImage &command) {
  std::scoped_lock const guard(stateMutex);
  GraphicsTransfer transfer;
  return transfer.submit(gx::GraphicsCommandCopyImage{.source = transfer.image(images.at(command.source)),
                                                      .destination = transfer.image(images.at(command.destination)),
                                                      .regions = command.copies});
}
static bool execute(Execution &, const recorded::ClearDepthStencil &command) {
  return clearImage(command.handle, command.regions, [&](ClearBatch &batch, Image &image, VkRect2D area, VkImageAspectFlags aspects) {
    if (!depthStencilFormat(image.format)) return false;
    batch.depthStencil(image, area, aspects, command.clear);
    return true;
  });
}
static bool execute(Execution &, const recorded::ClearColour &command) {
  return clearImage(command.handle, command.regions, [&](ClearBatch &batch, Image &image, VkRect2D area, VkImageAspectFlags aspects) {
    if (aspects != VK_IMAGE_ASPECT_COLOR_BIT) return false;
    batch.colour(image, area, command.value);
    return true;
  });
}
static bool execute(Execution &execution, const recorded::ClearAttachments &command) {
  const auto &[clears, rects] = command;
  std::scoped_lock const guard(stateMutex);
  ClearBatch batch;
  const auto pass = renderPasses.find(execution.renderScope.pass);
  const auto fb = framebuffers.find(execution.renderScope.framebuffer);
  if (pass == renderPasses.end() || fb == framebuffers.end()) return false;
  const auto &subpass = pass->second.subpasses.at(execution.renderScope.subpass);
  for (const auto &clear : clears) {
    const bool depth = (clear.aspectMask & (VK_IMAGE_ASPECT_DEPTH_BIT | VK_IMAGE_ASPECT_STENCIL_BIT)) != 0;
    if (!depth && (clear.aspectMask != VK_IMAGE_ASPECT_COLOR_BIT || clear.colorAttachment >= subpass.colours.size())) return false;
    const auto attachment = depth ? subpass.depth.attachment : subpass.colours[clear.colorAttachment].attachment;
    if (attachment == VK_ATTACHMENT_UNUSED) continue;
    auto image = attachmentImage(execution, attachment);
    for (const auto &rect : rects) {
      if (!rect.layerCount || rect.baseArrayLayer >= fb->second.layers || rect.layerCount > fb->second.layers - rect.baseArrayLayer)
        return false;
      const auto clearSlice = [&](Image slice) {
        if (depth) batch.depthStencil(slice, rect.rect, clear.aspectMask, clear.clearValue.depthStencil);
        else batch.colour(slice, rect.rect, clear.clearValue.color);
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
static bool execute(Execution &, const recorded::CopyBufferImage &command) {
  std::scoped_lock const guard(stateMutex);
  GraphicsTransfer transfer;
  return transfer.submit(gx::GraphicsCommandCopyBufferImage{.buffer = transfer.buffer(command.bufferHandle),
                                                            .image = transfer.image(images.at(command.imageHandle)),
                                                            .regions = command.copies,
                                                            .toImage = command.toImage});
}
static bool execute(Execution &execution, const recorded::DrawIndirect &command) {
  if (!command.drawCount) return true;
  return forEachDrawView(execution, [&](uint32_t view) {
    return drawView(execution, view, command.state, 0, 0, 0, 0, command.indexed, 0, command.indirect, command.offset, command.drawCount,
                    command.stride);
  });
}
static bool execute(Execution &execution, const recorded::Draw &command) {
  const auto &[state, vertices, instances, firstVertex, firstInstance, indexed, vertexOffset] = command;
  return forEachDrawView(execution, [&](uint32_t view) {
    return drawView(execution, view, state, vertices, instances, firstVertex, firstInstance, indexed, vertexOffset);
  });
}
static bool execute(Execution &execution, const recorded::EndRenderPass &) {
  std::scoped_lock const guard(stateMutex);
  if (!finishSubpass(execution)) return false;
  execution.renderScope = {};
  return true;
}
static bool execute(Execution &execution, const recorded::NextSubpass &) {
  std::scoped_lock const guard(stateMutex);
  if (!finishSubpass(execution)) return false;
  ++execution.renderScope.subpass;
  return beginSubpass(execution);
}
static bool execute(Execution &execution, const recorded::BeginRenderPass &command) {
  std::scoped_lock const guard(stateMutex);
  for (auto *const view : command.attachments)
    if (!imageViews.contains(view)) return false;
  execution.renderScope = {.pass = command.pass,
                           .framebuffer = command.framebuffer,
                           .area = command.area,
                           .subpass = 0,
                           .clearValues = command.clear,
                           .loaded = std::vector<std::array<uint32_t, 3>>(renderPasses.at(command.pass).attachments.size()),
                           .attachments = command.attachments};
  return beginSubpass(execution);
}
static bool execute(Execution &, const recorded::CopyQueryResults &command) {
  if (!command.count) return true;
  std::span<uint8_t> bytes;
  {
    std::scoped_lock const guard(stateMutex);
    const auto range = bufferRange(command.destination, command.offset, VK_WHOLE_SIZE);
    if (!range) return false;
    bytes = range->bytes;
  }
  const auto status = commandQueue().blocking([&] {
    return vkGetQueryPoolResults(VK_NULL_HANDLE, command.pool, command.first, command.count, bytes.size(), bytes.data(), command.stride,
                                 command.flags);
  });
  // Without WAIT, unavailable queries may leave result bytes unchanged; availability is still written.
  return status == VK_SUCCESS || status == VK_NOT_READY;
}
static bool execute(Execution &execution, const recorded::WriteTimestamp &command) {
  std::scoped_lock const guard(stateMutex);
  const auto found = queryPools.find(command.pool);
  if (found == queryPools.end()) return false;
  const auto &state = found->second;
  if (state->type != VK_QUERY_TYPE_TIMESTAMP || command.query >= state->slots.size()) return false;
  const auto ticks = std::chrono::duration_cast<std::chrono::nanoseconds>(std::chrono::steady_clock::now().time_since_epoch()).count();
  const auto count = viewCount(execution);
  if (count > state->slots.size() - command.query) return false;
  for (uint32_t i = 0; i < count; ++i)
    state->slots[command.query + i] = {.value = static_cast<uint64_t>(ticks), .available = true};
  stateChanged.notify_all();
  return true;
}
static bool execute(Execution &execution, const recorded::EndQuery &command) {
  std::scoped_lock const guard(stateMutex);
  const auto found = queryPools.find(command.pool);
  if (found == queryPools.end()) return false;
  const auto &state = found->second;
  if (!execution.activeOcclusion || execution.activeOcclusion->pool != state.get() || execution.activeOcclusion->query != command.query)
    return false;
  // The spec permits the combined count in the first slot and zero in the rest.
  for (uint32_t i = 0; i < execution.activeOcclusion->count; ++i)
    state->slots[command.query + i] = {.value = i == 0 ? execution.occlusionSamples - execution.activeOcclusion->firstSample : 0,
                                       .available = true};
  execution.activeOcclusion.reset();
  stateChanged.notify_all();
  return true;
}
static bool execute(Execution &execution, const recorded::BeginQuery &command) {
  std::scoped_lock const guard(stateMutex);
  const auto found = queryPools.find(command.pool);
  if (found == queryPools.end()) return false;
  const auto &state = found->second;
  if (state->type != VK_QUERY_TYPE_OCCLUSION || command.query >= state->slots.size() || execution.activeOcclusion) return false;
  const auto count = viewCount(execution);
  if (count > state->slots.size() - command.query) return false;
  for (uint32_t i = 0; i < count; ++i)
    state->slots[command.query + i] = {};
  execution.activeOcclusion =
      ActiveOcclusion{.pool = state.get(), .query = command.query, .count = count, .firstSample = execution.occlusionSamples};
  return true;
}
static bool execute(Execution &, const recorded::ResetQuery &command) { return resetQueryPool(command.pool, command.first, command.count); }
static bool execute(Execution &, const recorded::Blit &command) {
  std::scoped_lock const guard(stateMutex);
  GraphicsTransfer transfer;
  return transfer.submit(gx::GraphicsCommandBlit{.source = transfer.image(images.at(command.source)),
                                                 .destination = transfer.image(images.at(command.destination)),
                                                 .regions = command.copies,
                                                 .linear = command.filter == VK_FILTER_LINEAR});
}
static bool execute(Execution &, const recorded::Resolve &command) {
  std::scoped_lock const guard(stateMutex);
  GraphicsTransfer transfer;
  return transfer.submit(gx::GraphicsCommandResolve{.source = transfer.image(images.at(command.source)),
                                                    .destination = transfer.image(images.at(command.destination)),
                                                    .regions = command.copies});
}

static bool execute(Execution &, const recorded::Copy &command) {
  std::scoped_lock const lock(stateMutex);
  return command.regions ^ forall([&](const auto &region) {
           const auto source = bufferRange(command.source, region.srcOffset, region.size);
           const auto destination = bufferRange(command.destination, region.dstOffset, region.size);
           if (!source || !destination || source->bytes.size() != destination->bytes.size()) return false;
           std::memmove(destination->bytes.data(), source->bytes.data(), source->bytes.size());
           return true;
         });
}
bool execute(Execution &execution, const recorded::Operation &operation) {
  return std::visit([&](const auto &command) { return execute(execution, command); }, operation);
}
} // namespace gemstone::vulkan

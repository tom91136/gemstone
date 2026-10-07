#pragma once
#include <cstdio>

#include "gemstone_image_formats.hpp"

#include "resources.hpp"
#include "submission.hpp"

namespace gemstone::vulkan {
inline gx::GraphicsRect graphicsRect(VkRect2D area) {
  return {area.offset.x, area.offset.y, static_cast<int32_t>(area.extent.width), static_cast<int32_t>(area.extent.height)};
}
inline gx::GraphicsExtent graphicsExtent(VkExtent3D extent) {
  return {static_cast<int32_t>(extent.width), static_cast<int32_t>(extent.height), static_cast<int32_t>(extent.depth)};
}
inline gx::GraphicsOffset graphicsOffset(VkOffset3D offset) { return {offset.x, offset.y, offset.z}; }
inline gx::GraphicsLayers graphicsLayers(VkImageSubresourceLayers layers) {
  return {static_cast<int32_t>(layers.mipLevel),
          static_cast<int32_t>(layers.baseArrayLayer),
          static_cast<int32_t>(layers.layerCount),
          bool(layers.aspectMask & VK_IMAGE_ASPECT_COLOR_BIT),
          bool(layers.aspectMask & VK_IMAGE_ASPECT_DEPTH_BIT),
          bool(layers.aspectMask & VK_IMAGE_ASPECT_STENCIL_BIT)};
}
template <typename Region> gx::GraphicsImageCopy graphicsCopy(const Region &copy) {
  return {graphicsLayers(copy.srcSubresource), graphicsOffset(copy.srcOffset), graphicsLayers(copy.dstSubresource),
          graphicsOffset(copy.dstOffset), graphicsExtent(copy.extent)};
}
class GraphicsTransfer {
  SubmissionResources resources{rpcExecutionMutex};

public:
  gx::GraphicsRange range(VkDeviceMemory handle, VkDeviceSize offset, size_t size) {
    const auto &bytes = memories.at(handle)->bytes;
    if (!size || offset > bytes.size() || size > bytes.size() - offset) {
      resources.fail();
      return {};
    }
    auto result = resources.memory(bytes.data(), bytes.size(), false);
    result.offset.value = static_cast<int64_t>(offset);
    result.size.value = static_cast<int64_t>(size);
    return result;
  }

  gx::GraphicsRange buffer(VkBuffer handle, VkDeviceSize offset = 0) {
    const auto bound = bufferRange(handle, offset, VK_WHOLE_SIZE);
    if (!bound) {
      resources.fail();
      return {};
    }
    return range(bound->memory, bound->offset, bound->bytes.size());
  }
  gx::GraphicsImage image(const Image &value) {
    const auto format = graphicsFormat(value.format);
    if (!format) {
      resources.fail();
      return {};
    }
    auto layout = imageLayoutOf(value);
    return {range(value.memory, value.offset, layout.size),
            {std::move(layout.levels)},
            static_cast<int32_t>(value.layers),
            static_cast<int32_t>(value.samples),
            *format,
            value.type == VK_IMAGE_TYPE_3D};
  }
  // Stage resources must outlive this transfer and its synchronous submit.
  gx::GraphicsShader shader(const gx::PreparedShader &stage, const StageResources &resources,
                            const std::array<uint8_t, gx::GraphicsPushConstantBytes> &push) {
    gx::GraphicsShader result{stage.image, stage.entry.value, stage.interfaces, {}, {}, {}, stage.earlyFragmentTests, stage.fragmentQuads};
    auto bound = bindStageResources(resources, [&](const StageResources::Allocation &allocation) {
      return allocation.memory ? range(allocation.memory, allocation.offset, allocation.size)
                               : this->resources.memory(allocation.data, allocation.size, false);
    });
    if (!bound) {
      this->resources.fail();
      return result;
    }
    result.arguments = std::move(bound->arguments);
    result.descriptorBindings = std::move(bound->descriptorBindings);
    if (stage.pushConstants) result.pushConstants.assign(push.begin(), push.end());
    return result;
  }
  bool submit(std::vector<gx::GraphicsCommand> commands, uint64_t *samples = nullptr) {
    auto *client = resources.get();
    if (!client) return false;
    auto submitted = client->call(gx::SubmitGraphics{gx::GraphicsSubmission{std::move(commands)}});
    if (!submitted) {
      std::fprintf(stderr, "Vulkan graphics submission: %s\n", submitted.error().detail.c_str());
      return false;
    }
    return resources.complete(submitted.value(), samples);
  }
  bool submit(gx::GraphicsCommand command, uint64_t *samples = nullptr) {
    std::vector<gx::GraphicsCommand> commands;
    commands.push_back(std::move(command));
    return submit(std::move(commands), samples);
  }
};

} // namespace gemstone::vulkan

#pragma once

#include "gemstone_image_formats.hpp"

#include "state.hpp"

namespace gemstone::vulkan {
bool depthStencilFormat(VkFormat format);
VkImageAspectFlags imageAspect(VkFormat format);
bool validAspects(VkFormat format, VkImageAspectFlags aspects);
VkExtent3D mipExtent(const Image &image, uint32_t level);
struct ImageLayout {
  std::vector<gx::GraphicsImageLevel> levels;
  uint64_t size = 0;
};
ImageLayout imageLayoutOf(const Image &image);
uint64_t imageSize(const Image &image);
Image imageSlice(ImageSlice slice);
uint8_t *imageData(const Image &image);
std::optional<StageResources> descriptorResources(const std::vector<gx::ShaderDescriptor> &bindings,
                                                  const std::array<VkDescriptorSet, MaxBoundDescriptorSets> &sets,
                                                  const std::map<DescriptorSlot, uint32_t> &dynamicOffsets, uint32_t inputView = 0);
bool execute(Execution &execution, const recorded::Dispatch &dispatch);
} // namespace gemstone::vulkan

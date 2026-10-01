#pragma once

#include "gemstone_image_formats.hpp"

#include "state.hpp"

namespace gemstone::vulkan::adapter {
bool depthStencilFormat(VkFormat format);
VkImageAspectFlags imageAspect(VkFormat format);
bool validAspects(VkFormat format, VkImageAspectFlags aspects);
VkExtent3D mipExtent(const gemstone::vulkan::Image &image, uint32_t level);
struct ImageLayout {
  std::vector<gemstone::execution::GraphicsImageLevel> levels;
  uint64_t size = 0;
};
ImageLayout imageLayoutOf(const gemstone::vulkan::Image &image);
uint64_t imageSize(const gemstone::vulkan::Image &image);
gemstone::vulkan::Image imageSlice(ImageSlice slice);
uint8_t *imageData(const gemstone::vulkan::Image &image);
std::optional<gemstone::vulkan::StageResources> descriptorResources(const std::vector<gemstone::execution::ShaderDescriptor> &bindings,
                                                                    const std::array<VkDescriptorSet, MaxBoundDescriptorSets> &sets,
                                                                    const std::map<GemstoneVulkanDescriptorSlot, uint32_t> &dynamicOffsets,
                                                                    uint32_t inputView = 0);
bool run(const Dispatch &dispatch);
bool copyBuffers(const Copy &copy);
} // namespace gemstone::vulkan::adapter

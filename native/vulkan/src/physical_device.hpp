#pragma once

#include <limits>
#include <optional>

#include <vulkan/vulkan.h>

namespace gemstone::vulkan {
constexpr VkDeviceSize maxAllocationSize = static_cast<VkDeviceSize>(std::numeric_limits<int32_t>::max());
constexpr size_t memoryMapAlignment = 64;
// Image resource address-space bounds are distinct from the RPC allocation limit.
constexpr VkDeviceSize maxImageResourceSize = VkDeviceSize{1} << 31;

std::optional<uint32_t> configuredApiVersion();
void fillProperties(VkPhysicalDeviceProperties *properties);
void fillFeatures(VkPhysicalDeviceFeatures *features);
void fillMemoryProperties(VkPhysicalDeviceMemoryProperties *properties);
void fillPropertyChain(void *next);
void fillFeatureChain(void *next);
bool featureChainSupported(const void *next);
} // namespace gemstone::vulkan

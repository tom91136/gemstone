#include "physical_device.hpp"

#include <algorithm>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <limits>
#include <optional>
#include <string_view>

#include "gemstone_execution_rpc.hpp"
#include <gemstone/environment.hpp>

#include "environment_keys.hpp"
#include "graphics_state.hpp"

namespace gemstone::vulkan {
bool environmentAccepted() { return gemstone::environment::accepted(gemstone::environment::invalid()); }

std::optional<uint32_t> configuredApiVersion() {
  const char *value = gemstone::environment::value(gemstone::environment::VulkanApiVersion);
  if (!value || std::strcmp(value, "1.2") == 0) return VK_API_VERSION_1_2;
  if (std::strcmp(value, "1.0") == 0) return VK_API_VERSION_1_0;
  return std::nullopt;
}

void fillProperties(VkPhysicalDeviceProperties *properties) {
  if (!properties) return;
  *properties = {};
  properties->apiVersion = configuredApiVersion().value_or(VK_API_VERSION_1_0);
  properties->driverVersion = VK_MAKE_VERSION(1, 0, 0);
  properties->vendorID = 0;
  properties->deviceID = 1;
  properties->deviceType = VK_PHYSICAL_DEVICE_TYPE_CPU;
  const char *name = "Gemstone Vulkan Compute Device";
  if (const char *raw = gemstone::environment::value(gemstone::environment::ProfileName)) {
    // a rusticl profile names the Mesa driver, whose Vulkan side reports the same vendor
    std::string_view profile(raw);
    if (profile.starts_with("rusticl-")) profile.remove_prefix(std::string_view("rusticl-").size());
    if (profile == "amd") {
      name = "AMD Gemstone Vulkan GPU";
      properties->vendorID = 0x1002;
    } else if (profile == "nvidia") {
      name = "NVIDIA Gemstone Vulkan GPU";
      properties->vendorID = 0x10de;
    } else if (profile == "intel") {
      name = "Intel Gemstone Vulkan GPU";
      properties->vendorID = 0x8086;
    } else if (profile == "llvmpipe") {
      name = "llvmpipe (Gemstone Vulkan)";
      properties->vendorID = 0x10005;
    }
  }
  std::snprintf(properties->deviceName, sizeof(properties->deviceName), "%s", name);
  for (size_t i = 0; i < VK_UUID_SIZE; ++i)
    properties->pipelineCacheUUID[i] = static_cast<uint8_t>(0x47 + i);
  auto &limits = properties->limits;
  limits.subPixelPrecisionBits = 8;
  limits.subTexelPrecisionBits = limits.mipmapPrecisionBits = 8;
  limits.maxSamplerAllocationCount = 4096;
  limits.maxSamplerLodBias = 16;
  limits.maxSamplerAnisotropy = 1;
  limits.sampledImageColorSampleCounts = limits.sampledImageIntegerSampleCounts = limits.sampledImageDepthSampleCounts =
      VK_SAMPLE_COUNT_1_BIT | VK_SAMPLE_COUNT_4_BIT;
  limits.storageImageSampleCounts = VK_SAMPLE_COUNT_1_BIT;
  limits.maxFramebufferWidth = limits.maxFramebufferHeight = 16384;
  limits.maxFramebufferLayers = 2048;
  limits.maxColorAttachments = MaxColorAttachments;
  limits.maxDrawIndexedIndexValue = 0x00ffffffU;
  limits.maxDrawIndirectCount = 65535;
  limits.maxViewports = 1;
  limits.maxViewportDimensions[0] = limits.maxViewportDimensions[1] = 16384;
  limits.viewportBoundsRange[0] = -32768.0F;
  limits.viewportBoundsRange[1] = 32767.0F;
  limits.framebufferColorSampleCounts = VK_SAMPLE_COUNT_1_BIT | VK_SAMPLE_COUNT_4_BIT;
  limits.framebufferStencilSampleCounts = limits.sampledImageStencilSampleCounts = limits.framebufferDepthSampleCounts =
      VK_SAMPLE_COUNT_1_BIT | VK_SAMPLE_COUNT_4_BIT;
  limits.standardSampleLocations = VK_TRUE;
  limits.maxVertexInputAttributes = limits.maxVertexInputBindings = 16;
  limits.maxVertexInputAttributeOffset = 2047;
  limits.maxVertexInputBindingStride = 2048;
  limits.maxVertexOutputComponents = limits.maxFragmentInputComponents = 64;
  limits.maxFragmentOutputAttachments = MaxColorAttachments;
  limits.maxFragmentCombinedOutputResources = 128;
  limits.framebufferNoAttachmentsSampleCounts = VK_SAMPLE_COUNT_1_BIT | VK_SAMPLE_COUNT_4_BIT;
  limits.pointSizeRange[0] = limits.pointSizeRange[1] = 1;
  limits.pointSizeGranularity = 0;
  limits.lineWidthRange[0] = limits.lineWidthRange[1] = 1;
  limits.lineWidthGranularity = 0;
  limits.maxSampleMaskWords = 1;
  limits.maxImageDimension1D = limits.maxImageDimension2D = 16384;
  limits.maxImageDimension3D = 2048;
  limits.maxImageDimensionCube = 16384;
  limits.maxImageArrayLayers = 2048;
  limits.maxTexelBufferElements = std::numeric_limits<uint32_t>::max();
  limits.maxUniformBufferRange = 65536;
  limits.maxStorageBufferRange = std::numeric_limits<uint32_t>::max();
  limits.maxMemoryAllocationCount = 4096;
  limits.maxBoundDescriptorSets = MaxBoundDescriptorSets;
  limits.maxPushConstantsSize = gx::GraphicsPushConstantBytes;
  limits.maxPerStageDescriptorStorageBuffers = 1024;
  limits.maxPerStageDescriptorSamplers = limits.maxPerStageDescriptorSampledImages = 1024;
  limits.maxDescriptorSetSamplers = limits.maxDescriptorSetSampledImages = 1024;
  limits.maxPerStageDescriptorInputAttachments = limits.maxDescriptorSetInputAttachments = 8;
  limits.maxPerStageDescriptorStorageImages = limits.maxDescriptorSetStorageImages = 128;
  limits.maxPerStageDescriptorUniformBuffers = 1024;
  limits.maxPerStageResources = 2048;
  limits.maxDescriptorSetStorageBuffers = 1024;
  limits.maxDescriptorSetUniformBuffers = 1024;
  limits.maxDescriptorSetUniformBuffersDynamic = 8;
  limits.maxDescriptorSetStorageBuffersDynamic = 4;
  limits.maxComputeSharedMemorySize = 65536;
  limits.timestampComputeAndGraphics = VK_TRUE;
  limits.timestampPeriod = 1.0F;
  limits.maxComputeWorkGroupCount[0] = limits.maxComputeWorkGroupCount[1] = limits.maxComputeWorkGroupCount[2] = 65535;
  limits.maxComputeWorkGroupInvocations = 1024;
  limits.maxComputeWorkGroupSize[0] = 1024;
  limits.maxComputeWorkGroupSize[1] = 1024;
  limits.maxComputeWorkGroupSize[2] = 64;
  limits.minTexelOffset = -8;
  limits.maxTexelOffset = 7;
  limits.minTexelGatherOffset = -8;
  limits.maxTexelGatherOffset = 7;
  limits.discreteQueuePriorities = 2;
  limits.minTexelBufferOffsetAlignment = 1;
  limits.minMemoryMapAlignment = MemoryMapAlignment;
  limits.minUniformBufferOffsetAlignment = 16;
  limits.minStorageBufferOffsetAlignment = 16;
  limits.nonCoherentAtomSize = 1;
  limits.bufferImageGranularity = 1;
}

void fillFeatures(VkPhysicalDeviceFeatures *features) {
  if (!features) return;
  *features = {};
  features->robustBufferAccess = VK_TRUE;
  features->textureCompressionETC2 = VK_TRUE;
  features->shaderImageGatherExtended = VK_TRUE;
  features->vertexPipelineStoresAndAtomics = VK_TRUE;
  features->fragmentStoresAndAtomics = VK_TRUE;
  features->depthBounds = VK_TRUE;
  features->independentBlend = VK_TRUE;
  features->depthBiasClamp = VK_TRUE;
  features->multiDrawIndirect = VK_TRUE;
  features->drawIndirectFirstInstance = VK_TRUE;
  features->shaderFloat64 = VK_TRUE;
  features->shaderInt64 = VK_TRUE;
  features->shaderInt16 = VK_TRUE;
}

void fillMemoryProperties(VkPhysicalDeviceMemoryProperties *properties) {
  if (!properties) return;
  *properties = {};
  properties->memoryTypeCount = 1;
  properties->memoryTypes[0].propertyFlags = VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT | VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT
                                             | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT | VK_MEMORY_PROPERTY_HOST_CACHED_BIT;
  properties->memoryTypes[0].heapIndex = 0;
  properties->memoryHeapCount = 1;
  properties->memoryHeaps[0].size = VkDeviceSize{64} << 30;
  properties->memoryHeaps[0].flags = VK_MEMORY_HEAP_DEVICE_LOCAL_BIT;
}

// Output query structures retain the caller's chain, but every known payload
// field must be initialised even when the associated feature is unsupported.
template <typename T> static void resetQueryPayload(VkBaseOutStructure *base) {
  const auto type = base->sType;
  auto *next = base->pNext;
  *reinterpret_cast<T *>(base) = {};
  base->sType = type;
  base->pNext = next;
}

void fillPropertyChain(void *next) {
  for (auto *base = static_cast<VkBaseOutStructure *>(next); base; base = base->pNext) {
    switch (base->sType) {
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_PROPERTIES: {
        resetQueryPayload<VkPhysicalDeviceMultiviewProperties>(base);
        auto *value = reinterpret_cast<VkPhysicalDeviceMultiviewProperties *>(base);
        value->maxMultiviewViewCount = 6;
        value->maxMultiviewInstanceIndex = 134217727;
        break;
      }
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_POINT_CLIPPING_PROPERTIES:
        resetQueryPayload<VkPhysicalDevicePointClippingProperties>(base);
        break;
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROTECTED_MEMORY_PROPERTIES:
        resetQueryPayload<VkPhysicalDeviceProtectedMemoryProperties>(base);
        break;
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_STENCIL_RESOLVE_PROPERTIES:
        resetQueryPayload<VkPhysicalDeviceDepthStencilResolveProperties>(base);
        reinterpret_cast<VkPhysicalDeviceDepthStencilResolveProperties *>(base)->supportedDepthResolveModes =
            VK_RESOLVE_MODE_SAMPLE_ZERO_BIT;
        reinterpret_cast<VkPhysicalDeviceDepthStencilResolveProperties *>(base)->supportedStencilResolveModes =
            VK_RESOLVE_MODE_SAMPLE_ZERO_BIT;
        break;
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FLOAT_CONTROLS_PROPERTIES:
        resetQueryPayload<VkPhysicalDeviceFloatControlsProperties>(base);
        break;
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_INDEXING_PROPERTIES:
        resetQueryPayload<VkPhysicalDeviceDescriptorIndexingProperties>(base);
        break;
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SAMPLER_FILTER_MINMAX_PROPERTIES:
        resetQueryPayload<VkPhysicalDeviceSamplerFilterMinmaxProperties>(base);
        break;
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DRIVER_PROPERTIES: {
        resetQueryPayload<VkPhysicalDeviceDriverProperties>(base);
        auto *value = reinterpret_cast<VkPhysicalDeviceDriverProperties *>(base);
        std::snprintf(value->driverName, sizeof(value->driverName), "%s", "Gemstone");
        std::snprintf(value->driverInfo, sizeof(value->driverInfo), "%s", "Portable SPIR-V interpreter");
        // Matches the pinned CTS suite, not the supported Vulkan API version.
        value->conformanceVersion = {.major = 1, .minor = 4, .subminor = 6, .patch = 2};
        // Stable, unregistered local ID ("GEMS"); not a Khronos allocation.
        value->driverID = static_cast<VkDriverId>(0x47454d53);
        break;
      }
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_PROPERTIES: {
        resetQueryPayload<VkPhysicalDeviceVulkan11Properties>(base);
        auto *value = reinterpret_cast<VkPhysicalDeviceVulkan11Properties *>(base);
        VkPhysicalDeviceIDProperties identity{};
        identity.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ID_PROPERTIES;
        VkPhysicalDeviceSubgroupProperties subgroup{};
        subgroup.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBGROUP_PROPERTIES;
        VkPhysicalDeviceMaintenance3Properties maintenance{};
        maintenance.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_3_PROPERTIES;
        identity.pNext = &subgroup;
        subgroup.pNext = &maintenance;
        fillPropertyChain(&identity);
        std::copy_n(identity.deviceUUID, VK_UUID_SIZE, value->deviceUUID);
        std::copy_n(identity.driverUUID, VK_UUID_SIZE, value->driverUUID);
        value->subgroupSize = subgroup.subgroupSize;
        value->subgroupSupportedStages = subgroup.supportedStages;
        value->subgroupSupportedOperations = subgroup.supportedOperations;
        value->subgroupQuadOperationsInAllStages = subgroup.quadOperationsInAllStages;
        VkPhysicalDeviceMultiviewProperties multiview{};
        multiview.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_PROPERTIES;
        fillPropertyChain(&multiview);
        value->maxMultiviewViewCount = multiview.maxMultiviewViewCount;
        value->maxMultiviewInstanceIndex = multiview.maxMultiviewInstanceIndex;
        value->maxPerSetDescriptors = maintenance.maxPerSetDescriptors;
        value->maxMemoryAllocationSize = maintenance.maxMemoryAllocationSize;
        break;
      }
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ID_PROPERTIES: {
        resetQueryPayload<VkPhysicalDeviceIDProperties>(base);
        auto *value = reinterpret_cast<VkPhysicalDeviceIDProperties *>(base);
        for (size_t i = 0; i < VK_UUID_SIZE; ++i) {
          value->deviceUUID[i] = static_cast<uint8_t>(0x47 + i);
          value->driverUUID[i] = static_cast<uint8_t>(0x67 + i);
        }
        value->deviceLUIDValid = VK_FALSE;
        break;
      }
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TIMELINE_SEMAPHORE_PROPERTIES:
        reinterpret_cast<VkPhysicalDeviceTimelineSemaphoreProperties *>(base)->maxTimelineSemaphoreValueDifference = UINT64_MAX;
        break;
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_PROPERTIES: {
        resetQueryPayload<VkPhysicalDeviceVulkan12Properties>(base);
        auto *value = reinterpret_cast<VkPhysicalDeviceVulkan12Properties *>(base);
        value->maxTimelineSemaphoreValueDifference = UINT64_MAX;
        VkPhysicalDeviceDriverProperties driver{};
        driver.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DRIVER_PROPERTIES;
        fillPropertyChain(&driver);
        value->driverID = driver.driverID;
        value->conformanceVersion = driver.conformanceVersion;
        std::copy_n(driver.driverName, VK_MAX_DRIVER_NAME_SIZE, value->driverName);
        std::copy_n(driver.driverInfo, VK_MAX_DRIVER_INFO_SIZE, value->driverInfo);
        value->supportedDepthResolveModes = VK_RESOLVE_MODE_SAMPLE_ZERO_BIT;
        value->supportedStencilResolveModes = VK_RESOLVE_MODE_SAMPLE_ZERO_BIT;
        value->framebufferIntegerColorSampleCounts = VK_SAMPLE_COUNT_1_BIT | VK_SAMPLE_COUNT_4_BIT;
        break;
      }
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_3_PROPERTIES: {
        auto *value = reinterpret_cast<VkPhysicalDeviceMaintenance3Properties *>(base);
        value->maxPerSetDescriptors = MaxDescriptorsPerSet;
        value->maxMemoryAllocationSize = MaxAllocationSize;
        break;
      }
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBGROUP_PROPERTIES: {
        auto *value = reinterpret_cast<VkPhysicalDeviceSubgroupProperties *>(base);
        value->subgroupSize = 32;
        value->supportedStages = VK_SHADER_STAGE_COMPUTE_BIT;
        value->supportedOperations = VK_SUBGROUP_FEATURE_BASIC_BIT | VK_SUBGROUP_FEATURE_VOTE_BIT | VK_SUBGROUP_FEATURE_ARITHMETIC_BIT
                                     | VK_SUBGROUP_FEATURE_BALLOT_BIT | VK_SUBGROUP_FEATURE_SHUFFLE_BIT
                                     | VK_SUBGROUP_FEATURE_SHUFFLE_RELATIVE_BIT;
        value->quadOperationsInAllStages = VK_FALSE;
        break;
      }
      default: break;
    }
  }
}

template <typename T> static T supportedFeatures(VkStructureType type) {
  T value{};
  value.sType = type;
  if constexpr (requires { value.timelineSemaphore; }) value.timelineSemaphore = VK_TRUE;
  if constexpr (requires { value.hostQueryReset; }) value.hostQueryReset = VK_TRUE;
  if constexpr (requires { value.shaderFloat16; }) value.shaderFloat16 = VK_TRUE;
  if constexpr (requires { value.shaderInt8; }) value.shaderInt8 = VK_TRUE;
  if constexpr (requires { value.storageBuffer16BitAccess; }) value.storageBuffer16BitAccess = VK_TRUE;
  if constexpr (requires { value.uniformAndStorageBuffer16BitAccess; }) value.uniformAndStorageBuffer16BitAccess = VK_TRUE;
  if constexpr (requires { value.uniformBufferStandardLayout; }) value.uniformBufferStandardLayout = VK_TRUE;
  if constexpr (requires { value.subgroupBroadcastDynamicId; }) value.subgroupBroadcastDynamicId = VK_TRUE;
  if constexpr (requires { value.shaderSubgroupExtendedTypes; }) value.shaderSubgroupExtendedTypes = VK_TRUE;
  if constexpr (requires { value.separateDepthStencilLayouts; }) value.separateDepthStencilLayouts = VK_TRUE;
  if constexpr (requires { value.multiview; }) value.multiview = VK_TRUE;
  if constexpr (requires { value.imagelessFramebuffer; }) value.imagelessFramebuffer = VK_TRUE;
  return value;
}

#define GEMSTONE_FEATURE_STRUCTURES(F)                                                                                                     \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_FEATURES, VkPhysicalDeviceVulkan11Features, storageBuffer16BitAccess,                     \
    shaderDrawParameters)                                                                                                                  \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_16BIT_STORAGE_FEATURES, VkPhysicalDevice16BitStorageFeatures, storageBuffer16BitAccess,              \
    storageInputOutput16)                                                                                                                  \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_FEATURES, VkPhysicalDeviceMultiviewFeatures, multiview, multiviewTessellationShader)       \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VARIABLE_POINTERS_FEATURES, VkPhysicalDeviceVariablePointersFeatures, variablePointersStorageBuffer, \
    variablePointers)                                                                                                                      \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROTECTED_MEMORY_FEATURES, VkPhysicalDeviceProtectedMemoryFeatures, protectedMemory,                 \
    protectedMemory)                                                                                                                       \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SAMPLER_YCBCR_CONVERSION_FEATURES, VkPhysicalDeviceSamplerYcbcrConversionFeatures,                   \
    samplerYcbcrConversion, samplerYcbcrConversion)                                                                                        \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_DRAW_PARAMETERS_FEATURES, VkPhysicalDeviceShaderDrawParametersFeatures, shaderDrawParameters, \
    shaderDrawParameters)                                                                                                                  \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_8BIT_STORAGE_FEATURES, VkPhysicalDevice8BitStorageFeatures, storageBuffer8BitAccess,                 \
    storagePushConstant8)                                                                                                                  \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ATOMIC_INT64_FEATURES, VkPhysicalDeviceShaderAtomicInt64Features, shaderBufferInt64Atomics,   \
    shaderSharedInt64Atomics)                                                                                                              \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_INDEXING_FEATURES, VkPhysicalDeviceDescriptorIndexingFeatures,                            \
    shaderInputAttachmentArrayDynamicIndexing, runtimeDescriptorArray)                                                                     \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SCALAR_BLOCK_LAYOUT_FEATURES, VkPhysicalDeviceScalarBlockLayoutFeatures, scalarBlockLayout,          \
    scalarBlockLayout)                                                                                                                     \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGELESS_FRAMEBUFFER_FEATURES, VkPhysicalDeviceImagelessFramebufferFeatures, imagelessFramebuffer,  \
    imagelessFramebuffer)                                                                                                                  \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_UNIFORM_BUFFER_STANDARD_LAYOUT_FEATURES, VkPhysicalDeviceUniformBufferStandardLayoutFeatures,        \
    uniformBufferStandardLayout, uniformBufferStandardLayout)                                                                              \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SUBGROUP_EXTENDED_TYPES_FEATURES, VkPhysicalDeviceShaderSubgroupExtendedTypesFeatures,        \
    shaderSubgroupExtendedTypes, shaderSubgroupExtendedTypes)                                                                              \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SEPARATE_DEPTH_STENCIL_LAYOUTS_FEATURES, VkPhysicalDeviceSeparateDepthStencilLayoutsFeatures,        \
    separateDepthStencilLayouts, separateDepthStencilLayouts)                                                                              \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BUFFER_DEVICE_ADDRESS_FEATURES, VkPhysicalDeviceBufferDeviceAddressFeatures, bufferDeviceAddress,    \
    bufferDeviceAddressMultiDevice)                                                                                                        \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_MEMORY_MODEL_FEATURES, VkPhysicalDeviceVulkanMemoryModelFeatures, vulkanMemoryModel,          \
    vulkanMemoryModelAvailabilityVisibilityChains)                                                                                         \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TIMELINE_SEMAPHORE_FEATURES, VkPhysicalDeviceTimelineSemaphoreFeatures, timelineSemaphore,           \
    timelineSemaphore)                                                                                                                     \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HOST_QUERY_RESET_FEATURES, VkPhysicalDeviceHostQueryResetFeatures, hostQueryReset, hostQueryReset)   \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_FEATURES, VkPhysicalDeviceVulkan12Features, samplerMirrorClampToEdge,                     \
    subgroupBroadcastDynamicId)                                                                                                            \
  F(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_FLOAT16_INT8_FEATURES, VkPhysicalDeviceShaderFloat16Int8Features, shaderFloat16, shaderInt8)

void fillFeatureChain(void *next) {
  for (auto *base = static_cast<VkBaseOutStructure *>(next); base;) {
    VkBaseOutStructure header;
    std::memcpy(&header, base, sizeof(header));
    switch (header.sType) {
#define GEMSTONE_QUERY_FEATURES(tag, type, first, last)                                                                                    \
  case tag: {                                                                                                                              \
    auto value = supportedFeatures<type>(tag);                                                                                             \
    value.pNext = header.pNext;                                                                                                            \
    std::memcpy(base, &value, sizeof(value));                                                                                              \
    break;                                                                                                                                 \
  }
      GEMSTONE_FEATURE_STRUCTURES(GEMSTONE_QUERY_FEATURES)
#undef GEMSTONE_QUERY_FEATURES
      default: break;
    }
    base = header.pNext;
  }
}

template <typename T> static bool featureRequestSupported(const VkBaseInStructure *base, VkBool32 T::*first, VkBool32 T::*last) {
  const auto *requested = reinterpret_cast<const T *>(base);
  const T supported = supportedFeatures<T>(base->sType);
  const auto *wanted = reinterpret_cast<const uint8_t *>(requested);
  const auto *available = reinterpret_cast<const uint8_t *>(&supported);
  const auto begin = reinterpret_cast<const uint8_t *>(&(requested->*first)) - wanted;
  const auto end = reinterpret_cast<const uint8_t *>(&(requested->*last)) - wanted + sizeof(VkBool32);
  for (size_t offset = begin; offset < static_cast<size_t>(end); offset += sizeof(VkBool32)) {
    VkBool32 value{};
    VkBool32 support{};
    std::memcpy(&value, wanted + offset, sizeof(value));
    std::memcpy(&support, available + offset, sizeof(support));
    if (value && !support) return false;
  }
  return true;
}
bool featureChainSupported(const void *next) {
  for (const auto *base = static_cast<const VkBaseInStructure *>(next); base; base = base->pNext) {
    switch (base->sType) {
#define GEMSTONE_FEATURES(tag, type, first, last)                                                                                          \
  case tag:                                                                                                                                \
    if (!featureRequestSupported<type>(base, &type::first, &type::last)) return false;                                                     \
    break;
      GEMSTONE_FEATURE_STRUCTURES(GEMSTONE_FEATURES)
#undef GEMSTONE_FEATURES
      default: break;
    }
  }
  return true;
}

#undef GEMSTONE_FEATURE_STRUCTURES
} // namespace gemstone::vulkan

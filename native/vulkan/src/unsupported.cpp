#include <cstdlib>

#include <vulkan/vulkan.h>

// Explicit facade decisions for the pinned Vulkan 1.4.341 headers.
// The generated ABI check fails at link time when a new entry point is not
// implemented or reviewed into this inventory. Polyregion uses the loader
// dispatch path for the Vulkan operations it needs.

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkAcquireDrmDisplayEXT(VkPhysicalDevice, int32_t, VkDisplayKHR) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkAcquireNextImage2KHR(VkDevice, const VkAcquireNextImageInfoKHR *, uint32_t *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkAcquireNextImageKHR(VkDevice, VkSwapchainKHR, uint64_t, VkSemaphore, VkFence, uint32_t *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkAcquirePerformanceConfigurationINTEL(VkDevice,
                                                                                 const VkPerformanceConfigurationAcquireInfoINTEL *,
                                                                                 VkPerformanceConfigurationINTEL *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkAcquireProfilingLockKHR(VkDevice, const VkAcquireProfilingLockInfoKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkAntiLagUpdateAMD(VkDevice, const VkAntiLagDataAMD *) { std::abort(); }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindAccelerationStructureMemoryNV(VkDevice, uint32_t,
                                                                              const VkBindAccelerationStructureMemoryInfoNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindDataGraphPipelineSessionMemoryARM(VkDevice, uint32_t,
                                                                                  const VkBindDataGraphPipelineSessionMemoryInfoARM *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindImageMemory(VkDevice, VkImage, VkDeviceMemory, VkDeviceSize) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindImageMemory2(VkDevice, uint32_t, const VkBindImageMemoryInfo *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindImageMemory2KHR(VkDevice, uint32_t, const VkBindImageMemoryInfo *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindOpticalFlowSessionImageNV(VkDevice, VkOpticalFlowSessionNV,
                                                                          VkOpticalFlowSessionBindingPointNV, VkImageView, VkImageLayout) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindTensorMemoryARM(VkDevice, uint32_t, const VkBindTensorMemoryInfoARM *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindVideoSessionMemoryKHR(VkDevice, VkVideoSessionKHR, uint32_t,
                                                                      const VkBindVideoSessionMemoryInfoKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBuildAccelerationStructuresKHR(VkDevice, VkDeferredOperationKHR, uint32_t,
                                                                           const VkAccelerationStructureBuildGeometryInfoKHR *,
                                                                           const VkAccelerationStructureBuildRangeInfoKHR *const *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBuildMicromapsEXT(VkDevice, VkDeferredOperationKHR, uint32_t, const VkMicromapBuildInfoEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginConditionalRenderingEXT(VkCommandBuffer, const VkConditionalRenderingBeginInfoEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginCustomResolveEXT(VkCommandBuffer, const VkBeginCustomResolveInfoEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginDebugUtilsLabelEXT(VkCommandBuffer, const VkDebugUtilsLabelEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginPerTileExecutionQCOM(VkCommandBuffer, const VkPerTileBeginInfoQCOM *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginQuery(VkCommandBuffer, VkQueryPool, uint32_t, VkQueryControlFlags) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginQueryIndexedEXT(VkCommandBuffer, VkQueryPool, uint32_t, VkQueryControlFlags, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginRenderPass(VkCommandBuffer, const VkRenderPassBeginInfo *, VkSubpassContents) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginRenderPass2(VkCommandBuffer, const VkRenderPassBeginInfo *, const VkSubpassBeginInfo *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginRenderPass2KHR(VkCommandBuffer, const VkRenderPassBeginInfo *, const VkSubpassBeginInfo *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginRendering(VkCommandBuffer, const VkRenderingInfo *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginRenderingKHR(VkCommandBuffer, const VkRenderingInfo *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginTransformFeedbackEXT(VkCommandBuffer, uint32_t, uint32_t, const VkBuffer *,
                                                                     const VkDeviceSize *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginVideoCodingKHR(VkCommandBuffer, const VkVideoBeginCodingInfoKHR *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindDescriptorBufferEmbeddedSamplers2EXT(VkCommandBuffer,
                                                                                    const VkBindDescriptorBufferEmbeddedSamplersInfoEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindDescriptorBufferEmbeddedSamplersEXT(VkCommandBuffer, VkPipelineBindPoint, VkPipelineLayout,
                                                                                   uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindDescriptorBuffersEXT(VkCommandBuffer, uint32_t, const VkDescriptorBufferBindingInfoEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindDescriptorSets2(VkCommandBuffer, const VkBindDescriptorSetsInfo *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindDescriptorSets2KHR(VkCommandBuffer, const VkBindDescriptorSetsInfo *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindIndexBuffer(VkCommandBuffer, VkBuffer, VkDeviceSize, VkIndexType) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindIndexBuffer2(VkCommandBuffer, VkBuffer, VkDeviceSize, VkDeviceSize, VkIndexType) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindIndexBuffer2KHR(VkCommandBuffer, VkBuffer, VkDeviceSize, VkDeviceSize, VkIndexType) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindInvocationMaskHUAWEI(VkCommandBuffer, VkImageView, VkImageLayout) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindPipelineShaderGroupNV(VkCommandBuffer, VkPipelineBindPoint, VkPipeline, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindResourceHeapEXT(VkCommandBuffer, const VkBindHeapInfoEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindSamplerHeapEXT(VkCommandBuffer, const VkBindHeapInfoEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindShadersEXT(VkCommandBuffer, uint32_t, const VkShaderStageFlagBits *, const VkShaderEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindShadingRateImageNV(VkCommandBuffer, VkImageView, VkImageLayout) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindTileMemoryQCOM(VkCommandBuffer, const VkTileMemoryBindInfoQCOM *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindTransformFeedbackBuffersEXT(VkCommandBuffer, uint32_t, uint32_t, const VkBuffer *,
                                                                           const VkDeviceSize *, const VkDeviceSize *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindVertexBuffers(VkCommandBuffer, uint32_t, uint32_t, const VkBuffer *, const VkDeviceSize *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindVertexBuffers2(VkCommandBuffer, uint32_t, uint32_t, const VkBuffer *, const VkDeviceSize *,
                                                              const VkDeviceSize *, const VkDeviceSize *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindVertexBuffers2EXT(VkCommandBuffer, uint32_t, uint32_t, const VkBuffer *,
                                                                 const VkDeviceSize *, const VkDeviceSize *, const VkDeviceSize *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBlitImage(VkCommandBuffer, VkImage, VkImageLayout, VkImage, VkImageLayout, uint32_t,
                                                     const VkImageBlit *, VkFilter) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBlitImage2(VkCommandBuffer, const VkBlitImageInfo2 *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBlitImage2KHR(VkCommandBuffer, const VkBlitImageInfo2 *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBuildAccelerationStructureNV(VkCommandBuffer, const VkAccelerationStructureInfoNV *, VkBuffer,
                                                                        VkDeviceSize, VkBool32, VkAccelerationStructureNV,
                                                                        VkAccelerationStructureNV, VkBuffer, VkDeviceSize) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBuildAccelerationStructuresIndirectKHR(VkCommandBuffer, uint32_t,
                                                                                  const VkAccelerationStructureBuildGeometryInfoKHR *,
                                                                                  const VkDeviceAddress *, const uint32_t *,
                                                                                  const uint32_t *const *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBuildAccelerationStructuresKHR(VkCommandBuffer, uint32_t,
                                                                          const VkAccelerationStructureBuildGeometryInfoKHR *,
                                                                          const VkAccelerationStructureBuildRangeInfoKHR *const *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL
vkCmdBuildClusterAccelerationStructureIndirectNV(VkCommandBuffer, const VkClusterAccelerationStructureCommandsInfoNV *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBuildMicromapsEXT(VkCommandBuffer, uint32_t, const VkMicromapBuildInfoEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBuildPartitionedAccelerationStructuresNV(VkCommandBuffer,
                                                                                    const VkBuildPartitionedAccelerationStructureInfoNV *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdClearAttachments(VkCommandBuffer, uint32_t, const VkClearAttachment *, uint32_t,
                                                            const VkClearRect *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdClearColorImage(VkCommandBuffer, VkImage, VkImageLayout, const VkClearColorValue *, uint32_t,
                                                           const VkImageSubresourceRange *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdClearDepthStencilImage(VkCommandBuffer, VkImage, VkImageLayout, const VkClearDepthStencilValue *,
                                                                  uint32_t, const VkImageSubresourceRange *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdControlVideoCodingKHR(VkCommandBuffer, const VkVideoCodingControlInfoKHR *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdConvertCooperativeVectorMatrixNV(VkCommandBuffer, uint32_t,
                                                                            const VkConvertCooperativeVectorMatrixInfoNV *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyAccelerationStructureKHR(VkCommandBuffer, const VkCopyAccelerationStructureInfoKHR *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyAccelerationStructureNV(VkCommandBuffer, VkAccelerationStructureNV,
                                                                       VkAccelerationStructureNV, VkCopyAccelerationStructureModeKHR) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyAccelerationStructureToMemoryKHR(VkCommandBuffer,
                                                                                const VkCopyAccelerationStructureToMemoryInfoKHR *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyBuffer2(VkCommandBuffer, const VkCopyBufferInfo2 *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyBuffer2KHR(VkCommandBuffer, const VkCopyBufferInfo2 *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyBufferToImage(VkCommandBuffer, VkBuffer, VkImage, VkImageLayout, uint32_t,
                                                             const VkBufferImageCopy *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyBufferToImage2(VkCommandBuffer, const VkCopyBufferToImageInfo2 *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyBufferToImage2KHR(VkCommandBuffer, const VkCopyBufferToImageInfo2 *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyImage(VkCommandBuffer, VkImage, VkImageLayout, VkImage, VkImageLayout, uint32_t,
                                                     const VkImageCopy *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyImage2(VkCommandBuffer, const VkCopyImageInfo2 *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyImage2KHR(VkCommandBuffer, const VkCopyImageInfo2 *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyImageToBuffer(VkCommandBuffer, VkImage, VkImageLayout, VkBuffer, uint32_t,
                                                             const VkBufferImageCopy *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyImageToBuffer2(VkCommandBuffer, const VkCopyImageToBufferInfo2 *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyImageToBuffer2KHR(VkCommandBuffer, const VkCopyImageToBufferInfo2 *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyMemoryIndirectKHR(VkCommandBuffer, const VkCopyMemoryIndirectInfoKHR *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyMemoryIndirectNV(VkCommandBuffer, VkDeviceAddress, uint32_t, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyMemoryToAccelerationStructureKHR(VkCommandBuffer,
                                                                                const VkCopyMemoryToAccelerationStructureInfoKHR *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyMemoryToImageIndirectKHR(VkCommandBuffer, const VkCopyMemoryToImageIndirectInfoKHR *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyMemoryToImageIndirectNV(VkCommandBuffer, VkDeviceAddress, uint32_t, uint32_t, VkImage,
                                                                       VkImageLayout, const VkImageSubresourceLayers *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyMemoryToMicromapEXT(VkCommandBuffer, const VkCopyMemoryToMicromapInfoEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyMicromapEXT(VkCommandBuffer, const VkCopyMicromapInfoEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyMicromapToMemoryEXT(VkCommandBuffer, const VkCopyMicromapToMemoryInfoEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyQueryPoolResults(VkCommandBuffer, VkQueryPool, uint32_t, uint32_t, VkBuffer, VkDeviceSize,
                                                                VkDeviceSize, VkQueryResultFlags) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyTensorARM(VkCommandBuffer, const VkCopyTensorInfoARM *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCuLaunchKernelNVX(VkCommandBuffer, const VkCuLaunchInfoNVX *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDebugMarkerBeginEXT(VkCommandBuffer, const VkDebugMarkerMarkerInfoEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDebugMarkerEndEXT(VkCommandBuffer) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDebugMarkerInsertEXT(VkCommandBuffer, const VkDebugMarkerMarkerInfoEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDecodeVideoKHR(VkCommandBuffer, const VkVideoDecodeInfoKHR *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDecompressMemoryEXT(VkCommandBuffer, const VkDecompressMemoryInfoEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDecompressMemoryIndirectCountEXT(VkCommandBuffer, VkMemoryDecompressionMethodFlagsEXT,
                                                                            VkDeviceAddress, VkDeviceAddress, uint32_t, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDecompressMemoryIndirectCountNV(VkCommandBuffer, VkDeviceAddress, VkDeviceAddress, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDecompressMemoryNV(VkCommandBuffer, uint32_t, const VkDecompressMemoryRegionNV *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDispatchBase(VkCommandBuffer, uint32_t, uint32_t, uint32_t, uint32_t, uint32_t, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDispatchBaseKHR(VkCommandBuffer, uint32_t, uint32_t, uint32_t, uint32_t, uint32_t, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDispatchDataGraphARM(VkCommandBuffer, VkDataGraphPipelineSessionARM,
                                                                const VkDataGraphPipelineDispatchInfoARM *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDispatchIndirect(VkCommandBuffer, VkBuffer, VkDeviceSize) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDispatchTileQCOM(VkCommandBuffer, const VkDispatchTileInfoQCOM *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDraw(VkCommandBuffer, uint32_t, uint32_t, uint32_t, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawClusterHUAWEI(VkCommandBuffer, uint32_t, uint32_t, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawClusterIndirectHUAWEI(VkCommandBuffer, VkBuffer, VkDeviceSize) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawIndexed(VkCommandBuffer, uint32_t, uint32_t, uint32_t, int32_t, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawIndexedIndirect(VkCommandBuffer, VkBuffer, VkDeviceSize, uint32_t, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawIndexedIndirectCount(VkCommandBuffer, VkBuffer, VkDeviceSize, VkBuffer, VkDeviceSize,
                                                                    uint32_t, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawIndexedIndirectCountAMD(VkCommandBuffer, VkBuffer, VkDeviceSize, VkBuffer, VkDeviceSize,
                                                                       uint32_t, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawIndexedIndirectCountKHR(VkCommandBuffer, VkBuffer, VkDeviceSize, VkBuffer, VkDeviceSize,
                                                                       uint32_t, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawIndirect(VkCommandBuffer, VkBuffer, VkDeviceSize, uint32_t, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawIndirectByteCountEXT(VkCommandBuffer, uint32_t, uint32_t, VkBuffer, VkDeviceSize, uint32_t,
                                                                    uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawIndirectCount(VkCommandBuffer, VkBuffer, VkDeviceSize, VkBuffer, VkDeviceSize, uint32_t,
                                                             uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawIndirectCountAMD(VkCommandBuffer, VkBuffer, VkDeviceSize, VkBuffer, VkDeviceSize, uint32_t,
                                                                uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawIndirectCountKHR(VkCommandBuffer, VkBuffer, VkDeviceSize, VkBuffer, VkDeviceSize, uint32_t,
                                                                uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawMeshTasksEXT(VkCommandBuffer, uint32_t, uint32_t, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawMeshTasksIndirectCountEXT(VkCommandBuffer, VkBuffer, VkDeviceSize, VkBuffer, VkDeviceSize,
                                                                         uint32_t, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawMeshTasksIndirectCountNV(VkCommandBuffer, VkBuffer, VkDeviceSize, VkBuffer, VkDeviceSize,
                                                                        uint32_t, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawMeshTasksIndirectEXT(VkCommandBuffer, VkBuffer, VkDeviceSize, uint32_t, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawMeshTasksIndirectNV(VkCommandBuffer, VkBuffer, VkDeviceSize, uint32_t, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawMeshTasksNV(VkCommandBuffer, uint32_t, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawMultiEXT(VkCommandBuffer, uint32_t, const VkMultiDrawInfoEXT *, uint32_t, uint32_t,
                                                        uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawMultiIndexedEXT(VkCommandBuffer, uint32_t, const VkMultiDrawIndexedInfoEXT *, uint32_t,
                                                               uint32_t, uint32_t, const int32_t *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEncodeVideoKHR(VkCommandBuffer, const VkVideoEncodeInfoKHR *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndConditionalRenderingEXT(VkCommandBuffer) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndDebugUtilsLabelEXT(VkCommandBuffer) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndPerTileExecutionQCOM(VkCommandBuffer, const VkPerTileEndInfoQCOM *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndQuery(VkCommandBuffer, VkQueryPool, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndQueryIndexedEXT(VkCommandBuffer, VkQueryPool, uint32_t, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndRenderPass(VkCommandBuffer) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndRenderPass2(VkCommandBuffer, const VkSubpassEndInfo *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndRenderPass2KHR(VkCommandBuffer, const VkSubpassEndInfo *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndRendering(VkCommandBuffer) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndRendering2EXT(VkCommandBuffer, const VkRenderingEndInfoKHR *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndRendering2KHR(VkCommandBuffer, const VkRenderingEndInfoKHR *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndRenderingKHR(VkCommandBuffer) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndTransformFeedbackEXT(VkCommandBuffer, uint32_t, uint32_t, const VkBuffer *,
                                                                   const VkDeviceSize *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndVideoCodingKHR(VkCommandBuffer, const VkVideoEndCodingInfoKHR *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdExecuteCommands(VkCommandBuffer, uint32_t, const VkCommandBuffer *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdExecuteGeneratedCommandsEXT(VkCommandBuffer, VkBool32, const VkGeneratedCommandsInfoEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdExecuteGeneratedCommandsNV(VkCommandBuffer, VkBool32, const VkGeneratedCommandsInfoNV *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdFillBuffer(VkCommandBuffer, VkBuffer, VkDeviceSize, VkDeviceSize, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdInsertDebugUtilsLabelEXT(VkCommandBuffer, const VkDebugUtilsLabelEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdNextSubpass(VkCommandBuffer, VkSubpassContents) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdNextSubpass2(VkCommandBuffer, const VkSubpassBeginInfo *, const VkSubpassEndInfo *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdNextSubpass2KHR(VkCommandBuffer, const VkSubpassBeginInfo *, const VkSubpassEndInfo *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdOpticalFlowExecuteNV(VkCommandBuffer, VkOpticalFlowSessionNV,
                                                                const VkOpticalFlowExecuteInfoNV *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPipelineBarrier(VkCommandBuffer, VkPipelineStageFlags, VkPipelineStageFlags, VkDependencyFlags,
                                                           uint32_t, const VkMemoryBarrier *, uint32_t, const VkBufferMemoryBarrier *,
                                                           uint32_t, const VkImageMemoryBarrier *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPipelineBarrier2(VkCommandBuffer, const VkDependencyInfo *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPipelineBarrier2KHR(VkCommandBuffer, const VkDependencyInfo *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPreprocessGeneratedCommandsEXT(VkCommandBuffer, const VkGeneratedCommandsInfoEXT *,
                                                                          VkCommandBuffer) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPreprocessGeneratedCommandsNV(VkCommandBuffer, const VkGeneratedCommandsInfoNV *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPushConstants(VkCommandBuffer, VkPipelineLayout, VkShaderStageFlags, uint32_t, uint32_t,
                                                         const void *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPushConstants2(VkCommandBuffer, const VkPushConstantsInfo *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPushConstants2KHR(VkCommandBuffer, const VkPushConstantsInfo *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPushDataEXT(VkCommandBuffer, const VkPushDataInfoEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPushDescriptorSet(VkCommandBuffer, VkPipelineBindPoint, VkPipelineLayout, uint32_t, uint32_t,
                                                             const VkWriteDescriptorSet *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPushDescriptorSet2(VkCommandBuffer, const VkPushDescriptorSetInfo *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPushDescriptorSet2KHR(VkCommandBuffer, const VkPushDescriptorSetInfo *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPushDescriptorSetKHR(VkCommandBuffer, VkPipelineBindPoint, VkPipelineLayout, uint32_t, uint32_t,
                                                                const VkWriteDescriptorSet *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPushDescriptorSetWithTemplate(VkCommandBuffer, VkDescriptorUpdateTemplate, VkPipelineLayout,
                                                                         uint32_t, const void *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPushDescriptorSetWithTemplate2(VkCommandBuffer, const VkPushDescriptorSetWithTemplateInfo *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPushDescriptorSetWithTemplate2KHR(VkCommandBuffer, const VkPushDescriptorSetWithTemplateInfo *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPushDescriptorSetWithTemplateKHR(VkCommandBuffer, VkDescriptorUpdateTemplate, VkPipelineLayout,
                                                                            uint32_t, const void *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdResetEvent(VkCommandBuffer, VkEvent, VkPipelineStageFlags) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdResetEvent2(VkCommandBuffer, VkEvent, VkPipelineStageFlags2) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdResetEvent2KHR(VkCommandBuffer, VkEvent, VkPipelineStageFlags2) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdResetQueryPool(VkCommandBuffer, VkQueryPool, uint32_t, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdResolveImage(VkCommandBuffer, VkImage, VkImageLayout, VkImage, VkImageLayout, uint32_t,
                                                        const VkImageResolve *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdResolveImage2(VkCommandBuffer, const VkResolveImageInfo2 *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdResolveImage2KHR(VkCommandBuffer, const VkResolveImageInfo2 *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetAlphaToCoverageEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetAlphaToOneEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetAttachmentFeedbackLoopEnableEXT(VkCommandBuffer, VkImageAspectFlags) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetBlendConstants(VkCommandBuffer, const float *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetCheckpointNV(VkCommandBuffer, const void *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetCoarseSampleOrderNV(VkCommandBuffer, VkCoarseSampleOrderTypeNV, uint32_t,
                                                                  const VkCoarseSampleOrderCustomNV *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetColorBlendAdvancedEXT(VkCommandBuffer, uint32_t, uint32_t, const VkColorBlendAdvancedEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetColorBlendEnableEXT(VkCommandBuffer, uint32_t, uint32_t, const VkBool32 *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetColorBlendEquationEXT(VkCommandBuffer, uint32_t, uint32_t, const VkColorBlendEquationEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetColorWriteEnableEXT(VkCommandBuffer, uint32_t, const VkBool32 *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetColorWriteMaskEXT(VkCommandBuffer, uint32_t, uint32_t, const VkColorComponentFlags *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetComputeOccupancyPriorityNV(VkCommandBuffer, const VkComputeOccupancyPriorityParametersNV *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetConservativeRasterizationModeEXT(VkCommandBuffer, VkConservativeRasterizationModeEXT) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetCoverageModulationModeNV(VkCommandBuffer, VkCoverageModulationModeNV) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetCoverageModulationTableEnableNV(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetCoverageModulationTableNV(VkCommandBuffer, uint32_t, const float *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetCoverageReductionModeNV(VkCommandBuffer, VkCoverageReductionModeNV) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetCoverageToColorEnableNV(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetCoverageToColorLocationNV(VkCommandBuffer, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetCullMode(VkCommandBuffer, VkCullModeFlags) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetCullModeEXT(VkCommandBuffer, VkCullModeFlags) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthBias(VkCommandBuffer, float, float, float) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthBias2EXT(VkCommandBuffer, const VkDepthBiasInfoEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthBiasEnable(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthBiasEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthBounds(VkCommandBuffer, float, float) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthBoundsTestEnable(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthBoundsTestEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthClampEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthClampRangeEXT(VkCommandBuffer, VkDepthClampModeEXT, const VkDepthClampRangeEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthClipEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthClipNegativeOneToOneEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthCompareOp(VkCommandBuffer, VkCompareOp) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthCompareOpEXT(VkCommandBuffer, VkCompareOp) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthTestEnable(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthTestEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthWriteEnable(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthWriteEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDescriptorBufferOffsets2EXT(VkCommandBuffer, const VkSetDescriptorBufferOffsetsInfoEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDescriptorBufferOffsetsEXT(VkCommandBuffer, VkPipelineBindPoint, VkPipelineLayout, uint32_t,
                                                                         uint32_t, const uint32_t *, const VkDeviceSize *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDeviceMask(VkCommandBuffer, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDeviceMaskKHR(VkCommandBuffer, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDiscardRectangleEXT(VkCommandBuffer, uint32_t, uint32_t, const VkRect2D *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDiscardRectangleEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDiscardRectangleModeEXT(VkCommandBuffer, VkDiscardRectangleModeEXT) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetEvent(VkCommandBuffer, VkEvent, VkPipelineStageFlags) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetEvent2(VkCommandBuffer, VkEvent, const VkDependencyInfo *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetEvent2KHR(VkCommandBuffer, VkEvent, const VkDependencyInfo *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetExclusiveScissorEnableNV(VkCommandBuffer, uint32_t, uint32_t, const VkBool32 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetExclusiveScissorNV(VkCommandBuffer, uint32_t, uint32_t, const VkRect2D *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetExtraPrimitiveOverestimationSizeEXT(VkCommandBuffer, float) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetFragmentShadingRateEnumNV(VkCommandBuffer, VkFragmentShadingRateNV,
                                                                        const VkFragmentShadingRateCombinerOpKHR *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetFragmentShadingRateKHR(VkCommandBuffer, const VkExtent2D *,
                                                                     const VkFragmentShadingRateCombinerOpKHR *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetFrontFace(VkCommandBuffer, VkFrontFace) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetFrontFaceEXT(VkCommandBuffer, VkFrontFace) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetLineRasterizationModeEXT(VkCommandBuffer, VkLineRasterizationModeEXT) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetLineStipple(VkCommandBuffer, uint32_t, uint16_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetLineStippleEXT(VkCommandBuffer, uint32_t, uint16_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetLineStippleEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetLineStippleKHR(VkCommandBuffer, uint32_t, uint16_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetLineWidth(VkCommandBuffer, float) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetLogicOpEXT(VkCommandBuffer, VkLogicOp) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetLogicOpEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetPatchControlPointsEXT(VkCommandBuffer, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCmdSetPerformanceMarkerINTEL(VkCommandBuffer, const VkPerformanceMarkerInfoINTEL *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCmdSetPerformanceOverrideINTEL(VkCommandBuffer, const VkPerformanceOverrideInfoINTEL *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCmdSetPerformanceStreamMarkerINTEL(VkCommandBuffer,
                                                                               const VkPerformanceStreamMarkerInfoINTEL *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetPolygonModeEXT(VkCommandBuffer, VkPolygonMode) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetPrimitiveRestartEnable(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetPrimitiveRestartEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetPrimitiveTopology(VkCommandBuffer, VkPrimitiveTopology) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetPrimitiveTopologyEXT(VkCommandBuffer, VkPrimitiveTopology) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetProvokingVertexModeEXT(VkCommandBuffer, VkProvokingVertexModeEXT) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetRasterizationSamplesEXT(VkCommandBuffer, VkSampleCountFlagBits) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetRasterizationStreamEXT(VkCommandBuffer, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetRasterizerDiscardEnable(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetRasterizerDiscardEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetRayTracingPipelineStackSizeKHR(VkCommandBuffer, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetRenderingAttachmentLocations(VkCommandBuffer, const VkRenderingAttachmentLocationInfo *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetRenderingAttachmentLocationsKHR(VkCommandBuffer, const VkRenderingAttachmentLocationInfo *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetRenderingInputAttachmentIndices(VkCommandBuffer,
                                                                              const VkRenderingInputAttachmentIndexInfo *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetRenderingInputAttachmentIndicesKHR(VkCommandBuffer,
                                                                                 const VkRenderingInputAttachmentIndexInfo *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetRepresentativeFragmentTestEnableNV(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetSampleLocationsEXT(VkCommandBuffer, const VkSampleLocationsInfoEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetSampleLocationsEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetSampleMaskEXT(VkCommandBuffer, VkSampleCountFlagBits, const VkSampleMask *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetScissor(VkCommandBuffer, uint32_t, uint32_t, const VkRect2D *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetScissorWithCount(VkCommandBuffer, uint32_t, const VkRect2D *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetScissorWithCountEXT(VkCommandBuffer, uint32_t, const VkRect2D *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetShadingRateImageEnableNV(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetStencilCompareMask(VkCommandBuffer, VkStencilFaceFlags, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetStencilOp(VkCommandBuffer, VkStencilFaceFlags, VkStencilOp, VkStencilOp, VkStencilOp,
                                                        VkCompareOp) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetStencilOpEXT(VkCommandBuffer, VkStencilFaceFlags, VkStencilOp, VkStencilOp, VkStencilOp,
                                                           VkCompareOp) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetStencilReference(VkCommandBuffer, VkStencilFaceFlags, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetStencilTestEnable(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetStencilTestEnableEXT(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetStencilWriteMask(VkCommandBuffer, VkStencilFaceFlags, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetTessellationDomainOriginEXT(VkCommandBuffer, VkTessellationDomainOrigin) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetVertexInputEXT(VkCommandBuffer, uint32_t, const VkVertexInputBindingDescription2EXT *,
                                                             uint32_t, const VkVertexInputAttributeDescription2EXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetViewport(VkCommandBuffer, uint32_t, uint32_t, const VkViewport *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetViewportShadingRatePaletteNV(VkCommandBuffer, uint32_t, uint32_t,
                                                                           const VkShadingRatePaletteNV *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetViewportSwizzleNV(VkCommandBuffer, uint32_t, uint32_t, const VkViewportSwizzleNV *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetViewportWScalingEnableNV(VkCommandBuffer, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetViewportWScalingNV(VkCommandBuffer, uint32_t, uint32_t, const VkViewportWScalingNV *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetViewportWithCount(VkCommandBuffer, uint32_t, const VkViewport *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetViewportWithCountEXT(VkCommandBuffer, uint32_t, const VkViewport *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSubpassShadingHUAWEI(VkCommandBuffer) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdTraceRaysIndirect2KHR(VkCommandBuffer, VkDeviceAddress) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdTraceRaysIndirectKHR(VkCommandBuffer, const VkStridedDeviceAddressRegionKHR *,
                                                                const VkStridedDeviceAddressRegionKHR *,
                                                                const VkStridedDeviceAddressRegionKHR *,
                                                                const VkStridedDeviceAddressRegionKHR *, VkDeviceAddress) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdTraceRaysKHR(VkCommandBuffer, const VkStridedDeviceAddressRegionKHR *,
                                                        const VkStridedDeviceAddressRegionKHR *, const VkStridedDeviceAddressRegionKHR *,
                                                        const VkStridedDeviceAddressRegionKHR *, uint32_t, uint32_t, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdTraceRaysNV(VkCommandBuffer, VkBuffer, VkDeviceSize, VkBuffer, VkDeviceSize, VkDeviceSize,
                                                       VkBuffer, VkDeviceSize, VkDeviceSize, VkBuffer, VkDeviceSize, VkDeviceSize, uint32_t,
                                                       uint32_t, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdUpdateBuffer(VkCommandBuffer, VkBuffer, VkDeviceSize, VkDeviceSize, const void *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdUpdatePipelineIndirectBufferNV(VkCommandBuffer, VkPipelineBindPoint, VkPipeline) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdWaitEvents(VkCommandBuffer, uint32_t, const VkEvent *, VkPipelineStageFlags,
                                                      VkPipelineStageFlags, uint32_t, const VkMemoryBarrier *, uint32_t,
                                                      const VkBufferMemoryBarrier *, uint32_t, const VkImageMemoryBarrier *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdWaitEvents2(VkCommandBuffer, uint32_t, const VkEvent *, const VkDependencyInfo *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdWaitEvents2KHR(VkCommandBuffer, uint32_t, const VkEvent *, const VkDependencyInfo *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdWriteAccelerationStructuresPropertiesKHR(VkCommandBuffer, uint32_t,
                                                                                    const VkAccelerationStructureKHR *, VkQueryType,
                                                                                    VkQueryPool, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdWriteAccelerationStructuresPropertiesNV(VkCommandBuffer, uint32_t,
                                                                                   const VkAccelerationStructureNV *, VkQueryType,
                                                                                   VkQueryPool, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdWriteBufferMarker2AMD(VkCommandBuffer, VkPipelineStageFlags2, VkBuffer, VkDeviceSize, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdWriteBufferMarkerAMD(VkCommandBuffer, VkPipelineStageFlagBits, VkBuffer, VkDeviceSize,
                                                                uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdWriteMicromapsPropertiesEXT(VkCommandBuffer, uint32_t, const VkMicromapEXT *, VkQueryType,
                                                                       VkQueryPool, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdWriteTimestamp(VkCommandBuffer, VkPipelineStageFlagBits, VkQueryPool, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdWriteTimestamp2(VkCommandBuffer, VkPipelineStageFlags2, VkQueryPool, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdWriteTimestamp2KHR(VkCommandBuffer, VkPipelineStageFlags2, VkQueryPool, uint32_t) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCompileDeferredNV(VkDevice, VkPipeline, uint32_t) { return VK_ERROR_FEATURE_NOT_PRESENT; }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkConvertCooperativeVectorMatrixNV(VkDevice, const VkConvertCooperativeVectorMatrixInfoNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCopyAccelerationStructureKHR(VkDevice, VkDeferredOperationKHR,
                                                                         const VkCopyAccelerationStructureInfoKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCopyAccelerationStructureToMemoryKHR(VkDevice, VkDeferredOperationKHR,
                                                                                 const VkCopyAccelerationStructureToMemoryInfoKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCopyImageToImage(VkDevice, const VkCopyImageToImageInfo *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCopyImageToImageEXT(VkDevice, const VkCopyImageToImageInfo *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCopyImageToMemory(VkDevice, const VkCopyImageToMemoryInfo *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCopyImageToMemoryEXT(VkDevice, const VkCopyImageToMemoryInfo *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCopyMemoryToAccelerationStructureKHR(VkDevice, VkDeferredOperationKHR,
                                                                                 const VkCopyMemoryToAccelerationStructureInfoKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCopyMemoryToImage(VkDevice, const VkCopyMemoryToImageInfo *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCopyMemoryToImageEXT(VkDevice, const VkCopyMemoryToImageInfo *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCopyMemoryToMicromapEXT(VkDevice, VkDeferredOperationKHR,
                                                                    const VkCopyMemoryToMicromapInfoEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCopyMicromapEXT(VkDevice, VkDeferredOperationKHR, const VkCopyMicromapInfoEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCopyMicromapToMemoryEXT(VkDevice, VkDeferredOperationKHR,
                                                                    const VkCopyMicromapToMemoryInfoEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateAccelerationStructureKHR(VkDevice, const VkAccelerationStructureCreateInfoKHR *,
                                                                           const VkAllocationCallbacks *, VkAccelerationStructureKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateAccelerationStructureNV(VkDevice, const VkAccelerationStructureCreateInfoNV *,
                                                                          const VkAllocationCallbacks *, VkAccelerationStructureNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateBufferView(VkDevice, const VkBufferViewCreateInfo *, const VkAllocationCallbacks *,
                                                             VkBufferView *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateCuFunctionNVX(VkDevice, const VkCuFunctionCreateInfoNVX *, const VkAllocationCallbacks *,
                                                                VkCuFunctionNVX *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateCuModuleNVX(VkDevice, const VkCuModuleCreateInfoNVX *, const VkAllocationCallbacks *,
                                                              VkCuModuleNVX *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDataGraphPipelineSessionARM(VkDevice, const VkDataGraphPipelineSessionCreateInfoARM *,
                                                                              const VkAllocationCallbacks *,
                                                                              VkDataGraphPipelineSessionARM *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDataGraphPipelinesARM(VkDevice, VkDeferredOperationKHR, VkPipelineCache, uint32_t,
                                                                        const VkDataGraphPipelineCreateInfoARM *,
                                                                        const VkAllocationCallbacks *, VkPipeline *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDebugReportCallbackEXT(VkInstance, const VkDebugReportCallbackCreateInfoEXT *,
                                                                         const VkAllocationCallbacks *, VkDebugReportCallbackEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDebugUtilsMessengerEXT(VkInstance, const VkDebugUtilsMessengerCreateInfoEXT *,
                                                                         const VkAllocationCallbacks *, VkDebugUtilsMessengerEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDeferredOperationKHR(VkDevice, const VkAllocationCallbacks *, VkDeferredOperationKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDescriptorUpdateTemplate(VkDevice, const VkDescriptorUpdateTemplateCreateInfo *,
                                                                           const VkAllocationCallbacks *, VkDescriptorUpdateTemplate *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDescriptorUpdateTemplateKHR(VkDevice, const VkDescriptorUpdateTemplateCreateInfo *,
                                                                              const VkAllocationCallbacks *, VkDescriptorUpdateTemplate *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDisplayModeKHR(VkPhysicalDevice, VkDisplayKHR, const VkDisplayModeCreateInfoKHR *,
                                                                 const VkAllocationCallbacks *, VkDisplayModeKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDisplayPlaneSurfaceKHR(VkInstance, const VkDisplaySurfaceCreateInfoKHR *,
                                                                         const VkAllocationCallbacks *, VkSurfaceKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateEvent(VkDevice, const VkEventCreateInfo *, const VkAllocationCallbacks *, VkEvent *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateExternalComputeQueueNV(VkDevice, const VkExternalComputeQueueCreateInfoNV *,
                                                                         const VkAllocationCallbacks *, VkExternalComputeQueueNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateFramebuffer(VkDevice, const VkFramebufferCreateInfo *, const VkAllocationCallbacks *,
                                                              VkFramebuffer *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateGraphicsPipelines(VkDevice, VkPipelineCache, uint32_t,
                                                                    const VkGraphicsPipelineCreateInfo *, const VkAllocationCallbacks *,
                                                                    VkPipeline *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateHeadlessSurfaceEXT(VkInstance, const VkHeadlessSurfaceCreateInfoEXT *,
                                                                     const VkAllocationCallbacks *, VkSurfaceKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateImage(VkDevice, const VkImageCreateInfo *, const VkAllocationCallbacks *, VkImage *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateImageView(VkDevice, const VkImageViewCreateInfo *, const VkAllocationCallbacks *,
                                                            VkImageView *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateIndirectCommandsLayoutEXT(VkDevice, const VkIndirectCommandsLayoutCreateInfoEXT *,
                                                                            const VkAllocationCallbacks *, VkIndirectCommandsLayoutEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateIndirectCommandsLayoutNV(VkDevice, const VkIndirectCommandsLayoutCreateInfoNV *,
                                                                           const VkAllocationCallbacks *, VkIndirectCommandsLayoutNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateIndirectExecutionSetEXT(VkDevice, const VkIndirectExecutionSetCreateInfoEXT *,
                                                                          const VkAllocationCallbacks *, VkIndirectExecutionSetEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateMicromapEXT(VkDevice, const VkMicromapCreateInfoEXT *, const VkAllocationCallbacks *,
                                                              VkMicromapEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateOpticalFlowSessionNV(VkDevice, const VkOpticalFlowSessionCreateInfoNV *,
                                                                       const VkAllocationCallbacks *, VkOpticalFlowSessionNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreatePipelineBinariesKHR(VkDevice, const VkPipelineBinaryCreateInfoKHR *,
                                                                      const VkAllocationCallbacks *, VkPipelineBinaryHandlesInfoKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreatePrivateDataSlot(VkDevice, const VkPrivateDataSlotCreateInfo *,
                                                                  const VkAllocationCallbacks *, VkPrivateDataSlot *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreatePrivateDataSlotEXT(VkDevice, const VkPrivateDataSlotCreateInfo *,
                                                                     const VkAllocationCallbacks *, VkPrivateDataSlot *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateQueryPool(VkDevice, const VkQueryPoolCreateInfo *, const VkAllocationCallbacks *,
                                                            VkQueryPool *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateRayTracingPipelinesKHR(VkDevice, VkDeferredOperationKHR, VkPipelineCache, uint32_t,
                                                                         const VkRayTracingPipelineCreateInfoKHR *,
                                                                         const VkAllocationCallbacks *, VkPipeline *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateRayTracingPipelinesNV(VkDevice, VkPipelineCache, uint32_t,
                                                                        const VkRayTracingPipelineCreateInfoNV *,
                                                                        const VkAllocationCallbacks *, VkPipeline *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateRenderPass(VkDevice, const VkRenderPassCreateInfo *, const VkAllocationCallbacks *,
                                                             VkRenderPass *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateRenderPass2(VkDevice, const VkRenderPassCreateInfo2 *, const VkAllocationCallbacks *,
                                                              VkRenderPass *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateRenderPass2KHR(VkDevice, const VkRenderPassCreateInfo2 *, const VkAllocationCallbacks *,
                                                                 VkRenderPass *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateSampler(VkDevice, const VkSamplerCreateInfo *, const VkAllocationCallbacks *,
                                                          VkSampler *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateSamplerYcbcrConversion(VkDevice, const VkSamplerYcbcrConversionCreateInfo *,
                                                                         const VkAllocationCallbacks *, VkSamplerYcbcrConversion *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateSamplerYcbcrConversionKHR(VkDevice, const VkSamplerYcbcrConversionCreateInfo *,
                                                                            const VkAllocationCallbacks *, VkSamplerYcbcrConversion *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateSemaphore(VkDevice, const VkSemaphoreCreateInfo *, const VkAllocationCallbacks *,
                                                            VkSemaphore *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateShadersEXT(VkDevice, uint32_t, const VkShaderCreateInfoEXT *,
                                                             const VkAllocationCallbacks *, VkShaderEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateSharedSwapchainsKHR(VkDevice, uint32_t, const VkSwapchainCreateInfoKHR *,
                                                                      const VkAllocationCallbacks *, VkSwapchainKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateSwapchainKHR(VkDevice, const VkSwapchainCreateInfoKHR *, const VkAllocationCallbacks *,
                                                               VkSwapchainKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateTensorARM(VkDevice, const VkTensorCreateInfoARM *, const VkAllocationCallbacks *,
                                                            VkTensorARM *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateTensorViewARM(VkDevice, const VkTensorViewCreateInfoARM *, const VkAllocationCallbacks *,
                                                                VkTensorViewARM *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateValidationCacheEXT(VkDevice, const VkValidationCacheCreateInfoEXT *,
                                                                     const VkAllocationCallbacks *, VkValidationCacheEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateVideoSessionKHR(VkDevice, const VkVideoSessionCreateInfoKHR *,
                                                                  const VkAllocationCallbacks *, VkVideoSessionKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateVideoSessionParametersKHR(VkDevice, const VkVideoSessionParametersCreateInfoKHR *,
                                                                            const VkAllocationCallbacks *, VkVideoSessionParametersKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkDebugMarkerSetObjectNameEXT(VkDevice, const VkDebugMarkerObjectNameInfoEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkDebugMarkerSetObjectTagEXT(VkDevice, const VkDebugMarkerObjectTagInfoEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDebugReportMessageEXT(VkInstance, VkDebugReportFlagsEXT, VkDebugReportObjectTypeEXT, uint64_t,
                                                              size_t, int32_t, const char *, const char *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkDeferredOperationJoinKHR(VkDevice, VkDeferredOperationKHR) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyAccelerationStructureKHR(VkDevice, VkAccelerationStructureKHR,
                                                                        const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyAccelerationStructureNV(VkDevice, VkAccelerationStructureNV, const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyBufferView(VkDevice, VkBufferView, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyCuFunctionNVX(VkDevice, VkCuFunctionNVX, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyCuModuleNVX(VkDevice, VkCuModuleNVX, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDataGraphPipelineSessionARM(VkDevice, VkDataGraphPipelineSessionARM,
                                                                           const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDebugReportCallbackEXT(VkInstance, VkDebugReportCallbackEXT, const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDebugUtilsMessengerEXT(VkInstance, VkDebugUtilsMessengerEXT, const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDeferredOperationKHR(VkDevice, VkDeferredOperationKHR, const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDescriptorUpdateTemplate(VkDevice, VkDescriptorUpdateTemplate,
                                                                        const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDescriptorUpdateTemplateKHR(VkDevice, VkDescriptorUpdateTemplate,
                                                                           const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyEvent(VkDevice, VkEvent, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyExternalComputeQueueNV(VkDevice, VkExternalComputeQueueNV, const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyFramebuffer(VkDevice, VkFramebuffer, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyImage(VkDevice, VkImage, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyImageView(VkDevice, VkImageView, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyIndirectCommandsLayoutEXT(VkDevice, VkIndirectCommandsLayoutEXT,
                                                                         const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyIndirectCommandsLayoutNV(VkDevice, VkIndirectCommandsLayoutNV,
                                                                        const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyIndirectExecutionSetEXT(VkDevice, VkIndirectExecutionSetEXT, const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyMicromapEXT(VkDevice, VkMicromapEXT, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyOpticalFlowSessionNV(VkDevice, VkOpticalFlowSessionNV, const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyPipelineBinaryKHR(VkDevice, VkPipelineBinaryKHR, const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyPrivateDataSlot(VkDevice, VkPrivateDataSlot, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyPrivateDataSlotEXT(VkDevice, VkPrivateDataSlot, const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyQueryPool(VkDevice, VkQueryPool, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyRenderPass(VkDevice, VkRenderPass, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroySampler(VkDevice, VkSampler, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroySamplerYcbcrConversion(VkDevice, VkSamplerYcbcrConversion, const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroySamplerYcbcrConversionKHR(VkDevice, VkSamplerYcbcrConversion,
                                                                         const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroySemaphore(VkDevice, VkSemaphore, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyShaderEXT(VkDevice, VkShaderEXT, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroySurfaceKHR(VkInstance, VkSurfaceKHR, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroySwapchainKHR(VkDevice, VkSwapchainKHR, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyTensorARM(VkDevice, VkTensorARM, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyTensorViewARM(VkDevice, VkTensorViewARM, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyValidationCacheEXT(VkDevice, VkValidationCacheEXT, const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyVideoSessionKHR(VkDevice, VkVideoSessionKHR, const VkAllocationCallbacks *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyVideoSessionParametersKHR(VkDevice, VkVideoSessionParametersKHR,
                                                                         const VkAllocationCallbacks *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkDisplayPowerControlEXT(VkDevice, VkDisplayKHR, const VkDisplayPowerInfoEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumerateDeviceLayerProperties(VkPhysicalDevice, uint32_t *, VkLayerProperties *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumeratePhysicalDeviceGroups(VkInstance, uint32_t *, VkPhysicalDeviceGroupProperties *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumeratePhysicalDeviceGroupsKHR(VkInstance, uint32_t *, VkPhysicalDeviceGroupProperties *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumeratePhysicalDeviceQueueFamilyPerformanceCountersByRegionARM(
    VkPhysicalDevice, uint32_t, uint32_t *, VkPerformanceCounterARM *, VkPerformanceCounterDescriptionARM *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumeratePhysicalDeviceQueueFamilyPerformanceQueryCountersKHR(
    VkPhysicalDevice, uint32_t, uint32_t *, VkPerformanceCounterKHR *, VkPerformanceCounterDescriptionKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetAccelerationStructureBuildSizesKHR(VkDevice, VkAccelerationStructureBuildTypeKHR,
                                                                              const VkAccelerationStructureBuildGeometryInfoKHR *,
                                                                              const uint32_t *,
                                                                              VkAccelerationStructureBuildSizesInfoKHR *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkDeviceAddress VKAPI_CALL
vkGetAccelerationStructureDeviceAddressKHR(VkDevice, const VkAccelerationStructureDeviceAddressInfoKHR *) {
  return 0;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetAccelerationStructureHandleNV(VkDevice, VkAccelerationStructureNV, size_t, void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetAccelerationStructureMemoryRequirementsNV(
    VkDevice, const VkAccelerationStructureMemoryRequirementsInfoNV *, VkMemoryRequirements2KHR *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL
vkGetAccelerationStructureOpaqueCaptureDescriptorDataEXT(VkDevice, const VkAccelerationStructureCaptureDescriptorDataInfoEXT *, void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkDeviceAddress VKAPI_CALL vkGetBufferDeviceAddress(VkDevice, const VkBufferDeviceAddressInfo *) { return 0; }
extern "C" VKAPI_ATTR VkDeviceAddress VKAPI_CALL vkGetBufferDeviceAddressEXT(VkDevice, const VkBufferDeviceAddressInfo *) { return 0; }
extern "C" VKAPI_ATTR VkDeviceAddress VKAPI_CALL vkGetBufferDeviceAddressKHR(VkDevice, const VkBufferDeviceAddressInfo *) { return 0; }
extern "C" VKAPI_ATTR uint64_t VKAPI_CALL vkGetBufferOpaqueCaptureAddress(VkDevice, const VkBufferDeviceAddressInfo *) { return 0; }
extern "C" VKAPI_ATTR uint64_t VKAPI_CALL vkGetBufferOpaqueCaptureAddressKHR(VkDevice, const VkBufferDeviceAddressInfo *) { return 0; }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetBufferOpaqueCaptureDescriptorDataEXT(VkDevice, const VkBufferCaptureDescriptorDataInfoEXT *,
                                                                                    void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetCalibratedTimestampsEXT(VkDevice, uint32_t, const VkCalibratedTimestampInfoKHR *, uint64_t *,
                                                                       uint64_t *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetCalibratedTimestampsKHR(VkDevice, uint32_t, const VkCalibratedTimestampInfoKHR *, uint64_t *,
                                                                       uint64_t *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetClusterAccelerationStructureBuildSizesNV(VkDevice,
                                                                                    const VkClusterAccelerationStructureInputInfoNV *,
                                                                                    VkAccelerationStructureBuildSizesInfoKHR *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetDataGraphPipelineAvailablePropertiesARM(VkDevice, const VkDataGraphPipelineInfoARM *,
                                                                                       uint32_t *, VkDataGraphPipelinePropertyARM *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetDataGraphPipelinePropertiesARM(VkDevice, const VkDataGraphPipelineInfoARM *, uint32_t,
                                                                              VkDataGraphPipelinePropertyQueryResultARM *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL
vkGetDataGraphPipelineSessionBindPointRequirementsARM(VkDevice, const VkDataGraphPipelineSessionBindPointRequirementsInfoARM *, uint32_t *,
                                                      VkDataGraphPipelineSessionBindPointRequirementARM *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDataGraphPipelineSessionMemoryRequirementsARM(
    VkDevice, const VkDataGraphPipelineSessionMemoryRequirementsInfoARM *, VkMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR uint32_t VKAPI_CALL vkGetDeferredOperationMaxConcurrencyKHR(VkDevice, VkDeferredOperationKHR) { return 0; }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetDeferredOperationResultKHR(VkDevice, VkDeferredOperationKHR) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDescriptorEXT(VkDevice, const VkDescriptorGetInfoEXT *, size_t, void *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDescriptorSetHostMappingVALVE(VkDevice, VkDescriptorSet, void **) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDescriptorSetLayoutBindingOffsetEXT(VkDevice, VkDescriptorSetLayout, uint32_t, VkDeviceSize *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDescriptorSetLayoutHostMappingInfoVALVE(VkDevice, const VkDescriptorSetBindingReferenceVALVE *,
                                                                                   VkDescriptorSetLayoutHostMappingInfoVALVE *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDescriptorSetLayoutSizeEXT(VkDevice, VkDescriptorSetLayout, VkDeviceSize *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDescriptorSetLayoutSupport(VkDevice, const VkDescriptorSetLayoutCreateInfo *,
                                                                      VkDescriptorSetLayoutSupport *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDescriptorSetLayoutSupportKHR(VkDevice, const VkDescriptorSetLayoutCreateInfo *,
                                                                         VkDescriptorSetLayoutSupport *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceAccelerationStructureCompatibilityKHR(VkDevice,
                                                                                       const VkAccelerationStructureVersionInfoKHR *,
                                                                                       VkAccelerationStructureCompatibilityKHR *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceBufferMemoryRequirements(VkDevice, const VkDeviceBufferMemoryRequirements *,
                                                                          VkMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceBufferMemoryRequirementsKHR(VkDevice, const VkDeviceBufferMemoryRequirements *,
                                                                             VkMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR uint64_t VKAPI_CALL vkGetDeviceCombinedImageSamplerIndexNVX(VkDevice, uint64_t, uint64_t) { return 0; }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetDeviceFaultInfoEXT(VkDevice, VkDeviceFaultCountsEXT *, VkDeviceFaultInfoEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceGroupPeerMemoryFeatures(VkDevice, uint32_t, uint32_t, uint32_t,
                                                                         VkPeerMemoryFeatureFlags *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceGroupPeerMemoryFeaturesKHR(VkDevice, uint32_t, uint32_t, uint32_t,
                                                                            VkPeerMemoryFeatureFlags *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetDeviceGroupPresentCapabilitiesKHR(VkDevice, VkDeviceGroupPresentCapabilitiesKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetDeviceGroupSurfacePresentModesKHR(VkDevice, VkSurfaceKHR,
                                                                                 VkDeviceGroupPresentModeFlagsKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceImageMemoryRequirements(VkDevice, const VkDeviceImageMemoryRequirements *,
                                                                         VkMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceImageMemoryRequirementsKHR(VkDevice, const VkDeviceImageMemoryRequirements *,
                                                                            VkMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceImageSparseMemoryRequirements(VkDevice, const VkDeviceImageMemoryRequirements *,
                                                                               uint32_t *, VkSparseImageMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceImageSparseMemoryRequirementsKHR(VkDevice, const VkDeviceImageMemoryRequirements *,
                                                                                  uint32_t *, VkSparseImageMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceImageSubresourceLayout(VkDevice, const VkDeviceImageSubresourceInfo *,
                                                                        VkSubresourceLayout2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceImageSubresourceLayoutKHR(VkDevice, const VkDeviceImageSubresourceInfo *,
                                                                           VkSubresourceLayout2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceMemoryCommitment(VkDevice, VkDeviceMemory, VkDeviceSize *) { std::abort(); }
extern "C" VKAPI_ATTR uint64_t VKAPI_CALL vkGetDeviceMemoryOpaqueCaptureAddress(VkDevice, const VkDeviceMemoryOpaqueCaptureAddressInfo *) {
  return 0;
}
extern "C" VKAPI_ATTR uint64_t VKAPI_CALL vkGetDeviceMemoryOpaqueCaptureAddressKHR(VkDevice,
                                                                                   const VkDeviceMemoryOpaqueCaptureAddressInfo *) {
  return 0;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceMicromapCompatibilityEXT(VkDevice, const VkMicromapVersionInfoEXT *,
                                                                          VkAccelerationStructureCompatibilityKHR *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetDeviceSubpassShadingMaxWorkgroupSizeHUAWEI(VkDevice, VkRenderPass, VkExtent2D *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceTensorMemoryRequirementsARM(VkDevice, const VkDeviceTensorMemoryRequirementsARM *,
                                                                             VkMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetDisplayModeProperties2KHR(VkPhysicalDevice, VkDisplayKHR, uint32_t *,
                                                                         VkDisplayModeProperties2KHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetDisplayModePropertiesKHR(VkPhysicalDevice, VkDisplayKHR, uint32_t *,
                                                                        VkDisplayModePropertiesKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetDisplayPlaneCapabilities2KHR(VkPhysicalDevice, const VkDisplayPlaneInfo2KHR *,
                                                                            VkDisplayPlaneCapabilities2KHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetDisplayPlaneCapabilitiesKHR(VkPhysicalDevice, VkDisplayModeKHR, uint32_t,
                                                                           VkDisplayPlaneCapabilitiesKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetDisplayPlaneSupportedDisplaysKHR(VkPhysicalDevice, uint32_t, uint32_t *, VkDisplayKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetDrmDisplayEXT(VkPhysicalDevice, int32_t, uint32_t, VkDisplayKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetDynamicRenderingTilePropertiesQCOM(VkDevice, const VkRenderingInfo *,
                                                                                  VkTilePropertiesQCOM *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetEncodedVideoSessionParametersKHR(VkDevice, const VkVideoEncodeSessionParametersGetInfoKHR *,
                                                                                VkVideoEncodeSessionParametersFeedbackInfoKHR *, size_t *,
                                                                                void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetEventStatus(VkDevice, VkEvent) { return VK_ERROR_FEATURE_NOT_PRESENT; }
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetExternalComputeQueueDataNV(VkExternalComputeQueueNV, VkExternalComputeQueueDataParamsNV *,
                                                                      void *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetFenceFdKHR(VkDevice, const VkFenceGetFdInfoKHR *, int *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetFramebufferTilePropertiesQCOM(VkDevice, VkFramebuffer, uint32_t *, VkTilePropertiesQCOM *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetGeneratedCommandsMemoryRequirementsEXT(VkDevice,
                                                                                  const VkGeneratedCommandsMemoryRequirementsInfoEXT *,
                                                                                  VkMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetGeneratedCommandsMemoryRequirementsNV(VkDevice,
                                                                                 const VkGeneratedCommandsMemoryRequirementsInfoNV *,
                                                                                 VkMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetImageDrmFormatModifierPropertiesEXT(VkDevice, VkImage,
                                                                                   VkImageDrmFormatModifierPropertiesEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageMemoryRequirements(VkDevice, VkImage, VkMemoryRequirements *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageMemoryRequirements2(VkDevice, const VkImageMemoryRequirementsInfo2 *,
                                                                    VkMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageMemoryRequirements2KHR(VkDevice, const VkImageMemoryRequirementsInfo2 *,
                                                                       VkMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetImageOpaqueCaptureDataEXT(VkDevice, uint32_t, const VkImage *, VkHostAddressRangeEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetImageOpaqueCaptureDescriptorDataEXT(VkDevice, const VkImageCaptureDescriptorDataInfoEXT *,
                                                                                   void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageSparseMemoryRequirements(VkDevice, VkImage, uint32_t *, VkSparseImageMemoryRequirements *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageSparseMemoryRequirements2(VkDevice, const VkImageSparseMemoryRequirementsInfo2 *,
                                                                          uint32_t *, VkSparseImageMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageSparseMemoryRequirements2KHR(VkDevice, const VkImageSparseMemoryRequirementsInfo2 *,
                                                                             uint32_t *, VkSparseImageMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageSubresourceLayout(VkDevice, VkImage, const VkImageSubresource *, VkSubresourceLayout *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageSubresourceLayout2(VkDevice, VkImage, const VkImageSubresource2 *, VkSubresourceLayout2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageSubresourceLayout2EXT(VkDevice, VkImage, const VkImageSubresource2 *,
                                                                      VkSubresourceLayout2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageSubresourceLayout2KHR(VkDevice, VkImage, const VkImageSubresource2 *,
                                                                      VkSubresourceLayout2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetImageViewAddressNVX(VkDevice, VkImageView, VkImageViewAddressPropertiesNVX *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR uint64_t VKAPI_CALL vkGetImageViewHandle64NVX(VkDevice, const VkImageViewHandleInfoNVX *) { return 0; }
extern "C" VKAPI_ATTR uint32_t VKAPI_CALL vkGetImageViewHandleNVX(VkDevice, const VkImageViewHandleInfoNVX *) { return 0; }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetImageViewOpaqueCaptureDescriptorDataEXT(VkDevice,
                                                                                       const VkImageViewCaptureDescriptorDataInfoEXT *,
                                                                                       void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetLatencyTimingsNV(VkDevice, VkSwapchainKHR, VkGetLatencyMarkerInfoNV *) { std::abort(); }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetMemoryFdKHR(VkDevice, const VkMemoryGetFdInfoKHR *, int *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetMemoryFdPropertiesKHR(VkDevice, VkExternalMemoryHandleTypeFlagBits, int,
                                                                     VkMemoryFdPropertiesKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetMemoryHostPointerPropertiesEXT(VkDevice, VkExternalMemoryHandleTypeFlagBits, const void *,
                                                                              VkMemoryHostPointerPropertiesEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetMemoryRemoteAddressNV(VkDevice, const VkMemoryGetRemoteAddressInfoNV *,
                                                                     VkRemoteAddressNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetMicromapBuildSizesEXT(VkDevice, VkAccelerationStructureBuildTypeKHR,
                                                                 const VkMicromapBuildInfoEXT *, VkMicromapBuildSizesInfoEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPartitionedAccelerationStructuresBuildSizesNV(
    VkDevice, const VkPartitionedAccelerationStructureInstancesInputNV *, VkAccelerationStructureBuildSizesInfoKHR *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPastPresentationTimingEXT(VkDevice, const VkPastPresentationTimingInfoEXT *,
                                                                         VkPastPresentationTimingPropertiesEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPastPresentationTimingGOOGLE(VkDevice, VkSwapchainKHR, uint32_t *,
                                                                            VkPastPresentationTimingGOOGLE *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPerformanceParameterINTEL(VkDevice, VkPerformanceParameterTypeINTEL,
                                                                         VkPerformanceValueINTEL *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceCalibrateableTimeDomainsEXT(VkPhysicalDevice, uint32_t *, VkTimeDomainKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceCalibrateableTimeDomainsKHR(VkPhysicalDevice, uint32_t *, VkTimeDomainKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceCooperativeMatrixFlexibleDimensionsPropertiesNV(
    VkPhysicalDevice, uint32_t *, VkCooperativeMatrixFlexibleDimensionsPropertiesNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceCooperativeMatrixPropertiesKHR(VkPhysicalDevice, uint32_t *,
                                                                                            VkCooperativeMatrixPropertiesKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceCooperativeMatrixPropertiesNV(VkPhysicalDevice, uint32_t *,
                                                                                           VkCooperativeMatrixPropertiesNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceCooperativeVectorPropertiesNV(VkPhysicalDevice, uint32_t *,
                                                                                           VkCooperativeVectorPropertiesNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkDeviceSize VKAPI_CALL vkGetPhysicalDeviceDescriptorSizeEXT(VkPhysicalDevice, VkDescriptorType) { return 0; }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceDisplayPlaneProperties2KHR(VkPhysicalDevice, uint32_t *,
                                                                                        VkDisplayPlaneProperties2KHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceDisplayPlanePropertiesKHR(VkPhysicalDevice, uint32_t *,
                                                                                       VkDisplayPlanePropertiesKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceDisplayProperties2KHR(VkPhysicalDevice, uint32_t *,
                                                                                   VkDisplayProperties2KHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceDisplayPropertiesKHR(VkPhysicalDevice, uint32_t *, VkDisplayPropertiesKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceExternalBufferProperties(VkPhysicalDevice,
                                                                                  const VkPhysicalDeviceExternalBufferInfo *,
                                                                                  VkExternalBufferProperties *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceExternalBufferPropertiesKHR(VkPhysicalDevice,
                                                                                     const VkPhysicalDeviceExternalBufferInfo *,
                                                                                     VkExternalBufferProperties *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceExternalFenceProperties(VkPhysicalDevice,
                                                                                 const VkPhysicalDeviceExternalFenceInfo *,
                                                                                 VkExternalFenceProperties *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceExternalFencePropertiesKHR(VkPhysicalDevice,
                                                                                    const VkPhysicalDeviceExternalFenceInfo *,
                                                                                    VkExternalFenceProperties *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceExternalImageFormatPropertiesNV(VkPhysicalDevice, VkFormat, VkImageType,
                                                                                             VkImageTiling, VkImageUsageFlags,
                                                                                             VkImageCreateFlags,
                                                                                             VkExternalMemoryHandleTypeFlagsNV,
                                                                                             VkExternalImageFormatPropertiesNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceExternalSemaphoreProperties(VkPhysicalDevice,
                                                                                     const VkPhysicalDeviceExternalSemaphoreInfo *,
                                                                                     VkExternalSemaphoreProperties *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceExternalSemaphorePropertiesKHR(VkPhysicalDevice,
                                                                                        const VkPhysicalDeviceExternalSemaphoreInfo *,
                                                                                        VkExternalSemaphoreProperties *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceExternalTensorPropertiesARM(VkPhysicalDevice,
                                                                                     const VkPhysicalDeviceExternalTensorInfoARM *,
                                                                                     VkExternalTensorPropertiesARM *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceFormatProperties(VkPhysicalDevice, VkFormat, VkFormatProperties *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceFormatProperties2(VkPhysicalDevice, VkFormat, VkFormatProperties2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceFormatProperties2KHR(VkPhysicalDevice, VkFormat, VkFormatProperties2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceFragmentShadingRatesKHR(VkPhysicalDevice, uint32_t *,
                                                                                     VkPhysicalDeviceFragmentShadingRateKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceImageFormatProperties(VkPhysicalDevice, VkFormat, VkImageType, VkImageTiling,
                                                                                   VkImageUsageFlags, VkImageCreateFlags,
                                                                                   VkImageFormatProperties *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceImageFormatProperties2(VkPhysicalDevice,
                                                                                    const VkPhysicalDeviceImageFormatInfo2 *,
                                                                                    VkImageFormatProperties2 *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceImageFormatProperties2KHR(VkPhysicalDevice,
                                                                                       const VkPhysicalDeviceImageFormatInfo2 *,
                                                                                       VkImageFormatProperties2 *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceMultisamplePropertiesEXT(VkPhysicalDevice, VkSampleCountFlagBits,
                                                                                  VkMultisamplePropertiesEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceOpticalFlowImageFormatsNV(VkPhysicalDevice,
                                                                                       const VkOpticalFlowImageFormatInfoNV *, uint32_t *,
                                                                                       VkOpticalFlowImageFormatPropertiesNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDevicePresentRectanglesKHR(VkPhysicalDevice, VkSurfaceKHR, uint32_t *, VkRect2D *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceQueueFamilyDataGraphProcessingEnginePropertiesARM(
    VkPhysicalDevice, const VkPhysicalDeviceQueueFamilyDataGraphProcessingEngineInfoARM *,
    VkQueueFamilyDataGraphProcessingEnginePropertiesARM *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceQueueFamilyDataGraphPropertiesARM(VkPhysicalDevice, uint32_t, uint32_t *,
                                                                                               VkQueueFamilyDataGraphPropertiesARM *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceQueueFamilyPerformanceQueryPassesKHR(VkPhysicalDevice,
                                                                                              const VkQueryPoolPerformanceCreateInfoKHR *,
                                                                                              uint32_t *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceQueueFamilyProperties2(VkPhysicalDevice, uint32_t *, VkQueueFamilyProperties2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceQueueFamilyProperties2KHR(VkPhysicalDevice, uint32_t *,
                                                                                   VkQueueFamilyProperties2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceSparseImageFormatProperties(VkPhysicalDevice, VkFormat, VkImageType,
                                                                                     VkSampleCountFlagBits, VkImageUsageFlags,
                                                                                     VkImageTiling, uint32_t *,
                                                                                     VkSparseImageFormatProperties *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceSparseImageFormatProperties2(VkPhysicalDevice,
                                                                                      const VkPhysicalDeviceSparseImageFormatInfo2 *,
                                                                                      uint32_t *, VkSparseImageFormatProperties2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceSparseImageFormatProperties2KHR(VkPhysicalDevice,
                                                                                         const VkPhysicalDeviceSparseImageFormatInfo2 *,
                                                                                         uint32_t *, VkSparseImageFormatProperties2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL
vkGetPhysicalDeviceSupportedFramebufferMixedSamplesCombinationsNV(VkPhysicalDevice, uint32_t *, VkFramebufferMixedSamplesCombinationNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceSurfaceCapabilities2EXT(VkPhysicalDevice, VkSurfaceKHR,
                                                                                     VkSurfaceCapabilities2EXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceSurfaceCapabilities2KHR(VkPhysicalDevice,
                                                                                     const VkPhysicalDeviceSurfaceInfo2KHR *,
                                                                                     VkSurfaceCapabilities2KHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceSurfaceCapabilitiesKHR(VkPhysicalDevice, VkSurfaceKHR,
                                                                                    VkSurfaceCapabilitiesKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceSurfaceFormats2KHR(VkPhysicalDevice, const VkPhysicalDeviceSurfaceInfo2KHR *,
                                                                                uint32_t *, VkSurfaceFormat2KHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceSurfaceFormatsKHR(VkPhysicalDevice, VkSurfaceKHR, uint32_t *,
                                                                               VkSurfaceFormatKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceSurfacePresentModesKHR(VkPhysicalDevice, VkSurfaceKHR, uint32_t *,
                                                                                    VkPresentModeKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceSurfaceSupportKHR(VkPhysicalDevice, uint32_t, VkSurfaceKHR, VkBool32 *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceToolProperties(VkPhysicalDevice, uint32_t *,
                                                                            VkPhysicalDeviceToolProperties *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceToolPropertiesEXT(VkPhysicalDevice, uint32_t *,
                                                                               VkPhysicalDeviceToolProperties *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceVideoCapabilitiesKHR(VkPhysicalDevice, const VkVideoProfileInfoKHR *,
                                                                                  VkVideoCapabilitiesKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceVideoEncodeQualityLevelPropertiesKHR(
    VkPhysicalDevice, const VkPhysicalDeviceVideoEncodeQualityLevelInfoKHR *, VkVideoEncodeQualityLevelPropertiesKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceVideoFormatPropertiesKHR(VkPhysicalDevice,
                                                                                      const VkPhysicalDeviceVideoFormatInfoKHR *,
                                                                                      uint32_t *, VkVideoFormatPropertiesKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPipelineBinaryDataKHR(VkDevice, const VkPipelineBinaryDataInfoKHR *,
                                                                     VkPipelineBinaryKeyKHR *, size_t *, void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPipelineExecutableInternalRepresentationsKHR(
    VkDevice, const VkPipelineExecutableInfoKHR *, uint32_t *, VkPipelineExecutableInternalRepresentationKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPipelineExecutablePropertiesKHR(VkDevice, const VkPipelineInfoKHR *, uint32_t *,
                                                                               VkPipelineExecutablePropertiesKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPipelineExecutableStatisticsKHR(VkDevice, const VkPipelineExecutableInfoKHR *, uint32_t *,
                                                                               VkPipelineExecutableStatisticKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkDeviceAddress VKAPI_CALL vkGetPipelineIndirectDeviceAddressNV(VkDevice,
                                                                                      const VkPipelineIndirectDeviceAddressInfoNV *) {
  return 0;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPipelineIndirectMemoryRequirementsNV(VkDevice, const VkComputePipelineCreateInfo *,
                                                                                VkMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPipelineKeyKHR(VkDevice, const VkPipelineCreateInfoKHR *, VkPipelineBinaryKeyKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPipelinePropertiesEXT(VkDevice, const VkPipelineInfoEXT *, VkBaseOutStructure *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPrivateData(VkDevice, VkObjectType, uint64_t, VkPrivateDataSlot, uint64_t *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPrivateDataEXT(VkDevice, VkObjectType, uint64_t, VkPrivateDataSlot, uint64_t *) { std::abort(); }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetQueryPoolResults(VkDevice, VkQueryPool, uint32_t, uint32_t, size_t, void *, VkDeviceSize,
                                                                VkQueryResultFlags) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetQueueCheckpointData2NV(VkQueue, uint32_t *, VkCheckpointData2NV *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetQueueCheckpointDataNV(VkQueue, uint32_t *, VkCheckpointDataNV *) { std::abort(); }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetRayTracingCaptureReplayShaderGroupHandlesKHR(VkDevice, VkPipeline, uint32_t, uint32_t,
                                                                                            size_t, void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetRayTracingShaderGroupHandlesKHR(VkDevice, VkPipeline, uint32_t, uint32_t, size_t, void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetRayTracingShaderGroupHandlesNV(VkDevice, VkPipeline, uint32_t, uint32_t, size_t, void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkDeviceSize VKAPI_CALL vkGetRayTracingShaderGroupStackSizeKHR(VkDevice, VkPipeline, uint32_t,
                                                                                     VkShaderGroupShaderKHR) {
  return 0;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetRefreshCycleDurationGOOGLE(VkDevice, VkSwapchainKHR, VkRefreshCycleDurationGOOGLE *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetRenderAreaGranularity(VkDevice, VkRenderPass, VkExtent2D *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetRenderingAreaGranularity(VkDevice, const VkRenderingAreaInfo *, VkExtent2D *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetRenderingAreaGranularityKHR(VkDevice, const VkRenderingAreaInfo *, VkExtent2D *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetSamplerOpaqueCaptureDescriptorDataEXT(VkDevice,
                                                                                     const VkSamplerCaptureDescriptorDataInfoEXT *,
                                                                                     void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetSemaphoreCounterValue(VkDevice, VkSemaphore, uint64_t *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetSemaphoreCounterValueKHR(VkDevice, VkSemaphore, uint64_t *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetSemaphoreFdKHR(VkDevice, const VkSemaphoreGetFdInfoKHR *, int *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetShaderBinaryDataEXT(VkDevice, VkShaderEXT, size_t *, void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetShaderInfoAMD(VkDevice, VkPipeline, VkShaderStageFlagBits, VkShaderInfoTypeAMD, size_t *,
                                                             void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetShaderModuleCreateInfoIdentifierEXT(VkDevice, const VkShaderModuleCreateInfo *,
                                                                               VkShaderModuleIdentifierEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetShaderModuleIdentifierEXT(VkDevice, VkShaderModule, VkShaderModuleIdentifierEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetSwapchainCounterEXT(VkDevice, VkSwapchainKHR, VkSurfaceCounterFlagBitsEXT, uint64_t *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetSwapchainImagesKHR(VkDevice, VkSwapchainKHR, uint32_t *, VkImage *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetSwapchainStatusKHR(VkDevice, VkSwapchainKHR) { return VK_ERROR_FEATURE_NOT_PRESENT; }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetSwapchainTimeDomainPropertiesEXT(VkDevice, VkSwapchainKHR,
                                                                                VkSwapchainTimeDomainPropertiesEXT *, uint64_t *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetSwapchainTimingPropertiesEXT(VkDevice, VkSwapchainKHR, VkSwapchainTimingPropertiesEXT *,
                                                                            uint64_t *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkGetTensorMemoryRequirementsARM(VkDevice, const VkTensorMemoryRequirementsInfoARM *,
                                                                       VkMemoryRequirements2 *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetTensorOpaqueCaptureDataARM(VkDevice, uint32_t, const VkTensorARM *,
                                                                          VkHostAddressRangeEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetTensorOpaqueCaptureDescriptorDataARM(VkDevice, const VkTensorCaptureDescriptorDataInfoARM *,
                                                                                    void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetTensorViewOpaqueCaptureDescriptorDataARM(VkDevice,
                                                                                        const VkTensorViewCaptureDescriptorDataInfoARM *,
                                                                                        void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetValidationCacheDataEXT(VkDevice, VkValidationCacheEXT, size_t *, void *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetVideoSessionMemoryRequirementsKHR(VkDevice, VkVideoSessionKHR, uint32_t *,
                                                                                 VkVideoSessionMemoryRequirementsKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkImportFenceFdKHR(VkDevice, const VkImportFenceFdInfoKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkImportSemaphoreFdKHR(VkDevice, const VkImportSemaphoreFdInfoKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkInitializePerformanceApiINTEL(VkDevice, const VkInitializePerformanceApiInfoINTEL *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkLatencySleepNV(VkDevice, VkSwapchainKHR, const VkLatencySleepInfoNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkMapMemory2(VkDevice, const VkMemoryMapInfo *, void **) { return VK_ERROR_FEATURE_NOT_PRESENT; }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkMapMemory2KHR(VkDevice, const VkMemoryMapInfo *, void **) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkMergePipelineCaches(VkDevice, VkPipelineCache, uint32_t, const VkPipelineCache *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkMergeValidationCachesEXT(VkDevice, VkValidationCacheEXT, uint32_t,
                                                                     const VkValidationCacheEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkQueueBeginDebugUtilsLabelEXT(VkQueue, const VkDebugUtilsLabelEXT *) { std::abort(); }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkQueueBindSparse(VkQueue, uint32_t, const VkBindSparseInfo *, VkFence) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkQueueEndDebugUtilsLabelEXT(VkQueue) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkQueueInsertDebugUtilsLabelEXT(VkQueue, const VkDebugUtilsLabelEXT *) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkQueueNotifyOutOfBandNV(VkQueue, const VkOutOfBandQueueTypeInfoNV *) { std::abort(); }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkQueuePresentKHR(VkQueue, const VkPresentInfoKHR *) { return VK_ERROR_FEATURE_NOT_PRESENT; }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkQueueSetPerformanceConfigurationINTEL(VkQueue, VkPerformanceConfigurationINTEL) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkQueueSubmit2(VkQueue, uint32_t, const VkSubmitInfo2 *, VkFence) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkQueueSubmit2KHR(VkQueue, uint32_t, const VkSubmitInfo2 *, VkFence) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkRegisterCustomBorderColorEXT(VkDevice, const VkSamplerCustomBorderColorCreateInfoEXT *,
                                                                         VkBool32, uint32_t *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkRegisterDeviceEventEXT(VkDevice, const VkDeviceEventInfoEXT *, const VkAllocationCallbacks *,
                                                                   VkFence *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkRegisterDisplayEventEXT(VkDevice, VkDisplayKHR, const VkDisplayEventInfoEXT *,
                                                                    const VkAllocationCallbacks *, VkFence *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkReleaseCapturedPipelineDataKHR(VkDevice, const VkReleaseCapturedPipelineDataInfoKHR *,
                                                                           const VkAllocationCallbacks *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkReleaseDisplayEXT(VkPhysicalDevice, VkDisplayKHR) { return VK_ERROR_FEATURE_NOT_PRESENT; }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkReleasePerformanceConfigurationINTEL(VkDevice, VkPerformanceConfigurationINTEL) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkReleaseProfilingLockKHR(VkDevice) { std::abort(); }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkReleaseSwapchainImagesEXT(VkDevice, const VkReleaseSwapchainImagesInfoKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkReleaseSwapchainImagesKHR(VkDevice, const VkReleaseSwapchainImagesInfoKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkResetEvent(VkDevice, VkEvent) { return VK_ERROR_FEATURE_NOT_PRESENT; }
extern "C" VKAPI_ATTR void VKAPI_CALL vkResetQueryPool(VkDevice, VkQueryPool, uint32_t, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkResetQueryPoolEXT(VkDevice, VkQueryPool, uint32_t, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkSetDebugUtilsObjectNameEXT(VkDevice, const VkDebugUtilsObjectNameInfoEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkSetDebugUtilsObjectTagEXT(VkDevice, const VkDebugUtilsObjectTagInfoEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkSetDeviceMemoryPriorityEXT(VkDevice, VkDeviceMemory, float) { std::abort(); }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkSetEvent(VkDevice, VkEvent) { return VK_ERROR_FEATURE_NOT_PRESENT; }
extern "C" VKAPI_ATTR void VKAPI_CALL vkSetHdrMetadataEXT(VkDevice, uint32_t, const VkSwapchainKHR *, const VkHdrMetadataEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkSetLatencyMarkerNV(VkDevice, VkSwapchainKHR, const VkSetLatencyMarkerInfoNV *) { std::abort(); }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkSetLatencySleepModeNV(VkDevice, VkSwapchainKHR, const VkLatencySleepModeInfoNV *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkSetLocalDimmingAMD(VkDevice, VkSwapchainKHR, VkBool32) { std::abort(); }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkSetPrivateData(VkDevice, VkObjectType, uint64_t, VkPrivateDataSlot, uint64_t) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkSetPrivateDataEXT(VkDevice, VkObjectType, uint64_t, VkPrivateDataSlot, uint64_t) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkSetSwapchainPresentTimingQueueSizeEXT(VkDevice, VkSwapchainKHR, uint32_t) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkSignalSemaphore(VkDevice, const VkSemaphoreSignalInfo *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkSignalSemaphoreKHR(VkDevice, const VkSemaphoreSignalInfo *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkSubmitDebugUtilsMessageEXT(VkInstance, VkDebugUtilsMessageSeverityFlagBitsEXT,
                                                                   VkDebugUtilsMessageTypeFlagsEXT,
                                                                   const VkDebugUtilsMessengerCallbackDataEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkTransitionImageLayout(VkDevice, uint32_t, const VkHostImageLayoutTransitionInfo *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkTransitionImageLayoutEXT(VkDevice, uint32_t, const VkHostImageLayoutTransitionInfo *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkTrimCommandPool(VkDevice, VkCommandPool, VkCommandPoolTrimFlags) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkTrimCommandPoolKHR(VkDevice, VkCommandPool, VkCommandPoolTrimFlags) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkUninitializePerformanceApiINTEL(VkDevice) { std::abort(); }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkUnmapMemory2(VkDevice, const VkMemoryUnmapInfo *) { return VK_ERROR_FEATURE_NOT_PRESENT; }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkUnmapMemory2KHR(VkDevice, const VkMemoryUnmapInfo *) { return VK_ERROR_FEATURE_NOT_PRESENT; }
extern "C" VKAPI_ATTR void VKAPI_CALL vkUnregisterCustomBorderColorEXT(VkDevice, uint32_t) { std::abort(); }
extern "C" VKAPI_ATTR void VKAPI_CALL vkUpdateDescriptorSetWithTemplate(VkDevice, VkDescriptorSet, VkDescriptorUpdateTemplate,
                                                                        const void *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkUpdateDescriptorSetWithTemplateKHR(VkDevice, VkDescriptorSet, VkDescriptorUpdateTemplate,
                                                                           const void *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkUpdateIndirectExecutionSetPipelineEXT(VkDevice, VkIndirectExecutionSetEXT, uint32_t,
                                                                              const VkWriteIndirectExecutionSetPipelineEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkUpdateIndirectExecutionSetShaderEXT(VkDevice, VkIndirectExecutionSetEXT, uint32_t,
                                                                            const VkWriteIndirectExecutionSetShaderEXT *) {
  std::abort();
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkUpdateVideoSessionParametersKHR(VkDevice, VkVideoSessionParametersKHR,
                                                                            const VkVideoSessionParametersUpdateInfoKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkWaitForPresent2KHR(VkDevice, VkSwapchainKHR, const VkPresentWait2InfoKHR *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkWaitForPresentKHR(VkDevice, VkSwapchainKHR, uint64_t, uint64_t) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkWaitSemaphores(VkDevice, const VkSemaphoreWaitInfo *, uint64_t) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkWaitSemaphoresKHR(VkDevice, const VkSemaphoreWaitInfo *, uint64_t) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkWriteAccelerationStructuresPropertiesKHR(VkDevice, uint32_t, const VkAccelerationStructureKHR *,
                                                                                     VkQueryType, size_t, void *, size_t) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkWriteMicromapsPropertiesEXT(VkDevice, uint32_t, const VkMicromapEXT *, VkQueryType, size_t,
                                                                        void *, size_t) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkWriteResourceDescriptorsEXT(VkDevice, uint32_t, const VkResourceDescriptorInfoEXT *,
                                                                        const VkHostAddressRangeEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkWriteSamplerDescriptorsEXT(VkDevice, uint32_t, const VkSamplerCreateInfo *,
                                                                       const VkHostAddressRangeEXT *) {
  return VK_ERROR_FEATURE_NOT_PRESENT;
}

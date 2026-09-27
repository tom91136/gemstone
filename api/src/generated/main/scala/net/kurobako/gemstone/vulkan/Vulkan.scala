// Generated from pinned official headers by GenerateAPISurfaces. Regenerate with: sbt codegen
package net.kurobako.gemstone.vulkan

import net.kurobako.gemstone.Constraint.Positive
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.abi.ABI.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Vulkan {
  type PFN_vkVoidFunction = CallbackTypedef["PFN_vkVoidFunction", EmptyTuple, Unit]
  type VkAccelerationStructureBuildGeometryInfoKHR = CValue["VkAccelerationStructureBuildGeometryInfoKHR"]
  type VkAccelerationStructureBuildRangeInfoKHR = CValue["VkAccelerationStructureBuildRangeInfoKHR"]
  type VkAccelerationStructureBuildSizesInfoKHR = CValue["VkAccelerationStructureBuildSizesInfoKHR"]
  type VkAccelerationStructureBuildTypeKHR = CValue["VkAccelerationStructureBuildTypeKHR"]
  type VkAccelerationStructureCaptureDescriptorDataInfoEXT =
    CValue["VkAccelerationStructureCaptureDescriptorDataInfoEXT"]
  type VkAccelerationStructureCompatibilityKHR = CValue["VkAccelerationStructureCompatibilityKHR"]
  type VkAccelerationStructureCreateInfoKHR = CValue["VkAccelerationStructureCreateInfoKHR"]
  type VkAccelerationStructureCreateInfoNV = CValue["VkAccelerationStructureCreateInfoNV"]
  type VkAccelerationStructureDeviceAddressInfoKHR = CValue["VkAccelerationStructureDeviceAddressInfoKHR"]
  type VkAccelerationStructureInfoNV = CValue["VkAccelerationStructureInfoNV"]
  type VkAccelerationStructureKHR = CValue["VkAccelerationStructureKHR"]
  type VkAccelerationStructureMemoryRequirementsInfoNV = CValue["VkAccelerationStructureMemoryRequirementsInfoNV"]
  type VkAccelerationStructureNV = CValue["VkAccelerationStructureNV"]
  type VkAccelerationStructureVersionInfoKHR = CValue["VkAccelerationStructureVersionInfoKHR"]
  type VkAcquireNextImageInfoKHR = CValue["VkAcquireNextImageInfoKHR"]
  type VkAcquireProfilingLockInfoKHR = CValue["VkAcquireProfilingLockInfoKHR"]
  type VkAllocationCallbacks = CValue["VkAllocationCallbacks"]
  type VkAntiLagDataAMD = CValue["VkAntiLagDataAMD"]
  type VkBaseOutStructure = CValue["VkBaseOutStructure"]
  type VkBeginCustomResolveInfoEXT = CValue["VkBeginCustomResolveInfoEXT"]
  type VkBindAccelerationStructureMemoryInfoNV = CValue["VkBindAccelerationStructureMemoryInfoNV"]
  type VkBindBufferMemoryInfo = CValue["VkBindBufferMemoryInfo"]
  type VkBindDataGraphPipelineSessionMemoryInfoARM = CValue["VkBindDataGraphPipelineSessionMemoryInfoARM"]
  type VkBindDescriptorBufferEmbeddedSamplersInfoEXT = CValue["VkBindDescriptorBufferEmbeddedSamplersInfoEXT"]
  type VkBindDescriptorSetsInfo = CValue["VkBindDescriptorSetsInfo"]
  type VkBindHeapInfoEXT = CValue["VkBindHeapInfoEXT"]
  type VkBindImageMemoryInfo = CValue["VkBindImageMemoryInfo"]
  type VkBindSparseInfo = CValue["VkBindSparseInfo"]
  type VkBindTensorMemoryInfoARM = CValue["VkBindTensorMemoryInfoARM"]
  type VkBindVideoSessionMemoryInfoKHR = CValue["VkBindVideoSessionMemoryInfoKHR"]
  type VkBlitImageInfo2 = CValue["VkBlitImageInfo2"]
  type VkBool32 = CValue["VkBool32"]
  type VkBuffer = CValue["VkBuffer"]
  type VkBufferCaptureDescriptorDataInfoEXT = CValue["VkBufferCaptureDescriptorDataInfoEXT"]
  type VkBufferCopy = CValue["VkBufferCopy"]
  type VkBufferCreateInfo = CValue["VkBufferCreateInfo"]
  type VkBufferDeviceAddressInfo = CValue["VkBufferDeviceAddressInfo"]
  type VkBufferImageCopy = CValue["VkBufferImageCopy"]
  type VkBufferMemoryBarrier = CValue["VkBufferMemoryBarrier"]
  type VkBufferMemoryRequirementsInfo2 = CValue["VkBufferMemoryRequirementsInfo2"]
  type VkBufferView = CValue["VkBufferView"]
  type VkBufferViewCreateInfo = CValue["VkBufferViewCreateInfo"]
  type VkBuildPartitionedAccelerationStructureInfoNV = CValue["VkBuildPartitionedAccelerationStructureInfoNV"]
  type VkCalibratedTimestampInfoKHR = CValue["VkCalibratedTimestampInfoKHR"]
  type VkCheckpointData2NV = CValue["VkCheckpointData2NV"]
  type VkCheckpointDataNV = CValue["VkCheckpointDataNV"]
  type VkClearAttachment = CValue["VkClearAttachment"]
  type VkClearColorValue = CValue["VkClearColorValue"]
  type VkClearDepthStencilValue = CValue["VkClearDepthStencilValue"]
  type VkClearRect = CValue["VkClearRect"]
  type VkClusterAccelerationStructureCommandsInfoNV = CValue["VkClusterAccelerationStructureCommandsInfoNV"]
  type VkClusterAccelerationStructureInputInfoNV = CValue["VkClusterAccelerationStructureInputInfoNV"]
  type VkCoarseSampleOrderCustomNV = CValue["VkCoarseSampleOrderCustomNV"]
  type VkCoarseSampleOrderTypeNV = CValue["VkCoarseSampleOrderTypeNV"]
  type VkColorBlendAdvancedEXT = CValue["VkColorBlendAdvancedEXT"]
  type VkColorBlendEquationEXT = CValue["VkColorBlendEquationEXT"]
  type VkColorComponentFlags = CValue["VkColorComponentFlags"]
  type VkCommandBuffer = CValue["VkCommandBuffer"]
  type VkCommandBufferAllocateInfo = CValue["VkCommandBufferAllocateInfo"]
  type VkCommandBufferBeginInfo = CValue["VkCommandBufferBeginInfo"]
  type VkCommandBufferResetFlags = CValue["VkCommandBufferResetFlags"]
  type VkCommandPool = CValue["VkCommandPool"]
  type VkCommandPoolCreateInfo = CValue["VkCommandPoolCreateInfo"]
  type VkCommandPoolResetFlags = CValue["VkCommandPoolResetFlags"]
  type VkCommandPoolTrimFlags = CValue["VkCommandPoolTrimFlags"]
  type VkCompareOp = CValue["VkCompareOp"]
  type VkComputeOccupancyPriorityParametersNV = CValue["VkComputeOccupancyPriorityParametersNV"]
  type VkComputePipelineCreateInfo = CValue["VkComputePipelineCreateInfo"]
  type VkConditionalRenderingBeginInfoEXT = CValue["VkConditionalRenderingBeginInfoEXT"]
  type VkConservativeRasterizationModeEXT = CValue["VkConservativeRasterizationModeEXT"]
  type VkConvertCooperativeVectorMatrixInfoNV = CValue["VkConvertCooperativeVectorMatrixInfoNV"]
  type VkCooperativeMatrixFlexibleDimensionsPropertiesNV = CValue["VkCooperativeMatrixFlexibleDimensionsPropertiesNV"]
  type VkCooperativeMatrixPropertiesKHR = CValue["VkCooperativeMatrixPropertiesKHR"]
  type VkCooperativeMatrixPropertiesNV = CValue["VkCooperativeMatrixPropertiesNV"]
  type VkCooperativeVectorPropertiesNV = CValue["VkCooperativeVectorPropertiesNV"]
  type VkCopyAccelerationStructureInfoKHR = CValue["VkCopyAccelerationStructureInfoKHR"]
  type VkCopyAccelerationStructureModeKHR = CValue["VkCopyAccelerationStructureModeKHR"]
  type VkCopyAccelerationStructureToMemoryInfoKHR = CValue["VkCopyAccelerationStructureToMemoryInfoKHR"]
  type VkCopyBufferInfo2 = CValue["VkCopyBufferInfo2"]
  type VkCopyBufferToImageInfo2 = CValue["VkCopyBufferToImageInfo2"]
  type VkCopyDescriptorSet = CValue["VkCopyDescriptorSet"]
  type VkCopyImageInfo2 = CValue["VkCopyImageInfo2"]
  type VkCopyImageToBufferInfo2 = CValue["VkCopyImageToBufferInfo2"]
  type VkCopyImageToImageInfo = CValue["VkCopyImageToImageInfo"]
  type VkCopyImageToMemoryInfo = CValue["VkCopyImageToMemoryInfo"]
  type VkCopyMemoryIndirectInfoKHR = CValue["VkCopyMemoryIndirectInfoKHR"]
  type VkCopyMemoryToAccelerationStructureInfoKHR = CValue["VkCopyMemoryToAccelerationStructureInfoKHR"]
  type VkCopyMemoryToImageIndirectInfoKHR = CValue["VkCopyMemoryToImageIndirectInfoKHR"]
  type VkCopyMemoryToImageInfo = CValue["VkCopyMemoryToImageInfo"]
  type VkCopyMemoryToMicromapInfoEXT = CValue["VkCopyMemoryToMicromapInfoEXT"]
  type VkCopyMicromapInfoEXT = CValue["VkCopyMicromapInfoEXT"]
  type VkCopyMicromapToMemoryInfoEXT = CValue["VkCopyMicromapToMemoryInfoEXT"]
  type VkCopyTensorInfoARM = CValue["VkCopyTensorInfoARM"]
  type VkCoverageModulationModeNV = CValue["VkCoverageModulationModeNV"]
  type VkCoverageReductionModeNV = CValue["VkCoverageReductionModeNV"]
  type VkCuFunctionCreateInfoNVX = CValue["VkCuFunctionCreateInfoNVX"]
  type VkCuFunctionNVX = CValue["VkCuFunctionNVX"]
  type VkCuLaunchInfoNVX = CValue["VkCuLaunchInfoNVX"]
  type VkCuModuleCreateInfoNVX = CValue["VkCuModuleCreateInfoNVX"]
  type VkCuModuleNVX = CValue["VkCuModuleNVX"]
  type VkCullModeFlags = CValue["VkCullModeFlags"]
  type VkDataGraphPipelineCreateInfoARM = CValue["VkDataGraphPipelineCreateInfoARM"]
  type VkDataGraphPipelineDispatchInfoARM = CValue["VkDataGraphPipelineDispatchInfoARM"]
  type VkDataGraphPipelineInfoARM = CValue["VkDataGraphPipelineInfoARM"]
  type VkDataGraphPipelinePropertyARM = CValue["VkDataGraphPipelinePropertyARM"]
  type VkDataGraphPipelinePropertyQueryResultARM = CValue["VkDataGraphPipelinePropertyQueryResultARM"]
  type VkDataGraphPipelineSessionARM = CValue["VkDataGraphPipelineSessionARM"]
  type VkDataGraphPipelineSessionBindPointRequirementARM = CValue["VkDataGraphPipelineSessionBindPointRequirementARM"]
  type VkDataGraphPipelineSessionBindPointRequirementsInfoARM =
    CValue["VkDataGraphPipelineSessionBindPointRequirementsInfoARM"]
  type VkDataGraphPipelineSessionCreateInfoARM = CValue["VkDataGraphPipelineSessionCreateInfoARM"]
  type VkDataGraphPipelineSessionMemoryRequirementsInfoARM =
    CValue["VkDataGraphPipelineSessionMemoryRequirementsInfoARM"]
  type VkDebugMarkerMarkerInfoEXT = CValue["VkDebugMarkerMarkerInfoEXT"]
  type VkDebugMarkerObjectNameInfoEXT = CValue["VkDebugMarkerObjectNameInfoEXT"]
  type VkDebugMarkerObjectTagInfoEXT = CValue["VkDebugMarkerObjectTagInfoEXT"]
  type VkDebugReportCallbackCreateInfoEXT = CValue["VkDebugReportCallbackCreateInfoEXT"]
  type VkDebugReportCallbackEXT = CValue["VkDebugReportCallbackEXT"]
  type VkDebugReportFlagsEXT = CValue["VkDebugReportFlagsEXT"]
  type VkDebugReportObjectTypeEXT = CValue["VkDebugReportObjectTypeEXT"]
  type VkDebugUtilsLabelEXT = CValue["VkDebugUtilsLabelEXT"]
  type VkDebugUtilsMessageSeverityFlagBitsEXT = CValue["VkDebugUtilsMessageSeverityFlagBitsEXT"]
  type VkDebugUtilsMessageTypeFlagsEXT = CValue["VkDebugUtilsMessageTypeFlagsEXT"]
  type VkDebugUtilsMessengerCallbackDataEXT = CValue["VkDebugUtilsMessengerCallbackDataEXT"]
  type VkDebugUtilsMessengerCreateInfoEXT = CValue["VkDebugUtilsMessengerCreateInfoEXT"]
  type VkDebugUtilsMessengerEXT = CValue["VkDebugUtilsMessengerEXT"]
  type VkDebugUtilsObjectNameInfoEXT = CValue["VkDebugUtilsObjectNameInfoEXT"]
  type VkDebugUtilsObjectTagInfoEXT = CValue["VkDebugUtilsObjectTagInfoEXT"]
  type VkDecompressMemoryInfoEXT = CValue["VkDecompressMemoryInfoEXT"]
  type VkDecompressMemoryRegionNV = CValue["VkDecompressMemoryRegionNV"]
  type VkDeferredOperationKHR = CValue["VkDeferredOperationKHR"]
  type VkDependencyFlags = CValue["VkDependencyFlags"]
  type VkDependencyInfo = CValue["VkDependencyInfo"]
  type VkDepthBiasInfoEXT = CValue["VkDepthBiasInfoEXT"]
  type VkDepthClampModeEXT = CValue["VkDepthClampModeEXT"]
  type VkDepthClampRangeEXT = CValue["VkDepthClampRangeEXT"]
  type VkDescriptorBufferBindingInfoEXT = CValue["VkDescriptorBufferBindingInfoEXT"]
  type VkDescriptorGetInfoEXT = CValue["VkDescriptorGetInfoEXT"]
  type VkDescriptorPool = CValue["VkDescriptorPool"]
  type VkDescriptorPoolCreateInfo = CValue["VkDescriptorPoolCreateInfo"]
  type VkDescriptorPoolResetFlags = CValue["VkDescriptorPoolResetFlags"]
  type VkDescriptorSet = CValue["VkDescriptorSet"]
  type VkDescriptorSetAllocateInfo = CValue["VkDescriptorSetAllocateInfo"]
  type VkDescriptorSetBindingReferenceVALVE = CValue["VkDescriptorSetBindingReferenceVALVE"]
  type VkDescriptorSetLayout = CValue["VkDescriptorSetLayout"]
  type VkDescriptorSetLayoutCreateInfo = CValue["VkDescriptorSetLayoutCreateInfo"]
  type VkDescriptorSetLayoutHostMappingInfoVALVE = CValue["VkDescriptorSetLayoutHostMappingInfoVALVE"]
  type VkDescriptorSetLayoutSupport = CValue["VkDescriptorSetLayoutSupport"]
  type VkDescriptorType = CValue["VkDescriptorType"]
  type VkDescriptorUpdateTemplate = CValue["VkDescriptorUpdateTemplate"]
  type VkDescriptorUpdateTemplateCreateInfo = CValue["VkDescriptorUpdateTemplateCreateInfo"]
  type VkDevice = CValue["VkDevice"]
  type VkDeviceAddress = CValue["VkDeviceAddress"]
  type VkDeviceBufferMemoryRequirements = CValue["VkDeviceBufferMemoryRequirements"]
  type VkDeviceCreateInfo = CValue["VkDeviceCreateInfo"]
  type VkDeviceEventInfoEXT = CValue["VkDeviceEventInfoEXT"]
  type VkDeviceFaultCountsEXT = CValue["VkDeviceFaultCountsEXT"]
  type VkDeviceFaultInfoEXT = CValue["VkDeviceFaultInfoEXT"]
  type VkDeviceGroupPresentCapabilitiesKHR = CValue["VkDeviceGroupPresentCapabilitiesKHR"]
  type VkDeviceGroupPresentModeFlagsKHR = CValue["VkDeviceGroupPresentModeFlagsKHR"]
  type VkDeviceImageMemoryRequirements = CValue["VkDeviceImageMemoryRequirements"]
  type VkDeviceImageSubresourceInfo = CValue["VkDeviceImageSubresourceInfo"]
  type VkDeviceMemory = CValue["VkDeviceMemory"]
  type VkDeviceMemoryOpaqueCaptureAddressInfo = CValue["VkDeviceMemoryOpaqueCaptureAddressInfo"]
  type VkDeviceQueueInfo2 = CValue["VkDeviceQueueInfo2"]
  type VkDeviceSize = CValue["VkDeviceSize"]
  type VkDeviceTensorMemoryRequirementsARM = CValue["VkDeviceTensorMemoryRequirementsARM"]
  type VkDiscardRectangleModeEXT = CValue["VkDiscardRectangleModeEXT"]
  type VkDispatchTileInfoQCOM = CValue["VkDispatchTileInfoQCOM"]
  type VkDisplayEventInfoEXT = CValue["VkDisplayEventInfoEXT"]
  type VkDisplayKHR = CValue["VkDisplayKHR"]
  type VkDisplayModeCreateInfoKHR = CValue["VkDisplayModeCreateInfoKHR"]
  type VkDisplayModeKHR = CValue["VkDisplayModeKHR"]
  type VkDisplayModeProperties2KHR = CValue["VkDisplayModeProperties2KHR"]
  type VkDisplayModePropertiesKHR = CValue["VkDisplayModePropertiesKHR"]
  type VkDisplayPlaneCapabilities2KHR = CValue["VkDisplayPlaneCapabilities2KHR"]
  type VkDisplayPlaneCapabilitiesKHR = CValue["VkDisplayPlaneCapabilitiesKHR"]
  type VkDisplayPlaneInfo2KHR = CValue["VkDisplayPlaneInfo2KHR"]
  type VkDisplayPlaneProperties2KHR = CValue["VkDisplayPlaneProperties2KHR"]
  type VkDisplayPlanePropertiesKHR = CValue["VkDisplayPlanePropertiesKHR"]
  type VkDisplayPowerInfoEXT = CValue["VkDisplayPowerInfoEXT"]
  type VkDisplayProperties2KHR = CValue["VkDisplayProperties2KHR"]
  type VkDisplayPropertiesKHR = CValue["VkDisplayPropertiesKHR"]
  type VkDisplaySurfaceCreateInfoKHR = CValue["VkDisplaySurfaceCreateInfoKHR"]
  type VkEvent = CValue["VkEvent"]
  type VkEventCreateInfo = CValue["VkEventCreateInfo"]
  type VkExtensionProperties = CValue["VkExtensionProperties"]
  type VkExtent2D = CValue["VkExtent2D"]
  type VkExternalBufferProperties = CValue["VkExternalBufferProperties"]
  type VkExternalComputeQueueCreateInfoNV = CValue["VkExternalComputeQueueCreateInfoNV"]
  type VkExternalComputeQueueDataParamsNV = CValue["VkExternalComputeQueueDataParamsNV"]
  type VkExternalComputeQueueNV = CValue["VkExternalComputeQueueNV"]
  type VkExternalFenceProperties = CValue["VkExternalFenceProperties"]
  type VkExternalImageFormatPropertiesNV = CValue["VkExternalImageFormatPropertiesNV"]
  type VkExternalMemoryHandleTypeFlagBits = CValue["VkExternalMemoryHandleTypeFlagBits"]
  type VkExternalMemoryHandleTypeFlagsNV = CValue["VkExternalMemoryHandleTypeFlagsNV"]
  type VkExternalSemaphoreProperties = CValue["VkExternalSemaphoreProperties"]
  type VkExternalTensorPropertiesARM = CValue["VkExternalTensorPropertiesARM"]
  type VkFence = CValue["VkFence"]
  type VkFenceCreateInfo = CValue["VkFenceCreateInfo"]
  type VkFenceGetFdInfoKHR = CValue["VkFenceGetFdInfoKHR"]
  type VkFilter = CValue["VkFilter"]
  type VkFormat = CValue["VkFormat"]
  type VkFormatProperties = CValue["VkFormatProperties"]
  type VkFormatProperties2 = CValue["VkFormatProperties2"]
  type VkFragmentShadingRateCombinerOpKHR = CValue["VkFragmentShadingRateCombinerOpKHR"]
  type VkFragmentShadingRateNV = CValue["VkFragmentShadingRateNV"]
  type VkFramebuffer = CValue["VkFramebuffer"]
  type VkFramebufferCreateInfo = CValue["VkFramebufferCreateInfo"]
  type VkFramebufferMixedSamplesCombinationNV = CValue["VkFramebufferMixedSamplesCombinationNV"]
  type VkFrontFace = CValue["VkFrontFace"]
  type VkGeneratedCommandsInfoEXT = CValue["VkGeneratedCommandsInfoEXT"]
  type VkGeneratedCommandsInfoNV = CValue["VkGeneratedCommandsInfoNV"]
  type VkGeneratedCommandsMemoryRequirementsInfoEXT = CValue["VkGeneratedCommandsMemoryRequirementsInfoEXT"]
  type VkGeneratedCommandsMemoryRequirementsInfoNV = CValue["VkGeneratedCommandsMemoryRequirementsInfoNV"]
  type VkGetLatencyMarkerInfoNV = CValue["VkGetLatencyMarkerInfoNV"]
  type VkGraphicsPipelineCreateInfo = CValue["VkGraphicsPipelineCreateInfo"]
  type VkHdrMetadataEXT = CValue["VkHdrMetadataEXT"]
  type VkHeadlessSurfaceCreateInfoEXT = CValue["VkHeadlessSurfaceCreateInfoEXT"]
  type VkHostAddressRangeEXT = CValue["VkHostAddressRangeEXT"]
  type VkHostImageLayoutTransitionInfo = CValue["VkHostImageLayoutTransitionInfo"]
  type VkImage = CValue["VkImage"]
  type VkImageAspectFlags = CValue["VkImageAspectFlags"]
  type VkImageBlit = CValue["VkImageBlit"]
  type VkImageCaptureDescriptorDataInfoEXT = CValue["VkImageCaptureDescriptorDataInfoEXT"]
  type VkImageCopy = CValue["VkImageCopy"]
  type VkImageCreateFlags = CValue["VkImageCreateFlags"]
  type VkImageCreateInfo = CValue["VkImageCreateInfo"]
  type VkImageDrmFormatModifierPropertiesEXT = CValue["VkImageDrmFormatModifierPropertiesEXT"]
  type VkImageFormatProperties = CValue["VkImageFormatProperties"]
  type VkImageFormatProperties2 = CValue["VkImageFormatProperties2"]
  type VkImageLayout = CValue["VkImageLayout"]
  type VkImageMemoryBarrier = CValue["VkImageMemoryBarrier"]
  type VkImageMemoryRequirementsInfo2 = CValue["VkImageMemoryRequirementsInfo2"]
  type VkImageResolve = CValue["VkImageResolve"]
  type VkImageSparseMemoryRequirementsInfo2 = CValue["VkImageSparseMemoryRequirementsInfo2"]
  type VkImageSubresource = CValue["VkImageSubresource"]
  type VkImageSubresource2 = CValue["VkImageSubresource2"]
  type VkImageSubresourceLayers = CValue["VkImageSubresourceLayers"]
  type VkImageSubresourceRange = CValue["VkImageSubresourceRange"]
  type VkImageTiling = CValue["VkImageTiling"]
  type VkImageType = CValue["VkImageType"]
  type VkImageUsageFlags = CValue["VkImageUsageFlags"]
  type VkImageView = CValue["VkImageView"]
  type VkImageViewAddressPropertiesNVX = CValue["VkImageViewAddressPropertiesNVX"]
  type VkImageViewCaptureDescriptorDataInfoEXT = CValue["VkImageViewCaptureDescriptorDataInfoEXT"]
  type VkImageViewCreateInfo = CValue["VkImageViewCreateInfo"]
  type VkImageViewHandleInfoNVX = CValue["VkImageViewHandleInfoNVX"]
  type VkImportFenceFdInfoKHR = CValue["VkImportFenceFdInfoKHR"]
  type VkImportSemaphoreFdInfoKHR = CValue["VkImportSemaphoreFdInfoKHR"]
  type VkIndexType = CValue["VkIndexType"]
  type VkIndirectCommandsLayoutCreateInfoEXT = CValue["VkIndirectCommandsLayoutCreateInfoEXT"]
  type VkIndirectCommandsLayoutCreateInfoNV = CValue["VkIndirectCommandsLayoutCreateInfoNV"]
  type VkIndirectCommandsLayoutEXT = CValue["VkIndirectCommandsLayoutEXT"]
  type VkIndirectCommandsLayoutNV = CValue["VkIndirectCommandsLayoutNV"]
  type VkIndirectExecutionSetCreateInfoEXT = CValue["VkIndirectExecutionSetCreateInfoEXT"]
  type VkIndirectExecutionSetEXT = CValue["VkIndirectExecutionSetEXT"]
  type VkInitializePerformanceApiInfoINTEL = CValue["VkInitializePerformanceApiInfoINTEL"]
  type VkInstance = CValue["VkInstance"]
  type VkInstanceCreateInfo = CValue["VkInstanceCreateInfo"]
  type VkLatencySleepInfoNV = CValue["VkLatencySleepInfoNV"]
  type VkLatencySleepModeInfoNV = CValue["VkLatencySleepModeInfoNV"]
  type VkLayerProperties = CValue["VkLayerProperties"]
  type VkLineRasterizationModeEXT = CValue["VkLineRasterizationModeEXT"]
  type VkLogicOp = CValue["VkLogicOp"]
  type VkMappedMemoryRange = CValue["VkMappedMemoryRange"]
  type VkMemoryAllocateInfo = CValue["VkMemoryAllocateInfo"]
  type VkMemoryBarrier = CValue["VkMemoryBarrier"]
  type VkMemoryDecompressionMethodFlagsEXT = CValue["VkMemoryDecompressionMethodFlagsEXT"]
  type VkMemoryFdPropertiesKHR = CValue["VkMemoryFdPropertiesKHR"]
  type VkMemoryGetFdInfoKHR = CValue["VkMemoryGetFdInfoKHR"]
  type VkMemoryGetRemoteAddressInfoNV = CValue["VkMemoryGetRemoteAddressInfoNV"]
  type VkMemoryHostPointerPropertiesEXT = CValue["VkMemoryHostPointerPropertiesEXT"]
  type VkMemoryMapFlags = CValue["VkMemoryMapFlags"]
  type VkMemoryMapInfo = CValue["VkMemoryMapInfo"]
  type VkMemoryRequirements = CValue["VkMemoryRequirements"]
  type VkMemoryRequirements2 = CValue["VkMemoryRequirements2"]
  type VkMemoryRequirements2KHR = CValue["VkMemoryRequirements2KHR"]
  type VkMemoryUnmapInfo = CValue["VkMemoryUnmapInfo"]
  type VkMicromapBuildInfoEXT = CValue["VkMicromapBuildInfoEXT"]
  type VkMicromapBuildSizesInfoEXT = CValue["VkMicromapBuildSizesInfoEXT"]
  type VkMicromapCreateInfoEXT = CValue["VkMicromapCreateInfoEXT"]
  type VkMicromapEXT = CValue["VkMicromapEXT"]
  type VkMicromapVersionInfoEXT = CValue["VkMicromapVersionInfoEXT"]
  type VkMultiDrawIndexedInfoEXT = CValue["VkMultiDrawIndexedInfoEXT"]
  type VkMultiDrawInfoEXT = CValue["VkMultiDrawInfoEXT"]
  type VkMultisamplePropertiesEXT = CValue["VkMultisamplePropertiesEXT"]
  type VkObjectType = CValue["VkObjectType"]
  type VkOpticalFlowExecuteInfoNV = CValue["VkOpticalFlowExecuteInfoNV"]
  type VkOpticalFlowImageFormatInfoNV = CValue["VkOpticalFlowImageFormatInfoNV"]
  type VkOpticalFlowImageFormatPropertiesNV = CValue["VkOpticalFlowImageFormatPropertiesNV"]
  type VkOpticalFlowSessionBindingPointNV = CValue["VkOpticalFlowSessionBindingPointNV"]
  type VkOpticalFlowSessionCreateInfoNV = CValue["VkOpticalFlowSessionCreateInfoNV"]
  type VkOpticalFlowSessionNV = CValue["VkOpticalFlowSessionNV"]
  type VkOutOfBandQueueTypeInfoNV = CValue["VkOutOfBandQueueTypeInfoNV"]
  type VkPartitionedAccelerationStructureInstancesInputNV = CValue["VkPartitionedAccelerationStructureInstancesInputNV"]
  type VkPastPresentationTimingGOOGLE = CValue["VkPastPresentationTimingGOOGLE"]
  type VkPastPresentationTimingInfoEXT = CValue["VkPastPresentationTimingInfoEXT"]
  type VkPastPresentationTimingPropertiesEXT = CValue["VkPastPresentationTimingPropertiesEXT"]
  type VkPeerMemoryFeatureFlags = CValue["VkPeerMemoryFeatureFlags"]
  type VkPerTileBeginInfoQCOM = CValue["VkPerTileBeginInfoQCOM"]
  type VkPerTileEndInfoQCOM = CValue["VkPerTileEndInfoQCOM"]
  type VkPerformanceConfigurationAcquireInfoINTEL = CValue["VkPerformanceConfigurationAcquireInfoINTEL"]
  type VkPerformanceConfigurationINTEL = CValue["VkPerformanceConfigurationINTEL"]
  type VkPerformanceCounterARM = CValue["VkPerformanceCounterARM"]
  type VkPerformanceCounterDescriptionARM = CValue["VkPerformanceCounterDescriptionARM"]
  type VkPerformanceCounterDescriptionKHR = CValue["VkPerformanceCounterDescriptionKHR"]
  type VkPerformanceCounterKHR = CValue["VkPerformanceCounterKHR"]
  type VkPerformanceMarkerInfoINTEL = CValue["VkPerformanceMarkerInfoINTEL"]
  type VkPerformanceOverrideInfoINTEL = CValue["VkPerformanceOverrideInfoINTEL"]
  type VkPerformanceParameterTypeINTEL = CValue["VkPerformanceParameterTypeINTEL"]
  type VkPerformanceStreamMarkerInfoINTEL = CValue["VkPerformanceStreamMarkerInfoINTEL"]
  type VkPerformanceValueINTEL = CValue["VkPerformanceValueINTEL"]
  type VkPhysicalDevice = CValue["VkPhysicalDevice"]
  type VkPhysicalDeviceExternalBufferInfo = CValue["VkPhysicalDeviceExternalBufferInfo"]
  type VkPhysicalDeviceExternalFenceInfo = CValue["VkPhysicalDeviceExternalFenceInfo"]
  type VkPhysicalDeviceExternalSemaphoreInfo = CValue["VkPhysicalDeviceExternalSemaphoreInfo"]
  type VkPhysicalDeviceExternalTensorInfoARM = CValue["VkPhysicalDeviceExternalTensorInfoARM"]
  type VkPhysicalDeviceFeatures = CValue["VkPhysicalDeviceFeatures"]
  type VkPhysicalDeviceFeatures2 = CValue["VkPhysicalDeviceFeatures2"]
  type VkPhysicalDeviceFragmentShadingRateKHR = CValue["VkPhysicalDeviceFragmentShadingRateKHR"]
  type VkPhysicalDeviceGroupProperties = CValue["VkPhysicalDeviceGroupProperties"]
  type VkPhysicalDeviceImageFormatInfo2 = CValue["VkPhysicalDeviceImageFormatInfo2"]
  type VkPhysicalDeviceMemoryProperties = CValue["VkPhysicalDeviceMemoryProperties"]
  type VkPhysicalDeviceMemoryProperties2 = CValue["VkPhysicalDeviceMemoryProperties2"]
  type VkPhysicalDeviceProperties = CValue["VkPhysicalDeviceProperties"]
  type VkPhysicalDeviceProperties2 = CValue["VkPhysicalDeviceProperties2"]
  type VkPhysicalDeviceQueueFamilyDataGraphProcessingEngineInfoARM =
    CValue["VkPhysicalDeviceQueueFamilyDataGraphProcessingEngineInfoARM"]
  type VkPhysicalDeviceSparseImageFormatInfo2 = CValue["VkPhysicalDeviceSparseImageFormatInfo2"]
  type VkPhysicalDeviceSurfaceInfo2KHR = CValue["VkPhysicalDeviceSurfaceInfo2KHR"]
  type VkPhysicalDeviceToolProperties = CValue["VkPhysicalDeviceToolProperties"]
  type VkPhysicalDeviceVideoEncodeQualityLevelInfoKHR = CValue["VkPhysicalDeviceVideoEncodeQualityLevelInfoKHR"]
  type VkPhysicalDeviceVideoFormatInfoKHR = CValue["VkPhysicalDeviceVideoFormatInfoKHR"]
  type VkPipeline = CValue["VkPipeline"]
  type VkPipelineBinaryCreateInfoKHR = CValue["VkPipelineBinaryCreateInfoKHR"]
  type VkPipelineBinaryDataInfoKHR = CValue["VkPipelineBinaryDataInfoKHR"]
  type VkPipelineBinaryHandlesInfoKHR = CValue["VkPipelineBinaryHandlesInfoKHR"]
  type VkPipelineBinaryKHR = CValue["VkPipelineBinaryKHR"]
  type VkPipelineBinaryKeyKHR = CValue["VkPipelineBinaryKeyKHR"]
  type VkPipelineBindPoint = CValue["VkPipelineBindPoint"]
  type VkPipelineCache = CValue["VkPipelineCache"]
  type VkPipelineCacheCreateInfo = CValue["VkPipelineCacheCreateInfo"]
  type VkPipelineCreateInfoKHR = CValue["VkPipelineCreateInfoKHR"]
  type VkPipelineExecutableInfoKHR = CValue["VkPipelineExecutableInfoKHR"]
  type VkPipelineExecutableInternalRepresentationKHR = CValue["VkPipelineExecutableInternalRepresentationKHR"]
  type VkPipelineExecutablePropertiesKHR = CValue["VkPipelineExecutablePropertiesKHR"]
  type VkPipelineExecutableStatisticKHR = CValue["VkPipelineExecutableStatisticKHR"]
  type VkPipelineIndirectDeviceAddressInfoNV = CValue["VkPipelineIndirectDeviceAddressInfoNV"]
  type VkPipelineInfoEXT = CValue["VkPipelineInfoEXT"]
  type VkPipelineInfoKHR = CValue["VkPipelineInfoKHR"]
  type VkPipelineLayout = CValue["VkPipelineLayout"]
  type VkPipelineLayoutCreateInfo = CValue["VkPipelineLayoutCreateInfo"]
  type VkPipelineStageFlagBits = CValue["VkPipelineStageFlagBits"]
  type VkPipelineStageFlags = CValue["VkPipelineStageFlags"]
  type VkPipelineStageFlags2 = CValue["VkPipelineStageFlags2"]
  type VkPolygonMode = CValue["VkPolygonMode"]
  type VkPresentInfoKHR = CValue["VkPresentInfoKHR"]
  type VkPresentModeKHR = CValue["VkPresentModeKHR"]
  type VkPresentWait2InfoKHR = CValue["VkPresentWait2InfoKHR"]
  type VkPrimitiveTopology = CValue["VkPrimitiveTopology"]
  type VkPrivateDataSlot = CValue["VkPrivateDataSlot"]
  type VkPrivateDataSlotCreateInfo = CValue["VkPrivateDataSlotCreateInfo"]
  type VkProvokingVertexModeEXT = CValue["VkProvokingVertexModeEXT"]
  type VkPushConstantsInfo = CValue["VkPushConstantsInfo"]
  type VkPushDataInfoEXT = CValue["VkPushDataInfoEXT"]
  type VkPushDescriptorSetInfo = CValue["VkPushDescriptorSetInfo"]
  type VkPushDescriptorSetWithTemplateInfo = CValue["VkPushDescriptorSetWithTemplateInfo"]
  type VkQueryControlFlags = CValue["VkQueryControlFlags"]
  type VkQueryPool = CValue["VkQueryPool"]
  type VkQueryPoolCreateInfo = CValue["VkQueryPoolCreateInfo"]
  type VkQueryPoolPerformanceCreateInfoKHR = CValue["VkQueryPoolPerformanceCreateInfoKHR"]
  type VkQueryResultFlags = CValue["VkQueryResultFlags"]
  type VkQueryType = CValue["VkQueryType"]
  type VkQueue = CValue["VkQueue"]
  type VkQueueFamilyDataGraphProcessingEnginePropertiesARM =
    CValue["VkQueueFamilyDataGraphProcessingEnginePropertiesARM"]
  type VkQueueFamilyDataGraphPropertiesARM = CValue["VkQueueFamilyDataGraphPropertiesARM"]
  type VkQueueFamilyProperties = CValue["VkQueueFamilyProperties"]
  type VkQueueFamilyProperties2 = CValue["VkQueueFamilyProperties2"]
  type VkRayTracingPipelineCreateInfoKHR = CValue["VkRayTracingPipelineCreateInfoKHR"]
  type VkRayTracingPipelineCreateInfoNV = CValue["VkRayTracingPipelineCreateInfoNV"]
  type VkRect2D = CValue["VkRect2D"]
  type VkRefreshCycleDurationGOOGLE = CValue["VkRefreshCycleDurationGOOGLE"]
  type VkReleaseCapturedPipelineDataInfoKHR = CValue["VkReleaseCapturedPipelineDataInfoKHR"]
  type VkReleaseSwapchainImagesInfoKHR = CValue["VkReleaseSwapchainImagesInfoKHR"]
  type VkRemoteAddressNV = CValue["VkRemoteAddressNV"]
  type VkRenderPass = CValue["VkRenderPass"]
  type VkRenderPassBeginInfo = CValue["VkRenderPassBeginInfo"]
  type VkRenderPassCreateInfo = CValue["VkRenderPassCreateInfo"]
  type VkRenderPassCreateInfo2 = CValue["VkRenderPassCreateInfo2"]
  type VkRenderingAreaInfo = CValue["VkRenderingAreaInfo"]
  type VkRenderingAttachmentLocationInfo = CValue["VkRenderingAttachmentLocationInfo"]
  type VkRenderingEndInfoKHR = CValue["VkRenderingEndInfoKHR"]
  type VkRenderingInfo = CValue["VkRenderingInfo"]
  type VkRenderingInputAttachmentIndexInfo = CValue["VkRenderingInputAttachmentIndexInfo"]
  type VkResolveImageInfo2 = CValue["VkResolveImageInfo2"]
  type VkResourceDescriptorInfoEXT = CValue["VkResourceDescriptorInfoEXT"]
  type VkResult = CValue["VkResult"]
  type VkSampleCountFlagBits = CValue["VkSampleCountFlagBits"]
  type VkSampleLocationsInfoEXT = CValue["VkSampleLocationsInfoEXT"]
  type VkSampleMask = CValue["VkSampleMask"]
  type VkSampler = CValue["VkSampler"]
  type VkSamplerCaptureDescriptorDataInfoEXT = CValue["VkSamplerCaptureDescriptorDataInfoEXT"]
  type VkSamplerCreateInfo = CValue["VkSamplerCreateInfo"]
  type VkSamplerCustomBorderColorCreateInfoEXT = CValue["VkSamplerCustomBorderColorCreateInfoEXT"]
  type VkSamplerYcbcrConversion = CValue["VkSamplerYcbcrConversion"]
  type VkSamplerYcbcrConversionCreateInfo = CValue["VkSamplerYcbcrConversionCreateInfo"]
  type VkSemaphore = CValue["VkSemaphore"]
  type VkSemaphoreCreateInfo = CValue["VkSemaphoreCreateInfo"]
  type VkSemaphoreGetFdInfoKHR = CValue["VkSemaphoreGetFdInfoKHR"]
  type VkSemaphoreSignalInfo = CValue["VkSemaphoreSignalInfo"]
  type VkSemaphoreWaitInfo = CValue["VkSemaphoreWaitInfo"]
  type VkSetDescriptorBufferOffsetsInfoEXT = CValue["VkSetDescriptorBufferOffsetsInfoEXT"]
  type VkSetLatencyMarkerInfoNV = CValue["VkSetLatencyMarkerInfoNV"]
  type VkShaderCreateInfoEXT = CValue["VkShaderCreateInfoEXT"]
  type VkShaderEXT = CValue["VkShaderEXT"]
  type VkShaderGroupShaderKHR = CValue["VkShaderGroupShaderKHR"]
  type VkShaderInfoTypeAMD = CValue["VkShaderInfoTypeAMD"]
  type VkShaderModule = CValue["VkShaderModule"]
  type VkShaderModuleCreateInfo = CValue["VkShaderModuleCreateInfo"]
  type VkShaderModuleIdentifierEXT = CValue["VkShaderModuleIdentifierEXT"]
  type VkShaderStageFlagBits = CValue["VkShaderStageFlagBits"]
  type VkShaderStageFlags = CValue["VkShaderStageFlags"]
  type VkShadingRatePaletteNV = CValue["VkShadingRatePaletteNV"]
  type VkSparseImageFormatProperties = CValue["VkSparseImageFormatProperties"]
  type VkSparseImageFormatProperties2 = CValue["VkSparseImageFormatProperties2"]
  type VkSparseImageMemoryRequirements = CValue["VkSparseImageMemoryRequirements"]
  type VkSparseImageMemoryRequirements2 = CValue["VkSparseImageMemoryRequirements2"]
  type VkStencilFaceFlags = CValue["VkStencilFaceFlags"]
  type VkStencilOp = CValue["VkStencilOp"]
  type VkStridedDeviceAddressRegionKHR = CValue["VkStridedDeviceAddressRegionKHR"]
  type VkSubmitInfo = CValue["VkSubmitInfo"]
  type VkSubmitInfo2 = CValue["VkSubmitInfo2"]
  type VkSubpassBeginInfo = CValue["VkSubpassBeginInfo"]
  type VkSubpassContents = CValue["VkSubpassContents"]
  type VkSubpassEndInfo = CValue["VkSubpassEndInfo"]
  type VkSubresourceLayout = CValue["VkSubresourceLayout"]
  type VkSubresourceLayout2 = CValue["VkSubresourceLayout2"]
  type VkSurfaceCapabilities2EXT = CValue["VkSurfaceCapabilities2EXT"]
  type VkSurfaceCapabilities2KHR = CValue["VkSurfaceCapabilities2KHR"]
  type VkSurfaceCapabilitiesKHR = CValue["VkSurfaceCapabilitiesKHR"]
  type VkSurfaceCounterFlagBitsEXT = CValue["VkSurfaceCounterFlagBitsEXT"]
  type VkSurfaceFormat2KHR = CValue["VkSurfaceFormat2KHR"]
  type VkSurfaceFormatKHR = CValue["VkSurfaceFormatKHR"]
  type VkSurfaceKHR = CValue["VkSurfaceKHR"]
  type VkSwapchainCreateInfoKHR = CValue["VkSwapchainCreateInfoKHR"]
  type VkSwapchainKHR = CValue["VkSwapchainKHR"]
  type VkSwapchainTimeDomainPropertiesEXT = CValue["VkSwapchainTimeDomainPropertiesEXT"]
  type VkSwapchainTimingPropertiesEXT = CValue["VkSwapchainTimingPropertiesEXT"]
  type VkTensorARM = CValue["VkTensorARM"]
  type VkTensorCaptureDescriptorDataInfoARM = CValue["VkTensorCaptureDescriptorDataInfoARM"]
  type VkTensorCreateInfoARM = CValue["VkTensorCreateInfoARM"]
  type VkTensorMemoryRequirementsInfoARM = CValue["VkTensorMemoryRequirementsInfoARM"]
  type VkTensorViewARM = CValue["VkTensorViewARM"]
  type VkTensorViewCaptureDescriptorDataInfoARM = CValue["VkTensorViewCaptureDescriptorDataInfoARM"]
  type VkTensorViewCreateInfoARM = CValue["VkTensorViewCreateInfoARM"]
  type VkTessellationDomainOrigin = CValue["VkTessellationDomainOrigin"]
  type VkTileMemoryBindInfoQCOM = CValue["VkTileMemoryBindInfoQCOM"]
  type VkTilePropertiesQCOM = CValue["VkTilePropertiesQCOM"]
  type VkTimeDomainKHR = CValue["VkTimeDomainKHR"]
  type VkValidationCacheCreateInfoEXT = CValue["VkValidationCacheCreateInfoEXT"]
  type VkValidationCacheEXT = CValue["VkValidationCacheEXT"]
  type VkVertexInputAttributeDescription2EXT = CValue["VkVertexInputAttributeDescription2EXT"]
  type VkVertexInputBindingDescription2EXT = CValue["VkVertexInputBindingDescription2EXT"]
  type VkVideoBeginCodingInfoKHR = CValue["VkVideoBeginCodingInfoKHR"]
  type VkVideoCapabilitiesKHR = CValue["VkVideoCapabilitiesKHR"]
  type VkVideoCodingControlInfoKHR = CValue["VkVideoCodingControlInfoKHR"]
  type VkVideoDecodeInfoKHR = CValue["VkVideoDecodeInfoKHR"]
  type VkVideoEncodeInfoKHR = CValue["VkVideoEncodeInfoKHR"]
  type VkVideoEncodeQualityLevelPropertiesKHR = CValue["VkVideoEncodeQualityLevelPropertiesKHR"]
  type VkVideoEncodeSessionParametersFeedbackInfoKHR = CValue["VkVideoEncodeSessionParametersFeedbackInfoKHR"]
  type VkVideoEncodeSessionParametersGetInfoKHR = CValue["VkVideoEncodeSessionParametersGetInfoKHR"]
  type VkVideoEndCodingInfoKHR = CValue["VkVideoEndCodingInfoKHR"]
  type VkVideoFormatPropertiesKHR = CValue["VkVideoFormatPropertiesKHR"]
  type VkVideoProfileInfoKHR = CValue["VkVideoProfileInfoKHR"]
  type VkVideoSessionCreateInfoKHR = CValue["VkVideoSessionCreateInfoKHR"]
  type VkVideoSessionKHR = CValue["VkVideoSessionKHR"]
  type VkVideoSessionMemoryRequirementsKHR = CValue["VkVideoSessionMemoryRequirementsKHR"]
  type VkVideoSessionParametersCreateInfoKHR = CValue["VkVideoSessionParametersCreateInfoKHR"]
  type VkVideoSessionParametersKHR = CValue["VkVideoSessionParametersKHR"]
  type VkVideoSessionParametersUpdateInfoKHR = CValue["VkVideoSessionParametersUpdateInfoKHR"]
  type VkViewport = CValue["VkViewport"]
  type VkViewportSwizzleNV = CValue["VkViewportSwizzleNV"]
  type VkViewportWScalingNV = CValue["VkViewportWScalingNV"]
  type VkWriteDescriptorSet = CValue["VkWriteDescriptorSet"]
  type VkWriteIndirectExecutionSetPipelineEXT = CValue["VkWriteIndirectExecutionSetPipelineEXT"]
  type VkWriteIndirectExecutionSetShaderEXT = CValue["VkWriteIndirectExecutionSetShaderEXT"]
  type int32_t = CValue["int32_t"]
  type size_t = CValue["size_t"]
  type uint16_t = CValue["uint16_t"]
  type uint32_t = CValue["uint32_t"]
  type uint64_t = CValue["uint64_t"]

  val HeaderVersion: NonEmptyString = NonEmptyString("v1.4.341")
  val FunctionCount: Positive[Int] = 719
}

import Vulkan.*

trait Vulkan {
  def vkAcquireDrmDisplayEXT(physicalDevice: VkPhysicalDevice, drmFd: int32_t, display: VkDisplayKHR): VkResult =
    unsupported
  def vkAcquireNextImage2KHR(
      device: VkDevice,
      pAcquireInfo: Pointer[VkAcquireNextImageInfoKHR, Read, Opaque],
      pImageIndex: Pointer[uint32_t, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkAcquireNextImageKHR(
      device: VkDevice,
      swapchain: VkSwapchainKHR,
      timeout: uint64_t,
      semaphore: VkSemaphore,
      fence: VkFence,
      pImageIndex: Pointer[uint32_t, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkAcquirePerformanceConfigurationINTEL(
      device: VkDevice,
      pAcquireInfo: Pointer[VkPerformanceConfigurationAcquireInfoINTEL, Read, Opaque],
      pConfiguration: Pointer[VkPerformanceConfigurationINTEL, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkAcquireProfilingLockKHR(
      device: VkDevice,
      pInfo: Pointer[VkAcquireProfilingLockInfoKHR, Read, Opaque]
  ): VkResult = unsupported
  def vkAllocateCommandBuffers(
      device: VkDevice,
      pAllocateInfo: Pointer[VkCommandBufferAllocateInfo, Read, Opaque],
      pCommandBuffers: Pointer[VkCommandBuffer, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkAllocateDescriptorSets(
      device: VkDevice,
      pAllocateInfo: Pointer[VkDescriptorSetAllocateInfo, Read, Opaque],
      pDescriptorSets: Pointer[VkDescriptorSet, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkAllocateMemory(
      device: VkDevice,
      pAllocateInfo: Pointer[VkMemoryAllocateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pMemory: Pointer[VkDeviceMemory, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkAntiLagUpdateAMD(device: VkDevice, pData: Pointer[VkAntiLagDataAMD, Read, Opaque]): Unit = unsupported
  def vkBeginCommandBuffer(
      commandBuffer: VkCommandBuffer,
      pBeginInfo: Pointer[VkCommandBufferBeginInfo, Read, Opaque]
  ): VkResult = unsupported
  def vkBindAccelerationStructureMemoryNV(
      device: VkDevice,
      bindInfoCount: uint32_t,
      pBindInfos: Pointer[VkBindAccelerationStructureMemoryInfoNV, Read, Opaque]
  ): VkResult = unsupported
  def vkBindBufferMemory(
      device: VkDevice,
      buffer: VkBuffer,
      memory: VkDeviceMemory,
      memoryOffset: VkDeviceSize
  ): VkResult = unsupported
  def vkBindBufferMemory2(
      device: VkDevice,
      bindInfoCount: uint32_t,
      pBindInfos: Pointer[VkBindBufferMemoryInfo, Read, Opaque]
  ): VkResult = unsupported
  def vkBindBufferMemory2KHR(
      device: VkDevice,
      bindInfoCount: uint32_t,
      pBindInfos: Pointer[VkBindBufferMemoryInfo, Read, Opaque]
  ): VkResult = unsupported
  def vkBindDataGraphPipelineSessionMemoryARM(
      device: VkDevice,
      bindInfoCount: uint32_t,
      pBindInfos: Pointer[VkBindDataGraphPipelineSessionMemoryInfoARM, Read, Opaque]
  ): VkResult = unsupported
  def vkBindImageMemory(
      device: VkDevice,
      image: VkImage,
      memory: VkDeviceMemory,
      memoryOffset: VkDeviceSize
  ): VkResult = unsupported
  def vkBindImageMemory2(
      device: VkDevice,
      bindInfoCount: uint32_t,
      pBindInfos: Pointer[VkBindImageMemoryInfo, Read, Opaque]
  ): VkResult = unsupported
  def vkBindImageMemory2KHR(
      device: VkDevice,
      bindInfoCount: uint32_t,
      pBindInfos: Pointer[VkBindImageMemoryInfo, Read, Opaque]
  ): VkResult = unsupported
  def vkBindOpticalFlowSessionImageNV(
      device: VkDevice,
      session: VkOpticalFlowSessionNV,
      bindingPoint: VkOpticalFlowSessionBindingPointNV,
      view: VkImageView,
      layout: VkImageLayout
  ): VkResult = unsupported
  def vkBindTensorMemoryARM(
      device: VkDevice,
      bindInfoCount: uint32_t,
      pBindInfos: Pointer[VkBindTensorMemoryInfoARM, Read, Opaque]
  ): VkResult = unsupported
  def vkBindVideoSessionMemoryKHR(
      device: VkDevice,
      videoSession: VkVideoSessionKHR,
      bindSessionMemoryInfoCount: uint32_t,
      pBindSessionMemoryInfos: Pointer[VkBindVideoSessionMemoryInfoKHR, Read, Opaque]
  ): VkResult = unsupported
  def vkBuildAccelerationStructuresKHR(
      device: VkDevice,
      deferredOperation: VkDeferredOperationKHR,
      infoCount: uint32_t,
      pInfos: Pointer[VkAccelerationStructureBuildGeometryInfoKHR, Read, Opaque],
      ppBuildRangeInfos: Pointer[Pointer[VkAccelerationStructureBuildRangeInfoKHR, Read, Opaque], Read, Opaque]
  ): VkResult = unsupported
  def vkBuildMicromapsEXT(
      device: VkDevice,
      deferredOperation: VkDeferredOperationKHR,
      infoCount: uint32_t,
      pInfos: Pointer[VkMicromapBuildInfoEXT, Read, Opaque]
  ): VkResult = unsupported
  def vkCmdBeginConditionalRenderingEXT(
      commandBuffer: VkCommandBuffer,
      pConditionalRenderingBegin: Pointer[VkConditionalRenderingBeginInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBeginCustomResolveEXT(
      commandBuffer: VkCommandBuffer,
      pBeginCustomResolveInfo: Pointer[VkBeginCustomResolveInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBeginDebugUtilsLabelEXT(
      commandBuffer: VkCommandBuffer,
      pLabelInfo: Pointer[VkDebugUtilsLabelEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBeginPerTileExecutionQCOM(
      commandBuffer: VkCommandBuffer,
      pPerTileBeginInfo: Pointer[VkPerTileBeginInfoQCOM, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBeginQuery(
      commandBuffer: VkCommandBuffer,
      queryPool: VkQueryPool,
      query: uint32_t,
      flags: VkQueryControlFlags
  ): Unit = unsupported
  def vkCmdBeginQueryIndexedEXT(
      commandBuffer: VkCommandBuffer,
      queryPool: VkQueryPool,
      query: uint32_t,
      flags: VkQueryControlFlags,
      index: uint32_t
  ): Unit = unsupported
  def vkCmdBeginRenderPass(
      commandBuffer: VkCommandBuffer,
      pRenderPassBegin: Pointer[VkRenderPassBeginInfo, Read, Opaque],
      contents: VkSubpassContents
  ): Unit = unsupported
  def vkCmdBeginRenderPass2(
      commandBuffer: VkCommandBuffer,
      pRenderPassBegin: Pointer[VkRenderPassBeginInfo, Read, Opaque],
      pSubpassBeginInfo: Pointer[VkSubpassBeginInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBeginRenderPass2KHR(
      commandBuffer: VkCommandBuffer,
      pRenderPassBegin: Pointer[VkRenderPassBeginInfo, Read, Opaque],
      pSubpassBeginInfo: Pointer[VkSubpassBeginInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBeginRendering(
      commandBuffer: VkCommandBuffer,
      pRenderingInfo: Pointer[VkRenderingInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBeginRenderingKHR(
      commandBuffer: VkCommandBuffer,
      pRenderingInfo: Pointer[VkRenderingInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBeginTransformFeedbackEXT(
      commandBuffer: VkCommandBuffer,
      firstCounterBuffer: uint32_t,
      counterBufferCount: uint32_t,
      pCounterBuffers: Pointer[VkBuffer, Read, Opaque],
      pCounterBufferOffsets: Pointer[VkDeviceSize, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBeginVideoCodingKHR(
      commandBuffer: VkCommandBuffer,
      pBeginInfo: Pointer[VkVideoBeginCodingInfoKHR, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBindDescriptorBufferEmbeddedSamplers2EXT(
      commandBuffer: VkCommandBuffer,
      pBindDescriptorBufferEmbeddedSamplersInfo: Pointer[VkBindDescriptorBufferEmbeddedSamplersInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBindDescriptorBufferEmbeddedSamplersEXT(
      commandBuffer: VkCommandBuffer,
      pipelineBindPoint: VkPipelineBindPoint,
      layout: VkPipelineLayout,
      set: uint32_t
  ): Unit = unsupported
  def vkCmdBindDescriptorBuffersEXT(
      commandBuffer: VkCommandBuffer,
      bufferCount: uint32_t,
      pBindingInfos: Pointer[VkDescriptorBufferBindingInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBindDescriptorSets(
      commandBuffer: VkCommandBuffer,
      pipelineBindPoint: VkPipelineBindPoint,
      layout: VkPipelineLayout,
      firstSet: uint32_t,
      descriptorSetCount: uint32_t,
      pDescriptorSets: Pointer[VkDescriptorSet, Read, Opaque],
      dynamicOffsetCount: uint32_t,
      pDynamicOffsets: Pointer[uint32_t, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBindDescriptorSets2(
      commandBuffer: VkCommandBuffer,
      pBindDescriptorSetsInfo: Pointer[VkBindDescriptorSetsInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBindDescriptorSets2KHR(
      commandBuffer: VkCommandBuffer,
      pBindDescriptorSetsInfo: Pointer[VkBindDescriptorSetsInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBindIndexBuffer(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      indexType: VkIndexType
  ): Unit = unsupported
  def vkCmdBindIndexBuffer2(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      size: VkDeviceSize,
      indexType: VkIndexType
  ): Unit = unsupported
  def vkCmdBindIndexBuffer2KHR(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      size: VkDeviceSize,
      indexType: VkIndexType
  ): Unit = unsupported
  def vkCmdBindInvocationMaskHUAWEI(
      commandBuffer: VkCommandBuffer,
      imageView: VkImageView,
      imageLayout: VkImageLayout
  ): Unit = unsupported
  def vkCmdBindPipeline(
      commandBuffer: VkCommandBuffer,
      pipelineBindPoint: VkPipelineBindPoint,
      pipeline: VkPipeline
  ): Unit = unsupported
  def vkCmdBindPipelineShaderGroupNV(
      commandBuffer: VkCommandBuffer,
      pipelineBindPoint: VkPipelineBindPoint,
      pipeline: VkPipeline,
      groupIndex: uint32_t
  ): Unit = unsupported
  def vkCmdBindResourceHeapEXT(
      commandBuffer: VkCommandBuffer,
      pBindInfo: Pointer[VkBindHeapInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBindSamplerHeapEXT(
      commandBuffer: VkCommandBuffer,
      pBindInfo: Pointer[VkBindHeapInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBindShadersEXT(
      commandBuffer: VkCommandBuffer,
      stageCount: uint32_t,
      pStages: Pointer[VkShaderStageFlagBits, Read, Opaque],
      pShaders: Pointer[VkShaderEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBindShadingRateImageNV(
      commandBuffer: VkCommandBuffer,
      imageView: VkImageView,
      imageLayout: VkImageLayout
  ): Unit = unsupported
  def vkCmdBindTileMemoryQCOM(
      commandBuffer: VkCommandBuffer,
      pTileMemoryBindInfo: Pointer[VkTileMemoryBindInfoQCOM, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBindTransformFeedbackBuffersEXT(
      commandBuffer: VkCommandBuffer,
      firstBinding: uint32_t,
      bindingCount: uint32_t,
      pBuffers: Pointer[VkBuffer, Read, Opaque],
      pOffsets: Pointer[VkDeviceSize, Read, Opaque],
      pSizes: Pointer[VkDeviceSize, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBindVertexBuffers(
      commandBuffer: VkCommandBuffer,
      firstBinding: uint32_t,
      bindingCount: uint32_t,
      pBuffers: Pointer[VkBuffer, Read, Opaque],
      pOffsets: Pointer[VkDeviceSize, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBindVertexBuffers2(
      commandBuffer: VkCommandBuffer,
      firstBinding: uint32_t,
      bindingCount: uint32_t,
      pBuffers: Pointer[VkBuffer, Read, Opaque],
      pOffsets: Pointer[VkDeviceSize, Read, Opaque],
      pSizes: Pointer[VkDeviceSize, Read, Opaque],
      pStrides: Pointer[VkDeviceSize, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBindVertexBuffers2EXT(
      commandBuffer: VkCommandBuffer,
      firstBinding: uint32_t,
      bindingCount: uint32_t,
      pBuffers: Pointer[VkBuffer, Read, Opaque],
      pOffsets: Pointer[VkDeviceSize, Read, Opaque],
      pSizes: Pointer[VkDeviceSize, Read, Opaque],
      pStrides: Pointer[VkDeviceSize, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBlitImage(
      commandBuffer: VkCommandBuffer,
      srcImage: VkImage,
      srcImageLayout: VkImageLayout,
      dstImage: VkImage,
      dstImageLayout: VkImageLayout,
      regionCount: uint32_t,
      pRegions: Pointer[VkImageBlit, Read, Opaque],
      filter: VkFilter
  ): Unit = unsupported
  def vkCmdBlitImage2(commandBuffer: VkCommandBuffer, pBlitImageInfo: Pointer[VkBlitImageInfo2, Read, Opaque]): Unit =
    unsupported
  def vkCmdBlitImage2KHR(
      commandBuffer: VkCommandBuffer,
      pBlitImageInfo: Pointer[VkBlitImageInfo2, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBuildAccelerationStructureNV(
      commandBuffer: VkCommandBuffer,
      pInfo: Pointer[VkAccelerationStructureInfoNV, Read, Opaque],
      instanceData: VkBuffer,
      instanceOffset: VkDeviceSize,
      update: VkBool32,
      dst: VkAccelerationStructureNV,
      src: VkAccelerationStructureNV,
      scratch: VkBuffer,
      scratchOffset: VkDeviceSize
  ): Unit = unsupported
  def vkCmdBuildAccelerationStructuresIndirectKHR(
      commandBuffer: VkCommandBuffer,
      infoCount: uint32_t,
      pInfos: Pointer[VkAccelerationStructureBuildGeometryInfoKHR, Read, Opaque],
      pIndirectDeviceAddresses: Pointer[VkDeviceAddress, Read, Opaque],
      pIndirectStrides: Pointer[uint32_t, Read, Opaque],
      ppMaxPrimitiveCounts: Pointer[Pointer[uint32_t, Read, Opaque], Read, Opaque]
  ): Unit = unsupported
  def vkCmdBuildAccelerationStructuresKHR(
      commandBuffer: VkCommandBuffer,
      infoCount: uint32_t,
      pInfos: Pointer[VkAccelerationStructureBuildGeometryInfoKHR, Read, Opaque],
      ppBuildRangeInfos: Pointer[Pointer[VkAccelerationStructureBuildRangeInfoKHR, Read, Opaque], Read, Opaque]
  ): Unit = unsupported
  def vkCmdBuildClusterAccelerationStructureIndirectNV(
      commandBuffer: VkCommandBuffer,
      pCommandInfos: Pointer[VkClusterAccelerationStructureCommandsInfoNV, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBuildMicromapsEXT(
      commandBuffer: VkCommandBuffer,
      infoCount: uint32_t,
      pInfos: Pointer[VkMicromapBuildInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdBuildPartitionedAccelerationStructuresNV(
      commandBuffer: VkCommandBuffer,
      pBuildInfo: Pointer[VkBuildPartitionedAccelerationStructureInfoNV, Read, Opaque]
  ): Unit = unsupported
  def vkCmdClearAttachments(
      commandBuffer: VkCommandBuffer,
      attachmentCount: uint32_t,
      pAttachments: Pointer[VkClearAttachment, Read, Opaque],
      rectCount: uint32_t,
      pRects: Pointer[VkClearRect, Read, Opaque]
  ): Unit = unsupported
  def vkCmdClearColorImage(
      commandBuffer: VkCommandBuffer,
      image: VkImage,
      imageLayout: VkImageLayout,
      pColor: Pointer[VkClearColorValue, Read, Opaque],
      rangeCount: uint32_t,
      pRanges: Pointer[VkImageSubresourceRange, Read, Opaque]
  ): Unit = unsupported
  def vkCmdClearDepthStencilImage(
      commandBuffer: VkCommandBuffer,
      image: VkImage,
      imageLayout: VkImageLayout,
      pDepthStencil: Pointer[VkClearDepthStencilValue, Read, Opaque],
      rangeCount: uint32_t,
      pRanges: Pointer[VkImageSubresourceRange, Read, Opaque]
  ): Unit = unsupported
  def vkCmdControlVideoCodingKHR(
      commandBuffer: VkCommandBuffer,
      pCodingControlInfo: Pointer[VkVideoCodingControlInfoKHR, Read, Opaque]
  ): Unit = unsupported
  def vkCmdConvertCooperativeVectorMatrixNV(
      commandBuffer: VkCommandBuffer,
      infoCount: uint32_t,
      pInfos: Pointer[VkConvertCooperativeVectorMatrixInfoNV, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyAccelerationStructureKHR(
      commandBuffer: VkCommandBuffer,
      pInfo: Pointer[VkCopyAccelerationStructureInfoKHR, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyAccelerationStructureNV(
      commandBuffer: VkCommandBuffer,
      dst: VkAccelerationStructureNV,
      src: VkAccelerationStructureNV,
      mode: VkCopyAccelerationStructureModeKHR
  ): Unit = unsupported
  def vkCmdCopyAccelerationStructureToMemoryKHR(
      commandBuffer: VkCommandBuffer,
      pInfo: Pointer[VkCopyAccelerationStructureToMemoryInfoKHR, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyBuffer(
      commandBuffer: VkCommandBuffer,
      srcBuffer: VkBuffer,
      dstBuffer: VkBuffer,
      regionCount: uint32_t,
      pRegions: Pointer[VkBufferCopy, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyBuffer2(
      commandBuffer: VkCommandBuffer,
      pCopyBufferInfo: Pointer[VkCopyBufferInfo2, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyBuffer2KHR(
      commandBuffer: VkCommandBuffer,
      pCopyBufferInfo: Pointer[VkCopyBufferInfo2, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyBufferToImage(
      commandBuffer: VkCommandBuffer,
      srcBuffer: VkBuffer,
      dstImage: VkImage,
      dstImageLayout: VkImageLayout,
      regionCount: uint32_t,
      pRegions: Pointer[VkBufferImageCopy, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyBufferToImage2(
      commandBuffer: VkCommandBuffer,
      pCopyBufferToImageInfo: Pointer[VkCopyBufferToImageInfo2, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyBufferToImage2KHR(
      commandBuffer: VkCommandBuffer,
      pCopyBufferToImageInfo: Pointer[VkCopyBufferToImageInfo2, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyImage(
      commandBuffer: VkCommandBuffer,
      srcImage: VkImage,
      srcImageLayout: VkImageLayout,
      dstImage: VkImage,
      dstImageLayout: VkImageLayout,
      regionCount: uint32_t,
      pRegions: Pointer[VkImageCopy, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyImage2(commandBuffer: VkCommandBuffer, pCopyImageInfo: Pointer[VkCopyImageInfo2, Read, Opaque]): Unit =
    unsupported
  def vkCmdCopyImage2KHR(
      commandBuffer: VkCommandBuffer,
      pCopyImageInfo: Pointer[VkCopyImageInfo2, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyImageToBuffer(
      commandBuffer: VkCommandBuffer,
      srcImage: VkImage,
      srcImageLayout: VkImageLayout,
      dstBuffer: VkBuffer,
      regionCount: uint32_t,
      pRegions: Pointer[VkBufferImageCopy, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyImageToBuffer2(
      commandBuffer: VkCommandBuffer,
      pCopyImageToBufferInfo: Pointer[VkCopyImageToBufferInfo2, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyImageToBuffer2KHR(
      commandBuffer: VkCommandBuffer,
      pCopyImageToBufferInfo: Pointer[VkCopyImageToBufferInfo2, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyMemoryIndirectKHR(
      commandBuffer: VkCommandBuffer,
      pCopyMemoryIndirectInfo: Pointer[VkCopyMemoryIndirectInfoKHR, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyMemoryIndirectNV(
      commandBuffer: VkCommandBuffer,
      copyBufferAddress: VkDeviceAddress,
      copyCount: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdCopyMemoryToAccelerationStructureKHR(
      commandBuffer: VkCommandBuffer,
      pInfo: Pointer[VkCopyMemoryToAccelerationStructureInfoKHR, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyMemoryToImageIndirectKHR(
      commandBuffer: VkCommandBuffer,
      pCopyMemoryToImageIndirectInfo: Pointer[VkCopyMemoryToImageIndirectInfoKHR, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyMemoryToImageIndirectNV(
      commandBuffer: VkCommandBuffer,
      copyBufferAddress: VkDeviceAddress,
      copyCount: uint32_t,
      stride: uint32_t,
      dstImage: VkImage,
      dstImageLayout: VkImageLayout,
      pImageSubresources: Pointer[VkImageSubresourceLayers, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyMemoryToMicromapEXT(
      commandBuffer: VkCommandBuffer,
      pInfo: Pointer[VkCopyMemoryToMicromapInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyMicromapEXT(commandBuffer: VkCommandBuffer, pInfo: Pointer[VkCopyMicromapInfoEXT, Read, Opaque]): Unit =
    unsupported
  def vkCmdCopyMicromapToMemoryEXT(
      commandBuffer: VkCommandBuffer,
      pInfo: Pointer[VkCopyMicromapToMemoryInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCopyQueryPoolResults(
      commandBuffer: VkCommandBuffer,
      queryPool: VkQueryPool,
      firstQuery: uint32_t,
      queryCount: uint32_t,
      dstBuffer: VkBuffer,
      dstOffset: VkDeviceSize,
      stride: VkDeviceSize,
      flags: VkQueryResultFlags
  ): Unit = unsupported
  def vkCmdCopyTensorARM(
      commandBuffer: VkCommandBuffer,
      pCopyTensorInfo: Pointer[VkCopyTensorInfoARM, Read, Opaque]
  ): Unit = unsupported
  def vkCmdCuLaunchKernelNVX(
      commandBuffer: VkCommandBuffer,
      pLaunchInfo: Pointer[VkCuLaunchInfoNVX, Read, Opaque]
  ): Unit = unsupported
  def vkCmdDebugMarkerBeginEXT(
      commandBuffer: VkCommandBuffer,
      pMarkerInfo: Pointer[VkDebugMarkerMarkerInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdDebugMarkerEndEXT(commandBuffer: VkCommandBuffer): Unit = unsupported
  def vkCmdDebugMarkerInsertEXT(
      commandBuffer: VkCommandBuffer,
      pMarkerInfo: Pointer[VkDebugMarkerMarkerInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdDecodeVideoKHR(
      commandBuffer: VkCommandBuffer,
      pDecodeInfo: Pointer[VkVideoDecodeInfoKHR, Read, Opaque]
  ): Unit = unsupported
  def vkCmdDecompressMemoryEXT(
      commandBuffer: VkCommandBuffer,
      pDecompressMemoryInfoEXT: Pointer[VkDecompressMemoryInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdDecompressMemoryIndirectCountEXT(
      commandBuffer: VkCommandBuffer,
      decompressionMethod: VkMemoryDecompressionMethodFlagsEXT,
      indirectCommandsAddress: VkDeviceAddress,
      indirectCommandsCountAddress: VkDeviceAddress,
      maxDecompressionCount: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDecompressMemoryIndirectCountNV(
      commandBuffer: VkCommandBuffer,
      indirectCommandsAddress: VkDeviceAddress,
      indirectCommandsCountAddress: VkDeviceAddress,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDecompressMemoryNV(
      commandBuffer: VkCommandBuffer,
      decompressRegionCount: uint32_t,
      pDecompressMemoryRegions: Pointer[VkDecompressMemoryRegionNV, Read, Opaque]
  ): Unit = unsupported
  def vkCmdDispatch(
      commandBuffer: VkCommandBuffer,
      groupCountX: uint32_t,
      groupCountY: uint32_t,
      groupCountZ: uint32_t
  ): Unit = unsupported
  def vkCmdDispatchBase(
      commandBuffer: VkCommandBuffer,
      baseGroupX: uint32_t,
      baseGroupY: uint32_t,
      baseGroupZ: uint32_t,
      groupCountX: uint32_t,
      groupCountY: uint32_t,
      groupCountZ: uint32_t
  ): Unit = unsupported
  def vkCmdDispatchBaseKHR(
      commandBuffer: VkCommandBuffer,
      baseGroupX: uint32_t,
      baseGroupY: uint32_t,
      baseGroupZ: uint32_t,
      groupCountX: uint32_t,
      groupCountY: uint32_t,
      groupCountZ: uint32_t
  ): Unit = unsupported
  def vkCmdDispatchDataGraphARM(
      commandBuffer: VkCommandBuffer,
      session: VkDataGraphPipelineSessionARM,
      pInfo: Pointer[VkDataGraphPipelineDispatchInfoARM, Read, Opaque]
  ): Unit = unsupported
  def vkCmdDispatchIndirect(commandBuffer: VkCommandBuffer, buffer: VkBuffer, offset: VkDeviceSize): Unit = unsupported
  def vkCmdDispatchTileQCOM(
      commandBuffer: VkCommandBuffer,
      pDispatchTileInfo: Pointer[VkDispatchTileInfoQCOM, Read, Opaque]
  ): Unit = unsupported
  def vkCmdDraw(
      commandBuffer: VkCommandBuffer,
      vertexCount: uint32_t,
      instanceCount: uint32_t,
      firstVertex: uint32_t,
      firstInstance: uint32_t
  ): Unit = unsupported
  def vkCmdDrawClusterHUAWEI(
      commandBuffer: VkCommandBuffer,
      groupCountX: uint32_t,
      groupCountY: uint32_t,
      groupCountZ: uint32_t
  ): Unit = unsupported
  def vkCmdDrawClusterIndirectHUAWEI(commandBuffer: VkCommandBuffer, buffer: VkBuffer, offset: VkDeviceSize): Unit =
    unsupported
  def vkCmdDrawIndexed(
      commandBuffer: VkCommandBuffer,
      indexCount: uint32_t,
      instanceCount: uint32_t,
      firstIndex: uint32_t,
      vertexOffset: int32_t,
      firstInstance: uint32_t
  ): Unit = unsupported
  def vkCmdDrawIndexedIndirect(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      drawCount: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDrawIndexedIndirectCount(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      countBuffer: VkBuffer,
      countBufferOffset: VkDeviceSize,
      maxDrawCount: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDrawIndexedIndirectCountAMD(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      countBuffer: VkBuffer,
      countBufferOffset: VkDeviceSize,
      maxDrawCount: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDrawIndexedIndirectCountKHR(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      countBuffer: VkBuffer,
      countBufferOffset: VkDeviceSize,
      maxDrawCount: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDrawIndirect(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      drawCount: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDrawIndirectByteCountEXT(
      commandBuffer: VkCommandBuffer,
      instanceCount: uint32_t,
      firstInstance: uint32_t,
      counterBuffer: VkBuffer,
      counterBufferOffset: VkDeviceSize,
      counterOffset: uint32_t,
      vertexStride: uint32_t
  ): Unit = unsupported
  def vkCmdDrawIndirectCount(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      countBuffer: VkBuffer,
      countBufferOffset: VkDeviceSize,
      maxDrawCount: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDrawIndirectCountAMD(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      countBuffer: VkBuffer,
      countBufferOffset: VkDeviceSize,
      maxDrawCount: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDrawIndirectCountKHR(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      countBuffer: VkBuffer,
      countBufferOffset: VkDeviceSize,
      maxDrawCount: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDrawMeshTasksEXT(
      commandBuffer: VkCommandBuffer,
      groupCountX: uint32_t,
      groupCountY: uint32_t,
      groupCountZ: uint32_t
  ): Unit = unsupported
  def vkCmdDrawMeshTasksIndirectCountEXT(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      countBuffer: VkBuffer,
      countBufferOffset: VkDeviceSize,
      maxDrawCount: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDrawMeshTasksIndirectCountNV(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      countBuffer: VkBuffer,
      countBufferOffset: VkDeviceSize,
      maxDrawCount: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDrawMeshTasksIndirectEXT(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      drawCount: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDrawMeshTasksIndirectNV(
      commandBuffer: VkCommandBuffer,
      buffer: VkBuffer,
      offset: VkDeviceSize,
      drawCount: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDrawMeshTasksNV(commandBuffer: VkCommandBuffer, taskCount: uint32_t, firstTask: uint32_t): Unit = unsupported
  def vkCmdDrawMultiEXT(
      commandBuffer: VkCommandBuffer,
      drawCount: uint32_t,
      pVertexInfo: Pointer[VkMultiDrawInfoEXT, Read, Opaque],
      instanceCount: uint32_t,
      firstInstance: uint32_t,
      stride: uint32_t
  ): Unit = unsupported
  def vkCmdDrawMultiIndexedEXT(
      commandBuffer: VkCommandBuffer,
      drawCount: uint32_t,
      pIndexInfo: Pointer[VkMultiDrawIndexedInfoEXT, Read, Opaque],
      instanceCount: uint32_t,
      firstInstance: uint32_t,
      stride: uint32_t,
      pVertexOffset: Pointer[int32_t, Read, Opaque]
  ): Unit = unsupported
  def vkCmdEncodeVideoKHR(
      commandBuffer: VkCommandBuffer,
      pEncodeInfo: Pointer[VkVideoEncodeInfoKHR, Read, Opaque]
  ): Unit = unsupported
  def vkCmdEndConditionalRenderingEXT(commandBuffer: VkCommandBuffer): Unit = unsupported
  def vkCmdEndDebugUtilsLabelEXT(commandBuffer: VkCommandBuffer): Unit = unsupported
  def vkCmdEndPerTileExecutionQCOM(
      commandBuffer: VkCommandBuffer,
      pPerTileEndInfo: Pointer[VkPerTileEndInfoQCOM, Read, Opaque]
  ): Unit = unsupported
  def vkCmdEndQuery(commandBuffer: VkCommandBuffer, queryPool: VkQueryPool, query: uint32_t): Unit = unsupported
  def vkCmdEndQueryIndexedEXT(
      commandBuffer: VkCommandBuffer,
      queryPool: VkQueryPool,
      query: uint32_t,
      index: uint32_t
  ): Unit = unsupported
  def vkCmdEndRenderPass(commandBuffer: VkCommandBuffer): Unit = unsupported
  def vkCmdEndRenderPass2(
      commandBuffer: VkCommandBuffer,
      pSubpassEndInfo: Pointer[VkSubpassEndInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdEndRenderPass2KHR(
      commandBuffer: VkCommandBuffer,
      pSubpassEndInfo: Pointer[VkSubpassEndInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdEndRendering(commandBuffer: VkCommandBuffer): Unit = unsupported
  def vkCmdEndRendering2EXT(
      commandBuffer: VkCommandBuffer,
      pRenderingEndInfo: Pointer[VkRenderingEndInfoKHR, Read, Opaque]
  ): Unit = unsupported
  def vkCmdEndRendering2KHR(
      commandBuffer: VkCommandBuffer,
      pRenderingEndInfo: Pointer[VkRenderingEndInfoKHR, Read, Opaque]
  ): Unit = unsupported
  def vkCmdEndRenderingKHR(commandBuffer: VkCommandBuffer): Unit = unsupported
  def vkCmdEndTransformFeedbackEXT(
      commandBuffer: VkCommandBuffer,
      firstCounterBuffer: uint32_t,
      counterBufferCount: uint32_t,
      pCounterBuffers: Pointer[VkBuffer, Read, Opaque],
      pCounterBufferOffsets: Pointer[VkDeviceSize, Read, Opaque]
  ): Unit = unsupported
  def vkCmdEndVideoCodingKHR(
      commandBuffer: VkCommandBuffer,
      pEndCodingInfo: Pointer[VkVideoEndCodingInfoKHR, Read, Opaque]
  ): Unit = unsupported
  def vkCmdExecuteCommands(
      commandBuffer: VkCommandBuffer,
      commandBufferCount: uint32_t,
      pCommandBuffers: Pointer[VkCommandBuffer, Read, Opaque]
  ): Unit = unsupported
  def vkCmdExecuteGeneratedCommandsEXT(
      commandBuffer: VkCommandBuffer,
      isPreprocessed: VkBool32,
      pGeneratedCommandsInfo: Pointer[VkGeneratedCommandsInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdExecuteGeneratedCommandsNV(
      commandBuffer: VkCommandBuffer,
      isPreprocessed: VkBool32,
      pGeneratedCommandsInfo: Pointer[VkGeneratedCommandsInfoNV, Read, Opaque]
  ): Unit = unsupported
  def vkCmdFillBuffer(
      commandBuffer: VkCommandBuffer,
      dstBuffer: VkBuffer,
      dstOffset: VkDeviceSize,
      size: VkDeviceSize,
      data: uint32_t
  ): Unit = unsupported
  def vkCmdInsertDebugUtilsLabelEXT(
      commandBuffer: VkCommandBuffer,
      pLabelInfo: Pointer[VkDebugUtilsLabelEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdNextSubpass(commandBuffer: VkCommandBuffer, contents: VkSubpassContents): Unit = unsupported
  def vkCmdNextSubpass2(
      commandBuffer: VkCommandBuffer,
      pSubpassBeginInfo: Pointer[VkSubpassBeginInfo, Read, Opaque],
      pSubpassEndInfo: Pointer[VkSubpassEndInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdNextSubpass2KHR(
      commandBuffer: VkCommandBuffer,
      pSubpassBeginInfo: Pointer[VkSubpassBeginInfo, Read, Opaque],
      pSubpassEndInfo: Pointer[VkSubpassEndInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdOpticalFlowExecuteNV(
      commandBuffer: VkCommandBuffer,
      session: VkOpticalFlowSessionNV,
      pExecuteInfo: Pointer[VkOpticalFlowExecuteInfoNV, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPipelineBarrier(
      commandBuffer: VkCommandBuffer,
      srcStageMask: VkPipelineStageFlags,
      dstStageMask: VkPipelineStageFlags,
      dependencyFlags: VkDependencyFlags,
      memoryBarrierCount: uint32_t,
      pMemoryBarriers: Pointer[VkMemoryBarrier, Read, Opaque],
      bufferMemoryBarrierCount: uint32_t,
      pBufferMemoryBarriers: Pointer[VkBufferMemoryBarrier, Read, Opaque],
      imageMemoryBarrierCount: uint32_t,
      pImageMemoryBarriers: Pointer[VkImageMemoryBarrier, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPipelineBarrier2(
      commandBuffer: VkCommandBuffer,
      pDependencyInfo: Pointer[VkDependencyInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPipelineBarrier2KHR(
      commandBuffer: VkCommandBuffer,
      pDependencyInfo: Pointer[VkDependencyInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPreprocessGeneratedCommandsEXT(
      commandBuffer: VkCommandBuffer,
      pGeneratedCommandsInfo: Pointer[VkGeneratedCommandsInfoEXT, Read, Opaque],
      stateCommandBuffer: VkCommandBuffer
  ): Unit = unsupported
  def vkCmdPreprocessGeneratedCommandsNV(
      commandBuffer: VkCommandBuffer,
      pGeneratedCommandsInfo: Pointer[VkGeneratedCommandsInfoNV, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPushConstants(
      commandBuffer: VkCommandBuffer,
      layout: VkPipelineLayout,
      stageFlags: VkShaderStageFlags,
      offset: uint32_t,
      size: uint32_t,
      pValues: Pointer[CVoid, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPushConstants2(
      commandBuffer: VkCommandBuffer,
      pPushConstantsInfo: Pointer[VkPushConstantsInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPushConstants2KHR(
      commandBuffer: VkCommandBuffer,
      pPushConstantsInfo: Pointer[VkPushConstantsInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPushDataEXT(commandBuffer: VkCommandBuffer, pPushDataInfo: Pointer[VkPushDataInfoEXT, Read, Opaque]): Unit =
    unsupported
  def vkCmdPushDescriptorSet(
      commandBuffer: VkCommandBuffer,
      pipelineBindPoint: VkPipelineBindPoint,
      layout: VkPipelineLayout,
      set: uint32_t,
      descriptorWriteCount: uint32_t,
      pDescriptorWrites: Pointer[VkWriteDescriptorSet, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPushDescriptorSet2(
      commandBuffer: VkCommandBuffer,
      pPushDescriptorSetInfo: Pointer[VkPushDescriptorSetInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPushDescriptorSet2KHR(
      commandBuffer: VkCommandBuffer,
      pPushDescriptorSetInfo: Pointer[VkPushDescriptorSetInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPushDescriptorSetKHR(
      commandBuffer: VkCommandBuffer,
      pipelineBindPoint: VkPipelineBindPoint,
      layout: VkPipelineLayout,
      set: uint32_t,
      descriptorWriteCount: uint32_t,
      pDescriptorWrites: Pointer[VkWriteDescriptorSet, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPushDescriptorSetWithTemplate(
      commandBuffer: VkCommandBuffer,
      descriptorUpdateTemplate: VkDescriptorUpdateTemplate,
      layout: VkPipelineLayout,
      set: uint32_t,
      pData: Pointer[CVoid, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPushDescriptorSetWithTemplate2(
      commandBuffer: VkCommandBuffer,
      pPushDescriptorSetWithTemplateInfo: Pointer[VkPushDescriptorSetWithTemplateInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPushDescriptorSetWithTemplate2KHR(
      commandBuffer: VkCommandBuffer,
      pPushDescriptorSetWithTemplateInfo: Pointer[VkPushDescriptorSetWithTemplateInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdPushDescriptorSetWithTemplateKHR(
      commandBuffer: VkCommandBuffer,
      descriptorUpdateTemplate: VkDescriptorUpdateTemplate,
      layout: VkPipelineLayout,
      set: uint32_t,
      pData: Pointer[CVoid, Read, Opaque]
  ): Unit = unsupported
  def vkCmdResetEvent(commandBuffer: VkCommandBuffer, event: VkEvent, stageMask: VkPipelineStageFlags): Unit =
    unsupported
  def vkCmdResetEvent2(commandBuffer: VkCommandBuffer, event: VkEvent, stageMask: VkPipelineStageFlags2): Unit =
    unsupported
  def vkCmdResetEvent2KHR(commandBuffer: VkCommandBuffer, event: VkEvent, stageMask: VkPipelineStageFlags2): Unit =
    unsupported
  def vkCmdResetQueryPool(
      commandBuffer: VkCommandBuffer,
      queryPool: VkQueryPool,
      firstQuery: uint32_t,
      queryCount: uint32_t
  ): Unit = unsupported
  def vkCmdResolveImage(
      commandBuffer: VkCommandBuffer,
      srcImage: VkImage,
      srcImageLayout: VkImageLayout,
      dstImage: VkImage,
      dstImageLayout: VkImageLayout,
      regionCount: uint32_t,
      pRegions: Pointer[VkImageResolve, Read, Opaque]
  ): Unit = unsupported
  def vkCmdResolveImage2(
      commandBuffer: VkCommandBuffer,
      pResolveImageInfo: Pointer[VkResolveImageInfo2, Read, Opaque]
  ): Unit = unsupported
  def vkCmdResolveImage2KHR(
      commandBuffer: VkCommandBuffer,
      pResolveImageInfo: Pointer[VkResolveImageInfo2, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetAlphaToCoverageEnableEXT(commandBuffer: VkCommandBuffer, alphaToCoverageEnable: VkBool32): Unit =
    unsupported
  def vkCmdSetAlphaToOneEnableEXT(commandBuffer: VkCommandBuffer, alphaToOneEnable: VkBool32): Unit = unsupported
  def vkCmdSetAttachmentFeedbackLoopEnableEXT(commandBuffer: VkCommandBuffer, aspectMask: VkImageAspectFlags): Unit =
    unsupported
  def vkCmdSetBlendConstants(commandBuffer: VkCommandBuffer, blendConstants: Pointer[Float, Read, Opaque]): Unit =
    unsupported
  def vkCmdSetCheckpointNV(commandBuffer: VkCommandBuffer, pCheckpointMarker: Pointer[CVoid, Read, Opaque]): Unit =
    unsupported
  def vkCmdSetCoarseSampleOrderNV(
      commandBuffer: VkCommandBuffer,
      sampleOrderType: VkCoarseSampleOrderTypeNV,
      customSampleOrderCount: uint32_t,
      pCustomSampleOrders: Pointer[VkCoarseSampleOrderCustomNV, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetColorBlendAdvancedEXT(
      commandBuffer: VkCommandBuffer,
      firstAttachment: uint32_t,
      attachmentCount: uint32_t,
      pColorBlendAdvanced: Pointer[VkColorBlendAdvancedEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetColorBlendEnableEXT(
      commandBuffer: VkCommandBuffer,
      firstAttachment: uint32_t,
      attachmentCount: uint32_t,
      pColorBlendEnables: Pointer[VkBool32, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetColorBlendEquationEXT(
      commandBuffer: VkCommandBuffer,
      firstAttachment: uint32_t,
      attachmentCount: uint32_t,
      pColorBlendEquations: Pointer[VkColorBlendEquationEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetColorWriteEnableEXT(
      commandBuffer: VkCommandBuffer,
      attachmentCount: uint32_t,
      pColorWriteEnables: Pointer[VkBool32, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetColorWriteMaskEXT(
      commandBuffer: VkCommandBuffer,
      firstAttachment: uint32_t,
      attachmentCount: uint32_t,
      pColorWriteMasks: Pointer[VkColorComponentFlags, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetComputeOccupancyPriorityNV(
      commandBuffer: VkCommandBuffer,
      pParameters: Pointer[VkComputeOccupancyPriorityParametersNV, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetConservativeRasterizationModeEXT(
      commandBuffer: VkCommandBuffer,
      conservativeRasterizationMode: VkConservativeRasterizationModeEXT
  ): Unit = unsupported
  def vkCmdSetCoverageModulationModeNV(
      commandBuffer: VkCommandBuffer,
      coverageModulationMode: VkCoverageModulationModeNV
  ): Unit = unsupported
  def vkCmdSetCoverageModulationTableEnableNV(
      commandBuffer: VkCommandBuffer,
      coverageModulationTableEnable: VkBool32
  ): Unit = unsupported
  def vkCmdSetCoverageModulationTableNV(
      commandBuffer: VkCommandBuffer,
      coverageModulationTableCount: uint32_t,
      pCoverageModulationTable: Pointer[Float, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetCoverageReductionModeNV(
      commandBuffer: VkCommandBuffer,
      coverageReductionMode: VkCoverageReductionModeNV
  ): Unit = unsupported
  def vkCmdSetCoverageToColorEnableNV(commandBuffer: VkCommandBuffer, coverageToColorEnable: VkBool32): Unit =
    unsupported
  def vkCmdSetCoverageToColorLocationNV(commandBuffer: VkCommandBuffer, coverageToColorLocation: uint32_t): Unit =
    unsupported
  def vkCmdSetCullMode(commandBuffer: VkCommandBuffer, cullMode: VkCullModeFlags): Unit = unsupported
  def vkCmdSetCullModeEXT(commandBuffer: VkCommandBuffer, cullMode: VkCullModeFlags): Unit = unsupported
  def vkCmdSetDepthBias(
      commandBuffer: VkCommandBuffer,
      depthBiasConstantFactor: Float,
      depthBiasClamp: Float,
      depthBiasSlopeFactor: Float
  ): Unit = unsupported
  def vkCmdSetDepthBias2EXT(
      commandBuffer: VkCommandBuffer,
      pDepthBiasInfo: Pointer[VkDepthBiasInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetDepthBiasEnable(commandBuffer: VkCommandBuffer, depthBiasEnable: VkBool32): Unit = unsupported
  def vkCmdSetDepthBiasEnableEXT(commandBuffer: VkCommandBuffer, depthBiasEnable: VkBool32): Unit = unsupported
  def vkCmdSetDepthBounds(commandBuffer: VkCommandBuffer, minDepthBounds: Float, maxDepthBounds: Float): Unit =
    unsupported
  def vkCmdSetDepthBoundsTestEnable(commandBuffer: VkCommandBuffer, depthBoundsTestEnable: VkBool32): Unit = unsupported
  def vkCmdSetDepthBoundsTestEnableEXT(commandBuffer: VkCommandBuffer, depthBoundsTestEnable: VkBool32): Unit =
    unsupported
  def vkCmdSetDepthClampEnableEXT(commandBuffer: VkCommandBuffer, depthClampEnable: VkBool32): Unit = unsupported
  def vkCmdSetDepthClampRangeEXT(
      commandBuffer: VkCommandBuffer,
      depthClampMode: VkDepthClampModeEXT,
      pDepthClampRange: Pointer[VkDepthClampRangeEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetDepthClipEnableEXT(commandBuffer: VkCommandBuffer, depthClipEnable: VkBool32): Unit = unsupported
  def vkCmdSetDepthClipNegativeOneToOneEXT(commandBuffer: VkCommandBuffer, negativeOneToOne: VkBool32): Unit =
    unsupported
  def vkCmdSetDepthCompareOp(commandBuffer: VkCommandBuffer, depthCompareOp: VkCompareOp): Unit = unsupported
  def vkCmdSetDepthCompareOpEXT(commandBuffer: VkCommandBuffer, depthCompareOp: VkCompareOp): Unit = unsupported
  def vkCmdSetDepthTestEnable(commandBuffer: VkCommandBuffer, depthTestEnable: VkBool32): Unit = unsupported
  def vkCmdSetDepthTestEnableEXT(commandBuffer: VkCommandBuffer, depthTestEnable: VkBool32): Unit = unsupported
  def vkCmdSetDepthWriteEnable(commandBuffer: VkCommandBuffer, depthWriteEnable: VkBool32): Unit = unsupported
  def vkCmdSetDepthWriteEnableEXT(commandBuffer: VkCommandBuffer, depthWriteEnable: VkBool32): Unit = unsupported
  def vkCmdSetDescriptorBufferOffsets2EXT(
      commandBuffer: VkCommandBuffer,
      pSetDescriptorBufferOffsetsInfo: Pointer[VkSetDescriptorBufferOffsetsInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetDescriptorBufferOffsetsEXT(
      commandBuffer: VkCommandBuffer,
      pipelineBindPoint: VkPipelineBindPoint,
      layout: VkPipelineLayout,
      firstSet: uint32_t,
      setCount: uint32_t,
      pBufferIndices: Pointer[uint32_t, Read, Opaque],
      pOffsets: Pointer[VkDeviceSize, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetDeviceMask(commandBuffer: VkCommandBuffer, deviceMask: uint32_t): Unit = unsupported
  def vkCmdSetDeviceMaskKHR(commandBuffer: VkCommandBuffer, deviceMask: uint32_t): Unit = unsupported
  def vkCmdSetDiscardRectangleEXT(
      commandBuffer: VkCommandBuffer,
      firstDiscardRectangle: uint32_t,
      discardRectangleCount: uint32_t,
      pDiscardRectangles: Pointer[VkRect2D, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetDiscardRectangleEnableEXT(commandBuffer: VkCommandBuffer, discardRectangleEnable: VkBool32): Unit =
    unsupported
  def vkCmdSetDiscardRectangleModeEXT(
      commandBuffer: VkCommandBuffer,
      discardRectangleMode: VkDiscardRectangleModeEXT
  ): Unit = unsupported
  def vkCmdSetEvent(commandBuffer: VkCommandBuffer, event: VkEvent, stageMask: VkPipelineStageFlags): Unit = unsupported
  def vkCmdSetEvent2(
      commandBuffer: VkCommandBuffer,
      event: VkEvent,
      pDependencyInfo: Pointer[VkDependencyInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetEvent2KHR(
      commandBuffer: VkCommandBuffer,
      event: VkEvent,
      pDependencyInfo: Pointer[VkDependencyInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetExclusiveScissorEnableNV(
      commandBuffer: VkCommandBuffer,
      firstExclusiveScissor: uint32_t,
      exclusiveScissorCount: uint32_t,
      pExclusiveScissorEnables: Pointer[VkBool32, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetExclusiveScissorNV(
      commandBuffer: VkCommandBuffer,
      firstExclusiveScissor: uint32_t,
      exclusiveScissorCount: uint32_t,
      pExclusiveScissors: Pointer[VkRect2D, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetExtraPrimitiveOverestimationSizeEXT(
      commandBuffer: VkCommandBuffer,
      extraPrimitiveOverestimationSize: Float
  ): Unit = unsupported
  def vkCmdSetFragmentShadingRateEnumNV(
      commandBuffer: VkCommandBuffer,
      shadingRate: VkFragmentShadingRateNV,
      combinerOps: Pointer[VkFragmentShadingRateCombinerOpKHR, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetFragmentShadingRateKHR(
      commandBuffer: VkCommandBuffer,
      pFragmentSize: Pointer[VkExtent2D, Read, Opaque],
      combinerOps: Pointer[VkFragmentShadingRateCombinerOpKHR, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetFrontFace(commandBuffer: VkCommandBuffer, frontFace: VkFrontFace): Unit = unsupported
  def vkCmdSetFrontFaceEXT(commandBuffer: VkCommandBuffer, frontFace: VkFrontFace): Unit = unsupported
  def vkCmdSetLineRasterizationModeEXT(
      commandBuffer: VkCommandBuffer,
      lineRasterizationMode: VkLineRasterizationModeEXT
  ): Unit = unsupported
  def vkCmdSetLineStipple(
      commandBuffer: VkCommandBuffer,
      lineStippleFactor: uint32_t,
      lineStipplePattern: uint16_t
  ): Unit = unsupported
  def vkCmdSetLineStippleEXT(
      commandBuffer: VkCommandBuffer,
      lineStippleFactor: uint32_t,
      lineStipplePattern: uint16_t
  ): Unit = unsupported
  def vkCmdSetLineStippleEnableEXT(commandBuffer: VkCommandBuffer, stippledLineEnable: VkBool32): Unit = unsupported
  def vkCmdSetLineStippleKHR(
      commandBuffer: VkCommandBuffer,
      lineStippleFactor: uint32_t,
      lineStipplePattern: uint16_t
  ): Unit = unsupported
  def vkCmdSetLineWidth(commandBuffer: VkCommandBuffer, lineWidth: Float): Unit = unsupported
  def vkCmdSetLogicOpEXT(commandBuffer: VkCommandBuffer, logicOp: VkLogicOp): Unit = unsupported
  def vkCmdSetLogicOpEnableEXT(commandBuffer: VkCommandBuffer, logicOpEnable: VkBool32): Unit = unsupported
  def vkCmdSetPatchControlPointsEXT(commandBuffer: VkCommandBuffer, patchControlPoints: uint32_t): Unit = unsupported
  def vkCmdSetPerformanceMarkerINTEL(
      commandBuffer: VkCommandBuffer,
      pMarkerInfo: Pointer[VkPerformanceMarkerInfoINTEL, Read, Opaque]
  ): VkResult = unsupported
  def vkCmdSetPerformanceOverrideINTEL(
      commandBuffer: VkCommandBuffer,
      pOverrideInfo: Pointer[VkPerformanceOverrideInfoINTEL, Read, Opaque]
  ): VkResult = unsupported
  def vkCmdSetPerformanceStreamMarkerINTEL(
      commandBuffer: VkCommandBuffer,
      pMarkerInfo: Pointer[VkPerformanceStreamMarkerInfoINTEL, Read, Opaque]
  ): VkResult = unsupported
  def vkCmdSetPolygonModeEXT(commandBuffer: VkCommandBuffer, polygonMode: VkPolygonMode): Unit = unsupported
  def vkCmdSetPrimitiveRestartEnable(commandBuffer: VkCommandBuffer, primitiveRestartEnable: VkBool32): Unit =
    unsupported
  def vkCmdSetPrimitiveRestartEnableEXT(commandBuffer: VkCommandBuffer, primitiveRestartEnable: VkBool32): Unit =
    unsupported
  def vkCmdSetPrimitiveTopology(commandBuffer: VkCommandBuffer, primitiveTopology: VkPrimitiveTopology): Unit =
    unsupported
  def vkCmdSetPrimitiveTopologyEXT(commandBuffer: VkCommandBuffer, primitiveTopology: VkPrimitiveTopology): Unit =
    unsupported
  def vkCmdSetProvokingVertexModeEXT(
      commandBuffer: VkCommandBuffer,
      provokingVertexMode: VkProvokingVertexModeEXT
  ): Unit = unsupported
  def vkCmdSetRasterizationSamplesEXT(
      commandBuffer: VkCommandBuffer,
      rasterizationSamples: VkSampleCountFlagBits
  ): Unit = unsupported
  def vkCmdSetRasterizationStreamEXT(commandBuffer: VkCommandBuffer, rasterizationStream: uint32_t): Unit = unsupported
  def vkCmdSetRasterizerDiscardEnable(commandBuffer: VkCommandBuffer, rasterizerDiscardEnable: VkBool32): Unit =
    unsupported
  def vkCmdSetRasterizerDiscardEnableEXT(commandBuffer: VkCommandBuffer, rasterizerDiscardEnable: VkBool32): Unit =
    unsupported
  def vkCmdSetRayTracingPipelineStackSizeKHR(commandBuffer: VkCommandBuffer, pipelineStackSize: uint32_t): Unit =
    unsupported
  def vkCmdSetRenderingAttachmentLocations(
      commandBuffer: VkCommandBuffer,
      pLocationInfo: Pointer[VkRenderingAttachmentLocationInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetRenderingAttachmentLocationsKHR(
      commandBuffer: VkCommandBuffer,
      pLocationInfo: Pointer[VkRenderingAttachmentLocationInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetRenderingInputAttachmentIndices(
      commandBuffer: VkCommandBuffer,
      pInputAttachmentIndexInfo: Pointer[VkRenderingInputAttachmentIndexInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetRenderingInputAttachmentIndicesKHR(
      commandBuffer: VkCommandBuffer,
      pInputAttachmentIndexInfo: Pointer[VkRenderingInputAttachmentIndexInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetRepresentativeFragmentTestEnableNV(
      commandBuffer: VkCommandBuffer,
      representativeFragmentTestEnable: VkBool32
  ): Unit = unsupported
  def vkCmdSetSampleLocationsEXT(
      commandBuffer: VkCommandBuffer,
      pSampleLocationsInfo: Pointer[VkSampleLocationsInfoEXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetSampleLocationsEnableEXT(commandBuffer: VkCommandBuffer, sampleLocationsEnable: VkBool32): Unit =
    unsupported
  def vkCmdSetSampleMaskEXT(
      commandBuffer: VkCommandBuffer,
      samples: VkSampleCountFlagBits,
      pSampleMask: Pointer[VkSampleMask, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetScissor(
      commandBuffer: VkCommandBuffer,
      firstScissor: uint32_t,
      scissorCount: uint32_t,
      pScissors: Pointer[VkRect2D, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetScissorWithCount(
      commandBuffer: VkCommandBuffer,
      scissorCount: uint32_t,
      pScissors: Pointer[VkRect2D, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetScissorWithCountEXT(
      commandBuffer: VkCommandBuffer,
      scissorCount: uint32_t,
      pScissors: Pointer[VkRect2D, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetShadingRateImageEnableNV(commandBuffer: VkCommandBuffer, shadingRateImageEnable: VkBool32): Unit =
    unsupported
  def vkCmdSetStencilCompareMask(
      commandBuffer: VkCommandBuffer,
      faceMask: VkStencilFaceFlags,
      compareMask: uint32_t
  ): Unit = unsupported
  def vkCmdSetStencilOp(
      commandBuffer: VkCommandBuffer,
      faceMask: VkStencilFaceFlags,
      failOp: VkStencilOp,
      passOp: VkStencilOp,
      depthFailOp: VkStencilOp,
      compareOp: VkCompareOp
  ): Unit = unsupported
  def vkCmdSetStencilOpEXT(
      commandBuffer: VkCommandBuffer,
      faceMask: VkStencilFaceFlags,
      failOp: VkStencilOp,
      passOp: VkStencilOp,
      depthFailOp: VkStencilOp,
      compareOp: VkCompareOp
  ): Unit = unsupported
  def vkCmdSetStencilReference(
      commandBuffer: VkCommandBuffer,
      faceMask: VkStencilFaceFlags,
      reference: uint32_t
  ): Unit = unsupported
  def vkCmdSetStencilTestEnable(commandBuffer: VkCommandBuffer, stencilTestEnable: VkBool32): Unit = unsupported
  def vkCmdSetStencilTestEnableEXT(commandBuffer: VkCommandBuffer, stencilTestEnable: VkBool32): Unit = unsupported
  def vkCmdSetStencilWriteMask(
      commandBuffer: VkCommandBuffer,
      faceMask: VkStencilFaceFlags,
      writeMask: uint32_t
  ): Unit = unsupported
  def vkCmdSetTessellationDomainOriginEXT(
      commandBuffer: VkCommandBuffer,
      domainOrigin: VkTessellationDomainOrigin
  ): Unit = unsupported
  def vkCmdSetVertexInputEXT(
      commandBuffer: VkCommandBuffer,
      vertexBindingDescriptionCount: uint32_t,
      pVertexBindingDescriptions: Pointer[VkVertexInputBindingDescription2EXT, Read, Opaque],
      vertexAttributeDescriptionCount: uint32_t,
      pVertexAttributeDescriptions: Pointer[VkVertexInputAttributeDescription2EXT, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetViewport(
      commandBuffer: VkCommandBuffer,
      firstViewport: uint32_t,
      viewportCount: uint32_t,
      pViewports: Pointer[VkViewport, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetViewportShadingRatePaletteNV(
      commandBuffer: VkCommandBuffer,
      firstViewport: uint32_t,
      viewportCount: uint32_t,
      pShadingRatePalettes: Pointer[VkShadingRatePaletteNV, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetViewportSwizzleNV(
      commandBuffer: VkCommandBuffer,
      firstViewport: uint32_t,
      viewportCount: uint32_t,
      pViewportSwizzles: Pointer[VkViewportSwizzleNV, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetViewportWScalingEnableNV(commandBuffer: VkCommandBuffer, viewportWScalingEnable: VkBool32): Unit =
    unsupported
  def vkCmdSetViewportWScalingNV(
      commandBuffer: VkCommandBuffer,
      firstViewport: uint32_t,
      viewportCount: uint32_t,
      pViewportWScalings: Pointer[VkViewportWScalingNV, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetViewportWithCount(
      commandBuffer: VkCommandBuffer,
      viewportCount: uint32_t,
      pViewports: Pointer[VkViewport, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSetViewportWithCountEXT(
      commandBuffer: VkCommandBuffer,
      viewportCount: uint32_t,
      pViewports: Pointer[VkViewport, Read, Opaque]
  ): Unit = unsupported
  def vkCmdSubpassShadingHUAWEI(commandBuffer: VkCommandBuffer): Unit = unsupported
  def vkCmdTraceRaysIndirect2KHR(commandBuffer: VkCommandBuffer, indirectDeviceAddress: VkDeviceAddress): Unit =
    unsupported
  def vkCmdTraceRaysIndirectKHR(
      commandBuffer: VkCommandBuffer,
      pRaygenShaderBindingTable: Pointer[VkStridedDeviceAddressRegionKHR, Read, Opaque],
      pMissShaderBindingTable: Pointer[VkStridedDeviceAddressRegionKHR, Read, Opaque],
      pHitShaderBindingTable: Pointer[VkStridedDeviceAddressRegionKHR, Read, Opaque],
      pCallableShaderBindingTable: Pointer[VkStridedDeviceAddressRegionKHR, Read, Opaque],
      indirectDeviceAddress: VkDeviceAddress
  ): Unit = unsupported
  def vkCmdTraceRaysKHR(
      commandBuffer: VkCommandBuffer,
      pRaygenShaderBindingTable: Pointer[VkStridedDeviceAddressRegionKHR, Read, Opaque],
      pMissShaderBindingTable: Pointer[VkStridedDeviceAddressRegionKHR, Read, Opaque],
      pHitShaderBindingTable: Pointer[VkStridedDeviceAddressRegionKHR, Read, Opaque],
      pCallableShaderBindingTable: Pointer[VkStridedDeviceAddressRegionKHR, Read, Opaque],
      width: uint32_t,
      height: uint32_t,
      depth: uint32_t
  ): Unit = unsupported
  def vkCmdTraceRaysNV(
      commandBuffer: VkCommandBuffer,
      raygenShaderBindingTableBuffer: VkBuffer,
      raygenShaderBindingOffset: VkDeviceSize,
      missShaderBindingTableBuffer: VkBuffer,
      missShaderBindingOffset: VkDeviceSize,
      missShaderBindingStride: VkDeviceSize,
      hitShaderBindingTableBuffer: VkBuffer,
      hitShaderBindingOffset: VkDeviceSize,
      hitShaderBindingStride: VkDeviceSize,
      callableShaderBindingTableBuffer: VkBuffer,
      callableShaderBindingOffset: VkDeviceSize,
      callableShaderBindingStride: VkDeviceSize,
      width: uint32_t,
      height: uint32_t,
      depth: uint32_t
  ): Unit = unsupported
  def vkCmdUpdateBuffer(
      commandBuffer: VkCommandBuffer,
      dstBuffer: VkBuffer,
      dstOffset: VkDeviceSize,
      dataSize: VkDeviceSize,
      pData: Pointer[CVoid, Read, Opaque]
  ): Unit = unsupported
  def vkCmdUpdatePipelineIndirectBufferNV(
      commandBuffer: VkCommandBuffer,
      pipelineBindPoint: VkPipelineBindPoint,
      pipeline: VkPipeline
  ): Unit = unsupported
  def vkCmdWaitEvents(
      commandBuffer: VkCommandBuffer,
      eventCount: uint32_t,
      pEvents: Pointer[VkEvent, Read, Opaque],
      srcStageMask: VkPipelineStageFlags,
      dstStageMask: VkPipelineStageFlags,
      memoryBarrierCount: uint32_t,
      pMemoryBarriers: Pointer[VkMemoryBarrier, Read, Opaque],
      bufferMemoryBarrierCount: uint32_t,
      pBufferMemoryBarriers: Pointer[VkBufferMemoryBarrier, Read, Opaque],
      imageMemoryBarrierCount: uint32_t,
      pImageMemoryBarriers: Pointer[VkImageMemoryBarrier, Read, Opaque]
  ): Unit = unsupported
  def vkCmdWaitEvents2(
      commandBuffer: VkCommandBuffer,
      eventCount: uint32_t,
      pEvents: Pointer[VkEvent, Read, Opaque],
      pDependencyInfos: Pointer[VkDependencyInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdWaitEvents2KHR(
      commandBuffer: VkCommandBuffer,
      eventCount: uint32_t,
      pEvents: Pointer[VkEvent, Read, Opaque],
      pDependencyInfos: Pointer[VkDependencyInfo, Read, Opaque]
  ): Unit = unsupported
  def vkCmdWriteAccelerationStructuresPropertiesKHR(
      commandBuffer: VkCommandBuffer,
      accelerationStructureCount: uint32_t,
      pAccelerationStructures: Pointer[VkAccelerationStructureKHR, Read, Opaque],
      queryType: VkQueryType,
      queryPool: VkQueryPool,
      firstQuery: uint32_t
  ): Unit = unsupported
  def vkCmdWriteAccelerationStructuresPropertiesNV(
      commandBuffer: VkCommandBuffer,
      accelerationStructureCount: uint32_t,
      pAccelerationStructures: Pointer[VkAccelerationStructureNV, Read, Opaque],
      queryType: VkQueryType,
      queryPool: VkQueryPool,
      firstQuery: uint32_t
  ): Unit = unsupported
  def vkCmdWriteBufferMarker2AMD(
      commandBuffer: VkCommandBuffer,
      stage: VkPipelineStageFlags2,
      dstBuffer: VkBuffer,
      dstOffset: VkDeviceSize,
      marker: uint32_t
  ): Unit = unsupported
  def vkCmdWriteBufferMarkerAMD(
      commandBuffer: VkCommandBuffer,
      pipelineStage: VkPipelineStageFlagBits,
      dstBuffer: VkBuffer,
      dstOffset: VkDeviceSize,
      marker: uint32_t
  ): Unit = unsupported
  def vkCmdWriteMicromapsPropertiesEXT(
      commandBuffer: VkCommandBuffer,
      micromapCount: uint32_t,
      pMicromaps: Pointer[VkMicromapEXT, Read, Opaque],
      queryType: VkQueryType,
      queryPool: VkQueryPool,
      firstQuery: uint32_t
  ): Unit = unsupported
  def vkCmdWriteTimestamp(
      commandBuffer: VkCommandBuffer,
      pipelineStage: VkPipelineStageFlagBits,
      queryPool: VkQueryPool,
      query: uint32_t
  ): Unit = unsupported
  def vkCmdWriteTimestamp2(
      commandBuffer: VkCommandBuffer,
      stage: VkPipelineStageFlags2,
      queryPool: VkQueryPool,
      query: uint32_t
  ): Unit = unsupported
  def vkCmdWriteTimestamp2KHR(
      commandBuffer: VkCommandBuffer,
      stage: VkPipelineStageFlags2,
      queryPool: VkQueryPool,
      query: uint32_t
  ): Unit = unsupported
  def vkCompileDeferredNV(device: VkDevice, pipeline: VkPipeline, shader: uint32_t): VkResult = unsupported
  def vkConvertCooperativeVectorMatrixNV(
      device: VkDevice,
      pInfo: Pointer[VkConvertCooperativeVectorMatrixInfoNV, Read, Opaque]
  ): VkResult = unsupported
  def vkCopyAccelerationStructureKHR(
      device: VkDevice,
      deferredOperation: VkDeferredOperationKHR,
      pInfo: Pointer[VkCopyAccelerationStructureInfoKHR, Read, Opaque]
  ): VkResult = unsupported
  def vkCopyAccelerationStructureToMemoryKHR(
      device: VkDevice,
      deferredOperation: VkDeferredOperationKHR,
      pInfo: Pointer[VkCopyAccelerationStructureToMemoryInfoKHR, Read, Opaque]
  ): VkResult = unsupported
  def vkCopyImageToImage(
      device: VkDevice,
      pCopyImageToImageInfo: Pointer[VkCopyImageToImageInfo, Read, Opaque]
  ): VkResult = unsupported
  def vkCopyImageToImageEXT(
      device: VkDevice,
      pCopyImageToImageInfo: Pointer[VkCopyImageToImageInfo, Read, Opaque]
  ): VkResult = unsupported
  def vkCopyImageToMemory(
      device: VkDevice,
      pCopyImageToMemoryInfo: Pointer[VkCopyImageToMemoryInfo, Read, Opaque]
  ): VkResult = unsupported
  def vkCopyImageToMemoryEXT(
      device: VkDevice,
      pCopyImageToMemoryInfo: Pointer[VkCopyImageToMemoryInfo, Read, Opaque]
  ): VkResult = unsupported
  def vkCopyMemoryToAccelerationStructureKHR(
      device: VkDevice,
      deferredOperation: VkDeferredOperationKHR,
      pInfo: Pointer[VkCopyMemoryToAccelerationStructureInfoKHR, Read, Opaque]
  ): VkResult = unsupported
  def vkCopyMemoryToImage(
      device: VkDevice,
      pCopyMemoryToImageInfo: Pointer[VkCopyMemoryToImageInfo, Read, Opaque]
  ): VkResult = unsupported
  def vkCopyMemoryToImageEXT(
      device: VkDevice,
      pCopyMemoryToImageInfo: Pointer[VkCopyMemoryToImageInfo, Read, Opaque]
  ): VkResult = unsupported
  def vkCopyMemoryToMicromapEXT(
      device: VkDevice,
      deferredOperation: VkDeferredOperationKHR,
      pInfo: Pointer[VkCopyMemoryToMicromapInfoEXT, Read, Opaque]
  ): VkResult = unsupported
  def vkCopyMicromapEXT(
      device: VkDevice,
      deferredOperation: VkDeferredOperationKHR,
      pInfo: Pointer[VkCopyMicromapInfoEXT, Read, Opaque]
  ): VkResult = unsupported
  def vkCopyMicromapToMemoryEXT(
      device: VkDevice,
      deferredOperation: VkDeferredOperationKHR,
      pInfo: Pointer[VkCopyMicromapToMemoryInfoEXT, Read, Opaque]
  ): VkResult = unsupported
  def vkCreateAccelerationStructureKHR(
      device: VkDevice,
      pCreateInfo: Pointer[VkAccelerationStructureCreateInfoKHR, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pAccelerationStructure: Pointer[VkAccelerationStructureKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateAccelerationStructureNV(
      device: VkDevice,
      pCreateInfo: Pointer[VkAccelerationStructureCreateInfoNV, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pAccelerationStructure: Pointer[VkAccelerationStructureNV, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateBuffer(
      device: VkDevice,
      pCreateInfo: Pointer[VkBufferCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pBuffer: Pointer[VkBuffer, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateBufferView(
      device: VkDevice,
      pCreateInfo: Pointer[VkBufferViewCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pView: Pointer[VkBufferView, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateCommandPool(
      device: VkDevice,
      pCreateInfo: Pointer[VkCommandPoolCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pCommandPool: Pointer[VkCommandPool, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateComputePipelines(
      device: VkDevice,
      pipelineCache: VkPipelineCache,
      createInfoCount: uint32_t,
      pCreateInfos: Pointer[VkComputePipelineCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pPipelines: Pointer[VkPipeline, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateCuFunctionNVX(
      device: VkDevice,
      pCreateInfo: Pointer[VkCuFunctionCreateInfoNVX, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pFunction: Pointer[VkCuFunctionNVX, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateCuModuleNVX(
      device: VkDevice,
      pCreateInfo: Pointer[VkCuModuleCreateInfoNVX, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pModule: Pointer[VkCuModuleNVX, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateDataGraphPipelineSessionARM(
      device: VkDevice,
      pCreateInfo: Pointer[VkDataGraphPipelineSessionCreateInfoARM, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pSession: Pointer[VkDataGraphPipelineSessionARM, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateDataGraphPipelinesARM(
      device: VkDevice,
      deferredOperation: VkDeferredOperationKHR,
      pipelineCache: VkPipelineCache,
      createInfoCount: uint32_t,
      pCreateInfos: Pointer[VkDataGraphPipelineCreateInfoARM, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pPipelines: Pointer[VkPipeline, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateDebugReportCallbackEXT(
      instance: VkInstance,
      pCreateInfo: Pointer[VkDebugReportCallbackCreateInfoEXT, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pCallback: Pointer[VkDebugReportCallbackEXT, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateDebugUtilsMessengerEXT(
      instance: VkInstance,
      pCreateInfo: Pointer[VkDebugUtilsMessengerCreateInfoEXT, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pMessenger: Pointer[VkDebugUtilsMessengerEXT, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateDeferredOperationKHR(
      device: VkDevice,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pDeferredOperation: Pointer[VkDeferredOperationKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateDescriptorPool(
      device: VkDevice,
      pCreateInfo: Pointer[VkDescriptorPoolCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pDescriptorPool: Pointer[VkDescriptorPool, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateDescriptorSetLayout(
      device: VkDevice,
      pCreateInfo: Pointer[VkDescriptorSetLayoutCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pSetLayout: Pointer[VkDescriptorSetLayout, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateDescriptorUpdateTemplate(
      device: VkDevice,
      pCreateInfo: Pointer[VkDescriptorUpdateTemplateCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pDescriptorUpdateTemplate: Pointer[VkDescriptorUpdateTemplate, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateDescriptorUpdateTemplateKHR(
      device: VkDevice,
      pCreateInfo: Pointer[VkDescriptorUpdateTemplateCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pDescriptorUpdateTemplate: Pointer[VkDescriptorUpdateTemplate, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateDevice(
      physicalDevice: VkPhysicalDevice,
      pCreateInfo: Pointer[VkDeviceCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pDevice: Pointer[VkDevice, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateDisplayModeKHR(
      physicalDevice: VkPhysicalDevice,
      display: VkDisplayKHR,
      pCreateInfo: Pointer[VkDisplayModeCreateInfoKHR, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pMode: Pointer[VkDisplayModeKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateDisplayPlaneSurfaceKHR(
      instance: VkInstance,
      pCreateInfo: Pointer[VkDisplaySurfaceCreateInfoKHR, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pSurface: Pointer[VkSurfaceKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateEvent(
      device: VkDevice,
      pCreateInfo: Pointer[VkEventCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pEvent: Pointer[VkEvent, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateExternalComputeQueueNV(
      device: VkDevice,
      pCreateInfo: Pointer[VkExternalComputeQueueCreateInfoNV, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pExternalQueue: Pointer[VkExternalComputeQueueNV, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateFence(
      device: VkDevice,
      pCreateInfo: Pointer[VkFenceCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pFence: Pointer[VkFence, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateFramebuffer(
      device: VkDevice,
      pCreateInfo: Pointer[VkFramebufferCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pFramebuffer: Pointer[VkFramebuffer, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateGraphicsPipelines(
      device: VkDevice,
      pipelineCache: VkPipelineCache,
      createInfoCount: uint32_t,
      pCreateInfos: Pointer[VkGraphicsPipelineCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pPipelines: Pointer[VkPipeline, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateHeadlessSurfaceEXT(
      instance: VkInstance,
      pCreateInfo: Pointer[VkHeadlessSurfaceCreateInfoEXT, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pSurface: Pointer[VkSurfaceKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateImage(
      device: VkDevice,
      pCreateInfo: Pointer[VkImageCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pImage: Pointer[VkImage, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateImageView(
      device: VkDevice,
      pCreateInfo: Pointer[VkImageViewCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pView: Pointer[VkImageView, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateIndirectCommandsLayoutEXT(
      device: VkDevice,
      pCreateInfo: Pointer[VkIndirectCommandsLayoutCreateInfoEXT, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pIndirectCommandsLayout: Pointer[VkIndirectCommandsLayoutEXT, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateIndirectCommandsLayoutNV(
      device: VkDevice,
      pCreateInfo: Pointer[VkIndirectCommandsLayoutCreateInfoNV, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pIndirectCommandsLayout: Pointer[VkIndirectCommandsLayoutNV, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateIndirectExecutionSetEXT(
      device: VkDevice,
      pCreateInfo: Pointer[VkIndirectExecutionSetCreateInfoEXT, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pIndirectExecutionSet: Pointer[VkIndirectExecutionSetEXT, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateInstance(
      pCreateInfo: Pointer[VkInstanceCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pInstance: Pointer[VkInstance, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateMicromapEXT(
      device: VkDevice,
      pCreateInfo: Pointer[VkMicromapCreateInfoEXT, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pMicromap: Pointer[VkMicromapEXT, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateOpticalFlowSessionNV(
      device: VkDevice,
      pCreateInfo: Pointer[VkOpticalFlowSessionCreateInfoNV, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pSession: Pointer[VkOpticalFlowSessionNV, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreatePipelineBinariesKHR(
      device: VkDevice,
      pCreateInfo: Pointer[VkPipelineBinaryCreateInfoKHR, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pBinaries: Pointer[VkPipelineBinaryHandlesInfoKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreatePipelineCache(
      device: VkDevice,
      pCreateInfo: Pointer[VkPipelineCacheCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pPipelineCache: Pointer[VkPipelineCache, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreatePipelineLayout(
      device: VkDevice,
      pCreateInfo: Pointer[VkPipelineLayoutCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pPipelineLayout: Pointer[VkPipelineLayout, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreatePrivateDataSlot(
      device: VkDevice,
      pCreateInfo: Pointer[VkPrivateDataSlotCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pPrivateDataSlot: Pointer[VkPrivateDataSlot, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreatePrivateDataSlotEXT(
      device: VkDevice,
      pCreateInfo: Pointer[VkPrivateDataSlotCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pPrivateDataSlot: Pointer[VkPrivateDataSlot, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateQueryPool(
      device: VkDevice,
      pCreateInfo: Pointer[VkQueryPoolCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pQueryPool: Pointer[VkQueryPool, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateRayTracingPipelinesKHR(
      device: VkDevice,
      deferredOperation: VkDeferredOperationKHR,
      pipelineCache: VkPipelineCache,
      createInfoCount: uint32_t,
      pCreateInfos: Pointer[VkRayTracingPipelineCreateInfoKHR, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pPipelines: Pointer[VkPipeline, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateRayTracingPipelinesNV(
      device: VkDevice,
      pipelineCache: VkPipelineCache,
      createInfoCount: uint32_t,
      pCreateInfos: Pointer[VkRayTracingPipelineCreateInfoNV, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pPipelines: Pointer[VkPipeline, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateRenderPass(
      device: VkDevice,
      pCreateInfo: Pointer[VkRenderPassCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pRenderPass: Pointer[VkRenderPass, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateRenderPass2(
      device: VkDevice,
      pCreateInfo: Pointer[VkRenderPassCreateInfo2, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pRenderPass: Pointer[VkRenderPass, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateRenderPass2KHR(
      device: VkDevice,
      pCreateInfo: Pointer[VkRenderPassCreateInfo2, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pRenderPass: Pointer[VkRenderPass, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateSampler(
      device: VkDevice,
      pCreateInfo: Pointer[VkSamplerCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pSampler: Pointer[VkSampler, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateSamplerYcbcrConversion(
      device: VkDevice,
      pCreateInfo: Pointer[VkSamplerYcbcrConversionCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pYcbcrConversion: Pointer[VkSamplerYcbcrConversion, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateSamplerYcbcrConversionKHR(
      device: VkDevice,
      pCreateInfo: Pointer[VkSamplerYcbcrConversionCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pYcbcrConversion: Pointer[VkSamplerYcbcrConversion, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateSemaphore(
      device: VkDevice,
      pCreateInfo: Pointer[VkSemaphoreCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pSemaphore: Pointer[VkSemaphore, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateShaderModule(
      device: VkDevice,
      pCreateInfo: Pointer[VkShaderModuleCreateInfo, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pShaderModule: Pointer[VkShaderModule, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateShadersEXT(
      device: VkDevice,
      createInfoCount: uint32_t,
      pCreateInfos: Pointer[VkShaderCreateInfoEXT, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pShaders: Pointer[VkShaderEXT, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateSharedSwapchainsKHR(
      device: VkDevice,
      swapchainCount: uint32_t,
      pCreateInfos: Pointer[VkSwapchainCreateInfoKHR, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pSwapchains: Pointer[VkSwapchainKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateSwapchainKHR(
      device: VkDevice,
      pCreateInfo: Pointer[VkSwapchainCreateInfoKHR, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pSwapchain: Pointer[VkSwapchainKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateTensorARM(
      device: VkDevice,
      pCreateInfo: Pointer[VkTensorCreateInfoARM, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pTensor: Pointer[VkTensorARM, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateTensorViewARM(
      device: VkDevice,
      pCreateInfo: Pointer[VkTensorViewCreateInfoARM, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pView: Pointer[VkTensorViewARM, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateValidationCacheEXT(
      device: VkDevice,
      pCreateInfo: Pointer[VkValidationCacheCreateInfoEXT, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pValidationCache: Pointer[VkValidationCacheEXT, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateVideoSessionKHR(
      device: VkDevice,
      pCreateInfo: Pointer[VkVideoSessionCreateInfoKHR, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pVideoSession: Pointer[VkVideoSessionKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkCreateVideoSessionParametersKHR(
      device: VkDevice,
      pCreateInfo: Pointer[VkVideoSessionParametersCreateInfoKHR, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pVideoSessionParameters: Pointer[VkVideoSessionParametersKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkDebugMarkerSetObjectNameEXT(
      device: VkDevice,
      pNameInfo: Pointer[VkDebugMarkerObjectNameInfoEXT, Read, Opaque]
  ): VkResult = unsupported
  def vkDebugMarkerSetObjectTagEXT(
      device: VkDevice,
      pTagInfo: Pointer[VkDebugMarkerObjectTagInfoEXT, Read, Opaque]
  ): VkResult = unsupported
  def vkDebugReportMessageEXT(
      instance: VkInstance,
      flags: VkDebugReportFlagsEXT,
      objectType: VkDebugReportObjectTypeEXT,
      `object`: uint64_t,
      location: size_t,
      messageCode: int32_t,
      pLayerPrefix: CString,
      pMessage: CString
  ): Unit = unsupported
  def vkDeferredOperationJoinKHR(device: VkDevice, operation: VkDeferredOperationKHR): VkResult = unsupported
  def vkDestroyAccelerationStructureKHR(
      device: VkDevice,
      accelerationStructure: VkAccelerationStructureKHR,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyAccelerationStructureNV(
      device: VkDevice,
      accelerationStructure: VkAccelerationStructureNV,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyBuffer(
      device: VkDevice,
      buffer: VkBuffer,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyBufferView(
      device: VkDevice,
      bufferView: VkBufferView,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyCommandPool(
      device: VkDevice,
      commandPool: VkCommandPool,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyCuFunctionNVX(
      device: VkDevice,
      function: VkCuFunctionNVX,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyCuModuleNVX(
      device: VkDevice,
      module: VkCuModuleNVX,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyDataGraphPipelineSessionARM(
      device: VkDevice,
      session: VkDataGraphPipelineSessionARM,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyDebugReportCallbackEXT(
      instance: VkInstance,
      callback: VkDebugReportCallbackEXT,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyDebugUtilsMessengerEXT(
      instance: VkInstance,
      messenger: VkDebugUtilsMessengerEXT,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyDeferredOperationKHR(
      device: VkDevice,
      operation: VkDeferredOperationKHR,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyDescriptorPool(
      device: VkDevice,
      descriptorPool: VkDescriptorPool,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyDescriptorSetLayout(
      device: VkDevice,
      descriptorSetLayout: VkDescriptorSetLayout,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyDescriptorUpdateTemplate(
      device: VkDevice,
      descriptorUpdateTemplate: VkDescriptorUpdateTemplate,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyDescriptorUpdateTemplateKHR(
      device: VkDevice,
      descriptorUpdateTemplate: VkDescriptorUpdateTemplate,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyDevice(device: VkDevice, pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]): Unit = unsupported
  def vkDestroyEvent(device: VkDevice, event: VkEvent, pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]): Unit =
    unsupported
  def vkDestroyExternalComputeQueueNV(
      device: VkDevice,
      externalQueue: VkExternalComputeQueueNV,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyFence(device: VkDevice, fence: VkFence, pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]): Unit =
    unsupported
  def vkDestroyFramebuffer(
      device: VkDevice,
      framebuffer: VkFramebuffer,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyImage(device: VkDevice, image: VkImage, pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]): Unit =
    unsupported
  def vkDestroyImageView(
      device: VkDevice,
      imageView: VkImageView,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyIndirectCommandsLayoutEXT(
      device: VkDevice,
      indirectCommandsLayout: VkIndirectCommandsLayoutEXT,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyIndirectCommandsLayoutNV(
      device: VkDevice,
      indirectCommandsLayout: VkIndirectCommandsLayoutNV,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyIndirectExecutionSetEXT(
      device: VkDevice,
      indirectExecutionSet: VkIndirectExecutionSetEXT,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyInstance(instance: VkInstance, pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]): Unit =
    unsupported
  def vkDestroyMicromapEXT(
      device: VkDevice,
      micromap: VkMicromapEXT,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyOpticalFlowSessionNV(
      device: VkDevice,
      session: VkOpticalFlowSessionNV,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyPipeline(
      device: VkDevice,
      pipeline: VkPipeline,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyPipelineBinaryKHR(
      device: VkDevice,
      pipelineBinary: VkPipelineBinaryKHR,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyPipelineCache(
      device: VkDevice,
      pipelineCache: VkPipelineCache,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyPipelineLayout(
      device: VkDevice,
      pipelineLayout: VkPipelineLayout,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyPrivateDataSlot(
      device: VkDevice,
      privateDataSlot: VkPrivateDataSlot,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyPrivateDataSlotEXT(
      device: VkDevice,
      privateDataSlot: VkPrivateDataSlot,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyQueryPool(
      device: VkDevice,
      queryPool: VkQueryPool,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyRenderPass(
      device: VkDevice,
      renderPass: VkRenderPass,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroySampler(
      device: VkDevice,
      sampler: VkSampler,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroySamplerYcbcrConversion(
      device: VkDevice,
      ycbcrConversion: VkSamplerYcbcrConversion,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroySamplerYcbcrConversionKHR(
      device: VkDevice,
      ycbcrConversion: VkSamplerYcbcrConversion,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroySemaphore(
      device: VkDevice,
      semaphore: VkSemaphore,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyShaderEXT(
      device: VkDevice,
      shader: VkShaderEXT,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyShaderModule(
      device: VkDevice,
      shaderModule: VkShaderModule,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroySurfaceKHR(
      instance: VkInstance,
      surface: VkSurfaceKHR,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroySwapchainKHR(
      device: VkDevice,
      swapchain: VkSwapchainKHR,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyTensorARM(
      device: VkDevice,
      tensor: VkTensorARM,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyTensorViewARM(
      device: VkDevice,
      tensorView: VkTensorViewARM,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyValidationCacheEXT(
      device: VkDevice,
      validationCache: VkValidationCacheEXT,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyVideoSessionKHR(
      device: VkDevice,
      videoSession: VkVideoSessionKHR,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDestroyVideoSessionParametersKHR(
      device: VkDevice,
      videoSessionParameters: VkVideoSessionParametersKHR,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkDeviceWaitIdle(device: VkDevice): VkResult = unsupported
  def vkDisplayPowerControlEXT(
      device: VkDevice,
      display: VkDisplayKHR,
      pDisplayPowerInfo: Pointer[VkDisplayPowerInfoEXT, Read, Opaque]
  ): VkResult = unsupported
  def vkEndCommandBuffer(commandBuffer: VkCommandBuffer): VkResult = unsupported
  def vkEnumerateDeviceExtensionProperties(
      physicalDevice: VkPhysicalDevice,
      pLayerName: CString,
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkExtensionProperties, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkEnumerateDeviceLayerProperties(
      physicalDevice: VkPhysicalDevice,
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkLayerProperties, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkEnumerateInstanceExtensionProperties(
      pLayerName: CString,
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkExtensionProperties, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkEnumerateInstanceLayerProperties(
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkLayerProperties, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkEnumerateInstanceVersion(pApiVersion: Pointer[uint32_t, ReadWrite, Opaque]): VkResult = unsupported
  def vkEnumeratePhysicalDeviceGroups(
      instance: VkInstance,
      pPhysicalDeviceGroupCount: Pointer[uint32_t, ReadWrite, Opaque],
      pPhysicalDeviceGroupProperties: Pointer[VkPhysicalDeviceGroupProperties, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkEnumeratePhysicalDeviceGroupsKHR(
      instance: VkInstance,
      pPhysicalDeviceGroupCount: Pointer[uint32_t, ReadWrite, Opaque],
      pPhysicalDeviceGroupProperties: Pointer[VkPhysicalDeviceGroupProperties, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkEnumeratePhysicalDeviceQueueFamilyPerformanceCountersByRegionARM(
      physicalDevice: VkPhysicalDevice,
      queueFamilyIndex: uint32_t,
      pCounterCount: Pointer[uint32_t, ReadWrite, Opaque],
      pCounters: Pointer[VkPerformanceCounterARM, ReadWrite, Opaque],
      pCounterDescriptions: Pointer[VkPerformanceCounterDescriptionARM, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkEnumeratePhysicalDeviceQueueFamilyPerformanceQueryCountersKHR(
      physicalDevice: VkPhysicalDevice,
      queueFamilyIndex: uint32_t,
      pCounterCount: Pointer[uint32_t, ReadWrite, Opaque],
      pCounters: Pointer[VkPerformanceCounterKHR, ReadWrite, Opaque],
      pCounterDescriptions: Pointer[VkPerformanceCounterDescriptionKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkEnumeratePhysicalDevices(
      instance: VkInstance,
      pPhysicalDeviceCount: Pointer[uint32_t, ReadWrite, Opaque],
      pPhysicalDevices: Pointer[VkPhysicalDevice, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkFlushMappedMemoryRanges(
      device: VkDevice,
      memoryRangeCount: uint32_t,
      pMemoryRanges: Pointer[VkMappedMemoryRange, Read, Opaque]
  ): VkResult = unsupported
  def vkFreeCommandBuffers(
      device: VkDevice,
      commandPool: VkCommandPool,
      commandBufferCount: uint32_t,
      pCommandBuffers: Pointer[VkCommandBuffer, Read, Opaque]
  ): Unit = unsupported
  def vkFreeDescriptorSets(
      device: VkDevice,
      descriptorPool: VkDescriptorPool,
      descriptorSetCount: uint32_t,
      pDescriptorSets: Pointer[VkDescriptorSet, Read, Opaque]
  ): VkResult = unsupported
  def vkFreeMemory(
      device: VkDevice,
      memory: VkDeviceMemory,
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): Unit = unsupported
  def vkGetAccelerationStructureBuildSizesKHR(
      device: VkDevice,
      buildType: VkAccelerationStructureBuildTypeKHR,
      pBuildInfo: Pointer[VkAccelerationStructureBuildGeometryInfoKHR, Read, Opaque],
      pMaxPrimitiveCounts: Pointer[uint32_t, Read, Opaque],
      pSizeInfo: Pointer[VkAccelerationStructureBuildSizesInfoKHR, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetAccelerationStructureDeviceAddressKHR(
      device: VkDevice,
      pInfo: Pointer[VkAccelerationStructureDeviceAddressInfoKHR, Read, Opaque]
  ): VkDeviceAddress = unsupported
  def vkGetAccelerationStructureHandleNV(
      device: VkDevice,
      accelerationStructure: VkAccelerationStructureNV,
      dataSize: size_t,
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetAccelerationStructureMemoryRequirementsNV(
      device: VkDevice,
      pInfo: Pointer[VkAccelerationStructureMemoryRequirementsInfoNV, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2KHR, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetAccelerationStructureOpaqueCaptureDescriptorDataEXT(
      device: VkDevice,
      pInfo: Pointer[VkAccelerationStructureCaptureDescriptorDataInfoEXT, Read, Opaque],
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetBufferDeviceAddress(
      device: VkDevice,
      pInfo: Pointer[VkBufferDeviceAddressInfo, Read, Opaque]
  ): VkDeviceAddress = unsupported
  def vkGetBufferDeviceAddressEXT(
      device: VkDevice,
      pInfo: Pointer[VkBufferDeviceAddressInfo, Read, Opaque]
  ): VkDeviceAddress = unsupported
  def vkGetBufferDeviceAddressKHR(
      device: VkDevice,
      pInfo: Pointer[VkBufferDeviceAddressInfo, Read, Opaque]
  ): VkDeviceAddress = unsupported
  def vkGetBufferMemoryRequirements(
      device: VkDevice,
      buffer: VkBuffer,
      pMemoryRequirements: Pointer[VkMemoryRequirements, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetBufferMemoryRequirements2(
      device: VkDevice,
      pInfo: Pointer[VkBufferMemoryRequirementsInfo2, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetBufferMemoryRequirements2KHR(
      device: VkDevice,
      pInfo: Pointer[VkBufferMemoryRequirementsInfo2, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetBufferOpaqueCaptureAddress(
      device: VkDevice,
      pInfo: Pointer[VkBufferDeviceAddressInfo, Read, Opaque]
  ): uint64_t = unsupported
  def vkGetBufferOpaqueCaptureAddressKHR(
      device: VkDevice,
      pInfo: Pointer[VkBufferDeviceAddressInfo, Read, Opaque]
  ): uint64_t = unsupported
  def vkGetBufferOpaqueCaptureDescriptorDataEXT(
      device: VkDevice,
      pInfo: Pointer[VkBufferCaptureDescriptorDataInfoEXT, Read, Opaque],
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetCalibratedTimestampsEXT(
      device: VkDevice,
      timestampCount: uint32_t,
      pTimestampInfos: Pointer[VkCalibratedTimestampInfoKHR, Read, Opaque],
      pTimestamps: Pointer[uint64_t, ReadWrite, Opaque],
      pMaxDeviation: Pointer[uint64_t, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetCalibratedTimestampsKHR(
      device: VkDevice,
      timestampCount: uint32_t,
      pTimestampInfos: Pointer[VkCalibratedTimestampInfoKHR, Read, Opaque],
      pTimestamps: Pointer[uint64_t, ReadWrite, Opaque],
      pMaxDeviation: Pointer[uint64_t, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetClusterAccelerationStructureBuildSizesNV(
      device: VkDevice,
      pInfo: Pointer[VkClusterAccelerationStructureInputInfoNV, Read, Opaque],
      pSizeInfo: Pointer[VkAccelerationStructureBuildSizesInfoKHR, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDataGraphPipelineAvailablePropertiesARM(
      device: VkDevice,
      pPipelineInfo: Pointer[VkDataGraphPipelineInfoARM, Read, Opaque],
      pPropertiesCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkDataGraphPipelinePropertyARM, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetDataGraphPipelinePropertiesARM(
      device: VkDevice,
      pPipelineInfo: Pointer[VkDataGraphPipelineInfoARM, Read, Opaque],
      propertiesCount: uint32_t,
      pProperties: Pointer[VkDataGraphPipelinePropertyQueryResultARM, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetDataGraphPipelineSessionBindPointRequirementsARM(
      device: VkDevice,
      pInfo: Pointer[VkDataGraphPipelineSessionBindPointRequirementsInfoARM, Read, Opaque],
      pBindPointRequirementCount: Pointer[uint32_t, ReadWrite, Opaque],
      pBindPointRequirements: Pointer[VkDataGraphPipelineSessionBindPointRequirementARM, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetDataGraphPipelineSessionMemoryRequirementsARM(
      device: VkDevice,
      pInfo: Pointer[VkDataGraphPipelineSessionMemoryRequirementsInfoARM, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeferredOperationMaxConcurrencyKHR(device: VkDevice, operation: VkDeferredOperationKHR): uint32_t =
    unsupported
  def vkGetDeferredOperationResultKHR(device: VkDevice, operation: VkDeferredOperationKHR): VkResult = unsupported
  def vkGetDescriptorEXT(
      device: VkDevice,
      pDescriptorInfo: Pointer[VkDescriptorGetInfoEXT, Read, Opaque],
      dataSize: size_t,
      pDescriptor: OpaquePointer
  ): Unit = unsupported
  def vkGetDescriptorSetHostMappingVALVE(
      device: VkDevice,
      descriptorSet: VkDescriptorSet,
      ppData: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDescriptorSetLayoutBindingOffsetEXT(
      device: VkDevice,
      layout: VkDescriptorSetLayout,
      binding: uint32_t,
      pOffset: Pointer[VkDeviceSize, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDescriptorSetLayoutHostMappingInfoVALVE(
      device: VkDevice,
      pBindingReference: Pointer[VkDescriptorSetBindingReferenceVALVE, Read, Opaque],
      pHostMapping: Pointer[VkDescriptorSetLayoutHostMappingInfoVALVE, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDescriptorSetLayoutSizeEXT(
      device: VkDevice,
      layout: VkDescriptorSetLayout,
      pLayoutSizeInBytes: Pointer[VkDeviceSize, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDescriptorSetLayoutSupport(
      device: VkDevice,
      pCreateInfo: Pointer[VkDescriptorSetLayoutCreateInfo, Read, Opaque],
      pSupport: Pointer[VkDescriptorSetLayoutSupport, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDescriptorSetLayoutSupportKHR(
      device: VkDevice,
      pCreateInfo: Pointer[VkDescriptorSetLayoutCreateInfo, Read, Opaque],
      pSupport: Pointer[VkDescriptorSetLayoutSupport, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceAccelerationStructureCompatibilityKHR(
      device: VkDevice,
      pVersionInfo: Pointer[VkAccelerationStructureVersionInfoKHR, Read, Opaque],
      pCompatibility: Pointer[VkAccelerationStructureCompatibilityKHR, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceBufferMemoryRequirements(
      device: VkDevice,
      pInfo: Pointer[VkDeviceBufferMemoryRequirements, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceBufferMemoryRequirementsKHR(
      device: VkDevice,
      pInfo: Pointer[VkDeviceBufferMemoryRequirements, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceCombinedImageSamplerIndexNVX(
      device: VkDevice,
      imageViewIndex: uint64_t,
      samplerIndex: uint64_t
  ): uint64_t = unsupported
  def vkGetDeviceFaultInfoEXT(
      device: VkDevice,
      pFaultCounts: Pointer[VkDeviceFaultCountsEXT, ReadWrite, Opaque],
      pFaultInfo: Pointer[VkDeviceFaultInfoEXT, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetDeviceGroupPeerMemoryFeatures(
      device: VkDevice,
      heapIndex: uint32_t,
      localDeviceIndex: uint32_t,
      remoteDeviceIndex: uint32_t,
      pPeerMemoryFeatures: Pointer[VkPeerMemoryFeatureFlags, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceGroupPeerMemoryFeaturesKHR(
      device: VkDevice,
      heapIndex: uint32_t,
      localDeviceIndex: uint32_t,
      remoteDeviceIndex: uint32_t,
      pPeerMemoryFeatures: Pointer[VkPeerMemoryFeatureFlags, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceGroupPresentCapabilitiesKHR(
      device: VkDevice,
      pDeviceGroupPresentCapabilities: Pointer[VkDeviceGroupPresentCapabilitiesKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetDeviceGroupSurfacePresentModesKHR(
      device: VkDevice,
      surface: VkSurfaceKHR,
      pModes: Pointer[VkDeviceGroupPresentModeFlagsKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetDeviceImageMemoryRequirements(
      device: VkDevice,
      pInfo: Pointer[VkDeviceImageMemoryRequirements, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceImageMemoryRequirementsKHR(
      device: VkDevice,
      pInfo: Pointer[VkDeviceImageMemoryRequirements, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceImageSparseMemoryRequirements(
      device: VkDevice,
      pInfo: Pointer[VkDeviceImageMemoryRequirements, Read, Opaque],
      pSparseMemoryRequirementCount: Pointer[uint32_t, ReadWrite, Opaque],
      pSparseMemoryRequirements: Pointer[VkSparseImageMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceImageSparseMemoryRequirementsKHR(
      device: VkDevice,
      pInfo: Pointer[VkDeviceImageMemoryRequirements, Read, Opaque],
      pSparseMemoryRequirementCount: Pointer[uint32_t, ReadWrite, Opaque],
      pSparseMemoryRequirements: Pointer[VkSparseImageMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceImageSubresourceLayout(
      device: VkDevice,
      pInfo: Pointer[VkDeviceImageSubresourceInfo, Read, Opaque],
      pLayout: Pointer[VkSubresourceLayout2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceImageSubresourceLayoutKHR(
      device: VkDevice,
      pInfo: Pointer[VkDeviceImageSubresourceInfo, Read, Opaque],
      pLayout: Pointer[VkSubresourceLayout2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceMemoryCommitment(
      device: VkDevice,
      memory: VkDeviceMemory,
      pCommittedMemoryInBytes: Pointer[VkDeviceSize, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceMemoryOpaqueCaptureAddress(
      device: VkDevice,
      pInfo: Pointer[VkDeviceMemoryOpaqueCaptureAddressInfo, Read, Opaque]
  ): uint64_t = unsupported
  def vkGetDeviceMemoryOpaqueCaptureAddressKHR(
      device: VkDevice,
      pInfo: Pointer[VkDeviceMemoryOpaqueCaptureAddressInfo, Read, Opaque]
  ): uint64_t = unsupported
  def vkGetDeviceMicromapCompatibilityEXT(
      device: VkDevice,
      pVersionInfo: Pointer[VkMicromapVersionInfoEXT, Read, Opaque],
      pCompatibility: Pointer[VkAccelerationStructureCompatibilityKHR, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceProcAddr(device: VkDevice, pName: CString): PFN_vkVoidFunction = unsupported
  def vkGetDeviceQueue(
      device: VkDevice,
      queueFamilyIndex: uint32_t,
      queueIndex: uint32_t,
      pQueue: Pointer[VkQueue, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceQueue2(
      device: VkDevice,
      pQueueInfo: Pointer[VkDeviceQueueInfo2, Read, Opaque],
      pQueue: Pointer[VkQueue, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDeviceSubpassShadingMaxWorkgroupSizeHUAWEI(
      device: VkDevice,
      renderpass: VkRenderPass,
      pMaxWorkgroupSize: Pointer[VkExtent2D, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetDeviceTensorMemoryRequirementsARM(
      device: VkDevice,
      pInfo: Pointer[VkDeviceTensorMemoryRequirementsARM, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetDisplayModeProperties2KHR(
      physicalDevice: VkPhysicalDevice,
      display: VkDisplayKHR,
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkDisplayModeProperties2KHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetDisplayModePropertiesKHR(
      physicalDevice: VkPhysicalDevice,
      display: VkDisplayKHR,
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkDisplayModePropertiesKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetDisplayPlaneCapabilities2KHR(
      physicalDevice: VkPhysicalDevice,
      pDisplayPlaneInfo: Pointer[VkDisplayPlaneInfo2KHR, Read, Opaque],
      pCapabilities: Pointer[VkDisplayPlaneCapabilities2KHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetDisplayPlaneCapabilitiesKHR(
      physicalDevice: VkPhysicalDevice,
      mode: VkDisplayModeKHR,
      planeIndex: uint32_t,
      pCapabilities: Pointer[VkDisplayPlaneCapabilitiesKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetDisplayPlaneSupportedDisplaysKHR(
      physicalDevice: VkPhysicalDevice,
      planeIndex: uint32_t,
      pDisplayCount: Pointer[uint32_t, ReadWrite, Opaque],
      pDisplays: Pointer[VkDisplayKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetDrmDisplayEXT(
      physicalDevice: VkPhysicalDevice,
      drmFd: int32_t,
      connectorId: uint32_t,
      display: Pointer[VkDisplayKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetDynamicRenderingTilePropertiesQCOM(
      device: VkDevice,
      pRenderingInfo: Pointer[VkRenderingInfo, Read, Opaque],
      pProperties: Pointer[VkTilePropertiesQCOM, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetEncodedVideoSessionParametersKHR(
      device: VkDevice,
      pVideoSessionParametersInfo: Pointer[VkVideoEncodeSessionParametersGetInfoKHR, Read, Opaque],
      pFeedbackInfo: Pointer[VkVideoEncodeSessionParametersFeedbackInfoKHR, ReadWrite, Opaque],
      pDataSize: Pointer[size_t, ReadWrite, Opaque],
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetEventStatus(device: VkDevice, event: VkEvent): VkResult = unsupported
  def vkGetExternalComputeQueueDataNV(
      externalQueue: VkExternalComputeQueueNV,
      params: Pointer[VkExternalComputeQueueDataParamsNV, ReadWrite, Opaque],
      pData: OpaquePointer
  ): Unit = unsupported
  def vkGetFenceFdKHR(
      device: VkDevice,
      pGetFdInfo: Pointer[VkFenceGetFdInfoKHR, Read, Opaque],
      pFd: Pointer[Int, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetFenceStatus(device: VkDevice, fence: VkFence): VkResult = unsupported
  def vkGetFramebufferTilePropertiesQCOM(
      device: VkDevice,
      framebuffer: VkFramebuffer,
      pPropertiesCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkTilePropertiesQCOM, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetGeneratedCommandsMemoryRequirementsEXT(
      device: VkDevice,
      pInfo: Pointer[VkGeneratedCommandsMemoryRequirementsInfoEXT, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetGeneratedCommandsMemoryRequirementsNV(
      device: VkDevice,
      pInfo: Pointer[VkGeneratedCommandsMemoryRequirementsInfoNV, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetImageDrmFormatModifierPropertiesEXT(
      device: VkDevice,
      image: VkImage,
      pProperties: Pointer[VkImageDrmFormatModifierPropertiesEXT, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetImageMemoryRequirements(
      device: VkDevice,
      image: VkImage,
      pMemoryRequirements: Pointer[VkMemoryRequirements, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetImageMemoryRequirements2(
      device: VkDevice,
      pInfo: Pointer[VkImageMemoryRequirementsInfo2, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetImageMemoryRequirements2KHR(
      device: VkDevice,
      pInfo: Pointer[VkImageMemoryRequirementsInfo2, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetImageOpaqueCaptureDataEXT(
      device: VkDevice,
      imageCount: uint32_t,
      pImages: Pointer[VkImage, Read, Opaque],
      pDatas: Pointer[VkHostAddressRangeEXT, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetImageOpaqueCaptureDescriptorDataEXT(
      device: VkDevice,
      pInfo: Pointer[VkImageCaptureDescriptorDataInfoEXT, Read, Opaque],
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetImageSparseMemoryRequirements(
      device: VkDevice,
      image: VkImage,
      pSparseMemoryRequirementCount: Pointer[uint32_t, ReadWrite, Opaque],
      pSparseMemoryRequirements: Pointer[VkSparseImageMemoryRequirements, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetImageSparseMemoryRequirements2(
      device: VkDevice,
      pInfo: Pointer[VkImageSparseMemoryRequirementsInfo2, Read, Opaque],
      pSparseMemoryRequirementCount: Pointer[uint32_t, ReadWrite, Opaque],
      pSparseMemoryRequirements: Pointer[VkSparseImageMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetImageSparseMemoryRequirements2KHR(
      device: VkDevice,
      pInfo: Pointer[VkImageSparseMemoryRequirementsInfo2, Read, Opaque],
      pSparseMemoryRequirementCount: Pointer[uint32_t, ReadWrite, Opaque],
      pSparseMemoryRequirements: Pointer[VkSparseImageMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetImageSubresourceLayout(
      device: VkDevice,
      image: VkImage,
      pSubresource: Pointer[VkImageSubresource, Read, Opaque],
      pLayout: Pointer[VkSubresourceLayout, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetImageSubresourceLayout2(
      device: VkDevice,
      image: VkImage,
      pSubresource: Pointer[VkImageSubresource2, Read, Opaque],
      pLayout: Pointer[VkSubresourceLayout2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetImageSubresourceLayout2EXT(
      device: VkDevice,
      image: VkImage,
      pSubresource: Pointer[VkImageSubresource2, Read, Opaque],
      pLayout: Pointer[VkSubresourceLayout2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetImageSubresourceLayout2KHR(
      device: VkDevice,
      image: VkImage,
      pSubresource: Pointer[VkImageSubresource2, Read, Opaque],
      pLayout: Pointer[VkSubresourceLayout2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetImageViewAddressNVX(
      device: VkDevice,
      imageView: VkImageView,
      pProperties: Pointer[VkImageViewAddressPropertiesNVX, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetImageViewHandle64NVX(device: VkDevice, pInfo: Pointer[VkImageViewHandleInfoNVX, Read, Opaque]): uint64_t =
    unsupported
  def vkGetImageViewHandleNVX(device: VkDevice, pInfo: Pointer[VkImageViewHandleInfoNVX, Read, Opaque]): uint32_t =
    unsupported
  def vkGetImageViewOpaqueCaptureDescriptorDataEXT(
      device: VkDevice,
      pInfo: Pointer[VkImageViewCaptureDescriptorDataInfoEXT, Read, Opaque],
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetInstanceProcAddr(instance: VkInstance, pName: CString): PFN_vkVoidFunction = unsupported
  def vkGetLatencyTimingsNV(
      device: VkDevice,
      swapchain: VkSwapchainKHR,
      pLatencyMarkerInfo: Pointer[VkGetLatencyMarkerInfoNV, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetMemoryFdKHR(
      device: VkDevice,
      pGetFdInfo: Pointer[VkMemoryGetFdInfoKHR, Read, Opaque],
      pFd: Pointer[Int, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetMemoryFdPropertiesKHR(
      device: VkDevice,
      handleType: VkExternalMemoryHandleTypeFlagBits,
      fd: Int,
      pMemoryFdProperties: Pointer[VkMemoryFdPropertiesKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetMemoryHostPointerPropertiesEXT(
      device: VkDevice,
      handleType: VkExternalMemoryHandleTypeFlagBits,
      pHostPointer: Pointer[CVoid, Read, Opaque],
      pMemoryHostPointerProperties: Pointer[VkMemoryHostPointerPropertiesEXT, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetMemoryRemoteAddressNV(
      device: VkDevice,
      pMemoryGetRemoteAddressInfo: Pointer[VkMemoryGetRemoteAddressInfoNV, Read, Opaque],
      pAddress: Pointer[VkRemoteAddressNV, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetMicromapBuildSizesEXT(
      device: VkDevice,
      buildType: VkAccelerationStructureBuildTypeKHR,
      pBuildInfo: Pointer[VkMicromapBuildInfoEXT, Read, Opaque],
      pSizeInfo: Pointer[VkMicromapBuildSizesInfoEXT, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPartitionedAccelerationStructuresBuildSizesNV(
      device: VkDevice,
      pInfo: Pointer[VkPartitionedAccelerationStructureInstancesInputNV, Read, Opaque],
      pSizeInfo: Pointer[VkAccelerationStructureBuildSizesInfoKHR, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPastPresentationTimingEXT(
      device: VkDevice,
      pPastPresentationTimingInfo: Pointer[VkPastPresentationTimingInfoEXT, Read, Opaque],
      pPastPresentationTimingProperties: Pointer[VkPastPresentationTimingPropertiesEXT, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPastPresentationTimingGOOGLE(
      device: VkDevice,
      swapchain: VkSwapchainKHR,
      pPresentationTimingCount: Pointer[uint32_t, ReadWrite, Opaque],
      pPresentationTimings: Pointer[VkPastPresentationTimingGOOGLE, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPerformanceParameterINTEL(
      device: VkDevice,
      parameter: VkPerformanceParameterTypeINTEL,
      pValue: Pointer[VkPerformanceValueINTEL, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceCalibrateableTimeDomainsEXT(
      physicalDevice: VkPhysicalDevice,
      pTimeDomainCount: Pointer[uint32_t, ReadWrite, Opaque],
      pTimeDomains: Pointer[VkTimeDomainKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceCalibrateableTimeDomainsKHR(
      physicalDevice: VkPhysicalDevice,
      pTimeDomainCount: Pointer[uint32_t, ReadWrite, Opaque],
      pTimeDomains: Pointer[VkTimeDomainKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceCooperativeMatrixFlexibleDimensionsPropertiesNV(
      physicalDevice: VkPhysicalDevice,
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkCooperativeMatrixFlexibleDimensionsPropertiesNV, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceCooperativeMatrixPropertiesKHR(
      physicalDevice: VkPhysicalDevice,
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkCooperativeMatrixPropertiesKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceCooperativeMatrixPropertiesNV(
      physicalDevice: VkPhysicalDevice,
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkCooperativeMatrixPropertiesNV, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceCooperativeVectorPropertiesNV(
      physicalDevice: VkPhysicalDevice,
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkCooperativeVectorPropertiesNV, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceDescriptorSizeEXT(
      physicalDevice: VkPhysicalDevice,
      descriptorType: VkDescriptorType
  ): VkDeviceSize = unsupported
  def vkGetPhysicalDeviceDisplayPlaneProperties2KHR(
      physicalDevice: VkPhysicalDevice,
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkDisplayPlaneProperties2KHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceDisplayPlanePropertiesKHR(
      physicalDevice: VkPhysicalDevice,
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkDisplayPlanePropertiesKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceDisplayProperties2KHR(
      physicalDevice: VkPhysicalDevice,
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkDisplayProperties2KHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceDisplayPropertiesKHR(
      physicalDevice: VkPhysicalDevice,
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkDisplayPropertiesKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceExternalBufferProperties(
      physicalDevice: VkPhysicalDevice,
      pExternalBufferInfo: Pointer[VkPhysicalDeviceExternalBufferInfo, Read, Opaque],
      pExternalBufferProperties: Pointer[VkExternalBufferProperties, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceExternalBufferPropertiesKHR(
      physicalDevice: VkPhysicalDevice,
      pExternalBufferInfo: Pointer[VkPhysicalDeviceExternalBufferInfo, Read, Opaque],
      pExternalBufferProperties: Pointer[VkExternalBufferProperties, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceExternalFenceProperties(
      physicalDevice: VkPhysicalDevice,
      pExternalFenceInfo: Pointer[VkPhysicalDeviceExternalFenceInfo, Read, Opaque],
      pExternalFenceProperties: Pointer[VkExternalFenceProperties, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceExternalFencePropertiesKHR(
      physicalDevice: VkPhysicalDevice,
      pExternalFenceInfo: Pointer[VkPhysicalDeviceExternalFenceInfo, Read, Opaque],
      pExternalFenceProperties: Pointer[VkExternalFenceProperties, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceExternalImageFormatPropertiesNV(
      physicalDevice: VkPhysicalDevice,
      format: VkFormat,
      `type`: VkImageType,
      tiling: VkImageTiling,
      usage: VkImageUsageFlags,
      flags: VkImageCreateFlags,
      externalHandleType: VkExternalMemoryHandleTypeFlagsNV,
      pExternalImageFormatProperties: Pointer[VkExternalImageFormatPropertiesNV, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceExternalSemaphoreProperties(
      physicalDevice: VkPhysicalDevice,
      pExternalSemaphoreInfo: Pointer[VkPhysicalDeviceExternalSemaphoreInfo, Read, Opaque],
      pExternalSemaphoreProperties: Pointer[VkExternalSemaphoreProperties, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceExternalSemaphorePropertiesKHR(
      physicalDevice: VkPhysicalDevice,
      pExternalSemaphoreInfo: Pointer[VkPhysicalDeviceExternalSemaphoreInfo, Read, Opaque],
      pExternalSemaphoreProperties: Pointer[VkExternalSemaphoreProperties, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceExternalTensorPropertiesARM(
      physicalDevice: VkPhysicalDevice,
      pExternalTensorInfo: Pointer[VkPhysicalDeviceExternalTensorInfoARM, Read, Opaque],
      pExternalTensorProperties: Pointer[VkExternalTensorPropertiesARM, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceFeatures(
      physicalDevice: VkPhysicalDevice,
      pFeatures: Pointer[VkPhysicalDeviceFeatures, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceFeatures2(
      physicalDevice: VkPhysicalDevice,
      pFeatures: Pointer[VkPhysicalDeviceFeatures2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceFeatures2KHR(
      physicalDevice: VkPhysicalDevice,
      pFeatures: Pointer[VkPhysicalDeviceFeatures2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceFormatProperties(
      physicalDevice: VkPhysicalDevice,
      format: VkFormat,
      pFormatProperties: Pointer[VkFormatProperties, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceFormatProperties2(
      physicalDevice: VkPhysicalDevice,
      format: VkFormat,
      pFormatProperties: Pointer[VkFormatProperties2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceFormatProperties2KHR(
      physicalDevice: VkPhysicalDevice,
      format: VkFormat,
      pFormatProperties: Pointer[VkFormatProperties2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceFragmentShadingRatesKHR(
      physicalDevice: VkPhysicalDevice,
      pFragmentShadingRateCount: Pointer[uint32_t, ReadWrite, Opaque],
      pFragmentShadingRates: Pointer[VkPhysicalDeviceFragmentShadingRateKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceImageFormatProperties(
      physicalDevice: VkPhysicalDevice,
      format: VkFormat,
      `type`: VkImageType,
      tiling: VkImageTiling,
      usage: VkImageUsageFlags,
      flags: VkImageCreateFlags,
      pImageFormatProperties: Pointer[VkImageFormatProperties, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceImageFormatProperties2(
      physicalDevice: VkPhysicalDevice,
      pImageFormatInfo: Pointer[VkPhysicalDeviceImageFormatInfo2, Read, Opaque],
      pImageFormatProperties: Pointer[VkImageFormatProperties2, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceImageFormatProperties2KHR(
      physicalDevice: VkPhysicalDevice,
      pImageFormatInfo: Pointer[VkPhysicalDeviceImageFormatInfo2, Read, Opaque],
      pImageFormatProperties: Pointer[VkImageFormatProperties2, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceMemoryProperties(
      physicalDevice: VkPhysicalDevice,
      pMemoryProperties: Pointer[VkPhysicalDeviceMemoryProperties, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceMemoryProperties2(
      physicalDevice: VkPhysicalDevice,
      pMemoryProperties: Pointer[VkPhysicalDeviceMemoryProperties2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceMemoryProperties2KHR(
      physicalDevice: VkPhysicalDevice,
      pMemoryProperties: Pointer[VkPhysicalDeviceMemoryProperties2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceMultisamplePropertiesEXT(
      physicalDevice: VkPhysicalDevice,
      samples: VkSampleCountFlagBits,
      pMultisampleProperties: Pointer[VkMultisamplePropertiesEXT, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceOpticalFlowImageFormatsNV(
      physicalDevice: VkPhysicalDevice,
      pOpticalFlowImageFormatInfo: Pointer[VkOpticalFlowImageFormatInfoNV, Read, Opaque],
      pFormatCount: Pointer[uint32_t, ReadWrite, Opaque],
      pImageFormatProperties: Pointer[VkOpticalFlowImageFormatPropertiesNV, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDevicePresentRectanglesKHR(
      physicalDevice: VkPhysicalDevice,
      surface: VkSurfaceKHR,
      pRectCount: Pointer[uint32_t, ReadWrite, Opaque],
      pRects: Pointer[VkRect2D, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceProperties(
      physicalDevice: VkPhysicalDevice,
      pProperties: Pointer[VkPhysicalDeviceProperties, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceProperties2(
      physicalDevice: VkPhysicalDevice,
      pProperties: Pointer[VkPhysicalDeviceProperties2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceProperties2KHR(
      physicalDevice: VkPhysicalDevice,
      pProperties: Pointer[VkPhysicalDeviceProperties2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceQueueFamilyDataGraphProcessingEnginePropertiesARM(
      physicalDevice: VkPhysicalDevice,
      pQueueFamilyDataGraphProcessingEngineInfo: Pointer[
        VkPhysicalDeviceQueueFamilyDataGraphProcessingEngineInfoARM,
        Read,
        Opaque
      ],
      pQueueFamilyDataGraphProcessingEngineProperties: Pointer[
        VkQueueFamilyDataGraphProcessingEnginePropertiesARM,
        ReadWrite,
        Opaque
      ]
  ): Unit = unsupported
  def vkGetPhysicalDeviceQueueFamilyDataGraphPropertiesARM(
      physicalDevice: VkPhysicalDevice,
      queueFamilyIndex: uint32_t,
      pQueueFamilyDataGraphPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pQueueFamilyDataGraphProperties: Pointer[VkQueueFamilyDataGraphPropertiesARM, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceQueueFamilyPerformanceQueryPassesKHR(
      physicalDevice: VkPhysicalDevice,
      pPerformanceQueryCreateInfo: Pointer[VkQueryPoolPerformanceCreateInfoKHR, Read, Opaque],
      pNumPasses: Pointer[uint32_t, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceQueueFamilyProperties(
      physicalDevice: VkPhysicalDevice,
      pQueueFamilyPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pQueueFamilyProperties: Pointer[VkQueueFamilyProperties, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceQueueFamilyProperties2(
      physicalDevice: VkPhysicalDevice,
      pQueueFamilyPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pQueueFamilyProperties: Pointer[VkQueueFamilyProperties2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceQueueFamilyProperties2KHR(
      physicalDevice: VkPhysicalDevice,
      pQueueFamilyPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pQueueFamilyProperties: Pointer[VkQueueFamilyProperties2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceSparseImageFormatProperties(
      physicalDevice: VkPhysicalDevice,
      format: VkFormat,
      `type`: VkImageType,
      samples: VkSampleCountFlagBits,
      usage: VkImageUsageFlags,
      tiling: VkImageTiling,
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkSparseImageFormatProperties, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceSparseImageFormatProperties2(
      physicalDevice: VkPhysicalDevice,
      pFormatInfo: Pointer[VkPhysicalDeviceSparseImageFormatInfo2, Read, Opaque],
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkSparseImageFormatProperties2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceSparseImageFormatProperties2KHR(
      physicalDevice: VkPhysicalDevice,
      pFormatInfo: Pointer[VkPhysicalDeviceSparseImageFormatInfo2, Read, Opaque],
      pPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkSparseImageFormatProperties2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPhysicalDeviceSupportedFramebufferMixedSamplesCombinationsNV(
      physicalDevice: VkPhysicalDevice,
      pCombinationCount: Pointer[uint32_t, ReadWrite, Opaque],
      pCombinations: Pointer[VkFramebufferMixedSamplesCombinationNV, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceSurfaceCapabilities2EXT(
      physicalDevice: VkPhysicalDevice,
      surface: VkSurfaceKHR,
      pSurfaceCapabilities: Pointer[VkSurfaceCapabilities2EXT, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceSurfaceCapabilities2KHR(
      physicalDevice: VkPhysicalDevice,
      pSurfaceInfo: Pointer[VkPhysicalDeviceSurfaceInfo2KHR, Read, Opaque],
      pSurfaceCapabilities: Pointer[VkSurfaceCapabilities2KHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceSurfaceCapabilitiesKHR(
      physicalDevice: VkPhysicalDevice,
      surface: VkSurfaceKHR,
      pSurfaceCapabilities: Pointer[VkSurfaceCapabilitiesKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceSurfaceFormats2KHR(
      physicalDevice: VkPhysicalDevice,
      pSurfaceInfo: Pointer[VkPhysicalDeviceSurfaceInfo2KHR, Read, Opaque],
      pSurfaceFormatCount: Pointer[uint32_t, ReadWrite, Opaque],
      pSurfaceFormats: Pointer[VkSurfaceFormat2KHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceSurfaceFormatsKHR(
      physicalDevice: VkPhysicalDevice,
      surface: VkSurfaceKHR,
      pSurfaceFormatCount: Pointer[uint32_t, ReadWrite, Opaque],
      pSurfaceFormats: Pointer[VkSurfaceFormatKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceSurfacePresentModesKHR(
      physicalDevice: VkPhysicalDevice,
      surface: VkSurfaceKHR,
      pPresentModeCount: Pointer[uint32_t, ReadWrite, Opaque],
      pPresentModes: Pointer[VkPresentModeKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceSurfaceSupportKHR(
      physicalDevice: VkPhysicalDevice,
      queueFamilyIndex: uint32_t,
      surface: VkSurfaceKHR,
      pSupported: Pointer[VkBool32, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceToolProperties(
      physicalDevice: VkPhysicalDevice,
      pToolCount: Pointer[uint32_t, ReadWrite, Opaque],
      pToolProperties: Pointer[VkPhysicalDeviceToolProperties, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceToolPropertiesEXT(
      physicalDevice: VkPhysicalDevice,
      pToolCount: Pointer[uint32_t, ReadWrite, Opaque],
      pToolProperties: Pointer[VkPhysicalDeviceToolProperties, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceVideoCapabilitiesKHR(
      physicalDevice: VkPhysicalDevice,
      pVideoProfile: Pointer[VkVideoProfileInfoKHR, Read, Opaque],
      pCapabilities: Pointer[VkVideoCapabilitiesKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceVideoEncodeQualityLevelPropertiesKHR(
      physicalDevice: VkPhysicalDevice,
      pQualityLevelInfo: Pointer[VkPhysicalDeviceVideoEncodeQualityLevelInfoKHR, Read, Opaque],
      pQualityLevelProperties: Pointer[VkVideoEncodeQualityLevelPropertiesKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPhysicalDeviceVideoFormatPropertiesKHR(
      physicalDevice: VkPhysicalDevice,
      pVideoFormatInfo: Pointer[VkPhysicalDeviceVideoFormatInfoKHR, Read, Opaque],
      pVideoFormatPropertyCount: Pointer[uint32_t, ReadWrite, Opaque],
      pVideoFormatProperties: Pointer[VkVideoFormatPropertiesKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPipelineBinaryDataKHR(
      device: VkDevice,
      pInfo: Pointer[VkPipelineBinaryDataInfoKHR, Read, Opaque],
      pPipelineBinaryKey: Pointer[VkPipelineBinaryKeyKHR, ReadWrite, Opaque],
      pPipelineBinaryDataSize: Pointer[size_t, ReadWrite, Opaque],
      pPipelineBinaryData: OpaquePointer
  ): VkResult = unsupported
  def vkGetPipelineCacheData(
      device: VkDevice,
      pipelineCache: VkPipelineCache,
      pDataSize: Pointer[size_t, ReadWrite, Opaque],
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetPipelineExecutableInternalRepresentationsKHR(
      device: VkDevice,
      pExecutableInfo: Pointer[VkPipelineExecutableInfoKHR, Read, Opaque],
      pInternalRepresentationCount: Pointer[uint32_t, ReadWrite, Opaque],
      pInternalRepresentations: Pointer[VkPipelineExecutableInternalRepresentationKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPipelineExecutablePropertiesKHR(
      device: VkDevice,
      pPipelineInfo: Pointer[VkPipelineInfoKHR, Read, Opaque],
      pExecutableCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[VkPipelineExecutablePropertiesKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPipelineExecutableStatisticsKHR(
      device: VkDevice,
      pExecutableInfo: Pointer[VkPipelineExecutableInfoKHR, Read, Opaque],
      pStatisticCount: Pointer[uint32_t, ReadWrite, Opaque],
      pStatistics: Pointer[VkPipelineExecutableStatisticKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPipelineIndirectDeviceAddressNV(
      device: VkDevice,
      pInfo: Pointer[VkPipelineIndirectDeviceAddressInfoNV, Read, Opaque]
  ): VkDeviceAddress = unsupported
  def vkGetPipelineIndirectMemoryRequirementsNV(
      device: VkDevice,
      pCreateInfo: Pointer[VkComputePipelineCreateInfo, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPipelineKeyKHR(
      device: VkDevice,
      pPipelineCreateInfo: Pointer[VkPipelineCreateInfoKHR, Read, Opaque],
      pPipelineKey: Pointer[VkPipelineBinaryKeyKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPipelinePropertiesEXT(
      device: VkDevice,
      pPipelineInfo: Pointer[VkPipelineInfoEXT, Read, Opaque],
      pPipelineProperties: Pointer[VkBaseOutStructure, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetPrivateData(
      device: VkDevice,
      objectType: VkObjectType,
      objectHandle: uint64_t,
      privateDataSlot: VkPrivateDataSlot,
      pData: Pointer[uint64_t, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetPrivateDataEXT(
      device: VkDevice,
      objectType: VkObjectType,
      objectHandle: uint64_t,
      privateDataSlot: VkPrivateDataSlot,
      pData: Pointer[uint64_t, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetQueryPoolResults(
      device: VkDevice,
      queryPool: VkQueryPool,
      firstQuery: uint32_t,
      queryCount: uint32_t,
      dataSize: size_t,
      pData: OpaquePointer,
      stride: VkDeviceSize,
      flags: VkQueryResultFlags
  ): VkResult = unsupported
  def vkGetQueueCheckpointData2NV(
      queue: VkQueue,
      pCheckpointDataCount: Pointer[uint32_t, ReadWrite, Opaque],
      pCheckpointData: Pointer[VkCheckpointData2NV, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetQueueCheckpointDataNV(
      queue: VkQueue,
      pCheckpointDataCount: Pointer[uint32_t, ReadWrite, Opaque],
      pCheckpointData: Pointer[VkCheckpointDataNV, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetRayTracingCaptureReplayShaderGroupHandlesKHR(
      device: VkDevice,
      pipeline: VkPipeline,
      firstGroup: uint32_t,
      groupCount: uint32_t,
      dataSize: size_t,
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetRayTracingShaderGroupHandlesKHR(
      device: VkDevice,
      pipeline: VkPipeline,
      firstGroup: uint32_t,
      groupCount: uint32_t,
      dataSize: size_t,
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetRayTracingShaderGroupHandlesNV(
      device: VkDevice,
      pipeline: VkPipeline,
      firstGroup: uint32_t,
      groupCount: uint32_t,
      dataSize: size_t,
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetRayTracingShaderGroupStackSizeKHR(
      device: VkDevice,
      pipeline: VkPipeline,
      group: uint32_t,
      groupShader: VkShaderGroupShaderKHR
  ): VkDeviceSize = unsupported
  def vkGetRefreshCycleDurationGOOGLE(
      device: VkDevice,
      swapchain: VkSwapchainKHR,
      pDisplayTimingProperties: Pointer[VkRefreshCycleDurationGOOGLE, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetRenderAreaGranularity(
      device: VkDevice,
      renderPass: VkRenderPass,
      pGranularity: Pointer[VkExtent2D, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetRenderingAreaGranularity(
      device: VkDevice,
      pRenderingAreaInfo: Pointer[VkRenderingAreaInfo, Read, Opaque],
      pGranularity: Pointer[VkExtent2D, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetRenderingAreaGranularityKHR(
      device: VkDevice,
      pRenderingAreaInfo: Pointer[VkRenderingAreaInfo, Read, Opaque],
      pGranularity: Pointer[VkExtent2D, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetSamplerOpaqueCaptureDescriptorDataEXT(
      device: VkDevice,
      pInfo: Pointer[VkSamplerCaptureDescriptorDataInfoEXT, Read, Opaque],
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetSemaphoreCounterValue(
      device: VkDevice,
      semaphore: VkSemaphore,
      pValue: Pointer[uint64_t, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetSemaphoreCounterValueKHR(
      device: VkDevice,
      semaphore: VkSemaphore,
      pValue: Pointer[uint64_t, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetSemaphoreFdKHR(
      device: VkDevice,
      pGetFdInfo: Pointer[VkSemaphoreGetFdInfoKHR, Read, Opaque],
      pFd: Pointer[Int, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetShaderBinaryDataEXT(
      device: VkDevice,
      shader: VkShaderEXT,
      pDataSize: Pointer[size_t, ReadWrite, Opaque],
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetShaderInfoAMD(
      device: VkDevice,
      pipeline: VkPipeline,
      shaderStage: VkShaderStageFlagBits,
      infoType: VkShaderInfoTypeAMD,
      pInfoSize: Pointer[size_t, ReadWrite, Opaque],
      pInfo: OpaquePointer
  ): VkResult = unsupported
  def vkGetShaderModuleCreateInfoIdentifierEXT(
      device: VkDevice,
      pCreateInfo: Pointer[VkShaderModuleCreateInfo, Read, Opaque],
      pIdentifier: Pointer[VkShaderModuleIdentifierEXT, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetShaderModuleIdentifierEXT(
      device: VkDevice,
      shaderModule: VkShaderModule,
      pIdentifier: Pointer[VkShaderModuleIdentifierEXT, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetSwapchainCounterEXT(
      device: VkDevice,
      swapchain: VkSwapchainKHR,
      counter: VkSurfaceCounterFlagBitsEXT,
      pCounterValue: Pointer[uint64_t, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetSwapchainImagesKHR(
      device: VkDevice,
      swapchain: VkSwapchainKHR,
      pSwapchainImageCount: Pointer[uint32_t, ReadWrite, Opaque],
      pSwapchainImages: Pointer[VkImage, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetSwapchainStatusKHR(device: VkDevice, swapchain: VkSwapchainKHR): VkResult = unsupported
  def vkGetSwapchainTimeDomainPropertiesEXT(
      device: VkDevice,
      swapchain: VkSwapchainKHR,
      pSwapchainTimeDomainProperties: Pointer[VkSwapchainTimeDomainPropertiesEXT, ReadWrite, Opaque],
      pTimeDomainsCounter: Pointer[uint64_t, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetSwapchainTimingPropertiesEXT(
      device: VkDevice,
      swapchain: VkSwapchainKHR,
      pSwapchainTimingProperties: Pointer[VkSwapchainTimingPropertiesEXT, ReadWrite, Opaque],
      pSwapchainTimingPropertiesCounter: Pointer[uint64_t, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetTensorMemoryRequirementsARM(
      device: VkDevice,
      pInfo: Pointer[VkTensorMemoryRequirementsInfoARM, Read, Opaque],
      pMemoryRequirements: Pointer[VkMemoryRequirements2, ReadWrite, Opaque]
  ): Unit = unsupported
  def vkGetTensorOpaqueCaptureDataARM(
      device: VkDevice,
      tensorCount: uint32_t,
      pTensors: Pointer[VkTensorARM, Read, Opaque],
      pDatas: Pointer[VkHostAddressRangeEXT, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkGetTensorOpaqueCaptureDescriptorDataARM(
      device: VkDevice,
      pInfo: Pointer[VkTensorCaptureDescriptorDataInfoARM, Read, Opaque],
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetTensorViewOpaqueCaptureDescriptorDataARM(
      device: VkDevice,
      pInfo: Pointer[VkTensorViewCaptureDescriptorDataInfoARM, Read, Opaque],
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetValidationCacheDataEXT(
      device: VkDevice,
      validationCache: VkValidationCacheEXT,
      pDataSize: Pointer[size_t, ReadWrite, Opaque],
      pData: OpaquePointer
  ): VkResult = unsupported
  def vkGetVideoSessionMemoryRequirementsKHR(
      device: VkDevice,
      videoSession: VkVideoSessionKHR,
      pMemoryRequirementsCount: Pointer[uint32_t, ReadWrite, Opaque],
      pMemoryRequirements: Pointer[VkVideoSessionMemoryRequirementsKHR, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkImportFenceFdKHR(
      device: VkDevice,
      pImportFenceFdInfo: Pointer[VkImportFenceFdInfoKHR, Read, Opaque]
  ): VkResult = unsupported
  def vkImportSemaphoreFdKHR(
      device: VkDevice,
      pImportSemaphoreFdInfo: Pointer[VkImportSemaphoreFdInfoKHR, Read, Opaque]
  ): VkResult = unsupported
  def vkInitializePerformanceApiINTEL(
      device: VkDevice,
      pInitializeInfo: Pointer[VkInitializePerformanceApiInfoINTEL, Read, Opaque]
  ): VkResult = unsupported
  def vkInvalidateMappedMemoryRanges(
      device: VkDevice,
      memoryRangeCount: uint32_t,
      pMemoryRanges: Pointer[VkMappedMemoryRange, Read, Opaque]
  ): VkResult = unsupported
  def vkLatencySleepNV(
      device: VkDevice,
      swapchain: VkSwapchainKHR,
      pSleepInfo: Pointer[VkLatencySleepInfoNV, Read, Opaque]
  ): VkResult = unsupported
  def vkMapMemory(
      device: VkDevice,
      memory: VkDeviceMemory,
      offset: VkDeviceSize,
      size: VkDeviceSize,
      flags: VkMemoryMapFlags,
      ppData: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkMapMemory2(
      device: VkDevice,
      pMemoryMapInfo: Pointer[VkMemoryMapInfo, Read, Opaque],
      ppData: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkMapMemory2KHR(
      device: VkDevice,
      pMemoryMapInfo: Pointer[VkMemoryMapInfo, Read, Opaque],
      ppData: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkMergePipelineCaches(
      device: VkDevice,
      dstCache: VkPipelineCache,
      srcCacheCount: uint32_t,
      pSrcCaches: Pointer[VkPipelineCache, Read, Opaque]
  ): VkResult = unsupported
  def vkMergeValidationCachesEXT(
      device: VkDevice,
      dstCache: VkValidationCacheEXT,
      srcCacheCount: uint32_t,
      pSrcCaches: Pointer[VkValidationCacheEXT, Read, Opaque]
  ): VkResult = unsupported
  def vkQueueBeginDebugUtilsLabelEXT(queue: VkQueue, pLabelInfo: Pointer[VkDebugUtilsLabelEXT, Read, Opaque]): Unit =
    unsupported
  def vkQueueBindSparse(
      queue: VkQueue,
      bindInfoCount: uint32_t,
      pBindInfo: Pointer[VkBindSparseInfo, Read, Opaque],
      fence: VkFence
  ): VkResult = unsupported
  def vkQueueEndDebugUtilsLabelEXT(queue: VkQueue): Unit = unsupported
  def vkQueueInsertDebugUtilsLabelEXT(queue: VkQueue, pLabelInfo: Pointer[VkDebugUtilsLabelEXT, Read, Opaque]): Unit =
    unsupported
  def vkQueueNotifyOutOfBandNV(
      queue: VkQueue,
      pQueueTypeInfo: Pointer[VkOutOfBandQueueTypeInfoNV, Read, Opaque]
  ): Unit = unsupported
  def vkQueuePresentKHR(queue: VkQueue, pPresentInfo: Pointer[VkPresentInfoKHR, Read, Opaque]): VkResult = unsupported
  def vkQueueSetPerformanceConfigurationINTEL(
      queue: VkQueue,
      configuration: VkPerformanceConfigurationINTEL
  ): VkResult = unsupported
  def vkQueueSubmit(
      queue: VkQueue,
      submitCount: uint32_t,
      pSubmits: Pointer[VkSubmitInfo, Read, Opaque],
      fence: VkFence
  ): VkResult = unsupported
  def vkQueueSubmit2(
      queue: VkQueue,
      submitCount: uint32_t,
      pSubmits: Pointer[VkSubmitInfo2, Read, Opaque],
      fence: VkFence
  ): VkResult = unsupported
  def vkQueueSubmit2KHR(
      queue: VkQueue,
      submitCount: uint32_t,
      pSubmits: Pointer[VkSubmitInfo2, Read, Opaque],
      fence: VkFence
  ): VkResult = unsupported
  def vkQueueWaitIdle(queue: VkQueue): VkResult = unsupported
  def vkRegisterCustomBorderColorEXT(
      device: VkDevice,
      pBorderColor: Pointer[VkSamplerCustomBorderColorCreateInfoEXT, Read, Opaque],
      requestIndex: VkBool32,
      pIndex: Pointer[uint32_t, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkRegisterDeviceEventEXT(
      device: VkDevice,
      pDeviceEventInfo: Pointer[VkDeviceEventInfoEXT, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pFence: Pointer[VkFence, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkRegisterDisplayEventEXT(
      device: VkDevice,
      display: VkDisplayKHR,
      pDisplayEventInfo: Pointer[VkDisplayEventInfoEXT, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque],
      pFence: Pointer[VkFence, ReadWrite, Opaque]
  ): VkResult = unsupported
  def vkReleaseCapturedPipelineDataKHR(
      device: VkDevice,
      pInfo: Pointer[VkReleaseCapturedPipelineDataInfoKHR, Read, Opaque],
      pAllocator: Pointer[VkAllocationCallbacks, Read, Opaque]
  ): VkResult = unsupported
  def vkReleaseDisplayEXT(physicalDevice: VkPhysicalDevice, display: VkDisplayKHR): VkResult = unsupported
  def vkReleasePerformanceConfigurationINTEL(
      device: VkDevice,
      configuration: VkPerformanceConfigurationINTEL
  ): VkResult = unsupported
  def vkReleaseProfilingLockKHR(device: VkDevice): Unit = unsupported
  def vkReleaseSwapchainImagesEXT(
      device: VkDevice,
      pReleaseInfo: Pointer[VkReleaseSwapchainImagesInfoKHR, Read, Opaque]
  ): VkResult = unsupported
  def vkReleaseSwapchainImagesKHR(
      device: VkDevice,
      pReleaseInfo: Pointer[VkReleaseSwapchainImagesInfoKHR, Read, Opaque]
  ): VkResult = unsupported
  def vkResetCommandBuffer(commandBuffer: VkCommandBuffer, flags: VkCommandBufferResetFlags): VkResult = unsupported
  def vkResetCommandPool(device: VkDevice, commandPool: VkCommandPool, flags: VkCommandPoolResetFlags): VkResult =
    unsupported
  def vkResetDescriptorPool(
      device: VkDevice,
      descriptorPool: VkDescriptorPool,
      flags: VkDescriptorPoolResetFlags
  ): VkResult = unsupported
  def vkResetEvent(device: VkDevice, event: VkEvent): VkResult = unsupported
  def vkResetFences(device: VkDevice, fenceCount: uint32_t, pFences: Pointer[VkFence, Read, Opaque]): VkResult =
    unsupported
  def vkResetQueryPool(device: VkDevice, queryPool: VkQueryPool, firstQuery: uint32_t, queryCount: uint32_t): Unit =
    unsupported
  def vkResetQueryPoolEXT(device: VkDevice, queryPool: VkQueryPool, firstQuery: uint32_t, queryCount: uint32_t): Unit =
    unsupported
  def vkSetDebugUtilsObjectNameEXT(
      device: VkDevice,
      pNameInfo: Pointer[VkDebugUtilsObjectNameInfoEXT, Read, Opaque]
  ): VkResult = unsupported
  def vkSetDebugUtilsObjectTagEXT(
      device: VkDevice,
      pTagInfo: Pointer[VkDebugUtilsObjectTagInfoEXT, Read, Opaque]
  ): VkResult = unsupported
  def vkSetDeviceMemoryPriorityEXT(device: VkDevice, memory: VkDeviceMemory, priority: Float): Unit = unsupported
  def vkSetEvent(device: VkDevice, event: VkEvent): VkResult = unsupported
  def vkSetHdrMetadataEXT(
      device: VkDevice,
      swapchainCount: uint32_t,
      pSwapchains: Pointer[VkSwapchainKHR, Read, Opaque],
      pMetadata: Pointer[VkHdrMetadataEXT, Read, Opaque]
  ): Unit = unsupported
  def vkSetLatencyMarkerNV(
      device: VkDevice,
      swapchain: VkSwapchainKHR,
      pLatencyMarkerInfo: Pointer[VkSetLatencyMarkerInfoNV, Read, Opaque]
  ): Unit = unsupported
  def vkSetLatencySleepModeNV(
      device: VkDevice,
      swapchain: VkSwapchainKHR,
      pSleepModeInfo: Pointer[VkLatencySleepModeInfoNV, Read, Opaque]
  ): VkResult = unsupported
  def vkSetLocalDimmingAMD(device: VkDevice, swapChain: VkSwapchainKHR, localDimmingEnable: VkBool32): Unit =
    unsupported
  def vkSetPrivateData(
      device: VkDevice,
      objectType: VkObjectType,
      objectHandle: uint64_t,
      privateDataSlot: VkPrivateDataSlot,
      data: uint64_t
  ): VkResult = unsupported
  def vkSetPrivateDataEXT(
      device: VkDevice,
      objectType: VkObjectType,
      objectHandle: uint64_t,
      privateDataSlot: VkPrivateDataSlot,
      data: uint64_t
  ): VkResult = unsupported
  def vkSetSwapchainPresentTimingQueueSizeEXT(device: VkDevice, swapchain: VkSwapchainKHR, size: uint32_t): VkResult =
    unsupported
  def vkSignalSemaphore(device: VkDevice, pSignalInfo: Pointer[VkSemaphoreSignalInfo, Read, Opaque]): VkResult =
    unsupported
  def vkSignalSemaphoreKHR(device: VkDevice, pSignalInfo: Pointer[VkSemaphoreSignalInfo, Read, Opaque]): VkResult =
    unsupported
  def vkSubmitDebugUtilsMessageEXT(
      instance: VkInstance,
      messageSeverity: VkDebugUtilsMessageSeverityFlagBitsEXT,
      messageTypes: VkDebugUtilsMessageTypeFlagsEXT,
      pCallbackData: Pointer[VkDebugUtilsMessengerCallbackDataEXT, Read, Opaque]
  ): Unit = unsupported
  def vkTransitionImageLayout(
      device: VkDevice,
      transitionCount: uint32_t,
      pTransitions: Pointer[VkHostImageLayoutTransitionInfo, Read, Opaque]
  ): VkResult = unsupported
  def vkTransitionImageLayoutEXT(
      device: VkDevice,
      transitionCount: uint32_t,
      pTransitions: Pointer[VkHostImageLayoutTransitionInfo, Read, Opaque]
  ): VkResult = unsupported
  def vkTrimCommandPool(device: VkDevice, commandPool: VkCommandPool, flags: VkCommandPoolTrimFlags): Unit = unsupported
  def vkTrimCommandPoolKHR(device: VkDevice, commandPool: VkCommandPool, flags: VkCommandPoolTrimFlags): Unit =
    unsupported
  def vkUninitializePerformanceApiINTEL(device: VkDevice): Unit = unsupported
  def vkUnmapMemory(device: VkDevice, memory: VkDeviceMemory): Unit = unsupported
  def vkUnmapMemory2(device: VkDevice, pMemoryUnmapInfo: Pointer[VkMemoryUnmapInfo, Read, Opaque]): VkResult =
    unsupported
  def vkUnmapMemory2KHR(device: VkDevice, pMemoryUnmapInfo: Pointer[VkMemoryUnmapInfo, Read, Opaque]): VkResult =
    unsupported
  def vkUnregisterCustomBorderColorEXT(device: VkDevice, index: uint32_t): Unit = unsupported
  def vkUpdateDescriptorSetWithTemplate(
      device: VkDevice,
      descriptorSet: VkDescriptorSet,
      descriptorUpdateTemplate: VkDescriptorUpdateTemplate,
      pData: Pointer[CVoid, Read, Opaque]
  ): Unit = unsupported
  def vkUpdateDescriptorSetWithTemplateKHR(
      device: VkDevice,
      descriptorSet: VkDescriptorSet,
      descriptorUpdateTemplate: VkDescriptorUpdateTemplate,
      pData: Pointer[CVoid, Read, Opaque]
  ): Unit = unsupported
  def vkUpdateDescriptorSets(
      device: VkDevice,
      descriptorWriteCount: uint32_t,
      pDescriptorWrites: Pointer[VkWriteDescriptorSet, Read, Opaque],
      descriptorCopyCount: uint32_t,
      pDescriptorCopies: Pointer[VkCopyDescriptorSet, Read, Opaque]
  ): Unit = unsupported
  def vkUpdateIndirectExecutionSetPipelineEXT(
      device: VkDevice,
      indirectExecutionSet: VkIndirectExecutionSetEXT,
      executionSetWriteCount: uint32_t,
      pExecutionSetWrites: Pointer[VkWriteIndirectExecutionSetPipelineEXT, Read, Opaque]
  ): Unit = unsupported
  def vkUpdateIndirectExecutionSetShaderEXT(
      device: VkDevice,
      indirectExecutionSet: VkIndirectExecutionSetEXT,
      executionSetWriteCount: uint32_t,
      pExecutionSetWrites: Pointer[VkWriteIndirectExecutionSetShaderEXT, Read, Opaque]
  ): Unit = unsupported
  def vkUpdateVideoSessionParametersKHR(
      device: VkDevice,
      videoSessionParameters: VkVideoSessionParametersKHR,
      pUpdateInfo: Pointer[VkVideoSessionParametersUpdateInfoKHR, Read, Opaque]
  ): VkResult = unsupported
  def vkWaitForFences(
      device: VkDevice,
      fenceCount: uint32_t,
      pFences: Pointer[VkFence, Read, Opaque],
      waitAll: VkBool32,
      timeout: uint64_t
  ): VkResult = unsupported
  def vkWaitForPresent2KHR(
      device: VkDevice,
      swapchain: VkSwapchainKHR,
      pPresentWait2Info: Pointer[VkPresentWait2InfoKHR, Read, Opaque]
  ): VkResult = unsupported
  def vkWaitForPresentKHR(
      device: VkDevice,
      swapchain: VkSwapchainKHR,
      presentId: uint64_t,
      timeout: uint64_t
  ): VkResult = unsupported
  def vkWaitSemaphores(
      device: VkDevice,
      pWaitInfo: Pointer[VkSemaphoreWaitInfo, Read, Opaque],
      timeout: uint64_t
  ): VkResult = unsupported
  def vkWaitSemaphoresKHR(
      device: VkDevice,
      pWaitInfo: Pointer[VkSemaphoreWaitInfo, Read, Opaque],
      timeout: uint64_t
  ): VkResult = unsupported
  def vkWriteAccelerationStructuresPropertiesKHR(
      device: VkDevice,
      accelerationStructureCount: uint32_t,
      pAccelerationStructures: Pointer[VkAccelerationStructureKHR, Read, Opaque],
      queryType: VkQueryType,
      dataSize: size_t,
      pData: OpaquePointer,
      stride: size_t
  ): VkResult = unsupported
  def vkWriteMicromapsPropertiesEXT(
      device: VkDevice,
      micromapCount: uint32_t,
      pMicromaps: Pointer[VkMicromapEXT, Read, Opaque],
      queryType: VkQueryType,
      dataSize: size_t,
      pData: OpaquePointer,
      stride: size_t
  ): VkResult = unsupported
  def vkWriteResourceDescriptorsEXT(
      device: VkDevice,
      resourceCount: uint32_t,
      pResources: Pointer[VkResourceDescriptorInfoEXT, Read, Opaque],
      pDescriptors: Pointer[VkHostAddressRangeEXT, Read, Opaque]
  ): VkResult = unsupported
  def vkWriteSamplerDescriptorsEXT(
      device: VkDevice,
      samplerCount: uint32_t,
      pSamplers: Pointer[VkSamplerCreateInfo, Read, Opaque],
      pDescriptors: Pointer[VkHostAddressRangeEXT, Read, Opaque]
  ): VkResult = unsupported
}

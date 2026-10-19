package net.kurobako.gemstone.codegen

/** Vulkan implementation policies for every function in the pinned headers. */
object VulkanAdapter {
  private enum Policy derives CanEqual {
    case Global, Device, Query, DeviceVoid, CommandResult, Record, Destroy, Manual, Unsupported
    case Create(destroy: String, scope: String)
    case Alias(target: String)
  }
  import Policy.*
  private case class Entry(policy: Policy, minimumVersion: Option[String])
  private def group(
      policy: Policy,
      names: String,
      minimumVersion: Option[String] = Some("VK_API_VERSION_1_0")
  ): Vector[(String, Entry)] = {
    val entry = Entry(policy, minimumVersion)
    names.trim.split("\\s+").toVector.map(_ -> entry)
  }
  private def aliases(names: String): Vector[(String, Entry)] = names.trim.split("\\s+").toVector.map { name =>
    name -> Entry(Alias(name.stripSuffix("KHR").stripSuffix("EXT")), Some("VK_API_VERSION_1_0"))
  }
  private val entries = Vector(
    aliases("""vkBindBufferMemory2KHR vkBindImageMemory2KHR vkCmdBeginRenderPass2KHR vkCmdDispatchBaseKHR vkCmdEndRenderPass2KHR
vkCmdNextSubpass2KHR vkCreateDescriptorUpdateTemplateKHR vkCreateRenderPass2KHR vkDestroyDescriptorUpdateTemplateKHR
vkEnumeratePhysicalDeviceGroupsKHR vkGetBufferMemoryRequirements2KHR vkGetImageMemoryRequirements2KHR
vkGetImageSparseMemoryRequirements2KHR vkGetPhysicalDeviceFeatures2KHR vkGetPhysicalDeviceFormatProperties2KHR
vkGetPhysicalDeviceImageFormatProperties2KHR vkGetPhysicalDeviceMemoryProperties2KHR
vkGetPhysicalDeviceProperties2KHR vkGetPhysicalDeviceQueueFamilyProperties2KHR
vkGetPhysicalDeviceSparseImageFormatProperties2KHR vkGetSemaphoreCounterValueKHR vkResetQueryPoolEXT
vkSignalSemaphoreKHR vkTrimCommandPoolKHR vkUpdateDescriptorSetWithTemplateKHR vkWaitSemaphoresKHR"""),
    group(
      Global,
      """vkEnumerateInstanceVersion vkEnumerateInstanceExtensionProperties vkEnumerateInstanceLayerProperties"""
    ),
    group(Create("vkDestroyInstance", "INSTANCE"), """vkCreateInstance"""),
    group(
      Destroy,
      """vkDestroyInstance vkDestroyDevice vkFreeMemory vkDestroyBufferView vkDestroyBuffer
vkDestroyDescriptorSetLayout vkDestroyDescriptorPool vkDestroyPipelineLayout vkDestroyPipelineCache
vkDestroyShaderModule vkDestroyPipeline vkDestroyCommandPool vkDestroyEvent vkDestroySampler
vkDestroyImage vkDestroyImageView vkDestroyRenderPass vkDestroyFramebuffer vkDestroyQueryPool
vkDestroyDescriptorUpdateTemplate vkDestroySemaphore vkDestroyFence"""
    ),
    group(
      Device,
      """vkEnumeratePhysicalDevices vkEnumerateDeviceExtensionProperties vkEnumerateDeviceLayerProperties
vkMapMemory vkFlushMappedMemoryRanges vkInvalidateMappedMemoryRanges vkBindBufferMemory
vkBindBufferMemory2 vkFreeDescriptorSets vkGetPipelineCacheData vkMergePipelineCaches vkGetEventStatus
vkSetEvent vkResetEvent vkEnumeratePhysicalDeviceGroups vkGetPhysicalDeviceImageFormatProperties
vkGetPhysicalDeviceImageFormatProperties2 vkBindImageMemory vkBindImageMemory2 vkGetQueryPoolResults
vkGetFenceStatus vkResetFences vkWaitForFences vkQueueSubmit vkQueueWaitIdle vkDeviceWaitIdle
vkGetSemaphoreCounterValue vkSignalSemaphore vkWaitSemaphores"""
    ),
    group(
      Query,
      """vkGetPhysicalDeviceProperties vkGetPhysicalDeviceProperties2 vkGetPhysicalDeviceFeatures
vkGetPhysicalDeviceFeatures2 vkGetPhysicalDeviceMemoryProperties vkGetPhysicalDeviceMemoryProperties2
vkGetPhysicalDeviceQueueFamilyProperties vkGetPhysicalDeviceSparseImageFormatProperties
vkGetPhysicalDeviceSparseImageFormatProperties2 vkGetPhysicalDeviceQueueFamilyProperties2
vkGetPhysicalDeviceFormatProperties vkGetPhysicalDeviceFormatProperties2"""
    ),
    group(Create("vkDestroyDevice", "DEVICE"), """vkCreateDevice"""),
    group(
      DeviceVoid,
      """vkGetDeviceQueue vkGetDeviceQueue2 vkGetDeviceMemoryCommitment vkUnmapMemory vkGetBufferMemoryRequirements
vkGetBufferMemoryRequirements2 vkUpdateDescriptorSets vkFreeCommandBuffers
vkGetImageSparseMemoryRequirements vkGetImageSparseMemoryRequirements2 vkGetImageMemoryRequirements
vkGetImageMemoryRequirements2 vkGetImageSubresourceLayout vkGetRenderAreaGranularity vkResetQueryPool
vkUpdateDescriptorSetWithTemplate"""
    ),
    group(Create("vkFreeMemory", "OBJECT"), """vkAllocateMemory"""),
    group(Create("vkDestroyBuffer", "OBJECT"), """vkCreateBuffer"""),
    group(Create("vkDestroyBufferView", "OBJECT"), """vkCreateBufferView"""),
    group(Create("vkDestroyDescriptorSetLayout", "OBJECT"), """vkCreateDescriptorSetLayout"""),
    group(Create("vkDestroyDescriptorPool", "OBJECT"), """vkCreateDescriptorPool"""),
    group(
      Manual,
      """vkResetDescriptorPool vkAllocateDescriptorSets vkCreateComputePipelines vkResetCommandPool
vkAllocateCommandBuffers vkResetCommandBuffer vkTrimCommandPool vkCreateGraphicsPipelines
vkGetInstanceProcAddr vkGetDeviceProcAddr"""
    ),
    group(Create("vkDestroyPipelineLayout", "OBJECT"), """vkCreatePipelineLayout"""),
    group(Create("vkDestroyPipelineCache", "CACHE"), """vkCreatePipelineCache"""),
    group(Create("vkDestroyShaderModule", "OBJECT"), """vkCreateShaderModule"""),
    group(Create("vkDestroyCommandPool", "OBJECT"), """vkCreateCommandPool"""),
    group(CommandResult, """vkBeginCommandBuffer vkEndCommandBuffer"""),
    group(
      Record,
      """vkCmdBindPipeline vkCmdBindDescriptorSets vkCmdPushConstants vkCmdDispatch vkCmdDispatchBase
vkCmdCopyBuffer vkCmdSetEvent vkCmdWaitEvents vkCmdExecuteCommands vkCmdUpdateBuffer vkCmdDispatchIndirect
vkCmdResetEvent vkCmdPipelineBarrier vkCmdFillBuffer vkCmdBindVertexBuffers vkCmdSetViewport
vkCmdSetScissor vkCmdBeginRenderPass vkCmdNextSubpass vkCmdEndRenderPass vkCmdDraw vkCmdBindIndexBuffer
vkCmdDrawIndexed vkCmdDrawIndirect vkCmdDrawIndexedIndirect vkCmdCopyImageToBuffer vkCmdCopyBufferToImage
vkCmdClearAttachments vkCmdClearColorImage vkCmdClearDepthStencilImage vkCmdCopyImage vkCmdSetLineWidth
vkCmdSetDepthBias vkCmdSetBlendConstants vkCmdSetDepthBounds vkCmdSetStencilCompareMask
vkCmdSetStencilWriteMask vkCmdSetStencilReference vkCmdBeginRenderPass2 vkCmdNextSubpass2
vkCmdEndRenderPass2 vkCmdResetQueryPool vkCmdBeginQuery vkCmdEndQuery vkCmdWriteTimestamp
vkCmdCopyQueryPoolResults vkCmdResolveImage vkCmdBlitImage"""
    ),
    group(Create("vkDestroyEvent", "OBJECT"), """vkCreateEvent"""),
    group(Create("vkDestroySampler", "OBJECT"), """vkCreateSampler"""),
    group(Create("vkDestroyImage", "OBJECT"), """vkCreateImage"""),
    group(Create("vkDestroyImageView", "OBJECT"), """vkCreateImageView"""),
    group(Create("vkDestroyRenderPass", "OBJECT"), """vkCreateRenderPass vkCreateRenderPass2"""),
    group(Create("vkDestroyFramebuffer", "OBJECT"), """vkCreateFramebuffer"""),
    group(Create("vkDestroyQueryPool", "OBJECT"), """vkCreateQueryPool"""),
    group(Create("vkDestroyDescriptorUpdateTemplate", "OBJECT"), """vkCreateDescriptorUpdateTemplate"""),
    group(Create("vkDestroySemaphore", "OBJECT"), """vkCreateSemaphore"""),
    group(Create("vkDestroyFence", "OBJECT"), """vkCreateFence"""),
    group(
      Manual,
      """vkCmdSetDeviceMask vkDestroySamplerYcbcrConversion vkGetDescriptorSetLayoutSupport
vkGetDeviceGroupPeerMemoryFeatures vkGetPhysicalDeviceExternalBufferProperties
vkGetPhysicalDeviceExternalFenceProperties vkGetPhysicalDeviceExternalSemaphoreProperties""",
      minimumVersion = Some("VK_API_VERSION_1_1")
    ),
    group(
      Unsupported,
      """vkAcquireDrmDisplayEXT vkAcquireNextImage2KHR vkAcquireNextImageKHR vkAcquirePerformanceConfigurationINTEL
vkAcquireProfilingLockKHR vkAntiLagUpdateAMD vkBindAccelerationStructureMemoryNV
vkBindDataGraphPipelineSessionMemoryARM vkBindOpticalFlowSessionImageNV vkBindTensorMemoryARM
vkBindVideoSessionMemoryKHR vkBuildAccelerationStructuresKHR vkBuildMicromapsEXT
vkCmdBeginConditionalRenderingEXT vkCmdBeginCustomResolveEXT vkCmdBeginDebugUtilsLabelEXT
vkCmdBeginPerTileExecutionQCOM vkCmdBeginQueryIndexedEXT vkCmdBeginRendering vkCmdBeginRenderingKHR
vkCmdBeginTransformFeedbackEXT vkCmdBeginVideoCodingKHR vkCmdBindDescriptorBufferEmbeddedSamplers2EXT
vkCmdBindDescriptorBufferEmbeddedSamplersEXT vkCmdBindDescriptorBuffersEXT vkCmdBindDescriptorSets2
vkCmdBindDescriptorSets2KHR vkCmdBindIndexBuffer2 vkCmdBindIndexBuffer2KHR vkCmdBindInvocationMaskHUAWEI
vkCmdBindPipelineShaderGroupNV vkCmdBindResourceHeapEXT vkCmdBindSamplerHeapEXT vkCmdBindShadersEXT
vkCmdBindShadingRateImageNV vkCmdBindTileMemoryQCOM vkCmdBindTransformFeedbackBuffersEXT
vkCmdBindVertexBuffers2 vkCmdBindVertexBuffers2EXT vkCmdBlitImage2 vkCmdBlitImage2KHR
vkCmdBuildAccelerationStructureNV vkCmdBuildAccelerationStructuresIndirectKHR
vkCmdBuildAccelerationStructuresKHR vkCmdBuildClusterAccelerationStructureIndirectNV
vkCmdBuildMicromapsEXT vkCmdBuildPartitionedAccelerationStructuresNV vkCmdControlVideoCodingKHR
vkCmdConvertCooperativeVectorMatrixNV vkCmdCopyAccelerationStructureKHR vkCmdCopyAccelerationStructureNV
vkCmdCopyAccelerationStructureToMemoryKHR vkCmdCopyBuffer2 vkCmdCopyBuffer2KHR vkCmdCopyBufferToImage2
vkCmdCopyBufferToImage2KHR vkCmdCopyImage2 vkCmdCopyImage2KHR vkCmdCopyImageToBuffer2
vkCmdCopyImageToBuffer2KHR vkCmdCopyMemoryIndirectKHR vkCmdCopyMemoryIndirectNV
vkCmdCopyMemoryToAccelerationStructureKHR vkCmdCopyMemoryToImageIndirectKHR
vkCmdCopyMemoryToImageIndirectNV vkCmdCopyMemoryToMicromapEXT vkCmdCopyMicromapEXT
vkCmdCopyMicromapToMemoryEXT vkCmdCopyTensorARM vkCmdCuLaunchKernelNVX vkCmdDebugMarkerBeginEXT
vkCmdDebugMarkerEndEXT vkCmdDebugMarkerInsertEXT vkCmdDecodeVideoKHR vkCmdDecompressMemoryEXT
vkCmdDecompressMemoryIndirectCountEXT vkCmdDecompressMemoryIndirectCountNV vkCmdDecompressMemoryNV
vkCmdDispatchDataGraphARM vkCmdDispatchTileQCOM vkCmdDrawClusterHUAWEI vkCmdDrawClusterIndirectHUAWEI
vkCmdDrawIndexedIndirectCountAMD vkCmdDrawIndexedIndirectCountKHR vkCmdDrawIndirectByteCountEXT
vkCmdDrawIndirectCountAMD vkCmdDrawIndirectCountKHR vkCmdDrawMeshTasksEXT
vkCmdDrawMeshTasksIndirectCountEXT vkCmdDrawMeshTasksIndirectCountNV vkCmdDrawMeshTasksIndirectEXT
vkCmdDrawMeshTasksIndirectNV vkCmdDrawMeshTasksNV vkCmdDrawMultiEXT vkCmdDrawMultiIndexedEXT
vkCmdEncodeVideoKHR vkCmdEndConditionalRenderingEXT vkCmdEndDebugUtilsLabelEXT
vkCmdEndPerTileExecutionQCOM vkCmdEndQueryIndexedEXT vkCmdEndRendering vkCmdEndRendering2EXT
vkCmdEndRendering2KHR vkCmdEndRenderingKHR vkCmdEndTransformFeedbackEXT vkCmdEndVideoCodingKHR
vkCmdExecuteGeneratedCommandsEXT vkCmdExecuteGeneratedCommandsNV vkCmdInsertDebugUtilsLabelEXT
vkCmdOpticalFlowExecuteNV vkCmdPipelineBarrier2 vkCmdPipelineBarrier2KHR
vkCmdPreprocessGeneratedCommandsEXT vkCmdPreprocessGeneratedCommandsNV vkCmdPushConstants2
vkCmdPushConstants2KHR vkCmdPushDataEXT vkCmdPushDescriptorSet vkCmdPushDescriptorSet2
vkCmdPushDescriptorSet2KHR vkCmdPushDescriptorSetKHR vkCmdPushDescriptorSetWithTemplate
vkCmdPushDescriptorSetWithTemplate2 vkCmdPushDescriptorSetWithTemplate2KHR
vkCmdPushDescriptorSetWithTemplateKHR vkCmdResetEvent2 vkCmdResetEvent2KHR vkCmdResolveImage2
vkCmdResolveImage2KHR vkCmdSetAlphaToCoverageEnableEXT vkCmdSetAlphaToOneEnableEXT
vkCmdSetAttachmentFeedbackLoopEnableEXT vkCmdSetCheckpointNV vkCmdSetCoarseSampleOrderNV
vkCmdSetColorBlendAdvancedEXT vkCmdSetColorBlendEnableEXT vkCmdSetColorBlendEquationEXT
vkCmdSetColorWriteEnableEXT vkCmdSetColorWriteMaskEXT vkCmdSetComputeOccupancyPriorityNV
vkCmdSetConservativeRasterizationModeEXT vkCmdSetCoverageModulationModeNV
vkCmdSetCoverageModulationTableEnableNV vkCmdSetCoverageModulationTableNV vkCmdSetCoverageReductionModeNV
vkCmdSetCoverageToColorEnableNV vkCmdSetCoverageToColorLocationNV vkCmdSetCullMode vkCmdSetCullModeEXT
vkCmdSetDepthBias2EXT vkCmdSetDepthBiasEnable vkCmdSetDepthBiasEnableEXT vkCmdSetDepthBoundsTestEnable
vkCmdSetDepthBoundsTestEnableEXT vkCmdSetDepthClampEnableEXT vkCmdSetDepthClampRangeEXT
vkCmdSetDepthClipEnableEXT vkCmdSetDepthClipNegativeOneToOneEXT vkCmdSetDepthCompareOp
vkCmdSetDepthCompareOpEXT vkCmdSetDepthTestEnable vkCmdSetDepthTestEnableEXT vkCmdSetDepthWriteEnable
vkCmdSetDepthWriteEnableEXT vkCmdSetDescriptorBufferOffsets2EXT vkCmdSetDescriptorBufferOffsetsEXT
vkCmdSetDeviceMaskKHR vkCmdSetDiscardRectangleEXT vkCmdSetDiscardRectangleEnableEXT
vkCmdSetDiscardRectangleModeEXT vkCmdSetEvent2 vkCmdSetEvent2KHR vkCmdSetExclusiveScissorEnableNV
vkCmdSetExclusiveScissorNV vkCmdSetExtraPrimitiveOverestimationSizeEXT vkCmdSetFragmentShadingRateEnumNV
vkCmdSetFragmentShadingRateKHR vkCmdSetFrontFace vkCmdSetFrontFaceEXT vkCmdSetLineRasterizationModeEXT
vkCmdSetLineStipple vkCmdSetLineStippleEXT vkCmdSetLineStippleEnableEXT vkCmdSetLineStippleKHR
vkCmdSetLogicOpEXT vkCmdSetLogicOpEnableEXT vkCmdSetPatchControlPointsEXT vkCmdSetPerformanceMarkerINTEL
vkCmdSetPerformanceOverrideINTEL vkCmdSetPerformanceStreamMarkerINTEL vkCmdSetPolygonModeEXT
vkCmdSetPrimitiveRestartEnable vkCmdSetPrimitiveRestartEnableEXT vkCmdSetPrimitiveTopology
vkCmdSetPrimitiveTopologyEXT vkCmdSetProvokingVertexModeEXT vkCmdSetRasterizationSamplesEXT
vkCmdSetRasterizationStreamEXT vkCmdSetRasterizerDiscardEnable vkCmdSetRasterizerDiscardEnableEXT
vkCmdSetRayTracingPipelineStackSizeKHR vkCmdSetRenderingAttachmentLocations
vkCmdSetRenderingAttachmentLocationsKHR vkCmdSetRenderingInputAttachmentIndices
vkCmdSetRenderingInputAttachmentIndicesKHR vkCmdSetRepresentativeFragmentTestEnableNV
vkCmdSetSampleLocationsEXT vkCmdSetSampleLocationsEnableEXT vkCmdSetSampleMaskEXT vkCmdSetScissorWithCount
vkCmdSetScissorWithCountEXT vkCmdSetShadingRateImageEnableNV vkCmdSetStencilOp vkCmdSetStencilOpEXT
vkCmdSetStencilTestEnable vkCmdSetStencilTestEnableEXT vkCmdSetTessellationDomainOriginEXT
vkCmdSetVertexInputEXT vkCmdSetViewportShadingRatePaletteNV vkCmdSetViewportSwizzleNV
vkCmdSetViewportWScalingEnableNV vkCmdSetViewportWScalingNV vkCmdSetViewportWithCount
vkCmdSetViewportWithCountEXT vkCmdSubpassShadingHUAWEI vkCmdTraceRaysIndirect2KHR
vkCmdTraceRaysIndirectKHR vkCmdTraceRaysKHR vkCmdTraceRaysNV vkCmdUpdatePipelineIndirectBufferNV
vkCmdWaitEvents2 vkCmdWaitEvents2KHR vkCmdWriteAccelerationStructuresPropertiesKHR
vkCmdWriteAccelerationStructuresPropertiesNV vkCmdWriteBufferMarker2AMD vkCmdWriteBufferMarkerAMD
vkCmdWriteMicromapsPropertiesEXT vkCmdWriteTimestamp2 vkCmdWriteTimestamp2KHR vkCompileDeferredNV
vkConvertCooperativeVectorMatrixNV vkCopyAccelerationStructureKHR vkCopyAccelerationStructureToMemoryKHR
vkCopyImageToImage vkCopyImageToImageEXT vkCopyImageToMemory vkCopyImageToMemoryEXT
vkCopyMemoryToAccelerationStructureKHR vkCopyMemoryToImage vkCopyMemoryToImageEXT
vkCopyMemoryToMicromapEXT vkCopyMicromapEXT vkCopyMicromapToMemoryEXT vkCreateAccelerationStructureKHR
vkCreateAccelerationStructureNV vkCreateCuFunctionNVX vkCreateCuModuleNVX
vkCreateDataGraphPipelineSessionARM vkCreateDataGraphPipelinesARM vkCreateDebugReportCallbackEXT
vkCreateDebugUtilsMessengerEXT vkCreateDeferredOperationKHR vkCreateDisplayModeKHR
vkCreateDisplayPlaneSurfaceKHR vkCreateExternalComputeQueueNV vkCreateHeadlessSurfaceEXT
vkCreateIndirectCommandsLayoutEXT vkCreateIndirectCommandsLayoutNV vkCreateIndirectExecutionSetEXT
vkCreateMicromapEXT vkCreateOpticalFlowSessionNV vkCreatePipelineBinariesKHR vkCreatePrivateDataSlot
vkCreatePrivateDataSlotEXT vkCreateRayTracingPipelinesKHR vkCreateRayTracingPipelinesNV
vkCreateSamplerYcbcrConversionKHR vkCreateShadersEXT vkCreateSharedSwapchainsKHR vkCreateSwapchainKHR
vkCreateTensorARM vkCreateTensorViewARM vkCreateValidationCacheEXT vkCreateVideoSessionKHR
vkCreateVideoSessionParametersKHR vkDebugMarkerSetObjectNameEXT vkDebugMarkerSetObjectTagEXT
vkDebugReportMessageEXT vkDeferredOperationJoinKHR vkDestroyAccelerationStructureKHR
vkDestroyAccelerationStructureNV vkDestroyCuFunctionNVX vkDestroyCuModuleNVX
vkDestroyDataGraphPipelineSessionARM vkDestroyDebugReportCallbackEXT vkDestroyDebugUtilsMessengerEXT
vkDestroyDeferredOperationKHR vkDestroyExternalComputeQueueNV vkDestroyIndirectCommandsLayoutEXT
vkDestroyIndirectCommandsLayoutNV vkDestroyIndirectExecutionSetEXT vkDestroyMicromapEXT
vkDestroyOpticalFlowSessionNV vkDestroyPipelineBinaryKHR vkDestroyPrivateDataSlot
vkDestroyPrivateDataSlotEXT vkDestroySamplerYcbcrConversionKHR vkDestroyShaderEXT vkDestroySurfaceKHR
vkDestroySwapchainKHR vkDestroyTensorARM vkDestroyTensorViewARM vkDestroyValidationCacheEXT
vkDestroyVideoSessionKHR vkDestroyVideoSessionParametersKHR vkDisplayPowerControlEXT
vkEnumeratePhysicalDeviceQueueFamilyPerformanceCountersByRegionARM
vkEnumeratePhysicalDeviceQueueFamilyPerformanceQueryCountersKHR vkGetAccelerationStructureBuildSizesKHR
vkGetAccelerationStructureDeviceAddressKHR vkGetAccelerationStructureHandleNV
vkGetAccelerationStructureMemoryRequirementsNV vkGetAccelerationStructureOpaqueCaptureDescriptorDataEXT
vkGetBufferDeviceAddressEXT vkGetBufferDeviceAddressKHR vkGetBufferOpaqueCaptureAddressKHR
vkGetBufferOpaqueCaptureDescriptorDataEXT vkGetCalibratedTimestampsEXT vkGetCalibratedTimestampsKHR
vkGetClusterAccelerationStructureBuildSizesNV vkGetDataGraphPipelineAvailablePropertiesARM
vkGetDataGraphPipelinePropertiesARM vkGetDataGraphPipelineSessionBindPointRequirementsARM
vkGetDataGraphPipelineSessionMemoryRequirementsARM vkGetDeferredOperationMaxConcurrencyKHR
vkGetDeferredOperationResultKHR vkGetDescriptorEXT vkGetDescriptorSetHostMappingVALVE
vkGetDescriptorSetLayoutBindingOffsetEXT vkGetDescriptorSetLayoutHostMappingInfoVALVE
vkGetDescriptorSetLayoutSizeEXT vkGetDescriptorSetLayoutSupportKHR
vkGetDeviceAccelerationStructureCompatibilityKHR vkGetDeviceBufferMemoryRequirements
vkGetDeviceBufferMemoryRequirementsKHR vkGetDeviceCombinedImageSamplerIndexNVX vkGetDeviceFaultInfoEXT
vkGetDeviceGroupPeerMemoryFeaturesKHR vkGetDeviceGroupPresentCapabilitiesKHR
vkGetDeviceGroupSurfacePresentModesKHR vkGetDeviceImageMemoryRequirements
vkGetDeviceImageMemoryRequirementsKHR vkGetDeviceImageSparseMemoryRequirements
vkGetDeviceImageSparseMemoryRequirementsKHR vkGetDeviceImageSubresourceLayout
vkGetDeviceImageSubresourceLayoutKHR vkGetDeviceMemoryOpaqueCaptureAddressKHR
vkGetDeviceMicromapCompatibilityEXT vkGetDeviceSubpassShadingMaxWorkgroupSizeHUAWEI
vkGetDeviceTensorMemoryRequirementsARM vkGetDisplayModeProperties2KHR vkGetDisplayModePropertiesKHR
vkGetDisplayPlaneCapabilities2KHR vkGetDisplayPlaneCapabilitiesKHR vkGetDisplayPlaneSupportedDisplaysKHR
vkGetDrmDisplayEXT vkGetDynamicRenderingTilePropertiesQCOM vkGetEncodedVideoSessionParametersKHR
vkGetExternalComputeQueueDataNV vkGetFenceFdKHR vkGetFramebufferTilePropertiesQCOM
vkGetGeneratedCommandsMemoryRequirementsEXT vkGetGeneratedCommandsMemoryRequirementsNV
vkGetImageDrmFormatModifierPropertiesEXT vkGetImageOpaqueCaptureDataEXT
vkGetImageOpaqueCaptureDescriptorDataEXT vkGetImageSubresourceLayout2 vkGetImageSubresourceLayout2EXT
vkGetImageSubresourceLayout2KHR vkGetImageViewAddressNVX vkGetImageViewHandle64NVX vkGetImageViewHandleNVX
vkGetImageViewOpaqueCaptureDescriptorDataEXT vkGetLatencyTimingsNV vkGetMemoryFdKHR
vkGetMemoryFdPropertiesKHR vkGetMemoryHostPointerPropertiesEXT vkGetMemoryRemoteAddressNV
vkGetMicromapBuildSizesEXT vkGetPartitionedAccelerationStructuresBuildSizesNV
vkGetPastPresentationTimingEXT vkGetPastPresentationTimingGOOGLE vkGetPerformanceParameterINTEL
vkGetPhysicalDeviceCalibrateableTimeDomainsEXT vkGetPhysicalDeviceCalibrateableTimeDomainsKHR
vkGetPhysicalDeviceCooperativeMatrixFlexibleDimensionsPropertiesNV
vkGetPhysicalDeviceCooperativeMatrixPropertiesKHR vkGetPhysicalDeviceCooperativeMatrixPropertiesNV
vkGetPhysicalDeviceCooperativeVectorPropertiesNV vkGetPhysicalDeviceDescriptorSizeEXT
vkGetPhysicalDeviceDisplayPlaneProperties2KHR vkGetPhysicalDeviceDisplayPlanePropertiesKHR
vkGetPhysicalDeviceDisplayProperties2KHR vkGetPhysicalDeviceDisplayPropertiesKHR
vkGetPhysicalDeviceExternalBufferPropertiesKHR vkGetPhysicalDeviceExternalFencePropertiesKHR
vkGetPhysicalDeviceExternalImageFormatPropertiesNV vkGetPhysicalDeviceExternalSemaphorePropertiesKHR
vkGetPhysicalDeviceExternalTensorPropertiesARM vkGetPhysicalDeviceFragmentShadingRatesKHR
vkGetPhysicalDeviceMultisamplePropertiesEXT vkGetPhysicalDeviceOpticalFlowImageFormatsNV
vkGetPhysicalDevicePresentRectanglesKHR
vkGetPhysicalDeviceQueueFamilyDataGraphProcessingEnginePropertiesARM
vkGetPhysicalDeviceQueueFamilyDataGraphPropertiesARM
vkGetPhysicalDeviceQueueFamilyPerformanceQueryPassesKHR
vkGetPhysicalDeviceSupportedFramebufferMixedSamplesCombinationsNV
vkGetPhysicalDeviceSurfaceCapabilities2EXT vkGetPhysicalDeviceSurfaceCapabilities2KHR
vkGetPhysicalDeviceSurfaceCapabilitiesKHR vkGetPhysicalDeviceSurfaceFormats2KHR
vkGetPhysicalDeviceSurfaceFormatsKHR vkGetPhysicalDeviceSurfacePresentModesKHR
vkGetPhysicalDeviceSurfaceSupportKHR vkGetPhysicalDeviceToolProperties
vkGetPhysicalDeviceToolPropertiesEXT vkGetPhysicalDeviceVideoCapabilitiesKHR
vkGetPhysicalDeviceVideoEncodeQualityLevelPropertiesKHR vkGetPhysicalDeviceVideoFormatPropertiesKHR
vkGetPipelineBinaryDataKHR vkGetPipelineExecutableInternalRepresentationsKHR
vkGetPipelineExecutablePropertiesKHR vkGetPipelineExecutableStatisticsKHR
vkGetPipelineIndirectDeviceAddressNV vkGetPipelineIndirectMemoryRequirementsNV vkGetPipelineKeyKHR
vkGetPipelinePropertiesEXT vkGetPrivateData vkGetPrivateDataEXT vkGetQueueCheckpointData2NV
vkGetQueueCheckpointDataNV vkGetRayTracingCaptureReplayShaderGroupHandlesKHR
vkGetRayTracingShaderGroupHandlesKHR vkGetRayTracingShaderGroupHandlesNV
vkGetRayTracingShaderGroupStackSizeKHR vkGetRefreshCycleDurationGOOGLE vkGetRenderingAreaGranularity
vkGetRenderingAreaGranularityKHR vkGetSamplerOpaqueCaptureDescriptorDataEXT vkGetSemaphoreFdKHR
vkGetShaderBinaryDataEXT vkGetShaderInfoAMD vkGetShaderModuleCreateInfoIdentifierEXT
vkGetShaderModuleIdentifierEXT vkGetSwapchainCounterEXT vkGetSwapchainImagesKHR vkGetSwapchainStatusKHR
vkGetSwapchainTimeDomainPropertiesEXT vkGetSwapchainTimingPropertiesEXT vkGetTensorMemoryRequirementsARM
vkGetTensorOpaqueCaptureDataARM vkGetTensorOpaqueCaptureDescriptorDataARM
vkGetTensorViewOpaqueCaptureDescriptorDataARM vkGetValidationCacheDataEXT
vkGetVideoSessionMemoryRequirementsKHR vkImportFenceFdKHR vkImportSemaphoreFdKHR
vkInitializePerformanceApiINTEL vkLatencySleepNV vkMapMemory2 vkMapMemory2KHR vkMergeValidationCachesEXT
vkQueueBeginDebugUtilsLabelEXT vkQueueEndDebugUtilsLabelEXT vkQueueInsertDebugUtilsLabelEXT
vkQueueNotifyOutOfBandNV vkQueuePresentKHR vkQueueSetPerformanceConfigurationINTEL vkQueueSubmit2
vkQueueSubmit2KHR vkRegisterCustomBorderColorEXT vkRegisterDeviceEventEXT vkRegisterDisplayEventEXT
vkReleaseCapturedPipelineDataKHR vkReleaseDisplayEXT vkReleasePerformanceConfigurationINTEL
vkReleaseProfilingLockKHR vkReleaseSwapchainImagesEXT vkReleaseSwapchainImagesKHR
vkSetDebugUtilsObjectNameEXT vkSetDebugUtilsObjectTagEXT vkSetDeviceMemoryPriorityEXT vkSetHdrMetadataEXT
vkSetLatencyMarkerNV vkSetLatencySleepModeNV vkSetLocalDimmingAMD vkSetPrivateData vkSetPrivateDataEXT
vkSetSwapchainPresentTimingQueueSizeEXT vkSubmitDebugUtilsMessageEXT vkTransitionImageLayout
vkTransitionImageLayoutEXT vkUninitializePerformanceApiINTEL vkUnmapMemory2 vkUnmapMemory2KHR
vkUnregisterCustomBorderColorEXT vkUpdateIndirectExecutionSetPipelineEXT
vkUpdateIndirectExecutionSetShaderEXT vkUpdateVideoSessionParametersKHR vkWaitForPresent2KHR
vkWaitForPresentKHR vkWriteAccelerationStructuresPropertiesKHR vkWriteMicromapsPropertiesEXT
vkWriteResourceDescriptorsEXT vkWriteSamplerDescriptorsEXT""",
      minimumVersion = None
    ),
    group(
      Unsupported,
      """vkCmdDrawIndexedIndirectCount vkCmdDrawIndirectCount vkCreateSamplerYcbcrConversion
vkGetBufferOpaqueCaptureAddress vkGetDeviceMemoryOpaqueCaptureAddress""",
      minimumVersion = Some("VK_API_VERSION_1_1")
    ),
    group(Unsupported, """vkGetBufferDeviceAddress vkQueueBindSparse""")
  ).flatten
  def render(functions: Vector[GenerateOfficialAPIManifest.Function]): Vector[(String, String)] = {
    require(entries.map(_._1).distinct.size == entries.size, "duplicate Vulkan implementation policy")
    val declarations = entries.toMap
    val policies     = declarations.view.mapValues(_.policy).toMap
    val names        = functions.map(_.name.raw).toSet
    require(
      names == policies.keySet,
      s"Vulkan implementation inventory differs: added ${names -- policies.keySet}; removed ${policies.keySet -- names}"
    )
    val byName = functions.map(function => function.name.raw -> function).toMap
    policies.foreach {
      case (name, Alias(target)) =>
        require(policies.contains(target) && policies(target) != Unsupported, s"unsupported alias target $target")
        val alias = byName(name)
        val core  = byName(target)
        require(
          alias.returnType == core.returnType && alias.parameters == core.parameters,
          s"incompatible alias signature $name -> $target"
        )
      case _ => ()
    }
    val wrappers = functions.flatMap { function =>
      val policy = policies(function.name.raw)
      Option.when(policy != Manual && policy != Unsupported)(boundary(function, policy))
    }.mkString("\n\n")
    val unsupported = functions.filter(function => policies(function.name.raw) == Unsupported).map { function =>
      val result = function.returnType.raw
      val body   = if (result == "VkResult") "return VK_ERROR_FEATURE_NOT_PRESENT;"
      else if (result == "void") "std::abort();" else "return 0;"
      declaration(function) + s" { $body }"
    }.mkString("\n")
    val lookup = functions.flatMap { function =>
      val name = function.name.raw
      declarations(name).minimumVersion.map { version =>
        s"if (version >= $version && std::strcmp(name, \"$name\") == 0) return reinterpret_cast<PFN_vkVoidFunction>($name);"
      }
    }.mkString("\n")
    val implementations = functions.filter { function =>
      policies(function.name.raw) match {
        case Unsupported | Alias(_) => false
        case _                      => true
      }
    }.map { function =>
      val parameters = function.parameters.mkString(", ")
      s"${function.returnType.raw} ${implementation(function.name.raw)}($parameters);"
    }.mkString("\n")
    Vector(
      "gemstone_vulkan_adapter.hpp" ->
        s"#pragma once\n#include <vulkan/vulkan.h>\nnamespace gemstone::vulkan {\n$implementations\n}\n",
      "gemstone_vulkan_boundaries.inc"  -> wrappers,
      "gemstone_vulkan_unsupported.cpp" -> ("#include <cstdlib>\n#include <vulkan/vulkan.h>\n" + unsupported),
      "gemstone_vulkan_lookup.inc"      -> lookup
    )
  }

  private def implementation(name: String): String = s"impl${name.capitalize}"

  private def declaration(function: GenerateOfficialAPIManifest.Function): String =
    signature(function, function.parameters)

  private def definition(function: GenerateOfficialAPIManifest.Function): String =
    signature(function, function.parameters.zipWithIndex.map((parameter, index) => s"$parameter p$index"))

  private def signature(function: GenerateOfficialAPIManifest.Function, parameters: Vector[String]): String =
    s"extern \"C\" VKAPI_ATTR ${function.returnType.raw} VKAPI_CALL ${function.name}(${parameters.mkString(", ")})"

  private def boundary(function: GenerateOfficialAPIManifest.Function, policy: Policy): String = {
    val arguments = function.parameters.indices.map(index => s"p$index").mkString(", ")
    val invoke    = s"${implementation(function.name.raw)}($arguments)"
    val body      = policy match {
      case Alias(target)          => s"return $target($arguments);"
      case Create(destroy, scope) =>
        val allocator        = s"p${function.parameters.size - 2}"
        val output           = s"p${function.parameters.size - 1}"
        val parent           = if (scope == "INSTANCE") "nullptr" else "handleArena(p0)"
        val destroyArguments =
          if (scope == "INSTANCE" || scope == "DEVICE") s"*$output, $allocator" else s"p0, *$output, $allocator"
        val cleanup =
          s"if ($output && *$output) { ${implementation(destroy)}($destroyArguments); *$output = VK_NULL_HANDLE; }"
        s"""if ($output) *$output = VK_NULL_HANDLE;
try {
  host::Creation creation($allocator, $parent, VK_SYSTEM_ALLOCATION_SCOPE_$scope);
  try {
    const auto result = $invoke;
    if (result == VK_SUCCESS) creation.commit();
    else { $cleanup }
    return result;
  } catch (...) { $cleanup throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }"""
      case Destroy =>
        val handle    = s"p${function.parameters.size - 2}"
        val allocator = s"p${function.parameters.size - 1}"
        s"auto *arena = handleArena($handle);\nhost::Scope scope(arena);\n$invoke;\nif (arena) arena->destroy($allocator);"
      case _ =>
        val arena = policy match {
          case Global                 => "deviceArena(nullptr)"
          case CommandResult | Record => "handleArena(p0)"
          case _                      => "deviceArena(handleArena(p0))"
        }
        val failure = policy match {
          case Global | Device | CommandResult => "return VK_ERROR_OUT_OF_HOST_MEMORY;"
          case DeviceVoid                      =>
            "std::lock_guard lock(stateMutex);\nconst auto found = devices.find(p0);\nif (found != devices.end()) found->second->lost = true;\nstateChanged.notify_all();"
          case Record =>
            "std::lock_guard lock(stateMutex);\nconst auto found = commands.find(p0);\nif (found != commands.end()) found->second.allocationFailed = true;"
          case Query => ""
          case _     => throw IllegalArgumentException(s"unexpected boundary policy $policy")
        }
        val result = if (function.returnType.raw == "void") "" else "return "
        s"host::Scope scope($arena);\ntry { $result$invoke; } catch (const std::bad_alloc &) { $failure }"
    }
    definition(function) + s" {\n$body\n}"
  }
}

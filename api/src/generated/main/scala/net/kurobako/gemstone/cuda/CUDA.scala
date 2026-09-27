// Generated from pinned official headers by GenerateAPISurfaces. Regenerate with: sbt codegen
package net.kurobako.gemstone.cuda

import net.kurobako.gemstone.Constraint.Positive
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.abi.ABI.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object CUDA {
  type CUDA_ARRAY3D_DESCRIPTOR = CValue["CUDA_ARRAY3D_DESCRIPTOR"]
  type CUDA_ARRAY_DESCRIPTOR = CValue["CUDA_ARRAY_DESCRIPTOR"]
  type CUDA_ARRAY_MEMORY_REQUIREMENTS = CValue["CUDA_ARRAY_MEMORY_REQUIREMENTS"]
  type CUDA_ARRAY_SPARSE_PROPERTIES = CValue["CUDA_ARRAY_SPARSE_PROPERTIES"]
  type CUDA_BATCH_MEM_OP_NODE_PARAMS = CValue["CUDA_BATCH_MEM_OP_NODE_PARAMS"]
  type CUDA_EXTERNAL_MEMORY_BUFFER_DESC = CValue["CUDA_EXTERNAL_MEMORY_BUFFER_DESC"]
  type CUDA_EXTERNAL_MEMORY_HANDLE_DESC = CValue["CUDA_EXTERNAL_MEMORY_HANDLE_DESC"]
  type CUDA_EXTERNAL_MEMORY_MIPMAPPED_ARRAY_DESC = CValue["CUDA_EXTERNAL_MEMORY_MIPMAPPED_ARRAY_DESC"]
  type CUDA_EXTERNAL_SEMAPHORE_HANDLE_DESC = CValue["CUDA_EXTERNAL_SEMAPHORE_HANDLE_DESC"]
  type CUDA_EXTERNAL_SEMAPHORE_SIGNAL_PARAMS = CValue["CUDA_EXTERNAL_SEMAPHORE_SIGNAL_PARAMS"]
  type CUDA_EXTERNAL_SEMAPHORE_WAIT_PARAMS = CValue["CUDA_EXTERNAL_SEMAPHORE_WAIT_PARAMS"]
  type CUDA_EXT_SEM_SIGNAL_NODE_PARAMS = CValue["CUDA_EXT_SEM_SIGNAL_NODE_PARAMS"]
  type CUDA_EXT_SEM_WAIT_NODE_PARAMS = CValue["CUDA_EXT_SEM_WAIT_NODE_PARAMS"]
  type CUDA_GRAPH_INSTANTIATE_PARAMS = CValue["CUDA_GRAPH_INSTANTIATE_PARAMS"]
  type CUDA_HOST_NODE_PARAMS = CValue["CUDA_HOST_NODE_PARAMS"]
  type CUDA_KERNEL_NODE_PARAMS = CValue["CUDA_KERNEL_NODE_PARAMS"]
  type CUDA_LAUNCH_PARAMS = CValue["CUDA_LAUNCH_PARAMS"]
  type CUDA_MEMCPY2D = CValue["CUDA_MEMCPY2D"]
  type CUDA_MEMCPY3D = CValue["CUDA_MEMCPY3D"]
  type CUDA_MEMCPY3D_BATCH_OP = CValue["CUDA_MEMCPY3D_BATCH_OP"]
  type CUDA_MEMCPY3D_PEER = CValue["CUDA_MEMCPY3D_PEER"]
  type CUDA_MEMSET_NODE_PARAMS = CValue["CUDA_MEMSET_NODE_PARAMS"]
  type CUDA_MEM_ALLOC_NODE_PARAMS = CValue["CUDA_MEM_ALLOC_NODE_PARAMS"]
  type CUDA_RESOURCE_DESC = CValue["CUDA_RESOURCE_DESC"]
  type CUDA_RESOURCE_VIEW_DESC = CValue["CUDA_RESOURCE_VIEW_DESC"]
  type CUDA_TEXTURE_DESC = CValue["CUDA_TEXTURE_DESC"]
  type CU_DEV_SM_RESOURCE_GROUP_PARAMS = CValue["CU_DEV_SM_RESOURCE_GROUP_PARAMS"]
  type CUaddress_mode = CValue["CUaddress_mode"]
  type CUarray = CValue["CUarray"]
  type CUarrayMapInfo = CValue["CUarrayMapInfo"]
  type CUarray_format = CValue["CUarray_format"]
  type CUasyncCallback = CallbackTypedef[
    "CUasyncCallback",
    (Pointer[CUasyncNotificationInfo, ReadWrite, Opaque], OpaquePointer, CUasyncCallbackHandle),
    Unit
  ]
  type CUasyncCallbackHandle = CValue["CUasyncCallbackHandle"]
  type CUasyncNotificationInfo = CValue["CUasyncNotificationInfo"]
  type CUatomicOperation = CValue["CUatomicOperation"]
  type CUcheckpointCheckpointArgs = CValue["CUcheckpointCheckpointArgs"]
  type CUcheckpointLockArgs = CValue["CUcheckpointLockArgs"]
  type CUcheckpointRestoreArgs = CValue["CUcheckpointRestoreArgs"]
  type CUcheckpointUnlockArgs = CValue["CUcheckpointUnlockArgs"]
  type CUcontext = CValue["CUcontext"]
  type CUcoredumpCallbackHandle = CValue["CUcoredumpCallbackHandle"]
  type CUcoredumpSettings = CValue["CUcoredumpSettings"]
  type CUcoredumpStatusCallback = CallbackTypedef["CUcoredumpStatusCallback", (OpaquePointer, Int, CUdevice), Unit]
  type CUctxCreateParams = CValue["CUctxCreateParams"]
  type CUdevResource = CValue["CUdevResource"]
  type CUdevResourceDesc = CValue["CUdevResourceDesc"]
  type CUdevResourceType = CValue["CUdevResourceType"]
  type CUdevice = CValue["CUdevice"]
  type CUdevice_P2PAttribute = CValue["CUdevice_P2PAttribute"]
  type CUdevice_attribute = CValue["CUdevice_attribute"]
  type CUdeviceptr = CValue["CUdeviceptr"]
  type CUdevprop = CValue["CUdevprop"]
  type CUdriverProcAddressQueryResult = CValue["CUdriverProcAddressQueryResult"]
  type CUevent = CValue["CUevent"]
  type CUexecAffinityParam = CValue["CUexecAffinityParam"]
  type CUexecAffinityType = CValue["CUexecAffinityType"]
  type CUexternalMemory = CValue["CUexternalMemory"]
  type CUexternalSemaphore = CValue["CUexternalSemaphore"]
  type CUfilter_mode = CValue["CUfilter_mode"]
  type CUflushGPUDirectRDMAWritesScope = CValue["CUflushGPUDirectRDMAWritesScope"]
  type CUflushGPUDirectRDMAWritesTarget = CValue["CUflushGPUDirectRDMAWritesTarget"]
  type CUfunc_cache = CValue["CUfunc_cache"]
  type CUfunction = CValue["CUfunction"]
  type CUfunctionLoadingState = CValue["CUfunctionLoadingState"]
  type CUfunction_attribute = CValue["CUfunction_attribute"]
  type CUgraph = CValue["CUgraph"]
  type CUgraphConditionalHandle = CValue["CUgraphConditionalHandle"]
  type CUgraphEdgeData = CValue["CUgraphEdgeData"]
  type CUgraphExec = CValue["CUgraphExec"]
  type CUgraphExecUpdateResultInfo = CValue["CUgraphExecUpdateResultInfo"]
  type CUgraphMem_attribute = CValue["CUgraphMem_attribute"]
  type CUgraphNode = CValue["CUgraphNode"]
  type CUgraphNodeParams = CValue["CUgraphNodeParams"]
  type CUgraphNodeType = CValue["CUgraphNodeType"]
  type CUgraphRecaptureCallback = CallbackTypedef[
    "CUgraphRecaptureCallback",
    (
        OpaquePointer,
        CUgraphNode,
        Pointer[CUgraphNodeParams, Read, Opaque],
        Pointer[CUgraphNodeParams, Read, Opaque],
        CUgraphRecaptureStatus
    ),
    CUresult
  ]
  type CUgraphRecaptureStatus = CValue["CUgraphRecaptureStatus"]
  type CUgraphicsResource = CValue["CUgraphicsResource"]
  type CUgreenCtx = CValue["CUgreenCtx"]
  type CUhostFn = CallbackTypedef["CUhostFn", Tuple1[OpaquePointer], Unit]
  type CUipcEventHandle = CValue["CUipcEventHandle"]
  type CUipcMemHandle = CValue["CUipcMemHandle"]
  type CUjitInputType = CValue["CUjitInputType"]
  type CUjit_option = CValue["CUjit_option"]
  type CUkernel = CValue["CUkernel"]
  type CUkernelNodeAttrID = CValue["CUkernelNodeAttrID"]
  type CUkernelNodeAttrValue = CValue["CUkernelNodeAttrValue"]
  type CUlaunchConfig = CValue["CUlaunchConfig"]
  type CUlibrary = CValue["CUlibrary"]
  type CUlibraryOption = CValue["CUlibraryOption"]
  type CUlimit = CValue["CUlimit"]
  type CUlinkState = CValue["CUlinkState"]
  type CUlogIterator = CValue["CUlogIterator"]
  type CUlogLevel = CValue["CUlogLevel"]
  type CUlogicalEndpointId = CValue["CUlogicalEndpointId"]
  type CUlogicalEndpointIpcHandleType = CValue["CUlogicalEndpointIpcHandleType"]
  type CUlogicalEndpointProp = CValue["CUlogicalEndpointProp"]
  type CUlogsCallback =
    CallbackTypedef["CUlogsCallback", (OpaquePointer, CUlogLevel, Pointer[Byte, ReadWrite, Opaque], size_t), Unit]
  type CUlogsCallbackHandle = CValue["CUlogsCallbackHandle"]
  type CUmemAccessDesc = CValue["CUmemAccessDesc"]
  type CUmemAccess_flags = CValue["CUmemAccess_flags"]
  type CUmemAllocationGranularity_flags = CValue["CUmemAllocationGranularity_flags"]
  type CUmemAllocationHandleType = CValue["CUmemAllocationHandleType"]
  type CUmemAllocationProp = CValue["CUmemAllocationProp"]
  type CUmemAllocationType = CValue["CUmemAllocationType"]
  type CUmemDecompressParams = CValue["CUmemDecompressParams"]
  type CUmemGenericAllocationHandle = CValue["CUmemGenericAllocationHandle"]
  type CUmemLocation = CValue["CUmemLocation"]
  type CUmemPoolProps = CValue["CUmemPoolProps"]
  type CUmemPoolPtrExportData = CValue["CUmemPoolPtrExportData"]
  type CUmemPool_attribute = CValue["CUmemPool_attribute"]
  type CUmemRangeHandleType = CValue["CUmemRangeHandleType"]
  type CUmem_advise = CValue["CUmem_advise"]
  type CUmem_range_attribute = CValue["CUmem_range_attribute"]
  type CUmemcpyAttributes = CValue["CUmemcpyAttributes"]
  type CUmemoryPool = CValue["CUmemoryPool"]
  type CUmipmappedArray = CValue["CUmipmappedArray"]
  type CUmodule = CValue["CUmodule"]
  type CUmoduleLoadingMode = CValue["CUmoduleLoadingMode"]
  type CUmulticastGranularity_flags = CValue["CUmulticastGranularity_flags"]
  type CUmulticastObjectProp = CValue["CUmulticastObjectProp"]
  type CUoccupancyB2DSize = CallbackTypedef["CUoccupancyB2DSize", Tuple1[Int], size_t]
  type CUpointer_attribute = CValue["CUpointer_attribute"]
  type CUprocessState = CValue["CUprocessState"]
  type CUresult = CValue["CUresult"]
  type CUsharedconfig = CValue["CUsharedconfig"]
  type CUstream = CValue["CUstream"]
  type CUstreamAttrID = CValue["CUstreamAttrID"]
  type CUstreamAttrValue = CValue["CUstreamAttrValue"]
  type CUstreamBatchMemOpParams = CValue["CUstreamBatchMemOpParams"]
  type CUstreamCallback = CallbackTypedef["CUstreamCallback", (CUstream, CUresult, OpaquePointer), Unit]
  type CUstreamCaptureMode = CValue["CUstreamCaptureMode"]
  type CUstreamCaptureStatus = CValue["CUstreamCaptureStatus"]
  type CUstreamCigCaptureParams = CValue["CUstreamCigCaptureParams"]
  type CUsurfObject = CValue["CUsurfObject"]
  type CUsurfref = CValue["CUsurfref"]
  type CUtensorMap = CValue["CUtensorMap"]
  type CUtensorMapDataType = CValue["CUtensorMapDataType"]
  type CUtensorMapFloatOOBfill = CValue["CUtensorMapFloatOOBfill"]
  type CUtensorMapIm2ColWideMode = CValue["CUtensorMapIm2ColWideMode"]
  type CUtensorMapInterleave = CValue["CUtensorMapInterleave"]
  type CUtensorMapL2promotion = CValue["CUtensorMapL2promotion"]
  type CUtensorMapSwizzle = CValue["CUtensorMapSwizzle"]
  type CUtexObject = CValue["CUtexObject"]
  type CUtexref = CValue["CUtexref"]
  type CUuserObject = CValue["CUuserObject"]
  type CUuuid = CValue["CUuuid"]
  type cudaArray_const_t = CValue["cudaArray_const_t"]
  type cudaArray_t = CValue["cudaArray_t"]
  type cudaAsyncCallback = CallbackTypedef[
    "cudaAsyncCallback",
    (Pointer[cudaAsyncNotificationInfo_t, ReadWrite, Opaque], OpaquePointer, cudaAsyncCallbackHandle_t),
    Unit
  ]
  type cudaAsyncCallbackHandle_t = CValue["cudaAsyncCallbackHandle_t"]
  type cudaAsyncNotificationInfo_t = CValue["cudaAsyncNotificationInfo_t"]
  type cudaDevResource = CValue["cudaDevResource"]
  type cudaDevResourceDesc_t = CValue["cudaDevResourceDesc_t"]
  type cudaDevSmResourceGroupParams = CValue["cudaDevSmResourceGroupParams"]
  type cudaError_t = CValue["cudaError_t"]
  type cudaEvent_t = CValue["cudaEvent_t"]
  type cudaExecutionContext_t = CValue["cudaExecutionContext_t"]
  type cudaExternalMemory_t = CValue["cudaExternalMemory_t"]
  type cudaExternalSemaphore_t = CValue["cudaExternalSemaphore_t"]
  type cudaFunction_t = CValue["cudaFunction_t"]
  type cudaGraphConditionalHandle = CValue["cudaGraphConditionalHandle"]
  type cudaGraphEdgeData = CValue["cudaGraphEdgeData"]
  type cudaGraphExecUpdateResultInfo = CValue["cudaGraphExecUpdateResultInfo"]
  type cudaGraphExec_t = CValue["cudaGraphExec_t"]
  type cudaGraphInstantiateParams = CValue["cudaGraphInstantiateParams"]
  type cudaGraphNode_t = CValue["cudaGraphNode_t"]
  type cudaGraph_t = CValue["cudaGraph_t"]
  type cudaGraphicsResource_t = CValue["cudaGraphicsResource_t"]
  type cudaHostFn_t = CallbackTypedef["cudaHostFn_t", Tuple1[OpaquePointer], Unit]
  type cudaIpcEventHandle_t = CValue["cudaIpcEventHandle_t"]
  type cudaIpcMemHandle_t = CValue["cudaIpcMemHandle_t"]
  type cudaKernel_t = CValue["cudaKernel_t"]
  type cudaLaunchAttributeID = CValue["cudaLaunchAttributeID"]
  type cudaLaunchAttributeValue = CValue["cudaLaunchAttributeValue"]
  type cudaLaunchConfig_t = CValue["cudaLaunchConfig_t"]
  type cudaLibrary_t = CValue["cudaLibrary_t"]
  type cudaLogIterator = CValue["cudaLogIterator"]
  type cudaLogLevel = CValue["cudaLogLevel"]
  type cudaLogsCallbackHandle = CValue["cudaLogsCallbackHandle"]
  type cudaLogsCallback_t =
    CallbackTypedef["cudaLogsCallback_t", (OpaquePointer, cudaLogLevel, Pointer[Byte, ReadWrite, Opaque], size_t), Unit]
  type cudaMemPool_t = CValue["cudaMemPool_t"]
  type cudaMipmappedArray_const_t = CValue["cudaMipmappedArray_const_t"]
  type cudaMipmappedArray_t = CValue["cudaMipmappedArray_t"]
  type cudaStreamCallback_t = CallbackTypedef["cudaStreamCallback_t", (cudaStream_t, cudaError_t, OpaquePointer), Unit]
  type cudaStream_t = CValue["cudaStream_t"]
  type cudaSurfaceObject_t = CValue["cudaSurfaceObject_t"]
  type cudaTextureObject_t = CValue["cudaTextureObject_t"]
  type cudaUUID_t = CValue["cudaUUID_t"]
  type cudaUserObject_t = CValue["cudaUserObject_t"]
  type cuuint32_t = CValue["cuuint32_t"]
  type cuuint64_t = CValue["cuuint64_t"]
  type dim3 = CValue["dim3"]
  type enum_cudaAtomicOperation = CValue["enum cudaAtomicOperation"]
  type enum_cudaChannelFormatKind = CValue["enum cudaChannelFormatKind"]
  type enum_cudaDevResourceType = CValue["enum cudaDevResourceType"]
  type enum_cudaDeviceAttr = CValue["enum cudaDeviceAttr"]
  type enum_cudaDeviceP2PAttr = CValue["enum cudaDeviceP2PAttr"]
  type enum_cudaDriverEntryPointQueryResult = CValue["enum cudaDriverEntryPointQueryResult"]
  type enum_cudaFlushGPUDirectRDMAWritesScope = CValue["enum cudaFlushGPUDirectRDMAWritesScope"]
  type enum_cudaFlushGPUDirectRDMAWritesTarget = CValue["enum cudaFlushGPUDirectRDMAWritesTarget"]
  type enum_cudaFuncAttribute = CValue["enum cudaFuncAttribute"]
  type enum_cudaFuncCache = CValue["enum cudaFuncCache"]
  type enum_cudaGraphMemAttributeType = CValue["enum cudaGraphMemAttributeType"]
  type enum_cudaGraphNodeType = CValue["enum cudaGraphNodeType"]
  type enum_cudaJitOption = CValue["enum cudaJitOption"]
  type enum_cudaLibraryOption = CValue["enum cudaLibraryOption"]
  type enum_cudaLimit = CValue["enum cudaLimit"]
  type enum_cudaMemAccessFlags = CValue["enum cudaMemAccessFlags"]
  type enum_cudaMemAllocationHandleType = CValue["enum cudaMemAllocationHandleType"]
  type enum_cudaMemAllocationType = CValue["enum cudaMemAllocationType"]
  type enum_cudaMemPoolAttr = CValue["enum cudaMemPoolAttr"]
  type enum_cudaMemRangeAttribute = CValue["enum cudaMemRangeAttribute"]
  type enum_cudaMemcpyKind = CValue["enum cudaMemcpyKind"]
  type enum_cudaMemoryAdvise = CValue["enum cudaMemoryAdvise"]
  type enum_cudaSharedMemConfig = CValue["enum cudaSharedMemConfig"]
  type enum_cudaStreamCaptureMode = CValue["enum cudaStreamCaptureMode"]
  type enum_cudaStreamCaptureStatus = CValue["enum cudaStreamCaptureStatus"]
  type size_t = CValue["size_t"]
  type struct_cudaArrayMemoryRequirements = CValue["struct cudaArrayMemoryRequirements"]
  type struct_cudaArraySparseProperties = CValue["struct cudaArraySparseProperties"]
  type struct_cudaChannelFormatDesc = CValue["struct cudaChannelFormatDesc"]
  type struct_cudaDeviceProp = CValue["struct cudaDeviceProp"]
  type struct_cudaExtent = CValue["struct cudaExtent"]
  type struct_cudaExternalMemoryBufferDesc = CValue["struct cudaExternalMemoryBufferDesc"]
  type struct_cudaExternalMemoryHandleDesc = CValue["struct cudaExternalMemoryHandleDesc"]
  type struct_cudaExternalMemoryMipmappedArrayDesc = CValue["struct cudaExternalMemoryMipmappedArrayDesc"]
  type struct_cudaExternalSemaphoreHandleDesc = CValue["struct cudaExternalSemaphoreHandleDesc"]
  type struct_cudaExternalSemaphoreSignalNodeParams = CValue["struct cudaExternalSemaphoreSignalNodeParams"]
  type struct_cudaExternalSemaphoreSignalParams = CValue["struct cudaExternalSemaphoreSignalParams"]
  type struct_cudaExternalSemaphoreWaitNodeParams = CValue["struct cudaExternalSemaphoreWaitNodeParams"]
  type struct_cudaExternalSemaphoreWaitParams = CValue["struct cudaExternalSemaphoreWaitParams"]
  type struct_cudaFuncAttributes = CValue["struct cudaFuncAttributes"]
  type struct_cudaGraphNodeParams = CValue["struct cudaGraphNodeParams"]
  type struct_cudaGraphRecaptureCallbackData = CValue["struct cudaGraphRecaptureCallbackData"]
  type struct_cudaHostNodeParams = CValue["struct cudaHostNodeParams"]
  type struct_cudaKernelNodeParams = CValue["struct cudaKernelNodeParams"]
  type struct_cudaMemAccessDesc = CValue["struct cudaMemAccessDesc"]
  type struct_cudaMemAllocNodeParams = CValue["struct cudaMemAllocNodeParams"]
  type struct_cudaMemLocation = CValue["struct cudaMemLocation"]
  type struct_cudaMemPoolProps = CValue["struct cudaMemPoolProps"]
  type struct_cudaMemPoolPtrExportData = CValue["struct cudaMemPoolPtrExportData"]
  type struct_cudaMemcpy3DBatchOp = CValue["struct cudaMemcpy3DBatchOp"]
  type struct_cudaMemcpy3DParms = CValue["struct cudaMemcpy3DParms"]
  type struct_cudaMemcpy3DPeerParms = CValue["struct cudaMemcpy3DPeerParms"]
  type struct_cudaMemcpyAttributes = CValue["struct cudaMemcpyAttributes"]
  type struct_cudaMemsetParams = CValue["struct cudaMemsetParams"]
  type struct_cudaPitchedPtr = CValue["struct cudaPitchedPtr"]
  type struct_cudaPointerAttributes = CValue["struct cudaPointerAttributes"]
  type struct_cudaResourceDesc = CValue["struct cudaResourceDesc"]
  type struct_cudaResourceViewDesc = CValue["struct cudaResourceViewDesc"]
  type struct_cudaTextureDesc = CValue["struct cudaTextureDesc"]
  type unsigned_char = CValue["unsigned char"]
  type unsigned_int = CValue["unsigned int"]
  type unsigned_long_long = CValue["unsigned long long"]
  type unsigned_short = CValue["unsigned short"]

  val HeaderVersion: NonEmptyString = NonEmptyString("13.3")
  val FunctionCount: Positive[Int] = 820
}

import CUDA.*

trait CUDA {
  def cuArray3DCreate_v2(
      pHandle: Pointer[CUarray, ReadWrite, Opaque],
      pAllocateArray: Pointer[CUDA_ARRAY3D_DESCRIPTOR, Read, Opaque]
  ): CUresult = unsupported
  def cuArray3DGetDescriptor_v2(
      pArrayDescriptor: Pointer[CUDA_ARRAY3D_DESCRIPTOR, ReadWrite, Opaque],
      hArray: CUarray
  ): CUresult = unsupported
  def cuArrayCreate_v2(
      pHandle: Pointer[CUarray, ReadWrite, Opaque],
      pAllocateArray: Pointer[CUDA_ARRAY_DESCRIPTOR, Read, Opaque]
  ): CUresult = unsupported
  def cuArrayDestroy(hArray: CUarray): CUresult = unsupported
  def cuArrayGetDescriptor_v2(
      pArrayDescriptor: Pointer[CUDA_ARRAY_DESCRIPTOR, ReadWrite, Opaque],
      hArray: CUarray
  ): CUresult = unsupported
  def cuArrayGetMemoryRequirements(
      memoryRequirements: Pointer[CUDA_ARRAY_MEMORY_REQUIREMENTS, ReadWrite, Opaque],
      array: CUarray,
      device: CUdevice
  ): CUresult = unsupported
  def cuArrayGetPlane(
      pPlaneArray: Pointer[CUarray, ReadWrite, Opaque],
      hArray: CUarray,
      planeIdx: unsigned_int
  ): CUresult = unsupported
  def cuArrayGetSparseProperties(
      sparseProperties: Pointer[CUDA_ARRAY_SPARSE_PROPERTIES, ReadWrite, Opaque],
      array: CUarray
  ): CUresult = unsupported
  def cuCheckpointProcessCheckpoint(pid: Int, args: Pointer[CUcheckpointCheckpointArgs, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuCheckpointProcessGetRestoreThreadId(pid: Int, tid: Pointer[Int, ReadWrite, Opaque]): CUresult = unsupported
  def cuCheckpointProcessGetState(pid: Int, state: Pointer[CUprocessState, ReadWrite, Opaque]): CUresult = unsupported
  def cuCheckpointProcessLock(pid: Int, args: Pointer[CUcheckpointLockArgs, ReadWrite, Opaque]): CUresult = unsupported
  def cuCheckpointProcessRestore(pid: Int, args: Pointer[CUcheckpointRestoreArgs, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuCheckpointProcessUnlock(pid: Int, args: Pointer[CUcheckpointUnlockArgs, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuCoredumpDeregisterCompleteCallback(callback: CUcoredumpCallbackHandle): CUresult = unsupported
  def cuCoredumpDeregisterStartCallback(callback: CUcoredumpCallbackHandle): CUresult = unsupported
  def cuCoredumpGetAttribute(
      attrib: CUcoredumpSettings,
      value: OpaquePointer,
      size: Pointer[size_t, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuCoredumpGetAttributeGlobal(
      attrib: CUcoredumpSettings,
      value: OpaquePointer,
      size: Pointer[size_t, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuCoredumpRegisterCompleteCallback(
      callback: CUcoredumpStatusCallback,
      userData: OpaquePointer,
      callbackOut: Pointer[CUcoredumpCallbackHandle, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuCoredumpRegisterStartCallback(
      callback: CUcoredumpStatusCallback,
      userData: OpaquePointer,
      callbackOut: Pointer[CUcoredumpCallbackHandle, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuCoredumpSetAttribute(
      attrib: CUcoredumpSettings,
      value: OpaquePointer,
      size: Pointer[size_t, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuCoredumpSetAttributeGlobal(
      attrib: CUcoredumpSettings,
      value: OpaquePointer,
      size: Pointer[size_t, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuCtxAttach(pctx: Pointer[CUcontext, ReadWrite, Opaque], flags: unsigned_int): CUresult = unsupported
  def cuCtxCreate_v4(
      pctx: Pointer[CUcontext, ReadWrite, Opaque],
      ctxCreateParams: Pointer[CUctxCreateParams, ReadWrite, Opaque],
      flags: unsigned_int,
      dev: CUdevice
  ): CUresult = unsupported
  def cuCtxDestroy_v2(ctx: CUcontext): CUresult = unsupported
  def cuCtxDetach(ctx: CUcontext): CUresult = unsupported
  def cuCtxDisablePeerAccess(peerContext: CUcontext): CUresult = unsupported
  def cuCtxEnablePeerAccess(peerContext: CUcontext, Flags: unsigned_int): CUresult = unsupported
  def cuCtxFromGreenCtx(pContext: Pointer[CUcontext, ReadWrite, Opaque], hCtx: CUgreenCtx): CUresult = unsupported
  def cuCtxGetApiVersion(ctx: CUcontext, version: Pointer[unsigned_int, ReadWrite, Opaque]): CUresult = unsupported
  def cuCtxGetCacheConfig(pconfig: Pointer[CUfunc_cache, ReadWrite, Opaque]): CUresult = unsupported
  def cuCtxGetCurrent(pctx: Pointer[CUcontext, ReadWrite, Opaque]): CUresult = unsupported
  def cuCtxGetDevResource(
      hCtx: CUcontext,
      resource: Pointer[CUdevResource, ReadWrite, Opaque],
      `type`: CUdevResourceType
  ): CUresult = unsupported
  def cuCtxGetDevice(device: Pointer[CUdevice, ReadWrite, Opaque]): CUresult = unsupported
  def cuCtxGetDevice_v2(device: Pointer[CUdevice, ReadWrite, Opaque], ctx: CUcontext): CUresult = unsupported
  def cuCtxGetExecAffinity(
      pExecAffinity: Pointer[CUexecAffinityParam, ReadWrite, Opaque],
      `type`: CUexecAffinityType
  ): CUresult = unsupported
  def cuCtxGetFlags(flags: Pointer[unsigned_int, ReadWrite, Opaque]): CUresult = unsupported
  def cuCtxGetId(ctx: CUcontext, ctxId: Pointer[unsigned_long_long, ReadWrite, Opaque]): CUresult = unsupported
  def cuCtxGetLimit(pvalue: Pointer[size_t, ReadWrite, Opaque], limit: CUlimit): CUresult = unsupported
  def cuCtxGetSharedMemConfig(pConfig: Pointer[CUsharedconfig, ReadWrite, Opaque]): CUresult = unsupported
  def cuCtxGetStreamPriorityRange(
      leastPriority: Pointer[Int, ReadWrite, Opaque],
      greatestPriority: Pointer[Int, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuCtxPopCurrent_v2(pctx: Pointer[CUcontext, ReadWrite, Opaque]): CUresult = unsupported
  def cuCtxPushCurrent_v2(ctx: CUcontext): CUresult = unsupported
  def cuCtxRecordEvent(hCtx: CUcontext, hEvent: CUevent): CUresult = unsupported
  def cuCtxResetPersistingL2Cache(): CUresult = unsupported
  def cuCtxSetCacheConfig(config: CUfunc_cache): CUresult = unsupported
  def cuCtxSetCurrent(ctx: CUcontext): CUresult = unsupported
  def cuCtxSetFlags(flags: unsigned_int): CUresult = unsupported
  def cuCtxSetLimit(limit: CUlimit, value: size_t): CUresult = unsupported
  def cuCtxSetSharedMemConfig(config: CUsharedconfig): CUresult = unsupported
  def cuCtxSynchronize(): CUresult = unsupported
  def cuCtxSynchronize_v2(ctx: CUcontext): CUresult = unsupported
  def cuCtxWaitEvent(hCtx: CUcontext, hEvent: CUevent): CUresult = unsupported
  def cuDestroyExternalMemory(extMem: CUexternalMemory): CUresult = unsupported
  def cuDestroyExternalSemaphore(extSem: CUexternalSemaphore): CUresult = unsupported
  def cuDevResourceGenerateDesc(
      phDesc: Pointer[CUdevResourceDesc, ReadWrite, Opaque],
      resources: Pointer[CUdevResource, ReadWrite, Opaque],
      nbResources: unsigned_int
  ): CUresult = unsupported
  def cuDevSmResourceSplit(
      result: Pointer[CUdevResource, ReadWrite, Opaque],
      nbGroups: unsigned_int,
      input: Pointer[CUdevResource, Read, Opaque],
      remainder: Pointer[CUdevResource, ReadWrite, Opaque],
      flags: unsigned_int,
      groupParams: Pointer[CU_DEV_SM_RESOURCE_GROUP_PARAMS, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuDevSmResourceSplitByCount(
      result: Pointer[CUdevResource, ReadWrite, Opaque],
      nbGroups: Pointer[unsigned_int, ReadWrite, Opaque],
      input: Pointer[CUdevResource, Read, Opaque],
      remainder: Pointer[CUdevResource, ReadWrite, Opaque],
      flags: unsigned_int,
      minCount: unsigned_int
  ): CUresult = unsupported
  def cuDeviceCanAccessPeer(
      canAccessPeer: Pointer[Int, ReadWrite, Opaque],
      dev: CUdevice,
      peerDev: CUdevice
  ): CUresult = unsupported
  def cuDeviceComputeCapability(
      major: Pointer[Int, ReadWrite, Opaque],
      minor: Pointer[Int, ReadWrite, Opaque],
      dev: CUdevice
  ): CUresult = unsupported
  def cuDeviceGet(device: Pointer[CUdevice, ReadWrite, Opaque], ordinal: Int): CUresult = unsupported
  def cuDeviceGetAttribute(pi: Pointer[Int, ReadWrite, Opaque], attrib: CUdevice_attribute, dev: CUdevice): CUresult =
    unsupported
  def cuDeviceGetByPCIBusId(dev: Pointer[CUdevice, ReadWrite, Opaque], pciBusId: CString): CUresult = unsupported
  def cuDeviceGetCount(count: Pointer[Int, ReadWrite, Opaque]): CUresult = unsupported
  def cuDeviceGetDefaultMemPool(pool_out: Pointer[CUmemoryPool, ReadWrite, Opaque], dev: CUdevice): CUresult =
    unsupported
  def cuDeviceGetDevResource(
      device: CUdevice,
      resource: Pointer[CUdevResource, ReadWrite, Opaque],
      `type`: CUdevResourceType
  ): CUresult = unsupported
  def cuDeviceGetExecAffinitySupport(
      pi: Pointer[Int, ReadWrite, Opaque],
      `type`: CUexecAffinityType,
      dev: CUdevice
  ): CUresult = unsupported
  def cuDeviceGetGraphMemAttribute(device: CUdevice, attr: CUgraphMem_attribute, value: OpaquePointer): CUresult =
    unsupported
  def cuDeviceGetHostAtomicCapabilities(
      capabilities: Pointer[unsigned_int, ReadWrite, Opaque],
      operations: Pointer[CUatomicOperation, Read, Opaque],
      count: unsigned_int,
      dev: CUdevice
  ): CUresult = unsupported
  def cuDeviceGetLuid(
      luid: Pointer[Byte, ReadWrite, Opaque],
      deviceNodeMask: Pointer[unsigned_int, ReadWrite, Opaque],
      dev: CUdevice
  ): CUresult = unsupported
  def cuDeviceGetMemPool(pool: Pointer[CUmemoryPool, ReadWrite, Opaque], dev: CUdevice): CUresult = unsupported
  def cuDeviceGetName(name: Pointer[Byte, ReadWrite, Opaque], len: Int, dev: CUdevice): CUresult = unsupported
  def cuDeviceGetNvSciSyncAttributes(nvSciSyncAttrList: OpaquePointer, dev: CUdevice, flags: Int): CUresult =
    unsupported
  def cuDeviceGetP2PAtomicCapabilities(
      capabilities: Pointer[unsigned_int, ReadWrite, Opaque],
      operations: Pointer[CUatomicOperation, Read, Opaque],
      count: unsigned_int,
      srcDevice: CUdevice,
      dstDevice: CUdevice
  ): CUresult = unsupported
  def cuDeviceGetP2PAttribute(
      value: Pointer[Int, ReadWrite, Opaque],
      attrib: CUdevice_P2PAttribute,
      srcDevice: CUdevice,
      dstDevice: CUdevice
  ): CUresult = unsupported
  def cuDeviceGetPCIBusId(pciBusId: Pointer[Byte, ReadWrite, Opaque], len: Int, dev: CUdevice): CUresult = unsupported
  def cuDeviceGetProperties(prop: Pointer[CUdevprop, ReadWrite, Opaque], dev: CUdevice): CUresult = unsupported
  def cuDeviceGetTexture1DLinearMaxWidth(
      maxWidthInElements: Pointer[size_t, ReadWrite, Opaque],
      format: CUarray_format,
      numChannels: unsigned_int,
      dev: CUdevice
  ): CUresult = unsupported
  def cuDeviceGetUuid_v2(uuid: Pointer[CUuuid, ReadWrite, Opaque], dev: CUdevice): CUresult = unsupported
  def cuDeviceGraphMemTrim(device: CUdevice): CUresult = unsupported
  def cuDevicePrimaryCtxGetState(
      dev: CUdevice,
      flags: Pointer[unsigned_int, ReadWrite, Opaque],
      active: Pointer[Int, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuDevicePrimaryCtxRelease_v2(dev: CUdevice): CUresult = unsupported
  def cuDevicePrimaryCtxReset_v2(dev: CUdevice): CUresult = unsupported
  def cuDevicePrimaryCtxRetain(pctx: Pointer[CUcontext, ReadWrite, Opaque], dev: CUdevice): CUresult = unsupported
  def cuDevicePrimaryCtxSetFlags_v2(dev: CUdevice, flags: unsigned_int): CUresult = unsupported
  def cuDeviceRegisterAsyncNotification(
      device: CUdevice,
      callbackFunc: CUasyncCallback,
      userData: OpaquePointer,
      callback: Pointer[CUasyncCallbackHandle, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuDeviceSetGraphMemAttribute(device: CUdevice, attr: CUgraphMem_attribute, value: OpaquePointer): CUresult =
    unsupported
  def cuDeviceSetMemPool(dev: CUdevice, pool: CUmemoryPool): CUresult = unsupported
  def cuDeviceTotalMem_v2(bytes: Pointer[size_t, ReadWrite, Opaque], dev: CUdevice): CUresult = unsupported
  def cuDeviceUnregisterAsyncNotification(device: CUdevice, callback: CUasyncCallbackHandle): CUresult = unsupported
  def cuDriverGetVersion(driverVersion: Pointer[Int, ReadWrite, Opaque]): CUresult = unsupported
  def cuEventCreate(phEvent: Pointer[CUevent, ReadWrite, Opaque], Flags: unsigned_int): CUresult = unsupported
  def cuEventDestroy_v2(hEvent: CUevent): CUresult = unsupported
  def cuEventElapsedTime_v2(
      pMilliseconds: Pointer[Float, ReadWrite, Opaque],
      hStart: CUevent,
      hEnd: CUevent
  ): CUresult = unsupported
  def cuEventQuery(hEvent: CUevent): CUresult = unsupported
  def cuEventRecord(hEvent: CUevent, hStream: CUstream): CUresult = unsupported
  def cuEventRecordWithFlags(hEvent: CUevent, hStream: CUstream, flags: unsigned_int): CUresult = unsupported
  def cuEventSynchronize(hEvent: CUevent): CUresult = unsupported
  def cuExternalMemoryGetMappedBuffer(
      devPtr: Pointer[CUdeviceptr, ReadWrite, Opaque],
      extMem: CUexternalMemory,
      bufferDesc: Pointer[CUDA_EXTERNAL_MEMORY_BUFFER_DESC, Read, Opaque]
  ): CUresult = unsupported
  def cuExternalMemoryGetMappedMipmappedArray(
      mipmap: Pointer[CUmipmappedArray, ReadWrite, Opaque],
      extMem: CUexternalMemory,
      mipmapDesc: Pointer[CUDA_EXTERNAL_MEMORY_MIPMAPPED_ARRAY_DESC, Read, Opaque]
  ): CUresult = unsupported
  def cuFlushGPUDirectRDMAWrites(
      target: CUflushGPUDirectRDMAWritesTarget,
      scope: CUflushGPUDirectRDMAWritesScope
  ): CUresult = unsupported
  def cuFuncGetAttribute(
      pi: Pointer[Int, ReadWrite, Opaque],
      attrib: CUfunction_attribute,
      hfunc: CUfunction
  ): CUresult = unsupported
  def cuFuncGetModule(hmod: Pointer[CUmodule, ReadWrite, Opaque], hfunc: CUfunction): CUresult = unsupported
  def cuFuncGetName(name: Pointer[CString, ReadWrite, Opaque], hfunc: CUfunction): CUresult = unsupported
  def cuFuncGetParamCount(func: CUfunction, paramCount: Pointer[size_t, ReadWrite, Opaque]): CUresult = unsupported
  def cuFuncGetParamInfo(
      func: CUfunction,
      paramIndex: size_t,
      paramOffset: Pointer[size_t, ReadWrite, Opaque],
      paramSize: Pointer[size_t, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuFuncIsLoaded(state: Pointer[CUfunctionLoadingState, ReadWrite, Opaque], function: CUfunction): CUresult =
    unsupported
  def cuFuncLoad(function: CUfunction): CUresult = unsupported
  def cuFuncSetAttribute(hfunc: CUfunction, attrib: CUfunction_attribute, value: Int): CUresult = unsupported
  def cuFuncSetBlockShape(hfunc: CUfunction, x: Int, y: Int, z: Int): CUresult = unsupported
  def cuFuncSetCacheConfig(hfunc: CUfunction, config: CUfunc_cache): CUresult = unsupported
  def cuFuncSetSharedMemConfig(hfunc: CUfunction, config: CUsharedconfig): CUresult = unsupported
  def cuFuncSetSharedSize(hfunc: CUfunction, bytes: unsigned_int): CUresult = unsupported
  def cuGetErrorName(error: CUresult, pStr: Pointer[CString, ReadWrite, Opaque]): CUresult = unsupported
  def cuGetErrorString(error: CUresult, pStr: Pointer[CString, ReadWrite, Opaque]): CUresult = unsupported
  def cuGetExportTable(
      ppExportTable: Pointer[Pointer[CVoid, Read, Opaque], ReadWrite, Opaque],
      pExportTableId: Pointer[CUuuid, Read, Opaque]
  ): CUresult = unsupported
  def cuGetProcAddress_v2(
      symbol: CString,
      pfn: Pointer[OpaquePointer, ReadWrite, Opaque],
      cudaVersion: Int,
      flags: cuuint64_t,
      symbolStatus: Pointer[CUdriverProcAddressQueryResult, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphAddBatchMemOpNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[CUDA_BATCH_MEM_OP_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphAddChildGraphNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: size_t,
      childGraph: CUgraph
  ): CUresult = unsupported
  def cuGraphAddDependencies_v2(
      hGraph: CUgraph,
      from: Pointer[CUgraphNode, Read, Opaque],
      to: Pointer[CUgraphNode, Read, Opaque],
      edgeData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: size_t
  ): CUresult = unsupported
  def cuGraphAddEmptyNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: size_t
  ): CUresult = unsupported
  def cuGraphAddEventRecordNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: size_t,
      event: CUevent
  ): CUresult = unsupported
  def cuGraphAddEventWaitNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: size_t,
      event: CUevent
  ): CUresult = unsupported
  def cuGraphAddExternalSemaphoresSignalNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[CUDA_EXT_SEM_SIGNAL_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphAddExternalSemaphoresWaitNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[CUDA_EXT_SEM_WAIT_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphAddHostNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[CUDA_HOST_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphAddKernelNode_v2(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[CUDA_KERNEL_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphAddMemAllocNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[CUDA_MEM_ALLOC_NODE_PARAMS, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphAddMemFreeNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: size_t,
      dptr: CUdeviceptr
  ): CUresult = unsupported
  def cuGraphAddMemcpyNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: size_t,
      copyParams: Pointer[CUDA_MEMCPY3D, Read, Opaque],
      ctx: CUcontext
  ): CUresult = unsupported
  def cuGraphAddMemsetNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: size_t,
      memsetParams: Pointer[CUDA_MEMSET_NODE_PARAMS, Read, Opaque],
      ctx: CUcontext
  ): CUresult = unsupported
  def cuGraphAddNode_v2(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      dependencyData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[CUgraphNodeParams, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphBatchMemOpNodeGetParams(
      hNode: CUgraphNode,
      nodeParams_out: Pointer[CUDA_BATCH_MEM_OP_NODE_PARAMS, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphBatchMemOpNodeSetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_BATCH_MEM_OP_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphChildGraphNodeGetGraph(hNode: CUgraphNode, phGraph: Pointer[CUgraph, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuGraphClone(phGraphClone: Pointer[CUgraph, ReadWrite, Opaque], originalGraph: CUgraph): CUresult = unsupported
  def cuGraphConditionalHandleCreate(
      pHandle_out: Pointer[CUgraphConditionalHandle, ReadWrite, Opaque],
      hGraph: CUgraph,
      ctx: CUcontext,
      defaultLaunchValue: unsigned_int,
      flags: unsigned_int
  ): CUresult = unsupported
  def cuGraphCreate(phGraph: Pointer[CUgraph, ReadWrite, Opaque], flags: unsigned_int): CUresult = unsupported
  def cuGraphDebugDotPrint(hGraph: CUgraph, path: CString, flags: unsigned_int): CUresult = unsupported
  def cuGraphDestroy(hGraph: CUgraph): CUresult = unsupported
  def cuGraphDestroyNode(hNode: CUgraphNode): CUresult = unsupported
  def cuGraphEventRecordNodeGetEvent(hNode: CUgraphNode, event_out: Pointer[CUevent, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuGraphEventRecordNodeSetEvent(hNode: CUgraphNode, event: CUevent): CUresult = unsupported
  def cuGraphEventWaitNodeGetEvent(hNode: CUgraphNode, event_out: Pointer[CUevent, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuGraphEventWaitNodeSetEvent(hNode: CUgraphNode, event: CUevent): CUresult = unsupported
  def cuGraphExecBatchMemOpNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_BATCH_MEM_OP_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphExecChildGraphNodeSetParams(hGraphExec: CUgraphExec, hNode: CUgraphNode, childGraph: CUgraph): CUresult =
    unsupported
  def cuGraphExecDestroy(hGraphExec: CUgraphExec): CUresult = unsupported
  def cuGraphExecEventRecordNodeSetEvent(hGraphExec: CUgraphExec, hNode: CUgraphNode, event: CUevent): CUresult =
    unsupported
  def cuGraphExecEventWaitNodeSetEvent(hGraphExec: CUgraphExec, hNode: CUgraphNode, event: CUevent): CUresult =
    unsupported
  def cuGraphExecExternalSemaphoresSignalNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_EXT_SEM_SIGNAL_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphExecExternalSemaphoresWaitNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_EXT_SEM_WAIT_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphExecGetFlags(hGraphExec: CUgraphExec, flags: Pointer[cuuint64_t, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuGraphExecGetId(hGraphExec: CUgraphExec, graphId: Pointer[unsigned_int, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuGraphExecHostNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_HOST_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphExecKernelNodeSetParams_v2(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_KERNEL_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphExecMemcpyNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      copyParams: Pointer[CUDA_MEMCPY3D, Read, Opaque],
      ctx: CUcontext
  ): CUresult = unsupported
  def cuGraphExecMemsetNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      memsetParams: Pointer[CUDA_MEMSET_NODE_PARAMS, Read, Opaque],
      ctx: CUcontext
  ): CUresult = unsupported
  def cuGraphExecNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUgraphNodeParams, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphExecUpdate_v2(
      hGraphExec: CUgraphExec,
      hGraph: CUgraph,
      resultInfo: Pointer[CUgraphExecUpdateResultInfo, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphExternalSemaphoresSignalNodeGetParams(
      hNode: CUgraphNode,
      params_out: Pointer[CUDA_EXT_SEM_SIGNAL_NODE_PARAMS, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphExternalSemaphoresSignalNodeSetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_EXT_SEM_SIGNAL_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphExternalSemaphoresWaitNodeGetParams(
      hNode: CUgraphNode,
      params_out: Pointer[CUDA_EXT_SEM_WAIT_NODE_PARAMS, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphExternalSemaphoresWaitNodeSetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_EXT_SEM_WAIT_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphGetEdges_v2(
      hGraph: CUgraph,
      from: Pointer[CUgraphNode, ReadWrite, Opaque],
      to: Pointer[CUgraphNode, ReadWrite, Opaque],
      edgeData: Pointer[CUgraphEdgeData, ReadWrite, Opaque],
      numEdges: Pointer[size_t, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphGetId(hGraph: CUgraph, graphId: Pointer[unsigned_int, ReadWrite, Opaque]): CUresult = unsupported
  def cuGraphGetNodes(
      hGraph: CUgraph,
      nodes: Pointer[CUgraphNode, ReadWrite, Opaque],
      numNodes: Pointer[size_t, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphGetRootNodes(
      hGraph: CUgraph,
      rootNodes: Pointer[CUgraphNode, ReadWrite, Opaque],
      numRootNodes: Pointer[size_t, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphHostNodeGetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_HOST_NODE_PARAMS, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphHostNodeSetParams(hNode: CUgraphNode, nodeParams: Pointer[CUDA_HOST_NODE_PARAMS, Read, Opaque]): CUresult =
    unsupported
  def cuGraphInstantiateWithFlags(
      phGraphExec: Pointer[CUgraphExec, ReadWrite, Opaque],
      hGraph: CUgraph,
      flags: unsigned_long_long
  ): CUresult = unsupported
  def cuGraphInstantiateWithParams(
      phGraphExec: Pointer[CUgraphExec, ReadWrite, Opaque],
      hGraph: CUgraph,
      instantiateParams: Pointer[CUDA_GRAPH_INSTANTIATE_PARAMS, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphKernelNodeCopyAttributes(dst: CUgraphNode, src: CUgraphNode): CUresult = unsupported
  def cuGraphKernelNodeGetAttribute(
      hNode: CUgraphNode,
      attr: CUkernelNodeAttrID,
      value_out: Pointer[CUkernelNodeAttrValue, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphKernelNodeGetParams_v2(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_KERNEL_NODE_PARAMS, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphKernelNodeSetAttribute(
      hNode: CUgraphNode,
      attr: CUkernelNodeAttrID,
      value: Pointer[CUkernelNodeAttrValue, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphKernelNodeSetParams_v2(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_KERNEL_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphLaunch(hGraphExec: CUgraphExec, hStream: CUstream): CUresult = unsupported
  def cuGraphMemAllocNodeGetParams(
      hNode: CUgraphNode,
      params_out: Pointer[CUDA_MEM_ALLOC_NODE_PARAMS, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphMemFreeNodeGetParams(hNode: CUgraphNode, dptr_out: Pointer[CUdeviceptr, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuGraphMemcpyNodeGetParams(hNode: CUgraphNode, nodeParams: Pointer[CUDA_MEMCPY3D, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuGraphMemcpyNodeSetParams(hNode: CUgraphNode, nodeParams: Pointer[CUDA_MEMCPY3D, Read, Opaque]): CUresult =
    unsupported
  def cuGraphMemsetNodeGetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_MEMSET_NODE_PARAMS, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphMemsetNodeSetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_MEMSET_NODE_PARAMS, Read, Opaque]
  ): CUresult = unsupported
  def cuGraphNodeFindInClone(
      phNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hOriginalNode: CUgraphNode,
      hClonedGraph: CUgraph
  ): CUresult = unsupported
  def cuGraphNodeGetContainingGraph(hNode: CUgraphNode, phGraph: Pointer[CUgraph, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuGraphNodeGetDependencies_v2(
      hNode: CUgraphNode,
      dependencies: Pointer[CUgraphNode, ReadWrite, Opaque],
      edgeData: Pointer[CUgraphEdgeData, ReadWrite, Opaque],
      numDependencies: Pointer[size_t, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphNodeGetDependentNodes_v2(
      hNode: CUgraphNode,
      dependentNodes: Pointer[CUgraphNode, ReadWrite, Opaque],
      edgeData: Pointer[CUgraphEdgeData, ReadWrite, Opaque],
      numDependentNodes: Pointer[size_t, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphNodeGetEnabled(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      isEnabled: Pointer[unsigned_int, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuGraphNodeGetLocalId(hNode: CUgraphNode, nodeId: Pointer[unsigned_int, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuGraphNodeGetParams(hNode: CUgraphNode, nodeParams: Pointer[CUgraphNodeParams, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuGraphNodeGetToolsId(hNode: CUgraphNode, toolsNodeId: Pointer[unsigned_long_long, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuGraphNodeGetType(hNode: CUgraphNode, `type`: Pointer[CUgraphNodeType, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuGraphNodeSetEnabled(hGraphExec: CUgraphExec, hNode: CUgraphNode, isEnabled: unsigned_int): CUresult =
    unsupported
  def cuGraphNodeSetParams(hNode: CUgraphNode, nodeParams: Pointer[CUgraphNodeParams, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuGraphReleaseUserObject(graph: CUgraph, `object`: CUuserObject, count: unsigned_int): CUresult = unsupported
  def cuGraphRemoveDependencies_v2(
      hGraph: CUgraph,
      from: Pointer[CUgraphNode, Read, Opaque],
      to: Pointer[CUgraphNode, Read, Opaque],
      edgeData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: size_t
  ): CUresult = unsupported
  def cuGraphRetainUserObject(
      graph: CUgraph,
      `object`: CUuserObject,
      count: unsigned_int,
      flags: unsigned_int
  ): CUresult = unsupported
  def cuGraphUpload(hGraphExec: CUgraphExec, hStream: CUstream): CUresult = unsupported
  def cuGraphicsMapResources(
      count: unsigned_int,
      resources: Pointer[CUgraphicsResource, ReadWrite, Opaque],
      hStream: CUstream
  ): CUresult = unsupported
  def cuGraphicsResourceGetMappedMipmappedArray(
      pMipmappedArray: Pointer[CUmipmappedArray, ReadWrite, Opaque],
      resource: CUgraphicsResource
  ): CUresult = unsupported
  def cuGraphicsResourceGetMappedPointer_v2(
      pDevPtr: Pointer[CUdeviceptr, ReadWrite, Opaque],
      pSize: Pointer[size_t, ReadWrite, Opaque],
      resource: CUgraphicsResource
  ): CUresult = unsupported
  def cuGraphicsResourceSetMapFlags_v2(resource: CUgraphicsResource, flags: unsigned_int): CUresult = unsupported
  def cuGraphicsSubResourceGetMappedArray(
      pArray: Pointer[CUarray, ReadWrite, Opaque],
      resource: CUgraphicsResource,
      arrayIndex: unsigned_int,
      mipLevel: unsigned_int
  ): CUresult = unsupported
  def cuGraphicsUnmapResources(
      count: unsigned_int,
      resources: Pointer[CUgraphicsResource, ReadWrite, Opaque],
      hStream: CUstream
  ): CUresult = unsupported
  def cuGraphicsUnregisterResource(resource: CUgraphicsResource): CUresult = unsupported
  def cuGreenCtxCreate(
      phCtx: Pointer[CUgreenCtx, ReadWrite, Opaque],
      desc: CUdevResourceDesc,
      dev: CUdevice,
      flags: unsigned_int
  ): CUresult = unsupported
  def cuGreenCtxDestroy(hCtx: CUgreenCtx): CUresult = unsupported
  def cuGreenCtxGetDevResource(
      hCtx: CUgreenCtx,
      resource: Pointer[CUdevResource, ReadWrite, Opaque],
      `type`: CUdevResourceType
  ): CUresult = unsupported
  def cuGreenCtxGetId(greenCtx: CUgreenCtx, greenCtxId: Pointer[unsigned_long_long, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuGreenCtxRecordEvent(hCtx: CUgreenCtx, hEvent: CUevent): CUresult = unsupported
  def cuGreenCtxStreamCreate(
      phStream: Pointer[CUstream, ReadWrite, Opaque],
      greenCtx: CUgreenCtx,
      flags: unsigned_int,
      priority: Int
  ): CUresult = unsupported
  def cuGreenCtxWaitEvent(hCtx: CUgreenCtx, hEvent: CUevent): CUresult = unsupported
  def cuImportExternalMemory(
      extMem_out: Pointer[CUexternalMemory, ReadWrite, Opaque],
      memHandleDesc: Pointer[CUDA_EXTERNAL_MEMORY_HANDLE_DESC, Read, Opaque]
  ): CUresult = unsupported
  def cuImportExternalSemaphore(
      extSem_out: Pointer[CUexternalSemaphore, ReadWrite, Opaque],
      semHandleDesc: Pointer[CUDA_EXTERNAL_SEMAPHORE_HANDLE_DESC, Read, Opaque]
  ): CUresult = unsupported
  def cuInit(Flags: unsigned_int): CUresult = unsupported
  def cuIpcCloseMemHandle(dptr: CUdeviceptr): CUresult = unsupported
  def cuIpcGetEventHandle(pHandle: Pointer[CUipcEventHandle, ReadWrite, Opaque], event: CUevent): CUresult = unsupported
  def cuIpcGetMemHandle(pHandle: Pointer[CUipcMemHandle, ReadWrite, Opaque], dptr: CUdeviceptr): CUresult = unsupported
  def cuIpcOpenEventHandle(phEvent: Pointer[CUevent, ReadWrite, Opaque], handle: CUipcEventHandle): CUresult =
    unsupported
  def cuIpcOpenMemHandle_v2(
      pdptr: Pointer[CUdeviceptr, ReadWrite, Opaque],
      handle: CUipcMemHandle,
      Flags: unsigned_int
  ): CUresult = unsupported
  def cuKernelGetAttribute(
      pi: Pointer[Int, ReadWrite, Opaque],
      attrib: CUfunction_attribute,
      kernel: CUkernel,
      dev: CUdevice
  ): CUresult = unsupported
  def cuKernelGetFunction(pFunc: Pointer[CUfunction, ReadWrite, Opaque], kernel: CUkernel): CUresult = unsupported
  def cuKernelGetLibrary(pLib: Pointer[CUlibrary, ReadWrite, Opaque], kernel: CUkernel): CUresult = unsupported
  def cuKernelGetName(name: Pointer[CString, ReadWrite, Opaque], hfunc: CUkernel): CUresult = unsupported
  def cuKernelGetParamCount(kernel: CUkernel, paramCount: Pointer[size_t, ReadWrite, Opaque]): CUresult = unsupported
  def cuKernelGetParamInfo(
      kernel: CUkernel,
      paramIndex: size_t,
      paramOffset: Pointer[size_t, ReadWrite, Opaque],
      paramSize: Pointer[size_t, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuKernelSetAttribute(attrib: CUfunction_attribute, `val`: Int, kernel: CUkernel, dev: CUdevice): CUresult =
    unsupported
  def cuKernelSetCacheConfig(kernel: CUkernel, config: CUfunc_cache, dev: CUdevice): CUresult = unsupported
  def cuLaunch(f: CUfunction): CUresult = unsupported
  def cuLaunchCooperativeKernel(
      f: CUfunction,
      gridDimX: unsigned_int,
      gridDimY: unsigned_int,
      gridDimZ: unsigned_int,
      blockDimX: unsigned_int,
      blockDimY: unsigned_int,
      blockDimZ: unsigned_int,
      sharedMemBytes: unsigned_int,
      hStream: CUstream,
      kernelParams: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuLaunchCooperativeKernelMultiDevice(
      launchParamsList: Pointer[CUDA_LAUNCH_PARAMS, ReadWrite, Opaque],
      numDevices: unsigned_int,
      flags: unsigned_int
  ): CUresult = unsupported
  def cuLaunchGrid(f: CUfunction, grid_width: Int, grid_height: Int): CUresult = unsupported
  def cuLaunchGridAsync(f: CUfunction, grid_width: Int, grid_height: Int, hStream: CUstream): CUresult = unsupported
  def cuLaunchHostFunc(hStream: CUstream, fn: CUhostFn, userData: OpaquePointer): CUresult = unsupported
  def cuLaunchHostFunc_v2(hStream: CUstream, fn: CUhostFn, userData: OpaquePointer, syncMode: unsigned_int): CUresult =
    unsupported
  def cuLaunchKernel(
      f: CUfunction,
      gridDimX: unsigned_int,
      gridDimY: unsigned_int,
      gridDimZ: unsigned_int,
      blockDimX: unsigned_int,
      blockDimY: unsigned_int,
      blockDimZ: unsigned_int,
      sharedMemBytes: unsigned_int,
      hStream: CUstream,
      kernelParams: Pointer[OpaquePointer, ReadWrite, Opaque],
      extra: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuLaunchKernelEx(
      config: Pointer[CUlaunchConfig, Read, Opaque],
      f: CUfunction,
      kernelParams: Pointer[OpaquePointer, ReadWrite, Opaque],
      extra: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuLibraryEnumerateKernels(
      kernels: Pointer[CUkernel, ReadWrite, Opaque],
      numKernels: unsigned_int,
      lib: CUlibrary
  ): CUresult = unsupported
  def cuLibraryGetGlobal(
      dptr: Pointer[CUdeviceptr, ReadWrite, Opaque],
      bytes: Pointer[size_t, ReadWrite, Opaque],
      library: CUlibrary,
      name: CString
  ): CUresult = unsupported
  def cuLibraryGetKernel(pKernel: Pointer[CUkernel, ReadWrite, Opaque], library: CUlibrary, name: CString): CUresult =
    unsupported
  def cuLibraryGetKernelCount(count: Pointer[unsigned_int, ReadWrite, Opaque], lib: CUlibrary): CUresult = unsupported
  def cuLibraryGetManaged(
      dptr: Pointer[CUdeviceptr, ReadWrite, Opaque],
      bytes: Pointer[size_t, ReadWrite, Opaque],
      library: CUlibrary,
      name: CString
  ): CUresult = unsupported
  def cuLibraryGetModule(pMod: Pointer[CUmodule, ReadWrite, Opaque], library: CUlibrary): CUresult = unsupported
  def cuLibraryGetUnifiedFunction(
      fptr: Pointer[OpaquePointer, ReadWrite, Opaque],
      library: CUlibrary,
      symbol: CString
  ): CUresult = unsupported
  def cuLibraryLoadData(
      library: Pointer[CUlibrary, ReadWrite, Opaque],
      code: Pointer[CVoid, Read, Opaque],
      jitOptions: Pointer[CUjit_option, ReadWrite, Opaque],
      jitOptionsValues: Pointer[OpaquePointer, ReadWrite, Opaque],
      numJitOptions: unsigned_int,
      libraryOptions: Pointer[CUlibraryOption, ReadWrite, Opaque],
      libraryOptionValues: Pointer[OpaquePointer, ReadWrite, Opaque],
      numLibraryOptions: unsigned_int
  ): CUresult = unsupported
  def cuLibraryLoadFromFile(
      library: Pointer[CUlibrary, ReadWrite, Opaque],
      fileName: CString,
      jitOptions: Pointer[CUjit_option, ReadWrite, Opaque],
      jitOptionsValues: Pointer[OpaquePointer, ReadWrite, Opaque],
      numJitOptions: unsigned_int,
      libraryOptions: Pointer[CUlibraryOption, ReadWrite, Opaque],
      libraryOptionValues: Pointer[OpaquePointer, ReadWrite, Opaque],
      numLibraryOptions: unsigned_int
  ): CUresult = unsupported
  def cuLibraryUnload(library: CUlibrary): CUresult = unsupported
  def cuLinkAddData_v2(
      state: CUlinkState,
      `type`: CUjitInputType,
      data: OpaquePointer,
      size: size_t,
      name: CString,
      numOptions: unsigned_int,
      options: Pointer[CUjit_option, ReadWrite, Opaque],
      optionValues: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuLinkAddFile_v2(
      state: CUlinkState,
      `type`: CUjitInputType,
      path: CString,
      numOptions: unsigned_int,
      options: Pointer[CUjit_option, ReadWrite, Opaque],
      optionValues: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuLinkComplete(
      state: CUlinkState,
      cubinOut: Pointer[OpaquePointer, ReadWrite, Opaque],
      sizeOut: Pointer[size_t, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuLinkCreate_v2(
      numOptions: unsigned_int,
      options: Pointer[CUjit_option, ReadWrite, Opaque],
      optionValues: Pointer[OpaquePointer, ReadWrite, Opaque],
      stateOut: Pointer[CUlinkState, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuLinkDestroy(state: CUlinkState): CUresult = unsupported
  def cuLogicalEndpointAddDevice(leId: CUlogicalEndpointId, dev: CUdevice): CUresult = unsupported
  def cuLogicalEndpointBindAddr(
      leId: CUlogicalEndpointId,
      dev: CUdevice,
      offset: cuuint64_t,
      ptr: OpaquePointer,
      size: cuuint64_t,
      flags: unsigned_long_long
  ): CUresult = unsupported
  def cuLogicalEndpointBindMem(
      leId: CUlogicalEndpointId,
      dev: CUdevice,
      offset: cuuint64_t,
      memHandle: CUmemGenericAllocationHandle,
      memOffset: cuuint64_t,
      size: cuuint64_t,
      flags: unsigned_long_long
  ): CUresult = unsupported
  def cuLogicalEndpointCreate(leId: CUlogicalEndpointId, prop: Pointer[CUlogicalEndpointProp, Read, Opaque]): CUresult =
    unsupported
  def cuLogicalEndpointDestroy(leId: CUlogicalEndpointId): CUresult = unsupported
  def cuLogicalEndpointExport(
      handle: OpaquePointer,
      leId: CUlogicalEndpointId,
      handleType: CUlogicalEndpointIpcHandleType
  ): CUresult = unsupported
  def cuLogicalEndpointGetLimits(
      bindAlignment: Pointer[cuuint64_t, ReadWrite, Opaque],
      maxSize: Pointer[cuuint64_t, ReadWrite, Opaque],
      prop: Pointer[CUlogicalEndpointProp, Read, Opaque]
  ): CUresult = unsupported
  def cuLogicalEndpointIdRelease(baseLeId: CUlogicalEndpointId, count: cuuint32_t): CUresult = unsupported
  def cuLogicalEndpointIdReserve(
      baseLeId: Pointer[CUlogicalEndpointId, ReadWrite, Opaque],
      count: cuuint32_t
  ): CUresult = unsupported
  def cuLogicalEndpointImport(
      leId: CUlogicalEndpointId,
      handle: Pointer[CVoid, Read, Opaque],
      handleType: CUlogicalEndpointIpcHandleType
  ): CUresult = unsupported
  def cuLogicalEndpointQuery(
      leId: CUlogicalEndpointId,
      count: cuuint32_t,
      queryStatus: Pointer[Int, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuLogicalEndpointUnbind(
      leId: CUlogicalEndpointId,
      dev: CUdevice,
      offset: cuuint64_t,
      size: cuuint64_t
  ): CUresult = unsupported
  def cuLogsCurrent(iterator_out: Pointer[CUlogIterator, ReadWrite, Opaque], flags: unsigned_int): CUresult =
    unsupported
  def cuLogsDumpToFile(
      iterator: Pointer[CUlogIterator, ReadWrite, Opaque],
      pathToFile: CString,
      flags: unsigned_int
  ): CUresult = unsupported
  def cuLogsDumpToMemory(
      iterator: Pointer[CUlogIterator, ReadWrite, Opaque],
      buffer: Pointer[Byte, ReadWrite, Opaque],
      size: Pointer[size_t, ReadWrite, Opaque],
      flags: unsigned_int
  ): CUresult = unsupported
  def cuLogsRegisterCallback(
      callbackFunc: CUlogsCallback,
      userData: OpaquePointer,
      callback_out: Pointer[CUlogsCallbackHandle, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuLogsUnregisterCallback(callback: CUlogsCallbackHandle): CUresult = unsupported
  def cuMemAddressFree(ptr: CUdeviceptr, size: size_t): CUresult = unsupported
  def cuMemAddressReserve(
      ptr: Pointer[CUdeviceptr, ReadWrite, Opaque],
      size: size_t,
      alignment: size_t,
      addr: CUdeviceptr,
      flags: unsigned_long_long
  ): CUresult = unsupported
  def cuMemAdvise_v2(devPtr: CUdeviceptr, count: size_t, advice: CUmem_advise, location: CUmemLocation): CUresult =
    unsupported
  def cuMemAllocAsync(dptr: Pointer[CUdeviceptr, ReadWrite, Opaque], bytesize: size_t, hStream: CUstream): CUresult =
    unsupported
  def cuMemAllocFromPoolAsync(
      dptr: Pointer[CUdeviceptr, ReadWrite, Opaque],
      bytesize: size_t,
      pool: CUmemoryPool,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemAllocHost_v2(pp: Pointer[OpaquePointer, ReadWrite, Opaque], bytesize: size_t): CUresult = unsupported
  def cuMemAllocManaged(
      dptr: Pointer[CUdeviceptr, ReadWrite, Opaque],
      bytesize: size_t,
      flags: unsigned_int
  ): CUresult = unsupported
  def cuMemAllocPitch_v2(
      dptr: Pointer[CUdeviceptr, ReadWrite, Opaque],
      pPitch: Pointer[size_t, ReadWrite, Opaque],
      WidthInBytes: size_t,
      Height: size_t,
      ElementSizeBytes: unsigned_int
  ): CUresult = unsupported
  def cuMemAlloc_v2(dptr: Pointer[CUdeviceptr, ReadWrite, Opaque], bytesize: size_t): CUresult = unsupported
  def cuMemBatchDecompressAsync(
      paramsArray: Pointer[CUmemDecompressParams, ReadWrite, Opaque],
      count: size_t,
      flags: unsigned_int,
      errorIndex: Pointer[size_t, ReadWrite, Opaque],
      stream: CUstream
  ): CUresult = unsupported
  def cuMemCreate(
      handle: Pointer[CUmemGenericAllocationHandle, ReadWrite, Opaque],
      size: size_t,
      prop: Pointer[CUmemAllocationProp, Read, Opaque],
      flags: unsigned_long_long
  ): CUresult = unsupported
  def cuMemDiscardAndPrefetchBatchAsync(
      dptrs: Pointer[CUdeviceptr, ReadWrite, Opaque],
      sizes: Pointer[size_t, ReadWrite, Opaque],
      count: size_t,
      prefetchLocs: Pointer[CUmemLocation, ReadWrite, Opaque],
      prefetchLocIdxs: Pointer[size_t, ReadWrite, Opaque],
      numPrefetchLocs: size_t,
      flags: unsigned_long_long,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemDiscardBatchAsync(
      dptrs: Pointer[CUdeviceptr, ReadWrite, Opaque],
      sizes: Pointer[size_t, ReadWrite, Opaque],
      count: size_t,
      flags: unsigned_long_long,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemExportToShareableHandle(
      shareableHandle: OpaquePointer,
      handle: CUmemGenericAllocationHandle,
      handleType: CUmemAllocationHandleType,
      flags: unsigned_long_long
  ): CUresult = unsupported
  def cuMemFreeAsync(dptr: CUdeviceptr, hStream: CUstream): CUresult = unsupported
  def cuMemFreeHost(p: OpaquePointer): CUresult = unsupported
  def cuMemFree_v2(dptr: CUdeviceptr): CUresult = unsupported
  def cuMemGetAccess(
      flags: Pointer[unsigned_long_long, ReadWrite, Opaque],
      location: Pointer[CUmemLocation, Read, Opaque],
      ptr: CUdeviceptr
  ): CUresult = unsupported
  def cuMemGetAddressRange_v2(
      pbase: Pointer[CUdeviceptr, ReadWrite, Opaque],
      psize: Pointer[size_t, ReadWrite, Opaque],
      dptr: CUdeviceptr
  ): CUresult = unsupported
  def cuMemGetAllocationGranularity(
      granularity: Pointer[size_t, ReadWrite, Opaque],
      prop: Pointer[CUmemAllocationProp, Read, Opaque],
      option: CUmemAllocationGranularity_flags
  ): CUresult = unsupported
  def cuMemGetAllocationPropertiesFromHandle(
      prop: Pointer[CUmemAllocationProp, ReadWrite, Opaque],
      handle: CUmemGenericAllocationHandle
  ): CUresult = unsupported
  def cuMemGetDefaultMemPool(
      pool_out: Pointer[CUmemoryPool, ReadWrite, Opaque],
      location: Pointer[CUmemLocation, ReadWrite, Opaque],
      `type`: CUmemAllocationType
  ): CUresult = unsupported
  def cuMemGetHandleForAddressRange(
      handle: OpaquePointer,
      dptr: CUdeviceptr,
      size: size_t,
      handleType: CUmemRangeHandleType,
      flags: unsigned_long_long
  ): CUresult = unsupported
  def cuMemGetInfo_v2(free: Pointer[size_t, ReadWrite, Opaque], total: Pointer[size_t, ReadWrite, Opaque]): CUresult =
    unsupported
  def cuMemGetMemPool(
      pool: Pointer[CUmemoryPool, ReadWrite, Opaque],
      location: Pointer[CUmemLocation, ReadWrite, Opaque],
      `type`: CUmemAllocationType
  ): CUresult = unsupported
  def cuMemHostAlloc(pp: Pointer[OpaquePointer, ReadWrite, Opaque], bytesize: size_t, Flags: unsigned_int): CUresult =
    unsupported
  def cuMemHostGetDevicePointer_v2(
      pdptr: Pointer[CUdeviceptr, ReadWrite, Opaque],
      p: OpaquePointer,
      Flags: unsigned_int
  ): CUresult = unsupported
  def cuMemHostGetFlags(pFlags: Pointer[unsigned_int, ReadWrite, Opaque], p: OpaquePointer): CUresult = unsupported
  def cuMemHostRegister_v2(p: OpaquePointer, bytesize: size_t, Flags: unsigned_int): CUresult = unsupported
  def cuMemHostUnregister(p: OpaquePointer): CUresult = unsupported
  def cuMemImportFromShareableHandle(
      handle: Pointer[CUmemGenericAllocationHandle, ReadWrite, Opaque],
      osHandle: OpaquePointer,
      shHandleType: CUmemAllocationHandleType
  ): CUresult = unsupported
  def cuMemMap(
      ptr: CUdeviceptr,
      size: size_t,
      offset: size_t,
      handle: CUmemGenericAllocationHandle,
      flags: unsigned_long_long
  ): CUresult = unsupported
  def cuMemMapArrayAsync(
      mapInfoList: Pointer[CUarrayMapInfo, ReadWrite, Opaque],
      count: unsigned_int,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemPoolCreate(
      pool: Pointer[CUmemoryPool, ReadWrite, Opaque],
      poolProps: Pointer[CUmemPoolProps, Read, Opaque]
  ): CUresult = unsupported
  def cuMemPoolDestroy(pool: CUmemoryPool): CUresult = unsupported
  def cuMemPoolExportPointer(
      shareData_out: Pointer[CUmemPoolPtrExportData, ReadWrite, Opaque],
      ptr: CUdeviceptr
  ): CUresult = unsupported
  def cuMemPoolExportToShareableHandle(
      handle_out: OpaquePointer,
      pool: CUmemoryPool,
      handleType: CUmemAllocationHandleType,
      flags: unsigned_long_long
  ): CUresult = unsupported
  def cuMemPoolGetAccess(
      flags: Pointer[CUmemAccess_flags, ReadWrite, Opaque],
      memPool: CUmemoryPool,
      location: Pointer[CUmemLocation, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuMemPoolGetAttribute(pool: CUmemoryPool, attr: CUmemPool_attribute, value: OpaquePointer): CUresult = unsupported
  def cuMemPoolImportFromShareableHandle(
      pool_out: Pointer[CUmemoryPool, ReadWrite, Opaque],
      handle: OpaquePointer,
      handleType: CUmemAllocationHandleType,
      flags: unsigned_long_long
  ): CUresult = unsupported
  def cuMemPoolImportPointer(
      ptr_out: Pointer[CUdeviceptr, ReadWrite, Opaque],
      pool: CUmemoryPool,
      shareData: Pointer[CUmemPoolPtrExportData, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuMemPoolSetAccess(pool: CUmemoryPool, map: Pointer[CUmemAccessDesc, Read, Opaque], count: size_t): CUresult =
    unsupported
  def cuMemPoolSetAttribute(pool: CUmemoryPool, attr: CUmemPool_attribute, value: OpaquePointer): CUresult = unsupported
  def cuMemPoolTrimTo(pool: CUmemoryPool, minBytesToKeep: size_t): CUresult = unsupported
  def cuMemPrefetchAsync_v2(
      devPtr: CUdeviceptr,
      count: size_t,
      location: CUmemLocation,
      flags: unsigned_int,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemPrefetchBatchAsync(
      dptrs: Pointer[CUdeviceptr, ReadWrite, Opaque],
      sizes: Pointer[size_t, ReadWrite, Opaque],
      count: size_t,
      prefetchLocs: Pointer[CUmemLocation, ReadWrite, Opaque],
      prefetchLocIdxs: Pointer[size_t, ReadWrite, Opaque],
      numPrefetchLocs: size_t,
      flags: unsigned_long_long,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemRangeGetAttribute(
      data: OpaquePointer,
      dataSize: size_t,
      attribute: CUmem_range_attribute,
      devPtr: CUdeviceptr,
      count: size_t
  ): CUresult = unsupported
  def cuMemRangeGetAttributes(
      data: Pointer[OpaquePointer, ReadWrite, Opaque],
      dataSizes: Pointer[size_t, ReadWrite, Opaque],
      attributes: Pointer[CUmem_range_attribute, ReadWrite, Opaque],
      numAttributes: size_t,
      devPtr: CUdeviceptr,
      count: size_t
  ): CUresult = unsupported
  def cuMemRelease(handle: CUmemGenericAllocationHandle): CUresult = unsupported
  def cuMemRetainAllocationHandle(
      handle: Pointer[CUmemGenericAllocationHandle, ReadWrite, Opaque],
      addr: OpaquePointer
  ): CUresult = unsupported
  def cuMemSetAccess(
      ptr: CUdeviceptr,
      size: size_t,
      desc: Pointer[CUmemAccessDesc, Read, Opaque],
      count: size_t
  ): CUresult = unsupported
  def cuMemSetMemPool(
      location: Pointer[CUmemLocation, ReadWrite, Opaque],
      `type`: CUmemAllocationType,
      pool: CUmemoryPool
  ): CUresult = unsupported
  def cuMemUnmap(ptr: CUdeviceptr, size: size_t): CUresult = unsupported
  def cuMemcpy(dst: CUdeviceptr, src: CUdeviceptr, ByteCount: size_t): CUresult = unsupported
  def cuMemcpy2DAsync_v2(pCopy: Pointer[CUDA_MEMCPY2D, Read, Opaque], hStream: CUstream): CUresult = unsupported
  def cuMemcpy2DUnaligned_v2(pCopy: Pointer[CUDA_MEMCPY2D, Read, Opaque]): CUresult = unsupported
  def cuMemcpy2D_v2(pCopy: Pointer[CUDA_MEMCPY2D, Read, Opaque]): CUresult = unsupported
  def cuMemcpy3DAsync_v2(pCopy: Pointer[CUDA_MEMCPY3D, Read, Opaque], hStream: CUstream): CUresult = unsupported
  def cuMemcpy3DBatchAsync_v2(
      numOps: size_t,
      opList: Pointer[CUDA_MEMCPY3D_BATCH_OP, ReadWrite, Opaque],
      flags: unsigned_long_long,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemcpy3DPeer(pCopy: Pointer[CUDA_MEMCPY3D_PEER, Read, Opaque]): CUresult = unsupported
  def cuMemcpy3DPeerAsync(pCopy: Pointer[CUDA_MEMCPY3D_PEER, Read, Opaque], hStream: CUstream): CUresult = unsupported
  def cuMemcpy3DWithAttributesAsync(
      op: Pointer[CUDA_MEMCPY3D_BATCH_OP, ReadWrite, Opaque],
      flags: unsigned_long_long,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemcpy3D_v2(pCopy: Pointer[CUDA_MEMCPY3D, Read, Opaque]): CUresult = unsupported
  def cuMemcpyAsync(dst: CUdeviceptr, src: CUdeviceptr, ByteCount: size_t, hStream: CUstream): CUresult = unsupported
  def cuMemcpyAtoA_v2(
      dstArray: CUarray,
      dstOffset: size_t,
      srcArray: CUarray,
      srcOffset: size_t,
      ByteCount: size_t
  ): CUresult = unsupported
  def cuMemcpyAtoD_v2(dstDevice: CUdeviceptr, srcArray: CUarray, srcOffset: size_t, ByteCount: size_t): CUresult =
    unsupported
  def cuMemcpyAtoHAsync_v2(
      dstHost: OpaquePointer,
      srcArray: CUarray,
      srcOffset: size_t,
      ByteCount: size_t,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemcpyAtoH_v2(dstHost: OpaquePointer, srcArray: CUarray, srcOffset: size_t, ByteCount: size_t): CUresult =
    unsupported
  def cuMemcpyBatchAsync_v2(
      dsts: Pointer[CUdeviceptr, ReadWrite, Opaque],
      srcs: Pointer[CUdeviceptr, ReadWrite, Opaque],
      sizes: Pointer[size_t, ReadWrite, Opaque],
      count: size_t,
      attrs: Pointer[CUmemcpyAttributes, ReadWrite, Opaque],
      attrsIdxs: Pointer[size_t, ReadWrite, Opaque],
      numAttrs: size_t,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemcpyDtoA_v2(dstArray: CUarray, dstOffset: size_t, srcDevice: CUdeviceptr, ByteCount: size_t): CUresult =
    unsupported
  def cuMemcpyDtoDAsync_v2(
      dstDevice: CUdeviceptr,
      srcDevice: CUdeviceptr,
      ByteCount: size_t,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemcpyDtoD_v2(dstDevice: CUdeviceptr, srcDevice: CUdeviceptr, ByteCount: size_t): CUresult = unsupported
  def cuMemcpyDtoHAsync_v2(
      dstHost: OpaquePointer,
      srcDevice: CUdeviceptr,
      ByteCount: size_t,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemcpyDtoH_v2(dstHost: OpaquePointer, srcDevice: CUdeviceptr, ByteCount: size_t): CUresult = unsupported
  def cuMemcpyHtoAAsync_v2(
      dstArray: CUarray,
      dstOffset: size_t,
      srcHost: Pointer[CVoid, Read, Opaque],
      ByteCount: size_t,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemcpyHtoA_v2(
      dstArray: CUarray,
      dstOffset: size_t,
      srcHost: Pointer[CVoid, Read, Opaque],
      ByteCount: size_t
  ): CUresult = unsupported
  def cuMemcpyHtoDAsync_v2(
      dstDevice: CUdeviceptr,
      srcHost: Pointer[CVoid, Read, Opaque],
      ByteCount: size_t,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemcpyHtoD_v2(dstDevice: CUdeviceptr, srcHost: Pointer[CVoid, Read, Opaque], ByteCount: size_t): CUresult =
    unsupported
  def cuMemcpyPeer(
      dstDevice: CUdeviceptr,
      dstContext: CUcontext,
      srcDevice: CUdeviceptr,
      srcContext: CUcontext,
      ByteCount: size_t
  ): CUresult = unsupported
  def cuMemcpyPeerAsync(
      dstDevice: CUdeviceptr,
      dstContext: CUcontext,
      srcDevice: CUdeviceptr,
      srcContext: CUcontext,
      ByteCount: size_t,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemcpyWithAttributesAsync(
      dst: CUdeviceptr,
      src: CUdeviceptr,
      size: size_t,
      attr: Pointer[CUmemcpyAttributes, ReadWrite, Opaque],
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemsetD16Async(dstDevice: CUdeviceptr, us: unsigned_short, N: size_t, hStream: CUstream): CUresult = unsupported
  def cuMemsetD16_v2(dstDevice: CUdeviceptr, us: unsigned_short, N: size_t): CUresult = unsupported
  def cuMemsetD2D16Async(
      dstDevice: CUdeviceptr,
      dstPitch: size_t,
      us: unsigned_short,
      Width: size_t,
      Height: size_t,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemsetD2D16_v2(
      dstDevice: CUdeviceptr,
      dstPitch: size_t,
      us: unsigned_short,
      Width: size_t,
      Height: size_t
  ): CUresult = unsupported
  def cuMemsetD2D32Async(
      dstDevice: CUdeviceptr,
      dstPitch: size_t,
      ui: unsigned_int,
      Width: size_t,
      Height: size_t,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemsetD2D32_v2(
      dstDevice: CUdeviceptr,
      dstPitch: size_t,
      ui: unsigned_int,
      Width: size_t,
      Height: size_t
  ): CUresult = unsupported
  def cuMemsetD2D8Async(
      dstDevice: CUdeviceptr,
      dstPitch: size_t,
      uc: unsigned_char,
      Width: size_t,
      Height: size_t,
      hStream: CUstream
  ): CUresult = unsupported
  def cuMemsetD2D8_v2(
      dstDevice: CUdeviceptr,
      dstPitch: size_t,
      uc: unsigned_char,
      Width: size_t,
      Height: size_t
  ): CUresult = unsupported
  def cuMemsetD32Async(dstDevice: CUdeviceptr, ui: unsigned_int, N: size_t, hStream: CUstream): CUresult = unsupported
  def cuMemsetD32_v2(dstDevice: CUdeviceptr, ui: unsigned_int, N: size_t): CUresult = unsupported
  def cuMemsetD8Async(dstDevice: CUdeviceptr, uc: unsigned_char, N: size_t, hStream: CUstream): CUresult = unsupported
  def cuMemsetD8_v2(dstDevice: CUdeviceptr, uc: unsigned_char, N: size_t): CUresult = unsupported
  def cuMipmappedArrayCreate(
      pHandle: Pointer[CUmipmappedArray, ReadWrite, Opaque],
      pMipmappedArrayDesc: Pointer[CUDA_ARRAY3D_DESCRIPTOR, Read, Opaque],
      numMipmapLevels: unsigned_int
  ): CUresult = unsupported
  def cuMipmappedArrayDestroy(hMipmappedArray: CUmipmappedArray): CUresult = unsupported
  def cuMipmappedArrayGetLevel(
      pLevelArray: Pointer[CUarray, ReadWrite, Opaque],
      hMipmappedArray: CUmipmappedArray,
      level: unsigned_int
  ): CUresult = unsupported
  def cuMipmappedArrayGetMemoryRequirements(
      memoryRequirements: Pointer[CUDA_ARRAY_MEMORY_REQUIREMENTS, ReadWrite, Opaque],
      mipmap: CUmipmappedArray,
      device: CUdevice
  ): CUresult = unsupported
  def cuMipmappedArrayGetSparseProperties(
      sparseProperties: Pointer[CUDA_ARRAY_SPARSE_PROPERTIES, ReadWrite, Opaque],
      mipmap: CUmipmappedArray
  ): CUresult = unsupported
  def cuModuleEnumerateFunctions(
      functions: Pointer[CUfunction, ReadWrite, Opaque],
      numFunctions: unsigned_int,
      mod: CUmodule
  ): CUresult = unsupported
  def cuModuleGetFunction(hfunc: Pointer[CUfunction, ReadWrite, Opaque], hmod: CUmodule, name: CString): CUresult =
    unsupported
  def cuModuleGetFunctionCount(count: Pointer[unsigned_int, ReadWrite, Opaque], mod: CUmodule): CUresult = unsupported
  def cuModuleGetGlobal_v2(
      dptr: Pointer[CUdeviceptr, ReadWrite, Opaque],
      bytes: Pointer[size_t, ReadWrite, Opaque],
      hmod: CUmodule,
      name: CString
  ): CUresult = unsupported
  def cuModuleGetLoadingMode(mode: Pointer[CUmoduleLoadingMode, ReadWrite, Opaque]): CUresult = unsupported
  def cuModuleGetSurfRef(pSurfRef: Pointer[CUsurfref, ReadWrite, Opaque], hmod: CUmodule, name: CString): CUresult =
    unsupported
  def cuModuleGetTexRef(pTexRef: Pointer[CUtexref, ReadWrite, Opaque], hmod: CUmodule, name: CString): CUresult =
    unsupported
  def cuModuleLoad(module: Pointer[CUmodule, ReadWrite, Opaque], fname: CString): CUresult = unsupported
  def cuModuleLoadData(module: Pointer[CUmodule, ReadWrite, Opaque], image: Pointer[CVoid, Read, Opaque]): CUresult =
    unsupported
  def cuModuleLoadDataEx(
      module: Pointer[CUmodule, ReadWrite, Opaque],
      image: Pointer[CVoid, Read, Opaque],
      numOptions: unsigned_int,
      options: Pointer[CUjit_option, ReadWrite, Opaque],
      optionValues: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuModuleLoadFatBinary(
      module: Pointer[CUmodule, ReadWrite, Opaque],
      fatCubin: Pointer[CVoid, Read, Opaque]
  ): CUresult = unsupported
  def cuModuleUnload(hmod: CUmodule): CUresult = unsupported
  def cuMulticastAddDevice(mcHandle: CUmemGenericAllocationHandle, dev: CUdevice): CUresult = unsupported
  def cuMulticastBindAddr(
      mcHandle: CUmemGenericAllocationHandle,
      mcOffset: size_t,
      memptr: CUdeviceptr,
      size: size_t,
      flags: unsigned_long_long
  ): CUresult = unsupported
  def cuMulticastBindAddr_v2(
      mcHandle: CUmemGenericAllocationHandle,
      dev: CUdevice,
      mcOffset: size_t,
      memptr: CUdeviceptr,
      size: size_t,
      flags: unsigned_long_long
  ): CUresult = unsupported
  def cuMulticastBindMem(
      mcHandle: CUmemGenericAllocationHandle,
      mcOffset: size_t,
      memHandle: CUmemGenericAllocationHandle,
      memOffset: size_t,
      size: size_t,
      flags: unsigned_long_long
  ): CUresult = unsupported
  def cuMulticastBindMem_v2(
      mcHandle: CUmemGenericAllocationHandle,
      dev: CUdevice,
      mcOffset: size_t,
      memHandle: CUmemGenericAllocationHandle,
      memOffset: size_t,
      size: size_t,
      flags: unsigned_long_long
  ): CUresult = unsupported
  def cuMulticastCreate(
      mcHandle: Pointer[CUmemGenericAllocationHandle, ReadWrite, Opaque],
      prop: Pointer[CUmulticastObjectProp, Read, Opaque]
  ): CUresult = unsupported
  def cuMulticastGetGranularity(
      granularity: Pointer[size_t, ReadWrite, Opaque],
      prop: Pointer[CUmulticastObjectProp, Read, Opaque],
      option: CUmulticastGranularity_flags
  ): CUresult = unsupported
  def cuMulticastUnbind(
      mcHandle: CUmemGenericAllocationHandle,
      dev: CUdevice,
      mcOffset: size_t,
      size: size_t
  ): CUresult = unsupported
  def cuOccupancyAvailableDynamicSMemPerBlock(
      dynamicSmemSize: Pointer[size_t, ReadWrite, Opaque],
      func: CUfunction,
      numBlocks: Int,
      blockSize: Int
  ): CUresult = unsupported
  def cuOccupancyMaxActiveBlocksPerMultiprocessor(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      func: CUfunction,
      blockSize: Int,
      dynamicSMemSize: size_t
  ): CUresult = unsupported
  def cuOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      func: CUfunction,
      blockSize: Int,
      dynamicSMemSize: size_t,
      flags: unsigned_int
  ): CUresult = unsupported
  def cuOccupancyMaxActiveClusters(
      numClusters: Pointer[Int, ReadWrite, Opaque],
      func: CUfunction,
      config: Pointer[CUlaunchConfig, Read, Opaque]
  ): CUresult = unsupported
  def cuOccupancyMaxPotentialBlockSize(
      minGridSize: Pointer[Int, ReadWrite, Opaque],
      blockSize: Pointer[Int, ReadWrite, Opaque],
      func: CUfunction,
      blockSizeToDynamicSMemSize: CUoccupancyB2DSize,
      dynamicSMemSize: size_t,
      blockSizeLimit: Int
  ): CUresult = unsupported
  def cuOccupancyMaxPotentialBlockSizeWithFlags(
      minGridSize: Pointer[Int, ReadWrite, Opaque],
      blockSize: Pointer[Int, ReadWrite, Opaque],
      func: CUfunction,
      blockSizeToDynamicSMemSize: CUoccupancyB2DSize,
      dynamicSMemSize: size_t,
      blockSizeLimit: Int,
      flags: unsigned_int
  ): CUresult = unsupported
  def cuOccupancyMaxPotentialClusterSize(
      clusterSize: Pointer[Int, ReadWrite, Opaque],
      func: CUfunction,
      config: Pointer[CUlaunchConfig, Read, Opaque]
  ): CUresult = unsupported
  def cuParamSetSize(hfunc: CUfunction, numbytes: unsigned_int): CUresult = unsupported
  def cuParamSetTexRef(hfunc: CUfunction, texunit: Int, hTexRef: CUtexref): CUresult = unsupported
  def cuParamSetf(hfunc: CUfunction, offset: Int, value: Float): CUresult = unsupported
  def cuParamSeti(hfunc: CUfunction, offset: Int, value: unsigned_int): CUresult = unsupported
  def cuParamSetv(hfunc: CUfunction, offset: Int, ptr: OpaquePointer, numbytes: unsigned_int): CUresult = unsupported
  def cuPointerGetAttribute(data: OpaquePointer, attribute: CUpointer_attribute, ptr: CUdeviceptr): CUresult =
    unsupported
  def cuPointerGetAttributes(
      numAttributes: unsigned_int,
      attributes: Pointer[CUpointer_attribute, ReadWrite, Opaque],
      data: Pointer[OpaquePointer, ReadWrite, Opaque],
      ptr: CUdeviceptr
  ): CUresult = unsupported
  def cuPointerSetAttribute(
      value: Pointer[CVoid, Read, Opaque],
      attribute: CUpointer_attribute,
      ptr: CUdeviceptr
  ): CUresult = unsupported
  def cuSignalExternalSemaphoresAsync(
      extSemArray: Pointer[CUexternalSemaphore, Read, Opaque],
      paramsArray: Pointer[CUDA_EXTERNAL_SEMAPHORE_SIGNAL_PARAMS, Read, Opaque],
      numExtSems: unsigned_int,
      stream: CUstream
  ): CUresult = unsupported
  def cuStreamAddCallback(
      hStream: CUstream,
      callback: CUstreamCallback,
      userData: OpaquePointer,
      flags: unsigned_int
  ): CUresult = unsupported
  def cuStreamAttachMemAsync(hStream: CUstream, dptr: CUdeviceptr, length: size_t, flags: unsigned_int): CUresult =
    unsupported
  def cuStreamBatchMemOp_v2(
      stream: CUstream,
      count: unsigned_int,
      paramArray: Pointer[CUstreamBatchMemOpParams, ReadWrite, Opaque],
      flags: unsigned_int
  ): CUresult = unsupported
  def cuStreamBeginCaptureToCig(
      hStream: CUstream,
      streamCigCaptureParams: Pointer[CUstreamCigCaptureParams, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuStreamBeginCaptureToGraph(
      hStream: CUstream,
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      dependencyData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: size_t,
      mode: CUstreamCaptureMode
  ): CUresult = unsupported
  def cuStreamBeginCapture_v2(hStream: CUstream, mode: CUstreamCaptureMode): CUresult = unsupported
  def cuStreamBeginRecaptureToGraph(
      hStream: CUstream,
      mode: CUstreamCaptureMode,
      hGraph: CUgraph,
      callbackFunc: CUgraphRecaptureCallback,
      userData: OpaquePointer
  ): CUresult = unsupported
  def cuStreamCopyAttributes(dst: CUstream, src: CUstream): CUresult = unsupported
  def cuStreamCreate(phStream: Pointer[CUstream, ReadWrite, Opaque], Flags: unsigned_int): CUresult = unsupported
  def cuStreamCreateWithPriority(
      phStream: Pointer[CUstream, ReadWrite, Opaque],
      flags: unsigned_int,
      priority: Int
  ): CUresult = unsupported
  def cuStreamDestroy_v2(hStream: CUstream): CUresult = unsupported
  def cuStreamEndCapture(hStream: CUstream, phGraph: Pointer[CUgraph, ReadWrite, Opaque]): CUresult = unsupported
  def cuStreamEndCaptureToCig(hStream: CUstream): CUresult = unsupported
  def cuStreamGetAttribute(
      hStream: CUstream,
      attr: CUstreamAttrID,
      value_out: Pointer[CUstreamAttrValue, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuStreamGetCaptureInfo_v3(
      hStream: CUstream,
      captureStatus_out: Pointer[CUstreamCaptureStatus, ReadWrite, Opaque],
      id_out: Pointer[cuuint64_t, ReadWrite, Opaque],
      graph_out: Pointer[CUgraph, ReadWrite, Opaque],
      dependencies_out: Pointer[Pointer[CUgraphNode, Read, Opaque], ReadWrite, Opaque],
      edgeData_out: Pointer[Pointer[CUgraphEdgeData, Read, Opaque], ReadWrite, Opaque],
      numDependencies_out: Pointer[size_t, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuStreamGetCtx(hStream: CUstream, pctx: Pointer[CUcontext, ReadWrite, Opaque]): CUresult = unsupported
  def cuStreamGetCtx_v2(
      hStream: CUstream,
      pCtx: Pointer[CUcontext, ReadWrite, Opaque],
      pGreenCtx: Pointer[CUgreenCtx, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuStreamGetDevResource(
      hStream: CUstream,
      resource: Pointer[CUdevResource, ReadWrite, Opaque],
      `type`: CUdevResourceType
  ): CUresult = unsupported
  def cuStreamGetDevice(hStream: CUstream, device: Pointer[CUdevice, ReadWrite, Opaque]): CUresult = unsupported
  def cuStreamGetFlags(hStream: CUstream, flags: Pointer[unsigned_int, ReadWrite, Opaque]): CUresult = unsupported
  def cuStreamGetGreenCtx(hStream: CUstream, phCtx: Pointer[CUgreenCtx, ReadWrite, Opaque]): CUresult = unsupported
  def cuStreamGetId(hStream: CUstream, streamId: Pointer[unsigned_long_long, ReadWrite, Opaque]): CUresult = unsupported
  def cuStreamGetPriority(hStream: CUstream, priority: Pointer[Int, ReadWrite, Opaque]): CUresult = unsupported
  def cuStreamIsCapturing(
      hStream: CUstream,
      captureStatus: Pointer[CUstreamCaptureStatus, ReadWrite, Opaque]
  ): CUresult = unsupported
  def cuStreamQuery(hStream: CUstream): CUresult = unsupported
  def cuStreamSetAttribute(
      hStream: CUstream,
      attr: CUstreamAttrID,
      value: Pointer[CUstreamAttrValue, Read, Opaque]
  ): CUresult = unsupported
  def cuStreamSynchronize(hStream: CUstream): CUresult = unsupported
  def cuStreamUpdateCaptureDependencies_v2(
      hStream: CUstream,
      dependencies: Pointer[CUgraphNode, ReadWrite, Opaque],
      dependencyData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: size_t,
      flags: unsigned_int
  ): CUresult = unsupported
  def cuStreamWaitEvent(hStream: CUstream, hEvent: CUevent, Flags: unsigned_int): CUresult = unsupported
  def cuStreamWaitValue32_v2(stream: CUstream, addr: CUdeviceptr, value: cuuint32_t, flags: unsigned_int): CUresult =
    unsupported
  def cuStreamWaitValue64_v2(stream: CUstream, addr: CUdeviceptr, value: cuuint64_t, flags: unsigned_int): CUresult =
    unsupported
  def cuStreamWriteValue32_v2(stream: CUstream, addr: CUdeviceptr, value: cuuint32_t, flags: unsigned_int): CUresult =
    unsupported
  def cuStreamWriteValue64_v2(stream: CUstream, addr: CUdeviceptr, value: cuuint64_t, flags: unsigned_int): CUresult =
    unsupported
  def cuSurfObjectCreate(
      pSurfObject: Pointer[CUsurfObject, ReadWrite, Opaque],
      pResDesc: Pointer[CUDA_RESOURCE_DESC, Read, Opaque]
  ): CUresult = unsupported
  def cuSurfObjectDestroy(surfObject: CUsurfObject): CUresult = unsupported
  def cuSurfObjectGetResourceDesc(
      pResDesc: Pointer[CUDA_RESOURCE_DESC, ReadWrite, Opaque],
      surfObject: CUsurfObject
  ): CUresult = unsupported
  def cuSurfRefGetArray(phArray: Pointer[CUarray, ReadWrite, Opaque], hSurfRef: CUsurfref): CUresult = unsupported
  def cuSurfRefSetArray(hSurfRef: CUsurfref, hArray: CUarray, Flags: unsigned_int): CUresult = unsupported
  def cuTensorMapEncodeIm2col(
      tensorMap: Pointer[CUtensorMap, ReadWrite, Opaque],
      tensorDataType: CUtensorMapDataType,
      tensorRank: cuuint32_t,
      globalAddress: OpaquePointer,
      globalDim: Pointer[cuuint64_t, Read, Opaque],
      globalStrides: Pointer[cuuint64_t, Read, Opaque],
      pixelBoxLowerCorner: Pointer[Int, Read, Opaque],
      pixelBoxUpperCorner: Pointer[Int, Read, Opaque],
      channelsPerPixel: cuuint32_t,
      pixelsPerColumn: cuuint32_t,
      elementStrides: Pointer[cuuint32_t, Read, Opaque],
      interleave: CUtensorMapInterleave,
      swizzle: CUtensorMapSwizzle,
      l2Promotion: CUtensorMapL2promotion,
      oobFill: CUtensorMapFloatOOBfill
  ): CUresult = unsupported
  def cuTensorMapEncodeIm2colWide(
      tensorMap: Pointer[CUtensorMap, ReadWrite, Opaque],
      tensorDataType: CUtensorMapDataType,
      tensorRank: cuuint32_t,
      globalAddress: OpaquePointer,
      globalDim: Pointer[cuuint64_t, Read, Opaque],
      globalStrides: Pointer[cuuint64_t, Read, Opaque],
      pixelBoxLowerCornerWidth: Int,
      pixelBoxUpperCornerWidth: Int,
      channelsPerPixel: cuuint32_t,
      pixelsPerColumn: cuuint32_t,
      elementStrides: Pointer[cuuint32_t, Read, Opaque],
      interleave: CUtensorMapInterleave,
      mode: CUtensorMapIm2ColWideMode,
      swizzle: CUtensorMapSwizzle,
      l2Promotion: CUtensorMapL2promotion,
      oobFill: CUtensorMapFloatOOBfill
  ): CUresult = unsupported
  def cuTensorMapEncodeTiled(
      tensorMap: Pointer[CUtensorMap, ReadWrite, Opaque],
      tensorDataType: CUtensorMapDataType,
      tensorRank: cuuint32_t,
      globalAddress: OpaquePointer,
      globalDim: Pointer[cuuint64_t, Read, Opaque],
      globalStrides: Pointer[cuuint64_t, Read, Opaque],
      boxDim: Pointer[cuuint32_t, Read, Opaque],
      elementStrides: Pointer[cuuint32_t, Read, Opaque],
      interleave: CUtensorMapInterleave,
      swizzle: CUtensorMapSwizzle,
      l2Promotion: CUtensorMapL2promotion,
      oobFill: CUtensorMapFloatOOBfill
  ): CUresult = unsupported
  def cuTensorMapReplaceAddress(
      tensorMap: Pointer[CUtensorMap, ReadWrite, Opaque],
      globalAddress: OpaquePointer
  ): CUresult = unsupported
  def cuTexObjectCreate(
      pTexObject: Pointer[CUtexObject, ReadWrite, Opaque],
      pResDesc: Pointer[CUDA_RESOURCE_DESC, Read, Opaque],
      pTexDesc: Pointer[CUDA_TEXTURE_DESC, Read, Opaque],
      pResViewDesc: Pointer[CUDA_RESOURCE_VIEW_DESC, Read, Opaque]
  ): CUresult = unsupported
  def cuTexObjectDestroy(texObject: CUtexObject): CUresult = unsupported
  def cuTexObjectGetResourceDesc(
      pResDesc: Pointer[CUDA_RESOURCE_DESC, ReadWrite, Opaque],
      texObject: CUtexObject
  ): CUresult = unsupported
  def cuTexObjectGetResourceViewDesc(
      pResViewDesc: Pointer[CUDA_RESOURCE_VIEW_DESC, ReadWrite, Opaque],
      texObject: CUtexObject
  ): CUresult = unsupported
  def cuTexObjectGetTextureDesc(
      pTexDesc: Pointer[CUDA_TEXTURE_DESC, ReadWrite, Opaque],
      texObject: CUtexObject
  ): CUresult = unsupported
  def cuTexRefCreate(pTexRef: Pointer[CUtexref, ReadWrite, Opaque]): CUresult = unsupported
  def cuTexRefDestroy(hTexRef: CUtexref): CUresult = unsupported
  def cuTexRefGetAddressMode(pam: Pointer[CUaddress_mode, ReadWrite, Opaque], hTexRef: CUtexref, dim: Int): CUresult =
    unsupported
  def cuTexRefGetAddress_v2(pdptr: Pointer[CUdeviceptr, ReadWrite, Opaque], hTexRef: CUtexref): CUresult = unsupported
  def cuTexRefGetArray(phArray: Pointer[CUarray, ReadWrite, Opaque], hTexRef: CUtexref): CUresult = unsupported
  def cuTexRefGetBorderColor(pBorderColor: Pointer[Float, ReadWrite, Opaque], hTexRef: CUtexref): CUresult = unsupported
  def cuTexRefGetFilterMode(pfm: Pointer[CUfilter_mode, ReadWrite, Opaque], hTexRef: CUtexref): CUresult = unsupported
  def cuTexRefGetFlags(pFlags: Pointer[unsigned_int, ReadWrite, Opaque], hTexRef: CUtexref): CUresult = unsupported
  def cuTexRefGetFormat(
      pFormat: Pointer[CUarray_format, ReadWrite, Opaque],
      pNumChannels: Pointer[Int, ReadWrite, Opaque],
      hTexRef: CUtexref
  ): CUresult = unsupported
  def cuTexRefGetMaxAnisotropy(pmaxAniso: Pointer[Int, ReadWrite, Opaque], hTexRef: CUtexref): CUresult = unsupported
  def cuTexRefGetMipmapFilterMode(pfm: Pointer[CUfilter_mode, ReadWrite, Opaque], hTexRef: CUtexref): CUresult =
    unsupported
  def cuTexRefGetMipmapLevelBias(pbias: Pointer[Float, ReadWrite, Opaque], hTexRef: CUtexref): CUresult = unsupported
  def cuTexRefGetMipmapLevelClamp(
      pminMipmapLevelClamp: Pointer[Float, ReadWrite, Opaque],
      pmaxMipmapLevelClamp: Pointer[Float, ReadWrite, Opaque],
      hTexRef: CUtexref
  ): CUresult = unsupported
  def cuTexRefGetMipmappedArray(
      phMipmappedArray: Pointer[CUmipmappedArray, ReadWrite, Opaque],
      hTexRef: CUtexref
  ): CUresult = unsupported
  def cuTexRefSetAddress2D_v3(
      hTexRef: CUtexref,
      desc: Pointer[CUDA_ARRAY_DESCRIPTOR, Read, Opaque],
      dptr: CUdeviceptr,
      Pitch: size_t
  ): CUresult = unsupported
  def cuTexRefSetAddressMode(hTexRef: CUtexref, dim: Int, am: CUaddress_mode): CUresult = unsupported
  def cuTexRefSetAddress_v2(
      ByteOffset: Pointer[size_t, ReadWrite, Opaque],
      hTexRef: CUtexref,
      dptr: CUdeviceptr,
      bytes: size_t
  ): CUresult = unsupported
  def cuTexRefSetArray(hTexRef: CUtexref, hArray: CUarray, Flags: unsigned_int): CUresult = unsupported
  def cuTexRefSetBorderColor(hTexRef: CUtexref, pBorderColor: Pointer[Float, ReadWrite, Opaque]): CUresult = unsupported
  def cuTexRefSetFilterMode(hTexRef: CUtexref, fm: CUfilter_mode): CUresult = unsupported
  def cuTexRefSetFlags(hTexRef: CUtexref, Flags: unsigned_int): CUresult = unsupported
  def cuTexRefSetFormat(hTexRef: CUtexref, fmt: CUarray_format, NumPackedComponents: Int): CUresult = unsupported
  def cuTexRefSetMaxAnisotropy(hTexRef: CUtexref, maxAniso: unsigned_int): CUresult = unsupported
  def cuTexRefSetMipmapFilterMode(hTexRef: CUtexref, fm: CUfilter_mode): CUresult = unsupported
  def cuTexRefSetMipmapLevelBias(hTexRef: CUtexref, bias: Float): CUresult = unsupported
  def cuTexRefSetMipmapLevelClamp(hTexRef: CUtexref, minMipmapLevelClamp: Float, maxMipmapLevelClamp: Float): CUresult =
    unsupported
  def cuTexRefSetMipmappedArray(hTexRef: CUtexref, hMipmappedArray: CUmipmappedArray, Flags: unsigned_int): CUresult =
    unsupported
  def cuThreadExchangeStreamCaptureMode(mode: Pointer[CUstreamCaptureMode, ReadWrite, Opaque]): CUresult = unsupported
  def cuUserObjectCreate(
      object_out: Pointer[CUuserObject, ReadWrite, Opaque],
      ptr: OpaquePointer,
      destroy: CUhostFn,
      initialRefcount: unsigned_int,
      flags: unsigned_int
  ): CUresult = unsupported
  def cuUserObjectRelease(`object`: CUuserObject, count: unsigned_int): CUresult = unsupported
  def cuUserObjectRetain(`object`: CUuserObject, count: unsigned_int): CUresult = unsupported
  def cuWaitExternalSemaphoresAsync(
      extSemArray: Pointer[CUexternalSemaphore, Read, Opaque],
      paramsArray: Pointer[CUDA_EXTERNAL_SEMAPHORE_WAIT_PARAMS, Read, Opaque],
      numExtSems: unsigned_int,
      stream: CUstream
  ): CUresult = unsupported
  def cudaArrayGetInfo(
      desc: Pointer[struct_cudaChannelFormatDesc, ReadWrite, Opaque],
      extent: Pointer[struct_cudaExtent, ReadWrite, Opaque],
      flags: Pointer[unsigned_int, ReadWrite, Opaque],
      array: cudaArray_t
  ): cudaError_t = unsupported
  def cudaArrayGetMemoryRequirements(
      memoryRequirements: Pointer[struct_cudaArrayMemoryRequirements, ReadWrite, Opaque],
      array: cudaArray_t,
      device: Int
  ): cudaError_t = unsupported
  def cudaArrayGetPlane(
      pPlaneArray: Pointer[cudaArray_t, ReadWrite, Opaque],
      hArray: cudaArray_t,
      planeIdx: unsigned_int
  ): cudaError_t = unsupported
  def cudaArrayGetSparseProperties(
      sparseProperties: Pointer[struct_cudaArraySparseProperties, ReadWrite, Opaque],
      array: cudaArray_t
  ): cudaError_t = unsupported
  def cudaChooseDevice(
      device: Pointer[Int, ReadWrite, Opaque],
      prop: Pointer[struct_cudaDeviceProp, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaCreateChannelDesc(
      x: Int,
      y: Int,
      z: Int,
      w: Int,
      f: enum_cudaChannelFormatKind
  ): struct_cudaChannelFormatDesc = unsupported
  def cudaCreateSurfaceObject(
      pSurfObject: Pointer[cudaSurfaceObject_t, ReadWrite, Opaque],
      pResDesc: Pointer[struct_cudaResourceDesc, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaCreateTextureObject(
      pTexObject: Pointer[cudaTextureObject_t, ReadWrite, Opaque],
      pResDesc: Pointer[struct_cudaResourceDesc, Read, Opaque],
      pTexDesc: Pointer[struct_cudaTextureDesc, Read, Opaque],
      pResViewDesc: Pointer[struct_cudaResourceViewDesc, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaCtxResetPersistingL2Cache(): cudaError_t = unsupported
  def cudaDestroyExternalMemory(extMem: cudaExternalMemory_t): cudaError_t = unsupported
  def cudaDestroyExternalSemaphore(extSem: cudaExternalSemaphore_t): cudaError_t = unsupported
  def cudaDestroySurfaceObject(surfObject: cudaSurfaceObject_t): cudaError_t = unsupported
  def cudaDestroyTextureObject(texObject: cudaTextureObject_t): cudaError_t = unsupported
  def cudaDevResourceGenerateDesc(
      phDesc: Pointer[cudaDevResourceDesc_t, ReadWrite, Opaque],
      resources: Pointer[cudaDevResource, ReadWrite, Opaque],
      nbResources: unsigned_int
  ): cudaError_t = unsupported
  def cudaDevSmResourceSplit(
      result: Pointer[cudaDevResource, ReadWrite, Opaque],
      nbGroups: unsigned_int,
      input: Pointer[cudaDevResource, Read, Opaque],
      remainder: Pointer[cudaDevResource, ReadWrite, Opaque],
      flags: unsigned_int,
      groupParams: Pointer[cudaDevSmResourceGroupParams, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaDevSmResourceSplitByCount(
      result: Pointer[cudaDevResource, ReadWrite, Opaque],
      nbGroups: Pointer[unsigned_int, ReadWrite, Opaque],
      input: Pointer[cudaDevResource, Read, Opaque],
      remaining: Pointer[cudaDevResource, ReadWrite, Opaque],
      flags: unsigned_int,
      minCount: unsigned_int
  ): cudaError_t = unsupported
  def cudaDeviceCanAccessPeer(
      canAccessPeer: Pointer[Int, ReadWrite, Opaque],
      device: Int,
      peerDevice: Int
  ): cudaError_t = unsupported
  def cudaDeviceDisablePeerAccess(peerDevice: Int): cudaError_t = unsupported
  def cudaDeviceEnablePeerAccess(peerDevice: Int, flags: unsigned_int): cudaError_t = unsupported
  def cudaDeviceFlushGPUDirectRDMAWrites(
      target: enum_cudaFlushGPUDirectRDMAWritesTarget,
      scope: enum_cudaFlushGPUDirectRDMAWritesScope
  ): cudaError_t = unsupported
  def cudaDeviceGetAttribute(
      value: Pointer[Int, ReadWrite, Opaque],
      attr: enum_cudaDeviceAttr,
      device: Int
  ): cudaError_t = unsupported
  def cudaDeviceGetByPCIBusId(device: Pointer[Int, ReadWrite, Opaque], pciBusId: CString): cudaError_t = unsupported
  def cudaDeviceGetCacheConfig(pCacheConfig: Pointer[enum_cudaFuncCache, ReadWrite, Opaque]): cudaError_t = unsupported
  def cudaDeviceGetDefaultMemPool(memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque], device: Int): cudaError_t =
    unsupported
  def cudaDeviceGetDevResource(
      device: Int,
      resource: Pointer[cudaDevResource, ReadWrite, Opaque],
      `type`: enum_cudaDevResourceType
  ): cudaError_t = unsupported
  def cudaDeviceGetExecutionCtx(ctx: Pointer[cudaExecutionContext_t, ReadWrite, Opaque], device: Int): cudaError_t =
    unsupported
  def cudaDeviceGetGraphMemAttribute(
      device: Int,
      attr: enum_cudaGraphMemAttributeType,
      value: OpaquePointer
  ): cudaError_t = unsupported
  def cudaDeviceGetHostAtomicCapabilities(
      capabilities: Pointer[unsigned_int, ReadWrite, Opaque],
      operations: Pointer[enum_cudaAtomicOperation, Read, Opaque],
      count: unsigned_int,
      device: Int
  ): cudaError_t = unsupported
  def cudaDeviceGetLimit(pValue: Pointer[size_t, ReadWrite, Opaque], limit: enum_cudaLimit): cudaError_t = unsupported
  def cudaDeviceGetMemPool(memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque], device: Int): cudaError_t = unsupported
  def cudaDeviceGetNvSciSyncAttributes(nvSciSyncAttrList: OpaquePointer, device: Int, flags: Int): cudaError_t =
    unsupported
  def cudaDeviceGetP2PAtomicCapabilities(
      capabilities: Pointer[unsigned_int, ReadWrite, Opaque],
      operations: Pointer[enum_cudaAtomicOperation, Read, Opaque],
      count: unsigned_int,
      srcDevice: Int,
      dstDevice: Int
  ): cudaError_t = unsupported
  def cudaDeviceGetP2PAttribute(
      value: Pointer[Int, ReadWrite, Opaque],
      attr: enum_cudaDeviceP2PAttr,
      srcDevice: Int,
      dstDevice: Int
  ): cudaError_t = unsupported
  def cudaDeviceGetPCIBusId(pciBusId: Pointer[Byte, ReadWrite, Opaque], len: Int, device: Int): cudaError_t =
    unsupported
  def cudaDeviceGetSharedMemConfig(pConfig: Pointer[enum_cudaSharedMemConfig, ReadWrite, Opaque]): cudaError_t =
    unsupported
  def cudaDeviceGetStreamPriorityRange(
      leastPriority: Pointer[Int, ReadWrite, Opaque],
      greatestPriority: Pointer[Int, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaDeviceGetTexture1DLinearMaxWidth(
      maxWidthInElements: Pointer[size_t, ReadWrite, Opaque],
      fmtDesc: Pointer[struct_cudaChannelFormatDesc, Read, Opaque],
      device: Int
  ): cudaError_t = unsupported
  def cudaDeviceGraphMemTrim(device: Int): cudaError_t = unsupported
  def cudaDeviceRegisterAsyncNotification(
      device: Int,
      callbackFunc: cudaAsyncCallback,
      userData: OpaquePointer,
      callback: Pointer[cudaAsyncCallbackHandle_t, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaDeviceReset(): cudaError_t = unsupported
  def cudaDeviceSetCacheConfig(cacheConfig: enum_cudaFuncCache): cudaError_t = unsupported
  def cudaDeviceSetGraphMemAttribute(
      device: Int,
      attr: enum_cudaGraphMemAttributeType,
      value: OpaquePointer
  ): cudaError_t = unsupported
  def cudaDeviceSetLimit(limit: enum_cudaLimit, value: size_t): cudaError_t = unsupported
  def cudaDeviceSetMemPool(device: Int, memPool: cudaMemPool_t): cudaError_t = unsupported
  def cudaDeviceSetSharedMemConfig(config: enum_cudaSharedMemConfig): cudaError_t = unsupported
  def cudaDeviceSynchronize(): cudaError_t = unsupported
  def cudaDeviceUnregisterAsyncNotification(device: Int, callback: cudaAsyncCallbackHandle_t): cudaError_t = unsupported
  def cudaDriverGetVersion(driverVersion: Pointer[Int, ReadWrite, Opaque]): cudaError_t = unsupported
  def cudaEventCreate(event: Pointer[cudaEvent_t, ReadWrite, Opaque]): cudaError_t = unsupported
  def cudaEventCreateWithFlags(event: Pointer[cudaEvent_t, ReadWrite, Opaque], flags: unsigned_int): cudaError_t =
    unsupported
  def cudaEventDestroy(event: cudaEvent_t): cudaError_t = unsupported
  def cudaEventElapsedTime(ms: Pointer[Float, ReadWrite, Opaque], start: cudaEvent_t, end: cudaEvent_t): cudaError_t =
    unsupported
  def cudaEventQuery(event: cudaEvent_t): cudaError_t = unsupported
  def cudaEventRecord(event: cudaEvent_t, stream: cudaStream_t): cudaError_t = unsupported
  def cudaEventRecordWithFlags(event: cudaEvent_t, stream: cudaStream_t, flags: unsigned_int): cudaError_t = unsupported
  def cudaEventSynchronize(event: cudaEvent_t): cudaError_t = unsupported
  def cudaExecutionCtxDestroy(ctx: cudaExecutionContext_t): cudaError_t = unsupported
  def cudaExecutionCtxGetDevResource(
      ctx: cudaExecutionContext_t,
      resource: Pointer[cudaDevResource, ReadWrite, Opaque],
      `type`: enum_cudaDevResourceType
  ): cudaError_t = unsupported
  def cudaExecutionCtxGetDevice(device: Pointer[Int, ReadWrite, Opaque], ctx: cudaExecutionContext_t): cudaError_t =
    unsupported
  def cudaExecutionCtxGetId(
      ctx: cudaExecutionContext_t,
      ctxId: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaExecutionCtxRecordEvent(ctx: cudaExecutionContext_t, event: cudaEvent_t): cudaError_t = unsupported
  def cudaExecutionCtxStreamCreate(
      phStream: Pointer[cudaStream_t, ReadWrite, Opaque],
      ctx: cudaExecutionContext_t,
      flags: unsigned_int,
      priority: Int
  ): cudaError_t = unsupported
  def cudaExecutionCtxSynchronize(ctx: cudaExecutionContext_t): cudaError_t = unsupported
  def cudaExecutionCtxWaitEvent(ctx: cudaExecutionContext_t, event: cudaEvent_t): cudaError_t = unsupported
  def cudaExternalMemoryGetMappedBuffer(
      devPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      extMem: cudaExternalMemory_t,
      bufferDesc: Pointer[struct_cudaExternalMemoryBufferDesc, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaExternalMemoryGetMappedMipmappedArray(
      mipmap: Pointer[cudaMipmappedArray_t, ReadWrite, Opaque],
      extMem: cudaExternalMemory_t,
      mipmapDesc: Pointer[struct_cudaExternalMemoryMipmappedArrayDesc, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaFree(devPtr: OpaquePointer): cudaError_t = unsupported
  def cudaFreeArray(array: cudaArray_t): cudaError_t = unsupported
  def cudaFreeAsync(devPtr: OpaquePointer, hStream: cudaStream_t): cudaError_t = unsupported
  def cudaFreeHost(ptr: OpaquePointer): cudaError_t = unsupported
  def cudaFreeMipmappedArray(mipmappedArray: cudaMipmappedArray_t): cudaError_t = unsupported
  def cudaFuncGetAttributes(
      attr: Pointer[struct_cudaFuncAttributes, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaFuncGetName(name: Pointer[CString, ReadWrite, Opaque], func: Pointer[CVoid, Read, Opaque]): cudaError_t =
    unsupported
  def cudaFuncGetParamCount(
      func: Pointer[CVoid, Read, Opaque],
      paramCount: Pointer[size_t, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaFuncGetParamInfo(
      func: Pointer[CVoid, Read, Opaque],
      paramIndex: size_t,
      paramOffset: Pointer[size_t, ReadWrite, Opaque],
      paramSize: Pointer[size_t, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaFuncSetAttribute(func: Pointer[CVoid, Read, Opaque], attr: enum_cudaFuncAttribute, value: Int): cudaError_t =
    unsupported
  def cudaFuncSetCacheConfig(func: Pointer[CVoid, Read, Opaque], cacheConfig: enum_cudaFuncCache): cudaError_t =
    unsupported
  def cudaFuncSetSharedMemConfig(func: Pointer[CVoid, Read, Opaque], config: enum_cudaSharedMemConfig): cudaError_t =
    unsupported
  def cudaGetChannelDesc(
      desc: Pointer[struct_cudaChannelFormatDesc, ReadWrite, Opaque],
      array: cudaArray_const_t
  ): cudaError_t = unsupported
  def cudaGetDevice(device: Pointer[Int, ReadWrite, Opaque]): cudaError_t = unsupported
  def cudaGetDeviceCount(count: Pointer[Int, ReadWrite, Opaque]): cudaError_t = unsupported
  def cudaGetDeviceFlags(flags: Pointer[unsigned_int, ReadWrite, Opaque]): cudaError_t = unsupported
  def cudaGetDeviceProperties(prop: Pointer[struct_cudaDeviceProp, ReadWrite, Opaque], device: Int): cudaError_t =
    unsupported
  def cudaGetDriverEntryPoint(
      symbol: CString,
      funcPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      flags: unsigned_long_long,
      driverStatus: Pointer[enum_cudaDriverEntryPointQueryResult, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGetDriverEntryPointByVersion(
      symbol: CString,
      funcPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      cudaVersion: unsigned_int,
      flags: unsigned_long_long,
      driverStatus: Pointer[enum_cudaDriverEntryPointQueryResult, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGetErrorName(error: cudaError_t): CString = unsupported
  def cudaGetErrorString(error: cudaError_t): CString = unsupported
  def cudaGetExportTable(
      ppExportTable: Pointer[Pointer[CVoid, Read, Opaque], ReadWrite, Opaque],
      pExportTableId: Pointer[cudaUUID_t, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGetFuncBySymbol(
      functionPtr: Pointer[cudaFunction_t, ReadWrite, Opaque],
      symbolPtr: Pointer[CVoid, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGetKernel(
      kernelPtr: Pointer[cudaKernel_t, ReadWrite, Opaque],
      entryFuncAddr: Pointer[CVoid, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGetLastError(): cudaError_t = unsupported
  def cudaGetMipmappedArrayLevel(
      levelArray: Pointer[cudaArray_t, ReadWrite, Opaque],
      mipmappedArray: cudaMipmappedArray_const_t,
      level: unsigned_int
  ): cudaError_t = unsupported
  def cudaGetSurfaceObjectResourceDesc(
      pResDesc: Pointer[struct_cudaResourceDesc, ReadWrite, Opaque],
      surfObject: cudaSurfaceObject_t
  ): cudaError_t = unsupported
  def cudaGetSymbolAddress(
      devPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      symbol: Pointer[CVoid, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGetSymbolSize(size: Pointer[size_t, ReadWrite, Opaque], symbol: Pointer[CVoid, Read, Opaque]): cudaError_t =
    unsupported
  def cudaGetTextureObjectResourceDesc(
      pResDesc: Pointer[struct_cudaResourceDesc, ReadWrite, Opaque],
      texObject: cudaTextureObject_t
  ): cudaError_t = unsupported
  def cudaGetTextureObjectResourceViewDesc(
      pResViewDesc: Pointer[struct_cudaResourceViewDesc, ReadWrite, Opaque],
      texObject: cudaTextureObject_t
  ): cudaError_t = unsupported
  def cudaGetTextureObjectTextureDesc(
      pTexDesc: Pointer[struct_cudaTextureDesc, ReadWrite, Opaque],
      texObject: cudaTextureObject_t
  ): cudaError_t = unsupported
  def cudaGraphAddChildGraphNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      childGraph: cudaGraph_t
  ): cudaError_t = unsupported
  def cudaGraphAddDependencies(
      graph: cudaGraph_t,
      from: Pointer[cudaGraphNode_t, Read, Opaque],
      to: Pointer[cudaGraphNode_t, Read, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: size_t
  ): cudaError_t = unsupported
  def cudaGraphAddEmptyNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t
  ): cudaError_t = unsupported
  def cudaGraphAddEventRecordNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      event: cudaEvent_t
  ): cudaError_t = unsupported
  def cudaGraphAddEventWaitNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      event: cudaEvent_t
  ): cudaError_t = unsupported
  def cudaGraphAddExternalSemaphoresSignalNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[struct_cudaExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphAddExternalSemaphoresWaitNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[struct_cudaExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphAddHostNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      pNodeParams: Pointer[struct_cudaHostNodeParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphAddKernelNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      pNodeParams: Pointer[struct_cudaKernelNodeParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphAddMemAllocNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[struct_cudaMemAllocNodeParams, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphAddMemFreeNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      dptr: OpaquePointer
  ): cudaError_t = unsupported
  def cudaGraphAddMemcpyNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      pCopyParams: Pointer[struct_cudaMemcpy3DParms, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphAddMemcpyNode1D(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      dst: OpaquePointer,
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaGraphAddMemcpyNodeFromSymbol(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      dst: OpaquePointer,
      symbol: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaGraphAddMemcpyNodeToSymbol(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaGraphAddMemsetNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      pMemsetParams: Pointer[struct_cudaMemsetParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphAddNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      dependencyData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[struct_cudaGraphNodeParams, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphChildGraphNodeGetGraph(
      node: cudaGraphNode_t,
      pGraph: Pointer[cudaGraph_t, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphClone(pGraphClone: Pointer[cudaGraph_t, ReadWrite, Opaque], originalGraph: cudaGraph_t): cudaError_t =
    unsupported
  def cudaGraphConditionalHandleCreate(
      pHandle_out: Pointer[cudaGraphConditionalHandle, ReadWrite, Opaque],
      graph: cudaGraph_t,
      defaultLaunchValue: unsigned_int,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaGraphConditionalHandleCreate_v2(
      pHandle_out: Pointer[cudaGraphConditionalHandle, ReadWrite, Opaque],
      graph: cudaGraph_t,
      ctx: cudaExecutionContext_t,
      defaultLaunchValue: unsigned_int,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaGraphCreate(pGraph: Pointer[cudaGraph_t, ReadWrite, Opaque], flags: unsigned_int): cudaError_t = unsupported
  def cudaGraphDebugDotPrint(graph: cudaGraph_t, path: CString, flags: unsigned_int): cudaError_t = unsupported
  def cudaGraphDestroy(graph: cudaGraph_t): cudaError_t = unsupported
  def cudaGraphDestroyNode(node: cudaGraphNode_t): cudaError_t = unsupported
  def cudaGraphEventRecordNodeGetEvent(
      node: cudaGraphNode_t,
      event_out: Pointer[cudaEvent_t, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphEventRecordNodeSetEvent(node: cudaGraphNode_t, event: cudaEvent_t): cudaError_t = unsupported
  def cudaGraphEventWaitNodeGetEvent(
      node: cudaGraphNode_t,
      event_out: Pointer[cudaEvent_t, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphEventWaitNodeSetEvent(node: cudaGraphNode_t, event: cudaEvent_t): cudaError_t = unsupported
  def cudaGraphExecChildGraphNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      childGraph: cudaGraph_t
  ): cudaError_t = unsupported
  def cudaGraphExecDestroy(graphExec: cudaGraphExec_t): cudaError_t = unsupported
  def cudaGraphExecEventRecordNodeSetEvent(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      event: cudaEvent_t
  ): cudaError_t = unsupported
  def cudaGraphExecEventWaitNodeSetEvent(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      event: cudaEvent_t
  ): cudaError_t = unsupported
  def cudaGraphExecExternalSemaphoresSignalNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      nodeParams: Pointer[struct_cudaExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphExecExternalSemaphoresWaitNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      nodeParams: Pointer[struct_cudaExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphExecGetFlags(
      graphExec: cudaGraphExec_t,
      flags: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphExecGetId(hGraphExec: cudaGraphExec_t, graphID: Pointer[unsigned_int, ReadWrite, Opaque]): cudaError_t =
    unsupported
  def cudaGraphExecHostNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      pNodeParams: Pointer[struct_cudaHostNodeParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphExecKernelNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      pNodeParams: Pointer[struct_cudaKernelNodeParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphExecMemcpyNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      pNodeParams: Pointer[struct_cudaMemcpy3DParms, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphExecMemcpyNodeSetParams1D(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      dst: OpaquePointer,
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaGraphExecMemcpyNodeSetParamsFromSymbol(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      dst: OpaquePointer,
      symbol: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaGraphExecMemcpyNodeSetParamsToSymbol(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaGraphExecMemsetNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      pNodeParams: Pointer[struct_cudaMemsetParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphExecNodeSetParams(
      graphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      nodeParams: Pointer[struct_cudaGraphNodeParams, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphExecUpdate(
      hGraphExec: cudaGraphExec_t,
      hGraph: cudaGraph_t,
      resultInfo: Pointer[cudaGraphExecUpdateResultInfo, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphExternalSemaphoresSignalNodeGetParams(
      hNode: cudaGraphNode_t,
      params_out: Pointer[struct_cudaExternalSemaphoreSignalNodeParams, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphExternalSemaphoresSignalNodeSetParams(
      hNode: cudaGraphNode_t,
      nodeParams: Pointer[struct_cudaExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphExternalSemaphoresWaitNodeGetParams(
      hNode: cudaGraphNode_t,
      params_out: Pointer[struct_cudaExternalSemaphoreWaitNodeParams, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphExternalSemaphoresWaitNodeSetParams(
      hNode: cudaGraphNode_t,
      nodeParams: Pointer[struct_cudaExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphGetEdges(
      graph: cudaGraph_t,
      from: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      to: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, ReadWrite, Opaque],
      numEdges: Pointer[size_t, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphGetId(hGraph: cudaGraph_t, graphID: Pointer[unsigned_int, ReadWrite, Opaque]): cudaError_t = unsupported
  def cudaGraphGetNodes(
      graph: cudaGraph_t,
      nodes: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      numNodes: Pointer[size_t, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphGetRootNodes(
      graph: cudaGraph_t,
      pRootNodes: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      pNumRootNodes: Pointer[size_t, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphHostNodeGetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[struct_cudaHostNodeParams, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphHostNodeSetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[struct_cudaHostNodeParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphInstantiate(
      pGraphExec: Pointer[cudaGraphExec_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      flags: unsigned_long_long
  ): cudaError_t = unsupported
  def cudaGraphInstantiateWithFlags(
      pGraphExec: Pointer[cudaGraphExec_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      flags: unsigned_long_long
  ): cudaError_t = unsupported
  def cudaGraphInstantiateWithParams(
      pGraphExec: Pointer[cudaGraphExec_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      instantiateParams: Pointer[cudaGraphInstantiateParams, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphKernelNodeCopyAttributes(hDst: cudaGraphNode_t, hSrc: cudaGraphNode_t): cudaError_t = unsupported
  def cudaGraphKernelNodeGetAttribute(
      hNode: cudaGraphNode_t,
      attr: cudaLaunchAttributeID,
      value_out: Pointer[cudaLaunchAttributeValue, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphKernelNodeGetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[struct_cudaKernelNodeParams, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphKernelNodeSetAttribute(
      hNode: cudaGraphNode_t,
      attr: cudaLaunchAttributeID,
      value: Pointer[cudaLaunchAttributeValue, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphKernelNodeSetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[struct_cudaKernelNodeParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphLaunch(graphExec: cudaGraphExec_t, stream: cudaStream_t): cudaError_t = unsupported
  def cudaGraphMemAllocNodeGetParams(
      node: cudaGraphNode_t,
      params_out: Pointer[struct_cudaMemAllocNodeParams, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphMemFreeNodeGetParams(node: cudaGraphNode_t, dptr_out: OpaquePointer): cudaError_t = unsupported
  def cudaGraphMemcpyNodeGetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[struct_cudaMemcpy3DParms, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphMemcpyNodeSetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[struct_cudaMemcpy3DParms, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphMemcpyNodeSetParams1D(
      node: cudaGraphNode_t,
      dst: OpaquePointer,
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaGraphMemcpyNodeSetParamsFromSymbol(
      node: cudaGraphNode_t,
      dst: OpaquePointer,
      symbol: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaGraphMemcpyNodeSetParamsToSymbol(
      node: cudaGraphNode_t,
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaGraphMemsetNodeGetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[struct_cudaMemsetParams, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphMemsetNodeSetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[struct_cudaMemsetParams, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphNodeFindInClone(
      pNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      originalNode: cudaGraphNode_t,
      clonedGraph: cudaGraph_t
  ): cudaError_t = unsupported
  def cudaGraphNodeGetContainingGraph(
      hNode: cudaGraphNode_t,
      phGraph: Pointer[cudaGraph_t, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphNodeGetDependencies(
      node: cudaGraphNode_t,
      pDependencies: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, ReadWrite, Opaque],
      pNumDependencies: Pointer[size_t, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphNodeGetDependentNodes(
      node: cudaGraphNode_t,
      pDependentNodes: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, ReadWrite, Opaque],
      pNumDependentNodes: Pointer[size_t, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphNodeGetEnabled(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      isEnabled: Pointer[unsigned_int, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphNodeGetLocalId(hNode: cudaGraphNode_t, nodeId: Pointer[unsigned_int, ReadWrite, Opaque]): cudaError_t =
    unsupported
  def cudaGraphNodeGetParams(
      node: cudaGraphNode_t,
      nodeParams: Pointer[struct_cudaGraphNodeParams, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphNodeGetToolsId(
      hNode: cudaGraphNode_t,
      toolsNodeId: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphNodeGetType(
      node: cudaGraphNode_t,
      pType: Pointer[enum_cudaGraphNodeType, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphNodeSetEnabled(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      isEnabled: unsigned_int
  ): cudaError_t = unsupported
  def cudaGraphNodeSetParams(
      node: cudaGraphNode_t,
      nodeParams: Pointer[struct_cudaGraphNodeParams, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaGraphReleaseUserObject(graph: cudaGraph_t, `object`: cudaUserObject_t, count: unsigned_int): cudaError_t =
    unsupported
  def cudaGraphRemoveDependencies(
      graph: cudaGraph_t,
      from: Pointer[cudaGraphNode_t, Read, Opaque],
      to: Pointer[cudaGraphNode_t, Read, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: size_t
  ): cudaError_t = unsupported
  def cudaGraphRetainUserObject(
      graph: cudaGraph_t,
      `object`: cudaUserObject_t,
      count: unsigned_int,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaGraphUpload(graphExec: cudaGraphExec_t, stream: cudaStream_t): cudaError_t = unsupported
  def cudaGraphicsMapResources(
      count: Int,
      resources: Pointer[cudaGraphicsResource_t, ReadWrite, Opaque],
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaGraphicsResourceGetMappedMipmappedArray(
      mipmappedArray: Pointer[cudaMipmappedArray_t, ReadWrite, Opaque],
      resource: cudaGraphicsResource_t
  ): cudaError_t = unsupported
  def cudaGraphicsResourceGetMappedPointer(
      devPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      size: Pointer[size_t, ReadWrite, Opaque],
      resource: cudaGraphicsResource_t
  ): cudaError_t = unsupported
  def cudaGraphicsResourceSetMapFlags(resource: cudaGraphicsResource_t, flags: unsigned_int): cudaError_t = unsupported
  def cudaGraphicsSubResourceGetMappedArray(
      array: Pointer[cudaArray_t, ReadWrite, Opaque],
      resource: cudaGraphicsResource_t,
      arrayIndex: unsigned_int,
      mipLevel: unsigned_int
  ): cudaError_t = unsupported
  def cudaGraphicsUnmapResources(
      count: Int,
      resources: Pointer[cudaGraphicsResource_t, ReadWrite, Opaque],
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaGraphicsUnregisterResource(resource: cudaGraphicsResource_t): cudaError_t = unsupported
  def cudaGreenCtxCreate(
      phCtx: Pointer[cudaExecutionContext_t, ReadWrite, Opaque],
      desc: cudaDevResourceDesc_t,
      device: Int,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaHostAlloc(pHost: Pointer[OpaquePointer, ReadWrite, Opaque], size: size_t, flags: unsigned_int): cudaError_t =
    unsupported
  def cudaHostGetDevicePointer(
      pDevice: Pointer[OpaquePointer, ReadWrite, Opaque],
      pHost: OpaquePointer,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaHostGetFlags(pFlags: Pointer[unsigned_int, ReadWrite, Opaque], pHost: OpaquePointer): cudaError_t =
    unsupported
  def cudaHostRegister(ptr: OpaquePointer, size: size_t, flags: unsigned_int): cudaError_t = unsupported
  def cudaHostUnregister(ptr: OpaquePointer): cudaError_t = unsupported
  def cudaImportExternalMemory(
      extMem_out: Pointer[cudaExternalMemory_t, ReadWrite, Opaque],
      memHandleDesc: Pointer[struct_cudaExternalMemoryHandleDesc, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaImportExternalSemaphore(
      extSem_out: Pointer[cudaExternalSemaphore_t, ReadWrite, Opaque],
      semHandleDesc: Pointer[struct_cudaExternalSemaphoreHandleDesc, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaInitDevice(device: Int, deviceFlags: unsigned_int, flags: unsigned_int): cudaError_t = unsupported
  def cudaIpcCloseMemHandle(devPtr: OpaquePointer): cudaError_t = unsupported
  def cudaIpcGetEventHandle(handle: Pointer[cudaIpcEventHandle_t, ReadWrite, Opaque], event: cudaEvent_t): cudaError_t =
    unsupported
  def cudaIpcGetMemHandle(handle: Pointer[cudaIpcMemHandle_t, ReadWrite, Opaque], devPtr: OpaquePointer): cudaError_t =
    unsupported
  def cudaIpcOpenEventHandle(
      event: Pointer[cudaEvent_t, ReadWrite, Opaque],
      handle: cudaIpcEventHandle_t
  ): cudaError_t = unsupported
  def cudaIpcOpenMemHandle(
      devPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      handle: cudaIpcMemHandle_t,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaKernelSetAttributeForDevice(
      kernel: cudaKernel_t,
      attr: enum_cudaFuncAttribute,
      value: Int,
      device: Int
  ): cudaError_t = unsupported
  def cudaLaunchCooperativeKernel(
      func: Pointer[CVoid, Read, Opaque],
      gridDim: dim3,
      blockDim: dim3,
      args: Pointer[OpaquePointer, ReadWrite, Opaque],
      sharedMem: size_t,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaLaunchHostFunc(stream: cudaStream_t, fn: cudaHostFn_t, userData: OpaquePointer): cudaError_t = unsupported
  def cudaLaunchHostFunc_v2(
      stream: cudaStream_t,
      fn: cudaHostFn_t,
      userData: OpaquePointer,
      syncMode: unsigned_int
  ): cudaError_t = unsupported
  def cudaLaunchKernel(
      func: Pointer[CVoid, Read, Opaque],
      gridDim: dim3,
      blockDim: dim3,
      args: Pointer[OpaquePointer, ReadWrite, Opaque],
      sharedMem: size_t,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaLaunchKernelExC(
      config: Pointer[cudaLaunchConfig_t, Read, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      args: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaLibraryEnumerateKernels(
      kernels: Pointer[cudaKernel_t, ReadWrite, Opaque],
      numKernels: unsigned_int,
      lib: cudaLibrary_t
  ): cudaError_t = unsupported
  def cudaLibraryGetGlobal(
      dptr: Pointer[OpaquePointer, ReadWrite, Opaque],
      bytes: Pointer[size_t, ReadWrite, Opaque],
      library: cudaLibrary_t,
      name: CString
  ): cudaError_t = unsupported
  def cudaLibraryGetKernel(
      pKernel: Pointer[cudaKernel_t, ReadWrite, Opaque],
      library: cudaLibrary_t,
      name: CString
  ): cudaError_t = unsupported
  def cudaLibraryGetKernelCount(count: Pointer[unsigned_int, ReadWrite, Opaque], lib: cudaLibrary_t): cudaError_t =
    unsupported
  def cudaLibraryGetManaged(
      dptr: Pointer[OpaquePointer, ReadWrite, Opaque],
      bytes: Pointer[size_t, ReadWrite, Opaque],
      library: cudaLibrary_t,
      name: CString
  ): cudaError_t = unsupported
  def cudaLibraryGetUnifiedFunction(
      fptr: Pointer[OpaquePointer, ReadWrite, Opaque],
      library: cudaLibrary_t,
      symbol: CString
  ): cudaError_t = unsupported
  def cudaLibraryLoadData(
      library: Pointer[cudaLibrary_t, ReadWrite, Opaque],
      code: Pointer[CVoid, Read, Opaque],
      jitOptions: Pointer[enum_cudaJitOption, ReadWrite, Opaque],
      jitOptionsValues: Pointer[OpaquePointer, ReadWrite, Opaque],
      numJitOptions: unsigned_int,
      libraryOptions: Pointer[enum_cudaLibraryOption, ReadWrite, Opaque],
      libraryOptionValues: Pointer[OpaquePointer, ReadWrite, Opaque],
      numLibraryOptions: unsigned_int
  ): cudaError_t = unsupported
  def cudaLibraryLoadFromFile(
      library: Pointer[cudaLibrary_t, ReadWrite, Opaque],
      fileName: CString,
      jitOptions: Pointer[enum_cudaJitOption, ReadWrite, Opaque],
      jitOptionsValues: Pointer[OpaquePointer, ReadWrite, Opaque],
      numJitOptions: unsigned_int,
      libraryOptions: Pointer[enum_cudaLibraryOption, ReadWrite, Opaque],
      libraryOptionValues: Pointer[OpaquePointer, ReadWrite, Opaque],
      numLibraryOptions: unsigned_int
  ): cudaError_t = unsupported
  def cudaLibraryUnload(library: cudaLibrary_t): cudaError_t = unsupported
  def cudaLogsCurrent(iterator_out: Pointer[cudaLogIterator, ReadWrite, Opaque], flags: unsigned_int): cudaError_t =
    unsupported
  def cudaLogsDumpToFile(
      iterator: Pointer[cudaLogIterator, ReadWrite, Opaque],
      pathToFile: CString,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaLogsDumpToMemory(
      iterator: Pointer[cudaLogIterator, ReadWrite, Opaque],
      buffer: Pointer[Byte, ReadWrite, Opaque],
      size: Pointer[size_t, ReadWrite, Opaque],
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaLogsRegisterCallback(
      callbackFunc: cudaLogsCallback_t,
      userData: OpaquePointer,
      callback_out: Pointer[cudaLogsCallbackHandle, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaLogsUnregisterCallback(callback: cudaLogsCallbackHandle): cudaError_t = unsupported
  def cudaMalloc(devPtr: Pointer[OpaquePointer, ReadWrite, Opaque], size: size_t): cudaError_t = unsupported
  def cudaMalloc3D(
      pitchedDevPtr: Pointer[struct_cudaPitchedPtr, ReadWrite, Opaque],
      extent: struct_cudaExtent
  ): cudaError_t = unsupported
  def cudaMalloc3DArray(
      array: Pointer[cudaArray_t, ReadWrite, Opaque],
      desc: Pointer[struct_cudaChannelFormatDesc, Read, Opaque],
      extent: struct_cudaExtent,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaMallocArray(
      array: Pointer[cudaArray_t, ReadWrite, Opaque],
      desc: Pointer[struct_cudaChannelFormatDesc, Read, Opaque],
      width: size_t,
      height: size_t,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaMallocAsync(
      devPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      size: size_t,
      hStream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMallocFromPoolAsync(
      ptr: Pointer[OpaquePointer, ReadWrite, Opaque],
      size: size_t,
      memPool: cudaMemPool_t,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMallocHost(ptr: Pointer[OpaquePointer, ReadWrite, Opaque], size: size_t): cudaError_t = unsupported
  def cudaMallocManaged(
      devPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      size: size_t,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaMallocMipmappedArray(
      mipmappedArray: Pointer[cudaMipmappedArray_t, ReadWrite, Opaque],
      desc: Pointer[struct_cudaChannelFormatDesc, Read, Opaque],
      extent: struct_cudaExtent,
      numLevels: unsigned_int,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaMallocPitch(
      devPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      pitch: Pointer[size_t, ReadWrite, Opaque],
      width: size_t,
      height: size_t
  ): cudaError_t = unsupported
  def cudaMemAdvise(
      devPtr: Pointer[CVoid, Read, Opaque],
      count: size_t,
      advice: enum_cudaMemoryAdvise,
      location: struct_cudaMemLocation
  ): cudaError_t = unsupported
  def cudaMemDiscardAndPrefetchBatchAsync(
      dptrs: Pointer[OpaquePointer, ReadWrite, Opaque],
      sizes: Pointer[size_t, ReadWrite, Opaque],
      count: size_t,
      prefetchLocs: Pointer[struct_cudaMemLocation, ReadWrite, Opaque],
      prefetchLocIdxs: Pointer[size_t, ReadWrite, Opaque],
      numPrefetchLocs: size_t,
      flags: unsigned_long_long,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemDiscardBatchAsync(
      dptrs: Pointer[OpaquePointer, ReadWrite, Opaque],
      sizes: Pointer[size_t, ReadWrite, Opaque],
      count: size_t,
      flags: unsigned_long_long,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemGetDefaultMemPool(
      memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque],
      location: Pointer[struct_cudaMemLocation, ReadWrite, Opaque],
      `type`: enum_cudaMemAllocationType
  ): cudaError_t = unsupported
  def cudaMemGetInfo(free: Pointer[size_t, ReadWrite, Opaque], total: Pointer[size_t, ReadWrite, Opaque]): cudaError_t =
    unsupported
  def cudaMemGetMemPool(
      memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque],
      location: Pointer[struct_cudaMemLocation, ReadWrite, Opaque],
      `type`: enum_cudaMemAllocationType
  ): cudaError_t = unsupported
  def cudaMemPoolCreate(
      memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque],
      poolProps: Pointer[struct_cudaMemPoolProps, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaMemPoolDestroy(memPool: cudaMemPool_t): cudaError_t = unsupported
  def cudaMemPoolExportPointer(
      exportData: Pointer[struct_cudaMemPoolPtrExportData, ReadWrite, Opaque],
      ptr: OpaquePointer
  ): cudaError_t = unsupported
  def cudaMemPoolExportToShareableHandle(
      shareableHandle: OpaquePointer,
      memPool: cudaMemPool_t,
      handleType: enum_cudaMemAllocationHandleType,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaMemPoolGetAccess(
      flags: Pointer[enum_cudaMemAccessFlags, ReadWrite, Opaque],
      memPool: cudaMemPool_t,
      location: Pointer[struct_cudaMemLocation, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaMemPoolGetAttribute(memPool: cudaMemPool_t, attr: enum_cudaMemPoolAttr, value: OpaquePointer): cudaError_t =
    unsupported
  def cudaMemPoolImportFromShareableHandle(
      memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque],
      shareableHandle: OpaquePointer,
      handleType: enum_cudaMemAllocationHandleType,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaMemPoolImportPointer(
      ptr: Pointer[OpaquePointer, ReadWrite, Opaque],
      memPool: cudaMemPool_t,
      exportData: Pointer[struct_cudaMemPoolPtrExportData, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaMemPoolSetAccess(
      memPool: cudaMemPool_t,
      descList: Pointer[struct_cudaMemAccessDesc, Read, Opaque],
      count: size_t
  ): cudaError_t = unsupported
  def cudaMemPoolSetAttribute(memPool: cudaMemPool_t, attr: enum_cudaMemPoolAttr, value: OpaquePointer): cudaError_t =
    unsupported
  def cudaMemPoolTrimTo(memPool: cudaMemPool_t, minBytesToKeep: size_t): cudaError_t = unsupported
  def cudaMemPrefetchAsync(
      devPtr: Pointer[CVoid, Read, Opaque],
      count: size_t,
      location: struct_cudaMemLocation,
      flags: unsigned_int,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemPrefetchBatchAsync(
      dptrs: Pointer[OpaquePointer, ReadWrite, Opaque],
      sizes: Pointer[size_t, ReadWrite, Opaque],
      count: size_t,
      prefetchLocs: Pointer[struct_cudaMemLocation, ReadWrite, Opaque],
      prefetchLocIdxs: Pointer[size_t, ReadWrite, Opaque],
      numPrefetchLocs: size_t,
      flags: unsigned_long_long,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemRangeGetAttribute(
      data: OpaquePointer,
      dataSize: size_t,
      attribute: enum_cudaMemRangeAttribute,
      devPtr: Pointer[CVoid, Read, Opaque],
      count: size_t
  ): cudaError_t = unsupported
  def cudaMemRangeGetAttributes(
      data: Pointer[OpaquePointer, ReadWrite, Opaque],
      dataSizes: Pointer[size_t, ReadWrite, Opaque],
      attributes: Pointer[enum_cudaMemRangeAttribute, ReadWrite, Opaque],
      numAttributes: size_t,
      devPtr: Pointer[CVoid, Read, Opaque],
      count: size_t
  ): cudaError_t = unsupported
  def cudaMemSetMemPool(
      location: Pointer[struct_cudaMemLocation, ReadWrite, Opaque],
      `type`: enum_cudaMemAllocationType,
      memPool: cudaMemPool_t
  ): cudaError_t = unsupported
  def cudaMemcpy(
      dst: OpaquePointer,
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaMemcpy2D(
      dst: OpaquePointer,
      dpitch: size_t,
      src: Pointer[CVoid, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaMemcpy2DArrayToArray(
      dst: cudaArray_t,
      wOffsetDst: size_t,
      hOffsetDst: size_t,
      src: cudaArray_const_t,
      wOffsetSrc: size_t,
      hOffsetSrc: size_t,
      width: size_t,
      height: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaMemcpy2DAsync(
      dst: OpaquePointer,
      dpitch: size_t,
      src: Pointer[CVoid, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: enum_cudaMemcpyKind,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemcpy2DFromArray(
      dst: OpaquePointer,
      dpitch: size_t,
      src: cudaArray_const_t,
      wOffset: size_t,
      hOffset: size_t,
      width: size_t,
      height: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaMemcpy2DFromArrayAsync(
      dst: OpaquePointer,
      dpitch: size_t,
      src: cudaArray_const_t,
      wOffset: size_t,
      hOffset: size_t,
      width: size_t,
      height: size_t,
      kind: enum_cudaMemcpyKind,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemcpy2DToArray(
      dst: cudaArray_t,
      wOffset: size_t,
      hOffset: size_t,
      src: Pointer[CVoid, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaMemcpy2DToArrayAsync(
      dst: cudaArray_t,
      wOffset: size_t,
      hOffset: size_t,
      src: Pointer[CVoid, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: enum_cudaMemcpyKind,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemcpy3D(p: Pointer[struct_cudaMemcpy3DParms, Read, Opaque]): cudaError_t = unsupported
  def cudaMemcpy3DAsync(p: Pointer[struct_cudaMemcpy3DParms, Read, Opaque], stream: cudaStream_t): cudaError_t =
    unsupported
  def cudaMemcpy3DBatchAsync(
      numOps: size_t,
      opList: Pointer[struct_cudaMemcpy3DBatchOp, ReadWrite, Opaque],
      flags: unsigned_long_long,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemcpy3DPeer(p: Pointer[struct_cudaMemcpy3DPeerParms, Read, Opaque]): cudaError_t = unsupported
  def cudaMemcpy3DPeerAsync(p: Pointer[struct_cudaMemcpy3DPeerParms, Read, Opaque], stream: cudaStream_t): cudaError_t =
    unsupported
  def cudaMemcpy3DWithAttributesAsync(
      op: Pointer[struct_cudaMemcpy3DBatchOp, ReadWrite, Opaque],
      flags: unsigned_long_long,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemcpyArrayToArray(
      dst: cudaArray_t,
      wOffsetDst: size_t,
      hOffsetDst: size_t,
      src: cudaArray_const_t,
      wOffsetSrc: size_t,
      hOffsetSrc: size_t,
      count: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaMemcpyAsync(
      dst: OpaquePointer,
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      kind: enum_cudaMemcpyKind,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemcpyBatchAsync(
      dsts: Pointer[OpaquePointer, Read, Opaque],
      srcs: Pointer[Pointer[CVoid, Read, Opaque], Read, Opaque],
      sizes: Pointer[size_t, Read, Opaque],
      count: size_t,
      attrs: Pointer[struct_cudaMemcpyAttributes, ReadWrite, Opaque],
      attrsIdxs: Pointer[size_t, ReadWrite, Opaque],
      numAttrs: size_t,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemcpyFromArray(
      dst: OpaquePointer,
      src: cudaArray_const_t,
      wOffset: size_t,
      hOffset: size_t,
      count: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaMemcpyFromArrayAsync(
      dst: OpaquePointer,
      src: cudaArray_const_t,
      wOffset: size_t,
      hOffset: size_t,
      count: size_t,
      kind: enum_cudaMemcpyKind,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemcpyFromSymbol(
      dst: OpaquePointer,
      symbol: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaMemcpyFromSymbolAsync(
      dst: OpaquePointer,
      symbol: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: enum_cudaMemcpyKind,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemcpyPeer(
      dst: OpaquePointer,
      dstDevice: Int,
      src: Pointer[CVoid, Read, Opaque],
      srcDevice: Int,
      count: size_t
  ): cudaError_t = unsupported
  def cudaMemcpyPeerAsync(
      dst: OpaquePointer,
      dstDevice: Int,
      src: Pointer[CVoid, Read, Opaque],
      srcDevice: Int,
      count: size_t,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemcpyToArray(
      dst: cudaArray_t,
      wOffset: size_t,
      hOffset: size_t,
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaMemcpyToArrayAsync(
      dst: cudaArray_t,
      wOffset: size_t,
      hOffset: size_t,
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      kind: enum_cudaMemcpyKind,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemcpyToSymbol(
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: enum_cudaMemcpyKind
  ): cudaError_t = unsupported
  def cudaMemcpyToSymbolAsync(
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: enum_cudaMemcpyKind,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemcpyWithAttributesAsync(
      dst: OpaquePointer,
      src: Pointer[CVoid, Read, Opaque],
      size: size_t,
      attr: Pointer[struct_cudaMemcpyAttributes, ReadWrite, Opaque],
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemset(devPtr: OpaquePointer, value: Int, count: size_t): cudaError_t = unsupported
  def cudaMemset2D(devPtr: OpaquePointer, pitch: size_t, value: Int, width: size_t, height: size_t): cudaError_t =
    unsupported
  def cudaMemset2DAsync(
      devPtr: OpaquePointer,
      pitch: size_t,
      value: Int,
      width: size_t,
      height: size_t,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemset3D(pitchedDevPtr: struct_cudaPitchedPtr, value: Int, extent: struct_cudaExtent): cudaError_t =
    unsupported
  def cudaMemset3DAsync(
      pitchedDevPtr: struct_cudaPitchedPtr,
      value: Int,
      extent: struct_cudaExtent,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaMemsetAsync(devPtr: OpaquePointer, value: Int, count: size_t, stream: cudaStream_t): cudaError_t = unsupported
  def cudaMipmappedArrayGetMemoryRequirements(
      memoryRequirements: Pointer[struct_cudaArrayMemoryRequirements, ReadWrite, Opaque],
      mipmap: cudaMipmappedArray_t,
      device: Int
  ): cudaError_t = unsupported
  def cudaMipmappedArrayGetSparseProperties(
      sparseProperties: Pointer[struct_cudaArraySparseProperties, ReadWrite, Opaque],
      mipmap: cudaMipmappedArray_t
  ): cudaError_t = unsupported
  def cudaOccupancyAvailableDynamicSMemPerBlock(
      dynamicSmemSize: Pointer[size_t, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      numBlocks: Int,
      blockSize: Int
  ): cudaError_t = unsupported
  def cudaOccupancyMaxActiveBlocksPerMultiprocessor(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      blockSize: Int,
      dynamicSMemSize: size_t
  ): cudaError_t = unsupported
  def cudaOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      blockSize: Int,
      dynamicSMemSize: size_t,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaOccupancyMaxActiveClusters(
      numClusters: Pointer[Int, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      launchConfig: Pointer[cudaLaunchConfig_t, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaOccupancyMaxPotentialClusterSize(
      clusterSize: Pointer[Int, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      launchConfig: Pointer[cudaLaunchConfig_t, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaPeekAtLastError(): cudaError_t = unsupported
  def cudaPointerGetAttributes(
      attributes: Pointer[struct_cudaPointerAttributes, ReadWrite, Opaque],
      ptr: Pointer[CVoid, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaRuntimeGetVersion(runtimeVersion: Pointer[Int, ReadWrite, Opaque]): cudaError_t = unsupported
  def cudaSetDevice(device: Int): cudaError_t = unsupported
  def cudaSetDeviceFlags(flags: unsigned_int): cudaError_t = unsupported
  def cudaSetValidDevices(device_arr: Pointer[Int, ReadWrite, Opaque], len: Int): cudaError_t = unsupported
  def cudaSignalExternalSemaphoresAsync(
      extSemArray: Pointer[cudaExternalSemaphore_t, Read, Opaque],
      paramsArray: Pointer[struct_cudaExternalSemaphoreSignalParams, Read, Opaque],
      numExtSems: unsigned_int,
      stream: cudaStream_t
  ): cudaError_t = unsupported
  def cudaStreamAddCallback(
      stream: cudaStream_t,
      callback: cudaStreamCallback_t,
      userData: OpaquePointer,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaStreamAttachMemAsync(
      stream: cudaStream_t,
      devPtr: OpaquePointer,
      length: size_t,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaStreamBeginCapture(stream: cudaStream_t, mode: enum_cudaStreamCaptureMode): cudaError_t = unsupported
  def cudaStreamBeginCaptureToGraph(
      stream: cudaStream_t,
      graph: cudaGraph_t,
      dependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      dependencyData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: size_t,
      mode: enum_cudaStreamCaptureMode
  ): cudaError_t = unsupported
  def cudaStreamBeginRecaptureToGraph(
      stream: cudaStream_t,
      mode: enum_cudaStreamCaptureMode,
      graph: cudaGraph_t,
      callbackData: Pointer[struct_cudaGraphRecaptureCallbackData, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaStreamCopyAttributes(dst: cudaStream_t, src: cudaStream_t): cudaError_t = unsupported
  def cudaStreamCreate(pStream: Pointer[cudaStream_t, ReadWrite, Opaque]): cudaError_t = unsupported
  def cudaStreamCreateWithFlags(pStream: Pointer[cudaStream_t, ReadWrite, Opaque], flags: unsigned_int): cudaError_t =
    unsupported
  def cudaStreamCreateWithPriority(
      pStream: Pointer[cudaStream_t, ReadWrite, Opaque],
      flags: unsigned_int,
      priority: Int
  ): cudaError_t = unsupported
  def cudaStreamDestroy(stream: cudaStream_t): cudaError_t = unsupported
  def cudaStreamEndCapture(stream: cudaStream_t, pGraph: Pointer[cudaGraph_t, ReadWrite, Opaque]): cudaError_t =
    unsupported
  def cudaStreamGetAttribute(
      hStream: cudaStream_t,
      attr: cudaLaunchAttributeID,
      value_out: Pointer[cudaLaunchAttributeValue, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaStreamGetCaptureInfo(
      stream: cudaStream_t,
      captureStatus_out: Pointer[enum_cudaStreamCaptureStatus, ReadWrite, Opaque],
      id_out: Pointer[unsigned_long_long, ReadWrite, Opaque],
      graph_out: Pointer[cudaGraph_t, ReadWrite, Opaque],
      dependencies_out: Pointer[Pointer[cudaGraphNode_t, Read, Opaque], ReadWrite, Opaque],
      edgeData_out: Pointer[Pointer[cudaGraphEdgeData, Read, Opaque], ReadWrite, Opaque],
      numDependencies_out: Pointer[size_t, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaStreamGetDevResource(
      hStream: cudaStream_t,
      resource: Pointer[cudaDevResource, ReadWrite, Opaque],
      `type`: enum_cudaDevResourceType
  ): cudaError_t = unsupported
  def cudaStreamGetDevice(hStream: cudaStream_t, device: Pointer[Int, ReadWrite, Opaque]): cudaError_t = unsupported
  def cudaStreamGetFlags(hStream: cudaStream_t, flags: Pointer[unsigned_int, ReadWrite, Opaque]): cudaError_t =
    unsupported
  def cudaStreamGetId(hStream: cudaStream_t, streamId: Pointer[unsigned_long_long, ReadWrite, Opaque]): cudaError_t =
    unsupported
  def cudaStreamGetPriority(hStream: cudaStream_t, priority: Pointer[Int, ReadWrite, Opaque]): cudaError_t = unsupported
  def cudaStreamIsCapturing(
      stream: cudaStream_t,
      pCaptureStatus: Pointer[enum_cudaStreamCaptureStatus, ReadWrite, Opaque]
  ): cudaError_t = unsupported
  def cudaStreamQuery(stream: cudaStream_t): cudaError_t = unsupported
  def cudaStreamSetAttribute(
      hStream: cudaStream_t,
      attr: cudaLaunchAttributeID,
      value: Pointer[cudaLaunchAttributeValue, Read, Opaque]
  ): cudaError_t = unsupported
  def cudaStreamSynchronize(stream: cudaStream_t): cudaError_t = unsupported
  def cudaStreamUpdateCaptureDependencies(
      stream: cudaStream_t,
      dependencies: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      dependencyData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: size_t,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaStreamWaitEvent(stream: cudaStream_t, event: cudaEvent_t, flags: unsigned_int): cudaError_t = unsupported
  def cudaThreadExchangeStreamCaptureMode(mode: Pointer[enum_cudaStreamCaptureMode, ReadWrite, Opaque]): cudaError_t =
    unsupported
  def cudaUserObjectCreate(
      object_out: Pointer[cudaUserObject_t, ReadWrite, Opaque],
      ptr: OpaquePointer,
      destroy: cudaHostFn_t,
      initialRefcount: unsigned_int,
      flags: unsigned_int
  ): cudaError_t = unsupported
  def cudaUserObjectRelease(`object`: cudaUserObject_t, count: unsigned_int): cudaError_t = unsupported
  def cudaUserObjectRetain(`object`: cudaUserObject_t, count: unsigned_int): cudaError_t = unsupported
  def cudaWaitExternalSemaphoresAsync(
      extSemArray: Pointer[cudaExternalSemaphore_t, Read, Opaque],
      paramsArray: Pointer[struct_cudaExternalSemaphoreWaitParams, Read, Opaque],
      numExtSems: unsigned_int,
      stream: cudaStream_t
  ): cudaError_t = unsupported
}

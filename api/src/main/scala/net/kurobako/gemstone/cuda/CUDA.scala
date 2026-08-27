// Generated from pinned official headers by GenerateAPISurfaces. Do not edit mechanically.
package net.kurobako.gemstone.cuda

import net.kurobako.gemstone.abi.*

type CUDA_ARRAY3D_DESCRIPTOR                   = CValue["CUDA_ARRAY3D_DESCRIPTOR"]
type CUDA_ARRAY_DESCRIPTOR                     = CValue["CUDA_ARRAY_DESCRIPTOR"]
type CUDA_ARRAY_MEMORY_REQUIREMENTS            = CValue["CUDA_ARRAY_MEMORY_REQUIREMENTS"]
type CUDA_ARRAY_SPARSE_PROPERTIES              = CValue["CUDA_ARRAY_SPARSE_PROPERTIES"]
type CUDA_BATCH_MEM_OP_NODE_PARAMS             = CValue["CUDA_BATCH_MEM_OP_NODE_PARAMS"]
type CUDA_EXTERNAL_MEMORY_BUFFER_DESC          = CValue["CUDA_EXTERNAL_MEMORY_BUFFER_DESC"]
type CUDA_EXTERNAL_MEMORY_HANDLE_DESC          = CValue["CUDA_EXTERNAL_MEMORY_HANDLE_DESC"]
type CUDA_EXTERNAL_MEMORY_MIPMAPPED_ARRAY_DESC = CValue["CUDA_EXTERNAL_MEMORY_MIPMAPPED_ARRAY_DESC"]
type CUDA_EXTERNAL_SEMAPHORE_HANDLE_DESC       = CValue["CUDA_EXTERNAL_SEMAPHORE_HANDLE_DESC"]
type CUDA_EXTERNAL_SEMAPHORE_SIGNAL_PARAMS     = CValue["CUDA_EXTERNAL_SEMAPHORE_SIGNAL_PARAMS"]
type CUDA_EXTERNAL_SEMAPHORE_WAIT_PARAMS       = CValue["CUDA_EXTERNAL_SEMAPHORE_WAIT_PARAMS"]
type CUDA_EXT_SEM_SIGNAL_NODE_PARAMS           = CValue["CUDA_EXT_SEM_SIGNAL_NODE_PARAMS"]
type CUDA_EXT_SEM_WAIT_NODE_PARAMS             = CValue["CUDA_EXT_SEM_WAIT_NODE_PARAMS"]
type CUDA_GRAPH_INSTANTIATE_PARAMS             = CValue["CUDA_GRAPH_INSTANTIATE_PARAMS"]
type CUDA_HOST_NODE_PARAMS                     = CValue["CUDA_HOST_NODE_PARAMS"]
type CUDA_KERNEL_NODE_PARAMS                   = CValue["CUDA_KERNEL_NODE_PARAMS"]
type CUDA_LAUNCH_PARAMS                        = CValue["CUDA_LAUNCH_PARAMS"]
type CUDA_MEMCPY2D                             = CValue["CUDA_MEMCPY2D"]
type CUDA_MEMCPY3D                             = CValue["CUDA_MEMCPY3D"]
type CUDA_MEMCPY3D_BATCH_OP                    = CValue["CUDA_MEMCPY3D_BATCH_OP"]
type CUDA_MEMCPY3D_PEER                        = CValue["CUDA_MEMCPY3D_PEER"]
type CUDA_MEMSET_NODE_PARAMS                   = CValue["CUDA_MEMSET_NODE_PARAMS"]
type CUDA_MEM_ALLOC_NODE_PARAMS                = CValue["CUDA_MEM_ALLOC_NODE_PARAMS"]
type CUDA_RESOURCE_DESC                        = CValue["CUDA_RESOURCE_DESC"]
type CUDA_RESOURCE_VIEW_DESC                   = CValue["CUDA_RESOURCE_VIEW_DESC"]
type CUDA_TEXTURE_DESC                         = CValue["CUDA_TEXTURE_DESC"]
type CU_DEV_SM_RESOURCE_GROUP_PARAMS           = CValue["CU_DEV_SM_RESOURCE_GROUP_PARAMS"]
type CUaddress_mode                            = CValue["CUaddress_mode"]
type CUarray                                   = CValue["CUarray"]
type CUarrayMapInfo                            = CValue["CUarrayMapInfo"]
type CUarray_format                            = CValue["CUarray_format"]
type CUasyncCallback                           = CallbackTypedef[
  "CUasyncCallback",
  (Pointer[CUasyncNotificationInfo, ReadWrite, Opaque], OpaquePointer, CUasyncCallbackHandle),
  Unit
]
type CUasyncCallbackHandle            = CValue["CUasyncCallbackHandle"]
type CUasyncNotificationInfo          = CValue["CUasyncNotificationInfo"]
type CUatomicOperation                = CValue["CUatomicOperation"]
type CUcheckpointCheckpointArgs       = CValue["CUcheckpointCheckpointArgs"]
type CUcheckpointLockArgs             = CValue["CUcheckpointLockArgs"]
type CUcheckpointRestoreArgs          = CValue["CUcheckpointRestoreArgs"]
type CUcheckpointUnlockArgs           = CValue["CUcheckpointUnlockArgs"]
type CUcoredumpCallbackHandle         = CValue["CUcoredumpCallbackHandle"]
type CUcoredumpSettings               = CValue["CUcoredumpSettings"]
type CUcoredumpStatusCallback         = CallbackTypedef["CUcoredumpStatusCallback", (OpaquePointer, Int, Device), Unit]
type CUctxCreateParams                = CValue["CUctxCreateParams"]
type CUdevResource                    = CValue["CUdevResource"]
type CUdevResourceDesc                = CValue["CUdevResourceDesc"]
type CUdevResourceType                = CValue["CUdevResourceType"]
type CUdevice_P2PAttribute            = CValue["CUdevice_P2PAttribute"]
type CUdevice_attribute               = CValue["CUdevice_attribute"]
type CUdevprop                        = CValue["CUdevprop"]
type CUdriverProcAddressQueryResult   = CValue["CUdriverProcAddressQueryResult"]
type CUevent                          = CValue["CUevent"]
type CUexecAffinityParam              = CValue["CUexecAffinityParam"]
type CUexecAffinityType               = CValue["CUexecAffinityType"]
type CUexternalMemory                 = CValue["CUexternalMemory"]
type CUexternalSemaphore              = CValue["CUexternalSemaphore"]
type CUfilter_mode                    = CValue["CUfilter_mode"]
type CUflushGPUDirectRDMAWritesScope  = CValue["CUflushGPUDirectRDMAWritesScope"]
type CUflushGPUDirectRDMAWritesTarget = CValue["CUflushGPUDirectRDMAWritesTarget"]
type CUfunc_cache                     = CValue["CUfunc_cache"]
type CUfunctionLoadingState           = CValue["CUfunctionLoadingState"]
type CUfunction_attribute             = CValue["CUfunction_attribute"]
type CUgraph                          = CValue["CUgraph"]
type CUgraphConditionalHandle         = CValue["CUgraphConditionalHandle"]
type CUgraphEdgeData                  = CValue["CUgraphEdgeData"]
type CUgraphExec                      = CValue["CUgraphExec"]
type CUgraphExecUpdateResultInfo      = CValue["CUgraphExecUpdateResultInfo"]
type CUgraphMem_attribute             = CValue["CUgraphMem_attribute"]
type CUgraphNode                      = CValue["CUgraphNode"]
type CUgraphNodeParams                = CValue["CUgraphNodeParams"]
type CUgraphNodeType                  = CValue["CUgraphNodeType"]
type CUgraphRecaptureCallback         = CallbackTypedef[
  "CUgraphRecaptureCallback",
  (
      OpaquePointer,
      CUgraphNode,
      Pointer[CUgraphNodeParams, Read, Opaque],
      Pointer[CUgraphNodeParams, Read, Opaque],
      CUgraphRecaptureStatus
  ),
  CUDAStatus
]
type CUgraphRecaptureStatus         = CValue["CUgraphRecaptureStatus"]
type CUgraphicsResource             = CValue["CUgraphicsResource"]
type CUgreenCtx                     = CValue["CUgreenCtx"]
type CUhostFn                       = CallbackTypedef["CUhostFn", Tuple1[CallbackData], Unit]
type CUipcEventHandle               = CValue["CUipcEventHandle"]
type CUipcMemHandle                 = CValue["CUipcMemHandle"]
type CUjitInputType                 = CValue["CUjitInputType"]
type CUjit_option                   = CValue["CUjit_option"]
type CUkernel                       = CValue["CUkernel"]
type CUkernelNodeAttrID             = CValue["CUkernelNodeAttrID"]
type CUkernelNodeAttrValue          = CValue["CUkernelNodeAttrValue"]
type CUlaunchConfig                 = CValue["CUlaunchConfig"]
type CUlibrary                      = CValue["CUlibrary"]
type CUlibraryOption                = CValue["CUlibraryOption"]
type CUlimit                        = CValue["CUlimit"]
type CUlinkState                    = CValue["CUlinkState"]
type CUlogIterator                  = CValue["CUlogIterator"]
type CUlogicalEndpointId            = CValue["CUlogicalEndpointId"]
type CUlogicalEndpointIpcHandleType = CValue["CUlogicalEndpointIpcHandleType"]
type CUlogicalEndpointProp          = CValue["CUlogicalEndpointProp"]
type CUlogLevel                     = CValue["CUlogLevel"]
type CUlogsCallback                 = CallbackTypedef[
  "CUlogsCallback",
  (OpaquePointer, CUlogLevel, Pointer[Byte, ReadWrite, Opaque], SizeT),
  Unit
]
type CUlogsCallbackHandle             = CValue["CUlogsCallbackHandle"]
type CUmemAccessDesc                  = CValue["CUmemAccessDesc"]
type CUmemAccess_flags                = CValue["CUmemAccess_flags"]
type CUmemAllocationGranularity_flags = CValue["CUmemAllocationGranularity_flags"]
type CUmemAllocationHandleType        = CValue["CUmemAllocationHandleType"]
type CUmemAllocationProp              = CValue["CUmemAllocationProp"]
type CUmemAllocationType              = CValue["CUmemAllocationType"]
type CUmemDecompressParams            = CValue["CUmemDecompressParams"]
type CUmemGenericAllocationHandle     = CValue["CUmemGenericAllocationHandle"]
type CUmemLocation                    = CValue["CUmemLocation"]
type CUmemPoolProps                   = CValue["CUmemPoolProps"]
type CUmemPoolPtrExportData           = CValue["CUmemPoolPtrExportData"]
type CUmemPool_attribute              = CValue["CUmemPool_attribute"]
type CUmemRangeHandleType             = CValue["CUmemRangeHandleType"]
type CUmem_advise                     = CValue["CUmem_advise"]
type CUmem_range_attribute            = CValue["CUmem_range_attribute"]
type CUmemcpyAttributes               = CValue["CUmemcpyAttributes"]
type CUmemoryPool                     = CValue["CUmemoryPool"]
type CUmipmappedArray                 = CValue["CUmipmappedArray"]
type CUmoduleLoadingMode              = CValue["CUmoduleLoadingMode"]
type CUmulticastGranularity_flags     = CValue["CUmulticastGranularity_flags"]
type CUmulticastObjectProp            = CValue["CUmulticastObjectProp"]
type CUoccupancyB2DSize               = CallbackTypedef["CUoccupancyB2DSize", Tuple1[Int], SizeT]
type CUpointer_attribute              = CValue["CUpointer_attribute"]
type CUprocessState                   = CValue["CUprocessState"]
type CUsharedconfig                   = CValue["CUsharedconfig"]
type CUstream                         = CValue["CUstream"]
type CUstreamAttrID                   = CValue["CUstreamAttrID"]
type CUstreamAttrValue                = CValue["CUstreamAttrValue"]
type CUstreamBatchMemOpParams         = CValue["CUstreamBatchMemOpParams"]
type CUstreamCallback                 = CallbackTypedef["CUstreamCallback", (Stream, CUDAStatus, CallbackData), Unit]
type CUstreamCaptureMode              = CValue["CUstreamCaptureMode"]
type CUstreamCaptureStatus            = CValue["CUstreamCaptureStatus"]
type CUstreamCigCaptureParams         = CValue["CUstreamCigCaptureParams"]
type CUsurfObject                     = CValue["CUsurfObject"]
type CUsurfref                        = CValue["CUsurfref"]
type CUtensorMap                      = CValue["CUtensorMap"]
type CUtensorMapDataType              = CValue["CUtensorMapDataType"]
type CUtensorMapFloatOOBfill          = CValue["CUtensorMapFloatOOBfill"]
type CUtensorMapIm2ColWideMode        = CValue["CUtensorMapIm2ColWideMode"]
type CUtensorMapInterleave            = CValue["CUtensorMapInterleave"]
type CUtensorMapL2promotion           = CValue["CUtensorMapL2promotion"]
type CUtensorMapSwizzle               = CValue["CUtensorMapSwizzle"]
type CUtexObject                      = CValue["CUtexObject"]
type CUtexref                         = CValue["CUtexref"]
type CUuserObject                     = CValue["CUuserObject"]
type CUuuid                           = CValue["CUuuid"]
type cudaArrayMemoryRequirements      = CValue["struct cudaArrayMemoryRequirements"]
type cudaArraySparseProperties        = CValue["struct cudaArraySparseProperties"]
type cudaArray_const_t                = CValue["cudaArray_const_t"]
type cudaArray_t                      = CValue["cudaArray_t"]
type cudaAsyncCallback                = CallbackTypedef[
  "cudaAsyncCallback",
  (Pointer[cudaAsyncNotificationInfo_t, ReadWrite, Opaque], OpaquePointer, cudaAsyncCallbackHandle_t),
  Unit
]
type cudaAsyncCallbackHandle_t             = CValue["cudaAsyncCallbackHandle_t"]
type cudaAsyncNotificationInfo_t           = CValue["cudaAsyncNotificationInfo_t"]
type cudaAtomicOperation                   = CValue["enum cudaAtomicOperation"]
type cudaChannelFormatDesc                 = CValue["struct cudaChannelFormatDesc"]
type cudaChannelFormatKind                 = CValue["enum cudaChannelFormatKind"]
type cudaDevResource                       = CValue["cudaDevResource"]
type cudaDevResourceDesc_t                 = CValue["cudaDevResourceDesc_t"]
type cudaDevResourceType                   = CValue["enum cudaDevResourceType"]
type cudaDevSmResourceGroupParams          = CValue["cudaDevSmResourceGroupParams"]
type cudaDeviceAttr                        = CValue["enum cudaDeviceAttr"]
type cudaDeviceP2PAttr                     = CValue["enum cudaDeviceP2PAttr"]
type cudaDeviceProp                        = CValue["struct cudaDeviceProp"]
type cudaDriverEntryPointQueryResult       = CValue["enum cudaDriverEntryPointQueryResult"]
type cudaEvent_t                           = CValue["cudaEvent_t"]
type cudaExecutionContext_t                = CValue["cudaExecutionContext_t"]
type cudaExtent                            = CValue["struct cudaExtent"]
type cudaExternalMemoryBufferDesc          = CValue["struct cudaExternalMemoryBufferDesc"]
type cudaExternalMemoryHandleDesc          = CValue["struct cudaExternalMemoryHandleDesc"]
type cudaExternalMemoryMipmappedArrayDesc  = CValue["struct cudaExternalMemoryMipmappedArrayDesc"]
type cudaExternalMemory_t                  = CValue["cudaExternalMemory_t"]
type cudaExternalSemaphoreHandleDesc       = CValue["struct cudaExternalSemaphoreHandleDesc"]
type cudaExternalSemaphoreSignalNodeParams = CValue["struct cudaExternalSemaphoreSignalNodeParams"]
type cudaExternalSemaphoreSignalParams     = CValue["struct cudaExternalSemaphoreSignalParams"]
type cudaExternalSemaphoreWaitNodeParams   = CValue["struct cudaExternalSemaphoreWaitNodeParams"]
type cudaExternalSemaphoreWaitParams       = CValue["struct cudaExternalSemaphoreWaitParams"]
type cudaExternalSemaphore_t               = CValue["cudaExternalSemaphore_t"]
type cudaFlushGPUDirectRDMAWritesScope     = CValue["enum cudaFlushGPUDirectRDMAWritesScope"]
type cudaFlushGPUDirectRDMAWritesTarget    = CValue["enum cudaFlushGPUDirectRDMAWritesTarget"]
type cudaFuncAttribute                     = CValue["enum cudaFuncAttribute"]
type cudaFuncAttributes                    = CValue["struct cudaFuncAttributes"]
type cudaFuncCache                         = CValue["enum cudaFuncCache"]
type cudaFunction_t                        = CValue["cudaFunction_t"]
type cudaGraphConditionalHandle            = CValue["cudaGraphConditionalHandle"]
type cudaGraphEdgeData                     = CValue["cudaGraphEdgeData"]
type cudaGraphExecUpdateResultInfo         = CValue["cudaGraphExecUpdateResultInfo"]
type cudaGraphExec_t                       = CValue["cudaGraphExec_t"]
type cudaGraphInstantiateParams            = CValue["cudaGraphInstantiateParams"]
type cudaGraphMemAttributeType             = CValue["enum cudaGraphMemAttributeType"]
type cudaGraphNodeParams                   = CValue["struct cudaGraphNodeParams"]
type cudaGraphNodeType                     = CValue["enum cudaGraphNodeType"]
type cudaGraphNode_t                       = CValue["cudaGraphNode_t"]
type cudaGraphRecaptureCallbackData        = CValue["struct cudaGraphRecaptureCallbackData"]
type cudaGraph_t                           = CValue["cudaGraph_t"]
type cudaGraphicsResource_t                = CValue["cudaGraphicsResource_t"]
type cudaHostFn_t                          = CallbackTypedef["cudaHostFn_t", Tuple1[OpaquePointer], Unit]
type cudaHostNodeParams                    = CValue["struct cudaHostNodeParams"]
type cudaIpcEventHandle_t                  = CValue["cudaIpcEventHandle_t"]
type cudaIpcMemHandle_t                    = CValue["cudaIpcMemHandle_t"]
type cudaJitOption                         = CValue["enum cudaJitOption"]
type cudaKernelNodeParams                  = CValue["struct cudaKernelNodeParams"]
type cudaKernel_t                          = CValue["cudaKernel_t"]
type cudaLaunchAttributeID                 = CValue["cudaLaunchAttributeID"]
type cudaLaunchAttributeValue              = CValue["cudaLaunchAttributeValue"]
type cudaLaunchConfig_t                    = CValue["cudaLaunchConfig_t"]
type cudaLibraryOption                     = CValue["enum cudaLibraryOption"]
type cudaLibrary_t                         = CValue["cudaLibrary_t"]
type cudaLimit                             = CValue["enum cudaLimit"]
type cudaLogIterator                       = CValue["cudaLogIterator"]
type cudaLogLevel                          = CValue["cudaLogLevel"]
type cudaLogsCallbackHandle                = CValue["cudaLogsCallbackHandle"]
type cudaLogsCallback_t                    = CallbackTypedef[
  "cudaLogsCallback_t",
  (OpaquePointer, cudaLogLevel, Pointer[Byte, ReadWrite, Opaque], SizeT),
  Unit
]
type cudaMemAccessDesc           = CValue["struct cudaMemAccessDesc"]
type cudaMemAccessFlags          = CValue["enum cudaMemAccessFlags"]
type cudaMemAllocNodeParams      = CValue["struct cudaMemAllocNodeParams"]
type cudaMemAllocationHandleType = CValue["enum cudaMemAllocationHandleType"]
type cudaMemAllocationType       = CValue["enum cudaMemAllocationType"]
type cudaMemLocation             = CValue["struct cudaMemLocation"]
type cudaMemPoolAttr             = CValue["enum cudaMemPoolAttr"]
type cudaMemPoolProps            = CValue["struct cudaMemPoolProps"]
type cudaMemPoolPtrExportData    = CValue["struct cudaMemPoolPtrExportData"]
type cudaMemPool_t               = CValue["cudaMemPool_t"]
type cudaMemRangeAttribute       = CValue["enum cudaMemRangeAttribute"]
type cudaMemcpy3DBatchOp         = CValue["struct cudaMemcpy3DBatchOp"]
type cudaMemcpy3DParms           = CValue["struct cudaMemcpy3DParms"]
type cudaMemcpy3DPeerParms       = CValue["struct cudaMemcpy3DPeerParms"]
type cudaMemcpyAttributes        = CValue["struct cudaMemcpyAttributes"]
type cudaMemcpyKind              = CValue["enum cudaMemcpyKind"]
type cudaMemoryAdvise            = CValue["enum cudaMemoryAdvise"]
type cudaMemsetParams            = CValue["struct cudaMemsetParams"]
type cudaMipmappedArray_const_t  = CValue["cudaMipmappedArray_const_t"]
type cudaMipmappedArray_t        = CValue["cudaMipmappedArray_t"]
type cudaPitchedPtr              = CValue["struct cudaPitchedPtr"]
type cudaPointerAttributes       = CValue["struct cudaPointerAttributes"]
type cudaResourceDesc            = CValue["struct cudaResourceDesc"]
type cudaResourceViewDesc        = CValue["struct cudaResourceViewDesc"]
type cudaSharedMemConfig         = CValue["enum cudaSharedMemConfig"]
type cudaStreamCallback_t    = CallbackTypedef["cudaStreamCallback_t", (cudaStream_t, CUDAStatus, OpaquePointer), Unit]
type cudaStreamCaptureMode   = CValue["enum cudaStreamCaptureMode"]
type cudaStreamCaptureStatus = CValue["enum cudaStreamCaptureStatus"]
type cudaStream_t            = CValue["cudaStream_t"]
type cudaSurfaceObject_t     = CValue["cudaSurfaceObject_t"]
type cudaTextureDesc         = CValue["struct cudaTextureDesc"]
type cudaTextureObject_t     = CValue["cudaTextureObject_t"]
type cudaUUID_t              = CValue["cudaUUID_t"]
type cudaUserObject_t        = CValue["cudaUserObject_t"]
type cuuint32_t              = CValue["cuuint32_t"]
type cuuint64_t              = CValue["cuuint64_t"]
type dim3                    = CValue["dim3"]
type unsigned_long_long      = CValue["unsigned long long"]
type unsigned_short          = CValue["unsigned short"]
type void_const              = CValue["void *const"]
type GemstoneDim3            = CValue["GemstoneDim3"]
type int32_t                 = CValue["int32_t"]

trait CUDA {
  def __cudaGemstoneManagedRegister(pointer: DevicePtr, bytesize: SizeT): CUDAStatus
  def __cudaGemstoneManagedUnregister(pointer: DevicePtr): CUDAStatus
  def __cudaRegisterFatBinary(
      fatBinary: OpaquePointer
  ): Pointer[OpaquePointer, ReadWrite, Opaque] =
    delegate["gemstone_cudaRegisterFatBinary"](fatBinary)
  def __cudaRegisterFatBinaryEnd(handle: Pointer[OpaquePointer, ReadWrite, Opaque]): Unit =
    delegate["gemstone_cudaRegisterFatBinaryEnd"](handle)
  def __cudaRegisterFunction(
      handle: Pointer[OpaquePointer, ReadWrite, Opaque],
      hostFunction: Pointer[Byte, Read, Opaque],
      deviceFunction: Pointer[Byte, ReadWrite, Opaque],
      deviceName: CString,
      threadLimit: int32_t,
      threadId: OpaquePointer,
      blockId: OpaquePointer,
      blockDimension: OpaquePointer,
      gridDimension: OpaquePointer,
      warpSize: Pointer[int32_t, ReadWrite, Opaque]
  ): Unit = delegate["gemstone_cudaRegisterFunction"](
    handle,
    hostFunction,
    deviceFunction,
    deviceName,
    threadLimit,
    threadId,
    blockId,
    blockDimension,
    gridDimension,
    warpSize
  )
  def __cudaRegisterVar(
      handle: Pointer[OpaquePointer, ReadWrite, Opaque],
      hostVariable: Pointer[Byte, ReadWrite, Opaque],
      deviceAddress: Pointer[Byte, ReadWrite, Opaque],
      deviceName: CString,
      external: int32_t,
      size: SizeT,
      constant: int32_t,
      global: int32_t
  ): Unit = delegate["gemstone_cudaRegisterVar"](
    handle,
    hostVariable,
    deviceAddress,
    deviceName,
    external,
    size,
    constant,
    global
  )
  def __cudaUnregisterFatBinary(handle: Pointer[OpaquePointer, ReadWrite, Opaque]): Unit =
    delegate["gemstone_cudaUnregisterFatBinary"](handle)
  def __cudaGetKernel(kernel: Out[OpaquePointer], hostFunction: Pointer[CVoid, Read, Opaque]): int32_t =
    delegate["gemstone_cudaGetKernel"](kernel, hostFunction)
  def __cudaPushCallConfiguration(
      grid: GemstoneDim3,
      block: GemstoneDim3,
      sharedMemory: SizeT,
      stream: OpaquePointer
  ): UInt32 = delegate["gemstone_cudaPushCallConfiguration"](grid, block, sharedMemory, stream)
  def __cudaPopCallConfiguration(
      grid: Out[GemstoneDim3],
      block: Out[GemstoneDim3],
      sharedMemory: Out[SizeT],
      stream: OpaquePointer
  ): int32_t = delegate["gemstone_cudaPopCallConfiguration"](grid, block, sharedMemory, stream)
  def __cudaLaunchKernel(
      kernel: OpaquePointer,
      grid: GemstoneDim3,
      block: GemstoneDim3,
      arguments: Pointer[OpaquePointer, ReadWrite, Opaque],
      sharedMemory: SizeT,
      stream: OpaquePointer
  ): int32_t = delegate["gemstone_cudaLaunchKernel"](kernel, grid, block, arguments, sharedMemory, stream)
  def cuArray3DCreate_v2(
      pHandle: Pointer[CUarray, ReadWrite, Opaque],
      pAllocateArray: Pointer[CUDA_ARRAY3D_DESCRIPTOR, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuArray3DGetDescriptor_v2(
      pArrayDescriptor: Pointer[CUDA_ARRAY3D_DESCRIPTOR, ReadWrite, Opaque],
      hArray: CUarray
  ): CUDAStatus = unsupported
  def cuArrayCreate_v2(
      pHandle: Pointer[CUarray, ReadWrite, Opaque],
      pAllocateArray: Pointer[CUDA_ARRAY_DESCRIPTOR, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuArrayDestroy(hArray: CUarray): CUDAStatus = unsupported
  def cuArrayGetDescriptor_v2(
      pArrayDescriptor: Pointer[CUDA_ARRAY_DESCRIPTOR, ReadWrite, Opaque],
      hArray: CUarray
  ): CUDAStatus = unsupported
  def cuArrayGetMemoryRequirements(
      memoryRequirements: Pointer[CUDA_ARRAY_MEMORY_REQUIREMENTS, ReadWrite, Opaque],
      array: CUarray,
      device: Device
  ): CUDAStatus = unsupported
  def cuArrayGetPlane(pPlaneArray: Pointer[CUarray, ReadWrite, Opaque], hArray: CUarray, planeIdx: UInt32): CUDAStatus =
    unsupported
  def cuArrayGetSparseProperties(
      sparseProperties: Pointer[CUDA_ARRAY_SPARSE_PROPERTIES, ReadWrite, Opaque],
      array: CUarray
  ): CUDAStatus = unsupported
  def cuCheckpointProcessCheckpoint(
      pid: Int,
      args: Pointer[CUcheckpointCheckpointArgs, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuCheckpointProcessGetRestoreThreadId(pid: Int, tid: Pointer[Int, ReadWrite, Opaque]): CUDAStatus    = unsupported
  def cuCheckpointProcessGetState(pid: Int, state: Pointer[CUprocessState, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuCheckpointProcessLock(pid: Int, args: Pointer[CUcheckpointLockArgs, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuCheckpointProcessRestore(pid: Int, args: Pointer[CUcheckpointRestoreArgs, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuCheckpointProcessUnlock(pid: Int, args: Pointer[CUcheckpointUnlockArgs, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuCoredumpDeregisterCompleteCallback(callback: CUcoredumpCallbackHandle): CUDAStatus = unsupported
  def cuCoredumpDeregisterStartCallback(callback: CUcoredumpCallbackHandle): CUDAStatus    = unsupported
  def cuCoredumpGetAttribute(
      attrib: CUcoredumpSettings,
      value: Pointer[CVoid, ReadWrite, Opaque],
      size: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuCoredumpGetAttributeGlobal(
      attrib: CUcoredumpSettings,
      value: Pointer[CVoid, ReadWrite, Opaque],
      size: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuCoredumpRegisterCompleteCallback(
      callback: CUcoredumpStatusCallback,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      callbackOut: Pointer[CUcoredumpCallbackHandle, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuCoredumpRegisterStartCallback(
      callback: CUcoredumpStatusCallback,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      callbackOut: Pointer[CUcoredumpCallbackHandle, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuCoredumpSetAttribute(
      attrib: CUcoredumpSettings,
      value: Pointer[CVoid, ReadWrite, Opaque],
      size: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuCoredumpSetAttributeGlobal(
      attrib: CUcoredumpSettings,
      value: Pointer[CVoid, ReadWrite, Opaque],
      size: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuCtxAttach(pctx: Pointer[Context, ReadWrite, Opaque], flags: UInt32): CUDAStatus = unsupported
  def cuCtxCreate_v4(
      pctx: Out[Context],
      ctxCreateParams: Nullable[InOut[CUctxCreateParams]],
      flags: UInt32,
      dev: Device
  ): CUDAStatus =
    delegate["gemstone_cuCtxCreate_v4"](pctx, ctxCreateParams, flags, dev)
  def cuCtxCreate_v2(pctx: Out[Context], flags: UInt32, dev: Device): CUDAStatus
  def cuCtxDestroy_v2(ctx: Context): CUDAStatus
  def cuCtxDetach(ctx: Context): CUDAStatus                                                          = unsupported
  def cuCtxDisablePeerAccess(peerContext: Context): CUDAStatus                                       = unsupported
  def cuCtxEnablePeerAccess(peerContext: Context, Flags: UInt32): CUDAStatus                         = unsupported
  def cuCtxFromGreenCtx(pContext: Pointer[Context, ReadWrite, Opaque], hCtx: CUgreenCtx): CUDAStatus = unsupported
  def cuCtxGetApiVersion(ctx: Context, version: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus      = unsupported
  def cuCtxGetCacheConfig(pconfig: Pointer[CUfunc_cache, ReadWrite, Opaque]): CUDAStatus             = unsupported
  def cuCtxGetCurrent(pctx: Pointer[Context, ReadWrite, Opaque]): CUDAStatus                         = unsupported
  def cuCtxGetDevResource(
      hCtx: Context,
      resource: Pointer[CUdevResource, ReadWrite, Opaque],
      `type`: CUdevResourceType
  ): CUDAStatus = unsupported
  def cuCtxGetDevice(device: Pointer[Device, ReadWrite, Opaque]): CUDAStatus                  = unsupported
  def cuCtxGetDevice_v2(device: Pointer[Device, ReadWrite, Opaque], ctx: Context): CUDAStatus = unsupported
  def cuCtxGetExecAffinity(
      pExecAffinity: Pointer[CUexecAffinityParam, ReadWrite, Opaque],
      `type`: CUexecAffinityType
  ): CUDAStatus = unsupported
  def cuCtxGetFlags(flags: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus                        = unsupported
  def cuCtxGetId(ctx: Context, ctxId: Pointer[unsigned_long_long, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuCtxGetLimit(pvalue: Pointer[SizeT, ReadWrite, Opaque], limit: CUlimit): CUDAStatus        = unsupported
  def cuCtxGetSharedMemConfig(pConfig: Pointer[CUsharedconfig, ReadWrite, Opaque]): CUDAStatus    = unsupported
  def cuCtxGetStreamPriorityRange(
      leastPriority: Pointer[Int, ReadWrite, Opaque],
      greatestPriority: Pointer[Int, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuCtxPopCurrent_v2(pctx: Pointer[Context, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuCtxPushCurrent_v2(ctx: Context): CUDAStatus                             = unsupported
  def cuCtxRecordEvent(hCtx: Context, hEvent: CUevent): CUDAStatus              = unsupported
  def cuCtxResetPersistingL2Cache(): CUDAStatus                                 = unsupported
  def cuCtxSetCacheConfig(config: CUfunc_cache): CUDAStatus                     = unsupported
  def cuCtxSetCurrent(ctx: Context): CUDAStatus                                 = unsupported
  def cuCtxSetFlags(flags: UInt32): CUDAStatus                                  = unsupported
  def cuCtxSetLimit(limit: CUlimit, value: SizeT): CUDAStatus                   = unsupported
  def cuCtxSetSharedMemConfig(config: CUsharedconfig): CUDAStatus               = unsupported
  def cuCtxSynchronize(): CUDAStatus
  def cuCtxSynchronize_v2(ctx: Context): CUDAStatus                       = unsupported
  def cuCtxWaitEvent(hCtx: Context, hEvent: CUevent): CUDAStatus          = unsupported
  def cuDestroyExternalMemory(extMem: CUexternalMemory): CUDAStatus       = unsupported
  def cuDestroyExternalSemaphore(extSem: CUexternalSemaphore): CUDAStatus = unsupported
  def cuDevResourceGenerateDesc(
      phDesc: Pointer[CUdevResourceDesc, ReadWrite, Opaque],
      resources: Pointer[CUdevResource, ReadWrite, Opaque],
      nbResources: UInt32
  ): CUDAStatus = unsupported
  def cuDevSmResourceSplit(
      result: Pointer[CUdevResource, ReadWrite, Opaque],
      nbGroups: UInt32,
      input: Pointer[CUdevResource, Read, Opaque],
      remainder: Pointer[CUdevResource, ReadWrite, Opaque],
      flags: UInt32,
      groupParams: Pointer[CU_DEV_SM_RESOURCE_GROUP_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuDevSmResourceSplitByCount(
      result: Pointer[CUdevResource, ReadWrite, Opaque],
      nbGroups: Pointer[UInt32, ReadWrite, Opaque],
      input: Pointer[CUdevResource, Read, Opaque],
      remainder: Pointer[CUdevResource, ReadWrite, Opaque],
      flags: UInt32,
      minCount: UInt32
  ): CUDAStatus = unsupported
  def cuDeviceCanAccessPeer(canAccessPeer: Pointer[Int, ReadWrite, Opaque], dev: Device, peerDev: Device): CUDAStatus =
    unsupported
  def cuDeviceComputeCapability(
      major: Pointer[Int, ReadWrite, Opaque],
      minor: Pointer[Int, ReadWrite, Opaque],
      dev: Device
  ): CUDAStatus = unsupported
  def cuDeviceGet(device: Out[Device], ordinal: Int): CUDAStatus
  def cuDeviceGetAttribute(pi: Pointer[Int, ReadWrite, Opaque], attrib: CUdevice_attribute, dev: Device): CUDAStatus =
    unsupported
  def cuDeviceGetByPCIBusId(dev: Pointer[Device, ReadWrite, Opaque], pciBusId: CString): CUDAStatus = unsupported
  def cuDeviceGetCount(count: Out[Int]): CUDAStatus
  def cuDeviceGetDefaultMemPool(pool_out: Pointer[CUmemoryPool, ReadWrite, Opaque], dev: Device): CUDAStatus =
    unsupported
  def cuDeviceGetDevResource(
      device: Device,
      resource: Pointer[CUdevResource, ReadWrite, Opaque],
      `type`: CUdevResourceType
  ): CUDAStatus = unsupported
  def cuDeviceGetExecAffinitySupport(
      pi: Pointer[Int, ReadWrite, Opaque],
      `type`: CUexecAffinityType,
      dev: Device
  ): CUDAStatus = unsupported
  def cuDeviceGetGraphMemAttribute(
      device: Device,
      attr: CUgraphMem_attribute,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuDeviceGetHostAtomicCapabilities(
      capabilities: Pointer[UInt32, ReadWrite, Opaque],
      operations: Pointer[CUatomicOperation, Read, Opaque],
      count: UInt32,
      dev: Device
  ): CUDAStatus = unsupported
  def cuDeviceGetLuid(
      luid: Pointer[Byte, ReadWrite, Opaque],
      deviceNodeMask: Pointer[UInt32, ReadWrite, Opaque],
      dev: Device
  ): CUDAStatus = unsupported
  def cuDeviceGetMemPool(pool: Pointer[CUmemoryPool, ReadWrite, Opaque], dev: Device): CUDAStatus = unsupported
  def cuDeviceGetName(name: Pointer[Byte, ReadWrite, Opaque], len: Int, dev: Device): CUDAStatus  = unsupported
  def cuDeviceGetNvSciSyncAttributes(
      nvSciSyncAttrList: Pointer[CVoid, ReadWrite, Opaque],
      dev: Device,
      flags: Int
  ): CUDAStatus = unsupported
  def cuDeviceGetP2PAtomicCapabilities(
      capabilities: Pointer[UInt32, ReadWrite, Opaque],
      operations: Pointer[CUatomicOperation, Read, Opaque],
      count: UInt32,
      srcDevice: Device,
      dstDevice: Device
  ): CUDAStatus = unsupported
  def cuDeviceGetP2PAttribute(
      value: Pointer[Int, ReadWrite, Opaque],
      attrib: CUdevice_P2PAttribute,
      srcDevice: Device,
      dstDevice: Device
  ): CUDAStatus = unsupported
  def cuDeviceGetPCIBusId(pciBusId: Pointer[Byte, ReadWrite, Opaque], len: Int, dev: Device): CUDAStatus = unsupported
  def cuDeviceGetProperties(prop: Pointer[CUdevprop, ReadWrite, Opaque], dev: Device): CUDAStatus        = unsupported
  def cuDeviceGetTexture1DLinearMaxWidth(
      maxWidthInElements: Pointer[SizeT, ReadWrite, Opaque],
      format: CUarray_format,
      numChannels: UInt32,
      dev: Device
  ): CUDAStatus = unsupported
  def cuDeviceGetUuid_v2(uuid: Pointer[CUuuid, ReadWrite, Opaque], dev: Device): CUDAStatus = unsupported
  def cuDeviceGraphMemTrim(device: Device): CUDAStatus                                      = unsupported
  def cuDevicePrimaryCtxGetState(
      dev: Device,
      flags: Pointer[UInt32, ReadWrite, Opaque],
      active: Pointer[Int, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuDevicePrimaryCtxRelease_v2(dev: Device): CUDAStatus                                        = unsupported
  def cuDevicePrimaryCtxReset_v2(dev: Device): CUDAStatus                                          = unsupported
  def cuDevicePrimaryCtxRetain(pctx: Pointer[Context, ReadWrite, Opaque], dev: Device): CUDAStatus = unsupported
  def cuDevicePrimaryCtxSetFlags_v2(dev: Device, flags: UInt32): CUDAStatus                        = unsupported
  def cuDeviceRegisterAsyncNotification(
      device: Device,
      callbackFunc: CUasyncCallback,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      callback: Pointer[CUasyncCallbackHandle, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuDeviceSetGraphMemAttribute(
      device: Device,
      attr: CUgraphMem_attribute,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuDeviceSetMemPool(dev: Device, pool: CUmemoryPool): CUDAStatus                                  = unsupported
  def cuDeviceTotalMem_v2(bytes: Pointer[SizeT, ReadWrite, Opaque], dev: Device): CUDAStatus           = unsupported
  def cuDeviceUnregisterAsyncNotification(device: Device, callback: CUasyncCallbackHandle): CUDAStatus = unsupported
  def cuDriverGetVersion(driverVersion: Out[Int]): CUDAStatus
  def cuEventCreate(phEvent: Pointer[CUevent, ReadWrite, Opaque], Flags: UInt32): CUDAStatus = unsupported
  def cuEventDestroy_v2(hEvent: CUevent): CUDAStatus                                         = unsupported
  def cuEventElapsedTime_v2(
      pMilliseconds: Pointer[Float, ReadWrite, Opaque],
      hStart: CUevent,
      hEnd: CUevent
  ): CUDAStatus = unsupported
  def cuEventQuery(hEvent: CUevent): CUDAStatus                                             = unsupported
  def cuEventRecord(hEvent: CUevent, hStream: CUstream): CUDAStatus                         = unsupported
  def cuEventRecordWithFlags(hEvent: CUevent, hStream: CUstream, flags: UInt32): CUDAStatus = unsupported
  def cuEventSynchronize(hEvent: CUevent): CUDAStatus                                       = unsupported
  def cuExternalMemoryGetMappedBuffer(
      devPtr: Pointer[DevicePtr, ReadWrite, Opaque],
      extMem: CUexternalMemory,
      bufferDesc: Pointer[CUDA_EXTERNAL_MEMORY_BUFFER_DESC, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuExternalMemoryGetMappedMipmappedArray(
      mipmap: Pointer[CUmipmappedArray, ReadWrite, Opaque],
      extMem: CUexternalMemory,
      mipmapDesc: Pointer[CUDA_EXTERNAL_MEMORY_MIPMAPPED_ARRAY_DESC, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuFlushGPUDirectRDMAWrites(
      target: CUflushGPUDirectRDMAWritesTarget,
      scope: CUflushGPUDirectRDMAWritesScope
  ): CUDAStatus = unsupported
  def cuFuncGetAttribute(
      pi: Pointer[Int, ReadWrite, Opaque],
      attrib: CUfunction_attribute,
      hfunc: CUDAFunction
  ): CUDAStatus = unsupported
  def cuFuncGetModule(hmod: Pointer[CUDAModule, ReadWrite, Opaque], hfunc: CUDAFunction): CUDAStatus = unsupported
  def cuFuncGetName(name: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque], hfunc: CUDAFunction): CUDAStatus =
    unsupported
  def cuFuncGetParamCount(func: CUDAFunction, paramCount: Pointer[SizeT, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuFuncGetParamInfo(
      func: CUDAFunction,
      paramIndex: SizeT,
      paramOffset: Pointer[SizeT, ReadWrite, Opaque],
      paramSize: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuFuncIsLoaded(state: Pointer[CUfunctionLoadingState, ReadWrite, Opaque], function: CUDAFunction): CUDAStatus =
    unsupported
  def cuFuncLoad(function: CUDAFunction): CUDAStatus                                                = unsupported
  def cuFuncSetAttribute(hfunc: CUDAFunction, attrib: CUfunction_attribute, value: Int): CUDAStatus = unsupported
  def cuFuncSetBlockShape(hfunc: CUDAFunction, x: Int, y: Int, z: Int): CUDAStatus                  = unsupported
  def cuFuncSetCacheConfig(hfunc: CUDAFunction, config: CUfunc_cache): CUDAStatus                   = unsupported
  def cuFuncSetSharedMemConfig(hfunc: CUDAFunction, config: CUsharedconfig): CUDAStatus             = unsupported
  def cuFuncSetSharedSize(hfunc: CUDAFunction, bytes: UInt32): CUDAStatus                           = unsupported
  def cuGetErrorName(error: CUDAStatus, pStr: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque]): CUDAStatus =
    unsupported
  def cuGetErrorString(error: CUDAStatus, pStr: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque]): CUDAStatus =
    unsupported
  def cuGetExportTable(
      ppExportTable: Pointer[Pointer[CVoid, ReadWrite, Opaque], Read, Opaque],
      pExportTableId: Pointer[CUuuid, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGetProcAddress_v2(
      symbol: CString,
      pfn: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      cudaVersion: Int,
      flags: cuuint64_t,
      symbolStatus: Pointer[CUdriverProcAddressQueryResult, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphAddBatchMemOpNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[CUDA_BATCH_MEM_OP_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphAddChildGraphNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      childGraph: CUgraph
  ): CUDAStatus = unsupported
  def cuGraphAddDependencies_v2(
      hGraph: CUgraph,
      from: Pointer[CUgraphNode, Read, Opaque],
      to: Pointer[CUgraphNode, Read, Opaque],
      edgeData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: SizeT
  ): CUDAStatus = unsupported
  def cuGraphAddEmptyNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT
  ): CUDAStatus = unsupported
  def cuGraphAddEventRecordNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      event: CUevent
  ): CUDAStatus = unsupported
  def cuGraphAddEventWaitNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      event: CUevent
  ): CUDAStatus = unsupported
  def cuGraphAddExternalSemaphoresSignalNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[CUDA_EXT_SEM_SIGNAL_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphAddExternalSemaphoresWaitNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[CUDA_EXT_SEM_WAIT_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphAddHostNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[CUDA_HOST_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphAddKernelNode_v2(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[CUDA_KERNEL_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphAddMemAllocNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[CUDA_MEM_ALLOC_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphAddMemFreeNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      dptr: DevicePtr
  ): CUDAStatus = unsupported
  def cuGraphAddMemcpyNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      copyParams: Pointer[CUDA_MEMCPY3D, Read, Opaque],
      ctx: Context
  ): CUDAStatus = unsupported
  def cuGraphAddMemsetNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      memsetParams: Pointer[CUDA_MEMSET_NODE_PARAMS, Read, Opaque],
      ctx: Context
  ): CUDAStatus = unsupported
  def cuGraphAddNode_v2(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      dependencyData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[CUgraphNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphBatchMemOpNodeGetParams(
      hNode: CUgraphNode,
      nodeParams_out: Pointer[CUDA_BATCH_MEM_OP_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphBatchMemOpNodeSetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_BATCH_MEM_OP_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphChildGraphNodeGetGraph(hNode: CUgraphNode, phGraph: Pointer[CUgraph, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuGraphClone(phGraphClone: Pointer[CUgraph, ReadWrite, Opaque], originalGraph: CUgraph): CUDAStatus = unsupported
  def cuGraphConditionalHandleCreate(
      pHandle_out: Pointer[CUgraphConditionalHandle, ReadWrite, Opaque],
      hGraph: CUgraph,
      ctx: Context,
      defaultLaunchValue: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cuGraphCreate(phGraph: Pointer[CUgraph, ReadWrite, Opaque], flags: UInt32): CUDAStatus = unsupported
  def cuGraphDebugDotPrint(hGraph: CUgraph, path: CString, flags: UInt32): CUDAStatus        = unsupported
  def cuGraphDestroy(hGraph: CUgraph): CUDAStatus                                            = unsupported
  def cuGraphDestroyNode(hNode: CUgraphNode): CUDAStatus                                     = unsupported
  def cuGraphEventRecordNodeGetEvent(hNode: CUgraphNode, event_out: Pointer[CUevent, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuGraphEventRecordNodeSetEvent(hNode: CUgraphNode, event: CUevent): CUDAStatus = unsupported
  def cuGraphEventWaitNodeGetEvent(hNode: CUgraphNode, event_out: Pointer[CUevent, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuGraphEventWaitNodeSetEvent(hNode: CUgraphNode, event: CUevent): CUDAStatus = unsupported
  def cuGraphExecBatchMemOpNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_BATCH_MEM_OP_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphExecChildGraphNodeSetParams(hGraphExec: CUgraphExec, hNode: CUgraphNode, childGraph: CUgraph): CUDAStatus =
    unsupported
  def cuGraphExecDestroy(hGraphExec: CUgraphExec): CUDAStatus = unsupported
  def cuGraphExecEventRecordNodeSetEvent(hGraphExec: CUgraphExec, hNode: CUgraphNode, event: CUevent): CUDAStatus =
    unsupported
  def cuGraphExecEventWaitNodeSetEvent(hGraphExec: CUgraphExec, hNode: CUgraphNode, event: CUevent): CUDAStatus =
    unsupported
  def cuGraphExecExternalSemaphoresSignalNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_EXT_SEM_SIGNAL_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphExecExternalSemaphoresWaitNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_EXT_SEM_WAIT_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphExecGetFlags(hGraphExec: CUgraphExec, flags: Pointer[cuuint64_t, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuGraphExecGetId(hGraphExec: CUgraphExec, graphId: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuGraphExecHostNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_HOST_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphExecKernelNodeSetParams_v2(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_KERNEL_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphExecMemcpyNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      copyParams: Pointer[CUDA_MEMCPY3D, Read, Opaque],
      ctx: Context
  ): CUDAStatus = unsupported
  def cuGraphExecMemsetNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      memsetParams: Pointer[CUDA_MEMSET_NODE_PARAMS, Read, Opaque],
      ctx: Context
  ): CUDAStatus = unsupported
  def cuGraphExecNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUgraphNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphExecUpdate_v2(
      hGraphExec: CUgraphExec,
      hGraph: CUgraph,
      resultInfo: Pointer[CUgraphExecUpdateResultInfo, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphExternalSemaphoresSignalNodeGetParams(
      hNode: CUgraphNode,
      params_out: Pointer[CUDA_EXT_SEM_SIGNAL_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphExternalSemaphoresSignalNodeSetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_EXT_SEM_SIGNAL_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphExternalSemaphoresWaitNodeGetParams(
      hNode: CUgraphNode,
      params_out: Pointer[CUDA_EXT_SEM_WAIT_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphExternalSemaphoresWaitNodeSetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_EXT_SEM_WAIT_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphGetEdges_v2(
      hGraph: CUgraph,
      from: Pointer[CUgraphNode, ReadWrite, Opaque],
      to: Pointer[CUgraphNode, ReadWrite, Opaque],
      edgeData: Pointer[CUgraphEdgeData, ReadWrite, Opaque],
      numEdges: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphGetId(hGraph: CUgraph, graphId: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuGraphGetNodes(
      hGraph: CUgraph,
      nodes: Pointer[CUgraphNode, ReadWrite, Opaque],
      numNodes: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphGetRootNodes(
      hGraph: CUgraph,
      rootNodes: Pointer[CUgraphNode, ReadWrite, Opaque],
      numRootNodes: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphHostNodeGetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_HOST_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphHostNodeSetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_HOST_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphInstantiateWithFlags(
      phGraphExec: Pointer[CUgraphExec, ReadWrite, Opaque],
      hGraph: CUgraph,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cuGraphInstantiateWithParams(
      phGraphExec: Pointer[CUgraphExec, ReadWrite, Opaque],
      hGraph: CUgraph,
      instantiateParams: Pointer[CUDA_GRAPH_INSTANTIATE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphKernelNodeCopyAttributes(dst: CUgraphNode, src: CUgraphNode): CUDAStatus = unsupported
  def cuGraphKernelNodeGetAttribute(
      hNode: CUgraphNode,
      attr: CUkernelNodeAttrID,
      value_out: Pointer[CUkernelNodeAttrValue, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphKernelNodeGetParams_v2(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_KERNEL_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphKernelNodeSetAttribute(
      hNode: CUgraphNode,
      attr: CUkernelNodeAttrID,
      value: Pointer[CUkernelNodeAttrValue, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphKernelNodeSetParams_v2(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_KERNEL_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphLaunch(hGraphExec: CUgraphExec, hStream: CUstream): CUDAStatus = unsupported
  def cuGraphMemAllocNodeGetParams(
      hNode: CUgraphNode,
      params_out: Pointer[CUDA_MEM_ALLOC_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphMemFreeNodeGetParams(hNode: CUgraphNode, dptr_out: Pointer[DevicePtr, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuGraphMemcpyNodeGetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_MEMCPY3D, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphMemcpyNodeSetParams(hNode: CUgraphNode, nodeParams: Pointer[CUDA_MEMCPY3D, Read, Opaque]): CUDAStatus =
    unsupported
  def cuGraphMemsetNodeGetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_MEMSET_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphMemsetNodeSetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_MEMSET_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphNodeFindInClone(
      phNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hOriginalNode: CUgraphNode,
      hClonedGraph: CUgraph
  ): CUDAStatus = unsupported
  def cuGraphNodeGetContainingGraph(hNode: CUgraphNode, phGraph: Pointer[CUgraph, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuGraphNodeGetDependencies_v2(
      hNode: CUgraphNode,
      dependencies: Pointer[CUgraphNode, ReadWrite, Opaque],
      edgeData: Pointer[CUgraphEdgeData, ReadWrite, Opaque],
      numDependencies: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphNodeGetDependentNodes_v2(
      hNode: CUgraphNode,
      dependentNodes: Pointer[CUgraphNode, ReadWrite, Opaque],
      edgeData: Pointer[CUgraphEdgeData, ReadWrite, Opaque],
      numDependentNodes: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphNodeGetEnabled(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      isEnabled: Pointer[UInt32, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphNodeGetLocalId(hNode: CUgraphNode, nodeId: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuGraphNodeGetParams(hNode: CUgraphNode, nodeParams: Pointer[CUgraphNodeParams, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuGraphNodeGetToolsId(
      hNode: CUgraphNode,
      toolsNodeId: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuGraphNodeGetType(hNode: CUgraphNode, `type`: Pointer[CUgraphNodeType, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuGraphNodeSetEnabled(hGraphExec: CUgraphExec, hNode: CUgraphNode, isEnabled: UInt32): CUDAStatus = unsupported
  def cuGraphNodeSetParams(hNode: CUgraphNode, nodeParams: Pointer[CUgraphNodeParams, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuGraphReleaseUserObject(graph: CUgraph, `object`: CUuserObject, count: UInt32): CUDAStatus = unsupported
  def cuGraphRemoveDependencies_v2(
      hGraph: CUgraph,
      from: Pointer[CUgraphNode, Read, Opaque],
      to: Pointer[CUgraphNode, Read, Opaque],
      edgeData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: SizeT
  ): CUDAStatus = unsupported
  def cuGraphRetainUserObject(graph: CUgraph, `object`: CUuserObject, count: UInt32, flags: UInt32): CUDAStatus =
    unsupported
  def cuGraphUpload(hGraphExec: CUgraphExec, hStream: CUstream): CUDAStatus = unsupported
  def cuGraphicsMapResources(
      count: UInt32,
      resources: Pointer[CUgraphicsResource, ReadWrite, Opaque],
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuGraphicsResourceGetMappedMipmappedArray(
      pMipmappedArray: Pointer[CUmipmappedArray, ReadWrite, Opaque],
      resource: CUgraphicsResource
  ): CUDAStatus = unsupported
  def cuGraphicsResourceGetMappedPointer_v2(
      pDevPtr: Pointer[DevicePtr, ReadWrite, Opaque],
      pSize: Pointer[SizeT, ReadWrite, Opaque],
      resource: CUgraphicsResource
  ): CUDAStatus = unsupported
  def cuGraphicsResourceSetMapFlags_v2(resource: CUgraphicsResource, flags: UInt32): CUDAStatus = unsupported
  def cuGraphicsSubResourceGetMappedArray(
      pArray: Pointer[CUarray, ReadWrite, Opaque],
      resource: CUgraphicsResource,
      arrayIndex: UInt32,
      mipLevel: UInt32
  ): CUDAStatus = unsupported
  def cuGraphicsUnmapResources(
      count: UInt32,
      resources: Pointer[CUgraphicsResource, ReadWrite, Opaque],
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuGraphicsUnregisterResource(resource: CUgraphicsResource): CUDAStatus = unsupported
  def cuGreenCtxCreate(
      phCtx: Pointer[CUgreenCtx, ReadWrite, Opaque],
      desc: CUdevResourceDesc,
      dev: Device,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cuGreenCtxDestroy(hCtx: CUgreenCtx): CUDAStatus = unsupported
  def cuGreenCtxGetDevResource(
      hCtx: CUgreenCtx,
      resource: Pointer[CUdevResource, ReadWrite, Opaque],
      `type`: CUdevResourceType
  ): CUDAStatus = unsupported
  def cuGreenCtxGetId(greenCtx: CUgreenCtx, greenCtxId: Pointer[unsigned_long_long, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuGreenCtxRecordEvent(hCtx: CUgreenCtx, hEvent: CUevent): CUDAStatus = unsupported
  def cuGreenCtxStreamCreate(
      phStream: Pointer[CUstream, ReadWrite, Opaque],
      greenCtx: CUgreenCtx,
      flags: UInt32,
      priority: Int
  ): CUDAStatus = unsupported
  def cuGreenCtxWaitEvent(hCtx: CUgreenCtx, hEvent: CUevent): CUDAStatus = unsupported
  def cuImportExternalMemory(
      extMem_out: Pointer[CUexternalMemory, ReadWrite, Opaque],
      memHandleDesc: Pointer[CUDA_EXTERNAL_MEMORY_HANDLE_DESC, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuImportExternalSemaphore(
      extSem_out: Pointer[CUexternalSemaphore, ReadWrite, Opaque],
      semHandleDesc: Pointer[CUDA_EXTERNAL_SEMAPHORE_HANDLE_DESC, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuInit(Flags: UInt32): CUDAStatus
  def cuIpcCloseMemHandle(dptr: DevicePtr): CUDAStatus = unsupported
  def cuIpcGetEventHandle(pHandle: Pointer[CUipcEventHandle, ReadWrite, Opaque], event: CUevent): CUDAStatus =
    unsupported
  def cuIpcGetMemHandle(pHandle: Pointer[CUipcMemHandle, ReadWrite, Opaque], dptr: DevicePtr): CUDAStatus = unsupported
  def cuIpcOpenEventHandle(phEvent: Pointer[CUevent, ReadWrite, Opaque], handle: CUipcEventHandle): CUDAStatus =
    unsupported
  def cuIpcOpenMemHandle_v2(
      pdptr: Pointer[DevicePtr, ReadWrite, Opaque],
      handle: CUipcMemHandle,
      Flags: UInt32
  ): CUDAStatus = unsupported
  def cuKernelGetAttribute(
      pi: Pointer[Int, ReadWrite, Opaque],
      attrib: CUfunction_attribute,
      kernel: CUkernel,
      dev: Device
  ): CUDAStatus = unsupported
  def cuKernelGetFunction(pFunc: Pointer[CUDAFunction, ReadWrite, Opaque], kernel: CUkernel): CUDAStatus = unsupported
  def cuKernelGetLibrary(pLib: Pointer[CUlibrary, ReadWrite, Opaque], kernel: CUkernel): CUDAStatus      = unsupported
  def cuKernelGetName(name: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque], hfunc: CUkernel): CUDAStatus =
    unsupported
  def cuKernelGetParamCount(kernel: CUkernel, paramCount: Pointer[SizeT, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuKernelGetParamInfo(
      kernel: CUkernel,
      paramIndex: SizeT,
      paramOffset: Pointer[SizeT, ReadWrite, Opaque],
      paramSize: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuKernelSetAttribute(attrib: CUfunction_attribute, `val`: Int, kernel: CUkernel, dev: Device): CUDAStatus =
    unsupported
  def cuKernelSetCacheConfig(kernel: CUkernel, config: CUfunc_cache, dev: Device): CUDAStatus = unsupported
  def cuLaunch(f: CUDAFunction): CUDAStatus                                                   = unsupported
  def cuLaunchCooperativeKernel(
      f: CUDAFunction,
      gridDimX: UInt32,
      gridDimY: UInt32,
      gridDimZ: UInt32,
      blockDimX: UInt32,
      blockDimY: UInt32,
      blockDimZ: UInt32,
      sharedMemBytes: UInt32,
      hStream: CUstream,
      kernelParams: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuLaunchCooperativeKernelMultiDevice(
      launchParamsList: Pointer[CUDA_LAUNCH_PARAMS, ReadWrite, Opaque],
      numDevices: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cuLaunchGrid(f: CUDAFunction, grid_width: Int, grid_height: Int): CUDAStatus                         = unsupported
  def cuLaunchGridAsync(f: CUDAFunction, grid_width: Int, grid_height: Int, hStream: CUstream): CUDAStatus = unsupported
  def cuLaunchHostFunc(hStream: Stream, fn: CUhostFn, userData: CallbackData): CUDAStatus
  def cuLaunchHostFunc_v2(
      hStream: CUstream,
      fn: CUhostFn,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      syncMode: UInt32
  ): CUDAStatus = unsupported
  def cuLaunchKernel(
      f: CUDAFunction,
      gridDimX: UInt32,
      gridDimY: UInt32,
      gridDimZ: UInt32,
      blockDimX: UInt32,
      blockDimY: UInt32,
      blockDimZ: UInt32,
      sharedMemBytes: UInt32,
      hStream: Stream,
      kernelParams: KernelParameters,
      extra: Nullable[Pointer[OpaquePointer, ReadWrite, Opaque]]
  ): CUDAStatus
  def cuLaunchKernelEx(
      config: Pointer[CUlaunchConfig, Read, Opaque],
      f: CUDAFunction,
      kernelParams: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      extra: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuLibraryEnumerateKernels(
      kernels: Pointer[CUkernel, ReadWrite, Opaque],
      numKernels: UInt32,
      lib: CUlibrary
  ): CUDAStatus = unsupported
  def cuLibraryGetGlobal(
      dptr: Pointer[DevicePtr, ReadWrite, Opaque],
      bytes: Pointer[SizeT, ReadWrite, Opaque],
      library: CUlibrary,
      name: CString
  ): CUDAStatus = unsupported
  def cuLibraryGetKernel(pKernel: Pointer[CUkernel, ReadWrite, Opaque], library: CUlibrary, name: CString): CUDAStatus =
    unsupported
  def cuLibraryGetKernelCount(count: Pointer[UInt32, ReadWrite, Opaque], lib: CUlibrary): CUDAStatus = unsupported
  def cuLibraryGetManaged(
      dptr: Pointer[DevicePtr, ReadWrite, Opaque],
      bytes: Pointer[SizeT, ReadWrite, Opaque],
      library: CUlibrary,
      name: CString
  ): CUDAStatus = unsupported
  def cuLibraryGetModule(pMod: Pointer[CUDAModule, ReadWrite, Opaque], library: CUlibrary): CUDAStatus = unsupported
  def cuLibraryGetUnifiedFunction(
      fptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      library: CUlibrary,
      symbol: CString
  ): CUDAStatus = unsupported
  def cuLibraryLoadData(
      library: Pointer[CUlibrary, ReadWrite, Opaque],
      code: Pointer[CVoid, Read, Opaque],
      jitOptions: Pointer[CUjit_option, ReadWrite, Opaque],
      jitOptionsValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      numJitOptions: UInt32,
      libraryOptions: Pointer[CUlibraryOption, ReadWrite, Opaque],
      libraryOptionValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      numLibraryOptions: UInt32
  ): CUDAStatus = unsupported
  def cuLibraryLoadFromFile(
      library: Pointer[CUlibrary, ReadWrite, Opaque],
      fileName: CString,
      jitOptions: Pointer[CUjit_option, ReadWrite, Opaque],
      jitOptionsValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      numJitOptions: UInt32,
      libraryOptions: Pointer[CUlibraryOption, ReadWrite, Opaque],
      libraryOptionValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      numLibraryOptions: UInt32
  ): CUDAStatus = unsupported
  def cuLibraryUnload(library: CUlibrary): CUDAStatus = unsupported
  def cuLinkAddData_v2(
      state: CUlinkState,
      `type`: CUjitInputType,
      data: Pointer[CVoid, ReadWrite, Opaque],
      size: SizeT,
      name: CString,
      numOptions: UInt32,
      options: Pointer[CUjit_option, ReadWrite, Opaque],
      optionValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuLinkAddFile_v2(
      state: CUlinkState,
      `type`: CUjitInputType,
      path: CString,
      numOptions: UInt32,
      options: Pointer[CUjit_option, ReadWrite, Opaque],
      optionValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuLinkComplete(
      state: CUlinkState,
      cubinOut: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      sizeOut: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuLinkCreate_v2(
      numOptions: UInt32,
      options: Pointer[CUjit_option, ReadWrite, Opaque],
      optionValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      stateOut: Pointer[CUlinkState, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuLinkDestroy(state: CUlinkState): CUDAStatus                                  = unsupported
  def cuLogicalEndpointAddDevice(leId: CUlogicalEndpointId, dev: Device): CUDAStatus = unsupported
  def cuLogicalEndpointBindAddr(
      leId: CUlogicalEndpointId,
      dev: Device,
      offset: cuuint64_t,
      ptr: Pointer[CVoid, ReadWrite, Opaque],
      size: cuuint64_t,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cuLogicalEndpointBindMem(
      leId: CUlogicalEndpointId,
      dev: Device,
      offset: cuuint64_t,
      memHandle: CUmemGenericAllocationHandle,
      memOffset: cuuint64_t,
      size: cuuint64_t,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cuLogicalEndpointCreate(
      leId: CUlogicalEndpointId,
      prop: Pointer[CUlogicalEndpointProp, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuLogicalEndpointDestroy(leId: CUlogicalEndpointId): CUDAStatus = unsupported
  def cuLogicalEndpointExport(
      handle: Pointer[CVoid, ReadWrite, Opaque],
      leId: CUlogicalEndpointId,
      handleType: CUlogicalEndpointIpcHandleType
  ): CUDAStatus = unsupported
  def cuLogicalEndpointGetLimits(
      bindAlignment: Pointer[cuuint64_t, ReadWrite, Opaque],
      maxSize: Pointer[cuuint64_t, ReadWrite, Opaque],
      prop: Pointer[CUlogicalEndpointProp, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuLogicalEndpointIdRelease(baseLeId: CUlogicalEndpointId, count: cuuint32_t): CUDAStatus = unsupported
  def cuLogicalEndpointIdReserve(
      baseLeId: Pointer[CUlogicalEndpointId, ReadWrite, Opaque],
      count: cuuint32_t
  ): CUDAStatus = unsupported
  def cuLogicalEndpointImport(
      leId: CUlogicalEndpointId,
      handle: Pointer[CVoid, Read, Opaque],
      handleType: CUlogicalEndpointIpcHandleType
  ): CUDAStatus = unsupported
  def cuLogicalEndpointQuery(
      leId: CUlogicalEndpointId,
      count: cuuint32_t,
      queryStatus: Pointer[Int, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuLogicalEndpointUnbind(
      leId: CUlogicalEndpointId,
      dev: Device,
      offset: cuuint64_t,
      size: cuuint64_t
  ): CUDAStatus = unsupported
  def cuLogsCurrent(iterator_out: Pointer[CUlogIterator, ReadWrite, Opaque], flags: UInt32): CUDAStatus = unsupported
  def cuLogsDumpToFile(
      iterator: Pointer[CUlogIterator, ReadWrite, Opaque],
      pathToFile: CString,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cuLogsDumpToMemory(
      iterator: Pointer[CUlogIterator, ReadWrite, Opaque],
      buffer: Pointer[Byte, ReadWrite, Opaque],
      size: Pointer[SizeT, ReadWrite, Opaque],
      flags: UInt32
  ): CUDAStatus = unsupported
  def cuLogsRegisterCallback(
      callbackFunc: CUlogsCallback,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      callback_out: Pointer[CUlogsCallbackHandle, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuLogsUnregisterCallback(callback: CUlogsCallbackHandle): CUDAStatus = unsupported
  def cuMemAddressFree(ptr: DevicePtr, size: SizeT): CUDAStatus            = unsupported
  def cuMemAddressReserve(
      ptr: Pointer[DevicePtr, ReadWrite, Opaque],
      size: SizeT,
      alignment: SizeT,
      addr: DevicePtr,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cuMemAdvise_v2(devPtr: DevicePtr, count: SizeT, advice: CUmem_advise, location: CUmemLocation): CUDAStatus =
    unsupported
  def cuMemAllocAsync(dptr: Pointer[DevicePtr, ReadWrite, Opaque], bytesize: SizeT, hStream: CUstream): CUDAStatus =
    unsupported
  def cuMemAllocFromPoolAsync(
      dptr: Pointer[DevicePtr, ReadWrite, Opaque],
      bytesize: SizeT,
      pool: CUmemoryPool,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemAllocHost_v2(
      pp: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      bytesize: SizeT
  ): CUDAStatus = unsupported
  def cuMemAllocManaged(dptr: Pointer[DevicePtr, ReadWrite, Opaque], bytesize: SizeT, flags: UInt32): CUDAStatus =
    unsupported
  def cuMemAllocPitch_v2(
      dptr: Pointer[DevicePtr, ReadWrite, Opaque],
      pPitch: Pointer[SizeT, ReadWrite, Opaque],
      WidthInBytes: SizeT,
      Height: SizeT,
      ElementSizeBytes: UInt32
  ): CUDAStatus = unsupported
  def cuMemAlloc_v2(dptr: Out[DevicePtr], bytesize: SizeT): CUDAStatus
  def cuMemBatchDecompressAsync(
      paramsArray: Pointer[CUmemDecompressParams, ReadWrite, Opaque],
      count: SizeT,
      flags: UInt32,
      errorIndex: Pointer[SizeT, ReadWrite, Opaque],
      stream: CUstream
  ): CUDAStatus = unsupported
  def cuMemCreate(
      handle: Pointer[CUmemGenericAllocationHandle, ReadWrite, Opaque],
      size: SizeT,
      prop: Pointer[CUmemAllocationProp, Read, Opaque],
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cuMemDiscardAndPrefetchBatchAsync(
      dptrs: Pointer[DevicePtr, ReadWrite, Opaque],
      sizes: Pointer[SizeT, ReadWrite, Opaque],
      count: SizeT,
      prefetchLocs: Pointer[CUmemLocation, ReadWrite, Opaque],
      prefetchLocIdxs: Pointer[SizeT, ReadWrite, Opaque],
      numPrefetchLocs: SizeT,
      flags: unsigned_long_long,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemDiscardBatchAsync(
      dptrs: Pointer[DevicePtr, ReadWrite, Opaque],
      sizes: Pointer[SizeT, ReadWrite, Opaque],
      count: SizeT,
      flags: unsigned_long_long,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemExportToShareableHandle(
      shareableHandle: Pointer[CVoid, ReadWrite, Opaque],
      handle: CUmemGenericAllocationHandle,
      handleType: CUmemAllocationHandleType,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cuMemFreeAsync(dptr: DevicePtr, hStream: CUstream): CUDAStatus  = unsupported
  def cuMemFreeHost(p: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuMemFree_v2(dptr: DevicePtr): CUDAStatus
  def cuMemGetAccess(
      flags: Pointer[unsigned_long_long, ReadWrite, Opaque],
      location: Pointer[CUmemLocation, Read, Opaque],
      ptr: DevicePtr
  ): CUDAStatus = unsupported
  def cuMemGetAddressRange_v2(
      pbase: Pointer[DevicePtr, ReadWrite, Opaque],
      psize: Pointer[SizeT, ReadWrite, Opaque],
      dptr: DevicePtr
  ): CUDAStatus = unsupported
  def cuMemGetAllocationGranularity(
      granularity: Pointer[SizeT, ReadWrite, Opaque],
      prop: Pointer[CUmemAllocationProp, Read, Opaque],
      option: CUmemAllocationGranularity_flags
  ): CUDAStatus = unsupported
  def cuMemGetAllocationPropertiesFromHandle(
      prop: Pointer[CUmemAllocationProp, ReadWrite, Opaque],
      handle: CUmemGenericAllocationHandle
  ): CUDAStatus = unsupported
  def cuMemGetDefaultMemPool(
      pool_out: Pointer[CUmemoryPool, ReadWrite, Opaque],
      location: Pointer[CUmemLocation, ReadWrite, Opaque],
      `type`: CUmemAllocationType
  ): CUDAStatus = unsupported
  def cuMemGetHandleForAddressRange(
      handle: Pointer[CVoid, ReadWrite, Opaque],
      dptr: DevicePtr,
      size: SizeT,
      handleType: CUmemRangeHandleType,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cuMemGetInfo_v2(free: Pointer[SizeT, ReadWrite, Opaque], total: Pointer[SizeT, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuMemGetMemPool(
      pool: Pointer[CUmemoryPool, ReadWrite, Opaque],
      location: Pointer[CUmemLocation, ReadWrite, Opaque],
      `type`: CUmemAllocationType
  ): CUDAStatus = unsupported
  def cuMemHostAlloc(
      pp: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      bytesize: SizeT,
      Flags: UInt32
  ): CUDAStatus = unsupported
  def cuMemHostGetDevicePointer_v2(
      pdptr: Pointer[DevicePtr, ReadWrite, Opaque],
      p: Pointer[CVoid, ReadWrite, Opaque],
      Flags: UInt32
  ): CUDAStatus = unsupported
  def cuMemHostGetFlags(pFlags: Pointer[UInt32, ReadWrite, Opaque], p: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuMemHostRegister_v2(p: Pointer[CVoid, ReadWrite, Opaque], bytesize: SizeT, Flags: UInt32): CUDAStatus =
    unsupported
  def cuMemHostUnregister(p: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuMemImportFromShareableHandle(
      handle: Pointer[CUmemGenericAllocationHandle, ReadWrite, Opaque],
      osHandle: Pointer[CVoid, ReadWrite, Opaque],
      shHandleType: CUmemAllocationHandleType
  ): CUDAStatus = unsupported
  def cuMemMap(
      ptr: DevicePtr,
      size: SizeT,
      offset: SizeT,
      handle: CUmemGenericAllocationHandle,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cuMemMapArrayAsync(
      mapInfoList: Pointer[CUarrayMapInfo, ReadWrite, Opaque],
      count: UInt32,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemPoolCreate(
      pool: Pointer[CUmemoryPool, ReadWrite, Opaque],
      poolProps: Pointer[CUmemPoolProps, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuMemPoolDestroy(pool: CUmemoryPool): CUDAStatus = unsupported
  def cuMemPoolExportPointer(
      shareData_out: Pointer[CUmemPoolPtrExportData, ReadWrite, Opaque],
      ptr: DevicePtr
  ): CUDAStatus = unsupported
  def cuMemPoolExportToShareableHandle(
      handle_out: Pointer[CVoid, ReadWrite, Opaque],
      pool: CUmemoryPool,
      handleType: CUmemAllocationHandleType,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cuMemPoolGetAccess(
      flags: Pointer[CUmemAccess_flags, ReadWrite, Opaque],
      memPool: CUmemoryPool,
      location: Pointer[CUmemLocation, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuMemPoolGetAttribute(
      pool: CUmemoryPool,
      attr: CUmemPool_attribute,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuMemPoolImportFromShareableHandle(
      pool_out: Pointer[CUmemoryPool, ReadWrite, Opaque],
      handle: Pointer[CVoid, ReadWrite, Opaque],
      handleType: CUmemAllocationHandleType,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cuMemPoolImportPointer(
      ptr_out: Pointer[DevicePtr, ReadWrite, Opaque],
      pool: CUmemoryPool,
      shareData: Pointer[CUmemPoolPtrExportData, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuMemPoolSetAccess(pool: CUmemoryPool, map: Pointer[CUmemAccessDesc, Read, Opaque], count: SizeT): CUDAStatus =
    unsupported
  def cuMemPoolSetAttribute(
      pool: CUmemoryPool,
      attr: CUmemPool_attribute,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuMemPoolTrimTo(pool: CUmemoryPool, minBytesToKeep: SizeT): CUDAStatus = unsupported
  def cuMemPrefetchAsync_v2(
      devPtr: DevicePtr,
      count: SizeT,
      location: CUmemLocation,
      flags: UInt32,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemPrefetchBatchAsync(
      dptrs: Pointer[DevicePtr, ReadWrite, Opaque],
      sizes: Pointer[SizeT, ReadWrite, Opaque],
      count: SizeT,
      prefetchLocs: Pointer[CUmemLocation, ReadWrite, Opaque],
      prefetchLocIdxs: Pointer[SizeT, ReadWrite, Opaque],
      numPrefetchLocs: SizeT,
      flags: unsigned_long_long,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemRangeGetAttribute(
      data: Pointer[CVoid, ReadWrite, Opaque],
      dataSize: SizeT,
      attribute: CUmem_range_attribute,
      devPtr: DevicePtr,
      count: SizeT
  ): CUDAStatus = unsupported
  def cuMemRangeGetAttributes(
      data: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      dataSizes: Pointer[SizeT, ReadWrite, Opaque],
      attributes: Pointer[CUmem_range_attribute, ReadWrite, Opaque],
      numAttributes: SizeT,
      devPtr: DevicePtr,
      count: SizeT
  ): CUDAStatus = unsupported
  def cuMemRelease(handle: CUmemGenericAllocationHandle): CUDAStatus = unsupported
  def cuMemRetainAllocationHandle(
      handle: Pointer[CUmemGenericAllocationHandle, ReadWrite, Opaque],
      addr: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuMemSetAccess(
      ptr: DevicePtr,
      size: SizeT,
      desc: Pointer[CUmemAccessDesc, Read, Opaque],
      count: SizeT
  ): CUDAStatus = unsupported
  def cuMemSetMemPool(
      location: Pointer[CUmemLocation, ReadWrite, Opaque],
      `type`: CUmemAllocationType,
      pool: CUmemoryPool
  ): CUDAStatus = unsupported
  def cuMemUnmap(ptr: DevicePtr, size: SizeT): CUDAStatus                                            = unsupported
  def cuMemcpy(dst: DevicePtr, src: DevicePtr, ByteCount: SizeT): CUDAStatus                         = unsupported
  def cuMemcpy2DAsync_v2(pCopy: Pointer[CUDA_MEMCPY2D, Read, Opaque], hStream: CUstream): CUDAStatus = unsupported
  def cuMemcpy2DUnaligned_v2(pCopy: Pointer[CUDA_MEMCPY2D, Read, Opaque]): CUDAStatus                = unsupported
  def cuMemcpy2D_v2(pCopy: Pointer[CUDA_MEMCPY2D, Read, Opaque]): CUDAStatus                         = unsupported
  def cuMemcpy3DAsync_v2(pCopy: Pointer[CUDA_MEMCPY3D, Read, Opaque], hStream: CUstream): CUDAStatus = unsupported
  def cuMemcpy3DBatchAsync_v2(
      numOps: SizeT,
      opList: Pointer[CUDA_MEMCPY3D_BATCH_OP, ReadWrite, Opaque],
      flags: unsigned_long_long,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemcpy3DPeer(pCopy: Pointer[CUDA_MEMCPY3D_PEER, Read, Opaque]): CUDAStatus                         = unsupported
  def cuMemcpy3DPeerAsync(pCopy: Pointer[CUDA_MEMCPY3D_PEER, Read, Opaque], hStream: CUstream): CUDAStatus = unsupported
  def cuMemcpy3DWithAttributesAsync(
      op: Pointer[CUDA_MEMCPY3D_BATCH_OP, ReadWrite, Opaque],
      flags: unsigned_long_long,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemcpy3D_v2(pCopy: Pointer[CUDA_MEMCPY3D, Read, Opaque]): CUDAStatus                         = unsupported
  def cuMemcpyAsync(dst: DevicePtr, src: DevicePtr, ByteCount: SizeT, hStream: CUstream): CUDAStatus = unsupported
  def cuMemcpyAtoA_v2(
      dstArray: CUarray,
      dstOffset: SizeT,
      srcArray: CUarray,
      srcOffset: SizeT,
      ByteCount: SizeT
  ): CUDAStatus = unsupported
  def cuMemcpyAtoD_v2(dstDevice: DevicePtr, srcArray: CUarray, srcOffset: SizeT, ByteCount: SizeT): CUDAStatus =
    unsupported
  def cuMemcpyAtoHAsync_v2(
      dstHost: Pointer[CVoid, ReadWrite, Opaque],
      srcArray: CUarray,
      srcOffset: SizeT,
      ByteCount: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemcpyAtoH_v2(
      dstHost: Pointer[CVoid, ReadWrite, Opaque],
      srcArray: CUarray,
      srcOffset: SizeT,
      ByteCount: SizeT
  ): CUDAStatus = unsupported
  def cuMemcpyBatchAsync_v2(
      dsts: Pointer[DevicePtr, ReadWrite, Opaque],
      srcs: Pointer[DevicePtr, ReadWrite, Opaque],
      sizes: Pointer[SizeT, ReadWrite, Opaque],
      count: SizeT,
      attrs: Pointer[CUmemcpyAttributes, ReadWrite, Opaque],
      attrsIdxs: Pointer[SizeT, ReadWrite, Opaque],
      numAttrs: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemcpyDtoA_v2(dstArray: CUarray, dstOffset: SizeT, srcDevice: DevicePtr, ByteCount: SizeT): CUDAStatus =
    unsupported
  def cuMemcpyDtoDAsync_v2(
      dstDevice: DevicePtr,
      srcDevice: DevicePtr,
      ByteCount: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemcpyDtoD_v2(dstDevice: DevicePtr, srcDevice: DevicePtr, ByteCount: SizeT): CUDAStatus
  def cuMemcpyDtoHAsync_v2(
      dstHost: Pointer[CVoid, ReadWrite, Opaque],
      srcDevice: DevicePtr,
      ByteCount: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemcpyDtoH_v2(dstHost: OutputBuffer[Byte, "ByteCount"], srcDevice: DevicePtr, ByteCount: SizeT): CUDAStatus
  def cuMemcpyHtoAAsync_v2(
      dstArray: CUarray,
      dstOffset: SizeT,
      srcHost: Pointer[CVoid, Read, Opaque],
      ByteCount: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemcpyHtoA_v2(
      dstArray: CUarray,
      dstOffset: SizeT,
      srcHost: Pointer[CVoid, Read, Opaque],
      ByteCount: SizeT
  ): CUDAStatus = unsupported
  def cuMemcpyHtoDAsync_v2(
      dstDevice: DevicePtr,
      srcHost: Pointer[CVoid, Read, Opaque],
      ByteCount: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemcpyHtoD_v2(dstDevice: DevicePtr, srcHost: InputBuffer[Byte, "ByteCount"], ByteCount: SizeT): CUDAStatus
  def cuMemcpyPeer(
      dstDevice: DevicePtr,
      dstContext: Context,
      srcDevice: DevicePtr,
      srcContext: Context,
      ByteCount: SizeT
  ): CUDAStatus = unsupported
  def cuMemcpyPeerAsync(
      dstDevice: DevicePtr,
      dstContext: Context,
      srcDevice: DevicePtr,
      srcContext: Context,
      ByteCount: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemcpyWithAttributesAsync(
      dst: DevicePtr,
      src: DevicePtr,
      size: SizeT,
      attr: Pointer[CUmemcpyAttributes, ReadWrite, Opaque],
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemsetD16Async(dstDevice: DevicePtr, us: unsigned_short, N: SizeT, hStream: CUstream): CUDAStatus = unsupported
  def cuMemsetD16_v2(dstDevice: DevicePtr, us: unsigned_short, N: SizeT): CUDAStatus                      = unsupported
  def cuMemsetD2D16Async(
      dstDevice: DevicePtr,
      dstPitch: SizeT,
      us: unsigned_short,
      Width: SizeT,
      Height: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemsetD2D16_v2(
      dstDevice: DevicePtr,
      dstPitch: SizeT,
      us: unsigned_short,
      Width: SizeT,
      Height: SizeT
  ): CUDAStatus = unsupported
  def cuMemsetD2D32Async(
      dstDevice: DevicePtr,
      dstPitch: SizeT,
      ui: UInt32,
      Width: SizeT,
      Height: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemsetD2D32_v2(dstDevice: DevicePtr, dstPitch: SizeT, ui: UInt32, Width: SizeT, Height: SizeT): CUDAStatus =
    unsupported
  def cuMemsetD2D8Async(
      dstDevice: DevicePtr,
      dstPitch: SizeT,
      uc: UInt8,
      Width: SizeT,
      Height: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  def cuMemsetD2D8_v2(dstDevice: DevicePtr, dstPitch: SizeT, uc: UInt8, Width: SizeT, Height: SizeT): CUDAStatus =
    unsupported
  def cuMemsetD32Async(dstDevice: DevicePtr, ui: UInt32, N: SizeT, hStream: CUstream): CUDAStatus = unsupported
  def cuMemsetD32_v2(dstDevice: DevicePtr, ui: UInt32, N: SizeT): CUDAStatus                      = unsupported
  def cuMemsetD8Async(dstDevice: DevicePtr, uc: UInt8, N: SizeT, hStream: CUstream): CUDAStatus   = unsupported
  def cuMemsetD8_v2(dstDevice: DevicePtr, uc: UInt8, N: SizeT): CUDAStatus
  def cuMipmappedArrayCreate(
      pHandle: Pointer[CUmipmappedArray, ReadWrite, Opaque],
      pMipmappedArrayDesc: Pointer[CUDA_ARRAY3D_DESCRIPTOR, Read, Opaque],
      numMipmapLevels: UInt32
  ): CUDAStatus = unsupported
  def cuMipmappedArrayDestroy(hMipmappedArray: CUmipmappedArray): CUDAStatus = unsupported
  def cuMipmappedArrayGetLevel(
      pLevelArray: Pointer[CUarray, ReadWrite, Opaque],
      hMipmappedArray: CUmipmappedArray,
      level: UInt32
  ): CUDAStatus = unsupported
  def cuMipmappedArrayGetMemoryRequirements(
      memoryRequirements: Pointer[CUDA_ARRAY_MEMORY_REQUIREMENTS, ReadWrite, Opaque],
      mipmap: CUmipmappedArray,
      device: Device
  ): CUDAStatus = unsupported
  def cuMipmappedArrayGetSparseProperties(
      sparseProperties: Pointer[CUDA_ARRAY_SPARSE_PROPERTIES, ReadWrite, Opaque],
      mipmap: CUmipmappedArray
  ): CUDAStatus = unsupported
  def cuModuleEnumerateFunctions(
      functions: Pointer[CUDAFunction, ReadWrite, Opaque],
      numFunctions: UInt32,
      mod: CUDAModule
  ): CUDAStatus = unsupported
  def cuModuleGetFunction(hfunc: Out[KernelFunction], hmod: CUDAModule, name: FunctionName): CUDAStatus
  def cuModuleGetFunctionCount(count: Pointer[UInt32, ReadWrite, Opaque], mod: CUDAModule): CUDAStatus = unsupported
  def cuModuleGetGlobal_v2(
      dptr: Pointer[DevicePtr, ReadWrite, Opaque],
      bytes: Pointer[SizeT, ReadWrite, Opaque],
      hmod: CUDAModule,
      name: CString
  ): CUDAStatus = unsupported
  def cuModuleGetLoadingMode(mode: Pointer[CUmoduleLoadingMode, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuModuleGetSurfRef(pSurfRef: Pointer[CUsurfref, ReadWrite, Opaque], hmod: CUDAModule, name: CString): CUDAStatus =
    unsupported
  def cuModuleGetTexRef(pTexRef: Pointer[CUtexref, ReadWrite, Opaque], hmod: CUDAModule, name: CString): CUDAStatus =
    unsupported
  def cuModuleLoad(module: Pointer[CUDAModule, ReadWrite, Opaque], fname: CString): CUDAStatus = unsupported
  def cuModuleLoadData(module: Out[CUDAModule], image: ModuleImage): CUDAStatus
  def cuModuleLoadDataEx(
      module: Pointer[CUDAModule, ReadWrite, Opaque],
      image: Pointer[CVoid, Read, Opaque],
      numOptions: UInt32,
      options: Pointer[CUjit_option, ReadWrite, Opaque],
      optionValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuModuleLoadFatBinary(
      module: Pointer[CUDAModule, ReadWrite, Opaque],
      fatCubin: Pointer[CVoid, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuModuleUnload(hmod: CUDAModule): CUDAStatus
  def cuMulticastAddDevice(mcHandle: CUmemGenericAllocationHandle, dev: Device): CUDAStatus = unsupported
  def cuMulticastBindAddr(
      mcHandle: CUmemGenericAllocationHandle,
      mcOffset: SizeT,
      memptr: DevicePtr,
      size: SizeT,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cuMulticastBindAddr_v2(
      mcHandle: CUmemGenericAllocationHandle,
      dev: Device,
      mcOffset: SizeT,
      memptr: DevicePtr,
      size: SizeT,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cuMulticastBindMem(
      mcHandle: CUmemGenericAllocationHandle,
      mcOffset: SizeT,
      memHandle: CUmemGenericAllocationHandle,
      memOffset: SizeT,
      size: SizeT,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cuMulticastBindMem_v2(
      mcHandle: CUmemGenericAllocationHandle,
      dev: Device,
      mcOffset: SizeT,
      memHandle: CUmemGenericAllocationHandle,
      memOffset: SizeT,
      size: SizeT,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cuMulticastCreate(
      mcHandle: Pointer[CUmemGenericAllocationHandle, ReadWrite, Opaque],
      prop: Pointer[CUmulticastObjectProp, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuMulticastGetGranularity(
      granularity: Pointer[SizeT, ReadWrite, Opaque],
      prop: Pointer[CUmulticastObjectProp, Read, Opaque],
      option: CUmulticastGranularity_flags
  ): CUDAStatus = unsupported
  def cuMulticastUnbind(mcHandle: CUmemGenericAllocationHandle, dev: Device, mcOffset: SizeT, size: SizeT): CUDAStatus =
    unsupported
  def cuOccupancyAvailableDynamicSMemPerBlock(
      dynamicSmemSize: Pointer[SizeT, ReadWrite, Opaque],
      func: CUDAFunction,
      numBlocks: Int,
      blockSize: Int
  ): CUDAStatus = unsupported
  def cuOccupancyMaxActiveBlocksPerMultiprocessor(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      func: CUDAFunction,
      blockSize: Int,
      dynamicSMemSize: SizeT
  ): CUDAStatus = unsupported
  def cuOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      func: CUDAFunction,
      blockSize: Int,
      dynamicSMemSize: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cuOccupancyMaxActiveClusters(
      numClusters: Pointer[Int, ReadWrite, Opaque],
      func: CUDAFunction,
      config: Pointer[CUlaunchConfig, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuOccupancyMaxPotentialBlockSize(
      minGridSize: Pointer[Int, ReadWrite, Opaque],
      blockSize: Pointer[Int, ReadWrite, Opaque],
      func: CUDAFunction,
      blockSizeToDynamicSMemSize: CUoccupancyB2DSize,
      dynamicSMemSize: SizeT,
      blockSizeLimit: Int
  ): CUDAStatus = unsupported
  def cuOccupancyMaxPotentialBlockSizeWithFlags(
      minGridSize: Pointer[Int, ReadWrite, Opaque],
      blockSize: Pointer[Int, ReadWrite, Opaque],
      func: CUDAFunction,
      blockSizeToDynamicSMemSize: CUoccupancyB2DSize,
      dynamicSMemSize: SizeT,
      blockSizeLimit: Int,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cuOccupancyMaxPotentialClusterSize(
      clusterSize: Pointer[Int, ReadWrite, Opaque],
      func: CUDAFunction,
      config: Pointer[CUlaunchConfig, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuParamSetSize(hfunc: CUDAFunction, numbytes: UInt32): CUDAStatus                  = unsupported
  def cuParamSetTexRef(hfunc: CUDAFunction, texunit: Int, hTexRef: CUtexref): CUDAStatus = unsupported
  def cuParamSetf(hfunc: CUDAFunction, offset: Int, value: Float): CUDAStatus            = unsupported
  def cuParamSeti(hfunc: CUDAFunction, offset: Int, value: UInt32): CUDAStatus           = unsupported
  def cuParamSetv(
      hfunc: CUDAFunction,
      offset: Int,
      ptr: Pointer[CVoid, ReadWrite, Opaque],
      numbytes: UInt32
  ): CUDAStatus = unsupported
  def cuPointerGetAttribute(
      data: Pointer[CVoid, ReadWrite, Opaque],
      attribute: CUpointer_attribute,
      ptr: DevicePtr
  ): CUDAStatus = unsupported
  def cuPointerGetAttributes(
      numAttributes: UInt32,
      attributes: Pointer[CUpointer_attribute, ReadWrite, Opaque],
      data: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      ptr: DevicePtr
  ): CUDAStatus = unsupported
  def cuPointerSetAttribute(
      value: Pointer[CVoid, Read, Opaque],
      attribute: CUpointer_attribute,
      ptr: DevicePtr
  ): CUDAStatus = unsupported
  def cuSignalExternalSemaphoresAsync(
      extSemArray: Pointer[CUexternalSemaphore, Read, Opaque],
      paramsArray: Pointer[CUDA_EXTERNAL_SEMAPHORE_SIGNAL_PARAMS, Read, Opaque],
      numExtSems: UInt32,
      stream: CUstream
  ): CUDAStatus = unsupported
  def cuStreamAddCallback(
      hStream: Stream,
      callback: CUstreamCallback,
      userData: CallbackData,
      flags: UInt32
  ): CUDAStatus
  def cuStreamAttachMemAsync(hStream: CUstream, dptr: DevicePtr, length: SizeT, flags: UInt32): CUDAStatus = unsupported
  def cuStreamBatchMemOp_v2(
      stream: CUstream,
      count: UInt32,
      paramArray: Pointer[CUstreamBatchMemOpParams, ReadWrite, Opaque],
      flags: UInt32
  ): CUDAStatus = unsupported
  def cuStreamBeginCaptureToCig(
      hStream: CUstream,
      streamCigCaptureParams: Pointer[CUstreamCigCaptureParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuStreamBeginCaptureToGraph(
      hStream: CUstream,
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      dependencyData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: SizeT,
      mode: CUstreamCaptureMode
  ): CUDAStatus = unsupported
  def cuStreamBeginCapture_v2(hStream: CUstream, mode: CUstreamCaptureMode): CUDAStatus = unsupported
  def cuStreamBeginRecaptureToGraph(
      hStream: CUstream,
      mode: CUstreamCaptureMode,
      hGraph: CUgraph,
      callbackFunc: CUgraphRecaptureCallback,
      userData: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuStreamCopyAttributes(dst: CUstream, src: CUstream): CUDAStatus                          = unsupported
  def cuStreamCreate(phStream: Pointer[CUstream, ReadWrite, Opaque], Flags: UInt32): CUDAStatus = unsupported
  def cuStreamCreateWithPriority(
      phStream: Pointer[CUstream, ReadWrite, Opaque],
      flags: UInt32,
      priority: Int
  ): CUDAStatus = unsupported
  def cuStreamDestroy_v2(hStream: CUstream): CUDAStatus                                               = unsupported
  def cuStreamEndCapture(hStream: CUstream, phGraph: Pointer[CUgraph, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuStreamEndCaptureToCig(hStream: CUstream): CUDAStatus                                          = unsupported
  def cuStreamGetAttribute(
      hStream: CUstream,
      attr: CUstreamAttrID,
      value_out: Pointer[CUstreamAttrValue, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuStreamGetCaptureInfo_v3(
      hStream: CUstream,
      captureStatus_out: Pointer[CUstreamCaptureStatus, ReadWrite, Opaque],
      id_out: Pointer[cuuint64_t, ReadWrite, Opaque],
      graph_out: Pointer[CUgraph, ReadWrite, Opaque],
      dependencies_out: Pointer[Pointer[CUgraphNode, ReadWrite, Opaque], Read, Opaque],
      edgeData_out: Pointer[Pointer[CUgraphEdgeData, ReadWrite, Opaque], Read, Opaque],
      numDependencies_out: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuStreamGetCtx(hStream: CUstream, pctx: Pointer[Context, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuStreamGetCtx_v2(
      hStream: CUstream,
      pCtx: Pointer[Context, ReadWrite, Opaque],
      pGreenCtx: Pointer[CUgreenCtx, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuStreamGetDevResource(
      hStream: CUstream,
      resource: Pointer[CUdevResource, ReadWrite, Opaque],
      `type`: CUdevResourceType
  ): CUDAStatus = unsupported
  def cuStreamGetDevice(hStream: CUstream, device: Pointer[Device, ReadWrite, Opaque]): CUDAStatus      = unsupported
  def cuStreamGetFlags(hStream: CUstream, flags: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus        = unsupported
  def cuStreamGetGreenCtx(hStream: CUstream, phCtx: Pointer[CUgreenCtx, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuStreamGetId(hStream: CUstream, streamId: Pointer[unsigned_long_long, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuStreamGetPriority(hStream: CUstream, priority: Pointer[Int, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuStreamIsCapturing(
      hStream: CUstream,
      captureStatus: Pointer[CUstreamCaptureStatus, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuStreamQuery(hStream: CUstream): CUDAStatus = unsupported
  def cuStreamSetAttribute(
      hStream: CUstream,
      attr: CUstreamAttrID,
      value: Pointer[CUstreamAttrValue, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuStreamSynchronize(hStream: CUstream): CUDAStatus = unsupported
  def cuStreamUpdateCaptureDependencies_v2(
      hStream: CUstream,
      dependencies: Pointer[CUgraphNode, ReadWrite, Opaque],
      dependencyData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cuStreamWaitEvent(hStream: CUstream, hEvent: CUevent, Flags: UInt32): CUDAStatus = unsupported
  def cuStreamWaitValue32_v2(stream: CUstream, addr: DevicePtr, value: cuuint32_t, flags: UInt32): CUDAStatus =
    unsupported
  def cuStreamWaitValue64_v2(stream: CUstream, addr: DevicePtr, value: cuuint64_t, flags: UInt32): CUDAStatus =
    unsupported
  def cuStreamWriteValue32_v2(stream: CUstream, addr: DevicePtr, value: cuuint32_t, flags: UInt32): CUDAStatus =
    unsupported
  def cuStreamWriteValue64_v2(stream: CUstream, addr: DevicePtr, value: cuuint64_t, flags: UInt32): CUDAStatus =
    unsupported
  def cuSurfObjectCreate(
      pSurfObject: Pointer[CUsurfObject, ReadWrite, Opaque],
      pResDesc: Pointer[CUDA_RESOURCE_DESC, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuSurfObjectDestroy(surfObject: CUsurfObject): CUDAStatus = unsupported
  def cuSurfObjectGetResourceDesc(
      pResDesc: Pointer[CUDA_RESOURCE_DESC, ReadWrite, Opaque],
      surfObject: CUsurfObject
  ): CUDAStatus = unsupported
  def cuSurfRefGetArray(phArray: Pointer[CUarray, ReadWrite, Opaque], hSurfRef: CUsurfref): CUDAStatus = unsupported
  def cuSurfRefSetArray(hSurfRef: CUsurfref, hArray: CUarray, Flags: UInt32): CUDAStatus               = unsupported
  def cuTensorMapEncodeIm2col(
      tensorMap: Pointer[CUtensorMap, ReadWrite, Opaque],
      tensorDataType: CUtensorMapDataType,
      tensorRank: cuuint32_t,
      globalAddress: Pointer[CVoid, ReadWrite, Opaque],
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
  ): CUDAStatus = unsupported
  def cuTensorMapEncodeIm2colWide(
      tensorMap: Pointer[CUtensorMap, ReadWrite, Opaque],
      tensorDataType: CUtensorMapDataType,
      tensorRank: cuuint32_t,
      globalAddress: Pointer[CVoid, ReadWrite, Opaque],
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
  ): CUDAStatus = unsupported
  def cuTensorMapEncodeTiled(
      tensorMap: Pointer[CUtensorMap, ReadWrite, Opaque],
      tensorDataType: CUtensorMapDataType,
      tensorRank: cuuint32_t,
      globalAddress: Pointer[CVoid, ReadWrite, Opaque],
      globalDim: Pointer[cuuint64_t, Read, Opaque],
      globalStrides: Pointer[cuuint64_t, Read, Opaque],
      boxDim: Pointer[cuuint32_t, Read, Opaque],
      elementStrides: Pointer[cuuint32_t, Read, Opaque],
      interleave: CUtensorMapInterleave,
      swizzle: CUtensorMapSwizzle,
      l2Promotion: CUtensorMapL2promotion,
      oobFill: CUtensorMapFloatOOBfill
  ): CUDAStatus = unsupported
  def cuTensorMapReplaceAddress(
      tensorMap: Pointer[CUtensorMap, ReadWrite, Opaque],
      globalAddress: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cuTexObjectCreate(
      pTexObject: Pointer[CUtexObject, ReadWrite, Opaque],
      pResDesc: Pointer[CUDA_RESOURCE_DESC, Read, Opaque],
      pTexDesc: Pointer[CUDA_TEXTURE_DESC, Read, Opaque],
      pResViewDesc: Pointer[CUDA_RESOURCE_VIEW_DESC, Read, Opaque]
  ): CUDAStatus = unsupported
  def cuTexObjectDestroy(texObject: CUtexObject): CUDAStatus = unsupported
  def cuTexObjectGetResourceDesc(
      pResDesc: Pointer[CUDA_RESOURCE_DESC, ReadWrite, Opaque],
      texObject: CUtexObject
  ): CUDAStatus = unsupported
  def cuTexObjectGetResourceViewDesc(
      pResViewDesc: Pointer[CUDA_RESOURCE_VIEW_DESC, ReadWrite, Opaque],
      texObject: CUtexObject
  ): CUDAStatus = unsupported
  def cuTexObjectGetTextureDesc(
      pTexDesc: Pointer[CUDA_TEXTURE_DESC, ReadWrite, Opaque],
      texObject: CUtexObject
  ): CUDAStatus = unsupported
  def cuTexRefCreate(pTexRef: Pointer[CUtexref, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuTexRefDestroy(hTexRef: CUtexref): CUDAStatus                            = unsupported
  def cuTexRefGetAddressMode(pam: Pointer[CUaddress_mode, ReadWrite, Opaque], hTexRef: CUtexref, dim: Int): CUDAStatus =
    unsupported
  def cuTexRefGetAddress_v2(pdptr: Pointer[DevicePtr, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus = unsupported
  def cuTexRefGetArray(phArray: Pointer[CUarray, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus      = unsupported
  def cuTexRefGetBorderColor(pBorderColor: Pointer[Float, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus =
    unsupported
  def cuTexRefGetFilterMode(pfm: Pointer[CUfilter_mode, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus = unsupported
  def cuTexRefGetFlags(pFlags: Pointer[UInt32, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus          = unsupported
  def cuTexRefGetFormat(
      pFormat: Pointer[CUarray_format, ReadWrite, Opaque],
      pNumChannels: Pointer[Int, ReadWrite, Opaque],
      hTexRef: CUtexref
  ): CUDAStatus = unsupported
  def cuTexRefGetMaxAnisotropy(pmaxAniso: Pointer[Int, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus = unsupported
  def cuTexRefGetMipmapFilterMode(pfm: Pointer[CUfilter_mode, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus =
    unsupported
  def cuTexRefGetMipmapLevelBias(pbias: Pointer[Float, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus = unsupported
  def cuTexRefGetMipmapLevelClamp(
      pminMipmapLevelClamp: Pointer[Float, ReadWrite, Opaque],
      pmaxMipmapLevelClamp: Pointer[Float, ReadWrite, Opaque],
      hTexRef: CUtexref
  ): CUDAStatus = unsupported
  def cuTexRefGetMipmappedArray(
      phMipmappedArray: Pointer[CUmipmappedArray, ReadWrite, Opaque],
      hTexRef: CUtexref
  ): CUDAStatus = unsupported
  def cuTexRefSetAddress2D_v3(
      hTexRef: CUtexref,
      desc: Pointer[CUDA_ARRAY_DESCRIPTOR, Read, Opaque],
      dptr: DevicePtr,
      Pitch: SizeT
  ): CUDAStatus = unsupported
  def cuTexRefSetAddressMode(hTexRef: CUtexref, dim: Int, am: CUaddress_mode): CUDAStatus = unsupported
  def cuTexRefSetAddress_v2(
      ByteOffset: Pointer[SizeT, ReadWrite, Opaque],
      hTexRef: CUtexref,
      dptr: DevicePtr,
      bytes: SizeT
  ): CUDAStatus = unsupported
  def cuTexRefSetArray(hTexRef: CUtexref, hArray: CUarray, Flags: UInt32): CUDAStatus = unsupported
  def cuTexRefSetBorderColor(hTexRef: CUtexref, pBorderColor: Pointer[Float, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cuTexRefSetFilterMode(hTexRef: CUtexref, fm: CUfilter_mode): CUDAStatus                         = unsupported
  def cuTexRefSetFlags(hTexRef: CUtexref, Flags: UInt32): CUDAStatus                                  = unsupported
  def cuTexRefSetFormat(hTexRef: CUtexref, fmt: CUarray_format, NumPackedComponents: Int): CUDAStatus = unsupported
  def cuTexRefSetMaxAnisotropy(hTexRef: CUtexref, maxAniso: UInt32): CUDAStatus                       = unsupported
  def cuTexRefSetMipmapFilterMode(hTexRef: CUtexref, fm: CUfilter_mode): CUDAStatus                   = unsupported
  def cuTexRefSetMipmapLevelBias(hTexRef: CUtexref, bias: Float): CUDAStatus                          = unsupported
  def cuTexRefSetMipmapLevelClamp(
      hTexRef: CUtexref,
      minMipmapLevelClamp: Float,
      maxMipmapLevelClamp: Float
  ): CUDAStatus = unsupported
  def cuTexRefSetMipmappedArray(hTexRef: CUtexref, hMipmappedArray: CUmipmappedArray, Flags: UInt32): CUDAStatus =
    unsupported
  def cuThreadExchangeStreamCaptureMode(mode: Pointer[CUstreamCaptureMode, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cuUserObjectCreate(
      object_out: Pointer[CUuserObject, ReadWrite, Opaque],
      ptr: Pointer[CVoid, ReadWrite, Opaque],
      destroy: CUhostFn,
      initialRefcount: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cuUserObjectRelease(`object`: CUuserObject, count: UInt32): CUDAStatus = unsupported
  def cuUserObjectRetain(`object`: CUuserObject, count: UInt32): CUDAStatus  = unsupported
  def cuWaitExternalSemaphoresAsync(
      extSemArray: Pointer[CUexternalSemaphore, Read, Opaque],
      paramsArray: Pointer[CUDA_EXTERNAL_SEMAPHORE_WAIT_PARAMS, Read, Opaque],
      numExtSems: UInt32,
      stream: CUstream
  ): CUDAStatus = unsupported
  def cudaArrayGetInfo(
      desc: Pointer[cudaChannelFormatDesc, ReadWrite, Opaque],
      extent: Pointer[cudaExtent, ReadWrite, Opaque],
      flags: Pointer[UInt32, ReadWrite, Opaque],
      array: cudaArray_t
  ): CUDAStatus = unsupported
  def cudaArrayGetMemoryRequirements(
      memoryRequirements: Pointer[cudaArrayMemoryRequirements, ReadWrite, Opaque],
      array: cudaArray_t,
      device: Int
  ): CUDAStatus = unsupported
  def cudaArrayGetPlane(
      pPlaneArray: Pointer[cudaArray_t, ReadWrite, Opaque],
      hArray: cudaArray_t,
      planeIdx: UInt32
  ): CUDAStatus = unsupported
  def cudaArrayGetSparseProperties(
      sparseProperties: Pointer[cudaArraySparseProperties, ReadWrite, Opaque],
      array: cudaArray_t
  ): CUDAStatus = unsupported
  def cudaChooseDevice(
      device: Pointer[Int, ReadWrite, Opaque],
      prop: Pointer[cudaDeviceProp, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaCreateChannelDesc(x: Int, y: Int, z: Int, w: Int, f: cudaChannelFormatKind): cudaChannelFormatDesc =
    unsupported
  def cudaCreateSurfaceObject(
      pSurfObject: Pointer[cudaSurfaceObject_t, ReadWrite, Opaque],
      pResDesc: Pointer[cudaResourceDesc, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaCreateTextureObject(
      pTexObject: Pointer[cudaTextureObject_t, ReadWrite, Opaque],
      pResDesc: Pointer[cudaResourceDesc, Read, Opaque],
      pTexDesc: Pointer[cudaTextureDesc, Read, Opaque],
      pResViewDesc: Pointer[cudaResourceViewDesc, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaCtxResetPersistingL2Cache(): CUDAStatus                               = unsupported
  def cudaDestroyExternalMemory(extMem: cudaExternalMemory_t): CUDAStatus       = unsupported
  def cudaDestroyExternalSemaphore(extSem: cudaExternalSemaphore_t): CUDAStatus = unsupported
  def cudaDestroySurfaceObject(surfObject: cudaSurfaceObject_t): CUDAStatus     = unsupported
  def cudaDestroyTextureObject(texObject: cudaTextureObject_t): CUDAStatus      = unsupported
  def cudaDevResourceGenerateDesc(
      phDesc: Pointer[cudaDevResourceDesc_t, ReadWrite, Opaque],
      resources: Pointer[cudaDevResource, ReadWrite, Opaque],
      nbResources: UInt32
  ): CUDAStatus = unsupported
  def cudaDevSmResourceSplit(
      result: Pointer[cudaDevResource, ReadWrite, Opaque],
      nbGroups: UInt32,
      input: Pointer[cudaDevResource, Read, Opaque],
      remainder: Pointer[cudaDevResource, ReadWrite, Opaque],
      flags: UInt32,
      groupParams: Pointer[cudaDevSmResourceGroupParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaDevSmResourceSplitByCount(
      result: Pointer[cudaDevResource, ReadWrite, Opaque],
      nbGroups: Pointer[UInt32, ReadWrite, Opaque],
      input: Pointer[cudaDevResource, Read, Opaque],
      remaining: Pointer[cudaDevResource, ReadWrite, Opaque],
      flags: UInt32,
      minCount: UInt32
  ): CUDAStatus = unsupported
  def cudaDeviceCanAccessPeer(
      canAccessPeer: Pointer[Int, ReadWrite, Opaque],
      device: Int,
      peerDevice: Int
  ): CUDAStatus = unsupported
  def cudaDeviceDisablePeerAccess(peerDevice: Int): CUDAStatus               = unsupported
  def cudaDeviceEnablePeerAccess(peerDevice: Int, flags: UInt32): CUDAStatus = unsupported
  def cudaDeviceFlushGPUDirectRDMAWrites(
      target: cudaFlushGPUDirectRDMAWritesTarget,
      scope: cudaFlushGPUDirectRDMAWritesScope
  ): CUDAStatus = unsupported
  def cudaDeviceGetAttribute(value: Pointer[Int, ReadWrite, Opaque], attr: cudaDeviceAttr, device: Int): CUDAStatus =
    unsupported
  def cudaDeviceGetByPCIBusId(device: Pointer[Int, ReadWrite, Opaque], pciBusId: CString): CUDAStatus = unsupported
  def cudaDeviceGetCacheConfig(pCacheConfig: Pointer[cudaFuncCache, ReadWrite, Opaque]): CUDAStatus   = unsupported
  def cudaDeviceGetDefaultMemPool(memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque], device: Int): CUDAStatus =
    unsupported
  def cudaDeviceGetDevResource(
      device: Int,
      resource: Pointer[cudaDevResource, ReadWrite, Opaque],
      `type`: cudaDevResourceType
  ): CUDAStatus = unsupported
  def cudaDeviceGetExecutionCtx(ctx: Pointer[cudaExecutionContext_t, ReadWrite, Opaque], device: Int): CUDAStatus =
    unsupported
  def cudaDeviceGetGraphMemAttribute(
      device: Int,
      attr: cudaGraphMemAttributeType,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaDeviceGetHostAtomicCapabilities(
      capabilities: Pointer[UInt32, ReadWrite, Opaque],
      operations: Pointer[cudaAtomicOperation, Read, Opaque],
      count: UInt32,
      device: Int
  ): CUDAStatus = unsupported
  def cudaDeviceGetLimit(pValue: Pointer[SizeT, ReadWrite, Opaque], limit: cudaLimit): CUDAStatus       = unsupported
  def cudaDeviceGetMemPool(memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque], device: Int): CUDAStatus = unsupported
  def cudaDeviceGetNvSciSyncAttributes(
      nvSciSyncAttrList: Pointer[CVoid, ReadWrite, Opaque],
      device: Int,
      flags: Int
  ): CUDAStatus = unsupported
  def cudaDeviceGetP2PAtomicCapabilities(
      capabilities: Pointer[UInt32, ReadWrite, Opaque],
      operations: Pointer[cudaAtomicOperation, Read, Opaque],
      count: UInt32,
      srcDevice: Int,
      dstDevice: Int
  ): CUDAStatus = unsupported
  def cudaDeviceGetP2PAttribute(
      value: Pointer[Int, ReadWrite, Opaque],
      attr: cudaDeviceP2PAttr,
      srcDevice: Int,
      dstDevice: Int
  ): CUDAStatus = unsupported
  def cudaDeviceGetPCIBusId(pciBusId: Pointer[Byte, ReadWrite, Opaque], len: Int, device: Int): CUDAStatus = unsupported
  def cudaDeviceGetSharedMemConfig(pConfig: Pointer[cudaSharedMemConfig, ReadWrite, Opaque]): CUDAStatus   = unsupported
  def cudaDeviceGetStreamPriorityRange(
      leastPriority: Pointer[Int, ReadWrite, Opaque],
      greatestPriority: Pointer[Int, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaDeviceGetTexture1DLinearMaxWidth(
      maxWidthInElements: Pointer[SizeT, ReadWrite, Opaque],
      fmtDesc: Pointer[cudaChannelFormatDesc, Read, Opaque],
      device: Int
  ): CUDAStatus = unsupported
  def cudaDeviceGraphMemTrim(device: Int): CUDAStatus = unsupported
  def cudaDeviceRegisterAsyncNotification(
      device: Int,
      callbackFunc: cudaAsyncCallback,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      callback: Pointer[cudaAsyncCallbackHandle_t, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaDeviceReset(): CUDAStatus                                    = unsupported
  def cudaDeviceSetCacheConfig(cacheConfig: cudaFuncCache): CUDAStatus = unsupported
  def cudaDeviceSetGraphMemAttribute(
      device: Int,
      attr: cudaGraphMemAttributeType,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaDeviceSetLimit(limit: cudaLimit, value: SizeT): CUDAStatus                                      = unsupported
  def cudaDeviceSetMemPool(device: Int, memPool: cudaMemPool_t): CUDAStatus                               = unsupported
  def cudaDeviceSetSharedMemConfig(config: cudaSharedMemConfig): CUDAStatus                               = unsupported
  def cudaDeviceSynchronize(): CUDAStatus                                                                 = unsupported
  def cudaDeviceUnregisterAsyncNotification(device: Int, callback: cudaAsyncCallbackHandle_t): CUDAStatus = unsupported
  def cudaDriverGetVersion(driverVersion: Pointer[Int, ReadWrite, Opaque]): CUDAStatus                    = unsupported
  def cudaEventCreate(event: Pointer[cudaEvent_t, ReadWrite, Opaque]): CUDAStatus                         = unsupported
  def cudaEventCreateWithFlags(event: Pointer[cudaEvent_t, ReadWrite, Opaque], flags: UInt32): CUDAStatus = unsupported
  def cudaEventDestroy(event: cudaEvent_t): CUDAStatus                                                    = unsupported
  def cudaEventElapsedTime(ms: Pointer[Float, ReadWrite, Opaque], start: cudaEvent_t, end: cudaEvent_t): CUDAStatus =
    unsupported
  def cudaEventQuery(event: cudaEvent_t): CUDAStatus                                                = unsupported
  def cudaEventRecord(event: cudaEvent_t, stream: cudaStream_t): CUDAStatus                         = unsupported
  def cudaEventRecordWithFlags(event: cudaEvent_t, stream: cudaStream_t, flags: UInt32): CUDAStatus = unsupported
  def cudaEventSynchronize(event: cudaEvent_t): CUDAStatus                                          = unsupported
  def cudaExecutionCtxDestroy(ctx: cudaExecutionContext_t): CUDAStatus                              = unsupported
  def cudaExecutionCtxGetDevResource(
      ctx: cudaExecutionContext_t,
      resource: Pointer[cudaDevResource, ReadWrite, Opaque],
      `type`: cudaDevResourceType
  ): CUDAStatus = unsupported
  def cudaExecutionCtxGetDevice(device: Pointer[Int, ReadWrite, Opaque], ctx: cudaExecutionContext_t): CUDAStatus =
    unsupported
  def cudaExecutionCtxGetId(
      ctx: cudaExecutionContext_t,
      ctxId: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaExecutionCtxRecordEvent(ctx: cudaExecutionContext_t, event: cudaEvent_t): CUDAStatus = unsupported
  def cudaExecutionCtxStreamCreate(
      phStream: Pointer[cudaStream_t, ReadWrite, Opaque],
      ctx: cudaExecutionContext_t,
      flags: UInt32,
      priority: Int
  ): CUDAStatus = unsupported
  def cudaExecutionCtxSynchronize(ctx: cudaExecutionContext_t): CUDAStatus                   = unsupported
  def cudaExecutionCtxWaitEvent(ctx: cudaExecutionContext_t, event: cudaEvent_t): CUDAStatus = unsupported
  def cudaExternalMemoryGetMappedBuffer(
      devPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      extMem: cudaExternalMemory_t,
      bufferDesc: Pointer[cudaExternalMemoryBufferDesc, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaExternalMemoryGetMappedMipmappedArray(
      mipmap: Pointer[cudaMipmappedArray_t, ReadWrite, Opaque],
      extMem: cudaExternalMemory_t,
      mipmapDesc: Pointer[cudaExternalMemoryMipmappedArrayDesc, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaFree(devPtr: DevicePtr): CUDAStatus
  def cudaFreeArray(array: cudaArray_t): CUDAStatus                                               = unsupported
  def cudaFreeAsync(devPtr: Pointer[CVoid, ReadWrite, Opaque], hStream: cudaStream_t): CUDAStatus = unsupported
  def cudaFreeHost(ptr: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus                            = unsupported
  def cudaFreeMipmappedArray(mipmappedArray: cudaMipmappedArray_t): CUDAStatus                    = unsupported
  def cudaFuncGetAttributes(
      attr: Pointer[cudaFuncAttributes, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaFuncGetName(
      name: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque],
      func: Pointer[CVoid, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaFuncGetParamCount(
      func: Pointer[CVoid, Read, Opaque],
      paramCount: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaFuncGetParamInfo(
      func: Pointer[CVoid, Read, Opaque],
      paramIndex: SizeT,
      paramOffset: Pointer[SizeT, ReadWrite, Opaque],
      paramSize: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaFuncSetAttribute(func: Pointer[CVoid, Read, Opaque], attr: cudaFuncAttribute, value: Int): CUDAStatus =
    unsupported
  def cudaFuncSetCacheConfig(func: Pointer[CVoid, Read, Opaque], cacheConfig: cudaFuncCache): CUDAStatus = unsupported
  def cudaFuncSetSharedMemConfig(func: Pointer[CVoid, Read, Opaque], config: cudaSharedMemConfig): CUDAStatus =
    unsupported
  def cudaGetChannelDesc(
      desc: Pointer[cudaChannelFormatDesc, ReadWrite, Opaque],
      array: cudaArray_const_t
  ): CUDAStatus = unsupported
  def cudaGetDevice(device: Pointer[Int, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cudaGetDeviceCount(count: Out[Int]): CUDAStatus
  def cudaGetDeviceFlags(flags: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus                          = unsupported
  def cudaGetDeviceProperties(prop: Pointer[cudaDeviceProp, ReadWrite, Opaque], device: Int): CUDAStatus = unsupported
  def cudaGetDriverEntryPoint(
      symbol: CString,
      funcPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      flags: unsigned_long_long,
      driverStatus: Pointer[cudaDriverEntryPointQueryResult, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGetDriverEntryPointByVersion(
      symbol: CString,
      funcPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      cudaVersion: UInt32,
      flags: unsigned_long_long,
      driverStatus: Pointer[cudaDriverEntryPointQueryResult, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGetErrorName(error: CUDAStatus): CString   = unsupported
  def cudaGetErrorString(error: CUDAStatus): CString = unsupported
  def cudaGetExportTable(
      ppExportTable: Pointer[Pointer[CVoid, ReadWrite, Opaque], Read, Opaque],
      pExportTableId: Pointer[cudaUUID_t, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGetFuncBySymbol(
      functionPtr: Pointer[cudaFunction_t, ReadWrite, Opaque],
      symbolPtr: Pointer[CVoid, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGetKernel(
      kernelPtr: Pointer[cudaKernel_t, ReadWrite, Opaque],
      entryFuncAddr: Pointer[CVoid, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGetLastError(): CUDAStatus = unsupported
  def cudaGetMipmappedArrayLevel(
      levelArray: Pointer[cudaArray_t, ReadWrite, Opaque],
      mipmappedArray: cudaMipmappedArray_const_t,
      level: UInt32
  ): CUDAStatus = unsupported
  def cudaGetSurfaceObjectResourceDesc(
      pResDesc: Pointer[cudaResourceDesc, ReadWrite, Opaque],
      surfObject: cudaSurfaceObject_t
  ): CUDAStatus = unsupported
  def cudaGetSymbolAddress(
      devPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      symbol: Pointer[CVoid, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGetSymbolSize(size: Pointer[SizeT, ReadWrite, Opaque], symbol: Pointer[CVoid, Read, Opaque]): CUDAStatus =
    unsupported
  def cudaGetTextureObjectResourceDesc(
      pResDesc: Pointer[cudaResourceDesc, ReadWrite, Opaque],
      texObject: cudaTextureObject_t
  ): CUDAStatus = unsupported
  def cudaGetTextureObjectResourceViewDesc(
      pResViewDesc: Pointer[cudaResourceViewDesc, ReadWrite, Opaque],
      texObject: cudaTextureObject_t
  ): CUDAStatus = unsupported
  def cudaGetTextureObjectTextureDesc(
      pTexDesc: Pointer[cudaTextureDesc, ReadWrite, Opaque],
      texObject: cudaTextureObject_t
  ): CUDAStatus = unsupported
  def cudaGraphAddChildGraphNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      childGraph: cudaGraph_t
  ): CUDAStatus = unsupported
  def cudaGraphAddDependencies(
      graph: cudaGraph_t,
      from: Pointer[cudaGraphNode_t, Read, Opaque],
      to: Pointer[cudaGraphNode_t, Read, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: SizeT
  ): CUDAStatus = unsupported
  def cudaGraphAddEmptyNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT
  ): CUDAStatus = unsupported
  def cudaGraphAddEventRecordNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      event: cudaEvent_t
  ): CUDAStatus = unsupported
  def cudaGraphAddEventWaitNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      event: cudaEvent_t
  ): CUDAStatus = unsupported
  def cudaGraphAddExternalSemaphoresSignalNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[cudaExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphAddExternalSemaphoresWaitNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[cudaExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphAddHostNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      pNodeParams: Pointer[cudaHostNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphAddKernelNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      pNodeParams: Pointer[cudaKernelNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphAddMemAllocNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[cudaMemAllocNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphAddMemFreeNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      dptr: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphAddMemcpyNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      pCopyParams: Pointer[cudaMemcpy3DParms, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphAddMemcpyNode1D(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaGraphAddMemcpyNodeFromSymbol(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      dst: Pointer[CVoid, ReadWrite, Opaque],
      symbol: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaGraphAddMemcpyNodeToSymbol(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaGraphAddMemsetNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      pMemsetParams: Pointer[cudaMemsetParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphAddNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      dependencyData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[cudaGraphNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphChildGraphNodeGetGraph(
      node: cudaGraphNode_t,
      pGraph: Pointer[cudaGraph_t, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphClone(pGraphClone: Pointer[cudaGraph_t, ReadWrite, Opaque], originalGraph: cudaGraph_t): CUDAStatus =
    unsupported
  def cudaGraphConditionalHandleCreate(
      pHandle_out: Pointer[cudaGraphConditionalHandle, ReadWrite, Opaque],
      graph: cudaGraph_t,
      defaultLaunchValue: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaGraphConditionalHandleCreate_v2(
      pHandle_out: Pointer[cudaGraphConditionalHandle, ReadWrite, Opaque],
      graph: cudaGraph_t,
      ctx: cudaExecutionContext_t,
      defaultLaunchValue: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaGraphCreate(pGraph: Pointer[cudaGraph_t, ReadWrite, Opaque], flags: UInt32): CUDAStatus = unsupported
  def cudaGraphDebugDotPrint(graph: cudaGraph_t, path: CString, flags: UInt32): CUDAStatus        = unsupported
  def cudaGraphDestroy(graph: cudaGraph_t): CUDAStatus                                            = unsupported
  def cudaGraphDestroyNode(node: cudaGraphNode_t): CUDAStatus                                     = unsupported
  def cudaGraphEventRecordNodeGetEvent(
      node: cudaGraphNode_t,
      event_out: Pointer[cudaEvent_t, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphEventRecordNodeSetEvent(node: cudaGraphNode_t, event: cudaEvent_t): CUDAStatus = unsupported
  def cudaGraphEventWaitNodeGetEvent(
      node: cudaGraphNode_t,
      event_out: Pointer[cudaEvent_t, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphEventWaitNodeSetEvent(node: cudaGraphNode_t, event: cudaEvent_t): CUDAStatus = unsupported
  def cudaGraphExecChildGraphNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      childGraph: cudaGraph_t
  ): CUDAStatus = unsupported
  def cudaGraphExecDestroy(graphExec: cudaGraphExec_t): CUDAStatus = unsupported
  def cudaGraphExecEventRecordNodeSetEvent(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      event: cudaEvent_t
  ): CUDAStatus = unsupported
  def cudaGraphExecEventWaitNodeSetEvent(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      event: cudaEvent_t
  ): CUDAStatus = unsupported
  def cudaGraphExecExternalSemaphoresSignalNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      nodeParams: Pointer[cudaExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphExecExternalSemaphoresWaitNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      nodeParams: Pointer[cudaExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphExecGetFlags(
      graphExec: cudaGraphExec_t,
      flags: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphExecGetId(hGraphExec: cudaGraphExec_t, graphID: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cudaGraphExecHostNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaHostNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphExecKernelNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaKernelNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphExecMemcpyNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaMemcpy3DParms, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphExecMemcpyNodeSetParams1D(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaGraphExecMemcpyNodeSetParamsFromSymbol(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      dst: Pointer[CVoid, ReadWrite, Opaque],
      symbol: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaGraphExecMemcpyNodeSetParamsToSymbol(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaGraphExecMemsetNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaMemsetParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphExecNodeSetParams(
      graphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      nodeParams: Pointer[cudaGraphNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphExecUpdate(
      hGraphExec: cudaGraphExec_t,
      hGraph: cudaGraph_t,
      resultInfo: Pointer[cudaGraphExecUpdateResultInfo, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphExternalSemaphoresSignalNodeGetParams(
      hNode: cudaGraphNode_t,
      params_out: Pointer[cudaExternalSemaphoreSignalNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphExternalSemaphoresSignalNodeSetParams(
      hNode: cudaGraphNode_t,
      nodeParams: Pointer[cudaExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphExternalSemaphoresWaitNodeGetParams(
      hNode: cudaGraphNode_t,
      params_out: Pointer[cudaExternalSemaphoreWaitNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphExternalSemaphoresWaitNodeSetParams(
      hNode: cudaGraphNode_t,
      nodeParams: Pointer[cudaExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphGetEdges(
      graph: cudaGraph_t,
      from: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      to: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, ReadWrite, Opaque],
      numEdges: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphGetId(hGraph: cudaGraph_t, graphID: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cudaGraphGetNodes(
      graph: cudaGraph_t,
      nodes: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      numNodes: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphGetRootNodes(
      graph: cudaGraph_t,
      pRootNodes: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      pNumRootNodes: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphHostNodeGetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaHostNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphHostNodeSetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaHostNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphInstantiate(
      pGraphExec: Pointer[cudaGraphExec_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cudaGraphInstantiateWithFlags(
      pGraphExec: Pointer[cudaGraphExec_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  def cudaGraphInstantiateWithParams(
      pGraphExec: Pointer[cudaGraphExec_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      instantiateParams: Pointer[cudaGraphInstantiateParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphKernelNodeCopyAttributes(hDst: cudaGraphNode_t, hSrc: cudaGraphNode_t): CUDAStatus = unsupported
  def cudaGraphKernelNodeGetAttribute(
      hNode: cudaGraphNode_t,
      attr: cudaLaunchAttributeID,
      value_out: Pointer[cudaLaunchAttributeValue, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphKernelNodeGetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaKernelNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphKernelNodeSetAttribute(
      hNode: cudaGraphNode_t,
      attr: cudaLaunchAttributeID,
      value: Pointer[cudaLaunchAttributeValue, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphKernelNodeSetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaKernelNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphLaunch(graphExec: cudaGraphExec_t, stream: cudaStream_t): CUDAStatus = unsupported
  def cudaGraphMemAllocNodeGetParams(
      node: cudaGraphNode_t,
      params_out: Pointer[cudaMemAllocNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphMemFreeNodeGetParams(node: cudaGraphNode_t, dptr_out: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cudaGraphMemcpyNodeGetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaMemcpy3DParms, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphMemcpyNodeSetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaMemcpy3DParms, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphMemcpyNodeSetParams1D(
      node: cudaGraphNode_t,
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaGraphMemcpyNodeSetParamsFromSymbol(
      node: cudaGraphNode_t,
      dst: Pointer[CVoid, ReadWrite, Opaque],
      symbol: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaGraphMemcpyNodeSetParamsToSymbol(
      node: cudaGraphNode_t,
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaGraphMemsetNodeGetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaMemsetParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphMemsetNodeSetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaMemsetParams, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphNodeFindInClone(
      pNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      originalNode: cudaGraphNode_t,
      clonedGraph: cudaGraph_t
  ): CUDAStatus = unsupported
  def cudaGraphNodeGetContainingGraph(
      hNode: cudaGraphNode_t,
      phGraph: Pointer[cudaGraph_t, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphNodeGetDependencies(
      node: cudaGraphNode_t,
      pDependencies: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, ReadWrite, Opaque],
      pNumDependencies: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphNodeGetDependentNodes(
      node: cudaGraphNode_t,
      pDependentNodes: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, ReadWrite, Opaque],
      pNumDependentNodes: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphNodeGetEnabled(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      isEnabled: Pointer[UInt32, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphNodeGetLocalId(hNode: cudaGraphNode_t, nodeId: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cudaGraphNodeGetParams(
      node: cudaGraphNode_t,
      nodeParams: Pointer[cudaGraphNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphNodeGetToolsId(
      hNode: cudaGraphNode_t,
      toolsNodeId: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphNodeGetType(node: cudaGraphNode_t, pType: Pointer[cudaGraphNodeType, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cudaGraphNodeSetEnabled(hGraphExec: cudaGraphExec_t, hNode: cudaGraphNode_t, isEnabled: UInt32): CUDAStatus =
    unsupported
  def cudaGraphNodeSetParams(
      node: cudaGraphNode_t,
      nodeParams: Pointer[cudaGraphNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaGraphReleaseUserObject(graph: cudaGraph_t, `object`: cudaUserObject_t, count: UInt32): CUDAStatus =
    unsupported
  def cudaGraphRemoveDependencies(
      graph: cudaGraph_t,
      from: Pointer[cudaGraphNode_t, Read, Opaque],
      to: Pointer[cudaGraphNode_t, Read, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: SizeT
  ): CUDAStatus = unsupported
  def cudaGraphRetainUserObject(
      graph: cudaGraph_t,
      `object`: cudaUserObject_t,
      count: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaGraphUpload(graphExec: cudaGraphExec_t, stream: cudaStream_t): CUDAStatus = unsupported
  def cudaGraphicsMapResources(
      count: Int,
      resources: Pointer[cudaGraphicsResource_t, ReadWrite, Opaque],
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaGraphicsResourceGetMappedMipmappedArray(
      mipmappedArray: Pointer[cudaMipmappedArray_t, ReadWrite, Opaque],
      resource: cudaGraphicsResource_t
  ): CUDAStatus = unsupported
  def cudaGraphicsResourceGetMappedPointer(
      devPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      size: Pointer[SizeT, ReadWrite, Opaque],
      resource: cudaGraphicsResource_t
  ): CUDAStatus = unsupported
  def cudaGraphicsResourceSetMapFlags(resource: cudaGraphicsResource_t, flags: UInt32): CUDAStatus = unsupported
  def cudaGraphicsSubResourceGetMappedArray(
      array: Pointer[cudaArray_t, ReadWrite, Opaque],
      resource: cudaGraphicsResource_t,
      arrayIndex: UInt32,
      mipLevel: UInt32
  ): CUDAStatus = unsupported
  def cudaGraphicsUnmapResources(
      count: Int,
      resources: Pointer[cudaGraphicsResource_t, ReadWrite, Opaque],
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaGraphicsUnregisterResource(resource: cudaGraphicsResource_t): CUDAStatus = unsupported
  def cudaGreenCtxCreate(
      phCtx: Pointer[cudaExecutionContext_t, ReadWrite, Opaque],
      desc: cudaDevResourceDesc_t,
      device: Int,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaHostAlloc(
      pHost: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      size: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaHostGetDevicePointer(
      pDevice: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      pHost: Pointer[CVoid, ReadWrite, Opaque],
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaHostGetFlags(
      pFlags: Pointer[UInt32, ReadWrite, Opaque],
      pHost: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaHostRegister(ptr: Pointer[CVoid, ReadWrite, Opaque], size: SizeT, flags: UInt32): CUDAStatus = unsupported
  def cudaHostUnregister(ptr: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus                           = unsupported
  def cudaImportExternalMemory(
      extMem_out: Pointer[cudaExternalMemory_t, ReadWrite, Opaque],
      memHandleDesc: Pointer[cudaExternalMemoryHandleDesc, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaImportExternalSemaphore(
      extSem_out: Pointer[cudaExternalSemaphore_t, ReadWrite, Opaque],
      semHandleDesc: Pointer[cudaExternalSemaphoreHandleDesc, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaInitDevice(device: Int, deviceFlags: UInt32, flags: UInt32): CUDAStatus  = unsupported
  def cudaIpcCloseMemHandle(devPtr: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cudaIpcGetEventHandle(handle: Pointer[cudaIpcEventHandle_t, ReadWrite, Opaque], event: cudaEvent_t): CUDAStatus =
    unsupported
  def cudaIpcGetMemHandle(
      handle: Pointer[cudaIpcMemHandle_t, ReadWrite, Opaque],
      devPtr: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaIpcOpenEventHandle(event: Pointer[cudaEvent_t, ReadWrite, Opaque], handle: cudaIpcEventHandle_t): CUDAStatus =
    unsupported
  def cudaIpcOpenMemHandle(
      devPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      handle: cudaIpcMemHandle_t,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaKernelSetAttributeForDevice(
      kernel: cudaKernel_t,
      attr: cudaFuncAttribute,
      value: Int,
      device: Int
  ): CUDAStatus = unsupported
  def cudaLaunchCooperativeKernel(
      func: Pointer[CVoid, Read, Opaque],
      gridDim: dim3,
      blockDim: dim3,
      args: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      sharedMem: SizeT,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaLaunchHostFunc(
      stream: cudaStream_t,
      fn: cudaHostFn_t,
      userData: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaLaunchHostFunc_v2(
      stream: cudaStream_t,
      fn: cudaHostFn_t,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      syncMode: UInt32
  ): CUDAStatus = unsupported
  def cudaLaunchKernel(
      func: Pointer[CVoid, Read, Opaque],
      gridDim: dim3,
      blockDim: dim3,
      args: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      sharedMem: SizeT,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaLaunchKernelExC(
      config: Pointer[cudaLaunchConfig_t, Read, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      args: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaLibraryEnumerateKernels(
      kernels: Pointer[cudaKernel_t, ReadWrite, Opaque],
      numKernels: UInt32,
      lib: cudaLibrary_t
  ): CUDAStatus = unsupported
  def cudaLibraryGetGlobal(
      dptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      bytes: Pointer[SizeT, ReadWrite, Opaque],
      library: cudaLibrary_t,
      name: CString
  ): CUDAStatus = unsupported
  def cudaLibraryGetKernel(
      pKernel: Pointer[cudaKernel_t, ReadWrite, Opaque],
      library: cudaLibrary_t,
      name: CString
  ): CUDAStatus = unsupported
  def cudaLibraryGetKernelCount(count: Pointer[UInt32, ReadWrite, Opaque], lib: cudaLibrary_t): CUDAStatus = unsupported
  def cudaLibraryGetManaged(
      dptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      bytes: Pointer[SizeT, ReadWrite, Opaque],
      library: cudaLibrary_t,
      name: CString
  ): CUDAStatus = unsupported
  def cudaLibraryGetUnifiedFunction(
      fptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      library: cudaLibrary_t,
      symbol: CString
  ): CUDAStatus = unsupported
  def cudaLibraryLoadData(
      library: Pointer[cudaLibrary_t, ReadWrite, Opaque],
      code: Pointer[CVoid, Read, Opaque],
      jitOptions: Pointer[cudaJitOption, ReadWrite, Opaque],
      jitOptionsValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      numJitOptions: UInt32,
      libraryOptions: Pointer[cudaLibraryOption, ReadWrite, Opaque],
      libraryOptionValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      numLibraryOptions: UInt32
  ): CUDAStatus = unsupported
  def cudaLibraryLoadFromFile(
      library: Pointer[cudaLibrary_t, ReadWrite, Opaque],
      fileName: CString,
      jitOptions: Pointer[cudaJitOption, ReadWrite, Opaque],
      jitOptionsValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      numJitOptions: UInt32,
      libraryOptions: Pointer[cudaLibraryOption, ReadWrite, Opaque],
      libraryOptionValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      numLibraryOptions: UInt32
  ): CUDAStatus = unsupported
  def cudaLibraryUnload(library: cudaLibrary_t): CUDAStatus = unsupported
  def cudaLogsCurrent(iterator_out: Pointer[cudaLogIterator, ReadWrite, Opaque], flags: UInt32): CUDAStatus =
    unsupported
  def cudaLogsDumpToFile(
      iterator: Pointer[cudaLogIterator, ReadWrite, Opaque],
      pathToFile: CString,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaLogsDumpToMemory(
      iterator: Pointer[cudaLogIterator, ReadWrite, Opaque],
      buffer: Pointer[Byte, ReadWrite, Opaque],
      size: Pointer[SizeT, ReadWrite, Opaque],
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaLogsRegisterCallback(
      callbackFunc: cudaLogsCallback_t,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      callback_out: Pointer[cudaLogsCallbackHandle, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaLogsUnregisterCallback(callback: cudaLogsCallbackHandle): CUDAStatus = unsupported
  def cudaMalloc(devPtr: Out[DevicePtr], size: SizeT): CUDAStatus
  def cudaMalloc3D(pitchedDevPtr: Pointer[cudaPitchedPtr, ReadWrite, Opaque], extent: cudaExtent): CUDAStatus =
    unsupported
  def cudaMalloc3DArray(
      array: Pointer[cudaArray_t, ReadWrite, Opaque],
      desc: Pointer[cudaChannelFormatDesc, Read, Opaque],
      extent: cudaExtent,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaMallocArray(
      array: Pointer[cudaArray_t, ReadWrite, Opaque],
      desc: Pointer[cudaChannelFormatDesc, Read, Opaque],
      width: SizeT,
      height: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaMallocAsync(
      devPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      size: SizeT,
      hStream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMallocFromPoolAsync(
      ptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      size: SizeT,
      memPool: cudaMemPool_t,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMallocHost(ptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque], size: SizeT): CUDAStatus =
    unsupported
  def cudaMallocManaged(
      devPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      size: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaMallocMipmappedArray(
      mipmappedArray: Pointer[cudaMipmappedArray_t, ReadWrite, Opaque],
      desc: Pointer[cudaChannelFormatDesc, Read, Opaque],
      extent: cudaExtent,
      numLevels: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaMallocPitch(
      devPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      pitch: Pointer[SizeT, ReadWrite, Opaque],
      width: SizeT,
      height: SizeT
  ): CUDAStatus = unsupported
  def cudaMemAdvise(
      devPtr: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      advice: cudaMemoryAdvise,
      location: cudaMemLocation
  ): CUDAStatus = unsupported
  def cudaMemDiscardAndPrefetchBatchAsync(
      dptrs: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      sizes: Pointer[SizeT, ReadWrite, Opaque],
      count: SizeT,
      prefetchLocs: Pointer[cudaMemLocation, ReadWrite, Opaque],
      prefetchLocIdxs: Pointer[SizeT, ReadWrite, Opaque],
      numPrefetchLocs: SizeT,
      flags: unsigned_long_long,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemDiscardBatchAsync(
      dptrs: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      sizes: Pointer[SizeT, ReadWrite, Opaque],
      count: SizeT,
      flags: unsigned_long_long,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemGetDefaultMemPool(
      memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque],
      location: Pointer[cudaMemLocation, ReadWrite, Opaque],
      `type`: cudaMemAllocationType
  ): CUDAStatus = unsupported
  def cudaMemGetInfo(free: Pointer[SizeT, ReadWrite, Opaque], total: Pointer[SizeT, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cudaMemGetMemPool(
      memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque],
      location: Pointer[cudaMemLocation, ReadWrite, Opaque],
      `type`: cudaMemAllocationType
  ): CUDAStatus = unsupported
  def cudaMemPoolCreate(
      memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque],
      poolProps: Pointer[cudaMemPoolProps, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaMemPoolDestroy(memPool: cudaMemPool_t): CUDAStatus = unsupported
  def cudaMemPoolExportPointer(
      exportData: Pointer[cudaMemPoolPtrExportData, ReadWrite, Opaque],
      ptr: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaMemPoolExportToShareableHandle(
      shareableHandle: Pointer[CVoid, ReadWrite, Opaque],
      memPool: cudaMemPool_t,
      handleType: cudaMemAllocationHandleType,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaMemPoolGetAccess(
      flags: Pointer[cudaMemAccessFlags, ReadWrite, Opaque],
      memPool: cudaMemPool_t,
      location: Pointer[cudaMemLocation, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaMemPoolGetAttribute(
      memPool: cudaMemPool_t,
      attr: cudaMemPoolAttr,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaMemPoolImportFromShareableHandle(
      memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque],
      shareableHandle: Pointer[CVoid, ReadWrite, Opaque],
      handleType: cudaMemAllocationHandleType,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaMemPoolImportPointer(
      ptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      memPool: cudaMemPool_t,
      exportData: Pointer[cudaMemPoolPtrExportData, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaMemPoolSetAccess(
      memPool: cudaMemPool_t,
      descList: Pointer[cudaMemAccessDesc, Read, Opaque],
      count: SizeT
  ): CUDAStatus = unsupported
  def cudaMemPoolSetAttribute(
      memPool: cudaMemPool_t,
      attr: cudaMemPoolAttr,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaMemPoolTrimTo(memPool: cudaMemPool_t, minBytesToKeep: SizeT): CUDAStatus = unsupported
  def cudaMemPrefetchAsync(
      devPtr: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      location: cudaMemLocation,
      flags: UInt32,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemPrefetchBatchAsync(
      dptrs: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      sizes: Pointer[SizeT, ReadWrite, Opaque],
      count: SizeT,
      prefetchLocs: Pointer[cudaMemLocation, ReadWrite, Opaque],
      prefetchLocIdxs: Pointer[SizeT, ReadWrite, Opaque],
      numPrefetchLocs: SizeT,
      flags: unsigned_long_long,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemRangeGetAttribute(
      data: Pointer[CVoid, ReadWrite, Opaque],
      dataSize: SizeT,
      attribute: cudaMemRangeAttribute,
      devPtr: Pointer[CVoid, Read, Opaque],
      count: SizeT
  ): CUDAStatus = unsupported
  def cudaMemRangeGetAttributes(
      data: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      dataSizes: Pointer[SizeT, ReadWrite, Opaque],
      attributes: Pointer[cudaMemRangeAttribute, ReadWrite, Opaque],
      numAttributes: SizeT,
      devPtr: Pointer[CVoid, Read, Opaque],
      count: SizeT
  ): CUDAStatus = unsupported
  def cudaMemSetMemPool(
      location: Pointer[cudaMemLocation, ReadWrite, Opaque],
      `type`: cudaMemAllocationType,
      memPool: cudaMemPool_t
  ): CUDAStatus = unsupported
  def cudaMemcpy(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaMemcpy2D(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      dpitch: SizeT,
      src: Pointer[CVoid, Read, Opaque],
      spitch: SizeT,
      width: SizeT,
      height: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaMemcpy2DArrayToArray(
      dst: cudaArray_t,
      wOffsetDst: SizeT,
      hOffsetDst: SizeT,
      src: cudaArray_const_t,
      wOffsetSrc: SizeT,
      hOffsetSrc: SizeT,
      width: SizeT,
      height: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaMemcpy2DAsync(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      dpitch: SizeT,
      src: Pointer[CVoid, Read, Opaque],
      spitch: SizeT,
      width: SizeT,
      height: SizeT,
      kind: cudaMemcpyKind,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemcpy2DFromArray(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      dpitch: SizeT,
      src: cudaArray_const_t,
      wOffset: SizeT,
      hOffset: SizeT,
      width: SizeT,
      height: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaMemcpy2DFromArrayAsync(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      dpitch: SizeT,
      src: cudaArray_const_t,
      wOffset: SizeT,
      hOffset: SizeT,
      width: SizeT,
      height: SizeT,
      kind: cudaMemcpyKind,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemcpy2DToArray(
      dst: cudaArray_t,
      wOffset: SizeT,
      hOffset: SizeT,
      src: Pointer[CVoid, Read, Opaque],
      spitch: SizeT,
      width: SizeT,
      height: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaMemcpy2DToArrayAsync(
      dst: cudaArray_t,
      wOffset: SizeT,
      hOffset: SizeT,
      src: Pointer[CVoid, Read, Opaque],
      spitch: SizeT,
      width: SizeT,
      height: SizeT,
      kind: cudaMemcpyKind,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemcpy3D(p: Pointer[cudaMemcpy3DParms, Read, Opaque]): CUDAStatus                            = unsupported
  def cudaMemcpy3DAsync(p: Pointer[cudaMemcpy3DParms, Read, Opaque], stream: cudaStream_t): CUDAStatus = unsupported
  def cudaMemcpy3DBatchAsync(
      numOps: SizeT,
      opList: Pointer[cudaMemcpy3DBatchOp, ReadWrite, Opaque],
      flags: unsigned_long_long,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemcpy3DPeer(p: Pointer[cudaMemcpy3DPeerParms, Read, Opaque]): CUDAStatus = unsupported
  def cudaMemcpy3DPeerAsync(p: Pointer[cudaMemcpy3DPeerParms, Read, Opaque], stream: cudaStream_t): CUDAStatus =
    unsupported
  def cudaMemcpy3DWithAttributesAsync(
      op: Pointer[cudaMemcpy3DBatchOp, ReadWrite, Opaque],
      flags: unsigned_long_long,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemcpyArrayToArray(
      dst: cudaArray_t,
      wOffsetDst: SizeT,
      hOffsetDst: SizeT,
      src: cudaArray_const_t,
      wOffsetSrc: SizeT,
      hOffsetSrc: SizeT,
      count: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaMemcpyAsync(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      kind: cudaMemcpyKind,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemcpyBatchAsync(
      dsts: Pointer[void_const, ReadWrite, Opaque],
      srcs: Pointer[void_const, Read, Opaque],
      sizes: Pointer[SizeT, Read, Opaque],
      count: SizeT,
      attrs: Pointer[cudaMemcpyAttributes, ReadWrite, Opaque],
      attrsIdxs: Pointer[SizeT, ReadWrite, Opaque],
      numAttrs: SizeT,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemcpyFromArray(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: cudaArray_const_t,
      wOffset: SizeT,
      hOffset: SizeT,
      count: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaMemcpyFromArrayAsync(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: cudaArray_const_t,
      wOffset: SizeT,
      hOffset: SizeT,
      count: SizeT,
      kind: cudaMemcpyKind,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemcpyFromSymbol(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      symbol: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaMemcpyFromSymbolAsync(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      symbol: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemcpyPeer(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      dstDevice: Int,
      src: Pointer[CVoid, Read, Opaque],
      srcDevice: Int,
      count: SizeT
  ): CUDAStatus = unsupported
  def cudaMemcpyPeerAsync(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      dstDevice: Int,
      src: Pointer[CVoid, Read, Opaque],
      srcDevice: Int,
      count: SizeT,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemcpyToArray(
      dst: cudaArray_t,
      wOffset: SizeT,
      hOffset: SizeT,
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaMemcpyToArrayAsync(
      dst: cudaArray_t,
      wOffset: SizeT,
      hOffset: SizeT,
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      kind: cudaMemcpyKind,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemcpyToSymbol(
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  def cudaMemcpyToSymbolAsync(
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemcpyWithAttributesAsync(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      size: SizeT,
      attr: Pointer[cudaMemcpyAttributes, ReadWrite, Opaque],
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemset(devPtr: Pointer[CVoid, ReadWrite, Opaque], value: Int, count: SizeT): CUDAStatus = unsupported
  def cudaMemset2D(
      devPtr: Pointer[CVoid, ReadWrite, Opaque],
      pitch: SizeT,
      value: Int,
      width: SizeT,
      height: SizeT
  ): CUDAStatus = unsupported
  def cudaMemset2DAsync(
      devPtr: Pointer[CVoid, ReadWrite, Opaque],
      pitch: SizeT,
      value: Int,
      width: SizeT,
      height: SizeT,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemset3D(pitchedDevPtr: cudaPitchedPtr, value: Int, extent: cudaExtent): CUDAStatus = unsupported
  def cudaMemset3DAsync(
      pitchedDevPtr: cudaPitchedPtr,
      value: Int,
      extent: cudaExtent,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMemsetAsync(
      devPtr: Pointer[CVoid, ReadWrite, Opaque],
      value: Int,
      count: SizeT,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaMipmappedArrayGetMemoryRequirements(
      memoryRequirements: Pointer[cudaArrayMemoryRequirements, ReadWrite, Opaque],
      mipmap: cudaMipmappedArray_t,
      device: Int
  ): CUDAStatus = unsupported
  def cudaMipmappedArrayGetSparseProperties(
      sparseProperties: Pointer[cudaArraySparseProperties, ReadWrite, Opaque],
      mipmap: cudaMipmappedArray_t
  ): CUDAStatus = unsupported
  def cudaOccupancyAvailableDynamicSMemPerBlock(
      dynamicSmemSize: Pointer[SizeT, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      numBlocks: Int,
      blockSize: Int
  ): CUDAStatus = unsupported
  def cudaOccupancyMaxActiveBlocksPerMultiprocessor(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      blockSize: Int,
      dynamicSMemSize: SizeT
  ): CUDAStatus = unsupported
  def cudaOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      blockSize: Int,
      dynamicSMemSize: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaOccupancyMaxActiveClusters(
      numClusters: Pointer[Int, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      launchConfig: Pointer[cudaLaunchConfig_t, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaOccupancyMaxPotentialClusterSize(
      clusterSize: Pointer[Int, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      launchConfig: Pointer[cudaLaunchConfig_t, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaPeekAtLastError(): CUDAStatus = unsupported
  def cudaPointerGetAttributes(
      attributes: Pointer[cudaPointerAttributes, ReadWrite, Opaque],
      ptr: Pointer[CVoid, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaRuntimeGetVersion(runtimeVersion: Pointer[Int, ReadWrite, Opaque]): CUDAStatus     = unsupported
  def cudaSetDevice(device: Int): CUDAStatus                                                 = unsupported
  def cudaSetDeviceFlags(flags: UInt32): CUDAStatus                                          = unsupported
  def cudaSetValidDevices(device_arr: Pointer[Int, ReadWrite, Opaque], len: Int): CUDAStatus = unsupported
  def cudaSignalExternalSemaphoresAsync(
      extSemArray: Pointer[cudaExternalSemaphore_t, Read, Opaque],
      paramsArray: Pointer[cudaExternalSemaphoreSignalParams, Read, Opaque],
      numExtSems: UInt32,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  def cudaStreamAddCallback(
      stream: cudaStream_t,
      callback: cudaStreamCallback_t,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaStreamAttachMemAsync(
      stream: cudaStream_t,
      devPtr: Pointer[CVoid, ReadWrite, Opaque],
      length: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaStreamBeginCapture(stream: cudaStream_t, mode: cudaStreamCaptureMode): CUDAStatus = unsupported
  def cudaStreamBeginCaptureToGraph(
      stream: cudaStream_t,
      graph: cudaGraph_t,
      dependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      dependencyData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: SizeT,
      mode: cudaStreamCaptureMode
  ): CUDAStatus = unsupported
  def cudaStreamBeginRecaptureToGraph(
      stream: cudaStream_t,
      mode: cudaStreamCaptureMode,
      graph: cudaGraph_t,
      callbackData: Pointer[cudaGraphRecaptureCallbackData, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaStreamCopyAttributes(dst: cudaStream_t, src: cudaStream_t): CUDAStatus      = unsupported
  def cudaStreamCreate(pStream: Pointer[cudaStream_t, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cudaStreamCreateWithFlags(pStream: Pointer[cudaStream_t, ReadWrite, Opaque], flags: UInt32): CUDAStatus =
    unsupported
  def cudaStreamCreateWithPriority(
      pStream: Pointer[cudaStream_t, ReadWrite, Opaque],
      flags: UInt32,
      priority: Int
  ): CUDAStatus = unsupported
  def cudaStreamDestroy(stream: cudaStream_t): CUDAStatus = unsupported
  def cudaStreamEndCapture(stream: cudaStream_t, pGraph: Pointer[cudaGraph_t, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cudaStreamGetAttribute(
      hStream: cudaStream_t,
      attr: cudaLaunchAttributeID,
      value_out: Pointer[cudaLaunchAttributeValue, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaStreamGetCaptureInfo(
      stream: cudaStream_t,
      captureStatus_out: Pointer[cudaStreamCaptureStatus, ReadWrite, Opaque],
      id_out: Pointer[unsigned_long_long, ReadWrite, Opaque],
      graph_out: Pointer[cudaGraph_t, ReadWrite, Opaque],
      dependencies_out: Pointer[Pointer[cudaGraphNode_t, ReadWrite, Opaque], Read, Opaque],
      edgeData_out: Pointer[Pointer[cudaGraphEdgeData, ReadWrite, Opaque], Read, Opaque],
      numDependencies_out: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaStreamGetDevResource(
      hStream: cudaStream_t,
      resource: Pointer[cudaDevResource, ReadWrite, Opaque],
      `type`: cudaDevResourceType
  ): CUDAStatus = unsupported
  def cudaStreamGetDevice(hStream: cudaStream_t, device: Pointer[Int, ReadWrite, Opaque]): CUDAStatus  = unsupported
  def cudaStreamGetFlags(hStream: cudaStream_t, flags: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cudaStreamGetId(hStream: cudaStream_t, streamId: Pointer[unsigned_long_long, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cudaStreamGetPriority(hStream: cudaStream_t, priority: Pointer[Int, ReadWrite, Opaque]): CUDAStatus = unsupported
  def cudaStreamIsCapturing(
      stream: cudaStream_t,
      pCaptureStatus: Pointer[cudaStreamCaptureStatus, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  def cudaStreamQuery(stream: cudaStream_t): CUDAStatus = unsupported
  def cudaStreamSetAttribute(
      hStream: cudaStream_t,
      attr: cudaLaunchAttributeID,
      value: Pointer[cudaLaunchAttributeValue, Read, Opaque]
  ): CUDAStatus = unsupported
  def cudaStreamSynchronize(stream: cudaStream_t): CUDAStatus = unsupported
  def cudaStreamUpdateCaptureDependencies(
      stream: cudaStream_t,
      dependencies: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      dependencyData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaStreamWaitEvent(stream: cudaStream_t, event: cudaEvent_t, flags: UInt32): CUDAStatus = unsupported
  def cudaThreadExchangeStreamCaptureMode(mode: Pointer[cudaStreamCaptureMode, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  def cudaUserObjectCreate(
      object_out: Pointer[cudaUserObject_t, ReadWrite, Opaque],
      ptr: Pointer[CVoid, ReadWrite, Opaque],
      destroy: cudaHostFn_t,
      initialRefcount: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  def cudaUserObjectRelease(`object`: cudaUserObject_t, count: UInt32): CUDAStatus = unsupported
  def cudaUserObjectRetain(`object`: cudaUserObject_t, count: UInt32): CUDAStatus  = unsupported
  def cudaWaitExternalSemaphoresAsync(
      extSemArray: Pointer[cudaExternalSemaphore_t, Read, Opaque],
      paramsArray: Pointer[cudaExternalSemaphoreWaitParams, Read, Opaque],
      numExtSems: UInt32,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
}

object CUDA {
  val HeaderVersion: String = "13.3"
  val SurfaceSize: Int      = 820
}

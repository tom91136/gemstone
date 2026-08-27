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
type CUasyncCallback                           = CValue["CUasyncCallback"]
type CUasyncCallbackHandle                     = CValue["CUasyncCallbackHandle"]
type CUatomicOperation                         = CValue["CUatomicOperation"]
type CUcheckpointCheckpointArgs                = CValue["CUcheckpointCheckpointArgs"]
type CUcheckpointLockArgs                      = CValue["CUcheckpointLockArgs"]
type CUcheckpointRestoreArgs                   = CValue["CUcheckpointRestoreArgs"]
type CUcheckpointUnlockArgs                    = CValue["CUcheckpointUnlockArgs"]
type CUcoredumpCallbackHandle                  = CValue["CUcoredumpCallbackHandle"]
type CUcoredumpSettings                        = CValue["CUcoredumpSettings"]
type CUcoredumpStatusCallback                  = CValue["CUcoredumpStatusCallback"]
type CUctxCreateParams                         = CValue["CUctxCreateParams"]
type CUdevResource                             = CValue["CUdevResource"]
type CUdevResourceDesc                         = CValue["CUdevResourceDesc"]
type CUdevResourceType                         = CValue["CUdevResourceType"]
type CUdevice_P2PAttribute                     = CValue["CUdevice_P2PAttribute"]
type CUdevice_attribute                        = CValue["CUdevice_attribute"]
type CUdevprop                                 = CValue["CUdevprop"]
type CUdriverProcAddressQueryResult            = CValue["CUdriverProcAddressQueryResult"]
type CUevent                                   = CValue["CUevent"]
type CUexecAffinityParam                       = CValue["CUexecAffinityParam"]
type CUexecAffinityType                        = CValue["CUexecAffinityType"]
type CUexternalMemory                          = CValue["CUexternalMemory"]
type CUexternalSemaphore                       = CValue["CUexternalSemaphore"]
type CUfilter_mode                             = CValue["CUfilter_mode"]
type CUflushGPUDirectRDMAWritesScope           = CValue["CUflushGPUDirectRDMAWritesScope"]
type CUflushGPUDirectRDMAWritesTarget          = CValue["CUflushGPUDirectRDMAWritesTarget"]
type CUfunc_cache                              = CValue["CUfunc_cache"]
type CUfunctionLoadingState                    = CValue["CUfunctionLoadingState"]
type CUfunction_attribute                      = CValue["CUfunction_attribute"]
type CUgraph                                   = CValue["CUgraph"]
type CUgraphConditionalHandle                  = CValue["CUgraphConditionalHandle"]
type CUgraphEdgeData                           = CValue["CUgraphEdgeData"]
type CUgraphExec                               = CValue["CUgraphExec"]
type CUgraphExecUpdateResultInfo               = CValue["CUgraphExecUpdateResultInfo"]
type CUgraphMem_attribute                      = CValue["CUgraphMem_attribute"]
type CUgraphNode                               = CValue["CUgraphNode"]
type CUgraphNodeParams                         = CValue["CUgraphNodeParams"]
type CUgraphNodeType                           = CValue["CUgraphNodeType"]
type CUgraphRecaptureCallback                  = CValue["CUgraphRecaptureCallback"]
type CUgraphicsResource                        = CValue["CUgraphicsResource"]
type CUgreenCtx                                = CValue["CUgreenCtx"]
type CUhostFn                                  = CValue["CUhostFn"]
type CUipcEventHandle                          = CValue["CUipcEventHandle"]
type CUipcMemHandle                            = CValue["CUipcMemHandle"]
type CUjitInputType                            = CValue["CUjitInputType"]
type CUjit_option                              = CValue["CUjit_option"]
type CUkernel                                  = CValue["CUkernel"]
type CUkernelNodeAttrID                        = CValue["CUkernelNodeAttrID"]
type CUkernelNodeAttrValue                     = CValue["CUkernelNodeAttrValue"]
type CUlaunchConfig                            = CValue["CUlaunchConfig"]
type CUlibrary                                 = CValue["CUlibrary"]
type CUlibraryOption                           = CValue["CUlibraryOption"]
type CUlimit                                   = CValue["CUlimit"]
type CUlinkState                               = CValue["CUlinkState"]
type CUlogIterator                             = CValue["CUlogIterator"]
type CUlogicalEndpointId                       = CValue["CUlogicalEndpointId"]
type CUlogicalEndpointIpcHandleType            = CValue["CUlogicalEndpointIpcHandleType"]
type CUlogicalEndpointProp                     = CValue["CUlogicalEndpointProp"]
type CUlogsCallback                            = CValue["CUlogsCallback"]
type CUlogsCallbackHandle                      = CValue["CUlogsCallbackHandle"]
type CUmemAccessDesc                           = CValue["CUmemAccessDesc"]
type CUmemAccess_flags                         = CValue["CUmemAccess_flags"]
type CUmemAllocationGranularity_flags          = CValue["CUmemAllocationGranularity_flags"]
type CUmemAllocationHandleType                 = CValue["CUmemAllocationHandleType"]
type CUmemAllocationProp                       = CValue["CUmemAllocationProp"]
type CUmemAllocationType                       = CValue["CUmemAllocationType"]
type CUmemDecompressParams                     = CValue["CUmemDecompressParams"]
type CUmemGenericAllocationHandle              = CValue["CUmemGenericAllocationHandle"]
type CUmemLocation                             = CValue["CUmemLocation"]
type CUmemPoolProps                            = CValue["CUmemPoolProps"]
type CUmemPoolPtrExportData                    = CValue["CUmemPoolPtrExportData"]
type CUmemPool_attribute                       = CValue["CUmemPool_attribute"]
type CUmemRangeHandleType                      = CValue["CUmemRangeHandleType"]
type CUmem_advise                              = CValue["CUmem_advise"]
type CUmem_range_attribute                     = CValue["CUmem_range_attribute"]
type CUmemcpyAttributes                        = CValue["CUmemcpyAttributes"]
type CUmemoryPool                              = CValue["CUmemoryPool"]
type CUmipmappedArray                          = CValue["CUmipmappedArray"]
type CUmoduleLoadingMode                       = CValue["CUmoduleLoadingMode"]
type CUmulticastGranularity_flags              = CValue["CUmulticastGranularity_flags"]
type CUmulticastObjectProp                     = CValue["CUmulticastObjectProp"]
type CUoccupancyB2DSize                        = CValue["CUoccupancyB2DSize"]
type CUpointer_attribute                       = CValue["CUpointer_attribute"]
type CUprocessState                            = CValue["CUprocessState"]
type CUsharedconfig                            = CValue["CUsharedconfig"]
type CUstream                                  = CValue["CUstream"]
type CUstreamAttrID                            = CValue["CUstreamAttrID"]
type CUstreamAttrValue                         = CValue["CUstreamAttrValue"]
type CUstreamBatchMemOpParams                  = CValue["CUstreamBatchMemOpParams"]
type CUstreamCallback                          = CValue["CUstreamCallback"]
type CUstreamCaptureMode                       = CValue["CUstreamCaptureMode"]
type CUstreamCaptureStatus                     = CValue["CUstreamCaptureStatus"]
type CUstreamCigCaptureParams                  = CValue["CUstreamCigCaptureParams"]
type CUsurfObject                              = CValue["CUsurfObject"]
type CUsurfref                                 = CValue["CUsurfref"]
type CUtensorMap                               = CValue["CUtensorMap"]
type CUtensorMapDataType                       = CValue["CUtensorMapDataType"]
type CUtensorMapFloatOOBfill                   = CValue["CUtensorMapFloatOOBfill"]
type CUtensorMapIm2ColWideMode                 = CValue["CUtensorMapIm2ColWideMode"]
type CUtensorMapInterleave                     = CValue["CUtensorMapInterleave"]
type CUtensorMapL2promotion                    = CValue["CUtensorMapL2promotion"]
type CUtensorMapSwizzle                        = CValue["CUtensorMapSwizzle"]
type CUtexObject                               = CValue["CUtexObject"]
type CUtexref                                  = CValue["CUtexref"]
type CUuserObject                              = CValue["CUuserObject"]
type CUuuid                                    = CValue["CUuuid"]
type cudaArrayMemoryRequirements               = CValue["struct cudaArrayMemoryRequirements"]
type cudaArraySparseProperties                 = CValue["struct cudaArraySparseProperties"]
type cudaArray_const_t                         = CValue["cudaArray_const_t"]
type cudaArray_t                               = CValue["cudaArray_t"]
type cudaAsyncCallback                         = CValue["cudaAsyncCallback"]
type cudaAsyncCallbackHandle_t                 = CValue["cudaAsyncCallbackHandle_t"]
type cudaAtomicOperation                       = CValue["enum cudaAtomicOperation"]
type cudaChannelFormatDesc                     = CValue["struct cudaChannelFormatDesc"]
type cudaChannelFormatKind                     = CValue["enum cudaChannelFormatKind"]
type cudaDevResource                           = CValue["cudaDevResource"]
type cudaDevResourceDesc_t                     = CValue["cudaDevResourceDesc_t"]
type cudaDevResourceType                       = CValue["enum cudaDevResourceType"]
type cudaDevSmResourceGroupParams              = CValue["cudaDevSmResourceGroupParams"]
type cudaDeviceAttr                            = CValue["enum cudaDeviceAttr"]
type cudaDeviceP2PAttr                         = CValue["enum cudaDeviceP2PAttr"]
type cudaDeviceProp                            = CValue["struct cudaDeviceProp"]
type cudaDriverEntryPointQueryResult           = CValue["enum cudaDriverEntryPointQueryResult"]
type cudaEvent_t                               = CValue["cudaEvent_t"]
type cudaExecutionContext_t                    = CValue["cudaExecutionContext_t"]
type cudaExtent                                = CValue["struct cudaExtent"]
type cudaExternalMemoryBufferDesc              = CValue["struct cudaExternalMemoryBufferDesc"]
type cudaExternalMemoryHandleDesc              = CValue["struct cudaExternalMemoryHandleDesc"]
type cudaExternalMemoryMipmappedArrayDesc      = CValue["struct cudaExternalMemoryMipmappedArrayDesc"]
type cudaExternalMemory_t                      = CValue["cudaExternalMemory_t"]
type cudaExternalSemaphoreHandleDesc           = CValue["struct cudaExternalSemaphoreHandleDesc"]
type cudaExternalSemaphoreSignalNodeParams     = CValue["struct cudaExternalSemaphoreSignalNodeParams"]
type cudaExternalSemaphoreSignalParams         = CValue["struct cudaExternalSemaphoreSignalParams"]
type cudaExternalSemaphoreWaitNodeParams       = CValue["struct cudaExternalSemaphoreWaitNodeParams"]
type cudaExternalSemaphoreWaitParams           = CValue["struct cudaExternalSemaphoreWaitParams"]
type cudaExternalSemaphore_t                   = CValue["cudaExternalSemaphore_t"]
type cudaFlushGPUDirectRDMAWritesScope         = CValue["enum cudaFlushGPUDirectRDMAWritesScope"]
type cudaFlushGPUDirectRDMAWritesTarget        = CValue["enum cudaFlushGPUDirectRDMAWritesTarget"]
type cudaFuncAttribute                         = CValue["enum cudaFuncAttribute"]
type cudaFuncAttributes                        = CValue["struct cudaFuncAttributes"]
type cudaFuncCache                             = CValue["enum cudaFuncCache"]
type cudaFunction_t                            = CValue["cudaFunction_t"]
type cudaGraphConditionalHandle                = CValue["cudaGraphConditionalHandle"]
type cudaGraphEdgeData                         = CValue["cudaGraphEdgeData"]
type cudaGraphExecUpdateResultInfo             = CValue["cudaGraphExecUpdateResultInfo"]
type cudaGraphExec_t                           = CValue["cudaGraphExec_t"]
type cudaGraphInstantiateParams                = CValue["cudaGraphInstantiateParams"]
type cudaGraphMemAttributeType                 = CValue["enum cudaGraphMemAttributeType"]
type cudaGraphNodeParams                       = CValue["struct cudaGraphNodeParams"]
type cudaGraphNodeType                         = CValue["enum cudaGraphNodeType"]
type cudaGraphNode_t                           = CValue["cudaGraphNode_t"]
type cudaGraphRecaptureCallbackData            = CValue["struct cudaGraphRecaptureCallbackData"]
type cudaGraph_t                               = CValue["cudaGraph_t"]
type cudaGraphicsResource_t                    = CValue["cudaGraphicsResource_t"]
type cudaHostFn_t                              = CValue["cudaHostFn_t"]
type cudaHostNodeParams                        = CValue["struct cudaHostNodeParams"]
type cudaIpcEventHandle_t                      = CValue["cudaIpcEventHandle_t"]
type cudaIpcMemHandle_t                        = CValue["cudaIpcMemHandle_t"]
type cudaJitOption                             = CValue["enum cudaJitOption"]
type cudaKernelNodeParams                      = CValue["struct cudaKernelNodeParams"]
type cudaKernel_t                              = CValue["cudaKernel_t"]
type cudaLaunchAttributeID                     = CValue["cudaLaunchAttributeID"]
type cudaLaunchAttributeValue                  = CValue["cudaLaunchAttributeValue"]
type cudaLaunchConfig_t                        = CValue["cudaLaunchConfig_t"]
type cudaLibraryOption                         = CValue["enum cudaLibraryOption"]
type cudaLibrary_t                             = CValue["cudaLibrary_t"]
type cudaLimit                                 = CValue["enum cudaLimit"]
type cudaLogIterator                           = CValue["cudaLogIterator"]
type cudaLogsCallbackHandle                    = CValue["cudaLogsCallbackHandle"]
type cudaLogsCallback_t                        = CValue["cudaLogsCallback_t"]
type cudaMemAccessDesc                         = CValue["struct cudaMemAccessDesc"]
type cudaMemAccessFlags                        = CValue["enum cudaMemAccessFlags"]
type cudaMemAllocNodeParams                    = CValue["struct cudaMemAllocNodeParams"]
type cudaMemAllocationHandleType               = CValue["enum cudaMemAllocationHandleType"]
type cudaMemAllocationType                     = CValue["enum cudaMemAllocationType"]
type cudaMemLocation                           = CValue["struct cudaMemLocation"]
type cudaMemPoolAttr                           = CValue["enum cudaMemPoolAttr"]
type cudaMemPoolProps                          = CValue["struct cudaMemPoolProps"]
type cudaMemPoolPtrExportData                  = CValue["struct cudaMemPoolPtrExportData"]
type cudaMemPool_t                             = CValue["cudaMemPool_t"]
type cudaMemRangeAttribute                     = CValue["enum cudaMemRangeAttribute"]
type cudaMemcpy3DBatchOp                       = CValue["struct cudaMemcpy3DBatchOp"]
type cudaMemcpy3DParms                         = CValue["struct cudaMemcpy3DParms"]
type cudaMemcpy3DPeerParms                     = CValue["struct cudaMemcpy3DPeerParms"]
type cudaMemcpyAttributes                      = CValue["struct cudaMemcpyAttributes"]
type cudaMemcpyKind                            = CValue["enum cudaMemcpyKind"]
type cudaMemoryAdvise                          = CValue["enum cudaMemoryAdvise"]
type cudaMemsetParams                          = CValue["struct cudaMemsetParams"]
type cudaMipmappedArray_const_t                = CValue["cudaMipmappedArray_const_t"]
type cudaMipmappedArray_t                      = CValue["cudaMipmappedArray_t"]
type cudaPitchedPtr                            = CValue["struct cudaPitchedPtr"]
type cudaPointerAttributes                     = CValue["struct cudaPointerAttributes"]
type cudaResourceDesc                          = CValue["struct cudaResourceDesc"]
type cudaResourceViewDesc                      = CValue["struct cudaResourceViewDesc"]
type cudaSharedMemConfig                       = CValue["enum cudaSharedMemConfig"]
type cudaStreamCallback_t                      = CValue["cudaStreamCallback_t"]
type cudaStreamCaptureMode                     = CValue["enum cudaStreamCaptureMode"]
type cudaStreamCaptureStatus                   = CValue["enum cudaStreamCaptureStatus"]
type cudaStream_t                              = CValue["cudaStream_t"]
type cudaSurfaceObject_t                       = CValue["cudaSurfaceObject_t"]
type cudaTextureDesc                           = CValue["struct cudaTextureDesc"]
type cudaTextureObject_t                       = CValue["cudaTextureObject_t"]
type cudaUUID_t                                = CValue["cudaUUID_t"]
type cudaUserObject_t                          = CValue["cudaUserObject_t"]
type cuuint32_t                                = CValue["cuuint32_t"]
type cuuint64_t                                = CValue["cuuint64_t"]
type dim3                                      = CValue["dim3"]
type unsigned_long_long                        = CValue["unsigned long long"]
type unsigned_short                            = CValue["unsigned short"]
type void_const                                = CValue["void *const"]
type GemstoneDim3                              = CValue["GemstoneDim3"]

trait CUDA {
  def __cudaGemstoneManagedRegister(pointer: DevicePtr, bytesize: SizeT): CUDAStatus
  def __cudaGemstoneManagedUnregister(pointer: DevicePtr): CUDAStatus

  @CSignature("void **(void *)")
  @NativeDelegate("gemstone_cudaRegisterFatBinary")
  def __cudaRegisterFatBinary(fatBinary: OpaquePointer): OpaquePointer =
    delegate["gemstone_cudaRegisterFatBinary"](fatBinary)

  @CSignature("void (void **)")
  @NativeDelegate("gemstone_cudaRegisterFatBinaryEnd")
  def __cudaRegisterFatBinaryEnd(handle: OpaquePointer): Unit =
    delegate["gemstone_cudaRegisterFatBinaryEnd"](handle)

  @CSignature("void (void **, const char *, char *, const char *, int32_t, void *, void *, void *, void *, int32_t *)")
  @NativeDelegate("gemstone_cudaRegisterFunction")
  def __cudaRegisterFunction(
      handle: OpaquePointer,
      hostFunction: Pointer[Byte, Read, Opaque],
      deviceFunction: Pointer[Byte, ReadWrite, Opaque],
      deviceName: CString,
      threadLimit: Int,
      threadId: OpaquePointer,
      blockId: OpaquePointer,
      blockDimension: OpaquePointer,
      gridDimension: OpaquePointer,
      warpSize: Pointer[Int, ReadWrite, Opaque]
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

  @CSignature("void (void **, char *, char *, const char *, int32_t, size_t, int32_t, int32_t)")
  @NativeDelegate("gemstone_cudaRegisterVar")
  def __cudaRegisterVar(
      handle: OpaquePointer,
      hostVariable: Pointer[Byte, ReadWrite, Opaque],
      deviceAddress: Pointer[Byte, ReadWrite, Opaque],
      deviceName: CString,
      external: Int,
      size: SizeT,
      constant: Int,
      global: Int
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

  @CSignature("void (void **)")
  @NativeDelegate("gemstone_cudaUnregisterFatBinary")
  def __cudaUnregisterFatBinary(handle: OpaquePointer): Unit =
    delegate["gemstone_cudaUnregisterFatBinary"](handle)

  @CSignature("int32_t (void **, const void *)")
  @NativeDelegate("gemstone_cudaGetKernel")
  def __cudaGetKernel(kernel: Out[OpaquePointer], hostFunction: Pointer[CVoid, Read, Opaque]): Int =
    delegate["gemstone_cudaGetKernel"](kernel, hostFunction)

  @CSignature("unsigned int (GemstoneDim3, GemstoneDim3, size_t, void *)")
  @NativeDelegate("gemstone_cudaPushCallConfiguration")
  def __cudaPushCallConfiguration(
      grid: GemstoneDim3,
      block: GemstoneDim3,
      sharedMemory: SizeT,
      stream: OpaquePointer
  ): UInt32 = delegate["gemstone_cudaPushCallConfiguration"](grid, block, sharedMemory, stream)

  @CSignature("int32_t (GemstoneDim3 *, GemstoneDim3 *, size_t *, void *)")
  @NativeDelegate("gemstone_cudaPopCallConfiguration")
  def __cudaPopCallConfiguration(
      grid: Out[GemstoneDim3],
      block: Out[GemstoneDim3],
      sharedMemory: Out[SizeT],
      stream: Out[OpaquePointer]
  ): Int = delegate["gemstone_cudaPopCallConfiguration"](grid, block, sharedMemory, stream)

  @CSignature("int32_t (void *, GemstoneDim3, GemstoneDim3, void **, size_t, void *)")
  @NativeDelegate("gemstone_cudaLaunchKernel")
  def __cudaLaunchKernel(
      kernel: OpaquePointer,
      grid: GemstoneDim3,
      block: GemstoneDim3,
      arguments: Pointer[OpaquePointer, ReadWrite, Opaque],
      sharedMemory: SizeT,
      stream: OpaquePointer
  ): Int = delegate["gemstone_cudaLaunchKernel"](kernel, grid, block, arguments, sharedMemory, stream)

  @CSignature("CUresult (CUarray *, const CUDA_ARRAY3D_DESCRIPTOR *)")
  def cuArray3DCreate_v2(
      pHandle: Pointer[CUarray, ReadWrite, Opaque],
      pAllocateArray: Pointer[CUDA_ARRAY3D_DESCRIPTOR, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUDA_ARRAY3D_DESCRIPTOR *, CUarray)")
  def cuArray3DGetDescriptor_v2(
      pArrayDescriptor: Pointer[CUDA_ARRAY3D_DESCRIPTOR, ReadWrite, Opaque],
      hArray: CUarray
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUarray *, const CUDA_ARRAY_DESCRIPTOR *)")
  def cuArrayCreate_v2(
      pHandle: Pointer[CUarray, ReadWrite, Opaque],
      pAllocateArray: Pointer[CUDA_ARRAY_DESCRIPTOR, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUarray)")
  def cuArrayDestroy(hArray: CUarray): CUDAStatus = unsupported
  @CSignature("CUresult (CUDA_ARRAY_DESCRIPTOR *, CUarray)")
  def cuArrayGetDescriptor_v2(
      pArrayDescriptor: Pointer[CUDA_ARRAY_DESCRIPTOR, ReadWrite, Opaque],
      hArray: CUarray
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUDA_ARRAY_MEMORY_REQUIREMENTS *, CUarray, CUdevice)")
  def cuArrayGetMemoryRequirements(
      memoryRequirements: Pointer[CUDA_ARRAY_MEMORY_REQUIREMENTS, ReadWrite, Opaque],
      array: CUarray,
      device: Device
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUarray *, CUarray, unsigned int)")
  def cuArrayGetPlane(pPlaneArray: Pointer[CUarray, ReadWrite, Opaque], hArray: CUarray, planeIdx: UInt32): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUDA_ARRAY_SPARSE_PROPERTIES *, CUarray)")
  def cuArrayGetSparseProperties(
      sparseProperties: Pointer[CUDA_ARRAY_SPARSE_PROPERTIES, ReadWrite, Opaque],
      array: CUarray
  ): CUDAStatus = unsupported
  @CSignature("CUresult (int, CUcheckpointCheckpointArgs *)")
  def cuCheckpointProcessCheckpoint(
      pid: Int,
      args: Pointer[CUcheckpointCheckpointArgs, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (int, int *)")
  def cuCheckpointProcessGetRestoreThreadId(pid: Int, tid: Pointer[Int, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (int, CUprocessState *)")
  def cuCheckpointProcessGetState(pid: Int, state: Pointer[CUprocessState, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (int, CUcheckpointLockArgs *)")
  def cuCheckpointProcessLock(pid: Int, args: Pointer[CUcheckpointLockArgs, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (int, CUcheckpointRestoreArgs *)")
  def cuCheckpointProcessRestore(pid: Int, args: Pointer[CUcheckpointRestoreArgs, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (int, CUcheckpointUnlockArgs *)")
  def cuCheckpointProcessUnlock(pid: Int, args: Pointer[CUcheckpointUnlockArgs, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUcoredumpCallbackHandle)")
  def cuCoredumpDeregisterCompleteCallback(callback: CUcoredumpCallbackHandle): CUDAStatus = unsupported
  @CSignature("CUresult (CUcoredumpCallbackHandle)")
  def cuCoredumpDeregisterStartCallback(callback: CUcoredumpCallbackHandle): CUDAStatus = unsupported
  @CSignature("CUresult (CUcoredumpSettings, void *, size_t *)")
  def cuCoredumpGetAttribute(
      attrib: CUcoredumpSettings,
      value: Pointer[CVoid, ReadWrite, Opaque],
      size: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUcoredumpSettings, void *, size_t *)")
  def cuCoredumpGetAttributeGlobal(
      attrib: CUcoredumpSettings,
      value: Pointer[CVoid, ReadWrite, Opaque],
      size: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUcoredumpStatusCallback, void *, CUcoredumpCallbackHandle *)")
  def cuCoredumpRegisterCompleteCallback(
      callback: CUcoredumpStatusCallback,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      callbackOut: Pointer[CUcoredumpCallbackHandle, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUcoredumpStatusCallback, void *, CUcoredumpCallbackHandle *)")
  def cuCoredumpRegisterStartCallback(
      callback: CUcoredumpStatusCallback,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      callbackOut: Pointer[CUcoredumpCallbackHandle, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUcoredumpSettings, void *, size_t *)")
  def cuCoredumpSetAttribute(
      attrib: CUcoredumpSettings,
      value: Pointer[CVoid, ReadWrite, Opaque],
      size: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUcoredumpSettings, void *, size_t *)")
  def cuCoredumpSetAttributeGlobal(
      attrib: CUcoredumpSettings,
      value: Pointer[CVoid, ReadWrite, Opaque],
      size: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext *, unsigned int)")
  def cuCtxAttach(pctx: Pointer[Context, ReadWrite, Opaque], flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext *, CUctxCreateParams *, unsigned int, CUdevice)")
  @NativeDelegate("gemstone_cuCtxCreate_v4")
  def cuCtxCreate_v4(
      pctx: Out[Context],
      ctxCreateParams: Nullable[InOut[CUctxCreateParams]],
      flags: UInt32,
      dev: Device
  ): CUDAStatus =
    delegate["gemstone_cuCtxCreate_v4"](pctx, ctxCreateParams, flags, dev)
  @CSignature("CUresult (CUcontext *, unsigned int, CUdevice)")
  def cuCtxCreate_v2(pctx: Out[Context], flags: UInt32, dev: Device): CUDAStatus
  @CSignature("CUresult (CUcontext)")
  def cuCtxDestroy_v2(ctx: Context): CUDAStatus
  @CSignature("CUresult (CUcontext)")
  def cuCtxDetach(ctx: Context): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext)")
  def cuCtxDisablePeerAccess(peerContext: Context): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext, unsigned int)")
  def cuCtxEnablePeerAccess(peerContext: Context, Flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext *, CUgreenCtx)")
  def cuCtxFromGreenCtx(pContext: Pointer[Context, ReadWrite, Opaque], hCtx: CUgreenCtx): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext, unsigned int *)")
  def cuCtxGetApiVersion(ctx: Context, version: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunc_cache *)")
  def cuCtxGetCacheConfig(pconfig: Pointer[CUfunc_cache, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext *)")
  def cuCtxGetCurrent(pctx: Pointer[Context, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext, CUdevResource *, CUdevResourceType)")
  def cuCtxGetDevResource(
      hCtx: Context,
      resource: Pointer[CUdevResource, ReadWrite, Opaque],
      `type`: CUdevResourceType
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevice *)")
  def cuCtxGetDevice(device: Pointer[Device, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevice *, CUcontext)")
  def cuCtxGetDevice_v2(device: Pointer[Device, ReadWrite, Opaque], ctx: Context): CUDAStatus = unsupported
  @CSignature("CUresult (CUexecAffinityParam *, CUexecAffinityType)")
  def cuCtxGetExecAffinity(
      pExecAffinity: Pointer[CUexecAffinityParam, ReadWrite, Opaque],
      `type`: CUexecAffinityType
  ): CUDAStatus = unsupported
  @CSignature("CUresult (unsigned int *)")
  def cuCtxGetFlags(flags: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext, unsigned long long *)")
  def cuCtxGetId(ctx: Context, ctxId: Pointer[unsigned_long_long, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (size_t *, CUlimit)")
  def cuCtxGetLimit(pvalue: Pointer[SizeT, ReadWrite, Opaque], limit: CUlimit): CUDAStatus = unsupported
  @CSignature("CUresult (CUsharedconfig *)")
  def cuCtxGetSharedMemConfig(pConfig: Pointer[CUsharedconfig, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (int *, int *)")
  def cuCtxGetStreamPriorityRange(
      leastPriority: Pointer[Int, ReadWrite, Opaque],
      greatestPriority: Pointer[Int, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext *)")
  def cuCtxPopCurrent_v2(pctx: Pointer[Context, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext)")
  def cuCtxPushCurrent_v2(ctx: Context): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext, CUevent)")
  def cuCtxRecordEvent(hCtx: Context, hEvent: CUevent): CUDAStatus = unsupported
  @CSignature("CUresult (void)")
  def cuCtxResetPersistingL2Cache(): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunc_cache)")
  def cuCtxSetCacheConfig(config: CUfunc_cache): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext)")
  def cuCtxSetCurrent(ctx: Context): CUDAStatus = unsupported
  @CSignature("CUresult (unsigned int)")
  def cuCtxSetFlags(flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUlimit, size_t)")
  def cuCtxSetLimit(limit: CUlimit, value: SizeT): CUDAStatus = unsupported
  @CSignature("CUresult (CUsharedconfig)")
  def cuCtxSetSharedMemConfig(config: CUsharedconfig): CUDAStatus = unsupported
  @CSignature("CUresult (void)")
  def cuCtxSynchronize(): CUDAStatus
  @CSignature("CUresult (CUcontext)")
  def cuCtxSynchronize_v2(ctx: Context): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext, CUevent)")
  def cuCtxWaitEvent(hCtx: Context, hEvent: CUevent): CUDAStatus = unsupported
  @CSignature("CUresult (CUexternalMemory)")
  def cuDestroyExternalMemory(extMem: CUexternalMemory): CUDAStatus = unsupported
  @CSignature("CUresult (CUexternalSemaphore)")
  def cuDestroyExternalSemaphore(extSem: CUexternalSemaphore): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevResourceDesc *, CUdevResource *, unsigned int)")
  def cuDevResourceGenerateDesc(
      phDesc: Pointer[CUdevResourceDesc, ReadWrite, Opaque],
      resources: Pointer[CUdevResource, ReadWrite, Opaque],
      nbResources: UInt32
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUdevResource *, unsigned int, const CUdevResource *, CUdevResource *, unsigned int, CU_DEV_SM_RESOURCE_GROUP_PARAMS *)"
  )
  def cuDevSmResourceSplit(
      result: Pointer[CUdevResource, ReadWrite, Opaque],
      nbGroups: UInt32,
      input: Pointer[CUdevResource, Read, Opaque],
      remainder: Pointer[CUdevResource, ReadWrite, Opaque],
      flags: UInt32,
      groupParams: Pointer[CU_DEV_SM_RESOURCE_GROUP_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUdevResource *, unsigned int *, const CUdevResource *, CUdevResource *, unsigned int, unsigned int)"
  )
  def cuDevSmResourceSplitByCount(
      result: Pointer[CUdevResource, ReadWrite, Opaque],
      nbGroups: Pointer[UInt32, ReadWrite, Opaque],
      input: Pointer[CUdevResource, Read, Opaque],
      remainder: Pointer[CUdevResource, ReadWrite, Opaque],
      flags: UInt32,
      minCount: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (int *, CUdevice, CUdevice)")
  def cuDeviceCanAccessPeer(canAccessPeer: Pointer[Int, ReadWrite, Opaque], dev: Device, peerDev: Device): CUDAStatus =
    unsupported
  @CSignature("CUresult (int *, int *, CUdevice)")
  def cuDeviceComputeCapability(
      major: Pointer[Int, ReadWrite, Opaque],
      minor: Pointer[Int, ReadWrite, Opaque],
      dev: Device
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevice *, int)")
  def cuDeviceGet(device: Out[Device], ordinal: Int): CUDAStatus
  @CSignature("CUresult (int *, CUdevice_attribute, CUdevice)")
  def cuDeviceGetAttribute(pi: Pointer[Int, ReadWrite, Opaque], attrib: CUdevice_attribute, dev: Device): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUdevice *, const char *)")
  def cuDeviceGetByPCIBusId(dev: Pointer[Device, ReadWrite, Opaque], pciBusId: CString): CUDAStatus = unsupported
  @CSignature("CUresult (int *)")
  def cuDeviceGetCount(count: Out[Int]): CUDAStatus
  @CSignature("CUresult (CUmemoryPool *, CUdevice)")
  def cuDeviceGetDefaultMemPool(pool_out: Pointer[CUmemoryPool, ReadWrite, Opaque], dev: Device): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUdevice, CUdevResource *, CUdevResourceType)")
  def cuDeviceGetDevResource(
      device: Device,
      resource: Pointer[CUdevResource, ReadWrite, Opaque],
      `type`: CUdevResourceType
  ): CUDAStatus = unsupported
  @CSignature("CUresult (int *, CUexecAffinityType, CUdevice)")
  def cuDeviceGetExecAffinitySupport(
      pi: Pointer[Int, ReadWrite, Opaque],
      `type`: CUexecAffinityType,
      dev: Device
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevice, CUgraphMem_attribute, void *)")
  def cuDeviceGetGraphMemAttribute(
      device: Device,
      attr: CUgraphMem_attribute,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (unsigned int *, const CUatomicOperation *, unsigned int, CUdevice)")
  def cuDeviceGetHostAtomicCapabilities(
      capabilities: Pointer[UInt32, ReadWrite, Opaque],
      operations: Pointer[CUatomicOperation, Read, Opaque],
      count: UInt32,
      dev: Device
  ): CUDAStatus = unsupported
  @CSignature("CUresult (char *, unsigned int *, CUdevice)")
  def cuDeviceGetLuid(
      luid: Pointer[Byte, ReadWrite, Opaque],
      deviceNodeMask: Pointer[UInt32, ReadWrite, Opaque],
      dev: Device
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemoryPool *, CUdevice)")
  def cuDeviceGetMemPool(pool: Pointer[CUmemoryPool, ReadWrite, Opaque], dev: Device): CUDAStatus = unsupported
  @CSignature("CUresult (char *, int, CUdevice)")
  def cuDeviceGetName(name: Pointer[Byte, ReadWrite, Opaque], len: Int, dev: Device): CUDAStatus = unsupported
  @CSignature("CUresult (void *, CUdevice, int)")
  def cuDeviceGetNvSciSyncAttributes(
      nvSciSyncAttrList: Pointer[CVoid, ReadWrite, Opaque],
      dev: Device,
      flags: Int
  ): CUDAStatus = unsupported
  @CSignature("CUresult (unsigned int *, const CUatomicOperation *, unsigned int, CUdevice, CUdevice)")
  def cuDeviceGetP2PAtomicCapabilities(
      capabilities: Pointer[UInt32, ReadWrite, Opaque],
      operations: Pointer[CUatomicOperation, Read, Opaque],
      count: UInt32,
      srcDevice: Device,
      dstDevice: Device
  ): CUDAStatus = unsupported
  @CSignature("CUresult (int *, CUdevice_P2PAttribute, CUdevice, CUdevice)")
  def cuDeviceGetP2PAttribute(
      value: Pointer[Int, ReadWrite, Opaque],
      attrib: CUdevice_P2PAttribute,
      srcDevice: Device,
      dstDevice: Device
  ): CUDAStatus = unsupported
  @CSignature("CUresult (char *, int, CUdevice)")
  def cuDeviceGetPCIBusId(pciBusId: Pointer[Byte, ReadWrite, Opaque], len: Int, dev: Device): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevprop *, CUdevice)")
  def cuDeviceGetProperties(prop: Pointer[CUdevprop, ReadWrite, Opaque], dev: Device): CUDAStatus = unsupported
  @CSignature("CUresult (size_t *, CUarray_format, unsigned int, CUdevice)")
  def cuDeviceGetTexture1DLinearMaxWidth(
      maxWidthInElements: Pointer[SizeT, ReadWrite, Opaque],
      format: CUarray_format,
      numChannels: UInt32,
      dev: Device
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUuuid *, CUdevice)")
  def cuDeviceGetUuid_v2(uuid: Pointer[CUuuid, ReadWrite, Opaque], dev: Device): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevice)")
  def cuDeviceGraphMemTrim(device: Device): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevice, unsigned int *, int *)")
  def cuDevicePrimaryCtxGetState(
      dev: Device,
      flags: Pointer[UInt32, ReadWrite, Opaque],
      active: Pointer[Int, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevice)")
  def cuDevicePrimaryCtxRelease_v2(dev: Device): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevice)")
  def cuDevicePrimaryCtxReset_v2(dev: Device): CUDAStatus = unsupported
  @CSignature("CUresult (CUcontext *, CUdevice)")
  def cuDevicePrimaryCtxRetain(pctx: Pointer[Context, ReadWrite, Opaque], dev: Device): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevice, unsigned int)")
  def cuDevicePrimaryCtxSetFlags_v2(dev: Device, flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevice, CUasyncCallback, void *, CUasyncCallbackHandle *)")
  def cuDeviceRegisterAsyncNotification(
      device: Device,
      callbackFunc: CUasyncCallback,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      callback: Pointer[CUasyncCallbackHandle, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevice, CUgraphMem_attribute, void *)")
  def cuDeviceSetGraphMemAttribute(
      device: Device,
      attr: CUgraphMem_attribute,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevice, CUmemoryPool)")
  def cuDeviceSetMemPool(dev: Device, pool: CUmemoryPool): CUDAStatus = unsupported
  @CSignature("CUresult (size_t *, CUdevice)")
  def cuDeviceTotalMem_v2(bytes: Pointer[SizeT, ReadWrite, Opaque], dev: Device): CUDAStatus = unsupported
  @CSignature("CUresult (CUdevice, CUasyncCallbackHandle)")
  def cuDeviceUnregisterAsyncNotification(device: Device, callback: CUasyncCallbackHandle): CUDAStatus = unsupported
  @CSignature("CUresult (int *)")
  def cuDriverGetVersion(driverVersion: Out[Int]): CUDAStatus
  @CSignature("CUresult (CUevent *, unsigned int)")
  def cuEventCreate(phEvent: Pointer[CUevent, ReadWrite, Opaque], Flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUevent)")
  def cuEventDestroy_v2(hEvent: CUevent): CUDAStatus = unsupported
  @CSignature("CUresult (float *, CUevent, CUevent)")
  def cuEventElapsedTime_v2(
      pMilliseconds: Pointer[Float, ReadWrite, Opaque],
      hStart: CUevent,
      hEnd: CUevent
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUevent)")
  def cuEventQuery(hEvent: CUevent): CUDAStatus = unsupported
  @CSignature("CUresult (CUevent, CUstream)")
  def cuEventRecord(hEvent: CUevent, hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (CUevent, CUstream, unsigned int)")
  def cuEventRecordWithFlags(hEvent: CUevent, hStream: CUstream, flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUevent)")
  def cuEventSynchronize(hEvent: CUevent): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr *, CUexternalMemory, const CUDA_EXTERNAL_MEMORY_BUFFER_DESC *)")
  def cuExternalMemoryGetMappedBuffer(
      devPtr: Pointer[DevicePtr, ReadWrite, Opaque],
      extMem: CUexternalMemory,
      bufferDesc: Pointer[CUDA_EXTERNAL_MEMORY_BUFFER_DESC, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmipmappedArray *, CUexternalMemory, const CUDA_EXTERNAL_MEMORY_MIPMAPPED_ARRAY_DESC *)")
  def cuExternalMemoryGetMappedMipmappedArray(
      mipmap: Pointer[CUmipmappedArray, ReadWrite, Opaque],
      extMem: CUexternalMemory,
      mipmapDesc: Pointer[CUDA_EXTERNAL_MEMORY_MIPMAPPED_ARRAY_DESC, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUflushGPUDirectRDMAWritesTarget, CUflushGPUDirectRDMAWritesScope)")
  def cuFlushGPUDirectRDMAWrites(
      target: CUflushGPUDirectRDMAWritesTarget,
      scope: CUflushGPUDirectRDMAWritesScope
  ): CUDAStatus = unsupported
  @CSignature("CUresult (int *, CUfunction_attribute, CUfunction)")
  def cuFuncGetAttribute(
      pi: Pointer[Int, ReadWrite, Opaque],
      attrib: CUfunction_attribute,
      hfunc: CUDAFunction
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmodule *, CUfunction)")
  def cuFuncGetModule(hmod: Pointer[CUDAModule, ReadWrite, Opaque], hfunc: CUDAFunction): CUDAStatus = unsupported
  @CSignature("CUresult (const char **, CUfunction)")
  def cuFuncGetName(name: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque], hfunc: CUDAFunction): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUfunction, size_t *)")
  def cuFuncGetParamCount(func: CUDAFunction, paramCount: Pointer[SizeT, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction, size_t, size_t *, size_t *)")
  def cuFuncGetParamInfo(
      func: CUDAFunction,
      paramIndex: SizeT,
      paramOffset: Pointer[SizeT, ReadWrite, Opaque],
      paramSize: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunctionLoadingState *, CUfunction)")
  def cuFuncIsLoaded(state: Pointer[CUfunctionLoadingState, ReadWrite, Opaque], function: CUDAFunction): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUfunction)")
  def cuFuncLoad(function: CUDAFunction): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction, CUfunction_attribute, int)")
  def cuFuncSetAttribute(hfunc: CUDAFunction, attrib: CUfunction_attribute, value: Int): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction, int, int, int)")
  def cuFuncSetBlockShape(hfunc: CUDAFunction, x: Int, y: Int, z: Int): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction, CUfunc_cache)")
  def cuFuncSetCacheConfig(hfunc: CUDAFunction, config: CUfunc_cache): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction, CUsharedconfig)")
  def cuFuncSetSharedMemConfig(hfunc: CUDAFunction, config: CUsharedconfig): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction, unsigned int)")
  def cuFuncSetSharedSize(hfunc: CUDAFunction, bytes: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUresult, const char **)")
  def cuGetErrorName(error: CUDAStatus, pStr: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUresult, const char **)")
  def cuGetErrorString(error: CUDAStatus, pStr: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (const void **, const CUuuid *)")
  def cuGetExportTable(
      ppExportTable: Pointer[Pointer[CVoid, ReadWrite, Opaque], Read, Opaque],
      pExportTableId: Pointer[CUuuid, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (const char *, void **, int, cuuint64_t, CUdriverProcAddressQueryResult *)")
  def cuGetProcAddress_v2(
      symbol: CString,
      pfn: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      cudaVersion: Int,
      flags: cuuint64_t,
      symbolStatus: Pointer[CUdriverProcAddressQueryResult, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode *, CUgraph, const CUgraphNode *, size_t, const CUDA_BATCH_MEM_OP_NODE_PARAMS *)")
  def cuGraphAddBatchMemOpNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[CUDA_BATCH_MEM_OP_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode *, CUgraph, const CUgraphNode *, size_t, CUgraph)")
  def cuGraphAddChildGraphNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      childGraph: CUgraph
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraph, const CUgraphNode *, const CUgraphNode *, const CUgraphEdgeData *, size_t)")
  def cuGraphAddDependencies_v2(
      hGraph: CUgraph,
      from: Pointer[CUgraphNode, Read, Opaque],
      to: Pointer[CUgraphNode, Read, Opaque],
      edgeData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: SizeT
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode *, CUgraph, const CUgraphNode *, size_t)")
  def cuGraphAddEmptyNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode *, CUgraph, const CUgraphNode *, size_t, CUevent)")
  def cuGraphAddEventRecordNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      event: CUevent
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode *, CUgraph, const CUgraphNode *, size_t, CUevent)")
  def cuGraphAddEventWaitNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      event: CUevent
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode *, CUgraph, const CUgraphNode *, size_t, const CUDA_EXT_SEM_SIGNAL_NODE_PARAMS *)")
  def cuGraphAddExternalSemaphoresSignalNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[CUDA_EXT_SEM_SIGNAL_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode *, CUgraph, const CUgraphNode *, size_t, const CUDA_EXT_SEM_WAIT_NODE_PARAMS *)")
  def cuGraphAddExternalSemaphoresWaitNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[CUDA_EXT_SEM_WAIT_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode *, CUgraph, const CUgraphNode *, size_t, const CUDA_HOST_NODE_PARAMS *)")
  def cuGraphAddHostNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[CUDA_HOST_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode *, CUgraph, const CUgraphNode *, size_t, const CUDA_KERNEL_NODE_PARAMS *)")
  def cuGraphAddKernelNode_v2(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[CUDA_KERNEL_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode *, CUgraph, const CUgraphNode *, size_t, CUDA_MEM_ALLOC_NODE_PARAMS *)")
  def cuGraphAddMemAllocNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[CUDA_MEM_ALLOC_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode *, CUgraph, const CUgraphNode *, size_t, CUdeviceptr)")
  def cuGraphAddMemFreeNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      dptr: DevicePtr
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode *, CUgraph, const CUgraphNode *, size_t, const CUDA_MEMCPY3D *, CUcontext)")
  def cuGraphAddMemcpyNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      copyParams: Pointer[CUDA_MEMCPY3D, Read, Opaque],
      ctx: Context
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUgraphNode *, CUgraph, const CUgraphNode *, size_t, const CUDA_MEMSET_NODE_PARAMS *, CUcontext)"
  )
  def cuGraphAddMemsetNode(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      numDependencies: SizeT,
      memsetParams: Pointer[CUDA_MEMSET_NODE_PARAMS, Read, Opaque],
      ctx: Context
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUgraphNode *, CUgraph, const CUgraphNode *, const CUgraphEdgeData *, size_t, CUgraphNodeParams *)"
  )
  def cuGraphAddNode_v2(
      phGraphNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      dependencyData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[CUgraphNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUDA_BATCH_MEM_OP_NODE_PARAMS *)")
  def cuGraphBatchMemOpNodeGetParams(
      hNode: CUgraphNode,
      nodeParams_out: Pointer[CUDA_BATCH_MEM_OP_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, const CUDA_BATCH_MEM_OP_NODE_PARAMS *)")
  def cuGraphBatchMemOpNodeSetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_BATCH_MEM_OP_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUgraph *)")
  def cuGraphChildGraphNodeGetGraph(hNode: CUgraphNode, phGraph: Pointer[CUgraph, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgraph *, CUgraph)")
  def cuGraphClone(phGraphClone: Pointer[CUgraph, ReadWrite, Opaque], originalGraph: CUgraph): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphConditionalHandle *, CUgraph, CUcontext, unsigned int, unsigned int)")
  def cuGraphConditionalHandleCreate(
      pHandle_out: Pointer[CUgraphConditionalHandle, ReadWrite, Opaque],
      hGraph: CUgraph,
      ctx: Context,
      defaultLaunchValue: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraph *, unsigned int)")
  def cuGraphCreate(phGraph: Pointer[CUgraph, ReadWrite, Opaque], flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraph, const char *, unsigned int)")
  def cuGraphDebugDotPrint(hGraph: CUgraph, path: CString, flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraph)")
  def cuGraphDestroy(hGraph: CUgraph): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode)")
  def cuGraphDestroyNode(hNode: CUgraphNode): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUevent *)")
  def cuGraphEventRecordNodeGetEvent(hNode: CUgraphNode, event_out: Pointer[CUevent, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgraphNode, CUevent)")
  def cuGraphEventRecordNodeSetEvent(hNode: CUgraphNode, event: CUevent): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUevent *)")
  def cuGraphEventWaitNodeGetEvent(hNode: CUgraphNode, event_out: Pointer[CUevent, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgraphNode, CUevent)")
  def cuGraphEventWaitNodeSetEvent(hNode: CUgraphNode, event: CUevent): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec, CUgraphNode, const CUDA_BATCH_MEM_OP_NODE_PARAMS *)")
  def cuGraphExecBatchMemOpNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_BATCH_MEM_OP_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec, CUgraphNode, CUgraph)")
  def cuGraphExecChildGraphNodeSetParams(hGraphExec: CUgraphExec, hNode: CUgraphNode, childGraph: CUgraph): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgraphExec)")
  def cuGraphExecDestroy(hGraphExec: CUgraphExec): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec, CUgraphNode, CUevent)")
  def cuGraphExecEventRecordNodeSetEvent(hGraphExec: CUgraphExec, hNode: CUgraphNode, event: CUevent): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgraphExec, CUgraphNode, CUevent)")
  def cuGraphExecEventWaitNodeSetEvent(hGraphExec: CUgraphExec, hNode: CUgraphNode, event: CUevent): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgraphExec, CUgraphNode, const CUDA_EXT_SEM_SIGNAL_NODE_PARAMS *)")
  def cuGraphExecExternalSemaphoresSignalNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_EXT_SEM_SIGNAL_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec, CUgraphNode, const CUDA_EXT_SEM_WAIT_NODE_PARAMS *)")
  def cuGraphExecExternalSemaphoresWaitNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_EXT_SEM_WAIT_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec, cuuint64_t *)")
  def cuGraphExecGetFlags(hGraphExec: CUgraphExec, flags: Pointer[cuuint64_t, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgraphExec, unsigned int *)")
  def cuGraphExecGetId(hGraphExec: CUgraphExec, graphId: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec, CUgraphNode, const CUDA_HOST_NODE_PARAMS *)")
  def cuGraphExecHostNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_HOST_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec, CUgraphNode, const CUDA_KERNEL_NODE_PARAMS *)")
  def cuGraphExecKernelNodeSetParams_v2(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_KERNEL_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec, CUgraphNode, const CUDA_MEMCPY3D *, CUcontext)")
  def cuGraphExecMemcpyNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      copyParams: Pointer[CUDA_MEMCPY3D, Read, Opaque],
      ctx: Context
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec, CUgraphNode, const CUDA_MEMSET_NODE_PARAMS *, CUcontext)")
  def cuGraphExecMemsetNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      memsetParams: Pointer[CUDA_MEMSET_NODE_PARAMS, Read, Opaque],
      ctx: Context
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec, CUgraphNode, CUgraphNodeParams *)")
  def cuGraphExecNodeSetParams(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      nodeParams: Pointer[CUgraphNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec, CUgraph, CUgraphExecUpdateResultInfo *)")
  def cuGraphExecUpdate_v2(
      hGraphExec: CUgraphExec,
      hGraph: CUgraph,
      resultInfo: Pointer[CUgraphExecUpdateResultInfo, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUDA_EXT_SEM_SIGNAL_NODE_PARAMS *)")
  def cuGraphExternalSemaphoresSignalNodeGetParams(
      hNode: CUgraphNode,
      params_out: Pointer[CUDA_EXT_SEM_SIGNAL_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, const CUDA_EXT_SEM_SIGNAL_NODE_PARAMS *)")
  def cuGraphExternalSemaphoresSignalNodeSetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_EXT_SEM_SIGNAL_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUDA_EXT_SEM_WAIT_NODE_PARAMS *)")
  def cuGraphExternalSemaphoresWaitNodeGetParams(
      hNode: CUgraphNode,
      params_out: Pointer[CUDA_EXT_SEM_WAIT_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, const CUDA_EXT_SEM_WAIT_NODE_PARAMS *)")
  def cuGraphExternalSemaphoresWaitNodeSetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_EXT_SEM_WAIT_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraph, CUgraphNode *, CUgraphNode *, CUgraphEdgeData *, size_t *)")
  def cuGraphGetEdges_v2(
      hGraph: CUgraph,
      from: Pointer[CUgraphNode, ReadWrite, Opaque],
      to: Pointer[CUgraphNode, ReadWrite, Opaque],
      edgeData: Pointer[CUgraphEdgeData, ReadWrite, Opaque],
      numEdges: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraph, unsigned int *)")
  def cuGraphGetId(hGraph: CUgraph, graphId: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraph, CUgraphNode *, size_t *)")
  def cuGraphGetNodes(
      hGraph: CUgraph,
      nodes: Pointer[CUgraphNode, ReadWrite, Opaque],
      numNodes: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraph, CUgraphNode *, size_t *)")
  def cuGraphGetRootNodes(
      hGraph: CUgraph,
      rootNodes: Pointer[CUgraphNode, ReadWrite, Opaque],
      numRootNodes: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUDA_HOST_NODE_PARAMS *)")
  def cuGraphHostNodeGetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_HOST_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, const CUDA_HOST_NODE_PARAMS *)")
  def cuGraphHostNodeSetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_HOST_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec *, CUgraph, unsigned long long)")
  def cuGraphInstantiateWithFlags(
      phGraphExec: Pointer[CUgraphExec, ReadWrite, Opaque],
      hGraph: CUgraph,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec *, CUgraph, CUDA_GRAPH_INSTANTIATE_PARAMS *)")
  def cuGraphInstantiateWithParams(
      phGraphExec: Pointer[CUgraphExec, ReadWrite, Opaque],
      hGraph: CUgraph,
      instantiateParams: Pointer[CUDA_GRAPH_INSTANTIATE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUgraphNode)")
  def cuGraphKernelNodeCopyAttributes(dst: CUgraphNode, src: CUgraphNode): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUkernelNodeAttrID, CUkernelNodeAttrValue *)")
  def cuGraphKernelNodeGetAttribute(
      hNode: CUgraphNode,
      attr: CUkernelNodeAttrID,
      value_out: Pointer[CUkernelNodeAttrValue, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUDA_KERNEL_NODE_PARAMS *)")
  def cuGraphKernelNodeGetParams_v2(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_KERNEL_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUkernelNodeAttrID, const CUkernelNodeAttrValue *)")
  def cuGraphKernelNodeSetAttribute(
      hNode: CUgraphNode,
      attr: CUkernelNodeAttrID,
      value: Pointer[CUkernelNodeAttrValue, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, const CUDA_KERNEL_NODE_PARAMS *)")
  def cuGraphKernelNodeSetParams_v2(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_KERNEL_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec, CUstream)")
  def cuGraphLaunch(hGraphExec: CUgraphExec, hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUDA_MEM_ALLOC_NODE_PARAMS *)")
  def cuGraphMemAllocNodeGetParams(
      hNode: CUgraphNode,
      params_out: Pointer[CUDA_MEM_ALLOC_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUdeviceptr *)")
  def cuGraphMemFreeNodeGetParams(hNode: CUgraphNode, dptr_out: Pointer[DevicePtr, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgraphNode, CUDA_MEMCPY3D *)")
  def cuGraphMemcpyNodeGetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_MEMCPY3D, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, const CUDA_MEMCPY3D *)")
  def cuGraphMemcpyNodeSetParams(hNode: CUgraphNode, nodeParams: Pointer[CUDA_MEMCPY3D, Read, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgraphNode, CUDA_MEMSET_NODE_PARAMS *)")
  def cuGraphMemsetNodeGetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_MEMSET_NODE_PARAMS, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, const CUDA_MEMSET_NODE_PARAMS *)")
  def cuGraphMemsetNodeSetParams(
      hNode: CUgraphNode,
      nodeParams: Pointer[CUDA_MEMSET_NODE_PARAMS, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode *, CUgraphNode, CUgraph)")
  def cuGraphNodeFindInClone(
      phNode: Pointer[CUgraphNode, ReadWrite, Opaque],
      hOriginalNode: CUgraphNode,
      hClonedGraph: CUgraph
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUgraph *)")
  def cuGraphNodeGetContainingGraph(hNode: CUgraphNode, phGraph: Pointer[CUgraph, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgraphNode, CUgraphNode *, CUgraphEdgeData *, size_t *)")
  def cuGraphNodeGetDependencies_v2(
      hNode: CUgraphNode,
      dependencies: Pointer[CUgraphNode, ReadWrite, Opaque],
      edgeData: Pointer[CUgraphEdgeData, ReadWrite, Opaque],
      numDependencies: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUgraphNode *, CUgraphEdgeData *, size_t *)")
  def cuGraphNodeGetDependentNodes_v2(
      hNode: CUgraphNode,
      dependentNodes: Pointer[CUgraphNode, ReadWrite, Opaque],
      edgeData: Pointer[CUgraphEdgeData, ReadWrite, Opaque],
      numDependentNodes: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphExec, CUgraphNode, unsigned int *)")
  def cuGraphNodeGetEnabled(
      hGraphExec: CUgraphExec,
      hNode: CUgraphNode,
      isEnabled: Pointer[UInt32, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, unsigned int *)")
  def cuGraphNodeGetLocalId(hNode: CUgraphNode, nodeId: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUgraphNodeParams *)")
  def cuGraphNodeGetParams(hNode: CUgraphNode, nodeParams: Pointer[CUgraphNodeParams, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgraphNode, unsigned long long *)")
  def cuGraphNodeGetToolsId(
      hNode: CUgraphNode,
      toolsNodeId: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUgraphNodeType *)")
  def cuGraphNodeGetType(hNode: CUgraphNode, `type`: Pointer[CUgraphNodeType, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgraphExec, CUgraphNode, unsigned int)")
  def cuGraphNodeSetEnabled(hGraphExec: CUgraphExec, hNode: CUgraphNode, isEnabled: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphNode, CUgraphNodeParams *)")
  def cuGraphNodeSetParams(hNode: CUgraphNode, nodeParams: Pointer[CUgraphNodeParams, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgraph, CUuserObject, unsigned int)")
  def cuGraphReleaseUserObject(graph: CUgraph, `object`: CUuserObject, count: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraph, const CUgraphNode *, const CUgraphNode *, const CUgraphEdgeData *, size_t)")
  def cuGraphRemoveDependencies_v2(
      hGraph: CUgraph,
      from: Pointer[CUgraphNode, Read, Opaque],
      to: Pointer[CUgraphNode, Read, Opaque],
      edgeData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: SizeT
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraph, CUuserObject, unsigned int, unsigned int)")
  def cuGraphRetainUserObject(graph: CUgraph, `object`: CUuserObject, count: UInt32, flags: UInt32): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgraphExec, CUstream)")
  def cuGraphUpload(hGraphExec: CUgraphExec, hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (unsigned int, CUgraphicsResource *, CUstream)")
  def cuGraphicsMapResources(
      count: UInt32,
      resources: Pointer[CUgraphicsResource, ReadWrite, Opaque],
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmipmappedArray *, CUgraphicsResource)")
  def cuGraphicsResourceGetMappedMipmappedArray(
      pMipmappedArray: Pointer[CUmipmappedArray, ReadWrite, Opaque],
      resource: CUgraphicsResource
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr *, size_t *, CUgraphicsResource)")
  def cuGraphicsResourceGetMappedPointer_v2(
      pDevPtr: Pointer[DevicePtr, ReadWrite, Opaque],
      pSize: Pointer[SizeT, ReadWrite, Opaque],
      resource: CUgraphicsResource
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphicsResource, unsigned int)")
  def cuGraphicsResourceSetMapFlags_v2(resource: CUgraphicsResource, flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUarray *, CUgraphicsResource, unsigned int, unsigned int)")
  def cuGraphicsSubResourceGetMappedArray(
      pArray: Pointer[CUarray, ReadWrite, Opaque],
      resource: CUgraphicsResource,
      arrayIndex: UInt32,
      mipLevel: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (unsigned int, CUgraphicsResource *, CUstream)")
  def cuGraphicsUnmapResources(
      count: UInt32,
      resources: Pointer[CUgraphicsResource, ReadWrite, Opaque],
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgraphicsResource)")
  def cuGraphicsUnregisterResource(resource: CUgraphicsResource): CUDAStatus = unsupported
  @CSignature("CUresult (CUgreenCtx *, CUdevResourceDesc, CUdevice, unsigned int)")
  def cuGreenCtxCreate(
      phCtx: Pointer[CUgreenCtx, ReadWrite, Opaque],
      desc: CUdevResourceDesc,
      dev: Device,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgreenCtx)")
  def cuGreenCtxDestroy(hCtx: CUgreenCtx): CUDAStatus = unsupported
  @CSignature("CUresult (CUgreenCtx, CUdevResource *, CUdevResourceType)")
  def cuGreenCtxGetDevResource(
      hCtx: CUgreenCtx,
      resource: Pointer[CUdevResource, ReadWrite, Opaque],
      `type`: CUdevResourceType
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgreenCtx, unsigned long long *)")
  def cuGreenCtxGetId(greenCtx: CUgreenCtx, greenCtxId: Pointer[unsigned_long_long, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUgreenCtx, CUevent)")
  def cuGreenCtxRecordEvent(hCtx: CUgreenCtx, hEvent: CUevent): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream *, CUgreenCtx, unsigned int, int)")
  def cuGreenCtxStreamCreate(
      phStream: Pointer[CUstream, ReadWrite, Opaque],
      greenCtx: CUgreenCtx,
      flags: UInt32,
      priority: Int
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUgreenCtx, CUevent)")
  def cuGreenCtxWaitEvent(hCtx: CUgreenCtx, hEvent: CUevent): CUDAStatus = unsupported
  @CSignature("CUresult (CUexternalMemory *, const CUDA_EXTERNAL_MEMORY_HANDLE_DESC *)")
  def cuImportExternalMemory(
      extMem_out: Pointer[CUexternalMemory, ReadWrite, Opaque],
      memHandleDesc: Pointer[CUDA_EXTERNAL_MEMORY_HANDLE_DESC, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUexternalSemaphore *, const CUDA_EXTERNAL_SEMAPHORE_HANDLE_DESC *)")
  def cuImportExternalSemaphore(
      extSem_out: Pointer[CUexternalSemaphore, ReadWrite, Opaque],
      semHandleDesc: Pointer[CUDA_EXTERNAL_SEMAPHORE_HANDLE_DESC, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (unsigned int)")
  def cuInit(Flags: UInt32): CUDAStatus
  @CSignature("CUresult (CUdeviceptr)")
  def cuIpcCloseMemHandle(dptr: DevicePtr): CUDAStatus = unsupported
  @CSignature("CUresult (CUipcEventHandle *, CUevent)")
  def cuIpcGetEventHandle(pHandle: Pointer[CUipcEventHandle, ReadWrite, Opaque], event: CUevent): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUipcMemHandle *, CUdeviceptr)")
  def cuIpcGetMemHandle(pHandle: Pointer[CUipcMemHandle, ReadWrite, Opaque], dptr: DevicePtr): CUDAStatus = unsupported
  @CSignature("CUresult (CUevent *, CUipcEventHandle)")
  def cuIpcOpenEventHandle(phEvent: Pointer[CUevent, ReadWrite, Opaque], handle: CUipcEventHandle): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUdeviceptr *, CUipcMemHandle, unsigned int)")
  def cuIpcOpenMemHandle_v2(
      pdptr: Pointer[DevicePtr, ReadWrite, Opaque],
      handle: CUipcMemHandle,
      Flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (int *, CUfunction_attribute, CUkernel, CUdevice)")
  def cuKernelGetAttribute(
      pi: Pointer[Int, ReadWrite, Opaque],
      attrib: CUfunction_attribute,
      kernel: CUkernel,
      dev: Device
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction *, CUkernel)")
  def cuKernelGetFunction(pFunc: Pointer[CUDAFunction, ReadWrite, Opaque], kernel: CUkernel): CUDAStatus = unsupported
  @CSignature("CUresult (CUlibrary *, CUkernel)")
  def cuKernelGetLibrary(pLib: Pointer[CUlibrary, ReadWrite, Opaque], kernel: CUkernel): CUDAStatus = unsupported
  @CSignature("CUresult (const char **, CUkernel)")
  def cuKernelGetName(name: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque], hfunc: CUkernel): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUkernel, size_t *)")
  def cuKernelGetParamCount(kernel: CUkernel, paramCount: Pointer[SizeT, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUkernel, size_t, size_t *, size_t *)")
  def cuKernelGetParamInfo(
      kernel: CUkernel,
      paramIndex: SizeT,
      paramOffset: Pointer[SizeT, ReadWrite, Opaque],
      paramSize: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction_attribute, int, CUkernel, CUdevice)")
  def cuKernelSetAttribute(attrib: CUfunction_attribute, `val`: Int, kernel: CUkernel, dev: Device): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUkernel, CUfunc_cache, CUdevice)")
  def cuKernelSetCacheConfig(kernel: CUkernel, config: CUfunc_cache, dev: Device): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction)")
  def cuLaunch(f: CUDAFunction): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUfunction, unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, CUstream, void **)"
  )
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
  @CSignature("CUresult (CUDA_LAUNCH_PARAMS *, unsigned int, unsigned int)")
  def cuLaunchCooperativeKernelMultiDevice(
      launchParamsList: Pointer[CUDA_LAUNCH_PARAMS, ReadWrite, Opaque],
      numDevices: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction, int, int)")
  def cuLaunchGrid(f: CUDAFunction, grid_width: Int, grid_height: Int): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction, int, int, CUstream)")
  def cuLaunchGridAsync(f: CUDAFunction, grid_width: Int, grid_height: Int, hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUhostFn, void *)")
  def cuLaunchHostFunc(hStream: CUstream, fn: CUhostFn, userData: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUstream, CUhostFn, void *, unsigned int)")
  def cuLaunchHostFunc_v2(
      hStream: CUstream,
      fn: CUhostFn,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      syncMode: UInt32
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUfunction, unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, CUstream, void **, void **)"
  )
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
      extra: Nullable[OpaquePointer]
  ): CUDAStatus
  @CSignature("CUresult (const CUlaunchConfig *, CUfunction, void **, void **)")
  def cuLaunchKernelEx(
      config: Pointer[CUlaunchConfig, Read, Opaque],
      f: CUDAFunction,
      kernelParams: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      extra: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUkernel *, unsigned int, CUlibrary)")
  def cuLibraryEnumerateKernels(
      kernels: Pointer[CUkernel, ReadWrite, Opaque],
      numKernels: UInt32,
      lib: CUlibrary
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr *, size_t *, CUlibrary, const char *)")
  def cuLibraryGetGlobal(
      dptr: Pointer[DevicePtr, ReadWrite, Opaque],
      bytes: Pointer[SizeT, ReadWrite, Opaque],
      library: CUlibrary,
      name: CString
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUkernel *, CUlibrary, const char *)")
  def cuLibraryGetKernel(pKernel: Pointer[CUkernel, ReadWrite, Opaque], library: CUlibrary, name: CString): CUDAStatus =
    unsupported
  @CSignature("CUresult (unsigned int *, CUlibrary)")
  def cuLibraryGetKernelCount(count: Pointer[UInt32, ReadWrite, Opaque], lib: CUlibrary): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr *, size_t *, CUlibrary, const char *)")
  def cuLibraryGetManaged(
      dptr: Pointer[DevicePtr, ReadWrite, Opaque],
      bytes: Pointer[SizeT, ReadWrite, Opaque],
      library: CUlibrary,
      name: CString
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmodule *, CUlibrary)")
  def cuLibraryGetModule(pMod: Pointer[CUDAModule, ReadWrite, Opaque], library: CUlibrary): CUDAStatus = unsupported
  @CSignature("CUresult (void **, CUlibrary, const char *)")
  def cuLibraryGetUnifiedFunction(
      fptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      library: CUlibrary,
      symbol: CString
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUlibrary *, const void *, CUjit_option *, void **, unsigned int, CUlibraryOption *, void **, unsigned int)"
  )
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
  @CSignature(
    "CUresult (CUlibrary *, const char *, CUjit_option *, void **, unsigned int, CUlibraryOption *, void **, unsigned int)"
  )
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
  @CSignature("CUresult (CUlibrary)")
  def cuLibraryUnload(library: CUlibrary): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUlinkState, CUjitInputType, void *, size_t, const char *, unsigned int, CUjit_option *, void **)"
  )
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
  @CSignature("CUresult (CUlinkState, CUjitInputType, const char *, unsigned int, CUjit_option *, void **)")
  def cuLinkAddFile_v2(
      state: CUlinkState,
      `type`: CUjitInputType,
      path: CString,
      numOptions: UInt32,
      options: Pointer[CUjit_option, ReadWrite, Opaque],
      optionValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUlinkState, void **, size_t *)")
  def cuLinkComplete(
      state: CUlinkState,
      cubinOut: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      sizeOut: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (unsigned int, CUjit_option *, void **, CUlinkState *)")
  def cuLinkCreate_v2(
      numOptions: UInt32,
      options: Pointer[CUjit_option, ReadWrite, Opaque],
      optionValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      stateOut: Pointer[CUlinkState, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUlinkState)")
  def cuLinkDestroy(state: CUlinkState): CUDAStatus = unsupported
  @CSignature("CUresult (CUlogicalEndpointId, CUdevice)")
  def cuLogicalEndpointAddDevice(leId: CUlogicalEndpointId, dev: Device): CUDAStatus = unsupported
  @CSignature("CUresult (CUlogicalEndpointId, CUdevice, cuuint64_t, void *, cuuint64_t, unsigned long long)")
  def cuLogicalEndpointBindAddr(
      leId: CUlogicalEndpointId,
      dev: Device,
      offset: cuuint64_t,
      ptr: Pointer[CVoid, ReadWrite, Opaque],
      size: cuuint64_t,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUlogicalEndpointId, CUdevice, cuuint64_t, CUmemGenericAllocationHandle, cuuint64_t, cuuint64_t, unsigned long long)"
  )
  def cuLogicalEndpointBindMem(
      leId: CUlogicalEndpointId,
      dev: Device,
      offset: cuuint64_t,
      memHandle: CUmemGenericAllocationHandle,
      memOffset: cuuint64_t,
      size: cuuint64_t,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUlogicalEndpointId, const CUlogicalEndpointProp *)")
  def cuLogicalEndpointCreate(
      leId: CUlogicalEndpointId,
      prop: Pointer[CUlogicalEndpointProp, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUlogicalEndpointId)")
  def cuLogicalEndpointDestroy(leId: CUlogicalEndpointId): CUDAStatus = unsupported
  @CSignature("CUresult (void *, CUlogicalEndpointId, CUlogicalEndpointIpcHandleType)")
  def cuLogicalEndpointExport(
      handle: Pointer[CVoid, ReadWrite, Opaque],
      leId: CUlogicalEndpointId,
      handleType: CUlogicalEndpointIpcHandleType
  ): CUDAStatus = unsupported
  @CSignature("CUresult (cuuint64_t *, cuuint64_t *, const CUlogicalEndpointProp *)")
  def cuLogicalEndpointGetLimits(
      bindAlignment: Pointer[cuuint64_t, ReadWrite, Opaque],
      maxSize: Pointer[cuuint64_t, ReadWrite, Opaque],
      prop: Pointer[CUlogicalEndpointProp, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUlogicalEndpointId, cuuint32_t)")
  def cuLogicalEndpointIdRelease(baseLeId: CUlogicalEndpointId, count: cuuint32_t): CUDAStatus = unsupported
  @CSignature("CUresult (CUlogicalEndpointId *, cuuint32_t)")
  def cuLogicalEndpointIdReserve(
      baseLeId: Pointer[CUlogicalEndpointId, ReadWrite, Opaque],
      count: cuuint32_t
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUlogicalEndpointId, const void *, CUlogicalEndpointIpcHandleType)")
  def cuLogicalEndpointImport(
      leId: CUlogicalEndpointId,
      handle: Pointer[CVoid, Read, Opaque],
      handleType: CUlogicalEndpointIpcHandleType
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUlogicalEndpointId, cuuint32_t, int *)")
  def cuLogicalEndpointQuery(
      leId: CUlogicalEndpointId,
      count: cuuint32_t,
      queryStatus: Pointer[Int, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUlogicalEndpointId, CUdevice, cuuint64_t, cuuint64_t)")
  def cuLogicalEndpointUnbind(
      leId: CUlogicalEndpointId,
      dev: Device,
      offset: cuuint64_t,
      size: cuuint64_t
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUlogIterator *, unsigned int)")
  def cuLogsCurrent(iterator_out: Pointer[CUlogIterator, ReadWrite, Opaque], flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUlogIterator *, const char *, unsigned int)")
  def cuLogsDumpToFile(
      iterator: Pointer[CUlogIterator, ReadWrite, Opaque],
      pathToFile: CString,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUlogIterator *, char *, size_t *, unsigned int)")
  def cuLogsDumpToMemory(
      iterator: Pointer[CUlogIterator, ReadWrite, Opaque],
      buffer: Pointer[Byte, ReadWrite, Opaque],
      size: Pointer[SizeT, ReadWrite, Opaque],
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUlogsCallback, void *, CUlogsCallbackHandle *)")
  def cuLogsRegisterCallback(
      callbackFunc: CUlogsCallback,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      callback_out: Pointer[CUlogsCallbackHandle, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUlogsCallbackHandle)")
  def cuLogsUnregisterCallback(callback: CUlogsCallbackHandle): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, size_t)")
  def cuMemAddressFree(ptr: DevicePtr, size: SizeT): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr *, size_t, size_t, CUdeviceptr, unsigned long long)")
  def cuMemAddressReserve(
      ptr: Pointer[DevicePtr, ReadWrite, Opaque],
      size: SizeT,
      alignment: SizeT,
      addr: DevicePtr,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, size_t, CUmem_advise, CUmemLocation)")
  def cuMemAdvise_v2(devPtr: DevicePtr, count: SizeT, advice: CUmem_advise, location: CUmemLocation): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUdeviceptr *, size_t, CUstream)")
  def cuMemAllocAsync(dptr: Pointer[DevicePtr, ReadWrite, Opaque], bytesize: SizeT, hStream: CUstream): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUdeviceptr *, size_t, CUmemoryPool, CUstream)")
  def cuMemAllocFromPoolAsync(
      dptr: Pointer[DevicePtr, ReadWrite, Opaque],
      bytesize: SizeT,
      pool: CUmemoryPool,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (void **, size_t)")
  def cuMemAllocHost_v2(
      pp: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      bytesize: SizeT
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr *, size_t, unsigned int)")
  def cuMemAllocManaged(dptr: Pointer[DevicePtr, ReadWrite, Opaque], bytesize: SizeT, flags: UInt32): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUdeviceptr *, size_t *, size_t, size_t, unsigned int)")
  def cuMemAllocPitch_v2(
      dptr: Pointer[DevicePtr, ReadWrite, Opaque],
      pPitch: Pointer[SizeT, ReadWrite, Opaque],
      WidthInBytes: SizeT,
      Height: SizeT,
      ElementSizeBytes: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr *, size_t)")
  def cuMemAlloc_v2(dptr: Out[DevicePtr], bytesize: SizeT): CUDAStatus
  @CSignature("CUresult (CUmemDecompressParams *, size_t, unsigned int, size_t *, CUstream)")
  def cuMemBatchDecompressAsync(
      paramsArray: Pointer[CUmemDecompressParams, ReadWrite, Opaque],
      count: SizeT,
      flags: UInt32,
      errorIndex: Pointer[SizeT, ReadWrite, Opaque],
      stream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemGenericAllocationHandle *, size_t, const CUmemAllocationProp *, unsigned long long)")
  def cuMemCreate(
      handle: Pointer[CUmemGenericAllocationHandle, ReadWrite, Opaque],
      size: SizeT,
      prop: Pointer[CUmemAllocationProp, Read, Opaque],
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUdeviceptr *, size_t *, size_t, CUmemLocation *, size_t *, size_t, unsigned long long, CUstream)"
  )
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
  @CSignature("CUresult (CUdeviceptr *, size_t *, size_t, unsigned long long, CUstream)")
  def cuMemDiscardBatchAsync(
      dptrs: Pointer[DevicePtr, ReadWrite, Opaque],
      sizes: Pointer[SizeT, ReadWrite, Opaque],
      count: SizeT,
      flags: unsigned_long_long,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (void *, CUmemGenericAllocationHandle, CUmemAllocationHandleType, unsigned long long)")
  def cuMemExportToShareableHandle(
      shareableHandle: Pointer[CVoid, ReadWrite, Opaque],
      handle: CUmemGenericAllocationHandle,
      handleType: CUmemAllocationHandleType,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, CUstream)")
  def cuMemFreeAsync(dptr: DevicePtr, hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (void *)")
  def cuMemFreeHost(p: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr)")
  def cuMemFree_v2(dptr: DevicePtr): CUDAStatus
  @CSignature("CUresult (unsigned long long *, const CUmemLocation *, CUdeviceptr)")
  def cuMemGetAccess(
      flags: Pointer[unsigned_long_long, ReadWrite, Opaque],
      location: Pointer[CUmemLocation, Read, Opaque],
      ptr: DevicePtr
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr *, size_t *, CUdeviceptr)")
  def cuMemGetAddressRange_v2(
      pbase: Pointer[DevicePtr, ReadWrite, Opaque],
      psize: Pointer[SizeT, ReadWrite, Opaque],
      dptr: DevicePtr
  ): CUDAStatus = unsupported
  @CSignature("CUresult (size_t *, const CUmemAllocationProp *, CUmemAllocationGranularity_flags)")
  def cuMemGetAllocationGranularity(
      granularity: Pointer[SizeT, ReadWrite, Opaque],
      prop: Pointer[CUmemAllocationProp, Read, Opaque],
      option: CUmemAllocationGranularity_flags
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemAllocationProp *, CUmemGenericAllocationHandle)")
  def cuMemGetAllocationPropertiesFromHandle(
      prop: Pointer[CUmemAllocationProp, ReadWrite, Opaque],
      handle: CUmemGenericAllocationHandle
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemoryPool *, CUmemLocation *, CUmemAllocationType)")
  def cuMemGetDefaultMemPool(
      pool_out: Pointer[CUmemoryPool, ReadWrite, Opaque],
      location: Pointer[CUmemLocation, ReadWrite, Opaque],
      `type`: CUmemAllocationType
  ): CUDAStatus = unsupported
  @CSignature("CUresult (void *, CUdeviceptr, size_t, CUmemRangeHandleType, unsigned long long)")
  def cuMemGetHandleForAddressRange(
      handle: Pointer[CVoid, ReadWrite, Opaque],
      dptr: DevicePtr,
      size: SizeT,
      handleType: CUmemRangeHandleType,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature("CUresult (size_t *, size_t *)")
  def cuMemGetInfo_v2(free: Pointer[SizeT, ReadWrite, Opaque], total: Pointer[SizeT, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUmemoryPool *, CUmemLocation *, CUmemAllocationType)")
  def cuMemGetMemPool(
      pool: Pointer[CUmemoryPool, ReadWrite, Opaque],
      location: Pointer[CUmemLocation, ReadWrite, Opaque],
      `type`: CUmemAllocationType
  ): CUDAStatus = unsupported
  @CSignature("CUresult (void **, size_t, unsigned int)")
  def cuMemHostAlloc(
      pp: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      bytesize: SizeT,
      Flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr *, void *, unsigned int)")
  def cuMemHostGetDevicePointer_v2(
      pdptr: Pointer[DevicePtr, ReadWrite, Opaque],
      p: Pointer[CVoid, ReadWrite, Opaque],
      Flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (unsigned int *, void *)")
  def cuMemHostGetFlags(pFlags: Pointer[UInt32, ReadWrite, Opaque], p: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (void *, size_t, unsigned int)")
  def cuMemHostRegister_v2(p: Pointer[CVoid, ReadWrite, Opaque], bytesize: SizeT, Flags: UInt32): CUDAStatus =
    unsupported
  @CSignature("CUresult (void *)")
  def cuMemHostUnregister(p: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemGenericAllocationHandle *, void *, CUmemAllocationHandleType)")
  def cuMemImportFromShareableHandle(
      handle: Pointer[CUmemGenericAllocationHandle, ReadWrite, Opaque],
      osHandle: Pointer[CVoid, ReadWrite, Opaque],
      shHandleType: CUmemAllocationHandleType
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, size_t, size_t, CUmemGenericAllocationHandle, unsigned long long)")
  def cuMemMap(
      ptr: DevicePtr,
      size: SizeT,
      offset: SizeT,
      handle: CUmemGenericAllocationHandle,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUarrayMapInfo *, unsigned int, CUstream)")
  def cuMemMapArrayAsync(
      mapInfoList: Pointer[CUarrayMapInfo, ReadWrite, Opaque],
      count: UInt32,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemoryPool *, const CUmemPoolProps *)")
  def cuMemPoolCreate(
      pool: Pointer[CUmemoryPool, ReadWrite, Opaque],
      poolProps: Pointer[CUmemPoolProps, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemoryPool)")
  def cuMemPoolDestroy(pool: CUmemoryPool): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemPoolPtrExportData *, CUdeviceptr)")
  def cuMemPoolExportPointer(
      shareData_out: Pointer[CUmemPoolPtrExportData, ReadWrite, Opaque],
      ptr: DevicePtr
  ): CUDAStatus = unsupported
  @CSignature("CUresult (void *, CUmemoryPool, CUmemAllocationHandleType, unsigned long long)")
  def cuMemPoolExportToShareableHandle(
      handle_out: Pointer[CVoid, ReadWrite, Opaque],
      pool: CUmemoryPool,
      handleType: CUmemAllocationHandleType,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemAccess_flags *, CUmemoryPool, CUmemLocation *)")
  def cuMemPoolGetAccess(
      flags: Pointer[CUmemAccess_flags, ReadWrite, Opaque],
      memPool: CUmemoryPool,
      location: Pointer[CUmemLocation, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemoryPool, CUmemPool_attribute, void *)")
  def cuMemPoolGetAttribute(
      pool: CUmemoryPool,
      attr: CUmemPool_attribute,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemoryPool *, void *, CUmemAllocationHandleType, unsigned long long)")
  def cuMemPoolImportFromShareableHandle(
      pool_out: Pointer[CUmemoryPool, ReadWrite, Opaque],
      handle: Pointer[CVoid, ReadWrite, Opaque],
      handleType: CUmemAllocationHandleType,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr *, CUmemoryPool, CUmemPoolPtrExportData *)")
  def cuMemPoolImportPointer(
      ptr_out: Pointer[DevicePtr, ReadWrite, Opaque],
      pool: CUmemoryPool,
      shareData: Pointer[CUmemPoolPtrExportData, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemoryPool, const CUmemAccessDesc *, size_t)")
  def cuMemPoolSetAccess(pool: CUmemoryPool, map: Pointer[CUmemAccessDesc, Read, Opaque], count: SizeT): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUmemoryPool, CUmemPool_attribute, void *)")
  def cuMemPoolSetAttribute(
      pool: CUmemoryPool,
      attr: CUmemPool_attribute,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemoryPool, size_t)")
  def cuMemPoolTrimTo(pool: CUmemoryPool, minBytesToKeep: SizeT): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, size_t, CUmemLocation, unsigned int, CUstream)")
  def cuMemPrefetchAsync_v2(
      devPtr: DevicePtr,
      count: SizeT,
      location: CUmemLocation,
      flags: UInt32,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUdeviceptr *, size_t *, size_t, CUmemLocation *, size_t *, size_t, unsigned long long, CUstream)"
  )
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
  @CSignature("CUresult (void *, size_t, CUmem_range_attribute, CUdeviceptr, size_t)")
  def cuMemRangeGetAttribute(
      data: Pointer[CVoid, ReadWrite, Opaque],
      dataSize: SizeT,
      attribute: CUmem_range_attribute,
      devPtr: DevicePtr,
      count: SizeT
  ): CUDAStatus = unsupported
  @CSignature("CUresult (void **, size_t *, CUmem_range_attribute *, size_t, CUdeviceptr, size_t)")
  def cuMemRangeGetAttributes(
      data: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      dataSizes: Pointer[SizeT, ReadWrite, Opaque],
      attributes: Pointer[CUmem_range_attribute, ReadWrite, Opaque],
      numAttributes: SizeT,
      devPtr: DevicePtr,
      count: SizeT
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemGenericAllocationHandle)")
  def cuMemRelease(handle: CUmemGenericAllocationHandle): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemGenericAllocationHandle *, void *)")
  def cuMemRetainAllocationHandle(
      handle: Pointer[CUmemGenericAllocationHandle, ReadWrite, Opaque],
      addr: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, size_t, const CUmemAccessDesc *, size_t)")
  def cuMemSetAccess(
      ptr: DevicePtr,
      size: SizeT,
      desc: Pointer[CUmemAccessDesc, Read, Opaque],
      count: SizeT
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemLocation *, CUmemAllocationType, CUmemoryPool)")
  def cuMemSetMemPool(
      location: Pointer[CUmemLocation, ReadWrite, Opaque],
      `type`: CUmemAllocationType,
      pool: CUmemoryPool
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, size_t)")
  def cuMemUnmap(ptr: DevicePtr, size: SizeT): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, CUdeviceptr, size_t)")
  def cuMemcpy(dst: DevicePtr, src: DevicePtr, ByteCount: SizeT): CUDAStatus = unsupported
  @CSignature("CUresult (const CUDA_MEMCPY2D *, CUstream)")
  def cuMemcpy2DAsync_v2(pCopy: Pointer[CUDA_MEMCPY2D, Read, Opaque], hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (const CUDA_MEMCPY2D *)")
  def cuMemcpy2DUnaligned_v2(pCopy: Pointer[CUDA_MEMCPY2D, Read, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (const CUDA_MEMCPY2D *)")
  def cuMemcpy2D_v2(pCopy: Pointer[CUDA_MEMCPY2D, Read, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (const CUDA_MEMCPY3D *, CUstream)")
  def cuMemcpy3DAsync_v2(pCopy: Pointer[CUDA_MEMCPY3D, Read, Opaque], hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (size_t, CUDA_MEMCPY3D_BATCH_OP *, unsigned long long, CUstream)")
  def cuMemcpy3DBatchAsync_v2(
      numOps: SizeT,
      opList: Pointer[CUDA_MEMCPY3D_BATCH_OP, ReadWrite, Opaque],
      flags: unsigned_long_long,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (const CUDA_MEMCPY3D_PEER *)")
  def cuMemcpy3DPeer(pCopy: Pointer[CUDA_MEMCPY3D_PEER, Read, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (const CUDA_MEMCPY3D_PEER *, CUstream)")
  def cuMemcpy3DPeerAsync(pCopy: Pointer[CUDA_MEMCPY3D_PEER, Read, Opaque], hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (CUDA_MEMCPY3D_BATCH_OP *, unsigned long long, CUstream)")
  def cuMemcpy3DWithAttributesAsync(
      op: Pointer[CUDA_MEMCPY3D_BATCH_OP, ReadWrite, Opaque],
      flags: unsigned_long_long,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (const CUDA_MEMCPY3D *)")
  def cuMemcpy3D_v2(pCopy: Pointer[CUDA_MEMCPY3D, Read, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, CUdeviceptr, size_t, CUstream)")
  def cuMemcpyAsync(dst: DevicePtr, src: DevicePtr, ByteCount: SizeT, hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (CUarray, size_t, CUarray, size_t, size_t)")
  def cuMemcpyAtoA_v2(
      dstArray: CUarray,
      dstOffset: SizeT,
      srcArray: CUarray,
      srcOffset: SizeT,
      ByteCount: SizeT
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, CUarray, size_t, size_t)")
  def cuMemcpyAtoD_v2(dstDevice: DevicePtr, srcArray: CUarray, srcOffset: SizeT, ByteCount: SizeT): CUDAStatus =
    unsupported
  @CSignature("CUresult (void *, CUarray, size_t, size_t, CUstream)")
  def cuMemcpyAtoHAsync_v2(
      dstHost: Pointer[CVoid, ReadWrite, Opaque],
      srcArray: CUarray,
      srcOffset: SizeT,
      ByteCount: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (void *, CUarray, size_t, size_t)")
  def cuMemcpyAtoH_v2(
      dstHost: Pointer[CVoid, ReadWrite, Opaque],
      srcArray: CUarray,
      srcOffset: SizeT,
      ByteCount: SizeT
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUdeviceptr *, CUdeviceptr *, size_t *, size_t, CUmemcpyAttributes *, size_t *, size_t, CUstream)"
  )
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
  @CSignature("CUresult (CUarray, size_t, CUdeviceptr, size_t)")
  def cuMemcpyDtoA_v2(dstArray: CUarray, dstOffset: SizeT, srcDevice: DevicePtr, ByteCount: SizeT): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUdeviceptr, CUdeviceptr, size_t, CUstream)")
  def cuMemcpyDtoDAsync_v2(
      dstDevice: DevicePtr,
      srcDevice: DevicePtr,
      ByteCount: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, CUdeviceptr, size_t)")
  def cuMemcpyDtoD_v2(dstDevice: DevicePtr, srcDevice: DevicePtr, ByteCount: SizeT): CUDAStatus
  @CSignature("CUresult (void *, CUdeviceptr, size_t, CUstream)")
  def cuMemcpyDtoHAsync_v2(
      dstHost: Pointer[CVoid, ReadWrite, Opaque],
      srcDevice: DevicePtr,
      ByteCount: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (void *, CUdeviceptr, size_t)")
  def cuMemcpyDtoH_v2(dstHost: OutputBuffer[Byte, "ByteCount"], srcDevice: DevicePtr, ByteCount: SizeT): CUDAStatus
  @CSignature("CUresult (CUarray, size_t, const void *, size_t, CUstream)")
  def cuMemcpyHtoAAsync_v2(
      dstArray: CUarray,
      dstOffset: SizeT,
      srcHost: Pointer[CVoid, Read, Opaque],
      ByteCount: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUarray, size_t, const void *, size_t)")
  def cuMemcpyHtoA_v2(
      dstArray: CUarray,
      dstOffset: SizeT,
      srcHost: Pointer[CVoid, Read, Opaque],
      ByteCount: SizeT
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, const void *, size_t, CUstream)")
  def cuMemcpyHtoDAsync_v2(
      dstDevice: DevicePtr,
      srcHost: Pointer[CVoid, Read, Opaque],
      ByteCount: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, const void *, size_t)")
  def cuMemcpyHtoD_v2(dstDevice: DevicePtr, srcHost: InputBuffer[Byte, "ByteCount"], ByteCount: SizeT): CUDAStatus
  @CSignature("CUresult (CUdeviceptr, CUcontext, CUdeviceptr, CUcontext, size_t)")
  def cuMemcpyPeer(
      dstDevice: DevicePtr,
      dstContext: Context,
      srcDevice: DevicePtr,
      srcContext: Context,
      ByteCount: SizeT
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, CUcontext, CUdeviceptr, CUcontext, size_t, CUstream)")
  def cuMemcpyPeerAsync(
      dstDevice: DevicePtr,
      dstContext: Context,
      srcDevice: DevicePtr,
      srcContext: Context,
      ByteCount: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, CUdeviceptr, size_t, CUmemcpyAttributes *, CUstream)")
  def cuMemcpyWithAttributesAsync(
      dst: DevicePtr,
      src: DevicePtr,
      size: SizeT,
      attr: Pointer[CUmemcpyAttributes, ReadWrite, Opaque],
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, unsigned short, size_t, CUstream)")
  def cuMemsetD16Async(dstDevice: DevicePtr, us: unsigned_short, N: SizeT, hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, unsigned short, size_t)")
  def cuMemsetD16_v2(dstDevice: DevicePtr, us: unsigned_short, N: SizeT): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, size_t, unsigned short, size_t, size_t, CUstream)")
  def cuMemsetD2D16Async(
      dstDevice: DevicePtr,
      dstPitch: SizeT,
      us: unsigned_short,
      Width: SizeT,
      Height: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, size_t, unsigned short, size_t, size_t)")
  def cuMemsetD2D16_v2(
      dstDevice: DevicePtr,
      dstPitch: SizeT,
      us: unsigned_short,
      Width: SizeT,
      Height: SizeT
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, size_t, unsigned int, size_t, size_t, CUstream)")
  def cuMemsetD2D32Async(
      dstDevice: DevicePtr,
      dstPitch: SizeT,
      ui: UInt32,
      Width: SizeT,
      Height: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, size_t, unsigned int, size_t, size_t)")
  def cuMemsetD2D32_v2(dstDevice: DevicePtr, dstPitch: SizeT, ui: UInt32, Width: SizeT, Height: SizeT): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUdeviceptr, size_t, unsigned char, size_t, size_t, CUstream)")
  def cuMemsetD2D8Async(
      dstDevice: DevicePtr,
      dstPitch: SizeT,
      uc: UInt8,
      Width: SizeT,
      Height: SizeT,
      hStream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, size_t, unsigned char, size_t, size_t)")
  def cuMemsetD2D8_v2(dstDevice: DevicePtr, dstPitch: SizeT, uc: UInt8, Width: SizeT, Height: SizeT): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUdeviceptr, unsigned int, size_t, CUstream)")
  def cuMemsetD32Async(dstDevice: DevicePtr, ui: UInt32, N: SizeT, hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, unsigned int, size_t)")
  def cuMemsetD32_v2(dstDevice: DevicePtr, ui: UInt32, N: SizeT): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, unsigned char, size_t, CUstream)")
  def cuMemsetD8Async(dstDevice: DevicePtr, uc: UInt8, N: SizeT, hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr, unsigned char, size_t)")
  def cuMemsetD8_v2(dstDevice: DevicePtr, uc: UInt8, N: SizeT): CUDAStatus
  @CSignature("CUresult (CUmipmappedArray *, const CUDA_ARRAY3D_DESCRIPTOR *, unsigned int)")
  def cuMipmappedArrayCreate(
      pHandle: Pointer[CUmipmappedArray, ReadWrite, Opaque],
      pMipmappedArrayDesc: Pointer[CUDA_ARRAY3D_DESCRIPTOR, Read, Opaque],
      numMipmapLevels: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmipmappedArray)")
  def cuMipmappedArrayDestroy(hMipmappedArray: CUmipmappedArray): CUDAStatus = unsupported
  @CSignature("CUresult (CUarray *, CUmipmappedArray, unsigned int)")
  def cuMipmappedArrayGetLevel(
      pLevelArray: Pointer[CUarray, ReadWrite, Opaque],
      hMipmappedArray: CUmipmappedArray,
      level: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUDA_ARRAY_MEMORY_REQUIREMENTS *, CUmipmappedArray, CUdevice)")
  def cuMipmappedArrayGetMemoryRequirements(
      memoryRequirements: Pointer[CUDA_ARRAY_MEMORY_REQUIREMENTS, ReadWrite, Opaque],
      mipmap: CUmipmappedArray,
      device: Device
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUDA_ARRAY_SPARSE_PROPERTIES *, CUmipmappedArray)")
  def cuMipmappedArrayGetSparseProperties(
      sparseProperties: Pointer[CUDA_ARRAY_SPARSE_PROPERTIES, ReadWrite, Opaque],
      mipmap: CUmipmappedArray
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction *, unsigned int, CUmodule)")
  def cuModuleEnumerateFunctions(
      functions: Pointer[CUDAFunction, ReadWrite, Opaque],
      numFunctions: UInt32,
      mod: CUDAModule
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction *, CUmodule, const char *)")
  def cuModuleGetFunction(hfunc: Out[KernelFunction], hmod: CUDAModule, name: FunctionName): CUDAStatus
  @CSignature("CUresult (unsigned int *, CUmodule)")
  def cuModuleGetFunctionCount(count: Pointer[UInt32, ReadWrite, Opaque], mod: CUDAModule): CUDAStatus = unsupported
  @CSignature("CUresult (CUdeviceptr *, size_t *, CUmodule, const char *)")
  def cuModuleGetGlobal_v2(
      dptr: Pointer[DevicePtr, ReadWrite, Opaque],
      bytes: Pointer[SizeT, ReadWrite, Opaque],
      hmod: CUDAModule,
      name: CString
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmoduleLoadingMode *)")
  def cuModuleGetLoadingMode(mode: Pointer[CUmoduleLoadingMode, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUsurfref *, CUmodule, const char *)")
  def cuModuleGetSurfRef(pSurfRef: Pointer[CUsurfref, ReadWrite, Opaque], hmod: CUDAModule, name: CString): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUtexref *, CUmodule, const char *)")
  def cuModuleGetTexRef(pTexRef: Pointer[CUtexref, ReadWrite, Opaque], hmod: CUDAModule, name: CString): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUmodule *, const char *)")
  def cuModuleLoad(module: Pointer[CUDAModule, ReadWrite, Opaque], fname: CString): CUDAStatus = unsupported
  @CSignature("CUresult (CUmodule *, const void *)")
  def cuModuleLoadData(module: Out[CUDAModule], image: ModuleImage): CUDAStatus
  @CSignature("CUresult (CUmodule *, const void *, unsigned int, CUjit_option *, void **)")
  def cuModuleLoadDataEx(
      module: Pointer[CUDAModule, ReadWrite, Opaque],
      image: Pointer[CVoid, Read, Opaque],
      numOptions: UInt32,
      options: Pointer[CUjit_option, ReadWrite, Opaque],
      optionValues: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmodule *, const void *)")
  def cuModuleLoadFatBinary(
      module: Pointer[CUDAModule, ReadWrite, Opaque],
      fatCubin: Pointer[CVoid, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmodule)")
  def cuModuleUnload(hmod: CUDAModule): CUDAStatus
  @CSignature("CUresult (CUmemGenericAllocationHandle, CUdevice)")
  def cuMulticastAddDevice(mcHandle: CUmemGenericAllocationHandle, dev: Device): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemGenericAllocationHandle, size_t, CUdeviceptr, size_t, unsigned long long)")
  def cuMulticastBindAddr(
      mcHandle: CUmemGenericAllocationHandle,
      mcOffset: SizeT,
      memptr: DevicePtr,
      size: SizeT,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemGenericAllocationHandle, CUdevice, size_t, CUdeviceptr, size_t, unsigned long long)")
  def cuMulticastBindAddr_v2(
      mcHandle: CUmemGenericAllocationHandle,
      dev: Device,
      mcOffset: SizeT,
      memptr: DevicePtr,
      size: SizeT,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUmemGenericAllocationHandle, size_t, CUmemGenericAllocationHandle, size_t, size_t, unsigned long long)"
  )
  def cuMulticastBindMem(
      mcHandle: CUmemGenericAllocationHandle,
      mcOffset: SizeT,
      memHandle: CUmemGenericAllocationHandle,
      memOffset: SizeT,
      size: SizeT,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUmemGenericAllocationHandle, CUdevice, size_t, CUmemGenericAllocationHandle, size_t, size_t, unsigned long long)"
  )
  def cuMulticastBindMem_v2(
      mcHandle: CUmemGenericAllocationHandle,
      dev: Device,
      mcOffset: SizeT,
      memHandle: CUmemGenericAllocationHandle,
      memOffset: SizeT,
      size: SizeT,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemGenericAllocationHandle *, const CUmulticastObjectProp *)")
  def cuMulticastCreate(
      mcHandle: Pointer[CUmemGenericAllocationHandle, ReadWrite, Opaque],
      prop: Pointer[CUmulticastObjectProp, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (size_t *, const CUmulticastObjectProp *, CUmulticastGranularity_flags)")
  def cuMulticastGetGranularity(
      granularity: Pointer[SizeT, ReadWrite, Opaque],
      prop: Pointer[CUmulticastObjectProp, Read, Opaque],
      option: CUmulticastGranularity_flags
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmemGenericAllocationHandle, CUdevice, size_t, size_t)")
  def cuMulticastUnbind(mcHandle: CUmemGenericAllocationHandle, dev: Device, mcOffset: SizeT, size: SizeT): CUDAStatus =
    unsupported
  @CSignature("CUresult (size_t *, CUfunction, int, int)")
  def cuOccupancyAvailableDynamicSMemPerBlock(
      dynamicSmemSize: Pointer[SizeT, ReadWrite, Opaque],
      func: CUDAFunction,
      numBlocks: Int,
      blockSize: Int
  ): CUDAStatus = unsupported
  @CSignature("CUresult (int *, CUfunction, int, size_t)")
  def cuOccupancyMaxActiveBlocksPerMultiprocessor(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      func: CUDAFunction,
      blockSize: Int,
      dynamicSMemSize: SizeT
  ): CUDAStatus = unsupported
  @CSignature("CUresult (int *, CUfunction, int, size_t, unsigned int)")
  def cuOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      func: CUDAFunction,
      blockSize: Int,
      dynamicSMemSize: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (int *, CUfunction, const CUlaunchConfig *)")
  def cuOccupancyMaxActiveClusters(
      numClusters: Pointer[Int, ReadWrite, Opaque],
      func: CUDAFunction,
      config: Pointer[CUlaunchConfig, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (int *, int *, CUfunction, CUoccupancyB2DSize, size_t, int)")
  def cuOccupancyMaxPotentialBlockSize(
      minGridSize: Pointer[Int, ReadWrite, Opaque],
      blockSize: Pointer[Int, ReadWrite, Opaque],
      func: CUDAFunction,
      blockSizeToDynamicSMemSize: CUoccupancyB2DSize,
      dynamicSMemSize: SizeT,
      blockSizeLimit: Int
  ): CUDAStatus = unsupported
  @CSignature("CUresult (int *, int *, CUfunction, CUoccupancyB2DSize, size_t, int, unsigned int)")
  def cuOccupancyMaxPotentialBlockSizeWithFlags(
      minGridSize: Pointer[Int, ReadWrite, Opaque],
      blockSize: Pointer[Int, ReadWrite, Opaque],
      func: CUDAFunction,
      blockSizeToDynamicSMemSize: CUoccupancyB2DSize,
      dynamicSMemSize: SizeT,
      blockSizeLimit: Int,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (int *, CUfunction, const CUlaunchConfig *)")
  def cuOccupancyMaxPotentialClusterSize(
      clusterSize: Pointer[Int, ReadWrite, Opaque],
      func: CUDAFunction,
      config: Pointer[CUlaunchConfig, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction, unsigned int)")
  def cuParamSetSize(hfunc: CUDAFunction, numbytes: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction, int, CUtexref)")
  def cuParamSetTexRef(hfunc: CUDAFunction, texunit: Int, hTexRef: CUtexref): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction, int, float)")
  def cuParamSetf(hfunc: CUDAFunction, offset: Int, value: Float): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction, int, unsigned int)")
  def cuParamSeti(hfunc: CUDAFunction, offset: Int, value: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUfunction, int, void *, unsigned int)")
  def cuParamSetv(
      hfunc: CUDAFunction,
      offset: Int,
      ptr: Pointer[CVoid, ReadWrite, Opaque],
      numbytes: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (void *, CUpointer_attribute, CUdeviceptr)")
  def cuPointerGetAttribute(
      data: Pointer[CVoid, ReadWrite, Opaque],
      attribute: CUpointer_attribute,
      ptr: DevicePtr
  ): CUDAStatus = unsupported
  @CSignature("CUresult (unsigned int, CUpointer_attribute *, void **, CUdeviceptr)")
  def cuPointerGetAttributes(
      numAttributes: UInt32,
      attributes: Pointer[CUpointer_attribute, ReadWrite, Opaque],
      data: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      ptr: DevicePtr
  ): CUDAStatus = unsupported
  @CSignature("CUresult (const void *, CUpointer_attribute, CUdeviceptr)")
  def cuPointerSetAttribute(
      value: Pointer[CVoid, Read, Opaque],
      attribute: CUpointer_attribute,
      ptr: DevicePtr
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (const CUexternalSemaphore *, const CUDA_EXTERNAL_SEMAPHORE_SIGNAL_PARAMS *, unsigned int, CUstream)"
  )
  def cuSignalExternalSemaphoresAsync(
      extSemArray: Pointer[CUexternalSemaphore, Read, Opaque],
      paramsArray: Pointer[CUDA_EXTERNAL_SEMAPHORE_SIGNAL_PARAMS, Read, Opaque],
      numExtSems: UInt32,
      stream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUstreamCallback, void *, unsigned int)")
  def cuStreamAddCallback(
      hStream: CUstream,
      callback: CUstreamCallback,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUdeviceptr, size_t, unsigned int)")
  def cuStreamAttachMemAsync(hStream: CUstream, dptr: DevicePtr, length: SizeT, flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, unsigned int, CUstreamBatchMemOpParams *, unsigned int)")
  def cuStreamBatchMemOp_v2(
      stream: CUstream,
      count: UInt32,
      paramArray: Pointer[CUstreamBatchMemOpParams, ReadWrite, Opaque],
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUstreamCigCaptureParams *)")
  def cuStreamBeginCaptureToCig(
      hStream: CUstream,
      streamCigCaptureParams: Pointer[CUstreamCigCaptureParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUgraph, const CUgraphNode *, const CUgraphEdgeData *, size_t, CUstreamCaptureMode)")
  def cuStreamBeginCaptureToGraph(
      hStream: CUstream,
      hGraph: CUgraph,
      dependencies: Pointer[CUgraphNode, Read, Opaque],
      dependencyData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: SizeT,
      mode: CUstreamCaptureMode
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUstreamCaptureMode)")
  def cuStreamBeginCapture_v2(hStream: CUstream, mode: CUstreamCaptureMode): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUstreamCaptureMode, CUgraph, CUgraphRecaptureCallback, void *)")
  def cuStreamBeginRecaptureToGraph(
      hStream: CUstream,
      mode: CUstreamCaptureMode,
      hGraph: CUgraph,
      callbackFunc: CUgraphRecaptureCallback,
      userData: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUstream)")
  def cuStreamCopyAttributes(dst: CUstream, src: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream *, unsigned int)")
  def cuStreamCreate(phStream: Pointer[CUstream, ReadWrite, Opaque], Flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream *, unsigned int, int)")
  def cuStreamCreateWithPriority(
      phStream: Pointer[CUstream, ReadWrite, Opaque],
      flags: UInt32,
      priority: Int
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream)")
  def cuStreamDestroy_v2(hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUgraph *)")
  def cuStreamEndCapture(hStream: CUstream, phGraph: Pointer[CUgraph, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream)")
  def cuStreamEndCaptureToCig(hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUstreamAttrID, CUstreamAttrValue *)")
  def cuStreamGetAttribute(
      hStream: CUstream,
      attr: CUstreamAttrID,
      value_out: Pointer[CUstreamAttrValue, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUstream, CUstreamCaptureStatus *, cuuint64_t *, CUgraph *, const CUgraphNode **, const CUgraphEdgeData **, size_t *)"
  )
  def cuStreamGetCaptureInfo_v3(
      hStream: CUstream,
      captureStatus_out: Pointer[CUstreamCaptureStatus, ReadWrite, Opaque],
      id_out: Pointer[cuuint64_t, ReadWrite, Opaque],
      graph_out: Pointer[CUgraph, ReadWrite, Opaque],
      dependencies_out: Pointer[Pointer[CUgraphNode, ReadWrite, Opaque], Read, Opaque],
      edgeData_out: Pointer[Pointer[CUgraphEdgeData, ReadWrite, Opaque], Read, Opaque],
      numDependencies_out: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUcontext *)")
  def cuStreamGetCtx(hStream: CUstream, pctx: Pointer[Context, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUcontext *, CUgreenCtx *)")
  def cuStreamGetCtx_v2(
      hStream: CUstream,
      pCtx: Pointer[Context, ReadWrite, Opaque],
      pGreenCtx: Pointer[CUgreenCtx, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUdevResource *, CUdevResourceType)")
  def cuStreamGetDevResource(
      hStream: CUstream,
      resource: Pointer[CUdevResource, ReadWrite, Opaque],
      `type`: CUdevResourceType
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUdevice *)")
  def cuStreamGetDevice(hStream: CUstream, device: Pointer[Device, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, unsigned int *)")
  def cuStreamGetFlags(hStream: CUstream, flags: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUgreenCtx *)")
  def cuStreamGetGreenCtx(hStream: CUstream, phCtx: Pointer[CUgreenCtx, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, unsigned long long *)")
  def cuStreamGetId(hStream: CUstream, streamId: Pointer[unsigned_long_long, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUstream, int *)")
  def cuStreamGetPriority(hStream: CUstream, priority: Pointer[Int, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUstreamCaptureStatus *)")
  def cuStreamIsCapturing(
      hStream: CUstream,
      captureStatus: Pointer[CUstreamCaptureStatus, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream)")
  def cuStreamQuery(hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUstreamAttrID, const CUstreamAttrValue *)")
  def cuStreamSetAttribute(
      hStream: CUstream,
      attr: CUstreamAttrID,
      value: Pointer[CUstreamAttrValue, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream)")
  def cuStreamSynchronize(hStream: CUstream): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUgraphNode *, const CUgraphEdgeData *, size_t, unsigned int)")
  def cuStreamUpdateCaptureDependencies_v2(
      hStream: CUstream,
      dependencies: Pointer[CUgraphNode, ReadWrite, Opaque],
      dependencyData: Pointer[CUgraphEdgeData, Read, Opaque],
      numDependencies: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUevent, unsigned int)")
  def cuStreamWaitEvent(hStream: CUstream, hEvent: CUevent, Flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUstream, CUdeviceptr, cuuint32_t, unsigned int)")
  def cuStreamWaitValue32_v2(stream: CUstream, addr: DevicePtr, value: cuuint32_t, flags: UInt32): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUstream, CUdeviceptr, cuuint64_t, unsigned int)")
  def cuStreamWaitValue64_v2(stream: CUstream, addr: DevicePtr, value: cuuint64_t, flags: UInt32): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUstream, CUdeviceptr, cuuint32_t, unsigned int)")
  def cuStreamWriteValue32_v2(stream: CUstream, addr: DevicePtr, value: cuuint32_t, flags: UInt32): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUstream, CUdeviceptr, cuuint64_t, unsigned int)")
  def cuStreamWriteValue64_v2(stream: CUstream, addr: DevicePtr, value: cuuint64_t, flags: UInt32): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUsurfObject *, const CUDA_RESOURCE_DESC *)")
  def cuSurfObjectCreate(
      pSurfObject: Pointer[CUsurfObject, ReadWrite, Opaque],
      pResDesc: Pointer[CUDA_RESOURCE_DESC, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUsurfObject)")
  def cuSurfObjectDestroy(surfObject: CUsurfObject): CUDAStatus = unsupported
  @CSignature("CUresult (CUDA_RESOURCE_DESC *, CUsurfObject)")
  def cuSurfObjectGetResourceDesc(
      pResDesc: Pointer[CUDA_RESOURCE_DESC, ReadWrite, Opaque],
      surfObject: CUsurfObject
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUarray *, CUsurfref)")
  def cuSurfRefGetArray(phArray: Pointer[CUarray, ReadWrite, Opaque], hSurfRef: CUsurfref): CUDAStatus = unsupported
  @CSignature("CUresult (CUsurfref, CUarray, unsigned int)")
  def cuSurfRefSetArray(hSurfRef: CUsurfref, hArray: CUarray, Flags: UInt32): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUtensorMap *, CUtensorMapDataType, cuuint32_t, void *, const cuuint64_t *, const cuuint64_t *, const int *, const int *, cuuint32_t, cuuint32_t, const cuuint32_t *, CUtensorMapInterleave, CUtensorMapSwizzle, CUtensorMapL2promotion, CUtensorMapFloatOOBfill)"
  )
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
  @CSignature(
    "CUresult (CUtensorMap *, CUtensorMapDataType, cuuint32_t, void *, const cuuint64_t *, const cuuint64_t *, int, int, cuuint32_t, cuuint32_t, const cuuint32_t *, CUtensorMapInterleave, CUtensorMapIm2ColWideMode, CUtensorMapSwizzle, CUtensorMapL2promotion, CUtensorMapFloatOOBfill)"
  )
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
  @CSignature(
    "CUresult (CUtensorMap *, CUtensorMapDataType, cuuint32_t, void *, const cuuint64_t *, const cuuint64_t *, const cuuint32_t *, const cuuint32_t *, CUtensorMapInterleave, CUtensorMapSwizzle, CUtensorMapL2promotion, CUtensorMapFloatOOBfill)"
  )
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
  @CSignature("CUresult (CUtensorMap *, void *)")
  def cuTensorMapReplaceAddress(
      tensorMap: Pointer[CUtensorMap, ReadWrite, Opaque],
      globalAddress: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "CUresult (CUtexObject *, const CUDA_RESOURCE_DESC *, const CUDA_TEXTURE_DESC *, const CUDA_RESOURCE_VIEW_DESC *)"
  )
  def cuTexObjectCreate(
      pTexObject: Pointer[CUtexObject, ReadWrite, Opaque],
      pResDesc: Pointer[CUDA_RESOURCE_DESC, Read, Opaque],
      pTexDesc: Pointer[CUDA_TEXTURE_DESC, Read, Opaque],
      pResViewDesc: Pointer[CUDA_RESOURCE_VIEW_DESC, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUtexObject)")
  def cuTexObjectDestroy(texObject: CUtexObject): CUDAStatus = unsupported
  @CSignature("CUresult (CUDA_RESOURCE_DESC *, CUtexObject)")
  def cuTexObjectGetResourceDesc(
      pResDesc: Pointer[CUDA_RESOURCE_DESC, ReadWrite, Opaque],
      texObject: CUtexObject
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUDA_RESOURCE_VIEW_DESC *, CUtexObject)")
  def cuTexObjectGetResourceViewDesc(
      pResViewDesc: Pointer[CUDA_RESOURCE_VIEW_DESC, ReadWrite, Opaque],
      texObject: CUtexObject
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUDA_TEXTURE_DESC *, CUtexObject)")
  def cuTexObjectGetTextureDesc(
      pTexDesc: Pointer[CUDA_TEXTURE_DESC, ReadWrite, Opaque],
      texObject: CUtexObject
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUtexref *)")
  def cuTexRefCreate(pTexRef: Pointer[CUtexref, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUtexref)")
  def cuTexRefDestroy(hTexRef: CUtexref): CUDAStatus = unsupported
  @CSignature("CUresult (CUaddress_mode *, CUtexref, int)")
  def cuTexRefGetAddressMode(pam: Pointer[CUaddress_mode, ReadWrite, Opaque], hTexRef: CUtexref, dim: Int): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUdeviceptr *, CUtexref)")
  def cuTexRefGetAddress_v2(pdptr: Pointer[DevicePtr, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus = unsupported
  @CSignature("CUresult (CUarray *, CUtexref)")
  def cuTexRefGetArray(phArray: Pointer[CUarray, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus = unsupported
  @CSignature("CUresult (float *, CUtexref)")
  def cuTexRefGetBorderColor(pBorderColor: Pointer[Float, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUfilter_mode *, CUtexref)")
  def cuTexRefGetFilterMode(pfm: Pointer[CUfilter_mode, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus = unsupported
  @CSignature("CUresult (unsigned int *, CUtexref)")
  def cuTexRefGetFlags(pFlags: Pointer[UInt32, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus = unsupported
  @CSignature("CUresult (CUarray_format *, int *, CUtexref)")
  def cuTexRefGetFormat(
      pFormat: Pointer[CUarray_format, ReadWrite, Opaque],
      pNumChannels: Pointer[Int, ReadWrite, Opaque],
      hTexRef: CUtexref
  ): CUDAStatus = unsupported
  @CSignature("CUresult (int *, CUtexref)")
  def cuTexRefGetMaxAnisotropy(pmaxAniso: Pointer[Int, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus = unsupported
  @CSignature("CUresult (CUfilter_mode *, CUtexref)")
  def cuTexRefGetMipmapFilterMode(pfm: Pointer[CUfilter_mode, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus =
    unsupported
  @CSignature("CUresult (float *, CUtexref)")
  def cuTexRefGetMipmapLevelBias(pbias: Pointer[Float, ReadWrite, Opaque], hTexRef: CUtexref): CUDAStatus = unsupported
  @CSignature("CUresult (float *, float *, CUtexref)")
  def cuTexRefGetMipmapLevelClamp(
      pminMipmapLevelClamp: Pointer[Float, ReadWrite, Opaque],
      pmaxMipmapLevelClamp: Pointer[Float, ReadWrite, Opaque],
      hTexRef: CUtexref
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUmipmappedArray *, CUtexref)")
  def cuTexRefGetMipmappedArray(
      phMipmappedArray: Pointer[CUmipmappedArray, ReadWrite, Opaque],
      hTexRef: CUtexref
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUtexref, const CUDA_ARRAY_DESCRIPTOR *, CUdeviceptr, size_t)")
  def cuTexRefSetAddress2D_v3(
      hTexRef: CUtexref,
      desc: Pointer[CUDA_ARRAY_DESCRIPTOR, Read, Opaque],
      dptr: DevicePtr,
      Pitch: SizeT
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUtexref, int, CUaddress_mode)")
  def cuTexRefSetAddressMode(hTexRef: CUtexref, dim: Int, am: CUaddress_mode): CUDAStatus = unsupported
  @CSignature("CUresult (size_t *, CUtexref, CUdeviceptr, size_t)")
  def cuTexRefSetAddress_v2(
      ByteOffset: Pointer[SizeT, ReadWrite, Opaque],
      hTexRef: CUtexref,
      dptr: DevicePtr,
      bytes: SizeT
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUtexref, CUarray, unsigned int)")
  def cuTexRefSetArray(hTexRef: CUtexref, hArray: CUarray, Flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUtexref, float *)")
  def cuTexRefSetBorderColor(hTexRef: CUtexref, pBorderColor: Pointer[Float, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUtexref, CUfilter_mode)")
  def cuTexRefSetFilterMode(hTexRef: CUtexref, fm: CUfilter_mode): CUDAStatus = unsupported
  @CSignature("CUresult (CUtexref, unsigned int)")
  def cuTexRefSetFlags(hTexRef: CUtexref, Flags: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUtexref, CUarray_format, int)")
  def cuTexRefSetFormat(hTexRef: CUtexref, fmt: CUarray_format, NumPackedComponents: Int): CUDAStatus = unsupported
  @CSignature("CUresult (CUtexref, unsigned int)")
  def cuTexRefSetMaxAnisotropy(hTexRef: CUtexref, maxAniso: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUtexref, CUfilter_mode)")
  def cuTexRefSetMipmapFilterMode(hTexRef: CUtexref, fm: CUfilter_mode): CUDAStatus = unsupported
  @CSignature("CUresult (CUtexref, float)")
  def cuTexRefSetMipmapLevelBias(hTexRef: CUtexref, bias: Float): CUDAStatus = unsupported
  @CSignature("CUresult (CUtexref, float, float)")
  def cuTexRefSetMipmapLevelClamp(
      hTexRef: CUtexref,
      minMipmapLevelClamp: Float,
      maxMipmapLevelClamp: Float
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUtexref, CUmipmappedArray, unsigned int)")
  def cuTexRefSetMipmappedArray(hTexRef: CUtexref, hMipmappedArray: CUmipmappedArray, Flags: UInt32): CUDAStatus =
    unsupported
  @CSignature("CUresult (CUstreamCaptureMode *)")
  def cuThreadExchangeStreamCaptureMode(mode: Pointer[CUstreamCaptureMode, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("CUresult (CUuserObject *, void *, CUhostFn, unsigned int, unsigned int)")
  def cuUserObjectCreate(
      object_out: Pointer[CUuserObject, ReadWrite, Opaque],
      ptr: Pointer[CVoid, ReadWrite, Opaque],
      destroy: CUhostFn,
      initialRefcount: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("CUresult (CUuserObject, unsigned int)")
  def cuUserObjectRelease(`object`: CUuserObject, count: UInt32): CUDAStatus = unsupported
  @CSignature("CUresult (CUuserObject, unsigned int)")
  def cuUserObjectRetain(`object`: CUuserObject, count: UInt32): CUDAStatus = unsupported
  @CSignature(
    "CUresult (const CUexternalSemaphore *, const CUDA_EXTERNAL_SEMAPHORE_WAIT_PARAMS *, unsigned int, CUstream)"
  )
  def cuWaitExternalSemaphoresAsync(
      extSemArray: Pointer[CUexternalSemaphore, Read, Opaque],
      paramsArray: Pointer[CUDA_EXTERNAL_SEMAPHORE_WAIT_PARAMS, Read, Opaque],
      numExtSems: UInt32,
      stream: CUstream
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaChannelFormatDesc *, struct cudaExtent *, unsigned int *, cudaArray_t)")
  def cudaArrayGetInfo(
      desc: Pointer[cudaChannelFormatDesc, ReadWrite, Opaque],
      extent: Pointer[cudaExtent, ReadWrite, Opaque],
      flags: Pointer[UInt32, ReadWrite, Opaque],
      array: cudaArray_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaArrayMemoryRequirements *, cudaArray_t, int)")
  def cudaArrayGetMemoryRequirements(
      memoryRequirements: Pointer[cudaArrayMemoryRequirements, ReadWrite, Opaque],
      array: cudaArray_t,
      device: Int
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaArray_t *, cudaArray_t, unsigned int)")
  def cudaArrayGetPlane(
      pPlaneArray: Pointer[cudaArray_t, ReadWrite, Opaque],
      hArray: cudaArray_t,
      planeIdx: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaArraySparseProperties *, cudaArray_t)")
  def cudaArrayGetSparseProperties(
      sparseProperties: Pointer[cudaArraySparseProperties, ReadWrite, Opaque],
      array: cudaArray_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *, const struct cudaDeviceProp *)")
  def cudaChooseDevice(
      device: Pointer[Int, ReadWrite, Opaque],
      prop: Pointer[cudaDeviceProp, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("struct cudaChannelFormatDesc (int, int, int, int, enum cudaChannelFormatKind)")
  def cudaCreateChannelDesc(x: Int, y: Int, z: Int, w: Int, f: cudaChannelFormatKind): cudaChannelFormatDesc =
    unsupported
  @CSignature("cudaError_t (cudaSurfaceObject_t *, const struct cudaResourceDesc *)")
  def cudaCreateSurfaceObject(
      pSurfObject: Pointer[cudaSurfaceObject_t, ReadWrite, Opaque],
      pResDesc: Pointer[cudaResourceDesc, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaTextureObject_t *, const struct cudaResourceDesc *, const struct cudaTextureDesc *, const struct cudaResourceViewDesc *)"
  )
  def cudaCreateTextureObject(
      pTexObject: Pointer[cudaTextureObject_t, ReadWrite, Opaque],
      pResDesc: Pointer[cudaResourceDesc, Read, Opaque],
      pTexDesc: Pointer[cudaTextureDesc, Read, Opaque],
      pResViewDesc: Pointer[cudaResourceViewDesc, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void)")
  def cudaCtxResetPersistingL2Cache(): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaExternalMemory_t)")
  def cudaDestroyExternalMemory(extMem: cudaExternalMemory_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaExternalSemaphore_t)")
  def cudaDestroyExternalSemaphore(extSem: cudaExternalSemaphore_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaSurfaceObject_t)")
  def cudaDestroySurfaceObject(surfObject: cudaSurfaceObject_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaTextureObject_t)")
  def cudaDestroyTextureObject(texObject: cudaTextureObject_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaDevResourceDesc_t *, cudaDevResource *, unsigned int)")
  def cudaDevResourceGenerateDesc(
      phDesc: Pointer[cudaDevResourceDesc_t, ReadWrite, Opaque],
      resources: Pointer[cudaDevResource, ReadWrite, Opaque],
      nbResources: UInt32
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaDevResource *, unsigned int, const cudaDevResource *, cudaDevResource *, unsigned int, cudaDevSmResourceGroupParams *)"
  )
  def cudaDevSmResourceSplit(
      result: Pointer[cudaDevResource, ReadWrite, Opaque],
      nbGroups: UInt32,
      input: Pointer[cudaDevResource, Read, Opaque],
      remainder: Pointer[cudaDevResource, ReadWrite, Opaque],
      flags: UInt32,
      groupParams: Pointer[cudaDevSmResourceGroupParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaDevResource *, unsigned int *, const cudaDevResource *, cudaDevResource *, unsigned int, unsigned int)"
  )
  def cudaDevSmResourceSplitByCount(
      result: Pointer[cudaDevResource, ReadWrite, Opaque],
      nbGroups: Pointer[UInt32, ReadWrite, Opaque],
      input: Pointer[cudaDevResource, Read, Opaque],
      remaining: Pointer[cudaDevResource, ReadWrite, Opaque],
      flags: UInt32,
      minCount: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *, int, int)")
  def cudaDeviceCanAccessPeer(
      canAccessPeer: Pointer[Int, ReadWrite, Opaque],
      device: Int,
      peerDevice: Int
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int)")
  def cudaDeviceDisablePeerAccess(peerDevice: Int): CUDAStatus = unsupported
  @CSignature("cudaError_t (int, unsigned int)")
  def cudaDeviceEnablePeerAccess(peerDevice: Int, flags: UInt32): CUDAStatus = unsupported
  @CSignature("cudaError_t (enum cudaFlushGPUDirectRDMAWritesTarget, enum cudaFlushGPUDirectRDMAWritesScope)")
  def cudaDeviceFlushGPUDirectRDMAWrites(
      target: cudaFlushGPUDirectRDMAWritesTarget,
      scope: cudaFlushGPUDirectRDMAWritesScope
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *, enum cudaDeviceAttr, int)")
  def cudaDeviceGetAttribute(value: Pointer[Int, ReadWrite, Opaque], attr: cudaDeviceAttr, device: Int): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (int *, const char *)")
  def cudaDeviceGetByPCIBusId(device: Pointer[Int, ReadWrite, Opaque], pciBusId: CString): CUDAStatus = unsupported
  @CSignature("cudaError_t (enum cudaFuncCache *)")
  def cudaDeviceGetCacheConfig(pCacheConfig: Pointer[cudaFuncCache, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaMemPool_t *, int)")
  def cudaDeviceGetDefaultMemPool(memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque], device: Int): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (int, cudaDevResource *, enum cudaDevResourceType)")
  def cudaDeviceGetDevResource(
      device: Int,
      resource: Pointer[cudaDevResource, ReadWrite, Opaque],
      `type`: cudaDevResourceType
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaExecutionContext_t *, int)")
  def cudaDeviceGetExecutionCtx(ctx: Pointer[cudaExecutionContext_t, ReadWrite, Opaque], device: Int): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (int, enum cudaGraphMemAttributeType, void *)")
  def cudaDeviceGetGraphMemAttribute(
      device: Int,
      attr: cudaGraphMemAttributeType,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (unsigned int *, const enum cudaAtomicOperation *, unsigned int, int)")
  def cudaDeviceGetHostAtomicCapabilities(
      capabilities: Pointer[UInt32, ReadWrite, Opaque],
      operations: Pointer[cudaAtomicOperation, Read, Opaque],
      count: UInt32,
      device: Int
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (size_t *, enum cudaLimit)")
  def cudaDeviceGetLimit(pValue: Pointer[SizeT, ReadWrite, Opaque], limit: cudaLimit): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaMemPool_t *, int)")
  def cudaDeviceGetMemPool(memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque], device: Int): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, int, int)")
  def cudaDeviceGetNvSciSyncAttributes(
      nvSciSyncAttrList: Pointer[CVoid, ReadWrite, Opaque],
      device: Int,
      flags: Int
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (unsigned int *, const enum cudaAtomicOperation *, unsigned int, int, int)")
  def cudaDeviceGetP2PAtomicCapabilities(
      capabilities: Pointer[UInt32, ReadWrite, Opaque],
      operations: Pointer[cudaAtomicOperation, Read, Opaque],
      count: UInt32,
      srcDevice: Int,
      dstDevice: Int
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *, enum cudaDeviceP2PAttr, int, int)")
  def cudaDeviceGetP2PAttribute(
      value: Pointer[Int, ReadWrite, Opaque],
      attr: cudaDeviceP2PAttr,
      srcDevice: Int,
      dstDevice: Int
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (char *, int, int)")
  def cudaDeviceGetPCIBusId(pciBusId: Pointer[Byte, ReadWrite, Opaque], len: Int, device: Int): CUDAStatus = unsupported
  @CSignature("cudaError_t (enum cudaSharedMemConfig *)")
  def cudaDeviceGetSharedMemConfig(pConfig: Pointer[cudaSharedMemConfig, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *, int *)")
  def cudaDeviceGetStreamPriorityRange(
      leastPriority: Pointer[Int, ReadWrite, Opaque],
      greatestPriority: Pointer[Int, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (size_t *, const struct cudaChannelFormatDesc *, int)")
  def cudaDeviceGetTexture1DLinearMaxWidth(
      maxWidthInElements: Pointer[SizeT, ReadWrite, Opaque],
      fmtDesc: Pointer[cudaChannelFormatDesc, Read, Opaque],
      device: Int
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int)")
  def cudaDeviceGraphMemTrim(device: Int): CUDAStatus = unsupported
  @CSignature("cudaError_t (int, cudaAsyncCallback, void *, cudaAsyncCallbackHandle_t *)")
  def cudaDeviceRegisterAsyncNotification(
      device: Int,
      callbackFunc: cudaAsyncCallback,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      callback: Pointer[cudaAsyncCallbackHandle_t, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void)")
  def cudaDeviceReset(): CUDAStatus = unsupported
  @CSignature("cudaError_t (enum cudaFuncCache)")
  def cudaDeviceSetCacheConfig(cacheConfig: cudaFuncCache): CUDAStatus = unsupported
  @CSignature("cudaError_t (int, enum cudaGraphMemAttributeType, void *)")
  def cudaDeviceSetGraphMemAttribute(
      device: Int,
      attr: cudaGraphMemAttributeType,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (enum cudaLimit, size_t)")
  def cudaDeviceSetLimit(limit: cudaLimit, value: SizeT): CUDAStatus = unsupported
  @CSignature("cudaError_t (int, cudaMemPool_t)")
  def cudaDeviceSetMemPool(device: Int, memPool: cudaMemPool_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (enum cudaSharedMemConfig)")
  def cudaDeviceSetSharedMemConfig(config: cudaSharedMemConfig): CUDAStatus = unsupported
  @CSignature("cudaError_t (void)")
  def cudaDeviceSynchronize(): CUDAStatus = unsupported
  @CSignature("cudaError_t (int, cudaAsyncCallbackHandle_t)")
  def cudaDeviceUnregisterAsyncNotification(device: Int, callback: cudaAsyncCallbackHandle_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *)")
  def cudaDriverGetVersion(driverVersion: Pointer[Int, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaEvent_t *)")
  def cudaEventCreate(event: Pointer[cudaEvent_t, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaEvent_t *, unsigned int)")
  def cudaEventCreateWithFlags(event: Pointer[cudaEvent_t, ReadWrite, Opaque], flags: UInt32): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaEvent_t)")
  def cudaEventDestroy(event: cudaEvent_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (float *, cudaEvent_t, cudaEvent_t)")
  def cudaEventElapsedTime(ms: Pointer[Float, ReadWrite, Opaque], start: cudaEvent_t, end: cudaEvent_t): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaEvent_t)")
  def cudaEventQuery(event: cudaEvent_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaEvent_t, cudaStream_t)")
  def cudaEventRecord(event: cudaEvent_t, stream: cudaStream_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaEvent_t, cudaStream_t, unsigned int)")
  def cudaEventRecordWithFlags(event: cudaEvent_t, stream: cudaStream_t, flags: UInt32): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaEvent_t)")
  def cudaEventSynchronize(event: cudaEvent_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaExecutionContext_t)")
  def cudaExecutionCtxDestroy(ctx: cudaExecutionContext_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaExecutionContext_t, cudaDevResource *, enum cudaDevResourceType)")
  def cudaExecutionCtxGetDevResource(
      ctx: cudaExecutionContext_t,
      resource: Pointer[cudaDevResource, ReadWrite, Opaque],
      `type`: cudaDevResourceType
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *, cudaExecutionContext_t)")
  def cudaExecutionCtxGetDevice(device: Pointer[Int, ReadWrite, Opaque], ctx: cudaExecutionContext_t): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaExecutionContext_t, unsigned long long *)")
  def cudaExecutionCtxGetId(
      ctx: cudaExecutionContext_t,
      ctxId: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaExecutionContext_t, cudaEvent_t)")
  def cudaExecutionCtxRecordEvent(ctx: cudaExecutionContext_t, event: cudaEvent_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t *, cudaExecutionContext_t, unsigned int, int)")
  def cudaExecutionCtxStreamCreate(
      phStream: Pointer[cudaStream_t, ReadWrite, Opaque],
      ctx: cudaExecutionContext_t,
      flags: UInt32,
      priority: Int
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaExecutionContext_t)")
  def cudaExecutionCtxSynchronize(ctx: cudaExecutionContext_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaExecutionContext_t, cudaEvent_t)")
  def cudaExecutionCtxWaitEvent(ctx: cudaExecutionContext_t, event: cudaEvent_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, cudaExternalMemory_t, const struct cudaExternalMemoryBufferDesc *)")
  def cudaExternalMemoryGetMappedBuffer(
      devPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      extMem: cudaExternalMemory_t,
      bufferDesc: Pointer[cudaExternalMemoryBufferDesc, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaMipmappedArray_t *, cudaExternalMemory_t, const struct cudaExternalMemoryMipmappedArrayDesc *)"
  )
  def cudaExternalMemoryGetMappedMipmappedArray(
      mipmap: Pointer[cudaMipmappedArray_t, ReadWrite, Opaque],
      extMem: cudaExternalMemory_t,
      mipmapDesc: Pointer[cudaExternalMemoryMipmappedArrayDesc, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *)")
  def cudaFree(devPtr: DevicePtr): CUDAStatus
  @CSignature("cudaError_t (cudaArray_t)")
  def cudaFreeArray(array: cudaArray_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, cudaStream_t)")
  def cudaFreeAsync(devPtr: Pointer[CVoid, ReadWrite, Opaque], hStream: cudaStream_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *)")
  def cudaFreeHost(ptr: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaMipmappedArray_t)")
  def cudaFreeMipmappedArray(mipmappedArray: cudaMipmappedArray_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaFuncAttributes *, const void *)")
  def cudaFuncGetAttributes(
      attr: Pointer[cudaFuncAttributes, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (const char **, const void *)")
  def cudaFuncGetName(
      name: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque],
      func: Pointer[CVoid, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (const void *, size_t *)")
  def cudaFuncGetParamCount(
      func: Pointer[CVoid, Read, Opaque],
      paramCount: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (const void *, size_t, size_t *, size_t *)")
  def cudaFuncGetParamInfo(
      func: Pointer[CVoid, Read, Opaque],
      paramIndex: SizeT,
      paramOffset: Pointer[SizeT, ReadWrite, Opaque],
      paramSize: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (const void *, enum cudaFuncAttribute, int)")
  def cudaFuncSetAttribute(func: Pointer[CVoid, Read, Opaque], attr: cudaFuncAttribute, value: Int): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (const void *, enum cudaFuncCache)")
  def cudaFuncSetCacheConfig(func: Pointer[CVoid, Read, Opaque], cacheConfig: cudaFuncCache): CUDAStatus = unsupported
  @CSignature("cudaError_t (const void *, enum cudaSharedMemConfig)")
  def cudaFuncSetSharedMemConfig(func: Pointer[CVoid, Read, Opaque], config: cudaSharedMemConfig): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (struct cudaChannelFormatDesc *, cudaArray_const_t)")
  def cudaGetChannelDesc(
      desc: Pointer[cudaChannelFormatDesc, ReadWrite, Opaque],
      array: cudaArray_const_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *)")
  def cudaGetDevice(device: Pointer[Int, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *)")
  def cudaGetDeviceCount(count: Out[Int]): CUDAStatus
  @CSignature("cudaError_t (unsigned int *)")
  def cudaGetDeviceFlags(flags: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaDeviceProp *, int)")
  def cudaGetDeviceProperties(prop: Pointer[cudaDeviceProp, ReadWrite, Opaque], device: Int): CUDAStatus = unsupported
  @CSignature("cudaError_t (const char *, void **, unsigned long long, enum cudaDriverEntryPointQueryResult *)")
  def cudaGetDriverEntryPoint(
      symbol: CString,
      funcPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      flags: unsigned_long_long,
      driverStatus: Pointer[cudaDriverEntryPointQueryResult, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (const char *, void **, unsigned int, unsigned long long, enum cudaDriverEntryPointQueryResult *)"
  )
  def cudaGetDriverEntryPointByVersion(
      symbol: CString,
      funcPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      cudaVersion: UInt32,
      flags: unsigned_long_long,
      driverStatus: Pointer[cudaDriverEntryPointQueryResult, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("const char *(cudaError_t)")
  def cudaGetErrorName(error: CUDAStatus): CString = unsupported
  @CSignature("const char *(cudaError_t)")
  def cudaGetErrorString(error: CUDAStatus): CString = unsupported
  @CSignature("cudaError_t (const void **, const cudaUUID_t *)")
  def cudaGetExportTable(
      ppExportTable: Pointer[Pointer[CVoid, ReadWrite, Opaque], Read, Opaque],
      pExportTableId: Pointer[cudaUUID_t, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaFunction_t *, const void *)")
  def cudaGetFuncBySymbol(
      functionPtr: Pointer[cudaFunction_t, ReadWrite, Opaque],
      symbolPtr: Pointer[CVoid, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaKernel_t *, const void *)")
  def cudaGetKernel(
      kernelPtr: Pointer[cudaKernel_t, ReadWrite, Opaque],
      entryFuncAddr: Pointer[CVoid, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void)")
  def cudaGetLastError(): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaArray_t *, cudaMipmappedArray_const_t, unsigned int)")
  def cudaGetMipmappedArrayLevel(
      levelArray: Pointer[cudaArray_t, ReadWrite, Opaque],
      mipmappedArray: cudaMipmappedArray_const_t,
      level: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaResourceDesc *, cudaSurfaceObject_t)")
  def cudaGetSurfaceObjectResourceDesc(
      pResDesc: Pointer[cudaResourceDesc, ReadWrite, Opaque],
      surfObject: cudaSurfaceObject_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, const void *)")
  def cudaGetSymbolAddress(
      devPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      symbol: Pointer[CVoid, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (size_t *, const void *)")
  def cudaGetSymbolSize(size: Pointer[SizeT, ReadWrite, Opaque], symbol: Pointer[CVoid, Read, Opaque]): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (struct cudaResourceDesc *, cudaTextureObject_t)")
  def cudaGetTextureObjectResourceDesc(
      pResDesc: Pointer[cudaResourceDesc, ReadWrite, Opaque],
      texObject: cudaTextureObject_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaResourceViewDesc *, cudaTextureObject_t)")
  def cudaGetTextureObjectResourceViewDesc(
      pResViewDesc: Pointer[cudaResourceViewDesc, ReadWrite, Opaque],
      texObject: cudaTextureObject_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaTextureDesc *, cudaTextureObject_t)")
  def cudaGetTextureObjectTextureDesc(
      pTexDesc: Pointer[cudaTextureDesc, ReadWrite, Opaque],
      texObject: cudaTextureObject_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t, cudaGraph_t)")
  def cudaGraphAddChildGraphNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      childGraph: cudaGraph_t
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaGraph_t, const cudaGraphNode_t *, const cudaGraphNode_t *, const cudaGraphEdgeData *, size_t)"
  )
  def cudaGraphAddDependencies(
      graph: cudaGraph_t,
      from: Pointer[cudaGraphNode_t, Read, Opaque],
      to: Pointer[cudaGraphNode_t, Read, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: SizeT
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t)")
  def cudaGraphAddEmptyNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t, cudaEvent_t)")
  def cudaGraphAddEventRecordNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      event: cudaEvent_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t, cudaEvent_t)")
  def cudaGraphAddEventWaitNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      event: cudaEvent_t
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t, const struct cudaExternalSemaphoreSignalNodeParams *)"
  )
  def cudaGraphAddExternalSemaphoresSignalNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[cudaExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t, const struct cudaExternalSemaphoreWaitNodeParams *)"
  )
  def cudaGraphAddExternalSemaphoresWaitNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[cudaExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t, const struct cudaHostNodeParams *)"
  )
  def cudaGraphAddHostNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      pNodeParams: Pointer[cudaHostNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t, const struct cudaKernelNodeParams *)"
  )
  def cudaGraphAddKernelNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      pNodeParams: Pointer[cudaKernelNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t, struct cudaMemAllocNodeParams *)"
  )
  def cudaGraphAddMemAllocNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[cudaMemAllocNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t, void *)")
  def cudaGraphAddMemFreeNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      dptr: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t, const struct cudaMemcpy3DParms *)"
  )
  def cudaGraphAddMemcpyNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      pCopyParams: Pointer[cudaMemcpy3DParms, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t, void *, const void *, size_t, enum cudaMemcpyKind)"
  )
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
  @CSignature(
    "cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t, void *, const void *, size_t, size_t, enum cudaMemcpyKind)"
  )
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
  @CSignature(
    "cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t, const void *, const void *, size_t, size_t, enum cudaMemcpyKind)"
  )
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
  @CSignature(
    "cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, size_t, const struct cudaMemsetParams *)"
  )
  def cudaGraphAddMemsetNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      numDependencies: SizeT,
      pMemsetParams: Pointer[cudaMemsetParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaGraphNode_t *, cudaGraph_t, const cudaGraphNode_t *, const cudaGraphEdgeData *, size_t, struct cudaGraphNodeParams *)"
  )
  def cudaGraphAddNode(
      pGraphNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      pDependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      dependencyData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: SizeT,
      nodeParams: Pointer[cudaGraphNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, cudaGraph_t *)")
  def cudaGraphChildGraphNodeGetGraph(
      node: cudaGraphNode_t,
      pGraph: Pointer[cudaGraph_t, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraph_t *, cudaGraph_t)")
  def cudaGraphClone(pGraphClone: Pointer[cudaGraph_t, ReadWrite, Opaque], originalGraph: cudaGraph_t): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaGraphConditionalHandle *, cudaGraph_t, unsigned int, unsigned int)")
  def cudaGraphConditionalHandleCreate(
      pHandle_out: Pointer[cudaGraphConditionalHandle, ReadWrite, Opaque],
      graph: cudaGraph_t,
      defaultLaunchValue: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaGraphConditionalHandle *, cudaGraph_t, cudaExecutionContext_t, unsigned int, unsigned int)"
  )
  def cudaGraphConditionalHandleCreate_v2(
      pHandle_out: Pointer[cudaGraphConditionalHandle, ReadWrite, Opaque],
      graph: cudaGraph_t,
      ctx: cudaExecutionContext_t,
      defaultLaunchValue: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraph_t *, unsigned int)")
  def cudaGraphCreate(pGraph: Pointer[cudaGraph_t, ReadWrite, Opaque], flags: UInt32): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraph_t, const char *, unsigned int)")
  def cudaGraphDebugDotPrint(graph: cudaGraph_t, path: CString, flags: UInt32): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraph_t)")
  def cudaGraphDestroy(graph: cudaGraph_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t)")
  def cudaGraphDestroyNode(node: cudaGraphNode_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, cudaEvent_t *)")
  def cudaGraphEventRecordNodeGetEvent(
      node: cudaGraphNode_t,
      event_out: Pointer[cudaEvent_t, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, cudaEvent_t)")
  def cudaGraphEventRecordNodeSetEvent(node: cudaGraphNode_t, event: cudaEvent_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, cudaEvent_t *)")
  def cudaGraphEventWaitNodeGetEvent(
      node: cudaGraphNode_t,
      event_out: Pointer[cudaEvent_t, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, cudaEvent_t)")
  def cudaGraphEventWaitNodeSetEvent(node: cudaGraphNode_t, event: cudaEvent_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaGraphNode_t, cudaGraph_t)")
  def cudaGraphExecChildGraphNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      childGraph: cudaGraph_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t)")
  def cudaGraphExecDestroy(graphExec: cudaGraphExec_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaGraphNode_t, cudaEvent_t)")
  def cudaGraphExecEventRecordNodeSetEvent(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      event: cudaEvent_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaGraphNode_t, cudaEvent_t)")
  def cudaGraphExecEventWaitNodeSetEvent(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      event: cudaEvent_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaGraphNode_t, const struct cudaExternalSemaphoreSignalNodeParams *)")
  def cudaGraphExecExternalSemaphoresSignalNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      nodeParams: Pointer[cudaExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaGraphNode_t, const struct cudaExternalSemaphoreWaitNodeParams *)")
  def cudaGraphExecExternalSemaphoresWaitNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      nodeParams: Pointer[cudaExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, unsigned long long *)")
  def cudaGraphExecGetFlags(
      graphExec: cudaGraphExec_t,
      flags: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, unsigned int *)")
  def cudaGraphExecGetId(hGraphExec: cudaGraphExec_t, graphID: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaGraphNode_t, const struct cudaHostNodeParams *)")
  def cudaGraphExecHostNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaHostNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaGraphNode_t, const struct cudaKernelNodeParams *)")
  def cudaGraphExecKernelNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaKernelNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaGraphNode_t, const struct cudaMemcpy3DParms *)")
  def cudaGraphExecMemcpyNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaMemcpy3DParms, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaGraphNode_t, void *, const void *, size_t, enum cudaMemcpyKind)")
  def cudaGraphExecMemcpyNodeSetParams1D(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaGraphExec_t, cudaGraphNode_t, void *, const void *, size_t, size_t, enum cudaMemcpyKind)"
  )
  def cudaGraphExecMemcpyNodeSetParamsFromSymbol(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      dst: Pointer[CVoid, ReadWrite, Opaque],
      symbol: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaGraphExec_t, cudaGraphNode_t, const void *, const void *, size_t, size_t, enum cudaMemcpyKind)"
  )
  def cudaGraphExecMemcpyNodeSetParamsToSymbol(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaGraphNode_t, const struct cudaMemsetParams *)")
  def cudaGraphExecMemsetNodeSetParams(
      hGraphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaMemsetParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaGraphNode_t, struct cudaGraphNodeParams *)")
  def cudaGraphExecNodeSetParams(
      graphExec: cudaGraphExec_t,
      node: cudaGraphNode_t,
      nodeParams: Pointer[cudaGraphNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaGraph_t, cudaGraphExecUpdateResultInfo *)")
  def cudaGraphExecUpdate(
      hGraphExec: cudaGraphExec_t,
      hGraph: cudaGraph_t,
      resultInfo: Pointer[cudaGraphExecUpdateResultInfo, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, struct cudaExternalSemaphoreSignalNodeParams *)")
  def cudaGraphExternalSemaphoresSignalNodeGetParams(
      hNode: cudaGraphNode_t,
      params_out: Pointer[cudaExternalSemaphoreSignalNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, const struct cudaExternalSemaphoreSignalNodeParams *)")
  def cudaGraphExternalSemaphoresSignalNodeSetParams(
      hNode: cudaGraphNode_t,
      nodeParams: Pointer[cudaExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, struct cudaExternalSemaphoreWaitNodeParams *)")
  def cudaGraphExternalSemaphoresWaitNodeGetParams(
      hNode: cudaGraphNode_t,
      params_out: Pointer[cudaExternalSemaphoreWaitNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, const struct cudaExternalSemaphoreWaitNodeParams *)")
  def cudaGraphExternalSemaphoresWaitNodeSetParams(
      hNode: cudaGraphNode_t,
      nodeParams: Pointer[cudaExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraph_t, cudaGraphNode_t *, cudaGraphNode_t *, cudaGraphEdgeData *, size_t *)")
  def cudaGraphGetEdges(
      graph: cudaGraph_t,
      from: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      to: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, ReadWrite, Opaque],
      numEdges: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraph_t, unsigned int *)")
  def cudaGraphGetId(hGraph: cudaGraph_t, graphID: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraph_t, cudaGraphNode_t *, size_t *)")
  def cudaGraphGetNodes(
      graph: cudaGraph_t,
      nodes: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      numNodes: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraph_t, cudaGraphNode_t *, size_t *)")
  def cudaGraphGetRootNodes(
      graph: cudaGraph_t,
      pRootNodes: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      pNumRootNodes: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, struct cudaHostNodeParams *)")
  def cudaGraphHostNodeGetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaHostNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, const struct cudaHostNodeParams *)")
  def cudaGraphHostNodeSetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaHostNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t *, cudaGraph_t, unsigned long long)")
  def cudaGraphInstantiate(
      pGraphExec: Pointer[cudaGraphExec_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t *, cudaGraph_t, unsigned long long)")
  def cudaGraphInstantiateWithFlags(
      pGraphExec: Pointer[cudaGraphExec_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      flags: unsigned_long_long
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t *, cudaGraph_t, cudaGraphInstantiateParams *)")
  def cudaGraphInstantiateWithParams(
      pGraphExec: Pointer[cudaGraphExec_t, ReadWrite, Opaque],
      graph: cudaGraph_t,
      instantiateParams: Pointer[cudaGraphInstantiateParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, cudaGraphNode_t)")
  def cudaGraphKernelNodeCopyAttributes(hDst: cudaGraphNode_t, hSrc: cudaGraphNode_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, cudaLaunchAttributeID, cudaLaunchAttributeValue *)")
  def cudaGraphKernelNodeGetAttribute(
      hNode: cudaGraphNode_t,
      attr: cudaLaunchAttributeID,
      value_out: Pointer[cudaLaunchAttributeValue, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, struct cudaKernelNodeParams *)")
  def cudaGraphKernelNodeGetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaKernelNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, cudaLaunchAttributeID, const cudaLaunchAttributeValue *)")
  def cudaGraphKernelNodeSetAttribute(
      hNode: cudaGraphNode_t,
      attr: cudaLaunchAttributeID,
      value: Pointer[cudaLaunchAttributeValue, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, const struct cudaKernelNodeParams *)")
  def cudaGraphKernelNodeSetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaKernelNodeParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaStream_t)")
  def cudaGraphLaunch(graphExec: cudaGraphExec_t, stream: cudaStream_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, struct cudaMemAllocNodeParams *)")
  def cudaGraphMemAllocNodeGetParams(
      node: cudaGraphNode_t,
      params_out: Pointer[cudaMemAllocNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, void *)")
  def cudaGraphMemFreeNodeGetParams(node: cudaGraphNode_t, dptr_out: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, struct cudaMemcpy3DParms *)")
  def cudaGraphMemcpyNodeGetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaMemcpy3DParms, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, const struct cudaMemcpy3DParms *)")
  def cudaGraphMemcpyNodeSetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaMemcpy3DParms, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, void *, const void *, size_t, enum cudaMemcpyKind)")
  def cudaGraphMemcpyNodeSetParams1D(
      node: cudaGraphNode_t,
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, void *, const void *, size_t, size_t, enum cudaMemcpyKind)")
  def cudaGraphMemcpyNodeSetParamsFromSymbol(
      node: cudaGraphNode_t,
      dst: Pointer[CVoid, ReadWrite, Opaque],
      symbol: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, const void *, const void *, size_t, size_t, enum cudaMemcpyKind)")
  def cudaGraphMemcpyNodeSetParamsToSymbol(
      node: cudaGraphNode_t,
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, struct cudaMemsetParams *)")
  def cudaGraphMemsetNodeGetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaMemsetParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, const struct cudaMemsetParams *)")
  def cudaGraphMemsetNodeSetParams(
      node: cudaGraphNode_t,
      pNodeParams: Pointer[cudaMemsetParams, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t *, cudaGraphNode_t, cudaGraph_t)")
  def cudaGraphNodeFindInClone(
      pNode: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      originalNode: cudaGraphNode_t,
      clonedGraph: cudaGraph_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, cudaGraph_t *)")
  def cudaGraphNodeGetContainingGraph(
      hNode: cudaGraphNode_t,
      phGraph: Pointer[cudaGraph_t, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, cudaGraphNode_t *, cudaGraphEdgeData *, size_t *)")
  def cudaGraphNodeGetDependencies(
      node: cudaGraphNode_t,
      pDependencies: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, ReadWrite, Opaque],
      pNumDependencies: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, cudaGraphNode_t *, cudaGraphEdgeData *, size_t *)")
  def cudaGraphNodeGetDependentNodes(
      node: cudaGraphNode_t,
      pDependentNodes: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, ReadWrite, Opaque],
      pNumDependentNodes: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaGraphNode_t, unsigned int *)")
  def cudaGraphNodeGetEnabled(
      hGraphExec: cudaGraphExec_t,
      hNode: cudaGraphNode_t,
      isEnabled: Pointer[UInt32, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, unsigned int *)")
  def cudaGraphNodeGetLocalId(hNode: cudaGraphNode_t, nodeId: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, struct cudaGraphNodeParams *)")
  def cudaGraphNodeGetParams(
      node: cudaGraphNode_t,
      nodeParams: Pointer[cudaGraphNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, unsigned long long *)")
  def cudaGraphNodeGetToolsId(
      hNode: cudaGraphNode_t,
      toolsNodeId: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, enum cudaGraphNodeType *)")
  def cudaGraphNodeGetType(node: cudaGraphNode_t, pType: Pointer[cudaGraphNodeType, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaGraphNode_t, unsigned int)")
  def cudaGraphNodeSetEnabled(hGraphExec: cudaGraphExec_t, hNode: cudaGraphNode_t, isEnabled: UInt32): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaGraphNode_t, struct cudaGraphNodeParams *)")
  def cudaGraphNodeSetParams(
      node: cudaGraphNode_t,
      nodeParams: Pointer[cudaGraphNodeParams, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraph_t, cudaUserObject_t, unsigned int)")
  def cudaGraphReleaseUserObject(graph: cudaGraph_t, `object`: cudaUserObject_t, count: UInt32): CUDAStatus =
    unsupported
  @CSignature(
    "cudaError_t (cudaGraph_t, const cudaGraphNode_t *, const cudaGraphNode_t *, const cudaGraphEdgeData *, size_t)"
  )
  def cudaGraphRemoveDependencies(
      graph: cudaGraph_t,
      from: Pointer[cudaGraphNode_t, Read, Opaque],
      to: Pointer[cudaGraphNode_t, Read, Opaque],
      edgeData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: SizeT
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraph_t, cudaUserObject_t, unsigned int, unsigned int)")
  def cudaGraphRetainUserObject(
      graph: cudaGraph_t,
      `object`: cudaUserObject_t,
      count: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphExec_t, cudaStream_t)")
  def cudaGraphUpload(graphExec: cudaGraphExec_t, stream: cudaStream_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (int, cudaGraphicsResource_t *, cudaStream_t)")
  def cudaGraphicsMapResources(
      count: Int,
      resources: Pointer[cudaGraphicsResource_t, ReadWrite, Opaque],
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaMipmappedArray_t *, cudaGraphicsResource_t)")
  def cudaGraphicsResourceGetMappedMipmappedArray(
      mipmappedArray: Pointer[cudaMipmappedArray_t, ReadWrite, Opaque],
      resource: cudaGraphicsResource_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, size_t *, cudaGraphicsResource_t)")
  def cudaGraphicsResourceGetMappedPointer(
      devPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      size: Pointer[SizeT, ReadWrite, Opaque],
      resource: cudaGraphicsResource_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphicsResource_t, unsigned int)")
  def cudaGraphicsResourceSetMapFlags(resource: cudaGraphicsResource_t, flags: UInt32): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaArray_t *, cudaGraphicsResource_t, unsigned int, unsigned int)")
  def cudaGraphicsSubResourceGetMappedArray(
      array: Pointer[cudaArray_t, ReadWrite, Opaque],
      resource: cudaGraphicsResource_t,
      arrayIndex: UInt32,
      mipLevel: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int, cudaGraphicsResource_t *, cudaStream_t)")
  def cudaGraphicsUnmapResources(
      count: Int,
      resources: Pointer[cudaGraphicsResource_t, ReadWrite, Opaque],
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaGraphicsResource_t)")
  def cudaGraphicsUnregisterResource(resource: cudaGraphicsResource_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaExecutionContext_t *, cudaDevResourceDesc_t, int, unsigned int)")
  def cudaGreenCtxCreate(
      phCtx: Pointer[cudaExecutionContext_t, ReadWrite, Opaque],
      desc: cudaDevResourceDesc_t,
      device: Int,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, size_t, unsigned int)")
  def cudaHostAlloc(
      pHost: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      size: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, void *, unsigned int)")
  def cudaHostGetDevicePointer(
      pDevice: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      pHost: Pointer[CVoid, ReadWrite, Opaque],
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (unsigned int *, void *)")
  def cudaHostGetFlags(
      pFlags: Pointer[UInt32, ReadWrite, Opaque],
      pHost: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, size_t, unsigned int)")
  def cudaHostRegister(ptr: Pointer[CVoid, ReadWrite, Opaque], size: SizeT, flags: UInt32): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *)")
  def cudaHostUnregister(ptr: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaExternalMemory_t *, const struct cudaExternalMemoryHandleDesc *)")
  def cudaImportExternalMemory(
      extMem_out: Pointer[cudaExternalMemory_t, ReadWrite, Opaque],
      memHandleDesc: Pointer[cudaExternalMemoryHandleDesc, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaExternalSemaphore_t *, const struct cudaExternalSemaphoreHandleDesc *)")
  def cudaImportExternalSemaphore(
      extSem_out: Pointer[cudaExternalSemaphore_t, ReadWrite, Opaque],
      semHandleDesc: Pointer[cudaExternalSemaphoreHandleDesc, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int, unsigned int, unsigned int)")
  def cudaInitDevice(device: Int, deviceFlags: UInt32, flags: UInt32): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *)")
  def cudaIpcCloseMemHandle(devPtr: Pointer[CVoid, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaIpcEventHandle_t *, cudaEvent_t)")
  def cudaIpcGetEventHandle(handle: Pointer[cudaIpcEventHandle_t, ReadWrite, Opaque], event: cudaEvent_t): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaIpcMemHandle_t *, void *)")
  def cudaIpcGetMemHandle(
      handle: Pointer[cudaIpcMemHandle_t, ReadWrite, Opaque],
      devPtr: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaEvent_t *, cudaIpcEventHandle_t)")
  def cudaIpcOpenEventHandle(event: Pointer[cudaEvent_t, ReadWrite, Opaque], handle: cudaIpcEventHandle_t): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (void **, cudaIpcMemHandle_t, unsigned int)")
  def cudaIpcOpenMemHandle(
      devPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      handle: cudaIpcMemHandle_t,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaKernel_t, enum cudaFuncAttribute, int, int)")
  def cudaKernelSetAttributeForDevice(
      kernel: cudaKernel_t,
      attr: cudaFuncAttribute,
      value: Int,
      device: Int
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (const void *, dim3, dim3, void **, size_t, cudaStream_t)")
  def cudaLaunchCooperativeKernel(
      func: Pointer[CVoid, Read, Opaque],
      gridDim: dim3,
      blockDim: dim3,
      args: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      sharedMem: SizeT,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, cudaHostFn_t, void *)")
  def cudaLaunchHostFunc(
      stream: cudaStream_t,
      fn: cudaHostFn_t,
      userData: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, cudaHostFn_t, void *, unsigned int)")
  def cudaLaunchHostFunc_v2(
      stream: cudaStream_t,
      fn: cudaHostFn_t,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      syncMode: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (const void *, dim3, dim3, void **, size_t, cudaStream_t)")
  def cudaLaunchKernel(
      func: Pointer[CVoid, Read, Opaque],
      gridDim: dim3,
      blockDim: dim3,
      args: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      sharedMem: SizeT,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (const cudaLaunchConfig_t *, const void *, void **)")
  def cudaLaunchKernelExC(
      config: Pointer[cudaLaunchConfig_t, Read, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      args: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaKernel_t *, unsigned int, cudaLibrary_t)")
  def cudaLibraryEnumerateKernels(
      kernels: Pointer[cudaKernel_t, ReadWrite, Opaque],
      numKernels: UInt32,
      lib: cudaLibrary_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, size_t *, cudaLibrary_t, const char *)")
  def cudaLibraryGetGlobal(
      dptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      bytes: Pointer[SizeT, ReadWrite, Opaque],
      library: cudaLibrary_t,
      name: CString
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaKernel_t *, cudaLibrary_t, const char *)")
  def cudaLibraryGetKernel(
      pKernel: Pointer[cudaKernel_t, ReadWrite, Opaque],
      library: cudaLibrary_t,
      name: CString
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (unsigned int *, cudaLibrary_t)")
  def cudaLibraryGetKernelCount(count: Pointer[UInt32, ReadWrite, Opaque], lib: cudaLibrary_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, size_t *, cudaLibrary_t, const char *)")
  def cudaLibraryGetManaged(
      dptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      bytes: Pointer[SizeT, ReadWrite, Opaque],
      library: cudaLibrary_t,
      name: CString
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, cudaLibrary_t, const char *)")
  def cudaLibraryGetUnifiedFunction(
      fptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      library: cudaLibrary_t,
      symbol: CString
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaLibrary_t *, const void *, enum cudaJitOption *, void **, unsigned int, enum cudaLibraryOption *, void **, unsigned int)"
  )
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
  @CSignature(
    "cudaError_t (cudaLibrary_t *, const char *, enum cudaJitOption *, void **, unsigned int, enum cudaLibraryOption *, void **, unsigned int)"
  )
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
  @CSignature("cudaError_t (cudaLibrary_t)")
  def cudaLibraryUnload(library: cudaLibrary_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaLogIterator *, unsigned int)")
  def cudaLogsCurrent(iterator_out: Pointer[cudaLogIterator, ReadWrite, Opaque], flags: UInt32): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaLogIterator *, const char *, unsigned int)")
  def cudaLogsDumpToFile(
      iterator: Pointer[cudaLogIterator, ReadWrite, Opaque],
      pathToFile: CString,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaLogIterator *, char *, size_t *, unsigned int)")
  def cudaLogsDumpToMemory(
      iterator: Pointer[cudaLogIterator, ReadWrite, Opaque],
      buffer: Pointer[Byte, ReadWrite, Opaque],
      size: Pointer[SizeT, ReadWrite, Opaque],
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaLogsCallback_t, void *, cudaLogsCallbackHandle *)")
  def cudaLogsRegisterCallback(
      callbackFunc: cudaLogsCallback_t,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      callback_out: Pointer[cudaLogsCallbackHandle, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaLogsCallbackHandle)")
  def cudaLogsUnregisterCallback(callback: cudaLogsCallbackHandle): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, size_t)")
  def cudaMalloc(devPtr: Out[DevicePtr], size: SizeT): CUDAStatus
  @CSignature("cudaError_t (struct cudaPitchedPtr *, struct cudaExtent)")
  def cudaMalloc3D(pitchedDevPtr: Pointer[cudaPitchedPtr, ReadWrite, Opaque], extent: cudaExtent): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaArray_t *, const struct cudaChannelFormatDesc *, struct cudaExtent, unsigned int)")
  def cudaMalloc3DArray(
      array: Pointer[cudaArray_t, ReadWrite, Opaque],
      desc: Pointer[cudaChannelFormatDesc, Read, Opaque],
      extent: cudaExtent,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaArray_t *, const struct cudaChannelFormatDesc *, size_t, size_t, unsigned int)")
  def cudaMallocArray(
      array: Pointer[cudaArray_t, ReadWrite, Opaque],
      desc: Pointer[cudaChannelFormatDesc, Read, Opaque],
      width: SizeT,
      height: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, size_t, cudaStream_t)")
  def cudaMallocAsync(
      devPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      size: SizeT,
      hStream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, size_t, cudaMemPool_t, cudaStream_t)")
  def cudaMallocFromPoolAsync(
      ptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      size: SizeT,
      memPool: cudaMemPool_t,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, size_t)")
  def cudaMallocHost(ptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque], size: SizeT): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (void **, size_t, unsigned int)")
  def cudaMallocManaged(
      devPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      size: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaMipmappedArray_t *, const struct cudaChannelFormatDesc *, struct cudaExtent, unsigned int, unsigned int)"
  )
  def cudaMallocMipmappedArray(
      mipmappedArray: Pointer[cudaMipmappedArray_t, ReadWrite, Opaque],
      desc: Pointer[cudaChannelFormatDesc, Read, Opaque],
      extent: cudaExtent,
      numLevels: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, size_t *, size_t, size_t)")
  def cudaMallocPitch(
      devPtr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      pitch: Pointer[SizeT, ReadWrite, Opaque],
      width: SizeT,
      height: SizeT
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (const void *, size_t, enum cudaMemoryAdvise, struct cudaMemLocation)")
  def cudaMemAdvise(
      devPtr: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      advice: cudaMemoryAdvise,
      location: cudaMemLocation
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (void **, size_t *, size_t, struct cudaMemLocation *, size_t *, size_t, unsigned long long, cudaStream_t)"
  )
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
  @CSignature("cudaError_t (void **, size_t *, size_t, unsigned long long, cudaStream_t)")
  def cudaMemDiscardBatchAsync(
      dptrs: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      sizes: Pointer[SizeT, ReadWrite, Opaque],
      count: SizeT,
      flags: unsigned_long_long,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaMemPool_t *, struct cudaMemLocation *, enum cudaMemAllocationType)")
  def cudaMemGetDefaultMemPool(
      memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque],
      location: Pointer[cudaMemLocation, ReadWrite, Opaque],
      `type`: cudaMemAllocationType
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (size_t *, size_t *)")
  def cudaMemGetInfo(free: Pointer[SizeT, ReadWrite, Opaque], total: Pointer[SizeT, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaMemPool_t *, struct cudaMemLocation *, enum cudaMemAllocationType)")
  def cudaMemGetMemPool(
      memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque],
      location: Pointer[cudaMemLocation, ReadWrite, Opaque],
      `type`: cudaMemAllocationType
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaMemPool_t *, const struct cudaMemPoolProps *)")
  def cudaMemPoolCreate(
      memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque],
      poolProps: Pointer[cudaMemPoolProps, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaMemPool_t)")
  def cudaMemPoolDestroy(memPool: cudaMemPool_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaMemPoolPtrExportData *, void *)")
  def cudaMemPoolExportPointer(
      exportData: Pointer[cudaMemPoolPtrExportData, ReadWrite, Opaque],
      ptr: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, cudaMemPool_t, enum cudaMemAllocationHandleType, unsigned int)")
  def cudaMemPoolExportToShareableHandle(
      shareableHandle: Pointer[CVoid, ReadWrite, Opaque],
      memPool: cudaMemPool_t,
      handleType: cudaMemAllocationHandleType,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (enum cudaMemAccessFlags *, cudaMemPool_t, struct cudaMemLocation *)")
  def cudaMemPoolGetAccess(
      flags: Pointer[cudaMemAccessFlags, ReadWrite, Opaque],
      memPool: cudaMemPool_t,
      location: Pointer[cudaMemLocation, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaMemPool_t, enum cudaMemPoolAttr, void *)")
  def cudaMemPoolGetAttribute(
      memPool: cudaMemPool_t,
      attr: cudaMemPoolAttr,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaMemPool_t *, void *, enum cudaMemAllocationHandleType, unsigned int)")
  def cudaMemPoolImportFromShareableHandle(
      memPool: Pointer[cudaMemPool_t, ReadWrite, Opaque],
      shareableHandle: Pointer[CVoid, ReadWrite, Opaque],
      handleType: cudaMemAllocationHandleType,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, cudaMemPool_t, struct cudaMemPoolPtrExportData *)")
  def cudaMemPoolImportPointer(
      ptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      memPool: cudaMemPool_t,
      exportData: Pointer[cudaMemPoolPtrExportData, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaMemPool_t, const struct cudaMemAccessDesc *, size_t)")
  def cudaMemPoolSetAccess(
      memPool: cudaMemPool_t,
      descList: Pointer[cudaMemAccessDesc, Read, Opaque],
      count: SizeT
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaMemPool_t, enum cudaMemPoolAttr, void *)")
  def cudaMemPoolSetAttribute(
      memPool: cudaMemPool_t,
      attr: cudaMemPoolAttr,
      value: Pointer[CVoid, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaMemPool_t, size_t)")
  def cudaMemPoolTrimTo(memPool: cudaMemPool_t, minBytesToKeep: SizeT): CUDAStatus = unsupported
  @CSignature("cudaError_t (const void *, size_t, struct cudaMemLocation, unsigned int, cudaStream_t)")
  def cudaMemPrefetchAsync(
      devPtr: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      location: cudaMemLocation,
      flags: UInt32,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (void **, size_t *, size_t, struct cudaMemLocation *, size_t *, size_t, unsigned long long, cudaStream_t)"
  )
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
  @CSignature("cudaError_t (void *, size_t, enum cudaMemRangeAttribute, const void *, size_t)")
  def cudaMemRangeGetAttribute(
      data: Pointer[CVoid, ReadWrite, Opaque],
      dataSize: SizeT,
      attribute: cudaMemRangeAttribute,
      devPtr: Pointer[CVoid, Read, Opaque],
      count: SizeT
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void **, size_t *, enum cudaMemRangeAttribute *, size_t, const void *, size_t)")
  def cudaMemRangeGetAttributes(
      data: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque],
      dataSizes: Pointer[SizeT, ReadWrite, Opaque],
      attributes: Pointer[cudaMemRangeAttribute, ReadWrite, Opaque],
      numAttributes: SizeT,
      devPtr: Pointer[CVoid, Read, Opaque],
      count: SizeT
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaMemLocation *, enum cudaMemAllocationType, cudaMemPool_t)")
  def cudaMemSetMemPool(
      location: Pointer[cudaMemLocation, ReadWrite, Opaque],
      `type`: cudaMemAllocationType,
      memPool: cudaMemPool_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, const void *, size_t, enum cudaMemcpyKind)")
  def cudaMemcpy(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, size_t, const void *, size_t, size_t, size_t, enum cudaMemcpyKind)")
  def cudaMemcpy2D(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      dpitch: SizeT,
      src: Pointer[CVoid, Read, Opaque],
      spitch: SizeT,
      width: SizeT,
      height: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaArray_t, size_t, size_t, cudaArray_const_t, size_t, size_t, size_t, size_t, enum cudaMemcpyKind)"
  )
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
  @CSignature("cudaError_t (void *, size_t, const void *, size_t, size_t, size_t, enum cudaMemcpyKind, cudaStream_t)")
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
  @CSignature("cudaError_t (void *, size_t, cudaArray_const_t, size_t, size_t, size_t, size_t, enum cudaMemcpyKind)")
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
  @CSignature(
    "cudaError_t (void *, size_t, cudaArray_const_t, size_t, size_t, size_t, size_t, enum cudaMemcpyKind, cudaStream_t)"
  )
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
  @CSignature("cudaError_t (cudaArray_t, size_t, size_t, const void *, size_t, size_t, size_t, enum cudaMemcpyKind)")
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
  @CSignature(
    "cudaError_t (cudaArray_t, size_t, size_t, const void *, size_t, size_t, size_t, enum cudaMemcpyKind, cudaStream_t)"
  )
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
  @CSignature("cudaError_t (const struct cudaMemcpy3DParms *)")
  def cudaMemcpy3D(p: Pointer[cudaMemcpy3DParms, Read, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (const struct cudaMemcpy3DParms *, cudaStream_t)")
  def cudaMemcpy3DAsync(p: Pointer[cudaMemcpy3DParms, Read, Opaque], stream: cudaStream_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (size_t, struct cudaMemcpy3DBatchOp *, unsigned long long, cudaStream_t)")
  def cudaMemcpy3DBatchAsync(
      numOps: SizeT,
      opList: Pointer[cudaMemcpy3DBatchOp, ReadWrite, Opaque],
      flags: unsigned_long_long,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (const struct cudaMemcpy3DPeerParms *)")
  def cudaMemcpy3DPeer(p: Pointer[cudaMemcpy3DPeerParms, Read, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (const struct cudaMemcpy3DPeerParms *, cudaStream_t)")
  def cudaMemcpy3DPeerAsync(p: Pointer[cudaMemcpy3DPeerParms, Read, Opaque], stream: cudaStream_t): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (struct cudaMemcpy3DBatchOp *, unsigned long long, cudaStream_t)")
  def cudaMemcpy3DWithAttributesAsync(
      op: Pointer[cudaMemcpy3DBatchOp, ReadWrite, Opaque],
      flags: unsigned_long_long,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaArray_t, size_t, size_t, cudaArray_const_t, size_t, size_t, size_t, enum cudaMemcpyKind)"
  )
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
  @CSignature("cudaError_t (void *, const void *, size_t, enum cudaMemcpyKind, cudaStream_t)")
  def cudaMemcpyAsync(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      kind: cudaMemcpyKind,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (void *const *, const void *const *, const size_t *, size_t, struct cudaMemcpyAttributes *, size_t *, size_t, cudaStream_t)"
  )
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
  @CSignature("cudaError_t (void *, cudaArray_const_t, size_t, size_t, size_t, enum cudaMemcpyKind)")
  def cudaMemcpyFromArray(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: cudaArray_const_t,
      wOffset: SizeT,
      hOffset: SizeT,
      count: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, cudaArray_const_t, size_t, size_t, size_t, enum cudaMemcpyKind, cudaStream_t)")
  def cudaMemcpyFromArrayAsync(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: cudaArray_const_t,
      wOffset: SizeT,
      hOffset: SizeT,
      count: SizeT,
      kind: cudaMemcpyKind,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, const void *, size_t, size_t, enum cudaMemcpyKind)")
  def cudaMemcpyFromSymbol(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      symbol: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, const void *, size_t, size_t, enum cudaMemcpyKind, cudaStream_t)")
  def cudaMemcpyFromSymbolAsync(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      symbol: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, int, const void *, int, size_t)")
  def cudaMemcpyPeer(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      dstDevice: Int,
      src: Pointer[CVoid, Read, Opaque],
      srcDevice: Int,
      count: SizeT
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, int, const void *, int, size_t, cudaStream_t)")
  def cudaMemcpyPeerAsync(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      dstDevice: Int,
      src: Pointer[CVoid, Read, Opaque],
      srcDevice: Int,
      count: SizeT,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaArray_t, size_t, size_t, const void *, size_t, enum cudaMemcpyKind)")
  def cudaMemcpyToArray(
      dst: cudaArray_t,
      wOffset: SizeT,
      hOffset: SizeT,
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaArray_t, size_t, size_t, const void *, size_t, enum cudaMemcpyKind, cudaStream_t)")
  def cudaMemcpyToArrayAsync(
      dst: cudaArray_t,
      wOffset: SizeT,
      hOffset: SizeT,
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      kind: cudaMemcpyKind,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (const void *, const void *, size_t, size_t, enum cudaMemcpyKind)")
  def cudaMemcpyToSymbol(
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (const void *, const void *, size_t, size_t, enum cudaMemcpyKind, cudaStream_t)")
  def cudaMemcpyToSymbolAsync(
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: SizeT,
      offset: SizeT,
      kind: cudaMemcpyKind,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, const void *, size_t, struct cudaMemcpyAttributes *, cudaStream_t)")
  def cudaMemcpyWithAttributesAsync(
      dst: Pointer[CVoid, ReadWrite, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      size: SizeT,
      attr: Pointer[cudaMemcpyAttributes, ReadWrite, Opaque],
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, int, size_t)")
  def cudaMemset(devPtr: Pointer[CVoid, ReadWrite, Opaque], value: Int, count: SizeT): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, size_t, int, size_t, size_t)")
  def cudaMemset2D(
      devPtr: Pointer[CVoid, ReadWrite, Opaque],
      pitch: SizeT,
      value: Int,
      width: SizeT,
      height: SizeT
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, size_t, int, size_t, size_t, cudaStream_t)")
  def cudaMemset2DAsync(
      devPtr: Pointer[CVoid, ReadWrite, Opaque],
      pitch: SizeT,
      value: Int,
      width: SizeT,
      height: SizeT,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaPitchedPtr, int, struct cudaExtent)")
  def cudaMemset3D(pitchedDevPtr: cudaPitchedPtr, value: Int, extent: cudaExtent): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaPitchedPtr, int, struct cudaExtent, cudaStream_t)")
  def cudaMemset3DAsync(
      pitchedDevPtr: cudaPitchedPtr,
      value: Int,
      extent: cudaExtent,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void *, int, size_t, cudaStream_t)")
  def cudaMemsetAsync(
      devPtr: Pointer[CVoid, ReadWrite, Opaque],
      value: Int,
      count: SizeT,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaArrayMemoryRequirements *, cudaMipmappedArray_t, int)")
  def cudaMipmappedArrayGetMemoryRequirements(
      memoryRequirements: Pointer[cudaArrayMemoryRequirements, ReadWrite, Opaque],
      mipmap: cudaMipmappedArray_t,
      device: Int
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaArraySparseProperties *, cudaMipmappedArray_t)")
  def cudaMipmappedArrayGetSparseProperties(
      sparseProperties: Pointer[cudaArraySparseProperties, ReadWrite, Opaque],
      mipmap: cudaMipmappedArray_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (size_t *, const void *, int, int)")
  def cudaOccupancyAvailableDynamicSMemPerBlock(
      dynamicSmemSize: Pointer[SizeT, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      numBlocks: Int,
      blockSize: Int
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *, const void *, int, size_t)")
  def cudaOccupancyMaxActiveBlocksPerMultiprocessor(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      blockSize: Int,
      dynamicSMemSize: SizeT
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *, const void *, int, size_t, unsigned int)")
  def cudaOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      blockSize: Int,
      dynamicSMemSize: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *, const void *, const cudaLaunchConfig_t *)")
  def cudaOccupancyMaxActiveClusters(
      numClusters: Pointer[Int, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      launchConfig: Pointer[cudaLaunchConfig_t, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *, const void *, const cudaLaunchConfig_t *)")
  def cudaOccupancyMaxPotentialClusterSize(
      clusterSize: Pointer[Int, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque],
      launchConfig: Pointer[cudaLaunchConfig_t, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (void)")
  def cudaPeekAtLastError(): CUDAStatus = unsupported
  @CSignature("cudaError_t (struct cudaPointerAttributes *, const void *)")
  def cudaPointerGetAttributes(
      attributes: Pointer[cudaPointerAttributes, ReadWrite, Opaque],
      ptr: Pointer[CVoid, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *)")
  def cudaRuntimeGetVersion(runtimeVersion: Pointer[Int, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (int)")
  def cudaSetDevice(device: Int): CUDAStatus = unsupported
  @CSignature("cudaError_t (unsigned int)")
  def cudaSetDeviceFlags(flags: UInt32): CUDAStatus = unsupported
  @CSignature("cudaError_t (int *, int)")
  def cudaSetValidDevices(device_arr: Pointer[Int, ReadWrite, Opaque], len: Int): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (const cudaExternalSemaphore_t *, const struct cudaExternalSemaphoreSignalParams *, unsigned int, cudaStream_t)"
  )
  def cudaSignalExternalSemaphoresAsync(
      extSemArray: Pointer[cudaExternalSemaphore_t, Read, Opaque],
      paramsArray: Pointer[cudaExternalSemaphoreSignalParams, Read, Opaque],
      numExtSems: UInt32,
      stream: cudaStream_t
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, cudaStreamCallback_t, void *, unsigned int)")
  def cudaStreamAddCallback(
      stream: cudaStream_t,
      callback: cudaStreamCallback_t,
      userData: Pointer[CVoid, ReadWrite, Opaque],
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, void *, size_t, unsigned int)")
  def cudaStreamAttachMemAsync(
      stream: cudaStream_t,
      devPtr: Pointer[CVoid, ReadWrite, Opaque],
      length: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, enum cudaStreamCaptureMode)")
  def cudaStreamBeginCapture(stream: cudaStream_t, mode: cudaStreamCaptureMode): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaStream_t, cudaGraph_t, const cudaGraphNode_t *, const cudaGraphEdgeData *, size_t, enum cudaStreamCaptureMode)"
  )
  def cudaStreamBeginCaptureToGraph(
      stream: cudaStream_t,
      graph: cudaGraph_t,
      dependencies: Pointer[cudaGraphNode_t, Read, Opaque],
      dependencyData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: SizeT,
      mode: cudaStreamCaptureMode
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaStream_t, enum cudaStreamCaptureMode, cudaGraph_t, struct cudaGraphRecaptureCallbackData *)"
  )
  def cudaStreamBeginRecaptureToGraph(
      stream: cudaStream_t,
      mode: cudaStreamCaptureMode,
      graph: cudaGraph_t,
      callbackData: Pointer[cudaGraphRecaptureCallbackData, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, cudaStream_t)")
  def cudaStreamCopyAttributes(dst: cudaStream_t, src: cudaStream_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t *)")
  def cudaStreamCreate(pStream: Pointer[cudaStream_t, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t *, unsigned int)")
  def cudaStreamCreateWithFlags(pStream: Pointer[cudaStream_t, ReadWrite, Opaque], flags: UInt32): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaStream_t *, unsigned int, int)")
  def cudaStreamCreateWithPriority(
      pStream: Pointer[cudaStream_t, ReadWrite, Opaque],
      flags: UInt32,
      priority: Int
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t)")
  def cudaStreamDestroy(stream: cudaStream_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, cudaGraph_t *)")
  def cudaStreamEndCapture(stream: cudaStream_t, pGraph: Pointer[cudaGraph_t, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaStream_t, cudaLaunchAttributeID, cudaLaunchAttributeValue *)")
  def cudaStreamGetAttribute(
      hStream: cudaStream_t,
      attr: cudaLaunchAttributeID,
      value_out: Pointer[cudaLaunchAttributeValue, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (cudaStream_t, enum cudaStreamCaptureStatus *, unsigned long long *, cudaGraph_t *, const cudaGraphNode_t **, const cudaGraphEdgeData **, size_t *)"
  )
  def cudaStreamGetCaptureInfo(
      stream: cudaStream_t,
      captureStatus_out: Pointer[cudaStreamCaptureStatus, ReadWrite, Opaque],
      id_out: Pointer[unsigned_long_long, ReadWrite, Opaque],
      graph_out: Pointer[cudaGraph_t, ReadWrite, Opaque],
      dependencies_out: Pointer[Pointer[cudaGraphNode_t, ReadWrite, Opaque], Read, Opaque],
      edgeData_out: Pointer[Pointer[cudaGraphEdgeData, ReadWrite, Opaque], Read, Opaque],
      numDependencies_out: Pointer[SizeT, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, cudaDevResource *, enum cudaDevResourceType)")
  def cudaStreamGetDevResource(
      hStream: cudaStream_t,
      resource: Pointer[cudaDevResource, ReadWrite, Opaque],
      `type`: cudaDevResourceType
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, int *)")
  def cudaStreamGetDevice(hStream: cudaStream_t, device: Pointer[Int, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, unsigned int *)")
  def cudaStreamGetFlags(hStream: cudaStream_t, flags: Pointer[UInt32, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, unsigned long long *)")
  def cudaStreamGetId(hStream: cudaStream_t, streamId: Pointer[unsigned_long_long, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaStream_t, int *)")
  def cudaStreamGetPriority(hStream: cudaStream_t, priority: Pointer[Int, ReadWrite, Opaque]): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, enum cudaStreamCaptureStatus *)")
  def cudaStreamIsCapturing(
      stream: cudaStream_t,
      pCaptureStatus: Pointer[cudaStreamCaptureStatus, ReadWrite, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t)")
  def cudaStreamQuery(stream: cudaStream_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, cudaLaunchAttributeID, const cudaLaunchAttributeValue *)")
  def cudaStreamSetAttribute(
      hStream: cudaStream_t,
      attr: cudaLaunchAttributeID,
      value: Pointer[cudaLaunchAttributeValue, Read, Opaque]
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t)")
  def cudaStreamSynchronize(stream: cudaStream_t): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, cudaGraphNode_t *, const cudaGraphEdgeData *, size_t, unsigned int)")
  def cudaStreamUpdateCaptureDependencies(
      stream: cudaStream_t,
      dependencies: Pointer[cudaGraphNode_t, ReadWrite, Opaque],
      dependencyData: Pointer[cudaGraphEdgeData, Read, Opaque],
      numDependencies: SizeT,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaStream_t, cudaEvent_t, unsigned int)")
  def cudaStreamWaitEvent(stream: cudaStream_t, event: cudaEvent_t, flags: UInt32): CUDAStatus = unsupported
  @CSignature("cudaError_t (enum cudaStreamCaptureMode *)")
  def cudaThreadExchangeStreamCaptureMode(mode: Pointer[cudaStreamCaptureMode, ReadWrite, Opaque]): CUDAStatus =
    unsupported
  @CSignature("cudaError_t (cudaUserObject_t *, void *, cudaHostFn_t, unsigned int, unsigned int)")
  def cudaUserObjectCreate(
      object_out: Pointer[cudaUserObject_t, ReadWrite, Opaque],
      ptr: Pointer[CVoid, ReadWrite, Opaque],
      destroy: cudaHostFn_t,
      initialRefcount: UInt32,
      flags: UInt32
  ): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaUserObject_t, unsigned int)")
  def cudaUserObjectRelease(`object`: cudaUserObject_t, count: UInt32): CUDAStatus = unsupported
  @CSignature("cudaError_t (cudaUserObject_t, unsigned int)")
  def cudaUserObjectRetain(`object`: cudaUserObject_t, count: UInt32): CUDAStatus = unsupported
  @CSignature(
    "cudaError_t (const cudaExternalSemaphore_t *, const struct cudaExternalSemaphoreWaitParams *, unsigned int, cudaStream_t)"
  )
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

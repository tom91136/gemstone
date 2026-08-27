// Generated from pinned official headers by GenerateAPISurfaces. Do not edit mechanically.
package net.kurobako.gemstone.hip

import net.kurobako.gemstone.abi.*

type HIP_ARRAY3D_DESCRIPTOR               = CValue["HIP_ARRAY3D_DESCRIPTOR"]
type HIP_ARRAY_DESCRIPTOR                 = CValue["HIP_ARRAY_DESCRIPTOR"]
type HIP_LAUNCH_CONFIG                    = CValue["HIP_LAUNCH_CONFIG"]
type HIP_MEMCPY3D                         = CValue["HIP_MEMCPY3D"]
type HIP_RESOURCE_DESC                    = CValue["HIP_RESOURCE_DESC"]
type HIP_RESOURCE_VIEW_DESC               = CValue["HIP_RESOURCE_VIEW_DESC"]
type HIP_TEXTURE_DESC                     = CValue["HIP_TEXTURE_DESC"]
type const_hipFunction_t                  = CValue["const hipFunction_t"]
type dim3                                 = CValue["dim3"]
type hipArrayMapInfo                      = CValue["hipArrayMapInfo"]
type hipArray_Format                      = CValue["hipArray_Format"]
type hipArray_const_t                     = CValue["hipArray_const_t"]
type hipArray_t                           = CValue["hipArray_t"]
type hipBatchMemOpNodeParams              = CValue["hipBatchMemOpNodeParams"]
type hipChannelFormatDesc                 = CValue["hipChannelFormatDesc"]
type hipChannelFormatKind                 = CValue["enum hipChannelFormatKind"]
type hipCtx_t                             = CValue["hipCtx_t"]
type hipDeviceAttribute_t                 = CValue["hipDeviceAttribute_t"]
type hipDeviceP2PAttr                     = CValue["hipDeviceP2PAttr"]
type hipDeviceProp_tR0600                 = CValue["hipDeviceProp_tR0600"]
type hipDevice_t                          = CValue["hipDevice_t"]
type hipDeviceptr_t                       = CValue["hipDeviceptr_t"]
type hipDriverEntryPointQueryResult       = CValue["hipDriverEntryPointQueryResult"]
type hipDriverProcAddressQueryResult      = CValue["hipDriverProcAddressQueryResult"]
type hipError_t                           = CValue["hipError_t"]
type hipEvent_t                           = CValue["hipEvent_t"]
type hipExtent                            = CValue["hipExtent"]
type hipExternalMemoryBufferDesc          = CValue["hipExternalMemoryBufferDesc"]
type hipExternalMemoryHandleDesc          = CValue["hipExternalMemoryHandleDesc"]
type hipExternalMemoryMipmappedArrayDesc  = CValue["hipExternalMemoryMipmappedArrayDesc"]
type hipExternalMemory_t                  = CValue["hipExternalMemory_t"]
type hipExternalSemaphoreHandleDesc       = CValue["hipExternalSemaphoreHandleDesc"]
type hipExternalSemaphoreSignalNodeParams = CValue["hipExternalSemaphoreSignalNodeParams"]
type hipExternalSemaphoreSignalParams     = CValue["hipExternalSemaphoreSignalParams"]
type hipExternalSemaphoreWaitNodeParams   = CValue["hipExternalSemaphoreWaitNodeParams"]
type hipExternalSemaphoreWaitParams       = CValue["hipExternalSemaphoreWaitParams"]
type hipExternalSemaphore_t               = CValue["hipExternalSemaphore_t"]
type hipFuncAttribute                     = CValue["hipFuncAttribute"]
type hipFuncAttributes                    = CValue["struct hipFuncAttributes"]
type hipFuncCache_t                       = CValue["hipFuncCache_t"]
type hipFunctionLaunchParams              = CValue["hipFunctionLaunchParams"]
type hipFunction_attribute                = CValue["hipFunction_attribute"]
type hipFunction_t                        = CValue["hipFunction_t"]
type hipGraphEdgeData                     = CValue["hipGraphEdgeData"]
type hipGraphExecUpdateResult             = CValue["hipGraphExecUpdateResult"]
type hipGraphExec_t                       = CValue["hipGraphExec_t"]
type hipGraphInstantiateParams            = CValue["hipGraphInstantiateParams"]
type hipGraphMemAttributeType             = CValue["hipGraphMemAttributeType"]
type hipGraphNodeParams                   = CValue["hipGraphNodeParams"]
type hipGraphNodeType                     = CValue["hipGraphNodeType"]
type hipGraphNode_t                       = CValue["hipGraphNode_t"]
type hipGraph_t                           = CValue["hipGraph_t"]
type hipGraphicsResource_t                = CValue["hipGraphicsResource_t"]
type hipHostFn_t                          = CValue["hipHostFn_t"]
type hipHostNodeParams                    = CValue["hipHostNodeParams"]
type hipIpcEventHandle_t                  = CValue["hipIpcEventHandle_t"]
type hipIpcMemHandle_t                    = CValue["hipIpcMemHandle_t"]
type hipJitInputType                      = CValue["hipJitInputType"]
type hipJitOption                         = CValue["hipJitOption"]
type hipKernelNodeParams                  = CValue["hipKernelNodeParams"]
type hipKernel_t                          = CValue["hipKernel_t"]
type hipLaunchAttributeID                 = CValue["hipLaunchAttributeID"]
type hipLaunchAttributeValue              = CValue["hipLaunchAttributeValue"]
type hipLaunchConfig_t                    = CValue["hipLaunchConfig_t"]
type hipLaunchParams                      = CValue["hipLaunchParams"]
type hipLibraryOption                     = CValue["hipLibraryOption"]
type hipLibrary_t                         = CValue["hipLibrary_t"]
type hipLimit_t                           = CValue["enum hipLimit_t"]
type hipLinkState_t                       = CValue["hipLinkState_t"]
type hipMemAccessDesc                     = CValue["hipMemAccessDesc"]
type hipMemAccessFlags                    = CValue["hipMemAccessFlags"]
type hipMemAllocNodeParams                = CValue["hipMemAllocNodeParams"]
type hipMemAllocationGranularity_flags    = CValue["hipMemAllocationGranularity_flags"]
type hipMemAllocationHandleType           = CValue["hipMemAllocationHandleType"]
type hipMemAllocationProp                 = CValue["hipMemAllocationProp"]
type hipMemGenericAllocationHandle_t      = CValue["hipMemGenericAllocationHandle_t"]
type hipMemLocation                       = CValue["hipMemLocation"]
type hipMemPoolAttr                       = CValue["hipMemPoolAttr"]
type hipMemPoolProps                      = CValue["hipMemPoolProps"]
type hipMemPoolPtrExportData              = CValue["hipMemPoolPtrExportData"]
type hipMemPool_t                         = CValue["hipMemPool_t"]
type hipMemRangeAttribute                 = CValue["hipMemRangeAttribute"]
type hipMemRangeHandleType                = CValue["hipMemRangeHandleType"]
type hipMemcpy3DBatchOp                   = CValue["struct hipMemcpy3DBatchOp"]
type hipMemcpy3DParms                     = CValue["hipMemcpy3DParms"]
type hipMemcpy3DPeerParms                 = CValue["hipMemcpy3DPeerParms"]
type hipMemcpyAttributes                  = CValue["hipMemcpyAttributes"]
type hipMemcpyKind                        = CValue["hipMemcpyKind"]
type hipMemoryAdvise                      = CValue["hipMemoryAdvise"]
type hipMemsetParams                      = CValue["hipMemsetParams"]
type hipMipmappedArray                    = CValue["struct hipMipmappedArray"]
type hipMipmappedArray_const_t            = CValue["hipMipmappedArray_const_t"]
type hipMipmappedArray_t                  = CValue["hipMipmappedArray_t"]
type hipModule_t                          = CValue["hipModule_t"]
type hipPitchedPtr                        = CValue["hipPitchedPtr"]
type hipPointerAttribute_t                = CValue["hipPointerAttribute_t"]
type hipPointer_attribute                 = CValue["hipPointer_attribute"]
type hipResourceDesc                      = CValue["hipResourceDesc"]
type hipResourceViewDesc                  = CValue["struct hipResourceViewDesc"]
type hipSharedMemConfig                   = CValue["hipSharedMemConfig"]
type hipStreamBatchMemOpParams            = CValue["hipStreamBatchMemOpParams"]
type hipStreamCallback_t                  = CValue["hipStreamCallback_t"]
type hipStreamCaptureMode                 = CValue["hipStreamCaptureMode"]
type hipStreamCaptureStatus               = CValue["hipStreamCaptureStatus"]
type hipStream_t                          = CValue["hipStream_t"]
type hipSurfaceObject_t                   = CValue["hipSurfaceObject_t"]
type hipTextureAddressMode                = CValue["enum hipTextureAddressMode"]
type hipTextureDesc                       = CValue["hipTextureDesc"]
type hipTextureFilterMode                 = CValue["enum hipTextureFilterMode"]
type hipTextureObject_t                   = CValue["hipTextureObject_t"]
type hipUUID                              = CValue["hipUUID"]
type hipUserObject_t                      = CValue["hipUserObject_t"]
type hip_Memcpy2D                         = CValue["hip_Memcpy2D"]
type size_t                               = CValue["size_t"]
type textureReference                     = CValue["textureReference"]
type uint32_t                             = CValue["uint32_t"]
type uint64_t                             = CValue["uint64_t"]
type unsigned_int                         = CValue["unsigned int"]
type unsigned_long_long                   = CValue["unsigned long long"]
type unsigned_short                       = CValue["unsigned short"]

trait HIP {
  @CSignature("const char *(uint32_t)")
  def hipApiName(id: uint32_t): CString = unsupported
  @CSignature("hipError_t (hipArray_t *, const HIP_ARRAY3D_DESCRIPTOR *)")
  def hipArray3DCreate(
      array: Pointer[hipArray_t, ReadWrite, Opaque],
      pAllocateArray: Pointer[HIP_ARRAY3D_DESCRIPTOR, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (HIP_ARRAY3D_DESCRIPTOR *, hipArray_t)")
  def hipArray3DGetDescriptor(
      pArrayDescriptor: Pointer[HIP_ARRAY3D_DESCRIPTOR, ReadWrite, Opaque],
      array: hipArray_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t *, const HIP_ARRAY_DESCRIPTOR *)")
  def hipArrayCreate(
      pHandle: Pointer[hipArray_t, ReadWrite, Opaque],
      pAllocateArray: Pointer[HIP_ARRAY_DESCRIPTOR, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t)")
  def hipArrayDestroy(array: hipArray_t): hipError_t = unsupported
  @CSignature("hipError_t (HIP_ARRAY_DESCRIPTOR *, hipArray_t)")
  def hipArrayGetDescriptor(
      pArrayDescriptor: Pointer[HIP_ARRAY_DESCRIPTOR, ReadWrite, Opaque],
      array: hipArray_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipChannelFormatDesc *, hipExtent *, unsigned int *, hipArray_t)")
  def hipArrayGetInfo(
      desc: Pointer[hipChannelFormatDesc, ReadWrite, Opaque],
      extent: Pointer[hipExtent, ReadWrite, Opaque],
      flags: Pointer[unsigned_int, ReadWrite, Opaque],
      array: hipArray_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (size_t *, const textureReference *, const void *, const hipChannelFormatDesc *, size_t)")
  def hipBindTexture(
      offset: Pointer[size_t, ReadWrite, Opaque],
      tex: Pointer[textureReference, Read, Opaque],
      devPtr: Pointer[Unit, Read, Opaque],
      desc: Pointer[hipChannelFormatDesc, Read, Opaque],
      size: size_t
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (size_t *, const textureReference *, const void *, const hipChannelFormatDesc *, size_t, size_t, size_t)"
  )
  def hipBindTexture2D(
      offset: Pointer[size_t, ReadWrite, Opaque],
      tex: Pointer[textureReference, Read, Opaque],
      devPtr: Pointer[Unit, Read, Opaque],
      desc: Pointer[hipChannelFormatDesc, Read, Opaque],
      width: size_t,
      height: size_t,
      pitch: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (const textureReference *, hipArray_const_t, const hipChannelFormatDesc *)")
  def hipBindTextureToArray(
      tex: Pointer[textureReference, Read, Opaque],
      array: hipArray_const_t,
      desc: Pointer[hipChannelFormatDesc, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (const textureReference *, hipMipmappedArray_const_t, const hipChannelFormatDesc *)")
  def hipBindTextureToMipmappedArray(
      tex: Pointer[textureReference, Read, Opaque],
      mipmappedArray: hipMipmappedArray_const_t,
      desc: Pointer[hipChannelFormatDesc, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (int *, const hipDeviceProp_tR0600 *)")
  def hipChooseDeviceR0600(
      device: Pointer[Int, ReadWrite, Opaque],
      prop: Pointer[hipDeviceProp_tR0600, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (dim3, dim3, size_t, hipStream_t)")
  def hipConfigureCall(gridDim: dim3, blockDim: dim3, sharedMem: size_t, stream: hipStream_t): hipError_t = unsupported
  @CSignature("struct hipChannelFormatDesc (int, int, int, int, enum hipChannelFormatKind)")
  def hipCreateChannelDesc(x: Int, y: Int, z: Int, w: Int, f: hipChannelFormatKind): hipChannelFormatDesc = unsupported
  @CSignature("hipError_t (hipSurfaceObject_t *, const hipResourceDesc *)")
  def hipCreateSurfaceObject(
      pSurfObject: Pointer[hipSurfaceObject_t, ReadWrite, Opaque],
      pResDesc: Pointer[hipResourceDesc, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipTextureObject_t *, const hipResourceDesc *, const hipTextureDesc *, const struct hipResourceViewDesc *)"
  )
  def hipCreateTextureObject(
      pTexObject: Pointer[hipTextureObject_t, ReadWrite, Opaque],
      pResDesc: Pointer[hipResourceDesc, Read, Opaque],
      pTexDesc: Pointer[hipTextureDesc, Read, Opaque],
      pResViewDesc: Pointer[hipResourceViewDesc, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipCtx_t *, unsigned int, hipDevice_t)")
  def hipCtxCreate(ctx: Pointer[hipCtx_t, ReadWrite, Opaque], flags: unsigned_int, device: hipDevice_t): hipError_t =
    unsupported
  @CSignature("hipError_t (hipCtx_t)")
  def hipCtxDestroy(ctx: hipCtx_t): hipError_t = unsupported
  @CSignature("hipError_t (hipCtx_t)")
  def hipCtxDisablePeerAccess(peerCtx: hipCtx_t): hipError_t = unsupported
  @CSignature("hipError_t (hipCtx_t, unsigned int)")
  def hipCtxEnablePeerAccess(peerCtx: hipCtx_t, flags: unsigned_int): hipError_t = unsupported
  @CSignature("hipError_t (hipCtx_t, unsigned int *)")
  def hipCtxGetApiVersion(ctx: hipCtx_t, apiVersion: Pointer[unsigned_int, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipFuncCache_t *)")
  def hipCtxGetCacheConfig(cacheConfig: Pointer[hipFuncCache_t, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipCtx_t *)")
  def hipCtxGetCurrent(ctx: Pointer[hipCtx_t, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipDevice_t *)")
  def hipCtxGetDevice(device: Pointer[hipDevice_t, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (unsigned int *)")
  def hipCtxGetFlags(flags: Pointer[unsigned_int, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipSharedMemConfig *)")
  def hipCtxGetSharedMemConfig(pConfig: Pointer[hipSharedMemConfig, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipCtx_t *)")
  def hipCtxPopCurrent(ctx: Pointer[hipCtx_t, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipCtx_t)")
  def hipCtxPushCurrent(ctx: hipCtx_t): hipError_t = unsupported
  @CSignature("hipError_t (hipFuncCache_t)")
  def hipCtxSetCacheConfig(cacheConfig: hipFuncCache_t): hipError_t = unsupported
  @CSignature("hipError_t (hipCtx_t)")
  def hipCtxSetCurrent(ctx: hipCtx_t): hipError_t = unsupported
  @CSignature("hipError_t (hipSharedMemConfig)")
  def hipCtxSetSharedMemConfig(config: hipSharedMemConfig): hipError_t = unsupported
  @CSignature("hipError_t (void)")
  def hipCtxSynchronize(): hipError_t = unsupported
  @CSignature("hipError_t (hipExternalMemory_t)")
  def hipDestroyExternalMemory(extMem: hipExternalMemory_t): hipError_t = unsupported
  @CSignature("hipError_t (hipExternalSemaphore_t)")
  def hipDestroyExternalSemaphore(extSem: hipExternalSemaphore_t): hipError_t = unsupported
  @CSignature("hipError_t (hipSurfaceObject_t)")
  def hipDestroySurfaceObject(surfaceObject: hipSurfaceObject_t): hipError_t = unsupported
  @CSignature("hipError_t (hipTextureObject_t)")
  def hipDestroyTextureObject(textureObject: hipTextureObject_t): hipError_t = unsupported
  @CSignature("hipError_t (int *, int, int)")
  def hipDeviceCanAccessPeer(
      canAccessPeer: Pointer[Int, ReadWrite, Opaque],
      deviceId: Int,
      peerDeviceId: Int
  ): hipError_t = unsupported
  @CSignature("hipError_t (int *, int *, hipDevice_t)")
  def hipDeviceComputeCapability(
      major: Pointer[Int, ReadWrite, Opaque],
      minor: Pointer[Int, ReadWrite, Opaque],
      device: hipDevice_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (int)")
  def hipDeviceDisablePeerAccess(peerDeviceId: Int): hipError_t = unsupported
  @CSignature("hipError_t (int, unsigned int)")
  def hipDeviceEnablePeerAccess(peerDeviceId: Int, flags: unsigned_int): hipError_t = unsupported
  @CSignature("hipError_t (hipDevice_t *, int)")
  def hipDeviceGet(device: Pointer[hipDevice_t, ReadWrite, Opaque], ordinal: Int): hipError_t = unsupported
  @CSignature("hipError_t (int *, hipDeviceAttribute_t, int)")
  def hipDeviceGetAttribute(
      pi: Pointer[Int, ReadWrite, Opaque],
      attr: hipDeviceAttribute_t,
      deviceId: Int
  ): hipError_t = unsupported
  @CSignature("hipError_t (int *, const char *)")
  def hipDeviceGetByPCIBusId(device: Pointer[Int, ReadWrite, Opaque], pciBusId: CString): hipError_t = unsupported
  @CSignature("hipError_t (hipFuncCache_t *)")
  def hipDeviceGetCacheConfig(cacheConfig: Pointer[hipFuncCache_t, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipMemPool_t *, int)")
  def hipDeviceGetDefaultMemPool(mem_pool: Pointer[hipMemPool_t, ReadWrite, Opaque], device: Int): hipError_t =
    unsupported
  @CSignature("hipError_t (int, hipGraphMemAttributeType, void *)")
  def hipDeviceGetGraphMemAttribute(
      device: Int,
      attr: hipGraphMemAttributeType,
      value: Pointer[Unit, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (size_t *, enum hipLimit_t)")
  def hipDeviceGetLimit(pValue: Pointer[size_t, ReadWrite, Opaque], limit: hipLimit_t): hipError_t = unsupported
  @CSignature("hipError_t (hipMemPool_t *, int)")
  def hipDeviceGetMemPool(mem_pool: Pointer[hipMemPool_t, ReadWrite, Opaque], device: Int): hipError_t = unsupported
  @CSignature("hipError_t (char *, int, hipDevice_t)")
  def hipDeviceGetName(name: Pointer[Byte, ReadWrite, Opaque], len: Int, device: hipDevice_t): hipError_t = unsupported
  @CSignature("hipError_t (int *, hipDeviceP2PAttr, int, int)")
  def hipDeviceGetP2PAttribute(
      value: Pointer[Int, ReadWrite, Opaque],
      attr: hipDeviceP2PAttr,
      srcDevice: Int,
      dstDevice: Int
  ): hipError_t = unsupported
  @CSignature("hipError_t (char *, int, int)")
  def hipDeviceGetPCIBusId(pciBusId: Pointer[Byte, ReadWrite, Opaque], len: Int, device: Int): hipError_t = unsupported
  @CSignature("hipError_t (hipSharedMemConfig *)")
  def hipDeviceGetSharedMemConfig(pConfig: Pointer[hipSharedMemConfig, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (int *, int *)")
  def hipDeviceGetStreamPriorityRange(
      leastPriority: Pointer[Int, ReadWrite, Opaque],
      greatestPriority: Pointer[Int, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (size_t *, const hipChannelFormatDesc *, int)")
  def hipDeviceGetTexture1DLinearMaxWidth(
      max_width: Pointer[size_t, ReadWrite, Opaque],
      desc: Pointer[hipChannelFormatDesc, Read, Opaque],
      device: Int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipUUID *, hipDevice_t)")
  def hipDeviceGetUuid(uuid: Pointer[hipUUID, ReadWrite, Opaque], device: hipDevice_t): hipError_t = unsupported
  @CSignature("hipError_t (int)")
  def hipDeviceGraphMemTrim(device: Int): hipError_t = unsupported
  @CSignature("hipError_t (hipDevice_t, unsigned int *, int *)")
  def hipDevicePrimaryCtxGetState(
      dev: hipDevice_t,
      flags: Pointer[unsigned_int, ReadWrite, Opaque],
      active: Pointer[Int, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipDevice_t)")
  def hipDevicePrimaryCtxRelease(dev: hipDevice_t): hipError_t = unsupported
  @CSignature("hipError_t (hipDevice_t)")
  def hipDevicePrimaryCtxReset(dev: hipDevice_t): hipError_t = unsupported
  @CSignature("hipError_t (hipCtx_t *, hipDevice_t)")
  def hipDevicePrimaryCtxRetain(pctx: Pointer[hipCtx_t, ReadWrite, Opaque], dev: hipDevice_t): hipError_t = unsupported
  @CSignature("hipError_t (hipDevice_t, unsigned int)")
  def hipDevicePrimaryCtxSetFlags(dev: hipDevice_t, flags: unsigned_int): hipError_t = unsupported
  @CSignature("hipError_t (void)")
  def hipDeviceReset(): hipError_t = unsupported
  @CSignature("hipError_t (hipFuncCache_t)")
  def hipDeviceSetCacheConfig(cacheConfig: hipFuncCache_t): hipError_t = unsupported
  @CSignature("hipError_t (int, hipGraphMemAttributeType, void *)")
  def hipDeviceSetGraphMemAttribute(
      device: Int,
      attr: hipGraphMemAttributeType,
      value: Pointer[Unit, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (enum hipLimit_t, size_t)")
  def hipDeviceSetLimit(limit: hipLimit_t, value: size_t): hipError_t = unsupported
  @CSignature("hipError_t (int, hipMemPool_t)")
  def hipDeviceSetMemPool(device: Int, mem_pool: hipMemPool_t): hipError_t = unsupported
  @CSignature("hipError_t (hipSharedMemConfig)")
  def hipDeviceSetSharedMemConfig(config: hipSharedMemConfig): hipError_t = unsupported
  @CSignature("hipError_t (void)")
  def hipDeviceSynchronize(): hipError_t = unsupported
  @CSignature("hipError_t (size_t *, hipDevice_t)")
  def hipDeviceTotalMem(bytes: Pointer[size_t, ReadWrite, Opaque], device: hipDevice_t): hipError_t = unsupported
  @CSignature("hipError_t (int *)")
  def hipDriverGetVersion(driverVersion: Pointer[Int, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipError_t, const char **)")
  def hipDrvGetErrorName(
      hipError: hipError_t,
      errorString: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipError_t, const char **)")
  def hipDrvGetErrorString(
      hipError: hipError_t,
      errorString: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, hipDeviceptr_t)")
  def hipDrvGraphAddMemFreeNode(
      phGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      hGraph: hipGraph_t,
      dependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      dptr: hipDeviceptr_t
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, const HIP_MEMCPY3D *, hipCtx_t)"
  )
  def hipDrvGraphAddMemcpyNode(
      phGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      hGraph: hipGraph_t,
      dependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      copyParams: Pointer[HIP_MEMCPY3D, Read, Opaque],
      ctx: hipCtx_t
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, const hipMemsetParams *, hipCtx_t)"
  )
  def hipDrvGraphAddMemsetNode(
      phGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      hGraph: hipGraph_t,
      dependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      memsetParams: Pointer[hipMemsetParams, Read, Opaque],
      ctx: hipCtx_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, const HIP_MEMCPY3D *, hipCtx_t)")
  def hipDrvGraphExecMemcpyNodeSetParams(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      copyParams: Pointer[HIP_MEMCPY3D, Read, Opaque],
      ctx: hipCtx_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, const hipMemsetParams *, hipCtx_t)")
  def hipDrvGraphExecMemsetNodeSetParams(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      memsetParams: Pointer[hipMemsetParams, Read, Opaque],
      ctx: hipCtx_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, HIP_MEMCPY3D *)")
  def hipDrvGraphMemcpyNodeGetParams(
      hNode: hipGraphNode_t,
      nodeParams: Pointer[HIP_MEMCPY3D, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, const HIP_MEMCPY3D *)")
  def hipDrvGraphMemcpyNodeSetParams(
      hNode: hipGraphNode_t,
      nodeParams: Pointer[HIP_MEMCPY3D, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (const HIP_LAUNCH_CONFIG *, hipFunction_t, void **, void **)")
  def hipDrvLaunchKernelEx(
      config: Pointer[HIP_LAUNCH_CONFIG, Read, Opaque],
      f: hipFunction_t,
      params: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      extra: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (const hip_Memcpy2D *)")
  def hipDrvMemcpy2DUnaligned(pCopy: Pointer[hip_Memcpy2D, Read, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (const HIP_MEMCPY3D *)")
  def hipDrvMemcpy3D(pCopy: Pointer[HIP_MEMCPY3D, Read, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (const HIP_MEMCPY3D *, hipStream_t)")
  def hipDrvMemcpy3DAsync(pCopy: Pointer[HIP_MEMCPY3D, Read, Opaque], stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (unsigned int, hipPointer_attribute *, void **, hipDeviceptr_t)")
  def hipDrvPointerGetAttributes(
      numAttributes: unsigned_int,
      attributes: Pointer[hipPointer_attribute, ReadWrite, Opaque],
      data: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      ptr: hipDeviceptr_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipEvent_t *)")
  def hipEventCreate(event: Pointer[hipEvent_t, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipEvent_t *, unsigned int)")
  def hipEventCreateWithFlags(event: Pointer[hipEvent_t, ReadWrite, Opaque], flags: unsigned_int): hipError_t =
    unsupported
  @CSignature("hipError_t (hipEvent_t)")
  def hipEventDestroy(event: hipEvent_t): hipError_t = unsupported
  @CSignature("hipError_t (float *, hipEvent_t, hipEvent_t)")
  def hipEventElapsedTime(ms: Pointer[Float, ReadWrite, Opaque], start: hipEvent_t, stop: hipEvent_t): hipError_t =
    unsupported
  @CSignature("hipError_t (hipEvent_t)")
  def hipEventQuery(event: hipEvent_t): hipError_t = unsupported
  @CSignature("hipError_t (hipEvent_t, hipStream_t)")
  def hipEventRecord(event: hipEvent_t, stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (hipEvent_t, hipStream_t, unsigned int)")
  def hipEventRecordWithFlags(event: hipEvent_t, stream: hipStream_t, flags: unsigned_int): hipError_t = unsupported
  @CSignature("hipError_t (hipEvent_t, hipStream_t)")
  def hipEventRecord_spt(event: hipEvent_t, stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (hipEvent_t)")
  def hipEventSynchronize(event: hipEvent_t): hipError_t = unsupported
  @CSignature("hipError_t (void)")
  def hipExtGetLastError(): hipError_t = unsupported
  @CSignature("hipError_t (int, int, uint32_t *, uint32_t *)")
  def hipExtGetLinkTypeAndHopCount(
      device1: Int,
      device2: Int,
      linktype: Pointer[uint32_t, ReadWrite, Opaque],
      hopcount: Pointer[uint32_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (const void *, dim3, dim3, void **, size_t, hipStream_t, hipEvent_t, hipEvent_t, int)")
  def hipExtLaunchKernel(
      function_address: Pointer[Unit, Read, Opaque],
      numBlocks: dim3,
      dimBlocks: dim3,
      args: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      sharedMemBytes: size_t,
      stream: hipStream_t,
      startEvent: hipEvent_t,
      stopEvent: hipEvent_t,
      flags: Int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipLaunchParams *, int, unsigned int)")
  def hipExtLaunchMultiKernelMultiDevice(
      launchParamsList: Pointer[hipLaunchParams, ReadWrite, Opaque],
      numDevices: Int,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (void **, size_t, unsigned int)")
  def hipExtMallocWithFlags(
      ptr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      sizeBytes: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t *, uint32_t, const uint32_t *)")
  def hipExtStreamCreateWithCUMask(
      stream: Pointer[hipStream_t, ReadWrite, Opaque],
      cuMaskSize: uint32_t,
      cuMask: Pointer[uint32_t, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, uint32_t, uint32_t *)")
  def hipExtStreamGetCUMask(
      stream: hipStream_t,
      cuMaskSize: uint32_t,
      cuMask: Pointer[uint32_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (void **, hipExternalMemory_t, const hipExternalMemoryBufferDesc *)")
  def hipExternalMemoryGetMappedBuffer(
      devPtr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      extMem: hipExternalMemory_t,
      bufferDesc: Pointer[hipExternalMemoryBufferDesc, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipMipmappedArray_t *, hipExternalMemory_t, const hipExternalMemoryMipmappedArrayDesc *)")
  def hipExternalMemoryGetMappedMipmappedArray(
      mipmap: Pointer[hipMipmappedArray_t, ReadWrite, Opaque],
      extMem: hipExternalMemory_t,
      mipmapDesc: Pointer[hipExternalMemoryMipmappedArrayDesc, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *)")
  def hipFree(ptr: Pointer[Unit, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t)")
  def hipFreeArray(array: hipArray_t): hipError_t = unsupported
  @CSignature("hipError_t (void *, hipStream_t)")
  def hipFreeAsync(dev_ptr: Pointer[Unit, ReadWrite, Opaque], stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (void *)")
  def hipFreeHost(ptr: Pointer[Unit, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipMipmappedArray_t)")
  def hipFreeMipmappedArray(mipmappedArray: hipMipmappedArray_t): hipError_t = unsupported
  @CSignature("hipError_t (int *, hipFunction_attribute, hipFunction_t)")
  def hipFuncGetAttribute(
      value: Pointer[Int, ReadWrite, Opaque],
      attrib: hipFunction_attribute,
      hfunc: hipFunction_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (struct hipFuncAttributes *, const void *)")
  def hipFuncGetAttributes(
      attr: Pointer[hipFuncAttributes, ReadWrite, Opaque],
      func: Pointer[Unit, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (const void *, hipFuncAttribute, int)")
  def hipFuncSetAttribute(func: Pointer[Unit, Read, Opaque], attr: hipFuncAttribute, value: Int): hipError_t =
    unsupported
  @CSignature("hipError_t (const void *, hipFuncCache_t)")
  def hipFuncSetCacheConfig(func: Pointer[Unit, Read, Opaque], config: hipFuncCache_t): hipError_t = unsupported
  @CSignature("hipError_t (const void *, hipSharedMemConfig)")
  def hipFuncSetSharedMemConfig(func: Pointer[Unit, Read, Opaque], config: hipSharedMemConfig): hipError_t = unsupported
  @CSignature("hipError_t (hipChannelFormatDesc *, hipArray_const_t)")
  def hipGetChannelDesc(desc: Pointer[hipChannelFormatDesc, ReadWrite, Opaque], array: hipArray_const_t): hipError_t =
    unsupported
  @CSignature("hipError_t (int *)")
  def hipGetDevice(deviceId: Pointer[Int, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (int *)")
  def hipGetDeviceCount(count: Pointer[Int, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (unsigned int *)")
  def hipGetDeviceFlags(flags: Pointer[unsigned_int, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceProp_tR0600 *, int)")
  def hipGetDevicePropertiesR0600(prop: Pointer[hipDeviceProp_tR0600, ReadWrite, Opaque], deviceId: Int): hipError_t =
    unsupported
  @CSignature("hipError_t (const char *, void **, unsigned long long, hipDriverEntryPointQueryResult *)")
  def hipGetDriverEntryPoint(
      symbol: CString,
      funcPtr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      flags: unsigned_long_long,
      driverStatus: Pointer[hipDriverEntryPointQueryResult, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (const char *, void **, unsigned long long, hipDriverEntryPointQueryResult *)")
  def hipGetDriverEntryPoint_spt(
      symbol: CString,
      funcPtr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      flags: unsigned_long_long,
      status: Pointer[hipDriverEntryPointQueryResult, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("const char *(hipError_t)")
  def hipGetErrorName(hip_error: hipError_t): CString = unsupported
  @CSignature("const char *(hipError_t)")
  def hipGetErrorString(hipError: hipError_t): CString = unsupported
  @CSignature("hipError_t (hipFunction_t *, const void *)")
  def hipGetFuncBySymbol(
      functionPtr: Pointer[hipFunction_t, ReadWrite, Opaque],
      symbolPtr: Pointer[Unit, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (void)")
  def hipGetLastError(): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t *, hipMipmappedArray_const_t, unsigned int)")
  def hipGetMipmappedArrayLevel(
      levelArray: Pointer[hipArray_t, ReadWrite, Opaque],
      mipmappedArray: hipMipmappedArray_const_t,
      level: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (const char *, void **, int, uint64_t, hipDriverProcAddressQueryResult *)")
  def hipGetProcAddress(
      symbol: CString,
      pfn: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      hipVersion: Int,
      flags: uint64_t,
      symbolStatus: Pointer[hipDriverProcAddressQueryResult, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("int (hipStream_t)")
  def hipGetStreamDeviceId(stream: hipStream_t): Int = unsupported
  @CSignature("hipError_t (void **, const void *)")
  def hipGetSymbolAddress(
      devPtr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      symbol: Pointer[Unit, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (size_t *, const void *)")
  def hipGetSymbolSize(size: Pointer[size_t, ReadWrite, Opaque], symbol: Pointer[Unit, Read, Opaque]): hipError_t =
    unsupported
  @CSignature("hipError_t (size_t *, const textureReference *)")
  def hipGetTextureAlignmentOffset(
      offset: Pointer[size_t, ReadWrite, Opaque],
      texref: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipResourceDesc *, hipTextureObject_t)")
  def hipGetTextureObjectResourceDesc(
      pResDesc: Pointer[hipResourceDesc, ReadWrite, Opaque],
      textureObject: hipTextureObject_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (struct hipResourceViewDesc *, hipTextureObject_t)")
  def hipGetTextureObjectResourceViewDesc(
      pResViewDesc: Pointer[hipResourceViewDesc, ReadWrite, Opaque],
      textureObject: hipTextureObject_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipTextureDesc *, hipTextureObject_t)")
  def hipGetTextureObjectTextureDesc(
      pTexDesc: Pointer[hipTextureDesc, ReadWrite, Opaque],
      textureObject: hipTextureObject_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (const textureReference **, const void *)")
  def hipGetTextureReference(
      texref: Pointer[Pointer[textureReference, ReadWrite, Opaque], Read, Opaque],
      symbol: Pointer[Unit, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, const hipBatchMemOpNodeParams *)"
  )
  def hipGraphAddBatchMemOpNode(
      phGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      hGraph: hipGraph_t,
      dependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[hipBatchMemOpNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, hipGraph_t)")
  def hipGraphAddChildGraphNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      childGraph: hipGraph_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraph_t, const hipGraphNode_t *, const hipGraphNode_t *, size_t)")
  def hipGraphAddDependencies(
      graph: hipGraph_t,
      from: Pointer[hipGraphNode_t, Read, Opaque],
      to: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t)")
  def hipGraphAddEmptyNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, hipEvent_t)")
  def hipGraphAddEventRecordNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      event: hipEvent_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, hipEvent_t)")
  def hipGraphAddEventWaitNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      event: hipEvent_t
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, const hipExternalSemaphoreSignalNodeParams *)"
  )
  def hipGraphAddExternalSemaphoresSignalNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[hipExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, const hipExternalSemaphoreWaitNodeParams *)"
  )
  def hipGraphAddExternalSemaphoresWaitNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[hipExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, const hipHostNodeParams *)")
  def hipGraphAddHostNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      pNodeParams: Pointer[hipHostNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, const hipKernelNodeParams *)")
  def hipGraphAddKernelNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      pNodeParams: Pointer[hipKernelNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, hipMemAllocNodeParams *)")
  def hipGraphAddMemAllocNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      pNodeParams: Pointer[hipMemAllocNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, void *)")
  def hipGraphAddMemFreeNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      dev_ptr: Pointer[Unit, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, const hipMemcpy3DParms *)")
  def hipGraphAddMemcpyNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      pCopyParams: Pointer[hipMemcpy3DParms, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, void *, const void *, size_t, hipMemcpyKind)"
  )
  def hipGraphAddMemcpyNode1D(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      dst: Pointer[Unit, ReadWrite, Opaque],
      src: Pointer[Unit, Read, Opaque],
      count: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, void *, const void *, size_t, size_t, hipMemcpyKind)"
  )
  def hipGraphAddMemcpyNodeFromSymbol(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      dst: Pointer[Unit, ReadWrite, Opaque],
      symbol: Pointer[Unit, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, const void *, const void *, size_t, size_t, hipMemcpyKind)"
  )
  def hipGraphAddMemcpyNodeToSymbol(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      symbol: Pointer[Unit, Read, Opaque],
      src: Pointer[Unit, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, const hipMemsetParams *)")
  def hipGraphAddMemsetNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      pMemsetParams: Pointer[hipMemsetParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t *, hipGraph_t, const hipGraphNode_t *, size_t, hipGraphNodeParams *)")
  def hipGraphAddNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[hipGraphNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipBatchMemOpNodeParams *)")
  def hipGraphBatchMemOpNodeGetParams(
      hNode: hipGraphNode_t,
      nodeParams_out: Pointer[hipBatchMemOpNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipBatchMemOpNodeParams *)")
  def hipGraphBatchMemOpNodeSetParams(
      hNode: hipGraphNode_t,
      nodeParams: Pointer[hipBatchMemOpNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipGraph_t *)")
  def hipGraphChildGraphNodeGetGraph(node: hipGraphNode_t, pGraph: Pointer[hipGraph_t, ReadWrite, Opaque]): hipError_t =
    unsupported
  @CSignature("hipError_t (hipGraph_t *, hipGraph_t)")
  def hipGraphClone(pGraphClone: Pointer[hipGraph_t, ReadWrite, Opaque], originalGraph: hipGraph_t): hipError_t =
    unsupported
  @CSignature("hipError_t (hipGraph_t *, unsigned int)")
  def hipGraphCreate(pGraph: Pointer[hipGraph_t, ReadWrite, Opaque], flags: unsigned_int): hipError_t = unsupported
  @CSignature("hipError_t (hipGraph_t, const char *, unsigned int)")
  def hipGraphDebugDotPrint(graph: hipGraph_t, path: CString, flags: unsigned_int): hipError_t = unsupported
  @CSignature("hipError_t (hipGraph_t)")
  def hipGraphDestroy(graph: hipGraph_t): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t)")
  def hipGraphDestroyNode(node: hipGraphNode_t): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipEvent_t *)")
  def hipGraphEventRecordNodeGetEvent(
      node: hipGraphNode_t,
      event_out: Pointer[hipEvent_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipEvent_t)")
  def hipGraphEventRecordNodeSetEvent(node: hipGraphNode_t, event: hipEvent_t): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipEvent_t *)")
  def hipGraphEventWaitNodeGetEvent(
      node: hipGraphNode_t,
      event_out: Pointer[hipEvent_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipEvent_t)")
  def hipGraphEventWaitNodeSetEvent(node: hipGraphNode_t, event: hipEvent_t): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, const hipBatchMemOpNodeParams *)")
  def hipGraphExecBatchMemOpNodeSetParams(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      nodeParams: Pointer[hipBatchMemOpNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, hipGraph_t)")
  def hipGraphExecChildGraphNodeSetParams(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      childGraph: hipGraph_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t)")
  def hipGraphExecDestroy(graphExec: hipGraphExec_t): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, hipEvent_t)")
  def hipGraphExecEventRecordNodeSetEvent(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      event: hipEvent_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, hipEvent_t)")
  def hipGraphExecEventWaitNodeSetEvent(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      event: hipEvent_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, const hipExternalSemaphoreSignalNodeParams *)")
  def hipGraphExecExternalSemaphoresSignalNodeSetParams(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      nodeParams: Pointer[hipExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, const hipExternalSemaphoreWaitNodeParams *)")
  def hipGraphExecExternalSemaphoresWaitNodeSetParams(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      nodeParams: Pointer[hipExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, unsigned long long *)")
  def hipGraphExecGetFlags(
      graphExec: hipGraphExec_t,
      flags: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, const hipHostNodeParams *)")
  def hipGraphExecHostNodeSetParams(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipHostNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, const hipKernelNodeParams *)")
  def hipGraphExecKernelNodeSetParams(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipKernelNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, hipMemcpy3DParms *)")
  def hipGraphExecMemcpyNodeSetParams(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipMemcpy3DParms, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, void *, const void *, size_t, hipMemcpyKind)")
  def hipGraphExecMemcpyNodeSetParams1D(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      dst: Pointer[Unit, ReadWrite, Opaque],
      src: Pointer[Unit, Read, Opaque],
      count: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, void *, const void *, size_t, size_t, hipMemcpyKind)")
  def hipGraphExecMemcpyNodeSetParamsFromSymbol(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      dst: Pointer[Unit, ReadWrite, Opaque],
      symbol: Pointer[Unit, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, const void *, const void *, size_t, size_t, hipMemcpyKind)")
  def hipGraphExecMemcpyNodeSetParamsToSymbol(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      symbol: Pointer[Unit, Read, Opaque],
      src: Pointer[Unit, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, const hipMemsetParams *)")
  def hipGraphExecMemsetNodeSetParams(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipMemsetParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, hipGraphNodeParams *)")
  def hipGraphExecNodeSetParams(
      graphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      nodeParams: Pointer[hipGraphNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraph_t, hipGraphNode_t *, hipGraphExecUpdateResult *)")
  def hipGraphExecUpdate(
      hGraphExec: hipGraphExec_t,
      hGraph: hipGraph_t,
      hErrorNode_out: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      updateResult_out: Pointer[hipGraphExecUpdateResult, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipExternalSemaphoreSignalNodeParams *)")
  def hipGraphExternalSemaphoresSignalNodeGetParams(
      hNode: hipGraphNode_t,
      params_out: Pointer[hipExternalSemaphoreSignalNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, const hipExternalSemaphoreSignalNodeParams *)")
  def hipGraphExternalSemaphoresSignalNodeSetParams(
      hNode: hipGraphNode_t,
      nodeParams: Pointer[hipExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipExternalSemaphoreWaitNodeParams *)")
  def hipGraphExternalSemaphoresWaitNodeGetParams(
      hNode: hipGraphNode_t,
      params_out: Pointer[hipExternalSemaphoreWaitNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, const hipExternalSemaphoreWaitNodeParams *)")
  def hipGraphExternalSemaphoresWaitNodeSetParams(
      hNode: hipGraphNode_t,
      nodeParams: Pointer[hipExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraph_t, hipGraphNode_t *, hipGraphNode_t *, size_t *)")
  def hipGraphGetEdges(
      graph: hipGraph_t,
      from: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      to: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      numEdges: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraph_t, hipGraphNode_t *, size_t *)")
  def hipGraphGetNodes(
      graph: hipGraph_t,
      nodes: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      numNodes: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraph_t, hipGraphNode_t *, size_t *)")
  def hipGraphGetRootNodes(
      graph: hipGraph_t,
      pRootNodes: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      pNumRootNodes: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipHostNodeParams *)")
  def hipGraphHostNodeGetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipHostNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, const hipHostNodeParams *)")
  def hipGraphHostNodeSetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipHostNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t *, hipGraph_t, hipGraphNode_t *, char *, size_t)")
  def hipGraphInstantiate(
      pGraphExec: Pointer[hipGraphExec_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pErrorNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      pLogBuffer: Pointer[Byte, ReadWrite, Opaque],
      bufferSize: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t *, hipGraph_t, unsigned long long)")
  def hipGraphInstantiateWithFlags(
      pGraphExec: Pointer[hipGraphExec_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      flags: unsigned_long_long
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t *, hipGraph_t, hipGraphInstantiateParams *)")
  def hipGraphInstantiateWithParams(
      pGraphExec: Pointer[hipGraphExec_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      instantiateParams: Pointer[hipGraphInstantiateParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipGraphNode_t)")
  def hipGraphKernelNodeCopyAttributes(hSrc: hipGraphNode_t, hDst: hipGraphNode_t): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipLaunchAttributeID, hipLaunchAttributeValue *)")
  def hipGraphKernelNodeGetAttribute(
      hNode: hipGraphNode_t,
      attr: hipLaunchAttributeID,
      value: Pointer[hipLaunchAttributeValue, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipKernelNodeParams *)")
  def hipGraphKernelNodeGetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipKernelNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipLaunchAttributeID, const hipLaunchAttributeValue *)")
  def hipGraphKernelNodeSetAttribute(
      hNode: hipGraphNode_t,
      attr: hipLaunchAttributeID,
      value: Pointer[hipLaunchAttributeValue, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, const hipKernelNodeParams *)")
  def hipGraphKernelNodeSetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipKernelNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipStream_t)")
  def hipGraphLaunch(graphExec: hipGraphExec_t, stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipStream_t)")
  def hipGraphLaunch_spt(graphExec: hipGraphExec_t, stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipMemAllocNodeParams *)")
  def hipGraphMemAllocNodeGetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipMemAllocNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, void *)")
  def hipGraphMemFreeNodeGetParams(node: hipGraphNode_t, dev_ptr: Pointer[Unit, ReadWrite, Opaque]): hipError_t =
    unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipMemcpy3DParms *)")
  def hipGraphMemcpyNodeGetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipMemcpy3DParms, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, const hipMemcpy3DParms *)")
  def hipGraphMemcpyNodeSetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipMemcpy3DParms, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, void *, const void *, size_t, hipMemcpyKind)")
  def hipGraphMemcpyNodeSetParams1D(
      node: hipGraphNode_t,
      dst: Pointer[Unit, ReadWrite, Opaque],
      src: Pointer[Unit, Read, Opaque],
      count: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, void *, const void *, size_t, size_t, hipMemcpyKind)")
  def hipGraphMemcpyNodeSetParamsFromSymbol(
      node: hipGraphNode_t,
      dst: Pointer[Unit, ReadWrite, Opaque],
      symbol: Pointer[Unit, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, const void *, const void *, size_t, size_t, hipMemcpyKind)")
  def hipGraphMemcpyNodeSetParamsToSymbol(
      node: hipGraphNode_t,
      symbol: Pointer[Unit, Read, Opaque],
      src: Pointer[Unit, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipMemsetParams *)")
  def hipGraphMemsetNodeGetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipMemsetParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, const hipMemsetParams *)")
  def hipGraphMemsetNodeSetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipMemsetParams, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t *, hipGraphNode_t, hipGraph_t)")
  def hipGraphNodeFindInClone(
      pNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      originalNode: hipGraphNode_t,
      clonedGraph: hipGraph_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipGraphNode_t *, size_t *)")
  def hipGraphNodeGetDependencies(
      node: hipGraphNode_t,
      pDependencies: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      pNumDependencies: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipGraphNode_t *, size_t *)")
  def hipGraphNodeGetDependentNodes(
      node: hipGraphNode_t,
      pDependentNodes: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      pNumDependentNodes: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, unsigned int *)")
  def hipGraphNodeGetEnabled(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      isEnabled: Pointer[unsigned_int, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipGraphNodeType *)")
  def hipGraphNodeGetType(node: hipGraphNode_t, pType: Pointer[hipGraphNodeType, ReadWrite, Opaque]): hipError_t =
    unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipGraphNode_t, unsigned int)")
  def hipGraphNodeSetEnabled(hGraphExec: hipGraphExec_t, hNode: hipGraphNode_t, isEnabled: unsigned_int): hipError_t =
    unsupported
  @CSignature("hipError_t (hipGraphNode_t, hipGraphNodeParams *)")
  def hipGraphNodeSetParams(
      node: hipGraphNode_t,
      nodeParams: Pointer[hipGraphNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraph_t, hipUserObject_t, unsigned int)")
  def hipGraphReleaseUserObject(graph: hipGraph_t, `object`: hipUserObject_t, count: unsigned_int): hipError_t =
    unsupported
  @CSignature("hipError_t (hipGraph_t, const hipGraphNode_t *, const hipGraphNode_t *, size_t)")
  def hipGraphRemoveDependencies(
      graph: hipGraph_t,
      from: Pointer[hipGraphNode_t, Read, Opaque],
      to: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraph_t, hipUserObject_t, unsigned int, unsigned int)")
  def hipGraphRetainUserObject(
      graph: hipGraph_t,
      `object`: hipUserObject_t,
      count: unsigned_int,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphExec_t, hipStream_t)")
  def hipGraphUpload(graphExec: hipGraphExec_t, stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (int, hipGraphicsResource_t *, hipStream_t)")
  def hipGraphicsMapResources(
      count: Int,
      resources: Pointer[hipGraphicsResource_t, ReadWrite, Opaque],
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void **, size_t *, hipGraphicsResource_t)")
  def hipGraphicsResourceGetMappedPointer(
      devPtr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      size: Pointer[size_t, ReadWrite, Opaque],
      resource: hipGraphicsResource_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t *, hipGraphicsResource_t, unsigned int, unsigned int)")
  def hipGraphicsSubResourceGetMappedArray(
      array: Pointer[hipArray_t, ReadWrite, Opaque],
      resource: hipGraphicsResource_t,
      arrayIndex: unsigned_int,
      mipLevel: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (int, hipGraphicsResource_t *, hipStream_t)")
  def hipGraphicsUnmapResources(
      count: Int,
      resources: Pointer[hipGraphicsResource_t, ReadWrite, Opaque],
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipGraphicsResource_t)")
  def hipGraphicsUnregisterResource(resource: hipGraphicsResource_t): hipError_t = unsupported
  @CSignature("hipError_t (void **, size_t, unsigned int)")
  def hipHostAlloc(
      ptr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      size: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *)")
  def hipHostFree(ptr: Pointer[Unit, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (void **, void *, unsigned int)")
  def hipHostGetDevicePointer(
      devPtr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      hstPtr: Pointer[Unit, ReadWrite, Opaque],
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (unsigned int *, void *)")
  def hipHostGetFlags(
      flagsPtr: Pointer[unsigned_int, ReadWrite, Opaque],
      hostPtr: Pointer[Unit, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (void **, size_t, unsigned int)")
  def hipHostMalloc(
      ptr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      size: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t, unsigned int)")
  def hipHostRegister(hostPtr: Pointer[Unit, ReadWrite, Opaque], sizeBytes: size_t, flags: unsigned_int): hipError_t =
    unsupported
  @CSignature("hipError_t (void *)")
  def hipHostUnregister(hostPtr: Pointer[Unit, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipExternalMemory_t *, const hipExternalMemoryHandleDesc *)")
  def hipImportExternalMemory(
      extMem_out: Pointer[hipExternalMemory_t, ReadWrite, Opaque],
      memHandleDesc: Pointer[hipExternalMemoryHandleDesc, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipExternalSemaphore_t *, const hipExternalSemaphoreHandleDesc *)")
  def hipImportExternalSemaphore(
      extSem_out: Pointer[hipExternalSemaphore_t, ReadWrite, Opaque],
      semHandleDesc: Pointer[hipExternalSemaphoreHandleDesc, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (unsigned int)")
  def hipInit(flags: unsigned_int): hipError_t = unsupported
  @CSignature("hipError_t (void *)")
  def hipIpcCloseMemHandle(devPtr: Pointer[Unit, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipIpcEventHandle_t *, hipEvent_t)")
  def hipIpcGetEventHandle(handle: Pointer[hipIpcEventHandle_t, ReadWrite, Opaque], event: hipEvent_t): hipError_t =
    unsupported
  @CSignature("hipError_t (hipIpcMemHandle_t *, void *)")
  def hipIpcGetMemHandle(
      handle: Pointer[hipIpcMemHandle_t, ReadWrite, Opaque],
      devPtr: Pointer[Unit, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipEvent_t *, hipIpcEventHandle_t)")
  def hipIpcOpenEventHandle(event: Pointer[hipEvent_t, ReadWrite, Opaque], handle: hipIpcEventHandle_t): hipError_t =
    unsupported
  @CSignature("hipError_t (void **, hipIpcMemHandle_t, unsigned int)")
  def hipIpcOpenMemHandle(
      devPtr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      handle: hipIpcMemHandle_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipLibrary_t *, hipKernel_t)")
  def hipKernelGetLibrary(library: Pointer[hipLibrary_t, ReadWrite, Opaque], kernel: hipKernel_t): hipError_t =
    unsupported
  @CSignature("hipError_t (const char **, hipKernel_t)")
  def hipKernelGetName(name: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque], kernel: hipKernel_t): hipError_t =
    unsupported
  @CSignature("const char *(const hipFunction_t)")
  def hipKernelNameRef(f: const_hipFunction_t): CString = unsupported
  @CSignature("const char *(const void *, hipStream_t)")
  def hipKernelNameRefByPtr(hostFunction: Pointer[Unit, Read, Opaque], stream: hipStream_t): CString = unsupported
  @CSignature("hipError_t (const void *)")
  def hipLaunchByPtr(func: Pointer[Unit, Read, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (const void *, dim3, dim3, void **, unsigned int, hipStream_t)")
  def hipLaunchCooperativeKernel(
      f: Pointer[Unit, Read, Opaque],
      gridDim: dim3,
      blockDimX: dim3,
      kernelParams: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      sharedMemBytes: unsigned_int,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipLaunchParams *, int, unsigned int)")
  def hipLaunchCooperativeKernelMultiDevice(
      launchParamsList: Pointer[hipLaunchParams, ReadWrite, Opaque],
      numDevices: Int,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (const void *, dim3, dim3, void **, uint32_t, hipStream_t)")
  def hipLaunchCooperativeKernel_spt(
      f: Pointer[Unit, Read, Opaque],
      gridDim: dim3,
      blockDim: dim3,
      kernelParams: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      sharedMemBytes: uint32_t,
      hStream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipHostFn_t, void *)")
  def hipLaunchHostFunc(stream: hipStream_t, fn: hipHostFn_t, userData: Pointer[Unit, ReadWrite, Opaque]): hipError_t =
    unsupported
  @CSignature("hipError_t (hipStream_t, hipHostFn_t, void *)")
  def hipLaunchHostFunc_spt(
      stream: hipStream_t,
      fn: hipHostFn_t,
      userData: Pointer[Unit, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (const void *, dim3, dim3, void **, size_t, hipStream_t)")
  def hipLaunchKernel(
      function_address: Pointer[Unit, Read, Opaque],
      numBlocks: dim3,
      dimBlocks: dim3,
      args: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      sharedMemBytes: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (const hipLaunchConfig_t *, const void *, void **)")
  def hipLaunchKernelExC(
      config: Pointer[hipLaunchConfig_t, Read, Opaque],
      fPtr: Pointer[Unit, Read, Opaque],
      args: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (const void *, dim3, dim3, void **, size_t, hipStream_t)")
  def hipLaunchKernel_spt(
      function_address: Pointer[Unit, Read, Opaque],
      numBlocks: dim3,
      dimBlocks: dim3,
      args: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      sharedMemBytes: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipKernel_t *, unsigned int, hipLibrary_t)")
  def hipLibraryEnumerateKernels(
      kernels: Pointer[hipKernel_t, ReadWrite, Opaque],
      numKernels: unsigned_int,
      library: hipLibrary_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipKernel_t *, hipLibrary_t, const char *)")
  def hipLibraryGetKernel(
      pKernel: Pointer[hipKernel_t, ReadWrite, Opaque],
      library: hipLibrary_t,
      name: CString
  ): hipError_t = unsupported
  @CSignature("hipError_t (unsigned int *, hipLibrary_t)")
  def hipLibraryGetKernelCount(count: Pointer[unsigned_int, ReadWrite, Opaque], library: hipLibrary_t): hipError_t =
    unsupported
  @CSignature(
    "hipError_t (hipLibrary_t *, const void *, hipJitOption *, void **, unsigned int, hipLibraryOption *, void **, unsigned int)"
  )
  def hipLibraryLoadData(
      library: Pointer[hipLibrary_t, ReadWrite, Opaque],
      code: Pointer[Unit, Read, Opaque],
      jitOptions: Pointer[hipJitOption, ReadWrite, Opaque],
      jitOptionsValues: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      numJitOptions: unsigned_int,
      libraryOptions: Pointer[hipLibraryOption, ReadWrite, Opaque],
      libraryOptionValues: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      numLibraryOptions: unsigned_int
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipLibrary_t *, const char *, hipJitOption *, void **, unsigned int, hipLibraryOption *, void **, unsigned int)"
  )
  def hipLibraryLoadFromFile(
      library: Pointer[hipLibrary_t, ReadWrite, Opaque],
      fileName: CString,
      jitOptions: Pointer[hipJitOption, ReadWrite, Opaque],
      jitOptionsValues: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      numJitOptions: unsigned_int,
      libraryOptions: Pointer[hipLibraryOption, ReadWrite, Opaque],
      libraryOptionValues: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      numLibraryOptions: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipLibrary_t)")
  def hipLibraryUnload(library: hipLibrary_t): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipLinkState_t, hipJitInputType, void *, size_t, const char *, unsigned int, hipJitOption *, void **)"
  )
  def hipLinkAddData(
      state: hipLinkState_t,
      `type`: hipJitInputType,
      data: Pointer[Unit, ReadWrite, Opaque],
      size: size_t,
      name: CString,
      numOptions: unsigned_int,
      options: Pointer[hipJitOption, ReadWrite, Opaque],
      optionValues: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipLinkState_t, hipJitInputType, const char *, unsigned int, hipJitOption *, void **)")
  def hipLinkAddFile(
      state: hipLinkState_t,
      `type`: hipJitInputType,
      path: CString,
      numOptions: unsigned_int,
      options: Pointer[hipJitOption, ReadWrite, Opaque],
      optionValues: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipLinkState_t, void **, size_t *)")
  def hipLinkComplete(
      state: hipLinkState_t,
      hipBinOut: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      sizeOut: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (unsigned int, hipJitOption *, void **, hipLinkState_t *)")
  def hipLinkCreate(
      numOptions: unsigned_int,
      options: Pointer[hipJitOption, ReadWrite, Opaque],
      optionValues: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      stateOut: Pointer[hipLinkState_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipLinkState_t)")
  def hipLinkDestroy(state: hipLinkState_t): hipError_t = unsupported
  @CSignature("hipError_t (void **, size_t)")
  def hipMalloc(ptr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque], size: size_t): hipError_t =
    unsupported
  @CSignature("hipError_t (hipPitchedPtr *, hipExtent)")
  def hipMalloc3D(pitchedDevPtr: Pointer[hipPitchedPtr, ReadWrite, Opaque], extent: hipExtent): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t *, const struct hipChannelFormatDesc *, struct hipExtent, unsigned int)")
  def hipMalloc3DArray(
      array: Pointer[hipArray_t, ReadWrite, Opaque],
      desc: Pointer[hipChannelFormatDesc, Read, Opaque],
      extent: hipExtent,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t *, const hipChannelFormatDesc *, size_t, size_t, unsigned int)")
  def hipMallocArray(
      array: Pointer[hipArray_t, ReadWrite, Opaque],
      desc: Pointer[hipChannelFormatDesc, Read, Opaque],
      width: size_t,
      height: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (void **, size_t, hipStream_t)")
  def hipMallocAsync(
      dev_ptr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      size: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void **, size_t, hipMemPool_t, hipStream_t)")
  def hipMallocFromPoolAsync(
      dev_ptr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      size: size_t,
      mem_pool: hipMemPool_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void **, size_t)")
  def hipMallocHost(ptr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque], size: size_t): hipError_t =
    unsupported
  @CSignature("hipError_t (void **, size_t, unsigned int)")
  def hipMallocManaged(
      dev_ptr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      size: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipMipmappedArray_t *, const struct hipChannelFormatDesc *, struct hipExtent, unsigned int, unsigned int)"
  )
  def hipMallocMipmappedArray(
      mipmappedArray: Pointer[hipMipmappedArray_t, ReadWrite, Opaque],
      desc: Pointer[hipChannelFormatDesc, Read, Opaque],
      extent: hipExtent,
      numLevels: unsigned_int,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (void **, size_t *, size_t, size_t)")
  def hipMallocPitch(
      ptr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      pitch: Pointer[size_t, ReadWrite, Opaque],
      width: size_t,
      height: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t)")
  def hipMemAddressFree(devPtr: Pointer[Unit, ReadWrite, Opaque], size: size_t): hipError_t = unsupported
  @CSignature("hipError_t (void **, size_t, size_t, void *, unsigned long long)")
  def hipMemAddressReserve(
      ptr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      size: size_t,
      alignment: size_t,
      addr: Pointer[Unit, ReadWrite, Opaque],
      flags: unsigned_long_long
  ): hipError_t = unsupported
  @CSignature("hipError_t (const void *, size_t, hipMemoryAdvise, int)")
  def hipMemAdvise(
      dev_ptr: Pointer[Unit, Read, Opaque],
      count: size_t,
      advice: hipMemoryAdvise,
      device: Int
  ): hipError_t = unsupported
  @CSignature("hipError_t (const void *, size_t, hipMemoryAdvise, hipMemLocation)")
  def hipMemAdvise_v2(
      dev_ptr: Pointer[Unit, Read, Opaque],
      count: size_t,
      advice: hipMemoryAdvise,
      location: hipMemLocation
  ): hipError_t = unsupported
  @CSignature("hipError_t (void **, size_t)")
  def hipMemAllocHost(ptr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque], size: size_t): hipError_t =
    unsupported
  @CSignature("hipError_t (hipDeviceptr_t *, size_t *, size_t, size_t, unsigned int)")
  def hipMemAllocPitch(
      dptr: Pointer[hipDeviceptr_t, ReadWrite, Opaque],
      pitch: Pointer[size_t, ReadWrite, Opaque],
      widthInBytes: size_t,
      height: size_t,
      elementSizeBytes: unsigned_int
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipMemGenericAllocationHandle_t *, size_t, const hipMemAllocationProp *, unsigned long long)"
  )
  def hipMemCreate(
      handle: Pointer[hipMemGenericAllocationHandle_t, ReadWrite, Opaque],
      size: size_t,
      prop: Pointer[hipMemAllocationProp, Read, Opaque],
      flags: unsigned_long_long
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, hipMemGenericAllocationHandle_t, hipMemAllocationHandleType, unsigned long long)")
  def hipMemExportToShareableHandle(
      shareableHandle: Pointer[Unit, ReadWrite, Opaque],
      handle: hipMemGenericAllocationHandle_t,
      handleType: hipMemAllocationHandleType,
      flags: unsigned_long_long
  ): hipError_t = unsupported
  @CSignature("hipError_t (unsigned long long *, const hipMemLocation *, void *)")
  def hipMemGetAccess(
      flags: Pointer[unsigned_long_long, ReadWrite, Opaque],
      location: Pointer[hipMemLocation, Read, Opaque],
      ptr: Pointer[Unit, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t *, size_t *, hipDeviceptr_t)")
  def hipMemGetAddressRange(
      pbase: Pointer[hipDeviceptr_t, ReadWrite, Opaque],
      psize: Pointer[size_t, ReadWrite, Opaque],
      dptr: hipDeviceptr_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (size_t *, const hipMemAllocationProp *, hipMemAllocationGranularity_flags)")
  def hipMemGetAllocationGranularity(
      granularity: Pointer[size_t, ReadWrite, Opaque],
      prop: Pointer[hipMemAllocationProp, Read, Opaque],
      option: hipMemAllocationGranularity_flags
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipMemAllocationProp *, hipMemGenericAllocationHandle_t)")
  def hipMemGetAllocationPropertiesFromHandle(
      prop: Pointer[hipMemAllocationProp, ReadWrite, Opaque],
      handle: hipMemGenericAllocationHandle_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, hipDeviceptr_t, size_t, hipMemRangeHandleType, unsigned long long)")
  def hipMemGetHandleForAddressRange(
      handle: Pointer[Unit, ReadWrite, Opaque],
      dptr: hipDeviceptr_t,
      size: size_t,
      handleType: hipMemRangeHandleType,
      flags: unsigned_long_long
  ): hipError_t = unsupported
  @CSignature("hipError_t (size_t *, size_t *)")
  def hipMemGetInfo(free: Pointer[size_t, ReadWrite, Opaque], total: Pointer[size_t, ReadWrite, Opaque]): hipError_t =
    unsupported
  @CSignature("hipError_t (hipMemGenericAllocationHandle_t *, void *, hipMemAllocationHandleType)")
  def hipMemImportFromShareableHandle(
      handle: Pointer[hipMemGenericAllocationHandle_t, ReadWrite, Opaque],
      osHandle: Pointer[Unit, ReadWrite, Opaque],
      shHandleType: hipMemAllocationHandleType
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t, size_t, hipMemGenericAllocationHandle_t, unsigned long long)")
  def hipMemMap(
      ptr: Pointer[Unit, ReadWrite, Opaque],
      size: size_t,
      offset: size_t,
      handle: hipMemGenericAllocationHandle_t,
      flags: unsigned_long_long
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipArrayMapInfo *, unsigned int, hipStream_t)")
  def hipMemMapArrayAsync(
      mapInfoList: Pointer[hipArrayMapInfo, ReadWrite, Opaque],
      count: unsigned_int,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipMemPool_t *, const hipMemPoolProps *)")
  def hipMemPoolCreate(
      mem_pool: Pointer[hipMemPool_t, ReadWrite, Opaque],
      pool_props: Pointer[hipMemPoolProps, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipMemPool_t)")
  def hipMemPoolDestroy(mem_pool: hipMemPool_t): hipError_t = unsupported
  @CSignature("hipError_t (hipMemPoolPtrExportData *, void *)")
  def hipMemPoolExportPointer(
      export_data: Pointer[hipMemPoolPtrExportData, ReadWrite, Opaque],
      dev_ptr: Pointer[Unit, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, hipMemPool_t, hipMemAllocationHandleType, unsigned int)")
  def hipMemPoolExportToShareableHandle(
      shared_handle: Pointer[Unit, ReadWrite, Opaque],
      mem_pool: hipMemPool_t,
      handle_type: hipMemAllocationHandleType,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipMemAccessFlags *, hipMemPool_t, hipMemLocation *)")
  def hipMemPoolGetAccess(
      flags: Pointer[hipMemAccessFlags, ReadWrite, Opaque],
      mem_pool: hipMemPool_t,
      location: Pointer[hipMemLocation, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipMemPool_t, hipMemPoolAttr, void *)")
  def hipMemPoolGetAttribute(
      mem_pool: hipMemPool_t,
      attr: hipMemPoolAttr,
      value: Pointer[Unit, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipMemPool_t *, void *, hipMemAllocationHandleType, unsigned int)")
  def hipMemPoolImportFromShareableHandle(
      mem_pool: Pointer[hipMemPool_t, ReadWrite, Opaque],
      shared_handle: Pointer[Unit, ReadWrite, Opaque],
      handle_type: hipMemAllocationHandleType,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (void **, hipMemPool_t, hipMemPoolPtrExportData *)")
  def hipMemPoolImportPointer(
      dev_ptr: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      mem_pool: hipMemPool_t,
      export_data: Pointer[hipMemPoolPtrExportData, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipMemPool_t, const hipMemAccessDesc *, size_t)")
  def hipMemPoolSetAccess(
      mem_pool: hipMemPool_t,
      desc_list: Pointer[hipMemAccessDesc, Read, Opaque],
      count: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipMemPool_t, hipMemPoolAttr, void *)")
  def hipMemPoolSetAttribute(
      mem_pool: hipMemPool_t,
      attr: hipMemPoolAttr,
      value: Pointer[Unit, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipMemPool_t, size_t)")
  def hipMemPoolTrimTo(mem_pool: hipMemPool_t, min_bytes_to_hold: size_t): hipError_t = unsupported
  @CSignature("hipError_t (const void *, size_t, int, hipStream_t)")
  def hipMemPrefetchAsync(
      dev_ptr: Pointer[Unit, Read, Opaque],
      count: size_t,
      device: Int,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (const void *, size_t, hipMemLocation, unsigned int, hipStream_t)")
  def hipMemPrefetchAsync_v2(
      dev_ptr: Pointer[Unit, Read, Opaque],
      count: size_t,
      location: hipMemLocation,
      flags: unsigned_int,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t *)")
  def hipMemPtrGetInfo(ptr: Pointer[Unit, ReadWrite, Opaque], size: Pointer[size_t, ReadWrite, Opaque]): hipError_t =
    unsupported
  @CSignature("hipError_t (void *, size_t, hipMemRangeAttribute, const void *, size_t)")
  def hipMemRangeGetAttribute(
      data: Pointer[Unit, ReadWrite, Opaque],
      data_size: size_t,
      attribute: hipMemRangeAttribute,
      dev_ptr: Pointer[Unit, Read, Opaque],
      count: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void **, size_t *, hipMemRangeAttribute *, size_t, const void *, size_t)")
  def hipMemRangeGetAttributes(
      data: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      data_sizes: Pointer[size_t, ReadWrite, Opaque],
      attributes: Pointer[hipMemRangeAttribute, ReadWrite, Opaque],
      num_attributes: size_t,
      dev_ptr: Pointer[Unit, Read, Opaque],
      count: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipMemGenericAllocationHandle_t)")
  def hipMemRelease(handle: hipMemGenericAllocationHandle_t): hipError_t = unsupported
  @CSignature("hipError_t (hipMemGenericAllocationHandle_t *, void *)")
  def hipMemRetainAllocationHandle(
      handle: Pointer[hipMemGenericAllocationHandle_t, ReadWrite, Opaque],
      addr: Pointer[Unit, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t, const hipMemAccessDesc *, size_t)")
  def hipMemSetAccess(
      ptr: Pointer[Unit, ReadWrite, Opaque],
      size: size_t,
      desc: Pointer[hipMemAccessDesc, Read, Opaque],
      count: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t)")
  def hipMemUnmap(ptr: Pointer[Unit, ReadWrite, Opaque], size: size_t): hipError_t = unsupported
  @CSignature("hipError_t (void *, const void *, size_t, hipMemcpyKind)")
  def hipMemcpy(
      dst: Pointer[Unit, ReadWrite, Opaque],
      src: Pointer[Unit, Read, Opaque],
      sizeBytes: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t, const void *, size_t, size_t, size_t, hipMemcpyKind)")
  def hipMemcpy2D(
      dst: Pointer[Unit, ReadWrite, Opaque],
      dpitch: size_t,
      src: Pointer[Unit, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipArray_t, size_t, size_t, hipArray_const_t, size_t, size_t, size_t, size_t, hipMemcpyKind)"
  )
  def hipMemcpy2DArrayToArray(
      dst: hipArray_t,
      wOffsetDst: size_t,
      hOffsetDst: size_t,
      src: hipArray_const_t,
      wOffsetSrc: size_t,
      hOffsetSrc: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t, const void *, size_t, size_t, size_t, hipMemcpyKind, hipStream_t)")
  def hipMemcpy2DAsync(
      dst: Pointer[Unit, ReadWrite, Opaque],
      dpitch: size_t,
      src: Pointer[Unit, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t, const void *, size_t, size_t, size_t, hipMemcpyKind, hipStream_t)")
  def hipMemcpy2DAsync_spt(
      dst: Pointer[Unit, ReadWrite, Opaque],
      dpitch: size_t,
      src: Pointer[Unit, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t, hipArray_const_t, size_t, size_t, size_t, size_t, hipMemcpyKind)")
  def hipMemcpy2DFromArray(
      dst: Pointer[Unit, ReadWrite, Opaque],
      dpitch: size_t,
      src: hipArray_const_t,
      wOffset: size_t,
      hOffset: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (void *, size_t, hipArray_const_t, size_t, size_t, size_t, size_t, hipMemcpyKind, hipStream_t)"
  )
  def hipMemcpy2DFromArrayAsync(
      dst: Pointer[Unit, ReadWrite, Opaque],
      dpitch: size_t,
      src: hipArray_const_t,
      wOffset: size_t,
      hOffset: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (void *, size_t, hipArray_const_t, size_t, size_t, size_t, size_t, hipMemcpyKind, hipStream_t)"
  )
  def hipMemcpy2DFromArrayAsync_spt(
      dst: Pointer[Unit, ReadWrite, Opaque],
      dpitch: size_t,
      src: hipArray_const_t,
      wOffsetSrc: size_t,
      hOffsetSrc: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t, hipArray_const_t, size_t, size_t, size_t, size_t, hipMemcpyKind)")
  def hipMemcpy2DFromArray_spt(
      dst: Pointer[Unit, ReadWrite, Opaque],
      dpitch: size_t,
      src: hipArray_const_t,
      wOffset: size_t,
      hOffset: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t, size_t, size_t, const void *, size_t, size_t, size_t, hipMemcpyKind)")
  def hipMemcpy2DToArray(
      dst: hipArray_t,
      wOffset: size_t,
      hOffset: size_t,
      src: Pointer[Unit, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipArray_t, size_t, size_t, const void *, size_t, size_t, size_t, hipMemcpyKind, hipStream_t)"
  )
  def hipMemcpy2DToArrayAsync(
      dst: hipArray_t,
      wOffset: size_t,
      hOffset: size_t,
      src: Pointer[Unit, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipArray_t, size_t, size_t, const void *, size_t, size_t, size_t, hipMemcpyKind, hipStream_t)"
  )
  def hipMemcpy2DToArrayAsync_spt(
      dst: hipArray_t,
      wOffset: size_t,
      hOffset: size_t,
      src: Pointer[Unit, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t, size_t, size_t, const void *, size_t, size_t, size_t, hipMemcpyKind)")
  def hipMemcpy2DToArray_spt(
      dst: hipArray_t,
      wOffset: size_t,
      hOffset: size_t,
      src: Pointer[Unit, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t, const void *, size_t, size_t, size_t, hipMemcpyKind)")
  def hipMemcpy2D_spt(
      dst: Pointer[Unit, ReadWrite, Opaque],
      dpitch: size_t,
      src: Pointer[Unit, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (const struct hipMemcpy3DParms *)")
  def hipMemcpy3D(p: Pointer[hipMemcpy3DParms, Read, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (const struct hipMemcpy3DParms *, hipStream_t)")
  def hipMemcpy3DAsync(p: Pointer[hipMemcpy3DParms, Read, Opaque], stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (const hipMemcpy3DParms *, hipStream_t)")
  def hipMemcpy3DAsync_spt(p: Pointer[hipMemcpy3DParms, Read, Opaque], stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (size_t, struct hipMemcpy3DBatchOp *, size_t *, unsigned long long, hipStream_t)")
  def hipMemcpy3DBatchAsync(
      numOps: size_t,
      opList: Pointer[hipMemcpy3DBatchOp, ReadWrite, Opaque],
      failIdx: Pointer[size_t, ReadWrite, Opaque],
      flags: unsigned_long_long,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipMemcpy3DPeerParms *)")
  def hipMemcpy3DPeer(p: Pointer[hipMemcpy3DPeerParms, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipMemcpy3DPeerParms *, hipStream_t)")
  def hipMemcpy3DPeerAsync(p: Pointer[hipMemcpy3DPeerParms, ReadWrite, Opaque], stream: hipStream_t): hipError_t =
    unsupported
  @CSignature("hipError_t (const struct hipMemcpy3DParms *)")
  def hipMemcpy3D_spt(p: Pointer[hipMemcpy3DParms, Read, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (void *, const void *, size_t, hipMemcpyKind, hipStream_t)")
  def hipMemcpyAsync(
      dst: Pointer[Unit, ReadWrite, Opaque],
      src: Pointer[Unit, Read, Opaque],
      sizeBytes: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, const void *, size_t, hipMemcpyKind, hipStream_t)")
  def hipMemcpyAsync_spt(
      dst: Pointer[Unit, ReadWrite, Opaque],
      src: Pointer[Unit, Read, Opaque],
      sizeBytes: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t, size_t, hipArray_t, size_t, size_t)")
  def hipMemcpyAtoA(
      dstArray: hipArray_t,
      dstOffset: size_t,
      srcArray: hipArray_t,
      srcOffset: size_t,
      ByteCount: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t, hipArray_t, size_t, size_t)")
  def hipMemcpyAtoD(dstDevice: hipDeviceptr_t, srcArray: hipArray_t, srcOffset: size_t, ByteCount: size_t): hipError_t =
    unsupported
  @CSignature("hipError_t (void *, hipArray_t, size_t, size_t)")
  def hipMemcpyAtoH(
      dst: Pointer[Unit, ReadWrite, Opaque],
      srcArray: hipArray_t,
      srcOffset: size_t,
      count: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, hipArray_t, size_t, size_t, hipStream_t)")
  def hipMemcpyAtoHAsync(
      dstHost: Pointer[Unit, ReadWrite, Opaque],
      srcArray: hipArray_t,
      srcOffset: size_t,
      ByteCount: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (void **, void **, size_t *, size_t, hipMemcpyAttributes *, size_t *, size_t, size_t *, hipStream_t)"
  )
  def hipMemcpyBatchAsync(
      dsts: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      srcs: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      sizes: Pointer[size_t, ReadWrite, Opaque],
      count: size_t,
      attrs: Pointer[hipMemcpyAttributes, ReadWrite, Opaque],
      attrsIdxs: Pointer[size_t, ReadWrite, Opaque],
      numAttrs: size_t,
      failIdx: Pointer[size_t, ReadWrite, Opaque],
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t, size_t, hipDeviceptr_t, size_t)")
  def hipMemcpyDtoA(dstArray: hipArray_t, dstOffset: size_t, srcDevice: hipDeviceptr_t, ByteCount: size_t): hipError_t =
    unsupported
  @CSignature("hipError_t (hipDeviceptr_t, hipDeviceptr_t, size_t)")
  def hipMemcpyDtoD(dst: hipDeviceptr_t, src: hipDeviceptr_t, sizeBytes: size_t): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t, hipDeviceptr_t, size_t, hipStream_t)")
  def hipMemcpyDtoDAsync(dst: hipDeviceptr_t, src: hipDeviceptr_t, sizeBytes: size_t, stream: hipStream_t): hipError_t =
    unsupported
  @CSignature("hipError_t (void *, hipDeviceptr_t, size_t)")
  def hipMemcpyDtoH(dst: Pointer[Unit, ReadWrite, Opaque], src: hipDeviceptr_t, sizeBytes: size_t): hipError_t =
    unsupported
  @CSignature("hipError_t (void *, hipDeviceptr_t, size_t, hipStream_t)")
  def hipMemcpyDtoHAsync(
      dst: Pointer[Unit, ReadWrite, Opaque],
      src: hipDeviceptr_t,
      sizeBytes: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, hipArray_const_t, size_t, size_t, size_t, hipMemcpyKind)")
  def hipMemcpyFromArray(
      dst: Pointer[Unit, ReadWrite, Opaque],
      srcArray: hipArray_const_t,
      wOffset: size_t,
      hOffset: size_t,
      count: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, hipArray_const_t, size_t, size_t, size_t, hipMemcpyKind)")
  def hipMemcpyFromArray_spt(
      dst: Pointer[Unit, ReadWrite, Opaque],
      src: hipArray_const_t,
      wOffsetSrc: size_t,
      hOffset: size_t,
      count: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, const void *, size_t, size_t, hipMemcpyKind)")
  def hipMemcpyFromSymbol(
      dst: Pointer[Unit, ReadWrite, Opaque],
      symbol: Pointer[Unit, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, const void *, size_t, size_t, hipMemcpyKind, hipStream_t)")
  def hipMemcpyFromSymbolAsync(
      dst: Pointer[Unit, ReadWrite, Opaque],
      symbol: Pointer[Unit, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, const void *, size_t, size_t, hipMemcpyKind, hipStream_t)")
  def hipMemcpyFromSymbolAsync_spt(
      dst: Pointer[Unit, ReadWrite, Opaque],
      symbol: Pointer[Unit, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, const void *, size_t, size_t, hipMemcpyKind)")
  def hipMemcpyFromSymbol_spt(
      dst: Pointer[Unit, ReadWrite, Opaque],
      symbol: Pointer[Unit, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t, size_t, const void *, size_t)")
  def hipMemcpyHtoA(
      dstArray: hipArray_t,
      dstOffset: size_t,
      srcHost: Pointer[Unit, Read, Opaque],
      count: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t, size_t, const void *, size_t, hipStream_t)")
  def hipMemcpyHtoAAsync(
      dstArray: hipArray_t,
      dstOffset: size_t,
      srcHost: Pointer[Unit, Read, Opaque],
      ByteCount: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t, const void *, size_t)")
  def hipMemcpyHtoD(dst: hipDeviceptr_t, src: Pointer[Unit, Read, Opaque], sizeBytes: size_t): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t, const void *, size_t, hipStream_t)")
  def hipMemcpyHtoDAsync(
      dst: hipDeviceptr_t,
      src: Pointer[Unit, Read, Opaque],
      sizeBytes: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (const hip_Memcpy2D *)")
  def hipMemcpyParam2D(pCopy: Pointer[hip_Memcpy2D, Read, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (const hip_Memcpy2D *, hipStream_t)")
  def hipMemcpyParam2DAsync(pCopy: Pointer[hip_Memcpy2D, Read, Opaque], stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (void *, int, const void *, int, size_t)")
  def hipMemcpyPeer(
      dst: Pointer[Unit, ReadWrite, Opaque],
      dstDeviceId: Int,
      src: Pointer[Unit, Read, Opaque],
      srcDeviceId: Int,
      sizeBytes: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, int, const void *, int, size_t, hipStream_t)")
  def hipMemcpyPeerAsync(
      dst: Pointer[Unit, ReadWrite, Opaque],
      dstDeviceId: Int,
      src: Pointer[Unit, Read, Opaque],
      srcDevice: Int,
      sizeBytes: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t, size_t, size_t, const void *, size_t, hipMemcpyKind)")
  def hipMemcpyToArray(
      dst: hipArray_t,
      wOffset: size_t,
      hOffset: size_t,
      src: Pointer[Unit, Read, Opaque],
      count: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (const void *, const void *, size_t, size_t, hipMemcpyKind)")
  def hipMemcpyToSymbol(
      symbol: Pointer[Unit, Read, Opaque],
      src: Pointer[Unit, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (const void *, const void *, size_t, size_t, hipMemcpyKind, hipStream_t)")
  def hipMemcpyToSymbolAsync(
      symbol: Pointer[Unit, Read, Opaque],
      src: Pointer[Unit, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (const void *, const void *, size_t, size_t, hipMemcpyKind, hipStream_t)")
  def hipMemcpyToSymbolAsync_spt(
      symbol: Pointer[Unit, Read, Opaque],
      src: Pointer[Unit, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (const void *, const void *, size_t, size_t, hipMemcpyKind)")
  def hipMemcpyToSymbol_spt(
      symbol: Pointer[Unit, Read, Opaque],
      src: Pointer[Unit, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, const void *, size_t, hipMemcpyKind, hipStream_t)")
  def hipMemcpyWithStream(
      dst: Pointer[Unit, ReadWrite, Opaque],
      src: Pointer[Unit, Read, Opaque],
      sizeBytes: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, const void *, size_t, hipMemcpyKind)")
  def hipMemcpy_spt(
      dst: Pointer[Unit, ReadWrite, Opaque],
      src: Pointer[Unit, Read, Opaque],
      sizeBytes: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, int, size_t)")
  def hipMemset(dst: Pointer[Unit, ReadWrite, Opaque], value: Int, sizeBytes: size_t): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t, int, size_t, size_t)")
  def hipMemset2D(
      dst: Pointer[Unit, ReadWrite, Opaque],
      pitch: size_t,
      value: Int,
      width: size_t,
      height: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t, int, size_t, size_t, hipStream_t)")
  def hipMemset2DAsync(
      dst: Pointer[Unit, ReadWrite, Opaque],
      pitch: size_t,
      value: Int,
      width: size_t,
      height: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t, int, size_t, size_t, hipStream_t)")
  def hipMemset2DAsync_spt(
      dst: Pointer[Unit, ReadWrite, Opaque],
      pitch: size_t,
      value: Int,
      width: size_t,
      height: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, size_t, int, size_t, size_t)")
  def hipMemset2D_spt(
      dst: Pointer[Unit, ReadWrite, Opaque],
      pitch: size_t,
      value: Int,
      width: size_t,
      height: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipPitchedPtr, int, hipExtent)")
  def hipMemset3D(pitchedDevPtr: hipPitchedPtr, value: Int, extent: hipExtent): hipError_t = unsupported
  @CSignature("hipError_t (hipPitchedPtr, int, hipExtent, hipStream_t)")
  def hipMemset3DAsync(pitchedDevPtr: hipPitchedPtr, value: Int, extent: hipExtent, stream: hipStream_t): hipError_t =
    unsupported
  @CSignature("hipError_t (hipPitchedPtr, int, hipExtent, hipStream_t)")
  def hipMemset3DAsync_spt(
      pitchedDevPtr: hipPitchedPtr,
      value: Int,
      extent: hipExtent,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipPitchedPtr, int, hipExtent)")
  def hipMemset3D_spt(pitchedDevPtr: hipPitchedPtr, value: Int, extent: hipExtent): hipError_t = unsupported
  @CSignature("hipError_t (void *, int, size_t, hipStream_t)")
  def hipMemsetAsync(
      dst: Pointer[Unit, ReadWrite, Opaque],
      value: Int,
      sizeBytes: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, int, size_t, hipStream_t)")
  def hipMemsetAsync_spt(
      dst: Pointer[Unit, ReadWrite, Opaque],
      value: Int,
      sizeBytes: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t, unsigned short, size_t)")
  def hipMemsetD16(dest: hipDeviceptr_t, value: unsigned_short, count: size_t): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t, unsigned short, size_t, hipStream_t)")
  def hipMemsetD16Async(dest: hipDeviceptr_t, value: unsigned_short, count: size_t, stream: hipStream_t): hipError_t =
    unsupported
  @CSignature("hipError_t (hipDeviceptr_t, size_t, unsigned short, size_t, size_t)")
  def hipMemsetD2D16(
      dst: hipDeviceptr_t,
      dstPitch: size_t,
      value: unsigned_short,
      width: size_t,
      height: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t, size_t, unsigned short, size_t, size_t, hipStream_t)")
  def hipMemsetD2D16Async(
      dst: hipDeviceptr_t,
      dstPitch: size_t,
      value: unsigned_short,
      width: size_t,
      height: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t, size_t, unsigned int, size_t, size_t)")
  def hipMemsetD2D32(
      dst: hipDeviceptr_t,
      dstPitch: size_t,
      value: unsigned_int,
      width: size_t,
      height: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t, size_t, unsigned int, size_t, size_t, hipStream_t)")
  def hipMemsetD2D32Async(
      dst: hipDeviceptr_t,
      dstPitch: size_t,
      value: unsigned_int,
      width: size_t,
      height: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t, size_t, unsigned char, size_t, size_t)")
  def hipMemsetD2D8(
      dst: hipDeviceptr_t,
      dstPitch: size_t,
      value: net.kurobako.gemstone.cuda.UInt8,
      width: size_t,
      height: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t, size_t, unsigned char, size_t, size_t, hipStream_t)")
  def hipMemsetD2D8Async(
      dst: hipDeviceptr_t,
      dstPitch: size_t,
      value: net.kurobako.gemstone.cuda.UInt8,
      width: size_t,
      height: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t, int, size_t)")
  def hipMemsetD32(dest: hipDeviceptr_t, value: Int, count: size_t): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t, int, size_t, hipStream_t)")
  def hipMemsetD32Async(dst: hipDeviceptr_t, value: Int, count: size_t, stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t, unsigned char, size_t)")
  def hipMemsetD8(dest: hipDeviceptr_t, value: net.kurobako.gemstone.cuda.UInt8, count: size_t): hipError_t =
    unsupported
  @CSignature("hipError_t (hipDeviceptr_t, unsigned char, size_t, hipStream_t)")
  def hipMemsetD8Async(
      dest: hipDeviceptr_t,
      value: net.kurobako.gemstone.cuda.UInt8,
      count: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (void *, int, size_t)")
  def hipMemset_spt(dst: Pointer[Unit, ReadWrite, Opaque], value: Int, sizeBytes: size_t): hipError_t = unsupported
  @CSignature("hipError_t (hipMipmappedArray_t *, HIP_ARRAY3D_DESCRIPTOR *, unsigned int)")
  def hipMipmappedArrayCreate(
      pHandle: Pointer[hipMipmappedArray_t, ReadWrite, Opaque],
      pMipmappedArrayDesc: Pointer[HIP_ARRAY3D_DESCRIPTOR, ReadWrite, Opaque],
      numMipmapLevels: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipMipmappedArray_t)")
  def hipMipmappedArrayDestroy(hMipmappedArray: hipMipmappedArray_t): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t *, hipMipmappedArray_t, unsigned int)")
  def hipMipmappedArrayGetLevel(
      pLevelArray: Pointer[hipArray_t, ReadWrite, Opaque],
      hMipMappedArray: hipMipmappedArray_t,
      level: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipFunction_t *, hipModule_t, const char *)")
  def hipModuleGetFunction(
      function: Pointer[hipFunction_t, ReadWrite, Opaque],
      module: hipModule_t,
      kname: CString
  ): hipError_t = unsupported
  @CSignature("hipError_t (unsigned int *, hipModule_t)")
  def hipModuleGetFunctionCount(count: Pointer[unsigned_int, ReadWrite, Opaque], mod: hipModule_t): hipError_t =
    unsupported
  @CSignature("hipError_t (hipDeviceptr_t *, size_t *, hipModule_t, const char *)")
  def hipModuleGetGlobal(
      dptr: Pointer[hipDeviceptr_t, ReadWrite, Opaque],
      bytes: Pointer[size_t, ReadWrite, Opaque],
      hmod: hipModule_t,
      name: CString
  ): hipError_t = unsupported
  @CSignature("hipError_t (textureReference **, hipModule_t, const char *)")
  def hipModuleGetTexRef(
      texRef: Pointer[Pointer[textureReference, ReadWrite, Opaque], ReadWrite, Opaque],
      hmod: hipModule_t,
      name: CString
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipFunction_t, unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, hipStream_t, void **)"
  )
  def hipModuleLaunchCooperativeKernel(
      f: hipFunction_t,
      gridDimX: unsigned_int,
      gridDimY: unsigned_int,
      gridDimZ: unsigned_int,
      blockDimX: unsigned_int,
      blockDimY: unsigned_int,
      blockDimZ: unsigned_int,
      sharedMemBytes: unsigned_int,
      stream: hipStream_t,
      kernelParams: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipFunctionLaunchParams *, unsigned int, unsigned int)")
  def hipModuleLaunchCooperativeKernelMultiDevice(
      launchParamsList: Pointer[hipFunctionLaunchParams, ReadWrite, Opaque],
      numDevices: unsigned_int,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipFunction_t, unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, hipStream_t, void **, void **)"
  )
  def hipModuleLaunchKernel(
      f: hipFunction_t,
      gridDimX: unsigned_int,
      gridDimY: unsigned_int,
      gridDimZ: unsigned_int,
      blockDimX: unsigned_int,
      blockDimY: unsigned_int,
      blockDimZ: unsigned_int,
      sharedMemBytes: unsigned_int,
      stream: hipStream_t,
      kernelParams: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque],
      extra: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipModule_t *, const char *)")
  def hipModuleLoad(module: Pointer[hipModule_t, ReadWrite, Opaque], fname: CString): hipError_t = unsupported
  @CSignature("hipError_t (hipModule_t *, const void *)")
  def hipModuleLoadData(
      module: Pointer[hipModule_t, ReadWrite, Opaque],
      image: Pointer[Unit, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipModule_t *, const void *, unsigned int, hipJitOption *, void **)")
  def hipModuleLoadDataEx(
      module: Pointer[hipModule_t, ReadWrite, Opaque],
      image: Pointer[Unit, Read, Opaque],
      numOptions: unsigned_int,
      options: Pointer[hipJitOption, ReadWrite, Opaque],
      optionValues: Pointer[Pointer[Unit, ReadWrite, Opaque], ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipModule_t *, const void *)")
  def hipModuleLoadFatBinary(
      module: Pointer[hipModule_t, ReadWrite, Opaque],
      fatbin: Pointer[Unit, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (int *, hipFunction_t, int, size_t)")
  def hipModuleOccupancyMaxActiveBlocksPerMultiprocessor(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      f: hipFunction_t,
      blockSize: Int,
      dynSharedMemPerBlk: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (int *, hipFunction_t, int, size_t, unsigned int)")
  def hipModuleOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      f: hipFunction_t,
      blockSize: Int,
      dynSharedMemPerBlk: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (int *, int *, hipFunction_t, size_t, int)")
  def hipModuleOccupancyMaxPotentialBlockSize(
      gridSize: Pointer[Int, ReadWrite, Opaque],
      blockSize: Pointer[Int, ReadWrite, Opaque],
      f: hipFunction_t,
      dynSharedMemPerBlk: size_t,
      blockSizeLimit: Int
  ): hipError_t = unsupported
  @CSignature("hipError_t (int *, int *, hipFunction_t, size_t, int, unsigned int)")
  def hipModuleOccupancyMaxPotentialBlockSizeWithFlags(
      gridSize: Pointer[Int, ReadWrite, Opaque],
      blockSize: Pointer[Int, ReadWrite, Opaque],
      f: hipFunction_t,
      dynSharedMemPerBlk: size_t,
      blockSizeLimit: Int,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipModule_t)")
  def hipModuleUnload(module: hipModule_t): hipError_t = unsupported
  @CSignature("hipError_t (size_t *, const void *, int, int)")
  def hipOccupancyAvailableDynamicSMemPerBlock(
      dynamicSmemSize: Pointer[size_t, ReadWrite, Opaque],
      f: Pointer[Unit, Read, Opaque],
      numBlocks: Int,
      blockSize: Int
  ): hipError_t = unsupported
  @CSignature("hipError_t (int *, const void *, int, size_t)")
  def hipOccupancyMaxActiveBlocksPerMultiprocessor(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      f: Pointer[Unit, Read, Opaque],
      blockSize: Int,
      dynSharedMemPerBlk: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (int *, const void *, int, size_t, unsigned int)")
  def hipOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      f: Pointer[Unit, Read, Opaque],
      blockSize: Int,
      dynSharedMemPerBlk: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (int *, int *, const void *, size_t, int)")
  def hipOccupancyMaxPotentialBlockSize(
      gridSize: Pointer[Int, ReadWrite, Opaque],
      blockSize: Pointer[Int, ReadWrite, Opaque],
      f: Pointer[Unit, Read, Opaque],
      dynSharedMemPerBlk: size_t,
      blockSizeLimit: Int
  ): hipError_t = unsupported
  @CSignature("hipError_t (void)")
  def hipPeekAtLastError(): hipError_t = unsupported
  @CSignature("hipError_t (void *, hipPointer_attribute, hipDeviceptr_t)")
  def hipPointerGetAttribute(
      data: Pointer[Unit, ReadWrite, Opaque],
      attribute: hipPointer_attribute,
      ptr: hipDeviceptr_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipPointerAttribute_t *, const void *)")
  def hipPointerGetAttributes(
      attributes: Pointer[hipPointerAttribute_t, ReadWrite, Opaque],
      ptr: Pointer[Unit, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (const void *, hipPointer_attribute, hipDeviceptr_t)")
  def hipPointerSetAttribute(
      value: Pointer[Unit, Read, Opaque],
      attribute: hipPointer_attribute,
      ptr: hipDeviceptr_t
  ): hipError_t = unsupported
  @CSignature("hipError_t ()")
  def hipProfilerStart(): hipError_t = unsupported
  @CSignature("hipError_t ()")
  def hipProfilerStop(): hipError_t = unsupported
  @CSignature("hipError_t (int *)")
  def hipRuntimeGetVersion(runtimeVersion: Pointer[Int, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (int)")
  def hipSetDevice(deviceId: Int): hipError_t = unsupported
  @CSignature("hipError_t (unsigned int)")
  def hipSetDeviceFlags(flags: unsigned_int): hipError_t = unsupported
  @CSignature("hipError_t (int *, int)")
  def hipSetValidDevices(device_arr: Pointer[Int, ReadWrite, Opaque], len: Int): hipError_t = unsupported
  @CSignature("hipError_t (const void *, size_t, size_t)")
  def hipSetupArgument(arg: Pointer[Unit, Read, Opaque], size: size_t, offset: size_t): hipError_t = unsupported
  @CSignature(
    "hipError_t (const hipExternalSemaphore_t *, const hipExternalSemaphoreSignalParams *, unsigned int, hipStream_t)"
  )
  def hipSignalExternalSemaphoresAsync(
      extSemArray: Pointer[hipExternalSemaphore_t, Read, Opaque],
      paramsArray: Pointer[hipExternalSemaphoreSignalParams, Read, Opaque],
      numExtSems: unsigned_int,
      stream: hipStream_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipStreamCallback_t, void *, unsigned int)")
  def hipStreamAddCallback(
      stream: hipStream_t,
      callback: hipStreamCallback_t,
      userData: Pointer[Unit, ReadWrite, Opaque],
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipStreamCallback_t, void *, unsigned int)")
  def hipStreamAddCallback_spt(
      stream: hipStream_t,
      callback: hipStreamCallback_t,
      userData: Pointer[Unit, ReadWrite, Opaque],
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, void *, size_t, unsigned int)")
  def hipStreamAttachMemAsync(
      stream: hipStream_t,
      dev_ptr: Pointer[Unit, ReadWrite, Opaque],
      length: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, unsigned int, hipStreamBatchMemOpParams *, unsigned int)")
  def hipStreamBatchMemOp(
      stream: hipStream_t,
      count: unsigned_int,
      paramArray: Pointer[hipStreamBatchMemOpParams, ReadWrite, Opaque],
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipStreamCaptureMode)")
  def hipStreamBeginCapture(stream: hipStream_t, mode: hipStreamCaptureMode): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipStream_t, hipGraph_t, const hipGraphNode_t *, const hipGraphEdgeData *, size_t, hipStreamCaptureMode)"
  )
  def hipStreamBeginCaptureToGraph(
      stream: hipStream_t,
      graph: hipGraph_t,
      dependencies: Pointer[hipGraphNode_t, Read, Opaque],
      dependencyData: Pointer[hipGraphEdgeData, Read, Opaque],
      numDependencies: size_t,
      mode: hipStreamCaptureMode
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipStreamCaptureMode)")
  def hipStreamBeginCapture_spt(stream: hipStream_t, mode: hipStreamCaptureMode): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipStream_t)")
  def hipStreamCopyAttributes(dst: hipStream_t, src: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t *)")
  def hipStreamCreate(stream: Pointer[hipStream_t, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t *, unsigned int)")
  def hipStreamCreateWithFlags(stream: Pointer[hipStream_t, ReadWrite, Opaque], flags: unsigned_int): hipError_t =
    unsupported
  @CSignature("hipError_t (hipStream_t *, unsigned int, int)")
  def hipStreamCreateWithPriority(
      stream: Pointer[hipStream_t, ReadWrite, Opaque],
      flags: unsigned_int,
      priority: Int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t)")
  def hipStreamDestroy(stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipGraph_t *)")
  def hipStreamEndCapture(stream: hipStream_t, pGraph: Pointer[hipGraph_t, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipGraph_t *)")
  def hipStreamEndCapture_spt(stream: hipStream_t, pGraph: Pointer[hipGraph_t, ReadWrite, Opaque]): hipError_t =
    unsupported
  @CSignature("hipError_t (hipStream_t, hipLaunchAttributeID, hipLaunchAttributeValue *)")
  def hipStreamGetAttribute(
      stream: hipStream_t,
      attr: hipLaunchAttributeID,
      value_out: Pointer[hipLaunchAttributeValue, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipStreamCaptureStatus *, unsigned long long *)")
  def hipStreamGetCaptureInfo(
      stream: hipStream_t,
      pCaptureStatus: Pointer[hipStreamCaptureStatus, ReadWrite, Opaque],
      pId: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipStreamCaptureStatus *, unsigned long long *)")
  def hipStreamGetCaptureInfo_spt(
      stream: hipStream_t,
      pCaptureStatus: Pointer[hipStreamCaptureStatus, ReadWrite, Opaque],
      pId: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipStream_t, hipStreamCaptureStatus *, unsigned long long *, hipGraph_t *, const hipGraphNode_t **, size_t *)"
  )
  def hipStreamGetCaptureInfo_v2(
      stream: hipStream_t,
      captureStatus_out: Pointer[hipStreamCaptureStatus, ReadWrite, Opaque],
      id_out: Pointer[unsigned_long_long, ReadWrite, Opaque],
      graph_out: Pointer[hipGraph_t, ReadWrite, Opaque],
      dependencies_out: Pointer[Pointer[hipGraphNode_t, ReadWrite, Opaque], Read, Opaque],
      numDependencies_out: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipStream_t, hipStreamCaptureStatus *, unsigned long long *, hipGraph_t *, const hipGraphNode_t **, size_t *)"
  )
  def hipStreamGetCaptureInfo_v2_spt(
      stream: hipStream_t,
      captureStatus_out: Pointer[hipStreamCaptureStatus, ReadWrite, Opaque],
      id_out: Pointer[unsigned_long_long, ReadWrite, Opaque],
      graph_out: Pointer[hipGraph_t, ReadWrite, Opaque],
      dependencies_out: Pointer[Pointer[hipGraphNode_t, ReadWrite, Opaque], Read, Opaque],
      numDependencies_out: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipDevice_t *)")
  def hipStreamGetDevice(stream: hipStream_t, device: Pointer[hipDevice_t, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, unsigned int *)")
  def hipStreamGetFlags(stream: hipStream_t, flags: Pointer[unsigned_int, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, unsigned int *)")
  def hipStreamGetFlags_spt(stream: hipStream_t, flags: Pointer[unsigned_int, ReadWrite, Opaque]): hipError_t =
    unsupported
  @CSignature("hipError_t (hipStream_t, unsigned long long *)")
  def hipStreamGetId(stream: hipStream_t, streamId: Pointer[unsigned_long_long, ReadWrite, Opaque]): hipError_t =
    unsupported
  @CSignature("hipError_t (hipStream_t, int *)")
  def hipStreamGetPriority(stream: hipStream_t, priority: Pointer[Int, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, int *)")
  def hipStreamGetPriority_spt(stream: hipStream_t, priority: Pointer[Int, ReadWrite, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipStreamCaptureStatus *)")
  def hipStreamIsCapturing(
      stream: hipStream_t,
      pCaptureStatus: Pointer[hipStreamCaptureStatus, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipStreamCaptureStatus *)")
  def hipStreamIsCapturing_spt(
      stream: hipStream_t,
      pCaptureStatus: Pointer[hipStreamCaptureStatus, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t)")
  def hipStreamQuery(stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t)")
  def hipStreamQuery_spt(stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipLaunchAttributeID, const hipLaunchAttributeValue *)")
  def hipStreamSetAttribute(
      stream: hipStream_t,
      attr: hipLaunchAttributeID,
      value: Pointer[hipLaunchAttributeValue, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t)")
  def hipStreamSynchronize(stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t)")
  def hipStreamSynchronize_spt(stream: hipStream_t): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipGraphNode_t *, size_t, unsigned int)")
  def hipStreamUpdateCaptureDependencies(
      stream: hipStream_t,
      dependencies: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      numDependencies: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipEvent_t, unsigned int)")
  def hipStreamWaitEvent(stream: hipStream_t, event: hipEvent_t, flags: unsigned_int): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, hipEvent_t, unsigned int)")
  def hipStreamWaitEvent_spt(stream: hipStream_t, event: hipEvent_t, flags: unsigned_int): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, void *, uint32_t, unsigned int, uint32_t)")
  def hipStreamWaitValue32(
      stream: hipStream_t,
      ptr: Pointer[Unit, ReadWrite, Opaque],
      value: uint32_t,
      flags: unsigned_int,
      mask: uint32_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, void *, uint64_t, unsigned int, uint64_t)")
  def hipStreamWaitValue64(
      stream: hipStream_t,
      ptr: Pointer[Unit, ReadWrite, Opaque],
      value: uint64_t,
      flags: unsigned_int,
      mask: uint64_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, void *, uint32_t, unsigned int)")
  def hipStreamWriteValue32(
      stream: hipStream_t,
      ptr: Pointer[Unit, ReadWrite, Opaque],
      value: uint32_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStream_t, void *, uint64_t, unsigned int)")
  def hipStreamWriteValue64(
      stream: hipStream_t,
      ptr: Pointer[Unit, ReadWrite, Opaque],
      value: uint64_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature(
    "hipError_t (hipTextureObject_t *, const HIP_RESOURCE_DESC *, const HIP_TEXTURE_DESC *, const HIP_RESOURCE_VIEW_DESC *)"
  )
  def hipTexObjectCreate(
      pTexObject: Pointer[hipTextureObject_t, ReadWrite, Opaque],
      pResDesc: Pointer[HIP_RESOURCE_DESC, Read, Opaque],
      pTexDesc: Pointer[HIP_TEXTURE_DESC, Read, Opaque],
      pResViewDesc: Pointer[HIP_RESOURCE_VIEW_DESC, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipTextureObject_t)")
  def hipTexObjectDestroy(texObject: hipTextureObject_t): hipError_t = unsupported
  @CSignature("hipError_t (HIP_RESOURCE_DESC *, hipTextureObject_t)")
  def hipTexObjectGetResourceDesc(
      pResDesc: Pointer[HIP_RESOURCE_DESC, ReadWrite, Opaque],
      texObject: hipTextureObject_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (HIP_RESOURCE_VIEW_DESC *, hipTextureObject_t)")
  def hipTexObjectGetResourceViewDesc(
      pResViewDesc: Pointer[HIP_RESOURCE_VIEW_DESC, ReadWrite, Opaque],
      texObject: hipTextureObject_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (HIP_TEXTURE_DESC *, hipTextureObject_t)")
  def hipTexObjectGetTextureDesc(
      pTexDesc: Pointer[HIP_TEXTURE_DESC, ReadWrite, Opaque],
      texObject: hipTextureObject_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipDeviceptr_t *, const textureReference *)")
  def hipTexRefGetAddress(
      dev_ptr: Pointer[hipDeviceptr_t, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (enum hipTextureAddressMode *, const textureReference *, int)")
  def hipTexRefGetAddressMode(
      pam: Pointer[hipTextureAddressMode, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque],
      dim: Int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_t *, const textureReference *)")
  def hipTexRefGetArray(
      pArray: Pointer[hipArray_t, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (float *, const textureReference *)")
  def hipTexRefGetBorderColor(
      pBorderColor: Pointer[Float, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (enum hipTextureFilterMode *, const textureReference *)")
  def hipTexRefGetFilterMode(
      pfm: Pointer[hipTextureFilterMode, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (unsigned int *, const textureReference *)")
  def hipTexRefGetFlags(
      pFlags: Pointer[unsigned_int, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipArray_Format *, int *, const textureReference *)")
  def hipTexRefGetFormat(
      pFormat: Pointer[hipArray_Format, ReadWrite, Opaque],
      pNumChannels: Pointer[Int, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (int *, const textureReference *)")
  def hipTexRefGetMaxAnisotropy(
      pmaxAnsio: Pointer[Int, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipMipmappedArray_t *, const textureReference *)")
  def hipTexRefGetMipMappedArray(
      pArray: Pointer[hipMipmappedArray_t, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (enum hipTextureFilterMode *, const textureReference *)")
  def hipTexRefGetMipmapFilterMode(
      pfm: Pointer[hipTextureFilterMode, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (float *, const textureReference *)")
  def hipTexRefGetMipmapLevelBias(
      pbias: Pointer[Float, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (float *, float *, const textureReference *)")
  def hipTexRefGetMipmapLevelClamp(
      pminMipmapLevelClamp: Pointer[Float, ReadWrite, Opaque],
      pmaxMipmapLevelClamp: Pointer[Float, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (size_t *, textureReference *, hipDeviceptr_t, size_t)")
  def hipTexRefSetAddress(
      ByteOffset: Pointer[size_t, ReadWrite, Opaque],
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      dptr: hipDeviceptr_t,
      bytes: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (textureReference *, const HIP_ARRAY_DESCRIPTOR *, hipDeviceptr_t, size_t)")
  def hipTexRefSetAddress2D(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      desc: Pointer[HIP_ARRAY_DESCRIPTOR, Read, Opaque],
      dptr: hipDeviceptr_t,
      Pitch: size_t
  ): hipError_t = unsupported
  @CSignature("hipError_t (textureReference *, int, enum hipTextureAddressMode)")
  def hipTexRefSetAddressMode(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      dim: Int,
      am: hipTextureAddressMode
  ): hipError_t = unsupported
  @CSignature("hipError_t (textureReference *, hipArray_const_t, unsigned int)")
  def hipTexRefSetArray(
      tex: Pointer[textureReference, ReadWrite, Opaque],
      array: hipArray_const_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (textureReference *, float *)")
  def hipTexRefSetBorderColor(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      pBorderColor: Pointer[Float, ReadWrite, Opaque]
  ): hipError_t = unsupported
  @CSignature("hipError_t (textureReference *, enum hipTextureFilterMode)")
  def hipTexRefSetFilterMode(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      fm: hipTextureFilterMode
  ): hipError_t = unsupported
  @CSignature("hipError_t (textureReference *, unsigned int)")
  def hipTexRefSetFlags(texRef: Pointer[textureReference, ReadWrite, Opaque], Flags: unsigned_int): hipError_t =
    unsupported
  @CSignature("hipError_t (textureReference *, hipArray_Format, int)")
  def hipTexRefSetFormat(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      fmt: hipArray_Format,
      NumPackedComponents: Int
  ): hipError_t = unsupported
  @CSignature("hipError_t (textureReference *, unsigned int)")
  def hipTexRefSetMaxAnisotropy(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      maxAniso: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (textureReference *, enum hipTextureFilterMode)")
  def hipTexRefSetMipmapFilterMode(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      fm: hipTextureFilterMode
  ): hipError_t = unsupported
  @CSignature("hipError_t (textureReference *, float)")
  def hipTexRefSetMipmapLevelBias(texRef: Pointer[textureReference, ReadWrite, Opaque], bias: Float): hipError_t =
    unsupported
  @CSignature("hipError_t (textureReference *, float, float)")
  def hipTexRefSetMipmapLevelClamp(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      minMipMapLevelClamp: Float,
      maxMipMapLevelClamp: Float
  ): hipError_t = unsupported
  @CSignature("hipError_t (textureReference *, struct hipMipmappedArray *, unsigned int)")
  def hipTexRefSetMipmappedArray(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      mipmappedArray: Pointer[hipMipmappedArray, ReadWrite, Opaque],
      Flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipStreamCaptureMode *)")
  def hipThreadExchangeStreamCaptureMode(mode: Pointer[hipStreamCaptureMode, ReadWrite, Opaque]): hipError_t =
    unsupported
  @CSignature("hipError_t (const textureReference *)")
  def hipUnbindTexture(tex: Pointer[textureReference, Read, Opaque]): hipError_t = unsupported
  @CSignature("hipError_t (hipUserObject_t *, void *, hipHostFn_t, unsigned int, unsigned int)")
  def hipUserObjectCreate(
      object_out: Pointer[hipUserObject_t, ReadWrite, Opaque],
      ptr: Pointer[Unit, ReadWrite, Opaque],
      destroy: hipHostFn_t,
      initialRefcount: unsigned_int,
      flags: unsigned_int
  ): hipError_t = unsupported
  @CSignature("hipError_t (hipUserObject_t, unsigned int)")
  def hipUserObjectRelease(`object`: hipUserObject_t, count: unsigned_int): hipError_t = unsupported
  @CSignature("hipError_t (hipUserObject_t, unsigned int)")
  def hipUserObjectRetain(`object`: hipUserObject_t, count: unsigned_int): hipError_t = unsupported
  @CSignature(
    "hipError_t (const hipExternalSemaphore_t *, const hipExternalSemaphoreWaitParams *, unsigned int, hipStream_t)"
  )
  def hipWaitExternalSemaphoresAsync(
      extSemArray: Pointer[hipExternalSemaphore_t, Read, Opaque],
      paramsArray: Pointer[hipExternalSemaphoreWaitParams, Read, Opaque],
      numExtSems: unsigned_int,
      stream: hipStream_t
  ): hipError_t = unsupported
}

object HIP {
  val HeaderVersion: String = "7.2.3"
  val SurfaceSize: Int      = 498
}

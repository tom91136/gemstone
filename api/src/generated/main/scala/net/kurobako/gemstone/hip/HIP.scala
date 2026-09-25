// Generated from pinned official headers by GenerateAPISurfaces. Regenerate with: sbt codegen
package net.kurobako.gemstone.hip

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.abi.ABI.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object HIP {
  type HIP_ARRAY3D_DESCRIPTOR = CValue["HIP_ARRAY3D_DESCRIPTOR"]
  type HIP_ARRAY_DESCRIPTOR = CValue["HIP_ARRAY_DESCRIPTOR"]
  type HIP_LAUNCH_CONFIG = CValue["HIP_LAUNCH_CONFIG"]
  type HIP_MEMCPY3D = CValue["HIP_MEMCPY3D"]
  type HIP_RESOURCE_DESC = CValue["HIP_RESOURCE_DESC"]
  type HIP_RESOURCE_VIEW_DESC = CValue["HIP_RESOURCE_VIEW_DESC"]
  type HIP_TEXTURE_DESC = CValue["HIP_TEXTURE_DESC"]
  type const_hipFunction_t = CValue["const hipFunction_t"]
  type dim3 = CValue["dim3"]
  type enum_hipChannelFormatKind = CValue["enum hipChannelFormatKind"]
  type enum_hipLimit_t = CValue["enum hipLimit_t"]
  type enum_hipTextureAddressMode = CValue["enum hipTextureAddressMode"]
  type enum_hipTextureFilterMode = CValue["enum hipTextureFilterMode"]
  type hipArrayMapInfo = CValue["hipArrayMapInfo"]
  type hipArray_Format = CValue["hipArray_Format"]
  type hipArray_const_t = CValue["hipArray_const_t"]
  type hipArray_t = CValue["hipArray_t"]
  type hipBatchMemOpNodeParams = CValue["hipBatchMemOpNodeParams"]
  type hipChannelFormatDesc = CValue["hipChannelFormatDesc"]
  type hipCtx_t = CValue["hipCtx_t"]
  type hipDeviceAttribute_t = CValue["hipDeviceAttribute_t"]
  type hipDeviceP2PAttr = CValue["hipDeviceP2PAttr"]
  type hipDeviceProp_tR0600 = CValue["hipDeviceProp_tR0600"]
  type hipDevice_t = CValue["hipDevice_t"]
  type hipDeviceptr_t = CValue["hipDeviceptr_t"]
  type hipDriverEntryPointQueryResult = CValue["hipDriverEntryPointQueryResult"]
  type hipDriverProcAddressQueryResult = CValue["hipDriverProcAddressQueryResult"]
  type hipError_t = CValue["hipError_t"]
  type hipEvent_t = CValue["hipEvent_t"]
  type hipExtent = CValue["hipExtent"]
  type hipExternalMemoryBufferDesc = CValue["hipExternalMemoryBufferDesc"]
  type hipExternalMemoryHandleDesc = CValue["hipExternalMemoryHandleDesc"]
  type hipExternalMemoryMipmappedArrayDesc = CValue["hipExternalMemoryMipmappedArrayDesc"]
  type hipExternalMemory_t = CValue["hipExternalMemory_t"]
  type hipExternalSemaphoreHandleDesc = CValue["hipExternalSemaphoreHandleDesc"]
  type hipExternalSemaphoreSignalNodeParams = CValue["hipExternalSemaphoreSignalNodeParams"]
  type hipExternalSemaphoreSignalParams = CValue["hipExternalSemaphoreSignalParams"]
  type hipExternalSemaphoreWaitNodeParams = CValue["hipExternalSemaphoreWaitNodeParams"]
  type hipExternalSemaphoreWaitParams = CValue["hipExternalSemaphoreWaitParams"]
  type hipExternalSemaphore_t = CValue["hipExternalSemaphore_t"]
  type hipFuncAttribute = CValue["hipFuncAttribute"]
  type hipFuncCache_t = CValue["hipFuncCache_t"]
  type hipFunctionLaunchParams = CValue["hipFunctionLaunchParams"]
  type hipFunction_attribute = CValue["hipFunction_attribute"]
  type hipFunction_t = CValue["hipFunction_t"]
  type hipGraphEdgeData = CValue["hipGraphEdgeData"]
  type hipGraphExecUpdateResult = CValue["hipGraphExecUpdateResult"]
  type hipGraphExec_t = CValue["hipGraphExec_t"]
  type hipGraphInstantiateParams = CValue["hipGraphInstantiateParams"]
  type hipGraphMemAttributeType = CValue["hipGraphMemAttributeType"]
  type hipGraphNodeParams = CValue["hipGraphNodeParams"]
  type hipGraphNodeType = CValue["hipGraphNodeType"]
  type hipGraphNode_t = CValue["hipGraphNode_t"]
  type hipGraph_t = CValue["hipGraph_t"]
  type hipGraphicsResource_t = CValue["hipGraphicsResource_t"]
  type hipHostFn_t = CallbackTypedef["hipHostFn_t", Tuple1[OpaquePointer], Unit]
  type hipHostNodeParams = CValue["hipHostNodeParams"]
  type hipIpcEventHandle_t = CValue["hipIpcEventHandle_t"]
  type hipIpcMemHandle_t = CValue["hipIpcMemHandle_t"]
  type hipJitInputType = CValue["hipJitInputType"]
  type hipJitOption = CValue["hipJitOption"]
  type hipKernelNodeParams = CValue["hipKernelNodeParams"]
  type hipKernel_t = CValue["hipKernel_t"]
  type hipLaunchAttributeID = CValue["hipLaunchAttributeID"]
  type hipLaunchAttributeValue = CValue["hipLaunchAttributeValue"]
  type hipLaunchConfig_t = CValue["hipLaunchConfig_t"]
  type hipLaunchParams = CValue["hipLaunchParams"]
  type hipLibraryOption = CValue["hipLibraryOption"]
  type hipLibrary_t = CValue["hipLibrary_t"]
  type hipLinkState_t = CValue["hipLinkState_t"]
  type hipMemAccessDesc = CValue["hipMemAccessDesc"]
  type hipMemAccessFlags = CValue["hipMemAccessFlags"]
  type hipMemAllocNodeParams = CValue["hipMemAllocNodeParams"]
  type hipMemAllocationGranularity_flags = CValue["hipMemAllocationGranularity_flags"]
  type hipMemAllocationHandleType = CValue["hipMemAllocationHandleType"]
  type hipMemAllocationProp = CValue["hipMemAllocationProp"]
  type hipMemGenericAllocationHandle_t = CValue["hipMemGenericAllocationHandle_t"]
  type hipMemLocation = CValue["hipMemLocation"]
  type hipMemPoolAttr = CValue["hipMemPoolAttr"]
  type hipMemPoolProps = CValue["hipMemPoolProps"]
  type hipMemPoolPtrExportData = CValue["hipMemPoolPtrExportData"]
  type hipMemPool_t = CValue["hipMemPool_t"]
  type hipMemRangeAttribute = CValue["hipMemRangeAttribute"]
  type hipMemRangeHandleType = CValue["hipMemRangeHandleType"]
  type hipMemcpy3DParms = CValue["hipMemcpy3DParms"]
  type hipMemcpy3DPeerParms = CValue["hipMemcpy3DPeerParms"]
  type hipMemcpyAttributes = CValue["hipMemcpyAttributes"]
  type hipMemcpyKind = CValue["hipMemcpyKind"]
  type hipMemoryAdvise = CValue["hipMemoryAdvise"]
  type hipMemsetParams = CValue["hipMemsetParams"]
  type hipMipmappedArray_const_t = CValue["hipMipmappedArray_const_t"]
  type hipMipmappedArray_t = CValue["hipMipmappedArray_t"]
  type hipModule_t = CValue["hipModule_t"]
  type hipPitchedPtr = CValue["hipPitchedPtr"]
  type hipPointerAttribute_t = CValue["hipPointerAttribute_t"]
  type hipPointer_attribute = CValue["hipPointer_attribute"]
  type hipResourceDesc = CValue["hipResourceDesc"]
  type hipSharedMemConfig = CValue["hipSharedMemConfig"]
  type hipStreamBatchMemOpParams = CValue["hipStreamBatchMemOpParams"]
  type hipStreamCallback_t = CallbackTypedef["hipStreamCallback_t", (hipStream_t, hipError_t, OpaquePointer), Unit]
  type hipStreamCaptureMode = CValue["hipStreamCaptureMode"]
  type hipStreamCaptureStatus = CValue["hipStreamCaptureStatus"]
  type hipStream_t = CValue["hipStream_t"]
  type hipSurfaceObject_t = CValue["hipSurfaceObject_t"]
  type hipTextureDesc = CValue["hipTextureDesc"]
  type hipTextureObject_t = CValue["hipTextureObject_t"]
  type hipUUID = CValue["hipUUID"]
  type hipUserObject_t = CValue["hipUserObject_t"]
  type hip_Memcpy2D = CValue["hip_Memcpy2D"]
  type size_t = CValue["size_t"]
  type struct_hipChannelFormatDesc = CValue["struct hipChannelFormatDesc"]
  type struct_hipExtent = CValue["struct hipExtent"]
  type struct_hipFuncAttributes = CValue["struct hipFuncAttributes"]
  type struct_hipMemcpy3DBatchOp = CValue["struct hipMemcpy3DBatchOp"]
  type struct_hipMemcpy3DParms = CValue["struct hipMemcpy3DParms"]
  type struct_hipMipmappedArray = CValue["struct hipMipmappedArray"]
  type struct_hipResourceViewDesc = CValue["struct hipResourceViewDesc"]
  type textureReference = CValue["textureReference"]
  type uint32_t = CValue["uint32_t"]
  type uint64_t = CValue["uint64_t"]
  type unsigned_char = CValue["unsigned char"]
  type unsigned_int = CValue["unsigned int"]
  type unsigned_long_long = CValue["unsigned long long"]
  type unsigned_short = CValue["unsigned short"]

  val HeaderVersion: String :| (MinLength[1] & NoNul) = "7.1.1"
  val FunctionCount: Int :| Positive = 493
}

import HIP.*

trait HIP {
  def hipApiName(id: uint32_t): CString = unsupported
  def hipArray3DCreate(
      array: Pointer[hipArray_t, ReadWrite, Opaque],
      pAllocateArray: Pointer[HIP_ARRAY3D_DESCRIPTOR, Read, Opaque]
  ): hipError_t = unsupported
  def hipArray3DGetDescriptor(
      pArrayDescriptor: Pointer[HIP_ARRAY3D_DESCRIPTOR, ReadWrite, Opaque],
      array: hipArray_t
  ): hipError_t = unsupported
  def hipArrayCreate(
      pHandle: Pointer[hipArray_t, ReadWrite, Opaque],
      pAllocateArray: Pointer[HIP_ARRAY_DESCRIPTOR, Read, Opaque]
  ): hipError_t = unsupported
  def hipArrayDestroy(array: hipArray_t): hipError_t = unsupported
  def hipArrayGetDescriptor(
      pArrayDescriptor: Pointer[HIP_ARRAY_DESCRIPTOR, ReadWrite, Opaque],
      array: hipArray_t
  ): hipError_t = unsupported
  def hipArrayGetInfo(
      desc: Pointer[hipChannelFormatDesc, ReadWrite, Opaque],
      extent: Pointer[hipExtent, ReadWrite, Opaque],
      flags: Pointer[unsigned_int, ReadWrite, Opaque],
      array: hipArray_t
  ): hipError_t = unsupported
  def hipBindTexture(
      offset: Pointer[size_t, ReadWrite, Opaque],
      tex: Pointer[textureReference, Read, Opaque],
      devPtr: Pointer[CVoid, Read, Opaque],
      desc: Pointer[hipChannelFormatDesc, Read, Opaque],
      size: size_t
  ): hipError_t = unsupported
  def hipBindTexture2D(
      offset: Pointer[size_t, ReadWrite, Opaque],
      tex: Pointer[textureReference, Read, Opaque],
      devPtr: Pointer[CVoid, Read, Opaque],
      desc: Pointer[hipChannelFormatDesc, Read, Opaque],
      width: size_t,
      height: size_t,
      pitch: size_t
  ): hipError_t = unsupported
  def hipBindTextureToArray(
      tex: Pointer[textureReference, Read, Opaque],
      array: hipArray_const_t,
      desc: Pointer[hipChannelFormatDesc, Read, Opaque]
  ): hipError_t = unsupported
  def hipBindTextureToMipmappedArray(
      tex: Pointer[textureReference, Read, Opaque],
      mipmappedArray: hipMipmappedArray_const_t,
      desc: Pointer[hipChannelFormatDesc, Read, Opaque]
  ): hipError_t = unsupported
  def hipChooseDeviceR0600(
      device: Pointer[Int, ReadWrite, Opaque],
      prop: Pointer[hipDeviceProp_tR0600, Read, Opaque]
  ): hipError_t = unsupported
  def hipConfigureCall(gridDim: dim3, blockDim: dim3, sharedMem: size_t, stream: hipStream_t): hipError_t = unsupported
  def hipCreateChannelDesc(x: Int, y: Int, z: Int, w: Int, f: enum_hipChannelFormatKind): struct_hipChannelFormatDesc =
    unsupported
  def hipCreateSurfaceObject(
      pSurfObject: Pointer[hipSurfaceObject_t, ReadWrite, Opaque],
      pResDesc: Pointer[hipResourceDesc, Read, Opaque]
  ): hipError_t = unsupported
  def hipCreateTextureObject(
      pTexObject: Pointer[hipTextureObject_t, ReadWrite, Opaque],
      pResDesc: Pointer[hipResourceDesc, Read, Opaque],
      pTexDesc: Pointer[hipTextureDesc, Read, Opaque],
      pResViewDesc: Pointer[struct_hipResourceViewDesc, Read, Opaque]
  ): hipError_t = unsupported
  def hipCtxCreate(ctx: Pointer[hipCtx_t, ReadWrite, Opaque], flags: unsigned_int, device: hipDevice_t): hipError_t =
    unsupported
  def hipCtxDestroy(ctx: hipCtx_t): hipError_t = unsupported
  def hipCtxDisablePeerAccess(peerCtx: hipCtx_t): hipError_t = unsupported
  def hipCtxEnablePeerAccess(peerCtx: hipCtx_t, flags: unsigned_int): hipError_t = unsupported
  def hipCtxGetApiVersion(ctx: hipCtx_t, apiVersion: Pointer[unsigned_int, ReadWrite, Opaque]): hipError_t = unsupported
  def hipCtxGetCacheConfig(cacheConfig: Pointer[hipFuncCache_t, ReadWrite, Opaque]): hipError_t = unsupported
  def hipCtxGetCurrent(ctx: Pointer[hipCtx_t, ReadWrite, Opaque]): hipError_t = unsupported
  def hipCtxGetDevice(device: Pointer[hipDevice_t, ReadWrite, Opaque]): hipError_t = unsupported
  def hipCtxGetFlags(flags: Pointer[unsigned_int, ReadWrite, Opaque]): hipError_t = unsupported
  def hipCtxGetSharedMemConfig(pConfig: Pointer[hipSharedMemConfig, ReadWrite, Opaque]): hipError_t = unsupported
  def hipCtxPopCurrent(ctx: Pointer[hipCtx_t, ReadWrite, Opaque]): hipError_t = unsupported
  def hipCtxPushCurrent(ctx: hipCtx_t): hipError_t = unsupported
  def hipCtxSetCacheConfig(cacheConfig: hipFuncCache_t): hipError_t = unsupported
  def hipCtxSetCurrent(ctx: hipCtx_t): hipError_t = unsupported
  def hipCtxSetSharedMemConfig(config: hipSharedMemConfig): hipError_t = unsupported
  def hipCtxSynchronize(): hipError_t = unsupported
  def hipDestroyExternalMemory(extMem: hipExternalMemory_t): hipError_t = unsupported
  def hipDestroyExternalSemaphore(extSem: hipExternalSemaphore_t): hipError_t = unsupported
  def hipDestroySurfaceObject(surfaceObject: hipSurfaceObject_t): hipError_t = unsupported
  def hipDestroyTextureObject(textureObject: hipTextureObject_t): hipError_t = unsupported
  def hipDeviceCanAccessPeer(
      canAccessPeer: Pointer[Int, ReadWrite, Opaque],
      deviceId: Int,
      peerDeviceId: Int
  ): hipError_t = unsupported
  def hipDeviceComputeCapability(
      major: Pointer[Int, ReadWrite, Opaque],
      minor: Pointer[Int, ReadWrite, Opaque],
      device: hipDevice_t
  ): hipError_t = unsupported
  def hipDeviceDisablePeerAccess(peerDeviceId: Int): hipError_t = unsupported
  def hipDeviceEnablePeerAccess(peerDeviceId: Int, flags: unsigned_int): hipError_t = unsupported
  def hipDeviceGet(device: Pointer[hipDevice_t, ReadWrite, Opaque], ordinal: Int): hipError_t = unsupported
  def hipDeviceGetAttribute(
      pi: Pointer[Int, ReadWrite, Opaque],
      attr: hipDeviceAttribute_t,
      deviceId: Int
  ): hipError_t = unsupported
  def hipDeviceGetByPCIBusId(device: Pointer[Int, ReadWrite, Opaque], pciBusId: CString): hipError_t = unsupported
  def hipDeviceGetCacheConfig(cacheConfig: Pointer[hipFuncCache_t, ReadWrite, Opaque]): hipError_t = unsupported
  def hipDeviceGetDefaultMemPool(mem_pool: Pointer[hipMemPool_t, ReadWrite, Opaque], device: Int): hipError_t =
    unsupported
  def hipDeviceGetGraphMemAttribute(device: Int, attr: hipGraphMemAttributeType, value: OpaquePointer): hipError_t =
    unsupported
  def hipDeviceGetLimit(pValue: Pointer[size_t, ReadWrite, Opaque], limit: enum_hipLimit_t): hipError_t = unsupported
  def hipDeviceGetMemPool(mem_pool: Pointer[hipMemPool_t, ReadWrite, Opaque], device: Int): hipError_t = unsupported
  def hipDeviceGetName(name: Pointer[Byte, ReadWrite, Opaque], len: Int, device: hipDevice_t): hipError_t = unsupported
  def hipDeviceGetP2PAttribute(
      value: Pointer[Int, ReadWrite, Opaque],
      attr: hipDeviceP2PAttr,
      srcDevice: Int,
      dstDevice: Int
  ): hipError_t = unsupported
  def hipDeviceGetPCIBusId(pciBusId: Pointer[Byte, ReadWrite, Opaque], len: Int, device: Int): hipError_t = unsupported
  def hipDeviceGetSharedMemConfig(pConfig: Pointer[hipSharedMemConfig, ReadWrite, Opaque]): hipError_t = unsupported
  def hipDeviceGetStreamPriorityRange(
      leastPriority: Pointer[Int, ReadWrite, Opaque],
      greatestPriority: Pointer[Int, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipDeviceGetTexture1DLinearMaxWidth(
      max_width: Pointer[size_t, ReadWrite, Opaque],
      desc: Pointer[hipChannelFormatDesc, Read, Opaque],
      device: Int
  ): hipError_t = unsupported
  def hipDeviceGetUuid(uuid: Pointer[hipUUID, ReadWrite, Opaque], device: hipDevice_t): hipError_t = unsupported
  def hipDeviceGraphMemTrim(device: Int): hipError_t = unsupported
  def hipDevicePrimaryCtxGetState(
      dev: hipDevice_t,
      flags: Pointer[unsigned_int, ReadWrite, Opaque],
      active: Pointer[Int, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipDevicePrimaryCtxRelease(dev: hipDevice_t): hipError_t = unsupported
  def hipDevicePrimaryCtxReset(dev: hipDevice_t): hipError_t = unsupported
  def hipDevicePrimaryCtxRetain(pctx: Pointer[hipCtx_t, ReadWrite, Opaque], dev: hipDevice_t): hipError_t = unsupported
  def hipDevicePrimaryCtxSetFlags(dev: hipDevice_t, flags: unsigned_int): hipError_t = unsupported
  def hipDeviceReset(): hipError_t = unsupported
  def hipDeviceSetCacheConfig(cacheConfig: hipFuncCache_t): hipError_t = unsupported
  def hipDeviceSetGraphMemAttribute(device: Int, attr: hipGraphMemAttributeType, value: OpaquePointer): hipError_t =
    unsupported
  def hipDeviceSetLimit(limit: enum_hipLimit_t, value: size_t): hipError_t = unsupported
  def hipDeviceSetMemPool(device: Int, mem_pool: hipMemPool_t): hipError_t = unsupported
  def hipDeviceSetSharedMemConfig(config: hipSharedMemConfig): hipError_t = unsupported
  def hipDeviceSynchronize(): hipError_t = unsupported
  def hipDeviceTotalMem(bytes: Pointer[size_t, ReadWrite, Opaque], device: hipDevice_t): hipError_t = unsupported
  def hipDriverGetVersion(driverVersion: Pointer[Int, ReadWrite, Opaque]): hipError_t = unsupported
  def hipDrvGetErrorName(hipError: hipError_t, errorString: Pointer[CString, ReadWrite, Opaque]): hipError_t =
    unsupported
  def hipDrvGetErrorString(hipError: hipError_t, errorString: Pointer[CString, ReadWrite, Opaque]): hipError_t =
    unsupported
  def hipDrvGraphAddMemFreeNode(
      phGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      hGraph: hipGraph_t,
      dependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      dptr: hipDeviceptr_t
  ): hipError_t = unsupported
  def hipDrvGraphAddMemcpyNode(
      phGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      hGraph: hipGraph_t,
      dependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      copyParams: Pointer[HIP_MEMCPY3D, Read, Opaque],
      ctx: hipCtx_t
  ): hipError_t = unsupported
  def hipDrvGraphAddMemsetNode(
      phGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      hGraph: hipGraph_t,
      dependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      memsetParams: Pointer[hipMemsetParams, Read, Opaque],
      ctx: hipCtx_t
  ): hipError_t = unsupported
  def hipDrvGraphExecMemcpyNodeSetParams(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      copyParams: Pointer[HIP_MEMCPY3D, Read, Opaque],
      ctx: hipCtx_t
  ): hipError_t = unsupported
  def hipDrvGraphExecMemsetNodeSetParams(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      memsetParams: Pointer[hipMemsetParams, Read, Opaque],
      ctx: hipCtx_t
  ): hipError_t = unsupported
  def hipDrvGraphMemcpyNodeGetParams(
      hNode: hipGraphNode_t,
      nodeParams: Pointer[HIP_MEMCPY3D, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipDrvGraphMemcpyNodeSetParams(
      hNode: hipGraphNode_t,
      nodeParams: Pointer[HIP_MEMCPY3D, Read, Opaque]
  ): hipError_t = unsupported
  def hipDrvLaunchKernelEx(
      config: Pointer[HIP_LAUNCH_CONFIG, Read, Opaque],
      f: hipFunction_t,
      params: Pointer[OpaquePointer, ReadWrite, Opaque],
      extra: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipDrvMemcpy2DUnaligned(pCopy: Pointer[hip_Memcpy2D, Read, Opaque]): hipError_t = unsupported
  def hipDrvMemcpy3D(pCopy: Pointer[HIP_MEMCPY3D, Read, Opaque]): hipError_t = unsupported
  def hipDrvMemcpy3DAsync(pCopy: Pointer[HIP_MEMCPY3D, Read, Opaque], stream: hipStream_t): hipError_t = unsupported
  def hipDrvPointerGetAttributes(
      numAttributes: unsigned_int,
      attributes: Pointer[hipPointer_attribute, ReadWrite, Opaque],
      data: Pointer[OpaquePointer, ReadWrite, Opaque],
      ptr: hipDeviceptr_t
  ): hipError_t = unsupported
  def hipEventCreate(event: Pointer[hipEvent_t, ReadWrite, Opaque]): hipError_t = unsupported
  def hipEventCreateWithFlags(event: Pointer[hipEvent_t, ReadWrite, Opaque], flags: unsigned_int): hipError_t =
    unsupported
  def hipEventDestroy(event: hipEvent_t): hipError_t = unsupported
  def hipEventElapsedTime(ms: Pointer[Float, ReadWrite, Opaque], start: hipEvent_t, stop: hipEvent_t): hipError_t =
    unsupported
  def hipEventQuery(event: hipEvent_t): hipError_t = unsupported
  def hipEventRecord(event: hipEvent_t, stream: hipStream_t): hipError_t = unsupported
  def hipEventRecordWithFlags(event: hipEvent_t, stream: hipStream_t, flags: unsigned_int): hipError_t = unsupported
  def hipEventRecord_spt(event: hipEvent_t, stream: hipStream_t): hipError_t = unsupported
  def hipEventSynchronize(event: hipEvent_t): hipError_t = unsupported
  def hipExtGetLastError(): hipError_t = unsupported
  def hipExtGetLinkTypeAndHopCount(
      device1: Int,
      device2: Int,
      linktype: Pointer[uint32_t, ReadWrite, Opaque],
      hopcount: Pointer[uint32_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipExtLaunchKernel(
      function_address: Pointer[CVoid, Read, Opaque],
      numBlocks: dim3,
      dimBlocks: dim3,
      args: Pointer[OpaquePointer, ReadWrite, Opaque],
      sharedMemBytes: size_t,
      stream: hipStream_t,
      startEvent: hipEvent_t,
      stopEvent: hipEvent_t,
      flags: Int
  ): hipError_t = unsupported
  def hipExtLaunchMultiKernelMultiDevice(
      launchParamsList: Pointer[hipLaunchParams, ReadWrite, Opaque],
      numDevices: Int,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipExtMallocWithFlags(
      ptr: Pointer[OpaquePointer, ReadWrite, Opaque],
      sizeBytes: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipExtStreamCreateWithCUMask(
      stream: Pointer[hipStream_t, ReadWrite, Opaque],
      cuMaskSize: uint32_t,
      cuMask: Pointer[uint32_t, Read, Opaque]
  ): hipError_t = unsupported
  def hipExtStreamGetCUMask(
      stream: hipStream_t,
      cuMaskSize: uint32_t,
      cuMask: Pointer[uint32_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipExternalMemoryGetMappedBuffer(
      devPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      extMem: hipExternalMemory_t,
      bufferDesc: Pointer[hipExternalMemoryBufferDesc, Read, Opaque]
  ): hipError_t = unsupported
  def hipExternalMemoryGetMappedMipmappedArray(
      mipmap: Pointer[hipMipmappedArray_t, ReadWrite, Opaque],
      extMem: hipExternalMemory_t,
      mipmapDesc: Pointer[hipExternalMemoryMipmappedArrayDesc, Read, Opaque]
  ): hipError_t = unsupported
  def hipFree(ptr: OpaquePointer): hipError_t = unsupported
  def hipFreeArray(array: hipArray_t): hipError_t = unsupported
  def hipFreeAsync(dev_ptr: OpaquePointer, stream: hipStream_t): hipError_t = unsupported
  def hipFreeHost(ptr: OpaquePointer): hipError_t = unsupported
  def hipFreeMipmappedArray(mipmappedArray: hipMipmappedArray_t): hipError_t = unsupported
  def hipFuncGetAttribute(
      value: Pointer[Int, ReadWrite, Opaque],
      attrib: hipFunction_attribute,
      hfunc: hipFunction_t
  ): hipError_t = unsupported
  def hipFuncGetAttributes(
      attr: Pointer[struct_hipFuncAttributes, ReadWrite, Opaque],
      func: Pointer[CVoid, Read, Opaque]
  ): hipError_t = unsupported
  def hipFuncSetAttribute(func: Pointer[CVoid, Read, Opaque], attr: hipFuncAttribute, value: Int): hipError_t =
    unsupported
  def hipFuncSetCacheConfig(func: Pointer[CVoid, Read, Opaque], config: hipFuncCache_t): hipError_t = unsupported
  def hipFuncSetSharedMemConfig(func: Pointer[CVoid, Read, Opaque], config: hipSharedMemConfig): hipError_t =
    unsupported
  def hipGetChannelDesc(desc: Pointer[hipChannelFormatDesc, ReadWrite, Opaque], array: hipArray_const_t): hipError_t =
    unsupported
  def hipGetDevice(deviceId: Pointer[Int, ReadWrite, Opaque]): hipError_t = unsupported
  def hipGetDeviceCount(count: Pointer[Int, ReadWrite, Opaque]): hipError_t = unsupported
  def hipGetDeviceFlags(flags: Pointer[unsigned_int, ReadWrite, Opaque]): hipError_t = unsupported
  def hipGetDevicePropertiesR0600(prop: Pointer[hipDeviceProp_tR0600, ReadWrite, Opaque], deviceId: Int): hipError_t =
    unsupported
  def hipGetDriverEntryPoint(
      symbol: CString,
      funcPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      flags: unsigned_long_long,
      driverStatus: Pointer[hipDriverEntryPointQueryResult, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGetDriverEntryPoint_spt(
      symbol: CString,
      funcPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      flags: unsigned_long_long,
      status: Pointer[hipDriverEntryPointQueryResult, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGetErrorName(hip_error: hipError_t): CString = unsupported
  def hipGetErrorString(hipError: hipError_t): CString = unsupported
  def hipGetFuncBySymbol(
      functionPtr: Pointer[hipFunction_t, ReadWrite, Opaque],
      symbolPtr: Pointer[CVoid, Read, Opaque]
  ): hipError_t = unsupported
  def hipGetLastError(): hipError_t = unsupported
  def hipGetMipmappedArrayLevel(
      levelArray: Pointer[hipArray_t, ReadWrite, Opaque],
      mipmappedArray: hipMipmappedArray_const_t,
      level: unsigned_int
  ): hipError_t = unsupported
  def hipGetProcAddress(
      symbol: CString,
      pfn: Pointer[OpaquePointer, ReadWrite, Opaque],
      hipVersion: Int,
      flags: uint64_t,
      symbolStatus: Pointer[hipDriverProcAddressQueryResult, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGetStreamDeviceId(stream: hipStream_t): Int = unsupported
  def hipGetSymbolAddress(
      devPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      symbol: Pointer[CVoid, Read, Opaque]
  ): hipError_t = unsupported
  def hipGetSymbolSize(size: Pointer[size_t, ReadWrite, Opaque], symbol: Pointer[CVoid, Read, Opaque]): hipError_t =
    unsupported
  def hipGetTextureAlignmentOffset(
      offset: Pointer[size_t, ReadWrite, Opaque],
      texref: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  def hipGetTextureObjectResourceDesc(
      pResDesc: Pointer[hipResourceDesc, ReadWrite, Opaque],
      textureObject: hipTextureObject_t
  ): hipError_t = unsupported
  def hipGetTextureObjectResourceViewDesc(
      pResViewDesc: Pointer[struct_hipResourceViewDesc, ReadWrite, Opaque],
      textureObject: hipTextureObject_t
  ): hipError_t = unsupported
  def hipGetTextureObjectTextureDesc(
      pTexDesc: Pointer[hipTextureDesc, ReadWrite, Opaque],
      textureObject: hipTextureObject_t
  ): hipError_t = unsupported
  def hipGetTextureReference(
      texref: Pointer[Pointer[textureReference, Read, Opaque], ReadWrite, Opaque],
      symbol: Pointer[CVoid, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphAddBatchMemOpNode(
      phGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      hGraph: hipGraph_t,
      dependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[hipBatchMemOpNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphAddChildGraphNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      childGraph: hipGraph_t
  ): hipError_t = unsupported
  def hipGraphAddDependencies(
      graph: hipGraph_t,
      from: Pointer[hipGraphNode_t, Read, Opaque],
      to: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t
  ): hipError_t = unsupported
  def hipGraphAddEmptyNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t
  ): hipError_t = unsupported
  def hipGraphAddEventRecordNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      event: hipEvent_t
  ): hipError_t = unsupported
  def hipGraphAddEventWaitNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      event: hipEvent_t
  ): hipError_t = unsupported
  def hipGraphAddExternalSemaphoresSignalNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[hipExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphAddExternalSemaphoresWaitNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[hipExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphAddHostNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      pNodeParams: Pointer[hipHostNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphAddKernelNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      pNodeParams: Pointer[hipKernelNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphAddMemAllocNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      pNodeParams: Pointer[hipMemAllocNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphAddMemFreeNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      dev_ptr: OpaquePointer
  ): hipError_t = unsupported
  def hipGraphAddMemcpyNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      pCopyParams: Pointer[hipMemcpy3DParms, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphAddMemcpyNode1D(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      dst: OpaquePointer,
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipGraphAddMemcpyNodeFromSymbol(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      dst: OpaquePointer,
      symbol: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipGraphAddMemcpyNodeToSymbol(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipGraphAddMemsetNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      pMemsetParams: Pointer[hipMemsetParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphAddNode(
      pGraphNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pDependencies: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t,
      nodeParams: Pointer[hipGraphNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphBatchMemOpNodeGetParams(
      hNode: hipGraphNode_t,
      nodeParams_out: Pointer[hipBatchMemOpNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphBatchMemOpNodeSetParams(
      hNode: hipGraphNode_t,
      nodeParams: Pointer[hipBatchMemOpNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphChildGraphNodeGetGraph(node: hipGraphNode_t, pGraph: Pointer[hipGraph_t, ReadWrite, Opaque]): hipError_t =
    unsupported
  def hipGraphClone(pGraphClone: Pointer[hipGraph_t, ReadWrite, Opaque], originalGraph: hipGraph_t): hipError_t =
    unsupported
  def hipGraphCreate(pGraph: Pointer[hipGraph_t, ReadWrite, Opaque], flags: unsigned_int): hipError_t = unsupported
  def hipGraphDebugDotPrint(graph: hipGraph_t, path: CString, flags: unsigned_int): hipError_t = unsupported
  def hipGraphDestroy(graph: hipGraph_t): hipError_t = unsupported
  def hipGraphDestroyNode(node: hipGraphNode_t): hipError_t = unsupported
  def hipGraphEventRecordNodeGetEvent(
      node: hipGraphNode_t,
      event_out: Pointer[hipEvent_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphEventRecordNodeSetEvent(node: hipGraphNode_t, event: hipEvent_t): hipError_t = unsupported
  def hipGraphEventWaitNodeGetEvent(
      node: hipGraphNode_t,
      event_out: Pointer[hipEvent_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphEventWaitNodeSetEvent(node: hipGraphNode_t, event: hipEvent_t): hipError_t = unsupported
  def hipGraphExecBatchMemOpNodeSetParams(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      nodeParams: Pointer[hipBatchMemOpNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphExecChildGraphNodeSetParams(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      childGraph: hipGraph_t
  ): hipError_t = unsupported
  def hipGraphExecDestroy(graphExec: hipGraphExec_t): hipError_t = unsupported
  def hipGraphExecEventRecordNodeSetEvent(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      event: hipEvent_t
  ): hipError_t = unsupported
  def hipGraphExecEventWaitNodeSetEvent(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      event: hipEvent_t
  ): hipError_t = unsupported
  def hipGraphExecExternalSemaphoresSignalNodeSetParams(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      nodeParams: Pointer[hipExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphExecExternalSemaphoresWaitNodeSetParams(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      nodeParams: Pointer[hipExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphExecGetFlags(
      graphExec: hipGraphExec_t,
      flags: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphExecHostNodeSetParams(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipHostNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphExecKernelNodeSetParams(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipKernelNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphExecMemcpyNodeSetParams(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipMemcpy3DParms, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphExecMemcpyNodeSetParams1D(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      dst: OpaquePointer,
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipGraphExecMemcpyNodeSetParamsFromSymbol(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      dst: OpaquePointer,
      symbol: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipGraphExecMemcpyNodeSetParamsToSymbol(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipGraphExecMemsetNodeSetParams(
      hGraphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipMemsetParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphExecNodeSetParams(
      graphExec: hipGraphExec_t,
      node: hipGraphNode_t,
      nodeParams: Pointer[hipGraphNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphExecUpdate(
      hGraphExec: hipGraphExec_t,
      hGraph: hipGraph_t,
      hErrorNode_out: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      updateResult_out: Pointer[hipGraphExecUpdateResult, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphExternalSemaphoresSignalNodeGetParams(
      hNode: hipGraphNode_t,
      params_out: Pointer[hipExternalSemaphoreSignalNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphExternalSemaphoresSignalNodeSetParams(
      hNode: hipGraphNode_t,
      nodeParams: Pointer[hipExternalSemaphoreSignalNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphExternalSemaphoresWaitNodeGetParams(
      hNode: hipGraphNode_t,
      params_out: Pointer[hipExternalSemaphoreWaitNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphExternalSemaphoresWaitNodeSetParams(
      hNode: hipGraphNode_t,
      nodeParams: Pointer[hipExternalSemaphoreWaitNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphGetEdges(
      graph: hipGraph_t,
      from: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      to: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      numEdges: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphGetNodes(
      graph: hipGraph_t,
      nodes: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      numNodes: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphGetRootNodes(
      graph: hipGraph_t,
      pRootNodes: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      pNumRootNodes: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphHostNodeGetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipHostNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphHostNodeSetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipHostNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphInstantiate(
      pGraphExec: Pointer[hipGraphExec_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      pErrorNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      pLogBuffer: Pointer[Byte, ReadWrite, Opaque],
      bufferSize: size_t
  ): hipError_t = unsupported
  def hipGraphInstantiateWithFlags(
      pGraphExec: Pointer[hipGraphExec_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      flags: unsigned_long_long
  ): hipError_t = unsupported
  def hipGraphInstantiateWithParams(
      pGraphExec: Pointer[hipGraphExec_t, ReadWrite, Opaque],
      graph: hipGraph_t,
      instantiateParams: Pointer[hipGraphInstantiateParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphKernelNodeCopyAttributes(hSrc: hipGraphNode_t, hDst: hipGraphNode_t): hipError_t = unsupported
  def hipGraphKernelNodeGetAttribute(
      hNode: hipGraphNode_t,
      attr: hipLaunchAttributeID,
      value: Pointer[hipLaunchAttributeValue, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphKernelNodeGetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipKernelNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphKernelNodeSetAttribute(
      hNode: hipGraphNode_t,
      attr: hipLaunchAttributeID,
      value: Pointer[hipLaunchAttributeValue, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphKernelNodeSetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipKernelNodeParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphLaunch(graphExec: hipGraphExec_t, stream: hipStream_t): hipError_t = unsupported
  def hipGraphLaunch_spt(graphExec: hipGraphExec_t, stream: hipStream_t): hipError_t = unsupported
  def hipGraphMemAllocNodeGetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipMemAllocNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphMemFreeNodeGetParams(node: hipGraphNode_t, dev_ptr: OpaquePointer): hipError_t = unsupported
  def hipGraphMemcpyNodeGetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipMemcpy3DParms, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphMemcpyNodeSetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipMemcpy3DParms, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphMemcpyNodeSetParams1D(
      node: hipGraphNode_t,
      dst: OpaquePointer,
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipGraphMemcpyNodeSetParamsFromSymbol(
      node: hipGraphNode_t,
      dst: OpaquePointer,
      symbol: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipGraphMemcpyNodeSetParamsToSymbol(
      node: hipGraphNode_t,
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipGraphMemsetNodeGetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipMemsetParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphMemsetNodeSetParams(
      node: hipGraphNode_t,
      pNodeParams: Pointer[hipMemsetParams, Read, Opaque]
  ): hipError_t = unsupported
  def hipGraphNodeFindInClone(
      pNode: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      originalNode: hipGraphNode_t,
      clonedGraph: hipGraph_t
  ): hipError_t = unsupported
  def hipGraphNodeGetDependencies(
      node: hipGraphNode_t,
      pDependencies: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      pNumDependencies: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphNodeGetDependentNodes(
      node: hipGraphNode_t,
      pDependentNodes: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      pNumDependentNodes: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphNodeGetEnabled(
      hGraphExec: hipGraphExec_t,
      hNode: hipGraphNode_t,
      isEnabled: Pointer[unsigned_int, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphNodeGetType(node: hipGraphNode_t, pType: Pointer[hipGraphNodeType, ReadWrite, Opaque]): hipError_t =
    unsupported
  def hipGraphNodeSetEnabled(hGraphExec: hipGraphExec_t, hNode: hipGraphNode_t, isEnabled: unsigned_int): hipError_t =
    unsupported
  def hipGraphNodeSetParams(
      node: hipGraphNode_t,
      nodeParams: Pointer[hipGraphNodeParams, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipGraphReleaseUserObject(graph: hipGraph_t, `object`: hipUserObject_t, count: unsigned_int): hipError_t =
    unsupported
  def hipGraphRemoveDependencies(
      graph: hipGraph_t,
      from: Pointer[hipGraphNode_t, Read, Opaque],
      to: Pointer[hipGraphNode_t, Read, Opaque],
      numDependencies: size_t
  ): hipError_t = unsupported
  def hipGraphRetainUserObject(
      graph: hipGraph_t,
      `object`: hipUserObject_t,
      count: unsigned_int,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipGraphUpload(graphExec: hipGraphExec_t, stream: hipStream_t): hipError_t = unsupported
  def hipGraphicsMapResources(
      count: Int,
      resources: Pointer[hipGraphicsResource_t, ReadWrite, Opaque],
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipGraphicsResourceGetMappedPointer(
      devPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      size: Pointer[size_t, ReadWrite, Opaque],
      resource: hipGraphicsResource_t
  ): hipError_t = unsupported
  def hipGraphicsSubResourceGetMappedArray(
      array: Pointer[hipArray_t, ReadWrite, Opaque],
      resource: hipGraphicsResource_t,
      arrayIndex: unsigned_int,
      mipLevel: unsigned_int
  ): hipError_t = unsupported
  def hipGraphicsUnmapResources(
      count: Int,
      resources: Pointer[hipGraphicsResource_t, ReadWrite, Opaque],
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipGraphicsUnregisterResource(resource: hipGraphicsResource_t): hipError_t = unsupported
  def hipHostAlloc(ptr: Pointer[OpaquePointer, ReadWrite, Opaque], size: size_t, flags: unsigned_int): hipError_t =
    unsupported
  def hipHostFree(ptr: OpaquePointer): hipError_t = unsupported
  def hipHostGetDevicePointer(
      devPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      hstPtr: OpaquePointer,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipHostGetFlags(flagsPtr: Pointer[unsigned_int, ReadWrite, Opaque], hostPtr: OpaquePointer): hipError_t =
    unsupported
  def hipHostMalloc(ptr: Pointer[OpaquePointer, ReadWrite, Opaque], size: size_t, flags: unsigned_int): hipError_t =
    unsupported
  def hipHostRegister(hostPtr: OpaquePointer, sizeBytes: size_t, flags: unsigned_int): hipError_t = unsupported
  def hipHostUnregister(hostPtr: OpaquePointer): hipError_t = unsupported
  def hipImportExternalMemory(
      extMem_out: Pointer[hipExternalMemory_t, ReadWrite, Opaque],
      memHandleDesc: Pointer[hipExternalMemoryHandleDesc, Read, Opaque]
  ): hipError_t = unsupported
  def hipImportExternalSemaphore(
      extSem_out: Pointer[hipExternalSemaphore_t, ReadWrite, Opaque],
      semHandleDesc: Pointer[hipExternalSemaphoreHandleDesc, Read, Opaque]
  ): hipError_t = unsupported
  def hipInit(flags: unsigned_int): hipError_t = unsupported
  def hipIpcCloseMemHandle(devPtr: OpaquePointer): hipError_t = unsupported
  def hipIpcGetEventHandle(handle: Pointer[hipIpcEventHandle_t, ReadWrite, Opaque], event: hipEvent_t): hipError_t =
    unsupported
  def hipIpcGetMemHandle(handle: Pointer[hipIpcMemHandle_t, ReadWrite, Opaque], devPtr: OpaquePointer): hipError_t =
    unsupported
  def hipIpcOpenEventHandle(event: Pointer[hipEvent_t, ReadWrite, Opaque], handle: hipIpcEventHandle_t): hipError_t =
    unsupported
  def hipIpcOpenMemHandle(
      devPtr: Pointer[OpaquePointer, ReadWrite, Opaque],
      handle: hipIpcMemHandle_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipKernelNameRef(f: const_hipFunction_t): CString = unsupported
  def hipKernelNameRefByPtr(hostFunction: Pointer[CVoid, Read, Opaque], stream: hipStream_t): CString = unsupported
  def hipLaunchByPtr(func: Pointer[CVoid, Read, Opaque]): hipError_t = unsupported
  def hipLaunchCooperativeKernel(
      f: Pointer[CVoid, Read, Opaque],
      gridDim: dim3,
      blockDimX: dim3,
      kernelParams: Pointer[OpaquePointer, ReadWrite, Opaque],
      sharedMemBytes: unsigned_int,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipLaunchCooperativeKernelMultiDevice(
      launchParamsList: Pointer[hipLaunchParams, ReadWrite, Opaque],
      numDevices: Int,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipLaunchCooperativeKernel_spt(
      f: Pointer[CVoid, Read, Opaque],
      gridDim: dim3,
      blockDim: dim3,
      kernelParams: Pointer[OpaquePointer, ReadWrite, Opaque],
      sharedMemBytes: uint32_t,
      hStream: hipStream_t
  ): hipError_t = unsupported
  def hipLaunchHostFunc(stream: hipStream_t, fn: hipHostFn_t, userData: OpaquePointer): hipError_t = unsupported
  def hipLaunchHostFunc_spt(stream: hipStream_t, fn: hipHostFn_t, userData: OpaquePointer): hipError_t = unsupported
  def hipLaunchKernel(
      function_address: Pointer[CVoid, Read, Opaque],
      numBlocks: dim3,
      dimBlocks: dim3,
      args: Pointer[OpaquePointer, ReadWrite, Opaque],
      sharedMemBytes: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipLaunchKernelExC(
      config: Pointer[hipLaunchConfig_t, Read, Opaque],
      fPtr: Pointer[CVoid, Read, Opaque],
      args: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipLaunchKernel_spt(
      function_address: Pointer[CVoid, Read, Opaque],
      numBlocks: dim3,
      dimBlocks: dim3,
      args: Pointer[OpaquePointer, ReadWrite, Opaque],
      sharedMemBytes: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipLibraryGetKernel(
      pKernel: Pointer[hipKernel_t, ReadWrite, Opaque],
      library: hipLibrary_t,
      name: CString
  ): hipError_t = unsupported
  def hipLibraryGetKernelCount(count: Pointer[unsigned_int, ReadWrite, Opaque], library: hipLibrary_t): hipError_t =
    unsupported
  def hipLibraryLoadData(
      library: Pointer[hipLibrary_t, ReadWrite, Opaque],
      code: Pointer[CVoid, Read, Opaque],
      jitOptions: Pointer[hipJitOption, ReadWrite, Opaque],
      jitOptionsValues: Pointer[OpaquePointer, ReadWrite, Opaque],
      numJitOptions: unsigned_int,
      libraryOptions: Pointer[hipLibraryOption, ReadWrite, Opaque],
      libraryOptionValues: Pointer[OpaquePointer, ReadWrite, Opaque],
      numLibraryOptions: unsigned_int
  ): hipError_t = unsupported
  def hipLibraryLoadFromFile(
      library: Pointer[hipLibrary_t, ReadWrite, Opaque],
      fileName: CString,
      jitOptions: Pointer[hipJitOption, ReadWrite, Opaque],
      jitOptionsValues: Pointer[OpaquePointer, ReadWrite, Opaque],
      numJitOptions: unsigned_int,
      libraryOptions: Pointer[hipLibraryOption, ReadWrite, Opaque],
      libraryOptionValues: Pointer[OpaquePointer, ReadWrite, Opaque],
      numLibraryOptions: unsigned_int
  ): hipError_t = unsupported
  def hipLibraryUnload(library: hipLibrary_t): hipError_t = unsupported
  def hipLinkAddData(
      state: hipLinkState_t,
      `type`: hipJitInputType,
      data: OpaquePointer,
      size: size_t,
      name: CString,
      numOptions: unsigned_int,
      options: Pointer[hipJitOption, ReadWrite, Opaque],
      optionValues: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipLinkAddFile(
      state: hipLinkState_t,
      `type`: hipJitInputType,
      path: CString,
      numOptions: unsigned_int,
      options: Pointer[hipJitOption, ReadWrite, Opaque],
      optionValues: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipLinkComplete(
      state: hipLinkState_t,
      hipBinOut: Pointer[OpaquePointer, ReadWrite, Opaque],
      sizeOut: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipLinkCreate(
      numOptions: unsigned_int,
      options: Pointer[hipJitOption, ReadWrite, Opaque],
      optionValues: Pointer[OpaquePointer, ReadWrite, Opaque],
      stateOut: Pointer[hipLinkState_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipLinkDestroy(state: hipLinkState_t): hipError_t = unsupported
  def hipMalloc(ptr: Pointer[OpaquePointer, ReadWrite, Opaque], size: size_t): hipError_t = unsupported
  def hipMalloc3D(pitchedDevPtr: Pointer[hipPitchedPtr, ReadWrite, Opaque], extent: hipExtent): hipError_t = unsupported
  def hipMalloc3DArray(
      array: Pointer[hipArray_t, ReadWrite, Opaque],
      desc: Pointer[struct_hipChannelFormatDesc, Read, Opaque],
      extent: struct_hipExtent,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipMallocArray(
      array: Pointer[hipArray_t, ReadWrite, Opaque],
      desc: Pointer[hipChannelFormatDesc, Read, Opaque],
      width: size_t,
      height: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipMallocAsync(
      dev_ptr: Pointer[OpaquePointer, ReadWrite, Opaque],
      size: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMallocFromPoolAsync(
      dev_ptr: Pointer[OpaquePointer, ReadWrite, Opaque],
      size: size_t,
      mem_pool: hipMemPool_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMallocHost(ptr: Pointer[OpaquePointer, ReadWrite, Opaque], size: size_t): hipError_t = unsupported
  def hipMallocManaged(
      dev_ptr: Pointer[OpaquePointer, ReadWrite, Opaque],
      size: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipMallocMipmappedArray(
      mipmappedArray: Pointer[hipMipmappedArray_t, ReadWrite, Opaque],
      desc: Pointer[struct_hipChannelFormatDesc, Read, Opaque],
      extent: struct_hipExtent,
      numLevels: unsigned_int,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipMallocPitch(
      ptr: Pointer[OpaquePointer, ReadWrite, Opaque],
      pitch: Pointer[size_t, ReadWrite, Opaque],
      width: size_t,
      height: size_t
  ): hipError_t = unsupported
  def hipMemAddressFree(devPtr: OpaquePointer, size: size_t): hipError_t = unsupported
  def hipMemAddressReserve(
      ptr: Pointer[OpaquePointer, ReadWrite, Opaque],
      size: size_t,
      alignment: size_t,
      addr: OpaquePointer,
      flags: unsigned_long_long
  ): hipError_t = unsupported
  def hipMemAdvise(
      dev_ptr: Pointer[CVoid, Read, Opaque],
      count: size_t,
      advice: hipMemoryAdvise,
      device: Int
  ): hipError_t = unsupported
  def hipMemAdvise_v2(
      dev_ptr: Pointer[CVoid, Read, Opaque],
      count: size_t,
      advice: hipMemoryAdvise,
      location: hipMemLocation
  ): hipError_t = unsupported
  def hipMemAllocHost(ptr: Pointer[OpaquePointer, ReadWrite, Opaque], size: size_t): hipError_t = unsupported
  def hipMemAllocPitch(
      dptr: Pointer[hipDeviceptr_t, ReadWrite, Opaque],
      pitch: Pointer[size_t, ReadWrite, Opaque],
      widthInBytes: size_t,
      height: size_t,
      elementSizeBytes: unsigned_int
  ): hipError_t = unsupported
  def hipMemCreate(
      handle: Pointer[hipMemGenericAllocationHandle_t, ReadWrite, Opaque],
      size: size_t,
      prop: Pointer[hipMemAllocationProp, Read, Opaque],
      flags: unsigned_long_long
  ): hipError_t = unsupported
  def hipMemExportToShareableHandle(
      shareableHandle: OpaquePointer,
      handle: hipMemGenericAllocationHandle_t,
      handleType: hipMemAllocationHandleType,
      flags: unsigned_long_long
  ): hipError_t = unsupported
  def hipMemGetAccess(
      flags: Pointer[unsigned_long_long, ReadWrite, Opaque],
      location: Pointer[hipMemLocation, Read, Opaque],
      ptr: OpaquePointer
  ): hipError_t = unsupported
  def hipMemGetAddressRange(
      pbase: Pointer[hipDeviceptr_t, ReadWrite, Opaque],
      psize: Pointer[size_t, ReadWrite, Opaque],
      dptr: hipDeviceptr_t
  ): hipError_t = unsupported
  def hipMemGetAllocationGranularity(
      granularity: Pointer[size_t, ReadWrite, Opaque],
      prop: Pointer[hipMemAllocationProp, Read, Opaque],
      option: hipMemAllocationGranularity_flags
  ): hipError_t = unsupported
  def hipMemGetAllocationPropertiesFromHandle(
      prop: Pointer[hipMemAllocationProp, ReadWrite, Opaque],
      handle: hipMemGenericAllocationHandle_t
  ): hipError_t = unsupported
  def hipMemGetHandleForAddressRange(
      handle: OpaquePointer,
      dptr: hipDeviceptr_t,
      size: size_t,
      handleType: hipMemRangeHandleType,
      flags: unsigned_long_long
  ): hipError_t = unsupported
  def hipMemGetInfo(free: Pointer[size_t, ReadWrite, Opaque], total: Pointer[size_t, ReadWrite, Opaque]): hipError_t =
    unsupported
  def hipMemImportFromShareableHandle(
      handle: Pointer[hipMemGenericAllocationHandle_t, ReadWrite, Opaque],
      osHandle: OpaquePointer,
      shHandleType: hipMemAllocationHandleType
  ): hipError_t = unsupported
  def hipMemMap(
      ptr: OpaquePointer,
      size: size_t,
      offset: size_t,
      handle: hipMemGenericAllocationHandle_t,
      flags: unsigned_long_long
  ): hipError_t = unsupported
  def hipMemMapArrayAsync(
      mapInfoList: Pointer[hipArrayMapInfo, ReadWrite, Opaque],
      count: unsigned_int,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemPoolCreate(
      mem_pool: Pointer[hipMemPool_t, ReadWrite, Opaque],
      pool_props: Pointer[hipMemPoolProps, Read, Opaque]
  ): hipError_t = unsupported
  def hipMemPoolDestroy(mem_pool: hipMemPool_t): hipError_t = unsupported
  def hipMemPoolExportPointer(
      export_data: Pointer[hipMemPoolPtrExportData, ReadWrite, Opaque],
      dev_ptr: OpaquePointer
  ): hipError_t = unsupported
  def hipMemPoolExportToShareableHandle(
      shared_handle: OpaquePointer,
      mem_pool: hipMemPool_t,
      handle_type: hipMemAllocationHandleType,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipMemPoolGetAccess(
      flags: Pointer[hipMemAccessFlags, ReadWrite, Opaque],
      mem_pool: hipMemPool_t,
      location: Pointer[hipMemLocation, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipMemPoolGetAttribute(mem_pool: hipMemPool_t, attr: hipMemPoolAttr, value: OpaquePointer): hipError_t =
    unsupported
  def hipMemPoolImportFromShareableHandle(
      mem_pool: Pointer[hipMemPool_t, ReadWrite, Opaque],
      shared_handle: OpaquePointer,
      handle_type: hipMemAllocationHandleType,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipMemPoolImportPointer(
      dev_ptr: Pointer[OpaquePointer, ReadWrite, Opaque],
      mem_pool: hipMemPool_t,
      export_data: Pointer[hipMemPoolPtrExportData, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipMemPoolSetAccess(
      mem_pool: hipMemPool_t,
      desc_list: Pointer[hipMemAccessDesc, Read, Opaque],
      count: size_t
  ): hipError_t = unsupported
  def hipMemPoolSetAttribute(mem_pool: hipMemPool_t, attr: hipMemPoolAttr, value: OpaquePointer): hipError_t =
    unsupported
  def hipMemPoolTrimTo(mem_pool: hipMemPool_t, min_bytes_to_hold: size_t): hipError_t = unsupported
  def hipMemPrefetchAsync(
      dev_ptr: Pointer[CVoid, Read, Opaque],
      count: size_t,
      device: Int,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemPrefetchAsync_v2(
      dev_ptr: Pointer[CVoid, Read, Opaque],
      count: size_t,
      location: hipMemLocation,
      flags: unsigned_int,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemPtrGetInfo(ptr: OpaquePointer, size: Pointer[size_t, ReadWrite, Opaque]): hipError_t = unsupported
  def hipMemRangeGetAttribute(
      data: OpaquePointer,
      data_size: size_t,
      attribute: hipMemRangeAttribute,
      dev_ptr: Pointer[CVoid, Read, Opaque],
      count: size_t
  ): hipError_t = unsupported
  def hipMemRangeGetAttributes(
      data: Pointer[OpaquePointer, ReadWrite, Opaque],
      data_sizes: Pointer[size_t, ReadWrite, Opaque],
      attributes: Pointer[hipMemRangeAttribute, ReadWrite, Opaque],
      num_attributes: size_t,
      dev_ptr: Pointer[CVoid, Read, Opaque],
      count: size_t
  ): hipError_t = unsupported
  def hipMemRelease(handle: hipMemGenericAllocationHandle_t): hipError_t = unsupported
  def hipMemRetainAllocationHandle(
      handle: Pointer[hipMemGenericAllocationHandle_t, ReadWrite, Opaque],
      addr: OpaquePointer
  ): hipError_t = unsupported
  def hipMemSetAccess(
      ptr: OpaquePointer,
      size: size_t,
      desc: Pointer[hipMemAccessDesc, Read, Opaque],
      count: size_t
  ): hipError_t = unsupported
  def hipMemUnmap(ptr: OpaquePointer, size: size_t): hipError_t = unsupported
  def hipMemcpy(
      dst: OpaquePointer,
      src: Pointer[CVoid, Read, Opaque],
      sizeBytes: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipMemcpy2D(
      dst: OpaquePointer,
      dpitch: size_t,
      src: Pointer[CVoid, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
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
  def hipMemcpy2DAsync(
      dst: OpaquePointer,
      dpitch: size_t,
      src: Pointer[CVoid, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpy2DAsync_spt(
      dst: OpaquePointer,
      dpitch: size_t,
      src: Pointer[CVoid, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpy2DFromArray(
      dst: OpaquePointer,
      dpitch: size_t,
      src: hipArray_const_t,
      wOffset: size_t,
      hOffset: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipMemcpy2DFromArrayAsync(
      dst: OpaquePointer,
      dpitch: size_t,
      src: hipArray_const_t,
      wOffset: size_t,
      hOffset: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpy2DFromArrayAsync_spt(
      dst: OpaquePointer,
      dpitch: size_t,
      src: hipArray_const_t,
      wOffsetSrc: size_t,
      hOffsetSrc: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpy2DFromArray_spt(
      dst: OpaquePointer,
      dpitch: size_t,
      src: hipArray_const_t,
      wOffset: size_t,
      hOffset: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipMemcpy2DToArray(
      dst: hipArray_t,
      wOffset: size_t,
      hOffset: size_t,
      src: Pointer[CVoid, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipMemcpy2DToArrayAsync(
      dst: hipArray_t,
      wOffset: size_t,
      hOffset: size_t,
      src: Pointer[CVoid, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpy2DToArrayAsync_spt(
      dst: hipArray_t,
      wOffset: size_t,
      hOffset: size_t,
      src: Pointer[CVoid, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpy2DToArray_spt(
      dst: hipArray_t,
      wOffset: size_t,
      hOffset: size_t,
      src: Pointer[CVoid, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipMemcpy2D_spt(
      dst: OpaquePointer,
      dpitch: size_t,
      src: Pointer[CVoid, Read, Opaque],
      spitch: size_t,
      width: size_t,
      height: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipMemcpy3D(p: Pointer[struct_hipMemcpy3DParms, Read, Opaque]): hipError_t = unsupported
  def hipMemcpy3DAsync(p: Pointer[struct_hipMemcpy3DParms, Read, Opaque], stream: hipStream_t): hipError_t = unsupported
  def hipMemcpy3DAsync_spt(p: Pointer[hipMemcpy3DParms, Read, Opaque], stream: hipStream_t): hipError_t = unsupported
  def hipMemcpy3DBatchAsync(
      numOps: size_t,
      opList: Pointer[struct_hipMemcpy3DBatchOp, ReadWrite, Opaque],
      failIdx: Pointer[size_t, ReadWrite, Opaque],
      flags: unsigned_long_long,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpy3DPeer(p: Pointer[hipMemcpy3DPeerParms, ReadWrite, Opaque]): hipError_t = unsupported
  def hipMemcpy3DPeerAsync(p: Pointer[hipMemcpy3DPeerParms, ReadWrite, Opaque], stream: hipStream_t): hipError_t =
    unsupported
  def hipMemcpy3D_spt(p: Pointer[struct_hipMemcpy3DParms, Read, Opaque]): hipError_t = unsupported
  def hipMemcpyAsync(
      dst: OpaquePointer,
      src: Pointer[CVoid, Read, Opaque],
      sizeBytes: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpyAsync_spt(
      dst: OpaquePointer,
      src: Pointer[CVoid, Read, Opaque],
      sizeBytes: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpyAtoA(
      dstArray: hipArray_t,
      dstOffset: size_t,
      srcArray: hipArray_t,
      srcOffset: size_t,
      ByteCount: size_t
  ): hipError_t = unsupported
  def hipMemcpyAtoD(dstDevice: hipDeviceptr_t, srcArray: hipArray_t, srcOffset: size_t, ByteCount: size_t): hipError_t =
    unsupported
  def hipMemcpyAtoH(dst: OpaquePointer, srcArray: hipArray_t, srcOffset: size_t, count: size_t): hipError_t =
    unsupported
  def hipMemcpyAtoHAsync(
      dstHost: OpaquePointer,
      srcArray: hipArray_t,
      srcOffset: size_t,
      ByteCount: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpyBatchAsync(
      dsts: Pointer[OpaquePointer, ReadWrite, Opaque],
      srcs: Pointer[OpaquePointer, ReadWrite, Opaque],
      sizes: Pointer[size_t, ReadWrite, Opaque],
      count: size_t,
      attrs: Pointer[hipMemcpyAttributes, ReadWrite, Opaque],
      attrsIdxs: Pointer[size_t, ReadWrite, Opaque],
      numAttrs: size_t,
      failIdx: Pointer[size_t, ReadWrite, Opaque],
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpyDtoA(dstArray: hipArray_t, dstOffset: size_t, srcDevice: hipDeviceptr_t, ByteCount: size_t): hipError_t =
    unsupported
  def hipMemcpyDtoD(dst: hipDeviceptr_t, src: hipDeviceptr_t, sizeBytes: size_t): hipError_t = unsupported
  def hipMemcpyDtoDAsync(dst: hipDeviceptr_t, src: hipDeviceptr_t, sizeBytes: size_t, stream: hipStream_t): hipError_t =
    unsupported
  def hipMemcpyDtoH(dst: OpaquePointer, src: hipDeviceptr_t, sizeBytes: size_t): hipError_t = unsupported
  def hipMemcpyDtoHAsync(dst: OpaquePointer, src: hipDeviceptr_t, sizeBytes: size_t, stream: hipStream_t): hipError_t =
    unsupported
  def hipMemcpyFromArray(
      dst: OpaquePointer,
      srcArray: hipArray_const_t,
      wOffset: size_t,
      hOffset: size_t,
      count: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipMemcpyFromArray_spt(
      dst: OpaquePointer,
      src: hipArray_const_t,
      wOffsetSrc: size_t,
      hOffset: size_t,
      count: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipMemcpyFromSymbol(
      dst: OpaquePointer,
      symbol: Pointer[CVoid, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipMemcpyFromSymbolAsync(
      dst: OpaquePointer,
      symbol: Pointer[CVoid, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpyFromSymbolAsync_spt(
      dst: OpaquePointer,
      symbol: Pointer[CVoid, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpyFromSymbol_spt(
      dst: OpaquePointer,
      symbol: Pointer[CVoid, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipMemcpyHtoA(
      dstArray: hipArray_t,
      dstOffset: size_t,
      srcHost: Pointer[CVoid, Read, Opaque],
      count: size_t
  ): hipError_t = unsupported
  def hipMemcpyHtoAAsync(
      dstArray: hipArray_t,
      dstOffset: size_t,
      srcHost: Pointer[CVoid, Read, Opaque],
      ByteCount: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpyHtoD(dst: hipDeviceptr_t, src: Pointer[CVoid, Read, Opaque], sizeBytes: size_t): hipError_t = unsupported
  def hipMemcpyHtoDAsync(
      dst: hipDeviceptr_t,
      src: Pointer[CVoid, Read, Opaque],
      sizeBytes: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpyParam2D(pCopy: Pointer[hip_Memcpy2D, Read, Opaque]): hipError_t = unsupported
  def hipMemcpyParam2DAsync(pCopy: Pointer[hip_Memcpy2D, Read, Opaque], stream: hipStream_t): hipError_t = unsupported
  def hipMemcpyPeer(
      dst: OpaquePointer,
      dstDeviceId: Int,
      src: Pointer[CVoid, Read, Opaque],
      srcDeviceId: Int,
      sizeBytes: size_t
  ): hipError_t = unsupported
  def hipMemcpyPeerAsync(
      dst: OpaquePointer,
      dstDeviceId: Int,
      src: Pointer[CVoid, Read, Opaque],
      srcDevice: Int,
      sizeBytes: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpyToArray(
      dst: hipArray_t,
      wOffset: size_t,
      hOffset: size_t,
      src: Pointer[CVoid, Read, Opaque],
      count: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipMemcpyToSymbol(
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipMemcpyToSymbolAsync(
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpyToSymbolAsync_spt(
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpyToSymbol_spt(
      symbol: Pointer[CVoid, Read, Opaque],
      src: Pointer[CVoid, Read, Opaque],
      sizeBytes: size_t,
      offset: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipMemcpyWithStream(
      dst: OpaquePointer,
      src: Pointer[CVoid, Read, Opaque],
      sizeBytes: size_t,
      kind: hipMemcpyKind,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemcpy_spt(
      dst: OpaquePointer,
      src: Pointer[CVoid, Read, Opaque],
      sizeBytes: size_t,
      kind: hipMemcpyKind
  ): hipError_t = unsupported
  def hipMemset(dst: OpaquePointer, value: Int, sizeBytes: size_t): hipError_t = unsupported
  def hipMemset2D(dst: OpaquePointer, pitch: size_t, value: Int, width: size_t, height: size_t): hipError_t =
    unsupported
  def hipMemset2DAsync(
      dst: OpaquePointer,
      pitch: size_t,
      value: Int,
      width: size_t,
      height: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemset2DAsync_spt(
      dst: OpaquePointer,
      pitch: size_t,
      value: Int,
      width: size_t,
      height: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemset2D_spt(dst: OpaquePointer, pitch: size_t, value: Int, width: size_t, height: size_t): hipError_t =
    unsupported
  def hipMemset3D(pitchedDevPtr: hipPitchedPtr, value: Int, extent: hipExtent): hipError_t = unsupported
  def hipMemset3DAsync(pitchedDevPtr: hipPitchedPtr, value: Int, extent: hipExtent, stream: hipStream_t): hipError_t =
    unsupported
  def hipMemset3DAsync_spt(
      pitchedDevPtr: hipPitchedPtr,
      value: Int,
      extent: hipExtent,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemset3D_spt(pitchedDevPtr: hipPitchedPtr, value: Int, extent: hipExtent): hipError_t = unsupported
  def hipMemsetAsync(dst: OpaquePointer, value: Int, sizeBytes: size_t, stream: hipStream_t): hipError_t = unsupported
  def hipMemsetAsync_spt(dst: OpaquePointer, value: Int, sizeBytes: size_t, stream: hipStream_t): hipError_t =
    unsupported
  def hipMemsetD16(dest: hipDeviceptr_t, value: unsigned_short, count: size_t): hipError_t = unsupported
  def hipMemsetD16Async(dest: hipDeviceptr_t, value: unsigned_short, count: size_t, stream: hipStream_t): hipError_t =
    unsupported
  def hipMemsetD2D16(
      dst: hipDeviceptr_t,
      dstPitch: size_t,
      value: unsigned_short,
      width: size_t,
      height: size_t
  ): hipError_t = unsupported
  def hipMemsetD2D16Async(
      dst: hipDeviceptr_t,
      dstPitch: size_t,
      value: unsigned_short,
      width: size_t,
      height: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemsetD2D32(
      dst: hipDeviceptr_t,
      dstPitch: size_t,
      value: unsigned_int,
      width: size_t,
      height: size_t
  ): hipError_t = unsupported
  def hipMemsetD2D32Async(
      dst: hipDeviceptr_t,
      dstPitch: size_t,
      value: unsigned_int,
      width: size_t,
      height: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemsetD2D8(
      dst: hipDeviceptr_t,
      dstPitch: size_t,
      value: unsigned_char,
      width: size_t,
      height: size_t
  ): hipError_t = unsupported
  def hipMemsetD2D8Async(
      dst: hipDeviceptr_t,
      dstPitch: size_t,
      value: unsigned_char,
      width: size_t,
      height: size_t,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipMemsetD32(dest: hipDeviceptr_t, value: Int, count: size_t): hipError_t = unsupported
  def hipMemsetD32Async(dst: hipDeviceptr_t, value: Int, count: size_t, stream: hipStream_t): hipError_t = unsupported
  def hipMemsetD8(dest: hipDeviceptr_t, value: unsigned_char, count: size_t): hipError_t = unsupported
  def hipMemsetD8Async(dest: hipDeviceptr_t, value: unsigned_char, count: size_t, stream: hipStream_t): hipError_t =
    unsupported
  def hipMemset_spt(dst: OpaquePointer, value: Int, sizeBytes: size_t): hipError_t = unsupported
  def hipMipmappedArrayCreate(
      pHandle: Pointer[hipMipmappedArray_t, ReadWrite, Opaque],
      pMipmappedArrayDesc: Pointer[HIP_ARRAY3D_DESCRIPTOR, ReadWrite, Opaque],
      numMipmapLevels: unsigned_int
  ): hipError_t = unsupported
  def hipMipmappedArrayDestroy(hMipmappedArray: hipMipmappedArray_t): hipError_t = unsupported
  def hipMipmappedArrayGetLevel(
      pLevelArray: Pointer[hipArray_t, ReadWrite, Opaque],
      hMipMappedArray: hipMipmappedArray_t,
      level: unsigned_int
  ): hipError_t = unsupported
  def hipModuleGetFunction(
      function: Pointer[hipFunction_t, ReadWrite, Opaque],
      module: hipModule_t,
      kname: CString
  ): hipError_t = unsupported
  def hipModuleGetFunctionCount(count: Pointer[unsigned_int, ReadWrite, Opaque], mod: hipModule_t): hipError_t =
    unsupported
  def hipModuleGetGlobal(
      dptr: Pointer[hipDeviceptr_t, ReadWrite, Opaque],
      bytes: Pointer[size_t, ReadWrite, Opaque],
      hmod: hipModule_t,
      name: CString
  ): hipError_t = unsupported
  def hipModuleGetTexRef(
      texRef: Pointer[Pointer[textureReference, ReadWrite, Opaque], ReadWrite, Opaque],
      hmod: hipModule_t,
      name: CString
  ): hipError_t = unsupported
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
      kernelParams: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipModuleLaunchCooperativeKernelMultiDevice(
      launchParamsList: Pointer[hipFunctionLaunchParams, ReadWrite, Opaque],
      numDevices: unsigned_int,
      flags: unsigned_int
  ): hipError_t = unsupported
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
      kernelParams: Pointer[OpaquePointer, ReadWrite, Opaque],
      extra: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipModuleLoad(module: Pointer[hipModule_t, ReadWrite, Opaque], fname: CString): hipError_t = unsupported
  def hipModuleLoadData(
      module: Pointer[hipModule_t, ReadWrite, Opaque],
      image: Pointer[CVoid, Read, Opaque]
  ): hipError_t = unsupported
  def hipModuleLoadDataEx(
      module: Pointer[hipModule_t, ReadWrite, Opaque],
      image: Pointer[CVoid, Read, Opaque],
      numOptions: unsigned_int,
      options: Pointer[hipJitOption, ReadWrite, Opaque],
      optionValues: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipModuleLoadFatBinary(
      module: Pointer[hipModule_t, ReadWrite, Opaque],
      fatbin: Pointer[CVoid, Read, Opaque]
  ): hipError_t = unsupported
  def hipModuleOccupancyMaxActiveBlocksPerMultiprocessor(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      f: hipFunction_t,
      blockSize: Int,
      dynSharedMemPerBlk: size_t
  ): hipError_t = unsupported
  def hipModuleOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      f: hipFunction_t,
      blockSize: Int,
      dynSharedMemPerBlk: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipModuleOccupancyMaxPotentialBlockSize(
      gridSize: Pointer[Int, ReadWrite, Opaque],
      blockSize: Pointer[Int, ReadWrite, Opaque],
      f: hipFunction_t,
      dynSharedMemPerBlk: size_t,
      blockSizeLimit: Int
  ): hipError_t = unsupported
  def hipModuleOccupancyMaxPotentialBlockSizeWithFlags(
      gridSize: Pointer[Int, ReadWrite, Opaque],
      blockSize: Pointer[Int, ReadWrite, Opaque],
      f: hipFunction_t,
      dynSharedMemPerBlk: size_t,
      blockSizeLimit: Int,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipModuleUnload(module: hipModule_t): hipError_t = unsupported
  def hipOccupancyMaxActiveBlocksPerMultiprocessor(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      f: Pointer[CVoid, Read, Opaque],
      blockSize: Int,
      dynSharedMemPerBlk: size_t
  ): hipError_t = unsupported
  def hipOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(
      numBlocks: Pointer[Int, ReadWrite, Opaque],
      f: Pointer[CVoid, Read, Opaque],
      blockSize: Int,
      dynSharedMemPerBlk: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipOccupancyMaxPotentialBlockSize(
      gridSize: Pointer[Int, ReadWrite, Opaque],
      blockSize: Pointer[Int, ReadWrite, Opaque],
      f: Pointer[CVoid, Read, Opaque],
      dynSharedMemPerBlk: size_t,
      blockSizeLimit: Int
  ): hipError_t = unsupported
  def hipPeekAtLastError(): hipError_t = unsupported
  def hipPointerGetAttribute(data: OpaquePointer, attribute: hipPointer_attribute, ptr: hipDeviceptr_t): hipError_t =
    unsupported
  def hipPointerGetAttributes(
      attributes: Pointer[hipPointerAttribute_t, ReadWrite, Opaque],
      ptr: Pointer[CVoid, Read, Opaque]
  ): hipError_t = unsupported
  def hipPointerSetAttribute(
      value: Pointer[CVoid, Read, Opaque],
      attribute: hipPointer_attribute,
      ptr: hipDeviceptr_t
  ): hipError_t = unsupported
  def hipProfilerStart(): hipError_t = unsupported
  def hipProfilerStop(): hipError_t = unsupported
  def hipRuntimeGetVersion(runtimeVersion: Pointer[Int, ReadWrite, Opaque]): hipError_t = unsupported
  def hipSetDevice(deviceId: Int): hipError_t = unsupported
  def hipSetDeviceFlags(flags: unsigned_int): hipError_t = unsupported
  def hipSetValidDevices(device_arr: Pointer[Int, ReadWrite, Opaque], len: Int): hipError_t = unsupported
  def hipSetupArgument(arg: Pointer[CVoid, Read, Opaque], size: size_t, offset: size_t): hipError_t = unsupported
  def hipSignalExternalSemaphoresAsync(
      extSemArray: Pointer[hipExternalSemaphore_t, Read, Opaque],
      paramsArray: Pointer[hipExternalSemaphoreSignalParams, Read, Opaque],
      numExtSems: unsigned_int,
      stream: hipStream_t
  ): hipError_t = unsupported
  def hipStreamAddCallback(
      stream: hipStream_t,
      callback: hipStreamCallback_t,
      userData: OpaquePointer,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipStreamAddCallback_spt(
      stream: hipStream_t,
      callback: hipStreamCallback_t,
      userData: OpaquePointer,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipStreamAttachMemAsync(
      stream: hipStream_t,
      dev_ptr: OpaquePointer,
      length: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipStreamBatchMemOp(
      stream: hipStream_t,
      count: unsigned_int,
      paramArray: Pointer[hipStreamBatchMemOpParams, ReadWrite, Opaque],
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipStreamBeginCapture(stream: hipStream_t, mode: hipStreamCaptureMode): hipError_t = unsupported
  def hipStreamBeginCaptureToGraph(
      stream: hipStream_t,
      graph: hipGraph_t,
      dependencies: Pointer[hipGraphNode_t, Read, Opaque],
      dependencyData: Pointer[hipGraphEdgeData, Read, Opaque],
      numDependencies: size_t,
      mode: hipStreamCaptureMode
  ): hipError_t = unsupported
  def hipStreamBeginCapture_spt(stream: hipStream_t, mode: hipStreamCaptureMode): hipError_t = unsupported
  def hipStreamCreate(stream: Pointer[hipStream_t, ReadWrite, Opaque]): hipError_t = unsupported
  def hipStreamCreateWithFlags(stream: Pointer[hipStream_t, ReadWrite, Opaque], flags: unsigned_int): hipError_t =
    unsupported
  def hipStreamCreateWithPriority(
      stream: Pointer[hipStream_t, ReadWrite, Opaque],
      flags: unsigned_int,
      priority: Int
  ): hipError_t = unsupported
  def hipStreamDestroy(stream: hipStream_t): hipError_t = unsupported
  def hipStreamEndCapture(stream: hipStream_t, pGraph: Pointer[hipGraph_t, ReadWrite, Opaque]): hipError_t = unsupported
  def hipStreamEndCapture_spt(stream: hipStream_t, pGraph: Pointer[hipGraph_t, ReadWrite, Opaque]): hipError_t =
    unsupported
  def hipStreamGetAttribute(
      stream: hipStream_t,
      attr: hipLaunchAttributeID,
      value_out: Pointer[hipLaunchAttributeValue, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipStreamGetCaptureInfo(
      stream: hipStream_t,
      pCaptureStatus: Pointer[hipStreamCaptureStatus, ReadWrite, Opaque],
      pId: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipStreamGetCaptureInfo_spt(
      stream: hipStream_t,
      pCaptureStatus: Pointer[hipStreamCaptureStatus, ReadWrite, Opaque],
      pId: Pointer[unsigned_long_long, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipStreamGetCaptureInfo_v2(
      stream: hipStream_t,
      captureStatus_out: Pointer[hipStreamCaptureStatus, ReadWrite, Opaque],
      id_out: Pointer[unsigned_long_long, ReadWrite, Opaque],
      graph_out: Pointer[hipGraph_t, ReadWrite, Opaque],
      dependencies_out: Pointer[Pointer[hipGraphNode_t, Read, Opaque], ReadWrite, Opaque],
      numDependencies_out: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipStreamGetCaptureInfo_v2_spt(
      stream: hipStream_t,
      captureStatus_out: Pointer[hipStreamCaptureStatus, ReadWrite, Opaque],
      id_out: Pointer[unsigned_long_long, ReadWrite, Opaque],
      graph_out: Pointer[hipGraph_t, ReadWrite, Opaque],
      dependencies_out: Pointer[Pointer[hipGraphNode_t, Read, Opaque], ReadWrite, Opaque],
      numDependencies_out: Pointer[size_t, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipStreamGetDevice(stream: hipStream_t, device: Pointer[hipDevice_t, ReadWrite, Opaque]): hipError_t = unsupported
  def hipStreamGetFlags(stream: hipStream_t, flags: Pointer[unsigned_int, ReadWrite, Opaque]): hipError_t = unsupported
  def hipStreamGetFlags_spt(stream: hipStream_t, flags: Pointer[unsigned_int, ReadWrite, Opaque]): hipError_t =
    unsupported
  def hipStreamGetId(stream: hipStream_t, streamId: Pointer[unsigned_long_long, ReadWrite, Opaque]): hipError_t =
    unsupported
  def hipStreamGetPriority(stream: hipStream_t, priority: Pointer[Int, ReadWrite, Opaque]): hipError_t = unsupported
  def hipStreamGetPriority_spt(stream: hipStream_t, priority: Pointer[Int, ReadWrite, Opaque]): hipError_t = unsupported
  def hipStreamIsCapturing(
      stream: hipStream_t,
      pCaptureStatus: Pointer[hipStreamCaptureStatus, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipStreamIsCapturing_spt(
      stream: hipStream_t,
      pCaptureStatus: Pointer[hipStreamCaptureStatus, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipStreamQuery(stream: hipStream_t): hipError_t = unsupported
  def hipStreamQuery_spt(stream: hipStream_t): hipError_t = unsupported
  def hipStreamSetAttribute(
      stream: hipStream_t,
      attr: hipLaunchAttributeID,
      value: Pointer[hipLaunchAttributeValue, Read, Opaque]
  ): hipError_t = unsupported
  def hipStreamSynchronize(stream: hipStream_t): hipError_t = unsupported
  def hipStreamSynchronize_spt(stream: hipStream_t): hipError_t = unsupported
  def hipStreamUpdateCaptureDependencies(
      stream: hipStream_t,
      dependencies: Pointer[hipGraphNode_t, ReadWrite, Opaque],
      numDependencies: size_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipStreamWaitEvent(stream: hipStream_t, event: hipEvent_t, flags: unsigned_int): hipError_t = unsupported
  def hipStreamWaitEvent_spt(stream: hipStream_t, event: hipEvent_t, flags: unsigned_int): hipError_t = unsupported
  def hipStreamWaitValue32(
      stream: hipStream_t,
      ptr: OpaquePointer,
      value: uint32_t,
      flags: unsigned_int,
      mask: uint32_t
  ): hipError_t = unsupported
  def hipStreamWaitValue64(
      stream: hipStream_t,
      ptr: OpaquePointer,
      value: uint64_t,
      flags: unsigned_int,
      mask: uint64_t
  ): hipError_t = unsupported
  def hipStreamWriteValue32(stream: hipStream_t, ptr: OpaquePointer, value: uint32_t, flags: unsigned_int): hipError_t =
    unsupported
  def hipStreamWriteValue64(stream: hipStream_t, ptr: OpaquePointer, value: uint64_t, flags: unsigned_int): hipError_t =
    unsupported
  def hipTexObjectCreate(
      pTexObject: Pointer[hipTextureObject_t, ReadWrite, Opaque],
      pResDesc: Pointer[HIP_RESOURCE_DESC, Read, Opaque],
      pTexDesc: Pointer[HIP_TEXTURE_DESC, Read, Opaque],
      pResViewDesc: Pointer[HIP_RESOURCE_VIEW_DESC, Read, Opaque]
  ): hipError_t = unsupported
  def hipTexObjectDestroy(texObject: hipTextureObject_t): hipError_t = unsupported
  def hipTexObjectGetResourceDesc(
      pResDesc: Pointer[HIP_RESOURCE_DESC, ReadWrite, Opaque],
      texObject: hipTextureObject_t
  ): hipError_t = unsupported
  def hipTexObjectGetResourceViewDesc(
      pResViewDesc: Pointer[HIP_RESOURCE_VIEW_DESC, ReadWrite, Opaque],
      texObject: hipTextureObject_t
  ): hipError_t = unsupported
  def hipTexObjectGetTextureDesc(
      pTexDesc: Pointer[HIP_TEXTURE_DESC, ReadWrite, Opaque],
      texObject: hipTextureObject_t
  ): hipError_t = unsupported
  def hipTexRefGetAddress(
      dev_ptr: Pointer[hipDeviceptr_t, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  def hipTexRefGetAddressMode(
      pam: Pointer[enum_hipTextureAddressMode, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque],
      dim: Int
  ): hipError_t = unsupported
  def hipTexRefGetArray(
      pArray: Pointer[hipArray_t, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  def hipTexRefGetBorderColor(
      pBorderColor: Pointer[Float, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  def hipTexRefGetFilterMode(
      pfm: Pointer[enum_hipTextureFilterMode, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  def hipTexRefGetFlags(
      pFlags: Pointer[unsigned_int, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  def hipTexRefGetFormat(
      pFormat: Pointer[hipArray_Format, ReadWrite, Opaque],
      pNumChannels: Pointer[Int, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  def hipTexRefGetMaxAnisotropy(
      pmaxAnsio: Pointer[Int, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  def hipTexRefGetMipMappedArray(
      pArray: Pointer[hipMipmappedArray_t, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  def hipTexRefGetMipmapFilterMode(
      pfm: Pointer[enum_hipTextureFilterMode, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  def hipTexRefGetMipmapLevelBias(
      pbias: Pointer[Float, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  def hipTexRefGetMipmapLevelClamp(
      pminMipmapLevelClamp: Pointer[Float, ReadWrite, Opaque],
      pmaxMipmapLevelClamp: Pointer[Float, ReadWrite, Opaque],
      texRef: Pointer[textureReference, Read, Opaque]
  ): hipError_t = unsupported
  def hipTexRefSetAddress(
      ByteOffset: Pointer[size_t, ReadWrite, Opaque],
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      dptr: hipDeviceptr_t,
      bytes: size_t
  ): hipError_t = unsupported
  def hipTexRefSetAddress2D(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      desc: Pointer[HIP_ARRAY_DESCRIPTOR, Read, Opaque],
      dptr: hipDeviceptr_t,
      Pitch: size_t
  ): hipError_t = unsupported
  def hipTexRefSetAddressMode(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      dim: Int,
      am: enum_hipTextureAddressMode
  ): hipError_t = unsupported
  def hipTexRefSetArray(
      tex: Pointer[textureReference, ReadWrite, Opaque],
      array: hipArray_const_t,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipTexRefSetBorderColor(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      pBorderColor: Pointer[Float, ReadWrite, Opaque]
  ): hipError_t = unsupported
  def hipTexRefSetFilterMode(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      fm: enum_hipTextureFilterMode
  ): hipError_t = unsupported
  def hipTexRefSetFlags(texRef: Pointer[textureReference, ReadWrite, Opaque], Flags: unsigned_int): hipError_t =
    unsupported
  def hipTexRefSetFormat(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      fmt: hipArray_Format,
      NumPackedComponents: Int
  ): hipError_t = unsupported
  def hipTexRefSetMaxAnisotropy(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      maxAniso: unsigned_int
  ): hipError_t = unsupported
  def hipTexRefSetMipmapFilterMode(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      fm: enum_hipTextureFilterMode
  ): hipError_t = unsupported
  def hipTexRefSetMipmapLevelBias(texRef: Pointer[textureReference, ReadWrite, Opaque], bias: Float): hipError_t =
    unsupported
  def hipTexRefSetMipmapLevelClamp(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      minMipMapLevelClamp: Float,
      maxMipMapLevelClamp: Float
  ): hipError_t = unsupported
  def hipTexRefSetMipmappedArray(
      texRef: Pointer[textureReference, ReadWrite, Opaque],
      mipmappedArray: Pointer[struct_hipMipmappedArray, ReadWrite, Opaque],
      Flags: unsigned_int
  ): hipError_t = unsupported
  def hipThreadExchangeStreamCaptureMode(mode: Pointer[hipStreamCaptureMode, ReadWrite, Opaque]): hipError_t =
    unsupported
  def hipUnbindTexture(tex: Pointer[textureReference, Read, Opaque]): hipError_t = unsupported
  def hipUserObjectCreate(
      object_out: Pointer[hipUserObject_t, ReadWrite, Opaque],
      ptr: OpaquePointer,
      destroy: hipHostFn_t,
      initialRefcount: unsigned_int,
      flags: unsigned_int
  ): hipError_t = unsupported
  def hipUserObjectRelease(`object`: hipUserObject_t, count: unsigned_int): hipError_t = unsupported
  def hipUserObjectRetain(`object`: hipUserObject_t, count: unsigned_int): hipError_t = unsupported
  def hipWaitExternalSemaphoresAsync(
      extSemArray: Pointer[hipExternalSemaphore_t, Read, Opaque],
      paramsArray: Pointer[hipExternalSemaphoreWaitParams, Read, Opaque],
      numExtSems: unsigned_int,
      stream: hipStream_t
  ): hipError_t = unsupported
}

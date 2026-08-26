package net.kurobako.gemstone.cuda

/** Logical CUDA service. Method names are exact exported C symbol names and are also the canonical RPC identities. */
trait CUDA {
  def cuInit(flags: UInt32): CudaResult[Unit]
  def cuDriverGetVersion(): CudaResult[Int]
  def cuDeviceGetCount(): CudaResult[Int]
  def cuDeviceGet(ordinal: Int): CudaResult[Device]

  def cuCtxCreate_v2(flags: UInt32, device: Device): CudaResult[Context]
  def cuCtxDestroy_v2(context: Context): CudaResult[Unit]
  def cuCtxSynchronize(): CudaResult[Unit]

  def cuMemAlloc_v2(bytesize: SizeT): CudaResult[DevicePtr]
  def cuMemFree_v2(pointer: DevicePtr): CudaResult[Unit]
  def cuMemcpyHtoD_v2(destination: DevicePtr, source: HostInput): CudaResult[Unit]
  def cuMemcpyDtoH_v2(source: DevicePtr, byteCount: SizeT): CudaResult[HostOutput]
  def cuMemcpyDtoD_v2(destination: DevicePtr, source: DevicePtr, byteCount: SizeT): CudaResult[Unit]
  def cuMemsetD8_v2(destination: DevicePtr, value: UInt8, count: SizeT): CudaResult[Unit]

  def cudaGetDeviceCount(): CudaResult[Int]
  def cudaMalloc(bytesize: SizeT): CudaResult[DevicePtr]
  def cudaFree(pointer: DevicePtr): CudaResult[Unit]

  /** Stub-only symbols deliberately have no logical parameters: their native entry points ignore vendor ABI arguments
    * and return `CUDA_ERROR_NOT_SUPPORTED` without transport. Implementing one means replacing its concrete method with
    * a typed abstract signature, which automatically adds it to the RPC schema.
    */
  def cuArray3DCreate_v2(): CudaResult[Nothing]                    = unsupported("cuArray3DCreate_v2")
  def cuArray3DGetDescriptor_v2(): CudaResult[Nothing]             = unsupported("cuArray3DGetDescriptor_v2")
  def cuArrayCreate_v2(): CudaResult[Nothing]                      = unsupported("cuArrayCreate_v2")
  def cuArrayDestroy(): CudaResult[Nothing]                        = unsupported("cuArrayDestroy")
  def cuArrayGetDescriptor_v2(): CudaResult[Nothing]               = unsupported("cuArrayGetDescriptor_v2")
  def cuCtxAttach(): CudaResult[Nothing]                           = unsupported("cuCtxAttach")
  def cuCtxDestroy(): CudaResult[Nothing]                          = unsupported("cuCtxDestroy")
  def cuCtxDetach(): CudaResult[Nothing]                           = unsupported("cuCtxDetach")
  def cuCtxGetApiVersion(): CudaResult[Nothing]                    = unsupported("cuCtxGetApiVersion")
  def cuCtxGetCacheConfig(): CudaResult[Nothing]                   = unsupported("cuCtxGetCacheConfig")
  def cuCtxGetDevice(): CudaResult[Nothing]                        = unsupported("cuCtxGetDevice")
  def cuCtxGetLimit(): CudaResult[Nothing]                         = unsupported("cuCtxGetLimit")
  def cuCtxPopCurrent(): CudaResult[Nothing]                       = unsupported("cuCtxPopCurrent")
  def cuCtxPushCurrent(): CudaResult[Nothing]                      = unsupported("cuCtxPushCurrent")
  def cuCtxSetCacheConfig(): CudaResult[Nothing]                   = unsupported("cuCtxSetCacheConfig")
  def cuCtxSetLimit(): CudaResult[Nothing]                         = unsupported("cuCtxSetLimit")
  def cuDeviceComputeCapability(): CudaResult[Nothing]             = unsupported("cuDeviceComputeCapability")
  def cuDeviceGetAttribute(): CudaResult[Nothing]                  = unsupported("cuDeviceGetAttribute")
  def cuDeviceGetName(): CudaResult[Nothing]                       = unsupported("cuDeviceGetName")
  def cuDeviceGetProperties(): CudaResult[Nothing]                 = unsupported("cuDeviceGetProperties")
  def cuDeviceTotalMem_v2(): CudaResult[Nothing]                   = unsupported("cuDeviceTotalMem_v2")
  def cuEventCreate(): CudaResult[Nothing]                         = unsupported("cuEventCreate")
  def cuEventDestroy(): CudaResult[Nothing]                        = unsupported("cuEventDestroy")
  def cuEventElapsedTime(): CudaResult[Nothing]                    = unsupported("cuEventElapsedTime")
  def cuEventQuery(): CudaResult[Nothing]                          = unsupported("cuEventQuery")
  def cuEventRecord(): CudaResult[Nothing]                         = unsupported("cuEventRecord")
  def cuEventSynchronize(): CudaResult[Nothing]                    = unsupported("cuEventSynchronize")
  def cuFuncGetAttribute(): CudaResult[Nothing]                    = unsupported("cuFuncGetAttribute")
  def cuFuncSetBlockShape(): CudaResult[Nothing]                   = unsupported("cuFuncSetBlockShape")
  def cuFuncSetCacheConfig(): CudaResult[Nothing]                  = unsupported("cuFuncSetCacheConfig")
  def cuFuncSetSharedSize(): CudaResult[Nothing]                   = unsupported("cuFuncSetSharedSize")
  def cuGetExportTable(): CudaResult[Nothing]                      = unsupported("cuGetExportTable")
  def cuGraphicsMapResources(): CudaResult[Nothing]                = unsupported("cuGraphicsMapResources")
  def cuGraphicsResourceGetMappedPointer_v2(): CudaResult[Nothing] =
    unsupported("cuGraphicsResourceGetMappedPointer_v2")
  def cuGraphicsResourceSetMapFlags(): CudaResult[Nothing]       = unsupported("cuGraphicsResourceSetMapFlags")
  def cuGraphicsSubResourceGetMappedArray(): CudaResult[Nothing] = unsupported("cuGraphicsSubResourceGetMappedArray")
  def cuGraphicsUnmapResources(): CudaResult[Nothing]            = unsupported("cuGraphicsUnmapResources")
  def cuGraphicsUnregisterResource(): CudaResult[Nothing]        = unsupported("cuGraphicsUnregisterResource")
  def cuLaunch(): CudaResult[Nothing]                            = unsupported("cuLaunch")
  def cuLaunchGrid(): CudaResult[Nothing]                        = unsupported("cuLaunchGrid")
  def cuLaunchGridAsync(): CudaResult[Nothing]                   = unsupported("cuLaunchGridAsync")
  def cuMemAllocHost_v2(): CudaResult[Nothing]                   = unsupported("cuMemAllocHost_v2")
  def cuMemAllocPitch_v2(): CudaResult[Nothing]                  = unsupported("cuMemAllocPitch_v2")
  def cuMemFreeHost(): CudaResult[Nothing]                       = unsupported("cuMemFreeHost")
  def cuMemGetAddressRange_v2(): CudaResult[Nothing]             = unsupported("cuMemGetAddressRange_v2")
  def cuMemGetInfo_v2(): CudaResult[Nothing]                     = unsupported("cuMemGetInfo_v2")
  def cuMemHostAlloc(): CudaResult[Nothing]                      = unsupported("cuMemHostAlloc")
  def cuMemHostGetDevicePointer_v2(): CudaResult[Nothing]        = unsupported("cuMemHostGetDevicePointer_v2")
  def cuMemHostGetFlags(): CudaResult[Nothing]                   = unsupported("cuMemHostGetFlags")
  def cuMemcpy2DAsync_v2(): CudaResult[Nothing]                  = unsupported("cuMemcpy2DAsync_v2")
  def cuMemcpy2DUnaligned_v2(): CudaResult[Nothing]              = unsupported("cuMemcpy2DUnaligned_v2")
  def cuMemcpy2D_v2(): CudaResult[Nothing]                       = unsupported("cuMemcpy2D_v2")
  def cuMemcpy3DAsync_v2(): CudaResult[Nothing]                  = unsupported("cuMemcpy3DAsync_v2")
  def cuMemcpy3D_v2(): CudaResult[Nothing]                       = unsupported("cuMemcpy3D_v2")
  def cuMemcpyAtoA_v2(): CudaResult[Nothing]                     = unsupported("cuMemcpyAtoA_v2")
  def cuMemcpyAtoD_v2(): CudaResult[Nothing]                     = unsupported("cuMemcpyAtoD_v2")
  def cuMemcpyAtoHAsync_v2(): CudaResult[Nothing]                = unsupported("cuMemcpyAtoHAsync_v2")
  def cuMemcpyAtoH_v2(): CudaResult[Nothing]                     = unsupported("cuMemcpyAtoH_v2")
  def cuMemcpyDtoA_v2(): CudaResult[Nothing]                     = unsupported("cuMemcpyDtoA_v2")
  def cuMemcpyDtoDAsync_v2(): CudaResult[Nothing]                = unsupported("cuMemcpyDtoDAsync_v2")
  def cuMemcpyDtoHAsync_v2(): CudaResult[Nothing]                = unsupported("cuMemcpyDtoHAsync_v2")
  def cuMemcpyHtoAAsync_v2(): CudaResult[Nothing]                = unsupported("cuMemcpyHtoAAsync_v2")
  def cuMemcpyHtoA_v2(): CudaResult[Nothing]                     = unsupported("cuMemcpyHtoA_v2")
  def cuMemcpyHtoDAsync_v2(): CudaResult[Nothing]                = unsupported("cuMemcpyHtoDAsync_v2")
  def cuMemsetD16Async(): CudaResult[Nothing]                    = unsupported("cuMemsetD16Async")
  def cuMemsetD16_v2(): CudaResult[Nothing]                      = unsupported("cuMemsetD16_v2")
  def cuMemsetD2D16Async(): CudaResult[Nothing]                  = unsupported("cuMemsetD2D16Async")
  def cuMemsetD2D16_v2(): CudaResult[Nothing]                    = unsupported("cuMemsetD2D16_v2")
  def cuMemsetD2D32Async(): CudaResult[Nothing]                  = unsupported("cuMemsetD2D32Async")
  def cuMemsetD2D32_v2(): CudaResult[Nothing]                    = unsupported("cuMemsetD2D32_v2")
  def cuMemsetD2D8Async(): CudaResult[Nothing]                   = unsupported("cuMemsetD2D8Async")
  def cuMemsetD2D8_v2(): CudaResult[Nothing]                     = unsupported("cuMemsetD2D8_v2")
  def cuMemsetD32Async(): CudaResult[Nothing]                    = unsupported("cuMemsetD32Async")
  def cuMemsetD32_v2(): CudaResult[Nothing]                      = unsupported("cuMemsetD32_v2")
  def cuMemsetD8Async(): CudaResult[Nothing]                     = unsupported("cuMemsetD8Async")
  def cuModuleGetFunction(): CudaResult[Nothing]                 = unsupported("cuModuleGetFunction")
  def cuModuleGetGlobal_v2(): CudaResult[Nothing]                = unsupported("cuModuleGetGlobal_v2")
  def cuModuleGetSurfRef(): CudaResult[Nothing]                  = unsupported("cuModuleGetSurfRef")
  def cuModuleGetTexRef(): CudaResult[Nothing]                   = unsupported("cuModuleGetTexRef")
  def cuModuleLoad(): CudaResult[Nothing]                        = unsupported("cuModuleLoad")
  def cuModuleLoadData(): CudaResult[Nothing]                    = unsupported("cuModuleLoadData")
  def cuModuleLoadDataEx(): CudaResult[Nothing]                  = unsupported("cuModuleLoadDataEx")
  def cuModuleLoadFatBinary(): CudaResult[Nothing]               = unsupported("cuModuleLoadFatBinary")
  def cuModuleUnload(): CudaResult[Nothing]                      = unsupported("cuModuleUnload")
  def cuParamSetSize(): CudaResult[Nothing]                      = unsupported("cuParamSetSize")
  def cuParamSetTexRef(): CudaResult[Nothing]                    = unsupported("cuParamSetTexRef")
  def cuParamSetf(): CudaResult[Nothing]                         = unsupported("cuParamSetf")
  def cuParamSeti(): CudaResult[Nothing]                         = unsupported("cuParamSeti")
  def cuParamSetv(): CudaResult[Nothing]                         = unsupported("cuParamSetv")
  def cuStreamCreate(): CudaResult[Nothing]                      = unsupported("cuStreamCreate")
  def cuStreamDestroy(): CudaResult[Nothing]                     = unsupported("cuStreamDestroy")
  def cuStreamQuery(): CudaResult[Nothing]                       = unsupported("cuStreamQuery")
  def cuStreamSynchronize(): CudaResult[Nothing]                 = unsupported("cuStreamSynchronize")
  def cuStreamWaitEvent(): CudaResult[Nothing]                   = unsupported("cuStreamWaitEvent")
  def cuSurfRefGetArray(): CudaResult[Nothing]                   = unsupported("cuSurfRefGetArray")
  def cuSurfRefSetArray(): CudaResult[Nothing]                   = unsupported("cuSurfRefSetArray")
  def cuTexRefCreate(): CudaResult[Nothing]                      = unsupported("cuTexRefCreate")
  def cuTexRefDestroy(): CudaResult[Nothing]                     = unsupported("cuTexRefDestroy")
  def cuTexRefGetAddressMode(): CudaResult[Nothing]              = unsupported("cuTexRefGetAddressMode")
  def cuTexRefGetAddress_v2(): CudaResult[Nothing]               = unsupported("cuTexRefGetAddress_v2")
  def cuTexRefGetArray(): CudaResult[Nothing]                    = unsupported("cuTexRefGetArray")
  def cuTexRefGetFilterMode(): CudaResult[Nothing]               = unsupported("cuTexRefGetFilterMode")
  def cuTexRefGetFlags(): CudaResult[Nothing]                    = unsupported("cuTexRefGetFlags")
  def cuTexRefGetFormat(): CudaResult[Nothing]                   = unsupported("cuTexRefGetFormat")
  def cuTexRefSetAddress2D_v2(): CudaResult[Nothing]             = unsupported("cuTexRefSetAddress2D_v2")
  def cuTexRefSetAddressMode(): CudaResult[Nothing]              = unsupported("cuTexRefSetAddressMode")
  def cuTexRefSetAddress_v2(): CudaResult[Nothing]               = unsupported("cuTexRefSetAddress_v2")
  def cuTexRefSetArray(): CudaResult[Nothing]                    = unsupported("cuTexRefSetArray")
  def cuTexRefSetFilterMode(): CudaResult[Nothing]               = unsupported("cuTexRefSetFilterMode")
  def cuTexRefSetFlags(): CudaResult[Nothing]                    = unsupported("cuTexRefSetFlags")
  def cuTexRefSetFormat(): CudaResult[Nothing]                   = unsupported("cuTexRefSetFormat")
  def cuCtxCreate(): CudaResult[Nothing]                         = unsupported("cuCtxCreate")
  def cuCtxPushCurrent_v2(): CudaResult[Nothing]                 = unsupported("cuCtxPushCurrent_v2")
  def cuDevicePrimaryCtxRelease(): CudaResult[Nothing]           = unsupported("cuDevicePrimaryCtxRelease")
  def cuDevicePrimaryCtxRetain(): CudaResult[Nothing]            = unsupported("cuDevicePrimaryCtxRetain")
  def cuDeviceTotalMem(): CudaResult[Nothing]                    = unsupported("cuDeviceTotalMem")
  def cuGLCtxCreate(): CudaResult[Nothing]                       = unsupported("cuGLCtxCreate")
  def cuGLInit(): CudaResult[Nothing]                            = unsupported("cuGLInit")
  def cuGLRegisterBufferObject(): CudaResult[Nothing]            = unsupported("cuGLRegisterBufferObject")
  def cuGLSetBufferObjectMapFlags(): CudaResult[Nothing]         = unsupported("cuGLSetBufferObjectMapFlags")
  def cuGetErrorName(): CudaResult[Nothing]                      = unsupported("cuGetErrorName")
  def cuGetErrorString(): CudaResult[Nothing]                    = unsupported("cuGetErrorString")
  def cuGraphicsGLRegisterBuffer(): CudaResult[Nothing]          = unsupported("cuGraphicsGLRegisterBuffer")
  def cuGraphicsGLRegisterImage(): CudaResult[Nothing]           = unsupported("cuGraphicsGLRegisterImage")
  def cuLaunchHostFunc(): CudaResult[Nothing]                    = unsupported("cuLaunchHostFunc")
  def cuLaunchKernel(): CudaResult[Nothing]                      = unsupported("cuLaunchKernel")
  def cuMemAllocHost(): CudaResult[Nothing]                      = unsupported("cuMemAllocHost")
  def cuMemAllocManaged(): CudaResult[Nothing]                   = unsupported("cuMemAllocManaged")
  def cuMemcpyHtoH_v2(): CudaResult[Nothing]                     = unsupported("cuMemcpyHtoH_v2")
  def cuStreamAddCallback(): CudaResult[Nothing]                 = unsupported("cuStreamAddCallback")
  def cuStreamDestroy_v2(): CudaResult[Nothing]                  = unsupported("cuStreamDestroy_v2")

  private def unsupported(name: String): CudaResult[Nothing] =
    CudaResult.Failure(CudaError(801, s"$name is not implemented"))
}

object CUDA {
  val UpstreamRevision: String = "b16039dc940dc6bc4ea0a98380495769ff35ed99"
  val CudaVersion: Int         = 3020
}

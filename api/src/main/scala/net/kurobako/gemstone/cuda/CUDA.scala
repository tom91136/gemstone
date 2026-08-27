package net.kurobako.gemstone.cuda

/** Logical CUDA service. Method names are exact exported C symbol names and are also the canonical RPC identities. */
trait CUDA {
  def cuInit(flags: UInt32): CUDAResult[Unit]
  def cuDriverGetVersion(): CUDAResult[Int]
  def cuDeviceGetCount(): CUDAResult[Int]
  def cuDeviceGet(ordinal: Int): CUDAResult[Device]

  def cuCtxCreate_v2(flags: UInt32, device: Device): CUDAResult[Context]
  def cuCtxDestroy_v2(context: Context): CUDAResult[Unit]
  def cuCtxSynchronize(): CUDAResult[Unit]

  def cuMemAlloc_v2(bytesize: SizeT): CUDAResult[DevicePtr]
  def cuMemFree_v2(pointer: DevicePtr): CUDAResult[Unit]
  def cuMemcpyHtoD_v2(destination: DevicePtr, source: HostInput): CUDAResult[Unit]
  def cuMemcpyDtoH_v2(source: DevicePtr, byteCount: SizeT): CUDAResult[HostOutput]
  def cuMemcpyDtoD_v2(destination: DevicePtr, source: DevicePtr, byteCount: SizeT): CUDAResult[Unit]
  def cuMemsetD8_v2(destination: DevicePtr, value: UInt8, count: SizeT): CUDAResult[Unit]

  def cuModuleLoadData(image: ModuleImage): CUDAResult[CUDAModule]
  def cuModuleGetFunction(module: CUDAModule, name: FunctionName): CUDAResult[KernelFunction]
  def cuModuleUnload(module: CUDAModule): CUDAResult[Unit]
  def cuLaunchKernel(function: CUDAFunction, launch: KernelLaunch): CUDAResult[Unit]

  /** Internal transport operations backing host-dereferenceable managed and pinned allocations. */
  def __cudaGemstoneManagedRegister(pointer: DevicePtr, bytesize: SizeT): CUDAResult[Unit]
  def __cudaGemstoneManagedUnregister(pointer: DevicePtr): CUDAResult[Unit]

  def cudaGetDeviceCount(): CUDAResult[Int]
  def cudaMalloc(bytesize: SizeT): CUDAResult[DevicePtr]
  def cudaFree(pointer: DevicePtr): CUDAResult[Unit]
  def cudaMemcpy(): CUDAResult[Nothing]              = unsupported("cudaMemcpy")
  def cudaGetErrorString(): CUDAResult[Nothing]      = unsupported("cudaGetErrorString")
  def cudaGetErrorName(): CUDAResult[Nothing]        = unsupported("cudaGetErrorName")
  def cudaGetLastError(): CUDAResult[Nothing]        = unsupported("cudaGetLastError")
  def cudaPeekAtLastError(): CUDAResult[Nothing]     = unsupported("cudaPeekAtLastError")
  def cudaGetDevice(): CUDAResult[Nothing]           = unsupported("cudaGetDevice")
  def cudaSetDevice(): CUDAResult[Nothing]           = unsupported("cudaSetDevice")
  def cudaDeviceGetAttribute(): CUDAResult[Nothing]  = unsupported("cudaDeviceGetAttribute")
  def cudaGetDeviceProperties(): CUDAResult[Nothing] = unsupported("cudaGetDeviceProperties")
  def cudaDeviceSynchronize(): CUDAResult[Nothing]   = unsupported("cudaDeviceSynchronize")
  def cudaStreamSynchronize(): CUDAResult[Nothing]   = unsupported("cudaStreamSynchronize")
  def cudaMallocHost(): CUDAResult[Nothing]          = unsupported("cudaMallocHost")
  def cudaFreeHost(): CUDAResult[Nothing]            = unsupported("cudaFreeHost")
  def cudaMallocManaged(): CUDAResult[Nothing]       = unsupported("cudaMallocManaged")
  def cudaMemcpyAsync(): CUDAResult[Nothing]         = unsupported("cudaMemcpyAsync")
  def cudaFuncGetAttributes(): CUDAResult[Nothing]   = unsupported("cudaFuncGetAttributes")
  def cudaOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(): CUDAResult[Nothing] =
    unsupported("cudaOccupancyMaxActiveBlocksPerMultiprocessorWithFlags")
  def __cudaGetKernel(): CUDAResult[Nothing]             = unsupported("__cudaGetKernel")
  def __cudaLaunchKernel(): CUDAResult[Nothing]          = unsupported("__cudaLaunchKernel")
  def __cudaPushCallConfiguration(): CUDAResult[Nothing] = unsupported("__cudaPushCallConfiguration")
  def __cudaPopCallConfiguration(): CUDAResult[Nothing]  = unsupported("__cudaPopCallConfiguration")
  def __cudaRegisterFunction(): CUDAResult[Nothing]      = unsupported("__cudaRegisterFunction")
  def __cudaRegisterVar(): CUDAResult[Nothing]           = unsupported("__cudaRegisterVar")
  def __cudaRegisterFatBinary(): CUDAResult[Nothing]     = unsupported("__cudaRegisterFatBinary")
  def __cudaRegisterFatBinaryEnd(): CUDAResult[Nothing]  = unsupported("__cudaRegisterFatBinaryEnd")
  def __cudaUnregisterFatBinary(): CUDAResult[Nothing]   = unsupported("__cudaUnregisterFatBinary")

  /** Stub-only symbols deliberately have no logical parameters: their native entry points ignore vendor ABI arguments
    * and return `CUDA_ERROR_NOT_SUPPORTED` without transport. Implementing one means replacing its concrete method with
    * a typed abstract signature, which automatically adds it to the RPC schema.
    */
  def cuArray3DCreate_v2(): CUDAResult[Nothing]                    = unsupported("cuArray3DCreate_v2")
  def cuArray3DGetDescriptor_v2(): CUDAResult[Nothing]             = unsupported("cuArray3DGetDescriptor_v2")
  def cuArrayCreate_v2(): CUDAResult[Nothing]                      = unsupported("cuArrayCreate_v2")
  def cuArrayDestroy(): CUDAResult[Nothing]                        = unsupported("cuArrayDestroy")
  def cuArrayGetDescriptor_v2(): CUDAResult[Nothing]               = unsupported("cuArrayGetDescriptor_v2")
  def cuCtxAttach(): CUDAResult[Nothing]                           = unsupported("cuCtxAttach")
  def cuCtxDestroy(): CUDAResult[Nothing]                          = unsupported("cuCtxDestroy")
  def cuCtxDetach(): CUDAResult[Nothing]                           = unsupported("cuCtxDetach")
  def cuCtxGetApiVersion(): CUDAResult[Nothing]                    = unsupported("cuCtxGetApiVersion")
  def cuCtxGetCacheConfig(): CUDAResult[Nothing]                   = unsupported("cuCtxGetCacheConfig")
  def cuCtxGetDevice(): CUDAResult[Nothing]                        = unsupported("cuCtxGetDevice")
  def cuCtxGetLimit(): CUDAResult[Nothing]                         = unsupported("cuCtxGetLimit")
  def cuCtxPopCurrent(): CUDAResult[Nothing]                       = unsupported("cuCtxPopCurrent")
  def cuCtxPushCurrent(): CUDAResult[Nothing]                      = unsupported("cuCtxPushCurrent")
  def cuCtxSetCacheConfig(): CUDAResult[Nothing]                   = unsupported("cuCtxSetCacheConfig")
  def cuCtxSetLimit(): CUDAResult[Nothing]                         = unsupported("cuCtxSetLimit")
  def cuDeviceComputeCapability(): CUDAResult[Nothing]             = unsupported("cuDeviceComputeCapability")
  def cuDeviceGetAttribute(): CUDAResult[Nothing]                  = unsupported("cuDeviceGetAttribute")
  def cuDeviceGetName(): CUDAResult[Nothing]                       = unsupported("cuDeviceGetName")
  def cuDeviceGetProperties(): CUDAResult[Nothing]                 = unsupported("cuDeviceGetProperties")
  def cuDeviceTotalMem_v2(): CUDAResult[Nothing]                   = unsupported("cuDeviceTotalMem_v2")
  def cuEventCreate(): CUDAResult[Nothing]                         = unsupported("cuEventCreate")
  def cuEventDestroy(): CUDAResult[Nothing]                        = unsupported("cuEventDestroy")
  def cuEventElapsedTime(): CUDAResult[Nothing]                    = unsupported("cuEventElapsedTime")
  def cuEventQuery(): CUDAResult[Nothing]                          = unsupported("cuEventQuery")
  def cuEventRecord(): CUDAResult[Nothing]                         = unsupported("cuEventRecord")
  def cuEventSynchronize(): CUDAResult[Nothing]                    = unsupported("cuEventSynchronize")
  def cuFuncGetAttribute(): CUDAResult[Nothing]                    = unsupported("cuFuncGetAttribute")
  def cuFuncSetBlockShape(): CUDAResult[Nothing]                   = unsupported("cuFuncSetBlockShape")
  def cuFuncSetCacheConfig(): CUDAResult[Nothing]                  = unsupported("cuFuncSetCacheConfig")
  def cuFuncSetSharedSize(): CUDAResult[Nothing]                   = unsupported("cuFuncSetSharedSize")
  def cuGetExportTable(): CUDAResult[Nothing]                      = unsupported("cuGetExportTable")
  def cuGraphicsMapResources(): CUDAResult[Nothing]                = unsupported("cuGraphicsMapResources")
  def cuGraphicsResourceGetMappedPointer_v2(): CUDAResult[Nothing] =
    unsupported("cuGraphicsResourceGetMappedPointer_v2")
  def cuGraphicsResourceSetMapFlags(): CUDAResult[Nothing]       = unsupported("cuGraphicsResourceSetMapFlags")
  def cuGraphicsSubResourceGetMappedArray(): CUDAResult[Nothing] = unsupported("cuGraphicsSubResourceGetMappedArray")
  def cuGraphicsUnmapResources(): CUDAResult[Nothing]            = unsupported("cuGraphicsUnmapResources")
  def cuGraphicsUnregisterResource(): CUDAResult[Nothing]        = unsupported("cuGraphicsUnregisterResource")
  def cuLaunch(): CUDAResult[Nothing]                            = unsupported("cuLaunch")
  def cuLaunchGrid(): CUDAResult[Nothing]                        = unsupported("cuLaunchGrid")
  def cuLaunchGridAsync(): CUDAResult[Nothing]                   = unsupported("cuLaunchGridAsync")
  def cuMemAllocHost_v2(): CUDAResult[Nothing]                   = unsupported("cuMemAllocHost_v2")
  def cuMemAllocPitch_v2(): CUDAResult[Nothing]                  = unsupported("cuMemAllocPitch_v2")
  def cuMemFreeHost(): CUDAResult[Nothing]                       = unsupported("cuMemFreeHost")
  def cuMemGetAddressRange_v2(): CUDAResult[Nothing]             = unsupported("cuMemGetAddressRange_v2")
  def cuMemGetInfo_v2(): CUDAResult[Nothing]                     = unsupported("cuMemGetInfo_v2")
  def cuMemHostAlloc(): CUDAResult[Nothing]                      = unsupported("cuMemHostAlloc")
  def cuMemHostGetDevicePointer_v2(): CUDAResult[Nothing]        = unsupported("cuMemHostGetDevicePointer_v2")
  def cuMemHostGetFlags(): CUDAResult[Nothing]                   = unsupported("cuMemHostGetFlags")
  def cuMemcpy2DAsync_v2(): CUDAResult[Nothing]                  = unsupported("cuMemcpy2DAsync_v2")
  def cuMemcpy2DUnaligned_v2(): CUDAResult[Nothing]              = unsupported("cuMemcpy2DUnaligned_v2")
  def cuMemcpy2D_v2(): CUDAResult[Nothing]                       = unsupported("cuMemcpy2D_v2")
  def cuMemcpy3DAsync_v2(): CUDAResult[Nothing]                  = unsupported("cuMemcpy3DAsync_v2")
  def cuMemcpy3D_v2(): CUDAResult[Nothing]                       = unsupported("cuMemcpy3D_v2")
  def cuMemcpyAtoA_v2(): CUDAResult[Nothing]                     = unsupported("cuMemcpyAtoA_v2")
  def cuMemcpyAtoD_v2(): CUDAResult[Nothing]                     = unsupported("cuMemcpyAtoD_v2")
  def cuMemcpyAtoHAsync_v2(): CUDAResult[Nothing]                = unsupported("cuMemcpyAtoHAsync_v2")
  def cuMemcpyAtoH_v2(): CUDAResult[Nothing]                     = unsupported("cuMemcpyAtoH_v2")
  def cuMemcpyDtoA_v2(): CUDAResult[Nothing]                     = unsupported("cuMemcpyDtoA_v2")
  def cuMemcpyDtoDAsync_v2(): CUDAResult[Nothing]                = unsupported("cuMemcpyDtoDAsync_v2")
  def cuMemcpyDtoHAsync_v2(): CUDAResult[Nothing]                = unsupported("cuMemcpyDtoHAsync_v2")
  def cuMemcpyHtoAAsync_v2(): CUDAResult[Nothing]                = unsupported("cuMemcpyHtoAAsync_v2")
  def cuMemcpyHtoA_v2(): CUDAResult[Nothing]                     = unsupported("cuMemcpyHtoA_v2")
  def cuMemcpyHtoDAsync_v2(): CUDAResult[Nothing]                = unsupported("cuMemcpyHtoDAsync_v2")
  def cuMemsetD16Async(): CUDAResult[Nothing]                    = unsupported("cuMemsetD16Async")
  def cuMemsetD16_v2(): CUDAResult[Nothing]                      = unsupported("cuMemsetD16_v2")
  def cuMemsetD2D16Async(): CUDAResult[Nothing]                  = unsupported("cuMemsetD2D16Async")
  def cuMemsetD2D16_v2(): CUDAResult[Nothing]                    = unsupported("cuMemsetD2D16_v2")
  def cuMemsetD2D32Async(): CUDAResult[Nothing]                  = unsupported("cuMemsetD2D32Async")
  def cuMemsetD2D32_v2(): CUDAResult[Nothing]                    = unsupported("cuMemsetD2D32_v2")
  def cuMemsetD2D8Async(): CUDAResult[Nothing]                   = unsupported("cuMemsetD2D8Async")
  def cuMemsetD2D8_v2(): CUDAResult[Nothing]                     = unsupported("cuMemsetD2D8_v2")
  def cuMemsetD32Async(): CUDAResult[Nothing]                    = unsupported("cuMemsetD32Async")
  def cuMemsetD32_v2(): CUDAResult[Nothing]                      = unsupported("cuMemsetD32_v2")
  def cuMemsetD8Async(): CUDAResult[Nothing]                     = unsupported("cuMemsetD8Async")
  def cuModuleGetFunction(): CUDAResult[Nothing]                 = unsupported("cuModuleGetFunction")
  def cuModuleGetGlobal_v2(): CUDAResult[Nothing]                = unsupported("cuModuleGetGlobal_v2")
  def cuModuleGetSurfRef(): CUDAResult[Nothing]                  = unsupported("cuModuleGetSurfRef")
  def cuModuleGetTexRef(): CUDAResult[Nothing]                   = unsupported("cuModuleGetTexRef")
  def cuModuleLoad(): CUDAResult[Nothing]                        = unsupported("cuModuleLoad")
  def cuModuleLoadData(): CUDAResult[Nothing]                    = unsupported("cuModuleLoadData")
  def cuModuleLoadDataEx(): CUDAResult[Nothing]                  = unsupported("cuModuleLoadDataEx")
  def cuModuleLoadFatBinary(): CUDAResult[Nothing]               = unsupported("cuModuleLoadFatBinary")
  def cuModuleUnload(): CUDAResult[Nothing]                      = unsupported("cuModuleUnload")
  def cuParamSetSize(): CUDAResult[Nothing]                      = unsupported("cuParamSetSize")
  def cuParamSetTexRef(): CUDAResult[Nothing]                    = unsupported("cuParamSetTexRef")
  def cuParamSetf(): CUDAResult[Nothing]                         = unsupported("cuParamSetf")
  def cuParamSeti(): CUDAResult[Nothing]                         = unsupported("cuParamSeti")
  def cuParamSetv(): CUDAResult[Nothing]                         = unsupported("cuParamSetv")
  def cuStreamCreate(): CUDAResult[Nothing]                      = unsupported("cuStreamCreate")
  def cuStreamDestroy(): CUDAResult[Nothing]                     = unsupported("cuStreamDestroy")
  def cuStreamQuery(): CUDAResult[Nothing]                       = unsupported("cuStreamQuery")
  def cuStreamSynchronize(): CUDAResult[Nothing]                 = unsupported("cuStreamSynchronize")
  def cuStreamWaitEvent(): CUDAResult[Nothing]                   = unsupported("cuStreamWaitEvent")
  def cuSurfRefGetArray(): CUDAResult[Nothing]                   = unsupported("cuSurfRefGetArray")
  def cuSurfRefSetArray(): CUDAResult[Nothing]                   = unsupported("cuSurfRefSetArray")
  def cuTexRefCreate(): CUDAResult[Nothing]                      = unsupported("cuTexRefCreate")
  def cuTexRefDestroy(): CUDAResult[Nothing]                     = unsupported("cuTexRefDestroy")
  def cuTexRefGetAddressMode(): CUDAResult[Nothing]              = unsupported("cuTexRefGetAddressMode")
  def cuTexRefGetAddress_v2(): CUDAResult[Nothing]               = unsupported("cuTexRefGetAddress_v2")
  def cuTexRefGetArray(): CUDAResult[Nothing]                    = unsupported("cuTexRefGetArray")
  def cuTexRefGetFilterMode(): CUDAResult[Nothing]               = unsupported("cuTexRefGetFilterMode")
  def cuTexRefGetFlags(): CUDAResult[Nothing]                    = unsupported("cuTexRefGetFlags")
  def cuTexRefGetFormat(): CUDAResult[Nothing]                   = unsupported("cuTexRefGetFormat")
  def cuTexRefSetAddress2D_v2(): CUDAResult[Nothing]             = unsupported("cuTexRefSetAddress2D_v2")
  def cuTexRefSetAddressMode(): CUDAResult[Nothing]              = unsupported("cuTexRefSetAddressMode")
  def cuTexRefSetAddress_v2(): CUDAResult[Nothing]               = unsupported("cuTexRefSetAddress_v2")
  def cuTexRefSetArray(): CUDAResult[Nothing]                    = unsupported("cuTexRefSetArray")
  def cuTexRefSetFilterMode(): CUDAResult[Nothing]               = unsupported("cuTexRefSetFilterMode")
  def cuTexRefSetFlags(): CUDAResult[Nothing]                    = unsupported("cuTexRefSetFlags")
  def cuTexRefSetFormat(): CUDAResult[Nothing]                   = unsupported("cuTexRefSetFormat")
  def cuCtxCreate(): CUDAResult[Nothing]                         = unsupported("cuCtxCreate")
  def cuCtxPushCurrent_v2(): CUDAResult[Nothing]                 = unsupported("cuCtxPushCurrent_v2")
  def cuDevicePrimaryCtxRelease(): CUDAResult[Nothing]           = unsupported("cuDevicePrimaryCtxRelease")
  def cuDevicePrimaryCtxRetain(): CUDAResult[Nothing]            = unsupported("cuDevicePrimaryCtxRetain")
  def cuDeviceTotalMem(): CUDAResult[Nothing]                    = unsupported("cuDeviceTotalMem")
  def cuGLCtxCreate(): CUDAResult[Nothing]                       = unsupported("cuGLCtxCreate")
  def cuGLInit(): CUDAResult[Nothing]                            = unsupported("cuGLInit")
  def cuGLRegisterBufferObject(): CUDAResult[Nothing]            = unsupported("cuGLRegisterBufferObject")
  def cuGLSetBufferObjectMapFlags(): CUDAResult[Nothing]         = unsupported("cuGLSetBufferObjectMapFlags")
  def cuGetErrorName(): CUDAResult[Nothing]                      = unsupported("cuGetErrorName")
  def cuGetErrorString(): CUDAResult[Nothing]                    = unsupported("cuGetErrorString")
  def cuGraphicsGLRegisterBuffer(): CUDAResult[Nothing]          = unsupported("cuGraphicsGLRegisterBuffer")
  def cuGraphicsGLRegisterImage(): CUDAResult[Nothing]           = unsupported("cuGraphicsGLRegisterImage")
  def cuLaunchHostFunc(): CUDAResult[Nothing]                    = unsupported("cuLaunchHostFunc")
  def cuMemAllocHost(): CUDAResult[Nothing]                      = unsupported("cuMemAllocHost")
  def cuMemAllocManaged(): CUDAResult[Nothing]                   = unsupported("cuMemAllocManaged")
  def cuMemcpyHtoH_v2(): CUDAResult[Nothing]                     = unsupported("cuMemcpyHtoH_v2")
  def cuStreamAddCallback(): CUDAResult[Nothing]                 = unsupported("cuStreamAddCallback")
  def cuStreamDestroy_v2(): CUDAResult[Nothing]                  = unsupported("cuStreamDestroy_v2")

  private def unsupported(name: String): CUDAResult[Nothing] =
    CUDAResult.Failure(CUDAError(801, s"$name is not implemented"))
}

object CUDA {
  val UpstreamRevision: String = "b16039dc940dc6bc4ea0a98380495769ff35ed99"
  val CUDAVersion: Int         = 3020
}

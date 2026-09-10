package net.kurobako.gemstone.cuda

import net.kurobako.gemstone.abi.*

type CUuuid     = CValue["CUuuid"]
type cuuint64_t = CValue["cuuint64_t"]

type CUctxCreateParams              = CValue["CUctxCreateParams"]
type CUdriverProcAddressQueryResult = CValue["CUdriverProcAddressQueryResult"]
type CUhostFn                       = CallbackTypedef["CUhostFn", Tuple1[CallbackData], Unit]
type CUstreamCallback               = CallbackTypedef["CUstreamCallback", (Stream, CUDAStatus, CallbackData), Unit]
type GemstoneDim3                   = CValue["GemstoneDim3"]
type int32_t                        = CValue["int32_t"]

trait CUDACustomSurface {
  def __cudaGemstoneManagedRegister(pointer: DevicePtr, bytesize: SizeT): CUDAStatus
  def __cudaGemstoneManagedUnregister(pointer: DevicePtr): CUDAStatus
  def __cudaGemstoneRegisterGlobal(module: CUDAModule, name: FunctionName, initial: ModuleImage): CUDAStatus
  def __cudaGemstoneResolveFunctions(result: Out[ModuleImage], module: CUDAModule, names: ModuleImage): CUDAStatus

  def cuGetProcAddress_v2(
      symbol: CString,
      pfn: Pointer[OpaquePointer, ReadWrite, Opaque],
      cudaVersion: Int,
      flags: cuuint64_t,
      symbolStatus: Pointer[CUdriverProcAddressQueryResult, ReadWrite, Opaque]
  ): CUDAStatus = delegate["gemstone_cuGetProcAddress_v2"](symbol, pfn, cudaVersion, flags, symbolStatus)

  def cuGetExportTable(
      ppExportTable: Pointer[OpaquePointer, Read, Opaque],
      pExportTableId: Pointer[CUuuid, Read, Opaque]
  ): CUDAStatus = delegate["gemstone_cuGetExportTable"](ppExportTable, pExportTableId)

  def __cudaRegisterFatBinary(fatBinary: OpaquePointer): Pointer[OpaquePointer, ReadWrite, Opaque] =
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

  def cuModuleGetGlobal_v2(
      dptr: Out[DevicePtr],
      bytes: Out[SizeT],
      module: CUDAModule,
      name: FunctionName
  ): CUDAStatus

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

  def cuCtxCreate_v4(
      pctx: Out[Context],
      ctxCreateParams: Nullable[InOut[CUctxCreateParams]],
      flags: UInt32,
      dev: Device
  ): CUDAStatus = delegate["gemstone_cuCtxCreate_v4"](pctx, ctxCreateParams, flags, dev)

  def cuCtxCreate_v2(pctx: Out[Context], flags: UInt32, dev: Device): CUDAStatus
  def cuCtxDestroy_v2(ctx: Context): CUDAStatus
  def cuCtxSynchronize(): CUDAStatus
  def cuDeviceGet(device: Out[Device], ordinal: Int): CUDAStatus
  def cuDeviceGetCount(count: Out[Int]): CUDAStatus
  def cuDriverGetVersion(driverVersion: Out[Int]): CUDAStatus
  def cuInit(Flags: UInt32): CUDAStatus
  def cuLaunchHostFunc(hStream: Stream, fn: CUhostFn, userData: CallbackData): CUDAStatus
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
  def cuMemAlloc_v2(dptr: Out[DevicePtr], bytesize: SizeT): CUDAStatus
  def cuMemFree_v2(dptr: DevicePtr): CUDAStatus
  def cuMemcpyDtoD_v2(dstDevice: DevicePtr, srcDevice: DevicePtr, ByteCount: SizeT): CUDAStatus
  def cuMemcpyDtoH_v2(dstHost: OutputBuffer[Byte, "ByteCount"], srcDevice: DevicePtr, ByteCount: SizeT): CUDAStatus
  def cuMemcpyHtoD_v2(dstDevice: DevicePtr, srcHost: InputBuffer[Byte, "ByteCount"], ByteCount: SizeT): CUDAStatus
  def cuMemsetD8_v2(dstDevice: DevicePtr, uc: UInt8, N: SizeT): CUDAStatus
  def cuModuleGetFunction(hfunc: Out[KernelFunction], hmod: CUDAModule, name: FunctionName): CUDAStatus
  def cuModuleLoadData(module: Out[CUDAModule], image: ModuleImage): CUDAStatus
  def cuModuleUnload(hmod: CUDAModule): CUDAStatus
  def cuStreamAddCallback(
      hStream: Stream,
      callback: CUstreamCallback,
      userData: CallbackData,
      flags: UInt32
  ): CUDAStatus
  def cudaFree(devPtr: DevicePtr): CUDAStatus
  def cudaGetDeviceCount(count: Out[Int]): CUDAStatus
  def cudaMalloc(devPtr: Out[DevicePtr], size: SizeT): CUDAStatus
}

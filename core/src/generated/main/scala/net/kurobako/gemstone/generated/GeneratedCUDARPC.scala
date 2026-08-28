package net.kurobako.gemstone.generated

import net.kurobako.gemstone.abi.*
import net.kurobako.gemstone.cuda.*
import net.kurobako.gemstone.rpc.*

object GeneratedCUDARPC {
  val SchemaFingerprint: String = "ec53bf9a873f247b54719e7328615bc3ff5fa3c0eb4321e934c923c0a2a6016a"

  private val __cudaGemstoneManagedRegisterId = MessageId(0xc6207aea8d73f5baL, 0x2db4cd6e3a364b1aL)
  private val __cudaGemstoneManagedUnregisterId = MessageId(0x93e5439ad242c3c0L, 0x8e20904fec96004cL)
  private val cuCtxCreate_v2Id = MessageId(0xe6eb5473c0a097e3L, 0xa17c8d63cec75a44L)
  private val cuCtxDestroy_v2Id = MessageId(0xb574aacc9b35ee6aL, 0x8135b883071cc261L)
  private val cuCtxSynchronizeId = MessageId(0xe21f80c194a589d4L, 0x4af681a7ef75bb10L)
  private val cuDeviceGetId = MessageId(0xc91170009b2ba6c9L, 0x263d0d32e04e06a7L)
  private val cuDeviceGetCountId = MessageId(0x831f6fffdf16f0d4L, 0x3c498239d16d9867L)
  private val cuDriverGetVersionId = MessageId(0xe584ec56943a425dL, 0x3c512e60803234f9L)
  private val cuInitId = MessageId(0x3c1688958f08660eL, 0x1c2a40d0799c268cL)
  private val cuLaunchHostFuncId = MessageId(0xced40f65d252b18eL, 0xdc2b29f9f2e965b6L)
  private val cuLaunchKernelId = MessageId(0x0541d9142559d437L, 0x4cd345cbce10e2dfL)
  private val cuMemAlloc_v2Id = MessageId(0x7077a5441f532f0eL, 0x677e154cf996113dL)
  private val cuMemFree_v2Id = MessageId(0xb5fdc569871ec54eL, 0x418d186e2d8efa3aL)
  private val cuMemcpyDtoD_v2Id = MessageId(0xe27c11476bbea751L, 0x681167b2aac69936L)
  private val cuMemcpyDtoH_v2Id = MessageId(0x0cc3708077b8e76aL, 0xaa0bf9a6432ca141L)
  private val cuMemcpyHtoD_v2Id = MessageId(0x9a8f59036790ef69L, 0x4808ed612739b8faL)
  private val cuMemsetD8_v2Id = MessageId(0x2c1383458eec311aL, 0x73e621c93fd80644L)
  private val cuModuleGetFunctionId = MessageId(0x4b6b65a1226b0723L, 0xc3ac0d7e32b1d4b5L)
  private val cuModuleLoadDataId = MessageId(0xfef489c6c628b458L, 0x480e5cb6fe964d3fL)
  private val cuModuleUnloadId = MessageId(0xb94c365c6c5c3902L, 0x2a1466600700709aL)
  private val cuStreamAddCallbackId = MessageId(0x589d21fb33f2aca5L, 0xbe72b9cfe9179b7aL)
  private val cudaFreeId = MessageId(0xc9831c51cb850c1eL, 0x0300fb6b734556b4L)
  private val cudaGetDeviceCountId = MessageId(0x29770d4f155d9a98L, 0xccec9231bf109befL)
  private val cudaMallocId = MessageId(0x36207ff276ba04dbL, 0x8ed2474e0fef125dL)

  val Schema: RPCSchema = RPCSchema(
    "net.kurobako.gemstone.cuda.CUDA",
    SchemaFingerprint,
    Vector(
      RPCMethod(
        "__cudaGemstoneManagedRegister",
        __cudaGemstoneManagedRegisterId,
        Vector(
          RPCParameter("pointer", WireType.DevicePtr, WireDirection.In, WireExtent.Value),
          RPCParameter("bytesize", WireType.SizeT, WireDirection.In, WireExtent.Value)
        ),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "__cudaGemstoneManagedUnregister",
        __cudaGemstoneManagedUnregisterId,
        Vector(RPCParameter("pointer", WireType.DevicePtr, WireDirection.In, WireExtent.Value)),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuCtxCreate_v2",
        cuCtxCreate_v2Id,
        Vector(
          RPCParameter("pctx", WireType.Context, WireDirection.Out, WireExtent.One),
          RPCParameter("flags", WireType.UInt32, WireDirection.In, WireExtent.Value),
          RPCParameter("dev", WireType.Device, WireDirection.In, WireExtent.Value)
        ),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuCtxDestroy_v2",
        cuCtxDestroy_v2Id,
        Vector(RPCParameter("ctx", WireType.Context, WireDirection.In, WireExtent.Value)),
        WireType.CUDAStatus
      ),
      RPCMethod("cuCtxSynchronize", cuCtxSynchronizeId, Vector(), WireType.CUDAStatus),
      RPCMethod(
        "cuDeviceGet",
        cuDeviceGetId,
        Vector(
          RPCParameter("device", WireType.Device, WireDirection.Out, WireExtent.One),
          RPCParameter("ordinal", WireType.Int32, WireDirection.In, WireExtent.Value)
        ),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuDeviceGetCount",
        cuDeviceGetCountId,
        Vector(RPCParameter("count", WireType.Int32, WireDirection.Out, WireExtent.One)),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuDriverGetVersion",
        cuDriverGetVersionId,
        Vector(RPCParameter("driverVersion", WireType.Int32, WireDirection.Out, WireExtent.One)),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuInit",
        cuInitId,
        Vector(RPCParameter("Flags", WireType.UInt32, WireDirection.In, WireExtent.Value)),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuLaunchHostFunc",
        cuLaunchHostFuncId,
        Vector(
          RPCParameter("hStream", WireType.Stream, WireDirection.In, WireExtent.Value),
          RPCParameter("fn", WireType.CUDAHostCallback, WireDirection.In, WireExtent.Value),
          RPCParameter("userData", WireType.CallbackData, WireDirection.In, WireExtent.Value)
        ),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuLaunchKernel",
        cuLaunchKernelId,
        Vector(
          RPCParameter("f", WireType.CUDAFunction, WireDirection.In, WireExtent.Value),
          RPCParameter("gridDimX", WireType.UInt32, WireDirection.In, WireExtent.Value),
          RPCParameter("gridDimY", WireType.UInt32, WireDirection.In, WireExtent.Value),
          RPCParameter("gridDimZ", WireType.UInt32, WireDirection.In, WireExtent.Value),
          RPCParameter("blockDimX", WireType.UInt32, WireDirection.In, WireExtent.Value),
          RPCParameter("blockDimY", WireType.UInt32, WireDirection.In, WireExtent.Value),
          RPCParameter("blockDimZ", WireType.UInt32, WireDirection.In, WireExtent.Value),
          RPCParameter("sharedMemBytes", WireType.UInt32, WireDirection.In, WireExtent.Value),
          RPCParameter("hStream", WireType.Stream, WireDirection.In, WireExtent.Value),
          RPCParameter("kernelParams", WireType.KernelParameters, WireDirection.In, WireExtent.Value),
          RPCParameter("extra", WireType.NullableOpaquePointer, WireDirection.In, WireExtent.Value)
        ),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuMemAlloc_v2",
        cuMemAlloc_v2Id,
        Vector(
          RPCParameter("dptr", WireType.DevicePtr, WireDirection.Out, WireExtent.One),
          RPCParameter("bytesize", WireType.SizeT, WireDirection.In, WireExtent.Value)
        ),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuMemFree_v2",
        cuMemFree_v2Id,
        Vector(RPCParameter("dptr", WireType.DevicePtr, WireDirection.In, WireExtent.Value)),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuMemcpyDtoD_v2",
        cuMemcpyDtoD_v2Id,
        Vector(
          RPCParameter("dstDevice", WireType.DevicePtr, WireDirection.In, WireExtent.Value),
          RPCParameter("srcDevice", WireType.DevicePtr, WireDirection.In, WireExtent.Value),
          RPCParameter("ByteCount", WireType.SizeT, WireDirection.In, WireExtent.Value)
        ),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuMemcpyDtoH_v2",
        cuMemcpyDtoH_v2Id,
        Vector(
          RPCParameter("dstHost", WireType.ByteBuffer, WireDirection.Out, WireExtent.CountedBy("ByteCount")),
          RPCParameter("srcDevice", WireType.DevicePtr, WireDirection.In, WireExtent.Value),
          RPCParameter("ByteCount", WireType.SizeT, WireDirection.In, WireExtent.Value)
        ),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuMemcpyHtoD_v2",
        cuMemcpyHtoD_v2Id,
        Vector(
          RPCParameter("dstDevice", WireType.DevicePtr, WireDirection.In, WireExtent.Value),
          RPCParameter("srcHost", WireType.ByteBuffer, WireDirection.In, WireExtent.CountedBy("ByteCount")),
          RPCParameter("ByteCount", WireType.SizeT, WireDirection.In, WireExtent.Value)
        ),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuMemsetD8_v2",
        cuMemsetD8_v2Id,
        Vector(
          RPCParameter("dstDevice", WireType.DevicePtr, WireDirection.In, WireExtent.Value),
          RPCParameter("uc", WireType.UInt8, WireDirection.In, WireExtent.Value),
          RPCParameter("N", WireType.SizeT, WireDirection.In, WireExtent.Value)
        ),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuModuleGetFunction",
        cuModuleGetFunctionId,
        Vector(
          RPCParameter("hfunc", WireType.KernelFunction, WireDirection.Out, WireExtent.One),
          RPCParameter("hmod", WireType.CUDAModule, WireDirection.In, WireExtent.Value),
          RPCParameter("name", WireType.FunctionName, WireDirection.In, WireExtent.Value)
        ),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuModuleLoadData",
        cuModuleLoadDataId,
        Vector(
          RPCParameter("module", WireType.CUDAModule, WireDirection.Out, WireExtent.One),
          RPCParameter("image", WireType.ModuleImage, WireDirection.In, WireExtent.Value)
        ),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuModuleUnload",
        cuModuleUnloadId,
        Vector(RPCParameter("hmod", WireType.CUDAModule, WireDirection.In, WireExtent.Value)),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cuStreamAddCallback",
        cuStreamAddCallbackId,
        Vector(
          RPCParameter("hStream", WireType.Stream, WireDirection.In, WireExtent.Value),
          RPCParameter("callback", WireType.CUDAStreamCallback, WireDirection.In, WireExtent.Value),
          RPCParameter("userData", WireType.CallbackData, WireDirection.In, WireExtent.Value),
          RPCParameter("flags", WireType.UInt32, WireDirection.In, WireExtent.Value)
        ),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cudaFree",
        cudaFreeId,
        Vector(RPCParameter("devPtr", WireType.DevicePtr, WireDirection.In, WireExtent.Value)),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cudaGetDeviceCount",
        cudaGetDeviceCountId,
        Vector(RPCParameter("count", WireType.Int32, WireDirection.Out, WireExtent.One)),
        WireType.CUDAStatus
      ),
      RPCMethod(
        "cudaMalloc",
        cudaMallocId,
        Vector(
          RPCParameter("devPtr", WireType.DevicePtr, WireDirection.Out, WireExtent.One),
          RPCParameter("size", WireType.SizeT, WireDirection.In, WireExtent.Value)
        ),
        WireType.CUDAStatus
      )
    )
  )

  final class Client(transport: RPCTransport) extends CUDA {
    require(
      transport.schemaFingerprint == SchemaFingerprint,
      s"CUDA RPC schema mismatch: client=$SchemaFingerprint server=${transport.schemaFingerprint}"
    )

    override def __cudaGemstoneManagedRegister(pointer: DevicePtr, bytesize: SizeT): CUDAStatus =
      transport.call(RPCRequest(
        __cudaGemstoneManagedRegisterId,
        "__cudaGemstoneManagedRegister",
        Vector(WireValue.DevicePtrValue(pointer), WireValue.SizeTValue(bytesize))
      )) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          if (outputs.isEmpty) status else CUDAStatus.Unknown
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def __cudaGemstoneManagedUnregister(pointer: DevicePtr): CUDAStatus =
      transport.call(RPCRequest(
        __cudaGemstoneManagedUnregisterId,
        "__cudaGemstoneManagedUnregister",
        Vector(WireValue.DevicePtrValue(pointer))
      )) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          if (outputs.isEmpty) status else CUDAStatus.Unknown
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuCtxCreate_v2(pctx: Out[Context], flags: UInt32, dev: Device): CUDAStatus =
      transport.call(RPCRequest(
        cuCtxCreate_v2Id,
        "cuCtxCreate_v2",
        Vector(WireValue.UInt32Value(flags), WireValue.DeviceValue(dev))
      )) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          outputs match {
            case Vector(WireValue.ContextValue(output0)) =>
              if (writeOne(pctx, output0)) status else CUDAStatus.InvalidValue
            case _ => CUDAStatus.Unknown
          }
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuCtxDestroy_v2(ctx: Context): CUDAStatus =
      transport.call(RPCRequest(cuCtxDestroy_v2Id, "cuCtxDestroy_v2", Vector(WireValue.ContextValue(ctx)))) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          if (outputs.isEmpty) status else CUDAStatus.Unknown
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuCtxSynchronize(): CUDAStatus =
      transport.call(RPCRequest(cuCtxSynchronizeId, "cuCtxSynchronize", Vector())) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          if (outputs.isEmpty) status else CUDAStatus.Unknown
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuDeviceGet(device: Out[Device], ordinal: Int): CUDAStatus =
      transport.call(RPCRequest(cuDeviceGetId, "cuDeviceGet", Vector(WireValue.Int32Value(ordinal)))) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          outputs match {
            case Vector(WireValue.DeviceValue(output0)) =>
              if (writeOne(device, output0)) status else CUDAStatus.InvalidValue
            case _ => CUDAStatus.Unknown
          }
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuDeviceGetCount(count: Out[Int]): CUDAStatus =
      transport.call(RPCRequest(cuDeviceGetCountId, "cuDeviceGetCount", Vector())) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          outputs match {
            case Vector(WireValue.Int32Value(output0)) =>
              if (writeOne(count, output0)) status else CUDAStatus.InvalidValue
            case _ => CUDAStatus.Unknown
          }
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuDriverGetVersion(driverVersion: Out[Int]): CUDAStatus =
      transport.call(RPCRequest(cuDriverGetVersionId, "cuDriverGetVersion", Vector())) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          outputs match {
            case Vector(WireValue.Int32Value(output0)) =>
              if (writeOne(driverVersion, output0)) status else CUDAStatus.InvalidValue
            case _ => CUDAStatus.Unknown
          }
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuInit(Flags: UInt32): CUDAStatus =
      transport.call(RPCRequest(cuInitId, "cuInit", Vector(WireValue.UInt32Value(Flags)))) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          if (outputs.isEmpty) status else CUDAStatus.Unknown
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuLaunchHostFunc(hStream: Stream, fn: CUhostFn, userData: CallbackData): CUDAStatus =
      transport.call(RPCRequest(
        cuLaunchHostFuncId,
        "cuLaunchHostFunc",
        Vector(
          WireValue.StreamValue(hStream),
          WireValue.CUDAHostCallbackValue(fn),
          WireValue.CallbackDataValue(userData)
        )
      )) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          if (outputs.isEmpty) status else CUDAStatus.Unknown
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuLaunchKernel(
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
        extra: Nullable[Pointer[net.kurobako.gemstone.abi.OpaquePointer, ReadWrite, Opaque]]
    ): CUDAStatus =
      transport.call(RPCRequest(
        cuLaunchKernelId,
        "cuLaunchKernel",
        Vector(
          WireValue.CUDAFunctionValue(f),
          WireValue.UInt32Value(gridDimX),
          WireValue.UInt32Value(gridDimY),
          WireValue.UInt32Value(gridDimZ),
          WireValue.UInt32Value(blockDimX),
          WireValue.UInt32Value(blockDimY),
          WireValue.UInt32Value(blockDimZ),
          WireValue.UInt32Value(sharedMemBytes),
          WireValue.StreamValue(hStream),
          WireValue.KernelParametersValue(kernelParams),
          WireValue.NullableOpaquePointerValue(extra.toOption.nonEmpty)
        )
      )) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          if (outputs.isEmpty) status else CUDAStatus.Unknown
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuMemAlloc_v2(dptr: Out[DevicePtr], bytesize: SizeT): CUDAStatus =
      transport.call(RPCRequest(cuMemAlloc_v2Id, "cuMemAlloc_v2", Vector(WireValue.SizeTValue(bytesize)))) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          outputs match {
            case Vector(WireValue.DevicePtrValue(output0)) =>
              if (writeOne(dptr, output0)) status else CUDAStatus.InvalidValue
            case _ => CUDAStatus.Unknown
          }
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuMemFree_v2(dptr: DevicePtr): CUDAStatus =
      transport.call(RPCRequest(cuMemFree_v2Id, "cuMemFree_v2", Vector(WireValue.DevicePtrValue(dptr)))) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          if (outputs.isEmpty) status else CUDAStatus.Unknown
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuMemcpyDtoD_v2(dstDevice: DevicePtr, srcDevice: DevicePtr, ByteCount: SizeT): CUDAStatus =
      transport.call(RPCRequest(
        cuMemcpyDtoD_v2Id,
        "cuMemcpyDtoD_v2",
        Vector(
          WireValue.DevicePtrValue(dstDevice),
          WireValue.DevicePtrValue(srcDevice),
          WireValue.SizeTValue(ByteCount)
        )
      )) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          if (outputs.isEmpty) status else CUDAStatus.Unknown
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuMemcpyDtoH_v2(
        dstHost: OutputBuffer[Byte, "ByteCount"],
        srcDevice: DevicePtr,
        ByteCount: SizeT
    ): CUDAStatus =
      transport.call(RPCRequest(
        cuMemcpyDtoH_v2Id,
        "cuMemcpyDtoH_v2",
        Vector(WireValue.DevicePtrValue(srcDevice), WireValue.SizeTValue(ByteCount))
      )) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          outputs match {
            case Vector(WireValue.ByteBufferValue(output0)) =>
              if (writeBytes(dstHost, output0)) status else CUDAStatus.InvalidValue
            case _ => CUDAStatus.Unknown
          }
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuMemcpyHtoD_v2(
        dstDevice: DevicePtr,
        srcHost: InputBuffer[Byte, "ByteCount"],
        ByteCount: SizeT
    ): CUDAStatus =
      transport.call(RPCRequest(
        cuMemcpyHtoD_v2Id,
        "cuMemcpyHtoD_v2",
        Vector(
          WireValue.DevicePtrValue(dstDevice),
          WireValue.ByteBufferValue(IArray.from(srcHost.snapshot.getOrElse(Vector.empty))),
          WireValue.SizeTValue(ByteCount)
        )
      )) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          if (outputs.isEmpty) status else CUDAStatus.Unknown
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuMemsetD8_v2(dstDevice: DevicePtr, uc: UInt8, N: SizeT): CUDAStatus =
      transport.call(RPCRequest(
        cuMemsetD8_v2Id,
        "cuMemsetD8_v2",
        Vector(WireValue.DevicePtrValue(dstDevice), WireValue.UInt8Value(uc), WireValue.SizeTValue(N))
      )) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          if (outputs.isEmpty) status else CUDAStatus.Unknown
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuModuleGetFunction(hfunc: Out[KernelFunction], hmod: CUDAModule, name: FunctionName): CUDAStatus =
      transport.call(RPCRequest(
        cuModuleGetFunctionId,
        "cuModuleGetFunction",
        Vector(WireValue.CUDAModuleValue(hmod), WireValue.FunctionNameValue(name))
      )) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          outputs match {
            case Vector(WireValue.KernelFunctionValue(output0)) =>
              if (writeOne(hfunc, output0)) status else CUDAStatus.InvalidValue
            case _ => CUDAStatus.Unknown
          }
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuModuleLoadData(module: Out[CUDAModule], image: ModuleImage): CUDAStatus =
      transport.call(RPCRequest(
        cuModuleLoadDataId,
        "cuModuleLoadData",
        Vector(WireValue.ModuleImageValue(image))
      )) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          outputs match {
            case Vector(WireValue.CUDAModuleValue(output0)) =>
              if (writeOne(module, output0)) status else CUDAStatus.InvalidValue
            case _ => CUDAStatus.Unknown
          }
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuModuleUnload(hmod: CUDAModule): CUDAStatus =
      transport.call(RPCRequest(cuModuleUnloadId, "cuModuleUnload", Vector(WireValue.CUDAModuleValue(hmod)))) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          if (outputs.isEmpty) status else CUDAStatus.Unknown
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cuStreamAddCallback(
        hStream: Stream,
        callback: CUstreamCallback,
        userData: CallbackData,
        flags: UInt32
    ): CUDAStatus =
      transport.call(RPCRequest(
        cuStreamAddCallbackId,
        "cuStreamAddCallback",
        Vector(
          WireValue.StreamValue(hStream),
          WireValue.CUDAStreamCallbackValue(callback),
          WireValue.CallbackDataValue(userData),
          WireValue.UInt32Value(flags)
        )
      )) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          if (outputs.isEmpty) status else CUDAStatus.Unknown
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cudaFree(devPtr: DevicePtr): CUDAStatus =
      transport.call(RPCRequest(cudaFreeId, "cudaFree", Vector(WireValue.DevicePtrValue(devPtr)))) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          if (outputs.isEmpty) status else CUDAStatus.Unknown
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cudaGetDeviceCount(count: Out[Int]): CUDAStatus =
      transport.call(RPCRequest(cudaGetDeviceCountId, "cudaGetDeviceCount", Vector())) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          outputs match {
            case Vector(WireValue.Int32Value(output0)) =>
              if (writeOne(count, output0)) status else CUDAStatus.InvalidValue
            case _ => CUDAStatus.Unknown
          }
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }

    override def cudaMalloc(devPtr: Out[DevicePtr], size: SizeT): CUDAStatus =
      transport.call(RPCRequest(cudaMallocId, "cudaMalloc", Vector(WireValue.SizeTValue(size)))) match {
        case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          outputs match {
            case Vector(WireValue.DevicePtrValue(output0)) =>
              if (writeOne(devPtr, output0)) status else CUDAStatus.InvalidValue
            case _ => CUDAStatus.Unknown
          }
        case RPCResponse.Success(WireValue.CallResult(status, _)) => status
        case _ => CUDAStatus.Unknown
      }
  }

  object Dispatcher extends RPCDispatcher[CUDA] {
    override val schemaFingerprint: String = SchemaFingerprint

    override def dispatch(request: RPCRequest, implementation: CUDA): RPCResponse =
      request.messageId match {
        case `__cudaGemstoneManagedRegisterId` if request.functionName == "__cudaGemstoneManagedRegister" =>
          request.arguments match {
            case Vector(WireValue.DevicePtrValue(value0), WireValue.SizeTValue(value1)) =>
              val argument0 = value0
              val argument1 = value1
              val status = implementation.__cudaGemstoneManagedRegister(argument0, argument1)
              val outputs = if (status.isSuccess) Vector() else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for __cudaGemstoneManagedRegister")
          }
        case `__cudaGemstoneManagedUnregisterId` if request.functionName == "__cudaGemstoneManagedUnregister" =>
          request.arguments match {
            case Vector(WireValue.DevicePtrValue(value0)) =>
              val argument0 = value0
              val status = implementation.__cudaGemstoneManagedUnregister(argument0)
              val outputs = if (status.isSuccess) Vector() else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for __cudaGemstoneManagedUnregister")
          }
        case `cuCtxCreate_v2Id` if request.functionName == "cuCtxCreate_v2" =>
          request.arguments match {
            case Vector(WireValue.UInt32Value(value1), WireValue.DeviceValue(value2)) =>
              val argument0 = Pointer.output[Context, One](1)
              val argument1 = value1
              val argument2 = value2
              val status = implementation.cuCtxCreate_v2(argument0, argument1, argument2)
              val outputs = if (status.isSuccess) Vector(WireValue.ContextValue(argument0.snapshot.flatMap(
                _.headOption
              ).getOrElse(throw new IllegalStateException("pctx was not initialized"))))
              else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuCtxCreate_v2")
          }
        case `cuCtxDestroy_v2Id` if request.functionName == "cuCtxDestroy_v2" =>
          request.arguments match {
            case Vector(WireValue.ContextValue(value0)) =>
              val argument0 = value0
              val status = implementation.cuCtxDestroy_v2(argument0)
              val outputs = if (status.isSuccess) Vector() else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuCtxDestroy_v2")
          }
        case `cuCtxSynchronizeId` if request.functionName == "cuCtxSynchronize" =>
          request.arguments match {
            case Vector() =>
              val status = implementation.cuCtxSynchronize()
              val outputs = if (status.isSuccess) Vector() else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuCtxSynchronize")
          }
        case `cuDeviceGetId` if request.functionName == "cuDeviceGet" =>
          request.arguments match {
            case Vector(WireValue.Int32Value(value1)) =>
              val argument0 = Pointer.output[Device, One](1)
              val argument1 = value1
              val status = implementation.cuDeviceGet(argument0, argument1)
              val outputs = if (status.isSuccess) Vector(WireValue.DeviceValue(argument0.snapshot.flatMap(
                _.headOption
              ).getOrElse(throw new IllegalStateException("device was not initialized"))))
              else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuDeviceGet")
          }
        case `cuDeviceGetCountId` if request.functionName == "cuDeviceGetCount" =>
          request.arguments match {
            case Vector() =>
              val argument0 = Pointer.output[Int, One](1)
              val status = implementation.cuDeviceGetCount(argument0)
              val outputs = if (status.isSuccess) Vector(WireValue.Int32Value(argument0.snapshot.flatMap(
                _.headOption
              ).getOrElse(throw new IllegalStateException("count was not initialized"))))
              else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuDeviceGetCount")
          }
        case `cuDriverGetVersionId` if request.functionName == "cuDriverGetVersion" =>
          request.arguments match {
            case Vector() =>
              val argument0 = Pointer.output[Int, One](1)
              val status = implementation.cuDriverGetVersion(argument0)
              val outputs = if (status.isSuccess) Vector(WireValue.Int32Value(argument0.snapshot.flatMap(
                _.headOption
              ).getOrElse(throw new IllegalStateException("driverVersion was not initialized"))))
              else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuDriverGetVersion")
          }
        case `cuInitId` if request.functionName == "cuInit" =>
          request.arguments match {
            case Vector(WireValue.UInt32Value(value0)) =>
              val argument0 = value0
              val status = implementation.cuInit(argument0)
              val outputs = if (status.isSuccess) Vector() else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuInit")
          }
        case `cuLaunchHostFuncId` if request.functionName == "cuLaunchHostFunc" =>
          request.arguments match {
            case Vector(
                  WireValue.StreamValue(value0),
                  WireValue.CUDAHostCallbackValue(value1),
                  WireValue.CallbackDataValue(value2)
                ) =>
              val argument0 = value0
              val argument1 = value1
              val argument2 = value2
              val status = implementation.cuLaunchHostFunc(argument0, argument1, argument2)
              val outputs = if (status.isSuccess) Vector() else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuLaunchHostFunc")
          }
        case `cuLaunchKernelId` if request.functionName == "cuLaunchKernel" =>
          request.arguments match {
            case Vector(
                  WireValue.CUDAFunctionValue(value0),
                  WireValue.UInt32Value(value1),
                  WireValue.UInt32Value(value2),
                  WireValue.UInt32Value(value3),
                  WireValue.UInt32Value(value4),
                  WireValue.UInt32Value(value5),
                  WireValue.UInt32Value(value6),
                  WireValue.UInt32Value(value7),
                  WireValue.StreamValue(value8),
                  WireValue.KernelParametersValue(value9),
                  WireValue.NullableOpaquePointerValue(value10)
                ) =>
              val argument0 = value0
              val argument1 = value1
              val argument2 = value2
              val argument3 = value3
              val argument4 = value4
              val argument5 = value5
              val argument6 = value6
              val argument7 = value7
              val argument8 = value8
              val argument9 = value9
              val argument10 = if (value10)
                throw new IllegalArgumentException("non-null opaque RPC pointers are unsupported")
              else Nullable.Null
              val status = implementation.cuLaunchKernel(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
                argument10
              )
              val outputs = if (status.isSuccess) Vector() else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuLaunchKernel")
          }
        case `cuMemAlloc_v2Id` if request.functionName == "cuMemAlloc_v2" =>
          request.arguments match {
            case Vector(WireValue.SizeTValue(value1)) =>
              val argument0 = Pointer.output[DevicePtr, One](1)
              val argument1 = value1
              val status = implementation.cuMemAlloc_v2(argument0, argument1)
              val outputs = if (status.isSuccess) Vector(WireValue.DevicePtrValue(argument0.snapshot.flatMap(
                _.headOption
              ).getOrElse(throw new IllegalStateException("dptr was not initialized"))))
              else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuMemAlloc_v2")
          }
        case `cuMemFree_v2Id` if request.functionName == "cuMemFree_v2" =>
          request.arguments match {
            case Vector(WireValue.DevicePtrValue(value0)) =>
              val argument0 = value0
              val status = implementation.cuMemFree_v2(argument0)
              val outputs = if (status.isSuccess) Vector() else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuMemFree_v2")
          }
        case `cuMemcpyDtoD_v2Id` if request.functionName == "cuMemcpyDtoD_v2" =>
          request.arguments match {
            case Vector(
                  WireValue.DevicePtrValue(value0),
                  WireValue.DevicePtrValue(value1),
                  WireValue.SizeTValue(value2)
                ) =>
              val argument0 = value0
              val argument1 = value1
              val argument2 = value2
              val status = implementation.cuMemcpyDtoD_v2(argument0, argument1, argument2)
              val outputs = if (status.isSuccess) Vector() else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuMemcpyDtoD_v2")
          }
        case `cuMemcpyDtoH_v2Id` if request.functionName == "cuMemcpyDtoH_v2" =>
          request.arguments match {
            case Vector(WireValue.DevicePtrValue(value1), WireValue.SizeTValue(value2)) =>
              val argument0 = Pointer.output[Byte, CountedBy["ByteCount"]](value2.toLong.toInt)
              val argument1 = value1
              val argument2 = value2
              val status = implementation.cuMemcpyDtoH_v2(argument0, argument1, argument2)
              val outputs = if (status.isSuccess) Vector(WireValue.ByteBufferValue(IArray.from(
                argument0.snapshot.getOrElse(throw new IllegalStateException("dstHost was not initialized"))
              )))
              else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuMemcpyDtoH_v2")
          }
        case `cuMemcpyHtoD_v2Id` if request.functionName == "cuMemcpyHtoD_v2" =>
          request.arguments match {
            case Vector(
                  WireValue.DevicePtrValue(value0),
                  WireValue.ByteBufferValue(value1),
                  WireValue.SizeTValue(value2)
                ) =>
              val argument0 = value0
              val argument1 = Pointer.input[Byte, CountedBy["ByteCount"]](value1)
              val argument2 = value2
              val status = implementation.cuMemcpyHtoD_v2(argument0, argument1, argument2)
              val outputs = if (status.isSuccess) Vector() else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuMemcpyHtoD_v2")
          }
        case `cuMemsetD8_v2Id` if request.functionName == "cuMemsetD8_v2" =>
          request.arguments match {
            case Vector(WireValue.DevicePtrValue(value0), WireValue.UInt8Value(value1), WireValue.SizeTValue(value2)) =>
              val argument0 = value0
              val argument1 = value1
              val argument2 = value2
              val status = implementation.cuMemsetD8_v2(argument0, argument1, argument2)
              val outputs = if (status.isSuccess) Vector() else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuMemsetD8_v2")
          }
        case `cuModuleGetFunctionId` if request.functionName == "cuModuleGetFunction" =>
          request.arguments match {
            case Vector(WireValue.CUDAModuleValue(value1), WireValue.FunctionNameValue(value2)) =>
              val argument0 = Pointer.output[KernelFunction, One](1)
              val argument1 = value1
              val argument2 = value2
              val status = implementation.cuModuleGetFunction(argument0, argument1, argument2)
              val outputs = if (status.isSuccess) Vector(WireValue.KernelFunctionValue(argument0.snapshot.flatMap(
                _.headOption
              ).getOrElse(throw new IllegalStateException("hfunc was not initialized"))))
              else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuModuleGetFunction")
          }
        case `cuModuleLoadDataId` if request.functionName == "cuModuleLoadData" =>
          request.arguments match {
            case Vector(WireValue.ModuleImageValue(value1)) =>
              val argument0 = Pointer.output[CUDAModule, One](1)
              val argument1 = value1
              val status = implementation.cuModuleLoadData(argument0, argument1)
              val outputs = if (status.isSuccess) Vector(WireValue.CUDAModuleValue(argument0.snapshot.flatMap(
                _.headOption
              ).getOrElse(throw new IllegalStateException("module was not initialized"))))
              else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuModuleLoadData")
          }
        case `cuModuleUnloadId` if request.functionName == "cuModuleUnload" =>
          request.arguments match {
            case Vector(WireValue.CUDAModuleValue(value0)) =>
              val argument0 = value0
              val status = implementation.cuModuleUnload(argument0)
              val outputs = if (status.isSuccess) Vector() else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuModuleUnload")
          }
        case `cuStreamAddCallbackId` if request.functionName == "cuStreamAddCallback" =>
          request.arguments match {
            case Vector(
                  WireValue.StreamValue(value0),
                  WireValue.CUDAStreamCallbackValue(value1),
                  WireValue.CallbackDataValue(value2),
                  WireValue.UInt32Value(value3)
                ) =>
              val argument0 = value0
              val argument1 = value1
              val argument2 = value2
              val argument3 = value3
              val status = implementation.cuStreamAddCallback(argument0, argument1, argument2, argument3)
              val outputs = if (status.isSuccess) Vector() else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cuStreamAddCallback")
          }
        case `cudaFreeId` if request.functionName == "cudaFree" =>
          request.arguments match {
            case Vector(WireValue.DevicePtrValue(value0)) =>
              val argument0 = value0
              val status = implementation.cudaFree(argument0)
              val outputs = if (status.isSuccess) Vector() else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cudaFree")
          }
        case `cudaGetDeviceCountId` if request.functionName == "cudaGetDeviceCount" =>
          request.arguments match {
            case Vector() =>
              val argument0 = Pointer.output[Int, One](1)
              val status = implementation.cudaGetDeviceCount(argument0)
              val outputs = if (status.isSuccess) Vector(WireValue.Int32Value(argument0.snapshot.flatMap(
                _.headOption
              ).getOrElse(throw new IllegalStateException("count was not initialized"))))
              else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cudaGetDeviceCount")
          }
        case `cudaMallocId` if request.functionName == "cudaMalloc" =>
          request.arguments match {
            case Vector(WireValue.SizeTValue(value1)) =>
              val argument0 = Pointer.output[DevicePtr, One](1)
              val argument1 = value1
              val status = implementation.cudaMalloc(argument0, argument1)
              val outputs = if (status.isSuccess) Vector(WireValue.DevicePtrValue(argument0.snapshot.flatMap(
                _.headOption
              ).getOrElse(throw new IllegalStateException("devPtr was not initialized"))))
              else Vector.empty
              RPCResponse.Success(WireValue.CallResult(status, outputs))
            case _ => invalid("invalid arguments for cudaMalloc")
          }
        case _ => invalid(s"unknown or mismatched CUDA RPC function ${request.functionName}")
      }
  }

  private def invalid(message: String): RPCResponse = RPCResponse.Failure(CUDAError(999, message))

  private def writeOne[A, Mode <: Access, Size <: Extent](pointer: Pointer[A, Mode, Size], value: A)(using
      CanWrite[Mode],
      CanDereference[Size]
  ): Boolean = {
    pointer.write(value)
    true
  }

  private def writeBytes[Mode <: Access, Size <: Extent](pointer: Pointer[Byte, Mode, Size], value: IArray[Byte])(using
      CanWrite[Mode],
      CanDereference[Size]
  ): Boolean = {
    if (pointer.length != value.length) false
    else {
      var index = 0
      while (index < value.length) {
        pointer.write(value(index), index)
        index += 1
      }
      true
    }
  }
}

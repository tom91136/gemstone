package net.kurobako.gemstone.generated

import net.kurobako.gemstone.cuda.*
import net.kurobako.gemstone.rpc.*

object GeneratedCUDARPC {
  val SchemaFingerprint: String = "639c93e36ccba7921e326fd8c375efad7e5f086750854fe1ab71d6c4ab391217"

  private val cuCtxCreate_v2Id = MessageId(0xe6eb5473c0a097e3L, 0xa17c8d63cec75a44L)
  private val cuCtxDestroy_v2Id = MessageId(0xb574aacc9b35ee6aL, 0x8135b883071cc261L)
  private val cuCtxSynchronizeId = MessageId(0xe21f80c194a589d4L, 0x4af681a7ef75bb10L)
  private val cuDeviceGetId = MessageId(0xc91170009b2ba6c9L, 0x263d0d32e04e06a7L)
  private val cuDeviceGetCountId = MessageId(0x831f6fffdf16f0d4L, 0x3c498239d16d9867L)
  private val cuDriverGetVersionId = MessageId(0xe584ec56943a425dL, 0x3c512e60803234f9L)
  private val cuInitId = MessageId(0x3c1688958f08660eL, 0x1c2a40d0799c268cL)
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
  private val cudaFreeId = MessageId(0xc9831c51cb850c1eL, 0x0300fb6b734556b4L)
  private val cudaGetDeviceCountId = MessageId(0x29770d4f155d9a98L, 0xccec9231bf109befL)
  private val cudaMallocId = MessageId(0x36207ff276ba04dbL, 0x8ed2474e0fef125dL)

  val Schema: RPCSchema = RPCSchema("net.kurobako.gemstone.cuda.CUDA", SchemaFingerprint, Vector(
    RPCMethod("cuCtxCreate_v2", cuCtxCreate_v2Id, Vector(RPCParameter("flags", WireType.UInt32), RPCParameter("device", WireType.Device)), WireType.Context),
    RPCMethod("cuCtxDestroy_v2", cuCtxDestroy_v2Id, Vector(RPCParameter("context", WireType.Context)), WireType.UnitValue),
    RPCMethod("cuCtxSynchronize", cuCtxSynchronizeId, Vector(), WireType.UnitValue),
    RPCMethod("cuDeviceGet", cuDeviceGetId, Vector(RPCParameter("ordinal", WireType.Int32)), WireType.Device),
    RPCMethod("cuDeviceGetCount", cuDeviceGetCountId, Vector(), WireType.Int32),
    RPCMethod("cuDriverGetVersion", cuDriverGetVersionId, Vector(), WireType.Int32),
    RPCMethod("cuInit", cuInitId, Vector(RPCParameter("flags", WireType.UInt32)), WireType.UnitValue),
    RPCMethod("cuLaunchKernel", cuLaunchKernelId, Vector(RPCParameter("function", WireType.CUDAFunction), RPCParameter("launch", WireType.KernelLaunch)), WireType.UnitValue),
    RPCMethod("cuMemAlloc_v2", cuMemAlloc_v2Id, Vector(RPCParameter("bytesize", WireType.SizeT)), WireType.DevicePtr),
    RPCMethod("cuMemFree_v2", cuMemFree_v2Id, Vector(RPCParameter("pointer", WireType.DevicePtr)), WireType.UnitValue),
    RPCMethod("cuMemcpyDtoD_v2", cuMemcpyDtoD_v2Id, Vector(RPCParameter("destination", WireType.DevicePtr), RPCParameter("source", WireType.DevicePtr), RPCParameter("byteCount", WireType.SizeT)), WireType.UnitValue),
    RPCMethod("cuMemcpyDtoH_v2", cuMemcpyDtoH_v2Id, Vector(RPCParameter("source", WireType.DevicePtr), RPCParameter("byteCount", WireType.SizeT)), WireType.HostOutput),
    RPCMethod("cuMemcpyHtoD_v2", cuMemcpyHtoD_v2Id, Vector(RPCParameter("destination", WireType.DevicePtr), RPCParameter("source", WireType.HostInput)), WireType.UnitValue),
    RPCMethod("cuMemsetD8_v2", cuMemsetD8_v2Id, Vector(RPCParameter("destination", WireType.DevicePtr), RPCParameter("value", WireType.UInt8), RPCParameter("count", WireType.SizeT)), WireType.UnitValue),
    RPCMethod("cuModuleGetFunction", cuModuleGetFunctionId, Vector(RPCParameter("module", WireType.CUDAModule), RPCParameter("name", WireType.FunctionName)), WireType.KernelFunction),
    RPCMethod("cuModuleLoadData", cuModuleLoadDataId, Vector(RPCParameter("image", WireType.ModuleImage)), WireType.CUDAModule),
    RPCMethod("cuModuleUnload", cuModuleUnloadId, Vector(RPCParameter("module", WireType.CUDAModule)), WireType.UnitValue),
    RPCMethod("cudaFree", cudaFreeId, Vector(RPCParameter("pointer", WireType.DevicePtr)), WireType.UnitValue),
    RPCMethod("cudaGetDeviceCount", cudaGetDeviceCountId, Vector(), WireType.Int32),
    RPCMethod("cudaMalloc", cudaMallocId, Vector(RPCParameter("bytesize", WireType.SizeT)), WireType.DevicePtr)
  ))

  final class Client(transport: RPCTransport) extends CUDA {
    require(
      transport.schemaFingerprint == SchemaFingerprint,
      s"CUDA RPC schema mismatch: client=$SchemaFingerprint server=${transport.schemaFingerprint}"
    )

    override def cuCtxCreate_v2(flags: UInt32, device: Device): CUDAResult[Context] =
      decodeContext(transport.call(RPCRequest(cuCtxCreate_v2Id, "cuCtxCreate_v2", Vector(WireValue.UInt32Value(flags), WireValue.DeviceValue(device)))))

    override def cuCtxDestroy_v2(context: Context): CUDAResult[Unit] =
      decodeUnitValue(transport.call(RPCRequest(cuCtxDestroy_v2Id, "cuCtxDestroy_v2", Vector(WireValue.ContextValue(context)))))

    override def cuCtxSynchronize(): CUDAResult[Unit] =
      decodeUnitValue(transport.call(RPCRequest(cuCtxSynchronizeId, "cuCtxSynchronize", Vector())))

    override def cuDeviceGet(ordinal: Int): CUDAResult[Device] =
      decodeDevice(transport.call(RPCRequest(cuDeviceGetId, "cuDeviceGet", Vector(WireValue.Int32Value(ordinal)))))

    override def cuDeviceGetCount(): CUDAResult[Int] =
      decodeInt32(transport.call(RPCRequest(cuDeviceGetCountId, "cuDeviceGetCount", Vector())))

    override def cuDriverGetVersion(): CUDAResult[Int] =
      decodeInt32(transport.call(RPCRequest(cuDriverGetVersionId, "cuDriverGetVersion", Vector())))

    override def cuInit(flags: UInt32): CUDAResult[Unit] =
      decodeUnitValue(transport.call(RPCRequest(cuInitId, "cuInit", Vector(WireValue.UInt32Value(flags)))))

    override def cuLaunchKernel(function: CUDAFunction, launch: KernelLaunch): CUDAResult[Unit] =
      decodeUnitValue(transport.call(RPCRequest(cuLaunchKernelId, "cuLaunchKernel", Vector(WireValue.CUDAFunctionValue(function), WireValue.KernelLaunchValue(launch)))))

    override def cuMemAlloc_v2(bytesize: SizeT): CUDAResult[DevicePtr] =
      decodeDevicePtr(transport.call(RPCRequest(cuMemAlloc_v2Id, "cuMemAlloc_v2", Vector(WireValue.SizeTValue(bytesize)))))

    override def cuMemFree_v2(pointer: DevicePtr): CUDAResult[Unit] =
      decodeUnitValue(transport.call(RPCRequest(cuMemFree_v2Id, "cuMemFree_v2", Vector(WireValue.DevicePtrValue(pointer)))))

    override def cuMemcpyDtoD_v2(destination: DevicePtr, source: DevicePtr, byteCount: SizeT): CUDAResult[Unit] =
      decodeUnitValue(transport.call(RPCRequest(cuMemcpyDtoD_v2Id, "cuMemcpyDtoD_v2", Vector(WireValue.DevicePtrValue(destination), WireValue.DevicePtrValue(source), WireValue.SizeTValue(byteCount)))))

    override def cuMemcpyDtoH_v2(source: DevicePtr, byteCount: SizeT): CUDAResult[HostOutput] =
      decodeHostOutput(transport.call(RPCRequest(cuMemcpyDtoH_v2Id, "cuMemcpyDtoH_v2", Vector(WireValue.DevicePtrValue(source), WireValue.SizeTValue(byteCount)))))

    override def cuMemcpyHtoD_v2(destination: DevicePtr, source: HostInput): CUDAResult[Unit] =
      decodeUnitValue(transport.call(RPCRequest(cuMemcpyHtoD_v2Id, "cuMemcpyHtoD_v2", Vector(WireValue.DevicePtrValue(destination), WireValue.HostInputValue(source)))))

    override def cuMemsetD8_v2(destination: DevicePtr, value: UInt8, count: SizeT): CUDAResult[Unit] =
      decodeUnitValue(transport.call(RPCRequest(cuMemsetD8_v2Id, "cuMemsetD8_v2", Vector(WireValue.DevicePtrValue(destination), WireValue.UInt8Value(value), WireValue.SizeTValue(count)))))

    override def cuModuleGetFunction(module: CUDAModule, name: FunctionName): CUDAResult[KernelFunction] =
      decodeKernelFunction(transport.call(RPCRequest(cuModuleGetFunctionId, "cuModuleGetFunction", Vector(WireValue.CUDAModuleValue(module), WireValue.FunctionNameValue(name)))))

    override def cuModuleLoadData(image: ModuleImage): CUDAResult[CUDAModule] =
      decodeCUDAModule(transport.call(RPCRequest(cuModuleLoadDataId, "cuModuleLoadData", Vector(WireValue.ModuleImageValue(image)))))

    override def cuModuleUnload(module: CUDAModule): CUDAResult[Unit] =
      decodeUnitValue(transport.call(RPCRequest(cuModuleUnloadId, "cuModuleUnload", Vector(WireValue.CUDAModuleValue(module)))))

    override def cudaFree(pointer: DevicePtr): CUDAResult[Unit] =
      decodeUnitValue(transport.call(RPCRequest(cudaFreeId, "cudaFree", Vector(WireValue.DevicePtrValue(pointer)))))

    override def cudaGetDeviceCount(): CUDAResult[Int] =
      decodeInt32(transport.call(RPCRequest(cudaGetDeviceCountId, "cudaGetDeviceCount", Vector())))

    override def cudaMalloc(bytesize: SizeT): CUDAResult[DevicePtr] =
      decodeDevicePtr(transport.call(RPCRequest(cudaMallocId, "cudaMalloc", Vector(WireValue.SizeTValue(bytesize)))))
  }

  object Dispatcher extends RPCDispatcher[CUDA] {
    override val schemaFingerprint: String = SchemaFingerprint

    override def dispatch(request: RPCRequest, implementation: CUDA): RPCResponse =
      request.messageId match {
      case `cuCtxCreate_v2Id` if request.functionName == "cuCtxCreate_v2" =>
        request.arguments match {
          case Vector(WireValue.UInt32Value(flags), WireValue.DeviceValue(device)) => encodeContext(implementation.cuCtxCreate_v2(flags, device))
          case _ => invalid("invalid arguments for cuCtxCreate_v2")
        }
      case `cuCtxDestroy_v2Id` if request.functionName == "cuCtxDestroy_v2" =>
        request.arguments match {
          case Vector(WireValue.ContextValue(context)) => encodeUnitValue(implementation.cuCtxDestroy_v2(context))
          case _ => invalid("invalid arguments for cuCtxDestroy_v2")
        }
      case `cuCtxSynchronizeId` if request.functionName == "cuCtxSynchronize" =>
        request.arguments match {
          case Vector() => encodeUnitValue(implementation.cuCtxSynchronize())
          case _ => invalid("invalid arguments for cuCtxSynchronize")
        }
      case `cuDeviceGetId` if request.functionName == "cuDeviceGet" =>
        request.arguments match {
          case Vector(WireValue.Int32Value(ordinal)) => encodeDevice(implementation.cuDeviceGet(ordinal))
          case _ => invalid("invalid arguments for cuDeviceGet")
        }
      case `cuDeviceGetCountId` if request.functionName == "cuDeviceGetCount" =>
        request.arguments match {
          case Vector() => encodeInt32(implementation.cuDeviceGetCount())
          case _ => invalid("invalid arguments for cuDeviceGetCount")
        }
      case `cuDriverGetVersionId` if request.functionName == "cuDriverGetVersion" =>
        request.arguments match {
          case Vector() => encodeInt32(implementation.cuDriverGetVersion())
          case _ => invalid("invalid arguments for cuDriverGetVersion")
        }
      case `cuInitId` if request.functionName == "cuInit" =>
        request.arguments match {
          case Vector(WireValue.UInt32Value(flags)) => encodeUnitValue(implementation.cuInit(flags))
          case _ => invalid("invalid arguments for cuInit")
        }
      case `cuLaunchKernelId` if request.functionName == "cuLaunchKernel" =>
        request.arguments match {
          case Vector(WireValue.CUDAFunctionValue(function), WireValue.KernelLaunchValue(launch)) => encodeUnitValue(implementation.cuLaunchKernel(function, launch))
          case _ => invalid("invalid arguments for cuLaunchKernel")
        }
      case `cuMemAlloc_v2Id` if request.functionName == "cuMemAlloc_v2" =>
        request.arguments match {
          case Vector(WireValue.SizeTValue(bytesize)) => encodeDevicePtr(implementation.cuMemAlloc_v2(bytesize))
          case _ => invalid("invalid arguments for cuMemAlloc_v2")
        }
      case `cuMemFree_v2Id` if request.functionName == "cuMemFree_v2" =>
        request.arguments match {
          case Vector(WireValue.DevicePtrValue(pointer)) => encodeUnitValue(implementation.cuMemFree_v2(pointer))
          case _ => invalid("invalid arguments for cuMemFree_v2")
        }
      case `cuMemcpyDtoD_v2Id` if request.functionName == "cuMemcpyDtoD_v2" =>
        request.arguments match {
          case Vector(WireValue.DevicePtrValue(destination), WireValue.DevicePtrValue(source), WireValue.SizeTValue(byteCount)) => encodeUnitValue(implementation.cuMemcpyDtoD_v2(destination, source, byteCount))
          case _ => invalid("invalid arguments for cuMemcpyDtoD_v2")
        }
      case `cuMemcpyDtoH_v2Id` if request.functionName == "cuMemcpyDtoH_v2" =>
        request.arguments match {
          case Vector(WireValue.DevicePtrValue(source), WireValue.SizeTValue(byteCount)) => encodeHostOutput(implementation.cuMemcpyDtoH_v2(source, byteCount))
          case _ => invalid("invalid arguments for cuMemcpyDtoH_v2")
        }
      case `cuMemcpyHtoD_v2Id` if request.functionName == "cuMemcpyHtoD_v2" =>
        request.arguments match {
          case Vector(WireValue.DevicePtrValue(destination), WireValue.HostInputValue(source)) => encodeUnitValue(implementation.cuMemcpyHtoD_v2(destination, source))
          case _ => invalid("invalid arguments for cuMemcpyHtoD_v2")
        }
      case `cuMemsetD8_v2Id` if request.functionName == "cuMemsetD8_v2" =>
        request.arguments match {
          case Vector(WireValue.DevicePtrValue(destination), WireValue.UInt8Value(value), WireValue.SizeTValue(count)) => encodeUnitValue(implementation.cuMemsetD8_v2(destination, value, count))
          case _ => invalid("invalid arguments for cuMemsetD8_v2")
        }
      case `cuModuleGetFunctionId` if request.functionName == "cuModuleGetFunction" =>
        request.arguments match {
          case Vector(WireValue.CUDAModuleValue(module), WireValue.FunctionNameValue(name)) => encodeKernelFunction(implementation.cuModuleGetFunction(module, name))
          case _ => invalid("invalid arguments for cuModuleGetFunction")
        }
      case `cuModuleLoadDataId` if request.functionName == "cuModuleLoadData" =>
        request.arguments match {
          case Vector(WireValue.ModuleImageValue(image)) => encodeCUDAModule(implementation.cuModuleLoadData(image))
          case _ => invalid("invalid arguments for cuModuleLoadData")
        }
      case `cuModuleUnloadId` if request.functionName == "cuModuleUnload" =>
        request.arguments match {
          case Vector(WireValue.CUDAModuleValue(module)) => encodeUnitValue(implementation.cuModuleUnload(module))
          case _ => invalid("invalid arguments for cuModuleUnload")
        }
      case `cudaFreeId` if request.functionName == "cudaFree" =>
        request.arguments match {
          case Vector(WireValue.DevicePtrValue(pointer)) => encodeUnitValue(implementation.cudaFree(pointer))
          case _ => invalid("invalid arguments for cudaFree")
        }
      case `cudaGetDeviceCountId` if request.functionName == "cudaGetDeviceCount" =>
        request.arguments match {
          case Vector() => encodeInt32(implementation.cudaGetDeviceCount())
          case _ => invalid("invalid arguments for cudaGetDeviceCount")
        }
      case `cudaMallocId` if request.functionName == "cudaMalloc" =>
        request.arguments match {
          case Vector(WireValue.SizeTValue(bytesize)) => encodeDevicePtr(implementation.cudaMalloc(bytesize))
          case _ => invalid("invalid arguments for cudaMalloc")
        }
        case _ => invalid(s"unknown or mismatched CUDA RPC function ${request.functionName}")
      }
  }

  private def invalid(message: String): RPCResponse = RPCResponse.Failure(CUDAError(999, message))

  private def decodeContext(response: RPCResponse): CUDAResult[Context] = response match {
    case RPCResponse.Success(WireValue.ContextValue(value)) => CUDAResult.Success(value)
    case RPCResponse.Success(_) => CUDAResult.Failure(CUDAError(999, "invalid Context response payload"))
    case RPCResponse.Failure(error) => CUDAResult.Failure(error)
  }

  private def encodeContext(result: CUDAResult[Context]): RPCResponse = result match {
    case CUDAResult.Success(value) => RPCResponse.Success(WireValue.ContextValue(value))
    case CUDAResult.Failure(error) => RPCResponse.Failure(error)
  }

  private def decodeUnitValue(response: RPCResponse): CUDAResult[Unit] = response match {
    case RPCResponse.Success(WireValue.UnitValue) => CUDAResult.Success(())
    case RPCResponse.Success(_) => CUDAResult.Failure(CUDAError(999, "invalid UnitValue response payload"))
    case RPCResponse.Failure(error) => CUDAResult.Failure(error)
  }

  private def encodeUnitValue(result: CUDAResult[Unit]): RPCResponse = result match {
    case CUDAResult.Success(_) => RPCResponse.Success(WireValue.UnitValue)
    case CUDAResult.Failure(error) => RPCResponse.Failure(error)
  }

  private def decodeDevice(response: RPCResponse): CUDAResult[Device] = response match {
    case RPCResponse.Success(WireValue.DeviceValue(value)) => CUDAResult.Success(value)
    case RPCResponse.Success(_) => CUDAResult.Failure(CUDAError(999, "invalid Device response payload"))
    case RPCResponse.Failure(error) => CUDAResult.Failure(error)
  }

  private def encodeDevice(result: CUDAResult[Device]): RPCResponse = result match {
    case CUDAResult.Success(value) => RPCResponse.Success(WireValue.DeviceValue(value))
    case CUDAResult.Failure(error) => RPCResponse.Failure(error)
  }

  private def decodeInt32(response: RPCResponse): CUDAResult[Int] = response match {
    case RPCResponse.Success(WireValue.Int32Value(value)) => CUDAResult.Success(value)
    case RPCResponse.Success(_) => CUDAResult.Failure(CUDAError(999, "invalid Int32 response payload"))
    case RPCResponse.Failure(error) => CUDAResult.Failure(error)
  }

  private def encodeInt32(result: CUDAResult[Int]): RPCResponse = result match {
    case CUDAResult.Success(value) => RPCResponse.Success(WireValue.Int32Value(value))
    case CUDAResult.Failure(error) => RPCResponse.Failure(error)
  }

  private def decodeDevicePtr(response: RPCResponse): CUDAResult[DevicePtr] = response match {
    case RPCResponse.Success(WireValue.DevicePtrValue(value)) => CUDAResult.Success(value)
    case RPCResponse.Success(_) => CUDAResult.Failure(CUDAError(999, "invalid DevicePtr response payload"))
    case RPCResponse.Failure(error) => CUDAResult.Failure(error)
  }

  private def encodeDevicePtr(result: CUDAResult[DevicePtr]): RPCResponse = result match {
    case CUDAResult.Success(value) => RPCResponse.Success(WireValue.DevicePtrValue(value))
    case CUDAResult.Failure(error) => RPCResponse.Failure(error)
  }

  private def decodeHostOutput(response: RPCResponse): CUDAResult[HostOutput] = response match {
    case RPCResponse.Success(WireValue.HostOutputValue(value)) => CUDAResult.Success(value)
    case RPCResponse.Success(_) => CUDAResult.Failure(CUDAError(999, "invalid HostOutput response payload"))
    case RPCResponse.Failure(error) => CUDAResult.Failure(error)
  }

  private def encodeHostOutput(result: CUDAResult[HostOutput]): RPCResponse = result match {
    case CUDAResult.Success(value) => RPCResponse.Success(WireValue.HostOutputValue(value))
    case CUDAResult.Failure(error) => RPCResponse.Failure(error)
  }

  private def decodeKernelFunction(response: RPCResponse): CUDAResult[KernelFunction] = response match {
    case RPCResponse.Success(WireValue.KernelFunctionValue(value)) => CUDAResult.Success(value)
    case RPCResponse.Success(_) => CUDAResult.Failure(CUDAError(999, "invalid KernelFunction response payload"))
    case RPCResponse.Failure(error) => CUDAResult.Failure(error)
  }

  private def encodeKernelFunction(result: CUDAResult[KernelFunction]): RPCResponse = result match {
    case CUDAResult.Success(value) => RPCResponse.Success(WireValue.KernelFunctionValue(value))
    case CUDAResult.Failure(error) => RPCResponse.Failure(error)
  }

  private def decodeCUDAModule(response: RPCResponse): CUDAResult[CUDAModule] = response match {
    case RPCResponse.Success(WireValue.CUDAModuleValue(value)) => CUDAResult.Success(value)
    case RPCResponse.Success(_) => CUDAResult.Failure(CUDAError(999, "invalid CUDAModule response payload"))
    case RPCResponse.Failure(error) => CUDAResult.Failure(error)
  }

  private def encodeCUDAModule(result: CUDAResult[CUDAModule]): RPCResponse = result match {
    case CUDAResult.Success(value) => RPCResponse.Success(WireValue.CUDAModuleValue(value))
    case CUDAResult.Failure(error) => RPCResponse.Failure(error)
  }
}

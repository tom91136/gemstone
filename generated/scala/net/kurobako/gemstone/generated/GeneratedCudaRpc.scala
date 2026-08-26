package net.kurobako.gemstone.generated

import net.kurobako.gemstone.cuda.*
import net.kurobako.gemstone.rpc.*

object GeneratedCudaRpc {
  val SchemaFingerprint: String = "c98d0ee5da0348ca4f1bc94405b4b2bb89e36231ce6e1595384e2d761ddec230"

  private val cuCtxCreate_v2Id = MessageId(0xe6eb5473c0a097e3L, 0xa17c8d63cec75a44L)
  private val cuCtxDestroy_v2Id = MessageId(0xb574aacc9b35ee6aL, 0x8135b883071cc261L)
  private val cuCtxSynchronizeId = MessageId(0xe21f80c194a589d4L, 0x4af681a7ef75bb10L)
  private val cuDeviceGetId = MessageId(0xc91170009b2ba6c9L, 0x263d0d32e04e06a7L)
  private val cuDeviceGetCountId = MessageId(0x831f6fffdf16f0d4L, 0x3c498239d16d9867L)
  private val cuDriverGetVersionId = MessageId(0xe584ec56943a425dL, 0x3c512e60803234f9L)
  private val cuInitId = MessageId(0x3c1688958f08660eL, 0x1c2a40d0799c268cL)
  private val cuMemAlloc_v2Id = MessageId(0x7077a5441f532f0eL, 0x677e154cf996113dL)
  private val cuMemFree_v2Id = MessageId(0xb5fdc569871ec54eL, 0x418d186e2d8efa3aL)
  private val cuMemcpyDtoD_v2Id = MessageId(0xe27c11476bbea751L, 0x681167b2aac69936L)
  private val cuMemcpyDtoH_v2Id = MessageId(0x0cc3708077b8e76aL, 0xaa0bf9a6432ca141L)
  private val cuMemcpyHtoD_v2Id = MessageId(0x9a8f59036790ef69L, 0x4808ed612739b8faL)
  private val cuMemsetD8_v2Id = MessageId(0x2c1383458eec311aL, 0x73e621c93fd80644L)
  private val cudaFreeId = MessageId(0xc9831c51cb850c1eL, 0x0300fb6b734556b4L)
  private val cudaGetDeviceCountId = MessageId(0x29770d4f155d9a98L, 0xccec9231bf109befL)
  private val cudaMallocId = MessageId(0x36207ff276ba04dbL, 0x8ed2474e0fef125dL)

  val Schema: RpcSchema = RpcSchema("net.kurobako.gemstone.cuda.CUDA", SchemaFingerprint, Vector(
    RpcMethod("cuCtxCreate_v2", cuCtxCreate_v2Id, Vector(RpcParameter("flags", WireType.UInt32), RpcParameter("device", WireType.Device)), WireType.Context),
    RpcMethod("cuCtxDestroy_v2", cuCtxDestroy_v2Id, Vector(RpcParameter("context", WireType.Context)), WireType.UnitValue),
    RpcMethod("cuCtxSynchronize", cuCtxSynchronizeId, Vector(), WireType.UnitValue),
    RpcMethod("cuDeviceGet", cuDeviceGetId, Vector(RpcParameter("ordinal", WireType.Int32)), WireType.Device),
    RpcMethod("cuDeviceGetCount", cuDeviceGetCountId, Vector(), WireType.Int32),
    RpcMethod("cuDriverGetVersion", cuDriverGetVersionId, Vector(), WireType.Int32),
    RpcMethod("cuInit", cuInitId, Vector(RpcParameter("flags", WireType.UInt32)), WireType.UnitValue),
    RpcMethod("cuMemAlloc_v2", cuMemAlloc_v2Id, Vector(RpcParameter("bytesize", WireType.SizeT)), WireType.DevicePtr),
    RpcMethod("cuMemFree_v2", cuMemFree_v2Id, Vector(RpcParameter("pointer", WireType.DevicePtr)), WireType.UnitValue),
    RpcMethod("cuMemcpyDtoD_v2", cuMemcpyDtoD_v2Id, Vector(RpcParameter("destination", WireType.DevicePtr), RpcParameter("source", WireType.DevicePtr), RpcParameter("byteCount", WireType.SizeT)), WireType.UnitValue),
    RpcMethod("cuMemcpyDtoH_v2", cuMemcpyDtoH_v2Id, Vector(RpcParameter("source", WireType.DevicePtr), RpcParameter("byteCount", WireType.SizeT)), WireType.HostOutput),
    RpcMethod("cuMemcpyHtoD_v2", cuMemcpyHtoD_v2Id, Vector(RpcParameter("destination", WireType.DevicePtr), RpcParameter("source", WireType.HostInput)), WireType.UnitValue),
    RpcMethod("cuMemsetD8_v2", cuMemsetD8_v2Id, Vector(RpcParameter("destination", WireType.DevicePtr), RpcParameter("value", WireType.UInt8), RpcParameter("count", WireType.SizeT)), WireType.UnitValue),
    RpcMethod("cudaFree", cudaFreeId, Vector(RpcParameter("pointer", WireType.DevicePtr)), WireType.UnitValue),
    RpcMethod("cudaGetDeviceCount", cudaGetDeviceCountId, Vector(), WireType.Int32),
    RpcMethod("cudaMalloc", cudaMallocId, Vector(RpcParameter("bytesize", WireType.SizeT)), WireType.DevicePtr)
  ))

  final class Client(transport: RpcTransport) extends CUDA {
    require(
      transport.schemaFingerprint == SchemaFingerprint,
      s"CUDA RPC schema mismatch: client=$SchemaFingerprint server=${transport.schemaFingerprint}"
    )

    override def cuCtxCreate_v2(flags: UInt32, device: Device): CudaResult[Context] =
      decodeContext(transport.call(RpcRequest(cuCtxCreate_v2Id, "cuCtxCreate_v2", Vector(WireValue.UInt32Value(flags), WireValue.DeviceValue(device)))))

    override def cuCtxDestroy_v2(context: Context): CudaResult[Unit] =
      decodeUnitValue(transport.call(RpcRequest(cuCtxDestroy_v2Id, "cuCtxDestroy_v2", Vector(WireValue.ContextValue(context)))))

    override def cuCtxSynchronize(): CudaResult[Unit] =
      decodeUnitValue(transport.call(RpcRequest(cuCtxSynchronizeId, "cuCtxSynchronize", Vector())))

    override def cuDeviceGet(ordinal: Int): CudaResult[Device] =
      decodeDevice(transport.call(RpcRequest(cuDeviceGetId, "cuDeviceGet", Vector(WireValue.Int32Value(ordinal)))))

    override def cuDeviceGetCount(): CudaResult[Int] =
      decodeInt32(transport.call(RpcRequest(cuDeviceGetCountId, "cuDeviceGetCount", Vector())))

    override def cuDriverGetVersion(): CudaResult[Int] =
      decodeInt32(transport.call(RpcRequest(cuDriverGetVersionId, "cuDriverGetVersion", Vector())))

    override def cuInit(flags: UInt32): CudaResult[Unit] =
      decodeUnitValue(transport.call(RpcRequest(cuInitId, "cuInit", Vector(WireValue.UInt32Value(flags)))))

    override def cuMemAlloc_v2(bytesize: SizeT): CudaResult[DevicePtr] =
      decodeDevicePtr(transport.call(RpcRequest(cuMemAlloc_v2Id, "cuMemAlloc_v2", Vector(WireValue.SizeTValue(bytesize)))))

    override def cuMemFree_v2(pointer: DevicePtr): CudaResult[Unit] =
      decodeUnitValue(transport.call(RpcRequest(cuMemFree_v2Id, "cuMemFree_v2", Vector(WireValue.DevicePtrValue(pointer)))))

    override def cuMemcpyDtoD_v2(destination: DevicePtr, source: DevicePtr, byteCount: SizeT): CudaResult[Unit] =
      decodeUnitValue(transport.call(RpcRequest(cuMemcpyDtoD_v2Id, "cuMemcpyDtoD_v2", Vector(WireValue.DevicePtrValue(destination), WireValue.DevicePtrValue(source), WireValue.SizeTValue(byteCount)))))

    override def cuMemcpyDtoH_v2(source: DevicePtr, byteCount: SizeT): CudaResult[HostOutput] =
      decodeHostOutput(transport.call(RpcRequest(cuMemcpyDtoH_v2Id, "cuMemcpyDtoH_v2", Vector(WireValue.DevicePtrValue(source), WireValue.SizeTValue(byteCount)))))

    override def cuMemcpyHtoD_v2(destination: DevicePtr, source: HostInput): CudaResult[Unit] =
      decodeUnitValue(transport.call(RpcRequest(cuMemcpyHtoD_v2Id, "cuMemcpyHtoD_v2", Vector(WireValue.DevicePtrValue(destination), WireValue.HostInputValue(source)))))

    override def cuMemsetD8_v2(destination: DevicePtr, value: UInt8, count: SizeT): CudaResult[Unit] =
      decodeUnitValue(transport.call(RpcRequest(cuMemsetD8_v2Id, "cuMemsetD8_v2", Vector(WireValue.DevicePtrValue(destination), WireValue.UInt8Value(value), WireValue.SizeTValue(count)))))

    override def cudaFree(pointer: DevicePtr): CudaResult[Unit] =
      decodeUnitValue(transport.call(RpcRequest(cudaFreeId, "cudaFree", Vector(WireValue.DevicePtrValue(pointer)))))

    override def cudaGetDeviceCount(): CudaResult[Int] =
      decodeInt32(transport.call(RpcRequest(cudaGetDeviceCountId, "cudaGetDeviceCount", Vector())))

    override def cudaMalloc(bytesize: SizeT): CudaResult[DevicePtr] =
      decodeDevicePtr(transport.call(RpcRequest(cudaMallocId, "cudaMalloc", Vector(WireValue.SizeTValue(bytesize)))))
  }

  object Dispatcher extends RpcDispatcher[CUDA] {
    override val schemaFingerprint: String = SchemaFingerprint

    override def dispatch(request: RpcRequest, implementation: CUDA): RpcResponse =
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

  private def invalid(message: String): RpcResponse = RpcResponse.Failure(CudaError(999, message))

  private def decodeContext(response: RpcResponse): CudaResult[Context] = response match {
    case RpcResponse.Success(WireValue.ContextValue(value)) => CudaResult.Success(value)
    case RpcResponse.Success(_) => CudaResult.Failure(CudaError(999, "invalid Context response payload"))
    case RpcResponse.Failure(error) => CudaResult.Failure(error)
  }

  private def encodeContext(result: CudaResult[Context]): RpcResponse = result match {
    case CudaResult.Success(value) => RpcResponse.Success(WireValue.ContextValue(value))
    case CudaResult.Failure(error) => RpcResponse.Failure(error)
  }

  private def decodeUnitValue(response: RpcResponse): CudaResult[Unit] = response match {
    case RpcResponse.Success(WireValue.UnitValue) => CudaResult.Success(())
    case RpcResponse.Success(_) => CudaResult.Failure(CudaError(999, "invalid UnitValue response payload"))
    case RpcResponse.Failure(error) => CudaResult.Failure(error)
  }

  private def encodeUnitValue(result: CudaResult[Unit]): RpcResponse = result match {
    case CudaResult.Success(_) => RpcResponse.Success(WireValue.UnitValue)
    case CudaResult.Failure(error) => RpcResponse.Failure(error)
  }

  private def decodeDevice(response: RpcResponse): CudaResult[Device] = response match {
    case RpcResponse.Success(WireValue.DeviceValue(value)) => CudaResult.Success(value)
    case RpcResponse.Success(_) => CudaResult.Failure(CudaError(999, "invalid Device response payload"))
    case RpcResponse.Failure(error) => CudaResult.Failure(error)
  }

  private def encodeDevice(result: CudaResult[Device]): RpcResponse = result match {
    case CudaResult.Success(value) => RpcResponse.Success(WireValue.DeviceValue(value))
    case CudaResult.Failure(error) => RpcResponse.Failure(error)
  }

  private def decodeInt32(response: RpcResponse): CudaResult[Int] = response match {
    case RpcResponse.Success(WireValue.Int32Value(value)) => CudaResult.Success(value)
    case RpcResponse.Success(_) => CudaResult.Failure(CudaError(999, "invalid Int32 response payload"))
    case RpcResponse.Failure(error) => CudaResult.Failure(error)
  }

  private def encodeInt32(result: CudaResult[Int]): RpcResponse = result match {
    case CudaResult.Success(value) => RpcResponse.Success(WireValue.Int32Value(value))
    case CudaResult.Failure(error) => RpcResponse.Failure(error)
  }

  private def decodeDevicePtr(response: RpcResponse): CudaResult[DevicePtr] = response match {
    case RpcResponse.Success(WireValue.DevicePtrValue(value)) => CudaResult.Success(value)
    case RpcResponse.Success(_) => CudaResult.Failure(CudaError(999, "invalid DevicePtr response payload"))
    case RpcResponse.Failure(error) => CudaResult.Failure(error)
  }

  private def encodeDevicePtr(result: CudaResult[DevicePtr]): RpcResponse = result match {
    case CudaResult.Success(value) => RpcResponse.Success(WireValue.DevicePtrValue(value))
    case CudaResult.Failure(error) => RpcResponse.Failure(error)
  }

  private def decodeHostOutput(response: RpcResponse): CudaResult[HostOutput] = response match {
    case RpcResponse.Success(WireValue.HostOutputValue(value)) => CudaResult.Success(value)
    case RpcResponse.Success(_) => CudaResult.Failure(CudaError(999, "invalid HostOutput response payload"))
    case RpcResponse.Failure(error) => CudaResult.Failure(error)
  }

  private def encodeHostOutput(result: CudaResult[HostOutput]): RpcResponse = result match {
    case CudaResult.Success(value) => RpcResponse.Success(WireValue.HostOutputValue(value))
    case CudaResult.Failure(error) => RpcResponse.Failure(error)
  }
}

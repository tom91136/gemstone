package net.kurobako.gemstone.rpc

final case class MessageId(high: Long, low: Long) {
  def hex: String = f"$high%016x$low%016x"
}

enum WireType {
  case UnitValue
  case Int32
  case UInt8
  case UInt32
  case SizeT
  case Device
  case DevicePtr
  case Context
  case CUDAModule
  case CUDAFunction
  case HostInput
  case HostOutput
  case ModuleImage
  case FunctionName
  case KernelFunction
  case KernelLaunch
  case Stream
  case ByteBuffer
  case KernelParameters
  case NullableOpaquePointer
  case CUDAStatus

  /** Stable protocol spelling. Scala identifier casing may evolve without silently changing the RPC schema. */
  def protocolName: String = this match {
    case UnitValue             => "UnitValue"
    case Int32                 => "Int32"
    case UInt8                 => "UInt8"
    case UInt32                => "UInt32"
    case SizeT                 => "SizeT"
    case Device                => "Device"
    case DevicePtr             => "DevicePtr"
    case Context               => "Context"
    case CUDAModule            => "CudaModule"
    case CUDAFunction          => "CudaFunction"
    case HostInput             => "HostInput"
    case HostOutput            => "HostOutput"
    case ModuleImage           => "ModuleImage"
    case FunctionName          => "FunctionName"
    case KernelFunction        => "KernelFunction"
    case KernelLaunch          => "KernelLaunch"
    case Stream                => "Stream"
    case ByteBuffer            => "ByteBuffer"
    case KernelParameters      => "KernelParameters"
    case NullableOpaquePointer => "NullableOpaquePointer"
    case CUDAStatus            => "CUDAStatus"
  }
}

enum WireDirection {
  case In
  case Out
  case InOut
}

enum WireExtent {
  case Value
  case One
  case Opaque
  case CountedBy(parameter: String)
  case Fixed(count: Int)
}

final case class RPCParameter(
    name: String,
    wireType: WireType,
    direction: WireDirection = WireDirection.In,
    extent: WireExtent = WireExtent.Value
)

final case class RPCMethod(
    name: String,
    messageId: MessageId,
    parameters: Vector[RPCParameter],
    result: WireType
)

final case class RPCSchema(
    interfaceName: String,
    fingerprint: String,
    methods: Vector[RPCMethod]
)

enum WireValue {
  case UnitValue
  case Int32Value(value: Int)
  case UInt8Value(value: net.kurobako.gemstone.cuda.UInt8)
  case UInt32Value(value: net.kurobako.gemstone.cuda.UInt32)
  case SizeTValue(value: net.kurobako.gemstone.cuda.SizeT)
  case DeviceValue(value: net.kurobako.gemstone.cuda.Device)
  case DevicePtrValue(value: net.kurobako.gemstone.cuda.DevicePtr)
  case ContextValue(value: net.kurobako.gemstone.cuda.Context)
  case CUDAModuleValue(value: net.kurobako.gemstone.cuda.CUDAModule)
  case CUDAFunctionValue(value: net.kurobako.gemstone.cuda.CUDAFunction)
  case HostInputValue(value: net.kurobako.gemstone.cuda.HostInput)
  case HostOutputValue(value: net.kurobako.gemstone.cuda.HostOutput)
  case ModuleImageValue(value: net.kurobako.gemstone.cuda.ModuleImage)
  case FunctionNameValue(value: net.kurobako.gemstone.cuda.FunctionName)
  case KernelFunctionValue(value: net.kurobako.gemstone.cuda.KernelFunction)
  case KernelLaunchValue(value: net.kurobako.gemstone.cuda.KernelLaunch)
  case StreamValue(value: net.kurobako.gemstone.cuda.Stream)
  case ByteBufferValue(value: IArray[Byte])
  case KernelParametersValue(value: net.kurobako.gemstone.cuda.KernelParameters)
  case NullableOpaquePointerValue(present: Boolean)
  case CUDAStatusValue(value: net.kurobako.gemstone.cuda.CUDAStatus)
  case CallResult(status: net.kurobako.gemstone.cuda.CUDAStatus, outputs: Vector[WireValue])
}

final case class RPCRequest(
    messageId: MessageId,
    functionName: String,
    arguments: Vector[WireValue]
)

enum RPCResponse {
  case Success(value: WireValue)
  case Failure(error: net.kurobako.gemstone.cuda.CUDAError)
}

trait RPCTransport {
  def schemaFingerprint: String
  def call(request: RPCRequest): RPCResponse
}

trait RPCDispatcher[-T] {
  def schemaFingerprint: String
  def dispatch(request: RPCRequest, implementation: T): RPCResponse
}

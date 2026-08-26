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
  case HostInput
  case HostOutput
}

final case class RpcParameter(name: String, wireType: WireType)

final case class RpcMethod(
    name: String,
    messageId: MessageId,
    parameters: Vector[RpcParameter],
    result: WireType
)

final case class RpcSchema(
    interfaceName: String,
    fingerprint: String,
    methods: Vector[RpcMethod]
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
  case HostInputValue(value: net.kurobako.gemstone.cuda.HostInput)
  case HostOutputValue(value: net.kurobako.gemstone.cuda.HostOutput)
}

final case class RpcRequest(
    messageId: MessageId,
    functionName: String,
    arguments: Vector[WireValue]
)

enum RpcResponse {
  case Success(value: WireValue)
  case Failure(error: net.kurobako.gemstone.cuda.CudaError)
}

trait RpcTransport {
  def schemaFingerprint: String
  def call(request: RpcRequest): RpcResponse
}

trait RpcDispatcher[-T] {
  def schemaFingerprint: String
  def dispatch(request: RpcRequest, implementation: T): RpcResponse
}

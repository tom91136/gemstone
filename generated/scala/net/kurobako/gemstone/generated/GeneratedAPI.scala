package net.kurobako.gemstone.generated

object GeneratedAPI {
  final val ProtocolVersion: Int = 1

  object Opcode {
    final val Shutdown: Int = 0
    final val CUDAGetDeviceCount: Int = 1
    final val GemsiAddI32: Int = 2
  }

  opaque type DispatchResult = Long

  object DispatchResult {
    def apply(status: Int, result0: Long): DispatchResult =
      (status.toLong << 32) | (result0 & 0xffffffffL)

    extension (result: DispatchResult) {
      def status: Int   = (result >>> 32).toInt
      def result0: Long = result.toInt.toLong
    }
  }

  trait Handler {
    def cudaGetDeviceCount(): DispatchResult
    def gemsiAddI32(left: Int, right: Int): DispatchResult
  }

  def dispatch(opcode: Int, argument0: Long, argument1: Long, handler: Handler): DispatchResult = {
    opcode match {
      case Opcode.CUDAGetDeviceCount => handler.cudaGetDeviceCount()
      case Opcode.GemsiAddI32 => handler.gemsiAddI32(argument0.toInt, argument1.toInt)
      case _ => DispatchResult(status = 999, result0 = 0L)
    }
  }
}

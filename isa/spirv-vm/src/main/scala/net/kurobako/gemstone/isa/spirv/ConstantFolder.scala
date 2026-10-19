package net.kurobako.gemstone.isa.spirv

import LaneOps.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*

/** The sink family that evaluates semantics as closures outside an op, for specialisation constant folding. Unary
  * kernels take the binary shape and ignore their second operand; constants never take the other shapes.
  */
private object ConstantFolder extends SPIRVSinks[Long, Kernel2] {
  def width(typeId: Int): Int in 1 ~ 64                                  = 64
  def floatingWidth(typeId: Int): Int in (16, 32, 64)                    = 64
  def typeOf(value: Int): Int                                            = -1
  def lanes(typeId: Int): Int                                            = 1
  def memberType(typeId: Int, index: Int): Int                           = -1
  def rounding(result: Int, default: IEEE754.Rounding): IEEE754.Rounding = default
  def saturates(result: Int): Boolean                                    = false

  /** What a semantics family returns for an opcode outside it. */
  val unmatched: Kernel2 = (_, _) => 0L

  /** The kernel `kernel` gives, unless a family returned [[unmatched]]. */
  def matched(kernel: Kernel2): Option[Kernel2] = Option.when(kernel ne unmatched)(kernel)

  private def unsupported: Nothing = throw IllegalArgumentException("constant folding evaluates only lane kernels")

  inline def unary(ids: OperandIds)(inline kernel: Long => Long): Kernel2                 = (a, _) => kernel(a)
  inline def binary(ids: OperandIds)(inline kernel: (Long, Long) => Long): Kernel2        = (a, b) => kernel(a, b)
  inline def ternary(ids: OperandIds)(inline kernel: (Long, Long, Long) => Long): Kernel2 = unsupported
  inline def quaternary(ids: OperandIds)(inline kernel: (Long, Long, Long, Long) => Long): Kernel2        = unsupported
  inline def broadcast(ids: OperandIds)(inline kernel: Long => Long): Kernel2                             = unsupported
  inline def indexed(ids: OperandIds)(inline kernel: (Long, Long) => Long): Kernel2                       = unsupported
  inline def structure(ids: OperandIds)(inline first: Long => Long, inline second: Long => Long): Kernel2 = unsupported
  inline def binaryStructure(ids: OperandIds)(
      inline first: (Long, Long) => Long,
      inline second: (Long, Long) => Long
  ): Kernel2                                                                                          = unsupported
  inline def store(ids: OperandIds)(inline first: Long => Long, inline second: Long => Long): Kernel2 = unsupported
  inline def binaryStore(ids: OperandIds)(inline first: (Long, Long) => Long, inline second: (Long, Long) => Long)
      : Kernel2 = unsupported
  inline def fold(ids: OperandIds)(inline initial: Long, inline step: (Long, Long) => Long, inline finish: Long => Long)
      : Kernel2 = unsupported
  inline def binaryFold(ids: OperandIds)(
      inline initial: Long,
      inline step: (Long, Long, Long) => Long,
      inline finish: Long => Long
  ): Kernel2 = unsupported
  inline def accumulatedFold(ids: OperandIds)(
      inline initial: Long,
      inline step: (Long, Long, Long) => Long,
      inline finish: (Long, Long) => Long
  ): Kernel2                                                                            = unsupported
  inline def product(ids: OperandIds)(inline step: (Long, Long, Long) => Long): Kernel2 = unsupported
  inline def matrixAccumulate(ids: OperandIds)(
      inline initial: Long,
      inline step: (Long, Long, Long) => Long,
      inline finish: (Long, Long) => Long
  ): Kernel2                                                                                  = unsupported
  inline def outerProduct(ids: OperandIds)(inline kernel: (Long, Long) => Long): Kernel2      = unsupported
  inline def cross(ids: OperandIds)(inline kernel: (Long, Long, Long, Long) => Long): Kernel2 = unsupported
  inline def normalised(ids: OperandIds)(
      inline scale: (Long, Long) => Long,
      inline norm: (Long, Long, Long) => Long,
      inline element: (Long, Long, Long) => Long
  ): Kernel2 = unsupported
  inline def projection(ids: OperandIds, dotted: Int)(
      inline initial: Long,
      inline step: (Long, Long, Long) => Long,
      inline element: (Long, Long, Long, Long) => Long
  ): Kernel2 = unsupported
  inline def bitFieldExtract(ids: OperandIds)(
      inline inRange: (Long, Long) => Long,
      inline kernel: (Long, Long, Long) => Long
  ): Kernel2 = unsupported
  inline def bitFieldInsert(ids: OperandIds)(
      inline inRange: (Long, Long) => Long,
      inline kernel: (Long, Long, Long, Long) => Long
  ): Kernel2 = unsupported
  inline def unsignedConversion(ids: OperandIds)(inline representable: Long => Long, inline kernel: Long => Long)
      : Kernel2                                                                                         = unsupported
  inline def atomic(ids: OperandIds, anyStorage: Boolean)(inline kernel: (Long, Long) => Long): Kernel2 = unsupported
  inline def atomicUpdate(ids: OperandIds)(inline kernel: Long => Long): Kernel2                        = unsupported
  inline def compareExchange(ids: OperandIds)(inline kernel: (Long, Long, Long) => Long): Kernel2       = unsupported
  inline def addresses(ids: OperandIds)(inline kernel: (Long, Long) => Long): Kernel2                   = unsupported
  inline def addressDifference(ids: OperandIds)(inline kernel: (Long, Long, Long) => Long): Kernel2     = unsupported
  inline def halfLoad(ids: OperandIds, aligned: Boolean)(inline convert: Long => Long): Kernel2         = unsupported
  inline def halfStore(ids: OperandIds, aligned: Boolean)(inline convert: Long => Long): Kernel2        = unsupported
}

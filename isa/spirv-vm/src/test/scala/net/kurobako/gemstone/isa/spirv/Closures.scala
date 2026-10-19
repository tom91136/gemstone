package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*

/** The sink family that turns staged semantics into functions from operands to results, both in the order the shape
  * reads and writes them: a lane per operand, the lanes of each operand of a fold in turn, and a second result after
  * the first. Widths and lane counts come from the semantics' arguments, never from these queries.
  */
object Closures extends SPIRVSinks[Long, Seq[Long] => Seq[Long]] {
  type Closure = Seq[Long] => Seq[Long]

  def width(typeId: Int): Int in 1 ~ 64                                  = 64
  def floatingWidth(typeId: Int): Int in (16, 32, 64)                    = 64
  def typeOf(value: Int): Int                                            = -1
  def lanes(typeId: Int): Int                                            = 1
  def memberType(typeId: Int, index: Int): Int                           = -1
  def rounding(result: Int, default: IEEE754.Rounding): IEEE754.Rounding = default
  def saturates(result: Int): Boolean                                    = false

  private def unsupported: Nothing = throw UnsupportedOperationException("the shape spans whole vectors or matrices")

  inline def unary(ids: OperandIds)(inline kernel: Long => Long): Closure          = in => Seq(kernel(in.head))
  inline def binary(ids: OperandIds)(inline kernel: (Long, Long) => Long): Closure = in => Seq(kernel(in(0), in(1)))
  inline def ternary(ids: OperandIds)(inline kernel: (Long, Long, Long) => Long): Closure =
    in => Seq(kernel(in(0), in(1), in(2)))
  inline def quaternary(ids: OperandIds)(inline kernel: (Long, Long, Long, Long) => Long): Closure =
    in => Seq(kernel(in(0), in(1), in(2), in(3)))
  inline def broadcast(ids: OperandIds)(inline kernel: Long => Long): Closure       = in => Seq(kernel(in.head))
  inline def indexed(ids: OperandIds)(inline kernel: (Long, Long) => Long): Closure = in => Seq(kernel(in(0), in(1)))
  inline def structure(ids: OperandIds)(inline first: Long => Long, inline second: Long => Long): Closure =
    in => Seq(first(in.head), second(in.head))
  inline def binaryStructure(ids: OperandIds)(inline first: (Long, Long) => Long, inline second: (Long, Long) => Long)
      : Closure = in => Seq(first(in(0), in(1)), second(in(0), in(1)))
  inline def store(ids: OperandIds)(inline first: Long => Long, inline second: Long => Long): Closure =
    in => Seq(first(in.head), second(in.head))
  inline def binaryStore(ids: OperandIds)(inline first: (Long, Long) => Long, inline second: (Long, Long) => Long)
      : Closure = in => Seq(first(in(0), in(1)), second(in(0), in(1)))
  inline def fold(ids: OperandIds)(inline initial: Long, inline step: (Long, Long) => Long, inline finish: Long => Long)
      : Closure = in => Seq(finish(in.foldLeft(initial)((sum, a) => step(sum, a))))
  inline def binaryFold(ids: OperandIds)(
      inline initial: Long,
      inline step: (Long, Long, Long) => Long,
      inline finish: Long => Long
  ): Closure = in => {
    val (left, right) = in.splitAt(in.size / 2)
    Seq(finish(left.indices.foldLeft(initial)((sum, lane) => step(sum, left(lane), right(lane)))))
  }
  inline def accumulatedFold(ids: OperandIds)(
      inline initial: Long,
      inline step: (Long, Long, Long) => Long,
      inline finish: (Long, Long) => Long
  ): Closure = in => {
    val (left, right) = in.init.splitAt(in.size / 2)
    Seq(finish(left.indices.foldLeft(initial)((sum, lane) => step(sum, left(lane), right(lane))), in.last))
  }
  inline def product(ids: OperandIds)(inline step: (Long, Long, Long) => Long): Closure =
    in => Seq(step(in(0), in(1), in(2)))
  inline def matrixAccumulate(ids: OperandIds)(
      inline initial: Long,
      inline step: (Long, Long, Long) => Long,
      inline finish: (Long, Long) => Long
  ): Closure                                                                             = unsupported
  inline def outerProduct(ids: OperandIds)(inline kernel: (Long, Long) => Long): Closure =
    in => Seq(kernel(in(0), in(1)))
  inline def cross(ids: OperandIds)(inline kernel: (Long, Long, Long, Long) => Long): Closure = unsupported
  inline def projection(ids: OperandIds, dotted: Int)(
      inline initial: Long,
      inline step: (Long, Long, Long) => Long,
      inline element: (Long, Long, Long, Long) => Long
  ): Closure = unsupported
  inline def normalised(ids: OperandIds)(
      inline scale: (Long, Long) => Long,
      inline norm: (Long, Long, Long) => Long,
      inline element: (Long, Long, Long) => Long
  ): Closure = unsupported
  inline def bitFieldExtract(ids: OperandIds)(
      inline inRange: (Long, Long) => Long,
      inline kernel: (Long, Long, Long) => Long
  )
      : Closure = in => Seq(kernel(in(0), in(1), in(2)), inRange(in(1), in(2)))
  inline def bitFieldInsert(ids: OperandIds)(
      inline inRange: (Long, Long) => Long,
      inline kernel: (Long, Long, Long, Long) => Long
  ): Closure = in => Seq(kernel(in(0), in(1), in(2), in(3)), inRange(in(2), in(3)))
  inline def unsignedConversion(ids: OperandIds)(inline representable: Long => Long, inline kernel: Long => Long)
      : Closure = in => Seq(kernel(in.head), representable(in.head))
  inline def atomic(ids: OperandIds, anyStorage: Boolean)(inline kernel: (Long, Long) => Long): Closure =
    in => Seq(kernel(in(0), in(1)))
  inline def atomicUpdate(ids: OperandIds)(inline kernel: Long => Long): Closure = in => Seq(kernel(in.head))
  inline def compareExchange(ids: OperandIds)(inline kernel: (Long, Long, Long) => Long): Closure =
    in => Seq(kernel(in(0), in(1), in(2)))
  inline def addresses(ids: OperandIds)(inline kernel: (Long, Long) => Long): Closure = in => Seq(kernel(in(0), in(1)))
  inline def addressDifference(ids: OperandIds)(inline kernel: (Long, Long, Long) => Long): Closure =
    in => Seq(kernel(in(0), in(1), in(2)))
  inline def halfLoad(ids: OperandIds, aligned: Boolean)(inline convert: Long => Long): Closure =
    in => Seq(convert(in.head))
  inline def halfStore(ids: OperandIds, aligned: Boolean)(inline convert: Long => Long): Closure =
    in => Seq(convert(in.head))

}

package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

/** Integer dot products of SPV_KHR_integer_dot_product by opcode. Plain products wrap to the result width; accumulating
  * ones add the exact sum of products to the accumulator and saturate. The packed forms read four 8-bit lanes of each
  * 32-bit operand.
  */
object IntegerDotSemantics {

  inline def packed[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.SDot  => packedDot(algebra, sinks, width, leftSigned = true, rightSigned = true)(ids)
    case SPIRV.Opcode.UDot  => packedDot(algebra, sinks, width, leftSigned = false, rightSigned = false)(ids)
    case SPIRV.Opcode.SUDot => packedDot(algebra, sinks, width, leftSigned = true, rightSigned = false)(ids)
    case _                  => otherwise
  }

  private inline def packedDot[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in 1 ~ 64,
      inline leftSigned: Boolean,
      inline rightSigned: Boolean
  )(ids: OperandIds): R = sinks.binary(ids)((a, b) =>
    algebra.truncate(
      algebra.products(a, b, width = 8, lanes = 4, leftSigned = leftSigned, rightSigned = rightSigned),
      width
    )
  )

  inline def packedAccumulate[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.SDotAccSat =>
      packedAccumulation(algebra, sinks, width, leftSigned = true, rightSigned = true, signed = true)(ids)
    case SPIRV.Opcode.UDotAccSat =>
      packedAccumulation(algebra, sinks, width, leftSigned = false, rightSigned = false, signed = false)(ids)
    case SPIRV.Opcode.SUDotAccSat =>
      packedAccumulation(algebra, sinks, width, leftSigned = true, rightSigned = false, signed = true)(ids)
    case _ => otherwise
  }

  private inline def packedAccumulation[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in 1 ~ 64,
      inline leftSigned: Boolean,
      inline rightSigned: Boolean,
      inline signed: Boolean
  )(ids: OperandIds): R = sinks.ternary(ids)((a, b, c) =>
    accumulate(
      algebra,
      algebra.products(a, b, width = 8, lanes = 4, leftSigned = leftSigned, rightSigned = rightSigned),
      c,
      width,
      signed = signed
    )
  )

  /** The vector forms over `componentWidth`-bit lanes. */
  inline def vector[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      componentWidth: Int in 1 ~ 64,
      width: Int in 1 ~ 64
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.SDot =>
      vectorDot(algebra, sinks, componentWidth, width, first = true, second = true)(ids)
    case SPIRV.Opcode.UDot =>
      vectorDot(algebra, sinks, componentWidth, width, first = false, second = false)(ids)
    case SPIRV.Opcode.SUDot =>
      vectorDot(algebra, sinks, componentWidth, width, first = true, second = false)(ids)
    case _ => otherwise
  }

  private inline def vectorDot[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      componentWidth: Int in 1 ~ 64,
      width: Int in 1 ~ 64,
      inline first: Boolean,
      inline second: Boolean
  )(ids: OperandIds): R = sinks.binaryFold(ids)(
    algebra.literal(0L),
    step(algebra, _, _, _, componentWidth, first = first, second = second),
    algebra.truncate(_, width)
  )

  /** The accumulating vector forms, exact when every partial sum fits in 64 bits; see [[exact]]. */
  inline def vectorAccumulate[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      componentWidth: Int in 1 ~ 64,
      width: Int in 1 ~ 64
  )(ids: OperandIds)(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.SDotAccSat =>
      vectorAccumulation(algebra, sinks, componentWidth, width, first = true, second = true, signed = true)(ids)
    case SPIRV.Opcode.UDotAccSat =>
      vectorAccumulation(algebra, sinks, componentWidth, width, first = false, second = false, signed = false)(ids)
    case SPIRV.Opcode.SUDotAccSat =>
      vectorAccumulation(algebra, sinks, componentWidth, width, first = true, second = false, signed = true)(ids)
    case _ => otherwise
  }

  private inline def vectorAccumulation[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      componentWidth: Int in 1 ~ 64,
      width: Int in 1 ~ 64,
      inline first: Boolean,
      inline second: Boolean,
      inline signed: Boolean
  )(ids: OperandIds): R = sinks.accumulatedFold(ids)(
    algebra.literal(0L),
    step(algebra, _, _, _, componentWidth, first = first, second = second),
    accumulate(algebra, _, _, width, signed = signed)
  )

  /** Whether `lanes` products of `componentWidth`-bit lanes sum without leaving the signed 64-bit range. */
  def exact(componentWidth: Int in 1 ~ 64, lanes: Int): Boolean =
    2 * componentWidth + (32 - Integer.numberOfLeadingZeros(Math.max(lanes - 1, 0))) <= 63

  inline def step[V](
      inline algebra: Algebra[V],
      sum: V,
      a: V,
      b: V,
      componentWidth: Int in 1 ~ 64,
      inline first: Boolean,
      inline second: Boolean
  ): V = algebra.add(
    sum,
    algebra.multiply(
      algebra.extend(a, componentWidth, first),
      algebra.extend(b, componentWidth, second)
    )
  )

  /** The accumulator, read at `width` bits, plus an exact 64-bit sum, saturated to `width` bits. */
  inline def accumulate[V](
      inline algebra: Algebra[V],
      sum: V,
      accumulator: V,
      width: Int in 1 ~ 64,
      inline signed: Boolean
  )
      : V =
    if (signed)
      algebra.let("l", algebra.signExtend(accumulator, width))(l =>
        algebra.let("s", algebra.add(l, sum))(s =>
          algebra.truncate(
            algebra.saturated(algebra.and(algebra.xor(l, s), algebra.xor(sum, s)), l, s, width),
            width
          )
        )
      )
    else
      algebra.let("l", algebra.truncate(accumulator, width))(l =>
        algebra.let("s", algebra.add(l, sum))(s =>
          algebra.select(
            algebra.or(
              algebra.lessThan(s, l, signed = false),
              algebra.lessThan(algebra.literal(Bits.mask(width)), s, signed = false)
            ),
            algebra.literal(Bits.mask(width)),
            s
          )
        )
      )
}

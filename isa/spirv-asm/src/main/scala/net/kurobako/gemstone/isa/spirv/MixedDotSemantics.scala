package net.kurobako.gemstone.isa.spirv

import FloatingSemantics.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** SPV_VALVE_mixed_float_dot_product over binary16 or SPV_EXT_float8 lanes, whose precision the extension leaves to the
  * implementation: the products are exact in binary64 and sum there, and the accumulator joins the sum before one
  * rounding to the result width.
  */
object MixedDotSemantics {

  inline def dot[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], opcode: Int)(ids: OperandIds)(
      otherwise: => R
  ): R = opcode match {
    case SPIRV.Opcode.FDot2MixAcc32VALVE => pairDot(algebra, sinks, 32)(ids)
    case SPIRV.Opcode.FDot2MixAcc16VALVE => pairDot(algebra, sinks, 16)(ids)
    case SPIRV.Opcode.FDot4MixAcc32VALVE =>
      val left  = sinks.minifloat(sinks.typeOf(ids(0))).contains(Minifloat.E5M2)
      val right = sinks.minifloat(sinks.typeOf(ids(1))).contains(Minifloat.E5M2)
      sinks.accumulatedFold(ids)(
        algebra.literal(0L),
        algebra.step8(_, _, _, left, right),
        algebra.accumulate(_, _, 32)
      )
    case _ => otherwise
  }

  private inline def pairDot[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], inline bits: Int)(
      ids: OperandIds
  ): R = sinks.accumulatedFold(ids)(algebra.literal(0L), algebra.step(_, _, _), algebra.accumulate(_, _, bits))

  extension [V](inline algebra: Algebra[V]) {
    private inline def accumulate(sum: V, accumulator: V, inline bits: Int): V =
      algebra.rounded(algebra.primitive(algebra.widened(accumulator, bits), sum)(add(_: Float64, _: Float64)), bits)

    inline def step(sum: V, a: V, b: V): V =
      algebra.primitive(
        sum,
        algebra.primitive(algebra.widened(a, 16), algebra.widened(b, 16))(multiply(_: Float64, _: Float64))
      )(
        add(_: Float64, _: Float64)
      )

    inline def widen8(value: V, e5m2: Boolean): V =
      if (e5m2) algebra.primitive(value)(Elementary.widenE5M2(_)) else algebra.primitive(value)(Elementary.widenE4M3(_))

    inline def step8(sum: V, a: V, b: V, left: Boolean, right: Boolean): V =
      algebra.primitive(
        sum,
        algebra.primitive(algebra.widen8(a, left), algebra.widen8(b, right))(multiply(_: Float64, _: Float64))
      )(
        add(_: Float64, _: Float64)
      )
  }
}

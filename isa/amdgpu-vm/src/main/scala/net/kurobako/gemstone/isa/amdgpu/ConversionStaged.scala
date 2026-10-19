package net.kurobako.gemstone.isa.amdgpu

import FloatPrimitives.*
import FloatSemantics.*
import IntegerSemantics.*
import VMIR.*
import net.kurobako.gemstone.semantics.*

/** The staged semantics of conversions and the result placements that follow them. */
private object ConversionStaged {

  inline def staged[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: AMDGPUSinks[V, R])(
      otherwise: => R
  ): R = instruction match {
    case narrowed: Instruction.VConvertNarrow => sinks.section(FloatSemantics.narrow(algebra, narrowed)(sinks))
    case scaled: Instruction.VScaledConvert => sinks.section(ScaledConversionSemantics.convert(algebra, scaled)(sinks))
    case stochastic: Instruction.VConvertStochasticHalf =>
      sinks.section(ScaledConversionSemantics.stochasticHalf(algebra, stochastic)(sinks))
    case Instruction.VConvert(operation, dest, src, modifiers, clamp) =>
      sinks.section(convert(algebra, operation, clamp)(sinks, dest, src, modifiers))
    case modified: Instruction.VOutputModified =>
      sinks.section(
        sinks.outputModified(modified)(value =>
          algebra.outputModifier(modified.scale, modified.width, modified.clamp, value)
        )
      )
    case half: Instruction.VHalfResult =>
      sinks.section(
        sinks.halfResult(half)((existing, value) =>
          algebra.mergeHalf(existing, value, half.high)
        )
      )
    case placed: Instruction.VSubDwordResult =>
      sinks.section(
        sinks.placedResult(placed)((existing, value) =>
          algebra.placedPart(existing, value, placed.selection, placed.unused)
        )
      )
    case clamped: Instruction.VClampResult =>
      sinks.section(
        sinks.clampResult(clamped)(value => algebra.saturate(value, clamped.width))
      )
    case _ => otherwise
  }
}

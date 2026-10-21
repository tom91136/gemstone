package net.kurobako.gemstone.isa.amdgpu

import FloatPrimitives.*
import FloatSemantics.*
import VMIR.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** The staged semantics of vector floating-point and matrix instructions. */
private object VectorFloatStaged {

  inline def staged[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: AMDGPUSinks[V, R])(
      otherwise: => R
  ): R = instruction match {
    case Instruction.VDual(left, right) =>
      sinks.section(
        sinks.sequence(
          dual(algebra, left, right)(sinks, second = false),
          dual(algebra, right, left)(sinks, second = true)
        )
      )
    case packed: Instruction.VPackedF32 => sinks.section(packedSingle(algebra, packed)(sinks))
    case Instruction.VMadF32(operation, dest, srcA, srcB, srcC, modifiers, clamp) =>
      sinks.section(multiplyAdd(algebra, operation, clamp)(sinks, dest, srcA, srcB, srcC, modifiers))
    case Instruction.VMadF64(dest, srcA, srcB, srcC, modifiers) =>
      sinks.section(
        sinks.pairTernary(dest, srcA, srcB, srcC, modifiers)((a, b, c) =>
          algebra.flushedTernary(sinks.modeRegister, 64, a, b, c)(algebra.fusedMultiplyAdd(_, _, _, 64))
        )
      )
    case Instruction.VTrigPreopF64(dest, src, segment, modifiers) =>
      sinks.section(
        sinks.pairDword(dest, src, segment, PairRead.Floating, modifiers)((a, b) => algebra.trigPreop(a, b))
      )
    case Instruction.VDivScaleF32(dest, conditionDest, srcA, srcB, srcC, modifiers) =>
      sinks.section(
        sinks.scaledFlagged(dest, conditionDest, srcA, srcB, srcC, modifiers)(
          (a, b, c, _) => algebra.flushedScale(sinks.modeRegister, 32, a, b, c),
          (_, b, c, _, _) => algebra.divisionScaleFlag(32, b, c)
        )
      )
    case Instruction.VDivScaleF64(dest, conditionDest, srcA, srcB, srcC, modifiers) =>
      sinks.section(
        sinks.pairScaledFlagged(dest, conditionDest, srcA, srcB, srcC, modifiers)(
          (a, b, c, _) => algebra.flushedScale(sinks.modeRegister, 64, a, b, c),
          (_, b, c, _, _) => algebra.divisionScaleFlag(64, b, c)
        )
      )
    case Instruction.VDivFmasF64(dest, srcA, srcB, srcC, modifiers) =>
      sinks.section(
        sinks.pairScaled(dest, srcA, srcB, srcC, modifiers)((a, b, c, scaled) =>
          algebra.flushedTernary(sinks.modeRegister, 64, a, b, c)(algebra.divisionFusedMultiplyAdd(64, _, _, _, scaled))
        )
      )
    case Instruction.VDivFixupF32(dest, quotient, denominator, numerator, absolute, negate) =>
      sinks.section(
        sinks.vectorTernary(dest, quotient, denominator, numerator, SourceModifiers(absolute, negate), 32)((q, d, n) =>
          algebra.flushedFixup(sinks.modeRegister, 32, q, d, n)
        )
      )
    case Instruction.VDivFixupF64(dest, quotient, denominator, numerator, absolute, negate) =>
      sinks.section(
        sinks.pairTernary(dest, quotient, denominator, numerator, SourceModifiers(absolute, negate))((q, d, n) =>
          algebra.flushedFixup(sinks.modeRegister, 64, q, d, n)
        )
      )
    case Instruction.VDot2F32F16(dest, srcA, srcB, accumulate) =>
      sinks.section(
        sinks.vectorFloat(dest, srcA, srcB, SourceModifiers.Unmodified, 32)((a, b, d) =>
          algebra.primitive(a, b, if (accumulate) d else algebra.literal(0L))((x: UInt[32], y: UInt[32], z: Float32) =>
            Dots.halvesToFloat(x, y, z)
          )
        )
      )
    case Instruction.VDot(operation, dest, srcA, srcB, srcC, clamp, modifiers) =>
      sinks.section(dot(algebra, operation, clamp = clamp)(sinks, dest, srcA, srcB, srcC, modifiers))
    case packed: Instruction.VPackB32F16 =>
      sinks.section(
        sinks.halves(packed)((a, b) => algebra.packHalves(packed.clamp, a, b))
      )
    case packed: Instruction.VPackedTernary =>
      sinks.section(packedTernary(algebra, packed)(sinks))
    case mix: Instruction.VFmaMixF16 =>
      sinks.section(
        sinks.accumulating(mix.dest, mix.srcA, mix.srcB, mix.srcC)((a, b, c, d) =>
          algebra.mixedHalf(
            selection = mix.opSelection,
            selectionHigh = mix.opSelectionHi,
            negate = mix.negate,
            absolute = mix.absolute,
            clamp = mix.clamp,
            high = mix.high,
            fused = mix.fused,
            a = a,
            b = b,
            c = c,
            d = d
          )
        )
      )
    case mix: Instruction.VFmaMixF32 =>
      sinks.section(
        sinks.vectorTernary(mix.dest, mix.srcA, mix.srcB, mix.srcC, SourceModifiers.Unmodified, 32)((a, b, c) =>
          algebra.mixedSingle(
            selection = mix.opSelection,
            selectionHigh = mix.opSelectionHi,
            negate = mix.negate,
            absolute = mix.absolute,
            clamp = mix.clamp,
            fused = mix.fused,
            a = a,
            b = b,
            c = c
          )
        )
      )
    case matrix: Instruction.VMatrix => sinks.section(MatrixSemantics.multiply(algebra, matrix)(sinks))
    case _                           => otherwise
  }
}

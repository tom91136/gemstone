package net.kurobako.gemstone.isa.amdgpu

import FloatPrimitives.*
import FloatSemantics.*
import IntegerSemantics.*
import VMIR.*
import net.kurobako.gemstone.semantics.*

/** The staged semantics of vector integer and logic instructions. */
private object VectorStaged {

  inline def staged[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: AMDGPUSinks[V, R])(
      otherwise: => R
  ): R = instruction match {
    case Instruction.VBinary(operation, dest, srcA, srcB, clamp, modifiers) =>
      sinks.section(
        vectorFloat(algebra, operation, clamp)(sinks, dest, srcA, srcB, modifiers)(
          vectorBinary(algebra, operation, clamp)(sinks, dest, srcA, srcB)(
            vectorCarry(algebra, operation)(sinks, dest, srcA, srcB)
          )
        )
      )
    case Instruction.VMove(dest, src) => sinks.vectorUnary(RegisterBank.Vector, dest, src)(a => algebra.moved(a))
    case Instruction.VTernary(operation, dest, srcA, srcB, srcC, modifiers, clamp) =>
      sinks.section(
        vectorTernary(algebra, operation, clamp)(sinks, dest, srcA, srcB, srcC, modifiers)(
          sinks.scaled(dest, srcA, srcB, srcC, modifiers)((a, b, c, scaled) =>
            algebra.flushedTernary(
              sinks.modeRegister,
              32,
              a,
              b,
              c
            )(algebra.divisionFusedMultiplyAdd(32, _, _, _, scaled))
          )
        )
      )
    case Instruction.VCompare(comparison, dest, srcA, srcB, modifiers, selection) =>
      sinks.section(
        FloatSemantics.compare(algebra, comparison.kind, comparison.floatTest, comparison.floatSwapped)(
          sinks,
          scalar = false,
          dest,
          srcA,
          srcB,
          modifiers,
          selection,
          comparison.updateExec
        )(
          sinks.section(
            compareInteger(algebra, comparison.kind, comparison.integerTest, comparison.integerSwapped)(
              sinks,
              dest,
              srcA,
              srcB,
              selection,
              comparison.updateExec
            )(throw IllegalStateException(s"${comparison.kind} has no comparison"))
          )
        )
      )
    case Instruction.VMedia(operation, dest, srcA, srcB, srcC, modifiers) =>
      sinks.section(media(algebra, operation)(sinks, dest, srcA, srcB, srcC, modifiers))
    case Instruction.VQuadSad(dest, srcA, srcB, srcC, masked, false) =>
      sinks.section(
        sinks.pairShiftAdd(dest, srcA, srcB, srcC)((a, b, c) => algebra.quadSum(a, b, c, masked))
      )
    case Instruction.VQuadSad(dest, srcA, srcB, srcC, _, true) =>
      sinks.section(
        sinks.sequence(
          sinks.sequence(
            maskedWindow(algebra, 0)(sinks, dest, srcA, srcB, srcC),
            maskedWindow(algebra, 1)(sinks, dest, srcA, srcB, srcC)
          ),
          sinks.sequence(
            maskedWindow(algebra, 2)(sinks, dest, srcA, srcB, srcC),
            maskedWindow(algebra, 3)(sinks, dest, srcA, srcB, srcC)
          )
        )
      )
    case Instruction.VNot(dest, src) =>
      sinks.section(
        sinks.vectorUnary(RegisterBank.Vector, dest, src)(a => algebra.complement(a))
      )
    case Instruction.VBitReverse(dest, src) =>
      sinks.section(
        sinks.vectorUnary(RegisterBank.Vector, dest, src)(a => algebra.reversed(a))
      )
    case Instruction.VBitFind(operation, dest, src) =>
      sinks.section(vectorBitFind(algebra, operation)(sinks, dest, src))
    case Instruction.VBitOp3(dest, srcA, srcB, srcC, table, half) =>
      sinks.section(
        bitOperation3(algebra, half)(sinks, dest, srcA, srcB, srcC, table)
      )
    case Instruction.VCndMask(dest, srcA, srcB, condition, modifiers) =>
      sinks.section(
        sinks.select(dest, srcA, srcB, condition, modifiers)((a, b, c) => algebra.conditional(a, b, c))
      )
    case Instruction.VMaskBitCount(dest, mask, base, high) =>
      sinks.section(
        maskBitCount(algebra, high)(sinks, dest, mask, base)
      )
    case Instruction.VBitCount(dest, src, base) =>
      sinks.section(
        sinks.vectorBinary(dest, src, base)((a, b) => algebra.bitCount(a, b))
      )
    case Instruction.VDot4U32U8(dest, srcA, srcB, srcC) =>
      sinks.section(
        sinks.vectorTernary(dest, srcA, srcB, srcC, SourceModifiers.Unmodified, 32)((a, b, c) =>
          algebra.dotBytes(a, b, c)
        )
      )
    case Instruction.VBinary64(operation, dest, srcA, srcB, modifiers) =>
      sinks.section(binary64(algebra, operation)(sinks, dest, srcA, srcB, modifiers))
    case subDword: Instruction.VBinarySubDword =>
      sinks.section(vectorSubDword(algebra, subDword)(sinks)(vectorSubDwordFlagged(algebra, subDword)(sinks)))
    case packed: Instruction.VPackedBinary => sinks.section(vectorPacked(algebra, packed)(sinks))
    case Instruction.VAddCarry(dest, carryDest, srcA, srcB, carryIn, clamp) =>
      sinks.section(
        maskCarry(algebra, subtract = false, clamp)(sinks, dest, carryDest, srcA, srcB, carryIn)
      )
    case Instruction.VSubCarry(dest, carryDest, srcA, srcB, carryIn, clamp) =>
      sinks.section(
        maskCarry(algebra, subtract = true, clamp)(sinks, dest, carryDest, srcA, srcB, carryIn)
      )
    // the reversed subtraction takes its minuend from SRC1
    case Instruction.VSubReverseCarry(dest, carryDest, srcA, srcB, carryIn, clamp) =>
      sinks.section(
        maskCarry(algebra, subtract = true, clamp)(sinks, dest, carryDest, srcA = srcB, srcB = srcA, carryIn = carryIn)
      )
    case Instruction.VMadU64U32(dest, carryDest, srcA, srcB, srcC, clamp) =>
      sinks.section(
        multiplyAdd64(algebra, signed = false, clamp)(sinks, dest, carryDest, srcA, srcB, srcC)
      )
    case Instruction.VMadI64I32(dest, carryDest, srcA, srcB, srcC, clamp) =>
      sinks.section(
        multiplyAdd64(algebra, signed = true, clamp)(sinks, dest, carryDest, srcA, srcB, srcC)
      )
    case Instruction.VShiftLeftAdd64(dest, src, amount, addend) =>
      sinks.section(
        sinks.pairShiftAdd(dest, src, amount, addend)((value, count, sum) => algebra.shiftLeftAdd64(value, count, sum))
      )
    case Instruction.VShiftLeft64(dest, amount, src) =>
      sinks.section(
        sinks.pairDword(dest, src, amount, PairRead.Integer, SourceModifiers.Unmodified)((value, count) =>
          algebra.shiftLeft64(value, count)
        )
      )
    case Instruction.VShiftRight64(dest, amount, src) =>
      sinks.section(
        sinks.pairDword(dest, src, amount, PairRead.Integer, SourceModifiers.Unmodified)((value, count) =>
          algebra.shiftRight64(value, count)
        )
      )
    case Instruction.VShiftRightArithmetic64(dest, amount, src) =>
      sinks.section(
        sinks.pairDword(dest, src, amount, PairRead.Integer, SourceModifiers.Unmodified)((value, count) =>
          algebra.shiftRightArithmetic64(value, count)
        )
      )
    case Instruction.VPackedMove32(dest, srcA, srcB, highA, highB) =>
      sinks.section(
        sinks.pairBinary(dest, srcA, srcB, PairRead.Packed, SourceModifiers.Unmodified)((a, b) =>
          algebra.packedMove(a, b, highA, highB)
        )
      )
    case _ => otherwise
  }
}

package net.kurobako.gemstone.isa.amdgpu

import FloatPrimitives.*
import FloatSemantics.*
import IntegerSemantics.*
import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

/** The staged semantics of scalar ALU, EXEC, comparison and floating-point instructions. */
private object ScalarStaged {

  val M0: ScalarRegisterIndex = EncodingConstants.Fixed.M0

  inline def staged[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: AMDGPUSinks[V, R])(
      otherwise: => R
  ): R = instruction match {
    case Instruction.SBinary(operation, dest, srcA, srcB) =>
      sinks.section(scalarBinary(algebra, operation)(sinks, dest, srcA, srcB))
    case Instruction.SMove(dest, src)            => sinks.scalarMove(dest, src, pair = false)(a => algebra.moved(a))
    case Instruction.SMove64(dest, src)          => sinks.scalarMove(dest, src, pair = true)(a => algebra.moved64(a))
    case Instruction.SCmp(operation, srcA, srcB) =>
      sinks.section(IntegerSemantics.scalarCompare(algebra, operation)(sinks, srcA, srcB))
    case Instruction.SMinMax(operation, dest, srcA, srcB) =>
      sinks.section(scalarMinMax(algebra, operation)(sinks, dest, srcA, srcB))
    case Instruction.SBinary64(operation, dest, srcA, srcB) =>
      sinks.section(scalarBinary64(algebra, operation)(sinks, dest, srcA, srcB))
    case Instruction.SUnary(operation, dest, src) => sinks.section(scalarUnary(algebra, operation)(sinks, dest, src))
    case Instruction.SSaveExec(operation, dest, src, width) =>
      sinks.section(saveExec(algebra, operation)(sinks, dest, src, width))
    case Instruction.SCSelect(dest, srcA, srcB, width) =>
      sinks.section(
        if (width.raw == 64) sinks.scalarPair(dest, srcA, srcB)((a, b, scc) => algebra.scalarSelect(a, b, scc))
        else sinks.scalarDword(dest, srcA, srcB)((a, b, scc) => algebra.scalarSelect(a, b, scc))
      )
    case Instruction.SMultiplyImmediate(dest, value) =>
      sinks.section(
        multiplyImmediate(algebra)(sinks, dest, value.toLong)
      )
    case Instruction.SAddImmediate(dest, value) =>
      sinks.section(
        addImmediate(algebra)(sinks, dest, value.toLong)
      )
    case Instruction.SReciprocalF32(dest, src, modifiers, clamp) =>
      sinks.section(
        sinks.scalarModified(dest, src, modifiers)(a => algebra.reciprocal(clamp, a))
      )
    case Instruction.SFmaF32(dest, srcA, srcB, srcC) =>
      sinks.section(scalarFusedMultiplyAdd(algebra, sinks, dest, srcA, srcB, srcC, 32))
    case Instruction.SFloatCompare(comparison, srcA, srcB) =>
      sinks.section(
        FloatSemantics.compare(algebra, comparison.kind, comparison.floatTest, comparison.floatSwapped)(
          sinks,
          scalar = true,
          0,
          srcA,
          srcB,
          SourceModifiers.Unmodified,
          None,
          false
        )(throw IllegalStateException(s"${comparison.kind} is not a floating-point comparison"))
      )
    case Instruction.SWriteExec(true, dest, src, width) =>
      sinks.section(
        sinks.writeExec(dest, src, width)(
          (value, exec) => algebra.and(value, algebra.truncate(algebra.not(exec), 64)),
          (_, _, next) => algebra.notEqual(next, algebra.literal(0L))
        )
      )
    case Instruction.SWriteExec(false, dest, src, width) =>
      sinks.section(
        sinks.writeExec(dest, src, width)(
          (value, exec) => algebra.and(exec, algebra.truncate(algebra.not(value), 64)),
          (_, _, next) => algebra.notEqual(next, algebra.literal(0L))
        )
      )
    case Instruction.SFloat(operation, dest, srcA, srcB) =>
      sinks.section(scalarFloat(algebra, operation)(sinks, dest, srcA, srcB))
    case Instruction.SFloatUnary(operation, dest, src) =>
      sinks.section(scalarFloatUnary(algebra, operation)(sinks, dest, src))
    case Instruction.SFmaF16(dest, srcA, srcB, srcC) =>
      sinks.section(scalarFusedMultiplyAdd(algebra, sinks, dest, srcA, srcB, srcC, 16))
    case Instruction.SGprIndex(index, mode, _) =>
      sinks.section(
        sinks.scalarDword(M0, index.getOrElse(InstructionSource.Constant(0L)), InstructionSource.Scalar(M0))(
          (a, m0, _) => algebra.gprIndex(a, m0, index.nonEmpty, mode.getOrElse(-1))
        )
      )
    case _ => otherwise
  }

  private inline def scalarFusedMultiplyAdd[V, R](
      inline algebra: Algebra[V],
      inline sinks: AMDGPUSinks[V, R],
      dest: ScalarRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource,
      width: Int in (16, 32, 64)
  ): R = sinks.scalarTernary(dest, srcA, srcB, srcC)((a, b, c) =>
    algebra.flushedTernary(sinks.modeRegister, width, a, b, c)(algebra.fusedMultiplyAdd(_, _, _, width))
  )
}

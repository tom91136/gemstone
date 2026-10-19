package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.semantics.*

/** The staged semantics of floating-point arithmetic. */
private[isa] object NvidiaFloatStaged {

  inline def floating[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: NvidiaSinks[V, R])(
      inline next: Instruction => R
  ): R = instruction match {
    case Instruction.FloatBinary(operation, destination, left, right, width, control) =>
      FloatSemantics.binary(algebra, operation, width, control)(sinks, destination, left, right)
    case Instruction.FloatBinaryPacked(operation, destination, left, right, format, control) =>
      FloatSemantics.binaryPacked(algebra, operation, format, control)(sinks, destination, left, right)
    case Instruction.FloatUnary(operation, destination, source, width, control) =>
      FloatSemantics.unary(algebra, operation, width, control)(sinks, destination, source)
    case Instruction.FusedMultiplyAdd(destination, left, right, addend, width, control, rounding) =>
      FloatSemantics.fusedMultiplyAdd(algebra, width, control, rounding)(sinks, destination, left, right, addend)
    case Instruction.FusedMultiplyAddPacked(destination, left, right, addend, bf16, saturate) =>
      FloatSemantics.fusedMultiplyAddPacked(algebra, bf16, saturate)(sinks, destination, left, right, addend)
    case Instruction.FusedMultiplyAddBf16(destination, left, right, addend) =>
      FloatSemantics.fusedMultiplyAddBf16(algebra)(sinks, destination, left, right, addend)
    case _ => next(instruction)
  }
}

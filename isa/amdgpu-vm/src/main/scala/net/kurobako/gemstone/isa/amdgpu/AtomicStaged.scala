package net.kurobako.gemstone.isa.amdgpu

import FloatPrimitives.*
import IntegerSemantics.*
import VMIR.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.vm.*

/** The staged semantics of atomic read-modify-writes. */
private object AtomicStaged {

  /** An atomic read-modify-write, which execution dispatches with the other memory instructions. Floating-point
    * operations on dwords and register pairs are AMDGPU primitives.
    */
  inline def atomic[V, R](inline algebra: Algebra[V], instruction: Instruction.Atomic)(
      inline sinks: AMDGPUSinks[V, R]
  ): R = (instruction.operation, instruction.bytes.raw) match {
    case (AtomicOperation.FloatAdd, 4) => sinks.atomic(instruction)((old, operand) => algebra.addSingle(old, operand))
    case (AtomicOperation.FloatAdd, _) =>
      sinks.atomic(instruction)((old, operand) => algebra.primitive(old, operand)(Floats.add(_: Float64, _: Float64)))
    case (AtomicOperation.FloatMinimum, 4) =>
      sinks.atomic(instruction)((old, operand) => algebra.minimumNumber(old, operand, 32))
    case (AtomicOperation.FloatMinimum, _) =>
      sinks.atomic(instruction)((old, operand) => algebra.minimumNumber(old, operand, 64))
    case (AtomicOperation.FloatMaximum, 4) =>
      sinks.atomic(instruction)((old, operand) => algebra.maximumNumber(old, operand, 32))
    case (AtomicOperation.FloatMaximum, _) =>
      sinks.atomic(instruction)((old, operand) => algebra.maximumNumber(old, operand, 64))
    case (operation, _) =>
      operation.staged(algebra, instruction.bytes.toBitWidth)(sinks.atomic(instruction))
  }

  /** A DS source-2 update of the old value from the other. */
  inline def localSource2[V, R](inline algebra: Algebra[V], instruction: Instruction.LocalSource2)(
      inline sinks: AMDGPUSinks[V, R]
  ): R = (instruction.operation, instruction.reverse, instruction.bytes.raw) match {
    case (_, true, 4) =>
      sinks.localSource2(instruction)((old, other) => algebra.truncate(algebra.subtract(other, old), 32))
    case (_, true, _) => sinks.localSource2(instruction)((old, other) => algebra.subtract(other, old))
    case (AtomicOperation.FloatAdd, _, _) =>
      sinks.localSource2(instruction)((old, other) => algebra.addSingle(old, other))
    case (AtomicOperation.FloatMinimum, _, 4) =>
      sinks.localSource2(instruction)((old, other) => algebra.minimumNumber(old, other, 32))
    case (AtomicOperation.FloatMinimum, _, _) =>
      sinks.localSource2(instruction)((old, other) => algebra.minimumNumber(old, other, 64))
    case (AtomicOperation.FloatMaximum, _, 4) =>
      sinks.localSource2(instruction)((old, other) => algebra.maximumNumber(old, other, 32))
    case (AtomicOperation.FloatMaximum, _, _) =>
      sinks.localSource2(instruction)((old, other) => algebra.maximumNumber(old, other, 64))
    case (operation, _, _) =>
      operation.staged(algebra, instruction.bytes.toBitWidth)(sinks.localSource2(instruction))
  }

  inline def compareSwap[V, R](inline algebra: Algebra[V], instruction: Instruction.CompareSwap)(
      inline sinks: AMDGPUSinks[V, R]
  ): R = sinks.compareSwap(instruction)((previous, comparand, swap) =>
    algebra.compareSwap(previous, comparand, swap)
  )

  private inline def floatCompareSwap[V, R](
      inline algebra: Algebra[V],
      access: Instruction.CompareSwap,
      inline sinks: AMDGPUSinks[V, R],
      inline width: Int
  ): R = sinks.compareSwap(access)((old, a, b) => algebra.select(algebra.floatEqual(old, a, width), b, old))

  /** A read-modify-write on the old value and the comparand and data as operands A and B. */
  inline def extendedAtomic[V, R](inline algebra: Algebra[V], instruction: Instruction.AtomicExtended)(
      inline sinks: AMDGPUSinks[V, R]
  ): R = (instruction.operation, instruction.access.bytes.raw) match {
    case (ExtendedAtomicOperation.ReverseSubtract, 4) =>
      sinks.compareSwap(instruction.access)((old, a, _) => algebra.truncate(algebra.subtract(a, old), 32))
    case (ExtendedAtomicOperation.ReverseSubtract, _) =>
      sinks.compareSwap(instruction.access)((old, a, _) => algebra.subtract(a, old))
    case (ExtendedAtomicOperation.MaskOr, 4) =>
      sinks.compareSwap(instruction.access)((old, a, b) =>
        algebra.truncate(algebra.or(algebra.and(old, algebra.not(a)), b), 32)
      )
    case (ExtendedAtomicOperation.MaskOr, _) =>
      sinks.compareSwap(instruction.access)((old, a, b) => algebra.or(algebra.and(old, algebra.not(a)), b))
    case (ExtendedAtomicOperation.Wrap, _) =>
      sinks.compareSwap(instruction.access)((old, a, b) =>
        algebra.select(
          algebra.lessOrEqual(a, old, signed = false),
          algebra.subtract(old, a),
          algebra.truncate(algebra.add(old, b), 32)
        )
      )
    case (ExtendedAtomicOperation.FloatCompareSwap, 4) => floatCompareSwap(algebra, instruction.access, sinks, 32)
    case (ExtendedAtomicOperation.FloatCompareSwap, _) => floatCompareSwap(algebra, instruction.access, sinks, 64)
    case (ExtendedAtomicOperation.SubtractClamp, _)    =>
      sinks.compareSwap(instruction.access)((old, a, _) =>
        algebra.select(algebra.lessOrEqual(a, old, signed = false), algebra.subtract(old, a), algebra.literal(0L))
      )
    case (ExtendedAtomicOperation.ConditionalSubtract, _) =>
      sinks.compareSwap(instruction.access)((old, a, _) =>
        algebra.select(algebra.lessOrEqual(a, old, signed = false), algebra.subtract(old, a), old)
      )
    case (ExtendedAtomicOperation.ConditionalExchange, _) =>
      sinks.compareSwap(instruction.access)((old, a, _) =>
        algebra.or(
          algebra.conditionalDword(old, a, 0),
          algebra.shiftLeft(algebra.conditionalDword(old, a, 1), algebra.literal(32L))
        )
      )
  }

  inline def staged[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: AMDGPUSinks[V, R])(
      otherwise: => R
  ): R = instruction match {
    case update: Instruction.Atomic              => sinks.section(atomic(algebra, update)(sinks))
    case update: Instruction.LocalSource2        => sinks.section(localSource2(algebra, update)(sinks))
    case exchange: Instruction.CompareSwap       => sinks.section(compareSwap(algebra, exchange)(sinks))
    case extended: Instruction.AtomicExtended    => sinks.section(extendedAtomic(algebra, extended)(sinks))
    case Instruction.ExchangePair(first, second) =>
      sinks.section(
        sinks.sequence(sinks.atomic(first)((_, data) => data), sinks.atomic(second)((_, data) => data))
      )
    case update: Instruction.SAtomic =>
      sinks.section(update.operation.staged(algebra, update.bytes.toBitWidth)(sinks.scalarAtomic(update)))
    case exchange: Instruction.SCompareSwap =>
      sinks.section(
        sinks.scalarCompareSwap(exchange)((previous, comparand, swap) =>
          algebra.compareSwap(previous, comparand, swap)
        )
      )
    case _ => otherwise
  }
}

package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.vm.*

/** The value an atomic read-modify-write stores for the previous value and its operand, by opcode; the memory system
  * performs the access. Integer results wrap to the width.
  */
object AtomicSemantics {

  inline def binary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.AtomicExchange =>
      sinks.atomic(ids, anyStorage = false)((_, value) => algebra.truncate(value, width))
    case SPIRV.Opcode.AtomicIAdd =>
      sinks.atomic(ids, anyStorage = true)((old, value) => algebra.truncate(algebra.add(old, value), width))
    case SPIRV.Opcode.AtomicISub =>
      sinks.atomic(ids, anyStorage = true)((old, value) => algebra.truncate(algebra.subtract(old, value), width))
    case SPIRV.Opcode.AtomicSMin =>
      sinks.atomic(ids, anyStorage = true)((old, value) =>
        algebra.truncate(
          algebra.minimum(algebra.signExtend(old, width), algebra.signExtend(value, width), signed = true),
          width
        )
      )
    case SPIRV.Opcode.AtomicUMin =>
      sinks.atomic(ids, anyStorage = true)((old, value) =>
        algebra.minimum(algebra.truncate(old, width), algebra.truncate(value, width), signed = false)
      )
    case SPIRV.Opcode.AtomicSMax =>
      sinks.atomic(ids, anyStorage = true)((old, value) =>
        algebra.truncate(
          algebra.maximum(algebra.signExtend(old, width), algebra.signExtend(value, width), signed = true),
          width
        )
      )
    case SPIRV.Opcode.AtomicUMax =>
      sinks.atomic(ids, anyStorage = true)((old, value) =>
        algebra.maximum(algebra.truncate(old, width), algebra.truncate(value, width), signed = false)
      )
    case SPIRV.Opcode.AtomicAnd =>
      sinks.atomic(ids, anyStorage = true)((old, value) => algebra.truncate(algebra.and(old, value), width))
    case SPIRV.Opcode.AtomicOr =>
      sinks.atomic(ids, anyStorage = true)((old, value) => algebra.truncate(algebra.or(old, value), width))
    case SPIRV.Opcode.AtomicXor =>
      sinks.atomic(ids, anyStorage = true)((old, value) => algebra.truncate(algebra.xor(old, value), width))
    case _ => otherwise
  }

  inline def unary[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], opcode: Int, width: Int in 1 ~ 64)(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.AtomicIIncrement =>
      sinks.atomicUpdate(ids)(old => algebra.truncate(algebra.add(old, algebra.literal(1L)), width))
    case SPIRV.Opcode.AtomicIDecrement =>
      sinks.atomicUpdate(ids)(old => algebra.truncate(algebra.subtract(old, algebra.literal(1L)), width))
    case _ => otherwise
  }

  /** Floating-point read-modify-writes; minimum and maximum ignore a NaN operand. */
  inline def floating[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.AtomicFAddEXT =>
      floatingAt(algebra, sinks, width)(Float16.add(_, _), Float32.add(_, _), Float64.add(_, _))(ids)
    case SPIRV.Opcode.AtomicFMinEXT =>
      floatingAt(algebra, sinks, width)(Float16.minNum(_, _), Float32.minNum(_, _), Float64.minNum(_, _))(ids)
    case SPIRV.Opcode.AtomicFMaxEXT =>
      floatingAt(algebra, sinks, width)(Float16.maxNum(_, _), Float32.maxNum(_, _), Float64.maxNum(_, _))(ids)
    case _ => otherwise
  }

  inline def floatingAt[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      inline half: (Float16, Float16) => Float16,
      inline single: (Float32, Float32) => Float32,
      inline double: (Float64, Float64) => Float64
  )(ids: OperandIds): R = width.raw match {
    case 16 => sinks.atomic(ids, anyStorage = false)((old, value) => algebra.primitive(old, value)(half))
    case 32 => sinks.atomic(ids, anyStorage = false)((old, value) => algebra.primitive(old, value)(single))
    case _  => sinks.atomic(ids, anyStorage = false)((old, value) => algebra.primitive(old, value)(double))
  }

  /** OpAtomicCompareExchange over the previous value, the value and the comparator. */
  inline def compareExchange[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in 1 ~ 64)(
      ids: OperandIds
  ): R = sinks.compareExchange(ids)((old, value, comparator) =>
    AtomicOperation.exchanged(algebra)(width, old, comparator, algebra.truncate(value, width))
  )
}

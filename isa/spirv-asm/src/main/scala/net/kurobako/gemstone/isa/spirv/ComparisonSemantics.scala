package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

/** SPIR-V integer and Boolean comparisons by opcode, as 1 or 0. Booleans are any non-zero value; signed comparisons
  * read their operands at `width` bits, and the others compare raw values.
  */
object ComparisonSemantics {

  inline def binary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.LogicalEqual => sinks.binary(ids)((a, b) => algebra.equal(truth(algebra, a), truth(algebra, b)))
    case SPIRV.Opcode.LogicalNotEqual =>
      sinks.binary(ids)((a, b) => algebra.notEqual(truth(algebra, a), truth(algebra, b)))
    case SPIRV.Opcode.LogicalOr      => sinks.binary(ids)((a, b) => truth(algebra, algebra.or(a, b)))
    case SPIRV.Opcode.LogicalAnd     => sinks.binary(ids)((a, b) => algebra.and(truth(algebra, a), truth(algebra, b)))
    case SPIRV.Opcode.IEqual         => sinks.binary(ids)((a, b) => algebra.equal(a, b))
    case SPIRV.Opcode.INotEqual      => sinks.binary(ids)((a, b) => algebra.notEqual(a, b))
    case SPIRV.Opcode.ULessThan      => sinks.binary(ids)((a, b) => algebra.lessThan(a, b, signed = false))
    case SPIRV.Opcode.UGreaterThan   => sinks.binary(ids)((a, b) => algebra.lessThan(b, a, signed = false))
    case SPIRV.Opcode.ULessThanEqual => sinks.binary(ids)((a, b) => algebra.lessOrEqual(a, b, signed = false))
    case SPIRV.Opcode.UGreaterThanEqual => sinks.binary(ids)((a, b) => algebra.lessOrEqual(b, a, signed = false))
    case SPIRV.Opcode.SLessThan         =>
      sinks.binary(ids)((a, b) =>
        algebra.lessThan(algebra.signExtend(a, width), algebra.signExtend(b, width), signed = true)
      )
    case SPIRV.Opcode.SLessThanEqual =>
      sinks.binary(ids)((a, b) =>
        algebra.lessOrEqual(algebra.signExtend(a, width), algebra.signExtend(b, width), signed = true)
      )
    case SPIRV.Opcode.SGreaterThan =>
      sinks.binary(ids)((a, b) =>
        algebra.lessThan(algebra.signExtend(b, width), algebra.signExtend(a, width), signed = true)
      )
    case SPIRV.Opcode.SGreaterThanEqual =>
      sinks.binary(ids)((a, b) =>
        algebra.lessOrEqual(algebra.signExtend(b, width), algebra.signExtend(a, width), signed = true)
      )
    case _ => otherwise
  }

  inline def unary[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], opcode: Int)(ids: OperandIds)(
      otherwise: => R
  ): R = opcode match {
    case SPIRV.Opcode.LogicalNot => sinks.unary(ids)(a => algebra.equal(a, algebra.literal(0L)))
    case _                       => otherwise
  }

  /** OpSelect over condition, true value and false value. */
  inline def ternary[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], opcode: Int)(ids: OperandIds)(
      otherwise: => R
  ): R = opcode match {
    case SPIRV.Opcode.Select => sinks.ternary(ids)((condition, a, b) => algebra.select(condition, a, b))
    case _                   => otherwise
  }

  /** OpAny and OpAll over the lanes of a Boolean vector. */
  inline def reduction[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], opcode: Int)(ids: OperandIds)(
      otherwise: => R
  ): R = opcode match {
    case SPIRV.Opcode.Any =>
      sinks.fold(ids)(algebra.literal(0L), (any, a) => algebra.or(any, truth(algebra, a)), any => any)
    case SPIRV.Opcode.All =>
      sinks.fold(ids)(algebra.literal(1L), (all, a) => algebra.and(all, truth(algebra, a)), all => all)
    case _ => otherwise
  }

  inline def truth[V](inline algebra: Algebra[V], value: V): V = algebra.notEqual(value, algebra.literal(0L))
}

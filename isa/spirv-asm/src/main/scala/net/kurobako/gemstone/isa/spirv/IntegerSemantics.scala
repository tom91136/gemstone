package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

/** SPIR-V integer lane semantics by opcode, wrapping to the result width. Division by zero, which OpenCL leaves
  * unspecified without trapping, yields zero; a shift by the width or more shifts out every bit. Shift amounts are
  * masked to their own width when `amountMask` is not -1.
  */
object IntegerSemantics {

  inline def binary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64,
      amountMask: Long
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.IAdd       => sinks.binary(ids)((a, b) => algebra.truncate(algebra.add(a, b), width))
    case SPIRV.Opcode.ISub       => sinks.binary(ids)((a, b) => algebra.truncate(algebra.subtract(a, b), width))
    case SPIRV.Opcode.IMul       => sinks.binary(ids)((a, b) => algebra.truncate(algebra.multiply(a, b), width))
    case SPIRV.Opcode.BitwiseOr  => sinks.binary(ids)((a, b) => algebra.truncate(algebra.or(a, b), width))
    case SPIRV.Opcode.BitwiseXor => sinks.binary(ids)((a, b) => algebra.truncate(algebra.xor(a, b), width))
    case SPIRV.Opcode.BitwiseAnd => sinks.binary(ids)((a, b) => algebra.truncate(algebra.and(a, b), width))
    case SPIRV.Opcode.UDiv       =>
      sinks.binary(ids)((a, b) => algebra.quotient(a, b, width, signed = false, remainder = false))
    case SPIRV.Opcode.UMod =>
      sinks.binary(ids)((a, b) => algebra.quotient(a, b, width, signed = false, remainder = true))
    case SPIRV.Opcode.SDiv =>
      sinks.binary(ids)((a, b) => algebra.quotient(a, b, width, signed = true, remainder = false))
    case SPIRV.Opcode.SRem =>
      sinks.binary(ids)((a, b) => algebra.quotient(a, b, width, signed = true, remainder = true))
    case SPIRV.Opcode.SMod => sinks.binary(ids)((a, b) =>
        algebra.let("d", algebra.signExtend(b, width))(d =>
          algebra.select(
            algebra.equal(d, algebra.literal(0L)),
            algebra.literal(0L),
            algebra.let("r", algebra.remainder(algebra.signExtend(a, width), d, signed = true))(r =>
              algebra.truncate(
                algebra.select(
                  algebra.and(
                    algebra.notEqual(r, algebra.literal(0L)),
                    algebra.notEqual(
                      algebra.lessThan(r, algebra.literal(0L), signed = true),
                      algebra.lessThan(d, algebra.literal(0L), signed = true)
                    )
                  ),
                  algebra.add(r, d),
                  r
                ),
                width
              )
            )
          )
        )
      )
    case SPIRV.Opcode.ShiftLeftLogical =>
      sinks.binary(ids)((a, b) =>
        algebra.shiftedLeft(a, amount(algebra, b, amountMask), width, signed = false)
      )
    case SPIRV.Opcode.ShiftRightLogical =>
      sinks.binary(ids)((a, b) =>
        algebra.shiftedRightLogical(a, amount(algebra, b, amountMask), width, signed = false)
      )
    case SPIRV.Opcode.ShiftRightArithmetic => sinks.binary(ids)((a, b) =>
        algebra.let("n", amount(algebra, b, amountMask))(n =>
          algebra.truncate(
            algebra.shiftRightArithmetic(
              algebra.signExtend(a, width),
              algebra.select(
                algebra.lessThan(n, algebra.literal(width.toLong), signed = false),
                n,
                algebra.literal(63L)
              )
            ),
            width
          )
        )
      )
    case _ => otherwise
  }

  inline def unary[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], opcode: Int, width: Int in 1 ~ 64)(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.SNegate =>
      sinks.unary(ids)(a => algebra.truncate(algebra.subtract(algebra.literal(0L), a), width))
    case SPIRV.Opcode.Not => sinks.unary(ids)(a => algebra.truncate(algebra.not(a), width))
    // the count fits every result width, so `width` is the operand's
    case SPIRV.Opcode.BitCount   => sinks.unary(ids)(a => algebra.populationCount(algebra.truncate(a, width)))
    case SPIRV.Opcode.BitReverse => sinks.unary(ids)(a => algebra.reverseBits(a, width))
    case _                       => otherwise
  }

  /** Bit-field extraction over base, offset and count; a field outside the width extracts zero. */
  inline def bitFieldExtract[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.BitFieldUExtract => sinks.bitFieldExtract(ids)(
        fieldInRange(algebra, _, _, width),
        (base, offset, count) =>
          algebra.select(
            fieldInRange(algebra, offset, count, width),
            algebra.bitFieldExtract(algebra.truncate(base, width), offset, count, signed = false),
            algebra.literal(0L)
          )
      )
    case SPIRV.Opcode.BitFieldSExtract => sinks.bitFieldExtract(ids)(
        fieldInRange(algebra, _, _, width),
        (base, offset, count) =>
          algebra.select(
            fieldInRange(algebra, offset, count, width),
            algebra.truncate(
              algebra.bitFieldExtract(algebra.truncate(base, width), offset, count, signed = true),
              width
            ),
            algebra.literal(0L)
          )
      )
    case _ => otherwise
  }

  /** OpBitFieldInsert over base, inserted value, offset and count; a field outside the width leaves the base. */
  inline def bitFieldInsert[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in 1 ~ 64)(
      ids: OperandIds
  ): R = sinks.bitFieldInsert(ids)(
    fieldInRange(algebra, _, _, width),
    (base, insert, offset, count) =>
      algebra.let("t", algebra.truncate(base, width))(t =>
        algebra.select(
          fieldInRange(algebra, offset, count, width),
          algebra.insertField(t, insert, offset, count),
          t
        )
      )
  )

  /** 1 when `count` bits from `offset` lie within the width. */
  inline def fieldInRange[V](inline algebra: Algebra[V], offset: V, count: V, width: Int in 1 ~ 64): V = algebra.and(
    algebra.lessOrEqual(offset, algebra.literal(width.toLong), signed = false),
    algebra.lessOrEqual(count, algebra.subtract(algebra.literal(width.toLong), offset), signed = false)
  )

  /** Extended arithmetic as a low result and a high result: the carry or borrow, or the high half of the product. */
  inline def extended[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.IAddCarry => sinks.binaryStructure(ids)(
        (a, b) => algebra.truncate(algebra.add(a, b), width),
        (a, b) =>
          algebra.lessThan(algebra.truncate(algebra.add(a, b), width), algebra.truncate(a, width), signed = false)
      )
    case SPIRV.Opcode.ISubBorrow => sinks.binaryStructure(ids)(
        (a, b) => algebra.truncate(algebra.subtract(a, b), width),
        (a, b) => algebra.lessThan(algebra.truncate(a, width), algebra.truncate(b, width), signed = false)
      )
    case SPIRV.Opcode.UMulExtended => multiplyExtended(algebra, sinks, width, signed = false)(ids)
    case SPIRV.Opcode.SMulExtended => multiplyExtended(algebra, sinks, width, signed = true)(ids)
    case _                         => otherwise
  }

  private inline def multiplyExtended[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in 1 ~ 64,
      inline signed: Boolean
  )(ids: OperandIds): R = sinks.binaryStructure(ids)(
    (a, b) => algebra.truncate(algebra.multiply(a, b), width),
    (a, b) => algebra.truncate(algebra.multiplyHigh(a, b, width, signed = signed), width)
  )

  /** The smaller operand at `width` bits, extended to 64. */
  inline def minimum[V](inline algebra: Algebra[V], left: V, right: V, width: Int in 1 ~ 64, inline signed: Boolean)
      : V = algebra.minimum(
    algebra.extend(left, width, signed),
    algebra.extend(right, width, signed),
    signed
  )

  /** The larger operand at `width` bits, extended to 64. */
  inline def maximum[V](inline algebra: Algebra[V], left: V, right: V, width: Int in 1 ~ 64, inline signed: Boolean)
      : V = algebra.maximum(
    algebra.extend(left, width, signed),
    algebra.extend(right, width, signed),
    signed
  )

  inline def amount[V](inline algebra: Algebra[V], value: V, mask: Long): V =
    if (mask == -1L) value else algebra.and(value, algebra.literal(mask))
}

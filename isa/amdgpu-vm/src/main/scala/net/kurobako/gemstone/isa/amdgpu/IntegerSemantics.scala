package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import EncodingConstants.DPP.*
import FloatPrimitives.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** AMDGPU integer, bit, move, scalar and lane semantics over lane operands: `a` is SRC0 and `b` SRC1, so the reversed
  * shifts take their amount from `a`. Operations the semantics do not cover take `otherwise`.
  */
// enum-dispatch arms must stay lexical to inline into staged kernels
@SuppressWarnings(Array("baumkuchen:near-duplicate-code"))
private object IntegerSemantics {

  inline def vectorBinary[V, R](inline algebra: Algebra[V], operation: VectorBinaryOperation, clamp: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource
  )(otherwise: => R): R = operation match {
    case VectorBinaryOperation.AddU32 =>
      if (clamp) sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.unsignedSum(a, b, 32))
      else sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.truncate(algebra.add(a, b), 32))
    case VectorBinaryOperation.AddU16 =>
      if (clamp) sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.unsignedSum(a, b, 16))
      else sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.truncate(algebra.add(a, b), 16))
    case VectorBinaryOperation.SubtractU32 =>
      if (clamp) sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.unsignedDifference(a, b, 32))
      else sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.truncate(algebra.subtract(a, b), 32))
    case VectorBinaryOperation.SubtractU16 =>
      if (clamp) sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.unsignedDifference(a, b, 16))
      else sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.truncate(algebra.subtract(a, b), 16))
    case VectorBinaryOperation.SubtractReverseU16 =>
      if (clamp) sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.unsignedDifference(b, a, 16))
      else sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.truncate(algebra.subtract(b, a), 16))
    case VectorBinaryOperation.SubtractReverseU32 =>
      if (clamp) sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.unsignedDifference(b, a, 32))
      else sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.truncate(algebra.subtract(b, a), 32))
    case VectorBinaryOperation.AddI32 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.signedResult(algebra.add(algebra.signExtend(a, 32), algebra.signExtend(b, 32)), 32, clamp)
      )
    case VectorBinaryOperation.SubtractI32 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.signedResult(algebra.subtract(algebra.signExtend(a, 32), algebra.signExtend(b, 32)), 32, clamp)
      )
    case VectorBinaryOperation.AddI16 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.signedResult(algebra.add(algebra.signExtend(a, 16), algebra.signExtend(b, 16)), 16, clamp)
      )
    case VectorBinaryOperation.SubtractI16 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.signedResult(algebra.subtract(algebra.signExtend(a, 16), algebra.signExtend(b, 16)), 16, clamp)
      )
    case VectorBinaryOperation.MultiplyLowU32 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.truncate(algebra.multiply(a, b), 32))
    case VectorBinaryOperation.MultiplyLowU16 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.truncate(algebra.multiply(a, b), 16))
    case VectorBinaryOperation.MultiplyHighU32 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.multiplyHigh(a, b, 32, signed = false))
    case VectorBinaryOperation.MultiplyHighI32 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.truncate(algebra.multiplyHigh(a, b, 32, signed = true), 32)
      )
    case VectorBinaryOperation.MultiplyU24 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.truncate(algebra.multiply(algebra.low24(a), algebra.low24(b)), 32)
      )
    case VectorBinaryOperation.MultiplyHighU24 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.shiftRightLogical(algebra.multiply(algebra.low24(a), algebra.low24(b)), algebra.literal(32L))
      )
    case VectorBinaryOperation.MultiplyI24 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.multiplySigned24(a, b))
    case VectorBinaryOperation.MultiplyHighI24 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.truncate(
          algebra.shiftRightArithmetic(
            algebra.multiply(algebra.signExtend(a, 24), algebra.signExtend(b, 24)),
            algebra.literal(32L)
          ),
          32
        )
      )
    case VectorBinaryOperation.And32  => sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.and(a, b))
    case VectorBinaryOperation.Or32   => sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.or(a, b))
    case VectorBinaryOperation.Xor32  => sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.xor(a, b))
    case VectorBinaryOperation.Xnor32 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.truncate(algebra.not(algebra.xor(a, b)), 32))
    case VectorBinaryOperation.ShiftLeft32 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.truncate(algebra.shiftLeft(b, algebra.amount(a, 31L)), 32)
      )
    case VectorBinaryOperation.ShiftLeft16 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.shiftLeftHalf(a, b))
    case VectorBinaryOperation.ShiftRight32 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.shiftRightLogical(b, algebra.amount(a, 31L)))
    case VectorBinaryOperation.ShiftRight16 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.shiftRightLogical(algebra.truncate(b, 16), algebra.amount(a, 15L))
      )
    case VectorBinaryOperation.ShiftRightArithmeticI32 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.truncate(algebra.shiftRightArithmetic(algebra.signExtend(b, 32), algebra.amount(a, 31L)), 32)
      )
    case VectorBinaryOperation.ShiftRightArithmeticI16 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.shiftRightArithmeticHalf(a, b))
    case VectorBinaryOperation.MinimumU32 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.minimum(a, b, signed = false))
    case VectorBinaryOperation.MaximumU32 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) => algebra.maximum(a, b, signed = false))
    case VectorBinaryOperation.MinimumI32 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.extremum(a, b, 32, signed = true, maximum = false)
      )
    case VectorBinaryOperation.MaximumI32 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.extremum(a, b, 32, signed = true, maximum = true)
      )
    case VectorBinaryOperation.MinimumI16 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.extremum(a, b, 16, signed = true, maximum = false)
      )
    case VectorBinaryOperation.MaximumI16 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.extremum(a, b, 16, signed = true, maximum = true)
      )
    case VectorBinaryOperation.MinimumU16 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.extremum(a, b, 16, signed = false, maximum = false)
      )
    case VectorBinaryOperation.MaximumU16 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.extremum(a, b, 16, signed = false, maximum = true)
      )
    case _ => otherwise
  }

  /** VOP2 carry operations on SRC0 and SRC1 dwords and the lane's VCC bit, which they write back. */
  inline def vectorCarry[V, R](inline algebra: Algebra[V], operation: VectorBinaryOperation)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource
  ): R = operation match {
    case VectorBinaryOperation.AddCarryU32 =>
      sinks.vectorCarry(dest, srcA, srcB)(
        (x, y, c) => algebra.carrySum(x, y, c, chained = false, clamp = false),
        (x, y, c, _) => algebra.carryOut(x, y, c, chained = false)
      )
    case VectorBinaryOperation.AddWithCarryU32 =>
      sinks.vectorCarry(dest, srcA, srcB)(
        (x, y, c) => algebra.carrySum(x, y, c, chained = true, clamp = false),
        (x, y, c, _) => algebra.carryOut(x, y, c, chained = true)
      )
    case VectorBinaryOperation.SubtractCarryU32 =>
      sinks.vectorCarry(dest, srcA, srcB)(
        (x, y, c) => algebra.borrowDifference(x, y, c, chained = false, clamp = false),
        (x, y, c, _) => algebra.borrowOut(x, y, c, chained = false)
      )
    case VectorBinaryOperation.SubtractWithBorrowU32 =>
      sinks.vectorCarry(dest, srcA, srcB)(
        (x, y, c) => algebra.borrowDifference(x, y, c, chained = true, clamp = false),
        (x, y, c, _) => algebra.borrowOut(x, y, c, chained = true)
      )
    case other => throw IllegalArgumentException(s"$other is not a carry operation")
  }

  /** VOP3 carries into a scalar mask, with an optional carry-in mask; `clamp` saturates. */
  inline def maskCarry[V, R](inline algebra: Algebra[V], subtract: Boolean, clamp: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      carryDest: ScalarRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      carryIn: Option[InstructionSource]
  ): R =
    if (subtract)
      sinks.maskCarry(dest, carryDest, srcA, srcB, carryIn)(
        (x, y, c) => algebra.borrowDifference(x, y, c, chained = carryIn.nonEmpty, clamp = clamp),
        (x, y, c, _) => algebra.borrowOut(x, y, c, chained = carryIn.nonEmpty)
      )
    else
      sinks.maskCarry(dest, carryDest, srcA, srcB, carryIn)(
        (x, y, c) => algebra.carrySum(x, y, c, chained = carryIn.nonEmpty, clamp = clamp),
        (x, y, c, _) => algebra.carryOut(x, y, c, chained = carryIn.nonEmpty)
      )

  /** The sum of dwords and, when `chained`, the carry flag, saturated when `clamp`. */
  extension [V](inline algebra: Algebra[V]) {
    inline def carrySum(a: V, b: V, carry: V, chained: Boolean, clamp: Boolean): V =
      algebra.let("s", algebra.carried(algebra.add(a, b), carry, chained))(sum =>
        if (clamp) algebra.saturateUnsigned(sum, 32) else algebra.truncate(sum, 32)
      )

    /** The carry out of bit 31. */
    inline def carryOut(a: V, b: V, carry: V, chained: Boolean): V =
      algebra.lessThan(algebra.literal(0xffffffffL), algebra.carried(algebra.add(a, b), carry, chained), signed = true)

    /** The difference of dwords less, when `chained`, the borrow flag, saturated at zero when `clamp`. */
    inline def borrowDifference(a: V, b: V, borrow: V, chained: Boolean, clamp: Boolean)
        : V =
      algebra.let("d", algebra.borrowed(algebra.subtract(a, b), borrow, chained))(difference =>
        if (clamp) algebra.maximum(difference, algebra.literal(0L), signed = true)
        else algebra.truncate(difference, 32)
      )

    inline def borrowOut(a: V, b: V, borrow: V, chained: Boolean): V =
      algebra.lessThan(algebra.borrowed(algebra.subtract(a, b), borrow, chained), algebra.literal(0L), signed = true)

    inline def carried(sum: V, carry: V, chained: Boolean): V =
      if (chained) algebra.add(sum, carry) else sum

    inline def borrowed(difference: V, borrow: V, chained: Boolean): V =
      if (chained) algebra.subtract(difference, borrow) else difference
  }

  /** V_CMP on an integer kind, as 1 or 0: dwords read at the kind's width, or register pairs; other kinds take
    * `otherwise`.
    */
  inline def compareInteger[V, R](
      inline algebra: Algebra[V],
      kind: VectorComparisonKind,
      test: IntegerTest,
      swap: Boolean
  )(
      inline sinks: AMDGPUSinks[V, R],
      dest: ScalarRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      selection: Option[SubDwordSources],
      updateExec: Boolean
  )(otherwise: => R): R = kind match {
    case VectorComparisonKind.Signed16 =>
      relation(algebra, test, swap, signed = true, pair = false)(algebra.signExtend(_, 16))(
        sinks,
        dest,
        srcA,
        srcB,
        selection,
        updateExec
      )
    case VectorComparisonKind.Signed32 =>
      relation(algebra, test, swap, signed = true, pair = false)(algebra.signExtend(_, 32))(
        sinks,
        dest,
        srcA,
        srcB,
        selection,
        updateExec
      )
    case VectorComparisonKind.Unsigned16 =>
      relation(algebra, test, swap, signed = false, pair = false)(algebra.truncate(_, 16))(
        sinks,
        dest,
        srcA,
        srcB,
        selection,
        updateExec
      )
    case VectorComparisonKind.Unsigned32 =>
      relation(algebra, test, swap, signed = false, pair = false)(algebra.truncate(_, 32))(
        sinks,
        dest,
        srcA,
        srcB,
        selection,
        updateExec
      )
    case VectorComparisonKind.Signed64 =>
      relation(
        algebra,
        test,
        swap,
        signed = true,
        pair = true
      )(identity)(sinks, dest, srcA, srcB, selection, updateExec)
    case VectorComparisonKind.Unsigned64 =>
      relation(
        algebra,
        test,
        swap,
        signed = false,
        pair = true
      )(identity)(sinks, dest, srcA, srcB, selection, updateExec)
    case _ => otherwise
  }

  inline def relation[V, R](
      inline algebra: Algebra[V],
      test: IntegerTest,
      swap: Boolean,
      inline signed: Boolean,
      inline pair: Boolean
  )(inline read: V => V)(
      inline sinks: AMDGPUSinks[V, R],
      dest: ScalarRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      selection: Option[SubDwordSources],
      updateExec: Boolean
  ): R = test match {
    case IntegerTest.Less =>
      integerShape(sinks, pair, dest, srcA, srcB, selection, updateExec)((x, y) =>
        if (swap) algebra.lessThan(read(y), read(x), signed) else algebra.lessThan(read(x), read(y), signed)
      )
    case IntegerTest.LessOrEqual =>
      integerShape(sinks, pair, dest, srcA, srcB, selection, updateExec)((x, y) =>
        if (swap) algebra.lessOrEqual(read(y), read(x), signed) else algebra.lessOrEqual(read(x), read(y), signed)
      )
    case IntegerTest.Equal =>
      integerShape(sinks, pair, dest, srcA, srcB, selection, updateExec)((x, y) => algebra.equal(read(x), read(y)))
    case IntegerTest.NotEqual =>
      integerShape(sinks, pair, dest, srcA, srcB, selection, updateExec)((x, y) => algebra.notEqual(read(x), read(y)))
    case IntegerTest.Always =>
      integerShape(sinks, pair, dest, srcA, srcB, selection, updateExec)((_, _) => algebra.literal(1L))
    case IntegerTest.Never =>
      integerShape(sinks, pair, dest, srcA, srcB, selection, updateExec)((_, _) => algebra.literal(0L))
  }

  inline def integerShape[V, R](
      inline sinks: AMDGPUSinks[V, R],
      inline pair: Boolean,
      dest: ScalarRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      selection: Option[SubDwordSources],
      updateExec: Boolean
  )(inline kernel: (V, V) => V): R =
    inline if (pair)
      sinks.comparePair(dest, srcA, srcB, PairRead.Integer, SourceModifiers.Unmodified, updateExec)(kernel)
    else sinks.compare(dest, srcA, srcB, selection, updateExec)(kernel)

  /** VOP3 three-source operations. The lane loop stores each result's low 32 bits, as a VGPR holds. */
  inline def vectorTernary[V, R](inline algebra: Algebra[V], operation: VectorTernaryOperation, clamp: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource,
      modifiers: SourceModifiers
  )(otherwise: => R): R = operation match {
    case VectorTernaryOperation.Add3U32 => sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.truncate(algebra.add(algebra.add(a, b), c), 32)
      )
    case VectorTernaryOperation.ShiftRightPackI8 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.or(
          algebra.truncate(algebra.clampSigned(algebra.shiftedRight(a, c), 8), 8),
          algebra.shiftLeft(
            algebra.truncate(algebra.clampSigned(algebra.shiftedRight(b, c), 8), 8),
            algebra.literal(8L)
          )
        )
      )
    case VectorTernaryOperation.ShiftRightPackU8 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.or(
          algebra.clampUnsigned(algebra.shiftedRight(a, c), 8),
          algebra.shiftLeft(algebra.clampUnsigned(algebra.shiftedRight(b, c), 8), algebra.literal(8L))
        )
      )
    case VectorTernaryOperation.Or3B32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) => algebra.or(algebra.or(a, b), c))
    case VectorTernaryOperation.Xor3B32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) => algebra.xor(algebra.xor(a, b), c))
    case VectorTernaryOperation.XorAddU32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.truncate(algebra.add(algebra.xor(a, b), c), 32)
      )
    case VectorTernaryOperation.AndOr32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) => algebra.or(algebra.and(a, b), c))
    case VectorTernaryOperation.BitFieldInsert32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) => algebra.blend(a, b, c))
    case VectorTernaryOperation.AddShiftLeftU32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.truncate(algebra.shiftLeft(algebra.add(a, b), algebra.amount(c, 31L)), 32)
      )
    case VectorTernaryOperation.ShiftLeftAddU32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.truncate(algebra.add(algebra.shiftLeft(a, algebra.amount(b, 31L)), c), 32)
      )
    case VectorTernaryOperation.ShiftLeftOr32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.truncate(algebra.or(algebra.shiftLeft(a, algebra.amount(b, 31L)), c), 32)
      )
    case VectorTernaryOperation.AlignBit32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.truncate(
          algebra.shiftRightLogical(algebra.or(b, algebra.shiftLeft(a, algebra.literal(32L))), algebra.amount(c, 31L)),
          32
        )
      )
    case VectorTernaryOperation.BitFieldExtractU32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.bitFieldExtract(a, algebra.amount(b, 31L), algebra.amount(c, 31L), signed = false)
      )
    case VectorTernaryOperation.BitFieldExtractI32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.truncate(
          algebra.bitFieldExtract(
            algebra.signExtend(a, 32),
            algebra.amount(b, 31L),
            algebra.amount(c, 31L),
            signed = true
          ),
          32
        )
      )
    case VectorTernaryOperation.Maximum3U32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.three(a, b, c, 32, maximum = true, signed = false)
      )
    case VectorTernaryOperation.Minimum3U32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.three(a, b, c, 32, maximum = false, signed = false)
      )
    case VectorTernaryOperation.Maximum3I32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.three(a, b, c, 32, maximum = true, signed = true)
      )
    case VectorTernaryOperation.Minimum3I32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.three(a, b, c, 32, maximum = false, signed = true)
      )
    case VectorTernaryOperation.Maximum3U16 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.three(a, b, c, 16, maximum = true, signed = false)
      )
    case VectorTernaryOperation.Minimum3U16 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.three(a, b, c, 16, maximum = false, signed = false)
      )
    case VectorTernaryOperation.Maximum3I16 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.three(a, b, c, 16, maximum = true, signed = true)
      )
    case VectorTernaryOperation.Minimum3I16 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.three(a, b, c, 16, maximum = false, signed = true)
      )
    case VectorTernaryOperation.Median3U32 => sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.median(a, b, c, 32, signed = false)
      )
    case VectorTernaryOperation.Median3I32 => sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.median(a, b, c, 32, signed = true)
      )
    case VectorTernaryOperation.Median3U16 => sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.median(a, b, c, 16, signed = false)
      )
    case VectorTernaryOperation.Median3I16 => sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.median(a, b, c, 16, signed = true)
      )
    case _ =>
      sinks.section(operation match {
        case VectorTernaryOperation.Minimum3F32 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
            algebra.minimumNumber(algebra.minimumNumber(a, b, 32), c, 32)
          )
        case VectorTernaryOperation.Maximum3F32 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
            algebra.maximumNumber(algebra.maximumNumber(a, b, 32), c, 32)
          )
        case VectorTernaryOperation.Median3F32 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
            algebra.primitive(a, b, c, sinks.modeRegister)(Floats.med3(_: Float32, _: Float32, _: Float32, _: UInt[32]))
          )
        case VectorTernaryOperation.Minimum3F16 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 16)((a, b, c) =>
            algebra.minimumNumber(algebra.minimumNumber(a, b, 16), c, 16)
          )
        case VectorTernaryOperation.Maximum3F16 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 16)((a, b, c) =>
            algebra.maximumNumber(algebra.maximumNumber(a, b, 16), c, 16)
          )
        case VectorTernaryOperation.Median3F16 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 16)((a, b, c) =>
            algebra.primitive(a, b, c, sinks.modeRegister)(Floats.med3(_: Float16, _: Float16, _: Float16, _: UInt[32]))
          )
        case VectorTernaryOperation.MaximumMinimumF32 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
            algebra.minimumNumber(algebra.maximumNumber(a, b, 32), c, 32)
          )
        case VectorTernaryOperation.MinimumMaximumF32 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
            algebra.maximumNumber(algebra.minimumNumber(a, b, 32), c, 32)
          )
        case VectorTernaryOperation.MaximumMinimumF16 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 16)((a, b, c) =>
            algebra.minimumNumber(algebra.maximumNumber(a, b, 16), c, 16)
          )
        case VectorTernaryOperation.MinimumMaximumF16 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 16)((a, b, c) =>
            algebra.maximumNumber(algebra.minimumNumber(a, b, 16), c, 16)
          )
        case VectorTernaryOperation.Minimum3PropagateF32 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
            algebra.minimumPropagate(algebra.minimumPropagate(a, b, 32), c, 32)
          )
        case VectorTernaryOperation.Maximum3PropagateF32 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
            algebra.maximumPropagate(algebra.maximumPropagate(a, b, 32), c, 32)
          )
        case VectorTernaryOperation.Minimum3PropagateF16 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 16)((a, b, c) =>
            algebra.minimumPropagate(algebra.minimumPropagate(a, b, 16), c, 16)
          )
        case VectorTernaryOperation.Maximum3PropagateF16 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 16)((a, b, c) =>
            algebra.maximumPropagate(algebra.maximumPropagate(a, b, 16), c, 16)
          )
        case VectorTernaryOperation.MaximumMinimumPropagateF32 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
            algebra.minimumPropagate(algebra.maximumPropagate(a, b, 32), c, 32)
          )
        case VectorTernaryOperation.MinimumMaximumPropagateF32 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
            algebra.maximumPropagate(algebra.minimumPropagate(a, b, 32), c, 32)
          )
        case VectorTernaryOperation.MaximumMinimumPropagateF16 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 16)((a, b, c) =>
            algebra.minimumPropagate(algebra.maximumPropagate(a, b, 16), c, 16)
          )
        case VectorTernaryOperation.MinimumMaximumPropagateF16 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 16)((a, b, c) =>
            algebra.maximumPropagate(algebra.minimumPropagate(a, b, 16), c, 16)
          )
        case VectorTernaryOperation.MaximumMinimumI32 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
            algebra.truncate(
              algebra.minimum(
                algebra.maximum(algebra.signExtend(a, 32), algebra.signExtend(b, 32), signed = true),
                algebra.signExtend(c, 32),
                signed = true
              ),
              32
            )
          )
        case VectorTernaryOperation.MinimumMaximumI32 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
            algebra.truncate(
              algebra.maximum(
                algebra.minimum(algebra.signExtend(a, 32), algebra.signExtend(b, 32), signed = true),
                algebra.signExtend(c, 32),
                signed = true
              ),
              32
            )
          )
        case VectorTernaryOperation.MaximumMinimumU32 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
            algebra.minimum(algebra.maximum(a, b, signed = false), c, signed = false)
          )
        case VectorTernaryOperation.MinimumMaximumU32 =>
          sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
            algebra.maximum(algebra.minimum(a, b, signed = false), c, signed = false)
          )
        case _ =>
          sinks.section(operation match {
            case VectorTernaryOperation.AlignByte32 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
                algebra.truncate(
                  algebra.shiftRightLogical(
                    algebra.or(b, algebra.shiftLeft(a, algebra.literal(32L))),
                    algebra.shiftLeft(algebra.amount(c, 3L), algebra.literal(3L))
                  ),
                  32
                )
              )
            case VectorTernaryOperation.SumAbsoluteDifferenceU8 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
                algebra.truncate(algebra.add(algebra.absoluteDifferences(a, b, 8, masked = false), c), 32)
              )
            case VectorTernaryOperation.SumAbsoluteDifferenceHighU8 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
                algebra.truncate(
                  algebra.add(
                    algebra.shiftLeft(algebra.absoluteDifferences(a, b, 8, masked = false), algebra.literal(16L)),
                    c
                  ),
                  32
                )
              )
            case VectorTernaryOperation.SumAbsoluteDifferenceU16 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
                algebra.truncate(algebra.add(algebra.absoluteDifferences(a, b, 16, masked = false), c), 32)
              )
            case VectorTernaryOperation.SumAbsoluteDifferenceU32 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
                algebra.truncate(algebra.add(algebra.absoluteDifferences(a, b, 32, masked = false), c), 32)
              )
            // reference bytes of zero leave their elements out
            case VectorTernaryOperation.MaskedSumAbsoluteDifferenceU8 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
                algebra.truncate(algebra.add(algebra.absoluteDifferences(a, b, 8, masked = true), c), 32)
              )
            case VectorTernaryOperation.MultiplyAddU16 | VectorTernaryOperation.MultiplyAddLegacyU16 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
                algebra.let(
                  "s",
                  algebra.add(
                    algebra.multiply(algebra.truncate(a, 16), algebra.truncate(b, 16)),
                    algebra.truncate(c, 16)
                  )
                )(sum => if (clamp) algebra.saturateUnsigned(sum, 16) else algebra.truncate(sum, 16))
              )
            case VectorTernaryOperation.MultiplyAddU24 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
                algebra.let(
                  "s",
                  algebra.add(algebra.multiply(algebra.low24(a), algebra.low24(b)), algebra.truncate(c, 32))
                )(sum => if (clamp) algebra.saturateUnsigned(sum, 32) else algebra.truncate(sum, 32))
              )
            case VectorTernaryOperation.MultiplyAddI24 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
                algebra.let(
                  "s",
                  algebra.add(
                    algebra.multiply(algebra.signExtend(a, 24), algebra.signExtend(b, 24)),
                    algebra.signExtend(c, 32)
                  )
                )(sum => algebra.truncate(if (clamp) algebra.clampSigned(sum, 32) else sum, 32))
              )
            case VectorTernaryOperation.FusedMultiplyAddF16 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 16)((a, b, c) =>
                algebra.flushedTernary(sinks.modeRegister, 16, a, b, c)(algebra.fusedMultiplyAdd(_, _, _, 16))
              )
            case VectorTernaryOperation.MultiplyAddU32U16 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
                algebra.let("s", algebra.add(algebra.multiply(algebra.truncate(a, 16), algebra.truncate(b, 16)), c))(
                  sum =>
                    if (clamp) algebra.saturateUnsigned(sum, 32) else algebra.truncate(sum, 32)
                )
              )
            case VectorTernaryOperation.MultiplyAddI32I16 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
                algebra.signedResult(
                  algebra.add(
                    algebra.multiply(algebra.signExtend(a, 16), algebra.signExtend(b, 16)),
                    algebra.signExtend(c, 32)
                  ),
                  32,
                  clamp
                )
              )
            case VectorTernaryOperation.MultiplyAddI16 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
                algebra.signedResult(algebra.multiplyAddSigned16(a, b, c), 16, clamp)
              )
            case VectorTernaryOperation.MultiplyAddF16 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 16)((a, b, c) =>
                algebra.saturated(
                  clamp,
                  algebra.flushedTernary(sinks.modeRegister, 16, a, b, c)((x, y, z) =>
                    algebra.halfOperation(clamp = false, algebra.halfOperation(clamp = false, x, y)(Floats.mul), z)(
                      Floats.add
                    )
                  ),
                  16
                )
              )
            case VectorTernaryOperation.DivisionFixupF16 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 16)((q, d, n) =>
                algebra.flushedFixup(sinks.modeRegister, 16, q, d, n)
              )
            case VectorTernaryOperation.PermuteBytes32 =>
              sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) => algebra.permuteBytes(a, b, c))
            case _ => otherwise
          })
      })
  }

  /** V_PERM_B32: each selector byte picks a byte of `first:second` (0-7), copies the sign of byte 1 or 3 of `second` or
    * `first` into every bit (8-11), or gives 0x00 (12) or 0xff (13 and above).
    */
  extension [V](inline algebra: Algebra[V]) {
    inline def permuteBytes(first: V, second: V, selectors: V): V =
      algebra.let("x", algebra.or(algebra.truncate(second, 32), algebra.shiftLeft(first, algebra.literal(32L))))(
        sources =>
          algebra.or(
            algebra.or(algebra.selectByte(sources, selectors, 0), algebra.selectByte(sources, selectors, 1)),
            algebra.or(algebra.selectByte(sources, selectors, 2), algebra.selectByte(sources, selectors, 3))
          )
      )

    inline def selectByte(sources: V, selectors: V, inline index: Int): V =
      algebra.let(
        s"s$index",
        algebra.and(algebra.shiftRightLogical(selectors, algebra.literal(index * 8L)), algebra.literal(0xffL))
      )(selector =>
        algebra.shiftLeft(
          algebra.select(
            algebra.lessThan(selector, algebra.literal(8L), signed = false),
            algebra.byte(algebra.shiftRightLogical(sources, algebra.shiftLeft(selector, algebra.literal(3L))), 0),
            algebra.select(
              algebra.lessThan(selector, algebra.literal(12L), signed = false),
              algebra.multiply(
                algebra.and(
                  algebra.shiftRightLogical(
                    sources,
                    algebra.subtract(algebra.shiftLeft(selector, algebra.literal(4L)), algebra.literal(113L))
                  ),
                  algebra.literal(1L)
                ),
                algebra.literal(0xffL)
              ),
              algebra.select(algebra.equal(selector, algebra.literal(12L)), algebra.literal(0L), algebra.literal(0xffL))
            )
          ),
          algebra.literal(index * 8L)
        )
      )

    inline def byte(value: V, inline index: Int): V =
      algebra.field(value, index * 8, 8, signed = false)
  }

  /** SDWA operations over operands as their source selections deliver them, which may be sign-extended; the lane loop
    * places the result into the destination selection.
    */
  inline def vectorSubDword[V, R](inline algebra: Algebra[V], instruction: Instruction.VBinarySubDword)(
      inline sinks: AMDGPUSinks[V, R]
  )(otherwise: => R): R = instruction.operation match {
    case VectorSubDwordOperation.AddU32 =>
      sinks.subDword(instruction)((a, b) => algebra.truncate(algebra.add(a, b), 32))
    case VectorSubDwordOperation.AddU16 =>
      sinks.subDword(instruction)((a, b) => algebra.truncate(algebra.add(a, b), 16))
    case VectorSubDwordOperation.SubtractU32 =>
      sinks.subDword(instruction)((a, b) => algebra.truncate(algebra.subtract(a, b), 32))
    case VectorSubDwordOperation.SubtractU16 =>
      sinks.subDword(instruction)((a, b) => algebra.truncate(algebra.subtract(a, b), 16))
    case VectorSubDwordOperation.SubtractReverseU32 =>
      sinks.subDword(instruction)((a, b) => algebra.truncate(algebra.subtract(b, a), 32))
    case VectorSubDwordOperation.SubtractReverseU16 =>
      sinks.subDword(instruction)((a, b) => algebra.truncate(algebra.subtract(b, a), 16))
    case VectorSubDwordOperation.MultiplyLowU16 =>
      sinks.subDword(instruction)((a, b) => algebra.truncate(algebra.multiply(a, b), 16))
    case VectorSubDwordOperation.MultiplyU24 =>
      sinks.subDword(instruction)((a, b) =>
        algebra.truncate(algebra.multiply(algebra.low24(a), algebra.low24(b)), 32)
      )
    case VectorSubDwordOperation.MultiplyI24 =>
      sinks.subDword(instruction)((a, b) => algebra.multiplySigned24(a, b))
    case VectorSubDwordOperation.And32       => sinks.subDword(instruction)((a, b) => algebra.and(a, b))
    case VectorSubDwordOperation.Or32        => sinks.subDword(instruction)((a, b) => algebra.or(a, b))
    case VectorSubDwordOperation.Xor32       => sinks.subDword(instruction)((a, b) => algebra.xor(a, b))
    case VectorSubDwordOperation.ShiftLeft32 =>
      sinks.subDword(instruction)((a, b) => algebra.truncate(algebra.shiftLeft(b, algebra.amount(a, 31L)), 32))
    case VectorSubDwordOperation.ShiftLeft16 =>
      sinks.subDword(instruction)((a, b) => algebra.shiftLeftHalf(a, b))
    case VectorSubDwordOperation.ShiftRight32 =>
      sinks.subDword(instruction)((a, b) => algebra.shiftRightLogical(b, algebra.amount(a, 31L)))
    case VectorSubDwordOperation.ShiftRight16 =>
      sinks.subDword(instruction)((a, b) => algebra.shiftRightLogical(algebra.truncate(b, 16), algebra.amount(a, 15L)))
    case VectorSubDwordOperation.MaximumI16 =>
      sinks.subDword(instruction)((a, b) => algebra.extremum(a, b, 16, signed = true, maximum = true))
    case VectorSubDwordOperation.MinimumI16 =>
      sinks.subDword(instruction)((a, b) => algebra.extremum(a, b, 16, signed = true, maximum = false))
    case VectorSubDwordOperation.MinimumU16 =>
      sinks.subDword(instruction)((a, b) => algebra.extremum(a, b, 16, signed = false, maximum = false))
    case VectorSubDwordOperation.MaximumF16 => sinks.subDword(instruction)((a, b) => algebra.maximumNumber(a, b, 16))
    case VectorSubDwordOperation.MinimumF16 => sinks.subDword(instruction)((a, b) => algebra.minimumNumber(a, b, 16))
    case VectorSubDwordOperation.AddF16     =>
      sinks.subDword(instruction)((a, b) => algebra.flushedHalf(sinks.modeRegister, clamp = false, a, b)(Floats.add))
    case VectorSubDwordOperation.MultiplyF16 =>
      sinks.subDword(instruction)((a, b) => algebra.flushedHalf(sinks.modeRegister, clamp = false, a, b)(Floats.mul))
    case _ => otherwise
  }

  /** SDWA operations that read the lane's VCC bit as the flag; the carry forms write it back. */
  inline def vectorSubDwordFlagged[V, R](inline algebra: Algebra[V], instruction: Instruction.VBinarySubDword)(
      inline sinks: AMDGPUSinks[V, R]
  ): R = instruction.operation match {
    case VectorSubDwordOperation.ConditionalMask32 =>
      sinks.subDwordConditional(instruction)((a, b, condition) => algebra.conditional(a, b, condition))
    case VectorSubDwordOperation.AddCarryU32 =>
      sinks.subDwordFlagged(instruction)(
        (a, b, c) => algebra.carrySum(a, b, c, chained = false, clamp = false),
        (a, b, c, _) => algebra.carryOut(a, b, c, chained = false)
      )
    case VectorSubDwordOperation.AddWithCarryU32 =>
      sinks.subDwordFlagged(instruction)(
        (a, b, c) => algebra.carrySum(a, b, c, chained = true, clamp = false),
        (a, b, c, _) => algebra.carryOut(a, b, c, chained = true)
      )
    case VectorSubDwordOperation.SubtractCarryU32 =>
      sinks.subDwordFlagged(instruction)(
        (a, b, c) => algebra.borrowDifference(a, b, c, chained = false, clamp = false),
        (a, b, c, _) => algebra.borrowOut(a, b, c, chained = false)
      )
    case VectorSubDwordOperation.SubtractWithBorrowU32 =>
      sinks.subDwordFlagged(instruction)(
        (a, b, c) => algebra.borrowDifference(a, b, c, chained = true, clamp = false),
        (a, b, c, _) => algebra.borrowOut(a, b, c, chained = true)
      )
    case VectorSubDwordOperation.SubtractReverseCarryU32 =>
      sinks.subDwordFlagged(instruction)(
        (a, b, c) => algebra.borrowDifference(b, a, c, chained = false, clamp = false),
        (a, b, c, _) => algebra.borrowOut(b, a, c, chained = false)
      )
    case VectorSubDwordOperation.SubtractReverseWithBorrowU32 =>
      sinks.subDwordFlagged(instruction)(
        (a, b, c) => algebra.borrowDifference(b, a, c, chained = true, clamp = false),
        (a, b, c, _) => algebra.borrowOut(b, a, c, chained = true)
      )
    case other => throw IllegalArgumentException(s"$other reads no flag")
  }

  /** Packed 16-bit operations on one pair of halves; the lane loop applies them to the low and the high halves. */
  inline def vectorPacked[V, R](inline algebra: Algebra[V], instruction: Instruction.VPackedBinary)(
      inline sinks: AMDGPUSinks[V, R]
  ): R = {
    val clamp = instruction.clamp
    instruction.operation match {
      case VectorPackedBinaryOperation.AddU16 =>
        sinks.packedBinary(instruction)((a, b) => algebra.packedInteger(algebra.add(a, b), clamp, signed = false))
      case VectorPackedBinaryOperation.AddI16 =>
        sinks.packedBinary(instruction)((a, b) =>
          algebra.packedInteger(
            algebra.add(algebra.signExtend(a, 16), algebra.signExtend(b, 16)),
            clamp,
            signed = true
          )
        )
      case VectorPackedBinaryOperation.SubtractU16 =>
        sinks.packedBinary(instruction)((a, b) => algebra.packedInteger(algebra.subtract(a, b), clamp, signed = false))
      case VectorPackedBinaryOperation.SubtractI16 =>
        sinks.packedBinary(instruction)((a, b) =>
          algebra.packedInteger(
            algebra.subtract(algebra.signExtend(a, 16), algebra.signExtend(b, 16)),
            clamp,
            signed = true
          )
        )
      case VectorPackedBinaryOperation.MultiplyLowU16 =>
        sinks.packedBinary(instruction)((a, b) =>
          algebra.packedInteger(algebra.multiply(a, b), clamp, signed = false)
        )
      case VectorPackedBinaryOperation.ShiftLeft16 =>
        sinks.packedBinary(instruction)((a, b) => algebra.truncate(algebra.shiftLeft(b, algebra.amount(a, 15L)), 16))
      case VectorPackedBinaryOperation.ShiftRight16 =>
        sinks.packedBinary(instruction)((a, b) =>
          algebra.truncate(algebra.shiftRightLogical(b, algebra.amount(a, 15L)), 16)
        )
      case VectorPackedBinaryOperation.ShiftRightArithmetic16 =>
        sinks.packedBinary(instruction)((a, b) => algebra.shiftRightArithmeticHalf(a, b))
      case VectorPackedBinaryOperation.MaximumU16 =>
        sinks.packedBinary(instruction)((a, b) => algebra.maximum(a, b, signed = false))
      case VectorPackedBinaryOperation.MinimumU16 =>
        sinks.packedBinary(instruction)((a, b) => algebra.minimum(a, b, signed = false))
      case VectorPackedBinaryOperation.MaximumI16 =>
        sinks.packedBinary(instruction)((a, b) => algebra.extremum(a, b, 16, signed = true, maximum = true))
      case VectorPackedBinaryOperation.MinimumI16 =>
        sinks.packedBinary(instruction)((a, b) => algebra.extremum(a, b, 16, signed = true, maximum = false))
      case VectorPackedBinaryOperation.MaximumF16 =>
        sinks.packedBinary(instruction)((a, b) => algebra.saturated(clamp, algebra.maximumNumber(a, b, 16), 16))
      case VectorPackedBinaryOperation.MinimumF16 =>
        sinks.packedBinary(instruction)((a, b) => algebra.saturated(clamp, algebra.minimumNumber(a, b, 16), 16))
      case VectorPackedBinaryOperation.MaximumPropagateF16 =>
        sinks.packedBinary(instruction)((a, b) => algebra.saturated(clamp, algebra.maximumPropagate(a, b, 16), 16))
      case VectorPackedBinaryOperation.MinimumPropagateF16 =>
        sinks.packedBinary(instruction)((a, b) => algebra.saturated(clamp, algebra.minimumPropagate(a, b, 16), 16))
      case VectorPackedBinaryOperation.AddF16 =>
        sinks.packedBinary(instruction)((a, b) => algebra.flushedHalf(sinks.modeRegister, clamp, a, b)(Floats.add))
      case VectorPackedBinaryOperation.MultiplyF16 =>
        sinks.packedBinary(instruction)((a, b) => algebra.flushedHalf(sinks.modeRegister, clamp, a, b)(Floats.mul))
    }
  }

  /** A packed half's integer result as 16 bits, saturated to the signed or unsigned range when `clamp`. */
  extension [V](inline algebra: Algebra[V]) {
    inline def packedInteger(value: V, clamp: Boolean, inline signed: Boolean): V =
      if (!clamp) algebra.truncate(value, 16)
      else if (signed) algebra.truncate(algebra.clampSigned(value, 16), 16)
      else algebra.saturateUnsigned(algebra.maximum(value, algebra.literal(0L), signed = true), 16)
  }

  /** SOP2 operations on SRC0 and SRC1 dwords and SCC as 1 or 0; results write as dwords. */
  inline def scalarBinary[V, R](inline algebra: Algebra[V], operation: ScalarBinaryOperation)(
      inline sinks: AMDGPUSinks[V, R],
      dest: ScalarRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource
  ): R = operation match {
    case ScalarBinaryOperation.And32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)((a, b, _) => algebra.and(a, b), algebra.nonZero)
    case ScalarBinaryOperation.Or32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)((a, b, _) => algebra.or(a, b), algebra.nonZero)
    case ScalarBinaryOperation.Xor32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)((a, b, _) => algebra.xor(a, b), algebra.nonZero)
    case ScalarBinaryOperation.AndNot32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.and(a, algebra.truncate(algebra.not(b), 32)),
        algebra.nonZero
      )
    case ScalarBinaryOperation.OrNot32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.truncate(algebra.or(a, algebra.not(b)), 32),
        algebra.nonZero
      )
    case ScalarBinaryOperation.Nor32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.truncate(algebra.not(algebra.or(a, b)), 32),
        algebra.nonZero
      )
    case ScalarBinaryOperation.AddI32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.truncate(algebra.add(a, b), 32),
        (a, b, _, sum) => algebra.signBit(algebra.and(algebra.not(algebra.xor(a, b)), algebra.xor(a, sum)))
      )
    case ScalarBinaryOperation.SubtractI32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.truncate(algebra.subtract(a, b), 32),
        (a, b, _, difference) => algebra.signBit(algebra.and(algebra.xor(a, b), algebra.xor(a, difference)))
      )
    case ScalarBinaryOperation.AddU32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.truncate(algebra.add(a, b), 32),
        (a, b, _, _) => algebra.lessThan(algebra.literal(0xffffffffL), algebra.add(a, b), signed = true)
      )
    case ScalarBinaryOperation.AddCarryU32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, carry) => algebra.truncate(algebra.add(algebra.add(a, b), carry), 32),
        (a, b, carry, _) =>
          algebra.lessThan(algebra.literal(0xffffffffL), algebra.add(algebra.add(a, b), carry), signed = true)
      )
    case ScalarBinaryOperation.SubtractU32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.truncate(algebra.subtract(a, b), 32),
        (a, b, _, _) => algebra.lessOrEqual(b, a, signed = true)
      )
    case ScalarBinaryOperation.SubtractBorrowU32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, carry) =>
          algebra.truncate(algebra.subtract(algebra.subtract(a, b), algebra.subtract(algebra.literal(1L), carry)), 32),
        (a, b, carry, _) =>
          algebra.lessOrEqual(algebra.add(b, algebra.subtract(algebra.literal(1L), carry)), a, signed = true)
      )
    case ScalarBinaryOperation.MultiplyI32 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.truncate(algebra.multiply(a, b), 32))
    case ScalarBinaryOperation.MultiplyHighU32 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.multiplyHigh(a, b, 32, signed = false))
    case ScalarBinaryOperation.MultiplyHighI32 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) =>
        algebra.truncate(algebra.multiplyHigh(a, b, 32, signed = true), 32)
      )
    case ScalarBinaryOperation.ShiftLeft32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.truncate(algebra.shiftLeft(a, algebra.amount(b, 31L)), 32),
        algebra.nonZero
      )
    case ScalarBinaryOperation.ShiftRight32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.shiftRightLogical(a, algebra.amount(b, 31L)),
        algebra.nonZero
      )
    case ScalarBinaryOperation.ShiftRightArithmetic32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) =>
          algebra.truncate(algebra.shiftRightArithmetic(algebra.signExtend(a, 32), algebra.amount(b, 31L)), 32),
        algebra.nonZero
      )
    case ScalarBinaryOperation.ShiftLeftOneAndAddU32   => shiftLeftAdd(algebra, 1)(sinks, dest, srcA, srcB)
    case ScalarBinaryOperation.ShiftLeftTwoAndAddU32   => shiftLeftAdd(algebra, 2)(sinks, dest, srcA, srcB)
    case ScalarBinaryOperation.ShiftLeftThreeAndAddU32 => shiftLeftAdd(algebra, 3)(sinks, dest, srcA, srcB)
    case ScalarBinaryOperation.ShiftLeftFourAndAddU32  => shiftLeftAdd(algebra, 4)(sinks, dest, srcA, srcB)
    case ScalarBinaryOperation.Nand32                  =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.truncate(algebra.not(algebra.and(a, b)), 32),
        algebra.nonZero
      )
    case ScalarBinaryOperation.Xnor32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.truncate(algebra.not(algebra.xor(a, b)), 32),
        algebra.nonZero
      )
    case ScalarBinaryOperation.BitFieldMask32 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) =>
        algebra.truncate(algebra.shiftLeft(algebra.ones(algebra.amount(a, 31L)), algebra.amount(b, 31L)), 32)
      )
    // the difference of the signed dwords wraps before its magnitude
    case ScalarBinaryOperation.AbsoluteDifferenceI32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.truncate(algebra.absolute(algebra.subtract(a, b), 32), 32),
        algebra.nonZero
      )
    case ScalarBinaryOperation.PackHighLow16 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.pack(algebra.high16(a), algebra.truncate(b, 16)))
    case ScalarBinaryOperation.PackLowLow16 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.pack(algebra.truncate(a, 16), algebra.truncate(b, 16)))
    case ScalarBinaryOperation.PackLowHigh16 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.pack(algebra.truncate(a, 16), algebra.high16(b)))
    case ScalarBinaryOperation.PackHighHigh16 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.pack(algebra.high16(a), algebra.high16(b)))
    case ScalarBinaryOperation.BitFieldExtractU32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.scalarExtract(a, b, signed = false),
        algebra.nonZero
      )
    case ScalarBinaryOperation.BitFieldExtractI32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.scalarExtract(a, b, signed = true),
        algebra.nonZero
      )
    case ScalarBinaryOperation.AddF32 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) =>
        algebra.flushedSingle(sinks.modeRegister, clamp = false, a, b)(Floats.add)
      )
    case ScalarBinaryOperation.SubtractF32 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) =>
        algebra.flushedSingle(sinks.modeRegister, clamp = false, a, b)(Floats.sub)
      )
    case ScalarBinaryOperation.MultiplyF32 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) =>
        algebra.flushedSingle(sinks.modeRegister, clamp = false, a, b)(Floats.mul)
      )
  }

  /** SOP2 operations on SRC0 and SRC1 register pairs; the logical forms set SCC when the result is non-zero. */
  inline def scalarBinary64[V, R](inline algebra: Algebra[V], operation: ScalarBinary64Operation)(
      inline sinks: AMDGPUSinks[V, R],
      dest: ScalarRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource
  ): R = operation match {
    case ScalarBinary64Operation.Multiply   => sinks.scalarPair(dest, srcA, srcB)((a, b, _) => algebra.multiply(a, b))
    case ScalarBinary64Operation.AddNoCarry => sinks.scalarPair(dest, srcA, srcB)((a, b, _) => algebra.add(a, b))
    case ScalarBinary64Operation.SubtractNoCarry =>
      sinks.scalarPair(dest, srcA, srcB)((a, b, _) => algebra.subtract(a, b))
    case ScalarBinary64Operation.And =>
      sinks.scalarPairFlagged(dest, srcA, srcB)((a, b, _) => algebra.and(a, b), algebra.nonZero)
    case ScalarBinary64Operation.AndNot =>
      sinks.scalarPairFlagged(dest, srcA, srcB)((a, b, _) => algebra.and(a, algebra.not(b)), algebra.nonZero)
    case ScalarBinary64Operation.Or =>
      sinks.scalarPairFlagged(dest, srcA, srcB)((a, b, _) => algebra.or(a, b), algebra.nonZero)
    case ScalarBinary64Operation.OrNot =>
      sinks.scalarPairFlagged(dest, srcA, srcB)((a, b, _) => algebra.or(a, algebra.not(b)), algebra.nonZero)
    case ScalarBinary64Operation.Nor =>
      sinks.scalarPairFlagged(dest, srcA, srcB)((a, b, _) => algebra.not(algebra.or(a, b)), algebra.nonZero)
    case ScalarBinary64Operation.Xor =>
      sinks.scalarPairFlagged(dest, srcA, srcB)((a, b, _) => algebra.xor(a, b), algebra.nonZero)
    case ScalarBinary64Operation.ShiftLeft =>
      sinks.scalarPairFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.shiftLeft(a, algebra.amount(b, 63L)),
        algebra.nonZero
      )
    case ScalarBinary64Operation.ShiftRight =>
      sinks.scalarPairFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.shiftRightLogical(a, algebra.amount(b, 63L)),
        algebra.nonZero
      )
    case ScalarBinary64Operation.ShiftRightArithmetic =>
      sinks.scalarPairFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.shiftRightArithmetic(a, algebra.amount(b, 63L)),
        algebra.nonZero
      )
    case ScalarBinary64Operation.Nand =>
      sinks.scalarPairFlagged(dest, srcA, srcB)((a, b, _) => algebra.not(algebra.and(a, b)), algebra.nonZero)
    case ScalarBinary64Operation.Xnor =>
      sinks.scalarPairFlagged(dest, srcA, srcB)((a, b, _) => algebra.not(algebra.xor(a, b)), algebra.nonZero)
    // both operands are dwords: the field width in SRC0[5:0] and its offset in SRC1[5:0]
    case ScalarBinary64Operation.BitFieldMask =>
      sinks.scalarPair(dest, srcA, srcB)((a, b, _) =>
        algebra.shiftLeft(
          algebra.select(
            algebra.equal(algebra.amount(a, 0x3fL), algebra.literal(0L)),
            algebra.literal(0L),
            algebra.shiftRightLogical(
              algebra.literal(-1L),
              algebra.subtract(algebra.literal(64L), algebra.amount(a, 0x3fL))
            )
          ),
          algebra.amount(b, 0x3fL)
        )
      )
    case ScalarBinary64Operation.BitFieldExtractU64 =>
      sinks.scalarPairDwordFlagged(dest, srcA, srcB)(
        (a, b, _) =>
          algebra.bitFieldExtract(
            a,
            algebra.amount(b, 0x3fL),
            algebra.minimum(
              algebra.and(algebra.shiftRightLogical(b, algebra.literal(16L)), algebra.literal(0x7fL)),
              algebra.literal(64L),
              signed = true
            ),
            signed = false
          ),
        algebra.nonZero
      )
    // the offset is SRC1[5:0] and the width SRC1[22:16]; the field shifts arithmetically, so its sign fills past bit 63
    case ScalarBinary64Operation.BitFieldExtractI64 =>
      sinks.scalarPairDwordFlagged(dest, srcA, srcB)(
        (a, b, _) =>
          algebra.bitFieldExtract(
            a,
            algebra.amount(b, 0x3fL),
            algebra.minimum(
              algebra.and(algebra.shiftRightLogical(b, algebra.literal(16L)), algebra.literal(0x7fL)),
              algebra.literal(64L),
              signed = true
            ),
            signed = true
          ),
        algebra.nonZero
      )
  }

  /** SOP1 operations on SRC0 and the destination's previous dword, at the widths [[scalarUnaryReadsPair]] and
    * [[scalarUnaryWritesPair]] give. The complements set SCC when the result is non-zero.
    */
  inline def scalarUnary[V, R](inline algebra: Algebra[V], operation: ScalarUnaryOperation)(
      inline sinks: AMDGPUSinks[V, R],
      dest: ScalarRegisterIndex,
      srcA: InstructionSource
  ): R = {
    val readsPair  = scalarUnaryReadsPair(operation)
    val writesPair = scalarUnaryWritesPair(operation)
    operation match {
      case ScalarUnaryOperation.U32FromF32 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.singleToInteger(x, signed = false))
      case ScalarUnaryOperation.F32FromU32 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.unsignedToSingle(x))
      case ScalarUnaryOperation.BitReplicate =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.primitive(x)(Media.bitReplicate))
      case ScalarUnaryOperation.TruncateF32 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.truncateSingle(x))
      case ScalarUnaryOperation.AbsoluteI32 =>
        sinks.scalarUnaryFlagged(dest, srcA, readsPair, writesPair)(
          (x, _) => algebra.truncate(algebra.absolute(x, 32), 32),
          (_, _, result) => algebra.nonZeroValue(result)
        )
      case ScalarUnaryOperation.SignExtendI8 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.truncate(algebra.signExtend(x, 8), 32))
      case ScalarUnaryOperation.SignExtendI16 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.truncate(algebra.signExtend(x, 16), 32))
      case ScalarUnaryOperation.CountBits32 =>
        sinks.scalarUnaryFlagged(dest, srcA, readsPair, writesPair)(
          (x, _) => algebra.populationCount(algebra.truncate(x, 32)),
          (_, _, result) => algebra.nonZeroValue(result)
        )
      case ScalarUnaryOperation.CountBits64 =>
        sinks.scalarUnaryFlagged(dest, srcA, readsPair, writesPair)(
          (x, _) => algebra.populationCount(x),
          (_, _, result) => algebra.nonZeroValue(result)
        )
      case ScalarUnaryOperation.CountZeros32 =>
        sinks.scalarUnaryFlagged(dest, srcA, readsPair, writesPair)(
          (x, _) => algebra.populationCount(algebra.complement(x)),
          (_, _, result) => algebra.nonZeroValue(result)
        )
      case ScalarUnaryOperation.CountZeros64 =>
        sinks.scalarUnaryFlagged(dest, srcA, readsPair, writesPair)(
          (x, _) => algebra.populationCount(algebra.not(x)),
          (_, _, result) => algebra.nonZeroValue(result)
        )
      case ScalarUnaryOperation.FirstOneLow32 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.firstBitLow(x, 32))
      case ScalarUnaryOperation.FirstZeroLow32 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.firstBitLow(algebra.complement(x), 32))
      case ScalarUnaryOperation.FirstOneLow64 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.firstBitLow(x, 64))
      case ScalarUnaryOperation.FirstZeroLow64 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.firstBitLow(algebra.not(x), 64))
      case ScalarUnaryOperation.ReverseBits64 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.reverseBits(x, 64))
      case ScalarUnaryOperation.ClearBit64 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, d) =>
          algebra.and(d, algebra.not(algebra.shiftLeft(algebra.literal(1L), algebra.amount(x, 63L))))
        )
      case ScalarUnaryOperation.SetBit64 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, d) =>
          algebra.or(d, algebra.shiftLeft(algebra.literal(1L), algebra.amount(x, 63L)))
        )
      case ScalarUnaryOperation.QuadMask32 =>
        sinks.scalarUnaryFlagged(dest, srcA, readsPair, writesPair)(
          (x, _) => algebra.quadMask(x, 8),
          (_, _, result) => algebra.nonZeroValue(result)
        )
      case ScalarUnaryOperation.QuadMask64 =>
        sinks.scalarUnaryFlagged(dest, srcA, readsPair, writesPair)(
          (x, _) => algebra.quadMask(x, 16),
          (_, _, result) => algebra.nonZeroValue(result)
        )
      case ScalarUnaryOperation.WholeQuad32 =>
        sinks.scalarUnaryFlagged(dest, srcA, readsPair, writesPair)(
          (x, _) => algebra.wholeQuad(x, 8),
          (_, _, result) => algebra.nonZeroValue(result)
        )
      case ScalarUnaryOperation.WholeQuad64 =>
        sinks.scalarUnaryFlagged(dest, srcA, readsPair, writesPair)(
          (x, _) => algebra.wholeQuad(x, 16),
          (_, _, result) => algebra.nonZeroValue(result)
        )
      case ScalarUnaryOperation.ReverseBits32 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.reversed(x))
      case ScalarUnaryOperation.Not32 =>
        sinks.scalarUnaryFlagged(dest, srcA, readsPair, writesPair)(
          (x, _) => algebra.complement(x),
          (_, _, result) => algebra.notEqual(result, algebra.literal(0L))
        )
      case ScalarUnaryOperation.Not64 =>
        sinks.scalarUnaryFlagged(dest, srcA, readsPair, writesPair)(
          (x, _) => algebra.not(x),
          (_, _, result) => algebra.notEqual(result, algebra.literal(0L))
        )
      case ScalarUnaryOperation.FirstBitHighI32 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.firstBitHigh(x, 32, signed = true))
      case ScalarUnaryOperation.FirstBitHighI64 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.firstBitHigh(x, 64, signed = true))
      case ScalarUnaryOperation.FirstBitHighU32 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.firstBitHigh(x, 32, signed = false))
      case ScalarUnaryOperation.FirstBitHighU64 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, _) => algebra.firstBitHigh(x, 64, signed = false))
      case ScalarUnaryOperation.ClearBit32 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, d) =>
          algebra.truncate(algebra.and(d, algebra.not(algebra.bit(x))), 32)
        )
      case ScalarUnaryOperation.SetBit32 =>
        sinks.scalarUnary(dest, srcA, readsPair, writesPair)((x, d) =>
          algebra.truncate(algebra.or(d, algebra.bit(x)), 32)
        )
    }
  }

  /** Whether a SOP1 operation reads its source as a register pair. */
  def scalarUnaryReadsPair(operation: ScalarUnaryOperation): Boolean = operation match {
    case ScalarUnaryOperation.Not64 | ScalarUnaryOperation.CountBits64 | ScalarUnaryOperation.FirstBitHighI64 |
        ScalarUnaryOperation.FirstBitHighU64 | ScalarUnaryOperation.CountZeros64 | ScalarUnaryOperation.FirstOneLow64 |
        ScalarUnaryOperation.FirstZeroLow64 | ScalarUnaryOperation.ReverseBits64 | ScalarUnaryOperation.QuadMask64 |
        ScalarUnaryOperation.WholeQuad64 => true
    case _ => false
  }

  /** Whether a SOP1 operation writes a register pair. */
  def scalarUnaryWritesPair(operation: ScalarUnaryOperation): Boolean = operation match {
    case ScalarUnaryOperation.Not64 | ScalarUnaryOperation.ReverseBits64 | ScalarUnaryOperation.ClearBit64 |
        ScalarUnaryOperation.SetBit64 | ScalarUnaryOperation.QuadMask64 | ScalarUnaryOperation.WholeQuad64 |
        ScalarUnaryOperation.BitReplicate => true
    case _ => false
  }

  /** FFBH: the leading zeros of a `width`-bit value or, when `signed`, the leading bits equal to the sign; all ones
    * when every bit is.
    */
  extension [V](inline algebra: Algebra[V]) {
    inline def firstBitHigh(value: V, width: Int in 1 ~ 64, inline signed: Boolean): V =
      algebra.findMostSignificant(value, width, signed, fromTop = true, none = 0xffffffffL)

    /** FFBL: the trailing zeros of a `width`-bit value, or all ones when it is zero. */
    inline def firstBitLow(value: V, width: Int in 1 ~ 64): V =
      algebra.findLeastSignificant(value, width, none = 0xffffffffL)

    /** S_QUADMASK: bit `i` of `quads` is set when any of the value's bits `4i` to `4i + 3` is. */
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    inline def quadMask(value: V, quads: Int): V = {
      var mask = algebra.literal(0L)
      var quad = 0
      while (quad < quads) {
        mask = algebra.or(
          mask,
          algebra.shiftLeft(
            algebra.nonZeroValue(algebra.and(value, algebra.literal(0xfL << (quad * 4)))),
            algebra.literal(quad.toLong)
          )
        )
        quad += 1
      }
      mask
    }

    /** S_WQM: each of `quads` nibbles all set when any of its bits is. */
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    inline def wholeQuad(value: V, quads: Int): V = {
      var mask = algebra.literal(0L)
      var quad = 0
      while (quad < quads) {
        mask = algebra.or(
          mask,
          algebra.multiply(
            algebra.nonZeroValue(algebra.and(value, algebra.literal(0xfL << (quad * 4)))),
            algebra.literal(0xfL << (quad * 4))
          )
        )
        quad += 1
      }
      mask
    }

    /** The single set bit a dword's low five bits index. */
    inline def bit(index: V): V =
      algebra.shiftLeft(algebra.literal(1L), algebra.amount(index, 31L))

    /** The magnitude of a `width`-bit two's-complement value; the most negative value is its own magnitude. */
    inline def absolute(value: V, width: Int in 1 ~ 64): V =
      algebra.let("x", algebra.signExtend(value, width))(x =>
        algebra.select(
          algebra.lessThan(x, algebra.literal(0L), signed = true),
          algebra.subtract(algebra.literal(0L), x),
          x
        )
      )
  }

  inline def vectorBitFind[V, R](inline algebra: Algebra[V], operation: VectorBitFindOperation)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      srcA: InstructionSource
  ): R = operation match {
    case VectorBitFindOperation.FirstBitLow32 =>
      sinks.vectorUnary(RegisterBank.Vector, dest, srcA)(x => algebra.firstBitLow(x, 32))
    case VectorBitFindOperation.FirstBitHighI32 =>
      sinks.vectorUnary(RegisterBank.Vector, dest, srcA)(x => algebra.firstBitHigh(x, 32, signed = true))
    case VectorBitFindOperation.FirstBitHighU32 =>
      sinks.vectorUnary(RegisterBank.Vector, dest, srcA)(x => algebra.firstBitHigh(x, 32, signed = false))
  }

  /** A dword move. */
  extension [V](inline algebra: Algebra[V]) {
    inline def moved(value: V): V = algebra.truncate(value, 32)

    /** A register-pair move. */
    inline def moved64(value: V): V = algebra.truncate(value, 64)

    inline def complement(value: V): V = algebra.truncate(algebra.not(value), 32)

    inline def reversed(value: V): V = algebra.reverseBits(value, 32)

    /** V_CNDMASK: SRC1 where the condition bit is set, otherwise SRC0. */
    inline def conditional(a: V, b: V, condition: V): V =
      algebra.truncate(algebra.select(condition, b, a), 32)

    /** V_BCNT: the set bits of SRC0's dword added to SRC1. */
    inline def bitCount(a: V, b: V): V =
      algebra.truncate(algebra.add(algebra.populationCount(algebra.truncate(a, 32)), algebra.truncate(b, 32)), 32)
  }

  /** V_MBCNT on the mask, the lane index and the base: the mask bits of the lanes below this one in the half of the
    * wave `high` names, added to the base.
    */
  inline def maskBitCount[V, R](inline algebra: Algebra[V], high: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      maskSource: InstructionSource,
      baseSource: InstructionSource
  ): R =
    if (high)
      sinks.maskCount(dest, maskSource, baseSource)((mask, lane, base) =>
        algebra.countBelow(
          mask,
          algebra.select(
            algebra.lessThan(lane, algebra.literal(32L), signed = true),
            algebra.literal(0L),
            algebra.ones(algebra.amount(lane, 31L))
          ),
          base
        )
      )
    else
      sinks.maskCount(dest, maskSource, baseSource)((mask, lane, base) =>
        algebra.countBelow(
          mask,
          algebra.select(
            algebra.lessThan(lane, algebra.literal(32L), signed = true),
            algebra.ones(lane),
            algebra.literal(0xffffffffL)
          ),
          base
        )
      )

  extension [V](inline algebra: Algebra[V]) {
    inline def countBelow(mask: V, lanes: V, base: V): V =
      algebra.truncate(algebra.add(base, algebra.populationCount(algebra.truncate(algebra.and(mask, lanes), 32))), 32)
  }

  /** V_BITOP3: each result bit is bit `(a << 2) | (b << 1) | c` of the truth table. */
  inline def bitOperation3[V, R](inline algebra: Algebra[V], half: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource,
      table: Int
  ): R =
    if (half)
      sinks.vectorTernary(dest, srcA, srcB, srcC, SourceModifiers.Unmodified, 32)((a, b, c) =>
        algebra.truncate(algebra.truthTable(a, b, c, table), 16)
      )
    else
      sinks.vectorTernary(dest, srcA, srcB, srcC, SourceModifiers.Unmodified, 32)((a, b, c) =>
        algebra.truncate(algebra.truthTable(a, b, c, table), 32)
      )

  extension [V](inline algebra: Algebra[V]) {
    inline def truthTable(a: V, b: V, c: V, table: Int): V =
      algebra.blend(
        a,
        algebra.blend(
          b,
          algebra.blend(c, algebra.entry(table, 7), algebra.entry(table, 6)),
          algebra.blend(c, algebra.entry(table, 5), algebra.entry(table, 4))
        ),
        algebra.blend(
          b,
          algebra.blend(c, algebra.entry(table, 3), algebra.entry(table, 2)),
          algebra.blend(c, algebra.entry(table, 1), algebra.entry(table, 0))
        )
      )

    /** Bit `index` of the truth table, broadcast to every bit. */
    inline def entry(table: Int, inline index: Int): V =
      algebra.literal(-((table >>> index) & 1).toLong)
  }

  inline def integerDot[V, R](inline algebra: Algebra[V], inline width: Int, inline signed: Boolean, clamp: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource
  ): R = sinks.vectorTernary(dest, srcA, srcB, srcC, SourceModifiers.Unmodified, 32)((a, b, c) =>
    algebra.let(
      "s",
      algebra.dot(
        algebra.extend(c, 32, signed),
        a,
        b,
        width = width,
        lanes = 32 / width,
        leftSigned = signed,
        rightSigned = signed
      )
    )(sum =>
      if (!clamp) algebra.truncate(sum, 32)
      else if (signed) algebra.truncate(algebra.clampSigned(sum, 32), 32)
      else algebra.saturateUnsigned(sum, 32)
    )
  )

  /** V_DOT4_I32_IU8 and V_DOT8_I32_IU4: the elements of SRC0 and SRC1, signed where NEG_LO says so, multiplied and
    * added to the signed SRC2 dword; `clamp` saturates the signed sum.
    */
  inline def mixedDot[V, R](
      inline algebra: Algebra[V],
      inline width: Int,
      signedA: Boolean,
      signedB: Boolean,
      clamp: Boolean
  )(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource
  ): R = sinks.vectorTernary(dest, srcA, srcB, srcC, SourceModifiers.Unmodified, 32)((a, b, c) =>
    algebra.let(
      "s",
      algebra.dot(
        algebra.signExtend(c, 32),
        a,
        b,
        width = width,
        lanes = 32 / width,
        leftSigned = signedA,
        rightSigned = signedB
      )
    )(sum =>
      if (clamp) algebra.truncate(algebra.clampSigned(sum, 32), 32) else algebra.truncate(sum, 32)
    )
  )

  /** V_DOT4_U32_U8: four unsigned byte products added to the accumulator dword. */
  extension [V](inline algebra: Algebra[V]) {
    inline def dotBytes(a: V, b: V, c: V): V =
      algebra.truncate(
        algebra.dot(
          algebra.truncate(c, 32),
          a,
          b,
          width = 8,
          lanes = 4,
          leftSigned = false,
          rightSigned = false
        ),
        32
      )
  }

  /** V_MAD_U64_U32 and V_MAD_I64_I32 on SRC0 and SRC1 dwords and a 64-bit addend; the flag is the unsigned carry out of
    * the 64-bit addition, and `clamp` saturates the sum.
    */
  inline def multiplyAdd64[V, R](inline algebra: Algebra[V], signed: Boolean, clamp: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      carryDest: ScalarRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource
  ): R =
    if (signed)
      sinks.multiplyAdd64(dest, carryDest, srcA, srcB, srcC)(
        (a, b, c, _) =>
          algebra.let("p", algebra.multiply(algebra.signExtend(a, 32), algebra.signExtend(b, 32)))(product =>
            algebra.let("s", algebra.add(product, c))(sum =>
              if (clamp)
                algebra.select(
                  algebra.lessThan(
                    algebra.and(algebra.xor(product, sum), algebra.xor(c, sum)),
                    algebra.literal(0L),
                    signed = true
                  ),
                  algebra.select(
                    algebra.lessThan(c, algebra.literal(0L), signed = true),
                    algebra.literal(Long.MinValue),
                    algebra.literal(Long.MaxValue)
                  ),
                  sum
                )
              else sum
            )
          ),
        (a, b, c, _, _) =>
          algebra.let("p", algebra.multiply(algebra.signExtend(a, 32), algebra.signExtend(b, 32)))(product =>
            algebra.lessThan(algebra.add(product, c), product, signed = false)
          )
      )
    else
      sinks.multiplyAdd64(dest, carryDest, srcA, srcB, srcC)(
        (a, b, c, _) =>
          algebra.let("p", algebra.multiply(algebra.truncate(a, 32), algebra.truncate(b, 32)))(product =>
            algebra.let("s", algebra.add(product, c))(sum =>
              if (clamp) algebra.select(algebra.lessThan(sum, product, signed = false), algebra.literal(-1L), sum)
              else sum
            )
          ),
        (a, b, c, _, _) =>
          algebra.let("p", algebra.multiply(algebra.truncate(a, 32), algebra.truncate(b, 32)))(product =>
            algebra.lessThan(algebra.add(product, c), product, signed = false)
          )
      )

  /** 64-bit shifts of a register pair by a dword's low six bits. */
  extension [V](inline algebra: Algebra[V]) {
    inline def shiftLeft64(value: V, count: V): V =
      algebra.shiftLeft(value, algebra.amount(count, 63L))

    inline def shiftRight64(value: V, count: V): V =
      algebra.shiftRightLogical(value, algebra.amount(count, 63L))

    inline def shiftRightArithmetic64(value: V, count: V): V =
      algebra.shiftRightArithmetic(value, algebra.amount(count, 63L))

    /** V_LSHL_ADD_U64: the count's low three bits shift by up to four; larger counts act as zero. */
    inline def shiftLeftAdd64(value: V, count: V, addend: V): V =
      algebra.let("n", algebra.amount(count, 7L))(n =>
        algebra.add(
          algebra.shiftLeft(
            value,
            algebra.select(algebra.lessOrEqual(n, algebra.literal(4L), signed = true), n, algebra.literal(0L))
          ),
          addend
        )
      )

    /** Places the low half of `value` into the half of `existing`'s dword that `high` names, keeping the other half. */
    inline def mergeHalf(existing: V, value: V, high: Boolean): V =
      algebra.placeField(existing, value, if (high) 16 else 0, 16, 32)

    /** The high or low half of a dword. */
    inline def halfOf(value: V, high: Boolean): V =
      if (high) algebra.high16(value) else algebra.truncate(value, 16)

    /** The low half of `value` in the half of a dword `high` names, the other half zero. */
    inline def zeroedHalf(value: V, high: Boolean): V =
      if (high) algebra.shiftLeft(algebra.truncate(value, 16), algebra.literal(16L)) else algebra.truncate(value, 16)

    /** V_PK_MOV_B32: the dwords of SRC0 and SRC1 that `highA` and `highB` select, as a register pair. */
    inline def packedMove(a: V, b: V, highA: Boolean, highB: Boolean): V =
      algebra.or(algebra.dword(a, highA), algebra.shiftLeft(algebra.dword(b, highB), algebra.literal(32L)))

    inline def dword(value: V, high: Boolean): V =
      if (high) algebra.shiftRightLogical(value, algebra.literal(32L)) else algebra.truncate(value, 32)

    /** S_CSELECT and S_CMOV: SRC0 when SCC is set, otherwise SRC1. */
    inline def scalarSelect(a: V, b: V, condition: V): V = algebra.select(condition, a, b)
  }

  /** S_*_SAVEEXEC on the source and the previous EXEC: the next EXEC, and SCC set when it is non-zero. */
  inline def saveExec[V, R](inline algebra: Algebra[V], operation: SaveExecOperation)(
      inline sinks: AMDGPUSinks[V, R],
      dest: ScalarRegisterIndex,
      srcA: InstructionSource,
      width: Int in (32, 64)
  ): R = operation match {
    case SaveExecOperation.And =>
      sinks.saveExec(dest, srcA, width)((x, exec) => algebra.and(x, exec), (_, _, next) => algebra.nonZeroValue(next))
    case SaveExecOperation.AndNot =>
      sinks.saveExec(dest, srcA, width)(
        (x, exec) => algebra.and(x, algebra.not(exec)),
        (_, _, next) => algebra.nonZeroValue(next)
      )
    case SaveExecOperation.Or =>
      sinks.saveExec(dest, srcA, width)((x, exec) => algebra.or(x, exec), (_, _, next) => algebra.nonZeroValue(next))
    case SaveExecOperation.Xor =>
      sinks.saveExec(dest, srcA, width)((x, exec) => algebra.xor(x, exec), (_, _, next) => algebra.nonZeroValue(next))
    case SaveExecOperation.OrNot =>
      sinks.saveExec(dest, srcA, width)(
        (x, exec) => algebra.truncate(algebra.or(x, algebra.not(exec)), width.widen[Int in 1 ~ 64]),
        (_, _, next) => algebra.nonZeroValue(next)
      )
    case SaveExecOperation.Nand =>
      sinks.saveExec(dest, srcA, width)(
        (x, exec) => algebra.truncate(algebra.not(algebra.and(x, exec)), width.widen[Int in 1 ~ 64]),
        (_, _, next) => algebra.nonZeroValue(next)
      )
    case SaveExecOperation.Nor =>
      sinks.saveExec(dest, srcA, width)(
        (x, exec) => algebra.truncate(algebra.not(algebra.or(x, exec)), width.widen[Int in 1 ~ 64]),
        (_, _, next) => algebra.nonZeroValue(next)
      )
    case SaveExecOperation.Xnor =>
      sinks.saveExec(dest, srcA, width)(
        (x, exec) => algebra.truncate(algebra.not(algebra.xor(x, exec)), width.widen[Int in 1 ~ 64]),
        (_, _, next) => algebra.nonZeroValue(next)
      )
    case SaveExecOperation.NotAnd =>
      sinks.saveExec(dest, srcA, width)(
        (x, exec) => algebra.and(algebra.not(x), exec),
        (_, _, next) => algebra.nonZeroValue(next)
      )
    case SaveExecOperation.NotOr =>
      sinks.saveExec(dest, srcA, width)(
        (x, exec) => algebra.truncate(algebra.or(algebra.not(x), exec), width.widen[Int in 1 ~ 64]),
        (_, _, next) => algebra.nonZeroValue(next)
      )
  }

  /** S_MULK_I32 on the destination and the sign-extended immediate; SCC records signed overflow. */
  inline def multiplyImmediate[V, R](inline algebra: Algebra[V])(
      inline sinks: AMDGPUSinks[V, R],
      dest: ScalarRegisterIndex,
      immediate: Long
  ): R = sinks.scalarImmediate(dest, immediate)(
    (d, k) => algebra.truncate(algebra.multiply(algebra.signExtend(d, 32), k), 32),
    (d, k, _) =>
      algebra.let("p", algebra.multiply(algebra.signExtend(d, 32), k))(product =>
        algebra.notEqual(algebra.signExtend(product, 32), product)
      )
  )

  /** S_ADDK_I32 on the destination and the sign-extended immediate; SCC records a sum outside the dword range. */
  inline def addImmediate[V, R](inline algebra: Algebra[V])(
      inline sinks: AMDGPUSinks[V, R],
      dest: ScalarRegisterIndex,
      immediate: Long
  ): R = sinks.scalarImmediate(dest, immediate)(
    (d, k) => algebra.truncate(algebra.add(d, k), 32),
    (d, k, _) => algebra.let("s", algebra.add(d, k))(sum => algebra.notEqual(algebra.truncate(sum, 32), sum))
  )

  /** S_MIN and S_MAX; SCC records whether SRC0 was strictly chosen. */
  inline def scalarMinMax[V, R](inline algebra: Algebra[V], operation: ScalarMinMaxOperation)(
      inline sinks: AMDGPUSinks[V, R],
      dest: ScalarRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource
  ): R = operation match {
    case ScalarMinMaxOperation.MinimumU32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.extremum(a, b, 32, signed = false, maximum = false),
        (a, b, _, _) => algebra.lessThan(a, b, signed = true)
      )
    case ScalarMinMaxOperation.MaximumU32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.extremum(a, b, 32, signed = false, maximum = true),
        (a, b, _, _) => algebra.lessThan(b, a, signed = true)
      )
    case ScalarMinMaxOperation.MinimumI32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.extremum(a, b, 32, signed = true, maximum = false),
        (a, b, _, _) => algebra.lessThan(algebra.signExtend(a, 32), algebra.signExtend(b, 32), signed = true)
      )
    case ScalarMinMaxOperation.MaximumI32 =>
      sinks.scalarDwordFlagged(dest, srcA, srcB)(
        (a, b, _) => algebra.extremum(a, b, 32, signed = true, maximum = true),
        (a, b, _, _) => algebra.lessThan(algebra.signExtend(b, 32), algebra.signExtend(a, 32), signed = true)
      )
  }

  /** S_CMP on SRC0 and SRC1, dwords or for the 64-bit forms register pairs, as the next SCC. */
  inline def scalarCompare[V, R](inline algebra: Algebra[V], operation: ScalarCompareOperation)(
      inline sinks: AMDGPUSinks[V, R],
      srcA: InstructionSource,
      srcB: InstructionSource
  ): R = operation match {
    case ScalarCompareOperation.EqualU32 | ScalarCompareOperation.EqualI32 | ScalarCompareOperation.EqualU64 =>
      sinks.scalarCompare(srcA, srcB, scalarCompareWide(operation))((a, b) => algebra.equal(a, b))
    case ScalarCompareOperation.NotEqualU32 | ScalarCompareOperation.NotEqualI32 |
        ScalarCompareOperation.NotEqualU64 =>
      sinks.scalarCompare(srcA, srcB, scalarCompareWide(operation))((a, b) => algebra.notEqual(a, b))
    case ScalarCompareOperation.LessU32 =>
      sinks.scalarCompare(srcA, srcB, scalarCompareWide(operation))((a, b) => algebra.lessThan(a, b, signed = true))
    case ScalarCompareOperation.LessOrEqualU32 =>
      sinks.scalarCompare(srcA, srcB, scalarCompareWide(operation))((a, b) => algebra.lessOrEqual(a, b, signed = true))
    case ScalarCompareOperation.GreaterU32 =>
      sinks.scalarCompare(srcA, srcB, scalarCompareWide(operation))((a, b) => algebra.lessThan(b, a, signed = true))
    case ScalarCompareOperation.GreaterOrEqualU32 =>
      sinks.scalarCompare(srcA, srcB, scalarCompareWide(operation))((a, b) => algebra.lessOrEqual(b, a, signed = true))
    case ScalarCompareOperation.LessI32 =>
      sinks.scalarCompare(srcA, srcB, scalarCompareWide(operation))((a, b) =>
        algebra.lessThan(algebra.signExtend(a, 32), algebra.signExtend(b, 32), signed = true)
      )
    case ScalarCompareOperation.LessOrEqualI32 =>
      sinks.scalarCompare(srcA, srcB, scalarCompareWide(operation))((a, b) =>
        algebra.lessOrEqual(algebra.signExtend(a, 32), algebra.signExtend(b, 32), signed = true)
      )
    case ScalarCompareOperation.GreaterI32 =>
      sinks.scalarCompare(srcA, srcB, scalarCompareWide(operation))((a, b) =>
        algebra.lessThan(algebra.signExtend(b, 32), algebra.signExtend(a, 32), signed = true)
      )
    case ScalarCompareOperation.GreaterOrEqualI32 =>
      sinks.scalarCompare(srcA, srcB, scalarCompareWide(operation))((a, b) =>
        algebra.lessOrEqual(algebra.signExtend(b, 32), algebra.signExtend(a, 32), signed = true)
      )
    case ScalarCompareOperation.BitSet32 =>
      sinks.scalarCompare(srcA, srcB, scalarCompareWide(operation))((a, b) =>
        algebra.and(algebra.shiftRightLogical(a, algebra.amount(b, 31L)), algebra.literal(1L))
      )
    case ScalarCompareOperation.BitClear32 =>
      sinks.scalarCompare(srcA, srcB, scalarCompareWide(operation))((a, b) =>
        algebra.xor(
          algebra.and(algebra.shiftRightLogical(a, algebra.amount(b, 31L)), algebra.literal(1L)),
          algebra.literal(1L)
        )
      )
    // SRC1 is a dword; its pair reads it in the low half
    case ScalarCompareOperation.BitSet64 =>
      sinks.scalarCompare(srcA, srcB, scalarCompareWide(operation))((a, b) =>
        algebra.and(algebra.shiftRightLogical(a, algebra.amount(b, 63L)), algebra.literal(1L))
      )
    case ScalarCompareOperation.BitClear64 =>
      sinks.scalarCompare(srcA, srcB, scalarCompareWide(operation))((a, b) =>
        algebra.xor(
          algebra.and(algebra.shiftRightLogical(a, algebra.amount(b, 63L)), algebra.literal(1L)),
          algebra.literal(1L)
        )
      )
  }

  /** Whether S_CMP compares register pairs. */
  def scalarCompareWide(operation: ScalarCompareOperation): Boolean = operation match {
    case ScalarCompareOperation.EqualU64 | ScalarCompareOperation.NotEqualU64 | ScalarCompareOperation.BitSet64 |
        ScalarCompareOperation.BitClear64 => true
    case _ => false
  }

  extension [V](inline algebra: Algebra[V]) {

    /** The lane a quad permutation names: the control's two-bit selector picks the source lane of each of a quad's four
      * lanes.
      */
    inline def quadPermutation(control: Int, lane: V): V =
      algebra.or(
        algebra.and(lane, algebra.literal(~3L)),
        algebra.and(
          algebra.shiftRightLogical(
            algebra.literal(control.toLong),
            algebra.shiftLeft(algebra.and(lane, algebra.literal(3L)), algebra.literal(1L))
          ),
          algebra.literal(3L)
        )
      )

    /** The lane a DPP16 control reads for `lane`, or -1 past its row or the wave; the decoder admits only valid
      * controls.
      */
    inline def dppSource(control: Int, lane: V): V = {
      val distance = algebra.literal((control & 15).toLong)
      if (DppControl.within(control, QUAD_PERM_FIRST, QUAD_PERM_LAST)) quadPermutation(control, lane)
      else if (DppControl.within(control, ROW_SHL_FIRST, ROW_SHL_LAST))
        algebra.select(
          algebra.lessThan(algebra.add(algebra.rowLane(lane), distance), algebra.literal(16L), signed = true),
          algebra.add(lane, distance),
          algebra.literal(-1L)
        )
      else if (DppControl.within(control, ROW_SHR_FIRST, ROW_SHR_LAST))
        algebra.select(
          algebra.lessOrEqual(distance, algebra.rowLane(lane), signed = true),
          algebra.subtract(lane, distance),
          algebra.literal(-1L)
        )
      else if (DppControl.within(control, ROW_ROR_FIRST, ROW_ROR_LAST))
        algebra.or(
          algebra.rowBase(lane),
          algebra.and(algebra.subtract(algebra.rowLane(lane), distance), algebra.literal(15L))
        )
      else if (DppControl.within(control, ROW_SHARE_FIRST, ROW_SHARE_LAST)) algebra.or(algebra.rowBase(lane), distance)
      else if (DppControl.within(control, ROW_XMASK_FIRST, ROW_XMASK_LAST))
        algebra.or(algebra.rowBase(lane), algebra.xor(algebra.rowLane(lane), distance))
      else if (control == WAVE_SHL1)
        algebra.select(
          algebra.lessThan(lane, algebra.literal(63L), signed = true),
          algebra.add(lane, algebra.literal(1L)),
          algebra.literal(-1L)
        )
      else if (control == WAVE_ROL1) algebra.and(algebra.add(lane, algebra.literal(1L)), algebra.literal(63L))
      else if (control == WAVE_SHR1)
        algebra.select(
          algebra.lessThan(algebra.literal(0L), lane, signed = true),
          algebra.subtract(lane, algebra.literal(1L)),
          algebra.literal(-1L)
        )
      else if (control == WAVE_ROR1) algebra.and(algebra.subtract(lane, algebra.literal(1L)), algebra.literal(63L))
      else if (control == ROW_MIRROR)
        algebra.or(algebra.rowBase(lane), algebra.subtract(algebra.literal(15L), algebra.rowLane(lane)))
      else if (control == ROW_HALF_MIRROR)
        algebra.or(
          algebra.and(lane, algebra.literal(~7L)),
          algebra.subtract(algebra.literal(7L), algebra.and(lane, algebra.literal(7L)))
        )
      else if (control == BCAST15)
        algebra.let("r", algebra.rowBase(lane))(base =>
          algebra.select(
            algebra.lessThan(algebra.literal(0L), base, signed = true),
            algebra.subtract(base, algebra.literal(1L)),
            lane
          )
        )
      else if (control == BCAST31)
        algebra.select(
          algebra.lessOrEqual(algebra.literal(32L), lane, signed = true),
          algebra.literal(31L),
          lane
        )
      else algebra.literal(-1L)
    }

    inline def rowBase(lane: V): V = algebra.and(lane, algebra.literal(~15L))

    inline def rowLane(lane: V): V = algebra.and(lane, algebra.literal(15L))

    /** The lane DPP8 reads for `lane`: the three-bit selector its position in a group of eight names. */
    inline def dpp8Source(selectors: V, lane: V): V =
      algebra.or(
        algebra.and(lane, algebra.literal(~7L)),
        algebra.and(
          algebra.shiftRightLogical(
            selectors,
            algebra.multiply(algebra.and(lane, algebra.literal(7L)), algebra.literal(3L))
          ),
          algebra.literal(7L)
        )
      )

    /** DS_SWIZZLE's source lane: the swizzle pattern in `control` permutes the wave's lanes. A quad permutation picks
      * the source lane of each of a quad's four lanes; otherwise the pattern's bit masks rewrite the low five bits of
      * the lane index and the higher bits pass through.
      */
    inline def swizzleSource(control: Int, lane: V): V =
      if ((control & Swizzle.QUAD_PERM_ENC_MASK) == Swizzle.QUAD_PERM_ENC) quadPermutation(control, lane)
      else bitmaskSwizzleSource(control, lane)

    /** DS_SWIZZLE's bit-masked mode: the offset packs AND[4:0], OR[9:5] and XOR[14:10] masks of the lane index's low
      * five bits, which the higher bits of the index pass through.
      */
    inline def bitmaskSwizzleSource(control: Int, lane: V): V =
      algebra.or(
        algebra.and(lane, algebra.literal(~0x1fL)),
        algebra.xor(
          algebra.or(
            algebra.and(lane, algebra.literal((control & 0x1f).toLong)),
            algebra.literal(((control >> 5) & 0x1f).toLong)
          ),
          algebra.literal(((control >> 10) & 0x1f).toLong)
        )
      )

    /** The lane a DS_PERMUTE or DS_BPERMUTE byte address names, within `lanes` lanes. */
    inline def addressedLane(address: V, offset: V, lanes: V): V =
      algebra.and(
        algebra.shiftRightLogical(algebra.add(address, offset), algebra.literal(2L)),
        algebra.subtract(lanes, algebra.literal(1L))
      )

    /** The lane a DS_PERMUTE or DS_BPERMUTE byte address names for `lane`, within `lanes` lanes or, with `halves`,
      * within the 32-lane half `lane` is in.
      */
    inline def permutedLane(address: V, offset: V, lane: V, lanes: V, halves: Boolean): V =
      if (halves)
        algebra.or(
          algebra.and(lane, algebra.literal(32L)),
          algebra.addressedLane(address, offset, algebra.literal(32L))
        )
      else algebra.addressedLane(address, offset, lanes)

    /** The lane V_READLANE and V_WRITELANE name, within a wave of `lanes` lanes. */
    inline def selectedLane(lane: V, lanes: V): V =
      algebra.and(lane, algebra.subtract(lanes, algebra.literal(1L)))

    /** An SDWA source selection: the selected part of a dword shifted down, zero- or sign-extended. */
    inline def selectedPart(value: V, selection: SubDwordSelection, signExtend: Boolean)
        : V = algebra.extend(
      algebra.shiftRightLogical(value, algebra.literal(selection.shift.toLong)),
      selection.width,
      signExtend
    )

    /** An SDWA result placed into the destination selection of the destination's previous dword, the other bits padded
      * with zeros, preserved or sign-extended as `unused` says.
      */
    inline def placedPart(
        existing: V,
        value: V,
        selection: SubDwordSelection,
        unused: SubDwordDestinationUnused
    ): V = {
      val mask   = Bits.mask(selection.width) << selection.shift
      val masked =
        algebra.and(algebra.shiftLeft(value, algebra.literal(selection.shift.toLong)), algebra.literal(mask))
      unused match {
        case SubDwordDestinationUnused.Pad      => algebra.let("p", masked)(placed => algebra.truncate(placed, 32))
        case SubDwordDestinationUnused.Preserve =>
          algebra.let("p", masked)(placed =>
            algebra.truncate(algebra.or(algebra.and(existing, algebra.literal(~mask)), placed), 32)
          )
        case SubDwordDestinationUnused.SignExtend =>
          algebra.let("p", masked)(placed => algebra.truncate(algebra.signExtend(placed, selection.upperBit), 32))
      }
    }

    /** A 64-bit address plus a signed displacement. */
    inline def displaced(base: V, displacement: V): V = algebra.add(base, displacement)

    /** A 64-bit base plus a zero-extended dword offset and a signed displacement. */
    inline def offsetAddress(base: V, offset: V, displacement: V): V =
      algebra.add(algebra.add(base, algebra.truncate(offset, 32)), displacement)

    /** A zero-extended dword base plus a zero-extended dword offset and a signed displacement. */
    inline def dwordAddress(base: V, offset: V, displacement: V): V =
      algebra.offsetAddress(algebra.truncate(base, 32), offset, displacement)

    /** A scratch buffer access through a null descriptor: the wave's scalar offset spreads across its lanes. */
    inline def interleavedAddress(start: V, offset: V, lanes: V): V =
      algebra.add(start, algebra.divide(algebra.truncate(offset, 32), lanes, signed = true))

    /** Where a DS source-2 instruction finds its other operand: four bytes on from `address` per dword of the
      * instruction's signed displacement or, without one, of the signed `address[31:17]`.
      */
    inline def source2Address(address: V, displacement: Option[Int]): V = {
      val dwords =
        displacement.fold(algebra.shiftRightArithmetic(algebra.signExtend(address, 32), algebra.literal(17L)))(value =>
          algebra.literal(value.toLong)
        )
      algebra.truncate(algebra.add(address, algebra.shiftLeft(dwords, algebra.literal(2L))), 32)
    }

    /** The byte of the LDS an address names; one past the workgroup's allocation reads zero and drops writes. */
    inline def ldsOffset(address: V): V = algebra.truncate(address, 32)

    /** The LDS address of an ADDTID access: M0[15:0] plus `offset` plus four bytes a lane. */
    inline def laneLocalAddress(m0: V, offset: Long, lane: V): V =
      algebra.truncate(
        algebra.add(
          algebra.add(algebra.and(m0, algebra.literal(0xffffL)), algebra.literal(offset)),
          algebra.multiply(lane, algebra.literal(4L))
        ),
        32
      )

    /** The global address of an ADDTID access: a 64-bit base plus `offset` plus four bytes a lane. */
    inline def laneGlobalAddress(base: V, offset: Long, lane: V): V =
      algebra.add(algebra.add(base, algebra.literal(offset)), algebra.multiply(lane, algebra.literal(4L)))

    /** A loaded value of `width` bits, sign-extended for the signed loads. */
    inline def loaded(value: V, width: Int in 1 ~ 64, signed: Boolean): V =
      if (signed) algebra.signExtend(value, width) else value

    /** Compare-and-swap: `swap` replaces `previous` when it equals `comparand`. */
    inline def compareSwap(previous: V, comparand: V, swap: V): V =
      algebra.select(algebra.equal(previous, comparand), swap, previous)

    /** S_LOAD: a scalar pair base plus a byte displacement and an optional scalar offset. */
    inline def scalarAddress(base: V, displacement: V, offset: V): V =
      algebra.add(algebra.add(base, displacement), offset)

    /** SMEM ignores the low two address bits of a dword access. */
    inline def scalarDword(address: V): V = algebra.and(address, algebra.literal(~3L))

    inline def nonZero: (V, V, V, V) => V =
      (_, _, _, result) => algebra.notEqual(result, algebra.literal(0L))

    inline def nonZeroValue(value: V): V = algebra.notEqual(value, algebra.literal(0L))

    inline def signBit(value: V): V =
      algebra.notEqual(algebra.and(value, algebra.literal(0x80000000L)), algebra.literal(0L))

    /** M0 with its VGPR index byte from `index` when `setsIndex`, and its indexed operand bits from `mode` unless it is
      * negative.
      */
    inline def gprIndex(index: V, m0: V, setsIndex: Boolean, mode: Int): V = {
      val indexMask = if (setsIndex) 0xffL else 0L
      val modeMask  = if (mode >= 0) 0xf000L else 0L
      algebra.or(
        algebra.or(
          algebra.and(m0, algebra.literal(0xffffffffL & ~(indexMask | modeMask))),
          algebra.and(index, algebra.literal(indexMask))
        ),
        algebra.literal((mode.toLong & 0xfL) << 12 & modeMask)
      )
    }

    /** The bits a D16_HI store writes, and the high half of a dword. */
    inline def high16(value: V): V = algebra.field(value, 16, 16, signed = false)

    inline def pack(low: V, high: V): V =
      algebra.or(low, algebra.shiftLeft(high, algebra.literal(16L)))

    /** A signed result's low `width` bits, saturated to the signed range first when `clamp`. */
    inline def signedResult(value: V, width: Int in 1 ~ 63, clamp: Boolean): V =
      algebra.truncate(
        if (clamp) algebra.clampSigned(value, width.widen[Int in 1 ~ 64]) else value,
        width.widen[Int in 1 ~ 64]
      )

    /** A signed 16-bit value saturated to an unsigned byte. */
    inline def saturatedByte(value: V): V = algebra.truncate(
      algebra.saturateUnsigned(algebra.maximum(algebra.signExtend(value, 16), algebra.literal(0L), signed = true), 8),
      8
    )

    /** V_LERP_U8: each byte the rounded mean of the two sources' bytes, rounding up where the third's low bit is set.
      */
    inline def lerpBytes(a: V, b: V, c: V): V =
      algebra.or(
        algebra.or(algebra.lerpByte(a, b, c, 0), algebra.lerpByte(a, b, c, 1)),
        algebra.or(algebra.lerpByte(a, b, c, 2), algebra.lerpByte(a, b, c, 3))
      )

    inline def lerpByte(a: V, b: V, c: V, inline index: Int): V =
      algebra.shiftLeft(
        algebra.shiftRightLogical(
          algebra.add(
            algebra.add(algebra.byte(a, index), algebra.byte(b, index)),
            algebra.and(algebra.shiftRightLogical(c, algebra.literal(index * 8L)), algebra.literal(1L))
          ),
          algebra.literal(1L)
        ),
        algebra.literal(index * 8L)
      )
  }

  /** Media operations on modified dwords: byte interpolation, cube map coordinates, packing and bit masks. */
  inline def media[V, R](inline algebra: Algebra[V], operation: VectorMediaOperation)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource,
      modifiers: SourceModifiers
  ): R = operation match {
    case VectorMediaOperation.LerpU8 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) => algebra.lerpBytes(a, b, c))
    case VectorMediaOperation.CubeIdF32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) => algebra.primitive(a, b, c)(Media.cubeId))
    case VectorMediaOperation.CubeScF32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) => algebra.primitive(a, b, c)(Media.cubeSc))
    case VectorMediaOperation.CubeTcF32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) => algebra.primitive(a, b, c)(Media.cubeTc))
    case VectorMediaOperation.CubeMaF32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) => algebra.primitive(a, b, c)(Media.cubeMa))
    case VectorMediaOperation.PackByteF32 =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
        algebra.primitive(a, b, c)(Media.packByteF32)
      )
    case VectorMediaOperation.PackI16I32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, 32)((a, b, _) =>
        algebra.pack(
          algebra.truncate(algebra.clampSigned(algebra.signExtend(a, 32), 16), 16),
          algebra.truncate(algebra.clampSigned(algebra.signExtend(b, 32), 16), 16)
        )
      )
    case VectorMediaOperation.PackU16U32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, 32)((a, b, _) =>
        algebra.pack(algebra.saturateUnsigned(a, 16), algebra.saturateUnsigned(b, 16))
      )
    case VectorMediaOperation.PackNormI16F32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, 32)((a, b, _) => algebra.primitive(a, b)(Media.packNormI16F32))
    case VectorMediaOperation.PackNormU16F32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, 32)((a, b, _) => algebra.primitive(a, b)(Media.packNormU16F32))
    case VectorMediaOperation.PackNormI16F16 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, 16)((a, b, _) => algebra.primitive(a, b)(Media.packNormI16F16))
    case VectorMediaOperation.PackNormU16F16 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, 16)((a, b, _) => algebra.primitive(a, b)(Media.packNormU16F16))
    case VectorMediaOperation.PackF16F32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, 32)((a, b, _) =>
        algebra.pack(algebra.singleToHalf(a), algebra.singleToHalf(b))
      )
    case VectorMediaOperation.PackBF16F32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, 32)((a, b, _) => algebra.primitive(a, b)(Media.packBF16F32))
    case VectorMediaOperation.PackI16F32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, 32)((a, b, _) => algebra.primitive(a, b)(Media.packI16F32))
    case VectorMediaOperation.PackU16F32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, 32)((a, b, _) => algebra.primitive(a, b)(Media.packU16F32))
    case VectorMediaOperation.BitFieldMask32 =>
      sinks.vectorBinary(dest, srcA, srcB)((a, b) =>
        algebra.truncate(algebra.shiftLeft(algebra.ones(algebra.amount(a, 31L)), algebra.amount(b, 31L)), 32)
      )
  }

  /** One dword of V_MQSAD_U32_U8: the masked byte differences at byte offset `index` of SRC0 added to SRC2's dword
    * `index`.
    */
  inline def maskedWindow[V, R](inline algebra: Algebra[V], inline index: Int)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource
  ): R = sinks.windowed(dest.offsetBy(index.unsafe[Int >= 0]), srcA, srcB, laterRegister(srcC, index.unsafe[Int >= 0]))(
    (a, b, c) =>
      algebra.primitive(a, b, c)((source: UInt[64], reference: UInt[32], accumulator: UInt[32]) =>
        Media.maskedSad(source, reference, accumulator, index)
      )
  )

  /** V_QSAD_PK_U16_U8 and V_MQSAD_PK_U16_U8 on a register pair, a reference dword and a register pair. */
  extension [V](inline algebra: Algebra[V]) {
    inline def quadSum(a: V, b: V, c: V, masked: Boolean): V =
      algebra.primitive(a, b, c)((source: UInt[64], reference: UInt[32], accumulator: UInt[64]) =>
        Media.quadSad(source, reference, accumulator, masked)
      )

    /** The selector nibble of a lane's position in its row of 16: from `low` for the first eight lanes, `high` after.
      */
    inline def laneNibble(lane: V, low: V, high: V): V =
      algebra.let("n", algebra.shiftLeft(algebra.and(lane, algebra.literal(7L)), algebra.literal(2L)))(shift =>
        algebra.select(
          algebra.equal(algebra.and(lane, algebra.literal(8L)), algebra.literal(0L)),
          algebra.and(algebra.shiftRightLogical(low, shift), algebra.literal(15L)),
          algebra.and(algebra.shiftRightLogical(high, shift), algebra.literal(15L))
        )
      )

    /** Whether two binary32 or binary64 values compare equal as floats: -0 equals +0 and NaN equals nothing. */
    inline def floatEqual(a: V, b: V, inline width: Int): V =
      inline width match {
        case 32 => algebra.primitive(a, b)(Floats.eq(_: Float32, _: Float32))
        case _  => algebra.primitive(a, b)(Floats.eq(_: Float64, _: Float64))
      }

    /** Dword `index` of DS_CONDXCHG32: the data's low 31 bits where its bit 31 is set, the old dword otherwise. */
    inline def conditionalDword(old: V, data: V, inline index: Int): V =
      algebra.let("d", algebra.truncate(algebra.shiftRightLogical(data, algebra.literal(index * 32L)), 32))(dword =>
        algebra.select(
          algebra.notEqual(algebra.shiftRightLogical(dword, algebra.literal(31L)), algebra.literal(0L)),
          algebra.and(dword, algebra.literal(0x7fffffffL)),
          algebra.truncate(algebra.shiftRightLogical(old, algebra.literal(index * 32L)), 32)
        )
      )
  }

  /** The register `words` after a vector register source; other sources stand for themselves. */
  def laterRegister(source: InstructionSource, words: Int >= 0): InstructionSource = source match {
    case InstructionSource.VectorRegister(index) => InstructionSource.VectorRegister(index.offsetBy(words))
    case other                                   => other
  }

  /** The sum of two operands' low `width` bits, saturated to the unsigned range. */
  extension [V](inline algebra: Algebra[V]) {
    inline def unsignedSum(left: V, right: V, width: Int in 1 ~ 63): V =
      algebra.saturateUnsigned(
        algebra.add(
          algebra.truncate(left, width.widen[Int in 1 ~ 64]),
          algebra.truncate(right, width.widen[Int in 1 ~ 64])
        ),
        width
      )

    /** The difference of two operands' low `width` bits, saturated at zero. */
    inline def unsignedDifference(left: V, right: V, width: Int in 1 ~ 63): V =
      algebra.let(
        "d",
        algebra.subtract(
          algebra.truncate(left, width.widen[Int in 1 ~ 64]),
          algebra.truncate(right, width.widen[Int in 1 ~ 64])
        )
      )(difference => algebra.maximum(difference, algebra.literal(0L), signed = true))

    /** A non-negative value clamped to the unsigned `width`-bit maximum. */
    inline def saturateUnsigned(value: V, width: Int in 1 ~ 63): V =
      algebra.minimum(value, algebra.literal(Bits.mask(width.widen[Int in 1 ~ 64])), signed = true)

    /** A signed dword shifted right arithmetically by the low five bits of `amount`. */
    inline def shiftedRight(value: V, amount: V): V =
      algebra.shiftRightArithmetic(algebra.signExtend(value, 32), algebra.and(amount, algebra.literal(31L)))
  }

  /** S_LSHL<n>_ADD_U32: SCC records the carry out of the shifted sum, the bits shifted out included. */
  inline def shiftLeftAdd[V, R](inline algebra: Algebra[V], shift: Int in 1 ~ 4)(
      inline sinks: AMDGPUSinks[V, R],
      dest: ScalarRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource
  ): R = sinks.scalarDwordFlagged(dest, srcA, srcB)(
    (a, b, _) => algebra.truncate(algebra.add(algebra.shiftLeft(a, algebra.literal(shift.toLong)), b), 32),
    (a, b, _, _) =>
      algebra.lessThan(
        algebra.literal(0xffffffffL),
        algebra.add(algebra.shiftLeft(a, algebra.literal(shift.toLong)), b),
        signed = true
      )
  )

  /** S_BFE: the offset in SRC1[4:0] and the width in SRC1[22:16], which saturates at 32; a signed field shifts in the
    * sign.
    */
  extension [V](inline algebra: Algebra[V]) {
    inline def scalarExtract(value: V, field: V, inline signed: Boolean): V =
      algebra.truncate(
        algebra.bitFieldExtract(
          if (signed) algebra.signExtend(value, 32) else value,
          algebra.and(field, algebra.literal(0x1fL)),
          algebra.minimum(
            algebra.and(algebra.shiftRightLogical(field, algebra.literal(16L)), algebra.literal(0x7fL)),
            algebra.literal(32L),
            signed = true
          ),
          signed
        ),
        32
      )

    /** The sum of the absolute differences of the unsigned `width`-bit elements of two dwords, without the elements
      * whose second element is zero when `masked`.
      */
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    inline def absoluteDifferences(
        a: V,
        b: V,
        width: Int in 1 ~ 64,
        inline masked: Boolean
    ): V = {
      var sum    = algebra.literal(0L)
      var offset = 0
      while (offset < 32) {
        val x          = algebra.field(a, offset, width, signed = false)
        val y          = algebra.field(b, offset, width, signed = false)
        val difference = algebra.absoluteDifference(x, y, width, signed = false)
        sum = algebra.add(
          sum,
          if (masked) algebra.select(algebra.equal(y, algebra.literal(0L)), algebra.literal(0L), difference)
          else difference
        )
        offset += width
      }
      sum
    }

    /** The low `width` bits set, for a runtime `width` below 64. */
    inline def ones(width: V): V =
      algebra.subtract(algebra.shiftLeft(algebra.literal(1L), width), algebra.literal(1L))

    /** The maximum or minimum of three operands read at `width` bits, as `width` bits. */
    inline def three(
        first: V,
        second: V,
        third: V,
        width: Int in 1 ~ 64,
        inline maximum: Boolean,
        inline signed: Boolean
    ): V = algebra.truncate(
      algebra.pick(
        algebra.pick(
          algebra.extend(first, width, signed),
          algebra.extend(second, width, signed),
          maximum,
          signed
        ),
        algebra.extend(third, width, signed),
        maximum,
        signed
      ),
      width
    )

    /** The median of three operands read at `width` bits, as `width` bits. */
    inline def median(
        first: V,
        second: V,
        third: V,
        width: Int in 1 ~ 64,
        inline signed: Boolean
    ): V = algebra.let("x", algebra.extend(first, width, signed))(x =>
      algebra.let("y", algebra.extend(second, width, signed))(y =>
        algebra.let("z", algebra.extend(third, width, signed))(z =>
          algebra.truncate(
            algebra.maximum(
              algebra.minimum(x, y, signed),
              algebra.minimum(algebra.maximum(x, y, signed), z, signed),
              signed
            ),
            width
          )
        )
      )
    )

    inline def pick(
        left: V,
        right: V,
        inline maximum: Boolean,
        inline signed: Boolean
    ): V =
      if (maximum) algebra.maximum(left, right, signed) else algebra.minimum(left, right, signed)

    inline def low24(value: V): V = algebra.and(value, algebra.literal(0xffffffL))

    inline def multiplySigned24(a: V, b: V): V =
      algebra.truncate(algebra.multiply(algebra.signExtend(a, 24), algebra.signExtend(b, 24)), 32)

    inline def shiftLeftHalf(a: V, b: V): V =
      algebra.truncate(algebra.shiftLeft(algebra.truncate(b, 16), algebra.amount(a, 15L)), 16)

    inline def shiftRightArithmeticHalf(a: V, b: V): V =
      algebra.truncate(algebra.shiftRightArithmetic(algebra.signExtend(b, 16), algebra.amount(a, 15L)), 16)

    inline def multiplyAddSigned16(a: V, b: V, c: V): V =
      algebra.add(algebra.multiply(algebra.signExtend(a, 16), algebra.signExtend(b, 16)), algebra.signExtend(c, 16))

    inline def amount(value: V, mask: Long): V = algebra.and(value, algebra.literal(mask))
  }
}

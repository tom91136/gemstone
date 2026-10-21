package net.kurobako.gemstone.isa.amdgpu

import IntegerSemantics.{bitOperation3 as _, permuteBytes as _, scalarBinary64 as _, scalarUnary as _, *}
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*
import references.*

class IntegerSemanticsSuite extends munit.FunSuite {
  private def minMax(operation: ScalarMinMaxOperation, left: Long, right: Long): (Long, Boolean) = {
    val sinks = Values(left & 0xffffffffL, right & 0xffffffffL)
    Values.flagged(
      IntegerSemantics.scalarMinMax(
        Execute,
        operation
      )(sinks, 0, Values.Unused, Values.Unused)
    )
  }

  // The interpreter's SOP2 formulas before staging: the dword it wrote and the SCC it left, from an SCC of `carry`.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def scalarReference(operation: ScalarBinaryOperation, left: Long, right: Long, carry: Boolean)
      : (Long, Boolean) = {
    var scc    = carry
    val result = operation match {
      case ScalarBinaryOperation.And32  => val value = left & right; scc = value != 0L; value
      case ScalarBinaryOperation.Or32   => val value = left | right; scc = value != 0L; value
      case ScalarBinaryOperation.AddF32 =>
        java.lang.Float.floatToRawIntBits(java.lang.Float.intBitsToFloat(left.toInt) +
          java.lang.Float.intBitsToFloat(right.toInt)).toLong
      case ScalarBinaryOperation.SubtractF32 =>
        java.lang.Float.floatToRawIntBits(java.lang.Float.intBitsToFloat(left.toInt) -
          java.lang.Float.intBitsToFloat(right.toInt)).toLong
      case ScalarBinaryOperation.MultiplyF32 =>
        java.lang.Float.floatToRawIntBits(java.lang.Float.intBitsToFloat(left.toInt) *
          java.lang.Float.intBitsToFloat(right.toInt)).toLong
      case ScalarBinaryOperation.MultiplyI32     => left * right
      case ScalarBinaryOperation.MultiplyHighU32 => IntegerArithmetic.multiplyHigh(left, right, 32, false)
      case ScalarBinaryOperation.MultiplyHighI32 => IntegerArithmetic.multiplyHigh(left, right, 32, true)
      case ScalarBinaryOperation.AddI32          =>
        val sum = Bits.low32(left + right).toLong
        scc = ((~(left ^ right) & (left ^ sum)) & 0x80000000L) != 0L
        sum
      case ScalarBinaryOperation.SubtractI32 =>
        val difference = Bits.low32(left - right).toLong
        scc = (((left ^ right) & (left ^ difference)) & 0x80000000L) != 0L
        difference
      case ScalarBinaryOperation.ShiftLeft32 =>
        val value = Bits.low32(left << (right.toInt & 31)).toLong; scc = value != 0L; value
      case ScalarBinaryOperation.ShiftLeftTwoAndAddU32  => val sum = (left << 2) + right; scc = sum > Bits.Mask32; sum
      case ScalarBinaryOperation.ShiftRightArithmetic32 =>
        val value = Bits.unsigned32(left.toInt >> (right.toInt & 31)).toLong; scc = value != 0L; value
      // CDNA3 ISA 12.1: the offset is SRC1[4:0], the width SRC1[22:16], and SCC is set when the field is non-zero
      case ScalarBinaryOperation.BitFieldExtractU32 =>
        val shift = (right & 0x1fL).toInt
        val width = ((right >>> 16) & 0x7fL).toInt
        val value =
          if (width >= 32) Bits.low32(left >>> shift).toLong
          else if (width == 0) 0L
          else Bits.low32((left >>> shift) & ((1L << width) - 1L)).toLong
        scc = value != 0L
        value
      case ScalarBinaryOperation.BitFieldExtractI32 =>
        val shift = (right & 0x1fL).toInt
        val width = ((right >>> 16) & 0x7fL).toInt
        val value =
          if (width >= 32) Bits.unsigned32(left.toInt >> shift).toLong
          else if (width == 0) 0L
          else
            Bits.low32(Bits.signExtend(
              (left.toInt >> shift) & ((1L << width) - 1L),
              width.unsafe[Int in 1 ~ 64]
            )).toLong
        scc = value != 0L
        value
      case ScalarBinaryOperation.ShiftRight32 => val value = left >>> (right.toInt & 31); scc = value != 0L; value
      case ScalarBinaryOperation.AndNot32     => val value = left & ~right; scc = value != 0L; value
      case ScalarBinaryOperation.OrNot32      => val value = Bits.low32(left | ~right).toLong; scc = value != 0L; value
      case ScalarBinaryOperation.Nor32 => val value = Bits.low32(~(left | right)).toLong; scc = value != 0L; value
      case ScalarBinaryOperation.PackLowLow16   => Bits.low16(left).toLong | (Bits.low16(right).toLong << 16)
      case ScalarBinaryOperation.PackLowHigh16  => Bits.low16(left).toLong | (Bits.low16(right >>> 16).toLong << 16)
      case ScalarBinaryOperation.PackHighHigh16 =>
        Bits.low16(left >>> 16).toLong | (Bits.low16(right >>> 16).toLong << 16)
      case ScalarBinaryOperation.AddU32      => val sum = left + right; scc = sum > Bits.Mask32; sum
      case ScalarBinaryOperation.AddCarryU32 =>
        val sum = left + right + (if (scc) 1L else 0L)
        scc = sum > Bits.Mask32
        sum
      case ScalarBinaryOperation.SubtractU32       => scc = left >= right; left - right
      case ScalarBinaryOperation.SubtractBorrowU32 =>
        val borrow = if (scc) 0L else 1L
        scc = left >= right + borrow
        left - right - borrow
      case ScalarBinaryOperation.Xor32  => val value = left ^ right; scc = value != 0L; value
      case ScalarBinaryOperation.Nand32 => val value = Bits.low32(~(left & right)).toLong; scc = value != 0L; value
      case ScalarBinaryOperation.Xnor32 => val value = Bits.low32(~(left ^ right)).toLong; scc = value != 0L; value
      case ScalarBinaryOperation.BitFieldMask32        => ((1L << (left & 31L)) - 1L) << (right & 31L)
      case ScalarBinaryOperation.AbsoluteDifferenceI32 =>
        val value = Bits.low32(math.abs(left.toInt - right.toInt).toLong).toLong; scc = value != 0L; value
      case ScalarBinaryOperation.PackHighLow16         => (left >>> 16) | (Bits.low16(right).toLong << 16)
      case ScalarBinaryOperation.ShiftLeftOneAndAddU32 =>
        val sum = (left << 1) + right; scc = sum > Bits.Mask32; sum
      case ScalarBinaryOperation.ShiftLeftThreeAndAddU32 =>
        val sum = (left << 3) + right; scc = sum > Bits.Mask32; sum
      case ScalarBinaryOperation.ShiftLeftFourAndAddU32 =>
        val sum = (left << 4) + right; scc = sum > Bits.Mask32; sum
    }
    (Bits.low32(result).toLong, scc)
  }

  private val samples: Vector[Long] = {
    val random = scala.util.Random(11)
    Vector(0L, 1L, 2L, 15L, 16L, 31L, 32L, 0x7fffL, 0x8000L, 0xffffL, 0x7fffffffL, 0x80000000L, 0xffffffffL) ++
      Vector.fill(40)(random.nextLong() & 0xffffffffL)
  }

  // The interpreter's lane formulas before staging, kept as the reference.
  private def binaryReference(operation: VectorBinaryOperation, clamp: Boolean, left: Long, right: Long): Long =
    operation match {
      case VectorBinaryOperation.AddU32 =>
        if (clamp) IntegerArithmetic.addSaturated(left, right, 32, false) else Bits.low32(left + right).toLong
      case VectorBinaryOperation.MultiplyLowU32 => Bits.low32(left * right).toLong
      case VectorBinaryOperation.AddI32         =>
        Bits.low32(if (clamp) IntegerArithmetic.addSaturated(left, right, 32, true) else left + right).toLong
      case VectorBinaryOperation.SubtractI32 =>
        Bits.low32(if (clamp) IntegerArithmetic.subtractSaturated(left, right, 32, true) else left - right).toLong
      case VectorBinaryOperation.AddI16 =>
        Bits.low16(if (clamp) IntegerArithmetic.addSaturated(left, right, 16, true) else left + right).toLong
      case VectorBinaryOperation.SubtractI16 =>
        Bits.low16(if (clamp) IntegerArithmetic.subtractSaturated(left, right, 16, true) else left - right).toLong
      case VectorBinaryOperation.MultiplyHighU32 => IntegerArithmetic.multiplyHigh(left, right, 32, false)
      case VectorBinaryOperation.MultiplyHighI32 =>
        Bits.low32(IntegerArithmetic.multiplyHigh(left, right, 32, true)).toLong
      case VectorBinaryOperation.ShiftRightArithmeticI32 => Bits.unsigned32(right.toInt >> (left.toInt & 31)).toLong
      case VectorBinaryOperation.ShiftRightArithmeticI16 =>
        Bits.low16(((Bits.low16Int(right) << 16) >> 16) >> (left.toInt & 15)).toLong
      case VectorBinaryOperation.ShiftLeft32  => Bits.low32(right << (left.toInt & 31)).toLong
      case VectorBinaryOperation.ShiftRight32 => right >>> (left.toInt & 31)
      case VectorBinaryOperation.ShiftRight16 => Bits.low16(right).toLong >>> (left.toInt & 15)
      case VectorBinaryOperation.AddU16       =>
        if (clamp) IntegerArithmetic.addSaturated(left, right, 16, false) else Bits.low16(left + right).toLong
      case VectorBinaryOperation.And32       => left & right
      case VectorBinaryOperation.Or32        => left | right
      case VectorBinaryOperation.Xor32       => left ^ right
      case VectorBinaryOperation.Xnor32      => ~(left ^ right) & Bits.Mask32
      case VectorBinaryOperation.ShiftLeft16 => Bits.low16(Bits.low16(right).toLong << (left.toInt & 15)).toLong
      case VectorBinaryOperation.SubtractU16 =>
        if (clamp) IntegerArithmetic.subtractSaturated(left, right, 16, false) else Bits.low16(left - right).toLong
      case VectorBinaryOperation.SubtractReverseU16 =>
        if (clamp) IntegerArithmetic.subtractSaturated(right, left, 16, false) else Bits.low16(right - left).toLong
      case VectorBinaryOperation.SubtractU32 =>
        if (clamp) IntegerArithmetic.subtractSaturated(left, right, 32, false) else Bits.low32(left - right).toLong
      case VectorBinaryOperation.SubtractReverseU32 =>
        if (clamp) IntegerArithmetic.subtractSaturated(right, left, 32, false)
        else unsignedSubtractReverse32(left, right)
      case VectorBinaryOperation.MultiplyU24     => unsignedMultiply24(left, right)
      case VectorBinaryOperation.MultiplyHighU24 => unsignedMultiplyHigh24(left, right)
      case VectorBinaryOperation.MultiplyI24     => signedMultiply24(left, right)
      case VectorBinaryOperation.MultiplyHighI24 => signedMultiplyHigh24(left, right)
      case VectorBinaryOperation.MultiplyLowU16  => Bits.low16(left * right).toLong
      case VectorBinaryOperation.MinimumU32      => math.min(left, right)
      case VectorBinaryOperation.MaximumU32      => math.max(left, right)
      case VectorBinaryOperation.MinimumI32      => Bits.low32(math.min(left.toInt, right.toInt).toLong).toLong
      case VectorBinaryOperation.MaximumI32      => math.max(left.toInt, right.toInt).toLong & Bits.Mask32
      case VectorBinaryOperation.MinimumI16      => Bits.low16(math.min(signed16(left), signed16(right))).toLong
      case VectorBinaryOperation.MaximumI16      => Bits.low16(math.max(signed16(left), signed16(right))).toLong
      case VectorBinaryOperation.MinimumU16      =>
        Bits.low16(math.min(Bits.low16(left).toLong, Bits.low16(right).toLong)).toLong
      case VectorBinaryOperation.MaximumU16 =>
        Bits.low16(math.max(Bits.low16(left).toLong, Bits.low16(right).toLong)).toLong
      case other => fail(s"$other has no reference")
    }

  private def median(a: Long, b: Long, c: Long): Long = math.max(math.min(a, b), math.min(math.max(a, b), c))

  /** V_MED3 on floats (CDNA3 ISA 12.11): the minimum when any is NaN, otherwise the larger of the two the maximum
    * leaves.
    */
  // the sinks' MODE keeps denormals, which compare as they are, and the operand picked comes back as it is
  private def floatMedian(a: Long, b: Long, c: Long, width: Int in (16, 32, 64)): Long = {
    def value(bits: Long): Double       = IEEE754.toDouble(bits, width)
    def signalling(bits: Long): Boolean = IEEE754.isNaN(bits, width) && (bits & IEEE754.quietBit(width)) == 0L
    def pick(x: Long, y: Long, extremum: (Long, Long, Int in (16, 32, 64)) => Long): Long =
      if (x != y && extremum(x, y, width) == y) y else x
    def smaller(x: Long, y: Long): Long =
      if (signalling(x)) x | IEEE754.quietBit(width)
      else if (signalling(y)) y | IEEE754.quietBit(width)
      else pick(x, y, IEEE754.minNum)
    def larger(x: Long, y: Long): Long = pick(x, y, IEEE754.maxNum)
    if (IEEE754.isNaN(a, width) || IEEE754.isNaN(b, width) || IEEE754.isNaN(c, width))
      smaller(smaller(a, b), c)
    else {
      val largest = value(larger(larger(a, b), c))
      if (largest == value(a)) larger(b, c)
      else if (largest == value(b)) larger(a, c)
      else larger(a, b)
    }
  }

  private def absoluteDifferences(left: Long, right: Long, count: Int, width: Int): Long = {
    val mask = (1L << width) - 1L
    (0 until count).map { index =>
      val shift = index * width
      math.abs(((left >>> shift) & mask) - ((right >>> shift) & mask))
    }.sum
  }

  // The interpreter's lane formulas before staging, before the store's low dword.
  private def ternaryReference(operation: VectorTernaryOperation, clamp: Boolean, left: Long, right: Long, addend: Long)
      : Long = operation match {
    case VectorTernaryOperation.Add3U32     => left + right + addend
    case VectorTernaryOperation.Or3B32      => left | right | addend
    case VectorTernaryOperation.Xor3B32     => left ^ right ^ addend
    case VectorTernaryOperation.Maximum3I32 => math.max(math.max(left.toInt, right.toInt), addend.toInt).toLong
    case VectorTernaryOperation.Maximum3U32 => math.max(math.max(left, right), addend)
    case VectorTernaryOperation.Minimum3I32 => math.min(math.min(left.toInt, right.toInt), addend.toInt).toLong
    case VectorTernaryOperation.Minimum3F32 => IEEE754.minNum(IEEE754.minNum(left, right, 32), addend, 32)
    case VectorTernaryOperation.Maximum3F32 => IEEE754.maxNum(IEEE754.maxNum(left, right, 32), addend, 32)
    case VectorTernaryOperation.Minimum3F16 =>
      IEEE754.minNum(IEEE754.minNum(left & 0xffffL, right & 0xffffL, 16), addend & 0xffffL, 16)
    case VectorTernaryOperation.Maximum3F16 =>
      IEEE754.maxNum(IEEE754.maxNum(left & 0xffffL, right & 0xffffL, 16), addend & 0xffffL, 16)
    case VectorTernaryOperation.Median3F32 =>
      floatMedian(left & 0xffffffffL, right & 0xffffffffL, addend & 0xffffffffL, 32)
    case VectorTernaryOperation.Median3F16        => floatMedian(left & 0xffffL, right & 0xffffL, addend & 0xffffL, 16)
    case VectorTernaryOperation.MaximumMinimumF32 => IEEE754.minNum(IEEE754.maxNum(left, right, 32), addend, 32)
    case VectorTernaryOperation.MinimumMaximumF32 => IEEE754.maxNum(IEEE754.minNum(left, right, 32), addend, 32)
    case VectorTernaryOperation.MaximumMinimumF16 =>
      IEEE754.minNum(IEEE754.maxNum(left & 0xffffL, right & 0xffffL, 16), addend & 0xffffL, 16)
    case VectorTernaryOperation.MinimumMaximumF16 =>
      IEEE754.maxNum(IEEE754.minNum(left & 0xffffL, right & 0xffffL, 16), addend & 0xffffL, 16)
    case VectorTernaryOperation.Minimum3PropagateF32 =>
      IEEE754.minimum(IEEE754.minimum(left, right, 32), addend, 32)
    case VectorTernaryOperation.Maximum3PropagateF32 =>
      IEEE754.maximum(IEEE754.maximum(left, right, 32), addend, 32)
    case VectorTernaryOperation.Minimum3PropagateF16 =>
      IEEE754.minimum(IEEE754.minimum(left & 0xffffL, right & 0xffffL, 16), addend & 0xffffL, 16)
    case VectorTernaryOperation.Maximum3PropagateF16 =>
      IEEE754.maximum(IEEE754.maximum(left & 0xffffL, right & 0xffffL, 16), addend & 0xffffL, 16)
    case VectorTernaryOperation.MaximumMinimumPropagateF32 =>
      IEEE754.minimum(IEEE754.maximum(left, right, 32), addend, 32)
    case VectorTernaryOperation.MinimumMaximumPropagateF32 =>
      IEEE754.maximum(IEEE754.minimum(left, right, 32), addend, 32)
    case VectorTernaryOperation.MaximumMinimumPropagateF16 =>
      IEEE754.minimum(IEEE754.maximum(left & 0xffffL, right & 0xffffL, 16), addend & 0xffffL, 16)
    case VectorTernaryOperation.MinimumMaximumPropagateF16 =>
      IEEE754.maximum(IEEE754.minimum(left & 0xffffL, right & 0xffffL, 16), addend & 0xffffL, 16)
    case VectorTernaryOperation.MaximumMinimumI32 => math.min(math.max(left.toInt, right.toInt), addend.toInt).toLong
    case VectorTernaryOperation.MinimumMaximumI32 => math.max(math.min(left.toInt, right.toInt), addend.toInt).toLong
    case VectorTernaryOperation.MaximumMinimumU32 => math.min(math.max(left, right), addend)
    case VectorTernaryOperation.MinimumMaximumU32 => math.max(math.min(left, right), addend)
    case VectorTernaryOperation.AlignByte32       => ((left << 32) | right) >>> ((addend & 3L) * 8L)
    case VectorTernaryOperation.SumAbsoluteDifferenceU8 =>
      addend + absoluteDifferences(left, right, count = 4, width = 8)
    case VectorTernaryOperation.SumAbsoluteDifferenceHighU8 =>
      addend + (absoluteDifferences(left, right, count = 4, width = 8) << 16)
    case VectorTernaryOperation.SumAbsoluteDifferenceU16 =>
      addend + absoluteDifferences(left, right, count = 2, width = 16)
    case VectorTernaryOperation.SumAbsoluteDifferenceU32      => addend + math.abs(left - right)
    case VectorTernaryOperation.MaskedSumAbsoluteDifferenceU8 =>
      addend + (0 until 4).map { index =>
        val reference = (right >>> (index * 8)) & 0xffL
        if (reference == 0L) 0L else math.abs(((left >>> (index * 8)) & 0xffL) - reference)
      }.sum
    case VectorTernaryOperation.Minimum3U32 => math.min(math.min(left, right), addend)
    case VectorTernaryOperation.Median3I32  => median(left.toInt, right.toInt, addend.toInt)
    case VectorTernaryOperation.Median3U32  => median(left, right, addend)
    case VectorTernaryOperation.Median3I16  =>
      Bits.low16(median(signed16(left), signed16(right), signed16(addend))).toLong
    case VectorTernaryOperation.Median3U16 =>
      Bits.low16(median(Bits.low16(left).toLong, Bits.low16(right).toLong, Bits.low16(addend).toLong)).toLong
    case VectorTernaryOperation.Maximum3I16 =>
      Bits.low16(math.max(math.max(signed16(left), signed16(right)), signed16(addend))).toLong
    case VectorTernaryOperation.Maximum3U16 =>
      Bits.low16(math.max(math.max(Bits.low16(left).toLong, Bits.low16(right).toLong), Bits.low16(addend).toLong))
        .toLong
    case VectorTernaryOperation.Minimum3I16 =>
      Bits.low16(math.min(math.min(signed16(left), signed16(right)), signed16(addend))).toLong
    case VectorTernaryOperation.Minimum3U16 =>
      Bits.low16(math.min(math.min(Bits.low16(left).toLong, Bits.low16(right).toLong), Bits.low16(addend).toLong))
        .toLong
    case VectorTernaryOperation.MultiplyAddU16 | VectorTernaryOperation.MultiplyAddLegacyU16 if clamp =>
      IntegerArithmetic.multiplyAddSaturated(left, right, addend, 16, false)
    case VectorTernaryOperation.MultiplyAddU16 | VectorTernaryOperation.MultiplyAddLegacyU16 =>
      Bits.low16(Bits.low16(left).toLong * Bits.low16(right).toLong + Bits.low16(addend).toLong).toLong
    // a NaN product operand comes back quieted, not as whichever NaN the host's binary64 fma propagates
    case VectorTernaryOperation.FusedMultiplyAddF16 =>
      if (IEEE754.isNaN(left & 0xffffL, 16)) (left & 0xffffL) | 0x200L
      else if (IEEE754.isNaN(right & 0xffffL, 16)) (right & 0xffffL) | 0x200L
      else IEEE754.fusedMultiplyAdd(left, right, addend, 16)
    case VectorTernaryOperation.MultiplyAddU24 if clamp =>
      IntegerArithmetic.multiplyAddSaturated(left & 0xffffffL, right & 0xffffffL, addend, 32, false)
    case VectorTernaryOperation.MultiplyAddU24          => unsignedMultiplyAdd24(left, right, addend)
    case VectorTernaryOperation.MultiplyAddI24 if clamp =>
      IntegerArithmetic.multiplyAddSaturated(Bits.signExtend(left, 24), Bits.signExtend(right, 24), addend, 32, true)
    case VectorTernaryOperation.MultiplyAddI24     => Bits.low32(signedMultiply24(left, right) + addend).toLong
    case VectorTernaryOperation.AlignBit32         => Bits.funnelShiftRight(right, left, addend.toInt & 31)
    case VectorTernaryOperation.BitFieldInsert32   => (left & right) | (~left & addend)
    case VectorTernaryOperation.AndOr32            => (left & right) | addend
    case VectorTernaryOperation.BitFieldExtractI32 =>
      val width = addend.toInt & 31
      if (width == 0) 0L
      else
        Bits.low32(Bits.signExtend(
          (left.toInt >> (right.toInt & 31)) & ((1L << width) - 1L),
          width.unsafe[Int in 1 ~ 64]
        ))
          .toLong
    case VectorTernaryOperation.BitFieldExtractU32 =>
      val width = addend.toInt & 31
      if (width == 0) 0L else Bits.low32(left >>> (right.toInt & 31)).toLong & ((1L << width) - 1L)
    case VectorTernaryOperation.XorAddU32                  => Bits.low32((left ^ right) + addend).toLong
    case VectorTernaryOperation.AddShiftLeftU32            => (left + right) << (addend.toInt & 31)
    case VectorTernaryOperation.ShiftLeftAddU32            => (left << (right.toInt & 31)) + addend
    case VectorTernaryOperation.ShiftLeftOr32              => (left << (right.toInt & 31)) | addend
    case VectorTernaryOperation.PermuteBytes32             => permuteBytes(left, right, addend)
    case VectorTernaryOperation.MultiplyAddU32U16 if clamp =>
      IntegerArithmetic.multiplyAddSaturated(left & 0xffffL, right & 0xffffL, addend, 32, false)
    case VectorTernaryOperation.MultiplyAddU32U16          => (left & 0xffffL) * (right & 0xffffL) + addend
    case VectorTernaryOperation.MultiplyAddI32I16 if clamp =>
      IntegerArithmetic.multiplyAddSaturated(signed16(left), signed16(right), addend, 32, true)
    case VectorTernaryOperation.MultiplyAddI32I16       => signed16(left) * signed16(right) + addend
    case VectorTernaryOperation.MultiplyAddI16 if clamp =>
      Bits.low16(IntegerArithmetic.multiplyAddSaturated(left, right, addend, 16, true)).toLong
    case VectorTernaryOperation.MultiplyAddI16 =>
      Bits.low16(signed16(left) * signed16(right) + signed16(addend)).toLong
    // binary16 multiply-add with the product rounded first; each step returns its first NaN operand quieted
    case VectorTernaryOperation.MultiplyAddF16 =>
      def step(x: Long, y: Long)(operation: (Long, Long) => Long): Long =
        if (IEEE754.isNaN(x, 16)) x | 0x200L else if (IEEE754.isNaN(y, 16)) y | 0x200L else operation(x, y)
      val product = step(left & 0xffffL, right & 0xffffL)(IEEE754.multiply(_, _, 16, IEEE754.NaNOperand.Left))
      references.clampF16(step(product, addend & 0xffffL)(IEEE754.add(_, _, 16, IEEE754.NaNOperand.Left)), clamp)
    case VectorTernaryOperation.DivisionFixupF16 => divisionFixupF16(left & 0xffffL, right & 0xffffL, addend & 0xffffL)
    case VectorTernaryOperation.ShiftRightPackI8 | VectorTernaryOperation.ShiftRightPackU8 =>
      val (low, high) = if (operation == VectorTernaryOperation.ShiftRightPackI8) (-128, 127) else (0, 255)
      def packed(value: Long): Long = ((value.toInt >> (addend.toInt & 31)).max(low).min(high) & 0xff).toLong
      packed(left) | (packed(right) << 8)
    case other => fail(s"$other has no reference")
  }

  /** V_DIV_FIXUP_F16: a NaN numerator, else a NaN denominator, quieted; the negative default NaN for 0/0 and inf/inf;
    * infinity for a zero denominator, an infinite numerator or a NaN quotient, and zero for an infinite denominator, a
    * zero numerator or exponents 25 apart the denominator's way, signed by both; otherwise the quotient's magnitude
    * with that sign.
    */
  private def divisionFixupF16(quotient: Long, denominator: Long, numerator: Long): Long = {
    val (q, d, n) =
      (IEEE754.toDouble(quotient, 16), IEEE754.toDouble(denominator, 16), IEEE754.toDouble(numerator, 16))
    val sign = (numerator ^ denominator) & 0x8000L
    if (n.isNaN) numerator | 0x200L
    else if (d.isNaN) denominator | 0x200L
    else if ((n.isInfinite && d.isInfinite) || (n == 0.0 && d == 0.0)) 0xfe00L
    else if (d == 0.0 || n.isInfinite) sign | 0x7c00L
    else if (d.isInfinite || n == 0.0) sign
    else if (((numerator >>> 10) & 0x1fL) - ((denominator >>> 10) & 0x1fL) < -25) sign
    else if (q.isNaN) sign | 0x7c00L
    else sign | (quotient & 0x7fffL)
  }

  // The interpreter's SDWA lane formulas before staging.
  private def subDwordReference(operation: VectorSubDwordOperation, left: Long, right: Long): Long = operation match {
    case VectorSubDwordOperation.AddU32 => Bits.low32(left + right).toLong
    case VectorSubDwordOperation.AddU16 => Bits.low16(left + right).toLong
    case VectorSubDwordOperation.AddF16 =>
      IEEE754.floatToBinary16(IEEE754.binary16ToFloat(Bits.low16(left)) + IEEE754.binary16ToFloat(Bits.low16(right)))
        .toLong
    case VectorSubDwordOperation.And32        => left & right
    case VectorSubDwordOperation.ShiftLeft16  => Bits.low16(Bits.low16(right).toLong << (left.toInt & 15)).toLong
    case VectorSubDwordOperation.ShiftLeft32  => Bits.low32(right << (left.toInt & 31)).toLong
    case VectorSubDwordOperation.ShiftRight32 => right >>> (left.toInt & 31)
    case VectorSubDwordOperation.ShiftRight16 => Bits.low16(right).toLong >>> (left.toInt & 15)
    case VectorSubDwordOperation.MaximumI16   => Bits.low16(math.max(signed16(left), signed16(right))).toLong
    case VectorSubDwordOperation.MaximumF16   => IEEE754.maxNum(left, right, 16)
    case VectorSubDwordOperation.MinimumI16   => Bits.low16(math.min(signed16(left), signed16(right))).toLong
    case VectorSubDwordOperation.MinimumF16   => IEEE754.minNum(left, right, 16)
    case VectorSubDwordOperation.MinimumU16   =>
      Bits.low16(math.min(Bits.low16(left).toLong, Bits.low16(right).toLong)).toLong
    case VectorSubDwordOperation.MultiplyLowU16 => Bits.low16(left * right).toLong
    case VectorSubDwordOperation.MultiplyF16    =>
      IEEE754.floatToBinary16(IEEE754.binary16ToFloat(Bits.low16(left)) * IEEE754.binary16ToFloat(Bits.low16(right)))
        .toLong
    case VectorSubDwordOperation.MultiplyU24        => unsignedMultiply24(left, right)
    case VectorSubDwordOperation.MultiplyI24        => signedMultiply24(left, right)
    case VectorSubDwordOperation.Or32               => left | right
    case VectorSubDwordOperation.Xor32              => left ^ right
    case VectorSubDwordOperation.SubtractU16        => Bits.low16(left - right).toLong
    case VectorSubDwordOperation.SubtractReverseU16 => Bits.low16(right - left).toLong
    case VectorSubDwordOperation.SubtractU32        => Bits.low32(left - right).toLong
    case VectorSubDwordOperation.SubtractReverseU32 => Bits.low32(right - left).toLong
    case other                                      => fail(s"$other has no reference")
  }

  test("staged SDWA operations match the interpreter's formulas, sign-extended operands included") {
    val operands = samples ++ samples.map(Bits.signExtend(_, 16)) ++ samples.map(Bits.signExtend(_, 8))
    val zeros    = Values(0L, 0L)
    val ones     = (-1L, -1L)
    for {
      operation <- VectorSubDwordOperation.values.toVector
      if IntegerSemantics.vectorSubDword(Execute, Values.subDword(operation))(zeros)(ones)._2 != -1L
      a <- operands
      b <- operands
    } {
      val sinks  = Values(a, b)
      val staged =
        IntegerSemantics.vectorSubDword(Execute, Values.subDword(operation))(sinks)(fail("unstaged"))._1
      val reference = subDwordReference(operation, a, b)
      // the JVM leaves the payload of a NaN from NaN operands unspecified
      if (
        !(IEEE754.isNaN(staged & 0xffffL, 16) && IEEE754.isNaN(reference & 0xffffL, 16) &&
          operation.toString.endsWith("F16"))
      )
        assertEquals(staged, reference, s"$operation a=$a b=$b")
    }
  }

  // The interpreter's packed half formula before staging; clamp saturates integers and clamps floats to [0, 1].
  private def packedReference(operation: VectorPackedBinaryOperation, a: Long, b: Long, clamp: Boolean): Long = {
    def unsigned(value: Long): Long = Bits.low16(if (clamp) value.max(0L).min(0xffffL) else value).toLong
    def signed(value: Long): Long   = Bits.low16(if (clamp) value.max(-0x8000L).min(0x7fffL) else value).toLong
    def half(value: Float): Long    =
      Bits.low16(IEEE754.floatToBinary16(if (clamp) references.clampF32(value) else value).toLong).toLong
    def float(bits: Long): Float = IEEE754.binary16ToFloat(Bits.low16(bits))
    def unit(bits: Long): Long   = references.clampF16(bits, clamp)
    operation match {
      case VectorPackedBinaryOperation.AddU16              => unsigned(a + b)
      case VectorPackedBinaryOperation.AddI16              => signed(signed16(a) + signed16(b))
      case VectorPackedBinaryOperation.AddF16              => half(float(a) + float(b))
      case VectorPackedBinaryOperation.MultiplyLowU16      => unsigned(a * b)
      case VectorPackedBinaryOperation.MultiplyF16         => half(float(a) * float(b))
      case VectorPackedBinaryOperation.MaximumF16          => unit(IEEE754.maxNum(a, b, 16))
      case VectorPackedBinaryOperation.MinimumF16          => unit(IEEE754.minNum(a, b, 16))
      case VectorPackedBinaryOperation.MaximumPropagateF16 => unit(IEEE754.maximum(a, b, 16))
      case VectorPackedBinaryOperation.MinimumPropagateF16 => unit(IEEE754.minimum(a, b, 16))
      case VectorPackedBinaryOperation.MaximumI16          =>
        if (signed16(a) >= signed16(b)) Bits.low16(a).toLong else Bits.low16(b).toLong
      case VectorPackedBinaryOperation.MinimumI16 =>
        if (signed16(a) <= signed16(b)) Bits.low16(a).toLong else Bits.low16(b).toLong
      case VectorPackedBinaryOperation.MaximumU16             => math.max(Bits.low16(a).toLong, Bits.low16(b).toLong)
      case VectorPackedBinaryOperation.MinimumU16             => math.min(Bits.low16(a).toLong, Bits.low16(b).toLong)
      case VectorPackedBinaryOperation.SubtractI16            => signed(signed16(a) - signed16(b))
      case VectorPackedBinaryOperation.SubtractU16            => unsigned(a - b)
      case VectorPackedBinaryOperation.ShiftLeft16            => Bits.low16(b << (a.toInt & 15)).toLong
      case VectorPackedBinaryOperation.ShiftRight16           => Bits.low16(b >>> (a.toInt & 15)).toLong
      case VectorPackedBinaryOperation.ShiftRightArithmetic16 => Bits.low16(signed16(b) >> (a.toInt & 15)).toLong
    }
  }

  test("staged packed operations match the interpreter's formulas on every half") {
    val halves = samples.map(_ & 0xffffL).distinct
    for {
      operation <- VectorPackedBinaryOperation.values.toVector
      clamp     <- Vector(false, true)
      a         <- halves
      b         <- halves
    } {
      val sinks     = Values(a, b)
      val staged    = IntegerSemantics.vectorPacked(Execute, Values.packed(operation, clamp))(sinks)._1
      val reference = packedReference(operation, a, b, clamp)
      if (!(IEEE754.isNaN(staged, 16) && IEEE754.isNaN(reference, 16) && operation.toString.endsWith("F16")))
        assertEquals(staged, reference, s"$operation clamp=$clamp a=$a b=$b")
    }
  }

  test("scalar min/max SCC reports whether source zero won") {
    assertEquals(minMax(ScalarMinMaxOperation.MinimumU32, left = 3L, right = 4L), (3L, true))
    assertEquals(minMax(ScalarMinMaxOperation.MinimumU32, left = 4L, right = 3L), (3L, false))
    assertEquals(minMax(ScalarMinMaxOperation.MaximumU32, left = 4L, right = 3L), (4L, true))
    assertEquals(minMax(ScalarMinMaxOperation.MaximumU32, left = 3L, right = 4L), (4L, false))
    assertEquals(
      minMax(ScalarMinMaxOperation.MinimumI32, left = 0xffffffffL, right = 1L),
      (0xffffffffL, true)
    )
    assertEquals(
      minMax(ScalarMinMaxOperation.MaximumI32, left = 0xffffffffL, right = 1L),
      (1L, false)
    )
    assertEquals(minMax(ScalarMinMaxOperation.MinimumU32, left = 7L, right = 7L), (7L, false))
    assertEquals(minMax(ScalarMinMaxOperation.MaximumI32, left = 7L, right = 7L), (7L, false))
  }

  test("staged scalar operations match the interpreter's formulas and SCC") {
    val operands = samples ++ Vector(0x003f0000L, 0x00200004L, 0x0010001fL, 0x0008003fL)
    for {
      operation <- ScalarBinaryOperation.values.toVector
      carry     <- Vector(false, true)
      a         <- operands
      b         <- operands
    } {
      val sinks        = Values(a, b, if (carry) 1L else 0L)
      val (value, scc) = Values.flagged(
        IntegerSemantics.scalarBinary(
          Execute,
          operation
        )(sinks, 0, Values.Unused, Values.Unused)
      )
      val reference = scalarReference(operation, a, b, carry)
      // the JVM leaves the payload of a NaN from NaN operands unspecified
      if (!(operation.toString.endsWith("F32") && IEEE754.isNaN(value, 32) && IEEE754.isNaN(reference._1, 32)))
        assertEquals((Bits.low32(value).toLong, scc), reference, s"$operation a=$a b=$b scc=$carry")
    }
  }

  test("flagged scalar operations render both results") {
    val sinks = Values.render
    assertEquals(
      AMDGPUStaged.staged(
        Render,
        Instruction.SBinary(ScalarBinaryOperation.AddCarryU32, 0, Values.operand(1), Values.operand(2))
      )(sinks)(None).fold(fail("unstaged"))(_.show),
      "result = zext32((a + b) + scc)\nflag = 0xffffffff <s ((a + b) + scc)\nd = result\nscc = flag"
    )
  }

  test("staged integer comparisons match the interpreter's for every kind and relation") {
    val operands = samples ++ samples.map(Bits.signExtend(_, 16)) ++ Vector(Long.MinValue, Long.MaxValue, -1L)
    for {
      kind <- Vector(
        VectorComparisonKind.Signed16,
        VectorComparisonKind.Signed32,
        VectorComparisonKind.Unsigned16,
        VectorComparisonKind.Unsigned32,
        VectorComparisonKind.Signed64,
        VectorComparisonKind.Unsigned64
      )
      relation <- VectorComparisonRelation.values.toVector
      a        <- operands
      b        <- operands
    } {
      val (test, swap) = (relation.integerTest, relation.integerSwapped)
      val sinks        = Values(a, b)
      val staged       = IntegerSemantics.compareInteger(
        Execute,
        kind,
        test,
        swap
      )(sinks, 0, Values.Unused, Values.Unused, None, false)(
        fail("unstaged")
      )._1
      assertEquals(
        staged != 0L,
        compare(VectorComparison(kind, relation, updateExec = false), a, b),
        s"$kind $relation $a $b"
      )
    }
  }

  test("staged vector binaries match the interpreter's formulas") {
    val zeros = Values(0L, 0L)
    val ones  = (-1L, -1L)
    for {
      operation <- VectorBinaryOperation.values.toVector
      clamp     <- Vector(false, true)
      staged = IntegerSemantics.vectorBinary(
        Execute,
        operation,
        clamp
      )(zeros, 0, Values.Unused, Values.Unused)(ones)._2 != -1L
      if staged
      a <- samples
      b <- samples
      sinks = Values(a, b)
    } assertEquals(
      IntegerSemantics.vectorBinary(
        Execute,
        operation,
        clamp
      )(sinks, 0, Values.Unused, Values.Unused)(fail("unstaged"))._1,
      binaryReference(operation, clamp, a, b),
      s"$operation clamp=$clamp a=$a b=$b"
    )
  }

  test("staged vector ternaries match the interpreter's formulas") {
    val inputs = samples.take(24)
    for {
      operation <-
        VectorTernaryOperation.values.toVector.filterNot(_ == VectorTernaryOperation.DivisionFusedMultiplyAddF32)
      clamp <- Vector(false, true)
      a     <- inputs
      b     <- inputs
      c     <- inputs
      sinks = Values(a, b, c)
    } assertEquals(
      Bits.low32(IntegerSemantics.vectorTernary(Execute, operation, clamp)(
        sinks,
        0,
        Values.Unused,
        Values.Unused,
        Values.Unused,
        SourceModifiers.Unmodified
      )(fail("unstaged"))._1).toLong,
      Bits.low32(ternaryReference(operation, clamp, a, b, c)).toLong,
      s"$operation clamp=$clamp a=$a b=$b c=$c"
    )
  }
  private def show(operation: VectorBinaryOperation, clamp: Boolean = false)(using munit.Location): String = {
    val sinks = Values.render
    IntegerSemantics.vectorBinary(Render, operation, clamp)(sinks, 0, Values.vector(1), Values.vector(2))(
      fail(s"$operation has no semantics")
    ).fold(fail(s"$operation has no semantics"))(Values.shown)
  }

  test("vector integer operations render as pseudo-code") {
    assertEquals(show(VectorBinaryOperation.AddU32), "zext32(a + b)")
    assertEquals(
      show(VectorBinaryOperation.AddU32, clamp = true),
      "min.s(zext32(a) + zext32(b), 0xffffffff)"
    )
    assertEquals(show(VectorBinaryOperation.ShiftLeft32), "zext32(b << (a & 31))")
    assertEquals(show(VectorBinaryOperation.MultiplyHighI24), "zext32((sext24(a) * sext24(b)) >>s 32)")
    assertEquals(show(VectorBinaryOperation.MinimumI16), "zext16(min.s(sext16(a), sext16(b)))")
  }

  test("operations without semantics fall through") {
    val sinks = Values.render
    assertEquals(
      IntegerSemantics.vectorBinary(Render, VectorBinaryOperation.AddF32, clamp = false)(
        sinks,
        0,
        Values.vector(1),
        Values.vector(2)
      )(Some(Term.Steps(Term.Step("fallback", Term.Variable("fallback"))))),
      Some(Term.Steps(Term.Step("fallback", Term.Variable("fallback"))))
    )
  }

  private val wide: Vector[Long] = {
    val random = scala.util.Random(29)
    samples ++ Vector(-1L, Long.MinValue, Long.MaxValue, 0x8000000000000000L + 5L, 0xffffffff00000000L) ++
      Vector.fill(30)(random.nextLong())
  }

  test("staged dword unary operations match the interpreter's formulas") {
    for (a <- wide) {
      assertEquals(Execute.moved(a), Bits.low32(a).toLong)
      assertEquals(Execute.moved64(a), a)
      assertEquals(Execute.complement(a), Bits.low32(~a).toLong)
      assertEquals(Execute.reversed(a), Bits.reverseBits(a, 32))
      for (operation <- VectorBitFindOperation.values.toVector) {
        val value     = Bits.signed32(a)
        val reference = operation match {
          case VectorBitFindOperation.FirstBitLow32 =>
            if (value == 0) -1L else Integer.numberOfTrailingZeros(value).toLong
          case VectorBitFindOperation.FirstBitHighI32 => signedFirstBitHigh32(value)
          case VectorBitFindOperation.FirstBitHighU32 => firstBitHigh32(value)
        }
        val sinks = Values(a)
        assertEquals(
          IntegerSemantics.vectorBitFind(Execute, operation)(sinks, 0, Values.Unused)._1,
          Bits.unsigned32(reference.toInt).toLong,
          s"$operation $a"
        )
      }
    }
  }

  test("staged selects, bit counts and three-input truth tables match the interpreter's formulas") {
    val random = scala.util.Random(31)
    for {
      a <- wide.take(40)
      b <- wide.take(40)
    } {
      for (condition <- Vector(0L, 1L))
        assertEquals(
          Execute.conditional(a, b, condition),
          Bits.low32(if (condition != 0L) b else a).toLong
        )
      assertEquals(
        Execute.bitCount(Bits.low32(a).toLong, Bits.low32(b).toLong),
        Bits.low32(java.lang.Integer.bitCount(Bits.low32(a).toInt).toLong + Bits.low32(b).toLong).toLong
      )
    }
    for {
      table <- 0 until 256
      half  <- Vector(false, true)
      _     <- 0 until 8
    } {
      val (a, b, c) = (wide(random.nextInt(wide.size)), wide(random.nextInt(wide.size)), random.nextLong())
      val sinks     = Values(a, b, c)
      assertEquals(
        IntegerSemantics.bitOperation3(
          Execute,
          half
        )(sinks, 0, Values.Unused, Values.Unused, Values.Unused, table)._1,
        bitOperation3(a, b, c, table, half),
        s"table=$table half=$half"
      )
    }
    for {
      lane <- 0 until 64
      high <- Vector(false, true)
      _    <- 0 until 20
    } {
      val (mask, base) = (random.nextLong(), wide(random.nextInt(wide.size)))
      val sinks        = Values(mask, lane.toLong, base)
      assertEquals(
        IntegerSemantics.maskBitCount(
          Execute,
          high
        )(sinks, 0, Values.Unused, Values.Unused)._1,
        mbCount(mask, lane, high, base),
        s"lane=$lane high=$high"
      )
    }
  }

  // The interpreter's carry formulas before staging: the result and the carry or borrow out.
  private def carryReference(subtract: Boolean, clamp: Boolean, left: Long, right: Long, carryIn: Boolean)
      : (Long, Long) =
    if (subtract) {
      val difference = IntegerArithmetic.subtract32(left, right, carryIn)
      val borrow     = IntegerArithmetic.borrow32(difference)
      (if (clamp && borrow) 0L else Bits.low32(difference).toLong, if (borrow) 1L else 0L)
    } else {
      val sum      = IntegerArithmetic.add32(left, right, carryIn)
      val overflow = IntegerArithmetic.carry32(sum)
      (if (clamp && overflow) Bits.Mask32 else Bits.low32(sum).toLong, if (overflow) 1L else 0L)
    }

  test("staged carry operations match the interpreter's formulas in every form") {
    for {
      a       <- samples
      b       <- samples
      carryIn <- Vector(0L, 1L)
    } {
      val operands = Values(a, b, carryIn)
      for (
        operation <- Vector(
          VectorBinaryOperation.AddCarryU32,
          VectorBinaryOperation.AddWithCarryU32,
          VectorBinaryOperation.SubtractCarryU32,
          VectorBinaryOperation.SubtractWithBorrowU32
        )
      ) {
        val chained = operation == VectorBinaryOperation.AddWithCarryU32 ||
          operation == VectorBinaryOperation.SubtractWithBorrowU32
        val subtract = operation == VectorBinaryOperation.SubtractCarryU32 ||
          operation == VectorBinaryOperation.SubtractWithBorrowU32
        assertEquals(
          IntegerSemantics.vectorCarry(Execute, operation)(operands, 0, Values.Unused, Values.Unused),
          carryReference(subtract, clamp = false, a, b, chained && carryIn != 0L),
          s"$operation $a $b $carryIn"
        )
      }
      for {
        chained <- Vector(false, true)
        clamp   <- Vector(false, true)
      } {
        assertEquals(
          (
            Execute.carrySum(a, b, carryIn, chained, clamp),
            Execute.carryOut(a, b, carryIn, chained)
          ),
          carryReference(subtract = false, clamp, a, b, chained && carryIn != 0L)
        )
        assertEquals(
          (
            Execute.borrowDifference(a, b, carryIn, chained, clamp),
            Execute.borrowOut(a, b, carryIn, chained)
          ),
          carryReference(subtract = true, clamp, a, b, chained && carryIn != 0L)
        )
      }
      // SDWA: the condition mask selects on the VCC bit; the reversed forms swap the operands
      assertEquals(
        IntegerSemantics.vectorSubDwordFlagged(Execute, Values.subDword(VectorSubDwordOperation.ConditionalMask32))(
          operands
        ),
        (if (carryIn != 0L) b else a, carryIn)
      )
      assertEquals(
        IntegerSemantics.vectorSubDwordFlagged(
          Execute,
          Values.subDword(VectorSubDwordOperation.SubtractReverseWithBorrowU32)
        )(operands),
        carryReference(subtract = true, clamp = false, b, a, carryIn != 0L)
      )
      assertEquals(
        IntegerSemantics.vectorSubDwordFlagged(
          Execute,
          Values.subDword(VectorSubDwordOperation.SubtractReverseCarryU32)
        )(
          operands
        ),
        carryReference(subtract = true, clamp = false, b, a, false)
      )
    }
  }

  test("staged 64-bit multiply-adds match the interpreter's formulas and carry") {
    val random = scala.util.Random(37)
    for {
      signed <- Vector(false, true)
      clamp  <- Vector(false, true)
      a      <- wide.take(30)
      b      <- wide.take(30)
    } {
      val c       = wide(random.nextInt(wide.size))
      val left    = if (signed) Bits.signExtend(Bits.low32(a).toLong, 32) else Bits.low32(a).toLong
      val right   = if (signed) Bits.signExtend(Bits.low32(b).toLong, 32) else Bits.low32(b).toLong
      val product = left * right
      val sum     = product + c
      val result  =
        if (clamp) IntegerArithmetic.multiplyAddSaturated(left, right, c, 64, signed) else sum
      val sinks = Values(a, b, c)
      assertEquals(
        Values.flagged(
          IntegerSemantics.multiplyAdd64(
            Execute,
            signed,
            clamp
          )(sinks, 0, 0, Values.Unused, Values.Unused, Values.Unused)
        ),
        (result, java.lang.Long.compareUnsigned(sum, product) < 0),
        s"signed=$signed clamp=$clamp $a $b $c"
      )
    }
  }

  test("staged 64-bit shifts match the interpreter's formulas") {
    for {
      value  <- wide
      amount <- samples
    } {
      assertEquals(Execute.shiftLeft64(value, amount), value << (amount.toInt & 63))
      assertEquals(Execute.shiftRight64(value, amount), value >>> (amount.toInt & 63))
      assertEquals(
        Execute.shiftRightArithmetic64(value, amount),
        value >> (amount.toInt & 63)
      )
      val count = amount.toInt & 7
      assertEquals(
        Execute.shiftLeftAdd64(value, amount, value ^ amount),
        (value << (if (count <= 4) count else 0)) + (value ^ amount)
      )
    }
  }

  test("staged SOP1 operations match the interpreter's formulas and SCC") {
    for {
      operation <- ScalarUnaryOperation.values.toVector
      source    <- wide
      previous  <- Vector(0L, 0xffffffffL, 0x12345678L)
    } {
      val read         = if (IntegerSemantics.scalarUnaryReadsPair(operation)) source else Bits.low32(source).toLong
      val (value, scc) =
        Values.flagged(IntegerSemantics.scalarUnary(Execute, operation)(Values(read, previous), 0, Values.Unused))
      val reference = scalarUnary(operation, source, previous)
      val written   = if (IntegerSemantics.scalarUnaryWritesPair(operation)) value else Bits.low32(value).toLong
      assertEquals(
        written,
        if (IntegerSemantics.scalarUnaryWritesPair(operation)) reference else Bits.low32(reference).toLong,
        s"$operation $source"
      )
      assertEquals(scc, scalarUnarySetsCarry(operation) && reference != 0L, s"$operation $source")
    }
  }

  test("staged 64-bit SOP2 operations, selects and immediates match the interpreter's formulas and SCC") {
    val fields = Vector(0x003f0000L, 0x00200004L, 0x0010001fL, 0x0008003fL, 0x0040000aL, 0x003f003fL)
    for {
      operation <- ScalarBinary64Operation.values.toVector
      flags = operation match {
        case ScalarBinary64Operation.Multiply | ScalarBinary64Operation.AddNoCarry |
            ScalarBinary64Operation.SubtractNoCarry | ScalarBinary64Operation.BitFieldMask => false
        case _ => true
      }
      a <- wide
      b <- wide.take(30) ++ fields
    } {
      val reference = scalarBinary64(operation, a, b)
      val sinks     = Values(a, b)
      assertEquals(
        Values.flagged(IntegerSemantics.scalarBinary64(
          Execute,
          operation
        )(sinks, 0, Values.Unused, Values.Unused)),
        (reference, if (flags) reference != 0L else false),
        s"$operation $a $b"
      )
    }
    for {
      a <- wide.take(20)
      b <- wide.take(20)
      operands = Values(a, b)
      scc <- Vector(0L, 1L)
    } {
      assertEquals(Execute.scalarSelect(a, b, scc), if (scc != 0L) a else b)
      for (operation <- SaveExecOperation.values.toVector) {
        val result = operation match {
          case SaveExecOperation.And    => a & b
          case SaveExecOperation.AndNot => a & ~b
          case SaveExecOperation.Or     => a | b
          case SaveExecOperation.Xor    => a ^ b
          case SaveExecOperation.OrNot  => a | ~b
          case SaveExecOperation.Nand   => ~(a & b)
          case SaveExecOperation.Nor    => ~(a | b)
          case SaveExecOperation.Xnor   => ~(a ^ b)
          case SaveExecOperation.NotAnd => ~a & b
          case SaveExecOperation.NotOr  => ~a | b
        }
        assertEquals(
          Values.flagged(IntegerSemantics.saveExec(Execute, operation)(operands, 0, Values.Unused, 64)),
          (result, result != 0L)
        )
      }
    }
    for {
      register  <- samples
      immediate <- Vector(0, 1, -1, 0x300, -0x8000, 0x7fff, 2, 3)
    } {
      assertEquals(
        Values.flagged(IntegerSemantics.multiplyImmediate(Execute)(Values(register), 0, immediate.toLong)),
        signedMultiplyImmediate(register, immediate)
      )
      val sum = register + immediate.toLong
      assertEquals(
        Values.flagged(IntegerSemantics.addImmediate(Execute)(Values(register), 0, immediate.toLong)),
        (Bits.low32(sum).toLong, sum > Bits.Mask32 || sum < 0L)
      )
    }
    for {
      a <- wide
      b <- Vector(a, a ^ 1L, a ^ Long.MinValue, 0L)
    } {
      val sinks = Values(a, b)
      assertEquals(
        IntegerSemantics.scalarCompare(
          Execute,
          ScalarCompareOperation.EqualU64
        )(sinks, Values.Unused, Values.Unused)._1,
        if (a == b) 1L else 0L
      )
      assertEquals(
        IntegerSemantics.scalarCompare(
          Execute,
          ScalarCompareOperation.NotEqualU64
        )(sinks, Values.Unused, Values.Unused)._1,
        if (a != b) 1L else 0L
      )
    }
  }

  test("staged lane selection matches the interpreter's DPP, swizzle, permute and lane formulas") {
    val random = scala.util.Random(41)
    for {
      control <- 0 until 0x170
      lane    <- 0 until 64
    } assertEquals(
      Execute.dppSource(control, lane.toLong),
      dppSourceLane(lane, control).toLong,
      s"control=0x${control.toHexString} lane=$lane"
    )
    for {
      _    <- 0 until 200
      lane <- 0 until 64
    } {
      val selectors = random.nextInt(1 << 24)
      assertEquals(
        Execute.dpp8Source(selectors.toLong, lane.toLong),
        dpp8SourceLane(lane, selectors).toLong
      )
      val swizzle = random.nextInt(1 << 16)
      assertEquals(
        Execute.swizzleSource(swizzle, lane.toLong),
        swizzleSourceLane(lane, swizzle).toLong
      )
      val (address, offset) = (random.nextLong() & 0xffffffffL, random.nextInt(1 << 16).toLong)
      assertEquals(
        Execute.addressedLane(address, offset, 64L),
        bpermuteSourceLane(address, offset).toLong
      )
      for (waveSize <- Vector(32, 64))
        assertEquals(
          Execute.addressedLane(address, offset, waveSize.toLong),
          permuteDestinationLane(address, offset, waveSize).toLong
        )
      val selected = random.nextLong()
      assertEquals(
        Execute.selectedLane(selected, 32L),
        (selected.toInt & 31).toLong
      )
    }
  }

  test("staged memory address and data computations match the interpreter's formulas") {
    val random = scala.util.Random(43)
    for (_ <- 0 until 2000) {
      val (base, offset, other) = (random.nextLong(), random.nextLong(), random.nextLong())
      val displacement          = random.nextInt().toLong
      assertEquals(
        Execute.offsetAddress(base, offset, displacement),
        base + Bits.low32(offset).toLong + displacement
      )
      assertEquals(Execute.displaced(base, displacement), base + displacement)
      assertEquals(
        Execute.dwordAddress(base, offset, displacement),
        Bits.low32(base).toLong + Bits.low32(offset).toLong + displacement
      )
      for (waveSize <- Vector(32L, 64L))
        assertEquals(
          Execute.interleavedAddress(base, offset, waveSize),
          base + Bits.low32(offset).toLong / waveSize
        )
      assertEquals(
        Execute.scalarAddress(base, displacement, offset),
        base + displacement + offset
      )
      for (bytes <- Vector(1, 2, 4, 8))
        assertEquals(
          Execute.loaded(base, (bytes * 8).unsafe[Int in 1 ~ 64], signed = true),
          Bits.signExtend(base, (bytes * 8).unsafe[Int in 1 ~ 64])
        )
      assertEquals(Execute.high16(base), (base >>> 16) & 0xffffL)
      for (high <- Vector(false, true)) {
        assertEquals(
          Execute.mergeHalf(base, offset, high),
          mergeD16(base, Bits.low16(offset).toLong, high)
        )
        assertEquals(
          Execute.mergeHalf(base, offset, high),
          if (high) (base & 0xffffL) | (Bits.low16(offset).toLong << 16)
          else (base & 0xffff0000L) | Bits.low16(offset).toLong
        )
      }
      for (comparand <- Vector(base, other))
        assertEquals(
          Execute.compareSwap(base, comparand, other),
          if (base == comparand) other else base
        )
    }
  }
}

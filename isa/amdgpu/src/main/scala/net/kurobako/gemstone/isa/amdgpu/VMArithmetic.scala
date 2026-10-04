package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import VMState.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*

/** Pure arithmetic and lane-selection semantics shared by instruction execution. */
private object VMArithmetic {
  // The instruction's nonnegative left shift is masked to 53 bits, so only this tail of 2/pi contributes.
  private inline val TrigPreopBits = 0x856033046fc7bL

  def trigPreopF64(value: Double, segment: Int): Double = {
    val exponent = ((IEEE754.doubleToBinary64(value) >>> 52) & 0x7ffL).toInt
    val shift    = (segment & 0x1f) * 53 + (exponent - 1077).max(0)
    val chunk    = if (shift >= 53) 0L else (TrigPreopBits << shift) & 0x1fffffffffffffL
    val scale    = -53 - shift + (if (exponent >= 1968) 128 else 0)
    Math.scalb(chunk.toDouble, scale)
  }

  def unsignedMultiplyHigh32(left: Long, right: Long): Long =
    Bits.high32(Bits.low32(left).toLong * Bits.low32(right).toLong).toLong
  def signedMultiplyHigh32(left: Long, right: Long): Long =
    Bits.high32(Bits.signed32(left).toLong * Bits.signed32(right).toLong).toLong
  def unsignedMultiplyHigh24(left: Long, right: Long): Long =
    Bits.high32((left & 0xffffffL) * (right & 0xffffffL)).toLong
  def signedMultiplyImmediate(register: Long, immediate: Int): (Long, Boolean) = {
    val product  = register.toInt.toLong * immediate.toLong
    val overflow = product < Int.MinValue.toLong || product > Int.MaxValue.toLong
    (Bits.low32(product).toLong, overflow)
  }
  def unsignedSubtract32(left: Long, right: Long): (Long, Boolean) = {
    val unsignedLeft  = Bits.low32(left).toLong
    val unsignedRight = Bits.low32(right).toLong
    // AMD names the VCC result "carry", but V_SUB*_CO exposes the borrow
    // bit: it is set when the unsigned minuend is smaller than the
    // subtrahend.  V_SUBB then consumes that bit as its borrow-in.
    (Bits.low32(unsignedLeft - unsignedRight).toLong, unsignedLeft < unsignedRight)
  }
  def unsignedSubtractWithBorrow32(left: Long, right: Long, borrowIn: Boolean): (Long, Boolean) = {
    val unsignedLeft  = Bits.low32(left).toLong
    val unsignedRight = Bits.low32(right).toLong + (if (borrowIn) 1L else 0L)
    (Bits.low32(unsignedLeft - unsignedRight).toLong, unsignedLeft < unsignedRight)
  }
  def unsignedMultiply24(left: Long, right: Long): Long =
    Bits.low32((left & 0xffffffL) * (right & 0xffffffL)).toLong
  def unsignedMultiplyAdd24(left: Long, right: Long, addend: Long): Long =
    Bits.low32(unsignedMultiply24(left, right) + Bits.low32(addend).toLong).toLong
  def unsignedSubtractReverse32(left: Long, right: Long): Long =
    Bits.low32(Bits.low32(right).toLong - Bits.low32(left).toLong).toLong
  def unsignedMin32(left: Long, right: Long): Long =
    math.min(Bits.low32(left).toLong, Bits.low32(right).toLong)
  def scalarMinMax(
      operation: ScalarMinMaxOperation,
      left: Long,
      right: Long
  ): (Long, Boolean) = {
    val left32  = Bits.low32(left).toLong
    val right32 = Bits.low32(right).toLong
    operation match {
      case ScalarMinMaxOperation.MinimumU32 => (math.min(left32, right32), left32 < right32)
      case ScalarMinMaxOperation.MaximumU32 => (math.max(left32, right32), left32 > right32)
      case ScalarMinMaxOperation.MinimumI32 =>
        (Bits.low32(math.min(left32.toInt, right32.toInt).toLong).toLong, left32.toInt < right32.toInt)
      case ScalarMinMaxOperation.MaximumI32 =>
        (Bits.low32(math.max(left32.toInt, right32.toInt).toLong).toLong, left32.toInt > right32.toInt)
    }
  }
  def mergeD16(existing: Long, value: Long, high: Boolean): Long =
    if (high) (Bits.low32(existing).toLong & 0x0000ffffL) | (Bits.low16(value).toLong << 16)
    else (Bits.low32(existing).toLong & 0xffff0000L) | Bits.low16(value).toLong
  def andn2SaveExecMask(previous: Long, source: Long): Long =
    // SAVEEXEC keeps the explicit source as ANDN2's first operand and the
    // previous EXEC mask as its second operand: source & ~previous.
    source & ~previous
  def signedFirstBitHigh32(value: Long): Long = {
    val signed = Bits.signed32(value)
    if (signed == 0 || signed == -1) 0xffffffffL
    else if (signed >= 0) Integer.numberOfLeadingZeros(signed).toLong
    else Integer.numberOfLeadingZeros(~signed).toLong
  }
  def signedFirstBitHigh64(value: Long): Long =
    if (value == 0L || value == -1L) 0xffffffffL
    else java.lang.Long.numberOfLeadingZeros(if (value >= 0L) value else ~value).toLong

  def firstBitHigh32(value: Long): Long = {
    val unsigned = Bits.signed32(value)
    if (unsigned == 0) 0xffffffffL else Integer.numberOfLeadingZeros(unsigned).toLong
  }
  def floatClassMask(value: Float): Long = {
    val bits     = IEEE754.floatToBinary32(value).toLong
    val exponent = (bits >>> 23) & 0xff
    val fraction = bits & 0x7fffff
    val negative = (bits & 0x80000000L) != 0L
    val classBit =
      if (exponent == 0xff && fraction != 0) if ((fraction & 0x400000) == 0) 0 else 1
      else if (exponent == 0xff) if (negative) 2 else 9
      else if (exponent == 0 && fraction == 0) if (negative) 5 else 6
      else if (exponent == 0) if (negative) 4 else 7
      else if (negative) 3 else 8
    1L << classBit
  }
  def minimumNumberF32(left: Float, right: Float): Float =
    if (left.isNaN) right else if (right.isNaN) left else math.min(left, right)
  def maximumNumberF32(left: Float, right: Float): Float =
    if (left.isNaN) right else if (right.isNaN) left else math.max(left, right)
  def minimumNumberF64(left: Double, right: Double): Double =
    if (left.isNaN) right else if (right.isNaN) left else math.min(left, right)
  def maximumNumberF64(left: Double, right: Double): Double =
    if (left.isNaN) right else if (right.isNaN) left else math.max(left, right)
  def clampF32(value: Float): Float =
    if (value.isNaN) value else math.max(0.0f, math.min(1.0f, value))
  def trigF32(operation: VectorConvertOperation, value: Float): Float = operation match {
    // AMD's scalarized sin/cos instructions use turns, not radians: one
    // instruction unit is 2*pi radians.  HIP kernels commonly multiply
    // radians by 1/(2*pi) before reaching these instructions.
    case VectorConvertOperation.SineF32   => math.sin(2.0 * math.Pi * value.toDouble).toFloat
    case VectorConvertOperation.CosineF32 => math.cos(2.0 * math.Pi * value.toDouble).toFloat
    case _ => throw IllegalArgumentException(s"$operation is not a trigonometric operation")
  }
  def packedFmaF16(
      srcA: Long,
      srcB: Long,
      srcC: Long,
      lowSelection: Int,
      highSelection: Int,
      negateLow: Int,
      negateHigh: Int,
      clamp: Boolean
  ): Long = {
    def half(source: Long, high: Boolean): Float =
      IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(if (high) source >>> 16 else source)))
    def calculate(selection: Int, negate: Int): Long = {
      def operand(source: Long, index: Int): Float = {
        val value = half(source, (selection & (1 << index)) != 0)
        if ((negate & (1 << index)) != 0) -value else value
      }
      val result  = Math.fma(operand(srcA, 0), operand(srcB, 1), operand(srcC, 2))
      val clamped = if (clamp) clampF32(result) else result
      IEEE754.floatToBinary16(clamped).toLong
    }
    calculate(lowSelection, negateLow) | (calculate(highSelection, negateHigh) << 16)
  }
  private def mixedOperand(
      source: Long,
      index: Int,
      selection: Int,
      selectionHi: Int,
      negate: Int,
      absolute: Int
  ): Float = {
    val convertFromHalf = (selectionHi & (1 << index)) != 0
    val selectHigh      = (selection & (1 << index)) != 0
    val value           = if (convertFromHalf)
      IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(if (selectHigh) source >>> 16 else source)))
    else IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(source)))
    val magnitude = if ((absolute & (1 << index)) != 0) math.abs(value) else value
    if ((negate & (1 << index)) != 0) -magnitude else magnitude
  }
  def fmaMixF16(
      existing: Long,
      srcA: Long,
      srcB: Long,
      srcC: Long,
      opSelection: Int,
      opSelectionHi: Int,
      negate: Int,
      absolute: Int,
      clamp: Boolean,
      high: Boolean
  ): Long = {
    val rounded = IEEE754.floatToBinary16(Math.fma(
      mixedOperand(srcA, 0, opSelection, opSelectionHi, negate, absolute),
      mixedOperand(srcB, 1, opSelection, opSelectionHi, negate, absolute),
      mixedOperand(srcC, 2, opSelection, opSelectionHi, negate, absolute)
    ))
    val value =
      if (clamp) {
        val converted = IEEE754.binary16ToFloat(rounded)
        IEEE754.floatToBinary16(math.max(0.0f, math.min(1.0f, converted)))
      } else rounded
    mergeD16(existing, value.toLong, high)
  }
  def fmaMixF32(
      srcA: Long,
      srcB: Long,
      srcC: Long,
      opSelection: Int,
      opSelectionHi: Int,
      negate: Int,
      absolute: Int,
      clamp: Boolean
  ): Long = {
    val result = Math.fma(
      mixedOperand(srcA, 0, opSelection, opSelectionHi, negate, absolute),
      mixedOperand(srcB, 1, opSelection, opSelectionHi, negate, absolute),
      mixedOperand(srcC, 2, opSelection, opSelectionHi, negate, absolute)
    )
    IEEE754.floatToBinary32(if (clamp) clampF32(result) else result).toLong
  }
  def dot2F32F16(left: Long, right: Long, accumulator: Long, accumulate: Boolean): Binary32 = {
    val leftLow   = IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left)))
    val leftHigh  = IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left >>> 16)))
    val rightLow  = IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right)))
    val rightHigh = IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right >>> 16)))
    val products  = Math.fma(leftLow, rightLow, leftHigh * rightHigh)
    val result    =
      if (accumulate) products + IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(accumulator))) else products
    IEEE754.floatToBinary32(result)
  }
  // Four packed lanes are accumulated without creating an intermediate collection.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def dot4U32U8(left: Long, right: Long, accumulator: Long): Long = {
    var result = Bits.low32(accumulator).toLong
    var index  = 0
    while (index < 4) {
      val leftByte  = (left >>> (index * 8)) & 0xffL
      val rightByte = (right >>> (index * 8)) & 0xffL
      result = Bits.low32(result + leftByte * rightByte).toLong
      index += 1
    }
    result
  }

  /** AMD's unsigned float-to-dword conversion returns the truncated magnitude for negative inputs.
    *
    * LLVM uses this defined device behavior while lowering signed float-to-64-bit conversions: it restores the sign
    * with a subsequent XOR/subtract sequence. Converting a negative JVM Float directly to Long would instead produce
    * the two's-complement low dword, which breaks that sequence (for example, -3.0f would feed 0xfffffffd instead of
    * 3).
    */
  def unsignedFloatToInt32(value: Float): Long =
    if (value.isNaN || value <= 0.0f) 0L
    else if (value.toDouble >= 4294967295.0) 0xffffffffL
    else value.toLong
  def doubleClassMask(value: Double): Long = {
    val bits     = IEEE754.doubleToBinary64(value)
    val exponent = (bits >>> 52) & 0x7ffL
    val fraction = bits & 0x000fffffffffffffL
    val negative = bits < 0L
    val classBit =
      if (exponent == 0x7ffL && fraction != 0L) if ((fraction & 0x0008000000000000L) == 0L) 0 else 1
      else if (exponent == 0x7ffL) if (negative) 2 else 9
      else if (exponent == 0L && fraction == 0L) if (negative) 5 else 6
      else if (exponent == 0L) if (negative) 4 else 7
      else if (negative) 3 else 8
    1L << classBit
  }
  def signedMultiply24(left: Long, right: Long): Long =
    Bits.low32(Bits.signExtend(left, 24) * Bits.signExtend(right, 24)).toLong
  def signedMultiplyHigh24(left: Long, right: Long): Long =
    Bits.low32((Bits.signExtend(left, 24) * Bits.signExtend(right, 24)) >> 32).toLong
  def divisionFixupF32(quotient: Float, denominator: Float, numerator: Float): Float = {
    val negative =
      ((java.lang.Float.floatToRawIntBits(numerator) ^
        java.lang.Float.floatToRawIntBits(denominator)) & 0x80000000) != 0
    val sign = if (negative) -1.0f else 1.0f
    if (
      numerator.isNaN || denominator.isNaN || (numerator.isInfinite && denominator.isInfinite) ||
      (numerator == 0.0f && denominator == 0.0f)
    ) Float.NaN
    else if (denominator == 0.0f || numerator.isInfinite) Math.copySign(Float.PositiveInfinity, sign)
    else if (denominator.isInfinite || numerator == 0.0f) Math.copySign(0.0f, sign)
    else if (quotient.isNaN) numerator / denominator
    else Math.copySign(math.abs(quotient), sign)
  }

  def fmaF32(
      operation: VectorMadF32Operation,
      leftBits: Long,
      rightBits: Long,
      addendBits: Long
  ): Binary32 = {
    val left   = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(leftBits)))
    val right  = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(rightBits)))
    val addend = IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(addendBits)))
    val result = operation match {
      case VectorMadF32Operation.FusedMultiplyAdd => Math.fma(left, right, addend)
      case VectorMadF32Operation.MultiplyAdd      => left * right + addend
    }
    IEEE754.floatToBinary32(result)
  }
  def minimumF16(left: Long, right: Long): Binary16 =
    IEEE754.floatToBinary16(
      math.min(
        IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))),
        IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right)))
      )
    )
  private def comparisonResult(
      relation: VectorComparisonRelation,
      less: Boolean,
      equal: Boolean,
      greater: Boolean,
      unordered: Boolean
  ): Boolean = relation match {
    case VectorComparisonRelation.Equal             => equal
    case VectorComparisonRelation.OrderedNotEqual   => less || greater
    case VectorComparisonRelation.NotEqual          => !equal
    case VectorComparisonRelation.Less              => less
    case VectorComparisonRelation.LessOrEqual       => less || equal
    case VectorComparisonRelation.Greater           => greater
    case VectorComparisonRelation.GreaterOrEqual    => greater || equal
    case VectorComparisonRelation.NotLess           => !less
    case VectorComparisonRelation.NotLessOrEqual    => !(less || equal)
    case VectorComparisonRelation.NotGreater        => !greater
    case VectorComparisonRelation.NotGreaterOrEqual => !(greater || equal)
    case VectorComparisonRelation.Ordered           => !unordered
    case VectorComparisonRelation.Unordered         => unordered
    case VectorComparisonRelation.NotLessOrGreater  => !less && !greater
    case VectorComparisonRelation.Class             => false
  }

  def compare(operation: VectorComparison, left: Long, right: Long): Boolean = {
    def integer(leftValue: Long, rightValue: Long): Boolean = {
      val ordering = java.lang.Long.compare(leftValue, rightValue)
      comparisonResult(
        relation = operation.relation,
        less = ordering < 0,
        equal = ordering == 0,
        greater = ordering > 0,
        unordered = false
      )
    }
    def unsigned(leftValue: Long, rightValue: Long): Boolean = {
      val ordering = java.lang.Long.compareUnsigned(leftValue, rightValue)
      comparisonResult(
        relation = operation.relation,
        less = ordering < 0,
        equal = ordering == 0,
        greater = ordering > 0,
        unordered = false
      )
    }
    def floating(leftValue: Double, rightValue: Double): Boolean =
      comparisonResult(
        relation = operation.relation,
        less = leftValue < rightValue,
        equal = leftValue == rightValue,
        greater = leftValue > rightValue,
        unordered = leftValue.isNaN || rightValue.isNaN
      )

    operation.kind match {
      case VectorComparisonKind.Signed16 =>
        integer(
          Bits.signExtend(Bits.low16(left).toLong, 16),
          Bits.signExtend(Bits.low16(right).toLong, 16)
        )
      case VectorComparisonKind.Signed32 =>
        integer(
          Bits.signExtend(Bits.low32(left).toLong, 32),
          Bits.signExtend(Bits.low32(right).toLong, 32)
        )
      case VectorComparisonKind.Signed64   => integer(left, right)
      case VectorComparisonKind.Unsigned16 => unsigned(Bits.low16(left).toLong, Bits.low16(right).toLong)
      case VectorComparisonKind.Unsigned32 => unsigned(Bits.low32(left).toLong, Bits.low32(right).toLong)
      case VectorComparisonKind.Unsigned64 => unsigned(left, right)
      case VectorComparisonKind.Float16    =>
        floating(
          IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(left))).toDouble,
          IEEE754.binary16ToFloat(Binary16.fromLow(Bits.low16(right))).toDouble
        )
      case VectorComparisonKind.Float32 =>
        floating(
          IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left))).toDouble,
          IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(right))).toDouble
        )
      case VectorComparisonKind.Float64 =>
        floating(IEEE754.binary64ToDouble(left), IEEE754.binary64ToDouble(right))
      case VectorComparisonKind.ClassFloat32 =>
        (floatClassMask(IEEE754.binary32ToFloat(Binary32.fromLow(Bits.low32(left)))) & Bits.low32(right).toLong) != 0L
      case VectorComparisonKind.ClassFloat64 =>
        (doubleClassMask(IEEE754.binary64ToDouble(left)) & Bits.low32(right).toLong) != 0L
    }
  }
  def floatingLiteralToDouble(raw: Long): Double =
    IEEE754.binary64ToDouble(Bits.join32(0L, Bits.low32(raw).toLong))
  def bpermuteSourceLane(address: Long): Lane =
    bpermuteSourceLane(address, 0L)
  def bpermuteSourceLane(address: Long, offset: Long): Lane = (((address + offset) >>> 2) & 63L).toInt.unsafe[Lane]
  def permuteDestinationLane(address: Long, offset: Long, waveSize: Int in (32, 64)): Lane =
    (((address + offset) >>> 2) & (waveSize.raw - 1L)).toInt.unsafe[Lane]
  def dppQuadSourceLane(lane: Lane, control: Int): Lane =
    ((lane.raw & ~3) | ((control >>> ((lane.raw & 3) * 2)) & 3)).unsafe[Lane]
  def dppSourceLane(lane: Lane, control: Int): Option[Lane] = {
    val selected: Option[Int] =
      if (control <= 0xff) Some(dppQuadSourceLane(lane, control).raw)
      else if (control >= 0x100 && control <= 0x10f) {
        val source = (lane.raw & 15) + (control & 15)
        Option.when(source < 16)((lane.raw & ~15) | source)
      } else if (control >= 0x110 && control <= 0x11f) {
        val source = (lane.raw & 15) - (control & 15)
        Option.when(source >= 0)((lane.raw & ~15) | source)
      } else if (control >= 0x121 && control <= 0x12f) {
        val rowBase = lane.raw & ~15
        Some(rowBase | (((lane.raw & 15) - (control & 15)) & 15))
      } else control match {
        case 0x130 => Option.when(lane.raw < 63)(lane.raw + 1)
        case 0x134 => Some((lane.raw + 1) & 63)
        case 0x138 => Option.when(lane.raw > 0)(lane.raw - 1)
        case 0x13c => Some((lane.raw - 1) & 63)
        case 0x142 => Option.when(((lane.raw / 16) & 1) == 1)((lane.raw / 16 - 1) * 16 + 15)
        case 0x143 => Option.when(lane.raw / 16 >= 2)(31)
        case _     => None
      }
    selected.map(_.unsafe[Lane])
  }
  def dppLaneEnabled(lane: Lane, rowMask: Int, bankMask: Int): Boolean =
    (rowMask & (1 << (lane.raw / 16))) != 0 && (bankMask & (1 << (lane.raw & 3))) != 0
  def waveConditionMask(mask: Long, lanes: Int in 1 ~ 64): Long =
    if (lanes.raw <= 32) Bits.low32(mask).toLong else mask
  def mbCount(mask: Long, executionMask: Long, lane: Lane, high: Boolean, base: Long): Long = {
    val laneInHalf = lane.raw & 31
    val belowLane  = if (laneInHalf == 0) 0L else (1L << laneInHalf) - 1L
    val relevant   =
      if (high) {
        if (lane.raw < 32) 0L else Bits.high32(executionMask).toLong & belowLane
      } else {
        val belowLow = if (lane.raw < 32) belowLane else Bits.Mask32
        executionMask & belowLow
      }
    Bits.low32(base + java.lang.Integer.bitCount((mask & relevant).toInt).toLong).toLong
  }

  def selectSubDword(value: Long, selection: SubDwordSelection, signExtend: Boolean): Long = {
    val mask     = Bits.mask(selection.width)
    val selected = (value >>> selection.shift.raw) & mask
    if (signExtend) Bits.signExtend(selected, selection.width) else selected
  }

  def placeSubDword(
      existing: Long,
      value: Long,
      selection: SubDwordSelection,
      unused: SubDwordDestinationUnused
  ): Long = {
    val mask     = Bits.mask(selection.width) << selection.shift.raw
    val selected = (value << selection.shift.raw) & mask
    unused match {
      case SubDwordDestinationUnused.Pad        => Bits.low32(selected).toLong
      case SubDwordDestinationUnused.Preserve   => Bits.low32((existing & ~mask) | selected).toLong
      case SubDwordDestinationUnused.SignExtend =>
        val endMask =
          if (selection.upperBit.raw == 32) Bits.Mask32
          else Bits.mask(selection.upperBit)
        val upper =
          if ((selected & (1L << (selection.upperBit.raw - 1))) != 0L) Bits.Mask32 & ~endMask
          else 0L
        Bits.low32(selected | upper).toLong
    }
  }

  def permuteBytes(srcA: Long, srcB: Long, selectors: Long): Long = {
    val sources = Bits.join32(srcB, srcA)
    (0 until 4).foldLeft(0L) { (result, byte) =>
      // V_PERM uses the full four-bit selector.  Values 0..7 address the
      // concatenated second/first sources; selectors 8..15 produce zero
      // (in particular, 0xc is the canonical zero selector used by LLVM).
      val selector = ((selectors >>> (byte * 8)) & 0xfL).toInt
      if (selector < 8)
        result | (((sources >>> (selector * 8)) & 0xffL) << (byte * 8))
      else result
    }
  }

}

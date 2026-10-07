package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

/** Lane-independent NVIDIA arithmetic on raw register bits. */
object VMArithmetic {

  /** The encodings NVIDIA arithmetic produces for every binary16, bfloat16 and binary32 NaN result. */
  val CanonicalNaN16: Long = 0x7fffL
  val CanonicalNaN32: Long = 0x7fffffffL

  def integerUnaryResult(
      operation: IntegerUnaryOperation,
      value: Long,
      width: ScalarBitWidth
  ): Long = {
    val result = operation match {
      case IntegerUnaryOperation.Negate           => -value
      case IntegerUnaryOperation.Absolute         => Math.abs(signExtend(truncate(value, width), width))
      case IntegerUnaryOperation.BitwiseNot       => ~value
      case IntegerUnaryOperation.LogicalNot       => if (truncate(value, width) == 0L) 1L else 0L
      case IntegerUnaryOperation.PopulationCount  => bitCount(value, width).raw.toLong
      case IntegerUnaryOperation.LeadingZeroCount =>
        if (width.raw == 32) Integer.numberOfLeadingZeros(Bits.signed32(value)).toLong
        else java.lang.Long.numberOfLeadingZeros(value).toLong
      case IntegerUnaryOperation.BitReverse => reverseBits(value, width)
    }
    truncate(result, width)
  }

  // The wrap form takes the shift modulo 32; the clamp form saturates it at 32,
  // so a clamp of 32 selects the opposite half of the 64-bit pair rather than
  // leaving the input unchanged.
  def funnelShiftResult(low: Long, high: Long, amount: Long, left: Boolean, clamp: Boolean): Long = {
    val shift = if (clamp) Math.min(amount & 0xffffffffL, 32L).toInt else (amount & 31L).toInt
    if (left) Bits.funnelShiftLeft(low, high, shift) else Bits.funnelShiftRight(low, high, shift)
  }

  private def saturatingSum(left: Long, right: Long, width: ScalarBitWidth, subtract: Boolean): Long = {
    val a      = signExtend(left, width)
    val b      = signExtend(right, width)
    val result = if (subtract) a - b else a + b
    if (width.raw < 64) {
      val maximum = (1L << (width.raw - 1)) - 1L
      math.max(-maximum - 1L, math.min(maximum, result))
    } else if (((a ^ result) & ((if (subtract) ~b else b) ^ result)) < 0) {
      if (a < 0) Long.MinValue else Long.MaxValue
    } else result
  }

  def integerBinaryResult(
      operation: IntegerOperation,
      left: Long,
      right: Long,
      width: ScalarBitWidth
  ): Long = {
    val result = operation match {
      case IntegerOperation.Add                    => left + right
      case IntegerOperation.Subtract               => left - right
      case IntegerOperation.AddSaturated           => saturatingSum(left, right, width, subtract = false)
      case IntegerOperation.SubtractSaturated      => saturatingSum(left, right, width, subtract = true)
      case IntegerOperation.BitwiseOr              => left | right
      case IntegerOperation.BitwiseAnd             => left & right
      case IntegerOperation.BitwiseXor             => left ^ right
      case IntegerOperation.MultiplyLow            => left * right
      case IntegerOperation.MultiplyHigh(signed)   => IntegerArithmetic.multiplyHigh(left, right, width, signed)
      case IntegerOperation.Divide(signed)         => divideInteger(left, right, width, signed)
      case IntegerOperation.Remainder(signed)      => remainderInteger(left, right, width, signed)
      case IntegerOperation.MultiplyWideUnsigned16 => Bits.low16(left).toLong * Bits.low16(right).toLong
      case IntegerOperation.MultiplyWideSigned16   =>
        signExtend(
          left,
          16
        ) * signExtend(right, 16)
      case IntegerOperation.MultiplyWideUnsigned32 => low32(left).toLong * low32(right).toLong
      case IntegerOperation.MultiplyWideSigned32   => left.toInt.toLong * right.toInt.toLong
      case IntegerOperation.Minimum(signed)        =>
        if (signed) Math.min(signExtend(left, width), signExtend(right, width))
        else if (width.raw == 64) {
          if (java.lang.Long.compareUnsigned(left, right) <= 0) left else right
        } else Math.min(truncate(left, width), truncate(right, width))
      case IntegerOperation.Maximum(signed) =>
        if (signed) Math.max(signExtend(left, width), signExtend(right, width))
        else if (width.raw == 64) {
          if (java.lang.Long.compareUnsigned(left, right) >= 0) left else right
        } else Math.max(truncate(left, width), truncate(right, width))
      case IntegerOperation.ShiftLeft =>
        if ((right & 0xffffffffL) >= width.raw) 0L else left << right.toInt
      case IntegerOperation.ShiftRightLogical =>
        if ((right & 0xffffffffL) >= width.raw) 0L else truncate(left, width) >>> right.toInt
      case IntegerOperation.ShiftRightArithmetic =>
        signExtend(left, width) >> Math.min(right & 0xffffffffL, width.raw.toLong - 1L).toInt
    }
    truncate(result, width)
  }

  def flushFloat32(bits: Long, enabled: Boolean): Long =
    if (enabled && (bits & 0x7f800000L) == 0) bits & 0x80000000L else bits

  def floatInput(bits: Long, width: Int in (16, 32, 64), control: FloatControl): Long = {
    val exponent = width.raw match {
      case 16 => 0x7c00L
      case 32 => 0x7f800000L
      case _  => 0x7ff0000000000000L
    }
    if (control.flushSubnormal && (bits & exponent) == 0) bits & (1L << (width.raw - 1)) else bits
  }

  def floatResult(bits: Long, width: Int in (16, 32, 64), control: FloatControl): Long = {
    val bounded = if (!control.saturate) bits
    else {
      val value = IEEE754.toDouble(bits, width)
      if (value.isNaN || value <= 0.0) 0L
      else if (value >= 1.0) IEEE754.fromDouble(1.0, width)
      else bits
    }
    floatInput(bounded, width, control)
  }

  def canonical(bits: Long, width: Int in (16, 32, 64)): Long =
    if (width.raw == 64 || !IEEE754.isNaN(bits, width)) bits
    else if (width.raw == 16) CanonicalNaN16
    else CanonicalNaN32

  private def canonicalBfloat16(bits: Long): Long = if ((bits & 0x7fffL) > 0x7f80L) CanonicalNaN16 else bits

  private def bfloat16Bits(value: Double, rounding: IEEE754.Rounding = IEEE754.Rounding.NearestEven): Long =
    canonicalBfloat16(IEEE754.doubleToBfloat16(value, rounding).toLong)

  private def bfloat16Value(bits: Long): Double = IEEE754.bfloat16ToFloat(Binary16.low(bits)).toDouble

  private def floatExtremum(
      operation: FloatOperation,
      leftBits: Long,
      rightBits: Long,
      width: Int in (16, 32, 64),
      control: FloatControl
  ): Long = {
    val sign      = 1L << (width.raw - 1)
    val a         = if (control.absolute) leftBits & ~sign else leftBits
    val b         = if (control.absolute) rightBits & ~sign else rightBits
    val propagate = control.propagateNaN || operation == FloatOperation.Minimum || operation == FloatOperation.Maximum
    val result    = operation match {
      case FloatOperation.Minimum | FloatOperation.MinimumNumber =>
        if (propagate) IEEE754.minimum(a, b, width) else IEEE754.minNum(a, b, width)
      case _ => if (propagate) IEEE754.maximum(a, b, width) else IEEE754.maxNum(a, b, width)
    }
    if (IEEE754.isNaN(result, width)) canonical(result, width)
    else if (control.xorSign) (result & ~sign) | ((leftBits ^ rightBits) & sign)
    else result
  }

  def floatBinaryBits(
      operation: FloatOperation,
      leftBits: Long,
      rightBits: Long,
      width: Int in (16, 32, 64),
      control: FloatControl
  ): Long = {
    val sign = 1L << (width.raw - 1)
    operation match {
      case FloatOperation.Minimum | FloatOperation.Maximum | FloatOperation.MinimumNumber |
          FloatOperation.MaximumNumber =>
        floatExtremum(operation, leftBits, rightBits, width, control)
      case FloatOperation.CopySign => truncate((rightBits & ~sign) | (leftBits & sign), width)
      case FloatOperation.Add      => canonical(IEEE754.add(leftBits, rightBits, width), width)
      case FloatOperation.Subtract => canonical(IEEE754.add(leftBits, rightBits ^ sign, width), width)
      case FloatOperation.Multiply => canonical(IEEE754.multiply(leftBits, rightBits, width), width)
      case FloatOperation.Divide   =>
        val quotient = if (width.raw == 32)
          IEEE754.floatToBinary32(IEEE754.binary32ToFloat(low32(leftBits)) / IEEE754.binary32ToFloat(low32(rightBits)))
            .toLong
        else IEEE754.fromDouble(IEEE754.toDouble(leftBits, width) / IEEE754.toDouble(rightBits, width), width)
        canonical(quotient, width)
    }
  }

  def floatUnaryBits(operation: FloatUnaryOperation, bits: Long, width: Int in (16, 32, 64)): Long = {
    val sign                                    = 1L << (width.raw - 1)
    def rounded(result: Double => Double): Long =
      canonical(IEEE754.fromDouble(result(IEEE754.toDouble(bits, width)), width), width)
    operation match {
      case FloatUnaryOperation.Negate               => truncate(bits ^ sign, width)
      case FloatUnaryOperation.Absolute             => truncate(bits & ~sign, width)
      case FloatUnaryOperation.Reciprocal           => rounded(1.0 / _)
      case FloatUnaryOperation.SquareRoot           => rounded(Math.sqrt(_))
      case FloatUnaryOperation.ReciprocalSquareRoot => rounded(value => 1.0 / Math.sqrt(value))
      case FloatUnaryOperation.Exponent2            => rounded(Math.pow(2.0, _))
      case FloatUnaryOperation.Log2                 => rounded(value => Math.log(value) / Math.log(2.0))
      case FloatUnaryOperation.Sine                 => rounded(Math.sin(_))
      case FloatUnaryOperation.Cosine               => rounded(Math.cos(_))
      case FloatUnaryOperation.Tanh                 => rounded(Math.tanh(_))
    }
  }

  def roundingMode(rounding: Option[ConversionRounding]): IEEE754.Rounding = rounding match {
    case Some(ConversionRounding.RP | ConversionRounding.RPI) => IEEE754.Rounding.Up
    case Some(ConversionRounding.RM | ConversionRounding.RMI) => IEEE754.Rounding.Down
    case Some(ConversionRounding.RZ | ConversionRounding.RZI) => IEEE754.Rounding.TowardZero
    case _                                                    => IEEE754.Rounding.NearestEven
  }

  def packedBinaryBits(
      operation: FloatOperation,
      left: Long,
      right: Long,
      format: PackedFloatFormat,
      control: FloatControl
  ): Long = {
    def element(a: Long, b: Long, width: Int in (16, 32, 64)): Long =
      floatResult(
        floatBinaryBits(operation, floatInput(a, width, control), floatInput(b, width, control), width, control),
        width,
        control
      )
    format match {
      case PackedFloatFormat.F32x2 => element(low32(left).toLong, low32(right).toLong, 32) |
          (element(left >>> 32, right >>> 32, 32) << 32)
      case PackedFloatFormat.F16x2 => element(truncate(left, 16), truncate(right, 16), 16) |
          (element(truncate(left >>> 16, 16), truncate(right >>> 16, 16), 16) << 16)
      case PackedFloatFormat.BF16x2 =>
        // rounding through binary32 is innocuous: it carries more than twice the bfloat16 precision
        def lane(a: Long, b: Long): Long =
          bfloat16Bits(IEEE754.binary32ToFloat(low32(element(
            truncate(a, 16) << 16,
            truncate(b, 16) << 16,
            32
          ))).toDouble)
        lane(left, right) | (lane(left >>> 16, right >>> 16) << 16)
    }
  }

  def bfloat16FusedMultiplyAdd(left: Long, right: Long, addend: Long, saturate: Boolean): Long = {
    val result = Math.fma(bfloat16Value(left), bfloat16Value(right), bfloat16Value(addend))
    bfloat16Bits(if (!saturate) result else if (result.isNaN || result <= 0.0) 0.0 else math.min(result, 1.0))
  }

  def packedFmaBits(left: Long, right: Long, addend: Long, bf16: Boolean, saturate: Boolean): Long = {
    def lane(a: Long, b: Long, c: Long): Long =
      if (bf16) bfloat16FusedMultiplyAdd(a, b, c, saturate)
      else floatResult(canonical(IEEE754.fusedMultiplyAdd(a, b, c, 16), 16), 16, FloatControl(saturate = saturate))
    lane(left, right, addend) | (lane(left >>> 16, right >>> 16, addend >>> 16) << 16)
  }

  def loadBits(value: Long, width: ScalarBitWidth, signed: Boolean): Long = {
    val bits = truncate(value, width)
    if (signed) signExtend(bits, width) else bits
  }

  def bitFind(
      value: Long,
      width: ScalarBitWidth,
      signed: Boolean,
      shiftAmount: Boolean
  ): Long = {
    val bits   = truncate(value, width)
    val search =
      if (signed && ((bits >>> (width.raw - 1)) & 1L) != 0L) truncate(~bits, width)
      else bits
    val position =
      if (search == 0L) -1
      else if (width.raw == 32) 31 - Integer.numberOfLeadingZeros(search.toInt)
      else 63 - java.lang.Long.numberOfLeadingZeros(search)
    if (position < 0) 0xffffffffL
    else if (shiftAmount) (width.raw - 1 - position).toLong
    else position.toLong
  }

  // Fixed-width packed dot product is a measured arithmetic hot loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def dotProduct4(
      left: Long,
      right: Long,
      addend: Long,
      leftSigned: Boolean,
      rightSigned: Boolean
  ): Long = {
    var result = signExtend(
      truncate(addend, 32),
      32
    )
    var index = 0
    while (index < 4) {
      val leftByte   = (left >>> (index * 8)) & 0xffL
      val rightByte  = (right >>> (index * 8)) & 0xffL
      val leftValue  = if (leftSigned && leftByte >= 0x80L) leftByte - 0x100L else leftByte
      val rightValue = if (rightSigned && rightByte >= 0x80L) rightByte - 0x100L else rightByte
      result += leftValue * rightValue
      index += 1
    }
    truncate(result, 32)
  }

  def shuffleClamp(value: Long): Int =
    Math.min(value & 0xffffffffL, 31L).toInt

  def extractBits(
      source: Long,
      start: Long,
      count: Long,
      width: ScalarBitWidth,
      signed: Boolean
  ): Long = {
    val sourceBits = truncate(source, width)
    val bitStart   = (start & 0xffL).toInt
    val bitCount   = (count & 0xffL).toInt
    if (bitCount == 0) 0L
    else if (bitStart >= width.raw)
      if (signed && (sourceBits & (1L << (width.raw - 1))) != 0L) truncate(-1L, width) else 0L
    else {
      val extractedWidth    = Math.min(bitCount, width.raw - bitStart)
      val mask              = if (extractedWidth == 64) -1L else (1L << extractedWidth) - 1L
      val extracted         = (sourceBits >>> bitStart) & mask
      val extractedBitWidth = extractedWidth.unsafe[Int in 1 ~ 64]
      truncate(if (signed) Bits.signExtend(extracted, extractedBitWidth) else extracted, width)
    }
  }

  def bitMask(start: Long, count: Long, mode: BitMaskMode): Long = {
    val rawStart         = start & 0xffffffffL
    val rawCount         = count & 0xffffffffL
    val start1           = rawStart & 0x1fL
    val count1           = rawCount & 0x1fL
    val positionOverflow = mode == BitMaskMode.Clamp && rawStart >= 32L
    val widthOverflow    = mode == BitMaskMode.Clamp && rawCount >= 32L
    val sum              = start1 + count1
    val mask0            =
      if (positionOverflow) 0L else truncate(-1L << start1.toInt, 32)
    val mask1 =
      if (sum >= 32L || positionOverflow || widthOverflow) 0L
      else if (count1 == 0L) 0xffffffffL
      else truncate(-1L << sum.toInt, 32)
    truncate(mask0 & ~mask1, 32)
  }

  def integerMultiplyAdd(
      left: Long,
      right: Long,
      addend: Long,
      operation: IntegerMultiplyAddOperation,
      carryIn: Boolean
  ): (Long, Boolean) = operation match {
    case IntegerMultiplyAddOperation.Low(operandWidth, resultWidth, usesCarry, _) =>
      val leftValue    = truncate(left, operandWidth)
      val rightValue   = truncate(right, operandWidth)
      val productLow   = leftValue * rightValue
      val productHigh  = IntegerArithmetic.unsignedMultiplyHigh(leftValue, rightValue)
      val addendValue  = truncate(addend, resultWidth)
      val first        = productLow + addendValue
      val firstCarry   = java.lang.Long.compareUnsigned(first, productLow) < 0
      val useCarry     = usesCarry && carryIn
      val result       = if (useCarry) first + 1L else first
      val secondCarry  = useCarry && java.lang.Long.compareUnsigned(result, first) < 0
      val overflowHigh = productHigh + (if (firstCarry || secondCarry) 1L else 0L)
      val overflow     =
        if (resultWidth.raw == 64) overflowHigh != 0L
        else overflowHigh != 0L || (result >>> resultWidth.raw) != 0L
      truncate(result, resultWidth) -> overflow
    case IntegerMultiplyAddOperation.High(operandWidth, resultWidth, usesCarry, _) =>
      val leftValue   = truncate(left, operandWidth)
      val rightValue  = truncate(right, operandWidth)
      val productLow  = leftValue * rightValue
      val productHigh = IntegerArithmetic.unsignedMultiplyHigh(leftValue, rightValue)
      val high        =
        if (operandWidth.raw == 64) productHigh
        else truncate(
          (productLow >>> operandWidth.raw) | (productHigh << (64 - operandWidth.raw)),
          operandWidth
        )
      val addendValue = truncate(addend, resultWidth)
      IntegerArithmetic.addUnsigned(high, addendValue, usesCarry && carryIn, resultWidth)
  }

  def insertBits(
      insert: Long,
      base: Long,
      start: Long,
      count: Long,
      width: ScalarBitWidth
  ): Long = {
    val bitStart = (start & 0xffL).toInt
    val bitCount = (count & 0xffL).toInt
    if (bitCount == 0 || bitStart >= width.raw) truncate(base, width)
    else {
      val insertedWidth    = Math.min(bitCount, width.raw - bitStart)
      val fieldMask        = if (insertedWidth == 64) -1L else ((1L << insertedWidth) - 1L) << bitStart
      val insertedBitWidth = insertedWidth.unsafe[Int in 1 ~ 64]
      truncate((base & ~fieldMask) | ((insert & Bits.truncate(-1L, insertedBitWidth)) << bitStart), width)
    }
  }

  def permuteBytes(left: Long, right: Long, control: Long, mode: BytePermuteMode): Long = {
    val source = Array.tabulate(8) { index =>
      val value = if (index < 4) left else right
      ((value >>> (8 * (index & 3))) & 0xffL).toInt
    }
    val selector      = (control & 3L).toInt
    val selectedBytes = mode match {
      case BytePermuteMode.Default => Array.tabulate(4)(index => ((control >>> (4 * index)) & 7L).toInt)
      case BytePermuteMode.F4E     => Array.tabulate(4)(index => (selector + index) & 7)
      case BytePermuteMode.B4E     => Array.tabulate(4)(index => (selector - index) & 7)
      case BytePermuteMode.RC8     => Array.fill(4)(selector)
      case BytePermuteMode.ECL     => Array.tabulate(4)(index => Math.max(index, selector))
      case BytePermuteMode.ECR     => Array.tabulate(4)(index => Math.min(index, selector))
      case BytePermuteMode.RC16    => Array.tabulate(4)(index => ((selector & 1) * 2) + (index & 1))
    }
    val result = (0 until 4).foldLeft(0L) { (bits, index) =>
      val selection  = selectedBytes(index)
      val selected   = source(selection)
      val signExtend = mode == BytePermuteMode.Default && ((control >>> (4 * index)) & 8L) != 0L
      val byte       = if (signExtend) { if ((selected & 0x80) != 0) 0xff else 0 }
      else selected
      bits | (byte.toLong << (8 * index))
    }
    truncate(result, 32)
  }

  private def divideInteger(
      left: Long,
      right: Long,
      width: ScalarBitWidth,
      signed: Boolean
  ): Long = {
    val leftBits  = truncate(left, width)
    val rightBits = truncate(right, width)
    if (rightBits == 0L) 0L
    else if (signed) {
      val dividend = signExtend(leftBits, width)
      val divisor  = signExtend(rightBits, width)
      if (dividend == Long.MinValue && divisor == -1L) Long.MinValue
      else dividend / divisor
    } else if (width.raw == 64) java.lang.Long.divideUnsigned(leftBits, rightBits)
    else leftBits / rightBits
  }

  private def remainderInteger(
      left: Long,
      right: Long,
      width: ScalarBitWidth,
      signed: Boolean
  ): Long = {
    val leftBits  = truncate(left, width)
    val rightBits = truncate(right, width)
    if (rightBits == 0L) 0L
    else if (signed) {
      val dividend = signExtend(leftBits, width)
      val divisor  = signExtend(rightBits, width)
      if (dividend == Long.MinValue && divisor == -1L) 0L
      else dividend % divisor
    } else if (width.raw == 64) java.lang.Long.remainderUnsigned(leftBits, rightBits)
    else leftBits % rightBits
  }

  def compare(
      left: Long,
      right: Long,
      comparison: Comparison,
      format: NumericFormat
  ): Boolean = {
    val width      = format.width
    val leftValue  = truncate(left, width)
    val rightValue = truncate(right, width)
    format match {
      case NumericFormat.IEEE(bits) => compareFloating(
          IEEE754.toDouble(leftValue, bits),
          IEEE754.toDouble(rightValue, bits),
          comparison
        )
      case NumericFormat.BF16 => compareFloating(
          IEEE754.binary32ToFloat(Binary32.low(leftValue << 16)).toDouble,
          IEEE754.binary32ToFloat(Binary32.low(rightValue << 16)).toDouble,
          comparison
        )
      case NumericFormat.Signed(_) => compareOrder(
          java.lang.Long.compare(signExtend(leftValue, width), signExtend(rightValue, width)),
          false,
          comparison
        )
      case NumericFormat.Bits(_) | NumericFormat.Unsigned(_) => compareOrder(
          if (width.raw == 64) java.lang.Long.compareUnsigned(leftValue, rightValue)
          else java.lang.Long.compare(leftValue, rightValue),
          false,
          comparison
        )
    }
  }

  private def compareFloating(left: Double, right: Double, comparison: Comparison): Boolean =
    compareOrder(if (left < right) -1 else if (left > right) 1 else 0, left.isNaN || right.isNaN, comparison)

  private def compareOrder(order: Int, unordered: Boolean, comparison: Comparison): Boolean =
    comparison match {
      case Comparison.Ordered                     => !unordered
      case Comparison.Unordered                   => unordered
      case Comparison.Equal                       => !unordered && order == 0
      case Comparison.EqualUnordered              => unordered || order == 0
      case Comparison.NotEqual                    => !unordered && order != 0
      case Comparison.NotEqualUnordered           => unordered || order != 0
      case Comparison.LessThan                    => !unordered && order < 0
      case Comparison.LessThanUnordered           => unordered || order < 0
      case Comparison.LessThanOrEqual             => !unordered && order <= 0
      case Comparison.LessThanOrEqualUnordered    => unordered || order <= 0
      case Comparison.GreaterThan                 => !unordered && order > 0
      case Comparison.GreaterThanUnordered        => unordered || order > 0
      case Comparison.GreaterThanOrEqual          => !unordered && order >= 0
      case Comparison.GreaterThanOrEqualUnordered => unordered || order >= 0
    }

  def comparePredicates(left: Boolean, right: Boolean, comparison: Comparison): Boolean = comparison match {
    case Comparison.Ordered | Comparison.Unordered                              => false
    case Comparison.Equal | Comparison.EqualUnordered                           => left == right
    case Comparison.NotEqual | Comparison.NotEqualUnordered                     => left != right
    case Comparison.LessThan | Comparison.LessThanUnordered                     => !left && right
    case Comparison.LessThanOrEqual | Comparison.LessThanOrEqualUnordered       => !left || right
    case Comparison.GreaterThan | Comparison.GreaterThanUnordered               => left && !right
    case Comparison.GreaterThanOrEqual | Comparison.GreaterThanOrEqualUnordered => left || !right
  }

  def convert(
      value: Long,
      sourceType: NumericFormat,
      destinationType: NumericFormat,
      rounding: Option[ConversionRounding],
      clamp: Option[ConversionClamp] = None
  ): Long = {
    val converted = sourceType match {
      case NumericFormat.IEEE(bits) =>
        convertFloating(IEEE754.toDouble(value, bits), bits.raw == 64, destinationType, rounding)
      case NumericFormat.BF16 => convertFloating(bfloat16Value(value), false, destinationType, rounding)
      case source: (NumericFormat.Bits | NumericFormat.Signed | NumericFormat.Unsigned) =>
        convertInteger(value, source, destinationType, rounding, clamp.contains(ConversionClamp.Saturate))
    }
    clamp.fold(converted)(clampFloating(converted, destinationType, _))
  }

  private def clampFloating(bits: Long, destination: NumericFormat, clamp: ConversionClamp): Long = {
    def bounded(value: Double, one: => Long): Long = clamp match {
      case ConversionClamp.Saturate => if (value.isNaN || value <= 0.0) 0L else if (value >= 1.0) one else bits
      case ConversionClamp.Relu     => if (value < 0.0) 0L else bits
    }
    destination match {
      case NumericFormat.IEEE(width) => bounded(IEEE754.toDouble(bits, width), IEEE754.fromDouble(1.0, width))
      case NumericFormat.BF16        => bounded(bfloat16Value(bits), bfloat16Bits(1.0))
      case _                         => bits
    }
  }

  private def saturateInteger(value: Long, unsigned: Boolean, destination: NumericFormat): Long = {
    val bits   = destination.width.raw
    val signed = destination match {
      case NumericFormat.Signed(_) => true
      case _                       => false
    }
    val maximum = if (signed) (1L << (bits - 1)) - 1L else if (bits == 64) -1L else (1L << bits) - 1L
    if (!unsigned && value < 0) { if (signed) math.max(value, -maximum - 1L) else 0L }
    else if (java.lang.Long.compareUnsigned(value, maximum) > 0) maximum
    else value
  }

  // Narrowing binary64 keeps a NaN payload; every other conversion yields the canonical NaN.
  private def convertFloating(
      floating: Double,
      fromBinary64: Boolean,
      destination: NumericFormat,
      rounding: Option[ConversionRounding]
  ): Long = destination match {
    case NumericFormat.BF16       => bfloat16Bits(floating, roundingMode(rounding))
    case NumericFormat.IEEE(bits) =>
      // the integral variants round to an integer before the format conversion
      val integral = rounding match {
        case Some(ConversionRounding.RZI) => Some(if (floating < 0.0) Math.ceil(floating) else Math.floor(floating))
        case Some(ConversionRounding.RMI) => Some(Math.floor(floating))
        case Some(ConversionRounding.RPI) => Some(Math.ceil(floating))
        case Some(ConversionRounding.RNI) => Some(Math.rint(floating))
        case _                            => None
      }
      val encoded = integral match {
        case Some(value) => IEEE754.fromDouble(value, bits)
        case None        => IEEE754.fromDouble(floating, bits, roundingMode(rounding))
      }
      if (fromBinary64 && bits.raw == 32) encoded else canonical(encoded, bits)
    case integer =>
      val integerRounding = rounding match {
        case Some(ConversionRounding.RN | ConversionRounding.RNI) => IEEE754.Rounding.NearestEven
        case Some(ConversionRounding.RM | ConversionRounding.RMI) => IEEE754.Rounding.Down
        case Some(ConversionRounding.RP | ConversionRounding.RPI) => IEEE754.Rounding.Up
        case _                                                    => IEEE754.Rounding.TowardZero
      }
      val signed = integer match {
        case NumericFormat.Signed(_) => true
        case _                       => false
      }
      IEEE754.toIntegerSaturated(floating, integer.width, signed, integerRounding)
  }

  private def convertInteger(
      value: Long,
      source: NumericFormat.Bits | NumericFormat.Signed | NumericFormat.Unsigned,
      destination: NumericFormat,
      rounding: Option[ConversionRounding],
      saturate: Boolean
  ): Long = {
    val sourceBits = truncate(value, source.width)
    val integer    = source match {
      case NumericFormat.Signed(_) => signExtend(sourceBits, source.width)
      case _                       => sourceBits
    }
    val unsigned = source match {
      case NumericFormat.Unsigned(_) => true
      case _                         => false
    }
    destination match {
      case NumericFormat.IEEE(bits) => IEEE754.integerToBinary(integer, unsigned, bits, roundingMode(rounding))
      case NumericFormat.BF16       => IEEE754.integerToBfloat16(integer, unsigned, roundingMode(rounding)).toLong
      case _                        =>
        truncate(if (saturate) saturateInteger(integer, unsigned, destination) else integer, destination.width)
    }
  }
}

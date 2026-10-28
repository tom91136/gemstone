package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import VMIR.*
import java.math.BigDecimal
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.prelude.*

/** The interpreter's hand-written formulas before staging, kept as the references the staged semantics must match. */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private object references {
  def signed16(value: Long): Long = Bits.signExtend(Bits.low16(value).toLong, 16)

  def unsignedMultiplyHigh24(left: Long, right: Long): Long =
    Bits.high32((left & 0xffffffL) * (right & 0xffffffL)).toLong
  def unsignedMultiply24(left: Long, right: Long): Long =
    Bits.low32((left & 0xffffffL) * (right & 0xffffffL)).toLong
  def unsignedMultiplyAdd24(left: Long, right: Long, addend: Long): Long =
    Bits.low32(unsignedMultiply24(left, right) + Bits.low32(addend).toLong).toLong
  def unsignedSubtractReverse32(left: Long, right: Long): Long =
    Bits.low32(Bits.low32(right).toLong - Bits.low32(left).toLong).toLong
  def signedMultiply24(left: Long, right: Long): Long =
    Bits.low32(Bits.signExtend(left, 24) * Bits.signExtend(right, 24)).toLong
  def signedMultiplyHigh24(left: Long, right: Long): Long =
    Bits.low32((Bits.signExtend(left, 24) * Bits.signExtend(right, 24)) >> 32).toLong

  def signedMultiplyImmediate(register: Long, immediate: Int): (Long, Boolean) = {
    val product  = register.toInt.toLong * immediate.toLong
    val overflow = product < Int.MinValue.toLong || product > Int.MaxValue.toLong
    (Bits.low32(product).toLong, overflow)
  }

  def mergeD16(existing: Long, value: Long, high: Boolean): Long =
    if (high) (Bits.low32(existing).toLong & 0x0000ffffL) | (Bits.low16(value).toLong << 16)
    else (Bits.low32(existing).toLong & 0xffff0000L) | Bits.low16(value).toLong

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

  def classMask(bits: Long, width: Int in (16, 32, 64)): Long = {
    val infinity = IEEE754.infinity(width)
    val quiet    = IEEE754.quietBit(width)
    val exponent = bits & infinity
    val fraction = bits & (quiet * 2 - 1L)
    val negative = (bits & IEEE754.signBit(width)) != 0L
    val classBit =
      if (exponent == infinity && fraction != 0L) if ((fraction & quiet) == 0L) 0 else 1
      else if (exponent == infinity) if (negative) 2 else 9
      else if (exponent == 0L && fraction == 0L) if (negative) 5 else 6
      else if (exponent == 0L) if (negative) 4 else 7
      else if (negative) 3 else 8
    1L << classBit
  }

  def clampF32(value: Float): Float =
    if (value.isNaN) value else math.max(0.0f, math.min(1.0f, value))

  def clampF16(bits: Long, clamp: Boolean): Long =
    if (clamp) IEEE754.floatToBinary16(clampF32(IEEE754.binary16ToFloat(Bits.low16(bits)))).toLong else bits

  def clampFloat(bits: Long, width: Int in (16, 32, 64)): Long = width.raw match {
    case 64 =>
      val value = IEEE754.binary64ToDouble(bits)
      if (value.isNaN) bits else IEEE754.doubleToBinary64(math.max(0.0, math.min(1.0, value)))
    case 32 =>
      val value = IEEE754.binary32ToFloat(Bits.low32(bits))
      if (value.isNaN) bits else IEEE754.floatToBinary32(clampF32(value)).toLong
    case _ => Bits.low16(clampF16(bits, clamp = true)).toLong
  }

  private val Binary16MinNormal = 6.103515625e-5

  private def flushDenormal(value: Double, minimum: Double): Double =
    if (value != 0.0 && math.abs(value) < minimum) math.copySign(0.0, value) else value

  def outputModified(bits: Long, scale: OutputScale, width: Int in (16, 32, 64), clamp: Boolean): Long = {
    val (value, minimum) = width.raw match {
      case 64 => (IEEE754.binary64ToDouble(bits), java.lang.Double.MIN_NORMAL)
      case 32 => (IEEE754.binary32ToFloat(Bits.low32(bits)).toDouble, java.lang.Float.MIN_NORMAL.toDouble)
      case _  => (IEEE754.binary16ToFloat(Bits.low16(bits)).toDouble, Binary16MinNormal)
    }
    if (value.isNaN) bits
    else {
      val scaled  = flushDenormal(flushDenormal(value, minimum) * scale.factor, minimum) + 0.0
      val clamped = if (clamp) math.max(0.0, math.min(1.0, scaled)) else scaled
      width.raw match {
        case 64 => IEEE754.doubleToBinary64(clamped)
        case 32 => IEEE754.floatToBinary32(clamped.toFloat).toLong
        case _  => IEEE754.floatToBinary16(clamped.toFloat).toLong
      }
    }
  }

  @annotation.tailrec
  def trigF32(operation: VectorConvertOperation, value: Float): Float = operation match {
    case VectorConvertOperation.SineF32   => unit32(TranscendentalUnit.sine32, value)
    case VectorConvertOperation.CosineF32 => unit32(TranscendentalUnit.cosine32, value)
    // GFX8 gives sin(0) and cos(0) for finite operands beyond 256 turns
    case VectorConvertOperation.SineBoundedF32 =>
      if (math.abs(value) > 256.0f && !value.isInfinite) 0.0f else trigF32(VectorConvertOperation.SineF32, value)
    case VectorConvertOperation.CosineBoundedF32 =>
      if (math.abs(value) > 256.0f && !value.isInfinite) 1.0f else trigF32(VectorConvertOperation.CosineF32, value)
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
    def calculate(selection: Int, negate: Int): Long = {
      def operand(source: Long, index: Int): Long = {
        val value = Bits.low16(if ((selection & (1 << index)) != 0) source >>> 16 else source).toLong
        if ((negate & (1 << index)) != 0) value ^ 0x8000L else value
      }
      clampF16(IEEE754.fusedMultiplyAdd(operand(srcA, 0), operand(srcB, 1), operand(srcC, 2), 16), clamp)
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
      IEEE754.binary16ToFloat(Bits.low16(if (selectHigh) source >>> 16 else source))
    else IEEE754.binary32ToFloat(Bits.low32(source))
    val magnitude = if ((absolute & (1 << index)) != 0) math.abs(value) else value
    if ((negate & (1 << index)) != 0) -magnitude else magnitude
  }

  /** The FMA's NaN result: a NaN operand propagates quieted, and an invalid product gives the default NaN. */
  private def fmaNaN(left: Float, right: Float, addend: Float): Option[Long] = {
    def nan(value: Float): Boolean = value.isNaN
    def invalid: Boolean           =
      (left == 0.0f && right.isInfinite) || (right == 0.0f && left.isInfinite)
    if (nan(left)) Some(IEEE754.floatToBinary32(left).toLong)
    else if (nan(right)) Some(IEEE754.floatToBinary32(right).toLong)
    else if (invalid) Some(IEEE754.signBit(32) | IEEE754.infinity(32) | IEEE754.quietBit(32))
    else if (nan(addend)) Some(IEEE754.floatToBinary32(addend).toLong)
    else None
  }

  /** The NaN a product and an addend give: the product's NaN, else the addend's, both quieted. */
  private def madNaN(product: Float, addend: Float): Option[Long] =
    if (product.isNaN) Some(IEEE754.floatToBinary32(product).toLong)
    else if (addend.isNaN) Some(IEEE754.floatToBinary32(addend).toLong)
    else None

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
      high: Boolean,
      fused: Boolean
  ): Long = {
    val left    = mixedOperand(srcA, 0, opSelection, opSelectionHi, negate, absolute)
    val right   = mixedOperand(srcB, 1, opSelection, opSelectionHi, negate, absolute)
    val addend  = mixedOperand(srcC, 2, opSelection, opSelectionHi, negate, absolute)
    val rounded =
      if (fused)
        fmaNaN(left, right, addend) match {
          case Some(nan) => IEEE754.floatToBinary16(IEEE754.binary32ToFloat(Bits.low32(nan))).toLong
          case None      =>
            IEEE754.fusedMultiplyAddExact(
              left.toDouble,
              right.toDouble,
              addend.toDouble,
              16,
              IEEE754.Rounding.NearestEven
            )
        }
      else {
        val product = left * right
        madNaN(product, addend) match {
          case Some(nan) => IEEE754.floatToBinary16(IEEE754.binary32ToFloat(Bits.low32(nan))).toLong
          case None      =>
            IEEE754.fusedMultiplyAddExact(product.toDouble, 1.0, addend.toDouble, 16, IEEE754.Rounding.NearestEven)
        }
      }
    mergeD16(existing, clampF16(rounded, clamp), high)
  }

  def fmaMixF32(
      srcA: Long,
      srcB: Long,
      srcC: Long,
      opSelection: Int,
      opSelectionHi: Int,
      negate: Int,
      absolute: Int,
      clamp: Boolean,
      fused: Boolean
  ): Long = {
    val left   = mixedOperand(srcA, 0, opSelection, opSelectionHi, negate, absolute)
    val right  = mixedOperand(srcB, 1, opSelection, opSelectionHi, negate, absolute)
    val addend = mixedOperand(srcC, 2, opSelection, opSelectionHi, negate, absolute)
    val result =
      if (fused)
        fmaNaN(left, right, addend) match {
          case Some(nan) => IEEE754.binary32ToFloat(Bits.low32(nan))
          case None      => IEEE754.fma(left, right, addend)
        }
      else {
        val product = left * right
        madNaN(product, addend) match {
          case Some(nan) => IEEE754.binary32ToFloat(Bits.low32(nan))
          case None      => product + addend
        }
      }
    IEEE754.floatToBinary32(if (clamp) clampF32(result) else result).toLong
  }

  /** V_DOT2_F32_F16: the first NaN operand quieted, an invalid sum the negative default NaN, and otherwise the exact
    * products summed exactly with the accumulator and rounded once to binary32.
    */
  def dot2F32F16(left: Long, right: Long, accumulator: Long): Binary32 = {
    val halves = Vector(left & 0xffffL, right & 0xffffL, (left >>> 16) & 0xffffL, (right >>> 16) & 0xffffL)
    def widened(half: Long): Long = ((half & 0x8000L) << 16) | 0x7fc00000L | ((half & 0x3ffL) << 13)
    def value(half: Long): Double = IEEE754.binary16ToFloat(Bits.low16(half)).toDouble
    val nan                       = halves.find(IEEE754.isNaN(_, 16)).map(widened(_)).orElse(
      Option.when(IEEE754.isNaN(accumulator & 0xffffffffL, 32))((accumulator & 0xffffffffL) | 0x00400000L)
    )
    nan.fold {
      val terms = Vector(
        value(halves(0)) * value(halves(1)),
        value(halves(2)) * value(halves(3)),
        IEEE754.binary32ToFloat(Bits.low32(accumulator)).toDouble
      )
      val sum = terms.sum
      if (sum.isNaN) Bits.low32(0xffc00000L)
      else if (sum.isInfinite) IEEE754.floatToBinary32(sum.toFloat)
      else Bits.low32(exactlyRounded(terms))
    }(bits => Bits.low32(bits))
  }

  /** Finite binary64 `terms` summed exactly, as multiples of 2^-1074, and rounded once to binary32. */
  private def exactlyRounded(terms: Vector[Double]): Long = {
    def scaled(value: Double): BigInt = {
      val bits     = IEEE754.doubleToBinary64(value)
      val exponent = ((bits >>> 52) & 0x7ffL).toInt
      val fraction = bits & 0xfffffffffffffL
      val integer  = BigInt(if (exponent == 0) fraction else fraction | (1L << 52)) << math.max(exponent - 1, 0)
      if (bits < 0L) -integer else integer
    }
    val exact = terms.map(scaled(_)).sum
    // an exact zero is -0 only when every term is
    if (exact.signum == 0) if (terms.forall(IEEE754.doubleToBinary64(_) < 0L)) 0x80000000L else 0L
    else {
      val dropped = math.max(exact.bitLength - 62, 0)
      val sticky  = if (dropped > 0 && exact.abs.lowestSetBit < dropped) 1L else 0L
      val sum     = (exact.abs >> dropped).toLong | sticky
      IEEE754.encodeSum(
        if (exact.signum < 0) -sum else sum,
        dropped - 1074,
        exponentBits = 8,
        fieldBits = 23,
        kept = 23,
        IEEE754.Rounding.NearestEven
      )
    }
  }

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

  def permuteBytes(srcA: Long, srcB: Long, selectors: Long): Long = {
    val sources = Bits.join32(srcB, srcA)
    (0 until 4).foldLeft(0L) { (result, byte) =>
      val selector = ((selectors >>> (byte * 8)) & 0xffL).toInt
      val picked   =
        if (selector < 8) (sources >>> (selector * 8)) & 0xffL
        else if (selector < 12) if (((sources >>> ((selector - 8) * 16 + 15)) & 1L) != 0L) 0xffL else 0L
        else if (selector == 12) 0L
        else 0xffL
      result | (picked << (byte * 8))
    }
  }

  private def comparisonResult(
      relation: VectorComparisonRelation,
      less: Boolean,
      equal: Boolean,
      greater: Boolean,
      unordered: Boolean
  ): Boolean = relation match {
    case VectorComparisonRelation.Equal                                  => equal
    case VectorComparisonRelation.OrderedNotEqual                        => less || greater
    case VectorComparisonRelation.NotEqual                               => !equal
    case VectorComparisonRelation.Less                                   => less
    case VectorComparisonRelation.LessOrEqual                            => less || equal
    case VectorComparisonRelation.Greater                                => greater
    case VectorComparisonRelation.GreaterOrEqual                         => greater || equal
    case VectorComparisonRelation.NotLess                                => !less
    case VectorComparisonRelation.NotLessOrEqual                         => !(less || equal)
    case VectorComparisonRelation.NotGreater                             => !greater
    case VectorComparisonRelation.NotGreaterOrEqual                      => !(greater || equal)
    case VectorComparisonRelation.Ordered                                => !unordered
    case VectorComparisonRelation.Unordered                              => unordered
    case VectorComparisonRelation.NotLessOrGreater                       => !less && !greater
    case VectorComparisonRelation.Class | VectorComparisonRelation.Never => false
    case VectorComparisonRelation.Always                                 => true
  }

  def compare(operation: VectorComparison, left: Long, right: Long): Boolean = {
    def integer(leftValue: Long, rightValue: Long): Boolean = {
      val ordering = java.lang.Long.compare(leftValue, rightValue)
      comparisonResult(operation.relation, ordering < 0, ordering == 0, ordering > 0, false)
    }
    def unsigned(leftValue: Long, rightValue: Long): Boolean = {
      val ordering = java.lang.Long.compareUnsigned(leftValue, rightValue)
      comparisonResult(operation.relation, ordering < 0, ordering == 0, ordering > 0, false)
    }
    def floating(leftValue: Double, rightValue: Double): Boolean =
      comparisonResult(
        operation.relation,
        leftValue < rightValue,
        leftValue == rightValue,
        leftValue > rightValue,
        leftValue.isNaN || rightValue.isNaN
      )

    operation.kind match {
      case VectorComparisonKind.Signed16 =>
        integer(Bits.signExtend(Bits.low16(left).toLong, 16), Bits.signExtend(Bits.low16(right).toLong, 16))
      case VectorComparisonKind.Signed32 =>
        integer(Bits.signExtend(Bits.low32(left).toLong, 32), Bits.signExtend(Bits.low32(right).toLong, 32))
      case VectorComparisonKind.Signed64   => integer(left, right)
      case VectorComparisonKind.Unsigned16 => unsigned(Bits.low16(left).toLong, Bits.low16(right).toLong)
      case VectorComparisonKind.Unsigned32 => unsigned(Bits.low32(left).toLong, Bits.low32(right).toLong)
      case VectorComparisonKind.Unsigned64 => unsigned(left, right)
      case VectorComparisonKind.Float16    =>
        floating(
          IEEE754.binary16ToFloat(Bits.low16(left)).toDouble,
          IEEE754.binary16ToFloat(Bits.low16(right)).toDouble
        )
      case VectorComparisonKind.Float32 =>
        floating(
          IEEE754.binary32ToFloat(Bits.low32(left)).toDouble,
          IEEE754.binary32ToFloat(Bits.low32(right)).toDouble
        )
      case VectorComparisonKind.Float64 =>
        floating(IEEE754.binary64ToDouble(left), IEEE754.binary64ToDouble(right))
      case VectorComparisonKind.ClassFloat16 => (classMask(Bits.low16(left).toLong, 16) & Bits.low16(right).toLong) !=
          0L
      case VectorComparisonKind.ClassFloat32 =>
        (classMask(Bits.low32(left).toLong, 32) & Bits.low32(right).toLong) != 0L
      case VectorComparisonKind.ClassFloat64 =>
        (classMask(left, 64) & Bits.low32(right).toLong) != 0L
    }
  }

  def mbCount(mask: Long, lane: Int, high: Boolean, base: Long): Long = {
    val laneInHalf = lane & 31
    val belowLane  = if (laneInHalf == 0) 0L else (1L << laneInHalf) - 1L
    val relevant   =
      if (high) { if (lane < 32) 0L else belowLane }
      else if (lane < 32) belowLane
      else Bits.Mask32
    Bits.low32(base + java.lang.Integer.bitCount((mask & relevant).toInt).toLong).toLong
  }

  def dppQuadSourceLane(lane: Int, control: Int): Int = (lane & ~3) | ((control >>> ((lane & 3) * 2)) & 3)

  def dppSourceLane(lane: Int, control: Int): Int = {
    import EncodingConstants.DPP.*
    val rowBase  = lane & ~15
    val rowLane  = lane & 15
    val distance = control & 15
    if (DppControl.within(control, QUAD_PERM_FIRST, QUAD_PERM_LAST)) dppQuadSourceLane(lane, control)
    else if (DppControl.within(control, ROW_SHL_FIRST, ROW_SHL_LAST))
      if (rowLane + distance < 16) lane + distance else -1
    else if (DppControl.within(control, ROW_SHR_FIRST, ROW_SHR_LAST))
      if (rowLane >= distance) lane - distance else -1
    else if (DppControl.within(control, ROW_ROR_FIRST, ROW_ROR_LAST)) rowBase | ((rowLane - distance) & 15)
    else if (DppControl.within(control, ROW_SHARE_FIRST, ROW_SHARE_LAST)) rowBase | distance
    else if (DppControl.within(control, ROW_XMASK_FIRST, ROW_XMASK_LAST)) rowBase | (rowLane ^ distance)
    else control match {
      case WAVE_SHL1       => if (lane < 63) lane + 1 else -1
      case WAVE_ROL1       => (lane + 1) & 63
      case WAVE_SHR1       => if (lane > 0) lane - 1 else -1
      case WAVE_ROR1       => (lane - 1) & 63
      case ROW_MIRROR      => rowBase | (15 - rowLane)
      case ROW_HALF_MIRROR => (lane & ~7) | (7 - (lane & 7))
      case BCAST15         => if (rowBase > 0) rowBase - 1 else lane
      case BCAST31         => if (lane >= 32) 31 else lane
      case _               => -1
    }
  }

  def dpp8SourceLane(lane: Int, selectors: Int): Int = (lane & ~7) | ((selectors >>> ((lane & 7) * 3)) & 7)

  /** DS_SWIZZLE's source lane: a quad permutation names the source lane of each of a quad's four lanes; otherwise the
    * pattern's AND, OR and XOR masks rewrite the low five bits of the lane index and the higher bits pass through.
    */
  def swizzleSourceLane(lane: Int, control: Int): Int =
    if ((control & Swizzle.QUAD_PERM_ENC_MASK) == Swizzle.QUAD_PERM_ENC)
      (lane & ~3) | ((control >>> ((lane & 3) * 2)) & 3)
    else {
      val and = control & 0x1f
      val or  = (control >>> 5) & 0x1f
      val xor = (control >>> 10) & 0x1f
      (lane & ~0x1f) | ((((lane & 0x1f) & and) | or) ^ xor)
    }

  def bpermuteSourceLane(address: Long, offset: Long): Int                    = (((address + offset) >>> 2) & 63L).toInt
  def permuteDestinationLane(address: Long, offset: Long, waveSize: Int): Int =
    (((address + offset) >>> 2) & (waveSize - 1L)).toInt

  /** V_DIV_FIXUP_F64 on raw pairs, as [[divisionScaleF64]] decides on biased exponents. */
  def divisionFixupF64(q: Long, d: Long, n: Long): Long = {
    val (quotient, denominator, numerator) =
      (IEEE754.binary64ToDouble(q), IEEE754.binary64ToDouble(d), IEEE754.binary64ToDouble(n))
    val sign = (n ^ d) & Long.MinValue
    if (numerator.isNaN) n | 0x0008000000000000L
    else if (denominator.isNaN) d | 0x0008000000000000L
    else if ((numerator.isInfinite && denominator.isInfinite) || (numerator == 0.0 && denominator == 0.0))
      0xfff8000000000000L
    else if (denominator == 0.0 || numerator.isInfinite) sign | 0x7ff0000000000000L
    else if (denominator.isInfinite || numerator == 0.0) sign
    else if (Math.getExponent(numerator) - Math.getExponent(denominator) < -1075) sign
    else if (quotient.isNaN) sign | 0x7ff0000000000000L
    else sign | (q & Long.MaxValue)
  }

  /** V_DIV_SCALE_F32's result and VCC bit, which the hardware decides on the operands' biased exponents alone: VCC when
    * they differ by 96 either way.
    */
  private def biased32(value: Float): Int  = (java.lang.Float.floatToRawIntBits(value) >>> 23) & 0xff
  private def biased64(value: Double): Int = ((java.lang.Double.doubleToRawLongBits(value) >>> 52) & 0x7ffL).toInt

  def divisionScaleF32(s0: Float, s1: Float, s2: Float): (Float, Boolean) = {
    // the biased exponent fields, read from the bits since Math.getExponent of zero differs between platforms
    val (d, n) = (biased32(s1), biased32(s2))
    val result =
      if (s1 == 0.0f || s2 == 0.0f) Float.NaN
      else if (s0.isNaN) s0
      else if (n - d >= 96) if (s0 == s1) Math.scalb(s0, 64) else s0
      else if (d == 0) Math.scalb(s0, 64)
      else if (d >= 253) if (s0 == s1 || n - d > -96) Math.scalb(s0, -64) else s0
      else if (n - d <= -96) if (s0 == s2) Math.scalb(s0, 64) else s0
      else if (n <= 24) Math.scalb(s0, 64)
      else s0
    (result, math.abs(n - d) >= 96)
  }

  def divisionScaleF64(s0: Double, s1: Double, s2: Double): (Double, Boolean) = {
    val (d, n) = (biased64(s1), biased64(s2))
    val result =
      if (s1 == 0.0 || s2 == 0.0) Double.NaN
      else if (s0.isNaN) s0
      else if (n - d >= 768) if (s0 == s1) Math.scalb(s0, 128) else s0
      else if (d == 0) Math.scalb(s0, 128)
      else if (d >= 2045) if (s0 == s1 || n - d > -768) Math.scalb(s0, -128) else s0
      else if (n - d <= -768) if (s0 == s2) Math.scalb(s0, 128) else s0
      else if (n <= 54) Math.scalb(s0, 128)
      else s0
    (result, math.abs(n - d) >= 768)
  }

  /** V_DIV_FMAS with its flag set, on finite operands: `a * b + c` exactly, scaled by 2^64, or 2^128 for binary64, up
    * when `c`'s magnitude is at least one and down otherwise, then rounded once.
    */
  def divisionFusedMultiplyAdd(a: Double, b: Double, c: Double, width: Int): Long = {
    val power = (if (math.abs(c) >= 1.0) 1 else -1) * (if (width == 64) 128 else 64)
    val exact = BigDecimal(a).multiply(BigDecimal(b)).add(BigDecimal(c))
    val value = if (power > 0) exact.multiply(BigDecimal(2).pow(power))
    else exact.divide(BigDecimal(2).pow(-power))
    if (exact.signum == 0) IEEE754.fromDouble(IEEE754.fma(a, b, c), if (width == 64) 64 else 32)
    else if (width == 64) IEEE754.doubleToBinary64(value.doubleValue)
    else IEEE754.floatToBinary32(value.floatValue).toLong
  }

  def flushedF32(value: Float): Float =
    if (math.abs(value) < java.lang.Float.MIN_NORMAL) Math.copySign(0.0f, value) else value

  /** The transcendental unit's binary32 `function` of a value. */
  private def unit32(function: Long => Long, value: Float): Float =
    IEEE754.binary32ToFloat(Bits.low32(function(IEEE754.floatToBinary32(value).toLong)))

  /** V_RCP_F32, which reads and writes denormals as zeros of their sign whatever MODE says. */
  def reciprocalF32(value: Float): Float           = unit32(TranscendentalUnit.reciprocal32, value)
  def reciprocalSquareRootF32(value: Float): Float = unit32(TranscendentalUnit.reciprocalSquareRoot32, value)

  /** The VConvert lane formula: the stored dword, or register pair for the binary64 results, from the source's raw
    * bits; f64 sources arrive as a pair.
    */
  def convert(operation: VectorConvertOperation, source: Long, clamp: Boolean): Long = {
    val raw                                    = Bits.signed32(source)
    val float32                                = IEEE754.binary32ToFloat(Bits.low32(source))
    def float64: Double                        = IEEE754.binary64ToDouble(source)
    def unit64(function: Long => Long): Double = IEEE754.binary64ToDouble(function(source))
    def half: Float                            = IEEE754.binary16ToFloat(Bits.low16(source))
    def writeFloat16(value: Float): Long       = clampF16(IEEE754.floatToBinary16(value).toLong, clamp)
    def writeFloat32(value: Float): Long       =
      IEEE754.floatToBinary32(if (clamp) clampF32(value) else value).toLong
    def writeFloat64(value: Double): Long =
      IEEE754.doubleToBinary64(if (clamp) math.max(0.0, math.min(1.0, value)) else value)
    def toInteger(value: Double, width: Int in 1 ~ 64, signed: Boolean): Long =
      IEEE754.toIntegerSaturated(value, width, signed, IEEE754.Rounding.TowardZero)
    // the binary32 transcendental unit reads denormals as zeros whatever MODE says
    def transcendental: Float = flushedF32(float32)
    // Math.getExponent gives every denormal the same exponent
    def exponent(value: Double): Int =
      if (value != 0.0 && math.abs(value) < java.lang.Double.MIN_NORMAL) Math.getExponent(Math.scalb(value, 64)) - 64
      else Math.getExponent(value)
    operation match {
      case VectorConvertOperation.FrexpExponentF64 =>
        val value = float64
        Bits.unsigned32(if (value == 0.0 || value.isNaN || value.isInfinite) 0 else exponent(value) + 1).toLong
      case VectorConvertOperation.FrexpExponentF32 =>
        Bits.unsigned32(
          if (float32 == 0.0f || float32.isNaN || float32.isInfinite) 0 else exponent(float32.toDouble) + 1
        ).toLong
      case VectorConvertOperation.FrexpMantissaF32 =>
        writeFloat32(
          if (float32 == 0.0f || float32.isNaN || float32.isInfinite) float32
          else Math.scalb(float32.toDouble, -exponent(float32.toDouble) - 1).toFloat
        )
      case VectorConvertOperation.FrexpMantissaF64 =>
        val value = float64
        writeFloat64(
          if (value == 0.0 || value.isNaN || value.isInfinite) value
          else Math.scalb(value, -exponent(value) - 1)
        )
      case VectorConvertOperation.ReciprocalF32 => writeFloat32(reciprocalF32(float32))
      case VectorConvertOperation.ReciprocalF64 => writeFloat64(unit64(TranscendentalUnit.reciprocal64))
      case VectorConvertOperation.TruncateF32   =>
        writeFloat32((if (float32 >= 0.0f) math.floor(float32.toDouble) else math.ceil(float32.toDouble)).toFloat)
      case VectorConvertOperation.FractionF32 =>
        writeFloat32(math.min(float32 - math.floor(float32.toDouble).toFloat, Math.nextDown(1.0f)))
      case VectorConvertOperation.FloorF32            => writeFloat32(math.floor(float32.toDouble).toFloat)
      case VectorConvertOperation.RoundNearestEvenF32 => writeFloat32(Math.rint(float32.toDouble).toFloat)
      case VectorConvertOperation.TruncateF64         =>
        val value = float64
        writeFloat64(if (value >= 0.0) math.floor(value) else math.ceil(value))
      case VectorConvertOperation.FloorF64    => writeFloat64(math.floor(float64))
      case VectorConvertOperation.FractionF64 =>
        val value = float64
        writeFloat64(math.min(value - math.floor(value), Math.nextDown(1.0)))
      case VectorConvertOperation.RoundNearestEvenF64     => writeFloat64(Math.rint(float64))
      case VectorConvertOperation.ReciprocalSquareRootF32 => writeFloat32(reciprocalSquareRootF32(float32))
      case VectorConvertOperation.ReciprocalSquareRootF64 =>
        writeFloat64(unit64(TranscendentalUnit.reciprocalSquareRoot64))
      case VectorConvertOperation.SquareRootF32    => writeFloat32(unit32(TranscendentalUnit.squareRoot32, float32))
      case VectorConvertOperation.ExponentBase2F32 => writeFloat32(unit32(TranscendentalUnit.powerOfTwo32, float32))
      case trig @ (VectorConvertOperation.SineF32 | VectorConvertOperation.CosineF32 |
          VectorConvertOperation.SineBoundedF32 | VectorConvertOperation.CosineBoundedF32) =>
        writeFloat32(trigF32(trig, float32))
      case VectorConvertOperation.LogarithmBase2F32 =>
        writeFloat32(unit32(TranscendentalUnit.logarithm32, float32))
      // Sea Islands ISA: clamped forms bound infinities to max_float, legacy ones give +0 for a zero and flush
      // infinities to zero
      case VectorConvertOperation.ReciprocalClampF32 =>
        writeFloat32(math.max(-Float.MaxValue, math.min(Float.MaxValue, reciprocalF32(float32))))
      case VectorConvertOperation.ReciprocalClampF64 =>
        writeFloat64(
          math.max(-Double.MaxValue, math.min(Double.MaxValue, unit64(TranscendentalUnit.reciprocal64)))
        )
      case VectorConvertOperation.ReciprocalSquareRootClampF32 =>
        writeFloat32(math.max(-Float.MaxValue, math.min(Float.MaxValue, reciprocalSquareRootF32(float32))))
      case VectorConvertOperation.ReciprocalSquareRootClampF64 =>
        writeFloat64(
          math.max(-Double.MaxValue, math.min(Double.MaxValue, unit64(TranscendentalUnit.reciprocalSquareRoot64)))
        )
      case VectorConvertOperation.LogarithmClampF32 =>
        writeFloat32(math.max(-Float.MaxValue, unit32(TranscendentalUnit.logarithm32, float32)))
      case VectorConvertOperation.ReciprocalLegacyF32 =>
        val result = reciprocalF32(float32)
        writeFloat32(if (transcendental == 0.0f) 0.0f else if (result.isInfinite) 0.0f * result.sign else result)
      case VectorConvertOperation.ReciprocalSquareRootLegacyF32 =>
        writeFloat32(if (transcendental == 0.0f) 0.0f else reciprocalSquareRootF32(float32))
      case VectorConvertOperation.I32FromF32 => toInteger(float32.toDouble, 32, true)
      case VectorConvertOperation.U32FromF32 => toInteger(float32.toDouble, 32, false)
      case VectorConvertOperation.I32FromF64 => toInteger(float64, 32, true)
      case VectorConvertOperation.U32FromF64 => toInteger(float64, 32, false)
      case byte @ (
            VectorConvertOperation.F32FromUByte0 | VectorConvertOperation.F32FromUByte1 |
            VectorConvertOperation.F32FromUByte2 | VectorConvertOperation.F32FromUByte3
          ) =>
        val index = byte.ordinal - VectorConvertOperation.F32FromUByte0.ordinal
        writeFloat32(((raw >>> (index * 8)) & 0xff).toFloat)
      case VectorConvertOperation.F32FromF64 => writeFloat32(float64.toFloat)
      case VectorConvertOperation.F32FromF16 => writeFloat32(IEEE754.binary16ToFloat(Bits.low16(source)))
      case VectorConvertOperation.F16FromF32 => clampF16(IEEE754.floatToBinary16(float32).toLong, clamp)
      case VectorConvertOperation.I16FromF16 =>
        toInteger(IEEE754.binary16ToFloat(Bits.low16(source)).toDouble, 16, true)
      case VectorConvertOperation.F64FromI32        => writeFloat64(raw.toDouble)
      case VectorConvertOperation.F64FromU32        => writeFloat64(Integer.toUnsignedLong(raw).toDouble)
      case VectorConvertOperation.F64FromF32        => writeFloat64(float32.toDouble)
      case VectorConvertOperation.F32FromI32        => writeFloat32(raw.toFloat)
      case VectorConvertOperation.F32FromU32        => writeFloat32(Integer.toUnsignedLong(raw).toFloat)
      case VectorConvertOperation.CeilF32           => writeFloat32(math.ceil(float32.toDouble).toFloat)
      case VectorConvertOperation.CeilF64           => writeFloat64(math.ceil(float64))
      case VectorConvertOperation.SquareRootF64     => writeFloat64(unit64(TranscendentalUnit.squareRoot64))
      case VectorConvertOperation.FloorI32FromF32   => toInteger(math.floor(float32.toDouble), 32, true)
      case VectorConvertOperation.NearestI32FromF32 => toInteger(math.floor(float32.toDouble + 0.5), 32, true)
      case VectorConvertOperation.FrexpExponentF16  =>
        val value = half
        if (value == 0.0f || value.isNaN || value.isInfinite) 0L
        else Bits.low16(Math.getExponent(value) + 1L).toLong
      case VectorConvertOperation.F16FromI16 => writeFloat16(source.toShort.toFloat)
      case VectorConvertOperation.F16FromU16 => writeFloat16(Bits.low16(source).toLong.toFloat)
      case VectorConvertOperation.U16FromF16 => toInteger(half.toDouble, 16, false)
      // the I4 value over 16, and normalised integers rounded to nearest even
      case VectorConvertOperation.I32FromI16  => Bits.low32(Bits.signExtend(source & 0xffffL, 16)).toLong
      case VectorConvertOperation.U32FromU16  => source & 0xffffL
      case VectorConvertOperation.F32FromBF16 =>
        writeFloat32(IEEE754.binary32ToFloat(Bits.low32((source & 0xffffL) << 16)))
      case VectorConvertOperation.OffsetF32FromI4   => writeFloat32((((source & 0xfL) ^ 8L) - 8L).toFloat / 16.0f)
      case VectorConvertOperation.SignedNormFromF16 =>
        if (half.isNaN) 0L else Bits.low16(Math.rint(half.toDouble.max(-1.0).min(1.0) * 32767.0).toLong).toLong
      case VectorConvertOperation.UnsignedNormFromF16 =>
        if (half.isNaN) 0L else Math.rint(half.toDouble.max(0.0).min(1.0) * 65535.0).toLong
      case VectorConvertOperation.PseudoRandom =>
        ((source << 1) ^ (if ((source & 0x80000000L) != 0L) 197L else 0L)) & 0xffffffffL
      case VectorConvertOperation.SaturatePackU8FromI16 =>
        def byte(value: Long): Long = Bits.signExtend(value & 0xffffL, 16).max(0L).min(255L)
        byte(source) | (byte(source >>> 16) << 8)
      case VectorConvertOperation.SaturatePackI4FromI8 =>
        (0 until 4).map(index =>
          (Bits.signExtend((source >>> (8 * index)) & 0xffL, 8).max(-8L).min(7L) & 0xfL) << (4 * index)
        )
          .sum
      case VectorConvertOperation.SaturatePackU4FromU8 =>
        (0 until 4).map(index => ((source >>> (8 * index)) & 0xffL).min(15L) << (4 * index)).sum
      case VectorConvertOperation.F32FromE4M3     => eightBit(source, Minifloat.E4M3)
      case VectorConvertOperation.F32FromE5M2     => eightBit(source, Minifloat.E5M2)
      case VectorConvertOperation.F32FromE4M3Fnuz => eightBit(source, Minifloat.E4M3FNUZ)
      case VectorConvertOperation.F32FromE5M2Fnuz => eightBit(source, Minifloat.E5M2FNUZ)
      case VectorConvertOperation.PairF32FromE4M3 =>
        eightBit(source, Minifloat.E4M3) |
          (eightBit(source >>> 8, Minifloat.E4M3) << 32)
      case VectorConvertOperation.PairF32FromE5M2 =>
        eightBit(source, Minifloat.E5M2) |
          (eightBit(source >>> 8, Minifloat.E5M2) << 32)
      case VectorConvertOperation.PairF32FromE4M3Fnuz =>
        eightBit(source, Minifloat.E4M3FNUZ) |
          (eightBit(source >>> 8, Minifloat.E4M3FNUZ) << 32)
      case VectorConvertOperation.PairF32FromE5M2Fnuz =>
        eightBit(source, Minifloat.E5M2FNUZ) |
          (eightBit(source >>> 8, Minifloat.E5M2FNUZ) << 32)
      case VectorConvertOperation.F16FromE4M3       => eightBitHalf(source, Minifloat.E4M3)
      case VectorConvertOperation.F16FromE5M2       => eightBitHalf(source, Minifloat.E5M2)
      case VectorConvertOperation.PackedF16FromE4M3 =>
        eightBitHalf(source, Minifloat.E4M3) | (eightBitHalf(source >>> 8, Minifloat.E4M3) << 16)
      case VectorConvertOperation.PackedF16FromE5M2 =>
        eightBitHalf(source, Minifloat.E5M2) | (eightBitHalf(source >>> 8, Minifloat.E5M2) << 16)
      case operation => clampF16(IEEE754.doubleToBinary16(halfOperation(operation, half.toDouble)).toLong, clamp)
    }
  }

  /** The low byte as the 8-bit float `format`, as binary32. */
  private def eightBit(source: Long, format: Minifloat): Long =
    IEEE754.floatToBinary32(format.toDouble(source & 0xffL).toFloat).toLong

  /** The low byte as the 8-bit float `format`, as binary16. */
  private def eightBitHalf(source: Long, format: Minifloat): Long =
    IEEE754.doubleToBinary16(format.toDouble(source & 0xffL)).toLong

  /** The binary16 unary operations in binary64, before their one rounding to binary16. */
  private def halfOperation(operation: VectorConvertOperation, x: Double): Double = operation match {
    case VectorConvertOperation.CeilF16                 => math.ceil(x)
    case VectorConvertOperation.FloorF16                => math.floor(x)
    case VectorConvertOperation.TruncateF16             => if (x >= 0.0) math.floor(x) else math.ceil(x)
    case VectorConvertOperation.RoundNearestEvenF16     => Math.rint(x)
    case VectorConvertOperation.FractionF16             => math.min(x - math.floor(x), 2047.0 / 2048.0)
    case VectorConvertOperation.SquareRootF16           => math.sqrt(x)
    case VectorConvertOperation.ReciprocalF16           => 1.0 / x
    case VectorConvertOperation.ReciprocalSquareRootF16 => 1.0 / math.sqrt(x)
    case VectorConvertOperation.ExponentBase2F16        => math.pow(2.0, x)
    case VectorConvertOperation.LogarithmBase2F16       => unit32(TranscendentalUnit.logarithm32, x.toFloat).toDouble
    case VectorConvertOperation.SineF16                 => trigF32(VectorConvertOperation.SineF32, x.toFloat).toDouble
    case VectorConvertOperation.CosineF16               => trigF32(VectorConvertOperation.CosineF32, x.toFloat).toDouble
    case VectorConvertOperation.SineBoundedF16          =>
      trigF32(VectorConvertOperation.SineBoundedF32, x.toFloat).toDouble
    case VectorConvertOperation.CosineBoundedF16 =>
      trigF32(VectorConvertOperation.CosineBoundedF32, x.toFloat).toDouble
    case VectorConvertOperation.FrexpMantissaF16 =>
      if (x == 0.0 || x.isNaN || x.isInfinite) x else Math.scalb(x, -Math.getExponent(x) - 1)
    case other => throw IllegalArgumentException(s"$other is no binary16 operation")
  }

  /** The SUnary formula: the result from the source, read at its width, and the destination's previous dword. */
  def scalarUnary(operation: ScalarUnaryOperation, source: Long, destination: Long): Long = operation match {
    case ScalarUnaryOperation.U32FromF32 =>
      IEEE754.toIntegerSaturated(
        IEEE754.binary32ToFloat(Bits.low32(source)).toDouble,
        32,
        false,
        IEEE754.Rounding.TowardZero
      )
    case ScalarUnaryOperation.TruncateF32 =>
      val value = java.lang.Float.intBitsToFloat(source.toInt)
      java.lang.Float.floatToRawIntBits((if (value >= 0) math.floor(value.toDouble)
                                         else math.ceil(value.toDouble)).toFloat).toLong
    case ScalarUnaryOperation.F32FromU32 =>
      java.lang.Float.floatToRawIntBits(Bits.low32(source).toLong.toFloat).toLong
    case ScalarUnaryOperation.AbsoluteI32     => Bits.low32(math.abs(Bits.low32(source).toInt).toLong).toLong
    case ScalarUnaryOperation.SignExtendI8    => Bits.low32(Bits.signExtend(source & 0xffL, 8)).toLong
    case ScalarUnaryOperation.SignExtendI16   => Bits.low32(Bits.signExtend(source & 0xffffL, 16)).toLong
    case ScalarUnaryOperation.CountBits32     => Bits.bitCount(source, 32).toLong
    case ScalarUnaryOperation.CountBits64     => Bits.bitCount(source, 64).toLong
    case ScalarUnaryOperation.ReverseBits32   => Bits.reverseBits(source, 32)
    case ScalarUnaryOperation.Not32           => Bits.low32(~source).toLong
    case ScalarUnaryOperation.Not64           => ~source
    case ScalarUnaryOperation.FirstBitHighI32 => signedFirstBitHigh32(source)
    case ScalarUnaryOperation.FirstBitHighI64 => signedFirstBitHigh64(source)
    case ScalarUnaryOperation.FirstBitHighU32 => firstBitHigh32(source)
    case ScalarUnaryOperation.FirstBitHighU64 =>
      if (source == 0L) 0xffffffffL else java.lang.Long.numberOfLeadingZeros(source).toLong
    case ScalarUnaryOperation.ClearBit32    => Bits.low32(destination & ~(1L << (source.toInt & 31))).toLong
    case ScalarUnaryOperation.SetBit32      => Bits.low32(destination | (1L << (source.toInt & 31))).toLong
    case ScalarUnaryOperation.ClearBit64    => destination & ~(1L << (source.toInt & 63))
    case ScalarUnaryOperation.SetBit64      => destination | (1L << (source.toInt & 63))
    case ScalarUnaryOperation.FirstOneLow32 =>
      if (Bits.low32(source).toLong == 0L) 0xffffffffL else java.lang.Long.numberOfTrailingZeros(source).toLong
    case ScalarUnaryOperation.FirstZeroLow32 =>
      val zeros = Bits.low32(~source).toLong
      if (zeros == 0L) 0xffffffffL else java.lang.Long.numberOfTrailingZeros(zeros).toLong
    case ScalarUnaryOperation.FirstOneLow64 =>
      if (source == 0L) 0xffffffffL else java.lang.Long.numberOfTrailingZeros(source).toLong
    case ScalarUnaryOperation.FirstZeroLow64 =>
      if (source == -1L) 0xffffffffL else java.lang.Long.numberOfTrailingZeros(~source).toLong
    case ScalarUnaryOperation.CountZeros32  => 32L - Bits.bitCount(source, 32)
    case ScalarUnaryOperation.CountZeros64  => 64L - Bits.bitCount(source, 64)
    case ScalarUnaryOperation.ReverseBits64 => java.lang.Long.reverse(source)
    case ScalarUnaryOperation.QuadMask32    => quadMask(source, 8)
    case ScalarUnaryOperation.QuadMask64    => quadMask(source, 16)
    case ScalarUnaryOperation.WholeQuad32   =>
      (0 until 8).foldLeft(0L)((mask, quad) =>
        mask | (if (((source >>> (quad * 4)) & 0xfL) != 0L) 0xfL << (quad * 4) else 0L)
      )
    case ScalarUnaryOperation.WholeQuad64 =>
      (0 until 16).foldLeft(0L)((mask, quad) =>
        mask | (if (((source >>> (quad * 4)) & 0xfL) != 0L) 0xfL << (quad * 4) else 0L)
      )
    case ScalarUnaryOperation.BitReplicate =>
      (0 until 32).foldLeft(0L)((result, bit) => result | (((source >>> bit) & 1L) * 3L << (bit * 2)))
  }

  private def quadMask(source: Long, quads: Int): Long = (0 until quads).foldLeft(0L)((mask, quad) =>
    mask | (if (((source >>> (quad * 4)) & 0xfL) != 0L) 1L << quad else 0L)
  )

  /** Whether a SOP1 operation sets SCC, from its result: CDNA3 ISA 12.3. */
  def scalarUnarySetsCarry(operation: ScalarUnaryOperation): Boolean = operation match {
    case ScalarUnaryOperation.Not32 | ScalarUnaryOperation.Not64 | ScalarUnaryOperation.AbsoluteI32 |
        ScalarUnaryOperation.CountBits32 | ScalarUnaryOperation.CountBits64 | ScalarUnaryOperation.CountZeros32 |
        ScalarUnaryOperation.CountZeros64 | ScalarUnaryOperation.QuadMask32 | ScalarUnaryOperation.QuadMask64 |
        ScalarUnaryOperation.WholeQuad32 | ScalarUnaryOperation.WholeQuad64 => true
    case _ => false
  }

  def scalarBinary64(operation: ScalarBinary64Operation, left: Long, right: Long): Long = operation match {
    case ScalarBinary64Operation.Multiply             => left * right
    case ScalarBinary64Operation.AddNoCarry           => left + right
    case ScalarBinary64Operation.SubtractNoCarry      => left - right
    case ScalarBinary64Operation.And                  => left & right
    case ScalarBinary64Operation.AndNot               => left & ~right
    case ScalarBinary64Operation.Or                   => left | right
    case ScalarBinary64Operation.OrNot                => left | ~right
    case ScalarBinary64Operation.Nor                  => ~(left | right)
    case ScalarBinary64Operation.Xor                  => left ^ right
    case ScalarBinary64Operation.ShiftLeft            => left << (right.toInt & 63)
    case ScalarBinary64Operation.ShiftRight           => left >>> (right.toInt & 63)
    case ScalarBinary64Operation.ShiftRightArithmetic => left >> (right.toInt & 63)
    case ScalarBinary64Operation.BitFieldExtractI64   =>
      val shift = (right & 0x3fL).toInt
      val width = math.min(((right >>> 16) & 0x7fL).toInt, 64)
      if (width == 0) 0L
      else {
        val bitWidth = width.unsafe[Int in 1 ~ 64]
        Bits.signExtend((left >> shift) & Bits.mask(bitWidth), bitWidth)
      }
    case ScalarBinary64Operation.BitFieldExtractU64 =>
      val shift = (right & 0x3fL).toInt
      val width = math.min(((right >>> 16) & 0x7fL).toInt, 64)
      if (width == 0) 0L else (left >>> shift) & Bits.mask(width.unsafe[Int in 1 ~ 64])
    case ScalarBinary64Operation.Nand         => ~(left & right)
    case ScalarBinary64Operation.Xnor         => ~(left ^ right)
    case ScalarBinary64Operation.BitFieldMask => ((1L << (left & 63L)) - 1L) << (right & 63L)
  }

  def dual(operation: DualVectorOperation, a: Long, b: Long, addend: Long, carry: Boolean, table: Int = 0): Long = {
    def float(value: Long): Float = java.lang.Float.intBitsToFloat(value.toInt)
    def bits(value: Float): Long  = java.lang.Float.floatToRawIntBits(value).toLong & Bits.Mask32
    val result                    = operation match {
      case DualVectorOperation.Move               => a
      case DualVectorOperation.AddF32             => bits(float(a) + float(b))
      case DualVectorOperation.SubtractF32        => bits(float(a) - float(b))
      case DualVectorOperation.ReverseSubtractF32 => bits(float(b) - float(a))
      case DualVectorOperation.MultiplyF32        => bits(float(a) * float(b))
      case DualVectorOperation.FmaF32             => bits(IEEE754.fma(float(a), float(b), float(addend)))
      case DualVectorOperation.MinimumF32         => IEEE754.minNum(a, b, 32)
      case DualVectorOperation.MaximumF32         => IEEE754.maxNum(a, b, 32)
      case DualVectorOperation.ConditionalMask    => if (carry) b else a
      case DualVectorOperation.AddU32             => a + b
      case DualVectorOperation.ShiftLeft32        => b << (a.toInt & 31)
      case DualVectorOperation.And32              => a & b
      case DualVectorOperation.MultiplyLegacyF32  =>
        if ((a & 0x7fffffffL) == 0L || (b & 0x7fffffffL) == 0L) 0L else bits(float(a) * float(b))
      case DualVectorOperation.DotHalvesF32        => dot2F32F16(a, b, addend).toLong
      case DualVectorOperation.DotAlignedHalvesF32 =>
        toRaw(Dots.halvesToFloatAligned(fromRaw[UInt[32]](a), fromRaw[UInt[32]](b), fromRaw[Float32](addend)))
      case DualVectorOperation.DotBFloat16F32 =>
        def bfloat(value: Long): Float = float((value & 0xffffL) << 16)
        bits(IEEE754.fma(bfloat(a), bfloat(b), bfloat(a >>> 16) * bfloat(b >>> 16)) + float(addend))
      case DualVectorOperation.SubtractU32            => a - b
      case DualVectorOperation.MinimumI32             => math.min(a.toInt, b.toInt).toLong
      case DualVectorOperation.MaximumI32             => math.max(a.toInt, b.toInt).toLong
      case DualVectorOperation.ShiftRight32           => (b & Bits.Mask32) >>> (a.toInt & 31)
      case DualVectorOperation.ShiftRightArithmetic32 => (b.toInt >> (a.toInt & 31)).toLong
      case DualVectorOperation.AddF64                 =>
        doubleBits(java.lang.Double.longBitsToDouble(a) + java.lang.Double.longBitsToDouble(b))
      case DualVectorOperation.MultiplyF64 =>
        doubleBits(java.lang.Double.longBitsToDouble(a) * java.lang.Double.longBitsToDouble(b))
      case DualVectorOperation.FmaF64 =>
        doubleBits(IEEE754.fma(
          java.lang.Double.longBitsToDouble(a),
          java.lang.Double.longBitsToDouble(b),
          java.lang.Double.longBitsToDouble(addend)
        ))
      case DualVectorOperation.MinimumF64    => IEEE754.minNum(a, b, 64)
      case DualVectorOperation.MaximumF64    => IEEE754.maxNum(a, b, 64)
      case DualVectorOperation.BitOperation2 => bitOperation3(a, b, 0L, table, half = false)
    }
    if (operation.wide) result else Bits.low32(result).toLong
  }

  private def doubleBits(value: Double): Long = java.lang.Double.doubleToRawLongBits(value)

  def bitOperation3(a: Long, b: Long, c: Long, table: Int, half: Boolean): Long = {
    var result = 0L
    var bit    = 0
    while (bit < 8) {
      if ((table & (1 << bit)) != 0)
        result |= (if ((bit & 4) != 0) a else ~a) &
          (if ((bit & 2) != 0) b else ~b) & (if ((bit & 1) != 0) c else ~c)
      bit += 1
    }
    if (half) Bits.low16(result).toLong else Bits.low32(result).toLong
  }

  /** V_PK_ADD_F32, V_PK_MUL_F32 and V_PK_FMA_F32 on one half, from the packed sources' 64 bits. */
  def packedSingle(operation: PackedF32Operation, left: Long, right: Long, third: Long, selection: Int, negate: Int)
      : Long = {
    val rawA   = IEEE754.binary32ToFloat(Bits.low32(left >>> ((selection & 1) * 32)))
    val rawB   = IEEE754.binary32ToFloat(Bits.low32(right >>> (((selection >>> 1) & 1) * 32)))
    val valueA = if ((negate & 1) != 0) -rawA else rawA
    val valueB = if ((negate & 2) != 0) -rawB else rawB
    val result = operation match {
      case PackedF32Operation.Add      => valueA + valueB
      case PackedF32Operation.Multiply => valueA * valueB
      case PackedF32Operation.Fma      =>
        val rawC   = IEEE754.binary32ToFloat(Bits.low32(third >>> (((selection >>> 2) & 1) * 32)))
        val valueC = if ((negate & 4) != 0) -rawC else rawC
        IEEE754.fma(valueA, valueB, valueC)
    }
    IEEE754.floatToBinary32(result).toLong
  }
}

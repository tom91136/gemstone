package net.kurobako.gemstone.isa.amdgpu

import VMArithmetic.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import scala.annotation.targetName

/** AMDGPU floating-point arithmetic, rounding and conversions on register bits, as execution computes them. Binary32
  * arithmetic rounds as the JVM does, and binary16 arithmetic rounds its binary32 result. Arithmetic on a NaN returns
  * the first NaN operand quieted.
  */
// conversions differing only in the destination, mirroring the V_CVT_* mnemonics
@SuppressWarnings(Array("baumkuchen:numbered-family", "baumkuchen:near-duplicate-code"))
private[isa] object Floats {
  private inline def bits[T](value: T): Long = toRaw[T](value)

  private inline def quieted(bits: Long, width: Int in (16, 32, 64)): Long =
    (bits & ((IEEE754.signBit(width) << 1) - 1L)) | IEEE754.quietBit(width)

  private inline def infinite(bits: Long, width: Int in (16, 32, 64)): Boolean =
    (bits & (IEEE754.signBit(width) - 1L)) == IEEE754.infinity(width)

  /** The NaN an invalid operation gives: the negative default NaN. */
  private inline def defaultNaN(width: Int in (16, 32, 64)): Long =
    IEEE754.signBit(width) | IEEE754.infinity(width) | IEEE754.quietBit(width)

  /** Whether the product of two `width`-bit floats is invalid: a zero times an infinity, whose default NaN an addend's
    * NaN does not replace.
    */
  private def invalidProduct(a: Long, b: Long, width: Int in (16, 32, 64)): Boolean = {
    def zero(x: Long): Boolean = (x & (IEEE754.signBit(width) - 1L)) == 0L
    (zero(a) && infinite(b, width)) || (infinite(a, width) && zero(b))
  }

  // compiled code may order a commutative operation's operands either way, so which of two NaN operands the JVM
  // returns varies; it returns a lone NaN operand quieted, so the last operand needs no check
  private inline def singles(left: Float32, right: Float32)(inline operation: (Float, Float) => Float): Float32 = {
    val x = left.toFloat
    if (x != x) fromRaw[Float32](quieted(bits(left), 32)) else Float32(operation(x, right.toFloat))
  }

  private inline def doubles(left: Float64, right: Float64)(inline operation: (Double, Double) => Double): Float64 = {
    val x = left.toDouble
    if (x != x) fromRaw[Float64](quieted(bits(left), 64)) else Float64(operation(x, right.toDouble))
  }

  private inline def halves(left: Float16, right: Float16)(inline operation: (Float, Float) => Float): Float16 = {
    val x = left.toFloat
    if (x != x) fromRaw[Float16](quieted(bits(left), 16)) else Float16(operation(x, right.toFloat))
  }

  @targetName("addF32")
  def add(left: Float32, right: Float32): Float32 = singles(left, right)(_ + _)
  @targetName("subF32")
  def sub(left: Float32, right: Float32): Float32 = singles(left, right)(_ - _)
  @targetName("mulF32")
  def mul(left: Float32, right: Float32): Float32 = singles(left, right)(_ * _)

  /** The DX9 multiply: a zero operand gives +0 whatever the other is. */
  def mulLegacyF32(left: Float32, right: Float32): Float32 =
    if ((bits(left) & 0x7fffffffL) == 0L || (bits(right) & 0x7fffffffL) == 0L) Float32(0.0f) else mul(left, right)
  @targetName("addF16")
  def add(left: Float16, right: Float16): Float16 = halves(left, right)(_ + _)
  @targetName("subF16")
  def sub(left: Float16, right: Float16): Float16 =
    // the hardware subtracts by adding the negated right operand, whose NaN propagates negated
    if (IEEE754.isNaN(bits(left), 16)) fromRaw[Float16](quieted(bits(left), 16))
    else if (IEEE754.isNaN(bits(right), 16)) fromRaw[Float16](quieted(bits(right) ^ 0x8000L, 16))
    else Float16(left.toFloat - right.toFloat)
  @targetName("mulF16")
  def mul(left: Float16, right: Float16): Float16 = halves(left, right)(_ * _)
  @targetName("addF64")
  def add(left: Float64, right: Float64): Float64 = doubles(left, right)(_ + _)
  @targetName("mulF64")
  def mul(left: Float64, right: Float64): Float64 = doubles(left, right)(_ * _)

  @targetName("fmaF16")
  def fma(a: Float16, b: Float16, c: Float16): Float16 = fromRaw[Float16](
    if (IEEE754.isNaN(bits(a), 16)) quieted(bits(a), 16)
    else if (IEEE754.isNaN(bits(b), 16)) quieted(bits(b), 16)
    else if (invalidProduct(bits(a), bits(b), 16)) defaultNaN(16)
    else IEEE754.fusedMultiplyAdd(bits(a), bits(b), bits(c), 16)
  )

  @targetName("fmaF32")
  def fma(a: Float32, b: Float32, c: Float32): Float32 = {
    val (x, y) = (a.toFloat, b.toFloat)
    if (x != x) fromRaw[Float32](quieted(bits(a), 32))
    else if (y != y) fromRaw[Float32](quieted(bits(b), 32))
    else if (invalidProduct(bits(a), bits(b), 32)) fromRaw[Float32](defaultNaN(32))
    else if (IEEE754.isNaN(bits(c), 32)) fromRaw[Float32](quieted(bits(c), 32))
    else Float32(IEEE754.fma(x, y, c.toFloat))
  }

  /** V_FMA_LEGACY_F32: [[fma]] under DX9 rules, the product of a zero and anything +0. */
  def fmaLegacyF32(a: Float32, b: Float32, c: Float32): Float32 =
    if ((bits(a) & 0x7fffffffL) == 0L || (bits(b) & 0x7fffffffL) == 0L) add(Float32(0.0f), c) else fma(a, b, c)

  @targetName("fmaF64")
  def fma(a: Float64, b: Float64, c: Float64): Float64 = {
    val (x, y) = (a.toDouble, b.toDouble)
    if (x != x) fromRaw[Float64](quieted(bits(a), 64))
    else if (y != y) fromRaw[Float64](quieted(bits(b), 64))
    else if (invalidProduct(bits(a), bits(b), 64)) fromRaw[Float64](defaultNaN(64))
    else if (IEEE754.isNaN(bits(c), 64)) fromRaw[Float64](quieted(bits(c), 64))
    else Float64(IEEE754.fma(x, y, c.toDouble))
  }

  /** V_MED3: the median, or the minimum when any operand is NaN, where a signalling NaN propagates quieted and a quiet
    * one is ignored. A denormal compares as a zero unless MODE keeps input denormals of its width, ties keep the
    * earlier operand, and the operand picked is returned as it is.
    */
  @targetName("med3F32")
  def med3(a: Float32, b: Float32, c: Float32, mode: UInt[32]): Float32 =
    fromRaw[Float32](median(bits(a), bits(b), bits(c), 32, (bits(mode) & ModeRegister.Denormal32Input) == 0))
  @targetName("med3F16")
  def med3(a: Float16, b: Float16, c: Float16, mode: UInt[32]): Float16 =
    fromRaw[Float16](median(bits(a), bits(b), bits(c), 16, (bits(mode) & ModeRegister.Denormal16And64Input) == 0))

  private def median(x: Long, y: Long, z: Long, width: Int in (16, 32, 64), flush: Boolean): Long = {
    val exponent                         = IEEE754.infinity(width)
    val mask                             = (IEEE754.signBit(width) << 1) - 1L
    val (a, b, c)                        = (x & mask, y & mask, z & mask)
    def nan(x: Long): Boolean            = IEEE754.isNaN(x, width)
    def value(x: Long): Double           = if (flush && (x & exponent) == 0L) 0.0 else IEEE754.toDouble(x, width)
    def below(x: Long, y: Long): Boolean = {
      val (u, v) = (value(x), value(y))
      u < v ||
      (u == 0.0 && v == 0.0 && (x & IEEE754.signBit(width)) != 0L && (y & IEEE754.signBit(width)) == 0L)
    }
    def larger(x: Long, y: Long): Long  = if (below(x, y)) y else x
    def smaller(x: Long, y: Long): Long =
      if (nan(x) && (x & IEEE754.quietBit(width)) == 0L) quieted(x, width)
      else if (nan(y) && (y & IEEE754.quietBit(width)) == 0L) quieted(y, width)
      else if (nan(x) && !nan(y)) y
      else if (nan(x) || nan(y) || !below(y, x)) x
      else y
    if (nan(a) || nan(b) || nan(c)) smaller(smaller(a, b), c)
    else {
      val largest = value(larger(larger(a, b), c))
      if (largest == value(a)) larger(b, c) else if (largest == value(b)) larger(a, c) else larger(a, b)
    }
  }

  /** V_CVT_PKRTZ: two binary32 values rounded toward zero into the halves of a dword. */
  def cvtPkrtzF16F32(low: Float32, high: Float32): UInt[32] = fromRaw[UInt[32]](
    IEEE754.fromDouble(low.toDouble, 16, IEEE754.Rounding.TowardZero) |
      (IEEE754.fromDouble(high.toDouble, 16, IEEE754.Rounding.TowardZero) << 16)
  )

  /** LDEXP: the value scaled by two to the power of the exponent; a NaN quieted, with its sign and payload, which the
    * host's scaling need not keep.
    */
  @targetName("ldexpF32")
  def ldexp(value: Float32, exponent: SInt[32]): Float32 =
    if (IEEE754.isNaN(bits(value), 32)) fromRaw[Float32](quieted(bits(value), 32))
    else Float32(Math.scalb(value.toFloat, exponent.raw.toInt))
  @targetName("ldexpF64")
  def ldexp(value: Float64, exponent: SInt[32]): Float64 =
    if (IEEE754.isNaN(bits(value), 64)) fromRaw[Float64](quieted(bits(value), 64))
    else Float64(Math.scalb(value.toDouble, exponent.raw.toInt))
  @targetName("ldexpF16")
  def ldexp(value: Float16, exponent: SInt[16]): Float16 =
    if (IEEE754.isNaN(bits(value), 16)) fromRaw[Float16](quieted(bits(value), 16))
    else Float16(Math.scalb(value.toDouble, exponent.raw.toShort.toInt).toFloat)

  /** The binary32 transcendental unit reads denormals as zeros whatever MODE says. */
  private inline def flushed32(value: Float32): Float =
    if ((bits(value) & 0x7f800000L) == 0L) Math.copySign(0.0f, value.toFloat) else value.toFloat

  @targetName("rcpF32")
  def rcp(value: Float32): Float32 = fromRaw[Float32](TranscendentalUnit.reciprocal32(bits(value)))
  @targetName("rcpF64")
  def rcp(value: Float64): Float64 = fromRaw[Float64](TranscendentalUnit.reciprocal64(bits(value)))
  @targetName("rsqF32")
  def rsq(value: Float32): Float32 = fromRaw[Float32](TranscendentalUnit.reciprocalSquareRoot32(bits(value)))
  @targetName("rsqF64")
  def rsq(value: Float64): Float64 = fromRaw[Float64](TranscendentalUnit.reciprocalSquareRoot64(bits(value)))
  @targetName("sqrtF32")
  def sqrt(value: Float32): Float32 = fromRaw[Float32](TranscendentalUnit.squareRoot32(bits(value)))
  @targetName("exp2F32")
  def exp2(value: Float32): Float32 = fromRaw[Float32](TranscendentalUnit.powerOfTwo32(bits(value)))
  @targetName("log2F32")
  def log2(value: Float32): Float32 = fromRaw[Float32](TranscendentalUnit.logarithm32(bits(value)))

  private inline def bounded(value: Float): Float =
    if (value == Float.PositiveInfinity) Float.MaxValue
    else if (value == Float.NegativeInfinity) -Float.MaxValue
    else value
  private inline def bounded(value: Double): Double =
    if (value == Double.PositiveInfinity) Double.MaxValue
    else if (value == Double.NegativeInfinity) -Double.MaxValue
    else value
  private inline def flushed(value: Float): Float = if (value.isInfinite) Math.copySign(0.0f, value) else value

  /** V_RCP_CLAMP and V_RSQ_CLAMP: infinite results bound to the largest finite value of their sign. */
  @targetName("rcpClampF32")
  def rcpClamp(value: Float32): Float32 = Float32(bounded(rcp(value).toFloat))
  @targetName("rcpClampF64")
  def rcpClamp(value: Float64): Float64 = Float64(bounded(rcp(value).toDouble))
  @targetName("rsqClampF32")
  def rsqClamp(value: Float32): Float32 = Float32(bounded(rsq(value).toFloat))
  @targetName("rsqClampF64")
  def rsqClamp(value: Float64): Float64 = Float64(bounded(rsq(value).toDouble))

  /** V_RCP_LEGACY and V_RSQ_LEGACY: a zero gives +0 and other infinite results flush to a zero of their sign. */
  def rcpLegacyF32(value: Float32): Float32 =
    if (flushed32(value) == 0.0f) Float32(0.0f) else Float32(flushed(rcp(value).toFloat))
  def rsqLegacyF32(value: Float32): Float32 =
    if (flushed32(value) == 0.0f) Float32(0.0f) else Float32(flushed(rsq(value).toFloat))

  /** V_LOG_CLAMP: log2 with a -infinity result bound to -max_float. */
  def logClampF32(value: Float32): Float32 = {
    val result = log2(value).toFloat
    Float32(if (result == Float.NegativeInfinity) -Float.MaxValue else result)
  }

  /** V_MULLIT, R600's MUL_LIT: the DX9 product, or -max_float where the exponent `right` is -max_float, -infinity or
    * NaN or the dot product `select` is NaN or not positive.
    */
  def mullitF32(left: Float32, right: Float32, select: Float32): Float32 = {
    val (y, z) = (right.toFloat, select.toFloat)
    if (y == -Float.MaxValue || y == Float.NegativeInfinity || y.isNaN || !(z > 0.0f)) Float32(-Float.MaxValue)
    else mulLegacyF32(left, right)
  }

  /** V_MIN_LEGACY and V_MAX_LEGACY, DX9's selections: SRC0 where it compares less, or not less, else SRC1. */
  def minLegacyF32(left: Float32, right: Float32): Float32 = if (left.toFloat < right.toFloat) left else right
  def maxLegacyF32(left: Float32, right: Float32): Float32 = if (left.toFloat >= right.toFloat) left else right

  /** The sine of the operand in turns: one unit is 2 * pi radians. */
  @targetName("sinF32")
  def sin(value: Float32): Float32 = fromRaw[Float32](TranscendentalUnit.sine32(bits(value)))
  @targetName("cosF32")
  def cos(value: Float32): Float32 = fromRaw[Float32](TranscendentalUnit.cosine32(bits(value)))

  private inline def beyond(value: Float32): Boolean = {
    val magnitude = bits(value) & 0x7fffffffL
    magnitude > 0x43800000L && magnitude < 0x7f800000L
  }

  /** GFX8's sine and cosine, +0 and 1 for finite operands beyond 256 turns. */
  @targetName("sinBoundedF32")
  def sinBounded(value: Float32): Float32 = if (beyond(value)) Float32(0.0f) else sin(value)
  @targetName("cosBoundedF32")
  def cosBounded(value: Float32): Float32 = if (beyond(value)) Float32(1.0f) else cos(value)

  /** The integral roundings return a NaN quieted, with its sign and payload, which the JVM's need not keep. */
  private inline def integral32(value: Float32)(inline operation: Double => Double): Float32 =
    if (IEEE754.isNaN(bits(value), 32)) fromRaw[Float32](quieted(bits(value), 32))
    else Float32(operation(value.toDouble).toFloat)
  private inline def integral64(value: Float64)(inline operation: Double => Double): Float64 =
    if (IEEE754.isNaN(bits(value), 64)) fromRaw[Float64](quieted(bits(value), 64))
    else Float64(operation(value.toDouble))

  @targetName("ceilF32")
  def ceil(value: Float32): Float32 = integral32(value)(math.ceil)
  @targetName("ceilF64")
  def ceil(value: Float64): Float64 = integral64(value)(math.ceil)
  @targetName("sqrtF64")
  def sqrt(value: Float64): Float64 = fromRaw[Float64](TranscendentalUnit.squareRoot64(bits(value)))

  /** Conversions to integers that round down, or up from halfway, then saturate. */
  def cvtFloorI32F32(value: Float32): UInt[32] = fromRaw[UInt[32]](
    IEEE754.toIntegerSaturated(math.floor(value.toDouble), 32, true, IEEE754.Rounding.TowardZero)
  )
  def cvtNearestI32F32(value: Float32): UInt[32] = fromRaw[UInt[32]](
    IEEE754.toIntegerSaturated(math.floor(value.toDouble + 0.5), 32, true, IEEE754.Rounding.TowardZero)
  )

  /** Binary16 operations computed in binary64, which holds every binary16 result before its one rounding. */
  private inline def halved(value: Float16)(inline operation: Double => Double): Float16 =
    if (IEEE754.isNaN(bits(value), 16)) fromRaw[Float16](quieted(bits(value), 16))
    else Float16(operation(value.toDouble))

  /** Binary16 operations the transcendental unit evaluates as binary32, rounding the binary32 result. */
  private inline def narrowed(value: Float16)(inline operation: Float32 => Float32): Float16 =
    if (IEEE754.isNaN(bits(value), 16)) fromRaw[Float16](quieted(bits(value), 16))
    else Float16(operation(Float32(value.toFloat)).toFloat)

  @targetName("ceilF16")
  def ceil(value: Float16): Float16 = halved(value)(math.ceil)
  @targetName("floorF16")
  def floor(value: Float16): Float16 = halved(value)(math.floor)
  @targetName("truncF16")
  def trunc(value: Float16): Float16 = halved(value)(IEEE754.integral(_, IEEE754.Rounding.TowardZero))
  @targetName("rndneF16")
  def rndne(value: Float16): Float16 = halved(value)(Math.rint(_))

  /** The fractional part, below the largest binary16 under one; an infinity gives the default NaN. */
  @targetName("fractF16")
  def fract(value: Float16): Float16 =
    if (infinite(bits(value), 16)) fromRaw[Float16](defaultNaN(16))
    else halved(value)(x => math.min(x - math.floor(x), 0.99951171875))
  @targetName("sqrtF16")
  def sqrt(value: Float16): Float16 = halved(value)(math.sqrt)
  @targetName("rcpF16")
  def rcp(value: Float16): Float16 = halved(value)(1.0 / _)
  @targetName("rsqF16")
  def rsq(value: Float16): Float16 = halved(value)(x => 1.0 / math.sqrt(x))
  @targetName("exp2F16")
  def exp2(value: Float16): Float16 = halved(value)(math.pow(2.0, _))
  @targetName("log2F16")
  def log2(value: Float16): Float16 = narrowed(value)(log2(_: Float32))
  @targetName("sinF16")
  def sin(value: Float16): Float16 = narrowed(value)(sin(_: Float32))
  @targetName("cosF16")
  def cos(value: Float16): Float16 = narrowed(value)(cos(_: Float32))
  @targetName("sinBoundedF16")
  def sinBounded(value: Float16): Float16 = narrowed(value)(sinBounded(_: Float32))
  @targetName("cosBoundedF16")
  def cosBounded(value: Float16): Float16 = narrowed(value)(cosBounded(_: Float32))

  /** FREXP on binary16: the significand of a finite non-zero value in [0.5, 1), other values passing, and its exponent,
    * zero for zero, infinity and NaN.
    */
  @targetName("frexpMantF16")
  def frexpMant(value: Float16): Float16 = {
    val x = value.toFloat
    Float16(if (x == 0.0f || x.isNaN || x.isInfinite) x else Math.scalb(x, -Math.getExponent(x) - 1))
  }
  def frexpExpF16(value: Float16): UInt[16] = {
    val x = value.toFloat
    fromRaw[UInt[16]](
      if (x == 0.0f || x.isNaN || x.isInfinite) 0L else Bits.low16(Math.getExponent(x) + 1L).toLong
    )
  }

  def cvtF16I16(value: SInt[16]): Float16 = Float16(value.raw.toShort.toFloat)
  def cvtF16U16(value: UInt[16]): Float16 = Float16(Bits.low16(value.raw).toLong.toFloat)
  def cvtU16F16(value: Float16): UInt[16] = fromRaw[UInt[16]](
    IEEE754.toIntegerSaturated(value.toDouble, 16, false, IEEE754.Rounding.TowardZero)
  )

  @targetName("truncF32")
  def trunc(value: Float32): Float32 = integral32(value)(IEEE754.integral(_, IEEE754.Rounding.TowardZero))
  @targetName("truncF64")
  def trunc(value: Float64): Float64 = integral64(value)(IEEE754.integral(_, IEEE754.Rounding.TowardZero))
  @targetName("floorF32")
  def floor(value: Float32): Float32 = integral32(value)(math.floor)
  @targetName("floorF64")
  def floor(value: Float64): Float64 = integral64(value)(math.floor)
  @targetName("rndneF32")
  def rndne(value: Float32): Float32 = integral32(value)(Math.rint(_))
  @targetName("rndneF64")
  def rndne(value: Float64): Float64 = integral64(value)(Math.rint(_))

  /** The fractional part, below the largest value under one; an infinity gives the default NaN, which the host's
    * subtraction need not.
    */
  @targetName("fractF32")
  def fract(value: Float32): Float32 =
    if (infinite(bits(value), 32)) fromRaw[Float32](defaultNaN(32))
    else integral32(value)(x => math.min(x - math.floor(x), Math.nextDown(1.0f).toDouble))
  @targetName("fractF64")
  def fract(value: Float64): Float64 =
    if (infinite(bits(value), 64)) fromRaw[Float64](defaultNaN(64))
    else integral64(value)(x => math.min(x - math.floor(x), Math.nextDown(1.0)))

  /** FREXP_MANT: the significand of a finite non-zero value, denormals included, as a fraction in [0.5, 1); a NaN
    * returns quieted and other values pass.
    */
  @targetName("frexpMantF32")
  def frexpMant(value: Float32): Float32 = {
    val x = value.toDouble
    if (x.isNaN) fromRaw[Float32](quieted(bits(value), 32))
    else Float32((if (x == 0.0 || x.isInfinite) x else Math.scalb(x, -Math.getExponent(x) - 1)).toFloat)
  }
  @targetName("frexpMantF64")
  def frexpMant(value: Float64): Float64 = {
    val x = value.toDouble
    if (x.isNaN) fromRaw[Float64](quieted(bits(value), 64))
    else if (x == 0.0 || x.isInfinite) value
    else {
      // a denormal's exponent reads from its value scaled into the normal range
      val exponent =
        if (Math.getExponent(x) < java.lang.Double.MIN_EXPONENT) Math.getExponent(Math.scalb(x, 64)) - 64
        else Math.getExponent(x)
      Float64(Math.scalb(x, -exponent - 1))
    }
  }

  /** Conversions to integers saturate, take NaN to zero and return the two's-complement bits. */
  def cvtI32F32(value: Float32): UInt[32] = fromRaw[UInt[32]](signedSaturated(value.toDouble, 32))
  def cvtU32F32(value: Float32): UInt[32] = fromRaw[UInt[32]](unsignedSaturated(value.toDouble, 32))
  def cvtI32F64(value: Float64): UInt[32] = fromRaw[UInt[32]](signedSaturated(value.toDouble, 32))
  def cvtU32F64(value: Float64): UInt[32] = fromRaw[UInt[32]](unsignedSaturated(value.toDouble, 32))
  def cvtI16F16(value: Float16): UInt[16] = fromRaw[UInt[16]](signedSaturated(value.toDouble, 16))

  private def signedSaturated(value: Double, width: Int in 1 ~ 64): Long =
    IEEE754.toIntegerSaturated(value, width, signed = true, IEEE754.Rounding.TowardZero)

  private def unsignedSaturated(value: Double, width: Int in 1 ~ 64): Long =
    IEEE754.toIntegerSaturated(value, width, signed = false, IEEE754.Rounding.TowardZero)

  def cvtF32U32(value: UInt[32]): Float32 = Float32(Bits.low32(value.raw).toLong.toFloat)
  def cvtF32I32(value: SInt[32]): Float32 = Float32(value.raw.toInt.toFloat)
  def cvtF64U32(value: UInt[32]): Float64 = Float64(Bits.low32(value.raw).toLong.toDouble)
  def cvtF64I32(value: SInt[32]): Float64 = Float64(value.raw.toInt.toDouble)
  def cvtF32F16(value: Float16): Float32  = {
    val raw = bits(value)
    // the hardware quiets a signalling NaN as it widens it, keeping its sign and payload
    if (IEEE754.isNaN(raw, 16))
      fromRaw[Float32](
        (java.lang.Float.floatToRawIntBits(IEEE754.binary16ToFloat(Binary16.low(raw))).toLong & 0xffffffffL) |
          IEEE754.quietBit(32)
      )
    else Float32(value.toFloat)
  }
  def cvtF16F32(value: Float32): Float16 = Float16(value.toFloat)
  def cvtF32F64(value: Float64): Float32 = Float32(value.toDouble.toFloat)
  def cvtF64F32(value: Float32): Float64 = Float64(value.toDouble)

  /** Ordered comparisons, false when either operand is NaN. */
  @targetName("ltF16")
  def lt(left: Float16, right: Float16): Bool = left.toFloat < right.toFloat
  @targetName("ltF32")
  def lt(left: Float32, right: Float32): Bool = left.toFloat < right.toFloat
  @targetName("ltF64")
  def lt(left: Float64, right: Float64): Bool = left.toDouble < right.toDouble
  @targetName("leF16")
  def le(left: Float16, right: Float16): Bool = left.toFloat <= right.toFloat
  @targetName("leF32")
  def le(left: Float32, right: Float32): Bool = left.toFloat <= right.toFloat
  @targetName("leF64")
  def le(left: Float64, right: Float64): Bool = left.toDouble <= right.toDouble
  @targetName("eqF16")
  def eq(left: Float16, right: Float16): Bool = left.toFloat == right.toFloat
  @targetName("eqF32")
  def eq(left: Float32, right: Float32): Bool = left.toFloat == right.toFloat
  @targetName("eqF64")
  def eq(left: Float64, right: Float64): Bool = left.toDouble == right.toDouble

  /** V_DIV_FIXUP on the quotient, denominator and numerator. */
  @targetName("divFixupF32")
  def divFixup(quotient: Float32, denominator: Float32, numerator: Float32): Float32 =
    fromRaw[Float32](divisionFixup(bits(quotient), bits(denominator), bits(numerator), 32))
  @targetName("divFixupF16")
  def divFixup(quotient: Float16, denominator: Float16, numerator: Float16): Float16 =
    fromRaw[Float16](divisionFixup(bits(quotient), bits(denominator), bits(numerator), 16))
  @targetName("divFixupF64")
  def divFixup(quotient: Float64, denominator: Float64, numerator: Float64): Float64 =
    fromRaw[Float64](divisionFixup(bits(quotient), bits(denominator), bits(numerator), 64))

  /** V_DIV_SCALE: the operand pre-scaled away from the denormal range, given the denominator and numerator. */
  @targetName("divScaleF32")
  def divScale(value: Float32, denominator: Float32, numerator: Float32): Float32 =
    fromRaw[Float32](divisionScale(bits(value), bits(denominator), bits(numerator), 32))
  @targetName("divScaleF64")
  def divScale(value: Float64, denominator: Float64, numerator: Float64): Float64 =
    fromRaw[Float64](divisionScale(bits(value), bits(denominator), bits(numerator), 64))

  /** The flag V_DIV_SCALE leaves for V_DIV_FMAS. */
  @targetName("divScaleFlagF32")
  def divScaleFlag(denominator: Float32, numerator: Float32): Bool =
    divisionScaleCondition(bits(denominator), bits(numerator), 32)
  @targetName("divScaleFlagF64")
  def divScaleFlag(denominator: Float64, numerator: Float64): Bool =
    divisionScaleCondition(bits(denominator), bits(numerator), 64)

  /** V_DIV_FMAS: the fused multiply-add, rescaled once where V_DIV_SCALE left its flag and every operand is finite. */
  @targetName("divFmasF32")
  def divFmas(a: Float32, b: Float32, c: Float32, scaled: Bool): Float32 =
    if (scaled && a.toFloat.isFinite && b.toFloat.isFinite && c.toFloat.isFinite)
      fromRaw[Float32](divisionFusedMultiplyAdd(bits(a), bits(b), bits(c), 32))
    else fma(a, b, c)
  @targetName("divFmasF64")
  def divFmas(a: Float64, b: Float64, c: Float64, scaled: Bool): Float64 =
    if (scaled && a.toDouble.isFinite && b.toDouble.isFinite && c.toDouble.isFinite)
      fromRaw[Float64](divisionFusedMultiplyAdd(bits(a), bits(b), bits(c), 64))
    else fma(a, b, c)

  /** V_TRIG_PREOP_F64: the segment of 2/pi the dword selects, scaled for the operand's exponent. */
  def trigPreopF64(value: Float64, segment: UInt[32]): Float64 = Float64(trigPreop(value.toDouble, segment.raw.toInt))

  /** A binary32 NaN as the binary16 NaN it narrows to. */
  private def narrowed(bits: Long): Long = Float16(IEEE754.binary32ToFloat(Bits.low32(bits))).bits.raw

  private inline def mixed(a: Long, b: Long, c: Long)(value: => Long): Long =
    if (IEEE754.isNaN(a, 32)) narrowed(quieted(a, 32))
    else if (IEEE754.isNaN(b, 32)) narrowed(quieted(b, 32))
    else if (IEEE754.isNaN(c, 32)) narrowed(quieted(c, 32))
    else value

  /** V_FMA_MIX into binary16: the binary32 operands' fused multiply-add, rounded once. */
  def fmaMixF16(a: Float32, b: Float32, c: Float32): Float16 = fromRaw[Float16](
    if (IEEE754.isNaN(a.bits.raw, 32)) narrowed(quieted(a.bits.raw, 32))
    else if (IEEE754.isNaN(b.bits.raw, 32)) narrowed(quieted(b.bits.raw, 32))
    else if (invalidProduct(a.bits.raw, b.bits.raw, 32)) narrowed(defaultNaN(32))
    else if (IEEE754.isNaN(c.bits.raw, 32)) narrowed(quieted(c.bits.raw, 32))
    else IEEE754.fusedMultiplyAddExact(
      left = a.toDouble,
      right = b.toDouble,
      addend = c.toDouble,
      width = 16,
      rounding = IEEE754.Rounding.NearestEven
    )
  )

  /** V_MAD_MIX into binary16: a rounded binary32 product plus the addend, rounded once. */
  def madMixF16(product: Float32, addend: Float32): Float16 = fromRaw[Float16](
    mixed(product.bits.raw, addend.bits.raw, 0L)(
      IEEE754.fusedMultiplyAddExact(
        left = product.toDouble,
        right = 1.0,
        addend = addend.toDouble,
        width = 16,
        rounding = IEEE754.Rounding.NearestEven
      )
    )
  )

  /** VOP3 OMOD scaling by `factor` with denormals flushed. */
  @targetName("omodF16")
  def omod(value: Float16, factor: Double in (0.5, 2.0, 4.0)): Float16 =
    fromRaw[Float16](outputModified(value.bits.raw, factor, 16))
  @targetName("omodF32")
  def omod(value: Float32, factor: Double in (0.5, 2.0, 4.0)): Float32 =
    fromRaw[Float32](outputModified(value.bits.raw, factor, 32))
  @targetName("omodF64")
  def omod(value: Float64, factor: Double in (0.5, 2.0, 4.0)): Float64 =
    fromRaw[Float64](outputModified(value.bits.raw, factor, 64))
}

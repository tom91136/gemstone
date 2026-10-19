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
  def sub(left: Float16, right: Float16): Float16 = halves(left, right)(_ - _)
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
    else IEEE754.fusedMultiplyAdd(bits(a), bits(b), bits(c), 16)
  )

  @targetName("fmaF32")
  def fma(a: Float32, b: Float32, c: Float32): Float32 = {
    val (x, y) = (a.toFloat, b.toFloat)
    if (x != x) fromRaw[Float32](quieted(bits(a), 32))
    else if (y != y) fromRaw[Float32](quieted(bits(b), 32))
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
    else Float64(IEEE754.fma(x, y, c.toDouble))
  }

  /** V_MED3: the median, or the minimum when any operand is NaN. */
  @targetName("med3F32")
  def med3(a: Float32, b: Float32, c: Float32): Float32 =
    median(a, b, c, Float32.maxNum(_, _), Float32.minNum(_, _), _.toFloat)
  @targetName("med3F16")
  def med3(a: Float16, b: Float16, c: Float16): Float16 =
    median(a, b, c, Float16.maxNum(_, _), Float16.minNum(_, _), _.toFloat)

  private inline def median[A](
      a: A,
      b: A,
      c: A,
      inline max: (A, A) => A,
      inline min: (A, A) => A,
      inline toFloat: A => Float
  ): A =
    if (toFloat(a).isNaN || toFloat(b).isNaN || toFloat(c).isNaN) min(min(a, b), c)
    else {
      val largest = toFloat(max(max(a, b), c))
      if (largest == toFloat(a)) max(b, c) else if (largest == toFloat(b)) max(a, c) else max(a, b)
    }

  /** V_CVT_PKRTZ: two binary32 values rounded toward zero into the halves of a dword. */
  def cvtPkrtzF16F32(low: Float32, high: Float32): UInt[32] = fromRaw[UInt[32]](
    IEEE754.fromDouble(low.toDouble, 16, IEEE754.Rounding.TowardZero) |
      (IEEE754.fromDouble(high.toDouble, 16, IEEE754.Rounding.TowardZero) << 16)
  )

  /** LDEXP: the value scaled by two to the power of the exponent. */
  @targetName("ldexpF32")
  def ldexp(value: Float32, exponent: SInt[32]): Float32 = Float32(Math.scalb(value.toFloat, exponent.raw.toInt))
  @targetName("ldexpF64")
  def ldexp(value: Float64, exponent: SInt[32]): Float64 = Float64(Math.scalb(value.toDouble, exponent.raw.toInt))
  @targetName("ldexpF16")
  def ldexp(value: Float16, exponent: SInt[16]): Float16 =
    Float16(Math.scalb(value.toDouble, exponent.raw.toShort.toInt).toFloat)

  @targetName("rcpF32")
  def rcp(value: Float32): Float32 = Float32(1.0f / value.toFloat)
  @targetName("rcpF64")
  def rcp(value: Float64): Float64 = Float64(1.0 / value.toDouble)
  @targetName("rsqF32")
  def rsq(value: Float32): Float32 = Float32((1.0 / math.sqrt(value.toDouble)).toFloat)
  @targetName("rsqF64")
  def rsq(value: Float64): Float64 = Float64(1.0 / math.sqrt(value.toDouble))
  @targetName("sqrtF32")
  def sqrt(value: Float32): Float32 = Float32(math.sqrt(value.toDouble).toFloat)
  @targetName("exp2F32")
  def exp2(value: Float32): Float32 = Float32(math.pow(2.0, value.toDouble).toFloat)
  @targetName("log2F32")
  def log2(value: Float32): Float32 = Float32((math.log(value.toDouble) / math.log(2.0)).toFloat)

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
    if (value.toFloat == 0.0f) Float32(0.0f) else Float32(flushed(rcp(value).toFloat))
  def rsqLegacyF32(value: Float32): Float32 =
    if (value.toFloat == 0.0f) Float32(0.0f) else Float32(flushed(rsq(value).toFloat))

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
  def sin(value: Float32): Float32 = Float32(math.sin(2.0 * math.Pi * value.toDouble).toFloat)
  @targetName("cosF32")
  def cos(value: Float32): Float32 = Float32(math.cos(2.0 * math.Pi * value.toDouble).toFloat)

  @targetName("ceilF32")
  def ceil(value: Float32): Float32 = Float32(math.ceil(value.toDouble).toFloat)
  @targetName("ceilF64")
  def ceil(value: Float64): Float64 = Float64(math.ceil(value.toDouble))
  @targetName("sqrtF64")
  def sqrt(value: Float64): Float64 = Float64(math.sqrt(value.toDouble))

  /** Conversions to integers that round down, or up from halfway, then saturate. */
  def cvtFloorI32F32(value: Float32): UInt[32] = fromRaw[UInt[32]](
    IEEE754.toIntegerSaturated(math.floor(value.toDouble), 32, true, IEEE754.Rounding.TowardZero)
  )
  def cvtNearestI32F32(value: Float32): UInt[32] = fromRaw[UInt[32]](
    IEEE754.toIntegerSaturated(math.floor(value.toDouble + 0.5), 32, true, IEEE754.Rounding.TowardZero)
  )

  /** Binary16 operations computed in binary64, which holds every binary16 result before its one rounding. */
  private inline def halved(value: Float16)(inline operation: Double => Double): Float16 =
    Float16(operation(value.toDouble).toFloat)

  @targetName("ceilF16")
  def ceil(value: Float16): Float16 = halved(value)(math.ceil)
  @targetName("floorF16")
  def floor(value: Float16): Float16 = halved(value)(math.floor)
  @targetName("truncF16")
  def trunc(value: Float16): Float16 = halved(value)(IEEE754.integral(_, IEEE754.Rounding.TowardZero))
  @targetName("rndneF16")
  def rndne(value: Float16): Float16 = halved(value)(Math.rint(_))

  /** The fractional part, below the largest binary16 under one. */
  @targetName("fractF16")
  def fract(value: Float16): Float16 = halved(value)(x => math.min(x - math.floor(x), 0.99951171875))
  @targetName("sqrtF16")
  def sqrt(value: Float16): Float16 = halved(value)(math.sqrt)
  @targetName("rcpF16")
  def rcp(value: Float16): Float16 = halved(value)(1.0 / _)
  @targetName("rsqF16")
  def rsq(value: Float16): Float16 = halved(value)(x => 1.0 / math.sqrt(x))
  @targetName("exp2F16")
  def exp2(value: Float16): Float16 = halved(value)(math.pow(2.0, _))
  @targetName("log2F16")
  def log2(value: Float16): Float16 = halved(value)(x => math.log(x) / math.log(2.0))
  @targetName("sinF16")
  def sin(value: Float16): Float16 = halved(value)(x => math.sin(2.0 * math.Pi * x))
  @targetName("cosF16")
  def cos(value: Float16): Float16 = halved(value)(x => math.cos(2.0 * math.Pi * x))

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
  def trunc(value: Float32): Float32 =
    Float32(IEEE754.integral(value.toDouble, IEEE754.Rounding.TowardZero).toFloat)
  @targetName("truncF64")
  def trunc(value: Float64): Float64 = Float64(IEEE754.integral(value.toDouble, IEEE754.Rounding.TowardZero))
  @targetName("floorF32")
  def floor(value: Float32): Float32 = Float32(math.floor(value.toDouble).toFloat)
  @targetName("floorF64")
  def floor(value: Float64): Float64 = Float64(math.floor(value.toDouble))
  @targetName("rndneF32")
  def rndne(value: Float32): Float32 = Float32(Math.rint(value.toDouble).toFloat)
  @targetName("rndneF64")
  def rndne(value: Float64): Float64 = Float64(Math.rint(value.toDouble))
  @targetName("fractF32")
  def fract(value: Float32): Float32 = {
    val x = value.toFloat
    Float32(math.min(x - math.floor(x.toDouble).toFloat, Math.nextDown(1.0f)))
  }
  @targetName("fractF64")
  def fract(value: Float64): Float64 = {
    val x = value.toDouble
    Float64(math.min(x - math.floor(x), Math.nextDown(1.0)))
  }

  /** FREXP_MANT: the significand of a finite non-zero value as a fraction in [0.5, 1); other values pass. */
  @targetName("frexpMantF32")
  def frexpMant(value: Float32): Float32 = {
    val x = value.toFloat
    Float32(if (x == 0.0f || x.isNaN || x.isInfinite) x else Math.scalb(x, -Math.getExponent(x) - 1))
  }
  @targetName("frexpMantF64")
  def frexpMant(value: Float64): Float64 = {
    val x = value.toDouble
    Float64(if (x == 0.0 || x.isNaN || x.isInfinite) x else Math.scalb(x, -Math.getExponent(x) - 1))
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
  def cvtF32F16(value: Float16): Float32  = Float32(value.toFloat)
  def cvtF16F32(value: Float32): Float16  = Float16(value.toFloat)
  def cvtF32F64(value: Float64): Float32  = Float32(value.toDouble.toFloat)
  def cvtF64F32(value: Float32): Float64  = Float64(value.toDouble)

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
    Float32(divisionFixupF32(quotient.toFloat, denominator.toFloat, numerator.toFloat))
  @targetName("divFixupF16")
  def divFixup(quotient: Float16, denominator: Float16, numerator: Float16): Float16 =
    Float16(divisionFixupF32(quotient.toFloat, denominator.toFloat, numerator.toFloat))
  @targetName("divFixupF64")
  def divFixup(quotient: Float64, denominator: Float64, numerator: Float64): Float64 =
    Float64(divisionFixupF64(quotient.toDouble, denominator.toDouble, numerator.toDouble))

  /** V_DIV_SCALE: the operand pre-scaled away from the denormal range, given the denominator and numerator. */
  @targetName("divScaleF32")
  def divScale(value: Float32, denominator: Float32, numerator: Float32): Float32 =
    Float32(divisionScaleF32(value.toFloat, denominator.toFloat, numerator.toFloat))
  @targetName("divScaleF64")
  def divScale(value: Float64, denominator: Float64, numerator: Float64): Float64 =
    Float64(divisionScaleF64(value.toDouble, denominator.toDouble, numerator.toDouble))

  /** The flag V_DIV_SCALE leaves for V_DIV_FMAS. */
  @targetName("divScaleFlagF32")
  def divScaleFlag(denominator: Float32, numerator: Float32): Bool =
    divisionScaleConditionF32(denominator.toFloat, numerator.toFloat)
  @targetName("divScaleFlagF64")
  def divScaleFlag(denominator: Float64, numerator: Float64): Bool =
    divisionScaleConditionF64(denominator.toDouble, numerator.toDouble)

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
    mixed(a.bits.raw, b.bits.raw, c.bits.raw)(
      IEEE754.fusedMultiplyAddExact(
        left = a.toDouble,
        right = b.toDouble,
        addend = c.toDouble,
        width = 16,
        rounding = IEEE754.Rounding.NearestEven
      )
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

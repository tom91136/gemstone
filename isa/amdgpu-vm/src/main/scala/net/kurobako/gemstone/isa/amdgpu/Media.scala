package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** AMDGPU media operations on register bits: cube map coordinates, normalised and byte conversions, and the quad sums
  * of absolute byte differences, as execution computes them.
  */
private[isa] object Media {
  private inline def bits[T](value: T): Long = toRaw[T](value)

  /** The face V_CUBEID selects for a direction: 4 or 5 for the z axis, 2 or 3 for y, 0 or 1 for x, odd when negative.
    */
  def cubeId(x: Float32, y: Float32, z: Float32): Float32 = {
    val (a, b, c) = (x.toFloat, y.toFloat, z.toFloat)
    Float32(
      if (math.abs(c) >= math.abs(a) && math.abs(c) >= math.abs(b)) if (c < 0) 5.0f else 4.0f
      else if (math.abs(b) >= math.abs(a)) if (b < 0) 3.0f else 2.0f
      else if (a < 0) 1.0f
      else 0.0f
    )
  }

  /** V_CUBESC: the face's s coordinate before division by the major axis. */
  def cubeSc(x: Float32, y: Float32, z: Float32): Float32 = {
    val (a, b, c) = (x.toFloat, y.toFloat, z.toFloat)
    Float32(
      if (math.abs(c) >= math.abs(a) && math.abs(c) >= math.abs(b)) if (c < 0) -a else a
      else if (math.abs(b) >= math.abs(a)) a
      else if (a < 0) c
      else -c
    )
  }

  /** V_CUBETC: the face's t coordinate before division by the major axis. */
  def cubeTc(x: Float32, y: Float32, z: Float32): Float32 = {
    val (a, b, c) = (x.toFloat, y.toFloat, z.toFloat)
    Float32(
      if (math.abs(c) >= math.abs(a) && math.abs(c) >= math.abs(b)) -b
      else if (math.abs(b) >= math.abs(a)) if (b < 0) -c else c
      else -b
    )
  }

  /** V_CUBEMA: twice the major axis coordinate. */
  def cubeMa(x: Float32, y: Float32, z: Float32): Float32 = {
    val (a, b, c) = (x.toFloat, y.toFloat, z.toFloat)
    Float32(
      if (math.abs(c) >= math.abs(a) && math.abs(c) >= math.abs(b)) 2.0f * c
      else if (math.abs(b) >= math.abs(a)) 2.0f * b
      else 2.0f * a
    )
  }

  private def signedNorm(value: Float): Long =
    Normalised.encodeSigned(value.toDouble, 16, IEEE754.Rounding.NearestEven)

  private def unsignedNorm(value: Float): Long =
    Normalised.encodeUnsigned(value.toDouble, 16, IEEE754.Rounding.NearestEven)

  def packNormI16F32(low: Float32, high: Float32): UInt[32] =
    fromRaw[UInt[32]](signedNorm(low.toFloat) | (signedNorm(high.toFloat) << 16))
  def packNormU16F32(low: Float32, high: Float32): UInt[32] =
    fromRaw[UInt[32]](unsignedNorm(low.toFloat) | (unsignedNorm(high.toFloat) << 16))
  def packNormI16F16(low: Float16, high: Float16): UInt[32] =
    fromRaw[UInt[32]](signedNorm(low.toFloat) | (signedNorm(high.toFloat) << 16))
  def packNormU16F16(low: Float16, high: Float16): UInt[32] =
    fromRaw[UInt[32]](unsignedNorm(low.toFloat) | (unsignedNorm(high.toFloat) << 16))
  def signedNormF16(value: Float16): UInt[16]   = fromRaw[UInt[16]](signedNorm(value.toFloat))
  def unsignedNormF16(value: Float16): UInt[16] = fromRaw[UInt[16]](unsignedNorm(value.toFloat))

  /** V_CVT_PK_U8_F32: the float as a byte, rounded to nearest even and saturated, in the byte of `word` `index` names.
    */
  def packByteF32(value: Float32, index: UInt[32], word: UInt[32]): UInt[32] = {
    val x     = value.toFloat
    val byte  = if (x.isNaN) 0L else Math.rint(math.max(0.0, math.min(255.0, x.toDouble))).toLong
    val shift = ((bits(index) & 3L) * 8L).toInt
    fromRaw[UInt[32]]((bits(word) & 0xffffffffL & ~(0xffL << shift)) | (byte << shift))
  }

  /** V_CVT_OFF_F32_I4: the low four bits as a signed integer over 16. */
  def offsetF32I4(value: UInt[32]): Float32 = Float32(((bits(value) << 60) >> 60).toFloat / 16.0f)

  private def bfloat(value: Float32): Long = IEEE754.binary32ToBfloat16(Binary32.low(bits(value))).toLong

  /** V_CVT_PK_BF16_F32: both floats as bfloat16, the first in the low half. */
  def packBF16F32(low: Float32, high: Float32): UInt[32] = fromRaw[UInt[32]](bfloat(low) | (bfloat(high) << 16))

  /** `value` truncated to an integer and saturated to [`minimum`, `maximum`]; NaN is zero. */
  private def saturated(value: Float32, minimum: Long, maximum: Long): Long = {
    val real = value.toDouble
    if (real.isNaN) 0L else math.max(minimum.toDouble, math.min(maximum.toDouble, real)).toLong & 0xffffL
  }

  /** V_CVT_PK_I16_F32 and V_CVT_PK_U16_F32: two binary32 values truncated and saturated to 16-bit integers. */
  def packI16F32(low: Float32, high: Float32): UInt[32] =
    fromRaw[UInt[32]](
      saturated(low, minimum = -32768L, maximum = 32767L) | (saturated(high, minimum = -32768L, maximum = 32767L) << 16)
    )
  def packU16F32(low: Float32, high: Float32): UInt[32] =
    fromRaw[UInt[32]](
      saturated(low, minimum = 0L, maximum = 65535L) | (saturated(high, minimum = 0L, maximum = 65535L) << 16)
    )

  /** S_BITREPLICATE_B64_B32: each bit of the dword twice. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def bitReplicate(value: UInt[32]): UInt[64] = {
    var result = 0L
    var index  = 0
    while (index < 32) {
      if (((bits(value) >>> index) & 1L) != 0L) result |= 3L << (index * 2)
      index += 1
    }
    fromRaw[UInt[64]](result)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def byteDifferences(window: Long, reference: Long, masked: Boolean): Long = {
    var sum   = 0L
    var index = 0
    while (index < 4) {
      val x = (window >>> (index * 8)) & 0xffL
      val y = (reference >>> (index * 8)) & 0xffL
      if (!(masked && y == 0L)) sum += math.abs(x - y)
      index += 1
    }
    sum
  }

  /** V_QSAD_PK_U16_U8 and V_MQSAD_PK_U16_U8: each 16-bit half of the accumulator plus the byte differences of the dword
    * at its byte offset in the register pair and the reference, masked where the reference byte is zero.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def quadSad(source: UInt[64], reference: UInt[32], accumulator: UInt[64], masked: Boolean): UInt[64] = {
    var result = 0L
    var index  = 0
    while (index < 4) {
      val sum = byteDifferences(bits(source) >>> (index * 8), bits(reference), masked) +
        ((bits(accumulator) >>> (index * 16)) & 0xffffL)
      result |= (sum & 0xffffL) << (index * 16)
      index += 1
    }
    fromRaw[UInt[64]](result)
  }

  /** One dword of V_MQSAD_U32_U8: the accumulator dword plus the masked byte differences of the dword at byte offset
    * `index` of the register pair and the reference.
    */
  def maskedSad(source: UInt[64], reference: UInt[32], accumulator: UInt[32], index: Int): UInt[32] =
    fromRaw[UInt[32]](
      (byteDifferences(bits(source) >>> (index * 8), bits(reference), masked = true) + bits(accumulator)) &
        0xffffffffL
    )
}

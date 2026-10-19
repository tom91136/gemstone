package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import scala.annotation.targetName

/** Conversions between NVIDIA numeric formats where either side is floating, from each source type to each kind of
  * destination, of the `width` given unless bfloat16; bfloat16 values are their bits. Integer destinations round toward
  * zero and saturate, floating ones round to nearest even, unless the rounding says otherwise.
  */
private[isa] object Conversions {
  private val F16: NumericFormat  = NumericFormat.IEEE(16)
  private val F32: NumericFormat  = NumericFormat.IEEE(32)
  private val F64: NumericFormat  = NumericFormat.IEEE(64)
  private val BF16: NumericFormat = NumericFormat.BF16
  private val S8: NumericFormat   = NumericFormat.Signed(8)
  private val S16: NumericFormat  = NumericFormat.Signed(16)
  private val S32: NumericFormat  = NumericFormat.Signed(32)
  private val S64: NumericFormat  = NumericFormat.Signed(64)
  private val U8: NumericFormat   = NumericFormat.Unsigned(8)
  private val U16: NumericFormat  = NumericFormat.Unsigned(16)
  private val U32: NumericFormat  = NumericFormat.Unsigned(32)
  private val U64: NumericFormat  = NumericFormat.Unsigned(64)

  private val FloatingFormats = Array(F16, F32, F64)
  private val SignedFormats   = Array(S8, S16, S32, S64)
  private val UnsignedFormats = Array(U8, U16, U32, U64)

  // by the width's binary logarithm, so selecting a destination format takes no branch
  private inline def floating(width: Int): NumericFormat = FloatingFormats(Integer.numberOfTrailingZeros(width) - 4)
  private inline def signed(width: Int): NumericFormat   = SignedFormats(Integer.numberOfTrailingZeros(width) - 3)
  private inline def unsigned(width: Int): NumericFormat = UnsignedFormats(Integer.numberOfTrailingZeros(width) - 3)

  private inline def floatingBits[W <: Int](bits: Long, width: Int): FloatingPoint.Named[W] =
    fromRaw[FloatingPoint.Named[W]](bits & (-1L >>> (64 - width)))
  private inline def signedBits[W <: Int](bits: Long, width: Int): SInt[W] =
    fromRaw[SInt[W]]((bits << (64 - width)) >> (64 - width))
  private inline def unsignedBits[W <: Int](bits: Long, width: Int): UInt[W] =
    fromRaw[UInt[W]](bits & (-1L >>> (64 - width)))

  @targetName("float16ToFloating")
  def toFloating[W <: 16 | 32 | 64](value: Float16, rounding: ConversionRounding, width: W): FloatingPoint.Named[W] =
    floatingBits[W](VMArithmetic.convert(toRaw(value), F16, floating(width), rounding), width)
  @targetName("float32ToFloating")
  def toFloating[W <: 16 | 32 | 64](value: Float32, rounding: ConversionRounding, width: W): FloatingPoint.Named[W] =
    floatingBits[W](VMArithmetic.convert(toRaw(value), F32, floating(width), rounding), width)
  @targetName("float64ToFloating")
  def toFloating[W <: 16 | 32 | 64](value: Float64, rounding: ConversionRounding, width: W): FloatingPoint.Named[W] =
    floatingBits[W](VMArithmetic.convert(toRaw(value), F64, floating(width), rounding), width)
  @targetName("int8ToFloating")
  def toFloating[W <: 16 | 32 | 64](value: SInt[8], rounding: ConversionRounding, width: W): FloatingPoint.Named[W] =
    floatingBits[W](VMArithmetic.convert(toRaw(value), S8, floating(width), rounding), width)
  @targetName("int16ToFloating")
  def toFloating[W <: 16 | 32 | 64](value: SInt[16], rounding: ConversionRounding, width: W): FloatingPoint.Named[W] =
    floatingBits[W](VMArithmetic.convert(toRaw(value), S16, floating(width), rounding), width)
  @targetName("int32ToFloating")
  def toFloating[W <: 16 | 32 | 64](value: SInt[32], rounding: ConversionRounding, width: W): FloatingPoint.Named[W] =
    floatingBits[W](VMArithmetic.convert(toRaw(value), S32, floating(width), rounding), width)
  @targetName("int64ToFloating")
  def toFloating[W <: 16 | 32 | 64](value: SInt[64], rounding: ConversionRounding, width: W): FloatingPoint.Named[W] =
    floatingBits[W](VMArithmetic.convert(toRaw(value), S64, floating(width), rounding), width)
  @targetName("uint8ToFloating")
  def toFloating[W <: 16 | 32 | 64](value: UInt[8], rounding: ConversionRounding, width: W): FloatingPoint.Named[W] =
    floatingBits[W](VMArithmetic.convert(toRaw(value), U8, floating(width), rounding), width)
  @targetName("uint16ToFloating")
  def toFloating[W <: 16 | 32 | 64](value: UInt[16], rounding: ConversionRounding, width: W): FloatingPoint.Named[W] =
    floatingBits[W](VMArithmetic.convert(toRaw(value), U16, floating(width), rounding), width)
  @targetName("uint32ToFloating")
  def toFloating[W <: 16 | 32 | 64](value: UInt[32], rounding: ConversionRounding, width: W): FloatingPoint.Named[W] =
    floatingBits[W](VMArithmetic.convert(toRaw(value), U32, floating(width), rounding), width)
  @targetName("uint64ToFloating")
  def toFloating[W <: 16 | 32 | 64](value: UInt[64], rounding: ConversionRounding, width: W): FloatingPoint.Named[W] =
    floatingBits[W](VMArithmetic.convert(toRaw(value), U64, floating(width), rounding), width)
  @targetName("float16ToBfloat16")
  def toBfloat16(value: Float16, rounding: ConversionRounding): UInt[16] =
    UInt[16](VMArithmetic.convert(toRaw(value), F16, BF16, rounding))
  @targetName("float32ToBfloat16")
  def toBfloat16(value: Float32, rounding: ConversionRounding): UInt[16] =
    UInt[16](VMArithmetic.convert(toRaw(value), F32, BF16, rounding))
  @targetName("float64ToBfloat16")
  def toBfloat16(value: Float64, rounding: ConversionRounding): UInt[16] =
    UInt[16](VMArithmetic.convert(toRaw(value), F64, BF16, rounding))
  @targetName("int8ToBfloat16")
  def toBfloat16(value: SInt[8], rounding: ConversionRounding): UInt[16] =
    UInt[16](VMArithmetic.convert(toRaw(value), S8, BF16, rounding))
  @targetName("int16ToBfloat16")
  def toBfloat16(value: SInt[16], rounding: ConversionRounding): UInt[16] =
    UInt[16](VMArithmetic.convert(toRaw(value), S16, BF16, rounding))
  @targetName("int32ToBfloat16")
  def toBfloat16(value: SInt[32], rounding: ConversionRounding): UInt[16] =
    UInt[16](VMArithmetic.convert(toRaw(value), S32, BF16, rounding))
  @targetName("int64ToBfloat16")
  def toBfloat16(value: SInt[64], rounding: ConversionRounding): UInt[16] =
    UInt[16](VMArithmetic.convert(toRaw(value), S64, BF16, rounding))
  @targetName("uint8ToBfloat16")
  def toBfloat16(value: UInt[8], rounding: ConversionRounding): UInt[16] =
    UInt[16](VMArithmetic.convert(toRaw(value), U8, BF16, rounding))
  @targetName("uint16ToBfloat16")
  def toBfloat16(value: UInt[16], rounding: ConversionRounding): UInt[16] =
    UInt[16](VMArithmetic.convert(toRaw(value), U16, BF16, rounding))
  @targetName("uint32ToBfloat16")
  def toBfloat16(value: UInt[32], rounding: ConversionRounding): UInt[16] =
    UInt[16](VMArithmetic.convert(toRaw(value), U32, BF16, rounding))
  @targetName("uint64ToBfloat16")
  def toBfloat16(value: UInt[64], rounding: ConversionRounding): UInt[16] =
    UInt[16](VMArithmetic.convert(toRaw(value), U64, BF16, rounding))
  @targetName("float16ToSigned")
  def toSigned[W <: 8 | 16 | 32 | 64](value: Float16, rounding: ConversionRounding, width: W): SInt[W] =
    signedBits(VMArithmetic.convert(toRaw(value), F16, signed(width), rounding), width)
  @targetName("float32ToSigned")
  def toSigned[W <: 8 | 16 | 32 | 64](value: Float32, rounding: ConversionRounding, width: W): SInt[W] =
    signedBits(VMArithmetic.convert(toRaw(value), F32, signed(width), rounding), width)
  @targetName("float64ToSigned")
  def toSigned[W <: 8 | 16 | 32 | 64](value: Float64, rounding: ConversionRounding, width: W): SInt[W] =
    signedBits(VMArithmetic.convert(toRaw(value), F64, signed(width), rounding), width)
  @targetName("float16ToUnsigned")
  def toUnsigned[W <: 8 | 16 | 32 | 64](value: Float16, rounding: ConversionRounding, width: W): UInt[W] =
    unsignedBits(VMArithmetic.convert(toRaw(value), F16, unsigned(width), rounding), width)
  @targetName("float32ToUnsigned")
  def toUnsigned[W <: 8 | 16 | 32 | 64](value: Float32, rounding: ConversionRounding, width: W): UInt[W] =
    unsignedBits(VMArithmetic.convert(toRaw(value), F32, unsigned(width), rounding), width)
  @targetName("float64ToUnsigned")
  def toUnsigned[W <: 8 | 16 | 32 | 64](value: Float64, rounding: ConversionRounding, width: W): UInt[W] =
    unsignedBits(VMArithmetic.convert(toRaw(value), F64, unsigned(width), rounding), width)
  def bfloat16ToFloating[W <: 16 | 32 | 64](value: UInt[16], rounding: ConversionRounding, width: W)
      : FloatingPoint.Named[W] =
    floatingBits[W](VMArithmetic.convert(toRaw(value), BF16, floating(width), rounding), width)
  def bfloat16ToBfloat16(value: UInt[16], rounding: ConversionRounding): UInt[16] =
    UInt[16](VMArithmetic.convert(toRaw(value), BF16, BF16, rounding))
  def bfloat16ToSigned[W <: 8 | 16 | 32 | 64](value: UInt[16], rounding: ConversionRounding, width: W): SInt[W] =
    signedBits(VMArithmetic.convert(toRaw(value), BF16, signed(width), rounding), width)
  def bfloat16ToUnsigned[W <: 8 | 16 | 32 | 64](value: UInt[16], rounding: ConversionRounding, width: W): UInt[W] =
    unsignedBits(VMArithmetic.convert(toRaw(value), BF16, unsigned(width), rounding), width)

  /** The value clamped to [0, 1], with NaN as 0. */
  @targetName("saturate16")
  def saturate(value: Float16): Float16 = FloatArithmetic.saturate(value)
  @targetName("saturate32")
  def saturate(value: Float32): Float32 = FloatArithmetic.saturate(value)
  @targetName("saturate64")
  def saturate(value: Float64): Float64           = FloatArithmetic.saturate(value)
  def saturateBfloat16(value: UInt[16]): UInt[16] =
    UInt[16](VMArithmetic.clamp(toRaw(value), BF16, ConversionClamp.Saturate))

  /** A negative value as 0. */
  @targetName("relu16")
  def relu(value: Float16): Float16 =
    FloatingPoint[16](VMArithmetic.clamp(toRaw(value), F16, ConversionClamp.Relu))
  @targetName("relu32")
  def relu(value: Float32): Float32 =
    FloatingPoint[32](VMArithmetic.clamp(toRaw(value), F32, ConversionClamp.Relu))
  @targetName("relu64")
  def relu(value: Float64): Float64 =
    FloatingPoint[64](VMArithmetic.clamp(toRaw(value), F64, ConversionClamp.Relu))
  def reluBfloat16(value: UInt[16]): UInt[16] =
    UInt[16](VMArithmetic.clamp(toRaw(value), BF16, ConversionClamp.Relu))

  /** `value` converted from `from` to `to`, at least one of them floating. */
  inline def staged[V](
      inline algebra: Algebra[V],
      from: NumericFormat,
      to: NumericFormat,
      rounding: ConversionRounding,
      value: V
  ): V = to match {
    case NumericFormat.IEEE(width) =>
      width.raw match {
        case 16 => floatingFrom(algebra, from, rounding, 16, value)
        case 32 => floatingFrom(algebra, from, rounding, 32, value)
        case _  => floatingFrom(algebra, from, rounding, 64, value)
      }
    case NumericFormat.BF16          => bfloat16From(algebra, from, rounding, value)
    case NumericFormat.Signed(width) =>
      width.raw match {
        case 8  => signedFrom(algebra, from, rounding, 8, value)
        case 16 => signedFrom(algebra, from, rounding, 16, value)
        case 32 => signedFrom(algebra, from, rounding, 32, value)
        case _  => signedFrom(algebra, from, rounding, 64, value)
      }
    case integer =>
      integer.width.raw match {
        case 8  => unsignedFrom(algebra, from, rounding, 8, value)
        case 16 => unsignedFrom(algebra, from, rounding, 16, value)
        case 32 => unsignedFrom(algebra, from, rounding, 32, value)
        case _  => unsignedFrom(algebra, from, rounding, 64, value)
      }
  }

  private inline def floatingFrom[V, W <: 16 | 32 | 64](
      inline algebra: Algebra[V],
      from: NumericFormat,
      rounding: ConversionRounding,
      width: W,
      value: V
  ): V = from match {
    case NumericFormat.IEEE(source) =>
      source.raw match {
        case 16 => algebra.primitive(value)((x: Float16) => Conversions.toFloating(x, rounding, width))
        case 32 => algebra.primitive(value)((x: Float32) => Conversions.toFloating(x, rounding, width))
        case _  => algebra.primitive(value)((x: Float64) => Conversions.toFloating(x, rounding, width))
      }
    case NumericFormat.BF16 =>
      algebra.primitive(value)((x: UInt[16]) => Conversions.bfloat16ToFloating(x, rounding, width))
    case NumericFormat.Signed(source) =>
      source.raw match {
        case 8  => algebra.primitive(value)((x: SInt[8]) => Conversions.toFloating(x, rounding, width))
        case 16 => algebra.primitive(value)((x: SInt[16]) => Conversions.toFloating(x, rounding, width))
        case 32 => algebra.primitive(value)((x: SInt[32]) => Conversions.toFloating(x, rounding, width))
        case _  => algebra.primitive(value)((x: SInt[64]) => Conversions.toFloating(x, rounding, width))
      }
    case NumericFormat.Bits(source) if source == 64 =>
      algebra.primitive(value)((x: SInt[64]) => Conversions.toFloating(x, rounding, width))
    case integer =>
      integer.width.raw match {
        case 8  => algebra.primitive(value)((x: UInt[8]) => Conversions.toFloating(x, rounding, width))
        case 16 => algebra.primitive(value)((x: UInt[16]) => Conversions.toFloating(x, rounding, width))
        case 32 => algebra.primitive(value)((x: UInt[32]) => Conversions.toFloating(x, rounding, width))
        case _  => algebra.primitive(value)((x: UInt[64]) => Conversions.toFloating(x, rounding, width))
      }
  }

  private inline def bfloat16From[V](
      inline algebra: Algebra[V],
      from: NumericFormat,
      rounding: ConversionRounding,
      value: V
  ): V = from match {
    case NumericFormat.IEEE(source) =>
      source.raw match {
        case 16 => algebra.primitive(value)((x: Float16) => Conversions.toBfloat16(x, rounding))
        case 32 => algebra.primitive(value)((x: Float32) => Conversions.toBfloat16(x, rounding))
        case _  => algebra.primitive(value)((x: Float64) => Conversions.toBfloat16(x, rounding))
      }
    case NumericFormat.BF16 => algebra.primitive(value)((x: UInt[16]) => Conversions.bfloat16ToBfloat16(x, rounding))
    case NumericFormat.Signed(source) =>
      source.raw match {
        case 8  => algebra.primitive(value)((x: SInt[8]) => Conversions.toBfloat16(x, rounding))
        case 16 => algebra.primitive(value)((x: SInt[16]) => Conversions.toBfloat16(x, rounding))
        case 32 => algebra.primitive(value)((x: SInt[32]) => Conversions.toBfloat16(x, rounding))
        case _  => algebra.primitive(value)((x: SInt[64]) => Conversions.toBfloat16(x, rounding))
      }
    case NumericFormat.Bits(source) if source == 64 =>
      algebra.primitive(value)((x: SInt[64]) => Conversions.toBfloat16(x, rounding))
    case integer =>
      integer.width.raw match {
        case 8  => algebra.primitive(value)((x: UInt[8]) => Conversions.toBfloat16(x, rounding))
        case 16 => algebra.primitive(value)((x: UInt[16]) => Conversions.toBfloat16(x, rounding))
        case 32 => algebra.primitive(value)((x: UInt[32]) => Conversions.toBfloat16(x, rounding))
        case _  => algebra.primitive(value)((x: UInt[64]) => Conversions.toBfloat16(x, rounding))
      }
  }

  private inline def signedFrom[V, W <: 8 | 16 | 32 | 64](
      inline algebra: Algebra[V],
      from: NumericFormat,
      rounding: ConversionRounding,
      width: W,
      value: V
  ): V = from match {
    case NumericFormat.IEEE(source) =>
      source.raw match {
        case 16 => algebra.primitive(value)((x: Float16) => Conversions.toSigned(x, rounding, width))
        case 32 => algebra.primitive(value)((x: Float32) => Conversions.toSigned(x, rounding, width))
        case _  => algebra.primitive(value)((x: Float64) => Conversions.toSigned(x, rounding, width))
      }
    case _ => algebra.primitive(value)((x: UInt[16]) => Conversions.bfloat16ToSigned(x, rounding, width))
  }

  private inline def unsignedFrom[V, W <: 8 | 16 | 32 | 64](
      inline algebra: Algebra[V],
      from: NumericFormat,
      rounding: ConversionRounding,
      width: W,
      value: V
  ): V = from match {
    case NumericFormat.IEEE(source) =>
      source.raw match {
        case 16 => algebra.primitive(value)((x: Float16) => Conversions.toUnsigned(x, rounding, width))
        case 32 => algebra.primitive(value)((x: Float32) => Conversions.toUnsigned(x, rounding, width))
        case _  => algebra.primitive(value)((x: Float64) => Conversions.toUnsigned(x, rounding, width))
      }
    case _ => algebra.primitive(value)((x: UInt[16]) => Conversions.bfloat16ToUnsigned(x, rounding, width))
  }
}

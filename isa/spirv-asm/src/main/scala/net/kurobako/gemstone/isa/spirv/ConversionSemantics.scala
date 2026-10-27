package net.kurobako.gemstone.isa.spirv

import FloatingSemantics.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** SPIR-V conversions by opcode, from a `width`-bit operand to a `resultWidth`-bit result. Conversions that round do so
  * once, in the decorated rounding `mode`; float-to-integer conversions first round to an integral value in that mode.
  */
object ConversionSemantics {

  /** Integer width conversions, and same-width bit casts. */
  inline def integer[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64,
      resultWidth: Int in 1 ~ 64
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.UConvert | SPIRV.Opcode.Bitcast => sinks.unary(ids)(a => algebra.truncate(a, resultWidth))
    case SPIRV.Opcode.SConvert => sinks.unary(ids)(a => algebra.truncate(algebra.signExtend(a, width), resultWidth))
    case _                     => otherwise
  }

  /** SPV_EXT_ocp_microscaling_types' OpBitcastExtractEXT: the `resultWidth` bits of the base from the offset, which
    * read zero past the base's `width` bits.
    */
  inline def bitcastExtract[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in 1 ~ 64,
      resultWidth: Int in 1 ~ 64
  )(ids: OperandIds): R = sinks.binary(ids)((base, offset) =>
    algebra.select(
      algebra.lessThan(offset, algebra.literal(width.toLong), signed = false),
      algebra.truncate(algebra.shiftRightLogical(algebra.truncate(base, width), offset), resultWidth),
      algebra.literal(0L)
    )
  )

  /** OpSatConvertSToU and OpSatConvertUToS: the operand, read as signed or unsigned, clamped to the result's range. */
  inline def saturating[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64,
      resultWidth: Int in 1 ~ 64
  )(ids: OperandIds)(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.SatConvertSToU =>
      sinks.unary(ids)(a =>
        algebra.truncate(
          algebra.minimum(
            algebra.maximum(algebra.signExtend(a, width), algebra.literal(0L), signed = true),
            algebra.literal(Bits.mask(resultWidth)),
            signed = false
          ),
          resultWidth
        )
      )
    case SPIRV.Opcode.SatConvertUToS =>
      sinks.unary(ids)(a =>
        algebra.truncate(
          algebra.minimum(algebra.truncate(a, width), algebra.literal(Bits.mask(resultWidth) >>> 1), signed = false),
          resultWidth
        )
      )
    case _ => otherwise
  }

  inline def toFloating[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64,
      resultWidth: Int in (16, 32, 64),
      mode: IEEE754.Rounding
  )(ids: OperandIds)(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.ConvertSToF => resultWidth.raw match {
        case 16 =>
          sinks.unary(ids)(a =>
            algebra.primitive(algebra.signExtend(a, width))((x: SInt[64]) => fromSigned(x, mode, 16))
          )
        case 32 =>
          sinks.unary(ids)(a =>
            algebra.primitive(algebra.signExtend(a, width))((x: SInt[64]) => fromSigned(x, mode, 32))
          )
        case _ =>
          sinks.unary(ids)(a =>
            algebra.primitive(algebra.signExtend(a, width))((x: SInt[64]) => fromSigned(x, mode, 64))
          )
      }
    case SPIRV.Opcode.ConvertUToF => resultWidth.raw match {
        case 16 =>
          sinks.unary(ids)(a =>
            algebra.primitive(algebra.truncate(a, width))((x: UInt[64]) => fromUnsigned(x, mode, 16))
          )
        case 32 =>
          sinks.unary(ids)(a =>
            algebra.primitive(algebra.truncate(a, width))((x: UInt[64]) => fromUnsigned(x, mode, 32))
          )
        case _ =>
          sinks.unary(ids)(a =>
            algebra.primitive(algebra.truncate(a, width))((x: UInt[64]) => fromUnsigned(x, mode, 64))
          )
      }
    case _ => otherwise
  }

  /** OpConvertFToS: without saturation the value wraps to the width after clamping to 64 bits, as the JVM does. */
  inline def toSigned[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64),
      resultWidth: Int in 1 ~ 64,
      mode: IEEE754.Rounding,
      saturate: Boolean
  )(ids: OperandIds): R =
    if (saturate) width.raw match {
      case 16 => widenedUnary(algebra, sinks, 16)(floatToSignedSaturated(_, resultWidth, mode))(ids)
      case 32 => widenedUnary(algebra, sinks, 32)(floatToSignedSaturated(_, resultWidth, mode))(ids)
      case _  => widenedUnary(algebra, sinks, 64)(floatToSignedSaturated(_, resultWidth, mode))(ids)
    }
    else width.raw match {
      case 16 => widenedUnary(algebra, sinks, 16)(floatToSigned(_, resultWidth, mode))(ids)
      case 32 => widenedUnary(algebra, sinks, 32)(floatToSigned(_, resultWidth, mode))(ids)
      case _  => widenedUnary(algebra, sinks, 64)(floatToSigned(_, resultWidth, mode))(ids)
    }

  private inline def widenedUnary[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], inline bits: Int)(
      inline function: Float64 => UInt[64]
  )(ids: OperandIds): R = sinks.unary(ids)(a => algebra.primitive(widened(algebra, a, bits))(function))

  /** OpConvertFToU: a value outside the range clamps, NaN and negative values to zero, unless the sink faults on lanes
    * it cannot represent.
    */
  inline def toUnsigned[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64),
      resultWidth: Int in 1 ~ 64,
      mode: IEEE754.Rounding
  )(ids: OperandIds): R = width.raw match {
    case 16 => unsignedAt(algebra, sinks, 16, resultWidth, mode)(ids)
    case 32 => unsignedAt(algebra, sinks, 32, resultWidth, mode)(ids)
    case _  => unsignedAt(algebra, sinks, 64, resultWidth, mode)(ids)
  }

  private inline def unsignedAt[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      inline bits: Int,
      resultWidth: Int in 1 ~ 64,
      mode: IEEE754.Rounding
  )(ids: OperandIds): R = sinks.unsignedConversion(ids)(
    a => algebra.primitive(widened(algebra, a, bits))((x: Float64) => unsignedRepresentable(x, resultWidth, mode)),
    a => algebra.primitive(widened(algebra, a, bits))((x: Float64) => floatToUnsigned(x, resultWidth, mode))
  )

  /** OpenCL vload_half: halves converted to binary32. */
  inline def halfLoad[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], aligned: Boolean)(
      ids: OperandIds
  ): R =
    sinks.halfLoad(ids, aligned)(a =>
      algebra.primitive(algebra.primitive(a)(Elementary.widen16(_)))(Elementary.narrow32(_))
    )

  /** OpenCL vstore_half: lanes converted to halves in `mode`. */
  inline def halfStore[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64),
      mode: IEEE754.Rounding,
      aligned: Boolean
  )(ids: OperandIds): R = width.raw match {
    case 16 => sinks.halfStore(ids, aligned)(a => a)
    case 32 => sinks.halfStore(ids, aligned)(a =>
        algebra.primitive(widened(algebra, a, 32))((x: Float64) => Elementary.narrow16(x, mode))
      )
    case _ => sinks.halfStore(ids, aligned)(a => algebra.primitive(a)((x: Float64) => Elementary.narrow16(x, mode)))
  }

  /** The rounding an `FPRoundingMode` value names, or `default` for any other value. */
  def rounding(mode: Int, default: IEEE754.Rounding): IEEE754.Rounding = mode match {
    case FPRoundingMode.RTE => IEEE754.Rounding.NearestEven
    case FPRoundingMode.RTZ => IEEE754.Rounding.TowardZero
    case FPRoundingMode.RTP => IEEE754.Rounding.Up
    case FPRoundingMode.RTN => IEEE754.Rounding.Down
    case _                  => default
  }

  /** OpFConvert; widening is exact. */
  inline def toFloatingWidth[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64),
      resultWidth: Int in (16, 32, 64),
      mode: IEEE754.Rounding
  )(ids: OperandIds): R = width.raw match {
    case 16 => toFloatingWidthFrom(algebra, sinks, 16, resultWidth, mode)(ids)
    case 32 => toFloatingWidthFrom(algebra, sinks, 32, resultWidth, mode)(ids)
    case _  => toFloatingWidthFrom(algebra, sinks, 64, resultWidth, mode)(ids)
  }

  inline def toFloatingWidthFrom[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      inline width: Int,
      resultWidth: Int in (16, 32, 64),
      mode: IEEE754.Rounding
  )(ids: OperandIds): R = resultWidth.raw match {
    case 16 =>
      sinks.unary(ids)(a => algebra.primitive(widened(algebra, a, width))((x: Float64) => Elementary.narrow16(x, mode)))
    case 32 =>
      sinks.unary(ids)(a => algebra.primitive(widened(algebra, a, width))((x: Float64) => Elementary.narrow32(x, mode)))
    case _ => sinks.unary(ids)(a => widened(algebra, a, width))
  }

  /** OpQuantizeToF16: a binary32 encoding of the nearest binary16 value, with values below the smallest normal half
    * flushed to zero of the same sign.
    */
  inline def quantize[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R])(ids: OperandIds): R =
    sinks.unary(ids)(a => algebra.primitive(a)(quantizeToF16(_)))

  def quantizeToF16(x: Float32): Float32 = {
    val value = x.toFloat
    val half  =
      if (Math.abs(value) < Math.scalb(1.0f, -14)) FloatingPoint[16]((x.bits.raw >>> 16) & 0x8000L) else Float16(value)
    Float32(half.toFloat)
  }

  def fromSigned[W <: 16 | 32 | 64](x: SInt[64], mode: IEEE754.Rounding, width: W): FloatingPoint.Named[W] =
    floatingBits[W](IEEE754.integerToBinary(x.raw, unsigned = false, floating(width), mode), width)
  def fromUnsigned[W <: 16 | 32 | 64](x: UInt[64], mode: IEEE754.Rounding, width: W): FloatingPoint.Named[W] =
    floatingBits[W](IEEE754.integerToBinary(x.raw, unsigned = true, floating(width), mode), width)

  private val FloatingWidths: Array[Int in (16, 32, 64)] = Array(16, 32, 64)

  // by the width's binary logarithm, so refining it takes no check
  private inline def floating(width: Int): Int in (16, 32, 64) =
    FloatingWidths(Integer.numberOfTrailingZeros(width) - 4)

  private inline def floatingBits[W <: Int](bits: Long, width: Int): FloatingPoint.Named[W] =
    fromRaw[FloatingPoint.Named[W]](bits & (-1L >>> (64 - width)))

  /** `x` rounded to an integer in `mode` and wrapped to `width` bits after the JVM clamps it to 64. */
  def floatToSigned(x: Float64, width: Int in 1 ~ 64, mode: IEEE754.Rounding): UInt[64] =
    UInt[64](Bits.truncate(integral(x.toDouble, mode).toLong, width))

  /** `x` rounded to an integer in `mode` and clamped to the signed `width`-bit range, NaN to 0, as `width` bits. */
  def floatToSignedSaturated(x: Float64, width: Int in 1 ~ 64, mode: IEEE754.Rounding): UInt[64] =
    UInt[64](IEEE754.toIntegerSaturated(x.toDouble, width, signed = true, mode))

  /** `x` rounded to an integer in `mode` and clamped to the unsigned `width`-bit range, NaN to 0. */
  def floatToUnsigned(x: Float64, width: Int in 1 ~ 64, mode: IEEE754.Rounding): UInt[64] =
    UInt[64](unsigned(integral(x.toDouble, mode), width))

  /** Whether `x` rounded to an integer in `mode` has an unsigned `width`-bit representation. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def unsignedRepresentable(x: Float64, width: Int in 1 ~ 64, mode: IEEE754.Rounding): Bool =
    unrepresentable(integral(x.toDouble, mode), width) == null

  /** A value at `width` as binary64, exactly; `width` must be a literal. */
  inline def widened[V](inline algebra: Algebra[V], value: V, inline width: Int): V =
    algebra.widened(value, width)

  def integral(value: Double, mode: IEEE754.Rounding): Double = IEEE754.integral(value, mode)

  /** An integral binary64 value as an unsigned `width`-bit integer, clamping one outside the range. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def unsigned(value: Double, width: Int in 1 ~ 64): Long =
    if (unrepresentable(value, width) == null) truncated(value, width)
    else if (value.isNaN || value <= 0.0) 0L
    else Bits.mask(width)

  /** Why an integral binary64 value has no unsigned `width`-bit representation, or null when it has one. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def unrepresentable(value: Double, width: Int in 1 ~ 64): String | Null = {
    val bits     = IEEE754.doubleToBinary64(value)
    val exponent = ((bits >>> 52) & 0x7ffL).toInt
    if (exponent == 0x7ff) "NaN and infinity are not integers"
    else if (exponent < 1023) null
    else if (bits < 0L) "negative values are outside the unsigned range"
    else if (exponent - 1023 >= width) "value exceeds the result width"
    else null
  }

  /** Truncates a representable integral binary64 value into an unsigned width without narrowing through a Long. */
  private def truncated(value: Double, width: Int in 1 ~ 64): Long = {
    val bits     = IEEE754.doubleToBinary64(value)
    val exponent = ((bits >>> 52) & 0x7ffL).toInt
    if (exponent < 1023) 0L
    else {
      val shift       = exponent - 1023
      val significand = (1L << 52) | (bits & 0xfffffffffffffL)
      (if (shift >= 52) significand << (shift - 52) else significand >>> (52 - shift)) & Bits.mask(width)
    }
  }
}

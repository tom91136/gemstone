package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*

/** Packed colour and 16-bit float texels. Format numbers match the native image descriptor ABI. */
private object NarrowImageFormat {
  import NarrowEncoding.*

  case class Layout(
      components: Int,
      texelBytes: Int in (2, 4, 8),
      encoding: NarrowEncoding,
      widths: Vector[Int in 1 ~ 32] = Vector.empty,
      shifts: Vector[Int] = Vector.empty
  ) {
    val elementBytes: Int in (2, 4, 8)        = if (encoding == Half) 2 else texelBytes
    def offset(lane: Int): Int                = if (encoding == Half) lane * 2 else 0
    def decode(bits: Long, lane: Int): Double = encoding match {
      case Half           => IEEE754.binary16ToFloat(Binary16.low(bits)).toDouble
      case SharedExponent => Math.scalb(((bits >>> (lane * 9)) & 511).toDouble, (bits >>> 27).toInt - 24)
      case Unorm          => Normalised.decodeUnsigned(bits >>> shifts(lane), widths(lane))
      case Ufloat         =>
        val fractionBits = widths(lane).raw - 5
        val component    = (bits >>> shifts(lane)).toInt
        val fraction     = component & ((1 << fractionBits) - 1)
        val exponent     = (component >>> fractionBits) & 31
        if (exponent == 31) { if (fraction == 0) Double.PositiveInfinity else Double.NaN }
        else if (exponent == 0) Math.scalb(fraction.toDouble, -14 - fractionBits)
        else Math.scalb(((1 << fractionBits) + fraction).toDouble, exponent - 15 - fractionBits)
    }
    def encodeLane(value: Double, lane: Int): Long = encoding match {
      case Half   => IEEE754.doubleToBinary16(value).raw
      case Unorm  => Normalised.encodeUnsigned(value, widths(lane))
      case Ufloat =>
        val fractionBits = widths(lane).raw - 5
        if (value.isNaN) (31L << fractionBits) | (1L << (fractionBits - 1))
        else if (value <= 0.0) 0L
        else if (value >= 65536.0) 31L << fractionBits
        else {
          val exponent = Math.max(-14, Math.getExponent(value))
          ((exponent + 14).toLong << fractionBits) + Math.rint(Math.scalb(value, fractionBits - exponent)).toLong
        }
      case SharedExponent => 0L // This sampled/transfer format has no SPIR-V storage image format.
    }
    def encodePacked(values: Array[Double]): Long = {
      require(encoding == Unorm || encoding == Ufloat)
      (0 until components).foldLeft(0L)((bits, lane) => bits | (encodeLane(values(lane), lane) << shifts(lane)))
    }
  }
}

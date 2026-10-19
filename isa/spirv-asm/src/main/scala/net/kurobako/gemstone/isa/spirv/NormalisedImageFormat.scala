package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*

private object NormalisedImageFormat {
  private val srgbDecode = Array.tabulate(256)(value => SrgbTransfer.toLinear(value / 255.0))
  case class Layout(
      components: Int,
      bits: Int in (8, 16),
      signed: Boolean,
      srgb: Boolean,
      bgra: Boolean
  ) {
    val componentBytes: Int in (1, 2)         = if (bits == 8) 1 else 2
    val texelBytes: Int                       = components * componentBytes
    def offset(lane: Int): Int                = (if (bgra && lane < 3) 2 - lane else lane) * componentBytes
    def decode(bits: Long, lane: Int): Double =
      if (signed) Normalised.decodeSigned(bits, this.bits.widen[Int in 2 ~ 32])
      else if (srgb && lane < 3) srgbDecode((bits & 255).toInt)
      else Normalised.decodeUnsigned(bits, this.bits.widen[Int in 1 ~ 32])
    def encode(value: Double, lane: Int): Long = {
      val clamped = if (value.isNaN) 0.0 else Math.max(if (signed) -1.0 else 0.0, Math.min(1.0, value))
      val encoded = if (srgb && lane < 3) SrgbTransfer.fromLinear(clamped) else clamped
      if (signed) Normalised.encodeSigned(encoded, bits.widen[Int in 2 ~ 32])
      else Normalised.encodeUnsigned(encoded, bits.widen[Int in 1 ~ 32])
    }
  }
}

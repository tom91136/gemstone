package net.kurobako.gemstone.isa.spirv.v1_6

private object NormalizedImageFormat {
  private val srgbDecode = Array.tabulate(256) { value =>
    val encoded = value / 255.0
    if (encoded <= 0.04045) encoded / 12.92 else Math.pow((encoded + 0.055) / 1.055, 2.4)
  }
  case class Layout(components: Int, signed: Boolean = false, srgb: Boolean = false, bgra: Boolean = false) {
    def channel(lane: Int): Int               = if (bgra && lane < 3) 2 - lane else lane
    def decode(bits: Long, lane: Int): Double =
      if (signed) Math.max(-1.0, bits.toByte.toDouble / 127.0)
      else if (srgb && lane < 3) srgbDecode((bits & 255).toInt)
      else (bits & 255).toDouble / 255.0
    def encode(value: Double, lane: Int): Long = {
      val clamped = if (value.isNaN) 0.0 else Math.max(if (signed) -1.0 else 0.0, Math.min(1.0, value))
      val encoded = if (srgb && lane < 3) {
        if (clamped <= 0.0031308) 12.92 * clamped else 1.055 * Math.pow(clamped, 1.0 / 2.4) - 0.055
      } else clamped
      val scaled = encoded * (if (signed) 127.0 else 255.0)
      Math.copySign(Math.floor(Math.abs(scaled) + 0.5), scaled).toLong & 255L
    }
  }
}

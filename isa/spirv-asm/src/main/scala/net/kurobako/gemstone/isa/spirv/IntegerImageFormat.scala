package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.constraint.*

private object IntegerImageFormat {
  case class Layout(components: Int, componentBytes: Int in (1, 2, 4), signed: Boolean, packed: Boolean = false) {
    val texelBytes: Int                         = if (packed) 4 else components * componentBytes.raw
    def offset(lane: Int): Int                  = if (packed) 0 else lane * componentBytes.raw
    def decode(bits: Long, lane: Int = 0): Long =
      if (packed) (bits >>> (lane * 10)) & (if (lane == 3) 3L else 1023L)
      else {
        val value = if (!signed) bits
        else componentBytes.raw match {
          case 1 => bits.toByte.toLong
          case 2 => bits.toShort.toLong
          case _ => bits.toInt.toLong
        }
        value & 0xffffffffL
      }
    def encodePacked(values: Array[Long]): Long = (0 until 4).foldLeft(0L)((word, lane) =>
      word | (Math.min(values(lane) & 0xffffffffL, if (lane == 3) 3L else 1023L) << (lane * 10))
    )
    def encode(bits: Long): Long = {
      val value   = if (signed) bits.toInt.toLong else bits & 0xffffffffL
      val width   = componentBytes.raw * 8
      val minimum = if (signed) -(1L << (width - 1)) else 0L
      val maximum = if (signed) (1L << (width - 1)) - 1 else (1L << width) - 1
      Math.max(minimum, Math.min(maximum, value))
    }
  }
}

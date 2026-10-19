package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*

private object InstructionWord {
  def from(value: BigInt): InstructionWord =
    InstructionWord(value.toLong, (value >> 64).toLong, (value >> 128).toLong)

  /** Reads `size` bytes, a multiple of four no larger than 24. */
  def read(bytes: Array[Byte], offset: Int, size: Int): InstructionWord =
    InstructionWord(part(bytes, offset, size, 0), part(bytes, offset, size, 8), part(bytes, offset, size, 16))

  private def part(bytes: Array[Byte], offset: Int, size: Int, start: Int): Long =
    if (size - start >= 8) LittleEndian.decode(bytes, offset + start, 8)
    else if (size - start >= 4) LittleEndian.decode(bytes, offset + start, 4)
    else 0L
}

/** Up to 192 instruction bits as little-endian 64-bit words. */
private case class InstructionWord(low: Long, middle: Long, high: Long) derives CanEqual {
  def bits(offset: Int, count: Int): Long = {
    val index = offset >>> 6
    val shift = offset & 63
    val value =
      if (shift + count <= 64) word(index) >>> shift
      else (word(index) >>> shift) | (word(index + 1) << (64 - shift))
    if (count >= 64) value else value & ((1L << count) - 1)
  }

  def masked(mask: InstructionWord): InstructionWord =
    InstructionWord(low & mask.low, middle & mask.middle, high & mask.high)

  private def word(index: Int): Long = index match {
    case 0 => low
    case 1 => middle
    case 2 => high
    case _ => 0L
  }
}

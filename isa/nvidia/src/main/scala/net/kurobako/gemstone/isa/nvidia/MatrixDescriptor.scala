package net.kurobako.gemstone.isa.nvidia

/** The fields every shared memory matrix descriptor holds, `wgmma`'s and tcgen05's alike: the matrix's start and its
  * leading and stride dimension byte offsets, bits 4 to 17 of each, and the base offset of a swizzle pattern that
  * starts off its boundary. The swizzle mode sits above them, two bits at 62 for `wgmma` and three at 61 for tcgen05.
  */
private[isa] object MatrixDescriptor {
  def start(bits: Long): Long   = (bits & 0x3fffL) << 4
  def leading(bits: Long): Long = ((bits >>> 16) & 0x3fffL) << 4
  def stride(bits: Long): Long  = ((bits >>> 32) & 0x3fffL) << 4
  def base(bits: Long): Int     = ((bits >>> 49) & 7L).toInt

  /** The shared offset the 16-byte chunks of a swizzle pattern of `bytes`-wide rows move `offset` to, from a start the
    * pattern is aligned to: each 128-byte row's chunks exchanged by the row's index within the pattern. Matrix
    * descriptors and tensor maps swizzle alike; 16-byte rows do not swizzle.
    */
  def swizzled(offset: Long, bytes: Int): Long =
    if (bytes < 32) offset else offset ^ (((offset >>> 7) & (bytes / 16 - 1).toLong) << 4)
}

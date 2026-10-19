package net.kurobako.gemstone.isa.nvidia.fixtures

import net.kurobako.gemstone.isa.nvidia.*

/** Shared memory matrix descriptors as `wgmma` and tcgen05 encode them. */
object Descriptors {

  private def matrix(start: Long, leading: Long, stride: Long): Long =
    ((start >>> 4) & 0x3fffL) | (((leading >>> 4) & 0x3fffL) << 16) | (((stride >>> 4) & 0x3fffL) << 32)

  // call sites name these arguments
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def warpgroup(start: Long, leading: Long, stride: Long, swizzle: Warpgroup.Swizzle): Long =
    matrix(start = start, leading = leading, stride = stride) | (swizzle.ordinal.toLong << 62)

  /** A descriptor of swizzling mode `mode`, with the fixed version bit 46 sm_100 requires. */
  // call sites name these arguments
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def tensorMemory(start: Long, leading: Long, stride: Long, mode: Long): Long =
    matrix(start = start, leading = leading, stride = stride) | (1L << 46) | (mode << 61)
}

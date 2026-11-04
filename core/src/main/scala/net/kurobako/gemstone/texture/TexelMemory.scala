package net.kurobako.gemstone.texture

/** Guest memory as the sampler sees it. */
trait TexelMemory {

  /** The `size` bytes at `address`, or none when any of them is unmapped. */
  def read(address: Long, size: Int): Option[Array[Byte]]

  /** Whether all of `bytes` were written at `address`. */
  def write(address: Long, bytes: Array[Byte]): Boolean
}

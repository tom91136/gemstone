package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.vm.*

/** Where a sample reads texel bytes from: the last `read` leaves the texel's bits in `low` and `high`. */
private[isa] trait TexelReader {
  def read(address: Long, bytes: Int): Fault | Null
  def low: Long
  def high: Long
}

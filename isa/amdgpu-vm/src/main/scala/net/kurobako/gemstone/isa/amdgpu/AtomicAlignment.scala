package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** An atomic faults on an address that is not a multiple of its size: the unaligned SH_MEM_CONFIG mode that ROCm sets
  * on every generation relaxes only ordinary loads and stores. Rendered definitions require it of their addresses.
  */
private[isa] object AtomicAlignment {
  def address(at: UInt[64], bytes: Int): UInt[64] = where(accessed = true, at, bytes)

  def offset(at: UInt[32], bytes: Int): UInt[32] = {
    require((toRaw[UInt[32]](at) & (bytes - 1).toLong) == 0L, s"$bytes-byte atomic at a misaligned offset")
    at
  }

  /** `at`, which must be aligned where the atomic accesses it, as a buffer atomic out of range does not. */
  def where(accessed: Boolean, at: UInt[64], bytes: Int): UInt[64] = {
    require(
      !accessed || (toRaw[UInt[64]](at) & (bytes - 1).toLong) == 0L,
      s"$bytes-byte atomic at a misaligned address"
    )
    at
  }
}

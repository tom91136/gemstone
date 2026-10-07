package net.kurobako.gemstone.memory

/** Unsigned 64-bit address range checks shared by the memory views. */
object AddressRanges {

  /** True when `[address, address + width)` lies within `[base, base + extent)` under unsigned comparison. */
  def containsUnsigned(base: Long, extent: Long, address: Long, width: Long): Boolean = {
    val relative = address - base
    java.lang.Long.compareUnsigned(address, base) >= 0 &&
    width >= 0 && width <= extent &&
    java.lang.Long.compareUnsigned(relative, extent - width) <= 0
  }
}

package net.kurobako.gemstone.memory

/** Unsigned 64-bit address range checks shared by the memory views. */
object AddressRanges {

  /** True when `[address, address + width)` lies within `[base, base + extent)` under unsigned comparison. */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def containsUnsigned(base: Long, extent: Long, address: Long, width: Long): Boolean = {
    val relative = address - base
    java.lang.Long.compareUnsigned(address, base) >= 0 &&
    width >= 0 && width <= extent &&
    java.lang.Long.compareUnsigned(relative, extent - width) <= 0
  }
}

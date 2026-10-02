package net.kurobako.gemstone.isa.nvidia

/** Virtual-address apertures shared by the NVIDIA execution backend. */
object VMAddressSpace {
  inline val LocalLimit         = 0x100000000L
  inline val SharedBase         = 0x10000000L
  inline val SharedLimit        = 0x40000000L
  inline val KernelSharedStride = 0x100000L
  inline val ModuleSharedBase   = 0x20000000L
  inline val ModuleSharedStride = 0x10000000000L

  def isShared(address: Long): Boolean =
    java.lang.Long.compareUnsigned(address, SharedBase) >= 0 && java.lang.Long.compareUnsigned(address, SharedLimit) < 0
}

package net.kurobako.gemstone.isa.ptx.v9_3

/** Virtual-address apertures the PTX emulator assigns to local, shared, and module-shared storage. */
private[v9_3] object PTXAddressSpace {
  inline val LocalLimit         = 0x100000000L
  inline val SharedBase         = 0x10000000L
  inline val SharedLimit        = 0x40000000L
  inline val KernelSharedStride = 0x100000L
  inline val ModuleSharedBase   = 0x20000000L
  inline val ModuleSharedStride = 0x10000000000L

  def isShared(address: Long): Boolean =
    java.lang.Long.compareUnsigned(address, SharedBase) >= 0 && java.lang.Long.compareUnsigned(address, SharedLimit) < 0
}

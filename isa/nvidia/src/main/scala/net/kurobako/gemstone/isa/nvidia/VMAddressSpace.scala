package net.kurobako.gemstone.isa.nvidia

/** Virtual-address apertures shared by the NVIDIA execution backend. */
object VMAddressSpace {
  inline val LocalLimit         = 0x100000000L
  inline val SharedBase         = 0x10000000L
  inline val SharedLimit        = 0x40000000L
  inline val KernelSharedStride = 0x100000L
  inline val ModuleSharedBase   = 0x20000000L

  // SASS SR_SWINHI supplies the high word; its low word retains the raw shared offset.
  // Only rank zero is supported: bits 24..31 of that low word select another cluster CTA.
  inline val SharedWindowBase = 0x4000000000000000L
  inline val SharedWindowSize = 0x1000000L

  def isShared(address: Long, size: Int = 1): Boolean =
    size >= 0 &&
      ((address >= SharedBase && address <= SharedLimit - size.toLong) ||
        (address >= SharedWindowBase && address <= SharedWindowBase + SharedWindowSize - size.toLong))

  def sharedAddress(address: Long): Long =
    if (address >= SharedWindowBase && address < SharedWindowBase + SharedWindowSize)
      SharedBase + (address - SharedWindowBase)
    else address
}

package net.kurobako.gemstone.isa.nvidia

/** Virtual-address apertures shared by the NVIDIA execution backend. */
private[isa] object VMAddressSpace {
  inline val LocalLimit = 0x100000000L
  // PTX bounds a thread's local storage to 512 KiB, so frames this far apart keep literal local addresses private.
  inline val LocalFrameSize = 0x80000L
  inline val SharedBase     = 0x10000000L
  inline val SharedLimit    = 0x40000000L
  inline val ParameterBase  = 0x700000000L
  // an inline val's type is its constant, which a written type would only repeat
  @SuppressWarnings(Array("scalafix:ExplicitResultTypes"))
  inline val ParameterLimit = ParameterBase + 0x100000000L

  // Each cluster CTA's shared memory is a 16 MiB window. PTX shared::cluster addresses select it above
  // ClusterWindowBase; SASS selects it with bits 24..31 of the low word of an address whose high word is SR_SWINHI.
  inline val ClusterRankShift  = 24
  inline val ClusterWindowBase = 0x30000000L
  inline val SharedWindowBase  = 0x4000000000000000L
  inline val SharedWindowSize  = 0x100000000L
  inline val SharedOffsetMask  = 0xffffffL

  def isShared(address: Long, size: Int = 1): Boolean =
    size >= 0 &&
      ((address >= SharedBase && address <= SharedLimit - size.toLong) ||
        (address >= SharedWindowBase && address <= SharedWindowBase + SharedWindowSize - size.toLong))

  // Thread-local storage occupies the addresses below 4 GiB outside the shared aperture; the null address is in the
  // global window, as the hardware holds it.
  def isLocal(address: Long): Boolean =
    address != 0L && java.lang.Long.compareUnsigned(address, LocalLimit) < 0 && !isShared(address)

  /** Whether the address is in the window the kernel's parameters and the device's allocations occupy, which the
    * hardware holds in the constant window.
    */
  def isConstant(address: Long): Boolean = address >= ParameterBase && address < ParameterLimit

  /** The cluster rank whose window holds a shared address, or -1 for the issuing CTA's own shared::cta window. */
  def clusterRank(address: Long): Int =
    if (address >= SharedWindowBase && address < SharedWindowBase + SharedWindowSize)
      ((address - SharedWindowBase) >>> ClusterRankShift).toInt
    else if (address >= ClusterWindowBase && address < SharedLimit)
      ((address - ClusterWindowBase) >>> ClusterRankShift).toInt
    else -1

  /** The address a shared address names within its CTA's own shared::cta window. */
  def sharedAddress(address: Long): Long =
    if (clusterRank(address) < 0) address else SharedBase + (address & SharedOffsetMask)

  /** The PTX shared::cluster address of the shared location `address` within the CTA of rank `rank`. */
  def clusterAddress(address: Long, rank: Int): Long =
    ClusterWindowBase + (rank.toLong << ClusterRankShift) + ((sharedAddress(address) - SharedBase) & SharedOffsetMask)
}

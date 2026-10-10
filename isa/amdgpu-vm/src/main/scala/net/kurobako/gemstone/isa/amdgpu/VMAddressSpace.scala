package net.kurobako.gemstone.isa.amdgpu

/** Flat local/private apertures occupy non-canonical addresses, outside global memory. */
private object VMAddressSpace {
  val LocalBase           = 0xffff000000000000L
  val PrivateBase         = 0xfffe000000000000L
  val SegmentMask         = 0xffffffffL
  val LocalLimit: Long    = LocalBase | SegmentMask
  val PrivateLimit: Long  = PrivateBase | SegmentMask
  val HardwareBases: Long = (LocalBase >>> 48 << 16) | (PrivateBase >>> 48)

  def isLocal(address: Long): Boolean   = (address & ~SegmentMask) == LocalBase
  def isPrivate(address: Long): Boolean = (address & ~SegmentMask) == PrivateBase
  def offset(address: Long): Long       = address & SegmentMask
}

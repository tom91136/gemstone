package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.memory.*

/** Flat local/private apertures occupy non-canonical addresses, outside global memory. */
private object VMAddressSpace {
  val LocalBase           = AddressLayout.AmdgpuLocalBase
  val PrivateBase         = AddressLayout.AmdgpuPrivateBase
  val SegmentMask         = 0xffffffffL
  val LocalLimit: Long    = LocalBase | SegmentMask
  val PrivateLimit: Long  = PrivateBase | SegmentMask
  val HardwareBases: Long = (LocalBase >>> 48 << 16) | (PrivateBase >>> 48)

  def isLocal(address: Long): Boolean   = (address & ~SegmentMask) == LocalBase
  def isPrivate(address: Long): Boolean = (address & ~SegmentMask) == PrivateBase
  def offset(address: Long): Long       = address & SegmentMask
}

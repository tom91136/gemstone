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

  /** The 48-bit address a bit-47 sign-extended one names, for resolving code windows and what they hold. */
  def canonical(address: Long): Long = if ((address >> 47) == -1L) address & 0xffffffffffffL else address
}

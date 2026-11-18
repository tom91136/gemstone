package net.kurobako.gemstone.testing

import net.kurobako.gemstone.memory.*

/** Values a host program's user-space pointer can take: the bottom and top of the range, a typical heap address, and an
  * address inside each window an earlier layout mapped. The reserved range itself is excluded: the host cannot hold a
  * pointer there.
  */
object HostAddresses {
  val Bottom: Long = 0x10000L
  val Top: Long    = AddressLayout.HostUserLimit - 0x1000L

  /** The top of x86-64 4-level user space and an address past the reserved range, reachable on larger address spaces.
    */
  val BelowReserved: Long = AddressLayout.ReservedBase - 0x1000L
  val AboveReserved: Long = AddressLayout.ReservedLimit + 0x1000L

  private val earlierWindows = List(
    0x1000000000L,
    0x100000000000L,
    0x200000000000L,
    0x300000000000L,
    0x3f0000000000L,
    0x400000000000L,
    0x700000000L,
    0x7c0000000000L
  )

  /** An address the host cannot hold: inside the range the native shims reserve, or beyond every user-space pointer. */
  def guestOnly(address: Long): Boolean =
    (address >= AddressLayout.ReservedBase && address < AddressLayout.ReservedLimit) ||
      java.lang.Long.compareUnsigned(address, AddressLayout.HostUserLimit) >= 0

  val Samples: List[Long] = List(Bottom, 0x400000L, 0x7f405e98b490L, BelowReserved, AboveReserved, Top) ++
    earlierWindows.map(_ + 8L)

  /** Samples clear of the 4 GiB generic window an NVIDIA machine reads as thread-local storage. */
  val AboveLocalWindow: List[Long] = Samples.filter(_ >= 0x100000000L)
}

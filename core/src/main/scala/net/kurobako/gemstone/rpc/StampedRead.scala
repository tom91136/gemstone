package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.vm.AddressSpace

/** One guest read and the invalidation generation its caller began at. */
case class StampedRead(address: GuestAddress, size: Int >= 0, since: Long = AddressSpace.Now)

package net.kurobako.gemstone.vm

import net.kurobako.gemstone.memory.GuestAddress

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

trait AddressSpace {
  def address(
      region: MemoryRegion,
      offset: Long :| Positive0
  ): Either[Fault, GuestAddress]

  def locate(
      address: GuestAddress,
      size: Int :| Positive0
  ): Either[Fault, AddressSpace.Location]
}

object AddressSpace {
  final case class Location(region: MemoryRegion, offset: Long :| Positive0)
}

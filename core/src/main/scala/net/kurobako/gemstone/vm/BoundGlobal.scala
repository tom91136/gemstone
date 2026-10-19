package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

/** A global's placement and the address space it was bound in. */
case class BoundGlobal(binding: GlobalBinding, memory: AddressSpace) derives CanEqual {

  /** `bytes` written at `offset` within the global, or `exceeded` where they do not fit in it. */
  def write(offset: Long >= 0, bytes: Vec[Byte])(exceeded: => Fault): Either[Fault, Unit] =
    if (!binding.contains(offset, bytes.nonNegativeSize.widen[Long >= 0])) Left(exceeded)
    else memory.write(binding.region, binding.offset + offset, bytes)
}

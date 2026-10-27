package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.prelude.*

/** The pointer queries rendered definitions request from the machine. */
private[isa] trait Pointers {
  def arrayLength(structure: Any, member: Int): UInt[64]
  def isAddress(pointer: Any): Bool
}

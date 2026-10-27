package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.prelude.*

/** The clock reads rendered definitions make of the machine. */
private[isa] trait Clocks {
  def readClock(scope: Any): UInt[64]
}

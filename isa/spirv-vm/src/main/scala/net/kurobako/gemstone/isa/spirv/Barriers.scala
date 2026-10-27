package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.prelude.*

/** The barrier and fence requests rendered definitions make of the machine. */
private[isa] trait Barriers {
  def controlBarrier(scope: Any, memory: Any, semantics: Any): Unit
  def arriveBarrier(scope: Any, memory: Any, semantics: Any): Unit
  def fence(scope: Any, semantics: Any): Unit
  def namedBarrier(barrier: Any, memory: Any, semantics: Any): Unit
  def initializeBarrier(count: Any): UInt[64]
}

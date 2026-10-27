package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.prelude.*

/** The atomic accesses rendered definitions request from the machine. */
private[isa] trait Atomics {
  def load[T](pointer: Any, scope: Any, semantics: Any): T
  def store(pointer: Any, value: Any, scope: Any, semantics: Any): Unit
  def flagTestAndSet(pointer: Any, scope: Any, semantics: Any): Bool
  def flagClear(pointer: Any, scope: Any, semantics: Any): Unit
}

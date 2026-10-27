package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.semantics.prelude.*

/** The values the lanes of a unit hold, which rendered machine definitions compare across the unit. */
private[isa] trait Units {

  /** Whether every lane of the unit that holds the rendered lane holds the same value in `view`. */
  def agreed[V](view: V): Bool

  /** The index of the rendered lane's unit among the units of the workgroup. */
  def unitIndex(): UInt[64]
}

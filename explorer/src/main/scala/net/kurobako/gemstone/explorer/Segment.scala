package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.vm.*

/** One stretch of a unit's life in one state, over the issue stream. `end` is exclusive. */
case class Segment(start: Long, end: Long, state: MachineUnit.State) {
  def width: Long = end - start
}

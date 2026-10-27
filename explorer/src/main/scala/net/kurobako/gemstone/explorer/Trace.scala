package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*

object Trace {
  val empty: Trace = Trace(Vec.empty, Vec.empty)
}

/** What a launch did, in the order it did it: the issues it made, and the changes its units went through between them.
  * The issue ordinals are the launch's time axis, and a change names the ordinal of the issue the launch makes next.
  */
case class Trace(steps: Vec[TraceStep], changes: Vec[MachineEvent]) {

  /** The issues the launch made. */
  def issues: Long = steps.size.toLong

  /** The instruction the launch issued at `issue`, when it issued one there. */
  def stepAt(issue: Long): Option[TraceStep] = steps.lift(issue.toInt)
}

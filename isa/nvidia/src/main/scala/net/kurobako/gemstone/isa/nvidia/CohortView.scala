package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** Lanes that execute an NVIDIA instruction together, as an observer sees them before it. Registers and predicates are
  * the slots of the lowered program that PTX and SASS share, not source register names.
  */
trait CohortView extends ArchitecturalState {
  def function: NonEmpty[String]

  /** The active lanes, by index within the workgroup. */
  def lanes: Vec[Int >= 0]

  /** The source name of each register slot the executing routine declared, in slot order. */
  def registerNames: Vec[NonEmpty[String]]

  /** The source name of each predicate slot the executing routine declared, in slot order. */
  def predicateNames: Vec[NonEmpty[String]]

  def register(lane: Int >= 0, slot: Int >= 0): Long

  /** A predicate slot's value, unless the lane has not defined it. */
  def predicate(lane: Int >= 0, slot: Int >= 0): Option[Boolean]
}

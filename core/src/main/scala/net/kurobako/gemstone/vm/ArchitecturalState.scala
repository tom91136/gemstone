package net.kurobako.gemstone.vm

/** A backend's read-only view of the unit about to execute an instruction, in its ISA's own terms. It is valid only
  * during the observer call that received it.
  */
trait ArchitecturalState {

  /** The unit the backend runs the instruction on, and what it is doing. */
  def unit: MachineUnit

  /** The ordinal of this issue within the launch, counting every issue in order. */
  def issued: Long
}

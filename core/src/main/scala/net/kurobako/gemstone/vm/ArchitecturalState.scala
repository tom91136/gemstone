package net.kurobako.gemstone.vm

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.profile.*

/** A backend's read-only view of the unit about to execute an instruction, in its ISA's own terms. It is valid only
  * during the observer call that received it.
  */
trait ArchitecturalState {

  /** The unit the backend runs the instruction on, and what it is doing. */
  def unit: MachineUnit

  /** The ordinal of this issue within the launch, counting every issue in order. */
  def issued: Long

  /** Whether this issue begins a source instruction, which hardware would issue. It does not when it carries on one an
    * earlier issue of its lanes began, or when the machine added it without a source instruction.
    */
  def begins: Boolean

  /** The lanes the hardware issues together: a warp, a wave or a subgroup. */
  def subgroupSize: Int > 0

  /** `each` over the lanes the issue executes for, by index within the workgroup, in ascending order. */
  def eachLane(each: (Int >= 0) => Unit): Unit

  /** The instruction about to issue, as the ISA's assembly spells it. It may allocate, so an observer that needs it
    * takes it once per instruction address.
    */
  def text: String

  /** The static issue schedule the compiler encoded for the instruction, where the ISA encodes one. */
  def schedule: Option[Schedule] = None

  /** The compute pipe the instruction issues to, where the ISA's decoder knows it. */
  def pipe: Option[Pipe] = None
}

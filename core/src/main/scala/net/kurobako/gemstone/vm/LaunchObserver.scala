package net.kurobako.gemstone.vm

import net.kurobako.gemstone.constraint.*
import scala.annotation.unused

object LaunchObserver {
  enum AccessKind {
    case Read, Write, Atomic
  }

  case class Access(kind: AccessKind, location: AddressSpace.Location, size: Int >= 0)
}

/** Watches one launch. Backends consult it only when one is attached, so unobserved launches run unchanged. Calls come
  * from the threads running workgroups; under the sequential scheduler they arrive in execution order on one thread, so
  * blocking in [[instruction]] single-steps the launch.
  */
trait LaunchObserver derives CanEqual {

  /** Called before the instruction at `at` executes; a fault ends the launch with it. */
  def instruction(at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit]

  /** Called for each access to device-visible memory, before it happens. */
  def memory(@unused access: LaunchObserver.Access): Unit = ()

  /** Called when a unit changes what it is doing, such as parking at a barrier or finishing. An issue is reported by
    * [[instruction]], so only the changes between issues come through here.
    */
  def transition(@unused event: MachineEvent): Unit = ()

  /** Called after the instruction issued at `issued` has run, with the registers it read and wrote. A machine whose
    * view names no registers reports none.
    */
  def registers(@unused issued: Long, @unused use: RegisterUse): Unit = ()
}

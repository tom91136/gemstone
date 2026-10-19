package net.kurobako.gemstone.vm

import net.kurobako.gemstone.constraint.*

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
  def memory(@scala.annotation.unused access: LaunchObserver.Access): Unit = ()
}

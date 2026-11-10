package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.*
import net.kurobako.gemstone.semantics.*
import scala.annotation.unused

object LaunchObserver {
  enum AccessKind derives CanEqual {
    case Read, Write, Atomic
  }

  case class Access(kind: AccessKind, location: AddressSpace.Location, size: Int >= 0)

  /** The `target` that names no barrier or class: a transfer completes with its group, and a backend with a single
    * class of groups commits and waits on that class.
    */
  val DefaultTarget: Long = -1L

  /** Runs `report` on the attached observer. Inlined so that an unobserved launch builds no arguments for it. */
  extension (observer: LaunchObserver | Null) {
    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    inline def observed(inline report: LaunchObserver => Unit): Unit = observer match {
      case null                     => ()
      case attached: LaunchObserver => report(attached)
    }
  }

  /** Both observers, `first` before `second`; a fault from `first` stops the instruction before `second` sees it. */
  def both(first: LaunchObserver, second: LaunchObserver): LaunchObserver = Both(first, second)

  private class Both(first: LaunchObserver, second: LaunchObserver) extends LaunchObserver {
    def instruction(at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit] =
      first.instruction(at, state).flatMap(_ => second.instruction(at, state))
    override def memory(access: Access): Unit          = { first.memory(access); second.memory(access) }
    override def transition(event: MachineEvent): Unit = { first.transition(event); second.transition(event) }
    override def registers(issued: Long, use: => RegisterUse): Unit = {
      lazy val once = use
      first.registers(issued, once)
      second.registers(issued, once)
    }
    override def launched(resources: KernelResources): Unit = { first.launched(resources); second.launched(resources) }
    override def controlFlow(kernel: String, graph: => ControlFlowGraph, offsets: Vec[Long]): Unit = {
      lazy val once = graph
      first.controlFlow(kernel, once, offsets)
      second.controlFlow(kernel, once, offsets)
    }
    override def controlFlowLost(kernel: String): Unit = {
      first.controlFlowLost(kernel)
      second.controlFlowLost(kernel)
    }
    override def access(lane: Int >= 0, space: Space, kind: AccessKind, address: Long, bytes: Int >= 0): Unit = {
      first.access(lane, space, kind, address, bytes)
      second.access(lane, space, kind, address, bytes)
    }
    override def asynchronous(lane: Int >= 0, kind: AsyncKind, target: Long, bytes: Long): Unit = {
      first.asynchronous(lane, kind, target, bytes)
      second.asynchronous(lane, kind, target, bytes)
    }
  }

  /** A step of asynchronous work, which the machine completes at issue but hardware completes later. */
  enum AsyncKind derives CanEqual {

    /** A transfer of `bytes` begins; it completes on the barrier at `target`, or with its group when `target` is
      * [[DefaultTarget]].
      */
    case Transfer

    /** The lane's transfers since its last commit form a group of the class `target` names: 0 for `cp.async` groups, 1
      * for bulk async-groups, [[DefaultTarget]] where a backend has a single class.
      */
    case Commit

    /** The lane waits until at most `bytes` of its committed groups of the class `target` names are outstanding, with
      * the encoding of [[Commit]].
      */
    case WaitGroups

    /** The lane arrives at the barrier at `target`, expecting `bytes` more of transfers to complete on it. */
    case Arrive

    /** The lane waits on the phase `bytes` of the barrier at `target`. */
    case Wait

    /** The lane expects `bytes` more of transfers to complete on the barrier at `target`. */
    case Expect

    /** `bytes` of transfers complete on the barrier at `target`. */
    case Complete

    /** The lane waits until the counter `target` names is at most `bytes`: 0 vm (vmcnt, loadcnt), 1 exp (expcnt), 2
      * lgkm (lgkmcnt, dscnt), 3 vs (vscnt, storecnt), 4 km (kmcnt), 5 sample (samplecnt), 6 bvh (bvhcnt), 7 x (xcnt), 8
      * async (asynccnt).
      */
    case WaitCounter
  }
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
  def registers(@unused issued: Long, @unused use: => RegisterUse): Unit = ()

  /** Called once before the launch issues anything, with what each of its workgroups holds on the device. */
  def launched(@unused resources: KernelResources): Unit = ()

  /** Called once per launch of `kernel` before it issues, with its control flow and the address of each of its
    * instructions as [[instruction]] reports them.
    */
  def controlFlow(@unused kernel: String, @unused graph: => ControlFlowGraph, @unused offsets: Vec[Long]): Unit = ()

  /** Called when a wave of `kernel` enters code of another program, after which the addresses that launch reports no
    * longer match its graph.
    */
  def controlFlowLost(@unused kernel: String): Unit = ()

  /** Called for each lane's access to memory in any space, between [[instruction]] for the issue that makes it and the
    * next. `lane` is the lane's index within its workgroup; `space` is the space the access resolved to, never
    * [[Space.Generic]], and `address` is in that space's own addressing. PTX `ld.param` reads report
    * [[Space.Constant]]; SASS constant-bank operands record no access. The profiler's global and shared models ignore
    * that space.
    */
  def access(
      @unused lane: Int >= 0,
      @unused space: Space,
      @unused kind: LaunchObserver.AccessKind,
      @unused address: Long,
      @unused bytes: Int >= 0
  ): Unit = ()

  /** Called for each step of asynchronous work a lane makes, between [[instruction]] for its issue and the next. */
  def asynchronous(
      @unused lane: Int >= 0,
      @unused kind: LaunchObserver.AsyncKind,
      @unused target: Long,
      @unused bytes: Long
  ): Unit = ()
}

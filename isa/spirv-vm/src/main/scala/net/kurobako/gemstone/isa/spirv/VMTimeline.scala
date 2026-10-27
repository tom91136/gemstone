package net.kurobako.gemstone.isa.spirv

import VMCollectives.Kind as CollectiveKind
import VMCollectives.Request as CollectiveRequest
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

private object VMTimeline {

  /** What a collective rendezvous is called in the timeline. */
  def name(request: CollectiveRequest): String = request.kind match {
    case CollectiveKind.Barrier =>
      if (request.scope == Scope.Workgroup) "workgroup barrier" else "subgroup barrier"
    case CollectiveKind.Elect           => "election"
    case CollectiveKind.AllEqual        => "all equal"
    case CollectiveKind.BroadcastFirst  => "first broadcast"
    case CollectiveKind.Broadcast       => "broadcast"
    case CollectiveKind.Ballot          => "ballot"
    case CollectiveKind.Partition       => "partition"
    case CollectiveKind.Shuffle         => "shuffle"
    case CollectiveKind.ShuffleUp       => "shuffle up"
    case CollectiveKind.ShuffleDown     => "shuffle down"
    case CollectiveKind.Any             => "vote"
    case CollectiveKind.All             => "vote"
    case CollectiveKind.PipeReservation => "pipe reservation"
    case CollectiveKind.MatrixMultiply  => "matrix multiply"
    case CollectiveKind.MatrixRows      => "matrix rows"
    // The group operations reduce or scan the lanes that meet at them.
    case _ => if (request.operation == 0) "reduction" else "scan"
  }
}

/** The timeline of one launch: the unit every invocation runs as, and the changes it goes through. One invocation is
  * one unit of execution, because SPIR-V does not coalesce lanes: a workgroup of thirty-two invocations issues
  * thirty-two instructions, one for each invocation, and the timeline normalises by lanes.
  */
private class VMTimeline(kernel: NonEmpty[String], control: TaskControl) {

  /** The observer watching the launch, when one is attached. */
  def observer: Option[LaunchObserver] = control.observer

  /** The ordinal of the next issue within the launch, which the timeline's time axis is. */
  def issue(): Long = control.nextIssue()

  /** The unit an invocation runs as: its workgroup, its index within it, and the one lane it runs. */
  def unit(workgroup: Coordinates, ordinal: Int >= 0, state: MachineUnit.State): MachineUnit =
    MachineUnit(workgroup, ordinal, 1, state)

  /** Where in the program an invocation is, which the timeline records when it knows. */
  def location(pc: Int, workgroup: Coordinates, ordinal: Int >= 0): Fault.Location =
    Fault.Location(
      kernel,
      None,
      Some(pc.toLong.unsafe[Long >= 0]),
      Some(workgroup),
      Some(ordinal)
    )

  /** A change in what a unit is doing, which the timeline records between the issues it surrounds. */
  def transition(
      workgroup: Coordinates,
      ordinal: Int >= 0,
      kind: MachineEvent.Kind,
      at: => Option[Fault.Location]
  ): Unit =
    control.observer.foreach { observer =>
      observer.transition(MachineEvent(unit(workgroup, ordinal, kind.state), kind, control.issued(), at))
    }
}

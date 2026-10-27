package net.kurobako.gemstone.vm

object MachineEvent {

  /** What a unit does between issues. */
  enum Kind derives CanEqual {

    /** The unit issued the instruction at the event's location. */
    case Issue

    /** The unit parked, waiting for the machine to release it. */
    case Blocked(reason: MachineUnit.Reason)

    /** The unit resumed after parking. */
    case Resumed

    /** The unit finished; it issues nothing more. */
    case Retired

    /** The state the unit is in once this happens. */
    def state: MachineUnit.State = this match {
      case Blocked(reason) => MachineUnit.State.Blocked(reason)
      case Retired         => MachineUnit.State.Retired
      case Issue | Resumed => MachineUnit.State.Running
    }
  }
}

/** One thing a unit of execution did, at an ordinal of the launch's issue stream. A timeline is the sequence of these.
  * A backend reports them from the machine that runs the program, so the timeline describes the run that happened
  * rather than a model of one.
  */
case class MachineEvent(
    /** The unit that did it. */
    unit: MachineUnit,
    /** What it did. */
    kind: MachineEvent.Kind,
    /** The ordinal of the launch's issue stream at which it happened. */
    issued: Long,
    /** Where in the program it happened, when the backend knows. */
    at: Option[Fault.Location]
) derives CanEqual

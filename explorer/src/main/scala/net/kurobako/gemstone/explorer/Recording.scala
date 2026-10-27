package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

object Recording {

  /** How many issues a recording keeps. A launch that issues more still runs to completion, but the explorer stops
    * carrying it, so a long kernel cannot exhaust the page's memory.
    */
  val Limit: Int = 200000

  /** How many of the recorded issues also keep the machine's values. Reading values at every issue of a long launch
    * costs more than the timeline they belong to, so the explorer keeps only the first stretch of them.
    */
  val ValueLimit: Int = 20000

  /** How many lanes of a unit the stepper carries values for. */
  val Lanes: Int = 4

  /** How many registers of each lane the stepper carries values for. */
  val Registers: Int = 16
}

/** Records a launch's issues and the changes its units go through, so the explorer can draw and step through them. */
class Recording extends LaunchObserver {

  private val issued  = mutable.ArrayBuffer.empty[TraceStep]
  private val changed = mutable.ArrayBuffer.empty[MachineEvent]

  /** Whether the recording kept the whole launch. */
  def complete: Boolean = issued.size < Recording.Limit

  def instruction(at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit] = {
    if (complete) {
      val values =
        if (issued.size < Recording.ValueLimit) Registers.of(state, Recording.Lanes, Recording.Registers)
        else Vec.empty[RegisterValue]
      issued += TraceStep(state.issued, at, state.unit, values)
    }
    Right(())
  }

  override def transition(event: MachineEvent): Unit =
    if (complete) changed += event

  /** Attached to the step the instruction just reported, which is the last one the recording holds. */
  override def registers(at: Long, use: RegisterUse): Unit =
    if (issued.nonEmpty && issued.last.issue == at) issued(issued.size - 1) = issued.last.copy(use = use)

  def trace: Trace = Trace(Vec.from(issued), Vec.from(changed))
}

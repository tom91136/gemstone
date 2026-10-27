package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.vm.*

/** Reads the values a machine holds at one issue, in the terms its own view exposes them. A backend whose view names no
  * registers reports none, so the stepper shows only what the machine actually says.
  */
object Registers {

  /** The values of the first `lanes` lanes of `state`, naming at most `registers` of each lane's registers. */
  def of(state: ArchitecturalState, lanes: Int, registers: Int): Vec[RegisterValue] = state match {
    case view: CohortView => cohort(view, lanes, registers)
    case view: WaveView   => wave(view, registers)
    case _                => Vec.empty
  }

  /** NVIDIA's view names the slots the lowering allocated, so each value carries the declaration it came from. */
  private def cohort(view: CohortView, lanes: Int, registers: Int): Vec[RegisterValue] = {
    val active = view.lanes.take(lanes)
    val values = view.registerNames.take(registers).zipWithIndex.flatMap { (name, slot) =>
      val index = slot.unsafe[Int >= 0]
      active.map(lane => RegisterValue(lane.raw, name.raw, view.register(lane, index)))
    }
    val predicates = view.predicateNames.take(registers).zipWithIndex.flatMap { (name, slot) =>
      val index = slot.unsafe[Int >= 0]
      active.flatMap(lane =>
        view.predicate(lane, index).map(value => RegisterValue(lane.raw, name.raw, if (value) 1L else 0L))
      )
    }
    Vec.from(values ++ predicates)
  }

  /** AMDGPU's view names its register files by number, and one wave issues one instruction for all its lanes. */
  private def wave(view: WaveView, registers: Int): Vec[RegisterValue] = {
    val lane    = java.lang.Long.numberOfTrailingZeros(view.exec)
    val scalars = Vec.tabulate(math.min(registers, 128))(index =>
      RegisterValue(-1, s"s$index", view.scalar(index.unsafe[Int in 0 ~ 127]))
    )
    val vectors =
      if (lane >= 0 && lane < view.lanes.raw) {
        val holder = lane.unsafe[Int in 0 ~ 63]
        Vec.tabulate(math.min(registers, 256))(index =>
          RegisterValue(lane, s"v$index", view.vector(index.unsafe[Int in 0 ~ 255], holder))
        )
      } else Vec.empty[RegisterValue]
    scalars ++ vectors
  }
}

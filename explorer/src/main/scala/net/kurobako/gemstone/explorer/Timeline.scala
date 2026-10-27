package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** Lays a trace out as occupancy over the issue stream: one row per workgroup, one band per unit, and a band's height
  * the lanes the unit carries. A blocked stretch is the time the unit spent waiting for the machine.
  */
object Timeline {

  def layout(trace: Trace): Vec[Row] = {
    // One pass over the issues, because a launch with thousands of invocations would otherwise scan the trace once per
    // unit.
    val units  = mutable.LinkedHashMap.empty[UnitKey, MachineUnit]
    val issued = mutable.LinkedHashMap.empty[UnitKey, mutable.ArrayBuffer[Long]]
    trace.steps.foreach { step =>
      val key = UnitKey.of(step.unit)
      issued.getOrElseUpdate(key, mutable.ArrayBuffer.empty) += step.issue
      units.updateWith(key) {
        case Some(unit) => Some(if (unit.lanes.raw >= step.unit.lanes.raw) unit else step.unit)
        case None       => Some(step.unit)
      }
    }
    val bands = issued.toVector.map((key, values) => band(trace, units(key), values))
    bands.groupBy(band => band.unit.workgroup).toVec
      .sortBy((workgroup, _) => (workgroup.x.raw, workgroup.y.raw, workgroup.z.raw))
      .map((workgroup, found) =>
        Row(workgroup, Vec.from(found), found.map(_.unit.lanes.raw).maxOption.getOrElse(0))
      )
  }

  /** One unit's states over the stream: it runs while it issues, and waits where it parks. */
  private def band(trace: Trace, unit: MachineUnit, issued: mutable.ArrayBuffer[Long]): Band = {
    val key                     = UnitKey.of(unit)
    val start                   = issued.minOption.getOrElse(0L)
    val finish                  = issued.maxOption.fold(start)(_ + 1L)
    val changes                 = trace.changes.filter(event => UnitKey.of(event.unit) == key).sortBy(_.issued)
    val (segments, from, state) =
      changes.foldLeft((Vec.empty[Segment], start, MachineUnit.State.Running: MachineUnit.State)) {
        case ((found, at, current), event) =>
          val now = math.max(event.issued, at)
          event.kind match {
            case MachineEvent.Kind.Blocked(reason) =>
              (extend(found, at, now, current), now, MachineUnit.State.Blocked(reason))
            case MachineEvent.Kind.Resumed =>
              (extend(found, at, now, current), now, MachineUnit.State.Running)
            case MachineEvent.Kind.Retired =>
              (extend(found, at, now, current), now, MachineUnit.State.Retired)
            case MachineEvent.Kind.Issue => (found, at, current)
          }
      }
    val end = math.max(from, finish)
    Band(unit, extend(segments, from, end, state))
  }

  private def extend(found: Vec[Segment], from: Long, to: Long, state: MachineUnit.State): Vec[Segment] =
    if (to > from) found :+ Segment(start = from, end = to, state = state) else found
}

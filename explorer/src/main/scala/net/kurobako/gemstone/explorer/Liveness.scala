package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

object Liveness {

  val empty: Liveness = Liveness(Vec.empty, Vec.empty, 0, reported = false, Vec.empty)

  /** The registers live along `trace`'s executed path, from each issue's uses and definitions. A register is live
    * before an issue when the unit that runs it reads it before writing it again, so the sets are exact for the path
    * the launch took. Units that hold registers while parked keep them live until they need them, which is what makes
    * the pressure of a barrier visible.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def of(trace: Trace): Liveness = {
    val steps = trace.steps
    if (steps.isEmpty) empty
    else {
      // The steps of each unit, in the order it issued them, which is the order liveness runs backwards over.
      val perUnit = mutable.LinkedHashMap.empty[UnitKey, mutable.ArrayBuffer[Int]]
      var index   = 0
      while (index < steps.size) {
        perUnit.getOrElseUpdate(UnitKey.of(steps(index).unit), mutable.ArrayBuffer.empty) += index
        index += 1
      }
      // The live set before each issue, the next issue of the same unit, and each register's live spans.
      val perStep   = Array.fill(steps.size)(0)
      val next      = Array.fill(steps.size)(-1)
      val intervals = mutable.LinkedHashMap.empty[RegisterKey, mutable.ArrayBuffer[(Long, Long)]]
      perUnit.foreach { (_, indices) =>
        val live = mutable.HashSet.empty[RegisterKey]
        // The end of the live span being followed backwards; it closes at the write that makes the register dead.
        val open = mutable.HashMap.empty[RegisterKey, Long]
        var at   = indices.size - 1
        while (at >= 0) {
          val step = steps(indices(at))
          if (at + 1 < indices.size) next(indices(at)) = indices(at + 1)
          val reads   = step.use.reads.map(name => RegisterKey.of(step, name))
          val readSet = reads.toSet
          val writes  = step.use.writes.map(name => RegisterKey.of(step, name))
          writes.foreach { key =>
            if (live.contains(key) && !readSet.contains(key)) {
              live -= key
              open.remove(key).foreach { end =>
                intervals.getOrElseUpdate(key, mutable.ArrayBuffer.empty) += ((indices(at).toLong + 1L, end))
              }
            }
          }
          reads.foreach { key =>
            if (!live.contains(key)) { live += key; open(key) = indices(at).toLong + 1L }
          }
          perStep(indices(at)) = live.size
          at -= 1
        }
        // Anything still live was live before the unit's first issue.
        open.foreach { (key, end) =>
          intervals.getOrElseUpdate(key, mutable.ArrayBuffer.empty) += ((0L, end))
        }
      }
      // The registers each unit keeps live, and the total of the units resident at each issue.
      val contribution                           = mutable.HashMap.empty[UnitKey, Int]
      var live                                   = 0
      def place(unit: UnitKey, count: Int): Unit = {
        val previous = contribution.getOrElse(unit, 0)
        if (previous != count) { live += count - previous; contribution(unit) = count }
      }
      val retirements = trace.changes
        .filter(_.kind == MachineEvent.Kind.Retired)
        .sortBy(_.issued)
      val pressure = Array.fill(steps.size)(0)
      var retired  = 0
      index = 0
      while (index < steps.size) {
        val step = steps(index)
        while (retired < retirements.size && retirements(retired).issued <= step.issue) {
          place(UnitKey.of(retirements(retired).unit), 0)
          retired += 1
        }
        val unit = UnitKey.of(step.unit)
        place(unit, perStep(index))
        pressure(index) = live
        // Between this issue and the unit's next one, the registers it still needs are the next issue's live set.
        place(unit, if (next(index) >= 0) perStep(next(index)) else 0)
        index += 1
      }
      val registers = Vec.from(intervals.toVector.map { (key, spans) =>
        RegisterTimeline(key.function, key.name, coverage(spans))
      }).sortBy(row => (row.start, row.function, row.name))
      Liveness(
        Vec.from(perStep),
        Vec.from(pressure),
        pressure.maxOption.getOrElse(0),
        steps.exists(step => !step.use.isEmpty),
        registers
      )
    }
  }

  /** The spans of `spans`, with the units that overlap in each stretch counted once and neighbouring stretches of the
    * same count merged.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def coverage(spans: mutable.ArrayBuffer[(Long, Long)]): Vec[LiveSpan] = {
    val events = mutable.ArrayBuffer.empty[(Long, Int)]
    spans.foreach { (start, end) =>
      events += ((start, 1))
      events += ((end, -1))
    }
    val ordered  = events.sortBy((position, change) => (position, -change))
    val gathered = mutable.ArrayBuffer.empty[LiveSpan]
    var previous = 0L
    var units    = 0
    ordered.foreach { (position, change) =>
      if (position > previous && units > 0) gathered += LiveSpan(previous, position, units)
      units += change
      previous = position
    }
    val merged = mutable.ArrayBuffer.empty[LiveSpan]
    gathered.foreach { span =>
      merged.lastOption match {
        case Some(last) if last.end == span.start && last.units == span.units =>
          merged(merged.size - 1) = last.copy(end = span.end)
        case _ => merged += span
      }
    }
    Vec.from(merged)
  }
}

/** The registers live before each issue of a launch, each register's live spans over the issue stream, the total of the
  * units resident at each issue, and whether the machine reported register uses at all.
  */
case class Liveness(
    perStep: Vec[Int],
    pressure: Vec[Int],
    peak: Int,
    reported: Boolean,
    registers: Vec[RegisterTimeline]
) {

  def isEmpty: Boolean = perStep.isEmpty

  /** The registers live before the issue at `index`, named as the issuing unit names them. Names from another function
    * are prefixed with it, because a call's registers are not the caller's.
    */
  def liveAt(trace: Trace, index: Int): Vec[String] = trace.steps.lift(index).fold(Vec.empty[String]) { step =>
    val unit = UnitKey.of(step.unit)
    val live = (trace.steps.size - 1 to index by -1).foldLeft(mutable.HashSet.empty[RegisterKey]) { (found, at) =>
      val other = trace.steps(at)
      if (UnitKey.of(other.unit) == unit) {
        other.use.writes.foreach(name => found -= RegisterKey.of(other, name))
        other.use.reads.foreach(name => found += RegisterKey.of(other, name))
      }
      found
    }
    val here = step.location.function.fold("")(_.raw)
    Vec.from(live.toVector.sortBy(key => (key.function, key.name)).map { key =>
      if (key.function.isEmpty || key.function == here) key.name else s"${key.function}:${key.name}"
    })
  }
}

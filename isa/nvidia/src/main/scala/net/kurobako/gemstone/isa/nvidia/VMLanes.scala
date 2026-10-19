package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*

private object VMLanes {
  private val NoLanes = Array.empty[VMLane | Null]
}

/** Lane storage of one frame, taken per lane on first use from `pool`. */
@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
private class VMLanes(laneCount: Int in 1 ~ 1024, pool: VMFramePool) {
  private val lanes          = java.util.Arrays.copyOf(VMLanes.NoLanes, laneCount)
  private var registerCount  = 0: Int >= 0
  private var predicateCount = 0: Int >= 0

  /** Sizes lanes taken from now on for `routine`; this storage holds none when it is prepared. */
  def prepare(routine: VMIR.Routine): Unit = {
    registerCount = routine.registerCount
    predicateCount = routine.predicateCount
  }

  def apply(lane: WorkgroupLane): VMLane = lanes(lane.raw) match {
    case null =>
      val taken = pool.take(lane, registerCount, predicateCount)
      lanes(lane.raw) = taken
      taken
    case existing => existing
  }

  def vacate(): Unit = {
    var index = 0
    while (index < lanes.length) {
      lanes(index) match {
        case null => ()
        case lane =>
          lane.clear()
          pool.give(WorkgroupLane.fromIndex(index), lane)
          lanes(index) = null
      }
      index += 1
    }
  }
}

package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

/** Lane storage of one frame, allocated per lane on first use. */
@SuppressWarnings(Array("scalafix:DisableSyntax.null"))
class VMLanes(laneCount: Int in 1 ~ 1024) {
  private val lanes = Array.ofDim[VMLane | Null](laneCount.raw)

  def apply(lane: WorkgroupLane): VMLane = lanes(lane.raw) match {
    case null =>
      val allocated = VMLane()
      lanes(lane.raw) = allocated
      allocated
    case existing => existing
  }

  def clear(): Unit = lanes.foreach {
    case null => ()
    case lane => lane.clear()
  }
}

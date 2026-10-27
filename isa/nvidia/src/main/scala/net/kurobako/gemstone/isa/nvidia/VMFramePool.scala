package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*
import scala.collection.mutable

private object VMFramePool {
  private val NoLists = Array.empty[mutable.ArrayBuffer[VMLane] | Null]
}

/** Frame storage of one workgroup, reused by later frames and, after a reset, by later workgroups. Cleared lanes are
  * kept by lane index, so a frame reuses a lane whichever frame last held it.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
private class VMFramePool(laneCount: Int in 1 ~ 1024) extends VMLaneSource {
  private val idle        = java.util.Arrays.copyOf(VMFramePool.NoLists, laneCount)
  private val spare       = mutable.ArrayBuffer.empty[VMLanes]
  private val all         = mutable.ArrayBuffer.empty[VMLanes]
  private var outstanding = 0
  private var peak        = 0

  def acquire(routine: VMIR.Routine, observed: Boolean = false): VMLanes = {
    val lanes = if (spare.nonEmpty) spare.remove(spare.size - 1)
    else {
      val created = VMLanes(laneCount, this)
      all += created
      created
    }
    outstanding += 1
    peak = math.max(peak, outstanding)
    lanes.prepare(routine, observed)
    lanes
  }

  /** Only for a frame that no cohort, waiter or later instruction can reach again. */
  def release(lanes: VMLanes): Unit = {
    lanes.vacate()
    spare += lanes
    outstanding -= 1
  }

  /** Reclaims every frame, including those whose lanes exited inside a call and so never returned, and keeps only as
    * much storage as the last workgroup used at once. No frame may be live.
    */
  def reset(): Unit = {
    all.foreach(_.vacate())
    if (all.size > peak) all.dropRightInPlace(all.size - peak)
    spare.clear()
    spare ++= all
    idle.foreach {
      case null  => ()
      case lanes => if (lanes.size > peak) lanes.dropRightInPlace(lanes.size - peak)
    }
    outstanding = 0
    peak = 0
  }

  def take(lane: WorkgroupLane, registerCount: Int >= 0, predicateCount: Int >= 0): VMLane =
    reusePlain(lane, registerCount, predicateCount) match {
      case null   => VMLane(registerCount, predicateCount)
      case reused => reused
    }

  def takeObserved(lane: WorkgroupLane, registerCount: Int >= 0, predicateCount: Int >= 0, owner: VMLanes): VMLane = {
    val taken = reuseObserved(lane, registerCount, predicateCount) match {
      case existing: VMObservedLane => existing
      case _                        => VMObservedLane(registerCount, predicateCount)
    }
    taken.ownedBy(owner)
    taken
  }

  // an idle lane of the other kind is dropped, so a launch never meets a lane that reports when it should not
  private def reusePlain(
      lane: WorkgroupLane,
      registerCount: Int >= 0,
      predicateCount: Int >= 0
  ): VMLane | Null =
    idle(lane.raw) match {
      case lanes: mutable.ArrayBuffer[VMLane] if lanes.nonEmpty =>
        val reused = lanes.remove(lanes.size - 1)
        reused match {
          case _: VMObservedLane => null
          case _                 =>
            reused.reserve(registerCount, predicateCount)
            reused
        }
      case _ => null
    }

  private def reuseObserved(
      lane: WorkgroupLane,
      registerCount: Int >= 0,
      predicateCount: Int >= 0
  ): VMLane | Null =
    idle(lane.raw) match {
      case lanes: mutable.ArrayBuffer[VMLane] if lanes.nonEmpty =>
        val reused = lanes.remove(lanes.size - 1)
        reused match {
          case _: VMObservedLane =>
            reused.reserve(registerCount, predicateCount)
            reused
          case _ => null
        }
      case _ => null
    }

  def give(lane: WorkgroupLane, cleared: VMLane): Unit = idle(lane.raw) match {
    case lanes: mutable.ArrayBuffer[VMLane] => lanes += cleared
    case null                               => idle(lane.raw) = mutable.ArrayBuffer(cleared)
  }
}

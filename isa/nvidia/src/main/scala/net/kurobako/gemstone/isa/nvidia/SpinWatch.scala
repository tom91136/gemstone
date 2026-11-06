package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.vm.*

private object SpinWatch {
  val Off: SpinWatch = SpinWatch(0, Pace.Free)

  private val Repeats = 8
  private val Mix     = 0x9e3779b97f4a7c15L
}

/** Recognises a warp that polls memory: between two yields it re-reads the same addresses and sees the same values,
  * over and over. Such a warp waits on others, so the scheduler lets them run ahead of it, and since the memory may be
  * another work-group's, the launch's work-groups are asked to stay close together by [[Pace.engage]]ing pacing.
  */
private class SpinWatch(warps: Int, pace: Pace) {
  import SpinWatch.*

  private val digest   = Array.ofDim[Long](warps)
  private val previous = Array.ofDim[Long](warps)
  private val repeated = Array.ofDim[Int](warps)

  def recording: Boolean = warps > 0

  def record(lane: WorkgroupLane, address: Long, value: Long): Unit = {
    val warp = lane.raw >> 5
    digest(warp) = (digest(warp) + address) * Mix + value + 1L
  }

  /** Whether the warp has polled unchanged memory for long enough to be waiting on others. */
  def yielded(lane: WorkgroupLane): Boolean = {
    val warp  = lane.raw >> 5
    val reads = digest(warp)
    digest(warp) = 0L
    if (reads != 0L) {
      if (reads == previous(warp)) {
        if (repeated(warp) < Repeats) repeated(warp) += 1
      } else {
        previous(warp) = reads
        if (repeated(warp) > 0) repeated(warp) -= 1
      }
    }
    val spinning = repeated(warp) >= Repeats
    if (spinning) pace.engage()
    spinning
  }
}

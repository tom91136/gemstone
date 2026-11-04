package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.vm.*

private object SpinWatch {
  val Off: SpinWatch = SpinWatch(0, Pace.Free)

  private val Repeats = 8
  private val Mix     = 0x9e3779b97f4a7c15L
}

/** Recognises a warp that polls memory another work-group writes: between two yields it re-reads the same addresses and
  * sees the same values, over and over. That is the only kind of yield that asks the launch's work-groups to stay close
  * together, so it is what [[Pace.engage]]s pacing.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private class SpinWatch(warps: Int, pace: Pace) {
  import SpinWatch.*

  private val digest   = Array.ofDim[Long](warps)
  private val previous = Array.ofDim[Long](warps)
  private val repeated = Array.ofDim[Int](warps)
  private var tracking = warps > 0 && pace.pending

  def recording: Boolean = tracking

  def record(lane: WorkgroupLane, address: Long, value: Long): Unit = {
    val warp = lane.raw >> 5
    digest(warp) = (digest(warp) + address) * Mix + value + 1L
  }

  def yielded(lane: WorkgroupLane): Unit = {
    val warp  = lane.raw >> 5
    val reads = digest(warp)
    digest(warp) = 0L
    if (reads != 0L) {
      if (reads == previous(warp)) {
        repeated(warp) += 1
        if (repeated(warp) >= Repeats) { pace.engage(); tracking = false }
      } else {
        previous(warp) = reads
        if (repeated(warp) > 0) repeated(warp) -= 1
      }
    }
    if (tracking && !pace.pending) tracking = false
  }
}

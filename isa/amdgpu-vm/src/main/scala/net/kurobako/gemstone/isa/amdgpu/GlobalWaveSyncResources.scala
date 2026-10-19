package net.kurobako.gemstone.isa.amdgpu

import VMIR.*

private object GlobalWaveSyncResources {
  inline val Count  = 64
  inline val Idle   = -1
  inline val Queued = 2
}

/** The 64 GWS resources of a cooperative dispatch, which one thread steps. Each counts waves for a barrier or releases
  * for a semaphore; a barrier releases its waiting waves by toggling its flag. A wave's wait is [[Idle]], the flag it
  * waits on a barrier to change from, or [[Queued]] on a semaphore.
  */
private class GlobalWaveSyncResources derives CanEqual {
  import GlobalWaveSyncResources.*

  private val counters = Array.fill(Count)(0L)
  private val flags    = Array.fill(Count)(0)
  private val queued   = Array.fill(Count)(0)

  def initialise(resource: Int, value: Long): Unit = {
    counters(resource) = value
    flags(resource) = 0
  }

  /** `operation` on `resource` for a wave in wait `waiting`, with the wave's data `value`; the wave's next wait. */
  def step(operation: GlobalWaveSyncOperation, resource: Int, value: Long, waiting: Int): Int = operation match {
    case GlobalWaveSyncOperation.Init =>
      initialise(resource, value)
      Idle
    case GlobalWaveSyncOperation.SemaphoreV =>
      counters(resource) += 1
      Idle
    case GlobalWaveSyncOperation.SemaphoreBulkRelease =>
      counters(resource) += value
      Idle
    case GlobalWaveSyncOperation.SemaphoreReleaseAll =>
      counters(resource) = queued(resource).toLong
      Idle
    case GlobalWaveSyncOperation.SemaphoreP =>
      if (counters(resource) > 0) {
        counters(resource) -= 1
        if (waiting == Queued) queued(resource) -= 1
        Idle
      } else {
        if (waiting != Queued) queued(resource) += 1
        Queued
      }
    case GlobalWaveSyncOperation.Barrier =>
      if (waiting != Idle) {
        if (flags(resource) == waiting) waiting
        else {
          queued(resource) -= 1
          Idle
        }
      } else if (counters(resource) <= 0) {
        // the last wave to arrive releases the others and itself, and sets the count for the next barrier
        flags(resource) ^= 1
        counters(resource) = value
        Idle
      } else {
        counters(resource) -= 1
        queued(resource) += 1
        flags(resource)
      }
  }
}

package net.kurobako.gemstone.isa.spirv.v1_6

import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*

/** Per-task execution measurements reported separately from program events. Group-local counters are written by one
  * worker thread, so only the cross-thread merge and snapshot take the lock.
  */
private class VMMetrics {
  private val lock = ReentrantLock()
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var dispatchCount: Long >= 0 = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var collectiveDispatchCount: Long >= 0 = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var invocationCount: Long >= 0 = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var instructionCount: Long >= 0 = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var invocationValueInstanceCount: Long >= 0 = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var denseValueSlotCount: Long >= 0 = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var estimatedDenseValueByteCount: Long >= 0 = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var dispatchElapsedNanos: Long >= 0 = 0L

  private def saturatedAdd(left: Long >= 0, right: Long >= 0): Long >= 0 =
    if (Long.MaxValue - left.raw < right.raw) Long.MaxValue else left + right

  def recordDispatch(collective: Boolean): Unit = {
    dispatchCount = saturatedAdd(dispatchCount, 1L)
    if (collective) collectiveDispatchCount = saturatedAdd(collectiveDispatchCount, 1L)
  }

  def recordInvocation(): Unit  = invocationCount = saturatedAdd(invocationCount, 1L)
  def recordInstruction(): Unit = instructionCount = saturatedAdd(instructionCount, 1L)

  def recordInvocationValues(
      denseSlots: Int >= 0,
      bytesPerSlot: Int >= 0
  ): Unit = {
    invocationValueInstanceCount = saturatedAdd(invocationValueInstanceCount, 1L)
    denseValueSlotCount = saturatedAdd(denseValueSlotCount, denseSlots.toLong.widen[Long >= 0])
    val estimatedBytes = denseSlots.toLong.widen[Long >= 0] * bytesPerSlot.toLong.widen[Long >= 0]
    estimatedDenseValueByteCount = saturatedAdd(estimatedDenseValueByteCount, estimatedBytes)
  }

  def recordElapsed(nanos: Long >= 0): Unit =
    dispatchElapsedNanos = saturatedAdd(dispatchElapsedNanos, nanos)

  def merge(other: VMMetrics): Unit = {
    val snapshot = other.snapshot
    lock.withLock {
      dispatchCount = saturatedAdd(dispatchCount, snapshot.dispatches)
      collectiveDispatchCount = saturatedAdd(collectiveDispatchCount, snapshot.collectiveDispatches)
      invocationCount = saturatedAdd(invocationCount, snapshot.invocations)
      instructionCount = saturatedAdd(instructionCount, snapshot.instructions)
      invocationValueInstanceCount =
        saturatedAdd(invocationValueInstanceCount, snapshot.invocationValueInstances)
      denseValueSlotCount = saturatedAdd(denseValueSlotCount, snapshot.denseValueSlots)
      estimatedDenseValueByteCount =
        saturatedAdd(estimatedDenseValueByteCount, snapshot.estimatedDenseValueBytes)
      dispatchElapsedNanos = saturatedAdd(dispatchElapsedNanos, snapshot.elapsedNanos)
    }
  }

  def snapshot: ExecutionMetrics = lock.withLock {
    ExecutionMetrics(
      dispatchCount,
      collectiveDispatchCount,
      invocationCount,
      instructionCount,
      invocationValueInstanceCount,
      denseValueSlotCount,
      estimatedDenseValueByteCount,
      dispatchElapsedNanos
    )
  }
}

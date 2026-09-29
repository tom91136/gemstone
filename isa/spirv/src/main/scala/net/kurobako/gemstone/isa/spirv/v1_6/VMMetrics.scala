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
  private var dispatchCount = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var collectiveDispatchCount = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var invocationCount = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var instructionCount = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var invocationValueInstanceCount = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var denseValueSlotCount = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var estimatedDenseValueByteCount = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var dispatchElapsedNanos = 0L

  private def saturatedAdd(left: Long, right: Long): Long =
    if (Long.MaxValue - left < right) Long.MaxValue else left + right

  def recordDispatch(collective: Boolean): Unit = {
    dispatchCount = saturatedAdd(dispatchCount, 1L)
    if (collective) collectiveDispatchCount = saturatedAdd(collectiveDispatchCount, 1L)
  }

  def recordInvocation(): Unit  = invocationCount = saturatedAdd(invocationCount, 1L)
  def recordInstruction(): Unit = instructionCount = saturatedAdd(instructionCount, 1L)

  def recordInvocationValues(
      denseSlots: Int,
      bytesPerSlot: Int
  ): Unit = {
    require(denseSlots >= 0 && bytesPerSlot >= 0, "metric dimensions must be non-negative")
    invocationValueInstanceCount = saturatedAdd(invocationValueInstanceCount, 1L)
    denseValueSlotCount = saturatedAdd(denseValueSlotCount, denseSlots.toLong)
    val estimatedBytes = Math.multiplyExact(denseSlots.toLong, bytesPerSlot.toLong)
    estimatedDenseValueByteCount = saturatedAdd(estimatedDenseValueByteCount, estimatedBytes)
  }

  def recordElapsed(nanos: Long): Unit = {
    require(nanos >= 0L, "elapsed time must be non-negative")
    dispatchElapsedNanos = saturatedAdd(dispatchElapsedNanos, nanos)
  }

  def merge(other: VMMetrics): Unit = {
    val snapshot = other.snapshot
    lock.withLock {
      dispatchCount = saturatedAdd(dispatchCount, snapshot.dispatches.raw)
      collectiveDispatchCount = saturatedAdd(collectiveDispatchCount, snapshot.collectiveDispatches.raw)
      invocationCount = saturatedAdd(invocationCount, snapshot.invocations.raw)
      instructionCount = saturatedAdd(instructionCount, snapshot.instructions.raw)
      invocationValueInstanceCount =
        saturatedAdd(invocationValueInstanceCount, snapshot.invocationValueInstances.raw)
      denseValueSlotCount = saturatedAdd(denseValueSlotCount, snapshot.denseValueSlots.raw)
      estimatedDenseValueByteCount =
        saturatedAdd(estimatedDenseValueByteCount, snapshot.estimatedDenseValueBytes.raw)
      dispatchElapsedNanos = saturatedAdd(dispatchElapsedNanos, snapshot.elapsedNanos.raw)
    }
  }

  def snapshot: ExecutionMetrics = lock.withLock {
    ExecutionMetrics(
      dispatchCount.unsafe[Long >= 0],
      collectiveDispatchCount.unsafe[Long >= 0],
      invocationCount.unsafe[Long >= 0],
      instructionCount.unsafe[Long >= 0],
      invocationValueInstanceCount.unsafe[Long >= 0],
      denseValueSlotCount.unsafe[Long >= 0],
      estimatedDenseValueByteCount.unsafe[Long >= 0],
      dispatchElapsedNanos.unsafe[Long >= 0]
    )
  }
}

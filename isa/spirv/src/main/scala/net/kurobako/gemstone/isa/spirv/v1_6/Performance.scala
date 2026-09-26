package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.NonNegativeLong
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.vm.Event

import java.util.concurrent.locks.ReentrantLock

object Performance {
  final case class Snapshot(
      dispatches: NonNegativeLong,
      collectiveDispatches: NonNegativeLong,
      invocations: NonNegativeLong,
      instructions: NonNegativeLong,
      invocationValueInstances: NonNegativeLong,
      denseValueSlots: NonNegativeLong,
      estimatedDenseValueBytes: NonNegativeLong,
      elapsedNanos: NonNegativeLong
  )

  final case class Observation(snapshot: Snapshot) extends Event

  /** Per-task execution measurements emitted through the VM observer. Group-local counters are written by one worker
    * thread, so only the cross-thread merge and snapshot take the lock.
    */
  private[v1_6] final class Metrics {
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

    private[v1_6] def recordDispatch(collective: Boolean): Unit = {
      dispatchCount = saturatedAdd(dispatchCount, 1L)
      if (collective) collectiveDispatchCount = saturatedAdd(collectiveDispatchCount, 1L)
    }

    private[v1_6] def recordInvocation(): Unit  = invocationCount = saturatedAdd(invocationCount, 1L)
    private[v1_6] def recordInstruction(): Unit = instructionCount = saturatedAdd(instructionCount, 1L)

    private[v1_6] def recordInvocationValues(
        denseSlots: Int,
        bytesPerSlot: Int
    ): Unit = {
      require(denseSlots >= 0 && bytesPerSlot >= 0, "metric dimensions must be non-negative")
      invocationValueInstanceCount = saturatedAdd(invocationValueInstanceCount, 1L)
      denseValueSlotCount = saturatedAdd(denseValueSlotCount, denseSlots.toLong)
      val estimatedBytes = Math.multiplyExact(denseSlots.toLong, bytesPerSlot.toLong)
      estimatedDenseValueByteCount = saturatedAdd(estimatedDenseValueByteCount, estimatedBytes)
    }

    private[v1_6] def recordElapsed(nanos: Long): Unit = {
      require(nanos >= 0L, "elapsed time must be non-negative")
      dispatchElapsedNanos = saturatedAdd(dispatchElapsedNanos, nanos)
    }

    private[v1_6] def merge(other: Metrics): Unit = {
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

    def snapshot: Snapshot = lock.withLock {
      Snapshot(
        NonNegativeLong.unsafe(dispatchCount),
        NonNegativeLong.unsafe(collectiveDispatchCount),
        NonNegativeLong.unsafe(invocationCount),
        NonNegativeLong.unsafe(instructionCount),
        NonNegativeLong.unsafe(invocationValueInstanceCount),
        NonNegativeLong.unsafe(denseValueSlotCount),
        NonNegativeLong.unsafe(estimatedDenseValueByteCount),
        NonNegativeLong.unsafe(dispatchElapsedNanos)
      )
    }
  }
}

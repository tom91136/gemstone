package net.kurobako.gemstone.rpc

import java.util.concurrent.atomic.AtomicIntegerArray
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

private object StoredAllocation {
  // One sequence per cache line, so readers of one stripe never share a line with another stripe's writers.
  private val VersionStride = 16

  /** Aliases share storage and its locks. Disjoint cache lines progress independently; overlapping scalar, bulk and
    * atomic accesses acquire the same stripes in ascending order.
    *
    * Each stripe carries a sequence, odd while a writer holds it. A read within one stripe runs unlocked and stands
    * only if the sequence stayed even and unchanged, so it never observes a partial write; otherwise it repeats locked.
    */
  class Storage(val size: Int > 0) {
    val bytes: Array[Byte] = Array.ofDim[Byte](size.raw)
    private val locks      = Array.fill(math.min(64, size.ceilDiv(64).raw))(ReentrantLock())
    private val versions   = AtomicIntegerArray(locks.length * VersionStride)
    // Written only with every stripe held; unlocked readers observe it through the stripe sequence.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var live = true

    def release(): Unit = write(0, locks.length * 64)(_ => live = false)

    /** Run `access` with whether the storage is live, seeing no concurrent write to the range. */
    inline def read[A](offset: Int, size: Int)(inline access: Boolean => A): A = {
      val version = optimistic(offset, size)
      if (version < 0) write(offset, size)(access)
      else {
        val result = access(live)
        if (validate(offset, version)) result else write(offset, size)(access)
      }
    }

    /** Run `access` with whether the storage is live, holding every stripe the range covers. */
    inline def write[A](offset: Int, size: Int)(inline access: Boolean => A): A = {
      stripes(offset, size, true)
      try access(live)
      finally stripes(offset, size, false)
    }

    private def optimistic(offset: Int, size: Int): Int = {
      val first = offset / 64
      if (size > 0 && (offset + size - 1) / 64 != first) -1
      else {
        val version = versions.getAcquire((first % locks.length) * VersionStride)
        if ((version & 1) == 0) version else -1
      }
    }

    private def validate(offset: Int, version: Int): Boolean = {
      PlatformFence.acquire()
      versions.get((offset / 64 % locks.length) * VersionStride) == version
    }

    // The covered stripes form one cyclic interval; visit it in ascending lock order, and unlock in reverse.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private def stripes(offset: Int, size: Int, acquire: Boolean): Unit = {
      val count = locks.length
      val first = offset / 64
      val last  = if (size == 0) first else (offset + size - 1) / 64
      if (first == last) visit(first % count, acquire)
      else {
        val all   = last - first + 1 >= count
        val low   = if (all) 0 else first % count
        val high  = if (all) count - 1 else last % count
        val wraps = !all && low > high
        if (acquire) {
          if (wraps) {
            var index = 0
            while (index <= high) { visit(index, acquire); index += 1 }
            index = low
            while (index < count) { visit(index, acquire); index += 1 }
          } else {
            var index = low
            while (index <= high) { visit(index, acquire); index += 1 }
          }
        } else if (wraps) {
          var index = count - 1
          while (index >= low) { visit(index, acquire); index -= 1 }
          index = high
          while (index >= 0) { visit(index, acquire); index -= 1 }
        } else {
          var index = high
          while (index >= low) { visit(index, acquire); index -= 1 }
        }
      }
    }

    private def visit(index: Int, acquire: Boolean): Unit =
      if (acquire) {
        locks(index).lock()
        val _ = versions.getAndIncrement(index * VersionStride)
      } else {
        versions.setRelease(index * VersionStride, versions.get(index * VersionStride) + 1)
        locks(index).unlock()
      }
  }
}

/** Session-owned bytes visible at `base`: a client alias once registered, otherwise a synthetic address. */
private case class StoredAllocation(
    storage: StoredAllocation.Storage,
    base: GuestAddress,
    aliased: Boolean = false,
    hostBacked: Boolean = false
) {
  def bytes: Array[Byte] = storage.bytes
  def size: Int > 0      = storage.size
}

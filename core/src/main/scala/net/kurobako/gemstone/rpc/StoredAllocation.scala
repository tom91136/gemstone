package net.kurobako.gemstone.rpc

import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

private object StoredAllocation {

  /** Aliases share storage and its locks. Disjoint cache lines progress independently; overlapping scalar, bulk and
    * atomic accesses acquire the same stripes in ascending order.
    */
  class Storage(val size: Int > 0) {
    val bytes: Array[Byte] = Array.ofDim[Byte](size.raw)
    private val locks      = Array.fill(math.min(64, size.ceilDiv(64).raw))(ReentrantLock())
    def lockAll(): Unit    = locks.foreach(_.lock())
    def unlockAll(): Unit  = locks.reverseIterator.foreach(_.unlock())
    def access[A](offset: Int >= 0, size: Int >= 0)(operation: => A): A = {
      val first = offset.raw / 64
      val last  = if (size.raw == 0) first else (offset.raw + size.raw - 1) / 64
      if (first == last) locks(first % locks.length).withLock(operation)
      else {
        val selected =
          if (last - first + 1 >= locks.length) locks
          else (first to last).map(_ % locks.length).sorted.map(locks(_)).toArray
        selected.foreach(_.lock())
        try operation
        finally selected.reverseIterator.foreach(_.unlock())
      }
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

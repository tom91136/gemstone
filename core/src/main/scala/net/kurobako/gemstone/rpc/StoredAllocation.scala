package net.kurobako.gemstone.rpc

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReferenceArray
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

// tests observe resident bytes to check allocation lifetimes
@SuppressWarnings(Array("bonsai:test-only-member"))
private object StoredAllocation {

  /** Every device ordinal may access the allocation. */
  val AllDevices: Int = -1
  // One sequence per cache line, so readers of one stripe never share a line with another stripe's writers.
  private val VersionStride = 16

  private inline val SegmentShift = 30

  /** The largest span of one backing array; offsets split into a segment index and a position within it. */
  val SegmentBytes: Int = 1 << SegmentShift

  private val SegmentMask: Long = SegmentBytes - 1L

  /** The largest allocation; its segment table stays small. */
  val MaximumBytes: Long = (1L << 20) * SegmentBytes
  private val Untouched  = Array.emptyByteArray

  /** Aliases share storage and its locks. Disjoint cache lines progress independently; overlapping scalar, bulk and
    * atomic accesses acquire the same stripes in ascending order.
    *
    * Each stripe carries a sequence, odd while a writer holds it. A read within one stripe runs unlocked and stands
    * only if the sequence stayed even and unchanged, so it never observes a partial write; otherwise it repeats locked.
    *
    * Bytes live in segments of at most `SegmentBytes`, each created on its first write. A segment never changes once
    * published, and an untouched one reads as zeros, so memory use follows what the program writes.
    */
  class Storage(val size: Long > 0) {
    private val segments = AtomicReferenceArray[Array[Byte]](((size - 1L) >>> SegmentShift).toInt + 1)
    for (index <- 0 until segments.length) segments.set(index, Untouched)
    private val creation = ReentrantLock()
    private val locks    = Array.fill(math.min(64L, (size - 1L) / 64L + 1L).toInt)(ReentrantLock())
    private val versions = PlatformConcurrency.atomicInts(locks.length * VersionStride)
    // Written only with every stripe held; unlocked readers observe it through the stripe sequence.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var live = true

    private val holders = AtomicInteger(1)

    def retain(): Unit = { val _ = holders.incrementAndGet() }

    /** Drops one holder; the bytes die with the last. */
    def release(): Unit = if (holders.decrementAndGet() == 0) write(0L, locks.length * 64L)(_ => live = false)

    /** Bytes currently backed by segments. */
    def residentBytes: Long = (0 until segments.length).foldLeft(0L)((total, index) =>
      total + segments.get(index).length
    )

    /** Run `access` with whether the storage is live, seeing no concurrent write to the range. */
    inline def read[A](offset: Long, size: Long)(inline access: Boolean => A): A = {
      val version = optimistic(offset, size)
      if (version < 0) write(offset, size)(access)
      else {
        val result = access(live)
        if (validate(offset, version)) result else write(offset, size)(access)
      }
    }

    /** Run `access` with whether the storage is live, holding every stripe the range covers. */
    inline def write[A](offset: Long, size: Long)(inline access: Boolean => A): A = {
      stripes(offset, size, acquire = true)
      try access(live)
      finally stripes(offset, size, acquire = false)
    }

    /** Copy `length` bytes from `offset` into `target`; call within `read` or `write` over the range. */
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def copyTo(offset: Long, target: Array[Byte], at: Int, length: Int): Unit = {
      var done = 0
      while (done < length) {
        val position = offset + done
        val inner    = (position & SegmentMask).toInt
        val chunk    = math.min(length - done, SegmentBytes - inner)
        val segment  = segments.get((position >>> SegmentShift).toInt)
        if (segment.isEmpty) java.util.Arrays.fill(target, at + done, at + done + chunk, 0.toByte)
        else System.arraycopy(segment, inner, target, at + done, chunk)
        done += chunk
      }
    }

    /** Copy `length` bytes of `source` to `offset`; call within `write` over the range. */
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def copyFrom(offset: Long, source: Array[Byte], at: Int, length: Int): Unit = {
      var done = 0
      while (done < length) {
        val position = offset + done
        val inner    = (position & SegmentMask).toInt
        val chunk    = math.min(length - done, SegmentBytes - inner)
        System.arraycopy(source, at + done, segment((position >>> SegmentShift).toInt), inner, chunk)
        done += chunk
      }
    }

    /** Copy every byte of `source` to `offset`; call within `write` over the range. */
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def copyFrom(offset: Long, source: Vec[Byte]): Unit = {
      var done = 0
      while (done < source.length) {
        val position = offset + done
        val inner    = (position & SegmentMask).toInt
        val chunk    = math.min(source.length - done, SegmentBytes - inner)
        val part     = if (done == 0 && chunk == source.length) source else source.slice(done, done + chunk)
        val _        = part.copyToArray(segment((position >>> SegmentShift).toInt), inner, chunk)
        done += chunk
      }
    }

    /** The little endian value of `width` bytes at `offset`; call within `read` or `write` over the range. */
    def decode(offset: Long, width: Int in 0 ~ 8): Long = {
      val inner = (offset & SegmentMask).toInt
      if (inner + width <= SegmentBytes) {
        val segment = segments.get((offset >>> SegmentShift).toInt)
        if (segment.isEmpty) 0L else LittleEndian.decode(segment, inner, width)
      } else {
        val bytes = Array.ofDim[Byte](width)
        copyTo(offset, bytes, 0, width)
        LittleEndian.decode(bytes, 0, width)
      }
    }

    /** Store the low `width` bytes of `value` at `offset`; call within `write` over the range. */
    def encode(offset: Long, value: Long, width: Int in 0 ~ 8): Unit = {
      val inner = (offset & SegmentMask).toInt
      if (inner + width <= SegmentBytes)
        LittleEndian.write(value, segment((offset >>> SegmentShift).toInt), inner, width)
      else copyFrom(offset, LittleEndian.toArray(value, width), 0, width)
    }

    private def segment(index: Int): Array[Byte] = {
      val existing = segments.get(index)
      if (existing.nonEmpty) existing
      else {
        creation.lock()
        try {
          val raced = segments.get(index)
          if (raced.nonEmpty) raced
          else {
            val created = Array.ofDim[Byte](math.min(SegmentBytes.toLong, size - (index.toLong << SegmentShift)).toInt)
            segments.set(index, created)
            created
          }
        } finally creation.unlock()
      }
    }

    private def optimistic(offset: Long, size: Long): Int = {
      val first = offset / 64
      if (size > 0 && (offset + size - 1) / 64 != first) -1
      else {
        val version = versions.getAcquire((first % locks.length).toInt * VersionStride)
        if ((version & 1) == 0) version else -1
      }
    }

    private def validate(offset: Long, version: Int): Boolean = {
      PlatformFence.acquire()
      versions.get((offset / 64 % locks.length).toInt * VersionStride) == version
    }

    // The covered stripes form one cyclic interval; visit it in ascending lock order, and unlock in reverse.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private def stripes(offset: Long, size: Long, acquire: Boolean): Unit = {
      val count = locks.length
      val first = offset / 64
      val last  = if (size == 0) first else (offset + size - 1) / 64
      if (first == last) visit((first % count).toInt, acquire)
      else {
        val all   = last - first + 1 >= count
        val low   = if (all) 0 else (first % count).toInt
        val high  = if (all) count - 1 else (last % count).toInt
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
    hostBacked: Boolean = false,
    devices: Int = StoredAllocation.AllDevices,
    imported: Boolean = false
) {
  def size: Long > 0 = storage.size
}

package net.kurobako.gemstone.memory

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.collection.mutable

/** Written bytes of one allocation, reported as transfer chunks. */
trait WrittenExtent {
  def add(offset: Long >= 0, size: Int >= 0): Unit
  def chunks(maxChunkSize: Int > 0): Vec[(Long >= 0, Int > 0)]

  /** An empty extent of the same kind and shape. */
  def fresh: WrittenExtent

  /** Heap bytes this extent holds. */
  def retainedBytes: Long
}

object WrittenRanges {
  // A TreeMap entry and its two boxed keys.
  private val IntervalBytes = 88L
}

/** Exact byte intervals. Adjacent writes merge; unwritten gaps never enter writeback. */
class WrittenRanges extends WrittenExtent {
  private val intervals = java.util.TreeMap[Long >= 0, Long >= 0]()
  // Intervals only grow by merging, so the last one touched keeps covering what it covered.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var lastStart: Long >= 0 = 0L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var lastEnd: Long >= 0 = 0L
  // The start of the interval after the last one; sequential writes extend the last interval up to it in place.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var lastNext: Long >= 0 = 0L

  override def fresh: WrittenExtent = WrittenRanges()

  override def retainedBytes: Long = intervals.size.toLong * WrittenRanges.IntervalBytes

  override def add(offset: Long >= 0, size: Int >= 0): Unit =
    if (size > 0) {
      val limit = java.lang.Math.addExact(offset, size.toLong).unsafe[Long >= 0]
      if (offset >= lastStart && limit <= lastEnd) ()
      else if (offset >= lastStart && offset <= lastEnd && limit < lastNext) {
        val _ = intervals.put(lastStart, limit)
        lastEnd = limit
      } else insert(offset, limit)
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def insert(offset: Long >= 0, limit: Long >= 0): Unit = {
    val previous = Option(intervals.floorEntry(offset))
    var start    = offset
    var end      = limit
    previous match {
      case Some(entry) if entry.getValue >= limit =>
        start = entry.getKey
        end = entry.getValue
      case _ =>
        previous.filter(_.getValue >= start).foreach { entry =>
          start = entry.getKey
          val _ = intervals.remove(entry.getKey)
        }
        var next = Option(intervals.ceilingEntry(start))
        while (next.exists(_.getKey <= end)) {
          next.foreach { entry =>
            end = end.max(entry.getValue)
            val _ = intervals.remove(entry.getKey)
          }
          next = Option(intervals.ceilingEntry(start))
        }
        val _ = intervals.put(start, end)
    }
    lastStart = start
    lastEnd = end
    lastNext = Option(intervals.higherKey(start)).getOrElse[Long >= 0](Long.MaxValue)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  override def chunks(maxChunkSize: Int > 0): Vec[(Long >= 0, Int > 0)] = {
    val result = mutable.ArrayBuffer.empty[(Long >= 0, Int > 0)]
    intervals.forEach { (begin, end) =>
      var offset: Long = begin
      while (offset < end) {
        val size = math.min(maxChunkSize.toLong, end.raw - offset).toInt.unsafe[Int > 0]
        result += ((offset.unsafe[Long >= 0], size))
        offset += size
      }
    }
    Vec.from(result)
  }
}

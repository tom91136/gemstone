package net.kurobako.gemstone.memory

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.collection.mutable

/** Exact byte intervals. Adjacent writes merge; unwritten gaps never enter writeback. */
class WrittenRanges {
  private val intervals = java.util.TreeMap[Int >= 0, Int >= 0]()
  // Intervals only grow by merging, so the last one touched keeps covering what it covered.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var lastStart: Int >= 0 = 0
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var lastEnd: Int >= 0 = 0
  // The start of the interval after the last one; sequential writes extend the last interval up to it in place.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var lastNext: Int >= 0 = 0

  def add(offset: Int >= 0, size: Int >= 0): Unit =
    if (size.raw > 0) {
      val limit = offset + size
      if (offset.raw >= lastStart.raw && limit.raw <= lastEnd.raw) ()
      else if (offset.raw >= lastStart.raw && offset.raw <= lastEnd.raw && limit.raw < lastNext.raw) {
        val _ = intervals.put(lastStart, limit)
        lastEnd = limit
      } else insert(offset, limit)
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def insert(offset: Int >= 0, limit: Int >= 0): Unit = {
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
    lastNext = Option(intervals.higherKey(start)).getOrElse[Int >= 0](2147483647)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def chunks(maxChunkSize: Int > 0): Vec[(Int >= 0, Int > 0)] = {
    val result = mutable.ArrayBuffer.empty[(Int >= 0, Int > 0)]
    intervals.forEach { (begin, end) =>
      var offset = begin
      while (offset < end) {
        val remaining = (end - offset).raw.unsafe[Int > 0]
        val size      = maxChunkSize.min(remaining)
        result += ((offset, size))
        offset += size
      }
    }
    Vec.from(result)
  }
}

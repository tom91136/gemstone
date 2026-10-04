package net.kurobako.gemstone.memory

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.collection.mutable

/** Exact byte intervals. Adjacent writes merge; unwritten gaps never enter writeback. */
class WrittenRanges {
  private val intervals = java.util.TreeMap[Int >= 0, Int >= 0]()

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def add(offset: Int >= 0, size: Int >= 0): Unit =
    if (size.raw > 0) {
      val limit    = offset + size
      val previous = Option(intervals.floorEntry(offset))
      if (!previous.exists(_.getValue >= limit)) {
        var start = offset
        var end   = limit
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

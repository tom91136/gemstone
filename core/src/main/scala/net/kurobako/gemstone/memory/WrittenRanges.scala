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
      var start = offset
      var end   = offset + size
      Option(intervals.floorEntry(start)).filter(_.getValue >= start).foreach { previous =>
        start = previous.getKey
        end = end.max(previous.getValue)
        val _ = intervals.remove(previous.getKey)
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

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def chunks(maxChunkSize: Int > 0): Vec[(Int >= 0, Int > 0)] = {
    val result = mutable.ArrayBuffer.empty[(Int >= 0, Int > 0)]
    intervals.forEach { (begin, end) =>
      var offset = begin
      while (offset < end) {
        val size = math.min(maxChunkSize.raw, (end - offset).raw).unsafe[Int > 0]
        result += ((offset, size))
        offset += size
      }
    }
    Vec.from(result)
  }
}

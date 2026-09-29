package net.kurobako.gemstone.memory

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.collection.mutable

/** Exact byte intervals. Adjacent writes merge; unwritten gaps never enter writeback. */
class WrittenRanges {
  private val intervals = java.util.TreeMap[Integer, Integer]()

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def add(offset: Int, size: Int): Unit = {
    require(offset >= 0 && size >= 0, "byte ranges must be nonnegative")
    if (size > 0) {
      var start = offset
      var end   = Math.addExact(offset, size)
      Option(intervals.floorEntry(start)).filter(_.getValue.intValue >= start).foreach { previous =>
        start = previous.getKey.intValue
        end = math.max(end, previous.getValue.intValue)
        val _ = intervals.remove(previous.getKey)
      }
      var next = Option(intervals.ceilingEntry(start))
      while (next.exists(_.getKey.intValue <= end)) {
        next.foreach { entry =>
          end = math.max(end, entry.getValue.intValue)
          val _ = intervals.remove(entry.getKey)
        }
        next = Option(intervals.ceilingEntry(start))
      }
      val _ = intervals.put(start, end)
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def chunks(maxChunkSize: Int > 0): Vec[(Int, Int)] = {
    val result = mutable.ArrayBuffer.empty[(Int, Int)]
    intervals.forEach { (begin, end) =>
      var offset = begin.intValue
      while (offset < end.intValue) {
        val size = math.min(maxChunkSize.raw, end.intValue - offset)
        result += ((offset, size))
        offset += size
      }
    }
    Vec.from(result)
  }
}

package net.kurobako.gemstone.memory

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.collection.mutable

/** Written bytes at a fixed granule, one bit per granule of an allocation `extent` bytes long. The size depends on the
  * extent alone, never on the write pattern. A granule holding any written byte reports whole, clipped to the extent.
  * Not thread safe.
  */
class WrittenGranules(extent: Long > 0, granuleBytes: Int > 0) extends WrittenExtent {
  private val granules = (extent.raw - 1L) / granuleBytes.raw + 1L
  private val bits     = new Array[Long](java.lang.Math.toIntExact((granules + 63L) >>> 6))

  override def fresh: WrittenExtent = WrittenGranules(extent, granuleBytes)

  override def retainedBytes: Long = bits.length.toLong * java.lang.Long.BYTES

  override def add(offset: Long >= 0, size: Int >= 0): Unit =
    if (size > 0) {
      val first = offset.raw / granuleBytes.raw
      val last  = (java.lang.Math.addExact(offset.raw, size.toLong) - 1L) / granuleBytes.raw
      val low   = (first >>> 6).toInt
      val high  = (last >>> 6).toInt
      val head  = -1L << (first & 63L).toInt
      val tail  = -1L >>> (63 - (last & 63L).toInt)
      if (low == high) bits(low) |= head & tail
      else {
        bits(low) |= head
        java.util.Arrays.fill(bits, low + 1, high, -1L)
        bits(high) |= tail
      }
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  override def chunks(maxChunkSize: Int > 0): Vec[(Long >= 0, Int > 0)] = {
    val result                               = mutable.ArrayBuffer.empty[(Long >= 0, Int > 0)]
    var granule                              = 0L
    def next(from: Long, set: Boolean): Long = {
      val skippable = if (set) 0L else -1L
      var index     = from
      while (index < granules && (((bits((index >>> 6).toInt) >>> (index & 63L).toInt) & 1L) != 0L) != set)
        index += (if (bits((index >>> 6).toInt) == skippable && (index & 63L) == 0L) 64L else 1L)
      math.min(index, granules)
    }
    while (granule < granules) {
      val begin = next(granule, set = true)
      val end   = next(begin, set = false)
      var start = begin * granuleBytes.raw
      val limit = math.min(end * granuleBytes.raw, extent.raw)
      while (start < limit) {
        val size = math.min(maxChunkSize.raw.toLong, limit - start).toInt
        result += ((start.unsafe[Long >= 0], size.unsafe[Int > 0]))
        start += size
      }
      granule = end
    }
    Vec.from(result)
  }
}

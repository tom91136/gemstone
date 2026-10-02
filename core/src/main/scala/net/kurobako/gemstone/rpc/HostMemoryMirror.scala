package net.kurobako.gemstone.rpc

import java.util.BitSet
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import scala.collection.mutable
import scala.collection.mutable.LongMap

/** Sparse host bytes shared by aliases within one dispatch. The execution session owns locking. Missing bytes are
  * requested exactly; a page is never assumed readable because one byte is readable.
  */
private class HostMemoryMirror(maxBatchBytes: Int = 1024 * 1024, maxBatchRanges: Int = 1024) {
  require(maxBatchBytes >= 8, "a batch must fit the largest atomic word")
  require(maxBatchRanges > 0, "a batch must allow at least one range")

  private val pageSize = 4096

  // Pages are storage only: validity remains byte-exact, including allocation
  // boundaries and holes. No host access is widened to the containing page.
  private class Page {
    val bytes: Array[Byte] = Array.ofDim[Byte](pageSize)
    val valid: BitSet      = java.util.BitSet(pageSize)
    val words1: BitSet     = java.util.BitSet(pageSize)
    val words2: BitSet     = java.util.BitSet(pageSize / 2)
    val words4: BitSet     = java.util.BitSet(pageSize / 4)
    val words8: BitSet     = java.util.BitSet(pageSize / 8)

    def atomicWidth(offset: Int): Int =
      if (words8.get(offset / 8)) 8
      else if (words4.get(offset / 4)) 4
      else if (words2.get(offset / 2)) 2
      else if (words1.get(offset)) 1
      else 0
  }

  private class Addresses {
    val pages: LongMap[BitSet]                  = mutable.LongMap.empty[java.util.BitSet]
    def nonEmpty: Boolean                       = pages.nonEmpty
    def contains(address: Long): Boolean        = pages.get(address >>> 12).exists(_.get((address & 4095L).toInt))
    def add(address: Long, size: Int = 1): Unit = spans(address, size) { (number, offset, length, _) =>
      pages.getOrElseUpdate(number, java.util.BitSet(pageSize)).set(offset, offset + length)
    }
    def remove(address: Long, size: Int = 1): Unit = spans(address, size) { (number, offset, length, _) =>
      pages.get(number).foreach { bits =>
        bits.clear(offset, offset + length)
        if (bits.isEmpty) { val _ = pages.remove(number) }
      }
    }
    @SuppressWarnings(Array("scalafix:GemstoneNoIterator.no-iterator"))
    def iterator: Iterator[Long] = pages.view.toArray.sortBy(_._1).iterator.flatMap { (number, bits) =>
      Iterator.unfold(bits.nextSetBit(0)) { offset =>
        if (offset < 0) None else Some(((number << 12) + offset.toLong, bits.nextSetBit(offset + 1)))
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def spans(address: Long, size: Int)(f: (Long, Int, Int, Int) => Unit): Unit = {
    var consumed = 0
    while (consumed < size) {
      val current = address + consumed.toLong
      val offset  = (current & 4095L).toInt
      val length  = math.min(pageSize - offset, size - consumed)
      f(current >>> 12, offset, length, consumed)
      consumed += length
    }
  }

  private val pages                           = mutable.LongMap.empty[Page]
  private val missing                         = Addresses()
  private val issued                          = Addresses()
  private val changed                         = Addresses()
  private def atomicWidth(address: Long): Int =
    pages.get(address >>> 12).fold(0)(_.atomicWidth((address & 4095L).toInt))
  private def contains(address: Long): Boolean =
    pages.get(address >>> 12).exists(_.valid.get((address & 4095L).toInt))
  private def byte(address: Long): Byte = pages(address >>> 12).bytes((address & 4095L).toInt)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def selectedDirty(selected: Vec[HostMemoryRange]): Addresses = {
    val result = Addresses()
    changed.pages.view.foreach { (number, dirty) =>
      pages.get(number).foreach { page =>
        val base = number << 12
        val mask = java.util.BitSet(pageSize)
        selected.foreach { range =>
          val start = math.max(base, range.address.raw)
          val end   = math.min(base + pageSize, range.address.raw + range.size.raw.toLong)
          if (start < end) mask.set((start - base).toInt, (end - base).toInt)
        }
        def expand(words: BitSet, width: Int): Unit = {
          var index = words.nextSetBit(0)
          while (index >= 0) {
            val start = index * width
            val next  = mask.nextSetBit(start)
            if (next >= start && next < start + width) mask.set(start, start + width)
            index = words.nextSetBit(index + 1)
          }
        }
        expand(page.words8, 8)
        expand(page.words4, 4)
        expand(page.words2, 2)
        expand(page.words1, 1)
        mask.and(dirty)
        var offset = mask.nextSetBit(0)
        while (offset >= 0) {
          val end = math.min(mask.nextClearBit(offset), pageSize)
          result.add(base + offset.toLong, end - offset)
          offset = mask.nextSetBit(end)
        }
      }
    }
    result
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def read(address: Long, size: Int): Option[Vec[Byte]] = {
    var absent = false
    for (offset <- 0 until size) {
      val target = address + offset.toLong
      if (!contains(target)) {
        missing.add(target)
        absent = true
      }
    }
    if (absent) None else Some(Vec.from((0 until size).map(index => byte(address + index.toLong))))
  }

  def write(address: Long, values: Vec[Byte], atomicWidth: Int = 0): Unit = {
    // Preserve the width of naturally aligned stores when publishing a batch.
    // Mixed-width overlapping accesses do not have a cross-agent atomicity guarantee.
    spans(address, values.size) { (number, offset, length, consumed) =>
      val page = pages.getOrElseUpdate(number, Page())
      page.words1.clear(offset, offset + length)
      page.words2.clear(offset / 2, (offset + length + 1) / 2)
      page.words4.clear(offset / 4, (offset + length + 3) / 4)
      page.words8.clear(offset / 8, (offset + length + 7) / 8)
      for (index <- 0 until length) page.bytes(offset + index) = values(consumed + index)
      page.valid.set(offset, offset + length)
    }
    if (Set(1, 2, 4, 8)(atomicWidth) && values.nonEmpty && address % atomicWidth == 0) {
      val page   = pages(address >>> 12)
      val offset = (address & 4095L).toInt
      if (atomicWidth == 8) page.words8.set(offset / 8)
      else if (atomicWidth == 4) page.words4.set(offset / 4)
      else if (atomicWidth == 2) page.words2.set(offset / 2)
      else page.words1.set(offset)
    }
    changed.add(address, values.size)
    missing.remove(address, values.size)
  }

  private def storeIfAbsent(address: Long, value: Byte): Unit = {
    val page   = pages.getOrElseUpdate(address >>> 12, Page())
    val offset = (address & 4095L).toInt
    if (!page.valid.get(offset)) {
      page.bytes(offset) = value
      page.valid.set(offset)
    }
  }

  /** Record a value returned by a native atomic operation without making it dirty for publication. */
  def observeAtomic(address: Long, values: Vec[Byte]): Unit =
    values.zipWithIndex.foreach { case (value, offset) =>
      val target = address + offset.toLong
      if (!changed.contains(target)) {
        val page  = pages.getOrElseUpdate(target >>> 12, Page())
        val index = (target & 4095L).toInt
        page.bytes(index) = value
        page.valid.set(index)
        val _ = missing.remove(target)
        val _ = issued.remove(target)
      }
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneNoIterator.no-iterator"))
  private def ranges(addresses: Addresses, stores: Boolean = false): Vec[HostMemoryRange] = {
    val chunks    = mutable.ArrayBuffer.empty[HostMemoryRange]
    val source    = addresses.iterator.buffered
    var remaining = maxBatchBytes
    while (
      source.hasNext && remaining >= (if (stores) math.max(1, atomicWidth(source.head)) else 1) &&
      chunks.size < maxBatchRanges
    ) {
      val base     = source.next()
      val width    = if (stores) math.max(1, atomicWidth(base)) else 1
      val capacity = math.min(65536, remaining / width * width)
      var size     = 1
      remaining -= 1
      while (
        source.hasNext && remaining > 0 && size < capacity && source.head == base + size &&
        (!stores || atomicWidth(source.head) == atomicWidth(base))
      ) {
        val _ = source.next()
        size += 1
        remaining -= 1
      }
      chunks += HostMemoryRange(GuestAddress(base), size.unsafe[Int > 0])
    }
    Vec.from(chunks)
  }

  def hasPending: Boolean                               = missing.nonEmpty
  def hasDirty: Boolean                                 = changed.nonEmpty
  def hasDirty(selected: Vec[HostMemoryRange]): Boolean = selectedDirty(selected).nonEmpty

  def requests(): Vec[HostMemoryRange] = {
    val result = ranges(missing)
    result.foreach(range => issued.add(range.address.raw, range.size.raw))
    result
  }

  def supply(values: Vec[HostMemoryBytes]): Either[String, Unit] = {
    val valid = values.forall(value =>
      value.bytes.indices.forall { index =>
        val address = value.address.raw + index.toLong
        missing.contains(address) || issued.contains(address)
      }
    )
    if (!valid) Left("host memory reply includes bytes that were not requested")
    else {
      values.foreach { value =>
        value.bytes.zipWithIndex.foreach { case (byte, index) =>
          val address = value.address.raw + index.toLong
          // A concurrent lane may have written this byte while the request was in flight.
          storeIfAbsent(address, byte)
        }
        issued.remove(value.address.raw, value.bytes.size)
        missing.remove(value.address.raw, value.bytes.size)
      }
      Right(())
    }
  }

  def dirty: Vec[HostMemoryBytes] =
    ranges(changed, stores = true).map {
      range =>
        HostMemoryBytes(
          range.address,
          Vec.from((0 until range.size.raw).map(index => byte(range.address.raw + index.toLong))),
          atomicWidth(range.address.raw)
        )
    }
  def takeDirty(): Vec[HostMemoryBytes] = {
    val result = dirty
    result.foreach(range => changed.remove(range.address.raw, range.bytes.size))
    result
  }

  def takeDirty(selected: Vec[HostMemoryRange]): Vec[HostMemoryBytes] = {
    val addresses = selectedDirty(selected)
    val result    = ranges(addresses, stores = true).map { range =>
      HostMemoryBytes(
        range.address,
        Vec.from((0 until range.size.raw).map(index => byte(range.address.raw + index.toLong))),
        atomicWidth(range.address.raw)
      )
    }
    result.foreach(range => changed.remove(range.address.raw, range.bytes.size))
    result
  }

  /** Acquire discards stale host reads, retaining writes other lanes have not yet published. */
  def invalidateClean(): Unit =
    pages.filterInPlace { (number, page) =>
      val dirty = java.util.BitSet(pageSize)
      changed.pages.get(number).foreach(dirty.or(_))
      page.valid.and(dirty)
      !page.valid.isEmpty
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def invalidateClean(selected: Vec[HostMemoryRange]): Unit =
    pages.foreach { (number, page) =>
      val base = number << 12
      selected.foreach { range =>
        val start = math.max(base, range.address.raw)
        val end   = math.min(base + pageSize, range.address.raw + range.size.raw.toLong)
        if (start < end) {
          var offset = page.valid.nextSetBit((start - base).toInt)
          while (offset >= 0 && offset < end - base) {
            val address = base + offset.toLong
            if (!changed.contains(address)) {
              page.valid.clear(offset)
              missing.add(address)
              issued.remove(address)
            }
            offset = page.valid.nextSetBit(offset + 1)
          }
        }
      }
    }

}

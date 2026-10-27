package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import scala.collection.mutable

/** Sparse host bytes shared by aliases within one dispatch. The execution session owns locking. Missing bytes are
  * requested exactly; a page is never assumed readable because one byte is readable.
  */
private class HostMemoryMirror(maxBatchBytes: Int >= 8 = 1048576, maxBatchRanges: Int > 0 = 1024) {
  private enum Batch derives CanEqual {
    case Reads, Stores
  }

  private val pageSize = 4096

  private def number(address: Long): Long = address >>> 12
  private def offset(address: Long): Int  = (address & 4095L).toInt

  // Pages are storage only: validity remains byte-exact, including allocation
  // boundaries and holes. No host access is widened to the containing page.
  private class Page {
    val bytes: Array[Byte]       = Array.ofDim[Byte](pageSize)
    val valid: java.util.BitSet  = java.util.BitSet(pageSize)
    val words1: java.util.BitSet = java.util.BitSet(pageSize)
    val words2: java.util.BitSet = java.util.BitSet(pageSize / 2)
    val words4: java.util.BitSet = java.util.BitSet(pageSize / 4)
    val words8: java.util.BitSet = java.util.BitSet(pageSize / 8)

    def atomicWidth(offset: Int): Int in (0, 1, 2, 4, 8) =
      if (words8.get(offset / 8)) 8
      else if (words4.get(offset / 4)) 4
      else if (words2.get(offset / 2)) 2
      else if (words1.get(offset)) 1
      else 0
  }

  private class Addresses {
    val pages: mutable.LongMap[java.util.BitSet] = mutable.LongMap.empty[java.util.BitSet]
    def nonEmpty: Boolean                        = pages.nonEmpty
    def contains(address: Long): Boolean         = pages.get(number(address)).exists(_.get(offset(address)))
    def add(address: Long, size: Int = 1): Unit  = spans(address, size) { (number, offset, length, _) =>
      pages.getOrElseUpdate(number, java.util.BitSet(pageSize)).set(offset, offset + length)
    }
    def remove(address: Long, size: Int = 1): Unit = spans(address, size) { (number, offset, length, _) =>
      pages.get(number).foreach { bits =>
        bits.clear(offset, offset + length)
        if (bits.isEmpty) { val _ = pages.subtractOne(number) }
      }
    }
    @SuppressWarnings(Array("scalafix:BannedSyntax.no-iterator"))
    def iterator: Iterator[Long] = pages.view.toArray.sortBy(_._1).iterator.flatMap { (number, bits) =>
      Iterator.unfold(bits.nextSetBit(0)) { offset =>
        if (offset < 0) None else Some(((number << 12) + offset.toLong, bits.nextSetBit(offset + 1)))
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private inline def spans(address: Long, size: Int)(inline f: (Long, Int, Int, Int) => Unit): Unit = {
    var consumed = 0
    while (consumed < size) {
      val current = address + consumed.toLong
      val length  = math.min(pageSize - offset(current), size - consumed)
      f(number(current), offset(current), length, consumed)
      consumed += length
    }
  }

  private val pages                                              = mutable.LongMap.empty[Page]
  private val missing                                            = Addresses()
  private val issued                                             = Addresses()
  private val changed                                            = Addresses()
  private def atomicWidth(address: Long): Int in (0, 1, 2, 4, 8) =
    pages.get(number(address)).fold[Int in (0, 1, 2, 4, 8)](0)(_.atomicWidth(offset(address)))
  private def bytes(address: Long, size: Int): Vec[Byte] = {
    val result = Array.ofDim[Byte](size)
    spans(address, size)((number, offset, length, consumed) =>
      Array.copy(pages(number).bytes, offset, result, consumed, length)
    )
    Vec.unsafeWrapArray(result)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def selectedDirty(selected: Vec[HostMemoryRange]): Addresses = {
    val result = Addresses()
    val masks  = mutable.LongMap.empty[java.util.BitSet]
    selected.foreach { range =>
      val last    = range.address.raw + range.size.toLong
      var address = range.address.raw
      while (address < last) {
        val number = address >> 12
        val base   = number << 12
        val end    = math.min(base + pageSize, last)
        if (changed.pages.contains(number))
          masks.getOrElseUpdate(number, java.util.BitSet(pageSize)).set((address - base).toInt, (end - base).toInt)
        address = base + pageSize
      }
    }
    changed.pages.view.foreach { (number, dirty) =>
      masks.get(number).foreach { mask =>
        pages.get(number).foreach { page =>
          val base                                                              = number << 12
          def expand(words: java.util.BitSet, width: Int in (1, 2, 4, 8)): Unit = {
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
    }
    result
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  def read(address: GuestAddress, size: Int >= 0): Option[Vec[Byte]] = {
    val result = Array.ofDim[Byte](size)
    var absent = false
    spans(address.raw, size) { (number, offset, length, consumed) =>
      val page = pages.getOrNull(number)
      val end  = offset + length
      if (page == null) {
        missing.add(address.raw + consumed.toLong, length)
        absent = true
      } else {
        var gap = page.valid.nextClearBit(offset)
        while (gap < end) {
          val filled = math.min(page.valid.nextSetBit(gap) match { case -1 => end; case next => next }, end)
          missing.add((number << 12) + gap.toLong, filled - gap)
          absent = true
          gap = page.valid.nextClearBit(filled)
        }
        if (!absent) Array.copy(page.bytes, offset, result, consumed, length)
      }
    }
    if (absent) None else Some(Vec.unsafeWrapArray(result))
  }

  def write(at: GuestAddress, values: Vec[Byte], atomicWidth: Int in (0, 1, 2, 4, 8) = 0): Unit = {
    val address = at.raw
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
    if (atomicWidth != 0 && values.nonEmpty && address % atomicWidth == 0) {
      val page  = pages(number(address))
      val index = offset(address)
      if (atomicWidth == 8) page.words8.set(index / 8)
      else if (atomicWidth == 4) page.words4.set(index / 4)
      else if (atomicWidth == 2) page.words2.set(index / 2)
      else page.words1.set(index)
    }
    changed.add(address, values.size)
    missing.remove(address, values.size)
  }

  private def storeIfAbsent(address: Long, value: Byte): Unit = {
    val page  = pages.getOrElseUpdate(number(address), Page())
    val index = offset(address)
    if (!page.valid.get(index)) {
      page.bytes(index) = value
      page.valid.set(index)
    }
  }

  /** Record a value returned by a native atomic operation without making it dirty for publication. */
  def observeAtomic(address: GuestAddress, values: Vec[Byte]): Unit =
    values.zipWithIndex.foreach { case (value, position) =>
      val target = address.raw + position.toLong
      if (!changed.contains(target)) {
        val page  = pages.getOrElseUpdate(number(target), Page())
        val index = offset(target)
        page.bytes(index) = value
        page.valid.set(index)
        val _ = missing.remove(target)
        val _ = issued.remove(target)
      }
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:BannedSyntax.no-iterator"))
  private def ranges(addresses: Addresses, batch: Batch): Vec[HostMemoryRange] = {
    val stores    = batch == Batch.Stores
    val chunks    = mutable.ArrayBuffer.empty[HostMemoryRange]
    val source    = addresses.iterator.buffered
    var remaining = maxBatchBytes.raw
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
    val result = ranges(missing, Batch.Reads)
    result.foreach(range => issued.add(range.address.raw, range.size))
    result
  }

  def supply(values: Vec[HostMemoryBytes]): Either[NonEmpty[String], Unit] = {
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

  private def take(addresses: Addresses): Vec[HostMemoryBytes] = {
    val result = ranges(addresses, Batch.Stores).map { range =>
      HostMemoryBytes(range.address, bytes(range.address.raw, range.size), atomicWidth(range.address.raw))
    }
    result.foreach(range => changed.remove(range.address.raw, range.bytes.size))
    result
  }

  def takeDirty(): Vec[HostMemoryBytes] = take(changed)

  def takeDirty(selected: Vec[HostMemoryRange]): Vec[HostMemoryBytes] = take(selectedDirty(selected))

  /** Acquire discards stale host reads, retaining writes other lanes have not yet published. */
  def invalidateClean(): Unit =
    pages.filterInPlace { (number, page) =>
      val dirty = java.util.BitSet(pageSize)
      changed.pages.get(number).foreach(dirty.or(_))
      page.valid.and(dirty)
      !page.valid.isEmpty
    }
}

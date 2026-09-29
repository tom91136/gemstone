package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import scala.collection.mutable

/** Sparse host bytes shared by the lanes and aliases of one execution. The execution service owns locking. Missing
  * bytes are requested exactly; a page is never assumed readable because one byte is readable.
  */
private class HostMemoryMirror(maxBatchBytes: Int = 1024 * 1024, maxBatchRanges: Int = 1024) {
  require(maxBatchBytes >= 8, "a batch must fit the largest atomic word")
  require(maxBatchRanges > 0, "a batch must allow at least one range")

  private val atomicStores                    = mutable.LongMap.empty[Int]
  private def atomicWidth(address: Long): Int =
    if (atomicStores.get(address & ~7L).contains(8)) 8
    else if (atomicStores.get(address & ~3L).contains(4)) 4 else 0

  private val bytes   = mutable.LongMap.empty[Byte]
  private val missing = mutable.TreeSet.empty[Long]
  private val issued  = mutable.TreeSet.empty[Long]
  private val changed = mutable.TreeSet.empty[Long]

  def read(address: Long, size: Int): Option[Vec[Byte]] = {
    val absent = (0 until size).map(address + _.toLong).filterNot(bytes.contains).toVector
    if (absent.nonEmpty) {
      missing ++= absent
      None
    } else Some(Vec.from((0 until size).map(index => bytes(address + index.toLong))))
  }

  def write(address: Long, values: Vec[Byte], atomicWidth: Int = 0): Unit = {
    // Preserve the width of naturally aligned stores when publishing a batch.
    // Mixed-width overlapping accesses do not have a cross-agent atomicity guarantee.
    values.indices.foreach { offset =>
      val target = address + offset
      if (atomicStores.get(target & ~7L).contains(8)) { val _ = atomicStores.remove(target & ~7L) }
      if (atomicStores.get(target & ~3L).contains(4)) { val _ = atomicStores.remove(target & ~3L) }
    }
    if (atomicWidth != 0) atomicStores.update(address, atomicWidth)
    values.zipWithIndex.foreach { case (value, offset) =>
      val target = address + offset.toLong
      bytes.update(target, value)
      val _ = changed.add(target)
      val _ = missing.remove(target)
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneNoIterator.no-iterator"))
  private def ranges(addresses: Iterable[Long], stores: Boolean = false): Vec[HostMemoryRange] = {
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

  def hasPending: Boolean = missing.nonEmpty
  def hasDirty: Boolean   = changed.nonEmpty

  def requests(): Vec[HostMemoryRange] = {
    val result = ranges(missing)
    result.foreach(range => issued ++= (0 until range.size.raw).map(range.address.raw + _.toLong))
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
      values.foreach(value =>
        value.bytes.zipWithIndex.foreach { case (byte, index) =>
          val address = value.address.raw + index.toLong
          // A concurrent lane may have written this byte while the request was in flight.
          if (!bytes.contains(address)) bytes.update(address, byte)
          val _ = issued.remove(address)
          val _ = missing.remove(address)
        }
      )
      Right(())
    }
  }

  def dirty: Vec[HostMemoryBytes] =
    ranges(changed, stores = true).map {
      range =>
        HostMemoryBytes(
          range.address,
          Vec.from((0 until range.size.raw).map(index => bytes(range.address.raw + index.toLong))),
          atomicWidth(range.address.raw)
        )
    }
  def takeDirty(): Vec[HostMemoryBytes] = {
    val result = dirty
    result.foreach(range =>
      range.bytes.indices.foreach { index =>
        val _ = changed.remove(range.address.raw + index.toLong)
      }
    )
    result
  }

  /** Acquire discards stale host reads, retaining writes other lanes have not yet published. */
  def invalidateClean(): Unit = bytes.filterInPlace((address, _) => changed.contains(address))
}

package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.rpc.ExecutionProtocol.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.collection.mutable

/** Sparse host bytes shared by aliases and dependent completions. The execution service owns locking. Missing bytes are
  * requested exactly; a page is never assumed readable because one byte is readable.
  */
private[execution] final class HostMemoryMirror(maxBatchBytes: Int = 1024 * 1024, maxBatchRanges: Int = 1024) {
  private val atomicStores                    = mutable.LongMap.empty[Int]
  private def atomicWidth(address: Long): Int =
    if (atomicStores.get(address & ~7L).contains(8)) 8
    else if (atomicStores.get(address & ~3L).contains(4)) 4 else 0

  private val bytes   = mutable.LongMap.empty[Byte]
  private val missing = mutable.Map.empty[Long, mutable.TreeSet[Long]]
  private val issued  = mutable.Map.empty[Long, mutable.TreeSet[Long]]
  private val changed = mutable.Map.empty[Long, mutable.TreeSet[Long]]

  def read(completion: Long, address: Long, size: Int): Option[Vec[Byte]] = {
    val absent = (0 until size).map(address + _.toLong).filterNot(bytes.contains).toVector
    if (absent.nonEmpty) {
      missing.getOrElseUpdate(completion, mutable.TreeSet.empty) ++= absent
      None
    } else Some(Vec.from((0 until size).map(index => bytes(address + index.toLong))))
  }

  def write(completion: Long, address: Long, values: Vec[Byte], atomicWidth: Int = 0): Unit = {
    // Preserve the width of naturally aligned stores when publishing a batch.
    // Mixed-width overlapping accesses do not have a cross-agent atomicity guarantee.
    values.indices.foreach { offset =>
      val target = address + offset
      if (atomicStores.get(target & ~7L).contains(8)) { val _ = atomicStores.remove(target & ~7L) }
      if (atomicStores.get(target & ~3L).contains(4)) { val _ = atomicStores.remove(target & ~3L) }
    }
    if (atomicWidth != 0) atomicStores.update(address, atomicWidth)
    val dirty = changed.getOrElseUpdate(completion, mutable.TreeSet.empty)
    values.zipWithIndex.foreach { case (value, offset) =>
      val target = address + offset.toLong
      bytes.update(target, value)
      val _ = dirty.add(target)
      missing.values.foreach { set =>
        val _ = set.remove(target)
      }
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
      chunks += HostMemoryRange(GuestAddress(base), size.refineUnsafe[Positive])
    }
    Vec.from(chunks)
  }

  def hasPending(completion: Long): Boolean = missing.get(completion).exists(_.nonEmpty)
  def hasDirty(completion: Long): Boolean   = changed.get(completion).exists(_.nonEmpty)

  def requests(completion: Long): Vec[HostMemoryRange] = {
    val result = ranges(missing.getOrElse(completion, mutable.TreeSet.empty))
    val sent   = issued.getOrElseUpdate(completion, mutable.TreeSet.empty)
    result.foreach(range => sent ++= (0 until range.size).map(range.address.value + _.toLong))
    result
  }

  def supply(completion: Long, values: Vec[HostMemoryBytes]): Either[String, Unit] = {
    val pending     = missing.getOrElse(completion, mutable.TreeSet.empty[Long])
    val outstanding = issued.getOrElse(completion, mutable.TreeSet.empty[Long])
    val valid       = values.forall(value =>
      value.bytes.indices.forall { index =>
        val address = value.address.value + index.toLong
        pending.contains(address) || outstanding.contains(address)
      }
    )
    if (!valid) Left("host memory reply includes bytes that were not requested")
    else {
      values.foreach(value =>
        value.bytes.zipWithIndex.foreach { case (byte, index) =>
          val address = value.address.value + index.toLong
          // A concurrent lane may have written this byte while the request was in flight.
          if (!bytes.contains(address)) bytes.update(address, byte)
          val _ = outstanding.remove(address)
          missing.values.foreach { set =>
            val _ = set.remove(address)
          }
        }
      )
      Right(())
    }
  }

  def dirty(completion: Long): Vec[HostMemoryBytes] =
    ranges(changed.getOrElse(completion, mutable.TreeSet.empty), stores = true).map {
      range =>
        HostMemoryBytes(
          range.address,
          Vec.from((0 until range.size).map(index => bytes(range.address.value + index.toLong))),
          atomicWidth(range.address.value)
        )
    }
  def takeDirty(completion: Long): Vec[HostMemoryBytes] = {
    val result = dirty(completion)
    changed.get(completion).foreach { pending =>
      result.foreach(range =>
        (0 until range.bytes.size).foreach { index =>
          val _ = pending.remove(range.address.value + index.toLong)
        }
      )
    }
    result
  }

  /** Acquire discards stale host reads, retaining writes other lanes have not yet published. */
  def invalidateClean(): Unit = {
    val dirty = changed.values.view.flatMap(_.view).toSet
    bytes.filterInPlace((address, _) => dirty.contains(address))
  }

  def clear(): Unit = { atomicStores.clear(); bytes.clear(); missing.clear(); changed.clear(); issued.clear() }
}

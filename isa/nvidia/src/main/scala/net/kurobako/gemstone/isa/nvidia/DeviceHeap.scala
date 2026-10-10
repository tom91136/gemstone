package net.kurobako.gemstone.isa.nvidia

import cats.syntax.all.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.runtime.LongRef

private[gemstone] object DeviceHeap {
  val Base: Long = 0x700000000000L
}

/** Storage for device-side `malloc`/`free`, private to one dispatch and layered over its address space. Accesses wholly
  * outside heap blocks reach the backing address space; an access straddling a block boundary faults.
  */
private[gemstone] class DeviceHeap(backing: AddressSpace, base: GuestAddress = GuestAddress(DeviceHeap.Base))
    extends AddressSpace {
  // Blocks sorted by unsigned start, replaced whole under the exclusive lock so a lookup needs no lock of its own.
  private class Index(val starts: Array[Long], val blocks: Array[Array[Byte]])

  // A located range is a block index and offset packed as (index << 32) | offset, or one of these.
  private final val Backing  = -1L
  private final val Straddle = -2L

  // Stores, atomics, malloc and free hold the lock, which keeps them single-copy atomic, and make the sequence odd for
  // their duration. Readers copy without the lock and keep the copy only if the sequence stayed even and unchanged, so
  // they never return bytes torn by a store; otherwise they retry once under the lock.
  private val lock     = ReentrantLock()
  private val sequence = AtomicInteger()

  private inline def exclusive[A](inline body: A): A = {
    lock.lock()
    val _ = sequence.getAndIncrement()
    try body
    finally {
      sequence.setRelease(sequence.get() + 1)
      lock.unlock()
    }
  }
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  @volatile private var index = Index(Array.emptyLongArray, Array.empty[Array[Byte]])
  // The unsigned end of the allocated range, absent once blocks reach the top of the address space. Volatile so that
  // accesses past it can skip the heap without the lock.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  @volatile private var next: Option[Long] = Some(base.raw)

  def allocate(size: Int > 0): Either[Fault, GuestAddress] = exclusive {
    next match {
      case Some(start) if java.lang.Long.compareUnsigned(start + size.raw.toLong - 1L, start) >= 0 =>
        val following = start + ((size.raw.toLong + 255L) & -256L)
        // Starts only increase, so the new block sorts last.
        val current = index
        index = Index(current.starts :+ start, current.blocks :+ Array.ofDim[Byte](size.raw))
        // A final block may consume the remaining address space, alignment padding included.
        next = Option.when(java.lang.Long.compareUnsigned(following, start) > 0)(following)
        Right(GuestAddress(start))
      case _ => Left(Fault.Memory(s"device heap cannot fit an allocation of $size bytes"))
    }
  }

  def free(address: GuestAddress): Boolean = exclusive {
    val current = index
    val found   = floor(current, address.raw)
    found >= 0 && current.starts(found) == address.raw && {
      index = Index(
        current.starts.patch(found, Nil, 1),
        current.blocks.patch(found, Nil, 1)
      )
      true
    }
  }

  private def outside(address: GuestAddress, size: Long): Boolean =
    (java.lang.Long.compareUnsigned(address.raw, base.raw) < 0 &&
      java.lang.Long.compareUnsigned(size, base.raw - address.raw) <= 0) ||
      (next match {
        case Some(end) => java.lang.Long.compareUnsigned(address.raw, end) >= 0
        case None      => false
      })

  /** The position of the last block starting at or below `address`, or -1. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def floor(snapshot: Index, address: Long): Int = {
    var low  = 0
    var high = snapshot.starts.length - 1
    while (low <= high) {
      val middle = (low + high) >>> 1
      if (java.lang.Long.compareUnsigned(snapshot.starts(middle), address) <= 0) low = middle + 1
      else high = middle - 1
    }
    high
  }

  private def locate(snapshot: Index, address: GuestAddress, size: Long): Long = {
    val previous = floor(snapshot, address.raw)
    if (
      previous >= 0 && AddressRanges.containsUnsigned(
        snapshot.starts(previous),
        snapshot.blocks(previous).length.toLong,
        address.raw,
        size
      )
    ) (previous.toLong << 32) | (address.raw - snapshot.starts(previous))
    else {
      val overlapsPrevious = previous >= 0 &&
        java.lang.Long.compareUnsigned(
          address.raw - snapshot.starts(previous),
          snapshot.blocks(previous).length.toLong
        ) < 0
      val following    = previous + 1
      val overlapsNext = following < snapshot.starts.length &&
        java.lang.Long.compareUnsigned(snapshot.starts(following) - address.raw, size) < 0
      if (size > 0 && (overlapsPrevious || overlapsNext)) Straddle else Backing
    }
  }

  private def straddles(address: GuestAddress, size: Long): Fault =
    Fault.Memory(s"${address.show} + $size bytes straddles a device heap block")

  private inline def inHeap[A](address: GuestAddress, size: Long)(inline heap: (Array[Byte], Int) => A)
      : Either[Fault, Option[A]] = {
    val snapshot = index
    val located  = locate(snapshot, address, size)
    if (located >= 0L) Right(Some(heap(snapshot.blocks((located >>> 32).toInt), located.toInt)))
    else if (located == Backing) Right(None)
    else Left(straddles(address, size))
  }

  /** Runs `heap` on the block holding the range, or `delegated` when no block does. Only mutating operations may have
    * side effects in `heap`: a shared one may run again after a concurrent store invalidates its first attempt.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private inline def route[A](address: GuestAddress, size: Int >= 0, inline mutating: Boolean)(
      inline heap: (Array[Byte], Int) => A
  )(inline delegated: Either[Fault, A]): Either[Fault, A] =
    if (outside(address, size.raw.toLong)) delegated
    else {
      val routed =
        if (mutating) exclusive(inHeap(address, size.raw.toLong)(heap))
        else {
          val version                                  = sequence.getAcquire()
          val attempt: Either[Fault, Option[A]] | Null =
            if ((version & 1) == 0) inHeap(address, size.raw.toLong)(heap) else null
          PlatformFence.acquire()
          if (attempt != null && sequence.get() == version) attempt
          else lock.withLock(inHeap(address, size.raw.toLong)(heap))
        }
      routed match {
        case Left(fault)        => Left(fault)
        case Right(Some(value)) => Right(value)
        case Right(None)        => delegated
      }
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private def external(ranges: AddressSpace.ReadRanges)(prepare: AddressSpace.ReadRanges => Either[Fault, Unit])
      : Either[Fault, Unit] =
    if (ranges.forall((address, size) => outside(address, size.raw.toLong))) prepare(ranges)
    else {
      val kept                = AddressSpace.ReadRangeBuffer()
      val snapshot            = index
      var fault: Fault | Null = null
      ranges.foreach { (address, size) =>
        if (fault == null) {
          val located = locate(snapshot, address, size.raw.toLong)
          if (located == Backing) kept.add(address, size)
          else if (located == Straddle) fault = straddles(address, size.raw.toLong)
        }
      }
      if (fault == null) prepare(kept) else Left(fault)
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private inline def routeValue(address: GuestAddress, size: Int in 1 ~ 8, into: LongRef)(
      inline delegated: Fault | Null
  ): Fault | Null =
    if (outside(address, size.raw.toLong)) delegated
    else
      route(address, size.widen[Int >= 0], mutating = false)((bytes, offset) =>
        Option(LittleEndian.decode(bytes, offset, size.widen[Int in 0 ~ 8]))
      )(Right(None)) match {
        case Left(fault)        => fault
        case Right(Some(value)) =>
          into.elem = value
          null
        case Right(None) => delegated
      }

  private inline def routeValueWrite(address: GuestAddress, value: Long, size: Int in 1 ~ 8)(
      inline delegated: Either[Fault, Unit]
  ): Either[Fault, Unit] =
    route(address, size.widen[Int >= 0], mutating = true)((bytes, offset) =>
      LittleEndian.write(value, bytes, offset, size.widen[Int in 0 ~ 8])
    )(delegated)

  override def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
    backing.address(region, offset)

  override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
    route(
      address,
      size,
      mutating = false
    )((_, _) => AddressSpace.Location.Absolute(address))(backing.locate(address, size))

  override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
    backing.read(region, offset, size)

  override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
    backing.write(region, offset, bytes)

  override def readValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8): Either[Fault, Long] =
    backing.readValue(region, offset, size)

  override def readValueInto(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8, into: LongRef)
      : Fault | Null = backing.readValueInto(region, offset, size, into)

  override def writeValue(region: AllocationId, offset: Long >= 0, value: Long, size: Int in 1 ~ 8)
      : Either[Fault, Unit] = backing.writeValue(region, offset, value, size)

  override def atomicModifyValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)(
      update: Long => Long
  ): Either[Fault, Long] = backing.atomicModifyValue(region, offset, size)(update)

  override def readDeferred(region: AllocationId, offset: Long >= 0, size: Int >= 0)
      : AddressSpace.ReadResult[Vec[Byte]] = backing.readDeferred(region, offset, size)

  override def readValueDeferred(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)
      : AddressSpace.ReadResult[Long] = backing.readValueDeferred(region, offset, size)

  override def hasNativePointers(region: AllocationId): Boolean = backing.hasNativePointers(region)
  override def fenceHostMemory(): Either[Fault, Unit]           = backing.fenceHostMemory()
  override def flushHostWrites(): Either[Fault, Unit]           = backing.flushHostWrites()
  override def invalidateHostReads(): Unit                      = backing.invalidateHostReads()
  override def defersHostReads: Boolean                         = backing.defersHostReads
  override def flushHostReads(): Either[Fault, Unit]            = backing.flushHostReads()

  override def prepareReads(ranges: AddressSpace.ReadRanges): Either[Fault, Unit] =
    external(ranges)(backing.prepareReads(_))

  override def prepareSystemReads(ranges: AddressSpace.ReadRanges): Either[Fault, Unit] =
    external(ranges)(backing.prepareSystemReads(_))

  override def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] =
    route(address, size, mutating = false)((bytes, offset) =>
      Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(bytes, offset, offset + size.raw))
    )(backing.read(address, size))

  override def readDeferred(address: GuestAddress, size: Int >= 0): AddressSpace.ReadResult[Vec[Byte]] =
    route(address, size, mutating = false)((bytes, offset) =>
      Option(Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(bytes, offset, offset + size.raw)))
    )(Right(None)) match {
      case Left(fault)        => Left(fault)
      case Right(Some(bytes)) => Right(bytes)
      case Right(None)        => backing.readDeferred(address, size)
    }

  override def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    route(address, bytes.nonNegativeSize, mutating = true) { (destination, offset) =>
      val _ = bytes.copyToArray(destination, offset)
    }(backing.write(address, bytes))

  override def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    AddressSpace.scalar(readValueInto(address, size, _))

  override def readSystemValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    AddressSpace.scalar(readSystemValueInto(address, size, _))

  override def readValueInto(address: GuestAddress, size: Int in 1 ~ 8, into: LongRef): Fault | Null =
    routeValue(address, size, into)(backing.readValueInto(address, size, into))

  override def readSystemValueInto(address: GuestAddress, size: Int in 1 ~ 8, into: LongRef): Fault | Null =
    routeValue(address, size, into)(backing.readSystemValueInto(address, size, into))

  override def writeValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
    routeValueWrite(address, value, size)(backing.writeValue(address, value, size))

  override def writeSystemValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
    routeValueWrite(address, value, size)(backing.writeSystemValue(address, value, size))

  override def writeSystemBatch(writes: Vec[(GuestAddress, Long, Int in 1 ~ 8)]): Either[Fault, Unit] =
    if (writes.forall((address, _, size) => outside(address, size.raw.toLong))) backing.writeSystemBatch(writes)
    else exclusive {
      val snapshot = index
      writes.traverseFilter { (address, value, size) =>
        val located = locate(snapshot, address, size.raw.toLong)
        if (located == Backing) Right(Some((address, value, size)))
        else if (located == Straddle) Left(straddles(address, size.raw.toLong))
        else {
          LittleEndian.write(value, snapshot.blocks((located >>> 32).toInt), located.toInt, size.widen[Int in 0 ~ 8])
          Right(None)
        }
      }
    }.flatMap(backing.writeSystemBatch(_))

  override def atomicModifyValue(address: GuestAddress, size: Int in 1 ~ 8)(
      update: Long => Long
  ): Either[Fault, Long] =
    route(address, size.widen[Int >= 0], mutating = true)((bytes, offset) =>
      LittleEndian.atomicModify(bytes, offset, size.widen[Int in 0 ~ 8])(update)
    )(backing.atomicModifyValue(address, size)(update))
}

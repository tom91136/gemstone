package net.kurobako.gemstone.isa.nvidia

import cats.syntax.all.*
import java.util.TreeMap
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

object DeviceHeap {
  val Base: Long = 0x700000000000L
}

/** Storage for device-side `malloc`/`free`, private to one dispatch and layered over its address space. Accesses wholly
  * outside heap blocks reach the backing address space; an access straddling a block boundary faults.
  */
class DeviceHeap(backing: AddressSpace, base: GuestAddress = GuestAddress(DeviceHeap.Base))
    extends AddressSpace {
  private enum Route {
    case Backing
    case Block(bytes: Array[Byte], offset: Int)
  }

  private val lock   = ReentrantLock()
  private val blocks = TreeMap[Long, Array[Byte]]((left, right) => java.lang.Long.compareUnsigned(left, right))
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var next: Option[Long] = Some(base.raw)

  def allocate(size: Int > 0): Either[Fault, GuestAddress] = lock.withLock {
    next match {
      case Some(start) if java.lang.Long.compareUnsigned(start + size.raw.toLong - 1L, start) >= 0 =>
        val following = start + ((size.raw.toLong + 255L) & -256L)
        val _         = blocks.put(start, Array.ofDim[Byte](size.raw))
        // A final block may consume the remaining address space, alignment padding included.
        next = Option.when(java.lang.Long.compareUnsigned(following, start) > 0)(following)
        Right(GuestAddress(start))
      case _ => Left(Fault.Memory(s"device heap cannot fit an allocation of $size bytes"))
    }
  }

  def free(address: GuestAddress): Boolean = lock.withLock(Option(blocks.remove(address.raw)).nonEmpty)

  // Heap blocks lie above every address below the base, so most accesses skip the lock.
  private def below(address: GuestAddress, size: Long): Boolean =
    java.lang.Long.compareUnsigned(address.raw, base.raw) < 0 &&
      java.lang.Long.compareUnsigned(size, base.raw - address.raw) <= 0

  private def route[A](address: GuestAddress, size: Int >= 0)(heap: (Array[Byte], Int) => A)(
      delegated: => Either[Fault, A]
  ): Either[Fault, A] =
    if (below(address, size.raw.toLong)) delegated
    else lock.withLock(locateBlock(address, size.raw.toLong).map {
      case Route.Block(bytes, offset) => Some(heap(bytes, offset))
      case Route.Backing              => None
    }) match {
      case Left(fault)        => Left(fault)
      case Right(Some(value)) => Right(value)
      case Right(None)        => delegated
    }

  // The caller holds the lock.
  private def locateBlock(address: GuestAddress, size: Long): Either[Fault, Route] = {
    val previous = Option(blocks.floorEntry(address.raw))
    previous.filter(entry =>
      AddressRanges.containsUnsigned(entry.getKey, entry.getValue.length.toLong, address.raw, size)
    ) match {
      case Some(entry) => Right(Route.Block(entry.getValue, (address.raw - entry.getKey).toInt))
      case None        =>
        val overlapsPrevious = previous.exists(entry =>
          java.lang.Long.compareUnsigned(address.raw - entry.getKey, entry.getValue.length.toLong) < 0
        )
        val overlapsNext = Option(blocks.higherEntry(address.raw)).exists(entry =>
          java.lang.Long.compareUnsigned(entry.getKey - address.raw, size) < 0
        )
        if (size > 0 && (overlapsPrevious || overlapsNext))
          Left(Fault.Memory(s"${address.show} + $size bytes straddles a device heap block"))
        else Right(Route.Backing)
    }
  }

  private def external(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Vec[(GuestAddress, Int >= 0)]] =
    if (ranges.forall((address, size) => below(address, size.raw.toLong))) Right(ranges)
    else lock.withLock(ranges.traverseFilter((address, size) =>
      locateBlock(address, size.raw.toLong).map {
        case Route.Backing => Some((address, size))
        case _             => None
      }
    ))

  override def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
    backing.address(region, offset)

  override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
    route(address, size)((_, _) => AddressSpace.Location.Absolute(address))(backing.locate(address, size))

  override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
    backing.read(region, offset, size)

  override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
    backing.write(region, offset, bytes)

  override def readValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8): Either[Fault, Long] =
    backing.readValue(region, offset, size)

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

  override def prepareReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] =
    external(ranges).flatMap(backing.prepareReads(_))

  override def prepareSystemReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] =
    external(ranges).flatMap(backing.prepareSystemReads(_))

  override def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] =
    route(address, size)((bytes, offset) =>
      Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(bytes, offset, offset + size.raw))
    )(backing.read(address, size))

  override def readDeferred(address: GuestAddress, size: Int >= 0): AddressSpace.ReadResult[Vec[Byte]] =
    route(address, size)((bytes, offset) =>
      Option(Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(bytes, offset, offset + size.raw)))
    )(Right(None)) match {
      case Left(fault)        => Left(fault)
      case Right(Some(bytes)) => Right(bytes)
      case Right(None)        => backing.readDeferred(address, size)
    }

  override def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    route(address, bytes.nonNegativeSize) { (destination, offset) =>
      val _ = bytes.copyToArray(destination, offset)
    }(backing.write(address, bytes))

  override def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    route(address, size.widen[Int >= 0])((bytes, offset) =>
      LittleEndian.decode(bytes, offset, size.widen[Int in 0 ~ 8])
    )(
      backing.readValue(address, size)
    )

  override def readSystemValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    route(address, size.widen[Int >= 0])((bytes, offset) =>
      LittleEndian.decode(bytes, offset, size.widen[Int in 0 ~ 8])
    )(
      backing.readSystemValue(address, size)
    )

  override def writeValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
    route(address, size.widen[Int >= 0])((bytes, offset) =>
      LittleEndian.write(value, bytes, offset, size.widen[Int in 0 ~ 8])
    )(
      backing.writeValue(address, value, size)
    )

  override def writeSystemValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
    route(address, size.widen[Int >= 0])((bytes, offset) =>
      LittleEndian.write(value, bytes, offset, size.widen[Int in 0 ~ 8])
    )(
      backing.writeSystemValue(address, value, size)
    )

  override def writeSystemBatch(writes: Vec[(GuestAddress, Long, Int in 1 ~ 8)]): Either[Fault, Unit] =
    if (writes.forall((address, _, size) => below(address, size.raw.toLong))) backing.writeSystemBatch(writes)
    else lock.withLock(writes.traverseFilter((address, value, size) =>
      locateBlock(address, size.raw.toLong).map {
        case Route.Block(bytes, offset) =>
          LittleEndian.write(value, bytes, offset, size.widen[Int in 0 ~ 8])
          None
        case Route.Backing => Some((address, value, size))
      }
    )).flatMap(backing.writeSystemBatch(_))

  override def atomicModifyValue(address: GuestAddress, size: Int in 1 ~ 8)(
      update: Long => Long
  ): Either[Fault, Long] =
    route(address, size.widen[Int >= 0])((bytes, offset) =>
      LittleEndian.atomicModify(bytes, offset, size.widen[Int in 0 ~ 8])(update)
    )(backing.atomicModifyValue(address, size)(update))
}

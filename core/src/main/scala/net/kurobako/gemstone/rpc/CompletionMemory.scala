package net.kurobako.gemstone.rpc

import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.AllocationAddressIndex.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable
import scala.runtime.LongRef

private object CompletionMemory {

  /** Session storage seen by a dispatch. Writes record their dirty ranges against the dispatch. Entry forms take an
    * offset already inside the entry.
    */
  trait Storage {
    def lock: ReentrantLock
    def index: AllocationAddressIndex
    protected def unregistered(id: AllocationId): Fault
    protected def outside(range: AllocationRange, limit: Int > 0): Fault

    def read(entry: Entry, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]]
    def readValueInto(entry: Entry, offset: Long >= 0, size: Int in 1 ~ 8, into: LongRef): Fault | Null
    def write(entry: Entry, offset: Long >= 0, bytes: Vec[Byte], completion: Completion): Either[Fault, Unit]
    def writeValue(entry: Entry, offset: Long >= 0, value: Long, size: Int in 1 ~ 8, completion: Completion)
        : Either[Fault, Unit]
    def atomicModifyValue(entry: Entry, offset: Long >= 0, size: Int in 1 ~ 8, completion: Completion)(
        update: Long => Long
    ): Either[Fault, Long]

    final def read(range: AllocationRange): Either[Fault, Vec[Byte]] =
      located(range.allocation, range.offset, range.size)(read(_, range.offset, range.size))
    final def readValue(scalar: AllocationScalar): Either[Fault, Long] =
      located(scalar.allocation, scalar.offset, scalar.size)(entry =>
        AddressSpace.scalar(readValueInto(entry, scalar.offset, scalar.size, _))
      )
    final def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte], completion: Completion)
        : Either[Fault, Unit] = located(id, offset, bytes.nonNegativeSize)(write(_, offset, bytes, completion))
    final def writeValue(scalar: AllocationScalar, value: Long, completion: Completion): Either[Fault, Unit] =
      located(scalar.allocation, scalar.offset, scalar.size)(
        writeValue(_, scalar.offset, value, scalar.size, completion)
      )
    final def atomicModifyValue(scalar: AllocationScalar, completion: Completion)(update: Long => Long)
        : Either[Fault, Long] =
      located(scalar.allocation, scalar.offset, scalar.size)(
        atomicModifyValue(_, scalar.offset, scalar.size, completion)(update)
      )

    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    private inline def located[A](id: AllocationId, offset: Long >= 0, size: Int >= 0)(
        inline access: Entry => Either[Fault, A]
    ): Either[Fault, A] = {
      val entry = index.entry(id)
      if (entry == null) Left(unregistered(id))
      else if (!ByteRange.contains(entry.size, offset, size.toLong))
        Left(outside(AllocationRange(id, offset, size), entry.size))
      else access(entry)
    }

    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    final def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] = {
      val entry = index.entry(region)
      if (entry == null) Left(Fault.Memory(s"memory region $region is absent"))
      else Right(entry.base.offset(offset))
    }

    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    final def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] = {
      val entry = index.resolve(address, size)
      if (entry == null) Left(Fault.Memory(s"address ${address.show} is not bound"))
      else Right(AddressSpace.Location.Within(entry.id, entry.offsetOf(address)))
    }
  }

  private val Ready: Either[Fault, Unit] = Right(())
}

/** Session memory for one dispatch: device allocations directly, host-backed and unregistered bytes through the
  * client's host exchange.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.null"))
private class CompletionMemory(storage: CompletionMemory.Storage, host: HostCoherence, record: Completion)
    extends AddressSpace {
  import CompletionMemory.*

  private def resolve(address: GuestAddress, size: Int >= 0): Entry | Null = storage.index.resolve(address, size)

  private def hostBacked(id: AllocationId): Boolean = {
    val entry = storage.index.entry(id)
    entry != null && entry.allocation.hostBacked
  }

  private inline def onDevice[A](region: AllocationId, offset: Long >= 0, size: Int)(inline access: Entry => A)(
      inline otherwise: => A
  ): A = {
    val entry = storage.index.entry(region)
    if (entry != null && !entry.allocation.hostBacked && ByteRange.contains(entry.size, offset, size.toLong))
      access(entry)
    else otherwise
  }

  /** Run `device` when one device allocation backs the whole range, else `hostOwned` for host or unregistered bytes. */
  private inline def backed[E >: Fault, A](address: GuestAddress, size: Int >= 0)(
      inline device: (Entry, Long >= 0) => Either[E, A]
  )(inline hostOwned: => Either[E, A]): Either[E, A] = {
    val entry = resolve(address, size)
    if (entry != null && !entry.allocation.hostBacked) device(entry, entry.offsetOf(address))
    else if (entry != null || address.unwrap > 0) hostOwned
    else Left(Fault.Memory(s"address ${address.show} is not bound"))
  }

  override def hasNativePointers(region: AllocationId): Boolean = {
    val entry = storage.index.entry(region)
    entry != null && entry.allocation.aliased
  }

  // The synchronisation word can be device-owned while the ordered payload is
  // host-owned. Publication/acquisition must cover both kinds of backing.
  private inline def deviceAtomic(inline operation: => Either[Fault, Long]): Either[Fault, Long] = {
    val transfers = host.transfers
    (if (transfers) flushHostWrites() else Ready) match {
      case Left(fault) => Left(fault)
      case Right(_)    =>
        val result = operation
        if (transfers && result.isRight) invalidateHostReads()
        result
    }
  }

  override def fenceHostMemory(): Either[Fault, Unit] = if (host.transfers) host.fence(record) else Right(())

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  override def prepareReads(ranges: AddressSpace.ReadRanges): Either[Fault, Unit] = {
    var staged: Either[Fault, Unit] = Ready
    var requested                   = false
    val _                           = ranges.forall { (address, size) =>
      val entry       = resolve(address, size)
      val deviceOwned = entry != null && (!entry.allocation.hostBacked || !host.transfers)
      if (!deviceOwned) {
        val parts = segments(address, size)
        var part  = 0
        while (part < parts.size && staged.isRight) {
          val (begin, length) = parts(part)
          staged = backed(begin, length)((_, _) => Ready) {
            host.read(record, begin, length) match {
              case Left(_: AddressSpace.Pending.type) => requested = true; Ready
              case Left(fault: Fault)                 => Left(fault)
              case Right(_)                           => Ready
            }
          }
          part += 1
        }
      }
      staged.isRight
    }
    // Readers flush their own deferred reads, so a batch that staged none need not wait for other workers' reads.
    if (staged.isLeft) staged
    else if (requested) flushHostReads()
    else if (host.isClosed) Left(Fault.Cancelled)
    else Ready
  }

  private def systemHostRange(address: GuestAddress, size: Int >= 0): Boolean =
    host.transfers && host.accessible(address, size) && segments(address, size).forall { (begin, length) =>
      val entry = storage.index.resolve(begin, length)
      entry == null || entry.allocation.hostBacked
    }

  // System loads from host-backed storage use native atomic loads at the
  // actual scalar width. Do not first copy those control words by memcpy.
  override def prepareSystemReads(ranges: AddressSpace.ReadRanges): Either[Fault, Unit] =
    prepareReads(ranges.filter((address, size) => !systemHostRange(address, size)))
  override def flushHostWrites(): Either[Fault, Unit] = host.flushWrites(record)
  override def invalidateHostReads(): Unit            = host.invalidate(record)
  override def defersHostReads: Boolean               = host.transfers
  override def flushHostReads(): Either[Fault, Unit]  = host.flushReads(record)

  // Explicit registrations may share a view while using several non-overlapping
  // backing allocations. Split only at known boundaries; unknown gaps remain exact.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def segments(address: GuestAddress, size: Int >= 0): Vec[(GuestAddress, Int > 0)] = {
    val end = address.raw + size.toLong
    if (address.raw <= 0 || end < address.raw) Vec.from(size.refine[Int > 0].toOption.map(address -> _))
    else if (size == 0) Vec.empty
    else {
      val collected = mutable.ArrayBuilder.ofLong()
      collected += address.raw
      collected += end
      storage.index.boundaries(address.raw, end, collected)
      val points = collected.result()
      java.util.Arrays.sort(points)
      val result = Vec.newBuilder[(GuestAddress, Int > 0)]
      var index  = 1
      while (index < points.length) {
        val begin = points(index - 1)
        if (points(index) != begin) result += (GuestAddress(begin) -> (points(index) - begin).toInt.unsafe[Int > 0])
        index += 1
      }
      result.result()
    }
  }

  private def readSegments(address: GuestAddress, size: Int >= 0): AddressSpace.ReadResult[Vec[Byte]] = {
    val bytes = Vec.newBuilder[Byte]
    segments(address, size).foldLeft[AddressSpace.ReadResult[Unit]](Right(())) {
      case (result, (begin, length)) =>
        result.flatMap(_ =>
          backed(begin, length)(storage.read(_, _, length))(host.read(record, begin, length)).map { chunk =>
            val _ = bytes ++= chunk
          }
        )
    }.map(_ => bytes.result())
  }

  private def writeSegments(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    segments(address, bytes.nonNegativeSize).foldLeft[Either[Fault, Unit]](Right(())) {
      case (result, (begin, length)) =>
        result.flatMap { _ =>
          val start = (begin.raw - address.raw).toInt
          val part  = bytes.slice(start, start + length)
          backed(begin, length)(storage.write(_, _, part, record))(host.write(record, begin, part))
        }
    }

  // Device atomics serialise against every lane using this address space. Host
  // CPU participation still requires a separate coherence/atomic protocol.
  private def atomicAddress(address: GuestAddress, size: Int in 1 ~ 8)(update: Long => Long): Either[Fault, Long] =
    awaitRead {
      storage.lock.withLock {
        readSegments(address, size.widen[Int >= 0]).flatMap { bytes =>
          val previous = LittleEndian.decode(bytes, 0, size.widen[Int in 0 ~ 8])
          writeSegments(address, LittleEndian.toVec(update(previous), size.widen[Int in 0 ~ 8])).map(_ => previous)
        }
      }
    }

  private def crossesRegion(region: AllocationId, offset: Long >= 0, size: Int): Boolean = {
    val entry = storage.index.entry(region)
    entry != null && entry.allocation.aliased && (size > entry.size || offset > entry.size.raw - size)
  }

  private def redirected(region: AllocationId, offset: Long >= 0, size: Int): Boolean =
    hostBacked(region) || crossesRegion(region, offset, size)

  override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
    onDevice(region, offset, size)(storage.read(_, offset, size))(awaitRead(readDeferred(region, offset, size)))

  override def readDeferred(
      region: AllocationId,
      offset: Long >= 0,
      size: Int >= 0
  ): AddressSpace.ReadResult[Vec[Byte]] =
    onDevice(region, offset, size)(storage.read(_, offset, size)) {
      if (redirected(region, offset, size)) storage.address(region, offset).flatMap(readSegments(_, size))
      else storage.read(AllocationRange(region, offset, size))
    }

  override def readValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8): Either[Fault, Long] =
    AddressSpace.scalar(readValueInto(region, offset, size, _))

  override def readValueInto(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8, into: LongRef)
      : Fault | Null =
    onDevice(region, offset, size)(storage.readValueInto(_, offset, size, into))(
      AddressSpace.store(
        into,
        read(region, offset, size.widen[Int >= 0]).map(LittleEndian.decode(_, 0, size.widen[Int in 0 ~ 8]))
      )
    )

  override def readValueDeferred(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)
      : AddressSpace.ReadResult[Long] =
    onDevice(region, offset, size)(entry => AddressSpace.scalar(storage.readValueInto(entry, offset, size, _))) {
      if (redirected(region, offset, size))
        readDeferred(region, offset, size.widen[Int >= 0]).map(LittleEndian.decode(_, 0, size.widen[Int in 0 ~ 8]))
      else storage.readValue(AllocationScalar(region, offset, size))
    }

  override def readSystemValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    AddressSpace.scalar(readSystemValueInto(address, size, _))

  override def readSystemValueInto(address: GuestAddress, size: Int in 1 ~ 8, into: LongRef): Fault | Null =
    if (host.transfers && systemHostRange(address, size.widen[Int >= 0]))
      AddressSpace.store(into, host.atomicLoad(record, address, size))
    else readValueInto(address, size, into)

  override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
    onDevice(region, offset, bytes.size)(storage.write(_, offset, bytes, record)) {
      if (redirected(region, offset, bytes.size)) storage.address(region, offset).flatMap(writeSegments(_, bytes))
      else storage.write(region, offset, bytes, record)
    }

  override def writeValue(region: AllocationId, offset: Long >= 0, value: Long, size: Int in 1 ~ 8)
      : Either[Fault, Unit] =
    onDevice(region, offset, size)(storage.writeValue(_, offset, value, size, record)) {
      if (redirected(region, offset, size))
        write(region, offset, LittleEndian.toVec(value, size.widen[Int in 0 ~ 8]))
      else storage.writeValue(AllocationScalar(region, offset, size), value, record)
    }

  override def writeSystemValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
    writeSystemBatch(Vec((address, value, size)))

  override def writeSystemBatch(writes: Vec[(GuestAddress, Long, Int in 1 ~ 8)]): Either[Fault, Unit] = {
    val nativeAtomic = writes.flatMap { (address, value, size) =>
      size.refine[Int in (1, 2, 4, 8)].toOption.filter { width =>
        host.transfers && systemHostRange(address, width) && address.raw % width == 0
      }.map(width => HostMemoryAtomic(address, width, 0L, value, HostMemoryAtomicOperation.Store))
    }
    val stored = writes.foldLeft[Either[Fault, Unit]](Right(())) {
      case (result, (address, value, size)) =>
        if (nativeAtomic.exists(_.address == address)) result
        else result.flatMap(_ => writeValue(address, value, size))
    }
    stored.flatMap { _ =>
      val selected = Vec.from(writes.flatMap { (address, _, size) =>
        segments(address, size.widen[Int >= 0]).collect {
          case (begin, length) if backed(begin, length)((_, _) => Right(false))(Right(true)).getOrElse(false) =>
            HostMemoryRange(begin, length)
        }
      })
      val atomicRanges = Vec.from(nativeAtomic.map(atom => HostMemoryRange(atom.address, atom.size)))
      if (!host.transfers || (selected.isEmpty && nativeAtomic.isEmpty)) Right(())
      else host.store(record, Vec.from((selected ++ atomicRanges).distinct), nativeAtomic)
    }
  }

  override def atomicModifyValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)(
      update: Long => Long
  ): Either[Fault, Long] =
    onDevice(region, offset, size)(entry =>
      deviceAtomic(storage.atomicModifyValue(entry, offset, size, record)(update))
    ) {
      if (hostBacked(region)) storage.address(region, offset).flatMap(host.atomic(record, _, size)(update))
      else if (crossesRegion(region, offset, size))
        storage.address(region, offset).flatMap(atomicAddress(_, size)(update))
      else deviceAtomic(storage.atomicModifyValue(AllocationScalar(region, offset, size), record)(update))
    }

  override def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
    storage.address(region, offset)

  override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
    storage.locate(address, size) match {
      case found @ Right(_) => found
      case Left(_)          =>
        if (
          address.raw > 0 &&
          (host.enabled || segments(address, size).forall((begin, length) => storage.locate(begin, length).isRight))
        ) Right(AddressSpace.Location.Absolute(address))
        else Left(Fault.Memory(s"address ${address.show} is not bound"))
    }

  override def readDeferred(address: GuestAddress, size: Int >= 0): AddressSpace.ReadResult[Vec[Byte]] =
    readSegments(address, size)

  override def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] =
    backed(address, size)(storage.read(_, _, size))(awaitRead(readSegments(address, size)))

  override def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    AddressSpace.scalar(readValueInto(address, size, _))

  override def readValueInto(address: GuestAddress, size: Int in 1 ~ 8, into: LongRef): Fault | Null = {
    val entry = resolve(address, size.widen[Int >= 0])
    if (entry != null && !entry.allocation.hostBacked)
      storage.readValueInto(entry, entry.offsetOf(address), size, into)
    else if (entry == null && address.raw <= 0) Fault.Memory(s"address ${address.show} is not bound")
    else
      AddressSpace.store(
        into,
        awaitRead(readSegments(address, size.widen[Int >= 0])).map(LittleEndian.decode(_, 0, size.widen[Int in 0 ~ 8]))
      )
  }

  override def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    backed(address, bytes.nonNegativeSize)(storage.write(_, _, bytes, record))(writeSegments(address, bytes))

  override def writeValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
    backed(address, size.widen[Int >= 0])(storage.writeValue(_, _, value, size, record))(
      writeSegments(address, LittleEndian.toVec(value, size.widen[Int in 0 ~ 8]))
    )

  override def atomicModifyValue(address: GuestAddress, size: Int in 1 ~ 8)(update: Long => Long): Either[Fault, Long] =
    backed(address, size.widen[Int >= 0])((entry, start) =>
      deviceAtomic(storage.atomicModifyValue(entry, start, size, record)(update))
    )(host.atomic(record, address, size)(update))
}

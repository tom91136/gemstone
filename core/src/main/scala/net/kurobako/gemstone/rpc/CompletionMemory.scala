package net.kurobako.gemstone.rpc

import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.vm.*

private object CompletionMemory {

  /** Session storage seen by a dispatch. Writes record their dirty ranges against the dispatch. */
  trait Storage {
    def lock: ReentrantLock
    def allocation(id: AllocationId): Option[StoredAllocation]
    def allocations: Iterable[StoredAllocation]
    def find(address: GuestAddress, size: Int >= 0): Option[(AllocationId, Long >= 0)]
    def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]]
    def readValue(id: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8): Either[Fault, Long]
    def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte], completion: CompletionId): Either[Fault, Unit]
    def writeValue(
        id: AllocationId,
        offset: Long >= 0,
        value: Long,
        size: Int in 1 ~ 8,
        completion: CompletionId
    ): Either[Fault, Unit]
    def atomicModifyValue(id: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8, completion: CompletionId)(
        update: Long => Long
    ): Either[Fault, Long]

    final def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
      allocation(region).toRight(Fault.Memory(s"memory region $region is absent")).map(_.base.offset(offset.raw))

    final def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
      find(
        address,
        size
      ).map(AddressSpace.Location.Within(_, _)).toRight(Fault.Memory(s"address ${address.show} is not bound"))
  }
}

/** Session memory for one dispatch: device allocations directly, host-backed and unregistered bytes through the
  * client's host exchange.
  */
private class CompletionMemory(storage: CompletionMemory.Storage, host: HostCoherence, completion: CompletionId)
    extends AddressSpace {
  private def hostBacked(id: AllocationId): Boolean = storage.allocation(id).exists(_.hostBacked)

  /** Run `device` when one device allocation backs the whole range, else `hostOwned` for host or unregistered bytes. */
  private inline def backed[E >: Fault, A](address: GuestAddress, size: Int >= 0)(
      inline device: (AllocationId, Long >= 0) => Either[E, A]
  )(inline hostOwned: => Either[E, A]): Either[E, A] =
    storage.find(address, size) match {
      case Some((id, offset)) if !hostBacked(id) => device(id, offset)
      case Some(_)                               => hostOwned
      case None if address.unwrap > 0            => hostOwned
      case None                                  => Left(Fault.Memory(s"address ${address.show} is not bound"))
    }

  override def hasNativePointers(region: AllocationId): Boolean = storage.allocation(region).exists(_.aliased)

  // The synchronisation word can be device-owned while the ordered payload is
  // host-owned. Publication/acquisition must cover both kinds of backing.
  private def deviceAtomic(operation: => Either[Fault, Long]): Either[Fault, Long] =
    if (!host.transfers) operation
    else flushHostWrites().flatMap(_ => operation).map { value =>
      invalidateHostReads()
      value
    }

  override def fenceHostMemory(): Either[Fault, Unit] = if (host.transfers) host.fence(completion) else Right(())

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  override def prepareReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] = {
    var staged: Either[Fault, Unit] = Right(())
    var index                       = 0
    while (index < ranges.size && staged.isRight) {
      val (address, size) = ranges(index)
      val deviceOwned     = storage.find(address, size).exists((id, _) => !host.transfers || !hostBacked(id))
      if (!deviceOwned) {
        val parts = segments(address, size)
        var part  = 0
        while (part < parts.size && staged.isRight) {
          val (begin, length) = parts(part)
          staged = backed(begin, length)((_, _) => Right(())) {
            host.read(completion, begin, length) match {
              case Left(AddressSpace.Pending) => Right(())
              case Left(fault: Fault)         => Left(fault)
              case Right(_)                   => Right(())
            }
          }
          part += 1
        }
      }
      index += 1
    }
    staged.flatMap(_ => flushHostReads())
  }

  private def systemHostRange(address: GuestAddress, size: Int >= 0): Boolean =
    host.transfers && host.accessible(address, size) && segments(address, size).forall { (begin, length) =>
      storage.find(begin, length).forall((id, _) => hostBacked(id))
    }

  // System loads from host-backed storage use native atomic loads at the
  // actual scalar width. Do not first copy those control words by memcpy.
  override def prepareSystemReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] =
    prepareReads(Vec.from(ranges.filterNot((address, size) => systemHostRange(address, size))))
  override def flushHostWrites(): Either[Fault, Unit] = host.flushWrites(completion)
  override def invalidateHostReads(): Unit            = host.invalidate(completion)
  override def defersHostReads: Boolean               = host.transfers
  override def flushHostReads(): Either[Fault, Unit]  = host.flushReads(completion)

  // Explicit registrations may share a view while using several non-overlapping
  // backing allocations. Split only at known boundaries; unknown gaps remain exact.
  private def segments(address: GuestAddress, size: Int >= 0): Vec[(GuestAddress, Int > 0)] = {
    val end = address.raw + size.raw.toLong
    if (address.raw <= 0 || end < address.raw) Vec.from(size.raw.refine[Int > 0].toOption.map(address -> _))
    else {
      val boundaries = storage.allocations.toVector
        .flatMap(allocation => Vector(allocation.base.raw, allocation.base.raw + allocation.size.raw))
        .filter(point => point > address.raw && point < end)
      val points = (Vector(address.raw, end) ++ boundaries).distinct.sorted
      Vec.from(points.zip(points.drop(1)).map((begin, end) =>
        (GuestAddress(begin), (end - begin).toInt.unsafe[Int > 0])
      ))
    }
  }

  private def readSegments(address: GuestAddress, size: Int >= 0): AddressSpace.ReadResult[Vec[Byte]] =
    segments(address, size).foldLeft[AddressSpace.ReadResult[Vec[Byte]]](Right(Vec.empty)) {
      case (result, (begin, length)) =>
        result.flatMap(bytes =>
          backed(begin, length)(storage.read(_, _, length))(host.read(completion, begin, length)).map(bytes ++ _)
        )
    }

  private def writeSegments(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    segments(address, bytes.nonNegativeSize).foldLeft[Either[Fault, Unit]](Right(())) {
      case (result, (begin, length)) =>
        result.flatMap { _ =>
          val start = (begin.raw - address.raw).toInt
          val part  = bytes.slice(start, start + length.raw)
          backed(begin, length)(storage.write(_, _, part, completion))(host.write(completion, begin, part))
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

  private def crossesRegion(region: AllocationId, offset: Long >= 0, size: Int): Boolean =
    storage.allocation(region).exists(allocation =>
      allocation.aliased && (size > allocation.size.raw || offset.raw > allocation.size.raw - size)
    )

  private def redirected(region: AllocationId, offset: Long >= 0, size: Int): Boolean =
    hostBacked(region) || crossesRegion(region, offset, size)

  override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
    awaitRead(readDeferred(region, offset, size))

  override def readDeferred(
      region: AllocationId,
      offset: Long >= 0,
      size: Int >= 0
  ): AddressSpace.ReadResult[Vec[Byte]] =
    if (redirected(region, offset, size.raw)) storage.address(region, offset).flatMap(readSegments(_, size))
    else storage.read(region, offset, size)

  override def readValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8): Either[Fault, Long] =
    awaitRead(readValueDeferred(region, offset, size))

  override def readValueDeferred(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)
      : AddressSpace.ReadResult[Long] =
    if (redirected(region, offset, size.raw))
      readDeferred(region, offset, size.widen[Int >= 0]).map(LittleEndian.decode(_, 0, size.widen[Int in 0 ~ 8]))
    else storage.readValue(region, offset, size)

  override def readSystemValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    if (host.transfers && systemHostRange(address, size.widen[Int >= 0])) host.atomicLoad(completion, address, size)
    else readValue(address, size)

  override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
    if (redirected(region, offset, bytes.size)) storage.address(region, offset).flatMap(writeSegments(_, bytes))
    else storage.write(region, offset, bytes, completion)

  override def writeValue(region: AllocationId, offset: Long >= 0, value: Long, size: Int in 1 ~ 8)
      : Either[Fault, Unit] =
    if (redirected(region, offset, size.raw)) write(region, offset, LittleEndian.toVec(value, size.widen[Int in 0 ~ 8]))
    else storage.writeValue(region, offset, value, size, completion)

  override def writeSystemValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
    writeSystemBatch(Vec((address, value, size)))

  override def writeSystemBatch(writes: Vec[(GuestAddress, Long, Int in 1 ~ 8)]): Either[Fault, Unit] = {
    val nativeAtomic = writes.flatMap { (address, value, size) =>
      size.raw.refine[Int in (1, 2, 4, 8)].toOption.filter { width =>
        host.transfers && systemHostRange(address, width) && address.raw % width.raw == 0
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
      else host.store(completion, Vec.from((selected ++ atomicRanges).distinct), nativeAtomic)
    }
  }

  override def atomicModifyValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)(
      update: Long => Long
  ): Either[Fault, Long] =
    if (hostBacked(region)) storage.address(region, offset).flatMap(host.atomic(completion, _, size)(update))
    else if (crossesRegion(region, offset, size.raw))
      storage.address(region, offset).flatMap(atomicAddress(_, size)(update))
    else deviceAtomic(storage.atomicModifyValue(region, offset, size, completion)(update))

  override def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
    storage.address(region, offset)

  override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
    storage.locate(address, size).orElse(
      if (
        address.raw > 0 &&
        (host.enabled || segments(address, size).forall((begin, length) => storage.locate(begin, length).isRight))
      ) Right(AddressSpace.Location.Absolute(address))
      else Left(Fault.Memory(s"address ${address.show} is not bound"))
    )

  override def readDeferred(address: GuestAddress, size: Int >= 0): AddressSpace.ReadResult[Vec[Byte]] =
    readSegments(address, size)

  override def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] =
    backed(address, size)(storage.read(_, _, size))(awaitRead(readSegments(address, size)))

  override def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    backed(address, size.widen[Int >= 0])(storage.readValue(_, _, size))(
      awaitRead(readSegments(address, size.widen[Int >= 0])).map(LittleEndian.decode(_, 0, size.widen[Int in 0 ~ 8]))
    )

  override def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    backed(address, bytes.nonNegativeSize)(storage.write(_, _, bytes, completion))(writeSegments(address, bytes))

  override def writeValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
    backed(address, size.widen[Int >= 0])(storage.writeValue(_, _, value, size, completion))(
      writeSegments(address, LittleEndian.toVec(value, size.widen[Int in 0 ~ 8]))
    )

  override def atomicModifyValue(address: GuestAddress, size: Int in 1 ~ 8)(update: Long => Long): Either[Fault, Long] =
    backed(address, size.widen[Int >= 0])((id, offset) =>
      deviceAtomic(storage.atomicModifyValue(id, offset, size, completion)(update))
    )(host.atomic(completion, address, size)(update))
}

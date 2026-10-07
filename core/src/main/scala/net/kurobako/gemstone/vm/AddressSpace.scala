package net.kurobako.gemstone.vm

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

object AddressSpace {
  enum Location {
    case Within(allocation: AllocationId, offset: Long >= 0)
    case Absolute(address: GuestAddress)
  }

  /** A deferred read has not failed; the owner must flush staged reads before retrying. */
  case object Pending
  type ReadResult[+A] = Either[Fault | Pending.type, A]
}

trait AddressSpace {
  import AddressSpace.*

  @scala.annotation.tailrec
  final def awaitRead[A](read: => ReadResult[A]): Either[Fault, A] = read match {
    case Left(Pending) => flushHostReads() match {
        case Left(fault) => Left(fault)
        case Right(())   => awaitRead(read)
      }
    case Left(fault: Fault) => Left(fault)
    case Right(value)       => Right(value)
  }

  def readDeferred(region: AllocationId, offset: Long >= 0, size: Int >= 0): ReadResult[Vec[Byte]] =
    read(region, offset, size)

  def readValueDeferred(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8): ReadResult[Long] =
    readValue(region, offset, size)

  /** Pointer bytes use canonical addresses shared by all dispatches. Relocation metadata must not cache or replay them.
    */
  def hasNativePointers(region: AllocationId): Boolean = false

  /** A full fence; remote backing owners acknowledge publication before returning. */
  def fenceHostMemory(): Either[Fault, Unit] = flushHostWrites().map(_ => invalidateHostReads())
  def flushHostWrites(): Either[Fault, Unit] = Right(())
  def invalidateHostReads(): Unit            = ()
  def defersHostReads: Boolean               = false
  def flushHostReads(): Either[Fault, Unit]  = Right(())

  def read(
      region: AllocationId,
      offset: Long >= 0,
      size: Int >= 0
  ): Either[Fault, Vec[Byte]]

  def write(
      region: AllocationId,
      offset: Long >= 0,
      bytes: Vec[Byte]
  ): Either[Fault, Unit]

  def readValue(
      region: AllocationId,
      offset: Long >= 0,
      size: Int in 1 ~ 8
  ): Either[Fault, Long] =
    read(region, offset, size.widen[Int >= 0])
      .map(bytes => LittleEndian.decode(bytes, 0, size.widen[Int in 0 ~ 8]))

  def writeValue(
      region: AllocationId,
      offset: Long >= 0,
      value: Long,
      size: Int in 1 ~ 8
  ): Either[Fault, Unit] =
    write(region, offset, LittleEndian.toVec(value, size.widen[Int in 0 ~ 8]))

  def atomicModifyValue(
      @scala.annotation.unused region: AllocationId,
      @scala.annotation.unused offset: Long >= 0,
      @scala.annotation.unused size: Int in 1 ~ 8
  )(@scala.annotation.unused update: Long => Long): Either[Fault, Long] =
    Left(Fault.Unsupported("memory does not support atomic modification"))

  def prepareReads(@scala.annotation.unused ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] = Right(())

  def prepareSystemReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] = prepareReads(ranges)

  def address(
      region: AllocationId,
      offset: Long >= 0
  ): Either[Fault, GuestAddress]

  def locate(
      address: GuestAddress,
      size: Int >= 0
  ): Either[Fault, AddressSpace.Location]

  private inline def resolve[E >: Fault, A](address: GuestAddress, size: Int >= 0)(
      inline operation: (AllocationId, Long >= 0) => Either[E, A]
  ): Either[E, A] = locate(address, size) match {
    case Left(error)                                => Left(error)
    case Right(Location.Within(allocation, offset)) => operation(allocation, offset)
    case Right(Location.Absolute(absolute))         =>
      Left(Fault.Unsupported(s"absolute address ${absolute.show} requires an address-backed memory"))
  }

  /** Resolve and read in one operation. Implementations that own both the address map and memory can override this
    * boundary to avoid allocating an intermediate [[AddressSpace.Location]].
    */
  def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] =
    resolve(address, size)(read(_, _, size))

  /** Stage an address read, returning Pending until the backing owner completes it. */
  def readDeferred(address: GuestAddress, size: Int >= 0): ReadResult[Vec[Byte]] =
    resolve(address, size)(readDeferred(_, _, size))

  /** Resolve and write in one operation. */
  def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    resolve(address, bytes.nonNegativeSize)(write(_, _, bytes))

  /** Scalar form of [[read]], preserving a backing memory's allocation-free scalar implementation. */
  def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    resolve(address, size.widen[Int >= 0])(readValue(_, _, size))

  def readSystemValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    readValue(address, size)

  /** Scalar form of [[write]], preserving a backing memory's allocation-free scalar implementation. */
  def writeValue(
      address: GuestAddress,
      value: Long,
      size: Int in 1 ~ 8
  ): Either[Fault, Unit] =
    resolve(address, size.widen[Int >= 0])(writeValue(_, _, value, size))

  def writeSystemValue(
      address: GuestAddress,
      value: Long,
      size: Int in 1 ~ 8
  ): Either[Fault, Unit] = writeValue(address, value, size)

  def writeSystemBatch(
      writes: Vec[(GuestAddress, Long, Int in 1 ~ 8)]
  ): Either[Fault, Unit] = writes.foldLeft[Either[Fault, Unit]](Right(())) {
    case (result, (address, value, size)) => result.flatMap(_ => writeSystemValue(address, value, size))
  }

  /** Resolve and perform one indivisible scalar update. */
  def atomicModifyValue(
      address: GuestAddress,
      size: Int in 1 ~ 8
  )(update: Long => Long): Either[Fault, Long] =
    resolve(address, size.widen[Int >= 0])(atomicModifyValue(_, _, size)(update))
}

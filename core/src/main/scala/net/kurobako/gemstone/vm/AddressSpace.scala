package net.kurobako.gemstone.vm

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import scala.annotation.tailrec
import scala.annotation.unused
import scala.runtime.LongRef

object AddressSpace {
  enum Location {
    case Within(allocation: AllocationId, offset: Long >= 0)
    case Absolute(address: GuestAddress)
  }

  val Ready: Either[Fault, Unit] = Right(())

  trait ReadRanges {
    def length: Int
    def address(index: Int): GuestAddress
    def size(index: Int): Int >= 0

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    inline final def foreach(inline f: (GuestAddress, Int >= 0) => Unit): Unit = {
      var index = 0
      while (index < length) {
        f(address(index), size(index))
        index += 1
      }
    }

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    inline final def forall(inline p: (GuestAddress, Int >= 0) => Boolean): Boolean = {
      var index = 0
      while (index < length && p(address(index), size(index))) index += 1
      index == length
    }

    /** The ranges `keep` accepts; this batch itself when it accepts every one. */
    final def filter(keep: (GuestAddress, Int >= 0) => Boolean): ReadRanges =
      if (forall(keep)) this
      else {
        val kept = ReadRangeBuffer()
        foreach((address, size) => if (keep(address, size)) kept.add(address, size))
        kept
      }
  }

  /** Reused by one worker, cleared and refilled for each instruction. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  class ReadRangeBuffer(capacity: Int > 0 = 32) extends ReadRanges {
    private var addresses = Array.ofDim[Long](capacity)
    private var sizes     = Array.ofDim[Int](capacity)
    private var count     = 0

    def length: Int                       = count
    def isEmpty: Boolean                  = count == 0
    def address(index: Int): GuestAddress = GuestAddress(addresses(index))
    def size(index: Int): Int >= 0        = sizes(index).unsafe[Int >= 0]
    def clear(): Unit                     = count = 0

    def add(address: GuestAddress, size: Int >= 0): Unit = {
      if (count == addresses.length) {
        addresses = java.util.Arrays.copyOf(addresses, count * 2)
        sizes = java.util.Arrays.copyOf(sizes, count * 2)
      }
      addresses(count) = address.raw
      sizes(count) = size
      count += 1
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def store(into: LongRef, read: Either[Fault, Long]): Fault | Null = read match {
    case Left(fault)  => fault
    case Right(value) =>
      into.elem = value
      null
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  inline def scalar(inline read: LongRef => Fault | Null): Either[Fault, Long] = {
    val cell  = LongRef(0L)
    val fault = read(cell)
    if (fault == null) Right(cell.elem) else Left(fault)
  }

  /** A deferred read has not failed; the owner must flush staged reads before retrying. */
  case object Pending
  type ReadResult[+A] = Either[Fault | Pending.type, A]
}

trait AddressSpace derives CanEqual {
  import AddressSpace.*

  @tailrec
  final def awaitRead[A](read: => ReadResult[A]): Either[Fault, A] = read match {
    case Left(_: Pending.type) => flushHostReads() match {
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

  def readValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8): Either[Fault, Long] =
    read(region, offset, size.widen[Int >= 0]).map(LittleEndian.decode(_, 0, size.widen[Int in 0 ~ 8]))

  /** Allocation-free [[readValue]] for hot paths, null on success; memories on a hot path override both. */
  def readValueInto(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8, into: LongRef): Fault | Null =
    AddressSpace.store(into, readValue(region, offset, size))

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

  def writeValue(
      region: AllocationId,
      offset: Long >= 0,
      value: Long,
      size: Int in 1 ~ 8
  ): Either[Fault, Unit] =
    write(region, offset, LittleEndian.toVec(value, size.widen[Int in 0 ~ 8]))

  def atomicModifyValue(
      @unused region: AllocationId,
      @unused offset: Long >= 0,
      @unused size: Int in 1 ~ 8
  )(@unused update: Long => Long): Either[Fault, Long] =
    Left(Fault.Unsupported("memory does not support atomic modification"))

  def prepareReads(@unused ranges: ReadRanges): Either[Fault, Unit] = Ready

  def prepareSystemReads(ranges: ReadRanges): Either[Fault, Unit] = prepareReads(ranges)

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

  def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    locate(address, size.widen[Int >= 0]).flatMap {
      case Location.Within(allocation, offset) => readValue(allocation, offset, size)
      case Location.Absolute(absolute)         =>
        Left(Fault.Unsupported(s"absolute address ${absolute.show} requires an address-backed memory"))
    }

  def readValueInto(address: GuestAddress, size: Int in 1 ~ 8, into: LongRef): Fault | Null =
    AddressSpace.store(into, readValue(address, size))

  def readSystemValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] = readValue(address, size)

  def readSystemValueInto(address: GuestAddress, size: Int in 1 ~ 8, into: LongRef): Fault | Null =
    AddressSpace.store(into, readSystemValue(address, size))

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

package net.kurobako.gemstone.memory

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.execution.AllocationId
import net.kurobako.gemstone.execution.ExecutionFailure
import net.kurobako.gemstone.execution.ExecutionMemory

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.annotation.nowarn

type MemoryValueByteWidth = MemoryValueByteWidth.T
object MemoryValueByteWidth extends RefinedType[Int, Interval.Closed[1, 8]] {
  given (Interval.Closed[1, 8] ==> Positive0)             = Implication()
  given (Interval.Closed[1, 8] ==> Positive)              = Implication()
  given (Interval.Closed[1, 8] ==> Interval.Closed[0, 8]) = Implication()

  extension (width: MemoryValueByteWidth) {
    private[gemstone] def nonNegative: Int :| Positive0 = width.value

    @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
    private[gemstone] def scalarByteWidth: ScalarByteWidth = ScalarByteWidth.assume(width.value)
  }
}

/** Address-oriented view used by instruction sets whose pointer operands contain virtual device addresses. */
trait AddressMemory {
  def readInt8(address: GuestAddress): Either[MemoryError, Byte]
  def readInt32(address: GuestAddress): Either[MemoryError, Int]
  def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit]
  def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit]

  /** A byte-range fallback for implementations without a bulk operation. Atomic bulk access belongs to the backing
    * owner and must be provided by overriding this method.
    */
  def readBytes(address: GuestAddress, size: Int :| Positive0): Either[MemoryError, Vec[Byte]] = {
    val mutableBytes = Vec.newBuilder[Byte]
    @scala.annotation.tailrec
    def read(index: Int): Either[MemoryError, Vec[Byte]] =
      if (index == size) Right(mutableBytes.result())
      else readInt8(address.offset(index.toLong)) match {
        case Left(error) => Left(error)
        case Right(byte) =>
          val _ = mutableBytes += byte
          read(index + 1)
      }
    read(0)
  }

  def writeBytes(address: GuestAddress, bytes: Vec[Byte]): Either[MemoryError, Unit] =
    validateRange(address, bytes.nonNegativeSize) match {
      case Left(error) => Left(error)
      case Right(())   =>
        @scala.annotation.tailrec
        def write(index: Int): Either[MemoryError, Unit] =
          if (index == bytes.size) Right(())
          else writeInt8(address.offset(index.toLong), bytes(index)) match {
            case Left(error) => Left(error)
            case Right(())   => write(index + 1)
          }
        write(0)
    }

  final def validateRange(address: GuestAddress, size: Int :| Positive0): Either[MemoryError, Unit] = {
    @scala.annotation.tailrec
    def validate(index: Int): Either[MemoryError, Unit] =
      if (index == size) Right(())
      else readInt8(address.offset(index.toLong)) match {
        case Left(error) => Left(error)
        case Right(_)    => validate(index + 1)
      }

    validate(0)
  }

  def readLittleEndian(address: GuestAddress, size: MemoryValueByteWidth): Either[MemoryError, Long] = {
    @scala.annotation.tailrec
    def read(index: Int, value: Long): Either[MemoryError, Long] =
      if (index == size.value) Right(value)
      else readInt8(address.offset(index.toLong)) match {
        case Left(error) => Left(error)
        case Right(byte) => read(index + 1, value | ((byte.toLong & 0xffL) << (index * 8)))
      }
    read(0, 0L)
  }

  def writeLittleEndian(address: GuestAddress, value: Long, size: MemoryValueByteWidth): Either[MemoryError, Unit] =
    validateRange(address, size.nonNegative) match {
      case Left(error) => Left(error)
      case Right(())   =>
        @scala.annotation.tailrec
        def write(index: Int): Either[MemoryError, Unit] =
          if (index == size.value) Right(())
          else writeInt8(address.offset(index.toLong), (value >>> (index * 8)).toByte) match {
            case Left(error) => Left(error)
            case Right(())   => write(index + 1)
          }
        write(0)
    }

  /** Atomically replace a value and return its previous contents. Views over shared storage must delegate the whole
    * operation to their backing owner.
    */
  @nowarn("msg=unused explicit parameter")
  def atomicModifyLittleEndian(
      address: GuestAddress,
      size: MemoryValueByteWidth
  )(update: Long => Long): Either[MemoryError, Long] =
    Left(MemoryError.UnsupportedAtomic(address, size))
}

/** Optional device-side allocation capability used by instruction-level malloc/free calls. */
trait DeviceAllocationMemory extends AddressMemory {
  def allocate(size: DeviceAllocationByteSize): Either[MemoryError, GuestAddress]
  def free(address: GuestAddress): Boolean
}

type DeviceAllocationByteSize = DeviceAllocationByteSize.T
object DeviceAllocationByteSize extends RefinedType[Long, Interval.Closed[1L, 2147483647L]]

final case class AddressBinding(
    base: GuestAddress,
    allocation: AllocationId,
    offset: Long :| Positive0,
    span: Long :| Positive
)

/** Process-neutral allocation IDs projected into a per-dispatch virtual address space. */
final class ExecutionAddressSpace(
    memory: ExecutionMemory,
    bindings: Vec[AddressBinding]
) extends AddressMemory {
  override def readBytes(address: GuestAddress, size: Int :| Positive0): Either[MemoryError, Vec[Byte]] =
    read(address, size)
  override def writeBytes(address: GuestAddress, bytes: Vec[Byte]): Either[MemoryError, Unit] = write(address, bytes)

  override def readInt8(address: GuestAddress): Either[MemoryError, Byte] =
    readLittleEndian(address, MemoryValueByteWidth(1)).map(_.toByte)

  override def readInt32(address: GuestAddress): Either[MemoryError, Int] =
    readLittleEndian(address, MemoryValueByteWidth(4)).map(_.toInt)

  override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
    writeLittleEndian(address, value.toLong, MemoryValueByteWidth(1))

  override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
    writeLittleEndian(address, value.toLong, MemoryValueByteWidth(4))

  override def readLittleEndian(address: GuestAddress, size: MemoryValueByteWidth): Either[MemoryError, Long] =
    for {
      resolved <- resolve(address, size.value)
      (binding, relative) = resolved
      offset <- allocationOffset(address, binding, relative, size.value)
      value  <- memory.readValue(binding.allocation, offset, size)
        .left.map(failure => toMemoryError(address, size.value, failure))
    } yield value

  override def writeLittleEndian(
      address: GuestAddress,
      value: Long,
      size: MemoryValueByteWidth
  ): Either[MemoryError, Unit] =
    resolve(address, size.value) match {
      case Left(error)                => Left(error)
      case Right((binding, relative)) =>
        allocationOffset(address, binding, relative, size.value) match {
          case Left(error)   => Left(error)
          case Right(offset) =>
            memory.writeValue(binding.allocation, offset, value, size)
              .left.map(failure => toMemoryError(address, size.value, failure))
        }
    }

  override def atomicModifyLittleEndian(
      address: GuestAddress,
      size: MemoryValueByteWidth
  )(update: Long => Long): Either[MemoryError, Long] =
    resolve(address, size.value) match {
      case Left(error)                => Left(error)
      case Right((binding, relative)) =>
        allocationOffset(address, binding, relative, size.value) match {
          case Left(error)   => Left(error)
          case Right(offset) =>
            memory.atomicModifyValue(binding.allocation, offset, size)(update)
              .left.map(failure => toMemoryError(address, size.value, failure))
        }
    }

  private def read(address: GuestAddress, size: Int :| Positive0): Either[MemoryError, Vec[Byte]] =
    for {
      resolved <- resolve(address, size)
      (binding, relative) = resolved
      offset <- allocationOffset(address, binding, relative, size)
      bytes  <- memory.read(binding.allocation, offset, size)
        .left.map(failure => toMemoryError(address, size, failure))
    } yield bytes

  private def write(address: GuestAddress, bytes: Vec[Byte]): Either[MemoryError, Unit] = {
    val size = bytes.length
    resolve(address, size) match {
      case Left(error)                => Left(error)
      case Right((binding, relative)) =>
        allocationOffset(address, binding, relative, size) match {
          case Left(error)   => Left(error)
          case Right(offset) =>
            memory.write(binding.allocation, offset, bytes)
              .left.map(failure => toMemoryError(address, size, failure))
        }
    }
  }

  private def resolve(
      address: GuestAddress,
      size: Int
  ): Either[MemoryError, (AddressBinding, Long)] =
    bindings.iterator.flatMap { binding =>
      val relative = address.value - binding.base.value
      Option.when(
        java.lang.Long.compareUnsigned(address.value, binding.base.value) >= 0 &&
          size >= 0 && size.toLong <= binding.span &&
          java.lang.Long.compareUnsigned(relative, binding.span - size.toLong) <= 0
      )(binding -> relative)
    }.nextOption().toRight(MemoryError.InvalidAddress(address, size, "address is not bound for this dispatch"))

  private def allocationOffset(
      address: GuestAddress,
      binding: AddressBinding,
      relative: Long,
      size: Int
  ): Either[MemoryError, Long :| Positive0] =
    try
      Math.addExact(binding.offset, relative).refineEither[Positive0]
        .left.map(_ => MemoryError.InvalidAddress(address, size, "allocation-relative offset is negative"))
    catch {
      case _: ArithmeticException =>
        Left(MemoryError.InvalidAddress(address, size, "allocation-relative offset overflows"))
    }

  private def toMemoryError(
      address: GuestAddress,
      size: Int,
      failure: ExecutionFailure
  ): MemoryError =
    MemoryError.InvalidAddress(address, size, failure.message)
}

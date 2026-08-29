package net.kurobako.gemstone.memory

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.AllocationId
import net.kurobako.gemstone.execution.ExecutionFailure
import net.kurobako.gemstone.execution.ExecutionMemory

import cats.syntax.either.*

/** Address-oriented view used by instruction sets whose pointer operands contain virtual device addresses. */
trait AddressMemory {
  def readInt8(address: GuestAddress): Either[MemoryError, Byte]
  def readInt32(address: GuestAddress): Either[MemoryError, Int]
  def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit]
  def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit]

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  final def validateRange(address: GuestAddress, size: Int): Either[MemoryError, Unit] = {
    require(size >= 0, s"memory range size must be non-negative, found $size")
    var index = 0
    while (index < size) {
      readInt8(address.offset(index.toLong)) match {
        case Left(error) => return Left(error)
        case Right(_)    => ()
      }
      index += 1
    }
    Right(())
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  final def readLittleEndian(address: GuestAddress, size: Int): Either[MemoryError, Long] = {
    var failure: Option[MemoryError] = None
    val value                        = LittleEndian.decode(size) { index =>
      failure.fold(
        readInt8(address.offset(index.toLong)) match {
          case Left(error) =>
            failure = Some(error)
            0.toByte
          case Right(byte) => byte
        }
      )(_ => 0.toByte)
    }
    failure.toLeft(value)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  final def writeLittleEndian(address: GuestAddress, value: Long, size: Int): Either[MemoryError, Unit] =
    validateRange(address, size).flatMap { _ =>
      var failure: Option[MemoryError] = None
      LittleEndian.write(value, size) { (index, byte) =>
        if (failure.isEmpty) failure = writeInt8(address.offset(index.toLong), byte).left.toOption
      }
      failure.toLeft(())
    }
}

final case class AddressBinding(base: GuestAddress, allocation: AllocationId, offset: Long, span: Long)

/** Process-neutral allocation IDs projected into a per-dispatch virtual address space. */
final class ExecutionAddressSpace private (
    memory: ExecutionMemory,
    bindings: Col[AddressBinding]
) extends AddressMemory {
  override def readInt8(address: GuestAddress): Either[MemoryError, Byte] =
    read(address, 1).map(_.head)

  override def readInt32(address: GuestAddress): Either[MemoryError, Int] =
    read(address, 4).map(bytes => LittleEndian.decode(bytes, 0, 4).toInt)

  override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
    write(address, Col(value))

  override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
    write(address, LittleEndian.toCol(value.toLong, 4))

  private def read(address: GuestAddress, size: Int): Either[MemoryError, Col[Byte]] =
    resolve(address, size).flatMap { case (binding, relative) =>
      memory
        .read(binding.allocation, Math.addExact(binding.offset, relative), size)
        .leftMap(toMemoryError(address, size, _))
    }

  private def write(address: GuestAddress, bytes: Col[Byte]): Either[MemoryError, Unit] =
    resolve(address, bytes.size).flatMap { case (binding, relative) =>
      memory
        .write(binding.allocation, Math.addExact(binding.offset, relative), bytes)
        .leftMap(toMemoryError(address, bytes.size, _))
    }

  private def resolve(address: GuestAddress, size: Int): Either[MemoryError, (AddressBinding, Long)] =
    bindings.collectFirst {
      case binding
          if java.lang.Long.compareUnsigned(address.value, binding.base.value) >= 0 && {
            val relative = address.value - binding.base.value
            relative >= 0L && relative <= binding.span - size.toLong
          } => binding -> (address.value - binding.base.value)
    }.toRight(MemoryError.InvalidAddress(address, size, "address is not bound for this dispatch"))

  private def toMemoryError(address: GuestAddress, size: Int, failure: ExecutionFailure): MemoryError =
    MemoryError.InvalidAddress(address, size, failure.message)
}

object ExecutionAddressSpace {
  def create(memory: ExecutionMemory, bindings: Col[AddressBinding]): Either[String, ExecutionAddressSpace] = {
    val invalid = bindings.collectFirst {
      case binding if binding.offset < 0L || binding.span <= 0L =>
        s"invalid address binding for allocation ${binding.allocation.toLong}: offset=${binding.offset}, span=${binding.span}"
    }
    invalid.toLeft(ExecutionAddressSpace(memory, bindings))
  }
}

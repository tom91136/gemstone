package net.kurobako.gemstone.memory

final case class GuestAddress(value: Long) {
  def offset(bytes: Long): GuestAddress = GuestAddress(Math.addExact(value, bytes))
  def show: String                      = s"0x${java.lang.Long.toUnsignedString(value, 16)}"
}

enum AllocationKind {
  case Device
  case Managed
  case Shared
  case Host
}

final case class Allocation(
    start: GuestAddress,
    size: Int,
    kind: AllocationKind,
    provenance: String
)

sealed trait MemoryError extends Product with Serializable {
  def message: String
}

object MemoryError {
  final case class InvalidAddress(address: GuestAddress, width: Int, detail: String) extends MemoryError {
    override def message: String = s"${address.show} + $width bytes is invalid: $detail"
  }

  final case class OutOfBounds(address: GuestAddress, width: Int, allocation: Allocation) extends MemoryError {
    override def message: String =
      s"${address.show} + $width bytes is outside ${allocation.start.show} + ${allocation.size} (${allocation.provenance})"
  }

  final case class UseAfterFree(address: GuestAddress, allocation: Allocation) extends MemoryError {
    override def message: String = s"${address.show} refers to freed allocation ${allocation.provenance}"
  }
}

trait CheckedMemory {
  def allocation: Allocation
  def readInt8(address: GuestAddress): Either[MemoryError, Byte]
  def readInt32(address: GuestAddress): Either[MemoryError, Int]
  def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit]
  def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit]
  def free(): Unit
}

final class ArrayCheckedMemory private (val allocation: Allocation, bytes: Array[Byte]) extends CheckedMemory {
  private var live = true

  override def readInt8(address: GuestAddress): Either[MemoryError, Byte] = validate(address, 1).map(bytes(_))

  override def readInt32(address: GuestAddress): Either[MemoryError, Int] = validate(address, 4).map { offset =>
    (bytes(offset) & 0xff) |
      ((bytes(offset + 1) & 0xff) << 8) |
      ((bytes(offset + 2) & 0xff) << 16) |
      ((bytes(offset + 3) & 0xff) << 24)
  }

  override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] = validate(address, 4).map {
    offset =>
      bytes(offset) = value.toByte
      bytes(offset + 1) = (value >>> 8).toByte
      bytes(offset + 2) = (value >>> 16).toByte
      bytes(offset + 3) = (value >>> 24).toByte
  }

  override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] = validate(address, 1).map {
    offset => bytes(offset) = value
  }

  override def free(): Unit = live = false

  private def validate(address: GuestAddress, width: Int): Either[MemoryError, Int] =
    if (!live) Left(MemoryError.UseAfterFree(address, allocation))
    else {
      val relative   = address.value - allocation.start.value
      val validStart = java.lang.Long.compareUnsigned(address.value, allocation.start.value) >= 0
      val validEnd   = relative >= 0L && relative <= allocation.size.toLong - width.toLong
      if (validStart && validEnd) Right(relative.toInt)
      else Left(MemoryError.OutOfBounds(address, width, allocation))
    }
}

object ArrayCheckedMemory {
  def allocate(
      size: Int,
      start: GuestAddress = GuestAddress(0x10000L),
      kind: AllocationKind = AllocationKind.Device,
      provenance: String = "anonymous"
  ): ArrayCheckedMemory = {
    require(size >= 0, s"allocation size must be non-negative: $size")
    new ArrayCheckedMemory(Allocation(start, size, kind, provenance), new Array[Byte](size))
  }
}

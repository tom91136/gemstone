package net.kurobako.gemstone.memory

import net.kurobako.gemstone.binary.LittleEndian

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

enum MemoryError(val message: String) {
  case InvalidAddress(address: GuestAddress, width: Int, detail: String)
      extends MemoryError(s"${address.show} + $width bytes is invalid: $detail")

  case OutOfBounds(address: GuestAddress, width: Int, allocation: Allocation)
      extends MemoryError(
        s"${address.show} + $width bytes is outside ${allocation.start.show} + ${allocation.size} (${allocation.provenance})"
      )

  case UseAfterFree(address: GuestAddress, allocation: Allocation)
      extends MemoryError(s"${address.show} refers to freed allocation ${allocation.provenance}")
}

trait CheckedMemory extends AddressMemory {
  def allocation: Allocation
  def free(): Unit
}

final class ArrayCheckedMemory private (val allocation: Allocation, bytes: Array[Byte]) extends CheckedMemory {
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var live = true

  override def readInt8(address: GuestAddress): Either[MemoryError, Byte] = validate(address, 1).map(bytes(_))

  override def readInt32(address: GuestAddress): Either[MemoryError, Int] =
    validate(address, 4).map(offset => LittleEndian.decode(bytes, offset, 4).toInt)

  override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
    validate(address, 4).map(offset => LittleEndian.write(value.toLong, bytes, offset, 4))

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
    ArrayCheckedMemory(Allocation(start, size, kind, provenance), Array.ofDim[Byte](size))
  }
}

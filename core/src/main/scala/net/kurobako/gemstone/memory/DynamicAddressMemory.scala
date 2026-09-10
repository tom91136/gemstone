package net.kurobako.gemstone.memory

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.LittleEndian

import java.util.TreeMap

/** AddressMemory overlay for device-side allocations such as CUDA's malloc/free.
  *
  * Device-side temporary storage is private to an emulated dispatch. Keeping it in this overlay avoids pretending that
  * it belongs to one of the host-visible allocations supplied by the RPC caller.
  */
final class DynamicAddressMemory(
    delegate: AddressMemory,
    state: DynamicAddressMemory.State = DynamicAddressMemory.State()
) extends DeviceAllocationMemory {
  private val allocations = state.allocations

  def allocate(size: Long): Long = allocate(size, Array.empty[Byte])

  def allocate(size: Long, initial: Array[Byte]): Long = state.synchronized {
    if (size <= 0L || size > Int.MaxValue.toLong) 0L
    else {
      val bytes = size.toInt
      val span  = (bytes.toLong + 255L) & -256L
      val start = state.nextAddress
      state.nextAddress = Math.addExact(start, span)
      val contents = Array.ofDim[Byte](bytes)
      Array.copy(initial, 0, contents, 0, Math.min(initial.length, contents.length))
      allocations.put(start, contents)
      start
    }
  }

  def free(address: Long): Unit = state.synchronized {
    val _ = allocations.remove(address)
  }

  override def readInt8(address: GuestAddress): Either[MemoryError, Byte] =
    state.synchronized {
      dynamicRange(address, 1).map { case (bytes, offset) => Right(bytes(offset)) }
    }.getOrElse(delegate.readInt8(address))

  override def readInt32(address: GuestAddress): Either[MemoryError, Int] =
    state.synchronized {
      dynamicRange(address, 4).map { case (bytes, offset) => Right(LittleEndian.decode(bytes, offset, 4).toInt) }
        .orElse(Option.when(overlapsDynamic(address, 4))(super.readLittleEndian(address, 4).map(_.toInt)))
    }.getOrElse(delegate.readInt32(address))

  override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
    state.synchronized {
      dynamicRange(address, 1).map { case (bytes, offset) =>
        bytes(offset) = value
        Right(())
      }
    }.getOrElse(delegate.writeInt8(address, value))

  override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
    state.synchronized {
      dynamicRange(address, 4).map { case (bytes, offset) =>
        LittleEndian.write(value.toLong, bytes, offset, 4)
        Right(())
      }.orElse(Option.when(overlapsDynamic(address, 4))(super.writeLittleEndian(address, value.toLong, 4)))
    }.getOrElse(delegate.writeInt32(address, value))

  override def readBytes(address: GuestAddress, size: Int): Either[MemoryError, Col[Byte]] = {
    require(size >= 0, s"memory byte count must be non-negative, found $size")
    state.synchronized {
      dynamicRange(address, size).map { case (bytes, offset) =>
        Right(Col.unsafeFromArray(java.util.Arrays.copyOfRange(bytes, offset, offset + size)))
      }.orElse(Option.when(overlapsDynamic(address, size))(super.readBytes(address, size)))
    }.getOrElse(delegate.readBytes(address, size))
  }

  override def writeBytes(address: GuestAddress, bytes: Col[Byte]): Either[MemoryError, Unit] =
    state.synchronized {
      dynamicRange(address, bytes.size).map { case (destination, offset) =>
        val _ = bytes.copyToArray(destination, offset, bytes.size)
        Right(())
      }.orElse(Option.when(overlapsDynamic(address, bytes.size))(super.writeBytes(address, bytes)))
    }.getOrElse(delegate.writeBytes(address, bytes))

  override def readLittleEndian(address: GuestAddress, size: Int): Either[MemoryError, Long] =
    state.synchronized {
      dynamicRange(address, size).map { case (bytes, offset) => Right(LittleEndian.decode(bytes, offset, size)) }
        .orElse(Option.when(overlapsDynamic(address, size))(super.readLittleEndian(address, size)))
    }.getOrElse(delegate.readLittleEndian(address, size))

  override def writeLittleEndian(address: GuestAddress, value: Long, size: Int): Either[MemoryError, Unit] =
    state.synchronized {
      dynamicRange(address, size).map { case (bytes, offset) =>
        LittleEndian.write(value, bytes, offset, size)
        Right(())
      }.orElse(Option.when(overlapsDynamic(address, size))(super.writeLittleEndian(address, value, size)))
    }.getOrElse(delegate.writeLittleEndian(address, value, size))

  /** Called while holding State's monitor. Mixed ranges retain byte-wise overlay routing; wholly delegated words reach
    * the delegate as one transaction even while unrelated device allocations exist.
    */
  private def overlapsDynamic(address: GuestAddress, size: Int): Boolean = {
    val previous = allocations.floorEntry(address.value)
    val next     = allocations.ceilingEntry(address.value)
    size > 0 &&
    ((previous != null && address.value - previous.getKey < previous.getValue.length.toLong) ||
      (next != null && next.getKey - address.value < size.toLong))
  }

  /** Caller holds State's monitor for the complete lookup and access. */
  private def dynamicRange(address: GuestAddress, size: Int): Option[(Array[Byte], Int)] =
    Option(allocations.floorEntry(address.value)).flatMap { entry =>
      val bytes    = entry.getValue
      val relative = address.value - entry.getKey
      Option.when(relative >= 0L && relative <= bytes.length.toLong - size.toLong)(bytes -> relative.toInt)
    }
}

object DynamicAddressMemory {
  @SuppressWarnings(Array("scalafix:GemstoneNoExplicitNew.explicit-new"))
  def apply(delegate: AddressMemory, state: State = State()): DynamicAddressMemory =
    new DynamicAddressMemory(delegate, state)

  @SuppressWarnings(
    Array("scalafix:GemstoneNoExplicitNew.explicit-new")
  )
  object State {
    def apply(initialAddress: GuestAddress = GuestAddress(0x700000000000L)): State =
      new State(initialAddress)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneNoExplicitNew.explicit-new"))
  final class State(initialAddress: GuestAddress = GuestAddress(0x700000000000L)) {
    private[memory] val allocations = new TreeMap[Long, Array[Byte]]()
    private[memory] var nextAddress = initialAddress.value
  }
}

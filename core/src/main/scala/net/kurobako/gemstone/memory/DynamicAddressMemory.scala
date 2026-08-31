package net.kurobako.gemstone.memory

import java.util.TreeMap

/** AddressMemory overlay for device-side allocations such as CUDA's malloc/free.
  *
  * Device-side temporary storage is private to an emulated dispatch. Keeping it in this overlay avoids pretending that
  * it belongs to one of the host-visible allocations supplied by the RPC caller.
  */
final class DynamicAddressMemory(
    delegate: AddressMemory,
    state: DynamicAddressMemory.State = DynamicAddressMemory.State()
) extends AddressMemory {
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
    dynamicRange(address, 1).fold(delegate.readInt8(address)) { case (bytes, offset) => Right(bytes(offset)) }

  override def readInt32(address: GuestAddress): Either[MemoryError, Int] =
    dynamicRange(address, 4).fold(delegate.readInt32(address)) { case (bytes, offset) =>
      Right(
        (bytes(offset) & 0xff) |
          ((bytes(offset + 1) & 0xff) << 8) |
          ((bytes(offset + 2) & 0xff) << 16) |
          ((bytes(offset + 3) & 0xff) << 24)
      )
    }

  override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
    dynamicRange(address, 1).fold(delegate.writeInt8(address, value)) { case (bytes, offset) =>
      bytes(offset) = value
      Right(())
    }

  override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
    dynamicRange(address, 4).fold(delegate.writeInt32(address, value)) { case (bytes, offset) =>
      bytes(offset) = value.toByte
      bytes(offset + 1) = (value >>> 8).toByte
      bytes(offset + 2) = (value >>> 16).toByte
      bytes(offset + 3) = (value >>> 24).toByte
      Right(())
    }

  private def dynamicRange(address: GuestAddress, size: Int): Option[(Array[Byte], Int)] = state.synchronized {
    Option(allocations.floorEntry(address.value)).flatMap { entry =>
      val bytes    = entry.getValue
      val relative = address.value - entry.getKey
      Option.when(relative >= 0L && relative <= bytes.length.toLong - size.toLong)(bytes -> relative.toInt)
    }
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

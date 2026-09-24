package net.kurobako.gemstone.memory

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.concurrent.withLock

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.TreeMap
import java.util.concurrent.locks.ReentrantLock

/** AddressMemory overlay for device-side allocations such as CUDA's malloc/free.
  *
  * Device-side temporary storage is private to an emulated dispatch. Keeping it in this overlay avoids pretending that
  * it belongs to one of the host-visible allocations supplied by the RPC caller.
  */
final class DynamicAddressMemory(
    delegate: AddressMemory,
    state: DynamicAddressMemory.State = DynamicAddressMemory.State()
) extends DeviceAllocationMemory {
  override def fence(): Either[MemoryError, Unit] = delegate.fence()
  private val mutableAllocations                  = state.mutableAllocations

  override def allocate(size: DeviceAllocationByteSize): Either[MemoryError, GuestAddress] = allocate(size, Vec.empty)

  def allocate(size: DeviceAllocationByteSize, initial: Vec[Byte]): Either[MemoryError, GuestAddress] =
    if (initial.length > size.value)
      Left(MemoryError.AllocationInitializerTooLarge(size, initial.length))
    else state.lock.withLock {
      val byteCount       = size.value
      val span            = (byteCount.toLong + 255L) & -256L
      val start           = state.mutableNextAddress
      val mutableContents = Array.ofDim[Byte](byteCount)
      val _               = initial.copyToArray(mutableContents, 0, initial.length)
      try {
        state.mutableNextAddress = GuestAddress(Math.addExact(start.value, span))
        val _ = mutableAllocations.put(start.value, mutableContents)
        Right(start)
      } catch {
        case _: ArithmeticException => Left(MemoryError.AddressSpaceExhausted(size))
      }
    }

  override def free(address: GuestAddress): Boolean = state.lock.withLock {
    val found = mutableAllocations.containsKey(address.value)
    if (found) { val _ = mutableAllocations.remove(address.value) }
    found
  }

  override def prepareReads(ranges: Vec[(GuestAddress, Int :| Positive0)]): Either[MemoryError, Unit] = {
    val external = state.lock.withLock {
      ranges.flatMap { case (address, size) =>
        if (dynamicRange(address, size).nonEmpty) Vec.empty[(GuestAddress, Int :| Positive0)]
        else if (!overlapsDynamic(address, size)) Vec((address, size))
        else Vec.from((0 until size).collect {
          case offset if dynamicRange(address.offset(offset.toLong), 1).isEmpty =>
            (address.offset(offset.toLong), 1.refineUnsafe[Positive0])
        })
      }
    }
    delegate.prepareReads(external)
  }

  override def validateRange(address: GuestAddress, size: Int :| Positive0): Either[MemoryError, Unit] = {
    val local = state.lock.withLock {
      if (dynamicRange(address, size).nonEmpty) Some(Right(()))
      else if (overlapsDynamic(address, size)) Some(super.validateRange(address, size))
      else None
    }
    local.getOrElse(delegate.validateRange(address, size))
  }

  override def readInt8(address: GuestAddress): Either[MemoryError, Byte] =
    state.lock.withLock {
      dynamicRange(address, 1).map { case (bytes, offset) => Right(bytes(offset)) }
    }.getOrElse(delegate.readInt8(address))

  override def readInt32(address: GuestAddress): Either[MemoryError, Int] =
    state.lock.withLock {
      dynamicRange(address, 4).map { case (bytes, offset) =>
        Right(LittleEndian.decodeInt32(bytes, offset))
      }
        .orElse(
          Option.when(overlapsDynamic(address, 4))(
            super.readLittleEndian(address, MemoryValueByteWidth(4)).map(_.toInt)
          )
        )
    }.getOrElse(delegate.readInt32(address))

  override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
    state.lock.withLock {
      dynamicRange(address, 1) match {
        case Some((bytes, offset)) =>
          bytes(offset) = value
          Some(Right(()))
        case None => None
      }
    }.getOrElse(delegate.writeInt8(address, value))

  override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
    state.lock.withLock {
      dynamicRange(address, 4) match {
        case Some((bytes, offset)) =>
          LittleEndian.write(value.toLong, bytes, offset, ScalarByteWidth(4))
          Some(Right(()))
        case None =>
          if (overlapsDynamic(address, 4))
            Some(super.writeLittleEndian(address, value.toLong, MemoryValueByteWidth(4)))
          else None
      }
    }.getOrElse(delegate.writeInt32(address, value))

  override def readBytes(address: GuestAddress, size: Int :| Positive0): Either[MemoryError, Vec[Byte]] =
    state.lock.withLock {
      dynamicRange(address, size).map { case (bytes, offset) =>
        Right(Vec.unsafeFromArray(java.util.Arrays.copyOfRange(bytes, offset, offset + size)))
      }.orElse(Option.when(overlapsDynamic(address, size))(super.readBytes(address, size)))
    }.getOrElse(delegate.readBytes(address, size))

  override def writeBytes(address: GuestAddress, bytes: Vec[Byte]): Either[MemoryError, Unit] =
    state.lock.withLock {
      dynamicRange(address, bytes.nonNegativeSize) match {
        case Some((destination, offset)) =>
          val _ = bytes.copyToArray(destination, offset, bytes.size)
          Some(Right(()))
        case None =>
          if (overlapsDynamic(address, bytes.nonNegativeSize)) Some(super.writeBytes(address, bytes))
          else None
      }
    }.getOrElse(delegate.writeBytes(address, bytes))

  override def readLittleEndian(address: GuestAddress, size: MemoryValueByteWidth): Either[MemoryError, Long] =
    state.lock.withLock {
      dynamicRange(address, size.value).map { case (bytes, offset) =>
        Right(LittleEndian.decode(bytes, offset, size.scalarByteWidth))
      }.orElse(
        Option.when(overlapsDynamic(address, size.value))(super.readLittleEndian(address, size))
      )
    }.getOrElse(delegate.readLittleEndian(address, size))

  override def writeLittleEndian(
      address: GuestAddress,
      value: Long,
      size: MemoryValueByteWidth
  ): Either[MemoryError, Unit] =
    state.lock.withLock {
      dynamicRange(address, size.value) match {
        case Some((bytes, offset)) =>
          LittleEndian.write(value, bytes, offset, size.scalarByteWidth)
          Some(Right(()))
        case None =>
          if (overlapsDynamic(address, size.value))
            Some(super.writeLittleEndian(address, value, size))
          else None
      }
    }.getOrElse(delegate.writeLittleEndian(address, value, size))

  override def atomicModifyLittleEndian(
      address: GuestAddress,
      size: MemoryValueByteWidth
  )(update: Long => Long): Either[MemoryError, Long] =
    state.lock.withLock {
      dynamicRange(address, size.value) match {
        case Some((bytes, offset)) =>
          val width    = size.scalarByteWidth
          val previous = LittleEndian.decode(bytes, offset, width)
          LittleEndian.write(update(previous), bytes, offset, width)
          Some(Right(previous))
        case None =>
          if (overlapsDynamic(address, size.value))
            Some(super.atomicModifyLittleEndian(address, size)(update))
          else None
      }
    }.getOrElse(delegate.atomicModifyLittleEndian(address, size)(update))

  /** Called while holding State's lock. Mixed ranges retain byte-wise overlay routing; wholly delegated words reach the
    * delegate as one transaction even while unrelated device allocations exist.
    */
  private def overlapsDynamic(address: GuestAddress, size: Int): Boolean = {
    val previous = Option(mutableAllocations.floorEntry(address.value))
    val next     = Option(mutableAllocations.ceilingEntry(address.value))
    size > 0 &&
    (previous.exists(entry =>
      java.lang.Long.compareUnsigned(address.value - entry.getKey, entry.getValue.length.toLong) < 0
    ) || next.exists(entry => java.lang.Long.compareUnsigned(entry.getKey - address.value, size.toLong) < 0))
  }

  /** Caller holds State's lock for the complete lookup and access. */
  private def dynamicRange(
      address: GuestAddress,
      size: Int
  ): Option[(Array[Byte], Int)] =
    Option(mutableAllocations.floorEntry(address.value)).flatMap { entry =>
      val bytes    = entry.getValue
      val relative = address.value - entry.getKey
      Option.when(
        size >= 0 && size <= bytes.length &&
          java.lang.Long.compareUnsigned(relative, bytes.length.toLong - size.toLong) <= 0
      )(bytes -> relative.toInt)
    }
}

object DynamicAddressMemory {
  final class State(initialAddress: GuestAddress = GuestAddress(0x700000000000L)) {
    private[memory] val lock = ReentrantLock()
    // java.util.TreeMap accepts the unsigned comparator required by the 64-bit device address domain.
    private[memory] val mutableAllocations =
      TreeMap[Long, Array[Byte]]((left, right) => java.lang.Long.compareUnsigned(left, right))
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private[memory] var mutableNextAddress: GuestAddress = initialAddress
  }
}

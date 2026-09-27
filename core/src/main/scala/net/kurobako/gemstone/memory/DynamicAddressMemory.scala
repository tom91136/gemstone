package net.kurobako.gemstone.memory

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.NonNegative

import net.kurobako.gemstone.binary.Widths.*

import net.kurobako.gemstone.Constraint.Positive
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.concurrent.withLock

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

  override def allocate(size: Positive[Int]): Either[MemoryError, GuestAddress] = allocate(size, Vec.empty)

  def allocate(size: Positive[Int], initial: Vec[Byte]): Either[MemoryError, GuestAddress] =
    if (initial.length > size.raw)
      Left(MemoryError.AllocationInitializerTooLarge(size, initial.length))
    else state.lock.withLock {
      val byteCount       = size.raw
      val span            = (byteCount.toLong + 255L) & -256L
      val start           = state.mutableNextAddress
      val mutableContents = Array.ofDim[Byte](byteCount)
      val _               = initial.copyToArray(mutableContents, 0, initial.length)
      try {
        state.mutableNextAddress = GuestAddress(Math.addExact(start.raw, span))
        val _ = mutableAllocations.put(start.raw, mutableContents)
        Right(start)
      } catch {
        case _: ArithmeticException => Left(MemoryError.AddressSpaceExhausted(size))
      }
    }

  override def free(address: GuestAddress): Boolean = state.lock.withLock {
    val found = mutableAllocations.containsKey(address.raw)
    if (found) { val _ = mutableAllocations.remove(address.raw) }
    found
  }

  override def prepareReads(ranges: Vec[(GuestAddress, NonNegative[Int])]): Either[MemoryError, Unit] = {
    val external = state.lock.withLock {
      ranges.flatMap { case (address, size) =>
        if (dynamicRange(address, size.raw).nonEmpty) Vec.empty[(GuestAddress, NonNegative[Int])]
        else if (!overlapsDynamic(address, size.raw)) Vec((address, size))
        else Vec.from((0 until size.raw).collect {
          case offset if dynamicRange(address.offset(offset.toLong), 1).isEmpty =>
            (address.offset(offset.toLong), 1: NonNegative[Int])
        })
      }
    }
    delegate.prepareReads(external)
  }

  override def validateRange(address: GuestAddress, size: NonNegative[Int]): Either[MemoryError, Unit] = {
    val local = state.lock.withLock {
      if (dynamicRange(address, size.raw).nonEmpty) Some(Right(()))
      else if (overlapsDynamic(address, size.raw)) Some(super.validateRange(address, size))
      else None
    }
    local.getOrElse(delegate.validateRange(address, size))
  }

  override def readInt8(address: GuestAddress): Either[MemoryError, Byte] =
    route(address, 1)((bytes, offset) => bytes(offset), None, delegate.readInt8(address))

  override def readInt32(address: GuestAddress): Either[MemoryError, Int] =
    route(address, 4)(
      (bytes, offset) => LittleEndian.decodeInt32(bytes, offset),
      Some(super.readLittleEndian(address, 4).map(_.toInt)),
      delegate.readInt32(address)
    )

  override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
    route(address, 1)((bytes, offset) => bytes(offset) = value, None, delegate.writeInt8(address, value))

  override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
    route(address, 4)(
      (bytes, offset) => LittleEndian.write(value.toLong, bytes, offset, 4),
      Some(super.writeLittleEndian(address, value.toLong, 4)),
      delegate.writeInt32(address, value)
    )

  override def readBytes(address: GuestAddress, size: NonNegative[Int]): Either[MemoryError, Vec[Byte]] =
    route(address, size.raw)(
      (bytes, offset) => Vec.unsafeFromArray(java.util.Arrays.copyOfRange(bytes, offset, offset + size.raw)),
      Some(super.readBytes(address, size)),
      delegate.readBytes(address, size)
    )

  override def writeBytes(address: GuestAddress, bytes: Vec[Byte]): Either[MemoryError, Unit] =
    route(address, bytes.nonNegativeSize.raw)(
      (destination, offset) => { val _ = bytes.copyToArray(destination, offset, bytes.size) },
      Some(super.writeBytes(address, bytes)),
      delegate.writeBytes(address, bytes)
    )

  override def readLittleEndian(address: GuestAddress, size: IntBetween[1, 8]): Either[MemoryError, Long] =
    route(address, size.raw)(
      (bytes, offset) => LittleEndian.decode(bytes, offset, size.scalarByteWidth),
      Some(super.readLittleEndian(address, size)),
      delegate.readLittleEndian(address, size)
    )

  override def writeLittleEndian(
      address: GuestAddress,
      value: Long,
      size: IntBetween[1, 8]
  ): Either[MemoryError, Unit] =
    route(address, size.raw)(
      (bytes, offset) => LittleEndian.write(value, bytes, offset, size.scalarByteWidth),
      Some(super.writeLittleEndian(address, value, size)),
      delegate.writeLittleEndian(address, value, size)
    )

  override def atomicModifyLittleEndian(
      address: GuestAddress,
      size: IntBetween[1, 8]
  )(update: Long => Long): Either[MemoryError, Long] =
    route(address, size.raw)(
      (bytes, offset) => LittleEndian.atomicModify(bytes, offset, size.scalarByteWidth)(update),
      Some(super.atomicModifyLittleEndian(address, size)(update)),
      delegate.atomicModifyLittleEndian(address, size)(update)
    )

  /** Routes a fixed-size access to the overlay, then to the delegate when the range is entirely external. A range that
    * straddles the overlay and the delegate is handled by `straddle`.
    */
  private def route[A](
      address: GuestAddress,
      size: Int
  )(
      overlay: (Array[Byte], Int) => A,
      straddle: => Option[Either[MemoryError, A]],
      delegated: => Either[MemoryError, A]
  ): Either[MemoryError, A] =
    state.lock.withLock {
      dynamicRange(address, size) match {
        case Some((bytes, offset)) => Some(Right(overlay(bytes, offset)))
        case None                  => if (overlapsDynamic(address, size)) straddle else None
      }
    }.getOrElse(delegated)

  /** Called while holding State's lock. Mixed ranges retain byte-wise overlay routing; wholly delegated words reach the
    * delegate as one transaction even while unrelated device allocations exist.
    */
  private def overlapsDynamic(address: GuestAddress, size: Int): Boolean = {
    val previous = Option(mutableAllocations.floorEntry(address.raw))
    val next     = Option(mutableAllocations.ceilingEntry(address.raw))
    size > 0 &&
    (previous.exists(entry =>
      java.lang.Long.compareUnsigned(address.raw - entry.getKey, entry.getValue.length.toLong) < 0
    ) || next.exists(entry => java.lang.Long.compareUnsigned(entry.getKey - address.raw, size.toLong) < 0))
  }

  /** Caller holds State's lock for the complete lookup and access. */
  private def dynamicRange(
      address: GuestAddress,
      size: Int
  ): Option[(Array[Byte], Int)] =
    Option(mutableAllocations.floorEntry(address.raw)).flatMap { entry =>
      val bytes    = entry.getValue
      val relative = address.raw - entry.getKey
      Option.when(AddressRanges.containsUnsigned(entry.getKey, bytes.length.toLong, address.raw, size.toLong))(
        bytes -> relative.toInt
      )
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

package net.kurobako.gemstone.benchmarks

import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

object BenchmarkMemory {
  private val Base   = 0x400000000000L
  private val Stride = 0x100000000L
}

/** Byte-array allocations numbered from 1, each in its own address window. Benchmark lanes touch disjoint bytes, so
  * plain accesses are unsynchronised; atomic updates share one lock.
  */
class BenchmarkMemory(sizes: Int*) extends AddressSpace {
  import BenchmarkMemory.*

  private val allocations = sizes.map(Array.ofDim[Byte](_)).toArray
  private val lock        = ReentrantLock()

  val ids: Vec[AllocationId] =
    Vec.indices(allocations.length.unsafe[Int >= 0]).map(index =>
      AllocationId((1L: Long > 0) + index.widen[Long >= 0])
    )

  /** Live backing bytes for setup and verification. */
  @SuppressWarnings(Array("scalafix:MutableExposure"))
  def array(id: AllocationId): Array[Byte] = allocations(id.raw.toInt - 1)

  def base(id: AllocationId): GuestAddress = GuestAddress(Base + (id.raw - 1L) * Stride)

  def getInt(id: AllocationId, offset: Int): Int                = LittleEndian.decodeInt32(array(id), offset)
  def putInt(id: AllocationId, offset: Int, value: Int): Unit   = LittleEndian.write(value.toLong, array(id), offset, 4)
  def getLong(id: AllocationId, offset: Int): Long              = LittleEndian.decode(array(id), offset, 8)
  def putLong(id: AllocationId, offset: Int, value: Long): Unit = LittleEndian.write(value, array(id), offset, 8)

  private def range(id: AllocationId, offset: Long, size: Long): Either[Fault, Array[Byte]] =
    Either.cond(
      id.raw <= allocations.length && ByteRange.contains(allocations(id.raw.toInt - 1).length, offset, size),
      allocations(id.raw.toInt - 1),
      Fault.Memory(s"benchmark access $offset + $size outside allocation ${id.raw}")
    )

  override def address(id: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
    range(id, offset, 0).map(_ => base(id).offset(offset))

  override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] = {
    val relative = address.raw - Base
    val index    = Math.floorDiv(relative, Stride)
    val offset   = relative - index * Stride
    Either.cond(
      index >= 0 && index < allocations.length &&
        ByteRange.contains(allocations(index.toInt).length, offset, size.toLong),
      AddressSpace.Location.Within(ids(index.toInt), offset.unsafe[Long >= 0]),
      Fault.Memory(s"address ${address.show} is outside benchmark allocations")
    )
  }

  override def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
    range(id, offset, size.toLong).map(bytes =>
      Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size))
    )

  override def write(id: AllocationId, offset: Long >= 0, values: Vec[Byte]): Either[Fault, Unit] =
    range(id, offset, values.size.toLong).map { bytes =>
      val _ = values.copyToArray(bytes, offset.toInt)
    }

  override def readValue(id: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8): Either[Fault, Long] =
    range(id, offset, size.toLong).map(LittleEndian.decode(_, offset.toInt, size.widen[Int in 0 ~ 8]))

  override def writeValue(id: AllocationId, offset: Long >= 0, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
    range(id, offset, size.toLong).map(LittleEndian.write(value, _, offset.toInt, size.widen[Int in 0 ~ 8]))

  override def atomicModifyValue(id: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)(
      update: Long => Long
  ): Either[Fault, Long] =
    range(id, offset, size.toLong).map(bytes =>
      lock.withLock(LittleEndian.atomicModify(bytes, offset.toInt, size.widen[Int in 0 ~ 8])(update))
    )
}

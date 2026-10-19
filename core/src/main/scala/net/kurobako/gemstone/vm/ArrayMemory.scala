package net.kurobako.gemstone.vm

import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import scala.collection.mutable

object ArrayMemory {

  /** Every allocation's base address and contents at one moment. */
  case class Snapshot(regions: Vec[(AllocationId, GuestAddress, Vec[Byte])])

  /** Windows sit above the NVIDIA local and shared apertures. */
  val Base: Long   = 0x400000000000L
  val Stride: Long = 0x100000000L
}

/** Reference address space with one byte array per allocation. Each allocation gets its own window in allocation order
  * unless placed explicitly. One lock guards every access so parallel workgroups observe coherent bytes.
  */
class ArrayMemory(initial: (AllocationId, Int)*) extends AddressSpace {
  private class Region(val bytes: Array[Byte], val base: Long)
  private val lock    = ReentrantLock()
  private val regions = mutable.LinkedHashMap.empty[AllocationId, Region]
  initial.foreach((id, size) => allocate(id, size))

  protected def exclusive[A](body: => A): A = lock.withLock(body)

  def allocate(id: AllocationId, size: Int): Unit =
    exclusive(allocate(id, size, GuestAddress(ArrayMemory.Base + regions.size * ArrayMemory.Stride)))

  def allocate(id: AllocationId, size: Int, base: GuestAddress): Unit =
    exclusive(regions.update(id, Region(Array.ofDim[Byte](size), base.raw)))

  def allocate(size: Long > 0): AllocationId = exclusive {
    val id = regions.keys.foldLeft(AllocationId(1L))((next, used) => if (used.raw >= next.raw) used.next else next)
    allocate(id, Math.toIntExact(size))
    id
  }

  def snapshot(): ArrayMemory.Snapshot = exclusive(ArrayMemory.Snapshot(Vec.from(regions.map((id, region) =>
    (id, GuestAddress(region.base), Vec.unsafeWrapArray(region.bytes.clone()))
  ))))

  /** Replaces every allocation with the snapshot's, so allocations made since are gone. */
  def restore(snapshot: ArrayMemory.Snapshot): Unit = exclusive {
    regions.clear()
    snapshot.regions.foreach((id, base, bytes) => regions.update(id, Region(bytes.toArray, base.raw)))
  }

  def rebase(id: AllocationId, base: GuestAddress): Unit =
    exclusive(regions.update(id, Region(regions(id).bytes, base.raw)))

  /** Live backing bytes; mutate only while no dispatch runs. */
  @SuppressWarnings(Array("scalafix:MutableExposure"))
  def array(id: AllocationId): Array[Byte] = exclusive(regions(id).bytes)

  def bytes(id: AllocationId): Vec[Byte] = exclusive(Vec.unsafeWrapArray(regions(id).bytes.clone()))

  def getInt(id: AllocationId, offset: Int >= 0): Int =
    exclusive(LittleEndian.decodeInt32(regions(id).bytes, offset))

  def putInt(id: AllocationId, offset: Int >= 0, value: Int): Unit =
    exclusive(LittleEndian.write(value.toLong, regions(id).bytes, offset, 4))

  private def region(id: AllocationId): Either[Fault, Region] =
    regions.get(id).toRight(Fault.Memory(s"allocation ${id.raw} is not registered"))

  private def range(id: AllocationId, offset: Long >= 0, size: Long): Either[Fault, (Array[Byte], Int)] =
    region(id).flatMap { value =>
      Either.cond(
        ByteRange.contains(value.bytes.length, offset, size),
        (value.bytes, offset.toInt),
        Fault.Memory(s"access $offset + $size exceeds allocation ${id.raw}")
      )
    }

  override def address(id: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] = exclusive {
    region(id).flatMap { value =>
      Either.cond(
        offset <= value.bytes.length,
        GuestAddress(value.base + offset),
        Fault.Memory(s"offset $offset exceeds allocation ${id.raw}")
      )
    }
  }

  override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] = exclusive {
    regions.collectFirst {
      case (id, value) if ByteRange.contains(value.bytes.length, address.raw - value.base, size.toLong) =>
        AddressSpace.Location.Within(id, (address.raw - value.base).unsafe[Long >= 0])
    }.toRight(Fault.Memory(s"address ${address.show} is outside every allocation"))
  }

  override def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] = exclusive {
    range(id, offset, size.toLong).map((bytes, start) =>
      Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(bytes, start, start + size))
    )
  }

  override def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] = exclusive {
    range(id, offset, bytes.size.toLong).map { (destination, start) =>
      val _ = bytes.copyToArray(destination, start)
    }
  }

  override def atomicModifyValue(id: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)(
      update: Long => Long
  ): Either[Fault, Long] = exclusive {
    range(id, offset, size.toLong).map((bytes, start) =>
      LittleEndian.atomicModify(bytes, start, size.widen[Int in 0 ~ 8])(update)
    )
  }
}

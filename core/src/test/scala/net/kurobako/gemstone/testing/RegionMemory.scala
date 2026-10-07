package net.kurobako.gemstone.testing

import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

object RegionMemory {

  /** Windows sit above the NVIDIA local and shared apertures. */
  val Base: Long   = 0x400000000000L
  val Stride: Long = 0x100000000L
}

/** In-memory address space with one byte array per allocation. Each allocation gets its own window in allocation order
  * unless placed explicitly. One lock guards every access so parallel workgroups observe coherent bytes.
  */
class RegionMemory(initial: (AllocationId, Int)*) extends AddressSpace {
  private case class Region(bytes: Array[Byte], base: Long)
  private val lock    = ReentrantLock()
  private val regions = mutable.LinkedHashMap.empty[AllocationId, Region]
  initial.foreach((id, size) => allocate(id, size))

  protected def exclusive[A](body: => A): A = lock.withLock(body)

  def allocate(id: AllocationId, size: Int): Unit =
    exclusive(allocate(id, size, GuestAddress(RegionMemory.Base + regions.size * RegionMemory.Stride)))

  def allocate(id: AllocationId, size: Int, base: GuestAddress): Unit =
    exclusive(regions.update(id, Region(Array.ofDim[Byte](size), base.raw)))

  def allocate(size: Long > 0): AllocationId = exclusive {
    val id = regions.keys.foldLeft(AllocationId(1L))((next, used) => if (used.raw >= next.raw) used.next else next)
    allocate(id, Math.toIntExact(size.raw))
    id
  }

  def allocate(bytes: Vec[Byte])(using munit.Location): AllocationId = {
    val id = allocate(bytes.size.toLong.refine[Long > 0].fold(error => munit.Assertions.fail(error.raw), identity))
    val _  = bytes.copyToArray(array(id))
    id
  }

  def rebase(id: AllocationId, base: GuestAddress): Unit =
    exclusive(regions.update(id, regions(id).copy(base = base.raw)))

  /** Live backing bytes; mutate only while no dispatch runs. */
  def array(id: AllocationId): Array[Byte] = exclusive(regions(id).bytes)

  def bytes(id: AllocationId): Vec[Byte] = exclusive(Vec.unsafeWrapArray(regions(id).bytes.clone()))

  def getInt(id: AllocationId, offset: Int >= 0): Int =
    exclusive(LittleEndian.decodeInt32(regions(id).bytes, offset.raw))

  def putInt(id: AllocationId, offset: Int >= 0, value: Int): Unit =
    exclusive(LittleEndian.write(value.toLong, regions(id).bytes, offset.raw, 4))

  private def region(id: AllocationId): Either[Fault, Region] =
    regions.get(id).toRight(Fault.Memory(s"test allocation ${id.raw} is not registered"))

  private def range(id: AllocationId, offset: Long >= 0, size: Long): Either[Fault, (Array[Byte], Int)] =
    region(id).flatMap { value =>
      Either.cond(
        ByteRange.contains(value.bytes.length, offset.raw, size),
        (value.bytes, offset.raw.toInt),
        Fault.Memory(s"test access $offset + $size exceeds allocation ${id.raw}")
      )
    }

  override def address(id: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] = exclusive {
    region(id).flatMap { value =>
      Either.cond(
        offset.raw <= value.bytes.length,
        GuestAddress(value.base + offset.raw),
        Fault.Memory(s"test offset $offset exceeds allocation ${id.raw}")
      )
    }
  }

  override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] = exclusive {
    regions.collectFirst {
      case (id, value) if ByteRange.contains(value.bytes.length, address.raw - value.base, size.raw.toLong) =>
        AddressSpace.Location.Within(id, (address.raw - value.base).unsafe[Long >= 0])
    }.toRight(Fault.Memory(s"address ${address.show} is outside test allocations"))
  }

  override def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] = exclusive {
    range(id, offset, size.raw.toLong).map((bytes, start) =>
      Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(bytes, start, start + size.raw))
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
    range(id, offset, size.raw.toLong).map { (bytes, start) =>
      val width    = size.widen[Int in 0 ~ 8]
      val previous = LittleEndian.decode(bytes, start, width)
      LittleEndian.write(update(previous), bytes, start, width)
      previous
    }
  }
}

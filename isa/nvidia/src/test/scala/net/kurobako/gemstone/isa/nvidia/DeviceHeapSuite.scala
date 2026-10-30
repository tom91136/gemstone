package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class DeviceHeapSuite extends Suite {
  private def allocate(heap: DeviceHeap, size: Int > 0)(using munit.Location): GuestAddress =
    heap.allocate(size).fold(fault => fail(fault.message), identity)

  test("blocks are aligned, private and released by free") {
    val heap  = DeviceHeap(RegionMemory())
    val first = allocate(heap, 8)
    val next  = allocate(heap, 1)
    assertEquals(first.raw, DeviceHeap.Base)
    assertEquals(next.raw, DeviceHeap.Base + 256L)
    assertEquals(heap.writeValue(first, 0x1122334455667788L, 8), Right(()))
    assertEquals(heap.readValue(first.offset(4), 4), Right(0x11223344L))
    assertEquals(heap.atomicModifyValue(first, 4)(_ + 1L), Right(0x55667788L))
    assertEquals(heap.read(first, 8).map(_.head), Right(0x89.toByte))
    assert(heap.locate(first, 8).isRight)
    assert(heap.free(first))
    assert(!heap.free(first))
    assert(heap.readValue(first, 4).isLeft)
  }

  test("alternating accesses to neighbouring blocks each reach their own block") {
    val heap   = DeviceHeap(RegionMemory())
    val first  = allocate(heap, 8)
    val second = allocate(heap, 8)
    assertEquals(heap.writeValue(first, 1L, 8), Right(()))
    assertEquals(heap.writeValue(second, 2L, 8), Right(()))
    assertEquals(heap.readValue(first, 8), Right(1L))
    assertEquals(heap.readValue(second, 8), Right(2L))
    assertEquals(heap.readValue(first, 8), Right(1L))
    assert(heap.writeValue(first.offset(4), 0L, 8).isLeft)
    assert(heap.free(second))
    assert(heap.readValue(second, 8).isLeft)
    assertEquals(heap.readValue(first, 8), Right(1L))
  }

  test("accesses outside heap blocks reach the backing address space") {
    val backing = RegionMemory(AllocationId(1) -> 8)
    val heap    = DeviceHeap(backing)
    val _       = allocate(heap, 16)
    val outside = GuestAddress(RegionMemory.Base)
    assertEquals(heap.writeValue(outside, 42L, 4), Right(()))
    assertEquals(backing.getInt(AllocationId(1), 0), 42)
    val reads = AddressSpace.ReadRangeBuffer()
    reads.add(outside, 4)
    reads.add(GuestAddress(DeviceHeap.Base), 4)
    assertEquals(heap.prepareReads(reads), Right(()))
    assertEquals(
      heap.writeSystemBatch(Vec((outside, 7L, 4: Int in 1 ~ 8), (GuestAddress(DeviceHeap.Base), 9L, 4: Int in 1 ~ 8))),
      Right(())
    )
    assertEquals(backing.getInt(AllocationId(1), 0), 7)
    assertEquals(heap.readSystemValue(GuestAddress(DeviceHeap.Base), 4), Right(9L))
  }

  test("an access straddling a block boundary faults without touching either side") {
    val heap  = DeviceHeap(RegionMemory())
    val block = allocate(heap, 4)
    assert(heap.writeValue(block.offset(2), 1L, 4).isLeft)
    assert(heap.writeValue(block.offset(-2), 1L, 4).isLeft)
    assertEquals(heap.readValue(block, 4), Right(0L))
  }

  test("allocation checks the unsigned end of the address space before publishing a block") {
    val heap = DeviceHeap(RegionMemory(), GuestAddress(-128L))
    assert(heap.allocate(256).isLeft)
    val last = allocate(heap, 128)
    assertEquals(last.raw, -128L)
    assertEquals(heap.writeValue(last.offset(127), 42L, 1), Right(()))
    assertEquals(heap.readValue(last.offset(127), 1), Right(42L))
    assert(heap.allocate(1).isLeft)
  }

  test("the final guest address byte can be allocated once") {
    val heap = DeviceHeap(RegionMemory(), GuestAddress(-1L))
    val last = allocate(heap, 1)
    assertEquals(last.raw, -1L)
    assertEquals(heap.writeValue(last, 42L, 1), Right(()))
    assertEquals(heap.readValue(last, 1), Right(42L))
    assert(heap.allocate(1).isLeft)
  }

  test("blocks can cross the signed Long boundary") {
    val heap  = DeviceHeap(RegionMemory(), GuestAddress(Long.MaxValue - 127L))
    val first = allocate(heap, 256)
    assertEquals(heap.writeValue(first.offset(128), 42L, 1), Right(()))
    assertEquals(heap.readValue(GuestAddress(Long.MinValue), 1), Right(42L))
    assertEquals(heap.read(first, 256).map(_(128)), Right(42.toByte))
    assertEquals(allocate(heap, 1).raw, first.raw + 256L)
  }

  test("wide reads return snapshots and rejected writes leave the block unchanged") {
    val heap  = DeviceHeap(RegionMemory())
    val block = allocate(heap, 16)
    val bytes = Vec.from((1 to 16).map(_.toByte))
    assertEquals(heap.write(block, bytes), Right(()))
    val snapshot = heap.read(block, 16)
    val deferred = heap.readDeferred(block, 16)
    assertEquals(snapshot, Right(bytes))
    assert(heap.write(block.offset(1), Vec.fill(16)(0.toByte)).isLeft)
    assertEquals(heap.read(block, 16), Right(bytes))
    assertEquals(heap.write(block, Vec.fill(16)(0.toByte)), Right(()))
    assertEquals(snapshot, Right(bytes))
    assertEquals(deferred, Right(bytes))
  }
}

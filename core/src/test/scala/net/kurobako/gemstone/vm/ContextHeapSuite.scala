package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*

class ContextHeapSuite extends Suite {
  private def heapOf(memory: RegionMemory, size: Long): (HeapRegion, AllocationId) = {
    val id = memory.allocate(size.refine[Long > 0].fold(error => fail(error.raw), identity))
    (HeapRegion(memory.address(id, 0L).fold(fault => fail(fault.message), identity), size), id)
  }

  private def word(memory: RegionMemory, id: AllocationId)(using munit.Location): Long =
    memory.readValue(id, 0L, 8: Int in 1 ~ 8).fold(fault => fail(fault.message), identity)

  private def allocate(memory: AddressSpace, region: HeapRegion, size: Long)(using munit.Location): Long =
    ContextHeap.allocate(memory, region, size).fold(fault => fail(fault.message), identity)

  private def free(memory: AddressSpace, region: HeapRegion, address: Long)(using munit.Location): Unit =
    assertEquals(ContextHeap.free(memory, region, address), Right(()))

  test("blocks are aligned, disjoint and inside the region") {
    val memory      = RegionMemory()
    val (region, _) = heapOf(memory, 4096L)
    val blocks      = Vec.tabulate(10)(index => allocate(memory, region, 1L + index * 7L))
    assert(blocks.forall(block => block % 16L == 0L && block >= region.base.raw && block < region.base.raw + 4096L))
    assertEquals(blocks.toSet.size, 10)
    val sorted = blocks.toList.sorted
    assert(sorted.zip(sorted.tail).forall((a, b) => b - a >= 32L))
  }

  test("an exhausted heap returns null and recovers once blocks are freed") {
    val memory      = RegionMemory()
    val (region, _) = heapOf(memory, 1024L)
    val first       = allocate(memory, region, 400L)
    val second      = allocate(memory, region, 400L)
    assertEquals(allocate(memory, region, 400L), 0L)
    assertEquals(allocate(memory, region, 2048L), 0L)
    assertEquals(allocate(memory, region, 0L), 0L)
    free(memory, region, first)
    free(memory, region, second)
    assertNotEquals(allocate(memory, region, 900L), 0L)
  }

  test("adjacent free blocks coalesce and freed blocks are reused") {
    val memory      = RegionMemory()
    val (region, _) = heapOf(memory, 4096L)
    val blocks      = Vec.tabulate(8)(_ => allocate(memory, region, 100L))
    blocks.foreach(free(memory, region, _))
    assertNotEquals(allocate(memory, region, 3000L), 0L)
    val again = allocate(memory, region, 100L)
    free(memory, region, again)
    assertEquals(allocate(memory, region, 100L), again)
  }

  test("the middle of a run freed out of order still coalesces") {
    val memory      = RegionMemory()
    val (region, _) = heapOf(memory, 2048L)
    val a           = allocate(memory, region, 400L)
    val b           = allocate(memory, region, 400L)
    val c           = allocate(memory, region, 400L)
    val _           = allocate(memory, region, 400L)
    free(memory, region, a)
    free(memory, region, c)
    free(memory, region, b)
    assertEquals(allocate(memory, region, 1100L), a)
  }

  test("freeing null, an unknown address or a freed block changes nothing") {
    val memory      = RegionMemory()
    val (region, _) = heapOf(memory, 1024L)
    val block       = allocate(memory, region, 100L)
    free(memory, region, 0L)
    free(memory, region, block + 8L)
    free(memory, region, region.base.raw + 4096L)
    free(memory, region, block)
    free(memory, region, block)
    assertEquals(allocate(memory, region, 100L), block)
  }

  test("the heap is untouched until first used") {
    val memory       = RegionMemory()
    val (region, id) = heapOf(memory, 1024L)
    assertEquals(word(memory, id), 0L)
    val _ = allocate(memory, region, 8L)
    assertNotEquals(word(memory, id), 0L)
  }
}

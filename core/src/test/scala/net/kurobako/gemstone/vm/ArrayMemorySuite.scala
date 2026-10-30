package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.Suite

class ArrayMemorySuite extends Suite {
  test("restoring a snapshot brings back its contents and drops allocations made since") {
    val memory   = ArrayMemory()
    val first    = memory.allocate(4L)
    val _        = memory.write(first, 0L, Vec[Byte](1, 2, 3, 4))
    val snapshot = memory.snapshot()
    val _        = memory.write(first, 0L, Vec[Byte](9))
    val later    = memory.allocate(4L)
    memory.restore(snapshot)
    assertEquals(memory.bytes(first), Vec[Byte](1, 2, 3, 4))
    assert(memory.read(later, 0L, 1).isLeft)
    val _ = memory.write(first, 0L, Vec[Byte](7))
    assertEquals(snapshot.regions.head._3, Vec[Byte](1, 2, 3, 4))
    assertEquals(memory.address(first, 0L), Right(GuestAddress(ArrayMemory.Base)))
  }
}

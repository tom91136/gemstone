package net.kurobako.gemstone.memory

class CheckedMemorySuite extends munit.FunSuite {
  test("integer access is little endian and bounds checked") {
    val memory = ArrayCheckedMemory.allocate(8, provenance = "test allocation")
    val start  = memory.allocation.start

    assertEquals(memory.writeInt32(start, 0x12345678), Right(()))
    assertEquals(memory.readInt32(start), Right(0x12345678))
    assert(memory.readInt32(start.offset(5L)).isLeft)
  }

  test("freed memory is quarantined") {
    val memory = ArrayCheckedMemory.allocate(4, provenance = "freed allocation")
    val start  = memory.allocation.start
    memory.free()

    assert(memory.readInt32(start).left.exists(_.isInstanceOf[MemoryError.UseAfterFree]))
  }
}

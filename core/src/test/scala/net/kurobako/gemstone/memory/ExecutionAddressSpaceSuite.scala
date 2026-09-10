package net.kurobako.gemstone.memory
import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*

final class ExecutionAddressSpaceSuite extends munit.FunSuite {
  test("scalar address operations reach value methods once and validate the bound view") {
    val allocation = AllocationId.fromLong(7).toOption.get
    final class Memory extends ExecutionMemory {
      val bytes                                           = Array.ofDim[Byte](24)
      var reads                                           = 0
      var writes                                          = 0
      def read(id: AllocationId, offset: Long, size: Int) =
        Left(ExecutionFailure.Failed("unexpected byte collection read"))
      def write(id: AllocationId, offset: Long, value: Col[Byte]) =
        Left(ExecutionFailure.Failed("unexpected byte collection write"))
      override def readValue(id: AllocationId, offset: Long, size: Int) = {
        reads += 1; assertEquals(id, allocation); Right(LittleEndian.decode(bytes, offset.toInt, size))
      }
      override def writeValue(id: AllocationId, offset: Long, value: Long, size: Int) = {
        writes += 1; assertEquals(id, allocation); LittleEndian.write(value, bytes, offset.toInt, size); Right(())
      }
      def recordPointerRelocation(value: PointerRelocation): Unit = ()
    }
    val memory  = Memory()
    val base    = GuestAddress(0x1000L)
    val space   = ExecutionAddressSpace.create(memory, Col(AddressBinding(base, allocation, 4, 12))).toOption.get
    val overlay = DynamicAddressMemory(space)
    assert(overlay.allocate(8) != 0L)
    val value = 0x8877665544332211L
    for (width <- 1 to 8) {
      assertEquals(overlay.writeLittleEndian(base.offset(1), value, width), Right(()))
      val expected = if (width == 8) value else value & ((1L << (width * 8)) - 1)
      assertEquals(overlay.readLittleEndian(base.offset(1), width), Right(expected))
    }
    assertEquals(memory.reads, 8); assertEquals(memory.writes, 8)
    assertEquals(space.readInt8(base.offset(1)), Right(0x11.toByte))
    assertEquals(space.readInt32(base.offset(1)), Right(0x44332211))
    assertEquals(space.writeInt8(base, 0x7f.toByte), Right(()))
    assertEquals(space.writeInt32(base, 0x12345678), Right(()))
    val snapshot = memory.bytes.clone(); val reads = memory.reads; val writes = memory.writes
    assert(space.readLittleEndian(base.offset(5), 8).isLeft)
    assert(space.writeLittleEndian(base.offset(5), 0L, 8).isLeft)
    assertEquals(memory.reads, reads); assertEquals(memory.writes, writes)
    assert(memory.bytes.sameElements(snapshot))
  }
}

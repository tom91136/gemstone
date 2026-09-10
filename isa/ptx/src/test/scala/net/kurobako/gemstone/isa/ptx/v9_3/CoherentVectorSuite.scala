package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.*

final class CoherentVectorSuite extends munit.FunSuite {
  private val geometry = LaunchGeometry.create(Dimensions.One, Dimensions.One).toOption.get
  private val packed   = (668L << 32) | 101L

  private class InterleavingMemory extends AddressMemory {
    val backing                                   = ArrayCheckedMemory.allocate(32, GuestAddress(0x10000000000L))
    val base                                      = backing.allocation.start
    var observed                                  = Vector.empty[Long]
    var changeAfterRead                           = false
    override def readInt8(address: GuestAddress)  = backing.readInt8(address)
    override def readInt32(address: GuestAddress) = backing.readInt32(address)
    override def writeInt8(address: GuestAddress, value: Byte)      = backing.writeInt8(address, value)
    override def writeInt32(address: GuestAddress, value: Int)      = backing.writeInt32(address, value)
    override def readLittleEndian(address: GuestAddress, size: Int) = {
      val result = backing.readLittleEndian(address, size)
      if (changeAfterRead && address == base) {
        backing.writeLittleEndian(base, (1234L << 32) | 100L, 8).toOption.get
      }
      result
    }
    override def writeLittleEndian(address: GuestAddress, value: Long, size: Int) = {
      val result = backing.writeLittleEndian(address, value, size)
      observed :+= backing.readLittleEndian(base, 8).toOption.get
      result
    }
  }

  test("packed release store publishes the complete scan status and prefix") {
    val memory = InterleavingMemory()
    val kernel = ISA.compile(""".version 8.0
      .target sm_80
      .address_size 64
      .visible .entry publish(.param .u64 output) {
        .reg .u64 %rd;
        .reg .u32 %r<2>;
        ld.param.u64 %rd, [output];
        mov.u32 %r0, 101;
        mov.u32 %r1, 668;
        st.release.gpu.v2.u32 [%rd], {%r0, %r1};
        ret;
      }""").toOption.get
    assert(Interpreter.execute(
      kernel,
      geometry,
      Map("output" -> Argument(memory.base.value)),
      memory,
      captureTrace = false
    ).isRight)
    assert(memory.observed.nonEmpty)
    assert(memory.observed.forall(_ == packed), memory.observed.toString)
  }

  test("packed acquire load observes one descriptor despite a following publication") {
    val memory = InterleavingMemory()
    memory.backing.writeLittleEndian(memory.base, packed, 8).toOption.get
    memory.changeAfterRead = true
    val kernel = ISA.compile(""".version 8.0
      .target sm_80
      .address_size 64
      .visible .entry observe(.param .u64 input) {
        .reg .u64 %rd;
        .reg .u32 %r<2>;
        ld.param.u64 %rd, [input];
        ld.acquire.gpu.v2.u32 {%r0, %r1}, [%rd];
        add.u64 %rd, %rd, 8;
        st.global.v2.u32 [%rd], {%r0, %r1};
        ret;
      }""").toOption.get
    assert(Interpreter.execute(
      kernel,
      geometry,
      Map("input" -> Argument(memory.base.value)),
      memory,
      captureTrace = false
    ).isRight)
    assertEquals(memory.backing.readLittleEndian(memory.base.offset(8), 8), Right(packed))
  }
}

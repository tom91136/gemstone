package net.kurobako.gemstone.isa.ptx.v9_3

import execution.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class CoherentVectorSuite extends munit.FunSuite {
  private val packed = (668L << 32) | 101L

  // Interleaving controls and observations are confined to this final test fixture.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class InterleavingMemory extends AddressMemory {
    val backing: ArrayCheckedMemory = ArrayCheckedMemory.allocate(32, GuestAddress(0x10000000000L))
    val base                        = backing.allocation.start
    val observed: ArrayBuffer[Long] = mutable.ArrayBuffer.empty[Long]
    var changeAfterRead             = false
    override def readInt8(address: GuestAddress): Either[MemoryError, Byte]               = backing.readInt8(address)
    override def readInt32(address: GuestAddress): Either[MemoryError, Int]               = backing.readInt32(address)
    override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
      backing.writeInt8(address, value)
    override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
      backing.writeInt32(address, value)
    override def readLittleEndian(address: GuestAddress, size: Int in 1 ~ 8): Either[MemoryError, Long] = {
      val result = backing.readLittleEndian(address, size)
      if (changeAfterRead && address == base) {
        backing.writeLittleEndian(base, (1234L << 32) | 100L, 8).toOption.get
      }
      result
    }
    override def writeLittleEndian(
        address: GuestAddress,
        value: Long,
        size: Int in 1 ~ 8
    ): Either[MemoryError, Unit] = {
      val result = backing.writeLittleEndian(address, value, size)
      observed += backing.readLittleEndian(base, 8).toOption.get
      result
    }
  }

  test("packed release store publishes the complete scan status and prefix") {
    val memory = InterleavingMemory()
    val kernel = compileKernel(""".version 8.0
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
}""")
    assert(execute(
      kernel,
      1,
      1,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.base.raw)),
      memory
    ).isRight)
    assert(memory.observed.nonEmpty)
    assert(memory.observed.forall(_ == packed), memory.observed.toString)
  }

  test("packed acquire load observes one descriptor despite a following publication") {
    val memory = InterleavingMemory()
    memory.backing.writeLittleEndian(memory.base, packed, 8).toOption.get
    memory.changeAfterRead = true
    val kernel = compileKernel(""".version 8.0
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
}""")
    assert(execute(
      kernel,
      1,
      1,
      Map(("input": NonEmpty[String]) -> Argument.fromLong(memory.base.raw)),
      memory
    ).isRight)
    assertEquals(memory.backing.readLittleEndian(memory.base.offset(8), 8), Right(packed))
  }
}

package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import VirtualMachine.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.*

import scala.collection.mutable

final class CoherentVectorSuite extends munit.FunSuite {
  private val geometry = LaunchGeometry.forLaunch(Dimensions.One, Dimensions.One).toOption.get
  private val packed   = (668L << 32) | 101L

  // Interleaving controls and observations are confined to this final test fixture.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private final class InterleavingMemory extends AddressMemory {
    val backing                                   = ArrayCheckedMemory.allocate(32, GuestAddress(0x10000000000L))
    val base                                      = backing.allocation.start
    val observed                                  = mutable.ArrayBuffer.empty[Long]
    var changeAfterRead                           = false
    override def readInt8(address: GuestAddress)  = backing.readInt8(address)
    override def readInt32(address: GuestAddress) = backing.readInt32(address)
    override def writeInt8(address: GuestAddress, value: Byte) = backing.writeInt8(address, value)
    override def writeInt32(address: GuestAddress, value: Int) = backing.writeInt32(address, value)
    override def readLittleEndian(address: GuestAddress, size: MemoryValueByteWidth) = {
      val result = backing.readLittleEndian(address, size)
      if (changeAfterRead && address == base) {
        backing.writeLittleEndian(base, (1234L << 32) | 100L, MemoryValueByteWidth(8)).toOption.get
      }
      result
    }
    override def writeLittleEndian(address: GuestAddress, value: Long, size: MemoryValueByteWidth) = {
      val result = backing.writeLittleEndian(address, value, size)
      observed += backing.readLittleEndian(base, MemoryValueByteWidth(8)).toOption.get
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
    assert(VirtualMachine.execute(
      kernel,
      geometry,
      Map(ParameterName("output") -> Argument(memory.base.value)),
      memory,
      captureTrace = false
    ).isRight)
    assert(memory.observed.nonEmpty)
    assert(memory.observed.forall(_ == packed), memory.observed.toString)
  }

  test("packed acquire load observes one descriptor despite a following publication") {
    val memory = InterleavingMemory()
    memory.backing.writeLittleEndian(memory.base, packed, MemoryValueByteWidth(8)).toOption.get
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
    assert(VirtualMachine.execute(
      kernel,
      geometry,
      Map(ParameterName("input") -> Argument(memory.base.value)),
      memory,
      captureTrace = false
    ).isRight)
    assertEquals(memory.backing.readLittleEndian(memory.base.offset(8), MemoryValueByteWidth(8)), Right(packed))
  }
}

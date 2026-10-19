package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.collection.immutable.ArraySeq
import scala.collection.mutable

class CoherentVectorSuite extends munit.FunSuite {
  private val packed = (668L << 32) | 101L
  private val scopes = Vec("cta", "gpu")
  private val spaces = Vec("", ".global")

  // Interleaving controls and observations are confined to this final test fixture.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class InterleavingMemory extends DeviceMemory(32, GuestAddress(0x10000000000L)) {
    val base                                = start
    val observed: mutable.ArrayBuffer[Long] = mutable.ArrayBuffer.empty[Long]
    var changeAfterRead                     = false
    override def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] = {
      val result = readLittleEndian(address, size)
      if (changeAfterRead && address == base) writeLittleEndian(base, (1234L << 32) | 100L, 8).toOption.get
      result
    }
    override def writeValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] = {
      val result = writeLittleEndian(address, value, size)
      observed += readLittleEndian(base, 8).toOption.get
      result
    }
  }

  for {
    scope <- scopes
    space <- spaces
  }
    test(s"relaxed $scope$space b128 descriptors retain two-word coherence") {
      class DescriptorMemory extends DeviceMemory(32, GuestAddress(0x10000000000L)) {
        val base                                              = start
        val publications: mutable.ArrayBuffer[ArraySeq[Byte]] = mutable.ArrayBuffer.empty[Vec[Byte]]
        val reads: mutable.ArrayBuffer[Int]                   = mutable.ArrayBuffer.empty[Int]
        override def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] = {
          reads += size
          val snapshot = readBytes(address, size)
          // A second descriptor is published immediately after taking the coherent snapshot.
          assertEquals(writeLittleEndian(base.offset(8), 999L, 8), Right(()))
          snapshot
        }
        override def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] = {
          val result = writeBytes(address, bytes)
          publications += readBytes(address, 16).toOption.get
          result
        }
      }
      val memory = DescriptorMemory()
      assertEquals(memory.writeLittleEndian(memory.base, 101L, 8), Right(()))
      assertEquals(memory.writeLittleEndian(memory.base.offset(8), 668L, 8), Right(()))
      val kernel = compileKernel(s""".version 8.6
        |.target sm_100
        |.address_size 64
        |.visible .entry descriptor(.param .u64 input) {
        | .reg .u64 %ptr;
        | .reg .b128 %descriptor;
        | ld.param.u64 %ptr,[input];
        | ld.relaxed.$scope$space.b128 %descriptor,[%ptr];
        | add.u64 %ptr,%ptr,16;
        | st.relaxed.$scope$space.b128 [%ptr],%descriptor;
        | ret;
        |}""".stripMargin)
      assertEquals(
        execute(kernel, 1, 1, Map(("input": NonEmpty[String]) -> Argument.fromLong(memory.base.raw)), memory),
        Right(())
      )
      val expected = LittleEndian.toVec(101L, 8) ++ LittleEndian.toVec(668L, 8)
      assertEquals(memory.reads.toVector, Vector(16))
      assertEquals(memory.publications.toVector, Vector(expected))
      assertEquals(memory.readBytes(memory.base.offset(16), 16), Right(expected))
    }

  for (instruction <- Vec("ld.relaxed.sys.b128 %descriptor,[%ptr];", "st.relaxed.sys.b128 [%ptr],%descriptor;"))
    test(s"system-scope b128 access remains unsupported: $instruction") {
      val result = VMCompiler.compile(s""".version 8.6
        |.target sm_100
        |.address_size 64
        |.visible .entry descriptor() {
        | .reg .u64 %ptr;
        | .reg .b128 %descriptor;
        | $instruction
        | ret;
        |}""".stripMargin)
      assert(
        result.left.exists(_.message.contains("b128 memory access requires a memory state space below system scope"))
      )
    }

  test("explicit global vectors do not use local addressing for low global addresses") {
    val memory = DeviceMemory(32)
    memory.writeInt32(memory.start, 101).toOption.get
    memory.writeInt32(memory.start.offset(4), 668).toOption.get
    val kernel = compileKernel(""".version 8.0
.target sm_80
.address_size 64
.visible .entry copy(.param .u64 input) {
.reg .u64 %rd;
.reg .u32 %r<2>;
ld.param.u64 %rd,[input];
ld.global.v2.u32 {%r0,%r1},[%rd];
add.u64 %rd,%rd,8;
st.global.v2.u32 [%rd],{%r0,%r1};
ret;
}""")
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("input": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(memory.readLittleEndian(memory.start.offset(8), 8), Right(packed))
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
    memory.writeLittleEndian(memory.base, packed, 8).toOption.get
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
    assertEquals(memory.readLittleEndian(memory.base.offset(8), 8), Right(packed))
  }
}

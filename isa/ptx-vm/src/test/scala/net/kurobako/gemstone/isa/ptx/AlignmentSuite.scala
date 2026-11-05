package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

/** NVIDIA GPUs fault a load, store or atomic whose address is not a multiple of its size, in every state space, with
  * `cudaErrorMisalignedAddress`; `%p` holds the 16-byte aligned base of the space the access names.
  */
class AlignmentSuite extends Suite {
  private def run(base: String, access: String, offset: Int): Either[Fault, Unit] = {
    val source = s""".version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry access(.param .u64 buffer) {
      |  .shared .align 16 .b8 shared[64];
      |  .local .align 16 .b8 local[64];
      |  .reg .u64 %p, %d;
      |  .reg .u32 %r<4>;
      |  .reg .u16 %h;
      |  $base
      |  mov.u32 %r0, 1;
      |  mov.u32 %r1, 2;
      |  mov.u32 %r2, 3;
      |  mov.u32 %r3, 4;
      |  mov.u64 %d, 5;
      |  $access
      |  ret;
      |}""".stripMargin
    val memory = DeviceMemory(64)
    execute(
      compileKernel(source.replace("OFFSET", offset.toString)),
      1,
      1,
      Map(("buffer": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
  }

  private val global  = "ld.param.u64 %p, [buffer];"
  private val shared  = "mov.u64 %p, shared;"
  private val generic = "cvta.shared.u64 %p, shared;"
  private val local   = "mov.u64 %p, local;"

  private val accesses = Vec(
    (global, "ld.global.u16 %h, [%p+OFFSET];", 2),
    (global, "ld.global.u32 %r0, [%p+OFFSET];", 4),
    (global, "ld.global.nc.u32 %r0, [%p+OFFSET];", 4),
    (global, "ld.global.u64 %d, [%p+OFFSET];", 8),
    (global, "ld.global.v2.u32 {%r0, %r1}, [%p+OFFSET];", 8),
    (global, "ld.global.v4.u32 {%r0, %r1, %r2, %r3}, [%p+OFFSET];", 16),
    (global, "ld.acquire.gpu.global.u32 %r0, [%p+OFFSET];", 4),
    (global, "st.global.u16 [%p+OFFSET], %h;", 2),
    (global, "st.global.u32 [%p+OFFSET], %r0;", 4),
    (global, "st.global.v2.u64 [%p+OFFSET], {%d, %d};", 16),
    (global, "atom.global.add.u32 %r0, [%p+OFFSET], 1;", 4),
    (global, "atom.global.cas.b64 %d, [%p+OFFSET], 0, 1;", 8),
    (global, "red.global.add.u64 [%p+OFFSET], 1;", 8),
    (global, "ld.u64 %d, [%p+OFFSET];", 8),
    (global, "atom.add.u32 %r0, [%p+OFFSET], 1;", 4),
    (shared, "ld.shared.u32 %r0, [%p+OFFSET];", 4),
    (shared, "st.shared.v2.u32 [%p+OFFSET], {%r0, %r1};", 8),
    (shared, "ld.shared.v4.u32 {%r0, %r1, %r2, %r3}, [%p+OFFSET];", 16),
    (shared, "atom.shared.add.u32 %r0, [%p+OFFSET], 1;", 4),
    (shared, "atom.shared.exch.b64 %d, [%p+OFFSET], 1;", 8),
    (generic, "st.u64 [%p+OFFSET], %d;", 8),
    (generic, "atom.add.u32 %r0, [%p+OFFSET], 1;", 4),
    (local, "ld.local.u64 %d, [%p+OFFSET];", 8),
    (local, "st.local.u32 [%p+OFFSET], %r0;", 4)
  )

  for ((base, access, size) <- accesses) {
    test(s"$access at a multiple of $size bytes runs") {
      assertEquals(run(base, access, size), Right(()))
    }
    test(s"$access at an odd multiple of ${size / 2} bytes faults as misaligned") {
      assert(
        run(base, access, size / 2).left.exists { case Fault.Misaligned(_) => true; case _ => false },
        run(base, access, size / 2)
      )
    }
  }

  test("byte accesses at any offset run") {
    assertEquals(run(global, "ld.global.u8 %h, [%p+OFFSET]; st.global.u8 [%p+OFFSET], %h;", 3), Right(()))
  }

  test("shared storage of another entry does not shift this entry's alignment") {
    val source = """.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry a_pad(.param .u64 buffer) {
      |  .shared .align 4 .b8 pad[4];
      |  .reg .u64 %p, %s;
      |  ld.param.u64 %p, [buffer];
      |  mov.u64 %s, pad;
      |  st.global.u64 [%p], %s;
      |  ret;
      |}
      |.visible .entry access(.param .u64 buffer) {
      |  .shared .align 4 .b8 smem[8280];
      |  .reg .u64 %p;
      |  .reg .u32 %r<4>;
      |  ld.param.u64 %p, [buffer];
      |  ld.shared.v4.u32 {%r0, %r1, %r2, %r3}, [smem+4608];
      |  st.global.u32 [%p], %r0;
      |  ret;
      |}""".stripMargin
    val memory = DeviceMemory(64)
    assertEquals(
      execute(
        compileProgram(Parser.parse(source).toOption.get, "access": NonEmpty[String]),
        1,
        1,
        Map(("buffer": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
  }

  test("module scope shared storage only shifts the entries that name it") {
    val source = """.version 8.0
      |.target sm_80
      |.address_size 64
      |.shared .align 4 .b8 a_pad[4];
      |.shared .align 4 .b8 smem[8280];
      |.visible .entry first(.param .u64 buffer) {
      |  .reg .u64 %p;
      |  .reg .u32 %r;
      |  ld.param.u64 %p, [buffer];
      |  mov.u32 %r, a_pad;
      |  st.global.u32 [%p], %r;
      |  ret;
      |}
      |.visible .entry access(.param .u64 buffer) {
      |  .reg .u64 %p;
      |  .reg .u32 %r<4>;
      |  ld.param.u64 %p, [buffer];
      |  ld.shared.v4.u32 {%r0, %r1, %r2, %r3}, [smem+4608];
      |  st.global.u32 [%p], %r0;
      |  ret;
      |}""".stripMargin
    val memory = DeviceMemory(64)
    assertEquals(
      execute(
        compileProgram(Parser.parse(source).toOption.get, "access": NonEmpty[String]),
        1,
        1,
        Map(("buffer": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
  }
}

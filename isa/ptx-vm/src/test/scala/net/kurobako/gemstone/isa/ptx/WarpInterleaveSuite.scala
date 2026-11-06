package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.testing.*

class WarpInterleaveSuite extends Suite {
  private def run(spin: String): Vec[Int] = {
    val delay  = "add.u32 %s, %s, 1;\n" * 100
    val kernel = compileKernel(s""".version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry probe(.param .u64 data) {
      |  .shared .align 4 .b8 flag[4];
      |  .shared .align 4 .b8 counter[4];
      |  .reg .u64 %base, %address;
      |  .reg .u32 %t, %s, %v, %o, %i;
      |  .reg .pred %p, %q, %more;
      |  ld.param.u64 %base, [data];
      |  mov.u32 %t, %tid.x;
      |  mul.wide.u32 %address, %t, 4;
      |  add.u64 %address, %address, %base;
      |  mov.u32 %s, 0;
      |  mov.u32 %v, 0;
      |  mov.u32 %o, 0;
      |  bar.sync 0;
      |  setp.ge.u32 %p, %t, 32;
      |  @%p bra READER;
      |  $delay
      |  st.shared.u32 [flag], 1;
      |  bra.uni SPIN;
      |READER:
      |  ld.shared.u32 %v, [flag];
      |SPIN:
      |  mov.u32 %i, 0;
      |LOOP:
      |  $spin
      |  add.u32 %i, %i, 1;
      |  setp.lt.u32 %more, %i, 4;
      |  @%more bra LOOP;
      |  st.global.u32 [%address], %v;
      |  ret;
      |}""".stripMargin)
    val memory = DeviceMemory(256)
    val result = execute(
      kernel,
      1,
      64,
      Map(("data": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
    assertEquals(result, Right(()))
    memory.ints(from = 0, count = 64)
  }

  test("a converged warp does not run ahead of the other warps of its block") {
    assertEquals(run("atom.shared.add.u32 %o, [counter], 1;").drop(32), Vec.fill(32)(0))
  }

  test("a converged warp does not run ahead of the other warps of a block without spin loops") {
    assertEquals(run("add.u32 %o, %o, 1;").drop(32), Vec.fill(32)(0))
  }
}

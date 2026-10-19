package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** V_MOVRELS_B32, V_MOVRELD_B32, V_MOVRELSD_B32, V_MOVRELSD_2_B32 and V_SWAPREL_B32 in every encoding: the DPP, DPP8,
  * SDWA and VOP3 forms apply their routing, selection and placement to the register M0 offsets, as on a gfx1036 (RDNA2)
  * with the probe kernels `s_mov_b32 m0, N; v_movrels_b32_dpp v32, v17 quad_perm:[1,0,3,2]` and the like, whose results
  * these expectations match. Bytes from `llvm-mc -arch=amdgcn -mcpu=<target> -show-encoding`.
  */
class AMDGPURelativeMoveSuite extends munit.FunSuite {
  private val gfx1030 = Target("gfx1030")
  private val gfx1100 = Target("gfx1100")
  private val gfx1201 = Target("gfx1201")

  // lane l holds (l + 1) * 0x100 + k in the register 2 + k
  private val inputs = Vec.tabulate(4)(lane => Vec.tabulate(8)(index => (lane + 1) * 0x100L + index))

  private def value(lane: Int, register: Int): Long = (lane + 1) * 0x100L + register - 2

  private def swapped(lane: Int): Int = lane ^ 1

  private val InitialRegister = 0x11223344L

  /** Each lane's `result` after v20 is set to the initial value, M0 to `m0` and `code` has run. */
  private def run(target: Target, m0: Long, result: Int, code: Int*): Vec[Long] = {
    val setup = Vec(
      Instruction.SMove(m0Register(target.isa), InstructionSource.Constant(m0)),
      Instruction.VMove(20, InstructionSource.Constant(InitialRegister)),
      Instruction.VMove(22, InstructionSource.Constant(InitialRegister))
    )
    laneResults(target, inputs, setup ++ program(target, octets(code*)), result.unsafe[VectorRegisterIndex])
  }

  private def expected(per: Int => Long): Vec[Long] = Vec.tabulate(4)(per)

  test("v_movrels_b32_dpp routes the register M0 offsets") {
    val quad = Vec(0xfa, 0x86, 0x28, 0x7e, 0x03, 0xb1, 0x00, 0xff)
    val octs = Vec(0xe9, 0x86, 0x28, 0x7e, 0x03, 0xc1, 0x54, 0xde)
    for (target <- Vec(gfx1030, gfx1100, gfx1201)) {
      assertEquals(run(target, 2, 20, quad*), expected(lane => value(swapped(lane), 5)), target.name)
      assertEquals(run(target, 2, 20, octs*), expected(lane => value(swapped(lane), 5)), target.name)
    }
  }

  test("v_movrels_b32_sdwa selects from the register M0 offsets") {
    assertEquals(
      run(gfx1030, 2, 20, 0xf9, 0x86, 0x28, 0x7e, 0x03, 0x06, 0x01, 0x00),
      expected(lane => (value(lane, 5) >> 8) & 0xff)
    )
  }

  test("v_movreld_b32_dpp routes its source into the register M0 offsets") {
    val quad = Vec(0xfa, 0x84, 0x28, 0x7e, 0x03, 0xb1, 0x00, 0xff)
    assertEquals(run(gfx1030, 2, 22, quad*), expected(lane => value(swapped(lane), 3)))
    assertEquals(run(gfx1030, 2, 20, quad*), expected(_ => InitialRegister))
  }

  test("v_movreld_b32_sdwa places the byte into the register M0 offsets and keeps the rest of it") {
    val code = Vec(0xf9, 0x84, 0x28, 0x7e, 0x03, 0x11, 0x06, 0x00)
    assertEquals(
      run(gfx1030, 2, 22, code*),
      expected(lane => (InitialRegister & ~0xff00L) | ((value(lane, 3) & 0xff) << 8))
    )
    assertEquals(run(gfx1030, 2, 20, code*), expected(_ => InitialRegister))
  }

  test("v_movrelsd_b32_dpp routes the register M0 offsets into the register it offsets") {
    val quad = Vec(0xfa, 0x88, 0x28, 0x7e, 0x03, 0xb1, 0x00, 0xff)
    assertEquals(run(gfx1030, 2, 22, quad*), expected(lane => value(swapped(lane), 5)))
  }

  test("v_movrelsd_b32_sdwa selects from and places into the registers M0 offsets") {
    val code = Vec(0xf9, 0x88, 0x28, 0x7e, 0x03, 0x06, 0x01, 0x00)
    assertEquals(run(gfx1030, 2, 22, code*), expected(lane => (value(lane, 5) >> 8) & 0xff))
  }

  test("v_movrelsd_b32_e64_dpp and v_movreld_b32_e64_dpp route in their VOP3 forms") {
    val relative = Vec(0x14, 0x00, 0xc4, 0xd5, 0xe9, 0x00, 0x00, 0x00, 0x03, 0xc1, 0x54, 0xde)
    val loaded   = Vec(0x14, 0x00, 0xc2, 0xd5, 0xfa, 0x00, 0x00, 0x00, 0x03, 0xb1, 0x00, 0xff)
    for (target <- Vec(gfx1100, gfx1201)) {
      assertEquals(run(target, 2, 22, relative*), expected(lane => value(swapped(lane), 5)), target.name)
      assertEquals(run(target, 2, 22, loaded*), expected(lane => value(swapped(lane), 3)), target.name)
    }
  }

  test("v_movrels_b32_e64_dpp routes the register M0 offsets in its VOP3 form") {
    val quad = Vec(0x14, 0x00, 0xc3, 0xd5, 0xfa, 0x00, 0x00, 0x00, 0x03, 0xb1, 0x00, 0xff)
    val octs = Vec(0x14, 0x00, 0xc3, 0xd5, 0xe9, 0x00, 0x00, 0x00, 0x03, 0xc1, 0x54, 0xde)
    for (target <- Vec(gfx1100, gfx1201)) {
      assertEquals(run(target, 2, 20, quad*), expected(lane => value(swapped(lane), 5)), target.name)
      assertEquals(run(target, 2, 20, octs*), expected(lane => value(swapped(lane), 5)), target.name)
    }
  }

  test("v_movrelsd_2_b32 offsets its source by M0[9:0] and its destination by M0[25:16] in every encoding") {
    val vop3 = Vec(0x14, 0x00, 0xc8, 0xd5, 0x03, 0x01, 0x00, 0x00)
    assertEquals(run(gfx1030, 0x20002, 22, vop3*), expected(lane => value(lane, 5)))
    assertEquals(run(gfx1030, 0x20002, 20, vop3*), expected(_ => InitialRegister))
    assertEquals(run(gfx1030, 0x40402, 24, vop3*), expected(lane => value(lane, 5)))
    assertEquals(run(gfx1030, 0x40402, 22, vop3*), expected(_ => InitialRegister))
  }

  test("v_movrelsd_2_b32_dpp and v_movrelsd_2_b32_sdwa route and place the registers M0 offsets") {
    val quad = Vec(0xfa, 0x90, 0x28, 0x7e, 0x03, 0xb1, 0x00, 0xff)
    val octs = Vec(0xe9, 0x90, 0x28, 0x7e, 0x03, 0xc1, 0x54, 0xde)
    val sdwa = Vec(0xf9, 0x90, 0x28, 0x7e, 0x03, 0x11, 0x06, 0x00)
    assertEquals(run(gfx1030, 0x20002, 22, quad*), expected(lane => value(swapped(lane), 5)))
    assertEquals(run(gfx1030, 0x20002, 22, octs*), expected(lane => value(swapped(lane), 5)))
    assertEquals(
      run(gfx1030, 0x20002, 22, sdwa*),
      expected(lane => (InitialRegister & ~0xff00L) | ((value(lane, 5) & 0xff) << 8))
    )
    val wide = Vec(0xe9, 0x90, 0x28, 0x7e, 0x03, 0xc1, 0x54, 0xde)
    val bits = Vec(0x14, 0x00, 0xc8, 0xd5, 0xfa, 0x00, 0x00, 0x00, 0x03, 0xb1, 0x00, 0xff)
    for (target <- Vec(gfx1100, gfx1201)) {
      assertEquals(run(target, 0x20002, 22, wide*), expected(lane => value(swapped(lane), 5)), target.name)
      assertEquals(run(target, 0x20002, 22, bits*), expected(lane => value(swapped(lane), 5)), target.name)
    }
  }

  test("v_swaprel_b32 exchanges its source M0[9:0] offsets with its destination M0[25:16] offsets") {
    val code = Vec(0x03, 0xd1, 0x2a, 0x7e)
    assertEquals(run(gfx1030, 0x10002, 22, code*), expected(lane => value(lane, 5)))
    assertEquals(run(gfx1030, 0x10002, 5, code*), expected(_ => InitialRegister))
    assertEquals(run(gfx1030, 0x20001, 23, code*), expected(lane => value(lane, 4)))
    assertEquals(run(gfx1030, 0x20001, 4, code*), expected(_ => 0L))
  }

  test("v_swaprel_b32 writes neither register where either offset is beyond the last") {
    val code = Vec(0x03, 0xd1, 0x2a, 0x7e)
    assertEquals(run(gfx1030, 0x103ff, 22, code*), expected(_ => InitialRegister))
    assertEquals(run(gfx1030, 0x3ff0002L, 5, code*), expected(lane => value(lane, 5)))
  }

  test("a VGPR M0 offsets beyond 255 reads v0 and drops a write, M0 counting all 32 bits") {
    // gfx1036 bounds the offset by the kernel's VGPR allocation (64, 72, 96 and 128 probed); the VM allocates 256
    val read  = Vec(0x03, 0x87, 0x28, 0x7e)
    val write = Vec(0x03, 0x85, 0x28, 0x7e)
    assertEquals(run(gfx1030, 253, 20, read*), expected(lane => lane.toLong))
    assertEquals(run(gfx1030, 0x102, 20, read*), expected(lane => lane.toLong))
    assertEquals(run(gfx1030, 0xffffffffL, 20, read*), expected(lane => lane.toLong))
    assertEquals(run(gfx1030, 252, 20, read*), expected(_ => 0L))
    assertEquals(run(gfx1030, 0x100, 20, write*), expected(_ => InitialRegister))
    assertEquals(run(gfx1030, 0x100, 22, write*), expected(_ => InitialRegister))
  }
}

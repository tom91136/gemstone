package net.kurobako.gemstone.isa.amdgpu

import laneRuns.*
import net.kurobako.gemstone.collection.*

/** GFX11 onwards selects a 16-bit operand's high half in the 32-bit VOP1, VOP2 and VOPC encodings by bit 7 of its VGPR
  * number, which upstream LLVM 22 emits for half arithmetic on gfx1100 (`llc -mcpu=gfx1100`: `v_add_f16_e32 v0.l, v0.l,
  * v0.h`); bytes from ROCm 7.1.1 `llvm-mc -triple=amdgcn -mcpu=gfx1100 -mattr=+real-true16 -show-encoding`. Each lane's
  * v2 holds 1.0 low and 2.0 high and v3 0.5 low and 3.0 high.
  */
class AMDGPUTrue16Suite extends munit.FunSuite {
  private val target = Target("gfx1100")

  private def run(code: Int*)(destination: Long, high: Long = 0x4000L): Long =
    laneResults(
      target,
      Vec(Vec((high << 16) | 0x3c00L, 0x42003800L, 0L, destination)),
      program(target, octets(code*)),
      5
    ).head

  test("v_add_f16_e32 v5.h, v2.l, v3.h writes the high half from the halves it names") {
    assertEquals(run(0x02, 0x07, 0x0b, 0x65)(0x12345678L), 0x44005678L)
  }

  test("v_cvt_f32_f16_e32 v5, v2.h and v_mov_b16_e32 v5.h, v2.l read and write the named halves") {
    assertEquals(run(0x82, 0x17, 0x0a, 0x7e)(0L), 0x40000000L)
    assertEquals(run(0x02, 0x39, 0x0a, 0x7f)(0x12345678L), 0x3c005678L)
  }

  test("v_fmac_f16_e32 v5.h, v2.l, v3.h accumulates into the high half") {
    assertEquals(run(0x02, 0x07, 0x0b, 0x6d)(0x3c005678L), 0x44005678L)
  }

  test("v_cmp_lt_f16_e32 vcc_lo, v2.h, v3.l compares the named halves") {
    val select = Vec(0x05, 0x00, 0x01, 0xd5, 0x80, 0x02, 0xa9, 0x01)
    assertEquals(run((Vec(0x82, 0x07, 0x02, 0x7c) ++ select)*)(0x12345678L), 0L)
  }

  test("v_add_f16_dpp v5.h, v2.h, v3.l quad_perm:[1,0,3,2] routes the high half") {
    val results = laneResults(
      target,
      Vec(Vec(0x40003c00L, 0x42003800L, 0L, 0L), Vec(0x44003c00L, 0x42003800L, 0L, 0L)),
      program(target, octets(0xfa, 0x06, 0x0a, 0x65, 0x82, 0xb1, 0x00, 0xff)),
      5
    )
    assertEquals(results, Vec(0x44800000L, 0x41000000L))
  }
}

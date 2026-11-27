package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*

/** GFX11 onwards selects a 16-bit operand's high half in the 32-bit VOP1, VOP2 and VOPC encodings by bit 7 of its VGPR
  * number, which upstream LLVM 22 emits for half arithmetic on gfx1100 (`llc -mcpu=gfx1100`: `v_add_f16_e32 v0.l, v0.l,
  * v0.h`); bytes from ROCm 7.1.1 `llvm-mc -triple=amdgcn -mcpu=gfx1100 -mattr=+real-true16 -show-encoding`. Each lane's
  * v2 holds 1.0 low and 2.0 high and v3 0.5 low and 3.0 high.
  */
class AMDGPUTrue16Suite extends Suite {
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

  test("VOP3 f16 compare reads the halves its OP_SEL names") {
    // LLVM: v_cmp_gt_f16_e64 s0, v0.h, v1.h op_sel:[1,1,0]; v_cndmask_b16 v8.l, v6.l, v7.l, s0
    val compare = Vec(0xd4041800, 0x02020300, 0xd65d0008, 0x00020f06)
    val code    = Vec.from(compare.flatMap(word => Vec.tabulate(4)(i => (word >>> (i * 8)).toByte)))
    val ops     = Vec(
      Instruction.VMove(0.unsafe[VectorRegisterIndex], InstructionSource.VectorRegister(2.unsafe[VectorRegisterIndex])),
      Instruction.VMove(1.unsafe[VectorRegisterIndex], InstructionSource.VectorRegister(3.unsafe[VectorRegisterIndex]))
    ) ++ program(target, code)
    def select(v0: Long, v1: Long): Long =
      laneWords(
        target,
        Vec(Vec(v0, v1, 0L, 0L, 0x1111L, 0x2222L)),
        ops,
        Vec(8.unsafe[VectorRegisterIndex]),
        offset = 9.unsafe[VectorRegisterIndex]
      ).head.head
    assertEquals(select(0x50003800L, 0x3c004500L), 0x2222L)
    assertEquals(select(0x38005000L, 0x3c004500L), 0x1111L)
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

package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** The 8-bit float conversions (MI300 ISA, 7.2), which hipcc emits for `__builtin_amdgcn_cvt_f32_fp8`,
  * `cvt_pk_f32_bf8`, `cvt_pk_fp8_f32` and `cvt_sr_fp8_f32` (`-O3 --offload-arch=gfx942`: `v_cvt_f32_fp8_sdwa v2, v1
  * src0_sel:BYTE_1`, `v_cvt_pk_fp8_f32 v4, v0, v1 op_sel:[0,0,1]`, `v_cvt_sr_fp8_f32 v4, v1, v3 op_sel:[0,0,0,1]`;
  * gfx1201: `v_cvt_f32_fp8_e64 v2, v1 byte_sel:1`), in CDNA3's FNUZ formats and the OCP formats of gfx950 and GFX12.
  * Bytes from ROCm 7.1.1 `llvm-mc -triple=amdgcn -mcpu=<target> -show-encoding`.
  */
class AMDGPUEightBitFloatSuite extends munit.FunSuite {
  private val gfx942  = Target("gfx942")
  private val gfx950  = Target("gfx950")
  private val gfx1201 = Target("gfx1201")

  /** `result` after `code` with v2 to v5 holding `inputs`, MODE.FP16_OVFL set when `saturating`. */
  private def run(target: Target, code: Vec[Int], inputs: Vec[Long], result: Int = 5, saturating: Boolean = false)
      : Long = {
    val mode = Vec(Instruction.SSetMode(23, 1, InstructionSource.Constant(if (saturating) 1L else 0L)))
    laneResults(
      target,
      Vec(inputs),
      mode ++ program(target, octets(code*)),
      result.unsafe[VectorRegisterIndex],
      wave32 = target.isa.isRDNA
    ).head
  }

  private val one  = 0x3f800000L
  private val two  = 0x40000000L
  private val huge = 0x447a0000L

  test("gfx942 widens FNUZ bytes and words selected by SDWA") {
    assertEquals(run(gfx942, Vec(0xf9, 0xa8, 0x0a, 0x7e, 0x02, 0x06, 0x01, 0x00), Vec(0x4000L)), one)
    assertEquals(run(gfx942, Vec(0x02, 0xa9, 0x0a, 0x7e), Vec(0xc0L)), 0xbf800000L)
    assert(java.lang.Float.isNaN(java.lang.Float.intBitsToFloat(
      run(gfx942, Vec(0x02, 0xa9, 0x0a, 0x7e), Vec(0x80L)).toInt
    )))
    val pair = Vec(0x02, 0xaf, 0x08, 0x7e)
    assertEquals(Vec(4, 5).map(run(gfx942, pair, Vec(0x4440L), _)), Vec(one, two))
    val high = Vec(0xf9, 0xae, 0x08, 0x7e, 0x02, 0x06, 0x05, 0x00)
    assertEquals(Vec(4, 5).map(run(gfx942, high, Vec(0x44400000L), _)), Vec(one, two))
  }

  test("gfx942 packs FNUZ bytes into the word OP_SEL names, saturating under MODE.FP16_OVFL") {
    val pack = Vec(0x05, 0x40, 0xa2, 0xd2, 0x02, 0x07, 0x02, 0x00)
    assertEquals(run(gfx942, pack, Vec(one, two, 0L, 0x12345678L)), 0x48405678L)
    assertEquals(run(gfx942, pack, Vec(huge, two, 0L, 0x12345678L)), 0x48805678L)
    assertEquals(run(gfx942, pack, Vec(huge, two, 0L, 0x12345678L), saturating = true), 0x487f5678L)
  }

  test("gfx942 rounds stochastically by the seed's high bits into the byte OP_SEL names") {
    val stochastic = Vec(0x05, 0x20, 0xa4, 0xd2, 0x02, 0x07, 0x02, 0x00)
    assertEquals(run(gfx942, stochastic, Vec(0x3f880000L, 0L, 0L, 0x12345678L)), 0x12344078L)
    assertEquals(run(gfx942, stochastic, Vec(0x3f880000L, 0xffffffffL, 0L, 0x12345678L)), 0x12344178L)
  }

  test("gfx950 converts the OCP formats") {
    assertEquals(run(gfx950, Vec(0xf9, 0xa8, 0x0a, 0x7e, 0x02, 0x06, 0x01, 0x00), Vec(0x4000L)), two)
    val pack = Vec(0x05, 0x40, 0xa2, 0xd2, 0x02, 0x07, 0x02, 0x00)
    assertEquals(run(gfx950, pack, Vec(one, two, 0L, 0x12345678L)), 0x40385678L)
    assertEquals(run(gfx950, pack, Vec(huge, two, 0L, 0x12345678L)), 0x407f5678L)
    assertEquals(run(gfx950, pack, Vec(huge, two, 0L, 0x12345678L), saturating = true), 0x407e5678L)
  }

  test("gfx1201 selects bytes by byte_sel and words by op_sel") {
    assertEquals(run(gfx1201, Vec(0x05, 0x10, 0xec, 0xd5, 0x02, 0x01, 0x00, 0x00), Vec(0x4000L)), two)
    val pair = Vec(0x04, 0x08, 0xef, 0xd5, 0x02, 0x01, 0x00, 0x00)
    assertEquals(Vec(4, 5).map(run(gfx1201, pair, Vec(0x3c380000L), _)), Vec(0x3f000000L, one))
    val stochastic = Vec(0x05, 0x40, 0x6b, 0xd7, 0x02, 0x07, 0x02, 0x00)
    assertEquals(run(gfx1201, stochastic, Vec(0x3f880000L, 0xffffffffL, 0L, 0x12345678L)), 0x12395678L)
  }
}

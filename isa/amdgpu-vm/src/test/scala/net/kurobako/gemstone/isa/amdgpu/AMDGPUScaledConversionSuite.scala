package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.Suite

/** gfx950's scaled conversions (CDNA4 ISA, 7.3 and 12.11), which hipcc emits for `__builtin_amdgcn_cvt_scalef32_*`
  * (ROCm 7.1.1 `hipcc -O2 --offload-arch=gfx950`: `v_cvt_scalef32_f32_fp8 v13, v0, v12 op_sel:[1,0,0]`,
  * `v_cvt_scalef32_pk_fp8_f32 v15, v4, v5, v12 op_sel:[0,0,0,1]`, `v_cvt_scalef32_sr_pk_fp4_f32 v21, v[4:5], v1, v12
  * op_sel:[0,0,1,1]`, `v_cvt_scalef32_2xpk16_fp6_f32 v[68:73], v[2:17], v[18:33], v67`), and its other new conversions
  * and integer packs (`__builtin_amdgcn_prng_b32`, `ashr_pk_i8_i32`, `cvt_sr_bf16_f32`). Bytes from LLVM 22
  * `llvm-mc -triple=amdgcn -mcpu=gfx950 -show-encoding`.
  */
class AMDGPUScaledConversionSuite extends Suite {
  private val gfx950 = Target("gfx950")

  /** `result` after `code` with v2 onwards holding `inputs`, MODE.FP16_OVFL set when `saturating`. */
  private def run(code: Vec[Int], inputs: Vec[Long], result: Int = 5, saturating: Boolean = false): Long = {
    val mode = Vec(Instruction.SSetMode(23, 1, InstructionSource.Constant(if (saturating) 1L else 0L)))
    laneResults(gfx950, Vec(inputs), mode ++ program(gfx950, octets(code*)), result.unsafe[VectorRegisterIndex]).head
  }

  /** `results` after `code` with the registers `registers` maps set first, for blocks wider than the lane inputs. */
  private def block(code: Vec[Int], registers: Map[Int, Long], results: Range): Vec[Long] = {
    val moves = Vec.from(registers.toSeq.sortBy(_._1).map((index, value) =>
      Instruction.VMove(index.unsafe[VectorRegisterIndex], InstructionSource.Constant(value))
    ))
    Vec.from(results.map(index =>
      laneResults(gfx950, Vec(Vec(0L)), moves ++ program(gfx950, octets(code*)), index.unsafe[VectorRegisterIndex]).head
    ))
  }

  private val one   = 0x3f800000L
  private val two   = 0x40000000L
  private val half  = 0x3f000000L
  private val nan   = 0x7f800000L
  private val stale = 0x12345678L

  test("widening scales an 8-bit or 4-bit element by the scale's exponent into binary32 or a binary16 half") {
    val byte = Vec(0x05, 0x08, 0x3b, 0xd2, 0x02, 0x07, 0x02, 0x00)
    assertEquals(run(byte, Vec(0x4000L, two)), 0x40800000L)
    assert(java.lang.Float.isNaN(java.lang.Float.intBitsToFloat(run(byte, Vec(0x4000L, nan)).toInt)))
    val high = Vec(0x05, 0x40, 0x4a, 0xd2, 0x02, 0x07, 0x02, 0x00)
    assertEquals(run(high, Vec(0x38L, one, 0L, stale)), 0x3c005678L)
    val pair       = Vec(0x06, 0x08, 0x39, 0xd2, 0x02, 0x07, 0x02, 0x00)
    val registers  = Vec(6, 7)
    val pairInputs = Vec(0x40380000L, half)
    assertEquals(registers.map(run(pair, pairInputs, _)), Vec(half, one))
    val nibbles      = Vec(0x06, 0x18, 0x3f, 0xd2, 0x02, 0x07, 0x02, 0x00)
    val nibbleInputs = Vec(0x79000000L, one)
    assertEquals(registers.map(run(nibbles, nibbleInputs, _)), Vec(0xbf000000L, 0x40c00000L))
    val bfloat = Vec(0x05, 0x00, 0x51, 0xd2, 0x02, 0x07, 0x02, 0x00)
    assertEquals(run(bfloat, Vec(0x72L, one, 0L, stale)), 0x40c03f80L)
  }

  test("narrowing divides by the scale into the word or byte OP_SEL names, saturating FP4 always and FP8 by MODE") {
    val word = Vec(0x05, 0x40, 0x35, 0xd2, 0x02, 0x07, 0x12, 0x04)
    assertEquals(run(word, Vec(one, two, two, stale)), 0x38305678L)
    assertEquals(run(word, Vec(0x447a0000L, two, one, stale)), 0x407f5678L)
    assertEquals(run(word, Vec(0x447a0000L, two, one, stale), saturating = true), 0x407e5678L)
    val nibbles = Vec(0x05, 0x60, 0x3d, 0xd2, 0x02, 0x07, 0x12, 0x04)
    assertEquals(run(nibbles, Vec(0x40c00000L, 0xbf000000L, one, stale)), 0x97345678L)
    assertEquals(run(nibbles, Vec(0x42c80000L, 0xbf000000L, one, stale)), 0x97345678L)
    val halves = Vec(0x05, 0x40, 0x40, 0xd2, 0x02, 0x07, 0x02, 0x00)
    assertEquals(run(halves, Vec(0x40003c00L, one, 0L, stale)), 0x40385678L)
  }

  test("stochastic narrowing adds the seed's high bits below the format's step and truncates") {
    val byte = Vec(0x05, 0x20, 0x37, 0xd2, 0x02, 0x07, 0x12, 0x04)
    assertEquals(run(byte, Vec(0x3f880000L, 0L, one, stale)), 0x12343878L)
    assertEquals(run(byte, Vec(0x3f880000L, 0xffffffffL, one, stale)), 0x12343978L)
    val pair = Vec(0x06, 0x00, 0x3e, 0xd2, 0x02, 0x09, 0x16, 0x04)
    assertEquals(run(pair, Vec(0x3fa00000L, 0xc0400000L, 0L, one, stale), 6), 0x123456d2L)
  }

  test("the 32-element conversions pack 6-bit elements across six dwords, the 2xpk16 forms interleaving") {
    val widen     = Vec(0x0a, 0x00, 0x56, 0xd2, 0x02, 0x11, 0x02, 0x00)
    val sixes     = Vec(0x08208208L, 0x82082082L, 0x20820820L, 0x08208208L, 0x82082082L, 0x20820820L)
    val registers = sixes.zipWithIndex.map((value, index) => (2 + index) -> value).toMap + (8 -> two)
    assertEquals(block(widen, registers, 10 to 41), Vec.fill(32)(two))
    val interleave = Vec(0x28, 0x00, 0x52, 0xd2, 0x02, 0x25, 0x8a, 0x04)
    val pairs      = (2 to 17).map(_ -> one).toMap ++ (18 to 33).map(_ -> 0xc0000000L).toMap + (34 -> one)
    assertEquals(
      block(interleave, pairs, 40 to 45),
      Vec(0x08c08c08L, 0x8c08c08cL, 0xc08c08c0L, 0x08c08c08L, 0x8c08c08cL, 0xc08c08c0L)
    )
    val stochastic = Vec(0x28, 0x00, 0x5f, 0xd2, 0x02, 0x25, 0x4e, 0x04)
    val bfloats    = (2 to 17).map(_ -> 0x3e803f80L).toMap + (18 -> 0x9e3779b9L) + (19 -> one)
    assertEquals(
      block(stochastic, bfloats, 40 to 45),
      Vec(0x0c10c10cL, 0xc10c10c1L, 0x10c10c10L, 0x0c10c10cL, 0xc10c10c1L, 0x10c10c10L)
    )
  }

  test("V_CVT_SR_F16_F32 and V_CVT_SR_BF16_F32 round stochastically into the half OP_SEL names") {
    val bfloat = Vec(0x05, 0x40, 0xa7, 0xd2, 0x02, 0x07, 0x02, 0x00)
    assertEquals(run(bfloat, Vec(0x3f808000L, 0L, 0L, stale)), 0x3f805678L)
    assertEquals(run(bfloat, Vec(0x3f808000L, 0xffffffffL, 0L, stale)), 0x3f815678L)
    val half = Vec(0x05, 0x00, 0xa6, 0xd2, 0x02, 0x07, 0x02, 0x00)
    assertEquals(run(half, Vec(0x3f801000L, 0L, 0L, stale)), 0x12343c00L)
    assertEquals(run(half, Vec(0x3f801000L, 0xffffffffL, 0L, stale)), 0x12343c01L)
  }

  test("V_PRNG_B32, V_ASHR_PK_*8_I32 and V_CVT_PKACCUM_U8_F32") {
    assertEquals(run(Vec(0x02, 0xb1, 0x0a, 0x7e), Vec(0x80000001L)), 0xc7L)
    val signed = Vec(0x05, 0x00, 0x65, 0xd2, 0x02, 0x07, 0x12, 0x04)
    assertEquals(run(signed, Vec(0x1000L, 0xffff0000L, 4L, stale)), 0x807fL)
    val unsigned = Vec(0x05, 0x00, 0x66, 0xd2, 0x02, 0x07, 0x12, 0x04)
    assertEquals(run(unsigned, Vec(0x1000L, 0xffff0000L, 4L, stale)), 0x00ffL)
    val accumulate = Vec(0x05, 0x00, 0xf0, 0xd1, 0x02, 0x07, 0x02, 0x00)
    assertEquals(run(accumulate, Vec(0x43480000L, 2L, 0L, stale)), 0x12c85678L)
  }
}

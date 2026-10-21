package net.kurobako.gemstone.isa.amdgpu

import assembly.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*

/** Operand and modifier spellings of matrix and mixed-precision instructions, each with its bytes from ROCm 7.1.1
  * `llvm-mc -triple=amdgcn -mcpu=<target> -show-encoding`: SMFMAC into accumulation registers, which hipcc allocates
  * once a kernel's VGPRs run out, f64 MFMA negations, the register counts unscaled f8f6f4 MFMA takes by format, the
  * `index_key` spelling of SWMMAC's index set, and `v_fma_mix_f32`, which hipcc emits for `(float)h * (float)h + f` on
  * gfx942 (`v_fma_mix_f32 v1, v2, v3, v4 op_sel_hi:[1,1,0]`).
  */
class MatrixSyntaxSuite extends munit.FunSuite {
  private def roundTrip(target: Target, waveSize: Int in (32, 64), source: String, bytes: String)(using
      munit.Location
  ): Unit = {
    val expected = hex(bytes)
    assertEquals(Binary.encodeStatement(parseStatement(source), TargetMode(target, waveSize)), Right(expected), source)
    val decoded =
      Binary.decodeStatementAt(expected, 0, TargetMode(target, waveSize)).fold(
        error => fail(s"$source: $error"),
        identity
      )
    val printed = Printer.render(Program(Vec(decoded.statement)))
    assertEquals(
      Binary.encodeStatement(parseStatement(printed), TargetMode(target, waveSize)),
      Right(expected),
      printed
    )
  }

  test("gfx942 SMFMAC writes accumulation registers") {
    roundTrip(Target("gfx942"), 64, "v_smfmac_f32_16x16x32_f16 a[0:3], v[0:1], v[2:5], v6", "00 80 e2 d3 00 05 1a 04")
    roundTrip(
      Target("gfx942"),
      64,
      "v_smfmac_f32_16x16x32_f16 v[0:3], v[4:5], v[6:9], v10 cbsz:1 abid:2",
      "00 11 e2 d3 04 0d 2a 04"
    )
    roundTrip(
      Target("gfx950"),
      64,
      "v_smfmac_f32_16x16x64_bf16 a[0:3], v[4:7], v[8:15], v16",
      "00 80 b9 d3 04 11 42 04"
    )
  }

  test("gfx942 f64 MFMA negates A, B and C") {
    val target = Target("gfx942")
    roundTrip(
      target,
      64,
      "v_mfma_f64_16x16x4_f64 v[0:7], v[8:9], v[10:11], v[0:7] neg:[1,0,0]",
      "00 00 ee d3 08 15 02 24"
    )
    roundTrip(
      target,
      64,
      "v_mfma_f64_16x16x4_f64 a[0:7], v[8:9], v[10:11], a[0:7] neg:[0,1,1]",
      "00 80 ee d3 08 15 02 c4"
    )
    roundTrip(
      target,
      64,
      "v_mfma_f64_4x4x4_4b_f64 v[0:1], v[2:3], v[4:5], v[0:1] neg:[1,1,0]",
      "00 00 ef d3 02 09 02 64"
    )
  }

  test("gfx950 unscaled f8f6f4 MFMA takes eight, six or four registers by CBSZ and BLGP") {
    val target = Target("gfx950")
    roundTrip(
      target,
      64,
      "v_mfma_f32_16x16x128_f8f6f4 v[0:3], v[4:11], v[12:19], v[0:3]",
      "00 00 ad d3 04 19 02 04"
    )
    roundTrip(
      target,
      64,
      "v_mfma_f32_16x16x128_f8f6f4 v[0:3], v[4:9], v[12:17], v[0:3] cbsz:2 blgp:2",
      "00 02 ad d3 04 19 02 44"
    )
    roundTrip(
      target,
      64,
      "v_mfma_f32_16x16x128_f8f6f4 v[0:3], v[4:7], v[12:15], v[0:3] cbsz:4 blgp:4",
      "00 04 ad d3 04 19 02 84"
    )
    roundTrip(
      target,
      64,
      "v_mfma_f32_32x32x64_f8f6f4 v[0:15], v[16:21], v[22:25], v[0:15] cbsz:3 blgp:4",
      "00 03 ae d3 10 2d 02 84"
    )
    assert(Binary.encodeStatement(
      parseStatement("v_mfma_f32_16x16x128_f8f6f4 v[0:3], v[4:11], v[12:19], v[0:3] cbsz:4 blgp:4"),
      TargetMode(target, 64)
    ).isLeft)
  }

  test("gfx1201 SWMMAC spells its index set index_key") {
    roundTrip(
      Target("gfx1201"),
      32,
      "v_swmmac_f32_16x16x32_f16 v[0:7], v[8:11], v[12:19], v20 index_key:1",
      "00 48 50 cc 08 19 52 1c"
    )
  }

  test("gfx942 v_fma_mix_f32 and v_fma_mixlo_f16 assemble") {
    roundTrip(Target("gfx942"), 64, "v_fma_mix_f32 v0, v1, v2, v3 op_sel_hi:[1,1,1]", "00 40 a0 d3 01 05 0e 1c")
    roundTrip(Target("gfx942"), 64, "v_fma_mix_f32 v1, v2, v3, v4 op_sel_hi:[1,1,0]", "01 00 a0 d3 02 07 12 1c")
    roundTrip(Target("gfx942"), 64, "v_fma_mixlo_f16 v0, v1, v2, v3", "00 00 a1 d3 01 05 0e 04")
  }
}

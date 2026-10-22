package net.kurobako.gemstone.isa.amdgpu

import laneRuns.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*

/** gfx1250 dual issue: the 96-bit VOPD3 pairs, which add 64-bit, three-source, select-by-SGPR and two-input truth table
  * halves and negate the float sources NEGX and NEGY name, and the integer halves VOPD gains. The CDNA5 ISA describes
  * the operations; no ROCm compiler targets gfx1250, so the pairs are assembled by LLVM 22
  * `llvm-mc -triple=amdgcn -mcpu=gfx1250 -show-encoding`.
  */
class AMDGPUDualIssueSuite extends munit.FunSuite {
  private val gfx1250 = Target("gfx1250")

  private def single(value: Long): Float            = java.lang.Float.intBitsToFloat(value.toInt)
  private def bits(value: Float): Long              = java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL
  private def double(low: Long, high: Long): Double = java.lang.Double.longBitsToDouble((high << 32) | low)
  private def bits(value: Double): Long             = java.lang.Double.doubleToRawLongBits(value)

  // v2 to v9 of four lanes, as binary32 values and as three binary64 pairs and two binary32 values
  private val singles = Vec.tabulate(4)(lane =>
    Vec(1.5f + lane, 0.5f - 2.25f * lane, 3f, lane - 0.75f, 10f - lane, 0.125f * (lane + 1), lane - 4f, 2.5f * lane + 1)
      .map(bits(_))
  )
  private val doubles = Vec.tabulate(4) { lane =>
    Vec(1.5 + lane, -2.75 * (lane + 1), 0.5 * lane + 0.25).flatMap(value =>
      Vec(bits(value) & 0xffffffffL, bits(value) >>> 32)
    ) ++ Vec(bits(lane - 4f), bits(2.5f * lane + 1))
  }

  @FunctionalInterface
  private type Reference = Vec[Long] => Long

  /** Each lane's `register` after `code` on `inputs`. */
  private def run(inputs: Vec[Vec[Long]], code: Vec[Int], register: Int)(using munit.Location): Vec[Long] =
    encoded(gfx1250, code, inputs, register)

  private def check(inputs: Vec[Vec[Long]], code: Vec[Int], register: Int)(expected: Reference)(using
      munit.Location
  ): Unit = assertEquals(run(inputs, code, register), inputs.map(lane => expected(lane) & 0xffffffffL))

  test("VOPD3 FMA halves take a third source and negate the sources NEG names") {
    // v_dual_fma_f32 v10, v2, v3, v4 :: v_dual_fma_f32 v11, -v5, v6, -v7
    val code = Vec(0x02, 0x31, 0x4d, 0xcf, 0x05, 0x51, 0x03, 0x04, 0x0a, 0x06, 0x07, 0x0b)
    check(singles, code, 10)(v => bits(IEEE754.fma(single(v(0)), single(v(1)), single(v(2)))))
    check(singles, code, 11)(v => bits(IEEE754.fma(-single(v(3)), single(v(4)), -single(v(5)))))
    // v_dual_fmac_f32 v10, -v2, v3 :: v_dual_mul_dx9_zero_f32 v11, -v5, v6, where v10 starts at zero
    val accumulate = Vec(0x02, 0x71, 0x00, 0xcf, 0x05, 0x13, 0x03, 0x00, 0x0a, 0x06, 0x00, 0x0b)
    check(singles, accumulate, 10)(v => bits(IEEE754.fma(-single(v(0)), single(v(1)), 0f)))
    check(singles, accumulate, 11)(v => bits(-single(v(3)) * single(v(4))))
  }

  test("VOPD3 binary64 halves read and write register pairs beside a binary32 half") {
    def pair(v: Vec[Long], index: Int): Double = double(v(index), v(index + 1))
    for (
      (code, product) <- Vec(
        // v_dual_add_f64 v[10:11], v[2:3], v[4:5] :: v_dual_mul_f32 v12, -v8, v7
        Vec(0x02, 0x31, 0x84, 0xcf, 0x08, 0x11, 0x04, 0x00, 0x0a, 0x07, 0x00, 0x0c) ->
          ((v: Vec[Long]) => (pair(v, 0) + pair(v, 2), bits(-single(v(6)) * single(v(5))))),
        // v_dual_fma_f64 v[10:11], -v[2:3], v[4:5], v[6:7] :: v_dual_add_f32 v12, v9, -v7
        Vec(0x02, 0x41, 0x80, 0xcf, 0x09, 0x23, 0x04, 0x06, 0x0a, 0x07, 0x00, 0x0c) ->
          ((v: Vec[Long]) => (IEEE754.fma(-pair(v, 0), pair(v, 2), pair(v, 4)), bits(single(v(7)) - single(v(5))))),
        // v_dual_mul_f64 v[10:11], v[2:3], -v[4:5] :: v_dual_max_num_f32 v12, v9, v6
        Vec(0x02, 0xa1, 0x88, 0xcf, 0x09, 0x05, 0x04, 0x00, 0x0a, 0x06, 0x00, 0x0c) ->
          ((v: Vec[Long]) => (pair(v, 0) * -pair(v, 2), bits(math.max(single(v(7)), single(v(4)))))),
        // v_dual_max_num_f64 v[10:11], v[2:3], v[4:5] :: v_dual_min_num_f32 v12, v9, v6
        Vec(0x02, 0xb1, 0x8c, 0xcf, 0x09, 0x01, 0x04, 0x00, 0x0a, 0x06, 0x00, 0x0c) ->
          ((v: Vec[Long]) => (math.max(pair(v, 0), pair(v, 2)), bits(math.min(single(v(7)), single(v(4)))))),
        // v_dual_min_num_f64 v[10:11], v[2:3], v[4:5] :: v_dual_sub_f32 v12, v8, v6
        Vec(0x02, 0x51, 0x90, 0xcf, 0x08, 0x01, 0x04, 0x00, 0x0a, 0x06, 0x00, 0x0c) ->
          ((v: Vec[Long]) => (math.min(pair(v, 0), pair(v, 2)), bits(single(v(6)) - single(v(4)))))
      )
    ) {
      check(doubles, code, 10)(v => bits(product(v)._1))
      check(doubles, code, 11)(v => bits(product(v)._1) >>> 32)
      check(doubles, code, 12)(v => product(v)._2)
    }
  }

  test("a VOPD3 select reads its lane mask from an SGPR and bitop2 applies its truth table to two inputs") {
    // s_mov_b32 s8, 5; v_dual_cndmask_b32 v10, v2, v3, s8 :: v_dual_bitop2_b32 v11, v5, v6 bitop3:0x14
    val code =
      Vec(0x85, 0x00, 0x88, 0xbe, 0x02, 0x21, 0x25, 0xcf, 0x05, 0x01, 0x03, 0x08, 0x0a, 0x06, 0x14, 0x0b)
    assertEquals(
      run(singles, code, 10),
      singles.zipWithIndex.map((v, lane) => if ((5 >> lane & 1) != 0) v(1) else v(0))
    )
    check(singles, code, 11)(v => v(3) ^ v(4))
  }

  test("the integer VOPD halves subtract, take signed extremes and shift right") {
    def signed(value: Long): Int = value.toInt
    for (
      (code, x, y) <- Vec[(Vec[Int], Reference, Reference)](
        // v_dual_sub_nc_u32 v10, v2, v3 :: v_dual_lshrrev_b32 v11, v4, v5
        (
          Vec(0x02, 0x51, 0x51, 0xcf, 0x04, 0x01, 0x03, 0x00, 0x0a, 0x05, 0x00, 0x0b),
          (v: Vec[Long]) => v(0) - v(1),
          (v: Vec[Long]) => v(3) >>> (v(2) & 31)
        ),
        // v_dual_add_nc_u32 v10, v2, v3 :: v_dual_max_i32 v11, v4, v5
        (
          Vec(0x02, 0x71, 0x41, 0xcf, 0x04, 0x01, 0x03, 0x00, 0x0a, 0x05, 0x00, 0x0b),
          (v: Vec[Long]) => v(0) + v(1),
          (v: Vec[Long]) => math.max(signed(v(2)), signed(v(3))).toLong
        ),
        // v_dual_lshlrev_b32 v10, v2, v3 :: v_dual_min_i32 v11, v4, v5
        (
          Vec(0x02, 0x81, 0x45, 0xcf, 0x04, 0x01, 0x03, 0x00, 0x0a, 0x05, 0x00, 0x0b),
          (v: Vec[Long]) => v(1) << (v(0) & 31),
          (v: Vec[Long]) => math.min(signed(v(2)), signed(v(3))).toLong
        ),
        // v_dual_lshrrev_b32 v10, v2, v3 :: v_dual_ashrrev_i32 v11, v4, v5
        (
          Vec(0x02, 0x61, 0x55, 0xcf, 0x04, 0x01, 0x03, 0x00, 0x0a, 0x05, 0x00, 0x0b),
          (v: Vec[Long]) => v(1) >>> (v(0) & 31),
          (v: Vec[Long]) => (signed(v(3)) >> (v(2) & 31).toInt).toLong
        ),
        // v_dual_mov_b32 v10, v2 :: v_dual_sub_nc_u32 v11, v3, v4
        (Vec(0x02, 0x01, 0x28, 0xca, 0x03, 0x09, 0x0a, 0x0a), (v: Vec[Long]) => v(0), (v: Vec[Long]) => v(1) - v(2)),
        // v_dual_mov_b32 v10, v2 :: v_dual_ashrrev_i32 v11, v3, v4
        (
          Vec(0x02, 0x01, 0x2c, 0xca, 0x03, 0x09, 0x0a, 0x0a),
          (v: Vec[Long]) => v(0),
          (v: Vec[Long]) => (signed(v(2)) >> (v(1) & 31).toInt).toLong
        )
      )
    ) {
      check(singles, code, 10)(x)
      check(singles, code, 11)(y)
    }
  }
}

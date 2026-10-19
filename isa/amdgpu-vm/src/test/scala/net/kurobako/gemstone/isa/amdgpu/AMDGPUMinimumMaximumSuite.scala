package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.bits.*

/** The IEEE 754-2019 minimum and maximum, which propagate NaN and order -0 below +0, as hipcc emits them for
  * `__builtin_elementwise_minimum` and `_maximum`: `v_minimum_f32`, `v_minimum_f64`, `v_maximum_f16`, `v_maximum3_f32`
  * and `v_pk_minimum_f16` under `--offload-arch=gfx1201`, and `v_minimum3_f32 v4, v4, v5, v5` and `v_pk_minimum3_f16`
  * under gfx950. Encodings from `llvm-mc -show-encoding`; v2 onwards hold the inputs and v4 the result.
  */
class AMDGPUMinimumMaximumSuite extends munit.FunSuite {

  private val NaN32 = 0x7fc00000L
  private val NaN16 = 0x7e00L

  private val gfx1201 = Target("gfx1201")
  private val gfx950  = Target("gfx950")

  private def check(target: Target, name: String, code: Vec[Int], inputs: Vec[Long], expected: Long, result: Int = 4)(
      using munit.Location
  ): Unit = test(s"$target $name") {
    assertEquals(
      laneResult(target, inputs, program(target, octets(code*)), result.unsafe[VectorRegisterIndex]),
      expected
    )
  }

  private val minimumF32 = Vec(0x04, 0x00, 0x65, 0xd7, 0x02, 0x07, 0x02, 0x00)
  private val maximumF32 = Vec(0x04, 0x00, 0x66, 0xd7, 0x02, 0x07, 0x02, 0x00)
  private val minimumF16 = Vec(0x04, 0x00, 0x67, 0xd7, 0x02, 0x07, 0x02, 0x00)
  private val maximumF16 = Vec(0x04, 0x00, 0x68, 0xd7, 0x02, 0x07, 0x02, 0x00)

  check(gfx1201, "v_minimum_f32 v4, v2, v3 propagates NaN", minimumF32, Vec(NaN32, single(1.0f)), NaN32)
  check(gfx1201, "v_minimum_f32 v4, v2, v3 orders -0 below +0", minimumF32, Vec(0L, 0x80000000L), 0x80000000L)
  check(
    gfx1201,
    "v_minimum_f32 v4, v2, v3 picks the smaller",
    minimumF32,
    Vec(single(2.0f), single(1.0f)),
    single(1.0f)
  )
  check(gfx1201, "v_maximum_f32 v4, v2, v3 propagates NaN", maximumF32, Vec(single(1.0f), NaN32), NaN32)
  check(gfx1201, "v_maximum_f32 v4, v2, v3 orders +0 above -0", maximumF32, Vec(0x80000000L, 0L), 0L)
  check(gfx1201, "v_minimum_f16 v4, v2, v3 propagates NaN", minimumF16, Vec(half(1.0f), NaN16), NaN16)
  check(gfx1201, "v_maximum_f16 v4, v2, v3 orders +0 above -0", maximumF16, Vec(0x8000L, 0L), 0L)
  check(gfx1201, "v_maximum_f16 v4, v2, v3 picks the larger", maximumF16, Vec(half(-3.0f), half(2.0f)), half(2.0f))
  check(
    gfx1201,
    "v_minimum_f32 v4, -v2, |v3| clamp applies the modifiers and clamp",
    Vec(0x04, 0x82, 0x65, 0xd7, 0x02, 0x07, 0x02, 0x20),
    Vec(single(-0.75f), single(-0.5f)),
    single(0.5f)
  )

  for (
    (name, code, zero) <- Seq(
      ("v_minimum_f64 v[4:5], v[2:3], v[6:7]", Vec(0x04, 0x00, 0x41, 0xd7, 0x02, 0x0d, 0x02, 0x00), -0.0),
      ("v_maximum_f64 v[4:5], v[2:3], v[6:7]", Vec(0x04, 0x00, 0x42, 0xd7, 0x02, 0x0d, 0x02, 0x00), 0.0)
    )
  )
    test(s"$gfx1201 $name orders signed zeros and propagates NaN") {
      def run(a: Double, b: Double): Long = {
        val inputs = Vec(double(a) & 0xffffffffL, double(a) >>> 32, 0L, 0L, double(b) & 0xffffffffL, double(b) >>> 32)
        val operations = program(gfx1201, octets(code*))
        laneResult(gfx1201, inputs, operations, 4) | (laneResult(gfx1201, inputs, operations, 5) << 32)
      }
      assertEquals(run(0.0, -0.0), double(zero))
      assertEquals(run(1.0, Double.NaN), 0x7ff8000000000000L)
    }

  private val minimum3F32 = Vec(0x04, 0x00, 0x2d, 0xd6, 0x02, 0x07, 0x12, 0x04)
  private val maximum3F32 = Vec(0x04, 0x00, 0x2e, 0xd6, 0x02, 0x07, 0x12, 0x04)
  private val minimum3F16 = Vec(0x04, 0x00, 0x2f, 0xd6, 0x02, 0x07, 0x12, 0x04)
  private val maximum3F16 = Vec(0x04, 0x00, 0x30, 0xd6, 0x02, 0x07, 0x12, 0x04)

  check(
    gfx1201,
    "v_minimum3_f32 v4, v2, v3, v4 propagates NaN",
    minimum3F32,
    Vec(single(1.0f), NaN32, single(3.0f)),
    NaN32
  )
  check(
    gfx1201,
    "v_maximum3_f32 v4, v2, v3, v4 picks the largest",
    maximum3F32,
    Vec(single(1.0f), single(5.0f), single(3.0f)),
    single(5.0f)
  )
  check(gfx1201, "v_minimum3_f16 v4, v2, v3, v4 orders -0 below +0", minimum3F16, Vec(0L, half(1.0f), 0x8000L), 0x8000L)
  check(gfx1201, "v_maximum3_f16 v4, v2, v3, v4 propagates NaN", maximum3F16, Vec(half(1.0f), half(2.0f), NaN16), NaN16)

  private val minimumMaximumF32 = Vec(0x04, 0x00, 0x6c, 0xd6, 0x02, 0x07, 0x12, 0x04)
  private val maximumMinimumF32 = Vec(0x04, 0x00, 0x6d, 0xd6, 0x02, 0x07, 0x12, 0x04)
  private val minimumMaximumF16 = Vec(0x04, 0x00, 0x6e, 0xd6, 0x02, 0x07, 0x12, 0x04)
  private val maximumMinimumF16 = Vec(0x04, 0x00, 0x6f, 0xd6, 0x02, 0x07, 0x12, 0x04)

  check(
    gfx1201,
    "v_minimummaximum_f32 v4, v2, v3, v4 is the maximum of the minimum and v4",
    minimumMaximumF32,
    Vec(single(1.0f), single(2.0f), 0L),
    single(1.0f)
  )
  check(
    gfx1201,
    "v_maximumminimum_f32 v4, v2, v3, v4 is the minimum of the maximum and v4",
    maximumMinimumF32,
    Vec(single(1.0f), single(2.0f), 0L),
    0L
  )
  check(
    gfx1201,
    "v_minimummaximum_f16 v4, v2, v3, v4 propagates NaN",
    minimumMaximumF16,
    Vec(NaN16, half(2.0f), half(3.0f)),
    NaN16
  )
  check(
    gfx1201,
    "v_maximumminimum_f16 v4, v2, v3, v4 is the minimum of the maximum and v4",
    maximumMinimumF16,
    Vec(half(1.0f), half(2.0f), 0L),
    0L
  )

  check(
    gfx1201,
    "v_pk_minimum_f16 v4, v2, v3 propagates NaN in each half",
    Vec(0x04, 0x40, 0x1d, 0xcc, 0x02, 0x07, 0x02, 0x18),
    Vec(half(1.0f) | (NaN16 << 16), half(2.0f) | (half(0.5f) << 16)),
    half(1.0f) | (NaN16 << 16)
  )
  check(
    gfx1201,
    "v_pk_maximum_f16 v4, v2, v3 orders +0 above -0 in each half",
    Vec(0x04, 0x40, 0x1e, 0xcc, 0x02, 0x07, 0x02, 0x18),
    Vec(0x8000L | (half(4.0f) << 16), 0L | (half(-4.0f) << 16)),
    0L | (half(4.0f) << 16)
  )

  check(
    gfx950,
    "v_minimum3_f32 v4, v2, v3, v4 propagates NaN",
    Vec(0x04, 0x00, 0xa8, 0xd2, 0x02, 0x07, 0x12, 0x04),
    Vec(single(1.0f), single(2.0f), NaN32),
    NaN32
  )
  check(
    gfx950,
    "v_maximum3_f32 v4, v2, v3, v4 orders +0 above -0",
    Vec(0x04, 0x00, 0xa9, 0xd2, 0x02, 0x07, 0x12, 0x04),
    Vec(0x80000000L, 0L, 0x80000000L),
    0L
  )
  check(
    gfx950,
    "v_pk_minimum3_f16 v4, v2, v3, v4 takes the smallest of each half and propagates NaN",
    Vec(0x04, 0x40, 0x9b, 0xd3, 0x02, 0x07, 0x12, 0x1c),
    Vec(half(1.0f) | (half(5.0f) << 16), half(2.0f) | (NaN16 << 16), half(-1.0f) | (half(6.0f) << 16)),
    half(-1.0f) | (NaN16 << 16)
  )
  check(
    gfx950,
    "v_pk_maximum3_f16 v4, v2, v3, v4 takes the largest of each half",
    Vec(0x04, 0x40, 0x9c, 0xd3, 0x02, 0x07, 0x12, 0x1c),
    Vec(half(1.0f) | (0x8000L << 16), half(2.0f) | (0x8000L << 16), half(-1.0f) | (0L << 16)),
    half(2.0f)
  )
}

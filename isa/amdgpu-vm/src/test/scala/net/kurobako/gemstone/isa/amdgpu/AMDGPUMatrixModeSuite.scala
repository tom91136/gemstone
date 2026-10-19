package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** The matrix core under MODE (MI300 ISA, 7.1 and 7.3): MFMA has no CLAMP field, so MODE.FP16_OVFL clamps it, an
  * overflowing f32 result saturating to the largest finite magnitude while infinite operands keep their infinities, and
  * an i32 result saturating instead of wrapping; the f32 forms flush denormal A and B elements unless MODE allows
  * binary32 input denormals, while C and D keep theirs. Bytes from ROCm 7.1.1 `llvm-mc -triple=amdgcn -mcpu=gfx942
  * -show-encoding` for `v_mfma_f32_4x4x1_16b_f32 v[4:7], v2, v3, v[4:7]` and `v_mfma_i32_4x4x4_16b_i8 v[4:7], v2, v3,
  * v[4:7]`.
  */
class AMDGPUMatrixModeSuite extends munit.FunSuite {
  private val target = Target("gfx942")
  private val single = Vec(0x04, 0x00, 0xc2, 0xd3, 0x02, 0x07, 0x12, 0x04)
  private val bytes  = Vec(0x04, 0x00, 0xd2, 0xd3, 0x02, 0x07, 0x12, 0x04)

  /** D's first element, every lane's A, B and C being `a`, `b` and `c`, after MODE's bits from `offset` take `value`.
    */
  private def product(code: Vec[Int], a: Long, b: Long, c: Long, offset: Int, value: Long): Long =
    laneResults(
      target,
      Vec.fill(64)(Vec(a, b, c, c, c, c)),
      Instruction.SSetMode(offset.unsafe[Int in 0 ~ 31], 1, InstructionSource.Constant(value)) +:
        program(target, octets(code*)),
      4,
      wave32 = false
    ).head

  test("MODE.FP16_OVFL saturates an f32 MFMA overflow but keeps infinite operands' infinities") {
    val big = 0x71800000L
    assertEquals(product(single, big, big, 0L, 23, 0L), 0x7f800000L)
    assertEquals(product(single, big, big, 0L, 23, 1L), 0x7f7fffffL)
    assertEquals(product(single, big | 0x80000000L, big, 0L, 23, 1L), 0xff7fffffL)
    assertEquals(product(single, 0x7f800000L, big, 0L, 23, 1L), 0x7f800000L)
  }

  test("MODE.FP16_OVFL saturates an i32 MFMA instead of wrapping") {
    assertEquals(product(bytes, 0x7f7f7f7fL, 0x7f7f7f7fL, 0x7fffffffL, 23, 0L), 0x8000fc03L)
    assertEquals(product(bytes, 0x7f7f7f7fL, 0x7f7f7f7fL, 0x7fffffffL, 23, 1L), 0x7fffffffL)
  }

  test("f32 MFMA flushes denormal A and B unless MODE allows binary32 input denormals") {
    assertEquals(product(single, 0x00200000L, 0x71800000L, 0L, 4, 1L), 0x31800000L)
    assertEquals(product(single, 0x00200000L, 0x71800000L, 0L, 4, 0L), 0L)
    assertEquals(product(single, 0L, 0L, 0x00200000L, 4, 0L), 0x00200000L)
  }
}

package net.kurobako.gemstone.isa.nvidia

import TensorMap.Element.*
import TensorMap.Reduction.*

/** Tensor reductions on bfloat16 elements. The PTX ISA leaves the NaN of a `.min` or `.max` between two NaNs open. */
class TensorMapSuite extends munit.FunSuite {
  private val one      = 0x3f80L
  private val negative = 0xbf80L
  private val quiet    = 0x7fc1L
  private val signal   = 0xff81L

  private def nan(bits: Option[Long]): Boolean =
    bits.exists(value => (value & 0x7f80L) == 0x7f80L && (value & 0x7fL) != 0L)

  test("bf16 minimum and maximum keep the number against a NaN, either side") {
    assertEquals(Minimum(BF16, quiet, one), Some(one))
    assertEquals(Minimum(BF16, one, signal), Some(one))
    assertEquals(Maximum(BF16, signal, negative), Some(negative))
    assertEquals(Maximum(BF16, negative, quiet), Some(negative))
  }

  test("bf16 minimum and maximum order -0 below +0") {
    assertEquals(Minimum(BF16, old = 0x0000L, operand = 0x8000L), Some(0x8000L))
    assertEquals(Maximum(BF16, old = 0x8000L, operand = 0x0000L), Some(0x0000L))
    assertEquals(Minimum(BF16, one, negative), Some(negative))
  }

  test("bf16 minimum and maximum of two NaNs are a NaN") {
    assert(nan(Minimum(BF16, quiet, signal)))
    assert(nan(Maximum(BF16, signal, quiet)))
  }
}

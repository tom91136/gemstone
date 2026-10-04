package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.constraint.*
import scala.compiletime.testing.typeCheckErrors

class WidthPropagationSuite extends munit.FunSuite {
  test("combined widths retain finite membership") {
    for ((part, expected) <- Seq[(Int in (16, 32), Int)]((16, 32), (32, 64))) {
      val whole: Int in (32, 64) = part.combined
      assertEquals(whole.raw, expected)
      assertEquals(whole.widen[Int where (Between[8, 64] & MultipleOf[8])].byteCount.raw, expected / 8)
    }
    assertEquals(
      typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import VMIR.*
      val part: Int in (16, 32) = 16
      val whole: Int in (32, 64) = part.combined.widen[Int in (32, 64)]
    """),
      Nil
    )
  }
}

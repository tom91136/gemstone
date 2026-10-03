package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.compiletime.testing.typeCheckErrors

class ConstraintRangeSuite extends munit.FunSuite {
  private inline def assertUnsatisfiable(inline code: String): Unit =
    assert(typeCheckErrors(code).exists(_.message.contains("Unsatisfiable constraint")))

  test("range precedence and tuple choices preserve the existing predicate types") {
    summon[(Int in 1 ~ 3) =:= (Int where Between[1, 3])]
    summon[(Int in (1, 3)) =:= (Int where In[(1, 3)])]
    val low: Int in 1 ~ 3        = 1
    val mid: Int in 1 ~ 3        = 2
    val high: Int in 1 ~ 3       = 3
    val wide: Long in 0L ~ 4L    = mid
    val negative: Int in -3 ~ -1 = -2
    val singleton: Int in 2 ~ 2  = 2
    assertEquals(List(low.raw, mid.raw, high.raw), List(1, 2, 3))
    assertEquals(wide.raw, 2L)
    assertEquals(negative.raw, -2)
    assertEquals(singleton.raw, 2)
    for (value <- 0 to 4)
      assertEquals(value.refine[Int in 1 ~ 3].isRight, value >= 1 && value <= 3)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int in 1 ~ 3 = 0
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int in 1 ~ 3 = 4
    """).nonEmpty)
  }

  test("reversed ranges and contradictory predicates fail even for runtime inputs") {
    assertUnsatisfiable("""
      import net.kurobako.gemstone.constraint.*
      def check(value: Int) = value.unsafe[Int in 3 ~ 0]
    """)
    assertUnsatisfiable("""
      import net.kurobako.gemstone.constraint.*
      def check(value: Int) = value.refine[Int where (AtLeast[3] & AtMost[0])]
    """)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int in 3 ~ 0 = 2
    """).nonEmpty)
    assertUnsatisfiable("""
      import net.kurobako.gemstone.constraint.*
      def check(value: Int) = value.refine[Int where (GreaterThan[3] & LessThan[4])]
    """)
  }

  test("satisfiability includes carrier bounds, choices and divisibility") {
    assertUnsatisfiable("""
      import net.kurobako.gemstone.constraint.*
      def check(value: Byte) = value.refine[Byte in 128 ~ 255]
    """)
    assertUnsatisfiable("""
      import net.kurobako.gemstone.constraint.*
      Int.MaxValue.unsafe[Int > 2147483647L]
    """)
    assertUnsatisfiable("""
      import net.kurobako.gemstone.constraint.*
      Long.MinValue.unsafe[Long < Long.MinValue.type]
    """)
    assertUnsatisfiable("""
      import net.kurobako.gemstone.constraint.*
      Long.MaxValue.unsafe[Long > Long.MaxValue.type]
    """)
    assertUnsatisfiable("""
      import net.kurobako.gemstone.constraint.*
      def check(value: Int) = value.refine[Int where (In[(1, 3)] & AtLeast[4])]
    """)
    assertUnsatisfiable("""
      import net.kurobako.gemstone.constraint.*
      def check(value: Int) = value.refine[Int where (Between[1, 3] & MultipleOf[4])]
    """)
    // Bounds may extend beyond the carrier if their intersection still contains values.
    assertEquals(127.toByte.unsafe[Byte in 0 ~ 255].raw, 127.toByte)
    assertEquals(0.unsafe[Int where (Between[-1, 1] & MultipleOf[4])].raw, 0)
  }

  test("proofs reject empty sources and targets") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.constraint.auto.*
      def widen(value: Int in 3 ~ 0): Long >= 0 = value
    """).nonEmpty)
    assertUnsatisfiable("""
      import net.kurobako.gemstone.constraint.*
      def widen(value: Int in 3 ~ 0) = value.widen[Int >= 0]
    """)
    assertUnsatisfiable("""
      import net.kurobako.gemstone.constraint.*
      def narrow(value: Int in 1 ~ 3) = value.widen[Int in 3 ~ 0]
    """)
  }
}

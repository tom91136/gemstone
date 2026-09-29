package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.compiletime.testing.typeCheckErrors
import scala.util.Try

class ConstraintPredicateSuite extends munit.FunSuite {
  test("unsafe supports explicit and context-inferred complete target types") {
    case class BudgetExceeded(limit: Long > 0)
    val limit = java.lang.Long.parseLong("12")
    assertEquals(BudgetExceeded(limit.unsafe).limit.raw, 12L)
    assertEquals(limit.unsafe[Long > 0].raw, 12L)
    val zero: Int >= 0 = 0.unsafe
    assertEquals(zero.raw, 0)
    val _ = intercept[IllegalArgumentException](0.unsafe[Int > 0])
    val _ = intercept[IllegalArgumentException]((-1L).unsafe[Long >= 0])
  }

  test("unsafe requires the exact underlying representation") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      1.unsafe[Long > 0]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      1L.unsafe[Int > 0]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      "1".unsafe[Int > 0]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      1.unsafe[String]
    """).nonEmpty)
    assertEquals(1.toLong.unsafe[Long > 0].raw, 1L)
  }

  test("proven implications and numeric widening remain implicit") {
    val positive                       = 3.unsafe[Int > 0]
    val nonnegative: Int >= 0          = positive
    val negative                       = (-1).unsafe[Int < 0]
    val nonpositive: Int <= 0          = negative
    def offset(value: Long >= 0): Long = value.raw
    assertEquals(offset(positive), 3L)
    assertEquals(nonnegative.raw, 3)
    assertEquals(nonpositive.raw, -1)
    assertEquals(positive.widen[AtLeast[0]].raw, 3)
    assertEquals(positive.toLong.raw, 3L)
    val bounded                                         = 24.unsafe[Int where (GreaterThan[10] & AtMost[80])]
    val wider: Long where (AtLeast[10] & LessThan[100]) = bounded
    assertEquals(wider.raw, 24L)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.constraint.auto.*
      def strengthen(value: Int >= 0): Int > 0 = value
    """).nonEmpty)
  }

  test("finite choices and divisibility participate in implications") {
    val bits                                 = 32.unsafe[Int in (16, 32, 64)]
    val bytes                                = (bits.raw / 8).unsafe[OneOf[(2, 4, 8)]]
    val wide: Long in (2L, 4L, 8L)           = bytes
    val bounded: Int where Between[1, 8]     = bytes
    val choices: OneOf[(8, 16, 32, 64, 128)] = bits
    val aligned: Int where MultipleOf[8]     = bits
    val half: Int where MultipleOf[4]        = aligned
    assertEquals(wide.raw, 4L)
    assertEquals(bounded.raw, 4)
    assertEquals(choices.raw, 32)
    assertEquals(half.raw, 32)
    val _ = intercept[IllegalArgumentException](3.unsafe[OneOf[(2, 4, 8)]])
  }

  test("arithmetic preserves signs and checks overflow and negative differences") {
    val x = 3.unsafe[Int > 0]
    val y = 2.unsafe[Int >= 0]
    assertEquals((x + y).raw, 5)
    assertEquals((x * x).raw, 9)
    assertEquals((y * x).raw, 6)
    assertEquals(x.min(x).raw, 3)
    assertEquals(y.max(x).raw, 3)
    assertEquals((y - 1.unsafe).raw, 1)
    assertEquals((x + 1.unsafe).raw, 4)
    assert(x > y)
    assert(x >= 0.unsafe[Int >= 0])
    val _ = intercept[IllegalArgumentException](y - x)
    val _ = intercept[ArithmeticException](Int.MaxValue.unsafe[Int > 0] + x)
    val _ = intercept[ArithmeticException](Int.MaxValue.unsafe[Int >= 0] * y)
    val a = 3L.unsafe[Long > 0]
    val b = 2L.unsafe[Long >= 0]
    assertEquals((a + b).raw, 5L)
    assertEquals((b * a).raw, 6L)
    assertEquals(b.min(a).raw, 2L)
    assertEquals(a.max(a).raw, 3L)
    assertEquals((b - 1L.unsafe).raw, 1L)
    val _ = intercept[IllegalArgumentException](b - a)
    val _ = intercept[ArithmeticException](Long.MaxValue.unsafe[Long >= 0] + b)
    val _ = intercept[ArithmeticException](Long.MaxValue.unsafe[Long > 0] * a)
  }

  test("generated checks agree with predicates over negative and positive values") {
    for (value <- -80 to 80) {
      assertEquals(
        Try(value.unsafe[Int where (AtLeast[-20] & AtMost[20] & MultipleOf[6])]).isSuccess,
        value >= -20 && value <= 20 && value % 6 == 0
      )
      assertEquals(
        Try(value.unsafe[Int where (In[(-3, 2, 4, 8)] & AtLeast[0])]).isSuccess,
        Set(2, 4, 8).contains(value)
      )
      assertEquals(Try(value.unsafe[Int where (AtLeast[1] & AtMost[0])]).isSuccess, false)
      assertEquals(Try(value.unsafe[Int where (MultipleOf[4] & MultipleOf[6])]).isSuccess, value % 12 == 0)
    }
  }

  test("endpoint predicates and large divisors do not overflow during validation") {
    assert(Try(Int.MaxValue.unsafe[Int > 2147483647L]).isFailure)
    assert(Try(Long.MinValue.unsafe[Long < Long.MinValue.type]).isFailure)
    assert(Try(Long.MaxValue.unsafe[Long > Long.MaxValue.type]).isFailure)
    assert(Try(Long.MinValue.unsafe[Long where MultipleOf[2]]).isSuccess)
    assert(Try(Long.MaxValue.unsafe[Long where MultipleOf[2]]).isFailure)
    assert(Try(0L.unsafe[Long where (MultipleOf[4294967296L] & MultipleOf[4294967295L])]).isSuccess)
    assert(Try(1L.unsafe[Long where (MultipleOf[4294967296L] & MultipleOf[4294967295L])]).isFailure)
    assertEquals(Long.MinValue.unsafe[Long <= Long.MinValue.type].raw, Long.MinValue)
    assertEquals(Long.MaxValue.unsafe[Long >= Long.MaxValue.type].raw, Long.MaxValue)
  }

  test("validation evaluates runtime expressions once") {
    val calls = java.util.concurrent.atomic.AtomicInteger(0)
    val value = calls.incrementAndGet().unsafe[Int where Between[1, 8]]
    assertEquals(value.raw, 1)
    assertEquals(calls.get(), 1)
  }

  test("opaque numeric values cannot silently drop predicates") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def unwrap(value: Int > 0): Int = value
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def primitive(value: Int > 0): Long = value.toLong
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def divide(value: Int > 0): Int > 0 = value / 2
    """).nonEmpty)
  }

  test("malformed and abstract predicates fail during compilation") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      1.unsafe[Int where (LessThan[0] | GreaterThan[0])]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      1.unsafe[Int where MultipleOf[0]]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      1.unsafe[OneOf[(Int, 2)]]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      1.unsafe[OneOf[(1, "two")]]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      1.0.unsafe[Double where GreaterThan[0]]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def refine[P](value: Int) = value.unsafe[Int where P]
    """).nonEmpty)
  }
}

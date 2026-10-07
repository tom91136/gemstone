package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*
import scala.compiletime.testing.typeCheckErrors
import scala.util.Try

/** Proven implications, carrier widening, exact narrowing and sign-preserving arithmetic. */
class ConstraintWidenSuite extends munit.FunSuite {
  test("numeric conversions retain source carrier bounds and predicates") {
    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val b: Byte >= 0 = 1
      b.toShort.widen[Short in 0 ~ 127]
      b.toInt.widen[Int in 0 ~ 127]
      b.toLong.widen[Long in 0L ~ 127L]
      b.toShort.toInt.toLong.widen[Long in 0L ~ 127L]
      val s: Short >= 0 = 1
      s.toInt.widen[Int in 0 ~ 32767]
      s.toLong.widen[Long in 0L ~ 32767L]
      val n: Int >= 0 = 1
      n.toLong.widen[Long in 0L ~ 2147483647L]
      val negative: Byte < 0 = -1
      negative.toInt.widen[Int in -128 ~ -1]
      val aligned: Byte where MultipleOf[8] = 8
      aligned.toLong.widen[Long where (Between[-128, 127] & MultipleOf[8])]
    """)
    assertEquals(errors.map(_.message), Nil)
    val largestByte: Byte >= 0   = 127
    val smallestShort: Short < 0 = -32768
    val largestInt: Int >= 0     = Int.MaxValue
    assertEquals(largestByte.toShort.toInt.toLong.widen[Long in 0L ~ 127L].raw, 127L)
    assertEquals(smallestShort.toLong.widen[Long in -32768L ~ -1L].raw, -32768L)
    assertEquals(largestInt.toLong.widen[Long in 0L ~ 2147483647L].raw, 2147483647L)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val source: Byte >= 0 = 1
      source.toInt.widen[Int in 0 ~ 126]
    """).exists(_.message.contains("Cannot prove")))
  }

  test("complete targets support bounds, ranges and aliases without importing auto") {
    type Small = Int in 1 ~ 3
    type Wider = Int in 0 ~ 8
    val small: Small       = 2
    val inferred: Int >= 0 = small.widen
    assertEquals(small.widen[Int > 0].raw, 2)
    assertEquals(small.widen[Int >= 0].raw, 2)
    assertEquals(small.widen[Int <= 3].raw, 2)
    assertEquals(small.widen[Wider].raw, 2)
    assertEquals(small.widen[Small].raw, 2)
    assertEquals(inferred.raw, 2)
    val choice: Int in (2, 4, 8) = 4
    assertEquals(choice.widen[Int in 1 ~ 8].raw, 4)
    assertEquals(choice.widen[Int where MultipleOf[2]].raw, 4)
  }

  test("target choices jointly cover intervals and stepped domains") {
    import constraint.auto.*
    val interval: Int in 1 ~ 3               = 2
    val implicitChoices: Int in (3, 1, 2, 2) = interval
    assertEquals(implicitChoices.raw, 2)
    assertEquals(interval.widen[Long in (3L, 2L, 1L)].raw, 2L)
    val stepped: Int where (Between[-4, 4] & MultipleOf[2]) = 0
    assertEquals(stepped.widen[Int in (4, -2, 0, -4, 2)].raw, 0)
    val high: Long in 9223372036854775806L ~ 9223372036854775807L = Long.MaxValue
    assertEquals(high.widen[Long in (9223372036854775807L, 9223372036854775806L)].raw, Long.MaxValue)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val source: Int in 1 ~ 3 = 1
      source.widen[Int in (1, 3, 3)]
    """).exists(_.message.contains("Cannot prove")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val source: Long >= 0 = 0L
      source.widen[Long in (0L, 9223372036854775807L)]
    """).exists(_.message.contains("Cannot prove")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val source: Int where (Between[-4, 4] & MultipleOf[2]) = 0
      source.widen[Int in (-4, -2, 0, 4)]
    """).exists(_.message.contains("Cannot prove")))
  }

  test("all lossless numeric carrier widenings retain the target predicates") {
    val byte: Byte in -8 ~ 8   = -3
    val short: Short in -8 ~ 8 = -3
    val int: Int in -8 ~ 8     = -3
    assertEquals(byte.widen[Short in -10 ~ 10].raw, (-3).toShort)
    assertEquals(byte.widen[Int in -10 ~ 10].raw, -3)
    assertEquals(byte.widen[Long in -10L ~ 10L].raw, -3L)
    assertEquals(short.widen[Int in -10 ~ 10].raw, -3)
    assertEquals(short.widen[Long in -10L ~ 10L].raw, -3L)
    assertEquals(int.widen[Long in -10L ~ 10L].raw, -3L)
    val large: Long > 0 = Long.MaxValue
    assertEquals(large.widen[Long >= 0].raw, Long.MaxValue)
  }

  test("widening checks the whole source domain rather than the current value") {
    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int >= 0 = 3
      value.widen[Int > 0]
    """)
    assert(errors.exists(_.message.contains("Cannot prove")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Byte > 0 = 3
      value.widen[Long in 1L ~ 3L]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int in 1 ~ 8 = 2
      value.widen[Int where MultipleOf[2]]
    """).nonEmpty)
  }

  test("carrier narrowing and non-refinement targets are rejected") {
    val narrowing = typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Long in 1L ~ 3L = 2L
      value.widen[Int >= 0]
    """)
    assert(narrowing.exists(_.message.contains("Cannot widen representation")))
    val predicateOnly = typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int > 0 = 3
      value.widen[AtLeast[0]]
    """)
    assert(predicateOnly.exists(_.message.contains("complete numeric refinement target")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int > 0 = 3
      value.widen[Int]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int > 0 = 3
      value.widen[Double where AtLeast[0]]
    """).nonEmpty)
  }

  test("inline generic predicates resolve at the call site and inputs are evaluated once") {
    inline def positive[P](value: Int where P): Long > 0 = value.widen[Long > 0]
    val calls                                            = java.util.concurrent.atomic.AtomicInteger(0)
    def produce(): Int in 1 ~ 3                          = calls.incrementAndGet().unsafe[Int in 1 ~ 3]
    assertEquals(positive(produce()).raw, 1L)
    assertEquals(calls.get(), 1)
  }

  test("proven implications and numeric widening remain implicit") {
    import constraint.auto.*
    val positive                       = 3.unsafe[Int > 0]
    val nonnegative: Int >= 0          = positive
    val negative                       = (-1).unsafe[Int < 0]
    val nonpositive: Int <= 0          = negative
    def offset(value: Long >= 0): Long = value.raw
    assertEquals(offset(positive), 3L)
    assertEquals(nonnegative.raw, 3)
    assertEquals(nonpositive.raw, -1)
    assertEquals(positive.widen[Int >= 0].raw, 3)
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

  test("proven widening preserves ranges across every narrow carrier") {
    import constraint.auto.*
    val byte: Byte in 1 ~ 31                = 17
    val short: Short > 0                    = byte
    val int: Int > 0                        = byte
    val long: Long > 0                      = byte
    val shortInt: Int > 0                   = short
    val shortLong: Long > 0                 = short
    val byteCarrierBound: Int in 1 ~ 127    = 17: Byte > 0
    val shortCarrierBound: Int in 1 ~ 32767 = short
    assertEquals(short.raw, 17.toShort)
    assertEquals(int.raw, 17)
    assertEquals(long.raw, 17L)
    assertEquals(shortInt.raw, 17)
    assertEquals(shortLong.raw, 17L)
    assertEquals(byteCarrierBound.raw, 17)
    assertEquals(shortCarrierBound.raw, 17)
    assertEquals(byte.toShort.raw, 17.toShort)
    assertEquals(byte.toInt.raw, 17)
    assertEquals(byte.toLong.raw, 17L)
    assertEquals(short.toInt.raw, 17)
    assertEquals(short.toLong.raw, 17L)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.constraint.auto.*
      val positive: Byte > 0 = 17
      val small: Int in 1 ~ 16 = positive
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.constraint.auto.*
      val int: Int in 1 ~ 31 = 17
      val byte: Byte in 1 ~ 31 = int
    """).nonEmpty)
  }

  test("finite choices and divisibility participate in implications") {
    import constraint.auto.*
    val bits                                 = 32.unsafe[Int in (16, 32, 64)]
    val bytes                                = (bits.raw / 8).unsafe[Int in (2, 4, 8)]
    val wide: Long in (2L, 4L, 8L)           = bytes
    val bounded: Int in 1 ~ 8                = bytes
    val choices: Int in (8, 16, 32, 64, 128) = bits
    val aligned: Int where MultipleOf[8]     = bits
    val half: Int where MultipleOf[4]        = aligned
    assertEquals(wide.raw, 4L)
    assertEquals(bounded.raw, 4)
    assertEquals(choices.raw, 32)
    assertEquals(half.raw, 32)
    val _ = intercept[IllegalArgumentException](3.unsafe[Int in (2, 4, 8)])
  }

  test("generated checks agree with predicates over negative and positive values") {
    import constraint.auto.*
    for (value <- -80 to 80) {
      assertEquals(
        Try(value.unsafe[Int where (AtLeast[-20] & AtMost[20] & MultipleOf[6])]).isSuccess,
        value >= -20 && value <= 20 && value % 6 == 0
      )
      assertEquals(
        Try(value.unsafe[Int where (In[(-3, 2, 4, 8)] & AtLeast[0])]).isSuccess,
        Set(2, 4, 8).contains(value)
      )
      assertEquals(Try(value.unsafe[Int where (MultipleOf[4] & MultipleOf[6])]).isSuccess, value % 12 == 0)
    }
  }

  test("endpoint predicates and large divisors do not overflow during validation") {
    import constraint.auto.*
    assert(Try(Long.MinValue.unsafe[Long where MultipleOf[2]]).isSuccess)
    assert(Try(Long.MaxValue.unsafe[Long where MultipleOf[2]]).isFailure)
    assert(Try(0L.unsafe[Long where (MultipleOf[4294967296L] & MultipleOf[4294967295L])]).isSuccess)
    assert(Try(1L.unsafe[Long where (MultipleOf[4294967296L] & MultipleOf[4294967295L])]).isFailure)
    assertEquals(Long.MinValue.unsafe[Long <= Long.MinValue.type].raw, Long.MinValue)
    assertEquals(Long.MaxValue.unsafe[Long >= Long.MaxValue.type].raw, Long.MaxValue)
  }

  test("count division retains nonnegativity and positive ceiling division avoids overflow") {
    import constraint.auto.*
    val zero: Int >= 0      = 0
    val count: Int >= 0     = 11
    val group: Int > 0      = 4
    val quotient: Int >= 0  = count / group
    val remainder: Int >= 0 = count % group
    val maximum: Int > 0    = Int.MaxValue
    val ceiling: Int > 0    = maximum.ceilDiv(2)
    assertEquals(quotient.raw, 2)
    assertEquals(remainder.raw, 3)
    assertEquals((zero / group).raw, 0)
    assertEquals((zero % group).raw, 0)
    assertEquals(ceiling.raw, 1073741824)
    assertEquals(group.ceilDiv(group).raw, 1)
    val longCount: Long >= 0  = 11L
    val longGroup: Long > 0   = 4L
    val longMaximum: Long > 0 = Long.MaxValue
    assertEquals((longCount / longGroup).raw, 2L)
    assertEquals((longCount % longGroup).raw, 3L)
    assertEquals(longMaximum.ceilDiv(2L).raw, 4611686018427387904L)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val count: Int >= 0 = 1
      count / 0
    """).nonEmpty)
  }

  test("arithmetic preserves signs and checks overflow and negative differences") {
    import constraint.auto.*
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

  test("positive maxima retain their lower bound with nonnegative counts") {
    import constraint.auto.*
    val minimum: Int > 0      = 1
    val empty: Int >= 0       = 0
    val size: Int >= 0        = 8
    val emptyMaximum: Int > 0 = minimum.max(empty)
    val maximum: Int > 0      = minimum.max(size)
    val longMaximum: Long > 0 = minimum.toLong.widen[Long > 0].max(size.toLong)
    assertEquals(emptyMaximum.raw, 1)
    assertEquals(maximum.raw, 8)
    assertEquals(longMaximum.raw, 8L)
    assertEquals(minimum.toLong.widen[Long > 0].max(empty.toLong).raw, 1L)
  }

  test("checked Long narrowing preserves signs and rejects overflow") {
    import constraint.auto.*
    val zero: Long >= 0           = 0L
    val positive: Long > 0        = 2147483647L
    val tooLarge: Long > 0        = 2147483648L
    val narrowedZero: Int >= 0    = zero.toIntExact
    val narrowedPositive: Int > 0 = positive.toIntExact
    assertEquals(narrowedZero.raw, 0)
    assertEquals(narrowedPositive.raw, Int.MaxValue)
    val _ = intercept[ArithmeticException](tooLarge.toIntExact)
  }

  test("exact narrowing preserves predicates and rejects carrier overflow") {
    import constraint.auto.*
    val lower: Int in -32768 ~ 32767 = -32768
    assertEquals(lower.toShortExact.raw, (-32768).toShort)
    val _ = intercept[ArithmeticException]((32768: Int >= 0).toShortExact)
    val _ = intercept[ArithmeticException]((-32769: Int < 0).toShortExact)
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
}

package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*
import scala.compiletime.testing.typeCheckErrors

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
    val largestByte: Byte >= 0 = 127
    val smallestShort: Short < 0 = -32768
    val largestInt: Int >= 0 = Int.MaxValue
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
    val interval: Int in 1 ~ 3 = 2
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
}

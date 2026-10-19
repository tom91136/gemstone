package net.kurobako.gemstone

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import scala.compiletime.testing.typeCheckErrors

/** Compile-time literal checks, string proofs and domain satisfiability. */
class ConstraintLiteralSuite extends munit.FunSuite {
  private inline def assertUnsatisfiable(inline code: String): Unit =
    assert(typeCheckErrors(code).exists(_.message.contains("Unsatisfiable constraint")))

  test("literal arguments are checked at compile time") {
    val positive: Int > 0                                   = 4
    val nonnegative: Long >= 0                              = 0L
    val choice: Int in (16, 32, 64)                         = 32
    val aligned: Int where (Between[0, 64] & MultipleOf[8]) = 24
    val name: NonEmpty[String]                              = "kernel"
    val text: NonEmpty[String]                              = "text"
    assertEquals(positive.raw, 4)
    assertEquals(nonnegative.raw, 0L)
    assertEquals(choice.raw, 32)
    assertEquals(aligned.raw, 24)
    assert(name == "kernel")
    assert(text == "text")
    assertEquals(LittleEndian.toVec(0x12345678L, 4), Seq(0x78, 0x56, 0x34, 0x12).map(_.toByte))
  }

  test("Byte and Short literals retain their carriers and predicates") {
    val lane: Byte in 0 ~ 31                                     = 31
    val index: Short in 0 ~ 1023                                 = 1023
    val negative: Byte in -128 ~ -1                              = -128
    val aligned: Short where (Between[0, 1024] & MultipleOf[16]) = 512
    assertEquals(lane.raw, 31.toByte)
    assertEquals(index.raw, 1023.toShort)
    assertEquals(negative.raw, (-128).toByte)
    assertEquals(aligned.raw, 512.toShort)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val lane: Byte in 0 ~ 31 = 32
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Byte >= 0 = 128
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Short >= 0 = 32768
    """).nonEmpty)
  }

  test("invalid literals are rejected") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int > 0 = 0
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int in 0 ~ 8 = 9
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int in (16, 32, 64) = 8
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int where MultipleOf[8] = 7
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: NonEmpty[String] = ""
    """).nonEmpty)
  }

  test("literal conversions preserve the numeric representation") {
    val widened: Long > 0 = 1
    assertEquals(widened.raw, 1L)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int > 0 = 1L
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int > 0 = "1"
    """).nonEmpty)
  }

  test("interpolation proves nonemptiness with arbitrary string arguments") {
    def hex(value: Long): NonEmpty[String] = f"hex 0x$value%08x"
    assert(hex(4) == "hex 0x00000004")
    def diagnostic(input: String): NonEmpty[String]                     = s"invalid input: $input"
    def dimensions(size: Int, name: NonEmpty[String]): NonEmpty[String] = s"$name has $size elements"
    assert(diagnostic("") == "invalid input: ")
    assert(diagnostic("a\u0000b") == "invalid input: a\u0000b")
    assert(dimensions(4, "kernel") == "kernel has 4 elements")
    val calls                  = java.util.concurrent.atomic.AtomicInteger(0)
    val once: NonEmpty[String] = s"count ${calls.incrementAndGet()}"
    assert(once == "count 1")
    assertEquals(calls.get(), 1)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def empty(input: String): NonEmpty[String] = s"$input"
    """).nonEmpty)
  }

  test("formatted and concatenated strings cannot bypass string checks") {
    def joined(input: String): NonEmpty[String] = "input: " + input
    assert(joined("") == "input: ")
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def empty(input: String): NonEmpty[String] = f"$input%s"
    """).nonEmpty)
    def character(input: Int): NonEmpty[String] = f"character: $input%c"
    assert(character(0) == "character: \u0000")
    assert(joined("a\u0000b") == "input: a\u0000b")
  }

  test("range precedence and tuple choices preserve the existing predicate types") {
    import constraint.auto.*
    summon[(Int in 1 ~ 3) =:= (Int where Between[1, 3])]
    summon[(Int in (1, 3)) =:= (Int where Membership[(1, 3)])]
    summon[(Int in (0 ~ 17) | (24 ~ 31)) =:= (Int where Membership[(24 ~ 31) | (0 ~ 17)])]
    val low: Int in 1 ~ 3        = 1
    val mid: Int in 1 ~ 3        = 2
    val high: Int in 1 ~ 3       = 3
    val wide: Long in 0L ~ 4L    = mid
    val negative: Int in -3 ~ -1 = -2
    val singleton: Int in 2 ~ 2  = 2
    assertEquals(List[Int](low, mid, high), List(1, 2, 3))
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
      def check(value: Int) = value.refine[Int where (Membership[(1, 3)] & AtLeast[4])]
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

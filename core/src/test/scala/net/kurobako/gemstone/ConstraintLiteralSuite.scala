package net.kurobako.gemstone

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import scala.compiletime.testing.typeCheckErrors

class ConstraintLiteralSuite extends munit.FunSuite {
  private object Count       extends Newtype[Int > 0]
  private object Name        extends Newtype[NonEmpty[String]]
  private object NonzeroPair extends Newtype[(Long, Long)] {
    override protected def validate(value: (Long, Long)): Unit =
      require(value != (0L, 0L), "zero pair")
  }

  test("newtypes share checked construction without losing the carrier refinement") {
    val count = 4
    assertEquals(count.unsafe[Count.Type].raw, 4)
    assertEquals(Count.either(count).toOption.get.raw, 4)
    assert(Count.either(0).isLeft)
    val _ = intercept[IllegalArgumentException](0.unsafe[Count.Type])
    assert(Name.either("").isLeft)
    assert(Name.either("a\u0000b").isRight)
    assertEquals("kernel".unsafe[Name.Type].raw: String, "kernel")
    assert(NonzeroPair.either((0L, 0L)).isLeft)
    val _ = intercept[IllegalArgumentException](NonzeroPair((0L, 0L)))
    assertEquals((1L, 0L).unsafe[NonzeroPair.Type].raw, (1L, 0L))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      object Count extends Newtype[Int > 0]
      1L.unsafe[Count.Type]
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

  test("literal arguments and newtypes are checked at compile time") {
    val positive: Int > 0                                   = 4
    val nonnegative: Long >= 0                              = 0L
    val choice: OneOf[(16, 32, 64)]                         = 32
    val aligned: Int where (Between[0, 64] & MultipleOf[8]) = 24
    val name: NonEmpty[String]                              = "kernel"
    val text: NonEmpty[String]                              = "text"
    object Count extends Newtype[Int > 0]
    assertEquals(Count(4).raw, positive.raw)
    assertEquals(nonnegative.raw, 0L)
    assertEquals(choice.raw, 32)
    assertEquals(aligned.raw, 24)
    assert(name == "kernel")
    assert(text == "text")
    assertEquals(LittleEndian.toVec(0x12345678L, 4), Seq(0x78, 0x56, 0x34, 0x12).map(_.toByte))
  }

  test("invalid literals are rejected") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int > 0 = 0
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int where Between[0, 8] = 9
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: OneOf[(16, 32, 64)] = 8
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
}

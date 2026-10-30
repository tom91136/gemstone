package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.testing.Suite
import scala.compiletime.testing.typeCheckErrors

/** Integer views, unions of intervals and floating-point bounds. */
class ConstraintMembershipSuite extends Suite {
  test("prelude integers refine through their integer view") {
    type Register = UInt[5] in (0 ~ 17) | (24 ~ 31)
    val register = UInt[5](17).unsafe[Register]
    val sum      = register + UInt[5](1)
    assertEquals(toRaw(register), 17L)
    assertEquals(toRaw(sum), 18L)
    assert(UInt[5](20).refine[Register].isLeft)
    assert(UInt[5](24).refine[Register].isRight)
    assertEquals(toRaw(SInt[17](-4).unsafe[SInt[17] >= -4]), -4L)
    assert(SInt[17](-5).refine[SInt[17] >= -4].isLeft)
    assert(UInt[5](31).refine[UInt[5] in 0 ~ 40].isRight)
    assertEquals(toRaw(register.widen[UInt[5] in 0 ~ 31]), 17L)
    assertEquals(toRaw(fromRaw[UInt[64]](-1L).unsafe[UInt[64] >= 1]), -1L)
    assert(fromRaw[UInt[64]](-1L).refine[UInt[64] <= 100].isLeft)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.semantics.prelude.*
      UInt[5](1).unsafe[UInt[5] in 32 ~ 40]
    """).exists(_.message.contains("Unsatisfiable")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.semantics.prelude.*
      UInt[5](1).unsafe[UInt[5] in 0 ~ 17].widen[UInt[5] in 0 ~ 16]
    """).exists(_.message.contains("Cannot prove")))
  }

  test("a union admits any of its intervals, literals and tuples") {
    type Encoded = Int in (0 ~ 17) | (24 ~ 31)
    for (value <- -2 to 40)
      assertEquals(value.refine[Encoded].isRight, (value >= 0 && value <= 17) || (value >= 24 && value <= 31))
    val encoded: Encoded                   = 25
    val choice: Int in 1 | 2 | 4           = 4
    val mixed                              = 9.unsafe[Int in (1, 2) | (8 ~ 9)]
    val reordered: Int in 24 ~ 31 | 0 ~ 17 = 3
    assertEquals(List[Int](encoded, choice, mixed, reordered), List(25, 4, 9, 3))
    assert(5.refine[Int in 1 | 2 | 4].isLeft)
    assert(3.refine[Int in (1, 2) | (8 ~ 9)].isLeft)
    assertEquals(encoded.widen[Int in 0 ~ 31].raw, 25)
    assertEquals(3.unsafe[Int in (0 ~ 3) | (4 ~ 7)].widen[Int in 0 ~ 7].raw, 3)
    assertEquals(choice.widen[Int in (0 ~ 2) | (4 ~ 4)].raw, 4)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val encoded: Int in (0 ~ 17) | (24 ~ 31) = 20
    """).exists(_.message.contains("does not satisfy")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val wide: Int in 0 ~ 31 = 3
      wide.widen[Int in (0 ~ 17) | (24 ~ 31)]
    """).exists(_.message.contains("Cannot prove")))
  }

  test("floating-point bounds compare as IEEE 754 does, and NaN fails every bound") {
    val unit: Double in 0.0 ~ 1.0 = 0.5
    val nonnegative: Float >= 0   = 1.5f
    assertEquals(unit.raw, 0.5)
    assertEquals(nonnegative.raw, 1.5f)
    assert(Double.NaN.refine[Double in 0.0 ~ 1.0].isLeft)
    assert(Double.NaN.refine[Double < 1.0].isLeft)
    assert(Double.NaN.refine[Double where AtMost[1.0]].isLeft)
    assert(Float.NaN.refine[Float >= 0].isLeft)
    assert(Float.NaN.refine[Float in (-1.0 ~ -0.5) | (0.5 ~ 1.0)].isLeft)
    assert(1.5.refine[Double in 0.0 ~ 1.0].isLeft)
    assert((-0.0).refine[Double >= 0].isRight)
    assert(0.0.refine[Double > 0].isLeft)
    assert(Double.MinPositiveValue.refine[Double > 0].isRight)
    assert(Double.PositiveInfinity.refine[Double >= 0].isRight)
    assert(Double.PositiveInfinity.refine[Double in 0.0 ~ 1.0].isLeft)
    assert(0.1f.refine[Float >= 0.1].isRight)
    assert(Math.nextDown(0.1f).refine[Float >= 0.1].isLeft)
    assert(0.0.refine[Double in (-1.0 ~ -0.5) | (0.5 ~ 1.0)].isLeft)
    assert(0.75.refine[Double in (-1.0 ~ -0.5) | (0.5 ~ 1.0)].isRight)
    assertEquals(unit.widen[Double >= 0].raw, 0.5)
    assertEquals(unit.widen[Double in -1.0 ~ 2.0].raw, 0.5)
    val plain: Double = unit
    assertEquals(plain, 0.5)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val unit: Double in 0.0 ~ 1.0 = 0.5
      unit.widen[Double > 0]
    """).exists(_.message.contains("Cannot prove")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val unit: Double in 0.0 ~ 1.0 = 1.5
    """).exists(_.message.contains("does not satisfy")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      1.0.unsafe[Double where MultipleOf[2]]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      0.1f.unsafe[Float in 0.1 ~ 0.1]
    """).exists(_.message.contains("Unsatisfiable")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      1.unsafe[Int >= 0.5]
    """).nonEmpty)
  }

  test("integer bounds on a floating carrier must be exactly representable") {
    assert(9007199254740992.0.refine[Double >= 9007199254740992L].isRight)
    assert(9007199254740991.0.refine[Double >= 9007199254740992L].isLeft)
    assert(16777216f.refine[Float >= 16777216].isRight)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      9007199254740992.0.refine[Double >= 9007199254740993L]
    """).exists(_.message.contains("not exactly representable")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      0.0.refine[Double in -9007199254740993L ~ 0]
    """).exists(_.message.contains("not exactly representable")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      16777216f.refine[Float > 16777217]
    """).exists(_.message.contains("not exactly representable")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      0f.refine[Float <= 2147483647]
    """).exists(_.message.contains("not exactly representable")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      0.0.refine[Double <= 9223372036854775807L]
    """).exists(_.message.contains("not exactly representable")))
  }

  test("comparing a refined value with a plain one is a compile error, while refined values compare as the carrier") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val plain: Double         = 0.0
      val refined: Double >= 0 = 0.0
      plain == refined
    """).exists(_.message.contains("cannot be compared")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val plain: Double         = 0.0
      val refined: Double >= 0 = 0.0
      refined == plain
    """).exists(_.message.contains("cannot be compared")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val plain: Int         = 1
      val refined: Int >= 0 = 1
      plain != refined
    """).exists(_.message.contains("cannot be compared")))
    val negative: Double >= 0 = -0.0
    assert(negative.raw == 0.0)
    val count: Int > 0  = 3
    val other: Int >= 0 = 3
    assert(count == other)
  }

  test("a refined integer compares with integer literals and matches literal patterns without unrefining") {
    val width: Int in (16, 32, 64) = 32
    val count: Long >= 0           = 0
    val small: Byte >= 0           = 3
    assert(width == 32 && width != 16 && 64 != width && -1 != width && width == 0x20)
    assert(count == 0 && count == 0L && small == 3)
    assert(Option(width).exists(value => value == 16 || value == 32))
    val named = width match {
      case 16      => "half"
      case 32 | 64 => "wide"
    }
    val paired = (width, count) match {
      case (64, _) => "double"
      case (32, 0) => "empty"
      case _       => "other"
    }
    assertEquals(named, "wide")
    assertEquals(paired, "empty")
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val width: Int in (16, 32, 64) = 32
      val plain                      = 32
      width == plain
    """).exists(_.message.contains("cannot be compared")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      object Widths { final val Wide = 32 }
      val width: Int in (16, 32, 64) = 32
      width == Widths.Wide
    """).exists(_.message.contains("cannot be compared")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val width: Int in (16, 32, 64) = 32
      val plain                      = 1
      width == 31 + plain || 31 + plain == width
    """).exists(_.message.contains("cannot be compared")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val width: Int in (16, 32, 64) = 32
      val plain                      = 1
      width == plain // == 32
    """).exists(_.message.contains("cannot be compared")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      object Widths { final val Wide = 32 }
      val width: Int in (16, 32, 64) = 32
      width match {
        case Widths.Wide => true
        case _           => false
      }
    """).exists(_.message.contains("cannot be compared")))
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val unit: Double in 0.0 ~ 1.0 = 0.5
      unit == 1
    """).exists(_.message.contains("cannot be compared")))
  }

  test("a refined value converts to its carrier, while the carrier still needs validation to refine") {
    val positive: Int > 0      = 3
    val digits                 = positive.toString
    def plain(value: Int): Int = value
    val bases: Vector[Int]     = Vector(positive)
    assertEquals(digits, "3")
    assertEquals(plain(positive), 3)
    assertEquals(bases, Vector(3))
    assertEquals(positive.unsafe[Int in 1 ~ 3].raw, 3)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val positive: Int > 0 = 3
      val count: Long = positive
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def refine(value: Int): Int >= 0 = value
    """).nonEmpty)
  }
}

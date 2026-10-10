package net.kurobako.gemstone

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.vm.*
import scala.compiletime.testing.typeCheckErrors

/** Runtime validation, nonempty values and newtypes. */
class ConstraintSuite extends munit.FunSuite {
  private object Small       extends Newtype[Int in 1 ~ 8]
  private object Name        extends Newtype[NonEmpty[String]]
  private object OtherName   extends Newtype[NonEmpty[String]]
  private object Lane        extends Newtype[Byte in 0 ~ 31]
  private object NestedLane  extends Newtype[Lane.Type]
  private object Nested      extends Newtype[Small.Type]
  private object Names       extends Newtype[NonEmpty[Vec[Name.Type]]]
  private object NonzeroPair extends Newtype[(Long, Long)] {
    override protected def violation(value: (Long, Long)): Option[NonEmpty[String]] =
      Option.when[NonEmpty[String]](value == (0L, 0L))("zero pair")
  }
  private object Foreign {
    // stands in for an opaque type owned by another library
    @SuppressWarnings(Array("scalafix:TransparentOpaque"))
    opaque type Type = Int
    def apply(value: Int): Type = value
    def read(value: Type): Int  = value
  }
  private object ForeignName extends Newtype[Foreign.Type]

  test("refine reports failures as values, unsafe throws them, and both evaluate their input once") {
    val evaluations = java.util.concurrent.atomic.AtomicInteger()
    def input: Int  = evaluations.incrementAndGet()
    assertEquals(input.refine[Int > 0].map(_.raw), Right(1))
    assertEquals(input.unsafe[Int in 1 ~ 8].raw, 2)
    assertEquals(evaluations.get(), 2)
    assert(0.refine[Int > 0].left.exists(message => message.contains("GreaterThan[0]") && message.endsWith(": 0")))
    val _          = intercept[IllegalArgumentException](0.unsafe[Int > 0])
    val _          = intercept[IllegalArgumentException]((-1L).unsafe[Long >= 0])
    val byte: Byte = 17
    assertEquals(byte.refine[Byte in 0 ~ 31].map(_.raw), Right(byte))
    assert((1024: Short).refine[Short in 0 ~ 1023].isLeft)
    assert((-1).toByte.refine[Byte >= 0].isLeft)
    val limit = java.lang.Long.parseLong("12")
    case class BudgetExceeded(limit: Long > 0)
    assertEquals(BudgetExceeded(limit.unsafe).limit.raw, 12L)
  }

  test("runtime validation requires the exact carrier and a complete target") {
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
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int = 17
      value.refine[Byte in 0 ~ 31]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Byte = 17
      value.refine[Short > 0]
    """).nonEmpty)
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
      1.unsafe[Int in (Int, 2)]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      1.unsafe[Int in (1, "two")]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      1.0.unsafe[Double where GreaterThan[0]]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def refine[P](value: Int) = value.unsafe[Int where P]
    """).nonEmpty)
    assertEquals(1.toLong.unsafe[Long > 0].raw, 1L)
  }

  test("strings and immutable collections are checked at runtime; NUL is permitted") {
    assertEquals("kernel".unsafe[NonEmpty[String]].raw, "kernel")
    assertEquals("a\u0000b".unsafe[NonEmpty[String]].raw, "a\u0000b")
    assertEquals(List(1, 2).unsafe[NonEmpty[List[Int]]].raw, List(1, 2))
    assert("".refine[NonEmpty[String]].isLeft)
    assertEquals("kernel".refine[NonEmpty[String]].map(value => value: String), Right("kernel"))
    val _ = intercept[IllegalArgumentException]("".unsafe[NonEmpty[String]])
    val _ = intercept[IllegalArgumentException](List.empty[Int].unsafe[NonEmpty[List[Int]]])
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      scala.collection.mutable.ArrayBuffer(1).unsafe[NonEmpty[scala.collection.mutable.ArrayBuffer[Int]]]
    """).nonEmpty)
  }

  test("newtypes validate the carrier refinement and their own rules before wrapping") {
    assertEquals(4.unsafe[Small.Type].raw, 4)
    assertEquals(4.refine[Small.Type].map(_.raw), Right(4))
    assert(0.refine[Small.Type].isLeft)
    val _ = intercept[IllegalArgumentException](0.unsafe[Small.Type])
    assert("".refine[Name.Type].isLeft)
    assert("a\u0000b".refine[Name.Type].isRight)
    assertEquals((17: Byte).refine[Lane.Type].map(_.unwrap.raw), Right(17.toByte))
    assertEquals((0L, 0L).refine[NonzeroPair.Type].left.map(_.raw), Left("zero pair"))
    val _ = intercept[IllegalArgumentException](NonzeroPair((0L, 0L)))
    assertEquals((1L, 0L).unsafe[NonzeroPair.Type].raw, (1L, 0L))
    assert(0L.refine[AllocationId].isLeft)
    assert((-1L).refine[ProgramId].isLeft)
    assertEquals(Long.MaxValue.refine[AllocationId].map(_.raw: Long), Right(Long.MaxValue))
    val _ = intercept[ArithmeticException](AllocationId(Long.MaxValue).next)
    val _ = intercept[ArithmeticException](CompletionId(Long.MaxValue).next)
    assertEquals(summon[Ordering[Small.Type]].compare(Small(1), Small(8)), -1)
  }

  test("unwrap retains the carrier while raw removes nested wrappers, once, and only ours") {
    val small                         = Small(8)
    val carrier: Int in 1 ~ 8         = small.unwrap
    val inner: Small.Type             = Nested(small).unwrap
    val primitive: Int                = Nested(small).raw
    val lane: Byte                    = NestedLane(Lane(17)).raw
    val refinedText: NonEmpty[String] = Name("kernel").unwrap
    val names                         = Vec(Name("kernel"))
    val collection: Vec[Name.Type]    = Names(names.unsafe[NonEmpty[Vec[Name.Type]]]).raw
    val foreign: Foreign.Type         = ForeignName(Foreign(7)).raw
    assertEquals(carrier.raw, 8)
    assertEquals(inner, small)
    assertEquals(primitive, 8)
    assertEquals(lane, 17.toByte)
    assertEquals(Name("kernel").raw, refinedText: String)
    assertEquals(OtherName("kernel").raw, refinedText: String)
    assertEquals(collection, names)
    assertEquals(Foreign.read(foreign), 7)
    val calls                  = java.util.concurrent.atomic.AtomicInteger(0)
    def produce(): Nested.Type = { val _ = calls.incrementAndGet(); Nested(Small(8)) }
    assertEquals(produce().raw, 8)
    assertEquals(calls.get(), 1)
  }

  test("runtime values cannot enter refinements or nominal types implicitly, and names stay distinct") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.constraint.auto.*
      def refine(value: Int): Int > 0 = value
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def refine(value: String): NonEmpty[String] = value
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def refine(value: Long) = net.kurobako.gemstone.memory.AllocationId(value)
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      1.unsafe[net.kurobako.gemstone.memory.AllocationId]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val id: net.kurobako.gemstone.vm.ProgramId = net.kurobako.gemstone.memory.AllocationId(1L.unsafe)
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      object First extends Newtype[NonEmpty[String]]
      object Second extends Newtype[NonEmpty[String]]
      val name: Second.Type = First("kernel".unsafe)
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      where.checked[Int, GreaterThan[0]](1)
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      where.either[Int, GreaterThan[0]](1)
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      NonEmpty[String]("kernel")
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      object Count extends Newtype[Int > 0]
      1L.unsafe[Count.Type]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      object Foreign {
        opaque type Type = Int
        def apply(value: Int): Type = value
      }
      object Named extends Newtype[Foreign.Type]
      val primitive: Int = Named(Foreign(7)).raw
    """).nonEmpty)
  }
}

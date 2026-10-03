package net.kurobako.gemstone

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import scala.compiletime.testing.typeCheckErrors

class ConstraintSuite extends munit.FunSuite {
  private object Small     extends Newtype[Int in 1 ~ 8]
  private object Name      extends Newtype[NonEmpty[String]]
  private object OtherName extends Newtype[NonEmpty[String]]

  test("newtypes wrap validated values with inferred refinement targets") {
    val small = Small(8.unsafe)
    val name  = Name("kernel".unsafe)
    assertEquals(small.raw, 8)
    assertEquals(name.raw, "kernel")
    assertEquals(AllocationId(1L.unsafe).next.raw, 2L)
    assertEquals(summon[Ordering[Small.Type]].compare(Small(1.unsafe), small), -1)
    assertEquals(OtherName("kernel".unsafe).raw, name.raw)
  }

  private object Nested extends Newtype[Small.Type]
  private object Names  extends Newtype[NonEmpty[Vec[Name.Type]]]
  private object Foreign {
    opaque type Type = Int
    def apply(value: Int): Type = value
    def read(value: Type): Int  = value
  }
  private object ForeignName extends Newtype[Foreign.Type]

  test("unwrap retains the carrier while raw removes nested wrapper layers") {
    val small                            = Small(8)
    val nested                           = Nested(small)
    val carrier: Int in 1 ~ 8 = small.unwrap
    val inner: Small.Type                = nested.unwrap
    val primitive: Int                   = nested.raw
    val text: String                     = Name("kernel").raw
    val refinedText: NonEmpty[String]    = Name("kernel").unwrap
    val identifier: Long                 = AllocationId(3L).raw
    assertEquals(carrier.raw, 8)
    assertEquals(inner, small)
    assertEquals(primitive, 8)
    assertEquals(text, refinedText)
    assertEquals(identifier, 3L)
  }

  test("raw preserves collection elements and unrelated opaque types") {
    val names                             = Vec(Name("kernel"))
    val nonempty                          = names.unsafe[NonEmpty[Vec[Name.Type]]]
    val collection: Vec[Name.Type]        = Names(nonempty).raw
    val refinedCollection: Vec[Name.Type] = nonempty.raw
    val foreign: Foreign.Type             = ForeignName(Foreign(7)).raw
    assertEquals(collection, names)
    assertEquals(refinedCollection, names)
    assertEquals(Foreign.read(foreign), 7)
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

  test("raw evaluates its receiver once") {
    val calls                  = java.util.concurrent.atomic.AtomicInteger(0)
    def produce(): Nested.Type = {
      val _ = calls.incrementAndGet()
      Nested(Small(8))
    }
    assertEquals(produce().raw, 8)
    assertEquals(calls.get(), 1)
  }

  test("unwrapped identifier arithmetic retains overflow checks") {
    val _ = intercept[ArithmeticException](AllocationId(Long.MaxValue).next)
    val _ = intercept[ArithmeticException](ProgramId(Long.MaxValue).next)
    val _ = intercept[ArithmeticException](CompletionId(Long.MaxValue).next)
  }

  test("runtime values cannot enter refinements or nominal types implicitly") {
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
      import net.kurobako.gemstone.memory.*
      def refine(value: Long) = AllocationId(value)
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.memory.*
      1.unsafe[AllocationId]
    """).nonEmpty)
  }

  test("newtypes with identical representations remain distinct") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.rpc.*
      val id: ProgramId = AllocationId(1L.unsafe)
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      object First extends Newtype[NonEmpty[String]]
      object Second extends Newtype[NonEmpty[String]]
      val name: Second.Type = First("kernel".unsafe)
    """).nonEmpty)
  }

  test("domain decoders retain recoverable validation failures") {
    assert(AllocationId.either(0L).isLeft)
    assert(ProgramId.either(-1L).isLeft)
    assert(CompletionId.either(Long.MinValue).isLeft)
    assertEquals(AllocationId.either(Long.MaxValue).map(_.raw: Long), Right(Long.MaxValue))
  }

  test("unsafe checks strings and immutable collections at runtime") {
    assertEquals("kernel".unsafe[NonEmpty[String]].toString, "kernel")
    assertEquals("kernel".unsafe[NonEmpty[String]].raw, "kernel")
    assertEquals("x".unsafe[NonEmpty[String]].raw, "x")
    assertEquals(List(1, 2).unsafe[NonEmpty[List[Int]]].raw, List(1, 2))
    val _ = intercept[IllegalArgumentException]("".unsafe[NonEmpty[String]])
    assertEquals("a\u0000b".unsafe[NonEmpty[String]].raw, "a\u0000b")
    val _ = intercept[IllegalArgumentException](List.empty[Int].unsafe[NonEmpty[List[Int]]])
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      scala.collection.mutable.ArrayBuffer(1).unsafe[NonEmpty[scala.collection.mutable.ArrayBuffer[Int]]]
    """).nonEmpty)
  }

  test("alternate public refinement factories are unavailable") {
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
  }
  test("recoverable refinement validates once and retains domain failures") {
    val evaluations = java.util.concurrent.atomic.AtomicInteger()
    def input: Int  = evaluations.incrementAndGet()
    assertEquals(input.refine[Int > 0].map(_.raw), Right(1))
    assertEquals(evaluations.get(), 1)
    assert(0.refine[Int > 0].isLeft)
    assert("".refine[NonEmpty[String]].isLeft)
    assertEquals("kernel".refine[NonEmpty[String]].map(value => value: String), Right("kernel"))
  }

}

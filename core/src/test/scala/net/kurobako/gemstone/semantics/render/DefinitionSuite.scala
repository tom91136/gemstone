package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.semantics.render.*

class DefinitionSuite extends munit.FunSuite {
  private val u32 = Type.unsigned(32)

  private inline def scaled[V](inline algebra: Algebra[V], value: V, factor: Int): V =
    algebra.primitive(value)((operand: UInt[32]) => Fused.scaled(operand, factor, wrap = true))

  private def definition(steps: (String, Term)*)(declared: (String, Type)*): String = Definition.of(
    "op",
    Vec.from(steps.map(Term.Step(_, _))),
    Vec.from(declared.map((name, tpe) => (name, tpe)))
  ).fold(failure => fail(failure), ScalaBackend.show(_))

  import Term.*

  test("primitives execute their function and render as a call of it with its fixed arguments") {
    assertEquals(scaled(Execute, 0x80000001L, 2), 2L)
    assertEquals(Execute.primitive(3L, 4L)(Fused.sum(_, _)), 7L)
    assertEquals(Execute.primitive(0xbf800000L)(Fused.negative(_)), 1L)
    assertEquals(Execute.primitive(1L, 1L, 1L, 1L, 1L, 2L)(Fused.weighted(_, _, _, _, _, _)), 27L)
    assertEquals(
      Render.primitive(Variable("a"), Literal(1L), Literal(1L), Literal(1L), Literal(1L), Literal(1L))(
        Fused.weighted(_, _, _, _, _, _)
      ).show,
      "Fused.weighted(a, 1, 1, 1, 1, 1)"
    )
    assertEquals(
      scaled(Render, Variable("a"), 3),
      Primitive(
        "net.kurobako.gemstone.semantics.Fused",
        "scaled",
        Vec(Variable("a"), Constant("factor = 3"), Constant("wrap = true")),
        Vec(Some(u32), None, None),
        u32
      )
    )
  }

  test("integer semantics read as wrapping arithmetic over sized types, widening where the exact value is needed") {
    val sum = Binary("+", Variable("a"), Variable("b"))
    assertEquals(
      definition("d" -> Call("zext32", Vec(sum)), "c" -> Binary("<s", Literal(0xffffffffL), sum))(
        "a" -> u32,
        "b" -> u32,
        "d" -> u32,
        "c" -> Type.Bool
      ),
      """def op(a: UInt[32], b: UInt[32]): (d: UInt[32], c: Bool) =
        |  (
        |    d = a + b,
        |    c = a.to[UInt[64]] + b.to[UInt[64]] > 0xffffffffL
        |  )""".stripMargin
    )
    assertEquals(
      definition("p" -> Binary("<=s", Call("sext32", Vec(Variable("b"))), Call("sext32", Vec(Variable("a")))))(
        "a" -> u32,
        "b" -> u32,
        "p" -> Type.Bool
      ),
      "def op(a: UInt[32], b: UInt[32]): (p: Bool) =\n  (p = b.to[SInt[32]] <= a.to[SInt[32]])"
    )
  }

  test("primitive operands convert to the types the primitive takes") {
    assertEquals(
      definition("d" -> Primitive("x.Fused", "sum", Vec(Variable("a"), Literal(1L)), Vec(Some(u32), Some(u32)), u32))(
        "a" -> Type.Float(32),
        "d" -> u32
      ),
      "def op(a: Float32): (d: UInt[32]) =\n  (d = Fused.sum(a.bits, UInt[32](1)))"
    )
  }

  test("machine operations take their context and stay in order around effects") {
    def load(address: Term) = Context(
      "x.Memory",
      "memory",
      "load",
      Vec(u32),
      Vec(address),
      Vec(Some(Type.unsigned(64))),
      Some(u32),
      effect = false
    )
    def store(address: Term, value: Term) =
      Context(
        "x.Memory",
        "memory",
        "store",
        Vec(u32),
        Vec(address, value),
        Vec(Some(Type.unsigned(64)), Some(u32)),
        None,
        effect = true
      )
    assertEquals(
      definition(
        "t0"     -> load(Variable("a")),
        "memory" -> store(Variable("a"), Binary("+", Variable("t0"), Literal(1L))),
        "d"      -> load(Variable("a"))
      )("a" -> Type.unsigned(64), "d" -> u32),
      """def op(a: UInt[64])(using memory: Memory): (d: UInt[32]) = {
        |  val v0 = memory.load[UInt[32]](a)
        |  memory.store[UInt[32]](a, v0 + 1)
        |  val v1 = memory.load[UInt[32]](a)
        |  (d = v1)
        |}""".stripMargin
    )
    assertEquals(
      definition(
        "memory" ->
          Select(Binary("<u", Variable("a"), Literal(16L)), store(Variable("a"), Variable("b")), Constant("()"))
      )("a" -> Type.unsigned(64), "b" -> u32),
      """def op(a: UInt[64], b: UInt[32])(using memory: Memory): Unit = {
        |  if (a < 16) memory.store[UInt[32]](a, b) else ()
        |  ()
        |}""".stripMargin
    )
  }

  test("a binding an effect orders takes the type of the exact value it holds") {
    def load(address: Term) = Context(
      "x.Memory",
      "memory",
      "load",
      Vec(u32),
      Vec(address),
      Vec(Some(Type.unsigned(64))),
      Some(u32),
      effect = false
    )
    def store(address: Term, value: Term) = Context(
      "x.Memory",
      "memory",
      "store",
      Vec(u32),
      Vec(address, value),
      Vec(Some(Type.unsigned(64)), Some(u32)),
      None,
      effect = true
    )
    val loaded = load(Variable("a"))
    val index  =
      Binary("|", Binary("&", loaded, Literal(-16L)), Binary("-", Literal(15L), Call("zext4", Vec(loaded))))
    assertEquals(
      definition("i" -> index, "memory" -> store(Variable("a"), Variable("i")))("a" -> Type.unsigned(64)),
      """def op(a: UInt[64])(using memory: Memory): Unit = {
        |  val v0 = (memory.load[UInt[32]](a) & -16).to[UInt[64]] | UInt[4](15).to[UInt[64]] - memory.load[UInt[32]](a).to[UInt[4]].to[UInt[64]]
        |  memory.store[UInt[32]](a, v0.to[UInt[32]])
        |  ()
        |}""".stripMargin
    )
  }

  test("an untyped computed operand meets a typed one") {
    assertEquals(
      definition("d" -> Select(Variable("p"), Binary("/u", Literal(8L), Literal(2L)), Variable("a")))(
        "p" -> Type.Bool,
        "a" -> u32,
        "d" -> u32
      ),
      "def op(p: Bool, a: UInt[32]): (d: UInt[32]) =\n  (d = if (p) UInt[32](8 / 2) else a)"
    )
  }

  test("a literal wider than the other operand widens the operation") {
    assertEquals(
      definition("d" -> Binary("|", Variable("a"), Literal(0x100000000L)))("a" -> u32, "d" -> Type.unsigned(64)),
      "def op(a: UInt[32]): (d: UInt[64]) =\n  (d = a.to[UInt[64]] | 0x100000000L)"
    )
  }

  test("a literal the other operands of a minimum or blend cannot hold widens the operation") {
    assertEquals(
      definition("d" -> Call("min.u", Vec(Call("zext5", Vec(Variable("a"))), Literal(32L))))("a" -> u32, "d" -> u32),
      "def op(a: UInt[32]): (d: UInt[32]) =\n  (d = min(a.to[UInt[5]].to[UInt[64]], UInt[64](32)).to[UInt[32]])"
    )
    assertEquals(
      definition("d" -> Call("max.s", Vec(Literal(-200L), Call("sext8", Vec(Variable("a"))))))("a" -> u32, "d" -> u32),
      "def op(a: UInt[32]): (d: UInt[32]) =\n  (d = max(SInt[64](-200), a.to[SInt[8]].to[SInt[64]]).to[UInt[32]])"
    )
    assertEquals(
      definition("d" -> Call("blend", Vec(Variable("a"), Variable("b"), Literal(0x100000000L))))(
        "a" -> u32,
        "b" -> u32,
        "d" -> Type.unsigned(64)
      ),
      "def op(a: UInt[32], b: UInt[32]): (d: UInt[64]) =\n  (d = blend(a.to[UInt[64]], b.to[UInt[64]], UInt[64](0x100000000L)))"
    )
  }

  test("an untyped selection of literals takes the type of the operand it meets") {
    assertEquals(
      definition("d" -> Binary("|", Select(Variable("p"), Literal(0x3c00L), Literal(0L)), Variable("a")))(
        "p" -> Type.Bool,
        "a" -> u32,
        "d" -> u32
      ),
      "def op(p: Bool, a: UInt[32]): (d: UInt[32]) =\n  (d = UInt[32](if (p) 0x3c00 else 0) | a)"
    )
  }

  test("a selection as the receiver of a shift prints in parentheses, with its branches converted inside") {
    val selected = Select(Variable("p"), Variable("a"), Variable("b"))
    val declared = Seq("p" -> Type.Bool, "a" -> u32, "b" -> u32, "s" -> Type.signed(32), "d" -> u32)
    assertEquals(
      definition("d" -> Binary("<<", selected, Literal(3L)))(declared*),
      "def op(p: Bool, a: UInt[32], b: UInt[32]): (d: UInt[32]) =\n  (d = (if (p) a else b) << 3)"
    )
    assertEquals(
      definition("d" -> Binary(">>u", Select(Variable("p"), Variable("s"), Variable("a")), Variable("b")))(declared*),
      "def op(p: Bool, a: UInt[32], b: UInt[32], s: SInt[32]): (d: UInt[32]) =\n  (d = (if (p) s.to[UInt[32]] else a) >> b)"
    )
  }

  test("reading an undeclared name is an error") {
    assert(Definition.of("op", Vec(Step("d", Variable("x"))), Vec(("d", u32))).isLeft)
  }

  test("the new operations render as calls") {
    val (a, b, c) = (Variable("a"), Variable("b"), Variable("c"))
    assertEquals(Render.minimum(a, b, signed = true).show, "min.s(a, b)")
    assertEquals(Render.maximum(a, b, signed = false).show, "max.u(a, b)")
    assertEquals(Render.trailingZeros(a, 32).show, "ctz32(a)")
    assertEquals(Render.bitFieldExtract(a, b, c, signed = true).show, "bfe.s(a, b, c)")
  }
}

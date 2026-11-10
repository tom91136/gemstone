package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

class SmtBackendSuite extends Suite {
  private val u32 = Type.unsigned(32)
  private val s32 = Type.signed(32)

  private def definition(
      parameters: Vec[(name: String, tpe: Type)],
      results: Vec[(name: String, tpe: Type, value: Term)]
  ): Definition = Definition("f", parameters, Vec.empty, Vec.empty, Vec.empty, results)

  private val ab: Vec[(name: String, tpe: Type)] = Vec(("a", u32), ("b", u32))

  test("a sum is a bit-vector sum over its inputs") {
    val sum = definition(ab, Vec(("sum", u32, Term.Binary("+", Term.Variable("a"), Term.Variable("b")))))
    assertEquals(
      SmtBackend.show(sum),
      """|(declare-const a (_ BitVec 32))
         |(declare-const b (_ BitVec 32))
         |(define-fun sum () (_ BitVec 32) (bvadd a b))""".stripMargin
    )
  }

  test("a literal takes the width of the other operand") {
    val masked = definition(
      Vec(("a", u32)),
      Vec(("masked", u32, Term.Binary("&", Term.Variable("a"), Term.Literal(255L))))
    )
    assert(SmtBackend.show(masked).endsWith("(define-fun masked () (_ BitVec 32) (bvand a (_ bv255 32)))"))
  }

  test("a logical right shift runs over 64 bits with the amount masked to six bits") {
    val shifted = definition(ab, Vec(("shifted", u32, Term.Binary(">>u", Term.Variable("a"), Term.Variable("b")))))
    assert(SmtBackend.show(shifted).endsWith(
      "(define-fun shifted () (_ BitVec 32) ((_ extract 31 0) (bvlshr ((_ zero_extend 32) a) " +
        "(bvand ((_ zero_extend 32) b) (_ bv63 64)))))"
    ))
  }

  test("a selection on a signed comparison") {
    val a       = Term.Variable("a")
    val b       = Term.Variable("b")
    val minimum = definition(
      Vec(("a", s32), ("b", s32)),
      Vec(("minimum", s32, Term.Select(Term.Binary("<", a, b), a, b)))
    )
    assert(SmtBackend.show(minimum).endsWith("(define-fun minimum () (_ BitVec 32) (ite (bvslt a b) a b))"))
  }

  test("a comparison of unsigned operands is unsigned") {
    val a       = Term.Variable("a")
    val b       = Term.Variable("b")
    val atLeast = definition(ab, Vec(("atLeast", Type.Bool, Term.Binary(">=", a, b))))
    assert(SmtBackend.show(atLeast).contains("(bvuge a b)"))
  }

  test("a floating-point parameter is rejected before encoding") {
    val float = definition(Vec(("x", Type.Float(32))), Vec(("y", Type.Float(32), Term.Variable("x"))))
    assert(SmtBackend.equivalence(float, float, memory = false).isLeft)
  }

  test("division is rejected before encoding") {
    val quotient = definition(ab, Vec(("q", u32, Term.Binary("/u", Term.Variable("a"), Term.Variable("b")))))
    assert(SmtBackend.equivalence(quotient, quotient, memory = false).isLeft)
  }

  test("a commuted sum is a query asserting the results differ") {
    val left  = definition(ab, Vec(("s", u32, Term.Binary("+", Term.Variable("a"), Term.Variable("b")))))
    val right = definition(ab, Vec(("s", u32, Term.Binary("+", Term.Variable("b"), Term.Variable("a")))))
    assertEquals(
      SmtBackend.equivalence(left, right, memory = false),
      Right(
        """|(set-logic QF_ABV)
           |(set-option :produce-models true)
           |(declare-const a (_ BitVec 32))
           |(declare-const b (_ BitVec 32))
           |(define-fun l_s () (_ BitVec 32) (bvadd a b))
           |(define-fun r_s () (_ BitVec 32) (bvadd b a))
           |(assert (distinct l_s r_s))
           |(check-sat)
           |(get-value (a b))""".stripMargin
      )
    )
  }

  private def storing(value: Term): Definition = Definition(
    "f",
    Vec(("a", u32)),
    Vec.empty,
    Vec.empty,
    Vec(Definition.Statement(
      Definition.Target.Effect,
      Term.Context(
        RenderMachine.Machine.contextType,
        RenderMachine.Machine.context,
        "store",
        Vec(u32),
        Vec(Term.Constant("Space.Global"), Term.Literal(0L), value),
        Vec(None, None, None),
        None,
        effect = true
      )
    )),
    Vec.empty
  )

  test("final memory is compared when it is live-out") {
    val query = SmtBackend.equivalence(
      storing(Term.Variable("a")),
      storing(Term.Binary("+", Term.Variable("a"), Term.Literal(1L))),
      memory = true
    )
    assert(query.exists(_.contains("(assert (distinct l_memory_Global_0 r_memory_Global_0))")), query.toString)
  }

  test("final memory is ignored when it is not live-out") {
    val query = SmtBackend.equivalence(
      storing(Term.Variable("a")),
      storing(Term.Binary("+", Term.Variable("a"), Term.Literal(1L))),
      memory = false
    )
    assert(query.exists(_.contains("(assert false)")), query.toString)
    assert(query.exists(!_.contains("distinct")), query.toString)
  }
}

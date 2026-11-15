package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.toVec
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

  private val f32 = Type.Float(32)

  private def asFloat(term: Term): Term = Term.Call(".asFloat", Vec(term))

  private def bits(term: Term): Term = Term.Call(".bits", Vec(term))

  private def nvidia(method: String, arguments: Term*): Term = Term.Primitive(
    "net.kurobako.gemstone.isa.nvidia.FloatArithmetic",
    method,
    Vec(arguments*),
    arguments.toVec.map(_ => Some(f32)),
    f32
  )

  private def amdgpu(method: String, arguments: Term*): Term = Term.Primitive(
    "net.kurobako.gemstone.isa.amdgpu.Floats",
    method,
    Vec(arguments*),
    arguments.toVec.map(_ => Some(f32)),
    f32
  )

  private def rounded(value: Term, name: String): Term = Term.Primitive(
    "net.kurobako.gemstone.isa.nvidia.FloatArithmetic",
    "fusedMultiplyAdd",
    Vec(
      asFloat(value),
      asFloat(value),
      asFloat(value),
      Term.Constant(s"rounding = net.kurobako.gemstone.isa.nvidia.VMIR.ConversionRounding.$name")
    ),
    Vec(Some(f32), Some(f32), Some(f32), None),
    f32
  )

  test("a floating-point parameter is declared over its bits") {
    val float = definition(Vec(("x", Type.Float(32))), Vec(("y", Type.Float(32), Term.Variable("x"))))
    assertEquals(
      SmtBackend.equivalence(float, float, memory = false),
      Right(
        """|(set-logic QF_AUFBVFP)
           |(set-option :produce-models true)
           |(declare-const x (_ BitVec 32))
           |(define-fun l_y () (_ FloatingPoint 8 24) ((_ to_fp 8 24) x))
           |(define-fun r_y () (_ FloatingPoint 8 24) ((_ to_fp 8 24) x))
           |(assert (distinct x x))
           |(check-sat)
           |(get-value (x))""".stripMargin
      )
    )
  }

  test("a floating-point sum is the SMT floating-point sum of the operands' bits") {
    val sum = definition(
      ab,
      Vec((
        "d",
        u32,
        bits(nvidia("canonical", nvidia("add", asFloat(Term.Variable("a")), asFloat(Term.Variable("b")))))
      ))
    )
    val shown = SmtBackend.show(sum)
    assert(shown.contains("(_ to_fp 8 24)"), shown)
    assert(shown.contains("(fp.add RNE ((_ to_fp 8 24) a) ((_ to_fp 8 24) b))"), shown)
    assert(shown.contains("(fp.isNaN"), shown)
    assert(shown.contains("(_ bv2147483647 32)"), shown)
  }

  test("a flushed denormal is encoded on the operand's bits and its sign") {
    val flushed = Term.Select(
      Term.Binary("<", Term.Call(".to[UInt[31]]", Vec(Term.Variable("a"))), Term.Literal(0x800000L)),
      Term.Binary("&", Term.Variable("a"), Term.Literal(0x80000000L)),
      Term.Variable("a")
    )
    val sum = definition(
      ab,
      Vec(("d", u32, bits(nvidia("canonical", nvidia("add", asFloat(flushed), asFloat(Term.Variable("b")))))))
    )
    val shown = SmtBackend.show(sum)
    assert(shown.contains("(bvult ((_ extract 30 0) a) (_ bv8388608 31))"), shown)
    assert(shown.contains("(bvand a (_ bv2147483648 32))"), shown)
  }

  test("a floating-point operation takes the instruction's rounding mode") {
    val fma = definition(ab, Vec(("d", u32, bits(nvidia("canonical", rounded(Term.Variable("a"), "RM"))))))
    assert(SmtBackend.show(fma).contains("(fp.fma RTN "), SmtBackend.show(fma))
  }

  test("saturation clamps to zero and one around the operation") {
    val saturated = definition(ab, Vec(("d", u32, bits(nvidia("saturate", asFloat(Term.Variable("a")))))))
    val shown     = SmtBackend.show(saturated)
    assert(shown.contains("(fp.geq "), shown)
    assert(shown.contains("(fp.leq "), shown)
    assert(shown.contains("(_ bv1065353216 32)"), shown)
  }

  test("AMDGPU arithmetic quiets a NaN operand rather than leaving the solver to pick one") {
    val sum = definition(
      ab,
      Vec(("d", u32, bits(amdgpu("add", asFloat(Term.Variable("a")), asFloat(Term.Variable("b")), Term.Literal(0)))))
    )
    val shown = SmtBackend.show(sum)
    assert(shown.contains("(fp.isNaN ((_ to_fp 8 24) a))"), shown)
    assert(shown.contains("(bvor a (_ bv4194304 32))"), shown)
  }

  test("an AMDGPU binary32 operation rounds as the low field of the mode it is given") {
    val fields = Vec(("a", u32), ("b", u32), ("m", u32))
    val mode   = Term.Binary("&", Term.Variable("m"), Term.Literal(3))
    val sum    = definition(
      fields,
      Vec(("d", u32, bits(amdgpu("add", asFloat(Term.Variable("a")), asFloat(Term.Variable("b")), mode))))
    )
    val shown = SmtBackend.show(sum)
    assert(shown.contains("(fp.add (ite "), shown)
    Vec("RNE", "RTP", "RTN", "RTZ").foreach(name => assert(shown.contains(name), shown))
    assert(shown.contains("((_ extract 1 0) "), shown)
  }

  test("an AMDGPU binary16 fused multiply-add rounds as the second field of the mode it is given") {
    val f16   = Type.Float(16)
    val u16   = Type.unsigned(16)
    val mode  = Term.Binary("&", Term.Variable("m"), Term.Literal(12))
    val names = Vec("x", "y", "z")
    val fma   = Term.Primitive(
      "net.kurobako.gemstone.isa.amdgpu.Floats",
      "fma",
      names.map(name => asFloat(Term.Variable(name))) :+ mode,
      Vec(Some(f16), Some(f16), Some(f16), Some(u32)),
      f16
    )
    val shown = SmtBackend.show(definition(names.map((_, u16)) :+ (("m", u32)), Vec(("d", u16, bits(fma)))))
    assert(shown.contains("(fp.fma (ite "), shown)
    assert(shown.contains("((_ extract 3 2) "), shown)
  }

  test("an AMDGPU operation without a rounding argument is rejected") {
    val sum = definition(
      ab,
      Vec(("d", u32, bits(amdgpu("add", asFloat(Term.Variable("a")), asFloat(Term.Variable("b"))))))
    )
    assert(SmtBackend.equivalence(sum, sum, memory = false).isLeft)
  }

  test("an invalid NVIDIA sum is the negative default NaN the host gives") {
    val sum = definition(
      ab,
      Vec(("d", u32, bits(nvidia("add", asFloat(Term.Variable("a")), asFloat(Term.Variable("b"))))))
    )
    assert(SmtBackend.show(sum).contains("(_ bv4290772992 32)"), SmtBackend.show(sum))
  }

  test("a binary64 NVIDIA fused multiply-add takes a NaN from the factor, the addend, then the other factor") {
    val f64   = Type.Float(64)
    val u64   = Type.unsigned(64)
    val names = Vec("x", "y", "z")
    val fma   = Term.Primitive(
      "net.kurobako.gemstone.isa.nvidia.FloatArithmetic",
      "fusedMultiplyAdd",
      names.map(name => asFloat(Term.Variable(name))) :+
        Term.Constant("rounding = net.kurobako.gemstone.isa.nvidia.VMIR.ConversionRounding.RN"),
      Vec(Some(f64), Some(f64), Some(f64), None),
      f64
    )
    val shown = SmtBackend.show(definition(names.map((_, u64)), Vec(("d", u64, bits(fma)))))
    val order = Vec("y", "z", "x").map(name => shown.indexOf(s"(fp.isNaN ((_ to_fp 11 53) $name))"))
    assert(order.forall(_ >= 0) && order == order.sorted, shown)
    assert(shown.contains("(_ bv18444492273895866368 64)"), shown)
  }

  test("a binary16 AMDGPU difference propagates the negated NaN of its right operand") {
    val f16 = Type.Float(16)
    val u16 = Type.unsigned(16)
    val sub = Term.Primitive(
      "net.kurobako.gemstone.isa.amdgpu.Floats",
      "sub",
      Vec(asFloat(Term.Variable("a")), asFloat(Term.Variable("b")), Term.Literal(0)),
      Vec(Some(f16), Some(f16), Some(u32)),
      f16
    )
    val shown = SmtBackend.show(definition(Vec(("a", u16), ("b", u16)), Vec(("d", u16, bits(sub)))))
    assert(shown.contains("(bvxor b (_ bv32768 16))"), shown)
  }

  test("a floating-point result is compared over its bits") {
    val identity = definition(Vec(("x", f32)), Vec(("y", f32, Term.Variable("x"))))
    val query    = SmtBackend.equivalence(identity, identity, memory = false)
    assert(query.exists(_.contains("(define-fun l_y () (_ FloatingPoint 8 24) ((_ to_fp 8 24) x))")), query.toString)
    assert(query.exists(_.contains("(assert (distinct x x))")), query.toString)
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

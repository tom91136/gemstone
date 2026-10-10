package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*

class LegalitySuite extends munit.FunSuite {
  import NVDisasm.ConditionExpression.*

  private val database = NVDisasm.Database("test", 128, Vec.empty, Vec.empty)

  test("condition enum names resolve an unambiguous vendor case variant") {
    val mode     = NVDisasm.Vocabulary("ChkMode", Vec.nonEmpty(NVDisasm.SymbolValue("DIVIDE", BigInt(0))))
    val withMode = database.copy(symbols = Vec.nonEmpty(mode))
    assertEquals(Legality.evaluate(withMode, EnumValue("Chkmode", "DIVIDE"), Map.empty), Right(BigInt(0)))
    assert(Legality.evaluate(withMode, EnumValue("Chkmode", "OTHER"), Map.empty).isLeft)
    assert(Legality.evaluate(
      withMode.copy(symbols = Vec.nonEmpty(mode, mode.copy(name = "CHKMODE"))),
      EnumValue("Chkmode", "DIVIDE"),
      Map.empty
    ).isLeft)
  }

  test("condition operators and helper calls resolve to semantic tokens while preserving unknown names") {
    assertEquals(
      NVDisasm.BinaryOperator.from("->"),
      NVDisasm.BinaryOperator.Implies
    )
    assertEquals(NVDisasm.UnaryOperator.from("@"), NVDisasm.UnaryOperator.Unsupported("@"))
    assertEquals(NVDisasm.BuiltinCall.from("VendorLookup"), NVDisasm.BuiltinCall.Table("VendorLookup"))
    val unknown = Binary(NVDisasm.BinaryOperator.Unsupported("@"), Number(1), Number(2))
    assert(Legality.evaluate(
      database,
      unknown,
      Map.empty
    ).left.toOption.exists(_.contains("unsupported condition operator @")))
  }

  test("FORMAT semantic field categories are closed and unknown vendor names remain data") {
    val registerField: NVDisasm.Syntax.Field = NVDisasm.Syntax.field("Register", "r", None, None)
    val vendorField: NVDisasm.Syntax.Field   = NVDisasm.Syntax.field("VendorRegisterLike", "v", None, None)
    assertEquals(NVDisasm.fieldKind(registerField.kind), NVDisasm.FieldKind.Register)
    assertEquals(
      NVDisasm.fieldKind(vendorField.kind),
      NVDisasm.FieldKind.Vocabulary("VendorRegisterLike")
    )
  }

  test("Predicate_vimnmx retains its legacy operand category without becoming a predicate selector") {
    assertEquals(NVDisasm.fieldKind("Predicate_vimnmx"), NVDisasm.FieldKind.PredicateVimnmx)
    assert(NVDisasm.fieldKind("Predicate_vimnmx").isPredicateOperand)
    assert(!NVDisasm.fieldKind("Predicate_vimnmx").isPredicate)
    assert(NVDisasm.fieldKind("Predicate").isPredicate)
  }

  test("candidate failure categories are independent of diagnostic wording") {
    def invalid(message: String) = {
      val form = NVDisasm.Form(
        "typed",
        alternate = false,
        Vec.empty,
        Vec.empty,
        conditions =
          Vec.nonEmpty(NVDisasm.Condition(
            Binary(NVDisasm.BinaryOperator.Equal, Number(0), Number(1)),
            message
          ))
      )
      Legality.check(database, form, Map.empty).left.toOption.get
    }
    assertEquals(invalid("first wording").category, NVDisasm.FailureCategory.IllegalCandidate)
    assertEquals(invalid("changed wording").category, NVDisasm.FailureCategory.IllegalCandidate)
    assert(NVDisasm.MetadataIssue.AmbiguousInverse("TABLES_opex_sample", 7).structural)
    assert(!NVDisasm.MetadataIssue.AmbiguousInverse("ordinary_table", 7).structural)
    assert(NVDisasm.MetadataIssue.AmbiguousInverse("VarLatOperandEnc", 7).structural)
  }

  test("false implications do not require absent optional operands") {
    val condition = Binary(NVDisasm.BinaryOperator.Implies, Number(0), Field("optional", None))
    assertEquals(Legality.evaluate(database, condition, Map.empty), Right(BigInt(1)))
    assert(Legality.evaluate(
      database,
      Binary(NVDisasm.BinaryOperator.Implies, Number(1), Field("optional", None)),
      Map.empty
    ).isLeft)
  }

  test("operand alignment constraints distinguish legal and illegal register groups") {
    val condition = NVDisasm.Condition(
      Binary(
        NVDisasm.BinaryOperator.Equal,
        Binary(NVDisasm.BinaryOperator.Remainder, Field("Ra", None), Number(4)),
        Number(0)
      ),
      "unaligned register group"
    )
    val form = NVDisasm.Form(
      "matrix",
      alternate = false,
      Vec.empty,
      Vec.empty,
      conditions = Vec.nonEmpty(condition)
    )
    assertEquals(Legality.check(database, form, Map("Ra" -> BigInt(12))), Right(()))
    assertEquals(
      Legality.check(database, form, Map("Ra" -> BigInt(13))),
      Left(NVDisasm.CandidateFailure(
        NVDisasm.FailureCategory.IllegalCandidate,
        "illegal matrix: unaligned register group"
      ))
    )
    assert(Legality.check(
      database,
      form,
      Map.empty
    ).left.toOption.exists(_.message.startsWith("unsupported condition")))
  }

  test("FORMAT condition names resolve from normalised field bindings") {
    val condition = NVDisasm.Condition(
      Binary(NVDisasm.BinaryOperator.Equal, Field("sync", None), Number(1)),
      "requires sync"
    )
    val form = NVDisasm.Form(
      "sync-op",
      alternate = false,
      Vec.empty,
      Vec.empty,
      syntax = Vec.nonEmpty(NVDisasm.Syntax.field("sync", "SyncMode", None, None)),
      conditions = Vec.nonEmpty(condition)
    )
    val bindings = Legality.completeBindings(form, Map("SyncMode" -> BigInt(1)))
    assertEquals(bindings.get("sync"), Some(BigInt(1)))
    assertEquals(Legality.check(database, form, bindings), Right(()))
    assert(Legality.check(database, form, Legality.completeBindings(form, Map("SyncMode" -> BigInt(0)))).isLeft)
    val repeatedKind = form.copy(
      syntax = Vec.nonEmpty(
        NVDisasm.Syntax.field("register", "Ra", None, None),
        NVDisasm.Syntax.field("register", "Rb", None, None)
      )
    )
    assertEquals(Legality.completeBindings(repeatedKind, Map("register" -> BigInt(4))).get("Ra"), None)
  }

  test("evaluates recovered integer parity predicates") {
    assertEquals(
      Legality.evaluate(database, Call(NVDisasm.BuiltinCall.IsEven, Vec.nonEmpty(Number(12))), Map.empty),
      Right(BigInt(1))
    )
    assertEquals(
      Legality.evaluate(database, Call(NVDisasm.BuiltinCall.IsEven, Vec.nonEmpty(Number(13))), Map.empty),
      Right(BigInt(0))
    )
    assertEquals(
      Legality.evaluate(database, Call(NVDisasm.BuiltinCall.IsOdd, Vec.nonEmpty(Number(-3))), Map.empty),
      Right(BigInt(1))
    )
    assertEquals(
      Legality.evaluate(
        database,
        Call(NVDisasm.BuiltinCall.IsOdd, Vec.nonEmpty(Field("Rd", None))),
        Map("Rd" -> BigInt(8))
      ),
      Right(BigInt(0))
    )
    assert(Legality.evaluate(
      database,
      Call(NVDisasm.BuiltinCall.IsEven, Vec.empty[NVDisasm.ConditionExpression]),
      Map.empty
    ).isLeft)
    assert(Legality.evaluate(
      database,
      Call(NVDisasm.BuiltinCall.IsOdd, Vec.nonEmpty(Number(1), Number(3))),
      Map.empty
    ).isLeft)
  }

  test("conditions on pre-encoding values apply only when the value is supplied") {
    val condition = NVDisasm.Condition(
      Binary(NVDisasm.BinaryOperator.Equal, Field("dst_wr_sb", None), Number(7)),
      "Illegal instruction encoding: Instruction type INST_TYPE_MIO_RD_SCBD can not have a wr scoreboard value other than 7 (no scbd)",
      requires = Some("dst_wr_sb")
    )
    val form = NVDisasm.Form(
      "legacy-memory",
      alternate = false,
      Vec.empty,
      Vec.empty,
      conditions = Vec.nonEmpty(condition)
    )

    assertEquals(Legality.check(database, form, Map.empty), Right(()))
    assertEquals(Legality.check(database, form, Map("dst_wr_sb" -> BigInt(7))), Right(()))
    assert(Legality.check(database, form, Map("dst_wr_sb" -> BigInt(6))).isLeft)

    val unrelated =
      form.copy(conditions =
        Vec.nonEmpty(condition.copy(
          expression = Binary(NVDisasm.BinaryOperator.Equal, Field("other", None), Number(7)),
          requires = None
        ))
      )
    assert(Legality.check(database, unrelated, Map.empty).isLeft)
  }
}

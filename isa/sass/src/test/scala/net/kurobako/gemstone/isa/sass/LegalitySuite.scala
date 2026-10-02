package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*

class LegalitySuite extends munit.FunSuite {
  import NVDisasm.ConditionExpression.*

  private val database = NVDisasm.Database("SM89", "test", 128, Vec.empty, Vec.empty)

  test("condition operators and helper calls resolve to semantic tokens while preserving unknown names") {
    assertEquals(
      NVDisasm.BinaryOperator.from("->"),
      NVDisasm.BinaryOperator.Implies
    )
    assertEquals(NVDisasm.UnaryOperator.from("@").spelling, "@")
    assertEquals(NVDisasm.BuiltinCall.from("VendorLookup").name, "VendorLookup")
    val unknown = Binary(NVDisasm.BinaryOperator.Unsupported("@"), Number(1), Number(2))
    assert(Legality.evaluate(
      database,
      unknown,
      Map.empty
    ).left.toOption.exists(_.contains("unsupported condition operator @")))
  }

  test("FORMAT semantic field categories are closed and unknown vendor names remain data") {
    val registerField: NVDisasm.Syntax.Field = NVDisasm.Syntax.Field("Register", "r", None, None)
    val vendorField: NVDisasm.Syntax.Field   = NVDisasm.Syntax.Field("VendorRegisterLike", "v", None, None)
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
        None,
        "typed",
        false,
        Vec.empty,
        Vec.empty,
        Vec.empty,
        conditions =
          Vec(NVDisasm.Condition("ERROR", Binary(NVDisasm.BinaryOperator.Equal, Number(0), Number(1)), message))
      )
      Legality.check(database, form, Map.empty).left.toOption.get
    }
    assertEquals(invalid("first wording").category, NVDisasm.FailureCategory.IllegalCandidate)
    assertEquals(invalid("changed wording").category, NVDisasm.FailureCategory.IllegalCandidate)
    assertEquals(
      NVDisasm.MetadataIssue.ambiguousInverse("TABLES_opex_sample", 7).kind,
      NVDisasm.MetadataIssueKind.PermittedStructuralAmbiguity
    )
    assertEquals(
      NVDisasm.MetadataIssue.ambiguousInverse("ordinary_table", 7).kind,
      NVDisasm.MetadataIssueKind.AmbiguousInverse
    )
    assertEquals(
      NVDisasm.MetadataIssue.ambiguousInverse("VarLatOperandEnc", 7).kind,
      NVDisasm.MetadataIssueKind.PermittedStructuralAmbiguity
    )
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
      "OPERAND_ERROR",
      Binary(
        NVDisasm.BinaryOperator.Equal,
        Binary(NVDisasm.BinaryOperator.Remainder, Field("Ra", None), Number(4)),
        Number(0)
      ),
      "unaligned register group"
    )
    val form = NVDisasm.Form(
      None,
      "matrix",
      false,
      Vec.empty,
      Vec.empty,
      Vec.empty,
      conditions = Vec(condition)
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

  test("FORMAT condition names resolve from normalized field bindings") {
    val condition = NVDisasm.Condition(
      "ERROR",
      Binary(NVDisasm.BinaryOperator.Equal, Field("sync", None), Number(1)),
      "requires sync"
    )
    val form = NVDisasm.Form(
      None,
      "sync-op",
      false,
      Vec.empty,
      Vec.empty,
      Vec.empty,
      syntax = Vec(NVDisasm.Syntax.Field("sync", "SyncMode", None, None)),
      conditions = Vec(condition)
    )
    val bindings = Legality.completeBindings(form, Map("SyncMode" -> BigInt(1)))
    assertEquals(bindings.get("sync"), Some(BigInt(1)))
    assertEquals(Legality.check(database, form, bindings), Right(()))
    assert(Legality.check(database, form, Legality.completeBindings(form, Map("SyncMode" -> BigInt(0)))).isLeft)
    val repeatedKind = form.copy(
      syntax = Vec(
        NVDisasm.Syntax.Field("register", "Ra", None, None),
        NVDisasm.Syntax.Field("register", "Rb", None, None)
      )
    )
    assertEquals(Legality.completeBindings(repeatedKind, Map("register" -> BigInt(4))).get("Ra"), None)
  }

  test("partial recovered lookup tables never authorize a condition") {
    val table = NVDisasm.LookupTable(
      "relation",
      Vec(NVDisasm.Row(Vec(BigInt(1)), BigInt(2))),
      Vec("unsupported wildcard")
    )
    val condition =
      Call(NVDisasm.BuiltinCall.Defined, Vec(Call(NVDisasm.BuiltinCall.Table("relation"), Vec(Number(1)))))
    assert(Legality.evaluate(database.copy(tables = Vec(table)), condition, Map.empty).isLeft)
    assertEquals(
      Legality.evaluate(database.copy(tables = Vec(table.copy(issues = Vec.empty))), condition, Map.empty),
      Right(BigInt(1))
    )
  }

  test("evaluates recovered integer parity predicates") {
    assertEquals(
      Legality.evaluate(database, Call(NVDisasm.BuiltinCall.IsEven, Vec(Number(12))), Map.empty),
      Right(BigInt(1))
    )
    assertEquals(
      Legality.evaluate(database, Call(NVDisasm.BuiltinCall.IsEven, Vec(Number(13))), Map.empty),
      Right(BigInt(0))
    )
    assertEquals(
      Legality.evaluate(database, Call(NVDisasm.BuiltinCall.IsOdd, Vec(Number(-3))), Map.empty),
      Right(BigInt(1))
    )
    assertEquals(
      Legality.evaluate(database, Call(NVDisasm.BuiltinCall.IsOdd, Vec(Field("Rd", None))), Map("Rd" -> BigInt(8))),
      Right(BigInt(0))
    )
    assert(Legality.evaluate(
      database,
      Call(NVDisasm.BuiltinCall.IsEven, Vec.empty[NVDisasm.ConditionExpression]),
      Map.empty
    ).isLeft)
    assert(Legality.evaluate(database, Call(NVDisasm.BuiltinCall.IsOdd, Vec(Number(1), Number(3))), Map.empty).isLeft)
  }

  test("skips only the unobservable legacy scoreboard value constraint") {
    val condition = NVDisasm.Condition(
      "ILLEGAL_INSTR_ENCODING_ERROR",
      Binary(NVDisasm.BinaryOperator.Equal, Field("dst_wr_sb", None), Number(7)),
      "Illegal instruction encoding: Instruction type INST_TYPE_MIO_RD_SCBD can not have a wr scoreboard value other than 7 (no scbd)"
    )
    val form = NVDisasm.Form(
      None,
      "legacy-memory",
      false,
      Vec.empty,
      Vec.empty,
      Vec.empty,
      conditions = Vec(condition)
    )

    assertEquals(Legality.check(database, form, Map.empty), Right(()))
    assertEquals(Legality.check(database, form, Map("dst_wr_sb" -> BigInt(7))), Right(()))
    assert(Legality.check(database, form, Map("dst_wr_sb" -> BigInt(6))).isLeft)

    val unrelated =
      form.copy(conditions =
        Vec(condition.copy(expression = Binary(NVDisasm.BinaryOperator.Equal, Field("other", None), Number(7))))
      )
    assert(Legality.check(database, unrelated, Map.empty).isLeft)
  }
}

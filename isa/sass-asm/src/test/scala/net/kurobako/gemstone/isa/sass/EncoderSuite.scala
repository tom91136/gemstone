package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.testing.Suite

class EncoderSuite extends Suite {
  private val opcodeField = NVDisasm.Assignment(
    "opcode",
    Vec(0, 1, 2, 3),
    Expression.Reference("Opcode", None, relaxed = false)
  )
  private val opcodeValue = NVDisasm.SymbolValue("TEST", 0xa)

  private def database(form: NVDisasm.Form, tables: Vec[NVDisasm.LookupTable] = Vec.empty): NVDisasm.Database =
    NVDisasm.Database("", 64, Vec(form), tables)

  private def word(bytes: Array[Byte]): BigInt = BigInt(1, bytes.reverse)

  test("encoder writes independent opcode and operand fields and decodes them back") {
    val form = NVDisasm.Form(
      "TEST",
      alternate = false,
      Vec(opcodeValue),
      Vec(
        opcodeField,
        NVDisasm.Assignment("A", Vec(4, 5, 6, 7), Expression.Reference("A", None, relaxed = false)),
        NVDisasm.Assignment(
          "B",
          Vec(8, 9),
          Expression.Scale(
            Expression.Reference("B", None, relaxed = false),
            2
          )
        )
      )
    )
    val bytes = Encoder.encode(
      NVDisasm.Encoding(database(form), form),
      "TEST",
      Map("A" -> BigInt(5), "B" -> BigInt(6))
    ).toOption.get
    assertEquals(word(bytes), BigInt(0x035a))
    val candidate = FieldDecoder.candidates(database(form), word(bytes)).toOption.get.head
    assertEquals(candidate.bindings("A"), BigInt(5))
    assertEquals(candidate.bindings("B"), BigInt(6))
  }

  test("preserved words supply only unassigned bits while assigned fields are recomputed") {
    val form = NVDisasm.Form(
      "TEST",
      alternate = false,
      Vec(opcodeValue),
      Vec(
        opcodeField,
        NVDisasm.Assignment("A", Vec(4, 5, 6, 7), Expression.Reference("A", None, relaxed = false)),
        NVDisasm.Assignment(
          "B",
          Vec(8, 9),
          Expression.Scale(
            Expression.Reference("B", None, relaxed = false),
            2
          )
        )
      )
    )
    val encoded = Encoder.encode(
      NVDisasm.Encoding(database(form), form),
      "TEST",
      Map("A" -> BigInt(2), "B" -> BigInt(4)),
      preserved = Some(BigInt("ffff", 16))
    ).toOption.get
    assertEquals(word(encoded), BigInt("fe2a", 16))
  }

  test("forward evaluation handles multiply, identity, fixed-float, and float-convert expressions") {
    val form = NVDisasm.Form(
      "TRANSFORMS",
      alternate = false,
      Vec(opcodeValue),
      Vec(
        opcodeField,
        NVDisasm.Assignment(
          "twice",
          Vec(4, 5, 6, 7),
          Expression.Multiply(
            Expression.Reference("twice", None, relaxed = false),
            2
          )
        ),
        NVDisasm.Assignment(
          "same",
          Vec(8, 9, 10, 11),
          Expression.Identity(
            Expression.Reference("same", None, relaxed = false),
            Expression.Scale(Expression.Reference("scaled", None, relaxed = false), 2)
          )
        ),
        NVDisasm.Assignment(
          "half",
          Vec(12, 13, 14, 15, 16, 17, 18, 19),
          Expression.FixedFloat(
            Expression.Reference("half", None, relaxed = false),
            FloatEncoding.F16Imm
          )
        ),
        NVDisasm.Assignment(
          "converted",
          Vec(20, 21, 22, 23, 24, 25, 26, 27),
          Expression.FloatConvert(
            Expression.Reference("converted", None, relaxed = false),
            "format",
            Vec(Vec(BigInt(0)) -> FloatEncoding.F16Imm),
            FloatEncoding.F32Imm
          )
        )
      )
    )
    val encoded = Encoder.encode(
      NVDisasm.Encoding(database(form), form),
      "TEST",
      Map(
        "twice"     -> BigInt(3),
        "same"      -> BigInt(4),
        "scaled"    -> BigInt(8),
        "half"      -> BigInt(0xab),
        "converted" -> BigInt(0xcd),
        "format"    -> BigInt(0)
      )
    ).toOption.get
    assertEquals(word(encoded), BigInt(0x0cdab46a))
  }

  test("encoder rejects values that overflow fields or cannot satisfy transforms") {
    val form = NVDisasm.Form(
      "TEST",
      alternate = false,
      Vec(opcodeValue),
      Vec(
        opcodeField,
        NVDisasm.Assignment("A", Vec(4, 5, 6, 7), Expression.Reference("A", None, relaxed = false)),
        NVDisasm.Assignment(
          "B",
          Vec(8, 9),
          Expression.Scale(
            Expression.Reference("B", None, relaxed = false),
            2
          )
        )
      )
    )
    assert(Encoder.encode(
      NVDisasm.Encoding(database(form), form),
      "TEST",
      Map("A" -> BigInt(16), "B" -> BigInt(4))
    ).isLeft)
    assert(Encoder.encode(
      NVDisasm.Encoding(database(form), form),
      "TEST",
      Map("A" -> BigInt(1), "B" -> BigInt(3))
    ).isLeft)
    assert(Encoder.encode(
      NVDisasm.Encoding(database(form), form),
      "OTHER",
      Map("A" -> BigInt(1), "B" -> BigInt(4))
    ).isLeft)
  }

  test("preserved lookup bits are accepted only when current inputs select that output") {
    val lookupForm = NVDisasm.Form(
      "LOOKUP",
      alternate = false,
      Vec(opcodeValue),
      Vec(
        opcodeField,
        NVDisasm.Assignment(
          "mapped",
          Vec(4, 5),
          Expression.Lookup(
            "ambiguous",
            Vec(Expression.Reference("input", None, relaxed = false))
          )
        )
      )
    )
    val table = NVDisasm.LookupTable(
      "ambiguous",
      Vec(
        NVDisasm.Row(Vec(BigInt(3)), BigInt(1)),
        NVDisasm.Row(Vec(BigInt(3)), BigInt(2))
      )
    )
    val db = database(lookupForm, Vec(table))
    assert(Encoder.encode(NVDisasm.Encoding(db, lookupForm), "TEST", Map("input" -> BigInt(3))).isLeft)
    val preserved = BigInt("802a", 16)
    assertEquals(
      Encoder.encode(
        NVDisasm.Encoding(db, lookupForm),
        "TEST",
        Map("input" -> BigInt(3)),
        Some(preserved)
      ).map(word(_)),
      Right(preserved)
    )
    assertEquals(
      Encoder.encode(NVDisasm.Encoding(db, lookupForm), "TEST", Map.empty, Some(preserved)).map(word(_)),
      Right(preserved)
    )
    assert(Encoder.encode(NVDisasm.Encoding(db, lookupForm), "TEST", Map("input" -> BigInt(4)), Some(preserved)).isLeft)
    assert(Encoder.encode(NVDisasm.Encoding(db, lookupForm), "TEST", Map.empty, Some(BigInt("803a", 16))).isLeft)
  }

  test("relaxed constant fields preserve their original encoding") {
    val relaxed = NVDisasm.Form(
      "RELAXED",
      alternate = false,
      Vec(opcodeValue),
      Vec(
        opcodeField,
        NVDisasm.Assignment("reserved", Vec(4, 5, 6, 7), Expression.Constant(BigInt(1), relaxed = true))
      )
    )
    val encoded = Encoder.encode(
      NVDisasm.Encoding(database(relaxed), relaxed),
      "TEST",
      Map.empty,
      Some(BigInt("7a", 16))
    ).toOption.get
    assertEquals(word(encoded), BigInt("7a", 16))
  }

  test("conflicting overlapping field assignments are rejected") {
    val overlapping = NVDisasm.Form(
      "OVERLAP",
      alternate = false,
      Vec(opcodeValue),
      Vec(
        opcodeField,
        NVDisasm.Assignment("A", Vec(3, 4, 5, 6), Expression.Reference("A", None, relaxed = false))
      )
    )
    assert(Encoder.encode(NVDisasm.Encoding(database(overlapping), overlapping), "TEST", Map("A" -> BigInt(0))).isLeft)
  }

  test("invalid field positions are rejected before computing the assigned mask") {
    Vec(Vec(-1), Vec(64), Vec(4, 4)).foreach { positions =>
      val form = NVDisasm.Form(
        "INVALID",
        alternate = false,
        Vec(opcodeValue),
        Vec(opcodeField, NVDisasm.Assignment("A", positions, Expression.Constant(BigInt(0), relaxed = false)))
      )
      assert(Encoder.encode(NVDisasm.Encoding(database(form), form), "TEST", Map.empty).isLeft)
    }
  }
}

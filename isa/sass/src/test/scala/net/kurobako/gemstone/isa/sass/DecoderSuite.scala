package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.fixtures.*

class DecoderSuite extends munit.FunSuite {
  private def reference(name: String): NVDisasm.Expression = NVDisasm.Expression.Reference(name, None, false)
  private def form(name: String, fields: Vec[NVDisasm.Assignment]): NVDisasm.Form = NVDisasm.Form(
    Some(0),
    name,
    false,
    Vec(NVDisasm.OpcodeValue("ADDpipe", BigInt(5)), NVDisasm.OpcodeValue("ADD", BigInt(5))),
    NVDisasm.Assignment("opcode", Vec(0, 2, 7), reference("Opcode")) +: fields,
    Vec.empty
  )
  private def database(
      forms: Vec[NVDisasm.Form],
      tables: Vec[NVDisasm.LookupTable] = Vec.empty
  ): NVDisasm.Database =
    NVDisasm.Database("fixture", "fixture", 16, forms, tables)

  test("every pinned target has a decoder with the matching word layout and provenance") {
    Metadata.catalogue.targets.foreach { target =>
      val database = NVDisasm.forTarget(target.name).getOrElse(fail(s"missing ${target.name}"))
      val sm       = target.name.drop(2).takeWhile(_.isDigit).toInt
      assertEquals(database.target, target.name)
      assertEquals(database.sha256, target.encodingKey)
      assertEquals(database.width, if (sm < 50) 64 else if (sm < 70) 88 else 128)
      assert(database.forms.nonEmpty)
    }
  }

  test("discontiguous fields use physical bit order and opcode aliases retain one candidate") {
    val data   = database(Vec(form("add", Vec(NVDisasm.Assignment("dst", Vec(4, 5), reference("Rd"))))))
    val result = FieldDecoder.candidates(data, BigInt(0x91)).fold(fail(_), identity)
    assertEquals(result.size, 1)
    assertEquals(result.head.opcode, "ADD")
    assertEquals(result.head.bindings("Rd"), BigInt(1))
    assertEquals(FieldDecoder.extract(BigInt(0x81), Vec(0, 2, 7)), BigInt(5))
    assert(FieldDecoder.candidates(data, BigInt(0)).isLeft)
    assert(FieldDecoder.candidates(data, BigInt(1) << 16).isLeft)
    assert(FieldDecoder.candidates(data, BigInt(-1)).isLeft)
  }

  test("strict constants discriminate forms while starred defaults do not") {
    val strict  = NVDisasm.Assignment("mode", Vec(4), NVDisasm.Expression.Constant(BigInt(1), false))
    val relaxed = NVDisasm.Assignment("schedule", Vec(5), NVDisasm.Expression.Constant(BigInt(1), true))
    val data    = database(Vec(form("add", Vec(strict, relaxed))))
    assert(FieldDecoder.candidates(data, BigInt(0x81)).isLeft)
    assert(FieldDecoder.candidates(data, BigInt(0x91)).isRight)
    assert(FieldDecoder.candidates(data, BigInt(0xb1)).isRight)
  }

  test("lookup inverses retain unresolved alternatives and reject unlisted encodings") {
    val lookup = NVDisasm.LookupTable(
      "modes",
      Vec(
        NVDisasm.Row(Vec(BigInt(3), BigInt(4)), BigInt(0)),
        NVDisasm.Row(Vec(BigInt(3), BigInt(5)), BigInt(0)),
        NVDisasm.Row(Vec(BigInt(6), BigInt(7)), BigInt(1))
      )
    )
    val field = NVDisasm.Assignment(
      "mode",
      Vec(4, 5),
      NVDisasm.Expression.Lookup("modes", Vec(reference("a"), reference("b")))
    )
    val data      = database(Vec(form("add", Vec(field))), Vec(lookup))
    val ambiguous = FieldDecoder.candidates(data, BigInt(0x81)).fold(fail(_), identity).head
    assertEquals(ambiguous.bindings("a"), BigInt(3))
    assert(!ambiguous.bindings.contains("b"))
    assert(ambiguous.issues.exists(_.kind == NVDisasm.MetadataIssueKind.AmbiguousInverse))
    val unique = FieldDecoder.candidates(data, BigInt(0x91)).fold(fail(_), identity).head
    assertEquals(unique.bindings("b"), BigInt(7))
    assert(FieldDecoder.candidates(data, BigInt(0xa1)).isLeft)
  }

  test("lookup inverses are rechecked after later fields constrain their inputs") {
    val lookup = NVDisasm.LookupTable(
      "relation",
      Vec(
        NVDisasm.Row(Vec(BigInt(0), BigInt(0)), BigInt(0)),
        NVDisasm.Row(Vec(BigInt(1), BigInt(1)), BigInt(0))
      )
    )
    val fields = Vec(
      NVDisasm.Assignment(
        "relation",
        Vec(3),
        NVDisasm.Expression.Lookup("relation", Vec(reference("a"), reference("b")))
      ),
      NVDisasm.Assignment("a", Vec(4), reference("a")),
      NVDisasm.Assignment("b", Vec(5), reference("b"))
    )
    val data = database(Vec(form("related", fields)), Vec(lookup))
    assert(FieldDecoder.candidates(data, BigInt(0xa1)).isLeft)
    val consistent = FieldDecoder.candidates(data, BigInt(0xb1)).fold(fail(_), identity).head
    assertEquals(consistent.bindings("a"), BigInt(1))
    assertEquals(consistent.bindings("b"), BigInt(1))
    assert(consistent.issues.isEmpty)
  }

  test("different matching forms and unresolved transformations remain visible") {
    val unknown = NVDisasm.Assignment("future", Vec(4), NVDisasm.Expression.Unsupported("future(x)"))
    val data    = database(Vec(form("first", Vec(unknown)), form("second", Vec.empty)))
    val result  = FieldDecoder.candidates(data, BigInt(0x81)).fold(fail(_), identity)
    assertEquals(result.map(_.name).toSet, Set("first", "second"))
    assert(result.find(_.name == "first").exists(_.issues.nonEmpty))
  }

  test("generated decoder is pinned to SM89 and validates instruction length") {
    val database = NVDisasm.forTarget("SM89").getOrElse(fail("missing SM89"))
    assertEquals(database.forms.size, 1340)
    assertEquals(database.sha256, Metadata.catalogue.targets.find(_.name == "SM89").get.encodingKey)
    assert(database.forms.forall(_.fields.forall(field =>
      field.positions.distinct == field.positions &&
        field.positions.forall(bit => bit >= 0 && bit < 128)
    )))
    assert(FieldDecoder.candidates("SM89", Array.emptyByteArray).isLeft)
    assert(FieldDecoder.candidates("SM89", Array.fill[Byte](15)(0)).isLeft)
    assert(NVDisasm.forTarget("SM86").nonEmpty)
    assert(NVDisasm.forTarget("SM35").nonEmpty)
  }

  test("SAXPY and integer copy oracle words have structural binary candidates") {
    Vec("saxpy" -> SaxpyFixtures.saxpy, "copy-add" -> CopyAddFixtures.copyAdd).foreach { (name, text) =>
      SASSOracleWords.read(text).foreach { (address, bytes) =>
        val candidates = FieldDecoder.candidates("SM89", bytes)
          .fold(error => fail(s"$name+0x${address.toHexString}: $error"), identity)
        assert(candidates.exists(!_.alternate), s"$name+0x${address.toHexString}: no primary form")
      }
    }
  }

  test("NVIDIA mutation oracle distinguishes ignored bits, defaults, constants and scaled constant addresses") {
    val probes   = FixtureMutations.read(SaxpyFixtures.mutationsMetadata)
    val database = NVDisasm.forTarget("SM89").get
    probes.foreach { probe =>
      val result = FieldDecoder.candidates(database, FixtureMutations.word(probe))
      if (probe.expectedExitCode != 0) assert(result.isLeft, probe.id)
      else {
        val candidates = result.fold(error => fail(s"${probe.id}: $error"), identity)
        probe.id match {
          case "mov-const-bank-address" =>
            assert(
              candidates.exists(c =>
                c.bindings.get("Sb_bank").contains(BigInt(3)) &&
                  c.bindings.get("Sb_addr").contains(BigInt(0x48c))
              ),
              candidates.toString
            )
          case "ldc-direct-byte-offset" =>
            assert(candidates.exists(_.bindings.get("Ra_offset").contains(BigInt(0x1234))), candidates.toString)
          case _ => assert(candidates.exists(_.opcode == "NOP"), candidates.toString)
        }
      }
    }
  }
}

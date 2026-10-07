package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class DecoderSuite extends munit.FunSuite {
  private def reference(name: String): NVDisasm.Expression = NVDisasm.Expression.Reference(name, None, false)
  private def form(name: String, fields: Vec[NVDisasm.Assignment]): NVDisasm.Form = NVDisasm.Form(
    name,
    false,
    Vec(NVDisasm.OpcodeValue("ADD", BigInt(5))),
    NVDisasm.Assignment("opcode", Vec(0, 2, 7), reference("Opcode")) +: fields
  )
  private def fixture(
      forms: Vec[NVDisasm.Form],
      tables: Vec[NVDisasm.LookupTable] = Vec.empty
  ): NVDisasm.Database =
    NVDisasm.Database("fixture", 64, forms, tables)

  test("every target has a decoder with the matching word layout") {
    Vec(35, 37, 50, 52, 53, 60, 61, 62, 70, 72, 75, 80, 86, 87, 88, 89, 90, 100, 101, 103, 110, 120, 121).foreach {
      sm =>
        val tables = decoder(sm)
        assertEquals(tables.width.raw, if (sm < 50) 64 else if (sm < 70) 88 else 128)
        assert(tables.forms.nonEmpty)
    }
    assert(Target.parse("SM73").isLeft)
  }

  test("discontiguous fields use physical bit order") {
    val data   = fixture(Vec(form("add", Vec(NVDisasm.Assignment("dst", Vec(4, 5), reference("Rd"))))))
    val result = FieldDecoder.candidates(data, BigInt(0x91)).fold(fail(_), identity)
    assertEquals(result.size, 1)
    assertEquals(result.head.opcode, "ADD")
    assertEquals(result.head.bindings("Rd"), BigInt(1))
    assertEquals(FieldDecoder.extract(BigInt(0x81), Vec(0, 2, 7)), BigInt(5))
    assert(FieldDecoder.candidates(data, BigInt(0)).isLeft)
    assert(FieldDecoder.candidates(data, BigInt(1) << 64).isLeft)
    assert(FieldDecoder.candidates(data, BigInt(-1)).isLeft)
  }

  test("strict constants discriminate forms while starred defaults do not") {
    val strict  = NVDisasm.Assignment("mode", Vec(4), NVDisasm.Expression.Constant(BigInt(1), false))
    val relaxed = NVDisasm.Assignment("schedule", Vec(5), NVDisasm.Expression.Constant(BigInt(1), true))
    val data    = fixture(Vec(form("add", Vec(strict, relaxed))))
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
    val data      = fixture(Vec(form("add", Vec(field))), Vec(lookup))
    val ambiguous = FieldDecoder.candidates(data, BigInt(0x81)).fold(fail(_), identity).head
    assertEquals(ambiguous.bindings("a"), BigInt(3))
    assert(!ambiguous.bindings.contains("b"))
    assertEquals(ambiguous.issues, Vec(NVDisasm.MetadataIssue.AmbiguousInverse("modes", 0)))
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
    val data = fixture(Vec(form("related", fields)), Vec(lookup))
    assert(FieldDecoder.candidates(data, BigInt(0xa1)).isLeft)
    val consistent = FieldDecoder.candidates(data, BigInt(0xb1)).fold(fail(_), identity).head
    assertEquals(consistent.bindings("a"), BigInt(1))
    assertEquals(consistent.bindings("b"), BigInt(1))
    assert(consistent.issues.isEmpty)
  }

  test("different matching forms and unresolved transformations remain visible") {
    val unknown = NVDisasm.Assignment(
      "future",
      Vec(4),
      NVDisasm.Expression.FloatConvert(reference("value"), "format", Vec.empty, FloatEncoding.F32Imm)
    )
    val data   = fixture(Vec(form("first", Vec(unknown)), form("second", Vec.empty)))
    val result = FieldDecoder.candidates(data, BigInt(0x81)).fold(fail(_), identity)
    assertEquals(result.map(_.name).toSet, Set("first", "second"))
    assert(result.find(_.name == "first").exists(_.issues.nonEmpty))
  }

  test("generated decoder is pinned to SM89 and validates instruction length") {
    val database = decoder(89)
    assertEquals(database.forms.size, 1340)
    assert(database.forms.forall(_.fields.forall(field =>
      field.positions.distinct == field.positions &&
        field.positions.forall(bit => bit >= 0 && bit < 128)
    )))
    assert(FieldDecoder.candidates(decoder(89), Array.emptyByteArray).isLeft)
    assert(FieldDecoder.candidates(decoder(89), Array.fill[Byte](15)(0)).isLeft)
  }

  test("SAXPY and integer copy oracle words have structural binary candidates") {
    Vec("saxpy" -> "saxpyO0Sm89", "copy_add" -> "copyAddSm89").foreach { (name, fixture) =>
      Listing.words(Fixture.listing(fixture), name, 89).value.foreach { (address, bytes) =>
        val candidates = FieldDecoder.candidates(decoder(89), bytes)
          .fold(error => fail(s"$name+0x${address.raw.toHexString}: $error"), identity)
        assert(candidates.exists(!_.alternate), s"$name+0x${address.raw.toHexString}: no primary form")
      }
    }
  }

  test("NVIDIA mutation oracle distinguishes ignored bits, defaults, constants and scaled constant addresses") {
    val probes   = FixtureMutations.saxpy
    val database = decoder(89)
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

  private def assertInstruction(
      target: Target,
      encoded: String,
      opcode: String,
      operands: Vec[String]
  ): Unit = {
    val instruction = InstructionDecoder.decode(
      0,
      bytes.hex(encoded),
      target
    ).fold(fail(_), identity)
    val expected = Parser.parse(s"$opcode ${operands.mkString(", ")};").fold(fail(_), identity)
    assertEquals(instruction.mnemonic, expected.mnemonic)
    assertEquals(instruction.modifiers, expected.modifiers)
    assertEquals(instruction.operands, expected.operands)
  }

  test("SM35 resolves the canonical unnegated PSignFFMA inverse") {
    // Pinned saxpy-sm35.cubin, saxpy+0x78; ambiguous PSignFFMA output 0.
    assertInstruction(
      target(35),
      "1a101c2a0000004c",
      "FFMA",
      Vec("R6", "R4", "c[0x0][0x150]", "R0")
    )
  }

  test("SM35 resolves the canonical unnegated PSignMAD inverse") {
    // Pinned multiply-subtract-sm35.cubin, subtract_product+0x38; PSignMAD output 0.
    assertInstruction(
      target(35),
      "16001c29000c1052",
      "IMAD.U32.U32.HI.X",
      Vec("R5", "R0", "c[0x0][0x148]", "R3")
    )
  }

  test("SM35 filters PSign aliases with the IADD legality constraints") {
    // Pinned constant-sm35.cubin, constant_read+0x38; PSign output 0 forbids both negations.
    assertInstruction(
      target(35),
      "0a141c2800008460",
      "IADD",
      Vec("R2.CC", "R5", "c[0x0][0x140]")
    )
  }

  test("SM50 resolves PSign IADD after undoing bundle control bits") {
    // Pinned compiler_forms-sm50.cubin, xmad_patterns+0x78. Last 3 bytes are the slot control.
    assertInstruction(
      target(50),
      "020327050080104cf60700",
      "IADD",
      Vec("R2.CC", "R3", "c[0x0][0x148]")
    )
  }

  test("SM50 resolves PSignFFMA after undoing bundle control bits") {
    // Pinned saxpy-sm50.cubin, saxpy+0xb8. Last 3 bytes are the slot control.
    assertInstruction(
      target(50),
      "0004470500038049f20701",
      "FFMA",
      Vec("R0", "R4", "c[0x0][0x150]", "R6")
    )
  }

  test("SM35 omits all encodings of the default H0 half selector") {
    assertInstruction(target(35), "1ed41c03000001e6", "I2I.S16.S16", Vec("R7", "-R6"))
  }

  test("SM35 names DSETP comparisons and branch condition tests as nvdisasm does") {
    val comparisons =
      Vec("F", "LT", "EQ", "LE", "GT", "NE", "GE", "NUM", "NAN", "LTU", "EQU", "LEU", "GTU", "NEU", "GEU")
    comparisons.zipWithIndex.foreach { (comparison, value) =>
      val word = f"${0xdc001c007f9c181eL | (value.toLong << 51)}%016x".grouped(2).toVector.reverse.mkString
      assertInstruction(target(35), word, s"DSETP.$comparison.AND", Vec("P0", "PT", "R6", "RZ", "PT"))
    }
    Vec(16 -> "OFF", 18 -> "SFF", 21 -> "SFT", 23 -> "OFT", 30 -> "RLE", 31 -> "RGT").foreach { (value, test) =>
      val word        = f"${0x1200000000000000L | (value.toLong << 2)}%016x".grouped(2).toVector.reverse.mkString
      val instruction = InstructionDecoder.decode(0, bytes.hex(word), target(35)).fold(fail(_), identity)
      assertEquals(instruction.modifiers.map(_.name), Vec("CC", test))
    }
  }

  test("SM80 retains the non-default LDSM two-register count qualifier") {
    assertInstruction(target(80), "3b780c0c000000000001000000240e00", "LDSM.16.M88.2", Vec("R12", "[R12]"))
  }
}

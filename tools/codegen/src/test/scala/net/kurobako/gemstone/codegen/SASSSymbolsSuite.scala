package net.kurobako.gemstone.codegen

import scala.concurrent.duration.*

class SASSSymbolsSuite extends munit.FunSuite {
  override val munitTimeout: Duration = 120.seconds

  test("enumerations retain aliases, implicit values, quoted names and ranges") {
    val parsed =
      SASSSymbols.parse("SpecialRegister SR(0..3)=(0..3), SR_TID=32, \"SR_TID.X\", \"SR_TID.Y\", Alias=33;").fold(
        fail(_),
        identity
      )
    assertEquals(parsed.name, "SpecialRegister")
    assertEquals(
      parsed.values.map(value => value.name -> value.number),
      Vector(
        "SR0"      -> BigInt(0),
        "SR1"      -> BigInt(1),
        "SR2"      -> BigInt(2),
        "SR3"      -> BigInt(3),
        "SR_TID"   -> BigInt(32),
        "SR_TID.X" -> BigInt(33),
        "SR_TID.Y" -> BigInt(34),
        "Alias"    -> BigInt(33)
      )
    )
  }
  test("binary values, implicit tokens and comments are parsed without guessing") {
    val parsed = SASSSymbols.parse("REQ req*, other=0b11, // comment\n next, hex=0xff").fold(fail(_), identity)
    assertEquals(parsed.values.map(_.number), Vector(0, 3, 4, 255).map(BigInt(_)))
    assert(SASSSymbols.parse("Bad A = runtime(foo)").isLeft)
  }
  test("starred enum entries retain their default and ambiguous union defaults are omitted") {
    val parsed = SASSSymbols.parse("USCHED_INFO YIELD_SB *=0, NONE=1;").fold(fail(_), identity)
    assertEquals(parsed.default, Some(BigInt(0)))
    assert(parsed.values.find(_.name == "YIELD_SB").exists(_.default))

    def compile(secondDefault: Int): Vector[SASSSymbols.Vocabulary] = {
      val description = SASSDescription.Description(
        s"REGISTERS\n A FIRST *=0; B SECOND *=$secondDefault; C = A + B;\nTABLES",
        Vector.empty
      )
      SASSSymbols.compile(description)
    }
    assertEquals(compile(0).find(_.name == "C").flatMap(_.default), Some(BigInt(0)))
    assertEquals(compile(1).find(_.name == "C").flatMap(_.default), None)
  }

  test("all modern recovered vocabularies compile, including named unions") {
    val modern = ExtractSASS.expected("modern").values.toSet
    generator.sassReferences.tables.filter(table => modern(table.digest)).foreach { table =>
      val vocabularies = SASSSymbols.compile(table.description)
      assert(vocabularies.find(_.name == "Register").exists(_.values.exists(_.name == "RZ")), table.digest)
    }
  }
}

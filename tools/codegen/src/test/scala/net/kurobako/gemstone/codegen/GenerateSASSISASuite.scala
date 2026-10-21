package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.security.MessageDigest
import java.util.zip.GZIPOutputStream
import scala.concurrent.duration.*
import scala.util.Using

class GenerateSASSISASuite extends munit.FunSuite {
  override val munitTimeout: Duration = 120.seconds

  private def mask(offset: Int): String = "." * offset + "XXXX" + "." * (124 - offset)
  private val description               =
    s"""ARCHITECTURE "Volta"
       |PARAMETERS
       |  MAX_REG_COUNT = 255
       |CONSTANTS
       |  UNUSED = 1
       |STRING_MAP
       |FUNIT uC
       |  ENCODING WIDTH 128;
       |  Dest '${mask(0)}'
       |  Src '${mask(4)}'
       |  Op '${mask(8)}'
       |REGISTERS
       |  Register R(0..14), RZ = 15;
       |TABLES
       |CLASS "add_register"
       |FORMAT Opcode Register:Rd ',' Register:Ra ;
       |CONDITIONS
       |  ERROR Rd != `Register@RZ : "destination must be writable"
       |  ERROR batch_t -> usched_info : "scheduling"
       |OPCODES
       | ADD = 0b0001;
       | ADDint_pipe = 0b0001;
       |ENCODING
       | Op = Opcode;
       | Dest = Rd;
       | Src = Ra;
       |""".stripMargin

  private def sha256(bytes: Array[Byte]): String =
    MessageDigest.getInstance("SHA-256").digest(bytes).map(b => f"${b & 255}%02x").mkString

  private def bundle(text: String, targets: Vector[String]): GenerateSASSISA.Bundle = generator.inDirectory {
    directory =>
      val bytes  = text.getBytes(UTF_8)
      val digest = sha256(bytes)
      val _      = Files.createDirectory(directory.resolve("tables"))
      Using.resource(GZIPOutputStream(Files.newOutputStream(directory.resolve(s"tables/$digest.txt.gz"))))(
        _.write(bytes)
      )
      val manifest = ujson.Obj("targets" -> ujson.Arr.from(targets.map(name =>
        ujson.Obj("name" -> name, "tool" -> "modern", "table" -> digest)
      )))
      val _ = Files.writeString(directory.resolve("manifest.json"), ujson.write(manifest))
      GenerateSASSISA.read(directory)
  }

  test("reader verifies digests and omits architecture-suffixed aliases") {
    val read = bundle(description, Vector("SM90", "SM90a"))
    assertEquals(read.targets.map(_._1), Vector("SM90"))
    assertEquals(read.tables.size, 1)
    generator.inDirectory { directory =>
      val _ = Files.createDirectory(directory.resolve("tables"))
      Using.resource(GZIPOutputStream(Files.newOutputStream(directory.resolve(s"tables/${"0" * 64}.txt.gz"))))(
        _.write(description.getBytes(UTF_8))
      )
      val manifest = ujson.Obj("targets" -> ujson.Arr(ujson.Obj("name" -> "SM89", "table" -> "0" * 64)))
      val _        = Files.writeString(directory.resolve("manifest.json"), ujson.write(manifest))
      val error    = intercept[IllegalArgumentException](GenerateSASSISA.read(directory))
      assert(error.getMessage.contains("digest mismatch"))
    }
  }

  test("catalogue keeps legality conditions, drops pipe aliases and scheduling constraints, and shares records") {
    val catalogue = GenerateSASSISA.renderCatalogue(bundle(description, Vector("SM89", "SM90")))
    val strings   = catalogue.strings
    assertEquals(strings.distinct, strings)
    assertEquals(catalogue.databases.size, 1)
    assertEquals(catalogue.targets.map((name, database) => strings(name) -> database), Vector("SM89" -> 0, "SM90" -> 0))
    val form = catalogue.forms(catalogue.databases.head.forms.head)
    assertEquals(form.opcodes.map((name, value) => strings(name) -> value), Vector("ADD" -> BigInt(1)))
    assertEquals(
      form.conditions.map(id => strings(catalogue.conditions(id).message)),
      Vector("destination must be writable")
    )
    assertEquals(catalogue.databases.head.parameters, Vector.empty)
    assertEquals(catalogue.vocabularies.map(vocabulary => strings(vocabulary.name)), Vector("Register"))
    val encoded = ujson.write(upickle.default.writeJs(catalogue))
    assertEquals(ujson.write(upickle.default.writeJs(GenerateSASSISA.renderCatalogue(read(description)))), encoded)
  }

  private def read(text: String): GenerateSASSISA.Bundle = bundle(text, Vector("SM89", "SM90"))

  test("case-colliding opcodes keep their word boundaries") {
    val source = SASSInstructionRenderer.render(Vector("UCGABAR_ARV", "UCGABARARV", "MOV").map(opcode =>
      SASSInstructionModel.Shape(opcode, Vector.empty, Vector.empty)
    ))("Form.scala")
    assert(source.contains("case MovNoOperands("))
    assert(source.contains("case Ucgabar_ArvNoOperands("))
    assert(source.contains("case UcgabararvNoOperands("))
  }

  test("recovered descriptions preprocess, compile and render a catalogue for every pinned target") {
    val references = generator.sassReferences
    val pinned     = ExtractSASS.expected.values.flatten.toMap
    assertEquals(references.targets.toMap, pinned.filterNot((name, _) => name.last.isLetter))
    assertEquals(references.tables.map(_.digest).toSet, pinned.values.toSet)
    references.tables.foreach { table =>
      assert(table.description.preamble.startsWith("ARCHITECTURE "), table.digest)
      table.description.classes.foreach { form =>
        assert(!form.raw.linesIterator.exists(_.trim.startsWith("##")), form.name)
        assert(form.opcodes.nonEmpty && Vector("ENCODING", "REMAP").exists(form.section(_).nonEmpty), form.name)
      }
    }
    val catalogue = GenerateSASSISA.renderCatalogue(references)
    assertEquals(catalogue.databases.size, references.tables.size)
    assertEquals(catalogue.targets.size, references.targets.size)
    assert(catalogue.databases.forall(_.forms.nonEmpty))
  }
}

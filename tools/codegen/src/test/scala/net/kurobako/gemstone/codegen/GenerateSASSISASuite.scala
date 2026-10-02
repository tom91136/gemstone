package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.util.zip.GZIPOutputStream
import scala.concurrent.duration.*
import scala.util.Using

class GenerateSASSISASuite extends munit.FunSuite {
  override val munitTimeout: Duration = 120.seconds

  private def source(kind: String, fields: String): String =
    s"""ARCHITECTURE "Volta"
       |REGISTERS
       |  Register R0, RZ;
       |CLASS "add_register"
       |FORMAT Opcode $kind:Rd ',' Register:Ra $fields;
       |OPCODES
       | ADD = 0b0001;
       |ENCODING
       | Dest = Rd;
       | Src = Ra;
       |""".stripMargin

  private def table(text: String): GenerateSASSISA.Table =
    GenerateSASSISA.Table("fixture", SASSDescription.parse(text).fold(e => fail(e.message), identity))

  test("compatible binding layouts share types while target constraints remain in descriptions") {
    val a       = table(source("Register", ""))
    val b       = table(source("NonZeroRegister", ""))
    val c       = table(source("Register", "',' Predicate:Pu"))
    val schemas = GenerateSASSISA.instructionSchemas(Vector(a, b, c))
    assertEquals(schemas.size, 2)
    assertEquals(schemas.head.fields, Vector("Rd", "Ra"))
    assertNotEquals(a.description.classes.head.fields, b.description.classes.head.fields)
    assertEquals(GenerateSASSISA.instructionSchemas(Vector(c, b, a)), schemas)
    val scala = SASSInstructionRenderer.render(SASSInstructionModel.shapes(a.description.classes))("Instruction.scala")
    assert(scala.contains("case AddRegReg("))
    assert(scala.contains("Instruction.AddRegRegDefinition"))
  }

  test("reference reader verifies digest before interpreting a table") {
    generator.inDirectory { directory =>
      val bytes  = source("Register", "").getBytes(UTF_8)
      val digest = MessageDigest.getInstance("SHA-256").digest(bytes).map(b => f"${b & 255}%02x").mkString
      val file   = directory.resolve("table.gz")
      Using.resource(GZIPOutputStream(Files.newOutputStream(file)))(_.write(bytes))
      val manifest = ujson.Obj(
        "format"  -> 1,
        "targets" -> ujson.Arr(ujson.Obj("name" -> "SM89", "tool" -> "modern", "table" -> digest)),
        "tables"  -> ujson.Arr(ujson.Obj("sha256" -> digest, "path" -> "table.gz", "classes" -> 1))
      )
      val _ = Files.writeString(directory.resolve("manifest.json"), ujson.write(manifest))
      assertEquals(GenerateSASSISA.read(directory).tables.size, 1)
      Using.resource(GZIPOutputStream(Files.newOutputStream(file)))(_.write("corrupt".getBytes(UTF_8)))
      val error = intercept[IllegalArgumentException](GenerateSASSISA.read(directory))
      assert(error.getMessage.contains("digest mismatch"))
    }
  }

  test("unresolved preprocessing fragments never become empty instruction types") {
    val partial = table(source("Register", "").replace(
      "FORMAT Opcode Register:Rd ',' Register:Ra ;",
      "##IF DEFINED(ARCH_SEMANTICS)\nFORMAT Register:Rd;\n##ENDIF"
    ))
    assertEquals(GenerateSASSISA.instructionSchemas(Vector(partial)), Vector.empty)
    assert(partial.description.classes.head.raw.contains("##IF"))
  }

  test("recovered descriptions and active preprocessing stay checked in the codegen cache") {
    val bundle = GenerateSASSISA.read(Path.of("target/sass-codegen-references"))
    assertEquals(bundle.manifest("targets").arr.size, 36)
    assertEquals(bundle.tables.size, 16)
    val schemas   = GenerateSASSISA.instructionSchemas(bundle.tables)
    val schemaIds = schemas.zipWithIndex.toMap
    bundle.tables.foreach { table =>
      assertEquals(table.digest.length, 64)
      assert(table.description.preamble.startsWith("ARCHITECTURE "))
      table.description.classes.foreach { form =>
        val unresolved = form.conditional || form.raw.linesIterator.exists(_.trim.startsWith("##"))
        if (unresolved) assert(form.raw.linesIterator.exists(_.trim.startsWith("##")))
        else {
          form.opcodes.foreach(name => assert(name.nonEmpty))
          val schema = GenerateSASSISA.Schema(form.name, form.fields.map(_.name).distinct)
          assert(schemaIds.contains(schema), form.name)
          assert(form.section("ENCODING").nonEmpty, form.name)
          assert(form.opcodes.nonEmpty, form.name)
        }
      }
    }
    val sm35Digest = bundle.manifest("targets").arr.find(_("name").str == "SM35")
      .map(_("table").str).getOrElse(fail("missing SM35 reference target"))
    val sm35   = bundle.tables.find(_.digest == sm35Digest).getOrElse(fail("missing SM35 reference description"))
    val raw    = sm35.description.classes
    val active = sm35.encodingDescription.classes
    assert(active.nonEmpty)
    val rawTyped = raw.filterNot(form => form.conditional || form.raw.linesIterator.exists(_.trim.startsWith("##")))
      .map(form => GenerateSASSISA.Schema(form.name, form.fields.map(_.name).distinct) -> form.opcodes)
    val added = active.filterNot { form =>
      val schema = GenerateSASSISA.Schema(form.name, form.fields.map(_.name).distinct)
      schemaIds.get(schema).exists(id => rawTyped.contains(schemas(id) -> form.opcodes))
    }
    assert(added.nonEmpty, "expected preprocessed SM35 forms not present in raw forms")
    added.foreach(form =>
      assert(schemaIds.contains(GenerateSASSISA.Schema(form.name, form.fields.map(_.name).distinct)))
    )
  }

  test("runtime metadata retains target keys and the schema bound") {
    val manifest = ujson.Obj("targets" -> ujson.Arr(
      ujson.Obj("name" -> "SM89", "tool" -> "modern", "table" -> "a" * 64),
      ujson.Obj("name" -> "SM90", "tool" -> "modern", "table" -> "b" * 64)
    ))
    val bundle  = GenerateSASSISA.Bundle(manifest, Vector.empty)
    val schemas = Vector(GenerateSASSISA.Schema("IADD3", Vector("Rd", "Ra", "Rb")))
    val encoded = GenerateSASSISA.renderCatalogue(bundle, schemas)
    assertEquals(encoded("format").num.toInt, 3)
    val strings = encoded("strings").arr.map(_.str).toVector
    assertEquals(strings.distinct.size, strings.size)
    assertEquals(encoded("schemaCount").num.toInt, schemas.size)
    assert(!encoded.obj.contains("schemas"))
    assert(!strings.contains("IADD3"))
    assertEquals(
      encoded("targets").arr.map(row => strings(row("encodingKey").num.toInt)).toVector,
      Vector("a" * 64, "b" * 64)
    )
    assertEquals(encoded("targets").arr.size, 2)
  }

  test("decoder serializer shares typed expression, syntax, condition and domain records deterministically") {
    val expression = ujson.Obj("$type" -> "Reference", "name" -> "SrcA", "attribute" -> ujson.Null, "relaxed" -> false)
    def database(name: String): ujson.Value = ujson.Obj(
      "target"     -> name,
      "sha256"     -> (if (name == "SM89") "a" * 64 else "b" * 64),
      "width"      -> 128,
      "parameters" -> ujson.Obj("PARAM" -> 1),
      "constants"  -> ujson.Obj(),
      "symbols"    -> ujson.Arr(
        ujson.Obj(
          "name"    -> "Registers",
          "default" -> ujson.Arr(0),
          "values"  -> ujson.Arr(ujson.Obj("name" -> "R0", "value" -> 0))
        ),
        ujson.Obj("name" -> "Modes", "values" -> ujson.Arr(ujson.Obj("name" -> "OFF", "value" -> 0)))
      ),
      "tables" -> ujson.Arr(ujson.Obj("name" -> "TABLE", "issues" -> ujson.Arr(), "rows" -> ujson.Arr())),
      "forms"  -> ujson.Arr(ujson.Obj(
        "formId"    -> 0,
        "name"      -> "MOV",
        "alternate" -> false,
        "opcodes"   -> ujson.Arr(ujson.Obj("name" -> "MOV", "value" -> 1)),
        "fields" -> ujson.Arr(ujson.Obj("name" -> "SrcA", "positions" -> ujson.Arr(1, 2), "expression" -> expression)),
        "issues" -> ujson.Arr(),
        "syntax" -> ujson.Arr(ujson.Obj(
          "$type"     -> "Field",
          "kind"      -> "Register",
          "binding"   -> "SrcA",
          "arguments" -> ujson.Null,
          "default"   -> ujson.Null
        )),
        "conditions" -> ujson.Arr(ujson.Obj(
          "category"   -> "ERROR",
          "message"    -> "ok",
          "expression" -> ujson.Obj("$type" -> "StringValue", "value" -> "literal")
        ))
      ))
    )
    val decoders = Vector("a" * 64 -> database("SM89"), "b" * 64 -> database("SM90"))
    val encoded  = GenerateSASSISA.renderDecoderCatalogue(decoders)
    assertEquals(ujson.write(GenerateSASSISA.renderDecoderCatalogue(decoders)), ujson.write(encoded))
    assertEquals(encoded("format").num.toInt, 4)
    assert(!encoded.obj.contains("nodes"))
    assertEquals(encoded("expressions").arr.size, 1)
    assertEquals(encoded("syntaxNodes").arr.size, 1)
    assertEquals(encoded("formats").arr.size, 1)
    assertEquals(encoded("conditionExpressions").arr.size, 1)
    assertEquals(encoded("conditions").arr.size, 1)
    assertEquals(encoded("assignments").arr.size, 1)
    assertEquals(encoded("forms").arr.size, 1)
    assertEquals(encoded("vocabularies").arr.size, 2)
    val vocabularyRows = encoded("vocabularies").arr
    assertEquals(vocabularyRows(0)("default"), ujson.Arr(0))
    assertEquals(vocabularyRows(1)("default"), ujson.Arr())
    assertEquals(encoded("lookups").arr.size, 1)
    assertEquals(encoded("databases").arr.size, 2)
  }

  test("generated opcode cases disambiguate Scala identifiers deterministically") {
    val source = SASSInstructionRenderer.render(Vector("F.ADD", "F_ADD", "MOV").map(opcode =>
      SASSInstructionModel.Shape(opcode, Vector.empty, Vector.empty)
    ))("Instruction.scala")
    assert(source.contains("case MovNoOperands("))
    assertEquals(source.linesIterator.count(_.contains("case FAdd_")), 2)
  }

  test("binary generated files retain timestamps when content has not changed") {
    generator.inDirectory { directory =>
      val path = directory.resolve("table.json.gz")
      assert(GeneratedFiles.writeBytes(path, Array[Byte](1, 2, 3)))
      val before = Files.getLastModifiedTime(path)
      assert(!GeneratedFiles.writeBytes(path, Array[Byte](1, 2, 3)))
      assertEquals(Files.getLastModifiedTime(path), before)
      assert(GeneratedFiles.writeBytes(path, Array[Byte](1, 2, 4)))
    }
  }
}

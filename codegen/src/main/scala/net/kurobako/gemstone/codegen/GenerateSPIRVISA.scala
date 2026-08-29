package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import ujson.Value

object GenerateSPIRVISA {
  private final case class Operand(kind: String, name: Option[String], quantifier: Option[String])
  private final case class Enumerant(value: Long, parameters: Vector[String])
  private final case class OperandKind(kind: String, category: String, enumerants: Vector[Enumerant])
  private final case class Entry(
      opcode: Int,
      name: String,
      instructionClass: String,
      version: Option[String],
      capabilities: Vector[String],
      operands: Vector[Operand]
  )
  private final case class Grammar(
      major: Int,
      minor: Int,
      revision: Int,
      entries: Vector[Entry],
      kinds: Vector[OperandKind]
  )
  private final case class Field(name: String, operand: Operand)

  def main(arguments: Array[String]): Unit = arguments.toVector match {
    case Vector("instructions", grammarPath, outputPath, packageVersion) =>
      val grammar = readGrammar(Paths.get(grammarPath))
      val output  = Paths.get(outputPath)
      write(output, renderInstructions(grammar, packageVersion))
      println(s"generated ${grammar.entries.size} typed SPIR-V core instructions")
    case Vector("tests", grammarPath, tests, output, packageVersion) =>
      val grammar  = readGrammar(Paths.get(grammarPath))
      val testRoot = Paths.get(tests)
      val upstream = Vector("binary_parse_test.cpp", "binary_endianness_test.cpp", "val/val_opencl_test.cpp")
        .map(name => name -> discoveredTests(testRoot.resolve(name)))
      write(Paths.get(output), renderTests(grammar.entries.size, upstream, packageVersion))
      println(
        s"generated SPIR-V reference suite for ${grammar.entries.size} opcodes and ${upstream.map(_._2.size).sum} upstream tests"
      )
    case _ =>
      throw IllegalArgumentException(
        "usage: GenerateSPIRVISA instructions <grammar> <output> <package> | tests <grammar> <tests> <output> <package>"
      )
  }

  private def readGrammar(path: Path): Grammar = {
    val root = ujson.read(Files.readString(path, StandardCharsets.UTF_8)) match {
      case value: ujson.Obj => value
      case _                => throw IllegalArgumentException("SPIR-V grammar root is not an object")
    }
    val entries = arrayField(root, "instructions").map {
      case obj: ujson.Obj =>
        Entry(
          opcode = numberField(obj, "opcode"),
          name = stringField(obj, "opname"),
          instructionClass = stringField(obj, "class"),
          version = optionalString(obj, "version"),
          capabilities = optionalStringArray(obj, "capabilities"),
          operands = optionalObjectArray(obj, "operands").map { operand =>
            Operand(
              stringField(operand, "kind"),
              optionalString(operand, "name"),
              optionalString(operand, "quantifier")
            )
          }
        )
      case _ => throw IllegalArgumentException("instruction entry is not an object")
    }.sortBy(_.opcode)
    val kinds = arrayField(root, "operand_kinds").map {
      case obj: ujson.Obj =>
        OperandKind(
          kind = stringField(obj, "kind"),
          category = stringField(obj, "category"),
          enumerants = optionalObjectArray(obj, "enumerants").map { enumerant =>
            Enumerant(
              parseUnsigned(valueField(enumerant, "value")),
              optionalObjectArray(enumerant, "parameters").map(parameter => stringField(parameter, "kind"))
            )
          }.groupBy(_.value).values.map(_.head).toVector.sortBy(_.value)
        )
      case _ => throw IllegalArgumentException("operand kind is not an object")
    }
    Grammar(
      major = numberField(root, "major_version"),
      minor = numberField(root, "minor_version"),
      revision = numberField(root, "revision"),
      entries = entries,
      kinds = kinds
    )
  }

  private def renderInstructions(grammar: Grammar, packageVersion: String): String = {
    val entriesWithFields = grammar.entries.map(entry => entry -> fields(entry))
    val definitions       = entriesWithFields.map { case (entry, entryFields) => renderDefinition(entry, entryFields) }
    val cases             = entriesWithFields.map { case (entry, entryFields) => renderCase(entry, entryFields) }
    val kindValues        =
      grammar.kinds.filter(kind => kind.category == "ValueEnum" || kind.category == "BitEnum").map(renderKind)
    val kindChunks = kindValues.grouped(8).zipWithIndex.map { case (values, index) =>
      s"  private def operandKinds$index: Col[OperandKindInfo] = Col(\n${values.mkString(",\n")}\n  )"
    }.toVector
    val kindExpression = kindChunks.indices.map(index => s"operandKinds$index").mkString("Col(", ", ", ").flatten")
    s"""|// Generated from the pinned Khronos SPIR-V core grammar. Do not edit by hand.
        |package net.kurobako.gemstone.isa.spirv.$packageVersion
        |
        |import net.kurobako.gemstone.Col
        |import net.kurobako.gemstone.Col.*
        |
        |type Decoder = RawInstruction => Either[InstructionDecodeError, Instruction]
        |
        |enum InstructionDef(
        |    val opcode: Predef.String,
        |    val opcodeNumber: Int,
        |    decode: InstructionDef => Decoder,
        |    val meta: Metadata
        |) {
        |${definitions.mkString("\n")}
        |
        |  final lazy val decoder: Decoder = decode(this)
        |}
        |
        |enum Instruction(val definition: InstructionDef, operandValues: Operand*) {
        |${cases.mkString("\n")}
        |
        |  final val operands: Col[Operand] = operandValues.toCol
        |  final def opcode: Predef.String = definition.opcode
        |  final def opcodeNumber: Int = definition.opcodeNumber
        |  final def decoder: Decoder = definition.decoder
        |  final def meta: Metadata = definition.meta
        |}
        |
        |object Instruction {
        |  val version: SPIRVVersion = SPIRVVersion(${grammar.major}, ${grammar.minor})
        |  val grammarRevision: Int = ${grammar.revision}
        |  val definitions: Col[InstructionDef] = InstructionDef.values.toCol
        |
        |${kindChunks.mkString("\n\n")}
        |
        |  private val operandKinds: Map[Predef.String, OperandKindInfo] =
        |    $kindExpression.iterator.map(value => value.kind -> value).toMap
        |
        |  private val byNumber: Map[Int, Col[InstructionDef]] =
        |    definitions.groupMap(_.opcodeNumber)(identity)
        |  private val byOpcode: Map[Predef.String, Col[InstructionDef]] =
        |    definitions.groupMap(_.opcode)(identity)
        |
        |  def definitionsFor(opcodeNumber: Int): Col[InstructionDef] =
        |    byNumber.getOrElse(opcodeNumber, Col.empty[InstructionDef])
        |  def definitionsFor(opcode: Predef.String): Col[InstructionDef] =
        |    byOpcode.getOrElse(opcode, Col.empty[InstructionDef])
        |
        |  def decode(raw: RawInstruction): Either[InstructionDecodeError, Instruction] = {
        |    val candidates = definitionsFor(raw.opcodeNumber)
        |    candidates match {
        |      case Col() => Left(InstructionDecodeError.UnknownOpcode(raw.opcodeNumber))
        |      case Col(definition) => definition.decoder(raw)
        |      case definitions =>
        |        val matches = definitions.flatMap(definition =>
        |          definition.decoder(raw).toOption.map(instruction => definition -> instruction)
        |        )
        |        matches match {
        |          case Col((_, instruction)) => Right(instruction)
        |          case Col() =>
        |            Left(InstructionDecodeError.NoMatchingDefinition(raw.opcodeNumber, definitions.map(_.opcode)))
        |          case values =>
        |            Left(InstructionDecodeError.AmbiguousDefinition(raw.opcodeNumber, values.map(_._1.opcode)))
        |        }
        |    }
        |  }
        |
        |  def encode(instruction: Instruction): RawInstruction =
        |    RawInstruction(instruction.definition.opcodeNumber, instruction.operands.flatMap(_.words))
        |
        |  def minimumOperandWords(definition: InstructionDef): Col[Word] =
        |    OperandDecoder.minimumWords(definition.meta.operands, operandKinds)
        |
        |  private[v1_6] def decodeDefinition(
        |      definition: InstructionDef,
        |      raw: RawInstruction,
        |      construct: DecodedOperands => Instruction
        |  ): Either[InstructionDecodeError, Instruction] =
        |    if (raw.opcodeNumber != definition.opcodeNumber)
        |      Left(InstructionDecodeError.DecoderOpcodeMismatch(definition.opcodeNumber, raw.opcodeNumber))
        |    else
        |      OperandDecoder
        |        .decode(definition.opcode, raw.operandWords, definition.meta.operands, operandKinds)
        |        .map(construct)
        |}
        |""".stripMargin
  }

  private def renderDefinition(entry: Entry, fields: Vector[Field]): String = {
    val caseName  = normalizeCase(entry.name.stripPrefix("Op"))
    val parameter = if (fields.isEmpty) "_" else "values"
    val decoder   =
      s"definition => raw => Instruction.decodeDefinition(definition, raw, $parameter => ${construct(entry, fields)})"
    s"  case $caseName extends InstructionDef(\"${escape(entry.name)}\", ${entry.opcode}, $decoder, ${renderMetadata(entry, fields)})"
  }

  private def renderCase(entry: Entry, fields: Vector[Field]): String = {
    val caseName = normalizeCase(entry.name.stripPrefix("Op"))
    if (fields.isEmpty) s"  case $caseName() extends Instruction(InstructionDef.$caseName)"
    else {
      val parameters = fields.map { field =>
        val fieldType = field.operand.quantifier match {
          case Some("?") => "Option[Operand]"
          case Some("*") => "Col[Operand]"
          case _         => "Operand"
        }
        s"${field.name}: $fieldType"
      }.mkString(", ")
      if (fields.forall(_.operand.quantifier.isEmpty)) {
        val operands = fields.map(_.name).mkString(", ")
        s"  case $caseName($parameters) extends Instruction(InstructionDef.$caseName, $operands)"
      } else {
        def flush(parts: Vector[String], required: Vector[String]): Vector[String] =
          if (required.isEmpty) parts else parts :+ required.mkString("Col(", ", ", ")")
        val (parts, required) = fields.foldLeft((Vector.empty[String], Vector.empty[String])) {
          case ((parts, required), field) =>
            field.operand.quantifier match {
              case Some("?") =>
                (flush(parts, required) :+ s"${field.name}.toCol", Vector.empty)
              case Some("*") =>
                (flush(parts, required) :+ field.name, Vector.empty)
              case _ => (parts, required :+ field.name)
            }
        }
        val operands = flush(parts, required).mkString(" ++ ")
        s"  case $caseName($parameters) extends Instruction(InstructionDef.$caseName, ($operands)*)"
      }
    }
  }

  private def renderMetadata(entry: Entry, fields: Vector[Field]): String = {
    val since        = entry.version.map(value => s"Some(\"${escape(value)}\")").getOrElse("None")
    val capabilities = entry.capabilities.map(value => s"\"${escape(value)}\"").mkString("Col(", ", ", ")")
    val operands     = fields.map { field =>
      val quantifier = field.operand.quantifier match {
        case Some("?") => "OperandQuantifier.Optional"
        case Some("*") => "OperandQuantifier.Repeated"
        case _         => "OperandQuantifier.One"
      }
      s"OperandShape(\"${escape(field.operand.kind)}\", \"${escape(field.name)}\", $quantifier)"
    }.mkString("Col(", ", ", ")")
    s"Metadata(\"${escape(entry.instructionClass)}\", $since, $capabilities, $operands)"
  }

  private def renderKind(kind: OperandKind): String = {
    val enumerants = kind.enumerants.map { enumerant =>
      val parameters = enumerant.parameters.map(value => s"\"${escape(value)}\"").mkString("Col(", ", ", ")")
      s"OperandEnumerant(${enumerant.value}L, $parameters)"
    }.mkString("Col(", ", ", ")")
    s"    OperandKindInfo(\"${escape(kind.kind)}\", \"${escape(kind.category)}\", $enumerants)"
  }

  private def construct(entry: Entry, fields: Vector[Field]): String = {
    val caseName  = normalizeCase(entry.name.stripPrefix("Op"))
    val arguments = fields.zipWithIndex.map { case (field, index) =>
      field.operand.quantifier match {
        case Some("?") => s"values.optional($index)"
        case Some("*") => s"values.repeated($index)"
        case _         => s"values.required($index)"
      }
    }
    if (arguments.isEmpty) s"Instruction.$caseName()"
    else s"Instruction.$caseName(${arguments.mkString(", ")})"
  }

  private def fields(entry: Entry): Vector[Field] = {
    val generic = entry.operands.zipWithIndex.collect {
      case (operand, index) if operand.name.exists(_.toLowerCase(java.util.Locale.ROOT).startsWith("operand")) => index
    }
    val bases = entry.operands.zipWithIndex.map { case (operand, index) =>
      val name =
        if (operand.kind == "IdResultType") "resultType"
        else if (operand.kind == "IdResult") "dest"
        else if (operand.kind == "MemoryAccess" || operand.kind == "ImageOperands") "mods"
        else operand.name.map(_.toLowerCase(java.util.Locale.ROOT)) match {
          case Some("condition")                          => "pred"
          case Some("predicate")                          => "pred"
          case Some("memory access")                      => "mods"
          case Some("image operands")                     => "mods"
          case Some("object")                             => "src"
          case Some(value) if value.startsWith("operand") =>
            if (operand.quantifier.contains("*")) "srcs"
            else if (generic.size == 1) "src"
            else s"src${letter(generic.indexOf(index))}"
          case _ => normalizeField(operand.name.getOrElse(operand.kind))
        }
      name
    }
    val counts = bases.groupMapReduce(identity)(_ => 1)(_ + _)
    entry.operands.zip(bases).foldLeft((Map.empty[String, Int], Vector.empty[Field])) {
      case ((seen, fields), (operand, base)) =>
        val occurrence = seen.getOrElse(base, 0)
        val name       = if (counts(base) == 1) base else s"$base${letter(occurrence)}"
        val next       = (seen.updated(base, occurrence + 1), fields :+ Field(name, operand))
        next
    }._2
  }

  private def normalizeCase(value: String): String = {
    val words  = value.split("[^A-Za-z0-9]+", -1).toVector.filter(_.nonEmpty)
    val result = words.map(word => s"${word.head.toUpper}${word.tail}").mkString
    if (result.headOption.exists(_.isDigit)) s"Instruction$result" else result
  }

  private def normalizeField(value: String): String = {
    val words       = value.replace("~", "").split("[^A-Za-z0-9]+", -1).toVector.filter(_.nonEmpty)
    val abbreviated = words.map {
      case word if word.equalsIgnoreCase("source")       => "src"
      case word if word.equalsIgnoreCase("sources")      => "srcs"
      case word if word.equalsIgnoreCase("instruction")  => "inst"
      case word if word.equalsIgnoreCase("instructions") => "insts"
      case word                                          => word
    }
    val raw = abbreviated.headOption.map(word => s"${word.head.toLower}${word.tail}").getOrElse("operand") +
      abbreviated.drop(1).map(word => s"${word.head.toUpper}${word.tail}").mkString
    val prefixed = if (raw.headOption.exists(_.isDigit)) s"operand$raw" else raw
    prefixed match {
      case "type"      => "dataType"
      case "match"     => "matchValue"
      case "class"     => "classValue"
      case "object"    => "src"
      case "val"       => "value"
      case "var"       => "variable"
      case "macro"     => "macroValue"
      case "enum"      => "enumValue"
      case "given"     => "givenValue"
      case "extension" => "extensionValue"
      case "opcode"    => "instructionOpcode"
      case "operands"  => "operandList"
      case other       => other
    }
  }

  private def letter(index: Int): String = ('A'.toInt + index).toChar.toString

  private def discoveredTests(path: Path): Vector[String] = {
    val pattern = raw"(?:TEST|TEST_F|TEST_P)\s*\(\s*[^,]+,\s*([A-Za-z0-9_]+)\s*\)".r
    pattern.findAllMatchIn(Files.readString(path, StandardCharsets.UTF_8)).map(_.group(1)).toVector.distinct.sorted
  }

  private def renderTests(
      opcodeCount: Int,
      upstream: Vector[(String, Vector[String])],
      packageVersion: String
  ): String = {
    val sourceRows = upstream.map { case (file, names) => s"    \"${escape(file)}\" -> ${names.size}" }.mkString(",\n")
    s"""|// Generated from pinned Khronos grammar and SPIRV-Tools tests. Do not edit by hand.
        |package net.kurobako.gemstone.isa.spirv.$packageVersion
        |
        |import net.kurobako.gemstone.Col
        |
        |final class ReferenceSuite extends munit.FunSuite {
        |  private val upstreamTestCounts: Map[String, Int] = Map(
        |$sourceRows
        |  )
        |
        |  private def same(left: RawInstruction, right: RawInstruction): Boolean =
        |    left.opcodeNumber == right.opcodeNumber && left.operandWords.sameElements(right.operandWords)
        |
        |  private def same(left: RawModule, right: RawModule): Boolean =
        |    left.header == right.header && left.endianness == right.endianness &&
        |      left.instructions.size == right.instructions.size &&
        |      left.instructions.zip(right.instructions).forall { case (leftInstruction, rightInstruction) =>
        |        same(leftInstruction, rightInstruction)
        |      }
        |
        |  test("every grammar opcode decodes to a typed case and round-trips little endian") {
        |    assertEquals(Instruction.definitions.size, $opcodeCount)
        |    Instruction.definitions.foreach { definition =>
        |      val rawInstruction = RawInstruction(definition.opcodeNumber, Instruction.minimumOperandWords(definition))
        |      val instruction = Instruction.decode(rawInstruction).fold(error => fail(s"$${definition.opcode}: $${error.message}"), identity)
        |      assertEquals(instruction.definition, definition, definition.opcode)
        |      assert(same(Instruction.encode(instruction), rawInstruction), definition.opcode)
        |      val rawModule = RawModule(
        |        Header(ISA.version, Word(0), Word(1), Word(0)),
        |        Col(rawInstruction),
        |        Endianness.Little
        |      )
        |      val bytes = Binary.encode(rawModule).fold(error => fail(error.message), identity)
        |      assert(Binary.decode(bytes).exists(same(_, rawModule)), definition.opcode)
        |    }
        |  }
        |
        |  test("SPIRV-Tools endian oracle is represented") {
        |    assert(upstreamTestCounts("binary_endianness_test.cpp") > 0)
        |    val module = RawModule(Header(ISA.version, Word(7), Word(2), Word(0)), Col.empty, Endianness.Big)
        |    val bytes = Binary.encode(module).fold(error => fail(error.message), identity)
        |    assert(Binary.decode(bytes).exists(same(_, module)))
        |  }
        |
        |  test("SPIRV-Tools parser negative classes are fail-closed") {
        |    assert(upstreamTestCounts("binary_parse_test.cpp") >= 20)
        |    val malformed = Col(
        |      Col[Byte](1, 2, 3),
        |      words(0x07230203, 0x00010600, 0, 1),
        |      words(0xdeadbeef, 0x00010600, 0, 1, 0),
        |      words(0x07230203, 0x00010600, 0, 1, 0, 0),
        |      words(0x07230203, 0x00010600, 0, 1, 0, (3 << 16) | 17, 6)
        |    )
        |    malformed.foreach(bytes => assert(Binary.decode(bytes).isLeft))
        |  }
        |
        |  test("unknown binary opcodes round-trip raw but fail typed decode") {
        |    val raw = RawInstruction(65535, Col(Word(7)))
        |    assertEquals(Instruction.decode(raw), Left(InstructionDecodeError.UnknownOpcode(65535)))
        |    val module = RawModule(Header(ISA.version, Word(0), Word(1), Word(0)), Col(raw), Endianness.Little)
        |    val bytes = Binary.encode(module).fold(error => fail(error.message), identity)
        |    assert(Binary.decode(bytes).exists(same(_, module)))
        |  }
        |
        |  test("known opcodes with malformed grammar operands fail typed decode") {
        |    Instruction.decode(RawInstruction(17, Col.empty)) match {
        |      case Left(InstructionDecodeError.InvalidOperandCount("OpCapability", _)) => ()
        |      case other => fail(s"expected missing-operand failure, found $$other")
        |    }
        |    Instruction.decode(RawInstruction(17, Col(Word(-1)))) match {
        |      case Left(InstructionDecodeError.InvalidEnumerant("Capability", 0xffffffffL)) => ()
        |      case other => fail(s"expected invalid-enumerant failure, found $$other")
        |    }
        |    Instruction.decode(RawInstruction(5, Col(Word(1), Word(-1)))) match {
        |      case Left(InstructionDecodeError.UnterminatedString("OpName")) => ()
        |      case other => fail(s"expected unterminated-string failure, found $$other")
        |    }
        |  }
        |
        |  test("SPIRV-Tools OpenCL validation corpus is tracked") {
        |    assert(upstreamTestCounts("val/val_opencl_test.cpp") >= 10)
        |  }
        |
        |  private def words(values: Int*): Col[Byte] = Col.from(values).flatMap { value =>
        |    Col(value.toByte, (value >>> 8).toByte, (value >>> 16).toByte, (value >>> 24).toByte)
        |  }
        |}
        |""".stripMargin
  }

  private def field(obj: ujson.Obj, name: String): Value =
    obj.value.getOrElse(name, throw IllegalArgumentException(s"missing JSON field $name"))
  private def valueField(obj: ujson.Obj, name: String): Value         = field(obj, name)
  private def arrayField(obj: ujson.Obj, name: String): Vector[Value] = field(obj, name) match {
    case ujson.Arr(values) => values.toVector
    case _                 => throw IllegalArgumentException(s"JSON field $name is not an array")
  }
  private def optionalObjectArray(obj: ujson.Obj, name: String): Vector[ujson.Obj] = obj.value.get(name).toVector
    .flatMap {
      case ujson.Arr(values) => values.toVector.map {
          case value: ujson.Obj => value
          case _                => throw IllegalArgumentException(s"JSON field $name contains a non-object")
        }
      case _ => throw IllegalArgumentException(s"JSON field $name is not an array")
    }
  private def optionalStringArray(obj: ujson.Obj, name: String): Vector[String] = obj.value.get(name).toVector
    .flatMap {
      case ujson.Arr(values) => values.toVector.map {
          case ujson.Str(value) => value
          case _                => throw IllegalArgumentException(s"JSON field $name contains a non-string")
        }
      case _ => throw IllegalArgumentException(s"JSON field $name is not an array")
    }
  private def stringField(obj: ujson.Obj, name: String): String = field(obj, name) match {
    case ujson.Str(value) => value
    case _                => throw IllegalArgumentException(s"JSON field $name is not a string")
  }
  private def optionalString(obj: ujson.Obj, name: String): Option[String] = obj.value.get(name).map {
    case ujson.Str(value) => value
    case _                => throw IllegalArgumentException(s"JSON field $name is not a string")
  }
  private def numberField(obj: ujson.Obj, name: String): Int = field(obj, name) match {
    case ujson.Num(value) if value.isValidInt => value.toInt
    case _                                    => throw IllegalArgumentException(s"JSON field $name is not an int")
  }
  private def parseUnsigned(value: Value): Long = value match {
    case ujson.Str(text) if text.startsWith("0x") => java.lang.Long.parseUnsignedLong(text.drop(2), 16)
    case ujson.Str(text)                          => java.lang.Long.parseLong(text)
    case ujson.Num(number) if number.isWhole && number >= Long.MinValue && number <= Long.MaxValue => number.toLong
    case _ => throw IllegalArgumentException("enumerant value is not an integer")
  }
  private def escape(value: String): String = value.flatMap {
    case '\\' => "\\\\"
    case '"'  => "\\\""
    case '\n' => "\\n"
    case '\r' => "\\r"
    case '\t' => "\\t"
    case char => char.toString
  }

  private def write(path: Path, content: String): Unit = {
    val formatted = GeneratedScalaFormatter(path, content)
    val _         = Files.createDirectories(path.getParent)
    val _         = GeneratedFiles.write(path, formatted)
  }

}

package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import scala.collection.mutable

object GenerateSpirvIsa {
  private enum Json {
    case Object(fields: Map[Predef.String, Json])
    case Array(values: Vector[Json])
    case String(value: Predef.String)
    case Number(value: BigDecimal)
    case Bool(value: Boolean)
    case Null
  }

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
      throw new IllegalArgumentException(
        "usage: GenerateSpirvIsa instructions <grammar> <output> <package> | tests <grammar> <tests> <output> <package>"
      )
  }

  private def readGrammar(path: Path): Grammar = {
    val root = new JsonParser(Files.readString(path, StandardCharsets.UTF_8)).parse() match {
      case value: Json.Object => value
      case _                  => throw new IllegalArgumentException("SPIR-V grammar root is not an object")
    }
    val entries = arrayField(root, "instructions").map {
      case obj: Json.Object =>
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
      case _ => throw new IllegalArgumentException("instruction entry is not an object")
    }.sortBy(_.opcode)
    val kinds = arrayField(root, "operand_kinds").map {
      case obj: Json.Object =>
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
      case _ => throw new IllegalArgumentException("operand kind is not an object")
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
    val decoderValues     = entriesWithFields.zipWithIndex.map { case ((entry, entryFields), index) =>
      renderDecoder(index, entry, entryFields)
    }
    val decoderChunks = decoderValues.grouped(40).zipWithIndex.map { case (values, index) =>
      s"  private def decoders$index: Vector[Decoder] = Vector(\n${values.mkString(",\n")}\n  )"
    }.toVector
    val decoderExpression = decoderChunks.indices.map(index => s"decoders$index").mkString("Vector(", ", ", ").flatten")
    val kindValues        =
      grammar.kinds.filter(kind => kind.category == "ValueEnum" || kind.category == "BitEnum").map(renderKind)
    val kindChunks = kindValues.grouped(8).zipWithIndex.map { case (values, index) =>
      s"  private def operandKinds$index: Vector[OperandKindInfo] = Vector(\n${values.mkString(",\n")}\n  )"
    }.toVector
    val kindExpression = kindChunks.indices.map(index => s"operandKinds$index").mkString("Vector(", ", ", ").flatten")
    s"""|// Generated from the pinned Khronos SPIR-V core grammar. Do not edit by hand.
        |package net.kurobako.gemstone.isa.spirv.$packageVersion
        |
        |type Decoder = RawInstruction => Either[InstructionDecodeError, Instruction]
        |
        |enum InstructionDef(
        |    val opcode: Predef.String,
        |    val opcodeNumber: Int,
        |    val meta: Metadata
        |) {
        |${definitions.mkString("\n")}
        |
        |  final lazy val decoder: Decoder = Instruction.decoderFor(ordinal)
        |}
        |
        |enum Instruction(val definition: InstructionDef, operandValues: Operand*) {
        |${cases.mkString("\n")}
        |
        |  final val operands: Vector[Operand] = operandValues.toVector
        |  final def opcode: Predef.String = definition.opcode
        |  final def opcodeNumber: Int = definition.opcodeNumber
        |  final def decoder: Decoder = definition.decoder
        |  final def meta: Metadata = definition.meta
        |}
        |
        |val Version: SpirvVersion = SpirvVersion(${grammar.major}, ${grammar.minor})
        |val GrammarRevision: Int = ${grammar.revision}
        |val Instructions: Vector[InstructionDef] = InstructionDef.values.toVector
        |
        |object Instruction {
        |${decoderChunks.mkString("\n\n")}
        |
        |${kindChunks.mkString("\n\n")}
        |
        |  private val decoders: Vector[Decoder] = $decoderExpression
        |
        |  private val operandKinds: Map[Predef.String, OperandKindInfo] =
        |    $kindExpression.iterator.map(value => value.kind -> value).toMap
        |
        |  private val byNumber: Map[Int, Vector[InstructionDef]] =
        |    Instructions.groupMap(_.opcodeNumber)(identity)
        |  private val byOpcode: Map[Predef.String, Vector[InstructionDef]] =
        |    Instructions.groupMap(_.opcode)(identity)
        |
        |  def definitions(opcodeNumber: Int): Vector[InstructionDef] = byNumber.getOrElse(opcodeNumber, Vector.empty)
        |  def definitions(opcode: Predef.String): Vector[InstructionDef] = byOpcode.getOrElse(opcode, Vector.empty)
        |
        |  def decode(raw: RawInstruction): Either[InstructionDecodeError, Instruction] = {
        |    val candidates = definitions(raw.opcodeNumber)
        |    candidates match {
        |      case Vector() => Left(InstructionDecodeError.UnknownOpcode(raw.opcodeNumber))
        |      case Vector(definition) => definition.decoder(raw)
        |      case definitions =>
        |        val matches = definitions.flatMap(definition =>
        |          definition.decoder(raw).toOption.map(instruction => definition -> instruction)
        |        )
        |        matches match {
        |          case Vector((_, instruction)) => Right(instruction)
        |          case Vector() =>
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
        |  def minimumOperandWords(definition: InstructionDef): Vector[Word] =
        |    OperandDecoder.minimumWords(definition.meta.operands, operandKinds)
        |
        |  private[v1_6] def decoderFor(ordinal: Int): Decoder = decoders(ordinal)
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
    val caseName = normalizeCase(entry.name.stripPrefix("Op"))
    s"  case $caseName extends InstructionDef(\"${escape(entry.name)}\", ${entry.opcode}, ${renderMetadata(entry, fields)})"
  }

  private def renderDecoder(index: Int, entry: Entry, fields: Vector[Field]): String = {
    val parameter = if (fields.isEmpty) "_" else "values"
    s"    raw => Instruction.decodeDefinition(InstructionDef.fromOrdinal($index), raw, $parameter => ${construct(entry, fields)})"
  }

  private def renderCase(entry: Entry, fields: Vector[Field]): String = {
    val caseName = normalizeCase(entry.name.stripPrefix("Op"))
    if (fields.isEmpty) s"  case $caseName extends Instruction(InstructionDef.$caseName)"
    else {
      val parameters = fields.map { field =>
        val fieldType = field.operand.quantifier match {
          case Some("?") => "Option[Operand]"
          case Some("*") => "Vector[Operand]"
          case _         => "Operand"
        }
        s"${field.name}: $fieldType"
      }.mkString(", ")
      if (fields.forall(_.operand.quantifier.isEmpty)) {
        val operands = fields.map(_.name).mkString(", ")
        s"  case $caseName($parameters) extends Instruction(InstructionDef.$caseName, $operands)"
      } else {
        val parts                 = Vector.newBuilder[String]
        val required              = mutable.ArrayBuffer.empty[String]
        def flushRequired(): Unit =
          if (required.nonEmpty) {
            parts += required.mkString("Vector(", ", ", ")")
            required.clear()
          }
        fields.foreach { field =>
          field.operand.quantifier match {
            case Some("?") =>
              flushRequired()
              parts += s"${field.name}.toVector"
            case Some("*") =>
              flushRequired()
              parts += field.name
            case _ => required += field.name
          }
        }
        flushRequired()
        val operands = parts.result().mkString(" ++ ")
        s"  case $caseName($parameters) extends Instruction(InstructionDef.$caseName, ($operands)*)"
      }
    }
  }

  private def renderMetadata(entry: Entry, fields: Vector[Field]): String = {
    val since        = entry.version.map(value => s"Some(\"${escape(value)}\")").getOrElse("None")
    val capabilities = entry.capabilities.map(value => s"\"${escape(value)}\"").mkString("Vector(", ", ", ")")
    val operands     = fields.map { field =>
      val quantifier = field.operand.quantifier match {
        case Some("?") => "OperandQuantifier.Optional"
        case Some("*") => "OperandQuantifier.Repeated"
        case _         => "OperandQuantifier.One"
      }
      s"OperandShape(\"${escape(field.operand.kind)}\", \"${escape(field.name)}\", $quantifier)"
    }.mkString("Vector(", ", ", ")")
    s"Metadata(\"${escape(entry.instructionClass)}\", $since, $capabilities, $operands)"
  }

  private def renderKind(kind: OperandKind): String = {
    val enumerants = kind.enumerants.map { enumerant =>
      val parameters = enumerant.parameters.map(value => s"\"${escape(value)}\"").mkString("Vector(", ", ", ")")
      s"OperandEnumerant(${enumerant.value}L, $parameters)"
    }.mkString("Vector(", ", ", ")")
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
    if (arguments.isEmpty) s"Instruction.$caseName"
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
    val seen   = mutable.HashMap.empty[String, Int].withDefaultValue(0)
    entry.operands.zip(bases).map { case (operand, base) =>
      val occurrence = seen(base)
      seen.update(base, occurrence + 1)
      val name = if (counts(base) == 1) base else s"$base${letter(occurrence)}"
      Field(name, operand)
    }
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
        |package net.kurobako.gemstone.isa.spirv.$packageVersion.generated
        |
        |import net.kurobako.gemstone.isa.spirv.$packageVersion.*
        |
        |final class ReferenceSuite extends munit.FunSuite {
        |  private val upstreamTestCounts: Map[String, Int] = Map(
        |$sourceRows
        |  )
        |
        |  test("every grammar opcode decodes to a typed case and round-trips little endian") {
        |    assertEquals(Instructions.size, $opcodeCount)
        |    Instructions.foreach { definition =>
        |      val rawInstruction = RawInstruction(definition.opcodeNumber, Instruction.minimumOperandWords(definition))
        |      val instruction = Instruction.decode(rawInstruction).fold(error => fail(s"$${definition.opcode}: $${error.message}"), identity)
        |      assertEquals(instruction.definition, definition, definition.opcode)
        |      assertEquals(Instruction.encode(instruction), rawInstruction, definition.opcode)
        |      val rawModule = RawModule(
        |        Header(Version, Word(0), Word(1), Word(0)),
        |        Vector(rawInstruction),
        |        Endianness.Little
        |      )
        |      val bytes = Binary.serialize(rawModule).fold(error => fail(error.message), identity)
        |      assertEquals(Binary.parse(bytes), Right(rawModule), definition.opcode)
        |    }
        |  }
        |
        |  test("SPIRV-Tools endian oracle is represented") {
        |    assert(upstreamTestCounts("binary_endianness_test.cpp") > 0)
        |    val module = RawModule(Header(Version, Word(7), Word(2), Word(0)), Vector.empty, Endianness.Big)
        |    val bytes = Binary.serialize(module).fold(error => fail(error.message), identity)
        |    assertEquals(Binary.parse(bytes), Right(module))
        |  }
        |
        |  test("SPIRV-Tools parser negative classes are fail-closed") {
        |    assert(upstreamTestCounts("binary_parse_test.cpp") >= 20)
        |    val malformed = Vector(
        |      IArray[Byte](1, 2, 3),
        |      words(0x07230203, 0x00010600, 0, 1),
        |      words(0xdeadbeef, 0x00010600, 0, 1, 0),
        |      words(0x07230203, 0x00010600, 0, 1, 0, 0),
        |      words(0x07230203, 0x00010600, 0, 1, 0, (3 << 16) | 17, 6)
        |    )
        |    malformed.foreach(bytes => assert(Binary.parse(bytes).isLeft))
        |  }
        |
        |  test("unknown binary opcodes round-trip raw but fail typed decode") {
        |    val raw = RawInstruction(65535, Vector(Word(7)))
        |    assertEquals(Instruction.decode(raw), Left(InstructionDecodeError.UnknownOpcode(65535)))
        |    val module = RawModule(Header(Version, Word(0), Word(1), Word(0)), Vector(raw), Endianness.Little)
        |    val bytes = Binary.serialize(module).fold(error => fail(error.message), identity)
        |    assertEquals(Binary.parse(bytes), Right(module))
        |  }
        |
        |  test("known opcodes with malformed grammar operands fail typed decode") {
        |    Instruction.decode(RawInstruction(17, Vector.empty)) match {
        |      case Left(InstructionDecodeError.InvalidOperandCount("OpCapability", _)) => ()
        |      case other => fail(s"expected missing-operand failure, found $$other")
        |    }
        |    Instruction.decode(RawInstruction(17, Vector(Word(-1)))) match {
        |      case Left(InstructionDecodeError.InvalidEnumerant("Capability", 0xffffffffL)) => ()
        |      case other => fail(s"expected invalid-enumerant failure, found $$other")
        |    }
        |    Instruction.decode(RawInstruction(5, Vector(Word(1), Word(-1)))) match {
        |      case Left(InstructionDecodeError.UnterminatedString("OpName")) => ()
        |      case other => fail(s"expected unterminated-string failure, found $$other")
        |    }
        |  }
        |
        |  test("SPIRV-Tools OpenCL validation corpus is tracked") {
        |    assert(upstreamTestCounts("val/val_opencl_test.cpp") >= 10)
        |  }
        |
        |  private def words(values: Int*): IArray[Byte] = {
        |    val bytes = new Array[Byte](values.size * 4)
        |    values.zipWithIndex.foreach { case (value, index) =>
        |      bytes(index * 4) = value.toByte
        |      bytes(index * 4 + 1) = (value >>> 8).toByte
        |      bytes(index * 4 + 2) = (value >>> 16).toByte
        |      bytes(index * 4 + 3) = (value >>> 24).toByte
        |    }
        |    IArray.unsafeFromArray(bytes)
        |  }
        |}
        |""".stripMargin
  }

  private def field(obj: Json.Object, name: String): Json =
    obj.fields.getOrElse(name, throw new IllegalArgumentException(s"missing JSON field $name"))
  private def valueField(obj: Json.Object, name: String): Json         = field(obj, name)
  private def arrayField(obj: Json.Object, name: String): Vector[Json] = field(obj, name) match {
    case Json.Array(values) => values
    case _                  => throw new IllegalArgumentException(s"JSON field $name is not an array")
  }
  private def optionalObjectArray(obj: Json.Object, name: String): Vector[Json.Object] = obj.fields.get(name).toVector
    .flatMap {
      case Json.Array(values) => values.map {
          case value: Json.Object => value
          case _                  => throw new IllegalArgumentException(s"JSON field $name contains a non-object")
        }
      case _ => throw new IllegalArgumentException(s"JSON field $name is not an array")
    }
  private def optionalStringArray(obj: Json.Object, name: String): Vector[String] = obj.fields.get(name).toVector
    .flatMap {
      case Json.Array(values) => values.map {
          case Json.String(value) => value
          case _                  => throw new IllegalArgumentException(s"JSON field $name contains a non-string")
        }
      case _ => throw new IllegalArgumentException(s"JSON field $name is not an array")
    }
  private def stringField(obj: Json.Object, name: String): String = field(obj, name) match {
    case Json.String(value) => value
    case _                  => throw new IllegalArgumentException(s"JSON field $name is not a string")
  }
  private def optionalString(obj: Json.Object, name: String): Option[String] = obj.fields.get(name).map {
    case Json.String(value) => value
    case _                  => throw new IllegalArgumentException(s"JSON field $name is not a string")
  }
  private def numberField(obj: Json.Object, name: String): Int = field(obj, name) match {
    case Json.Number(value) if value.isValidInt => value.toInt
    case _                                      => throw new IllegalArgumentException(s"JSON field $name is not an int")
  }
  private def parseUnsigned(value: Json): Long = value match {
    case Json.String(text) if text.startsWith("0x") => java.lang.Long.parseUnsignedLong(text.drop(2), 16)
    case Json.String(text)                          => java.lang.Long.parseLong(text)
    case Json.Number(number) if number.isValidLong  => number.toLong
    case _ => throw new IllegalArgumentException("enumerant value is not an integer")
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
    val _ = Files.createDirectories(path.getParent)
    val _ = Files.writeString(path, content, StandardCharsets.UTF_8)
  }

  private final class JsonParser(source: String) {
    private var index = 0
    def parse(): Json = {
      val value = parseValue()
      whitespace()
      require(index == source.length, s"trailing JSON at offset $index")
      value
    }
    private def parseValue(): Json = {
      whitespace()
      if (index >= source.length) fail("unexpected end of JSON")
      source.charAt(index) match {
        case '{'                                 => parseObject()
        case '['                                 => parseArray()
        case '"'                                 => Json.String(parseString())
        case 't'                                 => literal("true", Json.Bool(true))
        case 'f'                                 => literal("false", Json.Bool(false))
        case 'n'                                 => literal("null", Json.Null)
        case char if char == '-' || char.isDigit => parseNumber()
        case char                                => fail(s"unexpected JSON character '$char'")
      }
    }
    private def parseObject(): Json.Object = {
      index += 1
      whitespace()
      val fields = Map.newBuilder[String, Json]
      if (consume('}')) return Json.Object(fields.result())
      var done = false
      while (!done) {
        whitespace()
        if (index >= source.length || source.charAt(index) != '"') fail("expected object key")
        val name = parseString()
        whitespace()
        expect(':')
        fields += name -> parseValue()
        whitespace()
        if (consume('}')) done = true else expect(',')
      }
      Json.Object(fields.result())
    }
    private def parseArray(): Json.Array = {
      index += 1
      whitespace()
      val values = Vector.newBuilder[Json]
      if (consume(']')) return Json.Array(values.result())
      var done = false
      while (!done) {
        values += parseValue()
        whitespace()
        if (consume(']')) done = true else expect(',')
      }
      Json.Array(values.result())
    }
    private def parseString(): String = {
      expect('"')
      val result = new StringBuilder
      var done   = false
      while (!done && index < source.length) {
        val char = source.charAt(index)
        index += 1
        char match {
          case '"'  => done = true
          case '\\' =>
            if (index >= source.length) fail("unterminated escape")
            val escaped = source.charAt(index)
            index += 1
            escaped match {
              case '"'  => result += '"'
              case '\\' => result += '\\'
              case '/'  => result += '/'
              case 'b'  => result += '\b'
              case 'f'  => result += '\f'
              case 'n'  => result += '\n'
              case 'r'  => result += '\r'
              case 't'  => result += '\t'
              case 'u'  =>
                if (index + 4 > source.length) fail("short unicode escape")
                result += Integer.parseInt(source.substring(index, index + 4), 16).toChar
                index += 4
              case other => fail(s"invalid escape '$other'")
            }
          case other => result += other
        }
      }
      if (!done) fail("unterminated string")
      result.result()
    }
    private def parseNumber(): Json.Number = {
      val start = index
      while (index < source.length && "-+0123456789.eE".contains(source.charAt(index))) index += 1
      Json.Number(BigDecimal(source.substring(start, index)))
    }
    private def literal(value: String, result: Json): Json = {
      if (!source.startsWith(value, index)) fail(s"expected $value")
      index += value.length
      result
    }
    private def whitespace(): Unit = while (index < source.length && source.charAt(index).isWhitespace) index += 1
    private def consume(char: Char): Boolean =
      if (index < source.length && source.charAt(index) == char) { index += 1; true }
      else false
    private def expect(char: Char): Unit       = if (!consume(char)) fail(s"expected '$char'")
    private def fail(message: String): Nothing = throw new IllegalArgumentException(s"$message at JSON offset $index")
  }
}

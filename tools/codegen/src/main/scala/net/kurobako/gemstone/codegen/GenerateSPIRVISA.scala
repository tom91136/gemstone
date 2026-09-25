package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.NoNul
import ujson.Value

object GenerateSPIRVISA {
  private sealed trait Request
  private final case class Instructions(
      grammar: Path,
      extinst: Path,
      sourceRoot: Path,
      packageVersion: String
  ) extends Request
  private final case class ExtendedInstructionSet(name: String, objectName: String, instructions: Vector[(String, Int)])
  private final case class Tests(grammar: Path, tests: Path, sourceRoot: Path, packageVersion: String)
      extends Request
  private final case class Operand(
      kind: String :| (MinLength[1] & NoNul),
      name: Option[String :| (MinLength[1] & NoNul)],
      quantifier: Option[String :| (MinLength[1] & NoNul)]
  )
  private final case class Enumerant(
      value: Long,
      name: String :| (MinLength[1] & NoNul),
      parameters: Vector[String :| (MinLength[1] & NoNul)]
  )
  private final case class OperandKind(
      kind: String :| (MinLength[1] & NoNul),
      category: String :| (MinLength[1] & NoNul),
      enumerants: Vector[Enumerant]
  )
  private final case class Entry(
      opcode: Int :| Interval.Closed[0, 65535],
      name: String :| (MinLength[1] & NoNul),
      instructionClass: String :| (MinLength[1] & NoNul),
      version: Option[String :| (MinLength[1] & NoNul)],
      capabilities: Vector[String :| (MinLength[1] & NoNul)],
      operands: Vector[Operand]
  )
  private final case class Grammar(
      major: Int :| Interval.Closed[0, 255],
      minor: Int :| Interval.Closed[0, 255],
      revision: Int :| Positive0,
      entries: Vector[Entry] :| MinLength[1],
      kinds: Vector[OperandKind] :| MinLength[1]
  )
  private final case class Field(name: String, operand: Operand)
  private val PackageRoot = "net/kurobako/gemstone/isa/spirv"

  private val ExtendedInstructionGrammars = Vector(
    ("extinst.glsl.std.450.grammar.json", "GLSL.std.450", "GlslStd450"),
    ("extinst.opencl.std.100.grammar.json", "OpenCL.std", "OpenClStd")
  )

  private val command = Command("GenerateSPIRVISA", "Generate SPIR-V ISA sources")(
    Opts.subcommands(
      Command("instructions", "Generate instruction sources")((
        Opts.argument[String]("grammar").map(Path.of(_)),
        Opts.argument[String]("extinst-directory").map(Path.of(_)),
        Opts.argument[String]("generated-source-root").map(Path.of(_)),
        Opts.argument[String]("package-version")
      ).mapN(Instructions(_, _, _, _))),
      Command("tests", "Generate reference tests")((
        Opts.argument[String]("grammar").map(Path.of(_)),
        Opts.argument[String]("tests").map(Path.of(_)),
        Opts.argument[String]("generated-source-root").map(Path.of(_)),
        Opts.argument[String]("package-version")
      ).mapN(Tests(_, _, _, _)))
    )
  )

  def main(arguments: Array[String]): Unit = command.parse(arguments.toSeq).fold(
    help => throw IllegalArgumentException(help.toString),
    {
      case Instructions(grammarPath, extinstDirectory, sourceRoot, packageVersion) =>
        val version  = checkedPackageVersion(packageVersion)
        val grammar  = readGrammar(grammarPath)
        val extended = readExtendedInstructions(extinstDirectory)
        val base     = sourceRoot.resolve(PackageRoot).resolve(version.value)
        syncRoot(
          sourceRoot,
          Map(
            base.resolve("Instruction.scala")         -> renderInstructions(grammar, version),
            base.resolve("ExtendedInstruction.scala") -> renderExtendedInstructions(extended, version),
            base.resolve("BuiltIn.scala")             -> renderBuiltIns(grammar, version)
          )
        )
        Console.out.println(
          s"generated ${grammar.entries.size} typed SPIR-V core instructions and ${extended.map(_.instructions.size).sum} extended instructions"
        )
      case Tests(grammarPath, testRoot, sourceRoot, packageVersion) =>
        val version  = checkedPackageVersion(packageVersion)
        val grammar  = readGrammar(grammarPath)
        val upstream = Vector("binary_parse_test.cpp", "binary_endianness_test.cpp", "val/val_opencl_test.cpp")
          .map(name => name -> discoveredTests(testRoot.resolve(name)))
        val output = sourceRoot.resolve(PackageRoot).resolve(version.value).resolve("ReferenceSuite.scala")
        syncRoot(sourceRoot, Map(output -> renderTests(grammar.entries.size, upstream, version)))
        Console.out.println(
          s"generated SPIR-V reference suite for ${grammar.entries.size} opcodes and ${upstream.map(_._2.size).sum} upstream tests"
        )
    }
  )

  // OpSpecConstantOp's trailing operands depend on the embedded opcode and are
  // not listed in the core grammar, so append a repeated single-word tail. Every
  // permitted operand there (IdRef or LiteralInteger) is one word wide.
  private def grammarOverride(opname: String): Vector[Operand] =
    if (opname == "OpSpecConstantOp") Vector(Operand("LiteralInteger", Some("operands"), Some("*")))
    else Vector.empty

  private def readGrammar(path: Path): Grammar = {
    val root = ujson.read(Files.readString(path, StandardCharsets.UTF_8)) match {
      case value: ujson.Obj => value
      case _                => throw IllegalArgumentException("SPIR-V grammar root is not an object")
    }
    val entries = arrayField(root, "instructions").map {
      case obj: ujson.Obj =>
        Entry(
          opcode = opcodeField(obj, "opcode"),
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
          } ++ grammarOverride(stringField(obj, "opname"))
        )
      case _ => throw IllegalArgumentException("instruction entry is not an object")
    }.sortBy(_.opcode).refineEither[MinLength[1]].fold(error => throw IllegalArgumentException(error), value => value)
    val kinds = arrayField(root, "operand_kinds").map {
      case obj: ujson.Obj =>
        OperandKind(
          kind = stringField(obj, "kind"),
          category = stringField(obj, "category"),
          enumerants = optionalObjectArray(obj, "enumerants").map { enumerant =>
            Enumerant(
              parseUnsigned(valueField(enumerant, "value")),
              stringField(enumerant, "enumerant"),
              optionalObjectArray(enumerant, "parameters").map(parameter => stringField(parameter, "kind"))
            )
          }.groupBy(_.value).values.map(_.head).toVector.sortBy(_.value)
        )
      case _ => throw IllegalArgumentException("operand kind is not an object")
    }
    Grammar(
      major = versionField(root, "major_version"),
      minor = versionField(root, "minor_version"),
      revision = numberField(root, "revision").refineEither[Positive0]
        .fold(error => throw IllegalArgumentException(s"invalid grammar revision: $error"), value => value),
      entries = entries,
      kinds = kinds.refineEither[MinLength[1]].fold(error => throw IllegalArgumentException(error), value => value)
    )
  }

  private def renderInstructions(grammar: Grammar, packageVersion: ScalaPackageVersion): String = {
    val entriesWithFields = grammar.entries.map(entry => entry -> fields(entry))
    val definitions       = entriesWithFields.map { case (entry, entryFields) => renderDefinition(entry, entryFields) }
    val cases             = entriesWithFields.map { case (entry, entryFields) => renderCase(entry, entryFields) }
    val kindValues        =
      grammar.kinds.filter(kind => kind.category == "ValueEnum" || kind.category == "BitEnum").map(renderKind(_))
    val kindChunks = kindValues.grouped(8).zipWithIndex.map { case (values, index) =>
      s"  private def operandKinds$index: Vec[OperandKindInfo] = Vec(\n${values.mkString(",\n")}\n  )"
    }.toVector
    val kindExpression = kindChunks.indices.map(index => s"operandKinds$index").mkString("Vec(", ", ", ").flatten")
    GeneratedHeader.scala("from the pinned Khronos SPIR-V core grammar", assume = true) +
      s"""|package net.kurobako.gemstone.isa.spirv.${packageVersion.value}
        |
        |import net.kurobako.gemstone.NoNul
        |import net.kurobako.gemstone.Vec
        |import net.kurobako.gemstone.Vec.*
        |import net.kurobako.gemstone.isa.spirv.${packageVersion.value}.OperandDecoder.DecodedOperands
        |
        |import io.github.iltotore.iron.*
        |import io.github.iltotore.iron.constraint.all.*
        |
        |${GeneratedHeader.annotation()}enum Instruction(val definition: Instruction.Definition, operandValues: Operand*) {
        |${cases.mkString("\n")}
        |
        |  final val operands: Vec[Operand] = operandValues.toVec
        |  final def opcode: SPIRVOpcode = definition.opcode
        |  final def opcodeNumber: SPIRVOpcodeNumber = definition.opcodeNumber
        |  final def decoder: Instruction.Decoder = definition.decoder
        |  final def meta: Metadata = definition.meta
        |}
        |
        |${GeneratedHeader.annotation()}object Instruction {
        |  type Decoder = RawInstruction => Either[InstructionDecodeError, Instruction]
        |
        |  enum Definition(
        |      val opcode: SPIRVOpcode,
        |      val opcodeNumber: SPIRVOpcodeNumber,
        |      decode: Definition => Decoder,
        |      val meta: Metadata
        |  ) {
        |${definitions.mkString("\n")}
        |
        |    final lazy val decoder: Decoder = decode(this)
        |  }
        |
        |  val version: SPIRVVersion = SPIRVVersion(
        |    ${grammar.major}.assume[Interval.Closed[0, 255]],
        |    ${grammar.minor}.assume[Interval.Closed[0, 255]]
        |  )
        |  val grammarRevision: Int :| Positive0 = ${grammar.revision}.assume[Positive0]
        |  val definitions: Vec[Definition] = Definition.values.toVec
        |
        |${kindChunks.mkString("\n\n")}
        |
        |  private val operandKinds: Map[SPIRVOperandKind, OperandKindInfo] =
        |    $kindExpression.view.map(value => value.kind -> value).toMap
        |
        |  private val byNumber: Map[SPIRVOpcodeNumber, Vec[Definition]] =
        |    definitions.groupMap(_.opcodeNumber)(value => value)
        |  private val byOpcode: Map[SPIRVOpcode, Vec[Definition]] =
        |    definitions.groupMap(_.opcode)(value => value)
        |
        |  def definitionsFor(opcodeNumber: SPIRVOpcodeNumber): Vec[Definition] =
        |    byNumber.getOrElse(opcodeNumber, Vec.empty[Definition])
        |  def definitionsFor(opcode: SPIRVOpcode): Vec[Definition] =
        |    byOpcode.getOrElse(opcode, Vec.empty[Definition])
        |
        |  def decode(raw: RawInstruction): Either[InstructionDecodeError, Instruction] = {
        |    val candidates = definitionsFor(raw.opcodeNumber)
        |    candidates match {
        |      case Vec() => Left(InstructionDecodeError.UnknownOpcode(raw.opcodeNumber))
        |      case Vec(definition) => definition.decoder(raw)
        |      case definitions =>
        |        val matches = definitions.flatMap(definition =>
        |          definition.decoder(raw).toOption.map(instruction => definition -> instruction)
        |        )
        |        matches match {
        |          case Vec((_, instruction)) => Right(instruction)
        |          case Vec() =>
        |            Left(InstructionDecodeError.NoMatchingDefinition(
        |              raw.opcodeNumber,
        |              definitions.map(_.opcode).assume[MinLength[1]]
        |            ))
        |          case values =>
        |            Left(InstructionDecodeError.AmbiguousDefinition(
        |              raw.opcodeNumber,
        |              values.map(_._1.opcode).assume[MinLength[1]]
        |            ))
        |        }
        |    }
        |  }
        |
        |  def encode(instruction: Instruction): RawInstruction =
        |    RawInstruction(instruction.definition.opcodeNumber, instruction.operands.flatMap(_.words))
        |
        |  def minimumOperandWords(definition: Definition): Vec[Word] =
        |    OperandDecoder.minimumWords(definition.meta.operands, operandKinds)
        |
        |  private[${packageVersion.value}] def decodeDefinition(
        |      definition: Definition,
        |      raw: RawInstruction,
        |      construct: DecodedOperands => Instruction
        |  ): Either[InstructionDecodeError, Instruction] =
        |    if (raw.opcodeNumber != definition.opcodeNumber)
        |      Left(InstructionDecodeError.DecoderOpcodeMismatch(definition.opcodeNumber, raw.opcodeNumber))
        |    else
        |      OperandDecoder
        |        .decode(definition, raw.operandWords, definition.meta.operands, operandKinds)
        |        .map(construct)
        |}
        |""".stripMargin
  }

  private def renderDefinition(entry: Entry, fields: Vector[Field]): String = {
    val caseName  = normalizeCase(entry.name.stripPrefix("Op"))
    val parameter = if (fields.isEmpty) "_" else "values"
    val decoder   =
      s"definition => raw => Instruction.decodeDefinition(definition, raw, $parameter => ${construct(entry, fields)})"
    s"    case $caseName extends Definition(SPIRVOpcode.assume(\"${escape(entry.name)}\"), " +
      s"SPIRVOpcodeNumber.assume(${entry.opcode}), $decoder, ${renderMetadata(entry, fields)})"
  }

  private def renderCase(entry: Entry, fields: Vector[Field]): String = {
    val caseName = normalizeCase(entry.name.stripPrefix("Op"))
    if (fields.isEmpty) s"  case $caseName() extends Instruction(Instruction.Definition.$caseName)"
    else {
      val parameters = fields.map { field =>
        val fieldType = field.operand.quantifier match {
          case Some("?") => "Option[Operand]"
          case Some("*") => "Vec[Operand]"
          case _         => "Operand"
        }
        s"${field.name}: $fieldType"
      }.mkString(", ")
      if (fields.forall(_.operand.quantifier.isEmpty)) {
        val operands = fields.map(_.name).mkString(", ")
        s"  case $caseName($parameters) extends Instruction(Instruction.Definition.$caseName, $operands)"
      } else {
        def flush(parts: Vector[String], required: Vector[String]): Vector[String] =
          if (required.isEmpty) parts else parts :+ required.mkString("Vec(", ", ", ")")
        val (parts, required) = fields.foldLeft((Vector.empty[String], Vector.empty[String])) {
          case ((parts, required), field) =>
            field.operand.quantifier match {
              case Some("?") =>
                (flush(parts, required) :+ s"${field.name}.toVec", Vector.empty)
              case Some("*") =>
                (flush(parts, required) :+ field.name, Vector.empty)
              case _ => (parts, required :+ field.name)
            }
        }
        val operands = flush(parts, required).mkString(" ++ ")
        s"  case $caseName($parameters) extends Instruction(Instruction.Definition.$caseName, ($operands)*)"
      }
    }
  }

  private def renderMetadata(entry: Entry, fields: Vector[Field]): String = {
    val since        = entry.version.map(value => s"Some(${trustedText(value)})").getOrElse("None")
    val capabilities = entry.capabilities.map(trustedText(_)).mkString("Vec(", ", ", ")")
    val operands     = fields.map { field =>
      val quantifier = field.operand.quantifier match {
        case Some("?") => "OperandQuantifier.Optional"
        case Some("*") => "OperandQuantifier.Repeated"
        case _         => "OperandQuantifier.One"
      }
      s"OperandShape(SPIRVOperandKind.assume(\"${escape(field.operand.kind)}\"), ${trustedText(field.name)}, $quantifier)"
    }.mkString("Vec(", ", ", ")")
    s"Metadata(${trustedText(entry.instructionClass)}, $since, $capabilities, $operands)"
  }

  private def renderKind(kind: OperandKind): String = {
    val enumerants = kind.enumerants.map { enumerant =>
      val parameters = enumerant.parameters.map(value => s"SPIRVOperandKind.assume(\"${escape(value)}\")")
        .mkString("Vec(", ", ", ")")
      s"OperandEnumerant(${enumerant.value}L, $parameters)"
    }.mkString("Vec(", ", ", ")")
    s"    OperandKindInfo(SPIRVOperandKind.assume(\"${escape(kind.kind)}\"), ${trustedText(kind.category)}, $enumerants)"
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
    val counts = bases.groupMapReduce(value => value)(_ => 1)(_ + _)
    entry.operands.zip(bases).foldLeft((Map.empty[String, Int], Vector.empty[Field])) {
      case ((seen, fields), (operand, base)) =>
        val occurrence = seen.getOrElse(base, 0)
        val name       = if (counts(base) == 1) base else s"$base${letter(occurrence)}"
        val next       = (seen.updated(base, occurrence + 1), fields :+ Field(name, operand))
        next
    }._2
  }

  private def normalizeCase(value: String): String = {
    val words  = Text.identifierWords(value)
    val result = words.map(word => s"${word.head.toUpper}${word.tail}").mkString
    if (result.headOption.exists(_.isDigit)) s"Instruction$result" else result
  }

  private def normalizeField(value: String): String = {
    val words       = Text.identifierWords(value.replace("~", ""))
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
      packageVersion: ScalaPackageVersion
  ): String = {
    val sourceRows = upstream.map { case (file, names) => s"    \"${escape(file)}\" -> ${names.size}" }.mkString(",\n")
    GeneratedHeader.scala("from pinned Khronos grammar and SPIRV-Tools tests") +
      s"""|package net.kurobako.gemstone.isa.spirv.${packageVersion.value}
        |
        |import net.kurobako.gemstone.Vec
        |
        |import io.github.iltotore.iron.*
        |import io.github.iltotore.iron.constraint.all.*
        |
        |${GeneratedHeader.annotation()}final class ReferenceSuite extends munit.FunSuite {
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
        |      val instruction = Instruction.decode(rawInstruction).fold(error => fail(s"$${definition.opcode}: $${error.message}"), value => value)
        |      assertEquals(instruction.definition, definition, definition.opcode)
        |      assert(same(Instruction.encode(instruction), rawInstruction), definition.opcode)
        |      val rawModule = RawModule(
        |        Header(ISA.version, Word(0), Word(1), Word(0)),
        |        Vec(rawInstruction),
        |        Endianness.Little
        |      )
        |      val bytes = Binary.encode(rawModule).fold(error => fail(error.message), value => value)
        |      assert(Binary.decode(bytes).exists(same(_, rawModule)), definition.opcode)
        |    }
        |  }
        |
        |  test("SPIRV-Tools endian oracle is represented") {
        |    assert(upstreamTestCounts("binary_endianness_test.cpp") > 0)
        |    val module = RawModule(Header(ISA.version, Word(7), Word(2), Word(0)), Vec.empty, Endianness.Big)
        |    val bytes = Binary.encode(module).fold(error => fail(error.message), value => value)
        |    assert(Binary.decode(bytes).exists(same(_, module)))
        |  }
        |
        |  test("SPIRV-Tools parser negative classes are fail-closed") {
        |    assert(upstreamTestCounts("binary_parse_test.cpp") >= 20)
        |    val malformed = Vec(
        |      Vec[Byte](1, 2, 3),
        |      words(0x07230203, 0x00010600, 0, 1),
        |      words(0xdeadbeef, 0x00010600, 0, 1, 0),
        |      words(0x07230203, 0x00010600, 0, 1, 0, 0),
        |      words(0x07230203, 0x00010600, 0, 1, 0, (3 << 16) | 17, 6)
        |    )
        |    malformed.foreach(bytes => assert(Binary.decode(bytes).isLeft))
        |  }
        |
        |  test("unknown binary opcodes round-trip raw but fail typed decode") {
        |    val unknownOpcode = SPIRVOpcodeNumber(65535)
        |    val raw = RawInstruction(unknownOpcode, Vec(Word(7)))
        |    assertEquals(Instruction.decode(raw), Left(InstructionDecodeError.UnknownOpcode(unknownOpcode)))
        |    val module = RawModule(Header(ISA.version, Word(0), Word(1), Word(0)), Vec(raw), Endianness.Little)
        |    val bytes = Binary.encode(module).fold(error => fail(error.message), value => value)
        |    assert(Binary.decode(bytes).exists(same(_, module)))
        |  }
        |
        |  test("known opcodes with malformed grammar operands fail typed decode") {
        |    Instruction.decode(RawInstruction(SPIRVOpcodeNumber(17), Vec.empty)) match {
        |      case Left(InstructionDecodeError.InvalidOperandCount(opcode, _))
        |          if opcode == SPIRVOpcode("OpCapability") => ()
        |      case other => fail(s"expected missing-operand failure, found $$other")
        |    }
        |    Instruction.decode(RawInstruction(SPIRVOpcodeNumber(17), Vec(Word(-1)))) match {
        |      case Left(InstructionDecodeError.InvalidEnumerant(kind, 0xffffffffL))
        |          if kind == SPIRVOperandKind("Capability") => ()
        |      case other => fail(s"expected invalid-enumerant failure, found $$other")
        |    }
        |    Instruction.decode(RawInstruction(SPIRVOpcodeNumber(5), Vec(Word(1), Word(-1)))) match {
        |      case Left(InstructionDecodeError.UnterminatedString(opcode))
        |          if opcode == SPIRVOpcode("OpName") => ()
        |      case other => fail(s"expected unterminated-string failure, found $$other")
        |    }
        |  }
        |
        |  test("SPIRV-Tools OpenCL validation corpus is tracked") {
        |    assert(upstreamTestCounts("val/val_opencl_test.cpp") >= 10)
        |  }
        |
        |  private def words(values: Int*): Vec[Byte] = Vec.from(values).flatMap { value =>
        |    Vec(value.toByte, (value >>> 8).toByte, (value >>> 16).toByte, (value >>> 24).toByte)
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
  private def optionalStringArray(
      obj: ujson.Obj,
      name: String
  ): Vector[String :| (MinLength[1] & NoNul)] = obj.value.get(name).toVector
    .flatMap {
      case ujson.Arr(values) => values.toVector.map {
          case ujson.Str(value) => nonEmpty(value, s"JSON field $name")
          case _                => throw IllegalArgumentException(s"JSON field $name contains a non-string")
        }
      case _ => throw IllegalArgumentException(s"JSON field $name is not an array")
    }
  private def stringField(obj: ujson.Obj, name: String): String :| (MinLength[1] & NoNul) = field(obj, name) match {
    case ujson.Str(value) => nonEmpty(value, s"JSON field $name")
    case _                => throw IllegalArgumentException(s"JSON field $name is not a string")
  }
  private def optionalString(
      obj: ujson.Obj,
      name: String
  ): Option[String :| (MinLength[1] & NoNul)] = obj.value.get(name).map {
    case ujson.Str(value) => nonEmpty(value, s"JSON field $name")
    case _                => throw IllegalArgumentException(s"JSON field $name is not a string")
  }
  private def numberField(obj: ujson.Obj, name: String): Int = field(obj, name) match {
    case ujson.Num(value) if value.isValidInt => value.toInt
    case _                                    => throw IllegalArgumentException(s"JSON field $name is not an int")
  }
  private def opcodeField(obj: ujson.Obj, name: String): Int :| Interval.Closed[0, 65535] =
    numberField(obj, name).refineEither[Interval.Closed[0, 65535]]
      .fold(error => throw IllegalArgumentException(s"invalid JSON field $name: $error"), value => value)

  private def versionField(obj: ujson.Obj, name: String): Int :| Interval.Closed[0, 255] =
    numberField(obj, name).refineEither[Interval.Closed[0, 255]]
      .fold(error => throw IllegalArgumentException(s"invalid JSON field $name: $error"), value => value)

  private def nonEmpty(value: String, description: String): String :| (MinLength[1] & NoNul) =
    value.refineEither[MinLength[1] & NoNul]
      .fold(
        error => throw IllegalArgumentException(s"$description must be non-empty and contain no NUL: $error"),
        value => value
      )
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

  private def trustedText(value: String): String =
    s"\"${escape(value)}\".assume[MinLength[1] & NoNul]"

  private def checkedPackageVersion(value: String): ScalaPackageVersion =
    ScalaPackageVersion.either(value).fold(
      message => throw IllegalArgumentException(s"invalid SPIR-V package version '$value': $message"),
      valid => valid
    )

  private def write(path: Path, content: String): Unit = {
    val _ = GeneratedFiles.write(path, GeneratedScalaFormatter(path, content))
  }

  private def readExtendedInstructions(directory: Path): Vector[ExtendedInstructionSet] =
    ExtendedInstructionGrammars.map { case (file, name, objectName) =>
      val root = ujson.read(Files.readString(directory.resolve(file), StandardCharsets.UTF_8)) match {
        case value: ujson.Obj => value
        case _                => throw IllegalArgumentException(s"$file root is not an object")
      }
      val instructions = arrayField(root, "instructions").map {
        case obj: ujson.Obj =>
          val opname: String = stringField(obj, "opname")
          val opcode: Int    = opcodeField(obj, "opcode")
          opname -> opcode
        case _ => throw IllegalArgumentException(s"$file instruction entry is not an object")
      }
      require(
        instructions.map(_._1).distinct.size == instructions.size,
        s"$file has duplicate extended instruction names"
      )
      require(
        instructions.map(_._2).distinct.size == instructions.size,
        s"$file has duplicate extended instruction opcodes"
      )
      ExtendedInstructionSet(name, objectName, instructions)
    }

  private def renderExtendedInstructions(
      sets: Vector[ExtendedInstructionSet],
      packageVersion: ScalaPackageVersion
  ): String = {
    val objects = sets.map { set =>
      val values = set.instructions.map { case (name, opcode) =>
        val safe = if (ScalaKeywords(name)) s"`$name`" else name
        s"    final val $safe = $opcode"
      }.mkString("\n")
      s"""|  /** ${escape(set.name)} */
          |  object ${set.objectName} {
          |$values
          |  }""".stripMargin
    }.mkString("\n\n")
    GeneratedHeader.scala("from the pinned Khronos SPIR-V extended instruction grammars") +
      s"""|package net.kurobako.gemstone.isa.spirv.${packageVersion.value}
          |
          |${GeneratedHeader.annotation()}object ExtendedInstruction {
          |$objects
          |}
          |""".stripMargin
  }

  private def renderBuiltIns(grammar: Grammar, packageVersion: ScalaPackageVersion): String = {
    val builtins = grammar.kinds.find(_.kind == "BuiltIn").map(_.enumerants).getOrElse(Vector.empty)
    require(builtins.nonEmpty, "SPIR-V grammar declares no BuiltIn enumerants")
    val values = builtins.map { enumerant =>
      val name: String = enumerant.name
      val safe         = if (ScalaKeywords(name)) s"`$name`" else name
      s"    final val $safe: Int = ${enumerant.value}"
    }.mkString("\n")
    GeneratedHeader.scala("from the pinned Khronos SPIR-V core grammar") +
      s"""|package net.kurobako.gemstone.isa.spirv.${packageVersion.value}
          |
          |${GeneratedHeader.annotation()}object BuiltIn {
          |$values
          |}
          |""".stripMargin
  }

  private def syncRoot(root: Path, outputs: Map[Path, String]): Unit = {
    outputs.foreach { case (path, content) => write(path, content) }
    val include = (path: Path) => path.getFileName.toString.endsWith(".scala")
    GeneratedFiles.removeUnexpected(root, outputs.keySet, include)
  }

}

package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.string.*
import ujson.Value

object GenerateSPIRVISA {
  private val MetadataChunkSize = 200

  private sealed trait Request
  private case class Instructions(
      grammar: Path,
      extinst: Path,
      sourceRoot: Path,
      nativeRoot: Path,
      packageVersion: String
  ) extends Request
  private case class ExtendedInstructionSet(name: String, objectName: String, instructions: Vector[(String, Int)])
  private case class Tests(grammar: Path, tests: Path, sourceRoot: Path, packageVersion: String)
      extends Request
  private case class Operand(
      kind: NonEmpty[String],
      name: Option[NonEmpty[String]],
      quantifier: Option[NonEmpty[String]]
  )
  private case class Enumerant(
      value: Long,
      name: NonEmpty[String],
      parameters: Vector[NonEmpty[String]],
      aliases: Vector[NonEmpty[String]]
  )
  private case class OperandKind(
      kind: NonEmpty[String],
      category: NonEmpty[String],
      bases: Vector[NonEmpty[String]],
      enumerants: Vector[Enumerant]
  )
  private case class Entry(
      opcode: Int,
      name: NonEmpty[String],
      instructionClass: NonEmpty[String],
      version: Option[NonEmpty[String]],
      capabilities: Vector[NonEmpty[String]],
      operands: Vector[Operand],
      aliases: Vector[NonEmpty[String]]
  )
  private case class Grammar(
      magic: Long,
      major: Int in 0 ~ 255,
      minor: Int in 0 ~ 255,
      revision: Int >= 0,
      entries: NonEmpty[Vec[Entry]],
      kinds: NonEmpty[Vec[OperandKind]]
  )
  private case class Field(name: String, operand: Operand)
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
        Opts.argument[String]("generated-native-root").map(Path.of(_)),
        Opts.argument[String]("package-version")
      ).mapN(Instructions(_, _, _, _, _))),
      Command("tests", "Generate reference tests")((
        Opts.argument[String]("grammar").map(Path.of(_)),
        Opts.argument[String]("tests").map(Path.of(_)),
        Opts.argument[String]("generated-source-root").map(Path.of(_)),
        Opts.argument[String]("package-version")
      ).mapN(Tests(_, _, _, _)))
    )
  )

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) {
    case Instructions(grammarPath, extinstDirectory, sourceRoot, nativeRoot, packageVersion) =>
      val version    = checkedPackageVersion(packageVersion)
      val grammar    = readGrammar(grammarPath)
      val extended   = readExtendedInstructions(extinstDirectory)
      val base       = sourceRoot.resolve(PackageRoot).resolve(version.raw)
      val enumerants = grammar.kinds.toVector.filter(_.enumerants.nonEmpty).map { kind =>
        base.resolve(s"${kind.kind}.scala") -> renderEnumerants(kind, version)
      }
      syncRoot(
        sourceRoot,
        (Map(
          base.resolve("SPIRV.scala")               -> renderConstants(grammar, version),
          base.resolve("OperandKind.scala")         -> renderOperandKinds(grammar, version),
          base.resolve("NarrowEncoding.scala")      -> ImageFormats.narrowEncodingSource(version),
          base.resolve("ImageFormats.scala")        -> ImageFormats.scalaSource(version),
          base.resolve("ImageDescriptor.scala")     -> ImageDescriptorABI.scalaSource(version),
          base.resolve("ExtendedInstruction.scala") -> renderExtendedInstructions(extended, version)
        ) ++ renderInstructions(grammar, version).map { case (name, content) => base.resolve(name) -> content } ++
          enumerants).toMap
      )
      val _ = GeneratedFiles.write(nativeRoot.resolve("gemstone_image_formats.hpp"), ImageFormats.nativeSource)
      val nativeOutput = nativeRoot.resolve("gemstone_spirv_constants.hpp")
      val _            = GeneratedFiles.write(nativeOutput, renderNativeConstants(grammar))
      val _ = GeneratedFiles.write(nativeRoot.resolve("gemstone_image_descriptor.hpp"), ImageDescriptorABI.nativeSource)
      Console.out.println(
        s"generated ${grammar.entries.size} typed SPIR-V core instructions and ${extended.map(_.instructions.size).sum} extended instructions"
      )
    case Tests(grammarPath, testRoot, sourceRoot, packageVersion) =>
      val version  = checkedPackageVersion(packageVersion)
      val grammar  = readGrammar(grammarPath)
      val upstream = Vector("binary_parse_test.cpp", "binary_endianness_test.cpp", "val/val_opencl_test.cpp")
        .map(name => name -> discoveredTests(testRoot.resolve(name)))
      val output = sourceRoot.resolve(PackageRoot).resolve(version.raw).resolve("ReferenceSuite.scala")
      syncRoot(sourceRoot, Map(output -> renderTests(grammar.entries.length, upstream, version)))
      Console.out.println(
        s"generated SPIR-V reference suite for ${grammar.entries.size} opcodes and ${upstream.map(_._2.size).sum} upstream tests"
      )
  }

  // OpSpecConstantOp's trailing operands depend on the embedded opcode and are
  // not listed in the core grammar, so append a repeated single-word tail. Every
  // permitted operand there (IdRef or LiteralInteger) is one word wide.
  private def grammarOverride(opname: String): Vector[Operand] =
    if (opname == "OpSpecConstantOp")
      Vector(
        Operand(
          "LiteralInteger",
          Some("operands"),
          Some("*")
        )
      )
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
          } ++ grammarOverride(stringField(obj, "opname")),
          aliases = optionalStringArray(obj, "aliases")
        )
      case _ => throw IllegalArgumentException("instruction entry is not an object")
    }.sortBy(_.opcode)
    val wrappedEntries = Vec.from(entries).unsafe[NonEmpty[Vec[Entry]]]
    val kinds          = arrayField(root, "operand_kinds").map {
      case obj: ujson.Obj =>
        OperandKind(
          kind = stringField(obj, "kind"),
          category = stringField(obj, "category"),
          bases = optionalStringArray(obj, "bases"),
          enumerants = optionalObjectArray(obj, "enumerants").map { enumerant =>
            Enumerant(
              parseUnsigned(valueField(enumerant, "value")),
              stringField(enumerant, "enumerant"),
              optionalObjectArray(enumerant, "parameters").map(parameter => stringField(parameter, "kind")),
              optionalStringArray(enumerant, "aliases")
            )
          }.groupBy(_.value).values.map { values =>
            val first = values.head
            first.copy(aliases = (first.aliases ++ values.tail.flatMap(value => value.name +: value.aliases)).distinct)
          }.toVector.sortBy(_.value)
        )
      case _ => throw IllegalArgumentException("operand kind is not an object")
    }
    Grammar(
      magic = parseUnsigned(valueField(root, "magic_number")),
      major = versionField(root, "major_version"),
      minor = versionField(root, "minor_version"),
      revision = numberField(root, "revision").refine[Int >= 0]
        .fold(error => throw IllegalArgumentException(s"invalid grammar revision: $error"), identity),
      entries = wrappedEntries,
      kinds = Vec.from(kinds).unsafe[NonEmpty[Vec[OperandKind]]]
    )
  }

  private def renderInstructions(grammar: Grammar, packageVersion: ScalaPackageVersion): Map[String, String] = {
    val entriesWithFields = grammar.entries.toVector.map(entry => entry -> fields(entry))
    val indexed           = entriesWithFields.zipWithIndex
    val definitions       = indexed.map { case ((entry, entryFields), index) =>
      renderDefinition(
        entry,
        entryFields,
        s"InstructionMetadata${index / MetadataChunkSize}.m${index % MetadataChunkSize}"
      )
    }
    val cases         = entriesWithFields.map { case (entry, entryFields) => renderCase(entry, entryFields) }
    val metadataFiles = indexed.groupBy { case (_, index) => index / MetadataChunkSize }.map { case (chunk, values) =>
      val body = values
        .map { case ((entry, entryFields), index) =>
          s"  lazy val m${index % MetadataChunkSize}: Metadata = ${renderMetadata(entry, entryFields)}"
        }
        .mkString("\n")
      s"InstructionMetadata$chunk.scala" ->
        (GeneratedHeader.scala("from the pinned Khronos SPIR-V core grammar") +
          s"""|package net.kurobako.gemstone.isa.spirv.$packageVersion
              |
              |import net.kurobako.gemstone.constraint.*
              |import net.kurobako.gemstone.collection.*
              |
              |${GeneratedHeader.annotation()}object InstructionMetadata$chunk {
              |$body
              |}
              |""".stripMargin)
    }
    val aliases = grammar.entries.toVector.flatMap(entry =>
      entry.aliases.map(name =>
        s"${Emission.literal(name)} -> Definition.${normalizeCase(entry.name.stripPrefix("Op"))}"
      )
    ).mkString("Map(", ", ", ")")
    val kindValues =
      grammar.kinds.toVector.filter(kind => kind.category == "ValueEnum" || kind.category == "BitEnum")
        .map(renderKind(_))
    val kindChunks = kindValues.grouped(8).zipWithIndex.map { case (values, index) =>
      s"  private def operandKinds$index: Vec[OperandKindInfo] = Vec(\n${values.mkString(",\n")}\n  )"
    }.toVector
    val kindExpression = kindChunks.indices.map(index => s"operandKinds$index").mkString("Vec(", ", ", ").flatten")
    val instruction    = GeneratedHeader.scala("from the pinned Khronos SPIR-V core grammar") +
      s"""|package net.kurobako.gemstone.isa.spirv.$packageVersion
        |
        |import net.kurobako.gemstone.constraint.*
        |import net.kurobako.gemstone.isa.*
        |import net.kurobako.gemstone.collection.*
        |import net.kurobako.gemstone.collection.Vec.*
        |import net.kurobako.gemstone.isa.spirv.$packageVersion.OperandDecoder.*
        |
        |${GeneratedHeader.annotation()}object Instruction {
        |  type Decoder = RawInstruction => Either[InstructionDecodeError, Instruction]
        |
        |  enum Definition(
        |      val opcode: NonEmpty[java.lang.String],
        |      val opcodeNumber: SPIRVOpcodeNumber,
        |      decode: Definition => Decoder,
        |      val meta: Metadata
        |  ) {
        |${definitions.mkString("\n")}
        |
        |    lazy val decoder: Decoder = decode(this)
        |  }
        |
        |  val version: Version = Version(
        |    ${grammar.major},
        |    ${grammar.minor}
        |  )
        |  val grammarRevision: Int >= 0 = ${grammar.revision}
        |  val definitions: Vec[Definition] = Definition.values.toVec
        |
        |${kindChunks.mkString("\n\n")}
        |
        |  val operandKinds: Map[OperandKind, OperandKindInfo] =
        |    $kindExpression.view.map(value => value.kind -> value).toMap
        |
        |  private val aliases: Map[java.lang.String, Definition] = $aliases
        |  private val byNumber: Map[SPIRVOpcodeNumber, Vec[Definition]] =
        |    definitions.groupMap(_.opcodeNumber)(value => value)
        |  private val byOpcode: Map[NonEmpty[java.lang.String], Vec[Definition]] =
        |    definitions.groupMap(_.opcode)(value => value)
        |
        |  def definitionsFor(opcodeNumber: SPIRVOpcodeNumber): Vec[Definition] =
        |    byNumber.getOrElse(opcodeNumber, Vec.empty[Definition])
        |  def definitionsFor(opcode: NonEmpty[java.lang.String]): Vec[Definition] =
        |    byOpcode.getOrElse(opcode, aliases.get(opcode).toVec)
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
        |              Vec.nonEmpty(definitions.head.opcode, definitions.tail.map(_.opcode)*)
        |            ))
        |          case values =>
        |            Left(InstructionDecodeError.AmbiguousDefinition(
        |              raw.opcodeNumber,
        |              Vec.nonEmpty(values.head._1.opcode, values.tail.map(_._1.opcode)*)
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
        |  private def decodeDefinition(
        |      definition: Definition,
        |      raw: RawInstruction,
        |      construct: DecodedOperands => Instruction
        |  ): Either[InstructionDecodeError, Instruction] =
        |    if (raw.opcodeNumber != definition.opcodeNumber)
        |      Left(InstructionDecodeError.DecoderOpcodeMismatch(definition.opcodeNumber, raw.opcodeNumber))
        |    else {
        |      val decoded = OperandDecoder.decode(definition.opcode, raw.operandWords, definition.meta.operands, operandKinds)
        |      // The grammar uses 32-bit switch literals; retain 64-bit cases until the selector type is resolved.
        |      val operands = if (definition == Definition.Switch)
        |        decoded.orElse(OperandDecoder.decodeSwitch64(definition.opcode, raw.operandWords).orElse(decoded))
        |      else decoded
        |      operands.map(construct)
        |    }
        |}
        |
        |${GeneratedHeader.annotation()}enum Instruction(val definition: Instruction.Definition, operandValues: Operand*) {
        |${cases.mkString("\n")}
        |
        |  val operands: Vec[Operand] = operandValues.toVec
        |  final def opcode: NonEmpty[java.lang.String] = definition.opcode
        |  final def opcodeNumber: SPIRVOpcodeNumber = definition.opcodeNumber
        |  final def decoder: Instruction.Decoder = definition.decoder
        |  final def meta: Metadata = definition.meta
        |}
        |
        |""".stripMargin
    Map("Instruction.scala" -> instruction) ++ metadataFiles
  }

  private def renderDefinition(entry: Entry, fields: Vector[Field], metadataRef: String): String = {
    val caseName  = normalizeCase(entry.name.stripPrefix("Op"))
    val parameter = if (fields.isEmpty) "_" else "values"
    val decoder   =
      s"definition => raw => Instruction.decodeDefinition(definition, raw, $parameter => ${construct(entry, fields)})"
    s"    case $caseName extends Definition(\"${Emission.escape(entry.name)}\", " +
      s"SPIRVOpcodeNumber(${entry.opcode}), $decoder, $metadataRef)"
  }

  private def renderCase(entry: Entry, fields: Vector[Field]): String = {
    val caseName = normalizeCase(entry.name.stripPrefix("Op"))
    if (fields.isEmpty) s"  case $caseName() extends Instruction(Instruction.Definition.$caseName)"
    else {
      val parameters = fields.map { field =>
        field.operand.quantifier match {
          case Some("?") => s"${field.name}: Option[Operand]"
          case Some("*") => s"${field.name}: Vec[Operand]"
          case _         => s"${field.name}: Operand"
        }
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
    val since        = entry.version.map(value => s"Some(${Emission.literal(value)})").getOrElse("None")
    val capabilities = entry.capabilities.map(value => Emission.literal(value)).mkString("Vec(", ", ", ")")
    val operands     = fields.map { field =>
      val quantifier = field.operand.quantifier match {
        case Some("?") => "OperandQuantifier.Optional"
        case Some("*") => "OperandQuantifier.Repeated"
        case _         => "OperandQuantifier.One"
      }
      s"OperandShape(OperandKind.${field.operand.kind}, ${Emission.literal(field.name)}, $quantifier)"
    }.mkString("Vec(", ", ", ")")
    s"Metadata(${Emission.literal(entry.instructionClass)}, $since, $capabilities, $operands)"
  }

  private def renderOperandKinds(grammar: Grammar, packageVersion: ScalaPackageVersion): String = {
    val categories = grammar.kinds.map(_.category).distinct.sorted.mkString(", ")
    val cases      = grammar.kinds.map { kind =>
      val bases = kind.bases.map(value => s"OperandKind.$value").mkString("Vector(", ", ", ")")
      s"  case ${kind.kind} extends OperandKind(OperandKind.Category.${kind.category}, $bases)"
    }.mkString("\n")
    GeneratedHeader.scala("from the pinned Khronos SPIR-V core grammar") +
      s"""|package net.kurobako.gemstone.isa.spirv.$packageVersion
          |
          |object OperandKind {
          |  enum Category { case $categories }
          |}
          |
          |${GeneratedHeader.annotation()}enum OperandKind(val category: OperandKind.Category, val components: Vector[OperandKind]) {
          |$cases
          |}
          |""".stripMargin
  }

  private def renderKind(kind: OperandKind): String = {
    val enumerants = kind.enumerants.map { enumerant =>
      val parameters = enumerant.parameters.map(value => s"OperandKind.$value")
        .mkString("Vec(", ", ", ")")
      val aliases = enumerant.aliases.map(Emission.literal(_)).mkString("Vec(", ", ", ")")
      s"OperandEnumerant(${Emission.literal(enumerant.name)}, ${enumerant.value}L, $parameters, $aliases)"
    }.mkString("Vec(", ", ", ")")
    s"    OperandKindInfo(OperandKind.${kind.kind}, $enumerants)"
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
      case (operand, index) if operand.name.exists(_.toLowerCaseRoot.startsWith("operand")) =>
        index
    }
    val bases = entry.operands.zipWithIndex.map { case (operand, index) =>
      val name =
        if (operand.kind == "IdResultType") "resultType"
        else if (operand.kind == "IdResult") "dest"
        else if (operand.kind == "MemoryAccess" || operand.kind == "ImageOperands") "mods"
        else operand.name.map(_.toLowerCaseRoot) match {
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

  private def normalizeCase(value: String): String = Identifiers.upperCamel(value, "", "Instruction")

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
    val provenance = upstream.map { case (file, names) =>
      s"// Reference inventory: $file (${names.size} upstream test declarations; not a coverage claim)."
    }.mkString("\n")
    GeneratedHeader.scala("from pinned Khronos grammar and SPIRV-Tools tests") +
      s"""        |package net.kurobako.gemstone.isa.spirv.$packageVersion
        |
        |import net.kurobako.gemstone.constraint.*
        |import net.kurobako.gemstone.collection.*
        |
        |$provenance
        |${GeneratedHeader.annotation()}class ReferenceSuite extends munit.FunSuite {
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
        |  test("big-endian headers match fixed wire bytes") {
        |    val module = RawModule(Header(ISA.version, Word(7), Word(2), Word(0)), Vec.empty, Endianness.Big)
        |    val bytes = Binary.encode(module).fold(error => fail(error.message), value => value)
        |    val expected = Vec[Byte](7, 35, 2, 3, 0, 1, 6, 0, 0, 0, 0, 7, 0, 0, 0, 2, 0, 0, 0, 0)
        |    assertEquals(bytes, expected)
        |    assert(Binary.decode(expected).exists(same(_, module)))
        |  }
        |
        |  test("malformed binary headers and instruction lengths are rejected") {
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
        |          if opcode == "OpCapability" => ()
        |      case other => fail(s"expected missing-operand failure, found $$other")
        |    }
        |    Instruction.decode(RawInstruction(SPIRVOpcodeNumber(17), Vec(Word(-1)))) match {
        |      case Left(InstructionDecodeError.InvalidEnumerant(kind, 0xffffffffL))
        |          if kind == OperandKind.Capability => ()
        |      case other => fail(s"expected invalid-enumerant failure, found $$other")
        |    }
        |    Instruction.decode(RawInstruction(SPIRVOpcodeNumber(5), Vec(Word(1), Word(-1)))) match {
        |      case Left(InstructionDecodeError.UnterminatedString(opcode))
        |          if opcode == "OpName" => ()
        |      case other => fail(s"expected unterminated-string failure, found $$other")
        |    }
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
  ): Vector[NonEmpty[String]] = obj.value.get(name).toVector
    .flatMap {
      case ujson.Arr(values) => values.toVector.map {
          case ujson.Str(value) => nonEmpty(value, s"JSON field $name")
          case _                => throw IllegalArgumentException(s"JSON field $name contains a non-string")
        }
      case _ => throw IllegalArgumentException(s"JSON field $name is not an array")
    }
  private def stringField(obj: ujson.Obj, name: String): NonEmpty[String] = field(obj, name) match {
    case ujson.Str(value) => nonEmpty(value, s"JSON field $name")
    case _                => throw IllegalArgumentException(s"JSON field $name is not a string")
  }
  private def optionalString(
      obj: ujson.Obj,
      name: String
  ): Option[NonEmpty[String]] = obj.value.get(name).map {
    case ujson.Str(value) => nonEmpty(value, s"JSON field $name")
    case _                => throw IllegalArgumentException(s"JSON field $name is not a string")
  }
  private def numberField(obj: ujson.Obj, name: String): Int = field(obj, name) match {
    case ujson.Num(value) if value.isValidInt => value.toInt
    case _                                    => throw IllegalArgumentException(s"JSON field $name is not an int")
  }
  private def opcodeField(obj: ujson.Obj, name: String): Int = {
    val value = numberField(obj, name)
    if (value >= 0 && value <= 65535) value
    else throw IllegalArgumentException(s"JSON field $name must be between 0 and 65535: $value")
  }

  private def versionField(obj: ujson.Obj, name: String): Int in 0 ~ 255 =
    numberField(obj, name).refine[Int in 0 ~ 255]
      .fold(error => throw IllegalArgumentException(s"invalid JSON field $name: $error"), identity)

  private def nonEmpty(value: String, description: String): NonEmpty[String] =
    value.refine[NonEmpty[String]]
      .fold(
        error => throw IllegalArgumentException(s"$description must be non-empty: $error"),
        identity
      )
  private def parseUnsigned(value: Value): Long = value match {
    case ujson.Str(text) if text.startsWith("0x") => java.lang.Long.parseUnsignedLong(text.drop(2), 16)
    case ujson.Str(text)                          => java.lang.Long.parseLong(text)
    case ujson.Num(number) if number.isWhole && number >= Long.MinValue && number <= Long.MaxValue => number.toLong
    case _ => throw IllegalArgumentException("enumerant value is not an integer")
  }

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
        val safe = Identifiers.upperCamel(name, "Value", "Value")
        s"    final val $safe = $opcode"
      }.mkString("\n")
      s"""|  /** ${Emission.escape(set.name)} */
          |  object ${set.objectName} {
          |$values
          |  }""".stripMargin
    }.mkString("\n\n")
    val names = sets.map { set =>
      val entries =
        set.instructions.map { case (name, opcode) => s"${Emission.literal(name)} -> $opcode" }.mkString(", ")
      s"    ${Emission.literal(set.name)} -> Map($entries)"
    }.mkString(",\n")
    GeneratedHeader.scala("from the pinned Khronos SPIR-V extended instruction grammars") +
      s"""|package net.kurobako.gemstone.isa.spirv.$packageVersion
          |
          |${GeneratedHeader.annotation()}object ExtendedInstruction {
          |  val names: Map[String, Map[String, Int]] = Map(
          |$names
          |  )
          |$objects
          |}
          |""".stripMargin
  }

  // These domains are consumed by reflection and execution on both sides of the RPC ABI.
  private val ConstantKinds = Set(
    "BuiltIn",
    "Decoration",
    "ExecutionModel",
    "ExecutionMode",
    "StorageClass",
    "Dim",
    "ImageFormat",
    "ImageOperands",
    "FPRoundingMode",
    "FunctionParameterAttribute",
    "Scope",
    "GroupOperation"
  )

  private def constantGroups(grammar: Grammar): Vector[(String, Vector[(String, Long)])] =
    Vector("Opcode" ->
      grammar.entries.toVector.map(entry => entry.name.raw.stripPrefix("Op") -> entry.opcode.toLong)) ++
      grammar.kinds.toVector.filter(kind => ConstantKinds(kind.kind.raw)).map { kind =>
        kind.kind.raw -> kind.enumerants.map { enumerant =>
          val name = enumerant.name.raw
          (if (name.head.isDigit) s"_$name" else name) -> enumerant.value
        }
      }

  private def renderConstants(grammar: Grammar, packageVersion: ScalaPackageVersion): String = {
    val groups = constantGroups(grammar).filter(_._1 == "Opcode").map { case (kind, values) =>
      val fields = values.map { case (name, value) =>
        val safe = if (ScalaKeywords(name)) s"`$name`" else name
        s"    final val $safe: Int = ${value.toInt}"
      }.mkString("\n")
      s"  object $kind {\n$fields\n  }"
    }.mkString("\n\n")
    GeneratedHeader.scala("from the pinned Khronos SPIR-V core grammar") +
      s"""|package net.kurobako.gemstone.isa.spirv.${packageVersion.raw}
          |
          |${GeneratedHeader.annotation()}object SPIRV {
          |  final val Magic: Int = ${grammar.magic}
          |$groups
          |}
          |""".stripMargin
  }

  private def renderNativeConstants(grammar: Grammar): String = {
    val groups = constantGroups(grammar).map { case (kind, values) =>
      val fields = values.map { case (name, value) => s"inline constexpr uint32_t $name = ${value}u;" }.mkString("\n")
      s"namespace $kind {\n$fields\n} // namespace $kind"
    }.mkString("\n\n")
    GeneratedHeader.cpp("from the pinned Khronos SPIR-V core grammar") +
      s"#pragma once\n\n#include <cstdint>\n\nnamespace gemstone::spirv {\ninline constexpr uint32_t Magic = ${grammar.magic}u;\n$groups\n} // namespace gemstone::spirv\n"
  }

  private def renderEnumerants(kind: OperandKind, packageVersion: ScalaPackageVersion): String = {
    val raw        = kind.kind
    val objectName = if (ScalaKeywords(raw)) s"`$raw`" else raw
    val values     = kind.enumerants.map { enumerant =>
      val name: String = enumerant.name
      val safe         = Identifiers.upperCamel(name, "Value", "Value")
      s"    final val $safe: Int = ${enumerant.value.toInt}"
    }.mkString("\n")
    GeneratedHeader.scala("from the pinned Khronos SPIR-V core grammar") +
      s"""|package net.kurobako.gemstone.isa.spirv.$packageVersion
          |
          |${GeneratedHeader.annotation()}object $objectName {
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

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
  private case class Instructions(
      grammar: Path,
      extinst: Path,
      sourceRoot: Path,
      nativeRoot: Path
  )
  private case class ExtendedInstructionSet(
      name: String,
      objectName: String,
      instructions: Vector[(String, Int)],
      grammar: Vector[(String, Vector[String], Vector[String])]
  )
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
      operands: Vector[Operand],
      aliases: Vector[NonEmpty[String]],
      grammarClass: String = "",
      version: String = "",
      capabilities: Vector[NonEmpty[String]] = Vector.empty,
      extensions: Vector[NonEmpty[String]] = Vector.empty
  )
  private case class Grammar(
      magic: Long,
      major: Int in 0 ~ 255,
      minor: Int in 0 ~ 255,
      entries: NonEmpty[Vec[Entry]],
      kinds: NonEmpty[Vec[OperandKind]]
  )
  private case class Field(name: String, operand: Operand)
  private val PackageRoot = "net/kurobako/gemstone/isa/spirv"

  // operand kinds whose enumerants hand-written code names as constants
  private val EnumerantObjects = Set(
    "AddressingModel",
    "BuiltIn",
    "Capability",
    "Decoration",
    "Dim",
    "ExecutionMode",
    "ExecutionModel",
    "FPRoundingMode",
    "FunctionControl",
    "FunctionParameterAttribute",
    "ImageFormat",
    "ImageOperands",
    "LinkageType",
    "MemoryAccess",
    "MemoryModel",
    "MemorySemantics",
    "Scope",
    "StorageClass"
  )

  private val ExtendedInstructionGrammars = Vector(
    ("extinst.glsl.std.450.grammar.json", "GLSL.std.450", "GlslStd450"),
    ("extinst.opencl.std.100.grammar.json", "OpenCL.std", "OpenClStd")
  )

  private val command = Command("GenerateSPIRVISA", "Generate SPIR-V ISA sources")(
    Opts.subcommands(
      Command("instructions", "Generate instruction sources")((
        CodegenCommand.path("grammar"),
        CodegenCommand.path("extinst-directory"),
        CodegenCommand.path("generated-source-root"),
        CodegenCommand.path("generated-native-root")
      ).mapN(Instructions(_, _, _, _)))
    )
  )

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) {
    case Instructions(grammarPath, extinstDirectory, sourceRoot, nativeRoot) =>
      val grammar    = readGrammar(grammarPath)
      val extended   = readExtendedInstructions(extinstDirectory)
      val base       = sourceRoot.resolve(PackageRoot)
      val enumerants =
        grammar.kinds.toVector.filter(kind => kind.enumerants.nonEmpty && EnumerantObjects(kind.kind)).map { kind =>
          base.resolve(s"${kind.kind}.scala") -> renderEnumerants(kind)
        }
      syncRoot(
        sourceRoot,
        (Map(
          base.resolve("SPIRV.scala")                   -> renderConstants(grammar),
          base.resolve("OperandKind.scala")             -> renderOperandKinds(grammar),
          base.resolve("NarrowEncoding.scala")          -> ImageFormats.narrowEncodingSource,
          base.resolve("ImageFormats.scala")            -> ImageFormats.scalaSource,
          base.resolve("ImageDescriptor.scala")         -> ImageDescriptorABI.scalaSource,
          base.resolve("PipeLayout.scala")              -> PipeABI.scalaSource,
          base.resolve("ExtendedInstruction.scala")     -> renderExtendedInstructions(extended),
          base.resolve("InstructionRequirements.scala") -> renderRequirements(grammar),
          base.resolve("OperandConstraintTable.scala")  -> renderOperandConstraints(grammar),
          base.resolve("Instruction.scala")             -> renderInstructions(grammar)
        ) ++ enumerants).toMap
      )
      val _ = GeneratedFiles.write(nativeRoot.resolve("gemstone_image_formats.hpp"), ImageFormats.nativeSource)
      val nativeOutput = nativeRoot.resolve("gemstone_spirv_constants.hpp")
      val _            = GeneratedFiles.write(nativeOutput, renderNativeConstants(grammar))
      val _ = GeneratedFiles.write(nativeRoot.resolve("gemstone_image_descriptor.hpp"), ImageDescriptorABI.nativeSource)
      val _ = GeneratedFiles.write(nativeRoot.resolve("gemstone_pipe_layout.hpp"), PipeABI.nativeSource)
      Console.out.println(
        s"generated ${grammar.entries.size} typed SPIR-V core instructions and ${extended.map(_.instructions.size).sum} extended instructions"
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

  // provisional OpenCL 2.1 SPIR-V named BuiltIn 35; later grammars dropped it, but older producers still emit it
  private def legacyEnumerants(kind: String): Vector[Enumerant] =
    if (kind == "BuiltIn") Vector(Enumerant(35L, "WorkgroupLinearId", Vector.empty, Vector.empty))
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
          operands = optionalObjectArray(obj, "operands").map { operand =>
            Operand(
              stringField(operand, "kind"),
              optionalString(operand, "name"),
              optionalString(operand, "quantifier")
            )
          } ++ grammarOverride(stringField(obj, "opname")),
          aliases = optionalStringArray(obj, "aliases"),
          grammarClass = optionalString(obj, "class").fold("")(_.raw),
          version = optionalString(obj, "version").fold("")(_.raw),
          capabilities = optionalStringArray(obj, "capabilities"),
          extensions = optionalStringArray(obj, "extensions")
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
          enumerants =
            (optionalObjectArray(obj, "enumerants").map { enumerant =>
              Enumerant(
                parseUnsigned(field(enumerant, "value")),
                stringField(enumerant, "enumerant"),
                optionalObjectArray(enumerant, "parameters").map(parameter => stringField(parameter, "kind")),
                optionalStringArray(enumerant, "aliases")
              )
            } ++ legacyEnumerants(stringField(obj, "kind"))).groupBy(_.value).values.map { values =>
              val first = values.head
              first.copy(aliases =
                (first.aliases ++ values.tail.flatMap(value => value.name +: value.aliases)).distinct
              )
            }.toVector.sortBy(_.value)
        )
      case _ => throw IllegalArgumentException("operand kind is not an object")
    }
    Grammar(
      magic = parseUnsigned(field(root, "magic_number")),
      major = versionField(root, "major_version"),
      minor = versionField(root, "minor_version"),
      entries = wrappedEntries,
      kinds = Vec.from(kinds).unsafe[NonEmpty[Vec[OperandKind]]]
    )
  }

  private def renderInstructions(grammar: Grammar): String = {
    val entriesWithFields = grammar.entries.toVector.map(entry => entry -> fields(entry))
    val definitions       = entriesWithFields.map((entry, entryFields) => renderDefinition(entry, entryFields))
    val cases             = entriesWithFields.map((entry, entryFields) => renderCase(entry, entryFields))
    val aliases           = Emission.collection(
      "Map",
      grammar.entries.toVector.flatMap(entry =>
        entry.aliases.map(name =>
          s"${Emission.literal(name)} -> Definition.${normaliseCase(entry.name.stripPrefix("Op"))}"
        )
      )
    )
    val kinds = grammar.kinds.toVector.filter(kind => kind.category == "ValueEnum" || kind.category == "BitEnum")
      .map(renderKind(_))
    val instruction = GeneratedHeader.scala("from the pinned Khronos SPIR-V core grammar") +
      s"""|package net.kurobako.gemstone.isa.spirv
        |
        |import net.kurobako.gemstone.constraint.*
        |import net.kurobako.gemstone.isa.*
        |import net.kurobako.gemstone.collection.*
        |import net.kurobako.gemstone.collection.Vec.*
        |import net.kurobako.gemstone.isa.spirv.OperandDecoder.*
        |
        |${GeneratedHeader.annotation()}object Instruction {
        |  type Decoder = RawInstruction => Either[InstructionDecodeError, Instruction]
        |
        |  enum Definition(
        |      val opcode: NonEmpty[java.lang.String],
        |      val opcodeNumber: SPIRVOpcodeNumber,
        |      decode: Definition => Decoder,
        |      operands: java.lang.String
        |  ) derives CanEqual {
        |${definitions.mkString("\n")}
        |
        |    val shapes: Vec[OperandShape] = OperandShape.parse(operands)
        |    lazy val decoder: Decoder = decode(this)
        |  }
        |
        |  val version: Version = Version(
        |    ${grammar.major},
        |    ${grammar.minor}
        |  )
        |  val definitions: Vec[Definition] = Definition.values.toVec
        |
        |  val operandKinds: Map[OperandKind, OperandKindInfo] =
        |    ${table(kinds)}.view.map(row => OperandKindInfo.parse(row)).map(value => value.kind -> value).toMap
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
        |      case _ =>
        |        val matches = candidates.flatMap(definition =>
        |          definition.decoder(raw).toOption.map(instruction => definition -> instruction)
        |        )
        |        matches match {
        |          case Vec((_, instruction)) => Right(instruction)
        |          case Vec() =>
        |            Left(InstructionDecodeError.NoMatchingDefinition(
        |              raw.opcodeNumber,
        |              Vec.nonEmpty(candidates.head.opcode, candidates.tail.map(_.opcode)*)
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
        |  private def decodeDefinition(
        |      definition: Definition,
        |      raw: RawInstruction,
        |      construct: DecodedOperands => Instruction
        |  ): Either[InstructionDecodeError, Instruction] =
        |    if (raw.opcodeNumber != definition.opcodeNumber)
        |      Left(InstructionDecodeError.DecoderOpcodeMismatch(definition.opcodeNumber, raw.opcodeNumber))
        |    else {
        |      val decoded = OperandDecoder.decode(definition.opcode, raw.operandWords, definition.shapes, operandKinds)
        |      // The grammar uses 32-bit switch literals; retain 64-bit cases until the selector type is resolved.
        |      val operands = if (definition == Definition.Switch)
        |        decoded.orElse(OperandDecoder.decodeSwitch64(definition.opcode, raw.operandWords).orElse(decoded))
        |      else decoded
        |      operands.map(construct)
        |    }
        |}
        |
        |${GeneratedHeader.annotation()}enum Instruction(val definition: Instruction.Definition, operandValues: Operand*) derives CanEqual {
        |${cases.mkString("\n")}
        |
        |  def operands: Vec[Operand] = operandValues.toVec
        |  final def opcode: NonEmpty[java.lang.String] = definition.opcode
        |  final def opcodeNumber: SPIRVOpcodeNumber = definition.opcodeNumber
        |  final def decoder: Instruction.Decoder = definition.decoder
        |}
        |
        |""".stripMargin
    instruction
  }

  private def renderDefinition(entry: Entry, fields: Vector[Field]): String = {
    val caseName  = normaliseCase(entry.name.stripPrefix("Op"))
    val parameter = if (fields.isEmpty) "_" else "values"
    val decoder   =
      s"definition => raw => Instruction.decodeDefinition(definition, raw, $parameter => ${construct(entry, fields)})"
    s"    case $caseName extends Definition(\"${Emission.escape(entry.name)}\", " +
      s"SPIRVOpcodeNumber(${entry.opcode}), $decoder, ${Emission.literal(renderShapes(fields))})"
  }

  private def renderCase(entry: Entry, fields: Vector[Field]): String = {
    val caseName = normaliseCase(entry.name.stripPrefix("Op"))
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

  /** The operand shapes `OperandShape.parse` reads. */
  private def renderShapes(fields: Vector[Field]): String =
    fields.map(field => s"${field.operand.kind}${field.operand.quantifier.getOrElse("")}").mkString(" ")

  private def renderOperandKinds(grammar: Grammar): String = {
    val categories = grammar.kinds.map(_.category).distinct.sorted.mkString(", ")
    val cases      = grammar.kinds.map { kind =>
      val bases = Emission.collection("Vector", kind.bases.map(value => s"OperandKind.$value"))
      s"  case ${kind.kind} extends OperandKind(OperandKind.Category.${kind.category}, $bases)"
    }.mkString("\n")
    GeneratedHeader.scala("from the pinned Khronos SPIR-V core grammar") +
      s"""|package net.kurobako.gemstone.isa.spirv
          |
          |object OperandKind {
          |  enum Category derives CanEqual { case $categories }
          |}
          |
          |${GeneratedHeader.annotation()}enum OperandKind(val category: OperandKind.Category, val components: Vector[OperandKind]) derives CanEqual {
          |$cases
          |}
          |""".stripMargin
  }

  /** The table row `OperandKindInfo.parse` reads. */
  private def renderKind(kind: OperandKind): String = {
    def list(values: Vector[NonEmpty[String]]): String = values.map(_.raw).mkString(",")
    val enumerants                                     = kind.enumerants.map { enumerant =>
      require(
        (enumerant.name +: (enumerant.parameters ++ enumerant.aliases)).forall(_.forall(!" ,\t\n".contains(_))),
        s"${kind.kind} enumerant ${enumerant.name} does not fit the operand kind table"
      )
      s"${enumerant.name} ${enumerant.value} ${list(enumerant.parameters)} ${list(enumerant.aliases)}"
    }
    (kind.kind.raw +: enumerants).mkString("\t")
  }

  private def construct(entry: Entry, fields: Vector[Field]): String = {
    val caseName  = normaliseCase(entry.name.stripPrefix("Op"))
    val arguments = fields.zipWithIndex.map { case (field, index) =>
      val value = field.operand.quantifier match {
        case Some("?") => s"values.optional($index)"
        case Some("*") => s"values.repeated($index)"
        case _         => s"values.required($index)"
      }
      if (fields.size > 4) s"${field.name} = $value" else value
    }
    if (arguments.isEmpty) s"Instruction.$caseName()"
    else s"Instruction.$caseName(${arguments.mkString(", ")})"
  }

  private def fields(entry: Entry): Vector[Field] = {
    val generic = entry.operands.zipWithIndex.collect {
      case (operand, index) if operand.name.exists(_.toLowerCaseRoot.startsWith("operand")) =>
        index
    }
    val letters = generic.zipWithIndex.toMap
    val bases   = entry.operands.zipWithIndex.map { case (operand, index) =>
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
            else s"src${letter(letters(index))}"
          case _ => normaliseField(operand.name.getOrElse(operand.kind))
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

  private def normaliseCase(value: String): String = Identifiers.upperCamel(value, "", "Instruction")

  private def normaliseField(value: String): String = {
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

  private def field(obj: ujson.Obj, name: String): Value =
    obj.value.getOrElse(name, throw IllegalArgumentException(s"missing JSON field $name"))
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
      case ujson.Arr(values) => values.toVector.map(string(_, name, "contains a non-string"))
      case _                 => throw IllegalArgumentException(s"JSON field $name is not an array")
    }
  private def string(value: ujson.Value, name: String, problem: String): NonEmpty[String] = value match {
    case ujson.Str(text) => nonEmpty(text, s"JSON field $name")
    case _               => throw IllegalArgumentException(s"JSON field $name $problem")
  }
  private def stringField(obj: ujson.Obj, name: String): NonEmpty[String] =
    string(field(obj, name), name, "is not a string")
  private def optionalString(obj: ujson.Obj, name: String): Option[NonEmpty[String]] =
    obj.value.get(name).map(string(_, name, "is not a string"))
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

  private def write(path: Path, content: String): Unit = {
    val _ = GeneratedFiles.write(path, GeneratedScalaFormatter(path, content))
  }

  private def readExtendedInstructions(directory: Path): Vector[ExtendedInstructionSet] =
    ExtendedInstructionGrammars.map { case (file, name, objectName) =>
      val root = ujson.read(Files.readString(directory.resolve(file), StandardCharsets.UTF_8)) match {
        case value: ujson.Obj => value
        case _                => throw IllegalArgumentException(s"$file root is not an object")
      }
      val entries = arrayField(root, "instructions").map {
        case obj: ujson.Obj =>
          val opname: String = stringField(obj, "opname")
          val opcode: Int    = opcodeField(obj, "opcode")
          (
            opname,
            opcode,
            optionalStringArray(obj, "capabilities").map(_.raw),
            optionalObjectArray(obj, "operands").map(operand => stringField(operand, "name").raw)
          )
        case _ => throw IllegalArgumentException(s"$file instruction entry is not an object")
      }
      val instructions = entries.map((name, opcode, _, _) => name -> opcode)
      require(
        instructions.map(_._1).distinct.size == instructions.size,
        s"$file has duplicate extended instruction names"
      )
      require(
        instructions.map(_._2).distinct.size == instructions.size,
        s"$file has duplicate extended instruction opcodes"
      )
      ExtendedInstructionSet(
        name,
        objectName,
        instructions,
        entries.map((name, _, capabilities, operands) => (name, capabilities, operands))
      )
    }

  private def renderExtendedInstructions(sets: Vector[ExtendedInstructionSet]): String = {
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
    val grammar = sets.flatMap(set =>
      set.grammar.map((name, capabilities, operands) =>
        (Vector(set.name, name, capabilities.mkString(",")) ++ operands).mkString("\t")
      )
    )
    GeneratedHeader.scala("from the pinned Khronos SPIR-V extended instruction grammars") +
      s"""|package net.kurobako.gemstone.isa.spirv
          |
          |import net.kurobako.gemstone.collection.*
          |import net.kurobako.gemstone.collection.Vec.*
          |
          |${GeneratedHeader.annotation()}object ExtendedInstruction {
          |  val names: Map[String, Map[String, Int]] = Map(
          |$names
          |  )
          |
          |  /** The capabilities any one of which enables an instruction, and the grammar's names of its operands after its
          |    * set and opcode, by set and instruction.
          |    */
          |  lazy val grammar: Map[String, Map[String, (capabilities: Vec[String], operands: Vec[String])]] =
          |    ${table(grammar)}.map(_.split('\\t').toVec).groupBy(_.head).map((set, rows) =>
          |      set -> rows.map(row => row(1) -> (Vec.from(row(2).split(',')).filter(_.nonEmpty), row.drop(3))).toMap
          |    )
          |$objects
          |}
          |""".stripMargin
  }

  /** Tab-separated rows as string constants of bounded size, each row ending in a newline, read back as rows. */
  private def table(rows: Vector[String]): String = {
    val chunks = rows.foldLeft(Vector.empty[String]) { (chunks, row) =>
      if (chunks.nonEmpty && chunks.last.length + row.length < 8000) chunks.init :+ (chunks.last + row + "\n")
      else chunks :+ (row + "\n")
    }
    s"Vec(${chunks.map(Emission.literal(_)).mkString(", ")}).flatMap(_.split('\\n').toVec)"
  }

  private def renderRequirements(grammar: Grammar): String = {
    val rows = grammar.entries.toVector.map(entry =>
      Vector(
        entry.opcode.toString,
        entry.grammarClass,
        entry.version,
        entry.capabilities.map(_.raw).mkString(","),
        entry.extensions.map(_.raw).mkString(",")
      ).mkString("\t")
    )
    GeneratedHeader.scala("from the pinned Khronos SPIR-V core grammar") +
      s"""|package net.kurobako.gemstone.isa.spirv
          |
          |import net.kurobako.gemstone.collection.*
          |import net.kurobako.gemstone.collection.Vec.*
          |
          |${GeneratedHeader.annotation()}object InstructionRequirements {
          |  private def names(field: String): Vec[String] = Vec.from(field.split(',')).filter(_.nonEmpty)
          |
          |  /** By opcode number. */
          |  lazy val all: Map[Int, InstructionRequirements] =
          |    ${table(rows)}.map { row =>
          |      val fields = row.split("\\t", -1)
          |      fields(0).toInt -> InstructionRequirements(fields(1), fields(2), names(fields(3)), names(fields(4)))
          |    }.toMap
          |}
          |
          |/** What the grammar files an instruction under, the version that made it core ("None" for one only an extension
          |  * adds), and the capabilities and extensions any one of which enables it.
          |  */
          |case class InstructionRequirements(
          |    grammarClass: String,
          |    version: String,
          |    capabilities: Vec[String],
          |    extensions: Vec[String]
          |)
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

  private def renderConstants(grammar: Grammar): String = {
    val groups = constantGroups(grammar).filter(_._1 == "Opcode").map { case (kind, values) =>
      val fields = values.map { case (name, value) =>
        val safe = if (ScalaKeywords(name)) s"`$name`" else name
        s"    final val $safe: Int = ${value.toInt}"
      }.mkString("\n")
      s"  object $kind {\n$fields\n  }"
    }.mkString("\n\n")
    GeneratedHeader.scala("from the pinned Khronos SPIR-V core grammar") +
      s"""|package net.kurobako.gemstone.isa.spirv
          |
          |${GeneratedHeader.annotation()}object SPIRV {
          |  final val Magic: Int = ${grammar.magic}
          |$groups
          |}
          |""".stripMargin
  }

  private def renderNativeConstants(grammar: Grammar): String = {
    val groups = constantGroups(grammar).map(Emission.cppConstants(_, _)).mkString("\n\n")
    GeneratedHeader.cpp("from the pinned Khronos SPIR-V core grammar") +
      s"#pragma once\n\n#include <cstdint>\n\nnamespace gemstone::spirv {\ninline constexpr uint32_t Magic = ${grammar.magic}u;\n$groups\n} // namespace gemstone::spirv\n"
  }

  private def renderEnumerants(kind: OperandKind): String = {
    val raw        = kind.kind
    val objectName = if (ScalaKeywords(raw)) s"`$raw`" else raw
    val values     = kind.enumerants.map { enumerant =>
      val name: String = enumerant.name
      val safe         = Identifiers.upperCamel(name, "Value", "Value")
      s"    final val $safe: Int = ${enumerant.value.toInt}"
    }.mkString("\n")
    GeneratedHeader.scala("from the pinned Khronos SPIR-V core grammar") +
      s"""|package net.kurobako.gemstone.isa.spirv
          |
          |${GeneratedHeader.annotation()}object $objectName {
          |$values
          |}
          |""".stripMargin
  }

  private def renderOperandConstraints(grammar: Grammar): String = {
    val entries         = grammar.entries.toVector.map(entry => entry.name.raw -> entry).toMap
    val kindNames       = grammar.kinds.toVector.map(_.kind.raw).toSet
    val parametricKinds = grammar.kinds.collect {
      case value if value.enumerants.exists(_.parameters.nonEmpty) => value.kind.raw
    }.toSet
    val kinds = SPIRVOperandConstraints.kinds.collect {
      case (kind, constraint, source) if kindNames(kind) =>
        s"KindRule(OperandKind.$kind, ${renderConstraint(constraint)}, ${Emission.literal(source)})"
    }
    val families = SPIRVOperandConstraints.families.map { (family, rules) =>
      val rows =
        rules.flatMap(rule => entries.get(rule.opcode).map(entry => renderOperandRule(parametricKinds, entry, rule)))
      if (rows.isEmpty) s"  private def $family: Vec[OperandRule] = Vec.empty[OperandRule]"
      else s"  private def $family: Vec[OperandRule] = Vec(\n${rows.map(row => s"    $row").mkString(",\n")}\n  )"
    }
    val rules = SPIRVOperandConstraints.families.map(_._1).mkString(" ++ ")
    GeneratedHeader.scala("from the pinned Khronos SPIR-V core grammar") +
      s"""|package net.kurobako.gemstone.isa.spirv
        |
        |import net.kurobako.gemstone.collection.*
        |
        |${GeneratedHeader.annotation()}object OperandConstraintTable {
        |  val kinds: Vec[KindRule] = ${Emission.collection("Vec", kinds)}
        |
        |  val rules: Vec[OperandRule] = $rules
        |
        |${families.mkString("\n\n")}
        |}
        |""".stripMargin
  }

  /** The row `OperandConstraintTable` holds for `rule`. */
  private def renderOperandRule(
      parametricKinds: Set[String],
      entry: Entry,
      rule: SPIRVOperandConstraints.Rule
  ): String = {
    val positions = entry.operands.zipWithIndex.collect {
      case (operand, index) if operand.name.fold(operand.kind.raw)(_.raw) == rule.operand => index
    }
    require(positions.size == 1, s"${rule.opcode} has ${positions.size} operands named ${rule.operand}")
    val index   = positions.head
    val operand = entry.operands(index)
    require(operand.quantifier.forall(_.raw == "*"), s"${rule.opcode} ${rule.operand} is optional")
    val prior = entry.operands.take(index)
    require(
      prior.forall(earlier => earlier.quantifier.isEmpty && !parametricKinds(earlier.kind.raw)),
      s"${rule.opcode} ${rule.operand} follows an optional, repeated or parametric operand"
    )
    val quantifier = if (operand.quantifier.isDefined) "Repeated" else "One"
    s"OperandRule(${Emission.literal(rule.opcode)}, $index, ${Emission.literal(rule.operand)}, " +
      s"OperandQuantifier.$quantifier, ${renderConstraint(rule.constraint)}, ${Emission.literal(rule.source)})"
  }

  private def renderConstraint(constraint: SPIRVOperandConstraints.Constraint): String = constraint match {
    case SPIRVOperandConstraints.Constraint.Type              => "OperandConstraint.Type"
    case SPIRVOperandConstraints.Constraint.Declares(opcodes) =>
      s"OperandConstraint.Declares(${Emission.collection("Vec", opcodes.map(Emission.literal))})"
    case SPIRVOperandConstraints.Constraint.Value(kind) =>
      s"OperandConstraint.Value(OperandConstraint.ValueClass.$kind)"
    case SPIRVOperandConstraints.Constraint.ScopeId           => "OperandConstraint.ScopeId"
    case SPIRVOperandConstraints.Constraint.MemorySemanticsId => "OperandConstraint.MemorySemanticsId"
    case SPIRVOperandConstraints.Constraint.ScalarComponent   => "OperandConstraint.ScalarComponent"
    case SPIRVOperandConstraints.Constraint.ByteWidth         => "OperandConstraint.ByteWidth"
    case SPIRVOperandConstraints.Constraint.OneOf(values)     =>
      s"OperandConstraint.OneOf(Vec(${values.map(value => s"${value}L").mkString(", ")}))"
  }

  private def syncRoot(root: Path, outputs: Map[Path, String]): Unit = {
    outputs.foreach { case (path, content) => write(path, content) }
    val include = (path: Path) => path.getFileName.toString.endsWith(".scala")
    GeneratedFiles.removeUnexpected(root, outputs.keySet, include)
  }

}

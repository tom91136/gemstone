package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** Builds the normalized AMDGPU instruction catalog from pinned AMD XML and Mesa ACO sources. */
object GenerateAMDGPUISA {
  private final case class Request(
      python: String,
      references: Path,
      sourceRoot: Path,
      resourceRoot: Path
  )
  private val ACOExporter = """import importlib.util
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
spec = importlib.util.spec_from_file_location("gemstone_pinned_aco_opcodes", path)
if spec is None or spec.loader is None:
    raise RuntimeError("cannot load pinned ACO opcode source")
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)

def operand(value, is_input, is_output):
    kind = value.base_type.name.removeprefix("aco_base_type_")
    return ",".join((kind, str(value.bit_size), str(value.num_components),
                     "1" if is_input else "0", "1" if is_output else "0",
                     "1" if value.fixed_reg.name != "not_fixed" else "0"))

for name in sorted(module.instructions):
    instruction = module.instructions[name]
    if instruction.format.name.startswith("PSEUDO"):
        continue
    operands = [operand(value, False, True) for value in instruction.definitions]
    operands.extend(operand(value, True, False) for value in instruction.operands)
    for generation in ("gfx6", "gfx7", "gfx8", "gfx9"):
        opcode = getattr(instruction.op, generation)
        if opcode >= 0:
            print("\t".join((generation.upper(), name, instruction.format.name, str(opcode),
                              instruction.cls.value, ";".join(operands), "mesa-aco")))
"""
  final case class Operand(
      field: String,
      kind: String,
      input: Boolean,
      output: Boolean,
      implicitOperand: Boolean
  )
  final case class Instruction(
      name: String,
      generation: String,
      target: String,
      format: String,
      opcode: Int,
      operands: Vector[Operand],
      source: String
  )
  private final case class SOPPRule(prefix: Long, opcodeShift: Int, immediateBits: Int)
  private final case class Extracted(records: Vector[Instruction], acoCount: Int, xmlCount: Int, sopp: SOPPRule)
  private final case class Field(name: String)
  private final case class InstructionForm(
      opcode: String,
      definitionName: String,
      caseName: String,
      fields: Vector[Field],
      operandCount: Int
  )
  private final case class ComparisonClassification(kind: String, relation: String, updateExec: Boolean)
  private enum DefinitionDomain(val directory: String, val stem: String) {
    case SOP1          extends DefinitionDomain("scalar", "SOP1")
    case SOP2          extends DefinitionDomain("scalar", "SOP2")
    case SOPC          extends DefinitionDomain("scalar", "SOPC")
    case SOPK          extends DefinitionDomain("scalar", "SOPK")
    case SOPP          extends DefinitionDomain("scalar", "SOPP")
    case ScalarMemory  extends DefinitionDomain("scalar", "ScalarMemory")
    case VOP1          extends DefinitionDomain("vector", "VOP1")
    case VOP2          extends DefinitionDomain("vector", "VOP2")
    case VOP3          extends DefinitionDomain("vector", "VOP3")
    case PackedVector  extends DefinitionDomain("vector", "PackedVector")
    case VectorCompare extends DefinitionDomain("vector", "VectorCompare")
    case DualVector    extends DefinitionDomain("vector", "DualVector")
    case Interpolation extends DefinitionDomain("vector", "Interpolation")
    case Matrix        extends DefinitionDomain("vector", "Matrix")
    case DataShare     extends DefinitionDomain("memory", "DataShare")
    case Buffer        extends DefinitionDomain("memory", "Buffer")
    case Flat          extends DefinitionDomain("memory", "Flat")
    case Image         extends DefinitionDomain("memory", "Image")
    case Export        extends DefinitionDomain("system", "Export")
    case LDSDirect     extends DefinitionDomain("system", "LDSDirect")
  }
  private final case class DefinitionChunk(
      fileName: String,
      objectName: String,
      groups: Vector[Vector[InstructionForm]]
  )
  private final case class InstructionSources(
      main: String,
      metadata: String,
      definitions: Vector[(String, String)]
  )

  private val MaximumDefinitionSourceCharacters = 750000
  private val PackageRoot                       = "net/kurobako/gemstone/isa/amdgpu"

  private val command = Command("GenerateAMDGPUISA", "Generate AMDGPU ISA sources")((
    Opts.option[String]("python", help = "Python executable for the pinned ACO model").withDefault("python3"),
    Opts.argument[String]("prepared-references").map(Path.of(_)),
    Opts.argument[String]("generated-source-root").map(Path.of(_)),
    Opts.argument[String]("generated-resource-root").map(Path.of(_))
  ).mapN(Request(_, _, _, _)))

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) { request =>
    val result          = extract(request.references, request.python)
    val instructionRoot = request.sourceRoot.resolve(PackageRoot)
    val constants       = instructionRoot.resolve("EncodingConstants.scala")
    write(constants, render(result.records, result.acoCount, result.xmlCount, result.sopp))
    val instructions = renderInstructions(result.records)
    write(instructionRoot.resolve("Instruction.scala"), instructions.main)
    val definitionsRoot = instructionRoot.resolve("definitions")
    val definitions     = instructions.definitions.map { case (name, content) =>
      definitionsRoot.resolve(name) -> content
    }
    definitions.foreach { case (path, content) => write(path, content) }
    val architectures = instructionRoot.resolve("Architectures.scala")
    write(architectures, renderArchitectures())
    val metadataResource = request.resourceRoot.resolve(PackageRoot).resolve("metadata.json")
    val _                = GeneratedFiles.write(metadataResource, instructions.metadata)
    val expected         = definitions.map(_._1).toSet ++
      Set(constants, instructionRoot.resolve("Instruction.scala"), architectures)
    val include = (path: Path) => path.getFileName.toString.endsWith(".scala")
    GeneratedFiles.removeUnexpected(request.sourceRoot, expected, include)
    GeneratedFiles.removeUnexpected(request.resourceRoot, Set(metadataResource), _ => true)
  }

  def generate(references: Path): String = {
    val result = extract(references)
    render(result.records, result.acoCount, result.xmlCount, result.sopp)
  }

  private def extract(references: Path, python: String = "python3"): Extracted = {
    if (!Files.isRegularFile(references.resolve("MANIFEST.tsv")))
      throw IllegalArgumentException(s"not a prepared ISA reference directory: $references")
    val xmlRoot  = references.resolve("amd/xml")
    val xmlFiles = Using.resource(Files.list(xmlRoot)) { stream =>
      stream.toList.asScala.view.filter(path => path.getFileName.toString.endsWith(".xml")).toVector.sortBy(_.toString)
    }
    if (xmlFiles.size != 10)
      throw IllegalStateException(s"expected ten AMD ISA XML files, found ${xmlFiles.size}")

    val official = xmlFiles.flatMap(parseOfficialXML(_))
    val aco      = parseACO(references.resolve("mesa/src/amd/compiler/aco_opcodes.py"), python)
    val sopp     = parseACOSOPPRule(references.resolve("mesa/src/amd/compiler"))
    val records  = (aco ++ official)
      .sortBy(record => (record.name, record.generation, record.target, record.format, record.opcode))
    Extracted(records, aco.size, official.size, sopp)
  }

  private def parseOfficialXML(input: Path): Vector[Instruction] = {
    val fileName   = input.getFileName.toString
    val generation = generationFor(fileName)
    val target     = fileName.stripPrefix("amdgpu_isa_").stripSuffix(".xml")
    val factory    = DocumentBuilderFactory.newInstance()
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "")
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "")
    factory.setXIncludeAware(false)
    factory.setExpandEntityReferences(false)
    factory.setNamespaceAware(false)
    val document = factory.newDocumentBuilder().parse(input.toFile)
    val nodes    = document.getElementsByTagName("Instruction")
    Vector.tabulate(nodes.getLength) { index =>
      val instruction = nodes.item(index) match {
        case element: Element => element
        case node             => throw IllegalArgumentException(s"Instruction node is not an XML element: $node")
      }
      val name = requiredText(instruction, "InstructionName").toLowerCase(java.util.Locale.ROOT)
      directChildren(requiredDirect(instruction, "InstructionEncodings"), "InstructionEncoding").map { encoding =>
        val format   = requiredText(encoding, "EncodingName").stripPrefix("ENC_")
        val opcode   = parseRadix(requiredDirect(encoding, "Opcode"))
        val operands = optionalDirect(encoding, "Operands").toVector
          .flatMap(value => directChildren(value, "Operand"))
          .map { operand =>
            Operand(
              field = optionalText(operand, "FieldName").getOrElse(""),
              kind = requiredText(operand, "OperandType").stripPrefix("OPR_"),
              input = booleanAttribute(operand, "Input"),
              output = booleanAttribute(operand, "Output"),
              implicitOperand = booleanAttribute(operand, "IsImplicit")
            )
          }
        Instruction(name, generation, target, format, opcode, operands, "AMDXML")
      }
    }.flatten
  }

  private def parseACO(source: Path, python: String): Vector[Instruction] = {
    if (!Files.isRegularFile(source)) throw IllegalArgumentException(s"missing Mesa ACO opcode source: $source")
    val process = ProcessBuilder(python, "-B", "-c", ACOExporter, source.toString)
      .redirectError(ProcessBuilder.Redirect.INHERIT)
      .start()
    val rows = Using.resource(scala.io.Source.fromInputStream(process.getInputStream, "UTF-8"))(_.getLines().toVector)
    val exit = process.waitFor()
    if (exit != 0) throw IllegalStateException(s"ACO exporter failed with exit code $exit")
    rows.map { row =>
      val columns = Text.splitDelimited(row, '\t')
      if (columns.size != 7) throw IllegalArgumentException(s"invalid ACO exporter row: $row")
      val operands =
        if (columns(5).isEmpty) Vector.empty
        else
          Text.splitDelimited(columns(5), ';').map { encoded =>
            val fields = Text.splitDelimited(encoded, ',')
            if (fields.size != 6) throw IllegalArgumentException(s"invalid ACO operand: $encoded")
            Operand("", fields(0), fields(3) == "1", fields(4) == "1", fields(5) == "1")
          }
      Instruction(
        columns(1),
        columns(0),
        columns(0).toLowerCase(java.util.Locale.ROOT),
        columns(2),
        columns(3).toInt,
        operands,
        "MesaACO"
      )
    }
  }

  private def parseACOSOPPRule(root: Path): SOPPRule = {
    val assembler = Files.readString(root.resolve("aco_assembler.cpp"), StandardCharsets.UTF_8)
    val ir        = Files.readString(root.resolve("aco_ir.h"), StandardCharsets.UTF_8)
    val SOPPBody  = raw"(?s)emit_sopp_instruction\(.*?\n\}".r
    val Prefix    = raw"encoding\s*=\s*\(0b([01]+)\s*<<\s*([0-9]+)\)".r
    val Opcode    = raw"encoding\s*\|=\s*opcode\s*<<\s*([0-9]+)".r
    val Immediate = raw"encoding\s*\|=\s*\(uint([0-9]+)_t\)sopp\.imm".r
    val body      = SOPPBody.findFirstIn(assembler).getOrElse {
      throw IllegalArgumentException("Mesa ACO assembler no longer contains emit_sopp_instruction")
    }
    val (prefixBits, prefixShift) = Prefix.findFirstMatchIn(body)
      .map(value => value.group(1) -> value.group(2).toInt)
      .getOrElse(throw IllegalArgumentException("unrecognized Mesa ACO SOPP prefix rule"))
    val opcodeShift = Opcode.findFirstMatchIn(body).map(_.group(1).toInt)
      .getOrElse(throw IllegalArgumentException("unrecognized Mesa ACO SOPP opcode rule"))
    val immediateBits = Immediate.findFirstMatchIn(body).map(_.group(1).toInt)
      .getOrElse(throw IllegalArgumentException("unrecognized Mesa ACO SOPP immediate rule"))
    val salu = ir.indexOf("struct SALU_instruction")
    val imm  = ir.indexOf("uint32_t imm;", salu.max(0))
    if (salu < 0 || imm <= salu)
      throw IllegalStateException("Mesa ACO IR no longer has the SALU immediate field")
    SOPPRule(java.lang.Long.parseLong(prefixBits, 2) << prefixShift, opcodeShift, immediateBits)
  }

  private def render(records: Vector[Instruction], acoCount: Int, xmlCount: Int, sopp: SOPPRule): String =
    GeneratedHeader.scala("encoding constants from pinned Mesa ACO inputs") +
      "// Instruction metadata is attached directly to generated Instruction.Definition cases.\n" +
      s"""|package net.kurobako.gemstone.isa.amdgpu
          |
          |${GeneratedHeader.annotation()}private[amdgpu] object EncodingConstants {
        |  val MesaACORecordCount: Int = $acoCount
        |  val AMDXMLRecordCount: Int = $xmlCount
        |  val MetadataRecordCount: Int = ${records.size}
        |  val SOPPEncodingPrefix: Long = ${sopp.prefix}L
        |  val SOPPOpcodeShift: Int = ${sopp.opcodeShift}
        |  val SOPPImmediateBits: Int = ${sopp.immediateBits}
        |}
        |""".stripMargin

  private def renderInstructions(records: Vector[Instruction]): InstructionSources = {
    val recordsByOpcode    = records.groupBy(_.name)
    val metadataEntries    = recordsByOpcode.toVector.sortBy(_._1).map { case (opcode, entries) => opcode -> entries }
    val distinctMetadata   = metadataEntries.map(_._2).distinct
    val metadataIds        = distinctMetadata.zipWithIndex.toMap
    val metadataIdByOpcode = metadataEntries.map { case (opcode, metadata) =>
      opcode -> metadataIds(metadata)
    }.toMap
    val metadata         = renderMetadataJson(distinctMetadata)
    val forms            = instructionForms(records)
    val definitionGroups = forms
      .groupBy(_.opcode)
      .toVector
      .sortBy(_._1)
      .map { case (_, opcodeForms) => opcodeForms.sortBy(_.operandCount) }
    val definitionChunks = DefinitionDomain.values.toVector.flatMap { domain =>
      val domainGroups =
        definitionGroups.filter(group => definitionDomain(group.head.opcode, recordsByOpcode) == domain)
      val shards = weightedChunks(domainGroups)(group =>
        renderDefinitionCase(group, metadataIdByOpcode(group.head.opcode)).length
      )
      shards.map { groups =>
        val range      = Option.when(shards.size > 1)(definitionRange(domain, groups)).getOrElse("")
        val name       = s"${domain.stem}$range"
        val objectName = s"${name}InstructionDefs"
        DefinitionChunk(s"${domain.directory}/$objectName.scala", objectName, groups)
      }
    }
    if (definitionChunks.flatMap(_.groups).size != definitionGroups.size)
      throw IllegalStateException("definition-domain partition lost an AMDGPU opcode")
    if (
      definitionChunks.map(_.fileName).distinct.size != definitionChunks.size ||
      definitionChunks.map(_.objectName).distinct.size != definitionChunks.size
    ) throw IllegalStateException("definition-domain partition produced a duplicate source or object name")
    val definitionReferences = definitionChunks.flatMap { chunk =>
      chunk.groups.map(forms => forms.head.opcode -> s"${chunk.objectName}.${forms.head.definitionName}")
    }.toMap
    val globalDefinitionReferences = definitionReferences.toVector.collect {
      case (opcode, reference) if opcode.startsWith("global_") => reference
    }.sorted
    val flatDefinitionReferences = definitionReferences.toVector.collect {
      case (opcode, reference) if opcode.startsWith("flat_") => reference
    }.sorted
    val comparisonEntries = definitionReferences.toVector.flatMap { case (opcode, reference) =>
      comparisonClassification(opcode).map(classification => reference -> classification)
    }.sortBy(_._1)
    val renderedComparisons = comparisonEntries.map { case (reference, classification) =>
      s"    $reference -> ComparisonOpcode(ComparisonKind.${classification.kind}, ComparisonRelation.${classification.relation}, ${classification.updateExec})"
    }.mkString(",\n")
    val definitionSources = definitionChunks.map { chunk =>
      chunk.fileName -> renderDefinitionChunkFile(chunk.objectName, chunk.groups, metadataIdByOpcode)
    }
    val definitionChunkNames = definitionChunks.map(chunk => s"${chunk.objectName}.definitions").mkString(", ")
    val instructionCases     = forms.map(form => renderInstructionCase(form, definitionReferences(form.opcode)))
    val main = GeneratedHeader.scala("from the pinned Mesa ACO and AMD machine-readable ISA inputs", assume = true) +
      s"""|package net.kurobako.gemstone.isa.amdgpu
        |
        |import net.kurobako.gemstone.Vec
        |import net.kurobako.gemstone.Vec.*
        |import Program.*
        |
        |${GeneratedHeader.annotation()}enum Instruction(val definition: Instruction.Definition, operandValues: Operand*) {
        |${instructionCases.mkString("\n")}
        |
        |  def mods: Vec[Modifier]
        |  final val opcode: Opcode = definition.opcode
        |  final val meta: Metadata = definition.meta
        |  final val operands: Vec[Operand] = operandValues.toVec
        |}
        |
        |${GeneratedHeader.annotation()}object Instruction {
        |  enum DecodeError {
        |    case UnknownOpcode(opcode: String)
        |    case InvalidOperandCount(opcode: String, expected: Vec[Int], actual: Int)
        |    case AmbiguousForms(opcode: String, definitions: Vec[Definition])
        |  }
        |
        |  private[amdgpu] final case class FormDecoder(
        |      val operandCount: Int,
        |      construct: (Vec[Operand], Vec[Modifier]) => Instruction
        |  ) {
        |    def decode(operands: Vec[Operand], mods: Vec[Modifier]): Option[Instruction] =
        |      Option.when(operands.size == operandCount)(construct(operands, mods))
        |  }
        |
        |  private[amdgpu] final class Decoder(forms: Vec[FormDecoder]) {
        |    val operandCounts: Vec[Int] = forms.map(_.operandCount).distinct.sorted
        |
        |    def apply(operands: Vec[Operand], mods: Vec[Modifier]): Vec[Instruction] =
        |      forms.flatMap(_.decode(operands, mods))
        |  }
        |
        |  private[amdgpu] def form(
        |      operandCount: Int
        |  )(construct: (Vec[Operand], Vec[Modifier]) => Instruction): FormDecoder =
        |    FormDecoder(operandCount, construct)
        |  private[amdgpu] def decoder(forms: FormDecoder*): Decoder = Decoder(forms.toVec)
        |
        |  private[amdgpu] enum ComparisonKind {
        |    case Signed16, Signed32, Signed64, Unsigned16, Unsigned32, Unsigned64
        |    case Float16, Float32, Float64, ClassFloat32, ClassFloat64
        |  }
        |
        |  private[amdgpu] enum ComparisonRelation {
        |    case Equal, NotEqual, Less, LessOrEqual, Greater, GreaterOrEqual
        |    case NotLess, NotLessOrEqual, NotGreater, NotGreaterOrEqual
        |    case Ordered, Unordered, NotLessOrGreater, Class
        |  }
        |
        |  private[amdgpu] final case class ComparisonOpcode(
        |      kind: ComparisonKind,
        |      relation: ComparisonRelation,
        |      updateExec: Boolean
        |  )
        |
        |  abstract class Definition private[amdgpu] (
        |      val opcode: Opcode,
        |      private[amdgpu] val decoder: Decoder,
        |      private val metadataId: Int
        |  ) {
        |    final lazy val meta: Metadata = MetadataCatalog(metadataId)
        |
        |    final def decode(operands: Vec[Operand], mods: Vec[Modifier]): Vec[Instruction] =
        |      decoder(operands, mods)
        |
        |    override final def toString: String = opcode.value
        |  }
        |
        |  private val AllDefinitions: Vec[Definition] = Vec($definitionChunkNames).flatten
        |  private[amdgpu] val GlobalDefinitions: Set[Definition] = Set(${globalDefinitionReferences.mkString(", ")})
        |  private[amdgpu] val FlatDefinitions: Set[Definition] = Set(${flatDefinitionReferences.mkString(", ")})
        |  private[amdgpu] val ComparisonOpcodes: Map[Definition, ComparisonOpcode] = Map(
        |$renderedComparisons
        |  )
        |
        |  object Definition {
        |    private lazy val ByOpcode: Map[String, Vec[Definition]] =
        |      AllDefinitions.groupBy(_.opcode.value)
        |
        |    def forOpcode(opcode: String): Vec[Definition] =
        |      ByOpcode.getOrElse(Opcode.normalize(opcode), Vec.empty[Definition])
        |  }
        |
        |  val definitions: Vec[Definition] = AllDefinitions
        |
        |  def decode(
        |      opcode: String,
        |      operands: Vec[Operand],
        |      mods: Vec[Modifier] = Vec.empty
        |  ): Either[DecodeError, Instruction] = {
        |    val name = Opcode.normalize(opcode)
        |    decodeWithSuffixes(name, operands, mods, name)
        |  }
        |
        |  private val EncodingSuffixes: Vec[String] = Vec("_dpp8", "_sdwa", "_dpp", "_e64", "_e32")
        |
        |  private def decodeWithSuffixes(
        |      name: String,
        |      operands: Vec[Operand],
        |      mods: Vec[Modifier],
        |      original: String
        |  ): Either[DecodeError, Instruction] =
        |    decodeCanonical(name, operands, mods) match {
        |      case Left(DecodeError.UnknownOpcode(_)) =>
        |        EncodingSuffixes.collectFirst {
        |          case suffix if name.endsWith(suffix) =>
        |            name.dropRight(suffix.length) -> Modifier(ModifierName.assume(suffix.drop(1)))
        |        } match {
        |          case Some((canonical, modifier)) => decodeWithSuffixes(canonical, operands, modifier +: mods, original)
        |          case None                        => Left(DecodeError.UnknownOpcode(original))
        |        }
        |      case result => result
        |    }
        |
        |  private def decodeCanonical(
        |      name: String,
        |      operands: Vec[Operand],
        |      mods: Vec[Modifier]
        |  ): Either[DecodeError, Instruction] =
        |    Definition.forOpcode(name) match {
        |      case Vec() => Left(DecodeError.UnknownOpcode(name))
        |      case candidates =>
        |        val matches = candidates.flatMap(_.decode(operands, mods))
        |        matches match {
        |          case Vec(instruction) => Right(instruction)
        |          case Vec() => Left(
        |            DecodeError.InvalidOperandCount(
        |              name, candidates.flatMap(_.decoder.operandCounts).distinct.sorted, operands.size
        |            )
        |          )
        |          case _ => Left(DecodeError.AmbiguousForms(name, matches.map(_.definition)))
        |        }
        |    }
        |}
        |""".stripMargin
    InstructionSources(main, metadata, definitionSources)
  }

  private def renderDefinitionChunkFile(
      objectName: String,
      groups: Vector[Vector[InstructionForm]],
      metadataIdByOpcode: Map[String, Int]
  ): String =
    GeneratedHeader.scala("from the pinned Mesa ACO and AMD machine-readable ISA inputs", assume = true) +
      s"""|package net.kurobako.gemstone.isa.amdgpu
          |
          |import net.kurobako.gemstone.Vec
          |import Instruction.*
          |import Program.*
          |
          |${renderDefinitionChunk(objectName, groups, metadataIdByOpcode)}
        |""".stripMargin

  private def comparisonClassification(opcode: String): Option[ComparisonClassification] = {
    val (updateExec, prefix) =
      if (opcode.startsWith("v_cmpx_")) true -> "v_cmpx_"
      else false                             -> "v_cmp_"
    Option.when(opcode.startsWith(prefix))(opcode.drop(prefix.length)).flatMap { body =>
      val separator = body.lastIndexOf('_')
      Option.when(separator > 0)(body.take(separator) -> body.drop(separator + 1)).flatMap {
        case ("class", "f32")         => Some(ComparisonClassification("ClassFloat32", "Class", updateExec))
        case ("class", "f64")         => Some(ComparisonClassification("ClassFloat64", "Class", updateExec))
        case (relationName, kindName) =>
          val relation = relationName match {
            case "eq"                => Some("Equal")
            case "ne" | "neq" | "lg" => Some("NotEqual")
            case "lt"                => Some("Less")
            case "le"                => Some("LessOrEqual")
            case "gt"                => Some("Greater")
            case "ge"                => Some("GreaterOrEqual")
            case "nlt"               => Some("NotLess")
            case "nle"               => Some("NotLessOrEqual")
            case "ngt"               => Some("NotGreater")
            case "nge"               => Some("NotGreaterOrEqual")
            case "o"                 => Some("Ordered")
            case "u"                 => Some("Unordered")
            case "nlg"               => Some("NotLessOrGreater")
            case _                   => None
          }
          val kind = kindName match {
            case "i16" => Some("Signed16")
            case "i32" => Some("Signed32")
            case "i64" => Some("Signed64")
            case "u16" => Some("Unsigned16")
            case "u32" => Some("Unsigned32")
            case "u64" => Some("Unsigned64")
            case "f16" => Some("Float16")
            case "f32" => Some("Float32")
            case "f64" => Some("Float64")
            case _     => None
          }
          for {
            parsedRelation <- relation
            parsedKind     <- kind
          } yield ComparisonClassification(parsedKind, parsedRelation, updateExec)
      }
    }
  }

  private def definitionDomain(
      opcode: String,
      metadataByOpcode: Map[String, Vector[Instruction]]
  ): DefinitionDomain = {
    val formats                               = metadataByOpcode(opcode).view.map(_.format).toSet
    def hasPrefix(prefixes: String*): Boolean = formats.exists(format => prefixes.exists(format.startsWith(_)))

    if (
      formats.contains("VOP3P_MFMA") ||
      Vector("mfma", "wmma", "swmma", "tensor").exists(opcode.contains(_))
    ) DefinitionDomain.Matrix
    else if (hasPrefix("VOPD")) DefinitionDomain.DualVector
    else if (hasPrefix("VOPC")) DefinitionDomain.VectorCompare
    else if (hasPrefix("VOP1")) DefinitionDomain.VOP1
    else if (hasPrefix("VOP2")) DefinitionDomain.VOP2
    else if (hasPrefix("VOP3P")) DefinitionDomain.PackedVector
    else if (hasPrefix("VOP3")) DefinitionDomain.VOP3
    else if (hasPrefix("VINTERP", "VINTRP")) DefinitionDomain.Interpolation
    else if (hasPrefix("SOP1")) DefinitionDomain.SOP1
    else if (hasPrefix("SOP2")) DefinitionDomain.SOP2
    else if (hasPrefix("SOPC")) DefinitionDomain.SOPC
    else if (hasPrefix("SOPK")) DefinitionDomain.SOPK
    else if (hasPrefix("SOPP")) DefinitionDomain.SOPP
    else if (formats.contains("SMEM")) DefinitionDomain.ScalarMemory
    else if (hasPrefix("DS", "VDS")) DefinitionDomain.DataShare
    else if (hasPrefix("MUBUF", "MTBUF", "VBUFFER")) DefinitionDomain.Buffer
    else if (hasPrefix("FLAT", "GLOBAL", "SCRATCH", "VFLAT", "VGLOBAL", "VSCRATCH")) DefinitionDomain.Flat
    else if (hasPrefix("MIMG", "VIMAGE", "VSAMPLE")) DefinitionDomain.Image
    else if (hasPrefix("EXP", "VEXPORT")) DefinitionDomain.Export
    else if (formats.contains("LDSDIR")) DefinitionDomain.LDSDirect
    else
      throw IllegalArgumentException(
        s"AMDGPU opcode '$opcode' has no definition domain for formats ${formats.toVector.sorted.mkString(", ")}"
      )
  }

  private def weightedChunks[A](values: Vector[A])(weight: A => Int): Vector[Vector[A]] =
    values.foldLeft(Vector.empty[(Int, Vector[A])]) { (chunks, value) =>
      val valueWeight = weight(value)
      chunks.lastOption match {
        case Some((currentWeight, current))
            if current.nonEmpty && currentWeight + valueWeight <= MaximumDefinitionSourceCharacters =>
          chunks.updated(chunks.size - 1, (currentWeight + valueWeight, current :+ value))
        case _ => chunks :+ (valueWeight -> Vector(value))
      }
    }.map(_._2)

  private def definitionRange(
      domain: DefinitionDomain,
      groups: Vector[Vector[InstructionForm]]
  ): String = {
    val first = rangeEndpoint(domain, groups.head.head.definitionName)
    val last  = rangeEndpoint(domain, groups.last.head.definitionName)
    if (first == last) first else s"${first}To$last"
  }

  private def rangeEndpoint(domain: DefinitionDomain, name: String): String = {
    val prefixes = domain match {
      case DefinitionDomain.SOP1 | DefinitionDomain.SOP2 | DefinitionDomain.SOPC | DefinitionDomain.SOPK |
          DefinitionDomain.SOPP => Vector("S")
      case DefinitionDomain.VOP1 | DefinitionDomain.VOP2 | DefinitionDomain.VOP3 |
          DefinitionDomain.PackedVector | DefinitionDomain.DualVector | DefinitionDomain.Interpolation |
          DefinitionDomain.Matrix => Vector("V")
      case DefinitionDomain.VectorCompare => Vector("VCmpx", "VCmp")
      case DefinitionDomain.DataShare     => Vector("Vds", "Ds")
      case DefinitionDomain.Image         => Vector("Image")
      case DefinitionDomain.Export        => Vector("VExport", "Exp")
      case _                              => Vector.empty
    }
    prefixes.collectFirst { case prefix if name.startsWith(prefix) => name.drop(prefix.length) }.getOrElse(name)
  }

  private def instructionForms(records: Vector[Instruction]): Vector[InstructionForm] = {
    val grouped = records.groupBy(_.name).toVector.sortBy(_._1)
    val names   = uniqueCaseNames(grouped.map(_._1))
    grouped.flatMap { case (opcode, definitions) =>
      val rawSignatures = definitions.map(_.operands.filter(!_.implicitOperand)).distinct
      val nonEmpty      = rawSignatures.filter(_.nonEmpty)
      val counts        = (nonEmpty.map(_.size) ++ syntaxOperandCounts(opcode)).distinct.sorted match {
        case Vector() => Vector(0)
        case values   => values
      }
      counts.map { operandCount =>
        val signatures = nonEmpty.filter(_.size == operandCount)
        val fields     = Vector.tabulate(operandCount) { index =>
          val operands = signatures.flatMap(_.lift(index))
          Field(preferredFieldName(opcode, index, operands, operandCount))
        }
        val caseName = if (counts.size == 1) names(opcode) else s"${names(opcode)}_$operandCount"
        InstructionForm(opcode, names(opcode), caseName, uniqueFields(fields), operandCount)
      }
    }
  }

  /** Reviewed assembly-syntax overlay for information absent from ACO's physical opcode model or conditional operands
    * that AMD XML marks as present in every encoding. LLVM remains an independent oracle and is deliberately not read
    * by this production generator.
    */
  private def syntaxOperandCounts(opcode: String): Vector[Int] = opcode match {
    case value if value.startsWith("ds_") && value.contains("_src2_")           => Vector(1)
    case "s_endpgm"                                                             => Vector(0, 1)
    case "image_atomic_rsub"                                                    => Vector(3)
    case value if value.startsWith("buffer_load_")                              => Vector(3, 4)
    case value if value.startsWith("flat_atomic_")                              => Vector(2, 3, 4)
    case value if value.startsWith("global_atomic_")                            => Vector(3, 4)
    case value if value.startsWith("global_load_lds_")                          => Vector(2)
    case value if value.startsWith("global_load_")                              => Vector(2, 3)
    case value if value.startsWith("scratch_load_lds_")                         => Vector(2)
    case value if value.startsWith("scratch_load_")                             => Vector(2, 3)
    case "flat_load_monitor_b128"                                               => Vector(2)
    case "flat_load_monitor_b32" | "flat_load_monitor_b64"                      => Vector(2, 3)
    case "flat_prefetch_b8"                                                     => Vector(1, 2)
    case "v_add_i32" | "v_add_u32" | "v_sub_i32" | "v_sub_u32" | "v_subrev_u32" => Vector(3, 4)
    case value if value.startsWith("v_cmpx_ne_")                                => Vector(2, 3)
    case value if value.startsWith("v_cmpx_t_")                                 => Vector(2, 3)
    case "v_dual_cndmask_b32"                                                   => Vector(3, 4)
    case _                                                                      => Vector.empty
  }

  private def renderDefinitionChunk(
      objectName: String,
      groups: Vector[Vector[InstructionForm]],
      metadataIdByOpcode: Map[String, Int]
  ): String = {
    val cases      = groups.map(forms => renderDefinitionCase(forms, metadataIdByOpcode(forms.head.opcode)))
    val references = groups.map(_.head.definitionName).mkString(", ")
    s"""|${GeneratedHeader.annotation()}private[amdgpu] object $objectName {
        |${cases.mkString("\n")}
        |
        |  val definitions: Vec[Definition] = Vec($references)
        |}""".stripMargin
  }

  private def renderDefinitionCase(forms: Vector[InstructionForm], metadataId: Int): String = {
    val first    = forms.head
    val decoders = forms.map { form =>
      val arguments        = form.fields.indices.map(index => s"operands($index)") :+ "mods"
      val operandParameter = if (form.operandCount == 0) "_" else "operands"
      s"form(${form.operandCount})(($operandParameter, mods) => Instruction.${form.caseName}(${arguments.mkString(", ")}))"
    }
    s"""|  case object ${first.definitionName} extends Definition(
        |    Opcode.assume(${literal(first.opcode)}),
        |    decoder(${decoders.mkString(", ")}),
        |    $metadataId
        |  )""".stripMargin
  }

  def renderMetadataJson(metadata: Vector[Vector[Instruction]]): String = {
    val records = metadata.flatten
    val strings = records.flatMap { record =>
      Vector(record.generation, record.target, record.format, record.source) ++
        record.operands.flatMap(operand => operand.kind +: Option.when(operand.field.nonEmpty)(operand.field).toVector)
    }.distinct.sorted
    val stringIds = strings.zipWithIndex.toMap
    val schemas   = records.map(_.operands).distinct
    val schemaIds = schemas.zipWithIndex.toMap
    val offsets   = metadata.foldLeft(Vector.empty[(Int, Int)]) { (acc, group) =>
      acc :+ (acc.lastOption.fold(0)((start, count) => start + count) -> group.size)
    }
    Vector(
      "{",
      "  \"strings\": [",
      strings.map(value => "    " + ujson.write(ujson.Str(value))).mkString(",\n"),
      "  ],",
      "  \"schemas\": [",
      schemas.map { operands =>
        "    " + operands.map { operand =>
          val field = if (operand.field.nonEmpty) stringIds(operand.field) else -1
          s"[${field},${stringIds(operand.kind)},${operand.implicitOperand}]"
        }.mkString("[", ",", "]")
      }.mkString(",\n"),
      "  ],",
      "  \"encodings\": [",
      records.map { record =>
        val family = stringIds(record.generation)
        val target = stringIds(record.target)
        val format = stringIds(record.format)
        val schema = schemaIds(record.operands)
        val source = stringIds(record.source)
        s"""    {"family":$family,"target":$target,"format":$format,"opcode":${record.opcode},"schema":$schema,"source":$source}"""
      }.mkString(",\n"),
      "  ],",
      "  \"metadata\": [",
      offsets.map { case (start, count) => s"    [$start,$count]" }.mkString(",\n"),
      "  ]",
      "}"
    ).mkString("\n") + "\n"
  }

  private def renderInstructionCase(form: InstructionForm, definitionReference: String): String = {
    val fields          = form.fields.map(field => s"${field.name}: Operand") :+ "mods: Vec[Modifier]"
    val parentArguments = form.fields.map(_.name)
    val parent          =
      if (parentArguments.isEmpty) s"Instruction($definitionReference)"
      else s"Instruction($definitionReference, ${parentArguments.mkString(", ")})"
    s"  case ${form.caseName}(${fields.mkString(", ")}) extends $parent"
  }

  private def preferredFieldName(
      opcode: String,
      index: Int,
      operands: Vector[Operand],
      arity: Int
  ): String = {
    val fields                            = operands.map(_.field.toUpperCase(java.util.Locale.ROOT)).filter(_.nonEmpty)
    val output                            = operands.exists(_.output)
    val input                             = operands.exists(_.input)
    def sourceName(position: Int): String =
      if (arity == 1) "src"
      else Vector("srcA", "srcB", "srcC", "srcD", "srcE").lift(position).getOrElse(s"src${position + 1}")

    fields.collectFirst {
      case "VDST" | "SDST" | "DST"                                                             => "dest"
      case "VDSTX"                                                                             => "destX"
      case "VDSTY"                                                                             => "destY"
      case "SRC0" | "SSRC0" | "VSRC0" | "SRCX0" | "SRCY0" | "SCALE_SRC0"                       => "srcA"
      case "SRC1" | "SSRC1" | "VSRC1" | "VSRCX1" | "VSRCY1" | "SCALE_SRC1"                     => "srcB"
      case "SRC2" | "VSRC2" | "VSRCX2" | "VSRCY2"                                              => "srcC"
      case "SRC3" | "VSRC3"                                                                    => "srcD"
      case "VADDR" | "SADDR" | "ADDR" if opcode.contains("store") || opcode.contains("atomic") => "destAddr"
      case "VADDR" | "SADDR" | "ADDR"                                                          => "srcAddr"
      case "SRSRC" | "RSRC"                                                                    => "resource"
      case "SOFFSET" | "IOFFSET"                                                               => "offset"
      case "SSAMP" | "SAMP"                                                                    => "sampler"
      case "SIMM16" | "LITERAL" | "LITERAL64"             => if (opcode.contains("branch")) "target" else "imm"
      case "TGT"                                          => "target"
      case "SBASE"                                        => "base"
      case "ATTR"                                         => "attr"
      case "VDATA" | "SDATA" | "DATA" if output && !input => "dest"
      case "VDATA" | "SDATA" | "DATA" if input && !output => "src"
      case "VDATA" | "SDATA" | "DATA"                     => "data"
      case "DATA0"                                        => "srcA"
      case "DATA1"                                        => "srcB"
    }.getOrElse {
      if (output && !input) "dest"
      else if (input) sourceName(index)
      else if (arity == 1) "value"
      else s"value${index + 1}"
    }
  }

  private def uniqueFields(fields: Vector[Field]): Vector[Field] =
    fields.foldLeft((Map.empty[String, Int], Vector.empty[Field])) { case ((seen, result), field) =>
      val count = seen.getOrElse(field.name, 0) + 1
      (
        seen.updated(field.name, count),
        result :+ field.copy(name = if (count == 1) field.name else s"${field.name}$count")
      )
    }._2

  private def uniqueCaseNames(opcodes: Vector[String]): Map[String, String] = {
    val bases      = opcodes.map(opcode => opcode -> scalaCaseName(opcode))
    val collisions = bases.groupMap { case (_, name) => name.toLowerCase(java.util.Locale.ROOT) }(_._1)
    bases.map { case (opcode, base) =>
      val alternatives = collisions(base.toLowerCase(java.util.Locale.ROOT)).sorted
      opcode -> (if (alternatives.size == 1) base else s"${base}_${alternatives.indexOf(opcode) + 1}")
    }.toMap
  }

  private def scalaCaseName(opcode: String): String = {
    val words = Text.identifierWords(opcode)
    val value = words.map(word => s"${word.head.toUpper}${word.tail}").mkString
    if (value.isEmpty) "Opcode" else if (value.head.isDigit) s"Opcode$value" else value
  }

  private def renderArchitectures(): String = {
    val families = Vector(
      "gfx6"  -> "GFX6",
      "gfx7"  -> "GFX7",
      "gfx8"  -> "GFX8",
      "gfx9"  -> "GFX9",
      "gfx10" -> "GFX10",
      "gfx11" -> "GFX11",
      "gfx12" -> "GFX12",
      "cdna1" -> "CDNA1",
      "cdna2" -> "CDNA2",
      "cdna3" -> "CDNA3",
      "cdna4" -> "CDNA4",
      "cdna5" -> "CDNA5"
    )
    val entries = families.map { case (name, generation) =>
      s"  val $name: ISA = ISA(Family.$generation)"
    }
    entries.mkString(
      GeneratedHeader.scala("from the normalized AMDGPU generation set") +
        "package net.kurobako.gemstone.isa.amdgpu\n\n" + GeneratedHeader.annotation() + "object Architectures {\n",
      "\n",
      "\n}\n"
    )
  }

  private def literal(value: String): String = {
    val escaped = value.flatMap {
      case '"'  => "\\\""
      case '\\' => "\\\\"
      case '\n' => "\\n"
      case '\r' => "\\r"
      case '\t' => "\\t"
      case char => char.toString
    }
    s"\"$escaped\""
  }

  private def generationFor(fileName: String): String = fileName match {
    case "amdgpu_isa_rdna1.xml"   => "GFX10"
    case "amdgpu_isa_rdna2.xml"   => "GFX10"
    case "amdgpu_isa_rdna3.xml"   => "GFX11"
    case "amdgpu_isa_rdna3_5.xml" => "GFX11"
    case "amdgpu_isa_rdna4.xml"   => "GFX12"
    case "amdgpu_isa_cdna1.xml"   => "CDNA1"
    case "amdgpu_isa_cdna2.xml"   => "CDNA2"
    case "amdgpu_isa_cdna3.xml"   => "CDNA3"
    case "amdgpu_isa_cdna4.xml"   => "CDNA4"
    case "amdgpu_isa_cdna5.xml"   => "CDNA5"
    case other                    => throw IllegalArgumentException(s"unknown AMD ISA XML: $other")
  }

  private def parseRadix(element: Element): Int = {
    val radix = Option(element.getAttribute("Radix")).filter(_.nonEmpty).map(_.toInt).getOrElse(10)
    Integer.parseInt(element.getTextContent.trim, radix)
  }

  private def booleanAttribute(element: Element, name: String): Boolean =
    element.getAttribute(name).toLowerCase(java.util.Locale.ROOT) match {
      case "true"  => true
      case "false" => false
      case other   => throw IllegalArgumentException(s"invalid $name '$other'")
    }

  private def requiredText(element: Element, name: String): String =
    optionalText(element, name).getOrElse(throw IllegalArgumentException(s"missing <$name>"))

  private def optionalText(element: Element, name: String): Option[String] = {
    val nodes = element.getElementsByTagName(name)
    if (nodes.getLength == 0) None else Some(nodes.item(0).getTextContent.trim)
  }

  private def requiredDirect(element: Element, name: String): Element =
    optionalDirect(element, name).getOrElse(throw IllegalArgumentException(s"missing direct <$name>"))

  private def optionalDirect(element: Element, name: String): Option[Element] = directChildren(element, name).headOption

  private def directChildren(element: Element, name: String): Vector[Element] = {
    val children = element.getChildNodes
    Vector.tabulate(children.getLength)(children.item(_)).collect {
      case child: Element if child.getTagName == name => child
    }
  }

  private def write(output: Path, content: String): Unit = {
    val _ = GeneratedFiles.write(output, GeneratedScalaFormatter(output, content))
  }

}

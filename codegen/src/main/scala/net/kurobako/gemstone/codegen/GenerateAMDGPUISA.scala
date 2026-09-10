package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import scala.jdk.CollectionConverters.*

/** Builds the normalized AMDGPU instruction catalog from pinned AMD XML and Mesa ACO sources. */
object GenerateAMDGPUISA {
  private final case class Operand(
      field: String,
      kind: String,
      bits: Int,
      input: Boolean,
      output: Boolean,
      implicitOperand: Boolean
  )
  private final case class Instruction(
      name: String,
      generation: String,
      target: String,
      format: String,
      opcode: Int,
      operands: Vector[Operand],
      group: String,
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
  private final case class InstructionSources(main: String, definitions: Vector[(String, String)])

  private val MaximumDefinitionSourceCharacters = 750000

  def main(arguments: Array[String]): Unit = {
    val (python, positional) = arguments.toVector match {
      case Vector("--python", executable, rest*) => (executable, rest.toVector)
      case other                                 => ("python3", other)
    }
    positional match {
      case Vector(references, exporter, output) =>
        val result  = extract(Paths.get(references), Paths.get(exporter), python)
        val catalog = Paths.get(output)
        write(catalog, render(result.records, result.acoCount, result.xmlCount, result.sopp))
        val instructions    = renderInstructions(result.records)
        val instructionRoot = catalog.getParent
        write(instructionRoot.resolve("Instruction.scala"), instructions.main)
        val definitionsRoot = instructionRoot.resolve("definitions")
        deleteScalaFiles(definitionsRoot)
        instructions.definitions.foreach { case (name, content) => write(definitionsRoot.resolve(name), content) }
        write(instructionRoot.resolve("Architectures.scala"), renderArchitectures())
      case _ =>
        throw IllegalArgumentException(
          "usage: GenerateAMDGPUISA [--python <executable>] <prepared-references> <aco-exporter.py> <output.scala>"
        )
    }

  }

  private[codegen] def generate(references: Path, exporter: Path): String = {
    val result = extract(references, exporter)
    render(result.records, result.acoCount, result.xmlCount, result.sopp)
  }

  private def extract(references: Path, exporter: Path, python: String = "python3"): Extracted = {
    require(
      Files.isRegularFile(references.resolve("MANIFEST.tsv")),
      s"not a prepared ISA reference directory: $references"
    )
    val xmlRoot  = references.resolve("amd/xml")
    val stream   = Files.list(xmlRoot)
    val xmlFiles =
      try
        stream.iterator().asScala.filter(path => path.getFileName.toString.endsWith(".xml")).toVector.sortBy(_.toString)
      finally stream.close()
    require(xmlFiles.size == 10, s"expected ten AMD ISA XML files, found ${xmlFiles.size}")

    val official = xmlFiles.flatMap(parseOfficialXML)
    val aco      = parseACO(references.resolve("mesa/src/amd/compiler/aco_opcodes.py"), exporter, python)
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
      val instruction = nodes.item(index).asInstanceOf[Element]
      val name        = requiredText(instruction, "InstructionName").toLowerCase
      val group       = optionalDirectText(instruction, "FunctionalGroup", "Name").getOrElse("")
      directChildren(requiredDirect(instruction, "InstructionEncodings"), "InstructionEncoding").map { encoding =>
        val format   = requiredText(encoding, "EncodingName").stripPrefix("ENC_")
        val opcode   = parseRadix(requiredDirect(encoding, "Opcode"))
        val operands = optionalDirect(encoding, "Operands").toVector
          .flatMap(value => directChildren(value, "Operand"))
          .map { operand =>
            Operand(
              field = optionalText(operand, "FieldName").getOrElse(""),
              kind = requiredText(operand, "OperandType").stripPrefix("OPR_"),
              bits = requiredText(operand, "OperandSize").toInt,
              input = booleanAttribute(operand, "Input"),
              output = booleanAttribute(operand, "Output"),
              implicitOperand = booleanAttribute(operand, "IsImplicit")
            )
          }
        Instruction(name, generation, target, format, opcode, operands, group, "AMDXML")
      }
    }.flatten
  }

  private def parseACO(source: Path, exporter: Path, python: String): Vector[Instruction] = {
    require(Files.isRegularFile(source), s"missing Mesa ACO opcode source: $source")
    require(Files.isRegularFile(exporter), s"missing ACO exporter: $exporter")
    val process = ProcessBuilder(python, "-B", exporter.toString, source.toString)
      .redirectError(ProcessBuilder.Redirect.INHERIT)
      .start()
    val lines = scala.io.Source.fromInputStream(process.getInputStream, "UTF-8")
    val rows  =
      try lines.getLines().toVector
      finally lines.close()
    val exit = process.waitFor()
    require(exit == 0, s"ACO exporter failed with exit code $exit")
    rows.map { row =>
      val columns = row.split("\\t", -1).toVector
      require(columns.size == 7, s"invalid ACO exporter row: $row")
      val operands =
        if (columns(5).isEmpty) Vector.empty
        else
          columns(5).split(';').toVector.map { encoded =>
            val fields = encoded.split(",", -1)
            require(fields.length == 6, s"invalid ACO operand: $encoded")
            Operand(
              "",
              fields(0),
              fields(1).toInt * fields(2).toInt,
              fields(3) == "1",
              fields(4) == "1",
              fields(5) == "1"
            )
          }
      Instruction(
        columns(1),
        columns(0),
        columns(0).toLowerCase,
        columns(2),
        columns(3).toInt,
        operands,
        columns(4),
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
    require(salu >= 0 && imm > salu, "Mesa ACO IR no longer has the SALU immediate field")
    SOPPRule(java.lang.Long.parseLong(prefixBits, 2) << prefixShift, opcodeShift, immediateBits)
  }

  private def render(records: Vector[Instruction], acoCount: Int, xmlCount: Int, sopp: SOPPRule): String =
    s"""|package net.kurobako.gemstone.isa.amdgpu
        |
        |// Generated encoding constants from pinned Mesa ACO inputs. Instruction metadata is attached
        |// directly to generated InstructionDef cases in Instruction.scala. Do not edit.
        |private[amdgpu] val MesaACORecordCount: Int = $acoCount
        |private[amdgpu] val AMDXMLRecordCount: Int = $xmlCount
        |private[amdgpu] val MetadataRecordCount: Int = ${records.size}
        |private[amdgpu] val SOPPEncodingPrefix: Long = ${sopp.prefix}L
        |private[amdgpu] val SOPPOpcodeShift: Int = ${sopp.opcodeShift}
        |private[amdgpu] val SOPPImmediateBits: Int = ${sopp.immediateBits}
        |""".stripMargin

  private def renderInstructions(records: Vector[Instruction]): InstructionSources = {
    val metadataByOpcode = records.groupBy(_.name)
    val forms            = instructionForms(records)
    val definitionGroups = forms
      .groupBy(_.opcode)
      .toVector
      .sortBy(_._1)
      .map { case (_, opcodeForms) => opcodeForms.sortBy(_.operandCount) }
    val definitionChunks = DefinitionDomain.values.toVector.flatMap { domain =>
      val domainGroups =
        definitionGroups.filter(group => definitionDomain(group.head.opcode, metadataByOpcode) == domain)
      val shards = weightedChunks(domainGroups)(group =>
        renderDefinitionCase(group, metadataByOpcode(group.head.opcode)).length
      )
      shards.map { groups =>
        val range      = Option.when(shards.size > 1)(definitionRange(domain, groups)).getOrElse("")
        val name       = s"${domain.stem}$range"
        val objectName = s"${name}InstructionDefs"
        DefinitionChunk(s"${domain.directory}/$objectName.scala", objectName, groups)
      }
    }
    require(
      definitionChunks.flatMap(_.groups).size == definitionGroups.size,
      "definition-domain partition lost an AMDGPU opcode"
    )
    require(
      definitionChunks.map(_.fileName).distinct.size == definitionChunks.size &&
        definitionChunks.map(_.objectName).distinct.size == definitionChunks.size,
      "definition-domain partition produced a duplicate source or object name"
    )
    val definitionReferences = definitionChunks.flatMap { chunk =>
      chunk.groups.map(forms => forms.head.opcode -> s"${chunk.objectName}.${forms.head.definitionName}")
    }.toMap
    val definitionSources = definitionChunks.map { chunk =>
      chunk.fileName -> renderDefinitionChunkFile(chunk.objectName, chunk.groups, metadataByOpcode)
    }
    val definitionChunkNames = definitionChunks.map(chunk => s"${chunk.objectName}.definitions").mkString(", ")
    val instructionCases     = forms.map(form => renderInstructionCase(form, definitionReferences(form.opcode)))
    val main                 = s"""|// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
        |package net.kurobako.gemstone.isa.amdgpu
        |
        |import net.kurobako.gemstone.Col
        |import net.kurobako.gemstone.Col.*
        |
        |enum InstructionDecodeError {
        |  case UnknownOpcode(opcode: String)
        |  case InvalidOperandCount(opcode: String, expected: Col[Int], actual: Int)
        |  case AmbiguousForms(opcode: String, definitions: Col[InstructionDef])
        |}
        |
        |private[amdgpu] final case class InstructionFormDecoder(
        |    val operandCount: Int,
        |    construct: (Col[Operand], Col[Modifier]) => Instruction
        |) {
        |  def decode(operands: Col[Operand], mods: Col[Modifier]): Option[Instruction] =
        |    Option.when(operands.size == operandCount)(construct(operands, mods))
        |}
        |
        |private[amdgpu] final class InstructionDecoder(forms: Col[InstructionFormDecoder]) {
        |  val operandCounts: Col[Int] = forms.map(_.operandCount).distinct.sorted
        |
        |  def apply(operands: Col[Operand], mods: Col[Modifier]): Col[Instruction] =
        |    forms.flatMap(_.decode(operands, mods))
        |}
        |
        |private[amdgpu] def form(
        |    operandCount: Int
        |)(construct: (Col[Operand], Col[Modifier]) => Instruction): InstructionFormDecoder =
        |  InstructionFormDecoder(operandCount, construct)
        |private[amdgpu] def decoder(forms: InstructionFormDecoder*): InstructionDecoder =
        |  InstructionDecoder(forms.toCol)
        |
        |abstract class InstructionDef private[amdgpu] (
        |    val opcode: String,
        |    private[amdgpu] val decoder: InstructionDecoder,
        |    val meta: Metadata
        |) {
        |  final def decode(operands: Col[Operand], mods: Col[Modifier]): Col[Instruction] =
        |    decoder(operands, mods)
        |}
        |
        |private[amdgpu] val AllInstructionDefinitions: Col[InstructionDef] =
        |  Col($definitionChunkNames).flatten
        |
        |object InstructionDef {
        |  private lazy val ByOpcode: Map[String, Col[InstructionDef]] = AllInstructionDefinitions.groupBy(_.opcode)
        |
        |  def forOpcode(opcode: String): Col[InstructionDef] =
        |    ByOpcode.getOrElse(normalizeOpcode(opcode), Col.empty[InstructionDef])
        |}
        |
        |enum Instruction(val definition: InstructionDef, operandValues: Operand*) {
        |${instructionCases.mkString("\n")}
        |
        |  def mods: Col[Modifier]
        |  final val opcode: String = definition.opcode
        |  final val meta: Metadata = definition.meta
        |  final val operands: Col[Operand] = operandValues.toCol
        |}
        |
        |object Instruction {
        |  val definitions: Col[InstructionDef] = AllInstructionDefinitions
        |
        |  def decode(
        |      opcode: String,
        |      operands: Col[Operand],
        |      mods: Col[Modifier] = Col.empty
        |  ): Either[InstructionDecodeError, Instruction] = {
        |    val name = normalizeOpcode(opcode)
        |    decodeWithSuffixes(name, operands, mods, name)
        |  }
        |
        |  private val EncodingSuffixes: Col[String] = Col("_dpp8", "_sdwa", "_dpp", "_e64", "_e32")
        |
        |  private def decodeWithSuffixes(
        |      name: String,
        |      operands: Col[Operand],
        |      mods: Col[Modifier],
        |      original: String
        |  ): Either[InstructionDecodeError, Instruction] =
        |    decodeCanonical(name, operands, mods) match {
        |      case Left(InstructionDecodeError.UnknownOpcode(_)) =>
        |        EncodingSuffixes.collectFirst {
        |          case suffix if name.endsWith(suffix) => name.dropRight(suffix.length) -> Modifier(suffix.drop(1))
        |        } match {
        |          case Some((canonical, modifier)) => decodeWithSuffixes(canonical, operands, modifier +: mods, original)
        |          case None                        => Left(InstructionDecodeError.UnknownOpcode(original))
        |        }
        |      case result => result
        |    }
        |
        |  private def decodeCanonical(
        |      name: String,
        |      operands: Col[Operand],
        |      mods: Col[Modifier]
        |  ): Either[InstructionDecodeError, Instruction] =
        |    InstructionDef.forOpcode(name) match {
        |      case Col() => Left(InstructionDecodeError.UnknownOpcode(name))
        |      case candidates =>
        |        val matches = candidates.flatMap(_.decode(operands, mods))
        |        matches match {
        |          case Col(instruction) => Right(instruction)
        |          case Col() => Left(
        |            InstructionDecodeError.InvalidOperandCount(
        |              name, candidates.flatMap(_.decoder.operandCounts).distinct.sorted, operands.size
        |            )
        |          )
        |          case _ => Left(InstructionDecodeError.AmbiguousForms(name, matches.map(_.definition)))
        |        }
        |    }
        |}
        |""".stripMargin
    InstructionSources(main, definitionSources)
  }

  private def renderDefinitionChunkFile(
      objectName: String,
      groups: Vector[Vector[InstructionForm]],
      metadataByOpcode: Map[String, Vector[Instruction]]
  ): String =
    s"""|// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
        |package net.kurobako.gemstone.isa.amdgpu
        |
        |import net.kurobako.gemstone.Col
        |
        |${renderDefinitionChunk(objectName, groups, metadataByOpcode)}
        |""".stripMargin

  private def definitionDomain(
      opcode: String,
      metadataByOpcode: Map[String, Vector[Instruction]]
  ): DefinitionDomain = {
    val formats                               = metadataByOpcode(opcode).iterator.map(_.format).toSet
    def hasPrefix(prefixes: String*): Boolean = formats.exists(format => prefixes.exists(format.startsWith))

    if (
      formats.contains("VOP3P_MFMA") ||
      Vector("mfma", "wmma", "swmma", "tensor").exists(opcode.contains)
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
      metadataByOpcode: Map[String, Vector[Instruction]]
  ): String = {
    val cases      = groups.map(forms => renderDefinitionCase(forms, metadataByOpcode(forms.head.opcode)))
    val references = groups.map(_.head.definitionName).mkString(", ")
    s"""|private[amdgpu] object $objectName {
        |${cases.mkString("\n")}
        |
        |  val definitions: Col[InstructionDef] = Col($references)
        |}""".stripMargin
  }

  private def renderDefinitionCase(forms: Vector[InstructionForm], records: Vector[Instruction]): String = {
    val first    = forms.head
    val decoders = forms.map { form =>
      val arguments        = form.fields.indices.map(index => s"operands($index)") :+ "mods"
      val operandParameter = if (form.operandCount == 0) "_" else "operands"
      s"form(${form.operandCount})(($operandParameter, mods) => Instruction.${form.caseName}(${arguments.mkString(", ")}))"
    }
    s"""|  case object ${first.definitionName} extends InstructionDef(
        |    ${literal(first.opcode)},
        |    decoder(${decoders.mkString(", ")}),
        |    ${renderMetadata(records)}
        |  )""".stripMargin
  }

  private def renderMetadata(records: Vector[Instruction]): String = {
    val encodings = records.map { record =>
      val operands = record.operands.map { operand =>
        val field = Option.when(operand.field.nonEmpty)(s"Some(${literal(operand.field)})").getOrElse("None")
        s"          OperandInfo($field, ${literal(operand.kind)}, ${operand.bits}, ${operand.input}, ${operand.output}, ${operand.implicitOperand})"
      }
      val operandVector =
        if (operands.isEmpty) "Col.empty"
        else s"Col(\n${operands.mkString(",\n")}\n        )"
      val group = Option.when(record.group.nonEmpty)(s"Some(${literal(record.group)})").getOrElse("None")
      s"""|      Encoding(
          |        Family.${record.generation},
          |        ${literal(record.target)},
          |        ${literal(record.format)},
          |        ${record.opcode},
          |        $operandVector,
          |        $group,
          |        MetadataSource.${record.source}
          |      )""".stripMargin
    }
    s"""|Metadata(Col(
        |${encodings.mkString(",\n")}
        |    ))""".stripMargin
  }

  private def renderInstructionCase(form: InstructionForm, definitionReference: String): String = {
    val fields          = form.fields.map(field => s"${field.name}: Operand") :+ "mods: Col[Modifier]"
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
    val fields                            = operands.map(_.field.toUpperCase).filter(_.nonEmpty)
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
    val words = opcode.split("[^A-Za-z0-9]+").toVector.filter(_.nonEmpty)
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
    families.map { case (packageName, generation) =>
      s"""|package net.kurobako.gemstone.isa.amdgpu.$packageName {
          |  val family: net.kurobako.gemstone.isa.amdgpu.Family =
          |    net.kurobako.gemstone.isa.amdgpu.Family.$generation
          |  val isa: net.kurobako.gemstone.isa.amdgpu.ISA =
          |    net.kurobako.gemstone.isa.amdgpu.ISA(family)
          |}""".stripMargin
    }.mkString("// Generated from the normalized AMDGPU generation set. Do not edit by hand.\n", "\n\n", "\n")
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

  private def booleanAttribute(element: Element, name: String): Boolean = element.getAttribute(name).toLowerCase match {
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

  private def optionalDirectText(element: Element, parent: String, name: String): Option[String] =
    optionalDirect(element, parent).flatMap(value => optionalDirect(value, name)).map(_.getTextContent.trim)

  private def directChildren(element: Element, name: String): Vector[Element] = {
    val children = element.getChildNodes
    Vector.tabulate(children.getLength)(children.item).collect {
      case child: Element if child.getTagName == name => child
    }
  }

  private def write(output: Path, content: String): Unit = {
    val formatted = GeneratedScalaFormatter(output, content)
    val current   = if (Files.isRegularFile(output)) Some(Files.readString(output, StandardCharsets.UTF_8)) else None
    if (!current.contains(formatted)) {
      val _ = Files.createDirectories(output.getParent)
      val _ = GeneratedFiles.write(output, formatted)
    }
  }

  private def deleteScalaFiles(directory: Path): Unit =
    if (Files.isDirectory(directory)) {
      val paths = Files.walk(directory)
      val found =
        try paths.iterator().asScala.toVector.sortBy(_.getNameCount).reverse
        finally paths.close()
      found.filter(path => Files.isRegularFile(path) && path.getFileName.toString.endsWith(".scala"))
        .foreach(path => Files.delete(path))
      found.filter(path => path != directory && Files.isDirectory(path) && isEmptyDirectory(path))
        .foreach(path => Files.delete(path))
    }

  private def isEmptyDirectory(directory: Path): Boolean = {
    val entries = Files.list(directory)
    try !entries.findAny().isPresent
    finally entries.close()
  }
}

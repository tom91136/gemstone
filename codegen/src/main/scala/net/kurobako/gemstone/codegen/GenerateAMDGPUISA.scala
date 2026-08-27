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
  private final case class InstructionSources(main: String, definitions: Vector[(String, String)])

  def main(arguments: Array[String]): Unit = arguments.toVector match {
    case Vector(references, exporter, output) =>
      val result  = extract(Paths.get(references), Paths.get(exporter))
      val catalog = Paths.get(output)
      write(catalog, render(result.records, result.acoCount, result.xmlCount, result.sopp))
      val instructions    = renderInstructions(result.records)
      val instructionRoot = catalog.getParent.getParent
      write(instructionRoot.resolve("Instructions.scala"), instructions.main)
      val definitionsRoot = instructionRoot.resolve("definitions")
      deleteScalaFiles(definitionsRoot)
      instructions.definitions.foreach { case (name, content) => write(definitionsRoot.resolve(name), content) }
      write(catalog.getParent.getParent.getParent.resolve("Architectures.scala"), renderArchitectures())
    case _ =>
      throw new IllegalArgumentException(
        "usage: GenerateAMDGPUISA <prepared-references> <aco-exporter.py> <output.scala>"
      )
  }

  private[codegen] def generate(references: Path, exporter: Path): String = {
    val result = extract(references, exporter)
    render(result.records, result.acoCount, result.xmlCount, result.sopp)
  }

  private def extract(references: Path, exporter: Path): Extracted = {
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
    val aco      = parseACO(references.resolve("mesa/src/amd/compiler/aco_opcodes.py"), exporter)
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

  private def parseACO(source: Path, exporter: Path): Vector[Instruction] = {
    require(Files.isRegularFile(source), s"missing Mesa ACO opcode source: $source")
    require(Files.isRegularFile(exporter), s"missing ACO exporter: $exporter")
    val process = new ProcessBuilder("python3", exporter.toString, source.toString)
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
      throw new IllegalArgumentException("Mesa ACO assembler no longer contains emit_sopp_instruction")
    }
    val (prefixBits, prefixShift) = Prefix.findFirstMatchIn(body)
      .map(value => value.group(1) -> value.group(2).toInt)
      .getOrElse(throw new IllegalArgumentException("unrecognized Mesa ACO SOPP prefix rule"))
    val opcodeShift = Opcode.findFirstMatchIn(body).map(_.group(1).toInt)
      .getOrElse(throw new IllegalArgumentException("unrecognized Mesa ACO SOPP opcode rule"))
    val immediateBits = Immediate.findFirstMatchIn(body).map(_.group(1).toInt)
      .getOrElse(throw new IllegalArgumentException("unrecognized Mesa ACO SOPP immediate rule"))
    require(
      ir.matches("(?s).*struct SALU_instruction.*?uint32_t imm;.*"),
      "Mesa ACO IR no longer has the SALU immediate field"
    )
    SOPPRule(java.lang.Long.parseLong(prefixBits, 2) << prefixShift, opcodeShift, immediateBits)
  }

  private def render(records: Vector[Instruction], acoCount: Int, xmlCount: Int, sopp: SOPPRule): String =
    s"""|package net.kurobako.gemstone.isa.amdgpu.common.generated
        |
        |// Generated encoding constants from pinned Mesa ACO inputs. Instruction metadata is attached
        |// directly to generated InstructionDef cases in Instructions.scala. Do not edit.
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
    val definitionReferences = definitionGroups.grouped(100).zipWithIndex.flatMap { case (groups, chunkIndex) =>
      groups.map(forms => forms.head.opcode -> s"InstructionDefs$chunkIndex.${forms.head.definitionName}")
    }.toMap
    val definitionChunks = definitionGroups.grouped(100).zipWithIndex.map { case (groups, chunkIndex) =>
      f"InstructionDefs$chunkIndex%02d.scala" -> renderDefinitionChunkFile(chunkIndex, groups, metadataByOpcode)
    }.toVector
    val definitionChunkNames =
      definitionChunks.indices.map(index => s"InstructionDefs$index.definitions").mkString(", ")
    val instructionCases = forms.map(form => renderInstructionCase(form, definitionReferences(form.opcode)))
    val main             = s"""|// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
        |package net.kurobako.gemstone.isa.amdgpu.common
        |
        |enum InstructionDecodeError {
        |  case UnknownOpcode(opcode: String)
        |  case InvalidOperandCount(opcode: String, expected: Vector[Int], actual: Int)
        |  case AmbiguousForms(opcode: String, definitions: Vector[InstructionDef])
        |}
        |
        |private[common] final case class InstructionFormDecoder(
        |    val operandCount: Int,
        |    construct: (Vector[Operand], Vector[Modifier]) => Instruction
        |) {
        |  def decode(operands: Vector[Operand], mods: Vector[Modifier]): Option[Instruction] =
        |    Option.when(operands.size == operandCount)(construct(operands, mods))
        |}
        |
        |final class Decoder private[common] (forms: Vector[InstructionFormDecoder]) {
        |  val operandCounts: Vector[Int] = forms.map(_.operandCount).distinct.sorted
        |
        |  def apply(operands: Vector[Operand], mods: Vector[Modifier]): Vector[Instruction] =
        |    forms.flatMap(_.decode(operands, mods))
        |}
        |
        |private[common] def form(
        |    operandCount: Int
        |)(construct: (Vector[Operand], Vector[Modifier]) => Instruction): InstructionFormDecoder =
        |  InstructionFormDecoder(operandCount, construct)
        |private[common] def decoder(forms: InstructionFormDecoder*): Decoder = new Decoder(forms.toVector)
        |
        |abstract class InstructionDef private[common] (
        |    val opcode: String,
        |    val decoder: Decoder,
        |    val meta: Metadata
        |)
        |
        |val Instructions: Vector[InstructionDef] =
        |  Vector($definitionChunkNames).flatten
        |
        |object InstructionDef {
        |  private lazy val ByOpcode: Map[String, Vector[InstructionDef]] = Instructions.groupBy(_.opcode)
        |
        |  def forOpcode(opcode: String): Vector[InstructionDef] =
        |    ByOpcode.getOrElse(normalizeOpcode(opcode), Vector.empty)
        |}
        |
        |enum Instruction(val definition: InstructionDef, operandValues: Operand*) {
        |${instructionCases.mkString("\n")}
        |
        |  def mods: Vector[Modifier]
        |  final val opcode: String = definition.opcode
        |  final val decoder: Decoder = definition.decoder
        |  final val meta: Metadata = definition.meta
        |  final val operands: Vector[Operand] = operandValues.toVector
        |}
        |
        |object Instruction {
        |  def decode(
        |      opcode: String,
        |      operands: Vector[Operand],
        |      mods: Vector[Modifier] = Vector.empty
        |  ): Either[InstructionDecodeError, Instruction] = {
        |    val name = normalizeOpcode(opcode)
        |    decodeCompatible(name, operands, mods, name)
        |  }
        |
        |  private val EncodingSuffixes: Vector[String] = Vector("_dpp8", "_sdwa", "_dpp", "_e64", "_e32")
        |
        |  private def decodeCompatible(
        |      name: String,
        |      operands: Vector[Operand],
        |      mods: Vector[Modifier],
        |      original: String
        |  ): Either[InstructionDecodeError, Instruction] =
        |    decodeCanonical(name, operands, mods) match {
        |      case Left(InstructionDecodeError.UnknownOpcode(_)) =>
        |        EncodingSuffixes.collectFirst {
        |          case suffix if name.endsWith(suffix) => name.dropRight(suffix.length) -> Modifier(suffix.drop(1))
        |        } match {
        |          case Some((canonical, modifier)) => decodeCompatible(canonical, operands, modifier +: mods, original)
        |          case None => compatibilityAlias(name) match {
        |            case Some(canonical) => decodeCanonical(canonical, operands, mods)
        |            case None            => Left(InstructionDecodeError.UnknownOpcode(original))
        |          }
        |        }
        |      case result => result
        |    }
        |
        |  // Reviewed aliases accepted by older AMD assemblers; the enum retains current canonical names.
        |  private def compatibilityAlias(name: String): Option[String] = name match {
        |    case value if value.startsWith("v_cmpsx_") => Some("v_cmpx_" + value.drop(8))
        |    case value if value.startsWith("v_cmps_")  => Some("v_cmp_" + value.drop(7))
        |    case "buffer_wbinvl1_sc"                    => Some("buffer_wbinvl1")
        |    case "export"                               => Some("exp")
        |    case "v_accvgpr_read_b32"                   => Some("v_accvgpr_read")
        |    case "v_accvgpr_write_b32"                  => Some("v_accvgpr_write")
        |    case "v_addc_u32"                           => Some("v_add_co_ci_u32")
        |    case "v_subb_u32"                           => Some("v_sub_co_ci_u32")
        |    case "v_subbrev_u32"                        => Some("v_subrev_co_ci_u32")
        |    case "v_subrev_i32"                         => Some("v_subrev_u32")
        |    case _                                        => None
        |  }
        |
        |  private def decodeCanonical(
        |      name: String,
        |      operands: Vector[Operand],
        |      mods: Vector[Modifier]
        |  ): Either[InstructionDecodeError, Instruction] =
        |    InstructionDef.forOpcode(name) match {
        |      case Vector() => Left(InstructionDecodeError.UnknownOpcode(name))
        |      case candidates =>
        |        val matches = candidates.flatMap(definition => definition.decoder(operands, mods))
        |        matches match {
        |          case Vector(instruction) => Right(instruction)
        |          case Vector() => Left(
        |            InstructionDecodeError.InvalidOperandCount(
        |              name, candidates.flatMap(_.decoder.operandCounts).distinct.sorted, operands.size
        |            )
        |          )
        |          case _ => Left(InstructionDecodeError.AmbiguousForms(name, matches.map(_.definition)))
        |        }
        |    }
        |}
        |""".stripMargin
    InstructionSources(main, definitionChunks)
  }

  private def renderDefinitionChunkFile(
      index: Int,
      groups: Vector[Vector[InstructionForm]],
      metadataByOpcode: Map[String, Vector[Instruction]]
  ): String =
    s"""|// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
        |package net.kurobako.gemstone.isa.amdgpu.common
        |
        |${renderDefinitionChunk(index, groups, metadataByOpcode)}
        |""".stripMargin

  private def instructionForms(records: Vector[Instruction]): Vector[InstructionForm] = {
    val grouped = records.groupBy(_.name).toVector.sortBy(_._1)
    val names   = uniqueCaseNames(grouped.map(_._1))
    grouped.flatMap { case (opcode, definitions) =>
      val rawSignatures = definitions.map(_.operands.filter(!_.implicitOperand)).distinct
      val nonEmpty      = rawSignatures.filter(_.nonEmpty)
      val counts        = (nonEmpty.map(_.size) ++ compatibilityOperandCounts(opcode)).distinct.sorted match {
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
  private def compatibilityOperandCounts(opcode: String): Vector[Int] = opcode match {
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
      index: Int,
      groups: Vector[Vector[InstructionForm]],
      metadataByOpcode: Map[String, Vector[Instruction]]
  ): String = {
    val cases      = groups.map(forms => renderDefinitionCase(forms, metadataByOpcode(forms.head.opcode)))
    val references = groups.map(_.head.definitionName).mkString(", ")
    s"""|private[common] object InstructionDefs$index {
        |${cases.mkString("\n")}
        |
        |  val definitions: Vector[InstructionDef] = Vector($references)
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
        if (operands.isEmpty) "Vector.empty"
        else s"Vector(\n${operands.mkString(",\n")}\n        )"
      val group = Option.when(record.group.nonEmpty)(s"Some(${literal(record.group)})").getOrElse("None")
      s"""|      Encoding(
          |        Generation.${record.generation},
          |        ${literal(record.target)},
          |        ${literal(record.format)},
          |        ${record.opcode},
          |        $operandVector,
          |        $group,
          |        MetadataSource.${record.source}
          |      )""".stripMargin
    }
    s"""|Metadata(Vector(
        |${encodings.mkString(",\n")}
        |    ))""".stripMargin
  }

  private def renderInstructionCase(form: InstructionForm, definitionReference: String): String = {
    val fields          = form.fields.map(field => s"${field.name}: Operand") :+ "mods: Vector[Modifier]"
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

  private def uniqueFields(fields: Vector[Field]): Vector[Field] = {
    val seen = scala.collection.mutable.Map.empty[String, Int]
    fields.map { field =>
      val count = seen.getOrElse(field.name, 0) + 1
      seen(field.name) = count
      field.copy(name = if (count == 1) field.name else s"${field.name}$count")
    }
  }

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
          |  export net.kurobako.gemstone.isa.amdgpu.common.API.*
          |  val Version: net.kurobako.gemstone.isa.amdgpu.common.Generation =
          |    net.kurobako.gemstone.isa.amdgpu.common.Generation.$generation
          |  val Instructions: Vector[net.kurobako.gemstone.isa.amdgpu.common.InstructionDef] =
          |    net.kurobako.gemstone.isa.amdgpu.common.instructionsFor(Version)
          |  object ISA extends net.kurobako.gemstone.isa.amdgpu.common.Architecture(Version)
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
    case other                    => throw new IllegalArgumentException(s"unknown AMD ISA XML: $other")
  }

  private def parseRadix(element: Element): Int = {
    val radix = Option(element.getAttribute("Radix")).filter(_.nonEmpty).map(_.toInt).getOrElse(10)
    Integer.parseInt(element.getTextContent.trim, radix)
  }

  private def booleanAttribute(element: Element, name: String): Boolean = element.getAttribute(name).toLowerCase match {
    case "true"  => true
    case "false" => false
    case other   => throw new IllegalArgumentException(s"invalid $name '$other'")
  }

  private def requiredText(element: Element, name: String): String =
    optionalText(element, name).getOrElse(throw new IllegalArgumentException(s"missing <$name>"))

  private def optionalText(element: Element, name: String): Option[String] = {
    val nodes = element.getElementsByTagName(name)
    if (nodes.getLength == 0) None else Some(nodes.item(0).getTextContent.trim)
  }

  private def requiredDirect(element: Element, name: String): Element =
    optionalDirect(element, name).getOrElse(throw new IllegalArgumentException(s"missing direct <$name>"))

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
      val _ = Files.writeString(output, formatted, StandardCharsets.UTF_8)
    }
  }

  private def deleteScalaFiles(directory: Path): Unit =
    if (Files.isDirectory(directory)) {
      val files = Files.list(directory)
      try files.iterator().asScala.filter(path => path.getFileName.toString.endsWith(".scala")).foreach(Files.delete)
      finally files.close()
    }
}

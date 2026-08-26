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
object GenerateAmdGpuIsa {
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
  private final case class SoppRule(prefix: Long, opcodeShift: Int, immediateBits: Int)

  def main(arguments: Array[String]): Unit = arguments.toVector match {
    case Vector(references, exporter, output) =>
      write(Paths.get(output), generate(Paths.get(references), Paths.get(exporter)))
    case _ =>
      throw new IllegalArgumentException(
        "usage: GenerateAmdGpuIsa <prepared-references> <aco-exporter.py> <output.scala>"
      )
  }

  private[codegen] def generate(references: Path, exporter: Path): String = {
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

    val official = xmlFiles.flatMap(parseOfficialXml)
    val aco      = parseAco(references.resolve("mesa/src/amd/compiler/aco_opcodes.py"), exporter)
    val sopp     = parseAcoSoppRule(references.resolve("mesa/src/amd/compiler"))
    val records  = (aco ++ official)
      .sortBy(record => (record.name, record.generation, record.target, record.format, record.opcode))
    render(records, aco.size, official.size, sopp)
  }

  private def parseOfficialXml(input: Path): Vector[Instruction] = {
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
        Instruction(name, generation, target, format, opcode, operands, group, "AmdXml")
      }
    }.flatten
  }

  private def parseAco(source: Path, exporter: Path): Vector[Instruction] = {
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
        "MesaAco"
      )
    }
  }

  private def parseAcoSoppRule(root: Path): SoppRule = {
    val assembler = Files.readString(root.resolve("aco_assembler.cpp"), StandardCharsets.UTF_8)
    val ir        = Files.readString(root.resolve("aco_ir.h"), StandardCharsets.UTF_8)
    val SoppBody  = raw"(?s)emit_sopp_instruction\(.*?\n\}".r
    val Prefix    = raw"encoding\s*=\s*\(0b([01]+)\s*<<\s*([0-9]+)\)".r
    val Opcode    = raw"encoding\s*\|=\s*opcode\s*<<\s*([0-9]+)".r
    val Immediate = raw"encoding\s*\|=\s*\(uint([0-9]+)_t\)sopp\.imm".r
    val body      = SoppBody.findFirstIn(assembler).getOrElse {
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
    SoppRule(java.lang.Long.parseLong(prefixBits, 2) << prefixShift, opcodeShift, immediateBits)
  }

  private def render(records: Vector[Instruction], acoCount: Int, xmlCount: Int, sopp: SoppRule): String = {
    val chunks = records.grouped(200).toVector.map { group =>
      val data = group.map(encode).mkString("\n")
      s"\"\"\"$data\"\"\""
    }
    s"""|package net.kurobako.gemstone.isa.amdgpu.common.generated
        |
        |import net.kurobako.gemstone.isa.amdgpu.common.*
        |
        |// Generated from pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit.
        |object Database extends InstructionDatabase {
        |  val mesaAcoRecordCount: Int = $acoCount
        |  val amdXmlRecordCount: Int = $xmlCount
        |  override val soppEncodingPrefix: Long = ${sopp.prefix}L
        |  override val soppOpcodeShift: Int = ${sopp.opcodeShift}
        |  override val soppImmediateBits: Int = ${sopp.immediateBits}
        |
        |  private val chunks: Vector[String] = Vector(
        |    ${chunks.mkString(",\n    ")}
        |  )
        |
        |  override lazy val instructions: Vector[InstructionInfo] = chunks.flatMap(_.linesIterator.map(decode).toVector)
        |
        |  private def decode(row: String): InstructionInfo = {
        |    val columns = row.split("\\t", -1)
        |    val operands = if (columns(6).isEmpty) Vector.empty else columns(6).split(';').toVector.map { value =>
        |      val fields = value.split(",", -1)
        |      OperandInfo(option(fields(0)), fields(1), fields(2).toInt, fields(3) == "1", fields(4) == "1", fields(5) == "1")
        |    }
        |    InstructionInfo(
        |      columns(0), Generation.valueOf(columns(1)), columns(2), columns(3), columns(4).toInt, operands,
        |      option(columns(5)), MetadataSource.valueOf(columns(7))
        |    )
        |  }
        |
        |  private def option(value: String): Option[String] = if (value.isEmpty) None else Some(value)
        |}
        |""".stripMargin
  }

  private def encode(record: Instruction): String = {
    val operands = record.operands.map { operand =>
      Vector(
        operand.field,
        operand.kind,
        operand.bits.toString,
        flag(operand.input),
        flag(operand.output),
        flag(operand.implicitOperand)
      ).mkString(",")
    }.mkString(";")
    Vector(
      safeField(record.name),
      safeField(record.generation),
      safeField(record.target),
      safeField(record.format),
      record.opcode.toString,
      safeField(record.group),
      operands,
      safeField(record.source)
    ).mkString("\t")
  }

  private def generationFor(fileName: String): String = fileName match {
    case "amdgpu_isa_rdna1.xml"   => "Gfx10"
    case "amdgpu_isa_rdna2.xml"   => "Gfx10"
    case "amdgpu_isa_rdna3.xml"   => "Gfx11"
    case "amdgpu_isa_rdna3_5.xml" => "Gfx11"
    case "amdgpu_isa_rdna4.xml"   => "Gfx12"
    case "amdgpu_isa_cdna1.xml"   => "Cdna1"
    case "amdgpu_isa_cdna2.xml"   => "Cdna2"
    case "amdgpu_isa_cdna3.xml"   => "Cdna3"
    case "amdgpu_isa_cdna4.xml"   => "Cdna4"
    case "amdgpu_isa_cdna5.xml"   => "Cdna5"
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

  private def flag(value: Boolean): String = if (value) "1" else "0"

  private def safeField(value: String): String = {
    require(
      !value.exists(char => char == '\t' || char == '\n' || char == '\r' || char == ',' || char == ';'),
      s"unsafe metadata field: $value"
    )
    value
  }

  private def write(output: Path, content: String): Unit = {
    val current = if (Files.isRegularFile(output)) Some(Files.readString(output, StandardCharsets.UTF_8)) else None
    if (!current.contains(content)) {
      val _ = Files.createDirectories(output.getParent)
      val _ = Files.writeString(output, content, StandardCharsets.UTF_8)
    }
  }
}

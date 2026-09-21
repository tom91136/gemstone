package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*

import upickle.default.{ReadWriter, macroRW, read}

import java.nio.charset.StandardCharsets
import scala.util.Using

/** Lazily loads the generated AMDGPU encoding catalog from its checked-in JSON resource. */
private[amdgpu] object MetadataCatalog {
  private final case class EncodingRow(family: Int, target: Int, format: Int, opcode: Int, schema: Int, source: Int)
  private final case class CatalogFile(
      strings: Vector[String],
      schemas: Vector[Vector[(Int, Int, Boolean)]],
      encodings: Vector[EncodingRow],
      metadata: Vector[(Int, Int)]
  )

  private given ReadWriter[EncodingRow] = macroRW
  private given ReadWriter[CatalogFile] = macroRW

  private val Resource = "/net/kurobako/gemstone/isa/amdgpu/metadata.json"

  private lazy val values: Vec[Metadata] = load()

  private def load(): Vec[Metadata] = {
    val stream = Option(getClass.getResourceAsStream(Resource))
      .getOrElse(throw IllegalStateException(s"missing AMDGPU metadata resource $Resource"))
    val json    = Using.resource(stream)(input => String(input.readAllBytes(), StandardCharsets.UTF_8))
    val file    = read[CatalogFile](json)
    val strings = file.strings
    val schemas = file.schemas.map(_.map { case (field, kind, implicitOperand) =>
      OperandInfo(
        field = Option.when(field >= 0)(strings(field)),
        kind = refined("operand kind", strings(kind), value => OperandKind.either(value)),
        implicitOperand = implicitOperand
      )
    }.toVec)
    val encodings = file.encodings.map { row =>
      Encoding(
        family = Family.valueOf(strings(row.family)),
        target = refined("target", strings(row.target), value => MetadataTarget.either(value)),
        format = refined("format", strings(row.format), value => InstructionFormat.either(value)),
        opcode = row.opcode,
        operands = schemas(row.schema),
        source = MetadataSource.valueOf(strings(row.source))
      )
    }
    file.metadata.map { case (start, count) =>
      Metadata(encodings.slice(start, start + count).toVec)
    }.toVec
  }

  private def refined[A](label: String, value: String, refine: String => Either[String, A]): A =
    refine(value).fold(
      message => throw IllegalArgumentException(s"invalid AMDGPU metadata $label '$value': $message"),
      identity
    )

  def apply(metadataId: Int): Metadata =
    if (metadataId >= 0 && metadataId < values.size) values(metadataId)
    else throw IllegalArgumentException(s"invalid AMDGPU metadata catalog id: $metadataId")
}

package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.pickler.given
import scala.util.Using
import upickle.default.ReadWriter
import upickle.default.read

object Metadata {
  case class Encoding(
      isa: ISA,
      format: EncodingFormat,
      opcode: Int,
      operands: Vec[OperandInfo],
      groups: Vec[InstructionGroup] = Vec.empty,
      processors: Vec[NonEmpty[String]] = Vec.empty
  ) {
    def supports(target: Target): Boolean = isa == target.isa &&
      (processors.isEmpty || processors.contains(target.name))
  }

  object Catalogue {
    case class Row(
        isa: Int,
        format: Int,
        opcode: Int,
        schema: Int,
        groups: Vector[Int],
        processors: Vector[Int] = Vector.empty
    ) derives ReadWriter

    private lazy val values: Vec[Metadata] = {
      val stream = Option(getClass.getResourceAsStream("metadata.json"))
        .getOrElse(throw IllegalStateException("missing AMDGPU metadata resource metadata.json"))
      Using.resource(stream) { input =>
        read[Catalogue](input).decode
      }
    }

    def metadata(id: Int): Metadata =
      if (id >= 0 && id < values.size) values(id)
      else throw IllegalArgumentException(s"invalid AMDGPU metadata catalogue id: $id")
  }

  /** The interned encoding catalogue emitted by the AMDGPU generator. */
  case class Catalogue(
      strings: Vector[NonEmpty[String]],
      schemas: Vector[Vector[(Int, Int, Boolean, Int, Int, Boolean)]],
      encodings: Vector[Catalogue.Row],
      metadata: Vector[(Int, Int)]
  ) derives ReadWriter {
    private def decode: Vec[Metadata] = {
      val operands = schemas.map(_.map { case (field, kind, implicitOperand, bits, dataFormat, output) =>
        OperandInfo(
          field = Option.when(field >= 0)(EncodingField.valueOf(strings(field))),
          kind = OperandKind.valueOf(strings(kind)),
          implicitOperand = implicitOperand,
          bits = bits.unsafe[Int >= 0],
          dataFormat = Option.when(dataFormat >= 0)(DataFormat.valueOf(strings(dataFormat))),
          output = output
        )
      }.toVec)
      val decoded = encodings.map { row =>
        Encoding(
          isa = ISA.valueOf(strings(row.isa)),
          format = EncodingFormat.valueOf(strings(row.format)),
          opcode = row.opcode,
          operands = operands(row.schema),
          groups = row.groups.map(id => InstructionGroup.valueOf(strings(id))).toVec,
          processors = row.processors.map(strings).toVec
        )
      }
      metadata.map { case (start, count) =>
        Metadata(decoded.slice(start, start + count).toVec)
      }.toVec
    }
  }

  def encodingsFor(definition: Opcode, target: Target): Vec[Encoding] =
    definition.meta.encodings.filter(_.supports(target))

}

case class Metadata(encodings: Vec[Metadata.Encoding]) {

  /** Whether this catalog contains an encoding; exact processor availability may need further validation. */
  def supports(isa: ISA): Boolean = encodings.exists(_.isa == isa)
}

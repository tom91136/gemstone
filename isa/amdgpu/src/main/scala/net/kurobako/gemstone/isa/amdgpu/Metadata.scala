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
      format: NonEmpty[String],
      opcode: Int,
      operands: Vec[OperandInfo]
  )

  object Catalogue {
    case class Row(isa: Int, format: Int, opcode: Int, schema: Int) derives ReadWriter

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
      schemas: Vector[Vector[(Int, Int, Boolean)]],
      encodings: Vector[Catalogue.Row],
      metadata: Vector[(Int, Int)]
  ) derives ReadWriter {
    private def decode: Vec[Metadata] = {
      val operands = schemas.map(_.map { case (field, kind, implicitOperand) =>
        OperandInfo(
          field = Option.when(field >= 0)(strings(field)),
          kind = strings(kind),
          implicitOperand = implicitOperand
        )
      }.toVec)
      val decoded = encodings.map { row =>
        Encoding(
          isa = ISA.valueOf(strings(row.isa)),
          format = strings(row.format),
          opcode = row.opcode,
          operands = operands(row.schema)
        )
      }
      metadata.map { case (start, count) =>
        Metadata(decoded.slice(start, start + count).toVec)
      }.toVec
    }
  }

  def encodingsFor(definition: Instruction.Definition, isa: ISA): Vec[Encoding] =
    definition.meta.encodings.filter(_.isa == isa)

  def encodingsFor(instruction: Instruction, target: Target): Vec[Encoding] =
    encodingsFor(instruction.definition, target.isa)

}

case class Metadata(encodings: Vec[Metadata.Encoding]) {

  /** Whether this catalog contains an encoding; exact processor availability may need further validation. */
  def supports(isa: ISA): Boolean = encodings.exists(_.isa == isa)
}

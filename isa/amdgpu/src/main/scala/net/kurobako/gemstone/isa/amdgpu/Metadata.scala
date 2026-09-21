package net.kurobako.gemstone.isa.amdgpu

import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.nio.charset.StandardCharsets
import java.util.Base64

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import scala.util.Using

final case class Metadata(encodings: Vec[Encoding]) {
  def supports(family: Family): Boolean = encodings.exists(_.family == family)
  def supports(target: Target): Boolean =
    encodings.exists(encoding => encoding.family == target.family && encoding.target == target.metadataTarget)
}

object Metadata {
  private[amdgpu] object CatalogCodec {
    private val Magic = 0x474d4301

    def decode(encoded: String, expectedMetadataCount: Int, expectedEncodingCount: Int): Vec[Metadata] = {
      val bytes = Base64.getDecoder.decode(encoded)
      Using.resource(DataInputStream(ByteArrayInputStream(bytes))) { input =>
        val magic = input.readInt()
        if (magic != Magic) throw IllegalArgumentException(f"invalid AMDGPU metadata catalog magic 0x$magic%08x")
        val strings: Vector[String :| (MinLength[1] & NoNul)] = Vector.fill(readCount(input, "string")) {
          val raw = Array.ofDim[Byte](readCount(input, "string byte"))
          input.readFully(raw)
          String(raw, StandardCharsets.UTF_8)
            .refineEither[MinLength[1] & NoNul]
            .fold(
              detail => throw IllegalArgumentException(s"invalid AMDGPU metadata catalog string: $detail"),
              value => value
            )
        }
        val schemas = Vector.fill(readCount(input, "operand schema")) {
          Vector.fill(readCount(input, "operand")) {
            val fieldIndex = input.readInt()
            val kind       = OperandKind.applyUnsafe(strings(readIndex(input, strings.size, "operand kind")))
            val sizeBits   = readPositive(input, "operand bit width")
            val flags      = input.readUnsignedByte()
            if ((flags & ~0x7) != 0) throw IllegalArgumentException(s"invalid AMDGPU operand flags: $flags")
            OperandInfo(
              field = optional(strings, fieldIndex, "operand field"),
              kind = kind,
              sizeBits = sizeBits,
              input = (flags & 1) != 0,
              output = (flags & 2) != 0,
              implicitOperand = (flags & 4) != 0
            )
          }.toVec
        }
        val metadata = Vector.fill(readCount(input, "metadata")) {
          Metadata(
            Vector.fill(readCount(input, "encoding")) {
              Encoding(
                family = family(strings(readIndex(input, strings.size, "family"))),
                target = MetadataTarget.applyUnsafe(strings(readIndex(input, strings.size, "target"))),
                format = InstructionFormat.applyUnsafe(strings(readIndex(input, strings.size, "format"))),
                opcode = readCount(input, "opcode"),
                operands = schemas(readIndex(input, schemas.size, "operand schema")),
                functionalGroup = optional(strings, input.readInt(), "functional group").map(value =>
                  FunctionalGroup.applyUnsafe(value)
                ),
                source = metadataSource(strings(readIndex(input, strings.size, "metadata source")))
              )
            }.toVec
          )
        }.toVec
        if (input.available() != 0)
          throw IllegalArgumentException(s"AMDGPU metadata catalog has ${input.available()} trailing bytes")
        if (metadata.size != expectedMetadataCount)
          throw IllegalArgumentException(
            s"AMDGPU metadata catalog contains ${metadata.size} entries, expected $expectedMetadataCount"
          )
        val encodingCount = metadata.view.map(_.encodings.size).sum
        if (encodingCount != expectedEncodingCount)
          throw IllegalArgumentException(
            s"AMDGPU metadata catalog contains $encodingCount encodings, expected $expectedEncodingCount"
          )
        metadata
      }
    }

    private def family(name: String): Family =
      Family.values.find(_.toString == name).getOrElse {
        throw IllegalArgumentException(s"unknown AMDGPU metadata family: $name")
      }

    private def metadataSource(name: String): MetadataSource =
      MetadataSource.values.find(_.toString == name).getOrElse {
        throw IllegalArgumentException(s"unknown AMDGPU metadata source: $name")
      }

    private def readCount(input: DataInputStream, label: String): Int :| Positive0 = {
      val value = input.readInt()
      value.refineEither[Positive0].fold(
        _ => throw IllegalArgumentException(s"negative AMDGPU $label count: $value"),
        refined => refined
      )
    }

    private def readPositive(input: DataInputStream, label: String): Int :| Positive = {
      val value = input.readInt()
      value.refineEither[Positive].fold(
        _ => throw IllegalArgumentException(s"non-positive AMDGPU $label: $value"),
        refined => refined
      )
    }

    private def readIndex(input: DataInputStream, limit: Int, label: String): Int = {
      val index = input.readInt()
      if (index < 0 || index >= limit)
        throw IllegalArgumentException(s"AMDGPU $label index $index is outside 0 until $limit")
      index
    }

    private def optional(
        values: Vector[String :| (MinLength[1] & NoNul)],
        index: Int,
        label: String
    ): Option[String :| (MinLength[1] & NoNul)] =
      if (index == -1) None
      else if (index >= 0 && index < values.size) Some(values(index))
      else throw IllegalArgumentException(s"AMDGPU $label index $index is outside -1 until ${values.size}")
  }

  private[amdgpu] def instructionsFor(family: Family): Vec[Instruction.Definition] =
    Instruction.definitions.view.filter(_.meta.supports(family)).toVec

  private[amdgpu] def encodingsFor(definition: Instruction.Definition, family: Family): Vec[Encoding] =
    definition.meta.encodings.filter(_.family == family)

  private[amdgpu] def encodingsFor(definition: Instruction.Definition, target: Target): Vec[Encoding] =
    definition.meta.encodings.filter(encoding =>
      encoding.family == target.family && encoding.target == target.metadataTarget
    )

  private[amdgpu] def encodingsFor(instruction: Instruction, target: Target): Vec[Encoding] =
    encodingsFor(instruction.definition, target)

  private[amdgpu] lazy val allEncodings: Vec[Encoding] =
    Instruction.definitions.view.map(_.meta).toVec.distinct.flatMap(_.encodings)
}

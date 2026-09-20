package net.kurobako.gemstone.codegen

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.util.Using

/** Compact, deterministic wire format for the generated AMDGPU metadata catalog. */
private[codegen] object AMDGPUMetadataCatalogFormat {
  final case class Operand(
      field: Option[String],
      kind: String,
      sizeBits: Int,
      input: Boolean,
      output: Boolean,
      implicitOperand: Boolean
  )

  final case class Encoding(
      family: String,
      target: String,
      format: String,
      opcode: Int,
      operands: Vector[Operand],
      functionalGroup: Option[String],
      source: String
  )

  final case class Catalog(metadata: Vector[Vector[Encoding]]) {
    val encodingCount: Int = metadata.iterator.map(_.size).sum
  }

  final case class Encoded(payload: String, metadataCount: Int, encodingCount: Int)

  private val Magic = 0x474d4301

  def encode(catalog: Catalog): Encoded = {
    val strings = catalog.metadata.iterator
      .flatMap(_.iterator)
      .flatMap { encoding =>
        Iterator(encoding.family, encoding.target, encoding.format, encoding.source) ++
          encoding.functionalGroup.iterator ++
          encoding.operands.iterator.flatMap(operand => operand.field.iterator ++ Iterator(operand.kind))
      }
      .toVector
      .distinct
    val stringIds = strings.zipWithIndex.toMap
    val schemas   = catalog.metadata.iterator.flatMap(_.iterator.map(_.operands)).toVector.distinct
    val schemaIds = schemas.zipWithIndex.toMap
    val bytes     = Using.resource(ByteArrayOutputStream()) { buffer =>
      Using.resource(DataOutputStream(buffer)) { output =>
        output.writeInt(Magic)
        output.writeInt(strings.size)
        strings.foreach { value =>
          val encoded = value.getBytes(StandardCharsets.UTF_8)
          output.writeInt(encoded.length)
          output.write(encoded)
        }
        output.writeInt(schemas.size)
        schemas.foreach { operands =>
          output.writeInt(operands.size)
          operands.foreach { operand =>
            output.writeInt(operand.field.fold(-1)(stringIds))
            output.writeInt(stringIds(operand.kind))
            output.writeInt(operand.sizeBits)
            output.writeByte(flags(operand))
          }
        }
        output.writeInt(catalog.metadata.size)
        catalog.metadata.foreach { metadata =>
          output.writeInt(metadata.size)
          metadata.foreach { encoding =>
            output.writeInt(stringIds(encoding.family))
            output.writeInt(stringIds(encoding.target))
            output.writeInt(stringIds(encoding.format))
            output.writeInt(encoding.opcode)
            output.writeInt(schemaIds(encoding.operands))
            output.writeInt(encoding.functionalGroup.fold(-1)(stringIds))
            output.writeInt(stringIds(encoding.source))
          }
        }
        output.flush()
        buffer.toByteArray
      }
    }
    Encoded(Base64.getEncoder.encodeToString(bytes), catalog.metadata.size, catalog.encodingCount)
  }

  def decode(payload: String): Catalog = {
    val bytes = Base64.getDecoder.decode(payload)
    Using.resource(DataInputStream(ByteArrayInputStream(bytes))) { input =>
      val magic = input.readInt()
      if (magic != Magic) throw IllegalArgumentException(f"invalid AMDGPU metadata catalog magic 0x$magic%08x")
      val strings = Vector.fill(readCount(input, "string")) {
        val bytes = Array.ofDim[Byte](readCount(input, "string byte"))
        input.readFully(bytes)
        String(bytes, StandardCharsets.UTF_8)
      }
      val schemas = Vector.fill(readCount(input, "operand schema")) {
        Vector.fill(readCount(input, "operand")) {
          val fieldIndex = input.readInt()
          val kind       = strings(readIndex(input, strings.size, "operand kind"))
          val sizeBits   = readCount(input, "operand bit width")
          val flags      = input.readUnsignedByte()
          if ((flags & ~0x7) != 0)
            throw IllegalArgumentException(s"invalid AMDGPU operand flags: $flags")
          Operand(
            field = optional(strings, fieldIndex, "operand field"),
            kind = kind,
            sizeBits = sizeBits,
            input = (flags & 1) != 0,
            output = (flags & 2) != 0,
            implicitOperand = (flags & 4) != 0
          )
        }
      }
      val metadata = Vector.fill(readCount(input, "metadata")) {
        Vector.fill(readCount(input, "encoding")) {
          Encoding(
            family = strings(readIndex(input, strings.size, "family")),
            target = strings(readIndex(input, strings.size, "target")),
            format = strings(readIndex(input, strings.size, "format")),
            opcode = readCount(input, "opcode"),
            operands = schemas(readIndex(input, schemas.size, "operand schema")),
            functionalGroup = optional(strings, input.readInt(), "functional group"),
            source = strings(readIndex(input, strings.size, "metadata source"))
          )
        }
      }
      if (input.available() != 0)
        throw IllegalArgumentException(s"AMDGPU metadata catalog has ${input.available()} trailing bytes")
      Catalog(metadata)
    }
  }

  private def flags(operand: Operand): Int =
    (if (operand.input) 1 else 0) |
      (if (operand.output) 2 else 0) |
      (if (operand.implicitOperand) 4 else 0)

  private def readCount(input: DataInputStream, label: String): Int = {
    val value = input.readInt()
    if (value < 0) throw IllegalArgumentException(s"negative AMDGPU $label count: $value")
    value
  }

  private def readIndex(input: DataInputStream, limit: Int, label: String): Int = {
    val index = input.readInt()
    if (index < 0 || index >= limit)
      throw IllegalArgumentException(s"AMDGPU $label index $index is outside 0 until $limit")
    index
  }

  private def optional(values: Vector[String], index: Int, label: String): Option[String] =
    if (index == -1) None
    else if (index >= 0 && index < values.size) Some(values(index))
    else throw IllegalArgumentException(s"AMDGPU $label index $index is outside -1 until ${values.size}")
}

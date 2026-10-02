package net.kurobako.gemstone.isa.amdgpu.asm

import OperandCodec.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*

/** Scalar ALU layouts. Operand order and register widths come from the generated catalogue. */
private object ScalarCodec {
  val Formats: Vector[EncodingFormat] = Vector(EncodingFormat.SOP1, EncodingFormat.SOPC, EncodingFormat.SOP2)

  def format(word: Long, target: Target): Option[InstructionLayout] =
    Formats.flatMap(value => InstructionLayout.Catalogue.forFormat(target, value))
      .sortBy(layout => -layout.fixedMask.bitCount)
      .find(layout => layout.matches(word))

  private def operands(encoding: Metadata.Encoding): Vec[OperandInfo] = encoding.operands.filterNot(_.implicitOperand)

  private def supported(encoding: Metadata.Encoding, layout: InstructionLayout): Boolean = {
    val schema = operands(encoding)
    schema.map(_.field).distinct.size == schema.size && schema.forall { operand =>
      operand.field.exists(layout.byName.contains(_)) &&
      (operand.bits.raw == 4 || operand.bits.raw == 8 || operand.bits.raw == 16 || operand.bits.raw == 32 ||
        operand.bits.raw == 64)
    }
  }

  def encode(instruction: Instruction, target: Target, layout: InstructionLayout): Either[CodecError, Vec[Byte]] = {
    val unsupported = CodecError.UnsupportedOperandSyntax(instruction.opcode, target, instruction.operands)
    val encodings   = Metadata.encodingsFor(instruction.definition, target.isa).filter(value =>
      value.format == layout.format && supported(value, layout)
    ).distinct
    if (instruction.mods.nonEmpty) Left(unsupported)
    else encodings.headOption.toRight(unsupported).flatMap { encoding =>
      val schema = operands(encoding)
      if (schema.size != instruction.operands.size) Left(unsupported)
      else schema.zip(instruction.operands).traverse { case (info, operand) =>
        OperandCodec.encode(operand, info, target).toRight(unsupported).flatMap { case (value, literal) =>
          info.field.flatMap(layout.byName.get).flatMap(_.put(BigInt(value))).map(_ -> literal).toRight(unsupported)
        }
      }.flatMap { fields =>
        val literals = fields.flatMap(_._2).distinct
        if (literals.size > 1) Left(CodecError.InvalidOperand("scalar sources must share one literal word"))
        else {
          layout.byName(EncodingField.OP).put(BigInt(encoding.opcode)).toRight(unsupported).flatMap { opcode =>
            val word = fields.foldLeft(layout.prefixes.head | opcode) { case (value, (field, _)) => value | field }
            Option.when(layout.accepts(word, literals.headOption.fold(0)(_.bytes.size)))(
              LittleEndian.toVec(word.toLong, 4) ++ literals.flatMap(_.bytes)
            ).toRight(unsupported)
          }
        }
      }
    }
  }

  def decode(
      bytes: Vec[Byte],
      offset: Int,
      word: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[CodecError, DecodedInstruction] = {
    val opcode      = layout.get(BigInt(word), EncodingField.OP).toInt
    val format      = layout.format
    val definitions = OpcodeIndex.canonical(OpcodeIndex.definitions(format, opcode, target), target)(identity)
    definitions match {
      case Vec(definition) =>
        val schemas = Metadata.encodingsFor(definition, target.isa)
          .filter(value => value.format == layout.format && supported(value, layout)).map(operands(_)).distinct
        schemas match {
          case Vec(schema) =>
            val literalBytes = schema.flatMap { info =>
              info.field.filter(_ != EncodingField.SDST).map { field =>
                OperandCodec.literalSize(layout.get(BigInt(word), field).toInt, target)
              }
            }.maxOption.getOrElse(0)
            val size = (4 + literalBytes).unsafe[Int > 0]
            if (bytes.size - offset < size.raw)
              Left(CodecError.InvalidEncoding(s"truncated $format instruction: requires $size bytes"))
            else if (!layout.accepts(BigInt(word), literalBytes))
              Left(CodecError.InvalidEncoding(s"invalid $format encoding selectors"))
            else {
              val literal = Option.when(literalBytes != 0)(Literal(
                LittleEndian.decode(bytes, offset + 4, literalBytes.unsafe[Int where Between[0, 8]]),
                literalBytes.unsafe[Int in (4, 8)]
              ))
              schema.traverse { info =>
                val field = info.field
                field.flatMap(field =>
                  OperandCodec.decode(layout.get(BigInt(word), field).toInt, info, literal, target)
                )
                  .toRight(CodecError.InvalidEncoding(s"unsupported $format operand encoding in $field"))
              }.flatMap { args =>
                Instruction.decode(definition, args, Vec.empty[Program.Modifier]) match {
                  case Vec(instruction) => Right(DecodedInstruction(instruction, size))
                  case _ => Left(CodecError.InvalidEncoding(s"ambiguous operand form for ${definition.opcode}"))
                }
              }
            }
          case _ => Left(CodecError.InvalidEncoding(s"unsupported operand layout for ${definition.opcode}"))
        }
      case _ => Left(CodecError.InvalidEncoding(s"unknown or ambiguous $format opcode $opcode for ${target.name}"))
    }
  }

}

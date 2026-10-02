package net.kurobako.gemstone.isa.amdgpu.asm

import Program.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.Metadata.*

object Binary {
  def encodeStatement(
      statement: Statement,
      target: Target,
      waveSize: Int in (32, 64) = 32
  ): Either[CodecError, Vec[Byte]] = statement match {
    case Statement.Instruction(value)           => encode(value, target, waveSize)
    case Statement.InstructionPair(left, right) => DualIssueCodec.encode(left, right, target)
    case _ => Left(CodecError.InvalidOperand("expected an instruction or dual-issue pair"))
  }

  def decodeStatementAt(
      bytes: Vec[Byte],
      offset: Int >= 0,
      target: Target,
      waveSize: Int in (32, 64) = 32
  ): Either[CodecError, DecodedStatement] =
    if (DualIssueCodec.matches(bytes, offset.raw, target)) DualIssueCodec.decode(bytes, offset.raw, target)
    else decodeAt(bytes, offset, target, waveSize).map(value =>
      DecodedStatement(Statement.Instruction(value.instruction), value.size)
    )

  private def soppLayout(target: Target): InstructionLayout =
    InstructionLayout.Catalogue.forFormat(target, EncodingFormat.SOPP)
      .getOrElse(throw IllegalStateException(s"missing generated SOPP layout for ${target.isa}"))
  private lazy val relativeBranches = Opcode.definitions.filter(_.meta.encodings.exists(encoding =>
    encoding.format == EncodingFormat.SOPP && encoding.operands.exists(_.kind == OperandKind.LABEL)
  )).toSet

  /** Encode every currently implemented format and classify every other known format explicitly. */
  def encode(
      instruction: Instruction,
      target: Target,
      waveSize: Int in (32, 64) = 32
  ): Either[CodecError, Vec[Byte]] = {
    val definitions = encodingsFor(instruction.definition, target.isa)
    if (definitions.isEmpty) Left(CodecError.UnsupportedInstruction(instruction.opcode, target))
    else {
      val formats = definitions.map(_.format).distinct.sortBy(_.ordinal)
      if (formats.contains(EncodingFormat.SOPP))
        encodeSOPP(instruction, target).leftMap {
          case CodecError.InvalidOperand(_)
              if instruction.mods.nonEmpty ||
                instruction.operands.exists {
                  case Operand.IntegerLiteral(_, _) => false
                  case _                            => true
                } =>
            CodecError.UnsupportedOperandSyntax(instruction.opcode, target, instruction.operands)
          case error => error
        }
      else ScalarCodec.Formats.filter(formats.contains)
        .flatMap(format => InstructionLayout.Catalogue.forFormat(target, format)).headOption match {
        case Some(format)                                             => ScalarCodec.encode(instruction, target, format)
        case None if definitions.exists(InstructionCodec.supports(_)) =>
          InstructionCodec.encode(instruction, target, waveSize)
        case None => Left(CodecError.UnsupportedFormat(instruction.opcode, target, formats))
      }
    }
  }

  def decode(bytes: Vec[Byte], target: Target, waveSize: Int in (32, 64) = 32): Either[CodecError, Instruction] =
    decodeAt(bytes, 0, target, waveSize).flatMap { decoded =>
      if (decoded.size.raw == bytes.size) Right(decoded.instruction)
      else Left(CodecError.InvalidEncoding(s"trailing bytes after instruction: ${bytes.size - decoded.size.raw}"))
    }

  def decodeAt(
      bytes: Vec[Byte],
      offset: Int >= 0,
      target: Target,
      waveSize: Int in (32, 64) = 32
  ): Either[CodecError, DecodedInstruction] =
    if (offset.raw > bytes.size || bytes.size - offset.raw < 4)
      Left(CodecError.InvalidEncoding(s"truncated instruction at byte $offset"))
    else {
      val word   = LittleEndian.decode(bytes, offset.raw, 4)
      val layout = soppLayout(target)
      val result = if (layout.matches(word))
        decodeSOPP(Bits.low32(word), target).map(DecodedInstruction(_, 4))
      else ScalarCodec.format(word, target) match {
        case Some(format) =>
          ScalarCodec.decode(
            bytes,
            offset.raw,
            word,
            target,
            format
          ).orElse(InstructionCodec.decode(bytes, offset.raw, target, waveSize))
        case None => InstructionCodec.decode(bytes, offset.raw, target, waveSize)
      }
      result.flatMap { decoded =>
        encode(decoded.instruction, target, waveSize).flatMap { encoded =>
          if (encoded == bytes.slice(offset.raw, offset.raw + decoded.size.raw)) Right(decoded)
          else Left(CodecError.InvalidEncoding(s"noncanonical or reserved bits at byte $offset"))
        }
      }
    }

  def encodeSOPP(
      instruction: Instruction,
      target: Target
  ): Either[CodecError, Vec[Byte]] = {
    val definitions = encodingsFor(instruction.definition, target.isa).filter(_.format == EncodingFormat.SOPP)
    val encodings   = definitions.map(_.opcode).distinct
    if (encodings.size > 1)
      Left(CodecError.AmbiguousInstruction(instruction.opcode, target))
    else
      definitions.headOption.toRight(CodecError.UnsupportedInstruction(instruction.opcode, target)).flatMap {
        definition =>
          val layout = soppLayout(target)
          immediate(instruction, target, definition).flatMap { value =>
            val packed = for {
              opcode    <- layout.byName(EncodingField.OP).put(BigInt(definition.opcode))
              immediate <- layout.byName(EncodingField.SIMM16).put(BigInt(value))
            } yield LittleEndian.toVec((layout.prefixes.head | opcode | immediate).toLong, 4)
            packed.toRight(CodecError.InvalidOperand("SOPP fields exceed their generated widths"))
          }
      }
  }

  def decodeSOPP(
      bytes: Vec[Byte],
      target: Target
  ): Either[CodecError, Instruction] =
    if (bytes.size != 4) Left(CodecError.InvalidEncoding(s"SOPP is exactly four bytes, found ${bytes.size}"))
    else decodeSOPP(Bits.low32(LittleEndian.decode(bytes, 0, 4)), target)

  def decodeSOPP(bits: Binary32, target: Target): Either[CodecError, Instruction] = {
    val word   = bits.toLong
    val layout = soppLayout(target)
    if (!layout.matches(word))
      Left(CodecError.InvalidEncoding(f"not a SOPP word: 0x$word%08x"))
    else {
      val opcode  = layout.get(BigInt(word), EncodingField.OP).toInt
      val matches = OpcodeIndex.definitions(EncodingFormat.SOPP, opcode, target)
      val names   = matches.map(_.opcode).distinct
      if (names.size > 1)
        Left(
          CodecError.InvalidEncoding(
            s"ambiguous SOPP opcode $opcode for ${target.name}: ${names.mkString(", ")}"
          )
        )
      else
        matches.headOption.toRight(
          CodecError.InvalidEncoding(s"unknown SOPP opcode $opcode for ${target.name}")
        )
          .flatMap { definition =>
            val field = layout.byName(EncodingField.SIMM16)
            val raw   = field.get(BigInt(word))
            val value = if (relativeBranches.contains(definition) && raw.testBit(field.width - 1))
              raw - (BigInt(1) << field.width)
            else raw
            val operands =
              if (!matches.exists(Instruction.operandCounts(_).contains(0)) || value != 0)
                Vec(Operand.IntegerLiteral(value, 10))
              else Vec.empty[Operand]
            Instruction.decode(definition, operands, Vec.empty[Modifier]) match {
              case Vec(instruction) => Right(instruction)
              case Vec()            => Left(CodecError.InvalidEncoding(
                  s"generated instruction decoder rejected ${definition.opcode}: expected ${Instruction.operandCounts(definition).mkString(" or ")} operands, found ${operands.size}"
                ))
              case _ => Left(CodecError.InvalidEncoding(
                  s"generated instruction decoder produced ambiguous ${definition.opcode} forms"
                ))
            }
          }
    }
  }

  private def immediate(
      instruction: Instruction,
      target: Target,
      definition: Metadata.Encoding
  ): Either[CodecError, Long] = {
    val width   = soppLayout(target).byName(EncodingField.SIMM16).width
    val maximum = (BigInt(1) << width) - 1
    val minimum = -(BigInt(1) << (width - 1))
    val named   = definition.operands.find(!_.implicitOperand).flatMap { info =>
      if (Set(OperandKind.WAITCNT, OperandKind.WAITCNT_DEPCTR, OperandKind.WAIT_ALU)(info.kind))
        SymbolicOperandEncoder.counters(instruction, info.kind, target)
      else if (instruction.mods.isEmpty && instruction.operands.size == 1)
        SymbolicOperandEncoder.encode(instruction.operands.head, info.kind, target)
      else None
    }
    named.filter(value => value >= 0 && value <= maximum) match {
      case Some(value) => Right(value.toLong)
      case None        => instruction.operands match {
          case _ if instruction.mods.nonEmpty =>
            Left(CodecError.InvalidOperand(s"SOPP does not accept modifiers: ${instruction.mods.mkString(", ")}"))
          case Vec()                                                                         => Right(0L)
          case Vec(Operand.IntegerLiteral(value, _)) if value >= minimum && value <= maximum =>
            Right((value & maximum).toLong)
          case Vec(operand) => Left(CodecError.InvalidOperand(s"SOPP requires a $width-bit integer, found $operand"))
          case operands => Left(CodecError.InvalidOperand(s"SOPP accepts at most one operand, found ${operands.size}"))
        }
    }
  }

}

class Binary(target: Target, waveSize: Int in (32, 64) = 32)
    extends Decoder[Vec[Byte], Instruction, CodecError]
    with Encoder[Instruction, Vec[Byte], CodecError] {
  override def decode(bytes: Vec[Byte]): Either[CodecError, Instruction]       = Binary.decode(bytes, target, waveSize)
  override def encode(instruction: Instruction): Either[CodecError, Vec[Byte]] =
    Binary.encode(instruction, target, waveSize)
}

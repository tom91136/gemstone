package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import Metadata.*
import Program.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.string.*

/** Binary support starts with SOPP, whose fixed 32-bit layout is common from GFX6 through GFX12. Other formats are
  * rejected explicitly until their conditional layouts are normalized from the vendor XML/ACO rules.
  */
object Binary {
  enum CodecError {
    case UnsupportedInstruction(opcode: NonEmpty[String], target: Target)
    case UnsupportedFormat(opcode: NonEmpty[String], target: Target, formats: Vec[String])
    case UnsupportedOperandSyntax(opcode: NonEmpty[String], target: Target, operands: Vec[Operand])
    case AmbiguousInstruction(opcode: NonEmpty[String], target: Target)
    case InvalidOperand(message: NonEmpty[String])
    case InvalidEncoding(message: NonEmpty[String])
  }

  private val SOPPMask = 0xff800000L

  /** Encode every currently implemented format and classify every other known format explicitly. */
  def encode(
      instruction: Instruction,
      target: Target
  ): Either[CodecError, Vec[Byte]] = {
    val definitions = encodingsFor(instruction, target)
    if (definitions.isEmpty) Left(CodecError.UnsupportedInstruction(instruction.opcode, target))
    else {
      val formats = definitions.map(_.format.toUpperCaseRoot).distinct.sorted
      if (formats.contains("SOPP"))
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
      else Left(CodecError.UnsupportedFormat(instruction.opcode, target, formats))
    }
  }

  def encodeSOPP(
      instruction: Instruction,
      target: Target
  ): Either[CodecError, Vec[Byte]] = {
    val definitions = encodingsFor(instruction, target).filter(_.format == "SOPP")
    val encodings   = definitions.map(_.opcode).distinct
    if (encodings.size > 1)
      Left(CodecError.AmbiguousInstruction(instruction.opcode, target))
    else
      definitions.headOption.toRight(CodecError.UnsupportedInstruction(instruction.opcode, target)).flatMap {
        definition =>
          immediate(instruction).map { value =>
            val immediateMask = (1L << SOPPImmediateBits) - 1L
            val word          = SOPPEncodingPrefix |
              ((definition.opcode.toLong & 0x7fL) << SOPPOpcodeShift) |
              (value & immediateMask)
            LittleEndian.toVec(word, 4)
          }
      }
  }

  def decodeSOPP(
      bytes: Vec[Byte],
      target: Target
  ): Either[CodecError, Instruction] =
    if (bytes.size != 4) Left(CodecError.InvalidEncoding(s"SOPP is exactly four bytes, found ${bytes.size}"))
    else {
      val word = LittleEndian.decode(bytes, 0, 4)
      if ((word & SOPPMask) != SOPPEncodingPrefix)
        Left(CodecError.InvalidEncoding(f"not a SOPP word: 0x$word%08x"))
      else {
        val opcode  = Bits.unsignedIntField(word, SOPPOpcodeShift, 7)
        val matches = Instruction.definitions.filter(definition =>
          encodingsFor(definition, target.isa).exists(info => info.format == "SOPP" && info.opcode == opcode)
        )
        val names = matches.map(_.opcode).distinct
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
              val value    = Bits.low16(word).toBigInt
              val operands =
                if (!matches.exists(_.operandCounts.contains(0)) || value != 0)
                  Vec(Operand.IntegerLiteral(value, 10))
                else Vec.empty[Operand]
              definition.decode(operands, Vec.empty) match {
                case Vec(instruction) => Right(instruction)
                case Vec()            => Left(CodecError.InvalidEncoding(
                    s"generated instruction decoder rejected ${definition.opcode}: expected ${definition.operandCounts.mkString(" or ")} operands, found ${operands.size}"
                  ))
                case _ => Left(CodecError.InvalidEncoding(
                    s"generated instruction decoder produced ambiguous ${definition.opcode} forms"
                  ))
              }
            }
      }
    }

  private def immediate(instruction: Instruction): Either[CodecError, Long] = instruction.operands match {
    case _ if instruction.mods.nonEmpty =>
      Left(CodecError.InvalidOperand(s"SOPP does not accept modifiers: ${instruction.mods.mkString(", ")}"))
    case Vec()                                                                      => Right(0L)
    case Vec(Operand.IntegerLiteral(value, _)) if value >= -32768 && value <= 65535 =>
      Right(Bits.low16(value.toLong).toLong)
    case Vec(operand) => Left(CodecError.InvalidOperand(s"SOPP requires a 16-bit integer, found $operand"))
    case operands     => Left(CodecError.InvalidOperand(s"SOPP accepts at most one operand, found ${operands.size}"))
  }
}

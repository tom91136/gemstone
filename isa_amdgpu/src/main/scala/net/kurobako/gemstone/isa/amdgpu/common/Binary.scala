package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.isa.amdgpu.common.generated.SoppEncodingPrefix
import net.kurobako.gemstone.isa.amdgpu.common.generated.SoppImmediateBits
import net.kurobako.gemstone.isa.amdgpu.common.generated.SoppOpcodeShift

enum CodecError {
  case WrongGeneration(target: Target, expected: Generation)
  case UnsupportedInstruction(opcode: String, target: Target)
  case UnsupportedFormat(opcode: String, target: Target, formats: Vector[String])
  case UnsupportedOperandSyntax(opcode: String, target: Target, operands: Vector[Operand])
  case AmbiguousInstruction(opcode: String, target: Target)
  case InvalidOperand(message: String)
  case InvalidEncoding(message: String)
}

/** Binary support starts with SOPP, whose fixed 32-bit layout is common from GFX6 through GFX12. Other formats are
  * rejected explicitly until their conditional layouts are normalized from the vendor XML/ACO rules.
  */
object Binary {
  private val SoppMask = 0xff800000L

  /** Encode every currently implemented format and classify every other known format explicitly. */
  def encode(
      instruction: Instruction,
      target: Target
  ): Either[CodecError, Vector[Byte]] = {
    val definitions = encodingsFor(instruction, target)
    if (definitions.isEmpty) Left(CodecError.UnsupportedInstruction(instruction.opcode, target))
    else {
      val formats = definitions.map(_.format.toUpperCase).distinct.sorted
      if (formats.contains("SOPP"))
        encodeSopp(instruction, target).left.map {
          case CodecError.InvalidOperand(_)
              if instruction.mods.nonEmpty ||
                instruction.operands.exists(!_.isInstanceOf[Operand.IntegerLiteral]) =>
            CodecError.UnsupportedOperandSyntax(instruction.opcode, target, instruction.operands)
          case error => error
        }
      else Left(CodecError.UnsupportedFormat(instruction.opcode, target, formats))
    }
  }

  def encodeSopp(
      instruction: Instruction,
      target: Target
  ): Either[CodecError, Vector[Byte]] = {
    val definitions = encodingsFor(instruction, target).filter(_.format == "SOPP")
    val encodings   = definitions.map(_.opcode).distinct
    if (encodings.size > 1)
      Left(CodecError.AmbiguousInstruction(instruction.opcode, target))
    else
      definitions.headOption.toRight(CodecError.UnsupportedInstruction(instruction.opcode, target)).flatMap {
        definition =>
          immediate(instruction).map { value =>
            val immediateMask = (1L << SoppImmediateBits) - 1L
            val word          = SoppEncodingPrefix |
              ((definition.opcode.toLong & 0x7fL) << SoppOpcodeShift) |
              (value & immediateMask)
            littleEndian(word)
          }
      }
  }

  def decodeSopp(
      bytes: Seq[Byte],
      target: Target
  ): Either[CodecError, Instruction] =
    if (bytes.size != 4) Left(CodecError.InvalidEncoding(s"SOPP is exactly four bytes, found ${bytes.size}"))
    else {
      val word = bytes.zipWithIndex.foldLeft(0L) { case (result, (byte, index)) =>
        result | ((byte.toLong & 0xffL) << (index * 8))
      }
      if ((word & SoppMask) != SoppEncodingPrefix)
        Left(CodecError.InvalidEncoding(f"not a SOPP word: 0x$word%08x"))
      else {
        val opcode  = ((word >>> SoppOpcodeShift) & 0x7fL).toInt
        val matches = Instructions.filter(definition =>
          encodingsFor(definition, target).exists(info => info.format == "SOPP" && info.opcode == opcode)
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
              val value    = BigInt(word & 0xffffL)
              val operands =
                if (!matches.exists(_.decoder.operandCounts.contains(0)) || value != 0)
                  Vector(Operand.IntegerLiteral(value, 10))
                else Vector.empty
              Instruction.decode(definition.opcode, operands).left.map(error =>
                CodecError.InvalidEncoding(s"generated instruction decoder rejected ${definition.opcode}: $error")
              )
            }
      }
    }

  private def immediate(instruction: Instruction): Either[CodecError, Long] = instruction.operands match {
    case _ if instruction.mods.nonEmpty =>
      Left(CodecError.InvalidOperand(s"SOPP does not accept modifiers: ${instruction.mods.mkString(", ")}"))
    case Vector()                                                                      => Right(0L)
    case Vector(Operand.IntegerLiteral(value, _)) if value >= -32768 && value <= 65535 => Right(value.toLong & 0xffffL)
    case Vector(operand) => Left(CodecError.InvalidOperand(s"SOPP requires a 16-bit integer, found $operand"))
    case operands        => Left(CodecError.InvalidOperand(s"SOPP accepts at most one operand, found ${operands.size}"))
  }

  private def littleEndian(word: Long): Vector[Byte] =
    Vector.tabulate(4)(index => ((word >>> (index * 8)) & 0xffL).toByte)
}

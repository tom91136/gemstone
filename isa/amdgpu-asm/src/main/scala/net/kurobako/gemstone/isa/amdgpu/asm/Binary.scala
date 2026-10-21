package net.kurobako.gemstone.isa.amdgpu.asm

import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*

object Binary {
  def encodeStatement(statement: Statement, mode: TargetMode): Either[CodecError, Vec[Byte]] = statement match {
    case Statement.Instruction(value)           => encode(value, mode)
    case Statement.InstructionPair(left, right) => DualIssueCodec.encode(left, right, mode.target)
    case _ => Left(CodecError.InvalidOperand("expected an instruction or dual-issue pair"))
  }

  def decodeStatementAt(bytes: Vec[Byte], offset: Int >= 0, mode: TargetMode): Either[CodecError, DecodedStatement] =
    decodeStatementAt(Vec.toArray(bytes), offset, mode)

  def decodeStatementAt(bytes: Array[Byte], offset: Int >= 0, mode: TargetMode): Either[CodecError, DecodedStatement] =
    DualIssueCodec.decode(bytes, offset, mode.target).getOrElse(
      decodeAt(bytes, offset, mode).map(value =>
        DecodedStatement(Statement.Instruction(value.instruction), value.size)
      )
    )

  def encode(instruction: Instruction, mode: TargetMode): Either[CodecError, Vec[Byte]] = {
    val target      = mode.target
    val definitions = instruction.definition.encodings(target)
    if (definitions.isEmpty) Left(CodecError.UnsupportedInstruction(instruction.opcode, target))
    else if (definitions.exists(InstructionCodec.supports(_))) InstructionCodec.encode(instruction, mode)
    else Left(CodecError.UnsupportedFormat(instruction.opcode, target, definitions.map(_.format).distinct))
  }

  def decode(bytes: Vec[Byte], mode: TargetMode): Either[CodecError, Instruction] =
    decodeAt(Vec.toArray(bytes), 0, mode).flatMap { decoded =>
      if (decoded.size.raw == bytes.size) Right(decoded.instruction)
      else Left(CodecError.InvalidEncoding(s"trailing bytes after instruction: ${bytes.size - decoded.size}"))
    }

  private def decodeAt(bytes: Array[Byte], offset: Int >= 0, mode: TargetMode): Either[CodecError, DecodedInstruction] =
    if (offset > bytes.length || bytes.length - offset < 4)
      Left(CodecError.InvalidEncoding(s"truncated instruction at byte $offset"))
    else InstructionCodec.decode(bytes, offset, mode)
}

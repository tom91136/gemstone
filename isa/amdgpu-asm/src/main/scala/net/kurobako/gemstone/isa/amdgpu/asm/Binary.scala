package net.kurobako.gemstone.isa.amdgpu.asm

import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*

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
  ): Either[CodecError, DecodedStatement] = decodeStatementAt(Vec.toArray(bytes), offset, target, waveSize)

  def decodeStatementAt(
      bytes: Array[Byte],
      offset: Int >= 0,
      target: Target,
      waveSize: Int in (32, 64)
  ): Either[CodecError, DecodedStatement] =
    DualIssueCodec.decode(bytes, offset, target).getOrElse(
      decodeAt(bytes, offset, target, waveSize).map(value =>
        DecodedStatement(Statement.Instruction(value.instruction), value.size)
      )
    )

  def encode(
      instruction: Instruction,
      target: Target,
      waveSize: Int in (32, 64) = 32
  ): Either[CodecError, Vec[Byte]] = {
    val definitions = instruction.definition.encodings(target)
    if (definitions.isEmpty) Left(CodecError.UnsupportedInstruction(instruction.opcode, target))
    else if (definitions.exists(InstructionCodec.supports(_))) InstructionCodec.encode(instruction, target, waveSize)
    else Left(CodecError.UnsupportedFormat(instruction.opcode, target, definitions.map(_.format).distinct))
  }

  def decode(bytes: Vec[Byte], target: Target, waveSize: Int in (32, 64) = 32): Either[CodecError, Instruction] =
    decodeAt(Vec.toArray(bytes), 0, target, waveSize).flatMap { decoded =>
      if (decoded.size.raw == bytes.size) Right(decoded.instruction)
      else Left(CodecError.InvalidEncoding(s"trailing bytes after instruction: ${bytes.size - decoded.size}"))
    }

  private def decodeAt(
      bytes: Array[Byte],
      offset: Int >= 0,
      target: Target,
      waveSize: Int in (32, 64)
  ): Either[CodecError, DecodedInstruction] =
    if (offset > bytes.length || bytes.length - offset < 4)
      Left(CodecError.InvalidEncoding(s"truncated instruction at byte $offset"))
    else InstructionCodec.decode(bytes, offset, target, waveSize)
}

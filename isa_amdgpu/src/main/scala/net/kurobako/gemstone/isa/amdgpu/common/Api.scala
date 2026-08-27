package net.kurobako.gemstone.isa.amdgpu.common

/** Text parser vocabulary shared by every AMD architectural family package. */
object Parser {
  def parse(source: String): Either[ParseError, Program]                = Assembly.parse(source)
  def parseInstruction(source: String): Either[ParseError, Instruction] = Assembly.parseInstruction(source)
}

/** Canonical assembly printer shared by every AMD architectural family package. */
object Printer {
  def render(program: Program): String         = Assembly.print(program)
  def render(instruction: Instruction): String = Assembly.print(instruction)
}

/** A package-level AMD ISA family. Syntax is shared; validity, metadata, and binary encoding are family-bound. */
abstract class Architecture private[amdgpu] (private[amdgpu] val generation: Generation) {
  final def encodeSopp(instruction: Instruction, target: Target): Either[CodecError, Vector[Byte]] =
    if (target.generation != generation) Left(CodecError.WrongGeneration(target, generation))
    else Binary.encodeSopp(instruction, target)

  final def decodeSopp(bytes: Seq[Byte], target: Target): Either[CodecError, Instruction] =
    if (target.generation != generation) Left(CodecError.WrongGeneration(target, generation))
    else Binary.decodeSopp(bytes, target)

  final def lower(instruction: Instruction, target: Target): Either[LoweringError, AmdExecutable] =
    if (target.generation != generation) Left(LoweringError.WrongGeneration(target, generation))
    else AmdLowering.lower(instruction, target)
}

/** Names exported by each versioned package. */
object Api {
  export _root_.net.kurobako.gemstone.isa.amdgpu.common.AmdSemantics.*

  type Program = _root_.net.kurobako.gemstone.isa.amdgpu.common.Program
  val Program: _root_.net.kurobako.gemstone.isa.amdgpu.common.Program.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.Program
  type Statement = _root_.net.kurobako.gemstone.isa.amdgpu.common.Statement
  val Statement: _root_.net.kurobako.gemstone.isa.amdgpu.common.Statement.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.Statement
  type Instruction = _root_.net.kurobako.gemstone.isa.amdgpu.common.Instruction
  val Instruction: _root_.net.kurobako.gemstone.isa.amdgpu.common.Instruction.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.Instruction
  type InstructionDef = _root_.net.kurobako.gemstone.isa.amdgpu.common.InstructionDef
  val InstructionDef: _root_.net.kurobako.gemstone.isa.amdgpu.common.InstructionDef.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.InstructionDef
  type Decoder                = _root_.net.kurobako.gemstone.isa.amdgpu.common.Decoder
  type Metadata               = _root_.net.kurobako.gemstone.isa.amdgpu.common.Metadata
  type InstructionDecodeError = _root_.net.kurobako.gemstone.isa.amdgpu.common.InstructionDecodeError
  val InstructionDecodeError: _root_.net.kurobako.gemstone.isa.amdgpu.common.InstructionDecodeError.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.InstructionDecodeError
  type Operand = _root_.net.kurobako.gemstone.isa.amdgpu.common.Operand
  val Operand: _root_.net.kurobako.gemstone.isa.amdgpu.common.Operand.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.Operand
  type Modifier = _root_.net.kurobako.gemstone.isa.amdgpu.common.Modifier
  val Modifier: _root_.net.kurobako.gemstone.isa.amdgpu.common.Modifier.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.Modifier
  type RegisterFile = _root_.net.kurobako.gemstone.isa.amdgpu.common.RegisterFile
  val RegisterFile: _root_.net.kurobako.gemstone.isa.amdgpu.common.RegisterFile.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.RegisterFile
  type Target = _root_.net.kurobako.gemstone.isa.amdgpu.common.Target
  val Target: _root_.net.kurobako.gemstone.isa.amdgpu.common.Target.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.Target
  type ParseError  = _root_.net.kurobako.gemstone.isa.amdgpu.common.ParseError
  type Encoding    = _root_.net.kurobako.gemstone.isa.amdgpu.common.Encoding
  type OperandInfo = _root_.net.kurobako.gemstone.isa.amdgpu.common.OperandInfo
  type CodecError  = _root_.net.kurobako.gemstone.isa.amdgpu.common.CodecError
  val CodecError: _root_.net.kurobako.gemstone.isa.amdgpu.common.CodecError.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.CodecError
  type ValidationError = _root_.net.kurobako.gemstone.isa.amdgpu.common.ValidationError
  val ValidationError: _root_.net.kurobako.gemstone.isa.amdgpu.common.ValidationError.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.ValidationError
  type ValidationResult = _root_.net.kurobako.gemstone.isa.amdgpu.common.ValidationResult
  val ValidationResult: _root_.net.kurobako.gemstone.isa.amdgpu.common.ValidationResult.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.ValidationResult
  type AmdExecutable = _root_.net.kurobako.gemstone.isa.amdgpu.common.AmdExecutable
  val AmdExecutable: _root_.net.kurobako.gemstone.isa.amdgpu.common.AmdExecutable.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.AmdExecutable
  type AmdWrite = _root_.net.kurobako.gemstone.isa.amdgpu.common.AmdWrite
  val AmdWrite: _root_.net.kurobako.gemstone.isa.amdgpu.common.AmdWrite.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.AmdWrite
  type BranchCondition = _root_.net.kurobako.gemstone.isa.amdgpu.common.BranchCondition
  val BranchCondition: _root_.net.kurobako.gemstone.isa.amdgpu.common.BranchCondition.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.BranchCondition
  type WaitOperation = _root_.net.kurobako.gemstone.isa.amdgpu.common.WaitOperation
  val WaitOperation: _root_.net.kurobako.gemstone.isa.amdgpu.common.WaitOperation.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.WaitOperation
  type SemanticClass = _root_.net.kurobako.gemstone.isa.amdgpu.common.SemanticClass
  val SemanticClass: _root_.net.kurobako.gemstone.isa.amdgpu.common.SemanticClass.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.SemanticClass
  type LoweringError = _root_.net.kurobako.gemstone.isa.amdgpu.common.LoweringError
  val LoweringError: _root_.net.kurobako.gemstone.isa.amdgpu.common.LoweringError.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.LoweringError
  type WaveState = _root_.net.kurobako.gemstone.isa.amdgpu.common.WaveState
  val WaveState: _root_.net.kurobako.gemstone.isa.amdgpu.common.WaveState.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.WaveState
  val AmdLowering: _root_.net.kurobako.gemstone.isa.amdgpu.common.AmdLowering.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.AmdLowering
  val SemanticsCoverage: _root_.net.kurobako.gemstone.isa.amdgpu.common.SemanticsCoverage.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.SemanticsCoverage
  val Validator: _root_.net.kurobako.gemstone.isa.amdgpu.common.Validator.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.Validator
  val Parser: _root_.net.kurobako.gemstone.isa.amdgpu.common.Parser.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.Parser
  val Printer: _root_.net.kurobako.gemstone.isa.amdgpu.common.Printer.type =
    _root_.net.kurobako.gemstone.isa.amdgpu.common.Printer
}

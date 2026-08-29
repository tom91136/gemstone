package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.isa.ValidationResult

import cats.syntax.all.*

enum DecodeError {
  case Binary(error: BinaryError)
  case Instruction(error: ModuleDecodeError)

  def message: String = this match {
    case Binary(error)      => error.message
    case Instruction(error) => error.message
  }
}

object ISA {
  val version: SPIRVVersion             = Instruction.version
  val instructions: Col[InstructionDef] = Instruction.definitions

  def decode(module: RawModule): Either[ModuleDecodeError, Module] =
    module.instructions.zipWithIndex
      .traverse { case (instruction, index) =>
        Instruction.decode(instruction).leftMap(ModuleDecodeError.Instruction(index, _))
      }
      .map(Module(module.header, _, module.endianness))

  def decode(bytes: Col[Byte]): Either[DecodeError, Module] = for {
    raw    <- Binary.decode(bytes).leftMap(DecodeError.Binary.apply)
    module <- decode(raw).leftMap(DecodeError.Instruction.apply)
  } yield module

  def encode(module: Module): Either[BinaryError, Col[Byte]] =
    Binary.encode(RawModule(module.header, module.instructions.map(Instruction.encode), module.endianness))

  def validate(module: Module, environment: Environment): ValidationResult[ValidationIssue] =
    Validator.validate(module, environment)
}

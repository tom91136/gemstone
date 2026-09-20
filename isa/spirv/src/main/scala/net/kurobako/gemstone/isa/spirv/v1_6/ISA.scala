package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.ValidationOutcome

import cats.syntax.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

object ISA {
  enum DecodeError {
    case Binary(error: net.kurobako.gemstone.isa.spirv.v1_6.Binary.BinaryError)
    case Instruction(error: ModuleDecodeError)

    def message: String = this match {
      case Binary(error)      => error.message
      case Instruction(error) => error.message
    }
  }

  val version: SPIRVVersion             = Instruction.version
  val instructions: Vec[InstructionDef] = Instruction.definitions

  def decode(module: RawModule): Either[ModuleDecodeError, Module] =
    module.instructions.zipWithIndex
      .traverse { case (instruction, index) =>
        Instruction.decode(instruction).leftMap(error =>
          ModuleDecodeError.Instruction(index.refineUnsafe[Positive0], error)
        )
      }
      .map(Module(module.header, _, module.endianness))

  def decode(bytes: Vec[Byte]): Either[DecodeError, Module] = for {
    raw    <- Binary.decode(bytes).leftMap(DecodeError.Binary(_))
    module <- decode(raw).leftMap(DecodeError.Instruction(_))
  } yield module

  def encode(module: Module): Either[Binary.BinaryError, Vec[Byte]] =
    Binary.encode(RawModule(module.header, module.instructions.map(Instruction.encode), module.endianness))

  def validate(module: Module, environment: Validation.Environment): ValidationOutcome[Validation.Issue] =
    Validation.validate(module, environment)
}

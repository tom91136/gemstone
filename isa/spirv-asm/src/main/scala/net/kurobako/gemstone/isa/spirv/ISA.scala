package net.kurobako.gemstone.isa.spirv

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.Binary as SPIRVBinary

object ISA {
  enum DecodeError {
    case Binary(error: SPIRVBinary.BinaryError)
    case Instruction(error: ModuleDecodeError)

    def message: NonEmpty[String] = this match {
      case Binary(error)      => error.message
      case Instruction(error) => error.message
    }
  }

  val version: Version = Instruction.version

  def decode(module: RawModule): Either[ModuleDecodeError, Module] =
    module.instructions.zipWithIndex
      .traverse { case (instruction, index) =>
        Instruction.decode(instruction).leftMap(error =>
          ModuleDecodeError.Instruction(index.unsafe[Int >= 0], error)
        )
      }
      .map(Module(module.header, _, module.endianness))

  def decode(bytes: Vec[Byte]): Either[DecodeError, Module] = for {
    raw    <- Binary.decode(bytes).leftMap(DecodeError.Binary(_))
    module <- decode(raw).leftMap(DecodeError.Instruction(_))
  } yield module

  def encode(module: Module): Either[Binary.BinaryError, Vec[Byte]] =
    Binary.encode(RawModule(module.header, module.instructions.map(Instruction.encode), module.endianness))
}

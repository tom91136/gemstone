package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Col

enum ParseError {
  case Binary(error: BinaryError)
  case Decode(error: ModuleDecodeError)

  def message: String = this match {
    case Binary(error) => error.message
    case Decode(error) => error.message
  }
}

object Decoder {
  def decode(module: RawModule): Either[ModuleDecodeError, Module] = {
    val instructions = Col.newBuilder[Instruction]
    var index        = 0
    while (index < module.instructions.size)
      Instruction.decode(module.instructions(index)) match {
        case Left(error)  => return Left(ModuleDecodeError.Instruction(index, error))
        case Right(value) =>
          instructions += value
          index += 1
      }
    Right(Module(module.header, instructions.result(), module.endianness))
  }
}

object Encoder {
  def encode(module: Module): RawModule =
    RawModule(module.header, module.instructions.map(Instruction.encode), module.endianness)
}

object Parser {
  def parse(bytes: Col[Byte]): Either[ParseError, Module] =
    Binary.parse(bytes).left.map(ParseError.Binary.apply).flatMap(raw =>
      Decoder.decode(raw).left.map(ParseError.Decode.apply)
    )

  def parseRaw(bytes: Col[Byte]): Either[BinaryError, RawModule] = Binary.parse(bytes)
}

object Serializer {
  def serialize(module: Module): Either[BinaryError, Col[Byte]]       = Binary.serialize(Encoder.encode(module))
  def serializeRaw(module: RawModule): Either[BinaryError, Col[Byte]] = Binary.serialize(module)
}

object ISA {
  val version: SPIRVVersion             = Version
  val instructions: Col[InstructionDef] = Instructions

  def parse(bytes: Col[Byte]): Either[ParseError, Module]           = Parser.parse(bytes)
  def serialize(module: Module): Either[BinaryError, Col[Byte]]     = Serializer.serialize(module)
  def validateCompute(module: Module): ValidationResult             = ComputeValidator.validate(module)
  def lower(module: Module): Either[LoweringError, SPIRVExecutable] = Lowering.lower(module)
}

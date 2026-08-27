package net.kurobako.gemstone.isa.spirv.v1_6

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
    val instructions = Vector.newBuilder[Instruction]
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
  def parse(bytes: IArray[Byte]): Either[ParseError, Module] =
    Binary.parse(bytes).left.map(ParseError.Binary.apply).flatMap(raw =>
      Decoder.decode(raw).left.map(ParseError.Decode.apply)
    )

  def parseRaw(bytes: IArray[Byte]): Either[BinaryError, RawModule] = Binary.parse(bytes)
}

object Serializer {
  def serialize(module: Module): Either[BinaryError, IArray[Byte]]       = Binary.serialize(Encoder.encode(module))
  def serializeRaw(module: RawModule): Either[BinaryError, IArray[Byte]] = Binary.serialize(module)
}

object Isa {
  val version: SpirvVersion                = Version
  val instructions: Vector[InstructionDef] = Instructions

  def parse(bytes: IArray[Byte]): Either[ParseError, Module]        = Parser.parse(bytes)
  def serialize(module: Module): Either[BinaryError, IArray[Byte]]  = Serializer.serialize(module)
  def validateCompute(module: Module): ValidationResult             = ComputeValidator.validate(module)
  def lower(module: Module): Either[LoweringError, SpirvExecutable] = Lowering.lower(module)
}

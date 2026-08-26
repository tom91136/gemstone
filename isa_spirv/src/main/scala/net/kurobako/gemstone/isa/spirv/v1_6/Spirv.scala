package net.kurobako.gemstone.isa.spirv.v1_6

object Parser {
  def parse(bytes: IArray[Byte]): Either[BinaryError, Module] = Binary.parse(bytes)
}

object Serializer {
  def serialize(module: Module): Either[BinaryError, IArray[Byte]] = Binary.serialize(module)
}

object Isa {
  val version: Version                            = Version.V1_6
  val instructionCatalog: Vector[InstructionInfo] = InstructionCatalog.instructions

  def parse(bytes: IArray[Byte]): Either[BinaryError, Module]       = Parser.parse(bytes)
  def serialize(module: Module): Either[BinaryError, IArray[Byte]]  = Serializer.serialize(module)
  def validateCompute(module: Module): ValidationResult             = ComputeValidator.validate(module)
  def lower(module: Module): Either[LoweringError, SpirvExecutable] = Lowering.lower(module)
}

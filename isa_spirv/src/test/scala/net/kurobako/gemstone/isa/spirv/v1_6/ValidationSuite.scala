package net.kurobako.gemstone.isa.spirv.v1_6

final class ValidationSuite extends munit.FunSuite {
  test("OpenCL compute environment is accepted") {
    val module = Module(
      Header(Version, Word(0), Word(10), Word(0)),
      Vector(
        Instruction.Capability(operand("Capability", 6)),
        Instruction.MemoryModel(operand("AddressingModel", 2), operand("MemoryModel", 2)),
        Instruction.EntryPoint(
          operand("ExecutionModel", 6),
          operand("IdRef", 1),
          operand("LiteralString", 0),
          Vector.empty
        )
      ),
      Endianness.Little
    )
    assertEquals(ComputeValidator.validate(module), ValidationResult.Valid)
  }

  test("non-compute module fails closed") {
    val module = Module(Header(Version, Word(0), Word(1), Word(0)), Vector.empty, Endianness.Little)
    ComputeValidator.validate(module) match {
      case ValidationResult.Invalid(issues) => assertEquals(issues.size, 3)
      case other                            => fail(s"expected invalid, found $other")
    }
  }

  test("unknown raw opcode fails before validation") {
    val raw = RawModule(
      Header(Version, Word(0), Word(10), Word(0)),
      Vector(RawInstruction(65535, Vector.empty)),
      Endianness.Little
    )
    assertEquals(
      Decoder.decode(raw),
      Left(ModuleDecodeError.Instruction(0, InstructionDecodeError.UnknownOpcode(65535)))
    )
  }

  private def operand(kind: String, value: Int): Operand = Operand(kind, Vector(Word(value)))
}

package net.kurobako.gemstone.isa.spirv.v1_6

final class ValidationSuite extends munit.FunSuite {
  test("OpenCL compute environment is accepted") {
    val module = Module(
      Header(Version.V1_6, Word(0), Word(10), Word(0)),
      Vector(
        instruction("OpCapability", 6),
        instruction("OpMemoryModel", 2, 2),
        instruction("OpEntryPoint", 6, 1, 0)
      ),
      Endianness.Little
    )
    assertEquals(ComputeValidator.validate(module), ValidationResult.Valid)
  }

  test("non-compute module fails closed") {
    val module = Module(Header(Version.V1_6, Word(0), Word(1), Word(0)), Vector.empty, Endianness.Little)
    ComputeValidator.validate(module) match {
      case ValidationResult.Invalid(issues) => assertEquals(issues.size, 3)
      case other                            => fail(s"expected invalid, found $other")
    }
  }

  test("unknown opcode is explicitly unsupported") {
    val module = Module(
      Header(Version.V1_6, Word(0), Word(10), Word(0)),
      Vector(
        instruction("OpCapability", 6),
        instruction("OpMemoryModel", 2, 2),
        instruction("OpEntryPoint", 6, 1, 0),
        Instruction(Opcode.unsafe(65535), Vector.empty)
      ),
      Endianness.Little
    )
    assert(ComputeValidator.validate(module).isInstanceOf[ValidationResult.Unsupported])
  }

  private def instruction(name: String, operands: Int*): Instruction = {
    val info = InstructionCatalog.byName.getOrElse(name, fail(s"missing $name"))
    Instruction(info.opcode, operands.toVector.map(Word.apply))
  }
}

package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.isa.ValidationResult

final class ValidationSuite extends munit.FunSuite {
  test("OpenCL compute environment is accepted") {
    val module = Module(
      Header(ISA.version, Word(0), Word(10), Word(0)),
      Col(
        Instruction.Capability(operand("Capability", 6)),
        Instruction.MemoryModel(operand("AddressingModel", 2), operand("MemoryModel", 2)),
        Instruction.EntryPoint(
          operand("ExecutionModel", 6),
          operand("IdRef", 1),
          operand("LiteralString", 0),
          Col.empty
        )
      ),
      Endianness.Little
    )
    assertEquals(Validator.validate(module, Environment.OpenCLCompute), ValidationResult.Valid)
  }

  test("non-compute module fails closed") {
    val module = Module(Header(ISA.version, Word(0), Word(1), Word(0)), Col.empty, Endianness.Little)
    Validator.validate(module, Environment.OpenCLCompute) match {
      case ValidationResult.Invalid(issues) => assertEquals(issues.size, 3)
      case other                            => fail(s"expected invalid, found $other")
    }
  }

  test("OpenCL libraries may omit an entry point but still require the OpenCL environment") {
    val module = Module(
      Header(ISA.version, Word(0), Word(1), Word(0)),
      Col(
        Instruction.Capability(operand("Capability", 6)),
        Instruction.MemoryModel(operand("AddressingModel", 2), operand("MemoryModel", 2))
      ),
      Endianness.Little
    )
    assertEquals(Validator.validate(module, Environment.OpenCLLibrary), ValidationResult.Valid)
  }

  test("unknown raw opcode fails before validation") {
    val raw = RawModule(
      Header(ISA.version, Word(0), Word(10), Word(0)),
      Col(RawInstruction(65535, Col.empty)),
      Endianness.Little
    )
    assertEquals(
      ISA.decode(raw),
      Left(ModuleDecodeError.Instruction(0, InstructionDecodeError.UnknownOpcode(65535)))
    )
  }

  private def operand(kind: String, value: Int): Operand = Operand(kind, Col(Word(value)))
}

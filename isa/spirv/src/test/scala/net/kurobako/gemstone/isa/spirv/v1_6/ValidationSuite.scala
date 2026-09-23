package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.ValidationOutcome
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class ValidationSuite extends munit.FunSuite {
  test("OpenCL compute environment is accepted") {
    val module = Module(
      Header(ISA.version, Word(0), Word(10), Word(0)),
      Vec(
        Instruction.Capability(operand("Capability", 6)),
        Instruction.MemoryModel(operand("AddressingModel", 2), operand("MemoryModel", 2)),
        Instruction.EntryPoint(
          operand("ExecutionModel", 6),
          operand("IdRef", 1),
          operand("LiteralString", 0),
          Vec.empty
        )
      ),
      Endianness.Little
    )
    assertEquals(Validation.validate(module, Validation.Environment.OpenCLCompute), ValidationOutcome.Accepted)
  }

  test("Vulkan compute environment accepts Shader GLCompute with Logical GLSL450 memory") {
    val module = Module(
      Header(ISA.version, Word(0), Word(10), Word(0)),
      Vec(
        Instruction.Capability(operand("Capability", 1)),
        Instruction.MemoryModel(operand("AddressingModel", 0), operand("MemoryModel", 1)),
        Instruction.EntryPoint(
          operand("ExecutionModel", 5),
          operand("IdRef", 1),
          operand("LiteralString", 0),
          Vec.empty
        )
      ),
      Endianness.Little
    )
    assertEquals(Validation.validate(module, Validation.Environment.VulkanCompute), ValidationOutcome.Accepted)
    assert(Validation.validate(module, Validation.Environment.OpenCLCompute) != ValidationOutcome.Accepted)
  }

  test("non-compute module fails closed") {
    val module = Module(Header(ISA.version, Word(0), Word(1), Word(0)), Vec.empty, Endianness.Little)
    Validation.validate(module, Validation.Environment.OpenCLCompute) match {
      case ValidationOutcome.Rejected(issues) => assertEquals(issues.size, 3)
      case other                              => fail(s"expected invalid, found $other")
    }
  }

  test("OpenCL libraries may omit an entry point but still require the OpenCL environment") {
    val module = Module(
      Header(ISA.version, Word(0), Word(1), Word(0)),
      Vec(
        Instruction.Capability(operand("Capability", 6)),
        Instruction.MemoryModel(operand("AddressingModel", 2), operand("MemoryModel", 2))
      ),
      Endianness.Little
    )
    assertEquals(Validation.validate(module, Validation.Environment.OpenCLLibrary), ValidationOutcome.Accepted)
  }

  test("unknown raw opcode fails before validation") {
    val unknownOpcode = SPIRVOpcodeNumber(65535)
    val raw           = RawModule(
      Header(ISA.version, Word(0), Word(10), Word(0)),
      Vec(RawInstruction(unknownOpcode, Vec.empty)),
      Endianness.Little
    )
    assertEquals(
      ISA.decode(raw),
      Left(ModuleDecodeError.Instruction(0, InstructionDecodeError.UnknownOpcode(unknownOpcode)))
    )
  }

  private def operand(kind: String, value: Int): Operand = Operand(
    SPIRVOperandKind.either(kind).fold(message => fail(message), valid => valid),
    Vec.nonEmpty(Word(value))
  )
}

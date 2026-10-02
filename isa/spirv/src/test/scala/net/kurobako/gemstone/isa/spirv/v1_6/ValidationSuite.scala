package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv

class ValidationSuite extends munit.FunSuite {
  test("OpenCL compute environment is accepted") {
    val module = Module(
      Header(ISA.version, Word(0), Word(10), Word(0)),
      Vec(
        Instruction.Capability(operand(OperandKind.Capability, spv.Capability.Kernel)),
        Instruction.MemoryModel(
          operand(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
          operand(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
        ),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          operand(OperandKind.IdRef, 1),
          operand(OperandKind.LiteralString, 0),
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
        Instruction.Capability(operand(OperandKind.Capability, spv.Capability.Shader)),
        Instruction.MemoryModel(
          operand(OperandKind.AddressingModel, spv.AddressingModel.Logical),
          operand(OperandKind.MemoryModel, spv.MemoryModel.GLSL450)
        ),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.GLCompute),
          operand(OperandKind.IdRef, 1),
          operand(OperandKind.LiteralString, 0),
          Vec.empty
        )
      ),
      Endianness.Little
    )
    assertEquals(Validation.validate(module, Validation.Environment.VulkanCompute), ValidationOutcome.Accepted)
    assert(Validation.validate(module, Validation.Environment.OpenCLCompute) != ValidationOutcome.Accepted)
  }

  test("non-compute module fails closed") {
    val module =
      Module(Header(ISA.version, Word(0), Word(1), Word(0)), Vec.empty, Endianness.Little)
    Validation.validate(module, Validation.Environment.OpenCLCompute) match {
      case ValidationOutcome.Rejected(issues) => assertEquals(issues.length, 3)
      case other                              => fail(s"expected invalid, found $other")
    }
  }

  test("OpenCL libraries may omit an entry point but still require the OpenCL environment") {
    val module = Module(
      Header(ISA.version, Word(0), Word(1), Word(0)),
      Vec(
        Instruction.Capability(operand(OperandKind.Capability, spv.Capability.Kernel)),
        Instruction.MemoryModel(
          operand(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
          operand(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
        )
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

  private def operand(kind: OperandKind, value: Int): Operand = Operand(
    kind,
    Vec.nonEmpty(Word(value))
  )
}

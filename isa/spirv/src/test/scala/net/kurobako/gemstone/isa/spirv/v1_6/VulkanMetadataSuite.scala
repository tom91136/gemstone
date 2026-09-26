package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.{NonNegativeInt, PositiveInt, Vec}
import net.kurobako.gemstone.isa.{KernelMetadata, KernelName}

final class VulkanMetadataSuite extends munit.FunSuite with ExecutionTestSupport {
  private def function(entry: Operand): Vec[Instruction] =
    Vec(
      Instruction.Function(operand("IdResultType", 1), entry, operand("FunctionControl", 0), operand("IdRef", 2)),
      Instruction.FunctionEnd()
    )

  private def metadata(module: Module, name: String): KernelMetadata =
    loadUnchecked(module).fold(error => fail(error.message), identity) match {
      case loaded =>
        val kernelName = KernelName.either(name).fold(message => fail(message), identity)
        VMInterpreter.kernelMetadata(loaded, kernelName).fold(error => fail(error.message), identity)
    }

  private def entryPoint(entry: Operand): Instruction =
    Instruction.EntryPoint(operand("ExecutionModel", 5), entry, stringOperand("main"), Vec.empty)

  test("required work-group size comes from LocalSize") {
    val entry  = operand("IdResult", 7)
    val module = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      Vec(entryPoint(entry), Instruction.ExecutionMode(entry, operand("ExecutionMode", 17, 8, 2, 1))) ++
        function(entry),
      Endianness.Little
    )
    assertEquals(
      metadata(module, "main").requiredWorkGroupSize,
      Some(KernelMetadata.WorkGroupSize(PositiveInt(8), PositiveInt(2), PositiveInt(1)))
    )
  }

  test("required work-group size comes from LocalSizeId constants") {
    val entry  = operand("IdResult", 7)
    val module = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      Vec(
        entryPoint(entry),
        Instruction.TypeInt(operand("IdResult", 2), operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.SpecConstant(operand("IdResult", 2), operand("IdResult", 20), operand("LiteralInteger", 4)),
        Instruction.SpecConstant(operand("IdResult", 2), operand("IdResult", 21), operand("LiteralInteger", 3)),
        Instruction.SpecConstant(operand("IdResult", 2), operand("IdResult", 22), operand("LiteralInteger", 2)),
        Instruction.ExecutionModeId(entry, operand("ExecutionMode", 38, 20, 21, 22))
      ) ++ function(entry),
      Endianness.Little
    )
    assertEquals(
      metadata(module, "main").requiredWorkGroupSize,
      Some(KernelMetadata.WorkGroupSize(PositiveInt(4), PositiveInt(3), PositiveInt(2)))
    )
  }

  test("required work-group size comes from the WorkgroupSize builtin") {
    val entry  = operand("IdResult", 7)
    val module = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      Vec(
        entryPoint(entry),
        Instruction.TypeInt(operand("IdResult", 2), operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeVector(operand("IdResult", 3), operand("IdResult", 2), operand("LiteralInteger", 3)),
        Instruction.SpecConstant(operand("IdResult", 2), operand("IdResult", 20), operand("LiteralInteger", 4)),
        Instruction.SpecConstant(operand("IdResult", 2), operand("IdResult", 21), operand("LiteralInteger", 3)),
        Instruction.SpecConstant(operand("IdResult", 2), operand("IdResult", 22), operand("LiteralInteger", 2)),
        Instruction.SpecConstantComposite(
          operand("IdResult", 3),
          operand("IdResult", 30),
          Vec(operand("IdRef", 20), operand("IdRef", 21), operand("IdRef", 22))
        ),
        Instruction.Decorate(operand("IdRef", 30), operand("Decoration", 11, 25))
      ) ++ function(entry),
      Endianness.Little
    )
    assertEquals(
      metadata(module, "main").requiredWorkGroupSize,
      Some(KernelMetadata.WorkGroupSize(PositiveInt(4), PositiveInt(3), PositiveInt(2)))
    )
  }

  test("descriptor bindings are scoped to the selected entry point") {
    val first  = operand("IdResult", 10)
    val second = operand("IdResult", 20)
    val left   = operand("IdResult", 30)
    val right  = operand("IdResult", 31)
    val module = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      Vec(
        Instruction.EntryPoint(operand("ExecutionModel", 5), first, stringOperand("first"), Vec(left)),
        Instruction.EntryPoint(operand("ExecutionModel", 5), second, stringOperand("second"), Vec(right)),
        Instruction.Decorate(left, operand("Decoration", 33, 3)),
        Instruction.Decorate(left, operand("Decoration", 34, 0)),
        Instruction.Decorate(right, operand("Decoration", 33, 5)),
        Instruction.Decorate(right, operand("Decoration", 34, 0))
      ) ++ function(first) ++ function(second),
      Endianness.Little
    )
    assertEquals(metadata(module, "first").bindingIndices, Vec(NonNegativeInt(3)))
    assertEquals(metadata(module, "second").bindingIndices, Vec(NonNegativeInt(5)))
  }
}

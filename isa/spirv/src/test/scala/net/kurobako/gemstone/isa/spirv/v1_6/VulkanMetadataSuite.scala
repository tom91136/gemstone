package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.vm.*

class VulkanMetadataSuite extends munit.FunSuite {
  private def function(entry: Operand): Vec[Instruction] =
    Vec(
      Instruction.Function(
        operand(OperandKind.IdResultType, 1),
        entry,
        operand(OperandKind.FunctionControl, spv.FunctionControl.None),
        operand(OperandKind.IdRef, 2)
      ),
      Instruction.FunctionEnd()
    )

  private def metadata(module: Module, name: String): KernelMetadata =
    loadUnchecked(module).fold(error => fail(error.message), identity) match {
      case loaded =>
        val kernelName =
          name.refine[NonEmpty[String]].fold(message => fail(message), identity)
        loaded.kernel(kernelName).fold(error => fail(error.message), identity)
    }

  private def entryPoint(entry: Operand): Instruction =
    Instruction.EntryPoint(
      operand(OperandKind.ExecutionModel, spv.ExecutionModel.GLCompute),
      entry,
      stringOperand("main"),
      Vec.empty
    )

  test("required work-group size comes from LocalSize") {
    val entry  = operand(OperandKind.IdResult, 7)
    val module = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      Vec(
        entryPoint(entry),
        Instruction.ExecutionMode(entry, operand(OperandKind.ExecutionMode, spv.ExecutionMode.LocalSize, 8, 2, 1))
      ) ++
        function(entry),
      Endianness.Little
    )
    assertEquals(
      metadata(module, "main").requiredWorkGroupSize,
      Some(LaunchGeometry.Dimensions(8, 2, 1))
    )
  }

  test("required work-group size comes from LocalSizeId constants") {
    val entry  = operand(OperandKind.IdResult, 7)
    val module = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      Vec(
        entryPoint(entry),
        Instruction.TypeInt(
          operand(OperandKind.IdResult, 2),
          operand(OperandKind.LiteralInteger, 32),
          operand(OperandKind.LiteralInteger, 0)
        ),
        Instruction.SpecConstant(
          operand(OperandKind.IdResult, 2),
          operand(OperandKind.IdResult, 20),
          operand(OperandKind.LiteralInteger, 4)
        ),
        Instruction.SpecConstant(
          operand(OperandKind.IdResult, 2),
          operand(OperandKind.IdResult, 21),
          operand(OperandKind.LiteralInteger, 3)
        ),
        Instruction.SpecConstant(
          operand(OperandKind.IdResult, 2),
          operand(OperandKind.IdResult, 22),
          operand(OperandKind.LiteralInteger, 2)
        ),
        Instruction.ExecutionModeId(
          entry,
          operand(OperandKind.ExecutionMode, spv.ExecutionMode.LocalSizeId, 20, 21, 22)
        )
      ) ++ function(entry),
      Endianness.Little
    )
    assertEquals(
      metadata(module, "main").requiredWorkGroupSize,
      Some(LaunchGeometry.Dimensions(4, 3, 2))
    )
  }

  test("WorkgroupSize builtin overrides a conflicting LocalSize execution mode") {
    val entry  = operand(OperandKind.IdResult, 7)
    val module = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      Vec(
        entryPoint(entry),
        Instruction.ExecutionMode(entry, operand(OperandKind.ExecutionMode, 17, 1, 1, 1)),
        Instruction.TypeInt(
          operand(OperandKind.IdResult, 2),
          operand(OperandKind.LiteralInteger, 32),
          operand(OperandKind.LiteralInteger, 0)
        ),
        Instruction.TypeVector(
          operand(OperandKind.IdResult, 3),
          operand(OperandKind.IdResult, 2),
          operand(OperandKind.LiteralInteger, 3)
        ),
        Instruction.SpecConstant(
          operand(OperandKind.IdResult, 2),
          operand(OperandKind.IdResult, 20),
          operand(OperandKind.LiteralInteger, 4)
        ),
        Instruction.SpecConstant(
          operand(OperandKind.IdResult, 2),
          operand(OperandKind.IdResult, 21),
          operand(OperandKind.LiteralInteger, 3)
        ),
        Instruction.SpecConstant(
          operand(OperandKind.IdResult, 2),
          operand(OperandKind.IdResult, 22),
          operand(OperandKind.LiteralInteger, 2)
        ),
        Instruction.SpecConstantComposite(
          operand(OperandKind.IdResult, 3),
          operand(OperandKind.IdResult, 30),
          Vec(operand(OperandKind.IdRef, 20), operand(OperandKind.IdRef, 21), operand(OperandKind.IdRef, 22))
        ),
        Instruction.Decorate(
          operand(OperandKind.IdRef, 30),
          operand(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.WorkgroupSize)
        )
      ) ++ function(entry),
      Endianness.Little
    )
    assertEquals(
      metadata(module, "main").requiredWorkGroupSize,
      Some(LaunchGeometry.Dimensions(4, 3, 2))
    )
  }

  test("invalid WorkgroupSize constants fail during loading before metadata is requested") {
    val entry = operand(OperandKind.IdResult, 7)
    for {
      invalid <- Vec(0L, 0x80000000L, 0x100000001L)
      axis    <- 0 until 3
    } {
      val dimensions = Vec(4L, 3L, 2L).updated(axis, invalid)
      val module     = Module(
        Header(ISA.version, Word(0), Word(64), Word(0)),
        Vec(
          entryPoint(entry),
          Instruction.TypeInt(
            operand(OperandKind.IdResult, 2),
            operand(OperandKind.LiteralInteger, 64),
            operand(OperandKind.LiteralInteger, 0)
          ),
          Instruction.TypeVector(
            operand(OperandKind.IdResult, 3),
            operand(OperandKind.IdResult, 2),
            operand(OperandKind.LiteralInteger, 3)
          )
        ) ++ Vec.from(dimensions.zipWithIndex.map { case (value, index) =>
          Instruction.SpecConstant(
            operand(OperandKind.IdResult, 2),
            operand(OperandKind.IdResult, 20 + index),
            operand(OperandKind.LiteralInteger, value.toInt, (value >>> 32).toInt)
          )
        }) ++ Vec(
          Instruction.SpecConstantComposite(
            operand(OperandKind.IdResult, 3),
            operand(OperandKind.IdResult, 30),
            Vec(operand(OperandKind.IdRef, 20), operand(OperandKind.IdRef, 21), operand(OperandKind.IdRef, 22))
          ),
          Instruction.Decorate(
            operand(OperandKind.IdRef, 30),
            operand(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.WorkgroupSize)
          )
        ) ++ function(entry),
        Endianness.Little
      )
      loadUnchecked(module) match {
        case Left(Fault.InvalidTask(message)) => assert(message.contains("WorkgroupSize"))
        case other => fail(s"expected invalid dimensions for axis $axis and value $invalid, got $other")
      }
    }
  }

  test("unsigned descriptor indices beyond the supported range return a loading error") {
    val entry    = operand(OperandKind.IdResult, 7)
    val variable = operand(OperandKind.IdRef, 30)
    for (decoration <- Vec(spv.Decoration.Binding, spv.Decoration.DescriptorSet)) {
      val module = Module(
        Header(ISA.version, Word(0), Word(64), Word(0)),
        Vec(entryPoint(entry), Instruction.Decorate(variable, operand(OperandKind.Decoration, decoration, -1))) ++
          function(entry),
        Endianness.Little
      )
      loadUnchecked(module) match {
        case Left(Fault.Unsupported(message)) => assert(message.contains("4294967295"))
        case other                            => fail(s"expected unsupported descriptor index, got $other")
      }
    }
  }

  test("descriptor bindings are scoped to the selected entry point") {
    val first  = operand(OperandKind.IdResult, 10)
    val second = operand(OperandKind.IdResult, 20)
    val left   = operand(OperandKind.IdResult, 30)
    val right  = operand(OperandKind.IdResult, 31)
    val module = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      Vec(
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.GLCompute),
          first,
          stringOperand("first"),
          Vec(left)
        ),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.GLCompute),
          second,
          stringOperand("second"),
          Vec(right)
        ),
        Instruction.Decorate(left, operand(OperandKind.Decoration, spv.Decoration.Binding, 3)),
        Instruction.Decorate(left, operand(OperandKind.Decoration, spv.Decoration.DescriptorSet, 0)),
        Instruction.Decorate(right, operand(OperandKind.Decoration, spv.Decoration.Binding, 5)),
        Instruction.Decorate(right, operand(OperandKind.Decoration, spv.Decoration.DescriptorSet, 0))
      ) ++ function(first) ++ function(second),
      Endianness.Little
    )
    assertEquals(metadata(module, "first").bindingIndices, Vec[Int >= 0](3))
    assertEquals(metadata(module, "second").bindingIndices, Vec[Int >= 0](5))
  }
}

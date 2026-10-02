package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class VulkanGraphicsSuite extends munit.FunSuite {
  private def stage(model: Int): Module = Module(
    Header(ISA.version, Word(0), Word(32), Word(0)),
    Vec(
      Instruction.Capability(operand(OperandKind.Capability, Capability.Shader)),
      Instruction.MemoryModel(
        operand(OperandKind.AddressingModel, AddressingModel.Logical),
        operand(OperandKind.MemoryModel, MemoryModel.GLSL450)
      ),
      Instruction.EntryPoint(
        operand(OperandKind.ExecutionModel, model),
        operand(OperandKind.IdRef, 10),
        stringOperand("main"),
        Vec(operand(OperandKind.IdRef, 6), operand(OperandKind.IdRef, 7))
      ),
      Instruction.TypeVoid(operand(OperandKind.IdResult, 1)),
      Instruction.TypeFloat(operand(OperandKind.IdResult, 2), operand(OperandKind.LiteralInteger, 32), None),
      Instruction.TypePointer(
        operand(OperandKind.IdResult, 3),
        operand(OperandKind.StorageClass, StorageClass.Input),
        operand(OperandKind.IdRef, 2)
      ),
      Instruction.TypePointer(
        operand(OperandKind.IdResult, 4),
        operand(OperandKind.StorageClass, StorageClass.Output),
        operand(OperandKind.IdRef, 2)
      ),
      Instruction.TypeFunction(operand(OperandKind.IdResult, 5), operand(OperandKind.IdRef, 1), Vec.empty),
      Instruction.Variable(
        operand(OperandKind.IdResultType, 3),
        operand(OperandKind.IdResult, 6),
        operand(OperandKind.StorageClass, StorageClass.Input),
        None
      ),
      Instruction.Variable(
        operand(OperandKind.IdResultType, 4),
        operand(OperandKind.IdResult, 7),
        operand(OperandKind.StorageClass, StorageClass.Output),
        None
      ),
      Instruction.Function(
        operand(OperandKind.IdResultType, 1),
        operand(OperandKind.IdResult, 10),
        operand(OperandKind.FunctionControl, FunctionControl.None),
        operand(OperandKind.IdRef, 5)
      ),
      Instruction.Label(operand(OperandKind.IdResult, 11)),
      Instruction.Load(
        operand(OperandKind.IdResultType, 2),
        operand(OperandKind.IdResult, 12),
        operand(OperandKind.IdRef, 6),
        None
      ),
      Instruction.FMul(
        operand(OperandKind.IdResultType, 2),
        operand(OperandKind.IdResult, 13),
        operand(OperandKind.IdRef, 12),
        operand(OperandKind.IdRef, 12)
      ),
      Instruction.Store(operand(OperandKind.IdRef, 7), operand(OperandKind.IdRef, 13), None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    ),
    Endianness.Little
  )

  for (model <- List(0, 4)) test(s"graphics stage $model keeps invocation input and output records distinct") {

    val program = LoadedProgram.load(
      stage(model),
      Validation.Environment.VulkanGraphics
    ).fold(error => fail(error.message), identity)
    val region = AllocationId(701L)
    val memory = ArrayMemory(region, 24)
    for ((value, index) <- List(0x40000000L, 0x40400000L, 0x40800000L).zipWithIndex)
      assertEquals(
        memory.write(region, (index * 4L).unsafe[Long >= 0], LittleEndian.toVec(value, 4)),
        Right(())
      )
    val task = KernelTask.validated(
      "main",
      LaunchGeometry.Dimensions.One,
      testDimensions(3),
      Vec(
        KernelTask.Argument.Memory(region, 0L),
        KernelTask.Argument.Memory(region, 12L)
      )
    ).fold(fail(_), identity)
    assert(program.execute(task, memory, TaskControl()).isRight)
    assertEquals(
      memory.bytes.drop(12),
      Vec.from(List(0x40800000L, 0x41100000L, 0x41800000L).flatMap(value =>
        LittleEndian.toVec(value, 4).toSeq
      ))
    )
  }

  test("compute environment rejects graphics entry points") {

    assert(LoadedProgram.load(stage(0), Validation.Environment.VulkanCompute).isLeft)
    assert(LoadedProgram.load(stage(4), Validation.Environment.VulkanCompute).isLeft)
  }
}

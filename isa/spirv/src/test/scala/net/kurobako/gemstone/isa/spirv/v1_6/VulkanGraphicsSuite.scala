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
      Instruction.Capability(operand("Capability", Capability.Shader)),
      Instruction.MemoryModel(
        operand("AddressingModel", AddressingModel.Logical),
        operand("MemoryModel", MemoryModel.GLSL450)
      ),
      Instruction.EntryPoint(
        operand("ExecutionModel", model),
        operand("IdRef", 10),
        stringOperand("main"),
        Vec(operand("IdRef", 6), operand("IdRef", 7))
      ),
      Instruction.TypeVoid(operand("IdResult", 1)),
      Instruction.TypeFloat(operand("IdResult", 2), operand("LiteralInteger", 32), None),
      Instruction.TypePointer(operand("IdResult", 3), operand("StorageClass", StorageClass.Input), operand("IdRef", 2)),
      Instruction.TypePointer(
        operand("IdResult", 4),
        operand("StorageClass", StorageClass.Output),
        operand("IdRef", 2)
      ),
      Instruction.TypeFunction(operand("IdResult", 5), operand("IdRef", 1), Vec.empty),
      Instruction.Variable(
        operand("IdResultType", 3),
        operand("IdResult", 6),
        operand("StorageClass", StorageClass.Input),
        None
      ),
      Instruction.Variable(
        operand("IdResultType", 4),
        operand("IdResult", 7),
        operand("StorageClass", StorageClass.Output),
        None
      ),
      Instruction.Function(
        operand("IdResultType", 1),
        operand("IdResult", 10),
        operand("FunctionControl", FunctionControl.None),
        operand("IdRef", 5)
      ),
      Instruction.Label(operand("IdResult", 11)),
      Instruction.Load(operand("IdResultType", 2), operand("IdResult", 12), operand("IdRef", 6), None),
      Instruction.FMul(operand("IdResultType", 2), operand("IdResult", 13), operand("IdRef", 12), operand("IdRef", 12)),
      Instruction.Store(operand("IdRef", 7), operand("IdRef", 13), None),
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

package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class ProgramExecutionSuite extends munit.FunSuite {
  private object EmptyMemory extends Memory {
    override def read(
        region: AllocationId,
        offset: Long >= 0,
        size: Int >= 0
    ): Either[Fault, Vec[Byte]] = Left(Fault.Memory("unexpected read"))

    override def write(
        region: AllocationId,
        offset: Long >= 0,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] = Left(Fault.Memory("unexpected write"))
  }

  test("loads an explicit environment and executes a kernel task") {
    val voidType     = operand("IdResult", 1)
    val functionType = operand("IdResult", 2)
    val function     = operand("IdResult", 3)
    val module       = Module(
      Header(ISA.version, Word(0), Word(5), Word(0)),
      Vec(
        Instruction.Capability(operand("Capability", spv.Capability.Kernel)),
        Instruction.MemoryModel(
          operand("AddressingModel", spv.AddressingModel.Physical64),
          operand("MemoryModel", spv.MemoryModel.OpenCL)
        ),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFunction(functionType, voidType, Vec.empty),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("empty"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType),
        Instruction.Label(operand("IdResult", 4)),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )

    val loaded = LoadedProgram.load(
      module,
      Validation.Environment.OpenCLCompute
    ).fold(error => fail(error.message), value => value)
    val task = KernelTask.validated(
      "empty",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec.empty
    ).fold(message => fail(message), value => value)
    val result = loaded.execute(task, EmptyMemory, TaskControl())
      .fold(error => fail(error.message), value => value)
    assertEquals(result, ())
  }

  private def operand(kind: NonEmpty[String], value: Int): Operand =
    Operand(kind, Vec.nonEmpty(Word(value)))

  private def stringOperand(value: String): Operand = {
    val bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8) ++ Array(0.toByte)
    val words = bytes.grouped(4).map { group =>
      Word(group.zipWithIndex.foldLeft(0) { case (word, (byte, index)) =>
        word | ((byte & 0xff) << (index * 8))
      })
    }.toSeq
    Operand("LiteralString", Vec.from(words).unsafe)
  }
}

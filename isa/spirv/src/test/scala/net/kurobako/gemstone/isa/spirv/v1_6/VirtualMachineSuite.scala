package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.vm.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class VirtualMachineSuite extends munit.FunSuite {
  private object EmptyMemory extends Memory {
    override def read(
        region: MemoryRegion,
        offset: Long :| Positive0,
        size: Int :| Positive0
    ): Either[Fault, Vec[Byte]] = Left(Fault.Memory("unexpected read"))

    override def write(
        region: MemoryRegion,
        offset: Long :| Positive0,
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
        Instruction.Capability(operand("Capability", 6)),
        Instruction.MemoryModel(operand("AddressingModel", 2), operand("MemoryModel", 2)),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFunction(functionType, voidType, Vec.empty),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("empty"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.Label(operand("IdResult", 4)),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val machine = VirtualMachine(Validation.Environment.OpenCLCompute, Observer.discard, Scheduler.Sequential)
    val loaded  = machine.load(module).fold(error => fail(error.message), value => value)
    val task    = KernelTask.validated(
      KernelName("empty"),
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec.empty
    ).fold(message => fail(message), value => value)
    val result = machine.execute(loaded, task, EmptyMemory, TaskControl.default)
      .fold(error => fail(error.message), value => value)
    assert(result.instructions > 0L)
  }

  private def operand(kind: String :| (MinLength[1] & NoNul), value: Int): Operand =
    Operand(SPIRVOperandKind(kind), Vec(Word(value)).refineUnsafe[MinLength[1]])

  private def stringOperand(value: String): Operand = {
    val bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8) ++ Array(0.toByte)
    val words = bytes.grouped(4).map { group =>
      Word(group.zipWithIndex.foldLeft(0) { case (word, (byte, index)) =>
        word | ((byte & 0xff) << (index * 8))
      })
    }.toSeq
    Operand(SPIRVOperandKind("LiteralString"), Vec.from(words).refineUnsafe[MinLength[1]])
  }
}

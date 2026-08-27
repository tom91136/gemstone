package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.execution.ExecutionFault
import net.kurobako.gemstone.execution.ExecutionLimits

final class ExecutionSuite extends munit.FunSuite {
  test("integer and memory effects execute through typed instructions") {
    val resultType = operand("IdResultType", 1)
    val module     = Module(
      Header(Version, Word(0), Word(20), Word(0)),
      Vector(
        Instruction.Constant(resultType, operand("IdResult", 2), operand("LiteralContextDependentNumber", 40)),
        Instruction.Constant(resultType, operand("IdResult", 3), operand("LiteralContextDependentNumber", 2)),
        Instruction.IAdd(resultType, operand("IdResult", 4), operand("IdRef", 2), operand("IdRef", 3)),
        Instruction.Variable(resultType, operand("IdResult", 6), operand("StorageClass", 7), Some(operand("IdRef", 4))),
        Instruction.Load(resultType, operand("IdResult", 8), operand("IdRef", 6), None),
        Instruction.Return
      ),
      Endianness.Little
    )
    val executable = Lowering.lower(module).fold(error => fail(error.message), identity)
    Interpreter.run(executable) match {
      case ExecutionResult.Completed(values, memory, instructions) =>
        assertEquals(values(4), 42L)
        assertEquals(values(8), 42L)
        assertEquals(memory(6L), 42L)
        assertEquals(instructions, 6L)
      case other => fail(s"expected completion, found $other")
    }
  }

  test("unsupported typed semantic family traps explicitly") {
    val module = Module(
      Header(Version, Word(0), Word(5), Word(0)),
      Vector(
        Instruction.ControlBarrier(operand("IdScope", 1), operand("IdScope", 2), operand("MemorySemantics", 3)),
        Instruction.Return
      ),
      Endianness.Little
    )
    val executable = Lowering.lower(module).fold(error => fail(error.message), identity)
    Interpreter.run(executable) match {
      case ExecutionResult.Fault(ExecutionFault.Unsupported("OpControlBarrier", _), _, _, 1L) => ()
      case other => fail(s"expected unsupported trap, found $other")
    }
  }

  test("instruction budget stops typed branch loops") {
    val module = Module(
      Header(Version, Word(0), Word(5), Word(0)),
      Vector(Instruction.Label(operand("IdResult", 1)), Instruction.Branch(operand("IdRef", 1))),
      Endianness.Little
    )
    val executable = Lowering.lower(module).fold(error => fail(error.message), identity)
    Interpreter.run(executable, limits = ExecutionLimits(5L, 2)) match {
      case ExecutionResult.Fault(ExecutionFault.BudgetExceeded(5L), _, _, 5L) => ()
      case other => fail(s"expected budget fault, found $other")
    }
  }

  private def operand(kind: String, value: Int): Operand = Operand(kind, Vector(Word(value)))
}

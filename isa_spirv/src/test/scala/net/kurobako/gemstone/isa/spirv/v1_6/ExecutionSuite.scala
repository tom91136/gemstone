package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.execution.ExecutionFault
import net.kurobako.gemstone.execution.ExecutionLimits

final class ExecutionSuite extends munit.FunSuite {
  test("integer and memory effects execute through the bounded shared substrate") {
    val module = Module(
      Header(Version.V1_6, Word(0), Word(20), Word(0)),
      Vector(
        instruction("OpConstant", 1, 2, 40),
        instruction("OpConstant", 1, 3, 2),
        instruction("OpIAdd", 1, 4, 2, 3),
        instruction("OpVariable", 5, 6, 7, 4),
        instruction("OpLoad", 1, 8, 6),
        instruction("OpReturn")
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

  test("unsupported semantic family traps explicitly") {
    val module = Module(
      Header(Version.V1_6, Word(0), Word(5), Word(0)),
      Vector(instruction("OpControlBarrier", 1, 2, 3), instruction("OpReturn")),
      Endianness.Little
    )
    val executable = Lowering.lower(module).fold(error => fail(error.message), identity)
    Interpreter.run(executable) match {
      case ExecutionResult.Fault(ExecutionFault.Unsupported("OpControlBarrier", _), _, _, 1L) => ()
      case other => fail(s"expected unsupported trap, found $other")
    }
  }

  test("instruction budget stops loops") {
    val module = Module(
      Header(Version.V1_6, Word(0), Word(5), Word(0)),
      Vector(instruction("OpLabel", 1), instruction("OpBranch", 1)),
      Endianness.Little
    )
    val executable = Lowering.lower(module).fold(error => fail(error.message), identity)
    Interpreter.run(executable, limits = ExecutionLimits(5L, 2)) match {
      case ExecutionResult.Fault(ExecutionFault.BudgetExceeded(5L), _, _, 5L) => ()
      case other => fail(s"expected budget fault, found $other")
    }
  }

  private def instruction(name: String, operands: Int*): Instruction = {
    val info = InstructionCatalog.byName.getOrElse(name, fail(s"missing $name"))
    Instruction(info.opcode, operands.toVector.map(Word.apply))
  }
}

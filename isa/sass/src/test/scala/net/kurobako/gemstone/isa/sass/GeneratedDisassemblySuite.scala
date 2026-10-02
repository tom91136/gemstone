package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*

class GeneratedDisassemblySuite extends munit.FunSuite {
  private def checkFixture(text: String, kernel: String): Unit = {
    val expected = ListingDecoder.parse(text, kernel, 89).fold(fail(_), identity).head.instructions
    val actual   = SASSOracleWords.read(text).map { (address, bytes) =>
      InstructionDecoder.decodeTyped(address, bytes, target = "SM89")
    }
    assertEquals(actual.size, expected.size, s"$kernel oracle instruction count")
    val unsupported = actual.zipWithIndex.collect { case (Left(error), index) => s"0x${index.toHexString}: $error" }
    val rendered    = actual.zip(expected).zipWithIndex.collect { case ((Right(instruction), oracle), index) =>
      index -> (instruction, oracle)
    }
    assert(rendered.nonEmpty, s"no $kernel instructions rendered; ${unsupported.take(3).mkString("; ")}")
    rendered.foreach { case (index, (instruction, oracle)) =>
      val clue = s"$kernel instruction 0x${(index * 16).toHexString}"
      assert(equivalentOpcode(oracle.operation, instruction.operation), clue)
      // Register-reuse scheduling hints do not change the operand value.
      assertEquals(
        instruction.operands.map(VMOperand.without(_, OperandSelector.reuse)),
        oracle.operands.map(VMOperand.without(_, OperandSelector.reuse)),
        clue
      )
      assertEquals(instruction.guard, oracle.guard, clue)
    }
    assert(unsupported.isEmpty)
  }

  test("generated FORMAT rendering matches the copy-add text oracle words") {
    checkFixture(CopyAddFixtures.copyAdd, "copy_add")
  }

  test("generated FORMAT rendering matches the scalar SGEMM text oracle words") {
    checkFixture(ScalarSgemmFixtures.scalarSgemm, "scalar_sgemm")
  }

  test("generated FORMAT rendering matches the SAXPY text oracle words") {
    checkFixture(SaxpyFixtures.saxpy, "saxpy")
  }

  test("text disassembly normalizes the PR marker from P2R operands") {
    val text        = """.global kernel
kernel:
/*0000*/ P2R R55, PR, RZ, 0x78;
"""
    val instruction = ListingDecoder.parse(text, "kernel", 89).fold(fail(_), identity).head.instructions.head
    assertEquals(
      instruction.operands.map(OperandPrinter.print(_).fold(fail(_), identity)),
      Vec("R55", "RZ", "0x78")
    )
    assertEquals(Printer.print(instruction), Right("P2R R55, RZ, 0x78;"))
  }

  private def equivalentOpcode(expected: Instruction, actual: Instruction): Boolean = {
    val expectedModifiers                           = expected.modifiers
    val actualModifiers                             = actual.modifiers
    def isZero(value: Operand): Boolean             = value == Operand.Register(RegisterBank.R, 255)
    def positivePowerOfTwo(value: Operand): Boolean = {
      val integer = value match {
        case Operand.Integer(number) => Some(number)
        case Operand.Bits(number, _) => Some(number)
        case _                       => None
      }
      integer.exists(number => number > 0 && (number & (number - 1)) == 0)
    }
    def isOne(value: Operand): Boolean = value match {
      case Operand.Integer(number) => number == 1
      case Operand.Bits(number, _) => number == 1
      case _                       => false
    }
    val imadMov = expected.mnemonic.raw == "IMAD" && expectedModifiers.headOption.contains(Instruction.Modifier.MOV) &&
      actual.mnemonic.raw == "IMAD" && expectedModifiers.filterNot(_ == Instruction.Modifier.MOV) == actualModifiers &&
      actual.operands.lift(1).exists(isZero(_)) && actual.operands.lift(2).exists(isZero(_))
    val imadShl = expected.mnemonic.raw == "IMAD" &&
      expectedModifiers.take(2) == Vec(Instruction.Modifier.SHL, Instruction.Modifier.U32) &&
      actual.mnemonic.raw == "IMAD" && actualModifiers == Vec(Instruction.Modifier.U32) &&
      actual.operands.lift(3).exists(isZero(_)) && actual.operands.lift(2).exists(positivePowerOfTwo(_))
    val imadAdd = expected.mnemonic.raw == "IMAD" && expectedModifiers.headOption.contains(Instruction.Modifier.IADD) &&
      actual.mnemonic.raw == "IMAD" && expectedModifiers.filterNot(_ == Instruction.Modifier.IADD) == actualModifiers &&
      actual.operands.lift(2).exists(isOne(_))
    (expected.mnemonic == actual.mnemonic && expected.modifiers == actual.modifiers) || imadMov || imadShl || imadAdd
  }
}

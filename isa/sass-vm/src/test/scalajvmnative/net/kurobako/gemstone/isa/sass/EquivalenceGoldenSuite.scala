package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*

class EquivalenceGoldenSuite extends Suite {
  private def definition(instruction: String, operands: (String, String)*)(using munit.Location): Definition = {
    val parsed    = asm.Parser.parseProgram(s"$instruction;").toOption.getOrElse(fail(s"$instruction does not parse"))
    val operation = parsed.statements.collectFirst {
      case asm.Program.Statement.Operation(operation, _, _) => operation
    }.getOrElse(fail(s"$instruction is not an instruction"))
    InstructionSemantics.describe(
      "op",
      89,
      operation,
      operands.toMap,
      Map.empty,
      Vec.from(operands.map(_._2)),
      Vec.empty
    )
      .fold(error => fail(s"$instruction: ${error.message}"), identity)
  }

  private def compare(left: String, right: String, operands: (String, String)*)(using munit.Location): Verdict = {
    assume(Bitwuzla.executable.isDefined, "no bitwuzla binary on GEMSTONE_BITWUZLA_EXE or PATH")
    Bitwuzla.equivalence(definition(left, operands*), definition(right, operands*), 10.seconds)
  }

  private val registers = Vector("R0" -> "Rd", "R1" -> "Ra", "R2" -> "Rb", "R3" -> "Rc")

  test("reordered integer additions are equal") {
    assertEquals(compare("IADD3 R0, R1, R2, R3", "IADD3 R0, R2, R1, R3", registers*), Verdict.Equal)
  }

  test("doubling by a multiply and by a self-add are equal") {
    assertEquals(
      compare("IMAD R0, R1, 0x2, RZ", "IADD3 R0, R1, R1, RZ", "R0" -> "Rd", "R1" -> "Ra"),
      Verdict.Equal
    )
  }

  test("a greater-or-equal and a greater-than against one are different") {
    compare("ISETP.GE.AND P0, PT, R1, 0x1, PT", "ISETP.GT.AND P0, PT, R1, 0x1, PT", "P0" -> "Pu", "R1" -> "Ra") match {
      case Verdict.Different(counterexample) => assert(counterexample.nonEmpty, "empty counterexample")
      case other                             => fail(s"expected Different, got $other")
    }
  }

  test("xor3 and xnor3 lookup tables are different") {
    compare(
      "LOP3.LUT R0, R1, R2, R3, 0x96, !PT",
      "LOP3.LUT R0, R1, R2, R3, 0x69, !PT",
      registers*
    ) match {
      case Verdict.Different(counterexample) => assert(counterexample.nonEmpty, "empty counterexample")
      case other                             => fail(s"expected Different, got $other")
    }
  }
}

package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*

class InstructionSemanticsSuite extends munit.FunSuite {
  private def show(instruction: String, operands: (String, String)*): Either[String, String] =
    asm.Parser.parseProgram(s"$instruction;").left.map(_.message).flatMap(_.statements.collectFirst {
      case asm.Program.Statement.Operation(operation, _, _) => operation
    }.toRight(s"$instruction is not an instruction")).flatMap(
      InstructionSemantics.describe("op", 89, _, operands.toMap, Map.empty, Vec.from(operands.map(_._2)), Vec.empty)
        .left.map(_.message)
    ).map(ScalaBackend.show(_))

  test("instructions render as definitions over their operands' names or spellings") {
    assertEquals(
      Vector(
        show("IADD3 R0, R1, R2, R3", "R0"               -> "Rd", "R1" -> "Ra", "R2" -> "Rb", "R3" -> "Rc"),
        show("LOP3.LUT R0, R1, R2, R3, 0x96, !PT", "R0" -> "Rd", "R1" -> "Ra", "R2" -> "Rb", "R3" -> "Rc"),
        show("FADD R0, R1, R2", "R0"                    -> "Rd", "R1" -> "Ra", "R2" -> "Rb"),
        show("ISETP.GE.AND P0, PT, R1, R2, PT", "P0"    -> "Pu", "R1" -> "Ra", "R2" -> "Rb"),
        show("IMAD R0, R1, R2, R3", "R0"                -> "Rd", "R1" -> "Ra", "R2" -> "Rb", "R3" -> "Rc"),
        show("IADD3 R0, R1, R2, R3")
      ),
      Vector(
        Right("def op(Ra: UInt[32], Rb: UInt[32], Rc: UInt[32]): (Rd: UInt[32]) =\n  (Rd = Ra + Rb + Rc)"),
        Right("def op(Ra: UInt[32], Rb: UInt[32], Rc: UInt[32]): (Rd: UInt[32]) =\n  (Rd = Ra ^ Rb ^ Rc)"),
        Right(
          "def op(Ra: UInt[32], Rb: UInt[32]): (Rd: UInt[32]) =\n" +
            "  (Rd = FloatArithmetic.canonical(FloatArithmetic.add(Ra.asFloat, Rb.asFloat)).bits)"
        ),
        Right(
          "def op(Ra: UInt[32], Rb: UInt[32]): (Pu: Bool) =\n  (Pu = Rb.to[SInt[32]] <= Ra.to[SInt[32]] && 1 != 0)"
        ),
        Right("def op(Ra: UInt[32], Rb: UInt[32], Rc: UInt[32]): (Rd: UInt[32]) =\n  (Rd = Ra * Rb + Rc + 0)"),
        Right("def op(R1: UInt[32], R2: UInt[32], R3: UInt[32]): (R0: UInt[32]) =\n  (R0 = R1 + R2 + R3)")
      )
    )
  }

  test("register pairs the lowering joins into one value read and write as one 64-bit value") {
    assertEquals(
      show("DADD R0, R2, R4", "R0" -> "Rd", "R2" -> "Ra", "R4" -> "Rb"),
      Right(
        "def op(Ra: UInt[64], Rb: UInt[64]): (Rd: UInt[64]) =\n  (Rd = FloatArithmetic.add(Ra.asFloat, Rb.asFloat).bits)"
      )
    )
    assertEquals(
      show("IMAD.WIDE R2, R1, R4, R2", "R2" -> "Rd", "R1" -> "Ra", "R4" -> "Rb"),
      Right(
        "def op(Rd: UInt[64], Ra: UInt[32], Rb: UInt[32]): (Rd: UInt[64]) =\n" +
          "  (Rd = (Ra.to[SInt[32]].to[SInt[64]] * Rb.to[SInt[32]].to[SInt[64]]).to[UInt[64]] + Rd + 0)"
      )
    )
    assertEquals(
      show("LDG.E R0, [R2.64]", "R0" -> "Rd", "R2" -> "Ra"),
      Right("def op(Ra: UInt[64])(using memory: Memory): (Rd: UInt[32]) =\n  (Rd = memory.loadGlobal[UInt[32]](Ra))")
    )
  }

  test("a later register of an operand follows the operand's first, before the operands after it") {
    val operation = asm.Parser.parseProgram("IADD3 R0, R4, R5, R6;").toOption.flatMap(_.statements.collectFirst {
      case asm.Program.Statement.Operation(operation, _, _) => operation
    }).getOrElse(fail("IADD3 does not parse"))
    val operands = Map("R0" -> "Rd", "R4" -> "Ra", "R5" -> "Ra+1", "R6" -> "Rb")
    assertEquals(
      InstructionSemantics.describe("op", 89, operation, operands, Map.empty, Vec("Rd", "Ra", "Rb"), Vec.empty)
        .map(_.parameters.map(_.name)),
      Right(Vec("Ra", "Ra+1", "Rb"))
    )
  }

  test("instructions without staged semantics are not described") {
    assertEquals(show("BAR.SYNC 0"), Left("lowers to BarrierSync, with no staged semantics"))
  }
}

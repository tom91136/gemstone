package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.Suite

class InstructionSemanticsSuite extends Suite {
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

  private def machine(instruction: String, operands: (String, String)*): Vec[Either[String, String]] =
    asm.Parser.parseProgram(s"$instruction;").toOption.flatMap(_.statements.collectFirst {
      case asm.Program.Statement.Operation(operation, _, _) => operation
    }).fold(Vec(Left(s"$instruction does not parse")))(
      InstructionSemantics.machine("op", 90, _, operands.toMap, Map.empty, Vec.from(operands.map(_._2)), Vec.empty)
        .map(_.left.map(_.message).map(ScalaBackend.show(_)))
    )

  test("a branch under a predicate renders as a conditional jump") {
    assertEquals(
      machine("BRA.U UP0, 0x20", "UP0" -> "UPq"),
      Vec(Right("def op(UPq: Bool)(using machine: Machine): Unit = {\n  if (UPq) machine.jump(3) else ()\n  ()\n}"))
    )
  }

  test("a convergence barrier is read and written as a 64-bit value named after its register") {
    assertEquals(
      machine("BSYNC B10"),
      Vec(Right("def op(B10: UInt[64])(using barriers: Barriers): Unit = {\n  barriers.synchronise(B10)\n  ()\n}"))
    )
  }

  test("a warp-elected bulk copy renders under its election, which its lane steps bind") {
    val rendered = machine(
      "UBLKCP.G.S [UR0], [UR4], UR8",
      "UR0" -> "URb",
      "UR1" -> "URb+1",
      "UR4" -> "URa",
      "UR5" -> "URa+1",
      "UR8" -> "URc"
    )
    assertEquals(rendered.size, 1)
    assert(
      rendered.forall(_.exists(text => text.contains("val v1 = warp.laneId() == v0") && text.contains("if (v1) {"))),
      rendered.toString
    )
  }

  test("a barrier result read renders as machine state, read by name") {
    assertEquals(
      machine("B2R.RESULT R5", "R5" -> "Rd"),
      Vec(Right("def op(barrierResult: UInt[32]): (Rd: UInt[32]) =\n  (Rd = barrierResult)"))
    )
    assertEquals(
      machine("B2R.RESULT R5, P1", "R5" -> "Rd", "P1" -> "Pd"),
      Vec(
        Right(
          "def op(barrierResult: UInt[32], barrier: Bool): (Rd: UInt[32], Pd: Bool) =\n  (\n    Rd = barrierResult,\n" +
            "    Pd = barrier\n  )"
        )
      )
    )
  }

  test("barrier register moves read and write the convergence barrier registers by name") {
    assertEquals(
      machine("BMOV.32.CLEAR R5, B1", "R5" -> "Rd"),
      Vec(
        Right(
          "def op(B1: UInt[64]): (Rd: UInt[32], B1: UInt[64]) =\n  (\n    Rd = B1.to[UInt[32]],\n    B1 = UInt[64](0)\n  )"
        )
      )
    )
    assertEquals(
      machine("BMOV.32.CLEAR B2, B1"),
      Vec(
        Right(
          "def op(B1: UInt[64]): (B2: UInt[64], B1: UInt[64]) =\n  (\n    B2 = B1.to[UInt[32]].to[UInt[64]],\n" +
            "    B1 = UInt[64](0)\n  )"
        )
      )
    )
    assertEquals(
      machine("BMOV.32 B1, B10"),
      Vec(Right("def op(B10: UInt[64]): (B1: UInt[64]) =\n  (B1 = B10.to[UInt[32]].to[UInt[64]])"))
    )
  }

  test("return address halves render as machine state, written or read by name") {
    val keepLow = "(returnAddress & -0x100000000L) | "
    assertEquals(
      machine("RPCMOV.32 Rpc.LO, R4", "R4" -> "Ra"),
      Vec(
        Right(
          "def op(Ra: UInt[32], returnAddress: UInt[64]): (returnAddress: UInt[64]) =\n  (returnAddress = " +
            keepLow + "(if (64 > 0) (Ra << 0).to[UInt[64]] else UInt[32](0).to[UInt[64]]))"
        )
      )
    )
    assertEquals(
      machine("RPCMOV.32 Rpc.LO, UR4", "UR4" -> "URa"),
      Vec(
        Right(
          "def op(URa: UInt[32], returnAddress: UInt[64]): (returnAddress: UInt[64]) =\n  (returnAddress = " +
            keepLow + "(if (64 > 0) (URa << 0).to[UInt[64]] else UInt[32](0).to[UInt[64]]))"
        )
      )
    )
    assertEquals(
      machine("RPCMOV.32 Rpc.LO, 0x10"),
      Vec(
        Right(
          "def op(returnAddress: UInt[64]): (returnAddress: UInt[64]) =\n  (returnAddress = " + keepLow +
            "(if (64 > 0) UInt[64](16) << 0 else UInt[64](0)))"
        )
      )
    )
    assertEquals(
      machine("RPCMOV.32 Rpc.HI, R4", "R4" -> "Ra"),
      Vec(
        Right(
          "def op(Ra: UInt[32], returnAddress: UInt[64]): (returnAddress: UInt[64]) =\n  (returnAddress = " +
            "returnAddress.to[UInt[32]].to[UInt[64]] | (if (64 > 32) Ra.to[UInt[64]] << 32 else UInt[64](0)))"
        )
      )
    )
    assertEquals(
      machine("RPCMOV.32 R4, Rpc.HI", "R4" -> "Rd"),
      Vec(
        Right(
          "def op(returnAddress: UInt[64]): (Rd: UInt[32]) =\n  (Rd = if (64 > 32) (returnAddress >> 32).to[UInt[32]] " +
            "else UInt[64](0).to[UInt[32]])"
        )
      )
    )
  }

  test("a lane instruction that reads no machine state has no machine definition") {
    assertEquals(machine("IADD3 R0, R1, R2, R3", "R0" -> "Rd", "R1" -> "Ra", "R2" -> "Rb", "R3" -> "Rc").size, 0)
  }

  test("an instruction without machine semantics has no machine definition") {
    assertEquals(machine("NOP").size, 0)
  }
}

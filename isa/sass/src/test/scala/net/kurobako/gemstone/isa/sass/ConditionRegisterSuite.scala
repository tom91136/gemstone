package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*

class ConditionRegisterSuite extends SyntheticSASSExecutionSuiteBase {
  private def decoded(raw: Long, control: Long, address: Int >= 0, sm: Int): DecodedInstruction =
    InstructionDecoder.decodeTyped(
      address,
      (LittleEndian.toVec(raw, 8) ++ LittleEndian.toVec(control, 3)).toArray,
      s"SM$sm"
    ).fold(fail(_), identity)

  for (sm <- Vec.nonEmpty(50, 60)) test(s"sm$sm machine instructions preserve the condition register bank") {
    // CUDA 11.8 scan-by-key saves and restores CC around unrelated address arithmetic.
    val save    = decoded(0x38e8010000f7ff1dL, 0xff1L, 0, sm)
    val restore = decoded(0x38f0010000f71d00L, 0x7f1L, 8, sm)
    assertEquals(save.operands(1), Operand.Symbol("CC"))
    assertEquals(restore.operands.head, Operand.Symbol("CC"))
    assertEquals(save.modifiers, Vec.empty[Instruction.Modifier])
    assertEquals(restore.modifiers, Vec.empty[Instruction.Modifier])
    Vec.nonEmpty(save, restore).foreach { instruction =>
      val bytes     = Assembler.encode(instruction.operation, s"SM$sm", instruction.address.raw).fold(fail(_), identity)
      val roundTrip = InstructionDecoder.decodeTyped(instruction.address, bytes, s"SM$sm").fold(fail(_), identity)
      assertEquals(roundTrip.operation, instruction.operation)
    }
  }

  for (sm <- Vec.nonEmpty(50, 60)) test(s"sm$sm normalized predicate-bank instructions assemble and decode") {
    val save    = decoded(0x38e8000000f7ff1dL, 0xff1L, 0, sm)
    val restore = decoded(0x38f0000000f71d00L, 0x7f1L, 8, sm)
    assertEquals(save.operands.size, 3)
    assertEquals(restore.operands.head, Operand.Symbol("PR"))
    Vec.nonEmpty(save, restore).foreach { instruction =>
      val bytes     = Assembler.encode(instruction.operation, s"SM$sm", instruction.address.raw).fold(fail(_), identity)
      val roundTrip = InstructionDecoder.decodeTyped(instruction.address, bytes, s"SM$sm").fold(fail(_), identity)
      assertEquals(roundTrip.operation, instruction.operation)
    }
  }

  for {
    sm     <- Vec.nonEmpty(50, 60)
    binary <- Vec.nonEmpty(false, true)
  }
    test(s"sm$sm condition save/restore retains carry, zero, sign and overflow: binary=$binary") {
      Vec.nonEmpty(("0xffffffff", 5, 1), ("0x7fffffff", 10, 0), ("1", 0, 0)).foreach { (input, flags, carry) =>
        executeSynthetic(
          Vec.nonEmpty(
            ("MOV", s"R0,$input", None),
            ("IADD", "RZ.CC,R0,1", None),
            ("P2R", "R29,CC,RZ,0xf", None),
            ("IADD", "RZ.CC,RZ,RZ", None),
            ("R2P", "CC,R29,0xf", None),
            ("IADD.X", "R3,RZ,RZ", None),
            ("P2R", "R2,CC,RZ,0xf", None),
            ("LDC.64", "R4,c[0x0][0x160]", None),
            ("STG.E.64", "[R4.64],R2", None),
            ("EXIT", "", None)
          ),
          Vec.nonEmpty(flags, carry),
          sm = sm,
          rewrite = instruction =>
            if (!binary) instruction
            else instruction.operation match {
              case _: Instruction.P2r =>
                val destination = instruction.operands.head match {
                  case Operand.Register(_, index) => index.raw
                  case other                      => fail(s"expected saved condition register, found $other")
                }
                decoded(0x38e8010000f7ff00L | destination, 0xff1L, instruction.address, sm)
              case _: Instruction.R2p => decoded(0x38f0010000f71d00L, 0x7f1L, instruction.address, sm)
              case _                  => instruction
            }
        )
      }
    }

  for (sm <- Vec.nonEmpty(50, 60)) test(s"sm$sm masked condition transfers preserve unselected bits and flags") {
    executeSynthetic(
      Vec.nonEmpty(
        ("IADD", "RZ.CC,RZ,RZ", None),
        ("MOV", "R0,4", None),
        ("R2P", "CC,R0,4", None),
        ("MOV", "R1,0xfffffff0", None),
        ("P2R", "R2,CC,R1,0xf", None),
        ("LDC.64", "R4,c[0x0][0x160]", None),
        ("STG.E", "[R4.64],R2", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(0xfffffff5),
      sm = sm
    )
  }
}

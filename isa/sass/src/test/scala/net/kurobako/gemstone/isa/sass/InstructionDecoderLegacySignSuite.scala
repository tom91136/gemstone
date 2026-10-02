package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*

class InstructionDecoderLegacySignSuite extends munit.FunSuite {
  private def assertInstruction(
      target: String,
      encoded: String,
      opcode: String,
      operands: Vec[String]
  ): Unit = {
    val instruction = InstructionDecoder.decodeTyped(0, FixtureBytes.readHex(encoded), target).fold(fail(_), identity)
    val expected    = Parser.parse(s"$opcode ${operands.mkString(", ")};").fold(fail(_), identity)
    assertEquals(instruction.mnemonic, expected.mnemonic)
    assertEquals(instruction.modifiers, expected.modifiers)
    assertEquals(instruction.operands, expected.operands)
  }

  test("SM35 resolves the canonical unnegated PSignFFMA inverse") {
    // Pinned saxpy-sm35.cubin, saxpy+0x78; ambiguous PSignFFMA output 0.
    assertInstruction(
      "SM35",
      "1a101c2a0000004c",
      "FFMA",
      Vec("R6", "R4", "c[0x0][0x150]", "R0")
    )
  }

  test("SM35 resolves the canonical unnegated PSignMAD inverse") {
    // Pinned multiply-subtract-sm35.cubin, subtract_product+0x38; PSignMAD output 0.
    assertInstruction(
      "SM35",
      "16001c29000c1052",
      "IMAD.U32.U32.HI.X",
      Vec("R5", "R0", "c[0x0][0x148]", "R3")
    )
  }

  test("SM35 filters PSign aliases with the IADD legality constraints") {
    // Pinned constant-sm35.cubin, constant_read+0x38; PSign output 0 forbids both negations.
    assertInstruction(
      "SM35",
      "0a141c2800008460",
      "IADD",
      Vec("R2.CC", "R5", "c[0x0][0x140]")
    )
  }

  test("SM50 resolves PSign IADD after undoing bundle control bits") {
    // Pinned compiler_forms-sm50.cubin, xmad_patterns+0x78. Last 3 bytes are the slot control.
    assertInstruction(
      "SM50",
      "020327050080104cf60700",
      "IADD",
      Vec("R2.CC", "R3", "c[0x0][0x148]")
    )
  }

  test("SM50 resolves PSignFFMA after undoing bundle control bits") {
    // Pinned saxpy-sm50.cubin, saxpy+0xb8. Last 3 bytes are the slot control.
    assertInstruction(
      "SM50",
      "0004470500038049f20701",
      "FFMA",
      Vec("R0", "R4", "c[0x0][0x150]", "R6")
    )
  }

  test("SM89 typed text parsing removes the PR marker and preserves the immediate operand") {
    val instruction = ListingDecoder.parse("kernel:\n/*0000*/ P2R R55, PR, RZ, 0x78;", "kernel")
      .fold(fail(_), identity).head.instructions.head
    assertEquals(instruction.mnemonic.raw, "P2R")
    assertEquals(instruction.operands.map(OperandPrinter.print(_).fold(fail(_), identity)), Vec("R55", "RZ", "0x78"))
  }
  test("SM35 omits all encodings of the default H0 half selector") {
    assertInstruction("SM35", "1ed41c03000001e6", "I2I.S16.S16", Vec("R7", "-R6"))
  }

  test("SM80 retains the non-default LDSM two-register count qualifier") {
    assertInstruction("SM80", "3b780c0c000000000001000000240e00", "LDSM.16.M88.2", Vec("R12", "[R12]"))
  }

}

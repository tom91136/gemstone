package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.either.*

/** Constant banks a uniform register selects, `cx[URa][offset]`: nvdisasm 13.3.73 (`nvdisasm -b SM90` on the raw words)
  * prints each word as its text, which assembles back to it.
  */
class IndexedConstantSuite extends Suite {
  private val sm90 = Target.of(90).value

  private def bytes(hex: String): Array[Byte] = hex.grouped(2).map(Integer.parseInt(_, 16).toByte).toArray

  for (
    (text, word) <- Vec(
      "LDC R0, cx[UR4][R8+0x10];"   -> "82750004000400000808000800c00f00",
      "LDC R0, cx[UR4][0x10];"      -> "8275000400040000ff08000800c00f00",
      "DADD R0, R4, cx[UR8][0x10];" -> "29760004080400000000000800c00f00",
      "ULDC UR0, cx[UR4][0x10];"    -> "b97a0004000400000008000800c00f00"
    )
  ) test(s"$text decodes, prints and assembles") {
    val decoded = InstructionDecoder.decode(0, bytes(word), sm90).value
    assertEquals(Printer.print(decoded.operation), Right(text))
    assertEquals(Assembler.assemble(text, sm90).map(_.toSeq), Right(bytes(word).toSeq))
    assertEquals(Parser.parse(text).flatMap(Printer.print(_)), Right(text))
  }
}

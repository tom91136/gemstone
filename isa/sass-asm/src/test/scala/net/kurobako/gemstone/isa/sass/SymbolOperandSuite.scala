package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class SymbolOperandSuite extends Suite {
  private val sm90 = Target.of(90).value

  private def word(lo: Long, hi: Long): Array[Byte] = (0 until 8).map(i => (lo >>> (8 * i)).toByte).toArray ++
    (0 until 8).map(i => (hi >>> (8 * i)).toByte).toArray

  for (
    (lo, hi, text) <- Vec(
      (0x0000000380009352L, 0x001fcc0000000000L, "@!P1 RPCMOV.32 Rpc.HI, R3;"),
      (0x0000000200009352L, 0x000fcc0000000000L, "@!P1 RPCMOV.32 Rpc.LO, R2;"),
      (0x0000000080039353L, 0x000fc80000000000L, "@!P1 RPCMOV.32 R3, Rpc.HI;"),
      (0x0000000000029353L, 0x000fe20000000000L, "@!P1 RPCMOV.32 R2, Rpc.LO;")
    )
  ) test(s"$text decodes and assembles back to the same instruction") {
    val bytes = word(lo, hi)
    assertEquals(Printer.print(InstructionDecoder.decode(0, bytes, sm90).value.operation).value, text)
    val reassembled = Assembler.assemble(text, sm90).value
    assertEquals(Printer.print(InstructionDecoder.decode(0, reassembled, sm90).value.operation).value, text)
  }
}

package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class InstructionDecoderCacheSuite extends Suite {
  private val sm90 = Target.of(90).value

  private val word = "29760004080400000000000800c00f00".grouped(2).map(Integer.parseInt(_, 16).toByte).toArray

  test("a decoded word is unchanged after distinct words flush the cache") {
    val before = Printer.print(InstructionDecoder.decode(0, word, sm90).value.operation)
    for (i <- 0 until 8192) {
      val other = word.clone()
      for (j <- 0 until 4) other(j) = (i >> (8 * j)).toByte
      val _ = InstructionDecoder.decode(0, other, sm90)
    }
    assertEquals(Printer.print(InstructionDecoder.decode(0, word, sm90).value.operation), before)
  }
}

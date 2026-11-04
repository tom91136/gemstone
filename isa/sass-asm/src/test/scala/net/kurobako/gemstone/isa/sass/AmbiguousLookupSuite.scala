package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

/** Maxwell texture forms pack (dimensionality, level, offset, multisample) into a table whose padding rows share the
  * output of the 1D form, so only legality picks the real combination: ptxas 11.8 words printed by nvdisasm 11.8.
  */
class AmbiguousLookupSuite extends Suite {
  private def bytes(hex: String): Array[Byte] = hex.grouped(2).map(Integer.parseInt(_, 16).toByte).toArray.reverse

  for (
    (text, expected, group) <- Seq(
      (
        "TLDS.LZ.P RZ, R0, R4, 0x50, 1D, R",
        (
          "TLDS",
          "LZ.P",
          "Register(R,255), Register(R,0), Register(R,4), Register(R,255), Bits(80,0), Symbol(1D), Symbol(R)"
        ),
        Seq("001fd840fe2407bf", "da00050ffff70400", "3829000001f70403", "4c10800005270402")
      )
    )
  ) test(s"$text decodes through legality of an ambiguous table inverse") {
    val (address, word) = codeWords(group.flatMap(bytes(_)).toArray, 50).value.head
    val decoded         = InstructionDecoder.decode(address, word, target(50)).value
    assertEquals(
      (decoded.mnemonic.raw, decoded.modifiers.map(_.name).mkString("."), decoded.operands.mkString(", ")),
      expected
    )
  }
}

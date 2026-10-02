package net.kurobako.gemstone.isa.sass.fixtures

/** Small byte helpers for independently pinned SASS and cubin test fixtures. */
object FixtureBytes {
  private val hexDigits = "0123456789abcdef"

  def readHex(hex: String): Array[Byte] = {
    val digits = hex.filterNot(_.isWhitespace)
    require(digits.length % 2 == 0, "hex fixture must contain complete bytes")
    Array.tabulate[Byte](digits.length / 2) { index =>
      Integer.parseInt(digits.substring(index * 2, index * 2 + 2), 16).toByte
    }
  }

  def writeHex(bytes: Array[Byte]): String =
    if (bytes.isEmpty) ""
    else
      bytes.grouped(32).map { group =>
        group.map { byte =>
          val value = byte & 0xff
          s"${hexDigits.charAt(value >>> 4)}${hexDigits.charAt(value & 0xf)}"
        }.mkString
      }.mkString("\n") + "\n"

}

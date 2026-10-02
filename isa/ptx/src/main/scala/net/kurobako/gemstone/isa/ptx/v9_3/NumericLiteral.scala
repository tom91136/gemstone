package net.kurobako.gemstone.isa.ptx.v9_3

/** Shared lexical operations for PTX integer and floating bit-pattern literals. */
private object NumericLiteral {
  def stripIntegerSuffix(raw: String): String =
    raw.reverse.dropWhile(character => "uUlL".contains(character)).reverse

  def integer(raw: String): BigInt =
    if (hasPrefix(raw, 'x')) BigInt(raw.drop(2), 16)
    else if (hasPrefix(raw, 'b')) BigInt(raw.drop(2), 2)
    else BigInt(raw)

  def hexadecimalPayload(raw: String, marker: Char): Option[String] =
    Option.when(hasPrefix(raw, marker))(raw.drop(2))

  private def hasPrefix(raw: String, marker: Char): Boolean =
    raw.length >= 2 && raw.charAt(0) == '0' && raw.charAt(1).toLower == marker
}

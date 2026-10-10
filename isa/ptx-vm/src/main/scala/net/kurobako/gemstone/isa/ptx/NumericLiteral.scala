package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.string.*

/** PTX integer and floating literals as raw bits. */
private object NumericLiteral {

  /** Decimal, 0x hexadecimal, 0b binary or leading-zero octal integer with an optional U/L suffix. */
  def integer(raw: String): Option[BigInt] =
    parseBigIntLiteral(raw.reverse.dropWhile("uUlL".contains(_)).reverse, binary = true, octal = true)

  /** Bits of a 0f (single) or 0d (double) hexadecimal floating literal. */
  def floatingBits(raw: String, marker: Char): Option[BigInt] =
    Option.when(raw.length > 2 && raw.charAt(0) == '0' && raw.charAt(1).toLower == marker)(raw.drop(2))
      .flatMap(digits => parseBigIntLiteral(s"0x$digits"))

  /** An instruction immediate: an integer or floating bit pattern within 64 bits, signed or unsigned. */
  def immediate(operand: Operand): Option[Long] = operand match {
    case Operand.Negate(value)   => immediate(value).map(-_)
    case Operand.Immediate(text) =>
      val raw      = text.raw
      val signless = raw.dropWhile(character => character == '+' || character == '-')
      floatingBits(signless, 'f').orElse(floatingBits(signless, 'd')).orElse(integer(signless))
        .map(number => if (raw.startsWith("-")) -number else number)
        .filter(number => number.isValidLong || (number >= 0 && number < (BigInt(1) << 64)))
        .map(_.toLong)
    case _ => None
  }

  /** A data initialiser of `kind` ('f' for floating, otherwise integral) and `width` bits. */
  def initialiser(raw: String, kind: Char, width: Int): Option[Long] =
    if (kind == 'f' && width == 32)
      floatingBits(raw, 'f').map(_.toLong).orElse(raw.toFloatOption.map(IEEE754.floatToBinary32(_).toLong))
    else if (kind == 'f' && width == 64)
      floatingBits(raw, 'd').map(_.toLong).orElse(raw.toDoubleOption.map(IEEE754.doubleToBinary64(_)))
    else integer(raw).map(_.toLong)
}

package net.kurobako.gemstone.isa.ptx

object Literal {
  private val Hex          = "0[xX]([0-9a-fA-F]+)([uU]?)".r
  private val Binary       = "0[bB]([01]+)([uU]?)".r
  private val Octal        = "0([0-7]*)([uU]?)".r
  private val Dec          = "([1-9][0-9]*)([uU]?)".r
  private val Binary32Text = "0[fF]([0-9a-fA-F]{8})".r
  private val Binary64Text = "0[dD]([0-9a-fA-F]{16})".r
  private val Float        = "([0-9]+\\.[0-9]*|\\.[0-9]+|[0-9]+)([eE][+-]?[0-9]+)?".r
  private val Limit        = BigInt(1) << 63

  private def integer(value: BigInt, suffix: String): Literal.Integer =
    Literal.Integer(value, suffix.nonEmpty || value >= Limit)

  /** The constant `raw` spells, or none when it follows none of the document's constant grammars. */
  def parse(raw: String): Option[Literal] = raw match {
    case Hex(digits, suffix)    => Some(integer(BigInt(digits, 16), suffix))
    case Binary(digits, suffix) => Some(integer(BigInt(digits, 2), suffix))
    case Dec(digits, suffix)    => Some(integer(BigInt(digits, 10), suffix))
    case Octal(digits, suffix)  => Some(integer(if (digits.isEmpty) BigInt(0) else BigInt(digits, 8), suffix))
    case Binary32Text(digits)   => Some(Literal.Binary32(java.lang.Long.parseLong(digits, 16)))
    case Binary64Text(digits)   => Some(Literal.Binary64(BigInt(digits, 16)))
    case Float(mantissa, exponent) if mantissa.contains('.') || Option(exponent).nonEmpty =>
      Some(Literal.Decimal(raw))
    case _ => None
  }
}

/** The constants PTX writes: integers of any radix, hexadecimal single and double bit patterns, and decimal floats. */
enum Literal derives CanEqual {

  /** An integer constant, of type `.u64` when written with a `U` suffix or beyond `.s64`, else `.s64`. */
  case Integer(value: BigInt, unsigned: Boolean)

  /** `0f` followed by the eight hexadecimal digits of an IEEE 754 single. */
  case Binary32(bits: Long)

  /** `0d` followed by the sixteen hexadecimal digits of an IEEE 754 double. */
  case Binary64(bits: BigInt)

  /** A floating-point constant with a decimal point or an exponent. */
  case Decimal(text: String)

  def negated: Literal = this match {
    case Literal.Integer(value, unsigned) => Literal.Integer(-value, unsigned)
    case other                            => other
  }
}

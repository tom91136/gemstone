package net.kurobako.gemstone.isa.sass

/** A stretch of operand values that share consecutive field codes, as the generated SASS domains list them. */
enum DomainRun derives CanEqual {
  case Registers(bank: String, first: Int, last: Int, code: Int)
  case Numbered(prefix: String, first: Int, last: Int, code: Int)
  case Named(name: String, code: Int)

  /** A numeric field of `bits` bits, two's complement when `signed`. */
  case Immediate(bits: Int, signed: Boolean)

  def spelling: String = this match {
    case Registers(bank, first, last, _)  => range(bank, first, last)
    case Numbered(prefix, first, last, _) => range(prefix, first, last)
    case Named(name, _)                   => name
    case Immediate(bits, signed)          => s"${if (signed) "s" else "u"}$bits"
  }

  private def range(prefix: String, first: Int, last: Int): String =
    if (first == last) s"$prefix$first" else s"$prefix$first..$prefix$last"
}

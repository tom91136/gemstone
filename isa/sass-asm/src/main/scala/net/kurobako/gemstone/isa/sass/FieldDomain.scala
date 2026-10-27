package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*

object FieldDomain {
  private val Shown = 6
}

/** The values one operand field of one form accepts, and the codes the form stores them under. */
class FieldDomain(val label: String, val runs: Vec[DomainRun]) {
  def immediate: Option[DomainRun.Immediate] = runs.collectFirst { case run: DomainRun.Immediate => run }

  /** Whether `code` selects a value; an immediate accepts every code its field holds. */
  def contains(code: BigInt): Boolean = runs.exists {
    case DomainRun.Registers(_, first, last, base) => code >= base && code <= base + last - first
    case DomainRun.Numbered(_, first, last, base)  => code >= base && code <= base + last - first
    case DomainRun.Named(_, found)                 => code == found
    case DomainRun.Immediate(bits, _)              => code >= 0 && code.bitLength <= bits
  }

  def bank: Option[String] = runs.collectFirst { case DomainRun.Registers(bank = bank) => bank }

  /** The register bank and index `code` selects. */
  def register(code: BigInt): Option[(String, Int)] = runs.collectFirst(Function.unlift {
    case DomainRun.Registers(bank, first, last, base) if code >= base && code <= base + last - first =>
      Some(bank -> (first + (code - base).toInt))
    case _ => None
  })

  /** Every named, numbered and register value with its code. */
  def values: Vec[(String, BigInt)] = runs.flatMap {
    case DomainRun.Registers(bank, first, last, base)  => numbered(bank, first, last, base)
    case DomainRun.Numbered(prefix, first, last, base) => numbered(prefix, first, last, base)
    case DomainRun.Named(name, code)                   => Vec(name -> BigInt(code))
    case DomainRun.Immediate(_, _)                     => Vec.empty[(String, BigInt)]
  }

  private def numbered(prefix: String, first: Int, last: Int, base: Int): Vec[(String, BigInt)] =
    Vec.tabulate(last - first + 1)(offset => s"$prefix${first + offset}" -> BigInt(base + offset))

  /** A compact reading of the domain: long vocabularies keep their first values. */
  def describe: String = {
    val parts = runs.map(_.spelling)
    if (parts.size > FieldDomain.Shown) parts.take(FieldDomain.Shown).mkString("", ", ", s", ... (${parts.size})")
    else parts.mkString(", ")
  }
}

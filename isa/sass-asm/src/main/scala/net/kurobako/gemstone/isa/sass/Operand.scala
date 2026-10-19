package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.SpecialRegister as SpecialRegisterName

object Operand {

  /** `PT`, `UPT`, `P0`-`P6` and `UP0`-`UP6`, optionally negated with `!`. */
  def predicate(name: String): Option[Predicate] = {
    val negated = name.startsWith("!")
    name.stripPrefix("!") match {
      case "PT"                             => Some(Predicate(None, uniform = false, negated))
      case "UPT"                            => Some(Predicate(None, uniform = true, negated))
      case s"UP$index" if index.length == 1 => index.toIntOption.flatMap(indexed(_, PredicateFile.Uniform, negated))
      case s"P$index" if index.length == 1  => index.toIntOption.flatMap(indexed(_, PredicateFile.Plain, negated))
      case _                                => None
    }
  }

  private enum PredicateFile derives CanEqual { case Plain, Uniform }

  private def indexed(index: Int, file: PredicateFile, negated: Boolean): Option[Predicate] =
    index.refine[Int in 0 ~ 6].toOption.map(value => Predicate(Some(value), file == PredicateFile.Uniform, negated))
}

/** Values bound by a machine-description FORMAT. Bounds, defaults and legal combinations belong to the target's form.
  * Floating point immediates use Bits so their payloads survive decoding unchanged.
  */
enum Operand derives CanEqual {
  case Register(bank: RegisterBank, index: Int in 0 ~ 255)
  case Integer(value: BigInt)
  case Bits(value: BigInt, width: Int >= 0)
  case Symbol(value: String)
  case Group(values: Vec[Operand])
  case Predicate(index: Option[Int in 0 ~ 6], uniform: Boolean, negated: Boolean)
  case SpecialRegister(name: SpecialRegisterName)
  case FloatingBits(format: FloatEncoding, width: Int in (16, 32, 64), bits: BigInt)
  case Floating(value: BigDecimal, spelling: String, negativeZero: Boolean)
  case FloatingSymbol(name: FloatingConstant)
  case ConstantBank(bank: BigInt, offset: Operand)
  case Address(
      base: Option[Register],
      index: Option[Register],
      displacement: BigInt,
      scale: Int in (1, 2, 4, 8, 16),
      mode: Option[AddressMode],
      descriptor: Option[Operand]
  )
  case Unary(value: Operand, negative: Boolean, not: Boolean, absolute: Boolean)
  case Qualified(value: Operand, selectors: Vec[OperandSelector])
}

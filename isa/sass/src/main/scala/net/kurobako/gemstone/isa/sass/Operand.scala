package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.SpecialRegister as SpecialRegisterName

/** Values bound by a machine-description FORMAT. Bounds, defaults and legal combinations belong to the target's form.
  * Floating point immediates use Bits so their payloads survive decoding unchanged.
  */
enum Operand {
  case Register(bank: RegisterBank, index: Int where Between[0, 255])
  case Integer(value: BigInt)
  case Bits(value: BigInt, width: Int >= 0)
  case Symbol(value: String)
  case Group(values: Vec[Operand])
  case Decorated(value: Operand, attributes: Map[OperandDecoration, Operand])
  case Predicate(index: Option[Int where Between[0, 6]], uniform: Boolean, negated: Boolean)
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

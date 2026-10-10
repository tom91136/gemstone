package net.kurobako.gemstone.isa.amdgpu.asm

import net.kurobako.gemstone.collection.*

enum Operand {
  case Register(file: RegisterFile, first: Int, last: Int)
  case SpecialRegister(name: String)
  case IntegerLiteral(value: BigInt, radix: Int)
  case DecimalLiteral(value: String)
  case Identifier(name: String)
  case Expression(text: String)
  case Negated(value: Operand)
  case Absolute(value: Operand)
  case SignExtended(value: Operand)
  case RegisterList(values: Vec[Operand])
  case Half(value: Operand, high: Boolean)
}

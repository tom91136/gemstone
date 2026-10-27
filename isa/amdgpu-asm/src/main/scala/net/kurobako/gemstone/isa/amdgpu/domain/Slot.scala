package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.isa.amdgpu.asm.*

/** A value of an operand domain in the form every domain shares; the generated enums convert to and from it. */
enum Slot derives CanEqual {
  case Register(file: RegisterFile, index: Int)
  case Named(name: String)
  case Numbered(prefix: String, index: Int)
  case Integer(value: Int)
  case Floating(value: Double)
  case Literal(bytes: Int)
  case Immediate(value: BigInt)
}

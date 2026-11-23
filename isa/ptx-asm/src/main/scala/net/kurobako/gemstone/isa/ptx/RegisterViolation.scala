package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*

/** The type checking rule a register operand breaks. */
enum RegisterRule(val description: String) derives CanEqual {
  case Exact       extends RegisterRule("the type checking rule for")
  case Source      extends RegisterRule("the relaxed source rule for")
  case Destination extends RegisterRule("the relaxed destination rule for")
}

/** Why a register operand does not fit the instruction type at its position. */
enum RegisterViolation derives CanEqual {
  case Incompatible(
      register: String,
      declared: String,
      instructionType: String,
      rule: RegisterRule,
      allowed: Vec[String]
  )

  def message: String = this match {
    case Incompatible(register, declared, instructionType, rule, allowed) =>
      val accepts = if (allowed.isEmpty) "no register declaration" else allowed.map("." + _).mkString(", ")
      s"$register is declared .$declared, which ${rule.description} .$instructionType does not accept (accepts $accepts)"
  }
}

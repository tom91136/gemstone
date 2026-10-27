package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** Why the modifiers of an instruction fit none of the slots its syntax gives them. */
enum ModifierViolation derives CanEqual {

  /** The modifier belongs to no slot of the instruction. */
  case Unknown(modifier: Modifier)

  /** The modifier belongs to a slot of the instruction, but not to any slot that may follow here. */
  case Misplaced(modifier: Modifier, expected: Vec[ModifierDomain])

  /** The modifiers end before this slot is filled. */
  case Missing(domain: ModifierDomain)

  def message: String = this match {
    case Unknown(modifier)             => s".${modifier.name.raw} is not a modifier of this instruction"
    case Misplaced(modifier, expected) =>
      s".${modifier.name.raw} cannot follow here; expected ${expected.map(_.spelling).distinct.mkString(" or ")}"
    case Missing(domain) => s"missing the ${domain.name} modifier, one of ${domain.spelling}"
  }
}

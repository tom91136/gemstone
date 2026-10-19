package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*

/** Why an instruction renders no definition. */
enum Unrendered derives CanEqual {

  /** The VM rejects the instruction, so a kernel that uses it does not load. */
  case Rejected(reason: String)

  /** The instruction lowers to these VM operations, which run as machine mechanics with no staged semantics; a lone
    * `NoOp` means the VM accepts the instruction and does nothing.
    */
  case Unstaged(operations: Vec[String])

  /** The instance is not well formed: it does not parse, or names operands the instruction cannot take. */
  case Malformed(reason: String)

  /** The instruction lowers and stages, but its steps do not form a definition. */
  case Undefined(reason: String)

  def message: String = this match {
    case Rejected(reason)     => reason
    case Malformed(reason)    => reason
    case Unstaged(operations) => s"lowers to ${operations.mkString(", ")}, with no staged semantics"
    case Undefined(reason)    => reason
  }
}

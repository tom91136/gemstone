package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*

/** How an instruction type constrains an operand: the table cell of the relaxed type-checking rules. */
enum Widening derives CanEqual {
  case Exact, Chop, Extend, Invalid
}

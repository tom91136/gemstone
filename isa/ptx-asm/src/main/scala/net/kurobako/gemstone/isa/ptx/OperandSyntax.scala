package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*

/** An operand as the syntax block names it. */
case class OperandSyntax(name: String, constraint: Metadata.OperandConstraint, optional: Boolean)

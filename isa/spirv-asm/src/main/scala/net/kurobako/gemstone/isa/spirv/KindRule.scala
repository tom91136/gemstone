package net.kurobako.gemstone.isa.spirv

/** A constraint on every operand of `kind`, in whichever instruction it appears. */
case class KindRule(kind: OperandKind, constraint: OperandConstraint, source: String)

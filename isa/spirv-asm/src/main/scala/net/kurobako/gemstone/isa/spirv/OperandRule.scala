package net.kurobako.gemstone.isa.spirv

/** A constraint on the operand at grammar position `index` of `opcode`, named `name` by the grammar. A repeated rule
  * covers every operand from `index` on.
  */
case class OperandRule(
    opcode: String,
    index: Int,
    name: String,
    quantifier: OperandQuantifier,
    constraint: OperandConstraint,
    source: String
)

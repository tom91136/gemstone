package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.constraint.*

case class OperandShape(
    kind: OperandKind,
    name: NonEmpty[String],
    quantifier: OperandQuantifier
)

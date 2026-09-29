package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.constraint.*

case class OperandShape(
    kind: NonEmpty[String],
    name: NonEmpty[String],
    quantifier: OperandQuantifier
)

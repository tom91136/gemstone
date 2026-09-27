package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.NonEmptyString

case class OperandShape(
    kind: NonEmptyString,
    name: NonEmptyString,
    quantifier: OperandQuantifier
)

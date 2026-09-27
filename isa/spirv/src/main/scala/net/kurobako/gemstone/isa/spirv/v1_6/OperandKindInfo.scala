package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.Vec

case class OperandKindInfo(
    kind: NonEmptyString,
    category: NonEmptyString,
    enumerants: Vec[OperandEnumerant]
)

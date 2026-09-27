package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.Vec

case class Metadata(
    instructionClass: NonEmptyString,
    since: Option[NonEmptyString],
    capabilities: Vec[NonEmptyString],
    operands: Vec[OperandShape]
)

package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.NonEmptyString

case class OperandInfo(
    field: Option[String],
    kind: NonEmptyString,
    implicitOperand: Boolean
)

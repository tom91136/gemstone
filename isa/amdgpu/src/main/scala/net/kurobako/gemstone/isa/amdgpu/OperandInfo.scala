package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*

case class OperandInfo(
    field: Option[String],
    kind: NonEmpty[String],
    implicitOperand: Boolean
)

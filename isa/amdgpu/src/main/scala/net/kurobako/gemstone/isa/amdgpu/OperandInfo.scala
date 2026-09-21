package net.kurobako.gemstone.isa.amdgpu

final case class OperandInfo(
    field: Option[String],
    kind: OperandKind,
    implicitOperand: Boolean
)

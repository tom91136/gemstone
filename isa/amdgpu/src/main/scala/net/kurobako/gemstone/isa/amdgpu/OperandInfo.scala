package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*

case class OperandInfo(
    field: Option[EncodingField],
    kind: OperandKind,
    implicitOperand: Boolean,
    bits: Int >= 0,
    dataFormat: Option[DataFormat] = None,
    output: Boolean = false
)

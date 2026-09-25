package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec

final case class Encoding(
    family: Family,
    target: MetadataTarget,
    format: InstructionFormat,
    opcode: Int,
    operands: Vec[OperandInfo],
    source: MetadataSource
)

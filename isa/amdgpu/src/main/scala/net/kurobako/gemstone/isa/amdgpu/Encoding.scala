package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.Vec

case class Encoding(
    family: Family,
    target: NonEmptyString,
    format: NonEmptyString,
    opcode: Int,
    operands: Vec[OperandInfo],
    source: MetadataSource
)

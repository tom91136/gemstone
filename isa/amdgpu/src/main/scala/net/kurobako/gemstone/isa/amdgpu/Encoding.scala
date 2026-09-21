package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class Encoding(
    family: Family,
    target: MetadataTarget,
    format: InstructionFormat,
    opcode: Int :| Positive0,
    operands: Vec[OperandInfo],
    functionalGroup: Option[FunctionalGroup],
    source: MetadataSource
)

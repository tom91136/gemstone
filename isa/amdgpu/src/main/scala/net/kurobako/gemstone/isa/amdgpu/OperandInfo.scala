package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.NoNul

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class OperandInfo(
    field: Option[String :| (MinLength[1] & NoNul)],
    kind: OperandKind,
    sizeBits: Int :| Positive,
    input: Boolean,
    output: Boolean,
    implicitOperand: Boolean
)

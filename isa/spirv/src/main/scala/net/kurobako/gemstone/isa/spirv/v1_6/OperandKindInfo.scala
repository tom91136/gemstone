package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.Vec
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class OperandKindInfo(
    kind: SPIRVOperandKind,
    category: String :| (MinLength[1] & NoNul),
    enumerants: Vec[OperandEnumerant]
)

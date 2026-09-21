package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.NoNul
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class OperandShape(
    kind: SPIRVOperandKind,
    name: String :| (MinLength[1] & NoNul),
    quantifier: OperandQuantifier
)

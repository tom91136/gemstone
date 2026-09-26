package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.NonEmptyString
import net.kurobako.gemstone.Vec

final case class OperandKindInfo(
    kind: SPIRVOperandKind,
    category: NonEmptyString,
    enumerants: Vec[OperandEnumerant]
)

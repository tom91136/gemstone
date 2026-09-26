package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.NonEmptyString

final case class OperandShape(
    kind: SPIRVOperandKind,
    name: NonEmptyString,
    quantifier: OperandQuantifier
)

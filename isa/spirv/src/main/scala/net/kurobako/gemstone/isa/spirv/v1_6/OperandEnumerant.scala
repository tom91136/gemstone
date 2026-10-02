package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

case class OperandEnumerant(
    name: NonEmpty[String],
    value: Long,
    parameterKinds: Vec[OperandKind],
    aliases: Vec[NonEmpty[String]]
)

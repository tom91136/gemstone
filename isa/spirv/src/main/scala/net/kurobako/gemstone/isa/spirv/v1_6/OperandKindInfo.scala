package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

case class OperandKindInfo(
    kind: NonEmpty[String],
    category: NonEmpty[String],
    enumerants: Vec[OperandEnumerant]
)

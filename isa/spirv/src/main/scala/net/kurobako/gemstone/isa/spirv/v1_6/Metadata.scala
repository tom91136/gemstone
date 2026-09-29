package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

case class Metadata(
    instructionClass: NonEmpty[String],
    since: Option[NonEmpty[String]],
    capabilities: Vec[NonEmpty[String]],
    operands: Vec[OperandShape]
)

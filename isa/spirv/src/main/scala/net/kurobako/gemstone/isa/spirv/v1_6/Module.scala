package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.collection.*

case class Module(header: Header, instructions: Vec[Instruction], endianness: Endianness)

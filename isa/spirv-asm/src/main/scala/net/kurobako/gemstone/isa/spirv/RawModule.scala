package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*

case class RawModule(header: Header, instructions: Vec[RawInstruction], endianness: Endianness)

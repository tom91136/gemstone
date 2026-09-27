package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec

case class Module(header: Header, instructions: Vec[Instruction], endianness: Endianness)
    extends net.kurobako.gemstone.isa.Program

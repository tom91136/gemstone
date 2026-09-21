package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec

final case class RawModule(header: Header, instructions: Vec[RawInstruction], endianness: Endianness)

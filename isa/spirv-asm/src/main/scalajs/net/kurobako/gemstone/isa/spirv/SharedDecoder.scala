package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*

/** Scala.js has no weak references to pool instructions with, so each module keeps its own instruction graph. */
class SharedDecoder {
  def decode(bytes: Vec[Byte]): Either[ISA.DecodeError, Module] = ISA.decode(bytes)
}

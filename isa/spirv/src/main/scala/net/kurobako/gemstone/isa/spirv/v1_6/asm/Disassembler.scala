package net.kurobako.gemstone.isa.spirv.v1_6.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6.*

/** Decodes SPIR-V binary modules into the validated module representation. */
object Disassembler extends Decoder[Vec[Byte], Module, ISA.DecodeError] {
  override def decode(bytes: Vec[Byte]): Either[ISA.DecodeError, Module] = ISA.decode(bytes)
}

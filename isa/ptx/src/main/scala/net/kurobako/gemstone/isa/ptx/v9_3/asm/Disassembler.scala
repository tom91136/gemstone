package net.kurobako.gemstone.isa.ptx.v9_3.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.ptx.v9_3.*

/** Decodes canonical PTX text bytes; PTX bytes are source text, never a cubin image. */
object Disassembler extends Decoder[Vec[Byte], Program, ISA.DecodeError] {
  override def decode(bytes: Vec[Byte]): Either[ISA.DecodeError, Program] = ISA.decode(bytes)
}

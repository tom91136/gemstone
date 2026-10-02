package net.kurobako.gemstone.isa.ptx.v9_3.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.ptx.v9_3.*
import net.kurobako.gemstone.string.*

/** Encodes a PTX syntax tree as its canonical UTF-8 text representation. */
object Assembler extends Encoder[Program, Vec[Byte], UTF8.Error] {
  override def encode(program: Program): Either[UTF8.Error, Vec[Byte]] = UTF8.encode(Printer.render(program))
}

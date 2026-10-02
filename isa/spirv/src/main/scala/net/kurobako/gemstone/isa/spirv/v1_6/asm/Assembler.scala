package net.kurobako.gemstone.isa.spirv.v1_6.asm

import cats.syntax.either.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6.*
import net.kurobako.gemstone.isa.spirv.v1_6.Binary as SPIRVBinary

object Assembler extends Encoder[String, Vec[Byte], Assembler.Error] {
  enum Error {
    case Parse(error: Parser.Error)
    case Binary(error: SPIRVBinary.BinaryError)
  }
  override def encode(source: String): Either[Error, Vec[Byte]] =
    Parser.parse(source).leftMap(Error.Parse(_)).flatMap(module => ISA.encode(module).leftMap(Error.Binary(_)))
}

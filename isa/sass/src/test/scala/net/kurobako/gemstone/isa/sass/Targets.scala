package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.either.*

object Targets {
  def target(sm: Int)(using munit.Location): Target = Target.parse(sm.toString).value

  def decoder(sm: Int)(using munit.Location): NVDisasm.Database = target(sm).database

  /** Decodes one instruction word of `sm` at `address`. */
  def disassemble(bytes: Array[Byte], sm: Int, address: Int >= 0 = 0)(using
      munit.Location
  ): Either[String, DecodedInstruction] =
    InstructionDecoder.decode(address, bytes, target(sm))

  def codeWords(code: Array[Byte], sm: Int)(using munit.Location): Either[String, Vec[(Int >= 0, Array[Byte])]] =
    CodeWords.split(code, target(sm).generation).map(CodeWords.words)
}

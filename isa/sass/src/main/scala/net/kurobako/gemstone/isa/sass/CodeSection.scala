package net.kurobako.gemstone.isa.sass

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*

object CodeSection {
  def decode(bytes: Array[Byte], sm: Int): Either[String, CodeSection] = for {
    layout       <- SASSCodeWords.split(bytes, sm)
    instructions <- SASSCodeWords.words(layout).traverse { (address, encoded) =>
      InstructionDecoder.decodeTyped(address, encoded, s"SM$sm")
        .left.map(error => s"at 0x${address.toHexString}: $error")
    }
  } yield CodeSection(layout, instructions)

}

/** A structured view of a machine-code section with its physical bundle layout retained. */
case class CodeSection(layout: SASSCodeWords.CodeSection, instructions: Vec[DecodedInstruction])

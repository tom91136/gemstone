package net.kurobako.gemstone.isa.sass

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*

object CodeSection {
  def decode(bytes: Array[Byte], target: Target): Either[String, CodeSection] = for {
    layout       <- CodeWords.split(bytes, target.generation)
    instructions <- CodeWords.words(layout).traverse { (address, encoded) =>
      InstructionDecoder.decode(address, encoded, target).left.map(error => s"at 0x${address.raw.toHexString}: $error")
    }
  } yield CodeSection(layout, instructions)
}

/** A structured view of a machine-code section with its physical bundle layout retained. */
case class CodeSection(layout: CodeWords.Layout, instructions: Vec[DecodedInstruction])
